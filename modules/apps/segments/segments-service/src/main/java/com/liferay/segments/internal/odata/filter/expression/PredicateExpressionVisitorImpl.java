/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.odata.filter.expression;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.CollectionPropertyExpression;
import com.liferay.portal.odata.filter.expression.ComplexPropertyExpression;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.ExpressionVisitor;
import com.liferay.portal.odata.filter.expression.LambdaFunctionExpression;
import com.liferay.portal.odata.filter.expression.LambdaVariableExpression;
import com.liferay.portal.odata.filter.expression.ListExpression;
import com.liferay.portal.odata.filter.expression.LiteralExpression;
import com.liferay.portal.odata.filter.expression.MemberExpression;
import com.liferay.portal.odata.filter.expression.MethodExpression;
import com.liferay.portal.odata.filter.expression.PrimitivePropertyExpression;
import com.liferay.portal.odata.filter.expression.PropertyExpression;
import com.liferay.portal.odata.filter.expression.UnaryExpression;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Eduardo García
 */
public class PredicateExpressionVisitorImpl<T extends Map>
	implements ExpressionVisitor<Object> {

	public PredicateExpressionVisitorImpl(EntityModel entityModel) {
		_entityModel = entityModel;

		_lambdaCollectionEntityField = null;
	}

	public PredicateExpressionVisitorImpl(
		EntityModel entityModel,
		CollectionEntityField lambdaCollectionEntityField) {

		_entityModel = entityModel;
		_lambdaCollectionEntityField = lambdaCollectionEntityField;
	}

	@Override
	public Predicate<T> visitBinaryExpressionOperation(
		BinaryExpression.Operation operation, Object left, Object right) {

		Predicate<T> predicate = null;

		if (_lambdaCollectionEntityField != null) {
			predicate = _getLambdaPredicate(operation, left, right);
		}
		else {
			predicate = _getPredicate(operation, left, right);
		}

		if (predicate != null) {
			return predicate;
		}

		throw new UnsupportedOperationException(
			"Unsupported method visitBinaryExpressionOperation with " +
				"operation " + operation);
	}

	@Override
	public Object visitCollectionPropertyExpression(
			CollectionPropertyExpression collectionPropertyExpression)
		throws ExpressionVisitException {

		LambdaFunctionExpression lambdaFunctionExpression =
			collectionPropertyExpression.getLambdaFunctionExpression();

		Map<String, EntityField> entityFieldsMap =
			_entityModel.getEntityFieldsMap();

		CollectionEntityField collectionEntityField =
			(CollectionEntityField)entityFieldsMap.get(
				collectionPropertyExpression.getName());

		return lambdaFunctionExpression.accept(
			new PredicateExpressionVisitorImpl(
				_getLambdaEntityModel(
					lambdaFunctionExpression.getVariableName(),
					collectionEntityField),
				collectionEntityField));
	}

	@Override
	public Object visitComplexPropertyExpression(
		ComplexPropertyExpression complexPropertyExpression) {

		Map<String, EntityField> entityFieldsMap =
			_entityModel.getEntityFieldsMap();

		ComplexEntityField complexEntityField =
			(ComplexEntityField)entityFieldsMap.get(
				complexPropertyExpression.getName());

		PropertyExpression propertyExpression =
			complexPropertyExpression.getPropertyExpression();

		Map<String, EntityField> complexEntityFieldEntityFieldsMap =
			complexEntityField.getEntityFieldsMap();

		return complexEntityFieldEntityFieldsMap.get(
			propertyExpression.getName());
	}

	@Override
	public Object visitLambdaFunctionExpression(
			LambdaFunctionExpression.Type type, String variable,
			Expression expression)
		throws ExpressionVisitException {

		if (type == LambdaFunctionExpression.Type.ANY) {
			return _any(expression);
		}

		throw new UnsupportedOperationException(
			"Unsupported type visitLambdaFunctionExpression with type " + type);
	}

	@Override
	public EntityField visitLambdaVariableExpression(
			LambdaVariableExpression lambdaVariableExpression)
		throws ExpressionVisitException {

		Map<String, EntityField> entityFieldsMap =
			_entityModel.getEntityFieldsMap();

		EntityField entityField = entityFieldsMap.get(
			lambdaVariableExpression.getVariableName());

		if (entityField == null) {
			throw new ExpressionVisitException(
				"Invoked visitLambdaVariableExpression when no entity field " +
					"is stored for lambda variable name " +
						lambdaVariableExpression.getVariableName());
		}

		return entityField;
	}

	@Override
	public Object visitListExpressionOperation(
			ListExpression.Operation operation, Object left,
			List<Object> rights)
		throws ExpressionVisitException {

		if (operation != ListExpression.Operation.IN) {
			throw new UnsupportedOperationException(
				"Unsupported method visitListExpressionOperation with " +
					"operation " + operation);
		}

		return _getINPredicate((EntityField)left, rights);
	}

	@Override
	public Object visitLiteralExpression(LiteralExpression literalExpression) {
		if (Objects.equals(
				LiteralExpression.Type.BOOLEAN, literalExpression.getType())) {

			return Boolean.valueOf(literalExpression.getText());
		}
		else if (Objects.equals(
					LiteralExpression.Type.DATE, literalExpression.getType())) {

			return LocalDate.parse(literalExpression.getText());
		}
		else if (Objects.equals(
					LiteralExpression.Type.DATE_TIME,
					literalExpression.getType())) {

			return ZonedDateTime.parse(literalExpression.getText());
		}
		else if (Objects.equals(
					LiteralExpression.Type.DOUBLE,
					literalExpression.getType())) {

			return Double.valueOf(literalExpression.getText());
		}
		else if (Objects.equals(
					LiteralExpression.Type.INTEGER,
					literalExpression.getType())) {

			return Integer.valueOf(literalExpression.getText());
		}
		else if (Objects.equals(
					LiteralExpression.Type.STRING,
					literalExpression.getType())) {

			return _normalizeStringLiteral(literalExpression.getText());
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
		List<Object> expressions, MethodExpression.Type type) {

		if (type == MethodExpression.Type.CONTAINS) {
			if (expressions.size() != 2) {
				throw new UnsupportedOperationException(
					StringBundler.concat(
						"Unsupported method visitMethodExpression with method ",
						"type ", type, " and ", expressions.size(),
						" parameters"));
			}

			if (_lambdaCollectionEntityField != null) {
				return _getLambdaContainsPredicate(
					(EntityField)expressions.get(0), expressions.get(1));
			}

			return _getContainsPredicate(
				(EntityField)expressions.get(0), expressions.get(1));
		}

		throw new UnsupportedOperationException(
			"Unsupported method visitMethodExpression with method type " +
				type);
	}

	@Override
	public Object visitPrimitivePropertyExpression(
		PrimitivePropertyExpression primitivePropertyExpression) {

		Map<String, EntityField> entityFieldsMap =
			_entityModel.getEntityFieldsMap();

		return entityFieldsMap.get(primitivePropertyExpression.getName());
	}

	@Override
	public Predicate<T> visitUnaryExpressionOperation(
		UnaryExpression.Operation operation, Object operand) {

		if (Objects.equals(UnaryExpression.Operation.NOT, operation)) {
			return _getNotPredicate((Predicate<T>)operand);
		}

		throw new UnsupportedOperationException(
			"Unsupported method visitUnaryExpressionOperation with operation " +
				operation);
	}

	private Object _any(Expression expression) throws ExpressionVisitException {
		return expression.accept(this);
	}

	private Predicate<T> _getANDPredicate(
		Predicate<T> leftPredicate, Predicate<T> rightPredicate) {

		return leftPredicate.and(rightPredicate);
	}

	private Predicate<T> _getContainsPredicate(
		EntityField entityField, Object fieldValue) {

		if (Objects.equals(entityField.getType(), EntityField.Type.ID) ||
			Objects.equals(entityField.getType(), EntityField.Type.STRING)) {

			return p -> StringUtils.containsIgnoreCase(
				String.valueOf(p.get(entityField.getName())),
				String.valueOf(fieldValue));
		}

		throw new UnsupportedOperationException(
			"Unsupported method _contains with entity field type " +
				entityField.getType());
	}

	private Predicate<T> _getEQPredicate(
		EntityField entityField, Object fieldValue) {

		if (Objects.equals(entityField.getType(), EntityField.Type.ID)) {
			return p -> StringUtils.containsIgnoreCase(
				String.valueOf(p.get(entityField.getName())),
				String.valueOf(fieldValue));
		}
		else if (Objects.equals(
					entityField.getType(), EntityField.Type.STRING)) {

			return p -> fieldValue.equals(
				_normalizeStringLiteral(
					String.valueOf(p.get(entityField.getName()))));
		}

		return p -> fieldValue.equals(p.get(entityField.getName()));
	}

	private Predicate<T> _getGEPredicate(
		EntityField entityField, Object fieldValue) {

		if ((fieldValue instanceof Comparable) &&
			(Objects.equals(entityField.getType(), EntityField.Type.DATE) ||
			 Objects.equals(
				 entityField.getType(), EntityField.Type.DATE_TIME) ||
			 Objects.equals(entityField.getType(), EntityField.Type.DOUBLE) ||
			 Objects.equals(entityField.getType(), EntityField.Type.INTEGER) ||
			 Objects.equals(entityField.getType(), EntityField.Type.STRING))) {

			Comparable<Object> comparable = (Comparable)fieldValue;

			return p -> comparable.compareTo(p.get(entityField.getName())) <= 0;
		}

		throw new UnsupportedOperationException(
			"Unsupported method _getGEPredicate with entity field type " +
				entityField.getType());
	}

	private Predicate<T> _getGTPredicate(
		EntityField entityField, Object fieldValue) {

		if ((fieldValue instanceof Comparable) &&
			(Objects.equals(entityField.getType(), EntityField.Type.DATE) ||
			 Objects.equals(
				 entityField.getType(), EntityField.Type.DATE_TIME) ||
			 Objects.equals(entityField.getType(), EntityField.Type.DOUBLE) ||
			 Objects.equals(entityField.getType(), EntityField.Type.INTEGER) ||
			 Objects.equals(entityField.getType(), EntityField.Type.STRING))) {

			Comparable<Object> comparable = (Comparable)fieldValue;

			return p -> comparable.compareTo(p.get(entityField.getName())) < 0;
		}

		throw new UnsupportedOperationException(
			"Unsupported method _getGTPredicate with entity field type " +
				entityField.getType());
	}

	private Predicate<T> _getINPredicate(
		EntityField entityField, List<Object> fieldValues) {

		return p -> {
			for (Object fieldValue : fieldValues) {
				if (StringUtils.containsIgnoreCase(
						String.valueOf(p.get(entityField.getName())),
						String.valueOf(fieldValue))) {

					return true;
				}
			}

			return false;
		};
	}

	private Predicate<T> _getLEPredicate(
		EntityField entityField, Object fieldValue) {

		if ((fieldValue instanceof Comparable) &&
			(Objects.equals(entityField.getType(), EntityField.Type.DATE) ||
			 Objects.equals(
				 entityField.getType(), EntityField.Type.DATE_TIME) ||
			 Objects.equals(entityField.getType(), EntityField.Type.DOUBLE) ||
			 Objects.equals(entityField.getType(), EntityField.Type.INTEGER) ||
			 Objects.equals(entityField.getType(), EntityField.Type.STRING))) {

			Comparable<Object> comparable = (Comparable)fieldValue;

			return p -> comparable.compareTo(p.get(entityField.getName())) >= 0;
		}

		throw new UnsupportedOperationException(
			"Unsupported method _getLEPredicate with entity field type " +
				entityField.getType());
	}

	private Predicate<T> _getLTPredicate(
		EntityField entityField, Object fieldValue) {

		if ((fieldValue instanceof Comparable) &&
			(Objects.equals(entityField.getType(), EntityField.Type.DATE) ||
			 Objects.equals(
				 entityField.getType(), EntityField.Type.DATE_TIME) ||
			 Objects.equals(entityField.getType(), EntityField.Type.DOUBLE) ||
			 Objects.equals(entityField.getType(), EntityField.Type.INTEGER) ||
			 Objects.equals(entityField.getType(), EntityField.Type.STRING))) {

			Comparable<Object> comparable = (Comparable)fieldValue;

			return p -> comparable.compareTo(p.get(entityField.getName())) > 0;
		}

		throw new UnsupportedOperationException(
			"Unsupported method _getLTPredicate with entity field type " +
				entityField.getType());
	}

	private Predicate<T> _getLambdaContainsPredicate(
		EntityField entityField, Object fieldValue) {

		if (!Objects.equals(entityField.getType(), EntityField.Type.STRING)) {
			throw new UnsupportedOperationException(
				"Unsupported method _lambdaContains with entity field type " +
					entityField.getType());
		}

		return p -> {
			for (String name :
					(String[])p.get(_lambdaCollectionEntityField.getName())) {

				if (StringUtils.containsIgnoreCase(
						String.valueOf(name), String.valueOf(fieldValue))) {

					return true;
				}
			}

			return false;
		};
	}

	private Predicate<T> _getLambdaEQPredicate(
		EntityField entityField, Object fieldValue) {

		if (!Objects.equals(entityField.getType(), EntityField.Type.STRING)) {
			throw new UnsupportedOperationException(
				"Unsupported method _getLambdaEQPredicate with entity field " +
					entityField.getType());
		}

		return p -> {
			for (String name :
					(String[])p.get(_lambdaCollectionEntityField.getName())) {

				if (fieldValue.equals(
						_normalizeStringLiteral(String.valueOf(name)))) {

					return true;
				}
			}

			return false;
		};
	}

	private EntityModel _getLambdaEntityModel(
		String variableName, CollectionEntityField collectionEntityField) {

		return new EntityModel() {

			@Override
			public Map<String, EntityField> getEntityFieldsMap() {
				return Collections.singletonMap(
					variableName, collectionEntityField.getEntityField());
			}

			@Override
			public String getName() {
				return collectionEntityField.getName();
			}

		};
	}

	private Predicate<T> _getLambdaPredicate(
		BinaryExpression.Operation operation, Object left, Object right) {

		if (!Objects.equals(BinaryExpression.Operation.EQ, operation)) {
			return null;
		}

		return _getLambdaEQPredicate((EntityField)left, right);
	}

	private Predicate<T> _getNotPredicate(Predicate<T> predicate) {
		return predicate.negate();
	}

	private Predicate<T> _getORPredicate(
		Predicate<T> leftPredicate, Predicate<T> rightPredicate) {

		return leftPredicate.or(rightPredicate);
	}

	private Predicate<T> _getPredicate(
		BinaryExpression.Operation operation, Object left, Object right) {

		if (Objects.equals(BinaryExpression.Operation.AND, operation)) {
			return _getANDPredicate((Predicate<T>)left, (Predicate<T>)right);
		}
		else if (Objects.equals(BinaryExpression.Operation.EQ, operation)) {
			return _getEQPredicate((EntityField)left, right);
		}
		else if (Objects.equals(BinaryExpression.Operation.GE, operation)) {
			return _getGEPredicate((EntityField)left, right);
		}
		else if (Objects.equals(BinaryExpression.Operation.GT, operation)) {
			return _getGTPredicate((EntityField)left, right);
		}
		else if (Objects.equals(BinaryExpression.Operation.LE, operation)) {
			return _getLEPredicate((EntityField)left, right);
		}
		else if (Objects.equals(BinaryExpression.Operation.LT, operation)) {
			return _getLTPredicate((EntityField)left, right);
		}
		else if (Objects.equals(BinaryExpression.Operation.OR, operation)) {
			return _getORPredicate((Predicate<T>)left, (Predicate<T>)right);
		}

		return null;
	}

	private Object _normalizeStringLiteral(String literal) {
		literal = StringUtil.toLowerCase(literal);

		literal = StringUtil.unquote(literal);

		return StringUtil.replace(
			literal, StringPool.DOUBLE_APOSTROPHE, StringPool.APOSTROPHE);
	}

	private final EntityModel _entityModel;
	private final CollectionEntityField _lambdaCollectionEntityField;

}