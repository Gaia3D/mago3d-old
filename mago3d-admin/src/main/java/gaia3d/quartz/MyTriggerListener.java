package gaia3d.quartz;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

public class MyTriggerListener implements TriggerListener {

    public static final String EXECUTION_COUNT = "EXECUTION_COUNT";

    public String getName() {
        return MyTriggerListener.class.getName();
    }

    /**
     * Trigger가 실행된 상태
     * 리스너 중에서 가장 먼저 실행됨
     */
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        System.out.println(String.format("\n[%-18s][%s]", "triggerFired", trigger.getKey().toString()));
    }

    /**
     * Trigger 중단 여부를 확인하는 메소드
     * Job을 수행하기 전 상태
     *
     * 반환값이 false인 경우, Job 수행
     * 반환값이 true인 경우, Job을 수행하지않고 'SchedulerListtener.jobExecutionVetoed'로 넘어감
     *
     * Job 실행횟수가 3회이상이면 작업중단
     */
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        int executeCount = -1;
        if (map.containsKey(EXECUTION_COUNT)) {
            executeCount = map.getInt(EXECUTION_COUNT);
        }
        System.out.println(String.format("[%-18s][%s]", "vetoJobExecution", trigger.getKey().toString()));

        return executeCount >= 3;
    }

    /**
     * Trigger가 중단된 상태
     */
    public void triggerMisfired(Trigger trigger) {
        System.out.println(String.format("[%-18s][%s]", "triggerMisfired", trigger.getKey().toString()));
    }

    /**
     * Trigger가 완료된 상태
     */
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        System.out.println(String.format("[%-18s][%s]", "triggerComplete", trigger.getKey().toString()));
    }
}
