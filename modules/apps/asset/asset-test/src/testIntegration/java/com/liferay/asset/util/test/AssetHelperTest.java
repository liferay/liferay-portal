/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.asset.util.AssetHelper;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
@Sync
public class AssetHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testFilterAssetEntriesByTagWithWhiteSpace() throws Exception {
		AssetTag assetTag = AssetTestUtil.addTag(
			_group.getGroupId(), "asset tag");

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setGroupIds(new long[] {_group.getGroupId()});

		long[] assetCategoryIds = new long[0];
		String[] assetTagNames = {assetTag.getName()};

		assertCount(
			0, assetEntryQuery, assetCategoryIds, assetTagNames, null,
			_group.getCompanyId(), StringPool.BLANK, null, null,
			_group.getGroupId(), null, _group.getCreatorUserId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		serviceContext.setAssetCategoryIds(assetCategoryIds);
		serviceContext.setAssetTagNames(assetTagNames);

		_blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		assertCount(
			1, assetEntryQuery, assetCategoryIds, assetTagNames, null,
			_group.getCompanyId(), null, null, null, _group.getGroupId(), null,
			_group.getCreatorUserId());
	}

	@Test
	public void testGetAssetKeywords() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		AssetTag assetTag = AssetTestUtil.addTag(_group.getGroupId());

		serviceContext.setAssetTagNames(new String[] {assetTag.getName()});

		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		Assert.assertEquals(
			assetTag.getName() + StringPool.COMMA + assetCategory.getName(),
			_assetHelper.getAssetKeywords(
				BlogsEntry.class.getName(), blogsEntry.getEntryId()));
		Assert.assertEquals(
			assetTag.getName() + StringPool.COMMA +
				assetCategory.getTitle(LocaleUtil.US),
			_assetHelper.getAssetKeywords(
				BlogsEntry.class.getName(), blogsEntry.getEntryId(),
				LocaleUtil.US));
	}

	@Test
	public void testSearchAssetEntries() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		AssetTag assetTag = AssetTestUtil.addTag(_group.getGroupId());

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setGroupIds(new long[] {_group.getGroupId()});

		long[] assetCategoryIds = {assetCategory.getCategoryId()};
		String[] assetTagNames = {assetTag.getName()};

		assertCount(
			0, assetEntryQuery, assetCategoryIds, assetTagNames, null,
			_group.getCompanyId(), StringPool.BLANK, null, null,
			_group.getGroupId(), null, _group.getCreatorUserId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAssetCategoryIds(assetCategoryIds);
		serviceContext.setAssetTagNames(assetTagNames);

		_blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		assertCount(
			1, assetEntryQuery, assetCategoryIds, assetTagNames, null,
			_group.getCompanyId(), StringPool.BLANK, null, null,
			_group.getGroupId(), null, _group.getCreatorUserId());
	}

	@Test
	public void testSearchWithMultipleAssetQueryByGroupId() throws Exception {
		Group group1 = GroupTestUtil.addGroup();
		Group group2 = GroupTestUtil.addGroup();

		try {
			_blogsEntryLocalService.addEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			_blogsEntryLocalService.addEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(group1.getGroupId()));

			_blogsEntryLocalService.addEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(group2.getGroupId()));

			_reindex();

			AssetEntryQuery assetEntryQuery1 = new AssetEntryQuery();

			assetEntryQuery1.setGroupIds(new long[] {group1.getGroupId()});

			AssetEntryQuery assetEntryQuery2 = new AssetEntryQuery();

			assetEntryQuery2.setGroupIds(new long[] {group2.getGroupId()});

			SearchContext searchContext = new SearchContext();

			searchContext.setCompanyId(TestPropsValues.getCompanyId());

			SearchHits searchHits = _assetHelper.search(
				searchContext,
				Arrays.asList(assetEntryQuery1, assetEntryQuery2),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Assert.assertEquals(
				searchHits.toString(), 2, searchHits.getTotalHits());
		}
		finally {
			GroupTestUtil.deleteGroup(group1);
			GroupTestUtil.deleteGroup(group2);
		}
	}

	@Test
	public void testSearchWithMultipleAssetQueryByTags() throws Exception {
		AssetTag assetTag1 = AssetTestUtil.addTag(
			_group.getGroupId(), RandomTestUtil.randomString());
		AssetTag assetTag2 = AssetTestUtil.addTag(
			_group.getGroupId(), RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAssetTagNames(new String[] {assetTag1.getName()});

		_blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		serviceContext.setAssetTagNames(new String[] {assetTag2.getName()});

		_blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		_blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_reindex();

		AssetEntryQuery assetEntryQuery1 = new AssetEntryQuery();

		assetEntryQuery1.setGroupIds(new long[] {_group.getGroupId()});
		assetEntryQuery1.setAllTagIds(new long[] {assetTag1.getTagId()});
		assetEntryQuery1.setClassName(BlogsEntry.class.getName());

		AssetEntryQuery assetEntryQuery2 = new AssetEntryQuery();

		assetEntryQuery2.setGroupIds(new long[] {_group.getGroupId()});
		assetEntryQuery2.setAllTagIds(new long[] {assetTag2.getTagId()});
		assetEntryQuery2.setClassName(BlogsEntry.class.getName());

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(_group.getCompanyId());

		SearchHits searchHits = _assetHelper.search(
			searchContext, Arrays.asList(assetEntryQuery1, assetEntryQuery2),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			searchHits.toString(), 2, searchHits.getTotalHits());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertCount(
			int expectedCount, AssetEntryQuery assetEntryQuery,
			long[] assetCategoryIds, String[] assetTagNames,
			Map<String, Serializable> attributes, long companyId,
			String keywords, Layout layout, Locale locale, long scopeGroupId,
			TimeZone timeZone, long userId)
		throws Exception {

		_reindex();

		BaseModelSearchResult<AssetEntry> baseModelSearchResult =
			_assetHelper.searchAssetEntries(
				assetEntryQuery, assetCategoryIds, assetTagNames, attributes,
				companyId, keywords, layout, locale, scopeGroupId, timeZone,
				userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			baseModelSearchResult.toString(), expectedCount,
			baseModelSearchResult.getLength());
	}

	private void _reindex() throws Exception {
		Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			AssetEntry.class.getName());

		indexer.reindexCompany(_group.getCompanyId());
	}

	@Inject
	private AssetHelper _assetHelper;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

}