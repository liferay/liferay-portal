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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat710.exception.NoSuchLocalizedEntryException;
import com.liferay.portal.tools.service.builder.test.compat710.model.LocalizedEntry;
import com.liferay.portal.tools.service.builder.test.compat710.model.impl.LocalizedEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat710.model.impl.LocalizedEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat710.service.persistence.LocalizedEntryLocalizationPersistence;
import com.liferay.portal.tools.service.builder.test.compat710.service.persistence.LocalizedEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat710.service.persistence.LocalizedEntryUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the localized entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LocalizedEntryPersistenceImpl
	extends BasePersistenceImpl<LocalizedEntry>
	implements LocalizedEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LocalizedEntryUtil</code> to access the localized entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LocalizedEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public LocalizedEntryPersistenceImpl() {
		setModelClass(LocalizedEntry.class);
	}

	/**
	 * Caches the localized entry in the entity cache if it is enabled.
	 *
	 * @param localizedEntry the localized entry
	 */
	@Override
	public void cacheResult(LocalizedEntry localizedEntry) {
		entityCache.putResult(
			LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
			LocalizedEntryImpl.class, localizedEntry.getPrimaryKey(),
			localizedEntry);

		localizedEntry.resetOriginalValues();
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the localized entries in the entity cache if it is enabled.
	 *
	 * @param localizedEntries the localized entries
	 */
	@Override
	public void cacheResult(List<LocalizedEntry> localizedEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (localizedEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (LocalizedEntry localizedEntry : localizedEntries) {
			if (entityCache.getResult(
					LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
					LocalizedEntryImpl.class, localizedEntry.getPrimaryKey()) ==
						null) {

				cacheResult(localizedEntry);
			}
			else {
				localizedEntry.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all localized entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(LocalizedEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the localized entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(LocalizedEntry localizedEntry) {
		entityCache.removeResult(
			LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
			LocalizedEntryImpl.class, localizedEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<LocalizedEntry> localizedEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (LocalizedEntry localizedEntry : localizedEntries) {
			entityCache.removeResult(
				LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
				LocalizedEntryImpl.class, localizedEntry.getPrimaryKey());
		}
	}

	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
				LocalizedEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new localized entry with the primary key. Does not add the localized entry to the database.
	 *
	 * @param localizedEntryId the primary key for the new localized entry
	 * @return the new localized entry
	 */
	@Override
	public LocalizedEntry create(long localizedEntryId) {
		LocalizedEntry localizedEntry = new LocalizedEntryImpl();

		localizedEntry.setNew(true);
		localizedEntry.setPrimaryKey(localizedEntryId);

		return localizedEntry;
	}

	/**
	 * Removes the localized entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param localizedEntryId the primary key of the localized entry
	 * @return the localized entry that was removed
	 * @throws NoSuchLocalizedEntryException if a localized entry with the primary key could not be found
	 */
	@Override
	public LocalizedEntry remove(long localizedEntryId)
		throws NoSuchLocalizedEntryException {

		return remove((Serializable)localizedEntryId);
	}

	/**
	 * Removes the localized entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the localized entry
	 * @return the localized entry that was removed
	 * @throws NoSuchLocalizedEntryException if a localized entry with the primary key could not be found
	 */
	@Override
	public LocalizedEntry remove(Serializable primaryKey)
		throws NoSuchLocalizedEntryException {

		Session session = null;

		try {
			session = openSession();

			LocalizedEntry localizedEntry = (LocalizedEntry)session.get(
				LocalizedEntryImpl.class, primaryKey);

			if (localizedEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchLocalizedEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(localizedEntry);
		}
		catch (NoSuchLocalizedEntryException noSuchEntityException) {
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
	protected LocalizedEntry removeImpl(LocalizedEntry localizedEntry) {
		localizedEntryLocalizationPersistence.removeByLocalizedEntryId(
			localizedEntry.getLocalizedEntryId());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(localizedEntry)) {
				localizedEntry = (LocalizedEntry)session.get(
					LocalizedEntryImpl.class,
					localizedEntry.getPrimaryKeyObj());
			}

			if (localizedEntry != null) {
				session.delete(localizedEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (localizedEntry != null) {
			clearCache(localizedEntry);
		}

		return localizedEntry;
	}

	@Override
	public LocalizedEntry updateImpl(LocalizedEntry localizedEntry) {
		boolean isNew = localizedEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(localizedEntry);

				localizedEntry.setNew(false);
			}
			else {
				localizedEntry = (LocalizedEntry)session.merge(localizedEntry);
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
			LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
			LocalizedEntryImpl.class, localizedEntry.getPrimaryKey(),
			localizedEntry, false);

		localizedEntry.resetOriginalValues();

		return localizedEntry;
	}

	/**
	 * Returns the localized entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the localized entry
	 * @return the localized entry
	 * @throws NoSuchLocalizedEntryException if a localized entry with the primary key could not be found
	 */
	@Override
	public LocalizedEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchLocalizedEntryException {

		LocalizedEntry localizedEntry = fetchByPrimaryKey(primaryKey);

		if (localizedEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchLocalizedEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return localizedEntry;
	}

	/**
	 * Returns the localized entry with the primary key or throws a <code>NoSuchLocalizedEntryException</code> if it could not be found.
	 *
	 * @param localizedEntryId the primary key of the localized entry
	 * @return the localized entry
	 * @throws NoSuchLocalizedEntryException if a localized entry with the primary key could not be found
	 */
	@Override
	public LocalizedEntry findByPrimaryKey(long localizedEntryId)
		throws NoSuchLocalizedEntryException {

		return findByPrimaryKey((Serializable)localizedEntryId);
	}

	/**
	 * Returns the localized entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the localized entry
	 * @return the localized entry, or <code>null</code> if a localized entry with the primary key could not be found
	 */
	@Override
	public LocalizedEntry fetchByPrimaryKey(Serializable primaryKey) {
		Serializable serializable = entityCache.getResult(
			LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
			LocalizedEntryImpl.class, primaryKey);

		if (serializable == nullModel) {
			return null;
		}

		LocalizedEntry localizedEntry = (LocalizedEntry)serializable;

		if (localizedEntry == null) {
			Session session = null;

			try {
				session = openSession();

				localizedEntry = (LocalizedEntry)session.get(
					LocalizedEntryImpl.class, primaryKey);

				if (localizedEntry != null) {
					cacheResult(localizedEntry);
				}
				else {
					entityCache.putResult(
						LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
						LocalizedEntryImpl.class, primaryKey, nullModel);
				}
			}
			catch (Exception exception) {
				entityCache.removeResult(
					LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
					LocalizedEntryImpl.class, primaryKey);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return localizedEntry;
	}

	/**
	 * Returns the localized entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param localizedEntryId the primary key of the localized entry
	 * @return the localized entry, or <code>null</code> if a localized entry with the primary key could not be found
	 */
	@Override
	public LocalizedEntry fetchByPrimaryKey(long localizedEntryId) {
		return fetchByPrimaryKey((Serializable)localizedEntryId);
	}

	@Override
	public Map<Serializable, LocalizedEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, LocalizedEntry> map =
			new HashMap<Serializable, LocalizedEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			LocalizedEntry localizedEntry = fetchByPrimaryKey(primaryKey);

			if (localizedEntry != null) {
				map.put(primaryKey, localizedEntry);
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
				LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
				LocalizedEntryImpl.class, primaryKey);

			if (serializable != nullModel) {
				if (serializable == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<Serializable>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, (LocalizedEntry)serializable);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler sb = new StringBundler(
			(uncachedPrimaryKeys.size() * 2) + 1);

		sb.append(_SQL_SELECT_LOCALIZEDENTRY_WHERE_PKS_IN);

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

			for (LocalizedEntry localizedEntry :
					(List<LocalizedEntry>)query.list()) {

				map.put(localizedEntry.getPrimaryKeyObj(), localizedEntry);

				cacheResult(localizedEntry);

				uncachedPrimaryKeys.remove(localizedEntry.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(
					LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
					LocalizedEntryImpl.class, primaryKey, nullModel);
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
	 * Returns all the localized entries.
	 *
	 * @return the localized entries
	 */
	@Override
	public List<LocalizedEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the localized entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LocalizedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of localized entries
	 * @param end the upper bound of the range of localized entries (not inclusive)
	 * @return the range of localized entries
	 */
	@Override
	public List<LocalizedEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the localized entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LocalizedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of localized entries
	 * @param end the upper bound of the range of localized entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of localized entries
	 */
	@Override
	public List<LocalizedEntry> findAll(
		int start, int end,
		OrderByComparator<LocalizedEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the localized entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LocalizedEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of localized entries
	 * @param end the upper bound of the range of localized entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of localized entries
	 */
	@Override
	public List<LocalizedEntry> findAll(
		int start, int end, OrderByComparator<LocalizedEntry> orderByComparator,
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

		List<LocalizedEntry> list = null;

		if (useFinderCache) {
			list = (List<LocalizedEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_LOCALIZEDENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_LOCALIZEDENTRY;

				sql = sql.concat(LocalizedEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<LocalizedEntry>)QueryUtil.list(
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
	 * Removes all the localized entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (LocalizedEntry localizedEntry : findAll()) {
			remove(localizedEntry);
		}
	}

	/**
	 * Returns the number of localized entries.
	 *
	 * @return the number of localized entries
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
					"SELECT COUNT(localizedEntry) FROM LocalizedEntry localizedEntry");

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
		return LocalizedEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the localized entry persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
			LocalizedEntryModelImpl.FINDER_CACHE_ENABLED,
			LocalizedEntryImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
			LocalizedEntryModelImpl.FINDER_CACHE_ENABLED,
			LocalizedEntryImpl.class, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findAll", new String[0]);

		_finderPathCountAll = new FinderPath(
			LocalizedEntryModelImpl.ENTITY_CACHE_ENABLED,
			LocalizedEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		LocalizedEntryUtil.setPersistence(this);
	}

	public void destroy() {
		LocalizedEntryUtil.setPersistence(null);

		entityCache.removeCache(LocalizedEntryImpl.class.getName());

		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	@BeanReference(type = LocalizedEntryLocalizationPersistence.class)
	protected LocalizedEntryLocalizationPersistence
		localizedEntryLocalizationPersistence;

	private static final String _ENTITY_ALIAS_PREFIX =
		LocalizedEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LOCALIZEDENTRY =
		"SELECT localizedEntry FROM LocalizedEntry localizedEntry";

	private static final String _SQL_SELECT_LOCALIZEDENTRY_WHERE_PKS_IN =
		"SELECT localizedEntry FROM LocalizedEntry localizedEntry WHERE localizedEntryId IN (";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No LocalizedEntry exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		LocalizedEntryPersistenceImpl.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-939677202