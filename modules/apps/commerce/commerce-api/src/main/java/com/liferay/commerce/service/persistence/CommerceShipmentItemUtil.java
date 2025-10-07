/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence;

import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce shipment item service. This utility wraps <code>com.liferay.commerce.service.persistence.impl.CommerceShipmentItemPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShipmentItemPersistence
 * @generated
 */
public class CommerceShipmentItemUtil {

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
	public static void clearCache(CommerceShipmentItem commerceShipmentItem) {
		getPersistence().clearCache(commerceShipmentItem);
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
	public static Map<Serializable, CommerceShipmentItem> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommerceShipmentItem> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommerceShipmentItem> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommerceShipmentItem> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommerceShipmentItem update(
		CommerceShipmentItem commerceShipmentItem) {

		return getPersistence().update(commerceShipmentItem);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommerceShipmentItem update(
		CommerceShipmentItem commerceShipmentItem,
		ServiceContext serviceContext) {

		return getPersistence().update(commerceShipmentItem, serviceContext);
	}

	/**
	 * Returns all the commerce shipment items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the commerce shipment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByUuid_First(
			String uuid,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByUuid_Last(
			String uuid,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByUuid_Last(
		String uuid,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where uuid = &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public static CommerceShipmentItem[] findByUuid_PrevAndNext(
			long commerceShipmentItemId, String uuid,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByUuid_PrevAndNext(
			commerceShipmentItemId, uuid, orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of commerce shipment items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce shipment items
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the commerce shipment item where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchShipmentItemException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByUUID_G(String uuid, long groupId)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the commerce shipment item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the commerce shipment item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the commerce shipment item where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce shipment item that was removed
	 */
	public static CommerceShipmentItem removeByUUID_G(String uuid, long groupId)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of commerce shipment items where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce shipment items
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the commerce shipment items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce shipment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public static CommerceShipmentItem[] findByUuid_C_PrevAndNext(
			long commerceShipmentItemId, String uuid, long companyId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByUuid_C_PrevAndNext(
			commerceShipmentItemId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of commerce shipment items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce shipment items
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the commerce shipment items where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the commerce shipment items where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByGroupId_First(
			long groupId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByGroupId_First(
		long groupId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByGroupId_Last(
			long groupId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByGroupId_Last(
		long groupId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where groupId = &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public static CommerceShipmentItem[] findByGroupId_PrevAndNext(
			long commerceShipmentItemId, long groupId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByGroupId_PrevAndNext(
			commerceShipmentItemId, groupId, orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of commerce shipment items where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce shipment items
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns all the commerce shipment items where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @return the matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByCommerceShipmentId(
		long commerceShipmentId) {

		return getPersistence().findByCommerceShipmentId(commerceShipmentId);
	}

	/**
	 * Returns a range of all the commerce shipment items where commerceShipmentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByCommerceShipmentId(
		long commerceShipmentId, int start, int end) {

		return getPersistence().findByCommerceShipmentId(
			commerceShipmentId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByCommerceShipmentId(
		long commerceShipmentId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().findByCommerceShipmentId(
			commerceShipmentId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByCommerceShipmentId(
		long commerceShipmentId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCommerceShipmentId(
			commerceShipmentId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByCommerceShipmentId_First(
			long commerceShipmentId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByCommerceShipmentId_First(
			commerceShipmentId, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByCommerceShipmentId_First(
		long commerceShipmentId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByCommerceShipmentId_First(
			commerceShipmentId, orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByCommerceShipmentId_Last(
			long commerceShipmentId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByCommerceShipmentId_Last(
			commerceShipmentId, orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByCommerceShipmentId_Last(
		long commerceShipmentId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByCommerceShipmentId_Last(
			commerceShipmentId, orderByComparator);
	}

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param commerceShipmentId the commerce shipment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public static CommerceShipmentItem[] findByCommerceShipmentId_PrevAndNext(
			long commerceShipmentItemId, long commerceShipmentId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByCommerceShipmentId_PrevAndNext(
			commerceShipmentItemId, commerceShipmentId, orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where commerceShipmentId = &#63; from the database.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 */
	public static void removeByCommerceShipmentId(long commerceShipmentId) {
		getPersistence().removeByCommerceShipmentId(commerceShipmentId);
	}

	/**
	 * Returns the number of commerce shipment items where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @return the number of matching commerce shipment items
	 */
	public static int countByCommerceShipmentId(long commerceShipmentId) {
		return getPersistence().countByCommerceShipmentId(commerceShipmentId);
	}

	/**
	 * Returns all the commerce shipment items where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByCommerceOrderItemId(
		long commerceOrderItemId) {

		return getPersistence().findByCommerceOrderItemId(commerceOrderItemId);
	}

	/**
	 * Returns a range of all the commerce shipment items where commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByCommerceOrderItemId(
		long commerceOrderItemId, int start, int end) {

		return getPersistence().findByCommerceOrderItemId(
			commerceOrderItemId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByCommerceOrderItemId(
		long commerceOrderItemId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().findByCommerceOrderItemId(
			commerceOrderItemId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByCommerceOrderItemId(
		long commerceOrderItemId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCommerceOrderItemId(
			commerceOrderItemId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByCommerceOrderItemId_First(
			long commerceOrderItemId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByCommerceOrderItemId_First(
			commerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByCommerceOrderItemId_First(
		long commerceOrderItemId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByCommerceOrderItemId_First(
			commerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByCommerceOrderItemId_Last(
			long commerceOrderItemId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByCommerceOrderItemId_Last(
			commerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByCommerceOrderItemId_Last(
		long commerceOrderItemId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByCommerceOrderItemId_Last(
			commerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public static CommerceShipmentItem[] findByCommerceOrderItemId_PrevAndNext(
			long commerceShipmentItemId, long commerceOrderItemId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByCommerceOrderItemId_PrevAndNext(
			commerceShipmentItemId, commerceOrderItemId, orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where commerceOrderItemId = &#63; from the database.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 */
	public static void removeByCommerceOrderItemId(long commerceOrderItemId) {
		getPersistence().removeByCommerceOrderItemId(commerceOrderItemId);
	}

	/**
	 * Returns the number of commerce shipment items where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the number of matching commerce shipment items
	 */
	public static int countByCommerceOrderItemId(long commerceOrderItemId) {
		return getPersistence().countByCommerceOrderItemId(commerceOrderItemId);
	}

	/**
	 * Returns all the commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByC_C(
		long commerceShipmentId, long commerceOrderItemId) {

		return getPersistence().findByC_C(
			commerceShipmentId, commerceOrderItemId);
	}

	/**
	 * Returns a range of all the commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByC_C(
		long commerceShipmentId, long commerceOrderItemId, int start, int end) {

		return getPersistence().findByC_C(
			commerceShipmentId, commerceOrderItemId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByC_C(
		long commerceShipmentId, long commerceOrderItemId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().findByC_C(
			commerceShipmentId, commerceOrderItemId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByC_C(
		long commerceShipmentId, long commerceOrderItemId, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			commerceShipmentId, commerceOrderItemId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByC_C_First(
			long commerceShipmentId, long commerceOrderItemId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByC_C_First(
			commerceShipmentId, commerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByC_C_First(
		long commerceShipmentId, long commerceOrderItemId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			commerceShipmentId, commerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByC_C_Last(
			long commerceShipmentId, long commerceOrderItemId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByC_C_Last(
			commerceShipmentId, commerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByC_C_Last(
		long commerceShipmentId, long commerceOrderItemId,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByC_C_Last(
			commerceShipmentId, commerceOrderItemId, orderByComparator);
	}

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public static CommerceShipmentItem[] findByC_C_PrevAndNext(
			long commerceShipmentItemId, long commerceShipmentId,
			long commerceOrderItemId,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByC_C_PrevAndNext(
			commerceShipmentItemId, commerceShipmentId, commerceOrderItemId,
			orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63; from the database.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 */
	public static void removeByC_C(
		long commerceShipmentId, long commerceOrderItemId) {

		getPersistence().removeByC_C(commerceShipmentId, commerceOrderItemId);
	}

	/**
	 * Returns the number of commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the number of matching commerce shipment items
	 */
	public static int countByC_C(
		long commerceShipmentId, long commerceOrderItemId) {

		return getPersistence().countByC_C(
			commerceShipmentId, commerceOrderItemId);
	}

	/**
	 * Returns the commerce shipment item where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63; or throws a <code>NoSuchShipmentItemException</code> if it could not be found.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByC_C_C(
			long commerceShipmentId, long commerceOrderItemId,
			long commerceInventoryWarehouseId)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByC_C_C(
			commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId);
	}

	/**
	 * Returns the commerce shipment item where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByC_C_C(
		long commerceShipmentId, long commerceOrderItemId,
		long commerceInventoryWarehouseId) {

		return getPersistence().fetchByC_C_C(
			commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId);
	}

	/**
	 * Returns the commerce shipment item where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByC_C_C(
		long commerceShipmentId, long commerceOrderItemId,
		long commerceInventoryWarehouseId, boolean useFinderCache) {

		return getPersistence().fetchByC_C_C(
			commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId, useFinderCache);
	}

	/**
	 * Removes the commerce shipment item where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63; from the database.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the commerce shipment item that was removed
	 */
	public static CommerceShipmentItem removeByC_C_C(
			long commerceShipmentId, long commerceOrderItemId,
			long commerceInventoryWarehouseId)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().removeByC_C_C(
			commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId);
	}

	/**
	 * Returns the number of commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the number of matching commerce shipment items
	 */
	public static int countByC_C_C(
		long commerceShipmentId, long commerceOrderItemId,
		long commerceInventoryWarehouseId) {

		return getPersistence().countByC_C_C(
			commerceShipmentId, commerceOrderItemId,
			commerceInventoryWarehouseId);
	}

	/**
	 * Returns all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @return the matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity) {

		return getPersistence().findByC_NotC_GteQ(
			commerceShipmentId, commerceInventoryWarehouseId, quantity);
	}

	/**
	 * Returns a range of all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity, int start, int end) {

		return getPersistence().findByC_NotC_GteQ(
			commerceShipmentId, commerceInventoryWarehouseId, quantity, start,
			end);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().findByC_NotC_GteQ(
			commerceShipmentId, commerceInventoryWarehouseId, quantity, start,
			end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public static List<CommerceShipmentItem> findByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity, int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_NotC_GteQ(
			commerceShipmentId, commerceInventoryWarehouseId, quantity, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByC_NotC_GteQ_First(
			long commerceShipmentId, long commerceInventoryWarehouseId,
			BigDecimal quantity,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByC_NotC_GteQ_First(
			commerceShipmentId, commerceInventoryWarehouseId, quantity,
			orderByComparator);
	}

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByC_NotC_GteQ_First(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByC_NotC_GteQ_First(
			commerceShipmentId, commerceInventoryWarehouseId, quantity,
			orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByC_NotC_GteQ_Last(
			long commerceShipmentId, long commerceInventoryWarehouseId,
			BigDecimal quantity,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByC_NotC_GteQ_Last(
			commerceShipmentId, commerceInventoryWarehouseId, quantity,
			orderByComparator);
	}

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByC_NotC_GteQ_Last(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().fetchByC_NotC_GteQ_Last(
			commerceShipmentId, commerceInventoryWarehouseId, quantity,
			orderByComparator);
	}

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public static CommerceShipmentItem[] findByC_NotC_GteQ_PrevAndNext(
			long commerceShipmentItemId, long commerceShipmentId,
			long commerceInventoryWarehouseId, BigDecimal quantity,
			OrderByComparator<CommerceShipmentItem> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByC_NotC_GteQ_PrevAndNext(
			commerceShipmentItemId, commerceShipmentId,
			commerceInventoryWarehouseId, quantity, orderByComparator);
	}

	/**
	 * Removes all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63; from the database.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 */
	public static void removeByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity) {

		getPersistence().removeByC_NotC_GteQ(
			commerceShipmentId, commerceInventoryWarehouseId, quantity);
	}

	/**
	 * Returns the number of commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @return the number of matching commerce shipment items
	 */
	public static int countByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity) {

		return getPersistence().countByC_NotC_GteQ(
			commerceShipmentId, commerceInventoryWarehouseId, quantity);
	}

	/**
	 * Returns the commerce shipment item where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchShipmentItemException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce shipment item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce shipment item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public static CommerceShipmentItem fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the commerce shipment item where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce shipment item that was removed
	 */
	public static CommerceShipmentItem removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of commerce shipment items where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce shipment items
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Caches the commerce shipment item in the entity cache if it is enabled.
	 *
	 * @param commerceShipmentItem the commerce shipment item
	 */
	public static void cacheResult(CommerceShipmentItem commerceShipmentItem) {
		getPersistence().cacheResult(commerceShipmentItem);
	}

	/**
	 * Caches the commerce shipment items in the entity cache if it is enabled.
	 *
	 * @param commerceShipmentItems the commerce shipment items
	 */
	public static void cacheResult(
		List<CommerceShipmentItem> commerceShipmentItems) {

		getPersistence().cacheResult(commerceShipmentItems);
	}

	/**
	 * Creates a new commerce shipment item with the primary key. Does not add the commerce shipment item to the database.
	 *
	 * @param commerceShipmentItemId the primary key for the new commerce shipment item
	 * @return the new commerce shipment item
	 */
	public static CommerceShipmentItem create(long commerceShipmentItemId) {
		return getPersistence().create(commerceShipmentItemId);
	}

	/**
	 * Removes the commerce shipment item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceShipmentItemId the primary key of the commerce shipment item
	 * @return the commerce shipment item that was removed
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public static CommerceShipmentItem remove(long commerceShipmentItemId)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().remove(commerceShipmentItemId);
	}

	public static CommerceShipmentItem updateImpl(
		CommerceShipmentItem commerceShipmentItem) {

		return getPersistence().updateImpl(commerceShipmentItem);
	}

	/**
	 * Returns the commerce shipment item with the primary key or throws a <code>NoSuchShipmentItemException</code> if it could not be found.
	 *
	 * @param commerceShipmentItemId the primary key of the commerce shipment item
	 * @return the commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public static CommerceShipmentItem findByPrimaryKey(
			long commerceShipmentItemId)
		throws com.liferay.commerce.exception.NoSuchShipmentItemException {

		return getPersistence().findByPrimaryKey(commerceShipmentItemId);
	}

	/**
	 * Returns the commerce shipment item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceShipmentItemId the primary key of the commerce shipment item
	 * @return the commerce shipment item, or <code>null</code> if a commerce shipment item with the primary key could not be found
	 */
	public static CommerceShipmentItem fetchByPrimaryKey(
		long commerceShipmentItemId) {

		return getPersistence().fetchByPrimaryKey(commerceShipmentItemId);
	}

	/**
	 * Returns all the commerce shipment items.
	 *
	 * @return the commerce shipment items
	 */
	public static List<CommerceShipmentItem> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the commerce shipment items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of commerce shipment items
	 */
	public static List<CommerceShipmentItem> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce shipment items
	 */
	public static List<CommerceShipmentItem> findAll(
		int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce shipment items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce shipment items
	 */
	public static List<CommerceShipmentItem> findAll(
		int start, int end,
		OrderByComparator<CommerceShipmentItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the commerce shipment items from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of commerce shipment items.
	 *
	 * @return the number of commerce shipment items
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static CommerceShipmentItemPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CommerceShipmentItemPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CommerceShipmentItemPersistence _persistence;

}