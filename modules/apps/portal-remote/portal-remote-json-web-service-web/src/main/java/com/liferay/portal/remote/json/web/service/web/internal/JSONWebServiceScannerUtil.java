/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Miguel Pastor
 */
public class JSONWebServiceScannerUtil {

	public static Method[] scan(Object service) {
		Class<?> clazz = JSONWebServiceActionsManagerImpl.getTargetClass(
			service);

		Method[] methods = clazz.getMethods();

		List<Method> serviceMethods = new ArrayList<>(methods.length);

		for (Method method : methods) {
			Class<?> declaringClass = method.getDeclaringClass();

			if (declaringClass != clazz) {
				continue;
			}

			serviceMethods.add(method);
		}

		return serviceMethods.toArray(new Method[0]);
	}

}