/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 10 - QuoteResultsController 
 * QuoteResultsController.java - MVC Servlet Controller that takes in quote request parameters and dispatches the correct results view
 * depending on the validity of the passed parameters.  If invalid parameter information is passed, then the client will be redirected to an 
 * error page.  Otherwise, the client is directed to a cost results page.
 * 
 * 07/27/2018 - Initial
 */

package anderson.bhcquotesv3.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import anderson.bhcquotesv3.model.CostResultModel;
import anderson.bhcquotesv3.model.HikeModel;
import anderson.bhcquotesv3.model.PartySizeModel;
import anderson.bhcquotesv3.model.RequestQuoteParamBaseModel;
import anderson.bhcquotesv3.model.RequestQuoteParamModel;
import anderson.bhcquotesv3.model.RequestQuoteParamBaseModel.ParamErrorType;
import anderson.bhcquotesv3.service.BhcLocalRatesService;
import anderson.bhcquotesv3.service.HikeService;
import anderson.bhcquotesv3.service.LocalHikeService;
import anderson.bhcquotesv3.service.RatesService;

@WebServlet("/QuoteResults")
public class QuoteResultsController extends HttpServlet {
	private static final long serialVersionUID = 3380336826552907066L;

	// Constants for the required parameter names for quote requests.
	private static final String HIKE_TYPE_PARAM = "hike";
	private static final String DURATION_PARAM = "duration";
	private static final String PARTY_SIZE_PARAM = "partysize";
	private static final String START_DATE_PARAM = "startdate";
	
	// Potential attributes that would make for an invalid request request
	private static final String EMPTY_PARAMS_ATTRIBUTE = "emptyParams";
	private static final String INVALID_INTEGER_PARAM_ATTRIBUTE = "invalidIntegerParams";
	private static final String INVALID_DATE_PARAM_ATTRIBUTE = "invalidDateParams";
	private static final String INVALID_DURATION_ATTRIBUTE = "invalidDuration";
	private static final String INVALID_HIKE_ID_ATTRIBUTE = "invalidHikeID";
	private static final String INVALID_PARTY_SIZE_ATTRIBUTE = "invalidPartySizeModel";
	private static final String INVALID_REQUEST_SERVICE_ATTRIBUTE = "invalidDateService";

	// We track these invalid attributes in an array for each access.  If the final request has an attribute within this array, then
	// we know that it is an invalid request and that the client should be redirected to an error page as appropriate.
	private static final String[] ERROR_ATTRIBUTES = { EMPTY_PARAMS_ATTRIBUTE, INVALID_INTEGER_PARAM_ATTRIBUTE, INVALID_DATE_PARAM_ATTRIBUTE, 
			INVALID_DURATION_ATTRIBUTE, INVALID_HIKE_ID_ATTRIBUTE, INVALID_PARTY_SIZE_ATTRIBUTE, INVALID_REQUEST_SERVICE_ATTRIBUTE };
		
	// The lone attribute that is used for a successful quote request. 
	private static final String COST_RESULTS_ATTRIBUTE = "costResult";

	// Interface for service that will be performing rate request calculations
	private RatesService ratesService;
	// Interface for service that provides us hike related information
	private HikeService hikeService;

	/**
	 * Instantiate implementations of our services required to help validate the parameters and get quote information.
	 */
	public QuoteResultsController() {
		ratesService = new BhcLocalRatesService();
		hikeService = new LocalHikeService();
	}

