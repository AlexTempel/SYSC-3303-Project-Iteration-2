import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ElevatorSubsystemTest {

    @Test
    void sendAndReceiveRequests() throws UnknownHostException {
        try {
            // Create elevator object and request
            ElevatorSubsystem testElevator = new ElevatorSubsystem(19505);
            Request req = new Request(2,1,5);

            // Build dummy socket and message
            String message = req.convertToPacketMessage();
            InetAddress elevatorAddress = InetAddress.getByName("127.0.0.1");
            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.getBytes().length, elevatorAddress,19505);
            DatagramSocket mySocket = new DatagramSocket(19506);

            // Send to Elevator socket
            mySocket.send(sendPacket);

            // Receive Request from Elevator
            Request readReq = testElevator.getRequestData();
            assertEquals(req.convertToPacketMessage(), readReq.convertToPacketMessage());

            // Confirmation
            req.complete();
            testElevator.sendConfirmation(req);
            DatagramPacket receiveConfPacket = new DatagramPacket(new byte[1024], 1024);
            mySocket.receive(receiveConfPacket);

            assertEquals(req.convertToPacketMessage(), Request.parsePacket(receiveConfPacket).convertToPacketMessage());

            // Cleanup
            mySocket.disconnect();
            testElevator.closeSocket();

        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    void moveElevator() {
        try {
            ElevatorSubsystem testElevator = new ElevatorSubsystem(1);
            testElevator.moveElevator(2);
            assertEquals(2,testElevator.getCurrentFloor());
            testElevator.closeSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleRequest() {
        try {
            Request req = new Request(1,2,10);
            ElevatorSubsystem testElevator = new ElevatorSubsystem(1);

            testElevator.handleRequest(req);
            assertEquals(10,testElevator.getCurrentFloor());
            testElevator.closeSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }
}
