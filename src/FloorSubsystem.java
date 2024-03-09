import java.io.BufferedReader;
import java.io.FileReader;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.time.LocalTime;

public class FloorSubsystem implements Runnable {

    private ArrayList<Floor> listOfFloors;
    private ArrayList<Request> listOfRequests;
    private Request[] currRequest;

    FloorSubsystem(int numberOfFloors, Request[] buffer) {
        listOfFloors = new ArrayList<>();
        for (int i = 0; i < numberOfFloors; i++) {
            listOfFloors.add(new Floor(i+1));
        }
        listOfRequests = readCSV("Input.csv");
        currRequest = buffer;
    }

    /**
     * Reads the input csv file and parses into our desired format for request calls
     * @param csvName input csv file
     * @return the list of requests in the desired format
     */
    public static ArrayList<Request> readCSV(String csvName) {
        //SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.S");
        ArrayList<Request> toReturn = new ArrayList<Request>();
        try {
            FileReader file = new FileReader(csvName);
            BufferedReader input = new BufferedReader(file);
            String line = input.readLine();

            while(line != null){
                line = input.readLine();
                String[] values = line.split(" ");
                Request newRequest = new Request(LocalTime.parse(values[0]),
                        Integer.parseInt(values[1]),
                        values[2],
                        Integer.parseInt(values[3]));
                toReturn.add(newRequest);
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
        for (Request r : listOfRequests) {
            if (r.getTime().truncatedTo(ChronoUnit.MINUTES).compareTo(LocalTime.now().truncatedTo(ChronoUnit.MINUTES)) == 0) {
                listOfRequests.remove(r);
                return r;
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
    private synchronized void fulfillBuffer() {
        if (currRequest[0] == null) {
            Request temp_request = getCurrentRequest();

            if (temp_request != null) {
                currRequest[0] = temp_request;
                System.out.println("Sent to Scheduler");
                System.out.println("Sent: " + temp_request.toString());
            }
        }
        notifyAll();
    }

    public void run() {
        System.out.println("Floor Subsystem Starting");
        while (true) {
            fulfillBuffer();
        }
    }
}
