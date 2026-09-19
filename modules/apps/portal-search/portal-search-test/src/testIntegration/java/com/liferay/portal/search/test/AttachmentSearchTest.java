/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.test.util.KBTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.test.util.WikiTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@RunWith(Arquillian.class)
public class AttachmentSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testIncludeKnowledgeBaseArticleAttachments() throws Exception {
		KBArticle kbArticle = KBTestUtil.addKBArticle(_group.getGroupId());

		KBTestUtil.addKBArticleAttachment(
			kbArticle.getUserId(), kbArticle.getResourcePrimKey(),
			kbArticle.getTitle(), getClass(), "Test.docx");

		Assert.assertEquals(1, _searchCount(false, kbArticle.getTitle()));
		Assert.assertEquals(2, _searchCount(true, kbArticle.getTitle()));
	}

	@Test
	public void testIncludeWikiPageAttachments() throws Exception {
		WikiNode wikiNode = WikiTestUtil.addNode(_group.getGroupId());

		WikiPage wikiPage = WikiTestUtil.addPage(
			_group.getGroupId(), wikiNode.getNodeId(), true);

		WikiTestUtil.addPageAttachment(
			wikiPage.getUserId(), wikiPage.getNodeId(), wikiPage.getTitle(),
			wikiPage.getTitle(), getClass(), "Test.docx");

		Assert.assertEquals(1, _searchCount(false, wikiPage.getTitle()));
		Assert.assertEquals(2, _searchCount(true, wikiPage.getTitle()));
	}

	private int _searchCount(boolean includeAttachments, String title)
		throws Exception {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).emptySearchEnabled(
				true
			).groupIds(
				_group.getGroupId()
			).query(
				QueriesUtil.multiMatch(
					title, "title_en_US", "localized_title_en_US")
			).withSearchContext(
				searchContext -> searchContext.setIncludeAttachments(
					includeAttachments)
			).build());

		return searchResponse.getTotalHits();
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

}