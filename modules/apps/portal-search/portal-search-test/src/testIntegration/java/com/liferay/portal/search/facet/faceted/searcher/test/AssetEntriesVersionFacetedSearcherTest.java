/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.facet.faceted.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprint;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.facet.type.AssetEntriesFacetFactory;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.FacetsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class AssetEntriesVersionFacetedSearcherTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		JournalArticleSearchFixture journalArticleSearchFixture =
			new JournalArticleSearchFixture(
				ddmStructureLocalService, journalArticleLocalService, portal);

		_group = groupSearchFixture.addGroup(new GroupBlueprint());
		_journalArticles = journalArticleSearchFixture.getJournalArticles();
		_journalArticleSearchFixture = journalArticleSearchFixture;
	}

	@Test
	public void testOriginal() throws Exception {
		String keyword = RandomTestUtil.randomString();

		index(keyword, _group);

		SearchContext searchContext = getSearchContext(keyword);

		Facet facet = createFacet(searchContext);

		searchContext.addFacet(facet);

		Hits hits = search(searchContext);

		assertVersions(Arrays.asList("1.0"), hits, keyword);

		FacetsAssert.assertFrequencies(
			facet.getFieldName(), searchContext, hits,
			Collections.singletonMap(JournalArticle.class.getName(), 1));
	}

	@Test
	public void testVersioned() throws Exception {
		String keyword = RandomTestUtil.randomString();

		JournalArticle journalArticle = index(keyword, _group);

		update(journalArticle);

		SearchContext searchContext = getSearchContext(keyword);

		Facet facet = createFacet(searchContext);

		searchContext.addFacet(facet);

		Hits hits = search(searchContext);

		assertVersions(Arrays.asList("1.1"), hits, keyword);

		FacetsAssert.assertFrequencies(
			facet.getFieldName(), searchContext, hits,
			Collections.singletonMap(JournalArticle.class.getName(), 1));
	}

	protected void assertVersions(
		List<String> expectedValues, Hits hits, String keyword) {

		DocumentsAssert.assertValuesIgnoreRelevance(
			keyword, hits.getDocs(), "version", expectedValues);
	}

	protected Facet createFacet(SearchContext searchContext) {
		return assetEntriesFacetFactory.newInstance(searchContext);
	}

	protected SearchContext getSearchContext(String keywords) throws Exception {
		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(TestPropsValues.getCompanyId());
		searchContext.setGroupIds(new long[] {_group.getGroupId()});
		searchContext.setKeywords(keywords);
		searchContext.setUserId(TestPropsValues.getUserId());

		return searchContext;
	}

	protected JournalArticle index(String keyword, Group group) {
		return _journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(
									LocaleUtil.US,
									RandomTestUtil.randomString());

								setDefaultLocale(LocaleUtil.US);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(LocaleUtil.US, keyword);
							}
						});
				}
			});
	}

	protected Hits search(SearchContext searchContext) throws Exception {
		FacetedSearcher facetedSearcher =
			facetedSearcherManager.createFacetedSearcher();

		return facetedSearcher.search(searchContext);
	}

	protected void update(JournalArticle journalArticle) throws Exception {
		_journalArticleSearchFixture.updateArticle(journalArticle);
	}

	@Inject
	protected AssetEntriesFacetFactory assetEntriesFacetFactory;

	@Inject
	protected DDMStructureLocalService ddmStructureLocalService;

	@Inject
	protected FacetedSearcherManager facetedSearcherManager;

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	@Inject
	protected Portal portal;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

}