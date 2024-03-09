import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;


class FloorSubsystemTest {

    @Test
    void readCSV() {
        try{
            InetAddress ipAddress = InetAddress.getLoopbackAddress();
            int testFloorPort = 16000;
            int testSchedPort = 17000;

            FloorSubsystem testFloor = new FloorSubsystem(testFloorPort, testSchedPort, 5, ipAddress);
            ArrayList<TimedRequest> testRequests;
            String file = "TestInput.csv";

            testRequests = testFloor.readCSV(file);
            System.out.println(testRequests);
        } catch (SocketException e){
            throw new RuntimeException(e);
        }
    }

    @org.junit.jupiter.api.Test
    void getCurrentRequest() {
        try{

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @org.junit.jupiter.api.Test
    void sendToScheduler() {
        try{

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
