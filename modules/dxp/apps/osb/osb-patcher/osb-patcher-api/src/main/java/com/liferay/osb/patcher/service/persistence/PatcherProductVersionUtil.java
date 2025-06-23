/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the patcher product version service. This utility wraps <code>com.liferay.osb.patcher.service.persistence.impl.PatcherProductVersionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherProductVersionPersistence
 * @generated
 */
public class PatcherProductVersionUtil {

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
	public static void clearCache(PatcherProductVersion patcherProductVersion) {
		getPersistence().clearCache(patcherProductVersion);
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
	public static Map<Serializable, PatcherProductVersion> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PatcherProductVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherProductVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherProductVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PatcherProductVersion> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PatcherProductVersion update(
		PatcherProductVersion patcherProductVersion) {

		return getPersistence().update(patcherProductVersion);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PatcherProductVersion update(
		PatcherProductVersion patcherProductVersion,
		ServiceContext serviceContext) {

		return getPersistence().update(patcherProductVersion, serviceContext);
	}

	/**
	 * Returns all the patcher product versions where fixDeliveryMethod = &#63;.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @return the matching patcher product versions
	 */
	public static List<PatcherProductVersion> findByFixDeliveryMethod(
		int fixDeliveryMethod) {

		return getPersistence().findByFixDeliveryMethod(fixDeliveryMethod);
	}

	/**
	 * Returns a range of all the patcher product versions where fixDeliveryMethod = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @return the range of matching patcher product versions
	 */
	public static List<PatcherProductVersion> findByFixDeliveryMethod(
		int fixDeliveryMethod, int start, int end) {

		return getPersistence().findByFixDeliveryMethod(
			fixDeliveryMethod, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher product versions where fixDeliveryMethod = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher product versions
	 */
	public static List<PatcherProductVersion> findByFixDeliveryMethod(
		int fixDeliveryMethod, int start, int end,
		OrderByComparator<PatcherProductVersion> orderByComparator) {

		return getPersistence().findByFixDeliveryMethod(
			fixDeliveryMethod, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher product versions where fixDeliveryMethod = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher product versions
	 */
	public static List<PatcherProductVersion> findByFixDeliveryMethod(
		int fixDeliveryMethod, int start, int end,
		OrderByComparator<PatcherProductVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFixDeliveryMethod(
			fixDeliveryMethod, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher product version in the ordered set where fixDeliveryMethod = &#63;.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher product version
	 * @throws NoSuchPatcherProductVersionException if a matching patcher product version could not be found
	 */
	public static PatcherProductVersion findByFixDeliveryMethod_First(
			int fixDeliveryMethod,
			OrderByComparator<PatcherProductVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProductVersionException {

		return getPersistence().findByFixDeliveryMethod_First(
			fixDeliveryMethod, orderByComparator);
	}

	/**
	 * Returns the first patcher product version in the ordered set where fixDeliveryMethod = &#63;.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher product version, or <code>null</code> if a matching patcher product version could not be found
	 */
	public static PatcherProductVersion fetchByFixDeliveryMethod_First(
		int fixDeliveryMethod,
		OrderByComparator<PatcherProductVersion> orderByComparator) {

		return getPersistence().fetchByFixDeliveryMethod_First(
			fixDeliveryMethod, orderByComparator);
	}

	/**
	 * Returns the last patcher product version in the ordered set where fixDeliveryMethod = &#63;.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher product version
	 * @throws NoSuchPatcherProductVersionException if a matching patcher product version could not be found
	 */
	public static PatcherProductVersion findByFixDeliveryMethod_Last(
			int fixDeliveryMethod,
			OrderByComparator<PatcherProductVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProductVersionException {

		return getPersistence().findByFixDeliveryMethod_Last(
			fixDeliveryMethod, orderByComparator);
	}

	/**
	 * Returns the last patcher product version in the ordered set where fixDeliveryMethod = &#63;.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher product version, or <code>null</code> if a matching patcher product version could not be found
	 */
	public static PatcherProductVersion fetchByFixDeliveryMethod_Last(
		int fixDeliveryMethod,
		OrderByComparator<PatcherProductVersion> orderByComparator) {

		return getPersistence().fetchByFixDeliveryMethod_Last(
			fixDeliveryMethod, orderByComparator);
	}

	/**
	 * Returns the patcher product versions before and after the current patcher product version in the ordered set where fixDeliveryMethod = &#63;.
	 *
	 * @param patcherProductVersionId the primary key of the current patcher product version
	 * @param fixDeliveryMethod the fix delivery method
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher product version
	 * @throws NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 */
	public static PatcherProductVersion[] findByFixDeliveryMethod_PrevAndNext(
			long patcherProductVersionId, int fixDeliveryMethod,
			OrderByComparator<PatcherProductVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProductVersionException {

		return getPersistence().findByFixDeliveryMethod_PrevAndNext(
			patcherProductVersionId, fixDeliveryMethod, orderByComparator);
	}

	/**
	 * Returns all the patcher product versions that the user has permission to view where fixDeliveryMethod = &#63;.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @return the matching patcher product versions that the user has permission to view
	 */
	public static List<PatcherProductVersion> filterFindByFixDeliveryMethod(
		int fixDeliveryMethod) {

		return getPersistence().filterFindByFixDeliveryMethod(
			fixDeliveryMethod);
	}

	/**
	 * Returns a range of all the patcher product versions that the user has permission to view where fixDeliveryMethod = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @return the range of matching patcher product versions that the user has permission to view
	 */
	public static List<PatcherProductVersion> filterFindByFixDeliveryMethod(
		int fixDeliveryMethod, int start, int end) {

		return getPersistence().filterFindByFixDeliveryMethod(
			fixDeliveryMethod, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher product versions that the user has permissions to view where fixDeliveryMethod = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher product versions that the user has permission to view
	 */
	public static List<PatcherProductVersion> filterFindByFixDeliveryMethod(
		int fixDeliveryMethod, int start, int end,
		OrderByComparator<PatcherProductVersion> orderByComparator) {

		return getPersistence().filterFindByFixDeliveryMethod(
			fixDeliveryMethod, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher product versions before and after the current patcher product version in the ordered set of patcher product versions that the user has permission to view where fixDeliveryMethod = &#63;.
	 *
	 * @param patcherProductVersionId the primary key of the current patcher product version
	 * @param fixDeliveryMethod the fix delivery method
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher product version
	 * @throws NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 */
	public static PatcherProductVersion[]
			filterFindByFixDeliveryMethod_PrevAndNext(
				long patcherProductVersionId, int fixDeliveryMethod,
				OrderByComparator<PatcherProductVersion> orderByComparator)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProductVersionException {

		return getPersistence().filterFindByFixDeliveryMethod_PrevAndNext(
			patcherProductVersionId, fixDeliveryMethod, orderByComparator);
	}

	/**
	 * Removes all the patcher product versions where fixDeliveryMethod = &#63; from the database.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 */
	public static void removeByFixDeliveryMethod(int fixDeliveryMethod) {
		getPersistence().removeByFixDeliveryMethod(fixDeliveryMethod);
	}

	/**
	 * Returns the number of patcher product versions where fixDeliveryMethod = &#63;.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @return the number of matching patcher product versions
	 */
	public static int countByFixDeliveryMethod(int fixDeliveryMethod) {
		return getPersistence().countByFixDeliveryMethod(fixDeliveryMethod);
	}

	/**
	 * Returns the number of patcher product versions that the user has permission to view where fixDeliveryMethod = &#63;.
	 *
	 * @param fixDeliveryMethod the fix delivery method
	 * @return the number of matching patcher product versions that the user has permission to view
	 */
	public static int filterCountByFixDeliveryMethod(int fixDeliveryMethod) {
		return getPersistence().filterCountByFixDeliveryMethod(
			fixDeliveryMethod);
	}

	/**
	 * Returns the patcher product version where name = &#63; or throws a <code>NoSuchPatcherProductVersionException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching patcher product version
	 * @throws NoSuchPatcherProductVersionException if a matching patcher product version could not be found
	 */
	public static PatcherProductVersion findByName(String name)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProductVersionException {

		return getPersistence().findByName(name);
	}

	/**
	 * Returns the patcher product version where name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param name the name
	 * @return the matching patcher product version, or <code>null</code> if a matching patcher product version could not be found
	 */
	public static PatcherProductVersion fetchByName(String name) {
		return getPersistence().fetchByName(name);
	}

	/**
	 * Returns the patcher product version where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher product version, or <code>null</code> if a matching patcher product version could not be found
	 */
	public static PatcherProductVersion fetchByName(
		String name, boolean useFinderCache) {

		return getPersistence().fetchByName(name, useFinderCache);
	}

	/**
	 * Removes the patcher product version where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the patcher product version that was removed
	 */
	public static PatcherProductVersion removeByName(String name)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProductVersionException {

		return getPersistence().removeByName(name);
	}

	/**
	 * Returns the number of patcher product versions where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching patcher product versions
	 */
	public static int countByName(String name) {
		return getPersistence().countByName(name);
	}

	/**
	 * Caches the patcher product version in the entity cache if it is enabled.
	 *
	 * @param patcherProductVersion the patcher product version
	 */
	public static void cacheResult(
		PatcherProductVersion patcherProductVersion) {

		getPersistence().cacheResult(patcherProductVersion);
	}

	/**
	 * Caches the patcher product versions in the entity cache if it is enabled.
	 *
	 * @param patcherProductVersions the patcher product versions
	 */
	public static void cacheResult(
		List<PatcherProductVersion> patcherProductVersions) {

		getPersistence().cacheResult(patcherProductVersions);
	}

	/**
	 * Creates a new patcher product version with the primary key. Does not add the patcher product version to the database.
	 *
	 * @param patcherProductVersionId the primary key for the new patcher product version
	 * @return the new patcher product version
	 */
	public static PatcherProductVersion create(long patcherProductVersionId) {
		return getPersistence().create(patcherProductVersionId);
	}

	/**
	 * Removes the patcher product version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version that was removed
	 * @throws NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 */
	public static PatcherProductVersion remove(long patcherProductVersionId)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProductVersionException {

		return getPersistence().remove(patcherProductVersionId);
	}

	public static PatcherProductVersion updateImpl(
		PatcherProductVersion patcherProductVersion) {

		return getPersistence().updateImpl(patcherProductVersion);
	}

	/**
	 * Returns the patcher product version with the primary key or throws a <code>NoSuchPatcherProductVersionException</code> if it could not be found.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version
	 * @throws NoSuchPatcherProductVersionException if a patcher product version with the primary key could not be found
	 */
	public static PatcherProductVersion findByPrimaryKey(
			long patcherProductVersionId)
		throws com.liferay.osb.patcher.exception.
			NoSuchPatcherProductVersionException {

		return getPersistence().findByPrimaryKey(patcherProductVersionId);
	}

	/**
	 * Returns the patcher product version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherProductVersionId the primary key of the patcher product version
	 * @return the patcher product version, or <code>null</code> if a patcher product version with the primary key could not be found
	 */
	public static PatcherProductVersion fetchByPrimaryKey(
		long patcherProductVersionId) {

		return getPersistence().fetchByPrimaryKey(patcherProductVersionId);
	}

	/**
	 * Returns all the patcher product versions.
	 *
	 * @return the patcher product versions
	 */
	public static List<PatcherProductVersion> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the patcher product versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @return the range of patcher product versions
	 */
	public static List<PatcherProductVersion> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the patcher product versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher product versions
	 */
	public static List<PatcherProductVersion> findAll(
		int start, int end,
		OrderByComparator<PatcherProductVersion> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher product versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherProductVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher product versions
	 * @param end the upper bound of the range of patcher product versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher product versions
	 */
	public static List<PatcherProductVersion> findAll(
		int start, int end,
		OrderByComparator<PatcherProductVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher product versions from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of patcher product versions.
	 *
	 * @return the number of patcher product versions
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static PatcherProductVersionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		PatcherProductVersionPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile PatcherProductVersionPersistence _persistence;

}