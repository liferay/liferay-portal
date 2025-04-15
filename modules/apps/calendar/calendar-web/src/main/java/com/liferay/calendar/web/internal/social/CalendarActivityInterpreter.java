/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.social;

import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.calendar.social.CalendarActivityKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.social.kernel.model.BaseSocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.model.SocialActivityInterpreter;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "jakarta.portlet.name=" + CalendarPortletKeys.CALENDAR,
	service = SocialActivityInterpreter.class
)
public class CalendarActivityInterpreter extends BaseSocialActivityInterpreter {

	@Override
	public String[] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected String getPath(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		CalendarBooking calendarBooking =
			_calendarBookingLocalService.getCalendarBooking(
				activity.getClassPK());

		long plid = _portal.getPlidFromPortletId(
			calendarBooking.getGroupId(), CalendarPortletKeys.CALENDAR);

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				serviceContext.getRequest(), CalendarPortletKeys.CALENDAR, plid,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/view_calendar_booking.jsp"
		).setBackURL(
			serviceContext.getCurrentURL()
		).setParameter(
			"calendarBookingId", activity.getClassPK()
		).setWindowState(
			WindowState.MAXIMIZED
		).buildString();
	}

	@Override
	protected String getTitlePattern(
		String groupName, SocialActivity activity) {

		int activityType = activity.getType();

		if (activityType == CalendarActivityKeys.ADD_CALENDAR_BOOKING) {
			if (Validator.isNull(groupName)) {
				return "activity-calendar-booking-add-booking";
			}

			return "activity-calendar-booking-add-booking-in";
		}
		else if (activityType == SocialActivityConstants.TYPE_MOVE_TO_TRASH) {
			if (Validator.isNull(groupName)) {
				return "activity-calendar-booking-move-to-trash";
			}

			return "activity-calendar-booking-move-to-trash-in";
		}
		else if (activityType ==
					SocialActivityConstants.TYPE_RESTORE_FROM_TRASH) {

			if (Validator.isNull(groupName)) {
				return "activity-calendar-booking-restore-from-trash";
			}

			return "activity-calendar-booking-restore-from-trash-in";
		}
		else if (activityType == CalendarActivityKeys.UPDATE_CALENDAR_BOOKING) {
			if (Validator.isNull(groupName)) {
				return "activity-calendar-booking-update-booking";
			}

			return "activity-calendar-booking-update-booking-in";
		}

		return StringPool.BLANK;
	}

	@Override
	protected boolean hasPermissions(
			PermissionChecker permissionChecker, SocialActivity activity,
			String actionId, ServiceContext serviceContext)
		throws Exception {

		CalendarBooking calendarBooking =
			_calendarBookingLocalService.getCalendarBooking(
				activity.getClassPK());

		return _calendarModelResourcePermission.contains(
			permissionChecker, calendarBooking.getCalendarId(), actionId);
	}

	private static final String[] _CLASS_NAMES = {
		CalendarBooking.class.getName()
	};

	@Reference
	private CalendarBookingLocalService _calendarBookingLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.calendar.model.Calendar)"
	)
	private ModelResourcePermission<Calendar> _calendarModelResourcePermission;

	@Reference
	private Portal _portal;

}