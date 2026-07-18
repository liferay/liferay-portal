/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.impl;

import com.liferay.calendar.configuration.CalendarServiceConfigurationValues;
import com.liferay.calendar.constants.CalendarActionKeys;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarLocalService;
import com.liferay.calendar.service.CalendarService;
import com.liferay.calendar.service.base.CalendarBookingServiceBaseImpl;
import com.liferay.calendar.util.JCalendarUtil;
import com.liferay.calendar.workflow.constants.CalendarBookingWorkflowConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.rss.export.RSSExporter;
import com.liferay.rss.model.SyndContent;
import com.liferay.rss.model.SyndEntry;
import com.liferay.rss.model.SyndFeed;
import com.liferay.rss.model.SyndLink;
import com.liferay.rss.model.SyndModelFactory;
import com.liferay.rss.util.RSSUtil;

import java.io.IOException;

import java.text.Format;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Lundgren
 * @author Fabio Pezzutto
 * @author Bruno Basto
 * @author Pier Paolo Ramon
 */
@Component(
	property = {
		"json.web.service.context.name=calendar",
		"json.web.service.context.path=CalendarBooking"
	},
	service = AopService.class
)
public class CalendarBookingServiceImpl extends CalendarBookingServiceBaseImpl {

	@Override
	public CalendarBooking addCalendarBooking(
			long calendarId, long[] childCalendarIds,
			long parentCalendarBookingId, long recurringCalendarBookingId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, int startTimeYear, int startTimeMonth,
			int startTimeDay, int startTimeHour, int startTimeMinute,
			int endTimeYear, int endTimeMonth, int endTimeDay, int endTimeHour,
			int endTimeMinute, String timeZoneId, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException {

		TimeZone timeZone = TimeZoneUtil.getTimeZone(timeZoneId);

		if (allDay) {
			timeZone = TimeZone.getTimeZone(StringPool.UTC);
		}

		java.util.Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(
			startTimeYear, startTimeMonth, startTimeDay, startTimeHour,
			startTimeMinute, 0, 0, timeZone);
		java.util.Calendar endTimeJCalendar = JCalendarUtil.getJCalendar(
			endTimeYear, endTimeMonth, endTimeDay, endTimeHour, endTimeMinute,
			0, 0, timeZone);

		return calendarBookingService.addCalendarBooking(
			calendarId, childCalendarIds, parentCalendarBookingId,
			recurringCalendarBookingId, titleMap, descriptionMap, location,
			startTimeJCalendar.getTimeInMillis(),
			endTimeJCalendar.getTimeInMillis(), allDay, recurrence,
			firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	@Override
	public CalendarBooking addCalendarBooking(
			long calendarId, long[] childCalendarIds,
			long parentCalendarBookingId, long recurringCalendarBookingId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException {

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarId,
			CalendarActionKeys.MANAGE_BOOKINGS);

		return calendarBookingLocalService.addCalendarBooking(
			getUserId(), calendarId, childCalendarIds, parentCalendarBookingId,
			recurringCalendarBookingId, titleMap, descriptionMap, location,
			startTime, endTime, allDay, recurrence, firstReminder,
			firstReminderType, secondReminder, secondReminderType,
			serviceContext);
	}

	@Override
	public CalendarBooking deleteCalendarBooking(long calendarBookingId)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarBooking.getCalendarId(),
			CalendarActionKeys.MANAGE_BOOKINGS);

		return calendarBookingLocalService.deleteCalendarBooking(
			calendarBookingId);
	}

	@Override
	public void deleteCalendarBookingInstance(
			long calendarBookingId, int instanceIndex, boolean allFollowing)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarBooking.getCalendarId(),
			CalendarActionKeys.MANAGE_BOOKINGS);

