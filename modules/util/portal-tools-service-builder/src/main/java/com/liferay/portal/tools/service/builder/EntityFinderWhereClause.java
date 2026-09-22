/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class EntityFinderWhereClause {

	public EntityFinderWhereClause(Entity entity, String where) {
		where = StringUtil.removeSubstring(
			where, entity.getAlias() + StringPool.PERIOD);

		String[] terms = StringUtil.split(
			StringUtil.removeChars(
				where, CharPool.OPEN_PARENTHESIS, CharPool.CLOSE_PARENTHESIS),
			_AND);

		Set<String> dbColumnNames = new LinkedHashSet<>();

		StringBundler sb = new StringBundler(terms.length * 2);

		for (String term : terms) {
			sb.append(
				_getTermJavaExpression(
					entity, dbColumnNames, term, terms.length > 1));
			sb.append(" && ");
		}

		sb.setIndex(sb.index() - 1);

		_dbColumnNames = dbColumnNames;
		_javaExpression = sb.toString();
	}

	public void addEntityFinder(EntityFinder entityFinder) {
		_entityFinders.add(entityFinder);
	}

	public Set<String> getDBColumnNames() {
		return _dbColumnNames;
	}

	public List<EntityFinder> getEntityFinders() {
		return _entityFinders;
	}

	public String getJavaExpression() {
		return _javaExpression;
	}

	private String _getColumnJavaExpression(
		Entity entity, EntityColumn entityColumn, Set<String> dbColumnNames) {

		String dbName = entityColumn.getDBName();

		dbColumnNames.add(dbName);

		String type = entityColumn.getType();

		if (type.equals("int")) {
			type = "Integer";
		}

		return StringBundler.concat(
			"GetterUtil.get", StringUtil.upperCaseFirstLetter(type),
			"(_getColumnValue(", entity.getVariableName(), "ModelImpl, \"",
			dbName, "\", original))");
	}

	private String _getLiteralJavaExpression(
		EntityColumn entityColumn, String literal) {

		if (literal.equals("[$TRUE$]")) {
			return "true";
		}

		if (literal.equals("[$FALSE$]")) {
			return "false";
		}

		String type = entityColumn.getType();

		if (type.equals("long")) {
			return literal + "L";
		}

		return literal;
	}

	private String _getTermJavaExpression(
		Entity entity, Set<String> dbColumnNames, String term,
		boolean parenthesize) {

		term = StringUtil.trim(term);

		if (term.endsWith(_IS_NOT_NULL)) {
			return StringBundler.concat(
				"Validator.isNotNull(",
				_getColumnJavaExpression(
					entity,
					entity.getEntityColumn(
						StringUtil.removeSubstring(term, _IS_NOT_NULL)),
					dbColumnNames),
				")");
		}

		if (term.endsWith(_IS_NULL)) {
			return StringBundler.concat(
				"Validator.isNull(",
				_getColumnJavaExpression(
					entity,
					entity.getEntityColumn(
						StringUtil.removeSubstring(term, _IS_NULL)),
					dbColumnNames),
				")");
		}

		String operator = null;
		int operatorIndex = -1;

		for (String candidateOperator : _OPERATORS) {
			operatorIndex = term.indexOf(candidateOperator);

			if (operatorIndex != -1) {
				operator = candidateOperator;

				break;
			}
		}

		String columnName = StringUtil.trim(term.substring(0, operatorIndex));

		EntityColumn entityColumn = entity.getEntityColumn(columnName);

		String leftJavaExpression = _getColumnJavaExpression(
			entity, entityColumn, dbColumnNames);

		String value = StringUtil.trim(
			term.substring(operatorIndex + operator.length()));

		EntityColumn valueEntityColumn = entity.fetchEntityColumn(value);

		String rightJavaExpression = null;

		if (valueEntityColumn == null) {
			rightJavaExpression = _getLiteralJavaExpression(
				entityColumn, value);
		}
		else {
			rightJavaExpression = _getColumnJavaExpression(
				entity, valueEntityColumn, dbColumnNames);
		}

		String type = entityColumn.getType();

		if (type.equals("String")) {
			if (operator.equals(StringPool.EQUAL)) {
				return StringBundler.concat(
					"Objects.equals(", leftJavaExpression, ", ",
					rightJavaExpression, ")");
			}

			if (operator.equals(StringPool.NOT_EQUAL)) {
				return StringBundler.concat(
					"!Objects.equals(", leftJavaExpression, ", ",
					rightJavaExpression, ")");
			}
		}

		if (type.equals("boolean") && (valueEntityColumn == null)) {
			boolean negated = rightJavaExpression.equals("false");

			if (operator.equals(StringPool.NOT_EQUAL)) {
				negated = !negated;
			}

			if (negated) {
				return "!" + leftJavaExpression;
			}

			return leftJavaExpression;
		}

		String javaOperator = operator;

		if (operator.equals(StringPool.EQUAL)) {
			javaOperator = "==";
		}

		if (parenthesize) {
			return StringBundler.concat(
				"(", leftJavaExpression, " ", javaOperator, " ",
				rightJavaExpression, ")");
		}

		return StringBundler.concat(
			leftJavaExpression, " ", javaOperator, " ", rightJavaExpression);
	}

	private static final String _AND = " AND ";

	private static final String _IS_NOT_NULL = " IS NOT NULL";

	private static final String _IS_NULL = " IS NULL";

	// The two character operators must come before the one character
	// operators, since the first operator found in a term wins

	private static final String[] _OPERATORS = {
		"<=", ">=", "!=", "=", "<", ">"
	};

	private final Set<String> _dbColumnNames;
	private final List<EntityFinder> _entityFinders = new ArrayList<>();
	private final String _javaExpression;

}