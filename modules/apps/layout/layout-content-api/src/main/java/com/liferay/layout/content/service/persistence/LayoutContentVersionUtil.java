/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.persistence;

import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout content version service. This utility wraps <code>com.liferay.layout.content.service.persistence.impl.LayoutContentVersionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Lourdes Fernández Besada
 * @see LayoutContentVersionPersistence
 * @generated
 */
public class LayoutContentVersionUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<LayoutContentVersion> layoutContentVersions) {

		getPersistence().cacheResult(layoutContentVersions);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(LayoutContentVersion layoutContentVersion) {
		getPersistence().cacheResult(layoutContentVersion);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(LayoutContentVersion layoutContentVersion) {
		getPersistence().clearCache(layoutContentVersion);
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
	public static Map<Serializable, LayoutContentVersion> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<LayoutContentVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<LayoutContentVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<LayoutContentVersion> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<LayoutContentVersion> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutContentVersion update(
		LayoutContentVersion layoutContentVersion) {

		return getPersistence().update(layoutContentVersion);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutContentVersion update(
		LayoutContentVersion layoutContentVersion,
		ServiceContext serviceContext) {

		return getPersistence().update(layoutContentVersion, serviceContext);
	}

	/**
	 * Returns an ordered range of all the layout content versions where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content versions
	 */
	public static List<LayoutContentVersion> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutContentVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPlid(
			plid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout content version in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	public static LayoutContentVersion findByPlid_First(
			long plid,
			OrderByComparator<LayoutContentVersion> orderByComparator)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionException {

		return getPersistence().findByPlid_First(plid, orderByComparator);
	}

	/**
	 * Returns the first layout content version in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	public static LayoutContentVersion fetchByPlid_First(
		long plid, OrderByComparator<LayoutContentVersion> orderByComparator) {

		return getPersistence().fetchByPlid_First(plid, orderByComparator);
	}

	/**
	 * Removes all the layout content versions where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	public static void removeByPlid(long plid) {
		getPersistence().removeByPlid(plid);
	}

	/**
	 * Returns the number of layout content versions where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout content versions
	 */
	public static int countByPlid(long plid) {
		return getPersistence().countByPlid(plid);
	}

	/**
	 * Returns the layout content version where plid = &#63; and version = &#63; or throws a <code>NoSuchLayoutContentVersionException</code> if it could not be found.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @return the matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	public static LayoutContentVersion findByP_V(long plid, int version)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionException {

		return getPersistence().findByP_V(plid, version);
	}

	/**
	 * Returns the layout content version where plid = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	public static LayoutContentVersion fetchByP_V(
		long plid, int version, boolean useFinderCache) {

		return getPersistence().fetchByP_V(plid, version, useFinderCache);
	}

	/**
	 * Removes the layout content version where plid = &#63; and version = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @return the layout content version that was removed
	 */
	public static LayoutContentVersion removeByP_V(long plid, int version)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionException {

		return getPersistence().removeByP_V(plid, version);
	}

	/**
	 * Returns the number of layout content versions where plid = &#63; and version = &#63;.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @return the number of matching layout content versions
	 */
	public static int countByP_V(long plid, int version) {
		return getPersistence().countByP_V(plid, version);
	}

	/**
	 * Returns an ordered range of all the layout content versions where plid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content versions
	 */
	public static List<LayoutContentVersion> findByP_S(
		long plid, int status, int start, int end,
		OrderByComparator<LayoutContentVersion> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_S(
			plid, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout content version in the ordered set where plid = &#63; and status = &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	public static LayoutContentVersion findByP_S_First(
			long plid, int status,
			OrderByComparator<LayoutContentVersion> orderByComparator)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionException {

		return getPersistence().findByP_S_First(
			plid, status, orderByComparator);
	}

	/**
	 * Returns the first layout content version in the ordered set where plid = &#63; and status = &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	public static LayoutContentVersion fetchByP_S_First(
		long plid, int status,
		OrderByComparator<LayoutContentVersion> orderByComparator) {

		return getPersistence().fetchByP_S_First(
			plid, status, orderByComparator);
	}

	/**
	 * Removes all the layout content versions where plid = &#63; and status = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param status the status
	 */
	public static void removeByP_S(long plid, int status) {
		getPersistence().removeByP_S(plid, status);
	}

	/**
	 * Returns the number of layout content versions where plid = &#63; and status = &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @return the number of matching layout content versions
	 */
	public static int countByP_S(long plid, int status) {
		return getPersistence().countByP_S(plid, status);
	}

	/**
	 * Returns the layout content version where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutContentVersionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	public static LayoutContentVersion findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout content version where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	public static LayoutContentVersion fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the layout content version where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout content version that was removed
	 */
	public static LayoutContentVersion removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of layout content versions where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout content versions
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new layout content version with the primary key. Does not add the layout content version to the database.
	 *
	 * @param layoutContentVersionId the primary key for the new layout content version
	 * @return the new layout content version
	 */
	public static LayoutContentVersion create(long layoutContentVersionId) {
		return getPersistence().create(layoutContentVersionId);
	}

	/**
	 * Removes the layout content version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version that was removed
	 * @throws NoSuchLayoutContentVersionException if a layout content version with the primary key could not be found
	 */
	public static LayoutContentVersion remove(long layoutContentVersionId)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionException {

		return getPersistence().remove(layoutContentVersionId);
	}

	public static LayoutContentVersion updateImpl(
		LayoutContentVersion layoutContentVersion) {

		return getPersistence().updateImpl(layoutContentVersion);
	}

	/**
	 * Returns the layout content version with the primary key or throws a <code>NoSuchLayoutContentVersionException</code> if it could not be found.
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version
	 * @throws NoSuchLayoutContentVersionException if a layout content version with the primary key could not be found
	 */
	public static LayoutContentVersion findByPrimaryKey(
			long layoutContentVersionId)
		throws com.liferay.layout.content.exception.
			NoSuchLayoutContentVersionException {

		return getPersistence().findByPrimaryKey(layoutContentVersionId);
	}

	/**
	 * Returns the layout content version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version, or <code>null</code> if a layout content version with the primary key could not be found
	 */
	public static LayoutContentVersion fetchByPrimaryKey(
		long layoutContentVersionId) {

		return getPersistence().fetchByPrimaryKey(layoutContentVersionId);
	}

	/**
	 * Returns the layout content version where plid = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @return the matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	public static LayoutContentVersion fetchByP_V(long plid, int version) {
		return getPersistence().fetchByP_V(plid, version);
	}

	/**
	 * Returns the layout content version where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	public static LayoutContentVersion fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the layout content versions where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the matching layout content versions
	 */
	public static List<LayoutContentVersion> findByPlid(long plid) {
		return getPersistence().findByPlid(plid);
	}

	/**
	 * Returns a range of all the layout content versions where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @return the range of matching layout content versions
	 */
	public static List<LayoutContentVersion> findByPlid(
		long plid, int start, int end) {

		return getPersistence().findByPlid(plid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout content versions where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout content versions
	 */
	public static List<LayoutContentVersion> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutContentVersion> orderByComparator) {

		return getPersistence().findByPlid(plid, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout content versions where plid = &#63; and status = &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @return the matching layout content versions
	 */
	public static List<LayoutContentVersion> findByP_S(long plid, int status) {
		return getPersistence().findByP_S(plid, status);
	}

	/**
	 * Returns a range of all the layout content versions where plid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @return the range of matching layout content versions
	 */
	public static List<LayoutContentVersion> findByP_S(
		long plid, int status, int start, int end) {

		return getPersistence().findByP_S(plid, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout content versions where plid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout content versions
	 */
	public static List<LayoutContentVersion> findByP_S(
		long plid, int status, int start, int end,
		OrderByComparator<LayoutContentVersion> orderByComparator) {

		return getPersistence().findByP_S(
			plid, status, start, end, orderByComparator);
	}

	public static LayoutContentVersionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		LayoutContentVersionPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile LayoutContentVersionPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1278857514