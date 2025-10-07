/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.exception.NoSuchRoleException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.model.AccountRole;
import com.liferay.account.model.AccountRoleTable;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.account.service.AccountRoleLocalServiceUtil;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.comparator.RoleNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class AccountRoleLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@Before
	public void setUp() throws Exception {
		_accountEntry1 = AccountEntryTestUtil.addAccountEntry();
		_accountEntry2 = AccountEntryTestUtil.addAccountEntry();
		_accountEntry3 = AccountEntryTestUtil.addAccountEntry();
	}

	@Test
	public void testAddAccountRole() throws Exception {
		List<AccountRole> accountRoles =
			_accountRoleLocalService.getAccountRolesByAccountEntryIds(
				new long[] {_accountEntry1.getAccountEntryId()});

		Assert.assertEquals(accountRoles.toString(), 0, accountRoles.size());

		String name = RandomTestUtil.randomString(50);

		AccountRole accountRole = _addAccountRole(
			_accountEntry1.getAccountEntryId(), name);

		Assert.assertEquals(
			1,
			_accountRoleLocalService.dslQueryCount(
				DSLQueryFactoryUtil.countDistinct(
					AccountRoleTable.INSTANCE.accountRoleId
				).from(
					AccountRoleTable.INSTANCE
				).where(
					AccountRoleTable.INSTANCE.roleId.eq(accountRole.getRoleId())
				)));

		accountRoles =
			_accountRoleLocalService.getAccountRolesByAccountEntryIds(
				new long[] {_accountEntry1.getAccountEntryId()});

		Assert.assertEquals(accountRoles.toString(), 1, accountRoles.size());

		accountRole = accountRoles.get(0);

		Assert.assertEquals(name, accountRole.getRoleName());

		Role role = accountRole.getRole();

		Assert.assertEquals(AccountRole.class.getName(), role.getClassName());
		Assert.assertEquals(accountRole.getAccountRoleId(), role.getClassPK());
	}

	@Test
	public void testAssociateUser() throws Exception {
		AccountRole accountRole = _addAccountRole(
			_accountEntry1.getAccountEntryId(), RandomTestUtil.randomString());

		User user = UserTestUtil.addUser();

		_users.add(user);

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelLocalService.addAccountEntryUserRel(
				_accountEntry1.getAccountEntryId(), user.getUserId());

		_accountEntryUserRels.add(accountEntryUserRel);

		Assert.assertFalse(
			_hasRoleId(
				_accountEntry1.getAccountEntryId(), accountRole.getRoleId(),
				user.getUserId()));

		_accountRoleLocalService.associateUser(
			_accountEntry1.getAccountEntryId(), accountRole.getAccountRoleId(),
			user.getUserId());

		Assert.assertTrue(
			_hasRoleId(
				_accountEntry1.getAccountEntryId(), accountRole.getRoleId(),
				user.getUserId()));

		_accountRoleLocalService.unassociateUser(
			_accountEntry1.getAccountEntryId(), accountRole.getAccountRoleId(),
			user.getUserId());

		Assert.assertFalse(
			_hasRoleId(
				_accountEntry1.getAccountEntryId(), accountRole.getRoleId(),
				user.getUserId()));

		// Permissions

		AccountRole defaultAccountRole = _addAccountRole(
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString());

		long[] roleIds = _getRoleIds(user);

		Assert.assertFalse(
			ArrayUtil.contains(roleIds, accountRole.getRoleId()));
		Assert.assertFalse(
			ArrayUtil.contains(roleIds, defaultAccountRole.getRoleId()));

		_accountRoleLocalService.associateUser(
			_accountEntry1.getAccountEntryId(), accountRole.getAccountRoleId(),
			user.getUserId());
		_accountRoleLocalService.associateUser(
			_accountEntry1.getAccountEntryId(),
			defaultAccountRole.getAccountRoleId(), user.getUserId());

		roleIds = _getRoleIds(user);

		Assert.assertTrue(ArrayUtil.contains(roleIds, accountRole.getRoleId()));
		Assert.assertTrue(
			ArrayUtil.contains(roleIds, defaultAccountRole.getRoleId()));

		_assertHasPermission(user, ActionKeys.DELETE, false);
		_assertHasPermission(user, ActionKeys.MANAGE_USERS, false);
		_assertHasPermission(user, ActionKeys.UPDATE, false);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			accountRole.getRoleId(), ActionKeys.DELETE);
		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP,
			String.valueOf(_accountEntry1.getAccountEntryGroupId()),
			accountRole.getRoleId(), ActionKeys.MANAGE_USERS);
		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
			defaultAccountRole.getRoleId(), ActionKeys.UPDATE);

		_assertHasPermission(user, ActionKeys.DELETE, true);
		_assertHasPermission(user, ActionKeys.MANAGE_USERS, true);
		_assertHasPermission(user, ActionKeys.UPDATE, true);

		_accountEntryUserRelLocalService.deleteAccountEntryUserRel(
			accountEntryUserRel);

		_accountEntryUserRels.remove(accountEntryUserRel);

		_assertHasPermission(user, ActionKeys.DELETE, false);
		_assertHasPermission(user, ActionKeys.MANAGE_USERS, false);
		_assertHasPermission(user, ActionKeys.UPDATE, false);

		_accountRoleLocalService.deleteAccountRole(defaultAccountRole);
	}

	@Test
	public void testDeleteAccountRole() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			AccountRoleLocalServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<ModelListener<UserGroupRole>> serviceRegistration =
			bundleContext.registerService(
				(Class<ModelListener<UserGroupRole>>)
					(Class<?>)ModelListener.class,
				new BaseModelListener<UserGroupRole>() {

					@Override
					public void onBeforeRemove(UserGroupRole userGroupRole)
						throws ModelListenerException {

						try {
							userGroupRole.getRole();
						}
						catch (PortalException portalException) {
							throw new ModelListenerException(portalException);
						}
					}

				},
				new HashMapDictionary<>());

		try {
			_testDeleteAccountRole(_accountRoleLocalService::deleteAccountRole);
			_testDeleteAccountRole(
				accountRole -> _accountRoleLocalService.deleteAccountRole(
					accountRole.getAccountRoleId()));
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testGetAccountRoles() throws Exception {
		List<AccountRole> accountRoles =
			_accountRoleLocalService.getAccountRolesByAccountEntryIds(
				new long[] {_accountEntry1.getAccountEntryId()});

		Assert.assertNotNull(accountRoles);

		Assert.assertEquals(accountRoles.toString(), 0, accountRoles.size());

		_addAccountRole(
			_accountEntry1.getAccountEntryId(),
			RandomTestUtil.randomString(50));
		_addAccountRole(
			_accountEntry2.getAccountEntryId(),
			RandomTestUtil.randomString(50));

		accountRoles =
			_accountRoleLocalService.getAccountRolesByAccountEntryIds(
				new long[] {_accountEntry1.getAccountEntryId()});

		Assert.assertNotNull(accountRoles);

		Assert.assertEquals(accountRoles.toString(), 1, accountRoles.size());
	}

	@Test
	public void testGetAccountRolesMultipleAccountEntries() throws Exception {
		List<AccountRole> accountRoles = new ArrayList<>();

		accountRoles.add(
			_addAccountRole(
				_accountEntry1.getAccountEntryId(),
				RandomTestUtil.randomString(50)));
		accountRoles.add(
			_addAccountRole(
				_accountEntry2.getAccountEntryId(),
				RandomTestUtil.randomString(50)));

		_addAccountRole(
			_accountEntry3.getAccountEntryId(),
			RandomTestUtil.randomString(50));

		List<AccountRole> actualAccountRoles =
			_accountRoleLocalService.getAccountRolesByAccountEntryIds(
				new long[] {
					_accountEntry1.getAccountEntryId(),
					_accountEntry2.getAccountEntryId()
				});

		Assert.assertEquals(
			actualAccountRoles.toString(), 2, actualAccountRoles.size());

		long[] expectedRoleIds = ListUtil.toLongArray(
			accountRoles, AccountRole::getRoleId);

		Arrays.sort(expectedRoleIds);

		long[] actualRoleIds = ListUtil.toLongArray(
			actualAccountRoles, AccountRole::getRoleId);

		Arrays.sort(actualRoleIds);

		Assert.assertArrayEquals(expectedRoleIds, actualRoleIds);
	}

	@Test
	public void testGetOrAddEmptyAccountRole() throws Exception {

		// Lazy referencing disabled

		try {
			_accountRoleLocalService.getOrAddEmptyAccountRole(
				RandomTestUtil.randomString(), TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), 0, RandomTestUtil.randomString());
		}
		catch (NoSuchRoleException noSuchRoleException) {
			Assert.assertNotNull(noSuchRoleException);
		}

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			AccountRole accountRole =
				_accountRoleLocalService.getOrAddEmptyAccountRole(
					RandomTestUtil.randomString(),
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					0, RandomTestUtil.randomString());

			Role role = accountRole.getRole();

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, role.getStatus());
		}
	}

	@Test
	public void testSearchAccountRoles() throws Exception {
		String keywords = RandomTestUtil.randomString();

		AccountRole accountRole = _addAccountRole(
			_accountEntry1.getAccountEntryId(),
			keywords + RandomTestUtil.randomString());

		_addAccountRole(
			_accountEntry1.getAccountEntryId(), RandomTestUtil.randomString());
		_addAccountRole(
			_accountEntry2.getAccountEntryId(),
			keywords + RandomTestUtil.randomString());

		BaseModelSearchResult<AccountRole> baseModelSearchResult =
			AccountRoleLocalServiceUtil.searchAccountRoles(
				_accountEntry1.getCompanyId(),
				new long[] {_accountEntry1.getAccountEntryId()},
				StringPool.BLANK, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(2, baseModelSearchResult.getLength());

		baseModelSearchResult = AccountRoleLocalServiceUtil.searchAccountRoles(
			_accountEntry1.getCompanyId(),
			new long[] {_accountEntry1.getAccountEntryId()}, keywords, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(1, baseModelSearchResult.getLength());

		List<AccountRole> accountRoles = baseModelSearchResult.getBaseModels();

		Assert.assertEquals(accountRole, accountRoles.get(0));
	}

	@Test
	public void testSearchAccountRolesByDescription() throws Exception {
		String keyword = RandomTestUtil.randomString();

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null,
			Collections.singletonMap(LocaleUtil.getDefault(), keyword));

		BaseModelSearchResult<AccountRole> baseModelSearchResult =
			_accountRoleLocalService.searchAccountRoles(
				accountRole.getCompanyId(),
				new long[] {AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT}, keyword,
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(1, baseModelSearchResult.getLength());

		List<AccountRole> accountRoles = baseModelSearchResult.getBaseModels();

		Assert.assertEquals(accountRole, accountRoles.get(0));
	}

	@Test
	public void testSearchAccountRolesByTitle() throws Exception {
		String keyword = RandomTestUtil.randomString();

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(),
			Collections.singletonMap(LocaleUtil.getDefault(), keyword), null);

		BaseModelSearchResult<AccountRole> baseModelSearchResult =
			_accountRoleLocalService.searchAccountRoles(
				accountRole.getCompanyId(),
				new long[] {AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT}, keyword,
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(1, baseModelSearchResult.getLength());

		List<AccountRole> accountRoles = baseModelSearchResult.getBaseModels();

		Assert.assertEquals(accountRole, accountRoles.get(0));
	}

	@Test
	public void testSearchAccountRolesWithDefaultAccountEntryId()
		throws Exception {

		String keyword = RandomTestUtil.randomString();

		AccountRole accountRoleA = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, keyword, null, null);

		User adminUserB = UserTestUtil.getAdminUser(_company.getCompanyId());

		AccountRole accountRoleB = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), adminUserB.getUserId(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, keyword, null, null);

		for (AccountRole accountRole :
				new AccountRole[] {accountRoleA, accountRoleB}) {

			BaseModelSearchResult<AccountRole> baseModelSearchResult =
				_accountRoleLocalService.searchAccountRoles(
					accountRole.getCompanyId(),
					new long[] {AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT},
					keyword, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.assertEquals(1, baseModelSearchResult.getLength());

			List<AccountRole> accountRoles =
				baseModelSearchResult.getBaseModels();

			Assert.assertEquals(accountRole, accountRoles.get(0));
		}
	}

	@Test
	public void testSearchAccountRolesWithPagination() throws Exception {
		String keywords = RandomTestUtil.randomString();

		List<AccountRole> expectedAccountRoles = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			expectedAccountRoles.add(
				_addAccountRole(
					_accountEntry1.getAccountEntryId(), keywords + i));
		}

		BaseModelSearchResult<AccountRole> baseModelSearchResult =
			AccountRoleLocalServiceUtil.searchAccountRoles(
				_accountEntry1.getCompanyId(),
				new long[] {_accountEntry1.getAccountEntryId()}, keywords, null,
				0, 2, RoleNameComparator.getInstance(true));

		Assert.assertEquals(
			expectedAccountRoles.toString(), 5,
			baseModelSearchResult.getLength());

		List<AccountRole> accountRoles = baseModelSearchResult.getBaseModels();

		Assert.assertEquals(accountRoles.toString(), 2, accountRoles.size());
		Assert.assertEquals(expectedAccountRoles.subList(0, 2), accountRoles);

		baseModelSearchResult = AccountRoleLocalServiceUtil.searchAccountRoles(
			_accountEntry1.getCompanyId(),
			new long[] {_accountEntry1.getAccountEntryId()}, keywords, null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			RoleNameComparator.getInstance(false));

		expectedAccountRoles = ListUtil.sort(
			expectedAccountRoles, Collections.reverseOrder());

		Assert.assertEquals(
			expectedAccountRoles, baseModelSearchResult.getBaseModels());
	}

	@Test
	public void testSearchAccountRolesWithParams() throws Exception {
		AccountRole accountRole1 = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_accountEntry1.getAccountEntryId(),
			RandomTestUtil.randomString() + " " + RandomTestUtil.randomString(),
			null, null);
		AccountRole accountRole2 = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_accountEntry1.getAccountEntryId(),
			RandomTestUtil.randomString() + " " + RandomTestUtil.randomString(),
			null, null);

		_testSearchAccountRolesWithParams(
			accountRole1.getCompanyId(),
			new long[] {_accountEntry1.getAccountEntryId()},
			LinkedHashMapBuilder.<String, Object>put(
				"excludedRoleNames", new String[] {accountRole1.getRoleName()}
			).build(),
			Collections.singletonList(accountRole2));
	}

	private AccountRole _addAccountRole(long accountEntryId, String name)
		throws Exception {

		return _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			accountEntryId, name, null, null);
	}

	private void _assertHasPermission(
		User user, String actionKey, boolean hasPermission) {

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		Assert.assertEquals(
			hasPermission,
			permissionChecker.hasPermission(
				_accountEntry1.getAccountEntryGroup(),
				AccountEntry.class.getName(),
				_accountEntry1.getAccountEntryId(), actionKey));
	}

	private long[] _getRoleIds(User user) throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		return permissionChecker.getRoleIds(
			user.getUserId(), _accountEntry1.getAccountEntryGroupId());
	}

	private boolean _hasRoleId(
			long accountEntryId, long roleId, long accountUserId)
		throws Exception {

		long[] accountRoleIds = ListUtil.toLongArray(
			_accountRoleLocalService.getAccountRoles(
				accountEntryId, accountUserId),
			AccountRole::getRoleId);

		return ArrayUtil.contains(accountRoleIds, roleId);
	}

	private void _testDeleteAccountRole(
			UnsafeFunction<AccountRole, AccountRole, PortalException>
				deleteAccountRoleUnsafeFunction)
		throws Exception {

		AccountRole accountRole = _addAccountRole(
			_accountEntry1.getAccountEntryId(), RandomTestUtil.randomString());

		User user = UserTestUtil.addUser();

		_users.add(user);

		_accountRoleLocalService.associateUser(
			_accountEntry1.getAccountEntryId(), accountRole.getAccountRoleId(),
			user.getUserId());

		Assert.assertTrue(
			ArrayUtil.contains(_getRoleIds(user), accountRole.getRoleId()));

		deleteAccountRoleUnsafeFunction.apply(accountRole);

		Assert.assertFalse(
			ArrayUtil.contains(
				_getRoleIds(_users.get(0)), accountRole.getRoleId()));
	}

	private void _testSearchAccountRolesWithParams(
		long companyId, long[] accountEntryIds,
		LinkedHashMap<String, Object> params,
		List<AccountRole> expectedAccountRoles) {

		BaseModelSearchResult<AccountRole> accountRoleBaseModelSearchResult =
			_accountRoleLocalService.searchAccountRoles(
				companyId, accountEntryIds, null, params, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			expectedAccountRoles,
			accountRoleBaseModelSearchResult.getBaseModels());
	}

	private static Company _company;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry1;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry2;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry3;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@DeleteAfterTestRun
	private final List<AccountEntryUserRel> _accountEntryUserRels =
		new ArrayList<>();

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}