/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client;

import com.liferay.portal.kernel.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

/**
 * @author Shinn Lok
 */
public class FaroClientHttpResponse
	implements ClientHttpResponse, Cloneable, Serializable {

	public FaroClientHttpResponse(
		byte[] bytes, HttpHeaders httpHeaders, HttpStatusCode httpStatusCode) {

		if (bytes == null) {
			_bytes = new byte[0];
		}
		else {
			_bytes = bytes.clone();
		}

		if (httpHeaders == null) {
			_httpHeaders = new HttpHeaders();
		}
		else {
			_httpHeaders = httpHeaders;
		}

		_httpStatusCode = httpStatusCode;
	}

	public FaroClientHttpResponse(ClientHttpResponse clientHttpResponse)
		throws Exception {

		this(
			FileUtil.getBytes(clientHttpResponse.getBody()),
			clientHttpResponse.getHeaders(),
			clientHttpResponse.getStatusCode());
	}

	@Override
	public FaroClientHttpResponse clone() throws CloneNotSupportedException {
		HttpHeaders httpHeaders = (HttpHeaders)super.clone();

		for (Map.Entry<String, List<String>> entry : _httpHeaders.headerSet()) {
			httpHeaders.put(entry.getKey(), new LinkedList<>(entry.getValue()));
		}

		return new FaroClientHttpResponse(
			_bytes.clone(), httpHeaders, _httpStatusCode);
	}

	@Override
	public void close() {
	}

	@Override
	public InputStream getBody() {
		return new ByteArrayInputStream(_bytes);
	}

	@Override
	public HttpHeaders getHeaders() {
		return _httpHeaders;
	}

	@Override
	public HttpStatusCode getStatusCode() {
		return _httpStatusCode;
	}

	@Override
	public String getStatusText() {
		if (_httpStatusCode instanceof HttpStatus httpStatus) {
			return httpStatus.getReasonPhrase();
		}

		return "";
	}

	private static final long serialVersionUID = 1L;

	private final byte[] _bytes;
	private final HttpHeaders _httpHeaders;
	private final HttpStatusCode _httpStatusCode;

}