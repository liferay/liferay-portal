/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchArrayableEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.ArrayableEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.ArrayableEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.ArrayableEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.ArrayableEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the arrayable entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ArrayableEntryPersistence.class)
public class ArrayableEntryPersistenceImpl
	extends BasePersistenceImpl<ArrayableEntry, NoSuchArrayableEntryException>
	implements ArrayableEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ArrayableEntryUtil</code> to access the arrayable entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ArrayableEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;
	private FinderPath _finderPathWithPaginationCountByGroupId;

	/**
	 * Returns an ordered range of all the arrayable entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching arrayable entries
	 */
	@Override
	public List<ArrayableEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<ArrayableEntry> orderByComparator,
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

		List<ArrayableEntry> list = null;

		if (useFinderCache) {
			list = (List<ArrayableEntry>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ArrayableEntry arrayableEntry : list) {
					if (groupId != arrayableEntry.getGroupId()) {
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

			sb.append(_SQL_SELECT_ARRAYABLEENTRY_WHERE);

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
			}
			else {
				sb.append(ArrayableEntryModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				list = (List<ArrayableEntry>)QueryUtil.list(
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
	 * Returns the first arrayable entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching arrayable entry
	 * @throws NoSuchArrayableEntryException if a matching arrayable entry could not be found
	 */
	@Override
	public ArrayableEntry findByGroupId_First(
			long groupId, OrderByComparator<ArrayableEntry> orderByComparator)
		throws NoSuchArrayableEntryException {

		ArrayableEntry arrayableEntry = fetchByGroupId_First(
			groupId, orderByComparator);

		if (arrayableEntry != null) {
			return arrayableEntry;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchArrayableEntryException(sb.toString());
	}

	/**
	 * Returns the first arrayable entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching arrayable entry, or <code>null</code> if a matching arrayable entry could not be found
	 */
	@Override
	public ArrayableEntry fetchByGroupId_First(
		long groupId, OrderByComparator<ArrayableEntry> orderByComparator) {

		List<ArrayableEntry> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns all the arrayable entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @return the matching arrayable entries
	 */
	@Override
	public List<ArrayableEntry> findByGroupId(long[] groupIds) {
		return findByGroupId(
			groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the arrayable entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @return the range of matching arrayable entries
	 */
	@Override
	public List<ArrayableEntry> findByGroupId(
		long[] groupIds, int start, int end) {

		return findByGroupId(groupIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the arrayable entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching arrayable entries
	 */
	@Override
	public List<ArrayableEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<ArrayableEntry> orderByComparator) {

		return findByGroupId(groupIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the arrayable entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching arrayable entries
	 */
	@Override
	public List<ArrayableEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<ArrayableEntry> orderByComparator,
		boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (groupIds.length == 1) {
			return findByGroupId(groupIds[0], start, end, orderByComparator);
		}

		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderArgs = new Object[] {StringUtil.merge(groupIds)};
			}
		}
		else if (useFinderCache) {
			finderArgs = new Object[] {
				StringUtil.merge(groupIds), start, end, orderByComparator
			};
		}

		List<ArrayableEntry> list = null;

		if (useFinderCache) {
			list = (List<ArrayableEntry>)finderCache.getResult(
				_finderPathWithPaginationFindByGroupId, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (ArrayableEntry arrayableEntry : list) {
					if (!ArrayUtil.contains(
							groupIds, arrayableEntry.getGroupId())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			try {
				if ((databaseInMaxParameters > 0) &&
					(groupIds.length > databaseInMaxParameters)) {

					list = new ArrayList<ArrayableEntry>();

					long[][] groupIdsPages = (long[][])ArrayUtil.split(
						groupIds, databaseInMaxParameters);

					for (long[] groupIdsPage : groupIdsPages) {
						list.addAll(
							_findByGroupId(
								groupIdsPage, QueryUtil.ALL_POS,
								QueryUtil.ALL_POS, orderByComparator));
					}

					Collections.sort(list, orderByComparator);

					cacheResult(list);

					list = Collections.unmodifiableList(
						ListUtil.subList(list, start, end));
				}
				else {
					list = _findByGroupId(
						groupIds, start, end, orderByComparator);

					cacheResult(list);
				}

				if (useFinderCache) {
					finderCache.putResult(
						_finderPathWithPaginationFindByGroupId, finderArgs,
						list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
		}

		return list;
	}

	private List<ArrayableEntry> _findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<ArrayableEntry> orderByComparator) {

		List<ArrayableEntry> list = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_SELECT_ARRAYABLEENTRY_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (orderByComparator != null) {
			appendOrderByComparator(
				sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
		}
		else {
			sb.append(ArrayableEntryModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			list = (List<ArrayableEntry>)QueryUtil.list(
				query, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return list;
	}

	/**
	 * Removes all the arrayable entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (ArrayableEntry arrayableEntry :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(arrayableEntry);
		}
	}

	/**
	 * Returns the number of arrayable entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching arrayable entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		FinderPath finderPath = _finderPathCountByGroupId;

		Object[] finderArgs = new Object[] {groupId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_ARRAYABLEENTRY_WHERE);

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

	/**
	 * Returns the number of arrayable entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching arrayable entries
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		Object[] finderArgs = new Object[] {StringUtil.merge(groupIds)};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountByGroupId, finderArgs, this);

		if (count == null) {
			try {
				if ((databaseInMaxParameters > 0) &&
					(groupIds.length > databaseInMaxParameters)) {

					count = Long.valueOf(0);

					long[][] groupIdsPages = (long[][])ArrayUtil.split(
						groupIds, databaseInMaxParameters);

					for (long[] groupIdsPage : groupIdsPages) {
						count += Long.valueOf(_countByGroupId(groupIdsPage));
					}
				}
				else {
					count = Long.valueOf(_countByGroupId(groupIds));
				}

				finderCache.putResult(
					_finderPathWithPaginationCountByGroupId, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
		}

		return count.intValue();
	}

	private int _countByGroupId(long[] groupIds) {
		Long count = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_COUNT_ARRAYABLEENTRY_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			count = (Long)query.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"arrayableEntry.groupId = ?";

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_7 =
		"arrayableEntry.groupId IN (";

	public ArrayableEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("integer", "integer_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ArrayableEntry.class);

		setModelImplClass(ArrayableEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ArrayableEntryTable.INSTANCE);
	}

	/**
	 * Creates a new arrayable entry with the primary key. Does not add the arrayable entry to the database.
	 *
	 * @param arrayableEntryId the primary key for the new arrayable entry
	 * @return the new arrayable entry
	 */
	@Override
	public ArrayableEntry create(long arrayableEntryId) {
		ArrayableEntry arrayableEntry = new ArrayableEntryImpl();

		arrayableEntry.setNew(true);
		arrayableEntry.setPrimaryKey(arrayableEntryId);

		arrayableEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return arrayableEntry;
	}

	/**
	 * Removes the arrayable entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param arrayableEntryId the primary key of the arrayable entry
	 * @return the arrayable entry that was removed
	 * @throws NoSuchArrayableEntryException if a arrayable entry with the primary key could not be found
	 */
	@Override
	public ArrayableEntry remove(long arrayableEntryId)
		throws NoSuchArrayableEntryException {

		return remove((Serializable)arrayableEntryId);
	}

	@Override
	protected ArrayableEntry removeImpl(ArrayableEntry arrayableEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(arrayableEntry)) {
				arrayableEntry = (ArrayableEntry)session.get(
					ArrayableEntryImpl.class,
					arrayableEntry.getPrimaryKeyObj());
			}

			if (arrayableEntry != null) {
				session.delete(arrayableEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (arrayableEntry != null) {
			clearCache(arrayableEntry);
		}

		return arrayableEntry;
	}

	@Override
	public ArrayableEntry updateImpl(ArrayableEntry arrayableEntry) {
		boolean isNew = arrayableEntry.isNew();

		if (!(arrayableEntry instanceof ArrayableEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(arrayableEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					arrayableEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in arrayableEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ArrayableEntry implementation " +
					arrayableEntry.getClass());
		}

		ArrayableEntryModelImpl arrayableEntryModelImpl =
			(ArrayableEntryModelImpl)arrayableEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(arrayableEntry);
			}
			else {
				arrayableEntry = (ArrayableEntry)session.merge(arrayableEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(arrayableEntry, false);

		if (isNew) {
			arrayableEntry.setNew(false);
		}

		arrayableEntry.resetOriginalValues();

		return arrayableEntry;
	}

	/**
	 * Returns the arrayable entry with the primary key or throws a <code>NoSuchArrayableEntryException</code> if it could not be found.
	 *
	 * @param arrayableEntryId the primary key of the arrayable entry
	 * @return the arrayable entry
	 * @throws NoSuchArrayableEntryException if a arrayable entry with the primary key could not be found
	 */
	@Override
	public ArrayableEntry findByPrimaryKey(long arrayableEntryId)
		throws NoSuchArrayableEntryException {

		return findByPrimaryKey((Serializable)arrayableEntryId);
	}

	/**
	 * Returns the arrayable entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param arrayableEntryId the primary key of the arrayable entry
	 * @return the arrayable entry, or <code>null</code> if a arrayable entry with the primary key could not be found
	 */
	@Override
	public ArrayableEntry fetchByPrimaryKey(long arrayableEntryId) {
		return fetchByPrimaryKey((Serializable)arrayableEntryId);
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
		return "arrayableEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ARRAYABLEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ArrayableEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the arrayable entry persistence.
	 */
	@Activate
	public void activate() {
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
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		_finderPathWithPaginationCountByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		ArrayableEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ArrayableEntryUtil.setPersistence(null);

		entityCache.removeCache(ArrayableEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		ArrayableEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ARRAYABLEENTRY =
		"SELECT arrayableEntry FROM ArrayableEntry arrayableEntry";

	private static final String _SQL_SELECT_ARRAYABLEENTRY_WHERE =
		"SELECT arrayableEntry FROM ArrayableEntry arrayableEntry WHERE ";

	private static final String _SQL_COUNT_ARRAYABLEENTRY_WHERE =
		"SELECT COUNT(arrayableEntry) FROM ArrayableEntry arrayableEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ArrayableEntry exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"integer", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1179752810