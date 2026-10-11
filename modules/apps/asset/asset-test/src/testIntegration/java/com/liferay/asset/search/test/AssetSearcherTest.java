/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.categories.configuration.AssetCategoriesCompanyConfiguration;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.search.AssetSearcherFactory;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class AssetSearcherTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		AssetVocabulary publicAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				Collections.emptyMap(), StringPool.BLANK,
				AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
				serviceContext);

		_publicAssetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), publicAssetVocabulary.getVocabularyId());
		_publicAssetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), publicAssetVocabulary.getVocabularyId());

		AssetVocabulary internalAssetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				Collections.emptyMap(), StringPool.BLANK,
				AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL,
				serviceContext);

		_internalAssetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), internalAssetVocabulary.getVocabularyId());

		_addBlogsEntry(_internalAssetCategory.getCategoryId());

		_addBlogsEntry(
			_publicAssetCategory1.getCategoryId(),
			_publicAssetCategory2.getCategoryId());
		_addBlogsEntry(
			_internalAssetCategory.getCategoryId(),
			_publicAssetCategory1.getCategoryId(),
			_publicAssetCategory2.getCategoryId());
	}

	@Test
	public void testSearchAllAssetCategoryIdsIncludingInternalAssetCategories()
		throws Exception {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAllCategoryIds(
			new long[] {
				_internalAssetCategory.getCategoryId(),
				_publicAssetCategory1.getCategoryId(),
				_publicAssetCategory2.getCategoryId()
			});

		BaseSearcher baseSearcher = _assetSearcherFactory.createBaseSearcher(
			assetEntryQuery);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setIncludeInternalAssetCategories(true);

		Hits hits = baseSearcher.search(searchContext);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());
	}

	@Test
	public void testSearchAllAssetCategoryIdsOnlyPublicAssetCategories()
		throws Exception {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAllCategoryIds(
			new long[] {
				_publicAssetCategory1.getCategoryId(),
				_publicAssetCategory2.getCategoryId()
			});

		BaseSearcher baseSearcher = _assetSearcherFactory.createBaseSearcher(
			assetEntryQuery);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setIncludeInternalAssetCategories(false);

		Hits hits = baseSearcher.search(searchContext);

		Assert.assertEquals(hits.toString(), 2, hits.getLength());
	}

	@Test
	public void testSearchAnyAssetCategoryIdsIncludingInternalAssetCategories()
		throws Exception {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAnyCategoryIds(
			new long[] {
				_internalAssetCategory.getCategoryId(),
				_publicAssetCategory1.getCategoryId(),
				_publicAssetCategory2.getCategoryId()
			});

		BaseSearcher baseSearcher = _assetSearcherFactory.createBaseSearcher(
			assetEntryQuery);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setIncludeInternalAssetCategories(true);

		Hits hits = baseSearcher.search(searchContext);

		Assert.assertEquals(hits.toString(), 3, hits.getLength());
	}

	@Test
	public void testSearchAnyAssetCategoryIdsOnlyPublicAssetCategories()
		throws Exception {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAnyCategoryIds(
			new long[] {
				_publicAssetCategory1.getCategoryId(),
				_publicAssetCategory2.getCategoryId()
			});

		BaseSearcher baseSearcher = _assetSearcherFactory.createBaseSearcher(
			assetEntryQuery);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setIncludeInternalAssetCategories(false);

		Hits hits = baseSearcher.search(searchContext);

		Assert.assertEquals(hits.toString(), 2, hits.getLength());
	}

	@Test
	public void testSearchAssetEntriesFilteredByAllCategoryIds()
		throws Exception {

		setGuestUser();

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAllCategoryIds(
			new long[] {
				_internalAssetCategory.getCategoryId(),
				_publicAssetCategory1.getCategoryId(),
				_publicAssetCategory2.getCategoryId()
			});

		BaseSearcher baseSearcher = _assetSearcherFactory.createBaseSearcher(
			assetEntryQuery);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setIncludeInternalAssetCategories(true);

		Hits hits = baseSearcher.search(searchContext);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());
	}

	@Test
	public void testSearchAssetEntriesFilteredByAnyCategoryIds()
		throws Exception {

		setGuestUser();

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAnyCategoryIds(
			new long[] {
				_internalAssetCategory.getCategoryId(),
				_publicAssetCategory1.getCategoryId(),
				_publicAssetCategory2.getCategoryId()
			});

		BaseSearcher baseSearcher = _assetSearcherFactory.createBaseSearcher(
			assetEntryQuery);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setIncludeInternalAssetCategories(true);

		Hits hits = baseSearcher.search(searchContext);

		Assert.assertEquals(hits.toString(), 3, hits.getLength());
	}

	@Test
	public void testSearchNotAllAssetCategoryIdsIncludingInternalAssetCategories()
		throws Exception {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setNotAllCategoryIds(
			new long[] {
				_internalAssetCategory.getCategoryId(),
				_publicAssetCategory1.getCategoryId(),
				_publicAssetCategory2.getCategoryId()
			});

		BaseSearcher baseSearcher = _assetSearcherFactory.createBaseSearcher(
			assetEntryQuery);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setIncludeInternalAssetCategories(true);

		Hits hits = baseSearcher.search(searchContext);

		Assert.assertEquals(hits.toString(), 2, hits.getLength());
	}

	@Test
	public void testSearchNotAllAssetCategoryIdsOnlyPublicAssetCategories()
		throws Exception {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setNotAllCategoryIds(
			new long[] {
				_publicAssetCategory1.getCategoryId(),
				_publicAssetCategory2.getCategoryId()
			});

		BaseSearcher baseSearcher = _assetSearcherFactory.createBaseSearcher(
			assetEntryQuery);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setIncludeInternalAssetCategories(false);

		Hits hits = baseSearcher.search(searchContext);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());
	}

	@Test
	public void testSearchNotAnyAssetCategoryIdsIncludingInternalAssetCategories()
		throws Exception {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setNotAnyCategoryIds(
			new long[] {
				_internalAssetCategory.getCategoryId(),
				_publicAssetCategory1.getCategoryId(),
				_publicAssetCategory2.getCategoryId()
			});

		BaseSearcher baseSearcher = _assetSearcherFactory.createBaseSearcher(
			assetEntryQuery);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setIncludeInternalAssetCategories(true);

		Hits hits = baseSearcher.search(searchContext);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	@Test
	public void testSearchNotAnyAssetCategoryIdsOnlyPublicAssetCategories()
		throws Exception {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setNotAnyCategoryIds(
			new long[] {
				_publicAssetCategory1.getCategoryId(),
				_publicAssetCategory2.getCategoryId()
			});

		BaseSearcher baseSearcher = _assetSearcherFactory.createBaseSearcher(
			assetEntryQuery);

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setIncludeInternalAssetCategories(false);

		Hits hits = baseSearcher.search(searchContext);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());
	}

	@Test
	public void testSearchParentAssetCategoryIdExcludingChildAssetCategories()
		throws Exception {

		AssetCategory childAssetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), _publicAssetCategory1.getVocabularyId(),
			_publicAssetCategory1.getCategoryId());

		_addBlogsEntry(childAssetCategory.getCategoryId());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_group.getCompanyId(),
						AssetCategoriesCompanyConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"includeChildrenCategoriesWhenSearchingParent" +
								"Categories",
							false
						).build())) {

			Hits hits = _searchAnyAssetCategoryIds(
				_publicAssetCategory1.getCategoryId());

			Assert.assertEquals(hits.toString(), 2, hits.getLength());
		}
	}

	@Test
	public void testSearchParentAssetCategoryIdIncludingChildAssetCategories()
		throws Exception {

		AssetCategory childAssetCategory = AssetTestUtil.addCategory(
			_group.getGroupId(), _publicAssetCategory1.getVocabularyId(),
			_publicAssetCategory1.getCategoryId());

		_addBlogsEntry(childAssetCategory.getCategoryId());

		Hits hits = _searchAnyAssetCategoryIds(
			_publicAssetCategory1.getCategoryId());

		Assert.assertEquals(hits.toString(), 3, hits.getLength());
	}

	protected void setGuestUser() throws Exception {
		UserTestUtil.setUser(
			_userLocalService.getGuestUser(_group.getCompanyId()));
	}

	private void _addBlogsEntry(long... assetCategoryIds) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		_blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, RandomTestUtil.randomString(),
			1, 1, 1965, 0, 0, true, true, null, StringPool.BLANK, null, null,
			serviceContext);
	}

	private Hits _searchAnyAssetCategoryIds(long... assetCategoryIds)
		throws Exception {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAnyCategoryIds(assetCategoryIds);

		BaseSearcher baseSearcher = _assetSearcherFactory.createBaseSearcher(
			assetEntryQuery);

		return baseSearcher.search(
			SearchContextTestUtil.getSearchContext(_group.getGroupId()));
	}

	@Inject
	private AssetSearcherFactory _assetSearcherFactory;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private AssetCategory _internalAssetCategory;
	private AssetCategory _publicAssetCategory1;
	private AssetCategory _publicAssetCategory2;

	@Inject
	private UserLocalService _userLocalService;

}