/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.ListUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.util.List;

/**
 * @author Alan Huang
 */
public class ComponentExposureCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		List<String> importNames = getImportNames(detailAST);

		DetailAST parentDetailAST = detailAST.getParent();

		if ((parentDetailAST != null) ||
			!importNames.contains(
				"org.osgi.service.component.annotations.Component")) {

			return;
		}

		DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
			detailAST, "Component");

		if (annotationDetailAST == null) {
			return;
		}

		_checkExposedStaticComponent(detailAST, annotationDetailAST);
	}

	private void _checkExposedStaticComponent(
		DetailAST detailAST, DetailAST annotationDetailAST) {

		if (!_isEmptyRegisterService(annotationDetailAST)) {
			return;
		}

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		List<DetailAST> methodDefinitionDetailASTs = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.METHOD_DEF);

		if (ListUtil.isEmpty(methodDefinitionDetailASTs)) {
			return;
		}

		List<DetailAST> variableDefDetailASTs = getAllChildTokens(
			objBlockDetailAST, false, TokenTypes.VARIABLE_DEF);

		for (DetailAST variableDefDetailAST : variableDefDetailASTs) {
			String variableName = getName(variableDefDetailAST);

			String variableTypeName = getVariableTypeName(
				variableDefDetailAST, variableName, false);

			if (variableTypeName.equals("Log") ||
				!variableDefDetailAST.branchContains(
					TokenTypes.LITERAL_PRIVATE) ||
				!variableDefDetailAST.branchContains(
					TokenTypes.LITERAL_STATIC)) {

				continue;
			}

			List<DetailAST> variableCallerDetailASTs =
				getVariableCallerDetailASTs(variableDefDetailAST);

			for (DetailAST variableCallerDetailAST : variableCallerDetailASTs) {
				if (_isInsidePublicStaticMethod(variableCallerDetailAST)) {
					log(
						variableDefDetailAST, _MSG_EXPOSED_STATIC_COMPONENT,
						variableName);

					break;
				}
			}
		}
	}

	private boolean _isEmptyRegisterService(DetailAST annotationDetailAST) {
		DetailAST serviceAnnotationMemberValuePairDetailAST =
			getAnnotationMemberValuePairDetailAST(
				annotationDetailAST, "service");

		if (serviceAnnotationMemberValuePairDetailAST == null) {
			return false;
		}

		DetailAST annotationArrayInitDetailAST =
			serviceAnnotationMemberValuePairDetailAST.findFirstToken(
				TokenTypes.ANNOTATION_ARRAY_INIT);

		if (annotationArrayInitDetailAST == null) {
			return false;
		}

		DetailAST firstChildDetailAST =
			annotationArrayInitDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.RCURLY) {
			return false;
		}

		return true;
	}

	private boolean _isInsidePublicStaticMethod(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		while (parentDetailAST != null) {
			if (parentDetailAST.getType() == TokenTypes.METHOD_DEF) {
				DetailAST modifiersDetailAST = parentDetailAST.findFirstToken(
					TokenTypes.MODIFIERS);

				if (modifiersDetailAST.branchContains(
						TokenTypes.LITERAL_PUBLIC) &&
					modifiersDetailAST.branchContains(
						TokenTypes.LITERAL_STATIC)) {

					return true;
				}
			}

			parentDetailAST = parentDetailAST.getParent();
		}

		return false;
	}

	private static final String _MSG_EXPOSED_STATIC_COMPONENT =
		"static.component.exposed";

}