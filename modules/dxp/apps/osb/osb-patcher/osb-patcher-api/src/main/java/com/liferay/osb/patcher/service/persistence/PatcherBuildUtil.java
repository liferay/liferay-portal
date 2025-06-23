/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the patcher build service. This utility wraps <code>com.liferay.osb.patcher.service.persistence.impl.PatcherBuildPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildPersistence
 * @generated
 */
public class PatcherBuildUtil {

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
	public static void clearCache(PatcherBuild patcherBuild) {
		getPersistence().clearCache(patcherBuild);
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
	public static Map<Serializable, PatcherBuild> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PatcherBuild> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherBuild> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherBuild> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PatcherBuild update(PatcherBuild patcherBuild) {
		return getPersistence().update(patcherBuild);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PatcherBuild update(
		PatcherBuild patcherBuild, ServiceContext serviceContext) {

		return getPersistence().update(patcherBuild, serviceContext);
	}

	/**
	 * Returns all the patcher builds where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByPatcherFixId(long patcherFixId) {
		return getPersistence().findByPatcherFixId(patcherFixId);
	}

	/**
	 * Returns a range of all the patcher builds where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByPatcherFixId(
		long patcherFixId, int start, int end) {

		return getPersistence().findByPatcherFixId(patcherFixId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByPatcherFixId(
		long patcherFixId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByPatcherFixId(
			patcherFixId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByPatcherFixId(
		long patcherFixId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPatcherFixId(
			patcherFixId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByPatcherFixId_First(
			long patcherFixId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByPatcherFixId_First(
			patcherFixId, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByPatcherFixId_First(
		long patcherFixId, OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByPatcherFixId_First(
			patcherFixId, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByPatcherFixId_Last(
			long patcherFixId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByPatcherFixId_Last(
			patcherFixId, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByPatcherFixId_Last(
		long patcherFixId, OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByPatcherFixId_Last(
			patcherFixId, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherFixId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByPatcherFixId_PrevAndNext(
			long patcherBuildId, long patcherFixId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByPatcherFixId_PrevAndNext(
			patcherBuildId, patcherFixId, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByPatcherFixId(
		long patcherFixId) {

		return getPersistence().filterFindByPatcherFixId(patcherFixId);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByPatcherFixId(
		long patcherFixId, int start, int end) {

		return getPersistence().filterFindByPatcherFixId(
			patcherFixId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherFixId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByPatcherFixId(
		long patcherFixId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByPatcherFixId(
			patcherFixId, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByPatcherFixId_PrevAndNext(
			long patcherBuildId, long patcherFixId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByPatcherFixId_PrevAndNext(
			patcherBuildId, patcherFixId, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where patcherFixId = &#63; from the database.
	 *
	 * @param patcherFixId the patcher fix ID
	 */
	public static void removeByPatcherFixId(long patcherFixId) {
		getPersistence().removeByPatcherFixId(patcherFixId);
	}

	/**
	 * Returns the number of patcher builds where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the number of matching patcher builds
	 */
	public static int countByPatcherFixId(long patcherFixId) {
		return getPersistence().countByPatcherFixId(patcherFixId);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherFixId = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByPatcherFixId(long patcherFixId) {
		return getPersistence().filterCountByPatcherFixId(patcherFixId);
	}

	/**
	 * Returns all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return getPersistence().findByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	/**
	 * Returns a range of all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end) {

		return getPersistence().findByPatcherProjectVersionId(
			patcherProjectVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByPatcherProjectVersionId(
			patcherProjectVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPatcherProjectVersionId(
			patcherProjectVersionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByPatcherProjectVersionId_First(
			long patcherProjectVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByPatcherProjectVersionId_First(
			patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByPatcherProjectVersionId_First(
		long patcherProjectVersionId,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByPatcherProjectVersionId_First(
			patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByPatcherProjectVersionId_Last(
			long patcherProjectVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByPatcherProjectVersionId_Last(
			patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByPatcherProjectVersionId_Last(
		long patcherProjectVersionId,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByPatcherProjectVersionId_Last(
			patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByPatcherProjectVersionId_PrevAndNext(
			long patcherBuildId, long patcherProjectVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByPatcherProjectVersionId_PrevAndNext(
			patcherBuildId, patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return getPersistence().filterFindByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end) {

		return getPersistence().filterFindByPatcherProjectVersionId(
			patcherProjectVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByPatcherProjectVersionId(
		long patcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByPatcherProjectVersionId(
			patcherProjectVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[]
			filterFindByPatcherProjectVersionId_PrevAndNext(
				long patcherBuildId, long patcherProjectVersionId,
				OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByPatcherProjectVersionId_PrevAndNext(
			patcherBuildId, patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where patcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 */
	public static void removeByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		getPersistence().removeByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	/**
	 * Returns the number of patcher builds where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher builds
	 */
	public static int countByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return getPersistence().countByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return getPersistence().filterCountByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	/**
	 * Returns all the patcher builds where key = &#63;.
	 *
	 * @param key the key
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByKey(String key) {
		return getPersistence().findByKey(key);
	}

	/**
	 * Returns a range of all the patcher builds where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByKey(String key, int start, int end) {
		return getPersistence().findByKey(key, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByKey(
		String key, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByKey(key, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByKey(
		String key, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByKey(
			key, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByKey_First(
			String key, OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByKey_First(key, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByKey_First(
		String key, OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByKey_First(key, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByKey_Last(
			String key, OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByKey_Last(key, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByKey_Last(
		String key, OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByKey_Last(key, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where key = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByKey_PrevAndNext(
			long patcherBuildId, String key,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByKey_PrevAndNext(
			patcherBuildId, key, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63;.
	 *
	 * @param key the key
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByKey(String key) {
		return getPersistence().filterFindByKey(key);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByKey(
		String key, int start, int end) {

		return getPersistence().filterFindByKey(key, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByKey(
		String key, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByKey(
			key, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where key = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByKey_PrevAndNext(
			long patcherBuildId, String key,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByKey_PrevAndNext(
			patcherBuildId, key, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where key = &#63; from the database.
	 *
	 * @param key the key
	 */
	public static void removeByKey(String key) {
		getPersistence().removeByKey(key);
	}

	/**
	 * Returns the number of patcher builds where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching patcher builds
	 */
	public static int countByKey(String key) {
		return getPersistence().countByKey(key);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByKey(String key) {
		return getPersistence().filterCountByKey(key);
	}

	/**
	 * Returns all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId) {

		return getPersistence().findByP_P(
			patcherAccountId, patcherProductVersionId);
	}

	/**
	 * Returns a range of all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId, int start,
		int end) {

		return getPersistence().findByP_P(
			patcherAccountId, patcherProductVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByP_P(
			patcherAccountId, patcherProductVersionId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByP_P(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_P(
			patcherAccountId, patcherProductVersionId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByP_P_First(
			long patcherAccountId, long patcherProductVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByP_P_First(
			patcherAccountId, patcherProductVersionId, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByP_P_First(
		long patcherAccountId, long patcherProductVersionId,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByP_P_First(
			patcherAccountId, patcherProductVersionId, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByP_P_Last(
			long patcherAccountId, long patcherProductVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByP_P_Last(
			patcherAccountId, patcherProductVersionId, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByP_P_Last(
		long patcherAccountId, long patcherProductVersionId,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByP_P_Last(
			patcherAccountId, patcherProductVersionId, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByP_P_PrevAndNext(
			long patcherBuildId, long patcherAccountId,
			long patcherProductVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByP_P_PrevAndNext(
			patcherBuildId, patcherAccountId, patcherProductVersionId,
			orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByP_P(
		long patcherAccountId, long patcherProductVersionId) {

		return getPersistence().filterFindByP_P(
			patcherAccountId, patcherProductVersionId);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByP_P(
		long patcherAccountId, long patcherProductVersionId, int start,
		int end) {

		return getPersistence().filterFindByP_P(
			patcherAccountId, patcherProductVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByP_P(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByP_P(
			patcherAccountId, patcherProductVersionId, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByP_P_PrevAndNext(
			long patcherBuildId, long patcherAccountId,
			long patcherProductVersionId,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByP_P_PrevAndNext(
			patcherBuildId, patcherAccountId, patcherProductVersionId,
			orderByComparator);
	}

	/**
	 * Removes all the patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63; from the database.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 */
	public static void removeByP_P(
		long patcherAccountId, long patcherProductVersionId) {

		getPersistence().removeByP_P(patcherAccountId, patcherProductVersionId);
	}

	/**
	 * Returns the number of patcher builds where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher builds
	 */
	public static int countByP_P(
		long patcherAccountId, long patcherProductVersionId) {

		return getPersistence().countByP_P(
			patcherAccountId, patcherProductVersionId);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherAccountId = &#63; and patcherProductVersionId = &#63;.
	 *
	 * @param patcherAccountId the patcher account ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByP_P(
		long patcherAccountId, long patcherProductVersionId) {

		return getPersistence().filterCountByP_P(
			patcherAccountId, patcherProductVersionId);
	}

	/**
	 * Returns all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByP_C(
		long patcherFixId, boolean childBuild) {

		return getPersistence().findByP_C(patcherFixId, childBuild);
	}

	/**
	 * Returns a range of all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByP_C(
		long patcherFixId, boolean childBuild, int start, int end) {

		return getPersistence().findByP_C(patcherFixId, childBuild, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByP_C(
		long patcherFixId, boolean childBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByP_C(
			patcherFixId, childBuild, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByP_C(
		long patcherFixId, boolean childBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_C(
			patcherFixId, childBuild, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByP_C_First(
			long patcherFixId, boolean childBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByP_C_First(
			patcherFixId, childBuild, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByP_C_First(
		long patcherFixId, boolean childBuild,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByP_C_First(
			patcherFixId, childBuild, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByP_C_Last(
			long patcherFixId, boolean childBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByP_C_Last(
			patcherFixId, childBuild, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByP_C_Last(
		long patcherFixId, boolean childBuild,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByP_C_Last(
			patcherFixId, childBuild, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByP_C_PrevAndNext(
			long patcherBuildId, long patcherFixId, boolean childBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByP_C_PrevAndNext(
			patcherBuildId, patcherFixId, childBuild, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByP_C(
		long patcherFixId, boolean childBuild) {

		return getPersistence().filterFindByP_C(patcherFixId, childBuild);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByP_C(
		long patcherFixId, boolean childBuild, int start, int end) {

		return getPersistence().filterFindByP_C(
			patcherFixId, childBuild, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByP_C(
		long patcherFixId, boolean childBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByP_C(
			patcherFixId, childBuild, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByP_C_PrevAndNext(
			long patcherBuildId, long patcherFixId, boolean childBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByP_C_PrevAndNext(
			patcherBuildId, patcherFixId, childBuild, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where patcherFixId = &#63; and childBuild = &#63; from the database.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 */
	public static void removeByP_C(long patcherFixId, boolean childBuild) {
		getPersistence().removeByP_C(patcherFixId, childBuild);
	}

	/**
	 * Returns the number of patcher builds where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the number of matching patcher builds
	 */
	public static int countByP_C(long patcherFixId, boolean childBuild) {
		return getPersistence().countByP_C(patcherFixId, childBuild);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherFixId = &#63; and childBuild = &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param childBuild the child build
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByP_C(long patcherFixId, boolean childBuild) {
		return getPersistence().filterCountByP_C(patcherFixId, childBuild);
	}

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or throws a <code>NoSuchPatcherBuildException</code> if it could not be found.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByK_KV(String key, double keyVersion)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByK_KV(key, keyVersion);
	}

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByK_KV(String key, double keyVersion) {
		return getPersistence().fetchByK_KV(key, keyVersion);
	}

	/**
	 * Returns the patcher build where key = &#63; and keyVersion = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByK_KV(
		String key, double keyVersion, boolean useFinderCache) {

		return getPersistence().fetchByK_KV(key, keyVersion, useFinderCache);
	}

	/**
	 * Removes the patcher build where key = &#63; and keyVersion = &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the patcher build that was removed
	 */
	public static PatcherBuild removeByK_KV(String key, double keyVersion)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().removeByK_KV(key, keyVersion);
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion = &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	public static int countByK_KV(String key, double keyVersion) {
		return getPersistence().countByK_KV(key, keyVersion);
	}

	/**
	 * Returns all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion) {

		return getPersistence().findByK_GtKV(key, keyVersion);
	}

	/**
	 * Returns a range of all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion, int start, int end) {

		return getPersistence().findByK_GtKV(key, keyVersion, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByK_GtKV(
			key, keyVersion, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByK_GtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByK_GtKV(
			key, keyVersion, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByK_GtKV_First(
			String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByK_GtKV_First(
			key, keyVersion, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByK_GtKV_First(
		String key, double keyVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByK_GtKV_First(
			key, keyVersion, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByK_GtKV_Last(
			String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByK_GtKV_Last(
			key, keyVersion, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByK_GtKV_Last(
		String key, double keyVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByK_GtKV_Last(
			key, keyVersion, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByK_GtKV_PrevAndNext(
			long patcherBuildId, String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByK_GtKV_PrevAndNext(
			patcherBuildId, key, keyVersion, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByK_GtKV(
		String key, double keyVersion) {

		return getPersistence().filterFindByK_GtKV(key, keyVersion);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByK_GtKV(
		String key, double keyVersion, int start, int end) {

		return getPersistence().filterFindByK_GtKV(key, keyVersion, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByK_GtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByK_GtKV(
			key, keyVersion, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByK_GtKV_PrevAndNext(
			long patcherBuildId, String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByK_GtKV_PrevAndNext(
			patcherBuildId, key, keyVersion, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where key = &#63; and keyVersion &gt; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 */
	public static void removeByK_GtKV(String key, double keyVersion) {
		getPersistence().removeByK_GtKV(key, keyVersion);
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	public static int countByK_GtKV(String key, double keyVersion) {
		return getPersistence().countByK_GtKV(key, keyVersion);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where key = &#63; and keyVersion &gt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByK_GtKV(String key, double keyVersion) {
		return getPersistence().filterCountByK_GtKV(key, keyVersion);
	}

	/**
	 * Returns all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion) {

		return getPersistence().findByK_LtKV(key, keyVersion);
	}

	/**
	 * Returns a range of all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion, int start, int end) {

		return getPersistence().findByK_LtKV(key, keyVersion, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByK_LtKV(
			key, keyVersion, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByK_LtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByK_LtKV(
			key, keyVersion, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByK_LtKV_First(
			String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByK_LtKV_First(
			key, keyVersion, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByK_LtKV_First(
		String key, double keyVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByK_LtKV_First(
			key, keyVersion, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByK_LtKV_Last(
			String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByK_LtKV_Last(
			key, keyVersion, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByK_LtKV_Last(
		String key, double keyVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByK_LtKV_Last(
			key, keyVersion, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByK_LtKV_PrevAndNext(
			long patcherBuildId, String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByK_LtKV_PrevAndNext(
			patcherBuildId, key, keyVersion, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByK_LtKV(
		String key, double keyVersion) {

		return getPersistence().filterFindByK_LtKV(key, keyVersion);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByK_LtKV(
		String key, double keyVersion, int start, int end) {

		return getPersistence().filterFindByK_LtKV(key, keyVersion, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByK_LtKV(
		String key, double keyVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByK_LtKV(
			key, keyVersion, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param keyVersion the key version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByK_LtKV_PrevAndNext(
			long patcherBuildId, String key, double keyVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByK_LtKV_PrevAndNext(
			patcherBuildId, key, keyVersion, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where key = &#63; and keyVersion &lt; &#63; from the database.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 */
	public static void removeByK_LtKV(String key, double keyVersion) {
		getPersistence().removeByK_LtKV(key, keyVersion);
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds
	 */
	public static int countByK_LtKV(String key, double keyVersion) {
		return getPersistence().countByK_LtKV(key, keyVersion);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where key = &#63; and keyVersion &lt; &#63;.
	 *
	 * @param key the key
	 * @param keyVersion the key version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByK_LtKV(String key, double keyVersion) {
		return getPersistence().filterCountByK_LtKV(key, keyVersion);
	}

	/**
	 * Returns all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByK_L(
		String key, boolean latestKeyBuild) {

		return getPersistence().findByK_L(key, latestKeyBuild);
	}

	/**
	 * Returns a range of all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByK_L(
		String key, boolean latestKeyBuild, int start, int end) {

		return getPersistence().findByK_L(key, latestKeyBuild, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByK_L(
		String key, boolean latestKeyBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByK_L(
			key, latestKeyBuild, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByK_L(
		String key, boolean latestKeyBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByK_L(
			key, latestKeyBuild, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByK_L_First(
			String key, boolean latestKeyBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByK_L_First(
			key, latestKeyBuild, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByK_L_First(
		String key, boolean latestKeyBuild,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByK_L_First(
			key, latestKeyBuild, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByK_L_Last(
			String key, boolean latestKeyBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByK_L_Last(
			key, latestKeyBuild, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByK_L_Last(
		String key, boolean latestKeyBuild,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByK_L_Last(
			key, latestKeyBuild, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByK_L_PrevAndNext(
			long patcherBuildId, String key, boolean latestKeyBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByK_L_PrevAndNext(
			patcherBuildId, key, latestKeyBuild, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByK_L(
		String key, boolean latestKeyBuild) {

		return getPersistence().filterFindByK_L(key, latestKeyBuild);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByK_L(
		String key, boolean latestKeyBuild, int start, int end) {

		return getPersistence().filterFindByK_L(
			key, latestKeyBuild, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByK_L(
		String key, boolean latestKeyBuild, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByK_L(
			key, latestKeyBuild, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByK_L_PrevAndNext(
			long patcherBuildId, String key, boolean latestKeyBuild,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByK_L_PrevAndNext(
			patcherBuildId, key, latestKeyBuild, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where key = &#63; and latestKeyBuild = &#63; from the database.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 */
	public static void removeByK_L(String key, boolean latestKeyBuild) {
		getPersistence().removeByK_L(key, latestKeyBuild);
	}

	/**
	 * Returns the number of patcher builds where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the number of matching patcher builds
	 */
	public static int countByK_L(String key, boolean latestKeyBuild) {
		return getPersistence().countByK_L(key, latestKeyBuild);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where key = &#63; and latestKeyBuild = &#63;.
	 *
	 * @param key the key
	 * @param latestKeyBuild the latest key build
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByK_L(String key, boolean latestKeyBuild) {
		return getPersistence().filterCountByK_L(key, latestKeyBuild);
	}

	/**
	 * Returns all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		return getPersistence().findByL_S(
			latestSupportTicketBuild, supportTicket);
	}

	/**
	 * Returns a range of all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end) {

		return getPersistence().findByL_S(
			latestSupportTicketBuild, supportTicket, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end, OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByL_S(
			latestSupportTicketBuild, supportTicket, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end, OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByL_S(
			latestSupportTicketBuild, supportTicket, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByL_S_First(
			boolean latestSupportTicketBuild, String supportTicket,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByL_S_First(
			latestSupportTicketBuild, supportTicket, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByL_S_First(
		boolean latestSupportTicketBuild, String supportTicket,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByL_S_First(
			latestSupportTicketBuild, supportTicket, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByL_S_Last(
			boolean latestSupportTicketBuild, String supportTicket,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByL_S_Last(
			latestSupportTicketBuild, supportTicket, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByL_S_Last(
		boolean latestSupportTicketBuild, String supportTicket,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByL_S_Last(
			latestSupportTicketBuild, supportTicket, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByL_S_PrevAndNext(
			long patcherBuildId, boolean latestSupportTicketBuild,
			String supportTicket,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByL_S_PrevAndNext(
			patcherBuildId, latestSupportTicketBuild, supportTicket,
			orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		return getPersistence().filterFindByL_S(
			latestSupportTicketBuild, supportTicket);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end) {

		return getPersistence().filterFindByL_S(
			latestSupportTicketBuild, supportTicket, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByL_S(
		boolean latestSupportTicketBuild, String supportTicket, int start,
		int end, OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByL_S(
			latestSupportTicketBuild, supportTicket, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByL_S_PrevAndNext(
			long patcherBuildId, boolean latestSupportTicketBuild,
			String supportTicket,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByL_S_PrevAndNext(
			patcherBuildId, latestSupportTicketBuild, supportTicket,
			orderByComparator);
	}

	/**
	 * Removes all the patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63; from the database.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 */
	public static void removeByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		getPersistence().removeByL_S(latestSupportTicketBuild, supportTicket);
	}

	/**
	 * Returns the number of patcher builds where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the number of matching patcher builds
	 */
	public static int countByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		return getPersistence().countByL_S(
			latestSupportTicketBuild, supportTicket);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where latestSupportTicketBuild = &#63; and supportTicket = &#63;.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build
	 * @param supportTicket the support ticket
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByL_S(
		boolean latestSupportTicketBuild, String supportTicket) {

		return getPersistence().filterCountByL_S(
			latestSupportTicketBuild, supportTicket);
	}

	/**
	 * Returns all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion) {

		return getPersistence().findByS_GtS(
			supportTicket, supportTicketVersion);
	}

	/**
	 * Returns a range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end) {

		return getPersistence().findByS_GtS(
			supportTicket, supportTicketVersion, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByS_GtS(
			supportTicket, supportTicketVersion, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByS_GtS(
			supportTicket, supportTicketVersion, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByS_GtS_First(
			String supportTicket, double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByS_GtS_First(
			supportTicket, supportTicketVersion, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByS_GtS_First(
		String supportTicket, double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByS_GtS_First(
			supportTicket, supportTicketVersion, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByS_GtS_Last(
			String supportTicket, double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByS_GtS_Last(
			supportTicket, supportTicketVersion, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByS_GtS_Last(
		String supportTicket, double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByS_GtS_Last(
			supportTicket, supportTicketVersion, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByS_GtS_PrevAndNext(
			long patcherBuildId, String supportTicket,
			double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByS_GtS_PrevAndNext(
			patcherBuildId, supportTicket, supportTicketVersion,
			orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByS_GtS(
		String supportTicket, double supportTicketVersion) {

		return getPersistence().filterFindByS_GtS(
			supportTicket, supportTicketVersion);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end) {

		return getPersistence().filterFindByS_GtS(
			supportTicket, supportTicketVersion, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByS_GtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByS_GtS(
			supportTicket, supportTicketVersion, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByS_GtS_PrevAndNext(
			long patcherBuildId, String supportTicket,
			double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByS_GtS_PrevAndNext(
			patcherBuildId, supportTicket, supportTicketVersion,
			orderByComparator);
	}

	/**
	 * Removes all the patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63; from the database.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 */
	public static void removeByS_GtS(
		String supportTicket, double supportTicketVersion) {

		getPersistence().removeByS_GtS(supportTicket, supportTicketVersion);
	}

	/**
	 * Returns the number of patcher builds where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds
	 */
	public static int countByS_GtS(
		String supportTicket, double supportTicketVersion) {

		return getPersistence().countByS_GtS(
			supportTicket, supportTicketVersion);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &gt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByS_GtS(
		String supportTicket, double supportTicketVersion) {

		return getPersistence().filterCountByS_GtS(
			supportTicket, supportTicketVersion);
	}

	/**
	 * Returns all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion) {

		return getPersistence().findByS_LtS(
			supportTicket, supportTicketVersion);
	}

	/**
	 * Returns a range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end) {

		return getPersistence().findByS_LtS(
			supportTicket, supportTicketVersion, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByS_LtS(
			supportTicket, supportTicketVersion, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByS_LtS(
			supportTicket, supportTicketVersion, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByS_LtS_First(
			String supportTicket, double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByS_LtS_First(
			supportTicket, supportTicketVersion, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByS_LtS_First(
		String supportTicket, double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByS_LtS_First(
			supportTicket, supportTicketVersion, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByS_LtS_Last(
			String supportTicket, double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByS_LtS_Last(
			supportTicket, supportTicketVersion, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByS_LtS_Last(
		String supportTicket, double supportTicketVersion,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByS_LtS_Last(
			supportTicket, supportTicketVersion, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByS_LtS_PrevAndNext(
			long patcherBuildId, String supportTicket,
			double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByS_LtS_PrevAndNext(
			patcherBuildId, supportTicket, supportTicketVersion,
			orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByS_LtS(
		String supportTicket, double supportTicketVersion) {

		return getPersistence().filterFindByS_LtS(
			supportTicket, supportTicketVersion);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end) {

		return getPersistence().filterFindByS_LtS(
			supportTicket, supportTicketVersion, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByS_LtS(
		String supportTicket, double supportTicketVersion, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByS_LtS(
			supportTicket, supportTicketVersion, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByS_LtS_PrevAndNext(
			long patcherBuildId, String supportTicket,
			double supportTicketVersion,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByS_LtS_PrevAndNext(
			patcherBuildId, supportTicket, supportTicketVersion,
			orderByComparator);
	}

	/**
	 * Removes all the patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63; from the database.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 */
	public static void removeByS_LtS(
		String supportTicket, double supportTicketVersion) {

		getPersistence().removeByS_LtS(supportTicket, supportTicketVersion);
	}

	/**
	 * Returns the number of patcher builds where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds
	 */
	public static int countByS_LtS(
		String supportTicket, double supportTicketVersion) {

		return getPersistence().countByS_LtS(
			supportTicket, supportTicketVersion);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where supportTicket = &#63; and supportTicketVersion &lt; &#63;.
	 *
	 * @param supportTicket the support ticket
	 * @param supportTicketVersion the support ticket version
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByS_LtS(
		String supportTicket, double supportTicketVersion) {

		return getPersistence().filterCountByS_LtS(
			supportTicket, supportTicketVersion);
	}

	/**
	 * Returns all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		return getPersistence().findByLtM_N_S(modifiedDate, notified, status);
	}

	/**
	 * Returns a range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end) {

		return getPersistence().findByLtM_N_S(
			modifiedDate, notified, status, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByLtM_N_S(
			modifiedDate, notified, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtM_N_S(
			modifiedDate, notified, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByLtM_N_S_First(
			Date modifiedDate, boolean notified, int status,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByLtM_N_S_First(
			modifiedDate, notified, status, orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByLtM_N_S_First(
		Date modifiedDate, boolean notified, int status,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByLtM_N_S_First(
			modifiedDate, notified, status, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByLtM_N_S_Last(
			Date modifiedDate, boolean notified, int status,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByLtM_N_S_Last(
			modifiedDate, notified, status, orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByLtM_N_S_Last(
		Date modifiedDate, boolean notified, int status,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByLtM_N_S_Last(
			modifiedDate, notified, status, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByLtM_N_S_PrevAndNext(
			long patcherBuildId, Date modifiedDate, boolean notified,
			int status, OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByLtM_N_S_PrevAndNext(
			patcherBuildId, modifiedDate, notified, status, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		return getPersistence().filterFindByLtM_N_S(
			modifiedDate, notified, status);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end) {

		return getPersistence().filterFindByLtM_N_S(
			modifiedDate, notified, status, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int status, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByLtM_N_S(
			modifiedDate, notified, status, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByLtM_N_S_PrevAndNext(
			long patcherBuildId, Date modifiedDate, boolean notified,
			int status, OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByLtM_N_S_PrevAndNext(
			patcherBuildId, modifiedDate, notified, status, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses) {

		return getPersistence().filterFindByLtM_N_S(
			modifiedDate, notified, statuses);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start,
		int end) {

		return getPersistence().filterFindByLtM_N_S(
			modifiedDate, notified, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByLtM_N_S(
			modifiedDate, notified, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses) {

		return getPersistence().findByLtM_N_S(modifiedDate, notified, statuses);
	}

	/**
	 * Returns a range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start,
		int end) {

		return getPersistence().findByLtM_N_S(
			modifiedDate, notified, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByLtM_N_S(
			modifiedDate, notified, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtM_N_S(
			modifiedDate, notified, statuses, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63; from the database.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 */
	public static void removeByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		getPersistence().removeByLtM_N_S(modifiedDate, notified, status);
	}

	/**
	 * Returns the number of patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the number of matching patcher builds
	 */
	public static int countByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		return getPersistence().countByLtM_N_S(modifiedDate, notified, status);
	}

	/**
	 * Returns the number of patcher builds where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the number of matching patcher builds
	 */
	public static int countByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses) {

		return getPersistence().countByLtM_N_S(
			modifiedDate, notified, statuses);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param status the status
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByLtM_N_S(
		Date modifiedDate, boolean notified, int status) {

		return getPersistence().filterCountByLtM_N_S(
			modifiedDate, notified, status);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where modifiedDate &lt; &#63; and notified = &#63; and status = any &#63;.
	 *
	 * @param modifiedDate the modified date
	 * @param notified the notified
	 * @param statuses the statuses
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByLtM_N_S(
		Date modifiedDate, boolean notified, int[] statuses) {

		return getPersistence().filterCountByLtM_N_S(
			modifiedDate, notified, statuses);
	}

	/**
	 * Returns all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		return getPersistence().findByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type);
	}

	/**
	 * Returns a range of all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end) {

		return getPersistence().findByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, start,
			end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByP_NotP_C_NotT_First(
			long patcherFixId, long patcherProductVersionId, boolean childBuild,
			int type, OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByP_NotP_C_NotT_First(
			patcherFixId, patcherProductVersionId, childBuild, type,
			orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByP_NotP_C_NotT_First(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByP_NotP_C_NotT_First(
			patcherFixId, patcherProductVersionId, childBuild, type,
			orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByP_NotP_C_NotT_Last(
			long patcherFixId, long patcherProductVersionId, boolean childBuild,
			int type, OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByP_NotP_C_NotT_Last(
			patcherFixId, patcherProductVersionId, childBuild, type,
			orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByP_NotP_C_NotT_Last(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByP_NotP_C_NotT_Last(
			patcherFixId, patcherProductVersionId, childBuild, type,
			orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByP_NotP_C_NotT_PrevAndNext(
			long patcherBuildId, long patcherFixId,
			long patcherProductVersionId, boolean childBuild, int type,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByP_NotP_C_NotT_PrevAndNext(
			patcherBuildId, patcherFixId, patcherProductVersionId, childBuild,
			type, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		return getPersistence().filterFindByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end) {

		return getPersistence().filterFindByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, start,
			end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByP_NotP_C_NotT_PrevAndNext(
			long patcherBuildId, long patcherFixId,
			long patcherProductVersionId, boolean childBuild, int type,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByP_NotP_C_NotT_PrevAndNext(
			patcherBuildId, patcherFixId, patcherProductVersionId, childBuild,
			type, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63; from the database.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 */
	public static void removeByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		getPersistence().removeByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type);
	}

	/**
	 * Returns the number of patcher builds where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the number of matching patcher builds
	 */
	public static int countByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		return getPersistence().countByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherFixId = &#63; and patcherProductVersionId &ne; &#63; and childBuild = &#63; and type &ne; &#63;.
	 *
	 * @param patcherFixId the patcher fix ID
	 * @param patcherProductVersionId the patcher product version ID
	 * @param childBuild the child build
	 * @param type the type
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByP_NotP_C_NotT(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		return getPersistence().filterCountByP_NotP_C_NotT(
			patcherFixId, patcherProductVersionId, childBuild, type);
	}

	/**
	 * Returns all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the matching patcher builds
	 */
	public static List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		return getPersistence().findByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name);
	}

	/**
	 * Returns a range of all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds
	 */
	public static List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end) {

		return getPersistence().findByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher builds
	 */
	public static List<PatcherBuild> findByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByP_N_L_A_First(
			long patcherProjectVersionId, String accountEntryCode,
			boolean latestKeyBuild, String name,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByP_N_L_A_First(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			orderByComparator);
	}

	/**
	 * Returns the first patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByP_N_L_A_First(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByP_N_L_A_First(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build
	 * @throws NoSuchPatcherBuildException if a matching patcher build could not be found
	 */
	public static PatcherBuild findByP_N_L_A_Last(
			long patcherProjectVersionId, String accountEntryCode,
			boolean latestKeyBuild, String name,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByP_N_L_A_Last(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			orderByComparator);
	}

	/**
	 * Returns the last patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher build, or <code>null</code> if a matching patcher build could not be found
	 */
	public static PatcherBuild fetchByP_N_L_A_Last(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().fetchByP_N_L_A_Last(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] findByP_N_L_A_PrevAndNext(
			long patcherBuildId, long patcherProjectVersionId,
			String accountEntryCode, boolean latestKeyBuild, String name,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByP_N_L_A_PrevAndNext(
			patcherBuildId, patcherProjectVersionId, accountEntryCode,
			latestKeyBuild, name, orderByComparator);
	}

	/**
	 * Returns all the patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		return getPersistence().filterFindByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name);
	}

	/**
	 * Returns a range of all the patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end) {

		return getPersistence().filterFindByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds that the user has permissions to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher builds that the user has permission to view
	 */
	public static List<PatcherBuild> filterFindByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().filterFindByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name,
			start, end, orderByComparator);
	}

	/**
	 * Returns the patcher builds before and after the current patcher build in the ordered set of patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherBuildId the primary key of the current patcher build
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild[] filterFindByP_N_L_A_PrevAndNext(
			long patcherBuildId, long patcherProjectVersionId,
			String accountEntryCode, boolean latestKeyBuild, String name,
			OrderByComparator<PatcherBuild> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().filterFindByP_N_L_A_PrevAndNext(
			patcherBuildId, patcherProjectVersionId, accountEntryCode,
			latestKeyBuild, name, orderByComparator);
	}

	/**
	 * Removes all the patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 */
	public static void removeByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		getPersistence().removeByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name);
	}

	/**
	 * Returns the number of patcher builds where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the number of matching patcher builds
	 */
	public static int countByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		return getPersistence().countByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name);
	}

