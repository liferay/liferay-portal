/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Validator;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import java.io.IOException;

import java.util.List;
import java.util.Map;

/**
 * @author Christian Moura
 */
public class OpenIdConnectHttpUtil {

	public static HTTPResponse send(HTTPRequest httpRequest)
		throws IOException, ParseException {

		Http.Options httpOptions = _toHttpOptions(httpRequest);

		String responseJSON = HttpUtil.URLtoString(httpOptions);

		Http.Response liferayHttpResponse = httpOptions.getResponse();

		HTTPResponse httpResponse = new HTTPResponse(
			liferayHttpResponse.getResponseCode());

		httpResponse.setBody(responseJSON);

		String contentType = liferayHttpResponse.getContentType();

		if (contentType != null) {
			httpResponse.setContentType(contentType);
		}

		return httpResponse;
	}

	private static Http.Options _toHttpOptions(HTTPRequest httpRequest) {
		Http.Options httpOptions = new Http.Options();

		httpOptions.setCookieSpec(Http.CookieSpec.STANDARD);

		Map<String, List<String>> headerMap = httpRequest.getHeaderMap();

		if (headerMap != null) {
			for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
				for (String value : entry.getValue()) {
					httpOptions.addHeader(entry.getKey(), value);
				}
			}
		}

		String body = httpRequest.getBody();

		if (Validator.isNotNull(body)) {
			ContentType entityContentType = httpRequest.getEntityContentType();

			String contentType =
				(entityContentType == null) ?
					"application/x-www-form-urlencoded" :
						entityContentType.toString();

			httpOptions.setBody(body, contentType, StringPool.UTF8);
		}

		httpOptions.setLocation(String.valueOf(httpRequest.getURL()));

		if (HTTPRequest.Method.POST.equals(httpRequest.getMethod())) {
			httpOptions.setPost(true);
		}

		httpOptions.setTimeout(
			Math.max(
				httpRequest.getConnectTimeout(), httpRequest.getReadTimeout()));

		return httpOptions;
	}

}