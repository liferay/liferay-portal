/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.resource;

import com.liferay.portal.vulcan.resource.OpenAPIResource;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Carlos Correa
 */
public class OpenAPIResourceImpl {

	public OpenAPIResourceImpl(OpenAPIResource openAPIResource) {
		_openAPIResource = openAPIResource;
	}

	@GET
	@Path("/openapi.{type:json|yaml}")
	@Produces({MediaType.APPLICATION_JSON, "application/yaml"})
	public Response getOpenAPI(
			@Context HttpServletRequest httpServletRequest,
			@PathParam("type") String type, @Context UriInfo uriInfo)
		throws Exception {

		return _openAPIResource.getOpenAPI(
			httpServletRequest, _resourceClasses, type, uriInfo);
	}

	private final OpenAPIResource _openAPIResource;
	private final Set<Class<?>> _resourceClasses = new HashSet<Class<?>>() {
		{
			add(OpenAPIResourceImpl.class);
		}
	};

}