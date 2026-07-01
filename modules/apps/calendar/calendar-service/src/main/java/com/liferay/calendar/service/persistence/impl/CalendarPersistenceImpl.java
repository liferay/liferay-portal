/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.persistence.impl;

import com.liferay.calendar.exception.NoSuchCalendarException;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarTable;
import com.liferay.calendar.model.impl.CalendarImpl;
import com.liferay.calendar.model.impl.CalendarModelImpl;
import com.liferay.calendar.service.persistence.CalendarPersistence;
import com.liferay.calendar.service.persistence.CalendarUtil;
import com.liferay.calendar.service.persistence.impl.constants.CalendarPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
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
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the calendar service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Eduardo Lundgren
 * @generated
 */
@Component(service = CalendarPersistence.class)
public class CalendarPersistenceImpl
	extends BasePersistenceImpl<Calendar, NoSuchCalendarException>
	implements CalendarPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CalendarUtil</code> to access the calendar persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CalendarImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<Calendar, NoSuchCalendarException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the calendars where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendars
	 */
	@Override
	public List<Calendar> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Calendar> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first calendar in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar
	 * @throws NoSuchCalendarException if a matching calendar could not be found
	 */
	@Override
	public Calendar findByUuid_First(
			String uuid, OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first calendar in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar, or <code>null</code> if a matching calendar could not be found
	 */
	@Override
	public Calendar fetchByUuid_First(
		String uuid, OrderByComparator<Calendar> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the calendars where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of calendars where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching calendars
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<Calendar, NoSuchCalendarException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the calendar where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCalendarException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching calendar
	 * @throws NoSuchCalendarException if a matching calendar could not be found
	 */
	@Override
	public Calendar findByUUID_G(String uuid, long groupId)
		throws NoSuchCalendarException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the calendar where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching calendar, or <code>null</code> if a matching calendar could not be found
	 */
	@Override
	public Calendar fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the calendar where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the calendar that was removed
	 */
	@Override
	public Calendar removeByUUID_G(String uuid, long groupId)
		throws NoSuchCalendarException {

		Calendar calendar = findByUUID_G(uuid, groupId);

		return remove(calendar);
	}

	/**
	 * Returns the number of calendars where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching calendars
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<Calendar, NoSuchCalendarException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the calendars where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendars
	 */
	@Override
	public List<Calendar> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Calendar> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar
	 * @throws NoSuchCalendarException if a matching calendar could not be found
	 */
	@Override
	public Calendar findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first calendar in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar, or <code>null</code> if a matching calendar could not be found
	 */
	@Override
	public Calendar fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Calendar> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the calendars where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of calendars where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching calendars
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder<Calendar, NoSuchCalendarException>
		_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the calendars where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendars
	 */
	@Override
	public List<Calendar> findByG_C(
		long groupId, long calendarResourceId, int start, int end,
		OrderByComparator<Calendar> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache, new Object[] {groupId, calendarResourceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar in the ordered set where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar
	 * @throws NoSuchCalendarException if a matching calendar could not be found
	 */
	@Override
	public Calendar findByG_C_First(
			long groupId, long calendarResourceId,
			OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		return _collectionPersistenceFinderByG_C.findFirst(
			finderCache, new Object[] {groupId, calendarResourceId},
			orderByComparator);
	}

	/**
	 * Returns the first calendar in the ordered set where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar, or <code>null</code> if a matching calendar could not be found
	 */
	@Override
	public Calendar fetchByG_C_First(
		long groupId, long calendarResourceId,
		OrderByComparator<Calendar> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			finderCache, new Object[] {groupId, calendarResourceId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendars that the user has permissions to view where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendars that the user has permission to view
	 */
	@Override
	public List<Calendar> filterFindByG_C(
		long groupId, long calendarResourceId, int start, int end,
		OrderByComparator<Calendar> orderByComparator) {

		return _collectionPersistenceFinderByG_C.filterFind(
			finderCache, new Object[] {groupId, calendarResourceId}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the calendars where groupId = &#63; and calendarResourceId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 */
	@Override
	public void removeByG_C(long groupId, long calendarResourceId) {
		_collectionPersistenceFinderByG_C.remove(
			finderCache, new Object[] {groupId, calendarResourceId});
	}

	/**
	 * Returns the number of calendars where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @return the number of matching calendars
	 */
	@Override
	public int countByG_C(long groupId, long calendarResourceId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache, new Object[] {groupId, calendarResourceId});
	}

	/**
	 * Returns the number of calendars that the user has permission to view where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @return the number of matching calendars that the user has permission to view
	 */
	@Override
	public int filterCountByG_C(long groupId, long calendarResourceId) {
		return _collectionPersistenceFinderByG_C.filterCount(
			finderCache, new Object[] {groupId, calendarResourceId}, groupId);
	}

	private FilterCollectionPersistenceFinder<Calendar, NoSuchCalendarException>
		_collectionPersistenceFinderByG_C_D;

	/**
	 * Returns an ordered range of all the calendars where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching calendars
	 */
	@Override
	public List<Calendar> findByG_C_D(
		long groupId, long calendarResourceId, boolean defaultCalendar,
		int start, int end, OrderByComparator<Calendar> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_D.find(
			finderCache,
			new Object[] {groupId, calendarResourceId, defaultCalendar}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first calendar in the ordered set where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar
	 * @throws NoSuchCalendarException if a matching calendar could not be found
	 */
	@Override
	public Calendar findByG_C_D_First(
			long groupId, long calendarResourceId, boolean defaultCalendar,
			OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		return _collectionPersistenceFinderByG_C_D.findFirst(
			finderCache,
			new Object[] {groupId, calendarResourceId, defaultCalendar},
			orderByComparator);
	}

	/**
	 * Returns the first calendar in the ordered set where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching calendar, or <code>null</code> if a matching calendar could not be found
	 */
	@Override
	public Calendar fetchByG_C_D_First(
		long groupId, long calendarResourceId, boolean defaultCalendar,
		OrderByComparator<Calendar> orderByComparator) {

		return _collectionPersistenceFinderByG_C_D.fetchFirst(
			finderCache,
			new Object[] {groupId, calendarResourceId, defaultCalendar},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the calendars that the user has permissions to view where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendars that the user has permission to view
	 */
	@Override
	public List<Calendar> filterFindByG_C_D(
		long groupId, long calendarResourceId, boolean defaultCalendar,
		int start, int end, OrderByComparator<Calendar> orderByComparator) {

		return _collectionPersistenceFinderByG_C_D.filterFind(
			finderCache,
			new Object[] {groupId, calendarResourceId, defaultCalendar}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the calendars where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 */
	@Override
	public void removeByG_C_D(
		long groupId, long calendarResourceId, boolean defaultCalendar) {

		_collectionPersistenceFinderByG_C_D.remove(
			finderCache,
			new Object[] {groupId, calendarResourceId, defaultCalendar});
	}

	/**
	 * Returns the number of calendars where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @return the number of matching calendars
	 */
	@Override
	public int countByG_C_D(
		long groupId, long calendarResourceId, boolean defaultCalendar) {

		return _collectionPersistenceFinderByG_C_D.count(
			finderCache,
			new Object[] {groupId, calendarResourceId, defaultCalendar});
	}

	/**
	 * Returns the number of calendars that the user has permission to view where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @return the number of matching calendars that the user has permission to view
	 */
	@Override
	public int filterCountByG_C_D(
		long groupId, long calendarResourceId, boolean defaultCalendar) {

		return _collectionPersistenceFinderByG_C_D.filterCount(
			finderCache,
			new Object[] {groupId, calendarResourceId, defaultCalendar},
			groupId);
	}

	public CalendarPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Calendar.class);

		setModelImplClass(CalendarImpl.class);
		setModelPKClass(long.class);

		setTable(CalendarTable.INSTANCE);
	}

	/**
	 * Creates a new calendar with the primary key. Does not add the calendar to the database.
	 *
	 * @param calendarId the primary key for the new calendar
	 * @return the new calendar
	 */
	@Override
	public Calendar create(long calendarId) {
		Calendar calendar = new CalendarImpl();

		calendar.setNew(true);
		calendar.setPrimaryKey(calendarId);

		String uuid = PortalUUIDUtil.generate();

		calendar.setUuid(uuid);

		calendar.setCompanyId(CompanyThreadLocal.getCompanyId());

		return calendar;
	}

	/**
	 * Removes the calendar with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param calendarId the primary key of the calendar
	 * @return the calendar that was removed
	 * @throws NoSuchCalendarException if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar remove(long calendarId) throws NoSuchCalendarException {
		return remove((Serializable)calendarId);
	}

	@Override
	protected Calendar removeImpl(Calendar calendar) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(calendar)) {
				calendar = (Calendar)session.get(
					CalendarImpl.class, calendar.getPrimaryKeyObj());
			}

			if ((calendar != null) && ctPersistenceHelper.isRemove(calendar)) {
				session.delete(calendar);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (calendar != null) {
			clearCache(calendar);
		}

		return calendar;
	}

	@Override
	public Calendar updateImpl(Calendar calendar) {
		boolean isNew = calendar.isNew();

		if (!(calendar instanceof CalendarModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(calendar.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(calendar);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in calendar proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Calendar implementation " +
					calendar.getClass());
		}

		CalendarModelImpl calendarModelImpl = (CalendarModelImpl)calendar;

		if (Validator.isNull(calendar.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			calendar.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (calendar.getCreateDate() == null)) {
			if (serviceContext == null) {
				calendar.setCreateDate(date);
			}
			else {
				calendar.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!calendarModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				calendar.setModifiedDate(date);
			}
			else {
				calendar.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(calendar)) {
				if (!isNew) {
					session.evict(
						CalendarImpl.class, calendar.getPrimaryKeyObj());
				}

				session.save(calendar);
			}
			else {
				calendar = (Calendar)session.merge(calendar);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(calendar, false);

		if (isNew) {
			calendar.setNew(false);
		}

		calendar.resetOriginalValues();

		return calendar;
	}

	/**
	 * Returns the calendar with the primary key or throws a <code>NoSuchCalendarException</code> if it could not be found.
	 *
	 * @param calendarId the primary key of the calendar
	 * @return the calendar
	 * @throws NoSuchCalendarException if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar findByPrimaryKey(long calendarId)
		throws NoSuchCalendarException {

		return findByPrimaryKey((Serializable)calendarId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the calendar with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param calendarId the primary key of the calendar
	 * @return the calendar, or <code>null</code> if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar fetchByPrimaryKey(long calendarId) {
		return fetchByPrimaryKey((Serializable)calendarId);
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
		return "calendarId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CALENDAR;
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
		return CalendarModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "Calendar";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("calendarResourceId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("timeZoneId");
		ctMergeColumnNames.add("color");
		ctMergeColumnNames.add("defaultCalendar");
		ctMergeColumnNames.add("enableComments");
		ctMergeColumnNames.add("enableRatings");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("calendarId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});
	}

	/**
	 * Initializes the calendar persistence.
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
			_SQL_SELECT_CALENDAR_WHERE, _SQL_COUNT_CALENDAR_WHERE,
			CalendarModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"calendar.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, Calendar::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(Calendar::getUuid), Calendar::getGroupId),
			_SQL_SELECT_CALENDAR_WHERE, "",
			new FinderColumn<>(
				"calendar.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, Calendar::getUuid),
			new FinderColumn<>(
				"calendar.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				Calendar::getGroupId));

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
				_SQL_SELECT_CALENDAR_WHERE, _SQL_COUNT_CALENDAR_WHERE,
				CalendarModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"calendar.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, Calendar::getUuid),
				new FinderColumn<>(
					"calendar.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Calendar::getCompanyId));

		_collectionPersistenceFinderByG_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "calendarResourceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "calendarResourceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"groupId", "calendarResourceId"}, false),
				_SQL_SELECT_CALENDAR_WHERE, _SQL_COUNT_CALENDAR_WHERE,
				CalendarModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"calendar.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, Calendar::getGroupId),
				new FinderColumn<>(
					"calendar.", "calendarResourceId", FinderColumn.Type.LONG,
					"=", true, true, Calendar::getCalendarResourceId));

		_collectionPersistenceFinderByG_C_D =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "calendarResourceId", "defaultCalendar"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"groupId", "calendarResourceId", "defaultCalendar"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_D",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"groupId", "calendarResourceId", "defaultCalendar"
					},
					false),
				_SQL_SELECT_CALENDAR_WHERE, _SQL_COUNT_CALENDAR_WHERE,
				CalendarModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"calendar.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, Calendar::getGroupId),
				new FinderColumn<>(
					"calendar.", "calendarResourceId", FinderColumn.Type.LONG,
					"=", true, true, Calendar::getCalendarResourceId),
				new FinderColumn<>(
					"calendar.", "defaultCalendar", FinderColumn.Type.BOOLEAN,
					"=", true, true, Calendar::isDefaultCalendar));

		CalendarUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CalendarUtil.setPersistence(null);

		entityCache.removeCache(CalendarImpl.class.getName());
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
		CalendarModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CALENDAR =
		"SELECT calendar FROM Calendar calendar";

	private static final String _SQL_SELECT_CALENDAR_WHERE =
		"SELECT calendar FROM Calendar calendar WHERE ";

	private static final String _SQL_COUNT_CALENDAR_WHERE =
		"SELECT COUNT(calendar) FROM Calendar calendar WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Calendar exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-101977283