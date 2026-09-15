/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchRecentLayoutBranchException;
import com.liferay.portal.kernel.model.RecentLayoutBranch;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the recent layout branch service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RecentLayoutBranchUtil
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
@ProviderType
public interface RecentLayoutBranchPersistence
	extends BasePersistence<RecentLayoutBranch> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link RecentLayoutBranchUtil} to access the recent layout branch persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

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
	public java.util.List<RecentLayoutBranch> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutBranch>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first recent layout branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a matching recent layout branch could not be found
	 */
	public RecentLayoutBranch findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutBranch>
				orderByComparator)
		throws NoSuchRecentLayoutBranchException;

	/**
	 * Returns the first recent layout branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	public RecentLayoutBranch fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutBranch>
			orderByComparator);

	/**
	 * Removes all the recent layout branches where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of recent layout branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching recent layout branches
	 */
	public int countByGroupId(long groupId);

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
	public java.util.List<RecentLayoutBranch> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutBranch>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first recent layout branch in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a matching recent layout branch could not be found
	 */
	public RecentLayoutBranch findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutBranch>
				orderByComparator)
		throws NoSuchRecentLayoutBranchException;

	/**
	 * Returns the first recent layout branch in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	public RecentLayoutBranch fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutBranch>
			orderByComparator);

	/**
	 * Removes all the recent layout branches where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of recent layout branches where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching recent layout branches
	 */
	public int countByUserId(long userId);

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
	public java.util.List<RecentLayoutBranch> findByLayoutBranchId(
		long layoutBranchId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutBranch>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first recent layout branch in the ordered set where layoutBranchId = &#63;.
	 *
	 * @param layoutBranchId the layout branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a matching recent layout branch could not be found
	 */
	public RecentLayoutBranch findByLayoutBranchId_First(
			long layoutBranchId,
			com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutBranch>
				orderByComparator)
		throws NoSuchRecentLayoutBranchException;

	/**
	 * Returns the first recent layout branch in the ordered set where layoutBranchId = &#63;.
	 *
	 * @param layoutBranchId the layout branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	public RecentLayoutBranch fetchByLayoutBranchId_First(
		long layoutBranchId,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutBranch>
			orderByComparator);

	/**
	 * Removes all the recent layout branches where layoutBranchId = &#63; from the database.
	 *
	 * @param layoutBranchId the layout branch ID
	 */
	public void removeByLayoutBranchId(long layoutBranchId);

	/**
	 * Returns the number of recent layout branches where layoutBranchId = &#63;.
	 *
	 * @param layoutBranchId the layout branch ID
	 * @return the number of matching recent layout branches
	 */
	public int countByLayoutBranchId(long layoutBranchId);

	/**
	 * Returns the recent layout branch where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; or throws a <code>NoSuchRecentLayoutBranchException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the matching recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a matching recent layout branch could not be found
	 */
	public RecentLayoutBranch findByU_L_P(
			long userId, long layoutSetBranchId, long plid)
		throws NoSuchRecentLayoutBranchException;

	/**
	 * Returns the recent layout branch where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	public RecentLayoutBranch fetchByU_L_P(
		long userId, long layoutSetBranchId, long plid, boolean useFinderCache);

	/**
	 * Removes the recent layout branch where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the recent layout branch that was removed
	 */
	public RecentLayoutBranch removeByU_L_P(
			long userId, long layoutSetBranchId, long plid)
		throws NoSuchRecentLayoutBranchException;

	/**
	 * Returns the number of recent layout branches where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the number of matching recent layout branches
	 */
	public int countByU_L_P(long userId, long layoutSetBranchId, long plid);

	/**
	 * Creates a new recent layout branch with the primary key. Does not add the recent layout branch to the database.
	 *
	 * @param recentLayoutBranchId the primary key for the new recent layout branch
	 * @return the new recent layout branch
	 */
	public RecentLayoutBranch create(long recentLayoutBranchId);

	/**
	 * Removes the recent layout branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recentLayoutBranchId the primary key of the recent layout branch
	 * @return the recent layout branch that was removed
	 * @throws NoSuchRecentLayoutBranchException if a recent layout branch with the primary key could not be found
	 */
	public RecentLayoutBranch remove(long recentLayoutBranchId)
		throws NoSuchRecentLayoutBranchException;

	public RecentLayoutBranch updateImpl(RecentLayoutBranch recentLayoutBranch);

	/**
	 * Returns the recent layout branch with the primary key or throws a <code>NoSuchRecentLayoutBranchException</code> if it could not be found.
	 *
	 * @param recentLayoutBranchId the primary key of the recent layout branch
	 * @return the recent layout branch
	 * @throws NoSuchRecentLayoutBranchException if a recent layout branch with the primary key could not be found
	 */
	public RecentLayoutBranch findByPrimaryKey(long recentLayoutBranchId)
		throws NoSuchRecentLayoutBranchException;

	/**
	 * Returns the recent layout branch with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recentLayoutBranchId the primary key of the recent layout branch
	 * @return the recent layout branch, or <code>null</code> if a recent layout branch with the primary key could not be found
	 */
	public RecentLayoutBranch fetchByPrimaryKey(long recentLayoutBranchId);

	/**
	 * Returns the recent layout branch where userId = &#63; and layoutSetBranchId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the matching recent layout branch, or <code>null</code> if a matching recent layout branch could not be found
	 */
	public default RecentLayoutBranch fetchByU_L_P(
		long userId, long layoutSetBranchId, long plid) {

		return fetchByU_L_P(userId, layoutSetBranchId, plid, true);
	}

	/**
	 * Returns all the recent layout branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching recent layout branches
	 */
	public default java.util.List<RecentLayoutBranch> findByGroupId(
		long groupId) {

		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<RecentLayoutBranch> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
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
	public default java.util.List<RecentLayoutBranch> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutBranch>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the recent layout branches where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching recent layout branches
	 */
	public default java.util.List<RecentLayoutBranch> findByUserId(
		long userId) {

		return findByUserId(
			userId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<RecentLayoutBranch> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null, true);
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
	public default java.util.List<RecentLayoutBranch> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutBranch>
			orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the recent layout branches where layoutBranchId = &#63;.
	 *
	 * @param layoutBranchId the layout branch ID
	 * @return the matching recent layout branches
	 */
	public default java.util.List<RecentLayoutBranch> findByLayoutBranchId(
		long layoutBranchId) {

		return findByLayoutBranchId(
			layoutBranchId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<RecentLayoutBranch> findByLayoutBranchId(
		long layoutBranchId, int start, int end) {

		return findByLayoutBranchId(layoutBranchId, start, end, null, true);
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
	public default java.util.List<RecentLayoutBranch> findByLayoutBranchId(
		long layoutBranchId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutBranch>
			orderByComparator) {

		return findByLayoutBranchId(
			layoutBranchId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-564133088