/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.core.exception.mapper;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * @author Igor Beslic
 * @author Ivica Cardic
 * @author Zoltán Takács
 */
public abstract class BaseExceptionMapper<E extends Throwable>
	implements ExceptionMapper<E> {

	public abstract String getErrorDescription();

	public abstract Response.Status getStatus();

	@Override
	public Response toResponse(E e) {
		if (_log.isDebugEnabled()) {
			_log.debug("General REST exception", e);
		}

		Response.ResponseBuilder responseBuilder = Response.status(getStatus());

		Response.Status status = getStatus();

		responseBuilder.entity(toJSON(e, status.getStatusCode()));

		responseBuilder.type(MediaType.APPLICATION_JSON_TYPE);

		return responseBuilder.build();
	}

	protected String toJSON(E e, int status) {
		return toJSON(e.getMessage(), status);
	}

	protected String toJSON(String message, int status, Object... args) {
		StringBundler sb = new StringBundler();

		sb.append("{\"errorDescription\": \"");
		sb.append(getErrorDescription());
		sb.append("\", \"message\": \"");
		sb.append(message);
		sb.append("\", \"status\": ");
		sb.append(status);

		if (args.length == 0) {
			sb.append("}");

			return sb.toString();
		}

		sb.append(", \"args\": {");

		for (int i = 0; i < (args.length - 1); i = i + 2) {
			sb.append("\"");
			sb.append(args[i]);
			sb.append("\":\"");
			sb.append(args[i + 1]);
			sb.append("\"");

			if ((i + 2) < args.length) {
				sb.append(", ");
			}
		}

		sb.append("}}");

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseExceptionMapper.class);

}