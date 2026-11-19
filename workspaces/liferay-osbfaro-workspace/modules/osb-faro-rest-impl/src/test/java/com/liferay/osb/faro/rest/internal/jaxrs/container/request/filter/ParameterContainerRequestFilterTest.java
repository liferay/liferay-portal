/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.container.request.filter;

import com.liferay.osb.faro.rest.resource.v1_0.AccountLifecycleResource;
import com.liferay.osb.faro.rest.resource.v1_0.AccountLifecycleStageTransitionResource;
import com.liferay.osb.faro.rest.resource.v1_0.AcquisitionResource;
import com.liferay.osb.faro.rest.resource.v1_0.EventAttributeDefinitionResource;
import com.liferay.osb.faro.rest.resource.v1_0.EventDefinitionResource;
import com.liferay.osb.faro.rest.resource.v1_0.IndividualSegmentMembershipResource;
import com.liferay.osb.faro.rest.resource.v1_0.VisitorMetricResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Nilton Vieira
 */
public class ParameterContainerRequestFilterTest {

	@Test
	public void testFilter() {
		_mockResourceInfo(
			AccountLifecycleStageTransitionResource.class,
			"getWorkspaceGroupAccountLifecycleStageTransitionsPage");

		Assert.assertNull(_filter("pageSize", "100"));
		Assert.assertNull(_filter("rangeKey", "LAST_30_DAYS"));
		Assert.assertNull(
			_filter(
				_getQueryParameters(
					"selectedMetrics", "accountsTouched", "closedWonAmount")));
		Assert.assertNull(_filter("sort", "transitionDate:desc"));
		Assert.assertNull(
			_filter(_getRangeQueryParameters("2026-10-02", "2026-04-06")));
		Assert.assertNull(_filter(null, null));

		_assertTitle(
			"\"accountLifecycleId\" must be a numeric ID",
			_filter("accountLifecycleId", RandomTestUtil.randomString()));
		_assertTitle(
			"Date range must be at most 180 days",
			_filter(_getRangeQueryParameters("2026-10-02", "2026-04-05")));
		_assertTitle(
			"\"bogusColumn\" is not a sortable column. The sortable columns " +
				"are \"accountId\", \"accountName\", " +
					"\"fromAccountLifecycleStageId\", " +
						"\"toAccountLifecycleStageId\", \"transitionDate\".",
			_filter("sort", "bogusColumn:desc"));
		_assertTitle(
			"\"pageSize\" must be a whole number from 1 to 100",
			_filter("pageSize", "101"));
		_assertTitle(
			"\"rangeEnd\" and \"rangeStart\" must be set together",
			_filter("rangeStart", "2026-10-01"));
		_assertTitle(
			"\"rangeKey\" must be one of \"LAST_180_DAYS\", " +
				"\"LAST_24_HOURS\", \"LAST_28_DAYS\", \"LAST_30_DAYS\", " +
					"\"LAST_7_DAYS\", \"LAST_90_DAYS\", \"YESTERDAY\"",
			_filter("rangeKey", "LAST_YEAR"));
		_assertTitle(
			"\"rangeStart\" must not be after \"rangeEnd\"",
			_filter(_getRangeQueryParameters("2026-10-01", "2026-10-20")));
		_assertTitle(
			"\"selectedMetrics\" must be one of \"accountsTouched\", " +
				"\"campaignCount\", \"closedWonAmount\", " +
					"\"openPipelineAmount\"",
			_filter(
				_getQueryParameters(
					"selectedMetrics", "accountsTouched", "bogusMetric")));
		_assertTitle(
			"\"sort\" accepts only one column on this endpoint",
			_filter("sort", "accountName:asc,transitionDate:desc"));
		_assertTitle(
			"\"sort\" must be a comma separated list of \"column:asc\" or " +
				"\"column:desc\" expressions",
			_filter("sort", "accountName:sideways"));
	}

	@Test
	public void testFilterWithAccountLifecycleResourceMethod() {
		_mockResourceInfo(
			AccountLifecycleResource.class,
			"getWorkspaceGroupChannelAccountLifecyclesPage");

		String fieldName = RandomTestUtil.randomString();

		_assertTitle(
			StringBundler.concat(
				"\"", fieldName,
				"\" is not a sortable column. The sortable columns are ",
				"\"dateModified\", \"id\", \"name\"."),
			_filter("sort", fieldName + ":asc"));
	}

	@Test
	public void testFilterWithAcquisitionResourceMethod() {
		_mockResourceInfo(
			AcquisitionResource.class,
			"getWorkspaceGroupChannelAcquisitionsPage");

		Assert.assertNull(_filter("pageSize", "100"));
		Assert.assertNull(
			_filter("segmentId", String.valueOf(RandomTestUtil.randomLong())));
		Assert.assertNull(_filter("type", "SOURCE_MEDIUM"));

		_assertTitle(
			"\"pageSize\" must be a whole number from 1 to 100",
			_filter("pageSize", "101"));
		_assertTitle(
			"\"segmentId\" must be a numeric ID",
			_filter("segmentId", RandomTestUtil.randomString()));
		_assertTitle(
			"\"type\" must be one of \"CHANNEL\", \"REFERRER\", " +
				"\"SOURCE_MEDIUM\"",
			_filter("type", RandomTestUtil.randomString()));
	}

