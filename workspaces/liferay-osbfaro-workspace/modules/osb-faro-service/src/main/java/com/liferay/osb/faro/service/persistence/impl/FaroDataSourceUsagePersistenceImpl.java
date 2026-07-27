/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence.impl;

import com.liferay.osb.faro.exception.NoSuchFaroDataSourceUsageException;
import com.liferay.osb.faro.model.FaroDataSourceUsage;
import com.liferay.osb.faro.model.FaroDataSourceUsageTable;
import com.liferay.osb.faro.model.impl.FaroDataSourceUsageImpl;
import com.liferay.osb.faro.model.impl.FaroDataSourceUsageModelImpl;
import com.liferay.osb.faro.service.persistence.FaroDataSourceUsagePersistence;
import com.liferay.osb.faro.service.persistence.FaroDataSourceUsageUtil;
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
 * The persistence implementation for the faro data source usage service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @generated
 */
@Component(service = FaroDataSourceUsagePersistence.class)
public class FaroDataSourceUsagePersistenceImpl
	extends BasePersistenceImpl<FaroDataSourceUsage>
	implements FaroDataSourceUsagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FaroDataSourceUsageUtil</code> to access the faro data source usage persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FaroDataSourceUsageImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByF_D_U;

	/**
	 * Returns the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; or throws a <code>NoSuchFaroDataSourceUsageException</code> if it could not be found.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro data source usage
	 * @throws NoSuchFaroDataSourceUsageException if a matching faro data source usage could not be found
	 */
	@Override
	public FaroDataSourceUsage findByF_D_U(
			long dataSourceId, long faroProjectId, long usageTime)
		throws NoSuchFaroDataSourceUsageException {

		FaroDataSourceUsage faroDataSourceUsage = fetchByF_D_U(
			dataSourceId, faroProjectId, usageTime);

		if (faroDataSourceUsage == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("dataSourceId=");
			sb.append(dataSourceId);

			sb.append(", faroProjectId=");
			sb.append(faroProjectId);

			sb.append(", usageTime=");
			sb.append(usageTime);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchFaroDataSourceUsageException(sb.toString());
		}

		return faroDataSourceUsage;
	}

	/**
	 * Returns the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro data source usage, or <code>null</code> if a matching faro data source usage could not be found
	 */
	@Override
	public FaroDataSourceUsage fetchByF_D_U(
		long dataSourceId, long faroProjectId, long usageTime) {

		return fetchByF_D_U(dataSourceId, faroProjectId, usageTime, true);
	}

	/**
	 * Returns the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro data source usage, or <code>null</code> if a matching faro data source usage could not be found
	 */
	@Override
	public FaroDataSourceUsage fetchByF_D_U(
		long dataSourceId, long faroProjectId, long usageTime,
		boolean useFinderCache) {

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {dataSourceId, faroProjectId, usageTime};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByF_D_U, finderArgs, this);
		}

		if (result instanceof FaroDataSourceUsage) {
			FaroDataSourceUsage faroDataSourceUsage =
				(FaroDataSourceUsage)result;

			if ((dataSourceId != faroDataSourceUsage.getDataSourceId()) ||
				(faroProjectId != faroDataSourceUsage.getFaroProjectId()) ||
				(usageTime != faroDataSourceUsage.getUsageTime())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(5);

			sb.append(_SQL_SELECT_FARODATASOURCEUSAGE_WHERE);

			sb.append(_FINDER_COLUMN_F_D_U_DATASOURCEID_2);

			sb.append(_FINDER_COLUMN_F_D_U_FAROPROJECTID_2);

			sb.append(_FINDER_COLUMN_F_D_U_USAGETIME_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(dataSourceId);

				queryPos.add(faroProjectId);

				queryPos.add(usageTime);

				List<FaroDataSourceUsage> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByF_D_U, finderArgs, list);
					}
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							if (!useFinderCache) {
								finderArgs = new Object[] {
									dataSourceId, faroProjectId, usageTime
								};
							}

							_log.warn(
								"FaroDataSourceUsagePersistenceImpl.fetchByF_D_U(long, long, long, boolean) with parameters (" +
									StringUtil.merge(finderArgs) +
										") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					FaroDataSourceUsage faroDataSourceUsage = list.get(0);

					result = faroDataSourceUsage;

					cacheResult(faroDataSourceUsage);
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
			return (FaroDataSourceUsage)result;
		}
	}

	/**
	 * Removes the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; from the database.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the faro data source usage that was removed
	 */
	@Override
	public FaroDataSourceUsage removeByF_D_U(
			long dataSourceId, long faroProjectId, long usageTime)
		throws NoSuchFaroDataSourceUsageException {

		FaroDataSourceUsage faroDataSourceUsage = findByF_D_U(
			dataSourceId, faroProjectId, usageTime);

		return remove(faroDataSourceUsage);
	}

	/**
	 * Returns the number of faro data source usages where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63;.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the number of matching faro data source usages
	 */
	@Override
	public int countByF_D_U(
		long dataSourceId, long faroProjectId, long usageTime) {

		FaroDataSourceUsage faroDataSourceUsage = fetchByF_D_U(
			dataSourceId, faroProjectId, usageTime);

		if (faroDataSourceUsage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_F_D_U_DATASOURCEID_2 =
		"faroDataSourceUsage.dataSourceId = ? AND ";

	private static final String _FINDER_COLUMN_F_D_U_FAROPROJECTID_2 =
		"faroDataSourceUsage.faroProjectId = ? AND ";

	private static final String _FINDER_COLUMN_F_D_U_USAGETIME_2 =
		"faroDataSourceUsage.usageTime = ?";

	public FaroDataSourceUsagePersistenceImpl() {
		setModelClass(FaroDataSourceUsage.class);

		setModelImplClass(FaroDataSourceUsageImpl.class);
		setModelPKClass(long.class);

		setTable(FaroDataSourceUsageTable.INSTANCE);
	}

	/**
	 * Caches the faro data source usage in the entity cache if it is enabled.
	 *
	 * @param faroDataSourceUsage the faro data source usage
	 */
	@Override
	public void cacheResult(FaroDataSourceUsage faroDataSourceUsage) {
		entityCache.putResult(
			FaroDataSourceUsageImpl.class, faroDataSourceUsage.getPrimaryKey(),
			faroDataSourceUsage);

		finderCache.putResult(
			_finderPathFetchByF_D_U,
			new Object[] {
				faroDataSourceUsage.getDataSourceId(),
				faroDataSourceUsage.getFaroProjectId(),
				faroDataSourceUsage.getUsageTime()
			},
			faroDataSourceUsage);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the faro data source usages in the entity cache if it is enabled.
	 *
	 * @param faroDataSourceUsages the faro data source usages
	 */
	@Override
	public void cacheResult(List<FaroDataSourceUsage> faroDataSourceUsages) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (faroDataSourceUsages.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (FaroDataSourceUsage faroDataSourceUsage : faroDataSourceUsages) {
			if (entityCache.getResult(
					FaroDataSourceUsageImpl.class,
					faroDataSourceUsage.getPrimaryKey()) == null) {

				cacheResult(faroDataSourceUsage);
			}
		}
	}

	/**
	 * Clears the cache for all faro data source usages.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(FaroDataSourceUsageImpl.class);

		finderCache.clearCache(FaroDataSourceUsageImpl.class);
	}

	/**
	 * Clears the cache for the faro data source usage.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(FaroDataSourceUsage faroDataSourceUsage) {
		entityCache.removeResult(
			FaroDataSourceUsageImpl.class, faroDataSourceUsage);
	}

	@Override
	public void clearCache(List<FaroDataSourceUsage> faroDataSourceUsages) {
		for (FaroDataSourceUsage faroDataSourceUsage : faroDataSourceUsages) {
			entityCache.removeResult(
				FaroDataSourceUsageImpl.class, faroDataSourceUsage);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FaroDataSourceUsageImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(FaroDataSourceUsageImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		FaroDataSourceUsageModelImpl faroDataSourceUsageModelImpl) {

		Object[] args = new Object[] {
			faroDataSourceUsageModelImpl.getDataSourceId(),
			faroDataSourceUsageModelImpl.getFaroProjectId(),
			faroDataSourceUsageModelImpl.getUsageTime()
		};

		finderCache.putResult(
			_finderPathFetchByF_D_U, args, faroDataSourceUsageModelImpl);
	}

	/**
	 * Creates a new faro data source usage with the primary key. Does not add the faro data source usage to the database.
	 *
	 * @param faroDataSourceUsageId the primary key for the new faro data source usage
	 * @return the new faro data source usage
	 */
	@Override
	public FaroDataSourceUsage create(long faroDataSourceUsageId) {
		FaroDataSourceUsage faroDataSourceUsage = new FaroDataSourceUsageImpl();

		faroDataSourceUsage.setNew(true);
		faroDataSourceUsage.setPrimaryKey(faroDataSourceUsageId);

		faroDataSourceUsage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return faroDataSourceUsage;
	}

	/**
	 * Removes the faro data source usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroDataSourceUsageId the primary key of the faro data source usage
	 * @return the faro data source usage that was removed
	 * @throws NoSuchFaroDataSourceUsageException if a faro data source usage with the primary key could not be found
	 */
	@Override
	public FaroDataSourceUsage remove(long faroDataSourceUsageId)
		throws NoSuchFaroDataSourceUsageException {

		return remove((Serializable)faroDataSourceUsageId);
	}

	/**
	 * Removes the faro data source usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the faro data source usage
	 * @return the faro data source usage that was removed
	 * @throws NoSuchFaroDataSourceUsageException if a faro data source usage with the primary key could not be found
	 */
	@Override
	public FaroDataSourceUsage remove(Serializable primaryKey)
		throws NoSuchFaroDataSourceUsageException {

		Session session = null;

		try {
			session = openSession();

			FaroDataSourceUsage faroDataSourceUsage =
				(FaroDataSourceUsage)session.get(
					FaroDataSourceUsageImpl.class, primaryKey);

			if (faroDataSourceUsage == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchFaroDataSourceUsageException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(faroDataSourceUsage);
		}
		catch (NoSuchFaroDataSourceUsageException noSuchEntityException) {
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
	protected FaroDataSourceUsage removeImpl(
		FaroDataSourceUsage faroDataSourceUsage) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(faroDataSourceUsage)) {
				faroDataSourceUsage = (FaroDataSourceUsage)session.get(
					FaroDataSourceUsageImpl.class,
					faroDataSourceUsage.getPrimaryKeyObj());
			}

			if (faroDataSourceUsage != null) {
				session.delete(faroDataSourceUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (faroDataSourceUsage != null) {
			clearCache(faroDataSourceUsage);
		}

		return faroDataSourceUsage;
	}

	@Override
	public FaroDataSourceUsage updateImpl(
		FaroDataSourceUsage faroDataSourceUsage) {

		boolean isNew = faroDataSourceUsage.isNew();

		if (!(faroDataSourceUsage instanceof FaroDataSourceUsageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(faroDataSourceUsage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					faroDataSourceUsage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in faroDataSourceUsage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FaroDataSourceUsage implementation " +
					faroDataSourceUsage.getClass());
		}

		FaroDataSourceUsageModelImpl faroDataSourceUsageModelImpl =
			(FaroDataSourceUsageModelImpl)faroDataSourceUsage;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(faroDataSourceUsage);
			}
			else {
				faroDataSourceUsage = (FaroDataSourceUsage)session.merge(
					faroDataSourceUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			FaroDataSourceUsageImpl.class, faroDataSourceUsageModelImpl, false,
			true);

		cacheUniqueFindersCache(faroDataSourceUsageModelImpl);

		if (isNew) {
			faroDataSourceUsage.setNew(false);
		}

		faroDataSourceUsage.resetOriginalValues();

		return faroDataSourceUsage;
	}

	/**
	 * Returns the faro data source usage with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the faro data source usage
	 * @return the faro data source usage
	 * @throws NoSuchFaroDataSourceUsageException if a faro data source usage with the primary key could not be found
	 */
	@Override
	public FaroDataSourceUsage findByPrimaryKey(Serializable primaryKey)
		throws NoSuchFaroDataSourceUsageException {

		FaroDataSourceUsage faroDataSourceUsage = fetchByPrimaryKey(primaryKey);

		if (faroDataSourceUsage == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchFaroDataSourceUsageException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return faroDataSourceUsage;
	}

	/**
	 * Returns the faro data source usage with the primary key or throws a <code>NoSuchFaroDataSourceUsageException</code> if it could not be found.
	 *
	 * @param faroDataSourceUsageId the primary key of the faro data source usage
	 * @return the faro data source usage
	 * @throws NoSuchFaroDataSourceUsageException if a faro data source usage with the primary key could not be found
	 */
	@Override
	public FaroDataSourceUsage findByPrimaryKey(long faroDataSourceUsageId)
		throws NoSuchFaroDataSourceUsageException {

		return findByPrimaryKey((Serializable)faroDataSourceUsageId);
	}

	/**
	 * Returns the faro data source usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroDataSourceUsageId the primary key of the faro data source usage
	 * @return the faro data source usage, or <code>null</code> if a faro data source usage with the primary key could not be found
	 */
	@Override
	public FaroDataSourceUsage fetchByPrimaryKey(long faroDataSourceUsageId) {
		return fetchByPrimaryKey((Serializable)faroDataSourceUsageId);
	}

	/**
	 * Returns all the faro data source usages.
	 *
	 * @return the faro data source usages
	 */
	@Override
	public List<FaroDataSourceUsage> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the faro data source usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroDataSourceUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro data source usages
	 * @param end the upper bound of the range of faro data source usages (not inclusive)
	 * @return the range of faro data source usages
	 */
	@Override
	public List<FaroDataSourceUsage> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the faro data source usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroDataSourceUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro data source usages
	 * @param end the upper bound of the range of faro data source usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of faro data source usages
	 */
	@Override
	public List<FaroDataSourceUsage> findAll(
		int start, int end,
		OrderByComparator<FaroDataSourceUsage> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the faro data source usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroDataSourceUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro data source usages
	 * @param end the upper bound of the range of faro data source usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of faro data source usages
	 */
	@Override
	public List<FaroDataSourceUsage> findAll(
		int start, int end,
		OrderByComparator<FaroDataSourceUsage> orderByComparator,
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

		List<FaroDataSourceUsage> list = null;

		if (useFinderCache) {
			list = (List<FaroDataSourceUsage>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_FARODATASOURCEUSAGE);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_FARODATASOURCEUSAGE;

				sql = sql.concat(FaroDataSourceUsageModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<FaroDataSourceUsage>)QueryUtil.list(
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
	 * Removes all the faro data source usages from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (FaroDataSourceUsage faroDataSourceUsage : findAll()) {
			remove(faroDataSourceUsage);
		}
	}

	/**
	 * Returns the number of faro data source usages.
	 *
	 * @return the number of faro data source usages
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
					_SQL_COUNT_FARODATASOURCEUSAGE);

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
		return "faroDataSourceUsageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FARODATASOURCEUSAGE;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return FaroDataSourceUsageModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the faro data source usage persistence.
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

		_finderPathFetchByF_D_U = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByF_D_U",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"dataSourceId", "faroProjectId", "usageTime"}, true);

		FaroDataSourceUsageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FaroDataSourceUsageUtil.setPersistence(null);

		entityCache.removeCache(FaroDataSourceUsageImpl.class.getName());
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

	private static final String _SQL_SELECT_FARODATASOURCEUSAGE =
		"SELECT faroDataSourceUsage FROM FaroDataSourceUsage faroDataSourceUsage";

	private static final String _SQL_SELECT_FARODATASOURCEUSAGE_WHERE =
		"SELECT faroDataSourceUsage FROM FaroDataSourceUsage faroDataSourceUsage WHERE ";

	private static final String _SQL_COUNT_FARODATASOURCEUSAGE =
		"SELECT COUNT(faroDataSourceUsage) FROM FaroDataSourceUsage faroDataSourceUsage";

	private static final String _SQL_COUNT_FARODATASOURCEUSAGE_WHERE =
		"SELECT COUNT(faroDataSourceUsage) FROM FaroDataSourceUsage faroDataSourceUsage WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "faroDataSourceUsage.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No FaroDataSourceUsage exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No FaroDataSourceUsage exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		FaroDataSourceUsagePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1334447063