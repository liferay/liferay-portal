/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Objects;

/**
 * @author Kevin Lee
 */
public class ThreadContextClassLoaderCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.METHOD_CALL};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		for (String allowedFileName :
				getAttributeValues(_ALLOWED_FILE_NAMES_KEY)) {

			if (absolutePath.endsWith(allowedFileName)) {
				return;
			}
		}

		if (!Objects.equals(getMethodName(detailAST), "currentThread") ||
			!Objects.equals(getClassOrVariableName(detailAST), "Thread")) {

			return;
		}

		DetailAST variableDefDetailAST = getParentWithTokenType(
			detailAST, TokenTypes.VARIABLE_DEF);

		if ((variableDefDetailAST == null) ||
			!Objects.equals(
				getTypeName(variableDefDetailAST, false), "Thread")) {

			return;
		}

		String contextClassLoaderVariableName = null;

		for (DetailAST variableCallerDetailAST :
				getVariableCallerDetailASTs(variableDefDetailAST)) {

			DetailAST parentDetailAST = variableCallerDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.DOT) {
				continue;
			}

			parentDetailAST = parentDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.METHOD_CALL) {
				continue;
			}

			String methodName = getMethodName(parentDetailAST);

			if (Objects.equals(methodName, "getContextClassLoader")) {
				DetailAST contextClassLoaderVariableDefDetailAST =
					getParentWithTokenType(
						parentDetailAST, TokenTypes.VARIABLE_DEF);

				if ((contextClassLoaderVariableDefDetailAST == null) ||
					!Objects.equals(
						getTypeName(
							contextClassLoaderVariableDefDetailAST, false),
						"ClassLoader")) {

					continue;
				}

				contextClassLoaderVariableName = getName(
					contextClassLoaderVariableDefDetailAST);
			}
			else if (Objects.equals(methodName, "setContextClassLoader")) {
				DetailAST firstParameterExprDetailAST =
					getFirstParameterExprDetailAST(parentDetailAST);

				if (firstParameterExprDetailAST == null) {
					continue;
				}

				DetailAST firstChildDetailAST =
					firstParameterExprDetailAST.getFirstChild();

				if (Objects.equals(
						contextClassLoaderVariableName,
						firstChildDetailAST.getText())) {

					log(
						firstChildDetailAST,
						_MSG_THREAD_CONTEXT_CLASS_LOADER_UTIL_USE);
				}
			}
		}
	}

	private static final String _ALLOWED_FILE_NAMES_KEY = "allowedFileNames";

	private static final String _MSG_THREAD_CONTEXT_CLASS_LOADER_UTIL_USE =
		"thread.context.class.loader.util.use";

}