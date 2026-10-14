/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence;

import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce subscription entry service. This utility wraps <code>com.liferay.commerce.service.persistence.impl.CommerceSubscriptionEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceSubscriptionEntryPersistence
 * @generated
 */
public class CommerceSubscriptionEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CommerceSubscriptionEntry> commerceSubscriptionEntries) {

		getPersistence().cacheResult(commerceSubscriptionEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		CommerceSubscriptionEntry commerceSubscriptionEntry) {

		getPersistence().cacheResult(commerceSubscriptionEntry);
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
		CommerceSubscriptionEntry commerceSubscriptionEntry) {

		getPersistence().clearCache(commerceSubscriptionEntry);
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
	public static Map<Serializable, CommerceSubscriptionEntry>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommerceSubscriptionEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommerceSubscriptionEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommerceSubscriptionEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommerceSubscriptionEntry update(
		CommerceSubscriptionEntry commerceSubscriptionEntry) {

		return getPersistence().update(commerceSubscriptionEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommerceSubscriptionEntry update(
		CommerceSubscriptionEntry commerceSubscriptionEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(
			commerceSubscriptionEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the commerce subscription entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce subscription entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce subscription entry
	 * @throws NoSuchSubscriptionEntryException if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry findByUuid_First(
			String uuid,
			OrderByComparator<CommerceSubscriptionEntry> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first commerce subscription entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the commerce subscription entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of commerce subscription entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce subscription entries
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the commerce subscription entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchSubscriptionEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce subscription entry
	 * @throws NoSuchSubscriptionEntryException if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry findByUUID_G(
			String uuid, long groupId)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the commerce subscription entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the commerce subscription entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce subscription entry that was removed
	 */
	public static CommerceSubscriptionEntry removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of commerce subscription entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce subscription entries
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the commerce subscription entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce subscription entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce subscription entry
	 * @throws NoSuchSubscriptionEntryException if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceSubscriptionEntry> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first commerce subscription entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the commerce subscription entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of commerce subscription entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce subscription entries
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the commerce subscription entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce subscription entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce subscription entry
	 * @throws NoSuchSubscriptionEntryException if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry findByGroupId_First(
			long groupId,
			OrderByComparator<CommerceSubscriptionEntry> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first commerce subscription entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the commerce subscription entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of commerce subscription entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce subscription entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the commerce subscription entry where commerceOrderItemId = &#63; or throws a <code>NoSuchSubscriptionEntryException</code> if it could not be found.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the matching commerce subscription entry
	 * @throws NoSuchSubscriptionEntryException if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry findByCommerceOrderItemId(
			long commerceOrderItemId)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().findByCommerceOrderItemId(commerceOrderItemId);
	}

	/**
	 * Returns the commerce subscription entry where commerceOrderItemId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry fetchByCommerceOrderItemId(
		long commerceOrderItemId, boolean useFinderCache) {

		return getPersistence().fetchByCommerceOrderItemId(
			commerceOrderItemId, useFinderCache);
	}

	/**
	 * Removes the commerce subscription entry where commerceOrderItemId = &#63; from the database.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the commerce subscription entry that was removed
	 */
	public static CommerceSubscriptionEntry removeByCommerceOrderItemId(
			long commerceOrderItemId)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().removeByCommerceOrderItemId(
			commerceOrderItemId);
	}

	/**
	 * Returns the number of commerce subscription entries where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the number of matching commerce subscription entries
	 */
	public static int countByCommerceOrderItemId(long commerceOrderItemId) {
		return getPersistence().countByCommerceOrderItemId(commerceOrderItemId);
	}

	/**
	 * Returns an ordered range of all the commerce subscription entries where subscriptionStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param subscriptionStatus the subscription status
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findBySubscriptionStatus(
		int subscriptionStatus, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findBySubscriptionStatus(
			subscriptionStatus, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce subscription entry in the ordered set where subscriptionStatus = &#63;.
	 *
	 * @param subscriptionStatus the subscription status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce subscription entry
	 * @throws NoSuchSubscriptionEntryException if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry findBySubscriptionStatus_First(
			int subscriptionStatus,
			OrderByComparator<CommerceSubscriptionEntry> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().findBySubscriptionStatus_First(
			subscriptionStatus, orderByComparator);
	}

	/**
	 * Returns the first commerce subscription entry in the ordered set where subscriptionStatus = &#63;.
	 *
	 * @param subscriptionStatus the subscription status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry fetchBySubscriptionStatus_First(
		int subscriptionStatus,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().fetchBySubscriptionStatus_First(
			subscriptionStatus, orderByComparator);
	}

	/**
	 * Removes all the commerce subscription entries where subscriptionStatus = &#63; from the database.
	 *
	 * @param subscriptionStatus the subscription status
	 */
	public static void removeBySubscriptionStatus(int subscriptionStatus) {
		getPersistence().removeBySubscriptionStatus(subscriptionStatus);
	}

	/**
	 * Returns the number of commerce subscription entries where subscriptionStatus = &#63;.
	 *
	 * @param subscriptionStatus the subscription status
	 * @return the number of matching commerce subscription entries
	 */
	public static int countBySubscriptionStatus(int subscriptionStatus) {
		return getPersistence().countBySubscriptionStatus(subscriptionStatus);
	}

	/**
	 * Returns an ordered range of all the commerce subscription entries where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_U(
			companyId, userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce subscription entry in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce subscription entry
	 * @throws NoSuchSubscriptionEntryException if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry findByC_U_First(
			long companyId, long userId,
			OrderByComparator<CommerceSubscriptionEntry> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().findByC_U_First(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the first commerce subscription entry in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().fetchByC_U_First(
			companyId, userId, orderByComparator);
	}

	/**
	 * Removes all the commerce subscription entries where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public static void removeByC_U(long companyId, long userId) {
		getPersistence().removeByC_U(companyId, userId);
	}

	/**
	 * Returns the number of commerce subscription entries where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching commerce subscription entries
	 */
	public static int countByC_U(long companyId, long userId) {
		return getPersistence().countByC_U(companyId, userId);
	}

	/**
	 * Returns an ordered range of all the commerce subscription entries where groupId = &#63; and companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByG_C_U(
		long groupId, long companyId, long userId, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_U(
			groupId, companyId, userId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce subscription entry in the ordered set where groupId = &#63; and companyId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce subscription entry
	 * @throws NoSuchSubscriptionEntryException if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry findByG_C_U_First(
			long groupId, long companyId, long userId,
			OrderByComparator<CommerceSubscriptionEntry> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().findByG_C_U_First(
			groupId, companyId, userId, orderByComparator);
	}

	/**
	 * Returns the first commerce subscription entry in the ordered set where groupId = &#63; and companyId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry fetchByG_C_U_First(
		long groupId, long companyId, long userId,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().fetchByG_C_U_First(
			groupId, companyId, userId, orderByComparator);
	}

	/**
	 * Removes all the commerce subscription entries where groupId = &#63; and companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public static void removeByG_C_U(
		long groupId, long companyId, long userId) {

		getPersistence().removeByG_C_U(groupId, companyId, userId);
	}

	/**
	 * Returns the number of commerce subscription entries where groupId = &#63; and companyId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching commerce subscription entries
	 */
	public static int countByG_C_U(long groupId, long companyId, long userId) {
		return getPersistence().countByG_C_U(groupId, companyId, userId);
	}

	/**
	 * Returns the commerce subscription entry where CPInstanceUuid = &#63; and CProductId = &#63; and commerceOrderItemId = &#63; or throws a <code>NoSuchSubscriptionEntryException</code> if it could not be found.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param CProductId the c product ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the matching commerce subscription entry
	 * @throws NoSuchSubscriptionEntryException if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry findByC_C_C(
			String CPInstanceUuid, long CProductId, long commerceOrderItemId)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().findByC_C_C(
			CPInstanceUuid, CProductId, commerceOrderItemId);
	}

	/**
	 * Returns the commerce subscription entry where CPInstanceUuid = &#63; and CProductId = &#63; and commerceOrderItemId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param CProductId the c product ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry fetchByC_C_C(
		String CPInstanceUuid, long CProductId, long commerceOrderItemId,
		boolean useFinderCache) {

		return getPersistence().fetchByC_C_C(
			CPInstanceUuid, CProductId, commerceOrderItemId, useFinderCache);
	}

	/**
	 * Removes the commerce subscription entry where CPInstanceUuid = &#63; and CProductId = &#63; and commerceOrderItemId = &#63; from the database.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param CProductId the c product ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the commerce subscription entry that was removed
	 */
	public static CommerceSubscriptionEntry removeByC_C_C(
			String CPInstanceUuid, long CProductId, long commerceOrderItemId)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().removeByC_C_C(
			CPInstanceUuid, CProductId, commerceOrderItemId);
	}

	/**
	 * Returns the number of commerce subscription entries where CPInstanceUuid = &#63; and CProductId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param CProductId the c product ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the number of matching commerce subscription entries
	 */
	public static int countByC_C_C(
		String CPInstanceUuid, long CProductId, long commerceOrderItemId) {

		return getPersistence().countByC_C_C(
			CPInstanceUuid, CProductId, commerceOrderItemId);
	}

	/**
	 * Creates a new commerce subscription entry with the primary key. Does not add the commerce subscription entry to the database.
	 *
	 * @param commerceSubscriptionEntryId the primary key for the new commerce subscription entry
	 * @return the new commerce subscription entry
	 */
	public static CommerceSubscriptionEntry create(
		long commerceSubscriptionEntryId) {

		return getPersistence().create(commerceSubscriptionEntryId);
	}

	/**
	 * Removes the commerce subscription entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceSubscriptionEntryId the primary key of the commerce subscription entry
	 * @return the commerce subscription entry that was removed
	 * @throws NoSuchSubscriptionEntryException if a commerce subscription entry with the primary key could not be found
	 */
	public static CommerceSubscriptionEntry remove(
			long commerceSubscriptionEntryId)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().remove(commerceSubscriptionEntryId);
	}

	public static CommerceSubscriptionEntry updateImpl(
		CommerceSubscriptionEntry commerceSubscriptionEntry) {

		return getPersistence().updateImpl(commerceSubscriptionEntry);
	}

	/**
	 * Returns the commerce subscription entry with the primary key or throws a <code>NoSuchSubscriptionEntryException</code> if it could not be found.
	 *
	 * @param commerceSubscriptionEntryId the primary key of the commerce subscription entry
	 * @return the commerce subscription entry
	 * @throws NoSuchSubscriptionEntryException if a commerce subscription entry with the primary key could not be found
	 */
	public static CommerceSubscriptionEntry findByPrimaryKey(
			long commerceSubscriptionEntryId)
		throws com.liferay.commerce.exception.NoSuchSubscriptionEntryException {

		return getPersistence().findByPrimaryKey(commerceSubscriptionEntryId);
	}

	/**
	 * Returns the commerce subscription entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceSubscriptionEntryId the primary key of the commerce subscription entry
	 * @return the commerce subscription entry, or <code>null</code> if a commerce subscription entry with the primary key could not be found
	 */
	public static CommerceSubscriptionEntry fetchByPrimaryKey(
		long commerceSubscriptionEntryId) {

		return getPersistence().fetchByPrimaryKey(commerceSubscriptionEntryId);
	}

	/**
	 * Returns the commerce subscription entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the commerce subscription entry where commerceOrderItemId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry fetchByCommerceOrderItemId(
		long commerceOrderItemId) {

		return getPersistence().fetchByCommerceOrderItemId(commerceOrderItemId);
	}

	/**
	 * Returns the commerce subscription entry where CPInstanceUuid = &#63; and CProductId = &#63; and commerceOrderItemId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param CProductId the c product ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry fetchByC_C_C(
		String CPInstanceUuid, long CProductId, long commerceOrderItemId) {

		return getPersistence().fetchByC_C_C(
			CPInstanceUuid, CProductId, commerceOrderItemId);
	}

	/**
	 * Returns all the commerce subscription entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the commerce subscription entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @return the range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce subscription entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce subscription entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce subscription entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @return the range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce subscription entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce subscription entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the commerce subscription entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @return the range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce subscription entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce subscription entries where subscriptionStatus = &#63;.
	 *
	 * @param subscriptionStatus the subscription status
	 * @return the matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findBySubscriptionStatus(
		int subscriptionStatus) {

		return getPersistence().findBySubscriptionStatus(subscriptionStatus);
	}

	/**
	 * Returns a range of all the commerce subscription entries where subscriptionStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param subscriptionStatus the subscription status
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @return the range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findBySubscriptionStatus(
		int subscriptionStatus, int start, int end) {

		return getPersistence().findBySubscriptionStatus(
			subscriptionStatus, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce subscription entries where subscriptionStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param subscriptionStatus the subscription status
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findBySubscriptionStatus(
		int subscriptionStatus, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().findBySubscriptionStatus(
			subscriptionStatus, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce subscription entries where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByC_U(
		long companyId, long userId) {

		return getPersistence().findByC_U(companyId, userId);
	}

	/**
	 * Returns a range of all the commerce subscription entries where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @return the range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByC_U(
		long companyId, long userId, int start, int end) {

		return getPersistence().findByC_U(companyId, userId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce subscription entries where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().findByC_U(
			companyId, userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce subscription entries where groupId = &#63; and companyId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByG_C_U(
		long groupId, long companyId, long userId) {

		return getPersistence().findByG_C_U(groupId, companyId, userId);
	}

	/**
	 * Returns a range of all the commerce subscription entries where groupId = &#63; and companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @return the range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByG_C_U(
		long groupId, long companyId, long userId, int start, int end) {

		return getPersistence().findByG_C_U(
			groupId, companyId, userId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce subscription entries where groupId = &#63; and companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry> findByG_C_U(
		long groupId, long companyId, long userId, int start, int end,
		OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getPersistence().findByG_C_U(
			groupId, companyId, userId, start, end, orderByComparator);
	}

	public static CommerceSubscriptionEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CommerceSubscriptionEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CommerceSubscriptionEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:927724346