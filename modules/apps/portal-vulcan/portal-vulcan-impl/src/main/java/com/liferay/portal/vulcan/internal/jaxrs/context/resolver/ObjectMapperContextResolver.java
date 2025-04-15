/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.liferay.portal.vulcan.internal.jaxrs.serializer.OpenAPIJsonSerializer;
import com.liferay.portal.vulcan.jackson.databind.ObjectMapperProviderUtil;

import io.swagger.v3.oas.models.OpenAPI;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import java.util.Set;

/**
 * @author Javier Gamarra
 */
@Provider
public class ObjectMapperContextResolver
	implements ContextResolver<ObjectMapper> {

	@Override
	public ObjectMapper getContext(Class<?> clazz) {
		ObjectMapper objectMapper = ObjectMapperProviderUtil.getObjectMapper();

		Set<Object> registeredModuleIds = objectMapper.getRegisteredModuleIds();

		if (!registeredModuleIds.contains(_simpleModule.getModuleName())) {
			objectMapper.registerModule(_simpleModule);
		}

		return objectMapper;
	}

	private final SimpleModule _simpleModule = new SimpleModule() {
		{
			addSerializer(OpenAPI.class, new OpenAPIJsonSerializer());
		}
	};

}