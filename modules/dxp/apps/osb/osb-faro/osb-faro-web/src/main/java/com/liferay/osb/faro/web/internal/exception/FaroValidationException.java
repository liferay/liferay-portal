/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.exception;

import com.liferay.osb.faro.engine.client.model.ErrorResponse;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @author André Miranda
 */
public class FaroValidationException extends WebApplicationException {

	public FaroValidationException() {
	}

	public FaroValidationException(String field, String localizedMessage) {
		this(field, localizedMessage, null);
	}

	public FaroValidationException(
		String field, String localizedMessage, String message) {

		this(field, localizedMessage, message, Response.Status.BAD_REQUEST);
	}

	public FaroValidationException(
		String field, String localizedMessage, String message,
		Response.StatusType statusType) {

		super(getResponse(field, localizedMessage, message, statusType));
	}

	protected static Response getResponse(
		String field, String localizedMessage, String message,
		Response.StatusType statusType) {

		Response.ResponseBuilder responseBuilder = Response.status(statusType);

		ErrorResponse errorResponse = new ErrorResponse();

		errorResponse.setError(statusType.getReasonPhrase());
		errorResponse.setField(field);
		errorResponse.setLocalizedMessage(localizedMessage);
		errorResponse.setStatus(statusType.getStatusCode());
		errorResponse.setMessage(message);

		responseBuilder.entity(errorResponse);

		responseBuilder.type(MediaType.APPLICATION_JSON_TYPE);

		return responseBuilder.build();
	}

}