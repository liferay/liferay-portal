/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.context.helper.internal;

import com.liferay.portal.kernel.servlet.ServletContextClassLoaderPool;
import com.liferay.portal.servlet.delegate.ServletContextDelegate;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Shuyang Zhou
 */
public class CustomServletContextListener implements ServletContextListener {

	public CustomServletContextListener(Bundle bundle) {
		_bundle = bundle;
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		ServletContextClassLoaderPool.unregister(
			_servletContext.getServletContextName());

		_servletContext = null;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		_servletContext = ServletContextDelegate.create(
			servletContextEvent.getServletContext());

		BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

		ServletContextClassLoaderPool.register(
			_servletContext.getServletContextName(),
			bundleWiring.getClassLoader());
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	private final Bundle _bundle;
	private ServletContext _servletContext;

}