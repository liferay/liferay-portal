/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprint;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.facet.category.CategoryFacetFactory;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
@Sync
public class AssetCategoryTitlesMultiLanguageSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		WorkflowThreadLocal.setEnabled(false);

		JournalArticleSearchFixture journalArticleSearchFixture =
			new JournalArticleSearchFixture(
				ddmStructureLocalService, journalArticleLocalService, portal);

		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		_groups = groupSearchFixture.getGroups();
		_groupSearchFixture = groupSearchFixture;

		_journalArticles = journalArticleSearchFixture.getJournalArticles();
		_journalArticleSearchFixture = journalArticleSearchFixture;
	}

	@Test
	public void testChineseCategories() throws Exception {
		String categoryTitleString = "你好";
		String journalArticleContentString = "English";
		String journalArticleTitleString = "English";

		Locale locale = LocaleUtil.CHINA;

		Group group = _groupSearchFixture.addGroup(
			new GroupBlueprint() {
				{
					setDefaultLocale(locale);
				}
			});

		AssetCategory assetCategory = addCategory(
			group, addVocabulary(group), categoryTitleString, locale);

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setAssetCategoryIds(
						new long[] {assetCategory.getCategoryId()});
					setGroupId(group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(locale, journalArticleContentString);

								setDefaultLocale(locale);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(locale, journalArticleTitleString);
							}
						});
				}
			});

		assertSearch(categoryTitleString, assetCategory, locale, group);
	}

	@Test
	public void testEnglishCategories() throws Exception {
		String categoryTitleString = "testCategory";
		String journalArticleContentString = "testContent";
		String journalArticleTitleString = "testTitle";

		Locale locale = LocaleUtil.US;

		Group group = _groupSearchFixture.addGroup(
			new GroupBlueprint() {
				{
					setDefaultLocale(locale);
				}
			});

		AssetCategory assetCategory = addCategory(
			group, addVocabulary(group), categoryTitleString, locale);

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setAssetCategoryIds(
						new long[] {assetCategory.getCategoryId()});
					setGroupId(group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(locale, journalArticleContentString);

								setDefaultLocale(locale);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(locale, journalArticleTitleString);
							}
						});
				}
			});

		assertSearch(categoryTitleString, assetCategory, locale, group);
	}

	@Test
	public void testJapaneseCategories() throws Exception {
		String categoryTitleString1 = "東京";
		String categoryTitleString2 = "京都";
		String journalArticleContentString1 = "豊島区";
		String journalArticleContentString2 = "伏見区";
		String journalArticleTitleString1 = "豊島区";
		String journalArticleTitleString2 = "伏見区";
		Locale locale = LocaleUtil.JAPAN;
		String vocabularyTitleString = "ボキャブラリ";

		Group group = _groupSearchFixture.addGroup(
			new GroupBlueprint() {
				{
					setDefaultLocale(locale);
				}
			});

		AssetVocabulary assetVocabulary = addVocabulary(
			group, vocabularyTitleString);

		AssetCategory assetCategory1 = addCategory(
			group, assetVocabulary, categoryTitleString1, locale);

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setAssetCategoryIds(
						new long[] {assetCategory1.getCategoryId()});
					setGroupId(group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(locale, journalArticleContentString1);

								setDefaultLocale(locale);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(locale, journalArticleTitleString1);
							}
						});
				}
			});

		AssetCategory assetCategory2 = addCategory(
			group, assetVocabulary, categoryTitleString2, locale);

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setAssetCategoryIds(
						new long[] {assetCategory2.getCategoryId()});
					setGroupId(group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(locale, journalArticleContentString2);

								setDefaultLocale(locale);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(locale, journalArticleTitleString2);
							}
						});
				}
			});

		assertSearch(categoryTitleString1, assetCategory1, locale, group);
		assertSearch(categoryTitleString2, assetCategory2, locale, group);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected AssetCategory addCategory(
			Group group, AssetVocabulary assetVocabulary, String title,
			Locale locale)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		Map<Locale, String> titleMap = HashMapBuilder.put(
			locale, title
		).build();

		Locale previousLocale = LocaleThreadLocal.getSiteDefaultLocale();

		LocaleThreadLocal.setSiteDefaultLocale(locale);

		try {
			AssetCategory assetCategory = assetCategoryService.addCategory(
				group.getGroupId(),
				AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, titleMap,
				new HashMap<>(), assetVocabulary.getVocabularyId(),
				new String[0], serviceContext);

			_assetCategories.add(assetCategory);

			return assetCategory;
		}
		finally {
			LocaleThreadLocal.setSiteDefaultLocale(previousLocale);
		}
	}

	protected AssetVocabulary addVocabulary(Group group) throws Exception {
		return addVocabulary(group, RandomTestUtil.randomString());
	}

	protected AssetVocabulary addVocabulary(Group group, String title)
		throws Exception {

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.addDefaultVocabulary(
				group.getGroupId());

		assetVocabulary.setTitle(title);

		assetVocabulary = assetVocabularyLocalService.updateAssetVocabulary(
			assetVocabulary);

		_assetVocabularies.add(assetVocabulary);

		return assetVocabulary;
	}

	protected void assertFacetedSearcher(
			String categoryTitleString, AssetCategory assetCategory,
			Locale locale, Group group)
		throws Exception {

		FacetedSearcher facetedSearcher =
			facetedSearcherManager.createFacetedSearcher();

		SearchContext searchContext = getSearchContext(
			categoryTitleString, locale, group);

		Hits hits = facetedSearcher.search(searchContext);

		assertHits(assetCategory, hits, searchContext);
	}

	protected void assertHits(
		AssetCategory assetCategory, Hits hits, SearchContext searchContext) {

		DocumentsAssert.assertValuesIgnoreRelevance(
			(String)searchContext.getAttribute("queryString"), hits.getDocs(),
			Field.ASSET_CATEGORY_IDS,
			Arrays.asList(String.valueOf(assetCategory.getCategoryId())));
	}

	protected void assertJournalArticleIndexer(
			String categoryTitleString, AssetCategory assetCategory,
			Locale locale, Group group)
		throws Exception {

		SearchContext searchContext = getSearchContext(
			categoryTitleString, locale, group);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.addSelectedFieldNames(Field.ASSET_CATEGORY_IDS);

		Hits hits = indexer.search(searchContext);

		assertHits(assetCategory, hits, searchContext);
	}

	protected void assertSearch(
			String categoryTitleString, AssetCategory assetCategory,
			Locale locale, Group group)
		throws Exception {

		assertJournalArticleIndexer(
			categoryTitleString, assetCategory, locale, group);
		assertFacetedSearcher(
			categoryTitleString, assetCategory, locale, group);
	}

	protected SearchContext getSearchContext(
		String keywords, Locale locale, Group group) {

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(group.getCompanyId());
		searchContext.setGroupIds(new long[] {group.getGroupId()});
		searchContext.setKeywords(keywords);
		searchContext.setLocale(locale);

		return searchContext;
	}

	@Inject
	protected static AssetCategoryService assetCategoryService;

	@Inject
	protected static AssetVocabularyLocalService assetVocabularyLocalService;

	@Inject
	protected static CategoryFacetFactory categoryFacetFactory;

	@Inject
	protected static DDMStructureLocalService ddmStructureLocalService;

	@Inject
	protected static FacetedSearcherManager facetedSearcherManager;

	@Inject(
		filter = "indexer.class.name=com.liferay.journal.model.JournalArticle"
	)
	protected static Indexer<JournalArticle> indexer;

	@Inject
	protected static JournalArticleLocalService journalArticleLocalService;

	@Inject
	protected static Portal portal;

	@Inject
	protected static UserLocalService userLocalService;

	@DeleteAfterTestRun
	private List<AssetCategory> _assetCategories = new ArrayList<>();

	@DeleteAfterTestRun
	private List<AssetVocabulary> _assetVocabularies = new ArrayList<>();

	private GroupSearchFixture _groupSearchFixture;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

}