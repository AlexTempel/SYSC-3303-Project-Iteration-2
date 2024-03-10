import static org.junit.jupiter.api.Assertions.*;

import org.junit.internal.runners.statements.Fail;
import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

class SchedulerSubsystemTest {

    @org.junit.jupiter.api.Test
    void getRequestFromInternet() throws Exception {
        ArrayList<ElevatorSchedulerData> elevatorList = new ArrayList<>();
        SchedulerSubsystem testSystem = new SchedulerSubsystem(10000, elevatorList);

        Request dummyRequest = new Request(1, 1, 2);

        DatagramSocket dummySocket = new DatagramSocket(10001);
        DatagramPacket dummyPacket = new DatagramPacket(dummyRequest.convertToPacketMessage().getBytes(), dummyRequest.convertToPacketMessage().getBytes().length);
        dummySocket.connect(InetAddress.getLoopbackAddress(), 10000);
        dummySocket.send(dummyPacket);
        dummySocket.disconnect();

        RequestWrapper result = testSystem.getRequestFromInternet();

        assertEquals(dummyRequest.getRequestID(), result.getRequest().getRequestID());
    }

    @org.junit.jupiter.api.Test
    void dealWithNewRequest() {
        try {
            ArrayList<ElevatorSchedulerData> elevatorList = new ArrayList<>();
            elevatorList.add(new ElevatorSchedulerData(1, InetAddress.getLoopbackAddress()));
            elevatorList.add(new ElevatorSchedulerData(2, InetAddress.getLoopbackAddress()));
            ElevatorSchedulerData elevator3 = new ElevatorSchedulerData(3, InetAddress.getLoopbackAddress());
            elevator3.setInUse(true);
            elevatorList.add(elevator3);


            Request r1 = new Request(1, 1, 2);
            ArrayList<Request> outstandingRequests = new ArrayList<>();
            outstandingRequests.add(r1);

            SchedulerSubsystem testSystem = new SchedulerSubsystem(1, elevatorList, outstandingRequests);

            ElevatorSchedulerData elevatorTest = new ElevatorSchedulerData(3, InetAddress.getLoopbackAddress());
            RequestWrapper rw = new RequestWrapper(new Request(1, 1, 2), elevatorTest);
            rw.getRequest().complete();

            testSystem.dealWithNewRequest(rw);

            assertTrue(testSystem.getOutstandingRequestList().isEmpty());
            assertFalse(elevator3.getInUse());
            assertEquals(2, elevator3.getCurrentFloor());

            int numOutstanding = testSystem.getOutstandingRequestList().size();

            testSystem.dealWithNewRequest(
                    new RequestWrapper(
                            new Request(5,5,10),
                            new ElevatorSchedulerData(1, InetAddress.getLoopbackAddress())));

            assertEquals(numOutstanding + 1, testSystem.getOutstandingRequestList().size());
        } catch (Exception e) {
            fail("Exception in code");
        }
    }

    @org.junit.jupiter.api.Test
    void sendRequestToElevator() throws Exception {
        ArrayList<ElevatorSchedulerData> elevatorList = new ArrayList<>();
        ElevatorSchedulerData e1 = new ElevatorSchedulerData(10000, InetAddress.getLoopbackAddress());
        elevatorList.add(e1);

        SchedulerSubsystem testSystem = new SchedulerSubsystem(10001, elevatorList);

        DatagramSocket dummySocket = new DatagramSocket(10000);

        Request req = new Request(1, 1, 20);
        testSystem.sendRequestToElevator(req, e1);

        assertTrue(e1.getInUse());

        DatagramPacket received = new DatagramPacket(new byte[1024], 1024);
        dummySocket.receive(received);
        Request receivedRequest = Request.parsePacket(received);

        assertEquals(req.getRequestID(), receivedRequest.getRequestID());
    }

    @org.junit.jupiter.api.Test
    void checkPending() {
        try {
            Request req = new Request(1,2,12);
            ArrayList<ElevatorSchedulerData> eleva = new ArrayList<>();
            ElevatorSchedulerData a = new ElevatorSchedulerData(1121, InetAddress.getLoopbackAddress());
            ElevatorSchedulerData b = new ElevatorSchedulerData(1232, InetAddress.getLoopbackAddress());
            ElevatorSchedulerData c = new ElevatorSchedulerData(1343, InetAddress.getLoopbackAddress());
            ElevatorSchedulerData d = new ElevatorSchedulerData(1454, InetAddress.getLoopbackAddress());
            eleva.add(a);
            eleva.add(b);
            eleva.add(c);
            eleva.add(d);

            SchedulerSubsystem testPending = new SchedulerSubsystem(34, eleva);

            // send one check if it has one in it
            assertTrue(testPending.getPendingRequestList().isEmpty());

            RequestWrapper req1 = new RequestWrapper(req, a);
            testPending.dealWithNewRequest(req1);
            RequestWrapper req2 = new RequestWrapper(req, b);
            testPending.dealWithNewRequest(req1);
            RequestWrapper req3 = new RequestWrapper(req, c);
            testPending.dealWithNewRequest(req1);
            RequestWrapper req4 = new RequestWrapper(req, d);
            testPending.dealWithNewRequest(req1);

            assertFalse(testPending.getPendingRequestList().isEmpty());
            assertEquals(testPending.getPendingRequestList().size(), 4);
            testPending.checkPending();
            assertEquals(testPending.getPendingRequestList().size(), 0);

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @org.junit.jupiter.api.Test
    void selectElevator() {
        try {
            Request req = new Request(1,4,10);
            ArrayList<ElevatorSchedulerData> eleva = new ArrayList<>();
            ElevatorSchedulerData a = new ElevatorSchedulerData(1111, InetAddress.getLoopbackAddress());
            ElevatorSchedulerData b = new ElevatorSchedulerData(1222, InetAddress.getLoopbackAddress());
            ElevatorSchedulerData c = new ElevatorSchedulerData(1333, InetAddress.getLoopbackAddress());
            ElevatorSchedulerData d = new ElevatorSchedulerData(1444, InetAddress.getLoopbackAddress());
            eleva.add(a);
            eleva.add(b);
            eleva.add(c);
            eleva.add(d);

            SchedulerSubsystem testScheduler = new SchedulerSubsystem(34, eleva);

            RequestWrapper req1 = new RequestWrapper(req, a);
            testScheduler.dealWithNewRequest(req1);
            testScheduler.selectElevator(req);
            assertTrue(testScheduler.getElevatorList().getFirst().getInUse(),
                    "first elevator should be running");

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
}