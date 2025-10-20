/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hugo Huijser
 */
public class RedundantBranchingStatementCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.CTOR_DEF, TokenTypes.LITERAL_FOR,
			TokenTypes.LITERAL_WHILE, TokenTypes.METHOD_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if ((detailAST.getType() == TokenTypes.LITERAL_FOR) ||
			(detailAST.getType() == TokenTypes.LITERAL_WHILE)) {

			_checkRedundantBranchingStatements(
				detailAST, TokenTypes.LITERAL_CONTINUE);

			return;
		}

		if (detailAST.getType() == TokenTypes.METHOD_DEF) {
			DetailAST typeDetailAST = detailAST.findFirstToken(TokenTypes.TYPE);

			DetailAST firstChildDetailAST = typeDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.LITERAL_VOID) {
				return;
			}
		}

		_checkRedundantBranchingStatements(
			detailAST, TokenTypes.LITERAL_RETURN);
	}

	private void _checkRedundantBranchingStatements(
		DetailAST detailAST, int branchingStatementType) {

		List<DetailAST> lastStatementDetailASTs = _getLastStatementDetailASTs(
			detailAST.findFirstToken(TokenTypes.SLIST));

		for (DetailAST lastStatementDetailAST : lastStatementDetailASTs) {
			if (lastStatementDetailAST.getType() != branchingStatementType) {
				continue;
			}

			DetailAST firstChildDetailAST =
				lastStatementDetailAST.getFirstChild();

			if ((firstChildDetailAST != null) &&
				(firstChildDetailAST.getType() == TokenTypes.SEMI)) {

				log(
					lastStatementDetailAST, _MSG_REDUNDANT_BRANCHING_STATEMENT,
					lastStatementDetailAST.getText());
			}
		}
	}

	private List<DetailAST> _getLastStatementDetailASTs(
		DetailAST slistDetailAST) {

		List<DetailAST> lastStatementDetailASTs = new ArrayList<>();

		if ((slistDetailAST == null) ||
			(slistDetailAST.getType() != TokenTypes.SLIST)) {

			return lastStatementDetailASTs;
		}

		DetailAST nextSiblingDetailAST = slistDetailAST.getNextSibling();

		while (true) {
			if (nextSiblingDetailAST == null) {
				break;
			}

			if (nextSiblingDetailAST.getType() == TokenTypes.LITERAL_CATCH) {
				lastStatementDetailASTs.addAll(
					_getLastStatementDetailASTs(
						nextSiblingDetailAST.findFirstToken(TokenTypes.SLIST)));

				nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

				continue;
			}

			if (nextSiblingDetailAST.getType() == TokenTypes.LITERAL_ELSE) {
				DetailAST firstChildDetailAST =
					nextSiblingDetailAST.getFirstChild();

				if (firstChildDetailAST.getType() == TokenTypes.LITERAL_IF) {
					lastStatementDetailASTs.addAll(
						_getLastStatementDetailASTs(
							firstChildDetailAST.findFirstToken(
								TokenTypes.SLIST)));
				}
				else {
					lastStatementDetailASTs.addAll(
						_getLastStatementDetailASTs(firstChildDetailAST));
				}
			}

			break;
		}

		DetailAST lastChildDetailAST = slistDetailAST.getLastChild();

		DetailAST previousSiblingDetailAST =
			lastChildDetailAST.getPreviousSibling();

		if (previousSiblingDetailAST == null) {
			return lastStatementDetailASTs;
		}

		if ((previousSiblingDetailAST.getType() == TokenTypes.LITERAL_IF) ||
			(previousSiblingDetailAST.getType() == TokenTypes.LITERAL_TRY)) {

			lastStatementDetailASTs.addAll(
				_getLastStatementDetailASTs(
					previousSiblingDetailAST.findFirstToken(TokenTypes.SLIST)));
		}
		else {
			lastStatementDetailASTs.add(previousSiblingDetailAST);
		}

		return lastStatementDetailASTs;
	}

	private static final String _MSG_REDUNDANT_BRANCHING_STATEMENT =
		"branching.statement.redundant";

}