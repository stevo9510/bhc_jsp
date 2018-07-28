/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 10 - RequestQuoteController 
 * RequestQuoteController.java - MVC Servlet Controller that is used to serve up a form to submit a quote/cost request for BHC.
 * 
 * 07/27/2018 - Initial
 */

package anderson.bhcquotesv3.controller;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import anderson.bhcquotesv3.model.HikeModel;
import anderson.bhcquotesv3.service.HikeService;
import anderson.bhcquotesv3.service.LocalHikeService;

@WebServlet("/RequestQuote")
public class RequestQuoteController extends HttpServlet {

	private static final long serialVersionUID = 4516654360375493301L;

	// Service for getting hike information to help render html
	private HikeService hikeService;
	
	public RequestQuoteController() {
		// Use a local (in memory) hike service here.    
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
	 * Primary method. Will serve up RequestQuote jsp page after retrieving the hike view models from the hike service.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HikeModel[] hikeViewModels = hikeService.getAllHikeModels();
		request.setAttribute("hikeViewModels", hikeViewModels);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/RequestQuote.jsp");
		dispatcher.forward(request, response);
	}
}