		calendarBookingLocalService.deleteCalendarBookingInstance(
			getUserId(), calendarBooking, instanceIndex, allFollowing);
	}

	@Override
	public void deleteCalendarBookingInstance(
			long calendarBookingId, int instanceIndex, boolean allFollowing,
			boolean deleteRecurringCalendarBookings)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarBooking.getCalendarId(),
			CalendarActionKeys.MANAGE_BOOKINGS);

		calendarBookingLocalService.deleteCalendarBookingInstance(
			getUserId(), calendarBooking, instanceIndex, allFollowing,
			deleteRecurringCalendarBookings);
	}

	@Override
	public void deleteCalendarBookingInstance(
			long calendarBookingId, long startTime, boolean allFollowing)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarBooking.getCalendarId(),
			CalendarActionKeys.MANAGE_BOOKINGS);

		calendarBookingLocalService.deleteCalendarBookingInstance(
			getUserId(), calendarBookingId, startTime, allFollowing);
	}

	@Override
	public String exportCalendarBooking(long calendarBookingId, String type)
		throws Exception {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarBooking.getCalendar(),
			CalendarActionKeys.VIEW_BOOKING_DETAILS);

		return calendarBookingLocalService.exportCalendarBooking(
			calendarBookingId, type);
	}

	@Override
	public CalendarBooking fetchCalendarBooking(long calendarBookingId)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.fetchByPrimaryKey(calendarBookingId);

		if (calendarBooking == null) {
			return null;
		}

		return _filterCalendarBooking(calendarBooking);
	}

	@Override
	public CalendarBooking getCalendarBooking(long calendarBookingId)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		return _filterCalendarBooking(calendarBooking);
	}

	@Override
	public CalendarBooking getCalendarBooking(
			long calendarId, long parentCalendarBookingId)
		throws PortalException {

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarId,
			CalendarActionKeys.MANAGE_BOOKINGS);

		return calendarBookingPersistence.findByC_P(
			calendarId, parentCalendarBookingId);
	}

	@Override
	public CalendarBooking getCalendarBookingInstance(
			long calendarBookingId, int instanceIndex)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingLocalService.getCalendarBookingInstance(
				calendarBookingId, instanceIndex);

		return _filterCalendarBooking(calendarBooking);
	}

	@Override
	public List<CalendarBooking> getCalendarBookings(
			long calendarId, int[] statuses)
		throws PortalException {

		return _filterCalendarBookings(
			calendarBookingLocalService.getCalendarBookings(
				calendarId, statuses),
			ActionKeys.VIEW);
	}

	@Override
	public List<CalendarBooking> getCalendarBookings(
			long calendarId, long startTime, long endTime)
		throws PortalException {

		return getCalendarBookings(
			calendarId, startTime, endTime, QueryUtil.ALL_POS);
	}

	@Override
	public List<CalendarBooking> getCalendarBookings(
			long calendarId, long startTime, long endTime, int max)
		throws PortalException {

		List<CalendarBooking> calendarBookings =
			calendarBookingLocalService.getCalendarBookings(
				calendarId, startTime, endTime, max);

		for (CalendarBooking calendarBooking : calendarBookings) {
			_filterCalendarBooking(calendarBooking);
		}

		return calendarBookings;
	}

	@Override
	public String getCalendarBookingsRSS(
			long calendarId, long startTime, long endTime, int max, String type,
			double version, String displayStyle, ThemeDisplay themeDisplay)
		throws PortalException {

		Calendar calendar = _calendarService.getCalendar(calendarId);

		List<CalendarBooking> calendarBookings = search(
			themeDisplay.getCompanyId(), new long[0], new long[] {calendarId},
			new long[0], -1, null, startTime, endTime, null, true,
			new int[] {
				WorkflowConstants.STATUS_APPROVED,
				CalendarBookingWorkflowConstants.STATUS_MAYBE
			},
			0, max, null);

		return _exportToRSS(
			calendar.getName(themeDisplay.getLocale()),
			calendar.getDescription(themeDisplay.getLocale()), type, version,
			displayStyle, _portal.getLayoutFullURL(themeDisplay),
			calendarBookings, themeDisplay);
	}

	@Override
	public List<CalendarBooking> getChildCalendarBookings(
			long parentCalendarBookingId)
		throws PortalException {

		List<CalendarBooking> calendarBookings =
			calendarBookingPersistence.findByParentCalendarBookingId(
				parentCalendarBookingId);

		return _filterCalendarBookings(calendarBookings);
	}

	@Override
	public List<CalendarBooking> getChildCalendarBookings(
			long parentCalendarBookingId,
			boolean includeStagingCalendarBookings)
		throws PortalException {

		List<CalendarBooking> childCalendarBookings = getChildCalendarBookings(
			parentCalendarBookingId);

		if (includeStagingCalendarBookings) {
			return childCalendarBookings;
		}

		return ListUtil.filter(
			childCalendarBookings,
			childCalendarBooking -> !_calendarLocalService.isStagingCalendar(
				_calendarLocalService.fetchCalendar(
					childCalendarBooking.getCalendarId())));
	}

	@Override
	public List<CalendarBooking> getChildCalendarBookings(
			long parentCalendarBookingId, int status)
		throws PortalException {

		List<CalendarBooking> calendarBookings =
			calendarBookingPersistence.findByP_S(
				parentCalendarBookingId, status);

		return _filterCalendarBookings(calendarBookings);
	}

	@Override
	public CalendarBooking getLastInstanceCalendarBooking(
			long calendarBookingId)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarBooking.getCalendarId(),
			ActionKeys.VIEW);

		return calendarBookingLocalService.getLastInstanceCalendarBooking(
			calendarBooking);
	}

	@Override
	public CalendarBooking getNewStartTimeAndDurationCalendarBooking(
			long calendarBookingId, long offset, long duration)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarBooking.getCalendarId(),
			CalendarActionKeys.MANAGE_BOOKINGS);

		calendarBooking.setStartTime(calendarBooking.getStartTime() + offset);
		calendarBooking.setEndTime(calendarBooking.getStartTime() + duration);

		return calendarBooking;
	}

	@Override
	public boolean hasChildCalendarBookings(long parentCalendarBookingId) {
		int total = calendarBookingPersistence.countByParentCalendarBookingId(
			parentCalendarBookingId);

		if (total > 1) {
			return true;
		}

		return false;
	}

	@Override
	public CalendarBooking invokeTransition(
			long calendarBookingId, int instanceIndex, int status,
			boolean updateInstance, boolean allFollowing,
			ServiceContext serviceContext)
		throws PortalException {

		CalendarBooking calendarBookingInstance = getCalendarBookingInstance(
			calendarBookingId, instanceIndex);

		return invokeTransition(
			calendarBookingId, calendarBookingInstance.getStartTime(), status,
			updateInstance, allFollowing, serviceContext);
	}

	@Override
	public CalendarBooking invokeTransition(
			long calendarBookingId, long startTime, int status,
			boolean updateInstance, boolean allFollowing,
			ServiceContext serviceContext)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarBooking.getCalendarId(),
			CalendarActionKeys.MANAGE_BOOKINGS);

		return calendarBookingLocalService.invokeTransition(
			getUserId(), calendarBooking, startTime, status, updateInstance,
			allFollowing, serviceContext);
	}

	@Override
	public CalendarBooking moveCalendarBookingToTrash(long calendarBookingId)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarBooking.getCalendarId(),
			CalendarActionKeys.MANAGE_BOOKINGS);

		return calendarBookingLocalService.moveCalendarBookingToTrash(
			getUserId(), calendarBooking.getCalendarBookingId());
	}

	@Override
	public CalendarBooking restoreCalendarBookingFromTrash(
			long calendarBookingId)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarBooking.getCalendarId(),
			CalendarActionKeys.MANAGE_BOOKINGS);

		return calendarBookingLocalService.restoreCalendarBookingFromTrash(
			getUserId(), calendarBooking.getCalendarBookingId());
	}

	@AccessControlled(guestAccessEnabled = true)
	@Override
	public List<CalendarBooking> search(
			long companyId, long[] groupIds, long[] calendarIds,
			long[] calendarResourceIds, long parentCalendarBookingId,
			String keywords, long startTime, long endTime,
			TimeZone displayTimeZone, boolean recurring, int[] statuses,
			int start, int end,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws PortalException {

		List<CalendarBooking> calendarBookings =
			calendarBookingLocalService.search(
				companyId, groupIds, calendarIds, calendarResourceIds,
				parentCalendarBookingId, keywords, startTime, endTime,
				displayTimeZone, recurring, statuses, start, end,
				orderByComparator);

		return _filterCalendarBookings(calendarBookings, ActionKeys.VIEW);
	}

	@Override
	public List<CalendarBooking> search(
			long companyId, long[] groupIds, long[] calendarIds,
			long[] calendarResourceIds, long parentCalendarBookingId,
			String title, String description, String location, long startTime,
			long endTime, boolean recurring, int[] statuses,
			boolean andOperator, int start, int end,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws PortalException {

		List<CalendarBooking> calendarBookings =
			calendarBookingLocalService.search(
				companyId, groupIds, calendarIds, calendarResourceIds,
				parentCalendarBookingId, title, description, location,
				startTime, endTime, recurring, statuses, andOperator, start,
				end, orderByComparator);

		return _filterCalendarBookings(calendarBookings, ActionKeys.VIEW);
	}

	@AccessControlled(guestAccessEnabled = true)
	@Override
	public int searchCount(
			long companyId, long[] groupIds, long[] calendarIds,
			long[] calendarResourceIds, long parentCalendarBookingId,
			String keywords, long startTime, long endTime, boolean recurring,
			int[] statuses)
		throws PortalException {

		List<CalendarBooking> calendarBookings = search(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, keywords, startTime, endTime, null,
			recurring, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		return calendarBookings.size();
	}

	@Override
	public int searchCount(
			long companyId, long[] groupIds, long[] calendarIds,
			long[] calendarResourceIds, long parentCalendarBookingId,
			String title, String description, String location, long startTime,
			long endTime, boolean recurring, int[] statuses,
			boolean andOperator)
		throws PortalException {

		List<CalendarBooking> calendarBookings = search(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, title, description, location, startTime,
			endTime, recurring, statuses, andOperator, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		return calendarBookings.size();
	}

	@Override
	public CalendarBooking updateCalendarBooking(
			long calendarBookingId, long calendarId, long[] childCalendarIds,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException {

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarId,
			CalendarActionKeys.MANAGE_BOOKINGS);

		return calendarBookingLocalService.updateCalendarBooking(
			getUserId(), calendarBookingId, calendarId, childCalendarIds,
			titleMap, descriptionMap, location, startTime, endTime, allDay,
			recurrence, firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	@Override
	public CalendarBooking updateCalendarBooking(
			long calendarBookingId, long calendarId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException {

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarId,
			CalendarActionKeys.MANAGE_BOOKINGS);

		return calendarBookingLocalService.updateCalendarBooking(
			getUserId(), calendarBookingId, calendarId, titleMap,
			descriptionMap, location, startTime, endTime, allDay, recurrence,
			firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	@Override
	public CalendarBooking updateCalendarBookingInstance(
			long calendarBookingId, int instanceIndex, long calendarId,
			long[] childCalendarIds, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String location, long startTime,
			long endTime, boolean allDay, boolean allFollowing,
			long firstReminder, String firstReminderType, long secondReminder,
			String secondReminderType, ServiceContext serviceContext)
		throws PortalException {

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarId,
			CalendarActionKeys.MANAGE_BOOKINGS);

		return calendarBookingLocalService.updateCalendarBookingInstance(
			getUserId(), calendarBookingId, instanceIndex, calendarId,
			childCalendarIds, titleMap, descriptionMap, location, startTime,
			endTime, allDay, allFollowing, firstReminder, firstReminderType,
			secondReminder, secondReminderType, serviceContext);
	}

	@Override
	public CalendarBooking updateCalendarBookingInstance(
			long calendarBookingId, int instanceIndex, long calendarId,
			long[] childCalendarIds, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String location, long startTime,
			long endTime, boolean allDay, String recurrence,
			boolean allFollowing, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException {

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarId,
			CalendarActionKeys.MANAGE_BOOKINGS);

		return calendarBookingLocalService.updateCalendarBookingInstance(
			getUserId(), calendarBookingId, instanceIndex, calendarId,
			childCalendarIds, titleMap, descriptionMap, location, startTime,
			endTime, allDay, recurrence, allFollowing, firstReminder,
			firstReminderType, secondReminder, secondReminderType,
			serviceContext);
	}

	@Override
	public CalendarBooking updateCalendarBookingInstance(
			long calendarBookingId, int instanceIndex, long calendarId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, int startTimeYear, int startTimeMonth,
			int startTimeDay, int startTimeHour, int startTimeMinute,
			int endTimeYear, int endTimeMonth, int endTimeDay, int endTimeHour,
			int endTimeMinute, String timeZoneId, boolean allDay,
			String recurrence, boolean allFollowing, long firstReminder,
			String firstReminderType, long secondReminder,
			String secondReminderType, ServiceContext serviceContext)
		throws PortalException {

		TimeZone timeZone = TimeZoneUtil.getTimeZone(timeZoneId);

		if (allDay) {
			timeZone = TimeZone.getTimeZone(StringPool.UTC);
		}

		java.util.Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(
			startTimeYear, startTimeMonth, startTimeDay, startTimeHour,
			startTimeMinute, 0, 0, timeZone);

		java.util.Calendar endTimeJCalendar = JCalendarUtil.getJCalendar(
			endTimeYear, endTimeMonth, endTimeDay, endTimeHour, endTimeMinute,
			0, 0, timeZone);

		return calendarBookingService.updateCalendarBookingInstance(
			calendarBookingId, instanceIndex, calendarId, titleMap,
			descriptionMap, location, startTimeJCalendar.getTimeInMillis(),
			endTimeJCalendar.getTimeInMillis(), allDay, recurrence,
			allFollowing, firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	@Override
	public CalendarBooking updateCalendarBookingInstance(
			long calendarBookingId, int instanceIndex, long calendarId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, boolean allFollowing, long firstReminder,
			String firstReminderType, long secondReminder,
			String secondReminderType, ServiceContext serviceContext)
		throws PortalException {

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarId,
			CalendarActionKeys.MANAGE_BOOKINGS);

		return calendarBookingLocalService.updateCalendarBookingInstance(
			getUserId(), calendarBookingId, instanceIndex, calendarId, titleMap,
			descriptionMap, location, startTime, endTime, allDay, recurrence,
			allFollowing, firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	@Override
	public void updateLastInstanceCalendarBookingRecurrence(
			long calendarBookingId, String recurrence)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarBooking.getCalendarId(),
			CalendarActionKeys.MANAGE_BOOKINGS);

		calendarBookingLocalService.updateLastInstanceCalendarBookingRecurrence(
			calendarBooking, recurrence);
	}

	@Override
	public CalendarBooking updateOffsetAndDuration(
			long calendarBookingId, long calendarId, long[] childCalendarIds,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long offset, long duration, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.fetchByPrimaryKey(calendarBookingId);

		java.util.Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(
			calendarBooking.getStartTime() + offset);

		java.util.Calendar endTimeJCalendar = JCalendarUtil.getJCalendar(
			startTimeJCalendar.getTimeInMillis() + duration);

		return calendarBookingService.updateCalendarBooking(
			calendarBookingId, calendarId, childCalendarIds, titleMap,
			descriptionMap, location, startTimeJCalendar.getTimeInMillis(),
			endTimeJCalendar.getTimeInMillis(), allDay, recurrence,
			firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	@Override
	public CalendarBooking updateOffsetAndDuration(
			long calendarBookingId, long calendarId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long offset, long duration, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException {

		long[] childCalendarIds =
			calendarBookingLocalService.getChildCalendarIds(
				calendarBookingId, calendarId);

		return updateOffsetAndDuration(
			calendarBookingId, calendarId, childCalendarIds, titleMap,
			descriptionMap, location, offset, duration, allDay, recurrence,
			firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	@Override
	public CalendarBooking updateRecurringCalendarBooking(
			long calendarBookingId, long calendarId, long[] childCalendarIds,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			long firstReminder, String firstReminderType, long secondReminder,
			String secondReminderType, ServiceContext serviceContext)
		throws PortalException {

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarId,
			CalendarActionKeys.MANAGE_BOOKINGS);

		return calendarBookingLocalService.updateRecurringCalendarBooking(
			getUserId(), calendarBookingId, calendarId, childCalendarIds,
			titleMap, descriptionMap, location, startTime, endTime, allDay,
			firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	private String _exportToRSS(
		String name, String description, String type, double version,
		String displayStyle, String feedURL,
		List<CalendarBooking> calendarBookings, ThemeDisplay themeDisplay) {

		SyndFeed syndFeed = _syndModelFactory.createSyndFeed();

		syndFeed.setDescription(description);

		List<SyndEntry> syndEntries = new ArrayList<>();

		syndFeed.setEntries(syndEntries);

		Locale locale = themeDisplay.getLocale();

		for (CalendarBooking calendarBooking : calendarBookings) {
			SyndEntry syndEntry = _syndModelFactory.createSyndEntry();

			syndEntry.setAuthor(_portal.getUserName(calendarBooking));

			SyndContent syndContent = _syndModelFactory.createSyndContent();

			syndContent.setType(RSSUtil.ENTRY_TYPE_DEFAULT);
			syndContent.setValue(
				_getContent(calendarBooking, displayStyle, themeDisplay));

			syndEntry.setDescription(syndContent);

			String link = StringPool.BLANK;

			syndEntry.setLink(link);

			syndEntry.setPublishedDate(calendarBooking.getCreateDate());
			syndEntry.setTitle(calendarBooking.getTitle(locale));
			syndEntry.setUpdatedDate(calendarBooking.getModifiedDate());
			syndEntry.setUri(link);

			syndEntries.add(syndEntry);
		}

		syndFeed.setFeedType(RSSUtil.getFeedType(type, version));

		List<SyndLink> syndLinks = new ArrayList<>();

		syndFeed.setLinks(syndLinks);

		SyndLink syndLink = _syndModelFactory.createSyndLink();

		syndLinks.add(syndLink);

		syndLink.setHref(feedURL);
		syndLink.setRel("self");

		syndFeed.setPublishedDate(new Date());
		syndFeed.setTitle(name);
		syndFeed.setUri(feedURL);

		return _rssExporter.export(syndFeed);
	}

	private CalendarBooking _filterCalendarBooking(
			CalendarBooking calendarBooking)
		throws PortalException {

		_calendarModelResourcePermission.check(
			getPermissionChecker(), calendarBooking.getCalendarId(),
			ActionKeys.VIEW);

		if (!_calendarModelResourcePermission.contains(
				getPermissionChecker(), calendarBooking.getCalendarId(),
				CalendarActionKeys.VIEW_BOOKING_DETAILS)) {

			calendarBooking.setTitle(StringPool.BLANK);
			calendarBooking.setDescription(StringPool.BLANK);
			calendarBooking.setLocation(StringPool.BLANK);
		}

		return calendarBooking;
	}

	private List<CalendarBooking> _filterCalendarBookings(
		List<CalendarBooking> calendarBookings) {

		return TransformUtil.transform(
			calendarBookings,
			calendarBooking -> {
				try {
					return _filterCalendarBooking(calendarBooking);
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(portalException);
					}

					return null;
				}
			});
	}

	private List<CalendarBooking> _filterCalendarBookings(
			List<CalendarBooking> calendarBookings, String actionId)
		throws PortalException {

		calendarBookings = ListUtil.copy(calendarBookings);

		Iterator<CalendarBooking> iterator = calendarBookings.iterator();

		while (iterator.hasNext()) {
			CalendarBooking calendarBooking = iterator.next();

			if (_isPendingInWorkflow(calendarBooking)) {
				iterator.remove();

				continue;
			}

			if (!_calendarModelResourcePermission.contains(
					getPermissionChecker(), calendarBooking.getCalendarId(),
					CalendarActionKeys.VIEW_BOOKING_DETAILS)) {

				if (!_calendarModelResourcePermission.contains(
						getPermissionChecker(), calendarBooking.getCalendarId(),
						actionId)) {

					iterator.remove();
				}
				else {
					_filterCalendarBooking(calendarBooking);
				}
			}
		}

		return calendarBookings;
	}

	private String _getContent(
		CalendarBooking calendarBooking, String displayStyle,
		ThemeDisplay themeDisplay) {

		if (displayStyle.equals(RSSUtil.DISPLAY_STYLE_ABSTRACT)) {
			return StringUtil.shorten(
				calendarBooking.getDescription(themeDisplay.getLocale()), 200);
		}

		if (displayStyle.equals(RSSUtil.DISPLAY_STYLE_TITLE)) {
			return calendarBooking.getTitle(themeDisplay.getLocale());
		}

		String content = null;

		try {
			content = StringUtil.read(
				CalendarServiceConfigurationValues.class.getClassLoader(),
				CalendarServiceConfigurationValues.CALENDAR_RSS_TEMPLATE);
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to read the content for " +
					CalendarServiceConfigurationValues.CALENDAR_RSS_TEMPLATE,
				ioException);
		}

		TimeZone timeZone = themeDisplay.getTimeZone();

		if (calendarBooking.isAllDay()) {
			timeZone = TimeZone.getTimeZone(StringPool.UTC);
		}

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			themeDisplay.getLocale(), timeZone);

		return StringUtil.replace(
			content,
			new String[] {
				"[$EVENT_DESCRIPTION$]", "[$EVENT_END_DATE$]",
				"[$EVENT_LOCATION$]", "[$EVENT_START_DATE$]", "[$EVENT_TITLE$]"
			},
			new String[] {
				calendarBooking.getDescription(themeDisplay.getLocale()),
				dateTimeFormat.format(calendarBooking.getEndTime()),
				calendarBooking.getLocation(),
				dateTimeFormat.format(calendarBooking.getStartTime()),
				calendarBooking.getTitle(themeDisplay.getLocale())
			});
	}

	private boolean _isPendingInWorkflow(CalendarBooking calendarBooking)
		throws PortalException {

		if (calendarBooking.isPending() &&
			!_calendarModelResourcePermission.contains(
				getPermissionChecker(), calendarBooking.getCalendarId(),
				CalendarActionKeys.MANAGE_BOOKINGS)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarBookingServiceImpl.class);

	@Reference
	private CalendarLocalService _calendarLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.calendar.model.Calendar)"
	)
	private ModelResourcePermission<Calendar> _calendarModelResourcePermission;

	@Reference
	private CalendarService _calendarService;

	@Reference
	private Portal _portal;

	@Reference
	private RSSExporter _rssExporter;

	@Reference
	private SyndModelFactory _syndModelFactory;

}