/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membership.policy.usergroup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.security.membershippolicy.MembershipPolicyException;
import com.liferay.portal.kernel.service.UserGroupServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.security.membership.policy.test.util.MembershipPolicyTestUtil;
import com.liferay.portal.security.membership.policy.usergroup.BaseUserGroupMembershipPolicyTestCase;
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
public class UserGroupMembershipPolicyMembershipsTest
	extends BaseUserGroupMembershipPolicyTestCase {

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
			TestPropsValues.getCompanyId(), UserGroup.class.getName());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testAddUserToForbiddenUserGroup() throws Exception {
		MembershipPolicyTestUtil.addUser(
			null, null, null, addForbiddenUserGroups());
	}

	@Test
	public void testAddUserToRequiredUserGroups() throws Exception {
		long[] requiredUserGroupIds = addRequiredUserGroups();

		int initialUserGroupUsersCount =
			UserLocalServiceUtil.getUserGroupUsersCount(
				requiredUserGroupIds[0]);

		MembershipPolicyTestUtil.addUser(
			null, null, null, new long[] {requiredUserGroupIds[0]});

		Assert.assertEquals(
			initialUserGroupUsersCount + 1,
			UserLocalServiceUtil.getUserGroupUsersCount(
				requiredUserGroupIds[0]));
	}

	@Test(expected = MembershipPolicyException.class)
	public void testAssignUsersToForbiddenUserGroup() throws Exception {
		long[] forbiddenUserGroupIds = addForbiddenUserGroups();

		UserServiceUtil.addUserGroupUsers(forbiddenUserGroupIds[0], addUsers());
	}

	@Test
	public void testAssignUsersToRequiredUserGroup() throws Exception {
		long[] requiredUserGroupIds = addRequiredUserGroups();

		int initialUserGroupUsersCount =
			UserLocalServiceUtil.getUserGroupUsersCount(
				requiredUserGroupIds[0]);

		UserServiceUtil.addUserGroupUsers(requiredUserGroupIds[0], addUsers());

		Assert.assertEquals(
			initialUserGroupUsersCount + 2,
			UserLocalServiceUtil.getUserGroupUsersCount(
				requiredUserGroupIds[0]));

		Assert.assertTrue(isPropagateMembership());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testAssignUserToForbiddenUserGroups() throws Exception {
		long[] userIds = addUsers();

		MembershipPolicyTestUtil.updateUser(
			UserLocalServiceUtil.getUser(userIds[0]), null, null, null,
			addForbiddenUserGroups(), Collections.<UserGroupRole>emptyList());
	}

	@Test
	public void testAssignUserToRequiredUserGroups() throws Exception {
		long[] userIds = addUsers();

		long[] requiredUserGroupIds = addRequiredUserGroups();

		int initialUserGroupUsersCount =
			UserLocalServiceUtil.getUserGroupUsersCount(
				requiredUserGroupIds[0]);

		MembershipPolicyTestUtil.updateUser(
			UserLocalServiceUtil.getUser(userIds[0]), null, null, null,
			new long[] {requiredUserGroupIds[0]},
			Collections.<UserGroupRole>emptyList());

		Assert.assertEquals(
			initialUserGroupUsersCount + 1,
			UserLocalServiceUtil.getUserGroupUsersCount(
				requiredUserGroupIds[0]));

		Assert.assertTrue(isPropagateMembership());
	}

	@Test
	public void testPropagateWhenAddingUserToRequiredUserGroups()
		throws Exception {

		MembershipPolicyTestUtil.addUser(
			null, null, null, addRequiredUserGroups());

		Assert.assertTrue(isPropagateMembership());
	}

	@Test
	public void testPropagateWhenAssigningUsersToRequiredUserGroup()
		throws Exception {

		long[] requiredUserGroupIds = addRequiredUserGroups();

		UserServiceUtil.addUserGroupUsers(requiredUserGroupIds[0], addUsers());

		Assert.assertTrue(isPropagateMembership());
	}

	@Test
	public void testPropagateWhenAssigningUserToRequiredUserGroups()
		throws Exception {

		long[] userIds = addUsers();

		MembershipPolicyTestUtil.updateUser(
			UserLocalServiceUtil.getUser(userIds[0]), null, null, null,
			addRequiredUserGroups(), Collections.<UserGroupRole>emptyList());

		Assert.assertTrue(isPropagateMembership());
	}

	@Test
	public void testUnassignUserFromRequiredUserGroups() throws Exception {
		long[] userIds = addUsers();
		long[] standardUserGroupIds = addStandardUserGroups();
		long[] requiredUserGroupIds = addRequiredUserGroups();

		User user = UserLocalServiceUtil.getUser(userIds[0]);

		List<UserGroup> userGroups = user.getUserGroups();

		Assert.assertEquals(userGroups.toString(), 0, userGroups.size());

		long[] userUserGroupIds = ArrayUtil.append(
			standardUserGroupIds, requiredUserGroupIds);

		user = MembershipPolicyTestUtil.updateUser(
			user, null, null, null, userUserGroupIds,
			Collections.<UserGroupRole>emptyList());

		userGroups = user.getUserGroups();

		Assert.assertEquals(
			userGroups.toString(), userUserGroupIds.length, userGroups.size());

		user = MembershipPolicyTestUtil.updateUser(
			user, null, null, null, requiredUserGroupIds,
			Collections.<UserGroupRole>emptyList());

		userGroups = user.getUserGroups();

		Assert.assertEquals(
			userGroups.toString(), requiredUserGroupIds.length,
			userGroups.size());
	}

	@Test
	public void testUnassignUserFromUserGroups() throws Exception {
		long[] userIds = addUsers();
		long[] standardUserGroupIds = addStandardUserGroups();
		long[] requiredUserGroupIds = addRequiredUserGroups();

		User user = UserLocalServiceUtil.getUser(userIds[0]);

		List<UserGroup> userGroups = user.getUserGroups();

		Assert.assertEquals(userGroups.toString(), 0, userGroups.size());

		long[] userUserGroupIds = ArrayUtil.append(
			standardUserGroupIds, requiredUserGroupIds);

		user = MembershipPolicyTestUtil.updateUser(
			user, null, null, null, userUserGroupIds,
			Collections.<UserGroupRole>emptyList());

		userGroups = user.getUserGroups();

		Assert.assertEquals(
			userGroups.toString(), userUserGroupIds.length, userGroups.size());

		user = MembershipPolicyTestUtil.updateUser(
			user, null, null, null, standardUserGroupIds,
			Collections.<UserGroupRole>emptyList());

		userGroups = user.getUserGroups();

		Assert.assertEquals(
			userGroups.toString(), userUserGroupIds.length, userGroups.size());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testUnassignUsersFromRequiredUserGroup() throws Exception {
		long[] requiredUserGroupIds = addRequiredUserGroups();

		User user = MembershipPolicyTestUtil.addUser(
			null, null, null, requiredUserGroupIds);

		UserServiceUtil.unsetUserGroupUsers(
			requiredUserGroupIds[0], new long[] {user.getUserId()});
	}

	@Test
	public void testUnassignUsersFromUserGroup() throws Exception {
		long[] standardUserGroupIds = addStandardUserGroups();

		User user = MembershipPolicyTestUtil.addUser(
			null, null, null, standardUserGroupIds);

		int initialUserUserGroupCount =
			UserLocalServiceUtil.getUserGroupUsersCount(
				standardUserGroupIds[0]);

		UserServiceUtil.unsetUserGroupUsers(
			standardUserGroupIds[0], new long[] {user.getUserId()});

		Assert.assertEquals(
			initialUserUserGroupCount - 1,
			UserLocalServiceUtil.getUserGroupUsersCount(
				standardUserGroupIds[0]));

		Assert.assertTrue(isPropagateMembership());
	}

	@Test
	public void testVerifyWhenAddingUserGroup() throws Exception {
		MembershipPolicyTestUtil.addUserGroup();

		Assert.assertTrue(isVerify());
	}

	@Test
	public void testVerifyWhenUpdatingUserGroup() throws Exception {
		UserGroup userGroup = MembershipPolicyTestUtil.addUserGroup();

		UserGroupServiceUtil.updateUserGroup(
			userGroup.getExternalReferenceCode(), userGroup.getUserGroupId(),
			userGroup.getName(), userGroup.getDescription(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(isVerify());
	}

}