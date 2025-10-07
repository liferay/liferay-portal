/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaAnonymousInnerClassCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		JavaClass javaClass = (JavaClass)javaTerm;

		if (!javaClass.isAnonymous()) {
			return javaTerm.getContent();
		}

		JavaMethod parentJavaMethod = javaTerm.getParentJavaMethod();

		if (parentJavaMethod == null) {
			return javaTerm.getContent();
		}

		String anonymousClassContent = javaTerm.getContent();

		anonymousClassContent = _convertToLambda(
			fileName, anonymousClassContent, javaClass, parentJavaMethod,
			"ActionableDynamicQuery.AddCriteriaMethod", false);

		return _convertToLambda(
			fileName, anonymousClassContent, javaClass, parentJavaMethod,
			"ActionableDynamicQuery.PerformActionMethod", true);
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private String _convertToLambda(
		String fileName, String anonymousClassContent, JavaClass anonymousClass,
		JavaTerm javaTerm, String className, boolean useParameterType) {

		if (!StringUtil.startsWith(
				StringUtil.removeChars(anonymousClassContent, '\n', '\t'),
				"new " + className)) {

			return anonymousClassContent;
		}

		List<JavaTerm> javaTerms = anonymousClass.getChildJavaTerms();

		if (javaTerms.size() != 1) {
			return anonymousClassContent;
		}

		JavaTerm anonymousClassJavaTerm = javaTerms.get(0);

		if (!anonymousClassJavaTerm.hasAnnotation("Override") ||
			!(anonymousClassJavaTerm instanceof JavaMethod)) {

			return anonymousClassContent;
		}

		JavaMethod anonymousClassJavaMethod =
			(JavaMethod)anonymousClassJavaTerm;

		if (_hasDuplicateParameterOrVariableName(
				fileName, anonymousClassContent, anonymousClassJavaMethod,
				javaTerm)) {

			return anonymousClassContent;
		}

		int x = anonymousClassContent.indexOf("{\n");

		String lastLine = anonymousClassContent.substring(
			anonymousClassContent.lastIndexOf("\n") + 1);

		String expectedContent = StringBundler.concat(
			anonymousClassContent.substring(0, x + 2), "\n",
			anonymousClassJavaMethod.getContent(), "\n", lastLine);

		if (!expectedContent.equals(anonymousClassContent)) {
			return anonymousClassContent;
		}

		String methodBody = _getMethodBody(
			anonymousClassJavaMethod.getContent());

		if (methodBody == null) {
			return anonymousClassContent;
		}

		StringBundler sb = new StringBundler(5);

		sb.append(
			_getLambdaSignature(
				anonymousClassJavaMethod.getSignature(), useParameterType));
		sb.append(" -> {\n");
		sb.append(methodBody);
		sb.append("\n");
		sb.append(lastLine);

		return sb.toString();
	}

	private String _getLambdaSignature(
		JavaSignature signature, boolean useParameterType) {

		StringBundler sb = new StringBundler();

		List<JavaParameter> parameters = signature.getParameters();

		if (parameters.isEmpty() || (parameters.size() > 1) ||
			useParameterType) {

			sb.append(CharPool.OPEN_PARENTHESIS);
		}

		for (JavaParameter parameter : parameters) {
			if (useParameterType) {
				sb.append(parameter.getParameterType());
				sb.append(CharPool.SPACE);
			}

			sb.append(parameter.getParameterName());
			sb.append(CharPool.COMMA);
		}

		if (!parameters.isEmpty()) {
			sb.setIndex(sb.index() - 1);
		}

		if (parameters.isEmpty() || (parameters.size() > 1) ||
			useParameterType) {

			sb.append(CharPool.CLOSE_PARENTHESIS);
		}

		return sb.toString();
	}

	private String _getMethodBody(String content) {
		int x = content.indexOf("{\n");

		int y = content.lastIndexOf("}");

		y = content.lastIndexOf("\n", y);

		String body = content.substring(x + 1, y);

		if (getLevel(body, "{", "}") != 0) {
			return null;
		}

		body = StringUtil.replace(body, "\n\t", "\n");

		return body.substring(1);
	}

	private List<String> _getParameterNames(JavaMethod javaMethod) {
		List<String> parameterNames = new ArrayList<>();

		JavaSignature signature = javaMethod.getSignature();

		if (signature == null) {
			return parameterNames;
		}

		List<JavaParameter> parameters = signature.getParameters();

		for (JavaParameter parameter : parameters) {
			parameterNames.add(parameter.getParameterName());
		}

		return parameterNames;
	}

	private boolean _hasDuplicateParameterOrVariableName(
		String fileName, String anonymousClassContent,
		JavaMethod anonymousClassJavaMethod, JavaTerm javaTerm) {

		List<String> parameterNames = _getParameterNames(
			anonymousClassJavaMethod);
		List<String> variableNames = getVariableNames(anonymousClassContent);

		if (parameterNames.isEmpty() && variableNames.isEmpty()) {
			return false;
		}

		String javaTermContent = javaTerm.getContent();

		int x = javaTermContent.indexOf(anonymousClassContent);

		for (String variableName : variableNames) {
			if (!_isDuplicateName(
					anonymousClassJavaMethod, javaTerm, variableName, x)) {

				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Variable \"", variableName,
					"\" in the Anonymous Inner Class is already used in the ",
					"main method"),
				javaTerm.getLineNumber());

			return true;
		}

		for (String parameterName : parameterNames) {
			if (!_isDuplicateName(
					anonymousClassJavaMethod, javaTerm, parameterName, x)) {

				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Parameter \"", parameterName,
					"\" in the Anonymous Inner Class is already used in the ",
					"main method"),
				javaTerm.getLineNumber());

			return true;
		}

		return false;
	}

	private boolean _isDuplicateName(
		JavaMethod anonymousClassJavaMethod, JavaTerm javaTerm,
		String variableName, int end) {

		JavaClass javaClass = anonymousClassJavaMethod.getParentJavaClass();

		while (true) {
			JavaClass parentJavaClass = javaClass.getParentJavaClass();

			if (parentJavaClass == null) {
				break;
			}

			javaClass = parentJavaClass;
		}

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (childJavaTerm.getLineNumber() == javaTerm.getLineNumber()) {
				javaTerm = childJavaTerm;

				break;
			}
		}

		JavaSignature signature = javaTerm.getSignature();

		List<JavaParameter> parameters = signature.getParameters();

		for (JavaParameter parameter : parameters) {
			if (variableName.equals(parameter.getParameterName())) {
				return true;
			}
		}

		Pattern pattern = Pattern.compile(
			"((\t\\w|\\()[\\w<>,\\s]+?)\\s" + variableName + "( =\\s|;)");

		String javaTermContent = javaTerm.getContent();

		Matcher matcher = pattern.matcher(javaTermContent);

		while (matcher.find()) {
			int x = matcher.start();

			if (x > end) {
				return false;
			}

			int y = x;

			while (true) {
				y = javaTermContent.indexOf("}", y + 1);

				if (y == -1) {
					return false;
				}

				if (ToolsUtil.isInsideQuotes(javaTermContent, y)) {
					continue;
				}

				if (getLevel(javaTermContent.substring(x, y + 1), "{", "}") <
						0) {

					break;
				}
			}

			if (y > end) {
				return true;
			}
		}

		return false;
	}

}