/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat700.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat700.exception.NoSuchWhereClauseEntryException;
import com.liferay.portal.tools.service.builder.test.compat700.model.WhereClauseEntry;
import com.liferay.portal.tools.service.builder.test.compat700.model.impl.WhereClauseEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat700.model.impl.WhereClauseEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat700.service.persistence.WhereClauseEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat700.service.persistence.WhereClauseEntryUtil;

import java.io.Serializable;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the where clause entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class WhereClauseEntryPersistenceImpl
	extends BasePersistenceImpl<WhereClauseEntry>
	implements WhereClauseEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>WhereClauseEntryUtil</code> to access the where clause entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		WhereClauseEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private static Object _split(Object array, int splitSize) {
		int length = Array.getLength(array);

		int pageCount = length / splitSize;

		if ((length % splitSize) > 0) {
			pageCount++;
		}

		Class<?> clazz = array.getClass();

		Class<?> componentType = clazz.getComponentType();

		Object newArray = Array.newInstance(
			componentType, pageCount, splitSize);

		if (pageCount == 1) {
			Array.set(newArray, 0, array);

			return newArray;
		}

		for (int i = 0; i < pageCount; i++) {
			int end = Math.min(length, splitSize * (i + 1));
			int start = splitSize * i;

			int elementLength = end - start;

			Object element = Array.newInstance(componentType, elementLength);

			System.arraycopy(array, start, element, 0, elementLength);

			Array.set(newArray, i, element);
		}

		return newArray;
	}

	private int _databaseInMaxParameters;
	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByName_Nickname;
	private FinderPath _finderPathWithoutPaginationFindByName_Nickname;
	private FinderPath _finderPathCountByName_Nickname;

	/**
	 * Returns all the where clause entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the matching where clause entries
	 */
	@Override
	public List<WhereClauseEntry> findByName_Nickname(String name) {
		return findByName_Nickname(
			name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the where clause entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of where clause entries
	 * @param end the upper bound of the range of where clause entries (not inclusive)
	 * @return the range of matching where clause entries
	 */
	@Override
	public List<WhereClauseEntry> findByName_Nickname(
		String name, int start, int end) {

		return findByName_Nickname(name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the where clause entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of where clause entries
	 * @param end the upper bound of the range of where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching where clause entries
	 */
	@Override
	public List<WhereClauseEntry> findByName_Nickname(
		String name, int start, int end,
		OrderByComparator<WhereClauseEntry> orderByComparator) {

		return findByName_Nickname(name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the where clause entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of where clause entries
	 * @param end the upper bound of the range of where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching where clause entries
	 */
	@Override
	public List<WhereClauseEntry> findByName_Nickname(
		String name, int start, int end,
		OrderByComparator<WhereClauseEntry> orderByComparator,
		boolean useFinderCache) {

		name = Objects.toString(name, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByName_Nickname;
				finderArgs = new Object[] {name};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByName_Nickname;
			finderArgs = new Object[] {name, start, end, orderByComparator};
		}

		List<WhereClauseEntry> list = null;

		if (useFinderCache) {
			list = (List<WhereClauseEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (WhereClauseEntry whereClauseEntry : list) {
					if (!name.equals(whereClauseEntry.getName())) {
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

			sb.append(_SQL_SELECT_WHERECLAUSEENTRY_WHERE);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_NAME_NICKNAME_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_NAME_NICKNAME_NAME_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(WhereClauseEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindName) {
					queryPos.add(name);
				}

				list = (List<WhereClauseEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(finderPath, finderArgs);
				}

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first where clause entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching where clause entry
	 * @throws NoSuchWhereClauseEntryException if a matching where clause entry could not be found
	 */
	@Override
	public WhereClauseEntry findByName_Nickname_First(
			String name, OrderByComparator<WhereClauseEntry> orderByComparator)
		throws NoSuchWhereClauseEntryException {

		WhereClauseEntry whereClauseEntry = fetchByName_Nickname_First(
			name, orderByComparator);

		if (whereClauseEntry != null) {
			return whereClauseEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchWhereClauseEntryException(sb.toString());
	}

	/**
	 * Returns the first where clause entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching where clause entry, or <code>null</code> if a matching where clause entry could not be found
	 */
	@Override
	public WhereClauseEntry fetchByName_Nickname_First(
		String name, OrderByComparator<WhereClauseEntry> orderByComparator) {

		List<WhereClauseEntry> list = findByName_Nickname(
			name, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last where clause entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching where clause entry
	 * @throws NoSuchWhereClauseEntryException if a matching where clause entry could not be found
	 */
	@Override
	public WhereClauseEntry findByName_Nickname_Last(
			String name, OrderByComparator<WhereClauseEntry> orderByComparator)
		throws NoSuchWhereClauseEntryException {

		WhereClauseEntry whereClauseEntry = fetchByName_Nickname_Last(
			name, orderByComparator);

		if (whereClauseEntry != null) {
			return whereClauseEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchWhereClauseEntryException(sb.toString());
	}

	/**
	 * Returns the last where clause entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching where clause entry, or <code>null</code> if a matching where clause entry could not be found
	 */
	@Override
	public WhereClauseEntry fetchByName_Nickname_Last(
		String name, OrderByComparator<WhereClauseEntry> orderByComparator) {

		int count = countByName_Nickname(name);

		if (count == 0) {
			return null;
		}

		List<WhereClauseEntry> list = findByName_Nickname(
			name, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the where clause entries before and after the current where clause entry in the ordered set where name = &#63;.
	 *
	 * @param whereClauseEntryId the primary key of the current where clause entry
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next where clause entry
	 * @throws NoSuchWhereClauseEntryException if a where clause entry with the primary key could not be found
	 */
	@Override
	public WhereClauseEntry[] findByName_Nickname_PrevAndNext(
			long whereClauseEntryId, String name,
			OrderByComparator<WhereClauseEntry> orderByComparator)
		throws NoSuchWhereClauseEntryException {

		name = Objects.toString(name, "");

		WhereClauseEntry whereClauseEntry = findByPrimaryKey(
			whereClauseEntryId);

		Session session = null;

		try {
			session = openSession();

			WhereClauseEntry[] array = new WhereClauseEntryImpl[3];

			array[0] = getByName_Nickname_PrevAndNext(
				session, whereClauseEntry, name, orderByComparator, true);

			array[1] = whereClauseEntry;

			array[2] = getByName_Nickname_PrevAndNext(
				session, whereClauseEntry, name, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WhereClauseEntry getByName_Nickname_PrevAndNext(
		Session session, WhereClauseEntry whereClauseEntry, String name,
		OrderByComparator<WhereClauseEntry> orderByComparator,
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

		sb.append(_SQL_SELECT_WHERECLAUSEENTRY_WHERE);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_NAME_NICKNAME_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_NAME_NICKNAME_NAME_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ENTITY_ALIAS_PREFIX);
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
				sb.append(_ENTITY_ALIAS_PREFIX);
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
			sb.append(WhereClauseEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindName) {
			queryPos.add(name);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						whereClauseEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WhereClauseEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the where clause entries where name = &#63; from the database.
	 *
	 * @param name the name
	 */
	@Override
	public void removeByName_Nickname(String name) {
		for (WhereClauseEntry whereClauseEntry :
				findByName_Nickname(
					name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(whereClauseEntry);
		}
	}

	/**
	 * Returns the number of where clause entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching where clause entries
	 */
	@Override
	public int countByName_Nickname(String name) {
		name = Objects.toString(name, "");

		FinderPath finderPath = _finderPathCountByName_Nickname;

		Object[] finderArgs = new Object[] {name};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_WHERECLAUSEENTRY_WHERE);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_NAME_NICKNAME_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_NAME_NICKNAME_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindName) {
					queryPos.add(name);
				}

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_NAME_NICKNAME_NAME_2 =
		"whereClauseEntry.name = ? AND whereClauseEntry.nickname IS NOT NULL";

	private static final String _FINDER_COLUMN_NAME_NICKNAME_NAME_3 =
		"(whereClauseEntry.name IS NULL OR whereClauseEntry.name = '') AND whereClauseEntry.nickname IS NOT NULL";

	public WhereClauseEntryPersistenceImpl() {
		setModelClass(WhereClauseEntry.class);
	}

	/**
	 * Caches the where clause entry in the entity cache if it is enabled.
	 *
	 * @param whereClauseEntry the where clause entry
	 */
	@Override
	public void cacheResult(WhereClauseEntry whereClauseEntry) {
		entityCache.putResult(
			WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
			WhereClauseEntryImpl.class, whereClauseEntry.getPrimaryKey(),
			whereClauseEntry);

		whereClauseEntry.resetOriginalValues();
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the where clause entries in the entity cache if it is enabled.
	 *
	 * @param whereClauseEntries the where clause entries
	 */
	@Override
	public void cacheResult(List<WhereClauseEntry> whereClauseEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (whereClauseEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (WhereClauseEntry whereClauseEntry : whereClauseEntries) {
			if (entityCache.getResult(
					WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
					WhereClauseEntryImpl.class,
					whereClauseEntry.getPrimaryKey()) == null) {

				cacheResult(whereClauseEntry);
			}
			else {
				whereClauseEntry.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all where clause entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(WhereClauseEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the where clause entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(WhereClauseEntry whereClauseEntry) {
		entityCache.removeResult(
			WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
			WhereClauseEntryImpl.class, whereClauseEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<WhereClauseEntry> whereClauseEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (WhereClauseEntry whereClauseEntry : whereClauseEntries) {
			entityCache.removeResult(
				WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
				WhereClauseEntryImpl.class, whereClauseEntry.getPrimaryKey());
		}
	}

	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
				WhereClauseEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new where clause entry with the primary key. Does not add the where clause entry to the database.
	 *
	 * @param whereClauseEntryId the primary key for the new where clause entry
	 * @return the new where clause entry
	 */
	@Override
	public WhereClauseEntry create(long whereClauseEntryId) {
		WhereClauseEntry whereClauseEntry = new WhereClauseEntryImpl();

		whereClauseEntry.setNew(true);
		whereClauseEntry.setPrimaryKey(whereClauseEntryId);

		return whereClauseEntry;
	}

	/**
	 * Removes the where clause entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param whereClauseEntryId the primary key of the where clause entry
	 * @return the where clause entry that was removed
	 * @throws NoSuchWhereClauseEntryException if a where clause entry with the primary key could not be found
	 */
	@Override
	public WhereClauseEntry remove(long whereClauseEntryId)
		throws NoSuchWhereClauseEntryException {

		return remove((Serializable)whereClauseEntryId);
	}

	/**
	 * Removes the where clause entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the where clause entry
	 * @return the where clause entry that was removed
	 * @throws NoSuchWhereClauseEntryException if a where clause entry with the primary key could not be found
	 */
	@Override
	public WhereClauseEntry remove(Serializable primaryKey)
		throws NoSuchWhereClauseEntryException {

		Session session = null;

		try {
			session = openSession();

			WhereClauseEntry whereClauseEntry = (WhereClauseEntry)session.get(
				WhereClauseEntryImpl.class, primaryKey);

			if (whereClauseEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchWhereClauseEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(whereClauseEntry);
		}
		catch (NoSuchWhereClauseEntryException noSuchEntityException) {
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
	protected WhereClauseEntry removeImpl(WhereClauseEntry whereClauseEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(whereClauseEntry)) {
				whereClauseEntry = (WhereClauseEntry)session.get(
					WhereClauseEntryImpl.class,
					whereClauseEntry.getPrimaryKeyObj());
			}

			if (whereClauseEntry != null) {
				session.delete(whereClauseEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (whereClauseEntry != null) {
			clearCache(whereClauseEntry);
		}

		return whereClauseEntry;
	}

	@Override
	public WhereClauseEntry updateImpl(WhereClauseEntry whereClauseEntry) {
		boolean isNew = whereClauseEntry.isNew();

		if (!(whereClauseEntry instanceof WhereClauseEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(whereClauseEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					whereClauseEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in whereClauseEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom WhereClauseEntry implementation " +
					whereClauseEntry.getClass());
		}

		WhereClauseEntryModelImpl whereClauseEntryModelImpl =
			(WhereClauseEntryModelImpl)whereClauseEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(whereClauseEntry);

				whereClauseEntry.setNew(false);
			}
			else {
				whereClauseEntry = (WhereClauseEntry)session.merge(
					whereClauseEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!WhereClauseEntryModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else if (isNew) {
			Object[] args = new Object[] {whereClauseEntryModelImpl.getName()};

			finderCache.removeResult(_finderPathCountByName_Nickname, args);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindByName_Nickname, args);

			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
		}
		else {
			if ((whereClauseEntryModelImpl.getColumnBitmask() &
				 _finderPathWithoutPaginationFindByName_Nickname.
					 getColumnBitmask()) != 0) {

				Object[] args = new Object[] {
					whereClauseEntryModelImpl.getOriginalName()
				};

				finderCache.removeResult(_finderPathCountByName_Nickname, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByName_Nickname, args);

				args = new Object[] {whereClauseEntryModelImpl.getName()};

				finderCache.removeResult(_finderPathCountByName_Nickname, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByName_Nickname, args);
			}
		}

		entityCache.putResult(
			WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
			WhereClauseEntryImpl.class, whereClauseEntry.getPrimaryKey(),
			whereClauseEntry, false);

		whereClauseEntry.resetOriginalValues();

		return whereClauseEntry;
	}

	/**
	 * Returns the where clause entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the where clause entry
	 * @return the where clause entry
	 * @throws NoSuchWhereClauseEntryException if a where clause entry with the primary key could not be found
	 */
	@Override
	public WhereClauseEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchWhereClauseEntryException {

		WhereClauseEntry whereClauseEntry = fetchByPrimaryKey(primaryKey);

		if (whereClauseEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchWhereClauseEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return whereClauseEntry;
	}

	/**
	 * Returns the where clause entry with the primary key or throws a <code>NoSuchWhereClauseEntryException</code> if it could not be found.
	 *
	 * @param whereClauseEntryId the primary key of the where clause entry
	 * @return the where clause entry
	 * @throws NoSuchWhereClauseEntryException if a where clause entry with the primary key could not be found
	 */
	@Override
	public WhereClauseEntry findByPrimaryKey(long whereClauseEntryId)
		throws NoSuchWhereClauseEntryException {

		return findByPrimaryKey((Serializable)whereClauseEntryId);
	}

	/**
	 * Returns the where clause entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the where clause entry
	 * @return the where clause entry, or <code>null</code> if a where clause entry with the primary key could not be found
	 */
	@Override
	public WhereClauseEntry fetchByPrimaryKey(Serializable primaryKey) {
		Serializable serializable = entityCache.getResult(
			WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
			WhereClauseEntryImpl.class, primaryKey);

		if (serializable == nullModel) {
			return null;
		}

		WhereClauseEntry whereClauseEntry = (WhereClauseEntry)serializable;

		if (whereClauseEntry == null) {
			Session session = null;

			try {
				session = openSession();

				whereClauseEntry = (WhereClauseEntry)session.get(
					WhereClauseEntryImpl.class, primaryKey);

				if (whereClauseEntry != null) {
					cacheResult(whereClauseEntry);
				}
				else {
					entityCache.putResult(
						WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
						WhereClauseEntryImpl.class, primaryKey, nullModel);
				}
			}
			catch (Exception exception) {
				entityCache.removeResult(
					WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
					WhereClauseEntryImpl.class, primaryKey);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return whereClauseEntry;
	}

	/**
	 * Returns the where clause entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param whereClauseEntryId the primary key of the where clause entry
	 * @return the where clause entry, or <code>null</code> if a where clause entry with the primary key could not be found
	 */
	@Override
	public WhereClauseEntry fetchByPrimaryKey(long whereClauseEntryId) {
		return fetchByPrimaryKey((Serializable)whereClauseEntryId);
	}

	@Override
	public Map<Serializable, WhereClauseEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, WhereClauseEntry> map =
			new HashMap<Serializable, WhereClauseEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			WhereClauseEntry whereClauseEntry = fetchByPrimaryKey(primaryKey);

			if (whereClauseEntry != null) {
				map.put(primaryKey, whereClauseEntry);
			}

			return map;
		}

		if ((_databaseInMaxParameters > 0) &&
			(primaryKeys.size() > _databaseInMaxParameters)) {

			Iterator<Serializable> iterator = primaryKeys.iterator();

			while (iterator.hasNext()) {
				Set<Serializable> page = new HashSet<>();

				for (int i = 0;
					 (i < _databaseInMaxParameters) && iterator.hasNext();
					 i++) {

					page.add(iterator.next());
				}

				map.putAll(fetchByPrimaryKeys(page));
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			Serializable serializable = entityCache.getResult(
				WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
				WhereClauseEntryImpl.class, primaryKey);

			if (serializable != nullModel) {
				if (serializable == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<Serializable>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, (WhereClauseEntry)serializable);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler sb = new StringBundler(
			(uncachedPrimaryKeys.size() * 2) + 1);

		sb.append(_SQL_SELECT_WHERECLAUSEENTRY_WHERE_PKS_IN);

		for (Serializable primaryKey : uncachedPrimaryKeys) {
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

			for (WhereClauseEntry whereClauseEntry :
					(List<WhereClauseEntry>)query.list()) {

				map.put(whereClauseEntry.getPrimaryKeyObj(), whereClauseEntry);

				cacheResult(whereClauseEntry);

				uncachedPrimaryKeys.remove(whereClauseEntry.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(
					WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
					WhereClauseEntryImpl.class, primaryKey, nullModel);
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
	 * Returns all the where clause entries.
	 *
	 * @return the where clause entries
	 */
	@Override
	public List<WhereClauseEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the where clause entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of where clause entries
	 * @param end the upper bound of the range of where clause entries (not inclusive)
	 * @return the range of where clause entries
	 */
	@Override
	public List<WhereClauseEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the where clause entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of where clause entries
	 * @param end the upper bound of the range of where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of where clause entries
	 */
	@Override
	public List<WhereClauseEntry> findAll(
		int start, int end,
		OrderByComparator<WhereClauseEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the where clause entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of where clause entries
	 * @param end the upper bound of the range of where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of where clause entries
	 */
	@Override
	public List<WhereClauseEntry> findAll(
		int start, int end,
		OrderByComparator<WhereClauseEntry> orderByComparator,
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

		List<WhereClauseEntry> list = null;

		if (useFinderCache) {
			list = (List<WhereClauseEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_WHERECLAUSEENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_WHERECLAUSEENTRY;

				sql = sql.concat(WhereClauseEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<WhereClauseEntry>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(finderPath, finderArgs);
				}

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the where clause entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (WhereClauseEntry whereClauseEntry : findAll()) {
			remove(whereClauseEntry);
		}
	}

	/**
	 * Returns the number of where clause entries.
	 *
	 * @return the number of where clause entries
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
					"SELECT COUNT(whereClauseEntry) FROM WhereClauseEntry whereClauseEntry");

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				finderCache.removeResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return WhereClauseEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the where clause entry persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get("value.object.finder.cache.list.threshold"));

		_finderPathWithPaginationFindAll = new FinderPath(
			WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
			WhereClauseEntryModelImpl.FINDER_CACHE_ENABLED,
			WhereClauseEntryImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
			WhereClauseEntryModelImpl.FINDER_CACHE_ENABLED,
			WhereClauseEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0]);

		_finderPathCountAll = new FinderPath(
			WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
			WhereClauseEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		_finderPathWithPaginationFindByName_Nickname = new FinderPath(
			WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
			WhereClauseEntryModelImpl.FINDER_CACHE_ENABLED,
			WhereClauseEntryImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByName_Nickname",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			});

		_finderPathWithoutPaginationFindByName_Nickname = new FinderPath(
			WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
			WhereClauseEntryModelImpl.FINDER_CACHE_ENABLED,
			WhereClauseEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByName_Nickname",
			new String[] {String.class.getName()},
			WhereClauseEntryModelImpl.NAME_COLUMN_BITMASK);

		_finderPathCountByName_Nickname = new FinderPath(
			WhereClauseEntryModelImpl.ENTITY_CACHE_ENABLED,
			WhereClauseEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByName_Nickname",
			new String[] {String.class.getName()});

		WhereClauseEntryUtil.setPersistence(this);
	}

	public void destroy() {
		WhereClauseEntryUtil.setPersistence(null);

		entityCache.removeCache(WhereClauseEntryImpl.class.getName());

		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);

		DBType dbType = DBManagerUtil.getDBType(sessionFactory.getDialect());

		_databaseInMaxParameters = GetterUtil.getInteger(
			PropsUtil.get(
				"database.in.max.parameters", new Filter(dbType.getName())));
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		WhereClauseEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_WHERECLAUSEENTRY =
		"SELECT whereClauseEntry FROM WhereClauseEntry whereClauseEntry";

	private static final String _SQL_SELECT_WHERECLAUSEENTRY_WHERE_PKS_IN =
		"SELECT whereClauseEntry FROM WhereClauseEntry whereClauseEntry WHERE whereClauseEntryId IN (";

	private static final String _SQL_SELECT_WHERECLAUSEENTRY_WHERE =
		"SELECT whereClauseEntry FROM WhereClauseEntry whereClauseEntry WHERE ";

	private static final String _SQL_COUNT_WHERECLAUSEENTRY_WHERE =
		"SELECT COUNT(whereClauseEntry) FROM WhereClauseEntry whereClauseEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No WhereClauseEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No WhereClauseEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		WhereClauseEntryPersistenceImpl.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1630617768