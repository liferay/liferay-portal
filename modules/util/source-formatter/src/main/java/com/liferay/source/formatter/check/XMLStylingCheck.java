/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class XMLStylingCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		content = content.replaceAll(">\t", ">\n\t");

		content = _fixRedundantEncodingAttribute(content);

		return content;
	}

	private String _fixRedundantEncodingAttribute(String content) {
		if (!content.startsWith("<?xml ")) {
			return content;
		}

		Matcher matcher = _xmlDeclarationPattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		String xmlDeclaration = matcher.group();

		String newXmlDeclaration = xmlDeclaration.replaceAll(" +=", "=");

		newXmlDeclaration = newXmlDeclaration.replaceAll("= +", "=");

		newXmlDeclaration = newXmlDeclaration.replaceFirst(
			"(?i)\\s+encoding=\"UTF-8\"", "");

		if (newXmlDeclaration.equals(xmlDeclaration)) {
			return content;
		}

		return StringUtil.replaceFirst(
			content, xmlDeclaration, newXmlDeclaration);
	}

	private static final Pattern _xmlDeclarationPattern = Pattern.compile(
		"(\\A)<\\?xml .+?\\?>");

}