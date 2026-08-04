/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.petra.string.StringBundler;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Manuel de la Peña
 */
public class HibernateSQLTransformerLogic extends BaseSQLTransformerLogic {

	public HibernateSQLTransformerLogic() {
		setFunctions(
			getBooleanFunction(), getCastDecimalFunction(),
			getCastLongFunction(), getCastTextFunction(), getInstrFunction(),
			getNullDateFunction(), getSubstrFunction(), _getCountFunction(),
			_getPositionalParameterFunction(),
			_getUnsupportedMacroFunction(
				"AGGREGATION", getAggregationPattern()),
			_getUnsupportedMacroFunction("BITAND", getBitwiseCheckPattern()),
			_getUnsupportedMacroFunction("BITOR", getBitwiseOrPattern()),
			_getUnsupportedMacroFunction(
				"DROP_TABLE_IF_EXISTS", getDropTableIfExistsTextPattern()),
			_getUnsupportedMacroFunction(
				"INTEGER_DIV", getIntegerDivisionPattern()),
			_getUnsupportedMacroFunction(
				"TRUNCATE TABLE", getTruncateTablePattern()));
	}

	@Override
	protected String replaceCastDecimal(Matcher matcher) {
		return matcher.replaceAll("CAST($1 AS big_decimal)");
	}

	@Override
	protected String replaceCastLong(Matcher matcher) {
		return matcher.replaceAll("CAST($1 AS long)");
	}

	@Override
	protected String replaceCastText(Matcher matcher) {
		return matcher.replaceAll("CAST($1 AS string)");
	}

	@Override
	protected String replaceInstr(Matcher matcher) {
		return matcher.replaceAll("LOCATE($2, $1)");
	}

	private Function<String, String> _getCountFunction() {
		return (String sql) -> {
			Matcher matcher = _countPattern.matcher(sql);

			if (matcher.find()) {
				String countExpression = matcher.group(1);
				String entityAlias = matcher.group(3);

				if (entityAlias.equals(countExpression)) {
					return matcher.replaceFirst(_HQL_COUNT_SQL);
				}
			}

			return sql;
		};
	}

	private Function<String, String> _getPositionalParameterFunction() {
		return (String sql) -> {
			if (!sql.contains("?")) {
				return sql;
			}

			StringBundler sb = new StringBundler();

			int counter = 1;
			boolean quoted = false;

			for (int i = 0; i < sql.length(); i++) {
				char c = sql.charAt(i);

				if (c == '\'') {
					quoted = !quoted;
				}

				if ((c == '?') && !quoted) {
					sb.append('?');
					sb.append(counter++);
				}
				else {
					sb.append(c);
				}
			}

			return sb.toString();
		};
	}

	private Function<String, String> _getUnsupportedMacroFunction(
		String macro, Pattern pattern) {

		return (String sql) -> {
			Matcher matcher = pattern.matcher(sql);

			if (matcher.find()) {
				throw new UnsupportedOperationException(
					StringBundler.concat(
						"The macro \"", macro,
						"\" has no HQL equivalent and cannot be used in an ",
						"HQL query: ", sql));
			}

			return sql;
		};
	}

	private static final String _HQL_COUNT_SQL = "SELECT COUNT(*) FROM $2 $3";

	private static final Pattern _countPattern = Pattern.compile(
		"SELECT COUNT\\((\\S+)\\) FROM (\\S+) (\\S+)");

}