package edu.cornell.info6130.betterU;

public class Meal {
	final String _key;
	final int _startHour;
	final int _endHour;
	
	/**
	 * Initializes definition of a meal. 
	 * @param  key			 	unique string value correlating to a meal ("B" for breakfast, "L" for lunch, "D" for dinner).
	 * @param  startHour		hour of day that meal begins
	 * @param  endHour		 	hour of day that meal ends
	 * @return 					the initialized instance of this class (i.e., a meal)
	 */
	public Meal(String key, int startHour, int endHour) {
		_key = key;
		_startHour = startHour;
		_endHour = endHour;
	}
	
	/**
	 * string that serves as a unique key value for this meal definition. 
	 * @return 					the same key value used to initialize this class
	 */
	public String getKey() {
		return _key;
	}
	
	/**
	 * Hour of day this meal begins.
	 * @return 					the same start hour value used to initialize this class
	 */
	public int getStartHour() {
		return _startHour;
	}
	
	/**
	 * Hour of day this meal ends. 
	 * @return 					the same end hour value used to initialize this class
	 */
	public int getEndHour() {
		return _endHour;
	}
}
