/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchCacheFieldEntryException;
import com.liferay.portal.tools.service.builder.test.model.CacheFieldEntry;
import com.liferay.portal.tools.service.builder.test.model.CacheFieldEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.CacheFieldEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.CacheFieldEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheFieldEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheFieldEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the cache field entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CacheFieldEntryPersistenceImpl
	extends BasePersistenceImpl<CacheFieldEntry, NoSuchCacheFieldEntryException>
	implements CacheFieldEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CacheFieldEntryUtil</code> to access the cache field entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CacheFieldEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CacheFieldEntry, NoSuchCacheFieldEntryException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the cache field entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CacheFieldEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cache field entries
	 * @param end the upper bound of the range of cache field entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cache field entries
	 */
	@Override
	public List<CacheFieldEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CacheFieldEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cache field entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cache field entry
	 * @throws NoSuchCacheFieldEntryException if a matching cache field entry could not be found
	 */
	@Override
	public CacheFieldEntry findByGroupId_First(
			long groupId, OrderByComparator<CacheFieldEntry> orderByComparator)
		throws NoSuchCacheFieldEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first cache field entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cache field entry, or <code>null</code> if a matching cache field entry could not be found
	 */
	@Override
	public CacheFieldEntry fetchByGroupId_First(
		long groupId, OrderByComparator<CacheFieldEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the cache field entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of cache field entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cache field entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	public CacheFieldEntryPersistenceImpl() {
		setModelClass(CacheFieldEntry.class);

		setModelImplClass(CacheFieldEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CacheFieldEntryTable.INSTANCE);
	}

	/**
	 * Creates a new cache field entry with the primary key. Does not add the cache field entry to the database.
	 *
	 * @param cacheFieldEntryId the primary key for the new cache field entry
	 * @return the new cache field entry
	 */
	@Override
	public CacheFieldEntry create(long cacheFieldEntryId) {
		CacheFieldEntry cacheFieldEntry = new CacheFieldEntryImpl();

		cacheFieldEntry.setNew(true);
		cacheFieldEntry.setPrimaryKey(cacheFieldEntryId);

		return cacheFieldEntry;
	}

	/**
	 * Removes the cache field entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param cacheFieldEntryId the primary key of the cache field entry
	 * @return the cache field entry that was removed
	 * @throws NoSuchCacheFieldEntryException if a cache field entry with the primary key could not be found
	 */
	@Override
	public CacheFieldEntry remove(long cacheFieldEntryId)
		throws NoSuchCacheFieldEntryException {

		return remove((Serializable)cacheFieldEntryId);
	}

	@Override
	protected CacheFieldEntry removeImpl(CacheFieldEntry cacheFieldEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cacheFieldEntry)) {
				cacheFieldEntry = (CacheFieldEntry)session.get(
					CacheFieldEntryImpl.class,
					cacheFieldEntry.getPrimaryKeyObj());
			}

			if (cacheFieldEntry != null) {
				session.delete(cacheFieldEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cacheFieldEntry != null) {
			clearCache(cacheFieldEntry);
		}

		return cacheFieldEntry;
	}

	@Override
	public CacheFieldEntry updateImpl(CacheFieldEntry cacheFieldEntry) {
		boolean isNew = cacheFieldEntry.isNew();

		if (!(cacheFieldEntry instanceof CacheFieldEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cacheFieldEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cacheFieldEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cacheFieldEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CacheFieldEntry implementation " +
					cacheFieldEntry.getClass());
		}

		CacheFieldEntryModelImpl cacheFieldEntryModelImpl =
			(CacheFieldEntryModelImpl)cacheFieldEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(cacheFieldEntry);
			}
			else {
				cacheFieldEntry = (CacheFieldEntry)session.merge(
					cacheFieldEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cacheFieldEntry, false);

		if (isNew) {
			cacheFieldEntry.setNew(false);
		}

		cacheFieldEntry.resetOriginalValues();

		return cacheFieldEntry;
	}

	/**
	 * Returns the cache field entry with the primary key or throws a <code>NoSuchCacheFieldEntryException</code> if it could not be found.
	 *
	 * @param cacheFieldEntryId the primary key of the cache field entry
	 * @return the cache field entry
	 * @throws NoSuchCacheFieldEntryException if a cache field entry with the primary key could not be found
	 */
	@Override
	public CacheFieldEntry findByPrimaryKey(long cacheFieldEntryId)
		throws NoSuchCacheFieldEntryException {

		return findByPrimaryKey((Serializable)cacheFieldEntryId);
	}

	/**
	 * Returns the cache field entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param cacheFieldEntryId the primary key of the cache field entry
	 * @return the cache field entry, or <code>null</code> if a cache field entry with the primary key could not be found
	 */
	@Override
	public CacheFieldEntry fetchByPrimaryKey(long cacheFieldEntryId) {
		return fetchByPrimaryKey((Serializable)cacheFieldEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "cacheFieldEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CACHEFIELDENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CacheFieldEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cache field entry persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_CACHEFIELDENTRY_WHERE,
				_SQL_COUNT_CACHEFIELDENTRY_WHERE,
				CacheFieldEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"cacheFieldEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, CacheFieldEntry::getGroupId));

		CacheFieldEntryUtil.setPersistence(this);
	}

	public void destroy() {
		CacheFieldEntryUtil.setPersistence(null);

		entityCache.removeCache(CacheFieldEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CacheFieldEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CACHEFIELDENTRY =
		"SELECT cacheFieldEntry FROM CacheFieldEntry cacheFieldEntry";

	private static final String _SQL_SELECT_CACHEFIELDENTRY_WHERE =
		"SELECT cacheFieldEntry FROM CacheFieldEntry cacheFieldEntry WHERE ";

	private static final String _SQL_COUNT_CACHEFIELDENTRY_WHERE =
		"SELECT COUNT(cacheFieldEntry) FROM CacheFieldEntry cacheFieldEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CacheFieldEntry exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1572352880