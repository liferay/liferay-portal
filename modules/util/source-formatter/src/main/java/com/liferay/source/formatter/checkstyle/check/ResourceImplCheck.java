/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

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

		if (!className.endsWith("ResourceImpl") ||
			className.startsWith("Base")) {

			return;
		}

		_checkMethodParameterAnnotations(detailAST);
	}

	private void _checkMethodParameterAnnotations(
		DetailAST classDefinitionDetailAST) {

		List<String> allowedParameterAnnotationNames = getAttributeValues(
			_ALLOWED_PARAMETER_ANNOTATION_NAMES_KEY);

		for (DetailAST methodDefinitionDetailAST :
				getAllChildTokens(
					classDefinitionDetailAST, true, TokenTypes.METHOD_DEF)) {

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

	private static final String _MSG_INVALID_METHOD_PARAMETER_ANNOTATION =
		"method.parameter.annotation.invalid";

}