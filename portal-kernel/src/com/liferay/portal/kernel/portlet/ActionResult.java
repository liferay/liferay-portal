/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import jakarta.portlet.Event;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;

/**
 * @author Shuyang Zhou
 */
public class ActionResult implements Serializable {

	public static final ActionResult EMPTY_ACTION_RESULT = new ActionResult(
		Collections.<Event>emptyList(), null);

	public ActionResult(List<Event> events, String location) {
		_events = events;
		_location = location;
	}

	public List<Event> getEvents() {
		return _events;
	}

	public String getLocation() {
		return _location;
	}

	private final List<Event> _events;
	private final String _location;

}