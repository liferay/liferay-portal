/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;

/**
 * @author Manuel de la Peña
 */
public class SQLServerSQLTransformerLogic extends BaseSQLTransformerLogic {

	public SQLServerSQLTransformerLogic(DB db) {
		super(db);

		Function[] functions = {
			getAggregationFunction(), getBitwiseCheckFunction(),
			getBooleanFunction(), getCastClobTextFunction(),
			getCastDecimalFunction(), getCastLongFunction(),
			getCastTextFunction(), getConcatFunction(),
			getDropTableIfExistsTextFunction(), getInstrFunction(),
			getIntegerDivisionFunction(), getLengthFunction(), getModFunction(),
			getNullDateFunction(), getSubstrFunction()
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
					"CASE WHEN $2(CAST($3 AS INT)) = 1 THEN 1 ELSE 0 END");
			}
		}

		return super.replaceAggregation(matcher);
	}

	@Override
	protected String replaceCastText(Matcher matcher) {
		return matcher.replaceAll("CAST($1 AS NVARCHAR(MAX))");
	}

	@Override
	protected String replaceDropTableIfExistsText(Matcher matcher) {
		String dropTableIfExists =
			"IF OBJECT_ID('$1', 'U') IS NOT NULL DROP TABLE $1";

		return matcher.replaceAll(dropTableIfExists);
	}

}