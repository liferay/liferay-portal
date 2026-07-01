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
import com.liferay.portal.kernel.exception.NoSuchRecentLayoutSetBranchException;
import com.liferay.portal.kernel.model.RecentLayoutSetBranch;
import com.liferay.portal.kernel.model.RecentLayoutSetBranchTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.RecentLayoutSetBranchPersistence;
import com.liferay.portal.kernel.service.persistence.RecentLayoutSetBranchUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.RecentLayoutSetBranchImpl;
import com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the recent layout set branch service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RecentLayoutSetBranchPersistenceImpl
	extends BasePersistenceImpl
		<RecentLayoutSetBranch, NoSuchRecentLayoutSetBranchException>
	implements RecentLayoutSetBranchPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RecentLayoutSetBranchUtil</code> to access the recent layout set branch persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RecentLayoutSetBranchImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<RecentLayoutSetBranch, NoSuchRecentLayoutSetBranchException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the recent layout set branches where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout set branches
	 */
	@Override
	public List<RecentLayoutSetBranch> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout set branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a matching recent layout set branch could not be found
	 */
	@Override
	public RecentLayoutSetBranch findByGroupId_First(
			long groupId,
			OrderByComparator<RecentLayoutSetBranch> orderByComparator)
		throws NoSuchRecentLayoutSetBranchException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first recent layout set branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	@Override
	public RecentLayoutSetBranch fetchByGroupId_First(
		long groupId,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the recent layout set branches where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of recent layout set branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching recent layout set branches
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<RecentLayoutSetBranch, NoSuchRecentLayoutSetBranchException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the recent layout set branches where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout set branches
	 */
	@Override
	public List<RecentLayoutSetBranch> findByUserId(
		long userId, int start, int end,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout set branch in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a matching recent layout set branch could not be found
	 */
	@Override
	public RecentLayoutSetBranch findByUserId_First(
			long userId,
			OrderByComparator<RecentLayoutSetBranch> orderByComparator)
		throws NoSuchRecentLayoutSetBranchException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first recent layout set branch in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	@Override
	public RecentLayoutSetBranch fetchByUserId_First(
		long userId,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the recent layout set branches where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of recent layout set branches where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching recent layout set branches
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<RecentLayoutSetBranch, NoSuchRecentLayoutSetBranchException>
			_collectionPersistenceFinderByLayoutSetBranchId;

	/**
	 * Returns an ordered range of all the recent layout set branches where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout set branches
	 */
	@Override
	public List<RecentLayoutSetBranch> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutSetBranchId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout set branch in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a matching recent layout set branch could not be found
	 */
	@Override
	public RecentLayoutSetBranch findByLayoutSetBranchId_First(
			long layoutSetBranchId,
			OrderByComparator<RecentLayoutSetBranch> orderByComparator)
		throws NoSuchRecentLayoutSetBranchException {

		return _collectionPersistenceFinderByLayoutSetBranchId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId},
			orderByComparator);
	}

	/**
	 * Returns the first recent layout set branch in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	@Override
	public RecentLayoutSetBranch fetchByLayoutSetBranchId_First(
		long layoutSetBranchId,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator) {

		return _collectionPersistenceFinderByLayoutSetBranchId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId},
			orderByComparator);
	}

	/**
	 * Removes all the recent layout set branches where layoutSetBranchId = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 */
	@Override
	public void removeByLayoutSetBranchId(long layoutSetBranchId) {
		_collectionPersistenceFinderByLayoutSetBranchId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId});
	}

	/**
	 * Returns the number of recent layout set branches where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the number of matching recent layout set branches
	 */
	@Override
	public int countByLayoutSetBranchId(long layoutSetBranchId) {
		return _collectionPersistenceFinderByLayoutSetBranchId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutSetBranchId});
	}

	private UniquePersistenceFinder
		<RecentLayoutSetBranch, NoSuchRecentLayoutSetBranchException>
			_uniquePersistenceFinderByU_L;

	/**
	 * Returns the recent layout set branch where userId = &#63; and layoutSetId = &#63; or throws a <code>NoSuchRecentLayoutSetBranchException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @return the matching recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a matching recent layout set branch could not be found
	 */
	@Override
	public RecentLayoutSetBranch findByU_L(long userId, long layoutSetId)
		throws NoSuchRecentLayoutSetBranchException {

		return _uniquePersistenceFinderByU_L.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, layoutSetId});
	}

	/**
	 * Returns the recent layout set branch where userId = &#63; and layoutSetId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	@Override
	public RecentLayoutSetBranch fetchByU_L(
		long userId, long layoutSetId, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_L.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, layoutSetId}, useFinderCache);
	}

	/**
	 * Removes the recent layout set branch where userId = &#63; and layoutSetId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @return the recent layout set branch that was removed
	 */
	@Override
	public RecentLayoutSetBranch removeByU_L(long userId, long layoutSetId)
		throws NoSuchRecentLayoutSetBranchException {

		RecentLayoutSetBranch recentLayoutSetBranch = findByU_L(
			userId, layoutSetId);

		return remove(recentLayoutSetBranch);
	}

	/**
	 * Returns the number of recent layout set branches where userId = &#63; and layoutSetId = &#63;.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @return the number of matching recent layout set branches
	 */
	@Override
	public int countByU_L(long userId, long layoutSetId) {
		return _uniquePersistenceFinderByU_L.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, layoutSetId});
	}

	public RecentLayoutSetBranchPersistenceImpl() {
		setModelClass(RecentLayoutSetBranch.class);

		setModelImplClass(RecentLayoutSetBranchImpl.class);
		setModelPKClass(long.class);

		setTable(RecentLayoutSetBranchTable.INSTANCE);
	}

	/**
	 * Creates a new recent layout set branch with the primary key. Does not add the recent layout set branch to the database.
	 *
	 * @param recentLayoutSetBranchId the primary key for the new recent layout set branch
	 * @return the new recent layout set branch
	 */
	@Override
	public RecentLayoutSetBranch create(long recentLayoutSetBranchId) {
		RecentLayoutSetBranch recentLayoutSetBranch =
			new RecentLayoutSetBranchImpl();

		recentLayoutSetBranch.setNew(true);
		recentLayoutSetBranch.setPrimaryKey(recentLayoutSetBranchId);

		recentLayoutSetBranch.setCompanyId(CompanyThreadLocal.getCompanyId());

		return recentLayoutSetBranch;
	}

	/**
	 * Removes the recent layout set branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recentLayoutSetBranchId the primary key of the recent layout set branch
	 * @return the recent layout set branch that was removed
	 * @throws NoSuchRecentLayoutSetBranchException if a recent layout set branch with the primary key could not be found
	 */
	@Override
	public RecentLayoutSetBranch remove(long recentLayoutSetBranchId)
		throws NoSuchRecentLayoutSetBranchException {

		return remove((Serializable)recentLayoutSetBranchId);
	}

	@Override
	protected RecentLayoutSetBranch removeImpl(
		RecentLayoutSetBranch recentLayoutSetBranch) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(recentLayoutSetBranch)) {
				recentLayoutSetBranch = (RecentLayoutSetBranch)session.get(
					RecentLayoutSetBranchImpl.class,
					recentLayoutSetBranch.getPrimaryKeyObj());
			}

			if (recentLayoutSetBranch != null) {
				session.delete(recentLayoutSetBranch);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (recentLayoutSetBranch != null) {
			clearCache(recentLayoutSetBranch);
		}

		return recentLayoutSetBranch;
	}

	@Override
	public RecentLayoutSetBranch updateImpl(
		RecentLayoutSetBranch recentLayoutSetBranch) {

		boolean isNew = recentLayoutSetBranch.isNew();

		if (!(recentLayoutSetBranch instanceof
				RecentLayoutSetBranchModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(recentLayoutSetBranch.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					recentLayoutSetBranch);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in recentLayoutSetBranch proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom RecentLayoutSetBranch implementation " +
					recentLayoutSetBranch.getClass());
		}

		RecentLayoutSetBranchModelImpl recentLayoutSetBranchModelImpl =
			(RecentLayoutSetBranchModelImpl)recentLayoutSetBranch;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(recentLayoutSetBranch);
			}
			else {
				recentLayoutSetBranch = (RecentLayoutSetBranch)session.merge(
					recentLayoutSetBranch);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(recentLayoutSetBranch, false);

		if (isNew) {
			recentLayoutSetBranch.setNew(false);
		}

		recentLayoutSetBranch.resetOriginalValues();

		return recentLayoutSetBranch;
	}

	/**
	 * Returns the recent layout set branch with the primary key or throws a <code>NoSuchRecentLayoutSetBranchException</code> if it could not be found.
	 *
	 * @param recentLayoutSetBranchId the primary key of the recent layout set branch
	 * @return the recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a recent layout set branch with the primary key could not be found
	 */
	@Override
	public RecentLayoutSetBranch findByPrimaryKey(long recentLayoutSetBranchId)
		throws NoSuchRecentLayoutSetBranchException {

		return findByPrimaryKey((Serializable)recentLayoutSetBranchId);
	}

	/**
	 * Returns the recent layout set branch with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recentLayoutSetBranchId the primary key of the recent layout set branch
	 * @return the recent layout set branch, or <code>null</code> if a recent layout set branch with the primary key could not be found
	 */
	@Override
	public RecentLayoutSetBranch fetchByPrimaryKey(
		long recentLayoutSetBranchId) {

		return fetchByPrimaryKey((Serializable)recentLayoutSetBranchId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "recentLayoutSetBranchId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_RECENTLAYOUTSETBRANCH;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return RecentLayoutSetBranchModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the recent layout set branch persistence.
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
				_SQL_SELECT_RECENTLAYOUTSETBRANCH_WHERE,
				_SQL_COUNT_RECENTLAYOUTSETBRANCH_WHERE,
				RecentLayoutSetBranchModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"recentLayoutSetBranch.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, RecentLayoutSetBranch::getGroupId));

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
				_SQL_SELECT_RECENTLAYOUTSETBRANCH_WHERE,
				_SQL_COUNT_RECENTLAYOUTSETBRANCH_WHERE,
				RecentLayoutSetBranchModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"recentLayoutSetBranch.", "userId", FinderColumn.Type.LONG,
					"=", true, true, RecentLayoutSetBranch::getUserId));

		_collectionPersistenceFinderByLayoutSetBranchId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLayoutSetBranchId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutSetBranchId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLayoutSetBranchId",
					new String[] {Long.class.getName()},
					new String[] {"layoutSetBranchId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLayoutSetBranchId",
					new String[] {Long.class.getName()},
					new String[] {"layoutSetBranchId"}, false),
				_SQL_SELECT_RECENTLAYOUTSETBRANCH_WHERE,
				_SQL_COUNT_RECENTLAYOUTSETBRANCH_WHERE,
				RecentLayoutSetBranchModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"recentLayoutSetBranch.", "layoutSetBranchId",
					FinderColumn.Type.LONG, "=", true, true,
					RecentLayoutSetBranch::getLayoutSetBranchId));

		_uniquePersistenceFinderByU_L = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_L",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "layoutSetId"}, 0, 0, false,
				RecentLayoutSetBranch::getUserId,
				RecentLayoutSetBranch::getLayoutSetId),
			_SQL_SELECT_RECENTLAYOUTSETBRANCH_WHERE, "",
			new FinderColumn<>(
				"recentLayoutSetBranch.", "userId", FinderColumn.Type.LONG, "=",
				true, true, RecentLayoutSetBranch::getUserId),
			new FinderColumn<>(
				"recentLayoutSetBranch.", "layoutSetId", FinderColumn.Type.LONG,
				"=", true, true, RecentLayoutSetBranch::getLayoutSetId));

		RecentLayoutSetBranchUtil.setPersistence(this);
	}

	public void destroy() {
		RecentLayoutSetBranchUtil.setPersistence(null);

		EntityCacheUtil.removeCache(RecentLayoutSetBranchImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		RecentLayoutSetBranchModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_RECENTLAYOUTSETBRANCH =
		"SELECT recentLayoutSetBranch FROM RecentLayoutSetBranch recentLayoutSetBranch";

	private static final String _SQL_SELECT_RECENTLAYOUTSETBRANCH_WHERE =
		"SELECT recentLayoutSetBranch FROM RecentLayoutSetBranch recentLayoutSetBranch WHERE ";

	private static final String _SQL_COUNT_RECENTLAYOUTSETBRANCH_WHERE =
		"SELECT COUNT(recentLayoutSetBranch) FROM RecentLayoutSetBranch recentLayoutSetBranch WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1259900893