public class ElevatorDoors {
    private boolean isOpen;
    private boolean isJammed;

    public ElevatorDoors(){
        this.isOpen = false;
        this.isJammed = false;
    }

    public void cycleDoors() {
        try {

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

        int jamChance = (int) (Math.random() * 10);
        if (jamChance == 5) {
            isJammed = true;
        }

        if (isJammed){
            System.out.println("The Elevator doors are jammed!");
            System.out.println("Sending another open signal...");
            isJammed = false; // Reset the jammed state
        }

        String action;
        if(isOpen){
            action = "closing";
            isOpen = false;
        }else{
            action = "opening";
            isOpen = true;
        }
        System.out.printf("Elevator doors are %s...\n", action);

        // Wait for the doors to close
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) { }
    }
}
