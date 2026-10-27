/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.exception.mapper;

import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.exception.NoSuchEntryException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Nilton Vieira
 */
public class FaroEngineClientExceptionMapperTest {

	@Before
	public void setUp() {
		Mockito.when(
			_httpHeaders.getAcceptableMediaTypes()
		).thenReturn(
			Collections.singletonList(MediaType.APPLICATION_JSON_TYPE)
		);

		ReflectionTestUtil.setFieldValue(
			_faroEngineClientExceptionMapper, "httpHeaders", _httpHeaders);
	}

	@Test
	public void testToResponse() {
		_assertResponse(
			Response.Status.BAD_REQUEST,
			"Check the parameter values against the tool description",
			new FaroEngineClientException(RandomTestUtil.randomString(), 400));
		_assertResponse(
			Response.Status.INTERNAL_SERVER_ERROR, "Internal Server Error",
			new FaroEngineClientException(RandomTestUtil.randomString(), 500));
		_assertResponse(
			Response.Status.NOT_FOUND, "Not Found", new NoSuchEntryException());
	}

	private void _assertResponse(
		Response.Status expectedStatus, String expectedTitle,
		FaroEngineClientException faroEngineClientException) {

		Response response = _faroEngineClientExceptionMapper.toResponse(
			faroEngineClientException);

		Assert.assertEquals(
			expectedStatus.getStatusCode(), response.getStatus());

		Problem problem = (Problem)response.getEntity();

		Assert.assertEquals(expectedTitle, problem.getTitle());
		Assert.assertNull(problem.getType());
	}

	private final FaroEngineClientExceptionMapper
		_faroEngineClientExceptionMapper =
			new FaroEngineClientExceptionMapper();
	private final HttpHeaders _httpHeaders = Mockito.mock(HttpHeaders.class);

}