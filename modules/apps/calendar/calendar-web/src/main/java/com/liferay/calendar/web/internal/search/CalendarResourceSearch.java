/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.search;

import com.liferay.calendar.model.CalendarResource;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Eduardo Lundgren
 * @author Fabio Pezzutto
 */
public class CalendarResourceSearch extends SearchContainer<CalendarResource> {

	public static final String EMPTY_RESULTS_MESSAGE =
		"no-calendar-resources-were-found";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("name");
			add("description");
			add("active");
		}
	};
	public static Map<String, String> orderableHeaders = HashMapBuilder.put(
		"name", "name"
	).build();

	public CalendarResourceSearch(
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

		try {
			setOrderableHeaders(orderableHeaders);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to initialize calendar resource search", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarResourceSearch.class);

}