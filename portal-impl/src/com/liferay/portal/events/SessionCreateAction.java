/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.portal.kernel.events.SessionAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 */
public class SessionCreateAction extends SessionAction {

	@Override
	public void run(HttpSession httpSession) {
		if (_log.isDebugEnabled()) {
			_log.debug(httpSession.getId());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SessionCreateAction.class);

}