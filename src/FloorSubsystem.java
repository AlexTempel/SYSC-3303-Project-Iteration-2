import java.io.BufferedReader;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.time.LocalTime;

public class FloorSubsystem implements Runnable {

    private ArrayList<Floor> listOfFloors;
    private ArrayList<TimedRequest> listOfRequests;



    FloorSubsystem(int numberOfFloors, Request[] buffer) {
        listOfFloors = new ArrayList<>();
        for (int i = 0; i < numberOfFloors; i++) {
            listOfFloors.add(new Floor(i+1));
        }
        listOfRequests = readCSV("Input.csv");

    }

    /**
     * Reads the input csv file and parses into our desired format for request calls
     * @param csvName input csv file
     * @return the list of requests in the desired format
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

                Request newRequest = new Request(counterID, Integer.parseInt(values[1]), Integer.parseInt(values[2]));

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
     * @return r the request from the input file that has the same time as the current time
     */
    private Request getCurrentRequest(){
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
     * Assigns the next current request to send to the scheduler and notifies when complete
     */
    private void sendToScheduler() {
        Request temp_request = getCurrentRequest();
        String message = temp_request.convertToPacketMessage();
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.getBytes().length);

        socket.connect(elevator.getIpAddress(), elevator.getSocketNumber());
        socket.send(sendPacket);
        socket.disconnect();

    }

    public void run() {
        System.out.println("Floor Subsystem Starting");
        while (true) {
            sendToScheduler();
            System.out.println("Sent to Scheduler");
        }
    }
}
