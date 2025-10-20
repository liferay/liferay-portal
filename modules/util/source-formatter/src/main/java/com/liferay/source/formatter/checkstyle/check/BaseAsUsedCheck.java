/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public abstract class BaseAsUsedCheck extends BaseCheck {

	protected void checkInline(
		DetailAST assignDetailAST, String variableName,
		DetailAST identDetailAST, List<DetailAST> dependentIdentDetailASTs) {

		if (!variableName.equals(identDetailAST.getText())) {
			return;
		}

		DetailAST assignExpressionDetailAST = _getAssignExpressionDetailAST(
			assignDetailAST);

		if (assignExpressionDetailAST == null) {
			return;
		}

		String assignExpressionType = null;
		boolean chainStyle = false;

		if (assignExpressionDetailAST.getType() == TokenTypes.METHOD_CALL) {
			if (_hasChainStyle(
					assignExpressionDetailAST, "build", "create.*", "map",
					"put")) {

				if (_isInsideStatementClause(identDetailAST)) {
					return;
				}

				chainStyle = true;
			}
			else {
				if (((getStartLineNumber(assignExpressionDetailAST) !=
						getEndLineNumber(assignExpressionDetailAST)) &&
					 (hasParentWithTokenType(
						 identDetailAST, ARITHMETIC_OPERATOR_TOKEN_TYPES) ||
					  hasParentWithTokenType(
						  identDetailAST, RELATIONAL_OPERATOR_TOKEN_TYPES)) &&
					 hasParentWithTokenType(
						 identDetailAST, TokenTypes.METHOD_CALL)) ||
					(_isInsideStatementClause(identDetailAST) &&
					 hasParentWithTokenType(
						 identDetailAST, RELATIONAL_OPERATOR_TOKEN_TYPES))) {

					return;
				}

				if (!_matchesGetOrSetCall(
						assignExpressionDetailAST, identDetailAST,
						variableName)) {

					return;
				}
			}

			assignExpressionType = "method call";
		}
		else if (assignExpressionDetailAST.getType() ==
					TokenTypes.LITERAL_NEW) {

			DetailAST objBlockDetailAST =
				assignExpressionDetailAST.findFirstToken(TokenTypes.OBJBLOCK);

			if (objBlockDetailAST != null) {
				return;
			}

			List<DetailAST> typeArgumentsDetailASTs = null;

			DetailAST parentDetailAST = assignDetailAST.getParent();

			if (parentDetailAST.getType() == TokenTypes.VARIABLE_DEF) {
				typeArgumentsDetailASTs = getAllChildTokens(
					parentDetailAST, true, TokenTypes.TYPE_ARGUMENTS);
			}
			else {
				typeArgumentsDetailASTs = getAllChildTokens(
					assignDetailAST, true, TokenTypes.TYPE_ARGUMENTS);
			}

			if (!typeArgumentsDetailASTs.isEmpty()) {
				return;
			}

			DetailAST elistDetailAST = assignExpressionDetailAST.findFirstToken(
				TokenTypes.ELIST);

			if ((elistDetailAST == null) ||
				(elistDetailAST.getChildCount() > 0)) {

				return;
			}

			assignExpressionType = "new instance";
		}
		else {
			return;
		}

		if (_isInsideMockitoMethodCall(identDetailAST)) {
			return;
		}

		DetailAST parentDetailAST = identDetailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.LNOT) {
			parentDetailAST = parentDetailAST.getParent();
		}

		boolean toString = false;

		if (parentDetailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(parentDetailAST);

			if (!Objects.equals(
					fullIdent.getText(), variableName + ".toString")) {

				return;
			}

			toString = true;
		}
		else if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		int endLineNumber = getEndLineNumber(assignDetailAST);

		for (DetailAST dependentIdentDetailAST : dependentIdentDetailASTs) {
			if (variableName.equals(dependentIdentDetailAST.getText()) &&
				!equals(dependentIdentDetailAST, identDetailAST) &&
				(dependentIdentDetailAST.getLineNo() > endLineNumber)) {

				return;
			}
		}

		parentDetailAST = getParentWithTokenType(
			identDetailAST, TokenTypes.LAMBDA, TokenTypes.LITERAL_DO,
			TokenTypes.LITERAL_FOR, TokenTypes.LITERAL_NEW,
			TokenTypes.LITERAL_SYNCHRONIZED, TokenTypes.LITERAL_TRY,
			TokenTypes.LITERAL_WHILE);

		if ((parentDetailAST != null) &&
			(parentDetailAST.getLineNo() >= assignDetailAST.getLineNo())) {

			return;
		}

		int emptyLineCount = 0;

		for (int i = endLineNumber; i <= identDetailAST.getLineNo(); i++) {
			if (Validator.isNull(getLine(i - 1))) {
				emptyLineCount++;

				if (emptyLineCount > 1) {
					return;
				}
			}
		}

		if (!toString) {
			log(
				assignDetailAST, _MSG_INLINE_VARIABLE, variableName,
				assignExpressionType, identDetailAST.getLineNo());
		}
		else if (chainStyle) {
			String variableTypeName = getVariableTypeName(
				identDetailAST, variableName, false);

			if (variableTypeName.equals("JSONArray") ||
				variableTypeName.equals("JSONObject")) {

				log(
					identDetailAST, _MSG_ADD_TO_STRING,
					assignDetailAST.getLineNo());
			}
		}
		else {
			log(
				identDetailAST, _MSG_USE_STRING_VALUE_OF,
				assignDetailAST.getLineNo(), identDetailAST.getLineNo());
		}
	}

	protected void checkMoveAfterBranchingStatement(
		DetailAST detailAST, DetailAST assignDetailAST, String variableName,
		DetailAST firstDependentIdentDetailAST, int actionLineNumber) {

		int endLineNumber = getEndLineNumber(assignDetailAST);

		DetailAST lastBranchingStatementDetailAST =
			_getLastBranchingStatementDetailAST(
				detailAST, endLineNumber,
				_getClosestParentLineNumber(
					firstDependentIdentDetailAST, endLineNumber));

		if (lastBranchingStatementDetailAST == null) {
			return;
		}

		int lineNumber = lastBranchingStatementDetailAST.getLineNo();

		if ((actionLineNumber == -1) || (actionLineNumber > lineNumber)) {
			log(
				assignDetailAST, _MSG_MOVE_VARIABLE_AFTER_BRANCHING_STATEMENT,
				variableName, lastBranchingStatementDetailAST.getText(),
				lastBranchingStatementDetailAST.getLineNo());
		}
	}

	protected void checkMoveInsideIfStatement(
		DetailAST assignDetailAST, DetailAST nameDetailAST, String variableName,
		DetailAST firstDependentIdentDetailAST,
		DetailAST lastDependentIdentDetailAST, int actionLineNumber) {

		DetailAST elseOrIfStatementDetailAST = _getElseOrIfStatementDetailAST(
			firstDependentIdentDetailAST, getEndLineNumber(assignDetailAST));

		if (elseOrIfStatementDetailAST == null) {
			return;
		}

		DetailAST parentDetailAST = getParentWithTokenType(
			elseOrIfStatementDetailAST, TokenTypes.LAMBDA,
			TokenTypes.LITERAL_DO, TokenTypes.LITERAL_FOR,
			TokenTypes.LITERAL_NEW, TokenTypes.LITERAL_SYNCHRONIZED,
			TokenTypes.LITERAL_TRY, TokenTypes.LITERAL_WHILE);

		if ((parentDetailAST != null) &&
			(parentDetailAST.getLineNo() >= assignDetailAST.getLineNo())) {

			return;
		}

		DetailAST slistDetailAST = elseOrIfStatementDetailAST.findFirstToken(
			TokenTypes.SLIST);

		if (getEndLineNumber(slistDetailAST) <=
				lastDependentIdentDetailAST.getLineNo()) {

			return;
		}

		if (actionLineNumber != -1) {
			parentDetailAST = elseOrIfStatementDetailAST;

			while (true) {
				DetailAST grandParentDetailAST = parentDetailAST.getParent();

				if ((grandParentDetailAST.getType() ==
						TokenTypes.LITERAL_ELSE) ||
					(grandParentDetailAST.getType() == TokenTypes.LITERAL_IF)) {

					parentDetailAST = grandParentDetailAST;

					continue;
				}

				if (actionLineNumber < parentDetailAST.getLineNo()) {
					return;
				}

				break;
			}
		}

		if (elseOrIfStatementDetailAST.getType() == TokenTypes.LITERAL_ELSE) {
			log(
				nameDetailAST, _MSG_MOVE_VARIABLE_INSIDE_IF_STATEMENT,
				variableName, "else", elseOrIfStatementDetailAST.getLineNo());
		}
		else {
			parentDetailAST = elseOrIfStatementDetailAST.getParent();

			if (parentDetailAST.getType() == TokenTypes.LITERAL_ELSE) {
				log(
					nameDetailAST, _MSG_MOVE_VARIABLE_INSIDE_IF_STATEMENT,
					variableName, "else if",
					elseOrIfStatementDetailAST.getLineNo());
			}
			else {
				log(
					nameDetailAST, _MSG_MOVE_VARIABLE_INSIDE_IF_STATEMENT,
					variableName, "if", elseOrIfStatementDetailAST.getLineNo());
			}
		}
	}

	protected int getActionLineNumber(DetailAST detailAST) {
		String actionNameRegex = StringBundler.concat(
			"_?(re|un)?(add|calculate|channel|close|copy|create|decode|delete|",
			"encode|execute|finish|import|increment|manage|next|open|post|put|",
			"read|register|resolve|run|send|set|start|stop|test|transform|",
			"update|upsert|zip)([A-Z].*)?");

		if (_containsMethodName(
				detailAST, actionNameRegex, "currentTimeMillis",
				"getCurrentTimeMillis", "nextVersion", "toString") ||
			_containsVariableType(detailAST, "ActionQueue", "File")) {

			return detailAST.getLineNo();
		}

		if (detailAST.getType() != TokenTypes.VARIABLE_DEF) {
			return -1;
		}

		List<DetailAST> dependentIdentDetailASTs = getDependentIdentDetailASTs(
			detailAST, detailAST.getLineNo(), true);

		for (DetailAST dependentIdentDetailAST : dependentIdentDetailASTs) {
			DetailAST elistDetailAST = getParentWithTokenType(
				dependentIdentDetailAST, TokenTypes.ELIST);

			if (elistDetailAST == null) {
				continue;
			}

			DetailAST parentDetailAST = elistDetailAST.getParent();

			if (parentDetailAST.getType() == TokenTypes.METHOD_CALL) {
				String methodName = getMethodName(parentDetailAST);

				if (methodName.matches(actionNameRegex)) {
					return dependentIdentDetailAST.getLineNo();
				}
			}
		}

		return -1;
	}

	private boolean _containsMethodName(
		DetailAST variableDefinitionDetailAST, String... methodNameRegexArray) {

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			variableDefinitionDetailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			String methodName = getMethodName(methodCallDetailAST);

			for (String methodNameRegex : methodNameRegexArray) {
				if (methodName.matches(methodNameRegex)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean _containsVariableType(
		DetailAST variableDefinitionDetailAST, String... variableTypeNames) {

		List<DetailAST> identDetailASTs = getAllChildTokens(
			variableDefinitionDetailAST, true, TokenTypes.IDENT);

		for (DetailAST identDetailAST : identDetailASTs) {
			if (ArrayUtil.contains(
					variableTypeNames,
					getVariableTypeName(
						identDetailAST, identDetailAST.getText(), false))) {

				return true;
			}
		}

		return false;
	}

	private DetailAST _getAssignExpressionDetailAST(DetailAST assignDetailAST) {
		DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

		if (firstChildDetailAST == null) {
			return null;
		}

		if (firstChildDetailAST.getType() == TokenTypes.EXPR) {
			return firstChildDetailAST.getFirstChild();
		}

		if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			return firstChildDetailAST.getNextSibling();
		}

		return null;
	}

	private int _getClosestParentLineNumber(
		DetailAST firstNameDetailAST, int lineNumber) {

		int closestLineNumber = firstNameDetailAST.getLineNo();

		DetailAST parentDetailAST = firstNameDetailAST.getParent();

		while (true) {
			if (parentDetailAST.getLineNo() <= lineNumber) {
				return closestLineNumber;
			}

			closestLineNumber = parentDetailAST.getLineNo();

			parentDetailAST = parentDetailAST.getParent();
		}
	}

	private DetailAST _getElseOrIfStatementDetailAST(
		DetailAST detailAST, int lineNumber) {

		DetailAST elseOrIfStatementDetailAST = null;

		DetailAST slistDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.SLIST);

		while (true) {
			if ((slistDetailAST == null) ||
				(slistDetailAST.getLineNo() < lineNumber)) {

				return elseOrIfStatementDetailAST;
			}

			DetailAST parentDetailAST = slistDetailAST.getParent();

			if ((parentDetailAST.getType() == TokenTypes.LITERAL_ELSE) ||
				(parentDetailAST.getType() == TokenTypes.LITERAL_IF)) {

				elseOrIfStatementDetailAST = parentDetailAST;
			}

			slistDetailAST = getParentWithTokenType(
				slistDetailAST, TokenTypes.SLIST);
		}
	}

	private DetailAST _getLastBranchingStatementDetailAST(
		DetailAST detailAST, int start, int end) {

		DetailAST lastBranchingStatementDetailAST = null;

		List<DetailAST> branchingStatementDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.LITERAL_BREAK,
			TokenTypes.LITERAL_CONTINUE, TokenTypes.LITERAL_RETURN,
			TokenTypes.LITERAL_THROW);

		for (DetailAST branchingStatementDetailAST :
				branchingStatementDetailASTs) {

			int lineNumber = getEndLineNumber(branchingStatementDetailAST);

			if ((start >= lineNumber) || (end <= lineNumber)) {
				continue;
			}

			DetailAST branchedStatementDetailAST = null;

			if ((branchingStatementDetailAST.getType() ==
					TokenTypes.LITERAL_BREAK) ||
				(branchingStatementDetailAST.getType() ==
					TokenTypes.LITERAL_CONTINUE)) {

				branchedStatementDetailAST = getParentWithTokenType(
					branchingStatementDetailAST, TokenTypes.LITERAL_DO,
					TokenTypes.LITERAL_FOR, TokenTypes.LITERAL_WHILE);
			}
			else {
				branchedStatementDetailAST = getParentWithTokenType(
					branchingStatementDetailAST, TokenTypes.CTOR_DEF,
					TokenTypes.LAMBDA, TokenTypes.METHOD_DEF);
			}

			if ((branchedStatementDetailAST != null) &&
				(branchedStatementDetailAST.getLineNo() < start) &&
				((lastBranchingStatementDetailAST == null) ||
				 (branchingStatementDetailAST.getLineNo() >
					 lastBranchingStatementDetailAST.getLineNo()))) {

				lastBranchingStatementDetailAST = branchingStatementDetailAST;
			}
		}

		return lastBranchingStatementDetailAST;
	}

	private boolean _hasChainStyle(
		DetailAST methodCallDetailAST, String... methodNameRegexArray) {

		int startLineNumber = getStartLineNumber(methodCallDetailAST);

		String line = getLine(startLineNumber - 1);

		if (!line.endsWith("(") || (ToolsUtil.getLevel(line) != 1)) {
			return false;
		}

		for (String methodNameRegex : methodNameRegexArray) {
			if (!line.matches(".*[\\.>]" + methodNameRegex + "\\(")) {
				continue;
			}

			int level = 1;

			for (int i = startLineNumber + 1;
				 i <= getEndLineNumber(methodCallDetailAST); i++) {

				line = StringUtil.trim(getLine(i - 1));

				if (line.startsWith(").") && (level == 1)) {
					return true;
				}

				level += ToolsUtil.getLevel(line);
			}
		}

		return false;
	}

	private boolean _isInsideMockitoMethodCall(DetailAST detailAST) {
		DetailAST methodCallDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.METHOD_CALL);

		if (methodCallDetailAST == null) {
			return false;
		}

		for (String name : getNames(methodCallDetailAST, true)) {
			if (Objects.equals(name, "Mockito")) {
				return true;
			}
		}

		return false;
	}

	private boolean _isInsideStatementClause(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		while (true) {
			DetailAST grandParentDetailAST = parentDetailAST.getParent();

			if (grandParentDetailAST == null) {
				return false;
			}

			if (grandParentDetailAST.getType() == TokenTypes.LITERAL_FOR) {
				if ((parentDetailAST.getType() == TokenTypes.FOR_CONDITION) ||
					(parentDetailAST.getType() == TokenTypes.FOR_EACH_CLAUSE) ||
					(parentDetailAST.getType() == TokenTypes.FOR_INIT) ||
					(parentDetailAST.getType() == TokenTypes.FOR_ITERATOR)) {

					return true;
				}

				return false;
			}

			if (grandParentDetailAST.getType() == TokenTypes.LITERAL_TRY) {
				if (parentDetailAST.getType() ==
						TokenTypes.RESOURCE_SPECIFICATION) {

					return true;
				}

				return false;
			}

			if (grandParentDetailAST.getType() == TokenTypes.LITERAL_WHILE) {
				if (parentDetailAST.getType() == TokenTypes.EXPR) {
					return true;
				}

				return false;
			}

			parentDetailAST = grandParentDetailAST;
		}
	}

	private boolean _matchesGetOrSetCall(
		DetailAST assignMethodCallDetailAST, DetailAST identDetailAST,
		String variableName) {

		String methodName = getMethodName(assignMethodCallDetailAST);

		if (methodName.matches("(?i)_?get" + variableName)) {
			return true;
		}

		DetailAST parentDetailAST = identDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return false;
		}

		parentDetailAST = parentDetailAST.getParent();

		if ((parentDetailAST.getType() != TokenTypes.ELIST) ||
			(parentDetailAST.getChildCount() != 1)) {

			return false;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.METHOD_CALL) {
			return false;
		}

		methodName = getMethodName(parentDetailAST);

		return methodName.matches("(?i)_?set" + variableName);
	}

	private static final String _MSG_ADD_TO_STRING = "to.string.add";

	private static final String _MSG_INLINE_VARIABLE = "variable.inline";

	private static final String _MSG_MOVE_VARIABLE_AFTER_BRANCHING_STATEMENT =
		"variable.move.after.branching.statement";

	private static final String _MSG_MOVE_VARIABLE_INSIDE_IF_STATEMENT =
		"variable.move.inside.if.statement";

	private static final String _MSG_USE_STRING_VALUE_OF =
		"string.value.of.use";

}