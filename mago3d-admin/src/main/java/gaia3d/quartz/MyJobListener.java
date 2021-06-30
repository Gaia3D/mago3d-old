package gaia3d.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class MyJobListener implements JobListener {

    @Override
    public String getName() {
        return MyJobListener.class.getName();
    }

    /**
     * Job이 수행되기 전 상태
     *   - TriggerListener.vetoJobExecution == false
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        System.out.println(String.format("[%-18s][%s] 작업시작", "jobToBeExecuted", context.getJobDetail().getKey().toString()));
    }

    /**
     * Job이 중단된 상태
     *   - TriggerListener.vetoJobExecution == true
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        System.out.println(String.format("[%-18s][%s] 작업중단", "jobExecutionVetoed", context.getJobDetail().getKey().toString()));
    }

    /**
     * Job 수행이 완료된 상태
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        System.out.println(String.format("[%-18s][%s] 작업완료", "jobWasExecuted", context.getJobDetail().getKey().toString()));
    }
}
