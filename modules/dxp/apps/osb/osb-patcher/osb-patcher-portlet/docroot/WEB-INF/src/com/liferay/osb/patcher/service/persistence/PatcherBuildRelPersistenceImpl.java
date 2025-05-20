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

import com.liferay.osb.patcher.NoSuchPatcherBuildRelException;
import com.liferay.osb.patcher.model.PatcherBuildRel;
import com.liferay.osb.patcher.model.impl.PatcherBuildRelImpl;
import com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl;

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
 * The persistence implementation for the patcher build rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherBuildRelPersistence
 * @see PatcherBuildRelUtil
 * @generated
 */
public class PatcherBuildRelPersistenceImpl extends BasePersistenceImpl<PatcherBuildRel>
	implements PatcherBuildRelPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link PatcherBuildRelUtil} to access the patcher build rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = PatcherBuildRelImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(PatcherBuildRelModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildRelModelImpl.FINDER_CACHE_ENABLED,
			PatcherBuildRelImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(PatcherBuildRelModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildRelModelImpl.FINDER_CACHE_ENABLED,
			PatcherBuildRelImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(PatcherBuildRelModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildRelModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);

	public PatcherBuildRelPersistenceImpl() {
		setModelClass(PatcherBuildRel.class);
	}

	/**
	 * Caches the patcher build rel in the entity cache if it is enabled.
	 *
	 * @param patcherBuildRel the patcher build rel
	 */
	@Override
	public void cacheResult(PatcherBuildRel patcherBuildRel) {
		EntityCacheUtil.putResult(PatcherBuildRelModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildRelImpl.class, patcherBuildRel.getPrimaryKey(),
			patcherBuildRel);

		patcherBuildRel.resetOriginalValues();
	}

	/**
	 * Caches the patcher build rels in the entity cache if it is enabled.
	 *
	 * @param patcherBuildRels the patcher build rels
	 */
	@Override
	public void cacheResult(List<PatcherBuildRel> patcherBuildRels) {
		for (PatcherBuildRel patcherBuildRel : patcherBuildRels) {
			if (EntityCacheUtil.getResult(
						PatcherBuildRelModelImpl.ENTITY_CACHE_ENABLED,
						PatcherBuildRelImpl.class,
						patcherBuildRel.getPrimaryKey()) == null) {
				cacheResult(patcherBuildRel);
			}
			else {
				patcherBuildRel.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all patcher build rels.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		if (_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			CacheRegistryUtil.clear(PatcherBuildRelImpl.class.getName());
		}

		EntityCacheUtil.clearCache(PatcherBuildRelImpl.class.getName());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the patcher build rel.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherBuildRel patcherBuildRel) {
		EntityCacheUtil.removeResult(PatcherBuildRelModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildRelImpl.class, patcherBuildRel.getPrimaryKey());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<PatcherBuildRel> patcherBuildRels) {
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (PatcherBuildRel patcherBuildRel : patcherBuildRels) {
			EntityCacheUtil.removeResult(PatcherBuildRelModelImpl.ENTITY_CACHE_ENABLED,
				PatcherBuildRelImpl.class, patcherBuildRel.getPrimaryKey());
		}
	}

	/**
	 * Creates a new patcher build rel with the primary key. Does not add the patcher build rel to the database.
	 *
	 * @param patcherBuildRelId the primary key for the new patcher build rel
	 * @return the new patcher build rel
	 */
	@Override
	public PatcherBuildRel create(long patcherBuildRelId) {
		PatcherBuildRel patcherBuildRel = new PatcherBuildRelImpl();

		patcherBuildRel.setNew(true);
		patcherBuildRel.setPrimaryKey(patcherBuildRelId);

		return patcherBuildRel;
	}

	/**
	 * Removes the patcher build rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuildRel remove(long patcherBuildRelId)
		throws NoSuchPatcherBuildRelException, SystemException {
		return remove((Serializable)patcherBuildRelId);
	}

	/**
	 * Removes the patcher build rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher build rel
	 * @return the patcher build rel that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuildRel remove(Serializable primaryKey)
		throws NoSuchPatcherBuildRelException, SystemException {
		Session session = null;

		try {
			session = openSession();

			PatcherBuildRel patcherBuildRel = (PatcherBuildRel)session.get(PatcherBuildRelImpl.class,
					primaryKey);

			if (patcherBuildRel == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherBuildRelException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(patcherBuildRel);
		}
		catch (NoSuchPatcherBuildRelException nsee) {
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
	protected PatcherBuildRel removeImpl(PatcherBuildRel patcherBuildRel)
		throws SystemException {
		patcherBuildRel = toUnwrappedModel(patcherBuildRel);

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherBuildRel)) {
				patcherBuildRel = (PatcherBuildRel)session.get(PatcherBuildRelImpl.class,
						patcherBuildRel.getPrimaryKeyObj());
			}

			if (patcherBuildRel != null) {
				session.delete(patcherBuildRel);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (patcherBuildRel != null) {
			clearCache(patcherBuildRel);
		}

		return patcherBuildRel;
	}

	@Override
	public PatcherBuildRel updateImpl(
		com.liferay.osb.patcher.model.PatcherBuildRel patcherBuildRel)
		throws SystemException {
		patcherBuildRel = toUnwrappedModel(patcherBuildRel);

		boolean isNew = patcherBuildRel.isNew();

		Session session = null;

		try {
			session = openSession();

			if (patcherBuildRel.isNew()) {
				session.save(patcherBuildRel);

				patcherBuildRel.setNew(false);
			}
			else {
				session.merge(patcherBuildRel);
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

		EntityCacheUtil.putResult(PatcherBuildRelModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildRelImpl.class, patcherBuildRel.getPrimaryKey(),
			patcherBuildRel);

		return patcherBuildRel;
	}

	protected PatcherBuildRel toUnwrappedModel(PatcherBuildRel patcherBuildRel) {
		if (patcherBuildRel instanceof PatcherBuildRelImpl) {
			return patcherBuildRel;
		}

		PatcherBuildRelImpl patcherBuildRelImpl = new PatcherBuildRelImpl();

		patcherBuildRelImpl.setNew(patcherBuildRel.isNew());
		patcherBuildRelImpl.setPrimaryKey(patcherBuildRel.getPrimaryKey());

		patcherBuildRelImpl.setPatcherBuildRelId(patcherBuildRel.getPatcherBuildRelId());
		patcherBuildRelImpl.setChildPatcherBuildId(patcherBuildRel.getChildPatcherBuildId());
		patcherBuildRelImpl.setParentPatcherBuildId(patcherBuildRel.getParentPatcherBuildId());

		return patcherBuildRelImpl;
	}

	/**
	 * Returns the patcher build rel with the primary key or throws a {@link com.liferay.portal.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher build rel
	 * @return the patcher build rel
	 * @throws com.liferay.osb.patcher.NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuildRel findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherBuildRelException, SystemException {
		PatcherBuildRel patcherBuildRel = fetchByPrimaryKey(primaryKey);

		if (patcherBuildRel == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherBuildRelException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return patcherBuildRel;
	}

	/**
	 * Returns the patcher build rel with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherBuildRelException} if it could not be found.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel
	 * @throws com.liferay.osb.patcher.NoSuchPatcherBuildRelException if a patcher build rel with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuildRel findByPrimaryKey(long patcherBuildRelId)
		throws NoSuchPatcherBuildRelException, SystemException {
		return findByPrimaryKey((Serializable)patcherBuildRelId);
	}

	/**
	 * Returns the patcher build rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher build rel
	 * @return the patcher build rel, or <code>null</code> if a patcher build rel with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuildRel fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		PatcherBuildRel patcherBuildRel = (PatcherBuildRel)EntityCacheUtil.getResult(PatcherBuildRelModelImpl.ENTITY_CACHE_ENABLED,
				PatcherBuildRelImpl.class, primaryKey);

		if (patcherBuildRel == _nullPatcherBuildRel) {
			return null;
		}

		if (patcherBuildRel == null) {
			Session session = null;

			try {
				session = openSession();

				patcherBuildRel = (PatcherBuildRel)session.get(PatcherBuildRelImpl.class,
						primaryKey);

				if (patcherBuildRel != null) {
					cacheResult(patcherBuildRel);
				}
				else {
					EntityCacheUtil.putResult(PatcherBuildRelModelImpl.ENTITY_CACHE_ENABLED,
						PatcherBuildRelImpl.class, primaryKey,
						_nullPatcherBuildRel);
				}
			}
			catch (Exception e) {
				EntityCacheUtil.removeResult(PatcherBuildRelModelImpl.ENTITY_CACHE_ENABLED,
					PatcherBuildRelImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return patcherBuildRel;
	}

	/**
	 * Returns the patcher build rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherBuildRelId the primary key of the patcher build rel
	 * @return the patcher build rel, or <code>null</code> if a patcher build rel with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuildRel fetchByPrimaryKey(long patcherBuildRelId)
		throws SystemException {
		return fetchByPrimaryKey((Serializable)patcherBuildRelId);
	}

	/**
	 * Returns all the patcher build rels.
	 *
	 * @return the patcher build rels
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherBuildRel> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher build rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @return the range of patcher build rels
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherBuildRel> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher build rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher build rels
	 * @param end the upper bound of the range of patcher build rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher build rels
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherBuildRel> findAll(int start, int end,
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

		List<PatcherBuildRel> list = (List<PatcherBuildRel>)FinderCacheUtil.getResult(finderPath,
				finderArgs, this);

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 3));

				query.append(_SQL_SELECT_PATCHERBUILDREL);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERBUILDREL;

				if (pagination) {
					sql = sql.concat(PatcherBuildRelModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<PatcherBuildRel>)QueryUtil.list(q,
							getDialect(), start, end, false);

					Collections.sort(list);

					list = new UnmodifiableList<PatcherBuildRel>(list);
				}
				else {
					list = (List<PatcherBuildRel>)QueryUtil.list(q,
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
	 * Removes all the patcher build rels from the database.
	 *
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removeAll() throws SystemException {
		for (PatcherBuildRel patcherBuildRel : findAll()) {
			remove(patcherBuildRel);
		}
	}

	/**
	 * Returns the number of patcher build rels.
	 *
	 * @return the number of patcher build rels
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

				Query q = session.createQuery(_SQL_COUNT_PATCHERBUILDREL);

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
	 * Initializes the patcher build rel persistence.
	 */
	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.util.service.ServiceProps.get(
						"value.object.listener.com.liferay.osb.patcher.model.PatcherBuildRel")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<PatcherBuildRel>> listenersList = new ArrayList<ModelListener<PatcherBuildRel>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<PatcherBuildRel>)InstanceFactory.newInstance(
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
		EntityCacheUtil.removeCache(PatcherBuildRelImpl.class.getName());
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	private static final String _SQL_SELECT_PATCHERBUILDREL = "SELECT patcherBuildRel FROM PatcherBuildRel patcherBuildRel";
	private static final String _SQL_COUNT_PATCHERBUILDREL = "SELECT COUNT(patcherBuildRel) FROM PatcherBuildRel patcherBuildRel";
	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherBuildRel.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No PatcherBuildRel exists with the primary key ";
	private static final boolean _HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = GetterUtil.getBoolean(PropsUtil.get(
				PropsKeys.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
	private static Log _log = LogFactoryUtil.getLog(PatcherBuildRelPersistenceImpl.class);
	private static PatcherBuildRel _nullPatcherBuildRel = new PatcherBuildRelImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<PatcherBuildRel> toCacheModel() {
				return _nullPatcherBuildRelCacheModel;
			}
		};

	private static CacheModel<PatcherBuildRel> _nullPatcherBuildRelCacheModel = new CacheModel<PatcherBuildRel>() {
			@Override
			public PatcherBuildRel toEntityModel() {
				return _nullPatcherBuildRel;
			}
		};
}