	/**
	 * Route into doRequest(HttpServletRequest, HttpServletResponse)
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequest(request, response);
	}

	/**
	 * Route into doRequest(HttpServletRequest, HttpServletResponse)
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doRequest(request, response);
	}

	/**
	 * Primary method. Will perform various error checks on the request parameters to determine the validity of the request.
	 * If there are invalid parameters by any means, a unique attribute is added to the request accordingly with details the jsp View can use.
	 * If there are not an invalid parameters, and therefore a successful request is made, the client is redirected to a jsp View to display the 
	 * cost results.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get our parameters
		List<RequestQuoteParamModel<Integer>> requestIntegerParams = getRequiredIntegerRequestParamObjects(request);
		RequestQuoteParamModel<Date> partySizeParam = getDateParam(request, START_DATE_PARAM);

		// Store our integer parameters
		Integer hikeID = getIntRequestParamVal(requestIntegerParams, HIKE_TYPE_PARAM);
		Integer duration = getIntRequestParamVal(requestIntegerParams, DURATION_PARAM);
		Integer partySize = getIntRequestParamVal(requestIntegerParams, PARTY_SIZE_PARAM);
		
		// Store our date param
		Date startDate = partySizeParam.getValue();
		
		// Combine all the params in a list for error checking
		List<RequestQuoteParamBaseModel> allParams = new ArrayList<RequestQuoteParamBaseModel>();
		allParams.addAll(requestIntegerParams);
		allParams.add(partySizeParam);

		// Perform checks on all the params.  Non-empty lists indicate that those corresponding params are invalid.
		List<RequestQuoteParamBaseModel> emptyParams = getInvalidRequestParams(allParams, ParamErrorType.EMPTY_VALUE);
		List<RequestQuoteParamBaseModel> invalidIntegerParams = getInvalidRequestParams(requestIntegerParams, ParamErrorType.INCORRECT_TYPE);
		List<RequestQuoteParamBaseModel> invalidDateParams = getInvalidRequestParams(Arrays.asList(partySizeParam), ParamErrorType.INCORRECT_TYPE);
		
		// Add the attribute to the request if the corresponding list is non-empty (i.e. the given params are bad) 
		setAttributeIfExists(EMPTY_PARAMS_ATTRIBUTE, emptyParams, request);
		setAttributeIfExists(INVALID_INTEGER_PARAM_ATTRIBUTE, invalidIntegerParams, request);
		setAttributeIfExists(INVALID_DATE_PARAM_ATTRIBUTE, invalidDateParams, request);
		
		// Query the full hike model details from the hike service.
		Optional<HikeModel> optHikeModel = hikeService.getHikeModelFromID(hikeID);
		HikeModel hikeModel = null;

		boolean isValidDuration = false;
		if (hikeID != null && optHikeModel.isPresent()) {
			// A hike model was found from the service, store it.
			hikeModel = optHikeModel.get();

			// Validate the duration requested by the client for the given hike.
			if(duration != null) {
				isValidDuration = hikeService.isValidHikeDuration(hikeModel, duration);
				if(!isValidDuration) {
					request.setAttribute(INVALID_DURATION_ATTRIBUTE, hikeModel);
				}
			}
		} else {
			request.setAttribute(INVALID_HIKE_ID_ATTRIBUTE, hikeID);
		}

		// Validate the Party Size param
		if(partySize != null) {
			PartySizeModel partySizeModel = new PartySizeModel(partySize);
			if(!partySizeModel.isValidSize()) {
				// Provide bean with details the .jsp View requires 
				request.setAttribute(INVALID_PARTY_SIZE_ATTRIBUTE, partySizeModel);
			}
		}
		
		// Do the date and request quote checks only if the information is available to perform it. 
		if (startDate != null && hikeModel != null && isValidDuration) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			ratesService.requestQuoteDetails(hikeModel.getHikeType(), year, month, day, duration);
			
			// If there were any problems found by the service, then pass it into an attribute.
			if(ratesService.isInvalidDate() || ratesService.isInvalidSeasonalDates() || !ratesService.isValidCost()) {
				request.setAttribute(INVALID_REQUEST_SERVICE_ATTRIBUTE, ratesService);	
			}
		}
		
		// If none of the error attributes were added to the request, then this was a successful quote request.
		if (Collections.disjoint(Collections.list(request.getAttributeNames()), Arrays.asList(ERROR_ATTRIBUTES))) {
			// Bundle up the results into a bean.
			CostResultModel costResult = new CostResultModel(hikeModel.getDisplayName(), 
					ratesService.getBeginDate(), ratesService.getEndDate(), partySize, ratesService.getCost());			
			request.setAttribute(COST_RESULTS_ATTRIBUTE, costResult);
			
			// Route to the cost results view 
			RequestDispatcher dispatcher = request.getRequestDispatcher("/CostResults.jsp");
			dispatcher.forward(request, response);
		} else {
			// Otherwise, at least one error attribute was set.  Route immediately to error results view.
			RequestDispatcher dispatcher = request.getRequestDispatcher("/ErrorResults.jsp");
			dispatcher.forward(request, response);
		}
	}
	
	/**
	 * Constructs a list of Request Quote Parameter wrapper objects to encapsulate some details about the
	 * parameter (e.g. if it actually exists, is it a valid integer?, what's its value?)
	 * 
	 * @param request
	 * @return
	 */
	private static List<RequestQuoteParamModel<Integer>> getRequiredIntegerRequestParamObjects(
			HttpServletRequest request) {
		List<RequestQuoteParamModel<Integer>> requestParams = new ArrayList<RequestQuoteParamModel<Integer>>();
		requestParams.add(getIntegerParam(request, HIKE_TYPE_PARAM));
		requestParams.add(getIntegerParam(request, DURATION_PARAM));
		requestParams.add(getIntegerParam(request, PARTY_SIZE_PARAM));
		return requestParams;
	}

