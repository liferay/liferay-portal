/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Saurasish Basak
 */
@RunWith(Arquillian.class)
public class DownloadEntriesMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testServeResource() throws Exception {
		_testServeResourceDownloadEntries();

		_testServeResourceDownloadFolder();

		_testServeResourceDownloadFolderWithoutShortcutTargetPermission();

		_testServeResourceMaxSizeToDownload();
	}

	private FileEntry _addFileEntry(
			String content, String fileName, long folderId)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(), folderId,
			fileName, ContentTypes.TEXT_PLAIN, content.getBytes(), null, null,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private Folder _addFolder(String name, long parentFolderId)
		throws Exception {

		return _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			parentFolderId, name, StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
			long folderId, String resourceID)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayResourceRequest.setParameter(
			"folderId", String.valueOf(folderId));
		mockLiferayResourceRequest.setParameter(
			"repositoryId", String.valueOf(_group.getGroupId()));
		mockLiferayResourceRequest.setResourceID(resourceID);

		return mockLiferayResourceRequest;
	}

	private Map<String, String> _getZipEntries(byte[] bytes) throws Exception {
		Map<String, String> zipEntries = new LinkedHashMap<>();

		try (ZipInputStream zipInputStream = new ZipInputStream(
				new ByteArrayInputStream(bytes))) {

			ZipEntry zipEntry = zipInputStream.getNextEntry();

			while (zipEntry != null) {
				zipEntries.put(
					zipEntry.getName(),
					new String(zipInputStream.readAllBytes()));

				zipEntry = zipInputStream.getNextEntry();
			}
		}

		return zipEntries;
	}

	private byte[] _serveResource(
			MockLiferayResourceRequest mockLiferayResourceRequest)
		throws Exception {

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return byteArrayOutputStream.toByteArray();
	}

	private void _testServeResourceDownloadEntries() throws Exception {
		FileEntry fileEntry = _addFileEntry(
			"notes", "notes.txt", DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Folder folder = _addFolder(
			"Archive", DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_addFileEntry("old", "old.txt", folder.getFolderId());

		MockLiferayResourceRequest mockLiferayResourceRequest =
			_getMockLiferayResourceRequest(
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				"/document_library/download_entry");

		mockLiferayResourceRequest.setParameter(
			"rowIdsFileEntry", String.valueOf(fileEntry.getFileEntryId()));
		mockLiferayResourceRequest.setParameter(
			"rowIdsFolder", String.valueOf(folder.getFolderId()));

		Map<String, String> zipEntries = _getZipEntries(
			_serveResource(mockLiferayResourceRequest));

		Assert.assertEquals(zipEntries.toString(), 2, zipEntries.size());
		Assert.assertEquals("old", zipEntries.get("Archive/old.txt"));
		Assert.assertEquals("notes", zipEntries.get("notes.txt"));
	}

	private void _testServeResourceDownloadFolder() throws Exception {
		Folder folder = _addFolder(
			"Reports", DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Folder subfolder = _addFolder("2025", folder.getFolderId());

		_addFileEntry("q1", "q1.txt", subfolder.getFolderId());

		_addFileEntry("original", "report.txt", folder.getFolderId());

		Folder otherFolder = _addFolder(
			"Other", DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		FileEntry fileEntry = _addFileEntry(
			"shortcut", "report.txt", otherFolder.getFolderId());

		_dlAppLocalService.addFileShortcut(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			folder.getFolderId(), fileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Map<String, String> zipEntries = _getZipEntries(
			_serveResource(
				_getMockLiferayResourceRequest(
					folder.getFolderId(),
					"/document_library/download_folder")));

		Assert.assertEquals(zipEntries.toString(), 3, zipEntries.size());
		Assert.assertEquals("q1", zipEntries.get("2025/q1.txt"));
		Assert.assertTrue(
			zipEntries.toString(), zipEntries.containsKey("report.txt"));
		Assert.assertTrue(
			zipEntries.toString(), zipEntries.containsKey("report (1).txt"));
	}

	private void _testServeResourceDownloadFolderWithoutShortcutTargetPermission()
		throws Exception {

		Folder folder = _addFolder(
			"Shared", DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_addFileEntry("visible", "visible.txt", folder.getFolderId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "private.txt",
			ContentTypes.TEXT_PLAIN, "private".getBytes(), null, null, null,
			serviceContext);

		_dlAppLocalService.addFileShortcut(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			folder.getFolderId(), fileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_user = UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Map<String, String> zipEntries = _getZipEntries(
				_serveResource(
					_getMockLiferayResourceRequest(
						folder.getFolderId(),
						"/document_library/download_folder")));

			Assert.assertEquals(zipEntries.toString(), 1, zipEntries.size());
			Assert.assertEquals("visible", zipEntries.get("visible.txt"));
		}
	}

	private void _testServeResourceMaxSizeToDownload() throws Exception {
		_portletFileRepository.addPortletFileEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			Group.class.getName(), _group.getGroupId(),
			RandomTestUtil.randomString(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, new byte[200],
			"attachment.txt", ContentTypes.TEXT_PLAIN, false);

		_addFileEntry(
			"small", "small.txt", DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.document.library.internal.configuration." +
						"DLSizeLimitConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"maxSizeToDownload", 100L
					).build())) {

			Map<String, String> zipEntries = _getZipEntries(
				_serveResource(
					_getMockLiferayResourceRequest(
						DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
						"/document_library/download_folder")));

			Assert.assertEquals("small", zipEntries.get("small.txt"));

			_addFileEntry(
				RandomTestUtil.randomString(150), "large.txt",
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			Assert.assertEquals(
				_language.format(
					LocaleUtil.US,
					"the-total-size-of-all-items-to-download-must-not-exceed-x",
					_language.formatStorageSize(100, LocaleUtil.US)),
				new String(
					_serveResource(
						_getMockLiferayResourceRequest(
							DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
							"/document_library/download_folder"))));
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject(filter = "mvc.command.name=/document_library/download_folder")
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@DeleteAfterTestRun
	private User _user;

}