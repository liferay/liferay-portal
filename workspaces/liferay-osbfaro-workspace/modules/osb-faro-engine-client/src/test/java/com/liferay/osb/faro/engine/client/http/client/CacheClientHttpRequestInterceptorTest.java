/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.http.client;

import com.liferay.osb.faro.engine.client.cache.FaroCache;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.net.URI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

/**
 * @author Eudaldo Alonso
 */
public class CacheClientHttpRequestInterceptorTest {

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

		Mockito.when(
			_clientHttpRequestExecution.execute(
				Mockito.any(HttpRequest.class), Mockito.any(byte[].class))
		).thenAnswer(
			invocation -> {
				ClientHttpResponse clientHttpResponse = Mockito.mock(
					ClientHttpResponse.class);

				String body = RandomTestUtil.randomString();

				Mockito.when(
					clientHttpResponse.getBody()
				).thenReturn(
					new ByteArrayInputStream(body.getBytes())
				);

				Mockito.when(
					clientHttpResponse.getStatusCode()
				).thenReturn(
					HttpStatus.OK
				);

				return clientHttpResponse;
			}
		);
	}

	@Test
	public void testIntercept() throws Exception {
		HttpRequest httpRequest = _getHttpRequest(HttpMethod.GET);

		ClientHttpResponse clientHttpResponse =
			_cacheClientHttpRequestInterceptor.intercept(
				httpRequest, new byte[0], _clientHttpRequestExecution);

		Assert.assertSame(
			clientHttpResponse,
			_cacheClientHttpRequestInterceptor.intercept(
				httpRequest, new byte[0], _clientHttpRequestExecution));

		Mockito.verify(
			_clientHttpRequestExecution, Mockito.times(1)
		).execute(
			Mockito.eq(httpRequest), Mockito.any(byte[].class)
		);
	}

	@Test
	public void testInterceptWithPost() throws Exception {
		HttpRequest httpRequest = _getHttpRequest(HttpMethod.GET);

		ClientHttpResponse clientHttpResponse =
			_cacheClientHttpRequestInterceptor.intercept(
				httpRequest, new byte[0], _clientHttpRequestExecution);

		_cacheClientHttpRequestInterceptor.intercept(
			_getHttpRequest(HttpMethod.POST), new byte[0],
			_clientHttpRequestExecution);

		Assert.assertNotSame(
			clientHttpResponse,
			_cacheClientHttpRequestInterceptor.intercept(
				httpRequest, new byte[0], _clientHttpRequestExecution));

		Mockito.verify(
			_clientHttpRequestExecution, Mockito.times(2)
		).execute(
			Mockito.eq(httpRequest), Mockito.any(byte[].class)
		);
	}

	private HttpRequest _getHttpRequest(HttpMethod httpMethod) {
		HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

		Mockito.when(
			httpRequest.getHeaders()
		).thenReturn(
			new HttpHeaders()
		);

		Mockito.when(
			httpRequest.getMethod()
		).thenReturn(
			httpMethod
		);

		Mockito.when(
			httpRequest.getURI()
		).thenReturn(
			URI.create("http://localhost/" + RandomTestUtil.randomString())
		);

		return httpRequest;
	}

	private final CacheClientHttpRequestInterceptor
		_cacheClientHttpRequestInterceptor =
			new CacheClientHttpRequestInterceptor(new FaroCache());
	private final ClientHttpRequestExecution _clientHttpRequestExecution =
		Mockito.mock(ClientHttpRequestExecution.class);

}