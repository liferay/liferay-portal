/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence;

import com.liferay.commerce.discount.model.CommerceDiscountUsageEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce discount usage entry service. This utility wraps <code>com.liferay.commerce.discount.service.persistence.impl.CommerceDiscountUsageEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CommerceDiscountUsageEntryPersistence
 * @generated
 */
public class CommerceDiscountUsageEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CommerceDiscountUsageEntry> commerceDiscountUsageEntries) {

		getPersistence().cacheResult(commerceDiscountUsageEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		CommerceDiscountUsageEntry commerceDiscountUsageEntry) {

		getPersistence().cacheResult(commerceDiscountUsageEntry);
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
		CommerceDiscountUsageEntry commerceDiscountUsageEntry) {

		getPersistence().clearCache(commerceDiscountUsageEntry);
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
	public static Map<Serializable, CommerceDiscountUsageEntry>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommerceDiscountUsageEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommerceDiscountUsageEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommerceDiscountUsageEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommerceDiscountUsageEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommerceDiscountUsageEntry update(
		CommerceDiscountUsageEntry commerceDiscountUsageEntry) {

		return getPersistence().update(commerceDiscountUsageEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommerceDiscountUsageEntry update(
		CommerceDiscountUsageEntry commerceDiscountUsageEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(
			commerceDiscountUsageEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the commerce discount usage entries where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountUsageEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCommerceDiscountId(
			commerceDiscountId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount usage entry in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount usage entry
	 * @throws NoSuchDiscountUsageEntryException if a matching commerce discount usage entry could not be found
	 */
	public static CommerceDiscountUsageEntry findByCommerceDiscountId_First(
			long commerceDiscountId,
			OrderByComparator<CommerceDiscountUsageEntry> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountUsageEntryException {

		return getPersistence().findByCommerceDiscountId_First(
			commerceDiscountId, orderByComparator);
	}

	/**
	 * Returns the first commerce discount usage entry in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount usage entry, or <code>null</code> if a matching commerce discount usage entry could not be found
	 */
	public static CommerceDiscountUsageEntry fetchByCommerceDiscountId_First(
		long commerceDiscountId,
		OrderByComparator<CommerceDiscountUsageEntry> orderByComparator) {

		return getPersistence().fetchByCommerceDiscountId_First(
			commerceDiscountId, orderByComparator);
	}

	/**
	 * Removes all the commerce discount usage entries where commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 */
	public static void removeByCommerceDiscountId(long commerceDiscountId) {
		getPersistence().removeByCommerceDiscountId(commerceDiscountId);
	}

	/**
	 * Returns the number of commerce discount usage entries where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount usage entries
	 */
	public static int countByCommerceDiscountId(long commerceDiscountId) {
		return getPersistence().countByCommerceDiscountId(commerceDiscountId);
	}

	/**
	 * Returns an ordered range of all the commerce discount usage entries where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry> findByCAI_CDI(
		long commerceAccountId, long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountUsageEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCAI_CDI(
			commerceAccountId, commerceDiscountId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount usage entry in the ordered set where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount usage entry
	 * @throws NoSuchDiscountUsageEntryException if a matching commerce discount usage entry could not be found
	 */
	public static CommerceDiscountUsageEntry findByCAI_CDI_First(
			long commerceAccountId, long commerceDiscountId,
			OrderByComparator<CommerceDiscountUsageEntry> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountUsageEntryException {

		return getPersistence().findByCAI_CDI_First(
			commerceAccountId, commerceDiscountId, orderByComparator);
	}

	/**
	 * Returns the first commerce discount usage entry in the ordered set where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount usage entry, or <code>null</code> if a matching commerce discount usage entry could not be found
	 */
	public static CommerceDiscountUsageEntry fetchByCAI_CDI_First(
		long commerceAccountId, long commerceDiscountId,
		OrderByComparator<CommerceDiscountUsageEntry> orderByComparator) {

		return getPersistence().fetchByCAI_CDI_First(
			commerceAccountId, commerceDiscountId, orderByComparator);
	}

	/**
	 * Removes all the commerce discount usage entries where commerceAccountId = &#63; and commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 */
	public static void removeByCAI_CDI(
		long commerceAccountId, long commerceDiscountId) {

		getPersistence().removeByCAI_CDI(commerceAccountId, commerceDiscountId);
	}

	/**
	 * Returns the number of commerce discount usage entries where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount usage entries
	 */
	public static int countByCAI_CDI(
		long commerceAccountId, long commerceDiscountId) {

		return getPersistence().countByCAI_CDI(
			commerceAccountId, commerceDiscountId);
	}

	/**
	 * Returns an ordered range of all the commerce discount usage entries where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry> findByCAI_COI_CDI(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId,
		int start, int end,
		OrderByComparator<CommerceDiscountUsageEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCAI_COI_CDI(
			commerceAccountId, commerceOrderId, commerceDiscountId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount usage entry in the ordered set where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount usage entry
	 * @throws NoSuchDiscountUsageEntryException if a matching commerce discount usage entry could not be found
	 */
	public static CommerceDiscountUsageEntry findByCAI_COI_CDI_First(
			long commerceAccountId, long commerceOrderId,
			long commerceDiscountId,
			OrderByComparator<CommerceDiscountUsageEntry> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountUsageEntryException {

		return getPersistence().findByCAI_COI_CDI_First(
			commerceAccountId, commerceOrderId, commerceDiscountId,
			orderByComparator);
	}

	/**
	 * Returns the first commerce discount usage entry in the ordered set where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount usage entry, or <code>null</code> if a matching commerce discount usage entry could not be found
	 */
	public static CommerceDiscountUsageEntry fetchByCAI_COI_CDI_First(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId,
		OrderByComparator<CommerceDiscountUsageEntry> orderByComparator) {

		return getPersistence().fetchByCAI_COI_CDI_First(
			commerceAccountId, commerceOrderId, commerceDiscountId,
			orderByComparator);
	}

	/**
	 * Removes all the commerce discount usage entries where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 */
	public static void removeByCAI_COI_CDI(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId) {

		getPersistence().removeByCAI_COI_CDI(
			commerceAccountId, commerceOrderId, commerceDiscountId);
	}

	/**
	 * Returns the number of commerce discount usage entries where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount usage entries
	 */
	public static int countByCAI_COI_CDI(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId) {

		return getPersistence().countByCAI_COI_CDI(
			commerceAccountId, commerceOrderId, commerceDiscountId);
	}

	/**
	 * Creates a new commerce discount usage entry with the primary key. Does not add the commerce discount usage entry to the database.
	 *
	 * @param commerceDiscountUsageEntryId the primary key for the new commerce discount usage entry
	 * @return the new commerce discount usage entry
	 */
	public static CommerceDiscountUsageEntry create(
		long commerceDiscountUsageEntryId) {

		return getPersistence().create(commerceDiscountUsageEntryId);
	}

	/**
	 * Removes the commerce discount usage entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountUsageEntryId the primary key of the commerce discount usage entry
	 * @return the commerce discount usage entry that was removed
	 * @throws NoSuchDiscountUsageEntryException if a commerce discount usage entry with the primary key could not be found
	 */
	public static CommerceDiscountUsageEntry remove(
			long commerceDiscountUsageEntryId)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountUsageEntryException {

		return getPersistence().remove(commerceDiscountUsageEntryId);
	}

	public static CommerceDiscountUsageEntry updateImpl(
		CommerceDiscountUsageEntry commerceDiscountUsageEntry) {

		return getPersistence().updateImpl(commerceDiscountUsageEntry);
	}

	/**
	 * Returns the commerce discount usage entry with the primary key or throws a <code>NoSuchDiscountUsageEntryException</code> if it could not be found.
	 *
	 * @param commerceDiscountUsageEntryId the primary key of the commerce discount usage entry
	 * @return the commerce discount usage entry
	 * @throws NoSuchDiscountUsageEntryException if a commerce discount usage entry with the primary key could not be found
	 */
	public static CommerceDiscountUsageEntry findByPrimaryKey(
			long commerceDiscountUsageEntryId)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountUsageEntryException {

		return getPersistence().findByPrimaryKey(commerceDiscountUsageEntryId);
	}

	/**
	 * Returns the commerce discount usage entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountUsageEntryId the primary key of the commerce discount usage entry
	 * @return the commerce discount usage entry, or <code>null</code> if a commerce discount usage entry with the primary key could not be found
	 */
	public static CommerceDiscountUsageEntry fetchByPrimaryKey(
		long commerceDiscountUsageEntryId) {

		return getPersistence().fetchByPrimaryKey(commerceDiscountUsageEntryId);
	}

	/**
	 * Returns all the commerce discount usage entries where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the matching commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry> findByCommerceDiscountId(
		long commerceDiscountId) {

		return getPersistence().findByCommerceDiscountId(commerceDiscountId);
	}

	/**
	 * Returns a range of all the commerce discount usage entries where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @return the range of matching commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end) {

		return getPersistence().findByCommerceDiscountId(
			commerceDiscountId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce discount usage entries where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountUsageEntry> orderByComparator) {

		return getPersistence().findByCommerceDiscountId(
			commerceDiscountId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce discount usage entries where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @return the matching commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry> findByCAI_CDI(
		long commerceAccountId, long commerceDiscountId) {

		return getPersistence().findByCAI_CDI(
			commerceAccountId, commerceDiscountId);
	}

	/**
	 * Returns a range of all the commerce discount usage entries where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @return the range of matching commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry> findByCAI_CDI(
		long commerceAccountId, long commerceDiscountId, int start, int end) {

		return getPersistence().findByCAI_CDI(
			commerceAccountId, commerceDiscountId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce discount usage entries where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry> findByCAI_CDI(
		long commerceAccountId, long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountUsageEntry> orderByComparator) {

		return getPersistence().findByCAI_CDI(
			commerceAccountId, commerceDiscountId, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the commerce discount usage entries where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @return the matching commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry> findByCAI_COI_CDI(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId) {

		return getPersistence().findByCAI_COI_CDI(
			commerceAccountId, commerceOrderId, commerceDiscountId);
	}

	/**
	 * Returns a range of all the commerce discount usage entries where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @return the range of matching commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry> findByCAI_COI_CDI(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId,
		int start, int end) {

		return getPersistence().findByCAI_COI_CDI(
			commerceAccountId, commerceOrderId, commerceDiscountId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce discount usage entries where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount usage entries
	 */
	public static List<CommerceDiscountUsageEntry> findByCAI_COI_CDI(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId,
		int start, int end,
		OrderByComparator<CommerceDiscountUsageEntry> orderByComparator) {

		return getPersistence().findByCAI_COI_CDI(
			commerceAccountId, commerceOrderId, commerceDiscountId, start, end,
			orderByComparator);
	}

	public static CommerceDiscountUsageEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CommerceDiscountUsageEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CommerceDiscountUsageEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1885905740