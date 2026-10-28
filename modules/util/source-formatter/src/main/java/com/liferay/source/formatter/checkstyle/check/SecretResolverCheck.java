/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

import java.util.List;
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
			_hasAcceptedEnclosingCall(detailAST) ||
			_isComparedWithNull(detailAST) ||
			_isResolvedThroughVariable(detailAST)) {

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

		JavaClass javaClass = getJavaClass(
			absolutePath,
			getFullyQualifiedTypeName(variableTypeName, detailAST, true));

		if (javaClass == null) {
			return;
		}

		String methodName = getMethodName(detailAST);

		if (_isPasswordAccessor(javaClass, methodName)) {
			log(detailAST, _MSG_REQUIRED_RESOLVE, methodName + "()");
		}
	}

	private String _getAssignedVariableName(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		while (parentDetailAST.getType() == TokenTypes.EXPR) {
			parentDetailAST = parentDetailAST.getParent();
		}

		if (parentDetailAST.getType() != TokenTypes.ASSIGN) {
			return null;
		}

		DetailAST grandParentDetailAST = parentDetailAST.getParent();

		if (grandParentDetailAST.getType() == TokenTypes.VARIABLE_DEF) {
			return getName(grandParentDetailAST);
		}

		return getName(parentDetailAST);
	}

	private boolean _hasAcceptedEnclosingCall(DetailAST detailAST) {
		DetailAST methodCallDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.METHOD_CALL);

		if (methodCallDetailAST == null) {
			return false;
		}

		String methodName = getMethodName(methodCallDetailAST);

		if (Objects.equals(methodName, "isBlank") ||
			Objects.equals(methodName, "isNotNull") ||
			Objects.equals(methodName, "isNull") ||
			_isResolveCall(methodCallDetailAST)) {

			return true;
		}

		return false;
	}

	private boolean _isComparedWithNull(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (!TokenUtil.isOfType(
				parentDetailAST, TokenTypes.EQUAL, TokenTypes.NOT_EQUAL)) {

			return false;
		}

		DetailAST firstChildDetailAST = parentDetailAST.getFirstChild();
		DetailAST lastChildDetailAST = parentDetailAST.getLastChild();

		if ((firstChildDetailAST.getType() == TokenTypes.LITERAL_NULL) ||
			(lastChildDetailAST.getType() == TokenTypes.LITERAL_NULL)) {

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

	private boolean _isResolveCall(DetailAST methodCallDetailAST) {
		if (!Objects.equals(getMethodName(methodCallDetailAST), "resolve")) {
			return false;
		}

		String variableName = getVariableName(methodCallDetailAST);

		if (variableName == null) {
			return false;
		}

		String variableTypeName = getVariableTypeName(
			methodCallDetailAST, variableName, false);

		if (Objects.equals(variableTypeName, "SecretResolver") ||
			Objects.equals(variableTypeName, "SecretResolverUtil")) {

			return true;
		}

		return false;
	}

	private boolean _isResolvedThroughVariable(DetailAST detailAST) {
		String assignedVariableName = _getAssignedVariableName(detailAST);

		if (assignedVariableName == null) {
			return false;
		}

		DetailAST methodDefDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF);

		if (methodDefDetailAST == null) {
			return false;
		}

		for (DetailAST identDetailAST :
				getAllChildTokens(methodDefDetailAST, true, TokenTypes.IDENT)) {

			if (!Objects.equals(
					identDetailAST.getText(), assignedVariableName)) {

				continue;
			}

			DetailAST parentDetailAST = identDetailAST.getParent();

			if ((parentDetailAST.getType() == TokenTypes.DOT) &&
				(parentDetailAST.getLastChild() == identDetailAST)) {

				continue;
			}

			DetailAST methodCallDetailAST = getParentWithTokenType(
				identDetailAST, TokenTypes.METHOD_CALL);

			if ((methodCallDetailAST != null) &&
				_isResolveCall(methodCallDetailAST)) {

				return true;
			}
		}

		return false;
	}

	private static final String _MSG_REQUIRED_RESOLVE = "resolve.required";

	private static final String _SECRET_RESOLVER_EXCLUDES =
		"secret.resolver.excludes";

}