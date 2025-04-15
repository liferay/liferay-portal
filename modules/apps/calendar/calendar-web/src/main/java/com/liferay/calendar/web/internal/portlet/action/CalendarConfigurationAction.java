/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.portlet.action;

import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SessionClicks;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletPreferences;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Lundgren
 * @author Fabio Pezzutto
 */
@Component(
	property = "jakarta.portlet.name=" + CalendarPortletKeys.CALENDAR,
	service = ConfigurationAction.class
)
public class CalendarConfigurationAction extends DefaultConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		_updateDisplaySettings(actionRequest);
		_updateUserSettings(actionRequest);

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	private void _updateDisplaySettings(ActionRequest actionRequest)
		throws Exception {

		PortletPreferences portletPreferences = actionRequest.getPreferences();

		boolean displaySchedulerHeader = ParamUtil.getBoolean(
			actionRequest, "displaySchedulerHeader");
		boolean displaySchedulerOnly = ParamUtil.getBoolean(
			actionRequest, "displaySchedulerOnly");
		int eventsPerPage = ParamUtil.getInteger(
			actionRequest, "eventsPerPage");
		int maxDaysDisplayed = ParamUtil.getInteger(
			actionRequest, "maxDaysDisplayed");
		boolean showUserEvents = ParamUtil.getBoolean(
			actionRequest, "showUserEvents");
		boolean showAgendaView = ParamUtil.getBoolean(
			actionRequest, "showAgendaView");
		boolean showDayView = ParamUtil.getBoolean(
			actionRequest, "showDayView");
		boolean showWeekView = ParamUtil.getBoolean(
			actionRequest, "showWeekView");
		boolean showMonthView = ParamUtil.getBoolean(
			actionRequest, "showMonthView");

		portletPreferences.setValue(
			"displaySchedulerHeader", String.valueOf(displaySchedulerHeader));
		portletPreferences.setValue(
			"displaySchedulerOnly", String.valueOf(displaySchedulerOnly));
		portletPreferences.setValue(
			"eventsPerPage", String.valueOf(eventsPerPage));
		portletPreferences.setValue(
			"maxDaysDisplayed", String.valueOf(maxDaysDisplayed));
		portletPreferences.setValue(
			"showAgendaView", String.valueOf(showAgendaView));
		portletPreferences.setValue("showDayView", String.valueOf(showDayView));
		portletPreferences.setValue(
			"showWeekView", String.valueOf(showWeekView));
		portletPreferences.setValue(
			"showMonthView", String.valueOf(showMonthView));
		portletPreferences.setValue(
			"showUserEvents", String.valueOf(showUserEvents));

		portletPreferences.store();
	}

	private void _updateUserSettings(ActionRequest actionRequest)
		throws Exception {

		PortletPreferences portletPreferences = actionRequest.getPreferences();

		int defaultDuration = ParamUtil.getInteger(
			actionRequest, "defaultDuration");
		String defaultView = ParamUtil.getString(actionRequest, "defaultView");
		String timeFormat = ParamUtil.getString(actionRequest, "timeFormat");
		String timeZoneId = ParamUtil.getString(actionRequest, "timeZoneId");
		boolean usePortalTimeZone = ParamUtil.getBoolean(
			actionRequest, "usePortalTimeZone");
		int weekStartsOn = ParamUtil.getInteger(actionRequest, "weekStartsOn");

		portletPreferences.setValue(
			"defaultDuration", String.valueOf(defaultDuration));
		portletPreferences.setValue("defaultView", defaultView);
		portletPreferences.setValue("timeFormat", timeFormat);
		portletPreferences.setValue("timeZoneId", timeZoneId);
		portletPreferences.setValue(
			"usePortalTimeZone", String.valueOf(usePortalTimeZone));
		portletPreferences.setValue(
			"weekStartsOn", String.valueOf(weekStartsOn));

		SessionClicks.put(
			_portal.getHttpServletRequest(actionRequest),
			"com.liferay.calendar.web_defaultView", defaultView);

		portletPreferences.store();
	}

	@Reference
	private Portal _portal;

}