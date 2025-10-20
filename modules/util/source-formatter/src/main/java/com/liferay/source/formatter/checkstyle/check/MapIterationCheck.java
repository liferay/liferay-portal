/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class MapIterationCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> forEachClauseDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.FOR_EACH_CLAUSE);

		for (DetailAST forEachClauseDetailAST : forEachClauseDetailASTs) {
			_checkKeySetIteration(forEachClauseDetailAST);
		}
	}

	private void _checkKeySetIteration(DetailAST forEachClauseDetailAST) {
		DetailAST variableDefinitionDetailAST =
			forEachClauseDetailAST.findFirstToken(TokenTypes.VARIABLE_DEF);

		String keyName = getName(variableDefinitionDetailAST);

		List<DetailAST> keySetMethodCallDetailASTs = getMethodCalls(
			forEachClauseDetailAST, "keySet");

		for (DetailAST keySetMethodCallDetailAST : keySetMethodCallDetailASTs) {
			FullIdent fullIdent = FullIdent.createFullIdentBelow(
				keySetMethodCallDetailAST);

			String mapName = StringUtil.replaceLast(
				fullIdent.getText(), ".keySet", StringPool.BLANK);

			if (!_containsGetMethod(
					forEachClauseDetailAST.getParent(), keyName, mapName)) {

				continue;
			}

			DetailAST typeDetailAST = getVariableTypeDetailAST(
				keySetMethodCallDetailAST, mapName);

			if ((typeDetailAST != null) && isCollection(typeDetailAST)) {
				List<DetailAST> wildcardTypeDetailASTs = getAllChildTokens(
					typeDetailAST, true, TokenTypes.WILDCARD_TYPE);

				if (wildcardTypeDetailASTs.isEmpty()) {
					log(forEachClauseDetailAST, _MSG_USE_ENTRY_SET);
				}
			}
		}
	}

	private boolean _containsGetMethod(
		DetailAST forDetailAST, String keyName, String mapName) {

		List<DetailAST> getMethodCallDetailASTs = getMethodCalls(
			forDetailAST, mapName, "get");

		for (DetailAST getMethodCallDetailAST : getMethodCallDetailASTs) {
			DetailAST eListDetailAST = getMethodCallDetailAST.findFirstToken(
				TokenTypes.ELIST);

			DetailAST firstChildDetailAST = eListDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
				continue;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
				String parameterName = firstChildDetailAST.getText();

				if (parameterName.equals(keyName)) {
					return true;
				}
			}
		}

		return false;
	}

	private static final String _MSG_USE_ENTRY_SET = "entry.set.use";

}