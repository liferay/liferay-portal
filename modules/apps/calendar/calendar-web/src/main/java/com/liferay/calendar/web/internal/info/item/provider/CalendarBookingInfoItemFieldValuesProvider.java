/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.info.item.provider;

import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingService;
import com.liferay.calendar.util.RecurrenceUtil;
import com.liferay.calendar.web.internal.info.item.CalendarBookingInfoItemFields;
import com.liferay.calendar.workflow.constants.CalendarBookingWorkflowConstants;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.layout.page.template.info.item.provider.DisplayPageInfoItemFieldSetProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.WindowState;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = InfoItemFieldValuesProvider.class)
public class CalendarBookingInfoItemFieldValuesProvider
	implements InfoItemFieldValuesProvider<CalendarBooking> {

	@Override
	public InfoItemFieldValues getInfoItemFieldValues(
		CalendarBooking calendarBooking) {

		try {
			return InfoItemFieldValues.builder(
			).infoFieldValues(
				_getCalendarBookingInfoFieldValues(calendarBooking)
			).infoFieldValues(
				_displayPageInfoItemFieldSetProvider.getInfoFieldValues(
					new InfoItemReference(
						CalendarBooking.class.getName(),
						calendarBooking.getCalendarBookingId()),
					StringPool.BLANK, CalendarBooking.class.getSimpleName(),
					calendarBooking, _getThemeDisplay())
			).infoItemReference(
				new InfoItemReference(
					CalendarBooking.class.getName(),
					calendarBooking.getCalendarBookingId())
			).build();
		}
		catch (PortalException portalException) {
			throw new RuntimeException(
				"Unexpected portal exception", portalException);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * See {@link
	 * com.liferay.calendar.internal.notification.NotificationTemplateContextFactory#_getCalendarBookingURL(
	 * User, long)}
	 */
	protected String getCalendarBookingURL(CalendarBooking calendarBooking) {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		if (themeDisplay != null) {
			return StringBundler.concat(
				themeDisplay.getPortalURL(),
				themeDisplay.getPathFriendlyURLPublic(),
				"/calendar/shared/-/calendar/",
				calendarBooking.getCalendarBookingId());
		}

		try {
			Company company = _companyLocalService.getCompany(
				calendarBooking.getCompanyId());

			Group group = _groupLocalService.getGroup(
				calendarBooking.getGroupId());

			Layout layout = _layoutLocalService.fetchLayout(
				group.getDefaultPublicPlid());

			if (layout == null) {
				Group guestGroup = _groupLocalService.getGroup(
					company.getCompanyId(), GroupConstants.GUEST);

				layout = _layoutLocalService.fetchLayout(
					guestGroup.getDefaultPublicPlid());
			}

			String url =
				company.getPortalURL(calendarBooking.getGroupId()) +
					_portal.getLayoutActualURL(layout);

			String namespace = _portal.getPortletNamespace(
				CalendarPortletKeys.CALENDAR);

			url = HttpComponentsUtil.addParameter(
				url, namespace + "mvcPath", "/view_calendar_booking.jsp");

			url = HttpComponentsUtil.addParameter(
				url, "p_p_id", CalendarPortletKeys.CALENDAR);
			url = HttpComponentsUtil.addParameter(url, "p_p_lifecycle", "0");
			url = HttpComponentsUtil.addParameter(
				url, "p_p_state", WindowState.MAXIMIZED.toString());
			url = HttpComponentsUtil.addParameter(
				url, namespace + "calendarBookingId",
				calendarBooking.getCalendarBookingId());

			return url;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return StringPool.BLANK;
		}
	}

	private List<InfoFieldValue<Object>> _getCalendarBookingInfoFieldValues(
			CalendarBooking calendarBooking)
		throws PortalException {

		return Arrays.asList(
			new InfoFieldValue<>(
				CalendarBookingInfoItemFields.titleInfoField,
				InfoLocalizedValue.<String>builder(
				).defaultLocale(
					LocaleUtil.fromLanguageId(
						calendarBooking.getDefaultLanguageId())
				).values(
					calendarBooking.getTitleMap()
				).build()),
			new InfoFieldValue<>(
				CalendarBookingInfoItemFields.descriptionInfoField,
				InfoLocalizedValue.<String>builder(
				).defaultLocale(
					LocaleUtil.fromLanguageId(
						calendarBooking.getDefaultLanguageId())
				).values(
					calendarBooking.getDescriptionMap()
				).build()),
			new InfoFieldValue<>(
				CalendarBookingInfoItemFields.locationInfoField,
				calendarBooking.getLocation()),
			new InfoFieldValue<>(
				CalendarBookingInfoItemFields.eventURLInfoField,
				getCalendarBookingURL(calendarBooking)),
			new InfoFieldValue<>(
				CalendarBookingInfoItemFields.startDateInfoField,
				new Date(calendarBooking.getStartTime())),
			new InfoFieldValue<>(
				CalendarBookingInfoItemFields.endDateInfoField,
				new Date(calendarBooking.getEndTime())),
			new InfoFieldValue<>(
				CalendarBookingInfoItemFields.allDayInfoField,
				calendarBooking.isAllDay()),
			new InfoFieldValue<>(
				CalendarBookingInfoItemFields.calendarNameInfoField,
				InfoLocalizedValue.<String>builder(
				).defaultLocale(
					LocaleUtil.fromLanguageId(
						calendarBooking.getDefaultLanguageId())
				).values(
					_getCalendarNameMap(calendarBooking)
				).build()),
			new InfoFieldValue<>(
				CalendarBookingInfoItemFields.invitationsInfoField,
				_getInvitations(calendarBooking)),
			new InfoFieldValue<>(
				CalendarBookingInfoItemFields.repetitionsInfoField,
				RecurrenceUtil.getSummary(
					calendarBooking, calendarBooking.getRecurrenceObj())));
	}

	private Map<Locale, String> _getCalendarNameMap(
			CalendarBooking calendarBooking)
		throws PortalException {

		Calendar calendar = calendarBooking.getCalendar();

		return calendar.getNameMap();
	}

	private String _getInvitations(CalendarBooking calendarBooking)
		throws PortalException {

		List<CalendarBooking> acceptedCalendarBookings =
			_calendarBookingService.getChildCalendarBookings(
				calendarBooking.getParentCalendarBookingId(),
				WorkflowConstants.STATUS_APPROVED);
		List<CalendarBooking> declinedCalendarBookings =
			_calendarBookingService.getChildCalendarBookings(
				calendarBooking.getParentCalendarBookingId(),
				WorkflowConstants.STATUS_DENIED);

		List<CalendarBooking> pendingCalendarBookings =
			_calendarBookingService.getChildCalendarBookings(
				calendarBooking.getParentCalendarBookingId(),
				WorkflowConstants.STATUS_PENDING);

		pendingCalendarBookings.addAll(
			_calendarBookingService.getChildCalendarBookings(
				calendarBooking.getParentCalendarBookingId(),
				WorkflowConstants.STATUS_DRAFT));
		pendingCalendarBookings.addAll(
			_calendarBookingService.getChildCalendarBookings(
				calendarBooking.getParentCalendarBookingId(),
				CalendarBookingWorkflowConstants.STATUS_MASTER_PENDING));

		List<CalendarBooking> maybeCalendarBookings =
			_calendarBookingService.getChildCalendarBookings(
				calendarBooking.getParentCalendarBookingId(),
				CalendarBookingWorkflowConstants.STATUS_MAYBE);

		return _language.format(
			LocaleUtil.getMostRelevantLocale(),
			"accepted-x-declined-x-pending-x-maybe-x",
			new Integer[] {
				acceptedCalendarBookings.size(),
				declinedCalendarBookings.size(), pendingCalendarBookings.size(),
				maybeCalendarBookings.size()
			},
			false);
	}

	private ThemeDisplay _getThemeDisplay() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			return serviceContext.getThemeDisplay();
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarBookingInfoItemFieldValuesProvider.class);

	@Reference
	private CalendarBookingService _calendarBookingService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DisplayPageInfoItemFieldSetProvider
		_displayPageInfoItemFieldSetProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}