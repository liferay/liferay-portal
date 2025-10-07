/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.UserBag;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.security.permission.UserBagFactoryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class UserBagFactoryUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_parentGroup = GroupTestUtil.addGroup();

		_childGroup = GroupTestUtil.addGroup(_parentGroup.getGroupId());

		_parentOrganization = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), true);

		_childOrganization = OrganizationTestUtil.addOrganization(
			_parentOrganization.getOrganizationId(),
			RandomTestUtil.randomString(), true);

		_userGroup = UserGroupTestUtil.addUserGroup(_childGroup.getGroupId());

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testGetGroups() throws Exception {
		Collection<Group> groups = getGroups();

		Assert.assertEquals(groups.toString(), 1, groups.size());

		Collection<Group> userGroups = getUserGroups();

		Assert.assertTrue(userGroups.containsAll(groups));

		Collection<Group> userOrgGroups = getUserOrgGroups();
		Collection<Group> userUserGroupGroups = getUserUserGroupGroups();

		groups = getGroups();

		Assert.assertEquals(groups.toString(), 5, groups.size());

		Assert.assertEquals(userGroups.toString(), 2, userGroups.size());
		Assert.assertEquals(userOrgGroups.toString(), 2, userOrgGroups.size());
		Assert.assertEquals(
			userUserGroupGroups.toString(), 1, userUserGroupGroups.size());

		groups = new HashSet<>(groups);
		userGroups = new HashSet<>(userGroups);
		userOrgGroups = new HashSet<>(userOrgGroups);

		Assert.assertEquals(groups.toString(), 5, groups.size());
		Assert.assertEquals(userGroups.toString(), 2, userGroups.size());
		Assert.assertEquals(userOrgGroups.toString(), 2, userOrgGroups.size());
		Assert.assertEquals(
			userUserGroupGroups.toString(), 1, userUserGroupGroups.size());
	}

	@Test
	public void testGetInheritedRoles() throws Exception {
		Role inheritedRole = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_groupLocalService.addRoleGroup(inheritedRole.getRoleId(), _childGroup);

		_userLocalService.addGroupUser(_childGroup.getGroupId(), _user);

		UserBag userBag = getUserBag();

		Assert.assertTrue(userBag.hasRole(inheritedRole));
	}

	@Test
	public void testGetRoles() throws Exception {
		Role regularRole = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUser(regularRole.getRoleId(), _user);

		long groupRoleId = RoleTestUtil.addGroupRole(_childGroup.getGroupId());

		_userLocalService.addRoleUser(groupRoleId, _user);

		long organizationRoleId = RoleTestUtil.addOrganizationRole(
			_childOrganization.getGroupId());

		_userLocalService.addRoleUser(organizationRoleId, _user);

		UserBag userBag = getUserBag();

		List<Role> roles = new ArrayList<>(userBag.getRoles());

		long[] roleIds = ListUtil.toLongArray(roles, Role.ROLE_ID_ACCESSOR);

		Assert.assertEquals(Arrays.toString(roleIds), 4, roleIds.length);
		Assert.assertTrue(ArrayUtil.contains(roleIds, regularRole.getRoleId()));
		Assert.assertTrue(ArrayUtil.contains(roleIds, groupRoleId));
		Assert.assertTrue(ArrayUtil.contains(roleIds, organizationRoleId));
	}

	@Test
	public void testGetUserGroups() throws Exception {
		Collection<Group> userGroups = getUserGroups();

		Assert.assertEquals(userGroups.toString(), 2, userGroups.size());
		Assert.assertTrue(
			userGroups.toString(), userGroups.contains(_childGroup));
		Assert.assertFalse(
			userGroups.toString(), userGroups.contains(_parentGroup));
	}

	@Test
	public void testGetUserId() throws Exception {
		UserBag userBag = getUserBag();

		Assert.assertEquals(_user.getUserId(), userBag.getUserId());
	}

	@Test
	public void testGetUserOrgGroups() throws Exception {
		Collection<Group> groups = getUserOrgGroups();

		Assert.assertEquals(groups.toString(), 2, groups.size());
		Assert.assertTrue(
			groups.toString(), groups.contains(_childOrganization.getGroup()));
		Assert.assertTrue(
			groups.toString(), groups.contains(_parentOrganization.getGroup()));
	}

	@Test
	public void testGetUserOrgs() throws Exception {
		Collection<Organization> organizations = getUserOrgs();

		Assert.assertEquals(organizations.toString(), 2, organizations.size());
		Assert.assertTrue(
			organizations.toString(),
			organizations.contains(_childOrganization));
		Assert.assertTrue(
			organizations.toString(),
			organizations.contains(_parentOrganization));
	}

	@Test
	public void testGetUserUserGroupGroups() throws Exception {
		Collection<Group> groups = getUserUserGroupGroups();

		Assert.assertTrue(
			groups.toString(), groups.contains(_userGroup.getGroup()));
	}

	protected Collection<Group> getGroups() throws Exception {
		UserBag userBag = getUserBag();

		return userBag.getGroups();
	}

	protected UserBag getUserBag() throws Exception {
		return UserBagFactoryUtil.create(
			_userLocalService.getUser(_user.getUserId()));
	}

	protected Collection<Group> getUserGroups() throws Exception {
		_userLocalService.addGroupUser(_childGroup.getGroupId(), _user);

		UserBag userBag = getUserBag();

		return userBag.getUserGroups();
	}

	protected Collection<Group> getUserOrgGroups() throws Exception {
		_userLocalService.addOrganizationUser(
			_childOrganization.getOrganizationId(), _user.getUserId());

		UserBag userBag = getUserBag();

		return userBag.getUserOrgGroups();
	}

	protected Collection<Organization> getUserOrgs() throws Exception {
		_userLocalService.addOrganizationUser(
			_childOrganization.getOrganizationId(), _user.getUserId());

		UserBag userBag = getUserBag();

		return userBag.getUserOrgs();
	}

	protected Collection<Group> getUserUserGroupGroups() throws Exception {
		_userLocalService.addUserGroupUser(
			_userGroup.getUserGroupId(), _user.getUserId());

		UserBag userBag = getUserBag();

		return userBag.getUserUserGroupGroups();
	}

	@DeleteAfterTestRun
	private Group _childGroup;

	@DeleteAfterTestRun
	private Organization _childOrganization;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private Group _parentGroup;

	@DeleteAfterTestRun
	private Organization _parentOrganization;

	@DeleteAfterTestRun
	private User _user;

	@DeleteAfterTestRun
	private UserGroup _userGroup;

	@Inject
	private UserLocalService _userLocalService;

}