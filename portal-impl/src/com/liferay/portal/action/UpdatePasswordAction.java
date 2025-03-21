/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.TicketLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.DefaultAdminUtil;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.security.pwd.PwdToolkitUtilThreadLocal;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Date;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Mika Koivisto
 */
public class UpdatePasswordAction implements Action {

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		Ticket ticket = getTicket(httpServletRequest);

		if ((ticket != null) &&
			StringUtil.equals(
				httpServletRequest.getMethod(), HttpMethods.GET)) {

			resendAsPost(httpServletRequest, httpServletResponse);

			return null;
		}

		httpServletRequest.setAttribute(WebKeys.TICKET, ticket);

		String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);

		if (Validator.isNull(cmd)) {
			if (ticket != null) {
				User user = UserLocalServiceUtil.getUser(ticket.getClassPK());

				UserLocalServiceUtil.updatePasswordReset(
					user.getUserId(), true);
			}

			User user = PortalUtil.getUser(httpServletRequest);

			if ((user != null) && _isUserDefaultAdmin(user)) {
				String reminderQueryAnswer = user.getReminderQueryAnswer();

				if (Validator.isNotNull(reminderQueryAnswer) &&
					reminderQueryAnswer.equals(
						WorkflowConstants.LABEL_PENDING)) {

					httpServletRequest.setAttribute(
						WebKeys.TITLE_SET_PASSWORD, "set-password");
				}
			}

			return actionMapping.getActionForward("portal.update_password");
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			updatePassword(
				httpServletRequest, httpServletResponse, themeDisplay, ticket);

			String redirect = ParamUtil.getString(
				httpServletRequest, WebKeys.REFERER);

			if (Validator.isNotNull(redirect)) {
				redirect = PortalUtil.escapeRedirect(redirect);
			}

			if (Validator.isNull(redirect)) {
				redirect = themeDisplay.getPathMain();
			}

			httpServletResponse.sendRedirect(redirect);

			return null;
		}
		catch (Exception exception) {
			if (exception instanceof UserPasswordException) {
				SessionErrors.add(
					httpServletRequest, exception.getClass(), exception);

				return actionMapping.getActionForward("portal.update_password");
			}
			else if (exception instanceof NoSuchUserException ||
					 exception instanceof PrincipalException) {

				SessionErrors.add(httpServletRequest, exception.getClass());

				return actionMapping.getActionForward("portal.error");
			}

			PortalUtil.sendError(
				exception, httpServletRequest, httpServletResponse);

			return null;
		}
	}

	protected Ticket getTicket(HttpServletRequest httpServletRequest)
		throws PortalException {

		String ticketId = ParamUtil.getString(httpServletRequest, "ticketId");

		String ticketKey = ParamUtil.getString(httpServletRequest, "ticketKey");

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

	protected boolean isValidatePassword(
		HttpServletRequest httpServletRequest) {

		HttpSession httpSession = httpServletRequest.getSession();

		Boolean setupWizardPasswordUpdated = (Boolean)httpSession.getAttribute(
			WebKeys.SETUP_WIZARD_PASSWORD_UPDATED);

		if ((setupWizardPasswordUpdated != null) &&
			setupWizardPasswordUpdated) {

			return false;
		}

		return true;
	}

	protected void resendAsPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletResponse.setHeader(
			"Cache-Control", "no-cache, no-store, must-revalidate");
		httpServletResponse.setHeader("Expires", "0");
		httpServletResponse.setHeader("Pragma", "no-cache");

		PrintWriter printWriter = httpServletResponse.getWriter();

		Map<String, String[]> parameterMap =
			httpServletRequest.getParameterMap();

		StringBundler sb = new StringBundler(8 + (parameterMap.size() * 5));

		sb.append("<html><body onload=\"document.fm.submit();\">");
		sb.append("<form action=\"");
		sb.append(PortalUtil.getPortalURL(httpServletRequest));
		sb.append(PortalUtil.getPathContext());
		sb.append("/c/portal/update_password\" method=\"post\" name=\"fm\">");

		for (String name : parameterMap.keySet()) {
			String value = ParamUtil.getString(httpServletRequest, name);

			sb.append("<input name=\"");
			sb.append(HtmlUtil.escapeAttribute(name));
			sb.append("\" type=\"hidden\" value=\"");
			sb.append(HtmlUtil.escapeAttribute(value));
			sb.append("\"/>");
		}

		sb.append("<noscript>");
		sb.append("<input type=\"submit\" value=\"Please continue here...\"/>");
		sb.append("</noscript></form></body></html>");

		printWriter.write(sb.toString());

		printWriter.close();
	}

	protected void updatePassword(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, ThemeDisplay themeDisplay,
			Ticket ticket)
		throws Exception {

		AuthTokenUtil.checkCSRFToken(
			httpServletRequest, UpdatePasswordAction.class.getName());

		HttpSession httpSession = httpServletRequest.getSession();

		long userId = 0;

		if (ticket != null) {
			userId = ticket.getClassPK();
		}
		else {
			userId = themeDisplay.getUserId();
		}

		String password1 = httpServletRequest.getParameter("password1");
		String password2 = httpServletRequest.getParameter("password2");
		boolean passwordReset = false;

		boolean previousValidate = PwdToolkitUtilThreadLocal.isValidate();

		try {
			boolean currentValidate = isValidatePassword(httpServletRequest);

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
	}

	private boolean _isUserDefaultAdmin(User user) {
		User defaultAdminUser = DefaultAdminUtil.fetchDefaultAdmin(
			user.getCompanyId());

		if ((defaultAdminUser != null) &&
			(defaultAdminUser.getUserId() == user.getUserId())) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdatePasswordAction.class);

}