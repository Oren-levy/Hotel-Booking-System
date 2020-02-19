package unsw.venues;

import java.time.LocalDate;
import java.util.ArrayList;

public class Reservation {
	private String id;
	private LocalDate start;
	private LocalDate end; 
	
	/** 
	 * @param id, unique string representing a reservation. 
	 * @param start, an immutable date time-object used to represent the year-month-day of a room reservation start date.
	 * @param end, an immutable date time-object used to represent the year-month-day of a room reservation end date.  
	*/
	public Reservation(String id, LocalDate start, LocalDate end) {
		this.id = id;
		this.start = start;
		this.end = end;
	}
	
	/** 
	 * @return id, unique string representing a reservation. 
	*/
	public String getId() {
		return id;
	}
	
	/** 
	 * @param id, unique string representing a reservation. 
	*/
	public void setId(String id) {
		this.id = id;
	}
	
	/** 
	 * @return start an immutable date time-object representing the year-month-day of a room reservation start date.
	*/
	public LocalDate getStart() {
		return start;
	}
	
	/** 
	 * @param start, an immutable date time-object used to represent the year-month-day of a room reservation start date.
	*/
	public void setStart(LocalDate start) {
		this.start = start;
	}
	
	/** 
	 * @return end an immutable date time-object representing the year-month-day of a room reservation end date.
	*/
	public LocalDate getEnd() {
		return end;
	}
	
	/** 
	 * @param end, an immutable date time-object used to represent the year-month-day of a room reservation end date.
	*/
	public void setEnd(LocalDate end) {
		this.end = end;
	}
	
	/** 
	 * @param start, an immutable date time-object used to represent the year-month-day of a room reservation start date.
	 * @param end, an immutable date time-object used to represent the year-month-day of a room reservation end date.
	*/
	public boolean checkAvailability(LocalDate startDate, LocalDate endDate) {
		// Check if the start date of a potential booking begins after the date of an existing booking.

		if(startDate.isAfter(end) || endDate.isBefore(start)) {
			return true;
		}		
		return false;
	}
	
}