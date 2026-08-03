/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ArrayUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Caio Farias
 */
public class FIPSTLSVerificationCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.CLASS_DEF, TokenTypes.IDENT, TokenTypes.LITERAL_NEW,
			TokenTypes.METHOD_CALL
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.contains("/modules/apps/archived/") ||
			absolutePath.contains("/modules/third-party/") ||
			absolutePath.contains("/src/test/") ||
			absolutePath.contains("/src/testIntegration/") ||
			absolutePath.contains("/test/unit/")) {

			return;
		}

		if (detailAST.getType() == TokenTypes.CLASS_DEF) {
			_checkTrustManagerClass(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.IDENT) {
			_checkBypassName(detailAST);
		}
		else if (detailAST.getType() == TokenTypes.LITERAL_NEW) {
			_checkAnonymousClass(detailAST);
		}
		else {
			_checkEndpointIdentificationAlgorithm(detailAST);
		}
	}

	private void _checkAnonymousClass(DetailAST literalNewDetailAST) {
		if (literalNewDetailAST.findFirstToken(TokenTypes.OBJBLOCK) == null) {
			return;
		}

		String className = _getClassName(literalNewDetailAST);

		if (!ArrayUtil.contains(_ANONYMOUS_CLASS_NAMES, className) ||
			_hasGuard(literalNewDetailAST)) {

			return;
		}

		log(
			literalNewDetailAST, _MSG_REQUIRED_GUARD_METHOD,
			"new " + className + "()");
	}

	private void _checkBypassName(DetailAST identDetailAST) {
		String name = identDetailAST.getText();

		if (!ArrayUtil.contains(_BYPASS_NAMES, name) ||
			hasParentWithTokenType(
				identDetailAST, TokenTypes.EXTENDS_CLAUSE,
				TokenTypes.IMPLEMENTS_CLAUSE, TokenTypes.IMPORT,
				TokenTypes.STATIC_IMPORT) ||
			_hasGuard(identDetailAST)) {

			return;
		}

		log(identDetailAST, _MSG_REQUIRED_GUARD_METHOD, name);
	}

	private void _checkEndpointIdentificationAlgorithm(
		DetailAST methodCallDetailAST) {

		String methodName = getMethodName(methodCallDetailAST);

		if ((methodName == null) || !methodName.equals(_METHOD_NAME)) {
			return;
		}

		List<DetailAST> parameterExprDetailASTs = getParameterExprDetailASTs(
			methodCallDetailAST);

		if (parameterExprDetailASTs.size() != 1) {
			return;
		}

		DetailAST exprDetailAST = parameterExprDetailASTs.get(0);

		DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

		if (firstChildDetailAST == null) {
			return;
		}

		String argument = null;

		if (firstChildDetailAST.getType() == TokenTypes.LITERAL_NULL) {
			argument = "null";
		}
		else if (firstChildDetailAST.getType() == TokenTypes.STRING_LITERAL) {
			String text = firstChildDetailAST.getText();

			if (text.equals("\"\"")) {
				argument = text;
			}
		}

		if ((argument == null) || _hasGuard(methodCallDetailAST)) {
			return;
		}

		log(
			methodCallDetailAST, _MSG_REQUIRED_GUARD_METHOD,
			StringBundler.concat(_METHOD_NAME, "(", argument, ")"));
	}

	private void _checkTrustManagerClass(DetailAST classDefDetailAST) {
		for (int tokenType :
				new int[] {
					TokenTypes.EXTENDS_CLAUSE, TokenTypes.IMPLEMENTS_CLAUSE
				}) {

			DetailAST clauseDetailAST = classDefDetailAST.findFirstToken(
				tokenType);

			if (clauseDetailAST == null) {
				continue;
			}

			for (String name : getNames(clauseDetailAST, false)) {
				if (!ArrayUtil.contains(_TRUST_MANAGER_CLASS_NAMES, name) ||
					_containsGuard(classDefDetailAST)) {

					continue;
				}

				log(classDefDetailAST, _MSG_REQUIRED_GUARD_CLASS);

				return;
			}
		}
	}

	private boolean _containsGuard(DetailAST detailAST) {
		for (DetailAST dotDetailAST :
				getAllChildTokens(detailAST, true, TokenTypes.DOT)) {

			FullIdent fullIdent = FullIdent.createFullIdent(dotDetailAST);

			String text = fullIdent.getText();

			if (text.equals(_GUARD_NAME)) {
				return true;
			}
		}

		return false;
	}

	private String _getClassName(DetailAST literalNewDetailAST) {
		DetailAST firstChildDetailAST = literalNewDetailAST.getFirstChild();

		if (firstChildDetailAST == null) {
			return null;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(firstChildDetailAST);

		String className = fullIdent.getText();

		if (className.contains(".")) {
			className = className.substring(className.lastIndexOf('.') + 1);
		}

		return className;
	}

	private boolean _hasGuard(DetailAST detailAST) {
		DetailAST parentDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.CTOR_DEF, TokenTypes.INSTANCE_INIT,
			TokenTypes.METHOD_DEF, TokenTypes.STATIC_INIT);

		if (parentDetailAST == null) {
			parentDetailAST = getParentWithTokenType(
				detailAST, TokenTypes.CLASS_DEF);
		}

		if (parentDetailAST == null) {
			return false;
		}

		return _containsGuard(parentDetailAST);
	}

	private static final String[] _ANONYMOUS_CLASS_NAMES = {
		"HostnameVerifier", "X509ExtendedTrustManager", "X509TrustManager"
	};

	private static final String[] _BYPASS_NAMES = {
		"ALLOW_ALL_HOSTNAME_VERIFIER", "AllowAllHostnameVerifier",
		"NoopHostnameVerifier", "TrustAllStrategy", "TrustSelfSignedStrategy"
	};

	private static final String _GUARD_NAME = "PropsValues.FIPS_ENABLED";

	private static final String _METHOD_NAME =
		"setEndpointIdentificationAlgorithm";

	private static final String _MSG_REQUIRED_GUARD_CLASS =
		"guard.class.required";

	private static final String _MSG_REQUIRED_GUARD_METHOD =
		"guard.method.required";

	private static final String[] _TRUST_MANAGER_CLASS_NAMES = {
		"X509ExtendedTrustManager", "X509TrustManager"
	};

}