import java.net.InetAddress;
import java.util.ArrayList;


class FloorSubsystemTest {

    @org.junit.jupiter.api.Test
    void readCSV(String file) {
        try{
            InetAddress ipAddress = InetAddress.getLocalHost();
            FloorSubsystem testFloor = new FloorSubsystem(16000, 17000, 5, ipAddress);
            ArrayList<TimedRequest> testRequests = new ArrayList<TimedRequest>();
            String file = "TestInput.txt";
            testRequests = readCSV();
        } catch (Exception e){
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
