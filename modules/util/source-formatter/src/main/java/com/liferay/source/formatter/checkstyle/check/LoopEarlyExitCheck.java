/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * @author Alan Huang
 */
public class LoopEarlyExitCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_IF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (!_isLastStatementInLoopBody(detailAST)) {
			return;
		}

		DetailAST slistDetailAST = detailAST.findFirstToken(TokenTypes.SLIST);

		if (slistDetailAST == null) {
			return;
		}

		DetailAST rcurlyDetailAST = slistDetailAST.getLastChild();

		if (_isJumpStatement(rcurlyDetailAST.getPreviousSibling())) {
			return;
		}

		DetailAST elseDetailAST = detailAST.findFirstToken(
			TokenTypes.LITERAL_ELSE);

		if (elseDetailAST == null) {
			log(detailAST, _MSG_EXIT_EARLY, "continue");

			return;
		}

		String jumpStatementName = _getJumpStatementName(
			elseDetailAST.findFirstToken(TokenTypes.SLIST));

		if ((jumpStatementName != null) &&
			!jumpStatementName.equals("continue")) {

			log(detailAST, _MSG_EXIT_EARLY, jumpStatementName);
		}
	}

	private String _getJumpStatementName(DetailAST slistDetailAST) {
		if (slistDetailAST == null) {
			return null;
		}

		DetailAST firstChildDetailAST = slistDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getNextSibling() !=
				slistDetailAST.getLastChild())) {

			return null;
		}

		if (firstChildDetailAST.getType() == TokenTypes.LITERAL_BREAK) {
			return "break";
		}

		if (firstChildDetailAST.getType() == TokenTypes.LITERAL_CONTINUE) {
			return "continue";
		}

		if (firstChildDetailAST.getType() == TokenTypes.LITERAL_RETURN) {
			return "return";
		}

		if (firstChildDetailAST.getType() == TokenTypes.LITERAL_THROW) {
			return "throw";
		}

		return null;
	}

	private boolean _isJumpStatement(DetailAST detailAST) {
		if ((detailAST != null) &&
			((detailAST.getType() == TokenTypes.LITERAL_BREAK) ||
			 (detailAST.getType() == TokenTypes.LITERAL_CONTINUE) ||
			 (detailAST.getType() == TokenTypes.LITERAL_RETURN) ||
			 (detailAST.getType() == TokenTypes.LITERAL_THROW))) {

			return true;
		}

		return false;
	}

	private boolean _isLastStatementInLoopBody(DetailAST literalIfDetailAST) {
		DetailAST nextSiblingDetailAST = literalIfDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.RCURLY)) {

			return false;
		}

		DetailAST parentDetailAST = literalIfDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.SLIST) {
			return false;
		}

		DetailAST loopDetailAST = parentDetailAST.getParent();

		if ((loopDetailAST.getType() == TokenTypes.LITERAL_DO) ||
			(loopDetailAST.getType() == TokenTypes.LITERAL_FOR) ||
			(loopDetailAST.getType() == TokenTypes.LITERAL_WHILE)) {

			return true;
		}

		return false;
	}

	private static final String _MSG_EXIT_EARLY = "loop.exit.early";

}