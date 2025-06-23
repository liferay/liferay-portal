/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the patcher fix pack service. This utility wraps <code>com.liferay.osb.patcher.service.persistence.impl.PatcherFixPackPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixPackPersistence
 * @generated
 */
public class PatcherFixPackUtil {

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
	public static void clearCache(PatcherFixPack patcherFixPack) {
		getPersistence().clearCache(patcherFixPack);
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
	public static Map<Serializable, PatcherFixPack> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PatcherFixPack> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherFixPack> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherFixPack> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PatcherFixPack update(PatcherFixPack patcherFixPack) {
		return getPersistence().update(patcherFixPack);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PatcherFixPack update(
		PatcherFixPack patcherFixPack, ServiceContext serviceContext) {

		return getPersistence().update(patcherFixPack, serviceContext);
	}

	/**
	 * Returns the patcher fix pack where patcherBuildId = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPatcherBuildId(long patcherBuildId)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPatcherBuildId(patcherBuildId);
	}

	/**
	 * Returns the patcher fix pack where patcherBuildId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPatcherBuildId(long patcherBuildId) {
		return getPersistence().fetchByPatcherBuildId(patcherBuildId);
	}

	/**
	 * Returns the patcher fix pack where patcherBuildId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPatcherBuildId(
		long patcherBuildId, boolean useFinderCache) {

		return getPersistence().fetchByPatcherBuildId(
			patcherBuildId, useFinderCache);
	}

	/**
	 * Removes the patcher fix pack where patcherBuildId = &#63; from the database.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the patcher fix pack that was removed
	 */
	public static PatcherFixPack removeByPatcherBuildId(long patcherBuildId)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().removeByPatcherBuildId(patcherBuildId);
	}

	/**
	 * Returns the number of patcher fix packs where patcherBuildId = &#63;.
	 *
	 * @param patcherBuildId the patcher build ID
	 * @return the number of matching patcher fix packs
	 */
	public static int countByPatcherBuildId(long patcherBuildId) {
		return getPersistence().countByPatcherBuildId(patcherBuildId);
	}

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId) {

		return getPersistence().findByPatcherFixComponentId(
			patcherFixComponentId);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end) {

		return getPersistence().findByPatcherFixComponentId(
			patcherFixComponentId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().findByPatcherFixComponentId(
			patcherFixComponentId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPatcherFixComponentId(
			patcherFixComponentId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPatcherFixComponentId_First(
			long patcherFixComponentId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPatcherFixComponentId_First(
			patcherFixComponentId, orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPatcherFixComponentId_First(
		long patcherFixComponentId,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByPatcherFixComponentId_First(
			patcherFixComponentId, orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPatcherFixComponentId_Last(
			long patcherFixComponentId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPatcherFixComponentId_Last(
			patcherFixComponentId, orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPatcherFixComponentId_Last(
		long patcherFixComponentId,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByPatcherFixComponentId_Last(
			patcherFixComponentId, orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] findByPatcherFixComponentId_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPatcherFixComponentId_PrevAndNext(
			patcherFixPackId, patcherFixComponentId, orderByComparator);
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPatcherFixComponentId(
		long patcherFixComponentId) {

		return getPersistence().filterFindByPatcherFixComponentId(
			patcherFixComponentId);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end) {

		return getPersistence().filterFindByPatcherFixComponentId(
			patcherFixComponentId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPatcherFixComponentId(
		long patcherFixComponentId, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().filterFindByPatcherFixComponentId(
			patcherFixComponentId, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[]
			filterFindByPatcherFixComponentId_PrevAndNext(
				long patcherFixPackId, long patcherFixComponentId,
				OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().filterFindByPatcherFixComponentId_PrevAndNext(
			patcherFixPackId, patcherFixComponentId, orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 */
	public static void removeByPatcherFixComponentId(
		long patcherFixComponentId) {

		getPersistence().removeByPatcherFixComponentId(patcherFixComponentId);
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the number of matching patcher fix packs
	 */
	public static int countByPatcherFixComponentId(long patcherFixComponentId) {
		return getPersistence().countByPatcherFixComponentId(
			patcherFixComponentId);
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public static int filterCountByPatcherFixComponentId(
		long patcherFixComponentId) {

		return getPersistence().filterCountByPatcherFixComponentId(
			patcherFixComponentId);
	}

	/**
	 * Returns all the patcher fix packs where version = &#63;.
	 *
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByVersion(int version) {
		return getPersistence().findByVersion(version);
	}

	/**
	 * Returns a range of all the patcher fix packs where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByVersion(
		int version, int start, int end) {

		return getPersistence().findByVersion(version, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByVersion(
		int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().findByVersion(
			version, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByVersion(
		int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByVersion(
			version, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByVersion_First(
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByVersion_First(version, orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByVersion_First(
		int version, OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByVersion_First(
			version, orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByVersion_Last(
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByVersion_Last(version, orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByVersion_Last(
		int version, OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByVersion_Last(version, orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where version = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] findByVersion_PrevAndNext(
			long patcherFixPackId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByVersion_PrevAndNext(
			patcherFixPackId, version, orderByComparator);
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByVersion(int version) {
		return getPersistence().filterFindByVersion(version);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByVersion(
		int version, int start, int end) {

		return getPersistence().filterFindByVersion(version, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByVersion(
		int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().filterFindByVersion(
			version, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] filterFindByVersion_PrevAndNext(
			long patcherFixPackId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().filterFindByVersion_PrevAndNext(
			patcherFixPackId, version, orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where version = &#63; from the database.
	 *
	 * @param version the version
	 */
	public static void removeByVersion(int version) {
		getPersistence().removeByVersion(version);
	}

	/**
	 * Returns the number of patcher fix packs where version = &#63;.
	 *
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	public static int countByVersion(int version) {
		return getPersistence().countByVersion(version);
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where version = &#63;.
	 *
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public static int filterCountByVersion(int version) {
		return getPersistence().filterCountByVersion(version);
	}

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		return getPersistence().findByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end) {

		return getPersistence().findByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end, OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().findByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end, OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPFCI_PPVI_First(
			long patcherFixComponentId, long patcherProjectVersionId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_PPVI_First(
			patcherFixComponentId, patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_PPVI_First(
		long patcherFixComponentId, long patcherProjectVersionId,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByPFCI_PPVI_First(
			patcherFixComponentId, patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPFCI_PPVI_Last(
			long patcherFixComponentId, long patcherProjectVersionId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_PPVI_Last(
			patcherFixComponentId, patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_PPVI_Last(
		long patcherFixComponentId, long patcherProjectVersionId,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByPFCI_PPVI_Last(
			patcherFixComponentId, patcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] findByPFCI_PPVI_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_PPVI_PrevAndNext(
			patcherFixPackId, patcherFixComponentId, patcherProjectVersionId,
			orderByComparator);
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		return getPersistence().filterFindByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end) {

		return getPersistence().filterFindByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId, int start,
		int end, OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().filterFindByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] filterFindByPFCI_PPVI_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().filterFindByPFCI_PPVI_PrevAndNext(
			patcherFixPackId, patcherFixComponentId, patcherProjectVersionId,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 */
	public static void removeByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		getPersistence().removeByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId);
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fix packs
	 */
	public static int countByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		return getPersistence().countByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId);
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public static int filterCountByPFCI_PPVI(
		long patcherFixComponentId, long patcherProjectVersionId) {

		return getPersistence().filterCountByPFCI_PPVI(
			patcherFixComponentId, patcherProjectVersionId);
	}

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version) {

		return getPersistence().findByPFCI_V(patcherFixComponentId, version);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version, int start, int end) {

		return getPersistence().findByPFCI_V(
			patcherFixComponentId, version, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().findByPFCI_V(
			patcherFixComponentId, version, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_V(
		long patcherFixComponentId, int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPFCI_V(
			patcherFixComponentId, version, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPFCI_V_First(
			long patcherFixComponentId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_V_First(
			patcherFixComponentId, version, orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_V_First(
		long patcherFixComponentId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByPFCI_V_First(
			patcherFixComponentId, version, orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPFCI_V_Last(
			long patcherFixComponentId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_V_Last(
			patcherFixComponentId, version, orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_V_Last(
		long patcherFixComponentId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByPFCI_V_Last(
			patcherFixComponentId, version, orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] findByPFCI_V_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_V_PrevAndNext(
			patcherFixPackId, patcherFixComponentId, version,
			orderByComparator);
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_V(
		long patcherFixComponentId, int version) {

		return getPersistence().filterFindByPFCI_V(
			patcherFixComponentId, version);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_V(
		long patcherFixComponentId, int version, int start, int end) {

		return getPersistence().filterFindByPFCI_V(
			patcherFixComponentId, version, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_V(
		long patcherFixComponentId, int version, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().filterFindByPFCI_V(
			patcherFixComponentId, version, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] filterFindByPFCI_V_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().filterFindByPFCI_V_PrevAndNext(
			patcherFixPackId, patcherFixComponentId, version,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and version = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 */
	public static void removeByPFCI_V(long patcherFixComponentId, int version) {
		getPersistence().removeByPFCI_V(patcherFixComponentId, version);
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	public static int countByPFCI_V(long patcherFixComponentId, int version) {
		return getPersistence().countByPFCI_V(patcherFixComponentId, version);
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public static int filterCountByPFCI_V(
		long patcherFixComponentId, int version) {

		return getPersistence().filterCountByPFCI_V(
			patcherFixComponentId, version);
	}

	/**
	 * Returns the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPFCI_N(
			long patcherProjectVersionId, String name)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_N(patcherProjectVersionId, name);
	}

	/**
	 * Returns the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_N(
		long patcherProjectVersionId, String name) {

		return getPersistence().fetchByPFCI_N(patcherProjectVersionId, name);
	}

	/**
	 * Returns the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_N(
		long patcherProjectVersionId, String name, boolean useFinderCache) {

		return getPersistence().fetchByPFCI_N(
			patcherProjectVersionId, name, useFinderCache);
	}

	/**
	 * Removes the patcher fix pack where patcherProjectVersionId = &#63; and name = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the patcher fix pack that was removed
	 */
	public static PatcherFixPack removeByPFCI_N(
			long patcherProjectVersionId, String name)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().removeByPFCI_N(patcherProjectVersionId, name);
	}

	/**
	 * Returns the number of patcher fix packs where patcherProjectVersionId = &#63; and name = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @return the number of matching patcher fix packs
	 */
	public static int countByPFCI_N(long patcherProjectVersionId, String name) {
		return getPersistence().countByPFCI_N(patcherProjectVersionId, name);
	}

	/**
	 * Returns all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status) {

		return getPersistence().findByPFCI_S(patcherProjectVersionId, status);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end) {

		return getPersistence().findByPFCI_S(
			patcherProjectVersionId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().findByPFCI_S(
			patcherProjectVersionId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPFCI_S(
			patcherProjectVersionId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPFCI_S_First(
			long patcherProjectVersionId, int status,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_S_First(
			patcherProjectVersionId, status, orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_S_First(
		long patcherProjectVersionId, int status,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByPFCI_S_First(
			patcherProjectVersionId, status, orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPFCI_S_Last(
			long patcherProjectVersionId, int status,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_S_Last(
			patcherProjectVersionId, status, orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_S_Last(
		long patcherProjectVersionId, int status,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByPFCI_S_Last(
			patcherProjectVersionId, status, orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] findByPFCI_S_PrevAndNext(
			long patcherFixPackId, long patcherProjectVersionId, int status,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_S_PrevAndNext(
			patcherFixPackId, patcherProjectVersionId, status,
			orderByComparator);
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_S(
		long patcherProjectVersionId, int status) {

		return getPersistence().filterFindByPFCI_S(
			patcherProjectVersionId, status);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end) {

		return getPersistence().filterFindByPFCI_S(
			patcherProjectVersionId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_S(
		long patcherProjectVersionId, int status, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().filterFindByPFCI_S(
			patcherProjectVersionId, status, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] filterFindByPFCI_S_PrevAndNext(
			long patcherFixPackId, long patcherProjectVersionId, int status,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().filterFindByPFCI_S_PrevAndNext(
			patcherFixPackId, patcherProjectVersionId, status,
			orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where patcherProjectVersionId = &#63; and status = &#63; from the database.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 */
	public static void removeByPFCI_S(
		long patcherProjectVersionId, int status) {

		getPersistence().removeByPFCI_S(patcherProjectVersionId, status);
	}

	/**
	 * Returns the number of patcher fix packs where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the number of matching patcher fix packs
	 */
	public static int countByPFCI_S(long patcherProjectVersionId, int status) {
		return getPersistence().countByPFCI_S(patcherProjectVersionId, status);
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherProjectVersionId = &#63; and status = &#63;.
	 *
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param status the status
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public static int filterCountByPFCI_S(
		long patcherProjectVersionId, int status) {

		return getPersistence().filterCountByPFCI_S(
			patcherProjectVersionId, status);
	}

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return getPersistence().findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end) {

		return getPersistence().findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, start,
			end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end, OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPFCI_PPVI_GtV_First(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_PPVI_GtV_First(
			patcherFixComponentId, patcherProjectVersionId, version,
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_PPVI_GtV_First(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByPFCI_PPVI_GtV_First(
			patcherFixComponentId, patcherProjectVersionId, version,
			orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPFCI_PPVI_GtV_Last(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_PPVI_GtV_Last(
			patcherFixComponentId, patcherProjectVersionId, version,
			orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_PPVI_GtV_Last(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByPFCI_PPVI_GtV_Last(
			patcherFixComponentId, patcherProjectVersionId, version,
			orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] findByPFCI_PPVI_GtV_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_PPVI_GtV_PrevAndNext(
			patcherFixPackId, patcherFixComponentId, patcherProjectVersionId,
			version, orderByComparator);
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return getPersistence().filterFindByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end) {

		return getPersistence().filterFindByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, start,
			end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().filterFindByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] filterFindByPFCI_PPVI_GtV_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().filterFindByPFCI_PPVI_GtV_PrevAndNext(
			patcherFixPackId, patcherFixComponentId, patcherProjectVersionId,
			version, orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 */
	public static void removeByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		getPersistence().removeByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version);
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	public static int countByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return getPersistence().countByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version);
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &gt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public static int filterCountByPFCI_PPVI_GtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return getPersistence().filterCountByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version);
	}

	/**
	 * Returns all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return getPersistence().findByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version);
	}

	/**
	 * Returns a range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end) {

		return getPersistence().findByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, start,
			end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().findByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher fix packs
	 */
	public static List<PatcherFixPack> findByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end, OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPFCI_PPVI_LtV_First(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_PPVI_LtV_First(
			patcherFixComponentId, patcherProjectVersionId, version,
			orderByComparator);
	}

	/**
	 * Returns the first patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_PPVI_LtV_First(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByPFCI_PPVI_LtV_First(
			patcherFixComponentId, patcherProjectVersionId, version,
			orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPFCI_PPVI_LtV_Last(
			long patcherFixComponentId, long patcherProjectVersionId,
			int version, OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_PPVI_LtV_Last(
			patcherFixComponentId, patcherProjectVersionId, version,
			orderByComparator);
	}

	/**
	 * Returns the last patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_PPVI_LtV_Last(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().fetchByPFCI_PPVI_LtV_Last(
			patcherFixComponentId, patcherProjectVersionId, version,
			orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] findByPFCI_PPVI_LtV_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_PPVI_LtV_PrevAndNext(
			patcherFixPackId, patcherFixComponentId, patcherProjectVersionId,
			version, orderByComparator);
	}

	/**
	 * Returns all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return getPersistence().filterFindByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version);
	}

	/**
	 * Returns a range of all the patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end) {

		return getPersistence().filterFindByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, start,
			end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs that the user has permissions to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher fix packs that the user has permission to view
	 */
	public static List<PatcherFixPack> filterFindByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().filterFindByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher fix packs before and after the current patcher fix pack in the ordered set of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixPackId the primary key of the current patcher fix pack
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack[] filterFindByPFCI_PPVI_LtV_PrevAndNext(
			long patcherFixPackId, long patcherFixComponentId,
			long patcherProjectVersionId, int version,
			OrderByComparator<PatcherFixPack> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().filterFindByPFCI_PPVI_LtV_PrevAndNext(
			patcherFixPackId, patcherFixComponentId, patcherProjectVersionId,
			version, orderByComparator);
	}

	/**
	 * Removes all the patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 */
	public static void removeByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		getPersistence().removeByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version);
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	public static int countByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return getPersistence().countByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version);
	}

	/**
	 * Returns the number of patcher fix packs that the user has permission to view where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and version &lt; &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param version the version
	 * @return the number of matching patcher fix packs that the user has permission to view
	 */
	public static int filterCountByPFCI_PPVI_LtV(
		long patcherFixComponentId, long patcherProjectVersionId, int version) {

		return getPersistence().filterCountByPFCI_PPVI_LtV(
			patcherFixComponentId, patcherProjectVersionId, version);
	}

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the matching patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack findByPFCI_PPVI_N_V(
			long patcherFixComponentId, long patcherProjectVersionId,
			String name, int version)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version);
	}

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version) {

		return getPersistence().fetchByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version);
	}

	/**
	 * Returns the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher fix pack, or <code>null</code> if a matching patcher fix pack could not be found
	 */
	public static PatcherFixPack fetchByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version, boolean useFinderCache) {

		return getPersistence().fetchByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version,
			useFinderCache);
	}

	/**
	 * Removes the patcher fix pack where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63; from the database.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the patcher fix pack that was removed
	 */
	public static PatcherFixPack removeByPFCI_PPVI_N_V(
			long patcherFixComponentId, long patcherProjectVersionId,
			String name, int version)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().removeByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version);
	}

	/**
	 * Returns the number of patcher fix packs where patcherFixComponentId = &#63; and patcherProjectVersionId = &#63; and name = &#63; and version = &#63;.
	 *
	 * @param patcherFixComponentId the patcher fix component ID
	 * @param patcherProjectVersionId the patcher project version ID
	 * @param name the name
	 * @param version the version
	 * @return the number of matching patcher fix packs
	 */
	public static int countByPFCI_PPVI_N_V(
		long patcherFixComponentId, long patcherProjectVersionId, String name,
		int version) {

		return getPersistence().countByPFCI_PPVI_N_V(
			patcherFixComponentId, patcherProjectVersionId, name, version);
	}

	/**
	 * Caches the patcher fix pack in the entity cache if it is enabled.
	 *
	 * @param patcherFixPack the patcher fix pack
	 */
	public static void cacheResult(PatcherFixPack patcherFixPack) {
		getPersistence().cacheResult(patcherFixPack);
	}

	/**
	 * Caches the patcher fix packs in the entity cache if it is enabled.
	 *
	 * @param patcherFixPacks the patcher fix packs
	 */
	public static void cacheResult(List<PatcherFixPack> patcherFixPacks) {
		getPersistence().cacheResult(patcherFixPacks);
	}

	/**
	 * Creates a new patcher fix pack with the primary key. Does not add the patcher fix pack to the database.
	 *
	 * @param patcherFixPackId the primary key for the new patcher fix pack
	 * @return the new patcher fix pack
	 */
	public static PatcherFixPack create(long patcherFixPackId) {
		return getPersistence().create(patcherFixPackId);
	}

	/**
	 * Removes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack that was removed
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack remove(long patcherFixPackId)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().remove(patcherFixPackId);
	}

	public static PatcherFixPack updateImpl(PatcherFixPack patcherFixPack) {
		return getPersistence().updateImpl(patcherFixPack);
	}

	/**
	 * Returns the patcher fix pack with the primary key or throws a <code>NoSuchPatcherFixPackException</code> if it could not be found.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack
	 * @throws NoSuchPatcherFixPackException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack findByPrimaryKey(long patcherFixPackId)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException {

		return getPersistence().findByPrimaryKey(patcherFixPackId);
	}

	/**
	 * Returns the patcher fix pack with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack, or <code>null</code> if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack fetchByPrimaryKey(long patcherFixPackId) {
		return getPersistence().fetchByPrimaryKey(patcherFixPackId);
	}

	/**
	 * Returns all the patcher fix packs.
	 *
	 * @return the patcher fix packs
	 */
	public static List<PatcherFixPack> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fix packs
	 */
	public static List<PatcherFixPack> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix packs
	 */
	public static List<PatcherFixPack> findAll(
		int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher fix packs
	 */
	public static List<PatcherFixPack> findAll(
		int start, int end, OrderByComparator<PatcherFixPack> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher fix packs from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of patcher fix packs.
	 *
	 * @return the number of patcher fix packs
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	/**
	 * Returns the primaryKeys of patcher fixes associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return long[] of the primaryKeys of patcher fixes associated with the patcher fix pack
	 */
	public static long[] getPatcherFixPrimaryKeys(long pk) {
		return getPersistence().getPatcherFixPrimaryKeys(pk);
	}

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * @param pk the primary key of the patcher fix
	 * @return the patcher fix packs associated with the patcher fix
	 */
	public static List<PatcherFixPack> getPatcherFixPatcherFixPacks(long pk) {
		return getPersistence().getPatcherFixPatcherFixPacks(pk);
	}

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher fix packs associated with the patcher fix
	 */
	public static List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long pk, int start, int end) {

		return getPersistence().getPatcherFixPatcherFixPacks(pk, start, end);
	}

	/**
	 * Returns all the patcher fix pack associated with the patcher fix.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher fix
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher fix packs associated with the patcher fix
	 */
	public static List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long pk, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getPersistence().getPatcherFixPatcherFixPacks(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher fixes associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @return the number of patcher fixes associated with the patcher fix pack
	 */
	public static int getPatcherFixesSize(long pk) {
		return getPersistence().getPatcherFixesSize(pk);
	}

	/**
	 * Returns <code>true</code> if the patcher fix is associated with the patcher fix pack.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if the patcher fix is associated with the patcher fix pack; <code>false</code> otherwise
	 */
	public static boolean containsPatcherFix(long pk, long patcherFixPK) {
		return getPersistence().containsPatcherFix(pk, patcherFixPK);
	}

	/**
	 * Returns <code>true</code> if the patcher fix pack has any patcher fixes associated with it.
	 *
	 * @param pk the primary key of the patcher fix pack to check for associations with patcher fixes
	 * @return <code>true</code> if the patcher fix pack has any patcher fixes associated with it; <code>false</code> otherwise
	 */
	public static boolean containsPatcherFixes(long pk) {
		return getPersistence().containsPatcherFixes(pk);
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 * @return <code>true</code> if an association between the patcher fix pack and the patcher fix was added; <code>false</code> if they were already associated
	 */
	public static boolean addPatcherFix(long pk, long patcherFixPK) {
		return getPersistence().addPatcherFix(pk, patcherFixPK);
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFix the patcher fix
	 * @return <code>true</code> if an association between the patcher fix pack and the patcher fix was added; <code>false</code> if they were already associated
	 */
	public static boolean addPatcherFix(
		long pk, com.liferay.osb.patcher.model.PatcherFix patcherFix) {

		return getPersistence().addPatcherFix(pk, patcherFix);
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher fix pack and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	public static boolean addPatcherFixes(long pk, long[] patcherFixPKs) {
		return getPersistence().addPatcherFixes(pk, patcherFixPKs);
	}

	/**
	 * Adds an association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes
	 * @return <code>true</code> if at least one association between the patcher fix pack and the patcher fixes was added; <code>false</code> if they were all already associated
	 */
	public static boolean addPatcherFixes(
		long pk, List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes) {

		return getPersistence().addPatcherFixes(pk, patcherFixes);
	}

	/**
	 * Clears all associations between the patcher fix pack and its patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack to clear the associated patcher fixes from
	 */
	public static void clearPatcherFixes(long pk) {
		getPersistence().clearPatcherFixes(pk);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPK the primary key of the patcher fix
	 */
	public static void removePatcherFix(long pk, long patcherFixPK) {
		getPersistence().removePatcherFix(pk, patcherFixPK);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fix. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFix the patcher fix
	 */
	public static void removePatcherFix(
		long pk, com.liferay.osb.patcher.model.PatcherFix patcherFix) {

		getPersistence().removePatcherFix(pk, patcherFix);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes
	 */
	public static void removePatcherFixes(long pk, long[] patcherFixPKs) {
		getPersistence().removePatcherFixes(pk, patcherFixPKs);
	}

	/**
	 * Removes the association between the patcher fix pack and the patcher fixes. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes
	 */
	public static void removePatcherFixes(
		long pk, List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes) {

		getPersistence().removePatcherFixes(pk, patcherFixes);
	}

	/**
	 * Sets the patcher fixes associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixPKs the primary keys of the patcher fixes to be associated with the patcher fix pack
	 */
	public static void setPatcherFixes(long pk, long[] patcherFixPKs) {
		getPersistence().setPatcherFixes(pk, patcherFixPKs);
	}

	/**
	 * Sets the patcher fixes associated with the patcher fix pack, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher fix pack
	 * @param patcherFixes the patcher fixes to be associated with the patcher fix pack
	 */
	public static void setPatcherFixes(
		long pk, List<com.liferay.osb.patcher.model.PatcherFix> patcherFixes) {

		getPersistence().setPatcherFixes(pk, patcherFixes);
	}

	public static PatcherFixPackPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(PatcherFixPackPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile PatcherFixPackPersistence _persistence;

}