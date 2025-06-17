/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Objects;

/**
 * @author Kevin Lee
 */
public class URLInputStreamCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<String> importNames = getImportNames(detailAST);

		if (!importNames.contains("java.net.URL")) {
			return;
		}

		_checkMethodCall(
			detailAST, "FileUtil", "getBytes", "URLUtil.toByteArray()");
		_checkMethodCall(
			detailAST, "Properties", "load", "PropertiesUtil.load()");
		_checkMethodCall(detailAST, "StringUtil", "read", "URLUtil.toString()");
	}

	private void _checkMethodCall(
		DetailAST methodCallDetailAST, String className, String methodName,
		String expectedUsage) {

		if (!Objects.equals(getMethodName(methodCallDetailAST), methodName)) {
			return;
		}

		String variableName = getClassOrVariableName(methodCallDetailAST);

		if (variableName == null) {
			return;
		}

		if (!variableName.equals(className)) {
			DetailAST typeDetailAST = getVariableTypeDetailAST(
				methodCallDetailAST, variableName, true);

			if ((typeDetailAST == null) ||
				!Objects.equals(getName(typeDetailAST), className)) {

				return;
			}
		}

		DetailAST firstParameterExprDetailAST = getFirstParameterExprDetailAST(
			methodCallDetailAST);

		if (firstParameterExprDetailAST == null) {
			return;
		}

		DetailAST firstChildDetailAST =
			firstParameterExprDetailAST.getFirstChild();

		if (_isURLOpenStream(firstChildDetailAST)) {
			String actualUsage = StringBundler.concat(
				variableName, StringPool.PERIOD, methodName,
				StringPool.OPEN_PARENTHESIS, StringPool.CLOSE_PARENTHESIS);

			log(
				methodCallDetailAST, _MSG_REPLACE_USAGE, expectedUsage,
				actualUsage);
		}
		else if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			DetailAST parameterDefinitionDetailAST =
				getVariableDefinitionDetailAST(
					firstChildDetailAST, firstChildDetailAST.getText());

			if ((parameterDefinitionDetailAST == null) ||
				!Objects.equals(
					getTypeName(parameterDefinitionDetailAST, false),
					"InputStream")) {

				return;
			}

			DetailAST assignDetailAST =
				parameterDefinitionDetailAST.findFirstToken(TokenTypes.ASSIGN);

			if (assignDetailAST == null) {
				return;
			}

			DetailAST exprDetailAST = assignDetailAST.findFirstToken(
				TokenTypes.EXPR);

			firstChildDetailAST = exprDetailAST.getFirstChild();

			if (_isURLOpenStream(firstChildDetailAST)) {
				String actualUsage = StringBundler.concat(
					variableName, StringPool.PERIOD, methodName,
					StringPool.OPEN_PARENTHESIS, StringPool.CLOSE_PARENTHESIS);

				log(
					methodCallDetailAST, _MSG_REPLACE_USAGE, expectedUsage,
					actualUsage);
			}
		}
	}

	private boolean _isURLOpenStream(DetailAST methodCallDetailAST) {
		if ((methodCallDetailAST.getType() != TokenTypes.METHOD_CALL) ||
			!Objects.equals(getMethodName(methodCallDetailAST), "openStream")) {

			return false;
		}

		String variableName = getVariableName(methodCallDetailAST);

		if (variableName == null) {
			return false;
		}

		DetailAST variableTypeDetailAST = getVariableTypeDetailAST(
			methodCallDetailAST, variableName, true);

		if (variableTypeDetailAST == null) {
			return false;
		}

		return Objects.equals(getTypeName(variableTypeDetailAST, false), "URL");
	}

	private static final String _MSG_REPLACE_USAGE = "replace.usage";

}