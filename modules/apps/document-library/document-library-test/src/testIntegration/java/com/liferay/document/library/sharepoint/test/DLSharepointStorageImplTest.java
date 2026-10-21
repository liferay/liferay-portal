/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.sharepoint.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class DLSharepointStorageImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

		Class<?> clazz = classLoader.loadClass(
			"com.liferay.portlet.documentlibrary.sharepoint." +
				"DLSharepointStorageImpl");

		Constructor<?> constructor = clazz.getConstructor();

		_dlSharepointStorageImpl = constructor.newInstance();

		_sharepointRequestClass = classLoader.loadClass(
			"com.liferay.portal.sharepoint.SharepointRequest");
	}

	@Test
	@TestInfo("LPD-110190")
	public void testCreateFolder() throws Exception {
		_testCreateFolder(false);
		_testCreateFolder(true);
	}

	@Test
	@TestInfo("LPD-110190")
	public void testPutDocument() throws Exception {
		_testPutDocument(false);
		_testPutDocument(true);
	}

	private Group _addGroup(boolean publicLayout) throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		if (publicLayout) {
			LayoutTestUtil.addTypeContentLayout(group);
		}

		return group;
	}

	private void _assertGuestViewPermission(
			String className, long classPK, boolean expected)
		throws Exception {

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		Assert.assertEquals(
			expected,
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), className,
				ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(classPK),
				role.getRoleId(), ActionKeys.VIEW));
	}

	private Object _getSharepointRequest(Group group, String name)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setContentType(
			ContentTypes.APPLICATION_OCTET_STREAM);

		Constructor<?> constructor = _sharepointRequestClass.getConstructor(
			HttpServletRequest.class, HttpServletResponse.class, User.class);

		Object sharepointRequest = constructor.newInstance(
			mockHttpServletRequest, new MockHttpServletResponse(),
			TestPropsValues.getUser());

		ReflectionTestUtil.invoke(
			sharepointRequest, "setRootPath", new Class<?>[] {String.class},
			StringBundler.concat(
				group.getFriendlyURL(), "/document_library/", name));

		return sharepointRequest;
	}

	private void _testCreateFolder(boolean publicLayout) throws Exception {
		Group group = _addGroup(publicLayout);

		String name = RandomTestUtil.randomString();

		ReflectionTestUtil.invoke(
			_dlSharepointStorageImpl, "createFolder",
			new Class<?>[] {_sharepointRequestClass},
			_getSharepointRequest(group, name));

		Folder folder = _dlAppLocalService.getFolder(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			name);

		_assertGuestViewPermission(
			DLFolderConstants.getClassName(), folder.getFolderId(),
			publicLayout);
	}

	private void _testPutDocument(boolean publicLayout) throws Exception {
		Group group = _addGroup(publicLayout);

		String title = RandomTestUtil.randomString();

		Object sharepointRequest = _getSharepointRequest(group, title);

		ReflectionTestUtil.invoke(
			sharepointRequest, "setBytes", new Class<?>[] {byte[].class},
			RandomTestUtil.randomBytes());

		ReflectionTestUtil.invoke(
			_dlSharepointStorageImpl, "putDocument",
			new Class<?>[] {_sharepointRequestClass}, sharepointRequest);

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			title);

		_assertGuestViewPermission(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId(),
			publicLayout);
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private Object _dlSharepointStorageImpl;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private Class<?> _sharepointRequestClass;

}