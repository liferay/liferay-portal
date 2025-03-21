/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal.adapter;

import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.osgi.web.http.servlet.HttpServletEndpoint;
import com.liferay.portal.osgi.web.wab.extender.internal.registration.ServletRegistrationImpl;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.descriptor.JspConfigDescriptor;
import jakarta.servlet.http.HttpServlet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.runtime.HttpServiceRuntimeConstants;

/**
 * @author Raymond Augé
 */
@Component(service = {})
public class HttpAdapter {

	@Activate
	protected void activate(ComponentContext componentContext) {
		Class<?> clazz = getClass();

		ServletContext servletContextProxy =
			(ServletContext)Proxy.newProxyInstance(
				clazz.getClassLoader(), _INTERFACES,
				new ServletContextAdaptor(_servletContext));

		ServletConfig servletConfig = new ServletConfig() {

			@Override
			public String getInitParameter(String name) {
				if (name.equals(
						HttpServiceRuntimeConstants.HTTP_SERVICE_ENDPOINT)) {

					return servletContextProxy.getContextPath() +
						servletContextProxy.getInitParameter(name);
				}

				return servletContextProxy.getInitParameter(name);
			}

			@Override
			public Enumeration<String> getInitParameterNames() {
				return servletContextProxy.getInitParameterNames();
			}

			@Override
			public ServletContext getServletContext() {
				return servletContextProxy;
			}

			@Override
			public String getServletName() {
				return "Module Framework Servlet";
			}

		};

		BundleContext bundleContext = componentContext.getBundleContext();

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put("bean.id", HttpServlet.class.getName());
		properties.put("original.bean", Boolean.TRUE.toString());

		_serviceRegistration = bundleContext.registerService(
			HttpServletEndpoint.class,
			new HttpServletEndpoint() {

				@Override
				public Dictionary<String, Object> getProperties() {
					return properties;
				}

				@Override
				public ServletConfig getServletConfig() {
					return servletConfig;
				}

			},
			null);
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();

		_serviceRegistration = null;
	}

	private static final Class<?>[] _INTERFACES = new Class<?>[] {
		ServletContext.class
	};

	private ServiceRegistration<?> _serviceRegistration;

	@Reference(target = "(original.bean=true)")
	private ServletContext _servletContext;

	private static class ServletContextAdaptor implements InvocationHandler {

		public ServletContextAdaptor(ServletContext servletContext) {
			_servletContext = servletContext;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			String methodName = method.getName();

			if (methodName.equals("getEffectiveMajorVersion")) {
				return 3;
			}
			else if (methodName.equals("getInitParameter") && (args != null) &&
					 (args.length == 1)) {

				if (Objects.equals(args[0], "osgi.http.endpoint")) {
					return _servletContext.getInitParameter((String)args[0]);
				}

				return null;
			}
			else if (methodName.equals("getInitParameterNames") &&
					 (args == null)) {

				return Collections.enumeration(
					Collections.singleton("osgi.http.endpoint"));
			}
			else if (methodName.equals("getJspConfigDescriptor") &&
					 JspConfigDescriptor.class.isAssignableFrom(
						 method.getReturnType())) {

				return null;
			}
			else if (methodName.equals("getServletRegistration") &&
					 (args != null) && (args.length == 1)) {

				if (Objects.equals(args[0], "Module Framework Servlet")) {
					ServletRegistration servletRegistration =
						new ServletRegistrationImpl();

					servletRegistration.addMapping("/o/*");

					return servletRegistration;
				}
			}

			try {
				return method.invoke(_servletContext, args);
			}
			catch (InvocationTargetException invocationTargetException) {
				throw invocationTargetException.getCause();
			}
		}

		private final ServletContext _servletContext;

	}

}