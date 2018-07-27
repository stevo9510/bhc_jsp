/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 6 - BHC Hike Quotes
 * RatesService.java - Defines a common interface for different rates services to implement so that they can be swapped out seamlessly.
 * 07/02/2018
 */

package anderson.bhcquotesv3;

import java.util.Date;

import com.rbevans.bookingrate.Rates;

/** 
 * @author sande107
 */
public interface RatesService {

	/**
	 * Entry way into the service, that is expected to populate fields so that other getter methods of this service can be fetched (e.g. getCost(), getDetails() );
	 * @param hikeType
	 * @param year
	 * @param month
	 * @param day
	 * @param duration
	 */
	void requestQuoteDetails(Rates.HIKE hikeType, int year, int month, int day, int duration);

	/** 
	 * Get the cost after a quote request is made.  It is expected that isValidCost() at the minimum is called before retrieving this value, otherwise
	 * an unexpected value or default value could be returned.   
	 * @return
	 */
	double getCost();

	/** 
	 * Get the details of the quote request.  Will contain failure information if the request could not be processed.  
	 * @return
	 */
	String getDetails();

	/** 
	 * Get the beginning date of the hike request.  Expected to return null if the begin date submitted for the request is invalid.
	 * @return
	 */
	Date getBeginDate();
	
	/** 
	 * Get the end date of the hike request.  Expected to return null if the begin date submitted for the request is invalid.
	 * @return
	 */
	Date getEndDate();
	
	/** 
	 * Gets whether the requested quote cost is valid.  This should be called before trying to actually retrieve the cost via getCost() to ensure validity.
	 * @return
	 */
	boolean isValidCost();

	/** 
	 * Gets whether the requested date is an invalid (non-real) date altogether (e.g. February 30th).  This is here to help display more detailed messages to client users.  
	 * @return
	 */
	boolean isInvalidDate();

	/** 
	 * Gets whether the requested date is invalid with the sole reason being that it is not a seasonal date  (in other words, the date is a real/valid date, 
	 * but just not in season). This is here to help display more detailed messages to client users.  
	 * @return
	 */
	boolean isInvalidSeasonalDates();

}