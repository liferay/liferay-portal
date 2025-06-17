/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.kernel.service.PasswordPolicyRelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.PrefsPropsTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.ldap.test.util.configuration.LDAPAuthConfigurationProviderTemporarySwapper;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.passwordpoliciesadmin.util.test.PasswordPolicyTestUtil;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alvaro Saugar
 * @author Olivér Kecskeméty
 */
@RunWith(Arquillian.class)
public class ForgotPasswordMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testLDAPPasswordPolicyPreventsLDAPUserPasswordReset()
		throws Exception {

		_createUser(true, false);

		try (LDAPAuthConfigurationProviderTemporarySwapper
				ldapAuthConfigurationProviderTemporarySwapper =
					new LDAPAuthConfigurationProviderTemporarySwapper(
						_user.getCompanyId(), true)) {

			List<Ticket> tickets = _processAction();

			Assert.assertTrue(tickets.isEmpty());
		}
	}

	@Test
	public void testLDAPUserWithoutLDAPPasswordPolicyCanResetPassword()
		throws Exception {

		_createUser(true, false);

		try (LDAPAuthConfigurationProviderTemporarySwapper
				ldapAuthConfigurationProviderTemporarySwapper =
					new LDAPAuthConfigurationProviderTemporarySwapper(
						_user.getCompanyId(), false)) {

			List<Ticket> tickets = _processAction();

			Assert.assertEquals(tickets.toString(), 1, tickets.size());
		}
	}

	@Test
	public void testPortalUserWithLDAPPasswordPolicyCanResetPassword()
		throws Exception {

		_createUser(false, false);

		try (LDAPAuthConfigurationProviderTemporarySwapper
				ldapAuthConfigurationProviderTemporarySwapper =
					new LDAPAuthConfigurationProviderTemporarySwapper(
						_user.getCompanyId(), true)) {

			List<Ticket> tickets = _processAction();

			Assert.assertEquals(tickets.toString(), 1, tickets.size());
		}
	}

	@Test
	public void testSendPasswordReminderToLockedOutUser() throws Exception {
		_createUser(false, true);

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.captcha.configuration.CaptchaConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"sendPasswordCaptchaEnabled", false
					).build());
			SafeCloseable safeCloseable =
				PrefsPropsTestUtil.swapWithSafeCloseable(
					_user.getCompanyId(),
					PropsKeys.USERS_REMINDER_QUERIES_ENABLED,
					Boolean.FALSE.toString())) {

			List<Ticket> tickets1 = _ticketLocalService.getTickets(
				_user.getCompanyId(), User.class.getName(), _user.getUserId());

			_mvcActionCommand.processAction(
				_getMockLiferayPortletActionRequest(),
				new MockLiferayPortletActionResponse());

			List<Ticket> tickets2 = _ticketLocalService.getTickets(
				_user.getCompanyId(), User.class.getName(), _user.getUserId());

			Assert.assertTrue((tickets1.size() + 1) == tickets2.size());
		}
	}

	private void _createUser(boolean ldapUser, boolean lockout)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute("ldapServerId", ldapUser ? 1 : -1);

		_user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			StringPool.BLANK,
			RandomTestUtil.randomString() + RandomTestUtil.nextLong() +
				"@liferay.com",
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			new long[] {TestPropsValues.getGroupId()}, serviceContext);

		_user.setLockout(lockout);
		_user.setLockoutDate(new Date());

		_user = _userLocalService.updateUser(_user);

		_passwordPolicyRelLocalService.deletePasswordPolicyRel(
			User.class.getName(), _user.getUserId());

		serviceContext = new ServiceContext();

		serviceContext.setUserId(_user.getUserId());

		_testPasswordPolicy = PasswordPolicyTestUtil.addPasswordPolicy(
			serviceContext);

		_testPasswordPolicy.setChangeable(true);
		_testPasswordPolicy.setLockout(lockout);
		_testPasswordPolicy.setLockoutDuration(0);
		_testPasswordPolicy.setResetTicketMaxAge(10);

		_testPasswordPolicy = _passwordPolicyLocalService.updatePasswordPolicy(
			_testPasswordPolicy);

		_userLocalService.addPasswordPolicyUsers(
			_testPasswordPolicy.getPasswordPolicyId(),
			new long[] {_user.getUserId()});
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"login", _user.getEmailAddress());
		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, null);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.fetchCompany(
				TestPropsValues.getCompanyId()));
		themeDisplay.setUser(_user);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	private List<Ticket> _processAction() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.captcha.configuration.CaptchaConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"sendPasswordCaptchaEnabled", false
					).build());
			SafeCloseable safeCloseable =
				PrefsPropsTestUtil.swapWithSafeCloseable(
					_user.getCompanyId(),
					PropsKeys.USERS_REMINDER_QUERIES_ENABLED,
					Boolean.FALSE.toString())) {

			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				_getMockLiferayPortletActionRequest();

			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());

			Assert.assertNotNull(
				SessionMessages.get(
					_portal.getHttpServletRequest(
						mockLiferayPortletActionRequest),
					"forgotPasswordSent"));

			return _ticketLocalService.getTickets(
				_user.getCompanyId(), User.class.getName(), _user.getUserId(),
				TicketConstants.TYPE_PASSWORD);
		}
	}

	@DeleteAfterTestRun
	private static User _user;

	@Inject(
		filter = "mvc.command.name=/login/forgot_password",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private PasswordPolicyLocalService _passwordPolicyLocalService;

	@Inject
	private PasswordPolicyRelLocalService _passwordPolicyRelLocalService;

	@Inject
	private Portal _portal;

	@DeleteAfterTestRun
	private PasswordPolicy _testPasswordPolicy;

	@Inject
	private TicketLocalService _ticketLocalService;

	@Inject
	private UserLocalService _userLocalService;

}