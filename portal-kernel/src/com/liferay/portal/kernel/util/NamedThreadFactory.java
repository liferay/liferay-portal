/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Michael C. Han
 * @author Shuyang Zhou
 */
public class NamedThreadFactory implements ThreadFactory {

	public NamedThreadFactory(
		String name, int priority, ClassLoader contextClassLoader) {

		Thread currentThread = Thread.currentThread();

		_group = currentThread.getThreadGroup();

		_name = name;
		_priority = priority;
		_contextClassLoader = contextClassLoader;
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(
			_group, runnable,
			StringBundler.concat(
				_name, StringPool.MINUS, _counter.incrementAndGet()));

		thread.setDaemon(true);
		thread.setPriority(_priority);

		if (_contextClassLoader != null) {
			thread.setContextClassLoader(_contextClassLoader);
		}

		return thread;
	}

	private final ClassLoader _contextClassLoader;
	private final AtomicInteger _counter = new AtomicInteger();
	private final ThreadGroup _group;
	private final String _name;
	private final int _priority;

}