package com.books.app.order.repository;

import static com.books.app.order.entity.QOrder.*;

import java.util.List;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<String> findByDateFormat_PayDate() {
		StringTemplate formattedDate = Expressions.stringTemplate(
			"DATE_FORMAT({0}, {1})"
			, order.payDate
			, ConstantImpl.create("%Y-%m")
		);

		return jpaQueryFactory
			.select(formattedDate)
			.from(order)
			.where(order.payDate.isNotNull())
			.groupBy(formattedDate)
			.orderBy(formattedDate.desc())
			.fetch();
	}

}
