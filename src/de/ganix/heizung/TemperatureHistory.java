package de.ganix.heizung;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TemperatureHistory.
 */
public class TemperatureHistory {

	/** The this singleton. */
	private static TemperatureHistory thisSingleton;
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(StartHeizung.class);

	/**
	 * Gets the single instance of TemperatureHistory.
	 *
	 * @return single instance of TemperatureHistory
	 */
	public static TemperatureHistory getInstance() {
		if(thisSingleton == null){
			thisSingleton = new TemperatureHistory();
		}
		return thisSingleton;
	}
	
	/**
	 * The Class TemperatureSample.
	 */
	class TemperatureSample {

		/** The time. */
		private Timestamp time;

		/**
		 * Gets the time.
		 * 
		 * @return the time
		 */
		public Timestamp getTime() {
			return time;
		}

		/**
		 * Sets the time.
		 * 
		 * @param time
		 *            the new time
		 */
		public void setTime(Timestamp time) {
			this.time = time;
		}

		/**
		 * Gets the value.
		 * 
		 * @return the value
		 */
		public float getValue() {
			return value;
		}

		/**
		 * Sets the value.
		 * 
		 * @param value
		 *            the new value
		 */
		public void setValue(float value) {
			this.value = value;
		}

		/** The value. */
		private float value;

		/**
		 * Instantiates a new temperature sample.
		 * 
		 * @param temp
		 *            the temp
		 */
		public TemperatureSample(float temp) {
			value = temp;
			time = new Timestamp(System.currentTimeMillis());
		}
	}

	/** The temps. */
	private List<TemperatureSample> tempsAussen = new Vector<TemperatureSample>();

	/** The temps. */
	private List<TemperatureSample> tempsInnen = new Vector<TemperatureSample>();

	/**
	 * Adds the sample.
	 *
	 * @param tempInnen the temp innen
	 * @param tempAussen the temp aussen
	 */
	public void addSample(float tempInnen, float tempAussen) {
		
		addSample(tempsAussen, tempAussen);
		addSample(tempsInnen, tempInnen);
	}

	/**
	 * Adds the sample.
	 *
	 * @param temps the temps
	 * @param temp the temp
	 */
	private synchronized void addSample(List<TemperatureSample> temps,
			float temp) {
		TemperatureSample sample = new TemperatureSample(temp);

		if(!contains(temps, sample)) {
			temps.add(sample);
		}

		while(temps.size()>Config.getInstance().getMaxGraphSamples()) {
			logger.debug("removing first element. size before: {}", temps.size());
			temps.remove(temps.get(0));
			logger.debug("removed first element. size after: {}", temps.size());
		}
		
	}

	/**
	 * Contains.
	 *
	 * @param temps the temps
	 * @param sample the sample
	 * @return true, if successful
	 */
	private boolean contains(List<TemperatureSample> temps, TemperatureSample sample) {
		for (TemperatureSample thisSample : temps) {
			if (thisSample.getTime().getTime() / 1000 == sample.getTime().getTime()/1000 ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the data set.
	 *
	 * @return the data set
	 */
	public TimeSeriesCollection getDataSet() {
		TimeSeriesCollection d = new TimeSeriesCollection();
        
		TimeSeries timeSeriesInnen = new TimeSeries("Innen");
		TimeSeries timeSeriesAuﬂen = new TimeSeries("Auﬂen");
		
        for (TemperatureSample sample : tempsInnen) {
        	Second second = new Second(new Date(sample.getTime().getTime()));
        	timeSeriesInnen.add(second, sample.getValue());        	
        }
        for (TemperatureSample sample : tempsAussen) {
        	Second second = new Second(new Date(sample.getTime().getTime()));
        	timeSeriesAuﬂen.add(second, sample.getValue());
        }

		d.addSeries(timeSeriesInnen);
		d.addSeries(timeSeriesAuﬂen);
        
        return d;
    }
}
