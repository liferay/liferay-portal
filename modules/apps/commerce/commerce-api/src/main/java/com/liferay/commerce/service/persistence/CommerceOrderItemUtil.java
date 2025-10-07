/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce order item service. This utility wraps <code>com.liferay.commerce.service.persistence.impl.CommerceOrderItemPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderItemPersistence
 * @generated
 */
public class CommerceOrderItemUtil {

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
	public static void clearCache(CommerceOrderItem commerceOrderItem) {
		getPersistence().clearCache(commerceOrderItem);
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
	public static Map<Serializable, CommerceOrderItem> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommerceOrderItem> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommerceOrderItem> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommerceOrderItem> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommerceOrderItem update(
		CommerceOrderItem commerceOrderItem) {

		return getPersistence().update(commerceOrderItem);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommerceOrderItem update(
		CommerceOrderItem commerceOrderItem, ServiceContext serviceContext) {

		return getPersistence().update(commerceOrderItem, serviceContext);
	}

	/**
	 * Returns all the commerce order items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce order items
	 */
	public static List<CommerceOrderItem> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the commerce order items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce order items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByUuid_First(
			String uuid, OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByUuid_First(
		String uuid, OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByUuid_Last(
			String uuid, OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByUuid_Last(
		String uuid, OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where uuid = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem[] findByUuid_PrevAndNext(
			long commerceOrderItemId, String uuid,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByUuid_PrevAndNext(
			commerceOrderItemId, uuid, orderByComparator);
	}

	/**
	 * Removes all the commerce order items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of commerce order items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce order items
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the commerce order item where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchOrderItemException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByUUID_G(String uuid, long groupId)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the commerce order item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the commerce order item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the commerce order item where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce order item that was removed
	 */
	public static CommerceOrderItem removeByUUID_G(String uuid, long groupId)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of commerce order items where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce order items
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the commerce order items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce order items
	 */
	public static List<CommerceOrderItem> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce order items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce order items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem[] findByUuid_C_PrevAndNext(
			long commerceOrderItemId, String uuid, long companyId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByUuid_C_PrevAndNext(
			commerceOrderItemId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the commerce order items where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of commerce order items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce order items
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the commerce order item where commerceInventoryBookedQuantityId = &#63; or throws a <code>NoSuchOrderItemException</code> if it could not be found.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @return the matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByCommerceInventoryBookedQuantityId(
			long commerceInventoryBookedQuantityId)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCommerceInventoryBookedQuantityId(
			commerceInventoryBookedQuantityId);
	}

	/**
	 * Returns the commerce order item where commerceInventoryBookedQuantityId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByCommerceInventoryBookedQuantityId(
		long commerceInventoryBookedQuantityId) {

		return getPersistence().fetchByCommerceInventoryBookedQuantityId(
			commerceInventoryBookedQuantityId);
	}

	/**
	 * Returns the commerce order item where commerceInventoryBookedQuantityId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByCommerceInventoryBookedQuantityId(
		long commerceInventoryBookedQuantityId, boolean useFinderCache) {

		return getPersistence().fetchByCommerceInventoryBookedQuantityId(
			commerceInventoryBookedQuantityId, useFinderCache);
	}

	/**
	 * Removes the commerce order item where commerceInventoryBookedQuantityId = &#63; from the database.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @return the commerce order item that was removed
	 */
	public static CommerceOrderItem removeByCommerceInventoryBookedQuantityId(
			long commerceInventoryBookedQuantityId)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().removeByCommerceInventoryBookedQuantityId(
			commerceInventoryBookedQuantityId);
	}

	/**
	 * Returns the number of commerce order items where commerceInventoryBookedQuantityId = &#63;.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @return the number of matching commerce order items
	 */
	public static int countByCommerceInventoryBookedQuantityId(
		long commerceInventoryBookedQuantityId) {

		return getPersistence().countByCommerceInventoryBookedQuantityId(
			commerceInventoryBookedQuantityId);
	}

	/**
	 * Returns all the commerce order items where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @return the matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCommerceOrderId(
		long commerceOrderId) {

		return getPersistence().findByCommerceOrderId(commerceOrderId);
	}

	/**
	 * Returns a range of all the commerce order items where commerceOrderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCommerceOrderId(
		long commerceOrderId, int start, int end) {

		return getPersistence().findByCommerceOrderId(
			commerceOrderId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCommerceOrderId(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().findByCommerceOrderId(
			commerceOrderId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCommerceOrderId(
		long commerceOrderId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCommerceOrderId(
			commerceOrderId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByCommerceOrderId_First(
			long commerceOrderId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCommerceOrderId_First(
			commerceOrderId, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByCommerceOrderId_First(
		long commerceOrderId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByCommerceOrderId_First(
			commerceOrderId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByCommerceOrderId_Last(
			long commerceOrderId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCommerceOrderId_Last(
			commerceOrderId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByCommerceOrderId_Last(
		long commerceOrderId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByCommerceOrderId_Last(
			commerceOrderId, orderByComparator);
	}

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem[] findByCommerceOrderId_PrevAndNext(
			long commerceOrderItemId, long commerceOrderId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCommerceOrderId_PrevAndNext(
			commerceOrderItemId, commerceOrderId, orderByComparator);
	}

	/**
	 * Removes all the commerce order items where commerceOrderId = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 */
	public static void removeByCommerceOrderId(long commerceOrderId) {
		getPersistence().removeByCommerceOrderId(commerceOrderId);
	}

	/**
	 * Returns the number of commerce order items where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @return the number of matching commerce order items
	 */
	public static int countByCommerceOrderId(long commerceOrderId) {
		return getPersistence().countByCommerceOrderId(commerceOrderId);
	}

	/**
	 * Returns all the commerce order items where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCPInstanceId(
		long CPInstanceId) {

		return getPersistence().findByCPInstanceId(CPInstanceId);
	}

	/**
	 * Returns a range of all the commerce order items where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCPInstanceId(
		long CPInstanceId, int start, int end) {

		return getPersistence().findByCPInstanceId(CPInstanceId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce order items where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().findByCPInstanceId(
			CPInstanceId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order items where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPInstanceId(
			CPInstanceId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByCPInstanceId_First(
			long CPInstanceId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCPInstanceId_First(
			CPInstanceId, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByCPInstanceId_First(
		long CPInstanceId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByCPInstanceId_First(
			CPInstanceId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByCPInstanceId_Last(
			long CPInstanceId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCPInstanceId_Last(
			CPInstanceId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByCPInstanceId_Last(
		long CPInstanceId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByCPInstanceId_Last(
			CPInstanceId, orderByComparator);
	}

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem[] findByCPInstanceId_PrevAndNext(
			long commerceOrderItemId, long CPInstanceId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCPInstanceId_PrevAndNext(
			commerceOrderItemId, CPInstanceId, orderByComparator);
	}

	/**
	 * Removes all the commerce order items where CPInstanceId = &#63; from the database.
	 *
	 * @param CPInstanceId the cp instance ID
	 */
	public static void removeByCPInstanceId(long CPInstanceId) {
		getPersistence().removeByCPInstanceId(CPInstanceId);
	}

	/**
	 * Returns the number of commerce order items where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching commerce order items
	 */
	public static int countByCPInstanceId(long CPInstanceId) {
		return getPersistence().countByCPInstanceId(CPInstanceId);
	}

	/**
	 * Returns all the commerce order items where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCProductId(long CProductId) {
		return getPersistence().findByCProductId(CProductId);
	}

	/**
	 * Returns a range of all the commerce order items where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCProductId(
		long CProductId, int start, int end) {

		return getPersistence().findByCProductId(CProductId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce order items where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().findByCProductId(
			CProductId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order items where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCProductId(
			CProductId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByCProductId_First(
			long CProductId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCProductId_First(
			CProductId, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByCProductId_First(
		long CProductId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByCProductId_First(
			CProductId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByCProductId_Last(
			long CProductId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCProductId_Last(
			CProductId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByCProductId_Last(
		long CProductId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByCProductId_Last(
			CProductId, orderByComparator);
	}

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where CProductId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem[] findByCProductId_PrevAndNext(
			long commerceOrderItemId, long CProductId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCProductId_PrevAndNext(
			commerceOrderItemId, CProductId, orderByComparator);
	}

	/**
	 * Removes all the commerce order items where CProductId = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 */
	public static void removeByCProductId(long CProductId) {
		getPersistence().removeByCProductId(CProductId);
	}

	/**
	 * Returns the number of commerce order items where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the number of matching commerce order items
	 */
	public static int countByCProductId(long CProductId) {
		return getPersistence().countByCProductId(CProductId);
	}

	/**
	 * Returns all the commerce order items where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @return the matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId) {

		return getPersistence().findByCustomerCommerceOrderItemId(
			customerCommerceOrderItemId);
	}

	/**
	 * Returns a range of all the commerce order items where customerCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId, int start, int end) {

		return getPersistence().findByCustomerCommerceOrderItemId(
			customerCommerceOrderItemId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce order items where customerCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().findByCustomerCommerceOrderItemId(
			customerCommerceOrderItemId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order items where customerCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCustomerCommerceOrderItemId(
			customerCommerceOrderItemId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByCustomerCommerceOrderItemId_First(
			long customerCommerceOrderItemId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCustomerCommerceOrderItemId_First(
			customerCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByCustomerCommerceOrderItemId_First(
		long customerCommerceOrderItemId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByCustomerCommerceOrderItemId_First(
			customerCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByCustomerCommerceOrderItemId_Last(
			long customerCommerceOrderItemId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCustomerCommerceOrderItemId_Last(
			customerCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByCustomerCommerceOrderItemId_Last(
		long customerCommerceOrderItemId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByCustomerCommerceOrderItemId_Last(
			customerCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where customerCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem[]
			findByCustomerCommerceOrderItemId_PrevAndNext(
				long commerceOrderItemId, long customerCommerceOrderItemId,
				OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByCustomerCommerceOrderItemId_PrevAndNext(
			commerceOrderItemId, customerCommerceOrderItemId,
			orderByComparator);
	}

	/**
	 * Removes all the commerce order items where customerCommerceOrderItemId = &#63; from the database.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 */
	public static void removeByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId) {

		getPersistence().removeByCustomerCommerceOrderItemId(
			customerCommerceOrderItemId);
	}

	/**
	 * Returns the number of commerce order items where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @return the number of matching commerce order items
	 */
	public static int countByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId) {

		return getPersistence().countByCustomerCommerceOrderItemId(
			customerCommerceOrderItemId);
	}

	/**
	 * Returns all the commerce order items where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @return the matching commerce order items
	 */
	public static List<CommerceOrderItem> findByParentCommerceOrderItemId(
		long parentCommerceOrderItemId) {

		return getPersistence().findByParentCommerceOrderItemId(
			parentCommerceOrderItemId);
	}

	/**
	 * Returns a range of all the commerce order items where parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByParentCommerceOrderItemId(
		long parentCommerceOrderItemId, int start, int end) {

		return getPersistence().findByParentCommerceOrderItemId(
			parentCommerceOrderItemId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce order items where parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByParentCommerceOrderItemId(
		long parentCommerceOrderItemId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().findByParentCommerceOrderItemId(
			parentCommerceOrderItemId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order items where parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByParentCommerceOrderItemId(
		long parentCommerceOrderItemId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByParentCommerceOrderItemId(
			parentCommerceOrderItemId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByParentCommerceOrderItemId_First(
			long parentCommerceOrderItemId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByParentCommerceOrderItemId_First(
			parentCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByParentCommerceOrderItemId_First(
		long parentCommerceOrderItemId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByParentCommerceOrderItemId_First(
			parentCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByParentCommerceOrderItemId_Last(
			long parentCommerceOrderItemId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByParentCommerceOrderItemId_Last(
			parentCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByParentCommerceOrderItemId_Last(
		long parentCommerceOrderItemId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByParentCommerceOrderItemId_Last(
			parentCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem[]
			findByParentCommerceOrderItemId_PrevAndNext(
				long commerceOrderItemId, long parentCommerceOrderItemId,
				OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByParentCommerceOrderItemId_PrevAndNext(
			commerceOrderItemId, parentCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Removes all the commerce order items where parentCommerceOrderItemId = &#63; from the database.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 */
	public static void removeByParentCommerceOrderItemId(
		long parentCommerceOrderItemId) {

		getPersistence().removeByParentCommerceOrderItemId(
			parentCommerceOrderItemId);
	}

	/**
	 * Returns the number of commerce order items where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @return the number of matching commerce order items
	 */
	public static int countByParentCommerceOrderItemId(
		long parentCommerceOrderItemId) {

		return getPersistence().countByParentCommerceOrderItemId(
			parentCommerceOrderItemId);
	}

	/**
	 * Returns all the commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @return the matching commerce order items
	 */
	public static List<CommerceOrderItem> findByC_CPI(
		long commerceOrderId, long CPInstanceId) {

		return getPersistence().findByC_CPI(commerceOrderId, CPInstanceId);
	}

	/**
	 * Returns a range of all the commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByC_CPI(
		long commerceOrderId, long CPInstanceId, int start, int end) {

		return getPersistence().findByC_CPI(
			commerceOrderId, CPInstanceId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByC_CPI(
		long commerceOrderId, long CPInstanceId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().findByC_CPI(
			commerceOrderId, CPInstanceId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByC_CPI(
		long commerceOrderId, long CPInstanceId, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_CPI(
			commerceOrderId, CPInstanceId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByC_CPI_First(
			long commerceOrderId, long CPInstanceId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByC_CPI_First(
			commerceOrderId, CPInstanceId, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByC_CPI_First(
		long commerceOrderId, long CPInstanceId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByC_CPI_First(
			commerceOrderId, CPInstanceId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByC_CPI_Last(
			long commerceOrderId, long CPInstanceId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByC_CPI_Last(
			commerceOrderId, CPInstanceId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByC_CPI_Last(
		long commerceOrderId, long CPInstanceId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByC_CPI_Last(
			commerceOrderId, CPInstanceId, orderByComparator);
	}

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem[] findByC_CPI_PrevAndNext(
			long commerceOrderItemId, long commerceOrderId, long CPInstanceId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByC_CPI_PrevAndNext(
			commerceOrderItemId, commerceOrderId, CPInstanceId,
			orderByComparator);
	}

	/**
	 * Removes all the commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 */
	public static void removeByC_CPI(long commerceOrderId, long CPInstanceId) {
		getPersistence().removeByC_CPI(commerceOrderId, CPInstanceId);
	}

	/**
	 * Returns the number of commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching commerce order items
	 */
	public static int countByC_CPI(long commerceOrderId, long CPInstanceId) {
		return getPersistence().countByC_CPI(commerceOrderId, CPInstanceId);
	}

	/**
	 * Returns all the commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @return the matching commerce order items
	 */
	public static List<CommerceOrderItem> findByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId) {

		return getPersistence().findByC_PCOI(
			commerceOrderId, parentCommerceOrderItemId);
	}

	/**
	 * Returns a range of all the commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId, int start,
		int end) {

		return getPersistence().findByC_PCOI(
			commerceOrderId, parentCommerceOrderItemId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId, int start,
		int end, OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().findByC_PCOI(
			commerceOrderId, parentCommerceOrderItemId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId, int start,
		int end, OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_PCOI(
			commerceOrderId, parentCommerceOrderItemId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByC_PCOI_First(
			long commerceOrderId, long parentCommerceOrderItemId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByC_PCOI_First(
			commerceOrderId, parentCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByC_PCOI_First(
		long commerceOrderId, long parentCommerceOrderItemId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByC_PCOI_First(
			commerceOrderId, parentCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByC_PCOI_Last(
			long commerceOrderId, long parentCommerceOrderItemId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByC_PCOI_Last(
			commerceOrderId, parentCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByC_PCOI_Last(
		long commerceOrderId, long parentCommerceOrderItemId,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByC_PCOI_Last(
			commerceOrderId, parentCommerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem[] findByC_PCOI_PrevAndNext(
			long commerceOrderItemId, long commerceOrderId,
			long parentCommerceOrderItemId,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByC_PCOI_PrevAndNext(
			commerceOrderItemId, commerceOrderId, parentCommerceOrderItemId,
			orderByComparator);
	}

	/**
	 * Removes all the commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 */
	public static void removeByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId) {

		getPersistence().removeByC_PCOI(
			commerceOrderId, parentCommerceOrderItemId);
	}

	/**
	 * Returns the number of commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @return the number of matching commerce order items
	 */
	public static int countByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId) {

		return getPersistence().countByC_PCOI(
			commerceOrderId, parentCommerceOrderItemId);
	}

