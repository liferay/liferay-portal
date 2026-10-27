/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.web.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.osb.faro.engine.client.exception.DuplicateEntryException;
import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.exception.HeadlessRequestRejectedException;
import com.liferay.osb.faro.engine.client.exception.InvalidFilterException;
import com.liferay.osb.faro.engine.client.exception.NoSuchEntryException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.net.URI;

import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * @author Shinn Lok
 */
public class ResponseErrorHandler extends DefaultResponseErrorHandler {

	@Override
	public void handleError(
			URI uri, HttpMethod httpMethod,
			ClientHttpResponse clientHttpResponse)
		throws IOException {

		HttpStatusCode httpStatusCode = clientHttpResponse.getStatusCode();

		int statusCode = httpStatusCode.value();

		if (statusCode < HttpServletResponse.SC_BAD_REQUEST) {
			super.handleError(uri, httpMethod, clientHttpResponse);

			return;
		}

		String response = new String(
			FileUtil.getBytes(clientHttpResponse.getBody()));

		if (statusCode == HttpServletResponse.SC_CONFLICT) {
			throw new DuplicateEntryException(response);
		}

		if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
			throw new NoSuchEntryException(response);
		}

		if (statusCode == 422) {
			throw new InvalidFilterException(response);
		}

		HeadlessRequestRejectedException headlessRequestRejectedException =
			_getHeadlessRequestRejectedException(
				clientHttpResponse.getHeaders(), response);

		if (headlessRequestRejectedException != null) {
			throw headlessRequestRejectedException;
		}

		throw new FaroEngineClientException(response, statusCode);
	}

	private HeadlessRequestRejectedException
		_getHeadlessRequestRejectedException(
			HttpHeaders httpHeaders, String response) {

		JsonNode jsonNode = null;

		try {
			jsonNode = _objectMapper.readTree(response);
		}
		catch (JsonProcessingException jsonProcessingException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonProcessingException);
			}

			return null;
		}

		JsonNode errorAttributesJsonNode = jsonNode.path("errorAttributes");

		JsonNode messageKeyJsonNode = errorAttributesJsonNode.path(
			"messageKey");

		String messageKey = messageKeyJsonNode.asText(null);

		if ((messageKey == null) || !_messageKeys.contains(messageKey)) {
			return null;
		}

		return new HeadlessRequestRejectedException(
			_getLong(errorAttributesJsonNode.path("limit")), response,
			messageKey, _getRetryAfterSeconds(httpHeaders));
	}

	private Long _getLong(JsonNode jsonNode) {
		if (!jsonNode.canConvertToLong()) {
			return null;
		}

		return jsonNode.asLong();
	}

	private Long _getRetryAfterSeconds(HttpHeaders httpHeaders) {
		long retryAfterSeconds = GetterUtil.getLong(
			httpHeaders.getFirst(HttpHeaders.RETRY_AFTER), -1);

		if (retryAfterSeconds < 0) {
			return null;
		}

		return retryAfterSeconds;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResponseErrorHandler.class);

	private static final Set<String> _messageKeys = Set.of(
		"date-range-limit-exceeded", "page-size-limit-exceeded",
		"query-in-progress", "query-timeout-exceeded",
		"request-deadline-exceeded", "service-capacity-unavailable",
		"too-many-concurrent-requests");
	private static final ObjectMapper _objectMapper = new ObjectMapper();

}