/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.List;

/**
 * @author Alan Huang
 */
public class OSGiResourceBuilderCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
			detailAST, "Component");

		if (annotationDetailAST == null) {
			return;
		}

		List<String> importNames = null;

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			String methodName = getMethodName(methodCallDetailAST);

			if (!methodName.equals("builder")) {
				continue;
			}

			String classOrVariableName = getClassOrVariableName(
				methodCallDetailAST);

			if ((classOrVariableName == null) ||
				!classOrVariableName.matches("[A-Z].*Resource")) {

				return;
			}

			if (importNames == null) {
				importNames = getImportNames(methodCallDetailAST);
			}

			String fullyQualifiedClassName = classOrVariableName;

			for (String importName : importNames) {
				if (importName.endsWith("." + classOrVariableName)) {
					fullyQualifiedClassName = importName;

					break;
				}
			}

			if (!fullyQualifiedClassName.contains(".client.")) {
				log(methodCallDetailAST, _MSG_AVOID_METHOD_CALL);
			}
		}
	}

	private static final String _MSG_AVOID_METHOD_CALL = "method.call.avoid";

}