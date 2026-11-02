/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.test;

import com.liferay.account.configuration.AccountEntryEmailConfiguration;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.exception.AccountEntryTypeException;
import com.liferay.account.exception.DuplicateAccountEntryIdException;
import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.account.exception.NoSuchEntryUserRelException;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.model.AccountEntryUserRelModel;
import com.liferay.account.model.AccountRole;
import com.liferay.account.retriever.AccountUserRetriever;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.account.service.test.util.AccountEntryArgs;
import com.liferay.account.service.test.util.AccountEntryTestUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.mail.MailMessage;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Drew Brokke
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class AccountEntryUserRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_accountEntry = AccountEntryTestUtil.addAccountEntry();

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testAddAccountEntryUserRel1() throws Exception {
		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelLocalService.addAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), _user.getUserId());

		Assert.assertNotNull(accountEntryUserRel);
		Assert.assertNotNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				accountEntryUserRel.getPrimaryKey()));
	}

	@Test
	public void testAddAccountEntryUserRel1DoesNotAddDuplicate()
		throws Exception {

		AccountEntryUserRel accountEntryUserRel1 =
			_accountEntryUserRelLocalService.addAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), _user.getUserId());
		AccountEntryUserRel accountEntryUserRel2 =
			_accountEntryUserRelLocalService.addAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), _user.getUserId());

		Assert.assertEquals(
			accountEntryUserRel1.getAccountEntryUserRelId(),
			accountEntryUserRel2.getAccountEntryUserRelId());
	}

	@Test(expected = NoSuchEntryException.class)
	public void testAddAccountEntryUserRel1ThrowsNoSuchEntryException()
		throws Exception {

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_accountEntry.getAccountEntryId() + RandomTestUtil.nextLong(),
			_user.getUserId());
	}

	@Test(expected = NoSuchUserException.class)
	public void testAddAccountEntryUserRel1ThrowsNoSuchUserException()
		throws Exception {

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_accountEntry.getAccountEntryId(),
			_user.getUserId() + RandomTestUtil.nextLong());
	}

	@Test
	public void testAddAccountEntryUserRel2() throws Exception {
		_testAddAccountEntryUserRel2(
			_accountEntry.getAccountEntryId(), _accountEntry.getCompanyId());
	}

	@Test
	public void testAddAccountEntryUserRel2WithBlockedEmailDomain()
		throws Exception {

		String originalName = PrincipalThreadLocal.getName();

		String pid =
			"com.liferay.account.configuration." +
				"AccountEntryEmailDomainsConfiguration";

		ConfigurationTestUtil.saveConfiguration(
			pid,
			HashMapDictionaryBuilder.<String, Object>put(
				"blockedEmailDomains", "test.com"
			).put(
				"enableEmailDomainValidation", false
			).build());

		try {
			PrincipalThreadLocal.setName(_user.getUserId());

			_userInfo.emailAddress = _userInfo.screenName + "@test.com";

			_addAccountEntryUserRel(_accountEntry.getAccountEntryId());

			Assert.fail();
		}
		catch (UserEmailAddressException.MustNotUseBlockedDomain
					userEmailAddressException) {

			Assert.assertEquals(
				_userInfo.emailAddress, userEmailAddressException.emailAddress);
			Assert.assertEquals(
				String.format(
					"Email address %s must not use one of the blocked " +
						"domains: %s",
					_userInfo.emailAddress, "test.com"),
				userEmailAddressException.getMessage());
		}
		finally {
			PrincipalThreadLocal.setName(originalName);

			ConfigurationTestUtil.deleteConfiguration(pid);
		}
	}

	@Test
	public void testAddAccountEntryUserRel2WithDefaultAccountEntryId()
		throws Exception {

		_testAddAccountEntryUserRel2(
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			TestPropsValues.getCompanyId());
	}

	@Test
	public void testAddAccountEntryUserRel2WithInvalidAccountEntryId()
		throws Exception {

		long invalidAccountEntryId = RandomTestUtil.nextLong();

		try {
			_addAccountEntryUserRel(invalidAccountEntryId);

			Assert.fail();
		}
		catch (NoSuchEntryException noSuchEntryException) {
			String message = noSuchEntryException.getMessage();

			Assert.assertTrue(
				message.contains(
					"No AccountEntry exists with the primary key " +
						invalidAccountEntryId));
		}

		Assert.assertNull(
			_userLocalService.fetchUserByScreenName(
				TestPropsValues.getCompanyId(), _userInfo.screenName));
	}

	@Test
	public void testAddAccountEntryUserRel2WithInvalidUserEmailAddressDomain()
		throws Exception {

		String originalName = PrincipalThreadLocal.getName();

		String pid =
			"com.liferay.account.configuration." +
				"AccountEntryEmailDomainsConfiguration";

		ConfigurationTestUtil.saveConfiguration(
			pid,
			HashMapDictionaryBuilder.<String, Object>put(
				"enableEmailDomainValidation", true
			).build());

		try {
			_accountEntry.setDomains("test1.com,test2.com");
			_accountEntry.setRestrictMembership(true);

			_accountEntry = _accountEntryLocalService.updateAccountEntry(
				_accountEntry);

			PrincipalThreadLocal.setName(_user.getUserId());

			_userInfo.emailAddress =
				_userInfo.screenName + "@invalid-domain.com";

			try {
				_addAccountEntryUserRel(_accountEntry.getAccountEntryId());

				Assert.fail();
			}
			catch (UserEmailAddressException.MustHaveValidDomain
						userEmailAddressException) {

				Assert.assertEquals(
					_userInfo.emailAddress,
					userEmailAddressException.emailAddress);
				Assert.assertEquals(
					_accountEntry.getDomains(),
					userEmailAddressException.validDomains);
				Assert.assertEquals(
					String.format(
						"Email address %s must have one of the valid " +
							"domains: %s",
						_userInfo.emailAddress, _accountEntry.getDomains()),
					userEmailAddressException.getMessage());
			}

			_accountEntry = _accountEntryLocalService.updateRestrictMembership(
				_accountEntry.getAccountEntryId(), false);

			_addAccountEntryUserRel(_accountEntry.getAccountEntryId());
		}
		finally {
			PrincipalThreadLocal.setName(originalName);

			ConfigurationTestUtil.deleteConfiguration(pid);
		}
	}

	@Test
	public void testAddAccountEntryUserRel2WithInvalidUserInfo()
		throws Exception {

		String invalidEmailAddress = "liferay";

		_userInfo.emailAddress = invalidEmailAddress;

		try {
			_addAccountEntryUserRel(_accountEntry.getAccountEntryId());

			Assert.fail();
		}
		catch (UserEmailAddressException.MustValidate
					userEmailAddressException) {

			String message = userEmailAddressException.getMessage();

			Assert.assertTrue(
				message.contains(
					"Email name address " + invalidEmailAddress +
						" must validate with"));
		}

		Assert.assertNull(
			_userLocalService.fetchUserByScreenName(
				TestPropsValues.getCompanyId(), _userInfo.screenName));
	}

	@Test
	public void testAddAccountEntryUserRelByEmailAddressForExistingUser()
		throws Exception {

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_accountEntry.getAccountEntryId(), RandomTestUtil.randomString(),
			null, null);

		User user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(), null,
			_userInfo.emailAddress, _userInfo.screenName,
			LocaleUtil.getDefault(), _userInfo.firstName, _userInfo.lastName,
			null, ServiceContextTestUtil.getServiceContext());

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelLocalService.
				addAccountEntryUserRelByEmailAddress(
					_accountEntry.getAccountEntryId(), _userInfo.emailAddress,
					new long[] {accountRole.getAccountRoleId()}, null,
					ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			user.getUserId(), accountEntryUserRel.getAccountUserId());

		Assert.assertTrue(
			_accountRoleLocalService.hasUserAccountRole(
				_accountEntry.getAccountEntryId(),
				accountRole.getAccountRoleId(), user.getUserId()));
	}

	@Test
	public void testAddAccountEntryUserRelByEmailAddressForNonexistingUser()
		throws Exception {

		AccountRole accountRole = _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_accountEntry.getAccountEntryId(), RandomTestUtil.randomString(),
			null, null);

		Assert.assertNull(
			_userLocalService.fetchUserByEmailAddress(
				TestPropsValues.getCompanyId(), _userInfo.emailAddress));

		AccountEntryUserRel accountEntryUserRel =
			_accountEntryUserRelLocalService.
				addAccountEntryUserRelByEmailAddress(
					_accountEntry.getAccountEntryId(), _userInfo.emailAddress,
					new long[] {accountRole.getAccountRoleId()}, null,
					ServiceContextTestUtil.getServiceContext());

		User user = _userLocalService.fetchUser(
			accountEntryUserRel.getAccountUserId());

		Assert.assertEquals(
			user,
			_userLocalService.fetchUserByEmailAddress(
				TestPropsValues.getCompanyId(), _userInfo.emailAddress));
		Assert.assertTrue(
			_accountRoleLocalService.hasUserAccountRole(
				_accountEntry.getAccountEntryId(),
				accountRole.getAccountRoleId(), user.getUserId()));
	}

	@Test
	public void testAddAccountEntryUserRels() throws Exception {
		List<User> users = new ArrayList<>();

		users.add(UserTestUtil.addUser());
		users.add(UserTestUtil.addUser());

		List<Long> userIds = ListUtil.toList(users, User.USER_ID_ACCESSOR);

		userIds.sort(null);

		_accountEntryUserRelLocalService.addAccountEntryUserRels(
			_accountEntry.getAccountEntryId(), ArrayUtil.toLongArray(userIds));

		Assert.assertEquals(
			2,
			_accountUserRetriever.getAccountUsersCount(
				_accountEntry.getAccountEntryId()));

		List<Long> accountUserIds = ListUtil.toList(
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountEntryId(
					_accountEntry.getAccountEntryId()),
			AccountEntryUserRel::getAccountUserId);

		accountUserIds.sort(null);

		Assert.assertEquals(userIds, accountUserIds);
	}

	@Test
	public void testAddPersonTypeAccountEntryUserRel() throws Exception {
		AccountEntry personTypeAccountEntry =
			AccountEntryTestUtil.addAccountEntry(AccountEntryArgs.TYPE_PERSON);

		AccountEntryUserRel accountEntryUserRel1 =
			_addPersonTypeAccountEntryUserRel(
				personTypeAccountEntry.getAccountEntryId());

		Assert.assertNotNull(
			_userLocalService.fetchUser(
				accountEntryUserRel1.getAccountUserId()));

		_assertPersonTypeAccountEntryUser(
			new long[] {accountEntryUserRel1.getAccountUserId()},
			personTypeAccountEntry.getAccountEntryId());

		AccountEntryUserRel accountEntryUserRel2 =
			_addPersonTypeAccountEntryUserRel(
				personTypeAccountEntry.getAccountEntryId());

		_assertPersonTypeAccountEntryUser(
			new long[] {accountEntryUserRel2.getAccountUserId()},
			personTypeAccountEntry.getAccountEntryId());
	}

	@Test
	public void testAddUserInvitationTicket() throws Exception {
		long[] accountRoleIds = {RandomTestUtil.randomLong()};
		String emailAddress =
			"A" + RandomTestUtil.randomString() + "@liferay.com";
		int initialInboxSize = MailServiceTestUtil.getInboxSize();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setRequest(new MockHttpServletRequest());

		Ticket ticket =
			_accountEntryUserRelLocalService.addUserInvitationTicket(
				_accountEntry.getAccountEntryId(), accountRoleIds, emailAddress,
				TestPropsValues.getUser(), serviceContext);

		Assert.assertEquals(
			AccountEntry.class.getName(), ticket.getClassName());
		Assert.assertEquals(
			_accountEntry.getAccountEntryId(), ticket.getClassPK());

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			ticket.getExtraInfo());

		Assert.assertNotNull(jsonObject.getJSONArray("accountRoleIds"));
		Assert.assertEquals(emailAddress, jsonObject.getString("emailAddress"));

		Assert.assertEquals(
			initialInboxSize, MailServiceTestUtil.getInboxSize());
	}

	@Test
	public void testDeleteAccountEntryUserRelByEmailAddress() throws Exception {
		User user = UserTestUtil.addUser();

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			_accountEntry.getAccountEntryId(), user.getUserId());

		Assert.assertNotNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));

		_accountEntryUserRelLocalService.
			deleteAccountEntryUserRelByEmailAddress(
				_accountEntry.getAccountEntryId(), user.getEmailAddress());

		Assert.assertNull(
			_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));
	}

	@Test
	public void testDeleteAccountEntryUserRels() throws Exception {
		AccountEntryUserRel accountEntryUserRel = _addAccountEntryUserRel(
			_accountEntry.getAccountEntryId());

		List<AccountEntryUserRel> accountEntryUserRels =
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountEntryId(
					_accountEntry.getAccountEntryId());

		Assert.assertEquals(
			accountEntryUserRels.toString(), 1, accountEntryUserRels.size());

		_accountEntryUserRelLocalService.deleteAccountEntryUserRels(
			_accountEntry.getAccountEntryId(),
			new long[] {accountEntryUserRel.getAccountUserId()});

		accountEntryUserRels =
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountEntryId(
					_accountEntry.getAccountEntryId());

		Assert.assertEquals(
			accountEntryUserRels.toString(), 0, accountEntryUserRels.size());
	}

	@Test
	public void testDeleteAccountEntryUserRelsByAccountEntryId()
		throws Exception {

		_testDeleteAccountEntryUserRelsByAccountEntryId(0);

		List<User> users = ListUtil.fromArray(
			UserTestUtil.addUser(), UserTestUtil.addUser(),
			UserTestUtil.addUser());

		for (User user : users) {
			_accountEntryUserRelLocalService.addAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId());
		}

		_testDeleteAccountEntryUserRelsByAccountEntryId(users.size());

		_accountEntryUserRelLocalService.
			deleteAccountEntryUserRelsByAccountEntryId(
				_accountEntry.getAccountEntryId());

		_testDeleteAccountEntryUserRelsByAccountEntryId(0);
	}

	@Test(expected = NoSuchEntryUserRelException.class)
	public void testDeleteAccountEntryUserRelsThrowsNoSuchEntryUserRelException()
		throws Exception {

		_accountEntryUserRelLocalService.deleteAccountEntryUserRels(
			_accountEntry.getAccountEntryId(), new long[] {_user.getUserId()});
	}

	@Test
	public void testGetAccountEntryUserRelsByAccountEntryId() throws Exception {
		List<User> users = new ArrayList<>();

		users.add(UserTestUtil.addUser());
		users.add(UserTestUtil.addUser());
		users.add(UserTestUtil.addUser());

		for (User user : users) {
			_accountEntryUserRelLocalService.addAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId());
		}

		List<AccountEntryUserRel> accountEntryUserRels =
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountEntryId(
					_accountEntry.getAccountEntryId());

		long[] expectedUserIds = ListUtil.toLongArray(
			users, User.USER_ID_ACCESSOR);

		Arrays.sort(expectedUserIds);

		long[] actualUserIds = ListUtil.toLongArray(
			accountEntryUserRels, AccountEntryUserRelModel::getAccountUserId);

		Arrays.sort(actualUserIds);

		Assert.assertArrayEquals(expectedUserIds, actualUserIds);
	}

	@Test
	public void testInviteUser() throws Exception {
		int initialInboxSize = MailServiceTestUtil.getInboxSize();

		User user = UserTestUtil.addUser();

		Assert.assertFalse(
			_accountEntryUserRelLocalService.hasAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));

		_accountEntryUserRelLocalService.inviteUser(
			_accountEntry.getAccountEntryId(), null, user.getEmailAddress(),
			TestPropsValues.getUser(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(
			_accountEntryUserRelLocalService.hasAccountEntryUserRel(
				_accountEntry.getAccountEntryId(), user.getUserId()));

		Assert.assertEquals(
			initialInboxSize, MailServiceTestUtil.getInboxSize());

		String emailAddress = "newuser@liferay.com";

		Assert.assertNull(
			_userLocalService.fetchUserByEmailAddress(
				_accountEntry.getCompanyId(), emailAddress));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setRequest(new MockHttpServletRequest());

		_accountEntryUserRelLocalService.inviteUser(
			_accountEntry.getAccountEntryId(), null, emailAddress,
			TestPropsValues.getUser(), serviceContext);

		Assert.assertEquals(
			initialInboxSize + 1, MailServiceTestUtil.getInboxSize());

		MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

		Assert.assertEquals(
			emailAddress, mailMessage.getFirstHeaderValue("To"));

		String mailMessageBody = mailMessage.getBody();

		Assert.assertTrue(
			mailMessageBody.contains(
				"Follow the link below to set up your account"));
	}

	@Test
	public void testInviteUserWithCustomEmailTemplates() throws Exception {
		String invitationEmailBody = "Custom email body";
		String invitationEmailSubject = "Custom email subject";

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AccountEntryEmailConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"invitationEmailBody", invitationEmailBody
						).put(
							"invitationEmailSubject", invitationEmailSubject
						).build())) {

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			serviceContext.setRequest(new MockHttpServletRequest());

			_accountEntryUserRelLocalService.inviteUser(
				_accountEntry.getAccountEntryId(), null, "user@test.com",
				TestPropsValues.getUser(), serviceContext);

			MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

			Assert.assertEquals(
				invitationEmailSubject,
				mailMessage.getFirstHeaderValue("Subject"));

			String mailMessageBody = mailMessage.getBody();

			Assert.assertTrue(mailMessageBody.contains(invitationEmailBody));
		}
	}

	@Test
	public void testInviteUserWithCustomSenderEmailAddress() throws Exception {
		String invitationEmailSenderEmailAddress = "custom@liferay.com";

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AccountEntryEmailConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"invitationEmailSenderEmailAddress",
							invitationEmailSenderEmailAddress
						).build())) {

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			serviceContext.setRequest(new MockHttpServletRequest());

			_accountEntryUserRelLocalService.inviteUser(
				_accountEntry.getAccountEntryId(), null, "user@test.com",
				TestPropsValues.getUser(), serviceContext);

			MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

			String from = mailMessage.getFirstHeaderValue("From");

			Assert.assertEquals(
				from,
				StringBundler.concat(
					"Test Test <", invitationEmailSenderEmailAddress, ">"));
		}
	}

	@Test
	public void testInviteUserWithCustomSenderName() throws Exception {
		String invitationEmailSenderName = "Custom sender name";

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AccountEntryEmailConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"invitationEmailSenderName",
							invitationEmailSenderName
						).build())) {

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			serviceContext.setRequest(new MockHttpServletRequest());

			_accountEntryUserRelLocalService.inviteUser(
				_accountEntry.getAccountEntryId(), null, "user@test.com",
				TestPropsValues.getUser(), serviceContext);

			MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

			String from = mailMessage.getFirstHeaderValue("From");

			Assert.assertEquals(
				from, invitationEmailSenderName + " <test@liferay.com>");
		}
	}

	@Test
	public void testInviteUserWithDefaultSender() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setRequest(new MockHttpServletRequest());

		_accountEntryUserRelLocalService.inviteUser(
			_accountEntry.getAccountEntryId(), null, "user@test.com",
			TestPropsValues.getUser(), serviceContext);

		MailMessage mailMessage = MailServiceTestUtil.getLastMailMessage();

		String from = mailMessage.getFirstHeaderValue("From");

		Assert.assertEquals("Test Test <test@liferay.com>", from);
	}

	@Test
	public void testSetPersonTypeAccountEntryUser() throws Exception {
		AccountEntry personTypeAccountEntry =
			AccountEntryTestUtil.addAccountEntry(AccountEntryArgs.TYPE_PERSON);
		User user1 = UserTestUtil.addUser();

		_testSetPersonTypeAccountEntryUser(
			new long[] {user1.getUserId()},
			personTypeAccountEntry.getAccountEntryId(), user1.getUserId());

		User user2 = UserTestUtil.addUser();

		_testSetPersonTypeAccountEntryUser(
			new long[] {user2.getUserId()},
			personTypeAccountEntry.getAccountEntryId(), user2.getUserId());

		_testSetPersonTypeAccountEntryUser(
			new long[0], personTypeAccountEntry.getAccountEntryId(),
			UserConstants.USER_ID_DEFAULT);
	}

	@Test(expected = AccountEntryTypeException.class)
	public void testSetPersonTypeAccountEntryUserThrowsAccountEntryTypeException()
		throws Exception {

		_accountEntryUserRelLocalService.setPersonTypeAccountEntryUser(
			_accountEntry.getAccountEntryId(), _user.getUserId());
	}

	@Test
	public void testUpdateAccountEntryUserRels() throws Exception {
		long userId = _user.getUserId();

		// Add account entries for a user

		long[] addAccountEntryIds1 = _addAccountEntries(3);
		long[] deleteAccountEntryIds1 = _addAccountEntries(2);

		_accountEntryUserRelLocalService.updateAccountEntryUserRels(
			addAccountEntryIds1, deleteAccountEntryIds1, userId);

		List<AccountEntryUserRel> accountEntryUserRels =
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountUserId(userId);

		Assert.assertEquals(
			accountEntryUserRels.toString(), 3, accountEntryUserRels.size());

		// Add and delete account entries for a user

		long[] addAccountEntryIds2 = _addAccountEntries(5);

		_accountEntryUserRelLocalService.updateAccountEntryUserRels(
			addAccountEntryIds2, new long[] {addAccountEntryIds1[0]}, userId);

		List<Long> expectedAccountEntryIds = new ArrayList<>(
			ListUtil.fromArray(addAccountEntryIds2));

		expectedAccountEntryIds.add(addAccountEntryIds1[1]);
		expectedAccountEntryIds.add(addAccountEntryIds1[2]);

		accountEntryUserRels =
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountUserId(userId);

		Assert.assertEquals(
			accountEntryUserRels.toString(), expectedAccountEntryIds.size(),
			accountEntryUserRels.size());

		for (AccountEntryUserRel accountEntryUserRel : accountEntryUserRels) {
			Assert.assertTrue(
				expectedAccountEntryIds.contains(
					accountEntryUserRel.getAccountEntryId()));
		}

		// Delete all account entries for a user

		long[] addAccountEntryIds3 = new long[0];
		long[] deleteAccountEntryIds3 = ArrayUtil.toLongArray(
			expectedAccountEntryIds);

		_accountEntryUserRelLocalService.updateAccountEntryUserRels(
			addAccountEntryIds3, deleteAccountEntryIds3, userId);

		accountEntryUserRels =
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountUserId(userId);

		Assert.assertTrue(accountEntryUserRels.isEmpty());
	}

	@Test(expected = DuplicateAccountEntryIdException.class)
	public void testUpdateAccountEntryUserRelsThrowsDuplicateAccountEntryIdException()
		throws Exception {

		long[] accountEntryIds = _addAccountEntries(3);

		_accountEntryUserRelLocalService.updateAccountEntryUserRels(
			accountEntryIds, accountEntryIds, _user.getUserId());
	}

	private long[] _addAccountEntries(int count) throws Exception {
		return ListUtil.toLongArray(
			AccountEntryTestUtil.addAccountEntries(count),
			AccountEntry.ACCOUNT_ENTRY_ID_ACCESSOR);
	}

	private AccountEntryUserRel _addAccountEntryUserRel(long accountEntryId)
		throws Exception {

		return _accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntryId, TestPropsValues.getUserId(), _userInfo.screenName,
			_userInfo.emailAddress, _userInfo.locale, _userInfo.firstName,
			_userInfo.middleName, _userInfo.lastName, _userInfo.prefixId,
			_userInfo.suffixId, null, null);
	}

	private AccountEntryUserRel _addPersonTypeAccountEntryUserRel(
			long accountEntryId)
		throws Exception {

		UserInfo userInfo = new UserInfo();

		return _accountEntryUserRelLocalService.
			addPersonTypeAccountEntryUserRel(
				accountEntryId, TestPropsValues.getUserId(),
				userInfo.screenName, userInfo.emailAddress, userInfo.locale,
				userInfo.firstName, userInfo.middleName, userInfo.lastName,
				userInfo.prefixId, userInfo.suffixId, null, null);
	}

	private void _assertPersonTypeAccountEntryUser(
			long[] expectedUserIds, long accountEntryId)
		throws Exception {

		long[] actualUserIds = ListUtil.toLongArray(
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountEntryId(accountEntryId),
			AccountEntryUserRelModel::getAccountUserId);

		Assert.assertEquals(
			Arrays.toString(actualUserIds), expectedUserIds.length,
			actualUserIds.length);

		for (int i = 0; i < expectedUserIds.length; i++) {
			Assert.assertEquals(expectedUserIds[i], actualUserIds[i]);
		}
	}

	private void _testAddAccountEntryUserRel2(
			long accountEntryId, long expectedCompanyId)
		throws Exception {

		AccountEntryUserRel accountEntryUserRel = _addAccountEntryUserRel(
			accountEntryId);

		User user = _userLocalService.fetchUser(
			accountEntryUserRel.getAccountUserId());

		Assert.assertNotNull(user);
		Assert.assertEquals(expectedCompanyId, user.getCompanyId());
		Assert.assertEquals(
			accountEntryUserRel.getAccountUserId(), user.getUserId());

		BaseModelSearchResult<User> baseModelSearchResult =
			_accountUserRetriever.searchAccountUsers(
				new long[] {accountEntryId}, user.getScreenName(), null,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, "screenName", false);

		List<User> users = baseModelSearchResult.getBaseModels();

		Assert.assertEquals(users.toString(), 1, users.size());
		Assert.assertEquals(users.get(0), user);
	}

	private void _testDeleteAccountEntryUserRelsByAccountEntryId(
			int expectedCount)
		throws Exception {

		List<AccountEntryUserRel> accountEntryUserRels =
			_accountEntryUserRelLocalService.
				getAccountEntryUserRelsByAccountEntryId(
					_accountEntry.getAccountEntryId());

		Assert.assertEquals(
			accountEntryUserRels.toString(), expectedCount,
			accountEntryUserRels.size());
	}

	private void _testSetPersonTypeAccountEntryUser(
			long[] expectedUserIds, long accountEntryId, long userId)
		throws Exception {

		_accountEntryUserRelLocalService.setPersonTypeAccountEntryUser(
			accountEntryId, userId);

		_assertPersonTypeAccountEntryUser(expectedUserIds, accountEntryId);
	}

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private AccountUserRetriever _accountUserRetriever;

	@DeleteAfterTestRun
	private User _user;

	private final UserInfo _userInfo = new UserInfo();

	@Inject
	private UserLocalService _userLocalService;

	private class UserInfo {

		public String emailAddress =
			RandomTestUtil.randomString() + "@liferay.com";
		public String firstName = RandomTestUtil.randomString();
		public String lastName = RandomTestUtil.randomString();
		public Locale locale = LocaleThreadLocal.getDefaultLocale();
		public String middleName = RandomTestUtil.randomString();
		public long prefixId = 0;
		public String screenName = StringUtil.toLowerCase(
			RandomTestUtil.randomString());
		public long suffixId = 0;

	}

}