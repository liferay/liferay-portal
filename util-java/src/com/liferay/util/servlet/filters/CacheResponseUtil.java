/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util.servlet.filters;

import com.liferay.portal.kernel.servlet.Header;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;
import java.util.Set;

/**
 * @author Alexander Chow
 */
public class CacheResponseUtil {

	public static void setHeaders(
		HttpServletResponse httpServletResponse,
		Map<String, Set<Header>> headers) {

		if (httpServletResponse.isCommitted()) {
			return;
		}

		for (Map.Entry<String, Set<Header>> entry : headers.entrySet()) {
			String key = entry.getKey();

			boolean first = true;

			for (Header header : entry.getValue()) {
				if (first) {
					header.setToResponse(key, httpServletResponse);

					first = false;
				}
				else {
					header.addToResponse(key, httpServletResponse);
				}
			}
		}
	}

	public static void write(
			HttpServletResponse httpServletResponse,
			CacheResponseData cacheResponseData)
		throws IOException {

		setHeaders(httpServletResponse, cacheResponseData.getHeaders());

		httpServletResponse.setContentType(cacheResponseData.getContentType());

		ServletResponseUtil.write(
			httpServletResponse, cacheResponseData.getByteBuffer());
	}

}