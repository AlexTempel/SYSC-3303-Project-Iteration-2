import org.junit.jupiter.api.Test;

import java.net.SocketException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ElevatorSubsystemV2Test {

    @Test
    void getRequests() {
    }

    @Test
    void pickRequest() throws SocketException {
        System.out.println("starting");
        ElevatorSubsystemV2 e = new ElevatorSubsystemV2(1, 5);
        System.out.println("Defining");
        Request r1 = new Request(1, 4, 10);
        Request r2 = new Request(2, 3, 10);
        Request r3 = new Request(3, 2, 10);
        Request r4 = new Request(4, 5, 10);
        Request r5 = new Request(5, 7, 10);
        Request r6 = new Request(6, 6, 10);
        System.out.println("over here");
        ArrayList<Request> list = new ArrayList<Request>();
        System.out.println("now here");
        list.add(r1);
        list.add(r2);
        list.add(r3);
        list.add(r4);
        list.add(r5);
        list.add(r6);

        e.setUpwards();
        System.out.println("After upwards");
        e.pickRequest(list);
        System.out.println("Picked");
        ArrayList<Request> newList = e.getCurrReqList();

        for(int i = 0; i < newList.size(); i++){
            int id = newList.get(i).getRequestID();
            System.out.println("id for element " + i + ": " + id);
        }

        assertEquals(1,1);

    }

    @Test
    void moveElevator() {
    }
}