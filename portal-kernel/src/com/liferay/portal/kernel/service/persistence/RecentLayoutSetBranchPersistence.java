/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchRecentLayoutSetBranchException;
import com.liferay.portal.kernel.model.RecentLayoutSetBranch;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the recent layout set branch service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RecentLayoutSetBranchUtil
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
@ProviderType
public interface RecentLayoutSetBranchPersistence
	extends BasePersistence<RecentLayoutSetBranch> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link RecentLayoutSetBranchUtil} to access the recent layout set branch persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

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
	public java.util.List<RecentLayoutSetBranch> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutSetBranch>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first recent layout set branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a matching recent layout set branch could not be found
	 */
	public RecentLayoutSetBranch findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<RecentLayoutSetBranch> orderByComparator)
		throws NoSuchRecentLayoutSetBranchException;

	/**
	 * Returns the first recent layout set branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	public RecentLayoutSetBranch fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutSetBranch>
			orderByComparator);

	/**
	 * Removes all the recent layout set branches where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of recent layout set branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching recent layout set branches
	 */
	public int countByGroupId(long groupId);

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
	public java.util.List<RecentLayoutSetBranch> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutSetBranch>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first recent layout set branch in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a matching recent layout set branch could not be found
	 */
	public RecentLayoutSetBranch findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator
				<RecentLayoutSetBranch> orderByComparator)
		throws NoSuchRecentLayoutSetBranchException;

	/**
	 * Returns the first recent layout set branch in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	public RecentLayoutSetBranch fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutSetBranch>
			orderByComparator);

	/**
	 * Removes all the recent layout set branches where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of recent layout set branches where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching recent layout set branches
	 */
	public int countByUserId(long userId);

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
	public java.util.List<RecentLayoutSetBranch> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutSetBranch>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first recent layout set branch in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a matching recent layout set branch could not be found
	 */
	public RecentLayoutSetBranch findByLayoutSetBranchId_First(
			long layoutSetBranchId,
			com.liferay.portal.kernel.util.OrderByComparator
				<RecentLayoutSetBranch> orderByComparator)
		throws NoSuchRecentLayoutSetBranchException;

	/**
	 * Returns the first recent layout set branch in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	public RecentLayoutSetBranch fetchByLayoutSetBranchId_First(
		long layoutSetBranchId,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutSetBranch>
			orderByComparator);

	/**
	 * Removes all the recent layout set branches where layoutSetBranchId = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 */
	public void removeByLayoutSetBranchId(long layoutSetBranchId);

	/**
	 * Returns the number of recent layout set branches where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the number of matching recent layout set branches
	 */
	public int countByLayoutSetBranchId(long layoutSetBranchId);

	/**
	 * Returns the recent layout set branch where userId = &#63; and layoutSetId = &#63; or throws a <code>NoSuchRecentLayoutSetBranchException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @return the matching recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a matching recent layout set branch could not be found
	 */
	public RecentLayoutSetBranch findByU_L(long userId, long layoutSetId)
		throws NoSuchRecentLayoutSetBranchException;

	/**
	 * Returns the recent layout set branch where userId = &#63; and layoutSetId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	public RecentLayoutSetBranch fetchByU_L(
		long userId, long layoutSetId, boolean useFinderCache);

	/**
	 * Removes the recent layout set branch where userId = &#63; and layoutSetId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @return the recent layout set branch that was removed
	 */
	public RecentLayoutSetBranch removeByU_L(long userId, long layoutSetId)
		throws NoSuchRecentLayoutSetBranchException;

	/**
	 * Returns the number of recent layout set branches where userId = &#63; and layoutSetId = &#63;.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @return the number of matching recent layout set branches
	 */
	public int countByU_L(long userId, long layoutSetId);

	/**
	 * Creates a new recent layout set branch with the primary key. Does not add the recent layout set branch to the database.
	 *
	 * @param recentLayoutSetBranchId the primary key for the new recent layout set branch
	 * @return the new recent layout set branch
	 */
	public RecentLayoutSetBranch create(long recentLayoutSetBranchId);

	/**
	 * Removes the recent layout set branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recentLayoutSetBranchId the primary key of the recent layout set branch
	 * @return the recent layout set branch that was removed
	 * @throws NoSuchRecentLayoutSetBranchException if a recent layout set branch with the primary key could not be found
	 */
	public RecentLayoutSetBranch remove(long recentLayoutSetBranchId)
		throws NoSuchRecentLayoutSetBranchException;

	public RecentLayoutSetBranch updateImpl(
		RecentLayoutSetBranch recentLayoutSetBranch);

	/**
	 * Returns the recent layout set branch with the primary key or throws a <code>NoSuchRecentLayoutSetBranchException</code> if it could not be found.
	 *
	 * @param recentLayoutSetBranchId the primary key of the recent layout set branch
	 * @return the recent layout set branch
	 * @throws NoSuchRecentLayoutSetBranchException if a recent layout set branch with the primary key could not be found
	 */
	public RecentLayoutSetBranch findByPrimaryKey(long recentLayoutSetBranchId)
		throws NoSuchRecentLayoutSetBranchException;

	/**
	 * Returns the recent layout set branch with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recentLayoutSetBranchId the primary key of the recent layout set branch
	 * @return the recent layout set branch, or <code>null</code> if a recent layout set branch with the primary key could not be found
	 */
	public RecentLayoutSetBranch fetchByPrimaryKey(
		long recentLayoutSetBranchId);

	/**
	 * Returns the recent layout set branch where userId = &#63; and layoutSetId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @param layoutSetId the layout set ID
	 * @return the matching recent layout set branch, or <code>null</code> if a matching recent layout set branch could not be found
	 */
	public default RecentLayoutSetBranch fetchByU_L(
		long userId, long layoutSetId) {

		return fetchByU_L(userId, layoutSetId, true);
	}

	/**
	 * Returns all the recent layout set branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching recent layout set branches
	 */
	public default java.util.List<RecentLayoutSetBranch> findByGroupId(
		long groupId) {

		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<RecentLayoutSetBranch> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
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
	public default java.util.List<RecentLayoutSetBranch> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutSetBranch>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the recent layout set branches where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching recent layout set branches
	 */
	public default java.util.List<RecentLayoutSetBranch> findByUserId(
		long userId) {

		return findByUserId(
			userId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<RecentLayoutSetBranch> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null, true);
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
	public default java.util.List<RecentLayoutSetBranch> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<RecentLayoutSetBranch>
			orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the recent layout set branches where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the matching recent layout set branches
	 */
	public default java.util.List<RecentLayoutSetBranch>
		findByLayoutSetBranchId(long layoutSetBranchId) {

		return findByLayoutSetBranchId(
			layoutSetBranchId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<RecentLayoutSetBranch>
		findByLayoutSetBranchId(long layoutSetBranchId, int start, int end) {

		return findByLayoutSetBranchId(
			layoutSetBranchId, start, end, null, true);
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
	public default java.util.List<RecentLayoutSetBranch>
		findByLayoutSetBranchId(
			long layoutSetBranchId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<RecentLayoutSetBranch> orderByComparator) {

		return findByLayoutSetBranchId(
			layoutSetBranchId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1383382543