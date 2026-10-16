/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence;

import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce inventory replenishment item service. This utility wraps <code>com.liferay.commerce.inventory.service.persistence.impl.CommerceInventoryReplenishmentItemPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryReplenishmentItemPersistence
 * @generated
 */
public class CommerceInventoryReplenishmentItemUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CommerceInventoryReplenishmentItem>
			commerceInventoryReplenishmentItems) {

		getPersistence().cacheResult(commerceInventoryReplenishmentItems);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem) {

		getPersistence().cacheResult(commerceInventoryReplenishmentItem);
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
		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem) {

		getPersistence().clearCache(commerceInventoryReplenishmentItem);
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
	public static Map<Serializable, CommerceInventoryReplenishmentItem>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommerceInventoryReplenishmentItem> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommerceInventoryReplenishmentItem> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommerceInventoryReplenishmentItem> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommerceInventoryReplenishmentItem update(
		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem) {

		return getPersistence().update(commerceInventoryReplenishmentItem);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommerceInventoryReplenishmentItem update(
		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem,
		ServiceContext serviceContext) {

		return getPersistence().update(
			commerceInventoryReplenishmentItem, serviceContext);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem findByUuid_First(
			String uuid,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator)
		throws com.liferay.commerce.inventory.exception.
			NoSuchInventoryReplenishmentItemException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of commerce inventory replenishment items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce inventory replenishment items
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator)
		throws com.liferay.commerce.inventory.exception.
			NoSuchInventoryReplenishmentItemException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of commerce inventory replenishment items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory replenishment items
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where commerceInventoryWarehouseId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem>
		findByCommerceInventoryWarehouseId(
			long commerceInventoryWarehouseId, int start, int end,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem
			findByCommerceInventoryWarehouseId_First(
				long commerceInventoryWarehouseId,
				OrderByComparator<CommerceInventoryReplenishmentItem>
					orderByComparator)
		throws com.liferay.commerce.inventory.exception.
			NoSuchInventoryReplenishmentItemException {

		return getPersistence().findByCommerceInventoryWarehouseId_First(
			commerceInventoryWarehouseId, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem
		fetchByCommerceInventoryWarehouseId_First(
			long commerceInventoryWarehouseId,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator) {

		return getPersistence().fetchByCommerceInventoryWarehouseId_First(
			commerceInventoryWarehouseId, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where commerceInventoryWarehouseId = &#63; from the database.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 */
	public static void removeByCommerceInventoryWarehouseId(
		long commerceInventoryWarehouseId) {

		getPersistence().removeByCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId);
	}

	/**
	 * Returns the number of commerce inventory replenishment items where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the number of matching commerce inventory replenishment items
	 */
	public static int countByCommerceInventoryWarehouseId(
		long commerceInventoryWarehouseId) {

		return getPersistence().countByCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param sku the sku
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findBySku(
		String sku, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findBySku(
			sku, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where sku = &#63;.
	 *
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem findBySku_First(
			String sku,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator)
		throws com.liferay.commerce.inventory.exception.
			NoSuchInventoryReplenishmentItemException {

		return getPersistence().findBySku_First(sku, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where sku = &#63;.
	 *
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem fetchBySku_First(
		String sku,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return getPersistence().fetchBySku_First(sku, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where sku = &#63; from the database.
	 *
	 * @param sku the sku
	 */
	public static void removeBySku(String sku) {
		getPersistence().removeBySku(sku);
	}

	/**
	 * Returns the number of commerce inventory replenishment items where sku = &#63;.
	 *
	 * @param sku the sku
	 * @return the number of matching commerce inventory replenishment items
	 */
	public static int countBySku(String sku) {
		return getPersistence().countBySku(sku);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByC_S_U(
		long companyId, String sku, String unitOfMeasureKey, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_S_U(
			companyId, sku, unitOfMeasureKey, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem findByC_S_U_First(
			long companyId, String sku, String unitOfMeasureKey,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator)
		throws com.liferay.commerce.inventory.exception.
			NoSuchInventoryReplenishmentItemException {

		return getPersistence().findByC_S_U_First(
			companyId, sku, unitOfMeasureKey, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem fetchByC_S_U_First(
		long companyId, String sku, String unitOfMeasureKey,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return getPersistence().fetchByC_S_U_First(
			companyId, sku, unitOfMeasureKey, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 */
	public static void removeByC_S_U(
		long companyId, String sku, String unitOfMeasureKey) {

		getPersistence().removeByC_S_U(companyId, sku, unitOfMeasureKey);
	}

	/**
	 * Returns the number of commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the number of matching commerce inventory replenishment items
	 */
	public static int countByC_S_U(
		long companyId, String sku, String unitOfMeasureKey) {

		return getPersistence().countByC_S_U(companyId, sku, unitOfMeasureKey);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByAD_S_U(
		Date availabilityDate, String sku, String unitOfMeasureKey, int start,
		int end,
		OrderByComparator<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByAD_S_U(
			availabilityDate, sku, unitOfMeasureKey, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem findByAD_S_U_First(
			Date availabilityDate, String sku, String unitOfMeasureKey,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator)
		throws com.liferay.commerce.inventory.exception.
			NoSuchInventoryReplenishmentItemException {

		return getPersistence().findByAD_S_U_First(
			availabilityDate, sku, unitOfMeasureKey, orderByComparator);
	}

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem fetchByAD_S_U_First(
		Date availabilityDate, String sku, String unitOfMeasureKey,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return getPersistence().fetchByAD_S_U_First(
			availabilityDate, sku, unitOfMeasureKey, orderByComparator);
	}

	/**
	 * Removes all the commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63; from the database.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 */
	public static void removeByAD_S_U(
		Date availabilityDate, String sku, String unitOfMeasureKey) {

		getPersistence().removeByAD_S_U(
			availabilityDate, sku, unitOfMeasureKey);
	}

	/**
	 * Returns the number of commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the number of matching commerce inventory replenishment items
	 */
	public static int countByAD_S_U(
		Date availabilityDate, String sku, String unitOfMeasureKey) {

		return getPersistence().countByAD_S_U(
			availabilityDate, sku, unitOfMeasureKey);
	}

	/**
	 * Returns the commerce inventory replenishment item where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchInventoryReplenishmentItemException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.inventory.exception.
			NoSuchInventoryReplenishmentItemException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce inventory replenishment item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the commerce inventory replenishment item where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce inventory replenishment item that was removed
	 */
	public static CommerceInventoryReplenishmentItem removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.inventory.exception.
			NoSuchInventoryReplenishmentItemException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of commerce inventory replenishment items where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory replenishment items
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new commerce inventory replenishment item with the primary key. Does not add the commerce inventory replenishment item to the database.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key for the new commerce inventory replenishment item
	 * @return the new commerce inventory replenishment item
	 */
	public static CommerceInventoryReplenishmentItem create(
		long commerceInventoryReplenishmentItemId) {

		return getPersistence().create(commerceInventoryReplenishmentItemId);
	}

	/**
	 * Removes the commerce inventory replenishment item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item that was removed
	 * @throws NoSuchInventoryReplenishmentItemException if a commerce inventory replenishment item with the primary key could not be found
	 */
	public static CommerceInventoryReplenishmentItem remove(
			long commerceInventoryReplenishmentItemId)
		throws com.liferay.commerce.inventory.exception.
			NoSuchInventoryReplenishmentItemException {

		return getPersistence().remove(commerceInventoryReplenishmentItemId);
	}

	public static CommerceInventoryReplenishmentItem updateImpl(
		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem) {

		return getPersistence().updateImpl(commerceInventoryReplenishmentItem);
	}

	/**
	 * Returns the commerce inventory replenishment item with the primary key or throws a <code>NoSuchInventoryReplenishmentItemException</code> if it could not be found.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a commerce inventory replenishment item with the primary key could not be found
	 */
	public static CommerceInventoryReplenishmentItem findByPrimaryKey(
			long commerceInventoryReplenishmentItemId)
		throws com.liferay.commerce.inventory.exception.
			NoSuchInventoryReplenishmentItemException {

		return getPersistence().findByPrimaryKey(
			commerceInventoryReplenishmentItemId);
	}

	/**
	 * Returns the commerce inventory replenishment item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item, or <code>null</code> if a commerce inventory replenishment item with the primary key could not be found
	 */
	public static CommerceInventoryReplenishmentItem fetchByPrimaryKey(
		long commerceInventoryReplenishmentItemId) {

		return getPersistence().fetchByPrimaryKey(
			commerceInventoryReplenishmentItemId);
	}

	/**
	 * Returns the commerce inventory replenishment item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public static CommerceInventoryReplenishmentItem fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the commerce inventory replenishment items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByUuid(
		String uuid) {

		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce inventory replenishment items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce inventory replenishment items where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem>
		findByCommerceInventoryWarehouseId(long commerceInventoryWarehouseId) {

		return getPersistence().findByCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items where commerceInventoryWarehouseId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem>
		findByCommerceInventoryWarehouseId(
			long commerceInventoryWarehouseId, int start, int end) {

		return getPersistence().findByCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where commerceInventoryWarehouseId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem>
		findByCommerceInventoryWarehouseId(
			long commerceInventoryWarehouseId, int start, int end,
			OrderByComparator<CommerceInventoryReplenishmentItem>
				orderByComparator) {

		return getPersistence().findByCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce inventory replenishment items where sku = &#63;.
	 *
	 * @param sku the sku
	 * @return the matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findBySku(
		String sku) {

		return getPersistence().findBySku(sku);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items where sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param sku the sku
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findBySku(
		String sku, int start, int end) {

		return getPersistence().findBySku(sku, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param sku the sku
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findBySku(
		String sku, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return getPersistence().findBySku(sku, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByC_S_U(
		long companyId, String sku, String unitOfMeasureKey) {

		return getPersistence().findByC_S_U(companyId, sku, unitOfMeasureKey);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByC_S_U(
		long companyId, String sku, String unitOfMeasureKey, int start,
		int end) {

		return getPersistence().findByC_S_U(
			companyId, sku, unitOfMeasureKey, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByC_S_U(
		long companyId, String sku, String unitOfMeasureKey, int start, int end,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return getPersistence().findByC_S_U(
			companyId, sku, unitOfMeasureKey, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByAD_S_U(
		Date availabilityDate, String sku, String unitOfMeasureKey) {

		return getPersistence().findByAD_S_U(
			availabilityDate, sku, unitOfMeasureKey);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByAD_S_U(
		Date availabilityDate, String sku, String unitOfMeasureKey, int start,
		int end) {

		return getPersistence().findByAD_S_U(
			availabilityDate, sku, unitOfMeasureKey, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public static List<CommerceInventoryReplenishmentItem> findByAD_S_U(
		Date availabilityDate, String sku, String unitOfMeasureKey, int start,
		int end,
		OrderByComparator<CommerceInventoryReplenishmentItem>
			orderByComparator) {

		return getPersistence().findByAD_S_U(
			availabilityDate, sku, unitOfMeasureKey, start, end,
			orderByComparator);
	}

	public static CommerceInventoryReplenishmentItemPersistence
		getPersistence() {

		return _persistence;
	}

	public static void setPersistence(
		CommerceInventoryReplenishmentItemPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CommerceInventoryReplenishmentItemPersistence
		_persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1542057464