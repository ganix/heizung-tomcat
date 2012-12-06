package de.ganix.heizung;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ganix.heizung.ModulationJob.State;
import de.ganix.heizung.RegulateJob.RegulateState;

/**
 * Servlet implementation class StartHeizung.
 */
@WebServlet("/StartHeizung")
public class StartHeizung extends HttpServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(StartHeizung.class);

	/** The scheduler. */
	private static Scheduler scheduler;
	
	private static final JobKey regulateJobKey = new JobKey("regulate");
	
	private static final JobKey pwmJobKey = new JobKey("pwm");

	/**
	 * Instantiates a new start heizung.
	 *
	 * @see HttpServlet#HttpServlet()
	 */
	public StartHeizung() {
		super();
	}

	/**
	 * Inits the.
	 *
	 * @param config the config
	 * @throws ServletException the servlet exception
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		logger.info("initializing config");
		
		Config heizungConfig = Config.restore();
		heizungConfig.clearChanged();
		Config.setInstance(heizungConfig);
		config.getServletContext().setAttribute("cfg", heizungConfig);
		
		logger.info("initializing scheduler");
		try {
			System.setProperty("org.quartz.threadPool.threadCount", "1");
			Scheduler newScheduler;
			// Grab the Scheduler instance from the Factory
			newScheduler = StdSchedulerFactory.getDefaultScheduler();

			// and start it off
			newScheduler.start();
			
			addRegulateJob(newScheduler, heizungConfig.getInterval());
			addModulationJob(newScheduler);
			
			scheduler = newScheduler;
		} catch (SchedulerException se) {
			logger.error("error initing scheduler", se);
		}
		logger.info("done initializing scheduler");
	}

	/**
	 * Reset job interval.
	 *
	 * @param newInterval the new interval in seconds
	 */
	public synchronized static void resetJobInterval(int newInterval) {
		try {
			if(scheduler != null) {
				if(scheduler.checkExists(regulateJobKey))
					scheduler.deleteJob(regulateJobKey);
				addRegulateJob(scheduler, newInterval);
				logger.info("changed job interval");
			}
		} catch (SchedulerException e) {
			logger.error("error rescheduling job", e);
		}
	}

	/**
	 * Adds the regulate job.
	 *
	 * @param scheduler the scheduler
	 * @throws SchedulerException the scheduler exception
	 */
	private static void addRegulateJob(Scheduler scheduler, int interval) throws SchedulerException {
		// define the job and tie it to our class
		JobDetail job = newJob(RegulateJob.class)
				.withIdentity(regulateJobKey)
				.build();
		
		// Trigger the job to run now, and then repeat every n seconds
		Trigger trigger = newTrigger()
				.withSchedule(repeatSecondlyForever(interval))
				.startNow()
				.build();

		// Tell quartz to schedule the job using our trigger
		scheduler.scheduleJob(job, trigger);
	}
	
	private static void addModulationJob(Scheduler scheduler) throws SchedulerException {
		// define the job and tie it to our HelloJob class
		JobDetail job = newJob(ModulationJob.class)
				.withIdentity(pwmJobKey)
				.build();

		// Trigger the job to run now, and then repeat every n seconds
/*		Trigger trigger = newTrigger()
				.withPriority(10)
				.startNow()
				.build();
*/
		Trigger trigger = newTrigger()
			    .withSchedule(simpleSchedule().repeatForever().withIntervalInMilliseconds(500))
			    .withPriority(10)
			    .startNow()
			    .build();

		// Tell quartz to schedule the job using our trigger
		scheduler.scheduleJob(job, trigger);
	}
	
	public static synchronized void setModulationDutyTime(float duty) {
		State state = getModulationJobState();
		if(state != null)
			state.duty = duty;
	}
	
	private static State getModulationJobState() {
		try {
			Object stateObj;
			stateObj = scheduler.getJobDetail(pwmJobKey).getJobDataMap().get("state");
			State state = (State) stateObj;
			return state;
		} catch (SchedulerException e) {
			logger.error("cannot get state", e);
		}		
		return null;
	}

	private static RegulateState getRegulateJobState() {
		try {
			Object stateObj;
			stateObj = scheduler.getJobDetail(regulateJobKey).getJobDataMap().get("state");
			RegulateState state = (RegulateState) stateObj;
			return state;
		} catch (SchedulerException e) {
			logger.error("cannot get state", e);
		}	
		return null;
	}
	/**
	 * Destroy.
	 *
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		logger.info("destroyed servlet");

		try {
			scheduler.shutdown();
		} catch (SchedulerException se) {
			logger.error("error shutting down scheduler", se);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.error("interrupted", e);
		}
		logger.info("scheduler shut down");
	}

	public static void setSetpointTemperature(float setpointTemperature) {
		RegulateState state = getRegulateJobState();
		if(state != null) {
			state.setpointTemperature = setpointTemperature;
		} else {
			logger.error("cant get regulate state");
		}
	}

}
