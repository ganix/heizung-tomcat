package de.ganix.heizung;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Config.
 */
@SuppressWarnings("serial")
public class Config implements Serializable {

	/** The Constant HEATING_CONFIG_WIN. */
	private static final String HEATING_CONFIG_WIN = "d:/tmp/heating.config";

	/** The Constant HEATING_CONFIG_UX. */
	private static final String HEATING_CONFIG_UX = "/tmp/heating.config";

	/** The interval. */
	private int interval = 10;
	
	private boolean intervalChanged = false;

	/** The max graph samples. */
	private int maxGraphSamples = 1000;
	
	private boolean maxGraphSamplesChanged = false;

	/** The setpoint temperature. */
	private float setpointTemperature = 22;

	private boolean setpointTemperatureChanged = false;

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(Config.class);

	/** The singleton config. */
	private static Config singletonConfig;

	/**
	 * Gets the interval.
	 * 
	 * @return the interval
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * Sets the interval.
	 * 
	 * @param newInterval
	 *            the new interval
	 */
	public void setInterval(int newInterval) {
		if(interval != newInterval) {
			interval = newInterval;
			intervalChanged = true;
		}
	}

	/**
	 * Persist.
	 */
	public void persist() {
		try {
			FileOutputStream os;
			try {
				logger.debug("saving {}", HEATING_CONFIG_UX);
				os = new FileOutputStream(HEATING_CONFIG_UX);
			} catch (FileNotFoundException e) {
				logger.debug("saving {}", HEATING_CONFIG_WIN);
				os = new FileOutputStream(HEATING_CONFIG_WIN);
			}
			XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(os));
			encoder.writeObject(this);
			encoder.close();
			logger.debug("done persisting config");
		} catch (FileNotFoundException e) {
			logger.error("error persisting config", e);
		}
	}

	/**
	 * Restore.
	 * 
	 * @return the config
	 */
	public static Config restore() {
		Config config = null;
		try {
			FileInputStream is;
			try {
				logger.debug("loading {}", HEATING_CONFIG_UX);
				is = new FileInputStream(HEATING_CONFIG_UX);
			} catch (FileNotFoundException e) {
				logger.debug("loading {}", HEATING_CONFIG_WIN);
				is = new FileInputStream(HEATING_CONFIG_WIN);
			}
			XMLDecoder decoder = new XMLDecoder(is);
			config = (Config) decoder.readObject();
			decoder.close();
			logger.debug("done restoring config");
		} catch (FileNotFoundException e) {
			logger.error("error restoring config", e);
			logger.info("creating new config");
			config = new Config();
		}
		return config;
	}

	/**
	 * Gets the single instance of Config.
	 * 
	 * @return single instance of Config
	 */
	public static Config getInstance() {
		if (singletonConfig != null)
			return singletonConfig;
		else
			return new Config();
	}
	
	/**
	 * Sets the single instance of Config.
	 * 
	 */
	public static void setInstance(Config config) {
		singletonConfig = config;
	}
	
	public void apply() {
		if (intervalChanged) {
			StartHeizung.resetJobInterval(interval);
			intervalChanged = false;
		}
		if (setpointTemperatureChanged) {
			StartHeizung.setSetpointTemperature(setpointTemperature);
			setpointTemperatureChanged = false;
		}
		if(maxGraphSamplesChanged) {
			maxGraphSamplesChanged = false;
		}
	}
	
	public void clearChanged() {
		intervalChanged = false;
		setpointTemperatureChanged = false;
		maxGraphSamplesChanged = false;
	}

	/**
	 * Gets the max graph samples.
	 *
	 * @return the max graph samples
	 */
	public int getMaxGraphSamples() {
		return maxGraphSamples;
	}

	/**
	 * Sets the max graph samples.
	 *
	 * @param maxGraphSamples the new max graph samples
	 */
	public void setMaxGraphSamples(int maxGraphSamples) {
		if(this.maxGraphSamples != maxGraphSamples) {
			this.maxGraphSamples = maxGraphSamples;
			maxGraphSamplesChanged = true;
		}
	}

	/**
	 * Gets the setpoint temperature.
	 *
	 * @return the setpoint temperature
	 */
	public float getSetpointTemperature() {
		return setpointTemperature;
	}

	/**
	 * Sets the setpoint temperature.
	 *
	 * @param setpointTemperature the new setpoint temperature
	 */
	public void setSetpointTemperature(float setpointTemperature) {
		if(this.setpointTemperature != setpointTemperature) {
			this.setpointTemperature = setpointTemperature;
			setpointTemperatureChanged = true;
		}
	}
}
