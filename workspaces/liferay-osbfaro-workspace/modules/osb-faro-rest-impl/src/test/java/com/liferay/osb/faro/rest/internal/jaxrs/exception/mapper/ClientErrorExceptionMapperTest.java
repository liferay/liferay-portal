/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class ClientErrorExceptionMapperTest {

	@Before
	public void setUp() {
		Mockito.when(
			_httpHeaders.getAcceptableMediaTypes()
		).thenReturn(
			Collections.singletonList(MediaType.APPLICATION_JSON_TYPE)
		);

		ReflectionTestUtil.setFieldValue(
			_clientErrorExceptionMapper, "httpHeaders", _httpHeaders);
	}

	@Test
	public void testToResponse() {
		String message = RandomTestUtil.randomString();

		_assertResponse(
			new BadRequestException(message), Response.Status.BAD_REQUEST,
			message);

		message = RandomTestUtil.randomString();

		_assertResponse(
			new ClientErrorException(message, Response.Status.CONFLICT),
			Response.Status.CONFLICT, message);
	}

	private void _assertResponse(
		ClientErrorException clientErrorException,
		Response.Status expectedStatus, String expectedTitle) {

		Response response = _clientErrorExceptionMapper.toResponse(
			clientErrorException);

		Assert.assertEquals(
			expectedStatus.getStatusCode(), response.getStatus());

		Problem problem = (Problem)response.getEntity();

		Assert.assertEquals(expectedTitle, problem.getTitle());
		Assert.assertNull(problem.getType());
	}

	private final ClientErrorExceptionMapper _clientErrorExceptionMapper =
		new ClientErrorExceptionMapper();
	private final HttpHeaders _httpHeaders = Mockito.mock(HttpHeaders.class);

}