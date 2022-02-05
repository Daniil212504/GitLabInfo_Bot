package telegramBot.jobs;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import telegramBot.handlers.ExceptionHandler;

import static java.lang.Boolean.parseBoolean;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class JobsScheduler {
    private Scheduler scheduler;

    public JobsScheduler() {
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public JobsScheduler setJob(JobDetail job, Trigger trigger) {
        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            ExceptionHandler.handleException(e);
        }
        return this;
    }

    public static <T extends Job> JobDetail createJob(Class<T> jobClass, String jobName, String jobGroup, String parameterKey, String parameter, String enabledMainProjects) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .usingJobData("enabledMainProjects", parseBoolean(enabledMainProjects))
                .usingJobData(parameterKey, parameter)
                .build();
    }

    public static <T extends Job> JobDetail createJob(Class<T> jobClass, String jobName, String jobGroup, String enabledMainProjects) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .usingJobData("enabledMainProjects", parseBoolean(enabledMainProjects))
                .build();
    }

    public static CronTrigger createTrigger(String triggerName, String cronExpression, String jobName, String jobGroup) {
        return newTrigger()
                .withIdentity(triggerName)
                .withSchedule(cronSchedule(cronExpression))
                .forJob(jobName, jobGroup)
                .build();
    }

}
