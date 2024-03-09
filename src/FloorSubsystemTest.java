import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


class FloorSubsystemTest {





    @Test
    void readCSV() {
        try{
            InetAddress ipAddress = InetAddress.getLoopbackAddress();
            int testFloorPort = 16000;
            int testSchedPort = 17000;

            ArrayList<TimedRequest> testRequests = new ArrayList<TimedRequest>();;
            String file = "TestInput.csv";

            FloorSubsystem testFloor = new FloorSubsystem(testFloorPort, testSchedPort, 5, ipAddress);
            testRequests = testFloor.readCSV(file);

            System.out.println(testRequests.get(0).getTime().truncatedTo(ChronoUnit.MINUTES));
            assertTrue(testRequests.get(0).getTime().truncatedTo(ChronoUnit.MINUTES).compareTo(LocalTime.of(17,30).truncatedTo(ChronoUnit.MINUTES)) == 0);
            assertEquals(1, testRequests.get(0).getRequest().getRequestID());
            assertEquals(18, testRequests.get(0).getRequest().getStartingFloor());
            assertEquals(19, testRequests.get(0).getRequest().getDestinationFloor());

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @org.junit.jupiter.api.Test
    void getCurrentRequest() {
        try{
            InetAddress ipAddress = InetAddress.getLoopbackAddress();
            int testFloorPort = 16001;
            int testSchedPort = 17001;
            ArrayList<TimedRequest> testRequests = new ArrayList<TimedRequest>();;
            String file = "TestInput.csv";

            FloorSubsystem testFloor = new FloorSubsystem(testFloorPort, testSchedPort, 5, ipAddress);

            testRequests = testFloor.readCSV(file);
            Request testRequest;
            testRequest = testFloor.getCurrentRequest(testRequests);

            assertEquals(2, testRequest.getRequestID());
            assertEquals(16, testRequest.getStartingFloor());
            assertEquals(8, testRequest.getDestinationFloor());


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
