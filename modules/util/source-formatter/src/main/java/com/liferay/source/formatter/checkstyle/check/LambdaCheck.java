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
public class LambdaCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LAMBDA};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.SWITCH_RULE) {
			return;
		}

		DetailAST lastChildDetailAST = detailAST.getLastChild();

		if (lastChildDetailAST.getType() != TokenTypes.SLIST) {
			DetailAST parametersDetailAST = detailAST.findFirstToken(
				TokenTypes.PARAMETERS);

			if ((parametersDetailAST == null) ||
				(parametersDetailAST.getChildCount() != 0)) {

				return;
			}

			DetailAST exprDetailAST = detailAST.findFirstToken(TokenTypes.EXPR);

			if ((exprDetailAST == null) ||
				(exprDetailAST.getLineNo() != detailAST.getLineNo())) {

				return;
			}

			DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

			if ((firstChildDetailAST != null) &&
				(firstChildDetailAST.getType() != TokenTypes.METHOD_CALL)) {

				return;
			}

			List<DetailAST> parameterExprDetailASTs =
				getParameterExprDetailASTs(firstChildDetailAST);

			if (!parameterExprDetailASTs.isEmpty()) {
				return;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if ((firstChildDetailAST.getType() != TokenTypes.DOT) ||
				(firstChildDetailAST.getChildCount(TokenTypes.IDENT) != 2)) {

				return;
			}

			DetailAST nextSiblingDetailAST =
				firstChildDetailAST.getNextSibling();

			if ((nextSiblingDetailAST.getType() != TokenTypes.ELIST) ||
				nextSiblingDetailAST.hasChildren()) {

				return;
			}

			log(detailAST, _MSG_SIMPLIFY_LAMBDA_2);

			return;
		}

		DetailAST firstChildDetailAST = lastChildDetailAST.getFirstChild();

		if (lastChildDetailAST.getChildCount() == 2) {
			if (firstChildDetailAST.getType() == TokenTypes.LITERAL_RETURN) {
				log(detailAST, _MSG_SIMPLIFY_LAMBDA_1);
			}

			return;
		}

		if ((lastChildDetailAST.getChildCount() != 3) ||
			(firstChildDetailAST.getType() != TokenTypes.EXPR)) {

			return;
		}

		DetailAST nextSiblingDetailAST = firstChildDetailAST.getNextSibling();

		if (nextSiblingDetailAST.getType() == TokenTypes.SEMI) {
			log(detailAST, _MSG_SIMPLIFY_LAMBDA_1);
		}
	}

	private static final String _MSG_SIMPLIFY_LAMBDA_1 = "lambda.simplify.1";

	private static final String _MSG_SIMPLIFY_LAMBDA_2 = "lambda.simplify.2";

}