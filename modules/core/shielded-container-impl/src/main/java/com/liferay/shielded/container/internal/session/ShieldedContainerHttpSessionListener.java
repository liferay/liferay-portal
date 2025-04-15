/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.shielded.container.internal.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * @author Shuyang Zhou
 */
public class ShieldedContainerHttpSessionListener
	implements HttpSessionListener {

	public ShieldedContainerHttpSessionListener(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		HttpSession httpSession = httpSessionEvent.getSession();

		httpSession.setAttribute(
			ShieldedContainerHttpSessionActivationListener.NAME,
			new ShieldedContainerHttpSessionActivationListener());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		HttpSession httpSession = httpSessionEvent.getSession();

		_servletContext.removeAttribute(httpSession.getId());
	}

	private final ServletContext _servletContext;

}