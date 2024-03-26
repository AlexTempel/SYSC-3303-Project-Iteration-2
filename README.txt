SYSC 3303 Project Iteration 5 Group 8

Iteration 5 Stuff:
Peter: Write the report
Nemec: Write console in scheduler and make timing diagram
Kam: Update the elevators to take multiple requests and update Scheduler every time they move
Alex: Update the Scheduler to be able to send multiple requests
Jake: Create Elevator-Scheduler Data class and static methods

The new Elevator-Scheduler class should have:
Direction
Number of passengers
Current Floor
Broken State

Console output should look like:
+----------+---------------+----------------------+-----------------+
| Elevator | Current Floor | Number of Passengers | Alerts (Broken) |
+----------+---------------+----------------------+-----------------+
| 1        | 7             | 3                    | No Alerts       |
| 2        | 2             | 0                    | Doors Jammed    |
| 3        | 17            | 37                   | Broken          |
| 4        | 4             | 0                    | No Alerts       |
+----------+---------------+----------------------+-----------------+

Instructions to Run
If all on local computer. Run Main.java
If on separate computers. In Main.java update the IP addresses that are currently set to InetAddress.getLoopbackAddress() to the IP address of whatever machine they are running on.
And comment out all of the Thread.start() lines of subsystems and elevators you don't want to run.

State Diagrams: Alex and Jake
- For elevator add open/close state:
- Scheduler diagram
- Floor diagram

Class diagrams: Peter
- Update methods to reflect UDP communication logic

Sequence Diagram: Peter
- Add sequence diagram for Request Handling

FloorSubsystem: Nik Nemec
- Add state variable
- Add logic to determine if the request has an error (e.g. Floor does not exist)
- Print error message, do not send error Request to Scheduler

ElevatorSubsystem: Nick Kam
- Random chance for doors to jam
- ENUM state variable

SchedulerSubsystem: Alex
- Add state variable


Responsibilities
Alex - Scheduler Communication

Peter - Scheduler Logic

Jake - Floor subsystem

Kam - Elevator

Nemec - Static methods for UDP and Request parsing

Basic Flow of Program
Floor subsystem continually checks for a request then sends a UDP to scheduler

Scheduler gets Requests from Floor subsystem and determines which Elevator to send it to, waits for confirmation from Elevator

Elevator gets Requests and fulfills it and sends confirmation to Scheduler

FloorSubsystem.java
The floor subsystem is in charge of reading the input file and parsing the data into TimedRequest and Request objects,
checking through all the requests and looking if there is a request for the current time, if there is, then the floor
converts the request into a packet message then into a datagram packet to then be sent to the scheduler subsystem.

FloorSubsystemTest.java
CurrentRequest Test Instructions (in FloorSubsystemTest)
Before running the test, ensure to input your current time into the second row of data (the line after 17:30)
in TestInput.csv file. Only put hours and minutes, leave the seconds as zeroes. The tests will only run if you do this.

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

SchedulerSubsystem.java:
The Scheduler receives requests from a socket. If the request is not marked complete, it adds it to the list of outstanding requests and pending requests.
The list of pending requests are then checked if there is a free elevator and which one is the closest to the starting floor.
The scheduler then sends this request to the determined elevator and the elevator is marked as in use, and the request is removed from the list of pending requests.
If the received request is marked complete, it is removed from the list of outstanding requests, and the elevator is marked as no longer in use and its location is updated to the destination floor of the request.

ElevatorSchedulerData.java
This is a data structure that contains the information the scheduler knows about the elevators.

RequestWrapper.java
This is a data structure that contains both a request and elevator. It is only used by the scheduler to bundle data from packets from the elevators together.

SchedulerSubsystemTest.java
Collection of tests for SchedulerSubsystem

Request.java
Data structure for the information about requests that are sent between components. It is designed to be converted in packets.
convertToPacketMessage() converts the object to a format for a packet.
parsePacket() converts a packet containing a message created by convertToPacketMessage() into a Request object.
parseString() converts a message created by convertToPacketMessage() into a Request object.