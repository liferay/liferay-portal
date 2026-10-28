/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence;

import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the asset list entry audience entry rel service. This utility wraps <code>com.liferay.asset.list.service.persistence.impl.AssetListEntryAudienceEntryRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryAudienceEntryRelPersistence
 * @generated
 */
public class AssetListEntryAudienceEntryRelUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<AssetListEntryAudienceEntryRel> assetListEntryAudienceEntryRels) {

		getPersistence().cacheResult(assetListEntryAudienceEntryRels);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		getPersistence().cacheResult(assetListEntryAudienceEntryRel);
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
	public static void clearCache(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		getPersistence().clearCache(assetListEntryAudienceEntryRel);
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
	public static Map<Serializable, AssetListEntryAudienceEntryRel>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<AssetListEntryAudienceEntryRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<AssetListEntryAudienceEntryRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<AssetListEntryAudienceEntryRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static AssetListEntryAudienceEntryRel update(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		return getPersistence().update(assetListEntryAudienceEntryRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static AssetListEntryAudienceEntryRel update(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel,
		ServiceContext serviceContext) {

		return getPersistence().update(
			assetListEntryAudienceEntryRel, serviceContext);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel findByUuid_First(
			String uuid,
			OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator)
		throws com.liferay.asset.list.exception.
			NoSuchEntryAudienceEntryRelException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel fetchByUuid_First(
		String uuid,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the asset list entry audience entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of asset list entry audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset list entry audience entry rels
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the asset list entry audience entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel findByUUID_G(
			String uuid, long groupId)
		throws com.liferay.asset.list.exception.
			NoSuchEntryAudienceEntryRelException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the asset list entry audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the asset list entry audience entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset list entry audience entry rel that was removed
	 */
	public static AssetListEntryAudienceEntryRel removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.asset.list.exception.
			NoSuchEntryAudienceEntryRelException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of asset list entry audience entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset list entry audience entry rels
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator)
		throws com.liferay.asset.list.exception.
			NoSuchEntryAudienceEntryRelException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the asset list entry audience entry rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of asset list entry audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset list entry audience entry rels
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByAssetListEntryId(
		long assetListEntryId, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByAssetListEntryId(
			assetListEntryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel findByAssetListEntryId_First(
			long assetListEntryId,
			OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator)
		throws com.liferay.asset.list.exception.
			NoSuchEntryAudienceEntryRelException {

		return getPersistence().findByAssetListEntryId_First(
			assetListEntryId, orderByComparator);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel fetchByAssetListEntryId_First(
		long assetListEntryId,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return getPersistence().fetchByAssetListEntryId_First(
			assetListEntryId, orderByComparator);
	}

	/**
	 * Removes all the asset list entry audience entry rels where assetListEntryId = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 */
	public static void removeByAssetListEntryId(long assetListEntryId) {
		getPersistence().removeByAssetListEntryId(assetListEntryId);
	}

	/**
	 * Returns the number of asset list entry audience entry rels where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @return the number of matching asset list entry audience entry rels
	 */
	public static int countByAssetListEntryId(long assetListEntryId) {
		return getPersistence().countByAssetListEntryId(assetListEntryId);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_AEERC(
			companyId, audienceEntryERC, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel findByC_AEERC_First(
			long companyId, String audienceEntryERC,
			OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator)
		throws com.liferay.asset.list.exception.
			NoSuchEntryAudienceEntryRelException {

		return getPersistence().findByC_AEERC_First(
			companyId, audienceEntryERC, orderByComparator);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel fetchByC_AEERC_First(
		long companyId, String audienceEntryERC,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return getPersistence().fetchByC_AEERC_First(
			companyId, audienceEntryERC, orderByComparator);
	}

	/**
	 * Removes all the asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 */
	public static void removeByC_AEERC(
		long companyId, String audienceEntryERC) {

		getPersistence().removeByC_AEERC(companyId, audienceEntryERC);
	}

	/**
	 * Returns the number of asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching asset list entry audience entry rels
	 */
	public static int countByC_AEERC(long companyId, String audienceEntryERC) {
		return getPersistence().countByC_AEERC(companyId, audienceEntryERC);
	}

	/**
	 * Returns the asset list entry audience entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; or throws a <code>NoSuchEntryAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel findByA_AEERC(
			long assetListEntryId, String audienceEntryERC)
		throws com.liferay.asset.list.exception.
			NoSuchEntryAudienceEntryRelException {

		return getPersistence().findByA_AEERC(
			assetListEntryId, audienceEntryERC);
	}

	/**
	 * Returns the asset list entry audience entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel fetchByA_AEERC(
		long assetListEntryId, String audienceEntryERC,
		boolean useFinderCache) {

		return getPersistence().fetchByA_AEERC(
			assetListEntryId, audienceEntryERC, useFinderCache);
	}

	/**
	 * Removes the asset list entry audience entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the asset list entry audience entry rel that was removed
	 */
	public static AssetListEntryAudienceEntryRel removeByA_AEERC(
			long assetListEntryId, String audienceEntryERC)
		throws com.liferay.asset.list.exception.
			NoSuchEntryAudienceEntryRelException {

		return getPersistence().removeByA_AEERC(
			assetListEntryId, audienceEntryERC);
	}

	/**
	 * Returns the number of asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching asset list entry audience entry rels
	 */
	public static int countByA_AEERC(
		long assetListEntryId, String audienceEntryERC) {

		return getPersistence().countByA_AEERC(
			assetListEntryId, audienceEntryERC);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByA_AEERC_C(
		long assetListEntryId, String audienceEntryERC, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByA_AEERC_C(
			assetListEntryId, audienceEntryERC, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel findByA_AEERC_C_First(
			long assetListEntryId, String audienceEntryERC,
			OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator)
		throws com.liferay.asset.list.exception.
			NoSuchEntryAudienceEntryRelException {

		return getPersistence().findByA_AEERC_C_First(
			assetListEntryId, audienceEntryERC, orderByComparator);
	}

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel fetchByA_AEERC_C_First(
		long assetListEntryId, String audienceEntryERC,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return getPersistence().fetchByA_AEERC_C_First(
			assetListEntryId, audienceEntryERC, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERCs the audience entry ercs
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByA_AEERC_C(
		long assetListEntryId, String[] audienceEntryERCs, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByA_AEERC_C(
			assetListEntryId, audienceEntryERCs, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 */
	public static void removeByA_AEERC_C(
		long assetListEntryId, String audienceEntryERC) {

		getPersistence().removeByA_AEERC_C(assetListEntryId, audienceEntryERC);
	}

	/**
	 * Returns the number of asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching asset list entry audience entry rels
	 */
	public static int countByA_AEERC_C(
		long assetListEntryId, String audienceEntryERC) {

		return getPersistence().countByA_AEERC_C(
			assetListEntryId, audienceEntryERC);
	}

	/**
	 * Returns the number of asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = any &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERCs the audience entry ercs
	 * @return the number of matching asset list entry audience entry rels
	 */
	public static int countByA_AEERC_C(
		long assetListEntryId, String[] audienceEntryERCs) {

		return getPersistence().countByA_AEERC_C(
			assetListEntryId, audienceEntryERCs);
	}

	/**
	 * Creates a new asset list entry audience entry rel with the primary key. Does not add the asset list entry audience entry rel to the database.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key for the new asset list entry audience entry rel
	 * @return the new asset list entry audience entry rel
	 */
	public static AssetListEntryAudienceEntryRel create(
		long assetListEntryAudienceEntryRelId) {

		return getPersistence().create(assetListEntryAudienceEntryRelId);
	}

	/**
	 * Removes the asset list entry audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key of the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel that was removed
	 * @throws NoSuchEntryAudienceEntryRelException if a asset list entry audience entry rel with the primary key could not be found
	 */
	public static AssetListEntryAudienceEntryRel remove(
			long assetListEntryAudienceEntryRelId)
		throws com.liferay.asset.list.exception.
			NoSuchEntryAudienceEntryRelException {

		return getPersistence().remove(assetListEntryAudienceEntryRelId);
	}

	public static AssetListEntryAudienceEntryRel updateImpl(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		return getPersistence().updateImpl(assetListEntryAudienceEntryRel);
	}

	/**
	 * Returns the asset list entry audience entry rel with the primary key or throws a <code>NoSuchEntryAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key of the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a asset list entry audience entry rel with the primary key could not be found
	 */
	public static AssetListEntryAudienceEntryRel findByPrimaryKey(
			long assetListEntryAudienceEntryRelId)
		throws com.liferay.asset.list.exception.
			NoSuchEntryAudienceEntryRelException {

		return getPersistence().findByPrimaryKey(
			assetListEntryAudienceEntryRelId);
	}

	/**
	 * Returns the asset list entry audience entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key of the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel, or <code>null</code> if a asset list entry audience entry rel with the primary key could not be found
	 */
	public static AssetListEntryAudienceEntryRel fetchByPrimaryKey(
		long assetListEntryAudienceEntryRelId) {

		return getPersistence().fetchByPrimaryKey(
			assetListEntryAudienceEntryRelId);
	}

	/**
	 * Returns the asset list entry audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the asset list entry audience entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public static AssetListEntryAudienceEntryRel fetchByA_AEERC(
		long assetListEntryId, String audienceEntryERC) {

		return getPersistence().fetchByA_AEERC(
			assetListEntryId, audienceEntryERC);
	}

	/**
	 * Returns all the asset list entry audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entry audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entry audience entry rels where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @return the matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByAssetListEntryId(
		long assetListEntryId) {

		return getPersistence().findByAssetListEntryId(assetListEntryId);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels where assetListEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByAssetListEntryId(
		long assetListEntryId, int start, int end) {

		return getPersistence().findByAssetListEntryId(
			assetListEntryId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByAssetListEntryId(
		long assetListEntryId, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return getPersistence().findByAssetListEntryId(
			assetListEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC) {

		return getPersistence().findByC_AEERC(companyId, audienceEntryERC);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end) {

		return getPersistence().findByC_AEERC(
			companyId, audienceEntryERC, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return getPersistence().findByC_AEERC(
			companyId, audienceEntryERC, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByA_AEERC_C(
		long assetListEntryId, String audienceEntryERC) {

		return getPersistence().findByA_AEERC_C(
			assetListEntryId, audienceEntryERC);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByA_AEERC_C(
		long assetListEntryId, String audienceEntryERC, int start, int end) {

		return getPersistence().findByA_AEERC_C(
			assetListEntryId, audienceEntryERC, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByA_AEERC_C(
		long assetListEntryId, String audienceEntryERC, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return getPersistence().findByA_AEERC_C(
			assetListEntryId, audienceEntryERC, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = any &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERCs the audience entry ercs
	 * @return the matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByA_AEERC_C(
		long assetListEntryId, String[] audienceEntryERCs) {

		return getPersistence().findByA_AEERC_C(
			assetListEntryId, audienceEntryERCs);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERCs the audience entry ercs
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByA_AEERC_C(
		long assetListEntryId, String[] audienceEntryERCs, int start, int end) {

		return getPersistence().findByA_AEERC_C(
			assetListEntryId, audienceEntryERCs, start, end);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERCs the audience entry ercs
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public static List<AssetListEntryAudienceEntryRel> findByA_AEERC_C(
		long assetListEntryId, String[] audienceEntryERCs, int start, int end,
		OrderByComparator<AssetListEntryAudienceEntryRel> orderByComparator) {

		return getPersistence().findByA_AEERC_C(
			assetListEntryId, audienceEntryERCs, start, end, orderByComparator);
	}

	public static AssetListEntryAudienceEntryRelPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		AssetListEntryAudienceEntryRelPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile AssetListEntryAudienceEntryRelPersistence
		_persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1008470553