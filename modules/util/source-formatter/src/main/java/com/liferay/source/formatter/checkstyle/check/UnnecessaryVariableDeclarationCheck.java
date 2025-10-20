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
public class UnnecessaryVariableDeclarationCheck
	extends BaseUnnecessaryStatementCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.VARIABLE_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST modifiersDetailAST = detailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		if (modifiersDetailAST.branchContains(TokenTypes.ANNOTATION)) {
			return;
		}

		DetailAST semiDetailAST = detailAST.getNextSibling();

		if ((semiDetailAST == null) ||
			(semiDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		String variableName = getName(detailAST);

		if (!isExcludedPath(RUN_OUTSIDE_PORTAL_EXCLUDES)) {
			checkUnnecessaryListVariableBeforeReturn(
				detailAST, semiDetailAST, variableName,
				_MSG_UNNECESSARY_LIST_DECLARATION_BEFORE_RETURN);
		}

		checkUnnecessaryStatementBeforeReturn(
			detailAST, semiDetailAST, variableName,
			_MSG_UNNECESSARY_VARIABLE_DECLARATION_BEFORE_RETURN);

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.SLIST) {
			return;
		}

		checkUnnecessaryToString(
			detailAST.findFirstToken(TokenTypes.ASSIGN),
			_MSG_UNNECESSARY_VARIABLE_DECLARATION_TO_STRING);

		List<DetailAST> variableCallerDetailASTs = getVariableCallerDetailASTs(
			detailAST, variableName);

		if (variableCallerDetailASTs.isEmpty()) {
			return;
		}

		DetailAST firstVariableCallerDetailAST = variableCallerDetailASTs.get(
			0);

		DetailAST secondVariableCallerDetailAST = null;

		if (variableCallerDetailASTs.size() > 1) {
			secondVariableCallerDetailAST = variableCallerDetailASTs.get(1);
		}

		checkUnnecessaryStatementBeforeReassign(
			detailAST, firstVariableCallerDetailAST,
			secondVariableCallerDetailAST, parentDetailAST, variableName,
			_MSG_UNNECESSARY_VARIABLE_DECLARATION_BEFORE_REASSIGN);
	}

	private static final String
		_MSG_UNNECESSARY_LIST_DECLARATION_BEFORE_RETURN =
			"list.declaration.unnecessary.before.return";

	private static final String
		_MSG_UNNECESSARY_VARIABLE_DECLARATION_BEFORE_REASSIGN =
			"variable.declaration.unnecessary.before.reassign";

	private static final String
		_MSG_UNNECESSARY_VARIABLE_DECLARATION_BEFORE_RETURN =
			"variable.declaration.unnecessary.before.return";

	private static final String
		_MSG_UNNECESSARY_VARIABLE_DECLARATION_TO_STRING =
			"variable.declaration.unnecessary.to.string";

}