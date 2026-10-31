/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringBundler;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class TypedPropertyKeyCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.STRING_LITERAL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String text = detailAST.getText();

		Matcher matcher = _typedPropertyKeyPattern.matcher(text);

		if (matcher.matches()) {
			if (!_isAttrsPut(detailAST)) {
				log(
					detailAST, _MSG_TYPED_PROPERTY_KEY_VALUE, matcher.group(1),
					matcher.group(2));
			}

			return;
		}

		matcher = _filterTypedPropertyKeyPattern.matcher(text);

		while (matcher.find()) {
			log(
				detailAST, _MSG_TYPED_PROPERTY_KEY_FILTER, matcher.group(1),
				matcher.group(2));
		}
	}

	private boolean _isAttrsPut(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.EXPR) {
			return false;
		}

		parentDetailAST = parentDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.ELIST) {
			return false;
		}

		DetailAST methodCallDetailAST = parentDetailAST.getParent();

		if (!Objects.equals(getMethodName(methodCallDetailAST), "put")) {
			return false;
		}

		String variableName = getVariableName(methodCallDetailAST);

		if (variableName == null) {
			return false;
		}

		String variableTypeName = getVariableTypeName(
			methodCallDetailAST, variableName, false);

		return variableTypeName.equals("Attrs");
	}

	private static final String _MSG_TYPED_PROPERTY_KEY_FILTER =
		"typed.property.key.filter";

	private static final String _MSG_TYPED_PROPERTY_KEY_VALUE =
		"typed.property.key.value";

	private static final String _TYPES =
		"(?:Boolean|Byte|Character|Double|Float|Integer|Long|Short|String)";

	private static final Pattern _filterTypedPropertyKeyPattern =
		Pattern.compile(
			StringBundler.concat("\\((([\\w.-]+):", _TYPES, ")(?=[=~<>])"));
	private static final Pattern _typedPropertyKeyPattern = Pattern.compile(
		StringBundler.concat("\"(([\\w.-]+):", _TYPES, ")\""));

}