/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.shielded.container.internal.proxy;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.EventObject;

/**
 * @author Tina Tian
 */
public class EventListenerInvocationHandler
	extends ContextClassLoaderInvocationHandler {

	public EventListenerInvocationHandler(
		ServletContext servletContext, ClassLoader contextClassLoader,
		Object target) {

		super(contextClassLoader, target);

		_servletContext = servletContext;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		if ((args != null) && (args[0] instanceof EventObject)) {
			EventObject eventObject = (EventObject)args[0];

			Object source = eventObject.getSource();

			if (source instanceof ServletContext) {
				_sourceField.set(eventObject, _servletContext);
			}
			else if (source instanceof HttpSession) {
				HttpSession httpSession = (HttpSession)source;

				Object updatedHttpSession = _servletContext.getAttribute(
					httpSession.getId());

				if (updatedHttpSession != null) {
					_sourceField.set(eventObject, updatedHttpSession);
				}
			}
		}

		return super.invoke(proxy, method, args);
	}

	private static Field _sourceField;

	static {
		try {
			_sourceField = EventObject.class.getDeclaredField("source");

			_sourceField.setAccessible(true);
		}
		catch (NoSuchFieldException noSuchFieldException) {
			throw new ExceptionInInitializerError(noSuchFieldException);
		}
	}

	private final ServletContext _servletContext;

}