/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * @author Neil Griffin
 */
public class SpringHttpSessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		HttpSession httpSession = httpSessionEvent.getSession();

		ServletContext servletContext = httpSession.getServletContext();

		SpringBeanPortletExtension springBeanPortletExtension =
			(SpringBeanPortletExtension)servletContext.getAttribute(
				SpringBeanPortletExtension.class.getName());

		springBeanPortletExtension.step4SessionScopeBeforeDestroyed(
			httpSession);
	}

}