/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.cxf.common.internal;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.remote.cxf.common.configuration.CXFEndpointPublisherConfiguration;
import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	configurationPid = "com.liferay.portal.remote.cxf.common.configuration.CXFEndpointPublisherConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class CXFEndpointPublisher {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_dependencyManager = new DependencyManager(bundleContext);

		org.apache.felix.dm.Component component =
			_dependencyManager.createComponent();

		CXFEndpointPublisherConfiguration cxfEndpointPublisherConfiguration =
			ConfigurableUtil.createConfigurable(
				CXFEndpointPublisherConfiguration.class, properties);

		ServicesRegistrator servicesRegistrator = new ServicesRegistrator(
			bundleContext, properties);

		component.setImplementation(servicesRegistrator);

		String[] extensions = cxfEndpointPublisherConfiguration.extensions();

		if (extensions != null) {
			for (String extension : extensions) {
				ServiceDependency serviceDependency =
					_dependencyManager.createServiceDependency();

				serviceDependency.setCallbacks(
					servicesRegistrator, "addExtension", null);
				serviceDependency.setRequired(true);
				serviceDependency.setService(Object.class, extension);

				component.add(serviceDependency);
			}
		}

		_dependencyManager.add(component);
	}

	@Deactivate
	protected void deactivate() {
		_dependencyManager.clear();
	}

	private DependencyManager _dependencyManager;

	private static class ServicesRegistrator {

		public ServicesRegistrator(
			BundleContext bundleContext, Map<String, Object> properties) {

			_bundleContext = bundleContext;
			_properties = properties;
		}

		@SuppressWarnings("unused")
		protected void addExtension(
			Map<String, Object> properties, Object extension) {

			Class<?> extensionClass = (Class<?>)properties.get(
				"cxf.extension.class");

			if (extensionClass == null) {
				extensionClass = extension.getClass();
			}

			_extensions.put(extensionClass, extension);
		}

		@SuppressWarnings("unused")
		protected void start() {
			Dictionary<String, Object> properties = new Hashtable<>();

			String contextPath = String.valueOf(_properties.get("contextPath"));

			String contextName = contextPath.substring(1);

			contextName = StringUtil.replace(contextName, '/', '.');

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
				contextName);

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
				contextPath);

			_servletContextHelperServiceRegistration =
				_bundleContext.registerService(
					ServletContextHelper.class,
					new ServletContextHelper(_bundleContext.getBundle()) {
					},
					properties);

			CXFNonSpringServlet cxfNonSpringServlet = new CXFNonSpringServlet();

			CXFBusFactory cxfBusFactory =
				(CXFBusFactory)CXFBusFactory.newInstance(
					CXFBusFactory.class.getName());

			Bus bus = cxfBusFactory.createBus(_extensions);

			ServiceReference<ServletContextHelper> serviceReference =
				_servletContextHelperServiceRegistration.getReference();

			properties = new Hashtable<>();

			String httpWhiteboardContextSelect =
				"(service.id=" + serviceReference.getProperty("service.id") +
					")";

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				httpWhiteboardContextSelect);

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME,
				CXFNonSpringServlet.class.getName());
			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, "/*");

			Object property = properties.get(
				"servlet.init.hide-service-list-page");

			if (property == null) {
				properties.put("servlet.init.hide-service-list-page", "true");
			}

			cxfNonSpringServlet.setBus(bus);

			_servletServiceRegistration = _bundleContext.registerService(
				Servlet.class, cxfNonSpringServlet, properties);

			Object authVerifierPropertiesObject = _properties.get(
				"authVerifierProperties");

			if (authVerifierPropertiesObject != null) {
				String[] authVerifierPropertiesArray = null;

				if (authVerifierPropertiesObject instanceof String) {
					authVerifierPropertiesArray = new String[] {
						(String)authVerifierPropertiesObject
					};
				}
				else {
					authVerifierPropertiesArray =
						(String[])authVerifierPropertiesObject;
				}

				properties = new Hashtable<>();

				properties.put(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
					httpWhiteboardContextSelect);
				properties.put(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME,
					AuthVerifierFilter.class.getName());
				properties.put(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET,
					CXFNonSpringServlet.class.getName());

				for (String authVerifierProperty :
						authVerifierPropertiesArray) {

					String[] authVerifierPropertyParts =
						authVerifierProperty.split("=");

					properties.put(
						HttpWhiteboardConstants.
							HTTP_WHITEBOARD_FILTER_INIT_PARAM_PREFIX +
								authVerifierPropertyParts[0],
						authVerifierPropertyParts[1]);
				}

				_authVerifierFilterServiceRegistration =
					_bundleContext.registerService(
						Filter.class, new AuthVerifierFilter(), properties);

				properties = new Hashtable<>();

				properties.put(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
					httpWhiteboardContextSelect);
				properties.put(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME,
					RemoteAccessFilter.class.getName());
				properties.put(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET,
					CXFNonSpringServlet.class.getName());

				_remoteAccessFilterServiceRegistration =
					_bundleContext.registerService(
						Filter.class, new RemoteAccessFilter(), properties);
			}

			properties = new Hashtable<>();

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH,
				contextPath);

			_busServiceRegistration = _bundleContext.registerService(
				Bus.class, bus, properties);
		}

		@SuppressWarnings("unused")
		protected void stop() {
			try {
				_busServiceRegistration.unregister();
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to unregister CXF bus service registration " +
							_busServiceRegistration,
						exception);
				}
			}

			if (_remoteAccessFilterServiceRegistration != null) {
				try {
					_remoteAccessFilterServiceRegistration.unregister();
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to unregister RemoteAccessFilter " +
								"registration " +
									_remoteAccessFilterServiceRegistration,
							exception);
					}
				}
			}

			if (_authVerifierFilterServiceRegistration != null) {
				try {
					_authVerifierFilterServiceRegistration.unregister();
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to unregister AuthVerifierFilter " +
								"registration " +
									_authVerifierFilterServiceRegistration,
							exception);
					}
				}
			}

			try {
				_servletServiceRegistration.unregister();
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to unregister servlet service registration " +
							_servletServiceRegistration,
						exception);
				}
			}

			try {
				_servletContextHelperServiceRegistration.unregister();
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to unregister servlet context helper service " +
							"registration " +
								_servletContextHelperServiceRegistration,
						exception);
				}
			}
		}

		private static final Log _log = LogFactoryUtil.getLog(
			CXFEndpointPublisher.class);

		private ServiceRegistration<Filter>
			_authVerifierFilterServiceRegistration;
		private final BundleContext _bundleContext;
		private ServiceRegistration<Bus> _busServiceRegistration;
		private final Map<Class<?>, Object> _extensions = new HashMap<>();
		private final Map<String, Object> _properties;
		private ServiceRegistration<Filter>
			_remoteAccessFilterServiceRegistration;
		private ServiceRegistration<ServletContextHelper>
			_servletContextHelperServiceRegistration;
		private ServiceRegistration<Servlet> _servletServiceRegistration;

		private static class RemoteAccessFilter implements Filter {

			@Override
			public void destroy() {
			}

			@Override
			public void doFilter(
					ServletRequest servletRequest,
					ServletResponse servletResponse, FilterChain chain)
				throws IOException, ServletException {

				boolean remoteAccess =
					AccessControlThreadLocal.isRemoteAccess();

				try {
					AccessControlThreadLocal.setRemoteAccess(true);

					chain.doFilter(servletRequest, servletResponse);
				}
				catch (Exception exception) {
					throw new ServletException(exception);
				}
				finally {
					AccessControlThreadLocal.setRemoteAccess(remoteAccess);
				}
			}

			@Override
			public void init(FilterConfig filterConfig)
				throws ServletException {
			}

		}

	}

}