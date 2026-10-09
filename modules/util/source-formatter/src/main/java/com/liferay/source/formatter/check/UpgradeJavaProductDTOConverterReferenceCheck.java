/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kyle Miho
 */
public class UpgradeJavaProductDTOConverterReferenceCheck
	extends BaseUpgradeCheck {

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws Exception {

		if (!fileName.endsWith(".java")) {
			return content;
		}

		JavaClass javaClass = JavaClassParser.parseJavaClass(fileName, content);

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			String childJavaTermContent = childJavaTerm.getContent();

			if (!childJavaTerm.hasAnnotation("Reference") ||
				!childJavaTermContent.contains("ProductDTOConverter")) {

				continue;
			}

			Matcher matcher = _pattern.matcher(content);

			if (matcher.find()) {
				return matcher.replaceFirst(
					_getAnnotationContent(matcher.group(2)));
			}
		}

		return content;
	}

	@Override
	protected String[] getNewImports() {
		return new String[] {
			"com.liferay.commerce.product.model.CPDefinition",
			"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product",
			"com.liferay.portal.vulcan.dto.converter.DTOConverter"
		};
	}

	private String _getAnnotationContent(String variableName) {
		return joinLines(
			"@Reference(",
			"\t\ttarget = \"(component.name=" +
				"com.liferay.headless.commerce.machine.learning.internal.dto." +
					"v1_0.converter.ProductDTOConverter)\"",
			"\t)",
			String.format(
				"\tprivate DTOConverter<CPDefinition, Product> %s;",
				variableName));
	}

	private static final Pattern _pattern = Pattern.compile(
		"@Reference\\s*(private|public|protected) " +
			"ProductDTOConverter (.*?);");

}