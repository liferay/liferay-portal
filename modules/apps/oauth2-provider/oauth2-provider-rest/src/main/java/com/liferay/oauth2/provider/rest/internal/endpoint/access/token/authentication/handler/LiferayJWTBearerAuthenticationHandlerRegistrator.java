/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.access.token.authentication.handler;

import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import jakarta.ws.rs.container.ContainerRequestFilter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(service = {})
public class LiferayJWTBearerAuthenticationHandlerRegistrator {

	@Activate
	protected void activate(BundleContext bundleContext) {
		LiferayJWTBearerAuthenticationHandler
			liferayJWTBearerAuthenticationHandler =
				new LiferayJWTBearerAuthenticationHandler();

		liferayJWTBearerAuthenticationHandler.setClientRegistrationProvider(
			_liferayOAuthDataProvider);

		_serviceRegistration = bundleContext.registerService(
			ContainerRequestFilter.class, liferayJWTBearerAuthenticationHandler,
			HashMapDictionaryBuilder.<String, Object>put(
				"osgi.jaxrs.application.select",
				"(osgi.jaxrs.name=Liferay.OAuth2.Application)"
			).put(
				"osgi.jaxrs.extension", true
			).put(
				"osgi.jaxrs.name", "Liferay.JWT.Bearer.Authentication.Handler"
			).build());
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();

			_serviceRegistration = null;
		}
	}

	@Reference
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	private volatile ServiceRegistration<ContainerRequestFilter>
		_serviceRegistration;

}