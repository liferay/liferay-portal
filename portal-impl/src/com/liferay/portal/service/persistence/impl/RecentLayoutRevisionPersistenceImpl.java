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
import com.liferay.portal.kernel.exception.NoSuchRecentLayoutRevisionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RecentLayoutRevision;
import com.liferay.portal.kernel.model.RecentLayoutRevisionTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.RecentLayoutRevisionPersistence;
import com.liferay.portal.kernel.service.persistence.RecentLayoutRevisionUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.RecentLayoutRevisionImpl;
import com.liferay.portal.model.impl.RecentLayoutRevisionModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the recent layout revision service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RecentLayoutRevisionPersistenceImpl
	extends BasePersistenceImpl
		<RecentLayoutRevision, NoSuchRecentLayoutRevisionException>
	implements RecentLayoutRevisionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RecentLayoutRevisionUtil</code> to access the recent layout revision persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RecentLayoutRevisionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<RecentLayoutRevision, NoSuchRecentLayoutRevisionException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the recent layout revisions where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RecentLayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of recent layout revisions
	 * @param end the upper bound of the range of recent layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout revisions
	 */
	@Override
	public List<RecentLayoutRevision> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RecentLayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout revision in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout revision
	 * @throws NoSuchRecentLayoutRevisionException if a matching recent layout revision could not be found
	 */
	@Override
	public RecentLayoutRevision findByGroupId_First(
			long groupId,
			OrderByComparator<RecentLayoutRevision> orderByComparator)
		throws NoSuchRecentLayoutRevisionException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first recent layout revision in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout revision, or <code>null</code> if a matching recent layout revision could not be found
	 */
	@Override
	public RecentLayoutRevision fetchByGroupId_First(
		long groupId,
		OrderByComparator<RecentLayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the recent layout revisions where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of recent layout revisions where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching recent layout revisions
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<RecentLayoutRevision, NoSuchRecentLayoutRevisionException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the recent layout revisions where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RecentLayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of recent layout revisions
	 * @param end the upper bound of the range of recent layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout revisions
	 */
	@Override
	public List<RecentLayoutRevision> findByUserId(
		long userId, int start, int end,
		OrderByComparator<RecentLayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout revision in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout revision
	 * @throws NoSuchRecentLayoutRevisionException if a matching recent layout revision could not be found
	 */
	@Override
	public RecentLayoutRevision findByUserId_First(
			long userId,
			OrderByComparator<RecentLayoutRevision> orderByComparator)
		throws NoSuchRecentLayoutRevisionException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first recent layout revision in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout revision, or <code>null</code> if a matching recent layout revision could not be found
	 */
	@Override
	public RecentLayoutRevision fetchByUserId_First(
		long userId,
		OrderByComparator<RecentLayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the recent layout revisions where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of recent layout revisions where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching recent layout revisions
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<RecentLayoutRevision, NoSuchRecentLayoutRevisionException>
			_collectionPersistenceFinderByLayoutRevisionId;

	/**
	 * Returns an ordered range of all the recent layout revisions where layoutRevisionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RecentLayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param layoutRevisionId the layout revision ID
	 * @param start the lower bound of the range of recent layout revisions
	 * @param end the upper bound of the range of recent layout revisions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout revisions
	 */
	@Override
	public List<RecentLayoutRevision> findByLayoutRevisionId(
		long layoutRevisionId, int start, int end,
		OrderByComparator<RecentLayoutRevision> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutRevisionId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutRevisionId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout revision in the ordered set where layoutRevisionId = &#63;.
	 *
	 * @param layoutRevisionId the layout revision ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout revision
	 * @throws NoSuchRecentLayoutRevisionException if a matching recent layout revision could not be found
	 */
	@Override
	public RecentLayoutRevision findByLayoutRevisionId_First(
			long layoutRevisionId,
			OrderByComparator<RecentLayoutRevision> orderByComparator)
		throws NoSuchRecentLayoutRevisionException {

		return _collectionPersistenceFinderByLayoutRevisionId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutRevisionId},
			orderByComparator);
	}

	/**
	 * Returns the first recent layout revision in the ordered set where layoutRevisionId = &#63;.
	 *
	 * @param layoutRevisionId the layout revision ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout revision, or <code>null</code> if a matching recent layout revision could not be found
	 */
	@Override
	public RecentLayoutRevision fetchByLayoutRevisionId_First(
		long layoutRevisionId,
		OrderByComparator<RecentLayoutRevision> orderByComparator) {

		return _collectionPersistenceFinderByLayoutRevisionId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutRevisionId},
			orderByComparator);
	}

	/**
	 * Removes all the recent layout revisions where layoutRevisionId = &#63; from the database.
	 *
	 * @param layoutRevisionId the layout revision ID
	 */
	@Override
	public void removeByLayoutRevisionId(long layoutRevisionId) {
		_collectionPersistenceFinderByLayoutRevisionId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutRevisionId});
	}

	/**
	 * Returns the number of recent layout revisions where layoutRevisionId = &#63;.
	 *
	 * @param layoutRevisionId the layout revision ID
	 * @return the number of matching recent layout revisions
	 */
	@Override
	public int countByLayoutRevisionId(long layoutRevisionId) {
		return _collectionPersistenceFinderByLayoutRevisionId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {layoutRevisionId});
	}

	private UniquePersistenceFinder
		<RecentLayoutRevision, NoSuchRecentLayoutRevisionException>
			_uniquePersistenceFinderByU_L_P;

	/**
	 * Returns the recent layout revision where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; or throws a <code>NoSuchRecentLayoutRevisionException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the matching recent layout revision
	 * @throws NoSuchRecentLayoutRevisionException if a matching recent layout revision could not be found
	 */
	@Override
	public RecentLayoutRevision findByU_L_P(
			long userId, long layoutSetBranchId, long plid)
		throws NoSuchRecentLayoutRevisionException {

		return _uniquePersistenceFinderByU_L_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, layoutSetBranchId, plid});
	}

	/**
	 * Returns the recent layout revision where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching recent layout revision, or <code>null</code> if a matching recent layout revision could not be found
	 */
	@Override
	public RecentLayoutRevision fetchByU_L_P(
		long userId, long layoutSetBranchId, long plid,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByU_L_P.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, layoutSetBranchId, plid}, useFinderCache);
	}

	/**
	 * Removes the recent layout revision where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the recent layout revision that was removed
	 */
	@Override
	public RecentLayoutRevision removeByU_L_P(
			long userId, long layoutSetBranchId, long plid)
		throws NoSuchRecentLayoutRevisionException {

		RecentLayoutRevision recentLayoutRevision = findByU_L_P(
			userId, layoutSetBranchId, plid);

		return remove(recentLayoutRevision);
	}

	/**
	 * Returns the number of recent layout revisions where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the number of matching recent layout revisions
	 */
	@Override
	public int countByU_L_P(long userId, long layoutSetBranchId, long plid) {
		return _uniquePersistenceFinderByU_L_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, layoutSetBranchId, plid});
	}

	public RecentLayoutRevisionPersistenceImpl() {
		setModelClass(RecentLayoutRevision.class);

		setModelImplClass(RecentLayoutRevisionImpl.class);
		setModelPKClass(long.class);

		setTable(RecentLayoutRevisionTable.INSTANCE);
	}

	/**
	 * Creates a new recent layout revision with the primary key. Does not add the recent layout revision to the database.
	 *
	 * @param recentLayoutRevisionId the primary key for the new recent layout revision
	 * @return the new recent layout revision
	 */
	@Override
	public RecentLayoutRevision create(long recentLayoutRevisionId) {
		RecentLayoutRevision recentLayoutRevision =
			new RecentLayoutRevisionImpl();

		recentLayoutRevision.setNew(true);
		recentLayoutRevision.setPrimaryKey(recentLayoutRevisionId);

		recentLayoutRevision.setCompanyId(CompanyThreadLocal.getCompanyId());

		return recentLayoutRevision;
	}

	/**
	 * Removes the recent layout revision with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recentLayoutRevisionId the primary key of the recent layout revision
	 * @return the recent layout revision that was removed
	 * @throws NoSuchRecentLayoutRevisionException if a recent layout revision with the primary key could not be found
	 */
	@Override
	public RecentLayoutRevision remove(long recentLayoutRevisionId)
		throws NoSuchRecentLayoutRevisionException {

		return remove((Serializable)recentLayoutRevisionId);
	}

	@Override
	protected RecentLayoutRevision removeImpl(
		RecentLayoutRevision recentLayoutRevision) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(recentLayoutRevision)) {
				recentLayoutRevision = (RecentLayoutRevision)session.get(
					RecentLayoutRevisionImpl.class,
					recentLayoutRevision.getPrimaryKeyObj());
			}

			if (recentLayoutRevision != null) {
				session.delete(recentLayoutRevision);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (recentLayoutRevision != null) {
			clearCache(recentLayoutRevision);
		}

		return recentLayoutRevision;
	}

	@Override
	public RecentLayoutRevision updateImpl(
		RecentLayoutRevision recentLayoutRevision) {

		boolean isNew = recentLayoutRevision.isNew();

		if (!(recentLayoutRevision instanceof RecentLayoutRevisionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(recentLayoutRevision.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					recentLayoutRevision);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in recentLayoutRevision proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom RecentLayoutRevision implementation " +
					recentLayoutRevision.getClass());
		}

		RecentLayoutRevisionModelImpl recentLayoutRevisionModelImpl =
			(RecentLayoutRevisionModelImpl)recentLayoutRevision;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(recentLayoutRevision);
			}
			else {
				recentLayoutRevision = (RecentLayoutRevision)session.merge(
					recentLayoutRevision);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(recentLayoutRevision, false);

		if (isNew) {
			recentLayoutRevision.setNew(false);
		}

		recentLayoutRevision.resetOriginalValues();

		return recentLayoutRevision;
	}

	/**
	 * Returns the recent layout revision with the primary key or throws a <code>NoSuchRecentLayoutRevisionException</code> if it could not be found.
	 *
	 * @param recentLayoutRevisionId the primary key of the recent layout revision
	 * @return the recent layout revision
	 * @throws NoSuchRecentLayoutRevisionException if a recent layout revision with the primary key could not be found
	 */
	@Override
	public RecentLayoutRevision findByPrimaryKey(long recentLayoutRevisionId)
		throws NoSuchRecentLayoutRevisionException {

		return findByPrimaryKey((Serializable)recentLayoutRevisionId);
	}

	/**
	 * Returns the recent layout revision with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recentLayoutRevisionId the primary key of the recent layout revision
	 * @return the recent layout revision, or <code>null</code> if a recent layout revision with the primary key could not be found
	 */
	@Override
	public RecentLayoutRevision fetchByPrimaryKey(long recentLayoutRevisionId) {
		return fetchByPrimaryKey((Serializable)recentLayoutRevisionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "recentLayoutRevisionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_RECENTLAYOUTREVISION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return RecentLayoutRevisionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the recent layout revision persistence.
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
				_SQL_SELECT_RECENTLAYOUTREVISION_WHERE,
				_SQL_COUNT_RECENTLAYOUTREVISION_WHERE,
				RecentLayoutRevisionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"recentLayoutRevision.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, RecentLayoutRevision::getGroupId));

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
				_SQL_SELECT_RECENTLAYOUTREVISION_WHERE,
				_SQL_COUNT_RECENTLAYOUTREVISION_WHERE,
				RecentLayoutRevisionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"recentLayoutRevision.", "userId", FinderColumn.Type.LONG,
					"=", true, true, RecentLayoutRevision::getUserId));

		_collectionPersistenceFinderByLayoutRevisionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLayoutRevisionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutRevisionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLayoutRevisionId",
					new String[] {Long.class.getName()},
					new String[] {"layoutRevisionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLayoutRevisionId",
					new String[] {Long.class.getName()},
					new String[] {"layoutRevisionId"}, false),
				_SQL_SELECT_RECENTLAYOUTREVISION_WHERE,
				_SQL_COUNT_RECENTLAYOUTREVISION_WHERE,
				RecentLayoutRevisionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"recentLayoutRevision.", "layoutRevisionId",
					FinderColumn.Type.LONG, "=", true, true,
					RecentLayoutRevision::getLayoutRevisionId));

		_uniquePersistenceFinderByU_L_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_L_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"userId", "layoutSetBranchId", "plid"}, 0, 0,
				false, RecentLayoutRevision::getUserId,
				RecentLayoutRevision::getLayoutSetBranchId,
				RecentLayoutRevision::getPlid),
			_SQL_SELECT_RECENTLAYOUTREVISION_WHERE, "",
			new FinderColumn<>(
				"recentLayoutRevision.", "userId", FinderColumn.Type.LONG, "=",
				true, true, RecentLayoutRevision::getUserId),
			new FinderColumn<>(
				"recentLayoutRevision.", "layoutSetBranchId",
				FinderColumn.Type.LONG, "=", true, true,
				RecentLayoutRevision::getLayoutSetBranchId),
			new FinderColumn<>(
				"recentLayoutRevision.", "plid", FinderColumn.Type.LONG, "=",
				true, true, RecentLayoutRevision::getPlid));

		RecentLayoutRevisionUtil.setPersistence(this);
	}

	public void destroy() {
		RecentLayoutRevisionUtil.setPersistence(null);

		EntityCacheUtil.removeCache(RecentLayoutRevisionImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		RecentLayoutRevisionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_RECENTLAYOUTREVISION =
		"SELECT recentLayoutRevision FROM RecentLayoutRevision recentLayoutRevision";

	private static final String _SQL_SELECT_RECENTLAYOUTREVISION_WHERE =
		"SELECT recentLayoutRevision FROM RecentLayoutRevision recentLayoutRevision WHERE ";

	private static final String _SQL_COUNT_RECENTLAYOUTREVISION_WHERE =
		"SELECT COUNT(recentLayoutRevision) FROM RecentLayoutRevision recentLayoutRevision WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No RecentLayoutRevision exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		RecentLayoutRevisionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2147207242