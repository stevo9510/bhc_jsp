/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 10 - Constants 
 * Constants.java - Class to hold some core Constants used across this Java project.
 * 
 * 07/27/2018 - Initial
 */

package anderson.bhcquotesv3.core;

public class Constants {
	// Note: These are based off BookingDay.java. Ideally these would be defined / shared between the two classes, 
	// but I did not want to modify the provided BookingDay class.
	public static final int VALID_START_YEAR = 2007; // Going to assume we're actually in the year 2007, and accept
														// quotes for anything pre-current date :)
	public static final int VALID_END_YEAR = 2020;

	public static final int MIN_PARTY_SIZE = 1;

	public static final int MAX_PARTY_SIZE = 10;
}
