/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.language.override.internal.odata.filter.expression;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.language.override.model.PLOEntryTable;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.ExpressionVisitor;
import com.liferay.portal.odata.filter.expression.ListExpression;
import com.liferay.portal.odata.filter.expression.LiteralExpression;
import com.liferay.portal.odata.filter.expression.MemberExpression;
import com.liferay.portal.odata.filter.expression.MethodExpression;
import com.liferay.portal.odata.filter.expression.PrimitivePropertyExpression;
import com.liferay.portal.odata.filter.expression.UnaryExpression;

import java.time.Instant;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Thiago Buarque
 */
public class PredicateExpressionVisitorImpl
	implements ExpressionVisitor<Object> {

	public PredicateExpressionVisitorImpl(EntityModel entityModel) {
		_entityModel = entityModel;
	}

	@Override
	public Object visitBinaryExpressionOperation(
			BinaryExpression.Operation operation, Object left, Object right)
		throws ExpressionVisitException {

		if (Objects.equals(BinaryExpression.Operation.AND, operation) ||
			Objects.equals(BinaryExpression.Operation.OR, operation)) {

			Predicate leftPredicate = Predicate.withParentheses(
				(Predicate)left);
			Predicate rightPredicate = Predicate.withParentheses(
				(Predicate)right);

			if (Objects.equals(BinaryExpression.Operation.AND, operation)) {
				return leftPredicate.and(rightPredicate);
			}

			return leftPredicate.or(rightPredicate);
		}

		Column<PLOEntryTable, Object> column =
			(Column<PLOEntryTable, Object>)left;

		_validateValue(column, right);

		if (Objects.equals(BinaryExpression.Operation.EQ, operation)) {
			return column.eq(right);
		}
		else if (Objects.equals(BinaryExpression.Operation.GE, operation)) {
			return column.gte(right);
		}
		else if (Objects.equals(BinaryExpression.Operation.GT, operation)) {
			return column.gt(right);
		}
		else if (Objects.equals(BinaryExpression.Operation.LE, operation)) {
			return column.lte(right);
		}
		else if (Objects.equals(BinaryExpression.Operation.LT, operation)) {
			return column.lt(right);
		}
		else if (Objects.equals(BinaryExpression.Operation.NE, operation)) {
			return column.neq(right);
		}

		throw new ExpressionVisitException(
			"Unsupported binary operation " + operation);
	}

	@Override
	public Object visitListExpressionOperation(
			ListExpression.Operation operation, Object left,
			List<Object> rights)
		throws ExpressionVisitException {

		if (!Objects.equals(ListExpression.Operation.IN, operation)) {
			throw new ExpressionVisitException(
				"Unsupported list operation " + operation);
		}

		Column<PLOEntryTable, Object> column =
			(Column<PLOEntryTable, Object>)left;

		for (Object right : rights) {
			_validateValue(column, right);
		}

		return column.in(rights.toArray());
	}

	@Override
	public Object visitLiteralExpression(LiteralExpression literalExpression)
		throws ExpressionVisitException {

		String text = literalExpression.getText();

		if (Objects.equals(
				LiteralExpression.Type.DATE_TIME,
				literalExpression.getType())) {

			Instant instant = Instant.parse(text);

			return Date.from(instant);
		}
		else if (Objects.equals(
					LiteralExpression.Type.STRING,
					literalExpression.getType())) {

			String value = StringUtil.unquote(text);

			return StringUtil.replace(
				value, StringPool.DOUBLE_APOSTROPHE, StringPool.APOSTROPHE);
		}

		throw new ExpressionVisitException(
			"Unsupported literal type " + literalExpression.getType());
	}

	@Override
	public Object visitMemberExpression(MemberExpression memberExpression)
		throws ExpressionVisitException {

		Expression expression = memberExpression.getExpression();

		return expression.accept(this);
	}

	@Override
	public Object visitMethodExpression(
			List<Object> expressions, MethodExpression.Type type)
		throws ExpressionVisitException {

		if (!Objects.equals(MethodExpression.Type.CONTAINS, type) &&
			!Objects.equals(MethodExpression.Type.STARTS_WITH, type)) {

			throw new ExpressionVisitException("Unsupported method " + type);
		}

		Column<PLOEntryTable, ?> column =
			(Column<PLOEntryTable, ?>)expressions.get(0);
		Object value = expressions.get(1);

		if (!(value instanceof String) ||
			!Objects.equals(column.getJavaType(), String.class)) {

			throw new ExpressionVisitException(
				StringBundler.concat(
					"Unsupported method ", type, " for ", column.getName()));
		}

		String pattern = StringUtil.toLowerCase((String)value);

		if (Objects.equals(MethodExpression.Type.CONTAINS, type)) {
			pattern = StringPool.PERCENT + pattern;
		}

		return DSLFunctionFactoryUtil.lower(
			(Column<PLOEntryTable, String>)column
		).like(
			pattern + StringPool.PERCENT
		);
	}

	@Override
	public Object visitPrimitivePropertyExpression(
		PrimitivePropertyExpression primitivePropertyExpression) {

		Map<String, EntityField> entityFieldsMap =
			_entityModel.getEntityFieldsMap();

		EntityField entityField = entityFieldsMap.get(
			primitivePropertyExpression.getName());

		return PLOEntryTable.INSTANCE.getColumn(
			entityField.getFilterableName(null));
	}

	@Override
	public Object visitUnaryExpressionOperation(
			UnaryExpression.Operation operation, Object operand)
		throws ExpressionVisitException {

		throw new ExpressionVisitException(
			"Unsupported unary operation " + operation);
	}

	private void _validateValue(
			Column<PLOEntryTable, Object> column, Object value)
		throws ExpressionVisitException {

		Class<?> javaType = column.getJavaType();

		if (!javaType.isInstance(value)) {
			throw new ExpressionVisitException(
				StringBundler.concat(
					"Invalid value ", value, " for ", column.getName()));
		}
	}

	private final EntityModel _entityModel;

}