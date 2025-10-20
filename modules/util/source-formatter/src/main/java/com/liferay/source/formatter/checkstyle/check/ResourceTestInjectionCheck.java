/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.List;

/**
 * @author Alan Huang
 */
public class ResourceTestInjectionCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String className = getName(detailAST);

		if (!className.endsWith("ResourceTest")) {
			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		List<DetailAST> variableDefDetailASTs = getAllChildTokens(
			detailAST.findFirstToken(TokenTypes.OBJBLOCK), false,
			TokenTypes.VARIABLE_DEF);

		for (DetailAST variableDefDetailAST : variableDefDetailASTs) {
			if (!AnnotationUtil.containsAnnotation(
					variableDefDetailAST, "Inject")) {

				continue;
			}

			String variableName = getName(variableDefDetailAST);

			String variableTypeName = getVariableTypeName(
				variableDefDetailAST, variableName, false, false, true);

			if ((variableTypeName == null) ||
				!variableTypeName.startsWith("com.liferay")) {

				continue;
			}

			int index = variableTypeName.lastIndexOf(".");

			if (!StringUtil.endsWith(
					variableTypeName.substring(index + 1), "Resource")) {

				continue;
			}

			String packageName = variableTypeName.substring(0, index);

			index = packageName.lastIndexOf(".resource.");

			if ((index == -1) ||
				StringUtil.endsWith(
					packageName.substring(0, index), ".client")) {

				continue;
			}

			log(
				variableDefDetailAST, _MSG_INCORRECT_RESOURCE_INJECTION,
				variableName);
		}
	}

	private static final String _MSG_INCORRECT_RESOURCE_INJECTION =
		"resource.injection.incorrect";

}