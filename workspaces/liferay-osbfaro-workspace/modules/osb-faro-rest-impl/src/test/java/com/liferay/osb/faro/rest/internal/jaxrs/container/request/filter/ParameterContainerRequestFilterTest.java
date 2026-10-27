/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.container.request.filter;

import com.liferay.osb.faro.rest.resource.v1_0.AccountLifecycleStageTransitionResource;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Nilton Vieira
 */
public class ParameterContainerRequestFilterTest {

	@Before
	public void setUp() {
		for (Method method :
				AccountLifecycleStageTransitionResource.class.getMethods()) {

			if (!Objects.equals(
					method.getName(),
					"getWorkspaceGroupAccountLifecycleStageTransitionsPage")) {

				continue;
			}

			Mockito.when(
				_resourceInfo.getResourceMethod()
			).thenReturn(
				method
			);
		}

		ReflectionTestUtil.setFieldValue(
			_parameterContainerRequestFilter, "_resourceInfo", _resourceInfo);
	}

	@Test
	public void testFilter() {
		Assert.assertNull(_filter("rangeKey", "LAST_30_DAYS"));
		Assert.assertNull(_filter("sort", "transitionDate:desc"));
		Assert.assertNull(_filter(null, null));

		_assertTitle(
			"\"bogusColumn\" is not a sortable column. Sortable columns: " +
				"accountId, accountName, fromAccountLifecycleStageId, " +
					"toAccountLifecycleStageId, transitionDate",
			_filter("sort", "bogusColumn:desc"));
		_assertTitle(
			"\"pageSize\" must be a whole number from 1 to 500",
			_filter("pageSize", "501"));
		_assertTitle(
			"\"rangeEnd\" and \"rangeStart\" must be set together",
			_filter("rangeStart", "2026-10-01"));
		_assertTitle(
			"\"rangeKey\" must be one of LAST_180_DAYS, LAST_24_HOURS, " +
				"LAST_28_DAYS, LAST_30_DAYS, LAST_7_DAYS, LAST_90_DAYS, " +
					"LAST_YEAR, YESTERDAY",
			_filter("rangeKey", "LAST_2_DAYS"));
		_assertTitle(
			"\"rangeStart\" must not be after \"rangeEnd\"",
			_filter(
				new MultivaluedHashMap<String, String>() {
					{
						putSingle("rangeEnd", "2026-10-01");
						putSingle("rangeStart", "2026-10-20");
					}
				}));
		_assertTitle(
			"\"sort\" must be a comma separated list of \"column:asc\" or " +
				"\"column:desc\" expressions",
			_filter("sort", "accountName:sideways"));
	}

	private void _assertTitle(String expectedTitle, Response response) {
		Assert.assertEquals(
			Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

		Problem problem = (Problem)response.getEntity();

		Assert.assertEquals(expectedTitle, problem.getTitle());
	}

	private Response _filter(MultivaluedMap<String, String> queryParameters) {
		ContainerRequestContext containerRequestContext = Mockito.mock(
			ContainerRequestContext.class);

		UriInfo uriInfo = Mockito.mock(UriInfo.class);

		Mockito.when(
			uriInfo.getPathParameters()
		).thenReturn(
			new MultivaluedHashMap<>()
		);

		Mockito.when(
			uriInfo.getQueryParameters()
		).thenReturn(
			queryParameters
		);

		Mockito.when(
			containerRequestContext.getUriInfo()
		).thenReturn(
			uriInfo
		);

		_parameterContainerRequestFilter.filter(containerRequestContext);

		ArgumentCaptor<Response> argumentCaptor = ArgumentCaptor.forClass(
			Response.class);

		Mockito.verify(
			containerRequestContext, Mockito.atMost(1)
		).abortWith(
			argumentCaptor.capture()
		);

		List<Response> responses = argumentCaptor.getAllValues();

		if (responses.isEmpty()) {
			return null;
		}

		return responses.get(0);
	}

	private Response _filter(String name, String value) {
		MultivaluedMap<String, String> queryParameters =
			new MultivaluedHashMap<>();

		if (name != null) {
			queryParameters.putSingle(name, value);
		}

		return _filter(queryParameters);
	}

	private final ParameterContainerRequestFilter
		_parameterContainerRequestFilter =
			new ParameterContainerRequestFilter();
	private final ResourceInfo _resourceInfo = Mockito.mock(ResourceInfo.class);

}