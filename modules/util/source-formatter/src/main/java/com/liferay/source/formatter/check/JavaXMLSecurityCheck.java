/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;

/**
 * @author Hugo Huijser
 */
public class JavaXMLSecurityCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (fileName.contains("/test/") ||
			fileName.contains("/testIntegration/")) {

			return content;
		}

		_checkXMLSecurity(fileName, absolutePath, content);

		return content;
	}

	private void _checkXMLSecurity(
		String fileName, String absolutePath, String content) {

		String[] xmlVulnerabilities = {
			"DocumentBuilderFactory.newInstance",
			"new javax.xml.parsers.SAXParser",
			"new org.apache.xerces.parsers.SAXParser",
			"new org.dom4j.io.SAXReader", "new SAXParser", "new SAXReader",
			"SAXParserFactory.newInstance", "saxParserFactory.newInstance",
			"SAXParserFactory.newSAXParser", "saxParserFactory.newSAXParser",
			"XMLInputFactory.newFactory", "xmlInputFactory.newFactory",
			"XMLInputFactory.newInstance", "xmlInputFactory.newInstance"
		};

		boolean runOutsidePortalExclusion = isExcludedPath(
			RUN_OUTSIDE_PORTAL_EXCLUDES, absolutePath);

		for (String xmlVulnerability : xmlVulnerabilities) {
			if (!content.contains(xmlVulnerability)) {
				continue;
			}

			StringBundler sb = new StringBundler(3);

			if (runOutsidePortalExclusion) {
				sb.append("Possible XXE or Quadratic Blowup security ");
				sb.append("vulnerability using ");
			}
			else {
				sb.append("Use SecureXMLFactoryProviderUtil.");
				sb.append("newDocumentBuilderFactory instead of ");
			}

			sb.append(xmlVulnerability);

			addMessage(fileName, sb.toString());
		}
	}

}