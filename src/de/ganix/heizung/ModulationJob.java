package de.ganix.heizung;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class ModulationJob implements Job {

	private static Logger logger = LoggerFactory.getLogger(ModulationJob.class);

	public class State {
		public float duty = 0.0f;
		public int cycleDuration = 10;
		public Ausgang ausgang = new Ausgang();
		public int step = 0;		
		public boolean stateLast = false;
		public boolean state = false;		
	}

	@Override
	public synchronized void execute(JobExecutionContext ctx) throws JobExecutionException {
		State state = restore(ctx);
		
		if(state.step < state.cycleDuration) {
			state.step++;
		} else {
			state.step = 0;
		}

		if(state.step < state.duty*state.cycleDuration) {
			state.state = true;
		} else {
			state.state = false;
		}

		if(state.state != state.stateLast) {
			state.stateLast = state.state;
			logger.debug("transition to {} in step {}", state.state, state.step);
			state.ausgang.setAusgang1(state.state?"an":"aus");
		}
	}

	private State restore(JobExecutionContext ctx) {
		String key = "state";
		State state;
		Object restoredStateObj = ctx.getJobDetail().getJobDataMap().get(key);
		if(State.class.isInstance(restoredStateObj)) {
			state = (State) restoredStateObj;
		} else {
			state = new State();
			ctx.getJobDetail().getJobDataMap().put(key, state);
			logger.debug("created new state");
		}
		return state;
	}
}
