/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.dom4j.Document;
import org.dom4j.Element;

import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class Dom4JUtilTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testRemoveWhitespaceTextNodes() throws Exception {
		Element rootElement = _getRootElement(
			"<a>\n\t<b>\n\t\t<c />\n\t</b>\n</a>");

		Dom4JUtil.removeWhitespaceTextNodes(rootElement);

		testEquals("<a><b><c/></b></a>", Dom4JUtil.format(rootElement, false));
	}

	@Test
	public void testRemoveWhitespaceTextNodesText() throws Exception {
		Element rootElement = _getRootElement(
			"<a>\n\t<b> keep me </b>\n\t<c>\n</c>\n</a>");

		Dom4JUtil.removeWhitespaceTextNodes(rootElement);

		testEquals(
			"<a><b> keep me </b><c/></a>",
			Dom4JUtil.format(rootElement, false));
	}

	@Test
	public void testRemoveWhitespaceTextNodesUnchanged() throws Exception {
		Element rootElement = _getRootElement("<a>\n\t<b />\n</a>");

		Dom4JUtil.removeWhitespaceTextNodes(rootElement);

		String content = Dom4JUtil.format(rootElement);

		rootElement = _getRootElement(content);

		Dom4JUtil.removeWhitespaceTextNodes(rootElement);

		testEquals(content, Dom4JUtil.format(rootElement));
	}

	private Element _getRootElement(String xml) throws Exception {
		Document document = Dom4JUtil.parse(xml);

		return document.getRootElement();
	}

}