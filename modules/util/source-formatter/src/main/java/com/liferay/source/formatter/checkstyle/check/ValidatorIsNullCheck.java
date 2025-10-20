/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class ValidatorIsNullCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "Validator", new String[] {"isNotNull", "isNull"});

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST elistDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.ELIST);

			DetailAST expressionDetailAST = elistDetailAST.findFirstToken(
				TokenTypes.EXPR);

			DetailAST childDetailAST = expressionDetailAST.getFirstChild();

			if (childDetailAST.getType() == TokenTypes.NUM_INT) {
				log(
					methodCallDetailAST, _MSG_INVALID_METHOD_NAME,
					StringBundler.concat(
						"Validator.", getMethodName(methodCallDetailAST),
						"(long)"));

				continue;
			}

			if (childDetailAST.getType() != TokenTypes.IDENT) {
				continue;
			}

			DetailAST typeDetailAST = getVariableTypeDetailAST(
				methodCallDetailAST, childDetailAST.getText());

			if (typeDetailAST == null) {
				continue;
			}

			childDetailAST = typeDetailAST.getFirstChild();

			if ((childDetailAST != null) &&
				((childDetailAST.getType() == TokenTypes.LITERAL_INT) ||
				 (childDetailAST.getType() == TokenTypes.LITERAL_LONG))) {

				log(
					methodCallDetailAST, _MSG_INVALID_METHOD_NAME,
					StringBundler.concat(
						"Validator.", getMethodName(methodCallDetailAST),
						"(long)"));

				continue;
			}

			String typeName = getTypeName(typeDetailAST, true);

			if (Validator.isNotNull(typeName) &&
				!ArrayUtil.contains(_RESERVED_TYPE_NAMES, typeName)) {

				log(
					methodCallDetailAST, _MSG_RESERVED_METHOD,
					"Validator." + getMethodName(methodCallDetailAST));
			}
		}
	}

	private static final String _MSG_INVALID_METHOD_NAME = "method.invalidName";

	private static final String _MSG_RESERVED_METHOD = "method.reserved";

	private static final String[] _RESERVED_TYPE_NAMES = {
		"Long", "Object", "Serializable", "String"
	};

}