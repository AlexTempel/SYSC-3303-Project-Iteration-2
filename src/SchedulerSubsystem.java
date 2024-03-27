import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class SchedulerSubsystem implements Runnable {

    /*
    Check for elevator updates
    Update Elevator's info
    Check for requests
    If it is a complete request, update elevator, add it to complete request list
    If it is a new request put it in pending list and outstanding list
    Try to send pending requests to elevators
     */

    private final DatagramSocket requestSocket;
    private final DatagramSocket infoSocket;
    private final ArrayList<ElevatorSchedulerData> elevatorList;
    private final ArrayList<RequestWrapper> completeRequestList;
    private final ArrayList<RequestWrapper> outstandingRequestList;
    private final ArrayList<RequestWrapper> pendingRequestList;

    SchedulerSubsystem(int requestSocketPort, int infoSocketPort, ArrayList<ElevatorSchedulerData> elevators) throws SocketException {
        requestSocket = new DatagramSocket(requestSocketPort);
        requestSocket.setSoTimeout(10);
        infoSocket = new DatagramSocket(infoSocketPort);
        infoSocket.setSoTimeout(10);

        completeRequestList = new ArrayList<>();
        outstandingRequestList = new ArrayList<>();
        pendingRequestList = new ArrayList<>();

        elevatorList = elevators;
    }

    /**
     * This method will receive elevator info updates from the elevators of their positions and then update the elevator list
     * It will keep calling itself if it does not time out, this ensures that if there are a bunch of pending updates they are all cleared
     * @throws IOException if socket fails
     */
    public void checkForElevatorUpdates() throws IOException {
        DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
        try {
            infoSocket.receive(receivePacket);
        } catch (SocketTimeoutException e) {
            return;
        }

        //Update ElevatorList
        updateElevators(new ElevatorSchedulerData(ElevatorInfo.parsePacket(receivePacket), receivePacket.getPort(), receivePacket.getAddress()));
        //Call this again
        //Recursively calling it again makes sure any pending updates get dealt with before sending any new requests out
        checkForElevatorUpdates();
    }

    /**
     * Updates the elevator list with the status of the elevator
     * @param elevator the Elevator object to update the list with
     */
    public void updateElevators(ElevatorSchedulerData elevator) {
        for (ElevatorSchedulerData e : elevatorList) {
            if (e.compare(elevator)) {
                elevatorList.remove(e);
                elevatorList.add(elevator);
                return;
            }
        }
    }

    /**
     * Check the socket for any requests
     * If they are complete remove that request from the list of outstanding requests and update the elevator
     * If they are incomplete add it to the list of outstanding requests and pending requests
     */
    public void checkForRequests() throws IOException {
        DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
        try {
            requestSocket.receive(receivePacket);
        } catch (SocketTimeoutException e) {
            return;
        }

        RequestWrapper request = new RequestWrapper(Request.parsePacket(receivePacket), findElevator(receivePacket.getPort()));
        if (request.getRequest().isFinished()) {
            //Remove request from list of outstanding requests
            for (RequestWrapper r : outstandingRequestList) {
                if (r.getRequest().getRequestID() == request.getRequest().getRequestID()) {
                    //Set finished time
                    r.complete();
                    //Add it to the list of complete requests
                    completeRequestList.add(r);
                    //Remove it from the list of outstanding requests
                    outstandingRequestList.remove(r);
                    //Decrease the number of passengers (Assuming one passenger per request
                    request.getElevator().decrementNumberOfPassengers();
                    //Set the floor to the destination floor
                    request.getElevator().setCurrentFloor(request.getRequest().getDestinationFloor());
                    return;
                }
            }
            /*
            Remove from outstanding request list
            Add to complete request list
            Update Elevator
             */
        } else { //If it is not complete
            /*
            Add it to the list of outstanding requests
            Add it to the pending list of requests
             */
            request.setElevator(null);
            outstandingRequestList.add(request);
            pendingRequestList.add(request);
            return;
        }
    }

    /**
     * Find the elevator that has this port number
     * @param portNumber port of the elevator you are trying to find
     * @return the elevator object from elevator list that matches that port number
     */
    public ElevatorSchedulerData findElevator(int portNumber) {
        for (ElevatorSchedulerData e : elevatorList) {
            if (e.getSocketNumber() == portNumber) {
                return e;
            }
        }
        return null;
    }

    /**
     * Try to send all the pending messages to elevators
     * If you can send one try with the next until you can't send any or the list is empty
     */
    public void clearPending() {
        if (pendingRequestList.isEmpty()) {
            return;
        }
        if (selectElevator(pendingRequestList.getFirst())) {
            pendingRequestList.removeFirst();
            clearPending();
        }
    }

    /**
     * Select and send to the best elevator for this request
     * @param r The request to be sent
     * @return If request was successfully sent
     */
    public boolean selectElevator(RequestWrapper r) {
        /*
        Criteria for best elevator
        Not at capacity
        Going in right direction (if it is empty direction doesn't matter since it should not be moving)
        Closest
         */
        ArrayList<ElevatorSchedulerData> notFullElevators = new ArrayList<>();
        for (ElevatorSchedulerData e : elevatorList) {
            if (e.isEmpty()) {
                notFullElevators.add(e);
            }
        }

        ArrayList<ElevatorSchedulerData> correctDirectionElevators = new ArrayList<>();
        for (ElevatorSchedulerData e : notFullElevators) {
            if (e.getCurrentFloor() > r.getRequest().getStartingFloor()) {
                if (e.isDownwards()) {
                    correctDirectionElevators.add(e);
                }
            } else if (e.getCurrentFloor() < r.getRequest().getStartingFloor()) {
                if (e.isUpwards()) {
                    correctDirectionElevators.add(e);
                }
            } else if (e.isEmpty() && e.getCurrentFloor() == r.getRequest().getStartingFloor()){ //If an elevator is moving (not empty) then it can pick up passengers on that floor
                correctDirectionElevators.add(e);
            }
        }

        ElevatorSchedulerData bestElevator = null;

        if (!correctDirectionElevators.isEmpty()) {
            bestElevator  = findClosestElevator(correctDirectionElevators, r);
        } else if (!notFullElevators.isEmpty()){
            bestElevator = findClosestElevator(notFullElevators, r);
        }

        if (bestElevator == null) {
            return false;
        }

        try {
            requestSocket.connect(bestElevator.getIpAddress(), bestElevator.getSocketNumber());
            String message = r.getRequest().convertToPacketMessage();
            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.getBytes().length);
            requestSocket.send(sendPacket);
            requestSocket.disconnect();
            bestElevator.incrementNumberOfPassengers();
            r.setElevator(bestElevator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Finds the elevator that is the closest to the starting floor of the request
     * @param elevators List of elevators to check
     * @param r Request to check
     * @return The elevator that is the closest, null if none of them are
     */
    public ElevatorSchedulerData findClosestElevator(ArrayList<ElevatorSchedulerData> elevators,  RequestWrapper r) {
        if (elevators.isEmpty()) {
            return null;
        }
        ElevatorSchedulerData returnElevator = null;
        int smallestDifference = Math.abs(elevators.getFirst().getCurrentFloor() - r.getRequest().getStartingFloor());
        for (ElevatorSchedulerData e : elevators) {
            if (smallestDifference >= Math.abs(e.getCurrentFloor() - r.getRequest().getStartingFloor())) {
                smallestDifference = Math.abs(e.getCurrentFloor() - r.getRequest().getStartingFloor());
                returnElevator = e;
            }
        }
        return returnElevator;
    }

    @Override
    public void run() {
        while (true) {
            try {
                checkForElevatorUpdates();
                checkForRequests();
            } catch (IOException e) {
                continue;
            }
            clearPending();
        }
    }

    public ArrayList<ElevatorSchedulerData> getElevatorList() {
        return elevatorList;
    }

    public ArrayList<RequestWrapper> getCompleteRequestList() {
        return completeRequestList;
    }

    public ArrayList<RequestWrapper> getOutstandingRequestList() {
        return outstandingRequestList;
    }

    public ArrayList<RequestWrapper> getPendingRequestList() {
        return pendingRequestList;
    }
}
