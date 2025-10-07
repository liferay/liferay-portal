/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service;

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CalendarBookingLocalService}.
 *
 * @author Eduardo Lundgren
 * @see CalendarBookingLocalService
 * @generated
 */
public class CalendarBookingLocalServiceWrapper
	implements CalendarBookingLocalService,
			   ServiceWrapper<CalendarBookingLocalService> {

	public CalendarBookingLocalServiceWrapper() {
		this(null);
	}

	public CalendarBookingLocalServiceWrapper(
		CalendarBookingLocalService calendarBookingLocalService) {

		_calendarBookingLocalService = calendarBookingLocalService;
	}

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
	@Override
	public CalendarBooking addCalendarBooking(CalendarBooking calendarBooking) {
		return _calendarBookingLocalService.addCalendarBooking(calendarBooking);
	}

	@Override
	public CalendarBooking addCalendarBooking(
			long userId, long calendarId, long[] childCalendarIds,
			long parentCalendarBookingId, long recurringCalendarBookingId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.addCalendarBooking(
			userId, calendarId, childCalendarIds, parentCalendarBookingId,
			recurringCalendarBookingId, titleMap, descriptionMap, location,
			startTime, endTime, allDay, recurrence, firstReminder,
			firstReminderType, secondReminder, secondReminderType,
			serviceContext);
	}

	@Override
	public void checkCalendarBookings()
		throws com.liferay.portal.kernel.exception.PortalException {

		_calendarBookingLocalService.checkCalendarBookings();
	}

	/**
	 * Creates a new calendar booking with the primary key. Does not add the calendar booking to the database.
	 *
	 * @param calendarBookingId the primary key for the new calendar booking
	 * @return the new calendar booking
	 */
	@Override
	public CalendarBooking createCalendarBooking(long calendarBookingId) {
		return _calendarBookingLocalService.createCalendarBooking(
			calendarBookingId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.createPersistedModel(primaryKeyObj);
	}

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
	@Override
	public CalendarBooking deleteCalendarBooking(
			CalendarBooking calendarBooking)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.deleteCalendarBooking(
			calendarBooking);
	}

	@Override
	public CalendarBooking deleteCalendarBooking(
			CalendarBooking calendarBooking, boolean allRecurringInstances)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.deleteCalendarBooking(
			calendarBooking, allRecurringInstances);
	}

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
	@Override
	public CalendarBooking deleteCalendarBooking(long calendarBookingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.deleteCalendarBooking(
			calendarBookingId);
	}

	@Override
	public CalendarBooking deleteCalendarBooking(
			long calendarBookingId, boolean allRecurringInstances)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.deleteCalendarBooking(
			calendarBookingId, allRecurringInstances);
	}

	@Override
	public void deleteCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, int instanceIndex,
			boolean allFollowing)
		throws com.liferay.portal.kernel.exception.PortalException {

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			userId, calendarBooking, instanceIndex, allFollowing);
	}

	@Override
	public void deleteCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, int instanceIndex,
			boolean allFollowing, boolean deleteRecurringCalendarBookings)
		throws com.liferay.portal.kernel.exception.PortalException {

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			userId, calendarBooking, instanceIndex, allFollowing,
			deleteRecurringCalendarBookings);
	}

	@Override
	public void deleteCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, long startTime,
			boolean allFollowing)
		throws com.liferay.portal.kernel.exception.PortalException {

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			userId, calendarBooking, startTime, allFollowing);
	}

	@Override
	public void deleteCalendarBookingInstance(
			long userId, CalendarBooking calendarBooking, long startTime,
			boolean allFollowing, boolean deleteRecurringCalendarBookings)
		throws com.liferay.portal.kernel.exception.PortalException {

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			userId, calendarBooking, startTime, allFollowing,
			deleteRecurringCalendarBookings);
	}

	@Override
	public void deleteCalendarBookingInstance(
			long userId, long calendarBookingId, long startTime,
			boolean allFollowing)
		throws com.liferay.portal.kernel.exception.PortalException {

		_calendarBookingLocalService.deleteCalendarBookingInstance(
			userId, calendarBookingId, startTime, allFollowing);
	}

	@Override
	public void deleteCalendarBookings(long calendarId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_calendarBookingLocalService.deleteCalendarBookings(calendarId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public CalendarBooking deleteRecurringCalendarBooking(
			CalendarBooking calendarBooking)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.deleteRecurringCalendarBooking(
			calendarBooking);
	}

	@Override
	public CalendarBooking deleteRecurringCalendarBooking(
			long calendarBookingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.deleteRecurringCalendarBooking(
			calendarBookingId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _calendarBookingLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _calendarBookingLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _calendarBookingLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _calendarBookingLocalService.dynamicQuery(dynamicQuery);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _calendarBookingLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _calendarBookingLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _calendarBookingLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _calendarBookingLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public String exportCalendarBooking(long calendarBookingId, String type)
		throws Exception {

		return _calendarBookingLocalService.exportCalendarBooking(
			calendarBookingId, type);
	}

	@Override
	public CalendarBooking fetchCalendarBooking(long calendarBookingId) {
		return _calendarBookingLocalService.fetchCalendarBooking(
			calendarBookingId);
	}

	@Override
	public CalendarBooking fetchCalendarBooking(
		long calendarId, String vEventUid) {

		return _calendarBookingLocalService.fetchCalendarBooking(
			calendarId, vEventUid);
	}

	@Override
	public CalendarBooking fetchCalendarBooking(String uuid, long groupId) {
		return _calendarBookingLocalService.fetchCalendarBooking(uuid, groupId);
	}

	@Override
	public CalendarBooking fetchCalendarBookingByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _calendarBookingLocalService.
			fetchCalendarBookingByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the calendar booking matching the UUID and group.
	 *
	 * @param uuid the calendar booking's UUID
	 * @param groupId the primary key of the group
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchCalendarBookingByUuidAndGroupId(
		String uuid, long groupId) {

		return _calendarBookingLocalService.
			fetchCalendarBookingByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _calendarBookingLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the calendar booking with the primary key.
	 *
	 * @param calendarBookingId the primary key of the calendar booking
	 * @return the calendar booking
	 * @throws PortalException if a calendar booking with the primary key could not be found
	 */
	@Override
	public CalendarBooking getCalendarBooking(long calendarBookingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.getCalendarBooking(
			calendarBookingId);
	}

	@Override
	public CalendarBooking getCalendarBooking(
			long calendarId, long parentCalendarBookingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.getCalendarBooking(
			calendarId, parentCalendarBookingId);
	}

	@Override
	public CalendarBooking getCalendarBookingByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.
			getCalendarBookingByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the calendar booking matching the UUID and group.
	 *
	 * @param uuid the calendar booking's UUID
	 * @param groupId the primary key of the group
	 * @return the matching calendar booking
	 * @throws PortalException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking getCalendarBookingByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.getCalendarBookingByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public CalendarBooking getCalendarBookingInstance(
			long calendarBookingId, int instanceIndex)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.getCalendarBookingInstance(
			calendarBookingId, instanceIndex);
	}

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
	@Override
	public java.util.List<CalendarBooking> getCalendarBookings(
		int start, int end) {

		return _calendarBookingLocalService.getCalendarBookings(start, end);
	}

	@Override
	public java.util.List<CalendarBooking> getCalendarBookings(
		long calendarId) {

		return _calendarBookingLocalService.getCalendarBookings(calendarId);
	}

	@Override
	public java.util.List<CalendarBooking> getCalendarBookings(
		long calendarId, int[] statuses) {

		return _calendarBookingLocalService.getCalendarBookings(
			calendarId, statuses);
	}

	@Override
	public java.util.List<CalendarBooking> getCalendarBookings(
		long calendarId, long startTime, long endTime) {

		return _calendarBookingLocalService.getCalendarBookings(
			calendarId, startTime, endTime);
	}

	@Override
	public java.util.List<CalendarBooking> getCalendarBookings(
		long calendarId, long startTime, long endTime, int max) {

		return _calendarBookingLocalService.getCalendarBookings(
			calendarId, startTime, endTime, max);
	}

	/**
	 * Returns all the calendar bookings matching the UUID and company.
	 *
	 * @param uuid the UUID of the calendar bookings
	 * @param companyId the primary key of the company
	 * @return the matching calendar bookings, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CalendarBooking>
		getCalendarBookingsByUuidAndCompanyId(String uuid, long companyId) {

		return _calendarBookingLocalService.
			getCalendarBookingsByUuidAndCompanyId(uuid, companyId);
	}

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
	@Override
	public java.util.List<CalendarBooking>
		getCalendarBookingsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CalendarBooking>
				orderByComparator) {

		return _calendarBookingLocalService.
			getCalendarBookingsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of calendar bookings.
	 *
	 * @return the number of calendar bookings
	 */
	@Override
	public int getCalendarBookingsCount() {
		return _calendarBookingLocalService.getCalendarBookingsCount();
	}

	@Override
	public int getCalendarBookingsCount(
		long calendarId, long parentCalendarBookingId) {

		return _calendarBookingLocalService.getCalendarBookingsCount(
			calendarId, parentCalendarBookingId);
	}

	@Override
	public java.util.List<CalendarBooking> getChildCalendarBookings(
		long calendarBookingId) {

		return _calendarBookingLocalService.getChildCalendarBookings(
			calendarBookingId);
	}

	@Override
	public java.util.List<CalendarBooking> getChildCalendarBookings(
		long parentCalendarBookingId, int status) {

		return _calendarBookingLocalService.getChildCalendarBookings(
			parentCalendarBookingId, status);
	}

	@Override
	public long[] getChildCalendarIds(long calendarBookingId, long calendarId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.getChildCalendarIds(
			calendarBookingId, calendarId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _calendarBookingLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _calendarBookingLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public CalendarBooking getLastInstanceCalendarBooking(
		CalendarBooking calendarBooking) {

		return _calendarBookingLocalService.getLastInstanceCalendarBooking(
			calendarBooking);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _calendarBookingLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<CalendarBooking> getRecurringCalendarBookings(
		CalendarBooking calendarBooking) {

		return _calendarBookingLocalService.getRecurringCalendarBookings(
			calendarBooking);
	}

	@Override
	public java.util.List<CalendarBooking> getRecurringCalendarBookings(
		CalendarBooking calendarBooking, long startTime) {

		return _calendarBookingLocalService.getRecurringCalendarBookings(
			calendarBooking, startTime);
	}

	@Override
	public boolean hasExclusiveCalendarBooking(
			com.liferay.calendar.model.Calendar calendar, long startTime,
			long endTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.hasExclusiveCalendarBooking(
			calendar, startTime, endTime);
	}

	@Override
	public CalendarBooking invokeTransition(
			long userId, CalendarBooking calendarBooking, long startTime,
			int status, boolean updateInstance, boolean allFollowing,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.invokeTransition(
			userId, calendarBooking, startTime, status, updateInstance,
			allFollowing, serviceContext);
	}

	@Override
	public boolean isStagingCalendarBooking(CalendarBooking calendarBooking)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.isStagingCalendarBooking(
			calendarBooking);
	}

	@Override
	public CalendarBooking moveCalendarBookingToTrash(
			long userId, CalendarBooking calendarBooking)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.moveCalendarBookingToTrash(
			userId, calendarBooking);
	}

	@Override
	public CalendarBooking moveCalendarBookingToTrash(
			long userId, long calendarBookingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.moveCalendarBookingToTrash(
			userId, calendarBookingId);
	}

	@Override
	public CalendarBooking restoreCalendarBookingFromTrash(
			long userId, long calendarBookingId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.restoreCalendarBookingFromTrash(
			userId, calendarBookingId);
	}

	@Override
	public java.util.List<CalendarBooking> search(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String keywords, long startTime, long endTime,
		java.util.TimeZone displayTimeZone, boolean recurring, int[] statuses,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CalendarBooking>
			orderByComparator) {

		return _calendarBookingLocalService.search(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, keywords, startTime, endTime,
			displayTimeZone, recurring, statuses, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<CalendarBooking> search(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId, String title,
		String description, String location, long startTime, long endTime,
		boolean recurring, int[] statuses, boolean andOperator, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<CalendarBooking>
			orderByComparator) {

		return _calendarBookingLocalService.search(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, title, description, location, startTime,
			endTime, recurring, statuses, andOperator, start, end,
			orderByComparator);
	}

	@Override
	public int searchCount(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId,
		String keywords, long startTime, long endTime, int[] statuses) {

		return _calendarBookingLocalService.searchCount(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, keywords, startTime, endTime, statuses);
	}

	@Override
	public int searchCount(
		long companyId, long[] groupIds, long[] calendarIds,
		long[] calendarResourceIds, long parentCalendarBookingId, String title,
		String description, String location, long startTime, long endTime,
		int[] statuses, boolean andOperator) {

		return _calendarBookingLocalService.searchCount(
			companyId, groupIds, calendarIds, calendarResourceIds,
			parentCalendarBookingId, title, description, location, startTime,
			endTime, statuses, andOperator);
	}

	@Override
	public void updateAsset(
			long userId, CalendarBooking calendarBooking,
			long[] assetCategoryIds, String[] assetTagNames,
			long[] assetLinkEntryIds, Double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		_calendarBookingLocalService.updateAsset(
			userId, calendarBooking, assetCategoryIds, assetTagNames,
			assetLinkEntryIds, priority);
	}

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
	@Override
	public CalendarBooking updateCalendarBooking(
		CalendarBooking calendarBooking) {

		return _calendarBookingLocalService.updateCalendarBooking(
			calendarBooking);
	}

	@Override
	public CalendarBooking updateCalendarBooking(
			long userId, long calendarBookingId, long calendarId,
			long[] childCalendarIds,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.updateCalendarBooking(
			userId, calendarBookingId, calendarId, childCalendarIds, titleMap,
			descriptionMap, location, startTime, endTime, allDay, recurrence,
			firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	@Override
	public CalendarBooking updateCalendarBooking(
			long userId, long calendarBookingId, long calendarId,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.updateCalendarBooking(
			userId, calendarBookingId, calendarId, titleMap, descriptionMap,
			location, startTime, endTime, allDay, recurrence, firstReminder,
			firstReminderType, secondReminder, secondReminderType,
			serviceContext);
	}

	@Override
	public CalendarBooking updateCalendarBookingInstance(
			long userId, long calendarBookingId, int instanceIndex,
			long calendarId, long[] childCalendarIds,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			boolean allFollowing, long firstReminder, String firstReminderType,
			long secondReminder, String secondReminderType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.updateCalendarBookingInstance(
			userId, calendarBookingId, instanceIndex, calendarId,
			childCalendarIds, titleMap, descriptionMap, location, startTime,
			endTime, allDay, allFollowing, firstReminder, firstReminderType,
			secondReminder, secondReminderType, serviceContext);
	}

	@Override
	public CalendarBooking updateCalendarBookingInstance(
			long userId, long calendarBookingId, int instanceIndex,
			long calendarId, long[] childCalendarIds,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, boolean allFollowing, long firstReminder,
			String firstReminderType, long secondReminder,
			String secondReminderType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.updateCalendarBookingInstance(
			userId, calendarBookingId, instanceIndex, calendarId,
			childCalendarIds, titleMap, descriptionMap, location, startTime,
			endTime, allDay, recurrence, allFollowing, firstReminder,
			firstReminderType, secondReminder, secondReminderType,
			serviceContext);
	}

	@Override
	public CalendarBooking updateCalendarBookingInstance(
			long userId, long calendarBookingId, int instanceIndex,
			long calendarId, java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			String recurrence, boolean allFollowing, long firstReminder,
			String firstReminderType, long secondReminder,
			String secondReminderType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.updateCalendarBookingInstance(
			userId, calendarBookingId, instanceIndex, calendarId, titleMap,
			descriptionMap, location, startTime, endTime, allDay, recurrence,
			allFollowing, firstReminder, firstReminderType, secondReminder,
			secondReminderType, serviceContext);
	}

	@Override
	public void updateLastInstanceCalendarBookingRecurrence(
		CalendarBooking calendarBooking, String recurrence) {

		_calendarBookingLocalService.
			updateLastInstanceCalendarBookingRecurrence(
				calendarBooking, recurrence);
	}

	@Override
	public CalendarBooking updateRecurringCalendarBooking(
			long userId, long calendarBookingId, long calendarId,
			long[] childCalendarIds,
			java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String location, long startTime, long endTime, boolean allDay,
			long firstReminder, String firstReminderType, long secondReminder,
			String secondReminderType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.updateRecurringCalendarBooking(
			userId, calendarBookingId, calendarId, childCalendarIds, titleMap,
			descriptionMap, location, startTime, endTime, allDay, firstReminder,
			firstReminderType, secondReminder, secondReminderType,
			serviceContext);
	}

	@Override
	public CalendarBooking updateStatus(
			long userId, CalendarBooking calendarBooking, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.updateStatus(
			userId, calendarBooking, status, serviceContext);
	}

	@Override
	public CalendarBooking updateStatus(
			long userId, long calendarBookingId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _calendarBookingLocalService.updateStatus(
			userId, calendarBookingId, status, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _calendarBookingLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CalendarBooking> getCTPersistence() {
		return _calendarBookingLocalService.getCTPersistence();
	}

	@Override
	public Class<CalendarBooking> getModelClass() {
		return _calendarBookingLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CalendarBooking>, R, E>
				updateUnsafeFunction)
		throws E {

		return _calendarBookingLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CalendarBookingLocalService getWrappedService() {
		return _calendarBookingLocalService;
	}

	@Override
	public void setWrappedService(
		CalendarBookingLocalService calendarBookingLocalService) {

		_calendarBookingLocalService = calendarBookingLocalService;
	}

	private CalendarBookingLocalService _calendarBookingLocalService;

}