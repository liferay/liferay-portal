/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.servlet.SharedSessionServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 */
public class SessionLayoutClone implements LayoutClone {

	@Override
	public String get(HttpServletRequest httpServletRequest, long plid) {
		HttpSession httpSession = getPortalSession(httpServletRequest);

		return (String)httpSession.getAttribute(encodeKey(plid));
	}

	@Override
	public void update(
		HttpServletRequest httpServletRequest, long plid, String typeSettings) {

		HttpSession httpSession = getPortalSession(httpServletRequest);

		httpSession.setAttribute(encodeKey(plid), typeSettings);
	}

	protected String encodeKey(long plid) {
		return StringBundler.concat(
			SessionLayoutClone.class.getName(), StringPool.POUND,
			StringUtil.toHexString(plid));
	}

	protected HttpSession getPortalSession(
		HttpServletRequest httpServletRequest) {

		HttpServletRequest originalHttpServletRequest = httpServletRequest;

		while (originalHttpServletRequest instanceof
					HttpServletRequestWrapper) {

			if (originalHttpServletRequest instanceof
					SharedSessionServletRequest) {

				SharedSessionServletRequest sharedSessionServletRequest =
					(SharedSessionServletRequest)originalHttpServletRequest;

				return sharedSessionServletRequest.getSharedSession();
			}

			HttpServletRequestWrapper httpServletRequestWrapper =
				(HttpServletRequestWrapper)originalHttpServletRequest;

			originalHttpServletRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();
		}

		return httpServletRequest.getSession();
	}

}