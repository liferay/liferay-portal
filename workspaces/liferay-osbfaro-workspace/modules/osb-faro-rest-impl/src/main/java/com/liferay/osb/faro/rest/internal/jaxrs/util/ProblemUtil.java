/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.util;

import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * @author Marcos Martins
 */
public class ProblemUtil {

	public static ForbiddenException getForbiddenException(String title) {
		return new ForbiddenException(
			title, toResponse(Response.Status.FORBIDDEN, title));
	}

	public static NotFoundException getNotFoundException(String title) {
		return new NotFoundException(
			title, toResponse(Response.Status.NOT_FOUND, title));
	}

	public static Response toResponse(Response.Status status, String title) {
		return Response.status(
			status
		).entity(
			new Problem(status, title)
		).type(
			MediaType.APPLICATION_JSON
		).build();
	}

}