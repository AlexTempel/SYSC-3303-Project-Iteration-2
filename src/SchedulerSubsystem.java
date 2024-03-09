import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.lang.Math;

public class SchedulerSubsystem implements Runnable {
    private final DatagramSocket socket;
    private final ArrayList<ElevatorSchedulerData> elevatorList;
    private final ArrayList<Request> pendingRequestList;
    private final ArrayList<Request> outstandingRequestList;

    private final ArrayList<Request> pendingRequestList;

    SchedulerSubsystem(int port) throws SocketException {
        elevatorList = new ArrayList<>();
        outstandingRequestList = new ArrayList<>();
        pendingRequestList = new ArrayList<>();

        socket = new DatagramSocket(port);
    }

    /**
     * Gets a packet from the socket and puts its data in a RequestWrapper
     * @return RequestWrapper of the data packet
     * @throws IOException if it cannot receive on socket for some reason
     */
    public RequestWrapper getRequestFromInternet() throws IOException {
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
        if (request.getRequest().isFinished()) { //If the request says it is done
            for (Request r : outstandingRequestList) { //Remove that request from the list of requests
                if (request.getRequest().getRequestID() == r.getRequestID()) { //Two requests are the same if they have the same requestID
                    outstandingRequestList.remove(r); //Remove it from the list of outstanding requests
                    updateElevatorList(request); //Update the record of the elevator to be in that location
                    return;
                }
            }
        } else { //If it isn't a complete request add it to the list of outstanding requests
            selectElevator(request.getRequest());
            outstandingRequestList.add(request.getRequest());
            pendingRequestList.add(request.getRequest());
        }
    }

    /**
     * Method to send a request to an elevator
     * @param request Request to be sent to the elevator
     * @param elevator Elevator to send request to
     * @throws IOException If there is an error sending the packet
     */
    public void sendRequestToElevator(Request request, ElevatorSchedulerData elevator) throws IOException {
        String message = request.convertToPacketMessage();
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.getBytes().length);

        socket.connect(elevator.getIpAddress(), elevator.getSocketNumber());
        socket.send(sendPacket);
        socket.disconnect();

        elevator.setInUse(true);
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

    public void checkPending() {
        selectElevator(pendingRequestList.getFirst());
    }

    /**
     * Selects the closeted elevator to the requested floor
     *
     */
    public void selectElevator(Request request) {
        int x = 0;
        ElevatorSchedulerData eli = elevatorList.get(0);
        for (int i = 0; i < 4; i++) {
            if (elevatorList.get(i).getInUse()){
                x++;
            }
            else {
                if (Math.abs(eli.getCurrentFloor() - request.getDestinationFloor()) >
                        Math.abs(elevatorList.get(i).getCurrentFloor() - request.getDestinationFloor())) {
                    eli = elevatorList.get(i);
                }
            }
        }
        if (x == 4) {
            // elevator are all in use
        }
        else {
            try {
                sendRequestToElevator(request, eli);
            } catch (IOException e) {
            }
        }
    }
}