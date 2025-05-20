/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.NoSuchPatcherTicketHintException;
import com.liferay.osb.patcher.model.PatcherTicketHint;
import com.liferay.osb.patcher.model.impl.PatcherTicketHintImpl;
import com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl;

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
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
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
 * The persistence implementation for the patcher ticket hint service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherTicketHintPersistence
 * @see PatcherTicketHintUtil
 * @generated
 */
public class PatcherTicketHintPersistenceImpl extends BasePersistenceImpl<PatcherTicketHint>
	implements PatcherTicketHintPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link PatcherTicketHintUtil} to access the patcher ticket hint persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = PatcherTicketHintImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
			PatcherTicketHintModelImpl.FINDER_CACHE_ENABLED,
			PatcherTicketHintImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
			PatcherTicketHintModelImpl.FINDER_CACHE_ENABLED,
			PatcherTicketHintImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
			PatcherTicketHintModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);
	public static final FinderPath FINDER_PATH_FETCH_BY_PATCHERPRODUCTVERSIONID = new FinderPath(PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
			PatcherTicketHintModelImpl.FINDER_CACHE_ENABLED,
			PatcherTicketHintImpl.class, FINDER_CLASS_NAME_ENTITY,
			"fetchByPatcherProductVersionId",
			new String[] { Long.class.getName() },
			PatcherTicketHintModelImpl.PATCHERPRODUCTVERSIONID_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_PATCHERPRODUCTVERSIONID = new FinderPath(PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
			PatcherTicketHintModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByPatcherProductVersionId",
			new String[] { Long.class.getName() });

	/**
	 * Returns the patcher ticket hint where patcherProductVersionId = &#63; or throws a {@link com.liferay.osb.patcher.NoSuchPatcherTicketHintException} if it could not be found.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher ticket hint
	 * @throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException if a matching patcher ticket hint could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherTicketHint findByPatcherProductVersionId(
		long patcherProductVersionId)
		throws NoSuchPatcherTicketHintException, SystemException {
		PatcherTicketHint patcherTicketHint = fetchByPatcherProductVersionId(patcherProductVersionId);

		if (patcherTicketHint == null) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("patcherProductVersionId=");
			msg.append(patcherProductVersionId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			if (_log.isWarnEnabled()) {
				_log.warn(msg.toString());
			}

			throw new NoSuchPatcherTicketHintException(msg.toString());
		}

		return patcherTicketHint;
	}

	/**
	 * Returns the patcher ticket hint where patcherProductVersionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher ticket hint, or <code>null</code> if a matching patcher ticket hint could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherTicketHint fetchByPatcherProductVersionId(
		long patcherProductVersionId) throws SystemException {
		return fetchByPatcherProductVersionId(patcherProductVersionId, true);
	}

	/**
	 * Returns the patcher ticket hint where patcherProductVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param retrieveFromCache whether to use the finder cache
	 * @return the matching patcher ticket hint, or <code>null</code> if a matching patcher ticket hint could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherTicketHint fetchByPatcherProductVersionId(
		long patcherProductVersionId, boolean retrieveFromCache)
		throws SystemException {
		Object[] finderArgs = new Object[] { patcherProductVersionId };

		Object result = null;

		if (retrieveFromCache) {
			result = FinderCacheUtil.getResult(FINDER_PATH_FETCH_BY_PATCHERPRODUCTVERSIONID,
					finderArgs, this);
		}

		if (result instanceof PatcherTicketHint) {
			PatcherTicketHint patcherTicketHint = (PatcherTicketHint)result;

			if ((patcherProductVersionId != patcherTicketHint.getPatcherProductVersionId())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_SELECT_PATCHERTICKETHINT_WHERE);

			query.append(_FINDER_COLUMN_PATCHERPRODUCTVERSIONID_PATCHERPRODUCTVERSIONID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(patcherProductVersionId);

				List<PatcherTicketHint> list = q.list();

				if (list.isEmpty()) {
					FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_PATCHERPRODUCTVERSIONID,
						finderArgs, list);
				}
				else {
					PatcherTicketHint patcherTicketHint = list.get(0);

					result = patcherTicketHint;

					cacheResult(patcherTicketHint);

					if ((patcherTicketHint.getPatcherProductVersionId() != patcherProductVersionId)) {
						FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_PATCHERPRODUCTVERSIONID,
							finderArgs, patcherTicketHint);
					}
				}
			}
			catch (Exception e) {
				FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_PATCHERPRODUCTVERSIONID,
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
			return (PatcherTicketHint)result;
		}
	}

	/**
	 * Removes the patcher ticket hint where patcherProductVersionId = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the patcher ticket hint that was removed
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherTicketHint removeByPatcherProductVersionId(
		long patcherProductVersionId)
		throws NoSuchPatcherTicketHintException, SystemException {
		PatcherTicketHint patcherTicketHint = findByPatcherProductVersionId(patcherProductVersionId);

		return remove(patcherTicketHint);
	}

	/**
	 * Returns the number of patcher ticket hints where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher ticket hints
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int countByPatcherProductVersionId(long patcherProductVersionId)
		throws SystemException {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_PATCHERPRODUCTVERSIONID;

		Object[] finderArgs = new Object[] { patcherProductVersionId };

		Long count = (Long)FinderCacheUtil.getResult(finderPath, finderArgs,
				this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_PATCHERTICKETHINT_WHERE);

			query.append(_FINDER_COLUMN_PATCHERPRODUCTVERSIONID_PATCHERPRODUCTVERSIONID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(patcherProductVersionId);

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

	private static final String _FINDER_COLUMN_PATCHERPRODUCTVERSIONID_PATCHERPRODUCTVERSIONID_2 =
		"patcherTicketHint.patcherProductVersionId = ?";

	public PatcherTicketHintPersistenceImpl() {
		setModelClass(PatcherTicketHint.class);
	}

	/**
	 * Caches the patcher ticket hint in the entity cache if it is enabled.
	 *
	 * @param patcherTicketHint the patcher ticket hint
	 */
	@Override
	public void cacheResult(PatcherTicketHint patcherTicketHint) {
		EntityCacheUtil.putResult(PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
			PatcherTicketHintImpl.class, patcherTicketHint.getPrimaryKey(),
			patcherTicketHint);

		FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_PATCHERPRODUCTVERSIONID,
			new Object[] { patcherTicketHint.getPatcherProductVersionId() },
			patcherTicketHint);

		patcherTicketHint.resetOriginalValues();
	}

	/**
	 * Caches the patcher ticket hints in the entity cache if it is enabled.
	 *
	 * @param patcherTicketHints the patcher ticket hints
	 */
	@Override
	public void cacheResult(List<PatcherTicketHint> patcherTicketHints) {
		for (PatcherTicketHint patcherTicketHint : patcherTicketHints) {
			if (EntityCacheUtil.getResult(
						PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
						PatcherTicketHintImpl.class,
						patcherTicketHint.getPrimaryKey()) == null) {
				cacheResult(patcherTicketHint);
			}
			else {
				patcherTicketHint.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all patcher ticket hints.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		if (_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			CacheRegistryUtil.clear(PatcherTicketHintImpl.class.getName());
		}

		EntityCacheUtil.clearCache(PatcherTicketHintImpl.class.getName());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the patcher ticket hint.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherTicketHint patcherTicketHint) {
		EntityCacheUtil.removeResult(PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
			PatcherTicketHintImpl.class, patcherTicketHint.getPrimaryKey());

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache(patcherTicketHint);
	}

	@Override
	public void clearCache(List<PatcherTicketHint> patcherTicketHints) {
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (PatcherTicketHint patcherTicketHint : patcherTicketHints) {
			EntityCacheUtil.removeResult(PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
				PatcherTicketHintImpl.class, patcherTicketHint.getPrimaryKey());

			clearUniqueFindersCache(patcherTicketHint);
		}
	}

	protected void cacheUniqueFindersCache(PatcherTicketHint patcherTicketHint) {
		if (patcherTicketHint.isNew()) {
			Object[] args = new Object[] {
					patcherTicketHint.getPatcherProductVersionId()
				};

			FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_PATCHERPRODUCTVERSIONID,
				args, Long.valueOf(1));
			FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_PATCHERPRODUCTVERSIONID,
				args, patcherTicketHint);
		}
		else {
			PatcherTicketHintModelImpl patcherTicketHintModelImpl = (PatcherTicketHintModelImpl)patcherTicketHint;

			if ((patcherTicketHintModelImpl.getColumnBitmask() &
					FINDER_PATH_FETCH_BY_PATCHERPRODUCTVERSIONID.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						patcherTicketHint.getPatcherProductVersionId()
					};

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_PATCHERPRODUCTVERSIONID,
					args, Long.valueOf(1));
				FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_PATCHERPRODUCTVERSIONID,
					args, patcherTicketHint);
			}
		}
	}

	protected void clearUniqueFindersCache(PatcherTicketHint patcherTicketHint) {
		PatcherTicketHintModelImpl patcherTicketHintModelImpl = (PatcherTicketHintModelImpl)patcherTicketHint;

		Object[] args = new Object[] {
				patcherTicketHint.getPatcherProductVersionId()
			};

		FinderCacheUtil.removeResult(FINDER_PATH_COUNT_BY_PATCHERPRODUCTVERSIONID,
			args);
		FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_PATCHERPRODUCTVERSIONID,
			args);

		if ((patcherTicketHintModelImpl.getColumnBitmask() &
				FINDER_PATH_FETCH_BY_PATCHERPRODUCTVERSIONID.getColumnBitmask()) != 0) {
			args = new Object[] {
					patcherTicketHintModelImpl.getOriginalPatcherProductVersionId()
				};

			FinderCacheUtil.removeResult(FINDER_PATH_COUNT_BY_PATCHERPRODUCTVERSIONID,
				args);
			FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_PATCHERPRODUCTVERSIONID,
				args);
		}
	}

	/**
	 * Creates a new patcher ticket hint with the primary key. Does not add the patcher ticket hint to the database.
	 *
	 * @param patcherTicketHintId the primary key for the new patcher ticket hint
	 * @return the new patcher ticket hint
	 */
	@Override
	public PatcherTicketHint create(long patcherTicketHintId) {
		PatcherTicketHint patcherTicketHint = new PatcherTicketHintImpl();

		patcherTicketHint.setNew(true);
		patcherTicketHint.setPrimaryKey(patcherTicketHintId);

		return patcherTicketHint;
	}

	/**
	 * Removes the patcher ticket hint with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherTicketHint remove(long patcherTicketHintId)
		throws NoSuchPatcherTicketHintException, SystemException {
		return remove((Serializable)patcherTicketHintId);
	}

	/**
	 * Removes the patcher ticket hint with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher ticket hint
	 * @return the patcher ticket hint that was removed
	 * @throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherTicketHint remove(Serializable primaryKey)
		throws NoSuchPatcherTicketHintException, SystemException {
		Session session = null;

		try {
			session = openSession();

			PatcherTicketHint patcherTicketHint = (PatcherTicketHint)session.get(PatcherTicketHintImpl.class,
					primaryKey);

			if (patcherTicketHint == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherTicketHintException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(patcherTicketHint);
		}
		catch (NoSuchPatcherTicketHintException nsee) {
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
	protected PatcherTicketHint removeImpl(PatcherTicketHint patcherTicketHint)
		throws SystemException {
		patcherTicketHint = toUnwrappedModel(patcherTicketHint);

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherTicketHint)) {
				patcherTicketHint = (PatcherTicketHint)session.get(PatcherTicketHintImpl.class,
						patcherTicketHint.getPrimaryKeyObj());
			}

			if (patcherTicketHint != null) {
				session.delete(patcherTicketHint);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (patcherTicketHint != null) {
			clearCache(patcherTicketHint);
		}

		return patcherTicketHint;
	}

	@Override
	public PatcherTicketHint updateImpl(
		com.liferay.osb.patcher.model.PatcherTicketHint patcherTicketHint)
		throws SystemException {
		patcherTicketHint = toUnwrappedModel(patcherTicketHint);

		boolean isNew = patcherTicketHint.isNew();

		Session session = null;

		try {
			session = openSession();

			if (patcherTicketHint.isNew()) {
				session.save(patcherTicketHint);

				patcherTicketHint.setNew(false);
			}
			else {
				session.merge(patcherTicketHint);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (isNew || !PatcherTicketHintModelImpl.COLUMN_BITMASK_ENABLED) {
			FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}

		EntityCacheUtil.putResult(PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
			PatcherTicketHintImpl.class, patcherTicketHint.getPrimaryKey(),
			patcherTicketHint);

		clearUniqueFindersCache(patcherTicketHint);
		cacheUniqueFindersCache(patcherTicketHint);

		return patcherTicketHint;
	}

	protected PatcherTicketHint toUnwrappedModel(
		PatcherTicketHint patcherTicketHint) {
		if (patcherTicketHint instanceof PatcherTicketHintImpl) {
			return patcherTicketHint;
		}

		PatcherTicketHintImpl patcherTicketHintImpl = new PatcherTicketHintImpl();

		patcherTicketHintImpl.setNew(patcherTicketHint.isNew());
		patcherTicketHintImpl.setPrimaryKey(patcherTicketHint.getPrimaryKey());

		patcherTicketHintImpl.setPatcherTicketHintId(patcherTicketHint.getPatcherTicketHintId());
		patcherTicketHintImpl.setCompanyId(patcherTicketHint.getCompanyId());
		patcherTicketHintImpl.setUserId(patcherTicketHint.getUserId());
		patcherTicketHintImpl.setUserName(patcherTicketHint.getUserName());
		patcherTicketHintImpl.setCreateDate(patcherTicketHint.getCreateDate());
		patcherTicketHintImpl.setModifiedDate(patcherTicketHint.getModifiedDate());
		patcherTicketHintImpl.setPatcherProductVersionId(patcherTicketHint.getPatcherProductVersionId());
		patcherTicketHintImpl.setScript(patcherTicketHint.getScript());

		return patcherTicketHintImpl;
	}

	/**
	 * Returns the patcher ticket hint with the primary key or throws a {@link com.liferay.portal.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher ticket hint
	 * @return the patcher ticket hint
	 * @throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherTicketHint findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherTicketHintException, SystemException {
		PatcherTicketHint patcherTicketHint = fetchByPrimaryKey(primaryKey);

		if (patcherTicketHint == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherTicketHintException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return patcherTicketHint;
	}

	/**
	 * Returns the patcher ticket hint with the primary key or throws a {@link com.liferay.osb.patcher.NoSuchPatcherTicketHintException} if it could not be found.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint
	 * @throws com.liferay.osb.patcher.NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherTicketHint findByPrimaryKey(long patcherTicketHintId)
		throws NoSuchPatcherTicketHintException, SystemException {
		return findByPrimaryKey((Serializable)patcherTicketHintId);
	}

	/**
	 * Returns the patcher ticket hint with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher ticket hint
	 * @return the patcher ticket hint, or <code>null</code> if a patcher ticket hint with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherTicketHint fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		PatcherTicketHint patcherTicketHint = (PatcherTicketHint)EntityCacheUtil.getResult(PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
				PatcherTicketHintImpl.class, primaryKey);

		if (patcherTicketHint == _nullPatcherTicketHint) {
			return null;
		}

		if (patcherTicketHint == null) {
			Session session = null;

			try {
				session = openSession();

				patcherTicketHint = (PatcherTicketHint)session.get(PatcherTicketHintImpl.class,
						primaryKey);

				if (patcherTicketHint != null) {
					cacheResult(patcherTicketHint);
				}
				else {
					EntityCacheUtil.putResult(PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
						PatcherTicketHintImpl.class, primaryKey,
						_nullPatcherTicketHint);
				}
			}
			catch (Exception e) {
				EntityCacheUtil.removeResult(PatcherTicketHintModelImpl.ENTITY_CACHE_ENABLED,
					PatcherTicketHintImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return patcherTicketHint;
	}

	/**
	 * Returns the patcher ticket hint with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint, or <code>null</code> if a patcher ticket hint with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public PatcherTicketHint fetchByPrimaryKey(long patcherTicketHintId)
		throws SystemException {
		return fetchByPrimaryKey((Serializable)patcherTicketHintId);
	}

	/**
	 * Returns all the patcher ticket hints.
	 *
	 * @return the patcher ticket hints
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherTicketHint> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher ticket hints.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher ticket hints
	 * @param end the upper bound of the range of patcher ticket hints (not inclusive)
	 * @return the range of patcher ticket hints
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherTicketHint> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher ticket hints.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher ticket hints
	 * @param end the upper bound of the range of patcher ticket hints (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher ticket hints
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<PatcherTicketHint> findAll(int start, int end,
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

		List<PatcherTicketHint> list = (List<PatcherTicketHint>)FinderCacheUtil.getResult(finderPath,
				finderArgs, this);

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 3));

				query.append(_SQL_SELECT_PATCHERTICKETHINT);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERTICKETHINT;

				if (pagination) {
					sql = sql.concat(PatcherTicketHintModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<PatcherTicketHint>)QueryUtil.list(q,
							getDialect(), start, end, false);

					Collections.sort(list);

					list = new UnmodifiableList<PatcherTicketHint>(list);
				}
				else {
					list = (List<PatcherTicketHint>)QueryUtil.list(q,
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
	 * Removes all the patcher ticket hints from the database.
	 *
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void removeAll() throws SystemException {
		for (PatcherTicketHint patcherTicketHint : findAll()) {
			remove(patcherTicketHint);
		}
	}

	/**
	 * Returns the number of patcher ticket hints.
	 *
	 * @return the number of patcher ticket hints
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

				Query q = session.createQuery(_SQL_COUNT_PATCHERTICKETHINT);

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
	 * Initializes the patcher ticket hint persistence.
	 */
	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.util.service.ServiceProps.get(
						"value.object.listener.com.liferay.osb.patcher.model.PatcherTicketHint")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<PatcherTicketHint>> listenersList = new ArrayList<ModelListener<PatcherTicketHint>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<PatcherTicketHint>)InstanceFactory.newInstance(
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
		EntityCacheUtil.removeCache(PatcherTicketHintImpl.class.getName());
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		FinderCacheUtil.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	private static final String _SQL_SELECT_PATCHERTICKETHINT = "SELECT patcherTicketHint FROM PatcherTicketHint patcherTicketHint";
	private static final String _SQL_SELECT_PATCHERTICKETHINT_WHERE = "SELECT patcherTicketHint FROM PatcherTicketHint patcherTicketHint WHERE ";
	private static final String _SQL_COUNT_PATCHERTICKETHINT = "SELECT COUNT(patcherTicketHint) FROM PatcherTicketHint patcherTicketHint";
	private static final String _SQL_COUNT_PATCHERTICKETHINT_WHERE = "SELECT COUNT(patcherTicketHint) FROM PatcherTicketHint patcherTicketHint WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherTicketHint.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No PatcherTicketHint exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No PatcherTicketHint exists with the key {";
	private static final boolean _HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = GetterUtil.getBoolean(PropsUtil.get(
				PropsKeys.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
	private static Log _log = LogFactoryUtil.getLog(PatcherTicketHintPersistenceImpl.class);
	private static PatcherTicketHint _nullPatcherTicketHint = new PatcherTicketHintImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<PatcherTicketHint> toCacheModel() {
				return _nullPatcherTicketHintCacheModel;
			}
		};

	private static CacheModel<PatcherTicketHint> _nullPatcherTicketHintCacheModel =
		new CacheModel<PatcherTicketHint>() {
			@Override
			public PatcherTicketHint toEntityModel() {
				return _nullPatcherTicketHint;
			}
		};
}