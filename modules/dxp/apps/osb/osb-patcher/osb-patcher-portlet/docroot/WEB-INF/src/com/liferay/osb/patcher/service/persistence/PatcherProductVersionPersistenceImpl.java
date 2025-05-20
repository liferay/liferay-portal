/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.NoSuchPatcherProductVersionException;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.impl.PatcherProductVersionImpl;
import com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl;

import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnmodifiableList;
import com.liferay.portal.model.CacheModel;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The persistence implementation for the patcher product version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherProductVersionPersistence
 * @see PatcherProductVersionUtil
 * @generated
 */
public class PatcherProductVersionPersistenceImpl extends BasePersistenceImpl<PatcherProductVersion>
	implements PatcherProductVersionPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link PatcherProductVersionUtil} to access the patcher product version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = PatcherProductVersionImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(PatcherProductVersionModelImpl.ENTITY_CACHE_ENABLED,
			PatcherProductVersionModelImpl.FINDER_CACHE_ENABLED,
			PatcherProductVersionImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(PatcherProductVersionModelImpl.ENTITY_CACHE_ENABLED,
			PatcherProductVersionModelImpl.FINDER_CACHE_ENABLED,
			PatcherProductVersionImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(PatcherProductVersionModelImpl.ENTITY_CACHE_ENABLED,
			PatcherProductVersionModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);

	public PatcherProductVersionPersistenceImpl() {
		setModelClass(PatcherProductVersion.class);
	}

	/**
	 * Caches the patcher product version in the entity cache if it is enabled.
	 *
	 * @param patcherProductVersion the patcher product version
	 */
	@Override
	public void cacheResult(PatcherProductVersion patcherProductVersion) {
		EntityCacheUtil.putResult(PatcherProductVersionModelImpl.ENTITY_CACHE_ENABLED,
			PatcherProductVersionImpl.class,
			patcherProductVersion.getPrimaryKey(), patcherProductVersion);

		patcherProductVersion.resetOriginalValues();
	}

	/**
	 * Caches the patcher product versions in the entity cache if it is enabled.
	 *
	 * @param patcherProductVersions the patcher product versions
	 */
	@Override
	public void cacheResult(List<PatcherProductVersion> patcherProductVersions) {
		for (PatcherProductVersion patcherProductVersion : patcherProductVersions) {
			if (EntityCacheUtil.getResult(
						PatcherProductVersionModelImpl.ENTITY_CACHE_ENABLED,
						PatcherProductVersionImpl.class,
						patcherProductVersion.getPrimaryKey()) == null) {
				cacheResult(patcherProductVersion);
			}
			else {
				patcherProductVersion.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all patcher product versions.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		if (_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			CacheRegistryUtil.clear(PatcherProductVersionImpl.class.getName());
		}

		EntityCacheUtil.clearCache(PatcherProductVersionImpl.class.getName());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the patcher product version.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherProductVersion patcherProductVersion) {
		EntityCacheUtil.removeResult(PatcherProductVersionModelImpl.ENTITY_CACHE_ENABLED,
			PatcherProductVersionImpl.class,
			patcherProductVersion.getPrimaryKey());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<PatcherProductVersion> patcherProductVersions) {
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (PatcherProductVersion patcherProductVersion : patcherProductVersions) {
			EntityCacheUtil.removeResult(PatcherProductVersionModelImpl.ENTITY_CACHE_ENABLED,
				PatcherProductVersionImpl.class,
				patcherProductVersion.getPrimaryKey());
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
		PatcherProductVersion patcherProductVersion = new PatcherProductVersionImpl();

		patcherProductVersion.setNew(true);
		patcherProductVersion.setPrimaryKey(patcherProductVersionId);

		return patcherProductVersion;
	}

	/**
	 * Removes the patcher product version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherProductVersion remove(long patcherProductVersionId)
		throws NoSuchPatcherProductVersionException, SystemException {
		return remove((Serializable)patcherProductVersionId);
	}

	/**
	 * Removes the patcher product version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher product version
	 * @return the patcher product version that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherProductVersion remove(Serializable primaryKey)
		throws NoSuchPatcherProductVersionException, SystemException {
		Session session = null;

		try {
			session = openSession();

			PatcherProductVersion patcherProductVersion = (PatcherProductVersion)session.get(PatcherProductVersionImpl.class,
					primaryKey);

			if (patcherProductVersion == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherProductVersionException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(patcherProductVersion);
		}
		catch (NoSuchPatcherProductVersionException nsee) {
			throw nsee;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected PatcherProductVersion removeImpl(
		PatcherProductVersion patcherProductVersion) throws SystemException {
		patcherProductVersion = toUnwrappedModel(patcherProductVersion);

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherProductVersion)) {
				patcherProductVersion = (PatcherProductVersion)session.get(PatcherProductVersionImpl.class,
						patcherProductVersion.getPrimaryKeyObj());
			}

			if (patcherProductVersion != null) {
				session.delete(patcherProductVersion);
			}
		}
		catch (Exception e) {
			throw processException(e);
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
		com.liferay.osb.patcher.model.PatcherProductVersion patcherProductVersion)
		throws SystemException {
		patcherProductVersion = toUnwrappedModel(patcherProductVersion);

		boolean isNew = patcherProductVersion.isNew();

		Session session = null;

		try {
			session = openSession();

			if (patcherProductVersion.isNew()) {
				session.save(patcherProductVersion);

				patcherProductVersion.setNew(false);
			}
			else {
				session.merge(patcherProductVersion);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (isNew) {
			FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}

		EntityCacheUtil.putResult(PatcherProductVersionModelImpl.ENTITY_CACHE_ENABLED,
			PatcherProductVersionImpl.class,
			patcherProductVersion.getPrimaryKey(), patcherProductVersion);

		return patcherProductVersion;
	}

	protected PatcherProductVersion toUnwrappedModel(
		PatcherProductVersion patcherProductVersion) {
		if (patcherProductVersion instanceof PatcherProductVersionImpl) {
			return patcherProductVersion;
		}

		PatcherProductVersionImpl patcherProductVersionImpl = new PatcherProductVersionImpl();

		patcherProductVersionImpl.setNew(patcherProductVersion.isNew());
		patcherProductVersionImpl.setPrimaryKey(patcherProductVersion.getPrimaryKey());

		patcherProductVersionImpl.setPatcherProductVersionId(patcherProductVersion.getPatcherProductVersionId());
		patcherProductVersionImpl.setCompanyId(patcherProductVersion.getCompanyId());
		patcherProductVersionImpl.setUserId(patcherProductVersion.getUserId());
		patcherProductVersionImpl.setUserName(patcherProductVersion.getUserName());
		patcherProductVersionImpl.setCreateDate(patcherProductVersion.getCreateDate());
		patcherProductVersionImpl.setModifiedDate(patcherProductVersion.getModifiedDate());
		patcherProductVersionImpl.setName(patcherProductVersion.getName());
		patcherProductVersionImpl.setFixDeliveryMethod(patcherProductVersion.getFixDeliveryMethod());
		patcherProductVersionImpl.setModuleFolderName(patcherProductVersion.getModuleFolderName());

		return patcherProductVersionImpl;
	}

	/**
	 * Returns the patcher product version with the primary key or throws a {@link com.liferay.portal.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher product version
	 * @return the patcher product version
	 * @throws com.liferay.osb.patcher.NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherProductVersion findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherProductVersionException, SystemException {
		PatcherProductVersion patcherProductVersion = fetchByPrimaryKey(primaryKey);

		if (patcherProductVersion == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherProductVersionException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return patcherProductVersion;
	}

	/**
	 * Returns the patcher product version with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherProductVersionException} if it could not be found.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version
	 * @throws com.liferay.osb.patcher.NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherProductVersion findByPrimaryKey(long patcherProductVersionId)
		throws NoSuchPatcherProductVersionException, SystemException {
		return findByPrimaryKey((Serializable)patcherProductVersionId);
	}

	/**
	 * Returns the patcher product version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher product version
	 * @return the patcher product version, or <code>null</code> if a patcher product version with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherProductVersion fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		PatcherProductVersion patcherProductVersion = (PatcherProductVersion)EntityCacheUtil.getResult(PatcherProductVersionModelImpl.ENTITY_CACHE_ENABLED,
				PatcherProductVersionImpl.class, primaryKey);

		if (patcherProductVersion == _nullPatcherProductVersion) {
			return null;
		}

		if (patcherProductVersion == null) {
			Session session = null;

			try {
				session = openSession();

				patcherProductVersion = (PatcherProductVersion)session.get(PatcherProductVersionImpl.class,
						primaryKey);

				if (patcherProductVersion != null) {
					cacheResult(patcherProductVersion);
				}
				else {
					EntityCacheUtil.putResult(PatcherProductVersionModelImpl.ENTITY_CACHE_ENABLED,
						PatcherProductVersionImpl.class, primaryKey,
						_nullPatcherProductVersion);
				}
			}
			catch (Exception e) {
				EntityCacheUtil.removeResult(PatcherProductVersionModelImpl.ENTITY_CACHE_ENABLED,
					PatcherProductVersionImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return patcherProductVersion;
	}

	/**
	 * Returns the patcher product version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version, or <code>null</code> if a patcher product version with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherProductVersion fetchByPrimaryKey(long patcherProductVersionId)
		throws SystemException {
		return fetchByPrimaryKey((Serializable)patcherProductVersionId);
	}

	/**
	 * Returns all the patcher product versions.
	 *
	 * @return the patcher product versions
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherProductVersion> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher product versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @return the range of patcher product versions
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherProductVersion> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher product versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProductVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher product versions
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherProductVersion> findAll(int start, int end,
		OrderByComparator orderByComparator) throws SystemException {
		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {
			pagination = false;
			finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL;
			finderArgs = FINDER_ARGS_EMPTY;
		}
		else {
			finderPath = FINDER_PATH_WITH_PAGINATION_FIND_ALL;
			finderArgs = new Object[] { start, end, orderByComparator };
		}

		List<PatcherProductVersion> list = (List<PatcherProductVersion>)FinderCacheUtil.getResult(finderPath,
				finderArgs, this);

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 3));

				query.append(_SQL_SELECT_PATCHERPRODUCTVERSION);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERPRODUCTVERSION;

				if (pagination) {
					sql = sql.concat(PatcherProductVersionModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<PatcherProductVersion>)QueryUtil.list(q,
							getDialect(), start, end, false);

					Collections.sort(list);

					list = new UnmodifiableList<PatcherProductVersion>(list);
				}
				else {
					list = (List<PatcherProductVersion>)QueryUtil.list(q,
							getDialect(), start, end);
				}

				cacheResult(list);

				FinderCacheUtil.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				FinderCacheUtil.removeResult(finderPath, finderArgs);

				throw processException(e);
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
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removeAll() throws SystemException {
		for (PatcherProductVersion patcherProductVersion : findAll()) {
			remove(patcherProductVersion);
		}
	}

	/**
	 * Returns the number of patcher product versions.
	 *
	 * @return the number of patcher product versions
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int countAll() throws SystemException {
		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_ALL,
				FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_PATCHERPRODUCTVERSION);

				count = (Long)q.uniqueResult();

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_ALL,
					FINDER_ARGS_EMPTY, count);
			}
			catch (Exception e) {
				FinderCacheUtil.removeResult(FINDER_PATH_COUNT_ALL,
					FINDER_ARGS_EMPTY);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	/**
	 * Initializes the patcher product version persistence.
	 */
	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.util.service.ServiceProps.get(
						"value.object.listener.com.liferay.osb.patcher.model.PatcherProductVersion")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<PatcherProductVersion>> listenersList = new ArrayList<ModelListener<PatcherProductVersion>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<PatcherProductVersion>)InstanceFactory.newInstance(
							getClassLoader(), listenerClassName));
				}

				listeners = listenersList.toArray(new ModelListener[listenersList.size()]);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
	}

	public void destroy() {
		EntityCacheUtil.removeCache(PatcherProductVersionImpl.class.getName());
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	private static final String _SQL_SELECT_PATCHERPRODUCTVERSION = "SELECT patcherProductVersion FROM PatcherProductVersion patcherProductVersion";
	private static final String _SQL_COUNT_PATCHERPRODUCTVERSION = "SELECT COUNT(patcherProductVersion) FROM PatcherProductVersion patcherProductVersion";
	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherProductVersion.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No PatcherProductVersion exists with the primary key ";
	private static final boolean _HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = GetterUtil.getBoolean(PropsUtil.get(
				PropsKeys.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
	private static Log _log = LogFactoryUtil.getLog(PatcherProductVersionPersistenceImpl.class);
	private static PatcherProductVersion _nullPatcherProductVersion = new PatcherProductVersionImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<PatcherProductVersion> toCacheModel() {
				return _nullPatcherProductVersionCacheModel;
			}
		};

	private static CacheModel<PatcherProductVersion> _nullPatcherProductVersionCacheModel =
		new CacheModel<PatcherProductVersion>() {
			@Override
			public PatcherProductVersion toEntityModel() {
				return _nullPatcherProductVersion;
			}
		};
}