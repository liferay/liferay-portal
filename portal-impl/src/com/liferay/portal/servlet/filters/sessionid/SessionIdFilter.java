/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.sessionid;

import com.liferay.portal.kernel.servlet.WrapHttpServletRequestFilter;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * http://forum.java.sun.com/thread.jspa?threadID=197150.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class SessionIdFilter
	extends BasePortalFilter implements WrapHttpServletRequestFilter {

	@Override
	public HttpServletRequest getWrappedHttpServletRequest(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return new SessionIdServletRequest(
			httpServletRequest, httpServletResponse);
	}

}