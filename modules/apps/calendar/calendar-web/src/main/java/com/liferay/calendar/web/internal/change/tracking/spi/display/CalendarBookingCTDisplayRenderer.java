/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.change.tracking.spi.display;

import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.recurrence.Frequency;
import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.text.Format;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class CalendarBookingCTDisplayRenderer
	extends BaseCTDisplayRenderer<CalendarBooking> {

	@Override
	public String[] getAvailableLanguageIds(CalendarBooking calendarBooking) {
		return calendarBooking.getAvailableLanguageIds();
	}

	@Override
	public String getDefaultLanguageId(CalendarBooking calendarBooking) {
		return calendarBooking.getDefaultLanguageId();
	}

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			CalendarBooking calendarBooking)
		throws Exception {

		Group group = _groupLocalService.getGroup(calendarBooking.getGroupId());

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
			"/edit_calendar_booking.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"calendarBookingId", calendarBooking.getCalendarBookingId()
		).buildString();
	}

	@Override
	public Class<CalendarBooking> getModelClass() {
		return CalendarBooking.class;
	}

	@Override
	public String getTitle(Locale locale, CalendarBooking calendarBooking) {
		return calendarBooking.getTitle(locale);
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<CalendarBooking> displayBuilder) {

		CalendarBooking calendarBooking = displayBuilder.getModel();

		DisplayContext<CalendarBooking> displayContext =
			displayBuilder.getDisplayContext();

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		TimeZone timeZone = themeDisplay.getTimeZone();

		if (calendarBooking.isAllDay()) {
			timeZone = TimeZone.getTimeZone(StringPool.UTC);
		}

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			displayBuilder.getLocale(), timeZone);

		displayBuilder.display(
			"title", calendarBooking.getTitle(displayBuilder.getLocale())
		).display(
			"description",
			calendarBooking.getDescription(displayBuilder.getLocale()), false
		).display(
			"status", calendarBooking.getStatus()
		).display(
			"starts", dateTimeFormat.format(calendarBooking.getStartTime())
		).display(
			"ends", dateTimeFormat.format(calendarBooking.getEndTime())
		).display(
			"location",
			() -> {
				String location = calendarBooking.getLocation();

				if (Validator.isNotNull(location)) {
					return location;
				}

				return null;
			}
		).display(
			"repeat",
			() -> {
				if (Validator.isNull(calendarBooking.getRecurrence())) {
					return null;
				}

				Recurrence recurrence = calendarBooking.getRecurrenceObj();

				Frequency frequency = recurrence.getFrequency();

				return frequency.getValue();
			}
		).display(
			"resources",
			() -> {
				List<CalendarBooking> childCalendarBookings =
					calendarBooking.getChildCalendarBookings();

				if (childCalendarBookings.isEmpty()) {
					return null;
				}

				StringBundler sb = new StringBundler(
					2 * childCalendarBookings.size());

				for (CalendarBooking childCalendarBooking :
						childCalendarBookings) {

					CalendarResource calendarResource =
						childCalendarBooking.getCalendarResource();

					sb.append(
						calendarResource.getName(displayBuilder.getLocale()));

					sb.append(StringPool.COMMA_AND_SPACE);
				}

				sb.setIndex(sb.index() - 1);

				return sb.toString();
			}
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}