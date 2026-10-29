/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence;

import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce price entry service. This utility wraps <code>com.liferay.commerce.price.list.service.persistence.impl.CommercePriceEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommercePriceEntryPersistence
 * @generated
 */
public class CommercePriceEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CommercePriceEntry> commercePriceEntries) {

		getPersistence().cacheResult(commercePriceEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(CommercePriceEntry commercePriceEntry) {
		getPersistence().cacheResult(commercePriceEntry);
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
	public static void clearCache(CommercePriceEntry commercePriceEntry) {
		getPersistence().clearCache(commercePriceEntry);
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
	public static Map<Serializable, CommercePriceEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommercePriceEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommercePriceEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommercePriceEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommercePriceEntry update(
		CommercePriceEntry commercePriceEntry) {

		return getPersistence().update(commercePriceEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommercePriceEntry update(
		CommercePriceEntry commercePriceEntry, ServiceContext serviceContext) {

		return getPersistence().update(commercePriceEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry findByUuid_First(
			String uuid,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry fetchByUuid_First(
		String uuid, OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of commerce price entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce price entries
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of commerce price entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce price entries
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where commercePriceListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByCommercePriceListId(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCommercePriceListId(
			commercePriceListId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry findByCommercePriceListId_First(
			long commercePriceListId,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().findByCommercePriceListId_First(
			commercePriceListId, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry fetchByCommercePriceListId_First(
		long commercePriceListId,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().fetchByCommercePriceListId_First(
			commercePriceListId, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where commercePriceListId = &#63; from the database.
	 *
	 * @param commercePriceListId the commerce price list ID
	 */
	public static void removeByCommercePriceListId(long commercePriceListId) {
		getPersistence().removeByCommercePriceListId(commercePriceListId);
	}

	/**
	 * Returns the number of commerce price entries where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @return the number of matching commerce price entries
	 */
	public static int countByCommercePriceListId(long commercePriceListId) {
		return getPersistence().countByCommercePriceListId(commercePriceListId);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPInstanceUuid(
			CPInstanceUuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry findByCPInstanceUuid_First(
			String CPInstanceUuid,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().findByCPInstanceUuid_First(
			CPInstanceUuid, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry fetchByCPInstanceUuid_First(
		String CPInstanceUuid,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().fetchByCPInstanceUuid_First(
			CPInstanceUuid, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where CPInstanceUuid = &#63; from the database.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 */
	public static void removeByCPInstanceUuid(String CPInstanceUuid) {
		getPersistence().removeByCPInstanceUuid(CPInstanceUuid);
	}

	/**
	 * Returns the number of commerce price entries where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching commerce price entries
	 */
	public static int countByCPInstanceUuid(String CPInstanceUuid) {
		return getPersistence().countByCPInstanceUuid(CPInstanceUuid);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByC_C(
		long commercePriceListId, String CPInstanceUuid, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			commercePriceListId, CPInstanceUuid, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where commercePriceListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry findByC_C_First(
			long commercePriceListId, String CPInstanceUuid,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().findByC_C_First(
			commercePriceListId, CPInstanceUuid, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where commercePriceListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry fetchByC_C_First(
		long commercePriceListId, String CPInstanceUuid,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			commercePriceListId, CPInstanceUuid, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63; from the database.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 */
	public static void removeByC_C(
		long commercePriceListId, String CPInstanceUuid) {

		getPersistence().removeByC_C(commercePriceListId, CPInstanceUuid);
	}

	/**
	 * Returns the number of commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching commerce price entries
	 */
	public static int countByC_C(
		long commercePriceListId, String CPInstanceUuid) {

		return getPersistence().countByC_C(commercePriceListId, CPInstanceUuid);
	}

	/**
	 * Returns all the commerce price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByLtD_S(
		Date displayDate, int status) {

		return getPersistence().findByLtD_S(displayDate, status);
	}

	/**
	 * Returns a range of all the commerce price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @return the range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return getPersistence().findByLtD_S(displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().findByLtD_S(
			displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtD_S(
			displayDate, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().findByLtD_S_First(
			displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().fetchByLtD_S_First(
			displayDate, status, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByLtD_S(Date displayDate, int status) {
		getPersistence().removeByLtD_S(displayDate, status);
	}

	/**
	 * Returns the number of commerce price entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching commerce price entries
	 */
	public static int countByLtD_S(Date displayDate, int status) {
		return getPersistence().countByLtD_S(displayDate, status);
	}

	/**
	 * Returns all the commerce price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByLtE_S(
		Date expirationDate, int status) {

		return getPersistence().findByLtE_S(expirationDate, status);
	}

	/**
	 * Returns a range of all the commerce price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @return the range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return getPersistence().findByLtE_S(expirationDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().findByLtE_S(
			expirationDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtE_S(
			expirationDate, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry findByLtE_S_First(
			Date expirationDate, int status,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().findByLtE_S_First(
			expirationDate, status, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry fetchByLtE_S_First(
		Date expirationDate, int status,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().fetchByLtE_S_First(
			expirationDate, status, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where expirationDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 */
	public static void removeByLtE_S(Date expirationDate, int status) {
		getPersistence().removeByLtE_S(expirationDate, status);
	}

	/**
	 * Returns the number of commerce price entries where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching commerce price entries
	 */
	public static int countByLtE_S(Date expirationDate, int status) {
		return getPersistence().countByLtE_S(expirationDate, status);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByC_C_S(
		long commercePriceListId, String CPInstanceUuid, int status, int start,
		int end, OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C_S(
			commercePriceListId, CPInstanceUuid, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry findByC_C_S_First(
			long commercePriceListId, String CPInstanceUuid, int status,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().findByC_C_S_First(
			commercePriceListId, CPInstanceUuid, status, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry fetchByC_C_S_First(
		long commercePriceListId, String CPInstanceUuid, int status,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().fetchByC_C_S_First(
			commercePriceListId, CPInstanceUuid, status, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63; from the database.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 */
	public static void removeByC_C_S(
		long commercePriceListId, String CPInstanceUuid, int status) {

		getPersistence().removeByC_C_S(
			commercePriceListId, CPInstanceUuid, status);
	}

	/**
	 * Returns the number of commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 * @return the number of matching commerce price entries
	 */
	public static int countByC_C_S(
		long commercePriceListId, String CPInstanceUuid, int status) {

		return getPersistence().countByC_C_S(
			commercePriceListId, CPInstanceUuid, status);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByC_Q_U(
		String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey,
		int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_Q_U(
			CPInstanceUuid, quantity, unitOfMeasureKey, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry findByC_Q_U_First(
			String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().findByC_Q_U_First(
			CPInstanceUuid, quantity, unitOfMeasureKey, orderByComparator);
	}

	/**
	 * Returns the first commerce price entry in the ordered set where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry fetchByC_Q_U_First(
		String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().fetchByC_Q_U_First(
			CPInstanceUuid, quantity, unitOfMeasureKey, orderByComparator);
	}

	/**
	 * Removes all the commerce price entries where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63; from the database.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 */
	public static void removeByC_Q_U(
		String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey) {

		getPersistence().removeByC_Q_U(
			CPInstanceUuid, quantity, unitOfMeasureKey);
	}

	/**
	 * Returns the number of commerce price entries where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the number of matching commerce price entries
	 */
	public static int countByC_Q_U(
		String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey) {

		return getPersistence().countByC_Q_U(
			CPInstanceUuid, quantity, unitOfMeasureKey);
	}

	/**
	 * Returns the commerce price entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchPriceEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce price entry
	 * @throws NoSuchPriceEntryException if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce price entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the commerce price entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce price entry that was removed
	 */
	public static CommercePriceEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of commerce price entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce price entries
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new commerce price entry with the primary key. Does not add the commerce price entry to the database.
	 *
	 * @param commercePriceEntryId the primary key for the new commerce price entry
	 * @return the new commerce price entry
	 */
	public static CommercePriceEntry create(long commercePriceEntryId) {
		return getPersistence().create(commercePriceEntryId);
	}

	/**
	 * Removes the commerce price entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commercePriceEntryId the primary key of the commerce price entry
	 * @return the commerce price entry that was removed
	 * @throws NoSuchPriceEntryException if a commerce price entry with the primary key could not be found
	 */
	public static CommercePriceEntry remove(long commercePriceEntryId)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().remove(commercePriceEntryId);
	}

	public static CommercePriceEntry updateImpl(
		CommercePriceEntry commercePriceEntry) {

		return getPersistence().updateImpl(commercePriceEntry);
	}

	/**
	 * Returns the commerce price entry with the primary key or throws a <code>NoSuchPriceEntryException</code> if it could not be found.
	 *
	 * @param commercePriceEntryId the primary key of the commerce price entry
	 * @return the commerce price entry
	 * @throws NoSuchPriceEntryException if a commerce price entry with the primary key could not be found
	 */
	public static CommercePriceEntry findByPrimaryKey(long commercePriceEntryId)
		throws com.liferay.commerce.price.list.exception.
			NoSuchPriceEntryException {

		return getPersistence().findByPrimaryKey(commercePriceEntryId);
	}

	/**
	 * Returns the commerce price entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commercePriceEntryId the primary key of the commerce price entry
	 * @return the commerce price entry, or <code>null</code> if a commerce price entry with the primary key could not be found
	 */
	public static CommercePriceEntry fetchByPrimaryKey(
		long commercePriceEntryId) {

		return getPersistence().fetchByPrimaryKey(commercePriceEntryId);
	}

	/**
	 * Returns the commerce price entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce price entry, or <code>null</code> if a matching commerce price entry could not be found
	 */
	public static CommercePriceEntry fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the commerce price entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the commerce price entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @return the range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce price entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce price entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @return the range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce price entries where commercePriceListId = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @return the matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByCommercePriceListId(
		long commercePriceListId) {

		return getPersistence().findByCommercePriceListId(commercePriceListId);
	}

	/**
	 * Returns a range of all the commerce price entries where commercePriceListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @return the range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByCommercePriceListId(
		long commercePriceListId, int start, int end) {

		return getPersistence().findByCommercePriceListId(
			commercePriceListId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where commercePriceListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByCommercePriceListId(
		long commercePriceListId, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().findByCommercePriceListId(
			commercePriceListId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce price entries where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByCPInstanceUuid(
		String CPInstanceUuid) {

		return getPersistence().findByCPInstanceUuid(CPInstanceUuid);
	}

	/**
	 * Returns a range of all the commerce price entries where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @return the range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end) {

		return getPersistence().findByCPInstanceUuid(
			CPInstanceUuid, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().findByCPInstanceUuid(
			CPInstanceUuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByC_C(
		long commercePriceListId, String CPInstanceUuid) {

		return getPersistence().findByC_C(commercePriceListId, CPInstanceUuid);
	}

	/**
	 * Returns a range of all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @return the range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByC_C(
		long commercePriceListId, String CPInstanceUuid, int start, int end) {

		return getPersistence().findByC_C(
			commercePriceListId, CPInstanceUuid, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByC_C(
		long commercePriceListId, String CPInstanceUuid, int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().findByC_C(
			commercePriceListId, CPInstanceUuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63;.
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 * @return the matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByC_C_S(
		long commercePriceListId, String CPInstanceUuid, int status) {

		return getPersistence().findByC_C_S(
			commercePriceListId, CPInstanceUuid, status);
	}

	/**
	 * Returns a range of all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @return the range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByC_C_S(
		long commercePriceListId, String CPInstanceUuid, int status, int start,
		int end) {

		return getPersistence().findByC_C_S(
			commercePriceListId, CPInstanceUuid, status, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where commercePriceListId = &#63; and CPInstanceUuid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commercePriceListId the commerce price list ID
	 * @param CPInstanceUuid the cp instance uuid
	 * @param status the status
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByC_C_S(
		long commercePriceListId, String CPInstanceUuid, int status, int start,
		int end, OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().findByC_C_S(
			commercePriceListId, CPInstanceUuid, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the commerce price entries where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByC_Q_U(
		String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey) {

		return getPersistence().findByC_Q_U(
			CPInstanceUuid, quantity, unitOfMeasureKey);
	}

	/**
	 * Returns a range of all the commerce price entries where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @return the range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByC_Q_U(
		String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey,
		int start, int end) {

		return getPersistence().findByC_Q_U(
			CPInstanceUuid, quantity, unitOfMeasureKey, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce price entries where CPInstanceUuid = &#63; and quantity = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.price.list.model.impl.CommercePriceEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param quantity the quantity
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce price entries
	 * @param end the upper bound of the range of commerce price entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce price entries
	 */
	public static List<CommercePriceEntry> findByC_Q_U(
		String CPInstanceUuid, BigDecimal quantity, String unitOfMeasureKey,
		int start, int end,
		OrderByComparator<CommercePriceEntry> orderByComparator) {

		return getPersistence().findByC_Q_U(
			CPInstanceUuid, quantity, unitOfMeasureKey, start, end,
			orderByComparator);
	}

	public static CommercePriceEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CommercePriceEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CommercePriceEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-932352781