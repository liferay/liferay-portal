/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagGroupRel;
import com.liferay.asset.kernel.service.AssetTagGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.tags.constants.AssetTagsAdminPortletKeys;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.constants.StagingConstants;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.exportimport.test.util.lar.BasePortletDataHandlerTestCase;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltan Csaszi
 */
@RunWith(Arquillian.class)
public class AssetTagsPortletDataHandlerTest
	extends BasePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testAssetTagExportImportReportEntries() throws Exception {
		AssetTag assetTag = _addTag();

		File larFile = _exportLayout();

		assetTag.setExternalReferenceCode(RandomTestUtil.randomString());

		_assetTagLocalService.updateAssetTag(assetTag);

		ExportImportConfiguration exportImportConfiguration =
			_setUpExportImportConfiguration();

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, larFile);

		List<ExportImportReportEntry> exportImportReportEntries =
			_exportImportReportEntryLocalService.getExportImportReportEntries(
				TestPropsValues.getCompanyId(),
				exportImportConfiguration.getExportImportConfigurationId());

		Assert.assertEquals(
			exportImportReportEntries.toString(), 1,
			exportImportReportEntries.size());

		Assert.assertTrue(
			ListUtil.exists(
				exportImportReportEntries,
				exportImportReportEntry ->
					Objects.equals(
						exportImportReportEntry.getErrorMessage(),
						"A tag with the name " + assetTag.getName() +
							" already exists") &&
					(exportImportReportEntry.getType() ==
						ExportImportReportEntryConstants.TYPE_ERROR)));
	}

	@Test
	public void testAssetTagImportCompletedWithErrors() throws Exception {
		AssetTag assetTag = _addTag();

		Layout layout = LayoutTestUtil.addTypePortletLayout(stagingGroup);

		Map<String, String[]> parameterMap = HashMapBuilder.put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA + "_" +
				AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN,
			new String[] {Boolean.TRUE.toString()}
		).build();

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportPortletSettingsMap(
							TestPropsValues.getUser(), layout.getPlid(),
							stagingGroup.getGroupId(),
							AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN,
							parameterMap, StringPool.BLANK));

		File larFile = _exportImportLocalService.exportPortletInfoAsFile(
			exportImportConfiguration);

		assetTag.setExternalReferenceCode(RandomTestUtil.randomString());

		_assetTagLocalService.updateAssetTag(assetTag);

		exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportPortletSettingsMap(
							TestPropsValues.getUser(), layout.getPlid(),
							stagingGroup.getGroupId(),
							AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN,
							parameterMap));

		long backgroundTaskId =
			_exportImportLocalService.importPortletInfoInBackground(
				TestPropsValues.getUserId(), exportImportConfiguration,
				larFile);

		ExportImportTestUtil.retryAssert(
			1, TimeUnit.SECONDS, 5, TimeUnit.SECONDS,
			() -> {
				BackgroundTask backgroundTask =
					BackgroundTaskManagerUtil.getBackgroundTask(
						backgroundTaskId);

				Assert.assertEquals(
					BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS,
					backgroundTask.getStatus());
			});
	}

	@Test
	public void testExportImportAssetTag() throws Exception {
		AssetTag assetTag = _addTag();

		File larFile = _exportLayout();

		_assetTagLocalService.deleteTag(assetTag);

		ExportImportConfiguration exportImportConfiguration =
			_setUpExportImportConfiguration();

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, larFile);

		Assert.assertNotNull(
			_assetTagLocalService.fetchAssetTagByExternalReferenceCode(
				assetTag.getExternalReferenceCode(),
				stagingGroup.getGroupId()));
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag(enable = false, value = "LPD-11235")}
	)
	@Test
	public void testExportImportCMSAssetTag() throws Exception {
		Group originalStagingGroup = stagingGroup;

		try {
			Group cmsGroup = GroupServiceUtil.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS);

			stagingGroup = cmsGroup;

			AssetTag assetTag = _addTag();

			DepotEntry depotEntry = _addStagedDepotEntry();

			Group depotGroup = depotEntry.getGroup();

			_assetTagGroupRelLocalService.setAssetTagGroupRels(
				assetTag.getTagId(), new long[] {depotGroup.getGroupId()});

			File larFile = _exportLayout();

			_assetTagLocalService.deleteTag(assetTag);

			ExportImportConfiguration exportImportConfiguration =
				_setUpExportImportConfiguration();

			_exportImportLocalService.importLayouts(
				exportImportConfiguration, larFile);

			AssetTag importedTag =
				_assetTagLocalService.fetchAssetTagByExternalReferenceCode(
					assetTag.getExternalReferenceCode(), cmsGroup.getGroupId());

			Assert.assertNotNull(importedTag);

			List<AssetTagGroupRel> assetTagGroupRels =
				_assetTagGroupRelLocalService.getAssetTagGroupRelsByTagId(
					importedTag.getTagId());

			Assert.assertTrue(
				ListUtil.exists(
					assetTagGroupRels,
					assetTagGroupRel ->
						assetTagGroupRel.getGroupId() ==
							depotGroup.getGroupId()));
		}
		finally {
			stagingGroup = originalStagingGroup;
		}
	}

	@Override
	protected void addStagedModels() throws Exception {
	}

	@Override
	protected DataLevel getDataLevel() {
		return DataLevel.SITE;
	}

	@Override
	protected String getPortletId() {
		return AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN;
	}

	@Override
	protected boolean isDataPortalLevel() {
		return false;
	}

	@Override
	protected boolean isDataPortletInstanceLevel() {
		return false;
	}

	@Override
	protected boolean isDataSiteLevel() {
		return true;
	}

	private DepotEntry _addStagedDepotEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		return _enableLocalStaging(depotEntry);
	}

	private AssetTag _addTag() throws Exception {
		return _assetTagLocalService.addTag(
			"", TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	private DepotEntry _enableLocalStaging(DepotEntry depotEntry)
		throws Exception {

		Group stagingGroup = _enableLocalStaging(depotEntry.getGroup());

		return DepotEntryLocalServiceUtil.fetchGroupDepotEntry(
			stagingGroup.getGroupId());
	}

	private Group _enableLocalStaging(Group group) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		_setStagingAttributes(serviceContext);

		serviceContext.setAttribute("staging", Boolean.TRUE);

		StagingLocalServiceUtil.enableLocalStaging(
			TestPropsValues.getUserId(), group, false, false, serviceContext);

		return group.getStagingGroup();
	}

	private File _exportLayout() throws Exception {
		return _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(),
							stagingGroup.getGroupId(), false, new long[0],
							HashMapBuilder.put(
								PortletDataHandlerKeys.PORTLET_DATA,
								new String[] {Boolean.TRUE.toString()}
							).put(
								PortletDataHandlerKeys.PORTLET_DATA + "_" +
									AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN,
								new String[] {Boolean.TRUE.toString()}
							).build())));
	}

	private void _setStagingAttribute(
		ServiceContext serviceContext, String key) {

		serviceContext.setAttribute(
			StagingConstants.STAGED_PREFIX + key + StringPool.DOUBLE_DASH,
			String.valueOf(Boolean.TRUE));
	}

	private void _setStagingAttributes(ServiceContext serviceContext) {
		_setStagingAttribute(
			serviceContext, PortletDataHandlerKeys.DATA_STRATEGY_MIRROR);
		_setStagingAttribute(
			serviceContext, PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL);
		_setStagingAttribute(
			serviceContext, PortletDataHandlerKeys.PORTLET_DATA_ALL);
		_setStagingAttribute(
			serviceContext, PortletDataHandlerKeys.PORTLET_SETUP_ALL);
	}

	private ExportImportConfiguration _setUpExportImportConfiguration()
		throws Exception {

		return _exportImportConfigurationLocalService.
			addDraftExportImportConfiguration(
				TestPropsValues.getUserId(),
				ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportLayoutSettingsMap(
						TestPropsValues.getUser(), stagingGroup.getGroupId(),
						false, new long[0],
						HashMapBuilder.put(
							PortletDataHandlerKeys.PORTLET_DATA,
							new String[] {Boolean.TRUE.toString()}
						).put(
							PortletDataHandlerKeys.PORTLET_DATA + "_" +
								AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN,
							new String[] {Boolean.TRUE.toString()}
						).build()));
	}

	@Inject
	private AssetTagGroupRelLocalService _assetTagGroupRelLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

}