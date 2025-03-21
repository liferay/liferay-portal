/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.absoluteredirects;

import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;

/**
 * @author Jorge Ferrer
 * @author Shuyang Zhou
 */
public class AbsoluteRedirectsResponse extends HttpServletResponseWrapper {

	public AbsoluteRedirectsResponse(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		super(httpServletResponse);

		_httpServletRequest = httpServletRequest;
	}

	@Override
	public void sendRedirect(String redirect) throws IOException {
		redirect = PortalUtil.getAbsoluteURL(_httpServletRequest, redirect);

		_httpServletRequest.setAttribute(
			AbsoluteRedirectsResponse.class.getName(), redirect);

		super.sendRedirect(redirect);
	}

	private final HttpServletRequest _httpServletRequest;

}