	/**
	 * Returns all the commerce order items where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @return the matching commerce order items
	 */
	public static List<CommerceOrderItem> findByC_S(
		long commerceOrderId, boolean subscription) {

		return getPersistence().findByC_S(commerceOrderId, subscription);
	}

	/**
	 * Returns a range of all the commerce order items where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByC_S(
		long commerceOrderId, boolean subscription, int start, int end) {

		return getPersistence().findByC_S(
			commerceOrderId, subscription, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByC_S(
		long commerceOrderId, boolean subscription, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().findByC_S(
			commerceOrderId, subscription, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public static List<CommerceOrderItem> findByC_S(
		long commerceOrderId, boolean subscription, int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_S(
			commerceOrderId, subscription, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByC_S_First(
			long commerceOrderId, boolean subscription,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByC_S_First(
			commerceOrderId, subscription, orderByComparator);
	}

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByC_S_First(
		long commerceOrderId, boolean subscription,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByC_S_First(
			commerceOrderId, subscription, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByC_S_Last(
			long commerceOrderId, boolean subscription,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByC_S_Last(
			commerceOrderId, subscription, orderByComparator);
	}

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByC_S_Last(
		long commerceOrderId, boolean subscription,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().fetchByC_S_Last(
			commerceOrderId, subscription, orderByComparator);
	}

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem[] findByC_S_PrevAndNext(
			long commerceOrderItemId, long commerceOrderId,
			boolean subscription,
			OrderByComparator<CommerceOrderItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByC_S_PrevAndNext(
			commerceOrderItemId, commerceOrderId, subscription,
			orderByComparator);
	}

	/**
	 * Removes all the commerce order items where commerceOrderId = &#63; and subscription = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 */
	public static void removeByC_S(long commerceOrderId, boolean subscription) {
		getPersistence().removeByC_S(commerceOrderId, subscription);
	}

