/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.odata.internal.filter;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.ExistsFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.InvalidFilterException;
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
import com.liferay.portal.search.query.NestedFieldQueryHelper;

import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Julio Camarero
 */
public class ExpressionVisitorImpl implements ExpressionVisitor<Object> {

	public ExpressionVisitorImpl(
		Format format, Locale locale, EntityModel entityModel,
		NestedFieldQueryHelper nestedFieldQueryHelper) {

		_format = format;
		_locale = locale;
		_entityModel = entityModel;
		_nestedFieldQueryHelper = nestedFieldQueryHelper;
	}

	@Override
	public Filter visitBinaryExpressionOperation(
		BinaryExpression.Operation operation, Object left, Object right) {

		Filter filter = null;

		if (Objects.equals(BinaryExpression.Operation.AND, operation)) {
			filter = _getANDFilter((Filter)left, (Filter)right);
		}
		else if (Objects.equals(BinaryExpression.Operation.EQ, operation)) {
			filter = _getEQFilter((EntityField)left, right, _locale);
		}
		else if (Objects.equals(BinaryExpression.Operation.GE, operation)) {
			filter = _getGEFilter((EntityField)left, right, _locale);
		}
		else if (Objects.equals(BinaryExpression.Operation.GT, operation)) {
			filter = _getGTFilter((EntityField)left, right, _locale);
		}
		else if (Objects.equals(BinaryExpression.Operation.LE, operation)) {
			filter = _getLEFilter((EntityField)left, right, _locale);
		}
		else if (Objects.equals(BinaryExpression.Operation.LT, operation)) {
			filter = _getLTFilter((EntityField)left, right, _locale);
		}
		else if (Objects.equals(BinaryExpression.Operation.NE, operation)) {
			filter = _getNEFilter((EntityField)left, right, _locale);
		}
		else if (Objects.equals(BinaryExpression.Operation.OR, operation)) {
			filter = _getORFilter((Filter)left, (Filter)right);
		}
		else {
			throw new UnsupportedOperationException(
				"Unsupported method visitBinaryExpressionOperation with " +
					"operation " + operation);
		}

		return filter;
	}

	@Override
	public Object visitCollectionPropertyExpression(
			CollectionPropertyExpression collectionPropertyExpression)
		throws ExpressionVisitException {

		LambdaFunctionExpression lambdaFunctionExpression =
			collectionPropertyExpression.getLambdaFunctionExpression();

		Map<String, EntityField> entityFieldsMap =
			_entityModel.getEntityFieldsMap();

		return lambdaFunctionExpression.accept(
			new ExpressionVisitorImpl(
				_format, _locale,
				_getLambdaEntityModel(
					lambdaFunctionExpression.getVariableName(),
					(CollectionEntityField)entityFieldsMap.get(
						collectionPropertyExpression.getName())),
				_nestedFieldQueryHelper));
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
			ListExpression.Operation operation, Object left, List<Object> right)
		throws ExpressionVisitException {

		if (operation == ListExpression.Operation.IN) {
			return _getINFilter((EntityField)left, right, _locale);
		}

		throw new UnsupportedOperationException(
			"Unsupported method visitListExpressionOperation with operation " +
				operation);
	}

