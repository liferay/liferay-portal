/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class ThreadNameCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_NEW};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST firstChildDetailAST = detailAST.getFirstChild();

		if ((firstChildDetailAST == null) ||
			(firstChildDetailAST.getType() != TokenTypes.IDENT) ||
			!Objects.equals(firstChildDetailAST.getText(), "Thread")) {

			return;
		}

		DetailAST elistDetailAST = detailAST.findFirstToken(TokenTypes.ELIST);

		if (elistDetailAST == null) {
			return;
		}

		List<DetailAST> exprDetailASTs = getAllChildTokens(
			elistDetailAST, false, TokenTypes.EXPR);

		for (DetailAST exprDetailAST : exprDetailASTs) {
			firstChildDetailAST = exprDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
				continue;
			}

			String name = firstChildDetailAST.getText();

			name = name.substring(1, name.length() - 1);

			Matcher matcher = _camelCasePattern.matcher(name);

			String expectedName = StringUtil.getTitleCase(
				matcher.replaceAll("$1 $2"), false);

			if (!name.equals(expectedName)) {
				log(firstChildDetailAST, _MSG_RENAME_THREAD, expectedName);
			}
		}
	}

	private static final String _MSG_RENAME_THREAD = "thread.rename";

	private static final Pattern _camelCasePattern = Pattern.compile(
		"([a-z])([A-Z])");

}