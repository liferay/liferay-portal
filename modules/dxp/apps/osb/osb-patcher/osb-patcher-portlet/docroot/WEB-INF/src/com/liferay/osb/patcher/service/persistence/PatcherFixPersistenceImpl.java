/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.NoSuchPatcherFixException;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.impl.PatcherFixImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixModelImpl;

import com.liferay.portal.kernel.bean.BeanReference;
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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnmodifiableList;
import com.liferay.portal.model.CacheModel;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.service.persistence.impl.TableMapper;
import com.liferay.portal.service.persistence.impl.TableMapperFactory;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The persistence implementation for the patcher fix service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixPersistence
 * @see PatcherFixUtil
 * @generated
 */
public class PatcherFixPersistenceImpl extends BasePersistenceImpl<PatcherFix>
	implements PatcherFixPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link PatcherFixUtil} to access the patcher fix persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = PatcherFixImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(PatcherFixModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixModelImpl.FINDER_CACHE_ENABLED, PatcherFixImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(PatcherFixModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixModelImpl.FINDER_CACHE_ENABLED, PatcherFixImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(PatcherFixModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);

	public PatcherFixPersistenceImpl() {
		setModelClass(PatcherFix.class);
	}

	/**
	 * Caches the patcher fix in the entity cache if it is enabled.
	 *
	 * @param patcherFix the patcher fix
	 */
	@Override
	public void cacheResult(PatcherFix patcherFix) {
		EntityCacheUtil.putResult(PatcherFixModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixImpl.class, patcherFix.getPrimaryKey(), patcherFix);

		patcherFix.resetOriginalValues();
	}

	/**
	 * Caches the patcher fixs in the entity cache if it is enabled.
	 *
	 * @param patcherFixs the patcher fixs
	 */
	@Override
	public void cacheResult(List<PatcherFix> patcherFixs) {
		for (PatcherFix patcherFix : patcherFixs) {
			if (EntityCacheUtil.getResult(
						PatcherFixModelImpl.ENTITY_CACHE_ENABLED,
						PatcherFixImpl.class, patcherFix.getPrimaryKey()) == null) {
				cacheResult(patcherFix);
			}
			else {
				patcherFix.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all patcher fixs.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		if (_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			CacheRegistryUtil.clear(PatcherFixImpl.class.getName());
		}

		EntityCacheUtil.clearCache(PatcherFixImpl.class.getName());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the patcher fix.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherFix patcherFix) {
		EntityCacheUtil.removeResult(PatcherFixModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixImpl.class, patcherFix.getPrimaryKey());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<PatcherFix> patcherFixs) {
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (PatcherFix patcherFix : patcherFixs) {
			EntityCacheUtil.removeResult(PatcherFixModelImpl.ENTITY_CACHE_ENABLED,
				PatcherFixImpl.class, patcherFix.getPrimaryKey());
		}
	}

	/**
	 * Creates a new patcher fix with the primary key. Does not add the patcher fix to the database.
	 *
	 * @param patcherFixId the primary key for the new patcher fix
	 * @return the new patcher fix
	 */
	@Override
	public PatcherFix create(long patcherFixId) {
		PatcherFix patcherFix = new PatcherFixImpl();

		patcherFix.setNew(true);
		patcherFix.setPrimaryKey(patcherFixId);

		return patcherFix;
	}

	/**
	 * Removes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFix remove(long patcherFixId)
		throws NoSuchPatcherFixException, SystemException {
		return remove((Serializable)patcherFixId);
	}

	/**
	 * Removes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher fix
	 * @return the patcher fix that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFix remove(Serializable primaryKey)
		throws NoSuchPatcherFixException, SystemException {
		Session session = null;

		try {
			session = openSession();

			PatcherFix patcherFix = (PatcherFix)session.get(PatcherFixImpl.class,
					primaryKey);

			if (patcherFix == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherFixException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(patcherFix);
		}
		catch (NoSuchPatcherFixException nsee) {
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
	protected PatcherFix removeImpl(PatcherFix patcherFix)
		throws SystemException {
		patcherFix = toUnwrappedModel(patcherFix);

		patcherFixToPatcherBuildTableMapper.deleteLeftPrimaryKeyTableMappings(patcherFix.getPrimaryKey());

		patcherFixToPatcherFixPackTableMapper.deleteLeftPrimaryKeyTableMappings(patcherFix.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFix)) {
				patcherFix = (PatcherFix)session.get(PatcherFixImpl.class,
						patcherFix.getPrimaryKeyObj());
			}

			if (patcherFix != null) {
				session.delete(patcherFix);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (patcherFix != null) {
			clearCache(patcherFix);
		}

		return patcherFix;
	}

	@Override
	public PatcherFix updateImpl(
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws SystemException {
		patcherFix = toUnwrappedModel(patcherFix);

		boolean isNew = patcherFix.isNew();

		Session session = null;

		try {
			session = openSession();

			if (patcherFix.isNew()) {
				session.save(patcherFix);

				patcherFix.setNew(false);
			}
			else {
				session.merge(patcherFix);
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

		EntityCacheUtil.putResult(PatcherFixModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixImpl.class, patcherFix.getPrimaryKey(), patcherFix);

		return patcherFix;
	}

	protected PatcherFix toUnwrappedModel(PatcherFix patcherFix) {
		if (patcherFix instanceof PatcherFixImpl) {
			return patcherFix;
		}

		PatcherFixImpl patcherFixImpl = new PatcherFixImpl();

		patcherFixImpl.setNew(patcherFix.isNew());
		patcherFixImpl.setPrimaryKey(patcherFix.getPrimaryKey());

		patcherFixImpl.setPatcherFixId(patcherFix.getPatcherFixId());
		patcherFixImpl.setCompanyId(patcherFix.getCompanyId());
		patcherFixImpl.setUserId(patcherFix.getUserId());
		patcherFixImpl.setUserName(patcherFix.getUserName());
		patcherFixImpl.setCreateDate(patcherFix.getCreateDate());
		patcherFixImpl.setModifiedDate(patcherFix.getModifiedDate());
		patcherFixImpl.setPatcherProductVersionId(patcherFix.getPatcherProductVersionId());
		patcherFixImpl.setPatcherProjectVersionId(patcherFix.getPatcherProjectVersionId());
		patcherFixImpl.setName(patcherFix.getName());
		patcherFixImpl.setKey(patcherFix.getKey());
		patcherFixImpl.setKeyVersion(patcherFix.getKeyVersion());
		patcherFixImpl.setType(patcherFix.getType());
		patcherFixImpl.setLatestFix(patcherFix.isLatestFix());
		patcherFixImpl.setObsolete(patcherFix.isObsolete());
		patcherFixImpl.setCommittish(patcherFix.getCommittish());
		patcherFixImpl.setGitHash(patcherFix.getGitHash());
		patcherFixImpl.setGitRemoteURL(patcherFix.getGitRemoteURL());
		patcherFixImpl.setDependencies(patcherFix.getDependencies());
		patcherFixImpl.setRequirements(patcherFix.getRequirements());
		patcherFixImpl.setRequestKey(patcherFix.getRequestKey());
		patcherFixImpl.setJenkinsResults(patcherFix.getJenkinsResults());
		patcherFixImpl.setComments(patcherFix.getComments());
		patcherFixImpl.setFixPackStatus(patcherFix.getFixPackStatus());
		patcherFixImpl.setNotified(patcherFix.isNotified());
		patcherFixImpl.setProductVersion(patcherFix.getProductVersion());
		patcherFixImpl.setStatus(patcherFix.getStatus());
		patcherFixImpl.setStatusByUserId(patcherFix.getStatusByUserId());
		patcherFixImpl.setStatusByUserName(patcherFix.getStatusByUserName());
		patcherFixImpl.setStatusDate(patcherFix.getStatusDate());

		return patcherFixImpl;
	}

	/**
	 * Returns the patcher fix with the primary key or throws a {@link com.liferay.portal.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix
	 * @return the patcher fix
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFix findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherFixException, SystemException {
		PatcherFix patcherFix = fetchByPrimaryKey(primaryKey);

		if (patcherFix == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherFixException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return patcherFix;
	}

	/**
	 * Returns the patcher fix with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixException} if it could not be found.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFix findByPrimaryKey(long patcherFixId)
		throws NoSuchPatcherFixException, SystemException {
		return findByPrimaryKey((Serializable)patcherFixId);
	}

	/**
	 * Returns the patcher fix with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix
	 * @return the patcher fix, or <code>null</code> if a patcher fix with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFix fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		PatcherFix patcherFix = (PatcherFix)EntityCacheUtil.getResult(PatcherFixModelImpl.ENTITY_CACHE_ENABLED,
				PatcherFixImpl.class, primaryKey);

		if (patcherFix == _nullPatcherFix) {
			return null;
		}

		if (patcherFix == null) {
			Session session = null;

			try {
				session = openSession();

				patcherFix = (PatcherFix)session.get(PatcherFixImpl.class,
						primaryKey);

				if (patcherFix != null) {
					cacheResult(patcherFix);
				}
				else {
					EntityCacheUtil.putResult(PatcherFixModelImpl.ENTITY_CACHE_ENABLED,
						PatcherFixImpl.class, primaryKey, _nullPatcherFix);
				}
			}
			catch (Exception e) {
				EntityCacheUtil.removeResult(PatcherFixModelImpl.ENTITY_CACHE_ENABLED,
					PatcherFixImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return patcherFix;
	}

	/**
	 * Returns the patcher fix with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix, or <code>null</code> if a patcher fix with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFix fetchByPrimaryKey(long patcherFixId)
		throws SystemException {
		return fetchByPrimaryKey((Serializable)patcherFixId);
	}

	/**
	 * Returns all the patcher fixs.
	 *
	 * @return the patcher fixs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherFix> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fixs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixs
	 * @param end the upper bound of the range of patcher fixs (not inclusive)
	 * @return the range of patcher fixs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherFix> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixs
	 * @param end the upper bound of the range of patcher fixs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherFix> findAll(int start, int end,
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

		List<PatcherFix> list = (List<PatcherFix>)FinderCacheUtil.getResult(finderPath,
				finderArgs, this);

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 3));

				query.append(_SQL_SELECT_PATCHERFIX);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERFIX;

				if (pagination) {
					sql = sql.concat(PatcherFixModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<PatcherFix>)QueryUtil.list(q, getDialect(),
							start, end, false);

					Collections.sort(list);

					list = new UnmodifiableList<PatcherFix>(list);
				}
				else {
					list = (List<PatcherFix>)QueryUtil.list(q, getDialect(),
							start, end);
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
	 * Removes all the patcher fixs from the database.
	 *
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removeAll() throws SystemException {
		for (PatcherFix patcherFix : findAll()) {
			remove(patcherFix);
		}
	}

	/**
	 * Returns the number of patcher fixs.
	 *
	 * @return the number of patcher fixs
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

				Query q = session.createQuery(_SQL_COUNT_PATCHERFIX);

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
	 * Returns all the patcher builds associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher builds associated with the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk) throws SystemException {
		return getPatcherBuilds(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the patcher builds associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixs
	 * @param end the upper bound of the range of patcher fixs (not inclusive)
	 * @return the range of patcher builds associated with the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk, int start, int end) throws SystemException {
		return getPatcherBuilds(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixs
	 * @param end the upper bound of the range of patcher fixs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk, int start, int end, OrderByComparator orderByComparator)
		throws SystemException {
		return patcherFixToPatcherBuildTableMapper.getRightBaseModels(pk,
			start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher builds associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the number of patcher builds associated with the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getPatcherBuildsSize(long pk) throws SystemException {
		long[] pks = patcherFixToPatcherBuildTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher build is associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if the patcher build is associated with the patcher fix; <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean containsPatcherBuild(long pk, long patcherBuildPK)
		throws SystemException {
		return patcherFixToPatcherBuildTableMapper.containsTableMapping(pk,
			patcherBuildPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix has any patcher builds associated with it.
	 *
	 * @param pk the primary key of the patcher fix to check for associations with patcher builds
	 * @return <code>true</code> if the patcher fix has any patcher builds associated with it; <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean containsPatcherBuilds(long pk) throws SystemException {
		if (getPatcherBuildsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherBuild(long pk, long patcherBuildPK)
		throws SystemException {
		patcherFixToPatcherBuildTableMapper.addTableMapping(pk, patcherBuildPK);
	}

	/**
	 * Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuild the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherBuild(long pk,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws SystemException {
		patcherFixToPatcherBuildTableMapper.addTableMapping(pk,
			patcherBuild.getPrimaryKey());
	}

	/**
	 * Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherBuilds(long pk, long[] patcherBuildPKs)
		throws SystemException {
		for (long patcherBuildPK : patcherBuildPKs) {
			patcherFixToPatcherBuildTableMapper.addTableMapping(pk,
				patcherBuildPK);
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherBuilds(long pk,
		List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws SystemException {
		for (com.liferay.osb.patcher.model.PatcherBuild patcherBuild : patcherBuilds) {
			patcherFixToPatcherBuildTableMapper.addTableMapping(pk,
				patcherBuild.getPrimaryKey());
		}
	}

	/**
	 * Clears all associations between the patcher fix and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix to clear the associated patcher builds from
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void clearPatcherBuilds(long pk) throws SystemException {
		patcherFixToPatcherBuildTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherBuild(long pk, long patcherBuildPK)
		throws SystemException {
		patcherFixToPatcherBuildTableMapper.deleteTableMapping(pk,
			patcherBuildPK);
	}

	/**
	 * Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuild the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherBuild(long pk,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws SystemException {
		patcherFixToPatcherBuildTableMapper.deleteTableMapping(pk,
			patcherBuild.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherBuilds(long pk, long[] patcherBuildPKs)
		throws SystemException {
		for (long patcherBuildPK : patcherBuildPKs) {
			patcherFixToPatcherBuildTableMapper.deleteTableMapping(pk,
				patcherBuildPK);
		}
	}

	/**
	 * Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherBuilds(long pk,
		List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws SystemException {
		for (com.liferay.osb.patcher.model.PatcherBuild patcherBuild : patcherBuilds) {
			patcherFixToPatcherBuildTableMapper.deleteTableMapping(pk,
				patcherBuild.getPrimaryKey());
		}
	}

	/**
	 * Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void setPatcherBuilds(long pk, long[] patcherBuildPKs)
		throws SystemException {
		Set<Long> newPatcherBuildPKsSet = SetUtil.fromArray(patcherBuildPKs);
		Set<Long> oldPatcherBuildPKsSet = SetUtil.fromArray(patcherFixToPatcherBuildTableMapper.getRightPrimaryKeys(
					pk));

		Set<Long> removePatcherBuildPKsSet = new HashSet<Long>(oldPatcherBuildPKsSet);

		removePatcherBuildPKsSet.removeAll(newPatcherBuildPKsSet);

		for (long removePatcherBuildPK : removePatcherBuildPKsSet) {
			patcherFixToPatcherBuildTableMapper.deleteTableMapping(pk,
				removePatcherBuildPK);
		}

		newPatcherBuildPKsSet.removeAll(oldPatcherBuildPKsSet);

		for (long newPatcherBuildPK : newPatcherBuildPKsSet) {
			patcherFixToPatcherBuildTableMapper.addTableMapping(pk,
				newPatcherBuildPK);
		}
	}

	/**
	 * Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds to be associated with the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void setPatcherBuilds(long pk,
		List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws SystemException {
		try {
			long[] patcherBuildPKs = new long[patcherBuilds.size()];

			for (int i = 0; i < patcherBuilds.size(); i++) {
				com.liferay.osb.patcher.model.PatcherBuild patcherBuild = patcherBuilds.get(i);

				patcherBuildPKs[i] = patcherBuild.getPrimaryKey();
			}

			setPatcherBuilds(pk, patcherBuildPKs);
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			FinderCacheUtil.clearCache(PatcherFixModelImpl.MAPPING_TABLE_OSB_PATCHERBUILDS_PATCHERFIXES_NAME);
		}
	}

	/**
	 * Returns all the patcher fix packs associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher fix packs associated with the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPacks(
		long pk) throws SystemException {
		return getPatcherFixPacks(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the patcher fix packs associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixs
	 * @param end the upper bound of the range of patcher fixs (not inclusive)
	 * @return the range of patcher fix packs associated with the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPacks(
		long pk, int start, int end) throws SystemException {
		return getPatcherFixPacks(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixs
	 * @param end the upper bound of the range of patcher fixs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix packs associated with the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherFixPack> getPatcherFixPacks(
		long pk, int start, int end, OrderByComparator orderByComparator)
		throws SystemException {
		return patcherFixToPatcherFixPackTableMapper.getRightBaseModels(pk,
			start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fix packs associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the number of patcher fix packs associated with the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getPatcherFixPacksSize(long pk) throws SystemException {
		long[] pks = patcherFixToPatcherFixPackTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher fix pack is associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 * @return <code>true</code> if the patcher fix pack is associated with the patcher fix; <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean containsPatcherFixPack(long pk, long patcherFixPackPK)
		throws SystemException {
		return patcherFixToPatcherFixPackTableMapper.containsTableMapping(pk,
			patcherFixPackPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix has any patcher fix packs associated with it.
	 *
	 * @param pk the primary key of the patcher fix to check for associations with patcher fix packs
	 * @return <code>true</code> if the patcher fix has any patcher fix packs associated with it; <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean containsPatcherFixPacks(long pk) throws SystemException {
		if (getPatcherFixPacksSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherFixPack(long pk, long patcherFixPackPK)
		throws SystemException {
		patcherFixToPatcherFixPackTableMapper.addTableMapping(pk,
			patcherFixPackPK);
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPack the patcher fix pack
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherFixPack(long pk,
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws SystemException {
		patcherFixToPatcherFixPackTableMapper.addTableMapping(pk,
			patcherFixPack.getPrimaryKey());
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherFixPacks(long pk, long[] patcherFixPackPKs)
		throws SystemException {
		for (long patcherFixPackPK : patcherFixPackPKs) {
			patcherFixToPatcherFixPackTableMapper.addTableMapping(pk,
				patcherFixPackPK);
		}
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherFixPacks(long pk,
		List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws SystemException {
		for (com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack : patcherFixPacks) {
			patcherFixToPatcherFixPackTableMapper.addTableMapping(pk,
				patcherFixPack.getPrimaryKey());
		}
	}

	/**
	 * Clears all associations between the patcher fix and its patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix to clear the associated patcher fix packs from
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void clearPatcherFixPacks(long pk) throws SystemException {
		patcherFixToPatcherFixPackTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherFixPack(long pk, long patcherFixPackPK)
		throws SystemException {
		patcherFixToPatcherFixPackTableMapper.deleteTableMapping(pk,
			patcherFixPackPK);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPack the patcher fix pack
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherFixPack(long pk,
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws SystemException {
		patcherFixToPatcherFixPackTableMapper.deleteTableMapping(pk,
			patcherFixPack.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherFixPacks(long pk, long[] patcherFixPackPKs)
		throws SystemException {
		for (long patcherFixPackPK : patcherFixPackPKs) {
			patcherFixToPatcherFixPackTableMapper.deleteTableMapping(pk,
				patcherFixPackPK);
		}
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherFixPacks(long pk,
		List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws SystemException {
		for (com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack : patcherFixPacks) {
			patcherFixToPatcherFixPackTableMapper.deleteTableMapping(pk,
				patcherFixPack.getPrimaryKey());
		}
	}

	/**
	 * Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs to be associated with the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void setPatcherFixPacks(long pk, long[] patcherFixPackPKs)
		throws SystemException {
		Set<Long> newPatcherFixPackPKsSet = SetUtil.fromArray(patcherFixPackPKs);
		Set<Long> oldPatcherFixPackPKsSet = SetUtil.fromArray(patcherFixToPatcherFixPackTableMapper.getRightPrimaryKeys(
					pk));

		Set<Long> removePatcherFixPackPKsSet = new HashSet<Long>(oldPatcherFixPackPKsSet);

		removePatcherFixPackPKsSet.removeAll(newPatcherFixPackPKsSet);

		for (long removePatcherFixPackPK : removePatcherFixPackPKsSet) {
			patcherFixToPatcherFixPackTableMapper.deleteTableMapping(pk,
				removePatcherFixPackPK);
		}

		newPatcherFixPackPKsSet.removeAll(oldPatcherFixPackPKsSet);

		for (long newPatcherFixPackPK : newPatcherFixPackPKsSet) {
			patcherFixToPatcherFixPackTableMapper.addTableMapping(pk,
				newPatcherFixPackPK);
		}
	}

	/**
	 * Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs to be associated with the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void setPatcherFixPacks(long pk,
		List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks)
		throws SystemException {
		try {
			long[] patcherFixPackPKs = new long[patcherFixPacks.size()];

			for (int i = 0; i < patcherFixPacks.size(); i++) {
				com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack = patcherFixPacks.get(i);

				patcherFixPackPKs[i] = patcherFixPack.getPrimaryKey();
			}

			setPatcherFixPacks(pk, patcherFixPackPKs);
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			FinderCacheUtil.clearCache(PatcherFixModelImpl.MAPPING_TABLE_OSB_PATCHERFIXES_PATCHERFIXPACKS_NAME);
		}
	}

	@Override
	protected Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	/**
	 * Initializes the patcher fix persistence.
	 */
	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.util.service.ServiceProps.get(
						"value.object.listener.com.liferay.osb.patcher.model.PatcherFix")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<PatcherFix>> listenersList = new ArrayList<ModelListener<PatcherFix>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<PatcherFix>)InstanceFactory.newInstance(
							getClassLoader(), listenerClassName));
				}

				listeners = listenersList.toArray(new ModelListener[listenersList.size()]);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}

		patcherFixToPatcherBuildTableMapper = TableMapperFactory.getTableMapper("OSB_PatcherBuilds_PatcherFixes",
				"patcherFixId", "patcherBuildId", this, patcherBuildPersistence);

		patcherFixToPatcherFixPackTableMapper = TableMapperFactory.getTableMapper("OSB_PatcherFixes_PatcherFixPacks",
				"patcherFixId", "patcherFixPackId", this,
				patcherFixPackPersistence);
	}

	public void destroy() {
		EntityCacheUtil.removeCache(PatcherFixImpl.class.getName());
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		TableMapperFactory.removeTableMapper("OSB_PatcherBuilds_PatcherFixes");
		TableMapperFactory.removeTableMapper("OSB_PatcherFixes_PatcherFixPacks");
	}

	@BeanReference(type = PatcherBuildPersistence.class)
	protected PatcherBuildPersistence patcherBuildPersistence;
	protected TableMapper<PatcherFix, com.liferay.osb.patcher.model.PatcherBuild> patcherFixToPatcherBuildTableMapper;
	@BeanReference(type = PatcherFixPackPersistence.class)
	protected PatcherFixPackPersistence patcherFixPackPersistence;
	protected TableMapper<PatcherFix, com.liferay.osb.patcher.model.PatcherFixPack> patcherFixToPatcherFixPackTableMapper;
	private static final String _SQL_SELECT_PATCHERFIX = "SELECT patcherFix FROM PatcherFix patcherFix";
	private static final String _SQL_COUNT_PATCHERFIX = "SELECT COUNT(patcherFix) FROM PatcherFix patcherFix";
	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherFix.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No PatcherFix exists with the primary key ";
	private static final boolean _HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = GetterUtil.getBoolean(PropsUtil.get(
				PropsKeys.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
	private static Log _log = LogFactoryUtil.getLog(PatcherFixPersistenceImpl.class);
	private static Set<String> _badColumnNames = SetUtil.fromArray(new String[] {
				"key", "type"
			});
	private static PatcherFix _nullPatcherFix = new PatcherFixImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<PatcherFix> toCacheModel() {
				return _nullPatcherFixCacheModel;
			}
		};

	private static CacheModel<PatcherFix> _nullPatcherFixCacheModel = new CacheModel<PatcherFix>() {
			@Override
			public PatcherFix toEntityModel() {
				return _nullPatcherFix;
			}
		};
}