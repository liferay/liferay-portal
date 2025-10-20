/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class ExceptionPrintStackTraceCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_CATCH};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.contains("/modules/sdk/") ||
			absolutePath.contains("/modules/util/") ||
			absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/") ||
			absolutePath.endsWith("Jdk14LogFactoryImpl.java")) {

			return;
		}

		DetailAST parameterDefinitionDetailAST = detailAST.findFirstToken(
			TokenTypes.PARAMETER_DEF);

		String exceptionVariableName = getName(parameterDefinitionDetailAST);

		String variableTypeName = getVariableTypeName(
			detailAST, exceptionVariableName, false);

		if ((variableTypeName == null) ||
			!variableTypeName.endsWith("Exception")) {

			return;
		}

		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, exceptionVariableName, "printStackTrace");

		if (methodCallDetailASTs.isEmpty()) {
			return;
		}

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			List<DetailAST> parameterExprDetailASTs =
				getParameterExprDetailASTs(methodCallDetailAST);

			if (parameterExprDetailASTs.isEmpty()) {
				log(methodCallDetailAST, _MSG_AVOID_METHOD_CALL);
			}
		}
	}

	private static final String _MSG_AVOID_METHOD_CALL = "method.call.avoid";

}