/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class ResultSetGetCallCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.PARAMETER_DEF, TokenTypes.RESOURCE,
			TokenTypes.VARIABLE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String variableName = getName(detailAST);

		String variableTypeName = getVariableTypeName(
			detailAST, variableName, false);

		if (!variableTypeName.equals("ResultSet") ||
			!_containsSelect(detailAST)) {

			return;
		}

		List<DetailAST> variableCallerDetailASTs = getVariableCallerDetailASTs(
			detailAST);

		for (DetailAST variableCallerDetailAST : variableCallerDetailASTs) {
			DetailAST parentDetailAST = variableCallerDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.DOT) {
				continue;
			}

			parentDetailAST = parentDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.METHOD_CALL) {
				continue;
			}

			String methodName = getMethodName(parentDetailAST);

			if (!ArrayUtil.contains(_GET_METHOD_NAMES, methodName)) {
				continue;
			}

			List<DetailAST> parameterExprDetailASTs =
				getParameterExprDetailASTs(parentDetailAST);

			if (parameterExprDetailASTs.size() != 1) {
				continue;
			}

			DetailAST parameterExprDetailAST = parameterExprDetailASTs.get(0);

			DetailAST firstChildDetailAST =
				parameterExprDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.NUM_INT) {
				log(firstChildDetailAST, _MSG_INCORRECT_SET_CALL_PARAMETER_1);

				continue;
			}

			if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
				continue;
			}

			String text = firstChildDetailAST.getText();

			if (methodName.equals("getInt") && text.equals("\"count\"")) {
				log(firstChildDetailAST, _MSG_METHOD_USE);

				continue;
			}

			if (!text.contains(".")) {
				continue;
			}

			log(firstChildDetailAST, _MSG_INCORRECT_SET_CALL_PARAMETER_2);
		}
	}

	private boolean _containsSelect(DetailAST detailAST) {
		DetailAST assignDetailAST = detailAST.findFirstToken(TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			return false;
		}

		DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.EXPR)) {

			return false;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.METHOD_CALL)) {

			return false;
		}

		DetailAST dotDetailAST = firstChildDetailAST.findFirstToken(
			TokenTypes.DOT);

		if (dotDetailAST == null) {
			return false;
		}

		List<String> names = getNames(dotDetailAST, false);

		if ((names.size() != 2) ||
			!StringUtil.equals(names.get(1), "executeQuery")) {

			return false;
		}

		DetailAST variableDefinitionDetailAST = getVariableDefinitionDetailAST(
			detailAST, names.get(0));

		if (variableDefinitionDetailAST == null) {
			return false;
		}

		String typeName = getTypeName(
			variableDefinitionDetailAST.findFirstToken(TokenTypes.TYPE), true);

		if (!typeName.equals("PreparedStatement")) {
			return false;
		}

		assignDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			return false;
		}

		List<DetailAST> stringLiteralDetailASTs = getAllChildTokens(
			assignDetailAST, true, TokenTypes.STRING_LITERAL);

		if (stringLiteralDetailASTs.isEmpty()) {
			return false;
		}

		for (DetailAST stringLiteralDetailAST : stringLiteralDetailASTs) {
			String text = stringLiteralDetailAST.getText();

			if (text.contains("select ")) {
				return true;
			}
		}

		return false;
	}

	private static final String[] _GET_METHOD_NAMES = {
		"getArray", "getAsciiStream", "getBigDecimal", "getBinaryStream",
		"getBlob", "getBoolean", "getByte", "getBytes", "getCharacterStream",
		"getClob", "getDate", "getDouble", "getFloat", "getInt", "getLong",
		"getNCharacterStream", "getNClob", "getNString", "getObject", "getRef",
		"getRowId", "getShort", "getSQLXML", "getString", "getTime",
		"getTimestamp", "getURL"
	};

	private static final String _MSG_INCORRECT_SET_CALL_PARAMETER_1 =
		"set.call.parameter.incorrect.1";

	private static final String _MSG_INCORRECT_SET_CALL_PARAMETER_2 =
		"set.call.parameter.incorrect.2";

	private static final String _MSG_METHOD_USE = "method.use";

}