/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.resource;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.constants.HeadlessBuilderConstants;
import com.liferay.headless.builder.internal.application.endpoint.EndpointMatcher;
import com.liferay.headless.builder.internal.helper.EndpointHelper;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import java.security.InvalidParameterException;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Luis Miguel Barcos
 */
public class HeadlessBuilderResourceImpl {

	public HeadlessBuilderResourceImpl(
		EndpointHelper endpointHelper,
		Function<Long, EndpointMatcher> endpointMatcherFunction) {

		_endpointHelper = endpointHelper;
		_endpointMatcherFunction = endpointMatcherFunction;
	}

	@GET
	@Path("/{path: .*}")
	@Produces({"application/json", "application/xml"})
	public Response get(
			@QueryParam("filter") String filterString,
			@Context Pagination pagination, @PathParam("path") String path,
			@QueryParam("sort") String sortString)
		throws Exception {

		return _get(
			path, APIApplication.Endpoint.Scope.COMPANY,
			endpoint -> _endpointHelper.getResponseEntityMapsPage(
				_acceptLanguage, _company.getCompanyId(), endpoint,
				filterString, pagination, null, sortString));
	}

	@GET
	@Path(HeadlessBuilderConstants.BASE_PATH_SCOPES_SUFFIX + "/{path: .*}")
	@Produces({"application/json", "application/xml"})
	public Response get(
			@QueryParam("filter") String filterString,
			@Context Pagination pagination, @PathParam("path") String path,
			@PathParam("scopeKey") String scopeKey,
			@QueryParam("sort") String sortString)
		throws Exception {

		return _get(
			path, APIApplication.Endpoint.Scope.SITE,
			endpoint -> _endpointHelper.getResponseEntityMapsPage(
				_acceptLanguage, _company.getCompanyId(), endpoint,
				filterString, pagination, scopeKey, sortString));
	}

	@GET
	@Path("/{path: .*}/{parameter}")
	@Produces({"application/json", "application/xml"})
	public Response get(
			@PathParam("path") String path,
			@PathParam("parameter") String pathParameterValue)
		throws Exception {

		return _get(
			path + "/" + pathParameterValue,
			APIApplication.Endpoint.Scope.COMPANY,
			endpoint -> _endpointHelper.getResponseEntityMap(
				_company.getCompanyId(), endpoint.getPathParameter(),
				pathParameterValue, endpoint.getResponseSchema(), null));
	}

	@GET
	@Path(
		HeadlessBuilderConstants.BASE_PATH_SCOPES_SUFFIX +
			"/{path: .*}/{parameter}"
	)
	@Produces({"application/json", "application/xml"})
	public Response get(
			@PathParam("scopeKey") String scopeKey,
			@PathParam("path") String path,
			@PathParam("parameter") String pathParameterValue)
		throws Exception {

		return _get(
			path + "/" + pathParameterValue, APIApplication.Endpoint.Scope.SITE,
			endpoint -> _endpointHelper.getResponseEntityMap(
				_company.getCompanyId(), endpoint.getPathParameter(),
				pathParameterValue, endpoint.getResponseSchema(), scopeKey));
	}

	@Consumes({"application/json", "application/xml"})
	@Path("/{path: .*}")
	@POST
	@Produces({"application/json", "application/xml"})
	public Response post(
			@PathParam("path") String path, Map<String, Object> properties)
		throws Exception {

		return _post(
			path, APIApplication.Endpoint.Scope.COMPANY,
			endpoint -> _endpointHelper.postObjectEntry(
				_company.getCompanyId(), properties,
				endpoint.getRequestSchema(), endpoint.getResponseSchema(),
				null));
	}

	@Consumes({"application/json", "application/xml"})
	@Path(HeadlessBuilderConstants.BASE_PATH_SCOPES_SUFFIX + "/{path: .*}")
	@POST
	@Produces({"application/json", "application/xml"})
	public Response post(
			@PathParam("scopeKey") String scopeKey,
			@PathParam("path") String path, Map<String, Object> properties)
		throws Exception {

		return _post(
			path, APIApplication.Endpoint.Scope.SITE,
			endpoint -> _endpointHelper.postObjectEntry(
				_company.getCompanyId(), properties,
				endpoint.getRequestSchema(), endpoint.getResponseSchema(),
				scopeKey));
	}

	private <T> Response _get(
			String path, APIApplication.Endpoint.Scope scope,
			UnsafeFunction<APIApplication.Endpoint, T, Exception>
				successUnsafeFunction)
		throws Exception {

		APIApplication.Endpoint endpoint = _getEndpoint(
			Http.Method.GET, path, scope);

		if (endpoint == null) {
			return Response.status(
				Response.Status.NOT_FOUND
			).build();
		}

		if (Validator.isBlank(endpoint.getPathParameter()) &&
			Objects.equals(
				endpoint.getRetrieveType(),
				APIApplication.Endpoint.RetrieveType.SINGLE_ELEMENT)) {

			throw new InvalidParameterException("Path parameter is missing");
		}

		if (endpoint.getResponseSchema() == null) {
			return Response.noContent(
			).build();
		}

		return Response.ok(
			successUnsafeFunction.apply(endpoint)
		).build();
	}

	private APIApplication.Endpoint _getEndpoint(
		Http.Method method, String path, APIApplication.Endpoint.Scope scope) {

		EndpointMatcher endpointMatcher = _endpointMatcherFunction.apply(
			_company.getCompanyId());

		if (endpointMatcher == null) {
			return null;
		}

		return endpointMatcher.getEndpoint(method, "/" + path, scope);
	}

	private <T> Response _post(
			String path, APIApplication.Endpoint.Scope scope,
			UnsafeFunction<APIApplication.Endpoint, T, Exception>
				successUnsafeFunction)
		throws Exception {

		APIApplication.Endpoint endpoint = _getEndpoint(
			Http.Method.POST, path, scope);

		if (endpoint == null) {
			return Response.status(
				Response.Status.NOT_FOUND
			).build();
		}

		Object object = successUnsafeFunction.apply(endpoint);

		if (object == null) {
			return Response.noContent(
			).build();
		}

		return Response.ok(
			object
		).build();
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Context
	private Company _company;

	private final EndpointHelper _endpointHelper;
	private final Function<Long, EndpointMatcher> _endpointMatcherFunction;

}