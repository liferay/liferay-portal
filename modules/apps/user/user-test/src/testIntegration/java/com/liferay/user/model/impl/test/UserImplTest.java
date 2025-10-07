/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.model.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class UserImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser(RandomTestUtil.randomString());
	}

	@Test
	public void testGetAllUserRoles() throws Exception {
		List<Role> roles = _user.getAllRoles();

		Assert.assertEquals(roles.toString(), 1, roles.size());

		_addRole();

		roles = _user.getAllRoles();

		Assert.assertEquals(roles.toString(), 2, roles.size());

		_addInheritedRole();

		roles = _user.getAllRoles();

		Assert.assertEquals(roles.toString(), 3, roles.size());

		_addOrganizationRole();

		roles = _user.getAllRoles();

		Assert.assertEquals(roles.toString(), 4, roles.size());

		_addSiteRole();

		roles = _user.getAllRoles();

		Assert.assertEquals(roles.toString(), 5, roles.size());

		_addInheritedSiteRole();

		roles = _user.getAllRoles();

		Assert.assertEquals(roles.toString(), 6, roles.size());
	}

	@Test
	public void testGetInheritedRoles() throws Exception {
		List<Role> roles = _user.getInheritedRoles();

		Assert.assertEquals(roles.toString(), 0, roles.size());

		_addInheritedRole();

		roles = _user.getInheritedRoles();

		Assert.assertEquals(roles.toString(), 1, roles.size());
	}

	@Test
	public void testGetInheritedSiteRoles() throws Exception {
		List<Role> roles = _user.getInheritedSiteRoles();

		Assert.assertEquals(roles.toString(), 0, roles.size());

		_addInheritedSiteRole();

		roles = _user.getInheritedSiteRoles();

		Assert.assertEquals(roles.toString(), 1, roles.size());
	}

	@Test
	public void testGetOrganizationsRoles() throws Exception {
		List<Role> roles = _user.getOrganizationsRoles();

		Assert.assertEquals(roles.toString(), 0, roles.size());

		_addOrganizationRole();

		roles = _user.getOrganizationsRoles();

		Assert.assertEquals(roles.toString(), 1, roles.size());
	}

	@Test
	public void testGetSiteRoles() throws Exception {
		List<Role> roles = _user.getSiteRoles();

		Assert.assertEquals(roles.toString(), 0, roles.size());

		_addSiteRole();

		roles = _user.getSiteRoles();

		Assert.assertEquals(roles.toString(), 1, roles.size());
	}

	private void _addInheritedRole() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		Role role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

		_groupLocalService.addRoleGroup(
			role.getRoleId(), userGroup.getGroupId());

		_userLocalService.addUserGroupUser(
			userGroup.getUserGroupId(), _user.getUserId());

		_user = _userLocalService.getUser(_user.getUserId());
	}

	private void _addInheritedSiteRole() throws Exception {
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		Group group = GroupTestUtil.addGroup();

		_groupLocalService.addUserGroupGroup(
			userGroup.getUserGroupId(), group.getGroupId());

		Role role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_SITE);

		_userGroupGroupRoleLocalService.addUserGroupGroupRoles(
			userGroup.getUserGroupId(), group.getGroupId(),
			new long[] {role.getRoleId()});

		_userLocalService.addUserGroupUser(
			userGroup.getUserGroupId(), _user.getUserId());
	}

	private void _addOrganizationRole() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();

		Role role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_ORGANIZATION);

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), organization.getGroupId(), role.getRoleId());
	}

	private void _addRole() throws Exception {
		Role role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUser(role.getRoleId(), _user.getUserId());
	}

	private void _addSiteRole() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Role role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_SITE);

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), group.getGroupId(), role.getRoleId());
	}

	@Inject
	private GroupLocalService _groupLocalService;

	private User _user;

	@Inject
	private UserGroupGroupRoleLocalService _userGroupGroupRoleLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}