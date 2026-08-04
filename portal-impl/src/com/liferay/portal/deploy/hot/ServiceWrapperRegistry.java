/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.deploy.hot;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.aop.AopInvocationHandler;

import java.io.Closeable;
import java.io.IOException;

import java.lang.reflect.Method;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Raymond Augé
 */
public class ServiceWrapperRegistry {

	public ServiceWrapperRegistry() {
		_serviceTracker = new ServiceTracker<>(
			_bundleContext, ServiceWrapper.class.getName(),
			new ServiceWrapperServiceTrackerCustomizer());

		_serviceTracker.open();
	}

	public void close() {
		_serviceTracker.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServiceWrapperRegistry.class);

	private final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private final ServiceTracker<ServiceWrapper<?>, Closeable> _serviceTracker;

	private class ServiceWrapperServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<ServiceWrapper<?>, Closeable> {

		@Override
		public Closeable addingService(
			ServiceReference<ServiceWrapper<?>> serviceReference) {

			ServiceWrapper<?> serviceWrapper = _bundleContext.getService(
				serviceReference);

			try {
				return _getServiceBag(serviceWrapper);
			}
			catch (Throwable throwable) {
				_log.error(
					"Unable to get service bag for " +
						serviceWrapper.getClass(),
					throwable);
			}

			return null;
		}

		@Override
		public void modifiedService(
			ServiceReference<ServiceWrapper<?>> serviceReference,
			Closeable closeable) {
		}

		@Override
		public void removedService(
			ServiceReference<ServiceWrapper<?>> serviceReference,
			Closeable closeable) {

			_bundleContext.ungetService(serviceReference);

			try {
				closeable.close();
			}
			catch (IOException ioException) {
				_log.error(ioException);
			}
		}

		private <T> ServiceBag<?> _createServiceBag(
			Object service, ServiceWrapper<T> serviceWrapper,
			Class<?> serviceTypeClass, ServiceReference<?> serviceReference) {

			Object serviceProxy = service;

			if (!ProxyUtil.isProxyClass(serviceProxy.getClass())) {
				_log.error(
					"Service hooks require Spring to be configured to use " +
						"JdkDynamicProxy and will not work with CGLIB");

				if (serviceReference != null) {
					_bundleContext.ungetService(serviceReference);
				}

				return null;
			}

			AopInvocationHandler aopInvocationHandler =
				ProxyUtil.fetchInvocationHandler(
					serviceProxy, AopInvocationHandler.class);

			synchronized (aopInvocationHandler) {
				serviceWrapper.setWrappedService(
					(T)aopInvocationHandler.getTarget());

				return new ServiceBag<>(
					aopInvocationHandler, serviceTypeClass, serviceWrapper,
					_bundleContext, serviceReference);
			}
		}

		private <T> Closeable _getServiceBag(ServiceWrapper<T> serviceWrapper)
			throws NoSuchMethodException {

			Class<?> clazz = serviceWrapper.getClass();

			Method method = clazz.getMethod(
				"getWrappedService", new Class<?>[0]);

			Class<T> serviceTypeClass = (Class<T>)method.getReturnType();

			ServiceReference<?> serviceReference =
				_bundleContext.getServiceReference(serviceTypeClass);

			if (serviceReference == null) {
				ServiceTracker<T, ServiceBag<?>> serviceTracker =
					new ServiceTracker<T, ServiceBag<?>>(
						_bundleContext, serviceTypeClass, null) {

						@Override
						public ServiceBag<?> addingService(
							ServiceReference<T> serviceReference) {

							return _createServiceBag(
								_bundleContext.getService(serviceReference),
								serviceWrapper, serviceTypeClass,
								serviceReference);
						}

						@Override
						public void removedService(
							ServiceReference<T> serviceReference,
							ServiceBag<?> serviceBag) {

							serviceBag.replace();
						}

					};

				serviceTracker.open();

				return serviceTracker::close;
			}

			ServiceBag<?> serviceBag = _createServiceBag(
				_bundleContext.getService(serviceReference), serviceWrapper,
				serviceTypeClass, serviceReference);

			return serviceBag::replace;
		}

	}

}