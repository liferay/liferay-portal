/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class ReturnVariableDeclarationAsUsedCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		List<DetailAST> methodDefinitionDetailASTs = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.METHOD_DEF);

		for (DetailAST methodDefinitionDetailAST : methodDefinitionDetailASTs) {
			DetailAST modifiersDetailAST =
				methodDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS);

			if (modifiersDetailAST.branchContains(TokenTypes.ABSTRACT)) {
				continue;
			}

			DetailAST typeDetailAST = methodDefinitionDetailAST.findFirstToken(
				TokenTypes.TYPE);

			String returnTypeName = getTypeName(typeDetailAST, false);

			if (returnTypeName.equals("void")) {
				continue;
			}

			String methodName = getName(methodDefinitionDetailAST);

			if (!methodName.matches("_?(create|fetch|generate|get)[A-Z].+")) {
				continue;
			}

			DetailAST slistDetailAST = methodDefinitionDetailAST.findFirstToken(
				TokenTypes.SLIST);

			if (slistDetailAST == null) {
				continue;
			}

			DetailAST returnIdentDetailAST = _getReturnIdentDetailAST(
				slistDetailAST);

			if (returnIdentDetailAST == null) {
				continue;
			}

			String variableName = returnIdentDetailAST.getText();

			if (!StringUtil.equalsIgnoreCase(
					methodName.replaceFirst(
						"_?(create|fetch|generate|get)(.+)", "$2"),
					variableName)) {

				continue;
			}

			DetailAST returnVariableDefinitionDetailAST =
				getVariableDefinitionDetailAST(
					returnIdentDetailAST, variableName, false);

			if ((returnVariableDefinitionDetailAST == null) ||
				(returnVariableDefinitionDetailAST.getType() ==
					TokenTypes.PARAMETER_DEF)) {

				continue;
			}

			DetailAST firstChildDetailAST = slistDetailAST.getFirstChild();

			if (equals(
					firstChildDetailAST, returnVariableDefinitionDetailAST)) {

				DetailAST nextSiblingDetailAST =
					firstChildDetailAST.getNextSibling();

				if ((nextSiblingDetailAST == null) ||
					(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

					return;
				}

				nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

				if (nextSiblingDetailAST == null) {
					return;
				}

				int startLineNumber = getStartLineNumber(nextSiblingDetailAST);

				int endLineNumber = getEndLineNumber(firstChildDetailAST);

				if ((endLineNumber + 1) == startLineNumber) {
					log(
						startLineNumber,
						_MSG_MISSING_EMPTY_LINE_AFTER_VARIABLE_DEFINITION,
						startLineNumber);
				}

				return;
			}

			_checkMoveVariableDeclaration(
				returnVariableDefinitionDetailAST, slistDetailAST,
				variableName);
		}
	}

	private void _checkMoveVariableDeclaration(
		DetailAST returnVariableDefinitionDetailAST, DetailAST slistDetailAST,
		String variableName) {

		if (_containsMethodCalls(
				slistDetailAST,
				returnVariableDefinitionDetailAST.getLineNo()) ||
			_containsSynchronizedBlocks(
				slistDetailAST,
				returnVariableDefinitionDetailAST.getLineNo()) ||
			_containsUnusedVariableNames(
				slistDetailAST,
				returnVariableDefinitionDetailAST.getLineNo())) {

			return;
		}

		List<DetailAST> childDetailASTs = getAllChildTokens(
			slistDetailAST, true, TokenTypes.LITERAL_RETURN,
			TokenTypes.LITERAL_THROW);

		for (DetailAST childDetailAST : childDetailASTs) {
			if (childDetailAST.getLineNo() <
					returnVariableDefinitionDetailAST.getLineNo()) {

				return;
			}
		}

		DetailAST returnVariableDefinitionAssignDetailAST =
			returnVariableDefinitionDetailAST.findFirstToken(TokenTypes.ASSIGN);

		if (returnVariableDefinitionAssignDetailAST == null) {
			log(
				returnVariableDefinitionDetailAST,
				_MSG_MOVE_VARIABLE_DECLARATION, variableName,
				getStartLineNumber(slistDetailAST.getFirstChild()));

			return;
		}

		DetailAST firstChildDetailAST =
			returnVariableDefinitionAssignDetailAST.getFirstChild();

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if ((firstChildDetailAST.getType() == TokenTypes.LITERAL_NULL) ||
			(firstChildDetailAST.getType() == TokenTypes.NUM_DOUBLE) ||
			(firstChildDetailAST.getType() == TokenTypes.NUM_FLOAT) ||
			(firstChildDetailAST.getType() == TokenTypes.NUM_INT) ||
			(firstChildDetailAST.getType() == TokenTypes.NUM_LONG) ||
			(firstChildDetailAST.getType() == TokenTypes.STRING_LITERAL)) {

			log(
				returnVariableDefinitionDetailAST,
				_MSG_MOVE_VARIABLE_DECLARATION, variableName,
				getStartLineNumber(slistDetailAST.getFirstChild()));

			return;
		}

		if (firstChildDetailAST.getType() == TokenTypes.METHOD_CALL) {
			DetailAST dotDetailAST = firstChildDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST == null) {
				return;
			}

			List<String> names = getNames(dotDetailAST, false);

			if (names.size() != 2) {
				return;
			}

			if (StringUtil.equals(names.get(0), "Collections")) {
				String methodName = names.get(1);

				if (methodName.equals("emptyList") ||
					methodName.equals("emptyMap") ||
					methodName.equals("emptySet")) {

					log(
						returnVariableDefinitionDetailAST,
						_MSG_MOVE_VARIABLE_DECLARATION, variableName,
						getStartLineNumber(slistDetailAST.getFirstChild()));

					return;
				}
			}
		}

		if (firstChildDetailAST.getType() != TokenTypes.LITERAL_NEW) {
			return;
		}

		DetailAST elistDetailAST = firstChildDetailAST.findFirstToken(
			TokenTypes.ELIST);

		if ((elistDetailAST == null) || (elistDetailAST.getChildCount() != 0)) {
			return;
		}

		DetailAST objBlockDetailAST = firstChildDetailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		if (objBlockDetailAST != null) {
			return;
		}

		log(
			returnVariableDefinitionDetailAST, _MSG_MOVE_VARIABLE_DECLARATION,
			variableName, getStartLineNumber(slistDetailAST.getFirstChild()));
	}

	private boolean _containsMethodCalls(
		DetailAST slistDetailAST, int lineNumber) {

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			slistDetailAST, true, TokenTypes.METHOD_CALL);

		methodCallDetailASTs = ListUtil.filter(
			methodCallDetailASTs,
			methodCallDetailAST -> {
				if (methodCallDetailAST.getLineNo() > lineNumber) {
					return false;
				}

				DetailAST parentDetailAST = methodCallDetailAST.getParent();

				if (parentDetailAST.getType() != TokenTypes.EXPR) {
					return false;
				}

				parentDetailAST = parentDetailAST.getParent();

				if (parentDetailAST.getType() != TokenTypes.SLIST) {
					return false;
				}

				String variableName = getVariableName(methodCallDetailAST);

				if (variableName == null) {
					return true;
				}

				if (Character.isUpperCase(variableName.charAt(0))) {
					return false;
				}

				DetailAST typeDetailAST = getVariableTypeDetailAST(
					methodCallDetailAST, variableName, false);

				if (typeDetailAST == null) {
					return true;
				}

				return false;
			});

		return !methodCallDetailASTs.isEmpty();
	}

	private boolean _containsSynchronizedBlocks(
		DetailAST slistDetailAST, int lineNumber) {

		List<DetailAST> literalSynchronizedDetailASTs = getAllChildTokens(
			slistDetailAST, false, TokenTypes.LITERAL_SYNCHRONIZED);

		if (literalSynchronizedDetailASTs.isEmpty()) {
			return false;
		}

		DetailAST literalSynchronizedDetailAST =
			literalSynchronizedDetailASTs.get(0);

		if (literalSynchronizedDetailAST.getLineNo() < lineNumber) {
			return true;
		}

		return false;
	}

	private boolean _containsUnusedVariableNames(
		DetailAST slistDetailAST, int lineNumber) {

		List<DetailAST> assignDetailASTs = getAllChildTokens(
			slistDetailAST, true, TokenTypes.ASSIGN);

		assignDetailASTs = ListUtil.filter(
			assignDetailASTs,
			assignDetailAST -> {
				if (assignDetailAST.getLineNo() > lineNumber) {
					return false;
				}

				DetailAST parentDetailAST = assignDetailAST.getParent();

				if (parentDetailAST.getType() == TokenTypes.EXPR) {
					return true;
				}

				return false;
			});

		if (assignDetailASTs.isEmpty()) {
			return false;
		}

		List<DetailAST> identDetailASTs = getAllChildTokens(
			slistDetailAST, true, TokenTypes.IDENT);

		outerLoop:
		for (DetailAST assignDetailAST : assignDetailASTs) {
			DetailAST nameDetailAST = assignDetailAST.getFirstChild();

			if (nameDetailAST.getType() != TokenTypes.IDENT) {
				continue;
			}

			for (DetailAST identDetailAST : identDetailASTs) {
				if (equals(nameDetailAST, identDetailAST) ||
					isMethodNameDetailAST(identDetailAST)) {

					continue;
				}

				if (StringUtil.equals(
						nameDetailAST.getText(), identDetailAST.getText())) {

					continue outerLoop;
				}
			}

			return true;
		}

		return false;
	}

	private DetailAST _getReturnIdentDetailAST(DetailAST detailAST) {
		DetailAST lastChildDetailAST = detailAST.getLastChild();

		DetailAST previousSiblingDetailAST =
			lastChildDetailAST.getPreviousSibling();

		if ((previousSiblingDetailAST == null) ||
			(previousSiblingDetailAST.getType() != TokenTypes.LITERAL_RETURN)) {

			return null;
		}

		DetailAST firstChildDetailAST =
			previousSiblingDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
			return null;
		}

		firstChildDetailAST = firstChildDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.IDENT)) {

			return null;
		}

		return firstChildDetailAST;
	}

	private static final String
		_MSG_MISSING_EMPTY_LINE_AFTER_VARIABLE_DEFINITION =
			"empty.line.missing.after.variable.definition";

	private static final String _MSG_MOVE_VARIABLE_DECLARATION =
		"variable.declaration.move";

}