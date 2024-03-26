import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ElevatorSubsystem implements Runnable {
    private final int elevator_id;
    private final ElevatorDoors doors;
    private final DatagramSocket socket;
    private int current_floor;
    private InetAddress schedulerAddress = null;
    private int schedulerPort = -1;
    private ElevatorState state;

    /**
     * Contruct the ElevatorSubsystem Object
     * @param id integer identifier of the elevator, also the recieve port
     */
    public ElevatorSubsystem(int id) throws SocketException {
        this.elevator_id = id;
        this.doors = new ElevatorDoors();
        this.socket = new DatagramSocket(id);
        this.current_floor = 1; //start the Elevator at the ground floor
        this.state = ElevatorState.WAITING;

    }

    /**
     * Attempt to complete the request
     */
    public void run() {
        System.out.println("Starting Elevator");
        while (state != ElevatorState.BROKEN) {
            // Obtain formatted request data
            state = ElevatorState.WAITING;
            System.out.printf("Elevator %d Current State: %s\n",elevator_id, state);
            try {
                Request myRequest = getRequestData();
                handleRequest(myRequest);
                // Send completed request back to scheduler
                sendConfirmation(myRequest);
            }catch(IOException e){} catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
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


    /**
     * Get the Request packet from scheduler, return request with the data
     * @return the Request from the scheduler
     * @throws IOException
     */
    public Request getRequestData() throws IOException {
        DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
        socket.receive(receivePacket);

        if(schedulerAddress == null) {
            schedulerAddress = receivePacket.getAddress();
            schedulerPort = receivePacket.getPort();
        }

        System.out.println("Elevator received request from scheduler");
        return Request.parsePacket(receivePacket);
    }


    /**
     * Moves the elevator to the starting floor, then to the destination floor
     */
    public void moveElevator(int destination) {
        // Check how many floors elevator needs to travel
        int floorDifference = Math.abs(current_floor - destination);

        // Print state
        state = ElevatorState.MOVING;
        System.out.printf("Elevator %d Current State: %s\n",elevator_id, state);

        for (int i = floorDifference; i > 0; i--) {
            if (Math.random() <= 0.01) {
                state = ElevatorState.BROKEN;
                System.out.printf("Elevator %d is broken\n", elevator_id);
                return;
            }
            System.out.printf("Elevator %d needs to travel %d floors to reach destination\n", elevator_id, i);
            try {
                Thread.sleep(1000); // Simulate travel time
            } catch (InterruptedException e) {
            }
        }
        // Complete the trip
        current_floor = destination;
    }

    /**
     * Moves the elevator to the starting floor, then to the destination floor
     */
    public void handleRequest(Request currentReq) throws InterruptedException {
        int startingFloor = currentReq.getStartingFloor();
        int endingFloor = currentReq.getDestinationFloor();

        // Move from the current floor to the starting floor
        if (startingFloor != current_floor) {
            moveElevator(startingFloor);
            if (state == ElevatorState.BROKEN) {
                return;
            }
        }
        // Open Close Elevator Doors
        cycleDoors();

        // Move to destination floor
        moveElevator(endingFloor);
        if (state == ElevatorState.BROKEN) {
            return;
        }

        cycleDoors();

        // Mark complete
        currentReq.complete();
    }

    private void cycleDoors() throws InterruptedException {
        state = ElevatorState.DOORS_OPEN;
        System.out.printf("Elevator %d Current State: %s\n",elevator_id, state);
        doors.toggleDoors();

        state = ElevatorState.LOADING;
        System.out.printf("Elevator %d Current State: %s\n",elevator_id, state);
        Thread.sleep(2000);

        doors.toggleDoors();
        state = ElevatorState.DOORS_CLOSE;
        System.out.printf("Elevator %d Current State: %s\n",elevator_id, state);
    }

    public int getCurrentFloor(){
        return current_floor;
    }

    public void closeSocket(){
        socket.close();
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