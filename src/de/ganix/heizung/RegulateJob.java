package de.ganix.heizung;

import java.util.LinkedList;
import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class RegulateJob implements Job {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(RegulateJob.class);

	private static final int maxHistorySize = 10;
	private static final float kp = 0.05f;
	private static final float kd = 0.05f;
	private static final float ki = 0.05f;

	public class RegulateState {
		public float setpointTemperature;
		Temperature temperature = new Temperature();
		List<Float> errors = new LinkedList<Float>();
	}

	private RegulateState regulateState;

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {

		regulateState = restoreRegulateState(ctx);

		// logger.debug("Hello World!");
		Float outdoor = regulateState.temperature.getAussen();
		Float innen = regulateState.temperature.getInnen();
		if (outdoor == null)
			outdoor = 0f;
		if (innen == null)
			innen = 0f;
		TemperatureHistory.getInstance().addSample(innen, outdoor);
		TemperatureChart.updateChart();

		applyPid(innen);
	}

	private RegulateState restoreRegulateState(JobExecutionContext ctx) {
		RegulateState regulateState;
		String key = "state";
		Object temperatureObj = ctx.getJobDetail().getJobDataMap().get(key);
		if (temperatureObj != null) {
			regulateState = (RegulateState) temperatureObj;
		} else {
			regulateState = new RegulateState();
			ctx.getJobDetail().getJobDataMap().put(key, regulateState);
		}
		return regulateState;
	}

	private void applyPid(Float innen) {
		float actuating = 0;
		float proportional;
		proportional = Config.getInstance().getSetpointTemperature() - innen;
		logger.debug("desired, actual: {}, {}", Config.getInstance().getSetpointTemperature(), innen);

		addError(proportional);
		float differential = calculateDifferential();
		float integral = calculateIntegral();

		actuating = proportional * kp;
		actuating += integral * ki;
		actuating += differential * kd;

		logger.debug("p,i,d: {}", new float[] {proportional, integral, differential});
		
		logger.debug("actuating: {}", actuating);
		
		StartHeizung.setModulationDutyTime(actuating);
	}

	private void addError(float error) {
		regulateState.errors.add(error);

		if (regulateState.errors.size() > maxHistorySize) {
			regulateState.errors.remove(0);
		}
	}

	private float calculateDifferential() {
		float differential = 0;
		if (regulateState.errors.size() > 1) {
			float last = regulateState.errors.get(regulateState.errors.size()-1);
			float preLast = regulateState.errors.get(regulateState.errors.size()-2);
			differential = last - preLast;
		}
		return differential;
	}

	private float calculateIntegral() {
		float integral = 0;
		for (float error : regulateState.errors) {
			integral += error / maxHistorySize;
		}
		return integral;
	}

}
