/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0.util;

import com.liferay.object.model.ObjectEntryVersionTable;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.spi.expression.DSLFunction;
import com.liferay.petra.sql.dsl.spi.expression.DSLFunctionType;
import com.liferay.petra.sql.dsl.spi.expression.Scalar;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;

import java.sql.Clob;

/**
 * @author Thiago Buarque
 */
public class ObjectEntryVersionTitleExpressionUtil {

	public static Expression<Clob> getLocalizedTitleExpression(
		String languageId) {

		Column<ObjectEntryVersionTable, Clob> contentColumn =
			ObjectEntryVersionTable.INSTANCE.content;

		DB db = DBManagerUtil.getDB();

		if (db.getDBType() == DBType.HYPERSONIC) {
			DSLFunction<Object> dslFunction1 = new DSLFunction<>(
				new DSLFunctionType("REGEXP_SUBSTRING(", ")"),
				new DSLFunction<>(
					new DSLFunctionType("CONVERT(", ", SQL_VARCHAR)"),
					contentColumn),
				new DSLFunction<>(
					new DSLFunctionType("CAST(", " AS LONGVARCHAR)"),
					new Scalar<>("(?s)\"title_i18n\"\\s*:\\s*(\\{.*?\\})")));

			DSLFunction<Object> dslFunction2 = new DSLFunction<>(
				new DSLFunctionType("REGEXP_SUBSTRING(", ")"), dslFunction1,
				new DSLFunction<>(
					new DSLFunctionType("CAST(", " AS LONGVARCHAR)"),
					new Scalar<>(
						StringBundler.concat(
							"\"", languageId, "\"\\s*:\\s*\"([^\"]*)\""))));

			return new DSLFunction<>(
				new DSLFunctionType("REGEXP_REPLACE(", ")"), dslFunction2,
				new DSLFunction<>(
					new DSLFunctionType("CAST(", " AS LONGVARCHAR)"),
					new Scalar<>(
						StringBundler.concat(
							"^\"", languageId, "\"\\s*:\\s*\"([^\"]*)\"$"))),
				new DSLFunction<>(
					new DSLFunctionType("CAST(", " AS LONGVARCHAR)"),
					new Scalar<>("$1")));
		}

		return _getPropertyValueExpression(
			contentColumn, "properties.title_i18n." + languageId);
	}

	public static Expression<Clob> getTitleExpression() {
		Column<ObjectEntryVersionTable, Clob> contentColumn =
			ObjectEntryVersionTable.INSTANCE.content;

		DB db = DBManagerUtil.getDB();

		if (db.getDBType() == DBType.HYPERSONIC) {
			DSLFunction<Object> dslFunction = new DSLFunction<>(
				new DSLFunctionType("REGEXP_SUBSTRING(", ")"),
				new DSLFunction<>(
					new DSLFunctionType("CONVERT(", ", SQL_VARCHAR)"),
					contentColumn),
				new DSLFunction<>(
					new DSLFunctionType("CAST(", " AS LONGVARCHAR)"),
					new Scalar<>(
						"\"title\"\\s*:\\s*\"(?:[^\"\\\\]|\\\\.)*\"")));

			return new DSLFunction<>(
				new DSLFunctionType("REGEXP_REPLACE(", ")"), dslFunction,
				new DSLFunction<>(
					new DSLFunctionType("CAST(", " AS LONGVARCHAR)"),
					new Scalar<>("^\"title\"\\s*:\\s*\"([^\"]*)\"$")),
				new DSLFunction<>(
					new DSLFunctionType("CAST(", " AS LONGVARCHAR)"),
					new Scalar<>("$1")));
		}

		return _getPropertyValueExpression(contentColumn, "properties.title");
	}

	private static <T> Expression<T> _getPropertyValueExpression(
		Expression<T> columnExpression, String propertyPath) {

		DB db = DBManagerUtil.getDB();

		if ((db.getDBType() == DBType.MARIADB) ||
			(db.getDBType() == DBType.MYSQL)) {

			return new DSLFunction<>(
				new DSLFunctionType("JSON_UNQUOTE(", ")"),
				new DSLFunction<>(
					new DSLFunctionType("JSON_EXTRACT(", ")"), columnExpression,
					new Scalar<>("$." + propertyPath)));
		}
		else if (db.getDBType() == DBType.POSTGRESQL) {
			String[] propertyPathParts = propertyPath.split("\\.");

			Expression[] expressions =
				new Expression[propertyPathParts.length + 1];

			expressions[0] = new DSLFunction<>(
				new DSLFunctionType("CAST(", " AS JSON)"), columnExpression);

			for (int i = 0; i < propertyPathParts.length; i++) {
				expressions[i + 1] = new Scalar<>(propertyPathParts[i]);
			}

			return new DSLFunction<>(
				new DSLFunctionType("JSON_EXTRACT_PATH_TEXT(", ")"),
				expressions);
		}

		return new DSLFunction<>(
			new DSLFunctionType("JSON_VALUE(", ")"), columnExpression,
			new Scalar<>("$." + propertyPath));
	}

}