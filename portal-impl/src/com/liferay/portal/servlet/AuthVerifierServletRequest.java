/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.portal.kernel.servlet.HttpSessionWrapper;
import com.liferay.portal.kernel.servlet.ProtectedServletRequest;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Stian Sigvartsen
 */
public class AuthVerifierServletRequest extends ProtectedServletRequest {

	public AuthVerifierServletRequest(
		HttpServletRequest httpServletRequest, long userId, String authType) {

		super(httpServletRequest, String.valueOf(userId), authType);

		_userId = userId;

		httpServletRequest.removeAttribute(WebKeys.USER);
		httpServletRequest.setAttribute(WebKeys.USER_ID, userId);
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	@Override
	public HttpSession getSession(boolean create) {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)getRequest();

		if (HttpServletRequest.FORM_AUTH.equals(getAuthType())) {
			return httpServletRequest.getSession(create);
		}

		HttpSession httpSession = httpServletRequest.getSession(false);

		if (httpSession == null) {
			if (create) {
				_isolatedHttpSession = new IsolatedHttpSession(
					httpServletRequest.getSession(true));
			}
			else {
				_isolatedHttpSession = null;
			}
		}
		else if ((_isolatedHttpSession == null) ||
				 !httpSession.equals(_isolatedHttpSession._httpSession)) {

			_isolatedHttpSession = new IsolatedHttpSession(httpSession);
		}

		return _isolatedHttpSession;
	}

	private IsolatedHttpSession _isolatedHttpSession;
	private final Long _userId;

	private class IsolatedHttpSession extends HttpSessionWrapper {

		public IsolatedHttpSession(HttpSession httpSession) {
			super(httpSession);

			_httpSession = httpSession;
		}

		@Override
		public Object getAttribute(String name) {
			if (name.equals(WebKeys.USER_ID)) {
				return _userId;
			}

			return _attributes.get(name);
		}

		@Override
		public void setAttribute(String name, Object value) {
			_attributes.put(name, value);
		}

		private final Map<String, Object> _attributes = new HashMap<>();
		private final HttpSession _httpSession;

	}

}