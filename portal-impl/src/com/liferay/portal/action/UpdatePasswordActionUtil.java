/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.DefaultAdminUtil;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.security.pwd.PwdToolkitUtilThreadLocal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Alvaro Saugar
 */
public class UpdatePasswordActionUtil {

	public static String generateUpdatePasswordURL(
			HttpServletRequest httpServletRequest, User user)
		throws PwdEncryptorException {

		StringBundler sb = new StringBundler(8);

		sb.append(PortalUtil.getPortalURL(httpServletRequest));
		sb.append(PortalUtil.getPathContext());
		sb.append("/c/portal/update_password?p_l_id=");
		sb.append(LayoutConstants.DEFAULT_PLID);
		sb.append("&ticketId=");

		Ticket ticket = TicketLocalServiceUtil.addDistinctTicket(
			user.getCompanyId(), User.class.getName(), user.getUserId(),
			TicketConstants.TYPE_PASSWORD, null,
			new Date(
				System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10)),
			new ServiceContext());

		sb.append(ticket.getTicketId());

		sb.append("&ticketKey=");
		sb.append(ticket.getKey());

		ticket.setKey(PasswordEncryptorUtil.encrypt(ticket.getKey()));

		TicketLocalServiceUtil.updateTicket(ticket);

		return sb.toString();
	}

	public static Ticket getTicket(String ticketId, String ticketKey)
		throws PortalException {

		if (Validator.isNull(ticketId) || Validator.isNull(ticketKey)) {
			return null;
		}

		try {
			Ticket ticket = TicketLocalServiceUtil.fetchTicket(
				GetterUtil.getLong(ticketId));

			if ((ticket == null) ||
				(ticket.getType() != TicketConstants.TYPE_PASSWORD)) {

				return null;
			}

			String encryptedTicketKey = PasswordEncryptorUtil.encrypt(
				ticketKey, ticket.getKey());

			if (!ticket.isExpired() &&
				encryptedTicketKey.equals(ticket.getKey())) {

				return ticket;
			}

			TicketLocalServiceUtil.deleteTicket(ticket);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	public static void updatePassword(
			String csrfOrigin, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String password1,
			String password2, String redirect,
			UnsafeConsumer<String, Exception> redirectUnsafeConsumer,
			ThemeDisplay themeDisplay, Ticket ticket)
		throws Exception {

		AuthTokenUtil.checkCSRFToken(httpServletRequest, csrfOrigin);

		HttpSession httpSession = httpServletRequest.getSession();

		long userId = 0;

		if (ticket != null) {
			userId = ticket.getClassPK();
		}
		else {
			userId = themeDisplay.getUserId();
		}

		boolean passwordReset = false;

		boolean previousValidate = PwdToolkitUtilThreadLocal.isValidate();

		try {
			boolean currentValidate = _isValidatePassword(httpServletRequest);

			PwdToolkitUtilThreadLocal.setValidate(currentValidate);

			User user = UserLocalServiceUtil.updatePassword(
				userId, password1, password2, passwordReset);

			String reminderQueryAnswer = user.getReminderQueryAnswer();

			if (_isUserDefaultAdmin(user) &&
				reminderQueryAnswer.equals(WorkflowConstants.LABEL_PENDING) &&
				Validator.isNull(user.getReminderQueryQuestion())) {

				user.setReminderQueryAnswer(null);

				user = UserLocalServiceUtil.updateUser(user);
			}

			Date passwordModifiedDate = user.getPasswordModifiedDate();

			httpSession.setAttribute(
				WebKeys.USER_PASSWORD_MODIFIED_TIME,
				passwordModifiedDate.getTime());
		}
		finally {
			PwdToolkitUtilThreadLocal.setValidate(previousValidate);
		}

		User user = UserLocalServiceUtil.getUser(userId);

		Company company = CompanyLocalServiceUtil.getCompanyById(
			user.getCompanyId());

		if (ticket != null) {
			TicketLocalServiceUtil.deleteTickets(
				user.getCompanyId(), User.class.getName(), userId,
				ticket.getType());

			user = UserLocalServiceUtil.updateLastLogin(
				user, httpServletRequest.getRemoteAddr());

			UserLocalServiceUtil.updateLockout(user, false);

			UserLocalServiceUtil.updatePasswordReset(userId, false);

			if (company.isStrangersVerify()) {
				UserLocalServiceUtil.updateEmailAddressVerified(userId, true);
			}
		}

		if (GetterUtil.getBoolean(
				httpSession.getAttribute(WebKeys.MFA_ENABLED))) {

			return;
		}

		String login = null;

		String authType = company.getAuthType();

		if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
			login = user.getEmailAddress();
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			login = user.getScreenName();
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
			login = String.valueOf(userId);
		}

		AuthenticatedSessionManagerUtil.login(
			httpServletRequest, httpServletResponse, login, password1, false,
			null);

		if (Validator.isNotNull(redirect)) {
			redirect = PortalUtil.escapeRedirect(redirect);
		}

		if (Validator.isNull(redirect)) {
			redirect = themeDisplay.getPathMain();
		}

		redirectUnsafeConsumer.accept(redirect);
	}

	public static User verifyUser(
			HttpServletRequest httpServletRequest, Ticket ticket)
		throws PortalException {

		if (ticket != null) {
			User user = UserLocalServiceUtil.getUser(ticket.getClassPK());

			UserLocalServiceUtil.updatePasswordReset(user.getUserId(), true);
		}

		User user = PortalUtil.getUser(httpServletRequest);

		if ((user != null) && _isUserDefaultAdmin(user)) {
			String reminderQueryAnswer = user.getReminderQueryAnswer();

			if (Validator.isNotNull(reminderQueryAnswer) &&
				reminderQueryAnswer.equals(WorkflowConstants.LABEL_PENDING)) {

				httpServletRequest.setAttribute(
					WebKeys.TITLE_SET_PASSWORD, "set-password");
			}
		}

		return user;
	}

	private static boolean _isUserDefaultAdmin(User user) {
		User defaultAdminUser = DefaultAdminUtil.fetchDefaultAdmin(
			user.getCompanyId());

		if ((defaultAdminUser != null) &&
			(defaultAdminUser.getUserId() == user.getUserId())) {

			return true;
		}

		return false;
	}

	private static boolean _isValidatePassword(
		HttpServletRequest httpServletRequest) {

		HttpSession httpSession = httpServletRequest.getSession();

		Boolean setupWizardPasswordUpdated = GetterUtil.getBoolean(
			httpSession.getAttribute(WebKeys.SETUP_WIZARD_PASSWORD_UPDATED));

		if ((setupWizardPasswordUpdated != null) &&
			setupWizardPasswordUpdated) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdatePasswordActionUtil.class);

}