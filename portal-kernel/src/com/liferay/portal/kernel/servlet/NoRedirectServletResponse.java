/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 */
public class NoRedirectServletResponse extends HttpServletResponseWrapper {

	public NoRedirectServletResponse(HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
	}

	public String getRedirectLocation() {
		return _redirectLocation;
	}

	@Override
	public void sendRedirect(String location) {

		// Disable send redirect

		_redirectLocation = location;
	}

	private String _redirectLocation;

}