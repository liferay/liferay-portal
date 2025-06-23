/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the patcher project version service. This utility wraps <code>com.liferay.osb.patcher.service.persistence.impl.PatcherProjectVersionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherProjectVersionPersistence
 * @generated
 */
public class PatcherProjectVersionUtil {

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
	public static void clearCache(PatcherProjectVersion patcherProjectVersion) {
		getPersistence().clearCache(patcherProjectVersion);
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
	public static Map<Serializable, PatcherProjectVersion> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PatcherProjectVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherProjectVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherProjectVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PatcherProjectVersion update(
		PatcherProjectVersion patcherProjectVersion) {

		return getPersistence().update(patcherProjectVersion);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PatcherProjectVersion update(
		PatcherProjectVersion patcherProjectVersion,
		ServiceContext serviceContext) {

		return getPersistence().update(patcherProjectVersion, serviceContext);
	}

	/**
	 * Returns all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId) {

		return getPersistence().findByPatcherProductVersionId(
			patcherProductVersionId);
	}

	/**
	 * Returns a range of all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end) {

		return getPersistence().findByPatcherProductVersionId(
			patcherProductVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().findByPatcherProductVersionId(
			patcherProductVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByPatcherProductVersionId(
		long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPatcherProductVersionId(
			patcherProductVersionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion findByPatcherProductVersionId_First(
			long patcherProductVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByPatcherProductVersionId_First(
			patcherProductVersionId, orderByComparator);
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion fetchByPatcherProductVersionId_First(
		long patcherProductVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().fetchByPatcherProductVersionId_First(
			patcherProductVersionId, orderByComparator);
	}

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion findByPatcherProductVersionId_Last(
			long patcherProductVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByPatcherProductVersionId_Last(
			patcherProductVersionId, orderByComparator);
	}

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion fetchByPatcherProductVersionId_Last(
		long patcherProductVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().fetchByPatcherProductVersionId_Last(
			patcherProductVersionId, orderByComparator);
	}

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public static PatcherProjectVersion[]
			findByPatcherProductVersionId_PrevAndNext(
				long patcherProjectVersionId, long patcherProductVersionId,
				OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByPatcherProductVersionId_PrevAndNext(
			patcherProjectVersionId, patcherProductVersionId,
			orderByComparator);
	}

	/**
	 * Returns all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the matching patcher project versions that the user has permission to view
	 */
	public static List<PatcherProjectVersion>
		filterFindByPatcherProductVersionId(long patcherProductVersionId) {

		return getPersistence().filterFindByPatcherProductVersionId(
			patcherProductVersionId);
	}

	/**
	 * Returns a range of all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions that the user has permission to view
	 */
	public static List<PatcherProjectVersion>
		filterFindByPatcherProductVersionId(
			long patcherProductVersionId, int start, int end) {

		return getPersistence().filterFindByPatcherProductVersionId(
			patcherProductVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where patcherProductVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	public static List<PatcherProjectVersion>
		filterFindByPatcherProductVersionId(
			long patcherProductVersionId, int start, int end,
			OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().filterFindByPatcherProductVersionId(
			patcherProductVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set of patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public static PatcherProjectVersion[]
			filterFindByPatcherProductVersionId_PrevAndNext(
				long patcherProjectVersionId, long patcherProductVersionId,
				OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().filterFindByPatcherProductVersionId_PrevAndNext(
			patcherProjectVersionId, patcherProductVersionId,
			orderByComparator);
	}

	/**
	 * Removes all the patcher project versions where patcherProductVersionId = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 */
	public static void removeByPatcherProductVersionId(
		long patcherProductVersionId) {

		getPersistence().removeByPatcherProductVersionId(
			patcherProductVersionId);
	}

	/**
	 * Returns the number of patcher project versions where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher project versions
	 */
	public static int countByPatcherProductVersionId(
		long patcherProductVersionId) {

		return getPersistence().countByPatcherProductVersionId(
			patcherProductVersionId);
	}

	/**
	 * Returns the number of patcher project versions that the user has permission to view where patcherProductVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	public static int filterCountByPatcherProductVersionId(
		long patcherProductVersionId) {

		return getPersistence().filterCountByPatcherProductVersionId(
			patcherProductVersionId);
	}

	/**
	 * Returns all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		return getPersistence().findByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId);
	}

	/**
	 * Returns a range of all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId, int start, int end) {

		return getPersistence().findByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().findByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion findByRootPatcherProjectVersionId_First(
			long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByRootPatcherProjectVersionId_First(
			rootPatcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the first patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion
		fetchByRootPatcherProjectVersionId_First(
			long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().fetchByRootPatcherProjectVersionId_First(
			rootPatcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the last patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion findByRootPatcherProjectVersionId_Last(
			long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByRootPatcherProjectVersionId_Last(
			rootPatcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the last patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion fetchByRootPatcherProjectVersionId_Last(
		long rootPatcherProjectVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().fetchByRootPatcherProjectVersionId_Last(
			rootPatcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public static PatcherProjectVersion[]
			findByRootPatcherProjectVersionId_PrevAndNext(
				long patcherProjectVersionId, long rootPatcherProjectVersionId,
				OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByRootPatcherProjectVersionId_PrevAndNext(
			patcherProjectVersionId, rootPatcherProjectVersionId,
			orderByComparator);
	}

	/**
	 * Returns all the patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the matching patcher project versions that the user has permission to view
	 */
	public static List<PatcherProjectVersion>
		filterFindByRootPatcherProjectVersionId(
			long rootPatcherProjectVersionId) {

		return getPersistence().filterFindByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId);
	}

	/**
	 * Returns a range of all the patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions that the user has permission to view
	 */
	public static List<PatcherProjectVersion>
		filterFindByRootPatcherProjectVersionId(
			long rootPatcherProjectVersionId, int start, int end) {

		return getPersistence().filterFindByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	public static List<PatcherProjectVersion>
		filterFindByRootPatcherProjectVersionId(
			long rootPatcherProjectVersionId, int start, int end,
			OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().filterFindByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set of patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public static PatcherProjectVersion[]
			filterFindByRootPatcherProjectVersionId_PrevAndNext(
				long patcherProjectVersionId, long rootPatcherProjectVersionId,
				OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().
			filterFindByRootPatcherProjectVersionId_PrevAndNext(
				patcherProjectVersionId, rootPatcherProjectVersionId,
				orderByComparator);
	}

	/**
	 * Removes all the patcher project versions where rootPatcherProjectVersionId = &#63; from the database.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 */
	public static void removeByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		getPersistence().removeByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId);
	}

	/**
	 * Returns the number of patcher project versions where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions
	 */
	public static int countByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		return getPersistence().countByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId);
	}

	/**
	 * Returns the number of patcher project versions that the user has permission to view where rootPatcherProjectVersionId = &#63;.
	 *
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	public static int filterCountByRootPatcherProjectVersionId(
		long rootPatcherProjectVersionId) {

		return getPersistence().filterCountByRootPatcherProjectVersionId(
			rootPatcherProjectVersionId);
	}

	/**
	 * Returns the patcher project version where committish = &#63; or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param committish the committish
	 * @return the matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion findByCommittish(String committish)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByCommittish(committish);
	}

	/**
	 * Returns the patcher project version where committish = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param committish the committish
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion fetchByCommittish(String committish) {
		return getPersistence().fetchByCommittish(committish);
	}

	/**
	 * Returns the patcher project version where committish = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param committish the committish
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion fetchByCommittish(
		String committish, boolean useFinderCache) {

		return getPersistence().fetchByCommittish(committish, useFinderCache);
	}

	/**
	 * Removes the patcher project version where committish = &#63; from the database.
	 *
	 * @param committish the committish
	 * @return the patcher project version that was removed
	 */
	public static PatcherProjectVersion removeByCommittish(String committish)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().removeByCommittish(committish);
	}

	/**
	 * Returns the number of patcher project versions where committish = &#63;.
	 *
	 * @param committish the committish
	 * @return the number of matching patcher project versions
	 */
	public static int countByCommittish(String committish) {
		return getPersistence().countByCommittish(committish);
	}

	/**
	 * Returns the patcher project version where name = &#63; or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion findByName(String name)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByName(name);
	}

	/**
	 * Returns the patcher project version where name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param name the name
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion fetchByName(String name) {
		return getPersistence().fetchByName(name);
	}

	/**
	 * Returns the patcher project version where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion fetchByName(
		String name, boolean useFinderCache) {

		return getPersistence().fetchByName(name, useFinderCache);
	}

	/**
	 * Removes the patcher project version where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the patcher project version that was removed
	 */
	public static PatcherProjectVersion removeByName(String name)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().removeByName(name);
	}

	/**
	 * Returns the number of patcher project versions where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching patcher project versions
	 */
	public static int countByName(String name) {
		return getPersistence().countByName(name);
	}

	/**
	 * Returns all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		return getPersistence().findByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId);
	}

	/**
	 * Returns a range of all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end) {

		return getPersistence().findByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().findByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion findByP_R_First(
			long patcherProductVersionId, long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByP_R_First(
			patcherProductVersionId, rootPatcherProjectVersionId,
			orderByComparator);
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion fetchByP_R_First(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().fetchByP_R_First(
			patcherProductVersionId, rootPatcherProjectVersionId,
			orderByComparator);
	}

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion findByP_R_Last(
			long patcherProductVersionId, long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByP_R_Last(
			patcherProductVersionId, rootPatcherProjectVersionId,
			orderByComparator);
	}

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion fetchByP_R_Last(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().fetchByP_R_Last(
			patcherProductVersionId, rootPatcherProjectVersionId,
			orderByComparator);
	}

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public static PatcherProjectVersion[] findByP_R_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByP_R_PrevAndNext(
			patcherProjectVersionId, patcherProductVersionId,
			rootPatcherProjectVersionId, orderByComparator);
	}

	/**
	 * Returns all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the matching patcher project versions that the user has permission to view
	 */
	public static List<PatcherProjectVersion> filterFindByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		return getPersistence().filterFindByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId);
	}

	/**
	 * Returns a range of all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions that the user has permission to view
	 */
	public static List<PatcherProjectVersion> filterFindByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end) {

		return getPersistence().filterFindByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	public static List<PatcherProjectVersion> filterFindByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId,
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().filterFindByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public static PatcherProjectVersion[] filterFindByP_R_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			long rootPatcherProjectVersionId,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().filterFindByP_R_PrevAndNext(
			patcherProjectVersionId, patcherProductVersionId,
			rootPatcherProjectVersionId, orderByComparator);
	}

	/**
	 * Removes all the patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 */
	public static void removeByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		getPersistence().removeByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId);
	}

	/**
	 * Returns the number of patcher project versions where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions
	 */
	public static int countByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		return getPersistence().countByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId);
	}

	/**
	 * Returns the number of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and rootPatcherProjectVersionId = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param rootPatcherProjectVersionId the root patcher project version ID
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	public static int filterCountByP_R(
		long patcherProductVersionId, long rootPatcherProjectVersionId) {

		return getPersistence().filterCountByP_R(
			patcherProductVersionId, rootPatcherProjectVersionId);
	}

	/**
	 * Returns all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName) {

		return getPersistence().findByP_RN(
			patcherProductVersionId, repositoryName);
	}

	/**
	 * Returns a range of all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName, int start,
		int end) {

		return getPersistence().findByP_RN(
			patcherProductVersionId, repositoryName, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().findByP_RN(
			patcherProductVersionId, repositoryName, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher project versions
	 */
	public static List<PatcherProjectVersion> findByP_RN(
		long patcherProductVersionId, String repositoryName, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_RN(
			patcherProductVersionId, repositoryName, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion findByP_RN_First(
			long patcherProductVersionId, String repositoryName,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByP_RN_First(
			patcherProductVersionId, repositoryName, orderByComparator);
	}

	/**
	 * Returns the first patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion fetchByP_RN_First(
		long patcherProductVersionId, String repositoryName,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().fetchByP_RN_First(
			patcherProductVersionId, repositoryName, orderByComparator);
	}

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion findByP_RN_Last(
			long patcherProductVersionId, String repositoryName,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByP_RN_Last(
			patcherProductVersionId, repositoryName, orderByComparator);
	}

	/**
	 * Returns the last patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher project version, or <code>null</code> if a matching patcher project version could not be found
	 */
	public static PatcherProjectVersion fetchByP_RN_Last(
		long patcherProductVersionId, String repositoryName,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().fetchByP_RN_Last(
			patcherProductVersionId, repositoryName, orderByComparator);
	}

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public static PatcherProjectVersion[] findByP_RN_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			String repositoryName,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByP_RN_PrevAndNext(
			patcherProjectVersionId, patcherProductVersionId, repositoryName,
			orderByComparator);
	}

	/**
	 * Returns all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the matching patcher project versions that the user has permission to view
	 */
	public static List<PatcherProjectVersion> filterFindByP_RN(
		long patcherProductVersionId, String repositoryName) {

		return getPersistence().filterFindByP_RN(
			patcherProductVersionId, repositoryName);
	}

	/**
	 * Returns a range of all the patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of matching patcher project versions that the user has permission to view
	 */
	public static List<PatcherProjectVersion> filterFindByP_RN(
		long patcherProductVersionId, String repositoryName, int start,
		int end) {

		return getPersistence().filterFindByP_RN(
			patcherProductVersionId, repositoryName, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher project versions that the user has permissions to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher project versions that the user has permission to view
	 */
	public static List<PatcherProjectVersion> filterFindByP_RN(
		long patcherProductVersionId, String repositoryName, int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().filterFindByP_RN(
			patcherProductVersionId, repositoryName, start, end,
			orderByComparator);
	}

	/**
	 * Returns the patcher project versions before and after the current patcher project version in the ordered set of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProjectVersionId the primary key of the current patcher project version
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public static PatcherProjectVersion[] filterFindByP_RN_PrevAndNext(
			long patcherProjectVersionId, long patcherProductVersionId,
			String repositoryName,
			OrderByComparator<PatcherProjectVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().filterFindByP_RN_PrevAndNext(
			patcherProjectVersionId, patcherProductVersionId, repositoryName,
			orderByComparator);
	}

	/**
	 * Removes all the patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63; from the database.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 */
	public static void removeByP_RN(
		long patcherProductVersionId, String repositoryName) {

		getPersistence().removeByP_RN(patcherProductVersionId, repositoryName);
	}

	/**
	 * Returns the number of patcher project versions where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the number of matching patcher project versions
	 */
	public static int countByP_RN(
		long patcherProductVersionId, String repositoryName) {

		return getPersistence().countByP_RN(
			patcherProductVersionId, repositoryName);
	}

	/**
	 * Returns the number of patcher project versions that the user has permission to view where patcherProductVersionId = &#63; and repositoryName = &#63;.
	 *
	 * @param patcherProductVersionId the patcher product version ID
	 * @param repositoryName the repository name
	 * @return the number of matching patcher project versions that the user has permission to view
	 */
	public static int filterCountByP_RN(
		long patcherProductVersionId, String repositoryName) {

		return getPersistence().filterCountByP_RN(
			patcherProductVersionId, repositoryName);
	}

	/**
	 * Caches the patcher project version in the entity cache if it is enabled.
	 *
	 * @param patcherProjectVersion the patcher project version
	 */
	public static void cacheResult(
		PatcherProjectVersion patcherProjectVersion) {

		getPersistence().cacheResult(patcherProjectVersion);
	}

	/**
	 * Caches the patcher project versions in the entity cache if it is enabled.
	 *
	 * @param patcherProjectVersions the patcher project versions
	 */
	public static void cacheResult(
		List<PatcherProjectVersion> patcherProjectVersions) {

		getPersistence().cacheResult(patcherProjectVersions);
	}

	/**
	 * Creates a new patcher project version with the primary key. Does not add the patcher project version to the database.
	 *
	 * @param patcherProjectVersionId the primary key for the new patcher project version
	 * @return the new patcher project version
	 */
	public static PatcherProjectVersion create(long patcherProjectVersionId) {
		return getPersistence().create(patcherProjectVersionId);
	}

	/**
	 * Removes the patcher project version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version that was removed
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public static PatcherProjectVersion remove(long patcherProjectVersionId)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().remove(patcherProjectVersionId);
	}

	public static PatcherProjectVersion updateImpl(
		PatcherProjectVersion patcherProjectVersion) {

		return getPersistence().updateImpl(patcherProjectVersion);
	}

	/**
	 * Returns the patcher project version with the primary key or throws a <code>NoSuchPatcherProjectVersionException</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version
	 * @throws NoSuchPatcherProjectVersionException if a patcher project version with the primary key could not be found
	 */
	public static PatcherProjectVersion findByPrimaryKey(
			long patcherProjectVersionId)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProjectVersionException {

		return getPersistence().findByPrimaryKey(patcherProjectVersionId);
	}

	/**
	 * Returns the patcher project version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherProjectVersionId the primary key of the patcher project version
	 * @return the patcher project version, or <code>null</code> if a patcher project version with the primary key could not be found
	 */
	public static PatcherProjectVersion fetchByPrimaryKey(
		long patcherProjectVersionId) {

		return getPersistence().fetchByPrimaryKey(patcherProjectVersionId);
	}

	/**
	 * Returns all the patcher project versions.
	 *
	 * @return the patcher project versions
	 */
	public static List<PatcherProjectVersion> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @return the range of patcher project versions
	 */
	public static List<PatcherProjectVersion> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher project versions
	 */
	public static List<PatcherProjectVersion> findAll(
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher project versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProjectVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher project versions
	 * @param end the upper bound of the range of patcher project versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher project versions
	 */
	public static List<PatcherProjectVersion> findAll(
		int start, int end,
		OrderByComparator<PatcherProjectVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher project versions from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of patcher project versions.
	 *
	 * @return the number of patcher project versions
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static PatcherProjectVersionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		PatcherProjectVersionPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile PatcherProjectVersionPersistence _persistence;

}