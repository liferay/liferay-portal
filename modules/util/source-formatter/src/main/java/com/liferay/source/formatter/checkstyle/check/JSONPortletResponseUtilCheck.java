/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class JSONPortletResponseUtilCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (detailAST.getParent() != null) {
			return;
		}

		String className = getName(detailAST);

		if (!className.endsWith("MVCActionCommand")) {
			return;
		}

		_checkWriteJSONMethodCall(detailAST);
	}

	private void _checkWriteJSONMethodCall(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "JSONPortletResponseUtil", "writeJSON");

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.ELIST);

			List<DetailAST> exprDetailASTs = getAllChildTokens(
				elistDetailAST, false, TokenTypes.EXPR);

			if (exprDetailASTs.size() != 3) {
				continue;
			}

			DetailAST exprDetailAST = exprDetailASTs.get(2);

			DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
				continue;
			}

			DetailAST parentDetailAST = methodCallDetailAST.getParent();

			if ((parentDetailAST == null) ||
				(parentDetailAST.getType() != TokenTypes.EXPR)) {

				continue;
			}

			DetailAST previousSiblingDetailAST =
				parentDetailAST.getPreviousSibling();

			while (true) {
				if (previousSiblingDetailAST == null) {
					break;
				}

				if (previousSiblingDetailAST.getType() == TokenTypes.SEMI) {
					previousSiblingDetailAST =
						previousSiblingDetailAST.getPreviousSibling();

					continue;
				}

				if (previousSiblingDetailAST.getType() != TokenTypes.EXPR) {
					break;
				}

				String variableName = firstChildDetailAST.getText();

				firstChildDetailAST = previousSiblingDetailAST.getFirstChild();

				if (firstChildDetailAST.getType() == TokenTypes.METHOD_CALL) {
					String methodName = getMethodName(firstChildDetailAST);

					if (methodName.equals("hideDefaultSuccessMessage")) {
						log(
							methodCallDetailAST,
							_MSG_MOVE_METHOD_CALL_BEFORE_METHOD_CALL);

						break;
					}
				}

				if (containsVariableName(firstChildDetailAST, variableName)) {
					break;
				}

				previousSiblingDetailAST =
					previousSiblingDetailAST.getPreviousSibling();
			}
		}
	}

	private static final String _MSG_MOVE_METHOD_CALL_BEFORE_METHOD_CALL =
		"method.call.move.before.method.call";

}