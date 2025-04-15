/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.cors.internal.jaxrs.feature;

import com.liferay.oauth2.provider.scope.liferay.OAuth2ProviderScopeLiferayAccessControlContext;
import com.liferay.petra.reflect.AnnotationLocator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.remote.cors.annotation.CORS;
import com.liferay.portal.remote.cors.internal.CORSSupport;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(liferay.cors.annotation=true)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.CORS.Annotation.Extension"
	},
	scope = ServiceScope.PROTOTYPE, service = DynamicFeature.class
)
public class CORSAnnotationDynamicFeature implements DynamicFeature {

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		CORS cors = _getCORS(resourceInfo);

		if (cors != null) {
			CORSSupport corsSupport = _getCORSSupport(cors);

			context.register(
				new CORSPreflighContainerRequestFilter(corsSupport));

			context.register(new CORSContainerRequestFilter(corsSupport));
		}
	}

	private CORS _getCORS(ResourceInfo resourceInfo) {
		return AnnotationLocator.locate(
			resourceInfo.getResourceMethod(), resourceInfo.getResourceClass(),
			CORS.class);
	}

	private CORSSupport _getCORSSupport(CORS cors) {
		CORSSupport corsSupport = new CORSSupport();

		corsSupport.setCORSHeaders(
			HashMapBuilder.put(
				CORSSupport.ACCESS_CONTROL_ALLOW_CREDENTIALS,
				String.valueOf(cors.allowCredentials())
			).put(
				CORSSupport.ACCESS_CONTROL_ALLOW_HEADERS,
				StringUtil.merge(cors.allowHeaders(), StringPool.COMMA)
			).put(
				CORSSupport.ACCESS_CONTROL_ALLOW_METHODS,
				StringUtil.merge(cors.allowMethods(), StringPool.COMMA)
			).put(
				CORSSupport.ACCESS_CONTROL_ALLOW_ORIGIN, cors.allowOrigin()
			).put(
				CORSSupport.ACCESS_CONTROL_EXPOSE_HEADERS,
				StringUtil.merge(cors.exposeHeaders(), StringPool.COMMA)
			).put(
				CORSSupport.ACCESS_CONTROL_MAX_AGE,
				String.valueOf(cors.maxAge())
			).build());

		return corsSupport;
	}

	@Context
	private ResourceInfo _resourceInfo;

	private class CORSContainerRequestFilter
		implements ContainerResponseFilter {

		public CORSContainerRequestFilter(CORSSupport corsSupport) {
			_corsSupport = corsSupport;
		}

		@Override
		public void filter(
				ContainerRequestContext containerRequestContext,
				ContainerResponseContext containerResponseContext)
			throws IOException {

			MultivaluedMap<String, String> headers =
				containerRequestContext.getHeaders();

			if (_corsSupport.isCORSRequest(headers::getFirst) &&
				_corsSupport.isValidCORSRequest(
					containerRequestContext.getMethod(), headers::getFirst) &&
				(OAuth2ProviderScopeLiferayAccessControlContext.
					isOAuth2AuthVerified() ||
				 _isGuestUser())) {

				MultivaluedMap<String, Object> responseHeaders =
					containerResponseContext.getHeaders();

				_corsSupport.writeResponseHeaders(
					headers::getFirst, responseHeaders::addFirst);
			}
		}

		private boolean _isGuestUser() {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (permissionChecker == null) {
				return true;
			}

			User user = permissionChecker.getUser();

			return user.isGuestUser();
		}

		private final CORSSupport _corsSupport;

	}

	@PreMatching
	private class CORSPreflighContainerRequestFilter
		implements ContainerRequestFilter {

		public CORSPreflighContainerRequestFilter(CORSSupport corsSupport) {
			_corsSupport = corsSupport;
		}

		@Override
		public void filter(ContainerRequestContext containerRequestContext)
			throws IOException {

			MultivaluedMap<String, String> headers =
				containerRequestContext.getHeaders();

			if (_corsSupport.isCORSRequest(headers::getFirst) &&
				StringUtil.equals(
					containerRequestContext.getMethod(), HttpMethod.OPTIONS) &&
				_corsSupport.isValidCORSPreflightRequest(headers::getFirst)) {

				Response.ResponseBuilder responseBuilder = Response.ok();

				_corsSupport.writeResponseHeaders(
					headers::getFirst, responseBuilder::header);

				containerRequestContext.abortWith(responseBuilder.build());
			}
		}

		private final CORSSupport _corsSupport;

	}

}