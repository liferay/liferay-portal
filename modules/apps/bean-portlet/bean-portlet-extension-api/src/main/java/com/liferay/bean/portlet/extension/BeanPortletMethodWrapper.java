/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.extension;

import jakarta.portlet.PortletMode;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Neil Griffin
 */
@ProviderType
public abstract class BeanPortletMethodWrapper implements BeanPortletMethod {

	public BeanPortletMethodWrapper(BeanPortletMethod beanPortletMethod) {
		_beanPortletMethod = beanPortletMethod;
	}

	@Override
	public int compareTo(BeanPortletMethod beanPortletMethod) {
		return _beanPortletMethod.compareTo(beanPortletMethod);
	}

	@Override
	public String getActionName() {
		return _beanPortletMethod.getActionName();
	}

	@Override
	public Class<?> getBeanClass() {
		return _beanPortletMethod.getBeanClass();
	}

	@Override
	public BeanPortletMethodType getBeanPortletMethodType() {
		return _beanPortletMethod.getBeanPortletMethodType();
	}

	@Override
	public Method getMethod() {
		return _beanPortletMethod.getMethod();
	}

	@Override
	public int getOrdinal() {
		return _beanPortletMethod.getOrdinal();
	}

	@Override
	public PortletMode getPortletMode() {
		return _beanPortletMethod.getPortletMode();
	}

	@Override
	public String getResourceID() {
		return _beanPortletMethod.getResourceID();
	}

	public BeanPortletMethod getWrapped() {
		return _beanPortletMethod;
	}

	@Override
	public Object invoke(Object... arguments)
		throws ReflectiveOperationException {

		return _beanPortletMethod.invoke(arguments);
	}

	@Override
	public boolean isEventProcessor(QName qName) {
		return _beanPortletMethod.isEventProcessor(qName);
	}

	private final BeanPortletMethod _beanPortletMethod;

}