/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.NoSuchPatcherFixRelException;
import com.liferay.osb.patcher.model.PatcherFixRel;
import com.liferay.osb.patcher.model.impl.PatcherFixRelImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixRelModelImpl;

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
 * The persistence implementation for the patcher fix rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixRelPersistence
 * @see PatcherFixRelUtil
 * @generated
 */
public class PatcherFixRelPersistenceImpl extends BasePersistenceImpl<PatcherFixRel>
	implements PatcherFixRelPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link PatcherFixRelUtil} to access the patcher fix rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = PatcherFixRelImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(PatcherFixRelModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixRelModelImpl.FINDER_CACHE_ENABLED,
			PatcherFixRelImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(PatcherFixRelModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixRelModelImpl.FINDER_CACHE_ENABLED,
			PatcherFixRelImpl.class, FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(PatcherFixRelModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixRelModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);

	public PatcherFixRelPersistenceImpl() {
		setModelClass(PatcherFixRel.class);
	}

	/**
	 * Caches the patcher fix rel in the entity cache if it is enabled.
	 *
	 * @param patcherFixRel the patcher fix rel
	 */
	@Override
	public void cacheResult(PatcherFixRel patcherFixRel) {
		EntityCacheUtil.putResult(PatcherFixRelModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixRelImpl.class, patcherFixRel.getPrimaryKey(),
			patcherFixRel);

		patcherFixRel.resetOriginalValues();
	}

	/**
	 * Caches the patcher fix rels in the entity cache if it is enabled.
	 *
	 * @param patcherFixRels the patcher fix rels
	 */
	@Override
	public void cacheResult(List<PatcherFixRel> patcherFixRels) {
		for (PatcherFixRel patcherFixRel : patcherFixRels) {
			if (EntityCacheUtil.getResult(
						PatcherFixRelModelImpl.ENTITY_CACHE_ENABLED,
						PatcherFixRelImpl.class, patcherFixRel.getPrimaryKey()) == null) {
				cacheResult(patcherFixRel);
			}
			else {
				patcherFixRel.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all patcher fix rels.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		if (_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			CacheRegistryUtil.clear(PatcherFixRelImpl.class.getName());
		}

		EntityCacheUtil.clearCache(PatcherFixRelImpl.class.getName());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the patcher fix rel.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherFixRel patcherFixRel) {
		EntityCacheUtil.removeResult(PatcherFixRelModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixRelImpl.class, patcherFixRel.getPrimaryKey());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<PatcherFixRel> patcherFixRels) {
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (PatcherFixRel patcherFixRel : patcherFixRels) {
			EntityCacheUtil.removeResult(PatcherFixRelModelImpl.ENTITY_CACHE_ENABLED,
				PatcherFixRelImpl.class, patcherFixRel.getPrimaryKey());
		}
	}

	/**
	 * Creates a new patcher fix rel with the primary key. Does not add the patcher fix rel to the database.
	 *
	 * @param patcherFixRelId the primary key for the new patcher fix rel
	 * @return the new patcher fix rel
	 */
	@Override
	public PatcherFixRel create(long patcherFixRelId) {
		PatcherFixRel patcherFixRel = new PatcherFixRelImpl();

		patcherFixRel.setNew(true);
		patcherFixRel.setPrimaryKey(patcherFixRelId);

		return patcherFixRel;
	}

	/**
	 * Removes the patcher fix rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixRel remove(long patcherFixRelId)
		throws NoSuchPatcherFixRelException, SystemException {
		return remove((Serializable)patcherFixRelId);
	}

	/**
	 * Removes the patcher fix rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher fix rel
	 * @return the patcher fix rel that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixRel remove(Serializable primaryKey)
		throws NoSuchPatcherFixRelException, SystemException {
		Session session = null;

		try {
			session = openSession();

			PatcherFixRel patcherFixRel = (PatcherFixRel)session.get(PatcherFixRelImpl.class,
					primaryKey);

			if (patcherFixRel == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherFixRelException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(patcherFixRel);
		}
		catch (NoSuchPatcherFixRelException nsee) {
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
	protected PatcherFixRel removeImpl(PatcherFixRel patcherFixRel)
		throws SystemException {
		patcherFixRel = toUnwrappedModel(patcherFixRel);

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFixRel)) {
				patcherFixRel = (PatcherFixRel)session.get(PatcherFixRelImpl.class,
						patcherFixRel.getPrimaryKeyObj());
			}

			if (patcherFixRel != null) {
				session.delete(patcherFixRel);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (patcherFixRel != null) {
			clearCache(patcherFixRel);
		}

		return patcherFixRel;
	}

	@Override
	public PatcherFixRel updateImpl(
		com.liferay.osb.patcher.model.PatcherFixRel patcherFixRel)
		throws SystemException {
		patcherFixRel = toUnwrappedModel(patcherFixRel);

		boolean isNew = patcherFixRel.isNew();

		Session session = null;

		try {
			session = openSession();

			if (patcherFixRel.isNew()) {
				session.save(patcherFixRel);

				patcherFixRel.setNew(false);
			}
			else {
				session.merge(patcherFixRel);
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

		EntityCacheUtil.putResult(PatcherFixRelModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixRelImpl.class, patcherFixRel.getPrimaryKey(),
			patcherFixRel);

		return patcherFixRel;
	}

	protected PatcherFixRel toUnwrappedModel(PatcherFixRel patcherFixRel) {
		if (patcherFixRel instanceof PatcherFixRelImpl) {
			return patcherFixRel;
		}

		PatcherFixRelImpl patcherFixRelImpl = new PatcherFixRelImpl();

		patcherFixRelImpl.setNew(patcherFixRel.isNew());
		patcherFixRelImpl.setPrimaryKey(patcherFixRel.getPrimaryKey());

		patcherFixRelImpl.setPatcherFixRelId(patcherFixRel.getPatcherFixRelId());
		patcherFixRelImpl.setChildPatcherFixId(patcherFixRel.getChildPatcherFixId());
		patcherFixRelImpl.setParentPatcherFixId(patcherFixRel.getParentPatcherFixId());

		return patcherFixRelImpl;
	}

	/**
	 * Returns the patcher fix rel with the primary key or throws a {@link com.liferay.portal.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix rel
	 * @return the patcher fix rel
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixRel findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherFixRelException, SystemException {
		PatcherFixRel patcherFixRel = fetchByPrimaryKey(primaryKey);

		if (patcherFixRel == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherFixRelException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return patcherFixRel;
	}

	/**
	 * Returns the patcher fix rel with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixRelException} if it could not be found.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixRelException if a patcher fix rel with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixRel findByPrimaryKey(long patcherFixRelId)
		throws NoSuchPatcherFixRelException, SystemException {
		return findByPrimaryKey((Serializable)patcherFixRelId);
	}

	/**
	 * Returns the patcher fix rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix rel
	 * @return the patcher fix rel, or <code>null</code> if a patcher fix rel with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixRel fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		PatcherFixRel patcherFixRel = (PatcherFixRel)EntityCacheUtil.getResult(PatcherFixRelModelImpl.ENTITY_CACHE_ENABLED,
				PatcherFixRelImpl.class, primaryKey);

		if (patcherFixRel == _nullPatcherFixRel) {
			return null;
		}

		if (patcherFixRel == null) {
			Session session = null;

			try {
				session = openSession();

				patcherFixRel = (PatcherFixRel)session.get(PatcherFixRelImpl.class,
						primaryKey);

				if (patcherFixRel != null) {
					cacheResult(patcherFixRel);
				}
				else {
					EntityCacheUtil.putResult(PatcherFixRelModelImpl.ENTITY_CACHE_ENABLED,
						PatcherFixRelImpl.class, primaryKey, _nullPatcherFixRel);
				}
			}
			catch (Exception e) {
				EntityCacheUtil.removeResult(PatcherFixRelModelImpl.ENTITY_CACHE_ENABLED,
					PatcherFixRelImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return patcherFixRel;
	}

	/**
	 * Returns the patcher fix rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixRelId the primary key of the patcher fix rel
	 * @return the patcher fix rel, or <code>null</code> if a patcher fix rel with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixRel fetchByPrimaryKey(long patcherFixRelId)
		throws SystemException {
		return fetchByPrimaryKey((Serializable)patcherFixRelId);
	}

	/**
	 * Returns all the patcher fix rels.
	 *
	 * @return the patcher fix rels
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherFixRel> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @return the range of patcher fix rels
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherFixRel> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixRelModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix rels
	 * @param end the upper bound of the range of patcher fix rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix rels
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherFixRel> findAll(int start, int end,
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

		List<PatcherFixRel> list = (List<PatcherFixRel>)FinderCacheUtil.getResult(finderPath,
				finderArgs, this);

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 3));

				query.append(_SQL_SELECT_PATCHERFIXREL);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERFIXREL;

				if (pagination) {
					sql = sql.concat(PatcherFixRelModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<PatcherFixRel>)QueryUtil.list(q, getDialect(),
							start, end, false);

					Collections.sort(list);

					list = new UnmodifiableList<PatcherFixRel>(list);
				}
				else {
					list = (List<PatcherFixRel>)QueryUtil.list(q, getDialect(),
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
	 * Removes all the patcher fix rels from the database.
	 *
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removeAll() throws SystemException {
		for (PatcherFixRel patcherFixRel : findAll()) {
			remove(patcherFixRel);
		}
	}

	/**
	 * Returns the number of patcher fix rels.
	 *
	 * @return the number of patcher fix rels
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

				Query q = session.createQuery(_SQL_COUNT_PATCHERFIXREL);

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
	 * Initializes the patcher fix rel persistence.
	 */
	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.util.service.ServiceProps.get(
						"value.object.listener.com.liferay.osb.patcher.model.PatcherFixRel")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<PatcherFixRel>> listenersList = new ArrayList<ModelListener<PatcherFixRel>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<PatcherFixRel>)InstanceFactory.newInstance(
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
		EntityCacheUtil.removeCache(PatcherFixRelImpl.class.getName());
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	private static final String _SQL_SELECT_PATCHERFIXREL = "SELECT patcherFixRel FROM PatcherFixRel patcherFixRel";
	private static final String _SQL_COUNT_PATCHERFIXREL = "SELECT COUNT(patcherFixRel) FROM PatcherFixRel patcherFixRel";
	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherFixRel.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No PatcherFixRel exists with the primary key ";
	private static final boolean _HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = GetterUtil.getBoolean(PropsUtil.get(
				PropsKeys.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
	private static Log _log = LogFactoryUtil.getLog(PatcherFixRelPersistenceImpl.class);
	private static PatcherFixRel _nullPatcherFixRel = new PatcherFixRelImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<PatcherFixRel> toCacheModel() {
				return _nullPatcherFixRelCacheModel;
			}
		};

	private static CacheModel<PatcherFixRel> _nullPatcherFixRelCacheModel = new CacheModel<PatcherFixRel>() {
			@Override
			public PatcherFixRel toEntityModel() {
				return _nullPatcherFixRel;
			}
		};
}