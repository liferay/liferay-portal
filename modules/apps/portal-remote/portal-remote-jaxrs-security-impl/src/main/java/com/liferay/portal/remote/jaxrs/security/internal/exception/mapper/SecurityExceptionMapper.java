/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.jaxrs.security.internal.exception.mapper;

import com.liferay.portal.remote.jaxrs.security.internal.entity.ForbiddenEntity;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(!(liferay.access.control.disable=true))",
		"osgi.jaxrs.extension=true"
	},
	service = ExceptionMapper.class
)
public class SecurityExceptionMapper
	implements ExceptionMapper<SecurityException> {

	@Override
	public Response toResponse(SecurityException securityException) {
		MediaType mediaType = null;

		List<MediaType> mediaTypes = _httpHeaders.getAcceptableMediaTypes();

		for (MediaType currentMediaType : mediaTypes) {
			MessageBodyWriter<ForbiddenEntity> messageBodyWriter =
				_providers.getMessageBodyWriter(
					ForbiddenEntity.class, null, null, currentMediaType);

			if (messageBodyWriter != null) {
				mediaType = currentMediaType;

				break;
			}
		}

		if (mediaType == null) {
			mediaType = MediaType.APPLICATION_XML_TYPE;
		}

		return Response.status(
			Response.Status.FORBIDDEN
		).entity(
			new ForbiddenEntity(securityException)
		).type(
			mediaType
		).build();
	}

	@Context
	private HttpHeaders _httpHeaders;

	@Context
	private Providers _providers;

}