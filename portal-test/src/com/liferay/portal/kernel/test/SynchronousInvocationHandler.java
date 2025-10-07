/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.concurrent.CyclicBarrier;

/**
 * @author Matthew Tambara
 * @author Shuyang Zhou
 */
public class SynchronousInvocationHandler implements InvocationHandler {

	public static void disable() {
		_synchronized.remove();
	}

	public static void enable() {
		_synchronized.set(Boolean.TRUE);
	}

	public SynchronousInvocationHandler(
		int syncCount, Runnable syncRunnable, Method syncMethod,
		Object target) {

		_syncMethod = syncMethod;
		_target = target;

		_cyclicBarrier = new CyclicBarrier(syncCount, syncRunnable);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		if ((_synchronized.get() == Boolean.TRUE) &&
			_syncMethod.equals(method)) {

			_cyclicBarrier.await();
		}

		return method.invoke(_target, args);
	}

	private static final ThreadLocal<Boolean> _synchronized =
		new InheritableThreadLocal<>();

	private final CyclicBarrier _cyclicBarrier;
	private final Method _syncMethod;
	private final Object _target;

}