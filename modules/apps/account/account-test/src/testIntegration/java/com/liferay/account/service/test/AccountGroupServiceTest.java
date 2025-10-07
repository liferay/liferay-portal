/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupService;
import com.liferay.account.service.test.util.AccountGroupTestUtil;
import com.liferay.account.service.test.util.UserRoleTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class AccountGroupServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddAccountGroup() throws Exception {

		// With permissions

		User user = UserTestUtil.addUser();

		UserRoleTestUtil.addResourcePermission(
			AccountActionKeys.ADD_ACCOUNT_GROUP, PortletKeys.PORTAL,
			user.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			AccountGroup accountGroup = _accountGroupService.addAccountGroup(
				StringPool.BLANK, user.getUserId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				new ServiceContext());

			Assert.assertNotNull(accountGroup);
		}

		// Without permissions

		user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			_accountGroupService.addAccountGroup(
				StringPool.BLANK, user.getUserId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				new ServiceContext());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertNotNull(principalException);
		}
	}

	@Test
	public void testDeleteAccountGroup() throws Exception {

		// With permissions

		User user = UserTestUtil.addUser();

		UserRoleTestUtil.addResourcePermission(
			ActionKeys.DELETE, AccountGroup.class.getName(), user.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			AccountGroup accountGroup = AccountGroupTestUtil.addAccountGroup(
				_accountGroupLocalService, RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			accountGroup = _accountGroupService.deleteAccountGroup(
				accountGroup.getAccountGroupId());

			Assert.assertNotNull(accountGroup);
		}

		// Without permissions

		user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			AccountGroup accountGroup = AccountGroupTestUtil.addAccountGroup(
				_accountGroupLocalService, RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			_accountGroupService.deleteAccountGroup(
				accountGroup.getAccountGroupId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertNotNull(principalException);
		}
	}

	@Test
	public void testGetOrAddEmptyAccountGroup() throws Exception {
		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			// With permissions

			User user = UserTestUtil.addUser();

			UserRoleTestUtil.addResourcePermission(
				AccountActionKeys.ADD_ACCOUNT_GROUP, PortletKeys.PORTAL,
				user.getUserId());

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, PermissionCheckerFactoryUtil.create(user))) {

				AccountGroup accountGroup =
					_accountGroupService.getOrAddEmptyAccountGroup(
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString());

				Assert.assertNotNull(accountGroup);
			}

			// Without permissions

			user = UserTestUtil.addUser();

			try (ContextUserReplace contextUserReplace = new ContextUserReplace(
					user, PermissionCheckerFactoryUtil.create(user))) {

				_accountGroupService.getOrAddEmptyAccountGroup(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString());

				Assert.fail();
			}
			catch (PrincipalException.MustHavePermission principalException) {
				Assert.assertNotNull(principalException);
			}
		}
	}

	@Test
	public void testSearchAccountGroups() throws Exception {

		// With permissions

		AccountGroupTestUtil.addAccountGroup(
			_accountGroupLocalService, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
		AccountGroupTestUtil.addAccountGroup(
			_accountGroupLocalService, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		User user = UserTestUtil.addUser();

		UserRoleTestUtil.addResourcePermission(
			ActionKeys.VIEW, AccountGroup.class.getName(), user.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			List<AccountGroup> expectedAccountGroups = ListUtil.filter(
				_accountGroupLocalService.getAccountGroups(
					user.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null),
				accountGroup -> !accountGroup.isDefaultAccountGroup());

			BaseModelSearchResult<AccountGroup> baseModelSearchResult =
				_accountGroupService.searchAccountGroups(
					user.getCompanyId(), null, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

			Assert.assertEquals(
				expectedAccountGroups.size(),
				baseModelSearchResult.getLength());

			List<AccountGroup> accountGroups =
				baseModelSearchResult.getBaseModels();

			Assert.assertTrue(accountGroups.containsAll(expectedAccountGroups));
		}

		// Without permissions

		user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			BaseModelSearchResult<AccountGroup> baseModelSearchResult =
				_accountGroupService.searchAccountGroups(
					user.getCompanyId(), null, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null);

			Assert.assertEquals(0, baseModelSearchResult.getLength());
			Assert.assertTrue(
				ListUtil.isEmpty(baseModelSearchResult.getBaseModels()));
		}
	}

	@Test
	public void testUpdateAccountGroup() throws Exception {

		// With permissions

		User user = UserTestUtil.addUser();

		UserRoleTestUtil.addResourcePermission(
			ActionKeys.UPDATE, AccountGroup.class.getName(), user.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			AccountGroup accountGroup = AccountGroupTestUtil.addAccountGroup(
				_accountGroupLocalService, RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			accountGroup = _accountGroupService.updateAccountGroup(
				StringPool.BLANK, accountGroup.getAccountGroupId(),
				RandomTestUtil.randomString(), accountGroup.getName(),
				new ServiceContext());

			Assert.assertNotNull(accountGroup);
		}

		// Without permissions

		user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			AccountGroup accountGroup = AccountGroupTestUtil.addAccountGroup(
				_accountGroupLocalService, RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			_accountGroupService.updateAccountGroup(
				StringPool.BLANK, accountGroup.getAccountGroupId(),
				RandomTestUtil.randomString(), accountGroup.getName(),
				new ServiceContext());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertNotNull(principalException);
		}
	}

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private AccountGroupService _accountGroupService;

}