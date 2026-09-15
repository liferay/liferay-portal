/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.RecentLayoutSetBranch;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the recent layout set branch service. This utility wraps <code>com.liferay.portal.service.persistence.impl.RecentLayoutSetBranchPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RecentLayoutSetBranchPersistence
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class RecentLayoutSetBranchUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<RecentLayoutSetBranch> recentLayoutSetBranchs) {

		getPersistence().cacheResult(recentLayoutSetBranchs);
	}

	/**
	 * @see BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		RecentLayoutSetBranch recentLayoutSetBranch) {

		getPersistence().cacheResult(recentLayoutSetBranch);
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
	public static void clearCache(RecentLayoutSetBranch recentLayoutSetBranch) {
		getPersistence().clearCache(recentLayoutSetBranch);
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
	public static Map<Serializable, RecentLayoutSetBranch> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<RecentLayoutSetBranch> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<RecentLayoutSetBranch> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<RecentLayoutSetBranch> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static RecentLayoutSetBranch update(
		RecentLayoutSetBranch recentLayoutSetBranch) {

		return getPersistence().update(recentLayoutSetBranch);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static RecentLayoutSetBranch update(
		RecentLayoutSetBranch recentLayoutSetBranch,
		ServiceContext serviceContext) {

		return getPersistence().update(recentLayoutSetBranch, serviceContext);
	}

	/**
	 * Returns an ordered range of all the recent layout set branches where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout set branches
	 */
	public static List<RecentLayoutSetBranch> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout set branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a matching recent layout set branch could not be found
	 */
	public static RecentLayoutSetBranch findByGroupId_First(
			long groupId,
			OrderByComparator<RecentLayoutSetBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutSetBranchException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first recent layout set branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	public static RecentLayoutSetBranch fetchByGroupId_First(
		long groupId,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the recent layout set branches where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of recent layout set branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching recent layout set branches
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the recent layout set branches where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout set branches
	 */
	public static List<RecentLayoutSetBranch> findByUserId(
		long userId, int start, int end,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout set branch in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a matching recent layout set branch could not be found
	 */
	public static RecentLayoutSetBranch findByUserId_First(
			long userId,
			OrderByComparator<RecentLayoutSetBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutSetBranchException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first recent layout set branch in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	public static RecentLayoutSetBranch fetchByUserId_First(
		long userId,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Removes all the recent layout set branches where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of recent layout set branches where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching recent layout set branches
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns an ordered range of all the recent layout set branches where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching recent layout set branches
	 */
	public static List<RecentLayoutSetBranch> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLayoutSetBranchId(
			layoutSetBranchId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first recent layout set branch in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a matching recent layout set branch could not be found
	 */
	public static RecentLayoutSetBranch findByLayoutSetBranchId_First(
			long layoutSetBranchId,
			OrderByComparator<RecentLayoutSetBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutSetBranchException {

		return getPersistence().findByLayoutSetBranchId_First(
			layoutSetBranchId, orderByComparator);
	}

	/**
	 * Returns the first recent layout set branch in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	public static RecentLayoutSetBranch fetchByLayoutSetBranchId_First(
		long layoutSetBranchId,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator) {

		return getPersistence().fetchByLayoutSetBranchId_First(
			layoutSetBranchId, orderByComparator);
	}

	/**
	 * Removes all the recent layout set branches where layoutSetBranchId = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 */
	public static void removeByLayoutSetBranchId(long layoutSetBranchId) {
		getPersistence().removeByLayoutSetBranchId(layoutSetBranchId);
	}

	/**
	 * Returns the number of recent layout set branches where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the number of matching recent layout set branches
	 */
	public static int countByLayoutSetBranchId(long layoutSetBranchId) {
		return getPersistence().countByLayoutSetBranchId(layoutSetBranchId);
	}

	/**
	 * Returns the recent layout set branch where userId = &#63; and layoutSetId = &#63; or throws a <code>NoSuchRecentLayoutSetBranchException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @return the matching recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a matching recent layout set branch could not be found
	 */
	public static RecentLayoutSetBranch findByU_L(long userId, long layoutSetId)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutSetBranchException {

		return getPersistence().findByU_L(userId, layoutSetId);
	}

	/**
	 * Returns the recent layout set branch where userId = &#63; and layoutSetId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	public static RecentLayoutSetBranch fetchByU_L(
		long userId, long layoutSetId, boolean useFinderCache) {

		return getPersistence().fetchByU_L(userId, layoutSetId, useFinderCache);
	}

	/**
	 * Removes the recent layout set branch where userId = &#63; and layoutSetId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @return the recent layout set branch that was removed
	 */
	public static RecentLayoutSetBranch removeByU_L(
			long userId, long layoutSetId)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutSetBranchException {

		return getPersistence().removeByU_L(userId, layoutSetId);
	}

	/**
	 * Returns the number of recent layout set branches where userId = &#63; and layoutSetId = &#63;.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @return the number of matching recent layout set branches
	 */
	public static int countByU_L(long userId, long layoutSetId) {
		return getPersistence().countByU_L(userId, layoutSetId);
	}

	/**
	 * Creates a new recent layout set branch with the primary key. Does not add the recent layout set branch to the database.
	 *
	 * @param recentLayoutSetBranchId the primary key for the new recent layout set branch
	 * @return the new recent layout set branch
	 */
	public static RecentLayoutSetBranch create(long recentLayoutSetBranchId) {
		return getPersistence().create(recentLayoutSetBranchId);
	}

	/**
	 * Removes the recent layout set branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recentLayoutSetBranchId the primary key of the recent layout set branch
	 * @return the recent layout set branch that was removed
	 * @throws NoSuchRecentLayoutSetBranchException if a recent layout set branch with the primary key could not be found
	 */
	public static RecentLayoutSetBranch remove(long recentLayoutSetBranchId)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutSetBranchException {

		return getPersistence().remove(recentLayoutSetBranchId);
	}

	public static RecentLayoutSetBranch updateImpl(
		RecentLayoutSetBranch recentLayoutSetBranch) {

		return getPersistence().updateImpl(recentLayoutSetBranch);
	}

	/**
	 * Returns the recent layout set branch with the primary key or throws a <code>NoSuchRecentLayoutSetBranchException</code> if it could not be found.
	 *
	 * @param recentLayoutSetBranchId the primary key of the recent layout set branch
	 * @return the recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a recent layout set branch with the primary key could not be found
	 */
	public static RecentLayoutSetBranch findByPrimaryKey(
			long recentLayoutSetBranchId)
		throws com.liferay.portal.kernel.exception.
			NoSuchRecentLayoutSetBranchException {

		return getPersistence().findByPrimaryKey(recentLayoutSetBranchId);
	}

	/**
	 * Returns the recent layout set branch with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recentLayoutSetBranchId the primary key of the recent layout set branch
	 * @return the recent layout set branch, or <code>null</code> if a recent layout set branch with the primary key could not be found
	 */
	public static RecentLayoutSetBranch fetchByPrimaryKey(
		long recentLayoutSetBranchId) {

		return getPersistence().fetchByPrimaryKey(recentLayoutSetBranchId);
	}

	/**
	 * Returns the recent layout set branch where userId = &#63; and layoutSetId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @return the matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	public static RecentLayoutSetBranch fetchByU_L(
		long userId, long layoutSetId) {

		return getPersistence().fetchByU_L(userId, layoutSetId);
	}

	/**
	 * Returns all the recent layout set branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching recent layout set branches
	 */
	public static List<RecentLayoutSetBranch> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the recent layout set branches where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @return the range of matching recent layout set branches
	 */
	public static List<RecentLayoutSetBranch> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the recent layout set branches where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching recent layout set branches
	 */
	public static List<RecentLayoutSetBranch> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the recent layout set branches where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching recent layout set branches
	 */
	public static List<RecentLayoutSetBranch> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns a range of all the recent layout set branches where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @return the range of matching recent layout set branches
	 */
	public static List<RecentLayoutSetBranch> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

	/**
	 * Returns an ordered range of all the recent layout set branches where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching recent layout set branches
	 */
	public static List<RecentLayoutSetBranch> findByUserId(
		long userId, int start, int end,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the recent layout set branches where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the matching recent layout set branches
	 */
	public static List<RecentLayoutSetBranch> findByLayoutSetBranchId(
		long layoutSetBranchId) {

		return getPersistence().findByLayoutSetBranchId(layoutSetBranchId);
	}

	/**
	 * Returns a range of all the recent layout set branches where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @return the range of matching recent layout set branches
	 */
	public static List<RecentLayoutSetBranch> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end) {

		return getPersistence().findByLayoutSetBranchId(
			layoutSetBranchId, start, end);
	}

	/**
	 * Returns an ordered range of all the recent layout set branches where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching recent layout set branches
	 */
	public static List<RecentLayoutSetBranch> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end,
		OrderByComparator<RecentLayoutSetBranch> orderByComparator) {

		return getPersistence().findByLayoutSetBranchId(
			layoutSetBranchId, start, end, orderByComparator);
	}

	public static RecentLayoutSetBranchPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		RecentLayoutSetBranchPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile RecentLayoutSetBranchPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1998278262