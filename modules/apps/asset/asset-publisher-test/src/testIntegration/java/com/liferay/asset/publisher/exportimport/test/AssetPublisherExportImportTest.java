/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.asset.publisher.test.util.AssetPublisherTestUtil;
import com.liferay.asset.publisher.util.AssetEntryResult;
import com.liferay.asset.publisher.util.AssetPublisherHelper;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.controller.ExportImportController;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.portlet.MockPortletRequest;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.display.template.test.util.BaseExportImportTestCase;

import jakarta.portlet.PortletPreferences;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * Tests the export and import behavior of the Asset Publisher bundle with
 * different types of assets.
 *
 * @author Julio Camarero
 */
@RunWith(Arquillian.class)
public class AssetPublisherExportImportTest extends BaseExportImportTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_assetPublisherWebConfiguration = _configurationAdmin.getConfiguration(
			"com.liferay.asset.publisher.web.internal.configuration." +
				"AssetPublisherWebConfiguration",
			StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_assetPublisherWebConfiguration,
			HashMapDictionaryBuilder.<String, Object>put(
				"dynamicExportEnabled", true
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			_assetPublisherWebConfiguration);
	}

	@Override
	public String getPortletId() throws Exception {
		return PortletIdCodec.encode(
			AssetPublisherPortletKeys.ASSET_PUBLISHER,
			RandomTestUtil.randomString());
	}

	@Before
	@Override
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		super.setUp();

		_permissionChecker = PermissionCheckerFactoryUtil.create(
			TestPropsValues.getUser());
	}

	@Test
	public void testAnyDLFileEntryType() throws Exception {
		long dlFileEntryClassNameId = _portal.getClassNameId(DLFileEntry.class);

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"anyAssetType",
				new String[] {String.valueOf(dlFileEntryClassNameId)}
			).put(
				"anyClassTypeDLFileEntryAssetRendererFactory",
				new String[] {String.valueOf(Boolean.TRUE)}
			).build());

		long anyAssetType = GetterUtil.getLong(
			portletPreferences.getValue("anyAssetType", null));

		Assert.assertEquals(dlFileEntryClassNameId, anyAssetType);

		String anyClassTypeDLFileEntryAssetRendererFactory =
			portletPreferences.getValue(
				"anyClassTypeDLFileEntryAssetRendererFactory", null);

		Assert.assertEquals(
			anyClassTypeDLFileEntryAssetRendererFactory,
			String.valueOf(Boolean.TRUE));
	}

	@Test
	public void testAnyJournalStructure() throws Exception {
		long journalArticleClassNameId = _portal.getClassNameId(
			JournalArticle.class);

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"anyAssetType",
				new String[] {String.valueOf(journalArticleClassNameId)}
			).put(
				"anyClassTypeJournalArticleAssetRendererFactory",
				new String[] {String.valueOf(Boolean.TRUE)}
			).build());

		long anyAssetType = GetterUtil.getLong(
			portletPreferences.getValue("anyAssetType", null));

		Assert.assertEquals(journalArticleClassNameId, anyAssetType);

		String anyClassTypeDLFileEntryAssetRendererFactory =
			portletPreferences.getValue(
				"anyClassTypeJournalArticleAssetRendererFactory", null);

		Assert.assertEquals(
			anyClassTypeDLFileEntryAssetRendererFactory,
			String.valueOf(Boolean.TRUE));
	}

	@Test
	public void testAssetCategories() throws Exception {
		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			group.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			group.getGroupId(), assetVocabulary.getVocabularyId());

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"queryName0", new String[] {"assetCategories"}
			).put(
				"queryValues0",
				new String[] {String.valueOf(assetCategory.getCategoryId())}
			).build());

		long importedAssetCategoryId = GetterUtil.getLong(
			portletPreferences.getValue("queryValues0", null));

		Assert.assertNotEquals(importedAssetCategoryId, 0L);

		Assert.assertNotEquals(
			assetCategory.getCategoryId(), importedAssetCategoryId);

		AssetCategory importedAssetCategory =
			_assetCategoryLocalService.fetchAssetCategory(
				importedAssetCategoryId);

		Assert.assertEquals(
			assetCategory.getUuid(), importedAssetCategory.getUuid());
	}

	@Test
	public void testChildLayoutScopeIds() throws Exception {
		Group childGroup = GroupTestUtil.addGroup(group.getGroupId());

		Map<String, String[]> preferenceMap = HashMapBuilder.put(
			"scopeIds",
			new String[] {
				AssetPublisherHelper.SCOPE_ID_CHILD_GROUP_PREFIX +
					childGroup.getGroupId()
			}
		).build();

		try {
			PortletPreferences portletPreferences =
				getImportedPortletPreferences(preferenceMap);

			Assert.assertEquals(
				null, portletPreferences.getValue("scopeId", null));
			Assert.assertEquals(
				AssetPublisherHelper.SCOPE_ID_GROUP_PREFIX +
					childGroup.getGroupId(),
				portletPreferences.getValue("scopeIds", null));
		}
		finally {
			_groupLocalService.deleteGroup(childGroup);
		}
	}

	@Test
	public void testDisplayStyle() throws Exception {
		String displayStyle = RandomTestUtil.randomString();

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"displayStyle", new String[] {displayStyle}
			).build());

		Assert.assertEquals(
			displayStyle, portletPreferences.getValue("displayStyle", null));
		Assert.assertTrue(
			"The display style should not be null",
			Validator.isNotNull(
				portletPreferences.getValue("displayStyle", null)));
	}

	@Test
	public void testDynamicExportImportAssetCategoryContainsFiltering()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			group.getGroupId(), assetVocabulary.getVocabularyId());
		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			group.getGroupId(), assetVocabulary.getVocabularyId());

		BlogsEntry blogsEntry1 = _addBlogsEntry(assetCategory1.getCategoryId());
		BlogsEntry blogsEntry2 = _addBlogsEntry(
			assetCategory1.getCategoryId(), assetCategory2.getCategoryId());

		testDynamicExportImport(
			HashMapBuilder.put(
				"queryAndOperator0", new String[] {Boolean.TRUE.toString()}
			).put(
				"queryContains0", new String[] {Boolean.TRUE.toString()}
			).put(
				"queryName0", new String[] {"assetCategories"}
			).put(
				"queryValues0",
				new String[] {String.valueOf(assetCategory1.getCategoryId())}
			).build(),
			Arrays.asList(
				getAssetEntry(blogsEntry1), getAssetEntry(blogsEntry2)),
			true);
	}

	@Test
	public void testDynamicExportImportAssetCategoryDoesNotContainsFiltering()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			group.getGroupId(), assetVocabulary.getVocabularyId());
		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			group.getGroupId(), assetVocabulary.getVocabularyId());

		JournalTestUtil.addArticle(
			group.getGroupId(), 0,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId(),
				new long[] {assetCategory1.getCategoryId()}));

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			group.getGroupId(), 0,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId(),
				new long[] {
					assetCategory1.getCategoryId(),
					assetCategory2.getCategoryId()
				}));

		testDynamicExportImport(
			HashMapBuilder.put(
				"queryAndOperator0", new String[] {Boolean.TRUE.toString()}
			).put(
				"queryContains0", new String[] {Boolean.FALSE.toString()}
			).put(
				"queryName0", new String[] {"assetCategories"}
			).put(
				"queryValues0",
				new String[] {
					String.valueOf(assetCategory1.getCategoryId()),
					String.valueOf(assetCategory2.getCategoryId())
				}
			).build(),
			Collections.singletonList(getAssetEntry(journalArticle2)), false);
	}

	@Test
	public void testDynamicExportImportAssetTagFiltering() throws Exception {
		AssetTag assetTag = AssetTestUtil.addTag(group.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAssetTagNames(new String[] {assetTag.getName()});

		List<AssetEntry> expectedAssetEntries = addAssetEntries(
			group, 2, new ArrayList<>(), serviceContext);

		testDynamicExportImport(
			HashMapBuilder.put(
				"queryContains0", new String[] {Boolean.TRUE.toString()}
			).put(
				"queryValues0", new String[] {assetTag.getName()}
			).build(),
			expectedAssetEntries, true);
	}

	@Test
	public void testDynamicExportImportAssetVocabularyFiltering()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			group.getGroupId());

		AssetCategory assetCategory1 = AssetTestUtil.addCategory(
			group.getGroupId(), assetVocabulary.getVocabularyId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory1.getCategoryId()});

		List<AssetEntry> expectedAssetEntries = addAssetEntries(
			group, 1, new ArrayList<>(), serviceContext);

		AssetCategory assetCategory2 = AssetTestUtil.addCategory(
			group.getGroupId(), assetVocabulary.getVocabularyId());

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory2.getCategoryId()});

		expectedAssetEntries = addAssetEntries(
			group, 1, expectedAssetEntries, serviceContext);

		testDynamicExportImport(
			HashMapBuilder.put(
				"assetVocabularyId",
				new String[] {String.valueOf(assetVocabulary.getVocabularyId())}
			).build(),
			expectedAssetEntries, true);
	}

	@Test
	public void testDynamicExportImportClassTypeFiltering() throws Exception {
		List<AssetEntry> expectedAssetEntries = new ArrayList<>();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(100),
			ServiceContextTestUtil.getServiceContext());

		expectedAssetEntries.add(getAssetEntry(journalArticle));

		testDynamicExportImport(
			HashMapBuilder.put(
				"anyAssetType",
				new String[] {
					String.valueOf(_portal.getClassNameId(JournalArticle.class))
				}
			).put(
				"classTypeIds",
				() -> {
					DDMStructure ddmStructure =
						journalArticle.getDDMStructure();

					return new String[] {
						String.valueOf(ddmStructure.getStructureId())
					};
				}
			).build(),
			expectedAssetEntries, true);
	}

	@Test
	public void testDynamicExportImportLayoutFiltering() throws Exception {
		List<AssetEntry> expectedAssetEntries = new ArrayList<>();

		Map<Locale, String> titleMap = HashMapBuilder.put(
			LocaleUtil.getDefault(), RandomTestUtil.randomString()
		).build();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			group.getGroupId(), JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, titleMap, titleMap,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString(100)
			).build(),
			layout.getUuid(), LocaleUtil.getDefault(), null, false, false,
			ServiceContextTestUtil.getServiceContext());

		expectedAssetEntries.add(getAssetEntry(journalArticle));

		testDynamicExportImport(
			HashMapBuilder.put(
				"showOnlyLayoutAssets", new String[] {Boolean.TRUE.toString()}
			).build(),
			expectedAssetEntries, true);
	}

	@Test
	public void testDynamicExportImportOtherClassNameFiltering()
		throws Exception {

		testDynamicExportImport(
			HashMapBuilder.put(
				"anyAssetType",
				new String[] {
					String.valueOf(_portal.getClassNameId(DLFileEntry.class))
				}
			).build(),
			new ArrayList<>(), true);
	}

	@Test
	public void testDynamicExportImportWithAssetEntriesInTrash()
		throws Exception {

		JournalFolder journalFolder = JournalTestUtil.addFolder(
			group.getGroupId(), RandomTestUtil.randomString());

		JournalTestUtil.addArticle(
			group.getGroupId(), journalFolder.getFolderId());

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false,
			new ServiceContext());

		String scopeId = _assetPublisherHelper.getScopeId(
			group, group.getGroupId());

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		portletPreferences.setValue("scopeIds", scopeId);
		portletPreferences.setValue("selectionStyle", "dynamic");

		AssetEntryQuery assetEntryQuery =
			_assetPublisherHelper.getAssetEntryQuery(
				portletPreferences, group.getGroupId(), layout, null, null);

		SearchContainer<AssetEntry> searchContainer = new SearchContainer<>();

		searchContainer.setResultsAndTotal(Collections::emptyList, 10);

		List<AssetEntryResult> actualAssetEntryResults =
			_assetPublisherHelper.getAssetEntryResults(
				searchContainer, assetEntryQuery, layout, portletPreferences,
				StringPool.BLANK, null, null, TestPropsValues.getCompanyId(),
				group.getGroupId(), TestPropsValues.getUserId(),
				assetEntryQuery.getClassNameIds(), null);

		AssetEntryResult assetEntryResult = actualAssetEntryResults.get(0);

		List<AssetEntry> actualAssetEntries =
			assetEntryResult.getAssetEntries();

		Assert.assertEquals(
			actualAssetEntries.toString(), 2, actualAssetEntries.size());

		Group stagingGroup = group.getStagingGroup();

		JournalFolder stagingJournalFolder =
			_journalFolderLocalService.fetchJournalFolderByUuidAndGroupId(
				journalFolder.getUuid(), stagingGroup.getGroupId());

		_journalFolderLocalService.moveFolderToTrash(
			TestPropsValues.getUserId(), stagingJournalFolder.getFolderId());

		_publishLayouts(stagingGroup);

		Assert.assertTrue(
			ListUtil.isEmpty(
				_assetPublisherHelper.getAssetEntryResults(
					searchContainer, assetEntryQuery, layout,
					portletPreferences, StringPool.BLANK, null, null,
					TestPropsValues.getCompanyId(), group.getGroupId(),
					TestPropsValues.getUserId(),
					assetEntryQuery.getClassNameIds(), null)));
	}

	@Test
	public void testDynamicExportImportWithNoFiltering() throws Exception {
		List<AssetEntry> expectedAssetEntries = addAssetEntries(
			group, 2, new ArrayList<>(),
			ServiceContextTestUtil.getServiceContext());

		testDynamicExportImport(new HashMap<>(), expectedAssetEntries, false);
	}

	@Test
	public void testExportImportAssetEntries() throws Exception {
		testExportImportAssetEntries(group);
	}

	@Override
	@Test
	public void testExportImportAssetLinks() throws Exception {
	}

	@Test
	public void testExportImportAssetListEntryWithDifferentScope()
		throws Exception {

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false,
			new ServiceContext());

		Group stagingGroup = group.getStagingGroup();

		Layout stagingLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout.getUuid(), stagingGroup.getGroupId(),
			layout.isPrivateLayout());

		AssetListEntry assetListEntry = _addAssetListEntry(importedGroup);

		String portletId = LayoutTestUtil.addPortletToLayout(
			stagingLayout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			_getPreferenceMap(
				assetListEntry.getExternalReferenceCode(),
				importedGroup.getExternalReferenceCode()));

		_publishLayouts(stagingGroup);

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPreferences(
				_portletPreferencesFactory.getPortletPreferencesIds(
					layout.getCompanyId(), layout.getGroupId(), 0,
					layout.getPlid(), portletId));

		Assert.assertEquals(
			assetListEntry.getExternalReferenceCode(),
			portletPreferences.getValue(
				"assetListEntryExternalReferenceCode", null));
		Assert.assertNull(
			portletPreferences.getValue("assetListEntryId", null));
		Assert.assertEquals(
			importedGroup.getExternalReferenceCode(),
			portletPreferences.getValue(
				"assetListEntryGroupExternalReferenceCode", null));
	}

	@Test
	public void testExportImportAssetListEntryWithNoSelection()
		throws Exception {

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false,
			new ServiceContext());

		Group stagingGroup = group.getStagingGroup();

		Layout stagingLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout.getUuid(), stagingGroup.getGroupId(),
			layout.isPrivateLayout());

		String portletId = LayoutTestUtil.addPortletToLayout(
			stagingLayout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			_getPreferenceMap(null, null));

		_publishLayouts(stagingGroup);

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPreferences(
				_portletPreferencesFactory.getPortletPreferencesIds(
					layout.getCompanyId(), layout.getGroupId(), 0,
					layout.getPlid(), portletId));

		Assert.assertNull(
			portletPreferences.getValue(
				"assetListEntryExternalReferenceCode", null));
		Assert.assertNull(
			portletPreferences.getValue("assetListEntryId", null));
		Assert.assertNull(
			portletPreferences.getValue(
				"assetListEntryGroupExternalReferenceCode", null));
	}

	@Test
	public void testExportImportAssetListEntryWithSameScope() throws Exception {
		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false,
			new ServiceContext());

		Group stagingGroup = group.getStagingGroup();

		Layout stagingLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout.getUuid(), stagingGroup.getGroupId(),
			layout.isPrivateLayout());

		AssetListEntry stagingAssetListEntry = _addAssetListEntry(group);

		String portletId = LayoutTestUtil.addPortletToLayout(
			stagingLayout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			_getPreferenceMap(
				stagingAssetListEntry.getExternalReferenceCode(), null));

		_publishLayouts(stagingGroup);

		AssetListEntry assetListEntry =
			_assetListEntryService.getAssetListEntryByUuidAndGroupId(
				stagingAssetListEntry.getUuid(), group.getGroupId());

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPreferences(
				_portletPreferencesFactory.getPortletPreferencesIds(
					layout.getCompanyId(), layout.getGroupId(), 0,
					layout.getPlid(), portletId));

		Assert.assertEquals(
			assetListEntry.getExternalReferenceCode(),
			portletPreferences.getValue(
				"assetListEntryExternalReferenceCode", null));
		Assert.assertNull(
			portletPreferences.getValue("assetListEntryId", null));
		Assert.assertNull(
			portletPreferences.getValue(
				"assetListEntryGroupExternalReferenceCode", null));
	}

	@Test
	public void testExportImportLayoutScopedAssetEntries() throws Exception {
		Group layoutGroup = GroupTestUtil.addGroup(
			TestPropsValues.getUserId(), layout);

		testExportImportAssetEntries(layoutGroup);
	}

	@Test
	public void testExportImportSeveralScopedAssetEntries() throws Exception {
		List<Group> groups = new ArrayList<>();

		Company company = _companyLocalService.getCompany(
			layout.getCompanyId());

		Group companyGroup = company.getGroup();

		groups.add(companyGroup);

		groups.add(group);

		Group group2 = GroupTestUtil.addGroup();

		groups.add(group2);

		Group layoutGroup1 = GroupTestUtil.addGroup(
			TestPropsValues.getUserId(), layout);

		groups.add(layoutGroup1);

		Layout layout2 = LayoutTestUtil.addTypePortletLayout(group);

		Group layoutGroup2 = GroupTestUtil.addGroup(
			TestPropsValues.getUserId(), layout2);

		groups.add(layoutGroup2);

		testExportImportAssetEntries(groups);
	}

	@Test
	public void testGlobalScopeId() throws Exception {
		Company company = _companyLocalService.getCompany(
			layout.getCompanyId());

		Group companyGroup = company.getGroup();

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"scopeIds",
				new String[] {
					AssetPublisherHelper.SCOPE_ID_GROUP_PREFIX +
						companyGroup.getGroupId()
				}
			).build());

		Assert.assertEquals(
			AssetPublisherHelper.SCOPE_ID_GROUP_PREFIX +
				companyGroup.getGroupId(),
			portletPreferences.getValue("scopeIds", null));
		Assert.assertEquals(null, portletPreferences.getValue("scopeId", null));
	}

	@Test
	public void testLayoutScopeId() throws Exception {
		GroupTestUtil.addGroup(TestPropsValues.getUserId(), layout);

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"scopeIds",
				new String[] {
					AssetPublisherHelper.SCOPE_ID_LAYOUT_UUID_PREFIX +
						layout.getUuid()
				}
			).build());

		Assert.assertEquals(
			AssetPublisherHelper.SCOPE_ID_LAYOUT_UUID_PREFIX +
				importedLayout.getUuid(),
			portletPreferences.getValue("scopeIds", null));
		Assert.assertEquals(null, portletPreferences.getValue("scopeId", null));
	}

	@Test
	public void testLegacyLayoutScopeId() throws Exception {
		GroupTestUtil.addGroup(TestPropsValues.getUserId(), layout);

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"scopeIds",
				new String[] {
					AssetPublisherHelper.SCOPE_ID_LAYOUT_PREFIX +
						layout.getLayoutId()
				}
			).build());

		Assert.assertEquals(
			AssetPublisherHelper.SCOPE_ID_LAYOUT_UUID_PREFIX +
				importedLayout.getUuid(),
			portletPreferences.getValue("scopeIds", null));
		Assert.assertEquals(null, portletPreferences.getValue("scopeId", null));
	}

	@Test
	public void testOneDLFileEntryType() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), DLFileEntryMetadata.class.getName());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		DLFileEntryType dlFileEntryType = addDLFileEntryType(
			group.getGroupId(), ddmStructure.getStructureId(), serviceContext);

		serviceContext.setUuid(ddmStructure.getUuid());

		DDMStructure importedDDMStructure = DDMStructureTestUtil.addStructure(
			importedGroup.getGroupId(), DLFileEntryMetadata.class.getName(), 0,
			ddmStructure.getDDMForm(), LocaleUtil.getDefault(), serviceContext);

		serviceContext.setUuid(dlFileEntryType.getUuid());

		DLFileEntryType importedDLFileEntryType = addDLFileEntryType(
			importedGroup.getGroupId(), importedDDMStructure.getStructureId(),
			serviceContext);

		long dlFileEntryClassNameId = _portal.getClassNameId(DLFileEntry.class);

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"anyAssetType",
				new String[] {String.valueOf(dlFileEntryClassNameId)}
			).put(
				"anyClassTypeDLFileEntryAssetRendererFactory",
				new String[] {
					String.valueOf(dlFileEntryType.getFileEntryTypeId())
				}
			).put(
				"classTypeIds",
				new String[] {
					String.valueOf(dlFileEntryType.getFileEntryTypeId())
				}
			).build());

		long anyClassTypeDLFileEntryAssetRendererFactory = GetterUtil.getLong(
			portletPreferences.getValue(
				"anyClassTypeDLFileEntryAssetRendererFactory", null));

		Assert.assertEquals(
			anyClassTypeDLFileEntryAssetRendererFactory,
			importedDLFileEntryType.getFileEntryTypeId());

		long anyAssetType = GetterUtil.getLong(
			portletPreferences.getValue("anyAssetType", null));

		Assert.assertEquals(dlFileEntryClassNameId, anyAssetType);

		long classTypeIds = GetterUtil.getLong(
			portletPreferences.getValue("classTypeIds", null));

		Assert.assertEquals(
			importedDLFileEntryType.getFileEntryTypeId(), classTypeIds);
	}

	@Test
	public void testOneJournalStructure() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), JournalArticle.class.getName());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setUuid(ddmStructure.getUuid());

		DDMStructure importedDDMStructure = DDMStructureTestUtil.addStructure(
			importedGroup.getGroupId(), JournalArticle.class.getName(), 0,
			ddmStructure.getDDMForm(), LocaleUtil.getDefault(), serviceContext);

		long journalArticleClassNameId = _portal.getClassNameId(
			JournalArticle.class);

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"anyAssetType",
				new String[] {String.valueOf(journalArticleClassNameId)}
			).put(
				"anyClassTypeJournalArticleAssetRendererFactory",
				new String[] {String.valueOf(ddmStructure.getStructureId())}
			).put(
				"classTypeIds",
				new String[] {String.valueOf(ddmStructure.getStructureId())}
			).build());

		long anyClassTypeJournalArticleAssetRendererFactory =
			GetterUtil.getLong(
				portletPreferences.getValue(
					"anyClassTypeJournalArticleAssetRendererFactory", null));

		Assert.assertEquals(
			anyClassTypeJournalArticleAssetRendererFactory,
			importedDDMStructure.getStructureId());

		long anyAssetType = GetterUtil.getLong(
			portletPreferences.getValue("anyAssetType", null));

		Assert.assertEquals(journalArticleClassNameId, anyAssetType);

		long classTypeIds = GetterUtil.getLong(
			portletPreferences.getValue("classTypeIds", null));

		Assert.assertEquals(
			importedDDMStructure.getStructureId(), classTypeIds);
	}

	@Test
	public void testSeveralDLFileEntryTypes() throws Exception {
		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			group.getGroupId(), DLFileEntryMetadata.class.getName());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId());

		DLFileEntryType dlFileEntryType1 = addDLFileEntryType(
			group.getGroupId(), ddmStructure1.getStructureId(), serviceContext);

		serviceContext.setUuid(ddmStructure1.getUuid());

		DDMStructure importedDDMStructure1 = DDMStructureTestUtil.addStructure(
			importedGroup.getGroupId(), DLFileEntryMetadata.class.getName(), 0,
			ddmStructure1.getDDMForm(), LocaleUtil.getDefault(),
			serviceContext);

		serviceContext.setUuid(dlFileEntryType1.getUuid());

		DLFileEntryType importedDLFileEntryType1 = addDLFileEntryType(
			importedGroup.getGroupId(), importedDDMStructure1.getStructureId(),
			serviceContext);

		DDMStructure ddmStructure2 = DDMStructureTestUtil.addStructure(
			group.getGroupId(), DLFileEntryMetadata.class.getName());

		serviceContext.setUuid(null);

		DLFileEntryType dlFileEntryType2 = addDLFileEntryType(
			group.getGroupId(), ddmStructure2.getStructureId(), serviceContext);

		serviceContext.setUuid(ddmStructure2.getUuid());

		DDMStructure importedDDMStructure2 = DDMStructureTestUtil.addStructure(
			importedGroup.getGroupId(), DLFileEntryMetadata.class.getName(), 0,
			ddmStructure2.getDDMForm(), LocaleUtil.getDefault(),
			serviceContext);

		serviceContext.setUuid(dlFileEntryType2.getUuid());

		DLFileEntryType importedDLFileEntryType2 = addDLFileEntryType(
			importedGroup.getGroupId(), importedDDMStructure2.getStructureId(),
			serviceContext);

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"anyAssetType",
				new String[] {
					String.valueOf(_portal.getClassNameId(DLFileEntry.class))
				}
			).put(
				"anyClassTypeDLFileEntryAssetRendererFactory",
				new String[] {String.valueOf(Boolean.FALSE)}
			).put(
				"classTypeIdsDLFileEntryAssetRendererFactory",
				new String[] {
					StringBundler.concat(
						dlFileEntryType1.getFileEntryTypeId(), StringPool.COMMA,
						dlFileEntryType2.getFileEntryTypeId())
				}
			).build());

		Assert.assertEquals(
			importedDLFileEntryType1.getFileEntryTypeId() + StringPool.COMMA +
				importedDLFileEntryType2.getFileEntryTypeId(),
			StringUtil.merge(
				portletPreferences.getValues(
					"classTypeIdsDLFileEntryAssetRendererFactory", null)));
	}

	@Test
	public void testSeveralJournalStructures() throws Exception {
		DDMStructure ddmStructure1 = DDMStructureTestUtil.addStructure(
			group.getGroupId(), JournalArticle.class.getName());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setUuid(ddmStructure1.getUuid());

		DDMStructure importedDDMStructure1 = DDMStructureTestUtil.addStructure(
			importedGroup.getGroupId(), JournalArticle.class.getName(), 0,
			ddmStructure1.getDDMForm(), LocaleUtil.getDefault(),
			serviceContext);

		DDMStructure ddmStructure2 = DDMStructureTestUtil.addStructure(
			group.getGroupId(), JournalArticle.class.getName());

		serviceContext.setUuid(ddmStructure2.getUuid());

		DDMStructure importedDDMStructure2 = DDMStructureTestUtil.addStructure(
			importedGroup.getGroupId(), JournalArticle.class.getName(), 0,
			ddmStructure1.getDDMForm(), LocaleUtil.getDefault(),
			serviceContext);

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"anyAssetType",
				new String[] {
					String.valueOf(_portal.getClassNameId(JournalArticle.class))
				}
			).put(
				"anyClassTypeJournalArticleAssetRendererFactory",
				new String[] {String.valueOf(Boolean.FALSE)}
			).put(
				"classTypeIdsJournalArticleAssetRendererFactory",
				new String[] {
					StringBundler.concat(
						ddmStructure1.getStructureId(), StringPool.COMMA,
						ddmStructure2.getStructureId())
				}
			).build());

		Assert.assertEquals(
			importedDDMStructure1.getStructureId() + StringPool.COMMA +
				importedDDMStructure2.getStructureId(),
			StringUtil.merge(
				portletPreferences.getValues(
					"classTypeIdsJournalArticleAssetRendererFactory", null)));
	}

	@Test
	public void testSeveralLayoutScopeIds() throws Exception {
		Company company = _companyLocalService.getCompany(
			layout.getCompanyId());

		Layout secondLayout = LayoutTestUtil.addTypePortletLayout(group);

		GroupTestUtil.addGroup(TestPropsValues.getUserId(), secondLayout);

		GroupTestUtil.addGroup(TestPropsValues.getUserId(), layout);

		Group companyGroup = company.getGroup();

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"scopeIds",
				new String[] {
					AssetPublisherHelper.SCOPE_ID_GROUP_PREFIX +
						companyGroup.getGroupId(),
					AssetPublisherHelper.SCOPE_ID_LAYOUT_UUID_PREFIX +
						layout.getUuid(),
					AssetPublisherHelper.SCOPE_ID_LAYOUT_UUID_PREFIX +
						secondLayout.getUuid()
				}
			).build());

		Layout importedSecondLayout =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				secondLayout.getUuid(), importedGroup.getGroupId(),
				importedLayout.isPrivateLayout());

		Assert.assertEquals(null, portletPreferences.getValue("scopeId", null));

		Assert.assertEquals(
			StringBundler.concat(
				AssetPublisherHelper.SCOPE_ID_GROUP_PREFIX,
				companyGroup.getGroupId(), StringPool.COMMA,
				AssetPublisherHelper.SCOPE_ID_LAYOUT_UUID_PREFIX,
				importedLayout.getUuid(), StringPool.COMMA,
				AssetPublisherHelper.SCOPE_ID_LAYOUT_UUID_PREFIX,
				importedSecondLayout.getUuid()),
			StringUtil.merge(portletPreferences.getValues("scopeIds", null)));
	}

	@Test
	public void testSeveralLegacyLayoutScopeIds() throws Exception {
		Layout secondLayout = LayoutTestUtil.addTypePortletLayout(group);

		GroupTestUtil.addGroup(TestPropsValues.getUserId(), secondLayout);

		GroupTestUtil.addGroup(TestPropsValues.getUserId(), layout);

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"scopeIds",
				new String[] {
					AssetPublisherHelper.SCOPE_ID_LAYOUT_PREFIX +
						layout.getLayoutId(),
					AssetPublisherHelper.SCOPE_ID_LAYOUT_PREFIX +
						secondLayout.getLayoutId()
				}
			).build());

		Layout importedSecondLayout =
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				secondLayout.getUuid(), importedGroup.getGroupId(),
				importedLayout.isPrivateLayout());

		Assert.assertEquals(null, portletPreferences.getValue("scopeId", null));

		Assert.assertEquals(
			StringBundler.concat(
				AssetPublisherHelper.SCOPE_ID_LAYOUT_UUID_PREFIX,
				importedLayout.getUuid(), StringPool.COMMA,
				AssetPublisherHelper.SCOPE_ID_LAYOUT_UUID_PREFIX,
				importedSecondLayout.getUuid()),
			StringUtil.merge(portletPreferences.getValues("scopeIds", null)));
	}

	@Test
	public void testSortByAssetVocabulary() throws Exception {
		testSortByAssetVocabulary(false);
	}

	@Test
	public void testSortByGlobalAssetVocabulary() throws Exception {
		testSortByAssetVocabulary(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected List<AssetEntry> addAssetEntries(
			Group group, int count, List<AssetEntry> assetEntries,
			ServiceContext serviceContext)
		throws Exception {

		for (int i = 0; i < count; i++) {
			JournalArticle journalArticle = JournalTestUtil.addArticle(
				group.getGroupId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(100), serviceContext);

			assetEntries.add(getAssetEntry(journalArticle));
		}

		return assetEntries;
	}

	protected DLFileEntryType addDLFileEntryType(
			long groupId, long ddmStructureId, ServiceContext serviceContext)
		throws Exception {

		return _dlFileEntryTypeLocalService.addFileEntryType(
			null, serviceContext.getUserId(), groupId, ddmStructureId, null,
			Collections.singletonMap(LocaleUtil.US, StringUtil.randomString()),
			Collections.singletonMap(LocaleUtil.US, StringUtil.randomString()),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
			serviceContext);
	}

	protected void assertAssetEntries(
		List<AssetEntry> expectedAssetEntries,
		List<AssetEntry> actualAssetEntries) {

		Assert.assertEquals(
			actualAssetEntries.toString(), expectedAssetEntries.size(),
			actualAssetEntries.size());

		Iterator<AssetEntry> expectedAssetEntriesIterator =
			expectedAssetEntries.iterator();
		Iterator<AssetEntry> actualAssetEntriesIterator =
			expectedAssetEntries.iterator();

		while (expectedAssetEntriesIterator.hasNext() &&
			   actualAssetEntriesIterator.hasNext()) {

			AssetEntry expectedAssetEntry = expectedAssetEntriesIterator.next();
			AssetEntry actualAssetEntry = actualAssetEntriesIterator.next();

			Assert.assertEquals(
				expectedAssetEntry.getClassName(),
				actualAssetEntry.getClassName());
			Assert.assertEquals(
				expectedAssetEntry.getClassUuid(),
				actualAssetEntry.getClassUuid());
		}
	}

	@Override
	protected void exportImportPortlet(String portletId) throws Exception {
		List<Layout> layouts = _layoutLocalService.getLayouts(
			layout.getGroupId(), layout.isPrivateLayout());

		User user = TestPropsValues.getUser();

		Map<String, Serializable> exportLayoutSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildExportLayoutSettingsMap(
					user, layout.getGroupId(), layout.isPrivateLayout(),
					ExportImportHelperUtil.getLayoutIds(layouts),
					getExportParameterMap());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					exportLayoutSettingsMap);

		larFile = _exportImportLocalService.exportLayoutsAsFile(
			exportImportConfiguration);

		// Import site LAR

		Map<String, Serializable> importLayoutSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildImportLayoutSettingsMap(
					user, importedGroup.getGroupId(), layout.isPrivateLayout(),
					null, getImportParameterMap());

		exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					user.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					importLayoutSettingsMap);

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, larFile);

		importedLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			layout.getUuid(), importedGroup.getGroupId(),
			layout.isPrivateLayout());
	}

	protected String[] getAssetEntriesXmls(List<AssetEntry> assetEntries) {
		String[] assetEntriesXmls = new String[assetEntries.size()];

		for (int i = 0; i < assetEntries.size(); i++) {
			assetEntriesXmls[i] = AssetPublisherTestUtil.getAssetEntryXml(
				assetEntries.get(i));
		}

		return assetEntriesXmls;
	}

	@Override
	protected Map<String, String[]> getExportParameterMap() throws Exception {
		return HashMapBuilder.put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).build();
	}

	protected long[] getGroupIdsFromScopeIds(String[] scopeIds, Layout layout)
		throws Exception {

		long[] groupIds = new long[scopeIds.length];

		for (int i = 0; i < scopeIds.length; i++) {
			groupIds[i] = _assetPublisherHelper.getGroupIdFromScopeId(
				scopeIds[i], layout.getGroupId(), layout.isPrivateLayout());
		}

		return groupIds;
	}

	@Override
	protected Map<String, String[]> getImportParameterMap() throws Exception {
		return getExportParameterMap();
	}

	protected void testDynamicExportImport(
			Map<String, String[]> preferenceMap,
			List<AssetEntry> expectedAssetEntries, boolean filtering)
		throws Exception {

		if (filtering) {

			// Creating entries to validate filtering

			addAssetEntries(
				group, 2, new ArrayList<>(),
				ServiceContextTestUtil.getServiceContext());
		}

		String scopeId = _assetPublisherHelper.getScopeId(
			group, group.getGroupId());

		preferenceMap.put("scopeIds", new String[] {scopeId});

		preferenceMap.put("selectionStyle", new String[] {"dynamic"});

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			preferenceMap);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		AssetEntryQuery assetEntryQuery =
			_assetPublisherHelper.getAssetEntryQuery(
				portletPreferences, importedGroup.getGroupId(), layout, null,
				null);

		SearchContainer<AssetEntry> searchContainer = new SearchContainer<>();

		searchContainer.setResultsAndTotal(Collections::emptyList, 10);

		List<AssetEntryResult> actualAssetEntryResults =
			_assetPublisherHelper.getAssetEntryResults(
				searchContainer, assetEntryQuery, layout, portletPreferences,
				StringPool.BLANK, null, null, company.getCompanyId(),
				importedGroup.getGroupId(), TestPropsValues.getUserId(),
				assetEntryQuery.getClassNameIds(), null);

		List<AssetEntry> actualAssetEntries = new ArrayList<>();

		for (AssetEntryResult assetEntryResult : actualAssetEntryResults) {
			actualAssetEntries.addAll(assetEntryResult.getAssetEntries());
		}

		assertAssetEntries(expectedAssetEntries, actualAssetEntries);
	}

	protected void testExportImportAssetEntries(Group scopeGroup)
		throws Exception {

		testExportImportAssetEntries(ListUtil.fromArray(scopeGroup));
	}

	protected void testExportImportAssetEntries(List<Group> scopeGroups)
		throws Exception {

		List<AssetEntry> assetEntries = new ArrayList<>();
		String[] scopeIds = new String[0];

		for (Group scopeGroup : scopeGroups) {
			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			if (scopeGroup.isLayout()) {

				// Creating structures and templates in layout scope group is
				// not possible

				Company company = _companyLocalService.getCompany(
					layout.getCompanyId());

				serviceContext.setAttribute("ddmGroupId", company.getGroupId());
			}

			assetEntries = addAssetEntries(
				scopeGroup, 3, assetEntries, serviceContext);

			scopeIds = ArrayUtil.append(
				scopeIds,
				_assetPublisherHelper.getScopeId(
					scopeGroup, group.getGroupId()));
		}

		PortletPreferences importedPortletPreferences =
			getImportedPortletPreferences(
				HashMapBuilder.put(
					"assetEntryXml", getAssetEntriesXmls(assetEntries)
				).put(
					"scopeIds", scopeIds
				).put(
					"selectionStyle", new String[] {"dynamic"}
				).build());

		String[] importedScopeIds = importedPortletPreferences.getValues(
			"scopeIds", null);

		long[] selectedGroupIds = getGroupIdsFromScopeIds(
			importedScopeIds, importedLayout);

		List<AssetEntry> actualAssetEntries =
			_assetPublisherHelper.getAssetEntries(
				new MockPortletRequest(), importedPortletPreferences,
				_permissionChecker, selectedGroupIds, false, false);

		assertAssetEntries(assetEntries, actualAssetEntries);
	}

	protected void testSortByAssetVocabulary(boolean globalVocabulary)
		throws Exception {

		long groupId = group.getGroupId();

		if (globalVocabulary) {
			Company company = _companyLocalService.getCompany(
				layout.getCompanyId());

			groupId = company.getGroupId();
		}

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), groupId,
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(groupId));

		PortletPreferences portletPreferences = getImportedPortletPreferences(
			HashMapBuilder.put(
				"assetVocabularyId",
				new String[] {String.valueOf(assetVocabulary.getVocabularyId())}
			).build());

		Assert.assertNotNull(
			"Portlet preference \"assetVocabularyId\" is null",
			portletPreferences.getValue("assetVocabularyId", null));

		long importedAssetVocabularyId = GetterUtil.getLong(
			portletPreferences.getValue("assetVocabularyId", null));

		AssetVocabulary importedVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(
				importedAssetVocabularyId);

		Assert.assertNotNull(
			"Vocabulary " + importedAssetVocabularyId + " does not exist",
			importedVocabulary);

		long expectedGroupId = groupId;

		if (!globalVocabulary) {
			expectedGroupId = importedGroup.getGroupId();
		}

		Assert.assertEquals(
			StringBundler.concat(
				"Vocabulary ", importedAssetVocabularyId,
				" does not belong to group ", expectedGroupId),
			expectedGroupId, importedVocabulary.getGroupId());

		_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);
	}

	private AssetListEntry _addAssetListEntry(Group group) throws Exception {
		return _assetListEntryService.addAssetListEntry(
			RandomTestUtil.randomString(), group.getGroupId(),
			RandomTestUtil.randomString(), 0,
			ServiceContextTestUtil.getServiceContext());
	}

	private BlogsEntry _addBlogsEntry(long... categoryIds) throws Exception {
		return _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), true, true,
			new String[0], StringPool.BLANK, null, null,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId(), categoryIds));
	}

	private Map<String, String[]> _getPreferenceMap(
		String assetListEntryExternalReferenceCode,
		String assetListEntryGroupExternalReferenceCode) {

		return HashMapBuilder.put(
			"assetListEntryExternalReferenceCode",
			() -> {
				if (Validator.isNull(assetListEntryExternalReferenceCode)) {
					return null;
				}

				return new String[] {assetListEntryExternalReferenceCode};
			}
		).put(
			"assetListEntryGroupExternalReferenceCode",
			() -> {
				if (Validator.isNull(
						assetListEntryGroupExternalReferenceCode)) {

					return null;
				}

				return new String[] {assetListEntryGroupExternalReferenceCode};
			}
		).put(
			"selectionStyle", new String[] {"asset-list"}
		).build();
	}

	private void _publishLayouts(Group stagingGroup) throws Exception {
		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()});

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			group.getGroupId(), false, parameterMap);
	}

	private static Configuration _assetPublisherWebConfiguration;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetListEntryService _assetListEntryService;

	@Inject
	private AssetPublisherHelper _assetPublisherHelper;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject(filter = "model.class.name=com.liferay.portal.kernel.model.Layout")
	private ExportImportController _exportImportController;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JournalFolderLocalService _journalFolderLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject(
		filter = "component.name=com.liferay.layout.admin.web.internal.exportimport.data.handler.LayoutStagedModelDataHandler"
	)
	private StagedModelDataHandler<?> _layoutStagedModelDataHandler;

	private PermissionChecker _permissionChecker;

	@Inject
	private Portal _portal;

	@Inject
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.internal.exportimport.data.handler.StagedGroupStagedModelDataHandler"
	)
	private StagedModelDataHandler<?> _stagedGroupStagedModelDataHandler;

	@Inject(
		filter = "component.name=com.liferay.layout.internal.exportimport.data.handler.StagedLayoutSetStagedModelDataHandler"
	)
	private StagedModelDataHandler<?> _stagedLayoutSetStagedModelDataHandler;

}