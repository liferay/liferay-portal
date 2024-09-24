/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.Ticket;
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
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.PrefsPropsTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.passwordpoliciesadmin.util.test.PasswordPolicyTestUtil;

import java.util.Date;
import java.util.Dictionary;
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

		List<Ticket> tickets = _performForgotPasswordAction(true);

		Assert.assertTrue(
			"New ticket created for LDAP user during password reset while " +
				"LDAP password policy is enabled",
			tickets.isEmpty());
	}

	@Test
	public void testLDAPUserWithoutLDAPPasswordPolicyCanResetPassword()
		throws Exception {

		_createUser(true, false);

		List<Ticket> tickets = _performForgotPasswordAction(false);

		Assert.assertTrue(
			"No ticket created for LDAP user during password reset while " +
				"LDAP password policy is not enabled",
			tickets.size() == 1);
	}

	@Test
	public void testPortalUserWithLDAPPasswordPolicyCanResetPassword()
		throws Exception {

		_createUser(false, false);

		List<Ticket> tickets = _performForgotPasswordAction(true);

		Assert.assertTrue(
			"No ticket created for portal user during password reset while " +
				"LDAP password policy is enabled",
			tickets.size() == 1);
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

		_user = UserTestUtil.addUser();

		if (ldapUser) {
			_user.setLdapServerId(1);
		}

		_user.setLockout(lockout);
		_user.setLockoutDate(new Date());

		_user = _userLocalService.updateUser(_user);

		_passwordPolicyRelLocalService.deletePasswordPolicyRel(
			User.class.getName(), _user.getUserId());

		ServiceContext serviceContext = new ServiceContext();

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
			JavaConstants.JAVAX_PORTLET_CONFIG, null);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.fetchCompany(
				TestPropsValues.getCompanyId()));
		themeDisplay.setUser(_user);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	private List<Ticket> _performForgotPasswordAction(
			boolean passwordPolicyEnabled)
		throws Exception {

		Dictionary<String, Object> configurations =
			_ldapAuthConfigurationProvider.getConfigurationProperties(
				_user.getCompanyId());

		Object existingValue = configurations.put(
			"passwordPolicyEnabled", passwordPolicyEnabled);

		_ldapAuthConfigurationProvider.updateProperties(
			_user.getCompanyId(), configurations);

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

			Object message = SessionMessages.get(
				_portal.getHttpServletRequest(mockLiferayPortletActionRequest),
				"forgotPasswordSent");

			Assert.assertNotNull(message);

			return _ticketLocalService.getTickets(
				_user.getCompanyId(), User.class.getName(), _user.getUserId());
		}
		finally {
			if (existingValue != null) {
				configurations.put("passwordPolicyEnabled", existingValue);
			}
			else {
				configurations.remove("passwordPolicyEnabled");
			}

			_ldapAuthConfigurationProvider.updateProperties(
				_user.getCompanyId(), configurations);
		}
	}

	@DeleteAfterTestRun
	private static User _user;

	@Inject(
		filter = "factoryPid=com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration"
	)
	private ConfigurationProvider<LDAPAuthConfiguration>
		_ldapAuthConfigurationProvider;

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