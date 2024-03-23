import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.lang.Math;
import java.util.NoSuchElementException;

public class SchedulerSubsystem implements Runnable {
    private final DatagramSocket socket;
    private final ArrayList<ElevatorSchedulerData> elevatorList;
    private final ArrayList<Request> pendingRequestList;
    private final ArrayList<Request> outstandingRequestList;
    private enum state {
        waiting,
        handlingRequest,
        clearingPending,
        sendingRequest
    }
    private Enum<state> currentState;

    SchedulerSubsystem(int port, ArrayList<ElevatorSchedulerData> elevatorList) throws SocketException {
        currentState = state.waiting;
        this.elevatorList = elevatorList;
        outstandingRequestList = new ArrayList<>();
        pendingRequestList = new ArrayList<>();

        socket = new DatagramSocket(port);
    }

    SchedulerSubsystem(int port, ArrayList<ElevatorSchedulerData> elevatorList, ArrayList<Request> outstandingRequestList) throws SocketException {
        this.elevatorList = elevatorList;
        this.outstandingRequestList = outstandingRequestList;
        pendingRequestList = new ArrayList<>();

        socket = new DatagramSocket(port);
    }

    /**
     * Gets a packet from the socket and puts its data in a RequestWrapper
     * @return RequestWrapper of the data packet
     * @throws IOException if it cannot receive on socket for some reason
     */
    public RequestWrapper getRequestFromInternet() throws IOException {
        currentState = state.waiting;
        DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
        socket.receive(receivePacket);

        return new RequestWrapper(Request.parsePacket(receivePacket), new ElevatorSchedulerData(receivePacket.getPort(), receivePacket.getAddress()));
    }

    /**
     * Updates the location of an elevator once it sends a message saying it has completed a request
     * @param request the data from a packet to update the elevator with
     */
    private void updateElevatorList(RequestWrapper request) {
        for (ElevatorSchedulerData e : elevatorList) {
            if (e.compare(request.getElevator())) {
                e.setCurrentFloor(request.getRequest().getDestinationFloor());
                e.setInUse(false);

                System.out.println("Scheduler received request from elevator " + (elevatorList.indexOf(e) + 1));
                return;
            }
        }
    }

    /**
     * Do what needs to be done with a message from the internet
     * If it is a request that is marked complete, delete that request from the list of outstanding requests and update the elevator position that handled that request
     * If it is not marked complete, add it to the list of outstanding requests another method will handle delegating those requests
     * @param request the new message from the internet
     */
    public void dealWithNewRequest(RequestWrapper request) {
        currentState = state.handlingRequest;
        if (request.getRequest().isFinished()) { //If the request says it is done
            for (Request r : outstandingRequestList) { //Remove that request from the list of requests
                if (request.getRequest().getRequestID() == r.getRequestID()) { //Two requests are the same if they have the same requestID
                    outstandingRequestList.remove(r); //Remove it from the list of outstanding requests
                    updateElevatorList(request); //Update the record of the elevator to be in that location
                    return;
                }
            }
        } else { //If it isn't a complete request add it to the list of outstanding requests
            outstandingRequestList.add(request.getRequest());
            pendingRequestList.add(request.getRequest());
            System.out.println("Scheduler received request from floor");
        }
    }

    /**
     * Method to send a request to an elevator
     * @param request Request to be sent to the elevator
     * @param elevator Elevator to send request to
     * @throws IOException If there is an error sending the packet
     */
    public void sendRequestToElevator(Request request, ElevatorSchedulerData elevator) throws IOException {
        currentState = state.sendingRequest;
        String message = request.convertToPacketMessage();
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.getBytes().length);

        socket.connect(elevator.getIpAddress(), elevator.getSocketNumber());
        socket.send(sendPacket);
        socket.disconnect();

        elevator.setInUse(true);

        System.out.println("Scheduler sent request to elevator " + (elevatorList.indexOf(elevator) + 1));
    }

    public void run() {
        System.out.println("Scheduler Starting");

        RequestWrapper currentRequest;
        while (true) {
            try {
                currentRequest = getRequestFromInternet();
            } catch (IOException e) { //If there's an error getting a request from the internet loop back again
                continue;
            }
            dealWithNewRequest(currentRequest);
            checkPending();
        }
    }

    /**
     * Checks if any pending requests can be sent. If so check the next one
     */
    public void checkPending() {
        currentState = state.clearingPending;
        try {
            if (selectElevator(pendingRequestList.getFirst())) {
                checkPending();
            }
        } catch (NoSuchElementException e) {}
    }

    /**
     * first checks for free elevator, if not exit, then if it has a free
     * Selects the closeted elevator to the requested floor, and removes the
     * request from pending
     * @param request
     */
    public boolean selectElevator(Request request) {
        int smallestDifference = 100000;
        ElevatorSchedulerData closestElevator = null;
        for (ElevatorSchedulerData e : elevatorList) {
            if (!e.getInUse()) {
                if (smallestDifference >= Math.abs(e.getCurrentFloor() - request.getStartingFloor())) {
                    smallestDifference = Math.abs(e.getCurrentFloor() - request.getStartingFloor());
                    closestElevator = e;
                }
            }
        }
        if (closestElevator != null) {
            try {
                sendRequestToElevator(request, closestElevator);
                pendingRequestList.removeFirst();
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
    public ArrayList<ElevatorSchedulerData> getElevatorList(){
        return elevatorList;
    }

    public ArrayList<Request> getOutstandingRequestList() {
        return outstandingRequestList;
    }

    public ArrayList<Request> getPendingRequestList() {
        return pendingRequestList;
    }
}