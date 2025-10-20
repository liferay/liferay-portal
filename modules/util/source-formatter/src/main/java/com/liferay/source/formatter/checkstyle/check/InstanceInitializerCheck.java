/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.Validator;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class InstanceInitializerCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.INSTANCE_INIT};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.OBJBLOCK) {
			return;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.LITERAL_NEW) {
			return;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.SLIST) {
			return;
		}

		List<DetailAST> exprDetailASTs = getAllChildTokens(
			firstChildDetailAST, false, TokenTypes.EXPR);

		if (exprDetailASTs.size() >= 2) {
			_checkAttributeOrder(exprDetailASTs);
		}
	}

	private void _checkAttributeOrder(List<DetailAST> exprDetailASTs) {
		String previousVariableName = null;
		String previousMethodName = null;

		for (DetailAST exprDetailAST : exprDetailASTs) {
			DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.ASSIGN) {
				String variableName = getName(firstChildDetailAST);

				if (Validator.isNotNull(
						getTypeName(
							getVariableTypeDetailAST(
								firstChildDetailAST, variableName, false),
							false))) {

					continue;
				}

				if ((previousVariableName != null) &&
					(previousVariableName.compareToIgnoreCase(variableName) >
						0)) {

					log(
						exprDetailAST, _MSG_INCORRECT_ASSIGN_ORDER,
						variableName, previousVariableName,
						firstChildDetailAST.getLineNo());
				}
				else if (Validator.isNotNull(previousMethodName)) {
					log(
						exprDetailAST, _MSG_MOVE_ASSIGN_BEFORE_METHOD_CALL,
						variableName, previousMethodName,
						firstChildDetailAST.getLineNo());
				}

				previousVariableName = variableName;
			}
			else if (firstChildDetailAST.getType() == TokenTypes.METHOD_CALL) {
				String methodName = getName(firstChildDetailAST);

				if (Validator.isNull(methodName) ||
					!methodName.matches("set[A-Z].+")) {

					continue;
				}

				if ((previousMethodName != null) &&
					(previousMethodName.compareToIgnoreCase(methodName) > 0)) {

					log(
						exprDetailAST, _MSG_INCORRECT_METHOD_CALL_ORDER,
						methodName, previousMethodName,
						firstChildDetailAST.getLineNo());
				}

				previousMethodName = methodName;
			}
		}
	}

	private static final String _MSG_INCORRECT_ASSIGN_ORDER =
		"assign.order.incorrect";

	private static final String _MSG_INCORRECT_METHOD_CALL_ORDER =
		"method.call.order.incorrect";

	private static final String _MSG_MOVE_ASSIGN_BEFORE_METHOD_CALL =
		"assign.move.before.method.call";

}