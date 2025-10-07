/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.internal.service;

import com.liferay.portal.deploy.hot.ServiceBag;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.aop.AopInvocationHandler;

import java.io.Closeable;

import java.lang.reflect.InvocationHandler;

import java.util.function.Function;

import org.osgi.framework.BundleContext;

/**
 * @author Dante Wang
 */
public class StagingAdviceUtil {

	public static <T> Closeable register(
		BundleContext bundleContext,
		Function<Object, InvocationHandler> function, T service,
		Class<T> serviceClass) {

		AopInvocationHandler aopInvocationHandler =
			ProxyUtil.fetchInvocationHandler(
				service, AopInvocationHandler.class);

		ServiceBag<?> serviceBag = new ServiceBag(
			aopInvocationHandler, serviceClass,
			(ServiceWrapper<?>)ProxyUtil.newProxyInstance(
				AggregateClassLoader.getAggregateClassLoader(
					PortalClassLoaderUtil.getClassLoader(),
					StagingAdviceUtil.class.getClassLoader()),
				new Class<?>[] {
					IdentifiableOSGiService.class, serviceClass,
					BaseLocalService.class, ServiceWrapper.class
				},
				function.apply(aopInvocationHandler.getTarget())),
			bundleContext, bundleContext.getServiceReference(serviceClass));

		return serviceBag::replace;
	}

}