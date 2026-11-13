/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ArrayUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Objects;

/**
 * @author Shuyang Zhou
 */
public class RegisterServicePropertiesCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.contains("/modules/third-party/") ||
			(detailAST.findFirstToken(TokenTypes.DOT) == null) ||
			!Objects.equals(getMethodName(detailAST), "registerService")) {

			return;
		}

		List<DetailAST> parameterExprDetailASTs = getParameterExprDetailASTs(
			detailAST);

		if (parameterExprDetailASTs.size() < 2) {
			return;
		}

		DetailAST exprDetailAST = parameterExprDetailASTs.get(
			parameterExprDetailASTs.size() - 1);

		DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

		if (_isEmptyDictionary(firstChildDetailAST)) {
			log(firstChildDetailAST, _MSG_USE_NULL);

			return;
		}

		if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		DetailAST variableDefinitionDetailAST = getVariableDefinitionDetailAST(
			firstChildDetailAST, firstChildDetailAST.getText(), false);

		if (variableDefinitionDetailAST == null) {
			return;
		}

		DetailAST assignDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			return;
		}

		DetailAST childDetailAST = assignDetailAST.getFirstChild();

		if ((childDetailAST.getType() != TokenTypes.EXPR) ||
			!_isEmptyDictionary(childDetailAST.getFirstChild())) {

			return;
		}

		List<DetailAST> variableCallerDetailASTs = getVariableCallerDetailASTs(
			variableDefinitionDetailAST);

		if ((variableCallerDetailASTs.size() == 1) &&
			equals(variableCallerDetailASTs.get(0), firstChildDetailAST)) {

			log(firstChildDetailAST, _MSG_USE_NULL);
		}
	}

	private boolean _isEmptyDictionary(DetailAST detailAST) {
		if ((detailAST.getType() != TokenTypes.LITERAL_NEW) ||
			(detailAST.findFirstToken(TokenTypes.OBJBLOCK) != null)) {

			return false;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(
			detailAST.getFirstChild());

		if (!ArrayUtil.contains(
				new String[] {
					"com.liferay.portal.kernel.util.HashMapDictionary",
					"java.util.Hashtable"
				},
				getFullyQualifiedTypeName(
					fullIdent.getText(), detailAST, true))) {

			return false;
		}

		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		if ((elistDetailAST == null) || (elistDetailAST.getChildCount() > 0)) {
			return false;
		}

		return true;
	}

	private static final String _MSG_USE_NULL = "null.use";

}