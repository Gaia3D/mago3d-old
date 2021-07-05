package gaia3d.domain;

import gaia3d.quartz.jobExecutor.MembershipJobExecutor;

public enum JobExecutor {

	// JOB EXECUTOR
	TEST(MembershipJobExecutor.class);

	private Class value;

	JobExecutor(Class value) {
		this.value = value;
	}

	public Class getValue() {
		return this.value;
	}

	public static JobExecutor findBy(Class value) {
		for(JobExecutor jobExecutor : values()) {
			if(jobExecutor.getValue().equals(value)) return jobExecutor;
		}
		return null;
	}

}
