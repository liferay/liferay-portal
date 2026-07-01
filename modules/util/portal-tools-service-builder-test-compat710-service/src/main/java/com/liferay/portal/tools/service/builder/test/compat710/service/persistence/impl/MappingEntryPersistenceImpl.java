/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat710.service.persistence.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat710.exception.NoSuchMappingEntryException;
import com.liferay.portal.tools.service.builder.test.compat710.model.MappingEntry;
import com.liferay.portal.tools.service.builder.test.compat710.model.impl.MappingEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat710.model.impl.MappingEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat710.service.persistence.BasicEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat710.service.persistence.MappingEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat710.service.persistence.MappingEntryUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the mapping entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class MappingEntryPersistenceImpl
	extends BasePersistenceImpl<MappingEntry>
	implements MappingEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MappingEntryUtil</code> to access the mapping entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MappingEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public MappingEntryPersistenceImpl() {
		setModelClass(MappingEntry.class);
	}

	/**
	 * Caches the mapping entry in the entity cache if it is enabled.
	 *
	 * @param mappingEntry the mapping entry
	 */
	@Override
	public void cacheResult(MappingEntry mappingEntry) {
		entityCache.putResult(
			MappingEntryModelImpl.ENTITY_CACHE_ENABLED, MappingEntryImpl.class,
			mappingEntry.getPrimaryKey(), mappingEntry);

		mappingEntry.resetOriginalValues();
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the mapping entries in the entity cache if it is enabled.
	 *
	 * @param mappingEntries the mapping entries
	 */
	@Override
	public void cacheResult(List<MappingEntry> mappingEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (mappingEntries.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (MappingEntry mappingEntry : mappingEntries) {
			if (entityCache.getResult(
					MappingEntryModelImpl.ENTITY_CACHE_ENABLED,
					MappingEntryImpl.class, mappingEntry.getPrimaryKey()) ==
						null) {

				cacheResult(mappingEntry);
			}
			else {
				mappingEntry.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all mapping entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(MappingEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the mapping entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(MappingEntry mappingEntry) {
		entityCache.removeResult(
			MappingEntryModelImpl.ENTITY_CACHE_ENABLED, MappingEntryImpl.class,
			mappingEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<MappingEntry> mappingEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (MappingEntry mappingEntry : mappingEntries) {
			entityCache.removeResult(
				MappingEntryModelImpl.ENTITY_CACHE_ENABLED,
				MappingEntryImpl.class, mappingEntry.getPrimaryKey());
		}
	}

	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				MappingEntryModelImpl.ENTITY_CACHE_ENABLED,
				MappingEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new mapping entry with the primary key. Does not add the mapping entry to the database.
	 *
	 * @param mappingEntryId the primary key for the new mapping entry
	 * @return the new mapping entry
	 */
	@Override
	public MappingEntry create(long mappingEntryId) {
		MappingEntry mappingEntry = new MappingEntryImpl();

		mappingEntry.setNew(true);
		mappingEntry.setPrimaryKey(mappingEntryId);

		mappingEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return mappingEntry;
	}

	/**
	 * Removes the mapping entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param mappingEntryId the primary key of the mapping entry
	 * @return the mapping entry that was removed
	 * @throws NoSuchMappingEntryException if a mapping entry with the primary key could not be found
	 */
	@Override
	public MappingEntry remove(long mappingEntryId)
		throws NoSuchMappingEntryException {

		return remove((Serializable)mappingEntryId);
	}

	/**
	 * Removes the mapping entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the mapping entry
	 * @return the mapping entry that was removed
	 * @throws NoSuchMappingEntryException if a mapping entry with the primary key could not be found
	 */
	@Override
	public MappingEntry remove(Serializable primaryKey)
		throws NoSuchMappingEntryException {

		Session session = null;

		try {
			session = openSession();

			MappingEntry mappingEntry = (MappingEntry)session.get(
				MappingEntryImpl.class, primaryKey);

			if (mappingEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchMappingEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(mappingEntry);
		}
		catch (NoSuchMappingEntryException noSuchEntityException) {
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
	protected MappingEntry removeImpl(MappingEntry mappingEntry) {
		mappingEntryToBasicEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			mappingEntry.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(mappingEntry)) {
				mappingEntry = (MappingEntry)session.get(
					MappingEntryImpl.class, mappingEntry.getPrimaryKeyObj());
			}

			if (mappingEntry != null) {
				session.delete(mappingEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (mappingEntry != null) {
			clearCache(mappingEntry);
		}

		return mappingEntry;
	}

	@Override
	public MappingEntry updateImpl(MappingEntry mappingEntry) {
		boolean isNew = mappingEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(mappingEntry);

				mappingEntry.setNew(false);
			}
			else {
				mappingEntry = (MappingEntry)session.merge(mappingEntry);
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
			MappingEntryModelImpl.ENTITY_CACHE_ENABLED, MappingEntryImpl.class,
			mappingEntry.getPrimaryKey(), mappingEntry, false);

		mappingEntry.resetOriginalValues();

		return mappingEntry;
	}

	/**
	 * Returns the mapping entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the mapping entry
	 * @return the mapping entry
	 * @throws NoSuchMappingEntryException if a mapping entry with the primary key could not be found
	 */
	@Override
	public MappingEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchMappingEntryException {

		MappingEntry mappingEntry = fetchByPrimaryKey(primaryKey);

		if (mappingEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchMappingEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return mappingEntry;
	}

	/**
	 * Returns the mapping entry with the primary key or throws a <code>NoSuchMappingEntryException</code> if it could not be found.
	 *
	 * @param mappingEntryId the primary key of the mapping entry
	 * @return the mapping entry
	 * @throws NoSuchMappingEntryException if a mapping entry with the primary key could not be found
	 */
	@Override
	public MappingEntry findByPrimaryKey(long mappingEntryId)
		throws NoSuchMappingEntryException {

		return findByPrimaryKey((Serializable)mappingEntryId);
	}

	/**
	 * Returns the mapping entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the mapping entry
	 * @return the mapping entry, or <code>null</code> if a mapping entry with the primary key could not be found
	 */
	@Override
	public MappingEntry fetchByPrimaryKey(Serializable primaryKey) {
		Serializable serializable = entityCache.getResult(
			MappingEntryModelImpl.ENTITY_CACHE_ENABLED, MappingEntryImpl.class,
			primaryKey);

		if (serializable == nullModel) {
			return null;
		}

		MappingEntry mappingEntry = (MappingEntry)serializable;

		if (mappingEntry == null) {
			Session session = null;

			try {
				session = openSession();

				mappingEntry = (MappingEntry)session.get(
					MappingEntryImpl.class, primaryKey);

				if (mappingEntry != null) {
					cacheResult(mappingEntry);
				}
				else {
					entityCache.putResult(
						MappingEntryModelImpl.ENTITY_CACHE_ENABLED,
						MappingEntryImpl.class, primaryKey, nullModel);
				}
			}
			catch (Exception exception) {
				entityCache.removeResult(
					MappingEntryModelImpl.ENTITY_CACHE_ENABLED,
					MappingEntryImpl.class, primaryKey);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return mappingEntry;
	}

	/**
	 * Returns the mapping entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param mappingEntryId the primary key of the mapping entry
	 * @return the mapping entry, or <code>null</code> if a mapping entry with the primary key could not be found
	 */
	@Override
	public MappingEntry fetchByPrimaryKey(long mappingEntryId) {
		return fetchByPrimaryKey((Serializable)mappingEntryId);
	}

	@Override
	public Map<Serializable, MappingEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, MappingEntry> map =
			new HashMap<Serializable, MappingEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			MappingEntry mappingEntry = fetchByPrimaryKey(primaryKey);

			if (mappingEntry != null) {
				map.put(primaryKey, mappingEntry);
			}

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

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			Serializable serializable = entityCache.getResult(
				MappingEntryModelImpl.ENTITY_CACHE_ENABLED,
				MappingEntryImpl.class, primaryKey);

			if (serializable != nullModel) {
				if (serializable == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<Serializable>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, (MappingEntry)serializable);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler sb = new StringBundler(
			(uncachedPrimaryKeys.size() * 2) + 1);

		sb.append(_SQL_SELECT_MAPPINGENTRY_WHERE_PKS_IN);

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

			for (MappingEntry mappingEntry : (List<MappingEntry>)query.list()) {
				map.put(mappingEntry.getPrimaryKeyObj(), mappingEntry);

				cacheResult(mappingEntry);

				uncachedPrimaryKeys.remove(mappingEntry.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(
					MappingEntryModelImpl.ENTITY_CACHE_ENABLED,
					MappingEntryImpl.class, primaryKey, nullModel);
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
	 * Returns all the mapping entries.
	 *
	 * @return the mapping entries
	 */
	@Override
	public List<MappingEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the mapping entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MappingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of mapping entries
	 * @param end the upper bound of the range of mapping entries (not inclusive)
	 * @return the range of mapping entries
	 */
	@Override
	public List<MappingEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the mapping entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MappingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of mapping entries
	 * @param end the upper bound of the range of mapping entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of mapping entries
	 */
	@Override
	public List<MappingEntry> findAll(
		int start, int end, OrderByComparator<MappingEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the mapping entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MappingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of mapping entries
	 * @param end the upper bound of the range of mapping entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of mapping entries
	 */
	@Override
	public List<MappingEntry> findAll(
		int start, int end, OrderByComparator<MappingEntry> orderByComparator,
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

		List<MappingEntry> list = null;

		if (useFinderCache) {
			list = (List<MappingEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_MAPPINGENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_MAPPINGENTRY;

				sql = sql.concat(MappingEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<MappingEntry>)QueryUtil.list(
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
	 * Removes all the mapping entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (MappingEntry mappingEntry : findAll()) {
			remove(mappingEntry);
		}
	}

	/**
	 * Returns the number of mapping entries.
	 *
	 * @return the number of mapping entries
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
					"SELECT COUNT(mappingEntry) FROM MappingEntry mappingEntry");

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

	/**
	 * Returns the primaryKeys of basic entries associated with the mapping entry.
	 *
	 * @param pk the primary key of the mapping entry
	 * @return long[] of the primaryKeys of basic entries associated with the mapping entry
	 */
	@Override
	public long[] getBasicEntryPrimaryKeys(long pk) {
		long[] pks = mappingEntryToBasicEntryTableMapper.getRightPrimaryKeys(
			pk);

		return pks.clone();
	}

	/**
	 * Returns all the basic entries associated with the mapping entry.
	 *
	 * @param pk the primary key of the mapping entry
	 * @return the basic entries associated with the mapping entry
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.compat710.model.
			BasicEntry> getBasicEntries(long pk) {

		return getBasicEntries(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the basic entries associated with the mapping entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MappingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the mapping entry
	 * @param start the lower bound of the range of mapping entries
	 * @param end the upper bound of the range of mapping entries (not inclusive)
	 * @return the range of basic entries associated with the mapping entry
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.compat710.model.
			BasicEntry> getBasicEntries(long pk, int start, int end) {

		return getBasicEntries(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the basic entries associated with the mapping entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MappingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the mapping entry
	 * @param start the lower bound of the range of mapping entries
	 * @param end the upper bound of the range of mapping entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of basic entries associated with the mapping entry
	 */
	@Override
	public List
		<com.liferay.portal.tools.service.builder.test.compat710.model.
			BasicEntry> getBasicEntries(
				long pk, int start, int end,
				OrderByComparator
					<com.liferay.portal.tools.service.builder.test.compat710.
						model.BasicEntry> orderByComparator) {

		return mappingEntryToBasicEntryTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of basic entries associated with the mapping entry.
	 *
	 * @param pk the primary key of the mapping entry
	 * @return the number of basic entries associated with the mapping entry
	 */
	@Override
	public int getBasicEntriesSize(long pk) {
		long[] pks = mappingEntryToBasicEntryTableMapper.getRightPrimaryKeys(
			pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the basic entry is associated with the mapping entry.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPK the primary key of the basic entry
	 * @return <code>true</code> if the basic entry is associated with the mapping entry; <code>false</code> otherwise
	 */
	@Override
	public boolean containsBasicEntry(long pk, long basicEntryPK) {
		return mappingEntryToBasicEntryTableMapper.containsTableMapping(
			pk, basicEntryPK);
	}

	/**
	 * Returns <code>true</code> if the mapping entry has any basic entries associated with it.
	 *
	 * @param pk the primary key of the mapping entry to check for associations with basic entries
	 * @return <code>true</code> if the mapping entry has any basic entries associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsBasicEntries(long pk) {
		if (getBasicEntriesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the mapping entry and the basic entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPK the primary key of the basic entry
	 */
	@Override
	public void addBasicEntry(long pk, long basicEntryPK) {
		MappingEntry mappingEntry = fetchByPrimaryKey(pk);

		if (mappingEntry == null) {
			mappingEntryToBasicEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, basicEntryPK);
		}
		else {
			mappingEntryToBasicEntryTableMapper.addTableMapping(
				mappingEntry.getCompanyId(), pk, basicEntryPK);
		}
	}

	/**
	 * Adds an association between the mapping entry and the basic entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntry the basic entry
	 */
	@Override
	public void addBasicEntry(
		long pk,
		com.liferay.portal.tools.service.builder.test.compat710.model.BasicEntry
			basicEntry) {

		MappingEntry mappingEntry = fetchByPrimaryKey(pk);

		if (mappingEntry == null) {
			mappingEntryToBasicEntryTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				basicEntry.getPrimaryKey());
		}
		else {
			mappingEntryToBasicEntryTableMapper.addTableMapping(
				mappingEntry.getCompanyId(), pk, basicEntry.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the mapping entry and the basic entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPKs the primary keys of the basic entries
	 */
	@Override
	public void addBasicEntries(long pk, long[] basicEntryPKs) {
		long companyId = 0;

		MappingEntry mappingEntry = fetchByPrimaryKey(pk);

		if (mappingEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = mappingEntry.getCompanyId();
		}

		mappingEntryToBasicEntryTableMapper.addTableMappings(
			companyId, pk, basicEntryPKs);
	}

	/**
	 * Adds an association between the mapping entry and the basic entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntries the basic entries
	 */
	@Override
	public void addBasicEntries(
		long pk,
		List
			<com.liferay.portal.tools.service.builder.test.compat710.model.
				BasicEntry> basicEntries) {

		addBasicEntries(
			pk,
			ListUtil.toLongArray(
				basicEntries,
				com.liferay.portal.tools.service.builder.test.compat710.model.
					BasicEntry.BASIC_ENTRY_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the mapping entry and its basic entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry to clear the associated basic entries from
	 */
	@Override
	public void clearBasicEntries(long pk) {
		mappingEntryToBasicEntryTableMapper.deleteLeftPrimaryKeyTableMappings(
			pk);
	}

	/**
	 * Removes the association between the mapping entry and the basic entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPK the primary key of the basic entry
	 */
	@Override
	public void removeBasicEntry(long pk, long basicEntryPK) {
		mappingEntryToBasicEntryTableMapper.deleteTableMapping(
			pk, basicEntryPK);
	}

	/**
	 * Removes the association between the mapping entry and the basic entry. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntry the basic entry
	 */
	@Override
	public void removeBasicEntry(
		long pk,
		com.liferay.portal.tools.service.builder.test.compat710.model.BasicEntry
			basicEntry) {

		mappingEntryToBasicEntryTableMapper.deleteTableMapping(
			pk, basicEntry.getPrimaryKey());
	}

	/**
	 * Removes the association between the mapping entry and the basic entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPKs the primary keys of the basic entries
	 */
	@Override
	public void removeBasicEntries(long pk, long[] basicEntryPKs) {
		mappingEntryToBasicEntryTableMapper.deleteTableMappings(
			pk, basicEntryPKs);
	}

	/**
	 * Removes the association between the mapping entry and the basic entries. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntries the basic entries
	 */
	@Override
	public void removeBasicEntries(
		long pk,
		List
			<com.liferay.portal.tools.service.builder.test.compat710.model.
				BasicEntry> basicEntries) {

		removeBasicEntries(
			pk,
			ListUtil.toLongArray(
				basicEntries,
				com.liferay.portal.tools.service.builder.test.compat710.model.
					BasicEntry.BASIC_ENTRY_ID_ACCESSOR));
	}

	/**
	 * Sets the basic entries associated with the mapping entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntryPKs the primary keys of the basic entries to be associated with the mapping entry
	 */
	@Override
	public void setBasicEntries(long pk, long[] basicEntryPKs) {
		Set<Long> newBasicEntryPKsSet = SetUtil.fromArray(basicEntryPKs);
		Set<Long> oldBasicEntryPKsSet = SetUtil.fromArray(
			mappingEntryToBasicEntryTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeBasicEntryPKsSet = new HashSet<Long>(
			oldBasicEntryPKsSet);

		removeBasicEntryPKsSet.removeAll(newBasicEntryPKsSet);

		mappingEntryToBasicEntryTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeBasicEntryPKsSet));

		newBasicEntryPKsSet.removeAll(oldBasicEntryPKsSet);

		long companyId = 0;

		MappingEntry mappingEntry = fetchByPrimaryKey(pk);

		if (mappingEntry == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = mappingEntry.getCompanyId();
		}

		mappingEntryToBasicEntryTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newBasicEntryPKsSet));
	}

	/**
	 * Sets the basic entries associated with the mapping entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the mapping entry
	 * @param basicEntries the basic entries to be associated with the mapping entry
	 */
	@Override
	public void setBasicEntries(
		long pk,
		List
			<com.liferay.portal.tools.service.builder.test.compat710.model.
				BasicEntry> basicEntries) {

		try {
			long[] basicEntryPKs = new long[basicEntries.size()];

			for (int i = 0; i < basicEntries.size(); i++) {
				com.liferay.portal.tools.service.builder.test.compat710.model.
					BasicEntry basicEntry = basicEntries.get(i);

				basicEntryPKs[i] = basicEntry.getPrimaryKey();
			}

			setBasicEntries(pk, basicEntryPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return MappingEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the mapping entry persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		mappingEntryToBasicEntryTableMapper = TableMapperFactory.getTableMapper(
			"MappingEntries_BasicEntries", "companyId", "mappingEntryId",
			"basicEntryId", this, basicEntryPersistence);

		_finderPathWithPaginationFindAll = new FinderPath(
			MappingEntryModelImpl.ENTITY_CACHE_ENABLED,
			MappingEntryModelImpl.FINDER_CACHE_ENABLED, MappingEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			MappingEntryModelImpl.ENTITY_CACHE_ENABLED,
			MappingEntryModelImpl.FINDER_CACHE_ENABLED, MappingEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0]);

		_finderPathCountAll = new FinderPath(
			MappingEntryModelImpl.ENTITY_CACHE_ENABLED,
			MappingEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		MappingEntryUtil.setPersistence(this);
	}

	public void destroy() {
		MappingEntryUtil.setPersistence(null);

		entityCache.removeCache(MappingEntryImpl.class.getName());

		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		TableMapperFactory.removeTableMapper("MappingEntries_BasicEntries");
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	@BeanReference(type = BasicEntryPersistence.class)
	protected BasicEntryPersistence basicEntryPersistence;

	protected TableMapper
		<MappingEntry,
		 com.liferay.portal.tools.service.builder.test.compat710.model.
			 BasicEntry> mappingEntryToBasicEntryTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		MappingEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MAPPINGENTRY =
		"SELECT mappingEntry FROM MappingEntry mappingEntry";

	private static final String _SQL_SELECT_MAPPINGENTRY_WHERE_PKS_IN =
		"SELECT mappingEntry FROM MappingEntry mappingEntry WHERE mappingEntryId IN (";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No MappingEntry exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		MappingEntryPersistenceImpl.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:998423975