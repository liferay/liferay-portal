/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.core.util;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.UriInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.List;

/**
 * @author Luca Pellizzon
 */
public class ActionUtil {

	public static String getHttpMethodName(Class<?> clazz, Method method)
		throws Exception {

		Class<?> superClass = clazz.getSuperclass();

		Method superMethod = superClass.getMethod(
			method.getName(), method.getParameterTypes());

		for (Annotation annotation : superMethod.getAnnotations()) {
			Class<? extends Annotation> annotationType =
				annotation.annotationType();

			Annotation[] annotations = annotationType.getAnnotationsByType(
				HttpMethod.class);

			if (annotations.length > 0) {
				HttpMethod httpMethod = (HttpMethod)annotations[0];

				return httpMethod.value();
			}
		}

		return null;
	}

	public static Method getMethod(Class<?> clazz, String methodName) {
		for (Method method : clazz.getMethods()) {
			if (methodName.equals(method.getName())) {
				return method;
			}
		}

		return null;
	}

	public static String getVersion(UriInfo uriInfo) {
		List<String> matchedURIs = uriInfo.getMatchedURIs();

		if (matchedURIs.isEmpty()) {
			return "";
		}

		return matchedURIs.get(matchedURIs.size() - 1);
	}

}