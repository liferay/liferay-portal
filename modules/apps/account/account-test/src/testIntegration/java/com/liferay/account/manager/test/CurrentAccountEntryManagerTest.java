/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.manager.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.exception.AccountEntryTypeException;
import com.liferay.account.manager.CurrentAccountEntryManager;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.role.AccountRolePermissionThreadLocal;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.test.util.AccountEntryArgs;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.account.settings.AccountEntryGroupSettings;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class CurrentAccountEntryManagerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() {
		AccountRolePermissionThreadLocal.setAccountEntryIdWithSafeCloseable(0L);
	}

	@Test
	public void testGetCurrentAccountEntry() throws Exception {
		AccountEntry accountEntry = AccountEntryTestUtil.addAccountEntry(
			AccountEntryArgs.withName("aaa"),
			AccountEntryArgs.withUsers(_user));

		AccountEntryTestUtil.addAccountEntry(
			AccountEntryArgs.withName("bbb"),
			AccountEntryArgs.withUsers(_user));

		Assert.assertEquals(
			accountEntry,
			_currentAccountEntryManager.getCurrentAccountEntry(
				_group.getGroupId(), _user.getUserId()));
	}

	@Test
	public void testGetCurrentAccountEntryDefault() throws Exception {
		AccountEntryTestUtil.addAccountEntry(
			AccountEntryArgs.withName("aInactive"),
			AccountEntryArgs.STATUS_INACTIVE,
			AccountEntryArgs.withUsers(_user));
		AccountEntryTestUtil.addAccountEntry(
			AccountEntryArgs.withName("bInvalidType"),
			AccountEntryArgs.TYPE_PERSON, AccountEntryArgs.withUsers(_user));

		_setAllowedTypes(
			_group.getGroupId(),
			new String[] {AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS});

		Organization organization = OrganizationTestUtil.addOrganization();

		AccountEntryTestUtil.addAccountEntry(
			AccountEntryArgs.withName("cNoPermission"),
			AccountEntryArgs.withOrganizations(organization));

		_organizationLocalService.addUserOrganization(
			_user.getUserId(), organization.getOrganizationId());

		AccountEntry expectedAccountEntry =
			AccountEntryTestUtil.addAccountEntry(
				AccountEntryArgs.withName("dHasPermission"),
				AccountEntryArgs.withUsers(_user));

		AccountEntry currentAccountEntry =
			_currentAccountEntryManager.getCurrentAccountEntry(
				_group.getGroupId(), _user.getUserId());

		Assert.assertNotNull(currentAccountEntry);
		Assert.assertEquals(expectedAccountEntry, currentAccountEntry);
	}

	@Test
	public void testGetCurrentAccountEntryForGroupWithRestrictedTypes()
		throws Exception {

		Group group = GroupTestUtil.addGroup();
		AccountEntry personAccountEntry = AccountEntryTestUtil.addAccountEntry(
			AccountEntryArgs.TYPE_PERSON, AccountEntryArgs.withUsers(_user));

		_currentAccountEntryManager.setCurrentAccountEntry(
			personAccountEntry.getAccountEntryId(), group.getGroupId(),
			_user.getUserId());

		Assert.assertEquals(
			personAccountEntry,
			_currentAccountEntryManager.getCurrentAccountEntry(
				group.getGroupId(), _user.getUserId()));

		_setAllowedTypes(
			group.getGroupId(),
			new String[] {AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS});

		Assert.assertNull(
			_currentAccountEntryManager.getCurrentAccountEntry(
				group.getGroupId(), _user.getUserId()));
	}

	@Test
	public void testGetCurrentAccountEntryForGuestUser() throws Exception {
		Assert.assertEquals(
			_accountEntryLocalService.getGuestAccountEntry(
				TestPropsValues.getCompanyId()),
			_currentAccountEntryManager.getCurrentAccountEntry(
				_group.getGroupId(), UserConstants.USER_ID_DEFAULT));
	}

	@Test
	public void testGetCurrentAccountEntryForUserWithNoAccountEntries()
		throws Exception {

		Assert.assertNull(
			_currentAccountEntryManager.getCurrentAccountEntry(
				_group.getGroupId(), _user.getUserId()));
	}

	@Test
	public void testGetCurrentAccountEntryWithNoViewPermission()
		throws Exception {

		AccountEntry accountEntry = AccountEntryTestUtil.addAccountEntry();

		_currentAccountEntryManager.setCurrentAccountEntry(
			accountEntry.getAccountEntryId(), _group.getGroupId(),
			_user.getUserId());

		Assert.assertNull(
			_currentAccountEntryManager.getCurrentAccountEntry(
				_group.getGroupId(), _user.getUserId()));
	}

	@Test
	public void testGetCurrentAccountEntryWithViewPermission()
		throws Exception {

		AccountEntry accountEntry = AccountEntryTestUtil.addAccountEntry(
			AccountEntryArgs.withUsers(_user));

		_currentAccountEntryManager.setCurrentAccountEntry(
			accountEntry.getAccountEntryId(), _group.getGroupId(),
			_user.getUserId());

		Assert.assertEquals(
			accountEntry,
			_currentAccountEntryManager.getCurrentAccountEntry(
				_group.getGroupId(), _user.getUserId()));
	}

	@Test
	public void testSetCurrentAccountEntry() throws Exception {
		AccountEntry accountEntry = AccountEntryTestUtil.addAccountEntry(
			AccountEntryArgs.withUsers(_user));

		_currentAccountEntryManager.setCurrentAccountEntry(
			accountEntry.getAccountEntryId(), _group.getGroupId(),
			_user.getUserId());

		Assert.assertEquals(
			accountEntry,
			_currentAccountEntryManager.getCurrentAccountEntry(
				_group.getGroupId(), _user.getUserId()));
	}

	@Test
	public void testSetCurrentAccountEntryForGroupWithRestrictedTypes()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.account.internal.manager." +
					"CurrentAccountEntryManagerImpl",
				LoggerTestUtil.WARN)) {

			Group group = GroupTestUtil.addGroup();

			_setAllowedTypes(
				group.getGroupId(),
				new String[] {AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS});

			AccountEntry personAccountEntry =
				AccountEntryTestUtil.addAccountEntry(
					AccountEntryArgs.TYPE_PERSON);

			_currentAccountEntryManager.setCurrentAccountEntry(
				personAccountEntry.getAccountEntryId(), group.getGroupId(),
				_user.getUserId());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(LoggerTestUtil.WARN, logEntry.getPriority());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				AccountEntryTypeException.class, throwable.getClass());

			Assert.assertEquals(
				"Cannot set a current account entry of a disallowed type: " +
					"person",
				throwable.getMessage());
		}
	}

	private void _setAllowedTypes(long groupId, String[] allowedTypes)
		throws Exception {

		_accountEntryGroupSettings.setAllowedTypes(groupId, allowedTypes);

		// Force async operations to complete before returning

		ConfigurationTestUtil.saveConfiguration(
			RandomTestUtil.randomString(), null);

		AccountRolePermissionThreadLocal.setAccountEntryIdWithSafeCloseable(0L);
	}

	@Inject
	private AccountEntryGroupSettings _accountEntryGroupSettings;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private CurrentAccountEntryManager _currentAccountEntryManager;

	private Group _group;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private User _user;

}