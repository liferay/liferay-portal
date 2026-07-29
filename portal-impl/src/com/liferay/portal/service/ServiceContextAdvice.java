/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service;

import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Map;

/**
 * @author Preston Crary
 */
public class ServiceContextAdvice extends ChainableMethodAdvice {

	@Override
	public Object createMethodContext(
		Object target, Method method,
		Map<Class<? extends Annotation>, Annotation> annotations) {

		int index = _getServiceContextParameterIndex(method);

		if (index == -1) {
			return null;
		}

		return index;
	}

	@Override
	public Object invoke(
			AopMethodInvocation aopMethodInvocation, Object[] arguments)
		throws Throwable {

		int index = aopMethodInvocation.getAdviceMethodContext();

		ServiceContext serviceContext = (ServiceContext)arguments[index];

		if (serviceContext != null) {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);
		}

		try {
			return aopMethodInvocation.proceed(arguments);
		}
		finally {
			if (serviceContext != null) {
				ServiceContextThreadLocal.popServiceContext();
			}
		}
	}

	private int _getServiceContextParameterIndex(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();

		for (int i = parameterTypes.length - 1; i >= 0; i--) {
			if (ServiceContext.class.isAssignableFrom(parameterTypes[i])) {
				return i;
			}
		}

		return -1;
	}

}