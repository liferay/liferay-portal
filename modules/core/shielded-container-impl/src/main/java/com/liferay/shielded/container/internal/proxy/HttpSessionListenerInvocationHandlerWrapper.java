/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.shielded.container.internal.proxy;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Shuyang Zhou
 */
public class HttpSessionListenerInvocationHandlerWrapper
	implements InvocationHandler {

	public HttpSessionListenerInvocationHandlerWrapper(
		InvocationHandler invocationHandler, ProxyFactory proxyFactory,
		ClassLoader classLoader) {

		_invocationHandler = invocationHandler;
		_proxyFactory = proxyFactory;
		_classLoader = classLoader;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		if (_sessionDestroyedMethod.equals(method)) {
			HttpSessionEvent httpSessionEvent = (HttpSessionEvent)args[0];

			HttpSession httpSession = httpSessionEvent.getSession();

			args[0] = new HttpSessionEvent(
				_proxyFactory.createASMWrapper(
					_classLoader, HttpSession.class,
					new HttpSessionDelegate(httpSession), httpSession));
		}

		return _invocationHandler.invoke(proxy, method, args);
	}

	private static final Method _sessionDestroyedMethod;

	static {
		try {
			_sessionDestroyedMethod = HttpSessionListener.class.getMethod(
				"sessionDestroyed", HttpSessionEvent.class);
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new ExceptionInInitializerError(noSuchMethodException);
		}
	}

	private final ClassLoader _classLoader;
	private final InvocationHandler _invocationHandler;
	private final ProxyFactory _proxyFactory;

}