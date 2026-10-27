/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout set service. This utility wraps <code>com.liferay.portal.service.persistence.impl.LayoutSetPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetPersistence
 * @generated
 */
public class LayoutSetUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<LayoutSet> layoutSets) {
		getPersistence().cacheResult(layoutSets);
	}

	/**
	 * @see BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(LayoutSet layoutSet) {
		getPersistence().cacheResult(layoutSet);
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
	public static void clearCache(LayoutSet layoutSet) {
		getPersistence().clearCache(layoutSet);
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
	public static Map<Serializable, LayoutSet> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<LayoutSet> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<LayoutSet> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<LayoutSet> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<LayoutSet> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutSet update(LayoutSet layoutSet) {
		return getPersistence().update(layoutSet);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutSet update(
		LayoutSet layoutSet, ServiceContext serviceContext) {

		return getPersistence().update(layoutSet, serviceContext);
	}

	/**
	 * Returns an ordered range of all the layout sets where layoutSetPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout sets
	 */
	public static List<LayoutSet> findByLayoutSetPrototypeUuid(
		String layoutSetPrototypeUuid, int start, int end,
		OrderByComparator<LayoutSet> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLayoutSetPrototypeUuid(
			layoutSetPrototypeUuid, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout set in the ordered set where layoutSetPrototypeUuid = &#63;.
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set
	 * @throws NoSuchLayoutSetException if a matching layout set could not be found
	 */
	public static LayoutSet findByLayoutSetPrototypeUuid_First(
			String layoutSetPrototypeUuid,
			OrderByComparator<LayoutSet> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutSetException {

		return getPersistence().findByLayoutSetPrototypeUuid_First(
			layoutSetPrototypeUuid, orderByComparator);
	}

	/**
	 * Returns the first layout set in the ordered set where layoutSetPrototypeUuid = &#63;.
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set, or <code>null</code> if a matching layout set could not be found
	 */
	public static LayoutSet fetchByLayoutSetPrototypeUuid_First(
		String layoutSetPrototypeUuid,
		OrderByComparator<LayoutSet> orderByComparator) {

		return getPersistence().fetchByLayoutSetPrototypeUuid_First(
			layoutSetPrototypeUuid, orderByComparator);
	}

	/**
	 * Removes all the layout sets where layoutSetPrototypeUuid = &#63; from the database.
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 */
	public static void removeByLayoutSetPrototypeUuid(
		String layoutSetPrototypeUuid) {

		getPersistence().removeByLayoutSetPrototypeUuid(layoutSetPrototypeUuid);
	}

	/**
	 * Returns the number of layout sets where layoutSetPrototypeUuid = &#63;.
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @return the number of matching layout sets
	 */
	public static int countByLayoutSetPrototypeUuid(
		String layoutSetPrototypeUuid) {

		return getPersistence().countByLayoutSetPrototypeUuid(
			layoutSetPrototypeUuid);
	}

	/**
	 * Returns the layout set where groupId = &#63; and privateLayout = &#63; or throws a <code>NoSuchLayoutSetException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout set
	 * @throws NoSuchLayoutSetException if a matching layout set could not be found
	 */
	public static LayoutSet findByG_P(long groupId, boolean privateLayout)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutSetException {

		return getPersistence().findByG_P(groupId, privateLayout);
	}

	/**
	 * Returns the layout set where groupId = &#63; and privateLayout = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout set, or <code>null</code> if a matching layout set could not be found
	 */
	public static LayoutSet fetchByG_P(
		long groupId, boolean privateLayout, boolean useFinderCache) {

		return getPersistence().fetchByG_P(
			groupId, privateLayout, useFinderCache);
	}

	/**
	 * Removes the layout set where groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the layout set that was removed
	 */
	public static LayoutSet removeByG_P(long groupId, boolean privateLayout)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutSetException {

		return getPersistence().removeByG_P(groupId, privateLayout);
	}

	/**
	 * Returns the number of layout sets where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layout sets
	 */
	public static int countByG_P(long groupId, boolean privateLayout) {
		return getPersistence().countByG_P(groupId, privateLayout);
	}

	/**
	 * Returns an ordered range of all the layout sets where companyId = &#63; and layoutSetPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout sets
	 */
	public static List<LayoutSet> findByC_L(
		long companyId, String layoutSetPrototypeUuid, int start, int end,
		OrderByComparator<LayoutSet> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_L(
			companyId, layoutSetPrototypeUuid, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout set in the ordered set where companyId = &#63; and layoutSetPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set
	 * @throws NoSuchLayoutSetException if a matching layout set could not be found
	 */
	public static LayoutSet findByC_L_First(
			long companyId, String layoutSetPrototypeUuid,
			OrderByComparator<LayoutSet> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutSetException {

		return getPersistence().findByC_L_First(
			companyId, layoutSetPrototypeUuid, orderByComparator);
	}

	/**
	 * Returns the first layout set in the ordered set where companyId = &#63; and layoutSetPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set, or <code>null</code> if a matching layout set could not be found
	 */
	public static LayoutSet fetchByC_L_First(
		long companyId, String layoutSetPrototypeUuid,
		OrderByComparator<LayoutSet> orderByComparator) {

		return getPersistence().fetchByC_L_First(
			companyId, layoutSetPrototypeUuid, orderByComparator);
	}

	/**
	 * Removes all the layout sets where companyId = &#63; and layoutSetPrototypeUuid = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 */
	public static void removeByC_L(
		long companyId, String layoutSetPrototypeUuid) {

		getPersistence().removeByC_L(companyId, layoutSetPrototypeUuid);
	}

	/**
	 * Returns the number of layout sets where companyId = &#63; and layoutSetPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @return the number of matching layout sets
	 */
	public static int countByC_L(
		long companyId, String layoutSetPrototypeUuid) {

		return getPersistence().countByC_L(companyId, layoutSetPrototypeUuid);
	}

	/**
	 * Returns an ordered range of all the layout sets where privateLayout = &#63; and logoId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout sets
	 */
	public static List<LayoutSet> findByP_L(
		boolean privateLayout, long logoId, int start, int end,
		OrderByComparator<LayoutSet> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_L(
			privateLayout, logoId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout set in the ordered set where privateLayout = &#63; and logoId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set
	 * @throws NoSuchLayoutSetException if a matching layout set could not be found
	 */
	public static LayoutSet findByP_L_First(
			boolean privateLayout, long logoId,
			OrderByComparator<LayoutSet> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutSetException {

		return getPersistence().findByP_L_First(
			privateLayout, logoId, orderByComparator);
	}

	/**
	 * Returns the first layout set in the ordered set where privateLayout = &#63; and logoId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set, or <code>null</code> if a matching layout set could not be found
	 */
	public static LayoutSet fetchByP_L_First(
		boolean privateLayout, long logoId,
		OrderByComparator<LayoutSet> orderByComparator) {

		return getPersistence().fetchByP_L_First(
			privateLayout, logoId, orderByComparator);
	}

	/**
	 * Removes all the layout sets where privateLayout = &#63; and logoId = &#63; from the database.
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 */
	public static void removeByP_L(boolean privateLayout, long logoId) {
		getPersistence().removeByP_L(privateLayout, logoId);
	}

	/**
	 * Returns the number of layout sets where privateLayout = &#63; and logoId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 * @return the number of matching layout sets
	 */
	public static int countByP_L(boolean privateLayout, long logoId) {
		return getPersistence().countByP_L(privateLayout, logoId);
	}

	/**
	 * Creates a new layout set with the primary key. Does not add the layout set to the database.
	 *
	 * @param layoutSetId the primary key for the new layout set
	 * @return the new layout set
	 */
	public static LayoutSet create(long layoutSetId) {
		return getPersistence().create(layoutSetId);
	}

	/**
	 * Removes the layout set with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutSetId the primary key of the layout set
	 * @return the layout set that was removed
	 * @throws NoSuchLayoutSetException if a layout set with the primary key could not be found
	 */
	public static LayoutSet remove(long layoutSetId)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutSetException {

		return getPersistence().remove(layoutSetId);
	}

	public static LayoutSet updateImpl(LayoutSet layoutSet) {
		return getPersistence().updateImpl(layoutSet);
	}

	/**
	 * Returns the layout set with the primary key or throws a <code>NoSuchLayoutSetException</code> if it could not be found.
	 *
	 * @param layoutSetId the primary key of the layout set
	 * @return the layout set
	 * @throws NoSuchLayoutSetException if a layout set with the primary key could not be found
	 */
	public static LayoutSet findByPrimaryKey(long layoutSetId)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutSetException {

		return getPersistence().findByPrimaryKey(layoutSetId);
	}

	/**
	 * Returns the layout set with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutSetId the primary key of the layout set
	 * @return the layout set, or <code>null</code> if a layout set with the primary key could not be found
	 */
	public static LayoutSet fetchByPrimaryKey(long layoutSetId) {
		return getPersistence().fetchByPrimaryKey(layoutSetId);
	}

	/**
	 * Returns the layout set where groupId = &#63; and privateLayout = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout set, or <code>null</code> if a matching layout set could not be found
	 */
	public static LayoutSet fetchByG_P(long groupId, boolean privateLayout) {
		return getPersistence().fetchByG_P(groupId, privateLayout);
	}

	/**
	 * Returns all the layout sets where layoutSetPrototypeUuid = &#63;.
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @return the matching layout sets
	 */
	public static List<LayoutSet> findByLayoutSetPrototypeUuid(
		String layoutSetPrototypeUuid) {

		return getPersistence().findByLayoutSetPrototypeUuid(
			layoutSetPrototypeUuid);
	}

	/**
	 * Returns a range of all the layout sets where layoutSetPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @return the range of matching layout sets
	 */
	public static List<LayoutSet> findByLayoutSetPrototypeUuid(
		String layoutSetPrototypeUuid, int start, int end) {

		return getPersistence().findByLayoutSetPrototypeUuid(
			layoutSetPrototypeUuid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout sets where layoutSetPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout sets
	 */
	public static List<LayoutSet> findByLayoutSetPrototypeUuid(
		String layoutSetPrototypeUuid, int start, int end,
		OrderByComparator<LayoutSet> orderByComparator) {

		return getPersistence().findByLayoutSetPrototypeUuid(
			layoutSetPrototypeUuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout sets where companyId = &#63; and layoutSetPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @return the matching layout sets
	 */
	public static List<LayoutSet> findByC_L(
		long companyId, String layoutSetPrototypeUuid) {

		return getPersistence().findByC_L(companyId, layoutSetPrototypeUuid);
	}

	/**
	 * Returns a range of all the layout sets where companyId = &#63; and layoutSetPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @return the range of matching layout sets
	 */
	public static List<LayoutSet> findByC_L(
		long companyId, String layoutSetPrototypeUuid, int start, int end) {

		return getPersistence().findByC_L(
			companyId, layoutSetPrototypeUuid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout sets where companyId = &#63; and layoutSetPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout sets
	 */
	public static List<LayoutSet> findByC_L(
		long companyId, String layoutSetPrototypeUuid, int start, int end,
		OrderByComparator<LayoutSet> orderByComparator) {

		return getPersistence().findByC_L(
			companyId, layoutSetPrototypeUuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout sets where privateLayout = &#63; and logoId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 * @return the matching layout sets
	 */
	public static List<LayoutSet> findByP_L(
		boolean privateLayout, long logoId) {

		return getPersistence().findByP_L(privateLayout, logoId);
	}

	/**
	 * Returns a range of all the layout sets where privateLayout = &#63; and logoId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @return the range of matching layout sets
	 */
	public static List<LayoutSet> findByP_L(
		boolean privateLayout, long logoId, int start, int end) {

		return getPersistence().findByP_L(privateLayout, logoId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout sets where privateLayout = &#63; and logoId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout sets
	 */
	public static List<LayoutSet> findByP_L(
		boolean privateLayout, long logoId, int start, int end,
		OrderByComparator<LayoutSet> orderByComparator) {

		return getPersistence().findByP_L(
			privateLayout, logoId, start, end, orderByComparator);
	}

	public static LayoutSetPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(LayoutSetPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile LayoutSetPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-714179083