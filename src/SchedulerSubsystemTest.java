import static org.junit.jupiter.api.Assertions.*;

import org.junit.internal.runners.statements.Fail;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

class SchedulerSubsystemTest {

    @org.junit.jupiter.api.Test
    void getRequestFromInternet() {

    }

    @org.junit.jupiter.api.Test
    void dealWithNewRequest() {
        try {
            ArrayList<ElevatorSchedulerData> elevatorList = new ArrayList<>();
            elevatorList.add(new ElevatorSchedulerData(1, InetAddress.getLoopbackAddress()));
            elevatorList.add(new ElevatorSchedulerData(2, InetAddress.getLoopbackAddress()));
            ElevatorSchedulerData elevator3 = new ElevatorSchedulerData(3, InetAddress.getLoopbackAddress());
            elevator3.setInUse(true);


            Request r = new Request(1, 1, 2);
            ArrayList<Request> outstandingRequests = new ArrayList<>();
            outstandingRequests.add(r);

            SchedulerSubsystem testSystem = new SchedulerSubsystem(1, elevatorList, outstandingRequests);

            ElevatorSchedulerData elevatorTest = new ElevatorSchedulerData(3, InetAddress.getLoopbackAddress());
            RequestWrapper rw = new RequestWrapper(r, elevatorTest);


            testSystem.dealWithNewRequest(rw);
        } catch (Exception e) {
            fail("Exception in code");
        }
    }

    @org.junit.jupiter.api.Test
    void sendRequestToElevator() {

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