/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class ServiceProxyFactoryCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		String absolutePath = getAbsolutePath();

		if (absolutePath.endsWith("ServiceProxyFactoryTest.java") ||
			(firstChildDetailAST.getType() != TokenTypes.DOT)) {

			return;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

		String fullyQualifiedName = fullIdent.getText();

		if (!fullyQualifiedName.equals(
				"ServiceProxyFactory.newServiceTrackedInstance")) {

			return;
		}

		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		List<DetailAST> exprDetailASTs = getAllChildTokens(
			elistDetailAST, false, TokenTypes.EXPR);

		if (exprDetailASTs.size() < 2) {
			return;
		}

		DetailAST exprDetailAST = exprDetailASTs.get(1);

		firstChildDetailAST = exprDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

		DetailAST parentDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.CLASS_DEF);

		if (parentDetailAST == null) {
			return;
		}

		String expectedParameter = getName(parentDetailAST) + ".class";

		if (expectedParameter.equals(fullIdent.getText())) {
			return;
		}

		log(detailAST, _MSG_INCORRECT_PARAMETER, expectedParameter);
	}

	private static final String _MSG_INCORRECT_PARAMETER =
		"parameter.incorrect";

}