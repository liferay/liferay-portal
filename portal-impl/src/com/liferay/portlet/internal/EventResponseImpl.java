/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayEventResponse;

import jakarta.portlet.EventRequest;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class EventResponseImpl
	extends StateAwareResponseImpl implements LiferayEventResponse {

	@Override
	public String getLifecycle() {
		return PortletRequest.EVENT_PHASE;
	}

	public void init(
			PortletRequestImpl portletRequestImpl,
			HttpServletResponse httpServletResponse, User user, Layout layout)
		throws PortletModeException, WindowStateException {

		init(portletRequestImpl, httpServletResponse, user, layout, false);
	}

	@Override
	public void setRenderParameters(EventRequest eventRequest) {
		if (eventRequest == null) {
			throw new IllegalArgumentException();
		}

		setRenderParameters(eventRequest.getParameterMap());
	}

}