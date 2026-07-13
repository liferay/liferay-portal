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

		return _fixRedundantEncodingAttribute(content);
	}

	private String _fixRedundantEncodingAttribute(String content) {
		if (!content.startsWith("<?xml ")) {
			return content;
		}

		Matcher matcher = _xmlDeclarationPattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		String attributes = matcher.group(1);

		String newAttributes = attributes.replaceAll(" +=", "=");

		newAttributes = newAttributes.replaceAll("= +", "=");

		newAttributes = newAttributes.replaceFirst(
			"(?i)\\s+encoding=\"UTF-8\"", "");

		newAttributes = newAttributes.trim();

		if (attributes.equals(newAttributes)) {
			return content;
		}

		return StringUtil.replaceFirst(content, attributes, newAttributes);
	}

	private static final Pattern _xmlDeclarationPattern = Pattern.compile(
		"\\A<\\?xml (.+?)\\?>");

}