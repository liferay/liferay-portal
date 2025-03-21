/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.membership.policy.organization.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.security.membership.policy.organization.BaseOrganizationMembershipPolicyTestCase;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.RenderResponseFactory;
import com.liferay.site.search.OrganizationRoleUserChecker;
import com.liferay.users.admin.search.UserOrganizationChecker;

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
public class OrganizationMembershipPolicyRowCheckerTest
	extends BaseOrganizationMembershipPolicyTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testIsCheckerDisabledWhenSettingForbiddenOrganizationToUser()
		throws Exception {

		long forbiddenOrganizationId = addForbiddenOrganizations()[0];

		Organization forbiddenOrganization =
			OrganizationLocalServiceUtil.getOrganization(
				forbiddenOrganizationId);

		UserOrganizationChecker userOrganizationChecker =
			new UserOrganizationChecker(_renderResponse, forbiddenOrganization);

		User user = UserTestUtil.addUser();

		Assert.assertTrue(userOrganizationChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenSettingForbiddenRoleToUser()
		throws Exception {

		long forbiddenRoleId = addForbiddenRoles()[0];

		OrganizationRoleUserChecker organizationRoleUserChecker =
			new OrganizationRoleUserChecker(
				_renderResponse, organization,
				RoleLocalServiceUtil.getRole(forbiddenRoleId));

		User user = UserTestUtil.addUser();

		Assert.assertTrue(organizationRoleUserChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenSettingRequiredOrganizationToUser()
		throws Exception {

		long requiredOrganizationId = addRequiredOrganizations()[0];

		Organization requiredOrganization =
			OrganizationLocalServiceUtil.getOrganization(
				requiredOrganizationId);

		UserOrganizationChecker userOrganizationChecker =
			new UserOrganizationChecker(_renderResponse, requiredOrganization);

		User user = UserTestUtil.addUser();

		Assert.assertFalse(userOrganizationChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenSettingRequiredRoleToUser()
		throws Exception {

		long requiredRoleId = addRequiredRoles()[0];

		OrganizationRoleUserChecker organizationRoleUserChecker =
			new OrganizationRoleUserChecker(
				_renderResponse, organization,
				RoleLocalServiceUtil.getRole(requiredRoleId));

		User user = UserTestUtil.addUser();

		Assert.assertFalse(organizationRoleUserChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenUnsettingForbiddenOrganizationToUser()
		throws Exception {

		long forbiddenOrganizationId = addForbiddenOrganizations()[0];

		Organization forbiddenOrganization =
			OrganizationLocalServiceUtil.getOrganization(
				forbiddenOrganizationId);

		UserOrganizationChecker userOrganizationChecker =
			new UserOrganizationChecker(_renderResponse, forbiddenOrganization);

		User user = UserTestUtil.addUser();

		OrganizationLocalServiceUtil.addUserOrganization(
			user.getUserId(), forbiddenOrganizationId);

		Assert.assertFalse(userOrganizationChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenUnsettingForbiddenRoleToUser()
		throws Exception {

		long forbiddenRoleId = addForbiddenRoles()[0];

		OrganizationRoleUserChecker organizationRoleUserChecker =
			new OrganizationRoleUserChecker(
				_renderResponse, organization,
				RoleLocalServiceUtil.getRole(forbiddenRoleId));

		User user = UserTestUtil.addUser();

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			user.getUserId(), organization.getGroupId(),
			new long[] {forbiddenRoleId});

		Assert.assertFalse(organizationRoleUserChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenUnsettingRequiredOrganizationToUser()
		throws Exception {

		long requiredOrganizationId = addRequiredOrganizations()[0];

		Organization requiredOrganization =
			OrganizationLocalServiceUtil.getOrganization(
				requiredOrganizationId);

		UserOrganizationChecker userOrganizationChecker =
			new UserOrganizationChecker(_renderResponse, requiredOrganization);

		User user = UserTestUtil.addUser();

		OrganizationLocalServiceUtil.addUserOrganization(
			user.getUserId(), requiredOrganizationId);

		Assert.assertTrue(userOrganizationChecker.isDisabled(user));
	}

	@Test
	public void testIsCheckerDisabledWhenUnsettingRequiredRoleToUser()
		throws Exception {

		long requiredRoleId = addRequiredRoles()[0];

		OrganizationRoleUserChecker organizationRoleUserChecker =
			new OrganizationRoleUserChecker(
				_renderResponse, organization,
				RoleLocalServiceUtil.getRole(requiredRoleId));

		User user = UserTestUtil.addUser();

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			user.getUserId(), organization.getGroupId(),
			new long[] {requiredRoleId});

		Assert.assertTrue(organizationRoleUserChecker.isDisabled(user));
	}

	private static final RenderResponse _renderResponse =
		new RenderResponseWrapper(RenderResponseFactory.create()) {

			@Override
			public String getNamespace() {
				return RandomTestUtil.randomString();
			}

		};

}