/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ArrayUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class TestClassHardcodedPortCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (!absolutePath.endsWith("Test.java")) {
			return;
		}

		if (absolutePath.contains("/testIntegration/")) {
			_checkHardcodedPort(detailAST);

			return;
		}

		List<DetailAST> methodCallDetailASTs = getMethodCalls(
			detailAST, "PortalUtil", "getPortalServerPort");

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			log(methodCallDetailAST, _MSG_USE_HARD_CODED_PORT);
		}
	}

	private void _checkHardcodedPort(DetailAST detailAST) {
		List<DetailAST> childDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.NUM_INT, TokenTypes.STRING_LITERAL);

		for (DetailAST childDetailAST : childDetailASTs) {
			String text = childDetailAST.getText();

			if (childDetailAST.getType() == TokenTypes.NUM_INT) {
				if (!text.equals("8080")) {
					continue;
				}

				_checkHardcodedPortByMethodName(
					childDetailAST, "endpoint", "setServerPort");
			}
			else if (childDetailAST.getType() == TokenTypes.STRING_LITERAL) {
				text = text.substring(1, text.length() - 1);

				if (!text.contains(":8080")) {
					continue;
				}

				log(childDetailAST, _MSG_PORTAL_UTIL_USE);
			}
		}
	}

	private void _checkHardcodedPortByMethodName(
		DetailAST detailAST, String... methodNames) {

		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST == null) ||
			(parentDetailAST.getType() != TokenTypes.EXPR)) {

			return;
		}

		parentDetailAST = parentDetailAST.getParent();

		if ((parentDetailAST == null) ||
			(parentDetailAST.getType() != TokenTypes.ELIST)) {

			return;
		}

		parentDetailAST = parentDetailAST.getParent();

		if ((parentDetailAST == null) ||
			(parentDetailAST.getType() != TokenTypes.METHOD_CALL) ||
			!ArrayUtil.contains(methodNames, getMethodName(parentDetailAST))) {

			return;
		}

		log(detailAST, _MSG_PORTAL_UTIL_USE);
	}

	private static final String _MSG_PORTAL_UTIL_USE = "portal.util.use";

	private static final String _MSG_USE_HARD_CODED_PORT =
		"hard.coded.port.use";

}