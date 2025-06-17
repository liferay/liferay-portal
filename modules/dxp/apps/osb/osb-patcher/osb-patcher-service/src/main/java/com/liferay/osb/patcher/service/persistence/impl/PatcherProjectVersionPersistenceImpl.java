/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherProjectVersionException;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersionTable;
import com.liferay.osb.patcher.model.impl.PatcherProjectVersionImpl;
import com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherProjectVersionPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherProjectVersionUtil;
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
 * The persistence implementation for the patcher project version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherProjectVersionPersistence.class)
public class PatcherProjectVersionPersistenceImpl
	extends BasePersistenceImpl<PatcherProjectVersion>
	implements PatcherProjectVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherProjectVersionUtil</code> to access the patcher project version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherProjectVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;

	public PatcherProjectVersionPersistenceImpl() {
		setModelClass(PatcherProjectVersion.class);

		setModelImplClass(PatcherProjectVersionImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherProjectVersionTable.INSTANCE);
	}

	/**
	 * Caches the patcher project version in the entity cache if it is enabled.
	 *
	 * @param patcherProjectVersion the patcher project version
	 */
	@Override
	public void cacheResult(PatcherProjectVersion patcherProjectVersion) {
		entityCache.putResult(
			PatcherProjectVersionImpl.class,
			patcherProjectVersion.getPrimaryKey(), patcherProjectVersion);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher project versions in the entity cache if it is enabled.
	 *
	 * @param patcherProjectVersions the patcher project versions
	 */
	@Override
	public void cacheResult(
		List<PatcherProjectVersion> patcherProjectVersions) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherProjectVersions.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherProjectVersion patcherProjectVersion :
				patcherProjectVersions) {

			if (entityCache.getResult(
					PatcherProjectVersionImpl.class,
					patcherProjectVersion.getPrimaryKey()) == null) {

				cacheResult(patcherProjectVersion);
			}
		}
	}

	/**
	 * Clears the cache for all patcher project versions.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherProjectVersionImpl.class);

		finderCache.clearCache(PatcherProjectVersionImpl.class);
	}

	/**
	 * Clears the cache for the patcher project version.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherProjectVersion patcherProjectVersion) {
		entityCache.removeResult(
			PatcherProjectVersionImpl.class, patcherProjectVersion);
	}

	@Override
	public void clearCache(List<PatcherProjectVersion> patcherProjectVersions) {
		for (PatcherProjectVersion patcherProjectVersion :
				patcherProjectVersions) {

			entityCache.removeResult(
				PatcherProjectVersionImpl.class, patcherProjectVersion);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherProjectVersionImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				PatcherProjectVersionImpl.class, primaryKey);
		}
	}

	/**
	 * Creates a new patcher project version with the primary key. Does not add the patcher project version to the database.
	 *
	 * @param patcherProjectVersionId the primary key for the new patcher project version
	 * @return the new patcher project version
	 */
	@Override
	public PatcherProjectVersion create(long patcherProjectVersionId) {
		PatcherProjectVersion patcherProjectVersion =
			new PatcherProjectVersionImpl();

		patcherProjectVersion.setNew(true);
		patcherProjectVersion.setPrimaryKey(patcherProjectVersionId);

		patcherProjectVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherProjectVersion;
	}

	/**
	 * Removes the patcher project version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version that was removed
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion remove(long patcherProjectVersionId)
		throws NoSuchPatcherProjectVersionException {

		return remove((Serializable)patcherProjectVersionId);
	}

	/**
	 * Removes the patcher project version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher project version
	 * @return the patcher project version that was removed
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion remove(Serializable primaryKey)
		throws NoSuchPatcherProjectVersionException {

		Session session = null;

		try {
			session = openSession();

			PatcherProjectVersion patcherProjectVersion =
				(PatcherProjectVersion)session.get(
					PatcherProjectVersionImpl.class, primaryKey);

			if (patcherProjectVersion == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherProjectVersionException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherProjectVersion);
		}
		catch (NoSuchPatcherProjectVersionException noSuchEntityException) {
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
	protected PatcherProjectVersion removeImpl(
		PatcherProjectVersion patcherProjectVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherProjectVersion)) {
				patcherProjectVersion = (PatcherProjectVersion)session.get(
					PatcherProjectVersionImpl.class,
					patcherProjectVersion.getPrimaryKeyObj());
			}

			if (patcherProjectVersion != null) {
				session.delete(patcherProjectVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (patcherProjectVersion != null) {
			clearCache(patcherProjectVersion);
		}

		return patcherProjectVersion;
	}

	@Override
	public PatcherProjectVersion updateImpl(
		PatcherProjectVersion patcherProjectVersion) {

		boolean isNew = patcherProjectVersion.isNew();

		if (!(patcherProjectVersion instanceof
				PatcherProjectVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherProjectVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherProjectVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherProjectVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherProjectVersion implementation " +
					patcherProjectVersion.getClass());
		}

		PatcherProjectVersionModelImpl patcherProjectVersionModelImpl =
			(PatcherProjectVersionModelImpl)patcherProjectVersion;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherProjectVersion.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherProjectVersion.setCreateDate(date);
			}
			else {
				patcherProjectVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherProjectVersionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherProjectVersion.setModifiedDate(date);
			}
			else {
				patcherProjectVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherProjectVersion);
			}
			else {
				patcherProjectVersion = (PatcherProjectVersion)session.merge(
					patcherProjectVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherProjectVersionImpl.class, patcherProjectVersion, false,
			true);

		if (isNew) {
			patcherProjectVersion.setNew(false);
		}

		patcherProjectVersion.resetOriginalValues();

		return patcherProjectVersion;
	}

	/**
	 * Returns the patcher project version with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher project version
	 * @return the patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherProjectVersionException {

		PatcherProjectVersion patcherProjectVersion = fetchByPrimaryKey(
			primaryKey);

		if (patcherProjectVersion == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherProjectVersionException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherProjectVersion;
	}

	/**
	 * Returns the patcher project version with the primary key or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion findByPrimaryKey(long patcherProjectVersionId)
		throws NoSuchPatcherProjectVersionException {

		return findByPrimaryKey((Serializable)patcherProjectVersionId);
	}

	/**
	 * Returns the patcher project version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version, or <code>null</code> if a patcher project version with the primary key could not be found
	 */
	@Override
	public PatcherProjectVersion fetchByPrimaryKey(
		long patcherProjectVersionId) {

		return fetchByPrimaryKey((Serializable)patcherProjectVersionId);
	}

	/**
	 * Returns all the patcher project versions.
	 *
	 * @return the patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findAll(
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher project versions
	 */
	@Override
	public List<PatcherProjectVersion> findAll(
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
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

		List<PatcherProjectVersion> list = null;

		if (useFinderCache) {
			list = (List<PatcherProjectVersion>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERPROJECTVERSION);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERPROJECTVERSION;

				sql = sql.concat(PatcherProjectVersionModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherProjectVersion>)QueryUtil.list(
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
	 * Removes all the patcher project versions from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PatcherProjectVersion patcherProjectVersion : findAll()) {
			remove(patcherProjectVersion);
		}
	}

	/**
	 * Returns the number of patcher project versions.
	 *
	 * @return the number of patcher project versions
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
					_SQL_COUNT_PATCHERPROJECTVERSION);

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
		return "patcherProjectVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERPROJECTVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherProjectVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher project version persistence.
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

		PatcherProjectVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherProjectVersionUtil.setPersistence(null);

		entityCache.removeCache(PatcherProjectVersionImpl.class.getName());
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

	private static final String _SQL_SELECT_PATCHERPROJECTVERSION =
		"SELECT patcherProjectVersion FROM PatcherProjectVersion patcherProjectVersion";

	private static final String _SQL_COUNT_PATCHERPROJECTVERSION =
		"SELECT COUNT(patcherProjectVersion) FROM PatcherProjectVersion patcherProjectVersion";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"patcherProjectVersion.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherProjectVersion exists with the primary key ";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherProjectVersionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}