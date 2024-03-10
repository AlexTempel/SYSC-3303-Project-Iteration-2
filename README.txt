SYSC 3303 Project Iteration 2 Group 8


Alex - Scheduler Communication

Peter - Scheduler Logic

Jake - Floor subsystem
     - CurrentRequest Test Instructions
        When testing, ensure to input your current time into the second row of data (the line after 17:30)
        in TestInput.csv, only put hours and minutes, leave the seconds as zeroes this test will only assert
        true if it is your PC's local time in the file

Kam - Elevator

Nemec - Static methods for UDP and Request parsing

Floor subsystem continually checks for a request then sends a UDP to scheduler

Scheduler gets Requests from Floor subsystem and determines which Elevator to send it to, waits for confirmation from Elevator

Elevator gets Requests and fulfills it and it send confirmation to Scheduler

ElevatorSubsystem.java:
This class is responsible for receiving and handling requests from the Scheduler.
The Elevator subsystem also controls all other components of the elevator, like the Elevator Doors.
Each instance of the Elevator subsystem has attributes that are unique to that specific Elevator.
For example, each elevator has a currentFloor attribute, along with a unique socket for Request communication.
The Elevator will receive a Request from the Scheduler, Move the elevator, and send confirmation back to the Scheduler once completed.

ElevatorDoors.java:
Each ElevatorSubsystem has an ElevatorDoors attribute. This class opens and closes the doors.

ElevatorSubsystemTest.java:
Send class for the Elevator subsystem.
Uses unit tests to verify the functionality of the send/receive Request, handle Request, and move elevator Methods.