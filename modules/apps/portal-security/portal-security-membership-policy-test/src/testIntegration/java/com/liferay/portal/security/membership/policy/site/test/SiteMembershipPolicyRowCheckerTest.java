/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membership.policy.site.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.security.membership.policy.site.BaseSiteMembershipPolicyTestCase;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.RenderResponseFactory;
import com.liferay.site.search.UserGroupRoleRoleChecker;
import com.liferay.site.search.UserGroupRoleUserChecker;

import jakarta.portlet.RenderResponse;
import jakarta.portlet.filter.RenderResponseWrapper;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class SiteMembershipPolicyRowCheckerTest
	extends BaseSiteMembershipPolicyTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testIsCheckerDisabledWhenSettingForbiddenRoleToUser()
		throws Exception {

		long forbiddenRoleId = addForbiddenRoles()[0];

		UserGroupRoleUserChecker userGroupRoleUserChecker =
			new UserGroupRoleUserChecker(
				_renderResponse, group,
				RoleLocalServiceUtil.getRole(forbiddenRoleId));

		User user = UserTestUtil.addUser();

		Assert.assertTrue(userGroupRoleUserChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenSettingRequiredRoleToUser()
		throws Exception {

		long requiredRoleId = addRequiredRoles()[0];

		UserGroupRoleUserChecker userGroupRoleUserChecker =
			new UserGroupRoleUserChecker(
				_renderResponse, group,
				RoleLocalServiceUtil.getRole(requiredRoleId));

		User user = UserTestUtil.addUser();

		Assert.assertFalse(userGroupRoleUserChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenSettingUserToForbiddenRole()
		throws Exception {

		User user = UserTestUtil.addUser();

		UserGroupRoleRoleChecker userGroupRoleRoleChecker =
			new UserGroupRoleRoleChecker(_renderResponse, user, group);

		Assert.assertTrue(
			userGroupRoleRoleChecker.isDisabled(
				RoleLocalServiceUtil.getRole(addForbiddenRoles()[0])));
	}

	@Test
	public void testIsCheckerDisabledWhenSettingUserToRequiredRole()
		throws Exception {

		User user = UserTestUtil.addUser();

		UserGroupRoleRoleChecker userGroupRoleRoleChecker =
			new UserGroupRoleRoleChecker(_renderResponse, user, group);

		Assert.assertFalse(
			userGroupRoleRoleChecker.isDisabled(
				RoleLocalServiceUtil.getRole(addRequiredRoles()[0])));
	}

	@Test
	public void testIsCheckerDisabledWhenUnsettingForbiddenRoleFromUser()
		throws Exception {

		long forbiddenRoleId = addForbiddenRoles()[0];

		UserGroupRoleUserChecker userGroupRoleUserChecker =
			new UserGroupRoleUserChecker(
				_renderResponse, group,
				RoleLocalServiceUtil.getRole(forbiddenRoleId));

		User user = UserTestUtil.addUser();

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			user.getUserId(), group.getGroupId(), new long[] {forbiddenRoleId});

		Assert.assertFalse(userGroupRoleUserChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenUnsettingRequiredRoleFromUser()
		throws Exception {

		long requiredRoleId = addRequiredRoles()[0];

		UserGroupRoleUserChecker userGroupRoleUserChecker =
			new UserGroupRoleUserChecker(
				_renderResponse, group,
				RoleLocalServiceUtil.getRole(requiredRoleId));

		User user = UserTestUtil.addUser();

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			user.getUserId(), group.getGroupId(), new long[] {requiredRoleId});

		Assert.assertTrue(userGroupRoleUserChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenUnsettingUserFromForbiddenRole()
		throws Exception {

		User user = UserTestUtil.addUser();

		UserGroupRoleRoleChecker userGroupRoleRoleChecker =
			new UserGroupRoleRoleChecker(_renderResponse, user, group);

		long forbiddenRoleId = addForbiddenRoles()[0];

		Role role = RoleLocalServiceUtil.getRole(forbiddenRoleId);

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			user.getUserId(), group.getGroupId(), new long[] {forbiddenRoleId});

		Assert.assertFalse(userGroupRoleRoleChecker.isDisabled(role));
	}

	@Test
	public void testIsCheckerDisabledWhenUnsettingUserFromRequiredRole()
		throws Exception {

		User user = UserTestUtil.addUser();

		UserGroupRoleRoleChecker userGroupRoleRoleChecker =
			new UserGroupRoleRoleChecker(_renderResponse, user, group);

		long requiredRoleId = addRequiredRoles()[0];

		Role role = RoleLocalServiceUtil.getRole(requiredRoleId);

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			user.getUserId(), group.getGroupId(), new long[] {requiredRoleId});

		Assert.assertTrue(userGroupRoleRoleChecker.isDisabled(role));
	}

	private static final RenderResponse _renderResponse =
		new RenderResponseWrapper(RenderResponseFactory.create()) {

			@Override
			public String getNamespace() {
				return RandomTestUtil.randomString();
			}

		};

}