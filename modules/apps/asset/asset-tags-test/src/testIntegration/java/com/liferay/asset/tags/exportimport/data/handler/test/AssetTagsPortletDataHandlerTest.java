/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.tags.constants.AssetTagsAdminPortletKeys;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.test.util.lar.BasePortletDataHandlerTestCase;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;

import java.util.List;
import java.util.Objects;

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

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(value = "LPD-17564"), @FeatureFlag(value = "LPD-35914")
		}
	)
	@Test
	public void testAssetTagExportImportReportEntries() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-35914");

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

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), false, "LPD-35914");
	}

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(value = "LPD-17564"), @FeatureFlag(value = "LPD-35914")
		}
	)
	@Test
	public void testExportImportAssetTag() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-35914");

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

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), false, "LPD-35914");
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

	private AssetTag _addTag() throws Exception {
		return _assetTagLocalService.addTag(
			"", TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
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
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

}