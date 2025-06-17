/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherProductVersionException;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProductVersionTable;
import com.liferay.osb.patcher.model.impl.PatcherProductVersionImpl;
import com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherProductVersionPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherProductVersionUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher product version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherProductVersionPersistence.class)
public class PatcherProductVersionPersistenceImpl
	extends BasePersistenceImpl<PatcherProductVersion>
	implements PatcherProductVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherProductVersionUtil</code> to access the patcher product version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherProductVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public PatcherProductVersionPersistenceImpl() {
		setModelClass(PatcherProductVersion.class);

		setModelImplClass(PatcherProductVersionImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherProductVersionTable.INSTANCE);
	}

	/**
	 * Caches the patcher product version in the entity cache if it is enabled.
	 *
	 * @param patcherProductVersion the patcher product version
	 */
	@Override
	public void cacheResult(PatcherProductVersion patcherProductVersion) {
		entityCache.putResult(
			PatcherProductVersionImpl.class,
			patcherProductVersion.getPrimaryKey(), patcherProductVersion);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher product versions in the entity cache if it is enabled.
	 *
	 * @param patcherProductVersions the patcher product versions
	 */
	@Override
	public void cacheResult(
		List<PatcherProductVersion> patcherProductVersions) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherProductVersions.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherProductVersion patcherProductVersion :
				patcherProductVersions) {

			if (entityCache.getResult(
					PatcherProductVersionImpl.class,
					patcherProductVersion.getPrimaryKey()) == null) {

				cacheResult(patcherProductVersion);
			}
		}
	}

	/**
	 * Clears the cache for all patcher product versions.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherProductVersionImpl.class);

		finderCache.clearCache(PatcherProductVersionImpl.class);
	}

	/**
	 * Clears the cache for the patcher product version.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherProductVersion patcherProductVersion) {
		entityCache.removeResult(
			PatcherProductVersionImpl.class, patcherProductVersion);
	}

	@Override
	public void clearCache(List<PatcherProductVersion> patcherProductVersions) {
		for (PatcherProductVersion patcherProductVersion :
				patcherProductVersions) {

			entityCache.removeResult(
				PatcherProductVersionImpl.class, patcherProductVersion);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherProductVersionImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				PatcherProductVersionImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new patcher product version with the primary key. Does not add the patcher product version to the database.
	 *
	 * @param patcherProductVersionId the primary key for the new patcher product version
	 * @return the new patcher product version
	 */
	@Override
	public PatcherProductVersion create(long patcherProductVersionId) {
		PatcherProductVersion patcherProductVersion =
			new PatcherProductVersionImpl();

		patcherProductVersion.setNew(true);
		patcherProductVersion.setPrimaryKey(patcherProductVersionId);

		patcherProductVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherProductVersion;
	}

	/**
	 * Removes the patcher product version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version that was removed
	 * @throws NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 */
	@Override
	public PatcherProductVersion remove(long patcherProductVersionId)
		throws NoSuchPatcherProductVersionException {

		return remove((Serializable)patcherProductVersionId);
	}

	/**
	 * Removes the patcher product version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher product version
	 * @return the patcher product version that was removed
	 * @throws NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 */
	@Override
	public PatcherProductVersion remove(Serializable primaryKey)
		throws NoSuchPatcherProductVersionException {

		Session session = null;

		try {
			session = openSession();

			PatcherProductVersion patcherProductVersion =
				(PatcherProductVersion)session.get(
					PatcherProductVersionImpl.class, primaryKey);

			if (patcherProductVersion == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherProductVersionException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherProductVersion);
		}
		catch (NoSuchPatcherProductVersionException noSuchEntityException) {
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
	protected PatcherProductVersion removeImpl(
		PatcherProductVersion patcherProductVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherProductVersion)) {
				patcherProductVersion = (PatcherProductVersion)session.get(
					PatcherProductVersionImpl.class,
					patcherProductVersion.getPrimaryKeyObj());
			}

			if (patcherProductVersion != null) {
				session.delete(patcherProductVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherProductVersion != null) {
			clearCache(patcherProductVersion);
		}

		return patcherProductVersion;
	}

	@Override
	public PatcherProductVersion updateImpl(
		PatcherProductVersion patcherProductVersion) {

		boolean isNew = patcherProductVersion.isNew();

		if (!(patcherProductVersion instanceof
				PatcherProductVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherProductVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherProductVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherProductVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherProductVersion implementation " +
					patcherProductVersion.getClass());
		}

		PatcherProductVersionModelImpl patcherProductVersionModelImpl =
			(PatcherProductVersionModelImpl)patcherProductVersion;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherProductVersion.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherProductVersion.setCreateDate(date);
			}
			else {
				patcherProductVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherProductVersionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherProductVersion.setModifiedDate(date);
			}
			else {
				patcherProductVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherProductVersion);
			}
			else {
				patcherProductVersion = (PatcherProductVersion)session.merge(
					patcherProductVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherProductVersionImpl.class, patcherProductVersion, false,
			true);

		if (isNew) {
			patcherProductVersion.setNew(false);
		}

		patcherProductVersion.resetOriginalValues();

		return patcherProductVersion;
	}

	/**
	 * Returns the patcher product version with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher product version
	 * @return the patcher product version
	 * @throws NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 */
	@Override
	public PatcherProductVersion findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherProductVersionException {

		PatcherProductVersion patcherProductVersion = fetchByPrimaryKey(
			primaryKey);

		if (patcherProductVersion == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherProductVersionException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherProductVersion;
	}

	/**
	 * Returns the patcher product version with the primary key or throws a <code>NoSuchPatcherProductVersionException</code> if it could not be found.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version
	 * @throws NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 */
	@Override
	public PatcherProductVersion findByPrimaryKey(long patcherProductVersionId)
		throws NoSuchPatcherProductVersionException {

		return findByPrimaryKey((Serializable)patcherProductVersionId);
	}

	/**
	 * Returns the patcher product version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version, or <code>null</code> if a patcher product version with the primary key could not be found
	 */
	@Override
	public PatcherProductVersion fetchByPrimaryKey(
		long patcherProductVersionId) {

		return fetchByPrimaryKey((Serializable)patcherProductVersionId);
	}

	/**
	 * Returns all the patcher product versions.
	 *
	 * @return the patcher product versions
	 */
	@Override
	public List<PatcherProductVersion> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher product versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @return the range of patcher product versions
	 */
	@Override
	public List<PatcherProductVersion> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher product versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher product versions
	 */
	@Override
	public List<PatcherProductVersion> findAll(
		int start, int end,
		OrderByComparator<PatcherProductVersion> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher product versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher product versions
	 */
	@Override
	public List<PatcherProductVersion> findAll(
		int start, int end,
		OrderByComparator<PatcherProductVersion> orderByComparator,
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

		List<PatcherProductVersion> list = null;

		if (useFinderCache) {
			list = (List<PatcherProductVersion>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERPRODUCTVERSION);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERPRODUCTVERSION;

				sql = sql.concat(PatcherProductVersionModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherProductVersion>)QueryUtil.list(
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
	 * Removes all the patcher product versions from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherProductVersion patcherProductVersion : findAll()) {
			remove(patcherProductVersion);
		}
	}

	/**
	 * Returns the number of patcher product versions.
	 *
	 * @return the number of patcher product versions
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
					_SQL_COUNT_PATCHERPRODUCTVERSION);

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
		return "patcherProductVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERPRODUCTVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherProductVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher product version persistence.
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

		PatcherProductVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherProductVersionUtil.setPersistence(null);

		entityCache.removeCache(PatcherProductVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_PATCHERPRODUCTVERSION =
		"SELECT patcherProductVersion FROM PatcherProductVersion patcherProductVersion";

	private static final String _SQL_COUNT_PATCHERPRODUCTVERSION =
		"SELECT COUNT(patcherProductVersion) FROM PatcherProductVersion patcherProductVersion";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"patcherProductVersion.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherProductVersion exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherProductVersionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}