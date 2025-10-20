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
public class VariableDeclarationAsUsedCheck extends BaseAsUsedCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> variableDefinitionDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.VARIABLE_DEF);

		for (DetailAST variableDefinitionDetailAST :
				variableDefinitionDetailASTs) {

			_checkVariableDefinition(detailAST, variableDefinitionDetailAST);
		}
	}

	private void _checkVariableDefinition(
		DetailAST detailAST, DetailAST variableDefinitionDetailAST) {

		if (hasParentWithTokenType(
				variableDefinitionDetailAST, TokenTypes.FOR_EACH_CLAUSE,
				TokenTypes.FOR_INIT)) {

			return;
		}

		DetailAST assignDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			return;
		}

		DetailAST nameDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.IDENT);

		List<DetailAST> dependentIdentDetailASTs = getDependentIdentDetailASTs(
			variableDefinitionDetailAST,
			variableDefinitionDetailAST.getLineNo());

		if (dependentIdentDetailASTs.isEmpty()) {
			return;
		}

		String variableName = nameDetailAST.getText();

		DetailAST firstDependentIdentDetailAST = dependentIdentDetailASTs.get(
			0);

		int actionLineNumber = getActionLineNumber(variableDefinitionDetailAST);

		if (actionLineNumber != assignDetailAST.getLineNo()) {
			checkMoveAfterBranchingStatement(
				detailAST, assignDetailAST, variableName,
				firstDependentIdentDetailAST, actionLineNumber);
			checkMoveInsideIfStatement(
				assignDetailAST, nameDetailAST, variableName,
				firstDependentIdentDetailAST,
				dependentIdentDetailASTs.get(
					dependentIdentDetailASTs.size() - 1),
				actionLineNumber);
		}

		DetailAST modifiersDetailAST =
			variableDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS);

		if (!modifiersDetailAST.branchContains(TokenTypes.ANNOTATION)) {
			checkInline(
				assignDetailAST, variableName, firstDependentIdentDetailAST,
				dependentIdentDetailASTs);
		}
	}

}