	@Override
	public Object visitLiteralExpression(LiteralExpression literalExpression) {
		if (Objects.equals(
				LiteralExpression.Type.DATE, literalExpression.getType()) ||
			Objects.equals(
				LiteralExpression.Type.DATE_TIME,
				literalExpression.getType())) {

			return _normalizeDateLiteral(literalExpression.getText());
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
						"type ", type, " and ", expressions.size(), "params"));
			}

			return _contains(
				(EntityField)expressions.get(0), expressions.get(1), _locale);
		}
		else if (type == MethodExpression.Type.NOW) {
			if (!expressions.isEmpty()) {
				throw new UnsupportedOperationException(
					StringBundler.concat(
						"Unsupported method visitMethodExpression with method",
						"type ", type, " and ", expressions.size(), "params"));
			}

			return _normalizeDateLiteral(ISO8601Utils.format(_now));
		}
		else if (type == MethodExpression.Type.STARTS_WITH) {
			if (expressions.size() != 2) {
				throw new UnsupportedOperationException(
					StringBundler.concat(
						"Unsupported method visitMethodExpression with method",
						"type ", type, " and ", expressions.size(), "params"));
			}

			return _startsWith(
				(EntityField)expressions.get(0), expressions.get(1), _locale);
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
	public Filter visitUnaryExpressionOperation(
		UnaryExpression.Operation operation, Object operand) {

		if (Objects.equals(UnaryExpression.Operation.NOT, operation)) {
			return _getNotFilter((Filter)operand);
		}

		throw new UnsupportedOperationException(
			"Unsupported method visitUnaryExpressionOperation with operation " +
				operation);
	}

	private Object _any(Expression expression) throws ExpressionVisitException {
		return expression.accept(this);
	}

	private Filter _contains(
		EntityField entityField, Object fieldValue, Locale locale) {

		return new QueryFilter(
			_nestedFieldQueryHelper.getQuery(
				entityField.getFilterableName(locale),
				fieldName -> new WildcardQuery(
					fieldName,
					"*" + entityField.getFilterableValue(fieldValue) + "*")));
	}

	private Filter _getANDFilter(Filter leftFilter, Filter rightFilter) {
		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.add(leftFilter, BooleanClauseOccur.MUST);
		booleanFilter.add(rightFilter, BooleanClauseOccur.MUST);

		return booleanFilter;
	}

	private Filter _getEQFilter(
		EntityField entityField, Object fieldValue, Locale locale) {

		if (fieldValue == null) {
			BooleanFilter booleanFilter = new BooleanFilter();

			booleanFilter.add(
				_getNullValueFilter(entityField, locale),
				BooleanClauseOccur.MUST_NOT);

			return booleanFilter;
		}

		return new QueryFilter(
			_nestedFieldQueryHelper.getQuery(
				entityField.getFilterableName(locale),
				fieldName -> new TermQuery(
					fieldName, entityField.getFilterableValue(fieldValue))));
	}

	private Filter _getGEFilter(
		EntityField entityField, Object fieldValue, Locale locale) {

		if (fieldValue == null) {
			throw new UnsupportedOperationException(
				"Unsupported method _getGEFilter with null values");
		}

		if (Objects.equals(entityField.getType(), EntityField.Type.DATE) ||
			Objects.equals(entityField.getType(), EntityField.Type.DATE_TIME) ||
			Objects.equals(entityField.getType(), EntityField.Type.DOUBLE) ||
			Objects.equals(entityField.getType(), EntityField.Type.INTEGER) ||
			Objects.equals(entityField.getType(), EntityField.Type.STRING)) {

			return new QueryFilter(
				_nestedFieldQueryHelper.getQuery(
					entityField.getFilterableName(locale),
					fieldName -> new TermRangeQuery(
						fieldName, entityField.getFilterableValue(fieldValue),
						null, true, true)));
		}

		throw new UnsupportedOperationException(
			"Unsupported method _getGEFilter with entity field type " +
				entityField.getType());
	}

	private Filter _getGTFilter(
		EntityField entityField, Object fieldValue, Locale locale) {

		if (fieldValue == null) {
			throw new UnsupportedOperationException(
				"Unsupported method _getGTFilter with null values");
		}

		if (Objects.equals(entityField.getType(), EntityField.Type.DATE) ||
			Objects.equals(entityField.getType(), EntityField.Type.DATE_TIME) ||
			Objects.equals(entityField.getType(), EntityField.Type.DOUBLE) ||
			Objects.equals(entityField.getType(), EntityField.Type.INTEGER) ||
			Objects.equals(entityField.getType(), EntityField.Type.STRING)) {

			return new QueryFilter(
				_nestedFieldQueryHelper.getQuery(
					entityField.getFilterableName(locale),
					fieldName -> new TermRangeQuery(
						fieldName, entityField.getFilterableValue(fieldValue),
						null, false, true)));
		}

		throw new UnsupportedOperationException(
			"Unsupported method _getGTFilter with entity field type " +
				entityField.getType());
	}

	private Filter _getINFilter(
		EntityField entityField, List<Object> fieldValues, Locale locale) {

		BooleanQuery booleanQuery = new BooleanQuery();

		for (Object fieldValue : fieldValues) {
			booleanQuery.add(
				_nestedFieldQueryHelper.getQuery(
					entityField.getFilterableName(locale),
					fieldName -> new TermQuery(
						fieldName, entityField.getFilterableValue(fieldValue))),
				BooleanClauseOccur.SHOULD);
		}

		return new QueryFilter(booleanQuery);
	}

	private Filter _getLEFilter(
		EntityField entityField, Object fieldValue, Locale locale) {

		if (fieldValue == null) {
			throw new UnsupportedOperationException(
				"Unsupported method _getLEFilter with null values");
		}

		if (Objects.equals(entityField.getType(), EntityField.Type.DATE) ||
			Objects.equals(entityField.getType(), EntityField.Type.DATE_TIME) ||
			Objects.equals(entityField.getType(), EntityField.Type.DOUBLE) ||
			Objects.equals(entityField.getType(), EntityField.Type.INTEGER) ||
			Objects.equals(entityField.getType(), EntityField.Type.STRING)) {

			return new QueryFilter(
				_nestedFieldQueryHelper.getQuery(
					entityField.getFilterableName(locale),
					fieldName -> new TermRangeQuery(
						fieldName, null,
						entityField.getFilterableValue(fieldValue), false,
						true)));
		}

		throw new UnsupportedOperationException(
			"Unsupported method _getLEFilter with entity field type " +
				entityField.getType());
	}

	private Filter _getLTFilter(
		EntityField entityField, Object fieldValue, Locale locale) {

		if (fieldValue == null) {
			throw new UnsupportedOperationException(
				"Unsupported method _getLTFilter with null values");
		}

		if (Objects.equals(entityField.getType(), EntityField.Type.DATE) ||
			Objects.equals(entityField.getType(), EntityField.Type.DATE_TIME) ||
			Objects.equals(entityField.getType(), EntityField.Type.DOUBLE) ||
			Objects.equals(entityField.getType(), EntityField.Type.INTEGER) ||
			Objects.equals(entityField.getType(), EntityField.Type.STRING)) {

			return new QueryFilter(
				_nestedFieldQueryHelper.getQuery(
					entityField.getFilterableName(locale),
					fieldName -> new TermRangeQuery(
						fieldName, null,
						entityField.getFilterableValue(fieldValue), false,
						false)));
		}

		throw new UnsupportedOperationException(
			"Unsupported method _getLTFilter with entity field type " +
				entityField.getType());
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

	private Filter _getNEFilter(
		EntityField entityField, Object fieldValue, Locale locale) {

		if (fieldValue == null) {
			return _getNullValueFilter(entityField, locale);
		}

		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.add(
			new QueryFilter(
				_nestedFieldQueryHelper.getQuery(
					entityField.getFilterableName(locale),
					fieldName -> new TermQuery(
						fieldName,
						entityField.getFilterableValue(fieldValue)))),
			BooleanClauseOccur.MUST_NOT);

		return booleanFilter;
	}

	private Filter _getNotFilter(Filter filter) {
		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.add(filter, BooleanClauseOccur.MUST_NOT);

		return booleanFilter;
	}

	private Filter _getNullValueFilter(EntityField entityField, Locale locale) {
		EntityField.Type type = entityField.getType();

		if (Objects.equals(type, EntityField.Type.DATE) ||
			Objects.equals(type, EntityField.Type.DATE_TIME)) {

			return new ExistsFilter(entityField.getFilterableName(locale));
		}

		return new QueryFilter(
			_nestedFieldQueryHelper.getQuery(
				entityField.getFilterableName(locale),
				fieldName -> new WildcardQuery(fieldName, "*")));
	}

	private Filter _getORFilter(Filter leftFilter, Filter rightFilter) {
		BooleanFilter booleanFilter = new BooleanFilter();

		booleanFilter.add(leftFilter, BooleanClauseOccur.SHOULD);
		booleanFilter.add(rightFilter, BooleanClauseOccur.SHOULD);

		return booleanFilter;
	}

	private Object _normalizeDateLiteral(String literal) {
		try {
			Date date = ISO8601Utils.parse(literal, new ParsePosition(0));

			return _format.format(date);
		}
		catch (ParseException parseException) {
			throw new InvalidFilterException(
				"Invalid date format, use ISO 8601: " +
					parseException.getMessage());
		}
	}

	private Object _normalizeStringLiteral(String literal) {
		literal = StringUtil.toLowerCase(literal);

		literal = StringUtil.unquote(literal);

		return StringUtil.replace(
			literal, StringPool.DOUBLE_APOSTROPHE, StringPool.APOSTROPHE);
	}

	private Filter _startsWith(
		EntityField entityField, Object fieldValue, Locale locale) {

		return new QueryFilter(
			_nestedFieldQueryHelper.getQuery(
				entityField.getFilterableName(locale),
				fieldName -> new WildcardQuery(
					fieldName,
					entityField.getFilterableValue(fieldValue) + "*")));
	}

	private final EntityModel _entityModel;
	private final Format _format;
	private final Locale _locale;
	private final NestedFieldQueryHelper _nestedFieldQueryHelper;
	private final Date _now = new Date();

}