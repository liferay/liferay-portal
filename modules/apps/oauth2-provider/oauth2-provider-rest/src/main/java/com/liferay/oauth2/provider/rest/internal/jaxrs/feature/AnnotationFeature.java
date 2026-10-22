/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.jaxrs.feature;

import com.liferay.oauth2.provider.rest.internal.scope.logic.AnnotationScopeLogic;
import com.liferay.oauth2.provider.rest.internal.scope.logic.ScopeLogic;
import com.liferay.oauth2.provider.rest.spi.scope.checker.container.request.filter.BaseScopeCheckerContainerRequestFilter;
import com.liferay.oauth2.provider.scope.ScopeChecker;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(|(oauth2.scope.checker.type=annotations)(oauth2.scopechecker.type=annotations))",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.extension.select=(osgi.jaxrs.name=Liferay.OAuth2)",
		"osgi.jaxrs.name=Liferay.OAuth2.annotations.feature"
	},
	scope = ServiceScope.PROTOTYPE, service = Feature.class
)
@Provider
public class AnnotationFeature implements Feature {

	@Override
	public boolean configure(FeatureContext featureContext) {
		featureContext.register(
			new AnnotationContainerScopeCheckerContainerRequestFilter(),
			Priorities.AUTHORIZATION - 8);

		Configuration configuration = featureContext.getConfiguration();

		Map<String, Object> applicationProperties =
			(Map<String, Object>)configuration.getProperty(
				"osgi.jaxrs.application.serviceProperties");

		_propertyAccessorFunction = applicationProperties::get;

		return true;
	}

	private Function<String, Object> _propertyAccessorFunction;

	@Reference
	private ScopeChecker _scopeChecker;

	private final ScopeLogic _scopeLogic = new AnnotationScopeLogic();

	private class AnnotationContainerScopeCheckerContainerRequestFilter
		extends BaseScopeCheckerContainerRequestFilter {

		public boolean isContainerRequestContextAllowed(
			ContainerRequestContext containerRequestContext) {

			return _scopeLogic.check(
				_propertyAccessorFunction, _resourceInfo.getResourceClass(),
				_resourceInfo.getResourceMethod(), _scopeChecker);
		}

		@Context
		private ResourceInfo _resourceInfo;

	}

}