/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.odata.filter.expression.field.predicate.provider;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.odata.filter.expression.field.predicate.provider.FieldPredicateProvider;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.MethodExpression;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio Jiménez del Coso
 */
@Component(
	property = "field.predicate.provider.key=" + ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST,
	service = FieldPredicateProvider.class
)
public class MultiselectPicklistFieldPredicateProvider
	implements FieldPredicateProvider {

	@Override
	public Predicate getBinaryExpressionPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		Object left, long objectDefinitionId,
		BinaryExpression.Operation operation, Object right) {

		Expression<String> expression =
			(Expression<String>)objectDefinitionColumnSupplier.apply(
				String.valueOf(left));

		if (Objects.equals(operation, BinaryExpression.Operation.EQ)) {
			expression = DSLFunctionFactoryUtil.concat(
				new Scalar<>(_SCALAR_EXPRESSION), expression,
				new Scalar<>(_SCALAR_EXPRESSION));

			return expression.like(
				_getFieldValueExpression(String.valueOf(right), null));
		}
		else if (Objects.equals(operation, BinaryExpression.Operation.NE)) {
			return expression.neq(String.valueOf(right));
		}

		throw new UnsupportedOperationException(
			operation +
				" is not supported in MultiselectPicklist Object Fields");
	}

	@Override
	public Predicate getContainsPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		String fieldName, Object fieldValue) {

		return objectDefinitionColumnSupplier.apply(
			fieldName
		).like(
			_getFieldValueExpression(
				String.valueOf(fieldValue), MethodExpression.Type.CONTAINS)
		);
	}

	@Override
	public Predicate getInPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		Object left, List<Object> rights) {

		if (ListUtil.isEmpty(rights)) {
			return null;
		}

		Predicate predicate = null;

		for (Object right : rights) {
			if (predicate == null) {
				predicate = getBinaryExpressionPredicate(
					objectDefinitionColumnSupplier, left, 0,
					BinaryExpression.Operation.EQ, right);
			}
			else {
				predicate = predicate.or(
					getBinaryExpressionPredicate(
						objectDefinitionColumnSupplier, left, 0,
						BinaryExpression.Operation.EQ, right));
			}
		}

		return predicate;
	}

	@Override
	public Predicate getIsNotEmptyPredicate(
		String fieldName,
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier) {

		return objectDefinitionColumnSupplier.apply(
			fieldName
		).isNotNull();
	}

	@Override
	public Predicate getStartsWithPredicate(
		Function<String, Column<?, ?>> objectDefinitionColumnSupplier,
		String fieldName, Object fieldValue) {

		Expression<String> expression = DSLFunctionFactoryUtil.concat(
			new Scalar<>(_SCALAR_EXPRESSION),
			(Expression<String>)objectDefinitionColumnSupplier.apply(fieldName),
			new Scalar<>(_SCALAR_EXPRESSION));

		return expression.like(
			_getFieldValueExpression(
				String.valueOf(fieldValue), MethodExpression.Type.STARTS_WITH));
	}

	private Expression<String> _getFieldValueExpression(
		String fieldValue, MethodExpression.Type methodExpressionType) {

		String expressionString = null;

		if (Objects.equals(
				methodExpressionType, MethodExpression.Type.CONTAINS)) {

			expressionString = StringBundler.concat(
				StringPool.PERCENT, fieldValue, StringPool.PERCENT);
		}
		else if (Objects.equals(
					methodExpressionType, MethodExpression.Type.STARTS_WITH)) {

			expressionString = StringBundler.concat(
				StringPool.PERCENT, _SCALAR_EXPRESSION, fieldValue,
				StringPool.PERCENT, _SCALAR_EXPRESSION, StringPool.PERCENT);
		}
		else {
			expressionString = StringBundler.concat(
				StringPool.PERCENT, _SCALAR_EXPRESSION, fieldValue,
				_SCALAR_EXPRESSION, StringPool.PERCENT);
		}

		return new Scalar<>(expressionString);
	}

	private static final String _SCALAR_EXPRESSION =
		StringPool.COMMA + StringPool.SPACE;

}