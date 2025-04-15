/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 */
public class LogSessionIdAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (_log.isDebugEnabled()) {
			HttpSession httpSession = httpServletRequest.getSession();

			_log.debug(
				StringBundler.concat(
					"Session id ", httpSession.getId(), " is ",
					!httpSession.isNew() ? "not " : "", "new"));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LogSessionIdAction.class);

}