/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.webserver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.BaseWebServerTestCase;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class FileEntryWebServerTest extends BaseWebServerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_regularUser = UserTestUtil.addUser();
	}

	@Test
	public void testServiceWithGuestRoleWithoutViewPermission()
		throws Exception {

		FileEntry fileEntry = _addFileEntry(RandomTestUtil.randomString());

		Role guestRole = RoleLocalServiceUtil.getRole(
			group.getCompanyId(), RoleConstants.GUEST);

		_addDownloadResourcePermission(guestRole);

		_removeViewResourcePermission(fileEntry, guestRole);

		String url = StringBundler.concat(
			StringPool.SLASH, fileEntry.getGroupId(), StringPool.SLASH,
			fileEntry.getFolderId(), StringPool.SLASH, fileEntry.getFileName(),
			StringPool.SLASH, fileEntry.getUuid());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_regularUser));
		PrincipalThreadLocal.setName(_regularUser.getUserId());

		MockHttpServletResponse mockHttpServletResponse = service(
			Method.GET, url, Collections.emptyMap(), Collections.emptyMap(),
			_regularUser, null);

		Assert.assertEquals(
			HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
	}

	@Test
	public void testServiceWithRegularRoleWithOnlyDownloadPermission()
		throws Exception {

		FileEntry fileEntry = _addFileEntry(RandomTestUtil.randomString());

		Role regularRole = _roleLocalService.addRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), null, 0,
			RandomTestUtil.randomString(), null,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			RoleConstants.TYPE_REGULAR, null, null);

		try {
			_addDownloadResourcePermission(regularRole);

			_userLocalService.addRoleUser(
				regularRole.getRoleId(), _regularUser);

			Role guestRole = RoleLocalServiceUtil.getRole(
				group.getCompanyId(), RoleConstants.GUEST);

			_removeViewResourcePermission(fileEntry, guestRole);

			_removeViewResourcePermission(fileEntry, regularRole);

			String url = StringBundler.concat(
				StringPool.SLASH, fileEntry.getGroupId(), StringPool.SLASH,
				fileEntry.getFolderId(), StringPool.SLASH,
				fileEntry.getFileName(), StringPool.SLASH, fileEntry.getUuid());

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_regularUser));
			PrincipalThreadLocal.setName(_regularUser.getUserId());

			MockHttpServletResponse mockHttpServletResponse = service(
				Method.GET, url, Collections.emptyMap(), Collections.emptyMap(),
				_regularUser, null);

			Assert.assertEquals(
				HttpServletResponse.SC_OK, mockHttpServletResponse.getStatus());
		}
		finally {
			_roleLocalService.deleteRole(regularRole);
		}
	}

	private void _addDownloadResourcePermission(Role guestRole)
		throws Exception {

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(guestRole.getCompanyId()), guestRole.getRoleId(),
			ActionKeys.DOWNLOAD);
	}

	private FileEntry _addFileEntry(String fileName) throws Exception {
		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.APPLICATION_OCTET_STREAM,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), (byte[])null, null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private void _removeViewResourcePermission(FileEntry fileEntry, Role role)
		throws Exception {

		_resourcePermissionLocalService.removeResourcePermission(
			group.getCompanyId(), DLFileEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fileEntry.getFileEntryId()), role.getRoleId(),
			ActionKeys.VIEW);
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private User _regularUser;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}