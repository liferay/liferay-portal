/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class DLAppHelperLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testMoveFileEntryFromTrashRestoresFileEntryContent()
		throws Exception {

		String content = StringUtil.randomString();

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			content.getBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext());

		_dlAppHelperLocalService.moveFileEntryToTrash(
			TestPropsValues.getUserId(),
			_dlAppLocalService.getFileEntry(fileEntry.getFileEntryId()));

		_dlAppHelperLocalService.moveFileEntryFromTrash(
			TestPropsValues.getUserId(),
			_dlAppLocalService.getFileEntry(fileEntry.getFileEntryId()),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			ServiceContextTestUtil.getServiceContext());

		FileEntry restoredFileEntry = _dlAppLocalService.getFileEntry(
			fileEntry.getFileEntryId());

		Assert.assertArrayEquals(
			content.getBytes(),
			_file.getBytes(restoredFileEntry.getContentStream()));
	}

	@Test
	public void testNotifySubscribers() throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/document/library/service/test/dependencies" +
				"/workflow-definition.json");

		String workflowDefinitionContent = StringUtil.read(inputStream);

		_workflowDefinitionManager.deployWorkflowDefinition(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			workflowDefinitionContent.getBytes());

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			TestPropsValues.getGroupId(),
			PortalUtil.getClassNameId(FileEntry.class.getName()), 0, null, true,
			WorkflowConstants.STATUS_APPROVED);

		String fileEntryContent = StringUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPortalURL("http://localhost:8080");
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		_dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "test.txt",
			ContentTypes.TEXT_PLAIN, fileEntryContent.getBytes(), null, null,
			null, serviceContext);

		Assert.assertEquals(
			"http://localhost:8080/web/guest/d/test-txt",
			serviceContext.getAttribute("friendlyURL"));
	}

	@Test
	public void testRestoreFileEntryFromTrashRestoresFileEntryContent()
		throws Exception {

		String content = StringUtil.randomString();

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			content.getBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext());

		_dlAppHelperLocalService.moveFileEntryToTrash(
			TestPropsValues.getUserId(),
			_dlAppLocalService.getFileEntry(fileEntry.getFileEntryId()));

		_dlAppHelperLocalService.restoreFileEntryFromTrash(
			TestPropsValues.getUserId(),
			_dlAppLocalService.getFileEntry(fileEntry.getFileEntryId()));

		FileEntry restoredFileEntry = _dlAppLocalService.getFileEntry(
			fileEntry.getFileEntryId());

		Assert.assertArrayEquals(
			content.getBytes(),
			_file.getBytes(restoredFileEntry.getContentStream()));
	}

	@Inject
	private DLAppHelperLocalService _dlAppHelperLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private File _file;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

}