/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence.impl;

import com.liferay.osb.faro.exception.NoSuchFaroProjectUsageException;
import com.liferay.osb.faro.model.FaroProjectUsage;
import com.liferay.osb.faro.model.FaroProjectUsageTable;
import com.liferay.osb.faro.model.impl.FaroProjectUsageImpl;
import com.liferay.osb.faro.model.impl.FaroProjectUsageModelImpl;
import com.liferay.osb.faro.service.persistence.FaroProjectUsagePersistence;
import com.liferay.osb.faro.service.persistence.FaroProjectUsageUtil;
import com.liferay.osb.faro.service.persistence.impl.constants.OSBFaroPersistenceConstants;
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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the faro project usage service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @generated
 */
@Component(service = FaroProjectUsagePersistence.class)
public class FaroProjectUsagePersistenceImpl
	extends BasePersistenceImpl<FaroProjectUsage>
	implements FaroProjectUsagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FaroProjectUsageUtil</code> to access the faro project usage persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FaroProjectUsageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByF_U;

	/**
	 * Returns the faro project usage where faroProjectId = &#63; and usageTime = &#63; or throws a <code>NoSuchFaroProjectUsageException</code> if it could not be found.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro project usage
	 * @throws NoSuchFaroProjectUsageException if a matching faro project usage could not be found
	 */
	@Override
	public FaroProjectUsage findByF_U(long faroProjectId, long usageTime)
		throws NoSuchFaroProjectUsageException {

		FaroProjectUsage faroProjectUsage = fetchByF_U(
			faroProjectId, usageTime);

		if (faroProjectUsage == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("faroProjectId=");
			sb.append(faroProjectId);

			sb.append(", usageTime=");
			sb.append(usageTime);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchFaroProjectUsageException(sb.toString());
		}

		return faroProjectUsage;
	}

	/**
	 * Returns the faro project usage where faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro project usage, or <code>null</code> if a matching faro project usage could not be found
	 */
	@Override
	public FaroProjectUsage fetchByF_U(long faroProjectId, long usageTime) {
		return fetchByF_U(faroProjectId, usageTime, true);
	}

	/**
	 * Returns the faro project usage where faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro project usage, or <code>null</code> if a matching faro project usage could not be found
	 */
	@Override
	public FaroProjectUsage fetchByF_U(
		long faroProjectId, long usageTime, boolean useFinderCache) {

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {faroProjectId, usageTime};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByF_U, finderArgs, this);
		}

		if (result instanceof FaroProjectUsage) {
			FaroProjectUsage faroProjectUsage = (FaroProjectUsage)result;

			if ((faroProjectId != faroProjectUsage.getFaroProjectId()) ||
				(usageTime != faroProjectUsage.getUsageTime())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_FAROPROJECTUSAGE_WHERE);

			sb.append(_FINDER_COLUMN_F_U_FAROPROJECTID_2);

			sb.append(_FINDER_COLUMN_F_U_USAGETIME_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(faroProjectId);

				queryPos.add(usageTime);

				List<FaroProjectUsage> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByF_U, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {
									faroProjectId, usageTime
								};
							}

							_log.warn(
								"FaroProjectUsagePersistenceImpl.fetchByF_U(long, long, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					FaroProjectUsage faroProjectUsage = list.get(0);

					result = faroProjectUsage;

					cacheResult(faroProjectUsage);
				}
			}
			catch (Exception exception) {
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
			return (FaroProjectUsage)result;
		}
	}

	/**
	 * Removes the faro project usage where faroProjectId = &#63; and usageTime = &#63; from the database.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the faro project usage that was removed
	 */
	@Override
	public FaroProjectUsage removeByF_U(long faroProjectId, long usageTime)
		throws NoSuchFaroProjectUsageException {

		FaroProjectUsage faroProjectUsage = findByF_U(faroProjectId, usageTime);

		return remove(faroProjectUsage);
	}

	/**
	 * Returns the number of faro project usages where faroProjectId = &#63; and usageTime = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the number of matching faro project usages
	 */
	@Override
	public int countByF_U(long faroProjectId, long usageTime) {
		FaroProjectUsage faroProjectUsage = fetchByF_U(
			faroProjectId, usageTime);

		if (faroProjectUsage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_F_U_FAROPROJECTID_2 =
		"faroProjectUsage.faroProjectId = ? AND ";

	private static final String _FINDER_COLUMN_F_U_USAGETIME_2 =
		"faroProjectUsage.usageTime = ?";

	public FaroProjectUsagePersistenceImpl() {
		setModelClass(FaroProjectUsage.class);

		setModelImplClass(FaroProjectUsageImpl.class);
		setModelPKClass(long.class);

		setTable(FaroProjectUsageTable.INSTANCE);
	}

	/**
	 * Caches the faro project usage in the entity cache if it is enabled.
	 *
	 * @param faroProjectUsage the faro project usage
	 */
	@Override
	public void cacheResult(FaroProjectUsage faroProjectUsage) {
		entityCache.putResult(
			FaroProjectUsageImpl.class, faroProjectUsage.getPrimaryKey(),
			faroProjectUsage);

		finderCache.putResult(
			_finderPathFetchByF_U,
			new Object[] {
				faroProjectUsage.getFaroProjectId(),
				faroProjectUsage.getUsageTime()
			},
			faroProjectUsage);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the faro project usages in the entity cache if it is enabled.
	 *
	 * @param faroProjectUsages the faro project usages
	 */
	@Override
	public void cacheResult(List<FaroProjectUsage> faroProjectUsages) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (faroProjectUsages.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (FaroProjectUsage faroProjectUsage : faroProjectUsages) {
			if (entityCache.getResult(
					FaroProjectUsageImpl.class,
					faroProjectUsage.getPrimaryKey()) == null) {

				cacheResult(faroProjectUsage);
			}
		}
	}

	/**
	 * Clears the cache for all faro project usages.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(FaroProjectUsageImpl.class);

		finderCache.clearCache(FaroProjectUsageImpl.class);
	}

	/**
	 * Clears the cache for the faro project usage.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(FaroProjectUsage faroProjectUsage) {
		entityCache.removeResult(FaroProjectUsageImpl.class, faroProjectUsage);
	}

	@Override
	public void clearCache(List<FaroProjectUsage> faroProjectUsages) {
		for (FaroProjectUsage faroProjectUsage : faroProjectUsages) {
			entityCache.removeResult(
				FaroProjectUsageImpl.class, faroProjectUsage);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FaroProjectUsageImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(FaroProjectUsageImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		FaroProjectUsageModelImpl faroProjectUsageModelImpl) {

		Object[] args = new Object[] {
			faroProjectUsageModelImpl.getFaroProjectId(),
			faroProjectUsageModelImpl.getUsageTime()
		};

		finderCache.putResult(
			_finderPathFetchByF_U, args, faroProjectUsageModelImpl);
	}

	/**
	 * Creates a new faro project usage with the primary key. Does not add the faro project usage to the database.
	 *
	 * @param faroProjectUsageId the primary key for the new faro project usage
	 * @return the new faro project usage
	 */
	@Override
	public FaroProjectUsage create(long faroProjectUsageId) {
		FaroProjectUsage faroProjectUsage = new FaroProjectUsageImpl();

		faroProjectUsage.setNew(true);
		faroProjectUsage.setPrimaryKey(faroProjectUsageId);

		faroProjectUsage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return faroProjectUsage;
	}

	/**
	 * Removes the faro project usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroProjectUsageId the primary key of the faro project usage
	 * @return the faro project usage that was removed
	 * @throws NoSuchFaroProjectUsageException if a faro project usage with the primary key could not be found
	 */
	@Override
	public FaroProjectUsage remove(long faroProjectUsageId)
		throws NoSuchFaroProjectUsageException {

		return remove((Serializable)faroProjectUsageId);
	}

	/**
	 * Removes the faro project usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the faro project usage
	 * @return the faro project usage that was removed
	 * @throws NoSuchFaroProjectUsageException if a faro project usage with the primary key could not be found
	 */
	@Override
	public FaroProjectUsage remove(Serializable primaryKey)
		throws NoSuchFaroProjectUsageException {

		Session session = null;

		try {
			session = openSession();

			FaroProjectUsage faroProjectUsage = (FaroProjectUsage)session.get(
				FaroProjectUsageImpl.class, primaryKey);

			if (faroProjectUsage == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchFaroProjectUsageException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(faroProjectUsage);
		}
		catch (NoSuchFaroProjectUsageException noSuchEntityException) {
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
	protected FaroProjectUsage removeImpl(FaroProjectUsage faroProjectUsage) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(faroProjectUsage)) {
				faroProjectUsage = (FaroProjectUsage)session.get(
					FaroProjectUsageImpl.class,
					faroProjectUsage.getPrimaryKeyObj());
			}

			if (faroProjectUsage != null) {
				session.delete(faroProjectUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (faroProjectUsage != null) {
			clearCache(faroProjectUsage);
		}

		return faroProjectUsage;
	}

	@Override
	public FaroProjectUsage updateImpl(FaroProjectUsage faroProjectUsage) {
		boolean isNew = faroProjectUsage.isNew();

		if (!(faroProjectUsage instanceof FaroProjectUsageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(faroProjectUsage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					faroProjectUsage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in faroProjectUsage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FaroProjectUsage implementation " +
					faroProjectUsage.getClass());
		}

		FaroProjectUsageModelImpl faroProjectUsageModelImpl =
			(FaroProjectUsageModelImpl)faroProjectUsage;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(faroProjectUsage);
			}
			else {
				faroProjectUsage = (FaroProjectUsage)session.merge(
					faroProjectUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			FaroProjectUsageImpl.class, faroProjectUsageModelImpl, false, true);

		cacheUniqueFindersCache(faroProjectUsageModelImpl);

		if (isNew) {
			faroProjectUsage.setNew(false);
		}

		faroProjectUsage.resetOriginalValues();

		return faroProjectUsage;
	}

	/**
	 * Returns the faro project usage with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the faro project usage
	 * @return the faro project usage
	 * @throws NoSuchFaroProjectUsageException if a faro project usage with the primary key could not be found
	 */
	@Override
	public FaroProjectUsage findByPrimaryKey(Serializable primaryKey)
		throws NoSuchFaroProjectUsageException {

		FaroProjectUsage faroProjectUsage = fetchByPrimaryKey(primaryKey);

		if (faroProjectUsage == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchFaroProjectUsageException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return faroProjectUsage;
	}

	/**
	 * Returns the faro project usage with the primary key or throws a <code>NoSuchFaroProjectUsageException</code> if it could not be found.
	 *
	 * @param faroProjectUsageId the primary key of the faro project usage
	 * @return the faro project usage
	 * @throws NoSuchFaroProjectUsageException if a faro project usage with the primary key could not be found
	 */
	@Override
	public FaroProjectUsage findByPrimaryKey(long faroProjectUsageId)
		throws NoSuchFaroProjectUsageException {

		return findByPrimaryKey((Serializable)faroProjectUsageId);
	}

	/**
	 * Returns the faro project usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroProjectUsageId the primary key of the faro project usage
	 * @return the faro project usage, or <code>null</code> if a faro project usage with the primary key could not be found
	 */
	@Override
	public FaroProjectUsage fetchByPrimaryKey(long faroProjectUsageId) {
		return fetchByPrimaryKey((Serializable)faroProjectUsageId);
	}

	/**
	 * Returns all the faro project usages.
	 *
	 * @return the faro project usages
	 */
	@Override
	public List<FaroProjectUsage> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the faro project usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro project usages
	 * @param end the upper bound of the range of faro project usages (not inclusive)
	 * @return the range of faro project usages
	 */
	@Override
	public List<FaroProjectUsage> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the faro project usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro project usages
	 * @param end the upper bound of the range of faro project usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of faro project usages
	 */
	@Override
	public List<FaroProjectUsage> findAll(
		int start, int end,
		OrderByComparator<FaroProjectUsage> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the faro project usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro project usages
	 * @param end the upper bound of the range of faro project usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of faro project usages
	 */
	@Override
	public List<FaroProjectUsage> findAll(
		int start, int end,
		OrderByComparator<FaroProjectUsage> orderByComparator,
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

		List<FaroProjectUsage> list = null;

		if (useFinderCache) {
			list = (List<FaroProjectUsage>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_FAROPROJECTUSAGE);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_FAROPROJECTUSAGE;

				sql = sql.concat(FaroProjectUsageModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<FaroProjectUsage>)QueryUtil.list(
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
	 * Removes all the faro project usages from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (FaroProjectUsage faroProjectUsage : findAll()) {
			remove(faroProjectUsage);
		}
	}

	/**
	 * Returns the number of faro project usages.
	 *
	 * @return the number of faro project usages
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_FAROPROJECTUSAGE);

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
		return "faroProjectUsageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FAROPROJECTUSAGE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return FaroProjectUsageModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the faro project usage persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathFetchByF_U = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByF_U",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"faroProjectId", "usageTime"}, true);

		FaroProjectUsageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FaroProjectUsageUtil.setPersistence(null);

		entityCache.removeCache(FaroProjectUsageImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_FAROPROJECTUSAGE =
		"SELECT faroProjectUsage FROM FaroProjectUsage faroProjectUsage";

	private static final String _SQL_SELECT_FAROPROJECTUSAGE_WHERE =
		"SELECT faroProjectUsage FROM FaroProjectUsage faroProjectUsage WHERE ";

	private static final String _SQL_COUNT_FAROPROJECTUSAGE =
		"SELECT COUNT(faroProjectUsage) FROM FaroProjectUsage faroProjectUsage";

	private static final String _SQL_COUNT_FAROPROJECTUSAGE_WHERE =
		"SELECT COUNT(faroProjectUsage) FROM FaroProjectUsage faroProjectUsage WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "faroProjectUsage.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No FaroProjectUsage exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No FaroProjectUsage exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		FaroProjectUsagePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}