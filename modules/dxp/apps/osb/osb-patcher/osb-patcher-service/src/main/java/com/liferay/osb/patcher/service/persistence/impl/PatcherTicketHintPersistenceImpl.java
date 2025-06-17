/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.impl;

import com.liferay.osb.patcher.exception.NoSuchPatcherTicketHintException;
import com.liferay.osb.patcher.model.PatcherTicketHint;
import com.liferay.osb.patcher.model.PatcherTicketHintTable;
import com.liferay.osb.patcher.model.impl.PatcherTicketHintImpl;
import com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl;
import com.liferay.osb.patcher.service.persistence.PatcherTicketHintPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherTicketHintUtil;
import com.liferay.osb.patcher.service.persistence.impl.constants.OSBPatcherPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the patcher ticket hint service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PatcherTicketHintPersistence.class)
public class PatcherTicketHintPersistenceImpl
	extends BasePersistenceImpl<PatcherTicketHint>
	implements PatcherTicketHintPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PatcherTicketHintUtil</code> to access the patcher ticket hint persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PatcherTicketHintImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByPatcherProductVersionId;

	/**
	 * Returns the patcher ticket hint where patcherProductVersionId = &#63; or throws a <code>NoSuchPatcherTicketHintException</code> if it could not be found.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher ticket hint
	 * @throws NoSuchPatcherTicketHintException if a matching patcher ticket hint could not be found
	 */
	@Override
	public PatcherTicketHint findByPatcherProductVersionId(
			long patcherProductVersionId)
		throws NoSuchPatcherTicketHintException {

		PatcherTicketHint patcherTicketHint = fetchByPatcherProductVersionId(
			patcherProductVersionId);

		if (patcherTicketHint == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("patcherProductVersionId=");
			sb.append(patcherProductVersionId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPatcherTicketHintException(sb.toString());
		}

		return patcherTicketHint;
	}

	/**
	 * Returns the patcher ticket hint where patcherProductVersionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher ticket hint, or <code>null</code> if a matching patcher ticket hint could not be found
	 */
	@Override
	public PatcherTicketHint fetchByPatcherProductVersionId(
		long patcherProductVersionId) {

		return fetchByPatcherProductVersionId(patcherProductVersionId, true);
	}

	/**
	 * Returns the patcher ticket hint where patcherProductVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher ticket hint, or <code>null</code> if a matching patcher ticket hint could not be found
	 */
	@Override
	public PatcherTicketHint fetchByPatcherProductVersionId(
		long patcherProductVersionId, boolean useFinderCache) {

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {patcherProductVersionId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByPatcherProductVersionId, finderArgs, this);
		}

		if (result instanceof PatcherTicketHint) {
			PatcherTicketHint patcherTicketHint = (PatcherTicketHint)result;

			if (patcherProductVersionId !=
					patcherTicketHint.getPatcherProductVersionId()) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_PATCHERTICKETHINT_WHERE);

			sb.append(
				_FINDER_COLUMN_PATCHERPRODUCTVERSIONID_PATCHERPRODUCTVERSIONID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(patcherProductVersionId);

				List<PatcherTicketHint> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByPatcherProductVersionId,
							finderArgs, list);
					}
				}
				else {
					PatcherTicketHint patcherTicketHint = list.get(0);

					result = patcherTicketHint;

					cacheResult(patcherTicketHint);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
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
	 */
	@Override
	public PatcherTicketHint removeByPatcherProductVersionId(
			long patcherProductVersionId)
		throws NoSuchPatcherTicketHintException {

		PatcherTicketHint patcherTicketHint = findByPatcherProductVersionId(
			patcherProductVersionId);

		return remove(patcherTicketHint);
	}

	/**
	 * Returns the number of patcher ticket hints where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher ticket hints
	 */
	@Override
	public int countByPatcherProductVersionId(long patcherProductVersionId) {
		PatcherTicketHint patcherTicketHint = fetchByPatcherProductVersionId(
			patcherProductVersionId);

		if (patcherTicketHint == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_PATCHERPRODUCTVERSIONID_PATCHERPRODUCTVERSIONID_2 =
			"patcherTicketHint.patcherProductVersionId = ?";

	public PatcherTicketHintPersistenceImpl() {
		setModelClass(PatcherTicketHint.class);

		setModelImplClass(PatcherTicketHintImpl.class);
		setModelPKClass(long.class);

		setTable(PatcherTicketHintTable.INSTANCE);
	}

	/**
	 * Caches the patcher ticket hint in the entity cache if it is enabled.
	 *
	 * @param patcherTicketHint the patcher ticket hint
	 */
	@Override
	public void cacheResult(PatcherTicketHint patcherTicketHint) {
		entityCache.putResult(
			PatcherTicketHintImpl.class, patcherTicketHint.getPrimaryKey(),
			patcherTicketHint);

		finderCache.putResult(
			_finderPathFetchByPatcherProductVersionId,
			new Object[] {patcherTicketHint.getPatcherProductVersionId()},
			patcherTicketHint);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the patcher ticket hints in the entity cache if it is enabled.
	 *
	 * @param patcherTicketHints the patcher ticket hints
	 */
	@Override
	public void cacheResult(List<PatcherTicketHint> patcherTicketHints) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (patcherTicketHints.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PatcherTicketHint patcherTicketHint : patcherTicketHints) {
			if (entityCache.getResult(
					PatcherTicketHintImpl.class,
					patcherTicketHint.getPrimaryKey()) == null) {

				cacheResult(patcherTicketHint);
			}
		}
	}

	/**
	 * Clears the cache for all patcher ticket hints.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PatcherTicketHintImpl.class);

		finderCache.clearCache(PatcherTicketHintImpl.class);
	}

	/**
	 * Clears the cache for the patcher ticket hint.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PatcherTicketHint patcherTicketHint) {
		entityCache.removeResult(
			PatcherTicketHintImpl.class, patcherTicketHint);
	}

	@Override
	public void clearCache(List<PatcherTicketHint> patcherTicketHints) {
		for (PatcherTicketHint patcherTicketHint : patcherTicketHints) {
			entityCache.removeResult(
				PatcherTicketHintImpl.class, patcherTicketHint);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PatcherTicketHintImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PatcherTicketHintImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		PatcherTicketHintModelImpl patcherTicketHintModelImpl) {

		Object[] args = new Object[] {
			patcherTicketHintModelImpl.getPatcherProductVersionId()
		};

		finderCache.putResult(
			_finderPathFetchByPatcherProductVersionId, args,
			patcherTicketHintModelImpl);
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

		patcherTicketHint.setCompanyId(CompanyThreadLocal.getCompanyId());

		return patcherTicketHint;
	}

	/**
	 * Removes the patcher ticket hint with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint that was removed
	 * @throws NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	 */
	@Override
	public PatcherTicketHint remove(long patcherTicketHintId)
		throws NoSuchPatcherTicketHintException {

		return remove((Serializable)patcherTicketHintId);
	}

	/**
	 * Removes the patcher ticket hint with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the patcher ticket hint
	 * @return the patcher ticket hint that was removed
	 * @throws NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	 */
	@Override
	public PatcherTicketHint remove(Serializable primaryKey)
		throws NoSuchPatcherTicketHintException {

		Session session = null;

		try {
			session = openSession();

			PatcherTicketHint patcherTicketHint =
				(PatcherTicketHint)session.get(
					PatcherTicketHintImpl.class, primaryKey);

			if (patcherTicketHint == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPatcherTicketHintException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(patcherTicketHint);
		}
		catch (NoSuchPatcherTicketHintException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected PatcherTicketHint removeImpl(
		PatcherTicketHint patcherTicketHint) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(patcherTicketHint)) {
				patcherTicketHint = (PatcherTicketHint)session.get(
					PatcherTicketHintImpl.class,
					patcherTicketHint.getPrimaryKeyObj());
			}

			if (patcherTicketHint != null) {
				session.delete(patcherTicketHint);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
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
	public PatcherTicketHint updateImpl(PatcherTicketHint patcherTicketHint) {
		boolean isNew = patcherTicketHint.isNew();

		if (!(patcherTicketHint instanceof PatcherTicketHintModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(patcherTicketHint.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					patcherTicketHint);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in patcherTicketHint proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PatcherTicketHint implementation " +
					patcherTicketHint.getClass());
		}

		PatcherTicketHintModelImpl patcherTicketHintModelImpl =
			(PatcherTicketHintModelImpl)patcherTicketHint;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (patcherTicketHint.getCreateDate() == null)) {
			if (serviceContext == null) {
				patcherTicketHint.setCreateDate(date);
			}
			else {
				patcherTicketHint.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!patcherTicketHintModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				patcherTicketHint.setModifiedDate(date);
			}
			else {
				patcherTicketHint.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(patcherTicketHint);
			}
			else {
				patcherTicketHint = (PatcherTicketHint)session.merge(
					patcherTicketHint);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PatcherTicketHintImpl.class, patcherTicketHintModelImpl, false,
			true);

		cacheUniqueFindersCache(patcherTicketHintModelImpl);

		if (isNew) {
			patcherTicketHint.setNew(false);
		}

		patcherTicketHint.resetOriginalValues();

		return patcherTicketHint;
	}

	/**
	 * Returns the patcher ticket hint with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the patcher ticket hint
	 * @return the patcher ticket hint
	 * @throws NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	 */
	@Override
	public PatcherTicketHint findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPatcherTicketHintException {

		PatcherTicketHint patcherTicketHint = fetchByPrimaryKey(primaryKey);

		if (patcherTicketHint == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPatcherTicketHintException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return patcherTicketHint;
	}

	/**
	 * Returns the patcher ticket hint with the primary key or throws a <code>NoSuchPatcherTicketHintException</code> if it could not be found.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint
	 * @throws NoSuchPatcherTicketHintException if a patcher ticket hint with the primary key could not be found
	 */
	@Override
	public PatcherTicketHint findByPrimaryKey(long patcherTicketHintId)
		throws NoSuchPatcherTicketHintException {

		return findByPrimaryKey((Serializable)patcherTicketHintId);
	}

	/**
	 * Returns the patcher ticket hint with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint, or <code>null</code> if a patcher ticket hint with the primary key could not be found
	 */
	@Override
	public PatcherTicketHint fetchByPrimaryKey(long patcherTicketHintId) {
		return fetchByPrimaryKey((Serializable)patcherTicketHintId);
	}

	/**
	 * Returns all the patcher ticket hints.
	 *
	 * @return the patcher ticket hints
	 */
	@Override
	public List<PatcherTicketHint> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the patcher ticket hints.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherTicketHintModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher ticket hints
	 * @param end the upper bound of the range of patcher ticket hints (not inclusive)
	 * @return the range of patcher ticket hints
	 */
	@Override
	public List<PatcherTicketHint> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the patcher ticket hints.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherTicketHintModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher ticket hints
	 * @param end the upper bound of the range of patcher ticket hints (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher ticket hints
	 */
	@Override
	public List<PatcherTicketHint> findAll(
		int start, int end,
		OrderByComparator<PatcherTicketHint> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the patcher ticket hints.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherTicketHintModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher ticket hints
	 * @param end the upper bound of the range of patcher ticket hints (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher ticket hints
	 */
	@Override
	public List<PatcherTicketHint> findAll(
		int start, int end,
		OrderByComparator<PatcherTicketHint> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<PatcherTicketHint> list = null;

		if (useFinderCache) {
			list = (List<PatcherTicketHint>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PATCHERTICKETHINT);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PATCHERTICKETHINT;

				sql = sql.concat(PatcherTicketHintModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PatcherTicketHint>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
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
	 */
	@Override
	public void removeAll() {
		for (PatcherTicketHint patcherTicketHint : findAll()) {
			remove(patcherTicketHint);
		}
	}

	/**
	 * Returns the number of patcher ticket hints.
	 *
	 * @return the number of patcher ticket hints
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PATCHERTICKETHINT);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "patcherTicketHintId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PATCHERTICKETHINT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PatcherTicketHintModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the patcher ticket hint persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathFetchByPatcherProductVersionId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByPatcherProductVersionId",
			new String[] {Long.class.getName()},
			new String[] {"patcherProductVersionId"}, true);

		PatcherTicketHintUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PatcherTicketHintUtil.setPersistence(null);

		entityCache.removeCache(PatcherTicketHintImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBPatcherPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_PATCHERTICKETHINT =
		"SELECT patcherTicketHint FROM PatcherTicketHint patcherTicketHint";

	private static final String _SQL_SELECT_PATCHERTICKETHINT_WHERE =
		"SELECT patcherTicketHint FROM PatcherTicketHint patcherTicketHint WHERE ";

	private static final String _SQL_COUNT_PATCHERTICKETHINT =
		"SELECT COUNT(patcherTicketHint) FROM PatcherTicketHint patcherTicketHint";

	private static final String _SQL_COUNT_PATCHERTICKETHINT_WHERE =
		"SELECT COUNT(patcherTicketHint) FROM PatcherTicketHint patcherTicketHint WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "patcherTicketHint.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PatcherTicketHint exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PatcherTicketHint exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherTicketHintPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}