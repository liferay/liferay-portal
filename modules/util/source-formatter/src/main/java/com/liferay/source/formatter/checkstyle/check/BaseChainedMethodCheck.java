/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public abstract class BaseChainedMethodCheck extends BaseCheck {

	protected boolean containsVariableName(
		DetailAST detailAST, String variableName, boolean allowGetters) {

		List<DetailAST> identDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.IDENT);

		for (DetailAST identDetailAST : identDetailASTs) {
			if (!variableName.equals(identDetailAST.getText())) {
				continue;
			}

			DetailAST nextSiblingDetailAST = identDetailAST.getNextSibling();

			if (allowGetters && (nextSiblingDetailAST != null) &&
				(nextSiblingDetailAST.getType() == TokenTypes.IDENT)) {

				String s = nextSiblingDetailAST.getText();

				if (s.startsWith("get")) {
					continue;
				}
			}

			return true;
		}

		return false;
	}

	protected FullIdent getMethodCallFullIdent(
		DetailAST detailAST, String variableName, String... methodNames) {

		if (detailAST.getType() != TokenTypes.EXPR) {
			return null;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		while (true) {
			if ((firstChildDetailAST == null) ||
				(firstChildDetailAST.getType() != TokenTypes.METHOD_CALL)) {

				return null;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			for (String methodName : methodNames) {
				if (Objects.equals(
						fullIdent.getText(), variableName + "." + methodName)) {

					return fullIdent;
				}
			}

			if (firstChildDetailAST.getType() != TokenTypes.DOT) {
				return null;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();
		}
	}

	protected String getVariableName(
		DetailAST assignDetailAST, DetailAST parentDetailAST) {

		if (parentDetailAST.getType() == TokenTypes.EXPR) {
			DetailAST nameDetailAST = assignDetailAST.getFirstChild();

			if (nameDetailAST.getType() == TokenTypes.IDENT) {
				return nameDetailAST.getText();
			}

			return null;
		}

		return getName(parentDetailAST);
	}

}