/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.test.util.DepotTestUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class DepotUserServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testUnsetGroupUsersFailsWhenUserHasNoAssignMembersPermission()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withAssetLibraryMember(
			depotEntry,
			user -> {
				_setUpPermissionThreadLocal(user);

				PrincipalThreadLocal.setName(user.getUserId());

				_userService.unsetGroupUsers(
					depotEntry.getGroupId(), new long[] {user.getUserId()},
					ServiceContextTestUtil.getServiceContext(
						depotEntry.getGroupId(), user.getUserId()));
			});
	}

	@Test
	public void testUnsetGroupUsersWithDepotMemberPermission()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withAssetLibraryAdministrator(
			depotEntry,
			user -> {
				_setUpPermissionThreadLocal(user);

				PrincipalThreadLocal.setName(user.getUserId());
				_user = UserTestUtil.addUser();

				Role role = _roleLocalService.getRole(
					depotEntry.getCompanyId(),
					DepotRolesConstants.ASSET_LIBRARY_MEMBER);

				_userGroupRoleLocalService.addUserGroupRoles(
					_user.getUserId(), depotEntry.getGroupId(),
					new long[] {role.getRoleId()});

				_userService.unsetGroupUsers(
					depotEntry.getGroupId(), new long[] {_user.getUserId()},
					ServiceContextTestUtil.getServiceContext(
						depotEntry.getGroupId(), user.getUserId()));

				Assert.assertFalse(
					_userService.hasGroupUser(
						depotEntry.getGroupId(), _user.getUserId()));

				List<UserGroupRole> userGroupRoles =
					_userGroupRoleLocalService.getUserGroupRoles(
						_user.getUserId());

				Assert.assertEquals(
					userGroupRoles.toString(), 0, userGroupRoles.size());
			});
	}

	@Test
	public void testUnsetGroupUsersWithDepotOwnerPermission() throws Exception {
		DepotEntry depotEntry = _addDepotEntry(TestPropsValues.getUserId());

		DepotTestUtil.withAssetLibraryAdministrator(
			depotEntry,
			user -> {
				_setUpPermissionThreadLocal(user);

				PrincipalThreadLocal.setName(user.getUserId());

				_user = UserTestUtil.addUser();

				Role role = _roleLocalService.getRole(
					depotEntry.getCompanyId(),
					DepotRolesConstants.ASSET_LIBRARY_OWNER);

				_userGroupRoleLocalService.addUserGroupRoles(
					_user.getUserId(), depotEntry.getGroupId(),
					new long[] {role.getRoleId()});

				_userService.unsetGroupUsers(
					depotEntry.getGroupId(), new long[] {_user.getUserId()},
					ServiceContextTestUtil.getServiceContext(
						depotEntry.getGroupId(), user.getUserId()));

				Assert.assertFalse(
					_userService.hasGroupUser(
						depotEntry.getGroupId(), _user.getUserId()));

				List<UserGroupRole> userGroupRoles =
					_userGroupRoleLocalService.getUserGroupRoles(
						_user.getUserId());

				Assert.assertEquals(
					userGroupRoles.toString(), 0, userGroupRoles.size());
			});
	}

	private DepotEntry _addDepotEntry(long userId) throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), userId));

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private void _setUpPermissionThreadLocal(User user) {
		PermissionThreadLocal.setPermissionChecker(
			_permissionCheckerFactory.create(user));
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserService _userService;

}