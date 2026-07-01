/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat730.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.NestedSetsTreeManager;
import com.liferay.portal.kernel.service.persistence.impl.PersistenceNestedSetsTreeManager;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat730.exception.NoSuchTreeEntryException;
import com.liferay.portal.tools.service.builder.test.compat730.model.TreeEntry;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.TreeEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat730.model.impl.TreeEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.TreeEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat730.service.persistence.TreeEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

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
 * The persistence implementation for the tree entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class TreeEntryPersistenceImpl
	extends BasePersistenceImpl<TreeEntry> implements TreeEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>TreeEntryUtil</code> to access the tree entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		TreeEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationCountAncestors;
	private FinderPath _finderPathWithPaginationCountDescendants;
	private FinderPath _finderPathWithPaginationGetAncestors;
	private FinderPath _finderPathWithPaginationGetDescendants;

	public TreeEntryPersistenceImpl() {
		setModelClass(TreeEntry.class);

		setModelImplClass(TreeEntryImpl.class);
		setModelPKClass(long.class);
	}

	/**
	 * Caches the tree entry in the entity cache if it is enabled.
	 *
	 * @param treeEntry the tree entry
	 */
	@Override
	public void cacheResult(TreeEntry treeEntry) {
		entityCache.putResult(
			TreeEntryImpl.class, treeEntry.getPrimaryKey(), treeEntry);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the tree entries in the entity cache if it is enabled.
	 *
	 * @param treeEntries the tree entries
	 */
	@Override
	public void cacheResult(List<TreeEntry> treeEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (treeEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (TreeEntry treeEntry : treeEntries) {
			if (entityCache.getResult(
					TreeEntryImpl.class, treeEntry.getPrimaryKey()) == null) {

				cacheResult(treeEntry);
			}
		}
	}

	/**
	 * Clears the cache for all tree entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(TreeEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the tree entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(TreeEntry treeEntry) {
		entityCache.removeResult(TreeEntryImpl.class, treeEntry);
	}

	@Override
	public void clearCache(List<TreeEntry> treeEntries) {
		for (TreeEntry treeEntry : treeEntries) {
			entityCache.removeResult(TreeEntryImpl.class, treeEntry);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(TreeEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new tree entry with the primary key. Does not add the tree entry to the database.
	 *
	 * @param treeEntryId the primary key for the new tree entry
	 * @return the new tree entry
	 */
	@Override
	public TreeEntry create(long treeEntryId) {
		TreeEntry treeEntry = new TreeEntryImpl();

		treeEntry.setNew(true);
		treeEntry.setPrimaryKey(treeEntryId);

		return treeEntry;
	}

	/**
	 * Removes the tree entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param treeEntryId the primary key of the tree entry
	 * @return the tree entry that was removed
	 * @throws NoSuchTreeEntryException if a tree entry with the primary key could not be found
	 */
	@Override
	public TreeEntry remove(long treeEntryId) throws NoSuchTreeEntryException {
		return remove((Serializable)treeEntryId);
	}

	/**
	 * Removes the tree entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the tree entry
	 * @return the tree entry that was removed
	 * @throws NoSuchTreeEntryException if a tree entry with the primary key could not be found
	 */
	@Override
	public TreeEntry remove(Serializable primaryKey)
		throws NoSuchTreeEntryException {

		Session session = null;

		try {
			session = openSession();

			TreeEntry treeEntry = (TreeEntry)session.get(
				TreeEntryImpl.class, primaryKey);

			if (treeEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchTreeEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(treeEntry);
		}
		catch (NoSuchTreeEntryException noSuchEntityException) {
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
	protected TreeEntry removeImpl(TreeEntry treeEntry) {
		Session session = null;

		try {
			session = openSession();

			if (rebuildTreeEnabled) {
				if (session.isDirty()) {
					session.flush();
				}

				nestedSetsTreeManager.delete(treeEntry);

				clearCache();

				session.clear();
			}

			if (!session.contains(treeEntry)) {
				treeEntry = (TreeEntry)session.get(
					TreeEntryImpl.class, treeEntry.getPrimaryKeyObj());
			}

			if (treeEntry != null) {
				session.delete(treeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (treeEntry != null) {
			clearCache(treeEntry);
		}

		return treeEntry;
	}

	@Override
	public TreeEntry updateImpl(TreeEntry treeEntry) {
		boolean isNew = treeEntry.isNew();

		if (!(treeEntry instanceof TreeEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(treeEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(treeEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in treeEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom TreeEntry implementation " +
					treeEntry.getClass());
		}

		TreeEntryModelImpl treeEntryModelImpl = (TreeEntryModelImpl)treeEntry;

		Session session = null;

		try {
			session = openSession();

			if (rebuildTreeEnabled) {
				if (session.isDirty()) {
					session.flush();
				}

				if (isNew) {
					nestedSetsTreeManager.insert(
						treeEntry,
						fetchByPrimaryKey(treeEntry.getParentTreeEntryId()));
				}
				else if ((treeEntryModelImpl.getColumnOriginalValue(
							"parentTreeEntryId") != null) &&
						 !Objects.equals(
							 treeEntry.getParentTreeEntryId(),
							 treeEntryModelImpl.getColumnOriginalValue(
								 "parentTreeEntryId"))) {

					nestedSetsTreeManager.move(
						treeEntry,
						fetchByPrimaryKey(
							treeEntryModelImpl.getColumnOriginalValue(
								"parentTreeEntryId")),
						fetchByPrimaryKey(treeEntry.getParentTreeEntryId()));
				}

				clearCache();

				session.clear();
			}

			if (isNew) {
				session.save(treeEntry);
			}
			else {
				treeEntry = (TreeEntry)session.merge(treeEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(TreeEntryImpl.class, treeEntry, false, true);

		if (isNew) {
			treeEntry.setNew(false);
		}

		treeEntry.resetOriginalValues();

		return treeEntry;
	}

	/**
	 * Returns the tree entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the tree entry
	 * @return the tree entry
	 * @throws NoSuchTreeEntryException if a tree entry with the primary key could not be found
	 */
	@Override
	public TreeEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchTreeEntryException {

		TreeEntry treeEntry = fetchByPrimaryKey(primaryKey);

		if (treeEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchTreeEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return treeEntry;
	}

	/**
	 * Returns the tree entry with the primary key or throws a <code>NoSuchTreeEntryException</code> if it could not be found.
	 *
	 * @param treeEntryId the primary key of the tree entry
	 * @return the tree entry
	 * @throws NoSuchTreeEntryException if a tree entry with the primary key could not be found
	 */
	@Override
	public TreeEntry findByPrimaryKey(long treeEntryId)
		throws NoSuchTreeEntryException {

		return findByPrimaryKey((Serializable)treeEntryId);
	}

	/**
	 * Returns the tree entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param treeEntryId the primary key of the tree entry
	 * @return the tree entry, or <code>null</code> if a tree entry with the primary key could not be found
	 */
	@Override
	public TreeEntry fetchByPrimaryKey(long treeEntryId) {
		return fetchByPrimaryKey((Serializable)treeEntryId);
	}

	/**
	 * Returns all the tree entries.
	 *
	 * @return the tree entries
	 */
	@Override
	public List<TreeEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the tree entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TreeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of tree entries
	 * @param end the upper bound of the range of tree entries (not inclusive)
	 * @return the range of tree entries
	 */
	@Override
	public List<TreeEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the tree entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TreeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of tree entries
	 * @param end the upper bound of the range of tree entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of tree entries
	 */
	@Override
	public List<TreeEntry> findAll(
		int start, int end, OrderByComparator<TreeEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the tree entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TreeEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of tree entries
	 * @param end the upper bound of the range of tree entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of tree entries
	 */
	@Override
	public List<TreeEntry> findAll(
		int start, int end, OrderByComparator<TreeEntry> orderByComparator,
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

		List<TreeEntry> list = null;

		if (useFinderCache) {
			list = (List<TreeEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_TREEENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_TREEENTRY;

				sql = sql.concat(TreeEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<TreeEntry>)QueryUtil.list(
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
	 * Removes all the tree entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (TreeEntry treeEntry : findAll()) {
			remove(treeEntry);
		}
	}

	/**
	 * Returns the number of tree entries.
	 *
	 * @return the number of tree entries
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
					"SELECT COUNT(treeEntry) FROM TreeEntry treeEntry");

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
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "treeEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_TREEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return TreeEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public long countAncestors(TreeEntry treeEntry) {
		Object[] finderArgs = new Object[] {
			treeEntry.getGroupId(), treeEntry.getLeftTreeEntryId(),
			treeEntry.getRightTreeEntryId()
		};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountAncestors, finderArgs, this);

		if (count == null) {
			try {
				count = nestedSetsTreeManager.countAncestors(treeEntry);

				finderCache.putResult(
					_finderPathWithPaginationCountAncestors, finderArgs, count);
			}
			catch (SystemException systemException) {
				throw systemException;
			}
		}

		return count.intValue();
	}

	@Override
	public long countDescendants(TreeEntry treeEntry) {
		Object[] finderArgs = new Object[] {
			treeEntry.getGroupId(), treeEntry.getLeftTreeEntryId(),
			treeEntry.getRightTreeEntryId()
		};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountDescendants, finderArgs, this);

		if (count == null) {
			try {
				count = nestedSetsTreeManager.countDescendants(treeEntry);

				finderCache.putResult(
					_finderPathWithPaginationCountDescendants, finderArgs,
					count);
			}
			catch (SystemException systemException) {
				throw systemException;
			}
		}

		return count.intValue();
	}

	@Override
	public List<TreeEntry> getAncestors(TreeEntry treeEntry) {
		Object[] finderArgs = new Object[] {
			treeEntry.getGroupId(), treeEntry.getLeftTreeEntryId(),
			treeEntry.getRightTreeEntryId()
		};

		List<TreeEntry> list = (List<TreeEntry>)finderCache.getResult(
			_finderPathWithPaginationGetAncestors, finderArgs, this);

		if ((list != null) && !list.isEmpty()) {
			for (TreeEntry tempTreeEntry : list) {
				if ((treeEntry.getLeftTreeEntryId() <
						tempTreeEntry.getLeftTreeEntryId()) ||
					(treeEntry.getRightTreeEntryId() >
						tempTreeEntry.getRightTreeEntryId())) {

					list = null;

					break;
				}
			}
		}

		if (list == null) {
			try {
				list = nestedSetsTreeManager.getAncestors(treeEntry);

				cacheResult(list);

				finderCache.putResult(
					_finderPathWithPaginationGetAncestors, finderArgs, list);
			}
			catch (SystemException systemException) {
				throw systemException;
			}
		}

		return list;
	}

	@Override
	public List<TreeEntry> getDescendants(TreeEntry treeEntry) {
		Object[] finderArgs = new Object[] {
			treeEntry.getGroupId(), treeEntry.getLeftTreeEntryId(),
			treeEntry.getRightTreeEntryId()
		};

		List<TreeEntry> list = (List<TreeEntry>)finderCache.getResult(
			_finderPathWithPaginationGetDescendants, finderArgs, this);

		if ((list != null) && !list.isEmpty()) {
			for (TreeEntry tempTreeEntry : list) {
				if ((treeEntry.getLeftTreeEntryId() >
						tempTreeEntry.getLeftTreeEntryId()) ||
					(treeEntry.getRightTreeEntryId() <
						tempTreeEntry.getRightTreeEntryId())) {

					list = null;

					break;
				}
			}
		}

		if (list == null) {
			try {
				list = nestedSetsTreeManager.getDescendants(treeEntry);

				cacheResult(list);

				finderCache.putResult(
					_finderPathWithPaginationGetDescendants, finderArgs, list);
			}
			catch (SystemException systemException) {
				throw systemException;
			}
		}

		return list;
	}

	/**
	 * Rebuilds the tree entries tree for the scope using the modified pre-order tree traversal algorithm.
	 *
	 * <p>
	 * Only call this method if the tree has become stale through operations other than normal CRUD. Under normal circumstances the tree is automatically rebuilt whenver necessary.
	 * </p>
	 *
	 * @param groupId the ID of the scope
	 * @param force whether to force the rebuild even if the tree is not stale
	 */
	@Override
	public void rebuildTree(long groupId, boolean force) {
		if (!rebuildTreeEnabled) {
			return;
		}

		if (force || (countOrphanTreeNodes(groupId) > 0)) {
			Session session = null;

			try {
				session = openSession();

				if (session.isDirty()) {
					session.flush();
				}

				SQLQuery selectSQLQuery = session.createSQLQuery(
					"SELECT treeEntryId FROM TreeEntry WHERE groupId = ? AND parentTreeEntryId = ? ORDER BY treeEntryId ASC");

				selectSQLQuery.addScalar(
					"treeEntryId", com.liferay.portal.kernel.dao.orm.Type.LONG);

				SQLQuery updateSQLQuery = session.createSQLQuery(
					"UPDATE TreeEntry SET leftTreeEntryId = ?, rightTreeEntryId = ? WHERE treeEntryId = ?");

				rebuildTree(
					session, selectSQLQuery, updateSQLQuery, groupId, 0, 0);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}

			clearCache();
		}
	}

	@Override
	public void setRebuildTreeEnabled(boolean rebuildTreeEnabled) {
		this.rebuildTreeEnabled = rebuildTreeEnabled;
	}

	protected long countOrphanTreeNodes(long groupId) {
		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(
				"SELECT COUNT(*) AS COUNT_VALUE FROM TreeEntry WHERE groupId = ? AND (leftTreeEntryId = 0 OR leftTreeEntryId IS NULL OR rightTreeEntryId = 0 OR rightTreeEntryId IS NULL)");

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (Long)sqlQuery.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected long rebuildTree(
		Session session, SQLQuery selectSQLQuery, SQLQuery updateSQLQuery,
		long groupId, long parentTreeEntryId, long leftTreeEntryId) {

		long rightTreeEntryId = leftTreeEntryId + 1;

		QueryPos queryPos = QueryPos.getInstance(selectSQLQuery);

		queryPos.add(groupId);
		queryPos.add(parentTreeEntryId);

		List<Long> treeEntryIds = selectSQLQuery.list();

		for (long treeEntryId : treeEntryIds) {
			rightTreeEntryId = rebuildTree(
				session, selectSQLQuery, updateSQLQuery, groupId, treeEntryId,
				rightTreeEntryId);
		}

		if (parentTreeEntryId > 0) {
			queryPos = QueryPos.getInstance(updateSQLQuery);

			queryPos.add(leftTreeEntryId);
			queryPos.add(rightTreeEntryId);
			queryPos.add(parentTreeEntryId);

			updateSQLQuery.executeUpdate();
		}

		return rightTreeEntryId + 1;
	}

	/**
	 * Initializes the tree entry persistence.
	 */
	public void afterPropertiesSet() {
		Bundle bundle = FrameworkUtil.getBundle(TreeEntryPersistenceImpl.class);

		_bundleContext = bundle.getBundleContext();

		_argumentsResolverServiceRegistration = _bundleContext.registerService(
			ArgumentsResolver.class, new TreeEntryModelArgumentsResolver(),
			MapUtil.singletonDictionary(
				"model.class.name", TreeEntry.class.getName()));

		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationCountAncestors = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countAncestors",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "leftTreeEntryId", "rightTreeEntryId"},
			false);

		_finderPathWithPaginationCountDescendants = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countDescendants",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "leftTreeEntryId", "rightTreeEntryId"},
			false);

		_finderPathWithPaginationGetAncestors = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "getAncestors",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "leftTreeEntryId", "rightTreeEntryId"},
			true);

		_finderPathWithPaginationGetDescendants = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "getDescendants",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "leftTreeEntryId", "rightTreeEntryId"},
			true);

		TreeEntryUtil.setPersistence(this);
	}

	public void destroy() {
		TreeEntryUtil.setPersistence(null);

		entityCache.removeCache(TreeEntryImpl.class.getName());

		_argumentsResolverServiceRegistration.unregister();

		for (ServiceRegistration<FinderPath> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private BundleContext _bundleContext;

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	protected NestedSetsTreeManager<TreeEntry> nestedSetsTreeManager =
		new PersistenceNestedSetsTreeManager<TreeEntry>(
			this, "TreeEntry", "TreeEntry", TreeEntryImpl.class, "treeEntryId",
			"groupId", "leftTreeEntryId", "rightTreeEntryId");
	protected boolean rebuildTreeEnabled = true;

	private static final String _ENTITY_ALIAS_PREFIX =
		TreeEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_TREEENTRY =
		"SELECT treeEntry FROM TreeEntry treeEntry";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No TreeEntry exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		TreeEntryPersistenceImpl.class);

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

	private static class TreeEntryModelArgumentsResolver
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

			TreeEntryModelImpl treeEntryModelImpl =
				(TreeEntryModelImpl)baseModel;

			long columnBitmask = treeEntryModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(treeEntryModelImpl, columnNames, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
				finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |=
						treeEntryModelImpl.getColumnBitmask(columnName);
				}

				_finderPathColumnBitmasksCache.put(
					finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(treeEntryModelImpl, columnNames, original);
			}

			return null;
		}

		private static Object[] _getValue(
			TreeEntryModelImpl treeEntryModelImpl, String[] columnNames,
			boolean original) {

			Object[] arguments = new Object[columnNames.length];

			for (int i = 0; i < arguments.length; i++) {
				String columnName = columnNames[i];

				if (original) {
					arguments[i] = treeEntryModelImpl.getColumnOriginalValue(
						columnName);
				}
				else {
					arguments[i] = treeEntryModelImpl.getColumnValue(
						columnName);
				}
			}

			return arguments;
		}

		private static final Map<FinderPath, Long>
			_finderPathColumnBitmasksCache = new ConcurrentHashMap<>();

	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1425244408