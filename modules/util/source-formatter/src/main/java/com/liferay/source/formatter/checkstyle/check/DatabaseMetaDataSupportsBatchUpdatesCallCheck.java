/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class DatabaseMetaDataSupportsBatchUpdatesCallCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (absolutePath.endsWith("/AutoBatchPreparedStatementUtil.java")) {
			return;
		}

		DetailAST dotDetailAST = detailAST.findFirstToken(TokenTypes.DOT);

		if (dotDetailAST == null) {
			return;
		}

		List<String> names = getNames(dotDetailAST, false);

		if ((names.size() != 2) ||
			!StringUtil.equals(names.get(1), "supportsBatchUpdates") ||
			!StringUtil.equals(
				getVariableTypeName(detailAST, names.get(0), false),
				"DatabaseMetaData")) {

			return;
		}

		log(detailAST, _MSG_AVOID_METHOD_CALL);
	}

	private static final String _MSG_AVOID_METHOD_CALL = "method.call.avoid";

}