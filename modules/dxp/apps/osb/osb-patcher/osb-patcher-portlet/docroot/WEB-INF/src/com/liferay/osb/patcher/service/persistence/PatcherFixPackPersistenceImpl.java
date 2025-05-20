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

import com.liferay.osb.patcher.NoSuchPatcherFixPackException;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.impl.PatcherFixPackImpl;
import com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl;

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
 * The persistence implementation for the patcher fix pack service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherFixPackPersistence
 * @see PatcherFixPackUtil
 * @generated
 */
public class PatcherFixPackPersistenceImpl extends BasePersistenceImpl<PatcherFixPack>
	implements PatcherFixPackPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link PatcherFixPackUtil} to access the patcher fix pack persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = PatcherFixPackImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixPackModelImpl.FINDER_CACHE_ENABLED,
			PatcherFixPackImpl.class, FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixPackModelImpl.FINDER_CACHE_ENABLED,
			PatcherFixPackImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixPackModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);
	public static final FinderPath FINDER_PATH_FETCH_BY_PFCI_PPVI_N_V = new FinderPath(PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixPackModelImpl.FINDER_CACHE_ENABLED,
			PatcherFixPackImpl.class, FINDER_CLASS_NAME_ENTITY,
			"fetchByPFCI_PPVI_N_V",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Integer.class.getName()
			},
			PatcherFixPackModelImpl.PATCHERFIXCOMPONENTID_COLUMN_BITMASK |
			PatcherFixPackModelImpl.PATCHERPROJECTVERSIONID_COLUMN_BITMASK |
			PatcherFixPackModelImpl.NAME_COLUMN_BITMASK |
			PatcherFixPackModelImpl.VERSION_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_PFCI_PPVI_N_V = new FinderPath(PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixPackModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPFCI_PPVI_N_V",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Integer.class.getName()
			});

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixPackException} if it could not be found.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the matching patcher fix pack
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixPack findByPFCI_PPVI_N_V(long patcherFixComponentId,
		long patcherProjectVersionId, String name, int version)
		throws NoSuchPatcherFixPackException, SystemException {
		PatcherFixPack patcherFixPack = fetchByPFCI_PPVI_N_V(patcherFixComponentId,
				patcherProjectVersionId, name, version);

		if (patcherFixPack == null) {
			StringBundler msg = new StringBundler(10);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("patcherFixComponentId=");
			msg.append(patcherFixComponentId);

			msg.append(", patcherProjectVersionId=");
			msg.append(patcherProjectVersionId);

			msg.append(", name=");
			msg.append(name);

			msg.append(", version=");
			msg.append(version);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			if (_log.isWarnEnabled()) {
				_log.warn(msg.toString());
			}

			throw new NoSuchPatcherFixPackException(msg.toString());
		}

		return patcherFixPack;
	}

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_N_V(long patcherFixComponentId,
		long patcherProjectVersionId, String name, int version)
		throws SystemException {
		return fetchByPFCI_PPVI_N_V(patcherFixComponentId,
			patcherProjectVersionId, name, version, true);
	}

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @param retrieveFromCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixPack fetchByPFCI_PPVI_N_V(long patcherFixComponentId,
		long patcherProjectVersionId, String name, int version,
		boolean retrieveFromCache) throws SystemException {
		Object[] finderArgs = new Object[] {
				patcherFixComponentId, patcherProjectVersionId, name, version
			};

		Object result = null;

		if (retrieveFromCache) {
			result = FinderCacheUtil.getResult(FINDER_PATH_FETCH_BY_PFCI_PPVI_N_V,
					finderArgs, this);
		}

		if (result instanceof PatcherFixPack) {
			PatcherFixPack patcherFixPack = (PatcherFixPack)result;

			if ((patcherFixComponentId != patcherFixPack.getPatcherFixComponentId()) ||
					(patcherProjectVersionId != patcherFixPack.getPatcherProjectVersionId()) ||
					!Validator.equals(name, patcherFixPack.getName()) ||
					(version != patcherFixPack.getVersion())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(6);

			query.append(_SQL_SELECT_PATCHERFIXPACK_WHERE);

			query.append(_FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERFIXCOMPONENTID_2);

			query.append(_FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERPROJECTVERSIONID_2);

			boolean bindName = false;

			if (name == null) {
				query.append(_FINDER_COLUMN_PFCI_PPVI_N_V_NAME_1);
			}
			else if (name.equals(StringPool.BLANK)) {
				query.append(_FINDER_COLUMN_PFCI_PPVI_N_V_NAME_3);
			}
			else {
				bindName = true;

				query.append(_FINDER_COLUMN_PFCI_PPVI_N_V_NAME_2);
			}

			query.append(_FINDER_COLUMN_PFCI_PPVI_N_V_VERSION_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(patcherFixComponentId);

				qPos.add(patcherProjectVersionId);

				if (bindName) {
					qPos.add(name);
				}

				qPos.add(version);

				List<PatcherFixPack> list = q.list();

				if (list.isEmpty()) {
					FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_PFCI_PPVI_N_V,
						finderArgs, list);
				}
				else {
					PatcherFixPack patcherFixPack = list.get(0);

					result = patcherFixPack;

					cacheResult(patcherFixPack);

					if ((patcherFixPack.getPatcherFixComponentId() != patcherFixComponentId) ||
							(patcherFixPack.getPatcherProjectVersionId() != patcherProjectVersionId) ||
							(patcherFixPack.getName() == null) ||
							!patcherFixPack.getName().equals(name) ||
							(patcherFixPack.getVersion() != version)) {
						FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_PFCI_PPVI_N_V,
							finderArgs, patcherFixPack);
					}
				}
			}
			catch (Exception e) {
				FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_PFCI_PPVI_N_V,
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
			return (PatcherFixPack)result;
		}
	}

	/**
	 * Removes the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the patcher fix pack that was removed
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixPack removeByPFCI_PPVI_N_V(long patcherFixComponentId,
		long patcherProjectVersionId, String name, int version)
		throws NoSuchPatcherFixPackException, SystemException {
		PatcherFixPack patcherFixPack = findByPFCI_PPVI_N_V(patcherFixComponentId,
				patcherProjectVersionId, name, version);

		return remove(patcherFixPack);
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int countByPFCI_PPVI_N_V(long patcherFixComponentId,
		long patcherProjectVersionId, String name, int version)
		throws SystemException {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_PFCI_PPVI_N_V;

		Object[] finderArgs = new Object[] {
				patcherFixComponentId, patcherProjectVersionId, name, version
			};

		Long count = (Long)FinderCacheUtil.getResult(finderPath, finderArgs,
				this);

		if (count == null) {
			StringBundler query = new StringBundler(5);

			query.append(_SQL_COUNT_PATCHERFIXPACK_WHERE);

			query.append(_FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERFIXCOMPONENTID_2);

			query.append(_FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERPROJECTVERSIONID_2);

			boolean bindName = false;

			if (name == null) {
				query.append(_FINDER_COLUMN_PFCI_PPVI_N_V_NAME_1);
			}
			else if (name.equals(StringPool.BLANK)) {
				query.append(_FINDER_COLUMN_PFCI_PPVI_N_V_NAME_3);
			}
			else {
				bindName = true;

				query.append(_FINDER_COLUMN_PFCI_PPVI_N_V_NAME_2);
			}

			query.append(_FINDER_COLUMN_PFCI_PPVI_N_V_VERSION_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(patcherFixComponentId);

				qPos.add(patcherProjectVersionId);

				if (bindName) {
					qPos.add(name);
				}

				qPos.add(version);

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

	private static final String _FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERFIXCOMPONENTID_2 =
		"patcherFixPack.patcherFixComponentId = ? AND ";
	private static final String _FINDER_COLUMN_PFCI_PPVI_N_V_PATCHERPROJECTVERSIONID_2 =
		"patcherFixPack.patcherProjectVersionId = ? AND ";
	private static final String _FINDER_COLUMN_PFCI_PPVI_N_V_NAME_1 = "patcherFixPack.name IS NULL AND ";
	private static final String _FINDER_COLUMN_PFCI_PPVI_N_V_NAME_2 = "patcherFixPack.name = ? AND ";
	private static final String _FINDER_COLUMN_PFCI_PPVI_N_V_NAME_3 = "(patcherFixPack.name IS NULL OR patcherFixPack.name = '') AND ";
	private static final String _FINDER_COLUMN_PFCI_PPVI_N_V_VERSION_2 = "patcherFixPack.version = ?";

	public PatcherFixPackPersistenceImpl() {
		setModelClass(PatcherFixPack.class);
	}

	/**
	 * Caches the patcher fix pack in the entity cache if it is enabled.
	 *
	 * @param patcherFixPack the patcher fix pack
	 */
	@Override
	public void cacheResult(PatcherFixPack patcherFixPack) {
		EntityCacheUtil.putResult(PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixPackImpl.class, patcherFixPack.getPrimaryKey(),
			patcherFixPack);

		FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_PFCI_PPVI_N_V,
			new Object[] {
				patcherFixPack.getPatcherFixComponentId(),
				patcherFixPack.getPatcherProjectVersionId(),
				patcherFixPack.getName(), patcherFixPack.getVersion()
			}, patcherFixPack);

		patcherFixPack.resetOriginalValues();
	}

	/**
	 * Caches the patcher fix packs in the entity cache if it is enabled.
	 *
	 * @param patcherFixPacks the patcher fix packs
	 */
	@Override
	public void cacheResult(List<PatcherFixPack> patcherFixPacks) {
		for (PatcherFixPack patcherFixPack : patcherFixPacks) {
			if (EntityCacheUtil.getResult(
						PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
						PatcherFixPackImpl.class, patcherFixPack.getPrimaryKey()) == null) {
				cacheResult(patcherFixPack);
			}
			else {
				patcherFixPack.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all patcher fix packs.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		if (_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			CacheRegistryUtil.clear(PatcherFixPackImpl.class.getName());
		}

		EntityCacheUtil.clearCache(PatcherFixPackImpl.class.getName());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the patcher fix pack.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherFixPack patcherFixPack) {
		EntityCacheUtil.removeResult(PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixPackImpl.class, patcherFixPack.getPrimaryKey());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache(patcherFixPack);
	}

	@Override
	public void clearCache(List<PatcherFixPack> patcherFixPacks) {
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (PatcherFixPack patcherFixPack : patcherFixPacks) {
			EntityCacheUtil.removeResult(PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
				PatcherFixPackImpl.class, patcherFixPack.getPrimaryKey());

			clearUniqueFindersCache(patcherFixPack);
		}
	}

	protected void cacheUniqueFindersCache(PatcherFixPack patcherFixPack) {
		if (patcherFixPack.isNew()) {
			Object[] args = new Object[] {
					patcherFixPack.getPatcherFixComponentId(),
					patcherFixPack.getPatcherProjectVersionId(),
					patcherFixPack.getName(), patcherFixPack.getVersion()
				};

			FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_PFCI_PPVI_N_V, args,
				Long.valueOf(1));
			FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_PFCI_PPVI_N_V, args,
				patcherFixPack);
		}
		else {
			PatcherFixPackModelImpl patcherFixPackModelImpl = (PatcherFixPackModelImpl)patcherFixPack;

			if ((patcherFixPackModelImpl.getColumnBitmask() &
					FINDER_PATH_FETCH_BY_PFCI_PPVI_N_V.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						patcherFixPack.getPatcherFixComponentId(),
						patcherFixPack.getPatcherProjectVersionId(),
						patcherFixPack.getName(), patcherFixPack.getVersion()
					};

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_PFCI_PPVI_N_V,
					args, Long.valueOf(1));
				FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_PFCI_PPVI_N_V,
					args, patcherFixPack);
			}
		}
	}

	protected void clearUniqueFindersCache(PatcherFixPack patcherFixPack) {
		PatcherFixPackModelImpl patcherFixPackModelImpl = (PatcherFixPackModelImpl)patcherFixPack;

		Object[] args = new Object[] {
				patcherFixPack.getPatcherFixComponentId(),
				patcherFixPack.getPatcherProjectVersionId(),
				patcherFixPack.getName(), patcherFixPack.getVersion()
			};

		FinderCacheUtil.removeResult(FINDER_PATH_COUNT_BY_PFCI_PPVI_N_V, args);
		FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_PFCI_PPVI_N_V, args);

		if ((patcherFixPackModelImpl.getColumnBitmask() &
				FINDER_PATH_FETCH_BY_PFCI_PPVI_N_V.getColumnBitmask()) != 0) {
			args = new Object[] {
					patcherFixPackModelImpl.getOriginalPatcherFixComponentId(),
					patcherFixPackModelImpl.getOriginalPatcherProjectVersionId(),
					patcherFixPackModelImpl.getOriginalName(),
					patcherFixPackModelImpl.getOriginalVersion()
				};

			FinderCacheUtil.removeResult(FINDER_PATH_COUNT_BY_PFCI_PPVI_N_V,
				args);
			FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_PFCI_PPVI_N_V,
				args);
		}
	}

	/**
	 * Creates a new patcher fix pack with the primary key. Does not add the patcher fix pack to the database.
	 *
	 * @param patcherFixPackId the primary key for the new patcher fix pack
	 * @return the new patcher fix pack
	 */
	@Override
	public PatcherFixPack create(long patcherFixPackId) {
		PatcherFixPack patcherFixPack = new PatcherFixPackImpl();

		patcherFixPack.setNew(true);
		patcherFixPack.setPrimaryKey(patcherFixPackId);

		return patcherFixPack;
	}

	/**
	 * Removes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixPack remove(long patcherFixPackId)
		throws NoSuchPatcherFixPackException, SystemException {
		return remove((Serializable)patcherFixPackId);
	}

	/**
	 * Removes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher fix pack
	 * @return the patcher fix pack that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixPack remove(Serializable primaryKey)
		throws NoSuchPatcherFixPackException, SystemException {
		Session session = null;

		try {
			session = openSession();

			PatcherFixPack patcherFixPack = (PatcherFixPack)session.get(PatcherFixPackImpl.class,
					primaryKey);

			if (patcherFixPack == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherFixPackException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(patcherFixPack);
		}
		catch (NoSuchPatcherFixPackException nsee) {
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
	protected PatcherFixPack removeImpl(PatcherFixPack patcherFixPack)
		throws SystemException {
		patcherFixPack = toUnwrappedModel(patcherFixPack);

		patcherFixPackToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(patcherFixPack.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherFixPack)) {
				patcherFixPack = (PatcherFixPack)session.get(PatcherFixPackImpl.class,
						patcherFixPack.getPrimaryKeyObj());
			}

			if (patcherFixPack != null) {
				session.delete(patcherFixPack);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (patcherFixPack != null) {
			clearCache(patcherFixPack);
		}

		return patcherFixPack;
	}

	@Override
	public PatcherFixPack updateImpl(
		com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack)
		throws SystemException {
		patcherFixPack = toUnwrappedModel(patcherFixPack);

		boolean isNew = patcherFixPack.isNew();

		Session session = null;

		try {
			session = openSession();

			if (patcherFixPack.isNew()) {
				session.save(patcherFixPack);

				patcherFixPack.setNew(false);
			}
			else {
				session.merge(patcherFixPack);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (isNew || !PatcherFixPackModelImpl.COLUMN_BITMASK_ENABLED) {
			FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}

		EntityCacheUtil.putResult(PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
			PatcherFixPackImpl.class, patcherFixPack.getPrimaryKey(),
			patcherFixPack);

		clearUniqueFindersCache(patcherFixPack);
		cacheUniqueFindersCache(patcherFixPack);

		return patcherFixPack;
	}

	protected PatcherFixPack toUnwrappedModel(PatcherFixPack patcherFixPack) {
		if (patcherFixPack instanceof PatcherFixPackImpl) {
			return patcherFixPack;
		}

		PatcherFixPackImpl patcherFixPackImpl = new PatcherFixPackImpl();

		patcherFixPackImpl.setNew(patcherFixPack.isNew());
		patcherFixPackImpl.setPrimaryKey(patcherFixPack.getPrimaryKey());

		patcherFixPackImpl.setPatcherFixPackId(patcherFixPack.getPatcherFixPackId());
		patcherFixPackImpl.setCompanyId(patcherFixPack.getCompanyId());
		patcherFixPackImpl.setUserId(patcherFixPack.getUserId());
		patcherFixPackImpl.setUserName(patcherFixPack.getUserName());
		patcherFixPackImpl.setCreateDate(patcherFixPack.getCreateDate());
		patcherFixPackImpl.setModifiedDate(patcherFixPack.getModifiedDate());
		patcherFixPackImpl.setPatcherBuildId(patcherFixPack.getPatcherBuildId());
		patcherFixPackImpl.setPatcherFixComponentId(patcherFixPack.getPatcherFixComponentId());
		patcherFixPackImpl.setPatcherProjectVersionId(patcherFixPack.getPatcherProjectVersionId());
		patcherFixPackImpl.setName(patcherFixPack.getName());
		patcherFixPackImpl.setVersion(patcherFixPack.getVersion());
		patcherFixPackImpl.setReleasedDate(patcherFixPack.getReleasedDate());
		patcherFixPackImpl.setRequirements(patcherFixPack.getRequirements());
		patcherFixPackImpl.setStatus(patcherFixPack.getStatus());

		return patcherFixPackImpl;
	}

	/**
	 * Returns the patcher fix pack with the primary key or throws a {@link com.liferay.portal.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix pack
	 * @return the patcher fix pack
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixPack findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherFixPackException, SystemException {
		PatcherFixPack patcherFixPack = fetchByPrimaryKey(primaryKey);

		if (patcherFixPack == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherFixPackException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return patcherFixPack;
	}

	/**
	 * Returns the patcher fix pack with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherFixPackException} if it could not be found.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack
	 * @throws com.liferay.osb.patcher.NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixPack findByPrimaryKey(long patcherFixPackId)
		throws NoSuchPatcherFixPackException, SystemException {
		return findByPrimaryKey((Serializable)patcherFixPackId);
	}

	/**
	 * Returns the patcher fix pack with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher fix pack
	 * @return the patcher fix pack, or <code>null</code> if a patcher fix pack with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixPack fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		PatcherFixPack patcherFixPack = (PatcherFixPack)EntityCacheUtil.getResult(PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
				PatcherFixPackImpl.class, primaryKey);

		if (patcherFixPack == _nullPatcherFixPack) {
			return null;
		}

		if (patcherFixPack == null) {
			Session session = null;

			try {
				session = openSession();

				patcherFixPack = (PatcherFixPack)session.get(PatcherFixPackImpl.class,
						primaryKey);

				if (patcherFixPack != null) {
					cacheResult(patcherFixPack);
				}
				else {
					EntityCacheUtil.putResult(PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
						PatcherFixPackImpl.class, primaryKey,
						_nullPatcherFixPack);
				}
			}
			catch (Exception e) {
				EntityCacheUtil.removeResult(PatcherFixPackModelImpl.ENTITY_CACHE_ENABLED,
					PatcherFixPackImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return patcherFixPack;
	}

	/**
	 * Returns the patcher fix pack with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack, or <code>null</code> if a patcher fix pack with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherFixPack fetchByPrimaryKey(long patcherFixPackId)
		throws SystemException {
		return fetchByPrimaryKey((Serializable)patcherFixPackId);
	}

	/**
	 * Returns all the patcher fix packs.
	 *
	 * @return the patcher fix packs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherFixPack> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fix packs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherFixPack> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix packs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherFixPack> findAll(int start, int end,
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

		List<PatcherFixPack> list = (List<PatcherFixPack>)FinderCacheUtil.getResult(finderPath,
				finderArgs, this);

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 3));

				query.append(_SQL_SELECT_PATCHERFIXPACK);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERFIXPACK;

				if (pagination) {
					sql = sql.concat(PatcherFixPackModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<PatcherFixPack>)QueryUtil.list(q,
							getDialect(), start, end, false);

					Collections.sort(list);

					list = new UnmodifiableList<PatcherFixPack>(list);
				}
				else {
					list = (List<PatcherFixPack>)QueryUtil.list(q,
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
	 * Removes all the patcher fix packs from the database.
	 *
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removeAll() throws SystemException {
		for (PatcherFixPack patcherFixPack : findAll()) {
			remove(patcherFixPack);
		}
	}

	/**
	 * Returns the number of patcher fix packs.
	 *
	 * @return the number of patcher fix packs
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

				Query q = session.createQuery(_SQL_COUNT_PATCHERFIXPACK);

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
	 * Returns all the patcher fixs associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return the patcher fixs associated with the patcher fix pack
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk) throws SystemException {
		return getPatcherFixs(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the patcher fixs associated with the patcher fix pack.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fixs associated with the patcher fix pack
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk, int start, int end) throws SystemException {
		return getPatcherFixs(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher fixs associated with the patcher fix pack.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixs associated with the patcher fix pack
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<com.liferay.osb.patcher.model.PatcherFix> getPatcherFixs(
		long pk, int start, int end, OrderByComparator orderByComparator)
		throws SystemException {
		return patcherFixPackToPatcherFixTableMapper.getRightBaseModels(pk,
			start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fixs associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return the number of patcher fixs associated with the patcher fix pack
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getPatcherFixsSize(long pk) throws SystemException {
		long[] pks = patcherFixPackToPatcherFixTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher fix pack; <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean containsPatcherFix(long pk, long patcherFixPK)
		throws SystemException {
		return patcherFixPackToPatcherFixTableMapper.containsTableMapping(pk,
			patcherFixPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix pack has any patcher fixs associated with it.
	 *
	 * @param pk the primary key of the patcher fix pack to check for associations with patcher fixs
	 * @return <code>true</code> if the patcher fix pack has any patcher fixs associated with it; <code>false</code> otherwise
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
	 * Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherFix(long pk, long patcherFixPK)
		throws SystemException {
		patcherFixPackToPatcherFixTableMapper.addTableMapping(pk, patcherFixPK);
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFix the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherFix(long pk,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws SystemException {
		patcherFixPackToPatcherFixTableMapper.addTableMapping(pk,
			patcherFix.getPrimaryKey());
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherFixs(long pk, long[] patcherFixPKs)
		throws SystemException {
		for (long patcherFixPK : patcherFixPKs) {
			patcherFixPackToPatcherFixTableMapper.addTableMapping(pk,
				patcherFixPK);
		}
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixs the patcher fixs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPatcherFixs(long pk,
		List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws SystemException {
		for (com.liferay.osb.patcher.model.PatcherFix patcherFix : patcherFixs) {
			patcherFixPackToPatcherFixTableMapper.addTableMapping(pk,
				patcherFix.getPrimaryKey());
		}
	}

	/**
	 * Clears all associations between the patcher fix pack and its patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack to clear the associated patcher fixs from
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void clearPatcherFixs(long pk) throws SystemException {
		patcherFixPackToPatcherFixTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherFix(long pk, long patcherFixPK)
		throws SystemException {
		patcherFixPackToPatcherFixTableMapper.deleteTableMapping(pk,
			patcherFixPK);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFix the patcher fix
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherFix(long pk,
		com.liferay.osb.patcher.model.PatcherFix patcherFix)
		throws SystemException {
		patcherFixPackToPatcherFixTableMapper.deleteTableMapping(pk,
			patcherFix.getPrimaryKey());
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherFixs(long pk, long[] patcherFixPKs)
		throws SystemException {
		for (long patcherFixPK : patcherFixPKs) {
			patcherFixPackToPatcherFixTableMapper.deleteTableMapping(pk,
				patcherFixPK);
		}
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fixs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixs the patcher fixs
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removePatcherFixs(long pk,
		List<com.liferay.osb.patcher.model.PatcherFix> patcherFixs)
		throws SystemException {
		for (com.liferay.osb.patcher.model.PatcherFix patcherFix : patcherFixs) {
			patcherFixPackToPatcherFixTableMapper.deleteTableMapping(pk,
				patcherFix.getPrimaryKey());
		}
	}

	/**
	 * Sets the patcher fixs associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixs to be associated with the patcher fix pack
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void setPatcherFixs(long pk, long[] patcherFixPKs)
		throws SystemException {
		Set<Long> newPatcherFixPKsSet = SetUtil.fromArray(patcherFixPKs);
		Set<Long> oldPatcherFixPKsSet = SetUtil.fromArray(patcherFixPackToPatcherFixTableMapper.getRightPrimaryKeys(
					pk));

		Set<Long> removePatcherFixPKsSet = new HashSet<Long>(oldPatcherFixPKsSet);

		removePatcherFixPKsSet.removeAll(newPatcherFixPKsSet);

		for (long removePatcherFixPK : removePatcherFixPKsSet) {
			patcherFixPackToPatcherFixTableMapper.deleteTableMapping(pk,
				removePatcherFixPK);
		}

		newPatcherFixPKsSet.removeAll(oldPatcherFixPKsSet);

		for (long newPatcherFixPK : newPatcherFixPKsSet) {
			patcherFixPackToPatcherFixTableMapper.addTableMapping(pk,
				newPatcherFixPK);
		}
	}

	/**
	 * Sets the patcher fixs associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixs the patcher fixs to be associated with the patcher fix pack
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
			FinderCacheUtil.clearCache(PatcherFixPackModelImpl.MAPPING_TABLE_OSB_PATCHERFIXES_PATCHERFIXPACKS_NAME);
		}
	}

	/**
	 * Initializes the patcher fix pack persistence.
	 */
	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.util.service.ServiceProps.get(
						"value.object.listener.com.liferay.osb.patcher.model.PatcherFixPack")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<PatcherFixPack>> listenersList = new ArrayList<ModelListener<PatcherFixPack>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<PatcherFixPack>)InstanceFactory.newInstance(
							getClassLoader(), listenerClassName));
				}

				listeners = listenersList.toArray(new ModelListener[listenersList.size()]);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}

		patcherFixPackToPatcherFixTableMapper = TableMapperFactory.getTableMapper("OSB_PatcherFixes_PatcherFixPacks",
				"patcherFixPackId", "patcherFixId", this, patcherFixPersistence);
	}

	public void destroy() {
		EntityCacheUtil.removeCache(PatcherFixPackImpl.class.getName());
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		TableMapperFactory.removeTableMapper("OSB_PatcherFixes_PatcherFixPacks");
	}

	@BeanReference(type = PatcherFixPersistence.class)
	protected PatcherFixPersistence patcherFixPersistence;
	protected TableMapper<PatcherFixPack, com.liferay.osb.patcher.model.PatcherFix> patcherFixPackToPatcherFixTableMapper;
	private static final String _SQL_SELECT_PATCHERFIXPACK = "SELECT patcherFixPack FROM PatcherFixPack patcherFixPack";
	private static final String _SQL_SELECT_PATCHERFIXPACK_WHERE = "SELECT patcherFixPack FROM PatcherFixPack patcherFixPack WHERE ";
	private static final String _SQL_COUNT_PATCHERFIXPACK = "SELECT COUNT(patcherFixPack) FROM PatcherFixPack patcherFixPack";
	private static final String _SQL_COUNT_PATCHERFIXPACK_WHERE = "SELECT COUNT(patcherFixPack) FROM PatcherFixPack patcherFixPack WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherFixPack.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No PatcherFixPack exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No PatcherFixPack exists with the key {";
	private static final boolean _HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = GetterUtil.getBoolean(PropsUtil.get(
				PropsKeys.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
	private static Log _log = LogFactoryUtil.getLog(PatcherFixPackPersistenceImpl.class);
	private static PatcherFixPack _nullPatcherFixPack = new PatcherFixPackImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<PatcherFixPack> toCacheModel() {
				return _nullPatcherFixPackCacheModel;
			}
		};

	private static CacheModel<PatcherFixPack> _nullPatcherFixPackCacheModel = new CacheModel<PatcherFixPack>() {
			@Override
			public PatcherFixPack toEntityModel() {
				return _nullPatcherFixPack;
			}
		};
}