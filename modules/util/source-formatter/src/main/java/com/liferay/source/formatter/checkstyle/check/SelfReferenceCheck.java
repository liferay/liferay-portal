/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.StringPool;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class SelfReferenceCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF, TokenTypes.INTERFACE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String className = getName(detailAST);

		List<DetailAST> methodCallDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.METHOD_CALL);

		for (DetailAST methodCallDetailAST : methodCallDetailASTs) {
			DetailAST dotDetailAST = methodCallDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST == null) {
				continue;
			}

			DetailAST firstChildDetailAST = dotDetailAST.getFirstChild();

			if ((firstChildDetailAST.getType() != TokenTypes.IDENT) &&
				(firstChildDetailAST.getType() != TokenTypes.LITERAL_THIS)) {

				continue;
			}

			String methodClassName = firstChildDetailAST.getText();

			if ((firstChildDetailAST.getType() == TokenTypes.LITERAL_THIS) ||
				(methodClassName.equals(className) &&
				 !_isInsideAnonymousClass(methodCallDetailAST) &&
				 !_isInsideInnerClass(methodCallDetailAST, className) &&
				 !hasParentWithTokenType(
					 methodCallDetailAST, TokenTypes.INSTANCE_INIT))) {

				DetailAST secondChildDetailAST =
					firstChildDetailAST.getNextSibling();

				if (secondChildDetailAST.getType() == TokenTypes.IDENT) {
					log(
						methodCallDetailAST, _MSG_UNNEEDED_SELF_REFERENCE,
						secondChildDetailAST.getText(),
						firstChildDetailAST.getText() + StringPool.PERIOD);
				}
			}
		}
	}

	private boolean _isInsideAnonymousClass(DetailAST methodCallDetailAST) {
		DetailAST parentDetailAST = methodCallDetailAST.getParent();

		while (true) {
			if (parentDetailAST == null) {
				return false;
			}

			if (parentDetailAST.getType() != TokenTypes.METHOD_DEF) {
				parentDetailAST = parentDetailAST.getParent();

				continue;
			}

			parentDetailAST = parentDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.OBJBLOCK) {
				return false;
			}

			parentDetailAST = parentDetailAST.getParent();

			if (parentDetailAST.getType() != TokenTypes.CLASS_DEF) {
				return true;
			}

			return false;
		}
	}

	private boolean _isInsideInnerClass(
		DetailAST methodCallDetailAST, String className) {

		DetailAST parentDetailAST = methodCallDetailAST.getParent();

		while (true) {
			if ((parentDetailAST.getType() == TokenTypes.CLASS_DEF) ||
				(parentDetailAST.getType() == TokenTypes.ENUM_DEF) ||
				(parentDetailAST.getType() == TokenTypes.INTERFACE_DEF)) {

				return !className.equals(getName(parentDetailAST));
			}

			parentDetailAST = parentDetailAST.getParent();
		}
	}

	private static final String _MSG_UNNEEDED_SELF_REFERENCE =
		"self.reference.unneeded";

}