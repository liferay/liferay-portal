/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
public class AssetVocabularyMultiLanguageSearchTest {

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

		_assetVocabularies = assetVocabularyFixture.getAssetVocabularies();
		_assetVocabularyFixture = assetVocabularyFixture;

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

		_addAssetVocabularyMultiLanguage();

		assertFieldValues(
			"description", LocaleUtil.US,
			HashMapBuilder.put(
				"description", _ENGLISH_DESCRIPTION
			).put(
				"description_en_US", _ENGLISH_DESCRIPTION
			).put(
				"description_ja_JP", _JAPANESE_DESCRIPTION
			).build(),
			"description");
	}

	@Test
	public void testEnglishTitle() throws Exception {
		setTestLocale(LocaleUtil.US);

		_addAssetVocabularyMultiLanguage();

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

		_addAssetVocabularyMultiLanguage();

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

		_addAssetVocabularyMultiLanguage();

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
		String prefix, Locale locale, Map<String, String> map,
		String searchTerm) {

		FieldValuesAssert.assertFieldValues(
			map, name -> name.startsWith(prefix),
			searcher.search(
				searchRequestBuilderFactory.builder(
				).companyId(
					getCompanyId()
				).groupIds(
					_group.getGroupId()
				).locale(
					locale
				).fields(
					StringPool.STAR
				).modelIndexerClasses(
					AssetVocabulary.class
				).queryString(
					searchTerm
				).build()));
	}

	protected long getCompanyId() {
		try {
			return TestPropsValues.getCompanyId();
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	protected void setTestLocale(Locale locale) throws Exception {
		_assetVocabularyFixture.updateDisplaySettings(locale);

		LocaleThreadLocal.setDefaultLocale(locale);
	}

	@Inject
	protected AssetVocabularyService assetVocabularyService;

	@Inject(
		filter = "indexer.class.name=com.liferay.asset.kernel.model.AssetVocabulary"
	)
	protected Indexer<AssetVocabulary> indexer;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	private void _addAssetVocabularyMultiLanguage() throws Exception {
		_assetVocabularyFixture.createAssetVocabulary(
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
	private List<AssetVocabulary> _assetVocabularies;

	private AssetVocabularyFixture _assetVocabularyFixture;
	private Locale _defaultLocale;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

}