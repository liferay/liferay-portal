/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.sessionid;

import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 */
public class SessionIdServletRequest extends HttpServletRequestWrapper {

	public SessionIdServletRequest(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		super(httpServletRequest);

		_httpServletResponse = httpServletResponse;
	}

	@Override
	public HttpSession getSession() {
		HttpSession httpSession = super.getSession();

		process(httpSession);

		return httpSession;
	}

	@Override
	public HttpSession getSession(boolean create) {
		HttpSession httpSession = super.getSession(create);

		process(httpSession);

		return httpSession;
	}

	protected void process(HttpSession httpSession) {
		if ((httpSession == null) || !httpSession.isNew() || !isSecure() ||
			isRequestedSessionIdFromCookie()) {

			return;
		}

		Object jSessionIdAlreadySet = getAttribute(_JSESSIONID_ALREADY_SET);

		if (jSessionIdAlreadySet != null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Processing " + httpSession.getId());
		}

		Cookie cookie = new Cookie(_JSESSIONID, httpSession.getId());

		cookie.setMaxAge(-1);

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_NECESSARY, cookie,
			(HttpServletRequest)super.getRequest(), _httpServletResponse);

		setAttribute(_JSESSIONID_ALREADY_SET, Boolean.TRUE);
	}

	private static final String _JSESSIONID = "JSESSIONID";

	private static final String _JSESSIONID_ALREADY_SET =
		"JSESSIONID_ALREADY_SET";

	private static final Log _log = LogFactoryUtil.getLog(
		SessionIdServletRequest.class);

	private final HttpServletResponse _httpServletResponse;

}