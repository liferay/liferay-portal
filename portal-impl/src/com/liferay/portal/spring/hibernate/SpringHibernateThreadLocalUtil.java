/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.reflect.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.NamedThreadLocal;
import org.springframework.transaction.support.ResourceHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Shuyang Zhou
 */
public class SpringHibernateThreadLocalUtil {

	public static <T> T getResource(Object key, Map<Object, Object> resources) {
		if ((resources == null) || resources.isEmpty()) {
			return null;
		}

		Object resource = resources.get(key);

		if (resource instanceof ResourceHolder) {
			ResourceHolder resourceHolder = (ResourceHolder)resource;

			if (resourceHolder.isVoid()) {
				resources.remove(key);

				return null;
			}
		}

		return (T)resource;
	}

	public static Map<Object, Object> getResources(boolean createIfAbsent) {
		Map<Object, Object> resources = _resources.get();

		if ((resources == null) && createIfAbsent) {
			resources = new HashMap<>();

			_resources.set(resources);
		}

		return resources;
	}

	public static boolean isCurrentTransactionReadOnly() {
		Boolean currentTransactionReadOnly = _currentTransactionReadOnly.get();

		// Spring only saves TRUE or null into this thread local

		if (currentTransactionReadOnly == null) {
			return false;
		}

		return true;
	}

	public static <T> T setResource(
		Object key, Object resource, Map<Object, Object> resources) {

		Object oldResource = null;

		if (resource == null) {
			if (resources == null) {
				return null;
			}

			oldResource = resources.remove(key);
		}
		else {
			oldResource = resources.put(key, resource);
		}

		if (oldResource instanceof ResourceHolder) {
			ResourceHolder resourceHolder = (ResourceHolder)oldResource;

			if (resourceHolder.isVoid()) {
				oldResource = null;
			}
		}

		return (T)oldResource;
	}

	private static final ThreadLocal<Boolean> _currentTransactionReadOnly;
	private static final ThreadLocal<Map<Object, Object>> _resources;

	static {
		try {
			Field nameField = ReflectionUtil.getDeclaredField(
				NamedThreadLocal.class, "name");

			ThreadLocal<?> currentTransactionReadOnly = null;
			ThreadLocal<?> resources = null;

			for (Field field :
					ReflectionUtil.getDeclaredFields(
						TransactionSynchronizationManager.class)) {

				if (!Modifier.isStatic(field.getModifiers()) ||
					!ThreadLocal.class.isAssignableFrom(field.getType())) {

					continue;
				}

				ThreadLocal<Object> threadLocal =
					(ThreadLocal<Object>)field.get(null);

				Object value = threadLocal.get();

				if (threadLocal instanceof NamedThreadLocal) {
					threadLocal = new CentralizedThreadLocal<>(
						(String)nameField.get(threadLocal), () -> null, false);
				}
				else {
					threadLocal = new CentralizedThreadLocal<>(false);
				}

				if (value != null) {
					threadLocal.set(value);
				}

				field.set(null, threadLocal);

				String name = field.getName();

				if (name.equals("currentTransactionReadOnly")) {
					currentTransactionReadOnly = threadLocal;
				}
				else if (name.equals("resources")) {
					resources = threadLocal;
				}
			}

			if (currentTransactionReadOnly == null) {
				throw new ExceptionInInitializerError(
					"Unable to locate \"currentTransactionReadOnly\" thread " +
						"local field from " +
							TransactionSynchronizationManager.class);
			}

			if (resources == null) {
				throw new ExceptionInInitializerError(
					"Unable to locate \"resources\" thread local field from " +
						TransactionSynchronizationManager.class);
			}

			_currentTransactionReadOnly =
				(ThreadLocal<Boolean>)currentTransactionReadOnly;

			_resources = (ThreadLocal<Map<Object, Object>>)resources;
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

}