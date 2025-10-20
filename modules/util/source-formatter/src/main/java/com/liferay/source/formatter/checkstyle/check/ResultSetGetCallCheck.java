/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class ResultSetGetCallCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.VARIABLE_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String variableName = getName(detailAST);

		String variableTypeName = getVariableTypeName(
			detailAST, variableName, false);

		if (!variableTypeName.equals("ResultSet")) {
			return;
		}

		List<DetailAST> variableCallerDetailASTs = getVariableCallerDetailASTs(
			detailAST);

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

			if (!methodName.matches("get[A-Z].+")) {
				continue;
			}

			List<DetailAST> parameterExprDetailASTs =
				getParameterExprDetailASTs(parentDetailAST);

			if (parameterExprDetailASTs.size() != 1) {
				continue;
			}

			DetailAST parameterExprDetailAST = parameterExprDetailASTs.get(0);

			DetailAST firstChildDetailAST =
				parameterExprDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
				continue;
			}

			String text = firstChildDetailAST.getText();

			if (!text.contains(".")) {
				continue;
			}

			log(
				firstChildDetailAST, _MSG_INCORRECT_SET_CALL_PARAMETER,
				variableName + ".get*");
		}
	}

	private static final String _MSG_INCORRECT_SET_CALL_PARAMETER =
		"set.call.parameter.incorrect";

}