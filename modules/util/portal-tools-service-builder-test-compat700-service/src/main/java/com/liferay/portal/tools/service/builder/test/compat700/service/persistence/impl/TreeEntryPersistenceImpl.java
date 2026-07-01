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
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.NestedSetsTreeManager;
import com.liferay.portal.kernel.service.persistence.impl.PersistenceNestedSetsTreeManager;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat700.exception.NoSuchTreeEntryException;
import com.liferay.portal.tools.service.builder.test.compat700.model.TreeEntry;
import com.liferay.portal.tools.service.builder.test.compat700.model.impl.TreeEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat700.model.impl.TreeEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat700.service.persistence.TreeEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat700.service.persistence.TreeEntryUtil;

import java.io.Serializable;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private FinderPath _finderPathWithPaginationCountAncestors;
	private FinderPath _finderPathWithPaginationCountDescendants;
	private FinderPath _finderPathWithPaginationGetAncestors;
	private FinderPath _finderPathWithPaginationGetDescendants;

	public TreeEntryPersistenceImpl() {
		setModelClass(TreeEntry.class);
	}

	/**
	 * Caches the tree entry in the entity cache if it is enabled.
	 *
	 * @param treeEntry the tree entry
	 */
	@Override
	public void cacheResult(TreeEntry treeEntry) {
		entityCache.putResult(
			TreeEntryModelImpl.ENTITY_CACHE_ENABLED, TreeEntryImpl.class,
			treeEntry.getPrimaryKey(), treeEntry);

		treeEntry.resetOriginalValues();
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
					TreeEntryModelImpl.ENTITY_CACHE_ENABLED,
					TreeEntryImpl.class, treeEntry.getPrimaryKey()) == null) {

				cacheResult(treeEntry);
			}
			else {
				treeEntry.resetOriginalValues();
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
		entityCache.removeResult(
			TreeEntryModelImpl.ENTITY_CACHE_ENABLED, TreeEntryImpl.class,
			treeEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<TreeEntry> treeEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (TreeEntry treeEntry : treeEntries) {
			entityCache.removeResult(
				TreeEntryModelImpl.ENTITY_CACHE_ENABLED, TreeEntryImpl.class,
				treeEntry.getPrimaryKey());
		}
	}

	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				TreeEntryModelImpl.ENTITY_CACHE_ENABLED, TreeEntryImpl.class,
				primaryKey);
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
				else if (treeEntry.getParentTreeEntryId() !=
							treeEntryModelImpl.getOriginalParentTreeEntryId()) {

					nestedSetsTreeManager.move(
						treeEntry,
						fetchByPrimaryKey(
							treeEntryModelImpl.getOriginalParentTreeEntryId()),
						fetchByPrimaryKey(treeEntry.getParentTreeEntryId()));
				}

				clearCache();

				session.clear();
			}

			if (isNew) {
				session.save(treeEntry);

				treeEntry.setNew(false);
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

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (isNew) {
			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
		}

		entityCache.putResult(
			TreeEntryModelImpl.ENTITY_CACHE_ENABLED, TreeEntryImpl.class,
			treeEntry.getPrimaryKey(), treeEntry, false);

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
	 * @param primaryKey the primary key of the tree entry
	 * @return the tree entry, or <code>null</code> if a tree entry with the primary key could not be found
	 */
	@Override
	public TreeEntry fetchByPrimaryKey(Serializable primaryKey) {
		Serializable serializable = entityCache.getResult(
			TreeEntryModelImpl.ENTITY_CACHE_ENABLED, TreeEntryImpl.class,
			primaryKey);

		if (serializable == nullModel) {
			return null;
		}

		TreeEntry treeEntry = (TreeEntry)serializable;

		if (treeEntry == null) {
			Session session = null;

			try {
				session = openSession();

				treeEntry = (TreeEntry)session.get(
					TreeEntryImpl.class, primaryKey);

				if (treeEntry != null) {
					cacheResult(treeEntry);
				}
				else {
					entityCache.putResult(
						TreeEntryModelImpl.ENTITY_CACHE_ENABLED,
						TreeEntryImpl.class, primaryKey, nullModel);
				}
			}
			catch (Exception exception) {
				entityCache.removeResult(
					TreeEntryModelImpl.ENTITY_CACHE_ENABLED,
					TreeEntryImpl.class, primaryKey);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return treeEntry;
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

	@Override
	public Map<Serializable, TreeEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, TreeEntry> map =
			new HashMap<Serializable, TreeEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			TreeEntry treeEntry = fetchByPrimaryKey(primaryKey);

			if (treeEntry != null) {
				map.put(primaryKey, treeEntry);
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
				TreeEntryModelImpl.ENTITY_CACHE_ENABLED, TreeEntryImpl.class,
				primaryKey);

			if (serializable != nullModel) {
				if (serializable == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<Serializable>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, (TreeEntry)serializable);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler sb = new StringBundler(
			(uncachedPrimaryKeys.size() * 2) + 1);

		sb.append(_SQL_SELECT_TREEENTRY_WHERE_PKS_IN);

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

			for (TreeEntry treeEntry : (List<TreeEntry>)query.list()) {
				map.put(treeEntry.getPrimaryKeyObj(), treeEntry);

				cacheResult(treeEntry);

				uncachedPrimaryKeys.remove(treeEntry.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(
					TreeEntryModelImpl.ENTITY_CACHE_ENABLED,
					TreeEntryImpl.class, primaryKey, nullModel);
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
				finderCache.removeResult(
					_finderPathWithPaginationCountAncestors, finderArgs);

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
				finderCache.removeResult(
					_finderPathWithPaginationCountDescendants, finderArgs);

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
				finderCache.removeResult(
					_finderPathWithPaginationGetAncestors, finderArgs);

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
				finderCache.removeResult(
					_finderPathWithPaginationGetDescendants, finderArgs);

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
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get("value.object.finder.cache.list.threshold"));

		_finderPathWithPaginationFindAll = new FinderPath(
			TreeEntryModelImpl.ENTITY_CACHE_ENABLED,
			TreeEntryModelImpl.FINDER_CACHE_ENABLED, TreeEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			TreeEntryModelImpl.ENTITY_CACHE_ENABLED,
			TreeEntryModelImpl.FINDER_CACHE_ENABLED, TreeEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0]);

		_finderPathCountAll = new FinderPath(
			TreeEntryModelImpl.ENTITY_CACHE_ENABLED,
			TreeEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		_finderPathWithPaginationCountAncestors = new FinderPath(
			TreeEntryModelImpl.ENTITY_CACHE_ENABLED,
			TreeEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countAncestors",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			});

		_finderPathWithPaginationCountDescendants = new FinderPath(
			TreeEntryModelImpl.ENTITY_CACHE_ENABLED,
			TreeEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countDescendants",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			});

		_finderPathWithPaginationGetAncestors = new FinderPath(
			TreeEntryModelImpl.ENTITY_CACHE_ENABLED,
			TreeEntryModelImpl.FINDER_CACHE_ENABLED, TreeEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "getAncestors",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			});

		_finderPathWithPaginationGetDescendants = new FinderPath(
			TreeEntryModelImpl.ENTITY_CACHE_ENABLED,
			TreeEntryModelImpl.FINDER_CACHE_ENABLED, TreeEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "getDescendants",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			});

		TreeEntryUtil.setPersistence(this);
	}

	public void destroy() {
		TreeEntryUtil.setPersistence(null);

		entityCache.removeCache(TreeEntryImpl.class.getName());

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

	protected NestedSetsTreeManager<TreeEntry> nestedSetsTreeManager =
		new PersistenceNestedSetsTreeManager<TreeEntry>(
			this, "TreeEntry", "TreeEntry", TreeEntryImpl.class, "treeEntryId",
			"groupId", "leftTreeEntryId", "rightTreeEntryId");
	protected boolean rebuildTreeEnabled = true;

	private static final String _ENTITY_ALIAS_PREFIX =
		TreeEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_TREEENTRY =
		"SELECT treeEntry FROM TreeEntry treeEntry";

	private static final String _SQL_SELECT_TREEENTRY_WHERE_PKS_IN =
		"SELECT treeEntry FROM TreeEntry treeEntry WHERE treeEntryId IN (";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No TreeEntry exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		TreeEntryPersistenceImpl.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1532523700