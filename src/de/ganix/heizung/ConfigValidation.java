package de.ganix.heizung;

public class ConfigValidation {

	private String interval;

	private String maxGraphSamples;
	
	public String getInterval() {
		return interval;
	}

	public String getMaxGraphSamples() {
		return maxGraphSamples;
	}

	public String getSetpointTemperature() {
		return setpointTemperature;
	}

	private String setpointTemperature;
	
	private String intervalError = "";

	public String getIntervalError() {
		return intervalError;
	}

	public String getMaxGraphSamplesError() {
		return maxGraphSamplesError;
	}

	public String getSetpointTemperatureError() {
		return setpointTemperatureError;
	}

	private String maxGraphSamplesError = "";
	
	private String setpointTemperatureError = "";
		
	public void setInterval(String interval) {
		this.interval = interval;
	}

	public void setMaxGraphSamples(String maxGraphSamples) {
		this.maxGraphSamples = maxGraphSamples;
	}

	public void setSetpointTemperature(String setpointTemperature) {
		this.setpointTemperature = setpointTemperature;
	}
	
	public boolean isValid() {
		boolean result = true;

		if(!isValidInt(interval)) {
			intervalError = "muss eine ganze Zahl sein!";
			result = false;
		}

		if(!isValidInt(maxGraphSamples)) {
			maxGraphSamplesError = "muss eine ganze Zahl sein!";
			result = false;
		}

		if(!isValidFloat(setpointTemperature)) {
			setpointTemperatureError = "muss eine Dezimalzahl sein";
			result = false;
		}
		
		return result;
	}
	
	private boolean isValidInt(String val) {
		try {
			Integer.decode(val);
		} catch (Exception e) {
			return false;
		}			
		return true;
	}

	private boolean isValidFloat(String val) {
		try {
			Float.parseFloat(val);
		} catch (Exception e) {
			return false;
		}			
		return true;
	}

}
