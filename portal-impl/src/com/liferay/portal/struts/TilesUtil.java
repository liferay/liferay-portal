/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.struts;

import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import jakarta.servlet.ServletContext;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shuyang Zhou
 */
public class TilesUtil {

	public static final String DEFINITION =
		"com.liferay.portal.struts.definition";

	public static final String DEFINITIONS =
		"com.liferay.portal.struts.definitions";

	public static void loadDefinitions(ServletContext servletContext)
		throws Exception {

		Map<String, Definition> definitions = new HashMap<>();

		List<Element> deferedElements = new ArrayList<>();

		try (InputStream inputStream = servletContext.getResourceAsStream(
				"/WEB-INF/tiles-defs.xml")) {

			Document document = SAXReaderUtil.read(inputStream, false);

			Element rootElement = document.getRootElement();

			for (Element definitionElement :
					rootElement.elements("definition")) {

				String parentName = definitionElement.attributeValue("extends");

				if (parentName == null) {
					_addDefinition(definitions, definitionElement, null);
				}
				else {
					Definition parentDefinition = definitions.get(parentName);

					if (parentDefinition == null) {
						deferedElements.add(rootElement);
					}
					else {
						_addDefinition(
							definitions, definitionElement, parentDefinition);
					}
				}
			}

			for (Element definitionElement : deferedElements) {
				Definition parentDefinition = definitions.get(
					definitionElement.attributeValue("extends"));

				_addDefinition(
					definitions, definitionElement, parentDefinition);
			}

			servletContext.setAttribute(DEFINITIONS, definitions);
		}
	}

	private static void _addDefinition(
		Map<String, Definition> definitions, Element definitionElement,
		Definition parentDefinition) {

		String name = definitionElement.attributeValue("name");

		Map<String, String> attributes = new HashMap<>();

		String path = definitionElement.attributeValue("path");

		if (parentDefinition != null) {
			attributes.putAll(parentDefinition.getAttributes());

			if (path == null) {
				path = parentDefinition.getPath();
			}
		}

		for (Element putElement : definitionElement.elements("put")) {
			attributes.put(
				putElement.attributeValue("name"),
				putElement.attributeValue("value"));
		}

		definitions.put(name, new Definition(path, attributes));
	}

}