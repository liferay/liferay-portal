/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.source.formatter.check.util.JavaSourceUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class AssertFailCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String className = JavaSourceUtil.getClassName(getAbsolutePath());

		if (className.startsWith("Base")) {
			return;
		}

		List<String> importNames = getImportNames(detailAST);

		if (!importNames.contains("org.junit.Test")) {
			return;
		}

		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "Assert", "fail");

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			if (hasParentWithTokenType(
					methodCallDetailAST, TokenTypes.LAMBDA,
					TokenTypes.LITERAL_NEW)) {

				continue;
			}

			DetailAST literalTryDetailAST = getParentWithTokenType(
				methodCallDetailAST, TokenTypes.LITERAL_TRY);

			if ((literalTryDetailAST == null) ||
				hasParentWithTokenType(
					methodCallDetailAST, TokenTypes.LITERAL_CATCH)) {

				log(methodCallDetailAST, _MSG_AVOID_ASSERT_FAIL);

				continue;
			}

			DetailAST slistChildDetailAST = literalTryDetailAST.findFirstToken(
				TokenTypes.SLIST);

			DetailAST lastChildDetailAST = slistChildDetailAST.getLastChild();

			DetailAST previousSiblingDetailAST =
				lastChildDetailAST.getPreviousSibling();

			if ((previousSiblingDetailAST == null) ||
				(previousSiblingDetailAST.getType() != TokenTypes.SEMI)) {

				continue;
			}

			previousSiblingDetailAST =
				previousSiblingDetailAST.getPreviousSibling();

			if ((previousSiblingDetailAST == null) ||
				(previousSiblingDetailAST.getType() != TokenTypes.EXPR)) {

				continue;
			}

			DetailAST firstChildDetailAST =
				previousSiblingDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) {
				continue;
			}

			if (!equals(methodCallDetailAST, firstChildDetailAST)) {
				log(methodCallDetailAST, _MSG_AVOID_ASSERT_FAIL);
			}
		}
	}

	private static final String _MSG_AVOID_ASSERT_FAIL = "assert.fail.avoid";

}