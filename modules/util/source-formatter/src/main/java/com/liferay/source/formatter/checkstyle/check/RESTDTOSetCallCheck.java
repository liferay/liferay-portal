/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.ParseException;
import com.liferay.source.formatter.util.FileUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alan Huang
 */
public class RESTDTOSetCallCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.INSTANCE_INIT, TokenTypes.PARAMETER_DEF,
			TokenTypes.VARIABLE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if ((!absolutePath.contains("/modules/") &&
			 !absolutePath.contains("/workspaces/")) ||
			absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/")) {

			return;
		}

		if (detailAST.getType() == TokenTypes.INSTANCE_INIT) {
			_checkSetCallInInstanceInitializer(absolutePath, detailAST);
		}
		else {
			_checkSetCallByVariableDefinition(absolutePath, detailAST);
		}
	}

	private void _checkSetCall(
		String absolutePath, DetailAST detailAST, String methodName,
		String fullyQualifiedTypeName) {

		if (detailAST.getType() == TokenTypes.METHOD_CALL) {
			DetailAST elistDetailAST = detailAST.findFirstToken(
				TokenTypes.ELIST);

			DetailAST childDetailAST = elistDetailAST.getFirstChild();

			if ((childDetailAST == null) ||
				(childDetailAST.findFirstToken(TokenTypes.METHOD_REF) !=
					null) ||
				(childDetailAST.getType() == TokenTypes.LAMBDA) ||
				_hasUnsafeSupplierReturnType(absolutePath, childDetailAST)) {

				return;
			}
		}

		JavaClass javaClass = _getJavaClass(
			absolutePath, fullyQualifiedTypeName);

		if ((javaClass == null) ||
			!_hasReplaceableMethodSignature(methodName, javaClass)) {

			return;
		}

		log(detailAST, _MSG_USE_SET_METHOD_INSTEAD, methodName);
	}

	private void _checkSetCallByVariableDefinition(
		String absolutePath, DetailAST detailAST) {

		String variableName = getName(detailAST);

		String fullyQualifiedTypeName = getVariableTypeName(
			detailAST, variableName, false, false, true);

		if ((fullyQualifiedTypeName == null) ||
			!fullyQualifiedTypeName.matches(
				"com\\.liferay\\..+\\.dto\\.v\\d+_\\d+\\.\\w+")) {

			return;
		}

		List<DetailAST> variableCallerDetailASTs = getVariableCallerDetailASTs(
			detailAST, variableName);

		for (DetailAST variableCallerDetailAST : variableCallerDetailASTs) {
			DetailAST parentDetailAST = variableCallerDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.DOT) {
				continue;
			}

			parentDetailAST = parentDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.METHOD_CALL) {
				continue;
			}

			String methodName = getMethodName(parentDetailAST);

			if (!methodName.startsWith("set")) {
				continue;
			}

			_checkSetCall(
				absolutePath, parentDetailAST, methodName,
				fullyQualifiedTypeName);
		}
	}

	private void _checkSetCallInInstanceInitializer(
		String absolutePath, DetailAST detailAST) {

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.OBJBLOCK) {
			return;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.LITERAL_NEW) {
			return;
		}

		String fullyQualifiedTypeName = null;

		DetailAST firstChildDetailAST = parentDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			fullyQualifiedTypeName = fullIdent.getText();
		}
		else if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			fullyQualifiedTypeName = getFullyQualifiedTypeName(
				firstChildDetailAST.getText(), detailAST, false);
		}

		if ((fullyQualifiedTypeName == null) ||
			!fullyQualifiedTypeName.matches(
				"com\\.liferay\\..+\\.dto\\.v\\d+_\\d+\\.\\w+")) {

			return;
		}

		DetailAST slistDetailAST = detailAST.findFirstToken(TokenTypes.SLIST);

		List<DetailAST> childDetailASTs = getAllChildTokens(
			slistDetailAST, false, ALL_TYPES);

		for (DetailAST childDetailAST : childDetailASTs) {
			if (childDetailAST.getType() == TokenTypes.RCURLY) {
				break;
			}

			if (childDetailAST.getType() == TokenTypes.SEMI) {
				continue;
			}

			if (childDetailAST.getType() != TokenTypes.EXPR) {
				log(childDetailAST, _MSG_MOVE_STATEMENT);

				continue;
			}

			firstChildDetailAST = childDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) {
				log(childDetailAST, _MSG_MOVE_STATEMENT);

				continue;
			}

			String methodName = getMethodName(firstChildDetailAST);

			if (!methodName.startsWith("set")) {
				log(childDetailAST, _MSG_MOVE_STATEMENT);

				continue;
			}

			_checkSetCall(
				absolutePath, firstChildDetailAST, methodName,
				fullyQualifiedTypeName);
		}
	}

	private synchronized Map<String, String> _getBundleSymbolicNamesMap(
		String absolutePath) {

		if (_bundleSymbolicNamesMap == null) {
			_bundleSymbolicNamesMap = BNDSourceUtil.getBundleSymbolicNamesMap(
				_getRootDirName(absolutePath));
		}

		return _bundleSymbolicNamesMap;
	}

	private JavaClass _getJavaClass(
		String absolutePath, String fullyQualifiedTypeName) {

		if (_javaClasses.containsKey(fullyQualifiedTypeName)) {
			return _javaClasses.get(fullyQualifiedTypeName);
		}

		File javaFile = JavaSourceUtil.getJavaFile(
			fullyQualifiedTypeName, _getRootDirName(absolutePath),
			_getBundleSymbolicNamesMap(absolutePath));

		if (javaFile == null) {
			return null;
		}

		JavaClass javaClass = null;

		try {
			javaClass = JavaClassParser.parseJavaClass(
				SourceUtil.getAbsolutePath(javaFile), FileUtil.read(javaFile));

			_javaClasses.put(fullyQualifiedTypeName, javaClass);

			return javaClass;
		}
		catch (IOException | ParseException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private synchronized String _getRootDirName(String absolutePath) {
		if (_rootDirName != null) {
			return _rootDirName;
		}

		_rootDirName = SourceUtil.getRootDirName(absolutePath);

		return _rootDirName;
	}

	private boolean _hasReplaceableMethodSignature(
		String methodName, JavaClass javaClass) {

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (!javaTerm.isJavaMethod() || javaTerm.isPrivate()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)javaTerm;

			if (!StringUtil.equals(methodName, javaMethod.getName())) {
				continue;
			}

			JavaSignature javaSignature = javaMethod.getSignature();

			List<JavaParameter> javaParameters = javaSignature.getParameters();

			if (javaParameters.size() != 1) {
				continue;
			}

			JavaParameter javaParameter = javaParameters.get(0);

			String parameterType = javaParameter.getParameterType();

			if (parameterType.startsWith("UnsafeSupplier")) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasUnsafeSupplierReturnType(
		String absolutePath, DetailAST detailAST) {

		if (detailAST.getType() != TokenTypes.EXPR) {
			return false;
		}

		DetailAST methodCallDetailAST = detailAST.getFirstChild();

		if ((methodCallDetailAST == null) ||
			(methodCallDetailAST.getType() != TokenTypes.METHOD_CALL)) {

			return false;
		}

		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return false;
		}

		List<String> names = getNames(firstChildDetailAST, false);

		if (names.size() != 2) {
			return false;
		}

		String fullyQualifiedTypeName = null;

		String methodCallClassName = names.get(0);

		if (Character.isUpperCase(methodCallClassName.charAt(0))) {
			fullyQualifiedTypeName = getFullyQualifiedTypeName(
				methodCallClassName, detailAST, false);
		}
		else {
			fullyQualifiedTypeName = getVariableTypeName(
				detailAST, methodCallClassName, false, false, true);
		}

		if (fullyQualifiedTypeName == null) {
			return false;
		}

		JavaClass javaClass = _getJavaClass(
			absolutePath, fullyQualifiedTypeName);

		if (javaClass == null) {
			return false;
		}

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (!javaTerm.isJavaMethod()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)javaTerm;

			if (!StringUtil.equals(names.get(1), javaMethod.getName())) {
				continue;
			}

			JavaSignature javaSignature = javaMethod.getSignature();

			String returnType = javaSignature.getReturnType();

			if ((returnType != null) &&
				returnType.startsWith("UnsafeSupplier")) {

				return true;
			}
		}

		return false;
	}

	private static final String _MSG_MOVE_STATEMENT = "statement.move";

	private static final String _MSG_USE_SET_METHOD_INSTEAD =
		"set.method.use.instead";

	private static final Log _log = LogFactoryUtil.getLog(
		RESTDTOSetCallCheck.class);

	private volatile Map<String, String> _bundleSymbolicNamesMap;
	private final Map<String, JavaClass> _javaClasses = new HashMap<>();
	private volatile String _rootDirName;

}