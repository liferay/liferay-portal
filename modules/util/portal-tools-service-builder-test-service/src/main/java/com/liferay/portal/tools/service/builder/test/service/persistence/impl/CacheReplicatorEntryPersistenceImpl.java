/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchCacheReplicatorEntryException;
import com.liferay.portal.tools.service.builder.test.model.CacheReplicatorEntry;
import com.liferay.portal.tools.service.builder.test.model.CacheReplicatorEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.CacheReplicatorEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.CacheReplicatorEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheReplicatorEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheReplicatorEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the cache replicator entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CacheReplicatorEntryPersistenceImpl
	extends BasePersistenceImpl
		<CacheReplicatorEntry, NoSuchCacheReplicatorEntryException>
	implements CacheReplicatorEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CacheReplicatorEntryUtil</code> to access the cache replicator entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CacheReplicatorEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CacheReplicatorEntry, NoSuchCacheReplicatorEntryException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cache replicator entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CacheReplicatorEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cache replicator entries
	 * @param end the upper bound of the range of cache replicator entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cache replicator entries
	 */
	@Override
	public List<CacheReplicatorEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CacheReplicatorEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cache replicator entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cache replicator entry
	 * @throws NoSuchCacheReplicatorEntryException if a matching cache replicator entry could not be found
	 */
	@Override
	public CacheReplicatorEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<CacheReplicatorEntry> orderByComparator)
		throws NoSuchCacheReplicatorEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cache replicator entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cache replicator entry, or <code>null</code> if a matching cache replicator entry could not be found
	 */
	@Override
	public CacheReplicatorEntry fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CacheReplicatorEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the cache replicator entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cache replicator entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cache replicator entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private UniquePersistenceFinder
		<CacheReplicatorEntry, NoSuchCacheReplicatorEntryException>
			_uniquePersistenceFinderByName;

	/**
	 * Returns the cache replicator entry where name = &#63; or throws a <code>NoSuchCacheReplicatorEntryException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching cache replicator entry
	 * @throws NoSuchCacheReplicatorEntryException if a matching cache replicator entry could not be found
	 */
	@Override
	public CacheReplicatorEntry findByName(String name)
		throws NoSuchCacheReplicatorEntryException {

		return _uniquePersistenceFinderByName.find(
			finderCache, new Object[] {name});
	}

	/**
	 * Returns the cache replicator entry where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cache replicator entry, or <code>null</code> if a matching cache replicator entry could not be found
	 */
	@Override
	public CacheReplicatorEntry fetchByName(
		String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByName.fetch(
			finderCache, new Object[] {name}, useFinderCache);
	}

	/**
	 * Removes the cache replicator entry where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the cache replicator entry that was removed
	 */
	@Override
	public CacheReplicatorEntry removeByName(String name)
		throws NoSuchCacheReplicatorEntryException {

		CacheReplicatorEntry cacheReplicatorEntry = findByName(name);

		return remove(cacheReplicatorEntry);
	}

	/**
	 * Returns the number of cache replicator entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching cache replicator entries
	 */
	@Override
	public int countByName(String name) {
		return _uniquePersistenceFinderByName.count(
			finderCache, new Object[] {name});
	}

	public CacheReplicatorEntryPersistenceImpl() {
		setModelClass(CacheReplicatorEntry.class);

		setModelImplClass(CacheReplicatorEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CacheReplicatorEntryTable.INSTANCE);
	}

	/**
	 * Creates a new cache replicator entry with the primary key. Does not add the cache replicator entry to the database.
	 *
	 * @param cacheReplicatorEntryId the primary key for the new cache replicator entry
	 * @return the new cache replicator entry
	 */
	@Override
	public CacheReplicatorEntry create(long cacheReplicatorEntryId) {
		CacheReplicatorEntry cacheReplicatorEntry =
			new CacheReplicatorEntryImpl();

		cacheReplicatorEntry.setNew(true);
		cacheReplicatorEntry.setPrimaryKey(cacheReplicatorEntryId);

		cacheReplicatorEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cacheReplicatorEntry;
	}

	/**
	 * Removes the cache replicator entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param cacheReplicatorEntryId the primary key of the cache replicator entry
	 * @return the cache replicator entry that was removed
	 * @throws NoSuchCacheReplicatorEntryException if a cache replicator entry with the primary key could not be found
	 */
	@Override
	public CacheReplicatorEntry remove(long cacheReplicatorEntryId)
		throws NoSuchCacheReplicatorEntryException {

		return remove((Serializable)cacheReplicatorEntryId);
	}

	@Override
	protected CacheReplicatorEntry removeImpl(
		CacheReplicatorEntry cacheReplicatorEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cacheReplicatorEntry)) {
				cacheReplicatorEntry = (CacheReplicatorEntry)session.get(
					CacheReplicatorEntryImpl.class,
					cacheReplicatorEntry.getPrimaryKeyObj());
			}

			if (cacheReplicatorEntry != null) {
				session.delete(cacheReplicatorEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cacheReplicatorEntry != null) {
			clearCache(cacheReplicatorEntry);
		}

		return cacheReplicatorEntry;
	}

	@Override
	public CacheReplicatorEntry updateImpl(
		CacheReplicatorEntry cacheReplicatorEntry) {

		boolean isNew = cacheReplicatorEntry.isNew();

		if (!(cacheReplicatorEntry instanceof CacheReplicatorEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cacheReplicatorEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cacheReplicatorEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cacheReplicatorEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CacheReplicatorEntry implementation " +
					cacheReplicatorEntry.getClass());
		}

		CacheReplicatorEntryModelImpl cacheReplicatorEntryModelImpl =
			(CacheReplicatorEntryModelImpl)cacheReplicatorEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(cacheReplicatorEntry);
			}
			else {
				cacheReplicatorEntry = (CacheReplicatorEntry)session.merge(
					cacheReplicatorEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cacheReplicatorEntry, false);

		if (isNew) {
			cacheReplicatorEntry.setNew(false);
		}

		cacheReplicatorEntry.resetOriginalValues();

		return cacheReplicatorEntry;
	}

	/**
	 * Returns the cache replicator entry with the primary key or throws a <code>NoSuchCacheReplicatorEntryException</code> if it could not be found.
	 *
	 * @param cacheReplicatorEntryId the primary key of the cache replicator entry
	 * @return the cache replicator entry
	 * @throws NoSuchCacheReplicatorEntryException if a cache replicator entry with the primary key could not be found
	 */
	@Override
	public CacheReplicatorEntry findByPrimaryKey(long cacheReplicatorEntryId)
		throws NoSuchCacheReplicatorEntryException {

		return findByPrimaryKey((Serializable)cacheReplicatorEntryId);
	}

	/**
	 * Returns the cache replicator entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param cacheReplicatorEntryId the primary key of the cache replicator entry
	 * @return the cache replicator entry, or <code>null</code> if a cache replicator entry with the primary key could not be found
	 */
	@Override
	public CacheReplicatorEntry fetchByPrimaryKey(long cacheReplicatorEntryId) {
		return fetchByPrimaryKey((Serializable)cacheReplicatorEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "cacheReplicatorEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CACHEREPLICATORENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CacheReplicatorEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cache replicator entry persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_CACHEREPLICATORENTRY_WHERE,
				_SQL_COUNT_CACHEREPLICATORENTRY_WHERE,
				CacheReplicatorEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cacheReplicatorEntry.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					CacheReplicatorEntry::getCompanyId));

		_uniquePersistenceFinderByName = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 0,
				1, false, convertNullFunction(CacheReplicatorEntry::getName)),
			_SQL_SELECT_CACHEREPLICATORENTRY_WHERE, "",
			new FinderColumn<>(
				"cacheReplicatorEntry.", "name", FinderColumn.Type.STRING, "=",
				true, true, CacheReplicatorEntry::getName));

		CacheReplicatorEntryUtil.setPersistence(this);
	}

	public void destroy() {
		CacheReplicatorEntryUtil.setPersistence(null);

		entityCache.removeCache(CacheReplicatorEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CacheReplicatorEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CACHEREPLICATORENTRY =
		"SELECT cacheReplicatorEntry FROM CacheReplicatorEntry cacheReplicatorEntry";

	private static final String _SQL_SELECT_CACHEREPLICATORENTRY_WHERE =
		"SELECT cacheReplicatorEntry FROM CacheReplicatorEntry cacheReplicatorEntry WHERE ";

	private static final String _SQL_COUNT_CACHEREPLICATORENTRY_WHERE =
		"SELECT COUNT(cacheReplicatorEntry) FROM CacheReplicatorEntry cacheReplicatorEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1084246024