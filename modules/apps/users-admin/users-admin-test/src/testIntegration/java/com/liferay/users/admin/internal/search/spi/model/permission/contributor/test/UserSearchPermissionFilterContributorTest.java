/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.search.spi.model.permission.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Drew Brokke
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class UserSearchPermissionFilterContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testWhenHasGroupManageTeamsPermissionSearch() throws Exception {
		Group group = GroupTestUtil.addGroup();

		User user = _addGroupUser(group);

		_addGroupUser(group);

		_addTeam(group);

		Assert.assertEquals(1, _performUserSearchCount(user));

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), Group.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			role.getRoleId(), ActionKeys.MANAGE_TEAMS);

		_userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), group.getGroupId(), role.getRoleId());

		Assert.assertEquals(3, _performUserSearchCount(user));
	}

	@Test
	public void testWhenHasOrganizationManageSuborganizationsUsersPermissionSearch()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization();

		User user1 = _addOrganizationUser(organization1);

		Assert.assertEquals(1, _performUserSearchCount(user1));

		Role organizationRole = RoleTestUtil.addRole(
			RoleConstants.TYPE_ORGANIZATION);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), Organization.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID),
			organizationRole.getRoleId(), ActionKeys.MANAGE_SUBORGANIZATIONS);

		_userGroupRoleLocalService.addUserGroupRole(
			user1.getUserId(), organization1.getGroupId(),
			organizationRole.getRoleId());

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			true);

		_addOrganizationUser(organization2);

		Assert.assertEquals(1, _performUserSearchCount(user1));

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), Organization.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID),
			organizationRole.getRoleId(),
			ActionKeys.MANAGE_SUBORGANIZATIONS_USERS);

		Assert.assertEquals(2, _performUserSearchCount(user1));

		Organization organization3 = OrganizationTestUtil.addOrganization(
			organization2.getOrganizationId(), RandomTestUtil.randomString(),
			true);

		_addOrganizationUser(organization3);

		Assert.assertEquals(3, _performUserSearchCount(user1));
	}

	@Test
	public void testWhenHasOrganizationManageUsersPermissionSearch()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		User userA = _addOrganizationUser(organization);

		_addOrganizationUser(organization);

		Assert.assertEquals(1, _performUserSearchCount(userA));

		Role organizationRole = RoleTestUtil.addRole(
			RoleConstants.TYPE_ORGANIZATION);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), Organization.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID),
			organizationRole.getRoleId(), ActionKeys.MANAGE_USERS);

		_userGroupRoleLocalService.addUserGroupRole(
			userA.getUserId(), organization.getGroupId(),
			organizationRole.getRoleId());

		Assert.assertEquals(2, _performUserSearchCount(userA));
	}

	@Test
	public void testWhenHasOwnerPermissionSearch() throws Exception {
		User user = UserTestUtil.addUser();

		Assert.assertEquals(1, _performUserSearchCount(user));

		UserTestUtil.addUser(
			user.getCompanyId(), user.getUserId(),
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[0], ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(2, _performUserSearchCount(user));
	}

	@Test
	public void testWhenHasOwnerPermissionSearchWithGuestUser()
		throws Exception {

		Company company = CompanyLocalServiceUtil.getCompanyById(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		Assert.assertEquals(0, _performUserSearchCount(guestUser));

		UserTestUtil.addUser(
			guestUser.getCompanyId(), guestUser.getUserId(),
			RandomTestUtil.randomString(), LocaleUtil.getDefault(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			new long[0], ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(0, _performUserSearchCount(guestUser));
	}

	@Test
	public void testWhenHasTeamAssignMembersPermissionSearch()
		throws Exception {

		Group group1 = GroupTestUtil.addGroup();

		User user = _addGroupUser(group1);

		_addGroupUser(group1);

		Group group2 = GroupTestUtil.addGroup();

		_userLocalService.addGroupUser(group2.getGroupId(), user);

		_addGroupUser(group2);

		_addTeam(group2);

		Assert.assertEquals(1, _performUserSearchCount(user));

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), Team.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			role.getRoleId(), ActionKeys.ASSIGN_MEMBERS);

		_userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), group1.getGroupId(), role.getRoleId());

		Assert.assertEquals(1, _performUserSearchCount(user));

		_addTeam(group1);

		Assert.assertEquals(3, _performUserSearchCount(user));
	}

	private User _addGroupUser(Group group) throws Exception {
		User user = UserTestUtil.addUser();

		_userLocalService.addGroupUser(group.getGroupId(), user);

		return user;
	}

	private User _addOrganizationUser(Organization organization)
		throws Exception {

		User user = UserTestUtil.addUser();

		_userLocalService.addOrganizationUser(
			organization.getOrganizationId(), user);

		return user;
	}

	private Team _addTeam(Group group) throws Exception {
		return _teamLocalService.addTeam(
			TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private int _performUserSearchCount(User user) throws Exception {
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			BaseModelSearchResult<User> userBaseModelSearchResult =
				_userLocalService.searchUsers(
					TestPropsValues.getCompanyId(), null,
					WorkflowConstants.STATUS_APPROVED, null, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, (Sort)null);

			return userBaseModelSearchResult.getLength();
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private TeamLocalService _teamLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}