/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.service.test;

import com.liferay.announcements.kernel.model.AnnouncementsDelivery;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManagerUtil;
import com.liferay.portal.kernel.workflow.search.WorkflowModelSearchResult;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class UserServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddInvalidOrganizationUsers() throws Exception {
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		User user1 = UserTestUtil.addUser();

		Company company = CompanyTestUtil.addCompany();

		User user2 = UserTestUtil.addCompanyAdminUser(company);

		Organization organization = _organizationLocalService.addOrganization(
			user2.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user2));

			_userService.addOrganizationUsers(
				organization.getOrganizationId(),
				new long[] {user1.getUserId()});

			Assert.fail();
		}
		catch (Exception exception) {
			String message = exception.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + user2.getUserId() +
						" must have VIEW permission"));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testAddUserWithInvalidName() {
		int firstNameMaxLength = ModelHintsUtil.getMaxLength(
			User.class.getName(), "firstName");

		try {
			UserTestUtil.addUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				RandomTestUtil.randomString(
					NumericStringRandomizerBumper.INSTANCE,
					UniqueStringRandomizerBumper.INSTANCE),
				LocaleUtil.getDefault(), RandomTestUtil.randomString(76),
				RandomTestUtil.randomString(),
				new long[] {
					ServiceContextTestUtil.getServiceContext(
					).getScopeGroupId()
				},
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (Exception exception) {
			String message = exception.getMessage();

			Assert.assertTrue(
				message.contains(
					StringBundler.concat(
						"Contact first name must have fewer than ",
						firstNameMaxLength, " characters")));
		}
	}

	@Test
	public void testAddUserWithInvalidScreenName() {
		int screenNameMaxLength = ModelHintsUtil.getMaxLength(
			User.class.getName(), "screenName");

		String screenName = RandomTestUtil.randomString(
			screenNameMaxLength + 1, NumericStringRandomizerBumper.INSTANCE,
			UniqueStringRandomizerBumper.INSTANCE);

		try {
			UserTestUtil.addUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				screenName, LocaleUtil.getDefault(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				new long[] {
					ServiceContextTestUtil.getServiceContext(
					).getScopeGroupId()
				},
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (Exception exception) {
			String message = exception.getMessage();

			Assert.assertTrue(
				message.contains(
					StringBundler.concat(
						"Screen name ", StringUtil.toLowerCase(screenName),
						" must have fewer than ", screenNameMaxLength,
						" characters")));
		}
	}

	@Test
	public void testAddUserWithWorkflow() throws Exception {
		String originalName = PrincipalThreadLocal.getName();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PrincipalThreadLocal.setName(TestPropsValues.getUserId());

			PermissionChecker adminPermissionChecker =
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser());

			PermissionThreadLocal.setPermissionChecker(adminPermissionChecker);

			_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
				null, TestPropsValues.getUserId(),
				TestPropsValues.getCompanyId(),
				WorkflowConstants.DEFAULT_GROUP_ID, User.class.getName(), 0, 0,
				"Single Approver", 1);

			User user = _userService.addUserWithWorkflow(
				TestPropsValues.getCompanyId(), true, StringPool.BLANK,
				StringPool.BLANK, true, StringPool.BLANK,
				"UserServiceTest." + RandomTestUtil.nextLong() + "@liferay.com",
				LocaleUtil.getDefault(), "UserServiceTest", StringPool.BLANK,
				"UserServiceTest", 0, 0, true, Calendar.JANUARY, 1, 1970,
				StringPool.BLANK, null, null, null, null,
				new ArrayList<Address>(), new ArrayList<EmailAddress>(),
				new ArrayList<Phone>(), new ArrayList<Website>(),
				new ArrayList<AnnouncementsDelivery>(), false,
				ServiceContextTestUtil.getServiceContext());

			WorkflowModelSearchResult<WorkflowTask> workflowModelSearchResult =
				WorkflowTaskManagerUtil.searchWorkflowTasks(
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					StringPool.BLANK, new String[0], null, null, null, null,
					null, null, null, true, true, null, null, false, 0, 20,
					null);

			Assert.assertEquals(1, workflowModelSearchResult.getLength());

			List<WorkflowTask> workflowTasks =
				workflowModelSearchResult.getWorkflowModels();

			WorkflowTask workflowTask = workflowTasks.get(0);

			Map<String, Serializable> optionalAttributes =
				workflowTask.getOptionalAttributes();

			Assert.assertEquals(
				optionalAttributes.get("entryClassPK"),
				String.valueOf(user.getUserId()));
		}
		finally {
			PrincipalThreadLocal.setName(originalName);

			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testGetRoleUserIds() throws Exception {
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

			User user = UserTestUtil.addUser();

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

			try {
				_userService.getRoleUserIds(role.getRoleId());

				Assert.fail();
			}
			catch (Exception exception) {
				String message = exception.getMessage();

				Assert.assertTrue(
					message.contains(
						"User " + user.getUserId() +
							" must have ASSIGN_MEMBERS permission"));
			}

			RoleTestUtil.addResourcePermission(
				role, Role.class.getName(), ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				ActionKeys.ASSIGN_MEMBERS);

			long[] roleUserIds = _userService.getRoleUserIds(role.getRoleId());

			Assert.assertEquals(
				Arrays.toString(roleUserIds), 1, roleUserIds.length);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	@Test
	public void testUpdateOrganizationsWithoutPermission() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, Organization.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			ActionKeys.ASSIGN_MEMBERS);
		RoleTestUtil.addResourcePermission(
			role, User.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), ActionKeys.UPDATE);

		User user1 = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user1.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user1)) {

			Organization organization = OrganizationTestUtil.addOrganization();
			User user2 = UserTestUtil.addUser();

			_userService.updateOrganizations(
				user2.getUserId(),
				new long[] {organization.getOrganizationId()},
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (Exception exception) {
			String message = exception.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + user1.getUserId() +
						" must have MANAGE_USERS permission"));
		}
	}

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private UserService _userService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}