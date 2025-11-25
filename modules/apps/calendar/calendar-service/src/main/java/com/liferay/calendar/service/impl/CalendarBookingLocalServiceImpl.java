/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.link.constants.AssetLinkConstants;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.calendar.configuration.CalendarServiceConfigurationValues;
import com.liferay.calendar.constants.CalendarBookingConstants;
import com.liferay.calendar.constants.CalendarPortletKeys;
import com.liferay.calendar.exception.CalendarBookingDurationException;
import com.liferay.calendar.exception.CalendarBookingRecurrenceException;
import com.liferay.calendar.exception.NoSuchCalendarException;
import com.liferay.calendar.exporter.CalendarDataFormat;
import com.liferay.calendar.exporter.CalendarDataHandler;
import com.liferay.calendar.exporter.CalendarDataHandlerFactory;
import com.liferay.calendar.internal.notification.NotificationTemplateContextFactory;
import com.liferay.calendar.internal.recurrence.RecurrenceSplit;
import com.liferay.calendar.internal.recurrence.RecurrenceSplitterUtil;
import com.liferay.calendar.internal.util.CalendarUtil;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.notification.NotificationRecipient;
import com.liferay.calendar.notification.NotificationSender;
import com.liferay.calendar.notification.NotificationTemplateContext;
import com.liferay.calendar.notification.NotificationTemplateType;
import com.liferay.calendar.notification.NotificationType;
import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.calendar.recurrence.RecurrenceSerializer;
import com.liferay.calendar.service.base.CalendarBookingLocalServiceBaseImpl;
import com.liferay.calendar.service.persistence.CalendarPersistence;
import com.liferay.calendar.social.CalendarActivityKeys;
import com.liferay.calendar.util.JCalendarUtil;
import com.liferay.calendar.util.RecurrenceUtil;
import com.liferay.calendar.workflow.constants.CalendarBookingWorkflowConstants;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.social.kernel.model.SocialActivityConstants;
import com.liferay.social.kernel.service.SocialActivityCounterLocalService;
import com.liferay.social.kernel.service.SocialActivityLocalService;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo Lundgren
 * @author Fabio Pezzutto
 * @author Marcellus Tavares
 * @author Pier Paolo Ramon
 */
