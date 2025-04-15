/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.portlet.action;

import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.multi.factor.authentication.web.internal.constants.MFAPortletKeys;
import com.liferay.multi.factor.authentication.web.internal.constants.MFAWebKeys;
import com.liferay.multi.factor.authentication.web.internal.policy.MFAPolicy;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.CookieNotSupportedException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PasswordExpiredException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserIdException;
import com.liferay.portal.kernel.exception.UserLockoutException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Accessor;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.filter.ActionRequestWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.Key;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 * @author Marta Medio
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LoginPortletKeys.FAST_LOGIN,
		"jakarta.portlet.name=" + LoginPortletKeys.LOGIN,
		"mvc.command.name=/login/login", "service.ranking:Integer=1"
	},
	service = MVCActionCommand.class
)
public class LoginMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long companyId = _portal.getCompanyId(actionRequest);

		if (!_mfaPolicy.isMFAEnabled(companyId)) {
			_loginMVCActionCommand.processAction(actionRequest, actionResponse);

			return;
		}

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		if (AuthenticatedSessionManagerUtil.isPasswordParameterInQueryString(
				httpServletRequest)) {

			_postProcessAuthFailure(actionRequest, actionResponse);

			hideDefaultErrorMessage(actionRequest);

			return;
		}

		String state = ParamUtil.getString(actionRequest, "state");

		if (!Validator.isBlank(state)) {
			actionRequest = _getActionRequest(actionRequest, state);
		}

		String login = ParamUtil.getString(actionRequest, "login");
		String password = ParamUtil.getString(actionRequest, "password");

		if (!Validator.isBlank(login) && !Validator.isBlank(password)) {
			try {
				long userId =
					AuthenticatedSessionManagerUtil.getAuthenticatedUserId(
						httpServletRequest, login, password, null);

				if (_mfaPolicy.isSatisfied(
						companyId, httpServletRequest, userId)) {

					_loginMVCActionCommand.processAction(
						actionRequest, actionResponse);

					return;
				}

				if (userId > 0) {
					_redirectToVerify(actionRequest, userId);
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
				}
				else if (exception instanceof CompanyMaxUsersException ||
						 exception instanceof CookieNotSupportedException ||
						 exception instanceof NoSuchUserException ||
						 exception instanceof PasswordExpiredException ||
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
	}

	private ActionRequest _getActionRequest(
			ActionRequest actionRequest, String state)
		throws Exception {

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		HttpSession httpSession = httpServletRequest.getSession();

		String mfaWebDigest = (String)httpSession.getAttribute(
			MFAWebKeys.MFA_WEB_DIGEST);

		if (!StringUtil.equals(DigesterUtil.digest(state), mfaWebDigest)) {
			throw new PrincipalException("User sent unverified state");
		}

		Key mfaWebKey = _encryptor.deserializeKey(
			(String)httpSession.getAttribute(MFAWebKeys.MFA_WEB_KEY));

		Map<String, Object> stateMap = _jsonFactory.looseDeserialize(
			_encryptor.decrypt(mfaWebKey, state), Map.class);

		Map<String, Object> requestParameters =
			(Map<String, Object>)stateMap.get("requestParameters");

		for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
			if (entry.getValue() instanceof List) {
				entry.setValue(
					ListUtil.toArray(
						(List<?>)entry.getValue(), _STRING_ACCESSOR));
			}
		}

		return new ActionRequestWrapper(actionRequest) {

			@Override
			public String getParameter(String name) {
				return MapUtil.getString(requestParameters, name, null);
			}

			@Override
			public Map<String, String[]> getParameterMap() {
				return new HashMap(requestParameters);
			}

			@Override
			public Enumeration<String> getParameterNames() {
				return Collections.enumeration(requestParameters.keySet());
			}

			@Override
			public String[] getParameterValues(String name) {
				return (String[])requestParameters.get(name);
			}

		};
	}

	private void _postProcessAuthFailure(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(actionRequest);

		String portletName = liferayPortletRequest.getPortletName();

		Layout layout = (Layout)actionRequest.getAttribute(WebKeys.LAYOUT);

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

		if (portletName.equals(LoginPortletKeys.LOGIN)) {
			portletURL.setWindowState(WindowState.MAXIMIZED);
		}
		else {
			portletURL.setWindowState(actionRequest.getWindowState());
		}

		actionResponse.sendRedirect(portletURL.toString());
	}

	private void _redirectToVerify(ActionRequest actionRequest, long userId)
		throws Exception {

		Key key = _encryptor.generateKey();

		String encryptedStateMapJSON = _encryptor.encrypt(
			key,
			_jsonFactory.looseSerializeDeep(
				HashMapBuilder.<String, Object>put(
					"requestParameters",
					() -> HashMapBuilder.putAll(
						actionRequest.getParameterMap()
					).remove(
						"redirect"
					).build()
				).build()));

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		httpServletRequest = _portal.getOriginalServletRequest(
			httpServletRequest);

		long plid = 0;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			plid = themeDisplay.getPlid();
		}

		LiferayPortletURL liferayPortletURL = _portletURLFactory.create(
			httpServletRequest, MFAPortletKeys.MFA_VERIFY, plid,
			PortletRequest.RENDER_PHASE);

		liferayPortletURL.setParameter(
			"saveLastPath", Boolean.FALSE.toString());
		liferayPortletURL.setParameter(
			"mvcRenderCommandName", "/mfa_verify/view");
		liferayPortletURL.setParameter(
			"redirect", ParamUtil.getString(actionRequest, "redirect"));
		liferayPortletURL.setParameter("state", encryptedStateMapJSON);

		String portletId = ParamUtil.getString(httpServletRequest, "p_p_id");

		if (LoginPortletKeys.FAST_LOGIN.equals(portletId)) {
			liferayPortletURL.setWindowState(actionRequest.getWindowState());
		}
		else {
			liferayPortletURL.setWindowState(WindowState.MAXIMIZED);
		}

		actionRequest.setAttribute(
			WebKeys.REDIRECT, liferayPortletURL.toString());

		HttpSession httpSession = httpServletRequest.getSession();

		httpSession.setAttribute(MFAWebKeys.MFA_USER_ID, userId);
		httpSession.setAttribute(
			MFAWebKeys.MFA_WEB_DIGEST,
			DigesterUtil.digest(encryptedStateMapJSON));
		httpSession.setAttribute(
			MFAWebKeys.MFA_WEB_KEY, _encryptor.serializeKey(key));
	}

	private static final Accessor<Object, String> _STRING_ACCESSOR =
		new Accessor<Object, String>() {

			@Override
			public String get(Object object) {
				return String.valueOf(object);
			}

			@Override
			public Class<String> getAttributeClass() {
				return String.class;
			}

			@Override
			public Class<Object> getTypeClass() {
				return Object.class;
			}

		};

	private static final Log _log = LogFactoryUtil.getLog(
		LoginMVCActionCommand.class);

	@Reference
	private Encryptor _encryptor;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(component.name=com.liferay.login.web.internal.portlet.action.LoginMVCActionCommand)"
	)
	private MVCActionCommand _loginMVCActionCommand;

	@Reference
	private MFAPolicy _mfaPolicy;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

}