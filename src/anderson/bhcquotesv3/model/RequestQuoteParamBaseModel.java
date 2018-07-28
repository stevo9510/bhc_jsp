/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 8 - BHC Request Quote Servlet 
 * RequestQuoteParamBaseModel.java - Used to help encapsulate pertinent (base) information about request quote servlet request parameters
 * including the value, name, and error (if applicable)
 * 
 * 07/19/2018 - Initial
 * 07/27/2018 - Name change to model.
 */

package anderson.bhcquotesv3.model;

/**
 * @author sande107
 *
 */
public class RequestQuoteParamBaseModel {
	public enum ParamErrorType { INCORRECT_TYPE, EMPTY_VALUE, NONE };

	private String name;
	private ParamErrorType errorType;

	public RequestQuoteParamBaseModel(String name, ParamErrorType errorType) {
		this.name = name;
		this.errorType = errorType;
	}
	
	public String getName() {
		return name;
	}

	public ParamErrorType getErrorType() {
		return errorType;
	}
}
