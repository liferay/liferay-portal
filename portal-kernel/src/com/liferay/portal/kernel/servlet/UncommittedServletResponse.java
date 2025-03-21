/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * @author Brian Wing Shun Chan
 */
public class UncommittedServletResponse extends HttpServletResponseWrapper {

	public UncommittedServletResponse(HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
	}

	@Override
	public boolean isCommitted() {
		return _COMMITTED;
	}

	private static final boolean _COMMITTED = false;

}