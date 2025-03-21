/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.sessionmaxallowed;

import com.liferay.portal.kernel.servlet.TryFinallyFilter;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 */
public class SessionMaxAllowedFilter
	extends BasePortalFilter implements TryFinallyFilter {

	@Override
	public void doFilterFinally(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Object object) {

		if (PropsValues.SESSION_MAX_ALLOWED > 0) {
			HttpSession httpSession = httpServletRequest.getSession();

			Boolean sessionMaxAllowed = (Boolean)httpSession.getAttribute(
				WebKeys.SESSION_MAX_ALLOWED);

			if ((sessionMaxAllowed != null) && sessionMaxAllowed) {
				httpSession.invalidate();
			}
		}
	}

	@Override
	public Object doFilterTry(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return null;
	}

}