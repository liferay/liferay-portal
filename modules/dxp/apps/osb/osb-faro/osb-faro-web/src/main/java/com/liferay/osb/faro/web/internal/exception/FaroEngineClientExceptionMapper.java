/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.exception;

import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.model.ErrorResponse;
import com.liferay.osb.faro.web.internal.util.JSONUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * @author Matthew Kong
 */
public class FaroEngineClientExceptionMapper
	implements ExceptionMapper<FaroEngineClientException> {

	@Override
	public Response toResponse(
		FaroEngineClientException faroEngineClientException) {

		Response.ResponseBuilder responseBuilder = null;

		try {
			ErrorResponse errorResponse = null;

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				faroEngineClientException.getMessage());

			if (jsonObject.get("errorAttributes") != null) {
				JSONObject errorAttributesJSONObject =
					(JSONObject)jsonObject.get("errorAttributes");

				errorResponse = JSONUtil.readValue(
					errorAttributesJSONObject.toString(), ErrorResponse.class);
			}
			else {
				errorResponse = JSONUtil.readValue(
					faroEngineClientException.getMessage(),
					ErrorResponse.class);
			}

			if (errorResponse.getStatus() !=
					Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {

				responseBuilder = Response.serverError();

				ErrorResponse wrappedErrorResponse = new ErrorResponse();

				wrappedErrorResponse.setMessage(errorResponse.getMessage());
				wrappedErrorResponse.setMessageKey(
					errorResponse.getMessageKey());
				wrappedErrorResponse.setStatus(
					Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
				wrappedErrorResponse.setTimestamp(System.currentTimeMillis());

				responseBuilder.entity(wrappedErrorResponse);
			}
			else {
				responseBuilder = Response.status(errorResponse.getStatus());

				responseBuilder.entity(errorResponse);
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			responseBuilder = Response.serverError();
		}

		return responseBuilder.build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroEngineClientExceptionMapper.class);

}