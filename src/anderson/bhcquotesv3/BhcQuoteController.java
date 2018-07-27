package anderson.bhcquotesv3;

import java.io.IOException;
import java.io.PrintWriter;
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
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rbevans.bookingrate.Rates;

import anderson.bhcquotesv3.BhcLocalRatesService;
import anderson.bhcquotesv3.HikeViewModel;
import anderson.bhcquotesv3.RatesService;
import anderson.bhcquotesv3.RequestQuoteParamBaseObject;
import anderson.bhcquotesv3.RequestQuoteParamObject;
import anderson.bhcquotesv3.RequestQuoteParamBaseObject.ParamErrorType;

@WebServlet("/RequestQuote")
public class BhcQuoteController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4516654360375493301L;

	// Constants for the required parameter names for quote requests.   
		private static final String HIKE_TYPE_PARAM = "hike";
		private static final String DURATION_PARAM = "duration";
		private static final String PARTY_SIZE_PARAM = "partysize";
		private static final String START_DATE_PARAM = "startdate";
			
		// Constants for the valid durations for the hike options.  
		// Note: This information is also within Rates.java, but requires an instance of the object to be created first.  Constants were preferred in this case, albeit redundant.
		private static final List<Integer> GARINDER_DURATIONS = Collections.unmodifiableList(Arrays.asList(3, 5));
		private static final List<Integer> HELLROARING_DURATIONS = Collections.unmodifiableList(Arrays.asList(2, 3, 4));
		private static final List<Integer> BEATEN_DURATIONS = Collections.unmodifiableList(Arrays.asList(5, 7));
		
		// Note: These are based off BookingDay.java.  Ideally these would be defined / shared between the two classes, but I did not want to modify the provided BookingDay class.
		public static final int VALID_START_YEAR = 2007; // Going to assume we're actually in the year 2007, and accept quotes for anything pre-current date :)
		public static final int VALID_END_YEAR = 2020;
		
		public static final int MAX_PARTY_SIZE = 10;
		
		// List of helper objects with attributes related to the hike types. 
		private static final HikeViewModel[] hikeViewModels = 
		{ 
			new HikeViewModel(Rates.HIKE.GARDINER, 0, "Gardiner Lake", "images\\morning_lake2.png", GARINDER_DURATIONS, "onGardinerSelected()"), 
			new HikeViewModel(Rates.HIKE.HELLROARING, 1, "Hellroaring Plateau", "images\\sunset2.png", HELLROARING_DURATIONS, "onHellroaringSelected()"), 
			new HikeViewModel(Rates.HIKE.BEATEN, 2, "Beaten Path", "images\\wilderness2.jpg", BEATEN_DURATIONS, "onBeatenPathSelected()")
		};
		
		// Interface for service that will be performing rate request calculations
		private RatesService ratesService;
		
		public void init() throws ServletException {
	    	ServletContext sc = getServletContext();
	    	if(sc.getAttribute("hikeViewModels") == null) {
	    		getServletContext().setAttribute("hikeViewModels", hikeViewModels);
	    	}
		}
		
	    /**
	     * @see HttpServlet#HttpServlet()
	     */
	    public BhcQuoteController() {        
	        // re-use the local (non-socket based) rates service we created in the previous HW assignment
	        ratesService = new BhcLocalRatesService(); 
	    }

		/**s
		 * Route into doRequest(HttpServletRequest, HttpServletResponse)
		 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
		 */
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			doRequest(request, response);
		}	
		
		/**
		 * Route into doRequest(HttpServletRequest, HttpServletResponse)
		 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
		 */
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			doRequest(request, response);
		}
		
		/**
		 * Primary method.  Will always serve up an instructions, request tour form, and contact information section - as well as other common html.
		 * If parameters are passed, then an additional section will be come available with result information.
		 * @param request
		 * @param response
		 * @throws ServletException
		 * @throws IOException
		 */
		private void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("text/html;charset=UTF-8");
			
			// holds our error message if parameters passed are invalid in some sort of manner
			StringBuilder errorMsgBuilder = new StringBuilder();
			
			boolean parametersPassed = !request.getParameterMap().isEmpty();
			
			// default these values to be used later for pre-selecting form input options (e.g. from a quote request) or for other method calls
			Integer hikeID = null;
			Integer duration = null;
			Date startDate = null;
			Integer partySize = null;
			HikeViewModel hikeViewModel = null;
			
			/*
			 * The following error checks are checked if parameters are passed:
			 *  - All required parameters are passed (i.e. all parameters are passed and not empty)
			 *  - All required parameters are the correct data type (e.g. a alphabetic string is not passed when an int is required)
			 *  - The hike id passed as a parameter is a valid hike id matching to one of the hike types.  
			 *  - The duration length is valid for the hike type.
			 *  - The date composed by the m/d/y parameters is valid (e.g. a valid date and in season)  
			 *  - The party size does not surpass the max limit
			 */
			if (parametersPassed) {
				List<RequestQuoteParamObject<Integer>> requestIntegerParams = getRequiredIntegerRequestParamObjects(request);
				RequestQuoteParamObject<Date> partySizeParam = getDateParam(request, START_DATE_PARAM);
				
				// set the actual values of each parameter object to local but outside scoped variable 
				hikeID = getIntRequestParamVal(requestIntegerParams, HIKE_TYPE_PARAM);
				duration = getIntRequestParamVal(requestIntegerParams, DURATION_PARAM);
				partySize = getIntRequestParamVal(requestIntegerParams, PARTY_SIZE_PARAM);
				startDate = partySizeParam.getValue();
				List<RequestQuoteParamBaseObject> allParams = new ArrayList<RequestQuoteParamBaseObject>();
				allParams.addAll(requestIntegerParams);
				allParams.add(partySizeParam);							
				
				// write to error message builder information about bad parameters
				String emptyParamInfo = checkInvalidRequestParams(allParams, ParamErrorType.EMPTY_VALUE, "The following required fields are required to be filled in:");
				appendLine(errorMsgBuilder, emptyParamInfo);
				String incorrectIntegerTypeParamInfo = checkInvalidRequestParams(requestIntegerParams, ParamErrorType.INCORRECT_TYPE, "The following required fields were expected to be integer values:");
				appendLine(errorMsgBuilder, incorrectIntegerTypeParamInfo);
				String incorrectDateTypeParamInfo = checkInvalidRequestParams(Arrays.asList(partySizeParam), ParamErrorType.INCORRECT_TYPE, "The following required fields were expected to be valid date values:");
				appendLine(errorMsgBuilder, incorrectDateTypeParamInfo);
				
				// only proceed if all required parameters are valid
				if(errorMsgBuilder.length() == 0) {
					Optional<HikeViewModel> optViewModel = getHikeViewModelFromID(hikeID);
					// checks validity of hikeID parameter passed 
					if(optViewModel.isPresent()) {
						hikeViewModel = optViewModel.get();
						
						// check hike duration is valid for the hike type
						String invalidHikeDurationInfo = checkInvalidHikeDuration(hikeViewModel, duration);
						appendLine(errorMsgBuilder, invalidHikeDurationInfo);
						
						// check the date is valid
						if(errorMsgBuilder.length() == 0) {
							Calendar cal = Calendar.getInstance();
							cal.setTime(startDate);
							int year = cal.get(Calendar.YEAR); 
							int month = cal.get(Calendar.MONTH) + 1;
							int day = cal.get(Calendar.DAY_OF_MONTH);
							String invalidDateInfo = checkDateValidity(ratesService, hikeViewModel, duration, month, day, year);
							appendLine(errorMsgBuilder, invalidDateInfo);
						}
					} else {
						appendLine(errorMsgBuilder, "<p>The provided hike ID parameter does not match a valid hike type.</p>");				
					}
					
					if(partySize < 1 || partySize > MAX_PARTY_SIZE) {
						appendLine(errorMsgBuilder, String.format("<p>The party size must be between %d and %d.</p>", 1, MAX_PARTY_SIZE));
					}
				} 			
			}

