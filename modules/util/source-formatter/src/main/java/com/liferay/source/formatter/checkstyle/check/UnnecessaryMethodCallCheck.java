/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alan Huang
 */
public class UnnecessaryMethodCallCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		Map<String, String> returnVariableNamesMap = _getReturnVariableNamesMap(
			detailAST);

		if (returnVariableNamesMap.isEmpty()) {
			return;
		}

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST != null) {
				continue;
			}

			DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.ELIST);

			List<DetailAST> exprDetailASTs = getAllChildTokens(
				elistDetailAST, false, TokenTypes.EXPR);

			if (!exprDetailASTs.isEmpty()) {
				continue;
			}

			String methodName = getName(methodCallDetailAST);

			if (!returnVariableNamesMap.containsKey(methodName)) {
				continue;
			}

			DetailAST parentDetailAST = methodCallDetailAST.getParent();

			while (parentDetailAST.getType() != TokenTypes.CLASS_DEF) {
				parentDetailAST = parentDetailAST.getParent();
			}

			if (parentDetailAST.equals(detailAST)) {
				String replacementValue = _getReplacementValue(
					methodCallDetailAST,
					returnVariableNamesMap.get(methodName));

				if (replacementValue != null) {
					log(
						methodCallDetailAST, _MSG_UNNECESSARY_METHOD_CALL,
						replacementValue, methodName);
				}
			}
		}
	}

	private String _getReplacementValue(
		DetailAST methodCallDetailAST, String variableName) {

		DetailAST previousDetailAST = methodCallDetailAST.getParent();

		while ((previousDetailAST.getType() != TokenTypes.METHOD_DEF) &&
			   (previousDetailAST.getType() != TokenTypes.CTOR_DEF)) {

			if ((previousDetailAST.getType() == TokenTypes.VARIABLE_DEF) &&
				(previousDetailAST.branchContains(TokenTypes.LITERAL_PRIVATE) ||
				 previousDetailAST.branchContains(
					 TokenTypes.LITERAL_PROTECTED) ||
				 previousDetailAST.branchContains(TokenTypes.LITERAL_PUBLIC))) {

				break;
			}

			previousDetailAST = previousDetailAST.getParent();
		}

		List<DetailAST> variableDefDetailASTs = getAllChildTokens(
			previousDetailAST, true, TokenTypes.VARIABLE_DEF);

		for (DetailAST variableDefDetailAST : variableDefDetailASTs) {
			DetailAST nameDetailAST = variableDefDetailAST.findFirstToken(
				TokenTypes.IDENT);

			if (Objects.equals(nameDetailAST.getText(), variableName)) {
				DetailAST modifiersDetailAST = previousDetailAST.findFirstToken(
					TokenTypes.MODIFIERS);

				if (modifiersDetailAST.branchContains(
						TokenTypes.LITERAL_STATIC)) {

					return null;
				}

				if (variableDefDetailAST.getLineNo() <=
						methodCallDetailAST.getLineNo()) {

					return "this." + variableName;
				}

				return variableName;
			}
		}

		return variableName;
	}

	private Map<String, String> _getReturnVariableNamesMap(
		DetailAST detailAST) {

		Map<String, String> returnVariableNamesMap = new HashMap<>();

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		if (objBlockDetailAST == null) {
			return returnVariableNamesMap;
		}

		List<DetailAST> methodDefinitionDetailASTs = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.METHOD_DEF);

		for (DetailAST methodDefinitionDetailAST : methodDefinitionDetailASTs) {
			if (!methodDefinitionDetailAST.branchContains(
					TokenTypes.LITERAL_PRIVATE) &&
				!methodDefinitionDetailAST.branchContains(
					TokenTypes.LITERAL_STATIC)) {

				continue;
			}

			List<DetailAST> parameterDefs = getParameterDefs(
				methodDefinitionDetailAST);

			if (!parameterDefs.isEmpty()) {
				continue;
			}

			DetailAST slistDetailAST = methodDefinitionDetailAST.findFirstToken(
				TokenTypes.SLIST);

			if (slistDetailAST == null) {
				continue;
			}

			DetailAST firstChildDetailAST = slistDetailAST.getFirstChild();

			if ((firstChildDetailAST == null) ||
				(firstChildDetailAST.getType() != TokenTypes.LITERAL_RETURN)) {

				continue;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
				continue;
			}

			DetailAST nextSiblingDetailAST =
				firstChildDetailAST.getNextSibling();

			if ((nextSiblingDetailAST == null) ||
				(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

				continue;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
				continue;
			}

			returnVariableNamesMap.put(
				getName(methodDefinitionDetailAST),
				firstChildDetailAST.getText());
		}

		return returnVariableNamesMap;
	}

	private static final String _MSG_UNNECESSARY_METHOD_CALL =
		"method.call.unnecessary";

}