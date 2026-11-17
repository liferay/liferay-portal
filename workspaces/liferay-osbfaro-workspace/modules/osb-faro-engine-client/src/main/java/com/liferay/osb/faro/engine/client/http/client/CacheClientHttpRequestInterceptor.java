/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.http.client;

import com.liferay.osb.faro.engine.client.FaroClientHttpResponse;
import com.liferay.osb.faro.engine.client.cache.FaroCache;
import com.liferay.osb.faro.engine.client.constants.OSBAsahHeaderConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * @author Shinn Lok
 */
public class CacheClientHttpRequestInterceptor
	implements ClientHttpRequestInterceptor {

	public CacheClientHttpRequestInterceptor(FaroCache faroCache) {
		_faroCache = faroCache;
	}

	@Override
	public ClientHttpResponse intercept(
			HttpRequest httpRequest, byte[] bytes,
			ClientHttpRequestExecution clientHttpRequestExecution)
		throws IOException {

		HttpMethod httpMethod = httpRequest.getMethod();

		if ((httpMethod != null) &&
			(httpMethod.equals(HttpMethod.PATCH) ||
			 httpMethod.equals(HttpMethod.POST) ||
			 httpMethod.equals(HttpMethod.PUT) ||
			 httpMethod.equals(HttpMethod.DELETE))) {

			_faroCache.clear();
		}

		if (bytes.length > 0) {
			return clientHttpRequestExecution.execute(httpRequest, bytes);
		}

		String key = getKey(httpRequest);

		FaroClientHttpResponse faroClientHttpResponse = _faroCache.get(key);

		if (faroClientHttpResponse != null) {
			return faroClientHttpResponse;
		}

		ClientHttpResponse clientHttpResponse =
			clientHttpRequestExecution.execute(httpRequest, bytes);

		HttpStatusCode httpStatusCode = clientHttpResponse.getStatusCode();

		if (httpStatusCode.value() != HttpServletResponse.SC_OK) {
			return clientHttpResponse;
		}

		try {
			faroClientHttpResponse = new FaroClientHttpResponse(
				clientHttpResponse);

			_faroCache.put(key, faroClientHttpResponse);

			return faroClientHttpResponse;
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}
	}

	protected String getKey(HttpRequest httpRequest) {
		StringBundler sb = new StringBundler(5);

		HttpMethod httpMethod = httpRequest.getMethod();

		if (httpMethod != null) {
			sb.append(httpMethod.name());
		}

		sb.append(StringPool.COLON);
		sb.append(httpRequest.getURI());
		sb.append(StringPool.COLON);
		sb.append(getProjectId(httpRequest));

		return sb.toString();
	}

	protected String getProjectId(HttpRequest httpRequest) {
		HttpHeaders httpHeaders = httpRequest.getHeaders();

		List<String> headers = httpHeaders.getOrEmpty(
			OSBAsahHeaderConstants.PROJECT_ID);

		if (headers.isEmpty()) {
			return StringPool.BLANK;
		}

		return headers.get(0);
	}

	private final FaroCache _faroCache;

}