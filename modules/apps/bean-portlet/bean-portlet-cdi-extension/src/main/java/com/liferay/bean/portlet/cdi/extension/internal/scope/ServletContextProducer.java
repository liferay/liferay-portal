/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.scope;

import jakarta.annotation.Priority;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;

import jakarta.interceptor.Interceptor;

import jakarta.servlet.ServletContext;

/**
 * @author Neil Griffin
 */
@Alternative
@ApplicationScoped
@Priority(Interceptor.Priority.APPLICATION + 10)
public class ServletContextProducer {

	public void applicationScopedInitialized(
		@Initialized(ApplicationScoped.class) @Observes ServletContext
			servletContext) {

		_servletContext = servletContext;
	}

	@Produces
	public ServletContext getServletContext() {
		return _servletContext;
	}

	private static ServletContext _servletContext;

}