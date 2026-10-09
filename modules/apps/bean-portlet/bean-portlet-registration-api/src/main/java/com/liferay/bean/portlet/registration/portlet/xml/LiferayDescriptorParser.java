/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.registration.portlet.xml;

import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;

import java.net.URL;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Neil Griffin
 */
public class LiferayDescriptorParser {

	public static Map<String, Map<String, Set<String>>> parse(
			URL liferayDescriptorURL)
		throws DocumentException {

		Map<String, Map<String, Set<String>>> configurations = new HashMap<>();

		Document document = UnsecureSAXReaderUtil.read(
			liferayDescriptorURL, true);

		Element rootElement = document.getRootElement();

		for (Element portletElement : rootElement.elements("portlet")) {
			String portletName = portletElement.elementText("portlet-name");

			Map<String, Set<String>> configuration = new HashMap<>();

			for (Element element : portletElement.elements()) {
				String elementName = element.getName();

				if (Objects.equals(elementName, "portlet-name")) {
					continue;
				}

				String key = "com.liferay.portlet.".concat(elementName);

				Set<String> values = configuration.get(key);

				if (values == null) {
					values = new LinkedHashSet<>();

					configuration.put(key, values);
				}

				values.add(element.getText());
			}

			configurations.put(portletName, configuration);
		}

		return configurations;
	}

}