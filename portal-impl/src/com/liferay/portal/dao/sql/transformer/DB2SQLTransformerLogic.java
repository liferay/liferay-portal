/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.internal.dao.sql.transformer.SQLFunctionTransformer;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Manuel de la Peña
 */
public class DB2SQLTransformerLogic extends BaseSQLTransformerLogic {

	public DB2SQLTransformerLogic(DB db) {
		super(db);

		Function[] functions = {
			getAggregationFunction(), getBooleanFunction(),
			getCastClobTextFunction(), getCastDecimalFunction(),
			getCastLongFunction(), getCastTextFunction(), getConcatFunction(),
			getDropTableIfExistsTextFunction(), getIntegerDivisionFunction(),
			getNullDateFunction(), _getCaseWhenThenFunction(),
			_getLikeFunction(), _getSelectFunction()
		};

		if (!db.isSupportsStringCaseSensitiveQuery()) {
			functions = ArrayUtil.append(functions, getLowerFunction());
		}

		setFunctions(functions);
	}

	@Override
	protected Function<String, String> getConcatFunction() {
		SQLFunctionTransformer sqlFunctionTransformer =
			new SQLFunctionTransformer(
				"CONCAT(", StringPool.BLANK, " CONCAT ", StringPool.BLANK);

		return sqlFunctionTransformer::transform;
	}

	@Override
	protected String replaceCastText(Matcher matcher) {
		return matcher.replaceAll("CAST($1 AS VARCHAR(2000))");
	}

	@Override
	protected String replaceDropTableIfExistsText(Matcher matcher) {
		String dropTableIfExists = StringBundler.concat(
			"BEGIN\n", "DECLARE CONTINUE HANDLER FOR SQLSTATE '42704'\n",
			"BEGIN END;\n", "EXECUTE IMMEDIATE 'DROP TABLE $1';\n", "END");

		return matcher.replaceAll(dropTableIfExists);
	}

	private Function<String, String> _getCaseWhenThenFunction() {
		return (String sql) -> _replaceQuestionParameterMarker(
			_caseWhenThenPattern.matcher(sql), sql);
	}

	private Function<String, String> _getLikeFunction() {
		return (String sql) -> {
			Matcher matcher = _likePattern.matcher(sql);

			return matcher.replaceAll(
				"LIKE COALESCE(CAST(? AS VARCHAR(2000)),'')");
		};
	}

	private Function<String, String> _getSelectFunction() {
		return (String sql) -> _replaceQuestionParameterMarker(
			_selectPattern.matcher(sql), sql);
	}

	private String _replaceQuestionParameterMarker(
		Matcher matcher, String sql) {

		int index = 0;

		StringBundler sb = new StringBundler();

		while (matcher.find()) {
			if (matcher.start() > index) {
				sb.append(sql.substring(index, matcher.start()));
			}

			sb.append(
				StringUtil.replace(
					matcher.group(),
					new String[] {
						StringBundler.concat(
							StringPool.SPACE, StringPool.QUESTION,
							StringPool.COMMA),
						StringBundler.concat(
							StringPool.SPACE, StringPool.QUESTION,
							StringPool.SPACE)
					},
					new String[] {
						StringBundler.concat(
							StringPool.SPACE,
							_QUESTION_PARAMETER_MARKER_REPLACEMENT,
							StringPool.COMMA),
						StringBundler.concat(
							StringPool.SPACE,
							_QUESTION_PARAMETER_MARKER_REPLACEMENT,
							StringPool.SPACE)
					}));

			index = matcher.end();
		}

		if (index < (sql.length() - 1)) {
			sb.append(sql.substring(index));
		}

		return sb.toString();
	}

	private static final String _QUESTION_PARAMETER_MARKER_REPLACEMENT =
		"COALESCE(CAST(? AS VARCHAR(2000)),'')";

	private static final Pattern _caseWhenThenPattern = Pattern.compile(
		"\\bcase when.+?end\\b", Pattern.CASE_INSENSITIVE);
	private static final Pattern _likePattern = Pattern.compile(
		"LIKE \\?", Pattern.CASE_INSENSITIVE);
	private static final Pattern _selectPattern = Pattern.compile(
		"select .+?from ", Pattern.CASE_INSENSITIVE);

}