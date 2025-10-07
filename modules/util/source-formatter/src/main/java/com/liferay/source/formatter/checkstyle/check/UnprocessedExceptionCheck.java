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
public class UnprocessedExceptionCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_CATCH, TokenTypes.LITERAL_NEW};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.contains("/modules/core/") ||
			absolutePath.contains("/modules/integrations/") ||
			absolutePath.contains("/modules/sdk/") ||
			absolutePath.contains("/modules/util/") ||
			absolutePath.contains("/portal-kernel/") ||
			absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/")) {

			return;
		}

		if (detailAST.getType() == TokenTypes.LITERAL_CATCH) {
			_checkUnprocessedException(detailAST);
		}
		else {
			_checkUnprocessedThrownException(detailAST);
		}
	}

	private void _checkUnprocessedException(DetailAST detailAST) {
		DetailAST parameterDefinitionDetailAST = detailAST.findFirstToken(
			TokenTypes.PARAMETER_DEF);

		String exceptionVariableName = _getName(parameterDefinitionDetailAST);

		if (_containsVariable(
				detailAST.findFirstToken(TokenTypes.SLIST),
				exceptionVariableName)) {

			return;
		}

		String exceptionClassName = _getExceptionClassName(
			parameterDefinitionDetailAST);

		log(
			parameterDefinitionDetailAST, _MSG_UNPROCESSED_EXCEPTION,
			exceptionClassName);
	}

	private void _checkUnprocessedThrownException(DetailAST detailAST) {
		String name = _getName(detailAST);

		if ((name == null) || !name.endsWith("Exception")) {
			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return;
		}

		DetailAST exprDetailAST = parentDetailAST;

		while (true) {
			if (parentDetailAST == null) {
				return;
			}

			if (parentDetailAST.getType() == TokenTypes.LITERAL_CATCH) {
				break;
			}

			parentDetailAST = parentDetailAST.getParent();
		}

		DetailAST parameterDefinitionDetailAST = parentDetailAST.findFirstToken(
			TokenTypes.PARAMETER_DEF);

		if (Objects.equals(
				_getExceptionClassName(parameterDefinitionDetailAST),
				"JSONException")) {

			return;
		}

		String exceptionVariableName = _getName(parameterDefinitionDetailAST);

		if (_containsVariable(
				parentDetailAST.findFirstToken(TokenTypes.SLIST),
				exceptionVariableName)) {

			return;
		}

		parentDetailAST = exprDetailAST.getParent();

		if ((parentDetailAST.getType() == TokenTypes.LITERAL_THROW) ||
			(parentDetailAST.getType() == TokenTypes.SLIST)) {

			log(detailAST, _MSG_UNPROCESSED_EXCEPTION, exceptionVariableName);
		}
	}

	private boolean _containsVariable(
		DetailAST detailAST, String variableName) {

		List<String> names = getNames(detailAST, true);

		return names.contains(variableName);
	}

	private String _getExceptionClassName(
		DetailAST parameterDefinitionDetailAST) {

		DetailAST typeDetailAST = parameterDefinitionDetailAST.findFirstToken(
			TokenTypes.TYPE);

		FullIdent typeFullIdent = FullIdent.createFullIdentBelow(typeDetailAST);

		return typeFullIdent.getText();
	}

	private String _getName(DetailAST detailAST) {
		String name = getName(detailAST);

		if (name != null) {
			return name;
		}

		DetailAST dotDetailAST = detailAST.findFirstToken(TokenTypes.DOT);

		if (dotDetailAST != null) {
			return getName(dotDetailAST);
		}

		return null;
	}

	private static final String _MSG_UNPROCESSED_EXCEPTION =
		"exception.unprocessed";

}