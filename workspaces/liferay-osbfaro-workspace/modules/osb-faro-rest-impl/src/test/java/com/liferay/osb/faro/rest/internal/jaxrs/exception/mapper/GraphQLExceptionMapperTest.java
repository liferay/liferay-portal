/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.exception.mapper;

import com.liferay.osb.faro.rest.internal.graphql.client.GraphQLException;
import com.liferay.osb.faro.rest.internal.graphql.model.GraphQLError;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class GraphQLExceptionMapperTest {

	@Before
	public void setUp() {
		Mockito.when(
			_httpHeaders.getAcceptableMediaTypes()
		).thenReturn(
			Collections.singletonList(MediaType.APPLICATION_JSON_TYPE)
		);

		ReflectionTestUtil.setFieldValue(
			_graphQLExceptionMapper, "httpHeaders", _httpHeaders);
	}

	@Test
	public void testToResponse() {
		_assertResponse(
			Response.Status.BAD_REQUEST,
			"Check the parameter values against the tool description",
			_getGraphQLError("ValidationError", null, null));
		_assertResponse(
			Response.Status.BAD_REQUEST, "Date range is too large",
			_getGraphQLError(
				"DataFetchingException", "date-range-limit-exceeded", null));
		_assertResponse(
			Response.Status.BAD_REQUEST, "Page size is too large",
			_getGraphQLError(
				"DataFetchingException", null, "page-size-limit-exceeded"));
		_assertResponse(
			Response.Status.GATEWAY_TIMEOUT,
			"Request took too long, narrow it with a shorter date range or a " +
				"smaller page size",
			_getGraphQLError(
				"DataFetchingException", "query-timeout-exceeded", null));
		_assertResponse(
			Response.Status.INTERNAL_SERVER_ERROR, "Internal Server Error");
		_assertResponse(
			Response.Status.INTERNAL_SERVER_ERROR, "Internal Server Error",
			_getGraphQLError(
				"DataFetchingException", "invalid-preference", null));
		_assertResponse(
			Response.Status.INTERNAL_SERVER_ERROR, "Internal Server Error",
			_getGraphQLError("DataFetchingException", null, null));
		_assertResponse(
			Response.Status.NOT_FOUND, "Not Found",
			_getGraphQLError("ValidationError", null, null),
			_getGraphQLError(
				"DataFetchingException", "no-account-was-found-with-id-1",
				null));
		_assertResponse(
			Response.Status.SERVICE_UNAVAILABLE,
			"Query is still running, repeat the same request with the same " +
				"parameters later",
			_getGraphQLError(
				"DataFetchingException", "no-account-was-found-with-id-1",
				null),
			_getGraphQLError(
				"DataFetchingException", "query-in-progress", null));
	}

	private void _assertResponse(
		Response.Status expectedStatus, String expectedTitle,
		GraphQLError... graphQLErrors) {

		Response response = _graphQLExceptionMapper.toResponse(
			new GraphQLException(
				Arrays.asList(graphQLErrors), RandomTestUtil.randomString()));

		Assert.assertEquals(
			expectedStatus.getStatusCode(), response.getStatus());

		Problem problem = (Problem)response.getEntity();

		Assert.assertEquals(expectedTitle, problem.getTitle());
		Assert.assertNull(problem.getType());
	}

	private GraphQLError _getGraphQLError(
		String classification, String extensionsMessageKey, String messageKey) {

		GraphQLError graphQLError = new GraphQLError();

		graphQLError.setExtensions(
			HashMapBuilder.<String, Object>put(
				"classification", classification
			).put(
				"errorId", RandomTestUtil.randomString()
			).put(
				"messageKey", extensionsMessageKey
			).build());
		graphQLError.setMessage(RandomTestUtil.randomString());
		graphQLError.setMessageKey(messageKey);

		return graphQLError;
	}

	private final GraphQLExceptionMapper _graphQLExceptionMapper =
		new GraphQLExceptionMapper();
	private final HttpHeaders _httpHeaders = Mockito.mock(HttpHeaders.class);

}