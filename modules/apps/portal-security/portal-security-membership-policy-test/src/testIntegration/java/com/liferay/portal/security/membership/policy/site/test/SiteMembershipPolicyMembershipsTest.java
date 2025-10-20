/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membership.policy.site.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.security.membershippolicy.MembershipPolicyException;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.security.membership.policy.site.BaseSiteMembershipPolicyTestCase;
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
public class SiteMembershipPolicyMembershipsTest
	extends BaseSiteMembershipPolicyTestCase {

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
			TestPropsValues.getCompanyId(), Group.class.getName());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testAddUserToForbiddenGroups() throws Exception {
		MembershipPolicyTestUtil.addUser(
			null, null, addForbiddenGroups(), null);
	}

	@Test
	public void testAddUserToRequiredGroups() throws Exception {
		long[] requiredGroupIds = addRequiredGroups();

		int initialGroupUsersCount = UserLocalServiceUtil.getGroupUsersCount(
			requiredGroupIds[0]);

		MembershipPolicyTestUtil.addUser(
			null, null, new long[] {requiredGroupIds[0]}, null);

		Assert.assertEquals(
			initialGroupUsersCount + 1,
			UserLocalServiceUtil.getGroupUsersCount(requiredGroupIds[0]));
	}

	@Test(expected = MembershipPolicyException.class)
	public void testAssignUsersToForbiddenGroup() throws Exception {
		long[] forbiddenGroupIds = addForbiddenGroups();

		UserServiceUtil.addGroupUsers(
			forbiddenGroupIds[0], addUsers(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testAssignUsersToRequiredGroup() throws Exception {
		long[] requiredGroupIds = addRequiredGroups();

		int initialGroupUsersCount = UserLocalServiceUtil.getGroupUsersCount(
			requiredGroupIds[0]);

		UserServiceUtil.addGroupUsers(
			requiredGroupIds[0], addUsers(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			initialGroupUsersCount + 2,
			UserLocalServiceUtil.getGroupUsersCount(requiredGroupIds[0]));

		Assert.assertTrue(isPropagateMembership());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testAssignUserToForbiddenGroups() throws Exception {
		long[] userIds = addUsers();

		MembershipPolicyTestUtil.updateUser(
			UserLocalServiceUtil.getUser(userIds[0]), null, null,
			addForbiddenGroups(), null, Collections.<UserGroupRole>emptyList());
	}

	@Test
	public void testAssignUserToRequiredGroups() throws Exception {
		long[] userIds = addUsers();

		long[] requiredGroupIds = addRequiredGroups();

		int initialGroupUsersCount = UserLocalServiceUtil.getGroupUsersCount(
			requiredGroupIds[0]);

		MembershipPolicyTestUtil.updateUser(
			UserLocalServiceUtil.getUser(userIds[0]), null, null,
			new long[] {requiredGroupIds[0]}, null,
			Collections.<UserGroupRole>emptyList());

		Assert.assertEquals(
			initialGroupUsersCount + 1,
			UserLocalServiceUtil.getGroupUsersCount(requiredGroupIds[0]));

		Assert.assertTrue(isPropagateMembership());
	}

	@Test
	public void testPropagateWhenAddingUserToRequiredGroups() throws Exception {
		MembershipPolicyTestUtil.addUser(null, null, addRequiredGroups(), null);

		Assert.assertTrue(isPropagateMembership());
	}

	@Test
	public void testPropagateWhenAssigningUsersToRequiredGroup()
		throws Exception {

		long[] requiredGroupIds = addRequiredGroups();

		UserServiceUtil.addGroupUsers(
			requiredGroupIds[0], addUsers(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(isPropagateMembership());
	}

	@Test
	public void testPropagateWhenAssigningUserToRequiredGroups()
		throws Exception {

		long[] userIds = addUsers();

		MembershipPolicyTestUtil.updateUser(
			UserLocalServiceUtil.getUser(userIds[0]), null, null,
			addRequiredGroups(), null, Collections.<UserGroupRole>emptyList());

		Assert.assertTrue(isPropagateMembership());
	}

	@Test
	public void testUnassignUserFromGroups() throws Exception {
		long[] userIds = addUsers();
		long[] standardGroupIds = addStandardGroups();
		long[] requiredGroupIds = addRequiredGroups();

		User user = UserLocalServiceUtil.getUser(userIds[0]);

		List<Group> groups = user.getGroups();

		Assert.assertEquals(groups.toString(), 1, groups.size());

		long[] userGroupIds = ArrayUtil.append(
			standardGroupIds, requiredGroupIds, new long[] {user.getGroupId()});

		MembershipPolicyTestUtil.updateUser(
			user, null, null, userGroupIds, null,
			Collections.<UserGroupRole>emptyList());

		groups = user.getGroups();

		Assert.assertEquals(
			groups.toString(), userGroupIds.length, groups.size());

		MembershipPolicyTestUtil.updateUser(
			user, null, null, standardGroupIds, null,
			Collections.<UserGroupRole>emptyList());

		groups = user.getGroups();

		Assert.assertEquals(
			groups.toString(), userGroupIds.length - 1, groups.size());
	}

	@Test
	public void testUnassignUserFromRequiredGroups() throws Exception {
		long[] userIds = addUsers();
		long[] standardGroupIds = addStandardGroups();
		long[] requiredGroupIds = addRequiredGroups();

		User user = UserLocalServiceUtil.getUser(userIds[0]);

		List<Group> groups = user.getGroups();

		Assert.assertEquals(groups.toString(), 1, groups.size());

		long[] userGroupIds = ArrayUtil.append(
			standardGroupIds, requiredGroupIds, new long[] {user.getGroupId()});

		MembershipPolicyTestUtil.updateUser(
			user, null, null, userGroupIds, null,
			Collections.<UserGroupRole>emptyList());

		groups = user.getGroups();

		Assert.assertEquals(
			groups.toString(), userGroupIds.length, groups.size());

		MembershipPolicyTestUtil.updateUser(
			user, null, null, requiredGroupIds, null,
			Collections.<UserGroupRole>emptyList());

		groups = user.getGroups();

		Assert.assertEquals(
			groups.toString(), requiredGroupIds.length, groups.size());
	}

	@Test
	public void testUnassignUsersFromGroup() throws Exception {
		long[] standardGroupIds = addStandardGroups();

		User user = MembershipPolicyTestUtil.addUser(
			null, null, standardGroupIds, null);

		int initialUserGroupCount = UserLocalServiceUtil.getGroupUsersCount(
			standardGroupIds[0]);

		UserServiceUtil.unsetGroupUsers(
			standardGroupIds[0], new long[] {user.getUserId()},
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			initialUserGroupCount - 1,
			UserLocalServiceUtil.getGroupUsersCount(standardGroupIds[0]));

		Assert.assertTrue(isPropagateMembership());
	}

	@Test(expected = MembershipPolicyException.class)
	public void testUnassignUsersFromRequiredGroup() throws Exception {
		long[] requiredGroupIds = addRequiredGroups();

		User user = MembershipPolicyTestUtil.addUser(
			null, null, requiredGroupIds, null);

		UserServiceUtil.unsetGroupUsers(
			requiredGroupIds[0], new long[] {user.getUserId()},
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testVerifyWhenAddingGroup() throws Exception {
		MembershipPolicyTestUtil.addGroup();

		Assert.assertTrue(isVerify());
	}

	@Test
	public void testVerifyWhenUpdatingGroup() throws Exception {
		Group group = MembershipPolicyTestUtil.addGroup();

		GroupServiceUtil.updateGroup(
			group.getGroupId(), group.getParentGroupId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			group.getDescriptionMap(), group.getType(), null,
			group.isManualMembership(), group.getMembershipRestriction(),
			group.getFriendlyURL(), group.isInheritContent(), group.isActive(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(isVerify());
	}

	@Test
	public void testVerifyWhenUpdatingGroupTypeSettings() throws Exception {
		Group group = MembershipPolicyTestUtil.addGroup();

		UnicodeProperties unicodeProperties =
			RandomTestUtil.randomUnicodeProperties(10, 2, 2);

		GroupServiceUtil.updateGroup(
			group.getGroupId(), unicodeProperties.toString());

		Assert.assertTrue(isVerify());
	}

}