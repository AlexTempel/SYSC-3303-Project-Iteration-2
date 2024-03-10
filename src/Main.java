import java.net.SocketException;

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


            int schedulerPort = 19509;
            SchedulerSubsystem scheduler = new SchedulerSubsystem(schedulerPort);

        } catch (SocketException ex) {
            throw new RuntimeException(ex);
        }
    }

}