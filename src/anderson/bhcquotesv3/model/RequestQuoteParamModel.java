/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 7 - BHC Request Quote Servlet 
 * RequestQuoteParamObject.java - Used to help encapsulate pertinent information about request quote servlet request parameters
 * including the value, name, and error (if applicable)
 * 
 * 07/13/2018 - Initial
 * 07/19/2018 - Shell off some fields/methods to base class 
 * 07/27/2018 - Name change to Model.
 */

package anderson.bhcquotesv3.model;

/**
 * Generic for the parameter's actual data type value
 * @author sande107
 * @param <T>
 */
public class RequestQuoteParamModel<T> extends RequestQuoteParamBaseModel {

	private T value;

	public RequestQuoteParamModel(String name, T value, ParamErrorType errorType) {
		super(name, errorType);
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}

}
