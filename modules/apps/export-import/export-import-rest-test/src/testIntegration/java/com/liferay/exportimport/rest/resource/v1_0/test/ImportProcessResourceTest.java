/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.rest.client.dto.v1_0.ImportProcess;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.staging.StagingGroupHelper;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@FeatureFlag("LPD-35914")
@RunWith(Arquillian.class)
public class ImportProcessResourceTest
	extends BaseImportProcessResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws PortalException {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-35914");
	}

	@AfterClass
	public static void tearDownClass() throws PortalException {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), false, "LPD-35914");
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected ImportProcess
			testGetAssetLibraryImportProcessesPage_addImportProcess(
				Long assetLibraryId, ImportProcess importProcess)
		throws Exception {

		return _addImportProcess(
			GroupUtil.getDepotGroupId(
				String.valueOf(assetLibraryId), TestPropsValues.getCompanyId(),
				_depotEntryLocalService, _groupLocalService),
			randomImportProcess());
	}

	@Override
	protected ImportProcess testGetImportProcess_addImportProcess()
		throws Exception {

		return _addImportProcess(_getCompanyGroupId(), randomImportProcess());
	}

	@Override
	protected ImportProcess testGetImportProcessesPage_addImportProcess(
			ImportProcess importProcess)
		throws Exception {

		return _addImportProcess(_getCompanyGroupId(), randomImportProcess());
	}

	@Override
	protected ImportProcess testGetSiteImportProcessesPage_addImportProcess(
			Long siteId, ImportProcess importProcess)
		throws Exception {

		return _addImportProcess(siteId, randomImportProcess());
	}

	private ImportProcess _addImportProcess(
			long groupId, ImportProcess importProcess)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.addBackgroundTask(
				TestPropsValues.getUserId(), groupId, importProcess.getTitle(),
				BackgroundTaskExecutorNames.
					LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR,
				HashMapBuilder.<String, Serializable>put(
					"exportImportConfigurationId", RandomTestUtil.randomLong()
				).build(),
				null);

		_backgroundTasks.add(backgroundTask);

		return new ImportProcess() {
			{
				setDateCreated(backgroundTask.getCreateDate());
				setDateModified(backgroundTask.getModifiedDate());
				setId(backgroundTask.getBackgroundTaskId());
				setTitle(backgroundTask.getName());
			}
		};
	}

	private long _getCompanyGroupId() throws Exception {
		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		return group.getGroupId();
	}

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@DeleteAfterTestRun
	private final List<BackgroundTask> _backgroundTasks = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

}