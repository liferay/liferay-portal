/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.Objects;

/**
 * @author Alan Huang
 */
public class JavaBasePortalFilterCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		if (!fileName.endsWith("Filter.java") ||
			!isDerivedFrom(
				absolutePath, fileContent,
				"com.liferay.portal.servlet.filters.BasePortalFilter") ||
			!javaTerm.hasAnnotation("Override") ||
			!Objects.equals(javaTerm.getName(), "isFilterEnabled")) {

			return javaTerm.getContent();
		}

		JavaSignature javaSignature = javaTerm.getSignature();

		List<JavaParameter> javaParameters = javaSignature.getParameters();

		if (!javaParameters.isEmpty()) {
			return javaTerm.getContent();
		}

		addMessage(
			fileName,
			"Do not override method \"isFilterEnabled\", see LPD-69645",
			javaTerm.getLineNumber());

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_METHOD};
	}

}