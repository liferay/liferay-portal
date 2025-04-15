/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.portlet.action;

import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.login.web.internal.portlet.util.LoginUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.CookieNotSupportedException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PasswordExpiredException;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.exception.UserActiveException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserIdException;
import com.liferay.portal.kernel.exception.UserLockoutException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Peter Fellwock
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LoginPortletKeys.CREATE_ACCOUNT,
		"jakarta.portlet.name=" + LoginPortletKeys.FAST_LOGIN,
		"jakarta.portlet.name=" + LoginPortletKeys.FORGOT_PASSWORD,
		"jakarta.portlet.name=" + LoginPortletKeys.LOGIN,
		"mvc.command.name=/login/login"
	},
	service = MVCActionCommand.class
)
public class LoginMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (PropsValues.AUTH_LOGIN_DISABLED) {
			actionResponse.sendRedirect(
				themeDisplay.getPathMain() +
					PropsValues.AUTH_LOGIN_DISABLED_PATH);

			return;
		}

		/*if (actionRequest.getRemoteUser() != null) {
			actionResponse.sendRedirect(themeDisplay.getPathMain());

			return;
		}*/

		try {
			login(themeDisplay, actionRequest, actionResponse);

			boolean doActionAfterLogin = ParamUtil.getBoolean(
				actionRequest, "doActionAfterLogin");

			if (doActionAfterLogin) {
				actionRequest.setAttribute(
					WebKeys.REDIRECT,
					PortletURLBuilder.createRenderURL(
						_portal.getLiferayPortletResponse(actionResponse)
					).setMVCRenderCommandName(
						"/login/login_redirect"
					).buildString());
			}
		}
		catch (Exception exception) {
			if (exception instanceof AuthException) {
				Throwable throwable = exception.getCause();

				if (throwable instanceof PasswordExpiredException ||
					throwable instanceof UserLockoutException) {

					SessionErrors.add(
						actionRequest, throwable.getClass(), throwable);
				}
				else {
					if (_log.isInfoEnabled()) {
						_log.info("Authentication failed");
					}

					SessionErrors.add(actionRequest, exception.getClass());
				}

				_postProcessAuthFailure(actionRequest, actionResponse);

				hideDefaultErrorMessage(actionRequest);

				return;
			}
			else if (exception instanceof
						UserLockoutException.PasswordPolicyLockout) {

				Company company = themeDisplay.getCompany();

				if (!company.isSendPasswordResetLink()) {
					User user = _getUser(actionRequest);

					PortletPreferences portletPreferences =
						actionRequest.getPreferences();

					String emailFromName = portletPreferences.getValue(
						"emailFromName", null);
					String emailFromAddress = portletPreferences.getValue(
						"emailFromAddress", null);

					String emailToAddress = user.getEmailAddress();

					LoginUtil.sendPasswordLockout(
						actionRequest, emailFromName, emailFromAddress,
						emailToAddress, null, null);
				}

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);

				_postProcessAuthFailure(actionRequest, actionResponse);

				hideDefaultErrorMessage(actionRequest);

				return;
			}

			if (exception instanceof CompanyMaxUsersException ||
				exception instanceof CookieNotSupportedException ||
				exception instanceof NoSuchUserException ||
				exception instanceof PasswordExpiredException ||
				exception instanceof PwdEncryptorException ||
				exception instanceof UserEmailAddressException ||
				exception instanceof UserIdException ||
				exception instanceof UserLockoutException ||
				exception instanceof UserPasswordException ||
				exception instanceof UserScreenNameException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);
			}
			else {
				_log.error(exception);

				_portal.sendError(exception, actionRequest, actionResponse);

				return;
			}

			_postProcessAuthFailure(actionRequest, actionResponse);

			hideDefaultErrorMessage(actionRequest);
		}
	}

	protected void login(
			ThemeDisplay themeDisplay, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		if (!themeDisplay.isSignedIn()) {
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			String login = ParamUtil.getString(actionRequest, "login");
			String password = actionRequest.getParameter("password");
			boolean rememberMe = ParamUtil.getBoolean(
				actionRequest, "rememberMe");

			PortletPreferences portletPreferences =
				PortletPreferencesFactoryUtil.getStrictPortletSetup(
					themeDisplay.getLayout(),
					_portal.getPortletId(actionRequest));

			String authType = portletPreferences.getValue("authType", null);

			AuthenticatedSessionManagerUtil.login(
				httpServletRequest, httpServletResponse, login, password,
				rememberMe, authType);
		}

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		String mainPath = themeDisplay.getPathMain();

		if (PropsValues.PORTAL_JAAS_ENABLE) {
			if (Validator.isNotNull(redirect)) {
				redirect = StringBundler.concat(
					mainPath, "/portal/protected?redirect=",
					URLCodec.encodeURL(redirect));
			}
			else {
				redirect = mainPath.concat("/portal/protected");
			}

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			httpServletResponse.sendRedirect(redirect);

			return;
		}

		if (Validator.isNotNull(redirect)) {
			if (!themeDisplay.isSignedIn()) {
				actionRequest.setAttribute(
					WebKeys.REDIRECT,
					HttpComponentsUtil.addParameter(
						_portal.getPathMain() + "/portal/login", "redirect",
						redirect));

				return;
			}

			redirect = _portal.escapeRedirect(redirect);

			if (Validator.isNotNull(redirect) &&
				!redirect.startsWith(Http.HTTP)) {

				redirect = _portal.getPortalURL(
					httpServletRequest
				).concat(
					redirect
				);
			}
		}

		if (Validator.isNotNull(redirect)) {
			actionResponse.sendRedirect(redirect);
		}
		else {
			boolean doActionAfterLogin = ParamUtil.getBoolean(
				actionRequest, "doActionAfterLogin");

			if (doActionAfterLogin) {
				return;
			}

			actionResponse.sendRedirect(mainPath);
		}
	}

	private User _getUser(ActionRequest actionRequest) throws Exception {
		User user = null;

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletPreferences portletPreferences = actionRequest.getPreferences();

		String authType = portletPreferences.getValue("authType", null);

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
			throw new UserActiveException("Inactive user " + user.getUuid());
		}

		return user;
	}

	private void _postProcessAuthFailure(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(actionRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout =
			_layoutUtilityPageEntryLayoutProvider.
				getDefaultLayoutUtilityPageEntryLayout(
					themeDisplay.getScopeGroupId(),
					LayoutUtilityPageEntryConstants.TYPE_LOGIN);

		if (layout == null) {
			layout = (Layout)actionRequest.getAttribute(WebKeys.LAYOUT);
		}

		PortletURL portletURL = PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				actionRequest, liferayPortletRequest.getPortlet(), layout,
				PortletRequest.RENDER_PHASE)
		).setRedirect(
			() -> {
				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				if (Validator.isNotNull(redirect)) {
					return redirect;
				}

				return null;
			}
		).setParameter(
			"saveLastPath", false
		).buildPortletURL();

		String login = ParamUtil.getString(actionRequest, "login");

		if (Validator.isNotNull(login)) {
			SessionErrors.add(actionRequest, "login", login);
		}

		String portletName = liferayPortletRequest.getPortletName();

		if (portletName.equals(LoginPortletKeys.LOGIN)) {
			if (layout.isTypeUtility()) {
				portletURL.setWindowState(WindowState.NORMAL);
			}
			else {
				portletURL.setWindowState(WindowState.MAXIMIZED);
			}
		}
		else {
			portletURL.setWindowState(actionRequest.getWindowState());
		}

		actionResponse.sendRedirect(portletURL.toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LoginMVCActionCommand.class);

	@Reference
	private LayoutUtilityPageEntryLayoutProvider
		_layoutUtilityPageEntryLayoutProvider;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}