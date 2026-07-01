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
import com.liferay.portal.tools.service.builder.test.compat720.exception.NoSuchConvertNullEntryException;
import com.liferay.portal.tools.service.builder.test.compat720.model.ConvertNullEntry;
import com.liferay.portal.tools.service.builder.test.compat720.model.impl.ConvertNullEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat720.model.impl.ConvertNullEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat720.service.persistence.ConvertNullEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat720.service.persistence.ConvertNullEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the convert null entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ConvertNullEntryPersistenceImpl
	extends BasePersistenceImpl<ConvertNullEntry>
	implements ConvertNullEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ConvertNullEntryUtil</code> to access the convert null entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ConvertNullEntryImpl.class.getName();

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
	 * Returns the convert null entry where name = &#63; or throws a <code>NoSuchConvertNullEntryException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching convert null entry
	 * @throws NoSuchConvertNullEntryException if a matching convert null entry could not be found
	 */
	@Override
	public ConvertNullEntry findByName(String name)
		throws NoSuchConvertNullEntryException {

		ConvertNullEntry convertNullEntry = fetchByName(name);

		if (convertNullEntry == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchConvertNullEntryException(sb.toString());
		}

		return convertNullEntry;
	}

	/**
	 * Returns the convert null entry where name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param name the name
	 * @return the matching convert null entry, or <code>null</code> if a matching convert null entry could not be found
	 */
	@Override
	public ConvertNullEntry fetchByName(String name) {
		return fetchByName(name, true);
	}

	/**
	 * Returns the convert null entry where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching convert null entry, or <code>null</code> if a matching convert null entry could not be found
	 */
	@Override
	public ConvertNullEntry fetchByName(String name, boolean useFinderCache) {
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

		if (result instanceof ConvertNullEntry) {
			ConvertNullEntry convertNullEntry = (ConvertNullEntry)result;

			if (!Objects.equals(name, convertNullEntry.getName())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_CONVERTNULLENTRY_WHERE);

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

				List<ConvertNullEntry> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByName, finderArgs, list);
					}
				}
				else {
					ConvertNullEntry convertNullEntry = list.get(0);

					result = convertNullEntry;

					cacheResult(convertNullEntry);
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
			return (ConvertNullEntry)result;
		}
	}

	/**
	 * Removes the convert null entry where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the convert null entry that was removed
	 */
	@Override
	public ConvertNullEntry removeByName(String name)
		throws NoSuchConvertNullEntryException {

		ConvertNullEntry convertNullEntry = findByName(name);

		return remove(convertNullEntry);
	}

	/**
	 * Returns the number of convert null entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching convert null entries
	 */
	@Override
	public int countByName(String name) {
		name = Objects.toString(name, "");

		FinderPath finderPath = _finderPathCountByName;

		Object[] finderArgs = new Object[] {name};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_CONVERTNULLENTRY_WHERE);

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
		"convertNullEntry.name = ?";

	private static final String _FINDER_COLUMN_NAME_NAME_3 =
		"(convertNullEntry.name IS NULL OR convertNullEntry.name = '')";

	public ConvertNullEntryPersistenceImpl() {
		setModelClass(ConvertNullEntry.class);

		setModelImplClass(ConvertNullEntryImpl.class);
		setModelPKClass(long.class);
		setEntityCacheEnabled(ConvertNullEntryModelImpl.ENTITY_CACHE_ENABLED);
	}

	/**
	 * Caches the convert null entry in the entity cache if it is enabled.
	 *
	 * @param convertNullEntry the convert null entry
	 */
	@Override
	public void cacheResult(ConvertNullEntry convertNullEntry) {
		entityCache.putResult(
			ConvertNullEntryModelImpl.ENTITY_CACHE_ENABLED,
			ConvertNullEntryImpl.class, convertNullEntry.getPrimaryKey(),
			convertNullEntry);

		finderCache.putResult(
			_finderPathFetchByName, new Object[] {convertNullEntry.getName()},
			convertNullEntry);

		convertNullEntry.resetOriginalValues();
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the convert null entries in the entity cache if it is enabled.
	 *
	 * @param convertNullEntries the convert null entries
	 */
	@Override
	public void cacheResult(List<ConvertNullEntry> convertNullEntries) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (convertNullEntries.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (ConvertNullEntry convertNullEntry : convertNullEntries) {
			if (entityCache.getResult(
					ConvertNullEntryModelImpl.ENTITY_CACHE_ENABLED,
					ConvertNullEntryImpl.class,
					convertNullEntry.getPrimaryKey()) == null) {

				cacheResult(convertNullEntry);
			}
			else {
				convertNullEntry.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all convert null entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ConvertNullEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the convert null entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(ConvertNullEntry convertNullEntry) {
		entityCache.removeResult(
			ConvertNullEntryModelImpl.ENTITY_CACHE_ENABLED,
			ConvertNullEntryImpl.class, convertNullEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache(
			(ConvertNullEntryModelImpl)convertNullEntry, true);
	}

	@Override
	public void clearCache(List<ConvertNullEntry> convertNullEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (ConvertNullEntry convertNullEntry : convertNullEntries) {
			entityCache.removeResult(
				ConvertNullEntryModelImpl.ENTITY_CACHE_ENABLED,
				ConvertNullEntryImpl.class, convertNullEntry.getPrimaryKey());

			clearUniqueFindersCache(
				(ConvertNullEntryModelImpl)convertNullEntry, true);
		}
	}

	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				ConvertNullEntryModelImpl.ENTITY_CACHE_ENABLED,
				ConvertNullEntryImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		ConvertNullEntryModelImpl convertNullEntryModelImpl) {

		Object[] args = new Object[] {convertNullEntryModelImpl.getName()};

		finderCache.putResult(
			_finderPathCountByName, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByName, args, convertNullEntryModelImpl, false);
	}

	protected void clearUniqueFindersCache(
		ConvertNullEntryModelImpl convertNullEntryModelImpl,
		boolean clearCurrent) {

		if (clearCurrent) {
			Object[] args = new Object[] {convertNullEntryModelImpl.getName()};

			finderCache.removeResult(_finderPathCountByName, args);
			finderCache.removeResult(_finderPathFetchByName, args);
		}

		if ((convertNullEntryModelImpl.getColumnBitmask() &
			 _finderPathFetchByName.getColumnBitmask()) != 0) {

			Object[] args = new Object[] {
				convertNullEntryModelImpl.getOriginalName()
			};

			finderCache.removeResult(_finderPathCountByName, args);
			finderCache.removeResult(_finderPathFetchByName, args);
		}
	}

	/**
	 * Creates a new convert null entry with the primary key. Does not add the convert null entry to the database.
	 *
	 * @param convertNullEntryId the primary key for the new convert null entry
	 * @return the new convert null entry
	 */
	@Override
	public ConvertNullEntry create(long convertNullEntryId) {
		ConvertNullEntry convertNullEntry = new ConvertNullEntryImpl();

		convertNullEntry.setNew(true);
		convertNullEntry.setPrimaryKey(convertNullEntryId);

		return convertNullEntry;
	}

	/**
	 * Removes the convert null entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param convertNullEntryId the primary key of the convert null entry
	 * @return the convert null entry that was removed
	 * @throws NoSuchConvertNullEntryException if a convert null entry with the primary key could not be found
	 */
	@Override
	public ConvertNullEntry remove(long convertNullEntryId)
		throws NoSuchConvertNullEntryException {

		return remove((Serializable)convertNullEntryId);
	}

	/**
	 * Removes the convert null entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the convert null entry
	 * @return the convert null entry that was removed
	 * @throws NoSuchConvertNullEntryException if a convert null entry with the primary key could not be found
	 */
	@Override
	public ConvertNullEntry remove(Serializable primaryKey)
		throws NoSuchConvertNullEntryException {

		Session session = null;

		try {
			session = openSession();

			ConvertNullEntry convertNullEntry = (ConvertNullEntry)session.get(
				ConvertNullEntryImpl.class, primaryKey);

			if (convertNullEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchConvertNullEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(convertNullEntry);
		}
		catch (NoSuchConvertNullEntryException noSuchEntityException) {
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
	protected ConvertNullEntry removeImpl(ConvertNullEntry convertNullEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(convertNullEntry)) {
				convertNullEntry = (ConvertNullEntry)session.get(
					ConvertNullEntryImpl.class,
					convertNullEntry.getPrimaryKeyObj());
			}

			if (convertNullEntry != null) {
				session.delete(convertNullEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (convertNullEntry != null) {
			clearCache(convertNullEntry);
		}

		return convertNullEntry;
	}

	@Override
	public ConvertNullEntry updateImpl(ConvertNullEntry convertNullEntry) {
		boolean isNew = convertNullEntry.isNew();

		if (!(convertNullEntry instanceof ConvertNullEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(convertNullEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					convertNullEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in convertNullEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ConvertNullEntry implementation " +
					convertNullEntry.getClass());
		}

		ConvertNullEntryModelImpl convertNullEntryModelImpl =
			(ConvertNullEntryModelImpl)convertNullEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(convertNullEntry);

				convertNullEntry.setNew(false);
			}
			else {
				convertNullEntry = (ConvertNullEntry)session.merge(
					convertNullEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!ConvertNullEntryModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else if (isNew) {
			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
		}

		entityCache.putResult(
			ConvertNullEntryModelImpl.ENTITY_CACHE_ENABLED,
			ConvertNullEntryImpl.class, convertNullEntry.getPrimaryKey(),
			convertNullEntry, false);

		clearUniqueFindersCache(convertNullEntryModelImpl, false);
		cacheUniqueFindersCache(convertNullEntryModelImpl);

		convertNullEntry.resetOriginalValues();

		return convertNullEntry;
	}

	/**
	 * Returns the convert null entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the convert null entry
	 * @return the convert null entry
	 * @throws NoSuchConvertNullEntryException if a convert null entry with the primary key could not be found
	 */
	@Override
	public ConvertNullEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchConvertNullEntryException {

		ConvertNullEntry convertNullEntry = fetchByPrimaryKey(primaryKey);

		if (convertNullEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchConvertNullEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return convertNullEntry;
	}

	/**
	 * Returns the convert null entry with the primary key or throws a <code>NoSuchConvertNullEntryException</code> if it could not be found.
	 *
	 * @param convertNullEntryId the primary key of the convert null entry
	 * @return the convert null entry
	 * @throws NoSuchConvertNullEntryException if a convert null entry with the primary key could not be found
	 */
	@Override
	public ConvertNullEntry findByPrimaryKey(long convertNullEntryId)
		throws NoSuchConvertNullEntryException {

		return findByPrimaryKey((Serializable)convertNullEntryId);
	}

	/**
	 * Returns the convert null entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param convertNullEntryId the primary key of the convert null entry
	 * @return the convert null entry, or <code>null</code> if a convert null entry with the primary key could not be found
	 */
	@Override
	public ConvertNullEntry fetchByPrimaryKey(long convertNullEntryId) {
		return fetchByPrimaryKey((Serializable)convertNullEntryId);
	}

	/**
	 * Returns all the convert null entries.
	 *
	 * @return the convert null entries
	 */
	@Override
	public List<ConvertNullEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the convert null entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ConvertNullEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of convert null entries
	 * @param end the upper bound of the range of convert null entries (not inclusive)
	 * @return the range of convert null entries
	 */
	@Override
	public List<ConvertNullEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the convert null entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ConvertNullEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of convert null entries
	 * @param end the upper bound of the range of convert null entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of convert null entries
	 */
	@Override
	public List<ConvertNullEntry> findAll(
		int start, int end,
		OrderByComparator<ConvertNullEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the convert null entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ConvertNullEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of convert null entries
	 * @param end the upper bound of the range of convert null entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of convert null entries
	 */
	@Override
	public List<ConvertNullEntry> findAll(
		int start, int end,
		OrderByComparator<ConvertNullEntry> orderByComparator,
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

		List<ConvertNullEntry> list = null;

		if (useFinderCache) {
			list = (List<ConvertNullEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_CONVERTNULLENTRY);

				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_CONVERTNULLENTRY;

				sql = sql.concat(ConvertNullEntryModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<ConvertNullEntry>)QueryUtil.list(
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
	 * Removes all the convert null entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (ConvertNullEntry convertNullEntry : findAll()) {
			remove(convertNullEntry);
		}
	}

	/**
	 * Returns the number of convert null entries.
	 *
	 * @return the number of convert null entries
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
					"SELECT COUNT(convertNullEntry) FROM ConvertNullEntry convertNullEntry");

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
		return "convertNullEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CONVERTNULLENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ConvertNullEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the convert null entry persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			ConvertNullEntryModelImpl.ENTITY_CACHE_ENABLED,
			ConvertNullEntryModelImpl.FINDER_CACHE_ENABLED,
			ConvertNullEntryImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			ConvertNullEntryModelImpl.ENTITY_CACHE_ENABLED,
			ConvertNullEntryModelImpl.FINDER_CACHE_ENABLED,
			ConvertNullEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0]);

		_finderPathCountAll = new FinderPath(
			ConvertNullEntryModelImpl.ENTITY_CACHE_ENABLED,
			ConvertNullEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		_finderPathFetchByName = new FinderPath(
			ConvertNullEntryModelImpl.ENTITY_CACHE_ENABLED,
			ConvertNullEntryModelImpl.FINDER_CACHE_ENABLED,
			ConvertNullEntryImpl.class, FINDER_CLASS_NAME_ENTITY, "fetchByName",
			new String[] {String.class.getName()},
			ConvertNullEntryModelImpl.NAME_COLUMN_BITMASK);

		_finderPathCountByName = new FinderPath(
			ConvertNullEntryModelImpl.ENTITY_CACHE_ENABLED,
			ConvertNullEntryModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByName",
			new String[] {String.class.getName()});

		ConvertNullEntryUtil.setPersistence(this);
	}

	public void destroy() {
		ConvertNullEntryUtil.setPersistence(null);

		entityCache.removeCache(ConvertNullEntryImpl.class.getName());

		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		ConvertNullEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CONVERTNULLENTRY =
		"SELECT convertNullEntry FROM ConvertNullEntry convertNullEntry";

	private static final String _SQL_SELECT_CONVERTNULLENTRY_WHERE =
		"SELECT convertNullEntry FROM ConvertNullEntry convertNullEntry WHERE ";

	private static final String _SQL_COUNT_CONVERTNULLENTRY_WHERE =
		"SELECT COUNT(convertNullEntry) FROM ConvertNullEntry convertNullEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No ConvertNullEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ConvertNullEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ConvertNullEntryPersistenceImpl.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1023668135