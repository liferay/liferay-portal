/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.exception.mapper;

import com.liferay.osb.faro.engine.client.exception.HeadlessRequestRejectedException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Leslie Wong
 */
public class HeadlessRequestRejectedExceptionMapperTest {

	@Before
	public void setUp() {
		HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);

		Mockito.when(
			httpHeaders.getAcceptableMediaTypes()
		).thenReturn(
			Collections.singletonList(MediaType.APPLICATION_JSON_TYPE)
		);

		ReflectionTestUtil.setFieldValue(
			_headlessRequestRejectedExceptionMapper, "httpHeaders",
			httpHeaders);
	}

	@Test
	public void testToResponse() {
		long limit = RandomTestUtil.randomLong();

		_testToResponse(
			limit, "date-range-limit-exceeded", null,
			Response.Status.BAD_REQUEST,
			"Date range must be at most " + limit + " days");
		_testToResponse(
			limit, "page-size-limit-exceeded", null,
			Response.Status.BAD_REQUEST, "Page size must be at most " + limit);

		long retryAfterSeconds = RandomTestUtil.randomLong();

		_testToResponse(
			null, "query-in-progress", retryAfterSeconds,
			Response.Status.SERVICE_UNAVAILABLE,
			"Query is still running, repeat the same request with the same " +
				"parameters after " + retryAfterSeconds + " seconds");
		_testToResponse(
			null, "service-capacity-unavailable", retryAfterSeconds,
			Response.Status.SERVICE_UNAVAILABLE,
			"Unable to process the request right now, retry after " +
				retryAfterSeconds + " seconds");
		_testToResponse(
			null, "too-many-concurrent-requests", retryAfterSeconds,
			Response.Status.SERVICE_UNAVAILABLE,
			"Unable to process the request right now, retry after " +
				retryAfterSeconds + " seconds");

		_testToResponse(
			null, "page-size-limit-exceeded", null, Response.Status.BAD_REQUEST,
			"Page size is too large");
		_testToResponse(
			null, "query-timeout-exceeded", null,
			Response.Status.GATEWAY_TIMEOUT,
			"Request took too long, narrow it with a shorter date range or a " +
				"smaller page size");
		_testToResponse(
			null, "request-deadline-exceeded", null,
			Response.Status.GATEWAY_TIMEOUT,
			"Request took too long, narrow it with a shorter date range or a " +
				"smaller page size");
		_testToResponse(
			null, "service-capacity-unavailable", null,
			Response.Status.SERVICE_UNAVAILABLE,
			"Unable to process the request right now, retry later");
	}

	private void _testToResponse(
		Long limit, String messageKey, Long retryAfterSeconds,
		Response.Status status, String title) {

		Response response = _headlessRequestRejectedExceptionMapper.toResponse(
			new HeadlessRequestRejectedException(
				limit, RandomTestUtil.randomString(), messageKey,
				retryAfterSeconds));

		Assert.assertEquals(
			Objects.toString(retryAfterSeconds, null),
			response.getHeaderString(HttpHeaders.RETRY_AFTER));
		Assert.assertEquals(status.getStatusCode(), response.getStatus());

		Problem problem = (Problem)response.getEntity();

		Assert.assertEquals(title, problem.getTitle());
	}

	private final HeadlessRequestRejectedExceptionMapper
		_headlessRequestRejectedExceptionMapper =
			new HeadlessRequestRejectedExceptionMapper();

}