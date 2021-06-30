package gaia3d.service.impl;

import gaia3d.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

/**
 * Schedule 관련 처리
 * @author hansang
 *
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	@Autowired
	private QuartzProperties quartzProperties;
	/*@Autowired
	private TriggerListener triggersListener;
	@Autowired
	private JobListener jobsListener;


	@Bean
	public TriggerListener triggerListener(){


	}

	@Bean
	public JobListener jobsListener(){


	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext) {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		schedulerFactoryBean.setJobFactory(jobFactory);

		schedulerFactoryBean.setApplicationContext(applicationContext);

		Properties properties = new Properties();
		properties.putAll(quartzProperties.getProperties());

		schedulerFactoryBean.setGlobalTriggerListeners(triggersListener);
		schedulerFactoryBean.setGlobalJobListeners(jobsListener);
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		schedulerFactoryBean.setQuartzProperties(properties);
		schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
		return schedulerFactoryBean;
	}*/

}
