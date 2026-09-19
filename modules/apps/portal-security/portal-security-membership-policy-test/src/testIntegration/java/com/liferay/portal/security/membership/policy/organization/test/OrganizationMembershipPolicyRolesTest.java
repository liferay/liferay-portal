/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membership.policy.organization.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.membershippolicy.MembershipPolicyException;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.security.membership.policy.organization.BaseOrganizationMembershipPolicyTestCase;
import com.liferay.portal.security.membership.policy.test.util.MembershipPolicyTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class OrganizationMembershipPolicyRolesTest
	extends BaseOrganizationMembershipPolicyTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		ExpandoTableLocalServiceUtil.deleteTables(
			TestPropsValues.getCompanyId(), Role.class.getName());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testAssignUserToForbiddenRole() throws Exception {
		List<UserGroupRole> userGroupRoles = new ArrayList<>();

		long[] userIds = addUsers();
		long[] forbiddenRoleIds = addForbiddenRoles();

		UserGroupRole userGroupRole =
			UserGroupRoleLocalServiceUtil.createUserGroupRole(0);

		userGroupRole.setUserId(userIds[0]);
		userGroupRole.setGroupId(organization.getGroupId());
		userGroupRole.setRoleId(forbiddenRoleIds[0]);

		userGroupRoles.add(userGroupRole);

		MembershipPolicyTestUtil.updateUser(
			UserLocalServiceUtil.getUser(userIds[0]), null, null, null, null,
			userGroupRoles);
	}

	@Test(expected = MembershipPolicyException.class)
	public void testAssignUserToForbiddenRoles() throws Exception {
		long[] userIds = addUsers();

		UserGroupRoleServiceUtil.addUserGroupRoles(
			userIds[0], organization.getGroupId(), addForbiddenRoles());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testAssignUsersToForbiddenRole() throws Exception {
		long[] forbiddenRoleIds = addForbiddenRoles();

		UserGroupRoleServiceUtil.addUserGroupRoles(
			addUsers(), organization.getGroupId(), forbiddenRoleIds[0]);
	}

	@Test
	public void testPropagateWhenAssigningRolesToUser() throws Exception {
		List<UserGroupRole> userGroupRoles = new ArrayList<>();

		long[] userIds = addUsers();
		long[] standardRoleIds = addStandardRoles();

		UserGroupRole userGroupRole =
			UserGroupRoleLocalServiceUtil.createUserGroupRole(0);

		userGroupRole.setUserId(userIds[0]);
		userGroupRole.setGroupId(organization.getGroupId());
		userGroupRole.setRoleId(standardRoleIds[0]);

		userGroupRoles.add(userGroupRole);

		MembershipPolicyTestUtil.updateUser(
			UserLocalServiceUtil.getUser(userIds[0]), null, null, null, null,
			userGroupRoles);

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testPropagateWhenAssigningUserToRoles() throws Exception {
		long[] userIds = addUsers();

		UserGroupRoleServiceUtil.addUserGroupRoles(
			userIds[0], organization.getGroupId(), addStandardRoles());

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testPropagateWhenAssigningUsersToRole() throws Exception {
		long[] standardRoleIds = addStandardRoles();

		UserGroupRoleServiceUtil.addUserGroupRoles(
			addUsers(), organization.getGroupId(), standardRoleIds[0]);

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testPropagateWhenUnassigningRolesFromUser() throws Exception {
		long[] userIds = addUsers();

		User user = UserLocalServiceUtil.getUser(userIds[0]);

		UserGroupRoleServiceUtil.addUserGroupRoles(
			user.getUserId(), organization.getGroupId(), addStandardRoles());

		MembershipPolicyTestUtil.updateUser(
			user, null, null, null, null,
			Collections.<UserGroupRole>emptyList());

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testPropagateWhenUnassigningUserFromRoles() throws Exception {
		long[] userIds = addUsers();

		UserGroupRoleServiceUtil.deleteUserGroupRoles(
			userIds[0], organization.getGroupId(), addStandardRoles());

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testUnassignRequiredRolesFromUser() throws Exception {
		long[] userIds = addUsers();

		UserGroupRoleServiceUtil.addUserGroupRoles(
			userIds[0], organization.getGroupId(), addRequiredRoles());

		User user = UserLocalServiceUtil.getUser(userIds[0]);

		List<UserGroupRole> initialUserGroupRoles =
			UserGroupRoleLocalServiceUtil.getUserGroupRoles(user.getUserId());

		List<UserGroupRole> emptyNonabstractList = new ArrayList<>();

		MembershipPolicyTestUtil.updateUser(
			user, null, null, null, null, emptyNonabstractList);

		List<UserGroupRole> currentUserGroupRoles =
			UserGroupRoleLocalServiceUtil.getUserGroupRoles(user.getUserId());

		Assert.assertEquals(
			currentUserGroupRoles.toString(), initialUserGroupRoles.size(),
			currentUserGroupRoles.size());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testUnassignUserFromRequiredRoles() throws Exception {
		long[] userIds = addUsers();

		UserGroupRoleServiceUtil.deleteUserGroupRoles(
			userIds[0], organization.getGroupId(), addRequiredRoles());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testUnassignUsersFromRequiredRole() throws Exception {
		long[] requiredRoleIds = addRequiredRoles();

		UserGroupRoleServiceUtil.deleteUserGroupRoles(
			addUsers(), organization.getGroupId(), requiredRoleIds[0]);
	}

	@Test
	public void testUnassignUsersFromRole() throws Exception {
		long[] standardRoleIds = addStandardRoles();

		UserGroupRoleServiceUtil.deleteUserGroupRoles(
			addUsers(), organization.getGroupId(), standardRoleIds[0]);

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testVerifyWhenAddingRole() throws Exception {
		MembershipPolicyTestUtil.addRole(RoleConstants.TYPE_ORGANIZATION);

		Assert.assertTrue(isVerify());
	}

	@Test
	public void testVerifyWhenUpdatingRole() throws Exception {
		Role role = MembershipPolicyTestUtil.addRole(
			RoleConstants.TYPE_ORGANIZATION);

		RoleServiceUtil.updateRole(
			role.getExternalReferenceCode(), role.getRoleId(),
			RandomTestUtil.randomString(), role.getTitleMap(),
			role.getDescriptionMap(), role.getSubtype(), new ServiceContext());

		Assert.assertTrue(isVerify());
	}

}