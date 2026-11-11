/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.odata.filter.expression.field.predicate.provider;

import com.liferay.object.odata.filter.expression.field.predicate.provider.FieldPredicateProvider;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;

/**
 * @author Daniel Raposo
 */
@Component(
	property = {
		"field.predicate.provider.key=dateCreated",
		"field.predicate.provider.key=dateModified"
	},
	service = FieldPredicateProvider.class
)
public class SystemDateFieldPredicateProvider
	implements FieldPredicateProvider {

	@Override
	public Predicate getBinaryExpressionPredicate(
			Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
			Object left, long objectDefinitionId,
			BinaryExpression.Operation operation, Object right)
		throws ExpressionVisitException {

		Expression<Object> expression =
			(Expression<Object>)objectDefinitionColumnSupplier.apply(
				String.valueOf(left));

		if (right == null) {
			if (operation == BinaryExpression.Operation.EQ) {
				return expression.isNull();
			}
			else if (operation == BinaryExpression.Operation.NE) {
				return expression.isNotNull();
			}
		}

		return _getDateTimePredicate((Date)right, expression, operation);
	}

	@Override
	public Predicate getContainsPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		String fieldName, long objectDefinitionId, Object fieldValue) {

		throw new UnsupportedOperationException(
			"Unsupported method getContainsPredicate for " +
				"dateCreated/dateModified fields");
	}

	@Override
	public Predicate getInPredicate(
			Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
			Object left, long objectDefinitionId, List<Object> rights)
		throws ExpressionVisitException {

		Predicate predicate = null;

		for (Object right : rights) {
			Predicate binaryExpressionPredicate = getBinaryExpressionPredicate(
				objectDefinitionColumnSupplier, left, objectDefinitionId,
				BinaryExpression.Operation.EQ, right);

			if (predicate == null) {
				predicate = binaryExpressionPredicate;

				continue;
			}

			predicate = predicate.or(binaryExpressionPredicate);
		}

		return Predicate.withParentheses(predicate);
	}

	@Override
	public Predicate getIsNotEmptyPredicate(
		String fieldName, long objectDefinitionId,
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier) {

		throw new UnsupportedOperationException(
			"Unsupported method getIsNotEmptyPredicate for " +
				"dateCreated/dateModified fields");
	}

	@Override
	public Predicate getStartsWithPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		String fieldName, long objectDefinitionId, Object fieldValue) {

		throw new UnsupportedOperationException(
			"Unsupported method getStartsWithPredicate for " +
				"dateCreated/dateModified fields");
	}

	private Predicate _getDateTimePredicate(
		Date date, Expression<Object> expression,
		BinaryExpression.Operation operation) {

		Date truncatedDate = _processDate(
			calendar -> calendar.set(Calendar.MILLISECOND, 0), date);

		if (operation == BinaryExpression.Operation.EQ) {
			return expression.gte(
				truncatedDate
			).and(
				expression.lt(_incrementSecond(truncatedDate))
			).withParentheses();
		}
		else if (operation == BinaryExpression.Operation.GE) {
			return expression.gte(truncatedDate);
		}
		else if (operation == BinaryExpression.Operation.GT) {
			return expression.gte(_incrementSecond(truncatedDate));
		}
		else if (operation == BinaryExpression.Operation.LE) {
			return expression.lt(_incrementSecond(truncatedDate));
		}
		else if (operation == BinaryExpression.Operation.LT) {
			return expression.lt(truncatedDate);
		}
		else if (operation == BinaryExpression.Operation.NE) {
			return expression.lt(
				truncatedDate
			).or(
				expression.gte(_incrementSecond(truncatedDate))
			).withParentheses();
		}

		return null;
	}

	private Date _incrementSecond(Date date) {
		return _processDate(calendar -> calendar.add(Calendar.SECOND, 1), date);
	}

	private Date _processDate(Consumer<Calendar> consumer, Date date) {
		Calendar calendar = CalendarFactoryUtil.getCalendar(date.getTime());

		consumer.accept(calendar);

		return calendar.getTime();
	}

}