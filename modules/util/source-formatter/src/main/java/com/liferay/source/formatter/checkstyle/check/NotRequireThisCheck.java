/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class NotRequireThisCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> thisDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.LITERAL_THIS);

		outerLoop:
		for (DetailAST thisDetailAST : thisDetailASTs) {
			if (thisDetailAST.getPreviousSibling() != null) {
				continue;
			}

			DetailAST parentDetailAST = thisDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.DOT) {
				continue;
			}

			String name = getName(parentDetailAST);

			List<DetailAST> definitionDetailASTs = getAllChildTokens(
				detailAST, true, TokenTypes.PARAMETER_DEF, TokenTypes.RESOURCE,
				TokenTypes.VARIABLE_DEF);

			for (DetailAST definitionDetailAST : definitionDetailASTs) {
				if (name.equals(getName(definitionDetailAST))) {
					continue outerLoop;
				}
			}

			log(thisDetailAST, _MSG_VARIABLE_THIS_NOT_REQUIRED, name);
		}
	}

	private static final String _MSG_VARIABLE_THIS_NOT_REQUIRED =
		"variable.not.require.this";

}