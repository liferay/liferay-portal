/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.exportimport.portlet.preferences.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.categories.navigation.constants.AssetCategoriesNavigationPortletKeys;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.kernel.staging.constants.StagingConstants;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.exporter.PortletPreferencesPortletConfigurationExporter;
import com.liferay.layout.importer.PortletPreferencesPortletConfigurationImporter;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.persistence.GroupUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class AssetCategoriesNavigationPortletPreferencesProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group.getGroupId());

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), _layout,
			AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION,
			"column-1", new HashMap<>());

		_portletDataContextExport =
			ExportImportTestUtil.getExportPortletDataContext(
				_group.getGroupId());

		_portletDataContextExport.setPlid(_layout.getPlid());
		_portletDataContextExport.setPortletId(
			AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION);

		_portletDataContextImport =
			ExportImportTestUtil.getImportPortletDataContext(
				_group.getGroupId());

		_portletDataContextImport.setPlid(_layout.getPlid());
		_portletDataContextImport.setPortletId(
			AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION);

		_portletPreferences =
			PortletPreferencesFactoryUtil.getStrictPortletSetup(
				_layout,
				AssetCategoriesNavigationPortletKeys.
					ASSET_CATEGORIES_NAVIGATION);
	}

	@Test
	@TestInfo("LPD-109596")
	public void testGetPortletConfigurationWithCompanyGroup() throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		_companyAssetVocabulary = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());

		String portletId = _addPortletToLayout(
			_companyAssetVocabulary, companyGroup);

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			Map<String, Object> portletConfiguration =
				_portletPreferencesPortletConfigurationExporter.
					getPortletConfiguration(_layout.getPlid(), portletId);

			Assert.assertEquals(
				_companyAssetVocabulary.getExternalReferenceCode(),
				portletConfiguration.get(
					"assetVocabularyExternalReferenceCodes_" +
						companyGroup.getExternalReferenceCode()));
			Assert.assertEquals(
				companyGroup.getExternalReferenceCode(),
				portletConfiguration.get(
					"assetVocabularyGroupExternalReferenceCodes"));
			Assert.assertEquals(
				companyGroup.getExternalReferenceCode(),
				portletConfiguration.get(
					"displayStyleGroupExternalReferenceCode"));
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}
	}

	@Test
	@TestInfo("LPD-109596")
	public void testImportPortletConfigurationWithCompanyGroup()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		_companyAssetVocabulary = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());

		String portletId = _addPortletToLayout(
			_companyAssetVocabulary, companyGroup);

		Layout importedLayout = LayoutTestUtil.addTypePortletLayout(_group);

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			_portletPreferencesPortletConfigurationImporter.
				importPortletConfiguration(
					importedLayout.getPlid(), portletId,
					_portletPreferencesPortletConfigurationExporter.
						getPortletConfiguration(_layout.getPlid(), portletId));
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(importedLayout, portletId);

		Assert.assertEquals(
			_companyAssetVocabulary.getExternalReferenceCode(),
			portletPreferences.getValue(
				"assetVocabularyExternalReferenceCodes_" +
					companyGroup.getExternalReferenceCode(),
				null));
		Assert.assertEquals(
			companyGroup.getExternalReferenceCode(),
			portletPreferences.getValue(
				"assetVocabularyGroupExternalReferenceCodes", null));
		Assert.assertEquals(
			companyGroup.getExternalReferenceCode(),
			portletPreferences.getValue(
				"displayStyleGroupExternalReferenceCode", null));
	}

	@Test
	public void testProcessAssetVocabularyExternalReferenceCode()
		throws Exception {

		_portletPreferences.setValue(
			"allAssetVocabularies", Boolean.FALSE.toString());

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		_portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes",
			assetVocabulary.getExternalReferenceCode());

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		AssetVocabulary companyAssetVocabulary1 = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());
		AssetVocabulary companyAssetVocabulary2 = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());

		_portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes_" +
				companyGroup.getExternalReferenceCode(),
			companyAssetVocabulary1.getExternalReferenceCode(),
			companyAssetVocabulary2.getExternalReferenceCode());

		_portletPreferences.setValues(
			"assetVocabularyGroupExternalReferenceCodes",
			companyGroup.getExternalReferenceCode(), StringPool.BLANK,
			companyGroup.getExternalReferenceCode(),
			RandomTestUtil.randomString());

		_portletPreferences.store();

		PortletPreferences exportedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processExportPortletPreferences(
					_portletDataContextExport, _portletPreferences);

		Assert.assertArrayEquals(
			new String[] {
				companyGroup.getExternalReferenceCode(), StringPool.BLANK,
				companyGroup.getExternalReferenceCode()
			},
			exportedPortletPreferences.getValues(
				"assetVocabularyGroupExternalReferenceCodes", null));
		Assert.assertArrayEquals(
			new String[] {assetVocabulary.getExternalReferenceCode()},
			exportedPortletPreferences.getValues(
				"assetVocabularyExternalReferenceCodes", null));
		Assert.assertArrayEquals(
			new String[] {
				companyAssetVocabulary1.getExternalReferenceCode(),
				companyAssetVocabulary2.getExternalReferenceCode()
			},
			exportedPortletPreferences.getValues(
				"assetVocabularyExternalReferenceCodes_" +
					companyGroup.getExternalReferenceCode(),
				null));

		PortletPreferences importedPortletPreferences =
			_exportImportPortletPreferencesProcessor.
				processImportPortletPreferences(
					_portletDataContextImport, exportedPortletPreferences);

		Assert.assertArrayEquals(
			new String[] {
				companyGroup.getExternalReferenceCode(), StringPool.BLANK,
				companyGroup.getExternalReferenceCode()
			},
			importedPortletPreferences.getValues(
				"assetVocabularyGroupExternalReferenceCodes", null));
		Assert.assertArrayEquals(
			new String[] {assetVocabulary.getExternalReferenceCode()},
			importedPortletPreferences.getValues(
				"assetVocabularyExternalReferenceCodes", null));
		Assert.assertArrayEquals(
			new String[] {
				companyAssetVocabulary1.getExternalReferenceCode(),
				companyAssetVocabulary2.getExternalReferenceCode()
			},
			importedPortletPreferences.getValues(
				"assetVocabularyExternalReferenceCodes_" +
					companyGroup.getExternalReferenceCode(),
				null));
	}

	@Test
	@TestInfo("LPD-109596")
	public void testProcessExportImportPortletPreferencesWithCompanyGroup()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		_companyAssetVocabulary = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());

		_portletPreferences.setValue(
			"allAssetVocabularies", Boolean.FALSE.toString());
		_portletPreferences.setValues(
			"assetVocabularyExternalReferenceCodes_" +
				companyGroup.getExternalReferenceCode(),
			_companyAssetVocabulary.getExternalReferenceCode());
		_portletPreferences.setValues(
			"assetVocabularyGroupExternalReferenceCodes",
			companyGroup.getExternalReferenceCode());

		_portletPreferences.store();

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		try {
			PortletPreferences exportedPortletPreferences =
				_exportImportPortletPreferencesProcessor.
					processExportPortletPreferences(
						_portletDataContextExport, _portletPreferences);

			Assert.assertArrayEquals(
				new String[] {
					_companyAssetVocabulary.getExternalReferenceCode()
				},
				exportedPortletPreferences.getValues(
					"assetVocabularyExternalReferenceCodes_" +
						companyGroup.getExternalReferenceCode(),
					null));
			Assert.assertArrayEquals(
				new String[] {companyGroup.getExternalReferenceCode()},
				exportedPortletPreferences.getValues(
					"assetVocabularyGroupExternalReferenceCodes", null));

			PortletPreferences importedPortletPreferences =
				_exportImportPortletPreferencesProcessor.
					processImportPortletPreferences(
						_portletDataContextImport, exportedPortletPreferences);

			Assert.assertArrayEquals(
				new String[] {
					_companyAssetVocabulary.getExternalReferenceCode()
				},
				importedPortletPreferences.getValues(
					"assetVocabularyExternalReferenceCodes_" +
						companyGroup.getExternalReferenceCode(),
					null));
			Assert.assertArrayEquals(
				new String[] {companyGroup.getExternalReferenceCode()},
				importedPortletPreferences.getValues(
					"assetVocabularyGroupExternalReferenceCodes", null));
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}
	}

	@Test
	@TestInfo("LPD-109596")
	public void testPublishLayoutsWithCompanyGroup() throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		_companyAssetVocabulary = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());

		String portletId = _addPortletToLayout(
			_companyAssetVocabulary, companyGroup);

		_remoteLiveGroup = GroupTestUtil.addGroup();

		_enableRemoteStaging();

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_remoteLiveGroup.getGroupId(), false,
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap());

		Layout liveLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_layout.getUuid(), _remoteLiveGroup.getGroupId(), false);

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(liveLayout, portletId);

		Assert.assertEquals(
			_companyAssetVocabulary.getExternalReferenceCode(),
			portletPreferences.getValue(
				"assetVocabularyExternalReferenceCodes_" +
					companyGroup.getExternalReferenceCode(),
				null));
		Assert.assertEquals(
			companyGroup.getExternalReferenceCode(),
			portletPreferences.getValue(
				"assetVocabularyGroupExternalReferenceCodes", null));
		Assert.assertEquals(
			companyGroup.getExternalReferenceCode(),
			portletPreferences.getValue(
				"displayStyleGroupExternalReferenceCode", null));
	}

	private String _addPortletToLayout(
			AssetVocabulary assetVocabulary, Group group)
		throws Exception {

		return LayoutTestUtil.addPortletToLayout(
			_layout,
			AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION,
			HashMapBuilder.put(
				"allAssetVocabularies", new String[] {Boolean.FALSE.toString()}
			).put(
				"assetVocabularyExternalReferenceCodes_" +
					group.getExternalReferenceCode(),
				new String[] {assetVocabulary.getExternalReferenceCode()}
			).put(
				"assetVocabularyGroupExternalReferenceCodes",
				new String[] {group.getExternalReferenceCode()}
			).put(
				"displayStyle",
				new String[] {"ddmTemplate_" + RandomTestUtil.randomString()}
			).put(
				"displayStyleGroupExternalReferenceCode",
				new String[] {group.getExternalReferenceCode()}
			).build());
	}

	private void _enableRemoteStaging() throws Exception {
		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET",
					"F0E1D2C3B4A5968778695A4B3C2D1E0F");
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET_HEX", true)) {

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);
			serviceContext.setAttribute(
				StagingConstants.STAGED_PREFIX +
					PortletDataHandlerKeys.PORTLET_DATA_ALL +
						StringPool.DOUBLE_DASH,
				Boolean.FALSE.toString());
			serviceContext.setAttribute(
				StagingConstants.STAGED_PREFIX +
					PortletDataHandlerKeys.PORTLET_SETUP_ALL +
						StringPool.DOUBLE_DASH,
				Boolean.FALSE.toString());
			serviceContext.setScopeGroupId(_group.getGroupId());

			StagingLocalServiceUtil.enableRemoteStaging(
				TestPropsValues.getUserId(), _group, false, false, "localhost",
				PortalUtil.getPortalServerPort(false),
				PortalUtil.getPathContext(), false,
				_remoteLiveGroup.getGroupId(), serviceContext);

			GroupUtil.clearCache();
		}
	}

	@DeleteAfterTestRun
	private AssetVocabulary _companyAssetVocabulary;

	@Inject(
		filter = "jakarta.portlet.name=" + AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION
	)
	private ExportImportPortletPreferencesProcessor
		_exportImportPortletPreferencesProcessor;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private PortletDataContext _portletDataContextExport;
	private PortletDataContext _portletDataContextImport;
	private PortletPreferences _portletPreferences;

	@Inject
	private PortletPreferencesPortletConfigurationExporter
		_portletPreferencesPortletConfigurationExporter;

	@Inject
	private PortletPreferencesPortletConfigurationImporter
		_portletPreferencesPortletConfigurationImporter;

	@DeleteAfterTestRun
	private Group _remoteLiveGroup;

}