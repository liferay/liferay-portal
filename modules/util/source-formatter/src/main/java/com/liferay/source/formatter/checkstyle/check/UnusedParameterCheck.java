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
		if (!isDirectChildOfCompilationUnit(detailAST)) {
			return;
		}

		List<DetailAST> childDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF);

		for (DetailAST childDetailAST : childDetailASTs) {
			_checkUnusedParameters(detailAST, childDetailAST);
		}
	}

	private void _checkUnusedParameters(
		DetailAST classDetailAST, DetailAST childDetailAST) {

		DetailAST modifiersDetailAST = childDetailAST.findFirstToken(
			TokenTypes.MODIFIERS);

		if ((!modifiersDetailAST.branchContains(TokenTypes.LITERAL_PRIVATE) &&
			 !_isDSLifecycleMethod(childDetailAST)) ||
			AnnotationUtil.containsAnnotation(childDetailAST, "Override")) {

			return;
		}

		String name = getName(childDetailAST);

		if (name.equals("readObject") || name.equals("writeObject")) {
			return;
		}

		List<DetailAST> parameterDefDetailASTs = getParameterDefs(
			childDetailAST);

		if (parameterDefDetailASTs.isEmpty()) {
			return;
		}

		DetailAST slistDetailAST = childDetailAST.findFirstToken(
			TokenTypes.SLIST);

		if ((slistDetailAST == null) ||
			_isReferencedMethod(classDetailAST, childDetailAST)) {

			return;
		}

		List<String> names = getNames(slistDetailAST, true);

		for (DetailAST parameterDefDetailAST : parameterDefDetailASTs) {
			String parameterName = getName(parameterDefDetailAST);

			if (names.contains(parameterName) ||
				AnnotationUtil.containsAnnotation(
					parameterDefDetailAST, "Reference")) {

				continue;
			}

			log(childDetailAST, _MSG_UNUSED_PARAMETER, parameterName);
		}
	}

	private boolean _isDSLifecycleMethod(DetailAST detailAST) {
		if (AnnotationUtil.containsAnnotation(detailAST, "Activate") ||
			AnnotationUtil.containsAnnotation(detailAST, "Deactivate") ||
			AnnotationUtil.containsAnnotation(detailAST, "Modified")) {

			return true;
		}

		return false;
	}

	private boolean _isReferencedMethod(
		DetailAST classDetailAST, DetailAST childDetailAST) {

		List<DetailAST> methodReferenceDetailASTs = getAllChildTokens(
			classDetailAST, true, TokenTypes.METHOD_REF);

		if (methodReferenceDetailASTs.isEmpty()) {
			return false;
		}

		String name = getName(childDetailAST);

		for (DetailAST methodReferenceDetailAST : methodReferenceDetailASTs) {
			List<String> names = getNames(methodReferenceDetailAST, true);

			if (names.contains(name)) {
				return true;
			}
		}

		return false;
	}

	private static final String _MSG_UNUSED_PARAMETER = "parameter.unused";

}