/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lucas Miranda
 */
public class SecretComparisonCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.contains("/modules/third-party/") ||
			absolutePath.contains("/modules/util/") ||
			absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/") ||
			isExcludedPath(_SECRET_COMPARISON_EXCLUDES)) {

			return;
		}

		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		String methodName = getMethodName(detailAST);

		if (!methodName.equals("equals")) {
			return;
		}

		List<DetailAST> operandDetailASTs = getParameterExprDetailASTs(
			detailAST);

		DetailAST receiverDetailAST = firstChildDetailAST.getFirstChild();

		if (!Objects.equals(_getName(receiverDetailAST), "Objects")) {
			operandDetailASTs.add(0, receiverDetailAST);
		}

		if (operandDetailASTs.size() != 2) {
			return;
		}

		DetailAST leftOperandDetailAST = operandDetailASTs.get(0);
		DetailAST rightOperandDetailAST = operandDetailASTs.get(1);

		if (_isConstant(leftOperandDetailAST) ||
			_isConstant(rightOperandDetailAST)) {

			return;
		}

		if (!_isSecretName(leftOperandDetailAST) &&
			!_isSecretName(rightOperandDetailAST)) {

			return;
		}

		log(detailAST, _MSG_USE_MESSAGE_DIGEST_IS_EQUAL);
	}

	private String _getName(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.EXPR) {
			detailAST = detailAST.getFirstChild();
		}

		if (detailAST.getType() == TokenTypes.IDENT) {
			return detailAST.getText();
		}

		if (detailAST.getType() == TokenTypes.DOT) {
			DetailAST lastChildDetailAST = detailAST.getLastChild();

			if (lastChildDetailAST.getType() == TokenTypes.IDENT) {
				return lastChildDetailAST.getText();
			}
		}

		return null;
	}

	private boolean _isConstant(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.EXPR) {
			detailAST = detailAST.getFirstChild();
		}

		if (detailAST.getType() == TokenTypes.STRING_LITERAL) {
			return true;
		}

		String name = _getName(detailAST);

		if (name == null) {
			return false;
		}

		Matcher matcher = _constantPattern.matcher(name);

		return matcher.matches();
	}

	private boolean _isSecretName(DetailAST detailAST) {
		String name = _getName(detailAST);

		if (name == null) {
			return false;
		}

		Matcher matcher = _identifierPattern.matcher(name);

		if (matcher.matches()) {
			return false;
		}

		matcher = _secretNamePattern.matcher(name);

		return matcher.matches();
	}

	private static final String _MSG_USE_MESSAGE_DIGEST_IS_EQUAL =
		"message.digest.is.equal.use";

	private static final String _SECRET_COMPARISON_EXCLUDES =
		"secret.comparison.excludes";

	private static final Pattern _constantPattern = Pattern.compile(
		"_*[A-Z][A-Z0-9_]{2,}");
	private static final Pattern _identifierPattern = Pattern.compile(
		".*(Ids?|Names?)");
	private static final Pattern _secretNamePattern = Pattern.compile(
		"\\w*(?i:secret|token|password|apiKey|hmac|otp|nonce)\\w*");

}