/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringUtil;

/**
 * @author Alan Huang
 */
public class JakartaTransformXMLCheck extends BaseJakartaTransformCheck {

	@Override
	protected String format(
		String fileName, String absolutePath, String content) {

		content = replace(content);
		content = replaceTaglibURIs(content);

		int startIndex = content.indexOf("<web-app");

		if (startIndex != -1) {
			int endIndex = content.indexOf('>', startIndex);

			String webAppTag = content.substring(startIndex, endIndex);

			content = StringUtil.replace(
				content, webAppTag, _updateXMLTag(webAppTag));
		}

		startIndex = content.indexOf("<web-fragment");

		if (startIndex != -1) {
			int endIndex = content.indexOf('>', startIndex);

			String webFragmentTag = content.substring(startIndex, endIndex);

			content = StringUtil.replace(
				content, webFragmentTag, _updateXMLTag(webFragmentTag));
		}

		return content;
	}

	@Override
	protected String[] getValidExtensions() {
		return _VALID_EXTENSIONS;
	}

	private static String _updateXMLTag(String xmlTag) {
		int startIndex = xmlTag.indexOf("version=\"");

		if (startIndex != -1) {
			int endIndex = xmlTag.indexOf('"', startIndex + 9);

			String version = xmlTag.substring(startIndex, endIndex);

			xmlTag = StringUtil.replace(xmlTag, version, "version=\"6.0");
		}

		startIndex = xmlTag.indexOf("xmlns=\"");

		if (startIndex != -1) {
			int endIndex = xmlTag.indexOf('"', startIndex + 7);

			String xmlns = xmlTag.substring(startIndex, endIndex);

			xmlTag = StringUtil.replace(
				xmlTag, xmlns, "xmlns=\"https://jakarta.ee/xml/ns/jakartaee");
		}

		startIndex = xmlTag.indexOf("xsi:schemaLocation=\"");

		if (startIndex != -1) {
			int endIndex = xmlTag.indexOf('"', startIndex + 20);

			String schemaLocation = xmlTag.substring(startIndex, endIndex);

			String newSchemaLocation =
				"xsi:schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee ";

			if (xmlTag.contains("web-app")) {
				newSchemaLocation +=
					"https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd";
			}

			if (xmlTag.contains("web-fragment")) {
				newSchemaLocation +=
					"https://jakarta.ee/xml/ns/jakartaee/web-fragment_6_0.xsd";
			}

			xmlTag = StringUtil.replace(
				xmlTag, schemaLocation, newSchemaLocation);
		}

		return xmlTag;
	}

	private static final String[] _VALID_EXTENSIONS = {".xml"};

}