/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 10 - HikeService
 * HikeService.java - Interface for service to get hike related information. Implementations can be of various forms (local/in-memory, database, etc.) 
 * 
 * 07/27/2018 - Initial.
 */

package anderson.bhcquotesv3.service;

import java.util.Optional;

import anderson.bhcquotesv3.model.HikeModel;

public interface HikeService {

	/**
	 * Get all the hike models available.
	 * @return
	 */
	HikeModel[] getAllHikeModels();

	/**
	 * Search up the matching HikeModel based on the passed hikeID.  
	 * Returns as an Optional datatype in case the HikeModel cannot be found. 
	 * 
	 * @param hikeID
	 * @return
	 */
	Optional<HikeModel> getHikeModelFromID(Integer hikeID);

	/**
	 * Verifies if the passed duration is valid for the given hike model.  
	 * 
	 * @param hikeModel
	 * @param duration
	 * @return
	 */
	boolean isValidHikeDuration(HikeModel hikeModel, int duration);

}