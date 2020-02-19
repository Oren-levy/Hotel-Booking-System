# Hotel-Booking-System

# Prototype system that could serve as the "back-end" of a venue hire system.

In this venue hire system, customers can make, change and cancel reservations. Each venue has a number of rooms of various sizes: rooms are either small, medium or large. A reservation request has a named identifier and is for one or more rooms of a certain size (e.g. two small rooms and one large room) for a period of time given by a start date and an end date. A reservation request is either granted in full or is completely rejected by the system (a request cannot be partially fulfilled).

The implementation inputs and outputs data in the JSON format. It will read from STDIN (System.in in Java) and output to STDOUT (System.out in Java). The input will be a series of JSON objects, each containing a single command (on its own line). After reading in a JSON object, the implementation will immediately process that command; i.e. it will not wait for all commands to be input before acting on them. 

# The commands are as follows:

Specify that venue venue has a room with name room of size size.
{ "command": "room", "venue": venue, "room": room, "size": size }


Request id is from startdate to endate for small number of small rooms, medium number of medium rooms, and large number of large rooms.
{ "command": "request", "id": id, "start": startdate, "end": enddate, "small": small, "medium": medium, "large": large }


Change existing reservation id to be from startdate to endate for small number of small rooms, medium number of medium rooms, and large number of large rooms.
{ "command": "change", "id": id, "start": startdate, "end": enddate, "small": small, "medium": medium, "large": large }


Cancel reservation id and free up rooms
{ "command": "cancel", "id": id }


List the occupancy of each room in the venue venue
{ "command": "list", "venue": venue }

# This is a concrete example input demonstrating the commands supported (comments are for explanation and should not appear in the actual input):

# Venue Zoo has the Penguin room which is small
{ "command": "room", "venue": "Zoo", "room": "Penguin", "size": "small" }

# Venue Zoo has the Hippo room which is large
{ "command": "room", "venue": "Zoo", "room": "Hippo", "size": "large" }

# Venue Zoo has the Elephant room which is large
{ "command": "room", "venue": "Zoo", "room": "Elephant", "size": "large" }

# Venue Gardens has the Figtree room which is large
{ "command": "room", "venue": "Gardens", "room": "Figtree", "size": "large" }

# Request 'Annual Meeting' is for 1 large and 1 small room from 2019-03-25 to 2019-03-26
# Assign Penguin and Hippo rooms of Zoo
{ "command": "request", "id": "Annual Meeting", "start": "2019-03-25", "end": "2019-03-26", "small": 1, "medium": 0, "large": 1 }

# Request 'Mattress Convention' is for 1 large room from 2019-03-24 to 2019-03-27
# Assign Elephant room of Zoo since Hippo room is occupied
{ "command": "request", "id": "Mattress Convention", "start": "2019-03-24", "end": "2019-03-27", "small": 0, "medium": 0, "large": 1 }

# Request 'Dance Party' is for 1 large room from 2019-03-26 to 2019-03-26
# Assign Figtree room of Gardens
{ "command": "request", "id": "Dance Party", "start": "2019-03-26", "end": "2019-03-26", "small": 0, "medium": 0, "large": 1 }

# Change reservation 'Annual Meeting' to 1 small room from 2019-03-27 to 2019-03-29
# Deassign Penguin and Hippo rooms of Zoo and assign Penguin room of Zoo
{ "command": "change", "id": "Annual Meeting", "start": "2019-03-27", "end": "2019-03-29", "small": 1, "medium": 0, "large": 0 }

# Request 'CSE Autumn Ball' is for 1 small room from 2019-03-25 to 2019-03-26
# Assign Penguin room of Zoo
{ "command": "request", "id": "CSE Autumn Ball", "start": "2019-03-25", "end": "2019-03-26" , "small": 1, "medium": 0, "large": 0 }

# Cancel reservation 'Dance Party'
# Deassign Figtree room of Gardens
{ "command": "cancel", "id": "Dance Party" }

# Request "Vivid" is for 1 small room from 2019-03-26 to 2019-03-26
# Request cannot be fulfilled
{ "command": "request", "id": "Vivid", "start": "2019-03-26", "end": "2019-03-26", "small": 1, "medium": 0, "large": 0 }

# Output a list of the occupancy for all rooms at Zoo, in order of room declarations, then date
{ "command": "list", "venue": "Zoo" }

# Of these commands, request, change, and list will produce output. The other commands do not. Inputting the above will yield the following (ordering of fields and indentation may differ):

{ "status": "success", "venue": "Zoo", "rooms": ["Penguin", "Hippo"] }
{ "status": "success", "venue": "Zoo" ,"rooms": ["Elephant"] }
{ "status": "success", "venue": "Gardens", "rooms": ["Figtree"] }
{ "status": "success", "venue": "Zoo", "rooms": ["Penguin"] }
{ "status": "success", "venue": "Zoo", "rooms": ["Penguin"] }
{ "status": "rejected" }
[ { "room": "Penguin", "reservations": [
    { "id": "CSE Autumn Ball", "start": "2019-03-25", "end": "2019-03-26" },
    { "id": "Annual Meeting", "start": "2019-03-27", "end": "2019-03-29" }
    ] },
  { "room": "Hippo", "reservations": [] },
  { "room": "Elephant", "reservations": [
    {"id": "Mattress Convention", "start": "2019-03-24", "end": "2019-03-27"}
    ] }
]

# Note that all commands produce a JSON object as output, except for list that produces a JSON array.

For commands that do not always succeed, the status field indicates whether the result was successful. If a reservation request or change cannot be fulfilled, the status will be rejected. In the case of such a rejection, no reservations is added, changed or deleted. You can assume change and cancel have identifiers for existing reservations, list has a valid venue, and room is not used to add a room that has already been added.
