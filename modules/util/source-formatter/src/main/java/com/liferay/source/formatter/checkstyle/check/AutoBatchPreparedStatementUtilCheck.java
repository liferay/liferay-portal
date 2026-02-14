/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class AutoBatchPreparedStatementUtilCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.RESOURCE, TokenTypes.VARIABLE_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String variableName = getName(detailAST);

		String variableTypeName = getVariableTypeName(
			detailAST, variableName, false);

		if (!variableTypeName.equals("PreparedStatement")) {
			return;
		}

		DetailAST assignDetailAST = detailAST.findFirstToken(TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			return;
		}

		DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.METHOD_CALL)) {

			return;
		}

		DetailAST dotDetailAST = firstChildDetailAST.findFirstToken(
			TokenTypes.DOT);

		if (dotDetailAST == null) {
			return;
		}

		List<String> names = getNames(dotDetailAST, false);

		if (names.size() != 2) {
			return;
		}

		if (StringUtil.equals(names.get(0), "AutoBatchPreparedStatementUtil") &&
			(StringUtil.equals(names.get(1), "autoBatch") ||
			 StringUtil.equals(names.get(1), "concurrentAutoBatch"))) {

			return;
		}

		DetailAST methodDefDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.METHOD_DEF);

		if (methodDefDetailAST == null) {
			return;
		}

		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			methodDefDetailAST, variableName, "executeBatch");

		if (methodCallDetailASTs.isEmpty()) {
			return;
		}

		log(detailAST, _MSG_USE_AUTO_BATCH_PREPARED_STATEMENT_UTIL);
	}

	private static final String _MSG_USE_AUTO_BATCH_PREPARED_STATEMENT_UTIL =
		"auto.batch.prepared.statement.util.use";

}