/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.http.client;

import com.liferay.osb.faro.util.FaroRequestAudit;
import com.liferay.osb.faro.util.FaroThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.io.IOException;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * @author Shinn Lok
 */
public class AuditClientHttpRequestInterceptor
	implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(
			HttpRequest httpRequest, byte[] bytes,
			ClientHttpRequestExecution clientHttpRequestExecution)
		throws IOException {

		FaroRequestAudit faroRequestAudit =
			FaroThreadLocal.getFaroRequestAudit();

		if ((faroRequestAudit == null) || !faroRequestAudit.isEnabled()) {
			return clientHttpRequestExecution.execute(httpRequest, bytes);
		}

		FaroRequestAudit childFaroRequestAudit = new FaroRequestAudit();

		HttpMethod httpMethod = httpRequest.getMethod();

		if (httpMethod != null) {
			childFaroRequestAudit.setMethod(httpMethod.toString());
		}

		URI uri = httpRequest.getURI();

		String urlPath = uri.getPath();

		String query = uri.getQuery();

		if (query != null) {
			urlPath = StringBundler.concat(urlPath, StringPool.QUESTION, query);
		}

		childFaroRequestAudit.setURLPath(urlPath);

		childFaroRequestAudit.setStartTime(System.currentTimeMillis());

		ClientHttpResponse clientHttpResponse = null;

		try {
			clientHttpResponse = clientHttpRequestExecution.execute(
				httpRequest, bytes);

			return clientHttpResponse;
		}
		finally {
			childFaroRequestAudit.setEndTime(System.currentTimeMillis());

			if (clientHttpResponse != null) {
				HttpStatusCode httpStatusCode =
					clientHttpResponse.getStatusCode();

				childFaroRequestAudit.setStatusCode(httpStatusCode.value());
			}

			faroRequestAudit.addChildFaroRequestAudit(childFaroRequestAudit);
		}
	}

}