package unsw.venues;

import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Room {
	
	private String size;
	private String name;
	private ArrayList<Reservation> reservations;

	/** 
	 * @param name, a string used to represent the name of the room. 
	 * @param size, a string used to represent the size of the room. 
	*/
	public Room(String name, String size) {
		this.setRoomName(name);
		this.setRoomSize(size);
		this.reservations = new ArrayList<Reservation>();
	}
	/** 
	 * @return A string used to represent the size of the room. 
	*/
	public String getRoomSize() {
		return size;
	}
	
	/** 
	 * @param size, a string used to represent the size of the room. 
	*/
	public void setRoomSize(String size) {
		this.size = size;
	}
	/** 
	 * @return A string used to represent the name of the room. 
	*/
	public String getRoomName() {
		return name;
	}
	
	/** 
	 * @param name, a string used to represent that name of the room. 
	*/
	public void setRoomName(String name) {
		this.name = name;
	}	
	
	/** 
	 * @param r, of type class reservation used to add this room to the reservation list. 
	*/
	public void addReservation(Reservation r) {
		this.reservations.add(r);
	}
	
	/** 
	 * @return A list of type Reservation, represting all reservations pertaining to this room. 
	*/
	public ArrayList<Reservation> getReservation() {
		return reservations;
	}
	
	/** 
	 * @return An integer used to represent the total number of reservations pertaining to this room. 
	*/	
	public int getNumReservations() {
		return reservations.size();
	}
	
	/** 
	 * @param id, a unique String used to represent the reservations. 
	 * @param startDate, an immutable date time-object used to represent the year-month-day of a room reservation start date.
	 * @param endDate, an immutable date time-object used to represent the year-month-day of a room reservation end date.
	*/
	public void createReservation(String id, LocalDate start, LocalDate end) {
		// Create a new reservation and append to the end of the array reservation list
		Reservation res = new Reservation(id, start, end);
		this.addReservation(res);
	}
	
	/** 
	 * @param startDate, an immutable date time-object used to represent the year-month-day of a room reservation start date.
	 * @param endDate, an immutable date time-object used to represent the year-month-day of a room reservation end date.
	*/	
	public void orderReservation(LocalDate start, LocalDate end) {
		// Bubble sort used to order the Array of reservations
		ArrayList<Reservation> reservations = this.reservations;
		Reservation res = null;
		for (int i=0;i <reservations.size()-1; i++) {
			for (int j=0; j < reservations.size()-1-i;i++) {
				if (reservations.get(j).getStart().isAfter(reservations.get(j+1).getStart())) {
					res = reservations.get(j);
					reservations.set(j,reservations.get(j+1));
					reservations.set(j+1,res);
				}
			}
		}				
	}	

	/** 
	 * @param id, type class Reservation representing the reservation to be removed from reservation list.
	*/	
	public void cancelRoomReservation(String id) {
		for(Reservation res : reservations) {
			if(res.getId().equals(id)) {
				reservations.remove(res);
				break;
			}
		}
	}
	
	 /**
	  * @param startDate, an immutable date time-object used to represent the year-month-day of a room reservation start date.
	  * @param endDate, an immutable date time-object used to represent the year-month-day of a room reservation end date.
	*/
	public boolean checkReservation(LocalDate startDate, LocalDate endDate) {
		// Get the total number of reservations pertaining to this room. 
		int numReservations = getNumReservations();
		
		// If numReservations is equal to zero then the room has no bookings, return true indicating room is available.		
		if(numReservations == 0) {
			return true;
		}
		// Look at the first reservation associated with this room and check whether the new dates conflict with the existing booking dates, 
		// if not, we subtract 1 from the overall number of reservations. 
		// We continue this process until the end of the list, if number of bookings are equal to zero, 
		// then there existed no conflicts and we can regard this room as available.
		for(Reservation res : this.getReservation()) { 	
			if(res.checkAvailability(startDate, endDate)) {
				numReservations--;			
				if(numReservations == 0) {
					return true;
				}
			}
		}
		return false;
	}
	

	/** 
	 * @return resJSON, a JSONArray of a list of reservations pertaining to this room.
	*/	
	public JSONArray resListToPrint() {
		JSONArray resJSON = new JSONArray();

		for(Reservation res : reservations) {
			JSONObject reservationJSON = new JSONObject();
			   
			reservationJSON.put("id", res.getId());
			reservationJSON.put("start", res.getStart());
			reservationJSON.put("end", res.getEnd());
			   
			resJSON.put(reservationJSON);
		}
		return resJSON;

	}
	
	/** 
	 * @param room, a list of rooms to be saved in case the change booking request fails.
	 * @param id, a string used to identify the reservation.
	 * @param foundMatch, used to determine whether we were successfully able to find the reservation.
	 * @param tempDates, an array used to preserve the dates of an existing booking through function calls, to be re-used if change booking request fails.
	 * 
	 * @return a boolean value to determine whether the save reservation was a success or failure, True for success, False otherwise 
	*/	
	public boolean saveRoomReservation(Room room, ArrayList<Room> tempRooms, String id, boolean foundMatch, ArrayList<LocalDate> tempDates) {
		for(Reservation res : getReservation()) { 	
			if(id.equals(res.getId())){
				//found a match, cancel reservation by removing it from the reservation array.
				tempDates.add(res.getStart());
				tempDates.add(res.getEnd());
				tempRooms.add(room);
				foundMatch = true;
				break;
			} 
		}
		
		return foundMatch;
		
	}
	
	
	/** 
	 * @param id, a string used to identify the reservation to be added to a JSONArray. 
	 * @param roomsJSON, a JSONArray used to save reservation names to be printed
	*/	
	public void resToPrint(String id, JSONArray roomsJSON) {
		for(Reservation res : reservations) {
			   if(id.equals(res.getId()))
				   roomsJSON.put(getRoomName());
		}
	}
	
	
}
