package unsw.venues;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Venue {
	
	private String name;
	private List<Room> rooms;
	
	/** 
	 * @param n, a string used to represent the name of the venue. 
	*/
	public Venue(String n) {
		this.setName(n);
		this.setRooms(new ArrayList<Room>());
	}
/**
 * @return A string representing the rooms name. 
 */
	public String getName() {
		return name;
	}
	/**
	 * @param name, a string representing the rooms name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return A list of all rooms pertaining to this venue.
	 */
	public List<Room> getRooms() {
		return rooms;
	}
	
	/**
	 * @param rooms, a list of rooms in the venue.
	 */
	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}
	
	/**
	 * @return An integer representing the number of rooms in the venue.
	 */
	public int getNumberRooms() {
		return rooms.size();
	}
	
	/**
	 * @param r, a variable of type class room to be added to the list of rooms in the venue.
	 */
	public void addRoom(String roomName, String roomSize) {
    	Room r = new Room(roomName, roomSize);
		rooms.add(r);
	}
	
	/** 
	 * @param numRoomsOfThisSizeNeeded, a integer representing the number of desired rooms.
	 * @param RoomSize, a string representing the room size desired.
	 * @param startDate, an immutable date time-object used to represent the year-month-day of a room reservation start date.
	 * @param endDate, an immutable date time-object used to represent the year-month-day of a room reservation end date.
	 * @param addAvailableRooms, a list of type class rooms.
	 * 
	 * @return numRooms, the total number of available rooms that meet the booking requirement.
	 * 
	*/ 	
	public int getNumberAvailableRooms(int numRoomsOfThisSizeNeeded, String RoomSize, LocalDate startDate, LocalDate endDate, ArrayList<Room> addAvailableRooms) {
		// Check if the room is equal to the room the guest actually wants, then check to see if the room is available.
        // If the room size matches, and its not reserved, we increment the numRooms counter (which we return)
		// to later verify whether there are enough available rooms, of desired size, in the hotel with the booking request.
		int numRooms = 0;
		for(Room r : this.getRooms()) {
			
			if((numRoomsOfThisSizeNeeded != 0) && RoomSize.equals(r.getRoomSize()) && r.checkReservation(startDate, endDate)) {
				addAvailableRooms.add(r);
				numRooms++;
				numRoomsOfThisSizeNeeded--;
			}
		}
		return numRooms;
	}
	
	/** 
	 * @param RoomsToReserve, a list of rooms to be reserved.
	 * @param id, a string used to identify the room.
	 * @param start, an immutable date time-object used to represent the year-month-day of a room reservation start date.
	 * @param end, an immutable date time-object used to represent the year-month-day of a room reservation end date.  
	*/	
	public void makeReservation(ArrayList<Room> RoomsToReserve, String id, LocalDate start, LocalDate end) {
		// Make a call to the room method where we pass in the id and start and end date to create a reservation. 
		// We then make another call to a method in room where we use bubble sort to sort the order of the reservation array by date.
		for(Room r : RoomsToReserve) {
			r.createReservation(id, start, end);
			r.orderReservation(start, end);
		}
	}

	/** 
	 * @param id, a string used to identify a reservation to be cancelled.
	*/
	public void cancelVenueReservation(String id) {
		for(Room r : rooms) {
			r.cancelRoomReservation(id);
		}
	}

	/** 
	 * @param listResultArray, a JSONArray used to store room names to be printed out.
	*/	
	public void roomListToPrint(JSONArray listResultArray) {
		for(Room r : rooms) {
			JSONObject roomJSON = new JSONObject();
			roomJSON.put("room", r.getRoomName());		
			
			JSONArray resJSON = r.resListToPrint();		
			roomJSON.put("reservations", resJSON);   
			listResultArray.put(roomJSON);
			
		}
	}
	
	/** 
	 * @param tempRooms, a list of rooms to be saved in case the change booking request fails.
	 * @param id, a string used to identify the reservation.
	 * @param foundMatch, used to determine whether we were successfully able to find the reservation.
	 * @param tempDates, an array used to preserve the dates of an existing booking through function calls, to be re-used if change booking request fails.
	 * 
	 * @return a boolean value to determine whether the save reservation was a success or failure, True for success, False otherwise 
	*/	
	public boolean saveVenueReservation(ArrayList<Room> tempRooms, String id, boolean foundMatch, ArrayList<LocalDate> tempDates) {
		for(Room r : rooms) {
			foundMatch = r.saveRoomReservation(r, tempRooms, id, foundMatch, tempDates);
		}
 
		return foundMatch;
	}
	
	/** 
	 * @param id, a string used to identify the reservation to be added to a JSONArray. 
	 * @param roomsJSON, a JSONArray used to save room names to be printed
	*/		
	public void roomToPrint(String id, JSONArray roomsJSON) {
		for(Room r : rooms) {
			r.resToPrint(id, roomsJSON);					
		}
	}
	
	
}