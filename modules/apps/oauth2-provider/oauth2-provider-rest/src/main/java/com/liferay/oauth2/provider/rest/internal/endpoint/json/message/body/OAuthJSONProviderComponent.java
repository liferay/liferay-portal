/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.json.message.body;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.apache.cxf.rs.security.oauth2.provider.OAuthJSONProvider;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		Constants.SERVICE_RANKING + ":Integer=" + (Integer.MAX_VALUE - 10),
		"osgi.jaxrs.application.select=(component.name=com.liferay.oauth2.provider.rest.internal.endpoint.OAuth2EndpointApplication)",
		"osgi.jaxrs.extension=true", "osgi.jaxrs.name=OAuthJSONProvider"
	},
	scope = ServiceScope.PROTOTYPE,
	service = {MessageBodyReader.class, MessageBodyWriter.class}
)
@Consumes("application/json")
@Produces("application/json")
@Provider
public class OAuthJSONProviderComponent extends OAuthJSONProvider {
}