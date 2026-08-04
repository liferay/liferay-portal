/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;
import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jaime León Rosado
 */
@RunWith(Arquillian.class)
public class DeleteObsoleteBackgroundTasksSchedulerJobConfigurationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPS-166513")
	public void testDeleteObsoleteExportBackgroundTasks() throws Exception {
		long layoutExportBackgroundTaskId = _exportLayoutsInBackground();
		long portletExportBackgroundTaskId = _exportPortletInfoInBackground();

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			layoutExportBackgroundTaskId);
		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			portletExportBackgroundTaskId);

		long layoutExportAttachmentsFolderId = _getAttachmentsFolderId(
			layoutExportBackgroundTaskId);
		long portletExportAttachmentsFolderId = _getAttachmentsFolderId(
			portletExportBackgroundTaskId);

		_updateBackgroundTaskModifiedDateToPast(layoutExportBackgroundTaskId);
		_updateBackgroundTaskModifiedDateToPast(portletExportBackgroundTaskId);

		long freshLayoutExportBackgroundTaskId = _exportLayoutsInBackground();

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			freshLayoutExportBackgroundTaskId);

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		Assert.assertNull(
			_backgroundTaskLocalService.fetchBackgroundTask(
				layoutExportBackgroundTaskId));
		Assert.assertNull(
			_backgroundTaskLocalService.fetchBackgroundTask(
				portletExportBackgroundTaskId));

		Assert.assertNull(
			_dlFolderLocalService.fetchDLFolder(
				layoutExportAttachmentsFolderId));
		Assert.assertNull(
			_dlFolderLocalService.fetchDLFolder(
				portletExportAttachmentsFolderId));

		Assert.assertNotNull(
			_backgroundTaskLocalService.fetchBackgroundTask(
				freshLayoutExportBackgroundTaskId));
	}

	@Test
	@TestInfo("LPS-166514")
	public void testDeleteObsoleteImportBackgroundTasks() throws Exception {
		long layoutImportBackgroundTaskId = _importLayoutsInBackground();
		long portletImportBackgroundTaskId = _importPortletInfoInBackground();

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			layoutImportBackgroundTaskId);
		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			portletImportBackgroundTaskId);

		_updateBackgroundTaskModifiedDateToPast(layoutImportBackgroundTaskId);
		_updateBackgroundTaskModifiedDateToPast(portletImportBackgroundTaskId);

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		Assert.assertNull(
			_backgroundTaskLocalService.fetchBackgroundTask(
				layoutImportBackgroundTaskId));
		Assert.assertNull(
			_backgroundTaskLocalService.fetchBackgroundTask(
				portletImportBackgroundTaskId));
	}

	private ExportImportConfiguration _addExportImportConfiguration(
			Map<String, Serializable> settingsMap, int type)
		throws Exception {

		return _exportImportConfigurationLocalService.
			addExportImportConfiguration(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				type, settingsMap, WorkflowConstants.STATUS_DRAFT,
				ServiceContextTestUtil.getServiceContext());
	}

	private ExportImportConfiguration _addPortletExportImportConfiguration()
		throws Exception {

		return _addExportImportConfiguration(
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildExportPortletSettingsMap(
					TestPropsValues.getUserId(), _layout.getPlid(),
					_group.getGroupId(), DLPortletKeys.DOCUMENT_LIBRARY,
					ExportImportConfigurationParameterMapFactoryUtil.
						buildParameterMap(),
					LocaleUtil.US, TimeZoneUtil.GMT,
					RandomTestUtil.randomString() + ".lar"),
			ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET);
	}

	private Map<String, Serializable> _buildExportLayoutSettingsMap()
		throws Exception {

		return ExportImportConfigurationSettingsMapFactoryUtil.
			buildExportLayoutSettingsMap(
				TestPropsValues.getUserId(), _group.getGroupId(), false,
				new long[] {_layout.getLayoutId()},
				ExportImportConfigurationParameterMapFactoryUtil.
					buildParameterMap(),
				LocaleUtil.US, TimeZoneUtil.GMT);
	}

	private long _exportLayoutsInBackground() throws Exception {
		return _exportImportLocalService.exportLayoutsAsFileInBackground(
			TestPropsValues.getUserId(),
			_addExportImportConfiguration(
				_buildExportLayoutSettingsMap(),
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT));
	}

	private long _exportPortletInfoInBackground() throws Exception {
		return _exportImportLocalService.exportPortletInfoAsFileInBackground(
			TestPropsValues.getUserId(),
			_addPortletExportImportConfiguration());
	}

	private long _getAttachmentsFolderId(long backgroundTaskId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId);

		return backgroundTask.getAttachmentsFolderId();
	}

	private long _importLayoutsInBackground() throws Exception {
		File larFile = _exportImportLocalService.exportLayoutsAsFile(
			_addExportImportConfiguration(
				_buildExportLayoutSettingsMap(),
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT));

		return _exportImportLocalService.importLayoutsInBackground(
			TestPropsValues.getUserId(),
			_addExportImportConfiguration(
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportLayoutSettingsMap(
						TestPropsValues.getUserId(), _group.getGroupId(), false,
						null,
						ExportImportConfigurationParameterMapFactoryUtil.
							buildParameterMap(),
						LocaleUtil.US, TimeZoneUtil.GMT),
				ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT),
			larFile);
	}

	private long _importPortletInfoInBackground() throws Exception {
		File larFile = _exportImportLocalService.exportPortletInfoAsFile(
			_addPortletExportImportConfiguration());

		return _exportImportLocalService.importPortletInfoInBackground(
			TestPropsValues.getUserId(),
			_addExportImportConfiguration(
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportPortletSettingsMap(
						TestPropsValues.getUserId(), _layout.getPlid(),
						_group.getGroupId(), DLPortletKeys.DOCUMENT_LIBRARY,
						ExportImportConfigurationParameterMapFactoryUtil.
							buildParameterMap(),
						LocaleUtil.US, TimeZoneUtil.GMT),
				ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET),
			larFile);
	}

	private void _updateBackgroundTaskModifiedDateToPast(long backgroundTaskId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId);

		backgroundTask.setModifiedDate(
			new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(31)));

		_backgroundTaskLocalService.updateBackgroundTask(backgroundTask);
	}

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject(
		filter = "component.name=com.liferay.exportimport.internal.scheduler.DeleteObsoleteBackgroundTasksSchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

}