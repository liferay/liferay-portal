/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.http.client;

import com.liferay.osb.faro.engine.client.constants.OSBAsahHeaderConstants;
import com.liferay.osb.faro.util.FaroThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

/**
 * @author Leslie Wong
 */
public class OriginClientHttpRequestInterceptorTest {

	@After
	public void tearDown() {
		FaroThreadLocal.setOrigin(null);
	}

	@Test
	public void testInterceptWithOrigin() throws Exception {
		String origin = RandomTestUtil.randomString();

		FaroThreadLocal.setOrigin(origin);

		HttpHeaders httpHeaders = _intercept();

		Assert.assertEquals(
			origin, httpHeaders.getFirst(OSBAsahHeaderConstants.ORIGIN));
	}

	@Test
	public void testInterceptWithoutOrigin() throws Exception {
		HttpHeaders httpHeaders = _intercept();

		Assert.assertFalse(
			httpHeaders.containsHeader(OSBAsahHeaderConstants.ORIGIN));
	}

	private HttpHeaders _intercept() throws Exception {
		HttpHeaders httpHeaders = new HttpHeaders();

		HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

		Mockito.when(
			httpRequest.getHeaders()
		).thenReturn(
			httpHeaders
		);

		ClientHttpRequestExecution clientHttpRequestExecution = Mockito.mock(
			ClientHttpRequestExecution.class);

		_originClientHttpRequestInterceptor.intercept(
			httpRequest, new byte[0], clientHttpRequestExecution);

		Mockito.verify(
			clientHttpRequestExecution
		).execute(
			Mockito.eq(httpRequest), Mockito.any(byte[].class)
		);

		return httpHeaders;
	}

	private final OriginClientHttpRequestInterceptor
		_originClientHttpRequestInterceptor =
			new OriginClientHttpRequestInterceptor();

}