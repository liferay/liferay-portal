/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.cache.thread.local;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.transaction.NewTransactionLifecycleListener;
import com.liferay.portal.kernel.transaction.TransactionAttribute;
import com.liferay.portal.kernel.transaction.TransactionLifecycleListener;
import com.liferay.portal.kernel.transaction.TransactionStatus;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Shuyang Zhou
 */
public class ThreadLocalCacheManager {

	public static final TransactionLifecycleListener
		TRANSACTION_LIFECYCLE_LISTENER = new NewTransactionLifecycleListener() {

			@Override
			protected void doCommitted(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus) {

				if (!transactionAttribute.isReadOnly()) {
					enable(Lifecycle.REQUEST);
				}
			}

			@Override
			protected void doCreated(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus) {

				if (!transactionAttribute.isReadOnly()) {
					disable(Lifecycle.REQUEST);
				}
			}

			@Override
			protected void doRollbacked(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus, Throwable throwable) {

				if (!transactionAttribute.isReadOnly()) {
					enable(Lifecycle.REQUEST);
				}
			}

		};

	public static void clearAll(Lifecycle lifecycle) {
		ThreadLocalCaches threadLocalCaches = _getThreadLocalCaches(lifecycle);

		if (threadLocalCaches != null) {
			Map<Object, ThreadLocalCache<?>> threadLocalCacheMaps =
				threadLocalCaches._threadLocalCacheMap;

			threadLocalCacheMaps.clear();
		}
	}

	public static void destroy() {
		_requestThreadLocalCaches.remove();

		_eternalThreadLocalCaches.remove();
	}

	public static void disable() {
		_disabled = true;

		disable(Lifecycle.ETERNAL);
		disable(Lifecycle.REQUEST);
	}

	public static void disable(Lifecycle lifecycle) {
		ThreadLocalCaches threadLocalCaches = _getThreadLocalCaches(lifecycle);

		if (threadLocalCaches != null) {
			threadLocalCaches._disabled = true;

			Map<Object, ThreadLocalCache<?>> threadLocalCacheMaps =
				threadLocalCaches._threadLocalCacheMap;

			threadLocalCacheMaps.clear();
		}
	}

	public static void enable() {
		_disabled = false;

		enable(Lifecycle.ETERNAL);
		enable(Lifecycle.REQUEST);
	}

	public static void enable(Lifecycle lifecycle) {
		ThreadLocalCaches threadLocalCaches = _getThreadLocalCaches(lifecycle);

		if (threadLocalCaches != null) {
			threadLocalCaches._disabled = false;
		}
	}

	public static <T> ThreadLocalCache<T> getThreadLocalCache(
		Lifecycle lifecycle, Object name) {

		ThreadLocalCaches threadLocalCaches = _getThreadLocalCaches(lifecycle);

		if (_disabled || (threadLocalCaches == null) ||
			threadLocalCaches._disabled) {

			return (ThreadLocalCache<T>)_emptyThreadLocalCache;
		}

		Map<Object, ThreadLocalCache<?>> threadLocalCacheMap =
			threadLocalCaches._threadLocalCacheMap;

		ThreadLocalCache<?> threadLocalCache = threadLocalCacheMap.get(name);

		if (threadLocalCache == null) {
			threadLocalCache = new ThreadLocalCache<>(name, lifecycle);

			threadLocalCacheMap.put(name, threadLocalCache);
		}

		return (ThreadLocalCache<T>)threadLocalCache;
	}

	public static <T> ThreadLocalCache<T> getThreadLocalCache(
		Lifecycle lifecycle, Serializable name) {

		return getThreadLocalCache(lifecycle, (Object)name);
	}

	private static ThreadLocalCaches _getThreadLocalCaches(
		Lifecycle lifecycle) {

		if (lifecycle == Lifecycle.REQUEST) {
			return _requestThreadLocalCaches.get();
		}

		if (lifecycle == Lifecycle.ETERNAL) {
			return _eternalThreadLocalCaches.get();
		}

		return null;
	}

	private static boolean _disabled;
	private static final EmptyThreadLocalCache<?> _emptyThreadLocalCache =
		new EmptyThreadLocalCache<>();
	private static final ThreadLocal<ThreadLocalCaches>
		_eternalThreadLocalCaches = new CentralizedThreadLocal<>(
			ThreadLocalCacheManager.class + "._eternalThreadLocalCaches",
			ThreadLocalCaches::new, false);
	private static final ThreadLocal<ThreadLocalCaches>
		_requestThreadLocalCaches = new CentralizedThreadLocal<>(
			ThreadLocalCacheManager.class + "._requestThreadLocalCaches",
			ThreadLocalCaches::new, false);

	private static class EmptyThreadLocalCache<T> extends ThreadLocalCache<T> {

		@Override
		public T get(String key) {
			return null;
		}

		@Override
		public void put(String key, T object) {
		}

		@Override
		public void remove(String key) {
		}

		@Override
		public void removeAll() {
		}

		@Override
		public String toString() {
			return EmptyThreadLocalCache.class.getName();
		}

		private EmptyThreadLocalCache() {
			super(null, null);
		}

	}

	private static class ThreadLocalCaches {

		private boolean _disabled;
		private final Map<Object, ThreadLocalCache<?>> _threadLocalCacheMap =
			new HashMap<>();

	}

}