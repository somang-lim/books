package com.books.app.job;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobConfig {

	private final JobLauncher jobLauncher;
	private final Job makeRebateDataJob;

	@Scheduled(cron = "0 0 4 * * *")
	public void performMakeRebateDataJob() throws Exception {
		String yearMonth = getPerformMakeRebateDataJobParam1Value();

		JobParameters param = new JobParametersBuilder()
				.addString("yearMonth", yearMonth)
				.toJobParameters();

		JobExecution execution = jobLauncher.run(makeRebateDataJob, param);
		log.info("job status: " + execution.getStatus());
	}

	public String getPerformMakeRebateDataJobParam1Value() {
		LocalDateTime rebateDate = LocalDateTime.now().getDayOfMonth() >= 15 ? LocalDateTime.now().minusMonths(1) : LocalDateTime.now().minusMonths(2);

		return "%04d".formatted(rebateDate.getYear()) + "-" + "%02d".formatted(rebateDate.getMonthValue());
	}

}
