/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.persistence.impl;

import com.liferay.calendar.exception.DuplicateCalendarBookingExternalReferenceCodeException;
import com.liferay.calendar.exception.NoSuchBookingException;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.CalendarBookingTable;
import com.liferay.calendar.model.impl.CalendarBookingImpl;
import com.liferay.calendar.model.impl.CalendarBookingModelImpl;
import com.liferay.calendar.service.persistence.CalendarBookingPersistence;
import com.liferay.calendar.service.persistence.CalendarBookingUtil;
import com.liferay.calendar.service.persistence.impl.constants.CalendarPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the calendar booking service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Lundgren
 * @generated
 */
@Component(service = CalendarBookingPersistence.class)
public class CalendarBookingPersistenceImpl
	extends BasePersistenceImpl<CalendarBooking, NoSuchBookingException>
	implements CalendarBookingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CalendarBookingUtil</code> to access the calendar booking persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CalendarBookingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<CalendarBooking, NoSuchBookingException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the calendar bookings where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByUuid_First(
			String uuid, OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByUuid_First(
		String uuid, OrderByComparator<CalendarBooking> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of calendar bookings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<CalendarBooking, NoSuchBookingException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the calendar booking where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchBookingException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByUUID_G(String uuid, long groupId)
		throws NoSuchBookingException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the calendar booking where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the calendar booking where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the calendar booking that was removed
	 */
	@Override
	public CalendarBooking removeByUUID_G(String uuid, long groupId)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = findByUUID_G(uuid, groupId);

		return remove(calendarBooking);
	}

	/**
	 * Returns the number of calendar bookings where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<CalendarBooking, NoSuchBookingException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the calendar bookings where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
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
	@Override
	public List<CalendarBooking> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
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
	@Override
	public CalendarBooking findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of calendar bookings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<CalendarBooking, NoSuchBookingException>
		_collectionPersistenceFinderByCalendarId;

	/**
	 * Returns an ordered range of all the calendar bookings where calendarId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarId the calendar ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByCalendarId(
		long calendarId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCalendarId.find(
			finderCache, new Object[] {calendarId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByCalendarId_First(
			long calendarId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		return _collectionPersistenceFinderByCalendarId.findFirst(
			finderCache, new Object[] {calendarId}, orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByCalendarId_First(
		long calendarId, OrderByComparator<CalendarBooking> orderByComparator) {

		return _collectionPersistenceFinderByCalendarId.fetchFirst(
			finderCache, new Object[] {calendarId}, orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where calendarId = &#63; from the database.
	 *
	 * @param calendarId the calendar ID
	 */
	@Override
	public void removeByCalendarId(long calendarId) {
		_collectionPersistenceFinderByCalendarId.remove(
			finderCache, new Object[] {calendarId});
	}

	/**
	 * Returns the number of calendar bookings where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByCalendarId(long calendarId) {
		return _collectionPersistenceFinderByCalendarId.count(
			finderCache, new Object[] {calendarId});
	}

	private CollectionPersistenceFinder<CalendarBooking, NoSuchBookingException>
		_collectionPersistenceFinderByCalendarResourceId;

	/**
	 * Returns an ordered range of all the calendar bookings where calendarResourceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByCalendarResourceId(
		long calendarResourceId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCalendarResourceId.find(
			finderCache, new Object[] {calendarResourceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByCalendarResourceId_First(
			long calendarResourceId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		return _collectionPersistenceFinderByCalendarResourceId.findFirst(
			finderCache, new Object[] {calendarResourceId}, orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByCalendarResourceId_First(
		long calendarResourceId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return _collectionPersistenceFinderByCalendarResourceId.fetchFirst(
			finderCache, new Object[] {calendarResourceId}, orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where calendarResourceId = &#63; from the database.
	 *
	 * @param calendarResourceId the calendar resource ID
	 */
	@Override
	public void removeByCalendarResourceId(long calendarResourceId) {
		_collectionPersistenceFinderByCalendarResourceId.remove(
			finderCache, new Object[] {calendarResourceId});
	}

	/**
	 * Returns the number of calendar bookings where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByCalendarResourceId(long calendarResourceId) {
		return _collectionPersistenceFinderByCalendarResourceId.count(
			finderCache, new Object[] {calendarResourceId});
	}

	private CollectionPersistenceFinder<CalendarBooking, NoSuchBookingException>
		_collectionPersistenceFinderByParentCalendarBookingId;

	/**
	 * Returns an ordered range of all the calendar bookings where parentCalendarBookingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByParentCalendarBookingId(
		long parentCalendarBookingId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByParentCalendarBookingId.find(
			finderCache, new Object[] {parentCalendarBookingId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByParentCalendarBookingId_First(
			long parentCalendarBookingId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		return _collectionPersistenceFinderByParentCalendarBookingId.findFirst(
			finderCache, new Object[] {parentCalendarBookingId},
			orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByParentCalendarBookingId_First(
		long parentCalendarBookingId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return _collectionPersistenceFinderByParentCalendarBookingId.fetchFirst(
			finderCache, new Object[] {parentCalendarBookingId},
			orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where parentCalendarBookingId = &#63; from the database.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 */
	@Override
	public void removeByParentCalendarBookingId(long parentCalendarBookingId) {
		_collectionPersistenceFinderByParentCalendarBookingId.remove(
			finderCache, new Object[] {parentCalendarBookingId});
	}

	/**
	 * Returns the number of calendar bookings where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByParentCalendarBookingId(long parentCalendarBookingId) {
		return _collectionPersistenceFinderByParentCalendarBookingId.count(
			finderCache, new Object[] {parentCalendarBookingId});
	}

	private CollectionPersistenceFinder<CalendarBooking, NoSuchBookingException>
		_collectionPersistenceFinderByRecurringCalendarBookingId;

	/**
	 * Returns an ordered range of all the calendar bookings where recurringCalendarBookingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
	 * </p>
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param start the lower bound of the range of calendar bookings
	 * @param end the upper bound of the range of calendar bookings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByRecurringCalendarBookingId(
		long recurringCalendarBookingId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRecurringCalendarBookingId.find(
			finderCache, new Object[] {recurringCalendarBookingId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar booking in the ordered set where recurringCalendarBookingId = &#63;.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByRecurringCalendarBookingId_First(
			long recurringCalendarBookingId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		return _collectionPersistenceFinderByRecurringCalendarBookingId.
			findFirst(
				finderCache, new Object[] {recurringCalendarBookingId},
				orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where recurringCalendarBookingId = &#63;.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByRecurringCalendarBookingId_First(
		long recurringCalendarBookingId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return _collectionPersistenceFinderByRecurringCalendarBookingId.
			fetchFirst(
				finderCache, new Object[] {recurringCalendarBookingId},
				orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where recurringCalendarBookingId = &#63; from the database.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 */
	@Override
	public void removeByRecurringCalendarBookingId(
		long recurringCalendarBookingId) {

		_collectionPersistenceFinderByRecurringCalendarBookingId.remove(
			finderCache, new Object[] {recurringCalendarBookingId});
	}

	/**
	 * Returns the number of calendar bookings where recurringCalendarBookingId = &#63;.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByRecurringCalendarBookingId(
		long recurringCalendarBookingId) {

		return _collectionPersistenceFinderByRecurringCalendarBookingId.count(
			finderCache, new Object[] {recurringCalendarBookingId});
	}

	private UniquePersistenceFinder<CalendarBooking, NoSuchBookingException>
		_uniquePersistenceFinderByC_P;

	/**
	 * Returns the calendar booking where calendarId = &#63; and parentCalendarBookingId = &#63; or throws a <code>NoSuchBookingException</code> if it could not be found.
	 *
	 * @param calendarId the calendar ID
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByC_P(
			long calendarId, long parentCalendarBookingId)
		throws NoSuchBookingException {

		return _uniquePersistenceFinderByC_P.find(
			finderCache, new Object[] {calendarId, parentCalendarBookingId});
	}

	/**
	 * Returns the calendar booking where calendarId = &#63; and parentCalendarBookingId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param calendarId the calendar ID
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByC_P(
		long calendarId, long parentCalendarBookingId, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_P.fetch(
			finderCache, new Object[] {calendarId, parentCalendarBookingId},
			useFinderCache);
	}

	/**
	 * Removes the calendar booking where calendarId = &#63; and parentCalendarBookingId = &#63; from the database.
	 *
	 * @param calendarId the calendar ID
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the calendar booking that was removed
	 */
	@Override
	public CalendarBooking removeByC_P(
			long calendarId, long parentCalendarBookingId)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = findByC_P(
			calendarId, parentCalendarBookingId);

		return remove(calendarBooking);
	}

	/**
	 * Returns the number of calendar bookings where calendarId = &#63; and parentCalendarBookingId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByC_P(long calendarId, long parentCalendarBookingId) {
		return _uniquePersistenceFinderByC_P.count(
			finderCache, new Object[] {calendarId, parentCalendarBookingId});
	}

	private UniquePersistenceFinder<CalendarBooking, NoSuchBookingException>
		_uniquePersistenceFinderByC_V;

	/**
	 * Returns the calendar booking where calendarId = &#63; and vEventUid = &#63; or throws a <code>NoSuchBookingException</code> if it could not be found.
	 *
	 * @param calendarId the calendar ID
	 * @param vEventUid the v event uid
	 * @return the matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByC_V(long calendarId, String vEventUid)
		throws NoSuchBookingException {

		return _uniquePersistenceFinderByC_V.find(
			finderCache, new Object[] {calendarId, vEventUid});
	}

	/**
	 * Returns the calendar booking where calendarId = &#63; and vEventUid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param calendarId the calendar ID
	 * @param vEventUid the v event uid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByC_V(
		long calendarId, String vEventUid, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_V.fetch(
			finderCache, new Object[] {calendarId, vEventUid}, useFinderCache);
	}

	/**
	 * Removes the calendar booking where calendarId = &#63; and vEventUid = &#63; from the database.
	 *
	 * @param calendarId the calendar ID
	 * @param vEventUid the v event uid
	 * @return the calendar booking that was removed
	 */
	@Override
	public CalendarBooking removeByC_V(long calendarId, String vEventUid)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = findByC_V(calendarId, vEventUid);

		return remove(calendarBooking);
	}

	/**
	 * Returns the number of calendar bookings where calendarId = &#63; and vEventUid = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param vEventUid the v event uid
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByC_V(long calendarId, String vEventUid) {
		return _uniquePersistenceFinderByC_V.count(
			finderCache, new Object[] {calendarId, vEventUid});
	}

	private CollectionPersistenceFinder<CalendarBooking, NoSuchBookingException>
		_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the calendar bookings where calendarId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
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
	@Override
	public List<CalendarBooking> findByC_S(
		long calendarId, int status, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache, new Object[] {calendarId, new int[] {status}}, start,
			end, orderByComparator, useFinderCache);
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
	@Override
	public CalendarBooking findByC_S_First(
			long calendarId, int status,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		return _collectionPersistenceFinderByC_S.findFirst(
			finderCache, new Object[] {calendarId, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where calendarId = &#63; and status = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByC_S_First(
		long calendarId, int status,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			finderCache, new Object[] {calendarId, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendar bookings where calendarId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
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
	@Override
	public List<CalendarBooking> findByC_S(
		long calendarId, int[] statuses, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			finderCache,
			new Object[] {calendarId, ArrayUtil.sortedUnique(statuses)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the calendar bookings where calendarId = &#63; and status = &#63; from the database.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long calendarId, int status) {
		_collectionPersistenceFinderByC_S.remove(
			finderCache, new Object[] {calendarId, new int[] {status}});
	}

	/**
	 * Returns the number of calendar bookings where calendarId = &#63; and status = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByC_S(long calendarId, int status) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache, new Object[] {calendarId, new int[] {status}});
	}

	/**
	 * Returns the number of calendar bookings where calendarId = &#63; and status = any &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param statuses the statuses
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByC_S(long calendarId, int[] statuses) {
		return _collectionPersistenceFinderByC_S.count(
			finderCache,
			new Object[] {calendarId, ArrayUtil.sortedUnique(statuses)});
	}

	private CollectionPersistenceFinder<CalendarBooking, NoSuchBookingException>
		_collectionPersistenceFinderByP_S;

	/**
	 * Returns an ordered range of all the calendar bookings where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarBookingModelImpl</code>.
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
	@Override
	public List<CalendarBooking> findByP_S(
		long parentCalendarBookingId, int status, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_S.find(
			finderCache, new Object[] {parentCalendarBookingId, status}, start,
			end, orderByComparator, useFinderCache);
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
	@Override
	public CalendarBooking findByP_S_First(
			long parentCalendarBookingId, int status,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		return _collectionPersistenceFinderByP_S.findFirst(
			finderCache, new Object[] {parentCalendarBookingId, status},
			orderByComparator);
	}

	/**
	 * Returns the first calendar booking in the ordered set where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByP_S_First(
		long parentCalendarBookingId, int status,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return _collectionPersistenceFinderByP_S.fetchFirst(
			finderCache, new Object[] {parentCalendarBookingId, status},
			orderByComparator);
	}

	/**
	 * Removes all the calendar bookings where parentCalendarBookingId = &#63; and status = &#63; from the database.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 */
	@Override
	public void removeByP_S(long parentCalendarBookingId, int status) {
		_collectionPersistenceFinderByP_S.remove(
			finderCache, new Object[] {parentCalendarBookingId, status});
	}

	/**
	 * Returns the number of calendar bookings where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByP_S(long parentCalendarBookingId, int status) {
		return _collectionPersistenceFinderByP_S.count(
			finderCache, new Object[] {parentCalendarBookingId, status});
	}

	private UniquePersistenceFinder<CalendarBooking, NoSuchBookingException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the calendar booking where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchBookingException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchBookingException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the calendar booking where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the calendar booking where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the calendar booking that was removed
	 */
	@Override
	public CalendarBooking removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = findByERC_G(
			externalReferenceCode, groupId);

		return remove(calendarBooking);
	}

	/**
	 * Returns the number of calendar bookings where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public CalendarBookingPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CalendarBooking.class);

		setModelImplClass(CalendarBookingImpl.class);
		setModelPKClass(long.class);

		setTable(CalendarBookingTable.INSTANCE);
	}

	/**
	 * Creates a new calendar booking with the primary key. Does not add the calendar booking to the database.
	 *
	 * @param calendarBookingId the primary key for the new calendar booking
	 * @return the new calendar booking
	 */
	@Override
	public CalendarBooking create(long calendarBookingId) {
		CalendarBooking calendarBooking = new CalendarBookingImpl();

		calendarBooking.setNew(true);
		calendarBooking.setPrimaryKey(calendarBookingId);

		String uuid = PortalUUIDUtil.generate();

		calendarBooking.setUuid(uuid);

		calendarBooking.setCompanyId(CompanyThreadLocal.getCompanyId());

		return calendarBooking;
	}

	/**
	 * Removes the calendar booking with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param calendarBookingId the primary key of the calendar booking
	 * @return the calendar booking that was removed
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	@Override
	public CalendarBooking remove(long calendarBookingId)
		throws NoSuchBookingException {

		return remove((Serializable)calendarBookingId);
	}

	@Override
	protected CalendarBooking removeImpl(CalendarBooking calendarBooking) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(calendarBooking)) {
				calendarBooking = (CalendarBooking)session.get(
					CalendarBookingImpl.class,
					calendarBooking.getPrimaryKeyObj());
			}

			if ((calendarBooking != null) &&
				ctPersistenceHelper.isRemove(calendarBooking)) {

				session.delete(calendarBooking);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (calendarBooking != null) {
			clearCache(calendarBooking);
		}

		return calendarBooking;
	}

	@Override
	public CalendarBooking updateImpl(CalendarBooking calendarBooking) {
		boolean isNew = calendarBooking.isNew();

		if (!(calendarBooking instanceof CalendarBookingModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(calendarBooking.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					calendarBooking);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in calendarBooking proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CalendarBooking implementation " +
					calendarBooking.getClass());
		}

		CalendarBookingModelImpl calendarBookingModelImpl =
			(CalendarBookingModelImpl)calendarBooking;

		if (Validator.isNull(calendarBooking.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			calendarBooking.setUuid(uuid);
		}

		if (Validator.isNull(calendarBooking.getExternalReferenceCode())) {
			calendarBooking.setExternalReferenceCode(calendarBooking.getUuid());
		}
		else {
			if (!Objects.equals(
					calendarBookingModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					calendarBooking.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = calendarBooking.getCompanyId();

					long groupId = calendarBooking.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = calendarBooking.getPrimaryKey();
					}

					try {
						calendarBooking.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								CalendarBooking.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								calendarBooking.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			CalendarBooking ercCalendarBooking = fetchByERC_G(
				calendarBooking.getExternalReferenceCode(),
				calendarBooking.getGroupId());

			if (isNew) {
				if (ercCalendarBooking != null) {
					throw new DuplicateCalendarBookingExternalReferenceCodeException(
						"Duplicate calendar booking with external reference code " +
							calendarBooking.getExternalReferenceCode() +
								" and group " + calendarBooking.getGroupId());
				}
			}
			else {
				if ((ercCalendarBooking != null) &&
					(calendarBooking.getCalendarBookingId() !=
						ercCalendarBooking.getCalendarBookingId())) {

					throw new DuplicateCalendarBookingExternalReferenceCodeException(
						"Duplicate calendar booking with external reference code " +
							calendarBooking.getExternalReferenceCode() +
								" and group " + calendarBooking.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (calendarBooking.getCreateDate() == null)) {
			if (serviceContext == null) {
				calendarBooking.setCreateDate(date);
			}
			else {
				calendarBooking.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!calendarBookingModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				calendarBooking.setModifiedDate(date);
			}
			else {
				calendarBooking.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(calendarBooking)) {
				if (!isNew) {
					session.evict(
						CalendarBookingImpl.class,
						calendarBooking.getPrimaryKeyObj());
				}

				session.save(calendarBooking);
			}
			else {
				calendarBooking = (CalendarBooking)session.merge(
					calendarBooking);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(calendarBooking, false);

		if (isNew) {
			calendarBooking.setNew(false);
		}

		calendarBooking.resetOriginalValues();

		return calendarBooking;
	}

	/**
	 * Returns the calendar booking with the primary key or throws a <code>NoSuchBookingException</code> if it could not be found.
	 *
	 * @param calendarBookingId the primary key of the calendar booking
	 * @return the calendar booking
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	@Override
	public CalendarBooking findByPrimaryKey(long calendarBookingId)
		throws NoSuchBookingException {

		return findByPrimaryKey((Serializable)calendarBookingId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the calendar booking with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param calendarBookingId the primary key of the calendar booking
	 * @return the calendar booking, or <code>null</code> if a calendar booking with the primary key could not be found
	 */
	@Override
	public CalendarBooking fetchByPrimaryKey(long calendarBookingId) {
		return fetchByPrimaryKey((Serializable)calendarBookingId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "calendarBookingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CALENDARBOOKING;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return CalendarBookingModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CalendarBooking";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("calendarId");
		ctMergeColumnNames.add("calendarResourceId");
		ctMergeColumnNames.add("parentCalendarBookingId");
		ctMergeColumnNames.add("recurringCalendarBookingId");
		ctMergeColumnNames.add("vEventUid");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("location");
		ctMergeColumnNames.add("startTime");
		ctMergeColumnNames.add("endTime");
		ctMergeColumnNames.add("allDay");
		ctMergeColumnNames.add("recurrence");
		ctMergeColumnNames.add("firstReminder");
		ctMergeColumnNames.add("firstReminderType");
		ctMergeColumnNames.add("secondReminder");
		ctMergeColumnNames.add("secondReminderType");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("calendarBookingId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"calendarId", "parentCalendarBookingId"});

		_uniqueIndexColumnNames.add(new String[] {"calendarId", "vEventUid"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the calendar booking persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_CALENDARBOOKING_WHERE, _SQL_COUNT_CALENDARBOOKING_WHERE,
			CalendarBookingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"calendarBooking.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CalendarBooking::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CalendarBooking::getUuid),
				CalendarBooking::getGroupId),
			_SQL_SELECT_CALENDARBOOKING_WHERE, "",
			new FinderColumn<>(
				"calendarBooking.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CalendarBooking::getUuid),
			new FinderColumn<>(
				"calendarBooking.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CalendarBooking::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_CALENDARBOOKING_WHERE,
				_SQL_COUNT_CALENDARBOOKING_WHERE,
				CalendarBookingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"calendarBooking.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CalendarBooking::getUuid),
				new FinderColumn<>(
					"calendarBooking.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CalendarBooking::getCompanyId));

		_collectionPersistenceFinderByCalendarId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCalendarId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"calendarId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCalendarId", new String[] {Long.class.getName()},
					new String[] {"calendarId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCalendarId", new String[] {Long.class.getName()},
					new String[] {"calendarId"}, false),
				_SQL_SELECT_CALENDARBOOKING_WHERE,
				_SQL_COUNT_CALENDARBOOKING_WHERE,
				CalendarBookingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"calendarBooking.", "calendarId", FinderColumn.Type.LONG,
					"=", true, true, CalendarBooking::getCalendarId));

		_collectionPersistenceFinderByCalendarResourceId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCalendarResourceId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"calendarResourceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCalendarResourceId",
					new String[] {Long.class.getName()},
					new String[] {"calendarResourceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCalendarResourceId",
					new String[] {Long.class.getName()},
					new String[] {"calendarResourceId"}, false),
				_SQL_SELECT_CALENDARBOOKING_WHERE,
				_SQL_COUNT_CALENDARBOOKING_WHERE,
				CalendarBookingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"calendarBooking.", "calendarResourceId",
					FinderColumn.Type.LONG, "=", true, true,
					CalendarBooking::getCalendarResourceId));

		_collectionPersistenceFinderByParentCalendarBookingId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByParentCalendarBookingId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"parentCalendarBookingId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByParentCalendarBookingId",
					new String[] {Long.class.getName()},
					new String[] {"parentCalendarBookingId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByParentCalendarBookingId",
					new String[] {Long.class.getName()},
					new String[] {"parentCalendarBookingId"}, false),
				_SQL_SELECT_CALENDARBOOKING_WHERE,
				_SQL_COUNT_CALENDARBOOKING_WHERE,
				CalendarBookingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"calendarBooking.", "parentCalendarBookingId",
					FinderColumn.Type.LONG, "=", true, true,
					CalendarBooking::getParentCalendarBookingId));

		_collectionPersistenceFinderByRecurringCalendarBookingId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByRecurringCalendarBookingId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"recurringCalendarBookingId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByRecurringCalendarBookingId",
					new String[] {Long.class.getName()},
					new String[] {"recurringCalendarBookingId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByRecurringCalendarBookingId",
					new String[] {Long.class.getName()},
					new String[] {"recurringCalendarBookingId"}, false),
				_SQL_SELECT_CALENDARBOOKING_WHERE,
				_SQL_COUNT_CALENDARBOOKING_WHERE,
				CalendarBookingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"calendarBooking.", "recurringCalendarBookingId",
					FinderColumn.Type.LONG, "=", true, true,
					CalendarBooking::getRecurringCalendarBookingId));

		_uniquePersistenceFinderByC_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"calendarId", "parentCalendarBookingId"}, 0, 0,
				false, CalendarBooking::getCalendarId,
				CalendarBooking::getParentCalendarBookingId),
			_SQL_SELECT_CALENDARBOOKING_WHERE, "",
			new FinderColumn<>(
				"calendarBooking.", "calendarId", FinderColumn.Type.LONG, "=",
				true, true, CalendarBooking::getCalendarId),
			new FinderColumn<>(
				"calendarBooking.", "parentCalendarBookingId",
				FinderColumn.Type.LONG, "=", true, true,
				CalendarBooking::getParentCalendarBookingId));

		_uniquePersistenceFinderByC_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_V",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"calendarId", "vEventUid"}, 0, 2, false,
				CalendarBooking::getCalendarId,
				convertNullFunction(CalendarBooking::getVEventUid)),
			_SQL_SELECT_CALENDARBOOKING_WHERE, "",
			new FinderColumn<>(
				"calendarBooking.", "calendarId", FinderColumn.Type.LONG, "=",
				true, true, CalendarBooking::getCalendarId),
			new FinderColumn<>(
				"calendarBooking.", "vEventUid", FinderColumn.Type.STRING, "=",
				true, true, CalendarBooking::getVEventUid));

		_collectionPersistenceFinderByC_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"calendarId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"calendarId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"calendarId", "status"}, false),
			_SQL_SELECT_CALENDARBOOKING_WHERE, _SQL_COUNT_CALENDARBOOKING_WHERE,
			CalendarBookingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"calendarBooking.", "calendarId", FinderColumn.Type.LONG, "=",
				true, true, CalendarBooking::getCalendarId),
			new ArrayableFinderColumn<>(
				"calendarBooking.", "status", FinderColumn.Type.INTEGER, "=",
				false, true, true, CalendarBooking::getStatus));

		_collectionPersistenceFinderByP_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"parentCalendarBookingId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"parentCalendarBookingId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"parentCalendarBookingId", "status"}, false),
			_SQL_SELECT_CALENDARBOOKING_WHERE, _SQL_COUNT_CALENDARBOOKING_WHERE,
			CalendarBookingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"calendarBooking.", "parentCalendarBookingId",
				FinderColumn.Type.LONG, "=", true, true,
				CalendarBooking::getParentCalendarBookingId),
			new FinderColumn<>(
				"calendarBooking.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, CalendarBooking::getStatus));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(CalendarBooking::getExternalReferenceCode),
				CalendarBooking::getGroupId),
			_SQL_SELECT_CALENDARBOOKING_WHERE, "",
			new FinderColumn<>(
				"calendarBooking.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				CalendarBooking::getExternalReferenceCode),
			new FinderColumn<>(
				"calendarBooking.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CalendarBooking::getGroupId));

		CalendarBookingUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CalendarBookingUtil.setPersistence(null);

		entityCache.removeCache(CalendarBookingImpl.class.getName());
	}

	@Override
	@Reference(
		target = CalendarPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CalendarPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CalendarPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CalendarBookingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CALENDARBOOKING =
		"SELECT calendarBooking FROM CalendarBooking calendarBooking";

	private static final String _SQL_SELECT_CALENDARBOOKING_WHERE =
		"SELECT calendarBooking FROM CalendarBooking calendarBooking WHERE ";

	private static final String _SQL_COUNT_CALENDARBOOKING_WHERE =
		"SELECT COUNT(calendarBooking) FROM CalendarBooking calendarBooking WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2038942349