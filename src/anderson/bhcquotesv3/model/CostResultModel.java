/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 10 - CostResultModel 
 * CostResultModel.java - Model object that encapsulates cost details of a successful quote request.   
 * 
 * 07/27/2018 - Initial
 */
package anderson.bhcquotesv3.model;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CostResultModel {
	private static final DecimalFormat costFormat = new DecimalFormat("#.00"); 
	private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private String hikeDisplayName;
	private Date startDate;
	private Date endDate;
	private int partySize;
	private double cost;
	private double totalCost;
	
	public CostResultModel(String hikeDisplayName, Date startDate, Date endDate, int partySize, double cost) {
		this.hikeDisplayName = hikeDisplayName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.partySize = partySize;
		this.cost = cost;
		this.totalCost = partySize * cost;
	}
	
	public CostResultModel() {
		
	}
	
	public String getHikeDisplayName() {
		return hikeDisplayName;
	}
	public String getStartDate() {
		return formatDate(startDate);
	}
	public String getEndDate() {
		return formatDate(endDate);
	}
	public int getPartySize() {
		return partySize;
	}
	public String getCost() {
		return formatCostAmount(cost);
	}
	public String getTotalCost() {
		return formatCostAmount(totalCost);
	}
	
	private static String formatCostAmount(double costAmount) {
		return "$" + costFormat.format(costAmount);
	}
	
	private static String formatDate(Date date) {
		return dateFormat.format(date);
	}
	
}
