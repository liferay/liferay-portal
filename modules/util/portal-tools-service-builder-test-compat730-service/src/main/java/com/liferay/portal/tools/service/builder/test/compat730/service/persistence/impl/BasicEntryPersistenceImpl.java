/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat730.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat730.exception.NoSuchBasicEntryException;
import com.liferay.portal.tools.service.builder.test.compat730.model.BasicEntry;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.BasicEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.BasicEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.BasicEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.BasicEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.MappingEntryPersistence;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * The persistence implementation for the basic entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class BasicEntryPersistenceImpl
	extends BasePersistenceImpl<BasicEntry> implements BasicEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BasicEntryUtil</code> to access the basic entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BasicEntryImpl.class.getName();

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
	 * Returns all the basic entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching basic entries
	 */
	@Override
	public List<BasicEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the basic entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BasicEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of basic entries
	 * @param end the upper bound of the range of basic entries (not inclusive)
	 * @return the range of matching basic entries
	 */
	@Override
	public List<BasicEntry> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the basic entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BasicEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of basic entries
	 * @param end the upper bound of the range of basic entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching basic entries
	 */
	@Override
	public List<BasicEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BasicEntry> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the basic entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BasicEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of basic entries
	 * @param end the upper bound of the range of basic entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching basic entries
	 */
	@Override
	public List<BasicEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BasicEntry> orderByComparator,
		boolean useFinderCache) {

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
			finderArgs = new Object[] {groupId, start, end, orderByComparator};
		}

		List<BasicEntry> list = null;

		if (useFinderCache) {
			list = (List<BasicEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (BasicEntry basicEntry : list) {
					if (groupId != basicEntry.getGroupId()) {
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

			sb.append(_SQL_SELECT_BASICENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(BasicEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<BasicEntry>)QueryUtil.list(
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
	 * Returns the first basic entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching basic entry
	 * @throws NoSuchBasicEntryException if a matching basic entry could not be found
	 */
	@Override
	public BasicEntry findByGroupId_First(
			long groupId, OrderByComparator<BasicEntry> orderByComparator)
		throws NoSuchBasicEntryException {

		BasicEntry basicEntry = fetchByGroupId_First(
			groupId, orderByComparator);

		if (basicEntry != null) {
			return basicEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchBasicEntryException(sb.toString());
	}

	/**
	 * Returns the first basic entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching basic entry, or <code>null</code> if a matching basic entry could not be found
	 */
	@Override
	public BasicEntry fetchByGroupId_First(
		long groupId, OrderByComparator<BasicEntry> orderByComparator) {

		List<BasicEntry> list = findByGroupId(groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last basic entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching basic entry
	 * @throws NoSuchBasicEntryException if a matching basic entry could not be found
	 */
	@Override
	public BasicEntry findByGroupId_Last(
			long groupId, OrderByComparator<BasicEntry> orderByComparator)
		throws NoSuchBasicEntryException {

		BasicEntry basicEntry = fetchByGroupId_Last(groupId, orderByComparator);

		if (basicEntry != null) {
			return basicEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchBasicEntryException(sb.toString());
	}

	/**
	 * Returns the last basic entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching basic entry, or <code>null</code> if a matching basic entry could not be found
	 */
	@Override
	public BasicEntry fetchByGroupId_Last(
		long groupId, OrderByComparator<BasicEntry> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<BasicEntry> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the basic entries before and after the current basic entry in the ordered set where groupId = &#63;.
	 *
	 * @param basicEntryId the primary key of the current basic entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next basic entry
	 * @throws NoSuchBasicEntryException if a basic entry with the primary key could not be found
	 */
	@Override
	public BasicEntry[] findByGroupId_PrevAndNext(
			long basicEntryId, long groupId,
			OrderByComparator<BasicEntry> orderByComparator)
		throws NoSuchBasicEntryException {

		BasicEntry basicEntry = findByPrimaryKey(basicEntryId);

		Session session = null;

		try {
			session = openSession();

			BasicEntry[] array = new BasicEntryImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, basicEntry, groupId, orderByComparator, true);

			array[1] = basicEntry;

			array[2] = getByGroupId_PrevAndNext(
				session, basicEntry, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BasicEntry getByGroupId_PrevAndNext(
		Session session, BasicEntry basicEntry, long groupId,
		OrderByComparator<BasicEntry> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_BASICENTRY_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

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
			sb.append(BasicEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(basicEntry)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BasicEntry> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the basic entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (BasicEntry basicEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(basicEntry);
		}
	}

	/**
	 * Returns the number of basic entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching basic entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_BASICENTRY_WHERE);

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

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"basicEntry.groupId = ?";

	private FinderPath _finderPathFetchByC_N;
	private FinderPath _finderPathCountByC_N;

	/**
	 * Returns the basic entry where companyId = &#63; and name = &#63; or throws a <code>NoSuchBasicEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching basic entry
	 * @throws NoSuchBasicEntryException if a matching basic entry could not be found
	 */
	@Override
	public BasicEntry findByC_N(long companyId, String name)
		throws NoSuchBasicEntryException {

		BasicEntry basicEntry = fetchByC_N(companyId, name);

		if (basicEntry == null) {
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

			throw new NoSuchBasicEntryException(sb.toString());
		}

		return basicEntry;
	}

	/**
	 * Returns the basic entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching basic entry, or <code>null</code> if a matching basic entry could not be found
	 */
	@Override
	public BasicEntry fetchByC_N(long companyId, String name) {
		return fetchByC_N(companyId, name, true);
	}

	/**
	 * Returns the basic entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching basic entry, or <code>null</code> if a matching basic entry could not be found
	 */
	@Override
	public BasicEntry fetchByC_N(
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

		if (result instanceof BasicEntry) {
			BasicEntry basicEntry = (BasicEntry)result;

			if ((companyId != basicEntry.getCompanyId()) ||
				!Objects.equals(name, basicEntry.getName())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_BASICENTRY_WHERE);

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

				List<BasicEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByC_N, finderArgs, list);
					}
				}
				else {
					BasicEntry basicEntry = list.get(0);

					result = basicEntry;

					cacheResult(basicEntry);
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
			return (BasicEntry)result;
		}
	}

	/**
	 * Removes the basic entry where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the basic entry that was removed
	 */
	@Override
	public BasicEntry removeByC_N(long companyId, String name)
		throws NoSuchBasicEntryException {

		BasicEntry basicEntry = findByC_N(companyId, name);

		return remove(basicEntry);
	}

	/**
	 * Returns the number of basic entries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching basic entries
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		name = Objects.toString(name, "");

		FinderPath finderPath = _finderPathCountByC_N;

		Object[] finderArgs = new Object[] {companyId, name};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_BASICENTRY_WHERE);

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
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_C_N_COMPANYID_2 =
		"basicEntry.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_NAME_2 =
		"basicEntry.name = ?";

	private static final String _FINDER_COLUMN_C_N_NAME_3 =
		"(basicEntry.name IS NULL OR basicEntry.name = '')";

	public BasicEntryPersistenceImpl() {
		setModelClass(BasicEntry.class);

		setModelImplClass(BasicEntryImpl.class);
		setModelPKClass(long.class);
	}

	/**
	 * Caches the basic entry in the entity cache if it is enabled.
	 *
	 * @param basicEntry the basic entry
	 */
	@Override
	public void cacheResult(BasicEntry basicEntry) {
		entityCache.putResult(
			BasicEntryImpl.class, basicEntry.getPrimaryKey(), basicEntry);

		finderCache.putResult(
			_finderPathFetchByC_N,
			new Object[] {basicEntry.getCompanyId(), basicEntry.getName()},
			basicEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the basic entries in the entity cache if it is enabled.
	 *
	 * @param basicEntries the basic entries
	 */
	@Override
	public void cacheResult(List<BasicEntry> basicEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (basicEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (BasicEntry basicEntry : basicEntries) {
			if (entityCache.getResult(
					BasicEntryImpl.class, basicEntry.getPrimaryKey()) == null) {

				cacheResult(basicEntry);
			}
		}
	}

	/**
	 * Clears the cache for all basic entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(BasicEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the basic entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(BasicEntry basicEntry) {
		entityCache.removeResult(BasicEntryImpl.class, basicEntry);
	}

	@Override
	public void clearCache(List<BasicEntry> basicEntries) {
		for (BasicEntry basicEntry : basicEntries) {
			entityCache.removeResult(BasicEntryImpl.class, basicEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(BasicEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		BasicEntryModelImpl basicEntryModelImpl) {

		Object[] args = new Object[] {
			basicEntryModelImpl.getCompanyId(), basicEntryModelImpl.getName()
		};

		finderCache.putResult(
			_finderPathCountByC_N, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByC_N, args, basicEntryModelImpl, false);
	}

	/**
	 * Creates a new basic entry with the primary key. Does not add the basic entry to the database.
	 *
	 * @param basicEntryId the primary key for the new basic entry
	 * @return the new basic entry
	 */
	@Override
	public BasicEntry create(long basicEntryId) {
		BasicEntry basicEntry = new BasicEntryImpl();

		basicEntry.setNew(true);
		basicEntry.setPrimaryKey(basicEntryId);

		basicEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return basicEntry;
	}

	/**
	 * Removes the basic entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param basicEntryId the primary key of the basic entry
	 * @return the basic entry that was removed
	 * @throws NoSuchBasicEntryException if a basic entry with the primary key could not be found
	 */
	@Override
	public BasicEntry remove(long basicEntryId)
		throws NoSuchBasicEntryException {

		return remove((Serializable)basicEntryId);
	}

	/**
	 * Removes the basic entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the basic entry
	 * @return the basic entry that was removed
	 * @throws NoSuchBasicEntryException if a basic entry with the primary key could not be found
	 */
	@Override
	public BasicEntry remove(Serializable primaryKey)
		throws NoSuchBasicEntryException {

		Session session = null;

		try {
			session = openSession();

			BasicEntry basicEntry = (BasicEntry)session.get(
				BasicEntryImpl.class, primaryKey);

			if (basicEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchBasicEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(basicEntry);
		}
		catch (NoSuchBasicEntryException noSuchEntityException) {
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
	protected BasicEntry removeImpl(BasicEntry basicEntry) {
		basicEntryToMappingEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			basicEntry.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(basicEntry)) {
				basicEntry = (BasicEntry)session.get(
					BasicEntryImpl.class, basicEntry.getPrimaryKeyObj());
			}

			if (basicEntry != null) {
				session.delete(basicEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (basicEntry != null) {
			clearCache(basicEntry);
		}

		return basicEntry;
	}

	@Override
	public BasicEntry updateImpl(BasicEntry basicEntry) {
		boolean isNew = basicEntry.isNew();

		if (!(basicEntry instanceof BasicEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(basicEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(basicEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in basicEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BasicEntry implementation " +
					basicEntry.getClass());
		}

		BasicEntryModelImpl basicEntryModelImpl =
			(BasicEntryModelImpl)basicEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (basicEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				basicEntry.setCreateDate(date);
			}
			else {
				basicEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!basicEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				basicEntry.setModifiedDate(date);
			}
			else {
				basicEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(basicEntry);
			}
			else {
				basicEntry = (BasicEntry)session.merge(basicEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			BasicEntryImpl.class, basicEntryModelImpl, false, true);

		cacheUniqueFindersCache(basicEntryModelImpl);

		if (isNew) {
			basicEntry.setNew(false);
		}

		basicEntry.resetOriginalValues();

		return basicEntry;
	}

	/**
	 * Returns the basic entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the basic entry
	 * @return the basic entry
	 * @throws NoSuchBasicEntryException if a basic entry with the primary key could not be found
	 */
	@Override
	public BasicEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchBasicEntryException {

		BasicEntry basicEntry = fetchByPrimaryKey(primaryKey);

		if (basicEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchBasicEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return basicEntry;
	}

	/**
	 * Returns the basic entry with the primary key or throws a <code>NoSuchBasicEntryException</code> if it could not be found.
	 *
	 * @param basicEntryId the primary key of the basic entry
	 * @return the basic entry
	 * @throws NoSuchBasicEntryException if a basic entry with the primary key could not be found
	 */
	@Override
	public BasicEntry findByPrimaryKey(long basicEntryId)
		throws NoSuchBasicEntryException {

		return findByPrimaryKey((Serializable)basicEntryId);
	}

	/**
	 * Returns the basic entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param basicEntryId the primary key of the basic entry
	 * @return the basic entry, or <code>null</code> if a basic entry with the primary key could not be found
	 */
	@Override
	public BasicEntry fetchByPrimaryKey(long basicEntryId) {
		return fetchByPrimaryKey((Serializable)basicEntryId);
	}

	/**
	 * Returns all the basic entries.
	 *
	 * @return the basic entries
	 */
	@Override
	public List<BasicEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the basic entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BasicEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of basic entries
	 * @param end the upper bound of the range of basic entries (not inclusive)
	 * @return the range of basic entries
	 */
	@Override
	public List<BasicEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the basic entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BasicEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of basic entries
	 * @param end the upper bound of the range of basic entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of basic entries
	 */
	@Override
	public List<BasicEntry> findAll(
		int start, int end, OrderByComparator<BasicEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the basic entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BasicEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of basic entries
	 * @param end the upper bound of the range of basic entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of basic entries
	 */
	@Override
	public List<BasicEntry> findAll(
		int start, int end, OrderByComparator<BasicEntry> orderByComparator,
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

		List<BasicEntry> list = null;

		if (useFinderCache) {
			list = (List<BasicEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_BASICENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_BASICENTRY;

				sql = sql.concat(BasicEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<BasicEntry>)QueryUtil.list(
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
	 * Removes all the basic entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (BasicEntry basicEntry : findAll()) {
			remove(basicEntry);
		}
	}

	/**
	 * Returns the number of basic entries.
	 *
	 * @return the number of basic entries
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
					"SELECT COUNT(basicEntry) FROM BasicEntry basicEntry");

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

	/**
	 * Returns the primaryKeys of mapping entries associated with the basic entry.
	 *
	 * @param pk the primary key of the basic entry
	 * @return long[] of the primaryKeys of mapping entries associated with the basic entry
	 */
	@Override
	public long[] getMappingEntryPrimaryKeys(long pk) {
		long[] pks = basicEntryToMappingEntryTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the mapping entries associated with the basic entry.
	 *
	 * @param pk the primary key of the basic entry
	 * @return the mapping entries associated with the basic entry
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.compat730.model.
			MappingEntry> getMappingEntries(long pk) {

		return getMappingEntries(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the mapping entries associated with the basic entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BasicEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the basic entry
	 * @param start the lower bound of the range of basic entries
	 * @param end the upper bound of the range of basic entries (not inclusive)
	 * @return the range of mapping entries associated with the basic entry
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.compat730.model.
			MappingEntry> getMappingEntries(long pk, int start, int end) {

		return getMappingEntries(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the mapping entries associated with the basic entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BasicEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the basic entry
	 * @param start the lower bound of the range of basic entries
	 * @param end the upper bound of the range of basic entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of mapping entries associated with the basic entry
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.compat730.model.
			MappingEntry> getMappingEntries(
				long pk, int start, int end,
				OrderByComparator
					<com.liferay.portal.tools.service.builder.test.compat730.
						model.MappingEntry> orderByComparator) {

		return basicEntryToMappingEntryTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of mapping entries associated with the basic entry.
	 *
	 * @param pk the primary key of the basic entry
	 * @return the number of mapping entries associated with the basic entry
	 */
	@Override
	public int getMappingEntriesSize(long pk) {
		long[] pks = basicEntryToMappingEntryTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the mapping entry is associated with the basic entry.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntryPK the primary key of the mapping entry
	 * @return <code>true</code> if the mapping entry is associated with the basic entry; <code>false</code> otherwise
	 */
	@Override
	public boolean containsMappingEntry(long pk, long mappingEntryPK) {
		return basicEntryToMappingEntryTableMapper.containsTableMapping(
			pk, mappingEntryPK);
	}

	/**
	 * Returns <code>true</code> if the basic entry has any mapping entries associated with it.
	 *
	 * @param pk the primary key of the basic entry to check for associations with mapping entries
	 * @return <code>true</code> if the basic entry has any mapping entries associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsMappingEntries(long pk) {
		if (getMappingEntriesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the basic entry and the mapping entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntryPK the primary key of the mapping entry
	 */
	@Override
	public void addMappingEntry(long pk, long mappingEntryPK) {
		BasicEntry basicEntry = fetchByPrimaryKey(pk);

		if (basicEntry == null) {
			basicEntryToMappingEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, mappingEntryPK);
		}
		else {
			basicEntryToMappingEntryTableMapper.addTableMapping(
				basicEntry.getCompanyId(), pk, mappingEntryPK);
		}
	}

	/**
	 * Adds an association between the basic entry and the mapping entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntry the mapping entry
	 */
	@Override
	public void addMappingEntry(
		long pk,
		com.liferay.portal.tools.service.builder.test.compat730.model.
			MappingEntry mappingEntry) {

		BasicEntry basicEntry = fetchByPrimaryKey(pk);

		if (basicEntry == null) {
			basicEntryToMappingEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				mappingEntry.getPrimaryKey());
		}
		else {
			basicEntryToMappingEntryTableMapper.addTableMapping(
				basicEntry.getCompanyId(), pk, mappingEntry.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the basic entry and the mapping entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntryPKs the primary keys of the mapping entries
	 */
	@Override
	public void addMappingEntries(long pk, long[] mappingEntryPKs) {
		long companyId = 0;

		BasicEntry basicEntry = fetchByPrimaryKey(pk);

		if (basicEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = basicEntry.getCompanyId();
		}

		basicEntryToMappingEntryTableMapper.addTableMappings(
			companyId, pk, mappingEntryPKs);
	}

	/**
	 * Adds an association between the basic entry and the mapping entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntries the mapping entries
	 */
	@Override
	public void addMappingEntries(
		long pk,
		List
			<com.liferay.portal.tools.service.builder.test.compat730.model.
				MappingEntry> mappingEntries) {

		addMappingEntries(
			pk,
			ListUtil.toLongArray(
				mappingEntries,
				com.liferay.portal.tools.service.builder.test.compat730.model.
					MappingEntry.MAPPING_ENTRY_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the basic entry and its mapping entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry to clear the associated mapping entries from
	 */
	@Override
	public void clearMappingEntries(long pk) {
		basicEntryToMappingEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the basic entry and the mapping entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntryPK the primary key of the mapping entry
	 */
	@Override
	public void removeMappingEntry(long pk, long mappingEntryPK) {
		basicEntryToMappingEntryTableMapper.deleteTableMapping(
			pk, mappingEntryPK);
	}

	/**
	 * Removes the association between the basic entry and the mapping entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntry the mapping entry
	 */
	@Override
	public void removeMappingEntry(
		long pk,
		com.liferay.portal.tools.service.builder.test.compat730.model.
			MappingEntry mappingEntry) {

		basicEntryToMappingEntryTableMapper.deleteTableMapping(
			pk, mappingEntry.getPrimaryKey());
	}

	/**
	 * Removes the association between the basic entry and the mapping entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntryPKs the primary keys of the mapping entries
	 */
	@Override
	public void removeMappingEntries(long pk, long[] mappingEntryPKs) {
		basicEntryToMappingEntryTableMapper.deleteTableMappings(
			pk, mappingEntryPKs);
	}

	/**
	 * Removes the association between the basic entry and the mapping entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntries the mapping entries
	 */
	@Override
	public void removeMappingEntries(
		long pk,
		List
			<com.liferay.portal.tools.service.builder.test.compat730.model.
				MappingEntry> mappingEntries) {

		removeMappingEntries(
			pk,
			ListUtil.toLongArray(
				mappingEntries,
				com.liferay.portal.tools.service.builder.test.compat730.model.
					MappingEntry.MAPPING_ENTRY_ID_ACCESSOR));
	}

	/**
	 * Sets the mapping entries associated with the basic entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntryPKs the primary keys of the mapping entries to be associated with the basic entry
	 */
	@Override
	public void setMappingEntries(long pk, long[] mappingEntryPKs) {
		Set<Long> newMappingEntryPKsSet = SetUtil.fromArray(mappingEntryPKs);
		Set<Long> oldMappingEntryPKsSet = SetUtil.fromArray(
			basicEntryToMappingEntryTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeMappingEntryPKsSet = new HashSet<Long>(
			oldMappingEntryPKsSet);

		removeMappingEntryPKsSet.removeAll(newMappingEntryPKsSet);

		basicEntryToMappingEntryTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeMappingEntryPKsSet));

		newMappingEntryPKsSet.removeAll(oldMappingEntryPKsSet);

		long companyId = 0;

		BasicEntry basicEntry = fetchByPrimaryKey(pk);

		if (basicEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = basicEntry.getCompanyId();
		}

		basicEntryToMappingEntryTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newMappingEntryPKsSet));
	}

	/**
	 * Sets the mapping entries associated with the basic entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the basic entry
	 * @param mappingEntries the mapping entries to be associated with the basic entry
	 */
	@Override
	public void setMappingEntries(
		long pk,
		List
			<com.liferay.portal.tools.service.builder.test.compat730.model.
				MappingEntry> mappingEntries) {

		try {
			long[] mappingEntryPKs = new long[mappingEntries.size()];

			for (int i = 0; i < mappingEntries.size(); i++) {
				com.liferay.portal.tools.service.builder.test.compat730.model.
					MappingEntry mappingEntry = mappingEntries.get(i);

				mappingEntryPKs[i] = mappingEntry.getPrimaryKey();
			}

			setMappingEntries(pk, mappingEntryPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "basicEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BASICENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BasicEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the basic entry persistence.
	 */
	public void afterPropertiesSet() {
		Bundle bundle = FrameworkUtil.getBundle(
			BasicEntryPersistenceImpl.class);

		_bundleContext = bundle.getBundleContext();

		_argumentsResolverServiceRegistration = _bundleContext.registerService(
			ArgumentsResolver.class, new BasicEntryModelArgumentsResolver(),
			MapUtil.singletonDictionary(
				"model.class.name", BasicEntry.class.getName()));

		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		basicEntryToMappingEntryTableMapper = TableMapperFactory.getTableMapper(
			"MappingEntries_BasicEntries", "companyId", "basicEntryId",
			"mappingEntryId", this, mappingEntryPersistence);

		_finderPathWithPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByGroupId = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId"}, true);

		_finderPathWithoutPaginationFindByGroupId = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			true);

		_finderPathCountByGroupId = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		_finderPathFetchByC_N = _createFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "name"}, true);

		_finderPathCountByC_N = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "name"}, false);

		BasicEntryUtil.setPersistence(this);
	}

	public void destroy() {
		BasicEntryUtil.setPersistence(null);

		entityCache.removeCache(BasicEntryImpl.class.getName());

		_argumentsResolverServiceRegistration.unregister();

		for (ServiceRegistration<FinderPath> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		TableMapperFactory.removeTableMapper("MappingEntries_BasicEntries");
	}

	private BundleContext _bundleContext;

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	@BeanReference(type = MappingEntryPersistence.class)
	protected MappingEntryPersistence mappingEntryPersistence;

	protected TableMapper
		<BasicEntry,
		 com.liferay.portal.tools.service.builder.test.compat730.model.
			 MappingEntry> basicEntryToMappingEntryTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		BasicEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_BASICENTRY =
		"SELECT basicEntry FROM BasicEntry basicEntry";

	private static final String _SQL_SELECT_BASICENTRY_WHERE =
		"SELECT basicEntry FROM BasicEntry basicEntry WHERE ";

	private static final String _SQL_COUNT_BASICENTRY_WHERE =
		"SELECT COUNT(basicEntry) FROM BasicEntry basicEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No BasicEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BasicEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		BasicEntryPersistenceImpl.class);

	private FinderPath _createFinderPath(
		String cacheName, String methodName, String[] params,
		String[] columnNames, boolean baseModelResult) {

		FinderPath finderPath = new FinderPath(
			cacheName, methodName, params, columnNames, baseModelResult);

		if (!cacheName.equals(FINDER_CLASS_NAME_LIST_WITH_PAGINATION)) {
			_serviceRegistrations.add(
				_bundleContext.registerService(
					FinderPath.class, finderPath,
					MapUtil.singletonDictionary("cache.name", cacheName)));
		}

		return finderPath;
	}

	private Set<ServiceRegistration<FinderPath>> _serviceRegistrations =
		new HashSet<>();
	private ServiceRegistration<ArgumentsResolver>
		_argumentsResolverServiceRegistration;

	private static class BasicEntryModelArgumentsResolver
		implements ArgumentsResolver {

		@Override
		public Object[] getArguments(
			FinderPath finderPath, BaseModel<?> baseModel, boolean checkColumn,
			boolean original) {

			String[] columnNames = finderPath.getColumnNames();

			if ((columnNames == null) || (columnNames.length == 0)) {
				if (baseModel.isNew()) {
					return new Object[0];
				}

				return null;
			}

			BasicEntryModelImpl basicEntryModelImpl =
				(BasicEntryModelImpl)baseModel;

			long columnBitmask = basicEntryModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(basicEntryModelImpl, columnNames, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
				finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |=
						basicEntryModelImpl.getColumnBitmask(columnName);
				}

				if (finderPath.isBaseModelResult() &&
					(BasicEntryPersistenceImpl.
						FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION ==
							finderPath.getCacheName())) {

					finderPathColumnBitmask |= _ORDER_BY_COLUMNS_BITMASK;
				}

				_finderPathColumnBitmasksCache.put(
					finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(basicEntryModelImpl, columnNames, original);
			}

			return null;
		}

		private static Object[] _getValue(
			BasicEntryModelImpl basicEntryModelImpl, String[] columnNames,
			boolean original) {

			Object[] arguments = new Object[columnNames.length];

			for (int i = 0; i < arguments.length; i++) {
				String columnName = columnNames[i];

				if (original) {
					arguments[i] = basicEntryModelImpl.getColumnOriginalValue(
						columnName);
				}
				else {
					arguments[i] = basicEntryModelImpl.getColumnValue(
						columnName);
				}
			}

			return arguments;
		}

		private static final Map<FinderPath, Long>
			_finderPathColumnBitmasksCache = new ConcurrentHashMap<>();

		private static final long _ORDER_BY_COLUMNS_BITMASK;

		static {
			long orderByColumnsBitmask = 0;

			orderByColumnsBitmask |= BasicEntryModelImpl.getColumnBitmask(
				"name");

			_ORDER_BY_COLUMNS_BITMASK = orderByColumnsBitmask;
		}

	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1460736421