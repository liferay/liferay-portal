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
public class LogMessageCheck extends BaseMessageCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "_log", _LOG_METHOD_NAMES);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.ELIST);

			List<DetailAST> exprDetailASTs = getAllChildTokens(
				elistDetailAST, false, TokenTypes.EXPR);

			for (DetailAST exprDetailAST : exprDetailASTs) {
				checkMessage(
					getLiteralStringValue(exprDetailAST),
					exprDetailAST.getLineNo());
			}
		}
	}

	private static final String[] _LOG_METHOD_NAMES = {
		"debug", "error", "info", "trace", "warn"
	};

}