/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cp configuration entry service. This utility wraps <code>com.liferay.commerce.product.service.persistence.impl.CPConfigurationEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPConfigurationEntryPersistence
 * @generated
 */
public class CPConfigurationEntryUtil {

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
	public static void clearCache(CPConfigurationEntry cpConfigurationEntry) {
		getPersistence().clearCache(cpConfigurationEntry);
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
	public static Map<Serializable, CPConfigurationEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CPConfigurationEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CPConfigurationEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CPConfigurationEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CPConfigurationEntry update(
		CPConfigurationEntry cpConfigurationEntry) {

		return getPersistence().update(cpConfigurationEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CPConfigurationEntry update(
		CPConfigurationEntry cpConfigurationEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(cpConfigurationEntry, serviceContext);
	}

	/**
	 * Returns all the cp configuration entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the cp configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByUuid_First(
			String uuid,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByUuid_Last(
			String uuid,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByUuid_Last(
		String uuid,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public static CPConfigurationEntry[] findByUuid_PrevAndNext(
			long CPConfigurationEntryId, String uuid,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByUuid_PrevAndNext(
			CPConfigurationEntryId, uuid, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of cp configuration entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp configuration entries
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the cp configuration entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByUUID_G(String uuid, long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cp configuration entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cp configuration entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the cp configuration entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp configuration entry that was removed
	 */
	public static CPConfigurationEntry removeByUUID_G(String uuid, long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of cp configuration entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp configuration entries
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public static CPConfigurationEntry[] findByUuid_C_PrevAndNext(
			long CPConfigurationEntryId, String uuid, long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByUuid_C_PrevAndNext(
			CPConfigurationEntryId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entries
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the cp configuration entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the cp configuration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByCompanyId_Last(
			long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public static CPConfigurationEntry[] findByCompanyId_PrevAndNext(
			long CPConfigurationEntryId, long companyId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByCompanyId_PrevAndNext(
			CPConfigurationEntryId, companyId, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of cp configuration entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entries
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId) {

		return getPersistence().findByCPConfigurationListId(
			CPConfigurationListId);
	}

	/**
	 * Returns a range of all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId, int start, int end) {

		return getPersistence().findByCPConfigurationListId(
			CPConfigurationListId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().findByCPConfigurationListId(
			CPConfigurationListId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPConfigurationListId(
			CPConfigurationListId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByCPConfigurationListId_First(
			long CPConfigurationListId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByCPConfigurationListId_First(
			CPConfigurationListId, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByCPConfigurationListId_First(
		long CPConfigurationListId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().fetchByCPConfigurationListId_First(
			CPConfigurationListId, orderByComparator);
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByCPConfigurationListId_Last(
			long CPConfigurationListId,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByCPConfigurationListId_Last(
			CPConfigurationListId, orderByComparator);
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByCPConfigurationListId_Last(
		long CPConfigurationListId,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().fetchByCPConfigurationListId_Last(
			CPConfigurationListId, orderByComparator);
	}

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public static CPConfigurationEntry[]
			findByCPConfigurationListId_PrevAndNext(
				long CPConfigurationEntryId, long CPConfigurationListId,
				OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByCPConfigurationListId_PrevAndNext(
			CPConfigurationEntryId, CPConfigurationListId, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entries where CPConfigurationListId = &#63; from the database.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 */
	public static void removeByCPConfigurationListId(
		long CPConfigurationListId) {

		getPersistence().removeByCPConfigurationListId(CPConfigurationListId);
	}

	/**
	 * Returns the number of cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the number of matching cp configuration entries
	 */
	public static int countByCPConfigurationListId(long CPConfigurationListId) {
		return getPersistence().countByCPConfigurationListId(
			CPConfigurationListId);
	}

	/**
	 * Returns all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK) {

		return getPersistence().findByC_C(classNameId, classPK);
	}

	/**
	 * Returns a range of all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return getPersistence().findByC_C(classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	public static List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByC_C_Last(
			long classNameId, long classPK,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByC_C_Last(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByC_C_Last(
		long classNameId, long classPK,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().fetchByC_C_Last(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public static CPConfigurationEntry[] findByC_C_PrevAndNext(
			long CPConfigurationEntryId, long classNameId, long classPK,
			OrderByComparator<CPConfigurationEntry> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByC_C_PrevAndNext(
			CPConfigurationEntryId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the cp configuration entries where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByC_C(long classNameId, long classPK) {
		getPersistence().removeByC_C(classNameId, classPK);
	}

	/**
	 * Returns the number of cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching cp configuration entries
	 */
	public static int countByC_C(long classNameId, long classPK) {
		return getPersistence().countByC_C(classNameId, classPK);
	}

	/**
	 * Returns the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByC_C_C(
			long classNameId, long classPK, long CPConfigurationListId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByC_C_C(
			classNameId, classPK, CPConfigurationListId);
	}

	/**
	 * Returns the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId) {

		return getPersistence().fetchByC_C_C(
			classNameId, classPK, CPConfigurationListId);
	}

	/**
	 * Returns the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId,
		boolean useFinderCache) {

		return getPersistence().fetchByC_C_C(
			classNameId, classPK, CPConfigurationListId, useFinderCache);
	}

	/**
	 * Removes the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the cp configuration entry that was removed
	 */
	public static CPConfigurationEntry removeByC_C_C(
			long classNameId, long classPK, long CPConfigurationListId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().removeByC_C_C(
			classNameId, classPK, CPConfigurationListId);
	}

	/**
	 * Returns the number of cp configuration entries where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the number of matching cp configuration entries
	 */
	public static int countByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId) {

		return getPersistence().countByC_C_C(
			classNameId, classPK, CPConfigurationListId);
	}

	/**
	 * Returns the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp configuration entry that was removed
	 */
	public static CPConfigurationEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of cp configuration entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entries
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Caches the cp configuration entry in the entity cache if it is enabled.
	 *
	 * @param cpConfigurationEntry the cp configuration entry
	 */
	public static void cacheResult(CPConfigurationEntry cpConfigurationEntry) {
		getPersistence().cacheResult(cpConfigurationEntry);
	}

	/**
	 * Caches the cp configuration entries in the entity cache if it is enabled.
	 *
	 * @param cpConfigurationEntries the cp configuration entries
	 */
	public static void cacheResult(
		List<CPConfigurationEntry> cpConfigurationEntries) {

		getPersistence().cacheResult(cpConfigurationEntries);
	}

	/**
	 * Creates a new cp configuration entry with the primary key. Does not add the cp configuration entry to the database.
	 *
	 * @param CPConfigurationEntryId the primary key for the new cp configuration entry
	 * @return the new cp configuration entry
	 */
	public static CPConfigurationEntry create(long CPConfigurationEntryId) {
		return getPersistence().create(CPConfigurationEntryId);
	}

	/**
	 * Removes the cp configuration entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry that was removed
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public static CPConfigurationEntry remove(long CPConfigurationEntryId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().remove(CPConfigurationEntryId);
	}

	public static CPConfigurationEntry updateImpl(
		CPConfigurationEntry cpConfigurationEntry) {

		return getPersistence().updateImpl(cpConfigurationEntry);
	}

	/**
	 * Returns the cp configuration entry with the primary key or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public static CPConfigurationEntry findByPrimaryKey(
			long CPConfigurationEntryId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationEntryException {

		return getPersistence().findByPrimaryKey(CPConfigurationEntryId);
	}

	/**
	 * Returns the cp configuration entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry, or <code>null</code> if a cp configuration entry with the primary key could not be found
	 */
	public static CPConfigurationEntry fetchByPrimaryKey(
		long CPConfigurationEntryId) {

		return getPersistence().fetchByPrimaryKey(CPConfigurationEntryId);
	}

	/**
	 * Returns all the cp configuration entries.
	 *
	 * @return the cp configuration entries
	 */
	public static List<CPConfigurationEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the cp configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of cp configuration entries
	 */
	public static List<CPConfigurationEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cp configuration entries
	 */
	public static List<CPConfigurationEntry> findAll(
		int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cp configuration entries
	 */
	public static List<CPConfigurationEntry> findAll(
		int start, int end,
		OrderByComparator<CPConfigurationEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the cp configuration entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of cp configuration entries.
	 *
	 * @return the number of cp configuration entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static CPConfigurationEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CPConfigurationEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CPConfigurationEntryPersistence _persistence;

}