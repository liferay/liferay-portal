/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth;

import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.security.auth.AuthTokenWhitelistUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.SecurityPortletContainerWrapper;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

/**
 * @author Amos Fong
 */
public class SessionAuthToken implements AuthToken {

	@Override
	public void addCSRFToken(
		HttpServletRequest httpServletRequest,
		LiferayPortletURL liferayPortletURL) {

		if (!PropsValues.AUTH_TOKEN_CHECK_ENABLED) {
			return;
		}

		String lifecycle = liferayPortletURL.getLifecycle();

		if (!lifecycle.equals(PortletRequest.ACTION_PHASE) ||
			AuthTokenWhitelistUtil.isPortletURLCSRFWhitelisted(
				liferayPortletURL)) {

			return;
		}

		liferayPortletURL.setParameter("p_auth", getToken(httpServletRequest));
	}

	@Override
	public void addPortletInvocationToken(
		HttpServletRequest httpServletRequest,
		LiferayPortletURL liferayPortletURL) {

		if (!PropsValues.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED) {
			return;
		}

		long plid = liferayPortletURL.getPlid();
		String portletId = liferayPortletURL.getPortletId();

		String key = PortletPermissionUtil.getPrimaryKey(plid, portletId);

		Object sessionAuthenticationToken = _getSessionAuthenticationToken(
			httpServletRequest, key, false);

		if (sessionAuthenticationToken == _NULL_TOKEN) {
			return;
		}

		if (sessionAuthenticationToken instanceof String) {
			liferayPortletURL.setParameter(
				"p_p_auth", (String)sessionAuthenticationToken);

			return;
		}

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			PortalUtil.getCompanyId(httpServletRequest), portletId);

		if ((portlet == null) || !portlet.isAddDefaultResource() ||
			AuthTokenWhitelistUtil.isPortletURLPortletInvocationWhitelisted(
				liferayPortletURL)) {

			_setNullSessionAuthenticationToken(httpServletRequest, key);

			return;
		}

		boolean skipMerge = MergeLayoutPrototypesThreadLocal.isSkipMerge();

		try {
			MergeLayoutPrototypesThreadLocal.setSkipMerge(true);

			Layout layout = LayoutLocalServiceUtil.getLayout(plid);

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			if (layoutTypePortlet.hasPortletId(portletId)) {
				_setNullSessionAuthenticationToken(httpServletRequest, key);

				return;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
		finally {
			MergeLayoutPrototypesThreadLocal.setSkipMerge(skipMerge);
		}

		liferayPortletURL.setParameter(
			"p_p_auth", getToken(httpServletRequest, plid, portletId));
	}

	@Override
	public void checkCSRFToken(
			HttpServletRequest httpServletRequest, String origin)
		throws PrincipalException {

		if (!PropsValues.AUTH_TOKEN_CHECK_ENABLED) {
			return;
		}

		String sharedSecret = ParamUtil.getString(
			httpServletRequest, "p_auth_secret");

		if (AuthTokenWhitelistUtil.isValidSharedSecret(sharedSecret)) {
			return;
		}

		long companyId = PortalUtil.getCompanyId(httpServletRequest);

		if (AuthTokenWhitelistUtil.isOriginCSRFWhitelisted(companyId, origin)) {
			return;
		}

		if (origin.equals(SecurityPortletContainerWrapper.class.getName())) {
			String ppid = ParamUtil.getString(httpServletRequest, "p_p_id");

			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				companyId, ppid);

			if (AuthTokenWhitelistUtil.isPortletCSRFWhitelisted(
					httpServletRequest, portlet)) {

				return;
			}
		}

		String sessionToken = getSessionAuthenticationToken(
			httpServletRequest, _CSRF, false);

		if (Validator.isNull(sessionToken)) {
			throw new PrincipalException.MustHaveSessionCSRFToken(
				PortalUtil.getUserId(httpServletRequest), origin);
		}

		String csrfToken = ParamUtil.getString(httpServletRequest, "p_auth");

		if (Validator.isNull(csrfToken)) {
			csrfToken = GetterUtil.getString(
				httpServletRequest.getHeader("X-CSRF-Token"));
		}

		if (!csrfToken.equals(sessionToken)) {
			throw new PrincipalException.MustHaveValidCSRFToken(
				PortalUtil.getUserId(httpServletRequest), origin);
		}
	}

