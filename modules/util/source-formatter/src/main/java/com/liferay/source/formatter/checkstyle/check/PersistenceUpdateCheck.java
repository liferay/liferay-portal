/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class PersistenceUpdateCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

		String s = fullIdent.getText();

		if (!s.endsWith("Persistence.update")) {
			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		DetailAST slistDetailAST = parentDetailAST.getParent();

		if (slistDetailAST.getType() != TokenTypes.SLIST) {
			return;
		}

		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		if (elistDetailAST.getChildCount() == 0) {
			return;
		}

		firstChildDetailAST = elistDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		DetailAST nameDetailAST = firstChildDetailAST.getFirstChild();

		if (nameDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		String variableName = nameDetailAST.getText();

		DetailAST typeDetailAST = getVariableTypeDetailAST(
			detailAST, variableName);

		if (typeDetailAST == null) {
			return;
		}

		String typeName = StringUtil.removeSubstring(s, "Persistence.update");

		if (typeName.startsWith("_")) {
			typeName = typeName.substring(1);
		}

		if (!StringUtil.equalsIgnoreCase(
				typeName, getTypeName(typeDetailAST, false))) {

			return;
		}

		List<DetailAST> variableCallerDetailASTs = getVariableCallerDetailASTs(
			typeDetailAST.getParent(), variableName);

		int size = variableCallerDetailASTs.size();

		for (int i = 0; i < size; i++) {
			if (!equals(variableCallerDetailASTs.get(i), nameDetailAST)) {
				continue;
			}

			if (i == (size - 1)) {
				return;
			}

			DetailAST firstNextVariableCallerDetailAST =
				variableCallerDetailASTs.get(i + 1);

			if (firstNextVariableCallerDetailAST.getPreviousSibling() != null) {
				break;
			}

			parentDetailAST = firstNextVariableCallerDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.ASSIGN) {
				break;
			}

			parentDetailAST = parentDetailAST.getParent();

			if ((parentDetailAST.getType() != TokenTypes.EXPR) ||
				!equals(parentDetailAST.getParent(), slistDetailAST)) {

				break;
			}

			if (i <= (size - 2)) {
				DetailAST secondNextVariableCallerDetailAST =
					variableCallerDetailASTs.get(i + 2);

				if (secondNextVariableCallerDetailAST.getLineNo() >
						getEndLineNumber(parentDetailAST)) {

					return;
				}
			}

			break;
		}

		log(detailAST, _MSG_REASSIGN_UPDATE_CALL, variableName);
	}

	private static final String _MSG_REASSIGN_UPDATE_CALL =
		"update.call.reassign";

}