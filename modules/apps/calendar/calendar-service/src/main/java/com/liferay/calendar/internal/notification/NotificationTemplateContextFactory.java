/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.internal.notification;

import com.liferay.calendar.configuration.CalendarServiceConfigurationValues;
import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.notification.NotificationTemplateContext;
import com.liferay.calendar.notification.NotificationTemplateType;
import com.liferay.calendar.notification.NotificationType;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.calendar.service.CalendarNotificationTemplateLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.WindowState;

import java.io.Serializable;

import java.text.Format;

import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author Eduardo Lundgren
 */
public class NotificationTemplateContextFactory {

	public static NotificationTemplateContext getInstance(
			CalendarBooking calendarBooking,
			NotificationTemplateType notificationTemplateType,
			NotificationType notificationType, String layoutURL,
			String portalURL, User user)
		throws Exception {

		CalendarBooking parentCalendarBooking =
			calendarBooking.getParentCalendarBooking();

		Calendar calendar = parentCalendarBooking.getCalendar();

		NotificationTemplateContext notificationTemplateContext =
			new NotificationTemplateContext(
				CalendarServiceConfigurationValues.
					CALENDAR_NOTIFICATION_DEFAULT_TYPE);

		notificationTemplateContext.setCalendarNotificationTemplate(
			CalendarNotificationTemplateLocalServiceUtil.
				fetchCalendarNotificationTemplate(
					calendar.getCalendarId(), notificationType,
					notificationTemplateType));
		notificationTemplateContext.setCompanyId(
			calendarBooking.getCompanyId());
		notificationTemplateContext.setGroupId(calendarBooking.getGroupId());
		notificationTemplateContext.setCalendarId(calendar.getCalendarId());
		notificationTemplateContext.setNotificationTemplateType(
			notificationTemplateType);
		notificationTemplateContext.setNotificationType(notificationType);

		// Attributes

		Format userDateTimeFormat = _getUserDateTimeFormat(
			calendarBooking, user);

		String userTimezoneDisplayName = _getUserTimezoneDisplayName(user);

		Map<String, Serializable> attributes =
			HashMapBuilder.<String, Serializable>put(
				"calendarBookingId", calendarBooking.getCalendarBookingId()
			).put(
				"calendarName", calendar.getName(user.getLocale(), true)
			).put(
				"endTime",
				StringBundler.concat(
					userDateTimeFormat.format(calendarBooking.getEndTime()),
					StringPool.SPACE, userTimezoneDisplayName)
			).put(
				"icsFile",
				() -> {
					if (!Objects.equals(
							notificationTemplateContext.
								getNotificationTemplateType(),
							NotificationTemplateType.INVITE)) {

						return null;
					}

					CalendarBookingLocalService calendarBookingLocalService =
						_calendarBookingLocalServiceSnapshot.get();

					String calendarBookingString =
						calendarBookingLocalService.exportCalendarBooking(
							calendarBooking.getCalendarBookingId(),
							CalendarUtil.ICAL_EXTENSION);

					return FileUtil.createTempFile(
						calendarBookingString.getBytes());
				}
			).put(
				"location", calendarBooking.getLocation()
			).put(
				"portalURL",
				() -> _getPortalURLOrCompanyPortalURL(
					portalURL, user.getCompanyId(),
					calendarBooking.getGroupId())
			).put(
				"portletName",
				LanguageUtil.get(
					user.getLocale(),
					"jakarta.portlet.title.".concat(
						CalendarPortletKeys.CALENDAR))
			).put(
				"siteName",
				() -> {
					GroupLocalService groupLocalService =
						_groupLocalServiceSnapshot.get();

					Group calendarGroup = groupLocalService.getGroup(
						calendar.getGroupId());

					if (calendarGroup.isSite()) {
						return calendarGroup.getName(user.getLocale(), true);
					}

					return StringPool.BLANK;
				}
			).put(
				"startTime",
				StringBundler.concat(
					userDateTimeFormat.format(calendarBooking.getStartTime()),
					StringPool.SPACE, userTimezoneDisplayName)
			).put(
				"title", calendarBooking.getTitle(user.getLocale(), true)
			).put(
				"url",
				_getCalendarBookingURL(
					calendarBooking, layoutURL, portalURL, user)
			).build();

		notificationTemplateContext.setAttributes(attributes);

		return notificationTemplateContext;
	}

