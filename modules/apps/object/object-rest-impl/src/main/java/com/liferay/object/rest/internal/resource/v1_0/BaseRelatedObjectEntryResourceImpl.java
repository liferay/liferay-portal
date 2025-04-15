/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0;

import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;

import jakarta.validation.constraints.NotNull;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;

/**
 * @author Luis Miguel Barcos
 */
@Path("/v1.0")
public abstract class BaseRelatedObjectEntryResourceImpl {

	@DELETE
	@Path(
		"/{previousPath: [a-zA-Z0-9-]+}/{objectEntryId: \\d+}/{objectRelationshipName: [a-zA-Z0-9-]+}/{relatedObjectEntryId}"
	)
	@Produces({"application/json", "application/xml"})
	public void deleteObjectRelationshipMappingTableValues(
			@NotNull @Parameter(hidden = true) @PathParam("previousPath") String
				previousPath,
			@NotNull @Parameter(hidden = true) @PathParam("objectEntryId") Long
				objectEntryId,
			@NotNull @Parameter(hidden = true)
			@PathParam("objectRelationshipName")
			String objectRelationshipName,
			@PathParam("relatedObjectEntryId") Long relatedObjectEntryId)
		throws Exception {
	}

	@GET
	@Parameters(
		{
			@Parameter(in = ParameterIn.PATH, name = "previousPath"),
			@Parameter(in = ParameterIn.PATH, name = "objectEntryId"),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(in = ParameterIn.QUERY, name = "page"),
			@Parameter(in = ParameterIn.QUERY, name = "pageSize")
		}
	)
	@Path(
		"/{previousPath: [a-zA-Z0-9-]+}/{objectEntryId: \\d+}/{objectRelationshipName: [a-zA-Z0-9-]+}"
	)
	@Produces({"application/json", "application/xml"})
	@Tags({@Tag(name = "ObjectEntry")})
	public Page<Object> getRelatedObjectEntriesPage(
			@NotNull @Parameter(hidden = true) @PathParam("previousPath") String
				previousPath,
			@NotNull @Parameter(hidden = true) @PathParam("objectEntryId") Long
				objectEntryId,
			@NotNull @Parameter(hidden = true)
			@PathParam("objectRelationshipName")
			String objectRelationshipName,
			@Context Pagination pagination)
		throws Exception {

		return null;
	}

	@Path(
		"/{previousPath: [a-zA-Z0-9-]+}/{objectEntryId: \\d+}/{objectRelationshipName: [a-zA-Z0-9-]+}/{relatedObjectEntryId}"
	)
	@Produces({"application/json", "application/xml"})
	@PUT
	public ObjectEntry putObjectRelationshipMappingTableValues(
			@NotNull @Parameter(hidden = true) @PathParam("previousPath") String
				previousPath,
			@NotNull @Parameter(hidden = true) @PathParam("objectEntryId") Long
				objectEntryId,
			@NotNull @Parameter(hidden = true)
			@PathParam("objectRelationshipName")
			String objectRelationshipName,
			@PathParam("relatedObjectEntryId") Long relatedObjectEntryId,
			@Context Pagination pagination)
		throws Exception {

		return null;
	}

}