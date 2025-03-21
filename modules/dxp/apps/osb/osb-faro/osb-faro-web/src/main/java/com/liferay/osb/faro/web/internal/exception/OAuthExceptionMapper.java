/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.exception;

import com.github.scribejava.core.exceptions.OAuthException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * @author Shinn Lok
 */
public class OAuthExceptionMapper implements ExceptionMapper<OAuthException> {

	@Override
	public Response toResponse(OAuthException oAuthException) {
		Response.ResponseBuilder responseBuilder = Response.serverError();

		responseBuilder.entity(oAuthException);

		return responseBuilder.build();
	}

}