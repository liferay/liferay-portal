/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.internal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.test.util.KBTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Akhash Ramprakash
 */
@RunWith(Arquillian.class)
public class KBArticleIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_indexer = IndexerRegistryUtil.getIndexer(KBArticle.class);
	}

	@Test
	public void testGetSummary() throws Exception {
		KBArticle kbArticle = KBTestUtil.addKBArticle(_group.getGroupId());

		Document document = _indexer.getDocument(kbArticle);

		String title = StringBundler.concat(
			HighlightUtil.HIGHLIGHT_TAG_OPEN, kbArticle.getTitle(),
			HighlightUtil.HIGHLIGHT_TAG_CLOSE);

		document.addText(
			Field.SNIPPET + StringPool.UNDERLINE + Field.TITLE, title);

		String content = StringBundler.concat(
			HighlightUtil.HIGHLIGHT_TAG_OPEN, "match",
			HighlightUtil.HIGHLIGHT_TAG_CLOSE);

		document.addText(Field.SNIPPET, content);

		Summary summary = _indexer.getSummary(
			document, LocaleUtil.US, document.get(Field.SNIPPET));

		Assert.assertNotNull(summary);
		Assert.assertEquals(title, summary.getTitle());
		Assert.assertEquals(content, summary.getContent());
	}

	@DeleteAfterTestRun
	private Group _group;

	private Indexer<KBArticle> _indexer;

}