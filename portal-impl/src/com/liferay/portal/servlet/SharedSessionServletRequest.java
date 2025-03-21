/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.portal.kernel.servlet.SharedSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 */
public class SharedSessionServletRequest extends HttpServletRequestWrapper {

	public SharedSessionServletRequest(
		HttpServletRequest httpServletRequest, boolean shared) {

		super(httpServletRequest);

		_shared = shared;
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	@Override
	public HttpSession getSession(boolean create) {
		if (!create || _shared) {
			return _getPortalHttpSession(create);
		}

		return new SharedSession(
			_getPortalHttpSession(true), super.getSession(true));
	}

	public HttpSession getSharedSession() {
		return _getPortalHttpSession(true);
	}

	private HttpSession _getPortalHttpSession(boolean create) {
		HttpSession httpSession = super.getSession(false);

		if (httpSession == null) {
			httpSession = super.getSession(create);
		}

		return httpSession;
	}

	private final boolean _shared;

}