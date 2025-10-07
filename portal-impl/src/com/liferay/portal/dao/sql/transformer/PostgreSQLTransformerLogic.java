/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Manuel de la Peña
 */
public class PostgreSQLTransformerLogic extends BaseSQLTransformerLogic {

	public PostgreSQLTransformerLogic(DB db) {
		super(db);

		Function[] functions = {
			getAggregationFunction(), getBitwiseCheckFunction(),
			getBitwiseOrFunction(), getBooleanFunction(),
			getCastClobTextFunction(), getCastDecimalFunction(),
			getCastLongFunction(), getCastTextFunction(),
			getDropTableIfExistsTextFunction(), getInstrFunction(),
			getIntegerDivisionFunction(), getTruncateTableFunction(),
			_getNegativeComparisonFunction(), _getNullDateFunction()
		};

		if (!db.isSupportsStringCaseSensitiveQuery()) {
			functions = ArrayUtil.append(functions, getLowerFunction());
		}

		setFunctions(functions);
	}

	@Override
	protected String replaceAggregation(Matcher matcher) {
		if (matcher.find()) {
			String type = matcher.group(1);

			if (Objects.equals(type, "BOOLEAN")) {
				return matcher.replaceAll(
					"CASE WHEN $2(CAST($3 AS INTEGER)) = 1 THEN 1 ELSE 0 END");
			}
		}

		return super.replaceAggregation(matcher);
	}

	@Override
	protected String replaceCastLong(Matcher matcher) {
		return matcher.replaceAll("CAST($1 AS BIGINT)");
	}

	@Override
	protected String replaceCastText(Matcher matcher) {
		return matcher.replaceAll("CAST($1 AS TEXT)");
	}

	@Override
	protected String replaceInstr(Matcher matcher) {
		return matcher.replaceAll("POSITION($2 in $1)");
	}

	private Function<String, String> _getNegativeComparisonFunction() {
		return (String sql) -> {
			Matcher matcher = _negativeComparisonPattern.matcher(sql);

			return matcher.replaceAll("$1 ($2)");
		};
	}

	private Function<String, String> _getNullDateFunction() {
		return (String sql) -> StringUtil.replace(
			sql, "[$NULL_DATE$]", "CAST(NULL AS TIMESTAMP)");
	}

	private static final Pattern _negativeComparisonPattern = Pattern.compile(
		"(!?=)\\s*(-([0-9]+)?)", Pattern.CASE_INSENSITIVE);

}