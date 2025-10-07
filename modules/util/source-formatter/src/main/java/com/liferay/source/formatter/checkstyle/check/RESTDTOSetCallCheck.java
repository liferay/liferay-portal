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

import java.util.List;
import java.util.Map;

/**
 * @author Alan Huang
 */
public class RESTDTOSetCallCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF, TokenTypes.INSTANCE_INIT};
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

		if (detailAST.getType() == TokenTypes.CLASS_DEF) {
			_checkClassDeclaration(detailAST, absolutePath);
		}
		else if (detailAST.getType() == TokenTypes.INSTANCE_INIT) {
			_checkInstanceInitializer(detailAST, absolutePath);
		}
	}

	private void _checkClassDeclaration(
		DetailAST detailAST, String absolutePath) {

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		List<DetailAST> methodCallDetailASTList = getAllChildTokens(
			objBlockDetailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTList) {
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST == null) {
				continue;
			}

			String methodName = getMethodName(methodCallDetailAST);

			if (!methodName.startsWith("set")) {
				continue;
			}

			String variableName = getVariableName(methodCallDetailAST);

			if (variableName == null) {
				return;
			}

			String fullyQualifiedTypeName = getVariableTypeName(
				methodCallDetailAST, variableName, false, false, true);

			if ((fullyQualifiedTypeName == null) ||
				!fullyQualifiedTypeName.startsWith("com.liferay.") ||
				!fullyQualifiedTypeName.contains(".dto.v")) {

				continue;
			}

			_checkSetCall(
				absolutePath, methodCallDetailAST, methodName,
				fullyQualifiedTypeName);
		}
	}

	private void _checkInstanceInitializer(
		DetailAST detailAST, String absolutePath) {

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.OBJBLOCK) {
			return;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.LITERAL_NEW) {
			return;
		}

		DetailAST literalNewDetailAST = parentDetailAST;

		String fullyQualifiedTypeName = null;

		DetailAST firstChildDetailAST = parentDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			fullyQualifiedTypeName = getFullyQualifiedTypeName(
				firstChildDetailAST.getText(), detailAST, false);
		}
		else if (firstChildDetailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			fullyQualifiedTypeName = fullIdent.getText();
		}

		if ((fullyQualifiedTypeName == null) ||
			!fullyQualifiedTypeName.startsWith("com.liferay.") ||
			!fullyQualifiedTypeName.contains(".dto.v")) {

			return;
		}

		List<DetailAST> childDetailASTList = getAllChildTokens(
			detailAST, true, TokenTypes.ASSIGN, TokenTypes.METHOD_CALL);

		for (DetailAST childDetailAST : childDetailASTList) {
			parentDetailAST = getParentWithTokenType(
				childDetailAST, TokenTypes.INSTANCE_INIT);

			if (parentDetailAST == null) {
				continue;
			}

			parentDetailAST = parentDetailAST.getParent();

			if ((parentDetailAST == null) ||
				(parentDetailAST.getType() != TokenTypes.OBJBLOCK)) {

				continue;
			}

			parentDetailAST = parentDetailAST.getParent();

			if ((parentDetailAST == null) ||
				!equals(parentDetailAST, literalNewDetailAST)) {

				continue;
			}

			if (childDetailAST.getType() == TokenTypes.ASSIGN) {
				firstChildDetailAST = childDetailAST.getFirstChild();

				if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
					continue;
				}

				String variableName = firstChildDetailAST.getText();

				DetailAST variableDefinitionDetailAST =
					getVariableDefinitionDetailAST(
						childDetailAST, variableName, false);

				if (variableDefinitionDetailAST != null) {
					return;
				}

				String methodName =
					"set" + StringUtil.upperCaseFirstLetter(variableName);

				_checkSetCall(
					absolutePath, childDetailAST, methodName,
					fullyQualifiedTypeName);
			}
			else {
				DetailAST dotDetailAST = childDetailAST.findFirstToken(
					TokenTypes.DOT);

				if (dotDetailAST != null) {
					continue;
				}

				String methodName = getMethodName(childDetailAST);

				if (!methodName.startsWith("set")) {
					continue;
				}

				_checkSetCall(
					absolutePath, childDetailAST, methodName,
					fullyQualifiedTypeName);
			}
		}
	}

	private void _checkSetCall(
		String absolutePath, DetailAST detailAST, String methodName,
		String fullyQualifiedTypeName) {

		File javaFile = JavaSourceUtil.getJavaFile(
			fullyQualifiedTypeName, _getRootDirName(absolutePath),
			_getBundleSymbolicNamesMap(absolutePath));

		if (javaFile == null) {
			return;
		}

		JavaClass javaClass = null;

		try {
			javaClass = _getJavaClass(absolutePath, fullyQualifiedTypeName);
		}
		catch (IOException | ParseException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		parentDetailAST = parentDetailAST.getParent();

		if ((parentDetailAST.getType() != TokenTypes.SLIST) ||
			!_hasReplaceableMethodSignature(methodName, javaClass)) {

			return;
		}

		if (detailAST.getType() == TokenTypes.METHOD_CALL) {
			DetailAST elistDetailAST = detailAST.findFirstToken(
				TokenTypes.ELIST);

			DetailAST childDetailAST = elistDetailAST.getFirstChild();

			if ((childDetailAST == null) ||
				(childDetailAST.getType() == TokenTypes.LAMBDA) ||
				(childDetailAST.findFirstToken(TokenTypes.METHOD_REF) !=
					null)) {

				return;
			}
		}

		log(detailAST, _MSG_USE_SET_METHOD_INSTEAD, methodName);
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
			String absolutePath, String fullyQualifiedTypeName)
		throws IOException, ParseException {

		File javaFile = JavaSourceUtil.getJavaFile(
			fullyQualifiedTypeName, _getRootDirName(absolutePath),
			_getBundleSymbolicNamesMap(absolutePath));

		if (javaFile == null) {
			return null;
		}

		return JavaClassParser.parseJavaClass(
			SourceUtil.getAbsolutePath(javaFile), FileUtil.read(javaFile));
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

	private static final String _MSG_USE_SET_METHOD_INSTEAD =
		"set.method.use.instead";

	private static final Log _log = LogFactoryUtil.getLog(
		RESTDTOSetCallCheck.class);

	private volatile Map<String, String> _bundleSymbolicNamesMap;
	private volatile String _rootDirName;

}