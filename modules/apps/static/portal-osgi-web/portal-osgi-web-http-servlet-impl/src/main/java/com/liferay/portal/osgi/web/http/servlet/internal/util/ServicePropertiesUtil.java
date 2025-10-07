/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.util;

import jakarta.servlet.ServletContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.ServiceReference;

/**
 * @author Dante Wang
 */
public class ServicePropertiesUtil {

	public static Map<String, String> parseInitParams(
		ServiceReference<?> serviceReference, String prefix) {

		return parseInitParams(serviceReference, prefix, null);
	}

	public static Map<String, String> parseInitParams(
		ServiceReference<?> serviceReference, String propertyKeyPrefix,
		ServletContext parentServletContext) {

		Map<String, String> initParameters = new HashMap<>();

		if (parentServletContext != null) {
			Enumeration<String> initParameterNamesEnumeration =
				parentServletContext.getInitParameterNames();

			while (initParameterNamesEnumeration.hasMoreElements()) {
				String initParameterName =
					initParameterNamesEnumeration.nextElement();

				initParameters.put(
					initParameterName,
					parentServletContext.getInitParameter(initParameterName));
			}
		}

		for (String propertyKey : serviceReference.getPropertyKeys()) {
			if (propertyKey.startsWith(propertyKeyPrefix)) {
				initParameters.put(
					propertyKey.substring(propertyKeyPrefix.length()),
					String.valueOf(serviceReference.getProperty(propertyKey)));
			}
		}

		return Collections.unmodifiableMap(initParameters);
	}

	public static String parseName(Object property, Object object) {
		if (property == null) {
			Class<?> clazz = object.getClass();

			return clazz.getName();
		}

		return String.valueOf(property);
	}

}