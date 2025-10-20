/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public class EqualClauseIfStatementsCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_IF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST nextSiblingDetailAST = detailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.LITERAL_IF)) {

			return;
		}

		DetailAST literalElseDetailAST1 = detailAST.findFirstToken(
			TokenTypes.LITERAL_ELSE);
		DetailAST literalElseDetailAST2 = nextSiblingDetailAST.findFirstToken(
			TokenTypes.LITERAL_ELSE);

		if ((literalElseDetailAST1 != null) ||
			(literalElseDetailAST2 != null)) {

			return;
		}

		DetailAST clauseExprDetailAST1 = detailAST.findFirstToken(
			TokenTypes.EXPR);
		DetailAST clauseExprDetailAST2 = nextSiblingDetailAST.findFirstToken(
			TokenTypes.EXPR);

		if (!_equals(clauseExprDetailAST1, clauseExprDetailAST2)) {
			return;
		}

		List<String> namesList = getNames(clauseExprDetailAST1, true);

		DetailAST slistDetailAST1 = detailAST.findFirstToken(TokenTypes.SLIST);

		if (!_hasValueChangeOperation(namesList, slistDetailAST1)) {
			log(detailAST, _MSG_COMBINE_IF_STATEMENTS);
		}
	}

	private boolean _equals(DetailAST detailAST1, DetailAST detailAST2) {
		if ((detailAST1 == null) && (detailAST2 == null)) {
			return true;
		}

		if ((detailAST1 == null) || (detailAST2 == null) ||
			(detailAST1.getType() != detailAST2.getType()) ||
			!Objects.equals(detailAST1.getText(), detailAST2.getText())) {

			return false;
		}

		DetailAST childDetailAST1 = detailAST1.getFirstChild();
		DetailAST childDetailAST2 = detailAST2.getFirstChild();

		while (true) {
			if (!_equals(childDetailAST1, childDetailAST2)) {
				return false;
			}

			if (childDetailAST1 == null) {
				return true;
			}

			childDetailAST1 = childDetailAST1.getNextSibling();
			childDetailAST2 = childDetailAST2.getNextSibling();
		}
	}

	private boolean _hasValueChangeOperation(
		List<String> namesList, DetailAST slistDetailAST) {

		if (namesList.isEmpty() || (slistDetailAST == null)) {
			return false;
		}

		List<DetailAST> assignDetailASTs = getAllChildTokens(
			slistDetailAST, true, ASSIGNMENT_OPERATOR_TOKEN_TYPES);

		for (DetailAST assignDetailAST : assignDetailASTs) {
			String name = getName(assignDetailAST);

			if (name == null) {
				continue;
			}

			if (namesList.contains(name)) {
				return true;
			}
		}

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			slistDetailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST firstChildDetailAST = methodCallDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
				String methodName = firstChildDetailAST.getText();

				DetailAST parentDetailAST = methodCallDetailAST.getParent();

				while (parentDetailAST.getParent() != null) {
					parentDetailAST = parentDetailAST.getParent();
				}

				List<DetailAST> methodDefinitionDetailASTs = getAllChildTokens(
					parentDetailAST, true, TokenTypes.METHOD_DEF);

				for (DetailAST methodDefinitionDetailAST :
						methodDefinitionDetailASTs) {

					String name = getName(methodDefinitionDetailAST);

					if ((name != null) && methodName.equals(name) &&
						_hasValueChangeOperation(
							namesList,
							methodDefinitionDetailAST.findFirstToken(
								TokenTypes.SLIST))) {

						return true;
					}
				}
			}

			if (firstChildDetailAST.getType() != TokenTypes.DOT) {
				continue;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if ((firstChildDetailAST.getType() != TokenTypes.IDENT) ||
				!namesList.contains(firstChildDetailAST.getText())) {

				continue;
			}

			DetailAST nextSiblingDetailAST =
				firstChildDetailAST.getNextSibling();

			if (nextSiblingDetailAST.getType() == TokenTypes.IDENT) {
				String methodName = nextSiblingDetailAST.getText();

				if (methodName.matches("(add|next|put|set).*")) {
					return true;
				}
			}
		}

		return false;
	}

	private static final String _MSG_COMBINE_IF_STATEMENTS =
		"if.statements.combine";

}