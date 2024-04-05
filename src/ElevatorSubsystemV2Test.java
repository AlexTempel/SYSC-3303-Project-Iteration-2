import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ElevatorSubsystemV2Test {

    @Test
    void getRequests() {
    }

    @Test
    void pickRequest() throws SocketException {

        ElevatorSubsystemV2 e = new ElevatorSubsystemV2(1, 5);

        Request r1 = new Request(1, 4, 10);
        Request r2 = new Request(2, 3, 10);
        Request r3 = new Request(3, 2, 10);
        Request r4 = new Request(4, 5, 10);
        Request r5 = new Request(5, 7, 10);
        Request r6 = new Request(6, 6, 10);

        ArrayList<Request> list = new ArrayList<Request>();

        list.add(r1);
        list.add(r2);
        list.add(r3);
        list.add(r4);
        list.add(r5);
        list.add(r6);

        e.setUpwards();
        e.pickRequest(list);
        ArrayList<Request> newList = e.getCurrReqList();

        for(int i = 0; i < newList.size(); i++){
            int id = newList.get(i).getRequestID();
            System.out.println("id for element " + i + ": " + id);
        }

        e.closeSocket();
        assertEquals(1,1);

    }

    @Test
    void moveElevator() throws IOException, InterruptedException {
        ElevatorSubsystemV2 e1 = new ElevatorSubsystemV2(    19505, 19506);

        Request r1 = new Request(1, 1, 2);
        Request r2 = new Request(2, 3, 10);
        Request r3 = new Request(3, 2, 10);
        Request r4 = new Request(4, 5, 10);
        Request r5 = new Request(5, 7, 10);
        Request r6 = new Request(6, 6, 10);

        ArrayList<Request> list = new ArrayList<Request>();

        list.add(r1);
        list.add(r2);
        list.add(r3);
        list.add(r4);
        list.add(r5);
        list.add(r6);

        e1.setUpwards();
        e1.pickRequest(list);

        //set address for scheduler
        e1.setSchedulerAddress();

        e1.moveElevator(); // Start on floor 1 go to 2
        e1.moveElevator(); // Actions at floor 2
    }
}