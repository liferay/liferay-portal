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
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
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
import java.util.Iterator;
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
	extends BasePersistenceImpl<Calendar> implements CalendarPersistence {

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

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByUuid;
	private FinderPath _finderPathWithoutPaginationFindByUuid;
	private FinderPath _finderPathCountByUuid;

	/**
	 * Returns all the calendars where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching calendars
	 */
	@Override
	public List<Calendar> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the calendars where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @return the range of matching calendars
	 */
	@Override
	public List<Calendar> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the calendars where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendars
	 */
	@Override
	public List<Calendar> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Calendar> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the calendars where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
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

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					Calendar.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUuid;
					finderArgs = new Object[] {uuid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUuid;
				finderArgs = new Object[] {uuid, start, end, orderByComparator};
			}

			List<Calendar> list = null;

			if (useFinderCache) {
				list = (List<Calendar>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Calendar calendar : list) {
						if (!uuid.equals(calendar.getUuid())) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_CALENDAR_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_UUID_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CalendarModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					list = (List<Calendar>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
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

		Calendar calendar = fetchByUuid_First(uuid, orderByComparator);

		if (calendar != null) {
			return calendar;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchCalendarException(sb.toString());
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

		List<Calendar> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last calendar in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar
	 * @throws NoSuchCalendarException if a matching calendar could not be found
	 */
	@Override
	public Calendar findByUuid_Last(
			String uuid, OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		Calendar calendar = fetchByUuid_Last(uuid, orderByComparator);

		if (calendar != null) {
			return calendar;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchCalendarException(sb.toString());
	}

	/**
	 * Returns the last calendar in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar, or <code>null</code> if a matching calendar could not be found
	 */
	@Override
	public Calendar fetchByUuid_Last(
		String uuid, OrderByComparator<Calendar> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<Calendar> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the calendars before and after the current calendar in the ordered set where uuid = &#63;.
	 *
	 * @param calendarId the primary key of the current calendar
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar
	 * @throws NoSuchCalendarException if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar[] findByUuid_PrevAndNext(
			long calendarId, String uuid,
			OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		uuid = Objects.toString(uuid, "");

		Calendar calendar = findByPrimaryKey(calendarId);

		Session session = null;

		try {
			session = openSession();

			Calendar[] array = new CalendarImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, calendar, uuid, orderByComparator, true);

			array[1] = calendar;

			array[2] = getByUuid_PrevAndNext(
				session, calendar, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Calendar getByUuid_PrevAndNext(
		Session session, Calendar calendar, String uuid,
		OrderByComparator<Calendar> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CALENDAR_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(CalendarModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(calendar)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Calendar> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the calendars where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (Calendar calendar :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(calendar);
		}
	}

	/**
	 * Returns the number of calendars where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching calendars
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					Calendar.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CALENDAR_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_UUID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"calendar.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(calendar.uuid IS NULL OR calendar.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

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

		Calendar calendar = fetchByUUID_G(uuid, groupId);

		if (calendar == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("uuid=");
			sb.append(uuid);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCalendarException(sb.toString());
		}

		return calendar;
	}

	/**
	 * Returns the calendar where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching calendar, or <code>null</code> if a matching calendar could not be found
	 */
	@Override
	public Calendar fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
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

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					Calendar.class)) {

			uuid = Objects.toString(uuid, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {uuid, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByUUID_G, finderArgs, this);
			}

			if (result instanceof Calendar) {
				Calendar calendar = (Calendar)result;

				if (!Objects.equals(uuid, calendar.getUuid()) ||
					(groupId != calendar.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CALENDAR_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_G_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_G_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_G_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(groupId);

					List<Calendar> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						Calendar calendar = list.get(0);

						result = calendar;

						cacheResult(calendar);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			if (result instanceof List<?>) {
				return null;
			}
			else {
				return (Calendar)result;
			}
		}
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
		Calendar calendar = fetchByUUID_G(uuid, groupId);

		if (calendar == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"calendar.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(calendar.uuid IS NULL OR calendar.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"calendar.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the calendars where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching calendars
	 */
	@Override
	public List<Calendar> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the calendars where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @return the range of matching calendars
	 */
	@Override
	public List<Calendar> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the calendars where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendars
	 */
	@Override
	public List<Calendar> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Calendar> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the calendars where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
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

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					Calendar.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUuid_C;
					finderArgs = new Object[] {uuid, companyId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUuid_C;
				finderArgs = new Object[] {
					uuid, companyId, start, end, orderByComparator
				};
			}

			List<Calendar> list = null;

			if (useFinderCache) {
				list = (List<Calendar>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Calendar calendar : list) {
						if (!uuid.equals(calendar.getUuid()) ||
							(companyId != calendar.getCompanyId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_CALENDAR_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CalendarModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(companyId);

					list = (List<Calendar>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
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

		Calendar calendar = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (calendar != null) {
			return calendar;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCalendarException(sb.toString());
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

		List<Calendar> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last calendar in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar
	 * @throws NoSuchCalendarException if a matching calendar could not be found
	 */
	@Override
	public Calendar findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		Calendar calendar = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (calendar != null) {
			return calendar;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchCalendarException(sb.toString());
	}

	/**
	 * Returns the last calendar in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar, or <code>null</code> if a matching calendar could not be found
	 */
	@Override
	public Calendar fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<Calendar> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<Calendar> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the calendars before and after the current calendar in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param calendarId the primary key of the current calendar
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar
	 * @throws NoSuchCalendarException if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar[] findByUuid_C_PrevAndNext(
			long calendarId, String uuid, long companyId,
			OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		uuid = Objects.toString(uuid, "");

		Calendar calendar = findByPrimaryKey(calendarId);

		Session session = null;

		try {
			session = openSession();

			Calendar[] array = new CalendarImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, calendar, uuid, companyId, orderByComparator, true);

			array[1] = calendar;

			array[2] = getByUuid_C_PrevAndNext(
				session, calendar, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Calendar getByUuid_C_PrevAndNext(
		Session session, Calendar calendar, String uuid, long companyId,
		OrderByComparator<Calendar> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_CALENDAR_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(CalendarModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(calendar)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Calendar> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the calendars where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (Calendar calendar :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(calendar);
		}
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
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					Calendar.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CALENDAR_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(companyId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"calendar.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(calendar.uuid IS NULL OR calendar.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"calendar.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C;
	private FinderPath _finderPathWithoutPaginationFindByG_C;
	private FinderPath _finderPathCountByG_C;

	/**
	 * Returns all the calendars where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @return the matching calendars
	 */
	@Override
	public List<Calendar> findByG_C(long groupId, long calendarResourceId) {
		return findByG_C(
			groupId, calendarResourceId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the calendars where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @return the range of matching calendars
	 */
	@Override
	public List<Calendar> findByG_C(
		long groupId, long calendarResourceId, int start, int end) {

		return findByG_C(groupId, calendarResourceId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the calendars where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendars
	 */
	@Override
	public List<Calendar> findByG_C(
		long groupId, long calendarResourceId, int start, int end,
		OrderByComparator<Calendar> orderByComparator) {

		return findByG_C(
			groupId, calendarResourceId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the calendars where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
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

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					Calendar.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C;
					finderArgs = new Object[] {groupId, calendarResourceId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C;
				finderArgs = new Object[] {
					groupId, calendarResourceId, start, end, orderByComparator
				};
			}

			List<Calendar> list = null;

			if (useFinderCache) {
				list = (List<Calendar>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Calendar calendar : list) {
						if ((groupId != calendar.getGroupId()) ||
							(calendarResourceId !=
								calendar.getCalendarResourceId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_CALENDAR_WHERE);

				sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_CALENDARRESOURCEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CalendarModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(calendarResourceId);

					list = (List<Calendar>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
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

		Calendar calendar = fetchByG_C_First(
			groupId, calendarResourceId, orderByComparator);

		if (calendar != null) {
			return calendar;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", calendarResourceId=");
		sb.append(calendarResourceId);

		sb.append("}");

		throw new NoSuchCalendarException(sb.toString());
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

		List<Calendar> list = findByG_C(
			groupId, calendarResourceId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last calendar in the ordered set where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar
	 * @throws NoSuchCalendarException if a matching calendar could not be found
	 */
	@Override
	public Calendar findByG_C_Last(
			long groupId, long calendarResourceId,
			OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		Calendar calendar = fetchByG_C_Last(
			groupId, calendarResourceId, orderByComparator);

		if (calendar != null) {
			return calendar;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", calendarResourceId=");
		sb.append(calendarResourceId);

		sb.append("}");

		throw new NoSuchCalendarException(sb.toString());
	}

	/**
	 * Returns the last calendar in the ordered set where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar, or <code>null</code> if a matching calendar could not be found
	 */
	@Override
	public Calendar fetchByG_C_Last(
		long groupId, long calendarResourceId,
		OrderByComparator<Calendar> orderByComparator) {

		int count = countByG_C(groupId, calendarResourceId);

		if (count == 0) {
			return null;
		}

		List<Calendar> list = findByG_C(
			groupId, calendarResourceId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the calendars before and after the current calendar in the ordered set where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * @param calendarId the primary key of the current calendar
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar
	 * @throws NoSuchCalendarException if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar[] findByG_C_PrevAndNext(
			long calendarId, long groupId, long calendarResourceId,
			OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		Calendar calendar = findByPrimaryKey(calendarId);

		Session session = null;

		try {
			session = openSession();

			Calendar[] array = new CalendarImpl[3];

			array[0] = getByG_C_PrevAndNext(
				session, calendar, groupId, calendarResourceId,
				orderByComparator, true);

			array[1] = calendar;

			array[2] = getByG_C_PrevAndNext(
				session, calendar, groupId, calendarResourceId,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Calendar getByG_C_PrevAndNext(
		Session session, Calendar calendar, long groupId,
		long calendarResourceId, OrderByComparator<Calendar> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_CALENDAR_WHERE);

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CALENDARRESOURCEID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(CalendarModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(calendarResourceId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(calendar)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Calendar> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the calendars that the user has permission to view where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @return the matching calendars that the user has permission to view
	 */
	@Override
	public List<Calendar> filterFindByG_C(
		long groupId, long calendarResourceId) {

		return filterFindByG_C(
			groupId, calendarResourceId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the calendars that the user has permission to view where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @return the range of matching calendars that the user has permission to view
	 */
	@Override
	public List<Calendar> filterFindByG_C(
		long groupId, long calendarResourceId, int start, int end) {

		return filterFindByG_C(groupId, calendarResourceId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the calendars that the user has permissions to view where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
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

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C(
				groupId, calendarResourceId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C(
					groupId, calendarResourceId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CALENDAR_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_CALENDAR_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CALENDARRESOURCEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CALENDAR_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(CalendarModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CalendarModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Calendar.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CalendarImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CalendarImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(calendarResourceId);

			return (List<Calendar>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the calendars before and after the current calendar in the ordered set of calendars that the user has permission to view where groupId = &#63; and calendarResourceId = &#63;.
	 *
	 * @param calendarId the primary key of the current calendar
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar
	 * @throws NoSuchCalendarException if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar[] filterFindByG_C_PrevAndNext(
			long calendarId, long groupId, long calendarResourceId,
			OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_PrevAndNext(
				calendarId, groupId, calendarResourceId, orderByComparator);
		}

		Calendar calendar = findByPrimaryKey(calendarId);

		Session session = null;

		try {
			session = openSession();

			Calendar[] array = new CalendarImpl[3];

			array[0] = filterGetByG_C_PrevAndNext(
				session, calendar, groupId, calendarResourceId,
				orderByComparator, true);

			array[1] = calendar;

			array[2] = filterGetByG_C_PrevAndNext(
				session, calendar, groupId, calendarResourceId,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Calendar filterGetByG_C_PrevAndNext(
		Session session, Calendar calendar, long groupId,
		long calendarResourceId, OrderByComparator<Calendar> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CALENDAR_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_CALENDAR_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CALENDARRESOURCEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CALENDAR_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(CalendarModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CalendarModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Calendar.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CalendarImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CalendarImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(calendarResourceId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(calendar)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Calendar> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the calendars where groupId = &#63; and calendarResourceId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 */
	@Override
	public void removeByG_C(long groupId, long calendarResourceId) {
		for (Calendar calendar :
				findByG_C(
					groupId, calendarResourceId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(calendar);
		}
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
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					Calendar.class)) {

			FinderPath finderPath = _finderPathCountByG_C;

			Object[] finderArgs = new Object[] {groupId, calendarResourceId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CALENDAR_WHERE);

				sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_CALENDARRESOURCEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(calendarResourceId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
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
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C(groupId, calendarResourceId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Calendar> calendars = findByG_C(groupId, calendarResourceId);

			calendars = InlineSQLHelperUtil.filter(calendars, groupId);

			return calendars.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_CALENDAR_WHERE);

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CALENDARRESOURCEID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Calendar.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(calendarResourceId);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_C_GROUPID_2 =
		"calendar.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_CALENDARRESOURCEID_2 =
		"calendar.calendarResourceId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_D;
	private FinderPath _finderPathWithoutPaginationFindByG_C_D;
	private FinderPath _finderPathCountByG_C_D;

	/**
	 * Returns all the calendars where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @return the matching calendars
	 */
	@Override
	public List<Calendar> findByG_C_D(
		long groupId, long calendarResourceId, boolean defaultCalendar) {

		return findByG_C_D(
			groupId, calendarResourceId, defaultCalendar, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the calendars where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @return the range of matching calendars
	 */
	@Override
	public List<Calendar> findByG_C_D(
		long groupId, long calendarResourceId, boolean defaultCalendar,
		int start, int end) {

		return findByG_C_D(
			groupId, calendarResourceId, defaultCalendar, start, end, null);
	}

	/**
	 * Returns an ordered range of all the calendars where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching calendars
	 */
	@Override
	public List<Calendar> findByG_C_D(
		long groupId, long calendarResourceId, boolean defaultCalendar,
		int start, int end, OrderByComparator<Calendar> orderByComparator) {

		return findByG_C_D(
			groupId, calendarResourceId, defaultCalendar, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the calendars where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
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

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					Calendar.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_D;
					finderArgs = new Object[] {
						groupId, calendarResourceId, defaultCalendar
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_D;
				finderArgs = new Object[] {
					groupId, calendarResourceId, defaultCalendar, start, end,
					orderByComparator
				};
			}

			List<Calendar> list = null;

			if (useFinderCache) {
				list = (List<Calendar>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Calendar calendar : list) {
						if ((groupId != calendar.getGroupId()) ||
							(calendarResourceId !=
								calendar.getCalendarResourceId()) ||
							(defaultCalendar != calendar.isDefaultCalendar())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_CALENDAR_WHERE);

				sb.append(_FINDER_COLUMN_G_C_D_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_D_CALENDARRESOURCEID_2);

				sb.append(_FINDER_COLUMN_G_C_D_DEFAULTCALENDAR_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CalendarModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(calendarResourceId);

					queryPos.add(defaultCalendar);

					list = (List<Calendar>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
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

		Calendar calendar = fetchByG_C_D_First(
			groupId, calendarResourceId, defaultCalendar, orderByComparator);

		if (calendar != null) {
			return calendar;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", calendarResourceId=");
		sb.append(calendarResourceId);

		sb.append(", defaultCalendar=");
		sb.append(defaultCalendar);

		sb.append("}");

		throw new NoSuchCalendarException(sb.toString());
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

		List<Calendar> list = findByG_C_D(
			groupId, calendarResourceId, defaultCalendar, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last calendar in the ordered set where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar
	 * @throws NoSuchCalendarException if a matching calendar could not be found
	 */
	@Override
	public Calendar findByG_C_D_Last(
			long groupId, long calendarResourceId, boolean defaultCalendar,
			OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		Calendar calendar = fetchByG_C_D_Last(
			groupId, calendarResourceId, defaultCalendar, orderByComparator);

		if (calendar != null) {
			return calendar;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", calendarResourceId=");
		sb.append(calendarResourceId);

		sb.append(", defaultCalendar=");
		sb.append(defaultCalendar);

		sb.append("}");

		throw new NoSuchCalendarException(sb.toString());
	}

	/**
	 * Returns the last calendar in the ordered set where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar, or <code>null</code> if a matching calendar could not be found
	 */
	@Override
	public Calendar fetchByG_C_D_Last(
		long groupId, long calendarResourceId, boolean defaultCalendar,
		OrderByComparator<Calendar> orderByComparator) {

		int count = countByG_C_D(groupId, calendarResourceId, defaultCalendar);

		if (count == 0) {
			return null;
		}

		List<Calendar> list = findByG_C_D(
			groupId, calendarResourceId, defaultCalendar, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the calendars before and after the current calendar in the ordered set where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * @param calendarId the primary key of the current calendar
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar
	 * @throws NoSuchCalendarException if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar[] findByG_C_D_PrevAndNext(
			long calendarId, long groupId, long calendarResourceId,
			boolean defaultCalendar,
			OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		Calendar calendar = findByPrimaryKey(calendarId);

		Session session = null;

		try {
			session = openSession();

			Calendar[] array = new CalendarImpl[3];

			array[0] = getByG_C_D_PrevAndNext(
				session, calendar, groupId, calendarResourceId, defaultCalendar,
				orderByComparator, true);

			array[1] = calendar;

			array[2] = getByG_C_D_PrevAndNext(
				session, calendar, groupId, calendarResourceId, defaultCalendar,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Calendar getByG_C_D_PrevAndNext(
		Session session, Calendar calendar, long groupId,
		long calendarResourceId, boolean defaultCalendar,
		OrderByComparator<Calendar> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_CALENDAR_WHERE);

		sb.append(_FINDER_COLUMN_G_C_D_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_D_CALENDARRESOURCEID_2);

		sb.append(_FINDER_COLUMN_G_C_D_DEFAULTCALENDAR_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(CalendarModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(calendarResourceId);

		queryPos.add(defaultCalendar);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(calendar)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Calendar> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the calendars that the user has permission to view where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @return the matching calendars that the user has permission to view
	 */
	@Override
	public List<Calendar> filterFindByG_C_D(
		long groupId, long calendarResourceId, boolean defaultCalendar) {

		return filterFindByG_C_D(
			groupId, calendarResourceId, defaultCalendar, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the calendars that the user has permission to view where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @return the range of matching calendars that the user has permission to view
	 */
	@Override
	public List<Calendar> filterFindByG_C_D(
		long groupId, long calendarResourceId, boolean defaultCalendar,
		int start, int end) {

		return filterFindByG_C_D(
			groupId, calendarResourceId, defaultCalendar, start, end, null);
	}

	/**
	 * Returns an ordered range of all the calendars that the user has permissions to view where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
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

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_D(
				groupId, calendarResourceId, defaultCalendar, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_C_D(
					groupId, calendarResourceId, defaultCalendar,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CALENDAR_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_CALENDAR_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_D_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_D_CALENDARRESOURCEID_2);

		sb.append(_FINDER_COLUMN_G_C_D_DEFAULTCALENDAR_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CALENDAR_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(CalendarModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CalendarModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Calendar.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CalendarImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CalendarImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(calendarResourceId);

			queryPos.add(defaultCalendar);

			return (List<Calendar>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the calendars before and after the current calendar in the ordered set of calendars that the user has permission to view where groupId = &#63; and calendarResourceId = &#63; and defaultCalendar = &#63;.
	 *
	 * @param calendarId the primary key of the current calendar
	 * @param groupId the group ID
	 * @param calendarResourceId the calendar resource ID
	 * @param defaultCalendar the default calendar
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next calendar
	 * @throws NoSuchCalendarException if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar[] filterFindByG_C_D_PrevAndNext(
			long calendarId, long groupId, long calendarResourceId,
			boolean defaultCalendar,
			OrderByComparator<Calendar> orderByComparator)
		throws NoSuchCalendarException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_C_D_PrevAndNext(
				calendarId, groupId, calendarResourceId, defaultCalendar,
				orderByComparator);
		}

		Calendar calendar = findByPrimaryKey(calendarId);

		Session session = null;

		try {
			session = openSession();

			Calendar[] array = new CalendarImpl[3];

			array[0] = filterGetByG_C_D_PrevAndNext(
				session, calendar, groupId, calendarResourceId, defaultCalendar,
				orderByComparator, true);

			array[1] = calendar;

			array[2] = filterGetByG_C_D_PrevAndNext(
				session, calendar, groupId, calendarResourceId, defaultCalendar,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Calendar filterGetByG_C_D_PrevAndNext(
		Session session, Calendar calendar, long groupId,
		long calendarResourceId, boolean defaultCalendar,
		OrderByComparator<Calendar> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CALENDAR_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_CALENDAR_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_C_D_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_D_CALENDARRESOURCEID_2);

		sb.append(_FINDER_COLUMN_G_C_D_DEFAULTCALENDAR_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_CALENDAR_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(CalendarModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(CalendarModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Calendar.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, CalendarImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, CalendarImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(calendarResourceId);

		queryPos.add(defaultCalendar);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(calendar)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Calendar> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
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

		for (Calendar calendar :
				findByG_C_D(
					groupId, calendarResourceId, defaultCalendar,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(calendar);
		}
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

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					Calendar.class)) {

			FinderPath finderPath = _finderPathCountByG_C_D;

			Object[] finderArgs = new Object[] {
				groupId, calendarResourceId, defaultCalendar
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_CALENDAR_WHERE);

				sb.append(_FINDER_COLUMN_G_C_D_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_D_CALENDARRESOURCEID_2);

				sb.append(_FINDER_COLUMN_G_C_D_DEFAULTCALENDAR_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(calendarResourceId);

					queryPos.add(defaultCalendar);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
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

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_C_D(groupId, calendarResourceId, defaultCalendar);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Calendar> calendars = findByG_C_D(
				groupId, calendarResourceId, defaultCalendar);

			calendars = InlineSQLHelperUtil.filter(calendars, groupId);

			return calendars.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_CALENDAR_WHERE);

		sb.append(_FINDER_COLUMN_G_C_D_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_D_CALENDARRESOURCEID_2);

		sb.append(_FINDER_COLUMN_G_C_D_DEFAULTCALENDAR_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Calendar.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(calendarResourceId);

			queryPos.add(defaultCalendar);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_C_D_GROUPID_2 =
		"calendar.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_D_CALENDARRESOURCEID_2 =
		"calendar.calendarResourceId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_D_DEFAULTCALENDAR_2 =
		"calendar.defaultCalendar = ?";

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
	 * Caches the calendar in the entity cache if it is enabled.
	 *
	 * @param calendar the calendar
	 */
	@Override
	public void cacheResult(Calendar calendar) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					calendar.getCtCollectionId())) {

			entityCache.putResult(
				CalendarImpl.class, calendar.getPrimaryKey(), calendar);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {calendar.getUuid(), calendar.getGroupId()},
				calendar);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the calendars in the entity cache if it is enabled.
	 *
	 * @param calendars the calendars
	 */
	@Override
	public void cacheResult(List<Calendar> calendars) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (calendars.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (Calendar calendar : calendars) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						calendar.getCtCollectionId())) {

				if (entityCache.getResult(
						CalendarImpl.class, calendar.getPrimaryKey()) == null) {

					cacheResult(calendar);
				}
			}
		}
	}

	/**
	 * Clears the cache for all calendars.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CalendarImpl.class);

		finderCache.clearCache(CalendarImpl.class);
	}

	/**
	 * Clears the cache for the calendar.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(Calendar calendar) {
		entityCache.removeResult(CalendarImpl.class, calendar);
	}

	@Override
	public void clearCache(List<Calendar> calendars) {
		for (Calendar calendar : calendars) {
			entityCache.removeResult(CalendarImpl.class, calendar);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CalendarImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(CalendarImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CalendarModelImpl calendarModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					calendarModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				calendarModelImpl.getUuid(), calendarModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, calendarModelImpl);
		}
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

	/**
	 * Removes the calendar with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the calendar
	 * @return the calendar that was removed
	 * @throws NoSuchCalendarException if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar remove(Serializable primaryKey)
		throws NoSuchCalendarException {

		Session session = null;

		try {
			session = openSession();

			Calendar calendar = (Calendar)session.get(
				CalendarImpl.class, primaryKey);

			if (calendar == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCalendarException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(calendar);
		}
		catch (NoSuchCalendarException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
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

		entityCache.putResult(
			CalendarImpl.class, calendarModelImpl, false, true);

		cacheUniqueFindersCache(calendarModelImpl);

		if (isNew) {
			calendar.setNew(false);
		}

		calendar.resetOriginalValues();

		return calendar;
	}

	/**
	 * Returns the calendar with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the calendar
	 * @return the calendar
	 * @throws NoSuchCalendarException if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCalendarException {

		Calendar calendar = fetchByPrimaryKey(primaryKey);

		if (calendar == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCalendarException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

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

	/**
	 * Returns the calendar with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the calendar
	 * @return the calendar, or <code>null</code> if a calendar with the primary key could not be found
	 */
	@Override
	public Calendar fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(Calendar.class, primaryKey)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		Calendar calendar = (Calendar)entityCache.getResult(
			CalendarImpl.class, primaryKey);

		if (calendar != null) {
			return calendar;
		}

		Session session = null;

		try {
			session = openSession();

			calendar = (Calendar)session.get(CalendarImpl.class, primaryKey);

			if (calendar != null) {
				cacheResult(calendar);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return calendar;
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
	public Map<Serializable, Calendar> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(Calendar.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, Calendar> map = new HashMap<Serializable, Calendar>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			Calendar calendar = fetchByPrimaryKey(primaryKey);

			if (calendar != null) {
				map.put(primaryKey, calendar);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						Calendar.class, primaryKey)) {

				Calendar calendar = (Calendar)entityCache.getResult(
					CalendarImpl.class, primaryKey);

				if (calendar == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, calendar);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		if ((databaseInMaxParameters > 0) &&
			(primaryKeys.size() > databaseInMaxParameters)) {

			Iterator<Serializable> iterator = primaryKeys.iterator();

			while (iterator.hasNext()) {
				Set<Serializable> page = new HashSet<>();

				for (int i = 0;
					 (i < databaseInMaxParameters) && iterator.hasNext(); i++) {

					page.add(iterator.next());
				}

				map.putAll(fetchByPrimaryKeys(page));
			}

			return map;
		}

		StringBundler sb = new StringBundler((primaryKeys.size() * 2) + 1);

		sb.append(getSelectSQL());
		sb.append(" WHERE ");
		sb.append(getPKDBName());
		sb.append(" IN (");

		for (Serializable primaryKey : primaryKeys) {
			sb.append((long)primaryKey);

			sb.append(",");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(")");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			for (Calendar calendar : (List<Calendar>)query.list()) {
				map.put(calendar.getPrimaryKeyObj(), calendar);

				cacheResult(calendar);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return map;
	}

	/**
	 * Returns all the calendars.
	 *
	 * @return the calendars
	 */
	@Override
	public List<Calendar> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the calendars.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @return the range of calendars
	 */
	@Override
	public List<Calendar> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the calendars.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of calendars
	 */
	@Override
	public List<Calendar> findAll(
		int start, int end, OrderByComparator<Calendar> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the calendars.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CalendarModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of calendars
	 * @param end the upper bound of the range of calendars (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of calendars
	 */
	@Override
	public List<Calendar> findAll(
		int start, int end, OrderByComparator<Calendar> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					Calendar.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindAll;
					finderArgs = FINDER_ARGS_EMPTY;
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindAll;
				finderArgs = new Object[] {start, end, orderByComparator};
			}

			List<Calendar> list = null;

			if (useFinderCache) {
				list = (List<Calendar>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_CALENDAR);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_CALENDAR;

					sql = sql.concat(CalendarModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<Calendar>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Removes all the calendars from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (Calendar calendar : findAll()) {
			remove(calendar);
		}
	}

	/**
	 * Returns the number of calendars.
	 *
	 * @return the number of calendars
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					Calendar.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_CALENDAR);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathCountAll, FINDER_ARGS_EMPTY, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
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
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"uuid_"}, true);

		_finderPathWithoutPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			true);

		_finderPathCountByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			false);

		_finderPathFetchByUUID_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "groupId"}, true);

		_finderPathWithPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathWithoutPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathCountByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, false);

		_finderPathWithPaginationFindByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "calendarResourceId"}, true);

		_finderPathWithoutPaginationFindByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "calendarResourceId"}, true);

		_finderPathCountByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "calendarResourceId"}, false);

		_finderPathWithPaginationFindByG_C_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_D",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "calendarResourceId", "defaultCalendar"},
			true);

		_finderPathWithoutPaginationFindByG_C_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_D",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"groupId", "calendarResourceId", "defaultCalendar"},
			true);

		_finderPathCountByG_C_D = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_D",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"groupId", "calendarResourceId", "defaultCalendar"},
			false);

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

	private static final String _SQL_SELECT_CALENDAR =
		"SELECT calendar FROM Calendar calendar";

	private static final String _SQL_SELECT_CALENDAR_WHERE =
		"SELECT calendar FROM Calendar calendar WHERE ";

	private static final String _SQL_COUNT_CALENDAR =
		"SELECT COUNT(calendar) FROM Calendar calendar";

	private static final String _SQL_COUNT_CALENDAR_WHERE =
		"SELECT COUNT(calendar) FROM Calendar calendar WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"calendar.calendarId";

	private static final String _FILTER_SQL_SELECT_CALENDAR_WHERE =
		"SELECT DISTINCT {calendar.*} FROM Calendar calendar WHERE ";

	private static final String
		_FILTER_SQL_SELECT_CALENDAR_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {Calendar.*} FROM (SELECT DISTINCT calendar.calendarId FROM Calendar calendar WHERE ";

	private static final String
		_FILTER_SQL_SELECT_CALENDAR_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN Calendar ON TEMP_TABLE.calendarId = Calendar.calendarId";

	private static final String _FILTER_SQL_COUNT_CALENDAR_WHERE =
		"SELECT COUNT(DISTINCT calendar.calendarId) AS COUNT_VALUE FROM Calendar calendar WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "calendar";

	private static final String _FILTER_ENTITY_TABLE = "Calendar";

	private static final String _ORDER_BY_ENTITY_ALIAS = "calendar.";

	private static final String _ORDER_BY_ENTITY_TABLE = "Calendar.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No Calendar exists with the primary key ";

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