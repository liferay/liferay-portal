/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce order service. This utility wraps <code>com.liferay.commerce.service.persistence.impl.CommerceOrderPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderPersistence
 * @generated
 */
public class CommerceOrderUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<CommerceOrder> commerceOrders) {
		getPersistence().cacheResult(commerceOrders);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(CommerceOrder commerceOrder) {
		getPersistence().cacheResult(commerceOrder);
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
	public static void clearCache(CommerceOrder commerceOrder) {
		getPersistence().clearCache(commerceOrder);
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
	public static Map<Serializable, CommerceOrder> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommerceOrder> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommerceOrder> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommerceOrder> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommerceOrder update(CommerceOrder commerceOrder) {
		return getPersistence().update(commerceOrder);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommerceOrder update(
		CommerceOrder commerceOrder, ServiceContext serviceContext) {

		return getPersistence().update(commerceOrder, serviceContext);
	}

	/**
	 * Returns an ordered range of all the commerce orders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByUuid_First(
			String uuid, OrderByComparator<CommerceOrder> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first commerce order in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByUuid_First(
		String uuid, OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the commerce orders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of commerce orders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce orders
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the commerce order where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchOrderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByUUID_G(String uuid, long groupId)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the commerce order where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the commerce order where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce order that was removed
	 */
	public static CommerceOrder removeByUUID_G(String uuid, long groupId)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of commerce orders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce orders
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first commerce order in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the commerce orders where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce orders
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByGroupId_First(
			long groupId, OrderByComparator<CommerceOrder> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByGroupId_First(
		long groupId, OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	public static List<CommerceOrder> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Removes all the commerce orders where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of commerce orders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce orders
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the commerce orders where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByUserId(
		long userId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByUserId_First(
			long userId, OrderByComparator<CommerceOrder> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first commerce order in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByUserId_First(
		long userId, OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Removes all the commerce orders where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of commerce orders where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching commerce orders
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns an ordered range of all the commerce orders where billingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param billingAddressId the billing address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByBillingAddressId(
		long billingAddressId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByBillingAddressId(
			billingAddressId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order in the ordered set where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByBillingAddressId_First(
			long billingAddressId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByBillingAddressId_First(
			billingAddressId, orderByComparator);
	}

	/**
	 * Returns the first commerce order in the ordered set where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByBillingAddressId_First(
		long billingAddressId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().fetchByBillingAddressId_First(
			billingAddressId, orderByComparator);
	}

	/**
	 * Removes all the commerce orders where billingAddressId = &#63; from the database.
	 *
	 * @param billingAddressId the billing address ID
	 */
	public static void removeByBillingAddressId(long billingAddressId) {
		getPersistence().removeByBillingAddressId(billingAddressId);
	}

	/**
	 * Returns the number of commerce orders where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @return the number of matching commerce orders
	 */
	public static int countByBillingAddressId(long billingAddressId) {
		return getPersistence().countByBillingAddressId(billingAddressId);
	}

	/**
	 * Returns an ordered range of all the commerce orders where commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByCommerceAccountId(
		long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCommerceAccountId(
			commerceAccountId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order in the ordered set where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByCommerceAccountId_First(
			long commerceAccountId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByCommerceAccountId_First(
			commerceAccountId, orderByComparator);
	}

	/**
	 * Returns the first commerce order in the ordered set where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByCommerceAccountId_First(
		long commerceAccountId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().fetchByCommerceAccountId_First(
			commerceAccountId, orderByComparator);
	}

	/**
	 * Removes all the commerce orders where commerceAccountId = &#63; from the database.
	 *
	 * @param commerceAccountId the commerce account ID
	 */
	public static void removeByCommerceAccountId(long commerceAccountId) {
		getPersistence().removeByCommerceAccountId(commerceAccountId);
	}

	/**
	 * Returns the number of commerce orders where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @return the number of matching commerce orders
	 */
	public static int countByCommerceAccountId(long commerceAccountId) {
		return getPersistence().countByCommerceAccountId(commerceAccountId);
	}

	/**
	 * Returns an ordered range of all the commerce orders where shippingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByShippingAddressId(
		long shippingAddressId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByShippingAddressId(
			shippingAddressId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order in the ordered set where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByShippingAddressId_First(
			long shippingAddressId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByShippingAddressId_First(
			shippingAddressId, orderByComparator);
	}

	/**
	 * Returns the first commerce order in the ordered set where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByShippingAddressId_First(
		long shippingAddressId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().fetchByShippingAddressId_First(
			shippingAddressId, orderByComparator);
	}

	/**
	 * Removes all the commerce orders where shippingAddressId = &#63; from the database.
	 *
	 * @param shippingAddressId the shipping address ID
	 */
	public static void removeByShippingAddressId(long shippingAddressId) {
		getPersistence().removeByShippingAddressId(shippingAddressId);
	}

	/**
	 * Returns the number of commerce orders where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @return the number of matching commerce orders
	 */
	public static int countByShippingAddressId(long shippingAddressId) {
		return getPersistence().countByShippingAddressId(shippingAddressId);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByG_C(
		long groupId, long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C(
			groupId, commerceAccountId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByG_C_First(
			long groupId, long commerceAccountId,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByG_C_First(
			groupId, commerceAccountId, orderByComparator);
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByG_C_First(
		long groupId, long commerceAccountId,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().fetchByG_C_First(
			groupId, commerceAccountId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	public static List<CommerceOrder> filterFindByG_C(
		long groupId, long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().filterFindByG_C(
			groupId, commerceAccountId, start, end, orderByComparator);
	}

	/**
	 * Removes all the commerce orders where groupId = &#63; and commerceAccountId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 */
	public static void removeByG_C(long groupId, long commerceAccountId) {
		getPersistence().removeByG_C(groupId, commerceAccountId);
	}

	/**
	 * Returns the number of commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @return the number of matching commerce orders
	 */
	public static int countByG_C(long groupId, long commerceAccountId) {
		return getPersistence().countByG_C(groupId, commerceAccountId);
	}

	/**
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	public static int filterCountByG_C(long groupId, long commerceAccountId) {
		return getPersistence().filterCountByG_C(groupId, commerceAccountId);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U_O(
			groupId, userId, orderStatus, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByG_U_O_First(
			long groupId, long userId, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByG_U_O_First(
			groupId, userId, orderStatus, orderByComparator);
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByG_U_O_First(
		long groupId, long userId, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().fetchByG_U_O_First(
			groupId, userId, orderStatus, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	public static List<CommerceOrder> filterFindByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().filterFindByG_U_O(
			groupId, userId, orderStatus, start, end, orderByComparator);
	}

	/**
	 * Removes all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 */
	public static void removeByG_U_O(
		long groupId, long userId, int orderStatus) {

		getPersistence().removeByG_U_O(groupId, userId, orderStatus);
	}

	/**
	 * Returns the number of commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders
	 */
	public static int countByG_U_O(long groupId, long userId, int orderStatus) {
		return getPersistence().countByG_U_O(groupId, userId, orderStatus);
	}

	/**
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	public static int filterCountByG_U_O(
		long groupId, long userId, int orderStatus) {

		return getPersistence().filterCountByG_U_O(
			groupId, userId, orderStatus);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end, OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_O(
			groupId, commerceAccountId, orderStatus, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByG_C_O_First(
			long groupId, long commerceAccountId, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByG_C_O_First(
			groupId, commerceAccountId, orderStatus, orderByComparator);
	}

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByG_C_O_First(
		long groupId, long commerceAccountId, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().fetchByG_C_O_First(
			groupId, commerceAccountId, orderStatus, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	public static List<CommerceOrder> filterFindByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end, OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().filterFindByG_C_O(
			groupId, commerceAccountId, orderStatus, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 */
	public static void removeByG_C_O(
		long groupId, long commerceAccountId, int orderStatus) {

		getPersistence().removeByG_C_O(groupId, commerceAccountId, orderStatus);
	}

	/**
	 * Returns the number of commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders
	 */
	public static int countByG_C_O(
		long groupId, long commerceAccountId, int orderStatus) {

		return getPersistence().countByG_C_O(
			groupId, commerceAccountId, orderStatus);
	}

	/**
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	public static int filterCountByG_C_O(
		long groupId, long commerceAccountId, int orderStatus) {

		return getPersistence().filterCountByG_C_O(
			groupId, commerceAccountId, orderStatus);
	}

	/**
	 * Returns all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders
	 */
	public static List<CommerceOrder> findByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus) {

		return getPersistence().findByC_LtC_O(
			createDate, commerceAccountId, orderStatus);
	}

	/**
	 * Returns a range of all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public static List<CommerceOrder> findByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus, int start,
		int end) {

		return getPersistence().findByC_LtC_O(
			createDate, commerceAccountId, orderStatus, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus, int start,
		int end, OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().findByC_LtC_O(
			createDate, commerceAccountId, orderStatus, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus, int start,
		int end, OrderByComparator<CommerceOrder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_LtC_O(
			createDate, commerceAccountId, orderStatus, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce order in the ordered set where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByC_LtC_O_First(
			Date createDate, long commerceAccountId, int orderStatus,
			OrderByComparator<CommerceOrder> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByC_LtC_O_First(
			createDate, commerceAccountId, orderStatus, orderByComparator);
	}

	/**
	 * Returns the first commerce order in the ordered set where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByC_LtC_O_First(
		Date createDate, long commerceAccountId, int orderStatus,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().fetchByC_LtC_O_First(
			createDate, commerceAccountId, orderStatus, orderByComparator);
	}

	/**
	 * Removes all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63; from the database.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 */
	public static void removeByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus) {

		getPersistence().removeByC_LtC_O(
			createDate, commerceAccountId, orderStatus);
	}

	/**
	 * Returns the number of commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders
	 */
	public static int countByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus) {

		return getPersistence().countByC_LtC_O(
			createDate, commerceAccountId, orderStatus);
	}

	/**
	 * Returns the commerce order where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOrderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public static CommerceOrder findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the commerce order where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce order that was removed
	 */
	public static CommerceOrder removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of commerce orders where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce orders
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new commerce order with the primary key. Does not add the commerce order to the database.
	 *
	 * @param commerceOrderId the primary key for the new commerce order
	 * @return the new commerce order
	 */
	public static CommerceOrder create(long commerceOrderId) {
		return getPersistence().create(commerceOrderId);
	}

	/**
	 * Removes the commerce order with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order that was removed
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	public static CommerceOrder remove(long commerceOrderId)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().remove(commerceOrderId);
	}

	public static CommerceOrder updateImpl(CommerceOrder commerceOrder) {
		return getPersistence().updateImpl(commerceOrder);
	}

	/**
	 * Returns the commerce order with the primary key or throws a <code>NoSuchOrderException</code> if it could not be found.
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	public static CommerceOrder findByPrimaryKey(long commerceOrderId)
		throws com.liferay.commerce.exception.NoSuchOrderException {

		return getPersistence().findByPrimaryKey(commerceOrderId);
	}

	/**
	 * Returns the commerce order with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order, or <code>null</code> if a commerce order with the primary key could not be found
	 */
	public static CommerceOrder fetchByPrimaryKey(long commerceOrderId) {
		return getPersistence().fetchByPrimaryKey(commerceOrderId);
	}

	/**
	 * Returns the commerce order where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the commerce order where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public static CommerceOrder fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the commerce orders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce orders
	 */
	public static List<CommerceOrder> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the commerce orders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public static List<CommerceOrder> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce orders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce orders
	 */
	public static List<CommerceOrder> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public static List<CommerceOrder> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce orders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce orders
	 */
	public static List<CommerceOrder> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public static List<CommerceOrder> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce orders that the user has permission to view
	 */
	public static List<CommerceOrder> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	public static List<CommerceOrder> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns all the commerce orders where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching commerce orders
	 */
	public static List<CommerceOrder> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns a range of all the commerce orders where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public static List<CommerceOrder> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce orders where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByUserId(
		long userId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce orders where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @return the matching commerce orders
	 */
	public static List<CommerceOrder> findByBillingAddressId(
		long billingAddressId) {

		return getPersistence().findByBillingAddressId(billingAddressId);
	}

	/**
	 * Returns a range of all the commerce orders where billingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param billingAddressId the billing address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public static List<CommerceOrder> findByBillingAddressId(
		long billingAddressId, int start, int end) {

		return getPersistence().findByBillingAddressId(
			billingAddressId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce orders where billingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param billingAddressId the billing address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByBillingAddressId(
		long billingAddressId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().findByBillingAddressId(
			billingAddressId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce orders where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @return the matching commerce orders
	 */
	public static List<CommerceOrder> findByCommerceAccountId(
		long commerceAccountId) {

		return getPersistence().findByCommerceAccountId(commerceAccountId);
	}

	/**
	 * Returns a range of all the commerce orders where commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public static List<CommerceOrder> findByCommerceAccountId(
		long commerceAccountId, int start, int end) {

		return getPersistence().findByCommerceAccountId(
			commerceAccountId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce orders where commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByCommerceAccountId(
		long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().findByCommerceAccountId(
			commerceAccountId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce orders where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @return the matching commerce orders
	 */
	public static List<CommerceOrder> findByShippingAddressId(
		long shippingAddressId) {

		return getPersistence().findByShippingAddressId(shippingAddressId);
	}

	/**
	 * Returns a range of all the commerce orders where shippingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public static List<CommerceOrder> findByShippingAddressId(
		long shippingAddressId, int start, int end) {

		return getPersistence().findByShippingAddressId(
			shippingAddressId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce orders where shippingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByShippingAddressId(
		long shippingAddressId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().findByShippingAddressId(
			shippingAddressId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @return the matching commerce orders
	 */
	public static List<CommerceOrder> findByG_C(
		long groupId, long commerceAccountId) {

		return getPersistence().findByG_C(groupId, commerceAccountId);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public static List<CommerceOrder> findByG_C(
		long groupId, long commerceAccountId, int start, int end) {

		return getPersistence().findByG_C(
			groupId, commerceAccountId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByG_C(
		long groupId, long commerceAccountId, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().findByG_C(
			groupId, commerceAccountId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @return the matching commerce orders that the user has permission to view
	 */
	public static List<CommerceOrder> filterFindByG_C(
		long groupId, long commerceAccountId) {

		return getPersistence().filterFindByG_C(groupId, commerceAccountId);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	public static List<CommerceOrder> filterFindByG_C(
		long groupId, long commerceAccountId, int start, int end) {

		return getPersistence().filterFindByG_C(
			groupId, commerceAccountId, start, end);
	}

	/**
	 * Returns all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders
	 */
	public static List<CommerceOrder> findByG_U_O(
		long groupId, long userId, int orderStatus) {

		return getPersistence().findByG_U_O(groupId, userId, orderStatus);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public static List<CommerceOrder> findByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end) {

		return getPersistence().findByG_U_O(
			groupId, userId, orderStatus, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end,
		OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().findByG_U_O(
			groupId, userId, orderStatus, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders that the user has permission to view
	 */
	public static List<CommerceOrder> filterFindByG_U_O(
		long groupId, long userId, int orderStatus) {

		return getPersistence().filterFindByG_U_O(groupId, userId, orderStatus);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	public static List<CommerceOrder> filterFindByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end) {

		return getPersistence().filterFindByG_U_O(
			groupId, userId, orderStatus, start, end);
	}

	/**
	 * Returns all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders
	 */
	public static List<CommerceOrder> findByG_C_O(
		long groupId, long commerceAccountId, int orderStatus) {

		return getPersistence().findByG_C_O(
			groupId, commerceAccountId, orderStatus);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public static List<CommerceOrder> findByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end) {

		return getPersistence().findByG_C_O(
			groupId, commerceAccountId, orderStatus, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public static List<CommerceOrder> findByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end, OrderByComparator<CommerceOrder> orderByComparator) {

		return getPersistence().findByG_C_O(
			groupId, commerceAccountId, orderStatus, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders that the user has permission to view
	 */
	public static List<CommerceOrder> filterFindByG_C_O(
		long groupId, long commerceAccountId, int orderStatus) {

		return getPersistence().filterFindByG_C_O(
			groupId, commerceAccountId, orderStatus);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	public static List<CommerceOrder> filterFindByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end) {

		return getPersistence().filterFindByG_C_O(
			groupId, commerceAccountId, orderStatus, start, end);
	}

	public static CommerceOrderPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(CommerceOrderPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile CommerceOrderPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-653001749