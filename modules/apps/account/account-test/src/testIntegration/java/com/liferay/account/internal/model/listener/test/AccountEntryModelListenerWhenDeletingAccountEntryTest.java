/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.internal.model.listener.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.account.service.test.util.AccountGroupTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class AccountEntryModelListenerWhenDeletingAccountEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_accountEntry = AccountEntryTestUtil.addAccountEntry();
	}

	@Test
	public void testAccountEntryOrganizationRelDeleted() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			_accountEntry.getAccountEntryId(),
			organization.getOrganizationId());

		Assert.assertTrue(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					organization.getOrganizationId()));

		_accountEntryLocalService.deleteAccountEntry(_accountEntry);

		Assert.assertNull(
			_accountEntryLocalService.fetchAccountEntry(
				_accountEntry.getAccountEntryId()));

		Assert.assertFalse(
			_accountEntryOrganizationRelLocalService.
				hasAccountEntryOrganizationRel(
					_accountEntry.getAccountEntryId(),
					organization.getOrganizationId()));
	}

	@Test
	public void testAccountEntryUserRelDeleted() throws Exception {
		User user = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_accountEntry.getAccountEntryId(), user.getUserId());

		Assert.assertTrue(
			_accountEntryUserRelLocalService.hasAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));

		_accountEntryLocalService.deleteAccountEntry(_accountEntry);

		Assert.assertNull(
			_accountEntryLocalService.fetchAccountEntry(
				_accountEntry.getAccountEntryId()));

		Assert.assertFalse(
			_accountEntryUserRelLocalService.hasAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));
	}

	@Test
	public void testAccountGroupRelDeleted() throws Exception {
		AccountGroup accountGroup = AccountGroupTestUtil.addAccountGroup(
			_accountGroupLocalService, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		_accountGroupRelLocalService.addAccountGroupRel(
			accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			_accountEntry.getAccountEntryId());

		_accountEntryLocalService.deleteAccountEntry(
			_accountEntry.getAccountEntryId());

		List<AccountGroupRel> accountGroupRels =
			_accountGroupRelLocalService.getAccountGroupRelsByAccountGroupId(
				accountGroup.getAccountGroupId());

		Assert.assertEquals(
			accountGroupRels.toString(), 0, accountGroupRels.size());
	}

	@Test
	public void testAccountRoleDeleted() throws Exception {
		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_accountEntry.getAccountEntryId(), RandomTestUtil.randomString(),
			null, null);

		Assert.assertEquals(
			_accountEntry.getAccountEntryId(), accountRole.getAccountEntryId());

		_accountEntryLocalService.deleteAccountEntry(_accountEntry);

		Assert.assertNull(
			_accountRoleLocalService.fetchAccountRole(
				accountRole.getAccountRoleId()));
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}