	/**
	 * Returns the number of patcher builds that the user has permission to view where patcherProjectVersionId = &#63; and accountEntryCode = &#63; and latestKeyBuild = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param accountEntryCode the account entry code
	 * @param latestKeyBuild the latest key build
	 * @param name the name
	 * @return the number of matching patcher builds that the user has permission to view
	 */
	public static int filterCountByP_N_L_A(
		long patcherProjectVersionId, String accountEntryCode,
		boolean latestKeyBuild, String name) {

		return getPersistence().filterCountByP_N_L_A(
			patcherProjectVersionId, accountEntryCode, latestKeyBuild, name);
	}

	/**
	 * Caches the patcher build in the entity cache if it is enabled.
	 *
	 * @param patcherBuild the patcher build
	 */
	public static void cacheResult(PatcherBuild patcherBuild) {
		getPersistence().cacheResult(patcherBuild);
	}

	/**
	 * Caches the patcher builds in the entity cache if it is enabled.
	 *
	 * @param patcherBuilds the patcher builds
	 */
	public static void cacheResult(List<PatcherBuild> patcherBuilds) {
		getPersistence().cacheResult(patcherBuilds);
	}

	/**
	 * Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	 *
	 * @param patcherBuildId the primary key for the new patcher build
	 * @return the new patcher build
	 */
	public static PatcherBuild create(long patcherBuildId) {
		return getPersistence().create(patcherBuildId);
	}