	/**
	 * Create a single integer Request Quote Parameter object based on information from servlet request
	 * 
	 * @param request
	 * @param paramName
	 * @return
	 */
	private static RequestQuoteParamModel<Integer> getIntegerParam(HttpServletRequest request, String paramName) {
		String paramStringValue = request.getParameter(paramName);
		Integer paramIntegerValue = null;
		ParamErrorType errorType = ParamErrorType.NONE; // defaulted

		if (paramStringValue == null) {
			// parameter was not passed
			errorType = ParamErrorType.EMPTY_VALUE;
		} else {
			try {
				paramIntegerValue = Integer.parseInt(paramStringValue);
			} catch (NumberFormatException nfe) {
				// parameter was passed, but not as an integer
				errorType = ParamErrorType.INCORRECT_TYPE;
			}
		}

		// construct object and return it
		RequestQuoteParamModel<Integer> paramObject = new RequestQuoteParamModel<Integer>(paramName,
				paramIntegerValue, errorType);
		return paramObject;
	}

	/**
	 * Create a single date Request Quote Parameter object based on information from servlet request
	 * 
	 * @param request
	 * @param paramName
	 * @return
	 */
	private static RequestQuoteParamModel<Date> getDateParam(HttpServletRequest request, String paramName) {
		String paramStringValue = request.getParameter(paramName);
		Date paramDateValue = null;
		ParamErrorType errorType = ParamErrorType.NONE; // defaulted

		if (paramStringValue == null) {
			// parameter was not passed
			errorType = ParamErrorType.EMPTY_VALUE;
		} else {
			try {
				// try to parse date in required format.
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				dateFormat.setLenient(false);
				paramDateValue = dateFormat.parse(paramStringValue);
			} catch (ParseException pe) {
				// parameter was passed, but not as a date
				errorType = ParamErrorType.INCORRECT_TYPE;
			}
		}

		// construct object and return it
		RequestQuoteParamModel<Date> paramObject = new RequestQuoteParamModel<Date>(paramName, paramDateValue,
				errorType);
		return paramObject;
	}

	/**
	 * Searches the list of parameters (using Java stream) and returns its value.
	 * Will return null if the parameter value is not valid.
	 * 
	 * @param requestParams
	 * @param paramName
	 * @return
	 */
	private static Integer getIntRequestParamVal(List<RequestQuoteParamModel<Integer>> requestParams, String paramName) {
		Integer value = null;
		OptionalInt val = requestParams.stream()
				.filter(param -> param.getName() == paramName && param.getErrorType() == ParamErrorType.NONE)
				.mapToInt(f -> f.getValue()).findAny();
		if (val.isPresent()) {
			value = val.getAsInt();
		}
		return value;
	}

	/**
	 * Queries the passed request params to retrieve any with the specified error type.  
	 * 
	 * @param requestParams
	 * @param checkedErrorType
	 * @return
	 */
	private static List<RequestQuoteParamBaseModel> getInvalidRequestParams(
			List<? extends RequestQuoteParamBaseModel> requestParams, ParamErrorType checkedErrorType) {
		List<RequestQuoteParamBaseModel> errorParams = requestParams.stream()
				.filter((param) -> param.getErrorType() == checkedErrorType).collect(Collectors.toList());

		return errorParams;
	}

	/**
	 * Set the attribute given by the @see attributeName if the passed params list is not empty
	 * @param attributeName
	 * @param params
	 * @param request
	 */
	private static void setAttributeIfExists(String attributeName, List<RequestQuoteParamBaseModel> params, HttpServletRequest request) {
		if(params != null && !params.isEmpty()) {
			request.setAttribute(attributeName, params);
		}
	}

}
