/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.http.client;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;

import java.nio.charset.StandardCharsets;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

/**
 * @author Eudaldo Alonso
 */
public class LoggingClientHttpRequestInterceptorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testInterceptWithDebugEnabled() throws Exception {
		ClientHttpRequestExecution clientHttpRequestExecution = Mockito.mock(
			ClientHttpRequestExecution.class);
		ClientHttpResponse clientHttpResponse = Mockito.mock(
			ClientHttpResponse.class);

		String body = RandomTestUtil.randomString();

		Mockito.when(
			clientHttpResponse.getBody()
		).thenReturn(
			new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))
		);

		Mockito.when(
			clientHttpRequestExecution.execute(
				Mockito.any(HttpRequest.class), Mockito.any(byte[].class))
		).thenReturn(
			clientHttpResponse
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				LoggingClientHttpRequestInterceptor.class.getName(),
				LoggerTestUtil.DEBUG)) {

			ClientHttpResponse interceptedClientHttpResponse =
				_loggingClientHttpRequestInterceptor.intercept(
					Mockito.mock(HttpRequest.class), new byte[0],
					clientHttpRequestExecution);

			Assert.assertEquals(
				body,
				StreamUtils.copyToString(
					interceptedClientHttpResponse.getBody(),
					StandardCharsets.UTF_8));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			LogEntry logEntry = logEntries.get(logEntries.size() - 1);

			Assert.assertEquals(
				"Response body: " + body, logEntry.getMessage());
		}

		Mockito.verify(
			clientHttpResponse
		).close();
	}

	private final LoggingClientHttpRequestInterceptor
		_loggingClientHttpRequestInterceptor =
			new LoggingClientHttpRequestInterceptor();

}