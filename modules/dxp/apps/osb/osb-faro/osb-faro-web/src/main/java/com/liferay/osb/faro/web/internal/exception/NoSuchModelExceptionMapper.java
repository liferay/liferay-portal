/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.exception;

import com.liferay.osb.faro.engine.client.model.ErrorResponse;
import com.liferay.portal.kernel.exception.NoSuchModelException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * @author André Miranda
 */
public class NoSuchModelExceptionMapper
	implements ExceptionMapper<NoSuchModelException> {

	@Override
	public Response toResponse(NoSuchModelException noSuchModelException) {
		Response.ResponseBuilder responseBuilder = Response.serverError();

		ErrorResponse errorResponse = new ErrorResponse();

		errorResponse.setMessage(noSuchModelException.getMessage());
		errorResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
		errorResponse.setTimestamp(System.currentTimeMillis());

		responseBuilder.entity(errorResponse);

		responseBuilder.status(Response.Status.BAD_REQUEST);
		responseBuilder.type(MediaType.APPLICATION_JSON_TYPE);

		return responseBuilder.build();
	}

}