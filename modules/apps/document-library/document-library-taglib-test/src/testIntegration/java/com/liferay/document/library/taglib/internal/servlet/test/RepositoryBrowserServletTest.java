/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.taglib.internal.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.petra.memory.DeleteFileFinalizeAction;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upload.test.util.UploadTestUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.HashMap;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class RepositoryBrowserServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testDoPutFileEntryWithGuestPermissions() throws Exception {
		String fileName = RandomTestUtil.randomString();

		_servlet.service(
			_getMockMultipartHttpServletRequest(
				fileName, false, new MockMultipartFile("file", _BYTES), true),
			new MockHttpServletResponse());

		_getFileEntry(fileName);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDoPutFileEntryWithoutGuestPermissions() throws Exception {
		String fileName = RandomTestUtil.randomString();

		_servlet.service(
			_getMockMultipartHttpServletRequest(
				fileName, false, new MockMultipartFile("file", _BYTES), false),
			new MockHttpServletResponse());

		_getFileEntry(fileName);
	}

	@Test
	public void testDoPutFolderWithGuestPermissions() throws Exception {
		String name = RandomTestUtil.randomString();

		_servlet.service(
			_getMockMultipartHttpServletRequest(0, false, "PUT", name, true),
			new MockHttpServletResponse());

		_getFolder(name);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testDoPutFolderWithoutGuestPermissions() throws Exception {
		String name = RandomTestUtil.randomString();

		_servlet.service(
			_getMockMultipartHttpServletRequest(0, false, "PUT", name, false),
			new MockHttpServletResponse());

		_getFolder(name);
	}

	@Test
	@TestInfo("LPD-55643")
	public void testIncludeExtension() throws Exception {
		_testIncludeExtension("testIncludeExtension", false);
		_testIncludeExtension("testIncludeExtension", true);
		_testIncludeExtension("testIncludeExtension.txt", false);
		_testIncludeExtension("testIncludeExtension.txt", true);
	}

	private FileItem _createFileItem(byte[] bytes, String fileName)
		throws Exception {

		Path tempFilePath = Files.createTempFile(null, ".txt");

		Files.write(tempFilePath, bytes);

		File tempFile = tempFilePath.toFile();

		FinalizeManager.register(
			tempFile, new DeleteFileFinalizeAction(tempFile.getAbsolutePath()),
			FinalizeManager.PHANTOM_REFERENCE_FACTORY);

		return ProxyUtil.newDelegateProxyInstance(
			FileItem.class.getClassLoader(), FileItem.class,
			new Object() {

				public void delete() {
					tempFile.delete();
				}

				public String getContentType() {
					return StringPool.BLANK;
				}

				public String getFileName() {
					return fileName;
				}

				public String getFullFileName() {
					return tempFile.getName();
				}

				public InputStream getInputStream() throws IOException {
					return new FileInputStream(tempFile);
				}

				public long getSize() {
					return bytes.length;
				}

				public int getSizeThreshold() {
					return 1024;
				}

				public File getStoreLocation() {
					return tempFile;
				}

				public boolean isFormField() {
					return true;
				}

				public boolean isInMemory() {
					return false;
				}

			},
			null);
	}

	private FileEntry _getFileEntry(String title) throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, _permissionCheckerFactory.create(_user))) {

			return _dlAppService.getFileEntry(
				_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				title);
		}
	}

	private Folder _getFolder(String name) throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, _permissionCheckerFactory.create(_user))) {

			return _dlAppService.getFolder(
				_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				name);
		}
	}

	private MockMultipartHttpServletRequest _getMockMultipartHttpServletRequest(
			long fileEntryId, boolean includeExtension, String method,
			String name, boolean viewableByGuest)
		throws Exception {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, RandomTestUtil.randomString());
		mockMultipartHttpServletRequest.setAttribute(
			WebKeys.USER, TestPropsValues.getUser());
		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());
		mockMultipartHttpServletRequest.setMethod(method);
		mockMultipartHttpServletRequest.setParameter(
			"fileEntryId", String.valueOf(fileEntryId));
		mockMultipartHttpServletRequest.setParameter(
			"includeExtension", String.valueOf(includeExtension));
		mockMultipartHttpServletRequest.setParameter("name", name);
		mockMultipartHttpServletRequest.setParameter(
			"repositoryId", String.valueOf(_group.getGroupId()));
		mockMultipartHttpServletRequest.setParameter(
			"viewableByGuest", String.valueOf(viewableByGuest));

		return mockMultipartHttpServletRequest;
	}

	private HttpServletRequest _getMockMultipartHttpServletRequest(
			String fileName, boolean includeExtension,
			MockMultipartFile mockMultipartFile, boolean viewableByGuest)
		throws Exception {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			_getMockMultipartHttpServletRequest(
				0, includeExtension, "PUT", null, viewableByGuest);

		mockMultipartHttpServletRequest.addFile(mockMultipartFile);
		mockMultipartHttpServletRequest.setContent(_BYTES);
		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());

		return UploadTestUtil.createUploadPortletRequest(
			UploadTestUtil.createUploadServletRequest(
				mockMultipartHttpServletRequest,
				HashMapBuilder.put(
					"file", new FileItem[] {_createFileItem(_BYTES, fileName)}
				).build(),
				new HashMap<>()),
			null, RandomTestUtil.randomString());
	}

	private void _testIncludeExtension(
			String fileName, boolean includeExtension)
		throws Exception {

		_servlet.service(
			_getMockMultipartHttpServletRequest(
				fileName, includeExtension,
				new MockMultipartFile("file", _BYTES), true),
			new MockHttpServletResponse());

		String title = fileName;

		if (!includeExtension) {
			title = FileUtil.stripExtension(fileName);
		}

		FileEntry fileEntry = _getFileEntry(title);

		Assert.assertEquals(fileName, fileEntry.getFileName());

		_testIncludeExtensionUpdateName(
			fileEntry, includeExtension, "testIncludeExtensionUpdateName");
		_testIncludeExtensionUpdateName(
			fileEntry, includeExtension, "testIncludeExtensionUpdateName.TXT");
		_testIncludeExtensionUpdateName(
			fileEntry, includeExtension, "testIncludeExtensionUpdateName.pdf");
		_testIncludeExtensionUpdateName(
			fileEntry, includeExtension, "testIncludeExtensionUpdateName.txt");

		_dlAppService.deleteFileEntry(fileEntry.getFileEntryId());
	}

	private void _testIncludeExtensionUpdateName(
			FileEntry fileEntry, boolean includeExtension, String name)
		throws Exception {

		_servlet.service(
			_getMockMultipartHttpServletRequest(
				fileEntry.getFileEntryId(), includeExtension, "POST", name,
				true),
			new MockHttpServletResponse());

		String title = name;

		if (includeExtension &&
			Objects.equals(fileEntry.getExtension(), "txt") &&
			!StringUtil.endsWith(StringUtil.toLowerCase(name), ".txt")) {

			title = name + ".txt";
		}

		_getFileEntry(title);
	}

	private static final byte[] _BYTES =
		"Enterprise. Open Source. For Life.".getBytes();

	@Inject
	private DLAppService _dlAppService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject(
		filter = "osgi.http.whiteboard.servlet.name=com.liferay.document.library.taglib.internal.servlet.RepositoryBrowserServlet"
	)
	private Servlet _servlet;

	@DeleteAfterTestRun
	private User _user;

}