/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.function.Function;
import java.util.regex.Matcher;

/**
 * @author Manuel de la Peña
 */
public class MySQLSQLTransformerLogic extends BaseSQLTransformerLogic {

	public MySQLSQLTransformerLogic(DB db) {
		_db = db;

		Function[] functions = {
			getAggregationFunction(), getBitwiseCheckFunction(),
			getBitwiseOrFunction(), getBooleanFunction(),
			getCastClobTextFunction(), getCastDecimalFunction(),
			getCastLongFunction(), getCastTextFunction(),
			getDropTableIfExistsTextFunction(), getIntegerDivisionFunction(),
			getNullDateFunction(), getTruncateTableFunction()
		};

		if (!db.isSupportsStringCaseSensitiveQuery()) {
			functions = ArrayUtil.append(functions, getLowerFunction());
		}

		setFunctions(functions);
	}

	@Override
	protected Function<String, String> getBooleanFunction() {
		return (String sql) -> StringUtil.replace(
			sql, new String[] {"[$FALSE$]", "[$TRUE$]"},
			new String[] {_db.getTemplateFalse(), _db.getTemplateTrue()});
	}

	@Override
	protected String replaceIntegerDivision(Matcher matcher) {
		return matcher.replaceAll("$1 DIV $2");
	}

	private final DB _db;

}