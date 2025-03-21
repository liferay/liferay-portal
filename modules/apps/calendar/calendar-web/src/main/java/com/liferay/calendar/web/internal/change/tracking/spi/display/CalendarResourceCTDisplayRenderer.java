/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.change.tracking.spi.display;

import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class CalendarResourceCTDisplayRenderer
	extends BaseCTDisplayRenderer<CalendarResource> {

	@Override
	public String[] getAvailableLanguageIds(CalendarResource calendarResource) {
		return calendarResource.getAvailableLanguageIds();
	}

	@Override
	public String getDefaultLanguageId(CalendarResource calendarResource) {
		return calendarResource.getDefaultLanguageId();
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			CalendarResource calendarResource)
		throws Exception {

		Group group = _groupLocalService.getGroup(
			calendarResource.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, CalendarPortletKeys.CALENDAR, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_calendar_resource.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"calendarResourceId", calendarResource.getCalendarResourceId()
		).buildString();
	}

	@Override
	public Class<CalendarResource> getModelClass() {
		return CalendarResource.class;
	}

	@Override
	public String getTitle(Locale locale, CalendarResource calendarResource) {
		return calendarResource.getName(locale);
	}

	@Override
	public boolean isHideable(CalendarResource calendarResource) {
		return true;
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<CalendarResource> displayBuilder) {

		CalendarResource calendarResource = displayBuilder.getModel();

		Locale locale = displayBuilder.getLocale();

		displayBuilder.display(
			"name", calendarResource.getName(locale)
		).display(
			"description", calendarResource.getDescription(locale)
		).display(
			"active", calendarResource.isActive()
		).display(
			"default-calendar",
			() -> {
				Calendar defaultCalendar =
					calendarResource.getDefaultCalendar();

				if (defaultCalendar != null) {
					return defaultCalendar.getName(locale);
				}

				return null;
			}
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}