/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.resource.v1_0.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.User;
import com.liferay.headless.commerce.admin.account.client.problem.Problem;
import com.liferay.headless.commerce.admin.account.client.resource.v1_0.UserResource;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class UserResourceTest extends BaseUserResourceTestCase {

	@Override
	@Test
	public void testPostAccountByExternalReferenceCodeAccountMemberCreateUser()
		throws Exception {

		super.testPostAccountByExternalReferenceCodeAccountMemberCreateUser();

		AccountEntry accountEntry1 = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());
		Role role1 = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(accountEntry1.getAccountEntryId()),
			role1.getRoleId(), new String[] {ActionKeys.VIEW});

		String password = RandomTestUtil.randomString();

		com.liferay.portal.kernel.model.User serviceBuilderUser =
			UserTestUtil.addUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				password, RandomTestUtil.randomString() + "@liferay.com",
				RandomTestUtil.randomString(), LocaleUtil.getDefault(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				new long[0], ServiceContextTestUtil.getServiceContext());

		_userLocalService.addRoleUsers(
			role1.getRoleId(), new long[] {serviceBuilderUser.getUserId()});

		UserResource userResource = UserResource.builder(
		).authentication(
			serviceBuilderUser.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		AssertUtils.assertFailure(
			Problem.ProblemException.class, "Forbidden",
			() ->
				userResource.
					postAccountByExternalReferenceCodeAccountMemberCreateUser(
						accountEntry1.getExternalReferenceCode(),
						new User() {
							{
								email = serviceBuilderUser.getEmailAddress();
								firstName = RandomTestUtil.randomString();
								id = serviceBuilderUser.getUserId();
								lastName = RandomTestUtil.randomString();
								roles = new String[] {
									AccountRoleConstants.
										REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR
								};
							}
						}));

		Assert.assertFalse(
			_userGroupRoleLocalService.hasUserGroupRole(
				serviceBuilderUser.getUserId(),
				accountEntry1.getAccountEntryGroupId(),
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR));

		AccountEntry accountEntry2 = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		AccountRole role2 = _accountRoleLocalService.addAccountRole(
			null, TestPropsValues.getUserId(),
			accountEntry2.getAccountEntryId(),
			StringUtil.toLowerCase(RandomTestUtil.randomString()), null, null);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(accountEntry1.getAccountEntryId()),
			role1.getRoleId(),
			new String[] {AccountActionKeys.ASSIGN_USERS, ActionKeys.VIEW});
		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), Role.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(role2.getRoleId()), role1.getRoleId(),
			new String[] {ActionKeys.VIEW});

		AssertUtils.assertFailure(
			Problem.ProblemException.class, "The account role is invalid.",
			() ->
				userResource.
					postAccountByExternalReferenceCodeAccountMemberCreateUser(
						accountEntry1.getExternalReferenceCode(),
						new User() {
							{
								email = serviceBuilderUser.getEmailAddress();
								firstName = RandomTestUtil.randomString();
								id = serviceBuilderUser.getUserId();
								lastName = RandomTestUtil.randomString();
								roles = new String[] {role2.getRoleName()};
							}
						}));

		Assert.assertFalse(
			_userGroupRoleLocalService.hasUserGroupRole(
				serviceBuilderUser.getUserId(),
				accountEntry1.getAccountEntryGroupId(), role2.getRoleId()));

		try {
			userResource.
				postAccountByExternalReferenceCodeAccountMemberCreateUser(
					accountEntry1.getExternalReferenceCode(),
					new User() {
						{
							email = serviceBuilderUser.getEmailAddress();
							firstName = RandomTestUtil.randomString();
							id = serviceBuilderUser.getUserId();
							lastName = RandomTestUtil.randomString();
							roles = new String[] {RoleConstants.ADMINISTRATOR};
						}
					});

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}

		Assert.assertFalse(
			_userGroupRoleLocalService.hasUserGroupRole(
				serviceBuilderUser.getUserId(),
				accountEntry1.getAccountEntryGroupId(),
				RoleConstants.ADMINISTRATOR));

		Role role3 = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), Role.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(role3.getRoleId()), role1.getRoleId(),
			new String[] {ActionKeys.VIEW});

		userResource.postAccountByExternalReferenceCodeAccountMemberCreateUser(
			accountEntry1.getExternalReferenceCode(),
			new User() {
				{
					email = serviceBuilderUser.getEmailAddress();
					firstName = RandomTestUtil.randomString();
					id = serviceBuilderUser.getUserId();
					lastName = RandomTestUtil.randomString();
					roles = new String[] {
						AccountRoleConstants.
							REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR
					};
				}
			});

		Assert.assertTrue(
			_userGroupRoleLocalService.hasUserGroupRole(
				serviceBuilderUser.getUserId(),
				accountEntry1.getAccountEntryGroupId(),
				AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR));
	}

	@Override
	protected User
			testPostAccountByExternalReferenceCodeAccountMemberCreateUser_addUser(
				User user)
		throws Exception {

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		return userResource.
			postAccountByExternalReferenceCodeAccountMemberCreateUser(
				accountEntry.getExternalReferenceCode(), user);
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}