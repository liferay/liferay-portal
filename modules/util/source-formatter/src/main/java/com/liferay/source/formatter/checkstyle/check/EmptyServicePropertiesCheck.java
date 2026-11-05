/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ArrayUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Objects;

/**
 * @author Shuyang Zhou
 */
public class EmptyServicePropertiesCheck extends BaseCheck {

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

		DetailAST argumentDetailAST = exprDetailAST.getFirstChild();

		if (_isEmptyDictionary(argumentDetailAST)) {
			log(argumentDetailAST, _MSG_PASS_NULL);

			return;
		}

		if (argumentDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		DetailAST variableDefinitionDetailAST = getVariableDefinitionDetailAST(
			argumentDetailAST, argumentDetailAST.getText(), false);

		if ((variableDefinitionDetailAST == null) ||
			(variableDefinitionDetailAST.getType() !=
				TokenTypes.VARIABLE_DEF)) {

			return;
		}

		DetailAST assignDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			return;
		}

		DetailAST initializerExprDetailAST = assignDetailAST.getFirstChild();

		if ((initializerExprDetailAST.getType() != TokenTypes.EXPR) ||
			!_isEmptyDictionary(initializerExprDetailAST.getFirstChild())) {

			return;
		}

		List<DetailAST> variableCallerDetailASTs = getVariableCallerDetailASTs(
			variableDefinitionDetailAST);

		if ((variableCallerDetailASTs.size() == 1) &&
			equals(variableCallerDetailASTs.get(0), argumentDetailAST)) {

			log(argumentDetailAST, _MSG_PASS_NULL);
		}
	}

	private boolean _isEmptyDictionary(DetailAST detailAST) {
		if ((detailAST.getType() != TokenTypes.LITERAL_NEW) ||
			(detailAST.findFirstToken(TokenTypes.OBJBLOCK) != null)) {

			return false;
		}

		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		if ((elistDetailAST == null) || (elistDetailAST.getChildCount() > 0)) {
			return false;
		}

		DetailAST typeDetailAST = detailAST.getFirstChild();

		if (typeDetailAST.getType() == TokenTypes.DOT) {
			typeDetailAST = typeDetailAST.findFirstToken(TokenTypes.IDENT);
		}

		if ((typeDetailAST == null) ||
			(typeDetailAST.getType() != TokenTypes.IDENT)) {

			return false;
		}

		return ArrayUtil.contains(
			new String[] {"HashMapDictionary", "Hashtable"},
			typeDetailAST.getText());
	}

	private static final String _MSG_PASS_NULL = "service.properties.null";

}