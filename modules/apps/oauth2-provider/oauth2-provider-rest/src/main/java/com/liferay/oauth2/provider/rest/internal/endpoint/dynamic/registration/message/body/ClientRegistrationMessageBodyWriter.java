/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration.message.body;

import com.liferay.oauth2.provider.rest.internal.endpoint.authorize.message.body.BaseMessageBodyWriter;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.nio.charset.StandardCharsets;

import org.apache.cxf.rs.security.oauth2.services.ClientRegistration;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.OAuth2.Application)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=ClientRegistrationMessageBodyWriter"
	},
	service = MessageBodyWriter.class
)
@Produces("application/json")
@Provider
public class ClientRegistrationMessageBodyWriter
	extends BaseMessageBodyWriter<ClientRegistration> {

	@Override
	public boolean isWriteable(
		Class<?> aClass, Type type, Annotation[] annotations,
		MediaType mediaType) {

		if (aClass.isAssignableFrom(ClientRegistration.class) &&
			StringUtil.equalsIgnoreCase(mediaType.getType(), "application") &&
			StringUtil.equalsIgnoreCase(mediaType.getSubtype(), "json")) {

			return true;
		}

		return false;
	}

	@Override
	public void writeTo(
			ClientRegistration clientRegistration, Class<?> aClass, Type type,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> multivaluedMap,
			OutputStream outputStream)
		throws WebApplicationException {

		JSONObject clientRegistrationJSONObject = JSONUtil.put(
			"application_type", clientRegistration.getApplicationType()
		).put(
			"client_name", clientRegistration.getClientName()
		).put(
			"grant_types", clientRegistration.getGrantTypes()
		).put(
			"redirect_uris", clientRegistration.getRedirectUris()
		);

		if (Validator.isNotNull(clientRegistration.getScope())) {
			clientRegistrationJSONObject.put(
				"scopes", clientRegistration.getScope());
		}

		try {
			outputStream.write(
				clientRegistrationJSONObject.toString(
				).getBytes(
					StandardCharsets.UTF_8
				));
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Error writing response: ", ioException);
			}

			throw new WebApplicationException(
				Response.status(
					Response.Status.INTERNAL_SERVER_ERROR
				).build());
		}
	}

	@Override
	protected String writeTo(
		ClientRegistration clientRegistration, String authorizeScreenURL) {

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClientRegistrationMessageBodyWriter.class);

}