/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchLayoutSetBranchException;
import com.liferay.portal.kernel.model.LayoutSetBranch;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the layout set branch service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetBranchUtil
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
@ProviderType
public interface LayoutSetBranchPersistence
	extends BasePersistence<LayoutSetBranch> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link LayoutSetBranchUtil} to access the layout set branch persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the layout set branches where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set branches
	 */
	public java.util.List<LayoutSetBranch> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch
	 * @throws NoSuchLayoutSetBranchException if a matching layout set branch could not be found
	 */
	public LayoutSetBranch findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
				orderByComparator)
		throws NoSuchLayoutSetBranchException;

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	public LayoutSetBranch fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layout set branches that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set branches that the user has permission to view
	 */
	public java.util.List<LayoutSetBranch> filterFindByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
			orderByComparator);

	/**
	 * Removes all the layout set branches where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of layout set branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout set branches
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns the number of layout set branches that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout set branches that the user has permission to view
	 */
	public int filterCountByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the layout set branches where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set branches
	 */
	public java.util.List<LayoutSetBranch> findByG_P(
		long groupId, boolean privateLayout, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch
	 * @throws NoSuchLayoutSetBranchException if a matching layout set branch could not be found
	 */
	public LayoutSetBranch findByG_P_First(
			long groupId, boolean privateLayout,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
				orderByComparator)
		throws NoSuchLayoutSetBranchException;

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	public LayoutSetBranch fetchByG_P_First(
		long groupId, boolean privateLayout,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layout set branches that the user has permissions to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set branches that the user has permission to view
	 */
	public java.util.List<LayoutSetBranch> filterFindByG_P(
		long groupId, boolean privateLayout, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
			orderByComparator);

	/**
	 * Removes all the layout set branches where groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 */
	public void removeByG_P(long groupId, boolean privateLayout);

	/**
	 * Returns the number of layout set branches where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layout set branches
	 */
	public int countByG_P(long groupId, boolean privateLayout);

	/**
	 * Returns the number of layout set branches that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layout set branches that the user has permission to view
	 */
	public int filterCountByG_P(long groupId, boolean privateLayout);

	/**
	 * Returns the layout set branch where groupId = &#63; and privateLayout = &#63; and name = &#63; or throws a <code>NoSuchLayoutSetBranchException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @return the matching layout set branch
	 * @throws NoSuchLayoutSetBranchException if a matching layout set branch could not be found
	 */
	public LayoutSetBranch findByG_P_N(
			long groupId, boolean privateLayout, String name)
		throws NoSuchLayoutSetBranchException;

	/**
	 * Returns the layout set branch where groupId = &#63; and privateLayout = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	public LayoutSetBranch fetchByG_P_N(
		long groupId, boolean privateLayout, String name,
		boolean useFinderCache);

	/**
	 * Removes the layout set branch where groupId = &#63; and privateLayout = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @return the layout set branch that was removed
	 */
	public LayoutSetBranch removeByG_P_N(
			long groupId, boolean privateLayout, String name)
		throws NoSuchLayoutSetBranchException;

	/**
	 * Returns the number of layout set branches where groupId = &#63; and privateLayout = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @return the number of matching layout set branches
	 */
	public int countByG_P_N(long groupId, boolean privateLayout, String name);

	/**
	 * Returns an ordered range of all the layout set branches where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set branches
	 */
	public java.util.List<LayoutSetBranch> findByG_P_M(
		long groupId, boolean privateLayout, boolean master, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch
	 * @throws NoSuchLayoutSetBranchException if a matching layout set branch could not be found
	 */
	public LayoutSetBranch findByG_P_M_First(
			long groupId, boolean privateLayout, boolean master,
			com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
				orderByComparator)
		throws NoSuchLayoutSetBranchException;

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	public LayoutSetBranch fetchByG_P_M_First(
		long groupId, boolean privateLayout, boolean master,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layout set branches that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set branches that the user has permission to view
	 */
	public java.util.List<LayoutSetBranch> filterFindByG_P_M(
		long groupId, boolean privateLayout, boolean master, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
			orderByComparator);

	/**
	 * Removes all the layout set branches where groupId = &#63; and privateLayout = &#63; and master = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 */
	public void removeByG_P_M(
		long groupId, boolean privateLayout, boolean master);

	/**
	 * Returns the number of layout set branches where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @return the number of matching layout set branches
	 */
	public int countByG_P_M(
		long groupId, boolean privateLayout, boolean master);

	/**
	 * Returns the number of layout set branches that the user has permission to view where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @return the number of matching layout set branches that the user has permission to view
	 */
	public int filterCountByG_P_M(
		long groupId, boolean privateLayout, boolean master);

	/**
	 * Creates a new layout set branch with the primary key. Does not add the layout set branch to the database.
	 *
	 * @param layoutSetBranchId the primary key for the new layout set branch
	 * @return the new layout set branch
	 */
	public LayoutSetBranch create(long layoutSetBranchId);

	/**
	 * Removes the layout set branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutSetBranchId the primary key of the layout set branch
	 * @return the layout set branch that was removed
	 * @throws NoSuchLayoutSetBranchException if a layout set branch with the primary key could not be found
	 */
	public LayoutSetBranch remove(long layoutSetBranchId)
		throws NoSuchLayoutSetBranchException;

	public LayoutSetBranch updateImpl(LayoutSetBranch layoutSetBranch);

	/**
	 * Returns the layout set branch with the primary key or throws a <code>NoSuchLayoutSetBranchException</code> if it could not be found.
	 *
	 * @param layoutSetBranchId the primary key of the layout set branch
	 * @return the layout set branch
	 * @throws NoSuchLayoutSetBranchException if a layout set branch with the primary key could not be found
	 */
	public LayoutSetBranch findByPrimaryKey(long layoutSetBranchId)
		throws NoSuchLayoutSetBranchException;

	/**
	 * Returns the layout set branch with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutSetBranchId the primary key of the layout set branch
	 * @return the layout set branch, or <code>null</code> if a layout set branch with the primary key could not be found
	 */
	public LayoutSetBranch fetchByPrimaryKey(long layoutSetBranchId);

	/**
	 * Returns the layout set branch where groupId = &#63; and privateLayout = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @return the matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	public default LayoutSetBranch fetchByG_P_N(
		long groupId, boolean privateLayout, String name) {

		return fetchByG_P_N(groupId, privateLayout, name, true);
	}

	/**
	 * Returns all the layout set branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout set branches
	 */
	public default java.util.List<LayoutSetBranch> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout set branches where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @return the range of matching layout set branches
	 */
	public default java.util.List<LayoutSetBranch> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout set branches where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set branches
	 */
	public default java.util.List<LayoutSetBranch> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout set branches that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout set branches that the user has permission to view
	 */
	public default java.util.List<LayoutSetBranch> filterFindByGroupId(
		long groupId) {

		return filterFindByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout set branches that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @return the range of matching layout set branches that the user has permission to view
	 */
	public default java.util.List<LayoutSetBranch> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns all the layout set branches where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout set branches
	 */
	public default java.util.List<LayoutSetBranch> findByG_P(
		long groupId, boolean privateLayout) {

		return findByG_P(
			groupId, privateLayout,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout set branches where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @return the range of matching layout set branches
	 */
	public default java.util.List<LayoutSetBranch> findByG_P(
		long groupId, boolean privateLayout, int start, int end) {

		return findByG_P(groupId, privateLayout, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout set branches where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set branches
	 */
	public default java.util.List<LayoutSetBranch> findByG_P(
		long groupId, boolean privateLayout, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
			orderByComparator) {

		return findByG_P(
			groupId, privateLayout, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout set branches that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout set branches that the user has permission to view
	 */
	public default java.util.List<LayoutSetBranch> filterFindByG_P(
		long groupId, boolean privateLayout) {

		return filterFindByG_P(
			groupId, privateLayout,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout set branches that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @return the range of matching layout set branches that the user has permission to view
	 */
	public default java.util.List<LayoutSetBranch> filterFindByG_P(
		long groupId, boolean privateLayout, int start, int end) {

		return filterFindByG_P(groupId, privateLayout, start, end, null);
	}

	/**
	 * Returns all the layout set branches where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @return the matching layout set branches
	 */
	public default java.util.List<LayoutSetBranch> findByG_P_M(
		long groupId, boolean privateLayout, boolean master) {

		return findByG_P_M(
			groupId, privateLayout, master,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout set branches where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @return the range of matching layout set branches
	 */
	public default java.util.List<LayoutSetBranch> findByG_P_M(
		long groupId, boolean privateLayout, boolean master, int start,
		int end) {

		return findByG_P_M(
			groupId, privateLayout, master, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout set branches where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set branches
	 */
	public default java.util.List<LayoutSetBranch> findByG_P_M(
		long groupId, boolean privateLayout, boolean master, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<LayoutSetBranch>
			orderByComparator) {

		return findByG_P_M(
			groupId, privateLayout, master, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the layout set branches that the user has permission to view where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @return the matching layout set branches that the user has permission to view
	 */
	public default java.util.List<LayoutSetBranch> filterFindByG_P_M(
		long groupId, boolean privateLayout, boolean master) {

		return filterFindByG_P_M(
			groupId, privateLayout, master,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout set branches that the user has permission to view where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @return the range of matching layout set branches that the user has permission to view
	 */
	public default java.util.List<LayoutSetBranch> filterFindByG_P_M(
		long groupId, boolean privateLayout, boolean master, int start,
		int end) {

		return filterFindByG_P_M(
			groupId, privateLayout, master, start, end, null);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1808323066