/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service.persistence;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the asset category service. This utility wraps <code>com.liferay.portlet.asset.service.persistence.impl.AssetCategoryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AssetCategoryPersistence
 * @generated
 */
public class AssetCategoryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<AssetCategory> assetCategories) {
		getPersistence().cacheResult(assetCategories);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(AssetCategory assetCategory) {
		getPersistence().cacheResult(assetCategory);
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
	public static void clearCache(AssetCategory assetCategory) {
		getPersistence().clearCache(assetCategory);
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
	public static Map<Serializable, AssetCategory> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<AssetCategory> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<AssetCategory> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<AssetCategory> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static AssetCategory update(AssetCategory assetCategory) {
		return getPersistence().update(assetCategory);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static AssetCategory update(
		AssetCategory assetCategory, ServiceContext serviceContext) {

		return getPersistence().update(assetCategory, serviceContext);
	}

	/**
	 * Returns an ordered range of all the asset categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByUuid_First(
			String uuid, OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByUuid_First(
		String uuid, OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the asset categories where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of asset categories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset categories
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the asset category where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByUUID_G(String uuid, long groupId)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the asset category where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the asset category where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset category that was removed
	 */
	public static AssetCategory removeByUUID_G(String uuid, long groupId)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of asset categories where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset categories
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the asset categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the asset categories where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of asset categories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset categories
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByGroupId_First(
			long groupId, OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByGroupId_First(
		long groupId, OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Removes all the asset categories where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of asset categories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset categories
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the asset categories where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByCompanyId_First(
			long companyId, OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByCompanyId_First(
		long companyId, OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the asset categories where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of asset categories where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching asset categories
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByParentCategoryId(
		long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByParentCategoryId(
			parentCategoryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByParentCategoryId_First(
			long parentCategoryId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByParentCategoryId_First(
			parentCategoryId, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByParentCategoryId_First(
		long parentCategoryId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByParentCategoryId_First(
			parentCategoryId, orderByComparator);
	}

	/**
	 * Removes all the asset categories where parentCategoryId = &#63; from the database.
	 *
	 * @param parentCategoryId the parent category ID
	 */
	public static void removeByParentCategoryId(long parentCategoryId) {
		getPersistence().removeByParentCategoryId(parentCategoryId);
	}

	/**
	 * Returns the number of asset categories where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching asset categories
	 */
	public static int countByParentCategoryId(long parentCategoryId) {
		return getPersistence().countByParentCategoryId(parentCategoryId);
	}

	/**
	 * Returns an ordered range of all the asset categories where vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByVocabularyId(
		long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByVocabularyId(
			vocabularyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByVocabularyId_First(
			long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByVocabularyId_First(
			vocabularyId, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByVocabularyId_First(
		long vocabularyId, OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByVocabularyId_First(
			vocabularyId, orderByComparator);
	}

	/**
	 * Removes all the asset categories where vocabularyId = &#63; from the database.
	 *
	 * @param vocabularyId the vocabulary ID
	 */
	public static void removeByVocabularyId(long vocabularyId) {
		getPersistence().removeByVocabularyId(vocabularyId);
	}

	/**
	 * Returns the number of asset categories where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	public static int countByVocabularyId(long vocabularyId) {
		return getPersistence().countByVocabularyId(vocabularyId);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P(
			groupId, parentCategoryId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByG_P_First(
			long groupId, long parentCategoryId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByG_P_First(
			groupId, parentCategoryId, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByG_P_First(
		long groupId, long parentCategoryId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByG_P_First(
			groupId, parentCategoryId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().filterFindByG_P(
			groupId, parentCategoryId, start, end, orderByComparator);
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and parentCategoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 */
	public static void removeByG_P(long groupId, long parentCategoryId) {
		getPersistence().removeByG_P(groupId, parentCategoryId);
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching asset categories
	 */
	public static int countByG_P(long groupId, long parentCategoryId) {
		return getPersistence().countByG_P(groupId, parentCategoryId);
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	public static int filterCountByG_P(long groupId, long parentCategoryId) {
		return getPersistence().filterCountByG_P(groupId, parentCategoryId);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_V(
		long groupId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_V(
			groupId, vocabularyId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByG_V_First(
			long groupId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByG_V_First(
			groupId, vocabularyId, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByG_V_First(
		long groupId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByG_V_First(
			groupId, vocabularyId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_V(
		long groupId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().filterFindByG_V(
			groupId, vocabularyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permission to view where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_V(
		long[] groupIds, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().filterFindByG_V(
			groupIds, vocabularyIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and vocabularyId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_V(
		long[] groupIds, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_V(
			groupIds, vocabularyIds, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 */
	public static void removeByG_V(long groupId, long vocabularyId) {
		getPersistence().removeByG_V(groupId, vocabularyId);
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	public static int countByG_V(long groupId, long vocabularyId) {
		return getPersistence().countByG_V(groupId, vocabularyId);
	}

	/**
	 * Returns the number of asset categories where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @return the number of matching asset categories
	 */
	public static int countByG_V(long[] groupIds, long[] vocabularyIds) {
		return getPersistence().countByG_V(groupIds, vocabularyIds);
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	public static int filterCountByG_V(long groupId, long vocabularyId) {
		return getPersistence().filterCountByG_V(groupId, vocabularyId);
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @return the number of matching asset categories that the user has permission to view
	 */
	public static int filterCountByG_V(long[] groupIds, long[] vocabularyIds) {
		return getPersistence().filterCountByG_V(groupIds, vocabularyIds);
	}

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByP_N(
		long parentCategoryId, String name, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_N(
			parentCategoryId, name, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByP_N_First(
			long parentCategoryId, String name,
			OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByP_N_First(
			parentCategoryId, name, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByP_N_First(
		long parentCategoryId, String name,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByP_N_First(
			parentCategoryId, name, orderByComparator);
	}

	/**
	 * Removes all the asset categories where parentCategoryId = &#63; and name = &#63; from the database.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 */
	public static void removeByP_N(long parentCategoryId, String name) {
		getPersistence().removeByP_N(parentCategoryId, name);
	}

	/**
	 * Returns the number of asset categories where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @return the number of matching asset categories
	 */
	public static int countByP_N(long parentCategoryId, String name) {
		return getPersistence().countByP_N(parentCategoryId, name);
	}

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByP_V(
		long parentCategoryId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_V(
			parentCategoryId, vocabularyId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByP_V_First(
			long parentCategoryId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByP_V_First(
			parentCategoryId, vocabularyId, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByP_V_First(
		long parentCategoryId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByP_V_First(
			parentCategoryId, vocabularyId, orderByComparator);
	}

	/**
	 * Removes all the asset categories where parentCategoryId = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 */
	public static void removeByP_V(long parentCategoryId, long vocabularyId) {
		getPersistence().removeByP_V(parentCategoryId, vocabularyId);
	}

	/**
	 * Returns the number of asset categories where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	public static int countByP_V(long parentCategoryId, long vocabularyId) {
		return getPersistence().countByP_V(parentCategoryId, vocabularyId);
	}

	/**
	 * Returns an ordered range of all the asset categories where name = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByN_V(
		String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByN_V(
			name, vocabularyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByN_V_First(
			String name, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByN_V_First(
			name, vocabularyId, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByN_V_First(
		String name, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByN_V_First(
			name, vocabularyId, orderByComparator);
	}

	/**
	 * Removes all the asset categories where name = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 */
	public static void removeByN_V(String name, long vocabularyId) {
		getPersistence().removeByN_V(name, vocabularyId);
	}

	/**
	 * Returns the number of asset categories where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	public static int countByN_V(String name, long vocabularyId) {
		return getPersistence().countByN_V(name, vocabularyId);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end, OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_V(
			groupId, parentCategoryId, vocabularyId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByG_P_V_First(
			long groupId, long parentCategoryId, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByG_P_V_First(
			groupId, parentCategoryId, vocabularyId, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByG_P_V_First(
		long groupId, long parentCategoryId, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByG_P_V_First(
			groupId, parentCategoryId, vocabularyId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end, OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().filterFindByG_P_V(
			groupId, parentCategoryId, vocabularyId, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 */
	public static void removeByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		getPersistence().removeByG_P_V(groupId, parentCategoryId, vocabularyId);
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	public static int countByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		return getPersistence().countByG_P_V(
			groupId, parentCategoryId, vocabularyId);
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	public static int filterCountByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		return getPersistence().filterCountByG_P_V(
			groupId, parentCategoryId, vocabularyId);
	}

	/**
	 * Returns all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		return getPersistence().findByG_LikeT_V(
			groupId, treePath, vocabularyId);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end) {

		return getPersistence().findByG_LikeT_V(
			groupId, treePath, vocabularyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByG_LikeT_V(
			groupId, treePath, vocabularyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeT_V(
			groupId, treePath, vocabularyId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByG_LikeT_V_First(
			long groupId, String treePath, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByG_LikeT_V_First(
			groupId, treePath, vocabularyId, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByG_LikeT_V_First(
		long groupId, String treePath, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByG_LikeT_V_First(
			groupId, treePath, vocabularyId, orderByComparator);
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		return getPersistence().filterFindByG_LikeT_V(
			groupId, treePath, vocabularyId);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end) {

		return getPersistence().filterFindByG_LikeT_V(
			groupId, treePath, vocabularyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_LikeT_V(
		long groupId, String treePath, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().filterFindByG_LikeT_V(
			groupId, treePath, vocabularyId, start, end, orderByComparator);
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 */
	public static void removeByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		getPersistence().removeByG_LikeT_V(groupId, treePath, vocabularyId);
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	public static int countByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		return getPersistence().countByG_LikeT_V(
			groupId, treePath, vocabularyId);
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and treePath LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param treePath the tree path
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	public static int filterCountByG_LikeT_V(
		long groupId, String treePath, long vocabularyId) {

		return getPersistence().filterCountByG_LikeT_V(
			groupId, treePath, vocabularyId);
	}

	/**
	 * Returns all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		return getPersistence().findByG_LikeN_V(groupId, name, vocabularyId);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end) {

		return getPersistence().findByG_LikeN_V(
			groupId, name, vocabularyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByG_LikeN_V(
			groupId, name, vocabularyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeN_V(
			groupId, name, vocabularyId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByG_LikeN_V_First(
			long groupId, String name, long vocabularyId,
			OrderByComparator<AssetCategory> orderByComparator)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByG_LikeN_V_First(
			groupId, name, vocabularyId, orderByComparator);
	}

	/**
	 * Returns the first asset category in the ordered set where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByG_LikeN_V_First(
		long groupId, String name, long vocabularyId,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().fetchByG_LikeN_V_First(
			groupId, name, vocabularyId, orderByComparator);
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		return getPersistence().filterFindByG_LikeN_V(
			groupId, name, vocabularyId);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end) {

		return getPersistence().filterFindByG_LikeN_V(
			groupId, name, vocabularyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permissions to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_LikeN_V(
		long groupId, String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().filterFindByG_LikeN_V(
			groupId, name, vocabularyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @return the matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds) {

		return getPersistence().filterFindByG_LikeN_V(
			groupIds, name, vocabularyIds);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start,
		int end) {

		return getPersistence().filterFindByG_LikeN_V(
			groupIds, name, vocabularyIds, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories that the user has permission to view where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().filterFindByG_LikeN_V(
			groupIds, name, vocabularyIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds) {

		return getPersistence().findByG_LikeN_V(groupIds, name, vocabularyIds);
	}

	/**
	 * Returns a range of all the asset categories where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start,
		int end) {

		return getPersistence().findByG_LikeN_V(
			groupIds, name, vocabularyIds, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByG_LikeN_V(
			groupIds, name, vocabularyIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeN_V(
			groupIds, name, vocabularyIds, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 */
	public static void removeByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		getPersistence().removeByG_LikeN_V(groupId, name, vocabularyId);
	}

	/**
	 * Returns the number of asset categories where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	public static int countByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		return getPersistence().countByG_LikeN_V(groupId, name, vocabularyId);
	}

	/**
	 * Returns the number of asset categories where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @return the number of matching asset categories
	 */
	public static int countByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds) {

		return getPersistence().countByG_LikeN_V(groupIds, name, vocabularyIds);
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = &#63; and name LIKE &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories that the user has permission to view
	 */
	public static int filterCountByG_LikeN_V(
		long groupId, String name, long vocabularyId) {

		return getPersistence().filterCountByG_LikeN_V(
			groupId, name, vocabularyId);
	}

	/**
	 * Returns the number of asset categories that the user has permission to view where groupId = any &#63; and name LIKE &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param name the name
	 * @param vocabularyIds the vocabulary IDs
	 * @return the number of matching asset categories that the user has permission to view
	 */
	public static int filterCountByG_LikeN_V(
		long[] groupIds, String name, long[] vocabularyIds) {

		return getPersistence().filterCountByG_LikeN_V(
			groupIds, name, vocabularyIds);
	}

	/**
	 * Returns the asset category where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByP_N_V(
			long parentCategoryId, String name, long vocabularyId)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByP_N_V(
			parentCategoryId, name, vocabularyId);
	}

	/**
	 * Returns the asset category where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByP_N_V(
		long parentCategoryId, String name, long vocabularyId,
		boolean useFinderCache) {

		return getPersistence().fetchByP_N_V(
			parentCategoryId, name, vocabularyId, useFinderCache);
	}

	/**
	 * Removes the asset category where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63; from the database.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the asset category that was removed
	 */
	public static AssetCategory removeByP_N_V(
			long parentCategoryId, String name, long vocabularyId)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().removeByP_N_V(
			parentCategoryId, name, vocabularyId);
	}

	/**
	 * Returns the number of asset categories where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the number of matching asset categories
	 */
	public static int countByP_N_V(
		long parentCategoryId, String name, long vocabularyId) {

		return getPersistence().countByP_N_V(
			parentCategoryId, name, vocabularyId);
	}

	/**
	 * Returns the asset category where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset category
	 * @throws NoSuchCategoryException if a matching asset category could not be found
	 */
	public static AssetCategory findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the asset category where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the asset category where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the asset category that was removed
	 */
	public static AssetCategory removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of asset categories where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching asset categories
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new asset category with the primary key. Does not add the asset category to the database.
	 *
	 * @param categoryId the primary key for the new asset category
	 * @return the new asset category
	 */
	public static AssetCategory create(long categoryId) {
		return getPersistence().create(categoryId);
	}

	/**
	 * Removes the asset category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category that was removed
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	public static AssetCategory remove(long categoryId)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().remove(categoryId);
	}

	public static AssetCategory updateImpl(AssetCategory assetCategory) {
		return getPersistence().updateImpl(assetCategory);
	}

	/**
	 * Returns the asset category with the primary key or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category
	 * @throws NoSuchCategoryException if a asset category with the primary key could not be found
	 */
	public static AssetCategory findByPrimaryKey(long categoryId)
		throws com.liferay.asset.kernel.exception.NoSuchCategoryException {

		return getPersistence().findByPrimaryKey(categoryId);
	}

	/**
	 * Returns the asset category with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param categoryId the primary key of the asset category
	 * @return the asset category, or <code>null</code> if a asset category with the primary key could not be found
	 */
	public static AssetCategory fetchByPrimaryKey(long categoryId) {
		return getPersistence().fetchByPrimaryKey(categoryId);
	}

	/**
	 * Returns the asset category where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the asset category where parentCategoryId = &#63; and name = &#63; and vocabularyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByP_N_V(
		long parentCategoryId, String name, long vocabularyId) {

		return getPersistence().fetchByP_N_V(
			parentCategoryId, name, vocabularyId);
	}

	/**
	 * Returns the asset category where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset category, or <code>null</code> if a matching asset category could not be found
	 */
	public static AssetCategory fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the asset categories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the asset categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the asset categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns all the asset categories where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the asset categories where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories where parentCategoryId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByParentCategoryId(
		long parentCategoryId) {

		return getPersistence().findByParentCategoryId(parentCategoryId);
	}

	/**
	 * Returns a range of all the asset categories where parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByParentCategoryId(
		long parentCategoryId, int start, int end) {

		return getPersistence().findByParentCategoryId(
			parentCategoryId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByParentCategoryId(
		long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByParentCategoryId(
			parentCategoryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories where vocabularyId = &#63;.
	 *
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByVocabularyId(long vocabularyId) {
		return getPersistence().findByVocabularyId(vocabularyId);
	}

	/**
	 * Returns a range of all the asset categories where vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByVocabularyId(
		long vocabularyId, int start, int end) {

		return getPersistence().findByVocabularyId(vocabularyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByVocabularyId(
		long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByVocabularyId(
			vocabularyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByG_P(
		long groupId, long parentCategoryId) {

		return getPersistence().findByG_P(groupId, parentCategoryId);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end) {

		return getPersistence().findByG_P(
			groupId, parentCategoryId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByG_P(
			groupId, parentCategoryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_P(
		long groupId, long parentCategoryId) {

		return getPersistence().filterFindByG_P(groupId, parentCategoryId);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_P(
		long groupId, long parentCategoryId, int start, int end) {

		return getPersistence().filterFindByG_P(
			groupId, parentCategoryId, start, end);
	}

	/**
	 * Returns all the asset categories where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByG_V(
		long groupId, long vocabularyId) {

		return getPersistence().findByG_V(groupId, vocabularyId);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByG_V(
		long groupId, long vocabularyId, int start, int end) {

		return getPersistence().findByG_V(groupId, vocabularyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_V(
		long groupId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByG_V(
			groupId, vocabularyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByG_V(
		long[] groupIds, long[] vocabularyIds) {

		return getPersistence().findByG_V(groupIds, vocabularyIds);
	}

	/**
	 * Returns a range of all the asset categories where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByG_V(
		long[] groupIds, long[] vocabularyIds, int start, int end) {

		return getPersistence().findByG_V(groupIds, vocabularyIds, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_V(
		long[] groupIds, long[] vocabularyIds, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByG_V(
			groupIds, vocabularyIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_V(
		long groupId, long vocabularyId) {

		return getPersistence().filterFindByG_V(groupId, vocabularyId);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_V(
		long groupId, long vocabularyId, int start, int end) {

		return getPersistence().filterFindByG_V(
			groupId, vocabularyId, start, end);
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @return the matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_V(
		long[] groupIds, long[] vocabularyIds) {

		return getPersistence().filterFindByG_V(groupIds, vocabularyIds);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = any &#63; and vocabularyId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param vocabularyIds the vocabulary IDs
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_V(
		long[] groupIds, long[] vocabularyIds, int start, int end) {

		return getPersistence().filterFindByG_V(
			groupIds, vocabularyIds, start, end);
	}

	/**
	 * Returns all the asset categories where parentCategoryId = &#63; and name = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByP_N(
		long parentCategoryId, String name) {

		return getPersistence().findByP_N(parentCategoryId, name);
	}

	/**
	 * Returns a range of all the asset categories where parentCategoryId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByP_N(
		long parentCategoryId, String name, int start, int end) {

		return getPersistence().findByP_N(parentCategoryId, name, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param name the name
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByP_N(
		long parentCategoryId, String name, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByP_N(
			parentCategoryId, name, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByP_V(
		long parentCategoryId, long vocabularyId) {

		return getPersistence().findByP_V(parentCategoryId, vocabularyId);
	}

	/**
	 * Returns a range of all the asset categories where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByP_V(
		long parentCategoryId, long vocabularyId, int start, int end) {

		return getPersistence().findByP_V(
			parentCategoryId, vocabularyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByP_V(
		long parentCategoryId, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByP_V(
			parentCategoryId, vocabularyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories where name = &#63; and vocabularyId = &#63;.
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByN_V(
		String name, long vocabularyId) {

		return getPersistence().findByN_V(name, vocabularyId);
	}

	/**
	 * Returns a range of all the asset categories where name = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByN_V(
		String name, long vocabularyId, int start, int end) {

		return getPersistence().findByN_V(name, vocabularyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where name = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByN_V(
		String name, long vocabularyId, int start, int end,
		OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByN_V(
			name, vocabularyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories
	 */
	public static List<AssetCategory> findByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		return getPersistence().findByG_P_V(
			groupId, parentCategoryId, vocabularyId);
	}

	/**
	 * Returns a range of all the asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories
	 */
	public static List<AssetCategory> findByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end) {

		return getPersistence().findByG_P_V(
			groupId, parentCategoryId, vocabularyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset categories where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset categories
	 */
	public static List<AssetCategory> findByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end, OrderByComparator<AssetCategory> orderByComparator) {

		return getPersistence().findByG_P_V(
			groupId, parentCategoryId, vocabularyId, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @return the matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId) {

		return getPersistence().filterFindByG_P_V(
			groupId, parentCategoryId, vocabularyId);
	}

	/**
	 * Returns a range of all the asset categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and vocabularyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param vocabularyId the vocabulary ID
	 * @param start the lower bound of the range of asset categories
	 * @param end the upper bound of the range of asset categories (not inclusive)
	 * @return the range of matching asset categories that the user has permission to view
	 */
	public static List<AssetCategory> filterFindByG_P_V(
		long groupId, long parentCategoryId, long vocabularyId, int start,
		int end) {

		return getPersistence().filterFindByG_P_V(
			groupId, parentCategoryId, vocabularyId, start, end);
	}

	public static AssetCategoryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(AssetCategoryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile AssetCategoryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1349448533