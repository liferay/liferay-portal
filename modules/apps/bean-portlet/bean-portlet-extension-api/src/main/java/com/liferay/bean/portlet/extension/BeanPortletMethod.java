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
public interface BeanPortletMethod extends Comparable<BeanPortletMethod> {

	public String getActionName();

	public Class<?> getBeanClass();

	public BeanPortletMethodType getBeanPortletMethodType();

	public Method getMethod();

	public int getOrdinal();

	public PortletMode getPortletMode();

	public String getResourceID();

	public Object invoke(Object... arguments)
		throws ReflectiveOperationException;

	public boolean isEventProcessor(QName qName);

}