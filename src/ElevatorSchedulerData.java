import java.net.InetAddress;

public class ElevatorSchedulerData { //Meant only for Scheduler
    //This class is used by the scheduler to hold the data it knows about an Elevator
    private int currentFloor;
    private final int socketNumber;
    private final InetAddress ipAddress;
    private int numberOfPassengers;
    private boolean upwards;

    ElevatorSchedulerData(int socketNumber, InetAddress ipAddress) {
        currentFloor = 1;
        numberOfPassengers = 0;
        upwards = true;
        this.socketNumber = socketNumber;
        this.ipAddress = ipAddress;
    }

    public void update(ElevatorInfo info) {
        this.currentFloor = info.getFloor();
        this.upwards = info.goingUpwards();
        this.numberOfPassengers = info.getNumberOfPassengers();
    }

    public void setUpwards(boolean upwards) {
        this.upwards = upwards;
    }

    public boolean isUpwards() {
        return upwards;
    }

    public boolean isDownwards() {
        return !upwards;
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int number) {
        numberOfPassengers = number;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }
    public void setCurrentFloor(int newFloor) {
        currentFloor = newFloor;
    }
    public int getSocketNumber() {
        return socketNumber;
    }
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public boolean compare(ElevatorSchedulerData otherElevator) {
        return (this.socketNumber == otherElevator.socketNumber);
    }
}
