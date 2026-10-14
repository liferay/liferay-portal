/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.category.property.service.persistence;

import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the asset category property service. This utility wraps <code>com.liferay.asset.category.property.service.persistence.impl.AssetCategoryPropertyPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AssetCategoryPropertyPersistence
 * @generated
 */
public class AssetCategoryPropertyUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<AssetCategoryProperty> assetCategoryProperties) {

		getPersistence().cacheResult(assetCategoryProperties);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		AssetCategoryProperty assetCategoryProperty) {

		getPersistence().cacheResult(assetCategoryProperty);
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
	public static void clearCache(AssetCategoryProperty assetCategoryProperty) {
		getPersistence().clearCache(assetCategoryProperty);
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
	public static Map<Serializable, AssetCategoryProperty> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<AssetCategoryProperty> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<AssetCategoryProperty> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<AssetCategoryProperty> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<AssetCategoryProperty> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static AssetCategoryProperty update(
		AssetCategoryProperty assetCategoryProperty) {

		return getPersistence().update(assetCategoryProperty);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static AssetCategoryProperty update(
		AssetCategoryProperty assetCategoryProperty,
		ServiceContext serviceContext) {

		return getPersistence().update(assetCategoryProperty, serviceContext);
	}

	/**
	 * Returns an ordered range of all the asset category properties where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset category properties
	 */
	public static List<AssetCategoryProperty> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AssetCategoryProperty> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category property in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	public static AssetCategoryProperty findByCompanyId_First(
			long companyId,
			OrderByComparator<AssetCategoryProperty> orderByComparator)
		throws com.liferay.asset.category.property.exception.
			NoSuchCategoryPropertyException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first asset category property in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	public static AssetCategoryProperty fetchByCompanyId_First(
		long companyId,
		OrderByComparator<AssetCategoryProperty> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the asset category properties where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of asset category properties where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching asset category properties
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns an ordered range of all the asset category properties where categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset category properties
	 */
	public static List<AssetCategoryProperty> findByCategoryId(
		long categoryId, int start, int end,
		OrderByComparator<AssetCategoryProperty> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCategoryId(
			categoryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset category property in the ordered set where categoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	public static AssetCategoryProperty findByCategoryId_First(
			long categoryId,
			OrderByComparator<AssetCategoryProperty> orderByComparator)
		throws com.liferay.asset.category.property.exception.
			NoSuchCategoryPropertyException {

		return getPersistence().findByCategoryId_First(
			categoryId, orderByComparator);
	}

	/**
	 * Returns the first asset category property in the ordered set where categoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	public static AssetCategoryProperty fetchByCategoryId_First(
		long categoryId,
		OrderByComparator<AssetCategoryProperty> orderByComparator) {

		return getPersistence().fetchByCategoryId_First(
			categoryId, orderByComparator);
	}

	/**
	 * Removes all the asset category properties where categoryId = &#63; from the database.
	 *
	 * @param categoryId the category ID
	 */
	public static void removeByCategoryId(long categoryId) {
		getPersistence().removeByCategoryId(categoryId);
	}

	/**
	 * Returns the number of asset category properties where categoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @return the number of matching asset category properties
	 */
	public static int countByCategoryId(long categoryId) {
		return getPersistence().countByCategoryId(categoryId);
	}

	/**
	 * Returns the asset category property where categoryId = &#63; and key = &#63; or throws a <code>NoSuchCategoryPropertyException</code> if it could not be found.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @return the matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	public static AssetCategoryProperty findByCA_K(long categoryId, String key)
		throws com.liferay.asset.category.property.exception.
			NoSuchCategoryPropertyException {

		return getPersistence().findByCA_K(categoryId, key);
	}

	/**
	 * Returns the asset category property where categoryId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	public static AssetCategoryProperty fetchByCA_K(
		long categoryId, String key, boolean useFinderCache) {

		return getPersistence().fetchByCA_K(categoryId, key, useFinderCache);
	}

	/**
	 * Removes the asset category property where categoryId = &#63; and key = &#63; from the database.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @return the asset category property that was removed
	 */
	public static AssetCategoryProperty removeByCA_K(
			long categoryId, String key)
		throws com.liferay.asset.category.property.exception.
			NoSuchCategoryPropertyException {

		return getPersistence().removeByCA_K(categoryId, key);
	}

	/**
	 * Returns the number of asset category properties where categoryId = &#63; and key = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @return the number of matching asset category properties
	 */
	public static int countByCA_K(long categoryId, String key) {
		return getPersistence().countByCA_K(categoryId, key);
	}

	/**
	 * Returns the asset category property where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCategoryPropertyException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching asset category property
	 * @throws NoSuchCategoryPropertyException if a matching asset category property could not be found
	 */
	public static AssetCategoryProperty findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.asset.category.property.exception.
			NoSuchCategoryPropertyException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the asset category property where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	public static AssetCategoryProperty fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the asset category property where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the asset category property that was removed
	 */
	public static AssetCategoryProperty removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.asset.category.property.exception.
			NoSuchCategoryPropertyException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of asset category properties where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching asset category properties
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new asset category property with the primary key. Does not add the asset category property to the database.
	 *
	 * @param categoryPropertyId the primary key for the new asset category property
	 * @return the new asset category property
	 */
	public static AssetCategoryProperty create(long categoryPropertyId) {
		return getPersistence().create(categoryPropertyId);
	}

	/**
	 * Removes the asset category property with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param categoryPropertyId the primary key of the asset category property
	 * @return the asset category property that was removed
	 * @throws NoSuchCategoryPropertyException if a asset category property with the primary key could not be found
	 */
	public static AssetCategoryProperty remove(long categoryPropertyId)
		throws com.liferay.asset.category.property.exception.
			NoSuchCategoryPropertyException {

		return getPersistence().remove(categoryPropertyId);
	}

	public static AssetCategoryProperty updateImpl(
		AssetCategoryProperty assetCategoryProperty) {

		return getPersistence().updateImpl(assetCategoryProperty);
	}

	/**
	 * Returns the asset category property with the primary key or throws a <code>NoSuchCategoryPropertyException</code> if it could not be found.
	 *
	 * @param categoryPropertyId the primary key of the asset category property
	 * @return the asset category property
	 * @throws NoSuchCategoryPropertyException if a asset category property with the primary key could not be found
	 */
	public static AssetCategoryProperty findByPrimaryKey(
			long categoryPropertyId)
		throws com.liferay.asset.category.property.exception.
			NoSuchCategoryPropertyException {

		return getPersistence().findByPrimaryKey(categoryPropertyId);
	}

	/**
	 * Returns the asset category property with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param categoryPropertyId the primary key of the asset category property
	 * @return the asset category property, or <code>null</code> if a asset category property with the primary key could not be found
	 */
	public static AssetCategoryProperty fetchByPrimaryKey(
		long categoryPropertyId) {

		return getPersistence().fetchByPrimaryKey(categoryPropertyId);
	}

	/**
	 * Returns the asset category property where categoryId = &#63; and key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param categoryId the category ID
	 * @param key the key
	 * @return the matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	public static AssetCategoryProperty fetchByCA_K(
		long categoryId, String key) {

		return getPersistence().fetchByCA_K(categoryId, key);
	}

	/**
	 * Returns the asset category property where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching asset category property, or <code>null</code> if a matching asset category property could not be found
	 */
	public static AssetCategoryProperty fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the asset category properties where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching asset category properties
	 */
	public static List<AssetCategoryProperty> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the asset category properties where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @return the range of matching asset category properties
	 */
	public static List<AssetCategoryProperty> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset category properties where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset category properties
	 */
	public static List<AssetCategoryProperty> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AssetCategoryProperty> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the asset category properties where categoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @return the matching asset category properties
	 */
	public static List<AssetCategoryProperty> findByCategoryId(
		long categoryId) {

		return getPersistence().findByCategoryId(categoryId);
	}

	/**
	 * Returns a range of all the asset category properties where categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @return the range of matching asset category properties
	 */
	public static List<AssetCategoryProperty> findByCategoryId(
		long categoryId, int start, int end) {

		return getPersistence().findByCategoryId(categoryId, start, end);
	}

	/**
	 * Returns an ordered range of all the asset category properties where categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.category.property.model.impl.AssetCategoryPropertyModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of asset category properties
	 * @param end the upper bound of the range of asset category properties (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset category properties
	 */
	public static List<AssetCategoryProperty> findByCategoryId(
		long categoryId, int start, int end,
		OrderByComparator<AssetCategoryProperty> orderByComparator) {

		return getPersistence().findByCategoryId(
			categoryId, start, end, orderByComparator);
	}

	public static AssetCategoryPropertyPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		AssetCategoryPropertyPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile AssetCategoryPropertyPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1412950343