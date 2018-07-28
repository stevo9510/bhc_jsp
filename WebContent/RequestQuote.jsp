<!-- 
	Copyright 2018
	Steven Anderson
	All rights reserved
	
	Homework 10 - ErrorResults
	RequestQuote.jsp - View for displaying form that allows client to submit a BHC quote request. 
	 
	07/27/2018 - Initial
 -->


<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<%@ page import="anderson.bhcquotesv3.core.Constants" %>
<html>
<head>
<title>Beartooth Hiking Company (BHC) - Request Quote</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="stylesheets/main.css"
	media="screen">
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
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
	<div class="container">
		<%@ include file="SiteHeader.jsp"%>
		<h2>Instructions</h2>
		<p>
			Request a quote for the cost of a tour by selecting a tour type,
			start date and duration. <span style="font-weight: bold">Note:</span>
			Tours are seasonal and may not be available depending on the time of
			the year.
		</p>
		<h2>Request Tour Quote</h2>
		<form action="QuoteResults" method="get"
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
					target="_blank">View more tour details...</a>
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
						for (int partySizeValue = 1; partySizeValue <= Constants.MAX_PARTY_SIZE; partySizeValue++) {
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
		<%@ include file="SiteFooter.jsp"%>
	</div>
</body>
</html>
