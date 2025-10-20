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
public class ConstructorGlobalVariableDeclarationCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.OBJBLOCK) {
			return;
		}

		DetailAST lastChildDetailAST = detailAST.getLastChild();

		if (lastChildDetailAST.getType() != TokenTypes.SLIST) {
			return;
		}

		DetailAST literalThrowsDetailAST = detailAST.findFirstToken(
			TokenTypes.LITERAL_THROWS);

		if (literalThrowsDetailAST != null) {
			return;
		}

		List<DetailAST> constructorDefinitionDetailASTs = getAllChildTokens(
			parentDetailAST, false, TokenTypes.CTOR_DEF);

		if (constructorDefinitionDetailASTs.size() > 1) {
			return;
		}

		List<DetailAST> expressionDetailASTs = getAllChildTokens(
			lastChildDetailAST, false, TokenTypes.EXPR);

		for (DetailAST expressionDetailAST : expressionDetailASTs) {
			DetailAST firstChildDetailAST = expressionDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.ASSIGN) {
				continue;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
				continue;
			}

			String variableName = firstChildDetailAST.getText();

			if (!variableName.startsWith("_")) {
				continue;
			}

			DetailAST nextSiblingDetailAST =
				firstChildDetailAST.getNextSibling();

			if (nextSiblingDetailAST.getType() != TokenTypes.LITERAL_NEW) {
				continue;
			}

			firstChildDetailAST = nextSiblingDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
				continue;
			}

			DetailAST objBlockDetailAST = nextSiblingDetailAST.findFirstToken(
				TokenTypes.OBJBLOCK);

			if (objBlockDetailAST != null) {
				continue;
			}

			DetailAST elistDetailAST = nextSiblingDetailAST.findFirstToken(
				TokenTypes.ELIST);

			if ((elistDetailAST == null) ||
				(elistDetailAST.getChildCount() > 0)) {

				continue;
			}

			List<DetailAST> variableDefDetailASTs = getAllChildTokens(
				parentDetailAST, false, TokenTypes.VARIABLE_DEF);

			for (DetailAST variableDefDetailAST : variableDefDetailASTs) {
				DetailAST identDetailAST = variableDefDetailAST.findFirstToken(
					TokenTypes.IDENT);

				if (variableName.equals(identDetailAST.getText())) {
					log(
						firstChildDetailAST, _MSG_DECLARE_GLOBAL_VARIABLE_VALUE,
						variableName, variableDefDetailAST.getLineNo());

					break;
				}
			}
		}
	}

	private static final String _MSG_DECLARE_GLOBAL_VARIABLE_VALUE =
		"global.variable.value.declare";

}