/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal;

import com.liferay.bean.portlet.extension.BeanFilterMethod;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

import java.lang.reflect.Method;

/**
 * @author Neil Griffin
 */
public class CDIBeanFilterMethod implements BeanFilterMethod {

	public CDIBeanFilterMethod(
		Class<?> beanClass, BeanManager beanManager, Method method) {

		_beanClass = beanClass;
		_beanManager = beanManager;
		_method = method;
	}

	@Override
	public Object invoke(Object... args) throws ReflectiveOperationException {
		Bean<?> resolvedBean = _beanManager.resolve(
			_beanManager.getBeans(_beanClass));

		CreationalContext<?> creationalContext =
			_beanManager.createCreationalContext(resolvedBean);

		Object beanInstance = _beanManager.getReference(
			resolvedBean, _beanClass, creationalContext);

		return _method.invoke(beanInstance, args);
	}

	private final Class<?> _beanClass;
	private final BeanManager _beanManager;
	private final Method _method;

}