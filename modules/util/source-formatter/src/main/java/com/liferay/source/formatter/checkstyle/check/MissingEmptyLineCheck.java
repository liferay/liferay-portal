/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hugo Huijser
 */
public class MissingEmptyLineCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.ASSIGN, TokenTypes.INSTANCE_INIT, TokenTypes.METHOD_CALL,
			TokenTypes.VARIABLE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.INSTANCE_INIT) {
			_checkMissingEmptyLineInInstanceInit(detailAST);

			return;
		}

		if (detailAST.getType() == TokenTypes.METHOD_CALL) {
			_checkMissingEmptyLinesAroundMethodCall(detailAST);

			return;
		}

		if (detailAST.getType() == TokenTypes.VARIABLE_DEF) {
			DetailAST parentDetailAST = detailAST.getParent();

			if (parentDetailAST.getType() == TokenTypes.SLIST) {
				_checkMissingEmptyLineAfterVariableDef(
					detailAST, "ThemeDisplay");
				_checkMissingEmptyLineBeforeVariableDef(detailAST);
			}

			return;
		}

		_checkMissingEmptyLineBeforeAssign(detailAST);

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() == TokenTypes.DOT)) {

			return;
		}

		String variableName = _getVariableName(detailAST);

		if (variableName == null) {
			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.EXPR) {
			DetailAST grandParentDetailAST = parentDetailAST.getParent();

			if (grandParentDetailAST.getType() == TokenTypes.SLIST) {
				grandParentDetailAST = grandParentDetailAST.getParent();

				if (grandParentDetailAST.getType() ==
						TokenTypes.INSTANCE_INIT) {

					return;
				}
			}
		}

		_checkMissingEmptyLineAfterReferencingVariable(
			parentDetailAST, variableName, getEndLineNumber(detailAST));
		_checkMissingEmptyLineBetweenAssigningAndUsingVariable(
			parentDetailAST, variableName, getEndLineNumber(detailAST));
	}

	private void _checkMissingEmptyLineAfterMethodCall(
		DetailAST detailAST, String variableName,
		DetailAST nextSiblingDetailAST) {

		if (nextSiblingDetailAST == null) {
			return;
		}

		int endLineNumber = getEndLineNumber(detailAST);

		int nextExpressionStartLineNumber = getStartLineNumber(
			nextSiblingDetailAST);

		if ((endLineNumber + 1) != nextExpressionStartLineNumber) {
			return;
		}

		if (nextSiblingDetailAST.getType() == TokenTypes.EXPR) {
			List<String> enforceEmptyLineAfterMethodNames = getAttributeValues(
				_ENFORCE_EMPTY_LINE_AFTER_METHOD_NAMES_KEY);

			String methodName = getMethodName(detailAST);

			if (enforceEmptyLineAfterMethodNames.contains(
					StringBundler.concat(
						getVariableTypeName(detailAST, variableName, false),
						StringPool.PERIOD, methodName))) {

				log(
					endLineNumber, _MSG_MISSING_EMPTY_LINE_AFTER_METHOD_NAME,
					StringBundler.concat(
						variableName, StringPool.PERIOD, methodName));
			}

			DetailAST firstChildDetailAST =
				nextSiblingDetailAST.getFirstChild();

			if ((firstChildDetailAST.getType() == TokenTypes.METHOD_CALL) &&
				variableName.equals(getVariableName(firstChildDetailAST))) {

				return;
			}
		}

		if (containsVariableName(nextSiblingDetailAST, variableName)) {
			log(
				endLineNumber, _MSG_MISSING_EMPTY_LINE_LINE_NUMBER, "after",
				endLineNumber);
		}
	}

	private void _checkMissingEmptyLineAfterReferencingVariable(
		DetailAST detailAST, String variableName, int endLineNumber) {

		String lastAssignedVariableName = null;
		DetailAST previousDetailAST = null;
		boolean referenced = false;

		DetailAST nextSiblingDetailAST = detailAST.getNextSibling();

		while (true) {
			if ((nextSiblingDetailAST == null) ||
				(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

				return;
			}

			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

			if ((nextSiblingDetailAST == null) ||
				hasPrecedingPlaceholder(nextSiblingDetailAST) ||
				((nextSiblingDetailAST.getType() != TokenTypes.EXPR) &&
				 (nextSiblingDetailAST.getType() != TokenTypes.VARIABLE_DEF))) {

				return;
			}

			if (!containsVariableName(nextSiblingDetailAST, variableName)) {
				if (!referenced) {
					return;
				}

				int nextExpressionStartLineNumber = getStartLineNumber(
					nextSiblingDetailAST);

				if ((endLineNumber + 1) != nextExpressionStartLineNumber) {
					return;
				}

				if (!containsVariableName(
						previousDetailAST, lastAssignedVariableName) ||
					!containsVariableName(
						nextSiblingDetailAST, lastAssignedVariableName)) {

					log(
						nextExpressionStartLineNumber,
						_MSG_MISSING_EMPTY_LINE_AFTER_VARIABLE_REFERENCE,
						nextExpressionStartLineNumber, variableName);
				}

				return;
			}

			List<DetailAST> assignDetailASTs = getAllChildTokens(
				nextSiblingDetailAST, false, TokenTypes.ASSIGN);

			if (assignDetailASTs.size() == 1) {
				lastAssignedVariableName = _getVariableName(
					assignDetailASTs.get(0));
			}

			referenced = true;

			endLineNumber = getEndLineNumber(nextSiblingDetailAST);

			previousDetailAST = nextSiblingDetailAST;

			nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();
		}
	}

	private void _checkMissingEmptyLineAfterVariableDef(
		DetailAST detailAST, String variableTypeName) {

		if (detailAST.findFirstToken(TokenTypes.ASSIGN) == null) {
			return;
		}

		int endLineNumber = getEndLineNumber(detailAST);

		String nextLine = getLine(endLineNumber);

		if (Validator.isNull(nextLine)) {
			return;
		}

		DetailAST nextSiblingDetailAST = detailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(getHiddenBefore(nextSiblingDetailAST) != null) ||
			(nextSiblingDetailAST.getType() == TokenTypes.RCURLY)) {

			return;
		}

		if (variableTypeName.equals(
				getVariableTypeName(detailAST, getName(detailAST), false))) {

			log(
				detailAST, _MSG_MISSING_EMPTY_LINE_AFTER_VARIABLE_DEFINITION,
				variableTypeName);

			return;
		}

		if (nextSiblingDetailAST.getType() == TokenTypes.VARIABLE_DEF) {
			return;
		}

		if (nextSiblingDetailAST.getType() == TokenTypes.EXPR) {
			DetailAST firstChildDetailAST =
				nextSiblingDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.ASSIGN) {
				return;
			}
		}

		log(
			endLineNumber, _MSG_MISSING_EMPTY_LINE_LINE_NUMBER, "after",
			endLineNumber);
	}

	private void _checkMissingEmptyLineBeforeAssign(DetailAST assignDetailAST) {
		DetailAST parentDetailAST = assignDetailAST.getParent();

		DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.SEMI) ||
			_hasPrecedingVariableDef(parentDetailAST)) {

			return;
		}

		List<DetailAST> adjacentAssignDetailASTs = _getAdjacentAssignDetailASTs(
			assignDetailAST);

		if (adjacentAssignDetailASTs.size() <= 1) {
			return;
		}

		DetailAST lastAssignDetailAST = adjacentAssignDetailASTs.get(
			adjacentAssignDetailASTs.size() - 1);

		List<DetailAST> identDetailASTs =
			_getFollowingStatementsIdentDetailASTs(
				lastAssignDetailAST.getParent());

		if (_containsVariableName(identDetailASTs, assignDetailAST)) {
			return;
		}

		DetailAST firstReferencedAssignDetailAST = null;

		for (int i = 1; i < adjacentAssignDetailASTs.size(); i++) {
			DetailAST curAssignDetailAST = adjacentAssignDetailASTs.get(i);

			if (_containsVariableName(identDetailASTs, curAssignDetailAST)) {
				if (firstReferencedAssignDetailAST == null) {
					firstReferencedAssignDetailAST = curAssignDetailAST;
				}

				continue;
			}

			if (firstReferencedAssignDetailAST != null) {
				return;
			}
		}

		if (firstReferencedAssignDetailAST != null) {
			String name = getName(firstReferencedAssignDetailAST);

			if (name == null) {
				name = getName(firstReferencedAssignDetailAST.getParent());
			}

			log(
				firstReferencedAssignDetailAST,
				_MSG_MISSING_EMPTY_LINE_BEFORE_VARIABLE_ASSIGN, name);
		}
	}

	private void _checkMissingEmptyLineBeforeMethodCall(
		DetailAST detailAST, String variableName,
		DetailAST previousSiblingDetailAST) {

		if (previousSiblingDetailAST == null) {
			return;
		}

		int startLineNumber = getStartLineNumber(detailAST);

		int previousExpressionEndLineNumber = getEndLineNumber(
			previousSiblingDetailAST);

		if ((previousExpressionEndLineNumber + 1) != startLineNumber) {
			return;
		}

		if (previousSiblingDetailAST.getType() == TokenTypes.EXPR) {
			DetailAST firstChildDetailAST =
				previousSiblingDetailAST.getFirstChild();

			if ((firstChildDetailAST.getType() == TokenTypes.METHOD_CALL) &&
				variableName.equals(getVariableName(firstChildDetailAST))) {

				List<String> enforceEmptyLineBeforeMethodNames =
					getAttributeValues(
						_ENFORCE_EMPTY_LINE_BEFORE_METHOD_NAMES_KEY);

				String methodName = getMethodName(detailAST);
				List<DetailAST> parameterExprDetailASTs =
					getParameterExprDetailASTs(detailAST);

				if (enforceEmptyLineBeforeMethodNames.contains(methodName) &&
					parameterExprDetailASTs.isEmpty()) {

					log(
						startLineNumber,
						_MSG_MISSING_EMPTY_LINE_BEFORE_METHOD_NAME,
						StringBundler.concat(
							variableName, StringPool.PERIOD, methodName));
				}

				return;
			}
		}

		if (containsVariableName(previousSiblingDetailAST, variableName)) {
			log(
				startLineNumber, _MSG_MISSING_EMPTY_LINE_LINE_NUMBER, "before",
				startLineNumber);
		}
	}

	private void _checkMissingEmptyLineBeforeVariableDef(DetailAST detailAST) {
		if (getHiddenBefore(detailAST) != null) {
			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.SLIST) {
			return;
		}

		int startLineNumber = getStartLineNumber(detailAST);

		String previousLine = getLine(startLineNumber - 2);

		if (Validator.isNull(previousLine)) {
			return;
		}

		DetailAST previousSiblingDetailAST = detailAST.getPreviousSibling();

		if ((previousSiblingDetailAST == null) ||
			(previousSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		previousSiblingDetailAST =
			previousSiblingDetailAST.getPreviousSibling();

		if (previousSiblingDetailAST.getType() == TokenTypes.VARIABLE_DEF) {
			return;
		}

		if (previousSiblingDetailAST.getType() == TokenTypes.EXPR) {
			DetailAST firstChildDetailAST =
				previousSiblingDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.ASSIGN) {
				return;
			}
		}

		log(
			startLineNumber, _MSG_MISSING_EMPTY_LINE_LINE_NUMBER, "before",
			startLineNumber);
	}

	private void _checkMissingEmptyLineBetweenAssigningAndUsingVariable(
		DetailAST detailAST, String name, int endLineNumber) {

		DetailAST nextSiblingDetailAST = detailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

		if (nextSiblingDetailAST == null) {
			return;
		}

		int nextExpressionStartLineNumber = getStartLineNumber(
			nextSiblingDetailAST);

		if ((endLineNumber + 1) != nextExpressionStartLineNumber) {
			return;
		}

		List<DetailAST> identDetailASTs = getAllChildTokens(
			nextSiblingDetailAST, true, TokenTypes.IDENT);

		boolean nextVariableUsesVariable = false;

		for (DetailAST identDetailAST : identDetailASTs) {
			String identName = identDetailAST.getText();

			if (!identName.equals(name)) {
				continue;
			}

			if (detailAST.getType() == TokenTypes.VARIABLE_DEF) {
				log(
					nextExpressionStartLineNumber,
					_MSG_MISSING_EMPTY_LINE_BEFORE_VARIABLE_USE, name);

				return;
			}

			DetailAST parentDetailAST = identDetailAST.getParent();

			if ((parentDetailAST.getType() == TokenTypes.ASSIGN) &&
				(identDetailAST.getPreviousSibling() == null)) {

				return;
			}

			nextVariableUsesVariable = true;
		}

		if (nextVariableUsesVariable) {
			log(
				nextExpressionStartLineNumber,
				_MSG_MISSING_EMPTY_LINE_BEFORE_VARIABLE_USE, name);
		}
	}

	private void _checkMissingEmptyLineInInstanceInit(DetailAST detailAST) {
		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.SLIST)) {

			return;
		}

		List<DetailAST> exprDetailASTs = getAllChildTokens(
			firstChildDetailAST, false, TokenTypes.EXPR);

		if (exprDetailASTs.size() < 2) {
			return;
		}

		DetailAST previousExprDetailAST = null;

		for (DetailAST exprDetailAST : exprDetailASTs) {
			if (previousExprDetailAST == null) {
				previousExprDetailAST = exprDetailAST;

				continue;
			}

			firstChildDetailAST = exprDetailAST.getFirstChild();
			DetailAST previousExprFirstChildDetailAST =
				previousExprDetailAST.getFirstChild();

			if ((firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) ||
				(previousExprFirstChildDetailAST.getType() !=
					TokenTypes.ASSIGN)) {

				previousExprDetailAST = exprDetailAST;

				continue;
			}

			int previousExprEndLineNo = getEndLineNumber(previousExprDetailAST);

			if ((previousExprEndLineNo + 1) == exprDetailAST.getLineNo()) {
				log(
					previousExprFirstChildDetailAST,
					_MSG_MISSING_EMPTY_LINE_LINE_NUMBER, "after",
					previousExprEndLineNo);
			}

			previousExprDetailAST = exprDetailAST;
		}
	}

	private void _checkMissingEmptyLinesAfterChaining(DetailAST detailAST) {
		DetailAST dotDetailAST = detailAST.findFirstToken(TokenTypes.DOT);

		if (dotDetailAST != null) {
			List<DetailAST> childMethodCallDetailASTs = getAllChildTokens(
				dotDetailAST, false, TokenTypes.METHOD_CALL);

			if (!childMethodCallDetailASTs.isEmpty()) {
				return;
			}
		}

		ChainInformation chainInformation = getChainInformation(detailAST);

		List<String> chainedMethodNames = chainInformation.getMethodNames();

		int chainSize = chainedMethodNames.size();

		if (chainSize == 1) {
			return;
		}

		DetailAST topLevelMethodCallDetailAST = getTopLevelMethodCallDetailAST(
			detailAST);

		DetailAST parentDetailAST = topLevelMethodCallDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.EXPR)) {

			return;
		}

		DetailAST nextMethodCallDetailAST =
			nextSiblingDetailAST.getFirstChild();

		if (nextMethodCallDetailAST.getType() != TokenTypes.METHOD_CALL) {
			return;
		}

		int endLineNumber = getEndLineNumber(detailAST);

		int nextExpressionStartLineNumber = getStartLineNumber(
			nextSiblingDetailAST);

		if ((endLineNumber + 1) != nextExpressionStartLineNumber) {
			return;
		}

		log(
			endLineNumber, _MSG_MISSING_EMPTY_LINE_LINE_NUMBER, "after",
			endLineNumber);
	}

	private void _checkMissingEmptyLinesAroundMethodCall(DetailAST detailAST) {
		_checkMissingEmptyLinesAfterChaining(detailAST);

		String variableName = getVariableName(detailAST);

		if ((variableName == null) ||
			Character.isUpperCase(variableName.charAt(0))) {

			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		DetailAST nextSiblingDetailAST = parentDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		_checkMissingEmptyLineAfterMethodCall(
			detailAST, variableName, nextSiblingDetailAST.getNextSibling());

		DetailAST previousSiblingDetailAST =
			parentDetailAST.getPreviousSibling();

		if ((previousSiblingDetailAST == null) ||
			(previousSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		_checkMissingEmptyLineBeforeMethodCall(
			detailAST, variableName,
			previousSiblingDetailAST.getPreviousSibling());
	}

	private boolean _containsVariableName(
		List<DetailAST> identDetailASTs, DetailAST assignDetailAST) {

		String variableName = _getVariableName(assignDetailAST);

		if (variableName == null) {
			return false;
		}

		return containsVariableName(identDetailASTs, variableName);
	}

	private List<DetailAST> _getAdjacentAssignDetailASTs(
		DetailAST assignDetailAST) {

		List<DetailAST> assignDetailASTs = new ArrayList<>();

		assignDetailASTs.add(assignDetailAST);

		DetailAST followingStatementDetailAST = _getFollowingStatementDetailAST(
			assignDetailAST.getParent(), false);

		while (true) {
			if (followingStatementDetailAST == null) {
				return assignDetailASTs;
			}

			DetailAST followingAssignDetailAST =
				followingStatementDetailAST.findFirstToken(TokenTypes.ASSIGN);

			if (followingAssignDetailAST == null) {
				return assignDetailASTs;
			}

			assignDetailASTs.add(followingAssignDetailAST);

			followingStatementDetailAST = _getFollowingStatementDetailAST(
				followingStatementDetailAST, false);
		}
	}

	private DetailAST _getFollowingStatementDetailAST(
		DetailAST detailAST, boolean allowDividingEmptyLine) {

		int endLineNumber = getEndLineNumber(detailAST);

		DetailAST nextSiblingDetailAST = detailAST.getNextSibling();

		while (true) {
			if (nextSiblingDetailAST == null) {
				return null;
			}

			int nextStartLineNumber = getStartLineNumber(nextSiblingDetailAST);

			if (nextStartLineNumber <= endLineNumber) {
				nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

				continue;
			}

			if (nextStartLineNumber == (endLineNumber + 1)) {
				return nextSiblingDetailAST;
			}

			if (hasPrecedingPlaceholder(nextSiblingDetailAST)) {
				return null;
			}

			if (allowDividingEmptyLine) {
				return nextSiblingDetailAST;
			}

			return null;
		}
	}

	private List<DetailAST> _getFollowingStatementsIdentDetailASTs(
		DetailAST detailAST) {

		List<DetailAST> identDetailASTs = new ArrayList<>();

		DetailAST followingStatementDetailAST = _getFollowingStatementDetailAST(
			detailAST, true);

		while (followingStatementDetailAST != null) {
			identDetailASTs.addAll(
				getAllChildTokens(
					followingStatementDetailAST, true, TokenTypes.IDENT));

			followingStatementDetailAST = _getFollowingStatementDetailAST(
				followingStatementDetailAST, false);
		}

		return identDetailASTs;
	}

	private String _getVariableName(DetailAST assignDetailAST) {
		DetailAST parentDetailAST = assignDetailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.EXPR) {
			DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
				return firstChildDetailAST.getText();
			}
		}
		else if (parentDetailAST.getType() == TokenTypes.VARIABLE_DEF) {
			DetailAST nameDetailAST = parentDetailAST.findFirstToken(
				TokenTypes.IDENT);

			return nameDetailAST.getText();
		}

		return null;
	}

	private boolean _hasPrecedingVariableDef(
		DetailAST variableDefinitionDetailAST) {

		DetailAST previousSiblingDetailAST =
			variableDefinitionDetailAST.getPreviousSibling();

		if ((previousSiblingDetailAST == null) ||
			(previousSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return false;
		}

		previousSiblingDetailAST =
			previousSiblingDetailAST.getPreviousSibling();

		if (previousSiblingDetailAST.getType() != TokenTypes.VARIABLE_DEF) {
			return false;
		}

		if ((getEndLineNumber(previousSiblingDetailAST) + 1) ==
				getStartLineNumber(variableDefinitionDetailAST)) {

			return true;
		}

		return false;
	}

	private static final String _ENFORCE_EMPTY_LINE_AFTER_METHOD_NAMES_KEY =
		"enforceEmptyLineAfterMethodNames";

	private static final String _ENFORCE_EMPTY_LINE_BEFORE_METHOD_NAMES_KEY =
		"enforceEmptyLineBeforeMethodNames";

	private static final String _MSG_MISSING_EMPTY_LINE_AFTER_METHOD_NAME =
		"empty.line.missing.after.method.name";

	private static final String
		_MSG_MISSING_EMPTY_LINE_AFTER_VARIABLE_DEFINITION =
			"empty.line.missing.after.variable.definition";

	private static final String
		_MSG_MISSING_EMPTY_LINE_AFTER_VARIABLE_REFERENCE =
			"empty.line.missing.after.variable.reference";

	private static final String _MSG_MISSING_EMPTY_LINE_BEFORE_METHOD_NAME =
		"empty.line.missing.before.method.name";

	private static final String _MSG_MISSING_EMPTY_LINE_BEFORE_VARIABLE_ASSIGN =
		"empty.line.missing.before.variable.assign";

	private static final String _MSG_MISSING_EMPTY_LINE_BEFORE_VARIABLE_USE =
		"empty.line.missing.before.variable.use";

	private static final String _MSG_MISSING_EMPTY_LINE_LINE_NUMBER =
		"empty.line.missing.line.number";

}