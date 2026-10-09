/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;

import java.io.IOException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class JavaFinderImplCustomSQLCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws DocumentException, IOException {

		JavaClass javaClass = (JavaClass)javaTerm;

		String className = javaClass.getName();

		if (!className.endsWith("FinderImpl")) {
			return javaClass.getContent();
		}

		Document customSQLDocument = getCustomSQLDocument(
			fileName, absolutePath, getPortalCustomSQLDocument(absolutePath));
		String finderName = className.substring(0, className.length() - 4);

		List<JavaTerm> childJavaTerms = javaClass.getChildJavaTerms();

		for (JavaTerm childJavaTerm : childJavaTerms) {
			if (childJavaTerm.isJavaMethod()) {
				_checkCustomSQL(
					fileName, childJavaTerm.getContent(), fileContent,
					customSQLDocument, finderName);
			}
			else if (childJavaTerm.isJavaVariable()) {
				_checkCustomSQLVariable(
					fileName, childJavaTerm.getName(),
					childJavaTerm.getContent(), fileContent, customSQLDocument);
			}
		}

		return javaClass.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private void _checkCustomSQL(
		String fileName, String methodContent, String fileContent,
		Document customSQLDocument, String finderName) {

		if ((customSQLDocument == null) || !customSQLDocument.hasContent()) {
			return;
		}

		Matcher matcher = _stringUtilReplacePattern.matcher(methodContent);

		outerLoop:
		while (matcher.find()) {
			List<String> parameterList = JavaSourceUtil.getParameterList(
				matcher.group());

			if (parameterList.size() != 3) {
				continue;
			}

			String replaceSQLValue = _getReplaceSQLValue(
				fileContent, parameterList.get(1));

			if (replaceSQLValue == null) {
				continue;
			}

			Element rootElement = customSQLDocument.getRootElement();

			for (Element sqlElement :
					(List<Element>)rootElement.elements("sql")) {

				String id = sqlElement.attributeValue("id");

				if (!id.contains(finderName)) {
					continue;
				}

				String sql = _transform(sqlElement.getText());

				if (sql.contains(replaceSQLValue)) {
					continue outerLoop;
				}
			}

			int pos = fileContent.indexOf(matcher.group());

			addMessage(
				fileName,
				StringBundler.concat(
					"SQL \"", replaceSQLValue,
					"\" does not exist in the custom-sql file"),
				getLineNumber(fileContent, pos));
		}
	}

	private void _checkCustomSQLVariable(
		String fileName, String variableName, String variableContent,
		String fileContent, Document customSQLDocument) {

		if (variableContent.contains("@Deprecated") ||
			fileContent.contains("\n@Deprecated")) {

			return;
		}

		Matcher matcher = _customQueryVariablePattern.matcher(variableContent);

		if (!matcher.find()) {
			return;
		}

		if ((customSQLDocument != null) && customSQLDocument.hasContent()) {
			Element rootElement = customSQLDocument.getRootElement();

			for (Element sqlElement :
					(List<Element>)rootElement.elements("sql")) {

				String id = sqlElement.attributeValue("id");

				if (id.endsWith(matcher.group(1) + matcher.group(2))) {
					return;
				}
			}
		}

		int pos = fileContent.indexOf(variableContent);

		addMessage(
			fileName,
			"\"" + variableName + "\" points to nonexistent custom query",
			getLineNumber(fileContent, pos));
	}

	private String _getReplaceSQLValue(String content, String parameterValue) {
		if (parameterValue.matches("[A-Z_]+")) {
			Pattern pattern = Pattern.compile(
				"final String\\s+" + parameterValue + "\\s+=\\s+(.*?);\n",
				Pattern.DOTALL);

			Matcher matcher = pattern.matcher(content);

			if (matcher.find()) {
				parameterValue = matcher.group(1);
			}
		}

		if (!parameterValue.matches("(?s)\".* .*\"")) {
			return null;
		}

		parameterValue = parameterValue.substring(
			1, parameterValue.length() - 1);

		return parameterValue.replaceAll("\"\\s+\\+\\s+\"", StringPool.BLANK);
	}

	private String _transform(String sql) {
		StringBundler sb = new StringBundler();

		String[] lines = StringUtil.splitLines(sql);

		for (String line : lines) {
			line = line.trim();

			if (line.startsWith(StringPool.CLOSE_PARENTHESIS)) {
				sb.setIndex(sb.index() - 1);
			}

			sb.append(line);

			if (line.endsWith(StringPool.OPEN_PARENTHESIS)) {
				continue;
			}

			sb.append(StringPool.SPACE);
		}

		return sb.toString();
	}

	private static final Pattern _customQueryVariablePattern = Pattern.compile(
		"=\\s+(\\w+)\\.class\\.getName\\(\\)\\s+\\+\\s+\"([\\.\\w]+)\";");
	private static final Pattern _stringUtilReplacePattern = Pattern.compile(
		"sql = StringUtil.replace\\(.*?\\);\n", Pattern.DOTALL);

}