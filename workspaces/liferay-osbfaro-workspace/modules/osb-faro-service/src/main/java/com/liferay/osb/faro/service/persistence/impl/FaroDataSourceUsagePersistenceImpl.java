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
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Map;

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
	extends BasePersistenceImpl
		<FaroDataSourceUsage, NoSuchFaroDataSourceUsageException>
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

	private UniquePersistenceFinder
		<FaroDataSourceUsage, NoSuchFaroDataSourceUsageException>
			_uniquePersistenceFinderByF_D_U;

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

		return _uniquePersistenceFinderByF_D_U.find(
			finderCache, new Object[] {dataSourceId, faroProjectId, usageTime});
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

		return _uniquePersistenceFinderByF_D_U.fetch(
			finderCache, new Object[] {dataSourceId, faroProjectId, usageTime},
			useFinderCache);
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

		return _uniquePersistenceFinderByF_D_U.count(
			finderCache, new Object[] {dataSourceId, faroProjectId, usageTime});
	}

	public FaroDataSourceUsagePersistenceImpl() {
		setModelClass(FaroDataSourceUsage.class);

		setModelImplClass(FaroDataSourceUsageImpl.class);
		setModelPKClass(long.class);

		setTable(FaroDataSourceUsageTable.INSTANCE);
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

		cacheUniqueFindersResult(faroDataSourceUsage, false);

		if (isNew) {
			faroDataSourceUsage.setNew(false);
		}

		faroDataSourceUsage.resetOriginalValues();

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
		_uniquePersistenceFinderByF_D_U = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByF_D_U",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"dataSourceId", "faroProjectId", "usageTime"}, 0,
				0, false, FaroDataSourceUsage::getDataSourceId,
				FaroDataSourceUsage::getFaroProjectId,
				FaroDataSourceUsage::getUsageTime),
			_SQL_SELECT_FARODATASOURCEUSAGE_WHERE, "",
			new FinderColumn<>(
				"faroDataSourceUsage.", "dataSourceId", FinderColumn.Type.LONG,
				"=", true, true, FaroDataSourceUsage::getDataSourceId),
			new FinderColumn<>(
				"faroDataSourceUsage.", "faroProjectId", FinderColumn.Type.LONG,
				"=", true, true, FaroDataSourceUsage::getFaroProjectId),
			new FinderColumn<>(
				"faroDataSourceUsage.", "usageTime", FinderColumn.Type.LONG,
				"=", true, true, FaroDataSourceUsage::getUsageTime));

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

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-832988730