	@Test
	public void testFilterWithEventAttributeDefinitionResourceMethod() {
		_mockResourceInfo(
			EventAttributeDefinitionResource.class,
			"getWorkspaceGroupEventAttributeDefinitionsPage");

		Assert.assertNull(_filter("attributeType", "GLOBAL"));
		Assert.assertNull(
			_filter(
				"eventDefinitionId",
				String.valueOf(RandomTestUtil.randomLong())));

		_assertTitle(
			"\"attributeType\" must be one of \"ALL\", \"GLOBAL\", \"LOCAL\"",
			_filter("attributeType", RandomTestUtil.randomString()));
		_assertTitle(
			"\"displayName\" is not a sortable column. The sortable columns " +
				"are \"name\".",
			_filter("sort", "displayName:asc"));
		_assertTitle(
			"\"eventDefinitionId\" must be a numeric ID",
			_filter("eventDefinitionId", RandomTestUtil.randomString()));
		_assertTitle(
			"\"pageSize\" must be a whole number from 1 to 100",
			_filter("pageSize", "101"));
	}

	@Test
	public void testFilterWithEventDefinitionResourceMethod() {
		_mockResourceInfo(
			EventDefinitionResource.class,
			"getWorkspaceGroupEventDefinitionsPage");

		Assert.assertNull(_filter("blocked", "TRUE"));
		Assert.assertNull(_filter("eventType", "CUSTOM"));
		Assert.assertNull(_filter("hidden", "False"));
		Assert.assertNull(_filter("pageSize", "100"));
		Assert.assertNull(_filter("sort", "displayName:desc"));

		_assertTitle(
			"\"blockedLastSeenDate\" is not a sortable column. The sortable " +
				"columns are \"displayName\", \"name\".",
			_filter("sort", "blockedLastSeenDate:desc"));
		_assertTitle(
			"\"blocked\" must be one of \"false\", \"true\"",
			_filter("blocked", RandomTestUtil.randomString()));
		_assertTitle(
			"\"eventType\" must be one of \"ALL\", \"CUSTOM\", \"DEFAULT\"",
			_filter("eventType", RandomTestUtil.randomString()));
		_assertTitle(
			"\"hidden\" must be one of \"false\", \"true\"",
			_filter("hidden", RandomTestUtil.randomString()));
		_assertTitle(
			"\"pageSize\" must be a whole number from 1 to 100",
			_filter("pageSize", "101"));
	}

	@Test
	public void testFilterWithUnsortableResourceMethod() {
		_mockResourceInfo(
			IndividualSegmentMembershipResource.class,
			"getWorkspaceGroupIndividualSegmentMembershipsPage");

		Assert.assertNull(_filter(null, null));

		_assertTitle(
			"\"sort\" is not supported by this endpoint",
			_filter("sort", "accountName:asc"));
	}

	@Test
	public void testFilterWithVisitorMetricResourceMethod() {
		_mockResourceInfo(
			VisitorMetricResource.class,
			"getWorkspaceGroupChannelVisitorMetric");

		Assert.assertNull(_filter("rangeKey", "LAST_7_DAYS"));

		_assertTitle(
			"\"rangeEnd\" and \"rangeStart\" are not supported by this " +
				"endpoint",
			_filter("rangeEnd", RandomTestUtil.randomString()));
		_assertTitle(
			"\"rangeEnd\" and \"rangeStart\" are not supported by this " +
				"endpoint",
			_filter("rangeStart", RandomTestUtil.randomString()));
	}

	private void _assertTitle(String expectedTitle, Response response) {
		Assert.assertEquals(
			Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

		Problem problem = (Problem)response.getEntity();

		Assert.assertEquals(Response.Status.BAD_REQUEST, problem.getStatus());
		Assert.assertEquals(expectedTitle, problem.getTitle());
		Assert.assertNull(problem.getType());
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

	private MultivaluedMap<String, String> _getQueryParameters(
		String name, String... values) {

		MultivaluedMap<String, String> queryParameters =
			new MultivaluedHashMap<>();

		queryParameters.addAll(name, values);

		return queryParameters;
	}

	private MultivaluedMap<String, String> _getRangeQueryParameters(
		String rangeEnd, String rangeStart) {

		MultivaluedMap<String, String> rangeQueryParameters =
			new MultivaluedHashMap<>();

		rangeQueryParameters.putSingle("rangeEnd", rangeEnd);
		rangeQueryParameters.putSingle("rangeStart", rangeStart);

		return rangeQueryParameters;
	}

	private ResourceInfo _mockResourceInfo(Class<?> clazz, String methodName) {
		ResourceInfo resourceInfo = Mockito.mock(ResourceInfo.class);

		for (Method method : clazz.getMethods()) {
			if (Objects.equals(method.getName(), methodName)) {
				Mockito.when(
					resourceInfo.getResourceMethod()
				).thenReturn(
					method
				);

				break;
			}
		}

		ReflectionTestUtil.setFieldValue(
			_parameterContainerRequestFilter, "_resourceInfo", resourceInfo);

		return resourceInfo;
	}

	private final ParameterContainerRequestFilter
		_parameterContainerRequestFilter =
			new ParameterContainerRequestFilter();

}