/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.jaxrs.exception.mapper;

import com.liferay.petra.string.StringUtil;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.util.List;

/**
 * @author Javier Gamarra
 */
public abstract class BaseExceptionMapper<T extends Throwable>
	implements ExceptionMapper<T> {

	@Override
	public Response toResponse(T exception) {
		Problem problem = getProblem(exception);

		if (problem.getThrowable() == null) {
			problem.setThrowable(exception);
		}

		String type = problem.getType();

		if (type != null) {
			String[] segments = type.split("\\.");

			String exceptionType = segments[segments.length - 1];

			if (exceptionType != null) {
				problem.setType(StringUtil.replace(exceptionType, '$', '.'));
			}
		}

		return Response.status(
			problem.getStatus()
		).entity(
			problem
		).type(
			getMediaType()
		).build();
	}

	protected MediaType getMediaType() {
		List<MediaType> mediaTypes = httpHeaders.getAcceptableMediaTypes();

		MediaType mediaType = mediaTypes.get(0);

		if (mediaType.equals(MediaType.valueOf(MediaType.TEXT_HTML)) ||
			mediaType.equals(MediaType.valueOf(MediaType.WILDCARD))) {

			return MediaType.valueOf(MediaType.APPLICATION_JSON);
		}

		return mediaType;
	}

	protected abstract Problem getProblem(T exception);

	@Context
	protected HttpHeaders httpHeaders;

}