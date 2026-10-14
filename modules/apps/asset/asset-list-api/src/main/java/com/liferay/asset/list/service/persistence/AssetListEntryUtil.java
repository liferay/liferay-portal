/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the asset list entry service. This utility wraps <code>com.liferay.asset.list.service.persistence.impl.AssetListEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryPersistence
 * @generated
 */
public class AssetListEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<AssetListEntry> assetListEntries) {
		getPersistence().cacheResult(assetListEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(AssetListEntry assetListEntry) {
		getPersistence().cacheResult(assetListEntry);
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
	public static void clearCache(AssetListEntry assetListEntry) {
		getPersistence().clearCache(assetListEntry);
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
	public static Map<Serializable, AssetListEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<AssetListEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<AssetListEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<AssetListEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static AssetListEntry update(AssetListEntry assetListEntry) {
		return getPersistence().update(assetListEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static AssetListEntry update(
		AssetListEntry assetListEntry, ServiceContext serviceContext) {

		return getPersistence().update(assetListEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the asset list entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public static AssetListEntry findByUuid_First(
			String uuid, OrderByComparator<AssetListEntry> orderByComparator)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first asset list entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByUuid_First(
		String uuid, OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the asset list entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of asset list entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset list entries
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the asset list entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public static AssetListEntry findByUUID_G(String uuid, long groupId)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the asset list entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the asset list entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset list entry that was removed
	 */
	public static AssetListEntry removeByUUID_G(String uuid, long groupId)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of asset list entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset list entries
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the asset list entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public static AssetListEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first asset list entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the asset list entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of asset list entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset list entries
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public static AssetListEntry findByGroupId_First(
			long groupId, OrderByComparator<AssetListEntry> orderByComparator)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByGroupId_First(
		long groupId, OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupIds, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset list entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of asset list entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching asset list entries
	 */
	public static int countByGroupId(long[] groupIds) {
		return getPersistence().countByGroupId(groupIds);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public static int filterCountByGroupId(long[] groupIds) {
		return getPersistence().filterCountByGroupId(groupIds);
	}

	/**
	 * Returns the asset list entry where groupId = &#63; and assetListEntryKey = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @return the matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public static AssetListEntry findByG_ALEK(
			long groupId, String assetListEntryKey)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByG_ALEK(groupId, assetListEntryKey);
	}

	/**
	 * Returns the asset list entry where groupId = &#63; and assetListEntryKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByG_ALEK(
		long groupId, String assetListEntryKey, boolean useFinderCache) {

		return getPersistence().fetchByG_ALEK(
			groupId, assetListEntryKey, useFinderCache);
	}

	/**
	 * Removes the asset list entry where groupId = &#63; and assetListEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @return the asset list entry that was removed
	 */
	public static AssetListEntry removeByG_ALEK(
			long groupId, String assetListEntryKey)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().removeByG_ALEK(groupId, assetListEntryKey);
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and assetListEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @return the number of matching asset list entries
	 */
	public static int countByG_ALEK(long groupId, String assetListEntryKey) {
		return getPersistence().countByG_ALEK(groupId, assetListEntryKey);
	}

	/**
	 * Returns the asset list entry where groupId = &#63; and title = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public static AssetListEntry findByG_T(long groupId, String title)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByG_T(groupId, title);
	}

	/**
	 * Returns the asset list entry where groupId = &#63; and title = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByG_T(
		long groupId, String title, boolean useFinderCache) {

		return getPersistence().fetchByG_T(groupId, title, useFinderCache);
	}

	/**
	 * Removes the asset list entry where groupId = &#63; and title = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the asset list entry that was removed
	 */
	public static AssetListEntry removeByG_T(long groupId, String title)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().removeByG_T(groupId, title);
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and title = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the number of matching asset list entries
	 */
	public static int countByG_T(long groupId, String title) {
		return getPersistence().countByG_T(groupId, title);
	}

	/**
	 * Returns all the asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT(
		long groupId, String title) {

		return getPersistence().findByG_LikeT(groupId, title);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT(
		long groupId, String title, int start, int end) {

		return getPersistence().findByG_LikeT(groupId, title, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT(
		long groupId, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByG_LikeT(
			groupId, title, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT(
		long groupId, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeT(
			groupId, title, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public static AssetListEntry findByG_LikeT_First(
			long groupId, String title,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByG_LikeT_First(
			groupId, title, orderByComparator);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByG_LikeT_First(
		long groupId, String title,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().fetchByG_LikeT_First(
			groupId, title, orderByComparator);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT(
		long groupId, String title) {

		return getPersistence().filterFindByG_LikeT(groupId, title);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT(
		long groupId, String title, int start, int end) {

		return getPersistence().filterFindByG_LikeT(groupId, title, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT(
		long groupId, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().filterFindByG_LikeT(
			groupId, title, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @return the matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT(
		long[] groupIds, String title) {

		return getPersistence().filterFindByG_LikeT(groupIds, title);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT(
		long[] groupIds, String title, int start, int end) {

		return getPersistence().filterFindByG_LikeT(
			groupIds, title, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT(
		long[] groupIds, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().filterFindByG_LikeT(
			groupIds, title, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entries where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT(
		long[] groupIds, String title) {

		return getPersistence().findByG_LikeT(groupIds, title);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT(
		long[] groupIds, String title, int start, int end) {

		return getPersistence().findByG_LikeT(groupIds, title, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT(
		long[] groupIds, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByG_LikeT(
			groupIds, title, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT(
		long[] groupIds, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeT(
			groupIds, title, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; and title LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 */
	public static void removeByG_LikeT(long groupId, String title) {
		getPersistence().removeByG_LikeT(groupId, title);
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the number of matching asset list entries
	 */
	public static int countByG_LikeT(long groupId, String title) {
		return getPersistence().countByG_LikeT(groupId, title);
	}

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and title LIKE &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @return the number of matching asset list entries
	 */
	public static int countByG_LikeT(long[] groupIds, String title) {
		return getPersistence().countByG_LikeT(groupIds, title);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public static int filterCountByG_LikeT(long groupId, String title) {
		return getPersistence().filterCountByG_LikeT(groupId, title);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public static int filterCountByG_LikeT(long[] groupIds, String title) {
		return getPersistence().filterCountByG_LikeT(groupIds, title);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AET(
		long groupId, String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_AET(
			groupId, assetEntryType, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public static AssetListEntry findByG_AET_First(
			long groupId, String assetEntryType,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByG_AET_First(
			groupId, assetEntryType, orderByComparator);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByG_AET_First(
		long groupId, String assetEntryType,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().fetchByG_AET_First(
			groupId, assetEntryType, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_AET(
		long groupId, String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().filterFindByG_AET(
			groupId, assetEntryType, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_AET(
		long[] groupIds, String[] assetEntryTypes, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().filterFindByG_AET(
			groupIds, assetEntryTypes, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntryType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AET(
		long[] groupIds, String[] assetEntryTypes, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_AET(
			groupIds, assetEntryTypes, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; and assetEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 */
	public static void removeByG_AET(long groupId, String assetEntryType) {
		getPersistence().removeByG_AET(groupId, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	public static int countByG_AET(long groupId, String assetEntryType) {
		return getPersistence().countByG_AET(groupId, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @return the number of matching asset list entries
	 */
	public static int countByG_AET(long[] groupIds, String[] assetEntryTypes) {
		return getPersistence().countByG_AET(groupIds, assetEntryTypes);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public static int filterCountByG_AET(long groupId, String assetEntryType) {
		return getPersistence().filterCountByG_AET(groupId, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public static int filterCountByG_AET(
		long[] groupIds, String[] assetEntryTypes) {

		return getPersistence().filterCountByG_AET(groupIds, assetEntryTypes);
	}

	/**
	 * Returns all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AET(
		long groupId, String title, String assetEntryType) {

		return getPersistence().findByG_LikeT_AET(
			groupId, title, assetEntryType);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end) {

		return getPersistence().findByG_LikeT_AET(
			groupId, title, assetEntryType, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByG_LikeT_AET(
			groupId, title, assetEntryType, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeT_AET(
			groupId, title, assetEntryType, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public static AssetListEntry findByG_LikeT_AET_First(
			long groupId, String title, String assetEntryType,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByG_LikeT_AET_First(
			groupId, title, assetEntryType, orderByComparator);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByG_LikeT_AET_First(
		long groupId, String title, String assetEntryType,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().fetchByG_LikeT_AET_First(
			groupId, title, assetEntryType, orderByComparator);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT_AET(
		long groupId, String title, String assetEntryType) {

		return getPersistence().filterFindByG_LikeT_AET(
			groupId, title, assetEntryType);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end) {

		return getPersistence().filterFindByG_LikeT_AET(
			groupId, title, assetEntryType, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().filterFindByG_LikeT_AET(
			groupId, title, assetEntryType, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @return the matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes) {

		return getPersistence().filterFindByG_LikeT_AET(
			groupIds, title, assetEntryTypes);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end) {

		return getPersistence().filterFindByG_LikeT_AET(
			groupIds, title, assetEntryTypes, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end, OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().filterFindByG_LikeT_AET(
			groupIds, title, assetEntryTypes, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes) {

		return getPersistence().findByG_LikeT_AET(
			groupIds, title, assetEntryTypes);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end) {

		return getPersistence().findByG_LikeT_AET(
			groupIds, title, assetEntryTypes, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end, OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByG_LikeT_AET(
			groupIds, title, assetEntryTypes, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end, OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeT_AET(
			groupIds, title, assetEntryTypes, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 */
	public static void removeByG_LikeT_AET(
		long groupId, String title, String assetEntryType) {

		getPersistence().removeByG_LikeT_AET(groupId, title, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	public static int countByG_LikeT_AET(
		long groupId, String title, String assetEntryType) {

		return getPersistence().countByG_LikeT_AET(
			groupId, title, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @return the number of matching asset list entries
	 */
	public static int countByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes) {

		return getPersistence().countByG_LikeT_AET(
			groupIds, title, assetEntryTypes);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public static int filterCountByG_LikeT_AET(
		long groupId, String title, String assetEntryType) {

		return getPersistence().filterCountByG_LikeT_AET(
			groupId, title, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public static int filterCountByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes) {

		return getPersistence().filterCountByG_LikeT_AET(
			groupIds, title, assetEntryTypes);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType,
		int start, int end, OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public static AssetListEntry findByG_AES_AET_First(
			long groupId, String assetEntrySubtype, String assetEntryType,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByG_AES_AET_First(
			groupId, assetEntrySubtype, assetEntryType, orderByComparator);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByG_AES_AET_First(
		long groupId, String assetEntrySubtype, String assetEntryType,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().fetchByG_AES_AET_First(
			groupId, assetEntrySubtype, assetEntryType, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().filterFindByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().filterFindByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end, OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 */
	public static void removeByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType) {

		getPersistence().removeByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	public static int countByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType) {

		return getPersistence().countByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	public static int countByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType) {

		return getPersistence().countByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public static int filterCountByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType) {

		return getPersistence().filterCountByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public static int filterCountByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType) {

		return getPersistence().filterCountByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType) {

		return getPersistence().findByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end) {

		return getPersistence().findByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public static AssetListEntry findByG_LikeT_AES_AET_First(
			long groupId, String title, String assetEntrySubtype,
			String assetEntryType,
			OrderByComparator<AssetListEntry> orderByComparator)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByG_LikeT_AES_AET_First(
			groupId, title, assetEntrySubtype, assetEntryType,
			orderByComparator);
	}

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByG_LikeT_AES_AET_First(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().fetchByG_LikeT_AES_AET_First(
			groupId, title, assetEntrySubtype, assetEntryType,
			orderByComparator);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType) {

		return getPersistence().filterFindByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end) {

		return getPersistence().filterFindByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().filterFindByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType) {

		return getPersistence().filterFindByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end) {

		return getPersistence().filterFindByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().filterFindByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType) {

		return getPersistence().findByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end) {

		return getPersistence().findByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 */
	public static void removeByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType) {

		getPersistence().removeByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	public static int countByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType) {

		return getPersistence().countByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	public static int countByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType) {

		return getPersistence().countByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public static int filterCountByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType) {

		return getPersistence().filterCountByG_LikeT_AES_AET(
			groupId, title, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public static int filterCountByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType) {

		return getPersistence().filterCountByG_LikeT_AES_AET(
			groupIds, title, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns the asset list entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public static AssetListEntry findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the asset list entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the asset list entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the asset list entry that was removed
	 */
	public static AssetListEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of asset list entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching asset list entries
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new asset list entry with the primary key. Does not add the asset list entry to the database.
	 *
	 * @param assetListEntryId the primary key for the new asset list entry
	 * @return the new asset list entry
	 */
	public static AssetListEntry create(long assetListEntryId) {
		return getPersistence().create(assetListEntryId);
	}

	/**
	 * Removes the asset list entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetListEntryId the primary key of the asset list entry
	 * @return the asset list entry that was removed
	 * @throws NoSuchEntryException if a asset list entry with the primary key could not be found
	 */
	public static AssetListEntry remove(long assetListEntryId)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().remove(assetListEntryId);
	}

	public static AssetListEntry updateImpl(AssetListEntry assetListEntry) {
		return getPersistence().updateImpl(assetListEntry);
	}

	/**
	 * Returns the asset list entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param assetListEntryId the primary key of the asset list entry
	 * @return the asset list entry
	 * @throws NoSuchEntryException if a asset list entry with the primary key could not be found
	 */
	public static AssetListEntry findByPrimaryKey(long assetListEntryId)
		throws com.liferay.asset.list.exception.NoSuchEntryException {

		return getPersistence().findByPrimaryKey(assetListEntryId);
	}

	/**
	 * Returns the asset list entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetListEntryId the primary key of the asset list entry
	 * @return the asset list entry, or <code>null</code> if a asset list entry with the primary key could not be found
	 */
	public static AssetListEntry fetchByPrimaryKey(long assetListEntryId) {
		return getPersistence().fetchByPrimaryKey(assetListEntryId);
	}

	/**
	 * Returns the asset list entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the asset list entry where groupId = &#63; and assetListEntryKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByG_ALEK(
		long groupId, String assetListEntryKey) {

		return getPersistence().fetchByG_ALEK(groupId, assetListEntryKey);
	}

	/**
	 * Returns the asset list entry where groupId = &#63; and title = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByG_T(long groupId, String title) {
		return getPersistence().fetchByG_T(groupId, title);
	}

	/**
	 * Returns the asset list entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public static AssetListEntry fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the asset list entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the asset list entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the asset list entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByGroupId(long[] groupIds) {
		return getPersistence().findByGroupId(groupIds);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByGroupId(
		long[] groupIds, int start, int end) {

		return getPersistence().findByGroupId(groupIds, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByGroupId(long[] groupIds) {
		return getPersistence().filterFindByGroupId(groupIds);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByGroupId(
		long[] groupIds, int start, int end) {

		return getPersistence().filterFindByGroupId(groupIds, start, end);
	}

	/**
	 * Returns all the asset list entries where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AET(
		long groupId, String assetEntryType) {

		return getPersistence().findByG_AET(groupId, assetEntryType);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AET(
		long groupId, String assetEntryType, int start, int end) {

		return getPersistence().findByG_AET(
			groupId, assetEntryType, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AET(
		long groupId, String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByG_AET(
			groupId, assetEntryType, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entries where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AET(
		long[] groupIds, String[] assetEntryTypes) {

		return getPersistence().findByG_AET(groupIds, assetEntryTypes);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AET(
		long[] groupIds, String[] assetEntryTypes, int start, int end) {

		return getPersistence().findByG_AET(
			groupIds, assetEntryTypes, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AET(
		long[] groupIds, String[] assetEntryTypes, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByG_AET(
			groupIds, assetEntryTypes, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_AET(
		long groupId, String assetEntryType) {

		return getPersistence().filterFindByG_AET(groupId, assetEntryType);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_AET(
		long groupId, String assetEntryType, int start, int end) {

		return getPersistence().filterFindByG_AET(
			groupId, assetEntryType, start, end);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @return the matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_AET(
		long[] groupIds, String[] assetEntryTypes) {

		return getPersistence().filterFindByG_AET(groupIds, assetEntryTypes);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_AET(
		long[] groupIds, String[] assetEntryTypes, int start, int end) {

		return getPersistence().filterFindByG_AET(
			groupIds, assetEntryTypes, start, end);
	}

	/**
	 * Returns all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType) {

		return getPersistence().findByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType,
		int start, int end) {

		return getPersistence().findByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the asset list entries where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType) {

		return getPersistence().findByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end) {

		return getPersistence().findByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public static List<AssetListEntry> findByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return getPersistence().findByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType) {

		return getPersistence().filterFindByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType,
		int start, int end) {

		return getPersistence().filterFindByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType, start, end);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType) {

		return getPersistence().filterFindByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public static List<AssetListEntry> filterFindByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end) {

		return getPersistence().filterFindByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType, start, end);
	}

	public static AssetListEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(AssetListEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile AssetListEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1023284352