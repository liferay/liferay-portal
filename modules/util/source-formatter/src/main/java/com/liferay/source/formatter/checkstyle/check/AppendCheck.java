/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class AppendCheck extends BaseStringConcatenationCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "append");

		for (int i = 0; i < methodCallDetailASTs.size(); i++) {
			DetailAST methodCallDetailAST = methodCallDetailASTs.get(i);

			String variableName = getVariableName(methodCallDetailAST);

			String variableTypeName = getVariableTypeName(
				methodCallDetailAST, variableName, false);

			if (!variableTypeName.equals("StringBundler")) {
				continue;
			}

			DetailAST methodCallFirstParameterExprDetailAST =
				getFirstParameterExprDetailAST(methodCallDetailAST);

			if (methodCallFirstParameterExprDetailAST == null) {
				continue;
			}

			DetailAST methodCallFirstParameterDetailAST =
				methodCallFirstParameterExprDetailAST.getFirstChild();

			_checkPlusOperator(methodCallFirstParameterDetailAST);

			if ((methodCallFirstParameterDetailAST.getType() !=
					TokenTypes.STRING_LITERAL) ||
				_containsMethodCall(
					detailAST, variableName, "setIndex", "setStringAt")) {

				continue;
			}

			if (i < (methodCallDetailASTs.size() - 1)) {
				DetailAST nextMethodCallDetailAST = methodCallDetailASTs.get(
					i + 1);

				if (!variableName.equals(
						getVariableName(nextMethodCallDetailAST)) ||
					(getEndLineNumber(methodCallDetailAST) !=
						(getStartLineNumber(nextMethodCallDetailAST) - 1))) {

					continue;
				}

				DetailAST nextMethodCallFirstParameterExprDetailAST =
					getFirstParameterExprDetailAST(nextMethodCallDetailAST);

				if (nextMethodCallFirstParameterExprDetailAST != null) {
					DetailAST nextMethodCallFirstParameterDetailAST =
						nextMethodCallFirstParameterExprDetailAST.
							getFirstChild();

					if (nextMethodCallFirstParameterDetailAST.getType() ==
							TokenTypes.STRING_LITERAL) {

						_checkLiteralStrings(
							methodCallDetailAST, nextMethodCallDetailAST,
							methodCallFirstParameterDetailAST.getText(),
							nextMethodCallFirstParameterDetailAST.getText());
					}
					else {
						checkCombineOperand(
							methodCallFirstParameterDetailAST,
							nextMethodCallFirstParameterDetailAST);
					}
				}
			}

			if (i == 0) {
				continue;
			}

			DetailAST previousMethodCallDetailAST = methodCallDetailASTs.get(
				i - 1);

			if (!variableName.equals(
					getVariableName(previousMethodCallDetailAST)) ||
				(getEndLineNumber(previousMethodCallDetailAST) !=
					(getStartLineNumber(methodCallDetailAST) - 1))) {

				continue;
			}

			DetailAST previousMethodCallFirstParameterExprDetailAST =
				getFirstParameterExprDetailAST(previousMethodCallDetailAST);

			if (previousMethodCallFirstParameterExprDetailAST == null) {
				continue;
			}

			DetailAST previousMethodCallFirstParameterDetailAST =
				previousMethodCallFirstParameterExprDetailAST.getFirstChild();

			if (previousMethodCallFirstParameterDetailAST.getType() !=
					TokenTypes.STRING_LITERAL) {

				checkCombineOperand(
					methodCallFirstParameterDetailAST,
					previousMethodCallFirstParameterDetailAST);
			}
		}
	}

	private void _checkLiteralStrings(
		DetailAST methodCallDetailAST, DetailAST nextMethodCallDetailAST,
		String literalStringValue, String nextLiteralStringValue) {

		literalStringValue = literalStringValue.substring(
			1, literalStringValue.length() - 1);

		if (literalStringValue.endsWith("\\n")) {
			return;
		}

		nextLiteralStringValue = nextLiteralStringValue.substring(
			1, nextLiteralStringValue.length() - 1);

		checkLiteralStringStartAndEndCharacter(
			literalStringValue, nextLiteralStringValue,
			methodCallDetailAST.getLineNo());

		if ((_hasIncorrectLineBreaks(methodCallDetailAST) |
			 _hasIncorrectLineBreaks(nextMethodCallDetailAST)) ||
			literalStringValue.startsWith("<") ||
			literalStringValue.endsWith(">") ||
			nextLiteralStringValue.startsWith("<") ||
			nextLiteralStringValue.endsWith(">")) {

			return;
		}

		String line = getLine(methodCallDetailAST.getLineNo() - 1);

		int lineLength = CommonUtil.lengthExpandedTabs(
			line, line.length(), getTabWidth());

		if ((lineLength + nextLiteralStringValue.length()) <=
				getMaxLineLength()) {

			log(
				nextMethodCallDetailAST, MSG_COMBINE_LITERAL_STRINGS,
				literalStringValue, nextLiteralStringValue);
		}
		else {
			int pos = getStringBreakPos(
				literalStringValue, nextLiteralStringValue,
				getMaxLineLength() - lineLength);

			if (pos != -1) {
				log(
					nextMethodCallDetailAST, MSG_MOVE_LITERAL_STRING,
					nextLiteralStringValue.substring(0, pos + 1), "previous");
			}
		}

		checkLiteralStringBreaks(
			nextMethodCallDetailAST, line,
			getLine(methodCallDetailAST.getLineNo()), literalStringValue,
			nextLiteralStringValue);
	}

	private void _checkPlusOperator(DetailAST parameterDetailAST) {
		if (parameterDetailAST.getType() != TokenTypes.PLUS) {
			return;
		}

		List<DetailAST> literalStringDetailASTs = getAllChildTokens(
			parameterDetailAST, true, TokenTypes.STRING_LITERAL);

		if (!literalStringDetailASTs.isEmpty()) {
			log(parameterDetailAST, MSG_INCORRECT_PLUS);
		}
	}

	private boolean _containsMethodCall(
		DetailAST detailAST, String variableName, String... methodNames) {

		for (String methodName : methodNames) {
			List<DetailAST> methodCallDetailASTs = getMethodCalls(
				detailAST, variableName, methodName);

			if (!methodCallDetailASTs.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasIncorrectLineBreaks(DetailAST methodCallDetailAST) {
		if (getStartLineNumber(methodCallDetailAST) != getEndLineNumber(
				methodCallDetailAST)) {

			log(methodCallDetailAST, _MSG_INCORRECT_LINE_BREAK);

			return true;
		}

		return false;
	}

	private static final String _MSG_INCORRECT_LINE_BREAK =
		"line.break.incorrect";

}