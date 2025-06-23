/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tamyris Bernardo
 */
public class UpgradeSetResultsSetTotalMethodCheck extends BaseUpgradeCheck {

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (fileName.endsWith(".java")) {
			content = _formatJava(content, fileName);
		}
		else if (fileName.endsWith(".jsp") || fileName.endsWith(".jspf")) {
			content = _formatContent(content, content, fileName);
		}

		return content;
	}

	@Override
	protected String[] getValidExtensions() {
		return new String[] {"java", "jsp", "jspf"};
	}

	private String _addTryCatch(
		String methodCall, String newContent, String stringBuilder) {

		newContent = StringUtil.replace(newContent, methodCall, stringBuilder);

		Matcher matcher = _setResultsAndTotalPattern.matcher(newContent);

		String newMethodCall = null;
		StringBuilder sb = new StringBuilder();

		if (matcher.find()) {
			newMethodCall = matcher.group();

			String tabs = SourceUtil.getIndent(newMethodCall);

			String newLineAndTab = StringPool.NEW_LINE + StringPool.TAB;

			sb.append(tabs);
			sb.append("try {");
			sb.append(newLineAndTab);
			sb.append(newMethodCall);
			sb.append(StringPool.NEW_LINE);
			sb.append(tabs);
			sb.append("}");
			sb.append(StringPool.NEW_LINE);
			sb.append(tabs);
			sb.append("catch (Exception exception) {");
			sb.append(newLineAndTab);
			sb.append(tabs);
			sb.append("throw new PortalException(exception);");
			sb.append(StringPool.NEW_LINE);
			sb.append(tabs);
			sb.append(StringPool.CLOSE_CURLY_BRACE);
		}

		return StringUtil.replace(newContent, newMethodCall, sb.toString());
	}

	private String _formatContent(
		String content, String fileContent, String fileName) {

		String newContent = fileContent;

		Matcher matcher = _setResultsPattern.matcher(content);

		int setResultsIndex = 0;

		String setResultsMethodCall = null;

		if (matcher.find()) {
			setResultsIndex = matcher.start();

			setResultsMethodCall = JavaSourceUtil.getMethodCall(
				content, setResultsIndex);
		}

		matcher = _setTotalPattern.matcher(content);

		int setTotalIndex = 0;

		String setTotalMethodCall = null;

		if (matcher.find()) {
			setTotalIndex = matcher.start();

			setTotalMethodCall = JavaSourceUtil.getMethodCall(
				content, setTotalIndex);
		}

		if (!hasClassOrVariableName(
				"SearchContainer", content, newContent, fileName,
				setResultsMethodCall) &&
			!hasClassOrVariableName(
				"SearchContainer", content, newContent, fileName,
				setTotalMethodCall)) {

			return newContent;
		}

		return StringUtil.replace(
			newContent, content,
			_getNewMethodCall(
				content, setResultsIndex, setResultsMethodCall, setTotalIndex,
				setTotalMethodCall));
	}

	private String _formatJava(String content, String fileName)
		throws Exception {

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		if (javaClass == null) {
			return content;
		}

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			String javaMethodContent = javaMethod.getContent();

			content = _formatContent(javaMethodContent, content, fileName);
		}

		return content;
	}

	private String _getNewMethodCall(
		String content, int setResultsIndex, String setResultsMethodCall,
		int setTotalIndex, String setTotalMethodCall) {

		String newContent = content;

		String lastMethodCall = null;

		List<String> parameters = new ArrayList<>();

		if ((setResultsMethodCall != null) && (setTotalMethodCall != null)) {
			String firstMethodCall = null;

			if (setResultsIndex < setTotalIndex) {
				firstMethodCall = setResultsMethodCall;
				lastMethodCall = setTotalMethodCall;
			}
			else {
				firstMethodCall = setTotalMethodCall;
				lastMethodCall = setResultsMethodCall;
			}

			newContent = StringUtil.removeSubstring(
				content,
				firstMethodCall + StringPool.SEMICOLON + StringPool.NEW_LINE);

			parameters.add(
				"() -> " + JavaSourceUtil.getParameters(setResultsMethodCall));
			parameters.add(JavaSourceUtil.getParameters(setTotalMethodCall));
		}
		else if ((setResultsMethodCall == null) &&
				 (setTotalMethodCall != null)) {

			lastMethodCall = setTotalMethodCall;

			parameters.add(
				getVariableName(setTotalMethodCall) + "::getResults");
			parameters.add(JavaSourceUtil.getParameters(setTotalMethodCall));
		}
		else if ((setResultsMethodCall != null) &&
				 (setTotalMethodCall == null)) {

			lastMethodCall = setResultsMethodCall;

			parameters.add(JavaSourceUtil.getParameters(setResultsMethodCall));
		}

		StringBuilder sb = new StringBuilder();

		sb.append(getVariableName(lastMethodCall));
		sb.append(".setResultsAndTotal(");
		sb.append(StringUtil.merge(parameters, StringPool.COMMA_AND_SPACE));
		sb.append(StringPool.CLOSE_PARENTHESIS);

		if ((setResultsMethodCall != null) && (setTotalMethodCall != null)) {
			return _addTryCatch(lastMethodCall, newContent, sb.toString());
		}

		return StringUtil.replace(newContent, lastMethodCall, sb.toString());
	}

	private static final Pattern _setResultsAndTotalPattern = Pattern.compile(
		"\\t*\\w+\\.setResultsAndTotal\\(\\(\\)[^)]+\\);");
	private static final Pattern _setResultsPattern = Pattern.compile(
		"\\t*\\w+\\.setResults\\(");
	private static final Pattern _setTotalPattern = Pattern.compile(
		"\\t*\\w+\\.setTotal\\(");

}