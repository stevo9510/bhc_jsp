<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<%@ page import="anderson.bhcquotesv3.HikeViewModel"%>
<%@ page import="anderson.bhcquotesv3.BhcQuoteController"%>

<html>
<head>
<title>Beartooth Hiking Company (BHC) - Request Quote</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="stylesheets/main.css"	media="screen">
<link rel="stylesheet"	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" href="/resources/demos/style.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script>
	$(document).ready(function() {
		$("#datepicker").datepicker({
			dateFormat : 'mm/dd/yy',
			inline : true,
			minDate : new Date(2007, 0, 1),
			maxDate : new Date(2020, 11, 31)
		});
	});
</script>
<script src="fieldValidator.js"></script>
</head>

<body>
	<a name="top"></a>
	<div class="container">
		<br />
		<div style="text-align: center">
			<img width="45%" src="images\Beartooth002-02.jpg"
				alt="Beartooth Hiking Company Logo" />
			<h1>Beartooth Hiking Company - Request Quote</h1>
		</div>
		<h2>Instructions</h2>
		<p>
			Request a quote for the cost of a tour by selecting a tour type,
			start date and duration. <span style="font-weight: bold">Note:</span>
			Tours are seasonal and may not be available depending on the time of
			the year.
		</p>
		<h2>Request Tour Quote</h2>
		<form action="RequestQuote#results" method="get"
			onsubmit="return validateForm()">
			<fieldset>
				<legend>Step 1: Select a Tour Type</legend>
				<table style="margin-left: auto; margin-right: auto;">
					<tr>
						<c:forEach items="${hikeViewModels}" var="viewModel">
							<td><label> <input type="radio" name="hike"
									id="${viewModel.getDisplayName()}"
									value="${viewModel.getHikeID()}"
									onchange="${viewModel.getOnChangeJsFunctionName()}">
									${viewModel.getDisplayName()} <br> <img
									src="${viewModel.getNormalIconFilePath()}" class="hikeImage"
									alt="${viewModel.getDisplayName()}">
							</label></td>
						</c:forEach>
					</tr>
				</table>

				<br> <br> <span class="helpIcon"
					style="padding-right: 5px;">?</span><a
					href="https://web7.jhuep.com/~sande107/bhc_site_v1/Homework3.html#tourInfoLabel"
					target="_blank" alt="Tour Details on BHC Website">View more
					tour details...</a>
			</fieldset>
			<fieldset>
				<legend>Step 2: Select Timeframe</legend>
				<span class="fieldLabel">Start date:</span> <input type="text"
					name="startdate" id="datepicker"> <span class="fieldLabel"
					style="padding-left: 25px">Duration (in days):</span> <select
					id="duration" name="duration">
				</select> <span class="fieldLabel" style="padding-left: 25px">Party
					Size:</span> <select id="partysize" name="partysize">
					<%
						for (int partySizeValue = 1; partySizeValue <= BhcQuoteController.MAX_PARTY_SIZE; partySizeValue++) {
					%>
					<option value="<%=partySizeValue%>"><%=partySizeValue%></option>
					<%
						}
					%>

				</select> <span class="fieldLabel">persons</span>
			</fieldset>
			<br />
			<button type="submit" class="requestQuote">
				<span style="color: green; padding-right: 5px;">$</span>Request
				Quote
			</button>
		</form>
		<h2>Contact Information</h2>
		<p>
			You can contact our wonderful customer service at BHC at anytime of
			the week via <a href="mailto:sande107@jh.edu">email</a> to schedule a
			tour or you have any questions in regards to the quote. Please send
			to the email with your name, contact information, and requested tour
			date. A member of our team will contact you within 24 hours of the
			request.
		</p>
		<hr />
		<a href="#top">Back to top</a>
	</div>
</body>
</html>