	public static NotificationTemplateContext getInstance(
			NotificationType notificationType,
			NotificationTemplateType notificationTemplateType,
			CalendarBooking calendarBooking, User user,
			ServiceContext serviceContext)
		throws Exception {

		String layoutURL = null;
		String portalURL = null;

		if (serviceContext != null) {
			layoutURL = serviceContext.getLayoutURL();
			portalURL = serviceContext.getPortalURL();
		}

		NotificationTemplateContext notificationTemplateContext = getInstance(
			calendarBooking, notificationTemplateType, notificationType,
			layoutURL, portalURL, user);

		if ((serviceContext != null) &&
			Validator.isNotNull(
				serviceContext.getAttribute("instanceStartTime"))) {

			long instanceStartTime = (long)serviceContext.getAttribute(
				"instanceStartTime");

			Format userDateTimeFormat = _getUserDateTimeFormat(
				calendarBooking, user);

			String userTimezoneDisplayName = _getUserTimezoneDisplayName(user);

			String instanceStartTimeFormatted =
				userDateTimeFormat.format(instanceStartTime) +
					StringPool.SPACE + userTimezoneDisplayName;

			notificationTemplateContext.setAttribute(
				"instanceStartTime", instanceStartTimeFormatted);
		}

		return notificationTemplateContext;
	}

	/**
	 * See {@link
	 * com.liferay.calendar.web.internal.info.item.provider.CalendarBookingInfoItemFieldValuesProvider#_getCalendarBookingURL(
	 * CalendarBooking)}
	 */
	private static String _getCalendarBookingURL(
			CalendarBooking calendarBooking, String layoutURL, String portalURL,
			User user)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

			if (themeDisplay != null) {
				return StringBundler.concat(
					themeDisplay.getPortalURL(),
					themeDisplay.getPathFriendlyURLPublic(),
					"/calendar/shared/-/calendar/",
					calendarBooking.getCalendarBookingId());
			}
		}

		String url = layoutURL;

		if (layoutURL == null) {
			GroupLocalService groupLocalService =
				_groupLocalServiceSnapshot.get();

			Group group = groupLocalService.getGroup(
				calendarBooking.getGroupId());

			LayoutLocalService layoutLocalService =
				_layoutLocalServiceSnapshot.get();

			Layout layout = layoutLocalService.fetchLayout(
				group.getDefaultPublicPlid());

			if (layout == null) {
				Group guestGroup = groupLocalService.getGroup(
					user.getCompanyId(), GroupConstants.GUEST);

				layout = layoutLocalService.fetchLayout(
					guestGroup.getDefaultPublicPlid());
			}

			layoutURL = PortalUtil.getLayoutActualURL(layout);

			portalURL = _getPortalURLOrCompanyPortalURL(
				portalURL, user.getCompanyId(), group.getGroupId());

			url = portalURL + layoutURL;
		}

		String namespace = PortalUtil.getPortletNamespace(
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

	private static String _getPortalURLOrCompanyPortalURL(
			String portalURL, long companyId, long groupId)
		throws PortalException {

		if (portalURL != null) {
			return portalURL;
		}

		CompanyLocalService companyLocalService =
			_companyLocalServiceSnapshot.get();

		Company company = companyLocalService.getCompany(companyId);

		return company.getPortalURL(groupId);
	}

	private static Format _getUserDateTimeFormat(
		CalendarBooking calendarBooking, User user) {

		TimeZone userTimeZone = user.getTimeZone();

		if ((calendarBooking != null) && calendarBooking.isAllDay()) {
			userTimeZone = TimeZone.getTimeZone(StringPool.UTC);
		}

		return FastDateFormatFactoryUtil.getDateTime(
			user.getLocale(), userTimeZone);
	}

	private static String _getUserTimezoneDisplayName(User user) {
		TimeZone userTimeZone = user.getTimeZone();

		return userTimeZone.getDisplayName(
			false, TimeZone.SHORT, user.getLocale());
	}

	private static final Snapshot<CalendarBookingLocalService>
		_calendarBookingLocalServiceSnapshot = new Snapshot<>(
			NotificationTemplateContextFactory.class,
			CalendarBookingLocalService.class);
	private static final Snapshot<CompanyLocalService>
		_companyLocalServiceSnapshot = new Snapshot<>(
			NotificationTemplateContextFactory.class,
			CompanyLocalService.class);
	private static final Snapshot<GroupLocalService>
		_groupLocalServiceSnapshot = new Snapshot<>(
			NotificationTemplateContextFactory.class, GroupLocalService.class);
	private static final Snapshot<LayoutLocalService>
		_layoutLocalServiceSnapshot = new Snapshot<>(
			NotificationTemplateContextFactory.class, LayoutLocalService.class);

}