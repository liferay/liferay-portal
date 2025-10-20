/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.FileUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class PersistenceCallCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF, TokenTypes.INTERFACE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		String absolutePath = getAbsolutePath();

		if (!absolutePath.contains("/modules/")) {
			return;
		}

		FileContents fileContents = getFileContents();

		FileText fileText = fileContents.getText();

		String content = (String)fileText.getFullText();

		if (!content.contains(".service.persistence.")) {
			return;
		}

		JavaClass javaClass = null;

		try {
			javaClass = JavaClassParser.parseJavaClass(absolutePath, content);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return;
		}

		Map<String, String> variablesMap = _getVariablesMap(javaClass);

		variablesMap.putAll(
			_getVariablesMap(_getExtendedJavaClass(absolutePath, content)));

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			_checkMethodCall(
				methodCallDetailAST, javaClass.getImportNames(), variablesMap,
				javaClass.getPackageName());
		}
	}

	private void _checkClass(
		String className, List<String> importNames, String packageName,
		int lineNo) {

		for (String importName : importNames) {
			if (!importName.endsWith("." + className)) {
				continue;
			}

			int pos = importName.indexOf(".service.persistence.");

			if (pos == -1) {
				return;
			}

			if (!packageName.startsWith(importName.substring(0, pos))) {
				log(lineNo, _MSG_ILLEGAL_PERSISTENCE_CALL, importName);
			}
		}
	}

	private void _checkMethodCall(
		DetailAST methodCallDetailAST, List<String> importNames,
		Map<String, String> variablesMap, String packageName) {

		DetailAST childDetailAST = methodCallDetailAST.getFirstChild();

		if (childDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		childDetailAST = childDetailAST.getFirstChild();

		if (childDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		DetailAST siblingDetailAST = childDetailAST.getNextSibling();

		if (siblingDetailAST.getType() == TokenTypes.IDENT) {
			String methodName = siblingDetailAST.getText();

			if (methodName.equals("clearCache") ||
				methodName.startsWith("create")) {

				return;
			}
		}

		String fieldName = childDetailAST.getText();

		if (fieldName.matches("[A-Z].*")) {
			_checkClass(
				fieldName, importNames, packageName,
				methodCallDetailAST.getLineNo());
		}
		else {
			_checkVariable(
				fieldName, variablesMap, packageName,
				methodCallDetailAST.getLineNo());
		}
	}

	private void _checkVariable(
		String variableName, Map<String, String> variablesMap,
		String packageName, int lineNo) {

		String fullyQualifiedTypeName = variablesMap.get(variableName);

		if (fullyQualifiedTypeName == null) {
			return;
		}

		int pos = fullyQualifiedTypeName.indexOf(".service.persistence.");

		if (pos == -1) {
			return;
		}

		if (!packageName.startsWith(fullyQualifiedTypeName.substring(0, pos))) {
			log(lineNo, _MSG_ILLEGAL_PERSISTENCE_CALL, fullyQualifiedTypeName);
		}
	}

	private JavaClass _getExtendedJavaClass(
		String absolutePath, String content) {

		Matcher matcher = _extendedClassPattern.matcher(content);

		if (!matcher.find()) {
			return null;
		}

		String extendedClassName = matcher.group(1);

		Pattern pattern = Pattern.compile(
			"\nimport (.*\\." + extendedClassName + ");");

		matcher = pattern.matcher(content);

		if (matcher.find()) {
			extendedClassName = matcher.group(1);

			if (!extendedClassName.startsWith("com.liferay.")) {
				return null;
			}
		}

		if (!extendedClassName.contains(StringPool.PERIOD)) {
			extendedClassName =
				JavaSourceUtil.getPackageName(content) + StringPool.PERIOD +
					extendedClassName;
		}

		int pos = absolutePath.lastIndexOf("/com/liferay/");

		String extendedClassFileName =
			absolutePath.substring(0, pos + 1) +
				StringUtil.replace(extendedClassName, '.', '/') + ".java";

		try {
			String extendedClassContent = FileUtil.read(
				new File(extendedClassFileName));

			if (extendedClassContent == null) {
				return null;
			}

			return JavaClassParser.parseJavaClass(
				extendedClassFileName, extendedClassContent);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private String _getFullyQualifiedName(
		String className, JavaClass javaClass) {

		for (String importName : javaClass.getImportNames()) {
			if (importName.endsWith(StringPool.PERIOD + className)) {
				return importName;
			}
		}

		return javaClass.getPackageName() + StringPool.PERIOD + className;
	}

	private Map<String, String> _getVariablesMap(JavaClass javaClass) {
		Map<String, String> variablesMap = new HashMap<>();

		if (javaClass == null) {
			return variablesMap;
		}

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (!javaTerm.isJavaVariable()) {
				continue;
			}

			Pattern pattern = Pattern.compile(
				"\\s(\\S+)\\s+(\\S+\\.)?" + javaTerm.getName());

			Matcher matcher = pattern.matcher(javaTerm.getContent());

			if (!matcher.find()) {
				continue;
			}

			String fieldTypeClassName = matcher.group(1);

			if (!fieldTypeClassName.contains(StringPool.PERIOD)) {
				fieldTypeClassName = _getFullyQualifiedName(
					fieldTypeClassName, javaClass);
			}

			variablesMap.put(javaTerm.getName(), fieldTypeClassName);
		}

		return variablesMap;
	}

	private static final String _MSG_ILLEGAL_PERSISTENCE_CALL =
		"persistence.call.illegal";

	private static final Log _log = LogFactoryUtil.getLog(
		PersistenceCallCheck.class);

	private static final Pattern _extendedClassPattern = Pattern.compile(
		"\\sextends\\s+(\\w+)\\W");

}