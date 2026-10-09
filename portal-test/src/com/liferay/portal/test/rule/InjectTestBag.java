/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Preston Crary
 */
public class InjectTestBag {

	public InjectTestBag(Class<?> testClass) throws Exception {
		this(testClass, null);
	}

	public InjectTestBag(Class<?> testClass, Object target) throws Exception {
		_target = target;

		while (testClass != Object.class) {
			for (Field field : ReflectionUtil.getDeclaredFields(testClass)) {
				boolean staticField = Modifier.isStatic(field.getModifiers());

				if (((target == null) != staticField) ||
					!field.isAnnotationPresent(Inject.class)) {

					continue;
				}

				_fields.add(field);
			}

			testClass = testClass.getSuperclass();
		}
	}

	public void injectFields() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (Field field : _fields) {
			Inject inject = field.getAnnotation(Inject.class);

			Class<?> clazz = inject.type();

			if (clazz == Inject.FieldType.class) {
				clazz = field.getType();
			}
			else if (clazz == Inject.NoType.class) {
				clazz = null;
			}

			ServiceReference<?> serviceReference = _getServiceReference(
				bundleContext, clazz, field, inject.filter(),
				inject.blocking());

			if (serviceReference == null) {
				continue;
			}

			_serviceReferences.add(serviceReference);

			ReflectionTestUtil.setFieldValue(
				field, _target, bundleContext.getService(serviceReference));
		}
	}

	public void resetFields() throws Exception {
		for (Field field : _fields) {
			ReflectionTestUtil.setFieldValue(field, _target, null);
		}

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (ServiceReference<?> serviceReference : _serviceReferences) {
			bundleContext.ungetService(serviceReference);
		}
	}

	private <T> String _getFilterString(Class<T> clazz, String filterString) {
		if (filterString.isEmpty()) {
			return "(objectClass=" + clazz.getName() + ")";
		}

		if ((clazz != null) && !filterString.contains("objectClass")) {
			int index = filterString.indexOf('&');

			StringBundler sb = new StringBundler(5);

			if (index < 0) {
				sb.append("(&(objectClass=");
				sb.append(clazz.getName());
				sb.append(")(");
				sb.append(filterString);
				sb.append("))");
			}
			else {
				sb.append(filterString.substring(0, index));
				sb.append("&(objectClass=");
				sb.append(clazz.getName());
				sb.append(")");
				sb.append(filterString.substring(index + 1));
			}

			filterString = sb.toString();
		}
		else if (!filterString.startsWith("(")) {
			filterString = StringBundler.concat("(", filterString, ")");
		}

		return filterString;
	}

	private <T> ServiceReference<T> _getServiceReference(
			BundleContext bundleContext, Class<T> clazz, Field field,
			String filterString, boolean blocking)
		throws Exception {

		String filterStringString = _getFilterString(clazz, filterString);

		ServiceReference<T> serviceReference = _getServiceReference(
			bundleContext, clazz, filterStringString);

		if ((serviceReference != null) || !blocking) {
			return serviceReference;
		}

		CountDownLatch countDownLatch = new CountDownLatch(1);

		AtomicReference<ServiceTracker<T, T>> atomicReference =
			new AtomicReference<>();

		ServiceTracker<T, T> serviceTracker = new ServiceTracker<>(
			bundleContext, SystemBundleUtil.createFilter(filterStringString),
			new ServiceTrackerCustomizer<T, T>() {

				@Override
				public T addingService(ServiceReference<T> serviceReference) {
					countDownLatch.countDown();

					ServiceTracker<T, T> serviceTracker = atomicReference.get();

					serviceTracker.close();

					return null;
				}

				@Override
				public void modifiedService(
					ServiceReference<T> serviceReference, T service) {
				}

				@Override
				public void removedService(
					ServiceReference<T> serviceReference, T service) {
				}

			});

		atomicReference.set(serviceTracker);

		serviceTracker.open();

		int waitTime = 0;

		String className = "(no type)";

		if (clazz != null) {
			className = clazz.getName();
		}

		while (serviceReference == null) {
			waitTime += _SLEEP_TIME;

			if (waitTime >= TestPropsValues.CI_TEST_TIMEOUT_TIME) {
				throw new IllegalStateException(
					StringBundler.concat(
						"Timed out while waiting for service ", className, " ",
						filterString));
			}

			Class<?> testClass = field.getDeclaringClass();

			System.out.println(
				StringBundler.concat(
					"Waiting for service ", className, " ", filterString,
					" for field ", testClass.getName(), ".", field.getName()));

			try {
				countDownLatch.await(_SLEEP_TIME, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException interruptedException) {
				System.out.println(
					StringBundler.concat(
						"Stopped waiting for service ", className, " ",
						filterString, " for field ", testClass.getName(), ".",
						field.getName(), " due to interruption"));

				throw interruptedException;
			}

			serviceReference = _getServiceReference(
				bundleContext, clazz, filterStringString);
		}

		return serviceReference;
	}

	private <T> ServiceReference<T> _getServiceReference(
			BundleContext bundleContext, Class<T> clazz, String filterString)
		throws Exception {

		String className = null;

		if (clazz != null) {
			className = clazz.getName();
		}

		ServiceReference<?>[] serviceReferences =
			bundleContext.getAllServiceReferences(className, filterString);

		if (ArrayUtil.isEmpty(serviceReferences)) {
			return null;
		}

		return (ServiceReference<T>)serviceReferences[0];
	}

	private static final int _SLEEP_TIME = 2000;

	private final List<Field> _fields = new ArrayList<>();
	private final List<ServiceReference<?>> _serviceReferences =
		new ArrayList<>();
	private final Object _target;

}