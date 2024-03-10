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
