/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.ParseException;

import java.io.IOException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Albert Gomes Cabral
 */
public class UpgradeJavaGetFDSTableSchemaParameterCheck
	extends BaseUpgradeCheck {

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws IOException, ParseException {

		String newContent = content;

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		List<String> extendedClassNames = javaClass.getExtendedClassNames();

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			if (extendedClassNames.contains("BaseTableFDSView")) {
				JavaMethod javaMethod = (JavaMethod)childJavaTerm;

				String javaMethodContent = javaMethod.getContent();

				Matcher matcher = _getFDSTableSchemaPattern.matcher(
					javaMethodContent);

				while (matcher.find()) {
					String methodCall = matcher.group();

					newContent = StringUtil.replace(
						content, methodCall,
						StringUtil.replace(
							methodCall, matcher.group(1), "(Locale locale)"));
				}

				matcher = _fDSTableSchemaFieldPattern.matcher(
					javaMethodContent);

				if (matcher.find()) {
					newContent = _formatFDSTableSchemaFieldCall(
						newContent, javaMethodContent, matcher);
				}
			}
		}

		return newContent;
	}

	@Override
	protected String[] getNewImports() {
		return new String[] {"java.util.Locale"};
	}

	private String _formatFDSTableSchemaFieldCall(
		String content, String javaMethodContent, Matcher matcher) {

		String newContent = content;

		Pattern pattern = Pattern.compile(
			"\\s*" + Pattern.quote(matcher.group(1)) +
				"\\s*(\\.\\w+\\([^)]*\\))\\s*;");

		Matcher javaMethodMatcher = pattern.matcher(javaMethodContent);

		if (!javaMethodMatcher.find()) {
			return newContent;
		}

		String indent = SourceUtil.getIndent(matcher.group());

		String newLineAndIndent = StringPool.NEW_LINE + indent;

		StringBundler sb = new StringBundler();

		sb.append(indent);
		sb.append(matcher.group(2));
		sb.append(newLineAndIndent);
		sb.append(StringPool.TAB);
		sb.append(matcher.group(3));

		newContent = StringUtil.removeSubstring(
			newContent, javaMethodMatcher.group());

		sb.append(StringPool.COMMA);
		sb.append(newLineAndIndent);
		sb.append("	fdsTableSchemaField -> fdsTableSchemaField");
		sb.append(javaMethodMatcher.group(1));

		while (javaMethodMatcher.find()) {
			sb.append(newLineAndIndent);
			sb.append(StringPool.TAB);
			sb.append(javaMethodMatcher.group(1));

			newContent = StringUtil.removeSubstring(
				newContent, javaMethodMatcher.group());
		}

		sb.append(StringPool.CLOSE_PARENTHESIS);
		sb.append(StringPool.SEMICOLON);

		return StringUtil.replace(
			newContent, matcher.group(),
			StringUtil.replace(
				matcher.group(), matcher.group(), sb.toString()));
	}

	private static final Pattern _fDSTableSchemaFieldPattern = Pattern.compile(
		"^[^\\n]\\s+FDSTableSchemaField\\s+(\\w+)\\s=\\s*(\\w+\\" +
			".add\\()\\s*([^)]*)\\)\\s*;",
		Pattern.MULTILINE);
	private static final Pattern _getFDSTableSchemaPattern = Pattern.compile(
		"\\w+\\s+FDSTableSchema\\s+getFDSTableSchema(\\(\\))");

}