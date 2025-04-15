/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.portlet.action;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.login.web.internal.portlet.util.LoginUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.captcha.CaptchaConfigurationException;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.RequiredReminderQueryException;
import com.liferay.portal.kernel.exception.SendPasswordException;
import com.liferay.portal.kernel.exception.UserActiveException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserReminderQueryException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SortedArrayList;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletSession;

import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Tibor Kovacs
 * @author Peter Fellwock
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LoginPortletKeys.CREATE_ACCOUNT,
		"jakarta.portlet.name=" + LoginPortletKeys.FAST_LOGIN,
		"jakarta.portlet.name=" + LoginPortletKeys.FORGOT_PASSWORD,
		"jakarta.portlet.name=" + LoginPortletKeys.LOGIN,
		"mvc.command.name=/login/forgot_password"
	},
	service = MVCActionCommand.class
)
public class ForgotPasswordMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		if (!company.isSendPasswordResetLink()) {
			throw new PrincipalException.MustBeEnabled(
				company.getCompanyId(),
				PropsKeys.COMPANY_SECURITY_SEND_PASSWORD_RESET_LINK);
		}

		try {
			if (_prefsProps.getBoolean(
					company.getCompanyId(),
					PropsKeys.USERS_REMINDER_QUERIES_ENABLED,
					PropsValues.USERS_REMINDER_QUERIES_ENABLED)) {

				_checkReminderQueries(actionRequest, actionResponse);
			}
			else {
				_checkCaptcha(actionRequest);

				_sendPassword(actionRequest, actionResponse);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to send password: " + exception.getMessage(),
					exception);
			}

			if (exception instanceof CaptchaException ||
				exception instanceof UserEmailAddressException ||
				exception instanceof UserReminderQueryException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else if (exception instanceof NoSuchUserException ||
					 exception instanceof RequiredReminderQueryException ||
					 exception instanceof SendPasswordException ||
					 exception instanceof UserActiveException) {

				if (PropsValues.LOGIN_SECURE_FORGOT_PASSWORD) {
					SessionMessages.add(
						_portal.getHttpServletRequest(actionRequest),
						"forgotPasswordSent");

					sendRedirect(actionRequest, actionResponse, null);
				}
				else {
					SessionErrors.add(
						actionRequest, exception.getClass(), exception);
				}
			}
			else {
				_portal.sendError(exception, actionRequest, actionResponse);
			}
		}
	}

	protected CaptchaConfiguration getCaptchaConfiguration(
			ActionRequest actionRequest)
		throws CaptchaConfigurationException {

		try {
			return _configurationProvider.getCompanyConfiguration(
				CaptchaConfiguration.class,
				_portal.getCompanyId(actionRequest));
		}
		catch (Exception exception) {
			throw new CaptchaConfigurationException(exception);
		}
	}

	private void _checkCaptcha(ActionRequest actionRequest)
		throws CaptchaConfigurationException, CaptchaException {

		CaptchaConfiguration captchaConfiguration = getCaptchaConfiguration(
			actionRequest);

		if (captchaConfiguration.sendPasswordCaptchaEnabled()) {
			CaptchaUtil.check(actionRequest);
		}
	}

	private void _checkReminderQueries(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		PortletSession portletSession = actionRequest.getPortletSession();

		int step = ParamUtil.getInteger(actionRequest, "step");

		String login = (String)portletSession.getAttribute(
			WebKeys.FORGOT_PASSWORD_REMINDER_USER_EMAIL_ADDRESS);

		if (login == null) {
			step = 1;
		}

		if (step == 1) {
			_checkCaptcha(actionRequest);

			portletSession.removeAttribute(
				WebKeys.FORGOT_PASSWORD_REMINDER_ATTEMPTS);

			login = ParamUtil.getString(actionRequest, "login");

			portletSession.setAttribute(
				WebKeys.FORGOT_PASSWORD_REMINDER_USER_EMAIL_ADDRESS, login);
		}

		actionRequest.setAttribute(
			WebKeys.FORGOT_PASSWORD_REMINDER_USER, _getUser(actionRequest));

		if (step == 2) {
			Integer reminderAttempts = (Integer)portletSession.getAttribute(
				WebKeys.FORGOT_PASSWORD_REMINDER_ATTEMPTS);

			if (reminderAttempts == null) {
				reminderAttempts = 0;
			}
			else if (reminderAttempts > 2) {
				_checkCaptcha(actionRequest);
			}

			reminderAttempts++;

			portletSession.setAttribute(
				WebKeys.FORGOT_PASSWORD_REMINDER_ATTEMPTS, reminderAttempts);

			_sendPassword(actionRequest, actionResponse);
		}
	}

	private String _getReminderQueryQuestion(
		String login, Set<String> reminderQueryQuestions) {

		List<String> list = new SortedArrayList(reminderQueryQuestions);

		return list.get(Math.abs(login.hashCode()) % list.size());
	}

	private User _getUser(ActionRequest actionRequest) throws Exception {
		try {
			User user = null;

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			String authType = null;

			PortletPreferences portletPreferences =
				actionRequest.getPreferences();

			if (portletPreferences != null) {
				authType = portletPreferences.getValue("authType", null);
			}

			if (Validator.isNull(authType)) {
				Company company = themeDisplay.getCompany();

				authType = company.getAuthType();
			}

			PortletSession portletSession = actionRequest.getPortletSession();

			String login = (String)portletSession.getAttribute(
				WebKeys.FORGOT_PASSWORD_REMINDER_USER_EMAIL_ADDRESS);

			if (Validator.isNull(login)) {
				login = ParamUtil.getString(actionRequest, "login");
			}

			if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
				user = _userLocalService.getUserByEmailAddress(
					themeDisplay.getCompanyId(), login);
			}
			else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
				user = _userLocalService.getUserByScreenName(
					themeDisplay.getCompanyId(), login);
			}
			else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
				user = _userLocalService.getUserById(GetterUtil.getLong(login));
			}
			else {
				throw new NoSuchUserException("User does not exist");
			}

			if (!user.isActive()) {
				throw new UserActiveException(
					"Inactive user " + user.getUuid());
			}

			return user;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get user: " + exception.getMessage(), exception);
			}

			if (!PropsValues.LOGIN_SECURE_FORGOT_PASSWORD) {
				throw exception;
			}
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User guestUser = _userLocalService.getGuestUser(
			themeDisplay.getCompanyId());

		Set<String> reminderQueryQuestions =
			guestUser.getReminderQueryQuestions();

		if (!reminderQueryQuestions.isEmpty()) {
			guestUser.setReminderQueryQuestion(
				_getReminderQueryQuestion(
					ParamUtil.getString(actionRequest, "login"),
					reminderQueryQuestions));
		}
		else {
			guestUser.setReminderQueryQuestion(
				"what-is-your-library-card-number");
		}

		guestUser.setReminderQueryAnswer(guestUser.getReminderQueryQuestion());

		return guestUser;
	}

	private void _sendPassword(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		User user = _getUser(actionRequest);

		if (_prefsProps.getBoolean(
				user.getCompanyId(), PropsKeys.USERS_REMINDER_QUERIES_ENABLED,
				PropsValues.USERS_REMINDER_QUERIES_ENABLED)) {

			if (user.isGuestUser()) {
				throw new UserReminderQueryException(
					"Reminder query answer does not match answer");
			}

			if (_prefsProps.getBoolean(
					user.getCompanyId(),
					PropsKeys.USERS_REMINDER_QUERIES_REQUIRED,
					PropsValues.USERS_REMINDER_QUERIES_REQUIRED) &&
				!user.hasReminderQuery()) {

				throw new RequiredReminderQueryException(
					"No reminder query or answer is defined for user " +
						user.getUserId());
			}

			String answer = ParamUtil.getString(actionRequest, "answer");

			String reminderQueryAnswer = user.getReminderQueryAnswer();

			if (!reminderQueryAnswer.equals(answer)) {
				throw new UserReminderQueryException(
					"Reminder query answer does not match answer");
			}
		}

		if (user.isGuestUser()) {
			SessionMessages.add(
				_portal.getHttpServletRequest(actionRequest),
				"forgotPasswordSent");

			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		PortletPreferences portletPreferences = actionRequest.getPreferences();

		String languageId = _language.getLanguageId(actionRequest);

		String emailFromName = portletPreferences.getValue(
			"emailFromName", null);
		String emailFromAddress = portletPreferences.getValue(
			"emailFromAddress", null);
		String emailToAddress = user.getEmailAddress();

		String emailParam = "emailPasswordSent";

		PasswordPolicy passwordPolicy = user.getPasswordPolicy();

		if ((passwordPolicy == null) || !passwordPolicy.isChangeable()) {
			emailParam = "emailPasswordUnchangeable";
		}
		else if (company.isSendPasswordResetLink()) {
			emailParam = "emailPasswordReset";
		}

		String subject = portletPreferences.getValue(
			emailParam + "Subject_" + languageId, null);
		String body = portletPreferences.getValue(
			emailParam + "Body_" + languageId, null);

		LoginUtil.sendPassword(
			actionRequest, emailFromName, emailFromAddress, emailToAddress,
			subject, body);

		SessionMessages.add(
			_portal.getHttpServletRequest(actionRequest), "forgotPasswordSent");

		sendRedirect(actionRequest, actionResponse, null);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ForgotPasswordMVCActionCommand.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PrefsProps _prefsProps;

	@Reference
	private UserLocalService _userLocalService;

}