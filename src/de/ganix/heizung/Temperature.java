package de.ganix.heizung;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Temperature.
 */
public class Temperature {

	/** The indoor sensor id. */
	private static String indoorSensorId;

	/** The flow line sensor id. */
	private static String flowLineSensorId;
	
	/** The outdoor sensor id. */
	private static String outdoorSensorId;
	
	/** The owfs path. */
	private static String owfsPath; 
	
	/** The temperature file. */
	private static String temperatureFile; 
	
	/** The speicher. */
	private Float speicher = Float.MIN_VALUE;

	/** The kondensation. */
	private Float kondensation = Float.MIN_VALUE;

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(Temperature.class);

	static {
			ResourceBundle bundle = ResourceBundle.getBundle("de.ganix.heizung.heizung");

			owfsPath = bundle.getString("owfs.path");
			logger.info("owfsPath: {}", owfsPath);
			
			indoorSensorId = bundle.getString("owfs.sensor.indoor.id");
			logger.info("indoor: {}", indoorSensorId);
			
			flowLineSensorId = bundle.getString("owfs.sensor.flowline.id");
			logger.info("flowline: {}", flowLineSensorId);

			outdoorSensorId = bundle.getString("owfs.sensor.outdoor.id");
			logger.info("outdoor: {}", outdoorSensorId);

			temperatureFile = bundle.getString("owfs.temperature.file");
			logger.info("temperature file: {}", temperatureFile);
	}
	
	/**
	 * Gets the vorlauf from owfs.
	 *
	 * @param device the device
	 * @return the vorlauf owfs
	 */
	private Float getOwfsTemperature(String device) {
		Float thisTemp = null;
		try {
			String file = owfsPath + device + "/" + temperatureFile;
			BufferedReader buf;
			buf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String value = buf.readLine();
			thisTemp = Float.parseFloat(value);
		} catch (IOException e) {
			logger.error("error getting vorlauf via owfs: {}", e.getMessage());
		}
		return thisTemp;
	}

	/**
	 * Gets the speicher.
	 * 
	 * @return the speicher
	 */
	public Float getSpeicher() {
		return speicher;
	}

	/**
	 * Gets the aussen.
	 * 
	 * @return the aussen
	 */
	public Float getAussen() {
		return getOwfsTemperature(outdoorSensorId);
	}

	/**
	 * Gets the kondensation.
	 * 
	 * @return the kondensation
	 */
	public Float getKondensation() {
		return kondensation;
	}

	/**
	 * Gets the vorlauf from owfs.
	 *
	 * @return the vorlauf owfs
	 */
	public Float getVorlauf() {
		return getOwfsTemperature(flowLineSensorId);
	}

	/**
	 * Gets the innen.
	 *
	 * @return the innen
	 */
	public Float getInnen() {
		return getOwfsTemperature(indoorSensorId);
	}
}
