/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.engine.creole;

import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.wiki.engine.creole.internal.antlrwiki.translator.XhtmlTranslator;
import com.liferay.wiki.engine.creole.util.test.CreoleTestUtil;
import com.liferay.wiki.model.WikiPage;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Roberto Díaz
 */
public class XhtmlTranslatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-107145")
	public void testEscapeHeadingId() {
		WikiPage wikiPage = Mockito.mock(WikiPage.class);

		Mockito.when(
			wikiPage.getTitle()
		).thenReturn(
			"test"
		);

		XhtmlTranslator xhtmlTranslator = new XhtmlTranslator();

		String translation = xhtmlTranslator.translate(
			wikiPage, null, null, null,
			CreoleTestUtil.getWikiPageNode("heading-11.creole", getClass()));

		Assert.assertEquals(
			"<h1 id=\"section-test-a&quot;b\"> a&#34;b </h1>", translation);
	}

	@Test
	@TestInfo("LPD-107145")
	public void testEscapeTableOfContentsText() {
		WikiPage wikiPage = Mockito.mock(WikiPage.class);

		Mockito.when(
			wikiPage.getTitle()
		).thenReturn(
			"test"
		);

		XhtmlTranslator xhtmlTranslator = new XhtmlTranslator();

		String translation = xhtmlTranslator.translate(
			wikiPage, null, null, null,
			CreoleTestUtil.getWikiPageNode(
				"tableofcontents-3.creole", getClass()));

		Assert.assertFalse(translation.contains("<script>x</script>"));
		Assert.assertTrue(
			translation.contains("&lt;script&gt;x&lt;/script&gt;"));
	}

	@Test
	public void testParseCorrectlyMultipleHeadingBlocks() {
		WikiPage wikiPage = Mockito.mock(WikiPage.class);

		Mockito.when(
			wikiPage.getTitle()
		).thenReturn(
			"test"
		);

		XhtmlTranslator xhtmlTranslator = new XhtmlTranslator();

		String translation = xhtmlTranslator.translate(
			wikiPage, null, null, null,
			CreoleTestUtil.getWikiPageNode("heading-10.creole", getClass()));

		Mockito.verify(
			wikiPage, Mockito.atLeast(1)
		).getTitle();

		Assert.assertEquals(
			"<h1 id=\"section-test-Level+1\">Level 1</h1><h2 " +
				"id=\"section-test-Level+2\">Level 2</h2><h3 " +
					"id=\"section-test-Level+3\">Level 3</h3>",
			translation);
	}

}