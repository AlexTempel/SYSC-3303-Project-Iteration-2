import java.net.InetAddress;

public class ElevatorSchedulerData { //Meant only for Scheduler
    //This class is used by the scheduler to hold the data it knows about an Elevator
    private int currentFloor;
    private boolean inUse;
    private final int socketNumber;
    private final InetAddress ipAddress;

    ElevatorSchedulerData(int socketNumber, InetAddress ipAddress) {
        currentFloor = 1;
        inUse = false;
        this.socketNumber = socketNumber;
        this.ipAddress = ipAddress;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }
    public void setCurrentFloor(int newFloor) {
        currentFloor = newFloor;
    }
    public boolean getInUse() {
        return inUse;
    }
    public void setInUse(boolean status) {
        inUse = status;
    }
    public int getSocketNumber() {
        return socketNumber;
    }
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public boolean compare(ElevatorSchedulerData otherElevator) {
        return (this.ipAddress == otherElevator.ipAddress && this.socketNumber == otherElevator.socketNumber);
    }
}
