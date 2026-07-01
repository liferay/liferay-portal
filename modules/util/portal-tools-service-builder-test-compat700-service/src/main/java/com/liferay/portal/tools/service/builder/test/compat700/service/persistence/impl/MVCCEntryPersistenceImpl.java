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
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat700.exception.NoSuchMVCCEntryException;
import com.liferay.portal.tools.service.builder.test.compat700.model.MVCCEntry;
import com.liferay.portal.tools.service.builder.test.compat700.model.impl.MVCCEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat700.model.impl.MVCCEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat700.service.persistence.MVCCEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat700.service.persistence.MVCCEntryUtil;

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
 * The persistence implementation for the mvcc entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class MVCCEntryPersistenceImpl
	extends BasePersistenceImpl<MVCCEntry> implements MVCCEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MVCCEntryUtil</code> to access the mvcc entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MVCCEntryImpl.class.getName();

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
	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the mvcc entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching mvcc entries
	 */
	@Override
	public List<MVCCEntry> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the mvcc entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MVCCEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of mvcc entries
	 * @param end the upper bound of the range of mvcc entries (not inclusive)
	 * @return the range of matching mvcc entries
	 */
	@Override
	public List<MVCCEntry> findByCompanyId(long companyId, int start, int end) {
		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the mvcc entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MVCCEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of mvcc entries
	 * @param end the upper bound of the range of mvcc entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching mvcc entries
	 */
	@Override
	public List<MVCCEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MVCCEntry> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the mvcc entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MVCCEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of mvcc entries
	 * @param end the upper bound of the range of mvcc entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching mvcc entries
	 */
	@Override
	public List<MVCCEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MVCCEntry> orderByComparator,
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

		List<MVCCEntry> list = null;

		if (useFinderCache) {
			list = (List<MVCCEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (MVCCEntry mvccEntry : list) {
					if (companyId != mvccEntry.getCompanyId()) {
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

			sb.append(_SQL_SELECT_MVCCENTRY_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(MVCCEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<MVCCEntry>)QueryUtil.list(
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
	 * Returns the first mvcc entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching mvcc entry
	 * @throws NoSuchMVCCEntryException if a matching mvcc entry could not be found
	 */
	@Override
	public MVCCEntry findByCompanyId_First(
			long companyId, OrderByComparator<MVCCEntry> orderByComparator)
		throws NoSuchMVCCEntryException {

		MVCCEntry mvccEntry = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (mvccEntry != null) {
			return mvccEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchMVCCEntryException(sb.toString());
	}

	/**
	 * Returns the first mvcc entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching mvcc entry, or <code>null</code> if a matching mvcc entry could not be found
	 */
	@Override
	public MVCCEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<MVCCEntry> orderByComparator) {

		List<MVCCEntry> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last mvcc entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching mvcc entry
	 * @throws NoSuchMVCCEntryException if a matching mvcc entry could not be found
	 */
	@Override
	public MVCCEntry findByCompanyId_Last(
			long companyId, OrderByComparator<MVCCEntry> orderByComparator)
		throws NoSuchMVCCEntryException {

		MVCCEntry mvccEntry = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (mvccEntry != null) {
			return mvccEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchMVCCEntryException(sb.toString());
	}

	/**
	 * Returns the last mvcc entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching mvcc entry, or <code>null</code> if a matching mvcc entry could not be found
	 */
	@Override
	public MVCCEntry fetchByCompanyId_Last(
		long companyId, OrderByComparator<MVCCEntry> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<MVCCEntry> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the mvcc entries before and after the current mvcc entry in the ordered set where companyId = &#63;.
	 *
	 * @param mvccEntryId the primary key of the current mvcc entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next mvcc entry
	 * @throws NoSuchMVCCEntryException if a mvcc entry with the primary key could not be found
	 */
	@Override
	public MVCCEntry[] findByCompanyId_PrevAndNext(
			long mvccEntryId, long companyId,
			OrderByComparator<MVCCEntry> orderByComparator)
		throws NoSuchMVCCEntryException {

		MVCCEntry mvccEntry = findByPrimaryKey(mvccEntryId);

		Session session = null;

		try {
			session = openSession();

			MVCCEntry[] array = new MVCCEntryImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, mvccEntry, companyId, orderByComparator, true);

			array[1] = mvccEntry;

			array[2] = getByCompanyId_PrevAndNext(
				session, mvccEntry, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected MVCCEntry getByCompanyId_PrevAndNext(
		Session session, MVCCEntry mvccEntry, long companyId,
		OrderByComparator<MVCCEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_MVCCENTRY_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

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
			sb.append(MVCCEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(mvccEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<MVCCEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the mvcc entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (MVCCEntry mvccEntry :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(mvccEntry);
		}
	}

	/**
	 * Returns the number of mvcc entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching mvcc entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_MVCCENTRY_WHERE);

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
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"mvccEntry.companyId = ?";

	private FinderPath _finderPathFetchByC_N;
	private FinderPath _finderPathCountByC_N;

	/**
	 * Returns the mvcc entry where companyId = &#63; and name = &#63; or throws a <code>NoSuchMVCCEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching mvcc entry
	 * @throws NoSuchMVCCEntryException if a matching mvcc entry could not be found
	 */
	@Override
	public MVCCEntry findByC_N(long companyId, String name)
		throws NoSuchMVCCEntryException {

		MVCCEntry mvccEntry = fetchByC_N(companyId, name);

		if (mvccEntry == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchMVCCEntryException(sb.toString());
		}

		return mvccEntry;
	}

	/**
	 * Returns the mvcc entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching mvcc entry, or <code>null</code> if a matching mvcc entry could not be found
	 */
	@Override
	public MVCCEntry fetchByC_N(long companyId, String name) {
		return fetchByC_N(companyId, name, true);
	}

	/**
	 * Returns the mvcc entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching mvcc entry, or <code>null</code> if a matching mvcc entry could not be found
	 */
	@Override
	public MVCCEntry fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		name = Objects.toString(name, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {companyId, name};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByC_N, finderArgs, this);
		}

		if (result instanceof MVCCEntry) {
			MVCCEntry mvccEntry = (MVCCEntry)result;

			if ((companyId != mvccEntry.getCompanyId()) ||
				!Objects.equals(name, mvccEntry.getName())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_MVCCENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_N_COMPANYID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_C_N_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindName) {
					queryPos.add(name);
				}

				List<MVCCEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByC_N, finderArgs, list);
					}
				}
				else {
					MVCCEntry mvccEntry = list.get(0);

					result = mvccEntry;

					cacheResult(mvccEntry);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(_finderPathFetchByC_N, finderArgs);
				}

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
			return (MVCCEntry)result;
		}
	}

	/**
	 * Removes the mvcc entry where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the mvcc entry that was removed
	 */
	@Override
	public MVCCEntry removeByC_N(long companyId, String name)
		throws NoSuchMVCCEntryException {

		MVCCEntry mvccEntry = findByC_N(companyId, name);

		return remove(mvccEntry);
	}

	/**
	 * Returns the number of mvcc entries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching mvcc entries
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		name = Objects.toString(name, "");

		FinderPath finderPath = _finderPathCountByC_N;

		Object[] finderArgs = new Object[] {companyId, name};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_MVCCENTRY_WHERE);

			sb.append(_FINDER_COLUMN_C_N_COMPANYID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_C_N_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_C_N_COMPANYID_2 =
		"mvccEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_NAME_2 =
		"mvccEntry.name = ?";

	private static final String _FINDER_COLUMN_C_N_NAME_3 =
		"(mvccEntry.name IS NULL OR mvccEntry.name = '')";

	public MVCCEntryPersistenceImpl() {
		setModelClass(MVCCEntry.class);
	}

	/**
	 * Caches the mvcc entry in the entity cache if it is enabled.
	 *
	 * @param mvccEntry the mvcc entry
	 */
	@Override
	public void cacheResult(MVCCEntry mvccEntry) {
		entityCache.putResult(
			MVCCEntryModelImpl.ENTITY_CACHE_ENABLED, MVCCEntryImpl.class,
			mvccEntry.getPrimaryKey(), mvccEntry);

		finderCache.putResult(
			_finderPathFetchByC_N,
			new Object[] {mvccEntry.getCompanyId(), mvccEntry.getName()},
			mvccEntry);

		mvccEntry.resetOriginalValues();
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the mvcc entries in the entity cache if it is enabled.
	 *
	 * @param mvccEntries the mvcc entries
	 */
	@Override
	public void cacheResult(List<MVCCEntry> mvccEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (mvccEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (MVCCEntry mvccEntry : mvccEntries) {
			if (entityCache.getResult(
					MVCCEntryModelImpl.ENTITY_CACHE_ENABLED,
					MVCCEntryImpl.class, mvccEntry.getPrimaryKey()) == null) {

				cacheResult(mvccEntry);
			}
			else {
				mvccEntry.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all mvcc entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(MVCCEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the mvcc entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(MVCCEntry mvccEntry) {
		entityCache.removeResult(
			MVCCEntryModelImpl.ENTITY_CACHE_ENABLED, MVCCEntryImpl.class,
			mvccEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache((MVCCEntryModelImpl)mvccEntry, true);
	}

	@Override
	public void clearCache(List<MVCCEntry> mvccEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (MVCCEntry mvccEntry : mvccEntries) {
			entityCache.removeResult(
				MVCCEntryModelImpl.ENTITY_CACHE_ENABLED, MVCCEntryImpl.class,
				mvccEntry.getPrimaryKey());

			clearUniqueFindersCache((MVCCEntryModelImpl)mvccEntry, true);
		}
	}

	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				MVCCEntryModelImpl.ENTITY_CACHE_ENABLED, MVCCEntryImpl.class,
				primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		MVCCEntryModelImpl mvccEntryModelImpl) {

		Object[] args = new Object[] {
			mvccEntryModelImpl.getCompanyId(), mvccEntryModelImpl.getName()
		};

		finderCache.putResult(
			_finderPathCountByC_N, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByC_N, args, mvccEntryModelImpl, false);
	}

	protected void clearUniqueFindersCache(
		MVCCEntryModelImpl mvccEntryModelImpl, boolean clearCurrent) {

		if (clearCurrent) {
			Object[] args = new Object[] {
				mvccEntryModelImpl.getCompanyId(), mvccEntryModelImpl.getName()
			};

			finderCache.removeResult(_finderPathCountByC_N, args);
			finderCache.removeResult(_finderPathFetchByC_N, args);
		}

		if ((mvccEntryModelImpl.getColumnBitmask() &
			 _finderPathFetchByC_N.getColumnBitmask()) != 0) {

			Object[] args = new Object[] {
				mvccEntryModelImpl.getOriginalCompanyId(),
				mvccEntryModelImpl.getOriginalName()
			};

			finderCache.removeResult(_finderPathCountByC_N, args);
			finderCache.removeResult(_finderPathFetchByC_N, args);
		}
	}

	/**
	 * Creates a new mvcc entry with the primary key. Does not add the mvcc entry to the database.
	 *
	 * @param mvccEntryId the primary key for the new mvcc entry
	 * @return the new mvcc entry
	 */
	@Override
	public MVCCEntry create(long mvccEntryId) {
		MVCCEntry mvccEntry = new MVCCEntryImpl();

		mvccEntry.setNew(true);
		mvccEntry.setPrimaryKey(mvccEntryId);

		mvccEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mvccEntry;
	}

	/**
	 * Removes the mvcc entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param mvccEntryId the primary key of the mvcc entry
	 * @return the mvcc entry that was removed
	 * @throws NoSuchMVCCEntryException if a mvcc entry with the primary key could not be found
	 */
	@Override
	public MVCCEntry remove(long mvccEntryId) throws NoSuchMVCCEntryException {
		return remove((Serializable)mvccEntryId);
	}

	/**
	 * Removes the mvcc entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the mvcc entry
	 * @return the mvcc entry that was removed
	 * @throws NoSuchMVCCEntryException if a mvcc entry with the primary key could not be found
	 */
	@Override
	public MVCCEntry remove(Serializable primaryKey)
		throws NoSuchMVCCEntryException {

		Session session = null;

		try {
			session = openSession();

			MVCCEntry mvccEntry = (MVCCEntry)session.get(
				MVCCEntryImpl.class, primaryKey);

			if (mvccEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchMVCCEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(mvccEntry);
		}
		catch (NoSuchMVCCEntryException noSuchEntityException) {
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
	protected MVCCEntry removeImpl(MVCCEntry mvccEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mvccEntry)) {
				mvccEntry = (MVCCEntry)session.get(
					MVCCEntryImpl.class, mvccEntry.getPrimaryKeyObj());
			}

			if (mvccEntry != null) {
				session.delete(mvccEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mvccEntry != null) {
			clearCache(mvccEntry);
		}

		return mvccEntry;
	}

	@Override
	public MVCCEntry updateImpl(MVCCEntry mvccEntry) {
		boolean isNew = mvccEntry.isNew();

		if (!(mvccEntry instanceof MVCCEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(mvccEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(mvccEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in mvccEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MVCCEntry implementation " +
					mvccEntry.getClass());
		}

		MVCCEntryModelImpl mvccEntryModelImpl = (MVCCEntryModelImpl)mvccEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(mvccEntry);

				mvccEntry.setNew(false);
			}
			else {
				mvccEntry = (MVCCEntry)session.merge(mvccEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!MVCCEntryModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else if (isNew) {
			Object[] args = new Object[] {mvccEntryModelImpl.getCompanyId()};

			finderCache.removeResult(_finderPathCountByCompanyId, args);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindByCompanyId, args);

			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
		}
		else {
			if ((mvccEntryModelImpl.getColumnBitmask() &
				 _finderPathWithoutPaginationFindByCompanyId.
					 getColumnBitmask()) != 0) {

				Object[] args = new Object[] {
					mvccEntryModelImpl.getOriginalCompanyId()
				};

				finderCache.removeResult(_finderPathCountByCompanyId, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByCompanyId, args);

				args = new Object[] {mvccEntryModelImpl.getCompanyId()};

				finderCache.removeResult(_finderPathCountByCompanyId, args);
				finderCache.removeResult(
					_finderPathWithoutPaginationFindByCompanyId, args);
			}
		}

		entityCache.putResult(
			MVCCEntryModelImpl.ENTITY_CACHE_ENABLED, MVCCEntryImpl.class,
			mvccEntry.getPrimaryKey(), mvccEntry, false);

		clearUniqueFindersCache(mvccEntryModelImpl, false);
		cacheUniqueFindersCache(mvccEntryModelImpl);

		mvccEntry.resetOriginalValues();

		return mvccEntry;
	}

	/**
	 * Returns the mvcc entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the mvcc entry
	 * @return the mvcc entry
	 * @throws NoSuchMVCCEntryException if a mvcc entry with the primary key could not be found
	 */
	@Override
	public MVCCEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchMVCCEntryException {

		MVCCEntry mvccEntry = fetchByPrimaryKey(primaryKey);

		if (mvccEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchMVCCEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return mvccEntry;
	}

	/**
	 * Returns the mvcc entry with the primary key or throws a <code>NoSuchMVCCEntryException</code> if it could not be found.
	 *
	 * @param mvccEntryId the primary key of the mvcc entry
	 * @return the mvcc entry
	 * @throws NoSuchMVCCEntryException if a mvcc entry with the primary key could not be found
	 */
	@Override
	public MVCCEntry findByPrimaryKey(long mvccEntryId)
		throws NoSuchMVCCEntryException {

		return findByPrimaryKey((Serializable)mvccEntryId);
	}

	/**
	 * Returns the mvcc entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the mvcc entry
	 * @return the mvcc entry, or <code>null</code> if a mvcc entry with the primary key could not be found
	 */
	@Override
	public MVCCEntry fetchByPrimaryKey(Serializable primaryKey) {
		Serializable serializable = entityCache.getResult(
			MVCCEntryModelImpl.ENTITY_CACHE_ENABLED, MVCCEntryImpl.class,
			primaryKey);

		if (serializable == nullModel) {
			return null;
		}

		MVCCEntry mvccEntry = (MVCCEntry)serializable;

		if (mvccEntry == null) {
			Session session = null;

			try {
				session = openSession();

				mvccEntry = (MVCCEntry)session.get(
					MVCCEntryImpl.class, primaryKey);

				if (mvccEntry != null) {
					cacheResult(mvccEntry);
				}
				else {
					entityCache.putResult(
						MVCCEntryModelImpl.ENTITY_CACHE_ENABLED,
						MVCCEntryImpl.class, primaryKey, nullModel);
				}
			}
			catch (Exception exception) {
				entityCache.removeResult(
					MVCCEntryModelImpl.ENTITY_CACHE_ENABLED,
					MVCCEntryImpl.class, primaryKey);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return mvccEntry;
	}

	/**
	 * Returns the mvcc entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param mvccEntryId the primary key of the mvcc entry
	 * @return the mvcc entry, or <code>null</code> if a mvcc entry with the primary key could not be found
	 */
	@Override
	public MVCCEntry fetchByPrimaryKey(long mvccEntryId) {
		return fetchByPrimaryKey((Serializable)mvccEntryId);
	}

	@Override
	public Map<Serializable, MVCCEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, MVCCEntry> map =
			new HashMap<Serializable, MVCCEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			MVCCEntry mvccEntry = fetchByPrimaryKey(primaryKey);

			if (mvccEntry != null) {
				map.put(primaryKey, mvccEntry);
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
				MVCCEntryModelImpl.ENTITY_CACHE_ENABLED, MVCCEntryImpl.class,
				primaryKey);

			if (serializable != nullModel) {
				if (serializable == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<Serializable>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, (MVCCEntry)serializable);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler sb = new StringBundler(
			(uncachedPrimaryKeys.size() * 2) + 1);

		sb.append(_SQL_SELECT_MVCCENTRY_WHERE_PKS_IN);

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

			for (MVCCEntry mvccEntry : (List<MVCCEntry>)query.list()) {
				map.put(mvccEntry.getPrimaryKeyObj(), mvccEntry);

				cacheResult(mvccEntry);

				uncachedPrimaryKeys.remove(mvccEntry.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(
					MVCCEntryModelImpl.ENTITY_CACHE_ENABLED,
					MVCCEntryImpl.class, primaryKey, nullModel);
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
	 * Returns all the mvcc entries.
	 *
	 * @return the mvcc entries
	 */
	@Override
	public List<MVCCEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the mvcc entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MVCCEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of mvcc entries
	 * @param end the upper bound of the range of mvcc entries (not inclusive)
	 * @return the range of mvcc entries
	 */
	@Override
	public List<MVCCEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the mvcc entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MVCCEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of mvcc entries
	 * @param end the upper bound of the range of mvcc entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of mvcc entries
	 */
	@Override
	public List<MVCCEntry> findAll(
		int start, int end, OrderByComparator<MVCCEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the mvcc entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MVCCEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of mvcc entries
	 * @param end the upper bound of the range of mvcc entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of mvcc entries
	 */
	@Override
	public List<MVCCEntry> findAll(
		int start, int end, OrderByComparator<MVCCEntry> orderByComparator,
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

		List<MVCCEntry> list = null;

		if (useFinderCache) {
			list = (List<MVCCEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_MVCCENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_MVCCENTRY;

				sql = sql.concat(MVCCEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<MVCCEntry>)QueryUtil.list(
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
	 * Removes all the mvcc entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (MVCCEntry mvccEntry : findAll()) {
			remove(mvccEntry);
		}
	}

	/**
	 * Returns the number of mvcc entries.
	 *
	 * @return the number of mvcc entries
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
					"SELECT COUNT(mvccEntry) FROM MVCCEntry mvccEntry");

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
		return MVCCEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the mvcc entry persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get("value.object.finder.cache.list.threshold"));

		_finderPathWithPaginationFindAll = new FinderPath(
			MVCCEntryModelImpl.ENTITY_CACHE_ENABLED,
			MVCCEntryModelImpl.FINDER_CACHE_ENABLED, MVCCEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			MVCCEntryModelImpl.ENTITY_CACHE_ENABLED,
			MVCCEntryModelImpl.FINDER_CACHE_ENABLED, MVCCEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0]);

		_finderPathCountAll = new FinderPath(
			MVCCEntryModelImpl.ENTITY_CACHE_ENABLED,
			MVCCEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		_finderPathWithPaginationFindByCompanyId = new FinderPath(
			MVCCEntryModelImpl.ENTITY_CACHE_ENABLED,
			MVCCEntryModelImpl.FINDER_CACHE_ENABLED, MVCCEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			});

		_finderPathWithoutPaginationFindByCompanyId = new FinderPath(
			MVCCEntryModelImpl.ENTITY_CACHE_ENABLED,
			MVCCEntryModelImpl.FINDER_CACHE_ENABLED, MVCCEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyId",
			new String[] {Long.class.getName()},
			MVCCEntryModelImpl.COMPANYID_COLUMN_BITMASK);

		_finderPathCountByCompanyId = new FinderPath(
			MVCCEntryModelImpl.ENTITY_CACHE_ENABLED,
			MVCCEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyId",
			new String[] {Long.class.getName()});

		_finderPathFetchByC_N = new FinderPath(
			MVCCEntryModelImpl.ENTITY_CACHE_ENABLED,
			MVCCEntryModelImpl.FINDER_CACHE_ENABLED, MVCCEntryImpl.class,
			FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
			new String[] {Long.class.getName(), String.class.getName()},
			MVCCEntryModelImpl.COMPANYID_COLUMN_BITMASK |
			MVCCEntryModelImpl.NAME_COLUMN_BITMASK);

		_finderPathCountByC_N = new FinderPath(
			MVCCEntryModelImpl.ENTITY_CACHE_ENABLED,
			MVCCEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N",
			new String[] {Long.class.getName(), String.class.getName()});

		MVCCEntryUtil.setPersistence(this);
	}

	public void destroy() {
		MVCCEntryUtil.setPersistence(null);

		entityCache.removeCache(MVCCEntryImpl.class.getName());

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
		MVCCEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MVCCENTRY =
		"SELECT mvccEntry FROM MVCCEntry mvccEntry";

	private static final String _SQL_SELECT_MVCCENTRY_WHERE_PKS_IN =
		"SELECT mvccEntry FROM MVCCEntry mvccEntry WHERE mvccEntryId IN (";

	private static final String _SQL_SELECT_MVCCENTRY_WHERE =
		"SELECT mvccEntry FROM MVCCEntry mvccEntry WHERE ";

	private static final String _SQL_COUNT_MVCCENTRY_WHERE =
		"SELECT COUNT(mvccEntry) FROM MVCCEntry mvccEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No MVCCEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No MVCCEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		MVCCEntryPersistenceImpl.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1304109440