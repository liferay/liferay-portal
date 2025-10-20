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
public class FrameworkBundleCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		List<String> importNames = getImportNames(detailAST);

		if (!importNames.contains("org.osgi.framework.Bundle")) {
			return;
		}

		List<DetailAST> detailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF);

		for (DetailAST curDetailAST : detailASTs) {
			_checkGetHeadersMethodCall(curDetailAST);
		}
	}

	private void _checkGetHeadersMethodCall(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "getHeaders");

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.ELIST);

			List<DetailAST> exprDetailASTs = getAllChildTokens(
				elistDetailAST, false, TokenTypes.EXPR);

			if (!exprDetailASTs.isEmpty()) {
				continue;
			}

			String variableTypeName = getVariableTypeName(
				methodCallDetailAST, getVariableName(methodCallDetailAST),
				false);

			if (variableTypeName.equals("Bundle")) {
				log(methodCallDetailAST, _MSG_USE_BUNDLE_GET_HEADERS);
			}
		}
	}

	private static final String _MSG_USE_BUNDLE_GET_HEADERS =
		"bundle.get.headers.use";

}