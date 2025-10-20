/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.ScopeUtil;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class LocalPatternCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.VARIABLE_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (!ScopeUtil.isLocalVariableDef(detailAST)) {
			return;
		}

		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "Pattern", "compile");

		if (methodCallDetailASTs.isEmpty()) {
			return;
		}

		DetailAST methodCallDetailAST = methodCallDetailASTs.get(0);

		DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
			TokenTypes.ELIST);

		DetailAST expressionDetailAST = elistDetailAST.findFirstToken(
			TokenTypes.EXPR);

		List<DetailAST> childDetailASTs = getAllChildTokens(
			expressionDetailAST, true, ALL_TYPES);

		for (DetailAST childDetailAST : childDetailASTs) {
			if ((childDetailAST.getType() != TokenTypes.PLUS) &&
				(childDetailAST.getType() != TokenTypes.STRING_LITERAL)) {

				return;
			}
		}

		log(detailAST, _MSG_LOCAL_PATTERN, getName(detailAST));
	}

	private static final String _MSG_LOCAL_PATTERN = "pattern.local";

}