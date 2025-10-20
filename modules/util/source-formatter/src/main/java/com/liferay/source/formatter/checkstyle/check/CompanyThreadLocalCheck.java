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
public class CompanyThreadLocalCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "CompanyThreadLocal", "setCompanyId");

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			log(methodCallDetailAST, _MSG_AVOID_SET_COMPANY_ID_CALL);
		}

		_checkMissingCloseCall(detailAST);
	}

	private void _checkMissingCloseCall(DetailAST detailAST) {
		DetailAST variableDefinitionDetailAST = null;
		String variableName = null;

		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "CompanyThreadLocal",
			new String[] {"lock", "setCompanyIdWithSafeCloseable"});

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST parentDetailAST = methodCallDetailAST.getParent();

			if (parentDetailAST.getType() == TokenTypes.ASSIGN) {
				variableName = getName(parentDetailAST);

				if (variableName == null) {
					continue;
				}

				variableDefinitionDetailAST = getVariableDefinitionDetailAST(
					detailAST, variableName, true);
			}
			else if (parentDetailAST.getType() == TokenTypes.EXPR) {
				parentDetailAST = parentDetailAST.getParent();

				if (parentDetailAST.getType() != TokenTypes.ASSIGN) {
					continue;
				}

				parentDetailAST = parentDetailAST.getParent();

				if (parentDetailAST.getType() != TokenTypes.VARIABLE_DEF) {
					continue;
				}

				variableDefinitionDetailAST = parentDetailAST;

				variableName = getName(variableDefinitionDetailAST);
			}

			if ((variableDefinitionDetailAST == null) ||
				(variableName == null)) {

				continue;
			}

			methodCallDetailASTs = getMethodCalls(
				detailAST, variableName, "close");

			if (!methodCallDetailASTs.isEmpty()) {
				continue;
			}

			log(
				variableDefinitionDetailAST, _MSG_MISSING_CLOSE_CALL,
				variableName, variableName);
		}
	}

	private static final String _MSG_AVOID_SET_COMPANY_ID_CALL =
		"set.company.id.call.avoid";

	private static final String _MSG_MISSING_CLOSE_CALL = "close.call.missing";

}