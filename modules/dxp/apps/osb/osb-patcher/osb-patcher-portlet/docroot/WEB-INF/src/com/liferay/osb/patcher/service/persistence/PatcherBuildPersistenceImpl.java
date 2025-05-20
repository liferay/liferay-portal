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

import com.liferay.osb.patcher.NoSuchPatcherBuildException;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.impl.PatcherBuildImpl;
import com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
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
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnmodifiableList;
import com.liferay.portal.kernel.util.Validator;
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
 * The persistence implementation for the patcher build service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherBuildPersistence
 * @see PatcherBuildUtil
 * @generated
 */
public class PatcherBuildPersistenceImpl extends BasePersistenceImpl<PatcherBuild>
	implements PatcherBuildPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link PatcherBuildUtil} to access the patcher build persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = PatcherBuildImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildModelImpl.FINDER_CACHE_ENABLED, PatcherBuildImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildModelImpl.FINDER_CACHE_ENABLED, PatcherBuildImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);
	public static final FinderPath FINDER_PATH_FETCH_BY_K_KV = new FinderPath(PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildModelImpl.FINDER_CACHE_ENABLED, PatcherBuildImpl.class,
			FINDER_CLASS_NAME_ENTITY, "fetchByK_KV",
			new String[] { String.class.getName(), Double.class.getName() },
			PatcherBuildModelImpl.KEY_COLUMN_BITMASK |
			PatcherBuildModelImpl.KEYVERSION_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_K_KV = new FinderPath(PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByK_KV",
			new String[] { String.class.getName(), Double.class.getName() });

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or throws a {@link com.liferay.osb.patcher.NoSuchPatcherBuildException} if it could not be found.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build
	 * @throws com.liferay.osb.patcher.NoSuchPatcherBuildException if a matching patcher build could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuild findByK_KV(String key, double keyVersion)
		throws NoSuchPatcherBuildException, SystemException {
		PatcherBuild patcherBuild = fetchByK_KV(key, keyVersion);

		if (patcherBuild == null) {
			StringBundler msg = new StringBundler(6);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("key=");
			msg.append(key);

			msg.append(", keyVersion=");
			msg.append(keyVersion);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			if (_log.isWarnEnabled()) {
				_log.warn(msg.toString());
			}

			throw new NoSuchPatcherBuildException(msg.toString());
		}

		return patcherBuild;
	}

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuild fetchByK_KV(String key, double keyVersion)
		throws SystemException {
		return fetchByK_KV(key, keyVersion, true);
	}

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param retrieveFromCache whether to use the finder cache
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuild fetchByK_KV(String key, double keyVersion,
		boolean retrieveFromCache) throws SystemException {
		Object[] finderArgs = new Object[] { key, keyVersion };

		Object result = null;

		if (retrieveFromCache) {
			result = FinderCacheUtil.getResult(FINDER_PATH_FETCH_BY_K_KV,
					finderArgs, this);
		}

		if (result instanceof PatcherBuild) {
			PatcherBuild patcherBuild = (PatcherBuild)result;

			if (!Validator.equals(key, patcherBuild.getKey()) ||
					(keyVersion != patcherBuild.getKeyVersion())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(4);

			query.append(_SQL_SELECT_PATCHERBUILD_WHERE);

			boolean bindKey = false;

			if (key == null) {
				query.append(_FINDER_COLUMN_K_KV_KEY_1);
			}
			else if (key.equals(StringPool.BLANK)) {
				query.append(_FINDER_COLUMN_K_KV_KEY_3);
			}
			else {
				bindKey = true;

				query.append(_FINDER_COLUMN_K_KV_KEY_2);
			}

			query.append(_FINDER_COLUMN_K_KV_KEYVERSION_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindKey) {
					qPos.add(key);
				}

				qPos.add(keyVersion);

				List<PatcherBuild> list = q.list();

				if (list.isEmpty()) {
					FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_K_KV,
						finderArgs, list);
				}
				else {
					PatcherBuild patcherBuild = list.get(0);

					result = patcherBuild;

					cacheResult(patcherBuild);

					if ((patcherBuild.getKey() == null) ||
							!patcherBuild.getKey().equals(key) ||
							(patcherBuild.getKeyVersion() != keyVersion)) {
						FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_K_KV,
							finderArgs, patcherBuild);
					}
				}
			}
			catch (Exception e) {
				FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_K_KV,
					finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (PatcherBuild)result;
		}
	}

	/**
	 * Removes the patcher build where key = &#63; and keyVersion = &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the patcher build that was removed
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuild removeByK_KV(String key, double keyVersion)
		throws NoSuchPatcherBuildException, SystemException {
		PatcherBuild patcherBuild = findByK_KV(key, keyVersion);

		return remove(patcherBuild);
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion = &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int countByK_KV(String key, double keyVersion)
		throws SystemException {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_K_KV;

		Object[] finderArgs = new Object[] { key, keyVersion };

		Long count = (Long)FinderCacheUtil.getResult(finderPath, finderArgs,
				this);

		if (count == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_COUNT_PATCHERBUILD_WHERE);

			boolean bindKey = false;

			if (key == null) {
				query.append(_FINDER_COLUMN_K_KV_KEY_1);
			}
			else if (key.equals(StringPool.BLANK)) {
				query.append(_FINDER_COLUMN_K_KV_KEY_3);
			}
			else {
				bindKey = true;

				query.append(_FINDER_COLUMN_K_KV_KEY_2);
			}

			query.append(_FINDER_COLUMN_K_KV_KEYVERSION_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindKey) {
					qPos.add(key);
				}

				qPos.add(keyVersion);

				count = (Long)q.uniqueResult();

				FinderCacheUtil.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				FinderCacheUtil.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_K_KV_KEY_1 = "patcherBuild.key IS NULL AND ";
	private static final String _FINDER_COLUMN_K_KV_KEY_2 = "patcherBuild.key = ? AND ";
	private static final String _FINDER_COLUMN_K_KV_KEY_3 = "(patcherBuild.key IS NULL OR patcherBuild.key = '') AND ";
	private static final String _FINDER_COLUMN_K_KV_KEYVERSION_2 = "patcherBuild.keyVersion = ?";

	public PatcherBuildPersistenceImpl() {
		setModelClass(PatcherBuild.class);
	}

	/**
	 * Caches the patcher build in the entity cache if it is enabled.
	 *
	 * @param patcherBuild the patcher build
	 */
	@Override
	public void cacheResult(PatcherBuild patcherBuild) {
		EntityCacheUtil.putResult(PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildImpl.class, patcherBuild.getPrimaryKey(), patcherBuild);

		FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_K_KV,
			new Object[] { patcherBuild.getKey(), patcherBuild.getKeyVersion() },
			patcherBuild);

		patcherBuild.resetOriginalValues();
	}

	/**
	 * Caches the patcher builds in the entity cache if it is enabled.
	 *
	 * @param patcherBuilds the patcher builds
	 */
	@Override
	public void cacheResult(List<PatcherBuild> patcherBuilds) {
		for (PatcherBuild patcherBuild : patcherBuilds) {
			if (EntityCacheUtil.getResult(
						PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
						PatcherBuildImpl.class, patcherBuild.getPrimaryKey()) == null) {
				cacheResult(patcherBuild);
			}
			else {
				patcherBuild.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all patcher builds.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		if (_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			CacheRegistryUtil.clear(PatcherBuildImpl.class.getName());
		}

		EntityCacheUtil.clearCache(PatcherBuildImpl.class.getName());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the patcher build.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherBuild patcherBuild) {
		EntityCacheUtil.removeResult(PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildImpl.class, patcherBuild.getPrimaryKey());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache(patcherBuild);
	}

	@Override
	public void clearCache(List<PatcherBuild> patcherBuilds) {
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (PatcherBuild patcherBuild : patcherBuilds) {
			EntityCacheUtil.removeResult(PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
				PatcherBuildImpl.class, patcherBuild.getPrimaryKey());

			clearUniqueFindersCache(patcherBuild);
		}
	}

	protected void cacheUniqueFindersCache(PatcherBuild patcherBuild) {
		if (patcherBuild.isNew()) {
			Object[] args = new Object[] {
					patcherBuild.getKey(), patcherBuild.getKeyVersion()
				};

			FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_K_KV, args,
				Long.valueOf(1));
			FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_K_KV, args,
				patcherBuild);
		}
		else {
			PatcherBuildModelImpl patcherBuildModelImpl = (PatcherBuildModelImpl)patcherBuild;

			if ((patcherBuildModelImpl.getColumnBitmask() &
					FINDER_PATH_FETCH_BY_K_KV.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						patcherBuild.getKey(), patcherBuild.getKeyVersion()
					};

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_K_KV, args,
					Long.valueOf(1));
				FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_K_KV, args,
					patcherBuild);
			}
		}
	}

	protected void clearUniqueFindersCache(PatcherBuild patcherBuild) {
		PatcherBuildModelImpl patcherBuildModelImpl = (PatcherBuildModelImpl)patcherBuild;

		Object[] args = new Object[] {
				patcherBuild.getKey(), patcherBuild.getKeyVersion()
			};

		FinderCacheUtil.removeResult(FINDER_PATH_COUNT_BY_K_KV, args);
		FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_K_KV, args);

		if ((patcherBuildModelImpl.getColumnBitmask() &
				FINDER_PATH_FETCH_BY_K_KV.getColumnBitmask()) != 0) {
			args = new Object[] {
					patcherBuildModelImpl.getOriginalKey(),
					patcherBuildModelImpl.getOriginalKeyVersion()
				};

			FinderCacheUtil.removeResult(FINDER_PATH_COUNT_BY_K_KV, args);
			FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_K_KV, args);
		}
	}

	/**
	 * Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	 *
	 * @param patcherBuildId the primary key for the new patcher build
	 * @return the new patcher build
	 */
	@Override
	public PatcherBuild create(long patcherBuildId) {
		PatcherBuild patcherBuild = new PatcherBuildImpl();

		patcherBuild.setNew(true);
		patcherBuild.setPrimaryKey(patcherBuildId);

		return patcherBuild;
	}

	/**
	 * Removes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuild remove(long patcherBuildId)
		throws NoSuchPatcherBuildException, SystemException {
		return remove((Serializable)patcherBuildId);
	}

	/**
	 * Removes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuild remove(Serializable primaryKey)
		throws NoSuchPatcherBuildException, SystemException {
		Session session = null;

		try {
			session = openSession();

			PatcherBuild patcherBuild = (PatcherBuild)session.get(PatcherBuildImpl.class,
					primaryKey);

			if (patcherBuild == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherBuildException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(patcherBuild);
		}
		catch (NoSuchPatcherBuildException nsee) {
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
	protected PatcherBuild removeImpl(PatcherBuild patcherBuild)
		throws SystemException {
		patcherBuild = toUnwrappedModel(patcherBuild);

		patcherBuildToPatcherAccountTableMapper.deleteLeftPrimaryKeyTableMappings(patcherBuild.getPrimaryKey());

		patcherBuildToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(patcherBuild.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherBuild)) {
				patcherBuild = (PatcherBuild)session.get(PatcherBuildImpl.class,
						patcherBuild.getPrimaryKeyObj());
			}

			if (patcherBuild != null) {
				session.delete(patcherBuild);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (patcherBuild != null) {
			clearCache(patcherBuild);
		}

		return patcherBuild;
	}

	@Override
	public PatcherBuild updateImpl(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild)
		throws SystemException {
		patcherBuild = toUnwrappedModel(patcherBuild);

		boolean isNew = patcherBuild.isNew();

		Session session = null;

		try {
			session = openSession();

			if (patcherBuild.isNew()) {
				session.save(patcherBuild);

				patcherBuild.setNew(false);
			}
			else {
				session.merge(patcherBuild);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (isNew || !PatcherBuildModelImpl.COLUMN_BITMASK_ENABLED) {
			FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}

		EntityCacheUtil.putResult(PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
			PatcherBuildImpl.class, patcherBuild.getPrimaryKey(), patcherBuild);

		clearUniqueFindersCache(patcherBuild);
		cacheUniqueFindersCache(patcherBuild);

		return patcherBuild;
	}

	protected PatcherBuild toUnwrappedModel(PatcherBuild patcherBuild) {
		if (patcherBuild instanceof PatcherBuildImpl) {
			return patcherBuild;
		}

		PatcherBuildImpl patcherBuildImpl = new PatcherBuildImpl();

		patcherBuildImpl.setNew(patcherBuild.isNew());
		patcherBuildImpl.setPrimaryKey(patcherBuild.getPrimaryKey());

		patcherBuildImpl.setPatcherBuildId(patcherBuild.getPatcherBuildId());
		patcherBuildImpl.setCompanyId(patcherBuild.getCompanyId());
		patcherBuildImpl.setUserId(patcherBuild.getUserId());
		patcherBuildImpl.setUserName(patcherBuild.getUserName());
		patcherBuildImpl.setCreateDate(patcherBuild.getCreateDate());
		patcherBuildImpl.setModifiedDate(patcherBuild.getModifiedDate());
		patcherBuildImpl.setPatcherAccountId(patcherBuild.getPatcherAccountId());
		patcherBuildImpl.setPatcherFixId(patcherBuild.getPatcherFixId());
		patcherBuildImpl.setPatcherProductVersionId(patcherBuild.getPatcherProductVersionId());
		patcherBuildImpl.setPatcherProjectVersionId(patcherBuild.getPatcherProjectVersionId());
		patcherBuildImpl.setTicketEntryId(patcherBuild.getTicketEntryId());
		patcherBuildImpl.setHotfixId(patcherBuild.getHotfixId());
		patcherBuildImpl.setName(patcherBuild.getName());
		patcherBuildImpl.setOriginalName(patcherBuild.getOriginalName());
		patcherBuildImpl.setKey(patcherBuild.getKey());
		patcherBuildImpl.setKeyVersion(patcherBuild.getKeyVersion());
		patcherBuildImpl.setType(patcherBuild.getType());
		patcherBuildImpl.setLatestBuild(patcherBuild.isLatestBuild());
		patcherBuildImpl.setLatestKeyBuild(patcherBuild.isLatestKeyBuild());
		patcherBuildImpl.setLatestLESATicketBuild(patcherBuild.isLatestLESATicketBuild());
		patcherBuildImpl.setLatestSupportTicketBuild(patcherBuild.isLatestSupportTicketBuild());
		patcherBuildImpl.setAccountEntryCode(patcherBuild.getAccountEntryCode());
		patcherBuildImpl.setLesaTicket(patcherBuild.getLesaTicket());
		patcherBuildImpl.setLesaTicketVersion(patcherBuild.getLesaTicketVersion());
		patcherBuildImpl.setSupportTicket(patcherBuild.getSupportTicket());
		patcherBuildImpl.setSupportTicketVersion(patcherBuild.getSupportTicketVersion());
		patcherBuildImpl.setFileName(patcherBuild.getFileName());
		patcherBuildImpl.setSourceName(patcherBuild.getSourceName());
		patcherBuildImpl.setChildBuild(patcherBuild.isChildBuild());
		patcherBuildImpl.setComments(patcherBuild.getComments());
		patcherBuildImpl.setQaComments(patcherBuild.getQaComments());
		patcherBuildImpl.setQaStatus(patcherBuild.getQaStatus());
		patcherBuildImpl.setRequestKey(patcherBuild.getRequestKey());
		patcherBuildImpl.setNotified(patcherBuild.isNotified());
		patcherBuildImpl.setProductVersion(patcherBuild.getProductVersion());
		patcherBuildImpl.setStatus(patcherBuild.getStatus());
		patcherBuildImpl.setStatusByUserId(patcherBuild.getStatusByUserId());
		patcherBuildImpl.setStatusByUserName(patcherBuild.getStatusByUserName());
		patcherBuildImpl.setStatusDate(patcherBuild.getStatusDate());

		return patcherBuildImpl;
	}

	/**
	 * Returns the patcher build with the primary key or throws a {@link com.liferay.portal.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher build
	 * @return the patcher build
	 * @throws com.liferay.osb.patcher.NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuild findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherBuildException, SystemException {
		PatcherBuild patcherBuild = fetchByPrimaryKey(primaryKey);

		if (patcherBuild == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherBuildException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return patcherBuild;
	}

	/**
	 * Returns the patcher build with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherBuildException} if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build
	 * @throws com.liferay.osb.patcher.NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuild findByPrimaryKey(long patcherBuildId)
		throws NoSuchPatcherBuildException, SystemException {
		return findByPrimaryKey((Serializable)patcherBuildId);
	}

	/**
	 * Returns the patcher build with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher build
	 * @return the patcher build, or <code>null</code> if a patcher build with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuild fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		PatcherBuild patcherBuild = (PatcherBuild)EntityCacheUtil.getResult(PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
				PatcherBuildImpl.class, primaryKey);

		if (patcherBuild == _nullPatcherBuild) {
			return null;
		}

		if (patcherBuild == null) {
			Session session = null;

			try {
				session = openSession();

				patcherBuild = (PatcherBuild)session.get(PatcherBuildImpl.class,
						primaryKey);

				if (patcherBuild != null) {
					cacheResult(patcherBuild);
				}
				else {
					EntityCacheUtil.putResult(PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
						PatcherBuildImpl.class, primaryKey, _nullPatcherBuild);
				}
			}
			catch (Exception e) {
				EntityCacheUtil.removeResult(PatcherBuildModelImpl.ENTITY_CACHE_ENABLED,
					PatcherBuildImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return patcherBuild;
	}

	/**
	 * Returns the patcher build with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build, or <code>null</code> if a patcher build with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherBuild fetchByPrimaryKey(long patcherBuildId)
		throws SystemException {
		return fetchByPrimaryKey((Serializable)patcherBuildId);
	}

	/**
	 * Returns all the patcher builds.
	 *
	 * @return the patcher builds
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherBuild> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher builds
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherBuild> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherBuild> findAll(int start, int end,
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

		List<PatcherBuild> list = (List<PatcherBuild>)FinderCacheUtil.getResult(finderPath,
				finderArgs, this);

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 3));

				query.append(_SQL_SELECT_PATCHERBUILD);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERBUILD;

				if (pagination) {
					sql = sql.concat(PatcherBuildModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<PatcherBuild>)QueryUtil.list(q, getDialect(),
							start, end, false);

					Collections.sort(list);

					list = new UnmodifiableList<PatcherBuild>(list);
				}
				else {
					list = (List<PatcherBuild>)QueryUtil.list(q, getDialect(),
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
	 * Removes all the patcher builds from the database.
	 *
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removeAll() throws SystemException {
		for (PatcherBuild patcherBuild : findAll()) {
			remove(patcherBuild);
		}
	}

	/**
	 * Returns the number of patcher builds.
	 *
	 * @return the number of patcher builds
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

				Query q = session.createQuery(_SQL_COUNT_PATCHERBUILD);

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
	 * Returns all the patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the patcher accounts associated with the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherAccounts(
		long pk) throws SystemException {
		return getPatcherAccounts(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the patcher accounts associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher accounts associated with the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherAccounts(
		long pk, int start, int end) throws SystemException {
		return getPatcherAccounts(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher accounts associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher accounts associated with the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherAccount> getPatcherAccounts(
		long pk, int start, int end, OrderByComparator orderByComparator)
		throws SystemException {
		return patcherBuildToPatcherAccountTableMapper.getRightBaseModels(pk,
			start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher accounts associated with the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getPatcherAccountsSize(long pk) throws SystemException {
		long[] pks = patcherBuildToPatcherAccountTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher account is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if the patcher account is associated with the patcher build; <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean containsPatcherAccount(long pk, long patcherAccountPK)
		throws SystemException {
		return patcherBuildToPatcherAccountTableMapper.containsTableMapping(pk,
			patcherAccountPK);
	}

	/**
	 * Returns <code>true</code> if the patcher build has any patcher accounts associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher accounts
	 * @return <code>true</code> if the patcher build has any patcher accounts associated with it; <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean containsPatcherAccounts(long pk) throws SystemException {
		if (getPatcherAccountsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherAccount(long pk, long patcherAccountPK)
		throws SystemException {
		patcherBuildToPatcherAccountTableMapper.addTableMapping(pk,
			patcherAccountPK);
	}

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherAccount(long pk,
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws SystemException {
		patcherBuildToPatcherAccountTableMapper.addTableMapping(pk,
			patcherAccount.getPrimaryKey());
	}

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherAccounts(long pk, long[] patcherAccountPKs)
		throws SystemException {
		for (long patcherAccountPK : patcherAccountPKs) {
			patcherBuildToPatcherAccountTableMapper.addTableMapping(pk,
				patcherAccountPK);
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherAccounts(long pk,
		List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts)
		throws SystemException {
		for (com.liferay.osb.patcher.model.PatcherAccount patcherAccount : patcherAccounts) {
			patcherBuildToPatcherAccountTableMapper.addTableMapping(pk,
				patcherAccount.getPrimaryKey());
		}
	}

	/**
	 * Clears all associations between the patcher build and its patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher accounts from
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void clearPatcherAccounts(long pk) throws SystemException {
		patcherBuildToPatcherAccountTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherAccount(long pk, long patcherAccountPK)
		throws SystemException {
		patcherBuildToPatcherAccountTableMapper.deleteTableMapping(pk,
			patcherAccountPK);
	}

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherAccount(long pk,
		com.liferay.osb.patcher.model.PatcherAccount patcherAccount)
		throws SystemException {
		patcherBuildToPatcherAccountTableMapper.deleteTableMapping(pk,
			patcherAccount.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherAccounts(long pk, long[] patcherAccountPKs)
		throws SystemException {
		for (long patcherAccountPK : patcherAccountPKs) {
			patcherBuildToPatcherAccountTableMapper.deleteTableMapping(pk,
				patcherAccountPK);
		}
	}

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherAccounts(long pk,
		List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts)
		throws SystemException {
		for (com.liferay.osb.patcher.model.PatcherAccount patcherAccount : patcherAccounts) {
			patcherBuildToPatcherAccountTableMapper.deleteTableMapping(pk,
				patcherAccount.getPrimaryKey());
		}
	}

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts to be associated with the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void setPatcherAccounts(long pk, long[] patcherAccountPKs)
		throws SystemException {
		Set<Long> newPatcherAccountPKsSet = SetUtil.fromArray(patcherAccountPKs);
		Set<Long> oldPatcherAccountPKsSet = SetUtil.fromArray(patcherBuildToPatcherAccountTableMapper.getRightPrimaryKeys(
					pk));

		Set<Long> removePatcherAccountPKsSet = new HashSet<Long>(oldPatcherAccountPKsSet);

		removePatcherAccountPKsSet.removeAll(newPatcherAccountPKsSet);

		for (long removePatcherAccountPK : removePatcherAccountPKsSet) {
			patcherBuildToPatcherAccountTableMapper.deleteTableMapping(pk,
				removePatcherAccountPK);
		}

		newPatcherAccountPKsSet.removeAll(oldPatcherAccountPKsSet);

		for (long newPatcherAccountPK : newPatcherAccountPKsSet) {
			patcherBuildToPatcherAccountTableMapper.addTableMapping(pk,
				newPatcherAccountPK);
		}
	}

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts to be associated with the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void setPatcherAccounts(long pk,
		List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts)
		throws SystemException {
		try {
			long[] patcherAccountPKs = new long[patcherAccounts.size()];

			for (int i = 0; i < patcherAccounts.size(); i++) {
				com.liferay.osb.patcher.model.PatcherAccount patcherAccount = patcherAccounts.get(i);

				patcherAccountPKs[i] = patcherAccount.getPrimaryKey();
			}

			setPatcherAccounts(pk, patcherAccountPKs);
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			FinderCacheUtil.clearCache(PatcherBuildModelImpl.MAPPING_TABLE_OSB_PATCHERACCOUNTS_PATCHERBUILDS_NAME);
		}
	}

	/**
	 * Returns all the patcher fixs associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the patcher fixs associated with the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk) throws SystemException {
		return getPatcherFixs(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the patcher fixs associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher fixs associated with the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk, int start, int end) throws SystemException {
		return getPatcherFixs(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixs associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixs associated with the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk, int start, int end, OrderByComparator orderByComparator)
		throws SystemException {
		return patcherBuildToPatcherFixTableMapper.getRightBaseModels(pk,
			start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fixs associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher fixs associated with the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getPatcherFixsSize(long pk) throws SystemException {
		long[] pks = patcherBuildToPatcherFixTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher build; <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean containsPatcherFix(long pk, long patcherFixPK)
		throws SystemException {
		return patcherBuildToPatcherFixTableMapper.containsTableMapping(pk,
			patcherFixPK);
	}

	/**
	 * Returns <code>true</code> if the patcher build has any patcher fixs associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher fixs
	 * @return <code>true</code> if the patcher build has any patcher fixs associated with it; <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean containsPatcherFixs(long pk) throws SystemException {
		if (getPatcherFixsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherFix(long pk, long patcherFixPK)
		throws SystemException {
		patcherBuildToPatcherFixTableMapper.addTableMapping(pk, patcherFixPK);
	}

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherFix(long pk,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws SystemException {
		patcherBuildToPatcherFixTableMapper.addTableMapping(pk,
			patcherFix.getPrimaryKey());
	}

	/**
	 * Adds an association between the patcher build and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherFixs(long pk, long[] patcherFixPKs)
		throws SystemException {
		for (long patcherFixPK : patcherFixPKs) {
			patcherBuildToPatcherFixTableMapper.addTableMapping(pk, patcherFixPK);
		}
	}

	/**
	 * Adds an association between the patcher build and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixs the patcher fixs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherFixs(long pk,
		List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws SystemException {
		for (com.liferay.osb.patcher.model.PatcherFix patcherFix : patcherFixs) {
			patcherBuildToPatcherFixTableMapper.addTableMapping(pk,
				patcherFix.getPrimaryKey());
		}
	}

	/**
	 * Clears all associations between the patcher build and its patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher fixs from
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void clearPatcherFixs(long pk) throws SystemException {
		patcherBuildToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherFix(long pk, long patcherFixPK)
		throws SystemException {
		patcherBuildToPatcherFixTableMapper.deleteTableMapping(pk, patcherFixPK);
	}

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherFix(long pk,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws SystemException {
		patcherBuildToPatcherFixTableMapper.deleteTableMapping(pk,
			patcherFix.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher build and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherFixs(long pk, long[] patcherFixPKs)
		throws SystemException {
		for (long patcherFixPK : patcherFixPKs) {
			patcherBuildToPatcherFixTableMapper.deleteTableMapping(pk,
				patcherFixPK);
		}
	}

	/**
	 * Removes the association between the patcher build and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixs the patcher fixs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherFixs(long pk,
		List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws SystemException {
		for (com.liferay.osb.patcher.model.PatcherFix patcherFix : patcherFixs) {
			patcherBuildToPatcherFixTableMapper.deleteTableMapping(pk,
				patcherFix.getPrimaryKey());
		}
	}

	/**
	 * Sets the patcher fixs associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixs to be associated with the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void setPatcherFixs(long pk, long[] patcherFixPKs)
		throws SystemException {
		Set<Long> newPatcherFixPKsSet = SetUtil.fromArray(patcherFixPKs);
		Set<Long> oldPatcherFixPKsSet = SetUtil.fromArray(patcherBuildToPatcherFixTableMapper.getRightPrimaryKeys(
					pk));

		Set<Long> removePatcherFixPKsSet = new HashSet<Long>(oldPatcherFixPKsSet);

		removePatcherFixPKsSet.removeAll(newPatcherFixPKsSet);

		for (long removePatcherFixPK : removePatcherFixPKsSet) {
			patcherBuildToPatcherFixTableMapper.deleteTableMapping(pk,
				removePatcherFixPK);
		}

		newPatcherFixPKsSet.removeAll(oldPatcherFixPKsSet);

		for (long newPatcherFixPK : newPatcherFixPKsSet) {
			patcherBuildToPatcherFixTableMapper.addTableMapping(pk,
				newPatcherFixPK);
		}
	}

	/**
	 * Sets the patcher fixs associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixs the patcher fixs to be associated with the patcher build
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void setPatcherFixs(long pk,
		List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws SystemException {
		try {
			long[] patcherFixPKs = new long[patcherFixs.size()];

			for (int i = 0; i < patcherFixs.size(); i++) {
				com.liferay.osb.patcher.model.PatcherFix patcherFix = patcherFixs.get(i);

				patcherFixPKs[i] = patcherFix.getPrimaryKey();
			}

			setPatcherFixs(pk, patcherFixPKs);
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			FinderCacheUtil.clearCache(PatcherBuildModelImpl.MAPPING_TABLE_OSB_PATCHERBUILDS_PATCHERFIXES_NAME);
		}
	}

	@Override
	protected Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	/**
	 * Initializes the patcher build persistence.
	 */
	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.util.service.ServiceProps.get(
						"value.object.listener.com.liferay.osb.patcher.model.PatcherBuild")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<PatcherBuild>> listenersList = new ArrayList<ModelListener<PatcherBuild>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<PatcherBuild>)InstanceFactory.newInstance(
							getClassLoader(), listenerClassName));
				}

				listeners = listenersList.toArray(new ModelListener[listenersList.size()]);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}

		patcherBuildToPatcherAccountTableMapper = TableMapperFactory.getTableMapper("OSB_PatcherAccounts_PatcherBuilds",
				"patcherBuildId", "patcherAccountId", this,
				patcherAccountPersistence);

		patcherBuildToPatcherFixTableMapper = TableMapperFactory.getTableMapper("OSB_PatcherBuilds_PatcherFixes",
				"patcherBuildId", "patcherFixId", this, patcherFixPersistence);
	}

	public void destroy() {
		EntityCacheUtil.removeCache(PatcherBuildImpl.class.getName());
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		TableMapperFactory.removeTableMapper(
			"OSB_PatcherAccounts_PatcherBuilds");
		TableMapperFactory.removeTableMapper("OSB_PatcherBuilds_PatcherFixes");
	}

	@BeanReference(type = PatcherAccountPersistence.class)
	protected PatcherAccountPersistence patcherAccountPersistence;
	protected TableMapper<PatcherBuild, com.liferay.osb.patcher.model.PatcherAccount> patcherBuildToPatcherAccountTableMapper;
	@BeanReference(type = PatcherFixPersistence.class)
	protected PatcherFixPersistence patcherFixPersistence;
	protected TableMapper<PatcherBuild, com.liferay.osb.patcher.model.PatcherFix> patcherBuildToPatcherFixTableMapper;
	private static final String _SQL_SELECT_PATCHERBUILD = "SELECT patcherBuild FROM PatcherBuild patcherBuild";
	private static final String _SQL_SELECT_PATCHERBUILD_WHERE = "SELECT patcherBuild FROM PatcherBuild patcherBuild WHERE ";
	private static final String _SQL_COUNT_PATCHERBUILD = "SELECT COUNT(patcherBuild) FROM PatcherBuild patcherBuild";
	private static final String _SQL_COUNT_PATCHERBUILD_WHERE = "SELECT COUNT(patcherBuild) FROM PatcherBuild patcherBuild WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherBuild.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No PatcherBuild exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No PatcherBuild exists with the key {";
	private static final boolean _HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = GetterUtil.getBoolean(PropsUtil.get(
				PropsKeys.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
	private static Log _log = LogFactoryUtil.getLog(PatcherBuildPersistenceImpl.class);
	private static Set<String> _badColumnNames = SetUtil.fromArray(new String[] {
				"key", "type"
			});
	private static PatcherBuild _nullPatcherBuild = new PatcherBuildImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<PatcherBuild> toCacheModel() {
				return _nullPatcherBuildCacheModel;
			}
		};

	private static CacheModel<PatcherBuild> _nullPatcherBuildCacheModel = new CacheModel<PatcherBuild>() {
			@Override
			public PatcherBuild toEntityModel() {
				return _nullPatcherBuild;
			}
		};
}