public class RequestWrapper { //Meant only for scheduler
    //Combines the request data and Elevator data
    private final Request request;
    private final ElevatorSchedulerData elevator;

    RequestWrapper(Request request, ElevatorSchedulerData elevator) {
        this.request = request;
        this.elevator = elevator;
    }

    public Request getRequest() {
        return request;
    }
    public ElevatorSchedulerData getElevator() {
        return elevator;
    }
}
