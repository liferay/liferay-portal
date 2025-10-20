/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.GetterUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class SizeIsZeroCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "size");

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			_checkMethodCall(methodCallDetailAST);
		}
	}

	private void _checkMethodCall(DetailAST methodCallDetailAST) {
		DetailAST nextSiblingDetailAST = methodCallDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.NUM_INT)) {

			return;
		}

		int compareCount = GetterUtil.getInteger(
			nextSiblingDetailAST.getText());

		DetailAST parentDetailAST = methodCallDetailAST.getParent();

		if (((compareCount != 0) ||
			 ((parentDetailAST.getType() != TokenTypes.EQUAL) &&
			  (parentDetailAST.getType() != TokenTypes.NOT_EQUAL) &&
			  (parentDetailAST.getType() != TokenTypes.GT))) &&
			((compareCount != 1) ||
			 ((parentDetailAST.getType() != TokenTypes.GE) &&
			  (parentDetailAST.getType() != TokenTypes.LT)))) {

			return;
		}

		String variableName = getName(methodCallDetailAST.getFirstChild());

		String variableTypeName = getVariableTypeName(
			methodCallDetailAST, variableName, false);

		if (variableTypeName.matches("Collection|List|Map|Set")) {
			log(
				methodCallDetailAST, _MSG_USE_METHOD,
				variableName + ".isEmpty()");
		}
	}

	private static final String _MSG_USE_METHOD = "method.use";

}