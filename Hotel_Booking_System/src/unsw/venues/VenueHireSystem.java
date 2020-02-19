/**
 *
 */
package unsw.venues;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Venue Hire System for COMP2511.
 *
 *Student Name: Oren Levy
 *Student ID: z3466301
 *COMP2511 VENUE HIRE SYSTEM ASSIGNMENT 1
 *
 * A basic prototype to serve as the "back-end" of a venue hire system. Input
 * and output is in JSON format.
 *
 * @author Robert Clifton-Everest
 *
 */
public class VenueHireSystem {

    /**
     * Constructs a venue hire system. 
     * 
     */
	
	private List<Venue> venues;	
	
    public VenueHireSystem() {
        this.venues = new ArrayList<Venue>();
    }
    
/** 
 * @param Json Object, converted into a string so we may read in a command and process the corresponding instruction appropriately.
 * 
*/ 
    private void processCommand(JSONObject json) {
        switch (json.getString("command")) {
        /** 
         * @case room, used to catch a command to create a room in a particular venue, of a particular size and with a unique name.
        */   
        case "room":  
            String venue = json.getString("venue");
            String roomName = json.getString("room");
            String roomSize = json.getString("size");
           
            // Add a room to a venue.
            addRoom(venue, roomName, roomSize);
            break;
            /** 
             * @case request, used to catch a command to create a reservation.
            */  
        case "request":
            String id = json.getString("id");
            LocalDate start = LocalDate.parse(json.getString("start"));
            LocalDate end = LocalDate.parse(json.getString("end"));
            int small = json.getInt("small");
            int medium = json.getInt("medium");
            int large = json.getInt("large");
           
            // Call the request function in an attempt to make a reservation.        

            JSONObject result = request(id, start, end, small, medium, large);
            System.out.println(result.toString());           
            break;
            /** 
             * @case change, used to catch a command to change an existing reservation.
            */  
        case "change":	
        	String changeId = json.getString("id");
            LocalDate changeStart = LocalDate.parse(json.getString("start"));
            LocalDate changeEnd = LocalDate.parse(json.getString("end"));
            int changeSmall = json.getInt("small");
            int changeMedium = json.getInt("medium");
            int changeLarge = json.getInt("large");

            // Create temp variables to store the old booking, before we cancel. If fail, re-add old booking.
     	    ArrayList<Room> tempRooms = new ArrayList<Room>();
     	    ArrayList<LocalDate> tempDates = new ArrayList<LocalDate>();
     	    LocalDate tempStart = null; 
     	    LocalDate tempEnd = null;
     	    
     	    // Copy the old booking using the ID of the change request, then destroy the original booking. 
     	    Venue v = saveOldResDetails(tempRooms, changeId, tempDates);
     	    tempStart = tempDates.get(0);
     	    tempEnd = tempDates.get(1);


     	    assert(v!=null);
     	    
     	    // Destroy the existing reservation.
            cancelReservation(changeId);
            
            // Attempt to add the 'change' booking as a new booking.
            JSONObject attemptChangeRes = request(changeId, changeStart, changeEnd, changeSmall, changeMedium, changeLarge);
        	
            // If length is less than 3 then the change request was rejected, reAdd the old booking.
            if(attemptChangeRes.length() < 3) {

            	v.makeReservation(tempRooms, changeId, tempStart, tempEnd); 
            }       
            tempRooms.clear();
            System.out.println(attemptChangeRes.toString(2));
            break;
            /** 
             * @case cancel, used to catch a command to cancel an existing reservation.
            */  
        case "cancel":
        	//JSONObject cancelResult = new JSONObject();
        	String cancelResId = json.getString("id");
        	cancelReservation(cancelResId);   	
        	break;
            /** 
             * @case list, used to catch a command to print all the reservations pertaining to a venue and its rooms.
            */  
        case "list":

        	String venueName = json.getString("venue");      	
        	JSONArray printRes = printReservations(venueName);
            System.out.println(printRes.toString(2));
        	
            break;

        }
    }
    
	/** 
	 * @param venue, a string used to represent the name of a new or existing venue. 
	 * @param roomName, a string used to represent the name of a new room.
	 * @param roomSize, a string used to represent the size of a room.
	*/   
  public void addRoom(String venue, String roomName, String roomSize) {
    	Venue v;
        
    	// If venue != null, create and append room to the existing venues list of rooms. 
    	if((v = (checkVenueExists(venue))) != null) {
        	v.addRoom(roomName, roomSize);
        
        // If venue does not exist, create a new venue, new room, and add the new room to the venues list of rooms.
    	} else {
    		v = new Venue(venue);
        	v.addRoom(roomName, roomSize);
    		this.venues.add(v);
    	}
    }
  
	/** 
	 * @param venueName, a string used to represent the name of a venue to determine its existance. 
	 * @return venue, returns a type class venue if it exists, null otherwise.
	*/     
  public Venue checkVenueExists(String venueName) {
   	 for(Venue v : this.venues) {
  		 if(v.getName().equals(venueName)) {
  			 return v;
  		 }
  	 }
   	 // No venue with same name exists.
  	 return null;	
   }
    
