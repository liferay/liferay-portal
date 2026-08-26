/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.SearchContextTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.HashMap;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Magdalena Jedraszak
 */
@RunWith(Arquillian.class)
@Sync
public class AssetCategoryStagingSearchTest {

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

		_liveGroup = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(
			_liveGroup, TestPropsValues.getUserId());

		_stagingGroup = _liveGroup.getStagingGroup();
	}

	@Test
	@TestInfo("LPS-93695")
	public void testSearchByCategoryWhenLayoutIsPublishedToLive()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_stagingGroup.getGroupId(), RandomTestUtil.randomString());

		String categoryTitleString = RandomTestUtil.randomString();

		AssetCategory assetCategory = _assetCategoryService.addCategory(
			_stagingGroup.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			HashMapBuilder.put(
				LocaleUtil.US, categoryTitleString
			).build(),
			new HashMap<>(), assetVocabulary.getVocabularyId(), new String[0],
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId()));

		ServiceContext articleServiceContext =
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId());

		articleServiceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		JournalTestUtil.addArticle(
			_stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			new HashMap<>(),
			HashMapBuilder.put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			LocaleUtil.US, false, true, articleServiceContext);

		ExportImportTestUtil.publishLayoutsRangeFromLastPublishedDate(
			_stagingGroup, _liveGroup);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_liveGroup.getGroupId());

		searchContext.setKeywords(categoryTitleString);

		Hits hits = _indexer.search(searchContext);

		DocumentsAssert.assertCount(
			(String)searchContext.getAttribute("queryString"), hits.getDocs(),
			Field.ASSET_CATEGORY_TITLES, 1);
		DocumentsAssert.assertValuesIgnoreRelevance(
			(String)searchContext.getAttribute("queryString"), hits.getDocs(),
			Field.getLocalizedName(LocaleUtil.US, Field.ASSET_CATEGORY_TITLES),
			Collections.singletonList(
				StringUtil.toLowerCase(categoryTitleString)));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Inject
	private AssetCategoryService _assetCategoryService;

	@Inject(
		filter = "indexer.class.name=com.liferay.journal.model.JournalArticle"
	)
	private Indexer<JournalArticle> _indexer;

	@DeleteAfterTestRun
	private Group _liveGroup;

	private Group _stagingGroup;

}