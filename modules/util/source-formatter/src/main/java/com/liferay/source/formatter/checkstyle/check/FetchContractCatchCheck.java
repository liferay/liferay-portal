/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alan Huang
 */
public class FetchContractCatchCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_TRY};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String lookupCall = _getSoleLookupCall(detailAST);

		if (lookupCall == null) {
			return;
		}

		DetailAST literalCatchDetailAST = detailAST.findFirstToken(
			TokenTypes.LITERAL_CATCH);

		if ((literalCatchDetailAST == null) ||
			!_isSwallowToSentinel(literalCatchDetailAST)) {

			return;
		}

		DetailAST parameterDefinitionDetailAST =
			literalCatchDetailAST.findFirstToken(TokenTypes.PARAMETER_DEF);

		DetailAST typeDetailAST = parameterDefinitionDetailAST.findFirstToken(
			TokenTypes.TYPE);

		DetailAST firstChildDetailAST = typeDetailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			((firstChildDetailAST.getType() != TokenTypes.DOT) &&
			 (firstChildDetailAST.getType() != TokenTypes.IDENT))) {

			return;
		}

		String exceptionClassName = null;

		if (firstChildDetailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			exceptionClassName = fullIdent.getText();
		}
		else if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			exceptionClassName = firstChildDetailAST.getText();
		}

		int index = exceptionClassName.lastIndexOf('.');

		String shortClassName = exceptionClassName.substring(index + 1);

		if (!shortClassName.equals("PortalException") &&
			(!shortClassName.matches("NoSuch\\w*Exception") ||
			 _jdkNoSuchExceptionClassNames.contains(shortClassName))) {

			return;
		}

		log(
			parameterDefinitionDetailAST, _MSG_USE_FETCH, shortClassName,
			lookupCall);
	}

	private DetailAST _getMethodCallDetailAST(DetailAST detailAST) {
		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.EXPR) {
			DetailAST siblingDetailAST = firstChildDetailAST.getNextSibling();

			if ((siblingDetailAST == null) ||
				(siblingDetailAST.getType() != TokenTypes.SEMI)) {

				return null;
			}

			siblingDetailAST = siblingDetailAST.getNextSibling();

			if ((siblingDetailAST == null) ||
				(siblingDetailAST.getType() != TokenTypes.RCURLY)) {

				return null;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if ((firstChildDetailAST == null) ||
				(firstChildDetailAST.getType() != TokenTypes.ASSIGN)) {

				return null;
			}

			return firstChildDetailAST.findFirstToken(TokenTypes.METHOD_CALL);
		}

		if (firstChildDetailAST.getType() == TokenTypes.LITERAL_RETURN) {
			DetailAST siblingDetailAST = firstChildDetailAST.getNextSibling();

			if ((siblingDetailAST == null) ||
				(siblingDetailAST.getType() != TokenTypes.RCURLY)) {

				return null;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if ((firstChildDetailAST == null) ||
				(firstChildDetailAST.getType() != TokenTypes.EXPR)) {

				return null;
			}

			firstChildDetailAST = firstChildDetailAST.getFirstChild();

			if ((firstChildDetailAST == null) ||
				(firstChildDetailAST.getType() != TokenTypes.METHOD_CALL)) {

				return null;
			}

			return firstChildDetailAST;
		}

		return null;
	}

	private String _getSoleLookupCall(DetailAST detailAST) {
		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.SLIST)) {

			return null;
		}

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			firstChildDetailAST, true, TokenTypes.METHOD_CALL);

		if (methodCallDetailASTs.size() != 1) {
			return null;
		}

		DetailAST methodCallDetailAST = _getMethodCallDetailAST(
			firstChildDetailAST);

		if (methodCallDetailAST == null) {
			return null;
		}

		DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
			TokenTypes.DOT);

		if (dotDetailAST == null) {
			return null;
		}

		List<String> names = getNames(dotDetailAST, false);

		if (names.size() != 2) {
			return null;
		}

		String methodCallClassName = names.get(0);

		if (!methodCallClassName.endsWith("LocalService") &&
			!methodCallClassName.endsWith("LocalServiceUtil") &&
			!methodCallClassName.endsWith("Persistence")) {

			return null;
		}

		String methodCallMethodName = names.get(1);

		if (!methodCallMethodName.matches("(findBy|get)\\w+") &&
			(!methodCallClassName.endsWith("Persistence") ||
			 !methodCallMethodName.equals("remove"))) {

			return null;
		}

		return methodCallClassName + "." + methodCallMethodName;
	}

	private boolean _isSwallowToSentinel(DetailAST detailAST) {
		DetailAST slistDetailAST = detailAST.findFirstToken(TokenTypes.SLIST);

		if (slistDetailAST == null) {
			return false;
		}

		List<DetailAST> childDetailASTs = getAllChildTokens(
			slistDetailAST, false, ALL_TYPES);

		for (DetailAST childDetailAST : childDetailASTs) {
			if (childDetailAST.getType() == TokenTypes.LITERAL_IF) {
				DetailAST exprDetailAST = childDetailAST.findFirstToken(
					TokenTypes.EXPR);

				if (exprDetailAST == null) {
					return false;
				}

				DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

				if ((firstChildDetailAST == null) ||
					(firstChildDetailAST.getType() != TokenTypes.METHOD_CALL)) {

					return false;
				}

				FullIdent fullIdent = FullIdent.createFullIdentBelow(
					firstChildDetailAST);

				String methodCall = fullIdent.getText();

				if (methodCall.matches("_log\\.is\\w+Enabled")) {
					continue;
				}

				return false;
			}

			if (childDetailAST.getType() != TokenTypes.LITERAL_RETURN) {
				return false;
			}

			DetailAST exprDetailAST = childDetailAST.findFirstToken(
				TokenTypes.EXPR);

			if (exprDetailAST == null) {
				return false;
			}

			DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

			if ((firstChildDetailAST.getType() == TokenTypes.LITERAL_FALSE) ||
				(firstChildDetailAST.getType() == TokenTypes.LITERAL_NULL)) {

				return true;
			}

			return false;
		}

		return false;
	}

	private static final String _MSG_USE_FETCH = "fetch.use";

	private static final List<String> _jdkNoSuchExceptionClassNames =
		Arrays.asList(
			"NoSuchAlgorithmException", "NoSuchElementException",
			"NoSuchFieldException", "NoSuchFileException",
			"NoSuchMechanismException", "NoSuchMethodException",
			"NoSuchObjectException", "NoSuchPaddingException",
			"NoSuchProviderException");

}