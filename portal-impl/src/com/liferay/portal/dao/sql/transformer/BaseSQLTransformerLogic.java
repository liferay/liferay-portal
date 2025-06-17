/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.internal.dao.sql.transformer.SQLFunctionTransformer;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Manuel de la Peña
 * @author Brian Wing Shun Chan
 */
public abstract class BaseSQLTransformerLogic implements SQLTransformerLogic {

	public BaseSQLTransformerLogic(DB db) {
		_db = db;
	}

	@Override
	public Function<String, String>[] getFunctions() {
		return _functions;
	}

	protected Function<String, String> getAggregationFunction() {
		Pattern pattern = getAggregationPattern();

		return (String sql) -> replaceAggregation(pattern.matcher(sql));
	}

	protected Pattern getAggregationPattern() {
		return Pattern.compile(
			"AGGREGATION_(.+?)_(.+?)\\(\\s*(.+?)\\s*\\)",
			Pattern.CASE_INSENSITIVE);
	}

	protected Function<String, String> getBitwiseCheckFunction() {
		Pattern pattern = getBitwiseCheckPattern();

		return (String sql) -> replaceBitwiseCheck(pattern.matcher(sql));
	}

	protected Pattern getBitwiseCheckPattern() {
		return Pattern.compile("BITAND\\(\\s*(.+?)\\s*,\\s*(.+?)\\s*\\)");
	}

	protected Function<String, String> getBooleanFunction() {
		return (String sql) -> StringUtil.replace(
			sql, new String[] {"[$FALSE$]", "[$TRUE$]"},
			new String[] {_db.getTemplateFalse(), _db.getTemplateTrue()});
	}

	protected Function<String, String> getCastClobTextFunction() {
		return _getCastFunction(
			matcher -> replaceCastClobText(matcher), "CAST_CLOB_TEXT",
			getCastClobTextPattern());
	}

	protected Pattern getCastClobTextPattern() {
		return Pattern.compile(
			"CAST_CLOB_TEXT\\((.*)\\)", Pattern.CASE_INSENSITIVE);
	}

	protected Function<String, String> getCastDecimalFunction() {
		return _getCastFunction(
			matcher -> replaceCastDecimal(matcher), "CAST_DECIMAL",
			getCastDecimalPattern());
	}

	protected Pattern getCastDecimalPattern() {
		return Pattern.compile(
			"CAST_DECIMAL\\((.*)\\)", Pattern.CASE_INSENSITIVE);
	}

	protected Function<String, String> getCastLongFunction() {
		return _getCastFunction(
			matcher -> replaceCastLong(matcher), "CAST_LONG",
			getCastLongPattern());
	}

	protected Pattern getCastLongPattern() {
		return Pattern.compile("CAST_LONG\\((.*)\\)", Pattern.CASE_INSENSITIVE);
	}

	protected Function<String, String> getCastTextFunction() {
		return _getCastFunction(
			matcher -> replaceCastText(matcher), "CAST_TEXT",
			getCastTextPattern());
	}

	protected Pattern getCastTextPattern() {
		return Pattern.compile("CAST_TEXT\\((.*)\\)", Pattern.CASE_INSENSITIVE);
	}

	protected Function<String, String> getConcatFunction() {
		SQLFunctionTransformer sqlFunctionTransformer =
			new SQLFunctionTransformer(
				"CONCAT(", StringPool.BLANK, " + ", StringPool.BLANK);

		return sqlFunctionTransformer::transform;
	}

	protected Function<String, String> getDropTableIfExistsTextFunction() {
		Pattern pattern = getDropTableIfExistsTextPattern();

		return (String sql) -> replaceDropTableIfExistsText(
			pattern.matcher(sql));
	}

	protected Pattern getDropTableIfExistsTextPattern() {
		return Pattern.compile(
			"DROP_TABLE_IF_EXISTS\\((.+?)\\)", Pattern.CASE_INSENSITIVE);
	}

	protected Function<String, String> getInstrFunction() {
		Pattern pattern = getInstrPattern();

		return (String sql) -> replaceInstr(pattern.matcher(sql));
	}

	protected Pattern getInstrPattern() {
		return Pattern.compile(
			"INSTR\\(\\s*(.+?)\\s*,\\s*(.+?)\\s*\\)", Pattern.CASE_INSENSITIVE);
	}

	protected Function<String, String> getIntegerDivisionFunction() {
		Pattern pattern = getIntegerDivisionPattern();

		return (String sql) -> replaceIntegerDivision(pattern.matcher(sql));
	}

	protected Pattern getIntegerDivisionPattern() {
		return Pattern.compile(
			"INTEGER_DIV\\(\\s*(.+?)\\s*,\\s*(.+?)\\s*\\)",
			Pattern.CASE_INSENSITIVE);
	}

