/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ArrayUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Pedro Victor Silvestre
 */
public class CredentialBufferCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_NEW, TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/")) {

			return;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST == null) {
			return;
		}

		if (detailAST.getType() == TokenTypes.LITERAL_NEW) {
			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			String className = fullIdent.getText();

			if (className.contains(".")) {
				className = className.substring(className.lastIndexOf('.') + 1);
			}

			if (!ArrayUtil.contains(_CONSTRUCTOR_NAMES, className)) {
				return;
			}
		}
		else {
			if (firstChildDetailAST.getType() != TokenTypes.DOT) {
				return;
			}

			String methodName = getMethodName(detailAST);

			if (!ArrayUtil.contains(_METHOD_NAMES, methodName)) {
				return;
			}

			if (methodName.equals("load") || methodName.equals("store")) {
				List<DetailAST> parameterExprDetailASTs =
					getParameterExprDetailASTs(detailAST);

				if (parameterExprDetailASTs.size() == 1) {
					return;
				}
			}
		}

		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		if (elistDetailAST == null) {
			return;
		}

		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			elistDetailAST, null, new String[] {"getBytes", "toCharArray"});

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST == null) {
				continue;
			}

			FullIdent fullIdent = FullIdent.createFullIdent(dotDetailAST);

			log(
				methodCallDetailAST, _MSG_REQUIRED_VARIABLE_ASSIGN,
				fullIdent.getText() + "()");
		}
	}

	private static final String[] _CONSTRUCTOR_NAMES = {
		"PasswordProtection", "PBEKeySpec", "SecretKeySpec"
	};

	private static final String[] _METHOD_NAMES = {
		"getKey", "init", "load", "loadKeyMaterial", "loadTrustMaterial",
		"setKeyEntry", "setPassword", "store"
	};

	private static final String _MSG_REQUIRED_VARIABLE_ASSIGN =
		"variable.assign.required";

}