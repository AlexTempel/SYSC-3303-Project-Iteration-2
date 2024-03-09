import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

class SchedulerSubsystemTest {

    @org.junit.jupiter.api.Test
    void getRequestFromInternet() {
    }

    @org.junit.jupiter.api.Test
    void dealWithNewRequest() {
    }

    @org.junit.jupiter.api.Test
    void sendRequestToElevator() {
    }

    @org.junit.jupiter.api.Test
    void checkPending() {
    }

    @org.junit.jupiter.api.Test
    void selectElevator() {
        try {
            Request req = new Request(1,4,10);
            SchedulerSubsystem testScheduler = new SchedulerSubsystem(1);

            testScheduler.selectElevator(req);

            assertTrue(testScheduler.get(0).getInUse());

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
}