<!-- 
	Copyright 2018
	Steven Anderson
	All rights reserved
	
	Homework 10 - ErrorResults
	ErrorResults.jsp - View for displaying any errors that resulted from a quote request. 
	 
	07/27/2018 - Initial
 -->

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="stylesheets/main.css"
	media="screen">
<title>Beartooth Hiking Company (BHC) - Could Not Calculate
	Results</title>
</head>

<body>
	<div class="container">
		<%@ include file="SiteHeader.jsp"%>
		<h2>Woops... We could not calculate your quote</h2>
		<p>
			We were unable to process your quote request for the following reason(s) listed in the following section.  
			Please review the details and <a href="RequestQuote">try the request again</a> if desired.  Sorry for the inconvenience. 
		</p>
		
		<h2>Details</h2>
		<div class="warningText">
		<c:if test="${!empty emptyParams}">
			<p>The following required fields are required to be filled in:</p>
			<ul>
			<c:forEach items="${emptyParams}" var="p">
				<li>${p.name}</li>
			</c:forEach>
			</ul>
		</c:if>

		<c:if test="${!empty invalidIntegerParams}">
			<p>The following required fields were expected to be integer
				values:</p>
			<ul>
				<c:forEach items="${invalidIntegerParams}" var="p">
					<li>${p.name}</li>
				</c:forEach>
			</ul>
		</c:if>

		<c:if test="${!empty invalidDateParams}">
			<p>The following required fields were expected to be valid date
				values:</p>
			<ul>
				<c:forEach items="${invalidDateParams}" var="p">
					<li>${p.name}</li>
				</c:forEach>
			</ul>
		</c:if>

		<c:if test="${!empty invalidDuration}">
			<p>The duration specified for ${invalidDuration.displayName} was
				not valid. These are the valid durations:</p>
			<ul>
				<c:forEach items="${invalidDuration.validDurations}" var="dur">
					<li>${dur}</li>
				</c:forEach>
			</ul>
		</c:if>

		<c:if test="${!empty invalidHikeID}">
			<p>The Hike ID of ${invalidHikeID} is not valid. Please choose a
				valid hike.</p>
		</c:if>

		<c:if test="${!empty invalidPartySizeModel}">
			<p>The party size of ${invalidPartySizeModel.actualSize} is
				not valid. Please choose a size between
				${invalidPartySizeModel.minSize} and
				${invalidPartySizeModel.maxSize}</p>
		</c:if>

		<c:if test="${!empty invalidDateService}">
			<p>The specified date was not valid for the following reason:
				${invalidDateService.details}</p>
		</c:if>
		</div>
		<hr>
		<a href="RequestQuote"><span style="color: green; padding-right: 5px;">$</span>Try request again?</a>
		
		<%@ include file="SiteFooter.jsp"%>
	</div>
</body>
</html>