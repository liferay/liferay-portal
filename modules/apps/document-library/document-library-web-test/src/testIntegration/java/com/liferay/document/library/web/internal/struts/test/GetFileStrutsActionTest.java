/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Shakir Shamim
 */
@RunWith(Arquillian.class)
public class GetFileStrutsActionTest {

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
	public void testGetFile() throws Exception {
		_testGetFileByIdAsGuestWithOnlyViewPermission();
		_testGetFileByIdAsGuestWithViewAndDownloadPermission();
		_testGetFileByIdWithOnlyDownloadPermission();
		_testGetFileByIdWithOnlyViewPermission();
		_testGetFileByIdWithoutViewAndDownloadPermission();
		_testGetFileByIdWithViewAndDownloadPermission();
		_testGetFileByNameWithOnlyViewPermission();
		_testGetFileByNameWithViewAndDownloadPermission();
		_testGetFileByTitleWithOnlyViewPermission();
		_testGetFileByTitleWithViewAndDownloadPermission();
		_testGetFileByUuidWithOnlyViewPermission();
		_testGetFileByUuidWithViewAndDownloadPermission();
	}

	private FileEntry _addFileEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			serviceContext);
	}

	private User _addUser(FileEntry fileEntry, String... actionIds)
		throws Exception {

		User user = UserTestUtil.addUser(_group.getGroupId());

		_users.add(user);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roles.add(role);

		_setResourcePermissions(fileEntry, role, actionIds);

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		return user;
	}

	private void _assertFileSent(
		MockHttpServletResponse mockHttpServletResponse) {

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
		Assert.assertArrayEquals(
			TestDataConstants.TEST_BYTE_ARRAY,
			mockHttpServletResponse.getContentAsByteArray());
	}

	private void _assertRedirectedToLogin(
		MockHttpServletResponse mockHttpServletResponse) {

		Assert.assertEquals(
			HttpServletResponse.SC_MOVED_TEMPORARILY,
			mockHttpServletResponse.getStatus());

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertTrue(
			redirectedURL, redirectedURL.contains("/portal/login"));
	}

	private void _assertUnauthorized(
		MockHttpServletResponse mockHttpServletResponse) {

		Assert.assertEquals(
			HttpServletResponse.SC_UNAUTHORIZED,
			mockHttpServletResponse.getStatus());
	}

	private Map<String, String> _getFileEntryIdParameters(FileEntry fileEntry) {
		return HashMapBuilder.put(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId())
		).build();
	}

	private Map<String, String> _getFileEntryNameParameters(FileEntry fileEntry)
		throws Exception {

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			fileEntry.getFileEntryId());

		return HashMapBuilder.put(
			"folderId", String.valueOf(fileEntry.getFolderId())
		).put(
			"groupId", String.valueOf(_group.getGroupId())
		).put(
			"name", dlFileEntry.getName()
		).build();
	}

	private Map<String, String> _getFileEntryTitleParameters(
		FileEntry fileEntry) {

		return HashMapBuilder.put(
			"folderId", String.valueOf(fileEntry.getFolderId())
		).put(
			"groupId", String.valueOf(_group.getGroupId())
		).put(
			"title", fileEntry.getTitle()
		).build();
	}

	private Map<String, String> _getFileEntryUuidParameters(
		FileEntry fileEntry) {

		return HashMapBuilder.put(
			"groupId", String.valueOf(_group.getGroupId())
		).put(
			"uuid", fileEntry.getUuid()
		).build();
	}

	private User _getGuestUser(FileEntry fileEntry, String... actionIds)
		throws Exception {

		_setResourcePermissions(
			fileEntry,
			_roleLocalService.getRole(
				_group.getCompanyId(), RoleConstants.GUEST),
			actionIds);

		return _userLocalService.getGuestUser(_group.getCompanyId());
	}

	private MockHttpServletResponse _getMockHttpServletResponse(
			Map<String, String> parameters, User user)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setRequestURI(
				"/c/document_library/get_file");

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setPermissionChecker(permissionChecker);
			themeDisplay.setScopeGroupId(_group.getGroupId());

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				mockHttpServletRequest.setParameter(
					entry.getKey(), entry.getValue());
			}

			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			_getFileStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse);

			return mockHttpServletResponse;
		}
	}

	private void _setResourcePermissions(
			FileEntry fileEntry, Role role, String... actionIds)
		throws Exception {

		_resourcePermissionLocalService.setResourcePermissions(
			_group.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fileEntry.getFileEntryId()), role.getRoleId(),
			actionIds);
	}

	private void _testGetFileByIdAsGuestWithOnlyViewPermission()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		_assertRedirectedToLogin(
			_getMockHttpServletResponse(
				_getFileEntryIdParameters(fileEntry),
				_getGuestUser(fileEntry, ActionKeys.VIEW)));
	}

	private void _testGetFileByIdAsGuestWithViewAndDownloadPermission()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		_assertFileSent(
			_getMockHttpServletResponse(
				_getFileEntryIdParameters(fileEntry),
				_getGuestUser(
					fileEntry, ActionKeys.DOWNLOAD, ActionKeys.VIEW)));
	}

	private void _testGetFileByIdWithOnlyDownloadPermission() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		_assertUnauthorized(
			_getMockHttpServletResponse(
				_getFileEntryIdParameters(fileEntry),
				_addUser(fileEntry, ActionKeys.DOWNLOAD)));
	}

	private void _testGetFileByIdWithOnlyViewPermission() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		_assertUnauthorized(
			_getMockHttpServletResponse(
				_getFileEntryIdParameters(fileEntry),
				_addUser(fileEntry, ActionKeys.VIEW)));
	}

	private void _testGetFileByIdWithoutViewAndDownloadPermission()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		_assertUnauthorized(
			_getMockHttpServletResponse(
				_getFileEntryIdParameters(fileEntry), _addUser(fileEntry)));
	}

	private void _testGetFileByIdWithViewAndDownloadPermission()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		_assertFileSent(
			_getMockHttpServletResponse(
				_getFileEntryIdParameters(fileEntry),
				_addUser(fileEntry, ActionKeys.DOWNLOAD, ActionKeys.VIEW)));
	}

	private void _testGetFileByNameWithOnlyViewPermission() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		_assertUnauthorized(
			_getMockHttpServletResponse(
				_getFileEntryNameParameters(fileEntry),
				_addUser(fileEntry, ActionKeys.VIEW)));
	}

	private void _testGetFileByNameWithViewAndDownloadPermission()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		_assertFileSent(
			_getMockHttpServletResponse(
				_getFileEntryNameParameters(fileEntry),
				_addUser(fileEntry, ActionKeys.DOWNLOAD, ActionKeys.VIEW)));
	}

	private void _testGetFileByTitleWithOnlyViewPermission() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		_assertUnauthorized(
			_getMockHttpServletResponse(
				_getFileEntryTitleParameters(fileEntry),
				_addUser(fileEntry, ActionKeys.VIEW)));
	}

	private void _testGetFileByTitleWithViewAndDownloadPermission()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		_assertFileSent(
			_getMockHttpServletResponse(
				_getFileEntryTitleParameters(fileEntry),
				_addUser(fileEntry, ActionKeys.DOWNLOAD, ActionKeys.VIEW)));
	}

	private void _testGetFileByUuidWithOnlyViewPermission() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		_assertUnauthorized(
			_getMockHttpServletResponse(
				_getFileEntryUuidParameters(fileEntry),
				_addUser(fileEntry, ActionKeys.VIEW)));
	}

	private void _testGetFileByUuidWithViewAndDownloadPermission()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		_assertFileSent(
			_getMockHttpServletResponse(
				_getFileEntryUuidParameters(fileEntry),
				_addUser(fileEntry, ActionKeys.DOWNLOAD, ActionKeys.VIEW)));
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject(filter = "path=/document_library/get_file")
	private StrutsAction _getFileStrutsAction;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private final List<Role> _roles = new ArrayList<>();

	@Inject
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}