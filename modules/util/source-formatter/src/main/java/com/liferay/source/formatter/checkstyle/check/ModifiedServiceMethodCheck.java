/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

/**
 * @author Alan Huang
 */
public class ModifiedServiceMethodCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String className = JavaSourceUtil.getClassName(getAbsolutePath());

		if (className.equals("ServiceTrackerCustomizerFactory") ||
			!AnnotationUtil.containsAnnotation(detailAST, "Override")) {

			return;
		}

		String methodName = getName(detailAST);

		if (!methodName.equals("modifiedService")) {
			return;
		}

		DetailAST modifiersDetailAST = detailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		if (!modifiersDetailAST.branchContains(TokenTypes.LITERAL_PUBLIC)) {
			return;
		}

		DetailAST typeDetailAST = detailAST.findFirstToken(TokenTypes.TYPE);

		DetailAST firstChildDetailAST = typeDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.LITERAL_VOID) {
			return;
		}

		DetailAST slistDetailAST = detailAST.findFirstToken(TokenTypes.SLIST);

		if (slistDetailAST == null) {
			return;
		}

		firstChildDetailAST = slistDetailAST.getFirstChild();

		if (!_isMethodCall(firstChildDetailAST, "removedService")) {
			return;
		}

		DetailAST nextSiblingDetailAST = firstChildDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.SEMI)) {

			return;
		}

		nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

		if (!_isMethodCall(nextSiblingDetailAST, "addingService")) {
			return;
		}

		nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

		if ((nextSiblingDetailAST == null) ||
			(nextSiblingDetailAST.getType() != TokenTypes.SEMI) ||
			!equals(
				nextSiblingDetailAST.getNextSibling(),
				slistDetailAST.getLastChild())) {

			return;
		}

		log(detailAST, _MSG_INCORRECT_METHOD_DECLARATION);
	}

	private boolean _isMethodCall(DetailAST detailAST, String methodName) {
		if ((detailAST == null) || (detailAST.getType() != TokenTypes.EXPR)) {
			return false;
		}

		return StringUtil.equals(
			getMethodName(detailAST.getFirstChild()), methodName);
	}

	private static final String _MSG_INCORRECT_METHOD_DECLARATION =
		"method.declaration.incorrect";

}