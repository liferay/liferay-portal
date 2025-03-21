/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.bundle.config.extender.internal;

import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;
import com.liferay.portal.kernel.servlet.PortalWebResources;
import com.liferay.portal.servlet.delegate.ServletContextDelegate;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 * @author Chema Balsas
 */
@Component(enabled = false, service = {})
public class JSBundleConfigPortalWebResources {

	@Activate
	protected void activate(BundleContext bundleContext) {
		try {
			ServletConfig servletConfig =
				_jsBundleConfigServlet.getServletConfig();

			PortalWebResources portalWebResources =
				new InternalPortalWebResources(
					servletConfig.getServletContext());

			_serviceRegistration = bundleContext.registerService(
				PortalWebResources.class, portalWebResources, null);
		}
		catch (NoClassDefFoundError ncdfe) {
			throw new RuntimeException(ncdfe);
		}
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Reference(
		target = "(component.name=com.liferay.frontend.js.bundle.config.extender.internal.JSBundleConfigServlet)"
	)
	private Servlet _jsBundleConfigServlet;

	private ServiceRegistration<?> _serviceRegistration;

	private class InternalPortalWebResources implements PortalWebResources {

		@Override
		public String getContextPath() {
			return _servletContext.getContextPath();
		}

		@Override
		public long getLastModified() {
			return JSBundleConfigRegistryUtil.getLastModified();
		}

		@Override
		public String getResourceType() {
			return PortalWebResourceConstants.RESOURCE_TYPE_JS_BUNDLE_CONFIG;
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		private InternalPortalWebResources(ServletContext servletContext) {
			_servletContext = ServletContextDelegate.create(servletContext);
		}

		private final ServletContext _servletContext;

	}

}