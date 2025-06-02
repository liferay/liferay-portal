/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.ModifiableSettings;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.search.test.util.SearchContextTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class JournalArticleAssetEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new CompanyServiceSettingsLocator(
				TestPropsValues.getCompanyId(),
				JournalServiceConfiguration.class.getName()));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		_originalIndexAllArticleVersionsEnabled = GetterUtil.getBoolean(
			modifiableSettings.getValue(
				"indexAllArticleVersionsEnabled", "true"));

		modifiableSettings.setValue("indexAllArticleVersionsEnabled", "true");

		modifiableSettings.store();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		Settings settings = FallbackKeysSettingsUtil.getSettings(
			new CompanyServiceSettingsLocator(
				TestPropsValues.getCompanyId(),
				JournalServiceConfiguration.class.getName()));

		ModifiableSettings modifiableSettings =
			settings.getModifiableSettings();

		modifiableSettings.setValue(
			"indexAllArticleVersionsEnabled",
			String.valueOf(_originalIndexAllArticleVersionsEnabled));

		modifiableSettings.store();
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_indexerFixture = new IndexerFixture<>(
			JournalArticle.class, _searchRequestBuilderFactory);
	}

	@Test
	public void testUpdateAssetCategoryTitle() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);

		Locale locale = PortalUtil.getSiteDefaultLocale(_group.getGroupId());

		_assertSearch(assetCategory.getTitle(locale), journalArticle);

		String updatedAssetCategoryTitle = RandomTestUtil.randomString();

		_assetCategoryLocalService.updateCategory(
			TestPropsValues.getUserId(), assetCategory.getCategoryId(),
			assetCategory.getParentCategoryId(),
			HashMapBuilder.put(
				locale, updatedAssetCategoryTitle
			).build(),
			assetCategory.getDescriptionMap(), assetCategory.getVocabularyId(),
			null, serviceContext);

		_assertSearch(updatedAssetCategoryTitle, journalArticle);
	}

	@Test
	public void testUpdateAssetTagClassificationInDraft() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetTag assetTag1 = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		serviceContext.setAssetTagNames(new String[] {assetTag1.getName()});

		Locale locale = _portal.getSiteDefaultLocale(_group);

		JournalArticle approvedJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, true, serviceContext);

		AssetTag assetTag2 = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		serviceContext.setAssetTagNames(new String[] {assetTag2.getName()});

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		JournalArticle draftJournalArticle = JournalTestUtil.updateArticle(
			approvedJournalArticle, approvedJournalArticle.getTitle(locale),
			approvedJournalArticle.getContent(), false, false, serviceContext);

		_assertSearchJournalArticleVersions(
			_localization.getLocalizedName(
				Field.ASSET_TAG_NAMES, LocaleUtil.toLanguageId(locale)),
			approvedJournalArticle,
			HashMapBuilder.put(
				approvedJournalArticle.getVersion(), assetTag1.getName()
			).put(
				draftJournalArticle.getVersion(), assetTag2.getName()
			).build());
	}

	@Test
	public void testUpdateAssetTagName() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetTag assetTag = _assetTagLocalService.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		serviceContext.setAssetTagNames(new String[] {assetTag.getName()});

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, serviceContext);

		_assertSearch(assetTag.getName(), journalArticle);

		String updatedAssetTagName = RandomTestUtil.randomString();

		_assetTagLocalService.updateTag(
			assetTag.getExternalReferenceCode(), TestPropsValues.getUserId(),
			assetTag.getTagId(), updatedAssetTagName, serviceContext);

		_assertSearch(updatedAssetTagName, journalArticle);
	}

	@Test
	public void testUpdateCategorizationInDraft() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory1.getCategoryId()});

		Locale locale = _portal.getSiteDefaultLocale(_group);

		JournalArticle approvedJournalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true, RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, true, serviceContext);

		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory2.getCategoryId()});

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		JournalArticle draftJournalArticle = JournalTestUtil.updateArticle(
			approvedJournalArticle, approvedJournalArticle.getTitle(locale),
			approvedJournalArticle.getContent(), false, false, serviceContext);

		_assertSearchJournalArticleVersions(
			_localization.getLocalizedName(
				Field.ASSET_CATEGORY_TITLES, LocaleUtil.toLanguageId(locale)),
			approvedJournalArticle,
			HashMapBuilder.put(
				approvedJournalArticle.getVersion(),
				assetCategory1.getTitle(locale)
			).put(
				draftJournalArticle.getVersion(),
				assetCategory2.getTitle(locale)
			).build());
	}

	@Test
	public void testUpdateJournalArticleTitleWithMultipleVersions()
		throws Exception {

		Locale locale = _portal.getSiteDefaultLocale(_group);

		String title = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, StringPool.BLANK,
			true,
			HashMapBuilder.put(
				locale, title
			).build(),
			RandomTestUtil.randomLocaleStringMap(locale),
			RandomTestUtil.randomLocaleStringMap(locale), null, locale, null,
			false, true, serviceContext);

		_assertSearch(title, journalArticle);

		Map<Double, String> versionTitleMap = HashMapBuilder.put(
			journalArticle.getVersion(), title
		).build();

		_assertSearchJournalArticleVersions(
			"localized_title", journalArticle, versionTitleMap);

		journalArticle = _getUpdatedJournalArticle(
			true, journalArticle, serviceContext, versionTitleMap);

		_assertSearchJournalArticleVersions(
			"localized_title", journalArticle, versionTitleMap);

		journalArticle = _getUpdatedJournalArticle(
			true, journalArticle, serviceContext, versionTitleMap);

		_assertSearchJournalArticleVersions(
			"localized_title", journalArticle, versionTitleMap);

		journalArticle = _getUpdatedJournalArticle(
			false, journalArticle, serviceContext, versionTitleMap);

		_assertSearchJournalArticleVersions(
			"localized_title", journalArticle, versionTitleMap);
	}

	private void _assertArticleId(
		Document document, JournalArticle journalArticle) {

		String[] values = document.getValues(Field.ARTICLE_ID);

		Assert.assertEquals(values.toString(), 1, values.length);

		Assert.assertEquals(journalArticle.getArticleId(), values[0]);
	}

	private void _assertSearch(String keyword, JournalArticle journalArticle) {
		Document document = _indexerFixture.searchOnlyOne(keyword);

		_assertArticleId(document, journalArticle);
	}

	private void _assertSearchJournalArticleVersions(
		String fieldName, JournalArticle journalArticle,
		Map<Double, String> versionFieldValueMap) {

		Document[] documents = _searchJournalArticleVersions(
			journalArticle.getDescriptionCurrentValue());

		Assert.assertEquals(
			documents.toString(), versionFieldValueMap.size(),
			documents.length);

		for (Document document : documents) {
			_assertArticleId(document, journalArticle);

			double version = GetterUtil.getDouble(document.get("versionCount"));

			Assert.assertTrue(versionFieldValueMap.containsKey(version));

			Assert.assertTrue(document.hasField(fieldName));

			String fieldValue = document.get(fieldName);

			Assert.assertTrue(
				fieldValue,
				StringUtil.equalsIgnoreCase(
					versionFieldValueMap.get(version), fieldValue));
		}
	}

	private JournalArticle _getUpdatedJournalArticle(
			boolean approved, JournalArticle journalArticle,
			ServiceContext serviceContext, Map<Double, String> versionTitleMap)
		throws Exception {

		if (approved) {
			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);
		}
		else {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		String title = RandomTestUtil.randomString();

		JournalArticle updatedJournalArticle = JournalTestUtil.updateArticle(
			journalArticle, title, journalArticle.getContent(), false, approved,
			serviceContext);

		if (approved) {
			_assertSearch(title, updatedJournalArticle);
		}

		versionTitleMap.put(updatedJournalArticle.getVersion(), title);

		return updatedJournalArticle;
	}

	private Document[] _searchJournalArticleVersions(String keywords) {
		try {
			Indexer<JournalArticle> indexer = _indexerRegistry.getIndexer(
				JournalArticle.class.getName());

			Hits hits = indexer.search(
				SearchContextTestUtil.getSearchContext(
					TestPropsValues.getUserId(),
					new long[] {_group.getGroupId()}, keywords, null,
					HashMapBuilder.<String, Serializable>put(
						Field.STATUS, WorkflowConstants.STATUS_ANY
					).put(
						"head", false
					).put(
						"latest", false
					).put(
						"showNonindexable", false
					).build()));

			return hits.getDocs();
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private static boolean _originalIndexAllArticleVersionsEnabled;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private IndexerFixture<JournalArticle> _indexerFixture;

	@Inject
	private IndexerRegistry _indexerRegistry;

	@Inject
	private Localization _localization;

	@Inject
	private Portal _portal;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}