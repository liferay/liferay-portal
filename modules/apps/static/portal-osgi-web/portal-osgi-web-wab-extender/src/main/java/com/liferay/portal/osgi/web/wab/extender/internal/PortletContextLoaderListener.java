/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal;

import com.liferay.portal.bean.BeanLocatorImpl;
import com.liferay.portal.kernel.bean.BeanLocator;
import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletClassLoaderUtil;
import com.liferay.portal.kernel.servlet.ServletContextClassLoaderPool;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.ModuleFrameworkPropsValues;
import com.liferay.portal.spring.aop.AopConfigurableApplicationContextConfigurator;
import com.liferay.portal.spring.configurator.ConfigurableApplicationContextConfigurator;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Brian Wing Shun Chan
 * @see    PortletApplicationContext
 */
public class PortletContextLoaderListener extends ContextLoaderListener {

	public PortletContextLoaderListener(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();

		ClassLoader classLoader = ServletContextClassLoaderPool.getClassLoader(
			servletContext.getServletContextName());

		if (classLoader == null) {
			throw new IllegalStateException(
				"Unable to find the class loader for servlet context " +
					servletContext.getServletContextName());
		}

		try {
			Class<?> beanLocatorUtilClass = Class.forName(
				"com.liferay.util.bean.PortletBeanLocatorUtil", true,
				classLoader);

			Method setBeanLocatorMethod = beanLocatorUtilClass.getMethod(
				"setBeanLocator", new Class<?>[] {BeanLocator.class});

			setBeanLocatorMethod.invoke(
				beanLocatorUtilClass, new Object[] {null});

			PortletBeanLocatorUtil.setBeanLocator(
				servletContext.getServletContextName(), null);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		super.contextDestroyed(servletContextEvent);
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		MethodKey.resetCache();

		ServletContext servletContext = servletContextEvent.getServletContext();

		Object previousApplicationContext = servletContext.getAttribute(
			WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

		servletContext.removeAttribute(
			WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

		ClassLoader classLoader = ServletContextClassLoaderPool.getClassLoader(
			servletContext.getServletContextName());

		if (classLoader == null) {
			throw new IllegalStateException(
				"Unable to find the class loader for servlet context " +
					servletContext.getServletContextName());
		}

		PortletClassLoaderUtil.setServletContextName(
			servletContext.getServletContextName());

		try {
			super.contextInitialized(servletContextEvent);
		}
		finally {
			PortletClassLoaderUtil.setServletContextName(null);
		}

		ApplicationContext applicationContext =
			WebApplicationContextUtils.getWebApplicationContext(servletContext);

		_registerApplicationContext(
			(ConfigurableApplicationContext)applicationContext);

		BeanLocatorImpl beanLocatorImpl = new BeanLocatorImpl(
			classLoader, applicationContext);

		try {
			Class<?> beanLocatorUtilClass = Class.forName(
				"com.liferay.util.bean.PortletBeanLocatorUtil", true,
				classLoader);

			Method setBeanLocatorMethod = beanLocatorUtilClass.getMethod(
				"setBeanLocator", new Class<?>[] {BeanLocator.class});

			setBeanLocatorMethod.invoke(
				beanLocatorUtilClass, new Object[] {beanLocatorImpl});

			PortletBeanLocatorUtil.setBeanLocator(
				servletContext.getServletContextName(), beanLocatorImpl);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (previousApplicationContext == null) {
			servletContext.removeAttribute(
				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		}
		else {
			servletContext.setAttribute(
				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
				previousApplicationContext);
		}
	}

	public List<ServiceRegistration<?>> getServiceRegistrations() {
		return _serviceRegistrations;
	}

	@Override
	protected WebApplicationContext createWebApplicationContext(
		ServletContext servletContext) {

		return new PortletApplicationContext();
	}

	@Override
	protected void customizeContext(
		ServletContext servletContext,
		ConfigurableWebApplicationContext configurableWebApplicationContext) {

		configurableWebApplicationContext.setConfigLocation(
			servletContext.getInitParameter(_PORTAL_CONFIG_LOCATION_PARAM));

		configurableWebApplicationContext.addBeanFactoryPostProcessor(
			configurableListableBeanFactory -> {
				if ((configurableListableBeanFactory.getBeanDefinitionCount() >
						0) &&
					!configurableListableBeanFactory.containsBean(
						"liferayDataSource")) {

					configurableListableBeanFactory.registerSingleton(
						"liferayDataSource",
						InfrastructureUtil.getDataSource());
				}
			});

		ConfigurableApplicationContextConfigurator
			configurableApplicationContextConfigurator =
				new AopConfigurableApplicationContextConfigurator();

		configurableApplicationContextConfigurator.configure(
			configurableWebApplicationContext);
	}

	private void _registerApplicationContext(
		ConfigurableApplicationContext configurableApplicationContext) {

		ConfigurableListableBeanFactory configurableListableBeanFactory =
			configurableApplicationContext.getBeanFactory();

		Iterator<String> iterator =
			configurableListableBeanFactory.getBeanNamesIterator();

		iterator.forEachRemaining(
			beanName -> {
				try {
					ServiceRegistration<?> serviceRegistration =
						_registerService(
							_bundleContext, beanName,
							configurableApplicationContext.getBean(beanName));

					if (serviceRegistration != null) {
						_serviceRegistrations.add(serviceRegistration);
					}
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			});
	}

	private ServiceRegistration<?> _registerService(
		BundleContext bundleContext, String beanName, Object bean) {

		Class<?> clazz = bean.getClass();

		OSGiBeanProperties osgiBeanProperties = clazz.getAnnotation(
			OSGiBeanProperties.class);

		Set<String> names = OSGiBeanProperties.Service.interfaceNames(
			bean, osgiBeanProperties,
			ModuleFrameworkPropsValues.
				MODULE_FRAMEWORK_SERVICES_IGNORED_INTERFACES);

		if (names.isEmpty()) {
			return null;
		}

		HashMapDictionary<String, Object> properties =
			new HashMapDictionary<>();

		if (osgiBeanProperties != null) {
			properties.putAll(
				OSGiBeanProperties.Convert.toMap(osgiBeanProperties));
		}

		properties.put("bean.id", beanName);

		return bundleContext.registerService(
			names.toArray(new String[0]), bean, properties);
	}

	private static final String _PORTAL_CONFIG_LOCATION_PARAM =
		"portalContextConfigLocation";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletContextLoaderListener.class);

	private final BundleContext _bundleContext;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}