	@Override
	public String getToken(HttpServletRequest httpServletRequest) {
		return getSessionAuthenticationToken(httpServletRequest, _CSRF, true);
	}

	@Override
	public String getToken(
		HttpServletRequest httpServletRequest, long plid, String portletId) {

		return getSessionAuthenticationToken(
			httpServletRequest,
			PortletPermissionUtil.getPrimaryKey(plid, portletId), true);
	}

	@Override
	public boolean isValidPortletInvocationToken(
		HttpServletRequest httpServletRequest, Layout layout, Portlet portlet) {

		if (AuthTokenWhitelistUtil.isPortletInvocationWhitelisted(
				httpServletRequest, portlet)) {

			return true;
		}

		String portletToken = ParamUtil.getString(
			httpServletRequest, "p_p_auth");

		if (Validator.isNull(portletToken)) {
			HttpServletRequest originalHttpServletRequest =
				PortalUtil.getOriginalServletRequest(httpServletRequest);

			portletToken = ParamUtil.getString(
				originalHttpServletRequest, "p_p_auth");
		}

		if (Validator.isNotNull(portletToken)) {
			String key = PortletPermissionUtil.getPrimaryKey(
				layout.getPlid(), portlet.getPortletId());

			String sessionToken = getSessionAuthenticationToken(
				httpServletRequest, key, false);

			if (Validator.isNotNull(sessionToken) &&
				sessionToken.equals(portletToken)) {

				return true;
			}
		}

		return false;
	}

	protected String getSessionAuthenticationToken(
		HttpServletRequest httpServletRequest, String key,
		boolean createToken) {

		Object sessionAuthenticationToken = _getSessionAuthenticationToken(
			httpServletRequest, key, createToken);

		if (sessionAuthenticationToken instanceof String) {
			return (String)sessionAuthenticationToken;
		}

		return null;
	}

	private Object _getSessionAuthenticationToken(
		HttpServletRequest httpServletRequest, String key,
		boolean createToken) {

		Object sessionAuthenticationToken = null;

		String authenticationTokenKey = WebKeys.AUTHENTICATION_TOKEN.concat(
			key);
		HttpServletRequest currentHttpServletRequest = httpServletRequest;
		HttpSession httpSession = null;

		while (currentHttpServletRequest instanceof HttpServletRequestWrapper) {
			httpSession = currentHttpServletRequest.getSession();

			sessionAuthenticationToken = httpSession.getAttribute(
				authenticationTokenKey);

			if (sessionAuthenticationToken != null) {
				break;
			}

			HttpServletRequestWrapper httpServletRequestWrapper =
				(HttpServletRequestWrapper)currentHttpServletRequest;

			currentHttpServletRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();
		}

		if (sessionAuthenticationToken == null) {
			httpSession = currentHttpServletRequest.getSession();

			sessionAuthenticationToken = httpSession.getAttribute(
				authenticationTokenKey);
		}

		if (createToken &&
			((sessionAuthenticationToken == null) ||
			 (sessionAuthenticationToken == _NULL_TOKEN))) {

			sessionAuthenticationToken = PwdGenerator.getPassword(
				PropsValues.AUTH_TOKEN_LENGTH);

			httpSession.setAttribute(
				authenticationTokenKey, sessionAuthenticationToken);
		}

		return sessionAuthenticationToken;
	}

	private void _setNullSessionAuthenticationToken(
		HttpServletRequest httpServletRequest, String key) {

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		String authenticationTokenKey = WebKeys.AUTHENTICATION_TOKEN.concat(
			key);

		httpSession.setAttribute(authenticationTokenKey, _NULL_TOKEN);
	}

	private static final String _CSRF = "#CSRF";

	private static final byte[] _NULL_TOKEN = new byte[0];

	private static final Log _log = LogFactoryUtil.getLog(
		SessionAuthToken.class);

}