/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membership.policy.role.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.membershippolicy.MembershipPolicyException;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.security.membership.policy.role.BaseRoleMembershipPolicyTestCase;
import com.liferay.portal.security.membership.policy.test.util.MembershipPolicyTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

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
public class RoleMembershipPolicyRolesTest
	extends BaseRoleMembershipPolicyTestCase {

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
		long[] userIds = addUsers();
		long[] forbiddenRoleIds = addForbiddenRoles();

		MembershipPolicyTestUtil.updateUser(
			UserLocalServiceUtil.getUser(userIds[0]), null,
			new long[] {forbiddenRoleIds[0]}, null, null,
			Collections.<UserGroupRole>emptyList());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testAssignUserToForbiddenRoles() throws Exception {
		long[] userIds = addUsers();

		RoleServiceUtil.addUserRoles(userIds[0], addForbiddenRoles());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testAssignUsersToForbiddenRole() throws Exception {
		long[] forbiddenRoleIds = addForbiddenRoles();

		UserServiceUtil.addRoleUsers(forbiddenRoleIds[0], addUsers());
	}

	@Test
	public void testPropagateWhenAssigningRoleToUsers() throws Exception {
		long[] standardRoleId = addStandardRoles();

		UserServiceUtil.setRoleUsers(standardRoleId[0], addUsers());

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testPropagateWhenAssigningRolesToUser() throws Exception {
		long[] userIds = addUsers();

		MembershipPolicyTestUtil.updateUser(
			UserLocalServiceUtil.getUser(userIds[0]), null, addStandardRoles(),
			null, null, Collections.<UserGroupRole>emptyList());

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testPropagateWhenAssigningUserToRoles() throws Exception {
		long[] userIds = addUsers();

		RoleServiceUtil.addUserRoles(userIds[0], addStandardRoles());

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testPropagateWhenAssigningUsersToRole() throws Exception {
		long[] standardRoleId = addStandardRoles();

		UserServiceUtil.addRoleUsers(standardRoleId[0], addUsers());

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testPropagateWhenUnassigningRolesFromUser() throws Exception {
		long[] userIds = addUsers();

		User user = UserLocalServiceUtil.getUser(userIds[0]);

		RoleServiceUtil.addUserRoles(user.getUserId(), addStandardRoles());

		MembershipPolicyTestUtil.updateUser(
			user, null, null, null, null,
			Collections.<UserGroupRole>emptyList());

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testPropagateWhenUnassigningUserFromRoles() throws Exception {
		long[] userIds = addUsers();

		RoleServiceUtil.unsetUserRoles(userIds[0], addStandardRoles());

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testPropagateWhenUnassigningUsersFromRole() throws Exception {
		long[] standardRoles = addStandardRoles();

		UserServiceUtil.unsetRoleUsers(standardRoles[0], addUsers());

		Assert.assertTrue(isPropagateRoles());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testSetForbbidenRoleToUsers() throws Exception {
		long[] forbiddenRoleIds = addForbiddenRoles();

		UserServiceUtil.setRoleUsers(forbiddenRoleIds[0], addUsers());
	}

	@Test
	public void testUnassignRequiredRolesFromUser() throws Exception {
		long[] userIds = addUsers();

		RoleServiceUtil.addUserRoles(userIds[0], addRequiredRoles());

		User user = UserLocalServiceUtil.getUser(userIds[0]);

		List<Role> initialUserRoles = RoleLocalServiceUtil.getUserRoles(
			user.getUserId());

		MembershipPolicyTestUtil.updateUser(
			user, null, null, null, null,
			Collections.<UserGroupRole>emptyList());

		List<Role> currentUserRoles = RoleLocalServiceUtil.getUserRoles(
			user.getUserId());

		Assert.assertEquals(
			currentUserRoles.toString(), initialUserRoles.size(),
			currentUserRoles.size());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testUnassignUserFromRequiredRoles() throws Exception {
		long[] userIds = addUsers();

		RoleServiceUtil.unsetUserRoles(userIds[0], addRequiredRoles());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testUnassignUsersFromRequiredRole() throws Exception {
		long[] requiredRoleIds = addRequiredRoles();

		UserServiceUtil.unsetRoleUsers(requiredRoleIds[0], addUsers());
	}

	@Test
	public void testUnassignUsersFromRole() throws Exception {
		long[] standardRoleIds = addStandardRoles();

		UserServiceUtil.unsetRoleUsers(standardRoleIds[0], addUsers());

		Assert.assertTrue(isPropagateRoles());
	}

	@Test
	public void testVerifyWhenAddingRole() throws Exception {
		MembershipPolicyTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		Assert.assertTrue(isVerify());
	}

	@Test
	public void testVerifyWhenUpdatingRole() throws Exception {
		Role role = MembershipPolicyTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		RoleServiceUtil.updateRole(
			role.getExternalReferenceCode(), role.getRoleId(),
			RandomTestUtil.randomString(), role.getTitleMap(),
			role.getDescriptionMap(), role.getSubtype(), new ServiceContext());

		Assert.assertTrue(isVerify());
	}

}