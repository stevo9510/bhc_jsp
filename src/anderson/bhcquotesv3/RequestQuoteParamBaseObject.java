/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 8 - BHC Request Quote Servlet 
 * RequestQuoteParamBaseObject.java - Used to help encapsulate pertinent (base) information about request quote servlet request parameters
 * including the value, name, and error (if applicable)
 * 
 * 07/19/2018 - Initial
 */

package anderson.bhcquotesv3;

/**
 * @author sande107
 *
 */
public class RequestQuoteParamBaseObject {
	public enum ParamErrorType { INCORRECT_TYPE, EMPTY_VALUE, NONE };

	private String name;
	private ParamErrorType errorType;

	public RequestQuoteParamBaseObject(String name, ParamErrorType errorType) {
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
