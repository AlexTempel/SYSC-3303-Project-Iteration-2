SYSC 3303 Project Iteration 2 Group 8


Alex - Scheduler Communication

Peter - Scheduler Logic

Jake - Floor subsystem
The floor subsystem is in charge of reading the input file and pasring the data into TimedRequest and Request objects,
checking through all the requests and looking if there is a request for the current ttime, if there is, then the floor
converts the request into a packet message then into a datagram packet to then be sent to the scheduler subsystem.

Testing
CurrentRequest Test Instructions
Before running the test, ensure to input your current time into the second row of data (the line after 17:30)
in TestInput.csv file. Only put hours and minutes, leave the seconds as zeroes. The tests will only run if you do this.

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