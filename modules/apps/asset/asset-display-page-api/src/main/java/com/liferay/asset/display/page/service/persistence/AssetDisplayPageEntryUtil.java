/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.service.persistence;

import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the asset display page entry service. This utility wraps <code>com.liferay.asset.display.page.service.persistence.impl.AssetDisplayPageEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AssetDisplayPageEntryPersistence
 * @generated
 */
public class AssetDisplayPageEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<AssetDisplayPageEntry> assetDisplayPageEntries) {

		getPersistence().cacheResult(assetDisplayPageEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		AssetDisplayPageEntry assetDisplayPageEntry) {

		getPersistence().cacheResult(assetDisplayPageEntry);
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
	public static void clearCache(AssetDisplayPageEntry assetDisplayPageEntry) {
		getPersistence().clearCache(assetDisplayPageEntry);
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
	public static Map<Serializable, AssetDisplayPageEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<AssetDisplayPageEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<AssetDisplayPageEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<AssetDisplayPageEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static AssetDisplayPageEntry update(
		AssetDisplayPageEntry assetDisplayPageEntry) {

		return getPersistence().update(assetDisplayPageEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static AssetDisplayPageEntry update(
		AssetDisplayPageEntry assetDisplayPageEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(assetDisplayPageEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the asset display page entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset display page entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry findByUuid_First(
			String uuid,
			OrderByComparator<AssetDisplayPageEntry> orderByComparator)
		throws com.liferay.asset.display.page.exception.
			NoSuchDisplayPageEntryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first asset display page entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the asset display page entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of asset display page entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset display page entries
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the asset display page entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchDisplayPageEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry findByUUID_G(String uuid, long groupId)
		throws com.liferay.asset.display.page.exception.
			NoSuchDisplayPageEntryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the asset display page entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the asset display page entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset display page entry that was removed
	 */
	public static AssetDisplayPageEntry removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.asset.display.page.exception.
			NoSuchDisplayPageEntryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of asset display page entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset display page entries
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the asset display page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset display page entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetDisplayPageEntry> orderByComparator)
		throws com.liferay.asset.display.page.exception.
			NoSuchDisplayPageEntryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first asset display page entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the asset display page entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of asset display page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset display page entries
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the asset display page entries where layoutPageTemplateEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByLayoutPageTemplateEntryId(
		long layoutPageTemplateEntryId, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset display page entry in the ordered set where layoutPageTemplateEntryId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry findByLayoutPageTemplateEntryId_First(
			long layoutPageTemplateEntryId,
			OrderByComparator<AssetDisplayPageEntry> orderByComparator)
		throws com.liferay.asset.display.page.exception.
			NoSuchDisplayPageEntryException {

		return getPersistence().findByLayoutPageTemplateEntryId_First(
			layoutPageTemplateEntryId, orderByComparator);
	}

	/**
	 * Returns the first asset display page entry in the ordered set where layoutPageTemplateEntryId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry fetchByLayoutPageTemplateEntryId_First(
		long layoutPageTemplateEntryId,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getPersistence().fetchByLayoutPageTemplateEntryId_First(
			layoutPageTemplateEntryId, orderByComparator);
	}

	/**
	 * Removes all the asset display page entries where layoutPageTemplateEntryId = &#63; from the database.
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 */
	public static void removeByLayoutPageTemplateEntryId(
		long layoutPageTemplateEntryId) {

		getPersistence().removeByLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId);
	}

	/**
	 * Returns the number of asset display page entries where layoutPageTemplateEntryId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @return the number of matching asset display page entries
	 */
	public static int countByLayoutPageTemplateEntryId(
		long layoutPageTemplateEntryId) {

		return getPersistence().countByLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId);
	}

	/**
	 * Returns an ordered range of all the asset display page entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_CN(
			groupId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset display page entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry findByG_CN_First(
			long groupId, long classNameId,
			OrderByComparator<AssetDisplayPageEntry> orderByComparator)
		throws com.liferay.asset.display.page.exception.
			NoSuchDisplayPageEntryException {

		return getPersistence().findByG_CN_First(
			groupId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first asset display page entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry fetchByG_CN_First(
		long groupId, long classNameId,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getPersistence().fetchByG_CN_First(
			groupId, classNameId, orderByComparator);
	}

	/**
	 * Removes all the asset display page entries where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	public static void removeByG_CN(long groupId, long classNameId) {
		getPersistence().removeByG_CN(groupId, classNameId);
	}

	/**
	 * Returns the number of asset display page entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching asset display page entries
	 */
	public static int countByG_CN(long groupId, long classNameId) {
		return getPersistence().countByG_CN(groupId, classNameId);
	}

	/**
	 * Returns the asset display page entry where groupId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchDisplayPageEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry findByG_C_C(
			long groupId, long classNameId, long classPK)
		throws com.liferay.asset.display.page.exception.
			NoSuchDisplayPageEntryException {

		return getPersistence().findByG_C_C(groupId, classNameId, classPK);
	}

	/**
	 * Returns the asset display page entry where groupId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry fetchByG_C_C(
		long groupId, long classNameId, long classPK, boolean useFinderCache) {

		return getPersistence().fetchByG_C_C(
			groupId, classNameId, classPK, useFinderCache);
	}

	/**
	 * Removes the asset display page entry where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the asset display page entry that was removed
	 */
	public static AssetDisplayPageEntry removeByG_C_C(
			long groupId, long classNameId, long classPK)
		throws com.liferay.asset.display.page.exception.
			NoSuchDisplayPageEntryException {

		return getPersistence().removeByG_C_C(groupId, classNameId, classPK);
	}

	/**
	 * Returns the number of asset display page entries where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching asset display page entries
	 */
	public static int countByG_C_C(
		long groupId, long classNameId, long classPK) {

		return getPersistence().countByG_C_C(groupId, classNameId, classPK);
	}

	/**
	 * Creates a new asset display page entry with the primary key. Does not add the asset display page entry to the database.
	 *
	 * @param assetDisplayPageEntryId the primary key for the new asset display page entry
	 * @return the new asset display page entry
	 */
	public static AssetDisplayPageEntry create(long assetDisplayPageEntryId) {
		return getPersistence().create(assetDisplayPageEntryId);
	}

	/**
	 * Removes the asset display page entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetDisplayPageEntryId the primary key of the asset display page entry
	 * @return the asset display page entry that was removed
	 * @throws NoSuchDisplayPageEntryException if a asset display page entry with the primary key could not be found
	 */
	public static AssetDisplayPageEntry remove(long assetDisplayPageEntryId)
		throws com.liferay.asset.display.page.exception.
			NoSuchDisplayPageEntryException {

		return getPersistence().remove(assetDisplayPageEntryId);
	}

	public static AssetDisplayPageEntry updateImpl(
		AssetDisplayPageEntry assetDisplayPageEntry) {

		return getPersistence().updateImpl(assetDisplayPageEntry);
	}

	/**
	 * Returns the asset display page entry with the primary key or throws a <code>NoSuchDisplayPageEntryException</code> if it could not be found.
	 *
	 * @param assetDisplayPageEntryId the primary key of the asset display page entry
	 * @return the asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a asset display page entry with the primary key could not be found
	 */
	public static AssetDisplayPageEntry findByPrimaryKey(
			long assetDisplayPageEntryId)
		throws com.liferay.asset.display.page.exception.
			NoSuchDisplayPageEntryException {

		return getPersistence().findByPrimaryKey(assetDisplayPageEntryId);
	}

	/**
	 * Returns the asset display page entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetDisplayPageEntryId the primary key of the asset display page entry
	 * @return the asset display page entry, or <code>null</code> if a asset display page entry with the primary key could not be found
	 */
	public static AssetDisplayPageEntry fetchByPrimaryKey(
		long assetDisplayPageEntryId) {

		return getPersistence().fetchByPrimaryKey(assetDisplayPageEntryId);
	}

	/**
	 * Returns the asset display page entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the asset display page entry where groupId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public static AssetDisplayPageEntry fetchByG_C_C(
		long groupId, long classNameId, long classPK) {

		return getPersistence().fetchByG_C_C(groupId, classNameId, classPK);
	}

	/**
	 * Returns all the asset display page entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the asset display page entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @return the range of matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the asset display page entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset display page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the asset display page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @return the range of matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset display page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset display page entries where layoutPageTemplateEntryId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @return the matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByLayoutPageTemplateEntryId(
		long layoutPageTemplateEntryId) {

		return getPersistence().findByLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId);
	}

	/**
	 * Returns a range of all the asset display page entries where layoutPageTemplateEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @return the range of matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByLayoutPageTemplateEntryId(
		long layoutPageTemplateEntryId, int start, int end) {

		return getPersistence().findByLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset display page entries where layoutPageTemplateEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByLayoutPageTemplateEntryId(
		long layoutPageTemplateEntryId, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getPersistence().findByLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset display page entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByG_CN(
		long groupId, long classNameId) {

		return getPersistence().findByG_CN(groupId, classNameId);
	}

	/**
	 * Returns a range of all the asset display page entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @return the range of matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByG_CN(
		long groupId, long classNameId, int start, int end) {

		return getPersistence().findByG_CN(groupId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset display page entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset display page entries
	 */
	public static List<AssetDisplayPageEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<AssetDisplayPageEntry> orderByComparator) {

		return getPersistence().findByG_CN(
			groupId, classNameId, start, end, orderByComparator);
	}

	public static AssetDisplayPageEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		AssetDisplayPageEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile AssetDisplayPageEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:2096406593