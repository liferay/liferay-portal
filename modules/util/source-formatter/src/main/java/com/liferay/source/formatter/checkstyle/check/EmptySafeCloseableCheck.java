/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * @author Shuyang Zhou
 */
public class EmptySafeCloseableCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LAMBDA};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.endsWith(
				"/com/liferay/petra/lang/SafeCloseable.java") ||
			!_isEmptyLambda(detailAST)) {

			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		while (parentDetailAST.getType() == TokenTypes.QUESTION) {
			parentDetailAST = parentDetailAST.getParent();
		}

		if (parentDetailAST.getType() == TokenTypes.EXPR) {
			parentDetailAST = parentDetailAST.getParent();
		}

		if (parentDetailAST.getType() == TokenTypes.LITERAL_RETURN) {
			if (_isSafeCloseableReturn(detailAST)) {
				log(detailAST, _MSG_USE_EMPTY);
			}

			return;
		}

		if (parentDetailAST.getType() != TokenTypes.ASSIGN) {
			return;
		}

		DetailAST grandparentDetailAST = parentDetailAST.getParent();

		if (grandparentDetailAST.getType() == TokenTypes.VARIABLE_DEF) {
			if (_isSafeCloseableType(grandparentDetailAST)) {
				log(detailAST, _MSG_USE_EMPTY);
			}

			return;
		}

		DetailAST targetDetailAST = parentDetailAST.getFirstChild();

		if (targetDetailAST.getType() == TokenTypes.DOT) {
			targetDetailAST = targetDetailAST.getLastChild();
		}

		if (targetDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		DetailAST variableDefinitionDetailAST = getVariableDefinitionDetailAST(
			detailAST, targetDetailAST.getText(), true);

		if ((variableDefinitionDetailAST != null) &&
			_isSafeCloseableType(variableDefinitionDetailAST)) {

			log(detailAST, _MSG_USE_EMPTY);
		}
	}

	private boolean _isEmptyLambda(DetailAST detailAST) {
		DetailAST parametersDetailAST = detailAST.findFirstToken(
			TokenTypes.PARAMETERS);

		if ((parametersDetailAST == null) ||
			(parametersDetailAST.getChildCount() != 0)) {

			return false;
		}

		DetailAST lastChildDetailAST = detailAST.getLastChild();

		if ((lastChildDetailAST.getType() == TokenTypes.SLIST) &&
			(lastChildDetailAST.getChildCount() == 1)) {

			return true;
		}

		return false;
	}

	private boolean _isSafeCloseableReturn(DetailAST detailAST) {
		DetailAST enclosingDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.COMPACT_CTOR_DEF, TokenTypes.CTOR_DEF,
			TokenTypes.INSTANCE_INIT, TokenTypes.LAMBDA, TokenTypes.METHOD_DEF,
			TokenTypes.STATIC_INIT);

		if ((enclosingDetailAST == null) ||
			(enclosingDetailAST.getType() != TokenTypes.METHOD_DEF)) {

			return false;
		}

		return _isSafeCloseableType(enclosingDetailAST);
	}

	private boolean _isSafeCloseableType(DetailAST detailAST) {
		if (detailAST.findFirstToken(TokenTypes.TYPE) == null) {
			return false;
		}

		String typeName = getTypeName(detailAST, false, true, true);

		return typeName.equals("com.liferay.petra.lang.SafeCloseable");
	}

	private static final String _MSG_USE_EMPTY = "safe.closeable.empty.use";

}