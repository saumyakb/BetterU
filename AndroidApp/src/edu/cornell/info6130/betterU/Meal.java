package edu.cornell.info6130.betterU;

public class Meal {
	final String _key;
	final int _startHour;
	final int _endHour;
	
	public Meal(String key, int startHour, int endHour) {
		_key = key;
		_startHour = startHour;
		_endHour = endHour;
	}
	
	public String getKey() {
		return _key;
	}
	
	public int getStartHour() {
		return _startHour;
	}
	
	public int getEndHour() {
		return _endHour;
	}
}
