/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.Castro
 */
@RunWith(Arquillian.class)
@Sync
public class AssetCategoryMultiLanguageSearchTest {

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

		_defaultLocale = LocaleThreadLocal.getDefaultLocale();

		_group = group;

		_groups = groupSearchFixture.getGroups();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
	}

	@Test
	public void testEnglishDescription() throws Exception {
		setTestLocale(LocaleUtil.US);

		_addAssetCategoryMultiLanguage();

		Map<String, String> descriptionMap = HashMapBuilder.put(
			"description", _ENGLISH_DESCRIPTION
		).put(
			"description_en_US", _ENGLISH_DESCRIPTION
		).put(
			"description_ja_JP", _JAPANESE_DESCRIPTION
		).build();

		String keyword = "description";

		assertFieldValues(
			"description", LocaleUtil.US, descriptionMap, keyword);
	}

	@Test
	public void testEnglishTitle() throws Exception {
		setTestLocale(LocaleUtil.US);

		_addAssetCategoryMultiLanguage();

		Map<String, String> titleMap = HashMapBuilder.put(
			"title", _ENGLISH_TITLE
		).put(
			"title_en_US", _ENGLISH_TITLE
		).put(
			"title_ja_JP", _JAPANESE_TITLE
		).put(
			"title_sortable", _ENGLISH_TITLE
		).build();

		assertFieldValues("title", LocaleUtil.US, titleMap, "title");

		assertFieldValues("title", LocaleUtil.US, titleMap, "tit");
	}

	@Test
	public void testJapaneseDescription() throws Exception {
		setTestLocale(LocaleUtil.JAPAN);

		_addAssetCategoryMultiLanguage();

		Map<String, String> descriptionMap = HashMapBuilder.put(
			"description", _JAPANESE_DESCRIPTION
		).put(
			"description_en_US", _ENGLISH_DESCRIPTION
		).put(
			"description_ja_JP", _JAPANESE_DESCRIPTION
		).build();

		assertFieldValues(
			"description", LocaleUtil.JAPAN, descriptionMap, "新規");

		assertFieldValues(
			"description", LocaleUtil.JAPAN, descriptionMap, "作成");

		assertFieldValues("description", LocaleUtil.JAPAN, descriptionMap, "新");

		assertFieldValues("description", LocaleUtil.JAPAN, descriptionMap, "作");
	}

	@Test
	public void testJapaneseTitle() throws Exception {
		setTestLocale(LocaleUtil.JAPAN);

		_addAssetCategoryMultiLanguage();

		Map<String, String> titleMap = HashMapBuilder.put(
			"title", _JAPANESE_TITLE
		).put(
			"title_en_US", _ENGLISH_TITLE
		).put(
			"title_ja_JP", _JAPANESE_TITLE
		).put(
			"title_sortable", _JAPANESE_TITLE
		).build();

		assertFieldValues("title", LocaleUtil.JAPAN, titleMap, "新規");

		assertFieldValues("title", LocaleUtil.JAPAN, titleMap, "作成");

		assertFieldValues("title", LocaleUtil.JAPAN, titleMap, "新");

		assertFieldValues("title", LocaleUtil.JAPAN, titleMap, "作");
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValues(
		String prefix, Locale locale, Map<String, String> titleStrings,
		String searchTerm) {

		FieldValuesAssert.assertFieldValues(
			titleStrings, name -> name.startsWith(prefix),
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

	protected void setTestLocale(Locale locale) throws Exception {
		_assetCategoryFixture.updateDisplaySettings(locale);

		LocaleThreadLocal.setDefaultLocale(locale);
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
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	private void _addAssetCategoryMultiLanguage() throws Exception {
		_assetCategoryFixture.createAssetCategory(
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, _ENGLISH_TITLE);
					put(LocaleUtil.JAPAN, _JAPANESE_TITLE);
				}
			},
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, _ENGLISH_DESCRIPTION);
					put(LocaleUtil.JAPAN, _JAPANESE_DESCRIPTION);
				}
			});
	}

	private static final String _ENGLISH_DESCRIPTION = "description";

	private static final String _ENGLISH_TITLE = "title";

	private static final String _JAPANESE_DESCRIPTION = "新規作成";

	private static final String _JAPANESE_TITLE = "新規作成";

	@DeleteAfterTestRun
	private List<AssetCategory> _assetCategories;

	private AssetCategoryFixture _assetCategoryFixture;
	private Locale _defaultLocale;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

}