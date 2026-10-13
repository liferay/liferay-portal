/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

import java.util.List;

/**
 * @author Alan Huang
 */
public class PatternMatchingForSwitchCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.LITERAL_SWITCH};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<DetailAST> childDetailASTs = getAllChildTokens(
			detailAST, false, TokenTypes.CASE_GROUP, TokenTypes.SWITCH_RULE);

		for (DetailAST childDetailAST : childDetailASTs) {
			List<DetailAST> literalCaseDetailASTs = getAllChildTokens(
				childDetailAST, false, TokenTypes.LITERAL_CASE);

			for (DetailAST literalCaseDetailAST : literalCaseDetailASTs) {
				if (!_hasPatternLabel(literalCaseDetailAST)) {
					continue;
				}

				log(
					literalCaseDetailAST,
					_MSG_AVOID_PATTERN_MATCHING_FOR_SWITCH);
			}
		}
	}

	private boolean _hasPatternLabel(DetailAST literalCaseDetailAST) {
		DetailAST childDetailAST = literalCaseDetailAST.getFirstChild();

		while (childDetailAST != null) {
			if (TokenUtil.isOfType(
					childDetailAST, TokenTypes.PATTERN_DEF,
					TokenTypes.PATTERN_VARIABLE_DEF,
					TokenTypes.RECORD_PATTERN_DEF)) {

				return true;
			}

			childDetailAST = childDetailAST.getNextSibling();
		}

		return false;
	}

	private static final String _MSG_AVOID_PATTERN_MATCHING_FOR_SWITCH =
		"pattern.matching.for.switch.avoid";

}