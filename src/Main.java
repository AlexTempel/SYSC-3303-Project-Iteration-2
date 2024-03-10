import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        // Create 4 Elevators with socket ports 19505-19508
        try {
            int elevator1Port = 19505;
            int elevator2Port = 19506;
            int elevator3Port = 19507;
            int elevator4Port = 19508;

            ElevatorSubsystem elevator1 = new ElevatorSubsystem(elevator1Port);
            ElevatorSubsystem elevator2 = new ElevatorSubsystem(elevator2Port);
            ElevatorSubsystem elevator3 = new ElevatorSubsystem(elevator3Port);
            ElevatorSubsystem elevator4 = new ElevatorSubsystem(elevator4Port);

            Thread elevator1Thread = new Thread(elevator1);
            Thread elevator2Thread = new Thread(elevator2);
            Thread elevator3Thread = new Thread(elevator3);
            Thread elevator4Thread = new Thread(elevator4);


            InetAddress elevator1IP = InetAddress.getLoopbackAddress();
            InetAddress elevator2IP = InetAddress.getLoopbackAddress();
            InetAddress elevator3IP = InetAddress.getLoopbackAddress();
            InetAddress elevator4IP = InetAddress.getLoopbackAddress();

            ArrayList<ElevatorSchedulerData> elevators = new ArrayList<>();
            elevators.add(new ElevatorSchedulerData(elevator1Port, elevator1IP));
            elevators.add(new ElevatorSchedulerData(elevator2Port, elevator2IP));
            elevators.add(new ElevatorSchedulerData(elevator3Port, elevator3IP));
            elevators.add(new ElevatorSchedulerData(elevator4Port, elevator4IP));

            int schedulerPort = 19509;
            SchedulerSubsystem scheduler = new SchedulerSubsystem(schedulerPort, elevators);

            Thread schedulerThread = new Thread(scheduler);

            InetAddress schedulerIP = InetAddress.getLoopbackAddress();
            FloorSubsystem floorSubsystem = new FloorSubsystem(19510, schedulerPort, 22, schedulerIP);

            Thread floorThread = new Thread(floorSubsystem);

            elevator1Thread.start();
            elevator2Thread.start();
            elevator3Thread.start();
            elevator4Thread.start();
            schedulerThread.start();
            floorThread.start();

        } catch (SocketException ex) {
            throw new RuntimeException(ex);
        }
    }
}