	/**
	 * Removes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild remove(long patcherBuildId)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().remove(patcherBuildId);
	}

	public static PatcherBuild updateImpl(PatcherBuild patcherBuild) {
		return getPersistence().updateImpl(patcherBuild);
	}

	/**
	 * Returns the patcher build with the primary key or throws a <code>NoSuchPatcherBuildException</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build
	 * @throws NoSuchPatcherBuildException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild findByPrimaryKey(long patcherBuildId)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherBuildException {

		return getPersistence().findByPrimaryKey(patcherBuildId);
	}

	/**
	 * Returns the patcher build with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build, or <code>null</code> if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild fetchByPrimaryKey(long patcherBuildId) {
		return getPersistence().fetchByPrimaryKey(patcherBuildId);
	}

	/**
	 * Returns all the patcher builds.
	 *
	 * @return the patcher builds
	 */
	public static List<PatcherBuild> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher builds
	 */
	public static List<PatcherBuild> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds
	 */
	public static List<PatcherBuild> findAll(
		int start, int end, OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher builds
	 */
	public static List<PatcherBuild> findAll(
		int start, int end, OrderByComparator<PatcherBuild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher builds from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of patcher builds.
	 *
	 * @return the number of patcher builds
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	/**
	 * Returns the primaryKeys of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return long[] of the primaryKeys of patcher accounts associated with the patcher build
	 */
	public static long[] getPatcherAccountPrimaryKeys(long pk) {
		return getPersistence().getPatcherAccountPrimaryKeys(pk);
	}

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return the patcher builds associated with the patcher account
	 */
	public static List<PatcherBuild> getPatcherAccountPatcherBuilds(long pk) {
		return getPersistence().getPatcherAccountPatcherBuilds(pk);
	}

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher builds associated with the patcher account
	 */
	public static List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long pk, int start, int end) {

		return getPersistence().getPatcherAccountPatcherBuilds(pk, start, end);
	}

	/**
	 * Returns all the patcher build associated with the patcher account.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher account
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher account
	 */
	public static List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long pk, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().getPatcherAccountPatcherBuilds(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher accounts associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher accounts associated with the patcher build
	 */
	public static int getPatcherAccountsSize(long pk) {
		return getPersistence().getPatcherAccountsSize(pk);
	}

	/**
	 * Returns <code>true</code> if the patcher account is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if the patcher account is associated with the patcher build; <code>false</code> otherwise
	 */
	public static boolean containsPatcherAccount(
		long pk, long patcherAccountPK) {

		return getPersistence().containsPatcherAccount(pk, patcherAccountPK);
	}

	/**
	 * Returns <code>true</code> if the patcher build has any patcher accounts associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher accounts
	 * @return <code>true</code> if the patcher build has any patcher accounts associated with it; <code>false</code> otherwise
	 */
	public static boolean containsPatcherAccounts(long pk) {
		return getPersistence().containsPatcherAccounts(pk);
	}

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 * @return <code>true</code> if an association between the patcher build and the patcher account was added; <code>false</code> if they were already associated
	 */
	public static boolean addPatcherAccount(long pk, long patcherAccountPK) {
		return getPersistence().addPatcherAccount(pk, patcherAccountPK);
	}

	/**
	 * Adds an association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 * @return <code>true</code> if an association between the patcher build and the patcher account was added; <code>false</code> if they were already associated
	 */
	public static boolean addPatcherAccount(
		long pk, com.liferay.osb.patcher.model.PatcherAccount patcherAccount) {

		return getPersistence().addPatcherAccount(pk, patcherAccount);
	}

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 * @return <code>true</code> if at least one association between the patcher build and the patcher accounts was added; <code>false</code> if they were all already associated
	 */
	public static boolean addPatcherAccounts(
		long pk, long[] patcherAccountPKs) {

		return getPersistence().addPatcherAccounts(pk, patcherAccountPKs);
	}

	/**
	 * Adds an association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 * @return <code>true</code> if at least one association between the patcher build and the patcher accounts was added; <code>false</code> if they were all already associated
	 */
	public static boolean addPatcherAccounts(
		long pk,
		List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts) {

		return getPersistence().addPatcherAccounts(pk, patcherAccounts);
	}

	/**
	 * Clears all associations between the patcher build and its patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher accounts from
	 */
	public static void clearPatcherAccounts(long pk) {
		getPersistence().clearPatcherAccounts(pk);
	}

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPK the primary key of the patcher account
	 */
	public static void removePatcherAccount(long pk, long patcherAccountPK) {
		getPersistence().removePatcherAccount(pk, patcherAccountPK);
	}

	/**
	 * Removes the association between the patcher build and the patcher account. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccount the patcher account
	 */
	public static void removePatcherAccount(
		long pk, com.liferay.osb.patcher.model.PatcherAccount patcherAccount) {

		getPersistence().removePatcherAccount(pk, patcherAccount);
	}

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts
	 */
	public static void removePatcherAccounts(
		long pk, long[] patcherAccountPKs) {

		getPersistence().removePatcherAccounts(pk, patcherAccountPKs);
	}

	/**
	 * Removes the association between the patcher build and the patcher accounts. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts
	 */
	public static void removePatcherAccounts(
		long pk,
		List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts) {

		getPersistence().removePatcherAccounts(pk, patcherAccounts);
	}

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccountPKs the primary keys of the patcher accounts to be associated with the patcher build
	 */
	public static void setPatcherAccounts(long pk, long[] patcherAccountPKs) {
		getPersistence().setPatcherAccounts(pk, patcherAccountPKs);
	}

	/**
	 * Sets the patcher accounts associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherAccounts the patcher accounts to be associated with the patcher build
	 */
	public static void setPatcherAccounts(
		long pk,
		List<com.liferay.osb.patcher.model.PatcherAccount> patcherAccounts) {

		getPersistence().setPatcherAccounts(pk, patcherAccounts);
	}

	/**
	 * Returns the primaryKeys of patcher fixes associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return long[] of the primaryKeys of patcher fixes associated with the patcher build
	 */
	public static long[] getPatcherFixPrimaryKeys(long pk) {
		return getPersistence().getPatcherFixPrimaryKeys(pk);
	}

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher builds associated with the patcher fix
	 */
	public static List<PatcherBuild> getPatcherFixPatcherBuilds(long pk) {
		return getPersistence().getPatcherFixPatcherBuilds(pk);
	}

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher builds associated with the patcher fix
	 */
	public static List<PatcherBuild> getPatcherFixPatcherBuilds(
		long pk, int start, int end) {

		return getPersistence().getPatcherFixPatcherBuilds(pk, start, end);
	}

	/**
	 * Returns all the patcher build associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher builds associated with the patcher fix
	 */
	public static List<PatcherBuild> getPatcherFixPatcherBuilds(
		long pk, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getPersistence().getPatcherFixPatcherBuilds(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fixes associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the number of patcher fixes associated with the patcher build
	 */
	public static int getPatcherFixesSize(long pk) {
		return getPersistence().getPatcherFixesSize(pk);
	}

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher build; <code>false</code> otherwise
	 */
	public static boolean containsPatcherFix(long pk, long patcherFixPK) {
		return getPersistence().containsPatcherFix(pk, patcherFixPK);
	}

	/**
	 * Returns <code>true</code> if the patcher build has any patcher fixes associated with it.
	 *
	 * @param pk the primary key of the patcher build to check for associations with patcher fixes
	 * @return <code>true</code> if the patcher build has any patcher fixes associated with it; <code>false</code> otherwise
	 */
	public static boolean containsPatcherFixes(long pk) {
		return getPersistence().containsPatcherFixes(pk);
	}

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if an association between the patcher build and the patcher fix was added; <code>false</code> if they were already associated
	 */
	public static boolean addPatcherFix(long pk, long patcherFixPK) {
		return getPersistence().addPatcherFix(pk, patcherFixPK);
	}

	/**
	 * Adds an association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 * @return <code>true</code> if an association between the patcher build and the patcher fix was added; <code>false</code> if they were already associated
	 */
	public static boolean addPatcherFix(
		long pk, com.liferay.osb.patcher.model.PatcherFix patcherFix) {

		return getPersistence().addPatcherFix(pk, patcherFix);
	}

	/**
	 * Adds an association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher build and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	public static boolean addPatcherFixes(long pk, long[] patcherFixPKs) {
		return getPersistence().addPatcherFixes(pk, patcherFixPKs);
	}

	/**
	 * Adds an association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher build and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	public static boolean addPatcherFixes(
		long pk, List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes) {

		return getPersistence().addPatcherFixes(pk, patcherFixes);
	}

	/**
	 * Clears all associations between the patcher build and its patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build to clear the associated patcher fixes from
	 */
	public static void clearPatcherFixes(long pk) {
		getPersistence().clearPatcherFixes(pk);
	}

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPK the primary key of the patcher fix
	 */
	public static void removePatcherFix(long pk, long patcherFixPK) {
		getPersistence().removePatcherFix(pk, patcherFixPK);
	}

	/**
	 * Removes the association between the patcher build and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFix the patcher fix
	 */
	public static void removePatcherFix(
		long pk, com.liferay.osb.patcher.model.PatcherFix patcherFix) {

		getPersistence().removePatcherFix(pk, patcherFix);
	}

	/**
	 * Removes the association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 */
	public static void removePatcherFixes(long pk, long[] patcherFixPKs) {
		getPersistence().removePatcherFixes(pk, patcherFixPKs);
	}

	/**
	 * Removes the association between the patcher build and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes
	 */
	public static void removePatcherFixes(
		long pk, List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes) {

		getPersistence().removePatcherFixes(pk, patcherFixes);
	}

	/**
	 * Sets the patcher fixes associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixPKs the primary keys of the patcher fixes to be associated with the patcher build
	 */
	public static void setPatcherFixes(long pk, long[] patcherFixPKs) {
		getPersistence().setPatcherFixes(pk, patcherFixPKs);
	}

	/**
	 * Sets the patcher fixes associated with the patcher build, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher build
	 * @param patcherFixes the patcher fixes to be associated with the patcher build
	 */
	public static void setPatcherFixes(
		long pk, List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes) {

		getPersistence().setPatcherFixes(pk, patcherFixes);
	}

	public static PatcherBuildPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(PatcherBuildPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile PatcherBuildPersistence _persistence;

}