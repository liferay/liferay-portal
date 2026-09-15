/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout branch service. This utility wraps <code>com.liferay.portal.service.persistence.impl.LayoutBranchPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutBranchPersistence
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutBranchUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<LayoutBranch> layoutBranchs) {
		getPersistence().cacheResult(layoutBranchs);
	}

	/**
	 * @see BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(LayoutBranch layoutBranch) {
		getPersistence().cacheResult(layoutBranch);
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
	public static void clearCache(LayoutBranch layoutBranch) {
		getPersistence().clearCache(layoutBranch);
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
	public static Map<Serializable, LayoutBranch> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<LayoutBranch> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<LayoutBranch> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<LayoutBranch> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutBranch update(LayoutBranch layoutBranch) {
		return getPersistence().update(layoutBranch);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutBranch update(
		LayoutBranch layoutBranch, ServiceContext serviceContext) {

		return getPersistence().update(layoutBranch, serviceContext);
	}

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout branches
	 */
	public static List<LayoutBranch> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLayoutSetBranchId(
			layoutSetBranchId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	public static LayoutBranch findByLayoutSetBranchId_First(
			long layoutSetBranchId,
			OrderByComparator<LayoutBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutBranchException {

		return getPersistence().findByLayoutSetBranchId_First(
			layoutSetBranchId, orderByComparator);
	}

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	public static LayoutBranch fetchByLayoutSetBranchId_First(
		long layoutSetBranchId,
		OrderByComparator<LayoutBranch> orderByComparator) {

		return getPersistence().fetchByLayoutSetBranchId_First(
			layoutSetBranchId, orderByComparator);
	}

	/**
	 * Removes all the layout branches where layoutSetBranchId = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 */
	public static void removeByLayoutSetBranchId(long layoutSetBranchId) {
		getPersistence().removeByLayoutSetBranchId(layoutSetBranchId);
	}

	/**
	 * Returns the number of layout branches where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the number of matching layout branches
	 */
	public static int countByLayoutSetBranchId(long layoutSetBranchId) {
		return getPersistence().countByLayoutSetBranchId(layoutSetBranchId);
	}

	/**
	 * Returns an ordered range of all the layout branches where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout branches
	 */
	public static List<LayoutBranch> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPlid(
			plid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout branch in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	public static LayoutBranch findByPlid_First(
			long plid, OrderByComparator<LayoutBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutBranchException {

		return getPersistence().findByPlid_First(plid, orderByComparator);
	}

	/**
	 * Returns the first layout branch in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	public static LayoutBranch fetchByPlid_First(
		long plid, OrderByComparator<LayoutBranch> orderByComparator) {

		return getPersistence().fetchByPlid_First(plid, orderByComparator);
	}

	/**
	 * Removes all the layout branches where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	public static void removeByPlid(long plid) {
		getPersistence().removeByPlid(plid);
	}

	/**
	 * Returns the number of layout branches where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout branches
	 */
	public static int countByPlid(long plid) {
		return getPersistence().countByPlid(plid);
	}

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout branches
	 */
	public static List<LayoutBranch> findByL_P(
		long layoutSetBranchId, long plid, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByL_P(
			layoutSetBranchId, plid, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	public static LayoutBranch findByL_P_First(
			long layoutSetBranchId, long plid,
			OrderByComparator<LayoutBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutBranchException {

		return getPersistence().findByL_P_First(
			layoutSetBranchId, plid, orderByComparator);
	}

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	public static LayoutBranch fetchByL_P_First(
		long layoutSetBranchId, long plid,
		OrderByComparator<LayoutBranch> orderByComparator) {

		return getPersistence().fetchByL_P_First(
			layoutSetBranchId, plid, orderByComparator);
	}

	/**
	 * Removes all the layout branches where layoutSetBranchId = &#63; and plid = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 */
	public static void removeByL_P(long layoutSetBranchId, long plid) {
		getPersistence().removeByL_P(layoutSetBranchId, plid);
	}

	/**
	 * Returns the number of layout branches where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the number of matching layout branches
	 */
	public static int countByL_P(long layoutSetBranchId, long plid) {
		return getPersistence().countByL_P(layoutSetBranchId, plid);
	}

	/**
	 * Returns the layout branch where layoutSetBranchId = &#63; and plid = &#63; and name = &#63; or throws a <code>NoSuchLayoutBranchException</code> if it could not be found.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @return the matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	public static LayoutBranch findByL_P_N(
			long layoutSetBranchId, long plid, String name)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutBranchException {

		return getPersistence().findByL_P_N(layoutSetBranchId, plid, name);
	}

	/**
	 * Returns the layout branch where layoutSetBranchId = &#63; and plid = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	public static LayoutBranch fetchByL_P_N(
		long layoutSetBranchId, long plid, String name,
		boolean useFinderCache) {

		return getPersistence().fetchByL_P_N(
			layoutSetBranchId, plid, name, useFinderCache);
	}

	/**
	 * Removes the layout branch where layoutSetBranchId = &#63; and plid = &#63; and name = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @return the layout branch that was removed
	 */
	public static LayoutBranch removeByL_P_N(
			long layoutSetBranchId, long plid, String name)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutBranchException {

		return getPersistence().removeByL_P_N(layoutSetBranchId, plid, name);
	}

	/**
	 * Returns the number of layout branches where layoutSetBranchId = &#63; and plid = &#63; and name = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @return the number of matching layout branches
	 */
	public static int countByL_P_N(
		long layoutSetBranchId, long plid, String name) {

		return getPersistence().countByL_P_N(layoutSetBranchId, plid, name);
	}

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout branches
	 */
	public static List<LayoutBranch> findByL_P_M(
		long layoutSetBranchId, long plid, boolean master, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByL_P_M(
			layoutSetBranchId, plid, master, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch
	 * @throws NoSuchLayoutBranchException if a matching layout branch could not be found
	 */
	public static LayoutBranch findByL_P_M_First(
			long layoutSetBranchId, long plid, boolean master,
			OrderByComparator<LayoutBranch> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutBranchException {

		return getPersistence().findByL_P_M_First(
			layoutSetBranchId, plid, master, orderByComparator);
	}

	/**
	 * Returns the first layout branch in the ordered set where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	public static LayoutBranch fetchByL_P_M_First(
		long layoutSetBranchId, long plid, boolean master,
		OrderByComparator<LayoutBranch> orderByComparator) {

		return getPersistence().fetchByL_P_M_First(
			layoutSetBranchId, plid, master, orderByComparator);
	}

	/**
	 * Removes all the layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63; from the database.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 */
	public static void removeByL_P_M(
		long layoutSetBranchId, long plid, boolean master) {

		getPersistence().removeByL_P_M(layoutSetBranchId, plid, master);
	}

	/**
	 * Returns the number of layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @return the number of matching layout branches
	 */
	public static int countByL_P_M(
		long layoutSetBranchId, long plid, boolean master) {

		return getPersistence().countByL_P_M(layoutSetBranchId, plid, master);
	}

	/**
	 * Creates a new layout branch with the primary key. Does not add the layout branch to the database.
	 *
	 * @param layoutBranchId the primary key for the new layout branch
	 * @return the new layout branch
	 */
	public static LayoutBranch create(long layoutBranchId) {
		return getPersistence().create(layoutBranchId);
	}

	/**
	 * Removes the layout branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch that was removed
	 * @throws NoSuchLayoutBranchException if a layout branch with the primary key could not be found
	 */
	public static LayoutBranch remove(long layoutBranchId)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutBranchException {

		return getPersistence().remove(layoutBranchId);
	}

	public static LayoutBranch updateImpl(LayoutBranch layoutBranch) {
		return getPersistence().updateImpl(layoutBranch);
	}

	/**
	 * Returns the layout branch with the primary key or throws a <code>NoSuchLayoutBranchException</code> if it could not be found.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch
	 * @throws NoSuchLayoutBranchException if a layout branch with the primary key could not be found
	 */
	public static LayoutBranch findByPrimaryKey(long layoutBranchId)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutBranchException {

		return getPersistence().findByPrimaryKey(layoutBranchId);
	}

	/**
	 * Returns the layout branch with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch, or <code>null</code> if a layout branch with the primary key could not be found
	 */
	public static LayoutBranch fetchByPrimaryKey(long layoutBranchId) {
		return getPersistence().fetchByPrimaryKey(layoutBranchId);
	}

	/**
	 * Returns the layout branch where layoutSetBranchId = &#63; and plid = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param name the name
	 * @return the matching layout branch, or <code>null</code> if a matching layout branch could not be found
	 */
	public static LayoutBranch fetchByL_P_N(
		long layoutSetBranchId, long plid, String name) {

		return getPersistence().fetchByL_P_N(layoutSetBranchId, plid, name);
	}

	/**
	 * Returns all the layout branches where layoutSetBranchId = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @return the matching layout branches
	 */
	public static List<LayoutBranch> findByLayoutSetBranchId(
		long layoutSetBranchId) {

		return getPersistence().findByLayoutSetBranchId(layoutSetBranchId);
	}

	/**
	 * Returns a range of all the layout branches where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @return the range of matching layout branches
	 */
	public static List<LayoutBranch> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end) {

		return getPersistence().findByLayoutSetBranchId(
			layoutSetBranchId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout branches
	 */
	public static List<LayoutBranch> findByLayoutSetBranchId(
		long layoutSetBranchId, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator) {

		return getPersistence().findByLayoutSetBranchId(
			layoutSetBranchId, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout branches where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the matching layout branches
	 */
	public static List<LayoutBranch> findByPlid(long plid) {
		return getPersistence().findByPlid(plid);
	}

	/**
	 * Returns a range of all the layout branches where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @return the range of matching layout branches
	 */
	public static List<LayoutBranch> findByPlid(long plid, int start, int end) {
		return getPersistence().findByPlid(plid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout branches where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout branches
	 */
	public static List<LayoutBranch> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator) {

		return getPersistence().findByPlid(plid, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout branches where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @return the matching layout branches
	 */
	public static List<LayoutBranch> findByL_P(
		long layoutSetBranchId, long plid) {

		return getPersistence().findByL_P(layoutSetBranchId, plid);
	}

	/**
	 * Returns a range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @return the range of matching layout branches
	 */
	public static List<LayoutBranch> findByL_P(
		long layoutSetBranchId, long plid, int start, int end) {

		return getPersistence().findByL_P(layoutSetBranchId, plid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout branches
	 */
	public static List<LayoutBranch> findByL_P(
		long layoutSetBranchId, long plid, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator) {

		return getPersistence().findByL_P(
			layoutSetBranchId, plid, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @return the matching layout branches
	 */
	public static List<LayoutBranch> findByL_P_M(
		long layoutSetBranchId, long plid, boolean master) {

		return getPersistence().findByL_P_M(layoutSetBranchId, plid, master);
	}

	/**
	 * Returns a range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @return the range of matching layout branches
	 */
	public static List<LayoutBranch> findByL_P_M(
		long layoutSetBranchId, long plid, boolean master, int start, int end) {

		return getPersistence().findByL_P_M(
			layoutSetBranchId, plid, master, start, end);
	}

	/**
	 * Returns an ordered range of all the layout branches where layoutSetBranchId = &#63; and plid = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetBranchId the layout set branch ID
	 * @param plid the plid
	 * @param master the master
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout branches
	 */
	public static List<LayoutBranch> findByL_P_M(
		long layoutSetBranchId, long plid, boolean master, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator) {

		return getPersistence().findByL_P_M(
			layoutSetBranchId, plid, master, start, end, orderByComparator);
	}

	public static LayoutBranchPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(LayoutBranchPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile LayoutBranchPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-185106051