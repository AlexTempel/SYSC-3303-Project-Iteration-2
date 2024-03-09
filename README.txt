SYSC 3303 Project Iteration 2 Group 8


Alex - Scheduler Communication

Peter - Scheduler Logic

Jake - Floor subsystem

Kam - Elevator

Nemec - Static methods for UDP and Request parsing

Floor subsystem continually checks for a request then sends a UDP to scheduler

Scheduler gets Requests from Floor subsystem and determines which Elevator to send it to, waits for confirmation from Elevator

Elevator gets Requests and fulfills it and it send confirmation to Scheduler
