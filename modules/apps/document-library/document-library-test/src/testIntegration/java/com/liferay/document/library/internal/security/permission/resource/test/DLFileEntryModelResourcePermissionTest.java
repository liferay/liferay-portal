/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.security.permission.resource.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class DLFileEntryModelResourcePermissionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_permissionChecker = PermissionCheckerFactoryUtil.create(_user);
	}

	@Test
	public void testCheckWithPermissionsViewDynamicInheritanceAndWithNoParentFolderPermission()
		throws Exception {

		Role guestRole = _roleLocalService.getRole(
			_group.getCompanyId(), RoleConstants.GUEST);

		DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

		_removeViewResourcePermission(dlFolder, _group, guestRole);

		Assert.assertFalse(
			_dlFolderModelResourcePermission.contains(
				_permissionChecker, dlFolder, ActionKeys.VIEW));

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"PERMISSIONS_VIEW_DYNAMIC_INHERITANCE", true, false)) {

			Assert.assertFalse(
				_dlFileEntryModelResourcePermission.contains(
					_permissionChecker, dlFileEntry, ActionKeys.VIEW));
		}
	}

	@Test
	public void testContainsWithScheduledFileEntryAndRegularUser()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.TEXT_PLAIN,
			StringUtil.randomString(), StringUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, -1, new HashMap<>(), null,
			new ByteArrayInputStream(new byte[0]), 0,
			new Date(System.currentTimeMillis() + Time.MONTH),
			new Date(System.currentTimeMillis() + Time.YEAR), new Date(),
			serviceContext);

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		_dlFileEntryLocalService.updateStatus(
			TestPropsValues.getUserId(), dlFileVersion.getFileVersionId(),
			WorkflowConstants.STATUS_SCHEDULED, serviceContext,
			new HashMap<>());

		Assert.assertFalse(
			_dlFileEntryModelResourcePermission.contains(
				_permissionChecker, dlFileEntry, ActionKeys.DOWNLOAD));
		Assert.assertFalse(
			_dlFileEntryModelResourcePermission.contains(
				_permissionChecker, dlFileEntry, ActionKeys.VIEW));
	}

	private void _removeViewResourcePermission(
			DLFolder dlFolder, Group group, Role guestRole)
		throws Exception {

		_resourcePermissionLocalService.removeResourcePermission(
			group.getCompanyId(), DLFolder.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(dlFolder.getFolderId()), guestRole.getRoleId(),
			ActionKeys.VIEW);

		Map<Object, Object> permissionChecksMap =
			_permissionChecker.getPermissionChecksMap();

		permissionChecksMap.clear();
	}

	@Inject(
		filter = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry"
	)
	private static ModelResourcePermission<DLFileEntry>
		_dlFileEntryModelResourcePermission;

	@Inject(
		filter = "model.class.name=com.liferay.document.library.kernel.model.DLFolder"
	)
	private static ModelResourcePermission<DLFolder>
		_dlFolderModelResourcePermission;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private PermissionChecker _permissionChecker;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

}