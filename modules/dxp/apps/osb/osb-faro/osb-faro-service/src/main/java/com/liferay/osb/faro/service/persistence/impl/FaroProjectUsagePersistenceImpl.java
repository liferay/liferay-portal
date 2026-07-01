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
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
	extends BasePersistenceImpl
		<FaroProjectUsage, NoSuchFaroProjectUsageException>
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

	private UniquePersistenceFinder
		<FaroProjectUsage, NoSuchFaroProjectUsageException>
			_uniquePersistenceFinderByF_U;

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

		return _uniquePersistenceFinderByF_U.find(
			finderCache, new Object[] {faroProjectId, usageTime});
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

		return _uniquePersistenceFinderByF_U.fetch(
			finderCache, new Object[] {faroProjectId, usageTime},
			useFinderCache);
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
		return _uniquePersistenceFinderByF_U.count(
			finderCache, new Object[] {faroProjectId, usageTime});
	}

	public FaroProjectUsagePersistenceImpl() {
		setModelClass(FaroProjectUsage.class);

		setModelImplClass(FaroProjectUsageImpl.class);
		setModelPKClass(long.class);

		setTable(FaroProjectUsageTable.INSTANCE);
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

		cacheUniqueFindersResult(faroProjectUsage, false);

		if (isNew) {
			faroProjectUsage.setNew(false);
		}

		faroProjectUsage.resetOriginalValues();

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
		_uniquePersistenceFinderByF_U = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByF_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"faroProjectId", "usageTime"}, 0, 0, false,
				FaroProjectUsage::getFaroProjectId,
				FaroProjectUsage::getUsageTime),
			_SQL_SELECT_FAROPROJECTUSAGE_WHERE, "",
			new FinderColumn<>(
				"faroProjectUsage.", "faroProjectId", FinderColumn.Type.LONG,
				"=", true, true, FaroProjectUsage::getFaroProjectId),
			new FinderColumn<>(
				"faroProjectUsage.", "usageTime", FinderColumn.Type.LONG, "=",
				true, true, FaroProjectUsage::getUsageTime));

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

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No FaroProjectUsage exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		FaroProjectUsagePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2039183915