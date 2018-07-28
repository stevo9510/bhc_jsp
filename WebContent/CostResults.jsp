<!-- 
	Copyright 2018
	Steven Anderson
	All rights reserved
	
	Homework 10 - CostResults 
	CostResults.jsp - View for displaying successful cost result information to the client.
	 
	07/27/2018 - Initial
 -->
 
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="stylesheets/main.css"
	media="screen">
<title>Beartooth Hiking Company (BHC) - Quote Results</title>
<jsp:useBean id="costResult" class="anderson.bhcquotesv3.model.CostResultModel" scope="request" />
</head>
<body>
	<div class="container">
		<%@ include file="SiteHeader.jsp"%>
		<h2>Quote Results</h2>
		<p>
			We've crunched the numbers and have a cost we know you will love!   
			If you'd try some different options, you can start another <a href="RequestQuote">request here.</a>
		</p>
		<h2>Cost Details</h2>
		<div class="results">
			<span class="resultLabel">Tour Type:</span> <span class="resultText">${costResult.hikeDisplayName}</span>
			<span style="padding-left: 15px" class="resultLabel">From: </span> <span
				class="resultText">${costResult.startDate}</span> <span
				class="resultLabel">to: </span> <span class="resultText">${costResult.endDate}</span>
			<span style="padding-left: 15px" class="resultLabel">Party
				Size: </span> <span class="resultText">${costResult.partySize}
				persons</span><br> <br> <span class="resultLabel">Cost
				(Per Person):</span> <span class="perPersonCostText">${costResult.cost}</span><br>
			<br> <span class="resultLabel">Grand Total:</span> <span
				class="totalCostText">${costResult.totalCost}</span><br>
		</div>

		<%@ include file="SiteFooter.jsp"%>

	</div>
</body>
</html>