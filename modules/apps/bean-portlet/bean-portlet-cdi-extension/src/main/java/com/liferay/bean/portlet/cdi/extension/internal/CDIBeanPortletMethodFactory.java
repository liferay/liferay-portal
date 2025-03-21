/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal;

import com.liferay.bean.portlet.extension.BeanPortletMethod;
import com.liferay.bean.portlet.extension.BeanPortletMethodFactory;
import com.liferay.bean.portlet.extension.BeanPortletMethodType;

import jakarta.enterprise.inject.spi.BeanManager;

import java.lang.reflect.Method;

/**
 * @author Neil Griffin
 */
public class CDIBeanPortletMethodFactory implements BeanPortletMethodFactory {

	public CDIBeanPortletMethodFactory(BeanManager beanManager) {
		_beanManager = beanManager;
	}

	@Override
	public BeanPortletMethod create(
		Class<?> beanClass, BeanPortletMethodType beanPortletMethodType,
		Method method) {

		return new CDIBeanPortletMethod(
			beanClass, _beanManager, beanPortletMethodType, method);
	}

	private final BeanManager _beanManager;

}