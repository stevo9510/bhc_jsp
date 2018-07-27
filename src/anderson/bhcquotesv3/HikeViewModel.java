package anderson.bhcquotesv3;

import java.util.List;

import com.rbevans.bookingrate.Rates;
import com.rbevans.bookingrate.Rates.HIKE;

public class HikeViewModel {

	private Rates.HIKE hikeType;
	private int hikeID;
	private String displayName;
	private String normalIconFilePath;
	private List<Integer> validDurations;
	private String onChangeJsFunctionName;
	
	public HikeViewModel(HIKE hikeType, int hikeID, String displayName, String normalIconFilePath, List<Integer> validDurations, String onChangeJsFunctionName) {
		this.hikeType = hikeType;
		this.hikeID = hikeID;
		this.displayName = displayName;
		this.normalIconFilePath = normalIconFilePath;
		this.validDurations = validDurations;
		this.onChangeJsFunctionName = onChangeJsFunctionName;
	}
	
	// For Java Bean support
	public HikeViewModel() {
		
	}
	
	public Rates.HIKE getHikeType() {
		return hikeType;
	}
	
	public int getHikeID() {
		return hikeID;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getNormalIconFilePath() {
		return normalIconFilePath;
	}
	
	public List<Integer> getValidDurations() {
		return validDurations;
	}
	
	public String getOnChangeJsFunctionName() {
		return onChangeJsFunctionName;
	}

	public void setHikeType(Rates.HIKE hikeType) {
		this.hikeType = hikeType;
	}

	public void setHikeID(int hikeID) {
		this.hikeID = hikeID;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setNormalIconFilePath(String normalIconFilePath) {
		this.normalIconFilePath = normalIconFilePath;
	}

	public void setValidDurations(List<Integer> validDurations) {
		this.validDurations = validDurations;
	}

	public void setOnChangeJsFunctionName(String onChangeJsFunctionName) {
		this.onChangeJsFunctionName = onChangeJsFunctionName;
	}
}
