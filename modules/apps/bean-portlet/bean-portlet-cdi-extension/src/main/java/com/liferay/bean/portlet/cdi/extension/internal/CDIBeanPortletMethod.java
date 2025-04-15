/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal;

import com.liferay.bean.portlet.extension.BaseBeanPortletMethod;
import com.liferay.bean.portlet.extension.BeanPortletMethodType;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

import java.lang.reflect.Method;

/**
 * @author Neil Griffin
 */
public class CDIBeanPortletMethod extends BaseBeanPortletMethod {

	public CDIBeanPortletMethod(
		Class<?> beanClass, BeanManager beanManager,
		BeanPortletMethodType beanPortletMethodType, Method method) {

		super(beanPortletMethodType, method);

		_beanClass = beanClass;
		_beanManager = beanManager;
	}

	@Override
	public Class<?> getBeanClass() {
		return _beanClass;
	}

	@Override
	public Object invoke(Object... args) throws ReflectiveOperationException {
		Bean<?> bean = _beanManager.resolve(_beanManager.getBeans(_beanClass));

		CreationalContext<?> creationalContext =
			_beanManager.createCreationalContext(bean);

		Object beanInstance = _beanManager.getReference(
			bean, bean.getBeanClass(), creationalContext);

		Method method = getMethod();

		return method.invoke(beanInstance, args);
	}

	private final Class<?> _beanClass;
	private final BeanManager _beanManager;

}