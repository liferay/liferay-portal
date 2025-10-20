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
 * @author Kevin Lee
 */
public class ResourceImplCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		String absolutePath = getAbsolutePath();

		if (!absolutePath.contains("/internal/resource/")) {
			return;
		}

		String className = getName(detailAST);

		if (!className.endsWith("ResourceImpl")) {
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

		_checkDoGetMethodModifier(methodDefinitionDetailASTs);

		if (!className.startsWith("Base")) {
			_checkMethodParameterAnnotations(methodDefinitionDetailASTs);
		}
	}

	private void _checkDoGetMethodModifier(
		List<DetailAST> methodDefinitionDetailASTs) {

		for (DetailAST methodDefinitionDetailAST : methodDefinitionDetailASTs) {
			String methodName = getName(methodDefinitionDetailAST);

			if (!methodName.startsWith("doGet")) {
				continue;
			}

			DetailAST modifiersDetailAST =
				methodDefinitionDetailAST.findFirstToken(TokenTypes.MODIFIERS);

			if (!AnnotationUtil.containsAnnotation(
					methodDefinitionDetailAST, "Override") ||
				modifiersDetailAST.branchContains(
					TokenTypes.LITERAL_PROTECTED)) {

				continue;
			}

			log(modifiersDetailAST, _MSG_INCORRECT_ACCESS_MODIFIER, methodName);
		}
	}

	private void _checkMethodParameterAnnotations(
		List<DetailAST> methodDefinitionDetailASTs) {

		List<String> allowedParameterAnnotationNames = getAttributeValues(
			_ALLOWED_PARAMETER_ANNOTATION_NAMES_KEY);

		for (DetailAST methodDefinitionDetailAST : methodDefinitionDetailASTs) {
			String methodName = getName(methodDefinitionDetailAST);

			for (DetailAST parameterDefinitionDetailAST :
					getAllChildTokens(
						methodDefinitionDetailAST, true,
						TokenTypes.PARAMETER_DEF)) {

				DetailAST modifiersDetailAST =
					parameterDefinitionDetailAST.findFirstToken(
						TokenTypes.MODIFIERS);

				for (DetailAST annotationDetailAST :
						getAllChildTokens(
							modifiersDetailAST, false, TokenTypes.ANNOTATION)) {

					String annotationName = getName(annotationDetailAST);

					if (allowedParameterAnnotationNames.contains(
							annotationName)) {

						continue;
					}

					log(
						annotationDetailAST,
						_MSG_INVALID_METHOD_PARAMETER_ANNOTATION,
						annotationName, methodName);
				}
			}
		}
	}

	private static final String _ALLOWED_PARAMETER_ANNOTATION_NAMES_KEY =
		"allowedParameterAnnotationNames";

	private static final String _MSG_INCORRECT_ACCESS_MODIFIER =
		"access.modifier.incorrect";

	private static final String _MSG_INVALID_METHOD_PARAMETER_ANNOTATION =
		"method.parameter.annotation.invalid";

}