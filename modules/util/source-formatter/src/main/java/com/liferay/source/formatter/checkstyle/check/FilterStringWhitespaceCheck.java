/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class FilterStringWhitespaceCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		_checkMethod(detailAST, "ServiceTrackerFactory", "open");
		_checkMethod(detailAST, "WaiterUtil", "waitForFilter");
	}

	private void _checkFilterStringAssign(
		DetailAST assignDetailAST, String filterStringVariableName) {

		DetailAST nameDetailAST = null;

		DetailAST parentDetailAST = assignDetailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.VARIABLE_DEF) {
			nameDetailAST = parentDetailAST.findFirstToken(TokenTypes.IDENT);
		}
		else {
			nameDetailAST = assignDetailAST.findFirstToken(TokenTypes.IDENT);
		}

		String name = nameDetailAST.getText();

		if (!name.equals(filterStringVariableName)) {
			return;
		}

		List<DetailAST> literalStringDetailASTs = getAllChildTokens(
			assignDetailAST, true, TokenTypes.STRING_LITERAL);

		for (DetailAST literalStringDetailAST : literalStringDetailASTs) {
			String literalStringValue = literalStringDetailAST.getText();

			if (literalStringValue.contains(" = ")) {
				log(nameDetailAST, _MSG_INCORRECT_WHITESPACE, name);

				return;
			}
		}
	}

	private void _checkMethod(
		DetailAST detailAST, String className, String methodName) {

		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, className, methodName);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			String filterStringVariableName = _getFilterStringVariableName(
				methodCallDetailAST);

			if (filterStringVariableName == null) {
				continue;
			}

			List<DetailAST> assignDetailASTs = getAllChildTokens(
				detailAST, true, TokenTypes.ASSIGN);

			for (DetailAST assignDetailAST : assignDetailASTs) {
				_checkFilterStringAssign(
					assignDetailAST, filterStringVariableName);
			}
		}
	}

	private String _getFilterStringVariableName(DetailAST methodCallDetailAST) {
		DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
			TokenTypes.ELIST);

		List<DetailAST> exprDetailASTs = getAllChildTokens(
			elistDetailAST, false, TokenTypes.EXPR);

		if (exprDetailASTs.size() < 2) {
			return null;
		}

		DetailAST secondParameterDetailAST = exprDetailASTs.get(1);

		DetailAST firstChildDetailAST =
			secondParameterDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
			return null;
		}

		return firstChildDetailAST.getText();
	}

	private static final String _MSG_INCORRECT_WHITESPACE =
		"whitespace.incorrect";

}