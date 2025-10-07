/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.test.util.AccountEntryArgs;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class AccountEntryServiceWhenSearchingAccountEntriesTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		for (AccountEntry accountEntry : _getAllAccountEntries()) {
			_accountEntryLocalService.deleteAccountEntry(accountEntry);
		}
	}

	@Before
	public void setUp() throws Exception {
		_rootOrganization = _organizationLocalService.addOrganization(
			TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		_organizationAccountEntries.put(
			_rootOrganization,
			AccountEntryTestUtil.addAccountEntry(
				AccountEntryArgs.withOrganizations(_rootOrganization)));

		_organization = _organizationLocalService.addOrganization(
			TestPropsValues.getUserId(), _rootOrganization.getOrganizationId(),
			RandomTestUtil.randomString(), false);

		_organizationAccountEntries.put(
			_organization,
			AccountEntryTestUtil.addAccountEntry(
				AccountEntryArgs.withOrganizations(_organization)));

		_suborganization = _organizationLocalService.addOrganization(
			TestPropsValues.getUserId(), _organization.getOrganizationId(),
			RandomTestUtil.randomString(), false);

		_organizationAccountEntries.put(
			_suborganization,
			AccountEntryTestUtil.addAccountEntry(
				AccountEntryArgs.withOrganizations(_suborganization)));

		_user = UserTestUtil.addUser();

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));
	}

	@After
	public void tearDown() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testShouldReturnAllAccountEntriesAsAdminUser()
		throws Exception {

		Role role = _roleLocalService.getRole(
			_user.getCompanyId(), RoleConstants.ADMINISTRATOR);

		_userLocalService.addRoleUser(role.getRoleId(), _user);

		_assertSearch(_getAllAccountEntries());
	}

	@Test
	public void testShouldReturnAllAccountEntriesWithCompanyViewPermission()
		throws Exception {

		_assertSearch();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, AccountEntry.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), ActionKeys.VIEW);

		_userLocalService.addRoleUser(role.getRoleId(), _user.getUserId());

		_assertSearch(_getAllAccountEntries());
	}

	@Test
	public void testShouldReturnDirectMembershipAccountEntries()
		throws Exception {

		_assertSearch();

		AccountEntry accountEntry = _organizationAccountEntries.get(
			_rootOrganization);

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), _user.getUserId());

		_assertSearch(accountEntry);
	}

	@Test
	public void testShouldReturnManagedAccountEntriesWithManageAvailableAccountsPermission()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, Organization.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			AccountActionKeys.MANAGE_AVAILABLE_ACCOUNTS);

		_userLocalService.addRoleUser(role.getRoleId(), _user);

		_userLocalService.addOrganizationUser(
			_organization.getOrganizationId(), _user);

		_assertSearch(
			_organizationAccountEntries.get(_organization),
			_organizationAccountEntries.get(_suborganization));
	}

	@Test
	public void testShouldReturnNoAccountEntriesWithoutManageAccountsPermission()
		throws Exception {

		for (Organization organization : _organizationAccountEntries.keySet()) {
			_userLocalService.addOrganizationUser(
				organization.getOrganizationId(), _user);
		}

		_assertSearch();
	}

	@Test
	public void testShouldReturnOrganizationAccountEntriesWithManageAccountsPermission()
		throws Exception {

		_userLocalService.addOrganizationUser(
			_rootOrganization.getOrganizationId(), _user);

		Role role = _addOrganizationRole();

		RoleTestUtil.addResourcePermission(
			role, Organization.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			AccountActionKeys.MANAGE_ACCOUNTS);

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), _rootOrganization.getGroupId(),
			role.getRoleId());

		_assertSearch(_organizationAccountEntries.get(_rootOrganization));
	}

	@Test
	public void testShouldReturnSuborganizationAccountEntriesWithManageSuborganizationAccountsPermission()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_ORGANIZATION);

		RoleTestUtil.addResourcePermission(
			role, Organization.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			AccountActionKeys.MANAGE_SUBORGANIZATIONS_ACCOUNTS);

		_userLocalService.addOrganizationUser(
			_rootOrganization.getOrganizationId(), _user);

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), _rootOrganization.getGroupId(),
			role.getRoleId());

		_assertSearch(
			_organizationAccountEntries.get(_organization),
			_organizationAccountEntries.get(_suborganization));
	}

	@Test
	public void testShouldReturnSuborganizationsAccountEntries()
		throws Exception {

		_userLocalService.addOrganizationUser(
			_organization.getOrganizationId(), _user);

		Role role = _addOrganizationRole();

		RoleTestUtil.addResourcePermission(
			role, Organization.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			AccountActionKeys.MANAGE_ACCOUNTS);

		_userGroupRoleLocalService.addUserGroupRole(
			_user.getUserId(), _organization.getGroupId(), role.getRoleId());

		AccountEntry accountEntry = _organizationAccountEntries.get(
			_organization);

		_assertSearch(accountEntry);

		RoleTestUtil.addResourcePermission(
			role, Organization.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			AccountActionKeys.MANAGE_SUBORGANIZATIONS_ACCOUNTS);

		AccountEntry suborgAccountEntry = _organizationAccountEntries.get(
			_suborganization);

		_assertSearch(accountEntry, suborgAccountEntry);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_user);

		Assert.assertFalse(
			_hasPermission(permissionChecker, accountEntry, ActionKeys.UPDATE));
		Assert.assertFalse(
			_hasPermission(
				permissionChecker, suborgAccountEntry, ActionKeys.UPDATE));

		RoleTestUtil.addResourcePermission(
			role, AccountEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0", ActionKeys.UPDATE);

		Assert.assertTrue(
			_hasPermission(permissionChecker, accountEntry, ActionKeys.UPDATE));
		Assert.assertTrue(
			_hasPermission(
				permissionChecker, suborgAccountEntry, ActionKeys.UPDATE));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private static List<AccountEntry> _getAllAccountEntries() throws Exception {
		return _accountEntryLocalService.getAccountEntries(
			TestPropsValues.getCompanyId(), WorkflowConstants.STATUS_APPROVED,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private Role _addOrganizationRole() throws Exception {
		return _roleLocalService.addRole(
			RandomTestUtil.randomString(), _user.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null,
			RoleConstants.TYPE_ORGANIZATION, null,
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertSearch(AccountEntry... expectedAccountEntries)
		throws Exception {

		_assertSearch(Arrays.asList(expectedAccountEntries));
	}

	private void _assertSearch(List<AccountEntry> expectedAccountEntries)
		throws Exception {

		BaseModelSearchResult<AccountEntry> baseModelSearchResult =
			_accountEntryService.searchAccountEntries(
				null, null, 0, 10, "name", false);

		Assert.assertEquals(
			expectedAccountEntries.size(), baseModelSearchResult.getLength());
		Assert.assertEquals(
			ListUtil.sort(
				expectedAccountEntries,
				Comparator.comparing(
					AccountEntry::getName, String::compareToIgnoreCase)),
			baseModelSearchResult.getBaseModels());
	}

	private boolean _hasPermission(
			PermissionChecker permissionChecker, AccountEntry accountEntry,
			String actionId)
		throws Exception {

		for (Organization organization : _user.getOrganizations(true)) {
			if (permissionChecker.hasPermission(
					organization.getGroupId(), AccountEntry.class.getName(),
					accountEntry.getAccountEntryId(), actionId)) {

				return true;
			}
		}

		return false;
	}

	@Inject
	private static AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private AccountEntryService _accountEntryService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	private Organization _organization;
	private final Map<Organization, AccountEntry> _organizationAccountEntries =
		new LinkedHashMap<>();

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private PermissionChecker _originalPermissionChecker;

	@Inject
	private RoleLocalService _roleLocalService;

	private Organization _rootOrganization;
	private Organization _suborganization;
	private User _user;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}