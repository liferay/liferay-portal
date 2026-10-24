/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.scope.util;

import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.osgi.util.StringPlus;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Valmir Junior
 */
public class HttpMethodScopeLogicUtil {

	public static boolean check(
		BundleContext bundleContext,
		Function<String, Object> propertyAccessorFunction,
		ScopeChecker scopeChecker, String requestMethod) {

		try {
			String applicationName = GetterUtil.getString(
				propertyAccessorFunction.apply("osgi.jaxrs.name"));

			Object ignoreMissingScopesObject = propertyAccessorFunction.apply(
				"ignore.missing.scopes");

			Set<String> ignoreMissingScopes = _defaultIgnoreMissingScopes;

			if (ignoreMissingScopesObject != null) {
				ignoreMissingScopes = new HashSet<>(
					StringPlus.asList(ignoreMissingScopesObject));
			}

			ServiceReference<? extends ScopeFinder> serviceReference =
				_getServiceReference(
					bundleContext, ScopeFinder.class, applicationName);

			ScopeFinder scopeFinder = bundleContext.getService(
				serviceReference);

			try {
				Collection<String> scopes = scopeFinder.findScopes();

				if ((!scopes.contains(requestMethod) &&
					 ignoreMissingScopes.contains(requestMethod)) ||
					scopeChecker.checkScope(requestMethod)) {

					return true;
				}
			}
			finally {
				bundleContext.ungetService(serviceReference);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return false;
	}

	private static <T> ServiceReference<? extends T> _getServiceReference(
			BundleContext bundleContext, Class<? extends T> clazz,
			String applicationName)
		throws Exception {

		List<ServiceReference<T>> serviceReferences =
			(List<ServiceReference<T>>)bundleContext.<T>getServiceReferences(
				(Class<T>)clazz, "(osgi.jaxrs.name=" + applicationName + ")");

		if (ListUtil.isNotEmpty(serviceReferences)) {
			return serviceReferences.get(0);
		}

		throw new UnsupportedOperationException(
			"Invalid JAX-RS application " + applicationName);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HttpMethodScopeLogicUtil.class);

	private static final Set<String> _defaultIgnoreMissingScopes =
		new HashSet<>(Arrays.asList("HEAD", "OPTIONS"));

}