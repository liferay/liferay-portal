/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class VirtualThreadsCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST dotDetailAST = detailAST.findFirstToken(TokenTypes.DOT);

		if (dotDetailAST == null) {
			return;
		}

		List<String> names = getNames(dotDetailAST, false);

		if (names.size() != 2) {
			return;
		}

		String methodCall = names.get(0) + "." + names.get(1);

		if (!methodCall.equals("Executors.newVirtualThreadPerTaskExecutor") &&
			!methodCall.equals("Thread.ofVirtual") &&
			!methodCall.equals("Thread.startVirtualThread")) {

			return;
		}

		log(detailAST, _MSG_AVOID_VIRTUAL_THREADS, detailAST);
	}

	private static final String _MSG_AVOID_VIRTUAL_THREADS =
		"virtual.threads.avoid";

}