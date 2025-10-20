/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class LPS42924Check extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (!absolutePath.endsWith("ServiceImpl.java")) {
			return;
		}

		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "PortalUtil", "getClassNameId");

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			log(methodCallDetailAST, _MSG_LPS_42924);
		}
	}

	private static final String _MSG_LPS_42924 = "lps42924";

}