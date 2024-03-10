import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.time.LocalTime;

public class FloorSubsystem implements Runnable {

    private  ArrayList<Floor> listOfFloors;
    private  ArrayList<TimedRequest> listOfRequests;
    private final DatagramSocket floorSocket;
    private final int schedulerPort;
    private final InetAddress shedIpAddress;

    FloorSubsystem(int floorPort, int schedPort, int numberOfFloors, InetAddress shedIpAddress) throws SocketException {
        this.shedIpAddress = shedIpAddress;
        floorSocket = new DatagramSocket(floorPort);
        this.schedulerPort = schedPort;
        listOfFloors = new ArrayList<>();
        for (int i = 0; i < numberOfFloors; i++) {
            listOfFloors.add(new Floor(i+1));
        }
        listOfRequests = readCSV("Input.csv");

    }

    /**
     * Reads the input csv file and parses into our desired format for timed request calls
     * @param csvName input csv file
     * @return toReturn the list of timed requests in the desired format
     */
    public static ArrayList<TimedRequest> readCSV(String csvName) {
        ArrayList<TimedRequest> toReturn = new ArrayList<TimedRequest>();
        try {
            FileReader file = new FileReader(csvName);
            BufferedReader input = new BufferedReader(file);
            String line = input.readLine();
            int counterID = 0;

            while(line != null){
                counterID = counterID + 1;
                line = input.readLine();
                String[] values = line.split(" ");

                Request newRequest = new Request(counterID, Integer.parseInt(values[1]), Integer.parseInt(values[3]));

                TimedRequest newTimedRequest = new TimedRequest(LocalTime.parse(values[0]), newRequest);
                toReturn.add(newTimedRequest);
            }
            input.close();
        } catch(Exception e) { e.getStackTrace(); }
        return toReturn;
    }

    /**
     * Loops through the listOfRequests and checks if they are scheduled for the current time, if so remove the
     * request from the list and return the request
     * @return reqToSend the request from the input file that has the same time as the current time
     */
    public Request getCurrentRequest(){
        for (TimedRequest r : listOfRequests) {
            if (r.getTime().truncatedTo(ChronoUnit.MINUTES).compareTo(LocalTime.now().truncatedTo(ChronoUnit.MINUTES)) == 0) {
                listOfRequests.remove(r);
                Request reqToSend = r.getRequest();
                return reqToSend;
            }
        }
        try { //Wait half a second and check again
            Thread.sleep(500);
        } catch (Exception e) {}
        return getCurrentRequest();
    }

    /**
     * Provides the functionality for the Floor Subsystem
     * Gets the request at the current time, convert it to a message string, then into a datagram packet and sends it
     * to the scheduler.
     */
    public void sendToScheduler() throws IOException {
        Request temp_request = getCurrentRequest();
        String message = temp_request.convertToPacketMessage();
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.getBytes().length);

        floorSocket.connect(shedIpAddress, schedulerPort);
        floorSocket.send(sendPacket);
        floorSocket.disconnect();
    }

    public void run() {
        System.out.println("Floor Subsystem Starting");
        while (true) {
            try {
                sendToScheduler();
            } catch (IOException e) {
                continue;
            }
            System.out.println("Sent to Scheduler");
        }
    }
}
