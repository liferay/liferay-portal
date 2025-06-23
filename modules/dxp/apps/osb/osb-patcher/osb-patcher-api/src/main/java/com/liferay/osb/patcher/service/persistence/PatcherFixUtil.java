/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the patcher fix service. This utility wraps <code>com.liferay.osb.patcher.service.persistence.impl.PatcherFixPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixPersistence
 * @generated
 */
public class PatcherFixUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(PatcherFix patcherFix) {
		getPersistence().clearCache(patcherFix);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, PatcherFix> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PatcherFix> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherFix> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherFix> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PatcherFix update(PatcherFix patcherFix) {
		return getPersistence().update(patcherFix);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PatcherFix update(
		PatcherFix patcherFix, ServiceContext serviceContext) {

		return getPersistence().update(patcherFix, serviceContext);
	}

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher fixes
	 */
	public static List<PatcherFix> findByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return getPersistence().findByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	public static List<PatcherFix> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end) {

		return getPersistence().findByPatcherProjectVersionId(
			patcherProjectVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().findByPatcherProjectVersionId(
			patcherProjectVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPatcherProjectVersionId(
			patcherProjectVersionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByPatcherProjectVersionId_First(
			long patcherProjectVersionId,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByPatcherProjectVersionId_First(
			patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByPatcherProjectVersionId_First(
		long patcherProjectVersionId,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByPatcherProjectVersionId_First(
			patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByPatcherProjectVersionId_Last(
			long patcherProjectVersionId,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByPatcherProjectVersionId_Last(
			patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByPatcherProjectVersionId_Last(
		long patcherProjectVersionId,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByPatcherProjectVersionId_Last(
			patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] findByPatcherProjectVersionId_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByPatcherProjectVersionId_PrevAndNext(
			patcherFixId, patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return getPersistence().filterFindByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end) {

		return getPersistence().filterFindByPatcherProjectVersionId(
			patcherProjectVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().filterFindByPatcherProjectVersionId(
			patcherProjectVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] filterFindByPatcherProjectVersionId_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().filterFindByPatcherProjectVersionId_PrevAndNext(
			patcherFixId, patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 */
	public static void removeByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		getPersistence().removeByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fixes
	 */
	public static int countByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return getPersistence().countByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	public static int filterCountByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return getPersistence().filterCountByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return getPersistence().findByP_L_T(
			patcherProjectVersionId, latestFix, type);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end) {

		return getPersistence().findByP_L_T(
			patcherProjectVersionId, latestFix, type, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().findByP_L_T(
			patcherProjectVersionId, latestFix, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_L_T(
			patcherProjectVersionId, latestFix, type, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByP_L_T_First(
			long patcherProjectVersionId, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByP_L_T_First(
			patcherProjectVersionId, latestFix, type, orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByP_L_T_First(
		long patcherProjectVersionId, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByP_L_T_First(
			patcherProjectVersionId, latestFix, type, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByP_L_T_Last(
			long patcherProjectVersionId, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByP_L_T_Last(
			patcherProjectVersionId, latestFix, type, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByP_L_T_Last(
		long patcherProjectVersionId, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByP_L_T_Last(
			patcherProjectVersionId, latestFix, type, orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] findByP_L_T_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByP_L_T_PrevAndNext(
			patcherFixId, patcherProjectVersionId, latestFix, type,
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return getPersistence().filterFindByP_L_T(
			patcherProjectVersionId, latestFix, type);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end) {

		return getPersistence().filterFindByP_L_T(
			patcherProjectVersionId, latestFix, type, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().filterFindByP_L_T(
			patcherProjectVersionId, latestFix, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] filterFindByP_L_T_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().filterFindByP_L_T_PrevAndNext(
			patcherFixId, patcherProjectVersionId, latestFix, type,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 */
	public static void removeByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		getPersistence().removeByP_L_T(
			patcherProjectVersionId, latestFix, type);
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	public static int countByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return getPersistence().countByP_L_T(
			patcherProjectVersionId, latestFix, type);
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	public static int filterCountByP_L_T(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return getPersistence().filterCountByP_L_T(
			patcherProjectVersionId, latestFix, type);
	}

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return getPersistence().findByP_L_NotT(
			patcherProjectVersionId, latestFix, type);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end) {

		return getPersistence().findByP_L_NotT(
			patcherProjectVersionId, latestFix, type, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().findByP_L_NotT(
			patcherProjectVersionId, latestFix, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_L_NotT(
			patcherProjectVersionId, latestFix, type, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByP_L_NotT_First(
			long patcherProjectVersionId, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByP_L_NotT_First(
			patcherProjectVersionId, latestFix, type, orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByP_L_NotT_First(
		long patcherProjectVersionId, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByP_L_NotT_First(
			patcherProjectVersionId, latestFix, type, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByP_L_NotT_Last(
			long patcherProjectVersionId, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByP_L_NotT_Last(
			patcherProjectVersionId, latestFix, type, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByP_L_NotT_Last(
		long patcherProjectVersionId, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByP_L_NotT_Last(
			patcherProjectVersionId, latestFix, type, orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] findByP_L_NotT_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByP_L_NotT_PrevAndNext(
			patcherFixId, patcherProjectVersionId, latestFix, type,
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return getPersistence().filterFindByP_L_NotT(
			patcherProjectVersionId, latestFix, type);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end) {

		return getPersistence().filterFindByP_L_NotT(
			patcherProjectVersionId, latestFix, type, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().filterFindByP_L_NotT(
			patcherProjectVersionId, latestFix, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] filterFindByP_L_NotT_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().filterFindByP_L_NotT_PrevAndNext(
			patcherFixId, patcherProjectVersionId, latestFix, type,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 */
	public static void removeByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		getPersistence().removeByP_L_NotT(
			patcherProjectVersionId, latestFix, type);
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	public static int countByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return getPersistence().countByP_L_NotT(
			patcherProjectVersionId, latestFix, type);
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	public static int filterCountByP_L_NotT(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return getPersistence().filterCountByP_L_NotT(
			patcherProjectVersionId, latestFix, type);
	}

	/**
	 * Returns all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	public static List<PatcherFix> findByK_GtKV_NotT(
		String key, double keyVersion, int type) {

		return getPersistence().findByK_GtKV_NotT(key, keyVersion, type);
	}

	/**
	 * Returns a range of all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	public static List<PatcherFix> findByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end) {

		return getPersistence().findByK_GtKV_NotT(
			key, keyVersion, type, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().findByK_GtKV_NotT(
			key, keyVersion, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByK_GtKV_NotT(
			key, keyVersion, type, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByK_GtKV_NotT_First(
			String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByK_GtKV_NotT_First(
			key, keyVersion, type, orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByK_GtKV_NotT_First(
		String key, double keyVersion, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByK_GtKV_NotT_First(
			key, keyVersion, type, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByK_GtKV_NotT_Last(
			String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByK_GtKV_NotT_Last(
			key, keyVersion, type, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByK_GtKV_NotT_Last(
		String key, double keyVersion, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByK_GtKV_NotT_Last(
			key, keyVersion, type, orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] findByK_GtKV_NotT_PrevAndNext(
			long patcherFixId, String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByK_GtKV_NotT_PrevAndNext(
			patcherFixId, key, keyVersion, type, orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByK_GtKV_NotT(
		String key, double keyVersion, int type) {

		return getPersistence().filterFindByK_GtKV_NotT(key, keyVersion, type);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end) {

		return getPersistence().filterFindByK_GtKV_NotT(
			key, keyVersion, type, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByK_GtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().filterFindByK_GtKV_NotT(
			key, keyVersion, type, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] filterFindByK_GtKV_NotT_PrevAndNext(
			long patcherFixId, String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().filterFindByK_GtKV_NotT_PrevAndNext(
			patcherFixId, key, keyVersion, type, orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 */
	public static void removeByK_GtKV_NotT(
		String key, double keyVersion, int type) {

		getPersistence().removeByK_GtKV_NotT(key, keyVersion, type);
	}

	/**
	 * Returns the number of patcher fixes where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	public static int countByK_GtKV_NotT(
		String key, double keyVersion, int type) {

		return getPersistence().countByK_GtKV_NotT(key, keyVersion, type);
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where key = &#63; and keyVersion &gt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	public static int filterCountByK_GtKV_NotT(
		String key, double keyVersion, int type) {

		return getPersistence().filterCountByK_GtKV_NotT(key, keyVersion, type);
	}

	/**
	 * Returns all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	public static List<PatcherFix> findByK_LtKV_NotT(
		String key, double keyVersion, int type) {

		return getPersistence().findByK_LtKV_NotT(key, keyVersion, type);
	}

	/**
	 * Returns a range of all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	public static List<PatcherFix> findByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end) {

		return getPersistence().findByK_LtKV_NotT(
			key, keyVersion, type, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().findByK_LtKV_NotT(
			key, keyVersion, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByK_LtKV_NotT(
			key, keyVersion, type, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByK_LtKV_NotT_First(
			String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByK_LtKV_NotT_First(
			key, keyVersion, type, orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByK_LtKV_NotT_First(
		String key, double keyVersion, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByK_LtKV_NotT_First(
			key, keyVersion, type, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByK_LtKV_NotT_Last(
			String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByK_LtKV_NotT_Last(
			key, keyVersion, type, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByK_LtKV_NotT_Last(
		String key, double keyVersion, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByK_LtKV_NotT_Last(
			key, keyVersion, type, orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] findByK_LtKV_NotT_PrevAndNext(
			long patcherFixId, String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByK_LtKV_NotT_PrevAndNext(
			patcherFixId, key, keyVersion, type, orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByK_LtKV_NotT(
		String key, double keyVersion, int type) {

		return getPersistence().filterFindByK_LtKV_NotT(key, keyVersion, type);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end) {

		return getPersistence().filterFindByK_LtKV_NotT(
			key, keyVersion, type, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByK_LtKV_NotT(
		String key, double keyVersion, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().filterFindByK_LtKV_NotT(
			key, keyVersion, type, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] filterFindByK_LtKV_NotT_PrevAndNext(
			long patcherFixId, String key, double keyVersion, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().filterFindByK_LtKV_NotT_PrevAndNext(
			patcherFixId, key, keyVersion, type, orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 */
	public static void removeByK_LtKV_NotT(
		String key, double keyVersion, int type) {

		getPersistence().removeByK_LtKV_NotT(key, keyVersion, type);
	}

	/**
	 * Returns the number of patcher fixes where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	public static int countByK_LtKV_NotT(
		String key, double keyVersion, int type) {

		return getPersistence().countByK_LtKV_NotT(key, keyVersion, type);
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where key = &#63; and keyVersion &lt; &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	public static int filterCountByK_LtKV_NotT(
		String key, double keyVersion, int type) {

		return getPersistence().filterCountByK_LtKV_NotT(key, keyVersion, type);
	}

	/**
	 * Returns all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	public static List<PatcherFix> findByK_L_NotT(
		String key, boolean latestFix, int type) {

		return getPersistence().findByK_L_NotT(key, latestFix, type);
	}

	/**
	 * Returns a range of all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	public static List<PatcherFix> findByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end) {

		return getPersistence().findByK_L_NotT(
			key, latestFix, type, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().findByK_L_NotT(
			key, latestFix, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByK_L_NotT(
			key, latestFix, type, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByK_L_NotT_First(
			String key, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByK_L_NotT_First(
			key, latestFix, type, orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByK_L_NotT_First(
		String key, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByK_L_NotT_First(
			key, latestFix, type, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByK_L_NotT_Last(
			String key, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByK_L_NotT_Last(
			key, latestFix, type, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByK_L_NotT_Last(
		String key, boolean latestFix, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByK_L_NotT_Last(
			key, latestFix, type, orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] findByK_L_NotT_PrevAndNext(
			long patcherFixId, String key, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByK_L_NotT_PrevAndNext(
			patcherFixId, key, latestFix, type, orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByK_L_NotT(
		String key, boolean latestFix, int type) {

		return getPersistence().filterFindByK_L_NotT(key, latestFix, type);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end) {

		return getPersistence().filterFindByK_L_NotT(
			key, latestFix, type, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByK_L_NotT(
		String key, boolean latestFix, int type, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().filterFindByK_L_NotT(
			key, latestFix, type, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] filterFindByK_L_NotT_PrevAndNext(
			long patcherFixId, String key, boolean latestFix, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().filterFindByK_L_NotT_PrevAndNext(
			patcherFixId, key, latestFix, type, orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63; from the database.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 */
	public static void removeByK_L_NotT(
		String key, boolean latestFix, int type) {

		getPersistence().removeByK_L_NotT(key, latestFix, type);
	}

	/**
	 * Returns the number of patcher fixes where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	public static int countByK_L_NotT(String key, boolean latestFix, int type) {
		return getPersistence().countByK_L_NotT(key, latestFix, type);
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where key = &#63; and latestFix = &#63; and type &ne; &#63;.
	 *
	 * @param key the key
	 * @param latestFix the latest fix
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	public static int filterCountByK_L_NotT(
		String key, boolean latestFix, int type) {

		return getPersistence().filterCountByK_L_NotT(key, latestFix, type);
	}

	/**
	 * Returns all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @return the matching patcher fixes
	 */
	public static List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		return getPersistence().findByLtM_N_T_S(
			modifiedDate, notified, type, status);
	}

	/**
	 * Returns a range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	public static List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end) {

		return getPersistence().findByLtM_N_T_S(
			modifiedDate, notified, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().findByLtM_N_T_S(
			modifiedDate, notified, type, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtM_N_T_S(
			modifiedDate, notified, type, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByLtM_N_T_S_First(
			Date modifiedDate, boolean notified, int type, int status,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByLtM_N_T_S_First(
			modifiedDate, notified, type, status, orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByLtM_N_T_S_First(
		Date modifiedDate, boolean notified, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByLtM_N_T_S_First(
			modifiedDate, notified, type, status, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByLtM_N_T_S_Last(
			Date modifiedDate, boolean notified, int type, int status,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByLtM_N_T_S_Last(
			modifiedDate, notified, type, status, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByLtM_N_T_S_Last(
		Date modifiedDate, boolean notified, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByLtM_N_T_S_Last(
			modifiedDate, notified, type, status, orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] findByLtM_N_T_S_PrevAndNext(
			long patcherFixId, Date modifiedDate, boolean notified, int type,
			int status, OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByLtM_N_T_S_PrevAndNext(
			patcherFixId, modifiedDate, notified, type, status,
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @return the matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		return getPersistence().filterFindByLtM_N_T_S(
			modifiedDate, notified, type, status);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end) {

		return getPersistence().filterFindByLtM_N_T_S(
			modifiedDate, notified, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().filterFindByLtM_N_T_S(
			modifiedDate, notified, type, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] filterFindByLtM_N_T_S_PrevAndNext(
			long patcherFixId, Date modifiedDate, boolean notified, int type,
			int status, OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().filterFindByLtM_N_T_S_PrevAndNext(
			patcherFixId, modifiedDate, notified, type, status,
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @return the matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status) {

		return getPersistence().filterFindByLtM_N_T_S(
			modifiedDate, notified, types, status);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end) {

		return getPersistence().filterFindByLtM_N_T_S(
			modifiedDate, notified, types, status, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().filterFindByLtM_N_T_S(
			modifiedDate, notified, types, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @return the matching patcher fixes
	 */
	public static List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status) {

		return getPersistence().findByLtM_N_T_S(
			modifiedDate, notified, types, status);
	}

	/**
	 * Returns a range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	public static List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end) {

		return getPersistence().findByLtM_N_T_S(
			modifiedDate, notified, types, status, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().findByLtM_N_T_S(
			modifiedDate, notified, types, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status, int start,
		int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtM_N_T_S(
			modifiedDate, notified, types, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 */
	public static void removeByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		getPersistence().removeByLtM_N_T_S(
			modifiedDate, notified, type, status);
	}

	/**
	 * Returns the number of patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @return the number of matching patcher fixes
	 */
	public static int countByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		return getPersistence().countByLtM_N_T_S(
			modifiedDate, notified, type, status);
	}

	/**
	 * Returns the number of patcher fixes where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @return the number of matching patcher fixes
	 */
	public static int countByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status) {

		return getPersistence().countByLtM_N_T_S(
			modifiedDate, notified, types, status);
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param type the type
	 * @param status the status
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	public static int filterCountByLtM_N_T_S(
		Date modifiedDate, boolean notified, int type, int status) {

		return getPersistence().filterCountByLtM_N_T_S(
			modifiedDate, notified, type, status);
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param types the types
	 * @param status the status
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	public static int filterCountByLtM_N_T_S(
		Date modifiedDate, boolean notified, int[] types, int status) {

		return getPersistence().filterCountByLtM_N_T_S(
			modifiedDate, notified, types, status);
	}

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @return the matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		return getPersistence().findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end) {

		return getPersistence().findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByP_L_N_NotT_First(
			long patcherProjectVersionId, boolean latestFix, String name,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByP_L_N_NotT_First(
			patcherProjectVersionId, latestFix, name, type, orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByP_L_N_NotT_First(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByP_L_N_NotT_First(
			patcherProjectVersionId, latestFix, name, type, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByP_L_N_NotT_Last(
			long patcherProjectVersionId, boolean latestFix, String name,
			int type, OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByP_L_N_NotT_Last(
			patcherProjectVersionId, latestFix, name, type, orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByP_L_N_NotT_Last(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByP_L_N_NotT_Last(
			patcherProjectVersionId, latestFix, name, type, orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] findByP_L_N_NotT_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			String name, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByP_L_N_NotT_PrevAndNext(
			patcherFixId, patcherProjectVersionId, latestFix, name, type,
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @return the matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		return getPersistence().filterFindByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end) {

		return getPersistence().filterFindByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name, int type,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().filterFindByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] filterFindByP_L_N_NotT_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			String name, int type,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().filterFindByP_L_N_NotT_PrevAndNext(
			patcherFixId, patcherProjectVersionId, latestFix, name, type,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 */
	public static void removeByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		getPersistence().removeByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type);
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @return the number of matching patcher fixes
	 */
	public static int countByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		return getPersistence().countByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type);
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and name = &#63; and type &ne; &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param name the name
	 * @param type the type
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	public static int filterCountByP_L_N_NotT(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		return getPersistence().filterCountByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type);
	}

	/**
	 * Returns all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @return the matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		return getPersistence().findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status);
	}

	/**
	 * Returns a range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end) {

		return getPersistence().findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fixes
	 */
	public static List<PatcherFix> findByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByP_L_NotT_S_First(
			long patcherProjectVersionId, boolean latestFix, int type,
			int status, OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByP_L_NotT_S_First(
			patcherProjectVersionId, latestFix, type, status,
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByP_L_NotT_S_First(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByP_L_NotT_S_First(
			patcherProjectVersionId, latestFix, type, status,
			orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix
	 * @throws NoSuchPatcherFixException if a matching patcher fix could not be found
	 */
	public static PatcherFix findByP_L_NotT_S_Last(
			long patcherProjectVersionId, boolean latestFix, int type,
			int status, OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByP_L_NotT_S_Last(
			patcherProjectVersionId, latestFix, type, status,
			orderByComparator);
	}

	/**
	 * Returns the last patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix, or <code>null</code> if a matching patcher fix could not be found
	 */
	public static PatcherFix fetchByP_L_NotT_S_Last(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().fetchByP_L_NotT_S_Last(
			patcherProjectVersionId, latestFix, type, status,
			orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] findByP_L_NotT_S_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			int type, int status,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByP_L_NotT_S_PrevAndNext(
			patcherFixId, patcherProjectVersionId, latestFix, type, status,
			orderByComparator);
	}

	/**
	 * Returns all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @return the matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		return getPersistence().filterFindByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status);
	}

	/**
	 * Returns a range of all the patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end) {

		return getPersistence().filterFindByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes that the user has permissions to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fixes that the user has permission to view
	 */
	public static List<PatcherFix> filterFindByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status,
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().filterFindByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher fixes before and after the current patcher fix in the ordered set of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherFixId the primary key of the current patcher fix
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix[] filterFindByP_L_NotT_S_PrevAndNext(
			long patcherFixId, long patcherProjectVersionId, boolean latestFix,
			int type, int status,
			OrderByComparator<PatcherFix> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().filterFindByP_L_NotT_S_PrevAndNext(
			patcherFixId, patcherProjectVersionId, latestFix, type, status,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 */
	public static void removeByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		getPersistence().removeByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status);
	}

	/**
	 * Returns the number of patcher fixes where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @return the number of matching patcher fixes
	 */
	public static int countByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		return getPersistence().countByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status);
	}

	/**
	 * Returns the number of patcher fixes that the user has permission to view where patcherProjectVersionId = &#63; and latestFix = &#63; and type &ne; &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param latestFix the latest fix
	 * @param type the type
	 * @param status the status
	 * @return the number of matching patcher fixes that the user has permission to view
	 */
	public static int filterCountByP_L_NotT_S(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		return getPersistence().filterCountByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status);
	}

	/**
	 * Caches the patcher fix in the entity cache if it is enabled.
	 *
	 * @param patcherFix the patcher fix
	 */
	public static void cacheResult(PatcherFix patcherFix) {
		getPersistence().cacheResult(patcherFix);
	}

	/**
	 * Caches the patcher fixes in the entity cache if it is enabled.
	 *
	 * @param patcherFixes the patcher fixes
	 */
	public static void cacheResult(List<PatcherFix> patcherFixes) {
		getPersistence().cacheResult(patcherFixes);
	}

	/**
	 * Creates a new patcher fix with the primary key. Does not add the patcher fix to the database.
	 *
	 * @param patcherFixId the primary key for the new patcher fix
	 * @return the new patcher fix
	 */
	public static PatcherFix create(long patcherFixId) {
		return getPersistence().create(patcherFixId);
	}

	/**
	 * Removes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix that was removed
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix remove(long patcherFixId)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().remove(patcherFixId);
	}

	public static PatcherFix updateImpl(PatcherFix patcherFix) {
		return getPersistence().updateImpl(patcherFix);
	}

	/**
	 * Returns the patcher fix with the primary key or throws a <code>NoSuchPatcherFixException</code> if it could not be found.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix
	 * @throws NoSuchPatcherFixException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix findByPrimaryKey(long patcherFixId)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixException {

		return getPersistence().findByPrimaryKey(patcherFixId);
	}

	/**
	 * Returns the patcher fix with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix, or <code>null</code> if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix fetchByPrimaryKey(long patcherFixId) {
		return getPersistence().fetchByPrimaryKey(patcherFixId);
	}

	/**
	 * Returns all the patcher fixes.
	 *
	 * @return the patcher fixes
	 */
	public static List<PatcherFix> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the patcher fixes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher fixes
	 */
	public static List<PatcherFix> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fixes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixes
	 */
	public static List<PatcherFix> findAll(
		int start, int end, OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fixes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fixes
	 */
	public static List<PatcherFix> findAll(
		int start, int end, OrderByComparator<PatcherFix> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher fixes from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of patcher fixes.
	 *
	 * @return the number of patcher fixes
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	/**
	 * Returns the primaryKeys of patcher builds associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return long[] of the primaryKeys of patcher builds associated with the patcher fix
	 */
	public static long[] getPatcherBuildPrimaryKeys(long pk) {
		return getPersistence().getPatcherBuildPrimaryKeys(pk);
	}

	/**
	 * Returns all the patcher fix associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the patcher fixes associated with the patcher build
	 */
	public static List<PatcherFix> getPatcherBuildPatcherFixes(long pk) {
		return getPersistence().getPatcherBuildPatcherFixes(pk);
	}

	/**
	 * Returns all the patcher fix associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher fixes associated with the patcher build
	 */
	public static List<PatcherFix> getPatcherBuildPatcherFixes(
		long pk, int start, int end) {

		return getPersistence().getPatcherBuildPatcherFixes(pk, start, end);
	}

	/**
	 * Returns all the patcher fix associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixes associated with the patcher build
	 */
	public static List<PatcherFix> getPatcherBuildPatcherFixes(
		long pk, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().getPatcherBuildPatcherFixes(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher builds associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the number of patcher builds associated with the patcher fix
	 */
	public static int getPatcherBuildsSize(long pk) {
		return getPersistence().getPatcherBuildsSize(pk);
	}

	/**
	 * Returns <code>true</code> if the patcher build is associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if the patcher build is associated with the patcher fix; <code>false</code> otherwise
	 */
	public static boolean containsPatcherBuild(long pk, long patcherBuildPK) {
		return getPersistence().containsPatcherBuild(pk, patcherBuildPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix has any patcher builds associated with it.
	 *
	 * @param pk the primary key of the patcher fix to check for associations with patcher builds
	 * @return <code>true</code> if the patcher fix has any patcher builds associated with it; <code>false</code> otherwise
	 */
	public static boolean containsPatcherBuilds(long pk) {
		return getPersistence().containsPatcherBuilds(pk);
	}

	/**
	 * Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if an association between the patcher fix and the patcher build was added; <code>false</code> if they were already associated
	 */
	public static boolean addPatcherBuild(long pk, long patcherBuildPK) {
		return getPersistence().addPatcherBuild(pk, patcherBuildPK);
	}

	/**
	 * Adds an association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuild the patcher build
	 * @return <code>true</code> if an association between the patcher fix and the patcher build was added; <code>false</code> if they were already associated
	 */
	public static boolean addPatcherBuild(
		long pk, com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {

		return getPersistence().addPatcherBuild(pk, patcherBuild);
	}

	/**
	 * Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	public static boolean addPatcherBuilds(long pk, long[] patcherBuildPKs) {
		return getPersistence().addPatcherBuilds(pk, patcherBuildPKs);
	}

	/**
	 * Adds an association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	public static boolean addPatcherBuilds(
		long pk,
		List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds) {

		return getPersistence().addPatcherBuilds(pk, patcherBuilds);
	}

	/**
	 * Clears all associations between the patcher fix and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix to clear the associated patcher builds from
	 */
	public static void clearPatcherBuilds(long pk) {
		getPersistence().clearPatcherBuilds(pk);
	}

	/**
	 * Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPK the primary key of the patcher build
	 */
	public static void removePatcherBuild(long pk, long patcherBuildPK) {
		getPersistence().removePatcherBuild(pk, patcherBuildPK);
	}

	/**
	 * Removes the association between the patcher fix and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuild the patcher build
	 */
	public static void removePatcherBuild(
		long pk, com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {

		getPersistence().removePatcherBuild(pk, patcherBuild);
	}

	/**
	 * Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 */
	public static void removePatcherBuilds(long pk, long[] patcherBuildPKs) {
		getPersistence().removePatcherBuilds(pk, patcherBuildPKs);
	}

	/**
	 * Removes the association between the patcher fix and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds
	 */
	public static void removePatcherBuilds(
		long pk,
		List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds) {

		getPersistence().removePatcherBuilds(pk, patcherBuilds);
	}

	/**
	 * Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher fix
	 */
	public static void setPatcherBuilds(long pk, long[] patcherBuildPKs) {
		getPersistence().setPatcherBuilds(pk, patcherBuildPKs);
	}

	/**
	 * Sets the patcher builds associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherBuilds the patcher builds to be associated with the patcher fix
	 */
	public static void setPatcherBuilds(
		long pk,
		List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds) {

		getPersistence().setPatcherBuilds(pk, patcherBuilds);
	}

	/**
	 * Returns the primaryKeys of patcher fix packs associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return long[] of the primaryKeys of patcher fix packs associated with the patcher fix
	 */
	public static long[] getPatcherFixPackPrimaryKeys(long pk) {
		return getPersistence().getPatcherFixPackPrimaryKeys(pk);
	}

	/**
	 * Returns all the patcher fix associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return the patcher fixes associated with the patcher fix pack
	 */
	public static List<PatcherFix> getPatcherFixPackPatcherFixes(long pk) {
		return getPersistence().getPatcherFixPackPatcherFixes(pk);
	}

	/**
	 * Returns all the patcher fix associated with the patcher fix pack.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fixes associated with the patcher fix pack
	 */
	public static List<PatcherFix> getPatcherFixPackPatcherFixes(
		long pk, int start, int end) {

		return getPersistence().getPatcherFixPackPatcherFixes(pk, start, end);
	}

	/**
	 * Returns all the patcher fix associated with the patcher fix pack.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fixes associated with the patcher fix pack
	 */
	public static List<PatcherFix> getPatcherFixPackPatcherFixes(
		long pk, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getPersistence().getPatcherFixPackPatcherFixes(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fix packs associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the number of patcher fix packs associated with the patcher fix
	 */
	public static int getPatcherFixPacksSize(long pk) {
		return getPersistence().getPatcherFixPacksSize(pk);
	}

	/**
	 * Returns <code>true</code> if the patcher fix pack is associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 * @return <code>true</code> if the patcher fix pack is associated with the patcher fix; <code>false</code> otherwise
	 */
	public static boolean containsPatcherFixPack(
		long pk, long patcherFixPackPK) {

		return getPersistence().containsPatcherFixPack(pk, patcherFixPackPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix has any patcher fix packs associated with it.
	 *
	 * @param pk the primary key of the patcher fix to check for associations with patcher fix packs
	 * @return <code>true</code> if the patcher fix has any patcher fix packs associated with it; <code>false</code> otherwise
	 */
	public static boolean containsPatcherFixPacks(long pk) {
		return getPersistence().containsPatcherFixPacks(pk);
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 * @return <code>true</code> if an association between the patcher fix and the patcher fix pack was added; <code>false</code> if they were already associated
	 */
	public static boolean addPatcherFixPack(long pk, long patcherFixPackPK) {
		return getPersistence().addPatcherFixPack(pk, patcherFixPackPK);
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPack the patcher fix pack
	 * @return <code>true</code> if an association between the patcher fix and the patcher fix pack was added; <code>false</code> if they were already associated
	 */
	public static boolean addPatcherFixPack(
		long pk, com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack) {

		return getPersistence().addPatcherFixPack(pk, patcherFixPack);
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher fix packs was added; <code>false</code> if they were all already associated
	 */
	public static boolean addPatcherFixPacks(
		long pk, long[] patcherFixPackPKs) {

		return getPersistence().addPatcherFixPacks(pk, patcherFixPackPKs);
	}

	/**
	 * Adds an association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs
	 * @return <code>true</code> if at least one association between the patcher fix and the patcher fix packs was added; <code>false</code> if they were all already associated
	 */
	public static boolean addPatcherFixPacks(
		long pk,
		List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks) {

		return getPersistence().addPatcherFixPacks(pk, patcherFixPacks);
	}

	/**
	 * Clears all associations between the patcher fix and its patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix to clear the associated patcher fix packs from
	 */
	public static void clearPatcherFixPacks(long pk) {
		getPersistence().clearPatcherFixPacks(pk);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPK the primary key of the patcher fix pack
	 */
	public static void removePatcherFixPack(long pk, long patcherFixPackPK) {
		getPersistence().removePatcherFixPack(pk, patcherFixPackPK);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix pack. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPack the patcher fix pack
	 */
	public static void removePatcherFixPack(
		long pk, com.liferay.osb.patcher.model.PatcherFixPack patcherFixPack) {

		getPersistence().removePatcherFixPack(pk, patcherFixPack);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs
	 */
	public static void removePatcherFixPacks(
		long pk, long[] patcherFixPackPKs) {

		getPersistence().removePatcherFixPacks(pk, patcherFixPackPKs);
	}

	/**
	 * Removes the association between the patcher fix and the patcher fix packs. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs
	 */
	public static void removePatcherFixPacks(
		long pk,
		List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks) {

		getPersistence().removePatcherFixPacks(pk, patcherFixPacks);
	}

	/**
	 * Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPackPKs the primary keys of the patcher fix packs to be associated with the patcher fix
	 */
	public static void setPatcherFixPacks(long pk, long[] patcherFixPackPKs) {
		getPersistence().setPatcherFixPacks(pk, patcherFixPackPKs);
	}

	/**
	 * Sets the patcher fix packs associated with the patcher fix, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix
	 * @param patcherFixPacks the patcher fix packs to be associated with the patcher fix
	 */
	public static void setPatcherFixPacks(
		long pk,
		List<com.liferay.osb.patcher.model.PatcherFixPack> patcherFixPacks) {

		getPersistence().setPatcherFixPacks(pk, patcherFixPacks);
	}

	public static PatcherFixPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(PatcherFixPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile PatcherFixPersistence _persistence;

}