/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.web.internal.session.replication;

import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * @author Dante Wang
 */
public class SessionReplicationHttpServletRequest
	extends PersistentHttpServletRequestWrapper {

	public SessionReplicationHttpServletRequest(
		HttpServletRequest httpServletRequest) {

		super(httpServletRequest);
	}

	@Override
	public HttpSession getSession() {
		HttpSession httpSession = super.getSession();

		if (httpSession == null) {
			return null;
		}

		httpSession = new SessionReplicationHttpSessionWrapper(httpSession);

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)getRequest();

		ServletContext servletContext = httpServletRequest.getServletContext();

		servletContext.setAttribute(httpSession.getId(), httpSession);

		return httpSession;
	}

	@Override
	public HttpSession getSession(boolean create) {
		HttpSession httpSession = super.getSession(create);

		if (httpSession == null) {
			return null;
		}

		return new SessionReplicationHttpSessionWrapper(httpSession);
	}

}