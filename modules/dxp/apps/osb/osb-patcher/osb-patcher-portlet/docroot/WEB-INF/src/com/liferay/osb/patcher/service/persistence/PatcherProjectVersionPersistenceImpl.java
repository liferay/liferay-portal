/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.NoSuchPatcherProjectVersionException;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.model.impl.PatcherProjectVersionImpl;
import com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl;

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
 * The persistence implementation for the patcher project version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherProjectVersionPersistence
 * @see PatcherProjectVersionUtil
 * @generated
 */
public class PatcherProjectVersionPersistenceImpl extends BasePersistenceImpl<PatcherProjectVersion>
	implements PatcherProjectVersionPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link PatcherProjectVersionUtil} to access the patcher project version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = PatcherProjectVersionImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(PatcherProjectVersionModelImpl.ENTITY_CACHE_ENABLED,
			PatcherProjectVersionModelImpl.FINDER_CACHE_ENABLED,
			PatcherProjectVersionImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(PatcherProjectVersionModelImpl.ENTITY_CACHE_ENABLED,
			PatcherProjectVersionModelImpl.FINDER_CACHE_ENABLED,
			PatcherProjectVersionImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(PatcherProjectVersionModelImpl.ENTITY_CACHE_ENABLED,
			PatcherProjectVersionModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);

	public PatcherProjectVersionPersistenceImpl() {
		setModelClass(PatcherProjectVersion.class);
	}

	/**
	 * Caches the patcher project version in the entity cache if it is enabled.
	 *
	 * @param patcherProjectVersion the patcher project version
	 */
	@Override
	public void cacheResult(PatcherProjectVersion patcherProjectVersion) {
		EntityCacheUtil.putResult(PatcherProjectVersionModelImpl.ENTITY_CACHE_ENABLED,
			PatcherProjectVersionImpl.class,
			patcherProjectVersion.getPrimaryKey(), patcherProjectVersion);

		patcherProjectVersion.resetOriginalValues();
	}

	/**
	 * Caches the patcher project versions in the entity cache if it is enabled.
	 *
	 * @param patcherProjectVersions the patcher project versions
	 */
	@Override
	public void cacheResult(List<PatcherProjectVersion> patcherProjectVersions) {
		for (PatcherProjectVersion patcherProjectVersion : patcherProjectVersions) {
			if (EntityCacheUtil.getResult(
						PatcherProjectVersionModelImpl.ENTITY_CACHE_ENABLED,
						PatcherProjectVersionImpl.class,
						patcherProjectVersion.getPrimaryKey()) == null) {
				cacheResult(patcherProjectVersion);
			}
			else {
				patcherProjectVersion.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all patcher project versions.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		if (_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			CacheRegistryUtil.clear(PatcherProjectVersionImpl.class.getName());
		}

		EntityCacheUtil.clearCache(PatcherProjectVersionImpl.class.getName());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the patcher project version.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherProjectVersion patcherProjectVersion) {
		EntityCacheUtil.removeResult(PatcherProjectVersionModelImpl.ENTITY_CACHE_ENABLED,
			PatcherProjectVersionImpl.class,
			patcherProjectVersion.getPrimaryKey());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<PatcherProjectVersion> patcherProjectVersions) {
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (PatcherProjectVersion patcherProjectVersion : patcherProjectVersions) {
			EntityCacheUtil.removeResult(PatcherProjectVersionModelImpl.ENTITY_CACHE_ENABLED,
				PatcherProjectVersionImpl.class,
				patcherProjectVersion.getPrimaryKey());
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
		PatcherProjectVersion patcherProjectVersion = new PatcherProjectVersionImpl();

		patcherProjectVersion.setNew(true);
		patcherProjectVersion.setPrimaryKey(patcherProjectVersionId);

		return patcherProjectVersion;
	}

	/**
	 * Removes the patcher project version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherProjectVersion remove(long patcherProjectVersionId)
		throws NoSuchPatcherProjectVersionException, SystemException {
		return remove((Serializable)patcherProjectVersionId);
	}

	/**
	 * Removes the patcher project version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher project version
	 * @return the patcher project version that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherProjectVersion remove(Serializable primaryKey)
		throws NoSuchPatcherProjectVersionException, SystemException {
		Session session = null;

		try {
			session = openSession();

			PatcherProjectVersion patcherProjectVersion = (PatcherProjectVersion)session.get(PatcherProjectVersionImpl.class,
					primaryKey);

			if (patcherProjectVersion == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherProjectVersionException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(patcherProjectVersion);
		}
		catch (NoSuchPatcherProjectVersionException nsee) {
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
	protected PatcherProjectVersion removeImpl(
		PatcherProjectVersion patcherProjectVersion) throws SystemException {
		patcherProjectVersion = toUnwrappedModel(patcherProjectVersion);

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherProjectVersion)) {
				patcherProjectVersion = (PatcherProjectVersion)session.get(PatcherProjectVersionImpl.class,
						patcherProjectVersion.getPrimaryKeyObj());
			}

			if (patcherProjectVersion != null) {
				session.delete(patcherProjectVersion);
			}
		}
		catch (Exception e) {
			throw processException(e);
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
		com.liferay.osb.patcher.model.PatcherProjectVersion patcherProjectVersion)
		throws SystemException {
		patcherProjectVersion = toUnwrappedModel(patcherProjectVersion);

		boolean isNew = patcherProjectVersion.isNew();

		Session session = null;

		try {
			session = openSession();

			if (patcherProjectVersion.isNew()) {
				session.save(patcherProjectVersion);

				patcherProjectVersion.setNew(false);
			}
			else {
				session.merge(patcherProjectVersion);
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

		EntityCacheUtil.putResult(PatcherProjectVersionModelImpl.ENTITY_CACHE_ENABLED,
			PatcherProjectVersionImpl.class,
			patcherProjectVersion.getPrimaryKey(), patcherProjectVersion);

		return patcherProjectVersion;
	}

	protected PatcherProjectVersion toUnwrappedModel(
		PatcherProjectVersion patcherProjectVersion) {
		if (patcherProjectVersion instanceof PatcherProjectVersionImpl) {
			return patcherProjectVersion;
		}

		PatcherProjectVersionImpl patcherProjectVersionImpl = new PatcherProjectVersionImpl();

		patcherProjectVersionImpl.setNew(patcherProjectVersion.isNew());
		patcherProjectVersionImpl.setPrimaryKey(patcherProjectVersion.getPrimaryKey());

		patcherProjectVersionImpl.setPatcherProjectVersionId(patcherProjectVersion.getPatcherProjectVersionId());
		patcherProjectVersionImpl.setCompanyId(patcherProjectVersion.getCompanyId());
		patcherProjectVersionImpl.setUserId(patcherProjectVersion.getUserId());
		patcherProjectVersionImpl.setUserName(patcherProjectVersion.getUserName());
		patcherProjectVersionImpl.setCreateDate(patcherProjectVersion.getCreateDate());
		patcherProjectVersionImpl.setModifiedDate(patcherProjectVersion.getModifiedDate());
		patcherProjectVersionImpl.setPatcherProductVersionId(patcherProjectVersion.getPatcherProductVersionId());
		patcherProjectVersionImpl.setRootPatcherProjectVersionId(patcherProjectVersion.getRootPatcherProjectVersionId());
		patcherProjectVersionImpl.setName(patcherProjectVersion.getName());
		patcherProjectVersionImpl.setCombinedBranch(patcherProjectVersion.isCombinedBranch());
		patcherProjectVersionImpl.setHide(patcherProjectVersion.isHide());
		patcherProjectVersionImpl.setCommittish(patcherProjectVersion.getCommittish());
		patcherProjectVersionImpl.setRepositoryName(patcherProjectVersion.getRepositoryName());
		patcherProjectVersionImpl.setFixedIssues(patcherProjectVersion.getFixedIssues());
		patcherProjectVersionImpl.setProductVersion(patcherProjectVersion.getProductVersion());

		return patcherProjectVersionImpl;
	}

	/**
	 * Returns the patcher project version with the primary key or throws a {@link com.liferay.portal.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher project version
	 * @return the patcher project version
	 * @throws com.liferay.osb.patcher.NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherProjectVersion findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherProjectVersionException, SystemException {
		PatcherProjectVersion patcherProjectVersion = fetchByPrimaryKey(primaryKey);

		if (patcherProjectVersion == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherProjectVersionException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return patcherProjectVersion;
	}

	/**
	 * Returns the patcher project version with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherProjectVersionException} if it could not be found.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version
	 * @throws com.liferay.osb.patcher.NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherProjectVersion findByPrimaryKey(long patcherProjectVersionId)
		throws NoSuchPatcherProjectVersionException, SystemException {
		return findByPrimaryKey((Serializable)patcherProjectVersionId);
	}

	/**
	 * Returns the patcher project version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher project version
	 * @return the patcher project version, or <code>null</code> if a patcher project version with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherProjectVersion fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		PatcherProjectVersion patcherProjectVersion = (PatcherProjectVersion)EntityCacheUtil.getResult(PatcherProjectVersionModelImpl.ENTITY_CACHE_ENABLED,
				PatcherProjectVersionImpl.class, primaryKey);

		if (patcherProjectVersion == _nullPatcherProjectVersion) {
			return null;
		}

		if (patcherProjectVersion == null) {
			Session session = null;

			try {
				session = openSession();

				patcherProjectVersion = (PatcherProjectVersion)session.get(PatcherProjectVersionImpl.class,
						primaryKey);

				if (patcherProjectVersion != null) {
					cacheResult(patcherProjectVersion);
				}
				else {
					EntityCacheUtil.putResult(PatcherProjectVersionModelImpl.ENTITY_CACHE_ENABLED,
						PatcherProjectVersionImpl.class, primaryKey,
						_nullPatcherProjectVersion);
				}
			}
			catch (Exception e) {
				EntityCacheUtil.removeResult(PatcherProjectVersionModelImpl.ENTITY_CACHE_ENABLED,
					PatcherProjectVersionImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return patcherProjectVersion;
	}

	/**
	 * Returns the patcher project version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version, or <code>null</code> if a patcher project version with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherProjectVersion fetchByPrimaryKey(long patcherProjectVersionId)
		throws SystemException {
		return fetchByPrimaryKey((Serializable)patcherProjectVersionId);
	}

	/**
	 * Returns all the patcher project versions.
	 *
	 * @return the patcher project versions
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherProjectVersion> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of patcher project versions
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherProjectVersion> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherProjectVersionModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher project versions
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherProjectVersion> findAll(int start, int end,
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

		List<PatcherProjectVersion> list = (List<PatcherProjectVersion>)FinderCacheUtil.getResult(finderPath,
				finderArgs, this);

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 3));

				query.append(_SQL_SELECT_PATCHERPROJECTVERSION);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERPROJECTVERSION;

				if (pagination) {
					sql = sql.concat(PatcherProjectVersionModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<PatcherProjectVersion>)QueryUtil.list(q,
							getDialect(), start, end, false);

					Collections.sort(list);

					list = new UnmodifiableList<PatcherProjectVersion>(list);
				}
				else {
					list = (List<PatcherProjectVersion>)QueryUtil.list(q,
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
	 * Removes all the patcher project versions from the database.
	 *
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removeAll() throws SystemException {
		for (PatcherProjectVersion patcherProjectVersion : findAll()) {
			remove(patcherProjectVersion);
		}
	}

	/**
	 * Returns the number of patcher project versions.
	 *
	 * @return the number of patcher project versions
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

				Query q = session.createQuery(_SQL_COUNT_PATCHERPROJECTVERSION);

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
	 * Initializes the patcher project version persistence.
	 */
	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.util.service.ServiceProps.get(
						"value.object.listener.com.liferay.osb.patcher.model.PatcherProjectVersion")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<PatcherProjectVersion>> listenersList = new ArrayList<ModelListener<PatcherProjectVersion>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<PatcherProjectVersion>)InstanceFactory.newInstance(
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
		EntityCacheUtil.removeCache(PatcherProjectVersionImpl.class.getName());
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	private static final String _SQL_SELECT_PATCHERPROJECTVERSION = "SELECT patcherProjectVersion FROM PatcherProjectVersion patcherProjectVersion";
	private static final String _SQL_COUNT_PATCHERPROJECTVERSION = "SELECT COUNT(patcherProjectVersion) FROM PatcherProjectVersion patcherProjectVersion";
	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherProjectVersion.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No PatcherProjectVersion exists with the primary key ";
	private static final boolean _HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = GetterUtil.getBoolean(PropsUtil.get(
				PropsKeys.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
	private static Log _log = LogFactoryUtil.getLog(PatcherProjectVersionPersistenceImpl.class);
	private static PatcherProjectVersion _nullPatcherProjectVersion = new PatcherProjectVersionImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<PatcherProjectVersion> toCacheModel() {
				return _nullPatcherProjectVersionCacheModel;
			}
		};

	private static CacheModel<PatcherProjectVersion> _nullPatcherProjectVersionCacheModel =
		new CacheModel<PatcherProjectVersion>() {
			@Override
			public PatcherProjectVersion toEntityModel() {
				return _nullPatcherProjectVersion;
			}
		};
}