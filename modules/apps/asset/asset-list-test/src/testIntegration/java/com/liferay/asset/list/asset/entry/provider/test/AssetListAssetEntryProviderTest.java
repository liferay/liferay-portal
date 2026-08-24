/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.asset.entry.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.list.test.util.AssetListTestUtil;
import com.liferay.asset.publisher.util.AssetQueryRule;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.info.pagination.InfoPage;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
public class AssetListAssetEntryProviderTest {

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

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());
	}

	@Test
	public void testCombineSegmentsEntriesOfDynamicCollection()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.asset.list.internal.configuration." +
						"AssetListConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"combineAssetsFromAllSegmentsDynamic", true
					).build())) {

			AssetListEntry assetListEntry =
				_assetListEntryLocalService.addAssetListEntry(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					_group.getGroupId(), RandomTestUtil.randomString(),
					AssetListEntryTypeConstants.TYPE_DYNAMIC, null,
					_serviceContext);

			User userTest = TestPropsValues.getUser();

			String userName = "RandomName";

			User user = UserTestUtil.addUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				StringPool.BLANK, userName + "@liferay.com", userName,
				LocaleUtil.getDefault(), userName,
				RandomTestUtil.randomString(), null,
				ServiceContextTestUtil.getServiceContext());

			SegmentsEntry segmentsEntry1 = _addSegmentsEntryByFirstName(
				_group.getGroupId(), userTest.getFirstName());
			SegmentsEntry segmentsEntry2 = _addSegmentsEntryByFirstName(
				_group.getGroupId(), user.getFirstName());

			JournalArticle journalArticle1 = _addJournalArticle(
				new long[0], TestPropsValues.getUserId());
			JournalArticle journalArticle2 = _addJournalArticle(
				new long[0], TestPropsValues.getUserId());
			JournalArticle journalArticle3 = _addJournalArticle(
				new long[0], user.getUserId());

			AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
				_group.getGroupId(), assetListEntry,
				segmentsEntry1.getSegmentsEntryId(),
				_getTypeSettings(userTest.getFirstName()));

			AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
				_group.getGroupId(), assetListEntry,
				segmentsEntry2.getSegmentsEntryId(),
				_getTypeSettings(userName));

			long[] segmentsEntryIds = {
				segmentsEntry1.getSegmentsEntryId(),
				segmentsEntry2.getSegmentsEntryId()
			};

			_assertAssetListEntryResults(
				_assetListAssetEntryProvider.getAssetEntriesInfoPage(
					assetListEntry, segmentsEntryIds, null, null,
					StringPool.BLANK, StringPool.BLANK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS),
				3, _getAssetEntry(journalArticle1),
				_getAssetEntry(journalArticle2),
				_getAssetEntry(journalArticle3));
			_assertAssetListEntryResultsPagination(
				assetListEntry, segmentsEntryIds,
				_getAssetEntry(journalArticle1),
				_getAssetEntry(journalArticle2),
				_getAssetEntry(journalArticle3));
		}
	}

	@Test
	public void testCombineSegmentsEntriesOfDynamicCollectionWithCategoryFilter()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.asset.list.internal.configuration." +
						"AssetListConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"combineAssetsFromAllSegmentsDynamic", true
					).build())) {

			AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
				_group.getGroupId());

			AssetCategory assetCategory1 = AssetTestUtil.addCategory(
				_group.getGroupId(), assetVocabulary.getVocabularyId());
			AssetCategory assetCategory2 = AssetTestUtil.addCategory(
				_group.getGroupId(), assetVocabulary.getVocabularyId());

			AssetListEntry assetListEntry =
				_assetListEntryLocalService.addAssetListEntry(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					_group.getGroupId(), RandomTestUtil.randomString(),
					AssetListEntryTypeConstants.TYPE_DYNAMIC, null,
					_serviceContext);

			User user = TestPropsValues.getUser();

			SegmentsEntry segmentsEntry1 = _addSegmentsEntryByFirstName(
				_group.getGroupId(), user.getFirstName());
			SegmentsEntry segmentsEntry2 = _addSegmentsEntryByLastName(
				_group.getGroupId(), user.getLastName());

			AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
				_group.getGroupId(), assetListEntry,
				segmentsEntry1.getSegmentsEntryId(),
				_getTypeSettings(
					new AssetQueryRule(
						true, false, "assetCategories",
						new String[] {
							String.valueOf(assetCategory1.getCategoryId())
						})));

			AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
				_group.getGroupId(), assetListEntry,
				segmentsEntry2.getSegmentsEntryId(),
				_getTypeSettings(
					new AssetQueryRule(
						true, false, "assetCategories",
						new String[] {
							String.valueOf(assetCategory2.getCategoryId())
						})));

			JournalArticle journalArticle1 = _addJournalArticle(
				new long[] {assetCategory1.getCategoryId()});
			JournalArticle journalArticle2 = _addJournalArticle(
				new long[] {assetCategory2.getCategoryId()});

			_addJournalArticle(new long[0]);

			_assertAssetListEntryResults(
				_assetListAssetEntryProvider.getAssetEntriesInfoPage(
					assetListEntry,
					new long[] {
						segmentsEntry1.getSegmentsEntryId(),
						segmentsEntry2.getSegmentsEntryId()
					},
					null, null, StringPool.BLANK, StringPool.BLANK,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				2, _getAssetEntry(journalArticle1),
				_getAssetEntry(journalArticle2));
		}
	}

	@Test
	public void testCombineSegmentsEntriesOfDynamicCollectionWithoutDuplications()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.asset.list.internal.configuration." +
						"AssetListConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"combineAssetsFromAllSegmentsDynamic", true
					).build())) {

			AssetListEntry assetListEntry =
				_assetListEntryLocalService.addAssetListEntry(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					_group.getGroupId(), RandomTestUtil.randomString(),
					AssetListEntryTypeConstants.TYPE_DYNAMIC, null,
					_serviceContext);

			User user = TestPropsValues.getUser();

			SegmentsEntry segmentsEntry1 = _addSegmentsEntryByFirstName(
				_group.getGroupId(), user.getFirstName());
			SegmentsEntry segmentsEntry2 = _addSegmentsEntryByFirstName(
				_group.getGroupId(), user.getFirstName());

			JournalArticle journalArticle1 = _addJournalArticle(
				new long[0], TestPropsValues.getUserId());
			JournalArticle journalArticle2 = _addJournalArticle(
				new long[0], TestPropsValues.getUserId());
			JournalArticle journalArticle3 = _addJournalArticle(
				new long[0], TestPropsValues.getUserId());

			AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
				_group.getGroupId(), assetListEntry,
				segmentsEntry1.getSegmentsEntryId(),
				_getTypeSettings(user.getFirstName()));

			AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
				_group.getGroupId(), assetListEntry,
				segmentsEntry2.getSegmentsEntryId(),
				_getTypeSettings(user.getFirstName()));

			long[] segmentsEntryIds = {
				segmentsEntry1.getSegmentsEntryId(),
				segmentsEntry2.getSegmentsEntryId()
			};

			_assertAssetListEntryResults(
				_assetListAssetEntryProvider.getAssetEntriesInfoPage(
					assetListEntry, segmentsEntryIds, null, null,
					StringPool.BLANK, StringPool.BLANK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS),
				3, _getAssetEntry(journalArticle1),
				_getAssetEntry(journalArticle2),
				_getAssetEntry(journalArticle3));
		}
	}

	@Test
	public void testCombineSegmentsEntriesOfManualCollection()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.asset.list.internal.configuration." +
						"AssetListConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"combineAssetsFromAllSegmentsManual", true
					).build())) {

			AssetListEntry assetListEntry =
				_assetListEntryLocalService.addAssetListEntry(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					_group.getGroupId(), RandomTestUtil.randomString(),
					AssetListEntryTypeConstants.TYPE_MANUAL,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

			User user = TestPropsValues.getUser();

			SegmentsEntry segmentsEntry1 = _addSegmentsEntryByFirstName(
				_group.getGroupId(), user.getFirstName());

			AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
				_group.getGroupId(), assetListEntry,
				segmentsEntry1.getSegmentsEntryId());

			JournalArticle journalArticle1 = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			AssetEntry assetEntry1 = _getAssetEntry(journalArticle1);

			AssetListTestUtil.addAssetListEntryAssetEntryRel(
				_group.getGroupId(), assetEntry1, assetListEntry,
				segmentsEntry1.getSegmentsEntryId(), 0);

			SegmentsEntry segmentsEntry2 = _addSegmentsEntryByLastName(
				_group.getGroupId(), user.getLastName());

			AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
				_group.getGroupId(), assetListEntry,
				segmentsEntry2.getSegmentsEntryId());

			JournalArticle journalArticle2 = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			AssetEntry assetEntry2 = _getAssetEntry(journalArticle2);

			AssetListTestUtil.addAssetListEntryAssetEntryRel(
				_group.getGroupId(), assetEntry2, assetListEntry,
				segmentsEntry2.getSegmentsEntryId(), 0);

			JournalArticle journalArticle3 = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			AssetEntry assetEntry3 = _getAssetEntry(journalArticle3);

			AssetListTestUtil.addAssetListEntryAssetEntryRel(
				_group.getGroupId(), assetEntry3, assetListEntry,
				segmentsEntry2.getSegmentsEntryId(), 1);

			InfoPage<AssetEntry> infoPage =
				_assetListAssetEntryProvider.getAssetEntriesInfoPage(
					assetListEntry,
					new long[] {
						segmentsEntry1.getSegmentsEntryId(),
						segmentsEntry2.getSegmentsEntryId()
					},
					null, null, StringPool.BLANK, StringPool.BLANK, 0, 2);

			Assert.assertEquals(3, infoPage.getTotalCount());
			Assert.assertTrue(
				ListUtil.exists(
					infoPage.getPageItems(),
					assetEntry ->
						assetEntry.getEntryId() == assetEntry1.getEntryId()));
			Assert.assertTrue(
				ListUtil.exists(
					infoPage.getPageItems(),
					assetEntry ->
						assetEntry.getEntryId() == assetEntry2.getEntryId()));
			Assert.assertFalse(
				ListUtil.exists(
					infoPage.getPageItems(),
					assetEntry ->
						assetEntry.getEntryId() == assetEntry3.getEntryId()));

			_assertAssetListEntryResultsPagination(
				assetListEntry,
				new long[] {
					segmentsEntry1.getSegmentsEntryId(),
					segmentsEntry2.getSegmentsEntryId()
				},
				_getAssetEntry(journalArticle1),
				_getAssetEntry(journalArticle2),
				_getAssetEntry(journalArticle3));
		}
	}

	@Test
	public void testCombineSegmentsEntriesOfManualCollectionWithoutDuplications()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.asset.list.internal.configuration." +
						"AssetListConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"combineAssetsFromAllSegmentsManual", true
					).build())) {

			AssetListEntry assetListEntry =
				_assetListEntryLocalService.addAssetListEntry(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					_group.getGroupId(), RandomTestUtil.randomString(),
					AssetListEntryTypeConstants.TYPE_MANUAL,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));

			User user = TestPropsValues.getUser();

			SegmentsEntry segmentsEntry1 = _addSegmentsEntryByFirstName(
				_group.getGroupId(), user.getFirstName());

			AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
				_group.getGroupId(), assetListEntry,
				segmentsEntry1.getSegmentsEntryId());

			SegmentsEntry segmentsEntry2 = _addSegmentsEntryByLastName(
				_group.getGroupId(), user.getLastName());

			AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
				_group.getGroupId(), assetListEntry,
				segmentsEntry2.getSegmentsEntryId());

			for (int i = 0; i < 4; i++) {
				JournalArticle journalArticle = JournalTestUtil.addArticle(
					_group.getGroupId(),
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

				AssetEntry assetEntry = _getAssetEntry(journalArticle);

				AssetListTestUtil.addAssetListEntryAssetEntryRel(
					_group.getGroupId(), assetEntry, assetListEntry,
					segmentsEntry1.getSegmentsEntryId());
				AssetListTestUtil.addAssetListEntryAssetEntryRel(
					_group.getGroupId(), assetEntry, assetListEntry,
					segmentsEntry2.getSegmentsEntryId());
			}

			_assertAssetListEntryResults(
				_assetListAssetEntryProvider.getAssetEntriesInfoPage(
					assetListEntry,
					new long[] {
						segmentsEntry1.getSegmentsEntryId(),
						segmentsEntry2.getSegmentsEntryId()
					},
					null, null, StringPool.BLANK, StringPool.BLANK,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				4);
		}
	}

	@Test
	@TestInfo("LPD-89770")
	public void testGetAssetEntryQuery() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		DepotEntryGroupRel depotEntryGroupRel =
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				_depotEntry.getDepotEntryId(), _group.getGroupId());

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			true
		).put(
			"groupIds", String.valueOf(_depotEntry.getGroupId())
		).build();

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				unicodeProperties.toString(), _serviceContext);

		AssetEntryQuery assetEntryQuery =
			_assetListAssetEntryProvider.getAssetEntryQuery(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				StringUtil.toHexString(TestPropsValues.getUserId()));

		Assert.assertArrayEquals(
			new long[] {_depotEntry.getGroupId()},
			assetEntryQuery.getGroupIds());

		_depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
			depotEntryGroupRel);

		assetEntryQuery = _assetListAssetEntryProvider.getAssetEntryQuery(
			assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
			StringUtil.toHexString(TestPropsValues.getUserId()));

		Assert.assertArrayEquals(new long[0], assetEntryQuery.getGroupIds());
	}

	@Test
	public void testGetAssetQueryWithMostRelevantLocale() throws Exception {
		Group group = GroupTestUtil.addGroup();

		GroupTestUtil.updateDisplaySettings(
			group.getGroupId(), Arrays.asList(LocaleUtil.US, LocaleUtil.SPAIN),
			LocaleUtil.US);

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, group.getGroupId(),
				_read("data_definition.json"), TestPropsValues.getUser());

		JournalTestUtil.addArticleWithXMLContent(
			group.getGroupId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT,
			_read("journal_article_content.xml"),
			dataDefinition.getDataDefinitionKey(), null, LocaleUtil.US);

		Locale originalThemeDisplayLocale =
			LocaleThreadLocal.getThemeDisplayLocale();

		try {
			long userId = TestPropsValues.getUserId();

			UnicodeProperties unicodeProperties =
				UnicodePropertiesBuilder.create(
					true
				).put(
					"anyAssetType",
					String.valueOf(_portal.getClassNameId(JournalArticle.class))
				).put(
					"anyClassTypeJournalArticleAssetRendererFactory",
					dataDefinition.getId()
				).put(
					"classNameIds", JournalArticle.class.getName()
				).put(
					"classTypeIdsJournalArticleAssetRendererFactory",
					dataDefinition.getId()
				).put(
					"ddmStructureDisplayFieldValue", true
				).put(
					"ddmStructureFieldName", "Checkbox21871963"
				).put(
					"ddmStructureFieldValue", true
				).put(
					"groupIds", String.valueOf(group.getGroupId())
				).build();

			AssetListEntry assetListEntry =
				_assetListEntryLocalService.addAssetListEntry(
					RandomTestUtil.randomString(), userId, group.getGroupId(),
					RandomTestUtil.randomString(),
					AssetListEntryTypeConstants.TYPE_DYNAMIC,
					unicodeProperties.toString(), _serviceContext);

			LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.US);

			AssetEntryQuery assetEntryQuery =
				_assetListAssetEntryProvider.getAssetEntryQuery(
					assetListEntry,
					new long[] {SegmentsEntryConstants.ID_DEFAULT},
					StringUtil.toHexString(userId));

			String ddmStructureFieldName = (String)assetEntryQuery.getAttribute(
				"ddmStructureFieldName");

			Assert.assertTrue(
				StringUtil.endsWith(ddmStructureFieldName, "en_US"));

			LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.SPAIN);

			assetEntryQuery = _assetListAssetEntryProvider.getAssetEntryQuery(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				StringUtil.toHexString(userId));

			ddmStructureFieldName = (String)assetEntryQuery.getAttribute(
				"ddmStructureFieldName");

			Assert.assertTrue(
				StringUtil.endsWith(ddmStructureFieldName, "es_ES"));
		}
		finally {
			LocaleThreadLocal.setThemeDisplayLocale(originalThemeDisplayLocale);
			GroupTestUtil.deleteGroup(group);
		}
	}

	@Test
	public void testGetDynamicAssetEntriesByContainsAllKeywords()
		throws Exception {

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Apple Fruit",
			RandomTestUtil.randomString());
		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Banana Fruit",
			RandomTestUtil.randomString());
		JournalArticle journalArticle3 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Orange Fruit",
			RandomTestUtil.randomString());

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				_getTypeSettings(
					new AssetQueryRule(
						true, false, "keywords", new String[] {"Fruit"})),
				_serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null, null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			3, _getAssetEntry(journalArticle1), _getAssetEntry(journalArticle2),
			_getAssetEntry(journalArticle3));

		assetListEntry = _assetListEntryLocalService.addAssetListEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), RandomTestUtil.randomString(),
			AssetListEntryTypeConstants.TYPE_DYNAMIC,
			_getTypeSettings(
				new AssetQueryRule(
					true, true, "keywords", new String[] {"Apple", "Fruit"})),
			_serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[0][], null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			1, _getAssetEntry(journalArticle1));
	}

	@Test
	public void testGetDynamicAssetEntriesByContainsAnyKeywords()
		throws Exception {

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Apple Fruit",
			RandomTestUtil.randomString());

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Banana Fruit",
			RandomTestUtil.randomString());

		JournalArticle journalArticle3 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Orange Fruit",
			RandomTestUtil.randomString());

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				_getTypeSettings(
					new AssetQueryRule(
						true, false, "keywords", new String[] {"Apple"})),
				_serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null, null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			1, _getAssetEntry(journalArticle1));

		assetListEntry = _assetListEntryLocalService.addAssetListEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), RandomTestUtil.randomString(),
			AssetListEntryTypeConstants.TYPE_DYNAMIC,
			_getTypeSettings(
				new AssetQueryRule(
					true, false, "keywords", new String[] {"Apple", "Orange"})),
			_serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[0][], null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			2, _getAssetEntry(journalArticle1),
			_getAssetEntry(journalArticle3));
	}

	@Test
	public void testGetDynamicAssetEntriesByDoesNotContainsAllKeywords()
		throws Exception {

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Apple Fruit",
			RandomTestUtil.randomString());
		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Banana Fruit",
			RandomTestUtil.randomString());
		JournalArticle journalArticle3 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Orange Fruit",
			RandomTestUtil.randomString());

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				_getTypeSettings(
					new AssetQueryRule(
						false, true, "keywords", new String[] {"Apple"})),
				_serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null, null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			2, _getAssetEntry(journalArticle2),
			_getAssetEntry(journalArticle3));

		assetListEntry = _assetListEntryLocalService.addAssetListEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), RandomTestUtil.randomString(),
			AssetListEntryTypeConstants.TYPE_DYNAMIC,
			_getTypeSettings(
				new AssetQueryRule(
					false, true, "keywords", new String[] {"Apple", "Orange"})),
			_serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[0][], null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			3, _getAssetEntry(journalArticle1), _getAssetEntry(journalArticle2),
			_getAssetEntry(journalArticle3));
	}

	@Test
	public void testGetDynamicAssetEntriesByDoesNotContainsAnyKeywords()
		throws Exception {

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Apple Fruit",
			RandomTestUtil.randomString());

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Banana Fruit",
			RandomTestUtil.randomString());
		JournalArticle journalArticle3 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Orange Fruit",
			RandomTestUtil.randomString());

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				_getTypeSettings(
					new AssetQueryRule(
						false, false, "keywords", new String[] {"Apple"})),
				_serviceContext);

		InfoPage<AssetEntry> infoPage =
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null, null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		_assertAssetListEntryResults(
			infoPage, 2, _getAssetEntry(journalArticle2),
			_getAssetEntry(journalArticle3));

		assetListEntry = _assetListEntryLocalService.addAssetListEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), RandomTestUtil.randomString(),
			AssetListEntryTypeConstants.TYPE_DYNAMIC,
			_getTypeSettings(
				new AssetQueryRule(
					false, false, "keywords",
					new String[] {"Apple", "Orange"})),
			_serviceContext);

		infoPage = _assetListAssetEntryProvider.getAssetEntriesInfoPage(
			assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
			new long[0][], null, StringPool.BLANK,
			String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		_assertAssetListEntryResults(
			infoPage, 1, _getAssetEntry(journalArticle2));
	}

	@Test
	public void testGetDynamicAssetEntriesByKeywords() throws Exception {
		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "title1",
			RandomTestUtil.randomString());
		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "title2",
			RandomTestUtil.randomString());
		JournalArticle journalArticle3 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "title3",
			RandomTestUtil.randomString());

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC, _serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null, null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			3, _getAssetEntry(journalArticle1), _getAssetEntry(journalArticle2),
			_getAssetEntry(journalArticle3));
		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[0][], null, "title1",
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			1, _getAssetEntry(journalArticle1));
	}

	@Test
	public void testGetDynamicAssetEntriesMatchingAllAssetCategories()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());
		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle1 = _addJournalArticle(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});
		JournalArticle journalArticle2 = _addJournalArticle(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		AssetCategory assetCategory3 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_addJournalArticle(new long[] {assetCategory3.getCategoryId()});

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC, _serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[][] {
					{assetCategory1.getCategoryId()},
					{assetCategory2.getCategoryId()}
				},
				null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			2, _getAssetEntry(journalArticle1),
			_getAssetEntry(journalArticle2));
	}

	@Test
	public void testGetDynamicAssetEntriesMatchingAnyAssetCategories()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle1 = _addJournalArticle(
			new long[] {assetCategory1.getCategoryId()});

		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle2 = _addJournalArticle(
			new long[] {assetCategory2.getCategoryId()});

		AssetCategory assetCategory3 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_addJournalArticle(new long[] {assetCategory3.getCategoryId()});

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC, _serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[][] {
					{
						assetCategory1.getCategoryId(),
						assetCategory2.getCategoryId()
					}
				},
				null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			2, _getAssetEntry(journalArticle1),
			_getAssetEntry(journalArticle2));
	}

	@Test
	public void testGetDynamicAssetEntriesMatchingOneAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle = _addJournalArticle(
			new long[] {assetCategory1.getCategoryId()});

		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_addJournalArticle(new long[] {assetCategory2.getCategoryId()});

		AssetCategory assetCategory3 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_addJournalArticle(new long[] {assetCategory3.getCategoryId()});

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC, _serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[][] {{assetCategory1.getCategoryId()}}, null,
				StringPool.BLANK, String.valueOf(TestPropsValues.getUserId()),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			1, _getAssetEntry(journalArticle));
	}

	@Test
	public void testGetDynamicAssetEntriesNonmatchingAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_addJournalArticle(new long[] {assetCategory1.getCategoryId()});

		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_addJournalArticle(new long[] {assetCategory2.getCategoryId()});

		AssetCategory assetCategory3 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_addJournalArticle(new long[] {assetCategory3.getCategoryId()});

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC, _serviceContext);

		AssetCategory assetCategory4 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[][] {{assetCategory4.getCategoryId()}}, null,
				StringPool.BLANK, String.valueOf(TestPropsValues.getUserId()),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			0);
	}

	@Test
	public void testGetDynamicAssetEntriesWithAnyClassNameIds()
		throws Exception {

		_blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		DLAppTestUtil.addFileEntryWithWorkflow(
			TestPropsValues.getUserId(), _group.getGroupId(), 0,
			StringPool.BLANK, RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"anyAssetType", true
				).put(
					"groupIds", String.valueOf(_group.getGroupId())
				).buildString(),
				_serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null, null, StringPool.BLANK, StringPool.BLANK,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			3);
	}

	@Test
	public void testGetDynamicAssetEntriesWithMultipleClassNameIds()
		throws Exception {

		_blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		DLAppTestUtil.addFileEntryWithWorkflow(
			TestPropsValues.getUserId(), _group.getGroupId(), 0,
			StringPool.BLANK, RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"anyAssetType", false
				).put(
					"classNameIds",
					StringUtil.merge(
						new long[] {
							_portal.getClassNameId(BlogsEntry.class.getName()),
							_portal.getClassNameId(DLFileEntry.class.getName()),
							_portal.getClassNameId(
								JournalArticle.class.getName())
						})
				).put(
					"classTypeIdsDLFileEntryAssetRendererFactory",
					String.valueOf(
						DLFileEntryTypeConstants.
							FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT)
				).put(
					"classTypeIdsJournalArticleAssetRendererFactory",
					String.valueOf(journalArticle.getDDMStructureId())
				).put(
					"groupIds", String.valueOf(_group.getGroupId())
				).buildString(),
				_serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null, null, StringPool.BLANK, StringPool.BLANK,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			3);
	}

	@Test
	public void testGetDynamicAssetEntriesWithSegmentsEntryNotPrioritized()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle = _addJournalArticle(
			new long[] {assetCategory1.getCategoryId()});

		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_addJournalArticle(new long[] {assetCategory2.getCategoryId()});

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				_getTypeSettings(
					new AssetQueryRule(
						true, false, "assetCategories",
						new String[] {
							String.valueOf(assetCategory1.getCategoryId())
						})),
				_serviceContext);

		User user = TestPropsValues.getUser();

		SegmentsEntry segmentsEntry = _addSegmentsEntryByLastName(
			_group.getGroupId(), user.getLastName());

		AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
			_group.getGroupId(), assetListEntry,
			segmentsEntry.getSegmentsEntryId(),
			_getTypeSettings(
				new AssetQueryRule(
					true, false, "assetCategories",
					new String[] {
						String.valueOf(assetCategory2.getCategoryId())
					})));

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry,
				new long[] {
					SegmentsEntryConstants.ID_DEFAULT,
					segmentsEntry.getSegmentsEntryId()
				},
				new long[0][], null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			1, _getAssetEntry(journalArticle));
	}

	@Test
	public void testGetDynamicAssetEntriesWithSegmentsEntryPrioritized()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_addJournalArticle(new long[] {assetCategory1.getCategoryId()});

		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle = _addJournalArticle(
			new long[] {assetCategory2.getCategoryId()});

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC,
				_getTypeSettings(
					new AssetQueryRule(
						true, false, "assetCategories",
						new String[] {
							String.valueOf(assetCategory1.getCategoryId())
						})),
				_serviceContext);

		User user = TestPropsValues.getUser();

		SegmentsEntry segmentsEntry = _addSegmentsEntryByLastName(
			_group.getGroupId(), user.getLastName());

		AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
			_group.getGroupId(), assetListEntry,
			segmentsEntry.getSegmentsEntryId(),
			_getTypeSettings(
				new AssetQueryRule(
					true, false, "assetCategories",
					new String[] {
						String.valueOf(assetCategory2.getCategoryId())
					})));

		_assetListEntrySegmentsEntryRelLocalService.updateVariationsPriority(
			new long[] {
				_getAssetListEntrySegmentsEntryRelId(
					assetListEntry.getAssetListEntryId(),
					segmentsEntry.getSegmentsEntryId()),
				_getAssetListEntrySegmentsEntryRelId(
					assetListEntry.getAssetListEntryId(),
					SegmentsEntryConstants.ID_DEFAULT)
			});

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry,
				new long[] {
					SegmentsEntryConstants.ID_DEFAULT,
					segmentsEntry.getSegmentsEntryId()
				},
				new long[0][], null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			1, _getAssetEntry(journalArticle));
	}

	@Test
	public void testGetManualAssetEntries() throws Exception {
		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, _serviceContext);

		AssetEntry assetEntry1 = _getAssetEntry(journalArticle1);

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, _serviceContext);

		AssetEntry assetEntry2 = _getAssetEntry(journalArticle2);

		JournalArticle journalArticle3 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, _serviceContext);

		AssetEntry assetEntry3 = _getAssetEntry(journalArticle3);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		long[] assetEntryIds = {
			assetEntry1.getEntryId(), assetEntry2.getEntryId(),
			assetEntry3.getEntryId()
		};

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(), assetEntryIds,
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null, null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			3, _getAssetEntry(journalArticle1), _getAssetEntry(journalArticle2),
			_getAssetEntry(journalArticle3));
	}

	@Test
	public void testGetManualAssetEntriesByKeywords() throws Exception {
		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "title1",
			RandomTestUtil.randomString());

		AssetEntry assetEntry1 = _getAssetEntry(journalArticle1);

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "title2",
			RandomTestUtil.randomString());

		AssetEntry assetEntry2 = _getAssetEntry(journalArticle2);

		JournalArticle journalArticle3 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "title3",
			RandomTestUtil.randomString());

		AssetEntry assetEntry3 = _getAssetEntry(journalArticle3);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		long[] assetEntryIds = {
			assetEntry1.getEntryId(), assetEntry2.getEntryId(),
			assetEntry3.getEntryId()
		};

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(), assetEntryIds,
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				null, null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			3, _getAssetEntry(journalArticle1), _getAssetEntry(journalArticle2),
			_getAssetEntry(journalArticle3));

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[0][], null, "title1",
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			1, _getAssetEntry(journalArticle1));
	}

	@Test
	public void testGetManualAssetEntriesFromDifferentGroups()
		throws Exception {

		Group group1 = GroupTestUtil.addGroup();
		Group group2 = GroupTestUtil.addGroup();

		try {
			JournalArticle journalArticle1 = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				_serviceContext);

			AssetEntry assetEntry1 = _getAssetEntry(journalArticle1);

			JournalArticle journalArticle2 = JournalTestUtil.addArticle(
				group1.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				_serviceContext);

			AssetEntry assetEntry2 = _getAssetEntry(journalArticle2);

			JournalArticle journalArticle3 = JournalTestUtil.addArticle(
				group2.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				_serviceContext);

			AssetEntry assetEntry3 = _getAssetEntry(journalArticle3);

			AssetListEntry assetListEntry =
				_assetListEntryLocalService.addAssetListEntry(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					_group.getGroupId(), RandomTestUtil.randomString(),
					AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

			_assetListEntryLocalService.addAssetEntrySelections(
				assetListEntry.getAssetListEntryId(),
				new long[] {
					assetEntry1.getEntryId(), assetEntry2.getEntryId(),
					assetEntry3.getEntryId()
				},
				SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

			_assertAssetListEntryResults(
				_assetListAssetEntryProvider.getAssetEntriesInfoPage(
					assetListEntry,
					new long[] {SegmentsEntryConstants.ID_DEFAULT}, null, null,
					StringPool.BLANK,
					String.valueOf(TestPropsValues.getUserId()),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				3, _getAssetEntry(journalArticle1),
				_getAssetEntry(journalArticle2),
				_getAssetEntry(journalArticle3));
		}
		finally {
			GroupTestUtil.deleteGroup(group1);
			GroupTestUtil.deleteGroup(group2);
		}
	}

	@Test
	public void testGetManualAssetEntriesMatchingAllAssetCategories()
		throws Exception {

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group companyGroup = company.getGroup();

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());

		try {
			_assertGetManualAssetEntriesMatchingAllAssetCategories(
				assetVocabulary);
		}
		finally {
			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
		}
	}

	@Test
	public void testGetManualAssetEntriesMatchingAllAssetCategoriesInGlobalSite()
		throws Exception {

		_assertGetManualAssetEntriesMatchingAllAssetCategories(
			AssetTestUtil.addVocabulary(_group.getGroupId()));
	}

	@Test
	public void testGetManualAssetEntriesMatchingAnyAssetCategories()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle1 = _addJournalArticle(
			new long[] {assetCategory1.getCategoryId()});

		AssetEntry assetEntry1 = _getAssetEntry(journalArticle1);

		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle2 = _addJournalArticle(
			new long[] {assetCategory2.getCategoryId()});

		AssetEntry assetEntry2 = _getAssetEntry(journalArticle2);

		AssetCategory assetCategory3 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle3 = _addJournalArticle(
			new long[] {assetCategory3.getCategoryId()});

		AssetEntry assetEntry3 = _getAssetEntry(journalArticle3);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		long[] assetEntryIds = {
			assetEntry1.getEntryId(), assetEntry2.getEntryId(),
			assetEntry3.getEntryId()
		};

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(), assetEntryIds,
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[][] {
					{
						assetCategory1.getCategoryId(),
						assetCategory2.getCategoryId()
					}
				},
				null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			2, _getAssetEntry(journalArticle1),
			_getAssetEntry(journalArticle2));
	}

	@Test
	public void testGetManualAssetEntriesMatchingOneAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle1 = _addJournalArticle(
			new long[] {assetCategory1.getCategoryId()});

		AssetEntry assetEntry1 = _getAssetEntry(journalArticle1);

		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle2 = _addJournalArticle(
			new long[] {assetCategory2.getCategoryId()});

		AssetEntry assetEntry2 = _getAssetEntry(journalArticle2);

		AssetCategory assetCategory3 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle3 = _addJournalArticle(
			new long[] {assetCategory3.getCategoryId()});

		AssetEntry assetEntry3 = _getAssetEntry(journalArticle3);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		long[] assetEntryIds = {
			assetEntry1.getEntryId(), assetEntry2.getEntryId(),
			assetEntry3.getEntryId()
		};

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(), assetEntryIds,
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[][] {{assetCategory1.getCategoryId()}}, null,
				StringPool.BLANK, String.valueOf(TestPropsValues.getUserId()),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			1, _getAssetEntry(journalArticle1));
	}

	@Test
	public void testGetManualAssetEntriesNonmatchingAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle1 = _addJournalArticle(
			new long[] {assetCategory1.getCategoryId()});

		AssetEntry assetEntry1 = _getAssetEntry(journalArticle1);

		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle2 = _addJournalArticle(
			new long[] {assetCategory2.getCategoryId()});

		AssetEntry assetEntry2 = _getAssetEntry(journalArticle2);

		AssetCategory assetCategory3 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle3 = _addJournalArticle(
			new long[] {assetCategory3.getCategoryId()});

		AssetEntry assetEntry3 = _getAssetEntry(journalArticle3);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		long[] assetEntryIds = {
			assetEntry1.getEntryId(), assetEntry2.getEntryId(),
			assetEntry3.getEntryId()
		};

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(), assetEntryIds,
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		AssetCategory assetCategory4 = AssetTestUtil.addCategory(
			_group.getGroupId(), assetVocabulary.getVocabularyId());

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[][] {{assetCategory4.getCategoryId()}}, null,
				StringPool.BLANK, String.valueOf(TestPropsValues.getUserId()),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			0);
	}

	@Test
	public void testGetManualAssetEntriesWithSegmentsEntryNotPrioritized()
		throws Exception {

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		AssetEntry assetEntry1 = _getAssetEntry(journalArticle1);

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry1.getEntryId()},
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		User user = TestPropsValues.getUser();

		SegmentsEntry segmentsEntry = _addSegmentsEntryByLastName(
			_group.getGroupId(), user.getLastName());

		AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
			_group.getGroupId(), assetListEntry,
			segmentsEntry.getSegmentsEntryId(), null);

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry2 = _getAssetEntry(journalArticle2);

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry2.getEntryId()},
			segmentsEntry.getSegmentsEntryId(), _serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry,
				new long[] {
					SegmentsEntryConstants.ID_DEFAULT,
					segmentsEntry.getSegmentsEntryId()
				},
				new long[0][], null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			1, assetEntry1);
	}

	@Test
	public void testGetManualAssetEntriesWithSegmentsEntryPrioritized()
		throws Exception {

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		AssetEntry assetEntry1 = _getAssetEntry(journalArticle1);

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry1.getEntryId()},
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		User user = TestPropsValues.getUser();

		SegmentsEntry segmentsEntry1 = _addSegmentsEntryByLastName(
			_group.getGroupId(), user.getLastName());

		AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
			_group.getGroupId(), assetListEntry,
			segmentsEntry1.getSegmentsEntryId(), null);

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry2 = _getAssetEntry(journalArticle2);

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry2.getEntryId()},
			segmentsEntry1.getSegmentsEntryId(), _serviceContext);

		SegmentsEntry segmentsEntry2 = _addSegmentsEntryByFirstName(
			_group.getGroupId(), user.getFirstName());

		AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
			_group.getGroupId(), assetListEntry,
			segmentsEntry2.getSegmentsEntryId(), null);

		JournalArticle journalArticle3 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry3 = _getAssetEntry(journalArticle3);

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(),
			new long[] {assetEntry3.getEntryId()},
			segmentsEntry2.getSegmentsEntryId(), _serviceContext);

		_assetListEntrySegmentsEntryRelLocalService.updateVariationsPriority(
			new long[] {
				_getAssetListEntrySegmentsEntryRelId(
					assetListEntry.getAssetListEntryId(),
					segmentsEntry2.getSegmentsEntryId()),
				_getAssetListEntrySegmentsEntryRelId(
					assetListEntry.getAssetListEntryId(),
					segmentsEntry1.getSegmentsEntryId()),
				_getAssetListEntrySegmentsEntryRelId(
					assetListEntry.getAssetListEntryId(),
					SegmentsEntryConstants.ID_DEFAULT)
			});

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry,
				new long[] {
					SegmentsEntryConstants.ID_DEFAULT,
					segmentsEntry1.getSegmentsEntryId(),
					segmentsEntry2.getSegmentsEntryId()
				},
				new long[0][], null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			1, assetEntry3);
	}

	@Test
	public void testNotCombineSegmentsEntriesOfDynamicCollection()
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_DYNAMIC, null,
				_serviceContext);

		User userTest = TestPropsValues.getUser();

		String userName = "RandomName";

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			StringPool.BLANK, userName + "@liferay.com", userName,
			LocaleUtil.getDefault(), userName, RandomTestUtil.randomString(),
			null, ServiceContextTestUtil.getServiceContext());

		SegmentsEntry segmentsEntry1 = _addSegmentsEntryByFirstName(
			_group.getGroupId(), userTest.getFirstName());
		SegmentsEntry segmentsEntry2 = _addSegmentsEntryByFirstName(
			_group.getGroupId(), user.getFirstName());

		JournalArticle journalArticle = _addJournalArticle(
			new long[0], TestPropsValues.getUserId());

		_addJournalArticle(new long[0], TestPropsValues.getUserId());
		_addJournalArticle(new long[0], user.getUserId());

		long[] segmentsEntryIds = {
			segmentsEntry1.getSegmentsEntryId(),
			segmentsEntry2.getSegmentsEntryId()
		};

		AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
			_group.getGroupId(), assetListEntry,
			segmentsEntry1.getSegmentsEntryId(),
			_getTypeSettings(userTest.getFirstName()));

		AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
			_group.getGroupId(), assetListEntry,
			segmentsEntry2.getSegmentsEntryId(), _getTypeSettings(userName));

		InfoPage<AssetEntry> infoPage =
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, segmentsEntryIds, null, null, StringPool.BLANK,
				StringPool.BLANK, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		_assertAssetListEntryResults(
			infoPage, 2, _getAssetEntry(journalArticle));
	}

	@Test
	public void testNotCombineSegmentsEntriesOfManualCollection()
		throws Exception {

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		User user = TestPropsValues.getUser();

		SegmentsEntry segmentsEntry1 = _addSegmentsEntryByFirstName(
			_group.getGroupId(), user.getFirstName());

		AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
			_group.getGroupId(), assetListEntry,
			segmentsEntry1.getSegmentsEntryId());

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry1 = _getAssetEntry(journalArticle1);

		AssetListTestUtil.addAssetListEntryAssetEntryRel(
			_group.getGroupId(), assetEntry1, assetListEntry,
			segmentsEntry1.getSegmentsEntryId(), 0);

		SegmentsEntry segmentsEntry2 = _addSegmentsEntryByLastName(
			_group.getGroupId(), user.getLastName());

		AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
			_group.getGroupId(), assetListEntry,
			segmentsEntry2.getSegmentsEntryId());

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry2 = _getAssetEntry(journalArticle2);

		AssetListTestUtil.addAssetListEntryAssetEntryRel(
			_group.getGroupId(), assetEntry2, assetListEntry,
			segmentsEntry2.getSegmentsEntryId(), 0);

		JournalArticle journalArticle3 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		AssetEntry assetEntry3 = _getAssetEntry(journalArticle3);

		AssetListTestUtil.addAssetListEntryAssetEntryRel(
			_group.getGroupId(), assetEntry3, assetListEntry,
			segmentsEntry2.getSegmentsEntryId(), 1);

		InfoPage<AssetEntry> infoPage =
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry,
				new long[] {
					segmentsEntry1.getSegmentsEntryId(),
					segmentsEntry2.getSegmentsEntryId()
				},
				null, null, StringPool.BLANK, StringPool.BLANK, 0, 2);

		Assert.assertEquals(1, infoPage.getTotalCount());
		Assert.assertTrue(
			ListUtil.exists(
				infoPage.getPageItems(),
				assetEntry ->
					assetEntry.getEntryId() == assetEntry1.getEntryId()));
		Assert.assertFalse(
			ListUtil.exists(
				infoPage.getPageItems(),
				assetEntry ->
					assetEntry.getEntryId() == assetEntry2.getEntryId()));
		Assert.assertFalse(
			ListUtil.exists(
				infoPage.getPageItems(),
				assetEntry ->
					assetEntry.getEntryId() == assetEntry3.getEntryId()));
	}

	private JournalArticle _addJournalArticle(long[] assetCategoryIds)
		throws Exception {

		return _addJournalArticle(
			assetCategoryIds, TestPropsValues.getUserId());
	}

	private JournalArticle _addJournalArticle(
			long[] assetCategoryIds, long userId)
		throws Exception {

		return JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), userId, assetCategoryIds));
	}

	private SegmentsEntry _addSegmentsEntry(long groupId, String filterString)
		throws Exception {

		Criteria criteria = new Criteria();

		_segmentsCriteriaContributor.contribute(
			criteria, filterString, Criteria.Conjunction.AND);

		return SegmentsTestUtil.addSegmentsEntry(
			groupId, CriteriaSerializer.serialize(criteria));
	}

	private SegmentsEntry _addSegmentsEntryByFirstName(
			long groupId, String firstName)
		throws Exception {

		return _addSegmentsEntry(
			groupId, String.format("(firstName eq '%s')", firstName));
	}

	private SegmentsEntry _addSegmentsEntryByLastName(
			long groupId, String lastName)
		throws Exception {

		return _addSegmentsEntry(
			groupId, String.format("(lastName eq '%s')", lastName));
	}

	private void _assertAssetListEntryResults(
		InfoPage<AssetEntry> infoPage, int expectedTotalCount,
		AssetEntry... expectedAssetEntries) {

		Assert.assertEquals(expectedTotalCount, infoPage.getTotalCount());

		List<AssetEntry> assetEntries =
			(List<AssetEntry>)infoPage.getPageItems();

		for (AssetEntry expectedAssetEntry : expectedAssetEntries) {
			Assert.assertTrue(assetEntries.contains(expectedAssetEntry));
		}
	}

	private void _assertAssetListEntryResultsPagination(
		AssetListEntry assetListEntry, long[] segmentsEntryIds,
		AssetEntry... expectedAssetEntries) {

		InfoPage<AssetEntry> infoPage =
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, segmentsEntryIds, null, null, StringPool.BLANK,
				StringPool.BLANK, 0, 2);

		List<AssetEntry> assetEntries =
			(List<AssetEntry>)infoPage.getPageItems();

		infoPage = _assetListAssetEntryProvider.getAssetEntriesInfoPage(
			assetListEntry, segmentsEntryIds, null, null, StringPool.BLANK,
			StringPool.BLANK, 2, 4);

		assetEntries.addAll(infoPage.getPageItems());

		Assert.assertEquals(
			assetEntries.toString(), expectedAssetEntries.length,
			assetEntries.size());

		for (AssetEntry expectedAssetEntry : expectedAssetEntries) {
			Assert.assertTrue(assetEntries.contains(expectedAssetEntry));
		}
	}

	private void _assertGetManualAssetEntriesMatchingAllAssetCategories(
			AssetVocabulary assetVocabulary)
		throws Exception {

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			assetVocabulary.getGroupId(), assetVocabulary.getVocabularyId());
		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			assetVocabulary.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle1 = _addJournalArticle(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		AssetEntry assetEntry1 = _getAssetEntry(journalArticle1);

		JournalArticle journalArticle2 = _addJournalArticle(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		AssetEntry assetEntry2 = _getAssetEntry(journalArticle2);

		AssetCategory assetCategory3 = AssetTestUtil.addCategory(
			assetVocabulary.getGroupId(), assetVocabulary.getVocabularyId());

		JournalArticle journalArticle3 = _addJournalArticle(
			new long[] {assetCategory3.getCategoryId()});

		AssetEntry assetEntry3 = _getAssetEntry(journalArticle3);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.addAssetListEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				AssetListEntryTypeConstants.TYPE_MANUAL, _serviceContext);

		long[] assetEntryIds = {
			assetEntry1.getEntryId(), assetEntry2.getEntryId(),
			assetEntry3.getEntryId()
		};

		_assetListEntryLocalService.addAssetEntrySelections(
			assetListEntry.getAssetListEntryId(), assetEntryIds,
			SegmentsEntryConstants.ID_DEFAULT, _serviceContext);

		_assertAssetListEntryResults(
			_assetListAssetEntryProvider.getAssetEntriesInfoPage(
				assetListEntry, new long[] {SegmentsEntryConstants.ID_DEFAULT},
				new long[][] {
					{assetCategory1.getCategoryId()},
					{assetCategory2.getCategoryId()}
				},
				null, StringPool.BLANK,
				String.valueOf(TestPropsValues.getUserId()), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			2, _getAssetEntry(journalArticle1),
			_getAssetEntry(journalArticle2));
	}

	private AssetEntry _getAssetEntry(JournalArticle journalArticle)
		throws Exception {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				JournalArticle.class.getName());

		return assetRendererFactory.getAssetEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());
	}

	private long _getAssetListEntrySegmentsEntryRelId(
		long assetListEntryId, long segmentsEntryId) {

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			_assetListEntrySegmentsEntryRelLocalService.
				fetchAssetListEntrySegmentsEntryRel(
					assetListEntryId, segmentsEntryId);

		return assetListEntrySegmentsEntryRel.
			getAssetListEntrySegmentsEntryRelId();
	}

	private String _getTypeSettings(AssetQueryRule... assetQueryRules) {
		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			true
		).put(
			"anyAssetType",
			String.valueOf(_portal.getClassNameId(JournalArticle.class))
		).put(
			"classNameIds", JournalArticle.class.getName()
		).put(
			"groupIds", String.valueOf(_group.getGroupId())
		).put(
			"orderByColumn1", Field.MODIFIED_DATE
		).put(
			"orderByColumn2", "title"
		).put(
			"orderByType1", "ASC"
		).put(
			"orderByType2", "ASC"
		).build();

		for (int i = 0; i < assetQueryRules.length; i++) {
			AssetQueryRule assetQueryRule = assetQueryRules[i];

			unicodeProperties.putAll(
				HashMapBuilder.put(
					"queryAndOperator" + i,
					String.valueOf(assetQueryRule.isAndOperator())
				).put(
					"queryContains" + i,
					String.valueOf(assetQueryRule.isContains())
				).put(
					"queryName" + i, assetQueryRule.getName()
				).put(
					"queryValues" + i,
					StringUtil.merge(assetQueryRule.getValues())
				).build());
		}

		return unicodeProperties.toString();
	}

	private String _getTypeSettings(String queryValue) {
		return UnicodePropertiesBuilder.create(
			true
		).put(
			"anyAssetType",
			String.valueOf(_portal.getClassNameId(JournalArticle.class))
		).put(
			"classNameIds", JournalArticle.class.getName()
		).put(
			"groupIds", String.valueOf(_group.getGroupId())
		).put(
			"orderByColumn1", Field.MODIFIED_DATE
		).put(
			"orderByColumn2", "title"
		).put(
			"orderByType1", "ASC"
		).put(
			"orderByType2", "ASC"
		).put(
			"queryContains0", "true"
		).put(
			"queryName0", "keywords"
		).put(
			"queryValues0", queryValue
		).buildString();
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	@Inject
	private AssetListAssetEntryProvider _assetListAssetEntryProvider;

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _segmentsCriteriaContributor;

	private ServiceContext _serviceContext;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

}