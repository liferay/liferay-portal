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
public class AssignAsUsedCheck extends BaseAsUsedCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> assignDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.ASSIGN);

		for (DetailAST assignDetailAST : assignDetailASTs) {
			DetailAST parentDetailAST = assignDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.EXPR) {
				continue;
			}

			parentDetailAST = parentDetailAST.getParent();

			if (parentDetailAST.getType() == TokenTypes.SLIST) {
				_checkAssign(
					detailAST, assignDetailAST,
					getEndLineNumber(parentDetailAST));
			}
		}
	}

	private void _checkAssign(
		DetailAST detailAST, DetailAST assignDetailAST, int endRange) {

		if (hasParentWithTokenType(
				assignDetailAST, TokenTypes.FOR_EACH_CLAUSE,
				TokenTypes.FOR_INIT)) {

			return;
		}

		DetailAST nameDetailAST = assignDetailAST.getFirstChild();

		if (nameDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		String variableName = nameDetailAST.getText();

		DetailAST typeDetailAST = getVariableTypeDetailAST(
			assignDetailAST, variableName, false);

		if (typeDetailAST == null) {
			return;
		}

		DetailAST parentDetailAST = typeDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.VARIABLE_DEF) {
			return;
		}

		List<DetailAST> dependentIdentDetailASTs = getDependentIdentDetailASTs(
			parentDetailAST, parentDetailAST.getLineNo());

		if (dependentIdentDetailASTs.isEmpty()) {
			return;
		}

		int endLineNumber = getEndLineNumber(assignDetailAST);

		for (DetailAST dependentIdentDetailAST : dependentIdentDetailASTs) {
			int lineNumber = dependentIdentDetailAST.getLineNo();

			if (lineNumber <= endLineNumber) {
				continue;
			}

			if (lineNumber > endRange) {
				return;
			}

			if (!hasParentWithTokenType(
					assignDetailAST, TokenTypes.LITERAL_FOR,
					TokenTypes.LITERAL_WHILE)) {

				int actionLineNumber = getActionLineNumber(assignDetailAST);

				if (actionLineNumber != assignDetailAST.getLineNo()) {
					checkMoveAfterBranchingStatement(
						detailAST, assignDetailAST, variableName,
						dependentIdentDetailAST, actionLineNumber);
					checkMoveInsideIfStatement(
						assignDetailAST, nameDetailAST, variableName,
						dependentIdentDetailAST,
						dependentIdentDetailASTs.get(
							dependentIdentDetailASTs.size() - 1),
						actionLineNumber);
				}
			}

			parentDetailAST = getParentWithTokenType(
				assignDetailAST, TokenTypes.LITERAL_FOR,
				TokenTypes.LITERAL_WHILE);

			if (parentDetailAST != null) {
				List<String> names = getNames(parentDetailAST, true);

				if (!names.contains(variableName)) {
					checkInline(
						assignDetailAST, variableName, dependentIdentDetailAST,
						dependentIdentDetailASTs);
				}
			}

			return;
		}
	}

}