/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class XMLPortletFileCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (fileName.endsWith("/liferay-portlet.xml") ||
			((isPortalSource() || isSubrepository()) &&
			 fileName.endsWith("/portlet-custom.xml")) ||
			(!isPortalSource() && !isSubrepository() &&
			 fileName.endsWith("/portlet.xml"))) {

			_checkPortletXML(fileName, absolutePath, content);
		}

		return content;
	}

	private void _checkPortletXML(
		String fileName, String absolutePath, String content) {

		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return;
		}

		Element rootElement = document.getRootElement();

		boolean checkNumericalPortletNameElement = !isExcludedPath(
			_NUMERICAL_PORTLET_NAME_ELEMENT_EXCLUDES, absolutePath);

		List<Element> portletElements = rootElement.elements("portlet");

		for (Element portletElement : portletElements) {
			if (!checkNumericalPortletNameElement) {
				continue;
			}

			Element portletNameElement = portletElement.element("portlet-name");

			String portletNameText = portletNameElement.getText();

			if (Validator.isNumber(portletNameText)) {
				continue;
			}

			addMessage(
				fileName,
				"Nonstandard portlet-name element \"" + portletNameText + "\"");
		}
	}

	private static final String _NUMERICAL_PORTLET_NAME_ELEMENT_EXCLUDES =
		"numerical.portlet.name.element.excludes";

}