	/**
	 * Returns the number of commerce order items where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @return the number of matching commerce order items
	 */
	public static int countByC_S(long commerceOrderId, boolean subscription) {
		return getPersistence().countByC_S(commerceOrderId, subscription);
	}

	/**
	 * Returns the commerce order item where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOrderItemException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public static CommerceOrderItem fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the commerce order item where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce order item that was removed
	 */
	public static CommerceOrderItem removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of commerce order items where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce order items
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Caches the commerce order item in the entity cache if it is enabled.
	 *
	 * @param commerceOrderItem the commerce order item
	 */
	public static void cacheResult(CommerceOrderItem commerceOrderItem) {
		getPersistence().cacheResult(commerceOrderItem);
	}

	/**
	 * Caches the commerce order items in the entity cache if it is enabled.
	 *
	 * @param commerceOrderItems the commerce order items
	 */
	public static void cacheResult(List<CommerceOrderItem> commerceOrderItems) {
		getPersistence().cacheResult(commerceOrderItems);
	}

	/**
	 * Creates a new commerce order item with the primary key. Does not add the commerce order item to the database.
	 *
	 * @param commerceOrderItemId the primary key for the new commerce order item
	 * @return the new commerce order item
	 */
	public static CommerceOrderItem create(long commerceOrderItemId) {
		return getPersistence().create(commerceOrderItemId);
	}

