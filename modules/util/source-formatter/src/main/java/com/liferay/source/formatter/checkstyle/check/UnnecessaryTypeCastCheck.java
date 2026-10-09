/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Hugo Huijser
 */
public class UnnecessaryTypeCastCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.TYPECAST};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST rparenDetailAST = detailAST.findFirstToken(TokenTypes.RPAREN);

		DetailAST nextSiblingDetailAST = rparenDetailAST.getNextSibling();

		if (nextSiblingDetailAST.getType() != TokenTypes.METHOD_CALL) {
			return;
		}

		DetailAST firstChildDetailAST = nextSiblingDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.DOT) {
			return;
		}

		_checkUnnecessaryMethodCallTypeCast(
			detailAST, firstChildDetailAST, "Enumeration", "nextElement", 1);
		_checkUnnecessaryMethodCallTypeCast(
			detailAST, firstChildDetailAST, "List", "get", 1);
		_checkUnnecessaryMethodCallTypeCast(
			detailAST, firstChildDetailAST, "Map", "get", 2);
		_checkUnnecessaryMethodCallTypeCast(
			detailAST, firstChildDetailAST, "Map.Entry", "getKey", 1);
		_checkUnnecessaryMethodCallTypeCast(
			detailAST, firstChildDetailAST, "Map.Entry", "getValue", 2);
	}

	private void _checkUnnecessaryMethodCallTypeCast(
		DetailAST typecastDetailAST, DetailAST dotDetailAST, String typeName,
		String methodName, int index) {

		DetailAST lastChildDetailAST = dotDetailAST.getLastChild();

		if (!methodName.equals(lastChildDetailAST.getText())) {
			return;
		}

		DetailAST firstChildDetailAST = dotDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.IDENT) {
			return;
		}

		String variableName = firstChildDetailAST.getText();

		String variableTypeName = getVariableTypeName(
			dotDetailAST, variableName, true);

		if (!variableTypeName.startsWith(typeName + "<")) {
			return;
		}

		List<String> genericTypeNames = _getGenericTypeNames(variableTypeName);

		if ((genericTypeNames.size() >= index) &&
			Objects.equals(
				getTypeName(typecastDetailAST, true),
				genericTypeNames.get(index - 1))) {

			log(
				dotDetailAST, _MSG_UNNECESSARY_TYPE_CAST,
				variableName + "." + methodName);
		}
	}

	private List<String> _getGenericTypeNames(String typeName) {
		int x = typeName.indexOf(CharPool.LESS_THAN);
		int y = typeName.lastIndexOf(CharPool.GREATER_THAN);

		if ((x == -1) || (y == -1)) {
			return null;
		}

		String genericType = typeName.substring(x + 1, y);

		List<String> genericTypeNames = new ArrayList<>();

		x = -1;
		y = 0;

		while (true) {
			x = genericType.indexOf(CharPool.COMMA, x + 1);

			if (x == -1) {
				genericTypeNames.add(StringUtil.trim(genericType.substring(y)));

				return genericTypeNames;
			}

			String s = genericType.substring(y, x);

			if (StringUtil.count(s, CharPool.LESS_THAN) != StringUtil.count(
					s, CharPool.GREATER_THAN)) {

				continue;
			}

			genericTypeNames.add(StringUtil.trim(s));

			y = x + 1;
		}
	}

	private static final String _MSG_UNNECESSARY_TYPE_CAST =
		"type.cast.unnecessary";

}