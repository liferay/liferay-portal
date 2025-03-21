/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.ldap.LDAPSettingsUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ORGANIZATIONS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/update_password"
	},
	service = MVCActionCommand.class
)
public class UpdatePasswordMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			User user = _portal.getSelectedUser(actionRequest);

			UserPermissionUtil.check(
				themeDisplay.getPermissionChecker(), user.getUserId(),
				ActionKeys.UPDATE);

			String newPassword1 = actionRequest.getParameter("password1");
			String newPassword2 = actionRequest.getParameter("password2");

			boolean passwordReset = ParamUtil.getBoolean(
				actionRequest, "passwordReset");

			PasswordPolicy passwordPolicy = user.getPasswordPolicy();

			boolean ldapPasswordPolicyEnabled =
				LDAPSettingsUtil.isPasswordPolicyEnabled(user.getCompanyId());

			if ((user.getLastLoginDate() == null) &&
				(((passwordPolicy == null) && !ldapPasswordPolicyEnabled) ||
				 ((passwordPolicy != null) && passwordPolicy.isChangeable() &&
				  passwordPolicy.isChangeRequired()))) {

				passwordReset = true;
			}

			String reminderQueryQuestion = BeanParamUtil.getString(
				user, actionRequest, "reminderQueryQuestion");

			if (reminderQueryQuestion.equals(UsersAdminUtil.CUSTOM_QUESTION)) {
				reminderQueryQuestion = BeanParamUtil.getStringSilent(
					user, actionRequest, "reminderQueryCustomQuestion");
			}

			String reminderQueryAnswer = BeanParamUtil.getString(
				user, actionRequest, "reminderQueryAnswer");

			boolean passwordModified = false;

			if (Validator.isNotNull(newPassword1) ||
				Validator.isNotNull(newPassword2)) {

				user = _userLocalService.updatePassword(
					user.getUserId(), newPassword1, newPassword2,
					passwordReset);

				passwordModified = true;
			}

			user = _userLocalService.updatePasswordReset(
				user.getUserId(), passwordReset);

			if (Validator.isNotNull(reminderQueryQuestion) &&
				Validator.isNotNull(reminderQueryAnswer) &&
				!reminderQueryAnswer.equals(Portal.TEMP_OBFUSCATION_VALUE)) {

				user = _userLocalService.updateReminderQuery(
					user.getUserId(), reminderQueryQuestion,
					reminderQueryAnswer);
			}

			if ((user.getUserId() == themeDisplay.getUserId()) &&
				passwordModified) {

				String login = null;

				Company company = themeDisplay.getCompany();

				String authType = company.getAuthType();

				if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
					login = user.getEmailAddress();
				}
				else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
					login = user.getScreenName();
				}
				else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
					login = String.valueOf(user.getUserId());
				}

				HttpServletRequest httpServletRequest =
					_portal.getOriginalServletRequest(
						_portal.getHttpServletRequest(actionRequest));

				if (httpServletRequest != null) {
					HttpSession httpSession = httpServletRequest.getSession();

					Date passwordModifiedDate = user.getPasswordModifiedDate();

					httpSession.setAttribute(
						WebKeys.USER_PASSWORD_MODIFIED_TIME,
						passwordModifiedDate.getTime());
				}

				AuthenticatedSessionManagerUtil.login(
					httpServletRequest,
					_portal.getHttpServletResponse(actionResponse), login,
					newPassword1, false, null);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchUserException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof UserPasswordException) {
				SessionErrors.add(
					actionRequest, exception.getClass(), exception);

				String redirect = _portal.escapeRedirect(
					ParamUtil.getString(actionRequest, "redirect"));

				if (Validator.isNotNull(redirect)) {
					sendRedirect(actionRequest, actionResponse, redirect);
				}
			}
			else {
				throw exception;
			}
		}
	}

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}