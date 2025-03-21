/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.resource;

import com.liferay.portal.vulcan.openapi.OpenAPIContext;
import com.liferay.portal.vulcan.openapi.OpenAPISchemaFilter;
import com.liferay.portal.vulcan.openapi.contributor.OpenAPIContributor;

import io.swagger.v3.oas.models.media.Schema;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.Set;

/**
 * @author Javier Gamarra
 */
public interface OpenAPIResource {

	public Response getOpenAPI(
			HttpServletRequest httpServletRequest,
			Set<Class<?>> resourceClasses, String type, UriInfo uriInfo)
		throws Exception;

	public default Response getOpenAPI(
			OpenAPIContributor openAPIContributor,
			OpenAPISchemaFilter openAPISchemaFilter,
			Set<Class<?>> resourceClasses, String type, UriInfo uriInfo)
		throws Exception {

		return null;
	}

	public default Response getOpenAPI(
			Set<Class<?>> resourceClasses, String type)
		throws Exception {

		return null;
	}

	public default Response getOpenAPI(
			Set<Class<?>> resourceClasses, String type, UriInfo uriInfo)
		throws Exception {

		return null;
	}

	public Map<String, Schema> getSchemas(Class<?> entityClass);

	public Map<String, Schema> getSchemas(Set<Class<?>> resourceClasses)
		throws Exception;

	public Response mergeOpenAPIs(
		String description, Map<OpenAPIContext, Response> openAPIResponses,
		String path, String title, String type);

}