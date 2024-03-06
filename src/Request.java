import java.net.DatagramPacket;

public class Request {
    private final int startingFloor;
    private final int destinationFloor;
    private boolean finished;

    Request(int startingFloor, int destinationFloor) throws IllegalArgumentException {
        finished = false;
        if (startingFloor == destinationFloor) {
            throw new IllegalArgumentException("starting floor can't be equal to destination floor");
        }
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

    public boolean isFinished() {
        return finished;
    }

    /**
     * Converts this Request into a string that can be put into a UDP packet
     * @return String that can be put into a UDP packet
     */
    public String convertToPacketMessage() {
        return ""; //Temporary body
    }

    /**
     * Returns a Request object from a UDP packet
     * @param packet the packet to be parsed into a Request
     * @return Request according to the data in the packet
     */
    public static Request parsePacket(DatagramPacket packet) {
        return new Request(1, 2); //Temporary body
    }

    /**
     * Returns a Request object from a string, following the proper formatting
     * @param message the string to be parsed into a Request
     * @return Request according to the data in the string
     */
    public static Request parseString(String message) {
        return new Request(1, 2); //Temporary body
    }
}
