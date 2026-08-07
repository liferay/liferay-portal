/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
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
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;
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
		long layoutExportBackgroundTaskId =
			ExportImportTestUtil.exportLayoutsInBackground(_group, _layout);
		long portletExportBackgroundTaskId =
			ExportImportTestUtil.exportPortletInfoInBackground(
				_group, _layout, DLPortletKeys.DOCUMENT_LIBRARY);

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

		long freshLayoutExportBackgroundTaskId =
			ExportImportTestUtil.exportLayoutsInBackground(_group, _layout);

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

		long freshLayoutImportBackgroundTaskId = _importLayoutsInBackground();

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			freshLayoutImportBackgroundTaskId);

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		Assert.assertNull(
			_backgroundTaskLocalService.fetchBackgroundTask(
				layoutImportBackgroundTaskId));
		Assert.assertNull(
			_backgroundTaskLocalService.fetchBackgroundTask(
				portletImportBackgroundTaskId));

		Assert.assertNotNull(
			_backgroundTaskLocalService.fetchBackgroundTask(
				freshLayoutImportBackgroundTaskId));
	}

	private long _getAttachmentsFolderId(long backgroundTaskId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId);

		return backgroundTask.getAttachmentsFolderId();
	}

	private long _importLayoutsInBackground() throws Exception {
		return ExportImportTestUtil.importLayoutsInBackground(
			_group, ExportImportTestUtil.exportLayoutsAsFile(_group, _layout));
	}

	private long _importPortletInfoInBackground() throws Exception {
		return ExportImportTestUtil.importPortletInfoInBackground(
			_group, _layout, DLPortletKeys.DOCUMENT_LIBRARY,
			ExportImportTestUtil.exportPortletInfoAsFile(
				_group, _layout, DLPortletKeys.DOCUMENT_LIBRARY));
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

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject(
		filter = "component.name=com.liferay.exportimport.internal.scheduler.DeleteObsoleteBackgroundTasksSchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

}