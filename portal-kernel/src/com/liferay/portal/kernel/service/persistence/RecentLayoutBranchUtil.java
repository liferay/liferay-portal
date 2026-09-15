/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.RecentLayoutBranch;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the recent layout branch service. This utility wraps <code>com.liferay.portal.service.persistence.impl.RecentLayoutBranchPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RecentLayoutBranchPersistence
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class RecentLayoutBranchUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<RecentLayoutBranch> recentLayoutBranchs) {

		getPersistence().cacheResult(recentLayoutBranchs);
	}

	/**
	 * @see BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(RecentLayoutBranch recentLayoutBranch) {
		getPersistence().cacheResult(recentLayoutBranch);
	}

	/**
	 * @see BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(RecentLayoutBranch recentLayoutBranch) {
		getPersistence().clearCache(recentLayoutBranch);
	}

	/**
	 * @see BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, RecentLayoutBranch> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<RecentLayoutBranch> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<RecentLayoutBranch> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<RecentLayoutBranch> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<RecentLayoutBranch> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static RecentLayoutBranch update(
		RecentLayoutBranch recentLayoutBranch) {

		return getPersistence().update(recentLayoutBranch);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static RecentLayoutBranch update(
		RecentLayoutBranch recentLayoutBranch, ServiceContext serviceContext) {

		return getPersistence().update(recentLayoutBranch, serviceContext);
	}

	/**
	 * Returns an ordered range of all the recent layout branches where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout branches
	 */
	public static List<RecentLayoutBranch> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RecentLayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a matching recent layout branch could not be found
	 */
	public static RecentLayoutBranch findByGroupId_First(
			long groupId,
			OrderByComparator<RecentLayoutBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutBranchException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first recent layout branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	public static RecentLayoutBranch fetchByGroupId_First(
		long groupId, OrderByComparator<RecentLayoutBranch> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the recent layout branches where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of recent layout branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching recent layout branches
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the recent layout branches where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout branches
	 */
	public static List<RecentLayoutBranch> findByUserId(
		long userId, int start, int end,
		OrderByComparator<RecentLayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout branch in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a matching recent layout branch could not be found
	 */
	public static RecentLayoutBranch findByUserId_First(
			long userId,
			OrderByComparator<RecentLayoutBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutBranchException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first recent layout branch in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	public static RecentLayoutBranch fetchByUserId_First(
		long userId, OrderByComparator<RecentLayoutBranch> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Removes all the recent layout branches where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of recent layout branches where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching recent layout branches
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns an ordered range of all the recent layout branches where layoutBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutBranchId the layout branch ID
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout branches
	 */
	public static List<RecentLayoutBranch> findByLayoutBranchId(
		long layoutBranchId, int start, int end,
		OrderByComparator<RecentLayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLayoutBranchId(
			layoutBranchId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout branch in the ordered set where layoutBranchId = &#63;.
	 *
	 * @param layoutBranchId the layout branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a matching recent layout branch could not be found
	 */
	public static RecentLayoutBranch findByLayoutBranchId_First(
			long layoutBranchId,
			OrderByComparator<RecentLayoutBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutBranchException {

		return getPersistence().findByLayoutBranchId_First(
			layoutBranchId, orderByComparator);
	}

	/**
	 * Returns the first recent layout branch in the ordered set where layoutBranchId = &#63;.
	 *
	 * @param layoutBranchId the layout branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	public static RecentLayoutBranch fetchByLayoutBranchId_First(
		long layoutBranchId,
		OrderByComparator<RecentLayoutBranch> orderByComparator) {

		return getPersistence().fetchByLayoutBranchId_First(
			layoutBranchId, orderByComparator);
	}

	/**
	 * Removes all the recent layout branches where layoutBranchId = &#63; from the database.
	 *
	 * @param layoutBranchId the layout branch ID
	 */
	public static void removeByLayoutBranchId(long layoutBranchId) {
		getPersistence().removeByLayoutBranchId(layoutBranchId);
	}

	/**
	 * Returns the number of recent layout branches where layoutBranchId = &#63;.
	 *
	 * @param layoutBranchId the layout branch ID
	 * @return the number of matching recent layout branches
	 */
	public static int countByLayoutBranchId(long layoutBranchId) {
		return getPersistence().countByLayoutBranchId(layoutBranchId);
	}

	/**
	 * Returns the recent layout branch where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; or throws a <code>NoSuchRecentLayoutBranchException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the matching recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a matching recent layout branch could not be found
	 */
	public static RecentLayoutBranch findByU_L_P(
			long userId, long layoutSetBranchId, long plid)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutBranchException {

		return getPersistence().findByU_L_P(userId, layoutSetBranchId, plid);
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
	public static RecentLayoutBranch fetchByU_L_P(
		long userId, long layoutSetBranchId, long plid,
		boolean useFinderCache) {

		return getPersistence().fetchByU_L_P(
			userId, layoutSetBranchId, plid, useFinderCache);
	}

	/**
	 * Removes the recent layout branch where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the recent layout branch that was removed
	 */
	public static RecentLayoutBranch removeByU_L_P(
			long userId, long layoutSetBranchId, long plid)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutBranchException {

		return getPersistence().removeByU_L_P(userId, layoutSetBranchId, plid);
	}

	/**
	 * Returns the number of recent layout branches where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the number of matching recent layout branches
	 */
	public static int countByU_L_P(
		long userId, long layoutSetBranchId, long plid) {

		return getPersistence().countByU_L_P(userId, layoutSetBranchId, plid);
	}

	/**
	 * Creates a new recent layout branch with the primary key. Does not add the recent layout branch to the database.
	 *
	 * @param recentLayoutBranchId the primary key for the new recent layout branch
	 * @return the new recent layout branch
	 */
	public static RecentLayoutBranch create(long recentLayoutBranchId) {
		return getPersistence().create(recentLayoutBranchId);
	}

	/**
	 * Removes the recent layout branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recentLayoutBranchId the primary key of the recent layout branch
	 * @return the recent layout branch that was removed
	 * @throws NoSuchRecentLayoutBranchException if a recent layout branch with the primary key could not be found
	 */
	public static RecentLayoutBranch remove(long recentLayoutBranchId)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutBranchException {

		return getPersistence().remove(recentLayoutBranchId);
	}

	public static RecentLayoutBranch updateImpl(
		RecentLayoutBranch recentLayoutBranch) {

		return getPersistence().updateImpl(recentLayoutBranch);
	}

	/**
	 * Returns the recent layout branch with the primary key or throws a <code>NoSuchRecentLayoutBranchException</code> if it could not be found.
	 *
	 * @param recentLayoutBranchId the primary key of the recent layout branch
	 * @return the recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a recent layout branch with the primary key could not be found
	 */
	public static RecentLayoutBranch findByPrimaryKey(long recentLayoutBranchId)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutBranchException {

		return getPersistence().findByPrimaryKey(recentLayoutBranchId);
	}

	/**
	 * Returns the recent layout branch with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recentLayoutBranchId the primary key of the recent layout branch
	 * @return the recent layout branch, or <code>null</code> if a recent layout branch with the primary key could not be found
	 */
	public static RecentLayoutBranch fetchByPrimaryKey(
		long recentLayoutBranchId) {

		return getPersistence().fetchByPrimaryKey(recentLayoutBranchId);
	}

	/**
	 * Returns the recent layout branch where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	public static RecentLayoutBranch fetchByU_L_P(
		long userId, long layoutSetBranchId, long plid) {

		return getPersistence().fetchByU_L_P(userId, layoutSetBranchId, plid);
	}

	/**
	 * Returns all the recent layout branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching recent layout branches
	 */
	public static List<RecentLayoutBranch> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the recent layout branches where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @return the range of matching recent layout branches
	 */
	public static List<RecentLayoutBranch> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the recent layout branches where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching recent layout branches
	 */
	public static List<RecentLayoutBranch> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RecentLayoutBranch> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the recent layout branches where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching recent layout branches
	 */
	public static List<RecentLayoutBranch> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns a range of all the recent layout branches where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @return the range of matching recent layout branches
	 */
	public static List<RecentLayoutBranch> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

	/**
	 * Returns an ordered range of all the recent layout branches where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching recent layout branches
	 */
	public static List<RecentLayoutBranch> findByUserId(
		long userId, int start, int end,
		OrderByComparator<RecentLayoutBranch> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the recent layout branches where layoutBranchId = &#63;.
	 *
	 * @param layoutBranchId the layout branch ID
	 * @return the matching recent layout branches
	 */
	public static List<RecentLayoutBranch> findByLayoutBranchId(
		long layoutBranchId) {

		return getPersistence().findByLayoutBranchId(layoutBranchId);
	}

	/**
	 * Returns a range of all the recent layout branches where layoutBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutBranchId the layout branch ID
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @return the range of matching recent layout branches
	 */
	public static List<RecentLayoutBranch> findByLayoutBranchId(
		long layoutBranchId, int start, int end) {

		return getPersistence().findByLayoutBranchId(
			layoutBranchId, start, end);
	}

	/**
	 * Returns an ordered range of all the recent layout branches where layoutBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutBranchId the layout branch ID
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching recent layout branches
	 */
	public static List<RecentLayoutBranch> findByLayoutBranchId(
		long layoutBranchId, int start, int end,
		OrderByComparator<RecentLayoutBranch> orderByComparator) {

		return getPersistence().findByLayoutBranchId(
			layoutBranchId, start, end, orderByComparator);
	}

	public static RecentLayoutBranchPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		RecentLayoutBranchPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile RecentLayoutBranchPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-724571899