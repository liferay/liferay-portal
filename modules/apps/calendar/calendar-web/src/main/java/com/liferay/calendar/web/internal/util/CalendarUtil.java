/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.util;

import com.liferay.calendar.constants.CalendarActionKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.calendar.recurrence.RecurrenceSerializer;
import com.liferay.calendar.service.CalendarBookingService;
import com.liferay.calendar.service.CalendarResourceLocalService;
import com.liferay.calendar.service.CalendarService;
import com.liferay.calendar.util.JCalendarUtil;
import com.liferay.calendar.util.RecurrenceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * @author Eduardo Lundgren
 * @author Peter Shin
 * @author Fabio Pezzutto
 */
public class CalendarUtil {

	public static JSONObject getCalendarRenderingRulesJSONObject(
			ThemeDisplay themeDisplay, long[] calendarIds, int[] statuses,
			long startTime, long endTime, String ruleName, TimeZone timeZone)
		throws PortalException {

		CalendarBookingService calendarBookingService =
			_calendarBookingServiceSnapshot.get();

		List<CalendarBooking> calendarBookings = calendarBookingService.search(
			themeDisplay.getCompanyId(), null, calendarIds, new long[0], -1,
			null, startTime, endTime, timeZone, true, statuses,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Map<Integer, Map<Integer, List<Integer>>> rulesMap = new HashMap<>();

		for (CalendarBooking calendarBooking : calendarBookings) {
			long maxStartTime = Math.max(
				calendarBooking.getStartTime(), startTime);

			java.util.Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(
				maxStartTime,
				calendarBooking.isAllDay() ?
					TimeZoneUtil.getTimeZone(StringPool.UTC) : timeZone);

			long minEndTime = Math.min(calendarBooking.getEndTime(), endTime);

			java.util.Calendar endTimeJCalendar = JCalendarUtil.getJCalendar(
				minEndTime,
				calendarBooking.isAllDay() ?
					TimeZoneUtil.getTimeZone(StringPool.UTC) : timeZone);

			long days = JCalendarUtil.getDaysBetween(
				startTimeJCalendar, endTimeJCalendar);

			for (int i = 0; i < days; i++) {
				int year = startTimeJCalendar.get(java.util.Calendar.YEAR);

				Map<Integer, List<Integer>> rulesMonth =
					rulesMap.computeIfAbsent(year, key -> new HashMap<>());

				int month = startTimeJCalendar.get(java.util.Calendar.MONTH);

				List<Integer> rulesDay = rulesMonth.computeIfAbsent(
					month, key -> new ArrayList<>());

				int day = startTimeJCalendar.get(
					java.util.Calendar.DAY_OF_MONTH);

				if (!rulesDay.contains(day)) {
					rulesDay.add(day);
				}

				startTimeJCalendar.add(java.util.Calendar.DATE, 1);
			}
		}

		Set<Integer> years = rulesMap.keySet();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		for (Integer year : years) {
			Map<Integer, List<Integer>> monthsMap = rulesMap.get(year);

			Set<Integer> months = monthsMap.keySet();

			JSONObject monthJSONObject = JSONFactoryUtil.createJSONObject();

			jsonObject.put(String.valueOf(year), monthJSONObject);

			for (Integer month : months) {
				JSONObject dayJSONObject = JSONUtil.put(
					StringUtil.merge(monthsMap.get(month)), ruleName);

				monthJSONObject.put(String.valueOf(month), dayJSONObject);
			}
		}

		return jsonObject;
	}

	public static Collection<CalendarResource> getCalendarResources(
			List<CalendarBooking> calendarBookings)
		throws PortalException {

		Set<CalendarResource> calendarResources = new HashSet<>();

		for (CalendarBooking calendarBooking : calendarBookings) {
			calendarResources.add(calendarBooking.getCalendarResource());
		}

		return calendarResources;
	}

	public static JSONObject toCalendarBookingJSONObject(
			ThemeDisplay themeDisplay, CalendarBooking calendarBooking,
			TimeZone timeZone)
		throws PortalException {

		JSONObject jsonObject = JSONUtil.put(
			"allDay", calendarBooking.isAllDay()
		).put(
			"calendarBookingId", calendarBooking.getCalendarBookingId()
		).put(
			"calendarId", calendarBooking.getCalendarId()
		).put(
			"calendarResourceName",
			() -> {
				CalendarResourceLocalService calendarResourceLocalService =
					_calendarResourceLocalServiceSnapshot.get();

				CalendarResource calendarResource =
					calendarResourceLocalService.getCalendarResource(
						calendarBooking.getCalendarResourceId());

				return HtmlUtil.escape(
					calendarResource.getName(themeDisplay.getLocale()));
			}
		).put(
			"description",
			calendarBooking.getDescription(themeDisplay.getLocale())
		);

		java.util.Calendar endTimeJCalendar = JCalendarUtil.getJCalendar(
			calendarBooking.getEndTime(), timeZone);

		_addTimeProperties(jsonObject, "endTime", endTimeJCalendar);

		jsonObject.put(
			"firstReminder", calendarBooking.getFirstReminder()
		).put(
			"firstReminderType", calendarBooking.getFirstReminderType()
		).put(
			"hasChildCalendarBookings",
			() -> {
				List<CalendarBooking> childCalendarBookings =
					calendarBooking.getChildCalendarBookings();

				return childCalendarBookings.size() > 1;
			}
		).put(
			"hasWorkflowInstanceLink",
			() -> {
				WorkflowInstanceLinkLocalService
					workflowInstanceLinkLocalService =
						_workflowInstanceLinkLocalServiceSnapshot.get();

				return workflowInstanceLinkLocalService.hasWorkflowInstanceLink(
					themeDisplay.getCompanyId(), calendarBooking.getGroupId(),
					CalendarBooking.class.getName(),
					calendarBooking.getCalendarBookingId());
			}
		).put(
			"instanceIndex", calendarBooking.getInstanceIndex()
		).put(
			"location", calendarBooking.getLocation()
		).put(
			"parentCalendarBookingId",
			calendarBooking.getParentCalendarBookingId()
		);

		java.util.Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(
			calendarBooking.getStartTime(), timeZone);

		jsonObject.put(
			"recurrence",
			() -> {
				CalendarBookingService calendarBookingService =
					_calendarBookingServiceSnapshot.get();

				CalendarBooking lastInstanceCalendarBooking =
					calendarBookingService.getLastInstanceCalendarBooking(
						calendarBooking.getCalendarBookingId());

				String recurrence = lastInstanceCalendarBooking.getRecurrence();

				if (Validator.isNotNull(recurrence)) {
					Recurrence recurrenceObj = RecurrenceUtil.inTimeZone(
						lastInstanceCalendarBooking.getRecurrenceObj(),
						startTimeJCalendar, timeZone);

					return RecurrenceSerializer.serialize(recurrenceObj);
				}

				return recurrence;
			}
		).put(
			"recurringCalendarBookingId",
			calendarBooking.getRecurringCalendarBookingId()
		).put(
			"secondReminder", calendarBooking.getSecondReminder()
		).put(
			"secondReminderType", calendarBooking.getSecondReminder()
		);

		_addTimeProperties(jsonObject, "startTime", startTimeJCalendar);

		jsonObject.put(
			"status", calendarBooking.getStatus()
		).put(
			"title", calendarBooking.getTitle(themeDisplay.getLocale())
		);

		return jsonObject;
	}

	public static JSONArray toCalendarBookingsJSONArray(
			ThemeDisplay themeDisplay, List<CalendarBooking> calendarBookings)
		throws PortalException {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		if (calendarBookings == null) {
			return jsonArray;
		}

		for (CalendarBooking calendarBooking : calendarBookings) {
			JSONObject jsonObject = toCalendarJSONObject(
				themeDisplay, calendarBooking.getCalendar());

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	public static JSONArray toCalendarBookingsJSONArray(
			ThemeDisplay themeDisplay, List<CalendarBooking> calendarBookings,
			TimeZone timeZone)
		throws PortalException {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (CalendarBooking calendarBooking : calendarBookings) {
			if ((calendarBooking.getStatus() ==
					WorkflowConstants.STATUS_DRAFT) &&
				(calendarBooking.getUserId() != themeDisplay.getUserId())) {

				continue;
			}

			JSONObject jsonObject = toCalendarBookingJSONObject(
				themeDisplay, calendarBooking,
				calendarBooking.isAllDay() ?
					TimeZoneUtil.getTimeZone(StringPool.UTC) : timeZone);

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	public static JSONObject toCalendarJSONObject(
			ThemeDisplay themeDisplay, Calendar calendar)
		throws PortalException {

		CalendarResourceLocalService calendarResourceLocalService =
			_calendarResourceLocalServiceSnapshot.get();

		CalendarResource calendarResource =
			calendarResourceLocalService.fetchCalendarResource(
				calendar.getCalendarResourceId());

		return JSONUtil.put(
			"calendarId", calendar.getCalendarId()
		).put(
			"calendarResourceId", calendarResource.getCalendarResourceId()
		).put(
			"calendarResourceName",
			HtmlUtil.escape(calendarResource.getName(themeDisplay.getLocale()))
		).put(
			"classNameId", calendarResource.getClassNameId()
		).put(
			"classPK", calendarResource.getClassPK()
		).put(
			"color", ColorUtil.toHexString(calendar.getColor())
		).put(
			"defaultCalendar", calendar.isDefaultCalendar()
		).put(
			"groupId", calendar.getGroupId()
		).put(
			"hasWorkflowDefinitionLink",
			() -> {
				WorkflowDefinitionLinkLocalService
					workflowDefinitionLinkLocalService =
						_workflowDefinitionLinkLocalServiceSnapshot.get();

				return workflowDefinitionLinkLocalService.
					hasWorkflowDefinitionLink(
						themeDisplay.getCompanyId(),
						calendarResource.getGroupId(),
						CalendarBooking.class.getName());
			}
		).put(
			"manageable",
			() -> {
				CalendarService calendarService =
					_calendarServiceSnapshot.get();

				return calendarService.isManageableFromGroup(
					calendar.getCalendarId(), themeDisplay.getScopeGroupId());
			}
		).put(
			"name", HtmlUtil.escape(calendar.getName(themeDisplay.getLocale()))
		).put(
			"permissions",
			_getPermissionsJSONObject(
				themeDisplay.getPermissionChecker(), calendar)
		).put(
			"userId", calendar.getUserId()
		);
	}

	public static JSONObject toCalendarResourceJSONObject(
		ThemeDisplay themeDisplay, CalendarResource calendarResource) {

		return JSONUtil.put(
			"calendarResourceId", calendarResource.getCalendarResourceId()
		).put(
			"classNameId", calendarResource.getClassNameId()
		).put(
			"classPK", calendarResource.getClassPK()
		).put(
			"classUuid", calendarResource.getClassUuid()
		).put(
			"code", calendarResource.getCode()
		).put(
			"groupId", calendarResource.getGroupId()
		).put(
			"name", calendarResource.getName(themeDisplay.getLocale())
		).put(
			"userId", calendarResource.getUserId()
		);
	}

	public static JSONArray toCalendarsJSONArray(
			ThemeDisplay themeDisplay, List<Calendar> calendars)
		throws PortalException {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		if (calendars == null) {
			return jsonArray;
		}

		for (Calendar calendar : calendars) {
			JSONObject jsonObject = toCalendarJSONObject(
				themeDisplay, calendar);

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private static void _addTimeProperties(
		JSONObject jsonObject, String prefix, java.util.Calendar jCalendar) {

		jsonObject.put(
			prefix, jCalendar.getTimeInMillis()
		).put(
			prefix + "Day", jCalendar.get(java.util.Calendar.DAY_OF_MONTH)
		).put(
			prefix + "Hour", jCalendar.get(java.util.Calendar.HOUR_OF_DAY)
		).put(
			prefix + "Minute", jCalendar.get(java.util.Calendar.MINUTE)
		).put(
			prefix + "Month", jCalendar.get(java.util.Calendar.MONTH)
		).put(
			prefix + "Year", jCalendar.get(java.util.Calendar.YEAR)
		);
	}

	private static JSONObject _getPermissionsJSONObject(
			PermissionChecker permissionChecker, Calendar calendar)
		throws PortalException {

		ModelResourcePermission<Calendar> calendarModelResourcePermission =
			_calendarModelResourcePermissionSnapshot.get();

		return JSONUtil.put(
			ActionKeys.DELETE,
			calendarModelResourcePermission.contains(
				permissionChecker, calendar, ActionKeys.DELETE)
		).put(
			ActionKeys.PERMISSIONS,
			calendarModelResourcePermission.contains(
				permissionChecker, calendar, ActionKeys.PERMISSIONS)
		).put(
			ActionKeys.UPDATE,
			calendarModelResourcePermission.contains(
				permissionChecker, calendar, ActionKeys.UPDATE)
		).put(
			ActionKeys.VIEW,
			calendarModelResourcePermission.contains(
				permissionChecker, calendar, ActionKeys.VIEW)
		).put(
			CalendarActionKeys.MANAGE_BOOKINGS,
			calendarModelResourcePermission.contains(
				permissionChecker, calendar, CalendarActionKeys.MANAGE_BOOKINGS)
		).put(
			CalendarActionKeys.VIEW_BOOKING_DETAILS,
			calendarModelResourcePermission.contains(
				permissionChecker, calendar,
				CalendarActionKeys.VIEW_BOOKING_DETAILS)
		);
	}

	private static final Snapshot<CalendarBookingService>
		_calendarBookingServiceSnapshot = new Snapshot<>(
			CalendarUtil.class, CalendarBookingService.class);
	private static final Snapshot<ModelResourcePermission<Calendar>>
		_calendarModelResourcePermissionSnapshot = new Snapshot<>(
			CalendarUtil.class, Snapshot.cast(ModelResourcePermission.class),
			"(model.class.name=com.liferay.calendar.model.Calendar)");
	private static final Snapshot<CalendarResourceLocalService>
		_calendarResourceLocalServiceSnapshot = new Snapshot<>(
			CalendarUtil.class, CalendarResourceLocalService.class);
	private static final Snapshot<CalendarService> _calendarServiceSnapshot =
		new Snapshot<>(CalendarUtil.class, CalendarService.class);
	private static final Snapshot<WorkflowDefinitionLinkLocalService>
		_workflowDefinitionLinkLocalServiceSnapshot = new Snapshot<>(
			CalendarUtil.class, WorkflowDefinitionLinkLocalService.class);
	private static final Snapshot<WorkflowInstanceLinkLocalService>
		_workflowInstanceLinkLocalServiceSnapshot = new Snapshot<>(
			CalendarUtil.class, WorkflowInstanceLinkLocalService.class);

}