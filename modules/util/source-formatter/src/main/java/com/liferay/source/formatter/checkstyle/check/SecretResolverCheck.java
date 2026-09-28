/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.ParseException;
import com.liferay.source.formatter.util.FileUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jorge García Jiménez
 */
public class SecretResolverCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.contains("/modules/apps/archived/") ||
			absolutePath.contains("/modules/third-party/") ||
			absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/") ||
			isExcludedPath(_SECRET_RESOLVER_EXCLUDES)) {

			return;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		List<DetailAST> parameterExprDetailASTs = getParameterExprDetailASTs(
			detailAST);

		if (!parameterExprDetailASTs.isEmpty() ||
			_hasAcceptedEnclosingCall(detailAST)) {

			return;
		}

		String variableName = getVariableName(detailAST);

		if (variableName == null) {
			return;
		}

		DetailAST variableDefinitionDetailAST = getVariableDefinitionDetailAST(
			detailAST, variableName);

		if (variableDefinitionDetailAST == null) {
			return;
		}

		String variableTypeName = getVariableTypeName(
			variableDefinitionDetailAST, variableName, false);

		if (Validator.isNull(variableTypeName)) {
			return;
		}

		JavaClass javaClass = _getJavaClass(
			absolutePath,
			getFullyQualifiedTypeName(variableTypeName, detailAST, true));

		if (javaClass == null) {
			return;
		}

		String methodName = getMethodName(detailAST);

		if (_isPasswordAccessor(javaClass, methodName)) {
			log(detailAST, _MSG_RESOLVE_REQUIRED, methodName + "()");
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

		if (fullyQualifiedTypeName == null) {
			return null;
		}

		JavaClass javaClass = _javaClasses.get(fullyQualifiedTypeName);

		if (javaClass != null) {
			return javaClass;
		}

		File javaFile = JavaSourceUtil.getJavaFile(
			fullyQualifiedTypeName, _getRootDirName(absolutePath),
			_getBundleSymbolicNamesMap(absolutePath));

		if (javaFile == null) {
			return null;
		}

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
		if (_rootDirName == null) {
			_rootDirName = SourceUtil.getRootDirName(absolutePath);
		}

		return _rootDirName;
	}

	private boolean _hasAcceptedEnclosingCall(DetailAST detailAST) {
		DetailAST methodCallDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.METHOD_CALL);

		if (methodCallDetailAST == null) {
			return false;
		}

		String methodName = getMethodName(methodCallDetailAST);

		if (Objects.equals(methodName, "resolve") ||
			Objects.equals(methodName, "isNotNull") ||
			Objects.equals(methodName, "isNull")) {

			return true;
		}

		return false;
	}

	private boolean _isPasswordAccessor(
		JavaClass javaClass, String methodName) {

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (!javaTerm.isJavaMethod() ||
				!Objects.equals(methodName, javaTerm.getName())) {

				continue;
			}

			String content = javaTerm.getContent();

			return content.contains("Meta.Type.Password");
		}

		return false;
	}

	private static final String _MSG_RESOLVE_REQUIRED = "resolve.required";

	private static final String _SECRET_RESOLVER_EXCLUDES =
		"secret.resolver.excludes";

	private static final Log _log = LogFactoryUtil.getLog(
		SecretResolverCheck.class);

	private volatile Map<String, String> _bundleSymbolicNamesMap;
	private final Map<String, JavaClass> _javaClasses = new HashMap<>();
	private volatile String _rootDirName;

}