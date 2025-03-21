/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.portlet.action;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.captcha.CaptchaConfigurationException;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.EmailAddressException;
import com.liferay.portal.kernel.exception.GroupFriendlyURLException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 * @author Peter Fellwock
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LoginPortletKeys.FAST_LOGIN,
		"jakarta.portlet.name=" + LoginPortletKeys.LOGIN,
		"mvc.command.name=/login/create_anonymous_account"
	},
	service = MVCActionCommand.class
)
public class CreateAnonymousAccountMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void addSuccessMessage(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String portletId = (String)actionRequest.getAttribute(
			WebKeys.PORTLET_ID);

		if (!portletId.equals(LoginPortletKeys.FAST_LOGIN)) {
			super.addSuccessMessage(actionRequest, actionResponse);
		}
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		PortletConfig portletConfig = (PortletConfig)actionRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_CONFIG);

		String portletName = portletConfig.getPortletName();

		if (!portletName.equals(LoginPortletKeys.FAST_LOGIN)) {
			throw new PrincipalException("Unable to create anonymous account");
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (actionRequest.getRemoteUser() != null) {
			actionResponse.sendRedirect(themeDisplay.getPathMain());

			return;
		}

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		String emailAddress = ParamUtil.getString(
			actionRequest, "emailAddress");

		PortletURL portletURL = PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				actionRequest, LoginPortletKeys.FAST_LOGIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/login/login_redirect"
		).setParameter(
			"anonymousUser", true
		).setParameter(
			"emailAddress", emailAddress
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildPortletURL();

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			if (cmd.equals(Constants.ADD)) {
				_addAnonymousUser(actionRequest);

				sendRedirect(
					actionRequest, actionResponse, portletURL.toString());
			}
			else if (cmd.equals(Constants.UPDATE)) {
				Company company = themeDisplay.getCompany();

				if (!company.isStrangers()) {
					throw new PrincipalException.MustBeEnabled(
						company.getCompanyId(),
						PropsKeys.COMPANY_SECURITY_STRANGERS);
				}

				jsonObject = updateIncompleteUser(
					actionRequest, actionResponse);

				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse, jsonObject);
			}
		}
		catch (Exception exception) {
			if (cmd.equals(Constants.UPDATE)) {
				jsonObject.putException(exception);

				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse, jsonObject);
			}
			else if (exception instanceof
						UserEmailAddressException.MustNotBeDuplicate) {

				User user = _userLocalService.getUserByEmailAddress(
					themeDisplay.getCompanyId(), emailAddress);

				if (user.getStatus() != WorkflowConstants.STATUS_INCOMPLETE) {
					SessionErrors.add(actionRequest, exception.getClass());
				}
				else {
					sendRedirect(
						actionRequest, actionResponse, portletURL.toString());
				}
			}
			else if (exception instanceof CaptchaException ||
					 exception instanceof CompanyMaxUsersException ||
					 exception instanceof ContactNameException ||
					 exception instanceof EmailAddressException ||
					 exception instanceof GroupFriendlyURLException ||
					 exception instanceof UserEmailAddressException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);
			}
			else {
				_log.error("Unable to create anonymous account", exception);

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

	protected JSONObject updateIncompleteUser(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			User.class.getName(), actionRequest);

		boolean autoPassword = true;
		String password1 = null;
		String password2 = null;
		boolean autoScreenName = false;
		String screenName = null;
		String emailAddress = ParamUtil.getString(
			actionRequest, "emailAddress");
		long facebookId = 0;
		String openId = null;
		String firstName = null;
		String middleName = null;
		String lastName = null;
		long prefixListTypeId = 0;
		long suffixListTypeId = 0;
		boolean male = true;
		int birthdayMonth = 0;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = null;
		boolean updateUserInformation = false;
		boolean sendEmail = true;

		User user = _userService.updateIncompleteUser(
			themeDisplay.getCompanyId(), autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, facebookId, openId,
			themeDisplay.getLocale(), firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, jobTitle, updateUserInformation,
			sendEmail, serviceContext);

		return JSONUtil.put(
			"userStatus",
			() -> {
				if (user.getStatus() == WorkflowConstants.STATUS_APPROVED) {
					return "user_added";
				}

				return "user_pending";
			});
	}

	private void _addAnonymousUser(ActionRequest actionRequest)
		throws Exception {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		boolean autoPassword = true;
		String password1 = null;
		String password2 = null;
		boolean autoScreenName = true;
		String screenName = null;
		String emailAddress = ParamUtil.getString(
			actionRequest, "emailAddress");
		long facebookId = 0;
		String openId = StringPool.BLANK;
		String firstName = ParamUtil.getString(actionRequest, "firstName");
		String lastName = ParamUtil.getString(actionRequest, "lastName");
		long prefixListTypeId = 0;
		long suffixListTypeId = 0;
		boolean male = true;
		int birthdayMonth = 0;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = null;
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = false;

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			User.class.getName(), actionRequest);

		serviceContext.setAttribute("anonymousUser", Boolean.TRUE);

		CaptchaConfiguration captchaConfiguration = getCaptchaConfiguration(
			actionRequest);

		if (captchaConfiguration.createAccountCaptchaEnabled()) {
			CaptchaUtil.check(actionRequest);
		}

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		User user = _userService.addUser(
			themeDisplay.getCompanyId(), autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, facebookId, openId,
			themeDisplay.getLocale(), firstName, null, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds,
			roleIds, userGroupIds, sendEmail, serviceContext);

		_userLocalService.updateStatus(
			user, WorkflowConstants.STATUS_INCOMPLETE, new ServiceContext());

		// Session messages

		SessionMessages.add(
			httpServletRequest, "userAdded", user.getEmailAddress());
		SessionMessages.add(
			httpServletRequest, "userAddedPassword",
			user.getPasswordUnencrypted());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CreateAnonymousAccountMVCActionCommand.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserService _userService;

}