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
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
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
	extends BasePersistenceImpl<CalendarBooking>
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

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByUuid;
	private FinderPath _finderPathWithoutPaginationFindByUuid;
	private FinderPath _finderPathCountByUuid;

	/**
	 * Returns all the calendar bookings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<CalendarBooking> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
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
	@Override
	public List<CalendarBooking> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
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
	@Override
	public List<CalendarBooking> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

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

			List<CalendarBooking> list = null;

			if (useFinderCache) {
				list = (List<CalendarBooking>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CalendarBooking calendarBooking : list) {
						if (!uuid.equals(calendarBooking.getUuid())) {
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

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

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
					sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
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

					list = (List<CalendarBooking>)QueryUtil.list(
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

		CalendarBooking calendarBooking = fetchByUuid_First(
			uuid, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
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

		List<CalendarBooking> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last calendar booking in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByUuid_Last(
			String uuid, OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = fetchByUuid_Last(
			uuid, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
	}

	/**
	 * Returns the last calendar booking in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByUuid_Last(
		String uuid, OrderByComparator<CalendarBooking> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<CalendarBooking> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public CalendarBooking[] findByUuid_PrevAndNext(
			long calendarBookingId, String uuid,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		uuid = Objects.toString(uuid, "");

		CalendarBooking calendarBooking = findByPrimaryKey(calendarBookingId);

		Session session = null;

		try {
			session = openSession();

			CalendarBooking[] array = new CalendarBookingImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, calendarBooking, uuid, orderByComparator, true);

			array[1] = calendarBooking;

			array[2] = getByUuid_PrevAndNext(
				session, calendarBooking, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CalendarBooking getByUuid_PrevAndNext(
		Session session, CalendarBooking calendarBooking, String uuid,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

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
			sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(
						calendarBooking)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CalendarBooking> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the calendar bookings where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (CalendarBooking calendarBooking :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(calendarBooking);
		}
	}

	/**
	 * Returns the number of calendar bookings where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CALENDARBOOKING_WHERE);

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
		"calendarBooking.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(calendarBooking.uuid IS NULL OR calendarBooking.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

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

		CalendarBooking calendarBooking = fetchByUUID_G(uuid, groupId);

		if (calendarBooking == null) {
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

			throw new NoSuchBookingException(sb.toString());
		}

		return calendarBooking;
	}

	/**
	 * Returns the calendar booking where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
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

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

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

			if (result instanceof CalendarBooking) {
				CalendarBooking calendarBooking = (CalendarBooking)result;

				if (!Objects.equals(uuid, calendarBooking.getUuid()) ||
					(groupId != calendarBooking.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

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

					List<CalendarBooking> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						CalendarBooking calendarBooking = list.get(0);

						result = calendarBooking;

						cacheResult(calendarBooking);
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
				return (CalendarBooking)result;
			}
		}
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
		CalendarBooking calendarBooking = fetchByUUID_G(uuid, groupId);

		if (calendarBooking == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"calendarBooking.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(calendarBooking.uuid IS NULL OR calendarBooking.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"calendarBooking.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the calendar bookings where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<CalendarBooking> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
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
	@Override
	public List<CalendarBooking> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
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
	@Override
	public List<CalendarBooking> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

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

			List<CalendarBooking> list = null;

			if (useFinderCache) {
				list = (List<CalendarBooking>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CalendarBooking calendarBooking : list) {
						if (!uuid.equals(calendarBooking.getUuid()) ||
							(companyId != calendarBooking.getCompanyId())) {

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

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

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
					sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
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

					list = (List<CalendarBooking>)QueryUtil.list(
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

		CalendarBooking calendarBooking = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
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

		List<CalendarBooking> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public CalendarBooking findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
	}

	/**
	 * Returns the last calendar booking in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<CalendarBooking> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public CalendarBooking[] findByUuid_C_PrevAndNext(
			long calendarBookingId, String uuid, long companyId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		uuid = Objects.toString(uuid, "");

		CalendarBooking calendarBooking = findByPrimaryKey(calendarBookingId);

		Session session = null;

		try {
			session = openSession();

			CalendarBooking[] array = new CalendarBookingImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, calendarBooking, uuid, companyId, orderByComparator,
				true);

			array[1] = calendarBooking;

			array[2] = getByUuid_C_PrevAndNext(
				session, calendarBooking, uuid, companyId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CalendarBooking getByUuid_C_PrevAndNext(
		Session session, CalendarBooking calendarBooking, String uuid,
		long companyId, OrderByComparator<CalendarBooking> orderByComparator,
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

		sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

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
			sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(
						calendarBooking)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CalendarBooking> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the calendar bookings where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (CalendarBooking calendarBooking :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(calendarBooking);
		}
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
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CALENDARBOOKING_WHERE);

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
		"calendarBooking.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(calendarBooking.uuid IS NULL OR calendarBooking.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"calendarBooking.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCalendarId;
	private FinderPath _finderPathWithoutPaginationFindByCalendarId;
	private FinderPath _finderPathCountByCalendarId;

	/**
	 * Returns all the calendar bookings where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @return the matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByCalendarId(long calendarId) {
		return findByCalendarId(
			calendarId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<CalendarBooking> findByCalendarId(
		long calendarId, int start, int end) {

		return findByCalendarId(calendarId, start, end, null);
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
	@Override
	public List<CalendarBooking> findByCalendarId(
		long calendarId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return findByCalendarId(
			calendarId, start, end, orderByComparator, true);
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
	@Override
	public List<CalendarBooking> findByCalendarId(
		long calendarId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByCalendarId;
					finderArgs = new Object[] {calendarId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCalendarId;
				finderArgs = new Object[] {
					calendarId, start, end, orderByComparator
				};
			}

			List<CalendarBooking> list = null;

			if (useFinderCache) {
				list = (List<CalendarBooking>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CalendarBooking calendarBooking : list) {
						if (calendarId != calendarBooking.getCalendarId()) {
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

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

				sb.append(_FINDER_COLUMN_CALENDARID_CALENDARID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(calendarId);

					list = (List<CalendarBooking>)QueryUtil.list(
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

		CalendarBooking calendarBooking = fetchByCalendarId_First(
			calendarId, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("calendarId=");
		sb.append(calendarId);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
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

		List<CalendarBooking> list = findByCalendarId(
			calendarId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last calendar booking in the ordered set where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByCalendarId_Last(
			long calendarId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = fetchByCalendarId_Last(
			calendarId, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("calendarId=");
		sb.append(calendarId);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
	}

	/**
	 * Returns the last calendar booking in the ordered set where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByCalendarId_Last(
		long calendarId, OrderByComparator<CalendarBooking> orderByComparator) {

		int count = countByCalendarId(calendarId);

		if (count == 0) {
			return null;
		}

		List<CalendarBooking> list = findByCalendarId(
			calendarId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public CalendarBooking[] findByCalendarId_PrevAndNext(
			long calendarBookingId, long calendarId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = findByPrimaryKey(calendarBookingId);

		Session session = null;

		try {
			session = openSession();

			CalendarBooking[] array = new CalendarBookingImpl[3];

			array[0] = getByCalendarId_PrevAndNext(
				session, calendarBooking, calendarId, orderByComparator, true);

			array[1] = calendarBooking;

			array[2] = getByCalendarId_PrevAndNext(
				session, calendarBooking, calendarId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CalendarBooking getByCalendarId_PrevAndNext(
		Session session, CalendarBooking calendarBooking, long calendarId,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

		sb.append(_FINDER_COLUMN_CALENDARID_CALENDARID_2);

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
			sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(calendarId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						calendarBooking)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CalendarBooking> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the calendar bookings where calendarId = &#63; from the database.
	 *
	 * @param calendarId the calendar ID
	 */
	@Override
	public void removeByCalendarId(long calendarId) {
		for (CalendarBooking calendarBooking :
				findByCalendarId(
					calendarId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(calendarBooking);
		}
	}

	/**
	 * Returns the number of calendar bookings where calendarId = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByCalendarId(long calendarId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			FinderPath finderPath = _finderPathCountByCalendarId;

			Object[] finderArgs = new Object[] {calendarId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CALENDARBOOKING_WHERE);

				sb.append(_FINDER_COLUMN_CALENDARID_CALENDARID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(calendarId);

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

	private static final String _FINDER_COLUMN_CALENDARID_CALENDARID_2 =
		"calendarBooking.calendarId = ?";

	private FinderPath _finderPathWithPaginationFindByCalendarResourceId;
	private FinderPath _finderPathWithoutPaginationFindByCalendarResourceId;
	private FinderPath _finderPathCountByCalendarResourceId;

	/**
	 * Returns all the calendar bookings where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @return the matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByCalendarResourceId(
		long calendarResourceId) {

		return findByCalendarResourceId(
			calendarResourceId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<CalendarBooking> findByCalendarResourceId(
		long calendarResourceId, int start, int end) {

		return findByCalendarResourceId(calendarResourceId, start, end, null);
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
	@Override
	public List<CalendarBooking> findByCalendarResourceId(
		long calendarResourceId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return findByCalendarResourceId(
			calendarResourceId, start, end, orderByComparator, true);
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
	@Override
	public List<CalendarBooking> findByCalendarResourceId(
		long calendarResourceId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByCalendarResourceId;
					finderArgs = new Object[] {calendarResourceId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCalendarResourceId;
				finderArgs = new Object[] {
					calendarResourceId, start, end, orderByComparator
				};
			}

			List<CalendarBooking> list = null;

			if (useFinderCache) {
				list = (List<CalendarBooking>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CalendarBooking calendarBooking : list) {
						if (calendarResourceId !=
								calendarBooking.getCalendarResourceId()) {

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

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

				sb.append(
					_FINDER_COLUMN_CALENDARRESOURCEID_CALENDARRESOURCEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(calendarResourceId);

					list = (List<CalendarBooking>)QueryUtil.list(
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

		CalendarBooking calendarBooking = fetchByCalendarResourceId_First(
			calendarResourceId, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("calendarResourceId=");
		sb.append(calendarResourceId);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
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

		List<CalendarBooking> list = findByCalendarResourceId(
			calendarResourceId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last calendar booking in the ordered set where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByCalendarResourceId_Last(
			long calendarResourceId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = fetchByCalendarResourceId_Last(
			calendarResourceId, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("calendarResourceId=");
		sb.append(calendarResourceId);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
	}

	/**
	 * Returns the last calendar booking in the ordered set where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByCalendarResourceId_Last(
		long calendarResourceId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		int count = countByCalendarResourceId(calendarResourceId);

		if (count == 0) {
			return null;
		}

		List<CalendarBooking> list = findByCalendarResourceId(
			calendarResourceId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public CalendarBooking[] findByCalendarResourceId_PrevAndNext(
			long calendarBookingId, long calendarResourceId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = findByPrimaryKey(calendarBookingId);

		Session session = null;

		try {
			session = openSession();

			CalendarBooking[] array = new CalendarBookingImpl[3];

			array[0] = getByCalendarResourceId_PrevAndNext(
				session, calendarBooking, calendarResourceId, orderByComparator,
				true);

			array[1] = calendarBooking;

			array[2] = getByCalendarResourceId_PrevAndNext(
				session, calendarBooking, calendarResourceId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CalendarBooking getByCalendarResourceId_PrevAndNext(
		Session session, CalendarBooking calendarBooking,
		long calendarResourceId,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

		sb.append(_FINDER_COLUMN_CALENDARRESOURCEID_CALENDARRESOURCEID_2);

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
			sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(calendarResourceId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						calendarBooking)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CalendarBooking> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the calendar bookings where calendarResourceId = &#63; from the database.
	 *
	 * @param calendarResourceId the calendar resource ID
	 */
	@Override
	public void removeByCalendarResourceId(long calendarResourceId) {
		for (CalendarBooking calendarBooking :
				findByCalendarResourceId(
					calendarResourceId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(calendarBooking);
		}
	}

	/**
	 * Returns the number of calendar bookings where calendarResourceId = &#63;.
	 *
	 * @param calendarResourceId the calendar resource ID
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByCalendarResourceId(long calendarResourceId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			FinderPath finderPath = _finderPathCountByCalendarResourceId;

			Object[] finderArgs = new Object[] {calendarResourceId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CALENDARBOOKING_WHERE);

				sb.append(
					_FINDER_COLUMN_CALENDARRESOURCEID_CALENDARRESOURCEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

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

	private static final String
		_FINDER_COLUMN_CALENDARRESOURCEID_CALENDARRESOURCEID_2 =
			"calendarBooking.calendarResourceId = ?";

	private FinderPath _finderPathWithPaginationFindByParentCalendarBookingId;
	private FinderPath
		_finderPathWithoutPaginationFindByParentCalendarBookingId;
	private FinderPath _finderPathCountByParentCalendarBookingId;

	/**
	 * Returns all the calendar bookings where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByParentCalendarBookingId(
		long parentCalendarBookingId) {

		return findByParentCalendarBookingId(
			parentCalendarBookingId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
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
	@Override
	public List<CalendarBooking> findByParentCalendarBookingId(
		long parentCalendarBookingId, int start, int end) {

		return findByParentCalendarBookingId(
			parentCalendarBookingId, start, end, null);
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
	@Override
	public List<CalendarBooking> findByParentCalendarBookingId(
		long parentCalendarBookingId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return findByParentCalendarBookingId(
			parentCalendarBookingId, start, end, orderByComparator, true);
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
	@Override
	public List<CalendarBooking> findByParentCalendarBookingId(
		long parentCalendarBookingId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByParentCalendarBookingId;
					finderArgs = new Object[] {parentCalendarBookingId};
				}
			}
			else if (useFinderCache) {
				finderPath =
					_finderPathWithPaginationFindByParentCalendarBookingId;
				finderArgs = new Object[] {
					parentCalendarBookingId, start, end, orderByComparator
				};
			}

			List<CalendarBooking> list = null;

			if (useFinderCache) {
				list = (List<CalendarBooking>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CalendarBooking calendarBooking : list) {
						if (parentCalendarBookingId !=
								calendarBooking.getParentCalendarBookingId()) {

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

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

				sb.append(
					_FINDER_COLUMN_PARENTCALENDARBOOKINGID_PARENTCALENDARBOOKINGID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentCalendarBookingId);

					list = (List<CalendarBooking>)QueryUtil.list(
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

		CalendarBooking calendarBooking = fetchByParentCalendarBookingId_First(
			parentCalendarBookingId, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentCalendarBookingId=");
		sb.append(parentCalendarBookingId);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
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

		List<CalendarBooking> list = findByParentCalendarBookingId(
			parentCalendarBookingId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last calendar booking in the ordered set where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByParentCalendarBookingId_Last(
			long parentCalendarBookingId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = fetchByParentCalendarBookingId_Last(
			parentCalendarBookingId, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentCalendarBookingId=");
		sb.append(parentCalendarBookingId);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
	}

	/**
	 * Returns the last calendar booking in the ordered set where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByParentCalendarBookingId_Last(
		long parentCalendarBookingId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		int count = countByParentCalendarBookingId(parentCalendarBookingId);

		if (count == 0) {
			return null;
		}

		List<CalendarBooking> list = findByParentCalendarBookingId(
			parentCalendarBookingId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public CalendarBooking[] findByParentCalendarBookingId_PrevAndNext(
			long calendarBookingId, long parentCalendarBookingId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = findByPrimaryKey(calendarBookingId);

		Session session = null;

		try {
			session = openSession();

			CalendarBooking[] array = new CalendarBookingImpl[3];

			array[0] = getByParentCalendarBookingId_PrevAndNext(
				session, calendarBooking, parentCalendarBookingId,
				orderByComparator, true);

			array[1] = calendarBooking;

			array[2] = getByParentCalendarBookingId_PrevAndNext(
				session, calendarBooking, parentCalendarBookingId,
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

	protected CalendarBooking getByParentCalendarBookingId_PrevAndNext(
		Session session, CalendarBooking calendarBooking,
		long parentCalendarBookingId,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

		sb.append(
			_FINDER_COLUMN_PARENTCALENDARBOOKINGID_PARENTCALENDARBOOKINGID_2);

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
			sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(parentCalendarBookingId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						calendarBooking)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CalendarBooking> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the calendar bookings where parentCalendarBookingId = &#63; from the database.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 */
	@Override
	public void removeByParentCalendarBookingId(long parentCalendarBookingId) {
		for (CalendarBooking calendarBooking :
				findByParentCalendarBookingId(
					parentCalendarBookingId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(calendarBooking);
		}
	}

	/**
	 * Returns the number of calendar bookings where parentCalendarBookingId = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByParentCalendarBookingId(long parentCalendarBookingId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			FinderPath finderPath = _finderPathCountByParentCalendarBookingId;

			Object[] finderArgs = new Object[] {parentCalendarBookingId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CALENDARBOOKING_WHERE);

				sb.append(
					_FINDER_COLUMN_PARENTCALENDARBOOKINGID_PARENTCALENDARBOOKINGID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentCalendarBookingId);

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

	private static final String
		_FINDER_COLUMN_PARENTCALENDARBOOKINGID_PARENTCALENDARBOOKINGID_2 =
			"calendarBooking.parentCalendarBookingId = ?";

	private FinderPath
		_finderPathWithPaginationFindByRecurringCalendarBookingId;
	private FinderPath
		_finderPathWithoutPaginationFindByRecurringCalendarBookingId;
	private FinderPath _finderPathCountByRecurringCalendarBookingId;

	/**
	 * Returns all the calendar bookings where recurringCalendarBookingId = &#63;.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @return the matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByRecurringCalendarBookingId(
		long recurringCalendarBookingId) {

		return findByRecurringCalendarBookingId(
			recurringCalendarBookingId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
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
	@Override
	public List<CalendarBooking> findByRecurringCalendarBookingId(
		long recurringCalendarBookingId, int start, int end) {

		return findByRecurringCalendarBookingId(
			recurringCalendarBookingId, start, end, null);
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
	@Override
	public List<CalendarBooking> findByRecurringCalendarBookingId(
		long recurringCalendarBookingId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return findByRecurringCalendarBookingId(
			recurringCalendarBookingId, start, end, orderByComparator, true);
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
	@Override
	public List<CalendarBooking> findByRecurringCalendarBookingId(
		long recurringCalendarBookingId, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByRecurringCalendarBookingId;
					finderArgs = new Object[] {recurringCalendarBookingId};
				}
			}
			else if (useFinderCache) {
				finderPath =
					_finderPathWithPaginationFindByRecurringCalendarBookingId;
				finderArgs = new Object[] {
					recurringCalendarBookingId, start, end, orderByComparator
				};
			}

			List<CalendarBooking> list = null;

			if (useFinderCache) {
				list = (List<CalendarBooking>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CalendarBooking calendarBooking : list) {
						if (recurringCalendarBookingId !=
								calendarBooking.
									getRecurringCalendarBookingId()) {

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

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

				sb.append(
					_FINDER_COLUMN_RECURRINGCALENDARBOOKINGID_RECURRINGCALENDARBOOKINGID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(recurringCalendarBookingId);

					list = (List<CalendarBooking>)QueryUtil.list(
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

		CalendarBooking calendarBooking =
			fetchByRecurringCalendarBookingId_First(
				recurringCalendarBookingId, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("recurringCalendarBookingId=");
		sb.append(recurringCalendarBookingId);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
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

		List<CalendarBooking> list = findByRecurringCalendarBookingId(
			recurringCalendarBookingId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last calendar booking in the ordered set where recurringCalendarBookingId = &#63;.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking
	 * @throws NoSuchBookingException if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking findByRecurringCalendarBookingId_Last(
			long recurringCalendarBookingId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking =
			fetchByRecurringCalendarBookingId_Last(
				recurringCalendarBookingId, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("recurringCalendarBookingId=");
		sb.append(recurringCalendarBookingId);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
	}

	/**
	 * Returns the last calendar booking in the ordered set where recurringCalendarBookingId = &#63;.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByRecurringCalendarBookingId_Last(
		long recurringCalendarBookingId,
		OrderByComparator<CalendarBooking> orderByComparator) {

		int count = countByRecurringCalendarBookingId(
			recurringCalendarBookingId);

		if (count == 0) {
			return null;
		}

		List<CalendarBooking> list = findByRecurringCalendarBookingId(
			recurringCalendarBookingId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public CalendarBooking[] findByRecurringCalendarBookingId_PrevAndNext(
			long calendarBookingId, long recurringCalendarBookingId,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = findByPrimaryKey(calendarBookingId);

		Session session = null;

		try {
			session = openSession();

			CalendarBooking[] array = new CalendarBookingImpl[3];

			array[0] = getByRecurringCalendarBookingId_PrevAndNext(
				session, calendarBooking, recurringCalendarBookingId,
				orderByComparator, true);

			array[1] = calendarBooking;

			array[2] = getByRecurringCalendarBookingId_PrevAndNext(
				session, calendarBooking, recurringCalendarBookingId,
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

	protected CalendarBooking getByRecurringCalendarBookingId_PrevAndNext(
		Session session, CalendarBooking calendarBooking,
		long recurringCalendarBookingId,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

		sb.append(
			_FINDER_COLUMN_RECURRINGCALENDARBOOKINGID_RECURRINGCALENDARBOOKINGID_2);

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
			sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(recurringCalendarBookingId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						calendarBooking)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CalendarBooking> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the calendar bookings where recurringCalendarBookingId = &#63; from the database.
	 *
	 * @param recurringCalendarBookingId the recurring calendar booking ID
	 */
	@Override
	public void removeByRecurringCalendarBookingId(
		long recurringCalendarBookingId) {

		for (CalendarBooking calendarBooking :
				findByRecurringCalendarBookingId(
					recurringCalendarBookingId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(calendarBooking);
		}
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

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			FinderPath finderPath =
				_finderPathCountByRecurringCalendarBookingId;

			Object[] finderArgs = new Object[] {recurringCalendarBookingId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_CALENDARBOOKING_WHERE);

				sb.append(
					_FINDER_COLUMN_RECURRINGCALENDARBOOKINGID_RECURRINGCALENDARBOOKINGID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(recurringCalendarBookingId);

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

	private static final String
		_FINDER_COLUMN_RECURRINGCALENDARBOOKINGID_RECURRINGCALENDARBOOKINGID_2 =
			"calendarBooking.recurringCalendarBookingId = ?";

	private FinderPath _finderPathFetchByC_P;

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

		CalendarBooking calendarBooking = fetchByC_P(
			calendarId, parentCalendarBookingId);

		if (calendarBooking == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("calendarId=");
			sb.append(calendarId);

			sb.append(", parentCalendarBookingId=");
			sb.append(parentCalendarBookingId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchBookingException(sb.toString());
		}

		return calendarBooking;
	}

	/**
	 * Returns the calendar booking where calendarId = &#63; and parentCalendarBookingId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param calendarId the calendar ID
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByC_P(
		long calendarId, long parentCalendarBookingId) {

		return fetchByC_P(calendarId, parentCalendarBookingId, true);
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

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {calendarId, parentCalendarBookingId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByC_P, finderArgs, this);
			}

			if (result instanceof CalendarBooking) {
				CalendarBooking calendarBooking = (CalendarBooking)result;

				if ((calendarId != calendarBooking.getCalendarId()) ||
					(parentCalendarBookingId !=
						calendarBooking.getParentCalendarBookingId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

				sb.append(_FINDER_COLUMN_C_P_CALENDARID_2);

				sb.append(_FINDER_COLUMN_C_P_PARENTCALENDARBOOKINGID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(calendarId);

					queryPos.add(parentCalendarBookingId);

					List<CalendarBooking> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByC_P, finderArgs, list);
						}
					}
					else {
						CalendarBooking calendarBooking = list.get(0);

						result = calendarBooking;

						cacheResult(calendarBooking);
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
				return (CalendarBooking)result;
			}
		}
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
		CalendarBooking calendarBooking = fetchByC_P(
			calendarId, parentCalendarBookingId);

		if (calendarBooking == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_P_CALENDARID_2 =
		"calendarBooking.calendarId = ? AND ";

	private static final String _FINDER_COLUMN_C_P_PARENTCALENDARBOOKINGID_2 =
		"calendarBooking.parentCalendarBookingId = ?";

	private FinderPath _finderPathFetchByC_V;

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

		CalendarBooking calendarBooking = fetchByC_V(calendarId, vEventUid);

		if (calendarBooking == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("calendarId=");
			sb.append(calendarId);

			sb.append(", vEventUid=");
			sb.append(vEventUid);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchBookingException(sb.toString());
		}

		return calendarBooking;
	}

	/**
	 * Returns the calendar booking where calendarId = &#63; and vEventUid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param calendarId the calendar ID
	 * @param vEventUid the v event uid
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByC_V(long calendarId, String vEventUid) {
		return fetchByC_V(calendarId, vEventUid, true);
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

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			vEventUid = Objects.toString(vEventUid, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {calendarId, vEventUid};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByC_V, finderArgs, this);
			}

			if (result instanceof CalendarBooking) {
				CalendarBooking calendarBooking = (CalendarBooking)result;

				if ((calendarId != calendarBooking.getCalendarId()) ||
					!Objects.equals(
						vEventUid, calendarBooking.getVEventUid())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

				sb.append(_FINDER_COLUMN_C_V_CALENDARID_2);

				boolean bindVEventUid = false;

				if (vEventUid.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_V_VEVENTUID_3);
				}
				else {
					bindVEventUid = true;

					sb.append(_FINDER_COLUMN_C_V_VEVENTUID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(calendarId);

					if (bindVEventUid) {
						queryPos.add(vEventUid);
					}

					List<CalendarBooking> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByC_V, finderArgs, list);
						}
					}
					else {
						CalendarBooking calendarBooking = list.get(0);

						result = calendarBooking;

						cacheResult(calendarBooking);
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
				return (CalendarBooking)result;
			}
		}
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
		CalendarBooking calendarBooking = fetchByC_V(calendarId, vEventUid);

		if (calendarBooking == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_V_CALENDARID_2 =
		"calendarBooking.calendarId = ? AND ";

	private static final String _FINDER_COLUMN_C_V_VEVENTUID_2 =
		"calendarBooking.vEventUid = ?";

	private static final String _FINDER_COLUMN_C_V_VEVENTUID_3 =
		"(calendarBooking.vEventUid IS NULL OR calendarBooking.vEventUid = '')";

	private FinderPath _finderPathWithPaginationFindByC_S;
	private FinderPath _finderPathWithoutPaginationFindByC_S;
	private FinderPath _finderPathCountByC_S;
	private FinderPath _finderPathWithPaginationCountByC_S;

	/**
	 * Returns all the calendar bookings where calendarId = &#63; and status = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @return the matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByC_S(long calendarId, int status) {
		return findByC_S(
			calendarId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<CalendarBooking> findByC_S(
		long calendarId, int status, int start, int end) {

		return findByC_S(calendarId, status, start, end, null);
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
	@Override
	public List<CalendarBooking> findByC_S(
		long calendarId, int status, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return findByC_S(
			calendarId, status, start, end, orderByComparator, true);
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
	@Override
	public List<CalendarBooking> findByC_S(
		long calendarId, int status, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_S;
					finderArgs = new Object[] {calendarId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_S;
				finderArgs = new Object[] {
					calendarId, status, start, end, orderByComparator
				};
			}

			List<CalendarBooking> list = null;

			if (useFinderCache) {
				list = (List<CalendarBooking>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CalendarBooking calendarBooking : list) {
						if ((calendarId != calendarBooking.getCalendarId()) ||
							(status != calendarBooking.getStatus())) {

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

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

				sb.append(_FINDER_COLUMN_C_S_CALENDARID_2);

				sb.append(_FINDER_COLUMN_C_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(calendarId);

					queryPos.add(status);

					list = (List<CalendarBooking>)QueryUtil.list(
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

		CalendarBooking calendarBooking = fetchByC_S_First(
			calendarId, status, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("calendarId=");
		sb.append(calendarId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
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

		List<CalendarBooking> list = findByC_S(
			calendarId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public CalendarBooking findByC_S_Last(
			long calendarId, int status,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = fetchByC_S_Last(
			calendarId, status, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("calendarId=");
		sb.append(calendarId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
	}

	/**
	 * Returns the last calendar booking in the ordered set where calendarId = &#63; and status = &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByC_S_Last(
		long calendarId, int status,
		OrderByComparator<CalendarBooking> orderByComparator) {

		int count = countByC_S(calendarId, status);

		if (count == 0) {
			return null;
		}

		List<CalendarBooking> list = findByC_S(
			calendarId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public CalendarBooking[] findByC_S_PrevAndNext(
			long calendarBookingId, long calendarId, int status,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = findByPrimaryKey(calendarBookingId);

		Session session = null;

		try {
			session = openSession();

			CalendarBooking[] array = new CalendarBookingImpl[3];

			array[0] = getByC_S_PrevAndNext(
				session, calendarBooking, calendarId, status, orderByComparator,
				true);

			array[1] = calendarBooking;

			array[2] = getByC_S_PrevAndNext(
				session, calendarBooking, calendarId, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected CalendarBooking getByC_S_PrevAndNext(
		Session session, CalendarBooking calendarBooking, long calendarId,
		int status, OrderByComparator<CalendarBooking> orderByComparator,
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

		sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

		sb.append(_FINDER_COLUMN_C_S_CALENDARID_2);

		sb.append(_FINDER_COLUMN_C_S_STATUS_2);

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
			sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(calendarId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						calendarBooking)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CalendarBooking> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
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
	@Override
	public List<CalendarBooking> findByC_S(long calendarId, int[] statuses) {
		return findByC_S(
			calendarId, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<CalendarBooking> findByC_S(
		long calendarId, int[] statuses, int start, int end) {

		return findByC_S(calendarId, statuses, start, end, null);
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
	@Override
	public List<CalendarBooking> findByC_S(
		long calendarId, int[] statuses, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return findByC_S(
			calendarId, statuses, start, end, orderByComparator, true);
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
	@Override
	public List<CalendarBooking> findByC_S(
		long calendarId, int[] statuses, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		if (statuses.length == 1) {
			return findByC_S(
				calendarId, statuses[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						calendarId, StringUtil.merge(statuses)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					calendarId, StringUtil.merge(statuses), start, end,
					orderByComparator
				};
			}

			List<CalendarBooking> list = null;

			if (useFinderCache) {
				list = (List<CalendarBooking>)finderCache.getResult(
					_finderPathWithPaginationFindByC_S, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CalendarBooking calendarBooking : list) {
						if ((calendarId != calendarBooking.getCalendarId()) ||
							!ArrayUtil.contains(
								statuses, calendarBooking.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

				sb.append(_FINDER_COLUMN_C_S_CALENDARID_2);

				if (statuses.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_C_S_STATUS_7);

					sb.append(StringUtil.merge(statuses));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(calendarId);

					list = (List<CalendarBooking>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByC_S, finderArgs,
							list);
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
	 * Removes all the calendar bookings where calendarId = &#63; and status = &#63; from the database.
	 *
	 * @param calendarId the calendar ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long calendarId, int status) {
		for (CalendarBooking calendarBooking :
				findByC_S(
					calendarId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(calendarBooking);
		}
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
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			FinderPath finderPath = _finderPathCountByC_S;

			Object[] finderArgs = new Object[] {calendarId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CALENDARBOOKING_WHERE);

				sb.append(_FINDER_COLUMN_C_S_CALENDARID_2);

				sb.append(_FINDER_COLUMN_C_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(calendarId);

					queryPos.add(status);

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
	 * Returns the number of calendar bookings where calendarId = &#63; and status = any &#63;.
	 *
	 * @param calendarId the calendar ID
	 * @param statuses the statuses
	 * @return the number of matching calendar bookings
	 */
	@Override
	public int countByC_S(long calendarId, int[] statuses) {
		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			Object[] finderArgs = new Object[] {
				calendarId, StringUtil.merge(statuses)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByC_S, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_CALENDARBOOKING_WHERE);

				sb.append(_FINDER_COLUMN_C_S_CALENDARID_2);

				if (statuses.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_C_S_STATUS_7);

					sb.append(StringUtil.merge(statuses));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(calendarId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByC_S, finderArgs, count);
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

	private static final String _FINDER_COLUMN_C_S_CALENDARID_2 =
		"calendarBooking.calendarId = ? AND ";

	private static final String _FINDER_COLUMN_C_S_STATUS_2 =
		"calendarBooking.status = ?";

	private static final String _FINDER_COLUMN_C_S_STATUS_7 =
		"calendarBooking.status IN (";

	private FinderPath _finderPathWithPaginationFindByP_S;
	private FinderPath _finderPathWithoutPaginationFindByP_S;
	private FinderPath _finderPathCountByP_S;

	/**
	 * Returns all the calendar bookings where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @return the matching calendar bookings
	 */
	@Override
	public List<CalendarBooking> findByP_S(
		long parentCalendarBookingId, int status) {

		return findByP_S(
			parentCalendarBookingId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
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
	@Override
	public List<CalendarBooking> findByP_S(
		long parentCalendarBookingId, int status, int start, int end) {

		return findByP_S(parentCalendarBookingId, status, start, end, null);
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
	@Override
	public List<CalendarBooking> findByP_S(
		long parentCalendarBookingId, int status, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return findByP_S(
			parentCalendarBookingId, status, start, end, orderByComparator,
			true);
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
	@Override
	public List<CalendarBooking> findByP_S(
		long parentCalendarBookingId, int status, int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByP_S;
					finderArgs = new Object[] {parentCalendarBookingId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByP_S;
				finderArgs = new Object[] {
					parentCalendarBookingId, status, start, end,
					orderByComparator
				};
			}

			List<CalendarBooking> list = null;

			if (useFinderCache) {
				list = (List<CalendarBooking>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (CalendarBooking calendarBooking : list) {
						if ((parentCalendarBookingId !=
								calendarBooking.getParentCalendarBookingId()) ||
							(status != calendarBooking.getStatus())) {

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

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

				sb.append(_FINDER_COLUMN_P_S_PARENTCALENDARBOOKINGID_2);

				sb.append(_FINDER_COLUMN_P_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentCalendarBookingId);

					queryPos.add(status);

					list = (List<CalendarBooking>)QueryUtil.list(
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

		CalendarBooking calendarBooking = fetchByP_S_First(
			parentCalendarBookingId, status, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentCalendarBookingId=");
		sb.append(parentCalendarBookingId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
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

		List<CalendarBooking> list = findByP_S(
			parentCalendarBookingId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public CalendarBooking findByP_S_Last(
			long parentCalendarBookingId, int status,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = fetchByP_S_Last(
			parentCalendarBookingId, status, orderByComparator);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentCalendarBookingId=");
		sb.append(parentCalendarBookingId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBookingException(sb.toString());
	}

	/**
	 * Returns the last calendar booking in the ordered set where parentCalendarBookingId = &#63; and status = &#63;.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByP_S_Last(
		long parentCalendarBookingId, int status,
		OrderByComparator<CalendarBooking> orderByComparator) {

		int count = countByP_S(parentCalendarBookingId, status);

		if (count == 0) {
			return null;
		}

		List<CalendarBooking> list = findByP_S(
			parentCalendarBookingId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
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
	@Override
	public CalendarBooking[] findByP_S_PrevAndNext(
			long calendarBookingId, long parentCalendarBookingId, int status,
			OrderByComparator<CalendarBooking> orderByComparator)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = findByPrimaryKey(calendarBookingId);

		Session session = null;

		try {
			session = openSession();

			CalendarBooking[] array = new CalendarBookingImpl[3];

			array[0] = getByP_S_PrevAndNext(
				session, calendarBooking, parentCalendarBookingId, status,
				orderByComparator, true);

			array[1] = calendarBooking;

			array[2] = getByP_S_PrevAndNext(
				session, calendarBooking, parentCalendarBookingId, status,
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

	protected CalendarBooking getByP_S_PrevAndNext(
		Session session, CalendarBooking calendarBooking,
		long parentCalendarBookingId, int status,
		OrderByComparator<CalendarBooking> orderByComparator,
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

		sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

		sb.append(_FINDER_COLUMN_P_S_PARENTCALENDARBOOKINGID_2);

		sb.append(_FINDER_COLUMN_P_S_STATUS_2);

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
			sb.append(CalendarBookingModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(parentCalendarBookingId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						calendarBooking)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<CalendarBooking> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the calendar bookings where parentCalendarBookingId = &#63; and status = &#63; from the database.
	 *
	 * @param parentCalendarBookingId the parent calendar booking ID
	 * @param status the status
	 */
	@Override
	public void removeByP_S(long parentCalendarBookingId, int status) {
		for (CalendarBooking calendarBooking :
				findByP_S(
					parentCalendarBookingId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(calendarBooking);
		}
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
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			FinderPath finderPath = _finderPathCountByP_S;

			Object[] finderArgs = new Object[] {
				parentCalendarBookingId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_CALENDARBOOKING_WHERE);

				sb.append(_FINDER_COLUMN_P_S_PARENTCALENDARBOOKINGID_2);

				sb.append(_FINDER_COLUMN_P_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentCalendarBookingId);

					queryPos.add(status);

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

	private static final String _FINDER_COLUMN_P_S_PARENTCALENDARBOOKINGID_2 =
		"calendarBooking.parentCalendarBookingId = ? AND ";

	private static final String _FINDER_COLUMN_P_S_STATUS_2 =
		"calendarBooking.status = ?";

	private FinderPath _finderPathFetchByERC_G;

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

		CalendarBooking calendarBooking = fetchByERC_G(
			externalReferenceCode, groupId);

		if (calendarBooking == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchBookingException(sb.toString());
		}

		return calendarBooking;
	}

	/**
	 * Returns the calendar booking where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching calendar booking, or <code>null</code> if a matching calendar booking could not be found
	 */
	@Override
	public CalendarBooking fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
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

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {externalReferenceCode, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByERC_G, finderArgs, this);
			}

			if (result instanceof CalendarBooking) {
				CalendarBooking calendarBooking = (CalendarBooking)result;

				if (!Objects.equals(
						externalReferenceCode,
						calendarBooking.getExternalReferenceCode()) ||
					(groupId != calendarBooking.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_CALENDARBOOKING_WHERE);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(_FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(_FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_ERC_G_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindExternalReferenceCode) {
						queryPos.add(externalReferenceCode);
					}

					queryPos.add(groupId);

					List<CalendarBooking> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						CalendarBooking calendarBooking = list.get(0);

						result = calendarBooking;

						cacheResult(calendarBooking);
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
				return (CalendarBooking)result;
			}
		}
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
		CalendarBooking calendarBooking = fetchByERC_G(
			externalReferenceCode, groupId);

		if (calendarBooking == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"calendarBooking.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(calendarBooking.externalReferenceCode IS NULL OR calendarBooking.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"calendarBooking.groupId = ?";

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
	 * Caches the calendar booking in the entity cache if it is enabled.
	 *
	 * @param calendarBooking the calendar booking
	 */
	@Override
	public void cacheResult(CalendarBooking calendarBooking) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					calendarBooking.getCtCollectionId())) {

			entityCache.putResult(
				CalendarBookingImpl.class, calendarBooking.getPrimaryKey(),
				calendarBooking);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					calendarBooking.getUuid(), calendarBooking.getGroupId()
				},
				calendarBooking);

			finderCache.putResult(
				_finderPathFetchByC_P,
				new Object[] {
					calendarBooking.getCalendarId(),
					calendarBooking.getParentCalendarBookingId()
				},
				calendarBooking);

			finderCache.putResult(
				_finderPathFetchByC_V,
				new Object[] {
					calendarBooking.getCalendarId(),
					calendarBooking.getVEventUid()
				},
				calendarBooking);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					calendarBooking.getExternalReferenceCode(),
					calendarBooking.getGroupId()
				},
				calendarBooking);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the calendar bookings in the entity cache if it is enabled.
	 *
	 * @param calendarBookings the calendar bookings
	 */
	@Override
	public void cacheResult(List<CalendarBooking> calendarBookings) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (calendarBookings.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CalendarBooking calendarBooking : calendarBookings) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						calendarBooking.getCtCollectionId())) {

				if (entityCache.getResult(
						CalendarBookingImpl.class,
						calendarBooking.getPrimaryKey()) == null) {

					cacheResult(calendarBooking);
				}
			}
		}
	}

	/**
	 * Clears the cache for all calendar bookings.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CalendarBookingImpl.class);

		finderCache.clearCache(CalendarBookingImpl.class);
	}

	/**
	 * Clears the cache for the calendar booking.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CalendarBooking calendarBooking) {
		entityCache.removeResult(CalendarBookingImpl.class, calendarBooking);
	}

	@Override
	public void clearCache(List<CalendarBooking> calendarBookings) {
		for (CalendarBooking calendarBooking : calendarBookings) {
			entityCache.removeResult(
				CalendarBookingImpl.class, calendarBooking);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(CalendarBookingImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(CalendarBookingImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CalendarBookingModelImpl calendarBookingModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					calendarBookingModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				calendarBookingModelImpl.getUuid(),
				calendarBookingModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, calendarBookingModelImpl);

			args = new Object[] {
				calendarBookingModelImpl.getCalendarId(),
				calendarBookingModelImpl.getParentCalendarBookingId()
			};

			finderCache.putResult(
				_finderPathFetchByC_P, args, calendarBookingModelImpl);

			args = new Object[] {
				calendarBookingModelImpl.getCalendarId(),
				calendarBookingModelImpl.getVEventUid()
			};

			finderCache.putResult(
				_finderPathFetchByC_V, args, calendarBookingModelImpl);

			args = new Object[] {
				calendarBookingModelImpl.getExternalReferenceCode(),
				calendarBookingModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args, calendarBookingModelImpl);
		}
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

	/**
	 * Removes the calendar booking with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the calendar booking
	 * @return the calendar booking that was removed
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	@Override
	public CalendarBooking remove(Serializable primaryKey)
		throws NoSuchBookingException {

		Session session = null;

		try {
			session = openSession();

			CalendarBooking calendarBooking = (CalendarBooking)session.get(
				CalendarBookingImpl.class, primaryKey);

			if (calendarBooking == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchBookingException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(calendarBooking);
		}
		catch (NoSuchBookingException noSuchEntityException) {
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

		entityCache.putResult(
			CalendarBookingImpl.class, calendarBookingModelImpl, false, true);

		cacheUniqueFindersCache(calendarBookingModelImpl);

		if (isNew) {
			calendarBooking.setNew(false);
		}

		calendarBooking.resetOriginalValues();

		return calendarBooking;
	}

	/**
	 * Returns the calendar booking with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the calendar booking
	 * @return the calendar booking
	 * @throws NoSuchBookingException if a calendar booking with the primary key could not be found
	 */
	@Override
	public CalendarBooking findByPrimaryKey(Serializable primaryKey)
		throws NoSuchBookingException {

		CalendarBooking calendarBooking = fetchByPrimaryKey(primaryKey);

		if (calendarBooking == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchBookingException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

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

	/**
	 * Returns the calendar booking with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the calendar booking
	 * @return the calendar booking, or <code>null</code> if a calendar booking with the primary key could not be found
	 */
	@Override
	public CalendarBooking fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				CalendarBooking.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		CalendarBooking calendarBooking =
			(CalendarBooking)entityCache.getResult(
				CalendarBookingImpl.class, primaryKey);

		if (calendarBooking != null) {
			return calendarBooking;
		}

		Session session = null;

		try {
			session = openSession();

			calendarBooking = (CalendarBooking)session.get(
				CalendarBookingImpl.class, primaryKey);

			if (calendarBooking != null) {
				cacheResult(calendarBooking);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return calendarBooking;
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
	public Map<Serializable, CalendarBooking> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(CalendarBooking.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, CalendarBooking> map =
			new HashMap<Serializable, CalendarBooking>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			CalendarBooking calendarBooking = fetchByPrimaryKey(primaryKey);

			if (calendarBooking != null) {
				map.put(primaryKey, calendarBooking);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						CalendarBooking.class, primaryKey)) {

				CalendarBooking calendarBooking =
					(CalendarBooking)entityCache.getResult(
						CalendarBookingImpl.class, primaryKey);

				if (calendarBooking == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, calendarBooking);
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

			for (CalendarBooking calendarBooking :
					(List<CalendarBooking>)query.list()) {

				map.put(calendarBooking.getPrimaryKeyObj(), calendarBooking);

				cacheResult(calendarBooking);
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
	 * Returns all the calendar bookings.
	 *
	 * @return the calendar bookings
	 */
	@Override
	public List<CalendarBooking> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<CalendarBooking> findAll(int start, int end) {
		return findAll(start, end, null);
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
	@Override
	public List<CalendarBooking> findAll(
		int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
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
	@Override
	public List<CalendarBooking> findAll(
		int start, int end,
		OrderByComparator<CalendarBooking> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

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

			List<CalendarBooking> list = null;

			if (useFinderCache) {
				list = (List<CalendarBooking>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_CALENDARBOOKING);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_CALENDARBOOKING;

					sql = sql.concat(CalendarBookingModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<CalendarBooking>)QueryUtil.list(
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
	 * Removes all the calendar bookings from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CalendarBooking calendarBooking : findAll()) {
			remove(calendarBooking);
		}
	}

	/**
	 * Returns the number of calendar bookings.
	 *
	 * @return the number of calendar bookings
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					CalendarBooking.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_CALENDARBOOKING);

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

		_finderPathWithPaginationFindByCalendarId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCalendarId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"calendarId"}, true);

		_finderPathWithoutPaginationFindByCalendarId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCalendarId",
			new String[] {Long.class.getName()}, new String[] {"calendarId"},
			true);

		_finderPathCountByCalendarId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCalendarId",
			new String[] {Long.class.getName()}, new String[] {"calendarId"},
			false);

		_finderPathWithPaginationFindByCalendarResourceId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCalendarResourceId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"calendarResourceId"}, true);

		_finderPathWithoutPaginationFindByCalendarResourceId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByCalendarResourceId", new String[] {Long.class.getName()},
			new String[] {"calendarResourceId"}, true);

		_finderPathCountByCalendarResourceId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByCalendarResourceId", new String[] {Long.class.getName()},
			new String[] {"calendarResourceId"}, false);

		_finderPathWithPaginationFindByParentCalendarBookingId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByParentCalendarBookingId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"parentCalendarBookingId"}, true);

		_finderPathWithoutPaginationFindByParentCalendarBookingId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByParentCalendarBookingId",
				new String[] {Long.class.getName()},
				new String[] {"parentCalendarBookingId"}, true);

		_finderPathCountByParentCalendarBookingId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByParentCalendarBookingId",
			new String[] {Long.class.getName()},
			new String[] {"parentCalendarBookingId"}, false);

		_finderPathWithPaginationFindByRecurringCalendarBookingId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
				"findByRecurringCalendarBookingId",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"recurringCalendarBookingId"}, true);

		_finderPathWithoutPaginationFindByRecurringCalendarBookingId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByRecurringCalendarBookingId",
				new String[] {Long.class.getName()},
				new String[] {"recurringCalendarBookingId"}, true);

		_finderPathCountByRecurringCalendarBookingId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByRecurringCalendarBookingId",
			new String[] {Long.class.getName()},
			new String[] {"recurringCalendarBookingId"}, false);

		_finderPathFetchByC_P = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"calendarId", "parentCalendarBookingId"}, true);

		_finderPathFetchByC_V = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_V",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"calendarId", "vEventUid"}, true);

		_finderPathWithPaginationFindByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"calendarId", "status"}, true);

		_finderPathWithoutPaginationFindByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"calendarId", "status"}, true);

		_finderPathCountByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"calendarId", "status"}, false);

		_finderPathWithPaginationCountByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"calendarId", "status"}, false);

		_finderPathWithPaginationFindByP_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"parentCalendarBookingId", "status"}, true);

		_finderPathWithoutPaginationFindByP_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"parentCalendarBookingId", "status"}, true);

		_finderPathCountByP_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"parentCalendarBookingId", "status"}, false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

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

	private static final String _SQL_SELECT_CALENDARBOOKING =
		"SELECT calendarBooking FROM CalendarBooking calendarBooking";

	private static final String _SQL_SELECT_CALENDARBOOKING_WHERE =
		"SELECT calendarBooking FROM CalendarBooking calendarBooking WHERE ";

	private static final String _SQL_COUNT_CALENDARBOOKING =
		"SELECT COUNT(calendarBooking) FROM CalendarBooking calendarBooking";

	private static final String _SQL_COUNT_CALENDARBOOKING_WHERE =
		"SELECT COUNT(calendarBooking) FROM CalendarBooking calendarBooking WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "calendarBooking.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CalendarBooking exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CalendarBooking exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarBookingPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}