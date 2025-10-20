/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class ModelSetCallWithCompanyIdCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.VARIABLE_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/")) {

			return;
		}

		String typeName = getTypeName(detailAST, false, false, true);

		if (!typeName.startsWith("com.liferay.") ||
			!typeName.contains(".model.")) {

			return;
		}

		String variableName = getName(detailAST);

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

			if (methodName.endsWith("CompanyId") ||
				!methodName.startsWith("set") ||
				(methodName.equals("setClassPK") &&
				 typeName.equals("com.liferay.portal.kernel.model.Group")) ||
				(methodName.equals("setPrimKey") &&
				 typeName.equals(
					 "com.liferay.portal.kernel.model.ResourcePermission"))) {

				continue;
			}

			List<DetailAST> parameterExprDetailASTs =
				getParameterExprDetailASTs(parentDetailAST);

			for (DetailAST parameterExprDetailAST : parameterExprDetailASTs) {
				DetailAST firstChildDetailAST =
					parameterExprDetailAST.getFirstChild();

				if ((firstChildDetailAST.getType() == TokenTypes.IDENT) &&
					StringUtil.equals(
						firstChildDetailAST.getText(), "companyId")) {

					log(firstChildDetailAST, _MSG_INCORRECT_PARAMETER);
				}
				else if (firstChildDetailAST.getType() == TokenTypes.PLUS) {
					List<String> names = getNames(firstChildDetailAST, false);

					if (names.contains("companyId")) {
						log(firstChildDetailAST, _MSG_INCORRECT_PARAMETER);
					}
				}
			}
		}
	}

	private static final String _MSG_INCORRECT_PARAMETER =
		"parameter.incorrect";

}