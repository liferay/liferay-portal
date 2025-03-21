/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class ClearRenderParametersAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		// Some users are confused by the behavior stated in the JSR 168 spec
		// that render parameters are saved across requests. Set this class to
		// always clear render parameters to please those users. You can also
		// modify the "layout.remember.request.window.state.maximized" property
		// in portal.properties to disable the remembering of window states
		// across requests.

		HttpSession httpSession = httpServletRequest.getSession();

		Map<Long, Map<String, Map<String, String[]>>> renderParametersPool =
			(Map<Long, Map<String, Map<String, String[]>>>)
				httpSession.getAttribute(WebKeys.PORTLET_RENDER_PARAMETERS);

		if (renderParametersPool != null) {
			renderParametersPool.clear();
		}
	}

}