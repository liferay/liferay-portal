/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.persistence;

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the calendar booking service. This utility wraps <code>com.liferay.calendar.service.persistence.impl.CalendarBookingPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Lundgren
 * @see CalendarBookingPersistence
 * @generated
 */
public class CalendarBookingUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(CalendarBooking calendarBooking) {
		getPersistence().clearCache(calendarBooking);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, CalendarBooking> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CalendarBooking> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CalendarBooking> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CalendarBooking> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CalendarBooking update(CalendarBooking calendarBooking) {
		return getPersistence().update(calendarBooking);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CalendarBooking update(
		CalendarBooking calendarBooking, ServiceContext serviceContext) {

		return getPersistence().update(calendarBooking, serviceContext);
	}

	/**
	 * Returns all the calendar bookings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching calendar bookings
	 */
	public static List<CalendarBooking> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the calendar bookings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @return the range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByUuid_First(
			String uuid, OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByUuid_First(
		String uuid, OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByUuid_Last(
			String uuid, OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByUuid_Last(
		String uuid, OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the calendar bookings before and after the current calendar booking in the ordered set where uuid = &#63;.
	 *
	 * @param calendarBookingId the primary key of the current calendar booking
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar booking
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	public static CalendarBooking[] findByUuid_PrevAndNext(
			long calendarBookingId, String uuid,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByUuid_PrevAndNext(
			calendarBookingId, uuid, orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of calendar bookings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching calendar bookings
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the calendar booking where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchBookingException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByUUID_G(String uuid, long groupId)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the calendar booking where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the calendar booking where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the calendar booking where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the calendar booking that was removed
	 */
	public static CalendarBooking removeByUUID_G(String uuid, long groupId)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of calendar bookings where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching calendar bookings
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the calendar bookings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching calendar bookings
	 */
	public static List<CalendarBooking> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the calendar bookings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @return the range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the calendar bookings before and after the current calendar booking in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param calendarBookingId the primary key of the current calendar booking
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar booking
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	public static CalendarBooking[] findByUuid_C_PrevAndNext(
			long calendarBookingId, String uuid, long companyId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByUuid_C_PrevAndNext(
			calendarBookingId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of calendar bookings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching calendar bookings
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the calendar bookings where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @return the matching calendar bookings
	 */
	public static List<CalendarBooking> findByCalendarId(long calendarId) {
		return getPersistence().findByCalendarId(calendarId);
	}

	/**
	 * Returns a range of all the calendar bookings where calendarId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarId the calendar ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @return the range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByCalendarId(
		long calendarId, int start, int end) {

		return getPersistence().findByCalendarId(calendarId, start, end);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where calendarId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarId the calendar ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByCalendarId(
		long calendarId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().findByCalendarId(
			calendarId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where calendarId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarId the calendar ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByCalendarId(
		long calendarId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCalendarId(
			calendarId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByCalendarId_First(
			long calendarId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByCalendarId_First(
			calendarId, orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByCalendarId_First(
		long calendarId, OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByCalendarId_First(
			calendarId, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByCalendarId_Last(
			long calendarId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByCalendarId_Last(
			calendarId, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByCalendarId_Last(
		long calendarId, OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByCalendarId_Last(
			calendarId, orderByComparator);
	}

	/**
	 * Returns the calendar bookings before and after the current calendar booking in the ordered set where calendarId = &#63;.
	 *
	 * @param calendarBookingId the primary key of the current calendar booking
	 * @param calendarId the calendar ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar booking
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	public static CalendarBooking[] findByCalendarId_PrevAndNext(
			long calendarBookingId, long calendarId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByCalendarId_PrevAndNext(
			calendarBookingId, calendarId, orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where calendarId = &#63; from the database.
	 *
	 * @param calendarId the calendar ID
	 */
	public static void removeByCalendarId(long calendarId) {
		getPersistence().removeByCalendarId(calendarId);
	}

	/**
	 * Returns the number of calendar bookings where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @return the number of matching calendar bookings
	 */
	public static int countByCalendarId(long calendarId) {
		return getPersistence().countByCalendarId(calendarId);
	}

	/**
	 * Returns all the calendar bookings where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @return the matching calendar bookings
	 */
	public static List<CalendarBooking> findByCalendarResourceId(
		long calendarResourceId) {

		return getPersistence().findByCalendarResourceId(calendarResourceId);
	}

	/**
	 * Returns a range of all the calendar bookings where calendarResourceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @return the range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByCalendarResourceId(
		long calendarResourceId, int start, int end) {

		return getPersistence().findByCalendarResourceId(
			calendarResourceId, start, end);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where calendarResourceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByCalendarResourceId(
		long calendarResourceId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().findByCalendarResourceId(
			calendarResourceId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where calendarResourceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByCalendarResourceId(
		long calendarResourceId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCalendarResourceId(
			calendarResourceId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByCalendarResourceId_First(
			long calendarResourceId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByCalendarResourceId_First(
			calendarResourceId, orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByCalendarResourceId_First(
		long calendarResourceId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByCalendarResourceId_First(
			calendarResourceId, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByCalendarResourceId_Last(
			long calendarResourceId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByCalendarResourceId_Last(
			calendarResourceId, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByCalendarResourceId_Last(
		long calendarResourceId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByCalendarResourceId_Last(
			calendarResourceId, orderByComparator);
	}

	/**
	 * Returns the calendar bookings before and after the current calendar booking in the ordered set where calendarResourceId = &#63;.
	 *
	 * @param calendarBookingId the primary key of the current calendar booking
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar booking
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	public static CalendarBooking[] findByCalendarResourceId_PrevAndNext(
			long calendarBookingId, long calendarResourceId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByCalendarResourceId_PrevAndNext(
			calendarBookingId, calendarResourceId, orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where calendarResourceId = &#63; from the database.
	 *
	 * @param calendarResourceId the calendar resource ID
	 */
	public static void removeByCalendarResourceId(long calendarResourceId) {
		getPersistence().removeByCalendarResourceId(calendarResourceId);
	}

	/**
	 * Returns the number of calendar bookings where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @return the number of matching calendar bookings
	 */
	public static int countByCalendarResourceId(long calendarResourceId) {
		return getPersistence().countByCalendarResourceId(calendarResourceId);
	}

	/**
	 * Returns all the calendar bookings where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the matching calendar bookings
	 */
	public static List<CalendarBooking> findByParentCalendarBookingId(
		long parentCalendarBookingId) {

		return getPersistence().findByParentCalendarBookingId(
			parentCalendarBookingId);
	}

	/**
	 * Returns a range of all the calendar bookings where parentCalendarBookingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @return the range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByParentCalendarBookingId(
		long parentCalendarBookingId, int start, int end) {

		return getPersistence().findByParentCalendarBookingId(
			parentCalendarBookingId, start, end);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where parentCalendarBookingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByParentCalendarBookingId(
		long parentCalendarBookingId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().findByParentCalendarBookingId(
			parentCalendarBookingId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where parentCalendarBookingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByParentCalendarBookingId(
		long parentCalendarBookingId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByParentCalendarBookingId(
			parentCalendarBookingId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByParentCalendarBookingId_First(
			long parentCalendarBookingId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByParentCalendarBookingId_First(
			parentCalendarBookingId, orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByParentCalendarBookingId_First(
		long parentCalendarBookingId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByParentCalendarBookingId_First(
			parentCalendarBookingId, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByParentCalendarBookingId_Last(
			long parentCalendarBookingId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByParentCalendarBookingId_Last(
			parentCalendarBookingId, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByParentCalendarBookingId_Last(
		long parentCalendarBookingId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByParentCalendarBookingId_Last(
			parentCalendarBookingId, orderByComparator);
	}

	/**
	 * Returns the calendar bookings before and after the current calendar booking in the ordered set where parentCalendarBookingId = &#63;.
	 *
	 * @param calendarBookingId the primary key of the current calendar booking
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar booking
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	public static CalendarBooking[] findByParentCalendarBookingId_PrevAndNext(
			long calendarBookingId, long parentCalendarBookingId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByParentCalendarBookingId_PrevAndNext(
			calendarBookingId, parentCalendarBookingId, orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where parentCalendarBookingId = &#63; from the database.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 */
	public static void removeByParentCalendarBookingId(
		long parentCalendarBookingId) {

		getPersistence().removeByParentCalendarBookingId(
			parentCalendarBookingId);
	}

	/**
	 * Returns the number of calendar bookings where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the number of matching calendar bookings
	 */
	public static int countByParentCalendarBookingId(
		long parentCalendarBookingId) {

		return getPersistence().countByParentCalendarBookingId(
			parentCalendarBookingId);
	}

	/**
	 * Returns all the calendar bookings where recurringCalendarBookingId = &#63;.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @return the matching calendar bookings
	 */
	public static List<CalendarBooking> findByRecurringCalendarBookingId(
		long recurringCalendarBookingId) {

		return getPersistence().findByRecurringCalendarBookingId(
			recurringCalendarBookingId);
	}

	/**
	 * Returns a range of all the calendar bookings where recurringCalendarBookingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @return the range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByRecurringCalendarBookingId(
		long recurringCalendarBookingId, int start, int end) {

		return getPersistence().findByRecurringCalendarBookingId(
			recurringCalendarBookingId, start, end);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where recurringCalendarBookingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByRecurringCalendarBookingId(
		long recurringCalendarBookingId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().findByRecurringCalendarBookingId(
			recurringCalendarBookingId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where recurringCalendarBookingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByRecurringCalendarBookingId(
		long recurringCalendarBookingId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByRecurringCalendarBookingId(
			recurringCalendarBookingId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where recurringCalendarBookingId = &#63;.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByRecurringCalendarBookingId_First(
			long recurringCalendarBookingId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByRecurringCalendarBookingId_First(
			recurringCalendarBookingId, orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where recurringCalendarBookingId = &#63;.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByRecurringCalendarBookingId_First(
		long recurringCalendarBookingId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByRecurringCalendarBookingId_First(
			recurringCalendarBookingId, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where recurringCalendarBookingId = &#63;.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByRecurringCalendarBookingId_Last(
			long recurringCalendarBookingId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByRecurringCalendarBookingId_Last(
			recurringCalendarBookingId, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where recurringCalendarBookingId = &#63;.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByRecurringCalendarBookingId_Last(
		long recurringCalendarBookingId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByRecurringCalendarBookingId_Last(
			recurringCalendarBookingId, orderByComparator);
	}

	/**
	 * Returns the calendar bookings before and after the current calendar booking in the ordered set where recurringCalendarBookingId = &#63;.
	 *
	 * @param calendarBookingId the primary key of the current calendar booking
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar booking
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	public static CalendarBooking[]
			findByRecurringCalendarBookingId_PrevAndNext(
				long calendarBookingId, long recurringCalendarBookingId,
				OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByRecurringCalendarBookingId_PrevAndNext(
			calendarBookingId, recurringCalendarBookingId, orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where recurringCalendarBookingId = &#63; from the database.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 */
	public static void removeByRecurringCalendarBookingId(
		long recurringCalendarBookingId) {

		getPersistence().removeByRecurringCalendarBookingId(
			recurringCalendarBookingId);
	}

	/**
	 * Returns the number of calendar bookings where recurringCalendarBookingId = &#63;.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @return the number of matching calendar bookings
	 */
	public static int countByRecurringCalendarBookingId(
		long recurringCalendarBookingId) {

		return getPersistence().countByRecurringCalendarBookingId(
			recurringCalendarBookingId);
	}

	/**
	 * Returns the calendar booking where calendarId = &#63; and parentCalendarBookingId = &#63; or throws a <code>NoSuchBookingException</code> if it could not be found.
	 *
	 * @param calendarId the calendar ID
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByC_P(
			long calendarId, long parentCalendarBookingId)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByC_P(calendarId, parentCalendarBookingId);
	}

	/**
	 * Returns the calendar booking where calendarId = &#63; and parentCalendarBookingId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param calendarId the calendar ID
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByC_P(
		long calendarId, long parentCalendarBookingId) {

		return getPersistence().fetchByC_P(calendarId, parentCalendarBookingId);
	}

	/**
	 * Returns the calendar booking where calendarId = &#63; and parentCalendarBookingId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param calendarId the calendar ID
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByC_P(
		long calendarId, long parentCalendarBookingId, boolean useFinderCache) {

		return getPersistence().fetchByC_P(
			calendarId, parentCalendarBookingId, useFinderCache);
	}

	/**
	 * Removes the calendar booking where calendarId = &#63; and parentCalendarBookingId = &#63; from the database.
	 *
	 * @param calendarId the calendar ID
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the calendar booking that was removed
	 */
	public static CalendarBooking removeByC_P(
			long calendarId, long parentCalendarBookingId)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().removeByC_P(
			calendarId, parentCalendarBookingId);
	}

	/**
	 * Returns the number of calendar bookings where calendarId = &#63; and parentCalendarBookingId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the number of matching calendar bookings
	 */
	public static int countByC_P(
		long calendarId, long parentCalendarBookingId) {

		return getPersistence().countByC_P(calendarId, parentCalendarBookingId);
	}

	/**
	 * Returns the calendar booking where calendarId = &#63; and vEventUid = &#63; or throws a <code>NoSuchBookingException</code> if it could not be found.
	 *
	 * @param calendarId the calendar ID
	 * @param vEventUid the v event uid
	 * @return the matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByC_V(long calendarId, String vEventUid)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByC_V(calendarId, vEventUid);
	}

	/**
	 * Returns the calendar booking where calendarId = &#63; and vEventUid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param calendarId the calendar ID
	 * @param vEventUid the v event uid
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByC_V(
		long calendarId, String vEventUid) {

		return getPersistence().fetchByC_V(calendarId, vEventUid);
	}

	/**
	 * Returns the calendar booking where calendarId = &#63; and vEventUid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param calendarId the calendar ID
	 * @param vEventUid the v event uid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByC_V(
		long calendarId, String vEventUid, boolean useFinderCache) {

		return getPersistence().fetchByC_V(
			calendarId, vEventUid, useFinderCache);
	}

	/**
	 * Removes the calendar booking where calendarId = &#63; and vEventUid = &#63; from the database.
	 *
	 * @param calendarId the calendar ID
	 * @param vEventUid the v event uid
	 * @return the calendar booking that was removed
	 */
	public static CalendarBooking removeByC_V(long calendarId, String vEventUid)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().removeByC_V(calendarId, vEventUid);
	}

	/**
	 * Returns the number of calendar bookings where calendarId = &#63; and vEventUid = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param vEventUid the v event uid
	 * @return the number of matching calendar bookings
	 */
	public static int countByC_V(long calendarId, String vEventUid) {
		return getPersistence().countByC_V(calendarId, vEventUid);
	}

	/**
	 * Returns all the calendar bookings where calendarId = &#63; and status = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @return the matching calendar bookings
	 */
	public static List<CalendarBooking> findByC_S(long calendarId, int status) {
		return getPersistence().findByC_S(calendarId, status);
	}

	/**
	 * Returns a range of all the calendar bookings where calendarId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @return the range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByC_S(
		long calendarId, int status, int start, int end) {

		return getPersistence().findByC_S(calendarId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where calendarId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByC_S(
		long calendarId, int status, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().findByC_S(
			calendarId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where calendarId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByC_S(
		long calendarId, int status, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_S(
			calendarId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where calendarId = &#63; and status = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByC_S_First(
			long calendarId, int status,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByC_S_First(
			calendarId, status, orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where calendarId = &#63; and status = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByC_S_First(
		long calendarId, int status,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByC_S_First(
			calendarId, status, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where calendarId = &#63; and status = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByC_S_Last(
			long calendarId, int status,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByC_S_Last(
			calendarId, status, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where calendarId = &#63; and status = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByC_S_Last(
		long calendarId, int status,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByC_S_Last(
			calendarId, status, orderByComparator);
	}

	/**
	 * Returns the calendar bookings before and after the current calendar booking in the ordered set where calendarId = &#63; and status = &#63;.
	 *
	 * @param calendarBookingId the primary key of the current calendar booking
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar booking
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	public static CalendarBooking[] findByC_S_PrevAndNext(
			long calendarBookingId, long calendarId, int status,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByC_S_PrevAndNext(
			calendarBookingId, calendarId, status, orderByComparator);
	}

	/**
	 * Returns all the calendar bookings where calendarId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarId the calendar ID
	 * @param statuses the statuses
	 * @return the matching calendar bookings
	 */
	public static List<CalendarBooking> findByC_S(
		long calendarId, int[] statuses) {

		return getPersistence().findByC_S(calendarId, statuses);
	}

	/**
	 * Returns a range of all the calendar bookings where calendarId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarId the calendar ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @return the range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByC_S(
		long calendarId, int[] statuses, int start, int end) {

		return getPersistence().findByC_S(calendarId, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where calendarId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarId the calendar ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByC_S(
		long calendarId, int[] statuses, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().findByC_S(
			calendarId, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where calendarId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarId the calendar ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByC_S(
		long calendarId, int[] statuses, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_S(
			calendarId, statuses, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the calendar bookings where calendarId = &#63; and status = &#63; from the database.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 */
	public static void removeByC_S(long calendarId, int status) {
		getPersistence().removeByC_S(calendarId, status);
	}

	/**
	 * Returns the number of calendar bookings where calendarId = &#63; and status = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @return the number of matching calendar bookings
	 */
	public static int countByC_S(long calendarId, int status) {
		return getPersistence().countByC_S(calendarId, status);
	}

	/**
	 * Returns the number of calendar bookings where calendarId = &#63; and status = any &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param statuses the statuses
	 * @return the number of matching calendar bookings
	 */
	public static int countByC_S(long calendarId, int[] statuses) {
		return getPersistence().countByC_S(calendarId, statuses);
	}

	/**
	 * Returns all the calendar bookings where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @return the matching calendar bookings
	 */
	public static List<CalendarBooking> findByP_S(
		long parentCalendarBookingId, int status) {

		return getPersistence().findByP_S(parentCalendarBookingId, status);
	}

	/**
	 * Returns a range of all the calendar bookings where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @return the range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByP_S(
		long parentCalendarBookingId, int status, int start, int end) {

		return getPersistence().findByP_S(
			parentCalendarBookingId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByP_S(
		long parentCalendarBookingId, int status, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().findByP_S(
			parentCalendarBookingId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	public static List<CalendarBooking> findByP_S(
		long parentCalendarBookingId, int status, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_S(
			parentCalendarBookingId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByP_S_First(
			long parentCalendarBookingId, int status,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByP_S_First(
			parentCalendarBookingId, status, orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByP_S_First(
		long parentCalendarBookingId, int status,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByP_S_First(
			parentCalendarBookingId, status, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByP_S_Last(
			long parentCalendarBookingId, int status,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByP_S_Last(
			parentCalendarBookingId, status, orderByComparator);
	}

	/**
	 * Returns the last calendar booking in the ordered set where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByP_S_Last(
		long parentCalendarBookingId, int status,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().fetchByP_S_Last(
			parentCalendarBookingId, status, orderByComparator);
	}

	/**
	 * Returns the calendar bookings before and after the current calendar booking in the ordered set where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * @param calendarBookingId the primary key of the current calendar booking
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar booking
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	public static CalendarBooking[] findByP_S_PrevAndNext(
			long calendarBookingId, long parentCalendarBookingId, int status,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByP_S_PrevAndNext(
			calendarBookingId, parentCalendarBookingId, status,
			orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where parentCalendarBookingId = &#63; and status = &#63; from the database.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 */
	public static void removeByP_S(long parentCalendarBookingId, int status) {
		getPersistence().removeByP_S(parentCalendarBookingId, status);
	}

	/**
	 * Returns the number of calendar bookings where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @return the number of matching calendar bookings
	 */
	public static int countByP_S(long parentCalendarBookingId, int status) {
		return getPersistence().countByP_S(parentCalendarBookingId, status);
	}

	/**
	 * Returns the calendar booking where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchBookingException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	public static CalendarBooking findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the calendar booking where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the calendar booking where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	public static CalendarBooking fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the calendar booking where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the calendar booking that was removed
	 */
	public static CalendarBooking removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of calendar bookings where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching calendar bookings
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Caches the calendar booking in the entity cache if it is enabled.
	 *
	 * @param calendarBooking the calendar booking
	 */
	public static void cacheResult(CalendarBooking calendarBooking) {
		getPersistence().cacheResult(calendarBooking);
	}

	/**
	 * Caches the calendar bookings in the entity cache if it is enabled.
	 *
	 * @param calendarBookings the calendar bookings
	 */
	public static void cacheResult(List<CalendarBooking> calendarBookings) {
		getPersistence().cacheResult(calendarBookings);
	}

	/**
	 * Creates a new calendar booking with the primary key. Does not add the calendar booking to the database.
	 *
	 * @param calendarBookingId the primary key for the new calendar booking
	 * @return the new calendar booking
	 */
	public static CalendarBooking create(long calendarBookingId) {
		return getPersistence().create(calendarBookingId);
	}

	/**
	 * Removes the calendar booking with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param calendarBookingId the primary key of the calendar booking
	 * @return the calendar booking that was removed
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	public static CalendarBooking remove(long calendarBookingId)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().remove(calendarBookingId);
	}

	public static CalendarBooking updateImpl(CalendarBooking calendarBooking) {
		return getPersistence().updateImpl(calendarBooking);
	}

	/**
	 * Returns the calendar booking with the primary key or throws a <code>NoSuchBookingException</code> if it could not be found.
	 *
	 * @param calendarBookingId the primary key of the calendar booking
	 * @return the calendar booking
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	public static CalendarBooking findByPrimaryKey(long calendarBookingId)
		throws com.liferay.calendar.exception.NoSuchBookingException {

		return getPersistence().findByPrimaryKey(calendarBookingId);
	}

	/**
	 * Returns the calendar booking with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param calendarBookingId the primary key of the calendar booking
	 * @return the calendar booking, or <code>null</code> if a calendar booking with the primary key could not be found
	 */
	public static CalendarBooking fetchByPrimaryKey(long calendarBookingId) {
		return getPersistence().fetchByPrimaryKey(calendarBookingId);
	}

	/**
	 * Returns all the calendar bookings.
	 *
	 * @return the calendar bookings
	 */
	public static List<CalendarBooking> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the calendar bookings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @return the range of calendar bookings
	 */
	public static List<CalendarBooking> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the calendar bookings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of calendar bookings
	 */
	public static List<CalendarBooking> findAll(
		int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendar bookings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of calendar bookings
	 */
	public static List<CalendarBooking> findAll(
		int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the calendar bookings from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of calendar bookings.
	 *
	 * @return the number of calendar bookings
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static CalendarBookingPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(CalendarBookingPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile CalendarBookingPersistence _persistence;

}