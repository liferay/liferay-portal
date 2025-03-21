/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.response.filter;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import com.liferay.petra.io.DummyOutputStream;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.fields.FieldsQueryParam;
import com.liferay.portal.vulcan.fields.RestrictFieldsQueryParam;
import com.liferay.portal.vulcan.jackson.databind.ser.VulcanPropertyFilter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import java.io.IOException;

import java.util.Map;

/**
 * @author Carlos Correa
 */
@Provider
public class EntityFieldsPreSerializerContainerResponseFilter
	implements ContainerResponseFilter {

	@Override
	public void filter(
			ContainerRequestContext containerRequestContext,
			ContainerResponseContext containerResponseContext)
		throws IOException {

		MediaType mediaType = containerResponseContext.getMediaType();

		if ((containerResponseContext.getEntity() == null) ||
			!_mediaTypeObjectMappers.containsKey(mediaType)) {

			return;
		}

		ContextResolver<ObjectMapper> contextResolver =
			_providers.getContextResolver(
				(Class<ObjectMapper>)_mediaTypeObjectMappers.get(mediaType),
				mediaType);

		ObjectMapper objectMapper = contextResolver.getContext(
			ObjectMapper.class);

		try {
			objectMapper.writer(
				_getSimpleFilterProvider()
			).writeValue(
				new DummyOutputStream(), containerResponseContext.getEntity()
			);
		}
		catch (JsonMappingException jsonMappingException) {
			if (jsonMappingException.getCause() != null) {
				throw new RuntimeException(jsonMappingException.getCause());
			}

			throw jsonMappingException;
		}
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

	private static final Map<MediaType, Class<? extends ObjectMapper>>
		_mediaTypeObjectMappers =
			HashMapBuilder.<MediaType, Class<? extends ObjectMapper>>put(
				MediaType.APPLICATION_JSON_TYPE, ObjectMapper.class
			).put(
				MediaType.APPLICATION_XML_TYPE, XmlMapper.class
			).build();

	@Context
	private FieldsQueryParam _fieldsQueryParam;

	@Context
	private Providers _providers;

	@Context
	private RestrictFieldsQueryParam _restrictFieldsQueryParam;

}