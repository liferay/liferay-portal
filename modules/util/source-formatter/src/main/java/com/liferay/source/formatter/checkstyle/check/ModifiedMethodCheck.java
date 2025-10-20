/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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
public class ModifiedMethodCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<String> importNames = getImportNames(detailAST);

		if (!importNames.contains(
				"org.osgi.service.component.annotations.Modified")) {

			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		List<DetailAST> methodDefinitionDetailASTs = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.METHOD_DEF);

		for (DetailAST methodDefinitionDetailAST : methodDefinitionDetailASTs) {
			if (!StringUtil.equals(
					getName(methodDefinitionDetailAST), "modified")) {

				continue;
			}

			DetailAST modifiersDetailAST =
				methodDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS);

			if (!AnnotationUtil.containsAnnotation(
					methodDefinitionDetailAST, "Modified") ||
				!modifiersDetailAST.branchContains(
					TokenTypes.LITERAL_PROTECTED) ||
				!StringUtil.equals(
					getTypeName(methodDefinitionDetailAST, false), "void")) {

				continue;
			}

			DetailAST slistDetailAST = methodDefinitionDetailAST.findFirstToken(
				TokenTypes.SLIST);

			List<DetailAST> exprDetailASTs = getAllChildTokens(
				slistDetailAST, false, TokenTypes.EXPR);

			if (exprDetailASTs.size() != 2) {
				continue;
			}

			DetailAST exprDetailAST = exprDetailASTs.get(0);

			DetailAST firstChildDetailAST = exprDetailAST.getFirstChild();

			if ((firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) ||
				!StringUtil.equals(
					getName(firstChildDetailAST), "deactivate")) {

				continue;
			}

			exprDetailAST = exprDetailASTs.get(1);

			firstChildDetailAST = exprDetailAST.getFirstChild();

			if ((firstChildDetailAST.getType() != TokenTypes.METHOD_CALL) ||
				!StringUtil.equals(getName(firstChildDetailAST), "activate")) {

				continue;
			}

			log(methodDefinitionDetailAST, _MSG_INCORRECT_MODIFIED_METHOD);
		}
	}

	private static final String _MSG_INCORRECT_MODIFIED_METHOD =
		"modified.method.incorrect";

}