	protected Function<String, String> getLengthFunction() {
		return (String sql) -> StringUtil.replace(sql, "LENGTH(", "LEN(");
	}

	protected Function<String, String> getLowerFunction() {
		return (String sql) -> {
			int x = sql.indexOf(_LOWER_OPEN);

			if (x == -1) {
				return sql;
			}

			StringBundler sb = new StringBundler(sql.length());

			int y = 0;

			while (true) {
				sb.append(sql.substring(y, x));

				y = sql.indexOf(_LOWER_CLOSE, x);

				if (y == -1) {
					sb.append(sql.substring(x));

					break;
				}

				sb.append(sql.substring(x + _LOWER_OPEN.length(), y));

				y++;

				x = sql.indexOf(_LOWER_OPEN, y);

				if (x == -1) {
					sb.append(sql.substring(y));

					break;
				}
			}

			return sb.toString();
		};
	}

	protected Function<String, String> getModFunction() {
		Pattern pattern = getModPattern();

		return (String sql) -> replaceMod(pattern.matcher(sql));
	}

	protected Pattern getModPattern() {
		return Pattern.compile(
			"MOD\\(\\s*(.+?)\\s*,\\s*(.+?)\\s*\\)", Pattern.CASE_INSENSITIVE);
	}

	protected Function<String, String> getNullDateFunction() {
		return (String sql) -> StringUtil.replace(sql, "[$NULL_DATE$]", "NULL");
	}

	protected Function<String, String> getSubstrFunction() {
		Pattern pattern = getSubstrPattern();

		return (String sql) -> replaceSubstr(pattern.matcher(sql));
	}

	protected Pattern getSubstrPattern() {
		return Pattern.compile(
			"SUBSTR\\(\\s*(.+?)\\s*,\\s*(.+?)\\s*,\\s*(.+?)\\s*\\)",
			Pattern.CASE_INSENSITIVE);
	}

	protected String replaceAggregation(Matcher matcher) {
		return matcher.replaceAll("$2($3)");
	}

	protected String replaceBitwiseCheck(Matcher matcher) {
		return matcher.replaceAll("($1 & $2)");
	}

	protected String replaceCastClobText(Matcher matcher) {
		return replaceCastText(matcher);
	}

	protected String replaceCastDecimal(Matcher matcher) {
		return matcher.replaceAll("CAST($1 AS DECIMAL(31, 2))");
	}

	protected String replaceCastLong(Matcher matcher) {
		return matcher.replaceAll("$1");
	}

	protected String replaceCastText(Matcher matcher) {
		return matcher.replaceAll("$1");
	}

	protected String replaceDropTableIfExistsText(Matcher matcher) {
		return matcher.replaceAll("DROP TABLE IF EXISTS $1");
	}

	protected String replaceInstr(Matcher matcher) {
		return matcher.replaceAll("CHARINDEX($2, $1)");
	}

	protected String replaceIntegerDivision(Matcher matcher) {
		return matcher.replaceAll("$1 / $2");
	}

	protected String replaceMod(Matcher matcher) {
		return matcher.replaceAll("$1 % $2");
	}

	protected String replaceSubstr(Matcher matcher) {
		return matcher.replaceAll("SUBSTRING($1, $2, $3)");
	}

	protected void setFunctions(Function... functions) {
		_functions = functions;
	}

	private Function<String, String> _getCastFunction(
		Function<Matcher, String> castFunction, String castName,
		Pattern castPattern) {

		return new Function<String, String>() {

			@Override
			public String apply(String sql) {
				int start = sql.indexOf(castName + StringPool.OPEN_PARENTHESIS);

				if (start == -1) {
					return sql;
				}

				int parenthesisCount = 0;

				for (int i = start + castName.length(); i < sql.length(); i++) {
					char character = sql.charAt(i);

					if (character == CharPool.OPEN_PARENTHESIS) {
						parenthesisCount++;
					}
					else if (character == CharPool.CLOSE_PARENTHESIS) {
						parenthesisCount--;
					}

					if (parenthesisCount == 0) {
						Matcher matcher = castPattern.matcher(
							sql.substring(start, i + 1));

						return sql.substring(0, start) +
							apply(castFunction.apply(matcher)) +
								apply(_safeSubstring(sql, i + 1));
					}
				}

				return sql;
			}

			private String _safeSubstring(String sql, int index) {
				if (index >= sql.length()) {
					return StringPool.BLANK;
				}

				return sql.substring(index);
			}

		};
	}

	private static final String _LOWER_CLOSE = StringPool.CLOSE_PARENTHESIS;

	private static final String _LOWER_OPEN = "lower(";

	private final DB _db;
	private Function[] _functions;

}