@Component(
	property = "model.class.name=com.liferay.calendar.model.CalendarBooking",
	service = AopService.class
)
public class CalendarBookingLocalServiceImpl
	extends CalendarBookingLocalServiceBaseImpl {

	@Override
	public CalendarBooking addCalendarBooking(
			long userId, long calendarId, long[] childCalendarIds,
			long parentCalendarBookingId, long recurringCalendarBookingId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException {

		// Calendar booking

		User user = _userLocalService.getUser(userId);
		Calendar calendar = _calendarPersistence.findByPrimaryKey(calendarId);

		long calendarBookingId = counterLocalService.increment();

		for (Map.Entry<Locale, String> entry : descriptionMap.entrySet()) {
			String description = null;

			if (!FeatureFlagManagerUtil.isEnabled(
					calendar.getCompanyId(), "LPD-11235") &&
				!FeatureFlagManagerUtil.isEnabled(
					calendar.getCompanyId(), "LPD-31212")) {

				description = SanitizerUtil.sanitize(
					calendar.getCompanyId(), calendar.getGroupId(), userId,
					CalendarBooking.class.getName(), calendarBookingId,
					ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
					entry.getValue(), null);
			}
			else {
				description = _sanitize(entry.getValue());
			}

			descriptionMap.put(entry.getKey(), description);
		}

		TimeZone timeZone = _getTimeZone(calendar, allDay);

		java.util.Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(
			startTime, timeZone);
		java.util.Calendar endTimeJCalendar = JCalendarUtil.getJCalendar(
			endTime, timeZone);

		if (firstReminder < secondReminder) {
			long originalSecondReminder = secondReminder;

			secondReminder = firstReminder;
			firstReminder = originalSecondReminder;
		}

		Date date = new Date();

		_validate(startTimeJCalendar, endTimeJCalendar, recurrence);

		CalendarBooking calendarBooking = calendarBookingPersistence.create(
			calendarBookingId);

		calendarBooking.setUuid(serviceContext.getUuid());
		calendarBooking.setGroupId(calendar.getGroupId());
		calendarBooking.setCompanyId(user.getCompanyId());
		calendarBooking.setUserId(user.getUserId());
		calendarBooking.setUserName(user.getFullName());

		Date createDate = serviceContext.getCreateDate(date);

		calendarBooking.setCreateDate(createDate);
		serviceContext.setCreateDate(createDate);

		Date modifiedDate = serviceContext.getModifiedDate(date);

		calendarBooking.setModifiedDate(modifiedDate);
		serviceContext.setModifiedDate(modifiedDate);

		calendarBooking.setCalendarId(calendarId);
		calendarBooking.setCalendarResourceId(calendar.getCalendarResourceId());

		if (parentCalendarBookingId > 0) {
			calendarBooking.setParentCalendarBookingId(parentCalendarBookingId);
		}
		else {
			calendarBooking.setParentCalendarBookingId(calendarBookingId);
		}

		if (recurringCalendarBookingId > 0) {
			calendarBooking.setRecurringCalendarBookingId(
				recurringCalendarBookingId);
		}
		else {
			calendarBooking.setRecurringCalendarBookingId(calendarBookingId);
		}

		String vEventUid = (String)serviceContext.getAttribute("vEventUid");

		if (vEventUid == null) {
			vEventUid = PortalUUIDUtil.generate();
		}

		calendarBooking.setVEventUid(vEventUid);
		calendarBooking.setTitleMap(titleMap);
		calendarBooking.setDescriptionMap(descriptionMap);
		calendarBooking.setLocation(location);
		calendarBooking.setStartTime(startTimeJCalendar.getTimeInMillis());
		calendarBooking.setEndTime(endTimeJCalendar.getTimeInMillis());
		calendarBooking.setAllDay(allDay);
		calendarBooking.setRecurrence(recurrence);
		calendarBooking.setFirstReminder(firstReminder);
		calendarBooking.setFirstReminderType(firstReminderType);
		calendarBooking.setSecondReminder(secondReminder);
		calendarBooking.setSecondReminderType(secondReminderType);

		int status = 0;

		if (calendarBooking.isMasterBooking()) {
			status = WorkflowConstants.STATUS_DRAFT;
		}
		else if (hasExclusiveCalendarBooking(calendar, startTime, endTime)) {
			status = WorkflowConstants.STATUS_DENIED;
		}
		else if (isStagingCalendarBooking(calendarBooking)) {
			status = CalendarBookingWorkflowConstants.STATUS_MASTER_STAGING;
		}
		else if (_isMasterPending(calendarBooking)) {
			status = CalendarBookingWorkflowConstants.STATUS_MASTER_PENDING;
		}
		else {
			status = WorkflowConstants.STATUS_PENDING;
		}

		calendarBooking.setStatus(status);
		calendarBooking.setStatusDate(serviceContext.getModifiedDate(date));
		calendarBooking.setExpandoBridgeAttributes(serviceContext);

		calendarBooking = calendarBookingPersistence.update(calendarBooking);

		_addChildCalendarBookings(
			calendarBooking, childCalendarIds, serviceContext);

		// Resources

		_resourceLocalService.addModelResources(
			calendarBooking, serviceContext);

		// Asset

		updateAsset(
			userId, calendarBooking, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Social

		_socialActivityLocalService.addActivity(
			userId, calendarBooking.getGroupId(),
			CalendarBooking.class.getName(), calendarBookingId,
			CalendarActivityKeys.ADD_CALENDAR_BOOKING,
			_getExtraDataJSON(calendarBooking), 0);

		// Notifications

		_sendNotification(
			calendarBooking, NotificationTemplateType.INVITE, serviceContext);

		// Workflow

		if (calendarBooking.isMasterBooking()) {
			WorkflowHandlerRegistryUtil.startWorkflowInstance(
				calendarBooking.getCompanyId(), calendarBooking.getGroupId(),
				userId, CalendarBooking.class.getName(),
				calendarBooking.getCalendarBookingId(), calendarBooking,
				serviceContext);
		}

		return calendarBooking;
	}

	@Override
	public void checkCalendarBookings() throws PortalException {
		Date date = new Date();

		List<CalendarBooking> calendarBookings =
			calendarBookingFinder.findByFutureReminders(date.getTime());

		long endTime = date.getTime() + Time.MONTH;

		calendarBookings = RecurrenceUtil.expandCalendarBookings(
			calendarBookings, date.getTime(), endTime, 1);

		for (CalendarBooking calendarBooking : calendarBookings) {
			try {
				Company company = _companyLocalService.getCompany(
					calendarBooking.getCompanyId());

				if (company.isActive() &&
					!isStagingCalendarBooking(calendarBooking)) {

					_notifyCalendarBookingReminders(
						calendarBooking, date.getTime());
				}
			}
			catch (PortalException portalException) {
				throw portalException;
			}
			catch (SystemException systemException) {
				throw systemException;
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}
		}
	}

	@Override
	public CalendarBooking deleteCalendarBooking(
			CalendarBooking calendarBooking)
		throws PortalException {

		return calendarBookingLocalService.deleteCalendarBooking(
			calendarBooking, false);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public CalendarBooking deleteCalendarBooking(
			CalendarBooking calendarBooking, boolean allRecurringInstances)
		throws PortalException {

		// Calendar bookings

		Set<CalendarBooking> recurringCalendarBookings = new HashSet<>();

		List<CalendarBooking> childCalendarBookings = new ArrayList<>();

		childCalendarBookings.addAll(
			getChildCalendarBookings(calendarBooking.getCalendarBookingId()));

		childCalendarBookings.add(calendarBooking);

		for (CalendarBooking childCalendarBooking : childCalendarBookings) {
			if (allRecurringInstances) {
				recurringCalendarBookings.addAll(
					getRecurringCalendarBookings(childCalendarBooking));
			}
			else {
				recurringCalendarBookings.add(childCalendarBooking);
			}
		}

		for (CalendarBooking recurringCalendarBooking :
				recurringCalendarBookings) {

			// Calendar booking

			calendarBookingPersistence.remove(recurringCalendarBooking);

			// Resources

			_resourceLocalService.deleteResource(
				recurringCalendarBooking, ResourceConstants.SCOPE_INDIVIDUAL);

			// Subscriptions

			subscriptionLocalService.deleteSubscriptions(
				recurringCalendarBooking.getCompanyId(),
				CalendarBooking.class.getName(),
				recurringCalendarBooking.getCalendarBookingId());

			// Asset

			_assetEntryLocalService.deleteEntry(
				CalendarBooking.class.getName(),
				recurringCalendarBooking.getCalendarBookingId());

			// Message boards

			mbMessageLocalService.deleteDiscussionMessages(
				CalendarBooking.class.getName(),
				recurringCalendarBooking.getCalendarBookingId());

			// Ratings

			_ratingsStatsLocalService.deleteStats(
				CalendarBooking.class.getName(),
				recurringCalendarBooking.getCalendarBookingId());

			// Trash

			trashEntryLocalService.deleteEntry(
				CalendarBooking.class.getName(),
				recurringCalendarBooking.getCalendarBookingId());

			// Workflow

			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
				recurringCalendarBooking.getCompanyId(),
				recurringCalendarBooking.getGroupId(),
				CalendarBooking.class.getName(),
				recurringCalendarBooking.getCalendarBookingId());
		}

		return calendarBooking;
	}

	@Override
	public CalendarBooking deleteCalendarBooking(long calendarBookingId)
		throws PortalException {

		return deleteCalendarBooking(calendarBookingId, false);
	}

	@Override
	public CalendarBooking deleteCalendarBooking(
			long calendarBookingId, boolean allRecurringInstances)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		calendarBookingLocalService.deleteCalendarBooking(
			calendarBooking, allRecurringInstances);

		return calendarBooking;
	}

	@Override
	public void deleteCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, int instanceIndex,
			boolean allFollowing)
		throws PortalException {

		CalendarBooking calendarBookingInstance =
			RecurrenceUtil.getCalendarBookingInstance(
				calendarBooking, instanceIndex);

		deleteCalendarBookingInstance(
			userId, calendarBooking, calendarBookingInstance.getStartTime(),
			allFollowing);
	}

	@Override
	public void deleteCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, int instanceIndex,
			boolean allFollowing, boolean deleteRecurringCalendarBookings)
		throws PortalException {

		CalendarBooking calendarBookingInstance =
			RecurrenceUtil.getCalendarBookingInstance(
				calendarBooking, instanceIndex);

		deleteCalendarBookingInstance(
			userId, calendarBooking, calendarBookingInstance.getStartTime(),
			allFollowing, deleteRecurringCalendarBookings);
	}

	@Override
	public void deleteCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, long startTime,
			boolean allFollowing)
		throws PortalException {

		deleteCalendarBookingInstance(
			userId, calendarBooking, startTime, allFollowing, false);
	}

	@Override
	public void deleteCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, long startTime,
			boolean allFollowing, boolean deleteRecurringCalendarBookings)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUserId(userId);

		NotificationTemplateType notificationTemplateType =
			NotificationTemplateType.INSTANCE_DELETED;

		java.util.Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(
			startTime, calendarBooking.getTimeZone());

		Recurrence recurrenceObj = calendarBooking.getRecurrenceObj();

		if (allFollowing) {
			if (deleteRecurringCalendarBookings) {
				List<CalendarBooking> recurringCalendarBookings =
					_splitCalendarBookingInstances(
						userId, calendarBooking, startTime);

				for (CalendarBooking recurringCalendarBooking :
						recurringCalendarBookings) {

					deleteCalendarBooking(recurringCalendarBooking, false);
				}
			}

			if (startTime == calendarBooking.getStartTime()) {
				_sendChildrenNotifications(
					calendarBooking, NotificationTemplateType.MOVED_TO_TRASH,
					serviceContext);

				calendarBookingLocalService.deleteCalendarBooking(
					calendarBooking, false);

				return;
			}

			if (recurrenceObj.getCount() > 0) {
				recurrenceObj.setCount(0);
			}

			if (_hasFollowingInstances(calendarBooking, startTime)) {
				notificationTemplateType = NotificationTemplateType.UPDATE;
			}

			startTimeJCalendar.add(java.util.Calendar.DATE, -1);

			recurrenceObj.setUntilJCalendar(startTimeJCalendar);
		}
		else {
			CalendarBooking calendarBookingInstance =
				RecurrenceUtil.getCalendarBookingInstance(calendarBooking, 1);

			if (calendarBookingInstance == null) {
				_sendChildrenNotifications(
					calendarBooking, NotificationTemplateType.MOVED_TO_TRASH,
					serviceContext);

				calendarBookingLocalService.deleteCalendarBooking(
					calendarBooking, false);

				return;
			}

			recurrenceObj.addExceptionJCalendar(startTimeJCalendar);
		}

		String recurrence = RecurrenceSerializer.serialize(recurrenceObj);

		_updateChildCalendarBookings(calendarBooking, new Date(), recurrence);

		serviceContext.setAttribute("instanceStartTime", startTime);

		_sendChildrenNotifications(
			calendarBooking, notificationTemplateType, serviceContext);
	}

	@Override
	public void deleteCalendarBookingInstance(
			long userId, long calendarBookingId, long startTime,
			boolean allFollowing)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		deleteCalendarBookingInstance(
			userId, calendarBooking, startTime, allFollowing);
	}

	@Override
	public void deleteCalendarBookings(long calendarId) throws PortalException {
		List<CalendarBooking> calendarBookings =
			calendarBookingPersistence.findByCalendarId(calendarId);

		for (CalendarBooking calendarBooking : calendarBookings) {
			calendarBookingLocalService.deleteCalendarBooking(calendarBooking);
		}
	}

	@Override
	public CalendarBooking deleteRecurringCalendarBooking(
			CalendarBooking calendarBooking)
		throws PortalException {

		return calendarBookingLocalService.deleteCalendarBooking(
			calendarBooking, true);
	}

	@Override
	public CalendarBooking deleteRecurringCalendarBooking(
			long calendarBookingId)
		throws PortalException {

		return calendarBookingLocalService.deleteCalendarBooking(
			calendarBookingId, true);
	}

	@Override
	public String exportCalendarBooking(long calendarBookingId, String type)
		throws Exception {

		CalendarDataFormat calendarDataFormat = CalendarDataFormat.parse(type);

		CalendarDataHandler calendarDataHandler =
			CalendarDataHandlerFactory.getCalendarDataHandler(
				calendarDataFormat);

		return calendarDataHandler.exportCalendarBooking(calendarBookingId);
	}

	@Override
	public CalendarBooking fetchCalendarBooking(
		long calendarId, String vEventUid) {

		return calendarBookingPersistence.fetchByC_V(calendarId, vEventUid);
	}

	@Override
	public CalendarBooking fetchCalendarBooking(String uuid, long groupId) {
		return calendarBookingPersistence.fetchByUUID_G(uuid, groupId);
	}

	@Override
	public CalendarBooking getCalendarBooking(long calendarBookingId)
		throws PortalException {

		return calendarBookingPersistence.findByPrimaryKey(calendarBookingId);
	}

	@Override
	public CalendarBooking getCalendarBooking(
			long calendarId, long parentCalendarBookingId)
		throws PortalException {

		return calendarBookingPersistence.findByC_P(
			calendarId, parentCalendarBookingId);
	}

	@Override
	public CalendarBooking getCalendarBookingInstance(
			long calendarBookingId, int instanceIndex)
		throws PortalException {

		return RecurrenceUtil.getCalendarBookingInstance(
			getCalendarBooking(calendarBookingId), instanceIndex);
	}

	@Override
	public List<CalendarBooking> getCalendarBookings(long calendarId) {
		return calendarBookingPersistence.findByCalendarId(calendarId);
	}

	@Override
	public List<CalendarBooking> getCalendarBookings(
		long calendarId, int[] statuses) {

		return calendarBookingPersistence.findByC_S(calendarId, statuses);
	}

	@Override
	public List<CalendarBooking> getCalendarBookings(
		long calendarId, long startTime, long endTime) {

		return getCalendarBookings(
			calendarId, startTime, endTime, QueryUtil.ALL_POS);
	}

	@Override
	public List<CalendarBooking> getCalendarBookings(
		long calendarId, long startTime, long endTime, int max) {

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CalendarBooking.class, getClassLoader());

		Property property = PropertyFactoryUtil.forName("calendarId");

		dynamicQuery.add(property.eq(calendarId));

		if (startTime >= 0) {
			Property propertyStartTime = PropertyFactoryUtil.forName(
				"startTime");

			dynamicQuery.add(propertyStartTime.gt(startTime));
		}

		if (endTime >= 0) {
			Property propertyEndTime = PropertyFactoryUtil.forName("endTime");

			dynamicQuery.add(propertyEndTime.lt(endTime));
		}

		if (max > 0) {
			dynamicQuery.setLimit(0, max);
		}

		return dynamicQuery(dynamicQuery);
	}

	@Override
	public int getCalendarBookingsCount(
		long calendarId, long parentCalendarBookingId) {

		return calendarBookingPersistence.countByC_P(
			calendarId, parentCalendarBookingId);
	}

	@Override
	public List<CalendarBooking> getChildCalendarBookings(
		long calendarBookingId) {

		return calendarBookingPersistence.findByParentCalendarBookingId(
			calendarBookingId);
	}

	@Override
	public List<CalendarBooking> getChildCalendarBookings(
		long parentCalendarBookingId, int status) {

		return calendarBookingPersistence.findByP_S(
			parentCalendarBookingId, status);
	}

	@Override
	public long[] getChildCalendarIds(long calendarBookingId, long calendarId)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		List<CalendarBooking> childCalendarBookings =
			calendarBookingPersistence.findByParentCalendarBookingId(
				calendarBookingId);

		long[] childCalendarIds = new long[childCalendarBookings.size()];

		for (int i = 0; i < childCalendarIds.length; i++) {
			CalendarBooking childCalendarBooking = childCalendarBookings.get(i);

			if (childCalendarBooking.getCalendarId() ==
					calendarBooking.getCalendarId()) {

				childCalendarIds[i] = calendarId;
			}
			else {
				childCalendarIds[i] = childCalendarBooking.getCalendarId();
			}
		}

		return childCalendarIds;
	}

	@Override
	public CalendarBooking getLastInstanceCalendarBooking(
		CalendarBooking calendarBooking) {

		List<CalendarBooking> calendarBookings = getRecurringCalendarBookings(
			calendarBooking);

		return RecurrenceUtil.getLastInstanceCalendarBooking(calendarBookings);
	}

	@Override
	public List<CalendarBooking> getRecurringCalendarBookings(
		CalendarBooking calendarBooking) {

		return calendarBookingPersistence.findByRecurringCalendarBookingId(
			calendarBooking.getRecurringCalendarBookingId());
	}

	@Override
	public List<CalendarBooking> getRecurringCalendarBookings(
		CalendarBooking calendarBooking, long startTime) {

		return TransformUtil.transform(
			getRecurringCalendarBookings(calendarBooking),
			recurringCalendarBooking -> {
				if (recurringCalendarBooking.getStartTime() > startTime) {
					return recurringCalendarBooking;
				}

				return null;
			});
	}

	@Override
	public boolean hasExclusiveCalendarBooking(
			Calendar calendar, long startTime, long endTime)
		throws PortalException {

		CalendarResource calendarResource = calendar.getCalendarResource();

		if (calendarResource.isGroup() && calendarResource.isUser()) {
			return false;
		}

		List<CalendarBooking> calendarBookings =
			_getOverlappingCalendarBookings(
				calendar.getCalendarId(), startTime, endTime,
				new int[] {
					WorkflowConstants.STATUS_APPROVED,
					WorkflowConstants.STATUS_PENDING
				});

		return !calendarBookings.isEmpty();
	}

	@Override
	public CalendarBooking invokeTransition(
			long userId, CalendarBooking calendarBooking, long startTime,
			int status, boolean updateInstance, boolean allFollowing,
			ServiceContext serviceContext)
		throws PortalException {

		if (updateInstance) {
			long calendarId = calendarBooking.getCalendarId();

			long[] childCalendarIds = getChildCalendarIds(
				calendarBooking.getCalendarBookingId(), calendarId);

			long duration =
				calendarBooking.getEndTime() - calendarBooking.getStartTime();

			long endTime = startTime + duration;

			String recurrence = null;

			_addAssetEntry(calendarBooking, serviceContext);

			if (allFollowing) {
				List<CalendarBooking> recurringCalendarBookings =
					_splitCalendarBookingInstances(
						userId, calendarBooking, startTime);

				for (CalendarBooking recurringCalendarBooking :
						recurringCalendarBookings) {

					calendarBookingLocalService.updateStatus(
						userId, recurringCalendarBooking, status,
						serviceContext);
				}

				Recurrence recurrenceObj = calendarBooking.getRecurrenceObj();

				if (recurrenceObj != null) {
					int count = recurrenceObj.getCount();

					if (count > 0) {
						int instanceIndex = RecurrenceUtil.getIndexOfInstance(
							calendarBooking.getRecurrence(),
							calendarBooking.getStartTime(), startTime);

						recurrenceObj.setCount(count - instanceIndex);
					}
				}

				recurrence = RecurrenceSerializer.serialize(recurrenceObj);
			}

			deleteCalendarBookingInstance(
				userId, calendarBooking, startTime, allFollowing);

			calendarBooking = addCalendarBooking(
				userId, calendarId, childCalendarIds, 0,
				calendarBooking.getRecurringCalendarBookingId(),
				calendarBooking.getTitleMap(),
				calendarBooking.getDescriptionMap(),
				calendarBooking.getLocation(), startTime, endTime,
				calendarBooking.isAllDay(), recurrence,
				calendarBooking.getFirstReminder(),
				calendarBooking.getFirstReminderType(),
				calendarBooking.getSecondReminder(),
				calendarBooking.getSecondReminderType(), serviceContext);

			calendarBookingLocalService.updateStatus(
				userId, calendarBooking, status, serviceContext);
		}
		else {
			List<CalendarBooking> recurringCalendarBookings =
				getRecurringCalendarBookings(calendarBooking);

			for (CalendarBooking recurringCalendarBooking :
					recurringCalendarBookings) {

				calendarBookingLocalService.updateStatus(
					userId, recurringCalendarBooking, status, serviceContext);
			}
		}

		return calendarBooking;
	}

	@Override
	public boolean isStagingCalendarBooking(CalendarBooking calendarBooking)
		throws PortalException {

		if (!calendarBooking.isMasterBooking()) {
			return isStagingCalendarBooking(
				calendarBooking.getParentCalendarBooking());
		}

		return CalendarUtil.isStagingCalendar(
			calendarBooking.getCalendar(), _groupLocalService);
	}

	@Override
	public CalendarBooking moveCalendarBookingToTrash(
			long userId, CalendarBooking calendarBooking)
		throws PortalException {

		if (calendarBooking.isInTrash()) {
			throw new TrashEntryException();
		}

		// Calendar booking

		if (!calendarBooking.isMasterBooking()) {
			return calendarBooking;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUserId(userId);

		List<CalendarBooking> recurringCalendarBookings =
			getRecurringCalendarBookings(calendarBooking);

		for (CalendarBooking recurringCalendarBooking :
				recurringCalendarBookings) {

			// Calendar booking

			calendarBookingLocalService.updateStatus(
				userId, recurringCalendarBooking,
				WorkflowConstants.STATUS_IN_TRASH, serviceContext);

			// Social

			_socialActivityCounterLocalService.disableActivityCounters(
				CalendarBooking.class.getName(),
				recurringCalendarBooking.getCalendarBookingId());

			_socialActivityLocalService.addActivity(
				userId, recurringCalendarBooking.getGroupId(),
				CalendarBooking.class.getName(),
				recurringCalendarBooking.getCalendarBookingId(),
				SocialActivityConstants.TYPE_MOVE_TO_TRASH,
				_getExtraDataJSON(recurringCalendarBooking), 0);

			// Workflow

			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
				recurringCalendarBooking.getCompanyId(),
				recurringCalendarBooking.getGroupId(),
				CalendarBooking.class.getName(),
				recurringCalendarBooking.getCalendarBookingId());
		}

		return calendarBooking;
	}

	@Override
	public CalendarBooking moveCalendarBookingToTrash(
			long userId, long calendarBookingId)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		return moveCalendarBookingToTrash(userId, calendarBooking);
	}

	@Override
	public CalendarBooking restoreCalendarBookingFromTrash(
			long userId, long calendarBookingId)
		throws PortalException {

		// Calendar booking

		CalendarBooking calendarBooking = getCalendarBooking(calendarBookingId);

		if (!calendarBooking.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (!calendarBooking.isMasterBooking()) {
			return calendarBooking;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setUserId(userId);

		TrashEntry trashEntry = trashEntryLocalService.getEntry(
			CalendarBooking.class.getName(), calendarBookingId);

		calendarBookingLocalService.updateStatus(
			userId, calendarBookingId, trashEntry.getStatus(), serviceContext);

		// Social

		_socialActivityCounterLocalService.enableActivityCounters(
			CalendarBooking.class.getName(), calendarBookingId);

		_socialActivityLocalService.addActivity(
			userId, calendarBooking.getGroupId(),
			CalendarBooking.class.getName(), calendarBookingId,
			SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
			_getExtraDataJSON(calendarBooking), 0);

		// Workflow

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			calendarBooking.getCompanyId(), calendarBooking.getGroupId(),
			userId, CalendarBooking.class.getName(),
			calendarBooking.getCalendarBookingId(), calendarBooking,
			serviceContext);

		if (calendarBooking.isMasterRecurringBooking()) {
			List<CalendarBooking> recurringCalendarBookings =
				getRecurringCalendarBookings(calendarBooking);

			for (CalendarBooking recurringCalendarBooking :
					recurringCalendarBookings) {

				if (recurringCalendarBooking.equals(calendarBooking)) {
					continue;
				}

				calendarBookingLocalService.updateStatus(
					userId, recurringCalendarBooking, trashEntry.getStatus(),
					serviceContext);
			}
		}

		return calendarBooking;
	}

	@Override
	public List<CalendarBooking> search(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String keywords, long startTime, long endTime, TimeZone displayTimeZone,
		boolean recurring, int[] statuses, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		List<CalendarBooking> calendarBookings =
			calendarBookingFinder.findByKeywords(
				companyId, groupIds, calendarIds, calendarResourceIds,
				parentCalendarBookingId, keywords, startTime, endTime,
				recurring, statuses, start, end, orderByComparator);

		if (recurring) {
			if (displayTimeZone == null) {
				calendarBookings = RecurrenceUtil.expandCalendarBookings(
					calendarBookings, startTime, endTime);
			}
			else {
				calendarBookings = RecurrenceUtil.expandCalendarBookings(
					calendarBookings, startTime, endTime, displayTimeZone);
			}
		}

		return calendarBookings;
	}

	@Override
	public List<CalendarBooking> search(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId, String title,
		String description, String location, long startTime, long endTime,
		boolean recurring, int[] statuses, boolean andOperator, int start,
		int end, OrderByComparator<CalendarBooking> orderByComparator) {

		List<CalendarBooking> calendarBookings =
			calendarBookingFinder.findByC_G_C_C_P_T_D_L_S_E_S(
				companyId, groupIds, calendarIds, calendarResourceIds,
				parentCalendarBookingId, title, description, location,
				startTime, endTime, recurring, statuses, andOperator, start,
				end, orderByComparator);

		if (recurring) {
			calendarBookings = RecurrenceUtil.expandCalendarBookings(
				calendarBookings, startTime, endTime);
		}

		return calendarBookings;
	}

	@Override
	public int searchCount(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String keywords, long startTime, long endTime, int[] statuses) {

		return calendarBookingFinder.countByKeywords(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, keywords, startTime, endTime, statuses);
	}

	@Override
	public int searchCount(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId, String title,
		String description, String location, long startTime, long endTime,
		int[] statuses, boolean andOperator) {

		return calendarBookingFinder.countByC_G_C_C_P_T_D_L_S_E_S(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, title, description, location, startTime,
			endTime, statuses, andOperator);
	}

	@Override
	public void updateAsset(
			long userId, CalendarBooking calendarBooking,
			long[] assetCategoryIds, String[] assetTagNames,
			long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		boolean visible = false;

		Date publishDate = null;

		if (calendarBooking.isApproved()) {
			visible = true;

			publishDate = calendarBooking.getCreateDate();
		}

		String summary = _htmlParser.extractText(
			StringUtil.shorten(calendarBooking.getDescription(), 500));

		AssetEntry assetEntry = _assetEntryLocalService.updateEntry(
			userId, calendarBooking.getGroupId(),
			calendarBooking.getCreateDate(), calendarBooking.getModifiedDate(),
			CalendarBooking.class.getName(),
			calendarBooking.getCalendarBookingId(), calendarBooking.getUuid(),
			0, assetCategoryIds, assetTagNames, true, visible, null, null,
			publishDate, null, ContentTypes.TEXT_HTML,
			calendarBooking.getTitle(), calendarBooking.getDescription(),
			summary, null, null, 0, 0, priority);

		_assetLinkLocalService.updateLinks(
			userId, assetEntry.getEntryId(), assetLinkEntryIds,
			AssetLinkConstants.TYPE_RELATED);
	}

	@Override
	public CalendarBooking updateCalendarBooking(
			long userId, long calendarBookingId, long calendarId,
			long[] childCalendarIds, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String location, long startTime,
			long endTime, boolean allDay, String recurrence, long firstReminder,
			String firstReminderType, long secondReminder,
			String secondReminderType, ServiceContext serviceContext)
		throws PortalException {

		// Calendar booking

		Calendar calendar = _calendarPersistence.findByPrimaryKey(calendarId);
		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		if (isStagingCalendarBooking(calendarBooking) &&
			(calendar.getGroupId() != calendarBooking.getGroupId())) {

			systemEventLocalService.addSystemEvent(
				userId, calendarBooking.getGroupId(), StringPool.BLANK,
				CalendarBooking.class.getName(),
				calendarBooking.getCalendarBookingId(),
				calendarBooking.getUuid(), null,
				SystemEventConstants.TYPE_DELETE, StringPool.BLANK);
		}

		for (Map.Entry<Locale, String> entry : descriptionMap.entrySet()) {
			String description = null;

			if (!FeatureFlagManagerUtil.isEnabled(
					calendar.getCompanyId(), "LPD-11235") &&
				!FeatureFlagManagerUtil.isEnabled(
					calendar.getCompanyId(), "LPD-31212")) {

				description = SanitizerUtil.sanitize(
					calendar.getCompanyId(), calendar.getGroupId(), userId,
					CalendarBooking.class.getName(), calendarBookingId,
					ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
					entry.getValue(), null);
			}
			else {
				description = _sanitize(entry.getValue());
			}

			descriptionMap.put(entry.getKey(), description);
		}

		TimeZone timeZone = _getTimeZone(calendar, allDay);

		java.util.Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(
			startTime, timeZone);
		java.util.Calendar endTimeJCalendar = JCalendarUtil.getJCalendar(
			endTime, timeZone);

		if (firstReminder < secondReminder) {
			long originalSecondReminder = secondReminder;

			secondReminder = firstReminder;
			firstReminder = originalSecondReminder;
		}

		_validate(startTimeJCalendar, endTimeJCalendar, recurrence);

		calendarBooking.setGroupId(calendar.getGroupId());
		calendarBooking.setModifiedDate(serviceContext.getModifiedDate(null));
		calendarBooking.setCalendarId(calendarId);
		calendarBooking.setCalendarResourceId(calendar.getCalendarResourceId());

		Map<Locale, String> updatedTitleMap = calendarBooking.getTitleMap();

		updatedTitleMap.putAll(titleMap);

		calendarBooking.setTitleMap(updatedTitleMap);

		Map<Locale, String> updatedDescriptionMap =
			calendarBooking.getDescriptionMap();

		updatedDescriptionMap.putAll(descriptionMap);

		calendarBooking.setDescriptionMap(updatedDescriptionMap);

		calendarBooking.setLocation(location);

		long oldStartTime = calendarBooking.getStartTime();
		long oldEndTime = calendarBooking.getEndTime();

		calendarBooking.setStartTime(startTimeJCalendar.getTimeInMillis());
		calendarBooking.setEndTime(endTimeJCalendar.getTimeInMillis());
		calendarBooking.setAllDay(allDay);
		calendarBooking.setRecurrence(recurrence);
		calendarBooking.setFirstReminder(firstReminder);
		calendarBooking.setFirstReminderType(firstReminderType);
		calendarBooking.setSecondReminder(secondReminder);
		calendarBooking.setSecondReminderType(secondReminderType);

		if (calendarBooking.isMasterBooking() && !calendarBooking.isDraft() &&
			!calendarBooking.isPending()) {

			calendarBooking.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else if ((oldStartTime != calendarBooking.getStartTime()) ||
				 (oldEndTime != calendarBooking.getEndTime())) {

			if (isStagingCalendarBooking(calendarBooking)) {
				calendarBooking.setStatus(
					CalendarBookingWorkflowConstants.STATUS_MASTER_STAGING);
			}
			else if (_isMasterPending(calendarBooking)) {
				calendarBooking.setStatus(
					CalendarBookingWorkflowConstants.STATUS_MASTER_PENDING);
			}
			else {
				calendarBooking.setStatus(WorkflowConstants.STATUS_PENDING);
			}
		}

		calendarBooking.setExpandoBridgeAttributes(serviceContext);

		calendarBooking = calendarBookingPersistence.update(calendarBooking);

		_updateChildCalendarBookings(
			calendarBooking, childCalendarIds, serviceContext);

		// Asset

		updateAsset(
			userId, calendarBooking, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames(),
			serviceContext.getAssetLinkEntryIds(),
			serviceContext.getAssetPriority());

		// Social

		_socialActivityLocalService.addActivity(
			userId, calendarBooking.getGroupId(),
			CalendarBooking.class.getName(), calendarBookingId,
			CalendarActivityKeys.UPDATE_CALENDAR_BOOKING,
			_getExtraDataJSON(calendarBooking), 0);

		// Notifications

		_sendNotification(
			calendarBooking, NotificationTemplateType.UPDATE, serviceContext);

		// Workflow

		if (calendarBooking.isMasterBooking()) {
			WorkflowHandlerRegistryUtil.startWorkflowInstance(
				calendarBooking.getCompanyId(), calendarBooking.getGroupId(),
				userId, CalendarBooking.class.getName(),
				calendarBooking.getCalendarBookingId(), calendarBooking,
				serviceContext);
		}

		return calendarBooking;
	}

	@Override
	public CalendarBooking updateCalendarBooking(
			long userId, long calendarBookingId, long calendarId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException {

		long[] childCalendarIds = getChildCalendarIds(
			calendarBookingId, calendarId);

		return updateCalendarBooking(
			userId, calendarBookingId, calendarId, childCalendarIds, titleMap,
			descriptionMap, location, startTime, endTime, allDay, recurrence,
			firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	@Override
	public CalendarBooking updateCalendarBookingInstance(
			long userId, long calendarBookingId, int instanceIndex,
			long calendarId, long[] childCalendarIds,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			boolean allFollowing, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.fetchByPrimaryKey(calendarBookingId);

		return updateCalendarBookingInstance(
			userId, calendarBookingId, instanceIndex, calendarId,
			childCalendarIds, titleMap, descriptionMap, location, startTime,
			endTime, allDay, calendarBooking.getRecurrence(), allFollowing,
			firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	@Override
	public CalendarBooking updateCalendarBookingInstance(
			long userId, long calendarBookingId, int instanceIndex,
			long calendarId, long[] childCalendarIds,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, boolean allFollowing, long firstReminder,
			String firstReminderType, long secondReminder,
			String secondReminderType, ServiceContext serviceContext)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		String oldRecurrence = calendarBooking.getRecurrence();

		deleteCalendarBookingInstance(
			userId, calendarBooking, instanceIndex, allFollowing, false);

		Map<Locale, String> updatedTitleMap = calendarBooking.getTitleMap();

		updatedTitleMap.putAll(titleMap);

		Map<Locale, String> updatedDescriptionMap =
			calendarBooking.getDescriptionMap();

		updatedDescriptionMap.putAll(descriptionMap);

		if (allFollowing) {
			Calendar calendar = _calendarPersistence.findByPrimaryKey(
				calendarId);

			List<CalendarBooking> recurringCalendarBookings =
				_splitCalendarBookingInstances(
					userId, calendarBooking, startTime);

			List<String> unmodifiedAttributesNames =
				_getUnmodifiedAttributesNames(
					calendarBooking, calendarId, titleMap, descriptionMap,
					location, startTime, endTime, allDay, firstReminder,
					firstReminderType, secondReminder, secondReminderType);

			Recurrence recurrenceObj = RecurrenceSerializer.deserialize(
				recurrence, calendar.getTimeZone());

			if (recurrenceObj != null) {
				if (oldRecurrence.equals(recurrence)) {
					if (recurrenceObj.getCount() > 0) {
						recurrenceObj.setCount(
							recurrenceObj.getCount() - instanceIndex);

						recurrence = RecurrenceSerializer.serialize(
							recurrenceObj);
					}
				}
				else {
					List<java.util.Calendar> exceptionJCalendars =
						recurrenceObj.getExceptionJCalendars();

					exceptionJCalendars.clear();

					recurrence = RecurrenceSerializer.serialize(recurrenceObj);
				}
			}

			_updateCalendarBookingsByChanges(
				userId, calendarId, childCalendarIds, updatedTitleMap,
				updatedDescriptionMap, location, startTime, endTime, allDay,
				firstReminder, firstReminderType, secondReminder,
				secondReminderType, serviceContext, recurringCalendarBookings,
				unmodifiedAttributesNames);
		}
		else {
			recurrence = StringPool.BLANK;
		}

		return addCalendarBooking(
			userId, calendarId, childCalendarIds,
			CalendarBookingConstants.PARENT_CALENDAR_BOOKING_ID_DEFAULT,
			calendarBooking.getRecurringCalendarBookingId(), updatedTitleMap,
			updatedDescriptionMap, location, startTime, endTime, allDay,
			recurrence, firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	@Override
	public CalendarBooking updateCalendarBookingInstance(
			long userId, long calendarBookingId, int instanceIndex,
			long calendarId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String location, long startTime,
			long endTime, boolean allDay, String recurrence,
			boolean allFollowing, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException {

		long[] childCalendarIds = getChildCalendarIds(
			calendarBookingId, calendarId);

		return updateCalendarBookingInstance(
			userId, calendarBookingId, instanceIndex, calendarId,
			childCalendarIds, titleMap, descriptionMap, location, startTime,
			endTime, allDay, recurrence, allFollowing, firstReminder,
			firstReminderType, secondReminder, secondReminderType,
			serviceContext);
	}

	@Override
	public void updateLastInstanceCalendarBookingRecurrence(
		CalendarBooking calendarBooking, String recurrence) {

		CalendarBooking lastInstanceCalendarBooking =
			getLastInstanceCalendarBooking(calendarBooking);

		if (recurrence == null) {
			recurrence = StringPool.BLANK;
		}
		else {
			Recurrence oldRecurrenceObj =
				lastInstanceCalendarBooking.getRecurrenceObj();

			Recurrence recurrenceObj = RecurrenceSerializer.deserialize(
				recurrence, calendarBooking.getTimeZone());

			if ((oldRecurrenceObj != null) && (recurrenceObj != null)) {
				recurrenceObj.setExceptionJCalendars(
					oldRecurrenceObj.getExceptionJCalendars());

				recurrence = RecurrenceSerializer.serialize(recurrenceObj);
			}
		}

		if (!recurrence.equals(lastInstanceCalendarBooking.getRecurrence())) {
			lastInstanceCalendarBooking.setRecurrence(recurrence);

			calendarBookingPersistence.update(lastInstanceCalendarBooking);
		}
	}

	@Override
	public CalendarBooking updateRecurringCalendarBooking(
			long userId, long calendarBookingId, long calendarId,
			long[] childCalendarIds, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String location, long startTime,
			long endTime, boolean allDay, long firstReminder,
			String firstReminderType, long secondReminder,
			String secondReminderType, ServiceContext serviceContext)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		List<CalendarBooking> recurringCalendarBookings =
			getRecurringCalendarBookings(calendarBooking);

		List<String> unmodifiedAttributesNames = _getUnmodifiedAttributesNames(
			calendarBooking, calendarId, titleMap, descriptionMap, location,
			startTime, endTime, allDay, firstReminder, firstReminderType,
			secondReminder, secondReminderType);

		_updateCalendarBookingsByChanges(
			userId, calendarId, childCalendarIds, titleMap, descriptionMap,
			location, startTime, endTime, allDay, firstReminder,
			firstReminderType, secondReminder, secondReminderType,
			serviceContext, recurringCalendarBookings,
			unmodifiedAttributesNames);

		return calendarBooking;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CalendarBooking updateStatus(
			long userId, CalendarBooking calendarBooking, int status,
			ServiceContext serviceContext)
		throws PortalException {

		// Calendar booking

		User user = _userLocalService.getUser(userId);
		Date date = new Date();

		Date oldModifiedDate = calendarBooking.getModifiedDate();
		int oldStatus = calendarBooking.getStatus();

		calendarBooking.setModifiedDate(serviceContext.getModifiedDate(date));
		calendarBooking.setStatus(status);
		calendarBooking.setStatusByUserId(user.getUserId());
		calendarBooking.setStatusByUserName(user.getFullName());
		calendarBooking.setStatusDate(serviceContext.getModifiedDate(date));

		calendarBooking = calendarBookingPersistence.update(calendarBooking);

		// Child calendar bookings

		List<CalendarBooking> childCalendarBookings =
			calendarBooking.getChildCalendarBookings();

		for (CalendarBooking childCalendarBooking : childCalendarBookings) {
			if (childCalendarBooking.equals(calendarBooking)) {
				continue;
			}

			int newStatus = _getNewChildStatus(
				status, oldStatus, childCalendarBooking.getStatus(),
				isStagingCalendarBooking(calendarBooking));

			if (newStatus == childCalendarBooking.getStatus()) {
				continue;
			}

			updateStatus(
				userId, childCalendarBooking, newStatus, serviceContext);
		}

		// Asset

		if (status == WorkflowConstants.STATUS_APPROVED) {
			_assetEntryLocalService.updateVisible(
				CalendarBooking.class.getName(),
				calendarBooking.getCalendarBookingId(), true);
		}
		else if (status == WorkflowConstants.STATUS_IN_TRASH) {
			_assetEntryLocalService.updateVisible(
				CalendarBooking.class.getName(),
				calendarBooking.getCalendarBookingId(), false);
		}

		// Trash

		if (oldStatus == WorkflowConstants.STATUS_IN_TRASH) {
			trashEntryLocalService.deleteEntry(
				CalendarBooking.class.getName(),
				calendarBooking.getCalendarBookingId());
		}

		if ((status == WorkflowConstants.STATUS_IN_TRASH) &&
			calendarBooking.isMasterRecurringBooking()) {

			if (calendarBooking.isMasterBooking()) {
				trashEntryLocalService.addTrashEntry(
					userId, calendarBooking.getGroupId(),
					CalendarBooking.class.getName(),
					calendarBooking.getCalendarBookingId(),
					calendarBooking.getUuid(), null, oldStatus, null, null);
			}
			else {
				trashEntryLocalService.addTrashEntry(
					userId, calendarBooking.getGroupId(),
					CalendarBooking.class.getName(),
					calendarBooking.getCalendarBookingId(),
					calendarBooking.getUuid(), null,
					WorkflowConstants.STATUS_PENDING, null, null);
			}
		}

		if (calendarBooking.isMasterBooking()) {
			Date createDate = calendarBooking.getCreateDate();

			NotificationTemplateType notificationTemplateType =
				NotificationTemplateType.INVITE;

			if (!DateUtil.equals(createDate, oldModifiedDate)) {
				notificationTemplateType = NotificationTemplateType.UPDATE;
			}

			for (CalendarBooking childCalendarBooking : childCalendarBookings) {
				if (childCalendarBooking.equals(calendarBooking)) {
					continue;
				}

				if (DateUtil.equals(
						childCalendarBooking.getCreateDate(),
						calendarBooking.getModifiedDate()) ||
					DateUtil.equals(
						childCalendarBooking.getCreateDate(),
						oldModifiedDate)) {

					notificationTemplateType = NotificationTemplateType.INVITE;
				}

				if (childCalendarBooking.isDenied()) {
					notificationTemplateType = NotificationTemplateType.DECLINE;
				}

				if (calendarBooking.isApproved()) {
					_sendNotification(
						childCalendarBooking, notificationTemplateType,
						serviceContext);
				}
				else if ((oldStatus == WorkflowConstants.STATUS_APPROVED) &&
						 (status == WorkflowConstants.STATUS_IN_TRASH)) {

					notificationTemplateType =
						NotificationTemplateType.MOVED_TO_TRASH;

					_sendNotification(
						childCalendarBooking, notificationTemplateType,
						serviceContext);
				}
			}
		}

		return calendarBooking;
	}

	@Override
	public CalendarBooking updateStatus(
			long userId, long calendarBookingId, int status,
			ServiceContext serviceContext)
		throws PortalException {

		CalendarBooking calendarBooking =
			calendarBookingPersistence.findByPrimaryKey(calendarBookingId);

		return calendarBookingLocalService.updateStatus(
			userId, calendarBooking, status, serviceContext);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, NotificationSender.class, "notification.type");
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();

		_serviceTrackerMap.close();
	}

	@Reference
	protected MBMessageLocalService mbMessageLocalService;

	@Reference
	protected SubscriptionLocalService subscriptionLocalService;

	@Reference
	protected SystemEventLocalService systemEventLocalService;

	@Reference
	protected TrashEntryLocalService trashEntryLocalService;

	private void _addAssetEntry(
		CalendarBooking calendarBooking, ServiceContext serviceContext) {

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			CalendarBooking.class.getName(),
			calendarBooking.getCalendarBookingId());

		if (assetEntry != null) {
			serviceContext.setAssetCategoryIds(assetEntry.getCategoryIds());
			serviceContext.setAssetLinkEntryIds(
				ListUtil.toLongArray(
					_assetLinkLocalService.getDirectLinks(
						assetEntry.getEntryId()),
					AssetLink.ENTRY_ID2_ACCESSOR));
			serviceContext.setAssetPriority(assetEntry.getPriority());
			serviceContext.setAssetTagNames(assetEntry.getTagNames());
		}

		ExpandoBridge expandoBridge = calendarBooking.getExpandoBridge();

		if (expandoBridge != null) {
			serviceContext.setExpandoBridgeAttributes(
				expandoBridge.getAttributes());
		}
	}

	private void _addChildCalendarBookings(
			CalendarBooking calendarBooking, long[] childCalendarIds,
			ServiceContext serviceContext)
		throws PortalException {

		if (!calendarBooking.isMasterBooking()) {
			return;
		}

		long recurringCalendarBookingId =
			CalendarBookingConstants.RECURRING_CALENDAR_BOOKING_ID_DEFAULT;

		Map<Long, CalendarBooking> childCalendarBookingMap = new HashMap<>();

		List<CalendarBooking> childCalendarBookings =
			calendarBookingPersistence.findByParentCalendarBookingId(
				calendarBooking.getCalendarBookingId());

		for (CalendarBooking childCalendarBooking : childCalendarBookings) {
			if (childCalendarBooking.isMasterBooking() ||
				(childCalendarBooking.isDenied() &&
				 ArrayUtil.contains(
					 childCalendarIds, childCalendarBooking.getCalendarId()))) {

				continue;
			}

			deleteCalendarBooking(childCalendarBooking.getCalendarBookingId());

			childCalendarBookingMap.put(
				childCalendarBooking.getCalendarId(), childCalendarBooking);
		}

		for (long calendarId : childCalendarIds) {
			try {
				calendarId = _getNotLiveCalendarId(calendarId);
			}
			catch (NoSuchCalendarException noSuchCalendarException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchCalendarException);
				}

				continue;
			}

			int count = calendarBookingPersistence.countByC_P(
				calendarId, calendarBooking.getCalendarBookingId());

			if (count > 0) {
				continue;
			}

			long firstReminder = calendarBooking.getFirstReminder();
			String firstReminderType = calendarBooking.getFirstReminderType();
			long secondReminder = calendarBooking.getSecondReminder();
			String secondReminderType = calendarBooking.getSecondReminderType();

			if (childCalendarBookingMap.containsKey(calendarId)) {
				CalendarBooking oldChildCalendarBooking =
					childCalendarBookingMap.get(calendarId);

				firstReminder = oldChildCalendarBooking.getFirstReminder();
				firstReminderType =
					oldChildCalendarBooking.getFirstReminderType();
				secondReminder = oldChildCalendarBooking.getSecondReminder();
				secondReminderType =
					oldChildCalendarBooking.getSecondReminderType();
			}

			if (!calendarBooking.isMasterRecurringBooking()) {
				CalendarBooking childMasterRecurringBooking =
					calendarBookingPersistence.fetchByC_P(
						calendarId,
						calendarBooking.getRecurringCalendarBookingId());

				if (childMasterRecurringBooking == null) {
					childMasterRecurringBooking = childCalendarBookingMap.get(
						calendarId);
				}

				if (childMasterRecurringBooking != null) {
					recurringCalendarBookingId =
						childMasterRecurringBooking.getCalendarBookingId();
				}
			}

			serviceContext.setAttribute("sendNotification", Boolean.FALSE);

			CalendarBooking childCalendarBooking = addCalendarBooking(
				calendarBooking.getUserId(), calendarId, new long[0],
				calendarBooking.getCalendarBookingId(),
				recurringCalendarBookingId, calendarBooking.getTitleMap(),
				calendarBooking.getDescriptionMap(),
				calendarBooking.getLocation(), calendarBooking.getStartTime(),
				calendarBooking.getEndTime(), calendarBooking.isAllDay(),
				calendarBooking.getRecurrence(), firstReminder,
				firstReminderType, secondReminder, secondReminderType,
				serviceContext);

			serviceContext.setAttribute("sendNotification", Boolean.TRUE);

			if (childCalendarBookingMap.containsKey(calendarId)) {
				CalendarBooking oldChildCalendarBooking =
					childCalendarBookingMap.get(calendarId);
				int workflowAction = GetterUtil.getInteger(
					serviceContext.getAttribute("workflowAction"));

				if ((calendarBooking.getStartTime() ==
						oldChildCalendarBooking.getStartTime()) &&
					(calendarBooking.getEndTime() ==
						oldChildCalendarBooking.getEndTime()) &&
					(workflowAction != WorkflowConstants.ACTION_SAVE_DRAFT)) {

					updateStatus(
						childCalendarBooking.getUserId(), childCalendarBooking,
						oldChildCalendarBooking.getStatus(), serviceContext);
				}
			}
		}
	}

	private Group _getCalendarResourceSiteGroup(
			CalendarResource calendarResource)
		throws PortalException {

		if (calendarResource.isGroup()) {
			return _groupLocalService.getGroup(calendarResource.getClassPK());
		}
		else if (_isCustomCalendarResource(calendarResource)) {
			return _groupLocalService.getGroup(calendarResource.getGroupId());
		}

		return null;
	}

	private User _getDefaultSenderUser(Calendar calendar) throws Exception {
		CalendarResource calendarResource = calendar.getCalendarResource();

		User user = _userLocalService.getUser(calendarResource.getUserId());

		if (calendarResource.isGroup()) {
			Group group = _groupLocalService.getGroup(
				calendarResource.getClassPK());

			user = _userLocalService.getUser(group.getCreatorUserId());
		}
		else if (calendarResource.isUser()) {
			user = _userLocalService.getUser(calendarResource.getClassPK());
		}

		return user;
	}

	private String _getExtraDataJSON(CalendarBooking calendarBooking) {
		JSONObject jsonObject = JSONUtil.put(
			"title", calendarBooking.getTitle());

		return jsonObject.toString();
	}

	private int _getNewChildStatus(
		int newParentStatus, int oldParentStatus, int oldChildStatus,
		boolean parentStaged) {

		if (newParentStatus == WorkflowConstants.STATUS_IN_TRASH) {
			return WorkflowConstants.STATUS_IN_TRASH;
		}

		if (oldParentStatus == WorkflowConstants.STATUS_IN_TRASH) {
			return WorkflowConstants.STATUS_PENDING;
		}

		if ((newParentStatus == WorkflowConstants.STATUS_DENIED) ||
			(newParentStatus ==
				CalendarBookingWorkflowConstants.STATUS_MAYBE)) {

			return oldChildStatus;
		}

		if (newParentStatus != WorkflowConstants.STATUS_APPROVED) {
			return CalendarBookingWorkflowConstants.STATUS_MASTER_PENDING;
		}

		if (oldChildStatus !=
				CalendarBookingWorkflowConstants.STATUS_MASTER_PENDING) {

			return oldChildStatus;
		}

		if (parentStaged) {
			return CalendarBookingWorkflowConstants.STATUS_MASTER_STAGING;
		}

		return WorkflowConstants.STATUS_PENDING;
	}

	private List<NotificationRecipient> _getNotificationRecipients(
			CalendarBooking calendarBooking)
		throws Exception {

		CalendarResource calendarResource =
			calendarBooking.getCalendarResource();

		Set<User> users = new HashSet<>();

		if (calendarBooking.isMasterBooking()) {
			users.add(_userLocalService.fetchUser(calendarBooking.getUserId()));
		}

		users.add(_userLocalService.fetchUser(calendarResource.getUserId()));

		return TransformUtil.transform(
			users,
			user -> {
				if (user == null) {
					return null;
				}

				if (user.isActive()) {
					return new NotificationRecipient(user);
				}

				if (_log.isDebugEnabled()) {
					_log.debug("Skip inactive user " + user.getUserId());
				}

				return null;
			});
	}

	private Calendar _getNotLiveCalendar(Calendar calendar)
		throws PortalException {

		CalendarResource calendarResource = calendar.getCalendarResource();

		if (_isCalendarResourceStaged(calendarResource)) {
			Group group = _getCalendarResourceSiteGroup(calendarResource);

			Group stagingGroup = group.getStagingGroup();

			calendar = _calendarPersistence.findByUUID_G(
				calendar.getUuid(), stagingGroup.getGroupId());
		}

		return calendar;
	}

	private long _getNotLiveCalendarId(long calendarId) throws PortalException {
		Calendar calendar = _calendarPersistence.findByPrimaryKey(calendarId);

		calendar = _getNotLiveCalendar(calendar);

		return calendar.getCalendarId();
	}

	private List<CalendarBooking> _getOverlappingCalendarBookings(
		long calendarId, long startTime, long endTime, int[] statuses) {

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CalendarBooking.class, getClassLoader());

		Property property = PropertyFactoryUtil.forName("calendarId");

		dynamicQuery.add(property.eq(calendarId));

		Property endTimeProperty = PropertyFactoryUtil.forName("endTime");

		Property startTimeProperty = PropertyFactoryUtil.forName("startTime");

		Criterion intervalCriterion = RestrictionsFactoryUtil.and(
			startTimeProperty.lt(endTime), endTimeProperty.gt(startTime));

		dynamicQuery.add(intervalCriterion);

		Property statusProperty = PropertyFactoryUtil.forName("status");

		dynamicQuery.add(statusProperty.in(statuses));

		return dynamicQuery(dynamicQuery);
	}

	private TimeZone _getTimeZone(Calendar calendar, boolean allDay) {
		if (allDay) {
			return TimeZoneUtil.getTimeZone(StringPool.UTC);
		}

		return calendar.getTimeZone();
	}

	private List<String> _getUnmodifiedAttributesNames(
		CalendarBooking calendarBooking, long calendarId,
		Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
		String location, long startTime, long endTime, boolean allDay,
		long firstReminder, String firstReminderType, long secondReminder,
		String secondReminderType) {

		List<String> unmodifiedAttributesNames = new ArrayList<>();

		if (calendarId == calendarBooking.getCalendarId()) {
			unmodifiedAttributesNames.add("calendarId");
		}

		Map<Locale, String> updatedTitleMap = new HashMap<>();

		for (Map.Entry<Locale, String> titleMapEntry : titleMap.entrySet()) {
			if (titleMapEntry.getValue() != null) {
				updatedTitleMap.put(
					titleMapEntry.getKey(), titleMapEntry.getValue());
			}
		}

		if (Objects.equals(updatedTitleMap, calendarBooking.getTitleMap())) {
			unmodifiedAttributesNames.add("titleMap");
		}

		Map<Locale, String> updatedDescriptionMap = new HashMap<>();

		for (Map.Entry<Locale, String> descriptionMapEntry :
				descriptionMap.entrySet()) {

			if (descriptionMapEntry.getValue() != null) {
				updatedDescriptionMap.put(
					descriptionMapEntry.getKey(),
					descriptionMapEntry.getValue());
			}
		}

		if (Objects.equals(
				updatedDescriptionMap, calendarBooking.getDescriptionMap())) {

			unmodifiedAttributesNames.add("descriptionMap");
		}

		if (Objects.equals(location, calendarBooking.getLocation())) {
			unmodifiedAttributesNames.add("location");
		}

		long newStartTime = JCalendarUtil.convertTimeToNewDay(
			calendarBooking.getStartTime(), startTime);

		long newEndTime = JCalendarUtil.convertTimeToNewDay(
			calendarBooking.getEndTime(), endTime);

		if ((startTime == newStartTime) && (endTime == newEndTime)) {
			unmodifiedAttributesNames.add("time");
		}

		if (allDay == calendarBooking.isAllDay()) {
			unmodifiedAttributesNames.add("allDay");
		}

		if (firstReminder == calendarBooking.getFirstReminder()) {
			unmodifiedAttributesNames.add("firstReminder");
		}

		if (Objects.equals(
				firstReminderType, calendarBooking.getFirstReminderType())) {

			unmodifiedAttributesNames.add("firstReminderType");
		}

		if (secondReminder == calendarBooking.getSecondReminder()) {
			unmodifiedAttributesNames.add("secondReminder");
		}

		if (Objects.equals(
				secondReminderType, calendarBooking.getSecondReminderType())) {

			unmodifiedAttributesNames.add("secondReminderType");
		}

		return unmodifiedAttributesNames;
	}

	private boolean _hasFollowingInstances(
		CalendarBooking calendarBooking, long startTime) {

		boolean followingInstances = false;

		int instanceIndex = RecurrenceUtil.getIndexOfInstance(
			calendarBooking.getRecurrence(), calendarBooking.getStartTime(),
			startTime);

		CalendarBooking calendarBookingInstance =
			RecurrenceUtil.getCalendarBookingInstance(
				calendarBooking, instanceIndex + 1);

		if (calendarBookingInstance != null) {
			followingInstances = true;
		}

		return followingInstances;
	}

	private boolean _isCalendarResourceStaged(CalendarResource calendarResource)
		throws PortalException {

		Group group = _getCalendarResourceSiteGroup(calendarResource);

		if (group == null) {
			return false;
		}

		Group stagingGroup = group.getStagingGroup();

		if (stagingGroup == null) {
			return false;
		}

		return stagingGroup.isInStagingPortlet(
			CalendarPortletKeys.CALENDAR_ADMIN);
	}

	private boolean _isCustomCalendarResource(
		CalendarResource calendarResource) {

		long calendarResourceClassNameId =
			_classNameLocalService.getClassNameId(CalendarResource.class);

		if (calendarResource.getClassNameId() == calendarResourceClassNameId) {
			return true;
		}

		return false;
	}

	private boolean _isInCheckInterval(long deltaTime, long intervalStart) {
		long intervalEnd = intervalStart + _CHECK_INTERVAL;

		if ((intervalStart > 0) && (intervalStart <= deltaTime) &&
			(deltaTime < intervalEnd)) {

			return true;
		}

		return false;
	}

	private boolean _isMasterPending(CalendarBooking calendarBooking)
		throws PortalException {

		if (calendarBooking.isMasterBooking()) {
			return false;
		}

		CalendarBooking parentCalendarBooking =
			calendarBooking.getParentCalendarBooking();

		if (parentCalendarBooking.isPending() ||
			parentCalendarBooking.isDraft()) {

			return true;
		}

		return false;
	}

	private void _notifyCalendarBookingRecipients(
			CalendarBooking calendarBooking, NotificationType notificationType,
			NotificationTemplateType notificationTemplateType, User senderUser,
			ServiceContext serviceContext)
		throws Exception {

		NotificationSender notificationSender = _serviceTrackerMap.getService(
			notificationType.toString());

		if (notificationTemplateType == NotificationTemplateType.DECLINE) {
			User recipientUser = senderUser;

			Calendar calendar = calendarBooking.getCalendar();

			senderUser = _getDefaultSenderUser(calendar);

			String resourceName = calendar.getName(
				recipientUser.getLanguageId());

			NotificationRecipient notificationRecipient =
				new NotificationRecipient(recipientUser);

			NotificationTemplateContext notificationTemplateContext =
				NotificationTemplateContextFactory.getInstance(
					notificationType, notificationTemplateType, calendarBooking,
					recipientUser, serviceContext);

			notificationSender.sendNotification(
				senderUser.getEmailAddress(), resourceName,
				notificationRecipient, notificationTemplateContext);
		}
		else {
			List<NotificationRecipient> notificationRecipients =
				_getNotificationRecipients(calendarBooking);

			for (NotificationRecipient notificationRecipient :
					notificationRecipients) {

				User user = notificationRecipient.getUser();

				if (user.equals(senderUser)) {
					continue;
				}

				NotificationTemplateContext notificationTemplateContext =
					NotificationTemplateContextFactory.getInstance(
						notificationType, notificationTemplateType,
						calendarBooking, user, serviceContext);

				notificationSender.sendNotification(
					senderUser.getEmailAddress(), senderUser.getFullName(),
					notificationRecipient, notificationTemplateContext);
			}
		}
	}

	private void _notifyCalendarBookingReminders(
			CalendarBooking calendarBooking, long nowTime)
		throws Exception {

		List<NotificationRecipient> notificationRecipients =
			_getNotificationRecipients(calendarBooking);

		for (NotificationRecipient notificationRecipient :
				notificationRecipients) {

			long startTime = calendarBooking.getStartTime();

			if (nowTime > startTime) {
				return;
			}

			NotificationType notificationType = null;

			long deltaTime = startTime - nowTime;

			if (_isInCheckInterval(
					deltaTime, calendarBooking.getFirstReminder())) {

				notificationType =
					calendarBooking.getFirstReminderNotificationType();
			}
			else if (_isInCheckInterval(
						deltaTime, calendarBooking.getSecondReminder())) {

				notificationType =
					calendarBooking.getSecondReminderNotificationType();
			}

			if (notificationType == null) {
				continue;
			}

			User user = notificationRecipient.getUser();

			NotificationSender notificationSender =
				_serviceTrackerMap.getService(notificationType.toString());

			NotificationTemplateContext notificationTemplateContext =
				NotificationTemplateContextFactory.getInstance(
					calendarBooking, NotificationTemplateType.REMINDER,
					notificationType, null, null, user);

			notificationSender.sendNotification(
				user.getEmailAddress(), user.getFullName(),
				notificationRecipient, notificationTemplateContext);
		}
	}

	private String _sanitize(String htmlEntry) {
		if ((htmlEntry == null) || htmlEntry.isEmpty()) {
			return htmlEntry;
		}

		StringBuffer sb = new StringBuffer();

		Matcher matcher = _htmlTagWithOnAttributePattern.matcher(htmlEntry);

		while (matcher.find()) {
			matcher.appendReplacement(
				sb,
				matcher.group(
				).replaceAll(
					_onAttributePattern.pattern(), ""
				));
		}

		matcher.appendTail(sb);

		String string = sb.toString();

		return string.replaceAll(
			_alertPattern.pattern(), ""
		).replaceAll(
			_innerHtmlPattern.pattern(), ""
		).replaceAll(
			_phpCodePattern.pattern(), ""
		).replaceAll(
			_aspCodePattern.pattern(), ""
		).replaceAll(
			_aspNetCodePattern.pattern(), ""
		);
	}

	private void _sendChildrenNotifications(
		CalendarBooking calendarBooking,
		NotificationTemplateType notificationTemplateType,
		ServiceContext serviceContext) {

		List<CalendarBooking> childCalendarBookings =
			calendarBooking.getChildCalendarBookings();

		for (CalendarBooking childCalendarBooking : childCalendarBookings) {
			if (childCalendarBooking.equals(calendarBooking)) {
				continue;
			}

			_sendNotification(
				childCalendarBooking, notificationTemplateType, serviceContext);
		}
	}

	private void _sendNotification(
		CalendarBooking calendarBooking,
		NotificationTemplateType notificationTemplateType,
		ServiceContext serviceContext) {

		boolean sendNotification = ParamUtil.getBoolean(
			serviceContext, "sendNotification", true);

		try {
			if (!sendNotification ||
				isStagingCalendarBooking(calendarBooking)) {

				return;
			}

			User sender = _userLocalService.fetchUser(
				serviceContext.getUserId());

			NotificationType notificationType =
				CalendarServiceConfigurationValues.
					CALENDAR_NOTIFICATION_DEFAULT_TYPE;

			_notifyCalendarBookingRecipients(
				calendarBooking, notificationType, notificationTemplateType,
				sender, serviceContext);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	private CalendarBooking _splitCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, long startTime,
			Recurrence recurrence)
		throws PortalException {

		long[] childCalendarIds = getChildCalendarIds(
			calendarBooking.getCalendarBookingId(),
			calendarBooking.getCalendarId());

		long duration =
			calendarBooking.getEndTime() - calendarBooking.getStartTime();

		long endTime = startTime + duration;

		CalendarBooking calendarBookingInstance = addCalendarBooking(
			calendarBooking.getUserId(), calendarBooking.getCalendarId(),
			childCalendarIds,
			CalendarBookingConstants.PARENT_CALENDAR_BOOKING_ID_DEFAULT,
			calendarBooking.getRecurringCalendarBookingId(),
			calendarBooking.getTitleMap(), calendarBooking.getDescriptionMap(),
			calendarBooking.getLocation(), startTime, endTime,
			calendarBooking.isAllDay(),
			RecurrenceSerializer.serialize(recurrence),
			calendarBooking.getFirstReminder(),
			calendarBooking.getFirstReminderType(),
			calendarBooking.getSecondReminder(),
			calendarBooking.getSecondReminderType(),
			ServiceContextThreadLocal.getServiceContext());

		deleteCalendarBookingInstance(
			userId, calendarBooking, startTime, true, false);

		return calendarBookingInstance;
	}

	private List<CalendarBooking> _splitCalendarBookingInstances(
			long userId, CalendarBooking calendarBooking, long startTime)
		throws PortalException {

		return TransformUtil.transform(
			getRecurringCalendarBookings(calendarBooking),
			recurringCalendarBooking -> {
				if (recurringCalendarBooking.getStartTime() > startTime) {
					return recurringCalendarBooking;
				}

				boolean singleInstance = false;
				java.util.Calendar splitJCalendar = null;

				if (Validator.isNull(calendarBooking.getRecurrence())) {
					singleInstance = true;

					splitJCalendar = JCalendarUtil.getJCalendar(
						calendarBooking.getStartTime(),
						_getTimeZone(
							calendarBooking.getCalendar(),
							calendarBooking.isAllDay()));

					splitJCalendar.add(java.util.Calendar.DATE, 1);
				}

				if (!singleInstance) {
					return null;
				}

				Recurrence recurrenceObj =
					recurringCalendarBooking.getRecurrenceObj();

				if (recurrenceObj == null) {
					return null;
				}

				java.util.Calendar startTimeJCalendar =
					JCalendarUtil.getJCalendar(
						recurringCalendarBooking.getStartTime(),
						recurringCalendarBooking.getTimeZone());

				RecurrenceSplit recurrenceSplit = RecurrenceSplitterUtil.split(
					recurrenceObj, startTimeJCalendar, splitJCalendar);

				if (!recurrenceSplit.isSplit()) {
					return null;
				}

				java.util.Calendar newStartTimeJCalendar =
					JCalendarUtil.mergeJCalendar(
						splitJCalendar, startTimeJCalendar,
						recurringCalendarBooking.getTimeZone());

				return _splitCalendarBookingInstance(
					userId, recurringCalendarBooking,
					newStartTimeJCalendar.getTimeInMillis(),
					recurrenceSplit.getSecondRecurrence());
			});
	}

	private void _updateCalendarBookingsByChanges(
			long userId, long calendarId, long[] childCalendarIds,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			long firstReminder, String firstReminderType, long secondReminder,
			String secondReminderType, ServiceContext serviceContext,
			List<CalendarBooking> calendarBookings,
			List<String> unmodifiedAttributeNames)
		throws PortalException {

		for (CalendarBooking calendarBooking : calendarBookings) {
			long calendarBookingId = calendarBooking.getCalendarBookingId();

			if (unmodifiedAttributeNames.contains("calendarId")) {
				calendarId = calendarBooking.getCalendarId();
			}

			if (unmodifiedAttributeNames.contains("titleMap")) {
				titleMap = calendarBooking.getTitleMap();
			}

			if (unmodifiedAttributeNames.contains("descriptionMap")) {
				descriptionMap = calendarBooking.getDescriptionMap();
			}

			if (unmodifiedAttributeNames.contains("location")) {
				location = calendarBooking.getLocation();
			}

			if (unmodifiedAttributeNames.contains("time")) {
				startTime = calendarBooking.getStartTime();
				endTime = calendarBooking.getEndTime();
			}
			else {
				startTime = JCalendarUtil.convertTimeToNewDay(
					startTime, calendarBooking.getStartTime());

				endTime = JCalendarUtil.convertTimeToNewDay(
					endTime, calendarBooking.getEndTime());
			}

			if (unmodifiedAttributeNames.contains("allDay")) {
				allDay = calendarBooking.isAllDay();
			}

			if (unmodifiedAttributeNames.contains("firstReminder")) {
				firstReminder = calendarBooking.getFirstReminder();
			}

			if (unmodifiedAttributeNames.contains("firstReminderType")) {
				firstReminderType = calendarBooking.getFirstReminderType();
			}

			if (unmodifiedAttributeNames.contains("secondReminder")) {
				secondReminder = calendarBooking.getSecondReminder();
			}

			if (unmodifiedAttributeNames.contains("secondReminderType")) {
				secondReminderType = calendarBooking.getSecondReminderType();
			}

			updateCalendarBooking(
				userId, calendarBookingId, calendarId, childCalendarIds,
				titleMap, descriptionMap, location, startTime, endTime, allDay,
				calendarBooking.getRecurrence(), firstReminder,
				firstReminderType, secondReminder, secondReminderType,
				serviceContext);
		}
	}

	private void _updateChildCalendarBookings(
		CalendarBooking calendarBooking, Date modifiedDate, String recurrence) {

		List<CalendarBooking> childCalendarBookings = new ArrayList<>();

		List<CalendarBooking> recurringCalendarBookings = new ArrayList<>();

		if (calendarBooking.isMasterBooking()) {
			childCalendarBookings = getChildCalendarBookings(
				calendarBooking.getCalendarBookingId());
		}
		else {
			childCalendarBookings.add(calendarBooking);
		}

		for (CalendarBooking childCalendarBooking : childCalendarBookings) {
			recurringCalendarBookings.addAll(
				getRecurringCalendarBookings(childCalendarBooking));

			recurringCalendarBookings.remove(childCalendarBooking);

			childCalendarBooking.setModifiedDate(modifiedDate);
			childCalendarBooking.setRecurrence(recurrence);

			calendarBookingPersistence.update(childCalendarBooking);
		}
	}

	private void _updateChildCalendarBookings(
			CalendarBooking calendarBooking, long[] childCalendarIds,
			ServiceContext serviceContext)
		throws PortalException {

		if (!calendarBooking.isMasterBooking()) {
			return;
		}

		long recurringCalendarBookingId =
			CalendarBookingConstants.RECURRING_CALENDAR_BOOKING_ID_DEFAULT;

		Map<Long, CalendarBooking> childCalendarBookingMap = new HashMap<>();

		List<CalendarBooking> childCalendarBookings =
			calendarBookingPersistence.findByParentCalendarBookingId(
				calendarBooking.getCalendarBookingId());

		for (CalendarBooking childCalendarBooking : childCalendarBookings) {
			if (!childCalendarBooking.isMasterBooking() &&
				!ArrayUtil.contains(
					childCalendarIds, childCalendarBooking.getCalendarId())) {

				deleteCalendarBooking(
					childCalendarBooking.getCalendarBookingId());
			}

			if (childCalendarBooking.isDenied()) {
				continue;
			}

			childCalendarBookingMap.put(
				childCalendarBooking.getCalendarId(), childCalendarBooking);
		}

		for (long calendarId : childCalendarIds) {
			try {
				calendarId = _getNotLiveCalendarId(calendarId);
			}
			catch (NoSuchCalendarException noSuchCalendarException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchCalendarException);
				}

				continue;
			}

			CalendarBooking oldChildCalendarBooking =
				childCalendarBookingMap.get(calendarId);

			if ((oldChildCalendarBooking != null) &&
				oldChildCalendarBooking.isMasterBooking()) {

				continue;
			}

			long firstReminder = calendarBooking.getFirstReminder();
			String firstReminderType = calendarBooking.getFirstReminderType();
			long secondReminder = calendarBooking.getSecondReminder();
			String secondReminderType = calendarBooking.getSecondReminderType();

			serviceContext.setAttribute("sendNotification", Boolean.FALSE);

			if (oldChildCalendarBooking != null) {
				firstReminder = oldChildCalendarBooking.getFirstReminder();
				firstReminderType =
					oldChildCalendarBooking.getFirstReminderType();
				secondReminder = oldChildCalendarBooking.getSecondReminder();
				secondReminderType =
					oldChildCalendarBooking.getSecondReminderType();

				CalendarBooking childCalendarBooking = updateCalendarBooking(
					calendarBooking.getUserId(),
					oldChildCalendarBooking.getCalendarBookingId(),
					oldChildCalendarBooking.getCalendarId(), new long[0],
					calendarBooking.getTitleMap(),
					calendarBooking.getDescriptionMap(),
					calendarBooking.getLocation(),
					calendarBooking.getStartTime(),
					calendarBooking.getEndTime(), calendarBooking.isAllDay(),
					calendarBooking.getRecurrence(), firstReminder,
					firstReminderType, secondReminder, secondReminderType,
					serviceContext);

				serviceContext.setAttribute("sendNotification", Boolean.TRUE);

				int workflowAction = GetterUtil.getInteger(
					serviceContext.getAttribute("workflowAction"));

				if ((calendarBooking.getStartTime() ==
						oldChildCalendarBooking.getStartTime()) &&
					(calendarBooking.getEndTime() ==
						oldChildCalendarBooking.getEndTime()) &&
					(workflowAction != WorkflowConstants.ACTION_SAVE_DRAFT)) {

					updateStatus(
						childCalendarBooking.getUserId(), childCalendarBooking,
						oldChildCalendarBooking.getStatus(), serviceContext);
				}
			}
			else {
				if (!calendarBooking.isMasterRecurringBooking()) {
					CalendarBooking childMasterRecurringBooking =
						calendarBookingPersistence.fetchByC_P(
							calendarId,
							calendarBooking.getRecurringCalendarBookingId());

					if (childMasterRecurringBooking == null) {
						childMasterRecurringBooking =
							childCalendarBookingMap.get(calendarId);
					}

					if (childMasterRecurringBooking != null) {
						recurringCalendarBookingId =
							childMasterRecurringBooking.getCalendarBookingId();
					}
				}

				addCalendarBooking(
					calendarBooking.getUserId(), calendarId, new long[0],
					calendarBooking.getCalendarBookingId(),
					recurringCalendarBookingId, calendarBooking.getTitleMap(),
					calendarBooking.getDescriptionMap(),
					calendarBooking.getLocation(),
					calendarBooking.getStartTime(),
					calendarBooking.getEndTime(), calendarBooking.isAllDay(),
					calendarBooking.getRecurrence(), firstReminder,
					firstReminderType, secondReminder, secondReminderType,
					serviceContext);
			}

			serviceContext.setAttribute("sendNotification", Boolean.TRUE);
		}
	}

	private void _validate(
			java.util.Calendar startTimeJCalendar,
			java.util.Calendar endTimeJCalendar, String recurrence)
		throws PortalException {

		if (startTimeJCalendar.after(endTimeJCalendar)) {
			throw new CalendarBookingDurationException();
		}

		if (Validator.isNull(recurrence)) {
			return;
		}

		Recurrence recurrenceObj = RecurrenceSerializer.deserialize(
			recurrence, startTimeJCalendar.getTimeZone());

		if ((recurrenceObj.getUntilJCalendar() != null) &&
			JCalendarUtil.isLaterDay(
				startTimeJCalendar, recurrenceObj.getUntilJCalendar())) {

			throw new CalendarBookingRecurrenceException();
		}
	}

	private static final long _CHECK_INTERVAL =
		CalendarServiceConfigurationValues.
			CALENDAR_NOTIFICATION_CHECK_INTERVAL * Time.MINUTE;

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarBookingLocalServiceImpl.class);

	private static final Pattern _alertPattern = Pattern.compile(
		"alert\\((.*?)\\)", Pattern.CASE_INSENSITIVE);
	private static final Pattern _aspCodePattern = Pattern.compile(
		"<%[\\s\\S]*?%>", Pattern.CASE_INSENSITIVE);
	private static final Pattern _aspNetCodePattern = Pattern.compile(
		"<asp:[^>]+>.*?</asp:[^>]+>", Pattern.CASE_INSENSITIVE);
	private static final Pattern _htmlTagWithOnAttributePattern =
		Pattern.compile(
			"<[^>]+?(\\s+\\bon\\w+=" +
				"(?:'[^']*'|\"[^\"]*\"|[^'\"\\s>]+))*\\s*/?>",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern _innerHtmlPattern = Pattern.compile(
		"innerHTML\\s*=\\s*.*?", Pattern.CASE_INSENSITIVE);
	private static final Pattern _onAttributePattern = Pattern.compile(
		"(\\s+\\bon\\w+=(?:'[^']*'|\"[^\"]*\"|[^'\"\\s>]+))",
		Pattern.CASE_INSENSITIVE);
	private static final Pattern _phpCodePattern = Pattern.compile(
		"<\\?[\\s\\S]*?\\?>", Pattern.CASE_INSENSITIVE);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private CalendarPersistence _calendarPersistence;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private HtmlParser _htmlParser;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	private ServiceTrackerMap<String, NotificationSender> _serviceTrackerMap;

	@Reference
	private SocialActivityCounterLocalService
		_socialActivityCounterLocalService;

	@Reference
	private SocialActivityLocalService _socialActivityLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}