	/**
	 * Removes the commerce order item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item that was removed
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem remove(long commerceOrderItemId)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().remove(commerceOrderItemId);
	}

	public static CommerceOrderItem updateImpl(
		CommerceOrderItem commerceOrderItem) {

		return getPersistence().updateImpl(commerceOrderItem);
	}

	/**
	 * Returns the commerce order item with the primary key or throws a <code>NoSuchOrderItemException</code> if it could not be found.
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem findByPrimaryKey(long commerceOrderItemId)
		throws com.liferay.commerce.exception.NoSuchOrderItemException {

		return getPersistence().findByPrimaryKey(commerceOrderItemId);
	}

	/**
	 * Returns the commerce order item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item, or <code>null</code> if a commerce order item with the primary key could not be found
	 */
	public static CommerceOrderItem fetchByPrimaryKey(
		long commerceOrderItemId) {

		return getPersistence().fetchByPrimaryKey(commerceOrderItemId);
	}

	/**
	 * Returns all the commerce order items.
	 *
	 * @return the commerce order items
	 */
	public static List<CommerceOrderItem> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the commerce order items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of commerce order items
	 */
	public static List<CommerceOrderItem> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the commerce order items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce order items
	 */
	public static List<CommerceOrderItem> findAll(
		int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce order items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce order items
	 */
	public static List<CommerceOrderItem> findAll(
		int start, int end,
		OrderByComparator<CommerceOrderItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the commerce order items from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of commerce order items.
	 *
	 * @return the number of commerce order items
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static CommerceOrderItemPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CommerceOrderItemPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CommerceOrderItemPersistence _persistence;

}