	/** 
	 * @param id, a string used to represent a reservation number. 
	 * @param startDate, an immutable date time-object, used to represent the year-month-day of a desired room reservation start date.
	 * @param endDate, an immutable date time-object, used to represent the year-month-day of a desired room reservation end date.
	 * @param small, an integer used to represent the number of small size rooms guests want to book.
	 * @param medium, an integer used to represent the number of medium size rooms guests want to book.
	 * @param large, an integer used to represent the number of large size rooms we guests to book.
	 * 
	 * @return jasn formated success if we found and removed the reservation, otherwise
	 * return a jasn formated rejected if we did not find a match.
	*/
   public JSONObject request(String id, LocalDate start, LocalDate end, int small, int medium, int large) {
       JSONObject result = new JSONObject();
       boolean success = false;
       
       // Total number of rooms to book.
 	   int bookingSize = small+medium+large;

 	   // Iterate through all venues, in an attempt to find venue that can meet booking requirements.
 	   for(Venue v : this.venues) {
 		   // Check if the venue has enough rooms in general.
 		   if(bookingSize <= v.getNumberRooms()) {
 			   // If venue has enough rooms, attempt to reserve the rooms. Return true if reservation was a success false otherwise. 			   
 			   success = (attemptReservation(v, id, start, end, small, medium, large));	 			   			  
			   // If successful, begin adding the appropriate information to be displayed as a message.
 			   if(success == true) {
 				   JSONArray rooms = new JSONArray();
 				   // Constant key/value relationship.
 				   result.put("status", "success"); 				   
 				   // Add the venue name we successfully added the reservation to.
 				   // V will be pointing to the correct venue at this point.
 		 		   result.put("venue", v.getName());
 		 		   
 		 		   // Loop through each room in the venue and their bookings, comparing the reservation id
 		 		   // to determine which rooms belong to this reservation.
 		 		   v.roomToPrint(id, rooms);		 		   
 		 		   result.put("rooms", rooms); 		 
 		 		   break;
 			   } 
 		   } 		   
 	   }
 	   
 	  // There was no venue that could support the desires reservation, return false.
 	  if(success == false) {
 		 result.put("status", "rejected");
	  }
 	  
	  return result;   
    }
   
	/** 
	 * @param v, a variable type class Venue used to represent a a venue we are attempting to book rooms in. 
	 * @param id, a string used to represent a reservation number. 
	 * @param startDate, an immutable date time-object, used to represent the year-month-day of a desired room reservation start date.
	 * @param endDate, an immutable date time-object, used to represent the year-month-day of a desired room reservation end date.
	 * @param small, an integer used to represent the number of small size rooms guests want to book.
	 * @param medium, an integer used to represent the number of medium size rooms guests want to book.
	 * @param large, an integer used to represent the number of large size rooms we guests to book.
	 * 
	 * @return boolean, true if reservation was successful, false otherwise.
	*/ 
   private boolean attemptReservation(Venue v, String id, LocalDate start, LocalDate end, int small, int medium, int large) {
	   // Strings used to loop through the list of Venue room sizes and return a count of the number of matching sized rooms.
	   String s = "small"; 
	   String m = "medium";
	   String l = "large";
	   
	   ArrayList<Room> addAvailableRooms = new ArrayList<Room>();
	   
	   // Determine if there are enough small, medium and large rooms available in the venue to meet the booking requirements.
	   // Only once each case has been passed do we create a reservation.
	   if(small <= (v.getNumberAvailableRooms(small, s, start, end, addAvailableRooms)) && 
		  medium <= (v.getNumberAvailableRooms(medium, m, start, end, addAvailableRooms)) && 
		  large <= (v.getNumberAvailableRooms(large, l, start, end, addAvailableRooms))) {
		   
		   // If we make it here, then there are both enough rooms of desired size
		   // AND the rooms are not reserved, so we create and add a new reservation. 		   		   
		   v.makeReservation(addAvailableRooms, id, start, end);
		   addAvailableRooms.clear();
		   return true;
	   }   	   
	   addAvailableRooms.clear();
	   return false;	   
   }
   
	/** 
	 * @param tempRooms, a list of all rooms in the venue to be copied. 
	 * @param id, a string used to represent a reservation number. 
	 * @param tempDates, an array of dates pertaining to a room reservation, used to preserve the values through function calls and later use if new booking request fails.
	 * 
	 * @return venue, a type class venue pointer to be saved for potential later use.
	*/  
   public Venue saveOldResDetails(ArrayList<Room> tempRooms, String id, ArrayList<LocalDate> tempDates) {
	   //Iterate through all venues in the system.
	   boolean foundMatch = false;
	   Venue v = null;
	   for(Venue venue : venues) {
		   foundMatch = venue.saveVenueReservation(tempRooms, id, foundMatch, tempDates);
		   if(foundMatch == true) {
			   return venue;
		   }
	   }
	   return v;
   }
   
	/** 
	 * @param id, a string used to represent a reservation number to be cancelled. 
	*/ 
   public void cancelReservation(String id) {
       // Here we cancel the reservation by looping through all venues, and subsequently the rooms,
       // until we find an id match.
	   for(Venue v : venues) {
		   v.cancelVenueReservation(id);
	   }
   }
   
	/** 
	 * @param venueName, a string used to represent a the name of a venue wish to be printed.
	 * @return json array list to be printed.
	*/ 
   public JSONArray printReservations(String venueName) {

	   // Create JSON objects and arrays.
	   JSONArray listResultArray = new JSONArray();
	   // Iterate through all venues in the system until we find the venue requested to be printed.
	   for(Venue v : this.venues) {
		   if(venueName.equals(v.getName())) {
			   v.roomListToPrint(listResultArray);
		   }
	   }
	   // return the amended arrayList to be printed.
	   return listResultArray;
	   
   }
   
	/** 
	 * Main function used to pass in the lines of Json objects to make bookings
	*/ 
    public static void main(String[] args) {
        VenueHireSystem system = new VenueHireSystem();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.trim().equals("")) {
                JSONObject command = new JSONObject(line);
                system.processCommand(command);
            }
        }
        sc.close();
    }

}