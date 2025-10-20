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
public class NullAssertionInIfStatementCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_IF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		DetailAST exprDetailAST = firstChildDetailAST.getNextSibling();

		List<DetailAST> compareDetailASTs = getAllChildTokens(
			exprDetailAST, true, TokenTypes.EQUAL, TokenTypes.NOT_EQUAL);

		for (DetailAST compareDetailAST : compareDetailASTs) {
			DetailAST identDetailAST = compareDetailAST.getFirstChild();

			if ((identDetailAST == null) ||
				(identDetailAST.getType() != TokenTypes.IDENT)) {

				continue;
			}

			DetailAST nextSiblingDetailAST = identDetailAST.getNextSibling();

			if (nextSiblingDetailAST.getType() != TokenTypes.LITERAL_NULL) {
				continue;
			}

			DetailAST parentDetailAST = compareDetailAST.getParent();

			List<DetailAST> methodCallDetailASTs = getAllChildTokens(
				parentDetailAST, true, TokenTypes.METHOD_CALL);

			if (methodCallDetailASTs.isEmpty()) {
				continue;
			}

			int compareColumnNumber = compareDetailAST.getColumnNo();
			int compareStartLineNumber = getStartLineNumber(compareDetailAST);

			String variableName = identDetailAST.getText();

			for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
				if (!variableName.equals(
						getVariableName(methodCallDetailAST))) {

					continue;
				}

				int methodCallColumnNumber = methodCallDetailAST.getColumnNo();
				int methodCallStartLineNumber = getStartLineNumber(
					methodCallDetailAST);

				if ((compareStartLineNumber == methodCallStartLineNumber) &&
					(compareColumnNumber > methodCallColumnNumber)) {

					log(
						nextSiblingDetailAST, _MSG_MOVE_NULL_CHECK,
						variableName);
				}

				if (compareStartLineNumber > methodCallStartLineNumber) {
					log(
						nextSiblingDetailAST, _MSG_MOVE_NULL_CHECK,
						variableName);
				}
			}
		}
	}

	private static final String _MSG_MOVE_NULL_CHECK = "null.check.move";

}