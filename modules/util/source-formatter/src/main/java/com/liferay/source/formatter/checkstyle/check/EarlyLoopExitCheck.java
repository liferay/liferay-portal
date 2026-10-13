/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.SetUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

import java.util.Set;

/**
 * @author Alan Huang
 */
public class EarlyLoopExitCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_IF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (!_isLastStatementInLoopBody(detailAST) ||
			_isLogLevelCheck(detailAST)) {

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
			log(detailAST, _MSG_EARLY_LOOP_EXIT, "continue");

			return;
		}

		DetailAST jumpStatementDetailAST = _getJumpStatementDetailAST(
			elseDetailAST.findFirstToken(TokenTypes.SLIST));

		if ((jumpStatementDetailAST != null) &&
			(jumpStatementDetailAST.getType() != TokenTypes.LITERAL_CONTINUE)) {

			log(
				detailAST, _MSG_EARLY_LOOP_EXIT,
				jumpStatementDetailAST.getText());
		}
	}

	private DetailAST _getJumpStatementDetailAST(DetailAST slistDetailAST) {
		if ((slistDetailAST == null) || (slistDetailAST.getChildCount() != 2)) {
			return null;
		}

		DetailAST firstChildDetailAST = slistDetailAST.getFirstChild();

		if (_isJumpStatement(firstChildDetailAST)) {
			return firstChildDetailAST;
		}

		return null;
	}

	private boolean _isJumpStatement(DetailAST detailAST) {
		return TokenUtil.isOfType(
			detailAST, TokenTypes.LITERAL_BREAK, TokenTypes.LITERAL_CONTINUE,
			TokenTypes.LITERAL_RETURN, TokenTypes.LITERAL_THROW);
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

		return TokenUtil.isOfType(
			parentDetailAST.getParent(), TokenTypes.LITERAL_DO,
			TokenTypes.LITERAL_FOR, TokenTypes.LITERAL_WHILE);
	}

	private boolean _isLogLevelCheck(DetailAST literalIfDetailAST) {
		for (String name :
				getNames(
					literalIfDetailAST.findFirstToken(TokenTypes.EXPR), true)) {

			if (_logNames.contains(name)) {
				return true;
			}
		}

		return false;
	}

	private static final String _MSG_EARLY_LOOP_EXIT = "loop.exit.early";

	private static final Set<String> _logNames = SetUtil.fromArray(
		"_log", "_logger", "log", "logger");

}