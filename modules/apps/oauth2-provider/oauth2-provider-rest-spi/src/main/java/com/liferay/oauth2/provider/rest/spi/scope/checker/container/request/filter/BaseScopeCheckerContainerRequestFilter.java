/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.spi.scope.checker.container.request.filter;

import com.liferay.oauth2.provider.scope.liferay.OAuth2ProviderScopeLiferayAccessControlContext;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;

/**
 * @author Marta Medio
 */
public abstract class BaseScopeCheckerContainerRequestFilter
	implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		if (OAuth2ProviderScopeLiferayAccessControlContext.
				isOAuth2AuthVerified() &&
			!isContainerRequestContextAllowed(containerRequestContext)) {

			containerRequestContext.abortWith(
				Response.status(
					Response.Status.FORBIDDEN
				).build());
		}
	}

	protected abstract boolean isContainerRequestContextAllowed(
		ContainerRequestContext containerRequestContext);

}