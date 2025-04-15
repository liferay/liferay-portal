/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public interface TryFinallyFilter extends TryFilter {

	public void doFilterFinally(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Object object)
		throws Exception;

}