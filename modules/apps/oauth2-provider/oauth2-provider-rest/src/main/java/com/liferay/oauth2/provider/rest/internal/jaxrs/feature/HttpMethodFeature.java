/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.jaxrs.feature;

import com.liferay.oauth2.provider.rest.internal.scope.util.HttpMethodScopeLogicUtil;
import com.liferay.oauth2.provider.rest.spi.scope.checker.container.request.filter.BaseScopeCheckerContainerRequestFilter;
import com.liferay.oauth2.provider.scope.ScopeChecker;

import jakarta.annotation.Priority;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.ext.Provider;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(|(&(!(oauth2.scope.checker.type=*))(!(oauth2.scopechecker.type=*)))(|(oauth2.scope.checker.type=http.method)(oauth2.scopechecker.type=http.method)))",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.extension.select=(osgi.jaxrs.name=Liferay.OAuth2)",
		"osgi.jaxrs.name=Liferay.OAuth2.HTTP.method.request.checker"
	},
	scope = ServiceScope.PROTOTYPE, service = Feature.class
)
@Priority(Priorities.AUTHORIZATION - 8)
@Provider
public class HttpMethodFeature implements Feature {

	@Override
	public boolean configure(FeatureContext featureContext) {
		featureContext.register(
			new HttpScopeCheckerContainerRequestFilter(),
			Collections.singletonMap(
				ContainerRequestFilter.class, Priorities.AUTHORIZATION - 8));

		Configuration configuration = featureContext.getConfiguration();

		Map<String, Object> applicationProperties =
			(Map<String, Object>)configuration.getProperty(
				"osgi.jaxrs.application.serviceProperties");

		_propertyAccessorFunction = applicationProperties::get;

		return true;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;
	}

	private BundleContext _bundleContext;
	private Function<String, Object> _propertyAccessorFunction;

	@Reference
	private ScopeChecker _scopeChecker;

	private class HttpScopeCheckerContainerRequestFilter
		extends BaseScopeCheckerContainerRequestFilter {

		public boolean isContainerRequestContextAllowed(
			ContainerRequestContext containerRequestContext) {

			Request request = containerRequestContext.getRequest();

			return HttpMethodScopeLogicUtil.check(
				_bundleContext, _propertyAccessorFunction, _scopeChecker,
				request.getMethod());
		}

		@Context
		private ResourceInfo _resourceInfo;

	}

}