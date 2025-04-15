/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.petra.lang.CentralizedThreadLocal;

import jakarta.servlet.http.HttpServlet;

/**
 * @author Brian Wing Shun Chan
 */
public class CleanUpServlet extends HttpServlet {

	@Override
	public void destroy() {
		CentralizedThreadLocal.clearShortLivedCentralizedThreadLocals();
	}

	@Override
	public void init() {
		CentralizedThreadLocal.clearShortLivedCentralizedThreadLocals();
	}

}