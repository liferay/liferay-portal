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
 * @author Brian Wing Shun Chan
 */
public class HypersonicSQLTransformerLogic extends BaseSQLTransformerLogic {

	public HypersonicSQLTransformerLogic(DB db) {
		super(db);

		Function[] functions = {
			getAggregationFunction(), getBooleanFunction(),
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
	protected Function<String, String> getNullDateFunction() {
		return (String sql) -> StringUtil.replace(
			sql, "[$NULL_DATE$]", "CAST(NULL AS DATE)");
	}

	@Override
	protected String replaceCastLong(Matcher matcher) {
		return matcher.replaceAll("CONVERT($1, SQL_BIGINT)");
	}

	@Override
	protected String replaceCastText(Matcher matcher) {
		return matcher.replaceAll("CONVERT($1, SQL_VARCHAR)");
	}

	@Override
	protected String replaceDropTableIfExistsText(Matcher matcher) {
		return matcher.replaceAll("DROP TABLE $1 IF EXISTS");
	}

}