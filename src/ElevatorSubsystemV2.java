import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ElevatorSubsystemV2 implements Runnable {
    private final int elevator_id;
    private final ElevatorDoors doors;
    private final DatagramSocket socket;
    private int ElevatorInfoSocketID;
    private int current_floor;
    private InetAddress schedulerAddress = null;
    private int schedulerPort = -1;
    private ElevatorState state;
    private int numPeople = 0;
    private ArrayList<Request> allReqList = new ArrayList<Request>();
    private Boolean upwards = null;
    private Request currentRequest = null;
    private ArrayList<Request> currReqList = new ArrayList<Request>();

    /**
     * Contruct the ElevatorSubsystem Object
     * @param id integer identifier of the elevator, also the recieve port
     */
    public ElevatorSubsystemV2(int id, int elevatorInfoPort) throws SocketException {
        this.elevator_id = id;
        this.doors = new ElevatorDoors();
        this.socket = new DatagramSocket(id);
        this.current_floor = 1; //start the Elevator at the ground floor
        this.state = ElevatorState.WAITING;
        this.ElevatorInfoSocketID = elevatorInfoPort;

    }

    @Override
    public void run() {
        System.out.println("Starting Elevator");
        while (state != ElevatorSubsystemV2.ElevatorState.BROKEN) {
            // Obtain formatted request data
            state = ElevatorSubsystemV2.ElevatorState.WAITING;
            System.out.printf("Elevator %d Current State: %s\n",elevator_id, state);
            try {
                if(currReqList.isEmpty()){
                    if(upwards == null){
                        upwards = true;
                    }else{
                        upwards = !upwards;
                    }
                }
                getRequests();
                pickRequest(allReqList);
                moveElevator();
                updateScheduler();
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Collect all the requests waiting on the socket and add to an arraylist
     */
    public void getRequests(){

        DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);

        //receive all the pending requests
        try {
            socket.setSoTimeout(10);
            socket.receive(receivePacket);

        } catch (IOException e) {
            return;
        }

        // Find scheduler address from rec'd packet
        if(schedulerAddress == null) {
            schedulerPort = receivePacket.getPort();
        }

        // Add request to list
        Request myRequest = Request.parsePacket(receivePacket);
        System.out.println("Elevator received request from scheduler");
        allReqList.add(Request.parsePacket(receivePacket));
    }

    /**
     * Determine which of the Requests to serve
     */
    public void pickRequest(ArrayList<Request> reqList) {
        if (reqList.isEmpty() || currReqList.size() == 5) {
            return;
        } else {
            // Loop through all requests to see the appropriate one to service
            while (currReqList.size() < 5) {

                System.out.println("This is the size of currReqList: " + currReqList.size());
                Request tempReq = null;
                int removeIndex = -1;

                for (int i = 0; i < reqList.size(); i++) {
                    // Check only for upwards requests
                    if (upwards) {
                        // Assign a request to temp req only if it is correct direction
                        if (tempReq == null) {
                            if (reqList.get(i).getStartingFloor() - current_floor >= 0){
                                tempReq = reqList.get(i);
                                removeIndex = i;
                            }
                        }else{
                            // Compare the temp req with the next one in the list
                            int currentDiff = tempReq.getStartingFloor() - current_floor;
                            int newDiff = reqList.get(i).getStartingFloor() - current_floor;

                            // Replace if it is closer and upwards
                            if(newDiff < currentDiff && newDiff > 0){
                                tempReq = reqList.get(i);
                                removeIndex = i;
                            }
                        }
                    }else{ //assume downwards
                        // Assign a request to temp req only if it is correct direction
                        if (tempReq == null) {
                            if (reqList.get(i).getStartingFloor() - current_floor <= 0){
                                tempReq = reqList.get(i);
                                removeIndex = i;
                            }
                        }else{
                            // Compare the temp req with the next one in the list
                            int currentDiff = tempReq.getStartingFloor() - current_floor;
                            int newDiff = reqList.get(i).getStartingFloor() - current_floor;

                            // Replace if it is closer and downwards
                            if(newDiff > currentDiff && newDiff < 0){
                                tempReq = reqList.get(i);
                                removeIndex = i;
                            }
                        }
                    }
                }
                currReqList.add(tempReq);
                if (removeIndex != -1){
                    System.out.println("Removing index: " + removeIndex);
                    reqList.remove(removeIndex);
                }
            }
        }
    }

    /**
     * Move the Elevator one floor and see if anyone is getting off/on
     */
    public void moveElevator() throws InterruptedException, IOException {

        // See if anyone is getting on or off
        int numUnloading = 0;
        int numLoading = 0;
        int index = 0;
        for (Request request : currReqList) {
            if (current_floor == request.getStartingFloor()) {
                numLoading  += 1;
            } else if (current_floor == request.getDestinationFloor()){
                numUnloading += 1;

                // Remove the finished request
                currReqList.get(index).complete();
                sendConfirmation(currReqList.get(index));

                // Take out of current req list
                currReqList.remove(index);
            }
            index += 1;
        }

        // Let them on or off
        if (numLoading != 0 || numUnloading != 0){
            cycleDoors();
            numPeople = numPeople + numLoading - numUnloading;
        }

        // Move if we have direction and request
        if(upwards && !currReqList.isEmpty()){
            current_floor += 1;
        }else if (!upwards && !currReqList.isEmpty()){
            current_floor -= 1;
        }

    }


    public ArrayList<Request> getCurrReqList(){

        return currReqList;
    }

    public void setUpwards(){

        upwards = true;
    }

    /**
     * Simulate opening and closing the doors
     * @throws InterruptedException
     */
    private void cycleDoors() throws InterruptedException {
        state = ElevatorSubsystemV2.ElevatorState.DOORS_OPEN;
        System.out.printf("Elevator %d Current State: %s\n",elevator_id, state);
        doors.toggleDoors();

        state = ElevatorSubsystemV2.ElevatorState.LOADING;
        System.out.printf("Elevator %d Current State: %s\n",elevator_id, state);
        Thread.sleep(2000);

        doors.toggleDoors();
        state = ElevatorSubsystemV2.ElevatorState.DOORS_CLOSE;
        System.out.printf("Elevator %d Current State: %s\n",elevator_id, state);
    }

    /**
     * Converts Request to string, sends packet to Scheduelr
     * @param confirmation Request with the completed attribute true
     * @throws IOException
     */
    public void sendConfirmation(Request confirmation) throws IOException {
        String message = confirmation.convertToPacketMessage();
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.getBytes().length);
        socket.connect(schedulerAddress, schedulerPort);
        socket.send(sendPacket);
        socket.disconnect();

        System.out.println("Elevator sent complete request");
    }

    // TODO add updating packet logic
    public void updateScheduler(){
        //create new info packet
        ElevatorInfo info = new ElevatorInfo(current_floor, numPeople, upwards, false);
        DatagramPacket infoPacket = info.convertToPacket();
    }

    public enum ElevatorState {

        WAITING,
        MOVING,
        DOORS_OPEN,
        DOORS_CLOSE,
        LOADING,
        BROKEN

    }
}
