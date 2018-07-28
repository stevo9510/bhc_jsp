/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 6 - BHC Hike Quotes
 * BhcLocalRatesService.java - Service used for calculating tour costs locally (on client machine) via 
 * usage of com.rbevans.bookingrate.BookingDay and com.rbevans.bookingrate.Rates classes.  Extends RatesService interface.
 * 
 * 07/02/2018
 */

package anderson.bhcquotesv3.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.rbevans.bookingrate.BookingDay;
import com.rbevans.bookingrate.Rates;
import com.rbevans.bookingrate.Rates.HIKE;

/** 
 * @author sande107
 */
public class BhcLocalRatesService implements RatesService {
	
	// Helper objects
	private BookingDay beginBookingDate;
	private Rates quoteHelper;
	
	// Fields exposed to consumers of this interface
	private String details;
	private double cost;
	private Date beginDate;
	private Date endDate;
	
	private static final String INVALID_SEASONAL_DATE_MSG = "begin or end date was out of season";
	
	public BhcLocalRatesService() {
		setDefaultValues();
	}
	
	private void setDefaultValues() {
		cost = -0.01;
		details = null;
		beginDate = null;
		endDate = null;
	}
	
	/**
	 * Request quote details client-side using the BookingDay and Rates classes.  
	 */
	@Override
	public void requestQuoteDetails(HIKE hikeType, int year, int month, int day, int duration) {
		setDefaultValues();
		
		beginBookingDate = new BookingDay(year, month, day);
		quoteHelper = new Rates(hikeType);
		quoteHelper.setBeginDate(beginBookingDate);
		quoteHelper.setDuration(duration);
		
		// get the cost from the quote helper, even if it is invalid
		cost = quoteHelper.getCost();
		details = quoteHelper.getDetails();
		
		// set and cache the dates if the start booking date was valid
		if(beginBookingDate.isValidDate()) {
			beginDate = beginBookingDate.getDate().getTime();
			GregorianCalendar beginBookingDateCopy = beginBookingDate.getDate();
			beginBookingDateCopy.add(Calendar.DAY_OF_MONTH, duration - 1);
			endDate = beginBookingDateCopy.getTime();
		}
	}

	@Override
	public double getCost() {
		return cost;
	}

	@Override
	public String getDetails() {
		return details;
	}

	/** 
	 * In this case, assume a valid cost is anything >= 0
	 */
	@Override
	public boolean isValidCost() {
		return getCost() >= 0;
	}

	/** 
	 * If the date is invalid/not-real (via bookingDate.isValidDate()), return true. Otherwise, false.
	 */
	@Override
	public boolean isInvalidDate() {
		return beginBookingDate != null && !beginBookingDate.isValidDate();
	}

	/**
	 * If the date is invalid because the details say that it is out of season.
	 */
	@Override
	public boolean isInvalidSeasonalDates() {
		return quoteHelper != null && quoteHelper.getDetails().contains(INVALID_SEASONAL_DATE_MSG);
	}

	@Override
	public Date getBeginDate() {
		return beginDate; 
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

}
