package de.ganix.heizung;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Ausgang.
 */
public class Ausgang {

	/** The owfs path. */
	private static String owfsPath;

	/** The switch id. */
	private static String switchId;

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(Ausgang.class);

	/** The heater1 file. */
	private static String heater1File;

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("de.ganix.heizung.heizung");

		owfsPath = bundle.getString("owfs.path");
		logger.info("owfsPath: {}", owfsPath);
		
		switchId = bundle.getString("owfs.switch.id");
		logger.info("switchId: {}", switchId);

		heater1File = bundle.getString("owfs.switch.heater1.file");
		logger.info("switchId: {}", heater1File);
	}

	/**
	 * Gets the ausgang1.
	 *
	 * @return the ausgang1
	 */
	public String getAusgang1() {
		String pio1File = owfsPath + switchId + "/" + heater1File;
		return "1".equals(readLine(pio1File)) ? "an" : "aus";
	}

	/**
	 * Sets the ausgang1.
	 *
	 * @param ausgang the new ausgang1
	 */
	public void setAusgang1(String ausgang) {
		logger.debug("setAusgang1 called with {}", ausgang);
		String pio1File = owfsPath + switchId + "/" + heater1File;
		String writtenValue = "an".equals(ausgang) ? "1" : "0";
		writeLine(pio1File, writtenValue);
		logger.debug("written value {} to {}", writtenValue, pio1File);
	}

	/**
	 * Read line.
	 *
	 * @param file the file
	 * @return the string
	 */
	private String readLine(String file) {
		String value = null;
		try {
			BufferedReader buf;
			buf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			value = buf.readLine();
			buf.close();
		} catch (IOException e) {
			logger.error("error reading from {} : {}", file, e.getMessage());
		}
		return value;
	}

	/**
	 * Write line.
	 *
	 * @param fileName the file name
	 * @param line the line
	 */
	private void writeLine(String fileName, String line) {
		File file = new File(fileName);
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			writer.write(line);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			logger.error("error writing to {} : {}", file, e.getMessage());
		}		
	}

}
