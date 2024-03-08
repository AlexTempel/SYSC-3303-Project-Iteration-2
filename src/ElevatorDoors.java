public class ElevatorDoors {
    private boolean isOpen;

    public ElevatorDoors(){
        this.isOpen = false;
    }

    public void cycleDoors() {
        try {
            // Open
            System.out.println("Elevator doors are opening...");
            isOpen = true;
            Thread.sleep(1500);

            // Close
            System.out.println("Elevator doors are closing...");
            isOpen = false;
            Thread.sleep(1500);

        } catch (InterruptedException e) {
        }
    }
    /**
     * Toggle the open or close action of the doors
     */
    public void toggleDoors(){
        String action;
        if(isOpen){
            action = "closing";
        }else{
            action = "opening";
        }
        System.out.printf("Elevator doors are %s...", action);

        // Wait for the doors to close
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) { }
    }
}
