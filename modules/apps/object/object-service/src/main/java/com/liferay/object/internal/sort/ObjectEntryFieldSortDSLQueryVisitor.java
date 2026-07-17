/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.sort;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.internal.entry.util.ObjectEntrySearchUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectField;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTable;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.OrderByStep;
import com.liferay.petra.sql.dsl.query.sort.OrderByExpression;
import com.liferay.petra.sql.dsl.spi.ast.BaseASTNode;
import com.liferay.petra.sql.dsl.spi.expression.AggregateExpression;
import com.liferay.petra.sql.dsl.spi.query.OrderBy;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Clob;
import java.sql.Types;

import java.util.Locale;
import java.util.Objects;
import java.util.Stack;

/**
 * @author Carlos Correa
 */
public class ObjectEntryFieldSortDSLQueryVisitor
	extends BaseSortDSLQueryVisitor {

	public ObjectEntryFieldSortDSLQueryVisitor(
		ObjectFieldLocalService objectFieldLocalService) {

		super(objectFieldLocalService, null);
	}

	@Override
	public DSLQuery visit(DSLQuery dslQuery, Sort sort) throws PortalException {
		ObjectDefinition objectDefinition = sort.getObjectDefinition();
		String fieldName = _getSortFieldName(sort);

		ObjectField objectField = objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), fieldName);

		Expression<?> columnExpression = null;
		Table fieldTable = null;
		boolean localized = false;
		String prefix = StringPool.BLANK;

		if (objectField == null) {
			Column<?, Object> column =
				(Column<?, Object>)objectFieldLocalService.getColumn(
					objectDefinition.getObjectDefinitionId(), fieldName);

			fieldTable = getAliasedTable(_getSuffix(sort), column.getTable());

			columnExpression = fieldTable.getColumn(fieldName);
		}
		else {
			Table objectFieldTable = objectFieldLocalService.getTable(
				objectDefinition.getObjectDefinitionId(),
				objectField.getName());

			fieldTable = getAliasedTable(_getSuffix(sort), objectFieldTable);

			columnExpression = _getColumnExpression(objectField, fieldTable);

			if (!_isParentComplexField(sort) && objectField.isLocalized() &&
				(objectFieldTable instanceof
					DynamicObjectDefinitionLocalizationTable)) {

				localized = true;

				String activeLanguageId = ObjectEntrySearchUtil.getLanguageId();
				String defaultLanguageId = _getDefaultLanguageId();

				if (!activeLanguageId.equals(defaultLanguageId)) {
					DynamicObjectDefinitionLocalizationTable
						defaultLanguageIdFieldTable =
							(DynamicObjectDefinitionLocalizationTable)
								getAliasedTable(
									"defaultLanguageId", objectFieldTable);

					if (!contains(dslQuery, defaultLanguageIdFieldTable)) {
						dslQuery = addLeftJoin(
							getPrimaryKeyColumn(defaultLanguageIdFieldTable),
							ObjectEntryTable.INSTANCE.objectEntryId, dslQuery,
							defaultLanguageIdFieldTable,
							defaultLanguageIdFieldTable.getLanguageIdColumn(
							).eq(
								defaultLanguageId
							));
					}

					Expression<String> activeExpression =
						(Expression<String>)columnExpression;
					Expression<String> defaultExpression =
						(Expression<String>)_getColumnExpression(
							objectField, defaultLanguageIdFieldTable);

					columnExpression = DSLFunctionFactoryUtil.caseWhenThen(
						activeExpression.isNotNull(), activeExpression
					).whenThen(
						defaultExpression.isNotNull(), defaultExpression
					).elseEnd(
						activeExpression
					);
				}
			}

			if (Objects.equals(
					objectField.getDBType(),
					ObjectFieldConstants.DB_TYPE_BOOLEAN)) {

				prefix = "AGGREGATION_BOOLEAN_";
			}
		}

		if (!contains(dslQuery, fieldTable)) {
			dslQuery = addLeftJoin(
				getPrimaryKeyColumn(fieldTable), null, dslQuery, fieldTable);
		}

		OrderByExpression orderByExpression = _getOrderByExpression(
			_isParentComplexField(sort), columnExpression, prefix,
			sort.isReverse());

		OrderByExpression[] orderByExpressions = {orderByExpression};

		if (localized) {
			OrderByExpression nullOrderByExpression =
				DSLFunctionFactoryUtil.caseWhenThen(
					columnExpression.isNull(), 1
				).elseEnd(
					0
				).ascending();

			orderByExpressions = new OrderByExpression[] {
				nullOrderByExpression, orderByExpression
			};
		}

		Stack<BaseASTNode> allBaseASTNodes = getAllBaseASTNodes(
			OrderByStep.class, dslQuery);

		OrderByStep orderByStep = (OrderByStep)allBaseASTNodes.pop();

		if (allBaseASTNodes.peek() instanceof OrderBy) {
			OrderBy orderBy = (OrderBy)allBaseASTNodes.pop();

			BaseASTNode baseASTNode = new OrderBy(
				(OrderByStep)orderBy.getChild(),
				ArrayUtil.append(
					orderBy.getOrderByExpressions(), orderByExpressions));

			return updateParents(baseASTNode, allBaseASTNodes);
		}

		BaseASTNode baseASTNode = new OrderBy(orderByStep, orderByExpressions);

		return updateParents(baseASTNode, allBaseASTNodes);
	}

	private Expression<?> _getColumnExpression(
		ObjectField objectField, Table table) {

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT)) {

			return table.getColumn(objectField.getSortableDBColumnName());
		}

		Column<?, ?> column = table.getColumn(objectField.getDBColumnName());

		if (column.getSQLType() == Types.CLOB) {
			return DSLFunctionFactoryUtil.castClobText(
				(Expression<Clob>)column);
		}

		return column;
	}

	private String _getDefaultLanguageId() {
		Locale locale = LocaleThreadLocal.getSiteDefaultLocale();

		if (locale == null) {
			locale = LocaleUtil.getDefault();
		}

		return LocaleUtil.toLanguageId(locale);
	}

	private OrderByExpression _getOrderByExpression(
		boolean aggregate, Expression<?> expression, String prefix,
		boolean reverse) {

		if (reverse) {
			if (aggregate) {
				expression = new AggregateExpression<>(
					false, expression, prefix + "max");
			}

			return expression.descending();
		}

		if (aggregate) {
			expression = new AggregateExpression<>(
				false, expression, prefix + "min");
		}

		return expression.ascending();
	}

	private String _getSortFieldName(Sort sort) {
		String fieldName = sort.getFieldName();

		if (!fieldName.contains(StringPool.SLASH)) {
			return fieldName;
		}

		return StringUtil.extractLast(fieldName, StringPool.SLASH);
	}

	private String _getSuffix(Sort sort) {
		if (!_isParentComplexField(sort)) {
			return null;
		}

		return StringUtil.replace(
			StringUtil.removeLast(
				sort.getFieldPath(),
				CharPool.FORWARD_SLASH + _getSortFieldName(sort)),
			CharPool.FORWARD_SLASH, CharPool.UNDERLINE);
	}

	private boolean _isParentComplexField(Sort sort) {
		return !StringUtil.equals(_getSortFieldName(sort), sort.getFieldPath());
	}

}