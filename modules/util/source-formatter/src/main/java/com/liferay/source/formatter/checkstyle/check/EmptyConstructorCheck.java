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
public class EmptyConstructorCheck extends BaseCheck {

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

		if ((lastChildDetailAST.getType() != TokenTypes.SLIST) ||
			(lastChildDetailAST.getChildCount() != 1)) {

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

		DetailAST modifiersDetailAST = detailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		if ((modifiersDetailAST == null) ||
			!modifiersDetailAST.branchContains(TokenTypes.LITERAL_PUBLIC)) {

			return;
		}

		DetailAST parametersDetailAST = detailAST.findFirstToken(
			TokenTypes.PARAMETERS);

		if ((parametersDetailAST == null) ||
			(parametersDetailAST.getChildCount() == 0)) {

			DetailAST firstChildDetailAST = lastChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.RCURLY) {
				log(detailAST, _MSG_UNNEEDED_EMTPY_CONSTRUCTOR);
			}
		}
	}

	private static final String _MSG_UNNEEDED_EMTPY_CONSTRUCTOR =
		"empty.constructor.unneeded";

}