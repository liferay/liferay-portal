/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 */
public class ServicePostAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {

			// Make sure this is called only once per full request, ignore
			// requests spawned by the portlet

			//String requestURI = GetterUtil.getString(request.getRequestURI());

			// Doesn't this cause a memory leak?

			/*if (requestURI.endsWith("/portal/render_portlet")) {
				return;
			}*/
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServicePostAction.class);

}