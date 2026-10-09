/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaConstructor;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaStaticBlock;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;
import com.liferay.source.formatter.processor.SourceProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public abstract class BaseJavaTermCheck
	extends BaseSourceCheck implements JavaTermCheck {

	@Override
	public String process(
			SourceProcessor sourceProcessor, String fileName,
			String absolutePath, JavaClass javaClass, String content)
		throws Exception {

		clearSourceFormatterMessages(fileName);

		setSourceProcessor(sourceProcessor);

		return _walkJavaClass(
			fileName, absolutePath, javaClass, content, content);
	}

	protected abstract String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception;

	protected abstract String[] getCheckableJavaTermNames();

	protected List<String> getImportNames(JavaTerm javaTerm) {
		JavaClass javaClass = javaTerm.getParentJavaClass();

		while (true) {
			JavaClass parentJavaClass = javaClass.getParentJavaClass();

			if (parentJavaClass == null) {
				return javaClass.getImportNames();
			}

			javaClass = parentJavaClass;
		}
	}

	protected String[] getTernaryOperatorParts(String operator) {
		int x = -1;

		while (true) {
			x = operator.indexOf(StringPool.QUESTION, x + 1);

			if (x == -1) {
				return null;
			}

			if (!ToolsUtil.isInsideQuotes(operator, x) &&
				(getLevel(operator.substring(0, x), "<", ">") == 0)) {

				break;
			}
		}

		int y = x;

		while (true) {
			y = operator.indexOf(StringPool.COLON, y + 1);

			if (y == -1) {
				return null;
			}

			if (!ToolsUtil.isInsideQuotes(operator, y)) {
				break;
			}
		}

		String falseValue = StringUtil.trim(operator.substring(y + 1));
		String ifCondition = StringUtil.trim(operator.substring(0, x));
		String trueValue = StringUtil.trim(operator.substring(x + 1, y));

		if ((getLevel(falseValue) == 0) && (getLevel(ifCondition) == 0) &&
			(getLevel(trueValue) == 0)) {

			return new String[] {ifCondition, trueValue, falseValue};
		}

		return null;
	}

	protected List<String> getVariableNames(String content) {
		List<String> variableNames = new ArrayList<>();

		int x = content.indexOf("{\n");

		Matcher matcher = _variableDeclarationPattern.matcher(content);

		while (matcher.find()) {
			if (matcher.start() < x) {
				continue;
			}

			String s = StringUtil.trim(matcher.group(1));

			if (s.equals("break") || s.equals("continue") ||
				s.equals("return") || s.equals("throw")) {

				continue;
			}

			variableNames.add(matcher.group(3));
		}

		return variableNames;
	}

	protected static final String JAVA_CLASS = JavaClass.class.getName();

	protected static final String JAVA_CONSTRUCTOR =
		JavaConstructor.class.getName();

	protected static final String JAVA_METHOD = JavaMethod.class.getName();

	protected static final String JAVA_STATIC_BLOCK =
		JavaStaticBlock.class.getName();

	protected static final String JAVA_VARIABLE = JavaVariable.class.getName();

	protected static final String JAVATERM_SORT_EXCLUDES =
		"javaterm.sort.excludes";

	private boolean _isCheckableJavaTerm(JavaTerm javaTerm) {
		Class<?> clazz = javaTerm.getClass();

		String className = clazz.getName();

		for (String name : getCheckableJavaTermNames()) {
			if (name.equals(className)) {
				return true;
			}
		}

		return false;
	}

	private String _walkJavaClass(
			String fileName, String absolutePath, JavaClass javaClass,
			String parentContent, String fileContent)
		throws Exception {

		String javaClassContent = javaClass.getContent();

		String newJavaClassContent = javaClassContent;

		if (_isCheckableJavaTerm(javaClass)) {
			newJavaClassContent = doProcess(
				fileName, absolutePath, javaClass, fileContent);

			if (!javaClassContent.equals(newJavaClassContent)) {
				return StringUtil.replace(
					parentContent, javaClassContent, newJavaClassContent);
			}
		}

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (javaTerm.isJavaClass()) {
				JavaClass childJavaClass = (JavaClass)javaTerm;

				newJavaClassContent = _walkJavaClass(
					fileName, absolutePath, childJavaClass, javaClassContent,
					fileContent);

				if (!newJavaClassContent.equals(javaClassContent)) {
					return StringUtil.replace(
						parentContent, javaClassContent, newJavaClassContent);
				}
			}
			else if (_isCheckableJavaTerm(javaTerm)) {
				String javaTermContent = javaTerm.getContent();

				String newJavaTermContent = doProcess(
					fileName, absolutePath, javaTerm, fileContent);

				if (!javaTermContent.equals(newJavaTermContent)) {
					newJavaClassContent = StringUtil.replace(
						javaClassContent, javaTermContent, newJavaTermContent);

					return StringUtil.replace(
						parentContent, javaClassContent, newJavaClassContent);
				}
			}
		}

		return parentContent;
	}

	private static final Pattern _variableDeclarationPattern = Pattern.compile(
		"((\t\\w|\\()[\\w<>,\\s]+?)\\s(\\w+)( =\\s|;)");

}