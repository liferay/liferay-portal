/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0;

import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.constraints.NotNull;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import java.util.Collection;
import java.util.List;

/**
 * @author Carlos Correa
 */
public abstract class BaseObjectEntryRelatedObjectsResourceImpl
	implements EntityModelResource {

	@DELETE
	@Operation(
		operationId = "deleteByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode"
	)
	@Parameters(
		{
			@Parameter(
				in = ParameterIn.PATH, name = "currentExternalReferenceCode"
			),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(
				in = ParameterIn.PATH, name = "relatedExternalReferenceCode"
			)
		}
	)
	@Path(
		"/by-external-reference-code/{currentExternalReferenceCode}/{objectRelationshipName}/{relatedExternalReferenceCode}"
	)
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract void
			deleteByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				@NotNull @Parameter(hidden = true)
				@PathParam("currentExternalReferenceCode")
				String currentExternalReferenceCode,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName,
				@NotNull @Parameter(hidden = true)
				@PathParam("relatedExternalReferenceCode")
				String relatedExternalReferenceCode)
		throws Exception;

	@DELETE
	@Operation(
		operationId = "deleteObjectEntryObjectRelationshipNameRelatedObjectEntry"
	)
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

	@DELETE
	@Operation(
		operationId = "deleteScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode"
	)
	@Parameters(
		{
			@Parameter(in = ParameterIn.PATH, name = "scopeKey"),
			@Parameter(
				in = ParameterIn.PATH, name = "currentExternalReferenceCode"
			),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(
				in = ParameterIn.PATH, name = "relatedExternalReferenceCode"
			)
		}
	)
	@Path(
		"/scopes/{scopeKey}/by-external-reference-code/{currentExternalReferenceCode}/{objectRelationshipName}/{relatedExternalReferenceCode}"
	)
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract void
			deleteScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				@NotNull @Parameter(hidden = true) @PathParam("scopeKey") String
					scopeKey,
				@NotNull @Parameter(hidden = true)
				@PathParam("currentExternalReferenceCode")
				String currentExternalReferenceCode,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName,
				@NotNull @Parameter(hidden = true)
				@PathParam("relatedExternalReferenceCode")
				String relatedExternalReferenceCode)
		throws Exception;

	@GET
	@Operation(
		operationId = "getByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNamePage"
	)
	@Parameters(
		{
			@Parameter(
				in = ParameterIn.PATH, name = "currentExternalReferenceCode"
			),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(in = ParameterIn.QUERY, name = "aggregationTerms"),
			@Parameter(in = ParameterIn.QUERY, name = "fields"),
			@Parameter(in = ParameterIn.QUERY, name = "filter"),
			@Parameter(in = ParameterIn.QUERY, name = "nestedFields"),
			@Parameter(in = ParameterIn.QUERY, name = "page"),
			@Parameter(in = ParameterIn.QUERY, name = "pageSize"),
			@Parameter(in = ParameterIn.QUERY, name = "restrictFields"),
			@Parameter(in = ParameterIn.QUERY, name = "search"),
			@Parameter(in = ParameterIn.QUERY, name = "sort")
		}
	)
	@Path(
		"/by-external-reference-code/{currentExternalReferenceCode}/{objectRelationshipName}"
	)
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Page<Object>
			getByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNamePage(
				@NotNull @Parameter(hidden = true)
				@PathParam("currentExternalReferenceCode")
				String currentExternalReferenceCode,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName,
				@Parameter(hidden = true) @QueryParam("search") String search,
				@Context Aggregation aggregation, @Context Filter filter,
				@Context Pagination pagination, @Context Sort[] sorts)
		throws Exception;

	@GET
	@Operation(
		operationId = "getByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode"
	)
	@Parameters(
		{
			@Parameter(
				in = ParameterIn.PATH, name = "currentExternalReferenceCode"
			),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(
				in = ParameterIn.PATH, name = "relatedExternalReferenceCode"
			)
		}
	)
	@Path(
		"/by-external-reference-code/{currentExternalReferenceCode}/{objectRelationshipName}/{relatedExternalReferenceCode}"
	)
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Object
			getByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				@NotNull @Parameter(hidden = true)
				@PathParam("currentExternalReferenceCode")
				String currentExternalReferenceCode,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName,
				@NotNull @Parameter(hidden = true)
				@PathParam("relatedExternalReferenceCode")
				String relatedExternalReferenceCode)
		throws Exception;

	@GET
	@Operation(
		operationId = "getObjectEntryObjectRelationshipNameRelatedObjectEntryPage"
	)
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

	@GET
	@Operation(
		operationId = "getObjectEntryObjectRelationshipNameRelatedObjectEntry"
	)
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
	public abstract Object
			getObjectEntryObjectRelationshipNameRelatedObjectEntry(
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
	@Operation(
		operationId = "getScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNamePage"
	)
	@Parameters(
		{
			@Parameter(in = ParameterIn.PATH, name = "scopeKey"),
			@Parameter(
				in = ParameterIn.PATH, name = "currentExternalReferenceCode"
			),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(in = ParameterIn.QUERY, name = "aggregationTerms"),
			@Parameter(in = ParameterIn.QUERY, name = "fields"),
			@Parameter(in = ParameterIn.QUERY, name = "filter"),
			@Parameter(in = ParameterIn.QUERY, name = "nestedFields"),
			@Parameter(in = ParameterIn.QUERY, name = "page"),
			@Parameter(in = ParameterIn.QUERY, name = "pageSize"),
			@Parameter(in = ParameterIn.QUERY, name = "restrictFields"),
			@Parameter(in = ParameterIn.QUERY, name = "search"),
			@Parameter(in = ParameterIn.QUERY, name = "sort")
		}
	)
	@Path(
		"/scopes/{scopeKey}/by-external-reference-code/{currentExternalReferenceCode}/{objectRelationshipName}"
	)
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Page<Object>
			getScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNamePage(
				@NotNull @Parameter(hidden = true) @PathParam("scopeKey") String
					scopeKey,
				@NotNull @Parameter(hidden = true)
				@PathParam("currentExternalReferenceCode")
				String currentExternalReferenceCode,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName,
				@Parameter(hidden = true) @QueryParam("search") String search,
				@Context Aggregation aggregation, @Context Filter filter,
				@Context Pagination pagination, @Context Sort[] sorts)
		throws Exception;

	@GET
	@Operation(
		operationId = "getScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode"
	)
	@Parameters(
		{
			@Parameter(in = ParameterIn.PATH, name = "scopeKey"),
			@Parameter(
				in = ParameterIn.PATH, name = "currentExternalReferenceCode"
			),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(
				in = ParameterIn.PATH, name = "relatedExternalReferenceCode"
			)
		}
	)
	@Path(
		"/scopes/{scopeKey}/by-external-reference-code/{currentExternalReferenceCode}/{objectRelationshipName}/{relatedExternalReferenceCode}"
	)
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Object
			getScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				@NotNull @Parameter(hidden = true) @PathParam("scopeKey") String
					scopeKey,
				@NotNull @Parameter(hidden = true)
				@PathParam("currentExternalReferenceCode")
				String currentExternalReferenceCode,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName,
				@NotNull @Parameter(hidden = true)
				@PathParam("relatedExternalReferenceCode")
				String relatedExternalReferenceCode)
		throws Exception;

	@Consumes({"application/json", "application/xml"})
	@Operation(
		operationId = "patchByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode"
	)
	@Parameters(
		{
			@Parameter(
				in = ParameterIn.PATH, name = "currentExternalReferenceCode"
			),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(
				in = ParameterIn.PATH, name = "relatedExternalReferenceCode"
			)
		}
	)
	@PATCH
	@Path(
		"/by-external-reference-code/{currentExternalReferenceCode}/{objectRelationshipName}/{relatedExternalReferenceCode}"
	)
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Object
			patchByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				@NotNull @Parameter(hidden = true)
				@PathParam("currentExternalReferenceCode")
				String currentExternalReferenceCode,
				ObjectEntry objectEntry,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName,
				@NotNull @Parameter(hidden = true)
				@PathParam("relatedExternalReferenceCode")
				String relatedExternalReferenceCode)
		throws Exception;

	@Consumes({"application/json", "application/xml"})
	@Operation(operationId = "patchCurrentObjectEntry")
	@Parameters(
		{
			@Parameter(in = ParameterIn.PATH, name = "currentObjectEntryId"),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(in = ParameterIn.PATH, name = "relatedObjectEntryId")
		}
	)
	@PATCH
	@Path(
		"/{currentObjectEntryId}/{objectRelationshipName}/{relatedObjectEntryId}"
	)
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Object patchCurrentObjectEntry(
			@NotNull @Parameter(hidden = true)
			@PathParam("currentObjectEntryId")
			Long currentObjectEntryId,
			ObjectEntry objectEntry,
			@NotNull @Parameter(hidden = true)
			@PathParam("objectRelationshipName")
			String objectRelationshipName,
			@NotNull @Parameter(hidden = true)
			@PathParam("relatedObjectEntryId")
			Long relatedObjectEntryId)
		throws Exception;

	@Consumes({"application/json", "application/xml"})
	@Operation(
		operationId = "patchScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode"
	)
	@Parameters(
		{
			@Parameter(in = ParameterIn.PATH, name = "scopeKey"),
			@Parameter(
				in = ParameterIn.PATH, name = "currentExternalReferenceCode"
			),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(
				in = ParameterIn.PATH, name = "relatedExternalReferenceCode"
			)
		}
	)
	@PATCH
	@Path(
		"/scopes/{scopeKey}/by-external-reference-code/{currentExternalReferenceCode}/{objectRelationshipName}/{relatedExternalReferenceCode}"
	)
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Object
			patchScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				@NotNull @Parameter(hidden = true) @PathParam("scopeKey") String
					scopeKey,
				@NotNull @Parameter(hidden = true)
				@PathParam("currentExternalReferenceCode")
				String currentExternalReferenceCode,
				ObjectEntry objectEntry,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName,
				@NotNull @Parameter(hidden = true)
				@PathParam("relatedExternalReferenceCode")
				String relatedExternalReferenceCode)
		throws Exception;

	@Consumes({"application/json", "application/xml"})
	@Operation(
		operationId = "postByExternalReferenceCodeObjectEntryObjectRelationshipName"
	)
	@Parameters(
		{
			@Parameter(
				in = ParameterIn.PATH, name = "currentExternalReferenceCode"
			),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName")
		}
	)
	@Path(
		"/by-external-reference-code/{currentExternalReferenceCode}/{objectRelationshipName}"
	)
	@POST
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Object
			postByExternalReferenceCodeObjectEntryObjectRelationshipName(
				@NotNull @Parameter(hidden = true)
				@PathParam("currentExternalReferenceCode")
				String currentExternalReferenceCode,
				ObjectEntry objectEntry,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName)
		throws Exception;

	@Consumes({"application/json", "application/xml"})
	@Operation(operationId = "postObjectEntryObjectRelationshipName")
	@Parameters(
		{
			@Parameter(in = ParameterIn.PATH, name = "currentObjectEntryId"),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName")
		}
	)
	@Path("/{currentObjectEntryId}/{objectRelationshipName}")
	@POST
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Object postObjectEntryObjectRelationshipName(
			@NotNull @Parameter(hidden = true)
			@PathParam("currentObjectEntryId")
			Long currentObjectEntryId,
			ObjectEntry objectEntry,
			@NotNull @Parameter(hidden = true)
			@PathParam("objectRelationshipName")
			String objectRelationshipName)
		throws Exception;

	@Consumes({"application/json", "application/xml"})
	@Operation(
		operationId = "postScopeScopeKeyByExternalReferenceCodeObjectEntryObjectRelationshipName"
	)
	@Parameters(
		{
			@Parameter(
				in = ParameterIn.PATH, name = "currentExternalReferenceCode"
			),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName")
		}
	)
	@Path(
		"/scopes/{scopeKey}/by-external-reference-code/{currentExternalReferenceCode}/{objectRelationshipName}"
	)
	@POST
	@Produces({"application/json", "application/xml"})
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Object
			postScopeScopeKeyByExternalReferenceCodeObjectEntryObjectRelationshipName(
				@NotNull @Parameter(hidden = true) @PathParam("scopeKey") String
					scopeKey,
				@NotNull @Parameter(hidden = true)
				@PathParam("currentExternalReferenceCode")
				String currentExternalReferenceCode,
				ObjectEntry objectEntry,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName)
		throws Exception;

	@Operation(
		operationId = "putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode"
	)
	@Parameters(
		{
			@Parameter(
				in = ParameterIn.PATH, name = "currentExternalReferenceCode"
			),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(
				in = ParameterIn.PATH, name = "relatedExternalReferenceCode"
			)
		}
	)
	@Path(
		"/by-external-reference-code/{currentExternalReferenceCode}/{objectRelationshipName}/{relatedExternalReferenceCode}"
	)
	@Produces({"application/json", "application/xml"})
	@PUT
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Object
			putByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				@NotNull @Parameter(hidden = true)
				@PathParam("currentExternalReferenceCode")
				String currentExternalReferenceCode,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName,
				@NotNull @Parameter(hidden = true)
				@PathParam("relatedExternalReferenceCode")
				String relatedExternalReferenceCode)
		throws Exception;

	@Operation(operationId = "putCurrentObjectEntry")
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

	@Consumes({"application/json", "application/xml"})
	@Operation(
		operationId = "putScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode"
	)
	@Parameters(
		{
			@Parameter(in = ParameterIn.PATH, name = "scopeKey"),
			@Parameter(
				in = ParameterIn.PATH, name = "currentExternalReferenceCode"
			),
			@Parameter(in = ParameterIn.PATH, name = "objectRelationshipName"),
			@Parameter(
				in = ParameterIn.PATH, name = "relatedExternalReferenceCode"
			)
		}
	)
	@Path(
		"/scopes/{scopeKey}/by-external-reference-code/{currentExternalReferenceCode}/{objectRelationshipName}/{relatedExternalReferenceCode}"
	)
	@Produces({"application/json", "application/xml"})
	@PUT
	@Tags(@Tag(name = "ObjectEntry"))
	public abstract Object
			putScopeScopeKeyByExternalReferenceCodeCurrentExternalReferenceCodeObjectRelationshipNameRelatedExternalReferenceCode(
				@NotNull @Parameter(hidden = true) @PathParam("scopeKey") String
					scopeKey,
				@NotNull @Parameter(hidden = true)
				@PathParam("currentExternalReferenceCode")
				String currentExternalReferenceCode,
				ObjectEntry objectEntry,
				@NotNull @Parameter(hidden = true)
				@PathParam("objectRelationshipName")
				String objectRelationshipName,
				@NotNull @Parameter(hidden = true)
				@PathParam("relatedExternalReferenceCode")
				String relatedExternalReferenceCode)
		throws Exception;

	public void setContextCompany(Company contextCompany) {
		this.contextCompany = contextCompany;
	}

	protected <T, R, E extends Throwable> List<R> transform(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transform(collection, unsafeFunction);
	}

	protected AcceptLanguage contextAcceptLanguage;
	protected Company contextCompany;
	protected HttpServletRequest contextHttpServletRequest;
	protected UriInfo contextUriInfo;
	protected User contextUser;

}