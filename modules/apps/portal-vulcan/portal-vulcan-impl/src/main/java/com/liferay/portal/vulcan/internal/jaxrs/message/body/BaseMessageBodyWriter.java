/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.message.body;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import com.liferay.portal.vulcan.fields.FieldsQueryParam;
import com.liferay.portal.vulcan.fields.RestrictFieldsQueryParam;
import com.liferay.portal.vulcan.jackson.databind.ser.VulcanPropertyFilter;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author Alejandro Hernández
 * @author Ivica Cardic
 */
public abstract class BaseMessageBodyWriter
	implements MessageBodyWriter<Object> {

	public BaseMessageBodyWriter(
		Class<? extends ObjectMapper> contextType, MediaType mediaType) {

		_contextType = contextType;
		_mediaType = mediaType;
	}

	@Override
	public boolean isWriteable(
		Class<?> clazz, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		ObjectMapper objectMapper = _getObjectMapper(clazz);

		return objectMapper.canSerialize(clazz);
	}

	@Override
	public void writeTo(
			Object object, Class<?> clazz, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> multivaluedMap,
			OutputStream outputStream)
		throws IOException, WebApplicationException {

		ObjectMapper objectMapper = _getObjectMapper(clazz);

		objectMapper.writer(
			_getSimpleFilterProvider()
		).writeValue(
			outputStream, object
		);

		outputStream.flush();
	}

	private ObjectMapper _getObjectMapper(Class<?> clazz) {
		ContextResolver<? extends ObjectMapper> contextResolver =
			_providers.getContextResolver(_contextType, _mediaType);

		if (contextResolver != null) {
			ObjectMapper objectMapper = contextResolver.getContext(clazz);

			if (objectMapper != null) {
				return objectMapper;
			}
		}

		throw new InternalServerErrorException(
			"Unable to generate object mapper for class " + clazz);
	}

	private SimpleFilterProvider _getSimpleFilterProvider() {
		return new SimpleFilterProvider() {
			{
				addFilter(
					"Liferay.Vulcan",
					VulcanPropertyFilter.of(
						_fieldsQueryParam.getFieldNames(),
						_restrictFieldsQueryParam.getRestrictFieldNames()));
			}
		};
	}

	private final Class<? extends ObjectMapper> _contextType;

	@Context
	private FieldsQueryParam _fieldsQueryParam;

	private final MediaType _mediaType;

	@Context
	private Providers _providers;

	@Context
	private RestrictFieldsQueryParam _restrictFieldsQueryParam;

}