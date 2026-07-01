/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchRecentLayoutBranchException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RecentLayoutBranch;
import com.liferay.portal.kernel.model.RecentLayoutBranchTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.RecentLayoutBranchPersistence;
import com.liferay.portal.kernel.service.persistence.RecentLayoutBranchUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.RecentLayoutBranchImpl;
import com.liferay.portal.model.impl.RecentLayoutBranchModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the recent layout branch service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RecentLayoutBranchPersistenceImpl
	extends BasePersistenceImpl
		<RecentLayoutBranch, NoSuchRecentLayoutBranchException>
	implements RecentLayoutBranchPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RecentLayoutBranchUtil</code> to access the recent layout branch persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RecentLayoutBranchImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<RecentLayoutBranch, NoSuchRecentLayoutBranchException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the recent layout branches where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout branches
	 */
	@Override
	public List<RecentLayoutBranch> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RecentLayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a matching recent layout branch could not be found
	 */
	@Override
	public RecentLayoutBranch findByGroupId_First(
			long groupId,
			OrderByComparator<RecentLayoutBranch> orderByComparator)
		throws NoSuchRecentLayoutBranchException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first recent layout branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	@Override
	public RecentLayoutBranch fetchByGroupId_First(
		long groupId, OrderByComparator<RecentLayoutBranch> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the recent layout branches where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of recent layout branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching recent layout branches
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<RecentLayoutBranch, NoSuchRecentLayoutBranchException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the recent layout branches where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout branches
	 */
	@Override
	public List<RecentLayoutBranch> findByUserId(
		long userId, int start, int end,
		OrderByComparator<RecentLayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout branch in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a matching recent layout branch could not be found
	 */
	@Override
	public RecentLayoutBranch findByUserId_First(
			long userId,
			OrderByComparator<RecentLayoutBranch> orderByComparator)
		throws NoSuchRecentLayoutBranchException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first recent layout branch in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	@Override
	public RecentLayoutBranch fetchByUserId_First(
		long userId, OrderByComparator<RecentLayoutBranch> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the recent layout branches where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of recent layout branches where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching recent layout branches
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<RecentLayoutBranch, NoSuchRecentLayoutBranchException>
			_collectionPersistenceFinderByLayoutBranchId;

	/**
	 * Returns an ordered range of all the recent layout branches where layoutBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutBranchId the layout branch ID
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout branches
	 */
	@Override
	public List<RecentLayoutBranch> findByLayoutBranchId(
		long layoutBranchId, int start, int end,
		OrderByComparator<RecentLayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutBranchId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutBranchId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout branch in the ordered set where layoutBranchId = &#63;.
	 *
	 * @param layoutBranchId the layout branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a matching recent layout branch could not be found
	 */
	@Override
	public RecentLayoutBranch findByLayoutBranchId_First(
			long layoutBranchId,
			OrderByComparator<RecentLayoutBranch> orderByComparator)
		throws NoSuchRecentLayoutBranchException {

		return _collectionPersistenceFinderByLayoutBranchId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutBranchId},
			orderByComparator);
	}

	/**
	 * Returns the first recent layout branch in the ordered set where layoutBranchId = &#63;.
	 *
	 * @param layoutBranchId the layout branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	@Override
	public RecentLayoutBranch fetchByLayoutBranchId_First(
		long layoutBranchId,
		OrderByComparator<RecentLayoutBranch> orderByComparator) {

		return _collectionPersistenceFinderByLayoutBranchId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutBranchId},
			orderByComparator);
	}

	/**
	 * Removes all the recent layout branches where layoutBranchId = &#63; from the database.
	 *
	 * @param layoutBranchId the layout branch ID
	 */
	@Override
	public void removeByLayoutBranchId(long layoutBranchId) {
		_collectionPersistenceFinderByLayoutBranchId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutBranchId});
	}

	/**
	 * Returns the number of recent layout branches where layoutBranchId = &#63;.
	 *
	 * @param layoutBranchId the layout branch ID
	 * @return the number of matching recent layout branches
	 */
	@Override
	public int countByLayoutBranchId(long layoutBranchId) {
		return _collectionPersistenceFinderByLayoutBranchId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutBranchId});
	}

	private UniquePersistenceFinder
		<RecentLayoutBranch, NoSuchRecentLayoutBranchException>
			_uniquePersistenceFinderByU_L_P;

	/**
	 * Returns the recent layout branch where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; or throws a <code>NoSuchRecentLayoutBranchException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the matching recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a matching recent layout branch could not be found
	 */
	@Override
	public RecentLayoutBranch findByU_L_P(
			long userId, long layoutSetBranchId, long plid)
		throws NoSuchRecentLayoutBranchException {

		return _uniquePersistenceFinderByU_L_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, layoutSetBranchId, plid});
	}

	/**
	 * Returns the recent layout branch where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	@Override
	public RecentLayoutBranch fetchByU_L_P(
		long userId, long layoutSetBranchId, long plid,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByU_L_P.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, layoutSetBranchId, plid}, useFinderCache);
	}

	/**
	 * Removes the recent layout branch where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the recent layout branch that was removed
	 */
	@Override
	public RecentLayoutBranch removeByU_L_P(
			long userId, long layoutSetBranchId, long plid)
		throws NoSuchRecentLayoutBranchException {

		RecentLayoutBranch recentLayoutBranch = findByU_L_P(
			userId, layoutSetBranchId, plid);

		return remove(recentLayoutBranch);
	}

	/**
	 * Returns the number of recent layout branches where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the number of matching recent layout branches
	 */
	@Override
	public int countByU_L_P(long userId, long layoutSetBranchId, long plid) {
		return _uniquePersistenceFinderByU_L_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, layoutSetBranchId, plid});
	}

	public RecentLayoutBranchPersistenceImpl() {
		setModelClass(RecentLayoutBranch.class);

		setModelImplClass(RecentLayoutBranchImpl.class);
		setModelPKClass(long.class);

		setTable(RecentLayoutBranchTable.INSTANCE);
	}

	/**
	 * Creates a new recent layout branch with the primary key. Does not add the recent layout branch to the database.
	 *
	 * @param recentLayoutBranchId the primary key for the new recent layout branch
	 * @return the new recent layout branch
	 */
	@Override
	public RecentLayoutBranch create(long recentLayoutBranchId) {
		RecentLayoutBranch recentLayoutBranch = new RecentLayoutBranchImpl();

		recentLayoutBranch.setNew(true);
		recentLayoutBranch.setPrimaryKey(recentLayoutBranchId);

		recentLayoutBranch.setCompanyId(CompanyThreadLocal.getCompanyId());

		return recentLayoutBranch;
	}

	/**
	 * Removes the recent layout branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recentLayoutBranchId the primary key of the recent layout branch
	 * @return the recent layout branch that was removed
	 * @throws NoSuchRecentLayoutBranchException if a recent layout branch with the primary key could not be found
	 */
	@Override
	public RecentLayoutBranch remove(long recentLayoutBranchId)
		throws NoSuchRecentLayoutBranchException {

		return remove((Serializable)recentLayoutBranchId);
	}

	@Override
	protected RecentLayoutBranch removeImpl(
		RecentLayoutBranch recentLayoutBranch) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(recentLayoutBranch)) {
				recentLayoutBranch = (RecentLayoutBranch)session.get(
					RecentLayoutBranchImpl.class,
					recentLayoutBranch.getPrimaryKeyObj());
			}

			if (recentLayoutBranch != null) {
				session.delete(recentLayoutBranch);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (recentLayoutBranch != null) {
			clearCache(recentLayoutBranch);
		}

		return recentLayoutBranch;
	}

	@Override
	public RecentLayoutBranch updateImpl(
		RecentLayoutBranch recentLayoutBranch) {

		boolean isNew = recentLayoutBranch.isNew();

		if (!(recentLayoutBranch instanceof RecentLayoutBranchModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(recentLayoutBranch.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					recentLayoutBranch);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in recentLayoutBranch proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom RecentLayoutBranch implementation " +
					recentLayoutBranch.getClass());
		}

		RecentLayoutBranchModelImpl recentLayoutBranchModelImpl =
			(RecentLayoutBranchModelImpl)recentLayoutBranch;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(recentLayoutBranch);
			}
			else {
				recentLayoutBranch = (RecentLayoutBranch)session.merge(
					recentLayoutBranch);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(recentLayoutBranch, false);

		if (isNew) {
			recentLayoutBranch.setNew(false);
		}

		recentLayoutBranch.resetOriginalValues();

		return recentLayoutBranch;
	}

	/**
	 * Returns the recent layout branch with the primary key or throws a <code>NoSuchRecentLayoutBranchException</code> if it could not be found.
	 *
	 * @param recentLayoutBranchId the primary key of the recent layout branch
	 * @return the recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a recent layout branch with the primary key could not be found
	 */
	@Override
	public RecentLayoutBranch findByPrimaryKey(long recentLayoutBranchId)
		throws NoSuchRecentLayoutBranchException {

		return findByPrimaryKey((Serializable)recentLayoutBranchId);
	}

	/**
	 * Returns the recent layout branch with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recentLayoutBranchId the primary key of the recent layout branch
	 * @return the recent layout branch, or <code>null</code> if a recent layout branch with the primary key could not be found
	 */
	@Override
	public RecentLayoutBranch fetchByPrimaryKey(long recentLayoutBranchId) {
		return fetchByPrimaryKey((Serializable)recentLayoutBranchId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "recentLayoutBranchId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_RECENTLAYOUTBRANCH;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return RecentLayoutBranchModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the recent layout branch persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_RECENTLAYOUTBRANCH_WHERE,
				_SQL_COUNT_RECENTLAYOUTBRANCH_WHERE,
				RecentLayoutBranchModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"recentLayoutBranch.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, RecentLayoutBranch::getGroupId));

		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_RECENTLAYOUTBRANCH_WHERE,
				_SQL_COUNT_RECENTLAYOUTBRANCH_WHERE,
				RecentLayoutBranchModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"recentLayoutBranch.", "userId", FinderColumn.Type.LONG,
					"=", true, true, RecentLayoutBranch::getUserId));

		_collectionPersistenceFinderByLayoutBranchId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLayoutBranchId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutBranchId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLayoutBranchId", new String[] {Long.class.getName()},
					new String[] {"layoutBranchId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLayoutBranchId",
					new String[] {Long.class.getName()},
					new String[] {"layoutBranchId"}, false),
				_SQL_SELECT_RECENTLAYOUTBRANCH_WHERE,
				_SQL_COUNT_RECENTLAYOUTBRANCH_WHERE,
				RecentLayoutBranchModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"recentLayoutBranch.", "layoutBranchId",
					FinderColumn.Type.LONG, "=", true, true,
					RecentLayoutBranch::getLayoutBranchId));

		_uniquePersistenceFinderByU_L_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_L_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"userId", "layoutSetBranchId", "plid"}, 0, 0,
				false, RecentLayoutBranch::getUserId,
				RecentLayoutBranch::getLayoutSetBranchId,
				RecentLayoutBranch::getPlid),
			_SQL_SELECT_RECENTLAYOUTBRANCH_WHERE, "",
			new FinderColumn<>(
				"recentLayoutBranch.", "userId", FinderColumn.Type.LONG, "=",
				true, true, RecentLayoutBranch::getUserId),
			new FinderColumn<>(
				"recentLayoutBranch.", "layoutSetBranchId",
				FinderColumn.Type.LONG, "=", true, true,
				RecentLayoutBranch::getLayoutSetBranchId),
			new FinderColumn<>(
				"recentLayoutBranch.", "plid", FinderColumn.Type.LONG, "=",
				true, true, RecentLayoutBranch::getPlid));

		RecentLayoutBranchUtil.setPersistence(this);
	}

	public void destroy() {
		RecentLayoutBranchUtil.setPersistence(null);

		EntityCacheUtil.removeCache(RecentLayoutBranchImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		RecentLayoutBranchModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_RECENTLAYOUTBRANCH =
		"SELECT recentLayoutBranch FROM RecentLayoutBranch recentLayoutBranch";

	private static final String _SQL_SELECT_RECENTLAYOUTBRANCH_WHERE =
		"SELECT recentLayoutBranch FROM RecentLayoutBranch recentLayoutBranch WHERE ";

	private static final String _SQL_COUNT_RECENTLAYOUTBRANCH_WHERE =
		"SELECT COUNT(recentLayoutBranch) FROM RecentLayoutBranch recentLayoutBranch WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No RecentLayoutBranch exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		RecentLayoutBranchPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1772092027