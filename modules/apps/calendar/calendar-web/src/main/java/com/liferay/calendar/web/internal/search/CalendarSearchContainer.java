/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.search;

import com.liferay.portal.kernel.dao.search.SearchContainer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

/**
 * @author Mariano Álvaro Sáiz
 */
public class CalendarSearchContainer<R> extends SearchContainer<R> {

	public static final String EMPTY_RESULTS_MESSAGE =
		"no-calendar-resources-were-found";

	public CalendarSearchContainer(
		PortletRequest portletRequest, String curParam,
		PortletURL iteratorURL) {

		super(
			portletRequest, new CalendarResourceDisplayTerms(portletRequest),
			new CalendarResourceDisplayTerms(portletRequest), curParam,
			DEFAULT_DELTA, iteratorURL, null, EMPTY_RESULTS_MESSAGE);

		CalendarResourceDisplayTerms displayTerms =
			(CalendarResourceDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			CalendarResourceDisplayTerms.ACTIVE,
			String.valueOf(displayTerms.isActive()));
		iteratorURL.setParameter(
			CalendarResourceDisplayTerms.DESCRIPTION,
			displayTerms.getDescription());
		iteratorURL.setParameter(
			CalendarResourceDisplayTerms.NAME, displayTerms.getName());
		iteratorURL.setParameter(
			CalendarResourceDisplayTerms.SCOPE,
			String.valueOf(displayTerms.getScope()));
	}

}