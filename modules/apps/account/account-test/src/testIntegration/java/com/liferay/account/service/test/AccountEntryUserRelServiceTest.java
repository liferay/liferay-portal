/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.test;

import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountEntryUserRelService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class AccountEntryUserRelServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_accountEntry = AccountEntryTestUtil.addAccountEntry();

		_accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_accountEntry.getAccountEntryId(), RandomTestUtil.randomString(),
			null, null);

		_user = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_accountEntry.getAccountEntryId(), _user.getUserId());

		_accountRoleLocalService.associateUser(
			_accountEntry.getAccountEntryId(), _accountRole.getAccountRoleId(),
			_user.getUserId());

		_serviceContext = ServiceContextTestUtil.getServiceContext();

		_serviceContext.setRequest(new MockHttpServletRequest());

		UserTestUtil.setUser(_user);
	}

	@Test
	public void testAddAccountEntryUserRelByEmailAddress() throws Exception {
		try {
			_accountEntryUserRelService.addAccountEntryUserRelByEmailAddress(
				_accountEntry.getAccountEntryId(), _user.getEmailAddress(),
				new long[0], _user.getExternalReferenceCode(), _serviceContext);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertNotNull(principalException);
		}

		try {
			_accountEntryUserRelService.addAccountEntryUserRelByEmailAddress(
				_accountEntry.getAccountEntryId(),
				"A" + RandomTestUtil.randomString() + "@liferay.com",
				new long[0], RandomTestUtil.randomString(), _serviceContext);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertNotNull(principalException);
		}

		_addResourcePermission(AccountActionKeys.ASSIGN_USERS);

		try {
			_accountEntryUserRelService.addAccountEntryUserRelByEmailAddress(
				_accountEntry.getAccountEntryId(),
				"A" + RandomTestUtil.randomString() + "@liferay.com",
				new long[0], RandomTestUtil.randomString(), _serviceContext);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertNotNull(principalException);
		}

		_addResourcePermission(AccountActionKeys.ADD_USER);

		_accountEntryUserRelService.addAccountEntryUserRelByEmailAddress(
			_accountEntry.getAccountEntryId(),
			"A" + RandomTestUtil.randomString() + "@liferay.com", new long[0],
			RandomTestUtil.randomString(), _serviceContext);
	}

	@Test
	public void testAddUserInvitationTicket() throws Exception {
		try {
			_accountEntryUserRelService.addUserInvitationTicket(
				_accountEntry.getAccountEntryId(), null,
				"A" + RandomTestUtil.randomString() + "@liferay.com", _user,
				_serviceContext);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertNotNull(principalException);
		}

		_addResourcePermission(AccountActionKeys.INVITE_USER);

		_accountEntryUserRelService.addUserInvitationTicket(
			_accountEntry.getAccountEntryId(), null,
			"A" + RandomTestUtil.randomString() + "@liferay.com", _user,
			_serviceContext);
	}

	@Test
	public void testInviteUser() throws Exception {
		try {
			_accountEntryUserRelService.inviteUser(
				_accountEntry.getAccountEntryId(), null,
				"A" + RandomTestUtil.randomString() + "@liferay.com", _user,
				_serviceContext);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertNotNull(principalException);
		}

		_addResourcePermission(AccountActionKeys.INVITE_USER);

		_accountEntryUserRelService.inviteUser(
			_accountEntry.getAccountEntryId(), null,
			"A" + RandomTestUtil.randomString() + "@liferay.com", _user,
			_serviceContext);
	}

	@Test
	public void testUpdateAccountEntryUserRels() throws Exception {
		long accountEntryId = _accountEntry.getAccountEntryId();
		User user = UserTestUtil.addUser();

		try {
			_accountEntryUserRelService.updateAccountEntryUserRels(
				new long[] {accountEntryId}, new long[0], user.getUserId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertNotNull(principalException);
		}

		Assert.assertFalse(
			_accountEntryUserRelLocalService.hasAccountEntryUserRel(
				accountEntryId, user.getUserId()));

		_addResourcePermission(AccountActionKeys.ASSIGN_USERS);

		_accountEntryUserRelService.updateAccountEntryUserRels(
			new long[] {accountEntryId}, new long[0], user.getUserId());

		Assert.assertTrue(
			_accountEntryUserRelLocalService.hasAccountEntryUserRel(
				accountEntryId, user.getUserId()));

		try {
			_accountEntryUserRelService.updateAccountEntryUserRels(
				new long[0], new long[] {accountEntryId}, user.getUserId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
			Assert.assertNotNull(principalException);
		}

		Assert.assertTrue(
			_accountEntryUserRelLocalService.hasAccountEntryUserRel(
				accountEntryId, user.getUserId()));

		_addResourcePermission(AccountActionKeys.UNASSIGN_USERS);

		_accountEntryUserRelService.updateAccountEntryUserRels(
			new long[0], new long[] {accountEntryId}, user.getUserId());

		Assert.assertFalse(
			_accountEntryUserRelLocalService.hasAccountEntryUserRel(
				accountEntryId, user.getUserId()));
	}

	private void _addResourcePermission(String actionId) throws Exception {
		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			_accountRole.getRoleId(), actionId);
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private AccountEntryUserRelService _accountEntryUserRelService;

	private AccountRole _accountRole;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}