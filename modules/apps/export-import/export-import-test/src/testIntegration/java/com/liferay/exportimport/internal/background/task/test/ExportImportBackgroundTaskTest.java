/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
public class ExportImportBackgroundTaskTest {

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
	@TestInfo("LRQA-29297")
	public void testDeleteLayoutExportProcessDeletesLARAttachments()
		throws Exception {

		long backgroundTaskId = ExportImportTestUtil.exportLayoutsInBackground(
			_group, _layout);

		ExportImportTestUtil.assertBackgroundTaskSuccessful(backgroundTaskId);

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId);

		Assert.assertFalse(
			ListUtil.isEmpty(backgroundTask.getAttachmentsFileEntries()));

		long attachmentsFolderId = backgroundTask.getAttachmentsFolderId();

		_backgroundTaskLocalService.deleteBackgroundTask(backgroundTaskId);

		Assert.assertNull(
			_backgroundTaskLocalService.fetchBackgroundTask(backgroundTaskId));
		Assert.assertNull(
			_dlFolderLocalService.fetchDLFolder(attachmentsFolderId));
	}

	@Test
	@TestInfo("LRQA-29297")
	public void testDeletePortletExportProcessDeletesLARAttachments()
		throws Exception {

		long backgroundTaskId =
			ExportImportTestUtil.exportPortletInfoInBackground(
				_group, _layout, DLPortletKeys.DOCUMENT_LIBRARY);

		ExportImportTestUtil.assertBackgroundTaskSuccessful(backgroundTaskId);

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId);

		Assert.assertFalse(
			ListUtil.isEmpty(backgroundTask.getAttachmentsFileEntries()));

		long attachmentsFolderId = backgroundTask.getAttachmentsFolderId();

		_backgroundTaskLocalService.deleteBackgroundTask(backgroundTaskId);

		Assert.assertNull(
			_backgroundTaskLocalService.fetchBackgroundTask(backgroundTaskId));
		Assert.assertNull(
			_dlFolderLocalService.fetchDLFolder(attachmentsFolderId));
	}

	@Test
	@TestInfo("LPS-166514")
	public void testLARAttachmentIsDeletedAfterLayoutImport() throws Exception {
		_importGroup = GroupTestUtil.addGroup();

		long backgroundTaskId = ExportImportTestUtil.importLayoutsInBackground(
			_importGroup,
			ExportImportTestUtil.exportLayoutsAsFile(_group, _layout));

		ExportImportTestUtil.assertBackgroundTaskSuccessful(backgroundTaskId);

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId);

		Assert.assertTrue(
			ListUtil.isEmpty(backgroundTask.getAttachmentsFileEntries()));
	}

	@Test
	@TestInfo("LPS-166514")
	public void testLARAttachmentIsDeletedAfterPortletImport()
		throws Exception {

		long backgroundTaskId =
			ExportImportTestUtil.importPortletInfoInBackground(
				_group, _layout, DLPortletKeys.DOCUMENT_LIBRARY,
				ExportImportTestUtil.exportPortletInfoAsFile(
					_group, _layout, DLPortletKeys.DOCUMENT_LIBRARY));

		ExportImportTestUtil.assertBackgroundTaskSuccessful(backgroundTaskId);

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId);

		Assert.assertTrue(
			ListUtil.isEmpty(backgroundTask.getAttachmentsFileEntries()));
	}

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Group _importGroup;

	private Layout _layout;

}
