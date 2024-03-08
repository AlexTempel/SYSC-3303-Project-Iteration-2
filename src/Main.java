import java.net.SocketException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");

        // Create 4 Elevators with socket ports 19505-19508
        try {
            ElevatorSubsystem elevator19505 = new ElevatorSubsystem(19505);
            ElevatorSubsystem elevator19506 = new ElevatorSubsystem(19506);
            ElevatorSubsystem elevator19507 = new ElevatorSubsystem(19507);
            ElevatorSubsystem elevator19508 = new ElevatorSubsystem(19508);

        } catch (SocketException ex) {
            throw new RuntimeException(ex);
        }
    }

}