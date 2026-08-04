/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task.test;

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
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;
import java.io.Serializable;

import java.util.Map;

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

		long backgroundTaskId = _exportLayoutsInBackground();

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

		long backgroundTaskId = _exportPortletInfoInBackground();

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
		File larFile = _exportLayoutsAsFile();

		_importGroup = GroupTestUtil.addGroup();

		long backgroundTaskId = _importLayoutsInBackground(
			_importGroup, larFile);

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

		File larFile = _exportImportLocalService.exportPortletInfoAsFile(
			_addPortletExportImportConfiguration());

		long backgroundTaskId =
			_exportImportLocalService.importPortletInfoInBackground(
				TestPropsValues.getUserId(),
				_addExportImportConfiguration(
					_group.getGroupId(),
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildImportPortletSettingsMap(
							TestPropsValues.getUserId(), _layout.getPlid(),
							_group.getGroupId(), DLPortletKeys.DOCUMENT_LIBRARY,
							ExportImportConfigurationParameterMapFactoryUtil.
								buildParameterMap(),
							LocaleUtil.US, TimeZoneUtil.GMT),
					ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET),
				larFile);

		ExportImportTestUtil.assertBackgroundTaskSuccessful(backgroundTaskId);

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId);

		Assert.assertTrue(
			ListUtil.isEmpty(backgroundTask.getAttachmentsFileEntries()));
	}

	private ExportImportConfiguration _addExportImportConfiguration(
			long groupId, Map<String, Serializable> settingsMap, int type)
		throws Exception {

		return _exportImportConfigurationLocalService.
			addExportImportConfiguration(
				TestPropsValues.getUserId(), groupId,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				type, settingsMap, WorkflowConstants.STATUS_DRAFT,
				ServiceContextTestUtil.getServiceContext());
	}

	private ExportImportConfiguration _addPortletExportImportConfiguration()
		throws Exception {

		return _addExportImportConfiguration(
			_group.getGroupId(),
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

	private File _exportLayoutsAsFile() throws Exception {
		return _exportImportLocalService.exportLayoutsAsFile(
			_addExportImportConfiguration(
				_group.getGroupId(), _buildExportLayoutSettingsMap(),
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT));
	}

	private long _exportLayoutsInBackground() throws Exception {
		return _exportImportLocalService.exportLayoutsAsFileInBackground(
			TestPropsValues.getUserId(),
			_addExportImportConfiguration(
				_group.getGroupId(), _buildExportLayoutSettingsMap(),
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT));
	}

	private long _exportPortletInfoInBackground() throws Exception {
		return _exportImportLocalService.exportPortletInfoAsFileInBackground(
			TestPropsValues.getUserId(),
			_addPortletExportImportConfiguration());
	}

	private long _importLayoutsInBackground(Group group, File larFile)
		throws Exception {

		return _exportImportLocalService.importLayoutsInBackground(
			TestPropsValues.getUserId(),
			_addExportImportConfiguration(
				group.getGroupId(),
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportLayoutSettingsMap(
						TestPropsValues.getUserId(), group.getGroupId(), false,
						null,
						ExportImportConfigurationParameterMapFactoryUtil.
							buildParameterMap(),
						LocaleUtil.US, TimeZoneUtil.GMT),
				ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT),
			larFile);
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

	@DeleteAfterTestRun
	private Group _importGroup;

	private Layout _layout;

}