public class Request {
    private final int startingFloor;
    private final int destinationFloor;
    private boolean finished;

    Request(int startingFloor, int destinationFloor) {
        finished = false;
        this.startingFloor = startingFloor;
        this.destinationFloor = destinationFloor;
    }

    public int getStartingFloor() {
        return startingFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public void complete() {
        this.finished = true;
    }

    //Will need a method to convert to string for UDP packets
}
