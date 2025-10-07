/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service;

import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for CalendarBooking. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Eduardo Lundgren
 * @see CalendarBookingLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CalendarBookingLocalService
	extends BaseLocalService, CTService<CalendarBooking>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.calendar.service.impl.CalendarBookingLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the calendar booking local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CalendarBookingLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the calendar booking to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CalendarBookingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param calendarBooking the calendar booking
	 * @return the calendar booking that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CalendarBooking addCalendarBooking(CalendarBooking calendarBooking);

	public CalendarBooking addCalendarBooking(
			long userId, long calendarId, long[] childCalendarIds,
			long parentCalendarBookingId, long recurringCalendarBookingId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException;

	public void checkCalendarBookings() throws PortalException;

	/**
	 * Creates a new calendar booking with the primary key. Does not add the calendar booking to the database.
	 *
	 * @param calendarBookingId the primary key for the new calendar booking
	 * @return the new calendar booking
	 */
	@Transactional(enabled = false)
	public CalendarBooking createCalendarBooking(long calendarBookingId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the calendar booking from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CalendarBookingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param calendarBooking the calendar booking
	 * @return the calendar booking that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	public CalendarBooking deleteCalendarBooking(
			CalendarBooking calendarBooking)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public CalendarBooking deleteCalendarBooking(
			CalendarBooking calendarBooking, boolean allRecurringInstances)
		throws PortalException;

	/**
	 * Deletes the calendar booking with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CalendarBookingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param calendarBookingId the primary key of the calendar booking
	 * @return the calendar booking that was removed
	 * @throws PortalException if a calendar booking with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public CalendarBooking deleteCalendarBooking(long calendarBookingId)
		throws PortalException;

	public CalendarBooking deleteCalendarBooking(
			long calendarBookingId, boolean allRecurringInstances)
		throws PortalException;

	public void deleteCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, int instanceIndex,
			boolean allFollowing)
		throws PortalException;

	public void deleteCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, int instanceIndex,
			boolean allFollowing, boolean deleteRecurringCalendarBookings)
		throws PortalException;

	public void deleteCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, long startTime,
			boolean allFollowing)
		throws PortalException;

	public void deleteCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, long startTime,
			boolean allFollowing, boolean deleteRecurringCalendarBookings)
		throws PortalException;

	public void deleteCalendarBookingInstance(
			long userId, long calendarBookingId, long startTime,
			boolean allFollowing)
		throws PortalException;

	public void deleteCalendarBookings(long calendarId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public CalendarBooking deleteRecurringCalendarBooking(
			CalendarBooking calendarBooking)
		throws PortalException;

	public CalendarBooking deleteRecurringCalendarBooking(
			long calendarBookingId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.calendar.model.impl.CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.calendar.model.impl.CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	public String exportCalendarBooking(long calendarBookingId, String type)
		throws Exception;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CalendarBooking fetchCalendarBooking(long calendarBookingId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CalendarBooking fetchCalendarBooking(
		long calendarId, String vEventUid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CalendarBooking fetchCalendarBooking(String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CalendarBooking fetchCalendarBookingByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the calendar booking matching the UUID and group.
	 *
	 * @param uuid the calendar booking's UUID
	 * @param groupId the primary key of the group
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CalendarBooking fetchCalendarBookingByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the calendar booking with the primary key.
	 *
	 * @param calendarBookingId the primary key of the calendar booking
	 * @return the calendar booking
	 * @throws PortalException if a calendar booking with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CalendarBooking getCalendarBooking(long calendarBookingId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CalendarBooking getCalendarBooking(
			long calendarId, long parentCalendarBookingId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CalendarBooking getCalendarBookingByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the calendar booking matching the UUID and group.
	 *
	 * @param uuid the calendar booking's UUID
	 * @param groupId the primary key of the group
	 * @return the matching calendar booking
	 * @throws PortalException if a matching calendar booking could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CalendarBooking getCalendarBookingByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CalendarBooking getCalendarBookingInstance(
			long calendarBookingId, int instanceIndex)
		throws PortalException;

	/**
	 * Returns a range of all the calendar bookings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.calendar.model.impl.CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @return the range of calendar bookings
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> getCalendarBookings(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> getCalendarBookings(long calendarId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> getCalendarBookings(
		long calendarId, int[] statuses);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> getCalendarBookings(
		long calendarId, long startTime, long endTime);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> getCalendarBookings(
		long calendarId, long startTime, long endTime, int max);

	/**
	 * Returns all the calendar bookings matching the UUID and company.
	 *
	 * @param uuid the UUID of the calendar bookings
	 * @param companyId the primary key of the company
	 * @return the matching calendar bookings, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> getCalendarBookingsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of calendar bookings matching the UUID and company.
	 *
	 * @param uuid the UUID of the calendar bookings
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching calendar bookings, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> getCalendarBookingsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator);

	/**
	 * Returns the number of calendar bookings.
	 *
	 * @return the number of calendar bookings
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCalendarBookingsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCalendarBookingsCount(
		long calendarId, long parentCalendarBookingId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> getChildCalendarBookings(
		long calendarBookingId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> getChildCalendarBookings(
		long parentCalendarBookingId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getChildCalendarIds(long calendarBookingId, long calendarId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CalendarBooking getLastInstanceCalendarBooking(
		CalendarBooking calendarBooking);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> getRecurringCalendarBookings(
		CalendarBooking calendarBooking);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> getRecurringCalendarBookings(
		CalendarBooking calendarBooking, long startTime);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasExclusiveCalendarBooking(
			Calendar calendar, long startTime, long endTime)
		throws PortalException;

	public CalendarBooking invokeTransition(
			long userId, CalendarBooking calendarBooking, long startTime,
			int status, boolean updateInstance, boolean allFollowing,
			ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isStagingCalendarBooking(CalendarBooking calendarBooking)
		throws PortalException;

	public CalendarBooking moveCalendarBookingToTrash(
			long userId, CalendarBooking calendarBooking)
		throws PortalException;

	public CalendarBooking moveCalendarBookingToTrash(
			long userId, long calendarBookingId)
		throws PortalException;

	public CalendarBooking restoreCalendarBookingFromTrash(
			long userId, long calendarBookingId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> search(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String keywords, long startTime, long endTime, TimeZone displayTimeZone,
		boolean recurring, int[] statuses, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CalendarBooking> search(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId, String title,
		String description, String location, long startTime, long endTime,
		boolean recurring, int[] statuses, boolean andOperator, int start,
		int end, OrderByComparator<CalendarBooking> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String keywords, long startTime, long endTime, int[] statuses);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId, String title,
		String description, String location, long startTime, long endTime,
		int[] statuses, boolean andOperator);

	public void updateAsset(
			long userId, CalendarBooking calendarBooking,
			long[] assetCategoryIds, String[] assetTagNames,
			long[] assetLinkEntryIds, Double priority)
		throws PortalException;

	/**
	 * Updates the calendar booking in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CalendarBookingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param calendarBooking the calendar booking
	 * @return the calendar booking that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public CalendarBooking updateCalendarBooking(
		CalendarBooking calendarBooking);

	public CalendarBooking updateCalendarBooking(
			long userId, long calendarBookingId, long calendarId,
			long[] childCalendarIds, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String location, long startTime,
			long endTime, boolean allDay, String recurrence, long firstReminder,
			String firstReminderType, long secondReminder,
			String secondReminderType, ServiceContext serviceContext)
		throws PortalException;

	public CalendarBooking updateCalendarBooking(
			long userId, long calendarBookingId, long calendarId,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException;

	public CalendarBooking updateCalendarBookingInstance(
			long userId, long calendarBookingId, int instanceIndex,
			long calendarId, long[] childCalendarIds,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			boolean allFollowing, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException;

	public CalendarBooking updateCalendarBookingInstance(
			long userId, long calendarBookingId, int instanceIndex,
			long calendarId, long[] childCalendarIds,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, boolean allFollowing, long firstReminder,
			String firstReminderType, long secondReminder,
			String secondReminderType, ServiceContext serviceContext)
		throws PortalException;

	public CalendarBooking updateCalendarBookingInstance(
			long userId, long calendarBookingId, int instanceIndex,
			long calendarId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String location, long startTime,
			long endTime, boolean allDay, String recurrence,
			boolean allFollowing, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			ServiceContext serviceContext)
		throws PortalException;

	public void updateLastInstanceCalendarBookingRecurrence(
		CalendarBooking calendarBooking, String recurrence);

	public CalendarBooking updateRecurringCalendarBooking(
			long userId, long calendarBookingId, long calendarId,
			long[] childCalendarIds, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String location, long startTime,
			long endTime, boolean allDay, long firstReminder,
			String firstReminderType, long secondReminder,
			String secondReminderType, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public CalendarBooking updateStatus(
			long userId, CalendarBooking calendarBooking, int status,
			ServiceContext serviceContext)
		throws PortalException;

	public CalendarBooking updateStatus(
			long userId, long calendarBookingId, int status,
			ServiceContext serviceContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<CalendarBooking> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<CalendarBooking> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CalendarBooking>, R, E>
				updateUnsafeFunction)
		throws E;

}