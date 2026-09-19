/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.test;

import com.liferay.account.exception.DuplicateAccountGroupRelException;
import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.account.exception.NoSuchGroupException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.account.service.AccountGroupRelLocalService;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.account.service.test.util.AccountGroupTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Albert Lee
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class AccountGroupRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_accountEntry = AccountEntryTestUtil.addAccountEntry();

		_accountGroup = AccountGroupTestUtil.addAccountGroup(
			_accountGroupLocalService, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	@Test
	public void testAddAccountGroupRel() throws Exception {
		AccountGroupRel accountGroupRel =
			_accountGroupRelLocalService.addAccountGroupRel(
				_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
				_accountEntry.getAccountEntryId());

		Assert.assertNotNull(accountGroupRel);
		Assert.assertNotNull(
			_accountGroupRelLocalService.fetchAccountGroupRel(
				accountGroupRel.getPrimaryKey()));

		String name = PrincipalThreadLocal.getName();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.account.service.impl." +
					"AccountGroupRelLocalServiceImpl",
				LoggerTestUtil.WARN)) {

			PrincipalThreadLocal.setName(TestPropsValues.getUserId());

			AccountEntry accountEntry = AccountEntryTestUtil.addAccountEntry();

			accountGroupRel = _accountGroupRelLocalService.addAccountGroupRel(
				_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
				accountEntry.getAccountEntryId());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());

			Assert.assertEquals(
				TestPropsValues.getUserId(), accountGroupRel.getUserId());

			PrincipalThreadLocal.setName(RandomTestUtil.randomLong());

			accountEntry = AccountEntryTestUtil.addAccountEntry();

			accountGroupRel = _accountGroupRelLocalService.addAccountGroupRel(
				_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
				accountEntry.getAccountEntryId());

			logEntries = logCapture.getLogEntries();

			Assert.assertFalse(logEntries.isEmpty());

			Assert.assertEquals(
				_userLocalService.getGuestUserId(
					accountGroupRel.getCompanyId()),
				accountGroupRel.getUserId());
		}
		finally {
			PrincipalThreadLocal.setName(name);
		}
	}

	@Test(expected = DuplicateAccountGroupRelException.class)
	public void testAddAccountGroupRelThrowsDuplicateAccountGroupRelException()
		throws Exception {

		_accountGroupRelLocalService.addAccountGroupRel(
			_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			_accountEntry.getAccountEntryId());

		_accountGroupRelLocalService.addAccountGroupRel(
			_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			_accountEntry.getAccountEntryId());
	}

	@Test(expected = NoSuchEntryException.class)
	public void testAddAccountGroupRelThrowsNoSuchEntryException()
		throws Exception {

		_accountGroupRelLocalService.addAccountGroupRel(
			_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			_accountEntry.getAccountEntryId() + RandomTestUtil.nextLong());
	}

	@Test(expected = NoSuchGroupException.class)
	public void testAddAccountGroupRelThrowsNoSuchGroupException()
		throws Exception {

		_accountGroupRelLocalService.addAccountGroupRel(
			_accountGroup.getAccountGroupId() + RandomTestUtil.nextLong(),
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId());
	}

	@Test
	public void testAddAccountGroupRels() throws Exception {
		List<AccountEntry> accountEntries =
			AccountEntryTestUtil.addAccountEntries(2);

		_accountGroupRelLocalService.addAccountGroupRels(
			_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			ListUtil.toLongArray(
				accountEntries, AccountEntry.ACCOUNT_ENTRY_ID_ACCESSOR));

		Assert.assertEquals(
			2,
			_accountGroupRelLocalService.
				getAccountGroupRelsCountByAccountGroupId(
					_accountGroup.getAccountGroupId()));

		List<AccountGroupRel> accountGroupRels =
			_accountGroupRelLocalService.getAccountGroupRelsByAccountGroupId(
				_accountGroup.getAccountGroupId());

		for (AccountGroupRel accountGroupRel : accountGroupRels) {
			Assert.assertEquals(
				_accountGroup.getAccountGroupId(),
				accountGroupRel.getAccountGroupId());

			long[] accountEntryIds = ListUtil.toLongArray(
				accountEntries, AccountEntry.ACCOUNT_ENTRY_ID_ACCESSOR);

			Assert.assertTrue(
				ArrayUtil.contains(
					accountEntryIds, accountGroupRel.getClassPK()));
		}
	}

	@Test
	public void testDeleteAccountGroupRels() throws Exception {
		_accountGroupRelLocalService.addAccountGroupRels(
			_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			new long[] {_accountEntry.getAccountEntryId()});

		List<AccountGroupRel> accountGroupRels =
			_accountGroupRelLocalService.getAccountGroupRelsByAccountGroupId(
				_accountGroup.getAccountGroupId());

		Assert.assertEquals(
			accountGroupRels.toString(), 1, accountGroupRels.size());

		_accountGroupRelLocalService.deleteAccountGroupRels(
			_accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
			new long[] {_accountEntry.getAccountEntryId()});

		Assert.assertEquals(
			0,
			_accountGroupRelLocalService.
				getAccountGroupRelsCountByAccountGroupId(
					_accountGroup.getAccountGroupId()));
	}

	@Test
	public void testGetAccountGroupRels() throws Exception {
		String randomString = RandomTestUtil.randomString();

		AccountGroup accountGroup1 = AccountGroupTestUtil.addAccountGroup(
			_accountGroupLocalService, RandomTestUtil.randomString(),
			"AG1-" + randomString);

		List<AccountGroup> accountGroups = Arrays.asList(
			accountGroup1,
			AccountGroupTestUtil.addAccountGroup(
				_accountGroupLocalService, RandomTestUtil.randomString(),
				"AG2-" + randomString),
			AccountGroupTestUtil.addAccountGroup(
				_accountGroupLocalService, RandomTestUtil.randomString(),
				"AG3-" + randomString));

		for (AccountGroup accountGroup : accountGroups) {
			_accountGroupRelLocalService.addAccountGroupRel(
				accountGroup.getAccountGroupId(), AccountEntry.class.getName(),
				_accountEntry.getAccountEntryId());
		}

		List<AccountGroupRel> accountGroupRels =
			_accountGroupRelLocalService.getAccountGroupRels(
				ListUtil.toLongArray(
					accountGroups, AccountGroup.ACCOUNT_GROUP_ID_ACCESSOR),
				AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
				accountGroup1.getName(), 0, 10);

		Assert.assertEquals(
			accountGroupRels.toString(), 1, accountGroupRels.size());

		AccountGroupRel accountGroupRel = accountGroupRels.get(0);

		Assert.assertEquals(
			accountGroup1.getAccountGroupId(),
			accountGroupRel.getAccountGroupId());
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private AccountGroup _accountGroup;

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private AccountGroupRelLocalService _accountGroupRelLocalService;

	@Inject
	private UserLocalService _userLocalService;

}