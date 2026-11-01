/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
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

import java.util.Calendar;
import java.util.Date;

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
	@TestInfo("LPD-85646")
	public void testAddFileEntry() throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.YEAR, -1);

		Date displayDate = calendar.getTime();

		FileEntry fileEntry = _addFileEntry(
			RandomTestUtil.randomString(), displayDate,
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());

		Date publishDate = assetEntry.getPublishDate();

		Assert.assertEquals(displayDate.getTime(), publishDate.getTime());
	}

	@Test
	@TestInfo("LPD-85646")
	public void testCheckAssetEntry() throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.YEAR, -1);

		Date displayDate = calendar.getTime();

		FileEntry fileEntry = _addFileEntry(
			RandomTestUtil.randomString(), displayDate,
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		fileEntry = _dlAppLocalService.updateFileEntry(
			fileEntry.getUserId(), fileEntry.getFileEntryId(),
			fileEntry.getFileName(), fileEntry.getMimeType(),
			fileEntry.getTitle(), "", fileEntry.getDescription(), "",
			DLVersionNumberIncrease.MINOR,
			fileEntry.getContentStream(
			).readAllBytes(),
			displayDate, fileEntry.getExpirationDate(),
			fileEntry.getReviewDate(),
			ServiceContextTestUtil.getServiceContext());

		DLFileVersion dlFileVersion =
			_dlFileVersionLocalService.getLatestFileVersion(
				fileEntry.getFileEntryId(), false);

		dlFileVersion.setStatus(WorkflowConstants.STATUS_DRAFT);

		_dlFileVersionLocalService.updateDLFileVersion(dlFileVersion);

		AssetEntry fileEntryAssetEntry = _assetEntryLocalService.fetchEntry(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());

		_assetEntryLocalService.deleteAssetEntry(
			fileEntryAssetEntry.getEntryId());

		FileVersion fileVersion = fileEntry.getFileVersion();

		_dlAppHelperLocalService.checkAssetEntry(
			fileEntry.getUserId(), fileEntry, fileVersion);

		fileEntryAssetEntry = _assetEntryLocalService.getEntry(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());

		AssetEntry fileVersionAssetEntry = _assetEntryLocalService.getEntry(
			DLFileEntry.class.getName(), fileVersion.getFileVersionId());

		Date publishDate = fileEntryAssetEntry.getPublishDate();

		Assert.assertEquals(displayDate.getTime(), publishDate.getTime());

		publishDate = fileVersionAssetEntry.getPublishDate();

		Assert.assertEquals(displayDate.getTime(), publishDate.getTime());
	}

	@Test
	public void testDeleteRepositoryFileEntries() throws Exception {
		_group = GroupTestUtil.addGroup();

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_dlAppHelperLocalService.deleteRepositoryFileEntries(
			fileEntry.getRepositoryId());

		Assert.assertNull(
			_assetEntryLocalService.fetchEntry(
				DLFileEntry.class.getName(), fileEntry.getFileEntryId()));
	}

	@Test
	public void testMoveFileEntryFromTrashRestoresFileEntryContent()
		throws Exception {

		String content = StringUtil.randomString();

		FileEntry fileEntry = _addFileEntry(
			content, null, RandomTestUtil.randomString(),
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
			workflowDefinitionContent.getBytes(),
			TestPropsValues.getCompanyId(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), TestPropsValues.getUserId());

		DLFileEntryType basicDocumentDLFileEntryType =
			DLFileEntryTypeLocalServiceUtil.getBasicDocumentDLFileEntryType();

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			TestPropsValues.getGroupId(),
			PortalUtil.getClassNameId(FileEntry.class.getName()),
			basicDocumentDLFileEntryType.getFileEntryTypeKey(), true,
			WorkflowConstants.STATUS_APPROVED);

		String fileEntryContent = StringUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		int portalServerPort = PortalUtil.getPortalServerPort(false);

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPortalURL("http://localhost:" + portalServerPort);

		long groupId = TestPropsValues.getGroupId();

		themeDisplay.setScopeGroupId(groupId);
		themeDisplay.setSiteGroupId(groupId);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		_addFileEntry(fileEntryContent, null, "test.txt", serviceContext);

		Assert.assertEquals(
			"http://localhost:" + portalServerPort + "/web/guest/d/test-txt",
			serviceContext.getAttribute("friendlyURL"));
	}

	@Test
	public void testRestoreFileEntryFromTrashRestoresFileEntryContent()
		throws Exception {

		String content = StringUtil.randomString();

		FileEntry fileEntry = _addFileEntry(
			content, null, RandomTestUtil.randomString(),
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

	@Test
	@TestInfo("LPD-85646")
	public void testUpdateFileEntry() throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.YEAR, -1);

		Date displayDate = calendar.getTime();

		FileEntry fileEntry = _addFileEntry(
			RandomTestUtil.randomString(), displayDate,
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		calendar.add(Calendar.YEAR, -1);

		Date updatedDisplayDate = calendar.getTime();

		fileEntry = _dlAppLocalService.updateFileEntry(
			fileEntry.getUserId(), fileEntry.getFileEntryId(),
			fileEntry.getFileName(), fileEntry.getMimeType(),
			fileEntry.getTitle(), "", fileEntry.getDescription(), "",
			DLVersionNumberIncrease.AUTOMATIC,
			fileEntry.getContentStream(
			).readAllBytes(),
			updatedDisplayDate, fileEntry.getExpirationDate(),
			fileEntry.getReviewDate(),
			ServiceContextTestUtil.getServiceContext());

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			DLFileEntry.class.getName(), fileEntry.getFileEntryId());

		Date publishDate = assetEntry.getPublishDate();

		Assert.assertEquals(
			updatedDisplayDate.getTime(), publishDate.getTime());
	}

	private FileEntry _addFileEntry(
			String content, Date displayDate, String fileName,
			ServiceContext serviceContext)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.TEXT_PLAIN, content.getBytes(), displayDate, null,
			null, serviceContext);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppHelperLocalService _dlAppHelperLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@Inject
	private File _file;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

}