/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
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
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltan Csaszi
 */
@RunWith(Arquillian.class)
public class AssetCategoryPortletDataHandlerTest
	extends BasePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAssetCategoryExportImportReportEntriesDuplicateExternalReferenceCode()
		throws Exception {

		AssetVocabulary assetVocabulary = _addAssetVocabulary();

		AssetCategory assetCategory = _addAssetCategory(assetVocabulary);

		String originalExternalReferenceCode =
			assetCategory.getExternalReferenceCode();

		File larFile = _exportLayoutsAsFile();

		assetCategory.setExternalReferenceCode(RandomTestUtil.randomString());

		_assetCategoryLocalService.updateAssetCategory(assetCategory);

		ExportImportConfiguration exportImportConfiguration =
			_setUpExportImportConfiguration();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL,
				LoggerTestUtil.ERROR)) {

			_exportImportLocalService.importLayouts(
				exportImportConfiguration, larFile);

			_assertBatchEngineImportTaskError(logCapture);
		}

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
						exportImportReportEntry.getClassExternalReferenceCode(),
						originalExternalReferenceCode) &&
					(exportImportReportEntry.getType() ==
						ExportImportReportEntryConstants.TYPE_ERROR)));
	}

	@Test
	public void testAssetVocabularyExportImportReportEntriesDuplicateTitle()
		throws Exception {

		AssetVocabulary assetVocabulary = _addAssetVocabulary();

		String originalExternalReferenceCode =
			assetVocabulary.getExternalReferenceCode();

		File larFile = _exportLayoutsAsFile();

		_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);

		_addAssetVocabulary();

		ExportImportConfiguration exportImportConfiguration =
			_setUpExportImportConfiguration();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL,
				LoggerTestUtil.ERROR)) {

			_exportImportLocalService.importLayouts(
				exportImportConfiguration, larFile);

			_assertBatchEngineImportTaskError(logCapture);
		}

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
						exportImportReportEntry.getClassExternalReferenceCode(),
						originalExternalReferenceCode) &&
					(exportImportReportEntry.getType() ==
						ExportImportReportEntryConstants.TYPE_ERROR)));
	}

	@Test
	public void testExportImportAssetCategory() throws Exception {
		AssetVocabulary assetVocabulary = _addAssetVocabulary();

		AssetCategory assetCategory = _addAssetCategory(assetVocabulary);

		File larFile = _exportLayoutsAsFile();

		_assetCategoryLocalService.deleteCategory(assetCategory);

		_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);

		ExportImportConfiguration exportImportConfiguration =
			_setUpExportImportConfiguration();

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, larFile);

		Assert.assertNotNull(
			_assetCategoryLocalService.
				fetchAssetCategoryByExternalReferenceCode(
					assetCategory.getExternalReferenceCode(),
					stagingGroup.getGroupId()));
	}

	@Test
	public void testExportImportAssetVocabulary() throws Exception {
		AssetVocabulary assetVocabulary = _addAssetVocabulary();

		File larFile = _exportLayoutsAsFile();

		_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);

		ExportImportConfiguration exportImportConfiguration =
			_setUpExportImportConfiguration();

		_exportImportLocalService.importLayouts(
			exportImportConfiguration, larFile);

		Assert.assertNotNull(
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					assetVocabulary.getExternalReferenceCode(),
					stagingGroup.getGroupId()));
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag(enable = false, value = "LPD-11235")}
	)
	@Test
	public void testExportImportCMSAssetVocabulary() throws Exception {
		Group originalStagingGroup = stagingGroup;

		try {
			Group cmsGroup = GroupServiceUtil.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.CMS);

			stagingGroup = cmsGroup;

			AssetVocabulary assetVocabulary = _addAssetVocabulary();

			DepotEntry depotEntry = _addDepotEntry();

			Group depotGroup = depotEntry.getGroup();

			_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
				assetVocabulary.getVocabularyId(),
				new long[] {depotGroup.getGroupId()}, depotEntry.getType());

			File larFile = _exportLayoutsAsFile();

			_assetVocabularyLocalService.deleteVocabulary(assetVocabulary);

			ExportImportConfiguration exportImportConfiguration =
				_setUpExportImportConfiguration();

			_exportImportLocalService.importLayouts(
				exportImportConfiguration, larFile);

			AssetVocabulary importedVocabulary =
				_assetVocabularyLocalService.
					fetchAssetVocabularyByExternalReferenceCode(
						assetVocabulary.getExternalReferenceCode(),
						cmsGroup.getGroupId());

			Assert.assertNotNull(importedVocabulary);

			List<AssetVocabularyGroupRel> assetVocabularyGroupRels =
				_assetVocabularyGroupRelLocalService.
					getAssetVocabularyGroupRelsByVocabularyId(
						importedVocabulary.getVocabularyId());

			Assert.assertTrue(
				ListUtil.exists(
					assetVocabularyGroupRels,
					assetVocabularyGroupRel ->
						assetVocabularyGroupRel.getGroupId() ==
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
		return AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN;
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

	private AssetCategory _addAssetCategory(AssetVocabulary assetVocabulary)
		throws Exception {

		return _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			ServiceContextTestUtil.getServiceContext());
	}

	private AssetVocabulary _addAssetVocabulary() throws Exception {
		return _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			"vocabulary", ServiceContextTestUtil.getServiceContext());
	}

	private DepotEntry _addDepotEntry() throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertBatchEngineImportTaskError(LogCapture logCapture) {
		List<LogEntry> logEntries = logCapture.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(LoggerTestUtil.ERROR, logEntry.getPriority());
	}

	private File _exportLayoutsAsFile() throws Exception {
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
									AssetCategoriesAdminPortletKeys.
										ASSET_CATEGORIES_ADMIN,
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
								AssetCategoriesAdminPortletKeys.
									ASSET_CATEGORIES_ADMIN,
							new String[] {Boolean.TRUE.toString()}
						).build()));
	}

	private static final String
		_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL =
			"com.liferay.batch.engine.internal." +
				"BatchEngineImportTaskExecutorImpl";

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

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