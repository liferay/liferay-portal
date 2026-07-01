/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat720.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.compat720.exception.NoSuchCacheDisabledEntryException;
import com.liferay.portal.tools.service.builder.test.compat720.model.CacheDisabledEntry;
import com.liferay.portal.tools.service.builder.test.compat720.model.impl.CacheDisabledEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat720.model.impl.CacheDisabledEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat720.service.persistence.CacheDisabledEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat720.service.persistence.CacheDisabledEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the cache disabled entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CacheDisabledEntryPersistenceImpl
	extends BasePersistenceImpl<CacheDisabledEntry>
	implements CacheDisabledEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CacheDisabledEntryUtil</code> to access the cache disabled entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CacheDisabledEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByName;
	private FinderPath _finderPathCountByName;

	/**
	 * Returns the cache disabled entry where name = &#63; or throws a <code>NoSuchCacheDisabledEntryException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching cache disabled entry
	 * @throws NoSuchCacheDisabledEntryException if a matching cache disabled entry could not be found
	 */
	@Override
	public CacheDisabledEntry findByName(String name)
		throws NoSuchCacheDisabledEntryException {

		CacheDisabledEntry cacheDisabledEntry = fetchByName(name);

		if (cacheDisabledEntry == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchCacheDisabledEntryException(sb.toString());
		}

		return cacheDisabledEntry;
	}

	/**
	 * Returns the cache disabled entry where name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param name the name
	 * @return the matching cache disabled entry, or <code>null</code> if a matching cache disabled entry could not be found
	 */
	@Override
	public CacheDisabledEntry fetchByName(String name) {
		return fetchByName(name, true);
	}

	/**
	 * Returns the cache disabled entry where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cache disabled entry, or <code>null</code> if a matching cache disabled entry could not be found
	 */
	@Override
	public CacheDisabledEntry fetchByName(String name, boolean useFinderCache) {
		name = Objects.toString(name, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {name};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByName, finderArgs, this);
		}

		if (result instanceof CacheDisabledEntry) {
			CacheDisabledEntry cacheDisabledEntry = (CacheDisabledEntry)result;

			if (!Objects.equals(name, cacheDisabledEntry.getName())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_CACHEDISABLEDENTRY_WHERE);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_NAME_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_NAME_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindName) {
					queryPos.add(name);
				}

				List<CacheDisabledEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByName, finderArgs, list);
					}
				}
				else {
					CacheDisabledEntry cacheDisabledEntry = list.get(0);

					result = cacheDisabledEntry;

					cacheResult(cacheDisabledEntry);
				}
			}
			catch (Exception exception) {
				if (useFinderCache) {
					finderCache.removeResult(
						_finderPathFetchByName, finderArgs);
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
			return (CacheDisabledEntry)result;
		}
	}

	/**
	 * Removes the cache disabled entry where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the cache disabled entry that was removed
	 */
	@Override
	public CacheDisabledEntry removeByName(String name)
		throws NoSuchCacheDisabledEntryException {

		CacheDisabledEntry cacheDisabledEntry = findByName(name);

		return remove(cacheDisabledEntry);
	}

	/**
	 * Returns the number of cache disabled entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching cache disabled entries
	 */
	@Override
	public int countByName(String name) {
		name = Objects.toString(name, "");

		FinderPath finderPath = _finderPathCountByName;

		Object[] finderArgs = new Object[] {name};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_CACHEDISABLEDENTRY_WHERE);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_NAME_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_NAME_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

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

	private static final String _FINDER_COLUMN_NAME_NAME_2 =
		"cacheDisabledEntry.name = ?";

	private static final String _FINDER_COLUMN_NAME_NAME_3 =
		"(cacheDisabledEntry.name IS NULL OR cacheDisabledEntry.name = '')";

	public CacheDisabledEntryPersistenceImpl() {
		setModelClass(CacheDisabledEntry.class);

		setModelImplClass(CacheDisabledEntryImpl.class);
		setModelPKClass(long.class);
		setEntityCacheEnabled(CacheDisabledEntryModelImpl.ENTITY_CACHE_ENABLED);
	}

	/**
	 * Caches the cache disabled entry in the entity cache if it is enabled.
	 *
	 * @param cacheDisabledEntry the cache disabled entry
	 */
	@Override
	public void cacheResult(CacheDisabledEntry cacheDisabledEntry) {
		entityCache.putResult(
			CacheDisabledEntryModelImpl.ENTITY_CACHE_ENABLED,
			CacheDisabledEntryImpl.class, cacheDisabledEntry.getPrimaryKey(),
			cacheDisabledEntry);

		finderCache.putResult(
			_finderPathFetchByName, new Object[] {cacheDisabledEntry.getName()},
			cacheDisabledEntry);

		cacheDisabledEntry.resetOriginalValues();
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the cache disabled entries in the entity cache if it is enabled.
	 *
	 * @param cacheDisabledEntries the cache disabled entries
	 */
	@Override
	public void cacheResult(List<CacheDisabledEntry> cacheDisabledEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (cacheDisabledEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (CacheDisabledEntry cacheDisabledEntry : cacheDisabledEntries) {
			if (entityCache.getResult(
					CacheDisabledEntryModelImpl.ENTITY_CACHE_ENABLED,
					CacheDisabledEntryImpl.class,
					cacheDisabledEntry.getPrimaryKey()) == null) {

				cacheResult(cacheDisabledEntry);
			}
			else {
				cacheDisabledEntry.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all cache disabled entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(CacheDisabledEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the cache disabled entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(CacheDisabledEntry cacheDisabledEntry) {
		entityCache.removeResult(
			CacheDisabledEntryModelImpl.ENTITY_CACHE_ENABLED,
			CacheDisabledEntryImpl.class, cacheDisabledEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache(
			(CacheDisabledEntryModelImpl)cacheDisabledEntry, true);
	}

	@Override
	public void clearCache(List<CacheDisabledEntry> cacheDisabledEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (CacheDisabledEntry cacheDisabledEntry : cacheDisabledEntries) {
			entityCache.removeResult(
				CacheDisabledEntryModelImpl.ENTITY_CACHE_ENABLED,
				CacheDisabledEntryImpl.class,
				cacheDisabledEntry.getPrimaryKey());

			clearUniqueFindersCache(
				(CacheDisabledEntryModelImpl)cacheDisabledEntry, true);
		}
	}

	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				CacheDisabledEntryModelImpl.ENTITY_CACHE_ENABLED,
				CacheDisabledEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		CacheDisabledEntryModelImpl cacheDisabledEntryModelImpl) {

		Object[] args = new Object[] {cacheDisabledEntryModelImpl.getName()};

		finderCache.putResult(
			_finderPathCountByName, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByName, args, cacheDisabledEntryModelImpl, false);
	}

	protected void clearUniqueFindersCache(
		CacheDisabledEntryModelImpl cacheDisabledEntryModelImpl,
		boolean clearCurrent) {

		if (clearCurrent) {
			Object[] args = new Object[] {
				cacheDisabledEntryModelImpl.getName()
			};

			finderCache.removeResult(_finderPathCountByName, args);
			finderCache.removeResult(_finderPathFetchByName, args);
		}

		if ((cacheDisabledEntryModelImpl.getColumnBitmask() &
			 _finderPathFetchByName.getColumnBitmask()) != 0) {

			Object[] args = new Object[] {
				cacheDisabledEntryModelImpl.getOriginalName()
			};

			finderCache.removeResult(_finderPathCountByName, args);
			finderCache.removeResult(_finderPathFetchByName, args);
		}
	}

	/**
	 * Creates a new cache disabled entry with the primary key. Does not add the cache disabled entry to the database.
	 *
	 * @param cacheDisabledEntryId the primary key for the new cache disabled entry
	 * @return the new cache disabled entry
	 */
	@Override
	public CacheDisabledEntry create(long cacheDisabledEntryId) {
		CacheDisabledEntry cacheDisabledEntry = new CacheDisabledEntryImpl();

		cacheDisabledEntry.setNew(true);
		cacheDisabledEntry.setPrimaryKey(cacheDisabledEntryId);

		return cacheDisabledEntry;
	}

	/**
	 * Removes the cache disabled entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param cacheDisabledEntryId the primary key of the cache disabled entry
	 * @return the cache disabled entry that was removed
	 * @throws NoSuchCacheDisabledEntryException if a cache disabled entry with the primary key could not be found
	 */
	@Override
	public CacheDisabledEntry remove(long cacheDisabledEntryId)
		throws NoSuchCacheDisabledEntryException {

		return remove((Serializable)cacheDisabledEntryId);
	}

	/**
	 * Removes the cache disabled entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the cache disabled entry
	 * @return the cache disabled entry that was removed
	 * @throws NoSuchCacheDisabledEntryException if a cache disabled entry with the primary key could not be found
	 */
	@Override
	public CacheDisabledEntry remove(Serializable primaryKey)
		throws NoSuchCacheDisabledEntryException {

		Session session = null;

		try {
			session = openSession();

			CacheDisabledEntry cacheDisabledEntry =
				(CacheDisabledEntry)session.get(
					CacheDisabledEntryImpl.class, primaryKey);

			if (cacheDisabledEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchCacheDisabledEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(cacheDisabledEntry);
		}
		catch (NoSuchCacheDisabledEntryException noSuchEntityException) {
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
	protected CacheDisabledEntry removeImpl(
		CacheDisabledEntry cacheDisabledEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cacheDisabledEntry)) {
				cacheDisabledEntry = (CacheDisabledEntry)session.get(
					CacheDisabledEntryImpl.class,
					cacheDisabledEntry.getPrimaryKeyObj());
			}

			if (cacheDisabledEntry != null) {
				session.delete(cacheDisabledEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cacheDisabledEntry != null) {
			clearCache(cacheDisabledEntry);
		}

		return cacheDisabledEntry;
	}

	@Override
	public CacheDisabledEntry updateImpl(
		CacheDisabledEntry cacheDisabledEntry) {

		boolean isNew = cacheDisabledEntry.isNew();

		if (!(cacheDisabledEntry instanceof CacheDisabledEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cacheDisabledEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cacheDisabledEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cacheDisabledEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CacheDisabledEntry implementation " +
					cacheDisabledEntry.getClass());
		}

		CacheDisabledEntryModelImpl cacheDisabledEntryModelImpl =
			(CacheDisabledEntryModelImpl)cacheDisabledEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(cacheDisabledEntry);

				cacheDisabledEntry.setNew(false);
			}
			else {
				cacheDisabledEntry = (CacheDisabledEntry)session.merge(
					cacheDisabledEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!CacheDisabledEntryModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else if (isNew) {
			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
		}

		entityCache.putResult(
			CacheDisabledEntryModelImpl.ENTITY_CACHE_ENABLED,
			CacheDisabledEntryImpl.class, cacheDisabledEntry.getPrimaryKey(),
			cacheDisabledEntry, false);

		clearUniqueFindersCache(cacheDisabledEntryModelImpl, false);
		cacheUniqueFindersCache(cacheDisabledEntryModelImpl);

		cacheDisabledEntry.resetOriginalValues();

		return cacheDisabledEntry;
	}

	/**
	 * Returns the cache disabled entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the cache disabled entry
	 * @return the cache disabled entry
	 * @throws NoSuchCacheDisabledEntryException if a cache disabled entry with the primary key could not be found
	 */
	@Override
	public CacheDisabledEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchCacheDisabledEntryException {

		CacheDisabledEntry cacheDisabledEntry = fetchByPrimaryKey(primaryKey);

		if (cacheDisabledEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchCacheDisabledEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return cacheDisabledEntry;
	}

	/**
	 * Returns the cache disabled entry with the primary key or throws a <code>NoSuchCacheDisabledEntryException</code> if it could not be found.
	 *
	 * @param cacheDisabledEntryId the primary key of the cache disabled entry
	 * @return the cache disabled entry
	 * @throws NoSuchCacheDisabledEntryException if a cache disabled entry with the primary key could not be found
	 */
	@Override
	public CacheDisabledEntry findByPrimaryKey(long cacheDisabledEntryId)
		throws NoSuchCacheDisabledEntryException {

		return findByPrimaryKey((Serializable)cacheDisabledEntryId);
	}

	/**
	 * Returns the cache disabled entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param cacheDisabledEntryId the primary key of the cache disabled entry
	 * @return the cache disabled entry, or <code>null</code> if a cache disabled entry with the primary key could not be found
	 */
	@Override
	public CacheDisabledEntry fetchByPrimaryKey(long cacheDisabledEntryId) {
		return fetchByPrimaryKey((Serializable)cacheDisabledEntryId);
	}

	/**
	 * Returns all the cache disabled entries.
	 *
	 * @return the cache disabled entries
	 */
	@Override
	public List<CacheDisabledEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cache disabled entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CacheDisabledEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cache disabled entries
	 * @param end the upper bound of the range of cache disabled entries (not inclusive)
	 * @return the range of cache disabled entries
	 */
	@Override
	public List<CacheDisabledEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the cache disabled entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CacheDisabledEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cache disabled entries
	 * @param end the upper bound of the range of cache disabled entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cache disabled entries
	 */
	@Override
	public List<CacheDisabledEntry> findAll(
		int start, int end,
		OrderByComparator<CacheDisabledEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cache disabled entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CacheDisabledEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cache disabled entries
	 * @param end the upper bound of the range of cache disabled entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cache disabled entries
	 */
	@Override
	public List<CacheDisabledEntry> findAll(
		int start, int end,
		OrderByComparator<CacheDisabledEntry> orderByComparator,
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

		List<CacheDisabledEntry> list = null;

		if (useFinderCache) {
			list = (List<CacheDisabledEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_CACHEDISABLEDENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_CACHEDISABLEDENTRY;

				sql = sql.concat(CacheDisabledEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<CacheDisabledEntry>)QueryUtil.list(
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
	 * Removes all the cache disabled entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (CacheDisabledEntry cacheDisabledEntry : findAll()) {
			remove(cacheDisabledEntry);
		}
	}

	/**
	 * Returns the number of cache disabled entries.
	 *
	 * @return the number of cache disabled entries
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
					"SELECT COUNT(cacheDisabledEntry) FROM CacheDisabledEntry cacheDisabledEntry");

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
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "cacheDisabledEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CACHEDISABLEDENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CacheDisabledEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cache disabled entry persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			CacheDisabledEntryModelImpl.ENTITY_CACHE_ENABLED,
			CacheDisabledEntryModelImpl.FINDER_CACHE_ENABLED,
			CacheDisabledEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			CacheDisabledEntryModelImpl.ENTITY_CACHE_ENABLED,
			CacheDisabledEntryModelImpl.FINDER_CACHE_ENABLED,
			CacheDisabledEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0]);

		_finderPathCountAll = new FinderPath(
			CacheDisabledEntryModelImpl.ENTITY_CACHE_ENABLED,
			CacheDisabledEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		_finderPathFetchByName = new FinderPath(
			CacheDisabledEntryModelImpl.ENTITY_CACHE_ENABLED,
			CacheDisabledEntryModelImpl.FINDER_CACHE_ENABLED,
			CacheDisabledEntryImpl.class, FINDER_CLASS_NAME_ENTITY,
			"fetchByName", new String[] {String.class.getName()},
			CacheDisabledEntryModelImpl.NAME_COLUMN_BITMASK);

		_finderPathCountByName = new FinderPath(
			CacheDisabledEntryModelImpl.ENTITY_CACHE_ENABLED,
			CacheDisabledEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByName",
			new String[] {String.class.getName()});

		CacheDisabledEntryUtil.setPersistence(this);
	}

	public void destroy() {
		CacheDisabledEntryUtil.setPersistence(null);

		entityCache.removeCache(CacheDisabledEntryImpl.class.getName());

		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CacheDisabledEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CACHEDISABLEDENTRY =
		"SELECT cacheDisabledEntry FROM CacheDisabledEntry cacheDisabledEntry";

	private static final String _SQL_SELECT_CACHEDISABLEDENTRY_WHERE =
		"SELECT cacheDisabledEntry FROM CacheDisabledEntry cacheDisabledEntry WHERE ";

	private static final String _SQL_COUNT_CACHEDISABLEDENTRY_WHERE =
		"SELECT COUNT(cacheDisabledEntry) FROM CacheDisabledEntry cacheDisabledEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No CacheDisabledEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CacheDisabledEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CacheDisabledEntryPersistenceImpl.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1939408729