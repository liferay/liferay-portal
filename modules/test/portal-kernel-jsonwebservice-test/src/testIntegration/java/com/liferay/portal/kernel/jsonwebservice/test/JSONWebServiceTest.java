/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.jsonwebservice.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.bean.ClassLoaderBeanHandler;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Istvan Sajtos
 */
@RunWith(Arquillian.class)
public class JSONWebServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAccessControlled() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(JSONWebServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		for (ServiceReference<?> serviceReference :
				_getServiceReferences(bundleContext)) {

			Class<?> clazz = _getTargetClass(
				bundleContext.getService(serviceReference));

			if (!_isAccessControlRequired(clazz)) {
				continue;
			}

			Assert.assertTrue(_hasAnnotation(clazz, AccessControlled.class));
		}
	}

	@Test
	public void testAopService() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(JSONWebServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		for (ServiceReference<?> serviceReference :
				_getServiceReferences(bundleContext)) {

			Object service = bundleContext.getService(serviceReference);

			if (!_isAccessControlRequired(_getTargetClass(service))) {
				continue;
			}

			Assert.assertTrue(service instanceof AopService);
		}
	}

	@Test
	public void testJSONWebService() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(JSONWebServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		for (ServiceReference<?> serviceReference :
				_getServiceReferences(bundleContext)) {

			Class<?> clazz = _getTargetClass(
				bundleContext.getService(serviceReference));

			Assert.assertTrue(_hasAnnotation(clazz, JSONWebService.class));
		}
	}

	private ServiceReference<?>[] _getServiceReferences(
			BundleContext bundleContext)
		throws Exception {

		return bundleContext.getServiceReferences(
			(String)null,
			"(&(component.name=*)(json.web.service.context.path=*))");
	}

	private Class<?> _getTargetClass(Object service) {
		while (true) {
			if (ProxyUtil.isProxyClass(service.getClass())) {
				InvocationHandler invocationHandler =
					ProxyUtil.getInvocationHandler(service);

				if (invocationHandler instanceof AopInvocationHandler) {
					AopInvocationHandler aopInvocationHandler =
						(AopInvocationHandler)invocationHandler;

					service = aopInvocationHandler.getTarget();
				}
				else if (invocationHandler instanceof ClassLoaderBeanHandler) {
					ClassLoaderBeanHandler classLoaderBeanHandler =
						(ClassLoaderBeanHandler)invocationHandler;

					Object bean = classLoaderBeanHandler.getBean();

					if (bean instanceof ServiceWrapper) {
						ServiceWrapper<?> serviceWrapper =
							(ServiceWrapper<?>)bean;

						service = serviceWrapper.getWrappedService();
					}
					else {
						service = bean;
					}
				}
			}
			else if (service instanceof ServiceWrapper) {
				ServiceWrapper<?> serviceWrapper = (ServiceWrapper<?>)service;

				service = serviceWrapper.getWrappedService();
			}
			else {
				break;
			}
		}

		return service.getClass();
	}

	private boolean _hasAnnotation(
		Class<?> clazz, Class<? extends Annotation> annotation) {

		while (clazz != null) {
			if (clazz.isAnnotationPresent(annotation)) {
				return true;
			}

			for (Class<?> iface : clazz.getInterfaces()) {
				if (iface.isAnnotationPresent(annotation)) {
					return true;
				}
			}

			clazz = clazz.getSuperclass();
		}

		return false;
	}

	private boolean _isAccessControlRequired(Class<?> clazz) {
		String className = clazz.getName();

		if (className.contains("example") || className.contains("test")) {
			return false;
		}

		return true;
	}

}