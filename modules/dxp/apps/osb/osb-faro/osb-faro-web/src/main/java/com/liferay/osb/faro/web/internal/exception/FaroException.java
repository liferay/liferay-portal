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
 * @author Matthew Kong
 */
public class FaroException extends WebApplicationException {

	public FaroException() {
	}

	public FaroException(String message) {
		this(message, Response.Status.BAD_REQUEST);
	}

	public FaroException(String message, Response.StatusType statusType) {
		super(getResponse(message, statusType));
	}

	protected static Response getResponse(
		String message, Response.StatusType statusType) {

		Response.ResponseBuilder responseBuilder = Response.status(statusType);

		ErrorResponse errorResponse = new ErrorResponse();

		errorResponse.setError(statusType.getReasonPhrase());
		errorResponse.setStatus(statusType.getStatusCode());
		errorResponse.setMessage(message);

		responseBuilder.entity(errorResponse);

		responseBuilder.type(MediaType.APPLICATION_JSON_TYPE);

		return responseBuilder.build();
	}

}