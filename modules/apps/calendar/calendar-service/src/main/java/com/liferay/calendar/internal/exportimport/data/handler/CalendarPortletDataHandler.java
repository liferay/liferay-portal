/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.internal.exportimport.data.handler;

import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;

import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	property = "jakarta.portlet.name=" + CalendarPortletKeys.CALENDAR,
	service = PortletDataHandler.class
)
public class CalendarPortletDataHandler extends BasePortletDataHandler {

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Activate
	protected void activate() {
		setDataLevel(DataLevel.PORTLET_INSTANCE);
		setDataPortletPreferences(StringPool.BLANK);
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletPreferences == null) {
			return null;
		}

		portletPreferences.setValue("defaultDuration", StringPool.BLANK);
		portletPreferences.setValue("defaultView", StringPool.BLANK);
		portletPreferences.setValue(
			"displaySchedulerHeader", Boolean.TRUE.toString());
		portletPreferences.setValue(
			"displaySchedulerOnly", Boolean.FALSE.toString());
		portletPreferences.setValue("enableRss", Boolean.TRUE.toString());
		portletPreferences.setValue("eventsPerPage", StringPool.BLANK);
		portletPreferences.setValue("maxDaysDisplayed", StringPool.BLANK);
		portletPreferences.setValue("portletSetupCss", StringPool.BLANK);
		portletPreferences.setValue(
			"portletSetupUseCustomTitle", Boolean.FALSE.toString());
		portletPreferences.setValue("rssDelta", StringPool.BLANK);
		portletPreferences.setValue("rssDisplayStyle", StringPool.BLANK);
		portletPreferences.setValue("rssFeedType", StringPool.BLANK);
		portletPreferences.setValue("rssTimeInterval", StringPool.BLANK);
		portletPreferences.setValue("showAgendaView", Boolean.TRUE.toString());
		portletPreferences.setValue("showDayView", Boolean.TRUE.toString());
		portletPreferences.setValue("showMonthView", Boolean.TRUE.toString());
		portletPreferences.setValue("showUserEvents", Boolean.TRUE.toString());
		portletPreferences.setValue("showWeekView", Boolean.TRUE.toString());
		portletPreferences.setValue("timeFormat", StringPool.BLANK);
		portletPreferences.setValue("timeZoneId", StringPool.BLANK);
		portletPreferences.setValue("usePortalTimeZone", StringPool.BLANK);
		portletPreferences.setValue("weekStartsOn", StringPool.BLANK);

		return portletPreferences;
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}