/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.persistence.impl;

import com.liferay.document.library.exception.NoSuchStorageQuotaException;
import com.liferay.document.library.model.DLStorageQuota;
import com.liferay.document.library.model.DLStorageQuotaTable;
import com.liferay.document.library.model.impl.DLStorageQuotaImpl;
import com.liferay.document.library.model.impl.DLStorageQuotaModelImpl;
import com.liferay.document.library.service.persistence.DLStorageQuotaPersistence;
import com.liferay.document.library.service.persistence.DLStorageQuotaUtil;
import com.liferay.document.library.service.persistence.impl.constants.DLPersistenceConstants;
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
 * The persistence implementation for the dl storage quota service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DLStorageQuotaPersistence.class)
public class DLStorageQuotaPersistenceImpl
	extends BasePersistenceImpl<DLStorageQuota, NoSuchStorageQuotaException>
	implements DLStorageQuotaPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DLStorageQuotaUtil</code> to access the dl storage quota persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DLStorageQuotaImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder<DLStorageQuota, NoSuchStorageQuotaException>
		_uniquePersistenceFinderByCompanyId;

	/**
	 * Returns the dl storage quota where companyId = &#63; or throws a <code>NoSuchStorageQuotaException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @return the matching dl storage quota
	 * @throws NoSuchStorageQuotaException if a matching dl storage quota could not be found
	 */
	@Override
	public DLStorageQuota findByCompanyId(long companyId)
		throws NoSuchStorageQuotaException {

		return _uniquePersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the dl storage quota where companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dl storage quota, or <code>null</code> if a matching dl storage quota could not be found
	 */
	@Override
	public DLStorageQuota fetchByCompanyId(
		long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByCompanyId.fetch(
			finderCache, new Object[] {companyId}, useFinderCache);
	}

	/**
	 * Removes the dl storage quota where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @return the dl storage quota that was removed
	 */
	@Override
	public DLStorageQuota removeByCompanyId(long companyId)
		throws NoSuchStorageQuotaException {

		DLStorageQuota dlStorageQuota = findByCompanyId(companyId);

		return remove(dlStorageQuota);
	}

	/**
	 * Returns the number of dl storage quotas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching dl storage quotas
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _uniquePersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	public DLStorageQuotaPersistenceImpl() {
		setModelClass(DLStorageQuota.class);

		setModelImplClass(DLStorageQuotaImpl.class);
		setModelPKClass(long.class);

		setTable(DLStorageQuotaTable.INSTANCE);
	}

	/**
	 * Creates a new dl storage quota with the primary key. Does not add the dl storage quota to the database.
	 *
	 * @param dlStorageQuotaId the primary key for the new dl storage quota
	 * @return the new dl storage quota
	 */
	@Override
	public DLStorageQuota create(long dlStorageQuotaId) {
		DLStorageQuota dlStorageQuota = new DLStorageQuotaImpl();

		dlStorageQuota.setNew(true);
		dlStorageQuota.setPrimaryKey(dlStorageQuotaId);

		dlStorageQuota.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dlStorageQuota;
	}

	/**
	 * Removes the dl storage quota with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dlStorageQuotaId the primary key of the dl storage quota
	 * @return the dl storage quota that was removed
	 * @throws NoSuchStorageQuotaException if a dl storage quota with the primary key could not be found
	 */
	@Override
	public DLStorageQuota remove(long dlStorageQuotaId)
		throws NoSuchStorageQuotaException {

		return remove((Serializable)dlStorageQuotaId);
	}

	@Override
	protected DLStorageQuota removeImpl(DLStorageQuota dlStorageQuota) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dlStorageQuota)) {
				dlStorageQuota = (DLStorageQuota)session.get(
					DLStorageQuotaImpl.class,
					dlStorageQuota.getPrimaryKeyObj());
			}

			if (dlStorageQuota != null) {
				session.delete(dlStorageQuota);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dlStorageQuota != null) {
			clearCache(dlStorageQuota);
		}

		return dlStorageQuota;
	}

	@Override
	public DLStorageQuota updateImpl(DLStorageQuota dlStorageQuota) {
		boolean isNew = dlStorageQuota.isNew();

		if (!(dlStorageQuota instanceof DLStorageQuotaModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dlStorageQuota.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					dlStorageQuota);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dlStorageQuota proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DLStorageQuota implementation " +
					dlStorageQuota.getClass());
		}

		DLStorageQuotaModelImpl dlStorageQuotaModelImpl =
			(DLStorageQuotaModelImpl)dlStorageQuota;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dlStorageQuota);
			}
			else {
				dlStorageQuota = (DLStorageQuota)session.merge(dlStorageQuota);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(dlStorageQuota, false);

		if (isNew) {
			dlStorageQuota.setNew(false);
		}

		dlStorageQuota.resetOriginalValues();

		return dlStorageQuota;
	}

	/**
	 * Returns the dl storage quota with the primary key or throws a <code>NoSuchStorageQuotaException</code> if it could not be found.
	 *
	 * @param dlStorageQuotaId the primary key of the dl storage quota
	 * @return the dl storage quota
	 * @throws NoSuchStorageQuotaException if a dl storage quota with the primary key could not be found
	 */
	@Override
	public DLStorageQuota findByPrimaryKey(long dlStorageQuotaId)
		throws NoSuchStorageQuotaException {

		return findByPrimaryKey((Serializable)dlStorageQuotaId);
	}

	/**
	 * Returns the dl storage quota with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dlStorageQuotaId the primary key of the dl storage quota
	 * @return the dl storage quota, or <code>null</code> if a dl storage quota with the primary key could not be found
	 */
	@Override
	public DLStorageQuota fetchByPrimaryKey(long dlStorageQuotaId) {
		return fetchByPrimaryKey((Serializable)dlStorageQuotaId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "dlStorageQuotaId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DLSTORAGEQUOTA;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DLStorageQuotaModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the dl storage quota persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByCompanyId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCompanyId",
				new String[] {Long.class.getName()}, new String[] {"companyId"},
				0, 0, false, DLStorageQuota::getCompanyId),
			_SQL_SELECT_DLSTORAGEQUOTA_WHERE, "",
			new FinderColumn<>(
				"dlStorageQuota.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, DLStorageQuota::getCompanyId));

		DLStorageQuotaUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DLStorageQuotaUtil.setPersistence(null);

		entityCache.removeCache(DLStorageQuotaImpl.class.getName());
	}

	@Override
	@Reference(
		target = DLPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_DLSTORAGEQUOTA =
		"SELECT dlStorageQuota FROM DLStorageQuota dlStorageQuota";

	private static final String _SQL_SELECT_DLSTORAGEQUOTA_WHERE =
		"SELECT dlStorageQuota FROM DLStorageQuota dlStorageQuota WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1038185721