/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.xml;

import com.liferay.petra.string.StringPool;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class XMLUtilTest {

	@Test
	public void testGetElementText() {
		String xml =
			"<root><name>value</name><name-type>type</name-type></root>";

		Assert.assertEquals("value", XMLUtil.getElementText(xml, "name"));
		Assert.assertEquals("type", XMLUtil.getElementText(xml, "name-type"));
	}

	@Test
	public void testGetElementTextWithEmptyElement() {
		Assert.assertEquals(
			StringPool.BLANK,
			XMLUtil.getElementText("<root><name/></root>", "name"));
		Assert.assertEquals(
			StringPool.BLANK,
			XMLUtil.getElementText("<root><name></name></root>", "name"));
	}

	@Test
	public void testGetElementTextWithEntityReferences() {
		Assert.assertEquals(
			"&lt;",
			XMLUtil.getElementText(
				"<root><name>&amp;lt;</name></root>", "name"));
		Assert.assertEquals(
			"a&b<c>d\"e'f",
			XMLUtil.getElementText(
				"<root><name>a&amp;b&lt;c&gt;d\"e'f</name></root>", "name"));
	}

	@Test
	public void testGetElementTextWithMissingElement() {
		Assert.assertNull(
			XMLUtil.getElementText("<root><name>value</name></root>", "type"));
		Assert.assertNull(XMLUtil.getElementText("<root><name>value", "name"));
	}

	@Test
	public void testGetElementTextWithRepeatedElements() {
		Assert.assertEquals(
			StringPool.BLANK,
			XMLUtil.getElementText(
				"<root><name/><name>second</name></root>", "name"));
		Assert.assertEquals(
			"first",
			XMLUtil.getElementText(
				"<root><name>first</name><name/></root>", "name"));
		Assert.assertEquals(
			"first",
			XMLUtil.getElementText(
				"<root><name>first</name><name>second</name></root>", "name"));
	}

	@Test
	public void testGetElementTextWithTextBeforeProlog() {
		Assert.assertEquals(
			"value",
			XMLUtil.getElementText(
				"text\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n<root>\n" +
					"<name>value</name>\n</root>",
				"name"));
	}

}