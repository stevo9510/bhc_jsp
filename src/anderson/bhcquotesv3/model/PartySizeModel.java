/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 10 - PartySizeModel 
 * PartySizeModel.java - Model object used to encapsulate information about a PartySize request for rendering html in View. 
 * Could be used for invalid party size requests. 
 * 
 * 07/27/2018 - Initial
 */

package anderson.bhcquotesv3.model;

import anderson.bhcquotesv3.core.Constants;

public class PartySizeModel {

	private int actualSize;
	
	public int getMinSize() {
		return Constants.MIN_PARTY_SIZE;
	}
	
	public int getMaxSize() {
		return Constants.MAX_PARTY_SIZE;
	}
	
	public int getActualSize() {
		return actualSize;
	}
		
	public PartySizeModel(int actualSize) {
		this.actualSize = actualSize;
	}
	
	public boolean isValidSize() {
		return this.getActualSize() >= this.getMinSize() && this.getActualSize() <= this.getMaxSize(); 
	}
	
}
