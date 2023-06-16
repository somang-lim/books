package com.books.app.job.makeRebateData;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import com.books.app.order.entity.OrderItem;
import com.books.app.order.repository.OrderItemRepository;
import com.books.app.rebate.entity.RebateOrderItem;
import com.books.app.rebate.repository.RebateOrderItemRepository;
import com.books.util.Ut;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MakeRebateDataJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	private final OrderItemRepository orderItemRepository;
	private final RebateOrderItemRepository rebateOrderItemRepository;


	@Bean
	public Job makeRebateDataJob(Step makeRebateDataStep, CommandLineRunner initData) throws Exception {
		initData.run();

		return jobBuilderFactory.get("makeRebateDataJob")
			.start(makeRebateDataStep)
			.build();
	}

	@Bean
	@JobScope
	public Step makeRebateDataStep(
		ItemReader orderItemReader,
		ItemProcessor orderItemToRebateOrderItemProcessor,
		ItemWriter rebateOrderItemWriter
	) {
		return stepBuilderFactory.get("makeRebateDataStep1")
			.<OrderItem, RebateOrderItem>chunk(100)
			.reader(orderItemReader)
			.processor(orderItemToRebateOrderItemProcessor)
			.writer(rebateOrderItemWriter)
			.build();
	}

	@StepScope
	@Bean
	public RepositoryItemReader<OrderItem> orderItemReader(
		@Value("#{jobParameters['yearMonth']}") String yearMonth
	) {
		int monthEndDay = Ut.date.getEndDayOf(yearMonth);
		LocalDateTime fromDate = Ut.date.parse(yearMonth + "-01 00:00:00.000000");
		LocalDateTime toDate = Ut.date.parse(yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay));

		return new RepositoryItemReaderBuilder<OrderItem>()
			.name("orderItemReader")
			.repository(orderItemRepository)
			.methodName("findAllByPayDateBetween")
			.pageSize(100)
			.arguments(Arrays.asList(fromDate, toDate))
			.sorts(Collections.singletonMap("id", Sort.Direction.ASC))
			.build();
	}

	@StepScope
	@Bean
	public ItemProcessor<OrderItem, RebateOrderItem> orderItemToRebateOrderItemProcessor() {
		return orderItem -> new RebateOrderItem(orderItem);
	}

	@StepScope
	@Bean
	public ItemWriter<RebateOrderItem> rebateOrderItemWriter() {
		return items -> items.forEach(item -> {
			RebateOrderItem oldRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(item.getOrderItem().getId()).orElse(null);

			if (oldRebateOrderItem != null ) {
				if ( oldRebateOrderItem.isRebateDone() ) {
					return;
				}

				rebateOrderItemRepository.delete(oldRebateOrderItem);
			}

			rebateOrderItemRepository.save(item);
		});
	}
}
