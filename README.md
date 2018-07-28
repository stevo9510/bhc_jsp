# bhc_jsp
Steven Anderson
Homework 10
MVC Website for Requesting Quotes for BHC

Package Information: 
- anderson.bhcquotev3.controller
   - Contains MVC Controllers for dispatching requests, setting request attributes, and handling parameters.
- anderson.bhcquotesv3.model
   - Contains View Model/Bean objects for binding to .jsp View files.
- anderson.bhcquotesv3.service 
   - Contains service/repository classes to handle data access-esque functionality or business logic.
   - Interfaced out to support future implementations as required.
   
App Workflow:
 - RequestQuoteController.java serves up RequestQuote.jsp 
 - RequestQuote.jsp contains form for submitting BHC quote request
 - Form submits to QuoteResultsController.java with parameters to dispatch either ErrorResults.jsp or CostResults.jsp, depending
   on the validity of the quote request parameters
   
Other note:
 - Changed up design of previous site to have separate pages for the Request and Results now.  Although I think this is less "user-friendly",
   it allowed me to fully exercise and illustrate an MVC architecture (with a relatively complex QuoteResultsController).
     

