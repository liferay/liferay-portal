/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.jwks;

import com.liferay.oauth2.provider.rest.internal.configuration.OAuth2AuthorizationServerConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Map;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.oauth2.services.JwksService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * @author Raymond Augé
 * @author Arthur Chan
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.rest.internal.configuration.OAuth2AuthorizationServerConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.OAuth2.Application)",
		"osgi.jaxrs.name=Liferay.Authorization.JWKS", "osgi.jaxrs.resource=true"
	},
	service = LiferayJWKSService.class
)
@Path("/jwks")
public class LiferayJWKSService extends JwksService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response jwks() {

		// TODO Replace with JwksService#getPublicVerificationKeys

		return Response.ok(
			JwkUtils.jwkSetToJson(_jsonWebKeys)
		).build();
	}

	@Activate
	protected void activate(Map<String, Object> properties) throws Exception {
		OAuth2AuthorizationServerConfiguration
			oAuth2AuthorizationServerConfiguration =
				ConfigurableUtil.createConfigurable(
					OAuth2AuthorizationServerConfiguration.class, properties);

		_jsonWebKeys = new JsonWebKeys(
			JwkUtils.stripPrivateParameters(
				Collections.singletonList(
					JwkUtils.readJwkKey(
						oAuth2AuthorizationServerConfiguration.
							jwtAccessTokenSigningJSONWebKey()))));
	}

	private JsonWebKeys _jsonWebKeys;

}