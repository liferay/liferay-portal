/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.constraints.NotNull;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import java.util.Collection;
import java.util.List;

/**
 * @author Carlos Correa
 */
public abstract class BaseObjectEntryRelatedObjectsResourceImpl {

	@DELETE
	@Parameters(
		{
			@Parameter(in = ParameterIn.PATH, name = "currentObjectEntryId"),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(in = ParameterIn.PATH, name = "relatedObjectEntryId")
		}
	)
	@Path(
		"/{currentObjectEntryId}/{objectRelationshipName}/{relatedObjectEntryId}"
	)
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract void deleteCurrentObjectEntry(
			@NotNull @Parameter(hidden = true)
			@PathParam("currentObjectEntryId")
			Long currentObjectEntryId,
			@NotNull @Parameter(hidden = true)
			@PathParam("objectRelationshipName")
			String objectRelationshipName,
			@NotNull @Parameter(hidden = true)
			@PathParam("relatedObjectEntryId")
			Long relatedObjectEntryId)
		throws Exception;

	@GET
	@Parameters(
		{
			@Parameter(in = ParameterIn.PATH, name = "currentObjectEntryId"),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(in = ParameterIn.QUERY, name = "fields"),
			@Parameter(in = ParameterIn.QUERY, name = "nestedFields"),
			@Parameter(in = ParameterIn.QUERY, name = "page"),
			@Parameter(in = ParameterIn.QUERY, name = "pageSize"),
			@Parameter(in = ParameterIn.QUERY, name = "restrictFields")
		}
	)
	@Path("/{currentObjectEntryId}/{objectRelationshipName}")
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Page<Object>
			getCurrentObjectEntriesObjectRelationshipNamePage(
				@NotNull @Parameter(hidden = true)
				@PathParam("currentObjectEntryId")
				Long currentObjectEntryId,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName,
				@Context Pagination pagination)
		throws Exception;

	@Parameters(
		{
			@Parameter(in = ParameterIn.PATH, name = "currentObjectEntryId"),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(in = ParameterIn.PATH, name = "relatedObjectEntryId")
		}
	)
	@Path(
		"/{currentObjectEntryId}/{objectRelationshipName}/{relatedObjectEntryId}"
	)
	@Produces({"application/json", "application/xml"})
	@PUT
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Object putCurrentObjectEntry(
			@NotNull @Parameter(hidden = true)
			@PathParam("currentObjectEntryId")
			Long currentObjectEntryId,
			@NotNull @Parameter(hidden = true)
			@PathParam("objectRelationshipName")
			String objectRelationshipName,
			@NotNull @Parameter(hidden = true)
			@PathParam("relatedObjectEntryId")
			Long relatedObjectEntryId)
		throws Exception;

	protected <T, R, E extends Throwable> List<R> transform(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transform(collection, unsafeFunction);
	}

	protected AcceptLanguage contextAcceptLanguage;
	protected HttpServletRequest contextHttpServletRequest;
	protected UriInfo contextUriInfo;
	protected User contextUser;

}