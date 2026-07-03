/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.sort;

import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.sql.dsl.spi.ast.BaseASTNode;
import com.liferay.petra.sql.dsl.spi.query.From;
import com.liferay.petra.sql.dsl.spi.query.Join;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Stack;

/**
 * @author Carlos Correa
 */
public abstract class BaseSortDSLQueryVisitor {

	public BaseSortDSLQueryVisitor(
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		this.objectFieldLocalService = objectFieldLocalService;
		this.objectRelationshipLocalService = objectRelationshipLocalService;
	}

	public abstract DSLQuery visit(DSLQuery dslQuery, Sort sort)
		throws PortalException;

	protected DSLQuery addLeftJoin(
		Column<?, Long> column1, Column<?, Long> column2, DSLQuery dslQuery,
		Table<?> table) {

		return addLeftJoin(column1, column2, dslQuery, table, null);
	}

	protected DSLQuery addLeftJoin(
		Column<?, Long> column1, Column<?, Long> column2, DSLQuery dslQuery,
		Table<?> table, Predicate onPredicate) {

		Stack<BaseASTNode> allBaseASTNodes = getAllBaseASTNodes(
			JoinStep.class, dslQuery);

		JoinStep joinStep = (JoinStep)allBaseASTNodes.pop();

		if (column2 == null) {
			column2 = getPrimaryKeyColumn(getTable(joinStep));
		}

		Predicate predicate = column1.eq(column2);

		if (onPredicate != null) {
			predicate = predicate.and(onPredicate);
		}

		BaseASTNode baseASTNode = (BaseASTNode)joinStep.leftJoinOn(
			table, predicate);

		return updateParents(baseASTNode, allBaseASTNodes);
	}

	protected boolean contains(DSLQuery dslQuery, Table<?> table) {
		BaseASTNode baseASTNode = (BaseASTNode)dslQuery;

		while (baseASTNode != null) {
			if (baseASTNode instanceof JoinStep) {
				Table<?> currentTable = getTable((JoinStep)baseASTNode);

				if (StringUtil.equals(
						currentTable.getName(), table.getName())) {

					return true;
				}
			}

			baseASTNode = (BaseASTNode)baseASTNode.getChild();
		}

		return false;
	}

	protected Table getAliasedTable(String prefix, Table table) {
		if (Validator.isBlank(prefix)) {
			return table;
		}

		return table.as(prefix + CharPool.UNDERLINE + table.getName());
	}

	protected Stack<BaseASTNode> getAllBaseASTNodes(
		Class<?> clazz, DSLQuery dslQuery) {

		Stack<BaseASTNode> allBaseASTNodes = new Stack<>();

		BaseASTNode baseASTNode = (BaseASTNode)dslQuery;

		while ((baseASTNode != null) && !clazz.isInstance(baseASTNode)) {
			allBaseASTNodes.push(baseASTNode);

			baseASTNode = (BaseASTNode)baseASTNode.getChild();
		}

		if (baseASTNode == null) {
			throw new IllegalStateException("Base AST node is null");
		}

		allBaseASTNodes.push(baseASTNode);

		return allBaseASTNodes;
	}

	protected Column<?, Long> getPrimaryKeyColumn(Table<?> table) {
		if (table instanceof DynamicObjectDefinitionLocalizationTable) {
			DynamicObjectDefinitionLocalizationTable
				dynamicObjectDefinitionLocalizationTable =
					(DynamicObjectDefinitionLocalizationTable)table;

			return dynamicObjectDefinitionLocalizationTable.
				getForeignKeyColumn();
		}
		else if (table instanceof DynamicObjectDefinitionTable) {
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
				(DynamicObjectDefinitionTable)table;

			return dynamicObjectDefinitionTable.getPrimaryKeyColumn();
		}

		ObjectEntryTable objectEntryTable = (ObjectEntryTable)table;

		return objectEntryTable.objectEntryId;
	}

	protected Table<?> getTable(JoinStep joinStep) {
		if (joinStep instanceof From) {
			From from = (From)joinStep;

			return from.getTable();
		}
		else if (joinStep instanceof Join) {
			Join join = (Join)joinStep;

			return join.getTable();
		}

		throw new RuntimeException();
	}

	protected DSLQuery updateParents(
		BaseASTNode baseASTNode, Stack<BaseASTNode> parentBaseASTNodes) {

		while (!parentBaseASTNodes.empty()) {
			BaseASTNode currentBaseASTNode = parentBaseASTNodes.pop();

			baseASTNode = currentBaseASTNode.withNewChild(baseASTNode);
		}

		return (DSLQuery)baseASTNode;
	}

	protected ObjectFieldLocalService objectFieldLocalService;
	protected ObjectRelationshipLocalService objectRelationshipLocalService;

}