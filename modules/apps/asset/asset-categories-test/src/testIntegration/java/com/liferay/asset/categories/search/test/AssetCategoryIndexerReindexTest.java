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
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 * @author Lucas Marques
 */
@RunWith(Arquillian.class)
public class AssetCategoryIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		Group group = groupSearchFixture.addGroup(new GroupBlueprint());

		AssetVocabularyFixture assetVocabularyFixture =
			new AssetVocabularyFixture(assetVocabularyService, group);

		AssetCategoryFixture assetCategoryFixture = new AssetCategoryFixture(
			assetCategoryService, assetVocabularyFixture, group);

		_assetCategories = assetCategoryFixture.getAssetCategories();

		_assetCategoryFixture = assetCategoryFixture;

		_assetVocabularies = assetVocabularyFixture.getAssetVocabularies();

		_group = group;

		_groups = groupSearchFixture.getGroups();
	}

	@Test
	public void testReindex() throws Exception {
		Locale locale = LocaleUtil.US;

		AssetCategory assetCategory =
			_assetCategoryFixture.createAssetCategory();

		String searchTerm = assetCategory.getName();

		String fieldName = Field.NAME;

		Map<String, String> map = Collections.singletonMap(
			fieldName, searchTerm);

		assertFieldValues(fieldName, map, locale, searchTerm);

		deleteDocument(
			assetCategory.getCompanyId(), uidFactory.getUID(assetCategory));

		assertFieldValues(
			fieldName, Collections.emptyMap(), locale, searchTerm);

		reindexAllIndexerModels();

		assertFieldValues(fieldName, map, locale, searchTerm);
	}

	@Test
	public void testReindexChildCategoriesCount() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetCategory parentAssetCategory =
			_assetCategoryFixture.createAssetCategory();

		_assertChildAssetCategoriesCount(parentAssetCategory, "0");

		AssetCategory childAssetCategory = assetCategoryService.addCategory(
			parentAssetCategory.getGroupId(),
			parentAssetCategory.getCategoryId(),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			Collections.emptyMap(), parentAssetCategory.getVocabularyId(),
			new String[0], serviceContext);

		_assetCategories.add(childAssetCategory);

		_assertChildAssetCategoriesCount(parentAssetCategory, "1");

		AssetCategory newParentAssetCategory = assetCategoryService.addCategory(
			parentAssetCategory.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			Collections.emptyMap(), parentAssetCategory.getVocabularyId(),
			new String[0], serviceContext);

		_assetCategories.add(newParentAssetCategory);

		_assertChildAssetCategoriesCount(newParentAssetCategory, "0");

		assetCategoryService.moveCategory(
			childAssetCategory.getCategoryId(),
			newParentAssetCategory.getCategoryId(),
			childAssetCategory.getVocabularyId(), serviceContext);

		_assertChildAssetCategoriesCount(parentAssetCategory, "0");
		_assertChildAssetCategoriesCount(newParentAssetCategory, "1");

		assetCategoryService.deleteCategory(childAssetCategory.getCategoryId());

		_assetCategories.remove(childAssetCategory);

		_assertChildAssetCategoriesCount(newParentAssetCategory, "0");
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValues(
		String fieldName, Map<String, String> map, Locale locale,
		String searchTerm) {

		FieldValuesAssert.assertFieldValues(
			map, fieldName::equals,
			searcher.search(
				searchRequestBuilderFactory.builder(
				).companyId(
					_group.getCompanyId()
				).groupIds(
					_group.getGroupId()
				).locale(
					locale
				).fields(
					StringPool.STAR
				).modelIndexerClasses(
					AssetCategory.class
				).queryString(
					searchTerm
				).build()));
	}

	protected void deleteDocument(long companyId, String uid) throws Exception {
		indexWriterHelper.deleteDocument(companyId, uid, true);
	}

	protected void reindexAllIndexerModels() throws Exception {
		indexer.reindexCompany(_group.getCompanyId());
	}

	@Inject
	protected AssetCategoryService assetCategoryService;

	@Inject
	protected AssetVocabularyService assetVocabularyService;

	@Inject(
		filter = "indexer.class.name=com.liferay.asset.kernel.model.AssetCategory"
	)
	protected Indexer<AssetCategory> indexer;

	@Inject
	protected IndexWriterHelper indexWriterHelper;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	@Inject
	protected Searcher searcher;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected UIDFactory uidFactory;

	private void _assertChildAssetCategoriesCount(
		AssetCategory assetCategory, String childAssetCategoriesCount) {

		assertFieldValues(
			"childAssetCategoriesCount",
			Collections.singletonMap(
				"childAssetCategoriesCount", childAssetCategoriesCount),
			LocaleUtil.US, assetCategory.getName());
	}

	@DeleteAfterTestRun
	private List<AssetCategory> _assetCategories;

	private AssetCategoryFixture _assetCategoryFixture;

	@DeleteAfterTestRun
	private List<AssetVocabulary> _assetVocabularies;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

}