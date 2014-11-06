package edu.cornell.info6130.betterU;

public enum FoodType {
	MEAL ("meal", 0)
	, SNACK ("snack", 1);
	
	private String stringValue;
	private int    intValue;
	
	private FoodType(String toString, int value) {
		stringValue = toString;
		intValue = value;
	}
	
	@Override
	public String toString() {
		return stringValue;
	}
}
