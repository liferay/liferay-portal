/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.util;

import com.liferay.petra.lang.CentralizedThreadLocal;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Victor Kammerer
 */
public class HttpServletRequestThreadLocal {

	public static HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest.get();
	}

	public static void setHttpServletRequest(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest.set(httpServletRequest);
	}

	private static final ThreadLocal<HttpServletRequest> _httpServletRequest =
		new CentralizedThreadLocal<>(
			HttpServletRequestThreadLocal.class + "._httpServletRequest",
			() -> null);

}