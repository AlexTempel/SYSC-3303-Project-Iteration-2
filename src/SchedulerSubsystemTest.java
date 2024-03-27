import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerSubsystemTest {

    @Test
    void checkForElevatorUpdates() {
        int schedulerRequestPort = 19999;
        int schedulerInfoPort = 19998;


        int elevator1Port = 10001;
        int elevator2Port = 10002;
        int elevator3Port = 10003;
        int elevator4Port = 10004;
        ElevatorSchedulerData elevator1 = new ElevatorSchedulerData(elevator1Port, InetAddress.getLoopbackAddress());
        ElevatorSchedulerData elevator2 = new ElevatorSchedulerData(elevator2Port, InetAddress.getLoopbackAddress());
        ElevatorSchedulerData elevator3 = new ElevatorSchedulerData(elevator3Port, InetAddress.getLoopbackAddress());
        ElevatorSchedulerData elevator4 = new ElevatorSchedulerData(elevator4Port, InetAddress.getLoopbackAddress());

        ArrayList<ElevatorSchedulerData> initialElevatorList = new ArrayList<>();
        initialElevatorList.add(elevator1);
        initialElevatorList.add(elevator2);
        initialElevatorList.add(elevator3);
        initialElevatorList.add(elevator4);


        int testFloor = 10;
        int testNumberOfPassengers = 3;
        boolean testDirection = true;
        boolean testBroken = true;
        DatagramPacket testPacket = (new ElevatorInfo(testFloor, testNumberOfPassengers, testDirection, testBroken)).convertToPacket();

        try {
            SchedulerSubsystem testScheduler = new SchedulerSubsystem(schedulerRequestPort, schedulerInfoPort, initialElevatorList);
            DatagramSocket testSocket = new DatagramSocket(elevator2Port);

            testSocket.connect(InetAddress.getLoopbackAddress(), schedulerInfoPort);
            testSocket.send(testPacket);
            testSocket.disconnect();

            testScheduler.checkForElevatorUpdates();

            ElevatorSchedulerData elevatorTested = null;
            for (ElevatorSchedulerData e : initialElevatorList) {
                if (e.getSocketNumber() == elevator2Port) {
                    elevatorTested = e;
                    break;
                }
            }

            assertEquals(elevatorTested.isBroken(), testBroken);
            assertEquals(elevatorTested.getCurrentFloor(), testFloor);
            assertEquals(elevatorTested.getNumberOfPassengers(), testNumberOfPassengers);
            assertEquals(elevatorTested.isUpwards(), testBroken);

        } catch (Exception e) {
            return;
        }
    }

    @Test
    void updateElevators() {

    }

    @Test
    void checkForRequests() {
    }

    @Test
    void findElevator() {
    }

    @Test
    void clearPending() {
    }

    @Test
    void selectElevator() {
    }

    @Test
    void findClosestElevator() {
    }
}