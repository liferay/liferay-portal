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

import com.liferay.osb.patcher.NoSuchPatcherAccountException;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.impl.PatcherAccountImpl;
import com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl;

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
 * The persistence implementation for the patcher account service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherAccountPersistence
 * @see PatcherAccountUtil
 * @generated
 */
public class PatcherAccountPersistenceImpl extends BasePersistenceImpl<PatcherAccount>
	implements PatcherAccountPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link PatcherAccountUtil} to access the patcher account persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = PatcherAccountImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(PatcherAccountModelImpl.ENTITY_CACHE_ENABLED,
			PatcherAccountModelImpl.FINDER_CACHE_ENABLED,
			PatcherAccountImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(PatcherAccountModelImpl.ENTITY_CACHE_ENABLED,
			PatcherAccountModelImpl.FINDER_CACHE_ENABLED,
			PatcherAccountImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(PatcherAccountModelImpl.ENTITY_CACHE_ENABLED,
			PatcherAccountModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);

	public PatcherAccountPersistenceImpl() {
		setModelClass(PatcherAccount.class);
	}

	/**
	 * Caches the patcher account in the entity cache if it is enabled.
	 *
	 * @param patcherAccount the patcher account
	 */
	@Override
	public void cacheResult(PatcherAccount patcherAccount) {
		EntityCacheUtil.putResult(PatcherAccountModelImpl.ENTITY_CACHE_ENABLED,
			PatcherAccountImpl.class, patcherAccount.getPrimaryKey(),
			patcherAccount);

		patcherAccount.resetOriginalValues();
	}

	/**
	 * Caches the patcher accounts in the entity cache if it is enabled.
	 *
	 * @param patcherAccounts the patcher accounts
	 */
	@Override
	public void cacheResult(List<PatcherAccount> patcherAccounts) {
		for (PatcherAccount patcherAccount : patcherAccounts) {
			if (EntityCacheUtil.getResult(
						PatcherAccountModelImpl.ENTITY_CACHE_ENABLED,
						PatcherAccountImpl.class, patcherAccount.getPrimaryKey()) == null) {
				cacheResult(patcherAccount);
			}
			else {
				patcherAccount.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all patcher accounts.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		if (_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			CacheRegistryUtil.clear(PatcherAccountImpl.class.getName());
		}

		EntityCacheUtil.clearCache(PatcherAccountImpl.class.getName());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the patcher account.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherAccount patcherAccount) {
		EntityCacheUtil.removeResult(PatcherAccountModelImpl.ENTITY_CACHE_ENABLED,
			PatcherAccountImpl.class, patcherAccount.getPrimaryKey());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<PatcherAccount> patcherAccounts) {
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (PatcherAccount patcherAccount : patcherAccounts) {
			EntityCacheUtil.removeResult(PatcherAccountModelImpl.ENTITY_CACHE_ENABLED,
				PatcherAccountImpl.class, patcherAccount.getPrimaryKey());
		}
	}

	/**
	 * Creates a new patcher account with the primary key. Does not add the patcher account to the database.
	 *
	 * @param patcherAccountId the primary key for the new patcher account
	 * @return the new patcher account
	 */
	@Override
	public PatcherAccount create(long patcherAccountId) {
		PatcherAccount patcherAccount = new PatcherAccountImpl();

		patcherAccount.setNew(true);
		patcherAccount.setPrimaryKey(patcherAccountId);

		return patcherAccount;
	}

	/**
	 * Removes the patcher account with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherAccount remove(long patcherAccountId)
		throws NoSuchPatcherAccountException, SystemException {
		return remove((Serializable)patcherAccountId);
	}

	/**
	 * Removes the patcher account with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher account
	 * @return the patcher account that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherAccount remove(Serializable primaryKey)
		throws NoSuchPatcherAccountException, SystemException {
		Session session = null;

		try {
			session = openSession();

			PatcherAccount patcherAccount = (PatcherAccount)session.get(PatcherAccountImpl.class,
					primaryKey);

			if (patcherAccount == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherAccountException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(patcherAccount);
		}
		catch (NoSuchPatcherAccountException nsee) {
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
	protected PatcherAccount removeImpl(PatcherAccount patcherAccount)
		throws SystemException {
		patcherAccount = toUnwrappedModel(patcherAccount);

		patcherAccountToPatcherBuildTableMapper.deleteLeftPrimaryKeyTableMappings(patcherAccount.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherAccount)) {
				patcherAccount = (PatcherAccount)session.get(PatcherAccountImpl.class,
						patcherAccount.getPrimaryKeyObj());
			}

			if (patcherAccount != null) {
				session.delete(patcherAccount);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (patcherAccount != null) {
			clearCache(patcherAccount);
		}

		return patcherAccount;
	}

	@Override
	public PatcherAccount updateImpl(
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws SystemException {
		patcherAccount = toUnwrappedModel(patcherAccount);

		boolean isNew = patcherAccount.isNew();

		Session session = null;

		try {
			session = openSession();

			if (patcherAccount.isNew()) {
				session.save(patcherAccount);

				patcherAccount.setNew(false);
			}
			else {
				session.merge(patcherAccount);
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

		EntityCacheUtil.putResult(PatcherAccountModelImpl.ENTITY_CACHE_ENABLED,
			PatcherAccountImpl.class, patcherAccount.getPrimaryKey(),
			patcherAccount);

		return patcherAccount;
	}

	protected PatcherAccount toUnwrappedModel(PatcherAccount patcherAccount) {
		if (patcherAccount instanceof PatcherAccountImpl) {
			return patcherAccount;
		}

		PatcherAccountImpl patcherAccountImpl = new PatcherAccountImpl();

		patcherAccountImpl.setNew(patcherAccount.isNew());
		patcherAccountImpl.setPrimaryKey(patcherAccount.getPrimaryKey());

		patcherAccountImpl.setPatcherAccountId(patcherAccount.getPatcherAccountId());
		patcherAccountImpl.setCompanyId(patcherAccount.getCompanyId());
		patcherAccountImpl.setUserId(patcherAccount.getUserId());
		patcherAccountImpl.setUserName(patcherAccount.getUserName());
		patcherAccountImpl.setCreateDate(patcherAccount.getCreateDate());
		patcherAccountImpl.setModifiedDate(patcherAccount.getModifiedDate());
		patcherAccountImpl.setAccountEntryId(patcherAccount.getAccountEntryId());
		patcherAccountImpl.setAccountEntryCode(patcherAccount.getAccountEntryCode());

		return patcherAccountImpl;
	}

	/**
	 * Returns the patcher account with the primary key or throws a {@link com.liferay.portal.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher account
	 * @return the patcher account
	 * @throws com.liferay.osb.patcher.NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherAccount findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherAccountException, SystemException {
		PatcherAccount patcherAccount = fetchByPrimaryKey(primaryKey);

		if (patcherAccount == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherAccountException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return patcherAccount;
	}

	/**
	 * Returns the patcher account with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherAccountException} if it could not be found.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account
	 * @throws com.liferay.osb.patcher.NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherAccount findByPrimaryKey(long patcherAccountId)
		throws NoSuchPatcherAccountException, SystemException {
		return findByPrimaryKey((Serializable)patcherAccountId);
	}

	/**
	 * Returns the patcher account with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher account
	 * @return the patcher account, or <code>null</code> if a patcher account with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherAccount fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		PatcherAccount patcherAccount = (PatcherAccount)EntityCacheUtil.getResult(PatcherAccountModelImpl.ENTITY_CACHE_ENABLED,
				PatcherAccountImpl.class, primaryKey);

		if (patcherAccount == _nullPatcherAccount) {
			return null;
		}

		if (patcherAccount == null) {
			Session session = null;

			try {
				session = openSession();

				patcherAccount = (PatcherAccount)session.get(PatcherAccountImpl.class,
						primaryKey);

				if (patcherAccount != null) {
					cacheResult(patcherAccount);
				}
				else {
					EntityCacheUtil.putResult(PatcherAccountModelImpl.ENTITY_CACHE_ENABLED,
						PatcherAccountImpl.class, primaryKey,
						_nullPatcherAccount);
				}
			}
			catch (Exception e) {
				EntityCacheUtil.removeResult(PatcherAccountModelImpl.ENTITY_CACHE_ENABLED,
					PatcherAccountImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return patcherAccount;
	}

	/**
	 * Returns the patcher account with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account, or <code>null</code> if a patcher account with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherAccount fetchByPrimaryKey(long patcherAccountId)
		throws SystemException {
		return fetchByPrimaryKey((Serializable)patcherAccountId);
	}

	/**
	 * Returns all the patcher accounts.
	 *
	 * @return the patcher accounts
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherAccount> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher accounts
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherAccount> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher accounts
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherAccount> findAll(int start, int end,
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

		List<PatcherAccount> list = (List<PatcherAccount>)FinderCacheUtil.getResult(finderPath,
				finderArgs, this);

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 3));

				query.append(_SQL_SELECT_PATCHERACCOUNT);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERACCOUNT;

				if (pagination) {
					sql = sql.concat(PatcherAccountModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<PatcherAccount>)QueryUtil.list(q,
							getDialect(), start, end, false);

					Collections.sort(list);

					list = new UnmodifiableList<PatcherAccount>(list);
				}
				else {
					list = (List<PatcherAccount>)QueryUtil.list(q,
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
	 * Removes all the patcher accounts from the database.
	 *
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removeAll() throws SystemException {
		for (PatcherAccount patcherAccount : findAll()) {
			remove(patcherAccount);
		}
	}

	/**
	 * Returns the number of patcher accounts.
	 *
	 * @return the number of patcher accounts
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

				Query q = session.createQuery(_SQL_COUNT_PATCHERACCOUNT);

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
	 * Returns all the patcher builds associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return the patcher builds associated with the patcher account
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk) throws SystemException {
		return getPatcherBuilds(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the patcher builds associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher builds associated with the patcher account
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk, int start, int end) throws SystemException {
		return getPatcherBuilds(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher account
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherBuild> getPatcherBuilds(
		long pk, int start, int end, OrderByComparator orderByComparator)
		throws SystemException {
		return patcherAccountToPatcherBuildTableMapper.getRightBaseModels(pk,
			start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher builds associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return the number of patcher builds associated with the patcher account
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getPatcherBuildsSize(long pk) throws SystemException {
		long[] pks = patcherAccountToPatcherBuildTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher build is associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if the patcher build is associated with the patcher account; <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean containsPatcherBuild(long pk, long patcherBuildPK)
		throws SystemException {
		return patcherAccountToPatcherBuildTableMapper.containsTableMapping(pk,
			patcherBuildPK);
	}

	/**
	 * Returns <code>true</code> if the patcher account has any patcher builds associated with it.
	 *
	 * @param pk the primary key of the patcher account to check for associations with patcher builds
	 * @return <code>true</code> if the patcher account has any patcher builds associated with it; <code>false</code> otherwise
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
	 * Adds an association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPK the primary key of the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherBuild(long pk, long patcherBuildPK)
		throws SystemException {
		patcherAccountToPatcherBuildTableMapper.addTableMapping(pk,
			patcherBuildPK);
	}

	/**
	 * Adds an association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuild the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherBuild(long pk,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws SystemException {
		patcherAccountToPatcherBuildTableMapper.addTableMapping(pk,
			patcherBuild.getPrimaryKey());
	}

	/**
	 * Adds an association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherBuilds(long pk, long[] patcherBuildPKs)
		throws SystemException {
		for (long patcherBuildPK : patcherBuildPKs) {
			patcherAccountToPatcherBuildTableMapper.addTableMapping(pk,
				patcherBuildPK);
		}
	}

	/**
	 * Adds an association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuilds the patcher builds
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherBuilds(long pk,
		List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws SystemException {
		for (com.liferay.osb.patcher.model.PatcherBuild patcherBuild : patcherBuilds) {
			patcherAccountToPatcherBuildTableMapper.addTableMapping(pk,
				patcherBuild.getPrimaryKey());
		}
	}

	/**
	 * Clears all associations between the patcher account and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account to clear the associated patcher builds from
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void clearPatcherBuilds(long pk) throws SystemException {
		patcherAccountToPatcherBuildTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPK the primary key of the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherBuild(long pk, long patcherBuildPK)
		throws SystemException {
		patcherAccountToPatcherBuildTableMapper.deleteTableMapping(pk,
			patcherBuildPK);
	}

	/**
	 * Removes the association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuild the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherBuild(long pk,
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws SystemException {
		patcherAccountToPatcherBuildTableMapper.deleteTableMapping(pk,
			patcherBuild.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherBuilds(long pk, long[] patcherBuildPKs)
		throws SystemException {
		for (long patcherBuildPK : patcherBuildPKs) {
			patcherAccountToPatcherBuildTableMapper.deleteTableMapping(pk,
				patcherBuildPK);
		}
	}

	/**
	 * Removes the association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuilds the patcher builds
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherBuilds(long pk,
		List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds)
		throws SystemException {
		for (com.liferay.osb.patcher.model.PatcherBuild patcherBuild : patcherBuilds) {
			patcherAccountToPatcherBuildTableMapper.deleteTableMapping(pk,
				patcherBuild.getPrimaryKey());
		}
	}

	/**
	 * Sets the patcher builds associated with the patcher account, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher account
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void setPatcherBuilds(long pk, long[] patcherBuildPKs)
		throws SystemException {
		Set<Long> newPatcherBuildPKsSet = SetUtil.fromArray(patcherBuildPKs);
		Set<Long> oldPatcherBuildPKsSet = SetUtil.fromArray(patcherAccountToPatcherBuildTableMapper.getRightPrimaryKeys(
					pk));

		Set<Long> removePatcherBuildPKsSet = new HashSet<Long>(oldPatcherBuildPKsSet);

		removePatcherBuildPKsSet.removeAll(newPatcherBuildPKsSet);

		for (long removePatcherBuildPK : removePatcherBuildPKsSet) {
			patcherAccountToPatcherBuildTableMapper.deleteTableMapping(pk,
				removePatcherBuildPK);
		}

		newPatcherBuildPKsSet.removeAll(oldPatcherBuildPKsSet);

		for (long newPatcherBuildPK : newPatcherBuildPKsSet) {
			patcherAccountToPatcherBuildTableMapper.addTableMapping(pk,
				newPatcherBuildPK);
		}
	}

	/**
	 * Sets the patcher builds associated with the patcher account, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuilds the patcher builds to be associated with the patcher account
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
			FinderCacheUtil.clearCache(PatcherAccountModelImpl.MAPPING_TABLE_OSB_PATCHERACCOUNTS_PATCHERBUILDS_NAME);
		}
	}

	/**
	 * Initializes the patcher account persistence.
	 */
	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.util.service.ServiceProps.get(
						"value.object.listener.com.liferay.osb.patcher.model.PatcherAccount")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<PatcherAccount>> listenersList = new ArrayList<ModelListener<PatcherAccount>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<PatcherAccount>)InstanceFactory.newInstance(
							getClassLoader(), listenerClassName));
				}

				listeners = listenersList.toArray(new ModelListener[listenersList.size()]);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}

		patcherAccountToPatcherBuildTableMapper = TableMapperFactory.getTableMapper("OSB_PatcherAccounts_PatcherBuilds",
				"patcherAccountId", "patcherBuildId", this,
				patcherBuildPersistence);
	}

	public void destroy() {
		EntityCacheUtil.removeCache(PatcherAccountImpl.class.getName());
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		TableMapperFactory.removeTableMapper(
			"OSB_PatcherAccounts_PatcherBuilds");
	}

	@BeanReference(type = PatcherBuildPersistence.class)
	protected PatcherBuildPersistence patcherBuildPersistence;
	protected TableMapper<PatcherAccount, com.liferay.osb.patcher.model.PatcherBuild> patcherAccountToPatcherBuildTableMapper;
	private static final String _SQL_SELECT_PATCHERACCOUNT = "SELECT patcherAccount FROM PatcherAccount patcherAccount";
	private static final String _SQL_COUNT_PATCHERACCOUNT = "SELECT COUNT(patcherAccount) FROM PatcherAccount patcherAccount";
	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherAccount.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No PatcherAccount exists with the primary key ";
	private static final boolean _HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = GetterUtil.getBoolean(PropsUtil.get(
				PropsKeys.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
	private static Log _log = LogFactoryUtil.getLog(PatcherAccountPersistenceImpl.class);
	private static PatcherAccount _nullPatcherAccount = new PatcherAccountImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<PatcherAccount> toCacheModel() {
				return _nullPatcherAccountCacheModel;
			}
		};

	private static CacheModel<PatcherAccount> _nullPatcherAccountCacheModel = new CacheModel<PatcherAccount>() {
			@Override
			public PatcherAccount toEntityModel() {
				return _nullPatcherAccount;
			}
		};
}