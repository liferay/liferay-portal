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
public class BatchableUpdateCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.LITERAL_DO, TokenTypes.LITERAL_FOR,
			TokenTypes.LITERAL_WHILE
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST slistDetailAST = detailAST.findFirstToken(TokenTypes.SLIST);

		if (slistDetailAST == null) {
			return;
		}

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			slistDetailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST == null) {
				continue;
			}

			List<String> names = getNames(dotDetailAST, false);

			if ((names.size() != 2) ||
				!StringUtil.equals(names.get(1), "executeUpdate")) {

				continue;
			}

			DetailAST variableDefinitionDetailAST =
				getVariableDefinitionDetailAST(
					methodCallDetailAST, names.get(0), false);

			if ((variableDefinitionDetailAST == null) ||
				(variableDefinitionDetailAST.getLineNo() >=
					detailAST.getLineNo())) {

				continue;
			}

			String variableTypeName = getVariableTypeName(
				variableDefinitionDetailAST, names.get(0), false);

			if (!variableTypeName.equals("PreparedStatement")) {
				continue;
			}

			log(methodCallDetailAST, _MSG_USE_ADD_BATCH);
		}
	}

	private static final String _MSG_USE_ADD_BATCH = "add.batch.use";

}