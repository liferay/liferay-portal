/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Tina Tian
 */
public class ClearThreadLocalUtil {

	public static void clearThreadLocal() throws Exception {
		if (!_INITIALIZED) {
			return;
		}

		Thread[] threads = ThreadUtil.getThreads();

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		for (Thread thread : threads) {
			_clearThreadLocal(thread, contextClassLoader);
		}
	}

	private static void _clearThreadLocal(
			Thread thread, ClassLoader classLoader)
		throws Exception {

		if (thread == null) {
			return;
		}

		Object threadLocalMap = _threadLocalsField.get(thread);

		Object inheritableThreadLocalMap = _inheritableThreadLocalsField.get(
			thread);

		_clearThreadLocalMap(threadLocalMap, classLoader);
		_clearThreadLocalMap(inheritableThreadLocalMap, classLoader);
	}

	private static void _clearThreadLocalMap(
			Object threadLocalMap, ClassLoader classLoader)
		throws Exception {

		if (threadLocalMap == null) {
			return;
		}

		Object[] table = (Object[])_tableField.get(threadLocalMap);

		if (table == null) {
			return;
		}

		int staleEntriesCount = 0;

		for (Object tableEntry : table) {
			if (tableEntry == null) {
				continue;
			}

			Reference<?> reference = (Reference<?>)tableEntry;

			Object key = reference.get();

			Object value = _valueField.get(tableEntry);

			boolean remove = false;

			if (key != null) {
				Class<?> keyClass = key.getClass();

				ClassLoader keyClassLoader = keyClass.getClassLoader();

				if (keyClassLoader == classLoader) {
					remove = true;
				}
			}

			if (value != null) {
				Class<?> valueClass = value.getClass();

				ClassLoader valueClassLoader = valueClass.getClassLoader();

				if (valueClassLoader == classLoader) {
					remove = true;
				}
			}

			if (!remove) {
				continue;
			}

			if (key != null) {
				if (_log.isDebugEnabled()) {
					Class<?> keyClass = key.getClass();

					_log.debug(
						"Clear a ThreadLocal with key of type " +
							keyClass.getCanonicalName());
				}

				_removeMethod.invoke(threadLocalMap, key);
			}
			else {
				staleEntriesCount++;
			}
		}

		if (staleEntriesCount > 0) {
			_expungeStaleEntriesMethod.invoke(threadLocalMap);
		}
	}

	private static final boolean _INITIALIZED;

	private static final Log _log = LogFactoryUtil.getLog(
		ClearThreadLocalUtil.class);

	private static final Method _expungeStaleEntriesMethod;
	private static final Field _inheritableThreadLocalsField;
	private static final Method _removeMethod;
	private static final Field _tableField;
	private static final Field _threadLocalsField;
	private static final Field _valueField;

	static {
		boolean initialized = false;

		Method expungeStaleEntriesMethod = null;
		Field inheritableThreadLocalsField = null;
		Method removeMethod = null;
		Field tableField = null;
		Field threadLocalsField = null;
		Field valueField = null;

		try {
			inheritableThreadLocalsField = ReflectionUtil.getDeclaredField(
				Thread.class, "inheritableThreadLocals");
			threadLocalsField = ReflectionUtil.getDeclaredField(
				Thread.class, "threadLocals");

			Class<?> threadLocalMapClass = Class.forName(
				"java.lang.ThreadLocal$ThreadLocalMap");

			expungeStaleEntriesMethod = ReflectionUtil.getDeclaredMethod(
				threadLocalMapClass, "expungeStaleEntries");
			removeMethod = ReflectionUtil.getDeclaredMethod(
				threadLocalMapClass, "remove", ThreadLocal.class);
			tableField = ReflectionUtil.getDeclaredField(
				threadLocalMapClass, "table");

			Class<?> threadLocalMapEntryClass = Class.forName(
				"java.lang.ThreadLocal$ThreadLocalMap$Entry");

			valueField = ReflectionUtil.getDeclaredField(
				threadLocalMapEntryClass, "value");

			initialized = true;
		}
		catch (Throwable throwable) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Failed to initialize ClearThreadLocalUtil", throwable);
			}
		}

		_expungeStaleEntriesMethod = expungeStaleEntriesMethod;
		_inheritableThreadLocalsField = inheritableThreadLocalsField;
		_removeMethod = removeMethod;
		_tableField = tableField;
		_threadLocalsField = threadLocalsField;
		_valueField = valueField;

		_INITIALIZED = initialized;
	}

}