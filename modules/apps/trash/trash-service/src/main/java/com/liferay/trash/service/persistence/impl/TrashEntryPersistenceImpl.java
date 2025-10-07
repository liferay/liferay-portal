/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service.persistence.impl;

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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.trash.exception.NoSuchEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashEntryTable;
import com.liferay.trash.model.impl.TrashEntryImpl;
import com.liferay.trash.model.impl.TrashEntryModelImpl;
import com.liferay.trash.service.persistence.TrashEntryPersistence;
import com.liferay.trash.service.persistence.TrashEntryUtil;
import com.liferay.trash.service.persistence.impl.constants.TrashPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the trash entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = TrashEntryPersistence.class)
public class TrashEntryPersistenceImpl
	extends BasePersistenceImpl<TrashEntry> implements TrashEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>TrashEntryUtil</code> to access the trash entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		TrashEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the trash entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching trash entries
	 */
	@Override
	public List<TrashEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the trash entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByGroupId;
					finderArgs = new Object[] {groupId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByGroupId;
				finderArgs = new Object[] {
					groupId, start, end, orderByComparator
				};
			}

			List<TrashEntry> list = null;

			if (useFinderCache) {
				list = (List<TrashEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (TrashEntry trashEntry : list) {
						if (groupId != trashEntry.getGroupId()) {
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

				sb.append(_SQL_SELECT_TRASHENTRY_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(TrashEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<TrashEntry>)QueryUtil.list(
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
	 * Returns the first trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByGroupId_First(
			long groupId, OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = fetchByGroupId_First(
			groupId, orderByComparator);

		if (trashEntry != null) {
			return trashEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByGroupId_First(
		long groupId, OrderByComparator<TrashEntry> orderByComparator) {

		List<TrashEntry> list = findByGroupId(groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByGroupId_Last(
			long groupId, OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = fetchByGroupId_Last(groupId, orderByComparator);

		if (trashEntry != null) {
			return trashEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByGroupId_Last(
		long groupId, OrderByComparator<TrashEntry> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<TrashEntry> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry[] findByGroupId_PrevAndNext(
			long entryId, long groupId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			TrashEntry[] array = new TrashEntryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, trashEntry, groupId, orderByComparator, true);

			array[1] = trashEntry;

			array[2] = getByGroupId_PrevAndNext(
				session, trashEntry, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected TrashEntry getByGroupId_PrevAndNext(
		Session session, TrashEntry trashEntry, long groupId,
		OrderByComparator<TrashEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_TRASHENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

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
			sb.append(TrashEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(trashEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<TrashEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the trash entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (TrashEntry trashEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(trashEntry);
		}
	}

	/**
	 * Returns the number of trash entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching trash entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_TRASHENTRY_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"trashEntry.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the trash entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching trash entries
	 */
	@Override
	public List<TrashEntry> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the trash entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

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

			List<TrashEntry> list = null;

			if (useFinderCache) {
				list = (List<TrashEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (TrashEntry trashEntry : list) {
						if (companyId != trashEntry.getCompanyId()) {
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

				sb.append(_SQL_SELECT_TRASHENTRY_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(TrashEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<TrashEntry>)QueryUtil.list(
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
	 * Returns the first trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByCompanyId_First(
			long companyId, OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (trashEntry != null) {
			return trashEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<TrashEntry> orderByComparator) {

		List<TrashEntry> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByCompanyId_Last(
			long companyId, OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (trashEntry != null) {
			return trashEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByCompanyId_Last(
		long companyId, OrderByComparator<TrashEntry> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<TrashEntry> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry[] findByCompanyId_PrevAndNext(
			long entryId, long companyId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			TrashEntry[] array = new TrashEntryImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, trashEntry, companyId, orderByComparator, true);

			array[1] = trashEntry;

			array[2] = getByCompanyId_PrevAndNext(
				session, trashEntry, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected TrashEntry getByCompanyId_PrevAndNext(
		Session session, TrashEntry trashEntry, long companyId,
		OrderByComparator<TrashEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_TRASHENTRY_WHERE);

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
			sb.append(TrashEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(trashEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<TrashEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the trash entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (TrashEntry trashEntry :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(trashEntry);
		}
	}

	/**
	 * Returns the number of trash entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching trash entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_TRASHENTRY_WHERE);

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
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"trashEntry.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_LtCD;
	private FinderPath _finderPathWithPaginationCountByG_LtCD;

	/**
	 * Returns all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @return the matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_LtCD(long groupId, Date createDate) {
		return findByG_LtCD(
			groupId, createDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate, int start, int end) {

		return findByG_LtCD(groupId, createDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return findByG_LtCD(
			groupId, createDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_LtCD;
			finderArgs = new Object[] {
				groupId, _getTime(createDate), start, end, orderByComparator
			};

			List<TrashEntry> list = null;

			if (useFinderCache) {
				list = (List<TrashEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (TrashEntry trashEntry : list) {
						if ((groupId != trashEntry.getGroupId()) ||
							(createDate.getTime() <= trashEntry.getCreateDate(
							).getTime())) {

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

				sb.append(_SQL_SELECT_TRASHENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_LTCD_GROUPID_2);

				boolean bindCreateDate = false;

				if (createDate == null) {
					sb.append(_FINDER_COLUMN_G_LTCD_CREATEDATE_1);
				}
				else {
					bindCreateDate = true;

					sb.append(_FINDER_COLUMN_G_LTCD_CREATEDATE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(TrashEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindCreateDate) {
						queryPos.add(new Timestamp(createDate.getTime()));
					}

					list = (List<TrashEntry>)QueryUtil.list(
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
	 * Returns the first trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByG_LtCD_First(
			long groupId, Date createDate,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = fetchByG_LtCD_First(
			groupId, createDate, orderByComparator);

		if (trashEntry != null) {
			return trashEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", createDate<");
		sb.append(createDate);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByG_LtCD_First(
		long groupId, Date createDate,
		OrderByComparator<TrashEntry> orderByComparator) {

		List<TrashEntry> list = findByG_LtCD(
			groupId, createDate, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByG_LtCD_Last(
			long groupId, Date createDate,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = fetchByG_LtCD_Last(
			groupId, createDate, orderByComparator);

		if (trashEntry != null) {
			return trashEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", createDate<");
		sb.append(createDate);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByG_LtCD_Last(
		long groupId, Date createDate,
		OrderByComparator<TrashEntry> orderByComparator) {

		int count = countByG_LtCD(groupId, createDate);

		if (count == 0) {
			return null;
		}

		List<TrashEntry> list = findByG_LtCD(
			groupId, createDate, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry[] findByG_LtCD_PrevAndNext(
			long entryId, long groupId, Date createDate,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			TrashEntry[] array = new TrashEntryImpl[3];

			array[0] = getByG_LtCD_PrevAndNext(
				session, trashEntry, groupId, createDate, orderByComparator,
				true);

			array[1] = trashEntry;

			array[2] = getByG_LtCD_PrevAndNext(
				session, trashEntry, groupId, createDate, orderByComparator,
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

	protected TrashEntry getByG_LtCD_PrevAndNext(
		Session session, TrashEntry trashEntry, long groupId, Date createDate,
		OrderByComparator<TrashEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_TRASHENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_LTCD_GROUPID_2);

		boolean bindCreateDate = false;

		if (createDate == null) {
			sb.append(_FINDER_COLUMN_G_LTCD_CREATEDATE_1);
		}
		else {
			bindCreateDate = true;

			sb.append(_FINDER_COLUMN_G_LTCD_CREATEDATE_2);
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
			sb.append(TrashEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindCreateDate) {
			queryPos.add(new Timestamp(createDate.getTime()));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(trashEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<TrashEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the trash entries where groupId = &#63; and createDate &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 */
	@Override
	public void removeByG_LtCD(long groupId, Date createDate) {
		for (TrashEntry trashEntry :
				findByG_LtCD(
					groupId, createDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(trashEntry);
		}
	}

	/**
	 * Returns the number of trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @return the number of matching trash entries
	 */
	@Override
	public int countByG_LtCD(long groupId, Date createDate) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_LtCD;

			Object[] finderArgs = new Object[] {groupId, _getTime(createDate)};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_TRASHENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_LTCD_GROUPID_2);

				boolean bindCreateDate = false;

				if (createDate == null) {
					sb.append(_FINDER_COLUMN_G_LTCD_CREATEDATE_1);
				}
				else {
					bindCreateDate = true;

					sb.append(_FINDER_COLUMN_G_LTCD_CREATEDATE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindCreateDate) {
						queryPos.add(new Timestamp(createDate.getTime()));
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

	private static final String _FINDER_COLUMN_G_LTCD_GROUPID_2 =
		"trashEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_LTCD_CREATEDATE_1 =
		"trashEntry.createDate IS NULL";

	private static final String _FINDER_COLUMN_G_LTCD_CREATEDATE_2 =
		"trashEntry.createDate < ?";

	private FinderPath _finderPathWithPaginationFindByG_CN;
	private FinderPath _finderPathWithoutPaginationFindByG_CN;
	private FinderPath _finderPathCountByG_CN;

	/**
	 * Returns all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_CN(long groupId, long classNameId) {
		return findByG_CN(
			groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_CN(
		long groupId, long classNameId, int start, int end) {

		return findByG_CN(groupId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return findByG_CN(
			groupId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_CN;
					finderArgs = new Object[] {groupId, classNameId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_CN;
				finderArgs = new Object[] {
					groupId, classNameId, start, end, orderByComparator
				};
			}

			List<TrashEntry> list = null;

			if (useFinderCache) {
				list = (List<TrashEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (TrashEntry trashEntry : list) {
						if ((groupId != trashEntry.getGroupId()) ||
							(classNameId != trashEntry.getClassNameId())) {

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

				sb.append(_SQL_SELECT_TRASHENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_CN_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_CN_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(TrashEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

					list = (List<TrashEntry>)QueryUtil.list(
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
	 * Returns the first trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByG_CN_First(
			long groupId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = fetchByG_CN_First(
			groupId, classNameId, orderByComparator);

		if (trashEntry != null) {
			return trashEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByG_CN_First(
		long groupId, long classNameId,
		OrderByComparator<TrashEntry> orderByComparator) {

		List<TrashEntry> list = findByG_CN(
			groupId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByG_CN_Last(
			long groupId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = fetchByG_CN_Last(
			groupId, classNameId, orderByComparator);

		if (trashEntry != null) {
			return trashEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByG_CN_Last(
		long groupId, long classNameId,
		OrderByComparator<TrashEntry> orderByComparator) {

		int count = countByG_CN(groupId, classNameId);

		if (count == 0) {
			return null;
		}

		List<TrashEntry> list = findByG_CN(
			groupId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry[] findByG_CN_PrevAndNext(
			long entryId, long groupId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			TrashEntry[] array = new TrashEntryImpl[3];

			array[0] = getByG_CN_PrevAndNext(
				session, trashEntry, groupId, classNameId, orderByComparator,
				true);

			array[1] = trashEntry;

			array[2] = getByG_CN_PrevAndNext(
				session, trashEntry, groupId, classNameId, orderByComparator,
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

	protected TrashEntry getByG_CN_PrevAndNext(
		Session session, TrashEntry trashEntry, long groupId, long classNameId,
		OrderByComparator<TrashEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_TRASHENTRY_WHERE);

		sb.append(_FINDER_COLUMN_G_CN_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_CN_CLASSNAMEID_2);

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
			sb.append(TrashEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(trashEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<TrashEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the trash entries where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_CN(long groupId, long classNameId) {
		for (TrashEntry trashEntry :
				findByG_CN(
					groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(trashEntry);
		}
	}

	/**
	 * Returns the number of trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching trash entries
	 */
	@Override
	public int countByG_CN(long groupId, long classNameId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

			FinderPath finderPath = _finderPathCountByG_CN;

			Object[] finderArgs = new Object[] {groupId, classNameId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_TRASHENTRY_WHERE);

				sb.append(_FINDER_COLUMN_G_CN_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_CN_CLASSNAMEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(classNameId);

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

	private static final String _FINDER_COLUMN_G_CN_GROUPID_2 =
		"trashEntry.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_CN_CLASSNAMEID_2 =
		"trashEntry.classNameId = ?";

	private FinderPath _finderPathWithPaginationFindByC_CN;
	private FinderPath _finderPathWithoutPaginationFindByC_CN;
	private FinderPath _finderPathCountByC_CN;

	/**
	 * Returns all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching trash entries
	 */
	@Override
	public List<TrashEntry> findByC_CN(long companyId, long classNameId) {
		return findByC_CN(
			companyId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByC_CN(
		long companyId, long classNameId, int start, int end) {

		return findByC_CN(companyId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return findByC_CN(
			companyId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_CN;
					finderArgs = new Object[] {companyId, classNameId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_CN;
				finderArgs = new Object[] {
					companyId, classNameId, start, end, orderByComparator
				};
			}

			List<TrashEntry> list = null;

			if (useFinderCache) {
				list = (List<TrashEntry>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (TrashEntry trashEntry : list) {
						if ((companyId != trashEntry.getCompanyId()) ||
							(classNameId != trashEntry.getClassNameId())) {

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

				sb.append(_SQL_SELECT_TRASHENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_CN_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_CN_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(TrashEntryModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					list = (List<TrashEntry>)QueryUtil.list(
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
	 * Returns the first trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = fetchByC_CN_First(
			companyId, classNameId, orderByComparator);

		if (trashEntry != null) {
			return trashEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<TrashEntry> orderByComparator) {

		List<TrashEntry> list = findByC_CN(
			companyId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByC_CN_Last(
			long companyId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = fetchByC_CN_Last(
			companyId, classNameId, orderByComparator);

		if (trashEntry != null) {
			return trashEntry;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchEntryException(sb.toString());
	}

	/**
	 * Returns the last trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByC_CN_Last(
		long companyId, long classNameId,
		OrderByComparator<TrashEntry> orderByComparator) {

		int count = countByC_CN(companyId, classNameId);

		if (count == 0) {
			return null;
		}

		List<TrashEntry> list = findByC_CN(
			companyId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry[] findByC_CN_PrevAndNext(
			long entryId, long companyId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		TrashEntry trashEntry = findByPrimaryKey(entryId);

		Session session = null;

		try {
			session = openSession();

			TrashEntry[] array = new TrashEntryImpl[3];

			array[0] = getByC_CN_PrevAndNext(
				session, trashEntry, companyId, classNameId, orderByComparator,
				true);

			array[1] = trashEntry;

			array[2] = getByC_CN_PrevAndNext(
				session, trashEntry, companyId, classNameId, orderByComparator,
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

	protected TrashEntry getByC_CN_PrevAndNext(
		Session session, TrashEntry trashEntry, long companyId,
		long classNameId, OrderByComparator<TrashEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_TRASHENTRY_WHERE);

		sb.append(_FINDER_COLUMN_C_CN_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_CN_CLASSNAMEID_2);

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
			sb.append(TrashEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(trashEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<TrashEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the trash entries where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_CN(long companyId, long classNameId) {
		for (TrashEntry trashEntry :
				findByC_CN(
					companyId, classNameId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(trashEntry);
		}
	}

	/**
	 * Returns the number of trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching trash entries
	 */
	@Override
	public int countByC_CN(long companyId, long classNameId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

			FinderPath finderPath = _finderPathCountByC_CN;

			Object[] finderArgs = new Object[] {companyId, classNameId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_TRASHENTRY_WHERE);

				sb.append(_FINDER_COLUMN_C_CN_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_CN_CLASSNAMEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

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

	private static final String _FINDER_COLUMN_C_CN_COMPANYID_2 =
		"trashEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_CN_CLASSNAMEID_2 =
		"trashEntry.classNameId = ?";

	private FinderPath _finderPathFetchByCN_CPK;

	/**
	 * Returns the trash entry where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByCN_CPK(long classNameId, long classPK)
		throws NoSuchEntryException {

		TrashEntry trashEntry = fetchByCN_CPK(classNameId, classPK);

		if (trashEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("classNameId=");
			sb.append(classNameId);

			sb.append(", classPK=");
			sb.append(classPK);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchEntryException(sb.toString());
		}

		return trashEntry;
	}

	/**
	 * Returns the trash entry where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByCN_CPK(long classNameId, long classPK) {
		return fetchByCN_CPK(classNameId, classPK, true);
	}

	/**
	 * Returns the trash entry where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByCN_CPK(
		long classNameId, long classPK, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {classNameId, classPK};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByCN_CPK, finderArgs, this);
			}

			if (result instanceof TrashEntry) {
				TrashEntry trashEntry = (TrashEntry)result;

				if ((classNameId != trashEntry.getClassNameId()) ||
					(classPK != trashEntry.getClassPK())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_TRASHENTRY_WHERE);

				sb.append(_FINDER_COLUMN_CN_CPK_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_CN_CPK_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					List<TrashEntry> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByCN_CPK, finderArgs, list);
						}
					}
					else {
						TrashEntry trashEntry = list.get(0);

						result = trashEntry;

						cacheResult(trashEntry);
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
				return (TrashEntry)result;
			}
		}
	}

	/**
	 * Removes the trash entry where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the trash entry that was removed
	 */
	@Override
	public TrashEntry removeByCN_CPK(long classNameId, long classPK)
		throws NoSuchEntryException {

		TrashEntry trashEntry = findByCN_CPK(classNameId, classPK);

		return remove(trashEntry);
	}

	/**
	 * Returns the number of trash entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching trash entries
	 */
	@Override
	public int countByCN_CPK(long classNameId, long classPK) {
		TrashEntry trashEntry = fetchByCN_CPK(classNameId, classPK);

		if (trashEntry == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_CN_CPK_CLASSNAMEID_2 =
		"trashEntry.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_CN_CPK_CLASSPK_2 =
		"trashEntry.classPK = ?";

	public TrashEntryPersistenceImpl() {
		setModelClass(TrashEntry.class);

		setModelImplClass(TrashEntryImpl.class);
		setModelPKClass(long.class);

		setTable(TrashEntryTable.INSTANCE);
	}

	/**
	 * Caches the trash entry in the entity cache if it is enabled.
	 *
	 * @param trashEntry the trash entry
	 */
	@Override
	public void cacheResult(TrashEntry trashEntry) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					trashEntry.getCtCollectionId())) {

			entityCache.putResult(
				TrashEntryImpl.class, trashEntry.getPrimaryKey(), trashEntry);

			finderCache.putResult(
				_finderPathFetchByCN_CPK,
				new Object[] {
					trashEntry.getClassNameId(), trashEntry.getClassPK()
				},
				trashEntry);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the trash entries in the entity cache if it is enabled.
	 *
	 * @param trashEntries the trash entries
	 */
	@Override
	public void cacheResult(List<TrashEntry> trashEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (trashEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (TrashEntry trashEntry : trashEntries) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						trashEntry.getCtCollectionId())) {

				if (entityCache.getResult(
						TrashEntryImpl.class, trashEntry.getPrimaryKey()) ==
							null) {

					cacheResult(trashEntry);
				}
			}
		}
	}

	/**
	 * Clears the cache for all trash entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(TrashEntryImpl.class);

		finderCache.clearCache(TrashEntryImpl.class);
	}

	/**
	 * Clears the cache for the trash entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(TrashEntry trashEntry) {
		entityCache.removeResult(TrashEntryImpl.class, trashEntry);
	}

	@Override
	public void clearCache(List<TrashEntry> trashEntries) {
		for (TrashEntry trashEntry : trashEntries) {
			entityCache.removeResult(TrashEntryImpl.class, trashEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(TrashEntryImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(TrashEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		TrashEntryModelImpl trashEntryModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					trashEntryModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				trashEntryModelImpl.getClassNameId(),
				trashEntryModelImpl.getClassPK()
			};

			finderCache.putResult(
				_finderPathFetchByCN_CPK, args, trashEntryModelImpl);
		}
	}

	/**
	 * Creates a new trash entry with the primary key. Does not add the trash entry to the database.
	 *
	 * @param entryId the primary key for the new trash entry
	 * @return the new trash entry
	 */
	@Override
	public TrashEntry create(long entryId) {
		TrashEntry trashEntry = new TrashEntryImpl();

		trashEntry.setNew(true);
		trashEntry.setPrimaryKey(entryId);

		trashEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return trashEntry;
	}

	/**
	 * Removes the trash entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry that was removed
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry remove(long entryId) throws NoSuchEntryException {
		return remove((Serializable)entryId);
	}

	/**
	 * Removes the trash entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the trash entry
	 * @return the trash entry that was removed
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry remove(Serializable primaryKey)
		throws NoSuchEntryException {

		Session session = null;

		try {
			session = openSession();

			TrashEntry trashEntry = (TrashEntry)session.get(
				TrashEntryImpl.class, primaryKey);

			if (trashEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(trashEntry);
		}
		catch (NoSuchEntryException noSuchEntityException) {
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
	protected TrashEntry removeImpl(TrashEntry trashEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(trashEntry)) {
				trashEntry = (TrashEntry)session.get(
					TrashEntryImpl.class, trashEntry.getPrimaryKeyObj());
			}

			if ((trashEntry != null) &&
				ctPersistenceHelper.isRemove(trashEntry)) {

				session.delete(trashEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (trashEntry != null) {
			clearCache(trashEntry);
		}

		return trashEntry;
	}

	@Override
	public TrashEntry updateImpl(TrashEntry trashEntry) {
		boolean isNew = trashEntry.isNew();

		if (!(trashEntry instanceof TrashEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(trashEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(trashEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in trashEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom TrashEntry implementation " +
					trashEntry.getClass());
		}

		TrashEntryModelImpl trashEntryModelImpl =
			(TrashEntryModelImpl)trashEntry;

		if (isNew && (trashEntry.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				trashEntry.setCreateDate(date);
			}
			else {
				trashEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(trashEntry)) {
				if (!isNew) {
					session.evict(
						TrashEntryImpl.class, trashEntry.getPrimaryKeyObj());
				}

				session.save(trashEntry);
			}
			else {
				trashEntry = (TrashEntry)session.merge(trashEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			TrashEntryImpl.class, trashEntryModelImpl, false, true);

		cacheUniqueFindersCache(trashEntryModelImpl);

		if (isNew) {
			trashEntry.setNew(false);
		}

		trashEntry.resetOriginalValues();

		return trashEntry;
	}

	/**
	 * Returns the trash entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the trash entry
	 * @return the trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchEntryException {

		TrashEntry trashEntry = fetchByPrimaryKey(primaryKey);

		if (trashEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return trashEntry;
	}

	/**
	 * Returns the trash entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry findByPrimaryKey(long entryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)entryId);
	}

	/**
	 * Returns the trash entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the trash entry
	 * @return the trash entry, or <code>null</code> if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				TrashEntry.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		TrashEntry trashEntry = (TrashEntry)entityCache.getResult(
			TrashEntryImpl.class, primaryKey);

		if (trashEntry != null) {
			return trashEntry;
		}

		Session session = null;

		try {
			session = openSession();

			trashEntry = (TrashEntry)session.get(
				TrashEntryImpl.class, primaryKey);

			if (trashEntry != null) {
				cacheResult(trashEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return trashEntry;
	}

	/**
	 * Returns the trash entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry, or <code>null</code> if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry fetchByPrimaryKey(long entryId) {
		return fetchByPrimaryKey((Serializable)entryId);
	}

	@Override
	public Map<Serializable, TrashEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(TrashEntry.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, TrashEntry> map =
			new HashMap<Serializable, TrashEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			TrashEntry trashEntry = fetchByPrimaryKey(primaryKey);

			if (trashEntry != null) {
				map.put(primaryKey, trashEntry);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						TrashEntry.class, primaryKey)) {

				TrashEntry trashEntry = (TrashEntry)entityCache.getResult(
					TrashEntryImpl.class, primaryKey);

				if (trashEntry == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, trashEntry);
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

			for (TrashEntry trashEntry : (List<TrashEntry>)query.list()) {
				map.put(trashEntry.getPrimaryKeyObj(), trashEntry);

				cacheResult(trashEntry);
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
	 * Returns all the trash entries.
	 *
	 * @return the trash entries
	 */
	@Override
	public List<TrashEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the trash entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of trash entries
	 */
	@Override
	public List<TrashEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the trash entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of trash entries
	 */
	@Override
	public List<TrashEntry> findAll(
		int start, int end, OrderByComparator<TrashEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the trash entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of trash entries
	 */
	@Override
	public List<TrashEntry> findAll(
		int start, int end, OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

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

			List<TrashEntry> list = null;

			if (useFinderCache) {
				list = (List<TrashEntry>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_TRASHENTRY);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_TRASHENTRY;

					sql = sql.concat(TrashEntryModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<TrashEntry>)QueryUtil.list(
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
	 * Removes all the trash entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (TrashEntry trashEntry : findAll()) {
			remove(trashEntry);
		}
	}

	/**
	 * Returns the number of trash entries.
	 *
	 * @return the number of trash entries
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					TrashEntry.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_TRASHENTRY);

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
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "entryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_TRASHENTRY;
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
		return TrashEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "TrashEntry";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("systemEventSetKey");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("entryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"classNameId", "classPK"});
	}

	/**
	 * Initializes the trash entry persistence.
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

		_finderPathWithPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId"}, true);

		_finderPathWithoutPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			true);

		_finderPathCountByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

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

		_finderPathWithPaginationFindByG_LtCD = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LtCD",
			new String[] {
				Long.class.getName(), Date.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "createDate"}, true);

		_finderPathWithPaginationCountByG_LtCD = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LtCD",
			new String[] {Long.class.getName(), Date.class.getName()},
			new String[] {"groupId", "createDate"}, false);

		_finderPathWithPaginationFindByG_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_CN",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByG_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_CN",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classNameId"}, true);

		_finderPathCountByG_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_CN",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classNameId"}, false);

		_finderPathWithPaginationFindByC_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByC_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CN",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "classNameId"}, true);

		_finderPathCountByC_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CN",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "classNameId"}, false);

		_finderPathFetchByCN_CPK = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByCN_CPK",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"classNameId", "classPK"}, true);

		TrashEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		TrashEntryUtil.setPersistence(null);

		entityCache.removeCache(TrashEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = TrashPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = TrashPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = TrashPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _SQL_SELECT_TRASHENTRY =
		"SELECT trashEntry FROM TrashEntry trashEntry";

	private static final String _SQL_SELECT_TRASHENTRY_WHERE =
		"SELECT trashEntry FROM TrashEntry trashEntry WHERE ";

	private static final String _SQL_COUNT_TRASHENTRY =
		"SELECT COUNT(trashEntry) FROM TrashEntry trashEntry";

	private static final String _SQL_COUNT_TRASHENTRY_WHERE =
		"SELECT COUNT(trashEntry) FROM TrashEntry trashEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "trashEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No TrashEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No TrashEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		TrashEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}