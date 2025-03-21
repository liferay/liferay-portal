/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.deploy.hot;

import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.util.ProxyFactory;

import jakarta.servlet.ServletContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Brian Wing Shun Chan
 */
public abstract class BaseHotDeployListener implements HotDeployListener {

	public void throwHotDeployException(
			HotDeployEvent event, String msg, Throwable throwable)
		throws HotDeployException {

		ServletContext servletContext = event.getServletContext();

		String servletContextName = servletContext.getServletContextName();

		throw new HotDeployException(msg + servletContextName, throwable);
	}

	protected String getClpServletContextName(
			Class<?> clpMessageListenerClass,
			MessageListener clpMessageListener)
		throws Exception {

		Exception exception1 = null;

		try {
			Method servletContextNameMethod = clpMessageListenerClass.getMethod(
				"getServletContextName");

			return (String)servletContextNameMethod.invoke(null);
		}
		catch (Exception exception2) {
			exception1 = exception2;
		}

		try {
			Field servletContextNameField = clpMessageListenerClass.getField(
				"SERVLET_CONTEXT_NAME");

			Object clpServletContextName = servletContextNameField.get(
				clpMessageListener);

			return clpServletContextName.toString();
		}
		catch (Exception exception2) {
		}

		throw exception1;
	}

	protected Object newInstance(
			ClassLoader portletClassLoader, Class<?> interfaceClass,
			String implClassName)
		throws Exception {

		return ProxyFactory.newInstance(
			portletClassLoader, interfaceClass, implClassName);
	}

	protected Object newInstance(
			ClassLoader portletClassLoader, Class<?>[] interfaceClasses,
			String implClassName)
		throws Exception {

		return ProxyFactory.newInstance(
			portletClassLoader, interfaceClasses, implClassName);
	}

}