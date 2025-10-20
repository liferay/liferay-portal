/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class StringIndexOfCallCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "indexOf");

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			_checkMethodCall(methodCallDetailAST);
		}
	}

	private void _checkConditionalExpression(DetailAST methodCallDetailAST) {
		DetailAST parentDetailAST = methodCallDetailAST.getParent();

		DetailAST grandParentDetailAST = parentDetailAST.getParent();

		if (grandParentDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		DetailAST nextSiblingDetailAST = methodCallDetailAST.getNextSibling();

		if (nextSiblingDetailAST == null) {
			return;
		}

		if (nextSiblingDetailAST.getType() == TokenTypes.NUM_INT) {
			if ((parentDetailAST.getType() != TokenTypes.GE) &&
				(parentDetailAST.getType() != TokenTypes.LT)) {

				return;
			}

			if (StringUtil.equals(nextSiblingDetailAST.getText(), "0")) {
				log(methodCallDetailAST, _MSG_USE_METHOD);
			}

			return;
		}

		if (nextSiblingDetailAST.getType() == TokenTypes.UNARY_MINUS) {
			if ((parentDetailAST.getType() != TokenTypes.EQUAL) &&
				(parentDetailAST.getType() != TokenTypes.NOT_EQUAL)) {

				return;
			}

			DetailAST firstChildDetailAST =
				nextSiblingDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.NUM_INT) {
				return;
			}

			if (StringUtil.equals(firstChildDetailAST.getText(), "1")) {
				log(methodCallDetailAST, _MSG_USE_METHOD);
			}
		}
	}

	private void _checkMethodCall(DetailAST methodCallDetailAST) {
		String variableName = getName(methodCallDetailAST.getFirstChild());

		String variableTypeName = getVariableTypeName(
			methodCallDetailAST, variableName, false);

		if (!variableTypeName.equals("String")) {
			return;
		}

		List<DetailAST> parameterExprDetailASTs = getParameterExprDetailASTs(
			methodCallDetailAST);

		if (parameterExprDetailASTs.size() != 1) {
			return;
		}

		DetailAST exprDetailAST = parameterExprDetailASTs.get(0);

		DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			String text = fullIdent.getText();

			if (!text.startsWith("StringPool.")) {
				return;
			}

			_checkConditionalExpression(methodCallDetailAST);
		}
		else if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			variableTypeName = getVariableTypeName(
				firstChildDetailAST, firstChildDetailAST.getText(), false);

			if ((variableTypeName == null) ||
				!variableTypeName.equals("String")) {

				return;
			}

			_checkConditionalExpression(methodCallDetailAST);
		}
		else if (firstChildDetailAST.getType() == TokenTypes.STRING_LITERAL) {
			_checkConditionalExpression(methodCallDetailAST);
		}
	}

	private static final String _MSG_USE_METHOD = "method.use";

}