/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.validhostname;

import com.liferay.portal.kernel.servlet.TryFilter;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Shuyang Zhou
 */
public class ValidHostNameFilter extends BasePortalFilter implements TryFilter {

	@Override
	public Object doFilterTry(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String serverName = httpServletRequest.getServerName();

		if (!PortalUtil.isValidPortalDomain(
				PortalUtil.getDefaultCompanyId(), serverName)) {

			throw new RuntimeException("Invalid host name " + serverName);
		}

		return null;
	}

}