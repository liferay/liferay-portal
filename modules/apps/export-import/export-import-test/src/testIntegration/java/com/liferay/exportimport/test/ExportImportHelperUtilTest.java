/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.tags.constants.AssetTagsAdminPortletKeys;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.ExportImportGroup;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.ManifestSummary;
import com.liferay.exportimport.kernel.lar.MissingReference;
import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.test.util.TestUserIdStrategy;
import com.liferay.exportimport.test.util.vulcan.batch.engine.TestExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.repository.capabilities.ThumbnailCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.GenericPortlet;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiPredicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Zsolt Berentey
 * @author Péter Borkuti
 * @author Balázs Sáfrány-Kovalik
 */
@RunWith(Arquillian.class)
public class ExportImportHelperUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_liveGroup = GroupTestUtil.addGroup();
		_stagingGroup = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetChildGroupsCount() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		GroupTestUtil.addGroup(_liveGroup.getGroupId());

		_deactivateGroup(GroupTestUtil.addGroup(_liveGroup.getGroupId()));

		Assert.assertEquals(
			1, ExportImportHelperUtil.getChildGroupsCount(_liveGroup));
	}

	@Test
	public void testGetDataSiteAndInstanceLevelPortlets() throws Exception {
		List<Portlet> portlets =
			ExportImportHelperUtil.getDataSiteAndInstanceLevelPortlets(
				TestPropsValues.getCompanyId());

		for (Portlet portlet : portlets) {
			PortletDataHandler portletDataHandler =
				portlet.getPortletDataHandlerInstance();

			DataLevel portletDataLevel = portletDataHandler.getDataLevel();

			Assert.assertTrue(!portletDataLevel.equals(DataLevel.PORTAL));
		}
	}

	@Test
	public void testGetDataSiteAndInstanceLevelPortletsRank() throws Exception {
		List<Portlet> portlets =
			ExportImportHelperUtil.getDataSiteAndInstanceLevelPortlets(
				TestPropsValues.getCompanyId());

		Integer previousRank = null;

		for (Portlet portlet : portlets) {
			PortletDataHandler portletDataHandler =
				portlet.getPortletDataHandlerInstance();

			int actualRank = portletDataHandler.getRank();

			if (previousRank != null) {
				Assert.assertTrue(
					"Portlets should be in ascending order by rank",
					previousRank <= actualRank);
			}

			previousRank = actualRank;
		}
	}

	@Test
	@TestInfo("LPD-74703")
	public void testGetDataSiteLevelPortlet() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			ExportImportHelperUtilTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		long companyId1 = RandomTestUtil.randomLong();
		long companyId2 = RandomTestUtil.randomLong();

		String className = RandomTestUtil.randomString();

		BasePortletDataHandler portletDataHandler1 = new TestPortletDataHandler(
			new String[] {className}, true, DataLevel.SITE);
		BasePortletDataHandler portletDataHandler2 = new TestPortletDataHandler(
			new String[] {className}, false, DataLevel.SITE);

		String portletId1 = RandomTestUtil.randomString();
		String portletId2 = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable1 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId1), portletDataHandler1,
				portletId1);
			SafeCloseable safeCloseable2 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId1),
				new TestPortletDataHandler(
					new String[] {RandomTestUtil.randomString()}, true,
					DataLevel.SITE),
				RandomTestUtil.randomString());
			SafeCloseable safeCloseable3 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId2, RandomTestUtil.randomLong()),
				portletDataHandler2, portletId2);
			SafeCloseable safeCloseable4 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId2, RandomTestUtil.randomLong()),
				new TestPortletDataHandler(
					new String[] {className}, true, DataLevel.SITE),
				RandomTestUtil.randomString());
			SafeCloseable safeCloseable5 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId2, RandomTestUtil.randomLong()),
				new TestPortletDataHandler(
					new String[] {className}, true, DataLevel.SITE),
				RandomTestUtil.randomString());
			SafeCloseable safeCloseable6 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId2, RandomTestUtil.randomLong()),
				new TestPortletDataHandler(null, false, DataLevel.SITE),
				RandomTestUtil.randomString());
			SafeCloseable safeCloseable7 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId2),
				new TestPortletDataHandler(
					new String[] {className}, false,
					DataLevel.PORTLET_INSTANCE),
				RandomTestUtil.randomString());
			SafeCloseable safeCloseable8 = _registerWithSafeCloseable(
				bundleContext, List.of(companyId2),
				new TestPortletDataHandler(
					new String[] {RandomTestUtil.randomString()}, false,
					DataLevel.SITE),
				RandomTestUtil.randomString());
			SafeCloseable safeCloseable9 = _registerWithSafeCloseable(
				bundleContext,
				List.of(companyId1, RandomTestUtil.randomLong(), companyId2),
				null, RandomTestUtil.randomString())) {

			Portlet portlet = ExportImportHelperUtil.getDataSiteLevelPortlet(
				className, companyId1, false);

			Assert.assertEquals(portletId1, portlet.getRootPortletId());
			Assert.assertEquals(
				portletDataHandler1, portlet.getPortletDataHandlerInstance());

			portlet = ExportImportHelperUtil.getDataSiteLevelPortlet(
				className, companyId2, true);

			Assert.assertEquals(portletId2, portlet.getRootPortletId());
			Assert.assertEquals(
				portletDataHandler2, portlet.getPortletDataHandlerInstance());

			Assert.assertNull(
				ExportImportHelperUtil.getDataSiteLevelPortlet(
					RandomTestUtil.randomString(), companyId1, false));
			Assert.assertNull(
				ExportImportHelperUtil.getDataSiteLevelPortlet(
					RandomTestUtil.randomString(), companyId2, true));
		}
	}

	@Test
	public void testGetDataSiteLevelPortlets() throws Exception {
		List<Portlet> portlets =
			ExportImportHelperUtil.getDataSiteLevelPortlets(
				TestPropsValues.getCompanyId());

		for (Portlet portlet : portlets) {
			PortletDataHandler portletDataHandler =
				portlet.getPortletDataHandlerInstance();

			DataLevel portletDataLevel = portletDataHandler.getDataLevel();

			Assert.assertTrue(
				!(portletDataLevel.equals(DataLevel.PORTAL) ||
				  portletDataLevel.equals(DataLevel.PORTLET_INSTANCE)));
		}
	}

	@Test
	public void testGetDataSiteLevelPortletsRank() throws Exception {
		List<Portlet> portlets =
			ExportImportHelperUtil.getDataSiteLevelPortlets(
				TestPropsValues.getCompanyId());

		Integer previousRank = null;

		for (Portlet portlet : portlets) {
			PortletDataHandler portletDataHandler =
				portlet.getPortletDataHandlerInstance();

			int actualRank = portletDataHandler.getRank();

			if (previousRank != null) {
				Assert.assertTrue(previousRank <= actualRank);
			}

			previousRank = actualRank;
		}
	}

	@Test
	public void testGetExportPortletControlsMapAllConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = "test_portlet";

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				true
			).withPortletData(
				true
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getExportPortletControlsMap(
				companyId, portletId, parameterMap);

		_assertPortletControlsMap(
			actualPortletControlsMap, true, true, false, true, true);
	}

	@Test
	public void testGetExportPortletControlsMapNoConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = "test_portlet";

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap = builder.withPortletConfiguration(
			false
		).withPortletConfigurationAll(
			false
		).withPortletData(
			false
		).build();

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getExportPortletControlsMap(
				companyId, portletId, parameterMap);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetExportPortletControlsMapRootConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = JournalPortletKeys.JOURNAL;

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getExportPortletControlsMap(
				companyId, portletId, parameterMap);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetExportPortletControlsMapRootConfigurationWithPortletConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		String portletId = JournalPortletKeys.JOURNAL;

		String rootPortletId = PortletIdCodec.decodePortletName(portletId);

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				true
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS +
				StringPool.UNDERLINE + rootPortletId,
			new String[] {"true"});

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getExportPortletControlsMap(
				companyId, portletId, parameterMap);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetExportPortletControlsMapRootConfigurationWithPortletConfiguration2()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		String portletId = JournalPortletKeys.JOURNAL;

		String rootPortletId = PortletIdCodec.decodePortletName(portletId);

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				true
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION +
				StringPool.UNDERLINE + rootPortletId,
			new String[] {"true"});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS +
				StringPool.UNDERLINE + rootPortletId,
			new String[] {"true"});

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getExportPortletControlsMap(
				companyId, portletId, parameterMap);

		_assertPortletControlsMap(
			actualPortletControlsMap, true, true, false, false, false);
	}

	@Test
	@TestInfo("LPD-106614")
	public void testGetExportablePortlets() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			ExportImportHelperUtilTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_depotEntry = _addDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY);

		String portletId1 = RandomTestUtil.randomString();
		String portletId2 = RandomTestUtil.randomString();
		String portletId3 = RandomTestUtil.randomString();
		String portletId4 = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable1 = _registerWithSafeCloseable(
				bundleContext, List.of(), null, portletId1);
			SafeCloseable safeCloseable2 = _registerWithSafeCloseable(
				bundleContext, List.of(), null, portletId2);
			SafeCloseable safeCloseable3 = _registerWithSafeCloseable(
				bundleContext, List.of(), null, portletId3);
			SafeCloseable safeCloseable4 = _registerWithSafeCloseable(
				bundleContext, List.of(), null, portletId4);
			SafeCloseable safeCloseable5 =
				_registerTestExportImportVulcanBatchEngineTaskItemDelegate(
					bundleContext, portletId1,
					(companyId, groupId) -> _stagingGroupHelper.isDepotGroup(
						groupId));
			SafeCloseable safeCloseable6 =
				_registerTestExportImportVulcanBatchEngineTaskItemDelegate(
					bundleContext, portletId2,
					(companyId, groupId) -> !_stagingGroupHelper.isDepotGroup(
						groupId));
			SafeCloseable safeCloseable7 =
				_registerTestExportImportVulcanBatchEngineTaskItemDelegate(
					bundleContext, portletId3, (companyId, groupId) -> false);
			SafeCloseable safeCloseable8 =
				_registerTestExportImportVulcanBatchEngineTaskItemDelegate(
					bundleContext, portletId3, (companyId, groupId) -> true);
			SafeCloseable safeCloseable9 =
				_registerTestExportImportVulcanBatchEngineTaskItemDelegate(
					bundleContext, portletId4, (companyId, groupId) -> false)) {

			_assertRootPortletIds(
				List.of(portletId1, portletId2, portletId3, portletId4),
				List.of(),
				ExportImportHelperUtil.getDataSiteLevelPortlets(
					TestPropsValues.getCompanyId()));

			_assertRootPortletIds(
				List.of(portletId1, portletId3),
				List.of(portletId2, portletId4),
				ExportImportHelperUtil.getExportablePortlets(
					TestPropsValues.getCompanyId(), false,
					_depotEntry.getGroupId()));
			_assertRootPortletIds(
				List.of(portletId2, portletId3),
				List.of(portletId1, portletId4),
				ExportImportHelperUtil.getExportablePortlets(
					TestPropsValues.getCompanyId(), false,
					_liveGroup.getGroupId()));
		}
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(value = "LPD-41307"), @FeatureFlag("LPD-57655"),
			@FeatureFlag("LPD-85946")
		}
	)
	@Test
	public void testGetExportablePortletsDepotGroup() throws Exception {
		DepotEntry assetLibraryDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		_depotGroups.add(assetLibraryDepotEntry.getGroup());

		Assert.assertTrue(
			ListUtil.exists(
				ExportImportHelperUtil.getExportablePortlets(
					assetLibraryDepotEntry.getCompanyId(), false,
					assetLibraryDepotEntry.getGroupId()),
				portlet -> {
					PortletDataHandler portletDataHandler =
						portlet.getPortletDataHandlerInstance();

					return !portletDataHandler.isBatch();
				}));

		DepotEntry spaceDepotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_depotGroups.add(spaceDepotEntry.getGroup());

		List<Portlet> exportablePortlets =
			ExportImportHelperUtil.getExportablePortlets(
				spaceDepotEntry.getCompanyId(), false,
				spaceDepotEntry.getGroupId());

		Assert.assertFalse(exportablePortlets.isEmpty());

		for (Portlet portlet : exportablePortlets) {
			PortletDataHandler portletDataHandler =
				portlet.getPortletDataHandlerInstance();

			Assert.assertTrue(
				portlet.getPortletId(), portletDataHandler.isBatch());
		}
	}

	@Test
	@TestInfo("LPD-106614")
	public void testGetExportablePortletsWithAssetCategoriesAndAssetTags()
		throws Exception {

		_depotEntry = _addDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY);

		_assertRootPortletIds(
			List.of(
				AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
				AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN),
			List.of(LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS),
			ExportImportHelperUtil.getExportablePortlets(
				TestPropsValues.getCompanyId(), false,
				_depotEntry.getGroupId()));

		_assertRootPortletIds(
			List.of(
				AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
				AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN,
				LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS),
			List.of(),
			ExportImportHelperUtil.getExportablePortlets(
				TestPropsValues.getCompanyId(), false,
				_liveGroup.getGroupId()));
	}

	@Test
	@TestInfo("LPD-106614")
	public void testGetExportablePortletsWithObjectDefinitions()
		throws Exception {

		ObjectDefinition companyObjectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);
		ObjectDefinition depotObjectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_DEPOT);
		ObjectDefinition siteObjectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		Group companyGroup = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		_assertRootPortletIds(
			List.of(companyObjectDefinition.getPortletId()),
			List.of(
				depotObjectDefinition.getPortletId(),
				siteObjectDefinition.getPortletId()),
			ExportImportHelperUtil.getExportablePortlets(
				TestPropsValues.getCompanyId(), false,
				companyGroup.getGroupId()));

		_depotEntry = _addDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY);

		_assertRootPortletIds(
			List.of(depotObjectDefinition.getPortletId()),
			List.of(
				companyObjectDefinition.getPortletId(),
				siteObjectDefinition.getPortletId()),
			ExportImportHelperUtil.getExportablePortlets(
				TestPropsValues.getCompanyId(), false,
				_depotEntry.getGroupId()));

		_assertRootPortletIds(
			List.of(siteObjectDefinition.getPortletId()),
			List.of(
				companyObjectDefinition.getPortletId(),
				depotObjectDefinition.getPortletId()),
			ExportImportHelperUtil.getExportablePortlets(
				TestPropsValues.getCompanyId(), false,
				_liveGroup.getGroupId()));
	}

	@Test
	public void testGetGroupPath() throws Exception {
		Group childGroup = GroupTestUtil.addGroup(_liveGroup.getGroupId());

		Locale locale = LocaleUtil.getDefault();

		Assert.assertEquals(
			_liveGroup.getDescriptiveName(locale) + " / " +
				childGroup.getDescriptiveName(locale),
			ExportImportHelperUtil.getGroupPath(childGroup, locale));
	}

	@Test
	public void testGetImportPortletControlsMapAllConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = "test_portlet";

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				true
			).withPortletData(
				true
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = null;

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, true, true, false, true, true);
	}

	@Test
	public void testGetImportPortletControlsMapAllConfigurationWithSummary()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = "test_portlet";

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				true
			).withPortletData(
				true
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				new ManifestSummary());

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapAllConfigurationWithSummary2()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = "test_portlet";

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				true
			).withPortletData(
				true
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = new ManifestSummary();

		Portlet testPortlet = new PortletImpl();

		testPortlet.setPortletId(portletId);

		manifestSummary.addDataPortlet(testPortlet, null);

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapAllConfigurationWithSummary3()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = JournalPortletKeys.JOURNAL;

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				true
			).withPortletData(
				true
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = new ManifestSummary();

		Portlet testPortlet = new PortletImpl();

		testPortlet.setPortletId(portletId);

		manifestSummary.addDataPortlet(testPortlet, new String[0]);

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, true, true, false, true, true);
	}

	@Test
	public void testGetImportPortletControlsMapNoConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = "test_portlet";

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap = builder.withPortletConfiguration(
			false
		).withPortletConfigurationAll(
			false
		).withPortletData(
			false
		).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = null;

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapRootConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = JournalPortletKeys.JOURNAL;

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = null;

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapRootConfigurationWithManifest()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = JournalPortletKeys.JOURNAL;

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				new ManifestSummary());

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapRootConfigurationWithManifest2()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = JournalPortletKeys.JOURNAL;

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = new ManifestSummary();

		Portlet testPortlet = new PortletImpl();

		testPortlet.setPortletId(portletId);

		manifestSummary.addDataPortlet(testPortlet, null);

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapRootConfigurationWithManifest3()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		String portletId = JournalPortletKeys.JOURNAL;

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				false
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = new ManifestSummary();

		Portlet testPortlet = new PortletImpl();

		testPortlet.setPortletId(portletId);

		manifestSummary.addDataPortlet(testPortlet, new String[0]);

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapRootConfigurationWithPortletConfiguration()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		String portletId = JournalPortletKeys.JOURNAL;

		String rootPortletId = PortletIdCodec.decodePortletName(portletId);

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				true
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		Element portletDataElement = null;
		ManifestSummary manifestSummary = null;

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS +
				StringPool.UNDERLINE + rootPortletId,
			new String[] {"true"});

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, false, false, false, false, false);
	}

	@Test
	public void testGetImportPortletControlsMapRootConfigurationWithPortletConfiguration2()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		String portletId = JournalPortletKeys.JOURNAL;

		String rootPortletId = PortletIdCodec.decodePortletName(portletId);

		ExportImportTestParameterMapBuilder builder =
			new ExportImportTestParameterMapBuilder();

		Map<String, String[]> parameterMap =
			builder.withPortletArchivedSetupAll(
				true
			).withPortletConfiguration(
				true
			).withPortletConfigurationAll(
				false
			).withPortletData(
				false
			).withPortletSetupAll(
				true
			).withPortletUserPreferencesAll(
				true
			).build();

		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION +
				StringPool.UNDERLINE + rootPortletId,
			new String[] {"true"});
		parameterMap.put(
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS +
				StringPool.UNDERLINE + rootPortletId,
			new String[] {"true"});

		Element portletDataElement = null;
		ManifestSummary manifestSummary = null;

		Map<String, Boolean> actualPortletControlsMap =
			ExportImportHelperUtil.getImportPortletControlsMap(
				companyId, portletId, parameterMap, portletDataElement,
				manifestSummary);

		_assertPortletControlsMap(
			actualPortletControlsMap, true, true, false, false, false);
	}

	@Test
	public void testGetSelectedLayoutsJSONSelectAllLayouts() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup, layout.getPlid());

		JSONArray selectedLayoutsJSONArray = JSONFactoryUtil.createJSONArray(
			ExportImportHelperUtil.getSelectedLayoutsJSON(
				_stagingGroup.getGroupId(), false,
				StringUtil.merge(
					new long[] {
						layout.getLayoutId(), childLayout.getLayoutId()
					})));

		Assert.assertEquals(1, selectedLayoutsJSONArray.length());

		JSONObject layoutJSONObject = selectedLayoutsJSONArray.getJSONObject(0);

		Assert.assertTrue(layoutJSONObject.getBoolean("includeChildren"));
		Assert.assertEquals(layout.getPlid(), layoutJSONObject.getLong("plid"));
	}

	@Test
	public void testGetSelectedLayoutsJSONSelectChildLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		Layout childLayout = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup, layout.getPlid());

		JSONArray selectedLayoutsJSONArray = JSONFactoryUtil.createJSONArray(
			ExportImportHelperUtil.getSelectedLayoutsJSON(
				_stagingGroup.getGroupId(), false,
				StringUtil.merge(new long[] {childLayout.getLayoutId()})));

		Assert.assertEquals(1, selectedLayoutsJSONArray.length());

		JSONObject layoutJSONObject = selectedLayoutsJSONArray.getJSONObject(0);

		Assert.assertTrue(layoutJSONObject.getBoolean("includeChildren"));
		Assert.assertEquals(
			childLayout.getPlid(), layoutJSONObject.getLong("plid"));
	}

	@Test
	public void testGetSelectedLayoutsJSONSelectNoLayouts() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		LayoutTestUtil.addTypePortletLayout(_stagingGroup, layout.getPlid());

		JSONArray selectedLayoutsJSONArray = JSONFactoryUtil.createJSONArray(
			ExportImportHelperUtil.getSelectedLayoutsJSON(
				_stagingGroup.getGroupId(), false,
				StringUtil.merge(new long[0])));

		Assert.assertEquals(0, selectedLayoutsJSONArray.length());
	}

	@Test
	public void testGetSelectedLayoutsJSONSelectParentLayout()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		LayoutTestUtil.addTypePortletLayout(
			_stagingGroup.getGroupId(), "Child Layout", layout.getPlid());

		JSONArray selectedLayoutsJSONArray = JSONFactoryUtil.createJSONArray(
			ExportImportHelperUtil.getSelectedLayoutsJSON(
				_stagingGroup.getGroupId(), false,
				StringUtil.merge(new long[] {layout.getLayoutId()})));

		Assert.assertEquals(1, selectedLayoutsJSONArray.length());

		JSONObject layoutJSONObject = selectedLayoutsJSONArray.getJSONObject(0);

		Assert.assertFalse(layoutJSONObject.getBoolean("includeChildren"));
		Assert.assertEquals(layout.getPlid(), layoutJSONObject.getLong("plid"));
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(value = "LPD-41307"), @FeatureFlag("LPD-57655"),
			@FeatureFlag("LPD-85946")
		}
	)
	@Test
	public void testGetSupportedGroups() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		List<Group> groups = ExportImportHelperUtil.getSupportedGroups(
			_liveGroup.getCompanyId(),
			_liveGroup.getName(LocaleUtil.getDefault()), null);

		Assert.assertEquals(groups.toString(), 1, groups.size());
		Assert.assertEquals(_liveGroup, groups.get(0));

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		Group group = depotEntry.getGroup();

		_depotGroups.add(group);

		groups = ExportImportHelperUtil.getSupportedGroups(
			group.getCompanyId(), group.getName(LocaleUtil.getDefault()), null);

		Assert.assertEquals(groups.toString(), 1, groups.size());
		Assert.assertEquals(group, groups.get(0));
	}

	@Test
	public void testIsGroupMatching() throws Exception {
		Assert.assertTrue(
			ExportImportHelperUtil.isGroupMatching(
				_createExportImportGroup(false, 0), _liveGroup));
		Assert.assertFalse(
			ExportImportHelperUtil.isGroupMatching(
				_createExportImportGroup(true, DepotConstants.TYPE_SPACE),
				_liveGroup));

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		Group depotGroup = depotEntry.getGroup();

		_depotGroups.add(depotGroup);

		Assert.assertFalse(
			ExportImportHelperUtil.isGroupMatching(
				_createExportImportGroup(false, 0), depotGroup));
		Assert.assertFalse(
			ExportImportHelperUtil.isGroupMatching(
				_createExportImportGroup(
					true, DepotConstants.TYPE_ASSET_LIBRARY),
				depotGroup));
		Assert.assertTrue(
			ExportImportHelperUtil.isGroupMatching(
				_createExportImportGroup(true, DepotConstants.TYPE_SPACE),
				depotGroup));
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(value = "LPD-41307"), @FeatureFlag("LPD-57655"),
			@FeatureFlag("LPD-85946")
		}
	)
	@Test
	public void testIsGroupSupported() throws Exception {
		Assert.assertTrue(ExportImportHelperUtil.isGroupSupported(_liveGroup));
		Assert.assertFalse(
			ExportImportHelperUtil.isGroupSupported(
				_stagingGroupHelper.fetchCompanyGroup(
					_liveGroup.getCompanyId())));
		Assert.assertFalse(
			ExportImportHelperUtil.isGroupSupported(
				_deactivateGroup(_liveGroup)));

		DepotEntry depotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		_depotGroups.add(depotEntry.getGroup());

		Assert.assertTrue(
			ExportImportHelperUtil.isGroupSupported(depotEntry.getGroup()));

		depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_depotGroups.add(depotEntry.getGroup());

		Assert.assertTrue(
			ExportImportHelperUtil.isGroupSupported(depotEntry.getGroup()));
	}

	@Test
	public void testIsGroupSupportedWhenFeatureFlagIsDisabled()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(DepotConstants.TYPE_SPACE);

		_depotGroups.add(depotEntry.getGroup());

		Assert.assertFalse(
			ExportImportHelperUtil.isGroupSupported(depotEntry.getGroup()));
	}

	@Test
	public void testValidateMissingReferences() throws Exception {
		_testValidateMissingReferences();
		_testValidateMissingReferencesWithUnknownClassName();
	}

	protected String getContent(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		Scanner scanner = new Scanner(inputStream);

		scanner.useDelimiter("\\Z");

		return scanner.next();
	}

	protected FileEntry getFileEntry() throws PortalException {
		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId(), TestPropsValues.getUserId()));

		ThumbnailCapability thumbnailCapability =
			fileEntry.getRepositoryCapability(ThumbnailCapability.class);

		return thumbnailCapability.setLargeImageId(
			fileEntry, fileEntry.getFileEntryId());
	}

	protected String replaceParameters(String content, FileEntry fileEntry) {
		return StringUtil.replace(
			content,
			new String[] {"[$GROUP_ID$]", "[$LIVE_GROUP_ID$]", "[$UUID$]"},
			new String[] {
				String.valueOf(fileEntry.getGroupId()),
				String.valueOf(fileEntry.getGroupId()), fileEntry.getUuid()
			});
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			type, ServiceContextTestUtil.getServiceContext());
	}

	private void _assertPortletControlsMap(
		Map<String, Boolean> actualPortletControlsMap,
		boolean portletArchivedSetups, boolean portletConfiguration,
		boolean portletData, boolean portletSetup,
		boolean portletUserPreferences) {

		boolean actualPortletArchivedSetups = MapUtil.getBoolean(
			actualPortletControlsMap,
			PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS);
		boolean actualPortletConfiguration = MapUtil.getBoolean(
			actualPortletControlsMap,
			PortletDataHandlerKeys.PORTLET_CONFIGURATION);
		boolean actualPortletData = MapUtil.getBoolean(
			actualPortletControlsMap, PortletDataHandlerKeys.PORTLET_DATA);
		boolean actualPortletSetup = MapUtil.getBoolean(
			actualPortletControlsMap, PortletDataHandlerKeys.PORTLET_SETUP);
		boolean actualPortletUserPreferences = MapUtil.getBoolean(
			actualPortletControlsMap,
			PortletDataHandlerKeys.PORTLET_USER_PREFERENCES);

		Assert.assertEquals(portletArchivedSetups, actualPortletArchivedSetups);
		Assert.assertEquals(portletConfiguration, actualPortletConfiguration);
		Assert.assertEquals(portletData, actualPortletData);
		Assert.assertEquals(portletSetup, actualPortletSetup);
		Assert.assertEquals(
			portletUserPreferences, actualPortletUserPreferences);
	}

	private void _assertRootPortletIds(
			List<String> expectedRootPortletIds,
			List<String> unexpectedRootPortletIds, List<Portlet> portlets)
		throws Exception {

		List<String> rootPortletIds = TransformUtil.transform(
			portlets, Portlet::getRootPortletId);

		for (String expectedRootPortletId : expectedRootPortletIds) {
			Assert.assertTrue(
				rootPortletIds.toString(),
				rootPortletIds.contains(expectedRootPortletId));
		}

		for (String unexpectedRootPortletId : unexpectedRootPortletIds) {
			Assert.assertFalse(
				rootPortletIds.toString(),
				rootPortletIds.contains(unexpectedRootPortletId));
		}
	}

	private ExportImportGroup _createExportImportGroup(
		boolean depot, int depotType) {

		return new ExportImportGroup(
			0, depot, depotType, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(), null,
			RandomTestUtil.randomString());
	}

	private Group _deactivateGroup(Group group) throws Exception {
		group.setActive(false);

		return _groupLocalService.updateGroup(group);
	}

	private ObjectDefinition _publishObjectDefinition(String scope)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				List.of(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				scope);

		_objectDefinitions.add(objectDefinition);

		return objectDefinition;
	}

	private SafeCloseable
		_registerTestExportImportVulcanBatchEngineTaskItemDelegate(
			BundleContext bundleContext, String portletId,
			BiPredicate<Long, Long> scopeSupportedBiPredicate) {

		String className = RandomTestUtil.randomString();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				VulcanBatchEngineTaskItemDelegate.class,
				new TestExportImportVulcanBatchEngineTaskItemDelegate(
					className, null, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), portletId,
					ExportImportVulcanBatchEngineTaskItemDelegate.Scope.SITE,
					scopeSupportedBiPredicate),
				HashMapDictionaryBuilder.<String, Object>put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.class.name", className
				).put(
					"export.import.vulcan.batch.engine.task.item.delegate",
					"true"
				).build());

		return serviceRegistration::unregister;
	}

	private SafeCloseable _registerWithSafeCloseable(
		BundleContext bundleContext, List<Long> companyIds,
		PortletDataHandler portletDataHandler, String portletId) {

		List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();

		serviceRegistrations.add(
			bundleContext.registerService(
				jakarta.portlet.Portlet.class,
				new GenericPortlet() {
				},
				HashMapDictionaryBuilder.<String, Object>put(
					"jakarta.portlet.name", portletId
				).build()));

		if (portletDataHandler != null) {
			serviceRegistrations.add(
				bundleContext.registerService(
					PortletDataHandler.class, portletDataHandler,
					HashMapDictionaryBuilder.<String, Object>put(
						"companyId",
						() -> TransformUtil.transform(
							companyIds, String::valueOf)
					).put(
						"jakarta.portlet.name", portletId
					).build()));
		}

		return () -> {
			for (ServiceRegistration<?> serviceRegistration :
					serviceRegistrations) {

				serviceRegistration.unregister();
			}
		};
	}

	private void _testValidateMissingReferences() throws Exception {
		String xml = replaceParameters(
			getContent("missing_references.txt"), getFileEntry());

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		zipWriter.addEntry("/manifest.xml", xml);

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(
				zipWriter.getFile())) {

			PortletDataContext portletDataContextImport =
				PortletDataContextFactoryUtil.createImportPortletDataContext(
					_liveGroup.getCompanyId(), _liveGroup.getGroupId(),
					new HashMap<String, String[]>(), new TestUserIdStrategy(),
					zipReader);

			MissingReferences missingReferences =
				ExportImportHelperUtil.validateMissingReferences(
					portletDataContextImport);

			Map<String, MissingReference> dependencyMissingReferences =
				missingReferences.getDependencyMissingReferences();

			Map<String, MissingReference> weakMissingReferences =
				missingReferences.getWeakMissingReferences();

			Assert.assertEquals(
				dependencyMissingReferences.toString(), 2,
				dependencyMissingReferences.size());
			Assert.assertEquals(
				weakMissingReferences.toString(), 1,
				weakMissingReferences.size());
		}

		FileUtil.delete(zipWriter.getFile());
	}

	@TestInfo("LPS-161743")
	private void _testValidateMissingReferencesWithUnknownClassName()
		throws Exception {

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		zipWriter.addEntry(
			"/manifest.xml",
			getContent("unknown_class_name_missing_references.txt"));

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(
				zipWriter.getFile())) {

			PortletDataContext portletDataContextImport =
				PortletDataContextFactoryUtil.createImportPortletDataContext(
					_liveGroup.getCompanyId(), _liveGroup.getGroupId(),
					new HashMap<String, String[]>(), new TestUserIdStrategy(),
					zipReader);

			MissingReferences missingReferences =
				ExportImportHelperUtil.validateMissingReferences(
					portletDataContextImport);

			Map<String, MissingReference> dependencyMissingReferences =
				missingReferences.getDependencyMissingReferences();

			Assert.assertEquals(
				dependencyMissingReferences.toString(), 1,
				dependencyMissingReferences.size());
			Assert.assertTrue(
				dependencyMissingReferences.toString(),
				dependencyMissingReferences.containsKey(
					"my-custom-display-name"));
		}

		FileUtil.delete(zipWriter.getFile());
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private final List<Group> _depotGroups = new ArrayList<>();

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private Group _liveGroup;

	@DeleteAfterTestRun
	private List<ObjectDefinition> _objectDefinitions = new ArrayList<>();

	@DeleteAfterTestRun
	private Group _stagingGroup;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

	private static class TestPortletDataHandler extends BasePortletDataHandler {

		@Override
		public String[] getClassNames() {
			return _classNames;
		}

		private TestPortletDataHandler(
			String[] classNames, boolean dataAlwaysStaged,
			DataLevel dataLevel) {

			_classNames = classNames;

			setDataAlwaysStaged(dataAlwaysStaged);
			setDataLevel(dataLevel);
		}

		private final String[] _classNames;

	}

	private class ExportImportTestParameterMapBuilder {

		public Map<String, String[]> build() {
			return _parameterMap;
		}

		public ExportImportTestParameterMapBuilder withPortletArchivedSetupAll(
			boolean portletArchivedSetupAll) {

			_parameterMap.put(
				PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL,
				new String[] {Boolean.toString(portletArchivedSetupAll)});

			return this;
		}

		public ExportImportTestParameterMapBuilder withPortletConfiguration(
			boolean portletConfiguration) {

			_parameterMap.put(
				PortletDataHandlerKeys.PORTLET_CONFIGURATION,
				new String[] {Boolean.toString(portletConfiguration)});

			return this;
		}

		public ExportImportTestParameterMapBuilder withPortletConfigurationAll(
			boolean portletConfigurationAll) {

			_parameterMap.put(
				PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
				new String[] {Boolean.toString(portletConfigurationAll)});

			return this;
		}

		public ExportImportTestParameterMapBuilder withPortletData(
			boolean portletData) {

			_parameterMap.put(
				PortletDataHandlerKeys.PORTLET_DATA,
				new String[] {Boolean.toString(portletData)});

			return this;
		}

		public ExportImportTestParameterMapBuilder withPortletSetupAll(
			boolean portletSetupAll) {

			_parameterMap.put(
				PortletDataHandlerKeys.PORTLET_SETUP_ALL,
				new String[] {Boolean.toString(portletSetupAll)});

			return this;
		}

		public ExportImportTestParameterMapBuilder
			withPortletUserPreferencesAll(boolean portletUserPreferencesAll) {

			_parameterMap.put(
				PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL,
				new String[] {Boolean.toString(portletUserPreferencesAll)});

			return this;
		}

		private final Map<String, String[]> _parameterMap = new HashMap<>();

	}

}