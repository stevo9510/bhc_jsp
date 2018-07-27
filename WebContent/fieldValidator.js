/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 8 - BHC Hike Quotes
 * fieldValidator.js - Handles client side validation of form input fields and ensuring proper selections could be performed.
 * 
 * 07/02/2018
 */

// Durations to be treated as constants 
var GARDINER_DURATIONS = [3,5];
var HELLROARING_DURATIONS = [2,3,4];
var BEATEN_DURATIONS = [5,7];

// when gardiner lake hike type is selected, use its durations 
function onGardinerSelected(){
	addOptionsToDuration(GARDINER_DURATIONS);
}

// when hellroaring plateau is selected, use its durations 
function onHellroaringSelected(){
	addOptionsToDuration(HELLROARING_DURATIONS);
}

// when beaten path is selected, use its durations
function onBeatenPathSelected(){
	addOptionsToDuration(BEATEN_DURATIONS);
}

// clear out durations and add new options (values parameter) 
function addOptionsToDuration(values){
	var select = document.getElementById('duration');
	// remove all items
	while(select.options.length > 0) {
		select.remove(0);
	}
	
	// add all new items
	values.forEach(function (val) {
		var opt = document.createElement('option');
		opt.value = val;
		opt.innerHTML = val;
		select.appendChild(opt);
	});
}

/*
 * Validate the form input that is not protected by the input type
 * @returns true if all fields are valid; otherwise, false
 */
function validateForm() {
	var radioValue = $("input[name='hike']:checked").val();
	if(!radioValue){
		alert("A hike must be selected to request a quote.");
		return false;
	}		
	
	var currentDate = $('#datepicker').datepicker("getDate");
	if(!currentDate){
		alert("A date must be selected to request a quote.");
		return false;
	}	
	
	// note: the remaining fields validation are covered by their input types.
	// all fields are covered by server side validation
	
	return true;
}

