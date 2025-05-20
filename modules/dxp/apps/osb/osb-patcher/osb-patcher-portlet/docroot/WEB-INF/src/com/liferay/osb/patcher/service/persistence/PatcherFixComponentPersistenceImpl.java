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

import com.liferay.osb.patcher.NoSuchPatcherFixComponentException;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.impl.PatcherFixComponentImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl;

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
 * The persistence implementation for the patcher fix component service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixComponentPersistence
 * @see PatcherFixComponentUtil
 * @generated
 */
public class PatcherFixComponentPersistenceImpl extends BasePersistenceImpl<PatcherFixComponent>
	implements PatcherFixComponentPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link PatcherFixComponentUtil} to access the patcher fix component persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = PatcherFixComponentImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(PatcherFixComponentModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixComponentModelImpl.FINDER_CACHE_ENABLED,
			PatcherFixComponentImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(PatcherFixComponentModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixComponentModelImpl.FINDER_CACHE_ENABLED,
			PatcherFixComponentImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(PatcherFixComponentModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixComponentModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);

	public PatcherFixComponentPersistenceImpl() {
		setModelClass(PatcherFixComponent.class);
	}

	/**
	 * Caches the patcher fix component in the entity cache if it is enabled.
	 *
	 * @param patcherFixComponent the patcher fix component
	 */
	@Override
	public void cacheResult(PatcherFixComponent patcherFixComponent) {
		EntityCacheUtil.putResult(PatcherFixComponentModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixComponentImpl.class, patcherFixComponent.getPrimaryKey(),
			patcherFixComponent);

		patcherFixComponent.resetOriginalValues();
	}

	/**
	 * Caches the patcher fix components in the entity cache if it is enabled.
	 *
	 * @param patcherFixComponents the patcher fix components
	 */
	@Override
	public void cacheResult(List<PatcherFixComponent> patcherFixComponents) {
		for (PatcherFixComponent patcherFixComponent : patcherFixComponents) {
			if (EntityCacheUtil.getResult(
						PatcherFixComponentModelImpl.ENTITY_CACHE_ENABLED,
						PatcherFixComponentImpl.class,
						patcherFixComponent.getPrimaryKey()) == null) {
				cacheResult(patcherFixComponent);
			}
			else {
				patcherFixComponent.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all patcher fix components.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		if (_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			CacheRegistryUtil.clear(PatcherFixComponentImpl.class.getName());
		}

		EntityCacheUtil.clearCache(PatcherFixComponentImpl.class.getName());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the patcher fix component.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherFixComponent patcherFixComponent) {
		EntityCacheUtil.removeResult(PatcherFixComponentModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixComponentImpl.class, patcherFixComponent.getPrimaryKey());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<PatcherFixComponent> patcherFixComponents) {
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (PatcherFixComponent patcherFixComponent : patcherFixComponents) {
			EntityCacheUtil.removeResult(PatcherFixComponentModelImpl.ENTITY_CACHE_ENABLED,
				PatcherFixComponentImpl.class,
				patcherFixComponent.getPrimaryKey());
		}
	}

	/**
	 * Creates a new patcher fix component with the primary key. Does not add the patcher fix component to the database.
	 *
	 * @param patcherFixComponentId the primary key for the new patcher fix component
	 * @return the new patcher fix component
	 */
	@Override
	public PatcherFixComponent create(long patcherFixComponentId) {
		PatcherFixComponent patcherFixComponent = new PatcherFixComponentImpl();

		patcherFixComponent.setNew(true);
		patcherFixComponent.setPrimaryKey(patcherFixComponentId);

		return patcherFixComponent;
	}

	/**
	 * Removes the patcher fix component with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixComponent remove(long patcherFixComponentId)
		throws NoSuchPatcherFixComponentException, SystemException {
		return remove((Serializable)patcherFixComponentId);
	}

	/**
	 * Removes the patcher fix component with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher fix component
	 * @return the patcher fix component that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixComponent remove(Serializable primaryKey)
		throws NoSuchPatcherFixComponentException, SystemException {
		Session session = null;

		try {
			session = openSession();

			PatcherFixComponent patcherFixComponent = (PatcherFixComponent)session.get(PatcherFixComponentImpl.class,
					primaryKey);

			if (patcherFixComponent == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherFixComponentException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(patcherFixComponent);
		}
		catch (NoSuchPatcherFixComponentException nsee) {
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
	protected PatcherFixComponent removeImpl(
		PatcherFixComponent patcherFixComponent) throws SystemException {
		patcherFixComponent = toUnwrappedModel(patcherFixComponent);

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFixComponent)) {
				patcherFixComponent = (PatcherFixComponent)session.get(PatcherFixComponentImpl.class,
						patcherFixComponent.getPrimaryKeyObj());
			}

			if (patcherFixComponent != null) {
				session.delete(patcherFixComponent);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (patcherFixComponent != null) {
			clearCache(patcherFixComponent);
		}

		return patcherFixComponent;
	}

	@Override
	public PatcherFixComponent updateImpl(
		com.liferay.osb.patcher.model.PatcherFixComponent patcherFixComponent)
		throws SystemException {
		patcherFixComponent = toUnwrappedModel(patcherFixComponent);

		boolean isNew = patcherFixComponent.isNew();

		Session session = null;

		try {
			session = openSession();

			if (patcherFixComponent.isNew()) {
				session.save(patcherFixComponent);

				patcherFixComponent.setNew(false);
			}
			else {
				session.merge(patcherFixComponent);
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

		EntityCacheUtil.putResult(PatcherFixComponentModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixComponentImpl.class, patcherFixComponent.getPrimaryKey(),
			patcherFixComponent);

		return patcherFixComponent;
	}

	protected PatcherFixComponent toUnwrappedModel(
		PatcherFixComponent patcherFixComponent) {
		if (patcherFixComponent instanceof PatcherFixComponentImpl) {
			return patcherFixComponent;
		}

		PatcherFixComponentImpl patcherFixComponentImpl = new PatcherFixComponentImpl();

		patcherFixComponentImpl.setNew(patcherFixComponent.isNew());
		patcherFixComponentImpl.setPrimaryKey(patcherFixComponent.getPrimaryKey());

		patcherFixComponentImpl.setPatcherFixComponentId(patcherFixComponent.getPatcherFixComponentId());
		patcherFixComponentImpl.setCompanyId(patcherFixComponent.getCompanyId());
		patcherFixComponentImpl.setUserId(patcherFixComponent.getUserId());
		patcherFixComponentImpl.setUserName(patcherFixComponent.getUserName());
		patcherFixComponentImpl.setCreateDate(patcherFixComponent.getCreateDate());
		patcherFixComponentImpl.setModifiedDate(patcherFixComponent.getModifiedDate());
		patcherFixComponentImpl.setName(patcherFixComponent.getName());

		return patcherFixComponentImpl;
	}

	/**
	 * Returns the patcher fix component with the primary key or throws a {@link com.liferay.portal.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix component
	 * @return the patcher fix component
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixComponent findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherFixComponentException, SystemException {
		PatcherFixComponent patcherFixComponent = fetchByPrimaryKey(primaryKey);

		if (patcherFixComponent == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherFixComponentException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return patcherFixComponent;
	}

	/**
	 * Returns the patcher fix component with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixComponentException} if it could not be found.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixComponentException if a patcher fix component with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixComponent findByPrimaryKey(long patcherFixComponentId)
		throws NoSuchPatcherFixComponentException, SystemException {
		return findByPrimaryKey((Serializable)patcherFixComponentId);
	}

	/**
	 * Returns the patcher fix component with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix component
	 * @return the patcher fix component, or <code>null</code> if a patcher fix component with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixComponent fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		PatcherFixComponent patcherFixComponent = (PatcherFixComponent)EntityCacheUtil.getResult(PatcherFixComponentModelImpl.ENTITY_CACHE_ENABLED,
				PatcherFixComponentImpl.class, primaryKey);

		if (patcherFixComponent == _nullPatcherFixComponent) {
			return null;
		}

		if (patcherFixComponent == null) {
			Session session = null;

			try {
				session = openSession();

				patcherFixComponent = (PatcherFixComponent)session.get(PatcherFixComponentImpl.class,
						primaryKey);

				if (patcherFixComponent != null) {
					cacheResult(patcherFixComponent);
				}
				else {
					EntityCacheUtil.putResult(PatcherFixComponentModelImpl.ENTITY_CACHE_ENABLED,
						PatcherFixComponentImpl.class, primaryKey,
						_nullPatcherFixComponent);
				}
			}
			catch (Exception e) {
				EntityCacheUtil.removeResult(PatcherFixComponentModelImpl.ENTITY_CACHE_ENABLED,
					PatcherFixComponentImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return patcherFixComponent;
	}

	/**
	 * Returns the patcher fix component with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component, or <code>null</code> if a patcher fix component with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixComponent fetchByPrimaryKey(long patcherFixComponentId)
		throws SystemException {
		return fetchByPrimaryKey((Serializable)patcherFixComponentId);
	}

	/**
	 * Returns all the patcher fix components.
	 *
	 * @return the patcher fix components
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherFixComponent> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix components.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix components
	 * @param end the upper bound of the range of patcher fix components (not inclusive)
	 * @return the range of patcher fix components
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherFixComponent> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix components.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix components
	 * @param end the upper bound of the range of patcher fix components (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix components
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherFixComponent> findAll(int start, int end,
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

		List<PatcherFixComponent> list = (List<PatcherFixComponent>)FinderCacheUtil.getResult(finderPath,
				finderArgs, this);

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 3));

				query.append(_SQL_SELECT_PATCHERFIXCOMPONENT);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERFIXCOMPONENT;

				if (pagination) {
					sql = sql.concat(PatcherFixComponentModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<PatcherFixComponent>)QueryUtil.list(q,
							getDialect(), start, end, false);

					Collections.sort(list);

					list = new UnmodifiableList<PatcherFixComponent>(list);
				}
				else {
					list = (List<PatcherFixComponent>)QueryUtil.list(q,
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
	 * Removes all the patcher fix components from the database.
	 *
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removeAll() throws SystemException {
		for (PatcherFixComponent patcherFixComponent : findAll()) {
			remove(patcherFixComponent);
		}
	}

	/**
	 * Returns the number of patcher fix components.
	 *
	 * @return the number of patcher fix components
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

				Query q = session.createQuery(_SQL_COUNT_PATCHERFIXCOMPONENT);

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
	 * Initializes the patcher fix component persistence.
	 */
	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.util.service.ServiceProps.get(
						"value.object.listener.com.liferay.osb.patcher.model.PatcherFixComponent")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<PatcherFixComponent>> listenersList = new ArrayList<ModelListener<PatcherFixComponent>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<PatcherFixComponent>)InstanceFactory.newInstance(
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
		EntityCacheUtil.removeCache(PatcherFixComponentImpl.class.getName());
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	private static final String _SQL_SELECT_PATCHERFIXCOMPONENT = "SELECT patcherFixComponent FROM PatcherFixComponent patcherFixComponent";
	private static final String _SQL_COUNT_PATCHERFIXCOMPONENT = "SELECT COUNT(patcherFixComponent) FROM PatcherFixComponent patcherFixComponent";
	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherFixComponent.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No PatcherFixComponent exists with the primary key ";
	private static final boolean _HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = GetterUtil.getBoolean(PropsUtil.get(
				PropsKeys.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
	private static Log _log = LogFactoryUtil.getLog(PatcherFixComponentPersistenceImpl.class);
	private static PatcherFixComponent _nullPatcherFixComponent = new PatcherFixComponentImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<PatcherFixComponent> toCacheModel() {
				return _nullPatcherFixComponentCacheModel;
			}
		};

	private static CacheModel<PatcherFixComponent> _nullPatcherFixComponentCacheModel =
		new CacheModel<PatcherFixComponent>() {
			@Override
			public PatcherFixComponent toEntityModel() {
				return _nullPatcherFixComponent;
			}
		};
}