/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.portal.kernel.events.Action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @author Brian Wing Shun Chan
 */
public class SampleServicePreAction extends Action {

	@Override
	public void run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		setSharedSessionAttributes(httpServletRequest);
	}

	public void setSharedSessionAttributes(
		HttpServletRequest httpServletRequest) {

		// Modify portal.properties property "session.shared.attributes". Make
		// sure that "TEST_SHARED_" is also one of the prefixed attributes that
		// will be shared across all portlets.

		HttpSession httpSession = httpServletRequest.getSession();

		httpSession.setAttribute("TEST_SHARED_HELLO", "world");
	}

}