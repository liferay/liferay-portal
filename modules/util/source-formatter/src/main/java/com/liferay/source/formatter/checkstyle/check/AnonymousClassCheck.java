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
public class AnonymousClassCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> addBackgroundTaskMethodCallDetailASTs = getMethodCalls(
			detailAST, "addBackgroundTask");

		for (DetailAST addBackgroundTaskMethodCallDetailAST :
				addBackgroundTaskMethodCallDetailASTs) {

			_checkAddBackgroundTaskMethodCall(
				addBackgroundTaskMethodCallDetailAST);
		}
	}

	private void _checkAddBackgroundTaskMethodCall(
		DetailAST methodCallDetailAST) {

		DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		String variableName = firstChildDetailAST.getText();

		String typeName = getVariableTypeName(
			methodCallDetailAST, variableName, false);

		if ((typeName == null) || !typeName.equals("BackgroundTaskManager")) {
			return;
		}

		DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
			TokenTypes.ELIST);

		List<DetailAST> exprDetailASTs = getAllChildTokens(
			elistDetailAST, false, TokenTypes.EXPR);

		if (exprDetailASTs.size() != 6) {
			return;
		}

		DetailAST exprDetailAST = exprDetailASTs.get(4);

		firstChildDetailAST = exprDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		String mapVariableName = firstChildDetailAST.getText();

		DetailAST typeDetailAST = getVariableTypeDetailAST(
			methodCallDetailAST, mapVariableName);

		if (typeDetailAST == null) {
			return;
		}

		DetailAST parentDetailAST = typeDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.VARIABLE_DEF) {
			return;
		}

		DetailAST assignDetailAST = parentDetailAST.findFirstToken(
			TokenTypes.ASSIGN);

		firstChildDetailAST = assignDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.LITERAL_NEW) {
			return;
		}

		if (firstChildDetailAST.findFirstToken(TokenTypes.OBJBLOCK) != null) {
			log(
				assignDetailAST, _MSG_INCORRECT_ANONYMOUS_CLASS,
				mapVariableName, variableName + ".addBackgroundTask");
		}
	}

	private static final String _MSG_INCORRECT_ANONYMOUS_CLASS =
		"anonymous.class.incorrect";

}