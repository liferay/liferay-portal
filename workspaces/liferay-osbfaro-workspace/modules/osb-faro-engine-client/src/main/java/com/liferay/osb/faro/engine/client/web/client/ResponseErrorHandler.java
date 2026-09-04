/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.web.client;

import com.liferay.osb.faro.engine.client.exception.DuplicateEntryException;
import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.exception.InvalidFilterException;
import com.liferay.osb.faro.engine.client.exception.NoSuchEntryException;
import com.liferay.portal.kernel.util.FileUtil;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.net.URI;

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

		throw new FaroEngineClientException(response);
	}

}