/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.persistence.impl;

import com.liferay.notification.exception.NoSuchNotificationQueueEntryException;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationQueueEntryTable;
import com.liferay.notification.model.impl.NotificationQueueEntryImpl;
import com.liferay.notification.model.impl.NotificationQueueEntryModelImpl;
import com.liferay.notification.service.persistence.NotificationQueueEntryPersistence;
import com.liferay.notification.service.persistence.NotificationQueueEntryUtil;
import com.liferay.notification.service.persistence.impl.constants.NotificationPersistenceConstants;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.sql.Timestamp;

import java.util.Date;
import java.util.HashMap;
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
 * The persistence implementation for the notification queue entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Gabriel Albuquerque
 * @generated
 */
@Component(service = NotificationQueueEntryPersistence.class)
public class NotificationQueueEntryPersistenceImpl
	extends BasePersistenceImpl<NotificationQueueEntry>
	implements NotificationQueueEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>NotificationQueueEntryUtil</code> to access the notification queue entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		NotificationQueueEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the notification queue entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the notification queue entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @return the range of matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the notification queue entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the notification queue entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByCompanyId;
				finderArgs = new Object[] {companyId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByCompanyId;
			finderArgs = new Object[] {
				companyId, start, end, orderByComparator
			};
		}

		List<NotificationQueueEntry> list = null;

		if (useFinderCache) {
			list = (List<NotificationQueueEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (NotificationQueueEntry notificationQueueEntry : list) {
					if (companyId != notificationQueueEntry.getCompanyId()) {
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

			sb.append(_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(NotificationQueueEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<NotificationQueueEntry>)QueryUtil.list(
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

	/**
	 * Returns the first notification queue entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		NotificationQueueEntry notificationQueueEntry = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (notificationQueueEntry != null) {
			return notificationQueueEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchNotificationQueueEntryException(sb.toString());
	}

	/**
	 * Returns the first notification queue entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification queue entry, or <code>null</code> if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry fetchByCompanyId_First(
		long companyId,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		List<NotificationQueueEntry> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last notification queue entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry findByCompanyId_Last(
			long companyId,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		NotificationQueueEntry notificationQueueEntry = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (notificationQueueEntry != null) {
			return notificationQueueEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchNotificationQueueEntryException(sb.toString());
	}

	/**
	 * Returns the last notification queue entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching notification queue entry, or <code>null</code> if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<NotificationQueueEntry> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the notification queue entries before and after the current notification queue entry in the ordered set where companyId = &#63;.
	 *
	 * @param notificationQueueEntryId the primary key of the current notification queue entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry[] findByCompanyId_PrevAndNext(
			long notificationQueueEntryId, long companyId,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		NotificationQueueEntry notificationQueueEntry = findByPrimaryKey(
			notificationQueueEntryId);

		Session session = null;

		try {
			session = openSession();

			NotificationQueueEntry[] array = new NotificationQueueEntryImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, notificationQueueEntry, companyId, orderByComparator,
				true);

			array[1] = notificationQueueEntry;

			array[2] = getByCompanyId_PrevAndNext(
				session, notificationQueueEntry, companyId, orderByComparator,
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

	protected NotificationQueueEntry getByCompanyId_PrevAndNext(
		Session session, NotificationQueueEntry notificationQueueEntry,
		long companyId,
		OrderByComparator<NotificationQueueEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

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
			sb.append(NotificationQueueEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						notificationQueueEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<NotificationQueueEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the notification queue entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching notification queue entries that the user has permission to view
	 */
	@Override
	public List<NotificationQueueEntry> filterFindByCompanyId(long companyId) {
		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the notification queue entries that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @return the range of matching notification queue entries that the user has permission to view
	 */
	@Override
	public List<NotificationQueueEntry> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the notification queue entries that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching notification queue entries that the user has permission to view
	 */
	@Override
	public List<NotificationQueueEntry> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId(companyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					NotificationQueueEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(NotificationQueueEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), NotificationQueueEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, NotificationQueueEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, NotificationQueueEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<NotificationQueueEntry>)QueryUtil.list(
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
	 * Returns the notification queue entries before and after the current notification queue entry in the ordered set of notification queue entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param notificationQueueEntryId the primary key of the current notification queue entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry[] filterFindByCompanyId_PrevAndNext(
			long notificationQueueEntryId, long companyId,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				notificationQueueEntryId, companyId, orderByComparator);
		}

		NotificationQueueEntry notificationQueueEntry = findByPrimaryKey(
			notificationQueueEntryId);

		Session session = null;

		try {
			session = openSession();

			NotificationQueueEntry[] array = new NotificationQueueEntryImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, notificationQueueEntry, companyId, orderByComparator,
				true);

			array[1] = notificationQueueEntry;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, notificationQueueEntry, companyId, orderByComparator,
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

	protected NotificationQueueEntry filterGetByCompanyId_PrevAndNext(
		Session session, NotificationQueueEntry notificationQueueEntry,
		long companyId,
		OrderByComparator<NotificationQueueEntry> orderByComparator,
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

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					NotificationQueueEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(NotificationQueueEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), NotificationQueueEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, NotificationQueueEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, NotificationQueueEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						notificationQueueEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<NotificationQueueEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the notification queue entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (NotificationQueueEntry notificationQueueEntry :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(notificationQueueEntry);
		}
	}

	/**
	 * Returns the number of notification queue entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching notification queue entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_NOTIFICATIONQUEUEENTRY_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

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

	/**
	 * Returns the number of notification queue entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching notification queue entries that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<NotificationQueueEntry> notificationQueueEntries =
				findByCompanyId(companyId);

			notificationQueueEntries = InlineSQLHelperUtil.filter(
				notificationQueueEntries);

			return notificationQueueEntries.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_NOTIFICATIONQUEUEENTRY_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), NotificationQueueEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"notificationQueueEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByNotificationTemplateId;
	private FinderPath _finderPathWithoutPaginationFindByNotificationTemplateId;
	private FinderPath _finderPathCountByNotificationTemplateId;

	/**
	 * Returns all the notification queue entries where notificationTemplateId = &#63;.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @return the matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByNotificationTemplateId(
		long notificationTemplateId) {

		return findByNotificationTemplateId(
			notificationTemplateId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the notification queue entries where notificationTemplateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @return the range of matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByNotificationTemplateId(
		long notificationTemplateId, int start, int end) {

		return findByNotificationTemplateId(
			notificationTemplateId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the notification queue entries where notificationTemplateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByNotificationTemplateId(
		long notificationTemplateId, int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		return findByNotificationTemplateId(
			notificationTemplateId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the notification queue entries where notificationTemplateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByNotificationTemplateId(
		long notificationTemplateId, int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath =
					_finderPathWithoutPaginationFindByNotificationTemplateId;
				finderArgs = new Object[] {notificationTemplateId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByNotificationTemplateId;
			finderArgs = new Object[] {
				notificationTemplateId, start, end, orderByComparator
			};
		}

		List<NotificationQueueEntry> list = null;

		if (useFinderCache) {
			list = (List<NotificationQueueEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (NotificationQueueEntry notificationQueueEntry : list) {
					if (notificationTemplateId !=
							notificationQueueEntry.
								getNotificationTemplateId()) {

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

			sb.append(_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);

			sb.append(
				_FINDER_COLUMN_NOTIFICATIONTEMPLATEID_NOTIFICATIONTEMPLATEID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(NotificationQueueEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(notificationTemplateId);

				list = (List<NotificationQueueEntry>)QueryUtil.list(
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

	/**
	 * Returns the first notification queue entry in the ordered set where notificationTemplateId = &#63;.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry findByNotificationTemplateId_First(
			long notificationTemplateId,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		NotificationQueueEntry notificationQueueEntry =
			fetchByNotificationTemplateId_First(
				notificationTemplateId, orderByComparator);

		if (notificationQueueEntry != null) {
			return notificationQueueEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("notificationTemplateId=");
		sb.append(notificationTemplateId);

		sb.append("}");

		throw new NoSuchNotificationQueueEntryException(sb.toString());
	}

	/**
	 * Returns the first notification queue entry in the ordered set where notificationTemplateId = &#63;.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification queue entry, or <code>null</code> if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry fetchByNotificationTemplateId_First(
		long notificationTemplateId,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		List<NotificationQueueEntry> list = findByNotificationTemplateId(
			notificationTemplateId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last notification queue entry in the ordered set where notificationTemplateId = &#63;.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry findByNotificationTemplateId_Last(
			long notificationTemplateId,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		NotificationQueueEntry notificationQueueEntry =
			fetchByNotificationTemplateId_Last(
				notificationTemplateId, orderByComparator);

		if (notificationQueueEntry != null) {
			return notificationQueueEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("notificationTemplateId=");
		sb.append(notificationTemplateId);

		sb.append("}");

		throw new NoSuchNotificationQueueEntryException(sb.toString());
	}

	/**
	 * Returns the last notification queue entry in the ordered set where notificationTemplateId = &#63;.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching notification queue entry, or <code>null</code> if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry fetchByNotificationTemplateId_Last(
		long notificationTemplateId,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		int count = countByNotificationTemplateId(notificationTemplateId);

		if (count == 0) {
			return null;
		}

		List<NotificationQueueEntry> list = findByNotificationTemplateId(
			notificationTemplateId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the notification queue entries before and after the current notification queue entry in the ordered set where notificationTemplateId = &#63;.
	 *
	 * @param notificationQueueEntryId the primary key of the current notification queue entry
	 * @param notificationTemplateId the notification template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry[] findByNotificationTemplateId_PrevAndNext(
			long notificationQueueEntryId, long notificationTemplateId,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		NotificationQueueEntry notificationQueueEntry = findByPrimaryKey(
			notificationQueueEntryId);

		Session session = null;

		try {
			session = openSession();

			NotificationQueueEntry[] array = new NotificationQueueEntryImpl[3];

			array[0] = getByNotificationTemplateId_PrevAndNext(
				session, notificationQueueEntry, notificationTemplateId,
				orderByComparator, true);

			array[1] = notificationQueueEntry;

			array[2] = getByNotificationTemplateId_PrevAndNext(
				session, notificationQueueEntry, notificationTemplateId,
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

	protected NotificationQueueEntry getByNotificationTemplateId_PrevAndNext(
		Session session, NotificationQueueEntry notificationQueueEntry,
		long notificationTemplateId,
		OrderByComparator<NotificationQueueEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);

		sb.append(
			_FINDER_COLUMN_NOTIFICATIONTEMPLATEID_NOTIFICATIONTEMPLATEID_2);

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
			sb.append(NotificationQueueEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(notificationTemplateId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						notificationQueueEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<NotificationQueueEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the notification queue entries that the user has permission to view where notificationTemplateId = &#63;.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @return the matching notification queue entries that the user has permission to view
	 */
	@Override
	public List<NotificationQueueEntry> filterFindByNotificationTemplateId(
		long notificationTemplateId) {

		return filterFindByNotificationTemplateId(
			notificationTemplateId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the notification queue entries that the user has permission to view where notificationTemplateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @return the range of matching notification queue entries that the user has permission to view
	 */
	@Override
	public List<NotificationQueueEntry> filterFindByNotificationTemplateId(
		long notificationTemplateId, int start, int end) {

		return filterFindByNotificationTemplateId(
			notificationTemplateId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the notification queue entries that the user has permissions to view where notificationTemplateId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param notificationTemplateId the notification template ID
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching notification queue entries that the user has permission to view
	 */
	@Override
	public List<NotificationQueueEntry> filterFindByNotificationTemplateId(
		long notificationTemplateId, int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByNotificationTemplateId(
				notificationTemplateId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByNotificationTemplateId(
					notificationTemplateId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(
			_FINDER_COLUMN_NOTIFICATIONTEMPLATEID_NOTIFICATIONTEMPLATEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					NotificationQueueEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(NotificationQueueEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), NotificationQueueEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, NotificationQueueEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, NotificationQueueEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(notificationTemplateId);

			return (List<NotificationQueueEntry>)QueryUtil.list(
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
	 * Returns the notification queue entries before and after the current notification queue entry in the ordered set of notification queue entries that the user has permission to view where notificationTemplateId = &#63;.
	 *
	 * @param notificationQueueEntryId the primary key of the current notification queue entry
	 * @param notificationTemplateId the notification template ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry[]
			filterFindByNotificationTemplateId_PrevAndNext(
				long notificationQueueEntryId, long notificationTemplateId,
				OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByNotificationTemplateId_PrevAndNext(
				notificationQueueEntryId, notificationTemplateId,
				orderByComparator);
		}

		NotificationQueueEntry notificationQueueEntry = findByPrimaryKey(
			notificationQueueEntryId);

		Session session = null;

		try {
			session = openSession();

			NotificationQueueEntry[] array = new NotificationQueueEntryImpl[3];

			array[0] = filterGetByNotificationTemplateId_PrevAndNext(
				session, notificationQueueEntry, notificationTemplateId,
				orderByComparator, true);

			array[1] = notificationQueueEntry;

			array[2] = filterGetByNotificationTemplateId_PrevAndNext(
				session, notificationQueueEntry, notificationTemplateId,
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

	protected NotificationQueueEntry
		filterGetByNotificationTemplateId_PrevAndNext(
			Session session, NotificationQueueEntry notificationQueueEntry,
			long notificationTemplateId,
			OrderByComparator<NotificationQueueEntry> orderByComparator,
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

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(
			_FINDER_COLUMN_NOTIFICATIONTEMPLATEID_NOTIFICATIONTEMPLATEID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					NotificationQueueEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(NotificationQueueEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), NotificationQueueEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, NotificationQueueEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, NotificationQueueEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(notificationTemplateId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						notificationQueueEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<NotificationQueueEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the notification queue entries where notificationTemplateId = &#63; from the database.
	 *
	 * @param notificationTemplateId the notification template ID
	 */
	@Override
	public void removeByNotificationTemplateId(long notificationTemplateId) {
		for (NotificationQueueEntry notificationQueueEntry :
				findByNotificationTemplateId(
					notificationTemplateId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(notificationQueueEntry);
		}
	}

	/**
	 * Returns the number of notification queue entries where notificationTemplateId = &#63;.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @return the number of matching notification queue entries
	 */
	@Override
	public int countByNotificationTemplateId(long notificationTemplateId) {
		FinderPath finderPath = _finderPathCountByNotificationTemplateId;

		Object[] finderArgs = new Object[] {notificationTemplateId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_NOTIFICATIONQUEUEENTRY_WHERE);

			sb.append(
				_FINDER_COLUMN_NOTIFICATIONTEMPLATEID_NOTIFICATIONTEMPLATEID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(notificationTemplateId);

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

	/**
	 * Returns the number of notification queue entries that the user has permission to view where notificationTemplateId = &#63;.
	 *
	 * @param notificationTemplateId the notification template ID
	 * @return the number of matching notification queue entries that the user has permission to view
	 */
	@Override
	public int filterCountByNotificationTemplateId(
		long notificationTemplateId) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByNotificationTemplateId(notificationTemplateId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<NotificationQueueEntry> notificationQueueEntries =
				findByNotificationTemplateId(notificationTemplateId);

			notificationQueueEntries = InlineSQLHelperUtil.filter(
				notificationQueueEntries);

			return notificationQueueEntries.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_NOTIFICATIONQUEUEENTRY_WHERE);

		sb.append(
			_FINDER_COLUMN_NOTIFICATIONTEMPLATEID_NOTIFICATIONTEMPLATEID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), NotificationQueueEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(notificationTemplateId);

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

	private static final String
		_FINDER_COLUMN_NOTIFICATIONTEMPLATEID_NOTIFICATIONTEMPLATEID_2 =
			"notificationQueueEntry.notificationTemplateId = ?";

	private FinderPath _finderPathWithPaginationFindByLtSentDate;
	private FinderPath _finderPathWithPaginationCountByLtSentDate;

	/**
	 * Returns all the notification queue entries where sentDate &lt; &#63;.
	 *
	 * @param sentDate the sent date
	 * @return the matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByLtSentDate(Date sentDate) {
		return findByLtSentDate(
			sentDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the notification queue entries where sentDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sentDate the sent date
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @return the range of matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByLtSentDate(
		Date sentDate, int start, int end) {

		return findByLtSentDate(sentDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the notification queue entries where sentDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sentDate the sent date
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByLtSentDate(
		Date sentDate, int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		return findByLtSentDate(sentDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the notification queue entries where sentDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sentDate the sent date
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByLtSentDate(
		Date sentDate, int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		finderPath = _finderPathWithPaginationFindByLtSentDate;
		finderArgs = new Object[] {
			_getTime(sentDate), start, end, orderByComparator
		};

		List<NotificationQueueEntry> list = null;

		if (useFinderCache) {
			list = (List<NotificationQueueEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (NotificationQueueEntry notificationQueueEntry : list) {
					if (sentDate.getTime() <=
							notificationQueueEntry.getSentDate(
							).getTime()) {

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

			sb.append(_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);

			boolean bindSentDate = false;

			if (sentDate == null) {
				sb.append(_FINDER_COLUMN_LTSENTDATE_SENTDATE_1);
			}
			else {
				bindSentDate = true;

				sb.append(_FINDER_COLUMN_LTSENTDATE_SENTDATE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(NotificationQueueEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindSentDate) {
					queryPos.add(new Timestamp(sentDate.getTime()));
				}

				list = (List<NotificationQueueEntry>)QueryUtil.list(
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

	/**
	 * Returns the first notification queue entry in the ordered set where sentDate &lt; &#63;.
	 *
	 * @param sentDate the sent date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry findByLtSentDate_First(
			Date sentDate,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		NotificationQueueEntry notificationQueueEntry = fetchByLtSentDate_First(
			sentDate, orderByComparator);

		if (notificationQueueEntry != null) {
			return notificationQueueEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("sentDate<");
		sb.append(sentDate);

		sb.append("}");

		throw new NoSuchNotificationQueueEntryException(sb.toString());
	}

	/**
	 * Returns the first notification queue entry in the ordered set where sentDate &lt; &#63;.
	 *
	 * @param sentDate the sent date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification queue entry, or <code>null</code> if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry fetchByLtSentDate_First(
		Date sentDate,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		List<NotificationQueueEntry> list = findByLtSentDate(
			sentDate, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last notification queue entry in the ordered set where sentDate &lt; &#63;.
	 *
	 * @param sentDate the sent date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry findByLtSentDate_Last(
			Date sentDate,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		NotificationQueueEntry notificationQueueEntry = fetchByLtSentDate_Last(
			sentDate, orderByComparator);

		if (notificationQueueEntry != null) {
			return notificationQueueEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("sentDate<");
		sb.append(sentDate);

		sb.append("}");

		throw new NoSuchNotificationQueueEntryException(sb.toString());
	}

	/**
	 * Returns the last notification queue entry in the ordered set where sentDate &lt; &#63;.
	 *
	 * @param sentDate the sent date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching notification queue entry, or <code>null</code> if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry fetchByLtSentDate_Last(
		Date sentDate,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		int count = countByLtSentDate(sentDate);

		if (count == 0) {
			return null;
		}

		List<NotificationQueueEntry> list = findByLtSentDate(
			sentDate, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the notification queue entries before and after the current notification queue entry in the ordered set where sentDate &lt; &#63;.
	 *
	 * @param notificationQueueEntryId the primary key of the current notification queue entry
	 * @param sentDate the sent date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry[] findByLtSentDate_PrevAndNext(
			long notificationQueueEntryId, Date sentDate,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		NotificationQueueEntry notificationQueueEntry = findByPrimaryKey(
			notificationQueueEntryId);

		Session session = null;

		try {
			session = openSession();

			NotificationQueueEntry[] array = new NotificationQueueEntryImpl[3];

			array[0] = getByLtSentDate_PrevAndNext(
				session, notificationQueueEntry, sentDate, orderByComparator,
				true);

			array[1] = notificationQueueEntry;

			array[2] = getByLtSentDate_PrevAndNext(
				session, notificationQueueEntry, sentDate, orderByComparator,
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

	protected NotificationQueueEntry getByLtSentDate_PrevAndNext(
		Session session, NotificationQueueEntry notificationQueueEntry,
		Date sentDate,
		OrderByComparator<NotificationQueueEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);

		boolean bindSentDate = false;

		if (sentDate == null) {
			sb.append(_FINDER_COLUMN_LTSENTDATE_SENTDATE_1);
		}
		else {
			bindSentDate = true;

			sb.append(_FINDER_COLUMN_LTSENTDATE_SENTDATE_2);
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
			sb.append(NotificationQueueEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindSentDate) {
			queryPos.add(new Timestamp(sentDate.getTime()));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						notificationQueueEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<NotificationQueueEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the notification queue entries that the user has permission to view where sentDate &lt; &#63;.
	 *
	 * @param sentDate the sent date
	 * @return the matching notification queue entries that the user has permission to view
	 */
	@Override
	public List<NotificationQueueEntry> filterFindByLtSentDate(Date sentDate) {
		return filterFindByLtSentDate(
			sentDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the notification queue entries that the user has permission to view where sentDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sentDate the sent date
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @return the range of matching notification queue entries that the user has permission to view
	 */
	@Override
	public List<NotificationQueueEntry> filterFindByLtSentDate(
		Date sentDate, int start, int end) {

		return filterFindByLtSentDate(sentDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the notification queue entries that the user has permissions to view where sentDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sentDate the sent date
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching notification queue entries that the user has permission to view
	 */
	@Override
	public List<NotificationQueueEntry> filterFindByLtSentDate(
		Date sentDate, int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtSentDate(sentDate, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByLtSentDate(
					sentDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindSentDate = false;

		if (sentDate == null) {
			sb.append(_FINDER_COLUMN_LTSENTDATE_SENTDATE_1);
		}
		else {
			bindSentDate = true;

			sb.append(_FINDER_COLUMN_LTSENTDATE_SENTDATE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					NotificationQueueEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(NotificationQueueEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), NotificationQueueEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, NotificationQueueEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, NotificationQueueEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindSentDate) {
				queryPos.add(new Timestamp(sentDate.getTime()));
			}

			return (List<NotificationQueueEntry>)QueryUtil.list(
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
	 * Returns the notification queue entries before and after the current notification queue entry in the ordered set of notification queue entries that the user has permission to view where sentDate &lt; &#63;.
	 *
	 * @param notificationQueueEntryId the primary key of the current notification queue entry
	 * @param sentDate the sent date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry[] filterFindByLtSentDate_PrevAndNext(
			long notificationQueueEntryId, Date sentDate,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByLtSentDate_PrevAndNext(
				notificationQueueEntryId, sentDate, orderByComparator);
		}

		NotificationQueueEntry notificationQueueEntry = findByPrimaryKey(
			notificationQueueEntryId);

		Session session = null;

		try {
			session = openSession();

			NotificationQueueEntry[] array = new NotificationQueueEntryImpl[3];

			array[0] = filterGetByLtSentDate_PrevAndNext(
				session, notificationQueueEntry, sentDate, orderByComparator,
				true);

			array[1] = notificationQueueEntry;

			array[2] = filterGetByLtSentDate_PrevAndNext(
				session, notificationQueueEntry, sentDate, orderByComparator,
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

	protected NotificationQueueEntry filterGetByLtSentDate_PrevAndNext(
		Session session, NotificationQueueEntry notificationQueueEntry,
		Date sentDate,
		OrderByComparator<NotificationQueueEntry> orderByComparator,
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

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindSentDate = false;

		if (sentDate == null) {
			sb.append(_FINDER_COLUMN_LTSENTDATE_SENTDATE_1);
		}
		else {
			bindSentDate = true;

			sb.append(_FINDER_COLUMN_LTSENTDATE_SENTDATE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					NotificationQueueEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(NotificationQueueEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), NotificationQueueEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, NotificationQueueEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, NotificationQueueEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindSentDate) {
			queryPos.add(new Timestamp(sentDate.getTime()));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						notificationQueueEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<NotificationQueueEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the notification queue entries where sentDate &lt; &#63; from the database.
	 *
	 * @param sentDate the sent date
	 */
	@Override
	public void removeByLtSentDate(Date sentDate) {
		for (NotificationQueueEntry notificationQueueEntry :
				findByLtSentDate(
					sentDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(notificationQueueEntry);
		}
	}

	/**
	 * Returns the number of notification queue entries where sentDate &lt; &#63;.
	 *
	 * @param sentDate the sent date
	 * @return the number of matching notification queue entries
	 */
	@Override
	public int countByLtSentDate(Date sentDate) {
		FinderPath finderPath = _finderPathWithPaginationCountByLtSentDate;

		Object[] finderArgs = new Object[] {_getTime(sentDate)};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_NOTIFICATIONQUEUEENTRY_WHERE);

			boolean bindSentDate = false;

			if (sentDate == null) {
				sb.append(_FINDER_COLUMN_LTSENTDATE_SENTDATE_1);
			}
			else {
				bindSentDate = true;

				sb.append(_FINDER_COLUMN_LTSENTDATE_SENTDATE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindSentDate) {
					queryPos.add(new Timestamp(sentDate.getTime()));
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

	/**
	 * Returns the number of notification queue entries that the user has permission to view where sentDate &lt; &#63;.
	 *
	 * @param sentDate the sent date
	 * @return the number of matching notification queue entries that the user has permission to view
	 */
	@Override
	public int filterCountByLtSentDate(Date sentDate) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByLtSentDate(sentDate);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<NotificationQueueEntry> notificationQueueEntries =
				findByLtSentDate(sentDate);

			notificationQueueEntries = InlineSQLHelperUtil.filter(
				notificationQueueEntries);

			return notificationQueueEntries.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_NOTIFICATIONQUEUEENTRY_WHERE);

		boolean bindSentDate = false;

		if (sentDate == null) {
			sb.append(_FINDER_COLUMN_LTSENTDATE_SENTDATE_1);
		}
		else {
			bindSentDate = true;

			sb.append(_FINDER_COLUMN_LTSENTDATE_SENTDATE_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), NotificationQueueEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindSentDate) {
				queryPos.add(new Timestamp(sentDate.getTime()));
			}

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

	private static final String _FINDER_COLUMN_LTSENTDATE_SENTDATE_1 =
		"notificationQueueEntry.sentDate IS NULL";

	private static final String _FINDER_COLUMN_LTSENTDATE_SENTDATE_2 =
		"notificationQueueEntry.sentDate < ?";

	private FinderPath _finderPathWithPaginationFindByT_S;
	private FinderPath _finderPathWithoutPaginationFindByT_S;
	private FinderPath _finderPathCountByT_S;

	/**
	 * Returns all the notification queue entries where type = &#63; and status = &#63;.
	 *
	 * @param type the type
	 * @param status the status
	 * @return the matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByT_S(String type, int status) {
		return findByT_S(
			type, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the notification queue entries where type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @return the range of matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByT_S(
		String type, int status, int start, int end) {

		return findByT_S(type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the notification queue entries where type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByT_S(
		String type, int status, int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		return findByT_S(type, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the notification queue entries where type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findByT_S(
		String type, int status, int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator,
		boolean useFinderCache) {

		type = Objects.toString(type, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByT_S;
				finderArgs = new Object[] {type, status};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByT_S;
			finderArgs = new Object[] {
				type, status, start, end, orderByComparator
			};
		}

		List<NotificationQueueEntry> list = null;

		if (useFinderCache) {
			list = (List<NotificationQueueEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (NotificationQueueEntry notificationQueueEntry : list) {
					if (!type.equals(notificationQueueEntry.getType()) ||
						(status != notificationQueueEntry.getStatus())) {

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

			sb.append(_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_T_S_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_T_S_TYPE_2);
			}

			sb.append(_FINDER_COLUMN_T_S_STATUS_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(NotificationQueueEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindType) {
					queryPos.add(type);
				}

				queryPos.add(status);

				list = (List<NotificationQueueEntry>)QueryUtil.list(
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

	/**
	 * Returns the first notification queue entry in the ordered set where type = &#63; and status = &#63;.
	 *
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry findByT_S_First(
			String type, int status,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		NotificationQueueEntry notificationQueueEntry = fetchByT_S_First(
			type, status, orderByComparator);

		if (notificationQueueEntry != null) {
			return notificationQueueEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("type=");
		sb.append(type);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchNotificationQueueEntryException(sb.toString());
	}

	/**
	 * Returns the first notification queue entry in the ordered set where type = &#63; and status = &#63;.
	 *
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching notification queue entry, or <code>null</code> if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry fetchByT_S_First(
		String type, int status,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		List<NotificationQueueEntry> list = findByT_S(
			type, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last notification queue entry in the ordered set where type = &#63; and status = &#63;.
	 *
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry findByT_S_Last(
			String type, int status,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		NotificationQueueEntry notificationQueueEntry = fetchByT_S_Last(
			type, status, orderByComparator);

		if (notificationQueueEntry != null) {
			return notificationQueueEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("type=");
		sb.append(type);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchNotificationQueueEntryException(sb.toString());
	}

	/**
	 * Returns the last notification queue entry in the ordered set where type = &#63; and status = &#63;.
	 *
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching notification queue entry, or <code>null</code> if a matching notification queue entry could not be found
	 */
	@Override
	public NotificationQueueEntry fetchByT_S_Last(
		String type, int status,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		int count = countByT_S(type, status);

		if (count == 0) {
			return null;
		}

		List<NotificationQueueEntry> list = findByT_S(
			type, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the notification queue entries before and after the current notification queue entry in the ordered set where type = &#63; and status = &#63;.
	 *
	 * @param notificationQueueEntryId the primary key of the current notification queue entry
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry[] findByT_S_PrevAndNext(
			long notificationQueueEntryId, String type, int status,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		type = Objects.toString(type, "");

		NotificationQueueEntry notificationQueueEntry = findByPrimaryKey(
			notificationQueueEntryId);

		Session session = null;

		try {
			session = openSession();

			NotificationQueueEntry[] array = new NotificationQueueEntryImpl[3];

			array[0] = getByT_S_PrevAndNext(
				session, notificationQueueEntry, type, status,
				orderByComparator, true);

			array[1] = notificationQueueEntry;

			array[2] = getByT_S_PrevAndNext(
				session, notificationQueueEntry, type, status,
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

	protected NotificationQueueEntry getByT_S_PrevAndNext(
		Session session, NotificationQueueEntry notificationQueueEntry,
		String type, int status,
		OrderByComparator<NotificationQueueEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_T_S_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_T_S_TYPE_2);
		}

		sb.append(_FINDER_COLUMN_T_S_STATUS_2);

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
			sb.append(NotificationQueueEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindType) {
			queryPos.add(type);
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						notificationQueueEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<NotificationQueueEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the notification queue entries that the user has permission to view where type = &#63; and status = &#63;.
	 *
	 * @param type the type
	 * @param status the status
	 * @return the matching notification queue entries that the user has permission to view
	 */
	@Override
	public List<NotificationQueueEntry> filterFindByT_S(
		String type, int status) {

		return filterFindByT_S(
			type, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the notification queue entries that the user has permission to view where type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @return the range of matching notification queue entries that the user has permission to view
	 */
	@Override
	public List<NotificationQueueEntry> filterFindByT_S(
		String type, int status, int start, int end) {

		return filterFindByT_S(type, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the notification queue entries that the user has permissions to view where type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching notification queue entries that the user has permission to view
	 */
	@Override
	public List<NotificationQueueEntry> filterFindByT_S(
		String type, int status, int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByT_S(type, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByT_S(
					type, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		type = Objects.toString(type, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_T_S_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_T_S_TYPE_2_SQL);
		}

		sb.append(_FINDER_COLUMN_T_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					NotificationQueueEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(NotificationQueueEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), NotificationQueueEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, NotificationQueueEntryImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, NotificationQueueEntryImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindType) {
				queryPos.add(type);
			}

			queryPos.add(status);

			return (List<NotificationQueueEntry>)QueryUtil.list(
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
	 * Returns the notification queue entries before and after the current notification queue entry in the ordered set of notification queue entries that the user has permission to view where type = &#63; and status = &#63;.
	 *
	 * @param notificationQueueEntryId the primary key of the current notification queue entry
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry[] filterFindByT_S_PrevAndNext(
			long notificationQueueEntryId, String type, int status,
			OrderByComparator<NotificationQueueEntry> orderByComparator)
		throws NoSuchNotificationQueueEntryException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByT_S_PrevAndNext(
				notificationQueueEntryId, type, status, orderByComparator);
		}

		type = Objects.toString(type, "");

		NotificationQueueEntry notificationQueueEntry = findByPrimaryKey(
			notificationQueueEntryId);

		Session session = null;

		try {
			session = openSession();

			NotificationQueueEntry[] array = new NotificationQueueEntryImpl[3];

			array[0] = filterGetByT_S_PrevAndNext(
				session, notificationQueueEntry, type, status,
				orderByComparator, true);

			array[1] = notificationQueueEntry;

			array[2] = filterGetByT_S_PrevAndNext(
				session, notificationQueueEntry, type, status,
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

	protected NotificationQueueEntry filterGetByT_S_PrevAndNext(
		Session session, NotificationQueueEntry notificationQueueEntry,
		String type, int status,
		OrderByComparator<NotificationQueueEntry> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_T_S_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_T_S_TYPE_2_SQL);
		}

		sb.append(_FINDER_COLUMN_T_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(
					NotificationQueueEntryModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(NotificationQueueEntryModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), NotificationQueueEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, NotificationQueueEntryImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, NotificationQueueEntryImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindType) {
			queryPos.add(type);
		}

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						notificationQueueEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<NotificationQueueEntry> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the notification queue entries where type = &#63; and status = &#63; from the database.
	 *
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByT_S(String type, int status) {
		for (NotificationQueueEntry notificationQueueEntry :
				findByT_S(
					type, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(notificationQueueEntry);
		}
	}

	/**
	 * Returns the number of notification queue entries where type = &#63; and status = &#63;.
	 *
	 * @param type the type
	 * @param status the status
	 * @return the number of matching notification queue entries
	 */
	@Override
	public int countByT_S(String type, int status) {
		type = Objects.toString(type, "");

		FinderPath finderPath = _finderPathCountByT_S;

		Object[] finderArgs = new Object[] {type, status};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_NOTIFICATIONQUEUEENTRY_WHERE);

			boolean bindType = false;

			if (type.isEmpty()) {
				sb.append(_FINDER_COLUMN_T_S_TYPE_3);
			}
			else {
				bindType = true;

				sb.append(_FINDER_COLUMN_T_S_TYPE_2);
			}

			sb.append(_FINDER_COLUMN_T_S_STATUS_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindType) {
					queryPos.add(type);
				}

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

	/**
	 * Returns the number of notification queue entries that the user has permission to view where type = &#63; and status = &#63;.
	 *
	 * @param type the type
	 * @param status the status
	 * @return the number of matching notification queue entries that the user has permission to view
	 */
	@Override
	public int filterCountByT_S(String type, int status) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByT_S(type, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<NotificationQueueEntry> notificationQueueEntries = findByT_S(
				type, status);

			notificationQueueEntries = InlineSQLHelperUtil.filter(
				notificationQueueEntries);

			return notificationQueueEntries.size();
		}

		type = Objects.toString(type, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_NOTIFICATIONQUEUEENTRY_WHERE);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_T_S_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_T_S_TYPE_2_SQL);
		}

		sb.append(_FINDER_COLUMN_T_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), NotificationQueueEntry.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindType) {
				queryPos.add(type);
			}

			queryPos.add(status);

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

	private static final String _FINDER_COLUMN_T_S_TYPE_2 =
		"notificationQueueEntry.type = ? AND ";

	private static final String _FINDER_COLUMN_T_S_TYPE_3 =
		"(notificationQueueEntry.type IS NULL OR notificationQueueEntry.type = '') AND ";

	private static final String _FINDER_COLUMN_T_S_TYPE_2_SQL =
		"notificationQueueEntry.type_ = ? AND ";

	private static final String _FINDER_COLUMN_T_S_TYPE_3_SQL =
		"(notificationQueueEntry.type_ IS NULL OR notificationQueueEntry.type_ = '') AND ";

	private static final String _FINDER_COLUMN_T_S_STATUS_2 =
		"notificationQueueEntry.status = ?";

	public NotificationQueueEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(NotificationQueueEntry.class);

		setModelImplClass(NotificationQueueEntryImpl.class);
		setModelPKClass(long.class);

		setTable(NotificationQueueEntryTable.INSTANCE);
	}

	/**
	 * Caches the notification queue entry in the entity cache if it is enabled.
	 *
	 * @param notificationQueueEntry the notification queue entry
	 */
	@Override
	public void cacheResult(NotificationQueueEntry notificationQueueEntry) {
		entityCache.putResult(
			NotificationQueueEntryImpl.class,
			notificationQueueEntry.getPrimaryKey(), notificationQueueEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the notification queue entries in the entity cache if it is enabled.
	 *
	 * @param notificationQueueEntries the notification queue entries
	 */
	@Override
	public void cacheResult(
		List<NotificationQueueEntry> notificationQueueEntries) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (notificationQueueEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (NotificationQueueEntry notificationQueueEntry :
				notificationQueueEntries) {

			if (entityCache.getResult(
					NotificationQueueEntryImpl.class,
					notificationQueueEntry.getPrimaryKey()) == null) {

				cacheResult(notificationQueueEntry);
			}
		}
	}

	/**
	 * Clears the cache for all notification queue entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(NotificationQueueEntryImpl.class);

		finderCache.clearCache(NotificationQueueEntryImpl.class);
	}

	/**
	 * Clears the cache for the notification queue entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(NotificationQueueEntry notificationQueueEntry) {
		entityCache.removeResult(
			NotificationQueueEntryImpl.class, notificationQueueEntry);
	}

	@Override
	public void clearCache(
		List<NotificationQueueEntry> notificationQueueEntries) {

		for (NotificationQueueEntry notificationQueueEntry :
				notificationQueueEntries) {

			entityCache.removeResult(
				NotificationQueueEntryImpl.class, notificationQueueEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(NotificationQueueEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				NotificationQueueEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new notification queue entry with the primary key. Does not add the notification queue entry to the database.
	 *
	 * @param notificationQueueEntryId the primary key for the new notification queue entry
	 * @return the new notification queue entry
	 */
	@Override
	public NotificationQueueEntry create(long notificationQueueEntryId) {
		NotificationQueueEntry notificationQueueEntry =
			new NotificationQueueEntryImpl();

		notificationQueueEntry.setNew(true);
		notificationQueueEntry.setPrimaryKey(notificationQueueEntryId);

		notificationQueueEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return notificationQueueEntry;
	}

	/**
	 * Removes the notification queue entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param notificationQueueEntryId the primary key of the notification queue entry
	 * @return the notification queue entry that was removed
	 * @throws NoSuchNotificationQueueEntryException if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry remove(long notificationQueueEntryId)
		throws NoSuchNotificationQueueEntryException {

		return remove((Serializable)notificationQueueEntryId);
	}

	/**
	 * Removes the notification queue entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the notification queue entry
	 * @return the notification queue entry that was removed
	 * @throws NoSuchNotificationQueueEntryException if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry remove(Serializable primaryKey)
		throws NoSuchNotificationQueueEntryException {

		Session session = null;

		try {
			session = openSession();

			NotificationQueueEntry notificationQueueEntry =
				(NotificationQueueEntry)session.get(
					NotificationQueueEntryImpl.class, primaryKey);

			if (notificationQueueEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchNotificationQueueEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(notificationQueueEntry);
		}
		catch (NoSuchNotificationQueueEntryException noSuchEntityException) {
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
	protected NotificationQueueEntry removeImpl(
		NotificationQueueEntry notificationQueueEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(notificationQueueEntry)) {
				notificationQueueEntry = (NotificationQueueEntry)session.get(
					NotificationQueueEntryImpl.class,
					notificationQueueEntry.getPrimaryKeyObj());
			}

			if (notificationQueueEntry != null) {
				session.delete(notificationQueueEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (notificationQueueEntry != null) {
			clearCache(notificationQueueEntry);
		}

		return notificationQueueEntry;
	}

	@Override
	public NotificationQueueEntry updateImpl(
		NotificationQueueEntry notificationQueueEntry) {

		boolean isNew = notificationQueueEntry.isNew();

		if (!(notificationQueueEntry instanceof
				NotificationQueueEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(notificationQueueEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					notificationQueueEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in notificationQueueEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom NotificationQueueEntry implementation " +
					notificationQueueEntry.getClass());
		}

		NotificationQueueEntryModelImpl notificationQueueEntryModelImpl =
			(NotificationQueueEntryModelImpl)notificationQueueEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (notificationQueueEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				notificationQueueEntry.setCreateDate(date);
			}
			else {
				notificationQueueEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!notificationQueueEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				notificationQueueEntry.setModifiedDate(date);
			}
			else {
				notificationQueueEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(notificationQueueEntry);
			}
			else {
				notificationQueueEntry = (NotificationQueueEntry)session.merge(
					notificationQueueEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			NotificationQueueEntryImpl.class, notificationQueueEntryModelImpl,
			false, true);

		if (isNew) {
			notificationQueueEntry.setNew(false);
		}

		notificationQueueEntry.resetOriginalValues();

		return notificationQueueEntry;
	}

	/**
	 * Returns the notification queue entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the notification queue entry
	 * @return the notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchNotificationQueueEntryException {

		NotificationQueueEntry notificationQueueEntry = fetchByPrimaryKey(
			primaryKey);

		if (notificationQueueEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchNotificationQueueEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return notificationQueueEntry;
	}

	/**
	 * Returns the notification queue entry with the primary key or throws a <code>NoSuchNotificationQueueEntryException</code> if it could not be found.
	 *
	 * @param notificationQueueEntryId the primary key of the notification queue entry
	 * @return the notification queue entry
	 * @throws NoSuchNotificationQueueEntryException if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry findByPrimaryKey(
			long notificationQueueEntryId)
		throws NoSuchNotificationQueueEntryException {

		return findByPrimaryKey((Serializable)notificationQueueEntryId);
	}

	/**
	 * Returns the notification queue entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param notificationQueueEntryId the primary key of the notification queue entry
	 * @return the notification queue entry, or <code>null</code> if a notification queue entry with the primary key could not be found
	 */
	@Override
	public NotificationQueueEntry fetchByPrimaryKey(
		long notificationQueueEntryId) {

		return fetchByPrimaryKey((Serializable)notificationQueueEntryId);
	}

	/**
	 * Returns all the notification queue entries.
	 *
	 * @return the notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the notification queue entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @return the range of notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the notification queue entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findAll(
		int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the notification queue entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>NotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of notification queue entries
	 * @param end the upper bound of the range of notification queue entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of notification queue entries
	 */
	@Override
	public List<NotificationQueueEntry> findAll(
		int start, int end,
		OrderByComparator<NotificationQueueEntry> orderByComparator,
		boolean useFinderCache) {

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

		List<NotificationQueueEntry> list = null;

		if (useFinderCache) {
			list = (List<NotificationQueueEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_NOTIFICATIONQUEUEENTRY);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_NOTIFICATIONQUEUEENTRY;

				sql = sql.concat(NotificationQueueEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<NotificationQueueEntry>)QueryUtil.list(
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

	/**
	 * Removes all the notification queue entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (NotificationQueueEntry notificationQueueEntry : findAll()) {
			remove(notificationQueueEntry);
		}
	}

	/**
	 * Returns the number of notification queue entries.
	 *
	 * @return the number of notification queue entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(
					_SQL_COUNT_NOTIFICATIONQUEUEENTRY);

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
		return "notificationQueueEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_NOTIFICATIONQUEUEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return NotificationQueueEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the notification queue entry persistence.
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

		_finderPathWithPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId"}, true);

		_finderPathWithoutPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			true);

		_finderPathCountByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			false);

		_finderPathWithPaginationFindByNotificationTemplateId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByNotificationTemplateId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"notificationTemplateId"}, true);

		_finderPathWithoutPaginationFindByNotificationTemplateId =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByNotificationTemplateId",
				new String[] {Long.class.getName()},
				new String[] {"notificationTemplateId"}, true);

		_finderPathCountByNotificationTemplateId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByNotificationTemplateId",
			new String[] {Long.class.getName()},
			new String[] {"notificationTemplateId"}, false);

		_finderPathWithPaginationFindByLtSentDate = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLtSentDate",
			new String[] {
				Date.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"sentDate"}, true);

		_finderPathWithPaginationCountByLtSentDate = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByLtSentDate",
			new String[] {Date.class.getName()}, new String[] {"sentDate"},
			false);

		_finderPathWithPaginationFindByT_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_S",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"type_", "status"}, true);

		_finderPathWithoutPaginationFindByT_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_S",
			new String[] {String.class.getName(), Integer.class.getName()},
			new String[] {"type_", "status"}, true);

		_finderPathCountByT_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_S",
			new String[] {String.class.getName(), Integer.class.getName()},
			new String[] {"type_", "status"}, false);

		NotificationQueueEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		NotificationQueueEntryUtil.setPersistence(null);

		entityCache.removeCache(NotificationQueueEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = NotificationPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = NotificationPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = NotificationPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _SQL_SELECT_NOTIFICATIONQUEUEENTRY =
		"SELECT notificationQueueEntry FROM NotificationQueueEntry notificationQueueEntry";

	private static final String _SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE =
		"SELECT notificationQueueEntry FROM NotificationQueueEntry notificationQueueEntry WHERE ";

	private static final String _SQL_COUNT_NOTIFICATIONQUEUEENTRY =
		"SELECT COUNT(notificationQueueEntry) FROM NotificationQueueEntry notificationQueueEntry";

	private static final String _SQL_COUNT_NOTIFICATIONQUEUEENTRY_WHERE =
		"SELECT COUNT(notificationQueueEntry) FROM NotificationQueueEntry notificationQueueEntry WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"notificationQueueEntry.notificationQueueEntryId";

	private static final String
		_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_WHERE =
			"SELECT DISTINCT {notificationQueueEntry.*} FROM NotificationQueueEntry notificationQueueEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {NotificationQueueEntry.*} FROM (SELECT DISTINCT notificationQueueEntry.notificationQueueEntryId FROM NotificationQueueEntry notificationQueueEntry WHERE ";

	private static final String
		_FILTER_SQL_SELECT_NOTIFICATIONQUEUEENTRY_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN NotificationQueueEntry ON TEMP_TABLE.notificationQueueEntryId = NotificationQueueEntry.notificationQueueEntryId";

	private static final String _FILTER_SQL_COUNT_NOTIFICATIONQUEUEENTRY_WHERE =
		"SELECT COUNT(DISTINCT notificationQueueEntry.notificationQueueEntryId) AS COUNT_VALUE FROM NotificationQueueEntry notificationQueueEntry WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "notificationQueueEntry";

	private static final String _FILTER_ENTITY_TABLE = "NotificationQueueEntry";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"notificationQueueEntry.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"NotificationQueueEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No NotificationQueueEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No NotificationQueueEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		NotificationQueueEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}