/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.exception;

import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.model.ErrorResponse;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Eudaldo Alonso
 */
public class FaroEngineClientExceptionMapperTest {

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testToResponseWhenMessageIsNotJSON() {
		FaroEngineClientExceptionMapper faroEngineClientExceptionMapper =
			new FaroEngineClientExceptionMapper();

		Response response = faroEngineClientExceptionMapper.toResponse(
			new FaroEngineClientException(
				"<html><body><h1>502 Bad Gateway</h1></body></html>"));

		Assert.assertEquals(
			MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
		Assert.assertEquals(
			Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
			response.getStatus());

		ErrorResponse errorResponse = (ErrorResponse)response.getEntity();

		Assert.assertEquals(
			"The analytics engine returned an unexpected response",
			errorResponse.getMessage());
		Assert.assertEquals(
			Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
			errorResponse.getStatus());
	}

}