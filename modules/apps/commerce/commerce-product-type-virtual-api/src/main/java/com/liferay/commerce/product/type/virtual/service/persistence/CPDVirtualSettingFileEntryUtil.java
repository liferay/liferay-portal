/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service.persistence;

import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cpd virtual setting file entry service. This utility wraps <code>com.liferay.commerce.product.type.virtual.service.persistence.impl.CPDVirtualSettingFileEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPDVirtualSettingFileEntryPersistence
 * @generated
 */
public class CPDVirtualSettingFileEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CPDVirtualSettingFileEntry> cpdVirtualSettingFileEntries) {

		getPersistence().cacheResult(cpdVirtualSettingFileEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		getPersistence().cacheResult(cpdVirtualSettingFileEntry);
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
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		getPersistence().clearCache(cpdVirtualSettingFileEntry);
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
	public static Map<Serializable, CPDVirtualSettingFileEntry>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CPDVirtualSettingFileEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CPDVirtualSettingFileEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CPDVirtualSettingFileEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CPDVirtualSettingFileEntry update(
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		return getPersistence().update(cpdVirtualSettingFileEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CPDVirtualSettingFileEntry update(
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(
			cpdVirtualSettingFileEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	public static CPDVirtualSettingFileEntry findByUuid_First(
			String uuid,
			OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator)
		throws com.liferay.commerce.product.type.virtual.exception.
			NoSuchCPDVirtualSettingFileEntryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	public static CPDVirtualSettingFileEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the cpd virtual setting file entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of cpd virtual setting file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cpd virtual setting file entries
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the cpd virtual setting file entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDVirtualSettingFileEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	public static CPDVirtualSettingFileEntry findByUUID_G(
			String uuid, long groupId)
		throws com.liferay.commerce.product.type.virtual.exception.
			NoSuchCPDVirtualSettingFileEntryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cpd virtual setting file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	public static CPDVirtualSettingFileEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the cpd virtual setting file entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cpd virtual setting file entry that was removed
	 */
	public static CPDVirtualSettingFileEntry removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.commerce.product.type.virtual.exception.
			NoSuchCPDVirtualSettingFileEntryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of cpd virtual setting file entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cpd virtual setting file entries
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	public static CPDVirtualSettingFileEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator)
		throws com.liferay.commerce.product.type.virtual.exception.
			NoSuchCPDVirtualSettingFileEntryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	public static CPDVirtualSettingFileEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the cpd virtual setting file entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of cpd virtual setting file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cpd virtual setting file entries
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry>
		findByCPDefinitionVirtualSettingId(
			long CPDefinitionVirtualSettingId, int start, int end,
			OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByCPDefinitionVirtualSettingId(
			CPDefinitionVirtualSettingId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	public static CPDVirtualSettingFileEntry
			findByCPDefinitionVirtualSettingId_First(
				long CPDefinitionVirtualSettingId,
				OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator)
		throws com.liferay.commerce.product.type.virtual.exception.
			NoSuchCPDVirtualSettingFileEntryException {

		return getPersistence().findByCPDefinitionVirtualSettingId_First(
			CPDefinitionVirtualSettingId, orderByComparator);
	}

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	public static CPDVirtualSettingFileEntry
		fetchByCPDefinitionVirtualSettingId_First(
			long CPDefinitionVirtualSettingId,
			OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator) {

		return getPersistence().fetchByCPDefinitionVirtualSettingId_First(
			CPDefinitionVirtualSettingId, orderByComparator);
	}

	/**
	 * Removes all the cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63; from the database.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 */
	public static void removeByCPDefinitionVirtualSettingId(
		long CPDefinitionVirtualSettingId) {

		getPersistence().removeByCPDefinitionVirtualSettingId(
			CPDefinitionVirtualSettingId);
	}

	/**
	 * Returns the number of cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @return the number of matching cpd virtual setting file entries
	 */
	public static int countByCPDefinitionVirtualSettingId(
		long CPDefinitionVirtualSettingId) {

		return getPersistence().countByCPDefinitionVirtualSettingId(
			CPDefinitionVirtualSettingId);
	}

	/**
	 * Creates a new cpd virtual setting file entry with the primary key. Does not add the cpd virtual setting file entry to the database.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key for the new cpd virtual setting file entry
	 * @return the new cpd virtual setting file entry
	 */
	public static CPDVirtualSettingFileEntry create(
		long CPDefinitionVirtualSettingFileEntryId) {

		return getPersistence().create(CPDefinitionVirtualSettingFileEntryId);
	}

	/**
	 * Removes the cpd virtual setting file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry that was removed
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a cpd virtual setting file entry with the primary key could not be found
	 */
	public static CPDVirtualSettingFileEntry remove(
			long CPDefinitionVirtualSettingFileEntryId)
		throws com.liferay.commerce.product.type.virtual.exception.
			NoSuchCPDVirtualSettingFileEntryException {

		return getPersistence().remove(CPDefinitionVirtualSettingFileEntryId);
	}

	public static CPDVirtualSettingFileEntry updateImpl(
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry) {

		return getPersistence().updateImpl(cpdVirtualSettingFileEntry);
	}

	/**
	 * Returns the cpd virtual setting file entry with the primary key or throws a <code>NoSuchCPDVirtualSettingFileEntryException</code> if it could not be found.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a cpd virtual setting file entry with the primary key could not be found
	 */
	public static CPDVirtualSettingFileEntry findByPrimaryKey(
			long CPDefinitionVirtualSettingFileEntryId)
		throws com.liferay.commerce.product.type.virtual.exception.
			NoSuchCPDVirtualSettingFileEntryException {

		return getPersistence().findByPrimaryKey(
			CPDefinitionVirtualSettingFileEntryId);
	}

	/**
	 * Returns the cpd virtual setting file entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry, or <code>null</code> if a cpd virtual setting file entry with the primary key could not be found
	 */
	public static CPDVirtualSettingFileEntry fetchByPrimaryKey(
		long CPDefinitionVirtualSettingFileEntryId) {

		return getPersistence().fetchByPrimaryKey(
			CPDefinitionVirtualSettingFileEntryId);
	}

	/**
	 * Returns the cpd virtual setting file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	public static CPDVirtualSettingFileEntry fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the cpd virtual setting file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the cpd virtual setting file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @return the range of matching cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the cpd virtual setting file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the cpd virtual setting file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @return the range of matching cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @return the matching cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry>
		findByCPDefinitionVirtualSettingId(long CPDefinitionVirtualSettingId) {

		return getPersistence().findByCPDefinitionVirtualSettingId(
			CPDefinitionVirtualSettingId);
	}

	/**
	 * Returns a range of all the cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @return the range of matching cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry>
		findByCPDefinitionVirtualSettingId(
			long CPDefinitionVirtualSettingId, int start, int end) {

		return getPersistence().findByCPDefinitionVirtualSettingId(
			CPDefinitionVirtualSettingId, start, end);
	}

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	public static List<CPDVirtualSettingFileEntry>
		findByCPDefinitionVirtualSettingId(
			long CPDefinitionVirtualSettingId, int start, int end,
			OrderByComparator<CPDVirtualSettingFileEntry> orderByComparator) {

		return getPersistence().findByCPDefinitionVirtualSettingId(
			CPDefinitionVirtualSettingId, start, end, orderByComparator);
	}

	public static CPDVirtualSettingFileEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CPDVirtualSettingFileEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CPDVirtualSettingFileEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1507831191