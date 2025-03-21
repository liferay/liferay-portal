/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.portlet.LiferayEventRequest;

import jakarta.portlet.Event;
import jakarta.portlet.PortletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public class EventRequestImpl
	extends PortletRequestImpl implements LiferayEventRequest {

	@Override
	public Event getEvent() {
		return _event;
	}

	@Override
	public String getLifecycle() {
		return PortletRequest.EVENT_PHASE;
	}

	@Override
	public void setEvent(Event event) {
		_event = event;
	}

	private Event _event;

}