/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.module.util;

import com.liferay.petra.function.UnsafeFunction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Shuyang Zhou
 */
public class SystemBundleUtil {

	public static <S, R, E extends Throwable> R callService(
			Class<S> serviceClass, UnsafeFunction<S, R, E> unsafeFunction)
		throws E {

		BundleContext bundleContext = getBundleContext();

		ServiceReference<S> serviceReference =
			bundleContext.getServiceReference(serviceClass);

		if (serviceReference == null) {
			return unsafeFunction.apply(null);
		}

		S service = bundleContext.getService(serviceReference);

		try {
			return unsafeFunction.apply(service);
		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
	}

	public static <S, R, E extends Throwable> R callService(
			String serviceClassName, UnsafeFunction<S, R, E> unsafeFunction)
		throws E {

		BundleContext bundleContext = getBundleContext();

		ServiceReference<?> serviceReference =
			bundleContext.getServiceReference(serviceClassName);

		if (serviceReference == null) {
			return unsafeFunction.apply(null);
		}

		S service = (S)bundleContext.getService(serviceReference);

		try {
			return unsafeFunction.apply(service);
		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
	}

	public static Filter createFilter(String filterString) {
		BundleContext bundleContext = getBundleContext();

		try {
			return bundleContext.createFilter(filterString);
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			throw new RuntimeException(invalidSyntaxException);
		}
	}

	public static BundleContext getBundleContext() {
		Bundle systemBundle = _systemBundleProvider.getSystemBundle();

		if (systemBundle == null) {
			throw new IllegalStateException("System bundle is not initialized");
		}

		return systemBundle.getBundleContext();
	}

	public static ServiceLatch newServiceLatch() {
		return new ServiceLatch(getBundleContext());
	}

	private static final SystemBundleProvider _systemBundleProvider;

	static {
		ServiceLoader<SystemBundleProvider> serviceLoader = ServiceLoader.load(
			SystemBundleProvider.class,
			SystemBundleUtil.class.getClassLoader());

		Iterator<SystemBundleProvider> iterator = serviceLoader.iterator();

		List<SystemBundleProvider> systemBundleProviders = new ArrayList<>();

		Throwable throwable1 = null;

		try {
			while (iterator.hasNext()) {
				systemBundleProviders.add(iterator.next());
			}
		}
		catch (Throwable throwable2) {
			if (throwable1 == null) {
				throwable1 = throwable2;
			}
			else {
				throwable1.addSuppressed(throwable2);
			}
		}

		if (systemBundleProviders.isEmpty()) {
			ExceptionInInitializerError exceptionInInitializerError =
				new ExceptionInInitializerError(
					"Unable to locate module framework implementation");

			if (throwable1 != null) {
				exceptionInInitializerError.addSuppressed(throwable1);
			}

			throw exceptionInInitializerError;
		}

		systemBundleProviders.sort(Comparator.reverseOrder());

		_systemBundleProvider = systemBundleProviders.get(0);
	}

}