//			PrintWriter out = response.getWriter();
//			
//			// write the initial html that is common whether parameters are passed or not.  Pass in values that should be used as default values set in elements of the form.   
//			writeCommonInitialHtml(request, hikeViewModel, startDate, duration, partySize, out);
//			
//			request.setAttribute("ratesService", ratesService);
//			
//			if(parametersPassed) {
//				writeRequestResults(ratesService, errorMsgBuilder, hikeViewModel, partySize, out);
//			}
//			
//			writeCommonFinalHtml(out);
			request.setAttribute("hikeViewModels", hikeViewModels);
			request.setAttribute("foo", "bar");
		       RequestDispatcher dispatcher = request.getRequestDispatcher(
		    	          "/RequestQuote.jsp");
		    	        dispatcher.forward(request, response);
		}
		
		/**
		 * Constructs a list of Request Quote Parameter wrapper objects (which all happen to be Integers currently) to encapsulate some details about the parameter 
		 * (e.g. if it actually exists, is it a valid integer?, what's its value?)
		 * @param request
		 * @return
		 */
		private static List<RequestQuoteParamObject<Integer>> getRequiredIntegerRequestParamObjects(HttpServletRequest request) {
			List<RequestQuoteParamObject<Integer>> requestParams = new ArrayList<RequestQuoteParamObject<Integer>>();
			requestParams.add(getIntegerParam(request, HIKE_TYPE_PARAM));
			requestParams.add(getIntegerParam(request, DURATION_PARAM));
			requestParams.add(getIntegerParam(request, PARTY_SIZE_PARAM));
			return requestParams;
		}

		/**
		 * Create a single integer Request Quote Parameter object based on information from servlet request 
		 * @param request
		 * @param paramName
		 * @return
		 */
		private static RequestQuoteParamObject<Integer> getIntegerParam(HttpServletRequest request, String paramName) {
			String paramStringValue = request.getParameter(paramName);
			Integer paramIntegerValue = null;
			ParamErrorType errorType = ParamErrorType.NONE; // defaulted
			
			if(paramStringValue == null) {
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
			RequestQuoteParamObject<Integer> paramObject = new RequestQuoteParamObject<Integer>(paramName, paramIntegerValue, errorType);
			return paramObject;
		}
		
		/**
		 * Create a single date Request Quote Parameter object based on information from servlet request 
		 * @param request
		 * @param paramName
		 * @return
		 */
		private static RequestQuoteParamObject<Date> getDateParam(HttpServletRequest request, String paramName) {
			String paramStringValue = request.getParameter(paramName);
			Date paramDateValue = null;
			ParamErrorType errorType = ParamErrorType.NONE; // defaulted
			
			if(paramStringValue == null) {
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
			RequestQuoteParamObject<Date> paramObject = new RequestQuoteParamObject<Date>(paramName, paramDateValue, errorType);
			return paramObject;
		}
		
		/**
		 * Searches the list of parameters (using Java stream) and returns its value. Will return null if the parameter value is not valid. 
		 * @param requestParams
		 * @param paramName
		 * @return
		 */
		private static Integer getIntRequestParamVal(List<RequestQuoteParamObject<Integer>> requestParams, String paramName) {
			Integer value = null;
			OptionalInt val = requestParams.stream().filter(param -> param.getName() == paramName && param.getErrorType() == ParamErrorType.NONE).mapToInt(f -> f.getValue()).findAny();
			if(val.isPresent()) {
				value = val.getAsInt();
			}
			return value;
		}

		/**
		 * Returns message if bad parameters exist.  Generates an html list with bad parameter names. 
		 * Extracted out into a method for reuse for different types of error types and base messages. 
		 * @param requestParams
		 * @param checkedErrorType
		 * @param baseErrorMsg
		 * @return
		 */
		private static String checkInvalidRequestParams(List<? extends RequestQuoteParamBaseObject> requestParams, ParamErrorType checkedErrorType, String baseErrorMsg) {
			StringBuilder localErrorMsgBuilder = new StringBuilder();
			List<RequestQuoteParamBaseObject> errorParams = 
					requestParams.stream().filter((param) -> param.getErrorType() == checkedErrorType).
					collect(Collectors.toList()); 
			
			if(!errorParams.isEmpty()) {
				appendLine(localErrorMsgBuilder, baseErrorMsg);
				appendLine(localErrorMsgBuilder, "<ul>");
				for(RequestQuoteParamBaseObject errParam : errorParams) {
					appendLine(localErrorMsgBuilder, "<li>" + errParam.getName());
				}
				appendLine(localErrorMsgBuilder, "</ul>");
			}
			return localErrorMsgBuilder.toString();
		}

		/**
		 * Search up the matching HikeViewModel based on the passed hikeID.  Uses Java streams to filter, and will return Optional data type (in case not found)  
		 * @param hikeID
		 * @return
		 */
		private static Optional<HikeViewModel> getHikeViewModelFromID(int hikeID) {
			Optional<HikeViewModel> optHikeViewModel = Arrays.asList(hikeViewModels).stream().filter(vm -> vm.getHikeID() == hikeID).findFirst();
			return optHikeViewModel;
		}

		/**
		 * Do error checking to see if the duration parameter passed is valid for the hike type.  
		 * If it is invalid, generates html unordered list of the valid durations for the user and returns.
		 * @param viewModel
		 * @param duration
		 * @return
		 */
		private static String checkInvalidHikeDuration(HikeViewModel viewModel, int duration) {
			StringBuilder localErrorMsgBuilder = new StringBuilder();
			if(!viewModel.getValidDurations().contains(duration)) {
				appendLine(localErrorMsgBuilder, String.format("The valid durations for %s are:", viewModel.getDisplayName()));	
				appendLine(localErrorMsgBuilder, "<ul>");
				
				for(Integer dur : viewModel.getValidDurations()) {
					appendLine(localErrorMsgBuilder, "<li>" + dur + " days");
				}
				
				appendLine(localErrorMsgBuilder, "</ul>");
			}

			return localErrorMsgBuilder.toString();
		}

		/**
		 * Check that the dates provided to the date service are valid.  If they are not, generate an error message and return.
		 * @param rService
		 * @param hikeViewModel
		 * @param duration
		 * @param month
		 * @param day
		 * @param year
		 * @return
		 */
		private static String checkDateValidity(RatesService rService, HikeViewModel hikeViewModel, Integer duration, Integer month, Integer day, Integer year) {
			StringBuilder localErrorMsgBuilder = new StringBuilder();
			rService.requestQuoteDetails(hikeViewModel.getHikeType(), year, month, day, duration);
			if(rService.isInvalidDate()) {
				appendLine(localErrorMsgBuilder, "<p>The selected date is not a valid day of that month/year, or is out of the acceptable range. Please choose another date.</p>");
			} else if(rService.isInvalidSeasonalDates()) {
				appendLine(localErrorMsgBuilder, "<p>The selected timeframe is not during the tour season. Please choose another date.</p>");
			} else if(!rService.isValidCost()) {
				appendLine(localErrorMsgBuilder, "<p>The selected timeframe is not valid for the following reason: " + rService.getDetails() + "</p>");
			}
			return localErrorMsgBuilder.toString();
		}

		/**
		 * Writes out common html for the page.  Passed parameters are used to set default values for the form element.
		 * @param request
		 * @param hikeViewModel
		 * @param startDate
		 * @param duration
		 * @param partySize
		 * @param out
		 */
		private static void writeCommonInitialHtml(HttpServletRequest request, HikeViewModel hikeViewModel, Date startDate, Integer duration, Integer partySize, PrintWriter out) {
			Integer hikeID = null;
			if(hikeViewModel != null) {
				hikeID = hikeViewModel.getHikeID();
			}
			out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			out.println("<head>");
			out.println("    <title>Beartooth Hiking Company (BHC) - Request Quote</title>");
			out.println("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
			out.println("	 <link rel=\"stylesheet\" type=\"text/css\" href=\"stylesheets/main.css\" media=\"screen\"> 	");
			out.println("    <link rel=\"stylesheet\" href=\"//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css\"> ");
		    out.println("    <link rel=\"stylesheet\" href=\"/resources/demos/style.css\"> ");
		    out.println("    <script src=\"https://code.jquery.com/jquery-1.12.4.js\"></script>");
			out.println("    <script src=\"https://code.jquery.com/ui/1.12.1/jquery-ui.js\"></script>");
			out.println("    <script> ");
			out.println("    $(document).ready (function() { ");
			out.println(String.format("$( \"#datepicker\" ).datepicker({ dateFormat: 'mm/dd/yy', inline: true, minDate: new Date(%d, 0, 1), maxDate: new Date(%d, 11, 31) });", VALID_START_YEAR, VALID_END_YEAR));
			if(startDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				int year = cal.get(Calendar.YEAR); 
				int month = cal.get(Calendar.MONTH);
				int day = cal.get(Calendar.DAY_OF_MONTH);
				out.println(String.format("$( \"#datepicker\" ).datepicker(\"setDate\", new Date(%d, %d, %d));", year, month, day));
			}				
			out.println(" } );");
			out.println("</script>");
			out.println("    <script src=\"fieldValidator.js\"></script>");
			out.println("</head>");
			out.println("<body>");
			// for jumping back to the top
			out.println("	 <a name=\"top\"></a>");
			// html container, styled by css
			out.println("    <div class=\"container\">");
			out.println("        <br />");
			// header logo
			out.println("        <div style=\"text-align:center\">");
			out.println("            <img width=\"45%\" src=\"images\\Beartooth002-02.jpg\" alt=\"Beartooth Hiking Company Logo\" />");
			out.println("            <h1>Beartooth Hiking Company - Request Quote</h1>");
			out.println("        </div>");
			// instructions section 
			out.println("        <h2>Instructions</h2>");
			out.println("        <p>");
			out.println("            Request a quote for the cost of a tour by selecting a tour type, start date and duration.");
			out.println("            <span style=\"font-weight:bold\">Note:</span> Tours are seasonal and may not be available depending on the time of the year.");
			out.println("        </p>");
			// request tour quote form 
			out.println("        <h2>Request Tour Quote</h2>");
			out.println("        <form action=\"RequestQuote#results\" method=\"get\" onsubmit=\"return validateForm()\">");
			out.println("            <fieldset>");
			out.println("                <legend>Step 1: Select a Tour Type</legend>");
			out.println(createHikeInputHtml(hikeViewModels, hikeID));    // generate hike input html dynamically
			out.println("			 </br>");
			out.println("			 </br>");
			// help icon for main page
			out.println("        	 <span class=\"helpIcon\" style=\"padding-right:5px;\">?</span><a href=\"https://web7.jhuep.com/~sande107/bhc_site_v1/Homework3.html#tourInfoLabel\" target=\"_blank\" alt=\"Tour Details on BHC Website\">View more tour details...</a>");
			out.println(" 			 </fieldset>");
			out.println("            <fieldset>");
			out.println("                <legend>Step 2: Select Timeframe</legend>");
			out.println("                <span class=\"fieldLabel\">Start date:</span>");
			// use jquery datepicker
			out.println(String.format(" <input type=\"text\" name=\"%s\" id=\"datepicker\">", START_DATE_PARAM));
			out.println("                <span class=\"fieldLabel\" style=\"padding-left:25px\">Duration (in days):</span>");
			out.println(createDurationPickerHtml(duration, hikeViewModel));
			out.println("                <span class=\"fieldLabel\" style=\"padding-left:25px\">Party Size:</span>");
			out.println(createPartySizePickerHtml(partySize));
			out.println("                <span class=\"fieldLabel\">persons</span>");
			out.println("            </fieldset>");
			out.println("            <br />");
			out.println("            <button type=\"submit\" class=\"requestQuote\"><span style=\"color:green;padding-right:5px;\">$</span>Request Quote</button>");
			out.println("        </form>");
		}

		/**
		 * Generates html to select a hike type within a form. 
		 * @param hikeViewModels
		 * @param hikeIdToSelect
		 * @return
		 */
		private static String createHikeInputHtml(HikeViewModel[] hikeViewModels, Integer hikeIdToSelect) {
			StringBuilder hikeInputHtml = new StringBuilder();
			
			appendLine(hikeInputHtml, "<table style=\"margin-left:auto;margin-right:auto;\">");
			appendLine(hikeInputHtml, "<tr>");
			
			for(HikeViewModel hvm : hikeViewModels) {
				appendLine(hikeInputHtml, "<td>");
				appendLine(hikeInputHtml, "<label>");
				String hikeViewModelHtml = createHikeInputHtml(hvm, hikeIdToSelect != null && hikeIdToSelect.intValue() == hvm.getHikeID());
				appendLine(hikeInputHtml, hikeViewModelHtml);
				appendLine(hikeInputHtml, "</label>");
				appendLine(hikeInputHtml, "</td>");
			}
			
			appendLine(hikeInputHtml, "</table>");
			appendLine(hikeInputHtml, "</tr>");
			
			return hikeInputHtml.toString();
		}
		
		/** 
		 * Create html for a single hike type input within a form, with an associated image.  Generates a radio control.    
		 * @param viewModel
		 * @param isSelected 
		 * @return
		 */
		private static String createHikeInputHtml(HikeViewModel viewModel, boolean isSelected) {
			StringBuilder singleHikeInputHtml = new StringBuilder();
			String radioInput = String.format("<input type=\"radio\" name=\"%s\" id=\"%s\" value=\"%d\" onchange=\"%s\" %s>", 
					HIKE_TYPE_PARAM,
					viewModel.getDisplayName(),
					viewModel.getHikeID(),
					viewModel.getOnChangeJsFunctionName(),
					isSelected ? "checked" : "");
			appendLine(singleHikeInputHtml, radioInput);
			
			appendLine(singleHikeInputHtml, "   " + viewModel.getDisplayName() + "</br>");
			
			String img = String.format("<img src=\"%s\" class=\"hikeImage\" alt=\"%s\">", viewModel.getNormalIconFilePath(), viewModel.getDisplayName());
			appendLine(singleHikeInputHtml, img);
			
			return singleHikeInputHtml.toString();	
		}
		
		/**
		 * Helper method for generating an option html picker with options from start to end parameters.  
		 * @param start
		 * @param end
		 * @param valToSelect
		 * @param paramName
		 * @return
		 */
		private static String createBasicSelectOptionHtml(int start, int end, Integer valToSelect, String paramName) {
			StringBuilder selectOptionHtml = new StringBuilder();
			appendLine(selectOptionHtml, String.format("<select id=\"%s\" name=\"%s\">", paramName, paramName));
			
			for(int val = start; val <= end; val++) {
				String selectedText = (valToSelect != null && valToSelect.intValue() == val) ? " selected" : "";
				appendLine(selectOptionHtml, String.format("<option value=\"%d\"%s>%d</option>", val, selectedText, val));
			}
			
			appendLine(selectOptionHtml, "</select>");
			return selectOptionHtml.toString();
		}
		
		/**
		 * Create html for a tour duration/length picker.
		 * @param yearToSelect
		 * @param selectedHikeViewModel
		 * @return
		 */
		private static String createDurationPickerHtml(Integer durationToSelect, HikeViewModel selectedHikeViewModel) {
			StringBuilder selectOptionHtml = new StringBuilder();
			appendLine(selectOptionHtml, String.format("<select id=\"%s\" name=\"%s\">", DURATION_PARAM, DURATION_PARAM));
			
			if(selectedHikeViewModel != null) {
				for(Integer dur : selectedHikeViewModel.getValidDurations()) {
					String selectedText = (durationToSelect != null && durationToSelect.intValue() == dur) ? " selected" : "";
					appendLine(selectOptionHtml, String.format("<option value=\"%d\"%s>%d</option>", dur, selectedText, dur));
				}
			}
			
			appendLine(selectOptionHtml, "</select>");
			return selectOptionHtml.toString();
		}
		
		/**
		 * Create html for a tour party size picker.
		 * @param partySizeToSelect
		 * @return
		 */
		private static String createPartySizePickerHtml(Integer partySizeToSelect) {
			return createBasicSelectOptionHtml(1, MAX_PARTY_SIZE, partySizeToSelect, PARTY_SIZE_PARAM);
		}

		/**
		 * Write results section of html out.  Will write error messages out if there are any, otherwise, it will display the cost information.
		 * This should only be called if parameters are passed
		 * @param rService
		 * @param errorMsgBuilder
		 * @param hikeViewModel
		 * @param partySize
		 * @param out
		 */
		private void writeRequestResults(RatesService rService, StringBuilder errorMsgBuilder, HikeViewModel hikeViewModel, Integer partySize, PrintWriter out) {
			out.println("<h2>Quote Results</h2>");
			out.println("<a name=\"results\"></a>");
			out.println("<div class=\"results\">");
			if(errorMsgBuilder.length() == 0) {
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				out.format("<span class=\"resultLabel\">Tour Type:</span> <span class=\"resultText\">%s</span>%s", hikeViewModel.getDisplayName(), System.lineSeparator());
				out.format("<span style=\"padding-left:15px\" class=\"resultLabel\">From: </span> <span class=\"resultText\">%s</span> ", df.format(rService.getBeginDate()));
				out.format("<span class=\"resultLabel\">to: </span> <span class=\"resultText\">%s</span>%s", df.format(rService.getEndDate()), System.lineSeparator());
				out.format("<span style=\"padding-left:15px\" class=\"resultLabel\">Party Size: </span> <span class=\"resultText\">%d persons</span></br></br>%s", partySize, System.lineSeparator());
				out.format("<span class=\"resultLabel\">Cost (Per Person):</span> <span class=\"perPersonCostText\">$%.2f</span></br></br>%s", rService.getCost(), System.lineSeparator());
				out.format("<span class=\"resultLabel\">Total Cost:</span> <span class=\"totalCostText\">$%.2f</span></br>%s", rService.getCost() * partySize, System.lineSeparator());
			} else {
				out.println("<span class=\"warningText\">We were unable to calculate the cost of the tour for the following reason(s).</span></br></br>");
				out.println(errorMsgBuilder.toString());
			}		
			out.println("</div>");
		}

		/**
		 * Write out the final section of html for the page (contact info, and a link back to the top)
		 * @param out
		 */
		private void writeCommonFinalHtml(PrintWriter out) {
			out.println("<h2>Contact Information</h2>");
	        out.println("	<p>");
	        out.println("    	You can contact our wonderful customer service at BHC at anytime of the week via <a href=\"mailto:sande107@jh.edu\">email</a> to schedule a tour or you have any questions in regards to the quote.  Please send to the email with your name, contact information, and requested tour date. A member of our team will contact you within 24 hours of the request.");
	        out.println("	</p>");
	        out.println("	<hr />");
	        out.println("	 <a href=\"#top\">Back to top</a>");
			out.println("    </div>");
			out.println("	</body>");
			out.println("</html>");
		}
		
		/** 
		 * Helper method for appending text and a new line to a string builder if the line is not empty or null.
		 * @param sb
		 * @param line
		 */
		private static void appendLine(StringBuilder sb, String line) {
			if(line != null && line.trim().length() > 0) {
				sb.append(line + System.lineSeparator());
			}
		}

}
