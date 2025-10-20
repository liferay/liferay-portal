/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class UnusedParameterCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF, TokenTypes.INTERFACE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		List<DetailAST> constructorsAndMethodsDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF);

		for (DetailAST constructorOrMethodDetailAST :
				constructorsAndMethodsDetailASTs) {

			_checkUnusedParameters(detailAST, constructorOrMethodDetailAST);
		}
	}

	private void _checkUnusedParameters(
		DetailAST classDetailAST, DetailAST constructorOrMethodDetailAST) {

		DetailAST modifiersDetailAST =
			constructorOrMethodDetailAST.findFirstToken(TokenTypes.MODIFIERS);

		if (!modifiersDetailAST.branchContains(TokenTypes.LITERAL_PRIVATE)) {
			return;
		}

		String constructorOrMethodName = getName(constructorOrMethodDetailAST);

		if (constructorOrMethodName.equals("readObject") ||
			constructorOrMethodName.equals("writeObject")) {

			return;
		}

		List<String> parameterNames = getParameterNames(
			constructorOrMethodDetailAST);

		if (parameterNames.isEmpty()) {
			return;
		}

		DetailAST statementsDetailAST =
			constructorOrMethodDetailAST.findFirstToken(TokenTypes.SLIST);

		List<String> names = getNames(statementsDetailAST, true);

		for (String parameterName :
				getParameterNames(constructorOrMethodDetailAST)) {

			if (names.contains(parameterName)) {
				continue;
			}

			if (!_isReferencedMethod(
					classDetailAST, constructorOrMethodDetailAST)) {

				log(
					constructorOrMethodDetailAST, _MSG_UNUSED_PARAMETER,
					parameterName);
			}
		}
	}

	private boolean _isReferencedMethod(
		DetailAST classDetailAST, DetailAST constructorOrMethodDetailAST) {

		List<DetailAST> methodReferenceDetailASTs = getAllChildTokens(
			classDetailAST, true, TokenTypes.METHOD_REF);

		if (methodReferenceDetailASTs.isEmpty()) {
			return false;
		}

		String constructorOrMethodName = getName(constructorOrMethodDetailAST);

		for (DetailAST methodReferenceDetailAST : methodReferenceDetailASTs) {
			for (String name : getNames(methodReferenceDetailAST, true)) {
				if (constructorOrMethodName.equals(name)) {
					return true;
				}
			}
		}

		return false;
	}

	private static final String _MSG_UNUSED_PARAMETER = "parameter.unused";

}