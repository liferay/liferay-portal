/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
public class ThreadLocalVariableNameCheck extends BaseCheck {

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

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		List<DetailAST> variableDefinitionDetailASTs = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.VARIABLE_DEF);

		for (DetailAST variableDefinitionDetailAST :
				variableDefinitionDetailASTs) {

			_checkVariableDefinition(variableDefinitionDetailAST);
		}
	}

	private void _checkLiteralString(
		DetailAST detailAST, DetailAST literalNewDetailAST,
		String variableName) {

		DetailAST elistDetailAST = literalNewDetailAST.findFirstToken(
			TokenTypes.ELIST);

		if (elistDetailAST == null) {
			return;
		}

		DetailAST firstChildDetailAST = elistDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.EXPR)) {

			return;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.PLUS)) {

			return;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if ((firstChildDetailAST.getType() != TokenTypes.DOT) &&
			(firstChildDetailAST.getType() != TokenTypes.METHOD_CALL)) {

			return;
		}

		DetailAST nextSiblingDetailAST = firstChildDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.STRING_LITERAL)) {

			return;
		}

		String expectedLiteralString = "." + variableName;
		String value = StringUtil.unquote(nextSiblingDetailAST.getText());

		if (!StringUtil.equals(expectedLiteralString, value)) {
			log(
				detailAST, _MSG_INCORRECT_LITERAL_STRING, variableName,
				expectedLiteralString);
		}
	}

	private void _checkVariableAssign(
		DetailAST detailAST, String variableName) {

		List<DetailAST> variableCallerDetailASTs = getVariableCallerDetailASTs(
			detailAST, variableName);

		for (DetailAST variableCallerDetailAST : variableCallerDetailASTs) {
			DetailAST parentDetailAST = variableCallerDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.ASSIGN) {
				continue;
			}

			parentDetailAST = parentDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.EXPR) {
				continue;
			}

			DetailAST nextSiblingDetailAST =
				variableCallerDetailAST.getNextSibling();

			if ((nextSiblingDetailAST == null) ||
				(nextSiblingDetailAST.getType() != TokenTypes.LITERAL_NEW)) {

				continue;
			}

			_checkLiteralString(
				parentDetailAST, nextSiblingDetailAST, variableName);
		}
	}

	private void _checkVariableDefinition(DetailAST detailAST) {
		DetailAST modifiersDetailAST = detailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		if (!modifiersDetailAST.branchContains(TokenTypes.FINAL) ||
			!modifiersDetailAST.branchContains(TokenTypes.LITERAL_PRIVATE) ||
			!modifiersDetailAST.branchContains(TokenTypes.LITERAL_STATIC)) {

			return;
		}

		String variableTypeName = getTypeName(detailAST, true);

		if (variableTypeName == null) {
			return;
		}

		int x = variableTypeName.indexOf("<");

		if ((x == -1) ||
			!StringUtil.endsWith(
				variableTypeName.substring(0, x), "ThreadLocal")) {

			return;
		}

		String genericTypes = variableTypeName.substring(x);
		String variableName = getName(detailAST);

		if (!genericTypes.contains("ThreadLocal") &&
			variableName.endsWith("ThreadLocal")) {

			log(
				detailAST, _MSG_INCORRECT_ENDING_VARIABLE, "*ThreadLocal",
				"ThreadLocal");

			return;
		}

		DetailAST assignDetailAST = detailAST.findFirstToken(TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			_checkVariableAssign(detailAST, variableName);

			return;
		}

		DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.LITERAL_NEW)) {

			return;
		}

		_checkLiteralString(detailAST, firstChildDetailAST, variableName);
	}

	private static final String _MSG_INCORRECT_ENDING_VARIABLE =
		"variable.incorrect.ending";

	private static final String _MSG_INCORRECT_LITERAL_STRING =
		"literal.string.incorrect";

}