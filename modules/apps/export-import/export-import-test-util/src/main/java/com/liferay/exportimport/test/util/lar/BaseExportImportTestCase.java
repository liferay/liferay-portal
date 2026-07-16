/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util.lar;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalServiceUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportClassedModelUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportServiceUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.DateTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.io.File;
import java.io.Serializable;

import java.util.Date;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Eduardo García
 */
public abstract class BaseExportImportTestCase {

	public void importLayouts(Map<String, String[]> parameterMap)
		throws Exception {

		importLayouts(parameterMap, false);
	}

	public void importLayouts(
			Map<String, String[]> parameterMap, boolean expectError)
		throws Exception {

		importLayouts(parameterMap, expectError, false);
	}

	public void importLayouts(
			Map<String, String[]> parameterMap, boolean expectError,
			boolean privateLayout)
		throws Exception {

		try (LogCapture logCapture = getLogCapture(expectError)) {
			User user = TestPropsValues.getUser();

			Map<String, Serializable> importLayoutSettingsMap =
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportLayoutSettingsMap(
						user, importedGroup.getGroupId(), privateLayout, null,
						parameterMap);

			ExportImportConfiguration exportImportConfiguration =
				ExportImportConfigurationLocalServiceUtil.
					addExportImportConfiguration(
						user.getUserId(), importedGroup.getGroupId(),
						StringPool.BLANK, StringPool.BLANK,
						ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
						importLayoutSettingsMap, WorkflowConstants.STATUS_DRAFT,
						new ServiceContext());

			ExportImportServiceUtil.importLayouts(
				exportImportConfiguration, larFile);
		}
	}

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();
		importedGroup = GroupTestUtil.addGroup();

		layout = LayoutTestUtil.addTypePortletLayout(group);

		// Delete and readd to ensure a different layout ID (not ID or UUID).
		// See LPS-32132.

		LayoutLocalServiceUtil.deleteLayout(layout, new ServiceContext());

		layout = LayoutTestUtil.addTypePortletLayout(group);
	}

	@After
	public void tearDown() throws Exception {
		if ((larFile != null) && larFile.exists()) {
			FileUtil.delete(larFile);
		}
	}

	protected AssetLink addAssetLink(
			StagedModel sourceStagedModel, StagedModel targetStagedModel,
			int weight)
		throws PortalException {

		AssetEntry originAssetEntry = getAssetEntry(sourceStagedModel);
		AssetEntry targetAssetEntry = getAssetEntry(targetStagedModel);

		return AssetLinkLocalServiceUtil.addLink(
			TestPropsValues.getUserId(), originAssetEntry.getEntryId(),
			targetAssetEntry.getEntryId(), 0, weight);
	}

	protected void addParameter(
		Map<String, String[]> parameterMap, String name, String value) {

		parameterMap.put(name, new String[] {value});
	}

	protected void addParameter(
		Map<String, String[]> parameterMap, String namespace, String name,
		boolean value) {

		PortletDataHandlerBoolean portletDataHandlerBoolean =
			new PortletDataHandlerBoolean(namespace, name);

		addParameter(
			parameterMap, portletDataHandlerBoolean.getNamespacedName(),
			String.valueOf(value));
	}

	protected StagedModel addStagedModel(long groupId) throws Exception {
		return null;
	}

	protected StagedModel addStagedModel(long groupId, Date createdDate)
		throws Exception {

		return null;
	}

	protected void deleteStagedModel(StagedModel stagedModel) throws Exception {
	}

	protected void exportImportLayouts(
			long[] layoutIds, Map<String, String[]> parameterMap)
		throws Exception {

		exportImportLayouts(layoutIds, parameterMap, false);
	}

	protected void exportImportLayouts(
			long[] layoutIds, Map<String, String[]> parameterMap,
			boolean expectError)
		throws Exception {

		exportLayouts(layoutIds, getExportParameterMap(), expectError);

		importLayouts(parameterMap, expectError);
	}

	protected void exportLayouts(
			long[] layoutIds, Map<String, String[]> parameterMap)
		throws Exception {

		exportLayouts(layoutIds, parameterMap, false);
	}

	protected void exportLayouts(
			long[] layoutIds, Map<String, String[]> parameterMap,
			boolean expectError)
		throws Exception {

		exportLayouts(layoutIds, parameterMap, expectError, false);
	}

	protected void exportLayouts(
			long[] layoutIds, Map<String, String[]> parameterMap,
			boolean expectError, boolean privateLayout)
		throws Exception {

		try (LogCapture logCapture = getLogCapture(expectError)) {
			User user = TestPropsValues.getUser();

			Map<String, Serializable> exportLayoutSettingsMap =
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildExportLayoutSettingsMap(
						user, group.getGroupId(), privateLayout, layoutIds,
						parameterMap);

			ExportImportConfiguration exportImportConfiguration =
				ExportImportConfigurationLocalServiceUtil.
					addDraftExportImportConfiguration(
						user.getUserId(),
						ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
						exportLayoutSettingsMap);

			larFile = ExportImportServiceUtil.exportLayoutsAsFile(
				exportImportConfiguration);
		}
	}

	protected AssetEntry getAssetEntry(StagedModel stagedModel)
		throws PortalException {

		return AssetEntryLocalServiceUtil.getEntry(
			ExportImportClassedModelUtil.getClassName(stagedModel),
			ExportImportClassedModelUtil.getClassPK(stagedModel));
	}

	protected Map<String, String[]> getExportParameterMap() throws Exception {
		return LinkedHashMapBuilder.put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).build();
	}

	protected Map<String, String[]> getImportParameterMap() throws Exception {
		return LinkedHashMapBuilder.put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).build();
	}

	protected LogCapture getLogCapture(boolean expectError) {
		LogCapture logCapture = null;

		if (expectError) {
			logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.exportimport.internal.lifecycle." +
					"LoggerExportImportLifecycleListener",
				LoggerTestUtil.ERROR);
		}

		return logCapture;
	}

	protected StagedModel getStagedModel(String uuid, long groupId)
		throws PortalException {

		return null;
	}

	protected String getStagedModelUuid(StagedModel stagedModel)
		throws PortalException {

		return stagedModel.getUuid();
	}

	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		DateTestUtil.assertEquals(
			stagedModel.getCreateDate(), importedStagedModel.getCreateDate());
		DateTestUtil.assertEquals(
			stagedModel.getModifiedDate(),
			importedStagedModel.getModifiedDate());

		Assert.assertEquals(
			stagedModel.getUuid(), importedStagedModel.getUuid());
	}

	@DeleteAfterTestRun
	protected Group group;

	@DeleteAfterTestRun
	protected Group importedGroup;

	protected Layout importedLayout;
	protected File larFile;
	protected Layout layout;

}