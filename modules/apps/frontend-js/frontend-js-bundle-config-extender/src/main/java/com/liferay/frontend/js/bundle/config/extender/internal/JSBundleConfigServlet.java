/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.bundle.config.extender.internal;

import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;
import com.liferay.portal.kernel.servlet.PortalWebResources;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.servlet.delegate.ServletContextDelegate;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Carlos Sierra Andrés
 * @author Chema Balsas
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.frontend.js.bundle.config.extender.internal.JSBundleConfigServlet",
		"osgi.http.whiteboard.servlet.pattern=/js_bundle_config",
		"service.ranking:Integer=" + (Integer.MAX_VALUE - 1000)
	},
	service = Servlet.class
)
public class JSBundleConfigServlet extends HttpServlet {

	@Override
	public void destroy() {
		_serviceRegistration.unregister();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		_serviceRegistration = _bundleContext.registerService(
			PortalWebResources.class,
			new InternalPortalWebResources(config.getServletContext()), null);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Override
	protected void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (GetterUtil.getLong(httpServletRequest.getParameter("t")) ==
				JSBundleConfigRegistryUtil.getLastModified()) {

			httpServletResponse.setHeader(
				HttpHeaders.CACHE_CONTROL,
				"immutable, max-age=31536000, public");
		}
		else {
			httpServletResponse.setHeader(
				HttpHeaders.CACHE_CONTROL, "no-cache");
		}

		httpServletResponse.setContentType(ContentTypes.TEXT_JAVASCRIPT_UTF8);

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(JSBundleConfigRegistryUtil.getJavaScript());
	}

	private BundleContext _bundleContext;
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