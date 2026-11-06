/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.storage.salesforce.internal.odata.filter.expression;

import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.ExpressionVisitor;
import com.liferay.portal.odata.filter.expression.ListExpression;
import com.liferay.portal.odata.filter.expression.LiteralExpression;
import com.liferay.portal.odata.filter.expression.MemberExpression;
import com.liferay.portal.odata.filter.expression.MethodExpression;
import com.liferay.portal.odata.filter.expression.PrimitivePropertyExpression;

import java.util.List;
import java.util.Objects;

/**
 * @author Paulo Albuquerque
 */
public class SOSQLExpressionVisitorImpl implements ExpressionVisitor<Object> {

	public SOSQLExpressionVisitorImpl(
		long objectDefinitionId,
		ObjectFieldLocalService objectFieldLocalService) {

		_objectDefinitionId = objectDefinitionId;
		_objectFieldLocalService = objectFieldLocalService;
	}

	@Override
	public String visitBinaryExpressionOperation(
			BinaryExpression.Operation operation, Object left, Object right)
		throws ExpressionVisitException {

		StringBuilder sb = new StringBuilder();

		if (Objects.equals(BinaryExpression.Operation.AND, operation)) {
			_buildBinaryOperationAndOr(left, " AND ", right, sb);
		}
		else if (Objects.equals(BinaryExpression.Operation.OR, operation)) {
			_buildBinaryOperationAndOr(left, " OR ", right, sb);
		}
		else {
			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				_objectDefinitionId, (String)left);

			if (objectField != null) {
				left = objectField.getExternalReferenceCode();

				if (objectField.compareBusinessType(
						ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

					right = StringUtil.quote(
						ListTypeEntryUtil.getListTypeEntryExternalReferenceCode(
							objectField.getListTypeDefinitionId(),
							StringUtil.unquote((String)right)));
				}
			}
		}

		if (Objects.equals(BinaryExpression.Operation.EQ, operation)) {
			_buildBinaryOperation(left, " = ", right, sb);
		}
		else if (Objects.equals(BinaryExpression.Operation.GE, operation)) {
			_buildBinaryOperation(left, " >= ", right, sb);
		}
		else if (Objects.equals(BinaryExpression.Operation.GT, operation)) {
			_buildBinaryOperation(left, " > ", right, sb);
		}
		else if (Objects.equals(BinaryExpression.Operation.LE, operation)) {
			_buildBinaryOperation(left, " <= ", right, sb);
		}
		else if (Objects.equals(BinaryExpression.Operation.LT, operation)) {
			_buildBinaryOperation(left, " < ", right, sb);
		}
		else if (Objects.equals(BinaryExpression.Operation.NE, operation)) {
			_buildBinaryOperation(left, " != ", right, sb);
		}

		if (Validator.isNull(sb.toString())) {
			throw new UnsupportedOperationException();
		}

		return sb.toString();
	}

	@Override
	public Object visitListExpressionOperation(
			ListExpression.Operation operation, Object left, List<Object> right)
		throws ExpressionVisitException {

		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLiteralExpression(LiteralExpression literalExpression)
		throws ExpressionVisitException {

		if (Objects.equals(null, literalExpression.getType())) {
			return null;
		}

		if (!Objects.equals(
				LiteralExpression.Type.BOOLEAN, literalExpression.getType()) &&
			!Objects.equals(
				LiteralExpression.Type.DATE, literalExpression.getType()) &&
			!Objects.equals(
				LiteralExpression.Type.DATE_TIME,
				literalExpression.getType()) &&
			!Objects.equals(
				LiteralExpression.Type.INTEGER, literalExpression.getType()) &&
			!Objects.equals(
				LiteralExpression.Type.STRING, literalExpression.getType())) {

			throw new UnsupportedOperationException();
		}

		if (Objects.equals(
				LiteralExpression.Type.BOOLEAN, literalExpression.getType())) {

			return GetterUtil.getBoolean(literalExpression.getText());
		}

		if (Objects.equals(
				LiteralExpression.Type.STRING, literalExpression.getType())) {

			return StringUtil.quote(
				StringUtil.replace(
					_normalize(literalExpression.getText()),
					new String[] {StringPool.BACK_SLASH, StringPool.APOSTROPHE},
					new String[] {StringPool.DOUBLE_BACK_SLASH, "\\'"}));
		}

		return literalExpression.getText();
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

		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitPrimitivePropertyExpression(
			PrimitivePropertyExpression primitivePropertyExpression)
		throws ExpressionVisitException {

		return primitivePropertyExpression.getName();
	}

	private void _buildBinaryOperation(
		Object left, String operator, Object right, StringBuilder sb) {

		sb.append(left);
		sb.append(operator);
		sb.append(right);
	}

	private void _buildBinaryOperationAndOr(
		Object left, String operator, Object right, StringBuilder sb) {

		_buildBinaryOperation(
			StringBundler.concat(
				StringPool.OPEN_PARENTHESIS, left,
				StringPool.CLOSE_PARENTHESIS),
			operator,
			StringBundler.concat(
				StringPool.OPEN_PARENTHESIS, right,
				StringPool.CLOSE_PARENTHESIS),
			sb);
	}

	private String _normalize(String text) {
		text = StringUtil.unquote(text);

		return StringUtil.replace(
			text, StringPool.DOUBLE_APOSTROPHE, StringPool.APOSTROPHE);
	}

	private final long _objectDefinitionId;
	private final ObjectFieldLocalService _objectFieldLocalService;

}