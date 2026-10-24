/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.web.client;

import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.exception.HeadlessRequestRejectedException;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.net.URI;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

/**
 * @author Leslie Wong
 */
public class ResponseErrorHandlerTest {

	@Before
	public void setUp() throws Exception {
		FileUtil fileUtil = new FileUtil();

		File file = Mockito.mock(File.class);

		Mockito.when(
			file.getBytes(Mockito.any(InputStream.class))
		).thenAnswer(
			invocation -> {
				InputStream inputStream = invocation.getArgument(0);

				return inputStream.readAllBytes();
			}
		);

		fileUtil.setFile(file);

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testHandleErrorWithHeadlessRejection() throws Exception {
		String messageKey = "query-in-progress";
		long retryAfterSeconds = RandomTestUtil.randomLong();

		FaroEngineClientException faroEngineClientException = _handleError(
			JSONUtil.put(
				"errorAttributes", JSONUtil.put("messageKey", messageKey)
			).toString(),
			String.valueOf(retryAfterSeconds), 503);

		Assert.assertTrue(
			faroEngineClientException instanceof
				HeadlessRequestRejectedException);

		HeadlessRequestRejectedException headlessRequestRejectedException =
			(HeadlessRequestRejectedException)faroEngineClientException;

		Assert.assertNull(headlessRequestRejectedException.getLimit());
		Assert.assertEquals(
			messageKey, headlessRequestRejectedException.getMessageKey());
		Assert.assertEquals(
			Long.valueOf(retryAfterSeconds),
			headlessRequestRejectedException.getRetryAfterSeconds());
	}

	@Test
	public void testHandleErrorWithLimit() throws Exception {
		long limit = RandomTestUtil.randomLong();
		String messageKey = "page-size-limit-exceeded";

		FaroEngineClientException faroEngineClientException = _handleError(
			JSONUtil.put(
				"errorAttributes",
				JSONUtil.put(
					"limit", limit
				).put(
					"messageKey", messageKey
				)
			).toString(),
			null, 400);

		Assert.assertTrue(
			faroEngineClientException instanceof
				HeadlessRequestRejectedException);

		HeadlessRequestRejectedException headlessRequestRejectedException =
			(HeadlessRequestRejectedException)faroEngineClientException;

		Assert.assertEquals(
			Long.valueOf(limit), headlessRequestRejectedException.getLimit());
		Assert.assertEquals(
			messageKey, headlessRequestRejectedException.getMessageKey());
		Assert.assertNull(
			headlessRequestRejectedException.getRetryAfterSeconds());
	}

	@Test
	public void testHandleErrorWithOtherMessageKey() throws Exception {
		String json = JSONUtil.put(
			"errorAttributes",
			JSONUtil.put("messageKey", RandomTestUtil.randomString())
		).toString();

		FaroEngineClientException faroEngineClientException = _handleError(
			json, null, 500);

		Assert.assertEquals(
			FaroEngineClientException.class,
			faroEngineClientException.getClass());
		Assert.assertEquals(json, faroEngineClientException.getMessage());
	}

	@Test
	public void testHandleErrorWithoutJSON() throws Exception {
		String content = RandomTestUtil.randomString();

		FaroEngineClientException faroEngineClientException = _handleError(
			content, null, 500);

		Assert.assertEquals(
			FaroEngineClientException.class,
			faroEngineClientException.getClass());
		Assert.assertEquals(content, faroEngineClientException.getMessage());
	}

	private FaroEngineClientException _handleError(
			String content, String retryAfter, int statusCode)
		throws Exception {

		ClientHttpResponse clientHttpResponse = Mockito.mock(
			ClientHttpResponse.class);

		Mockito.when(
			clientHttpResponse.getBody()
		).thenReturn(
			new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))
		);

		HttpHeaders httpHeaders = new HttpHeaders();

		if (retryAfter != null) {
			httpHeaders.set(HttpHeaders.RETRY_AFTER, retryAfter);
		}

		Mockito.when(
			clientHttpResponse.getHeaders()
		).thenReturn(
			httpHeaders
		);

		Mockito.when(
			clientHttpResponse.getStatusCode()
		).thenReturn(
			HttpStatusCode.valueOf(statusCode)
		);

		ResponseErrorHandler responseErrorHandler = new ResponseErrorHandler();

		return Assert.assertThrows(
			FaroEngineClientException.class,
			() -> responseErrorHandler.handleError(
				URI.create(RandomTestUtil.randomString()), HttpMethod.GET,
				clientHttpResponse));
	}

}