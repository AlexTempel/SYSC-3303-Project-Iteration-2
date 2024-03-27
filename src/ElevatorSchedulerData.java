import java.net.InetAddress;

public class ElevatorSchedulerData { //Meant only for Scheduler
    //This class is used by the scheduler to hold the data it knows about an Elevator
    private int currentFloor;
    private final int socketNumber;
    private final InetAddress ipAddress;
    private int numberOfPassengers;
    private boolean upwards;
    private final int capacity;

    ElevatorSchedulerData(int socketNumber, InetAddress ipAddress) {
        currentFloor = 1;
        numberOfPassengers = 0;
        upwards = true;
        this.socketNumber = socketNumber;
        this.ipAddress = ipAddress;
        capacity = 5;
    }

    ElevatorSchedulerData(ElevatorInfo info, int socketNumber, InetAddress ipAddress) {
        this.update(info);
        this.socketNumber = socketNumber;
        this.ipAddress = ipAddress;
        this.capacity = 5;
    }

    public void update(ElevatorInfo info) {
        this.currentFloor = info.getFloor();
        this.upwards = info.goingUpwards();
        this.numberOfPassengers = info.getNumberOfPassengers();
    }
    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return numberOfPassengers >= capacity;
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

    public void setNumberOfPassengers(int number) throws IllegalArgumentException {
        if (number < 0 || number > capacity) {
            throw new IllegalArgumentException("Number of passengers must be 0 or more and less than capacity");
        }
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
