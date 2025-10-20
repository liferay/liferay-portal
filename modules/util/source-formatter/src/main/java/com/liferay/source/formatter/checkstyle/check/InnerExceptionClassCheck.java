/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Alan Huang
 */
public class InnerExceptionClassCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (!absolutePath.endsWith("Exception.java")) {
			return;
		}

		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST != null) || !_isExtendedException(detailAST)) {
			return;
		}

		if (_hasPublicConstructor(detailAST) &&
			_hasPublicStaticInnerClass(detailAST)) {

			log(detailAST, _MSG_INCORRECT_CLASS_DEFINITION);
		}
	}

	private boolean _hasPublicConstructor(DetailAST detailAST) {
		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		List<DetailAST> constructorDefinitionDetailASTs = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.CTOR_DEF);

		for (DetailAST constructorDefinitionDetailAST :
				constructorDefinitionDetailASTs) {

			DetailAST modifiersDetailAST =
				constructorDefinitionDetailAST.findFirstToken(
					TokenTypes.MODIFIERS);

			if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PUBLIC)) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasPublicStaticInnerClass(DetailAST detailAST) {
		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		List<DetailAST> classDefinitionDetailASTs = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.CLASS_DEF);

		for (DetailAST classDefinitionDetailAST : classDefinitionDetailASTs) {
			DetailAST modifiersDetailAST =
				classDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS);

			if (modifiersDetailAST.branchContains(TokenTypes.LITERAL_PUBLIC) &&
				modifiersDetailAST.branchContains(TokenTypes.LITERAL_STATIC)) {

				return true;
			}
		}

		return false;
	}

	private boolean _isExtendedException(DetailAST detailAST) {
		DetailAST extendsClauseDetailAST = detailAST.findFirstToken(
			TokenTypes.EXTENDS_CLAUSE);

		if (extendsClauseDetailAST == null) {
			return false;
		}

		String extendsClassName = null;
		DetailAST firstChildDetailAST = extendsClauseDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() == TokenTypes.DOT) {
			FullIdent fullIdent = FullIdent.createFullIdent(
				firstChildDetailAST);

			String[] parts = StringUtil.split(fullIdent.getText(), "\\.");

			extendsClassName = parts[parts.length - 1];
		}
		else if (firstChildDetailAST.getType() == TokenTypes.IDENT) {
			extendsClassName = getName(extendsClauseDetailAST);
		}

		return extendsClassName.endsWith("Exception");
	}

	private static final String _MSG_INCORRECT_CLASS_DEFINITION =
		"class.definition.incorrect";

}