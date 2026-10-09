/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * @author Hugo Huijser
 */
public class ContractionsCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.STRING_LITERAL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String s = StringUtil.toLowerCase(detailAST.getText());

		for (String contraction : _CONTRACTIONS) {
			int i = s.indexOf(StringUtil.toLowerCase(contraction));

			if ((i == -1) || Character.isLetterOrDigit(s.charAt(i - 1)) ||
				Character.isLetterOrDigit(s.charAt(i + contraction.length()))) {

				continue;
			}

			log(detailAST, _MSG_AVOID_CONTRACTION, contraction);
		}
	}

	private static final String[] _CONTRACTIONS = {
		"aren't", "can't", "could've", "couldn't", "didn't", "doesn't", "don't",
		"hadn't", "hasn't", "haven't", "how's", "I'd", "I'll", "I've", "isn't",
		"it's", "let's", "shouldn't", "that's", "there's", "wasn't", "we'd",
		"we'll", "we're", "we've", "weren't", "what's", "where's", "won't",
		"would've", "wouldn't", "you'd", "you'll", "you're", "you've"
	};

	private static final String _MSG_AVOID_CONTRACTION = "contraction.avoid";

}