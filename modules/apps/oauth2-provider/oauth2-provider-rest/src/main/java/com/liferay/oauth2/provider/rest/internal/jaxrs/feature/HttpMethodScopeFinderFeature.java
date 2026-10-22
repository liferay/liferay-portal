/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.jaxrs.feature;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.ws.rs.HttpMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Set;

import org.apache.cxf.jaxrs.model.OperationResourceInfo;

import org.osgi.framework.BundleContext;

/**
 * @author Shuyang Zhou
 */
public class HttpMethodScopeFinderFeature extends BaseScopeFinderFeature {

	public HttpMethodScopeFinderFeature(BundleContext bundleContext) {
		super(bundleContext);
	}

	@Override
	protected void collectScopes(
		OperationResourceInfo operationResourceInfo, Set<String> scopes) {

		Method method = operationResourceInfo.getMethodToInvoke();

		while (method != null) {
			for (Annotation annotation : method.getAnnotations()) {
				Class<? extends Annotation> annotationType =
					annotation.annotationType();

				HttpMethod[] annotationsByType =
					annotationType.getAnnotationsByType(HttpMethod.class);

				if (annotationsByType == null) {
					continue;
				}

				for (HttpMethod httpMethod : annotationsByType) {
					scopes.add(httpMethod.value());
				}
			}

			method = _getSuperMethod(method);
		}
	}

	private Method _getSuperMethod(Method method) {
		Class<?> clazz = method.getDeclaringClass();

		clazz = clazz.getSuperclass();

		if (clazz == Object.class) {
			return null;
		}

		try {
			return clazz.getDeclaredMethod(
				method.getName(), method.getParameterTypes());
		}
		catch (NoSuchMethodException noSuchMethodException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchMethodException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HttpMethodScopeFinderFeature.class);

}