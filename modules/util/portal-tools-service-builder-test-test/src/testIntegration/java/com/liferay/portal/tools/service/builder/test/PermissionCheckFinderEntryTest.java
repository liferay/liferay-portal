/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.model.PermissionCheckFinderEntry;
import com.liferay.portal.tools.service.builder.test.service.PermissionCheckFinderEntryLocalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eric Yan
 */
@RunWith(Arquillian.class)
public class PermissionCheckFinderEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_adminGroupId = TestPropsValues.getGroupId();
		_adminUser = TestPropsValues.getUser();
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();
		_ownerRole = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.OWNER);

		_permissionCheckFinderEntry1 = _addPermissionCheckFinderEntry(
			_adminGroupId, _adminUser.getUserId());
		_permissionedUser = UserTestUtil.addUser(new long[0]);

		_user = UserTestUtil.addUser(new long[0]);

		_permissionCheckFinderEntry2 = _addPermissionCheckFinderEntry(
			_group1.getGroupId(), _user.getUserId());
		_permissionCheckFinderEntry3 = _addPermissionCheckFinderEntry(
			_group2.getGroupId(), _user.getUserId());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		_testFilterFindByGroupId(
			Collections.emptyList(), Collections.emptyList(),
			Collections.emptyList(), Collections.emptyList(),
			Collections.emptyList());
	}

	@Test
	public void testFilterFindByGroupIdWithScopeCompany() throws Exception {
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.setUserRoles(
			_permissionedUser.getUserId(),
			ArrayUtil.append(
				_permissionedUser.getRoleIds(), _role.getRoleId()));

		_addResourcePermission(
			TestPropsValues.getCompanyId(),
			PermissionCheckFinderEntry.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), _role.getRoleId(),
			ActionKeys.VIEW);

		_testFilterFindByGroupId(
			Collections.singletonList(_permissionCheckFinderEntry1),
			Collections.singletonList(_permissionCheckFinderEntry2),
			Collections.singletonList(_permissionCheckFinderEntry3),
			Arrays.asList(
				_permissionCheckFinderEntry2, _permissionCheckFinderEntry3),
			Arrays.asList(
				_permissionCheckFinderEntry1, _permissionCheckFinderEntry2));
	}

	@Test
	public void testFilterFindByGroupIdWithScopeGroup() throws Exception {
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roleLocalService.setUserRoles(
			_permissionedUser.getUserId(),
			ArrayUtil.append(
				_permissionedUser.getRoleIds(), _role.getRoleId()));

		_addResourcePermission(
			TestPropsValues.getCompanyId(),
			PermissionCheckFinderEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group1.getGroupId()),
			_role.getRoleId(), ActionKeys.VIEW);
		_addResourcePermission(
			TestPropsValues.getCompanyId(),
			PermissionCheckFinderEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group2.getGroupId()),
			_role.getRoleId(), ActionKeys.VIEW);

		_testFilterFindByGroupId(
			Collections.emptyList(),
			Collections.singletonList(_permissionCheckFinderEntry2),
			Collections.singletonList(_permissionCheckFinderEntry3),
			Arrays.asList(
				_permissionCheckFinderEntry2, _permissionCheckFinderEntry3),
			Collections.singletonList(_permissionCheckFinderEntry2));
	}

	@Test
	public void testFilterFindByGroupIdWithScopeGroupTemplate()
		throws Exception {

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		_userGroupRoleLocalService.addUserGroupRole(
			_permissionedUser.getUserId(), _group1.getGroupId(),
			_role.getRoleId());
		_userGroupRoleLocalService.addUserGroupRole(
			_permissionedUser.getUserId(), _group2.getGroupId(),
			_role.getRoleId());

		_addResourcePermission(
			TestPropsValues.getCompanyId(),
			PermissionCheckFinderEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			_role.getRoleId(), ActionKeys.VIEW);

		_testFilterFindByGroupId(
			Collections.emptyList(),
			Collections.singletonList(_permissionCheckFinderEntry2),
			Collections.singletonList(_permissionCheckFinderEntry3),
			Arrays.asList(
				_permissionCheckFinderEntry2, _permissionCheckFinderEntry3),
			Collections.singletonList(_permissionCheckFinderEntry2));
	}

	private PermissionCheckFinderEntry _addPermissionCheckFinderEntry(
			long groupId, long userId)
		throws Exception {

		PermissionCheckFinderEntry permissionCheckFinderEntry =
			_permissionCheckFinderEntryLocalService.
				addPermissionCheckFinderEntry(
					TestPropsValues.getCompanyId(), groupId,
					RandomTestUtil.nextInt(), RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), userId);

		_permissionCheckFinderEntries.add(permissionCheckFinderEntry);
		_resourcePermissions.add(
			_resourcePermissionLocalService.getResourcePermission(
				TestPropsValues.getCompanyId(),
				PermissionCheckFinderEntry.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(permissionCheckFinderEntry.getPrimaryKey()),
				_ownerRole.getRoleId()));

		return permissionCheckFinderEntry;
	}

	private void _addResourcePermission(
			long companyId, String name, int scope, String primKey, long roleId,
			String actionId)
		throws Exception {

		_resourcePermissionLocalService.addResourcePermission(
			companyId, name, scope, primKey, roleId, actionId);

		_resourcePermissions.add(
			_resourcePermissionLocalService.getResourcePermission(
				companyId, name, scope, primKey, roleId));
	}

	private void _assertFilterFindByGroupIdAdminAndOwnerDefaultPermissions() {
		_assertFilterFindByGroupIdPermissions(
			Collections.singletonList(_permissionCheckFinderEntry1),
			Collections.singletonList(_permissionCheckFinderEntry2),
			Collections.singletonList(_permissionCheckFinderEntry3),
			Arrays.asList(
				_permissionCheckFinderEntry2, _permissionCheckFinderEntry3),
			Arrays.asList(
				_permissionCheckFinderEntry1, _permissionCheckFinderEntry2),
			_adminUser);
		_assertFilterFindByGroupIdPermissions(
			Collections.emptyList(),
			Collections.singletonList(_permissionCheckFinderEntry2),
			Collections.singletonList(_permissionCheckFinderEntry3),
			Arrays.asList(
				_permissionCheckFinderEntry2, _permissionCheckFinderEntry3),
			Collections.singletonList(_permissionCheckFinderEntry2), _user);
	}

	private void _assertFilterFindByGroupIdPermissions(
		List<PermissionCheckFinderEntry> expectedEntriesAdminGroup,
		List<PermissionCheckFinderEntry> expectedEntriesGroup1,
		List<PermissionCheckFinderEntry> expectedEntriesGroup2,
		List<PermissionCheckFinderEntry> expectedEntriesGroup1AndGroup2,
		List<PermissionCheckFinderEntry> expectedEntriesGroup1AndAdminGroup,
		User user) {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			Assert.assertEquals(
				expectedEntriesAdminGroup,
				_permissionCheckFinderEntryLocalService.filterFindByGroupId(
					new long[] {_adminGroupId}));
			Assert.assertEquals(
				expectedEntriesGroup1,
				_permissionCheckFinderEntryLocalService.filterFindByGroupId(
					new long[] {_group1.getGroupId()}));
			Assert.assertEquals(
				expectedEntriesGroup2,
				_permissionCheckFinderEntryLocalService.filterFindByGroupId(
					new long[] {_group2.getGroupId()}));
			Assert.assertEquals(
				expectedEntriesGroup1AndGroup2,
				_permissionCheckFinderEntryLocalService.filterFindByGroupId(
					new long[] {_group1.getGroupId(), _group2.getGroupId()}));
			Assert.assertEquals(
				expectedEntriesGroup1AndAdminGroup,
				_permissionCheckFinderEntryLocalService.filterFindByGroupId(
					new long[] {_group1.getGroupId(), _adminGroupId}));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private void _testFilterFindByGroupId(
			List<PermissionCheckFinderEntry> expectedEntriesAdminGroup,
			List<PermissionCheckFinderEntry> expectedEntriesGroup1,
			List<PermissionCheckFinderEntry> expectedEntriesGroup2,
			List<PermissionCheckFinderEntry> expectedEntriesGroup1AndGroup2,
			List<PermissionCheckFinderEntry> expectedEntriesGroup1AndAdminGroup)
		throws Exception {

		BasePersistence<?> basePersistence =
			_permissionCheckFinderEntryLocalService.getBasePersistence();

		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				basePersistence, "isPermissionsInMemoryFilterEnabled", null));

		_assertFilterFindByGroupIdAdminAndOwnerDefaultPermissions();
		_assertFilterFindByGroupIdPermissions(
			expectedEntriesAdminGroup, expectedEntriesGroup1,
			expectedEntriesGroup2, expectedEntriesGroup1AndGroup2,
			expectedEntriesGroup1AndAdminGroup, _permissionedUser);

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					basePersistence, "_permissionsInMemoryFilterEnabled",
					false)) {

			Assert.assertFalse(
				ReflectionTestUtil.invoke(
					basePersistence, "isPermissionsInMemoryFilterEnabled",
					null));

			_assertFilterFindByGroupIdAdminAndOwnerDefaultPermissions();
			_assertFilterFindByGroupIdPermissions(
				expectedEntriesAdminGroup, expectedEntriesGroup1,
				expectedEntriesGroup2, expectedEntriesGroup1AndGroup2,
				expectedEntriesGroup1AndAdminGroup, _permissionedUser);
		}
	}

	private long _adminGroupId;
	private User _adminUser;

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	private Role _ownerRole;

	@DeleteAfterTestRun
	private List<PermissionCheckFinderEntry> _permissionCheckFinderEntries =
		new ArrayList<>();

	private PermissionCheckFinderEntry _permissionCheckFinderEntry1;
	private PermissionCheckFinderEntry _permissionCheckFinderEntry2;
	private PermissionCheckFinderEntry _permissionCheckFinderEntry3;

	@Inject
	private PermissionCheckFinderEntryLocalService
		_permissionCheckFinderEntryLocalService;

	@DeleteAfterTestRun
	private User _permissionedUser;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private List<ResourcePermission> _resourcePermissions = new ArrayList<>();

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}