import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

public class ElevatorSubsystemTest {

    @Test
    void moveElevator() {
        try {
            ElevatorSubsystem testElevator = new ElevatorSubsystem(1);
            testElevator.moveElevator(2);
            assertEquals(2,testElevator.getCurrentFloor());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleRequest() {
        try {
            Request req = new Request(1,2,10);
            ElevatorSubsystem testElevator = new ElevatorSubsystem(2);

            testElevator.handleRequest(req);
            assertEquals(10,testElevator.getCurrentFloor());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }
}
