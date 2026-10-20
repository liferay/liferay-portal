/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Petteri Karttunen
 */
public class WordUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testIsPlural() {
		Assert.assertFalse(WordUtil.isPlural(null));
		Assert.assertFalse(WordUtil.isPlural("address"));
		Assert.assertTrue(WordUtil.isPlural("asset-metrics"));
		Assert.assertTrue(WordUtil.isPlural("blog-postings"));
		Assert.assertFalse(WordUtil.isPlural("by-status"));
		Assert.assertTrue(WordUtil.isPlural("navigation-menus"));
		Assert.assertFalse(WordUtil.isPlural("site"));
		Assert.assertTrue(WordUtil.isPlural("sites"));
		Assert.assertFalse(WordUtil.isPlural("status"));
		Assert.assertTrue(WordUtil.isPlural("statuses"));
		Assert.assertFalse(WordUtil.isPlural("task-statistics"));
	}

	@Test
	public void testNormalize() {
		Assert.assertEquals(StringPool.BLANK, WordUtil.normalize(null));
		Assert.assertEquals(
			"blogpostings", WordUtil.normalize("Blog-Postings"));
		Assert.assertEquals("blogpostings", WordUtil.normalize("BlogPostings"));
	}

	@Test
	public void testToPhrase() {
		Assert.assertEquals(StringPool.BLANK, WordUtil.toPhrase(null));
		Assert.assertEquals("blog posting", WordUtil.toPhrase("BlogPosting"));
		Assert.assertEquals("cms blog id", WordUtil.toPhrase("cMSBlogId"));
		Assert.assertEquals(
			"get site blog postings page",
			WordUtil.toPhrase("getSiteBlogPostingsPage"));
		Assert.assertEquals(
			"open api document", WordUtil.toPhrase("OpenAPIDocument"));
	}

	@Test
	public void testToPlural() {
		Assert.assertEquals(StringPool.BLANK, WordUtil.toPlural(null));
		Assert.assertEquals("addresses", WordUtil.toPlural("address"));
		Assert.assertEquals("boxes", WordUtil.toPlural("box"));
		Assert.assertEquals("categories", WordUtil.toPlural("category"));
		Assert.assertEquals("keys", WordUtil.toPlural("key"));
		Assert.assertEquals("metrics", WordUtil.toPlural("metrics"));
		Assert.assertEquals("news", WordUtil.toPlural("news"));
		Assert.assertEquals(
			"reindex statuses", WordUtil.toPlural("reindex status"));
		Assert.assertEquals("sites", WordUtil.toPlural("site"));
		Assert.assertEquals("sites", WordUtil.toPlural("sites"));
		Assert.assertEquals("statuses", WordUtil.toPlural("status"));
	}

	@Test
	public void testToSingular() {
		Assert.assertEquals(StringPool.BLANK, WordUtil.toSingular(null));
		Assert.assertEquals("address", WordUtil.toSingular("address"));
		Assert.assertEquals("address", WordUtil.toSingular("addresses"));
		Assert.assertEquals(
			"blog posting", WordUtil.toSingular("blog postings"));
		Assert.assertEquals("box", WordUtil.toSingular("boxes"));
		Assert.assertEquals("category", WordUtil.toSingular("categories"));
		Assert.assertEquals("metric", WordUtil.toSingular("metrics"));
		Assert.assertEquals("news", WordUtil.toSingular("news"));
		Assert.assertEquals("process", WordUtil.toSingular("process"));
		Assert.assertEquals("site", WordUtil.toSingular("site"));
		Assert.assertEquals("site", WordUtil.toSingular("sites"));
		Assert.assertEquals("status", WordUtil.toSingular("status"));
		Assert.assertEquals("status", WordUtil.toSingular("statuses"));
		Assert.assertEquals("warehouse", WordUtil.toSingular("warehouses"));
	}

	@Test
	public void testToWords() {
		Assert.assertArrayEquals(new String[0], WordUtil.toWords(null));
		Assert.assertArrayEquals(
			new String[] {"delete", "in", "bulk", "the", "users"},
			WordUtil.toWords("Delete, in bulk,  the users!"));
		Assert.assertArrayEquals(
			new String[] {"list", "blog-postings"},
			WordUtil.toWords("list blog-postings"));
		Assert.assertArrayEquals(
			new String[] {"list", "the", "blog", "postings", "of", "a", "site"},
			WordUtil.toWords("list the blog postings of a site?"));
		Assert.assertArrayEquals(
			new String[] {"list", "the", "site", "blog", "postings"},
			WordUtil.toWords("list the site\u2019s blog postings"));
		Assert.assertArrayEquals(
			new String[] {"list", "the", "users", "roles"},
			WordUtil.toWords("list the users' roles"));
	}

}