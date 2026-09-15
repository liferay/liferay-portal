/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout set branch service. This utility wraps <code>com.liferay.portal.service.persistence.impl.LayoutSetBranchPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetBranchPersistence
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutSetBranchUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<LayoutSetBranch> layoutSetBranchs) {
		getPersistence().cacheResult(layoutSetBranchs);
	}

	/**
	 * @see BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(LayoutSetBranch layoutSetBranch) {
		getPersistence().cacheResult(layoutSetBranch);
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
	public static void clearCache(LayoutSetBranch layoutSetBranch) {
		getPersistence().clearCache(layoutSetBranch);
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
	public static Map<Serializable, LayoutSetBranch> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<LayoutSetBranch> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<LayoutSetBranch> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<LayoutSetBranch> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutSetBranch update(LayoutSetBranch layoutSetBranch) {
		return getPersistence().update(layoutSetBranch);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutSetBranch update(
		LayoutSetBranch layoutSetBranch, ServiceContext serviceContext) {

		return getPersistence().update(layoutSetBranch, serviceContext);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set branches
	 */
	public static List<LayoutSetBranch> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch
	 * @throws NoSuchLayoutSetBranchException if a matching layout set branch could not be found
	 */
	public static LayoutSetBranch findByGroupId_First(
			long groupId, OrderByComparator<LayoutSetBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutSetBranchException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	public static LayoutSetBranch fetchByGroupId_First(
		long groupId, OrderByComparator<LayoutSetBranch> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

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
	public static List<LayoutSetBranch> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Removes all the layout set branches where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of layout set branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout set branches
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of layout set branches that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout set branches that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set branches
	 */
	public static List<LayoutSetBranch> findByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P(
			groupId, privateLayout, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch
	 * @throws NoSuchLayoutSetBranchException if a matching layout set branch could not be found
	 */
	public static LayoutSetBranch findByG_P_First(
			long groupId, boolean privateLayout,
			OrderByComparator<LayoutSetBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutSetBranchException {

		return getPersistence().findByG_P_First(
			groupId, privateLayout, orderByComparator);
	}

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	public static LayoutSetBranch fetchByG_P_First(
		long groupId, boolean privateLayout,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return getPersistence().fetchByG_P_First(
			groupId, privateLayout, orderByComparator);
	}

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
	public static List<LayoutSetBranch> filterFindByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return getPersistence().filterFindByG_P(
			groupId, privateLayout, start, end, orderByComparator);
	}

	/**
	 * Removes all the layout set branches where groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 */
	public static void removeByG_P(long groupId, boolean privateLayout) {
		getPersistence().removeByG_P(groupId, privateLayout);
	}

	/**
	 * Returns the number of layout set branches where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layout set branches
	 */
	public static int countByG_P(long groupId, boolean privateLayout) {
		return getPersistence().countByG_P(groupId, privateLayout);
	}

	/**
	 * Returns the number of layout set branches that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layout set branches that the user has permission to view
	 */
	public static int filterCountByG_P(long groupId, boolean privateLayout) {
		return getPersistence().filterCountByG_P(groupId, privateLayout);
	}

	/**
	 * Returns the layout set branch where groupId = &#63; and privateLayout = &#63; and name = &#63; or throws a <code>NoSuchLayoutSetBranchException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @return the matching layout set branch
	 * @throws NoSuchLayoutSetBranchException if a matching layout set branch could not be found
	 */
	public static LayoutSetBranch findByG_P_N(
			long groupId, boolean privateLayout, String name)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutSetBranchException {

		return getPersistence().findByG_P_N(groupId, privateLayout, name);
	}

	/**
	 * Returns the layout set branch where groupId = &#63; and privateLayout = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	public static LayoutSetBranch fetchByG_P_N(
		long groupId, boolean privateLayout, String name,
		boolean useFinderCache) {

		return getPersistence().fetchByG_P_N(
			groupId, privateLayout, name, useFinderCache);
	}

	/**
	 * Removes the layout set branch where groupId = &#63; and privateLayout = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @return the layout set branch that was removed
	 */
	public static LayoutSetBranch removeByG_P_N(
			long groupId, boolean privateLayout, String name)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutSetBranchException {

		return getPersistence().removeByG_P_N(groupId, privateLayout, name);
	}

	/**
	 * Returns the number of layout set branches where groupId = &#63; and privateLayout = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @return the number of matching layout set branches
	 */
	public static int countByG_P_N(
		long groupId, boolean privateLayout, String name) {

		return getPersistence().countByG_P_N(groupId, privateLayout, name);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set branches
	 */
	public static List<LayoutSetBranch> findByG_P_M(
		long groupId, boolean privateLayout, boolean master, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_M(
			groupId, privateLayout, master, start, end, orderByComparator,
			useFinderCache);
	}

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
	public static LayoutSetBranch findByG_P_M_First(
			long groupId, boolean privateLayout, boolean master,
			OrderByComparator<LayoutSetBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutSetBranchException {

		return getPersistence().findByG_P_M_First(
			groupId, privateLayout, master, orderByComparator);
	}

	/**
	 * Returns the first layout set branch in the ordered set where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	public static LayoutSetBranch fetchByG_P_M_First(
		long groupId, boolean privateLayout, boolean master,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return getPersistence().fetchByG_P_M_First(
			groupId, privateLayout, master, orderByComparator);
	}

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
	public static List<LayoutSetBranch> filterFindByG_P_M(
		long groupId, boolean privateLayout, boolean master, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return getPersistence().filterFindByG_P_M(
			groupId, privateLayout, master, start, end, orderByComparator);
	}

	/**
	 * Removes all the layout set branches where groupId = &#63; and privateLayout = &#63; and master = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 */
	public static void removeByG_P_M(
		long groupId, boolean privateLayout, boolean master) {

		getPersistence().removeByG_P_M(groupId, privateLayout, master);
	}

	/**
	 * Returns the number of layout set branches where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @return the number of matching layout set branches
	 */
	public static int countByG_P_M(
		long groupId, boolean privateLayout, boolean master) {

		return getPersistence().countByG_P_M(groupId, privateLayout, master);
	}

	/**
	 * Returns the number of layout set branches that the user has permission to view where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @return the number of matching layout set branches that the user has permission to view
	 */
	public static int filterCountByG_P_M(
		long groupId, boolean privateLayout, boolean master) {

		return getPersistence().filterCountByG_P_M(
			groupId, privateLayout, master);
	}

	/**
	 * Creates a new layout set branch with the primary key. Does not add the layout set branch to the database.
	 *
	 * @param layoutSetBranchId the primary key for the new layout set branch
	 * @return the new layout set branch
	 */
	public static LayoutSetBranch create(long layoutSetBranchId) {
		return getPersistence().create(layoutSetBranchId);
	}

	/**
	 * Removes the layout set branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutSetBranchId the primary key of the layout set branch
	 * @return the layout set branch that was removed
	 * @throws NoSuchLayoutSetBranchException if a layout set branch with the primary key could not be found
	 */
	public static LayoutSetBranch remove(long layoutSetBranchId)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutSetBranchException {

		return getPersistence().remove(layoutSetBranchId);
	}

	public static LayoutSetBranch updateImpl(LayoutSetBranch layoutSetBranch) {
		return getPersistence().updateImpl(layoutSetBranch);
	}

	/**
	 * Returns the layout set branch with the primary key or throws a <code>NoSuchLayoutSetBranchException</code> if it could not be found.
	 *
	 * @param layoutSetBranchId the primary key of the layout set branch
	 * @return the layout set branch
	 * @throws NoSuchLayoutSetBranchException if a layout set branch with the primary key could not be found
	 */
	public static LayoutSetBranch findByPrimaryKey(long layoutSetBranchId)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutSetBranchException {

		return getPersistence().findByPrimaryKey(layoutSetBranchId);
	}

	/**
	 * Returns the layout set branch with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutSetBranchId the primary key of the layout set branch
	 * @return the layout set branch, or <code>null</code> if a layout set branch with the primary key could not be found
	 */
	public static LayoutSetBranch fetchByPrimaryKey(long layoutSetBranchId) {
		return getPersistence().fetchByPrimaryKey(layoutSetBranchId);
	}

	/**
	 * Returns the layout set branch where groupId = &#63; and privateLayout = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param name the name
	 * @return the matching layout set branch, or <code>null</code> if a matching layout set branch could not be found
	 */
	public static LayoutSetBranch fetchByG_P_N(
		long groupId, boolean privateLayout, String name) {

		return getPersistence().fetchByG_P_N(groupId, privateLayout, name);
	}

	/**
	 * Returns all the layout set branches where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout set branches
	 */
	public static List<LayoutSetBranch> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
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
	public static List<LayoutSetBranch> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
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
	public static List<LayoutSetBranch> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout set branches that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout set branches that the user has permission to view
	 */
	public static List<LayoutSetBranch> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
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
	public static List<LayoutSetBranch> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns all the layout set branches where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout set branches
	 */
	public static List<LayoutSetBranch> findByG_P(
		long groupId, boolean privateLayout) {

		return getPersistence().findByG_P(groupId, privateLayout);
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
	public static List<LayoutSetBranch> findByG_P(
		long groupId, boolean privateLayout, int start, int end) {

		return getPersistence().findByG_P(groupId, privateLayout, start, end);
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
	public static List<LayoutSetBranch> findByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return getPersistence().findByG_P(
			groupId, privateLayout, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout set branches that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout set branches that the user has permission to view
	 */
	public static List<LayoutSetBranch> filterFindByG_P(
		long groupId, boolean privateLayout) {

		return getPersistence().filterFindByG_P(groupId, privateLayout);
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
	public static List<LayoutSetBranch> filterFindByG_P(
		long groupId, boolean privateLayout, int start, int end) {

		return getPersistence().filterFindByG_P(
			groupId, privateLayout, start, end);
	}

	/**
	 * Returns all the layout set branches where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @return the matching layout set branches
	 */
	public static List<LayoutSetBranch> findByG_P_M(
		long groupId, boolean privateLayout, boolean master) {

		return getPersistence().findByG_P_M(groupId, privateLayout, master);
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
	public static List<LayoutSetBranch> findByG_P_M(
		long groupId, boolean privateLayout, boolean master, int start,
		int end) {

		return getPersistence().findByG_P_M(
			groupId, privateLayout, master, start, end);
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
	public static List<LayoutSetBranch> findByG_P_M(
		long groupId, boolean privateLayout, boolean master, int start, int end,
		OrderByComparator<LayoutSetBranch> orderByComparator) {

		return getPersistence().findByG_P_M(
			groupId, privateLayout, master, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout set branches that the user has permission to view where groupId = &#63; and privateLayout = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param master the master
	 * @return the matching layout set branches that the user has permission to view
	 */
	public static List<LayoutSetBranch> filterFindByG_P_M(
		long groupId, boolean privateLayout, boolean master) {

		return getPersistence().filterFindByG_P_M(
			groupId, privateLayout, master);
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
	public static List<LayoutSetBranch> filterFindByG_P_M(
		long groupId, boolean privateLayout, boolean master, int start,
		int end) {

		return getPersistence().filterFindByG_P_M(
			groupId, privateLayout, master, start, end);
	}

	public static LayoutSetBranchPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(LayoutSetBranchPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile LayoutSetBranchPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1128691645