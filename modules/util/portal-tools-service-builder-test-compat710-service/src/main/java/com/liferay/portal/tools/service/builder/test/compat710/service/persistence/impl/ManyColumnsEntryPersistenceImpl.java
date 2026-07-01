/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat710.service.persistence.impl;

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
import com.liferay.portal.tools.service.builder.test.compat710.exception.NoSuchManyColumnsEntryException;
import com.liferay.portal.tools.service.builder.test.compat710.model.ManyColumnsEntry;
import com.liferay.portal.tools.service.builder.test.compat710.model.impl.ManyColumnsEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat710.model.impl.ManyColumnsEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat710.service.persistence.ManyColumnsEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat710.service.persistence.ManyColumnsEntryUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the many columns entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ManyColumnsEntryPersistenceImpl
	extends BasePersistenceImpl<ManyColumnsEntry>
	implements ManyColumnsEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ManyColumnsEntryUtil</code> to access the many columns entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ManyColumnsEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public ManyColumnsEntryPersistenceImpl() {
		setModelClass(ManyColumnsEntry.class);
	}

	/**
	 * Caches the many columns entry in the entity cache if it is enabled.
	 *
	 * @param manyColumnsEntry the many columns entry
	 */
	@Override
	public void cacheResult(ManyColumnsEntry manyColumnsEntry) {
		entityCache.putResult(
			ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
			ManyColumnsEntryImpl.class, manyColumnsEntry.getPrimaryKey(),
			manyColumnsEntry);

		manyColumnsEntry.resetOriginalValues();
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the many columns entries in the entity cache if it is enabled.
	 *
	 * @param manyColumnsEntries the many columns entries
	 */
	@Override
	public void cacheResult(List<ManyColumnsEntry> manyColumnsEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (manyColumnsEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ManyColumnsEntry manyColumnsEntry : manyColumnsEntries) {
			if (entityCache.getResult(
					ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
					ManyColumnsEntryImpl.class,
					manyColumnsEntry.getPrimaryKey()) == null) {

				cacheResult(manyColumnsEntry);
			}
			else {
				manyColumnsEntry.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all many columns entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ManyColumnsEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the many columns entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ManyColumnsEntry manyColumnsEntry) {
		entityCache.removeResult(
			ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
			ManyColumnsEntryImpl.class, manyColumnsEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<ManyColumnsEntry> manyColumnsEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (ManyColumnsEntry manyColumnsEntry : manyColumnsEntries) {
			entityCache.removeResult(
				ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
				ManyColumnsEntryImpl.class, manyColumnsEntry.getPrimaryKey());
		}
	}

	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
				ManyColumnsEntryImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new many columns entry with the primary key. Does not add the many columns entry to the database.
	 *
	 * @param manyColumnsEntryId the primary key for the new many columns entry
	 * @return the new many columns entry
	 */
	@Override
	public ManyColumnsEntry create(long manyColumnsEntryId) {
		ManyColumnsEntry manyColumnsEntry = new ManyColumnsEntryImpl();

		manyColumnsEntry.setNew(true);
		manyColumnsEntry.setPrimaryKey(manyColumnsEntryId);

		return manyColumnsEntry;
	}

	/**
	 * Removes the many columns entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param manyColumnsEntryId the primary key of the many columns entry
	 * @return the many columns entry that was removed
	 * @throws NoSuchManyColumnsEntryException if a many columns entry with the primary key could not be found
	 */
	@Override
	public ManyColumnsEntry remove(long manyColumnsEntryId)
		throws NoSuchManyColumnsEntryException {

		return remove((Serializable)manyColumnsEntryId);
	}

	/**
	 * Removes the many columns entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the many columns entry
	 * @return the many columns entry that was removed
	 * @throws NoSuchManyColumnsEntryException if a many columns entry with the primary key could not be found
	 */
	@Override
	public ManyColumnsEntry remove(Serializable primaryKey)
		throws NoSuchManyColumnsEntryException {

		Session session = null;

		try {
			session = openSession();

			ManyColumnsEntry manyColumnsEntry = (ManyColumnsEntry)session.get(
				ManyColumnsEntryImpl.class, primaryKey);

			if (manyColumnsEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchManyColumnsEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(manyColumnsEntry);
		}
		catch (NoSuchManyColumnsEntryException noSuchEntityException) {
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
	protected ManyColumnsEntry removeImpl(ManyColumnsEntry manyColumnsEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(manyColumnsEntry)) {
				manyColumnsEntry = (ManyColumnsEntry)session.get(
					ManyColumnsEntryImpl.class,
					manyColumnsEntry.getPrimaryKeyObj());
			}

			if (manyColumnsEntry != null) {
				session.delete(manyColumnsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (manyColumnsEntry != null) {
			clearCache(manyColumnsEntry);
		}

		return manyColumnsEntry;
	}

	@Override
	public ManyColumnsEntry updateImpl(ManyColumnsEntry manyColumnsEntry) {
		boolean isNew = manyColumnsEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(manyColumnsEntry);

				manyColumnsEntry.setNew(false);
			}
			else {
				manyColumnsEntry = (ManyColumnsEntry)session.merge(
					manyColumnsEntry);
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
			ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
			ManyColumnsEntryImpl.class, manyColumnsEntry.getPrimaryKey(),
			manyColumnsEntry, false);

		manyColumnsEntry.resetOriginalValues();

		return manyColumnsEntry;
	}

	/**
	 * Returns the many columns entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the many columns entry
	 * @return the many columns entry
	 * @throws NoSuchManyColumnsEntryException if a many columns entry with the primary key could not be found
	 */
	@Override
	public ManyColumnsEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchManyColumnsEntryException {

		ManyColumnsEntry manyColumnsEntry = fetchByPrimaryKey(primaryKey);

		if (manyColumnsEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchManyColumnsEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return manyColumnsEntry;
	}

	/**
	 * Returns the many columns entry with the primary key or throws a <code>NoSuchManyColumnsEntryException</code> if it could not be found.
	 *
	 * @param manyColumnsEntryId the primary key of the many columns entry
	 * @return the many columns entry
	 * @throws NoSuchManyColumnsEntryException if a many columns entry with the primary key could not be found
	 */
	@Override
	public ManyColumnsEntry findByPrimaryKey(long manyColumnsEntryId)
		throws NoSuchManyColumnsEntryException {

		return findByPrimaryKey((Serializable)manyColumnsEntryId);
	}

	/**
	 * Returns the many columns entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the many columns entry
	 * @return the many columns entry, or <code>null</code> if a many columns entry with the primary key could not be found
	 */
	@Override
	public ManyColumnsEntry fetchByPrimaryKey(Serializable primaryKey) {
		Serializable serializable = entityCache.getResult(
			ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
			ManyColumnsEntryImpl.class, primaryKey);

		if (serializable == nullModel) {
			return null;
		}

		ManyColumnsEntry manyColumnsEntry = (ManyColumnsEntry)serializable;

		if (manyColumnsEntry == null) {
			Session session = null;

			try {
				session = openSession();

				manyColumnsEntry = (ManyColumnsEntry)session.get(
					ManyColumnsEntryImpl.class, primaryKey);

				if (manyColumnsEntry != null) {
					cacheResult(manyColumnsEntry);
				}
				else {
					entityCache.putResult(
						ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
						ManyColumnsEntryImpl.class, primaryKey, nullModel);
				}
			}
			catch (Exception exception) {
				entityCache.removeResult(
					ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
					ManyColumnsEntryImpl.class, primaryKey);

				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return manyColumnsEntry;
	}

	/**
	 * Returns the many columns entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param manyColumnsEntryId the primary key of the many columns entry
	 * @return the many columns entry, or <code>null</code> if a many columns entry with the primary key could not be found
	 */
	@Override
	public ManyColumnsEntry fetchByPrimaryKey(long manyColumnsEntryId) {
		return fetchByPrimaryKey((Serializable)manyColumnsEntryId);
	}

	@Override
	public Map<Serializable, ManyColumnsEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, ManyColumnsEntry> map =
			new HashMap<Serializable, ManyColumnsEntry>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			ManyColumnsEntry manyColumnsEntry = fetchByPrimaryKey(primaryKey);

			if (manyColumnsEntry != null) {
				map.put(primaryKey, manyColumnsEntry);
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
				ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
				ManyColumnsEntryImpl.class, primaryKey);

			if (serializable != nullModel) {
				if (serializable == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<Serializable>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, (ManyColumnsEntry)serializable);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler sb = new StringBundler(
			(uncachedPrimaryKeys.size() * 2) + 1);

		sb.append(_SQL_SELECT_MANYCOLUMNSENTRY_WHERE_PKS_IN);

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

			for (ManyColumnsEntry manyColumnsEntry :
					(List<ManyColumnsEntry>)query.list()) {

				map.put(manyColumnsEntry.getPrimaryKeyObj(), manyColumnsEntry);

				cacheResult(manyColumnsEntry);

				uncachedPrimaryKeys.remove(manyColumnsEntry.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(
					ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
					ManyColumnsEntryImpl.class, primaryKey, nullModel);
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
	 * Returns all the many columns entries.
	 *
	 * @return the many columns entries
	 */
	@Override
	public List<ManyColumnsEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the many columns entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ManyColumnsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of many columns entries
	 * @param end the upper bound of the range of many columns entries (not inclusive)
	 * @return the range of many columns entries
	 */
	@Override
	public List<ManyColumnsEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the many columns entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ManyColumnsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of many columns entries
	 * @param end the upper bound of the range of many columns entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of many columns entries
	 */
	@Override
	public List<ManyColumnsEntry> findAll(
		int start, int end,
		OrderByComparator<ManyColumnsEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the many columns entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ManyColumnsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of many columns entries
	 * @param end the upper bound of the range of many columns entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of many columns entries
	 */
	@Override
	public List<ManyColumnsEntry> findAll(
		int start, int end,
		OrderByComparator<ManyColumnsEntry> orderByComparator,
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

		List<ManyColumnsEntry> list = null;

		if (useFinderCache) {
			list = (List<ManyColumnsEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_MANYCOLUMNSENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_MANYCOLUMNSENTRY;

				sql = sql.concat(ManyColumnsEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ManyColumnsEntry>)QueryUtil.list(
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
	 * Removes all the many columns entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ManyColumnsEntry manyColumnsEntry : findAll()) {
			remove(manyColumnsEntry);
		}
	}

	/**
	 * Returns the number of many columns entries.
	 *
	 * @return the number of many columns entries
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
					"SELECT COUNT(manyColumnsEntry) FROM ManyColumnsEntry manyColumnsEntry");

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
		return ManyColumnsEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the many columns entry persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
			ManyColumnsEntryModelImpl.FINDER_CACHE_ENABLED,
			ManyColumnsEntryImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
			ManyColumnsEntryModelImpl.FINDER_CACHE_ENABLED,
			ManyColumnsEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0]);

		_finderPathCountAll = new FinderPath(
			ManyColumnsEntryModelImpl.ENTITY_CACHE_ENABLED,
			ManyColumnsEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		ManyColumnsEntryUtil.setPersistence(this);
	}

	public void destroy() {
		ManyColumnsEntryUtil.setPersistence(null);

		entityCache.removeCache(ManyColumnsEntryImpl.class.getName());

		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		ManyColumnsEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MANYCOLUMNSENTRY =
		"SELECT manyColumnsEntry FROM ManyColumnsEntry manyColumnsEntry";

	private static final String _SQL_SELECT_MANYCOLUMNSENTRY_WHERE_PKS_IN =
		"SELECT manyColumnsEntry FROM ManyColumnsEntry manyColumnsEntry WHERE manyColumnsEntryId IN (";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ManyColumnsEntry exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		ManyColumnsEntryPersistenceImpl.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1328872101