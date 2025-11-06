/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration.message.body;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.liferay.oauth2.provider.rest.internal.endpoint.dynamic.registration.model.LiferayClientRegistration;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.InputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.OAuth2.Application)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=ClientRegistrationMessageBodyReader"
	},
	service = MessageBodyReader.class
)
@Consumes(MediaType.APPLICATION_JSON)
@Provider
public class ClientRegistrationMessageBodyReader
	implements MessageBodyReader<LiferayClientRegistration> {

	@Override
	public boolean isReadable(
		Class<?> clazz, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		return true;
	}

	@Override
	public LiferayClientRegistration readFrom(
			Class<LiferayClientRegistration> clazz, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> multivaluedMap,
			InputStream inputStream)
		throws IOException {

		ObjectMapper objectMapper = ObjectMapperHolder._objectMapper;

		ObjectReader objectReader = objectMapper.readerFor(clazz);

		JsonNode jsonNode = objectReader.readTree(inputStream);

		objectReader = objectReader.without(
			DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		return objectReader.readValue(jsonNode);
	}

	private static class ObjectMapperHolder {

		private static final ObjectMapper _objectMapper = new ObjectMapper() {
			{
				configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			}
		};

	}

}