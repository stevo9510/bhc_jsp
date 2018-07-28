/**
 * Copyright 2018
 * Steven Anderson
 * All rights reserved
 * 
 * Homework 10 - LocalHikeService
 * LocalHikeService.java - In-memory/local Implementation of HikeService. 
 * 
 * 07/27/2018 - Initial.
 */
package anderson.bhcquotesv3.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.rbevans.bookingrate.Rates;

import anderson.bhcquotesv3.model.HikeModel;

public class LocalHikeService implements HikeService {

	// Constants for the valid durations for the hike options.
	// Note: This information is also within Rates.java, but requires an instance of
	// the object to be created first. Constants were preferred in this case, albeit
	// redundant.
	private static final List<Integer> GARINDER_DURATIONS = Collections.unmodifiableList(Arrays.asList(3, 5));
	private static final List<Integer> HELLROARING_DURATIONS = Collections.unmodifiableList(Arrays.asList(2, 3, 4));
	private static final List<Integer> BEATEN_DURATIONS = Collections.unmodifiableList(Arrays.asList(5, 7));

	// List of helper objects with attributes related to the hike types.  
	private static final HikeModel[] hikeViewModels = {
			new HikeModel(Rates.HIKE.GARDINER, 0, "Gardiner Lake", "images\\morning_lake2.png", GARINDER_DURATIONS,
					"onGardinerSelected()"),
			new HikeModel(Rates.HIKE.HELLROARING, 1, "Hellroaring Plateau", "images\\sunset2.png",
					HELLROARING_DURATIONS, "onHellroaringSelected()"),
			new HikeModel(Rates.HIKE.BEATEN, 2, "Beaten Path", "images\\wilderness2.jpg", BEATEN_DURATIONS,
					"onBeatenPathSelected()") };

	/**
	 * Get all the HikeModels from in-memory database
	 */
	@Override
	public HikeModel[] getAllHikeModels(){
		return hikeViewModels;
	}
	

	/**
	 * Get HikeModel from passed HikeID.   
	 */
	@Override
	public Optional<HikeModel> getHikeModelFromID(int hikeID) {
		Optional<HikeModel> optHikeViewModel = Arrays.asList(hikeViewModels).stream()
				.filter(vm -> vm.getHikeID() == hikeID).findFirst();
		return optHikeViewModel;
	}
	

	/**
	 * Checks if the duration is valid for the passed hike model.  Assumes the hikeModel is not null.  
	 */
	@Override
	public boolean isValidHikeDuration(HikeModel hikeModel, int duration) {
		return hikeModel.getValidDurations().contains(duration);
	}
	
	
	
} 
