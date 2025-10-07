/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service.persistence;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the document library file entry service. This utility wraps <code>com.liferay.portlet.documentlibrary.service.persistence.impl.DLFileEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DLFileEntryPersistence
 * @generated
 */
public class DLFileEntryUtil {

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
	public static void clearCache(DLFileEntry dlFileEntry) {
		getPersistence().clearCache(dlFileEntry);
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
	public static Map<Serializable, DLFileEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DLFileEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DLFileEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DLFileEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DLFileEntry update(DLFileEntry dlFileEntry) {
		return getPersistence().update(dlFileEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DLFileEntry update(
		DLFileEntry dlFileEntry, ServiceContext serviceContext) {

		return getPersistence().update(dlFileEntry, serviceContext);
	}

	/**
	 * Returns all the document library file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the document library file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByUuid_First(
			String uuid, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByUuid_First(
		String uuid, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByUuid_Last(
			String uuid, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByUuid_Last(
		String uuid, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where uuid = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByUuid_PrevAndNext(
			long fileEntryId, String uuid,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByUuid_PrevAndNext(
			fileEntryId, uuid, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of document library file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching document library file entries
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the document library file entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByUUID_G(String uuid, long groupId)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the document library file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the document library file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the document library file entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the document library file entry that was removed
	 */
	public static DLFileEntry removeByUUID_G(String uuid, long groupId)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of document library file entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching document library file entries
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByUuid_C_PrevAndNext(
			long fileEntryId, String uuid, long companyId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByUuid_C_PrevAndNext(
			fileEntryId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of document library file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching document library file entries
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByGroupId_First(
			long groupId, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByGroupId_First(
		long groupId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByGroupId_Last(
			long groupId, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByGroupId_Last(
		long groupId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where groupId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByGroupId_PrevAndNext(
			long fileEntryId, long groupId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByGroupId_PrevAndNext(
			fileEntryId, groupId, orderByComparator);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set of document library file entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] filterFindByGroupId_PrevAndNext(
			long fileEntryId, long groupId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().filterFindByGroupId_PrevAndNext(
			fileEntryId, groupId, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library file entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns all the document library file entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the document library file entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByCompanyId_First(
			long companyId, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByCompanyId_Last(
			long companyId, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByCompanyId_Last(
		long companyId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where companyId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByCompanyId_PrevAndNext(
			long fileEntryId, long companyId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByCompanyId_PrevAndNext(
			fileEntryId, companyId, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of document library file entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching document library file entries
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns all the document library file entries where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByRepositoryId(long repositoryId) {
		return getPersistence().findByRepositoryId(repositoryId);
	}

	/**
	 * Returns a range of all the document library file entries where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByRepositoryId(
		long repositoryId, int start, int end) {

		return getPersistence().findByRepositoryId(repositoryId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByRepositoryId(
		long repositoryId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByRepositoryId(
			repositoryId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where repositoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByRepositoryId(
		long repositoryId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByRepositoryId(
			repositoryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByRepositoryId_First(
			long repositoryId, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByRepositoryId_First(
			repositoryId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByRepositoryId_First(
		long repositoryId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByRepositoryId_First(
			repositoryId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByRepositoryId_Last(
			long repositoryId, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByRepositoryId_Last(
			repositoryId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByRepositoryId_Last(
		long repositoryId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByRepositoryId_Last(
			repositoryId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where repositoryId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param repositoryId the repository ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByRepositoryId_PrevAndNext(
			long fileEntryId, long repositoryId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByRepositoryId_PrevAndNext(
			fileEntryId, repositoryId, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where repositoryId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 */
	public static void removeByRepositoryId(long repositoryId) {
		getPersistence().removeByRepositoryId(repositoryId);
	}

	/**
	 * Returns the number of document library file entries where repositoryId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @return the number of matching document library file entries
	 */
	public static int countByRepositoryId(long repositoryId) {
		return getPersistence().countByRepositoryId(repositoryId);
	}

	/**
	 * Returns all the document library file entries where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByMimeType(String mimeType) {
		return getPersistence().findByMimeType(mimeType);
	}

	/**
	 * Returns a range of all the document library file entries where mimeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param mimeType the mime type
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByMimeType(
		String mimeType, int start, int end) {

		return getPersistence().findByMimeType(mimeType, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where mimeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param mimeType the mime type
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByMimeType(
		String mimeType, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByMimeType(
			mimeType, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where mimeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param mimeType the mime type
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByMimeType(
		String mimeType, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByMimeType(
			mimeType, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByMimeType_First(
			String mimeType, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByMimeType_First(
			mimeType, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByMimeType_First(
		String mimeType, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByMimeType_First(
			mimeType, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByMimeType_Last(
			String mimeType, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByMimeType_Last(
			mimeType, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByMimeType_Last(
		String mimeType, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByMimeType_Last(
			mimeType, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where mimeType = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param mimeType the mime type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByMimeType_PrevAndNext(
			long fileEntryId, String mimeType,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByMimeType_PrevAndNext(
			fileEntryId, mimeType, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where mimeType = &#63; from the database.
	 *
	 * @param mimeType the mime type
	 */
	public static void removeByMimeType(String mimeType) {
		getPersistence().removeByMimeType(mimeType);
	}

	/**
	 * Returns the number of document library file entries where mimeType = &#63;.
	 *
	 * @param mimeType the mime type
	 * @return the number of matching document library file entries
	 */
	public static int countByMimeType(String mimeType) {
		return getPersistence().countByMimeType(mimeType);
	}

	/**
	 * Returns all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByFileEntryTypeId(
		long fileEntryTypeId) {

		return getPersistence().findByFileEntryTypeId(fileEntryTypeId);
	}

	/**
	 * Returns a range of all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByFileEntryTypeId(
		long fileEntryTypeId, int start, int end) {

		return getPersistence().findByFileEntryTypeId(
			fileEntryTypeId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByFileEntryTypeId(
		long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByFileEntryTypeId(
			fileEntryTypeId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByFileEntryTypeId(
		long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFileEntryTypeId(
			fileEntryTypeId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByFileEntryTypeId_First(
			long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByFileEntryTypeId_First(
			fileEntryTypeId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByFileEntryTypeId_First(
		long fileEntryTypeId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByFileEntryTypeId_First(
			fileEntryTypeId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByFileEntryTypeId_Last(
			long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByFileEntryTypeId_Last(
			fileEntryTypeId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByFileEntryTypeId_Last(
		long fileEntryTypeId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByFileEntryTypeId_Last(
			fileEntryTypeId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByFileEntryTypeId_PrevAndNext(
			long fileEntryId, long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByFileEntryTypeId_PrevAndNext(
			fileEntryId, fileEntryTypeId, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where fileEntryTypeId = &#63; from the database.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 */
	public static void removeByFileEntryTypeId(long fileEntryTypeId) {
		getPersistence().removeByFileEntryTypeId(fileEntryTypeId);
	}

	/**
	 * Returns the number of document library file entries where fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries
	 */
	public static int countByFileEntryTypeId(long fileEntryTypeId) {
		return getPersistence().countByFileEntryTypeId(fileEntryTypeId);
	}

	/**
	 * Returns all the document library file entries where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findBySmallImageId(long smallImageId) {
		return getPersistence().findBySmallImageId(smallImageId);
	}

	/**
	 * Returns a range of all the document library file entries where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findBySmallImageId(
		long smallImageId, int start, int end) {

		return getPersistence().findBySmallImageId(smallImageId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findBySmallImageId(
		long smallImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findBySmallImageId(
			smallImageId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findBySmallImageId(
		long smallImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findBySmallImageId(
			smallImageId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findBySmallImageId_First(
			long smallImageId, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findBySmallImageId_First(
			smallImageId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchBySmallImageId_First(
		long smallImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchBySmallImageId_First(
			smallImageId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findBySmallImageId_Last(
			long smallImageId, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findBySmallImageId_Last(
			smallImageId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchBySmallImageId_Last(
		long smallImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchBySmallImageId_Last(
			smallImageId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where smallImageId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findBySmallImageId_PrevAndNext(
			long fileEntryId, long smallImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findBySmallImageId_PrevAndNext(
			fileEntryId, smallImageId, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where smallImageId = &#63; from the database.
	 *
	 * @param smallImageId the small image ID
	 */
	public static void removeBySmallImageId(long smallImageId) {
		getPersistence().removeBySmallImageId(smallImageId);
	}

	/**
	 * Returns the number of document library file entries where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @return the number of matching document library file entries
	 */
	public static int countBySmallImageId(long smallImageId) {
		return getPersistence().countBySmallImageId(smallImageId);
	}

	/**
	 * Returns all the document library file entries where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByLargeImageId(long largeImageId) {
		return getPersistence().findByLargeImageId(largeImageId);
	}

	/**
	 * Returns a range of all the document library file entries where largeImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param largeImageId the large image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByLargeImageId(
		long largeImageId, int start, int end) {

		return getPersistence().findByLargeImageId(largeImageId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where largeImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param largeImageId the large image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByLargeImageId(
		long largeImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByLargeImageId(
			largeImageId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where largeImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param largeImageId the large image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByLargeImageId(
		long largeImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLargeImageId(
			largeImageId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByLargeImageId_First(
			long largeImageId, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByLargeImageId_First(
			largeImageId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByLargeImageId_First(
		long largeImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByLargeImageId_First(
			largeImageId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByLargeImageId_Last(
			long largeImageId, OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByLargeImageId_Last(
			largeImageId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByLargeImageId_Last(
		long largeImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByLargeImageId_Last(
			largeImageId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where largeImageId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param largeImageId the large image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByLargeImageId_PrevAndNext(
			long fileEntryId, long largeImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByLargeImageId_PrevAndNext(
			fileEntryId, largeImageId, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where largeImageId = &#63; from the database.
	 *
	 * @param largeImageId the large image ID
	 */
	public static void removeByLargeImageId(long largeImageId) {
		getPersistence().removeByLargeImageId(largeImageId);
	}

	/**
	 * Returns the number of document library file entries where largeImageId = &#63;.
	 *
	 * @param largeImageId the large image ID
	 * @return the number of matching document library file entries
	 */
	public static int countByLargeImageId(long largeImageId) {
		return getPersistence().countByLargeImageId(largeImageId);
	}

	/**
	 * Returns all the document library file entries where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByCustom1ImageId(long custom1ImageId) {
		return getPersistence().findByCustom1ImageId(custom1ImageId);
	}

	/**
	 * Returns a range of all the document library file entries where custom1ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByCustom1ImageId(
		long custom1ImageId, int start, int end) {

		return getPersistence().findByCustom1ImageId(
			custom1ImageId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where custom1ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByCustom1ImageId(
		long custom1ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByCustom1ImageId(
			custom1ImageId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where custom1ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByCustom1ImageId(
		long custom1ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCustom1ImageId(
			custom1ImageId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByCustom1ImageId_First(
			long custom1ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByCustom1ImageId_First(
			custom1ImageId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByCustom1ImageId_First(
		long custom1ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByCustom1ImageId_First(
			custom1ImageId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByCustom1ImageId_Last(
			long custom1ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByCustom1ImageId_Last(
			custom1ImageId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByCustom1ImageId_Last(
		long custom1ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByCustom1ImageId_Last(
			custom1ImageId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where custom1ImageId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param custom1ImageId the custom1 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByCustom1ImageId_PrevAndNext(
			long fileEntryId, long custom1ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByCustom1ImageId_PrevAndNext(
			fileEntryId, custom1ImageId, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where custom1ImageId = &#63; from the database.
	 *
	 * @param custom1ImageId the custom1 image ID
	 */
	public static void removeByCustom1ImageId(long custom1ImageId) {
		getPersistence().removeByCustom1ImageId(custom1ImageId);
	}

	/**
	 * Returns the number of document library file entries where custom1ImageId = &#63;.
	 *
	 * @param custom1ImageId the custom1 image ID
	 * @return the number of matching document library file entries
	 */
	public static int countByCustom1ImageId(long custom1ImageId) {
		return getPersistence().countByCustom1ImageId(custom1ImageId);
	}

	/**
	 * Returns all the document library file entries where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByCustom2ImageId(long custom2ImageId) {
		return getPersistence().findByCustom2ImageId(custom2ImageId);
	}

	/**
	 * Returns a range of all the document library file entries where custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByCustom2ImageId(
		long custom2ImageId, int start, int end) {

		return getPersistence().findByCustom2ImageId(
			custom2ImageId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByCustom2ImageId(
		long custom2ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByCustom2ImageId(
			custom2ImageId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByCustom2ImageId(
		long custom2ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCustom2ImageId(
			custom2ImageId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByCustom2ImageId_First(
			long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByCustom2ImageId_First(
			custom2ImageId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByCustom2ImageId_First(
		long custom2ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByCustom2ImageId_First(
			custom2ImageId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByCustom2ImageId_Last(
			long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByCustom2ImageId_Last(
			custom2ImageId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByCustom2ImageId_Last(
		long custom2ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByCustom2ImageId_Last(
			custom2ImageId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where custom2ImageId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByCustom2ImageId_PrevAndNext(
			long fileEntryId, long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByCustom2ImageId_PrevAndNext(
			fileEntryId, custom2ImageId, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where custom2ImageId = &#63; from the database.
	 *
	 * @param custom2ImageId the custom2 image ID
	 */
	public static void removeByCustom2ImageId(long custom2ImageId) {
		getPersistence().removeByCustom2ImageId(custom2ImageId);
	}

	/**
	 * Returns the number of document library file entries where custom2ImageId = &#63;.
	 *
	 * @param custom2ImageId the custom2 image ID
	 * @return the number of matching document library file entries
	 */
	public static int countByCustom2ImageId(long custom2ImageId) {
		return getPersistence().countByCustom2ImageId(custom2ImageId);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByG_U(long groupId, long userId) {
		return getPersistence().findByG_U(groupId, userId);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_U(
		long groupId, long userId, int start, int end) {

		return getPersistence().findByG_U(groupId, userId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByG_U(
			groupId, userId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U(
			groupId, userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByG_U_First(
			long groupId, long userId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_U_First(
			groupId, userId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByG_U_First(
			groupId, userId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByG_U_Last(
			long groupId, long userId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_U_Last(
			groupId, userId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_U_Last(
		long groupId, long userId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByG_U_Last(
			groupId, userId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByG_U_PrevAndNext(
			long fileEntryId, long groupId, long userId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_U_PrevAndNext(
			fileEntryId, groupId, userId, orderByComparator);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_U(long groupId, long userId) {
		return getPersistence().filterFindByG_U(groupId, userId);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_U(
		long groupId, long userId, int start, int end) {

		return getPersistence().filterFindByG_U(groupId, userId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().filterFindByG_U(
			groupId, userId, start, end, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] filterFindByG_U_PrevAndNext(
			long fileEntryId, long groupId, long userId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().filterFindByG_U_PrevAndNext(
			fileEntryId, groupId, userId, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	public static void removeByG_U(long groupId, long userId) {
		getPersistence().removeByG_U(groupId, userId);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching document library file entries
	 */
	public static int countByG_U(long groupId, long userId) {
		return getPersistence().countByG_U(groupId, userId);
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public static int filterCountByG_U(long groupId, long userId) {
		return getPersistence().filterCountByG_U(groupId, userId);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F(long groupId, long folderId) {
		return getPersistence().findByG_F(groupId, folderId);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F(
		long groupId, long folderId, int start, int end) {

		return getPersistence().findByG_F(groupId, folderId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByG_F(
			groupId, folderId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_F(
			groupId, folderId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByG_F_First(
			long groupId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_F_First(
			groupId, folderId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_F_First(
		long groupId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByG_F_First(
			groupId, folderId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByG_F_Last(
			long groupId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_F_Last(
			groupId, folderId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_F_Last(
		long groupId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByG_F_Last(
			groupId, folderId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByG_F_PrevAndNext(
			long fileEntryId, long groupId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_F_PrevAndNext(
			fileEntryId, groupId, folderId, orderByComparator);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_F(
		long groupId, long folderId) {

		return getPersistence().filterFindByG_F(groupId, folderId);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_F(
		long groupId, long folderId, int start, int end) {

		return getPersistence().filterFindByG_F(groupId, folderId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().filterFindByG_F(
			groupId, folderId, start, end, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set of document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] filterFindByG_F_PrevAndNext(
			long fileEntryId, long groupId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().filterFindByG_F_PrevAndNext(
			fileEntryId, groupId, folderId, orderByComparator);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_F(
		long groupId, long[] folderIds) {

		return getPersistence().filterFindByG_F(groupId, folderIds);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_F(
		long groupId, long[] folderIds, int start, int end) {

		return getPersistence().filterFindByG_F(groupId, folderIds, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().filterFindByG_F(
			groupId, folderIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F(long groupId, long[] folderIds) {
		return getPersistence().findByG_F(groupId, folderIds);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F(
		long groupId, long[] folderIds, int start, int end) {

		return getPersistence().findByG_F(groupId, folderIds, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByG_F(
			groupId, folderIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_F(
			groupId, folderIds, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the document library file entries where groupId = &#63; and folderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 */
	public static void removeByG_F(long groupId, long folderId) {
		getPersistence().removeByG_F(groupId, folderId);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries
	 */
	public static int countByG_F(long groupId, long folderId) {
		return getPersistence().countByG_F(groupId, folderId);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries
	 */
	public static int countByG_F(long groupId, long[] folderIds) {
		return getPersistence().countByG_F(groupId, folderIds);
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public static int filterCountByG_F(long groupId, long folderId) {
		return getPersistence().filterCountByG_F(groupId, folderId);
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public static int filterCountByG_F(long groupId, long[] folderIds) {
		return getPersistence().filterCountByG_F(groupId, folderIds);
	}

	/**
	 * Returns all the document library file entries where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByR_F(
		long repositoryId, long folderId) {

		return getPersistence().findByR_F(repositoryId, folderId);
	}

	/**
	 * Returns a range of all the document library file entries where repositoryId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByR_F(
		long repositoryId, long folderId, int start, int end) {

		return getPersistence().findByR_F(repositoryId, folderId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where repositoryId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByR_F(
		long repositoryId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByR_F(
			repositoryId, folderId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where repositoryId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByR_F(
		long repositoryId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_F(
			repositoryId, folderId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByR_F_First(
			long repositoryId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByR_F_First(
			repositoryId, folderId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByR_F_First(
		long repositoryId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByR_F_First(
			repositoryId, folderId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByR_F_Last(
			long repositoryId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByR_F_Last(
			repositoryId, folderId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByR_F_Last(
		long repositoryId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByR_F_Last(
			repositoryId, folderId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByR_F_PrevAndNext(
			long fileEntryId, long repositoryId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByR_F_PrevAndNext(
			fileEntryId, repositoryId, folderId, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where repositoryId = &#63; and folderId = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 */
	public static void removeByR_F(long repositoryId, long folderId) {
		getPersistence().removeByR_F(repositoryId, folderId);
	}

	/**
	 * Returns the number of document library file entries where repositoryId = &#63; and folderId = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries
	 */
	public static int countByR_F(long repositoryId, long folderId) {
		return getPersistence().countByR_F(repositoryId, folderId);
	}

	/**
	 * Returns all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByF_N(long folderId, String name) {
		return getPersistence().findByF_N(folderId, name);
	}

	/**
	 * Returns a range of all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByF_N(
		long folderId, String name, int start, int end) {

		return getPersistence().findByF_N(folderId, name, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByF_N(
		long folderId, String name, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByF_N(
			folderId, name, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByF_N(
		long folderId, String name, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByF_N(
			folderId, name, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByF_N_First(
			long folderId, String name,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByF_N_First(
			folderId, name, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByF_N_First(
		long folderId, String name,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByF_N_First(
			folderId, name, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByF_N_Last(
			long folderId, String name,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByF_N_Last(
			folderId, name, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByF_N_Last(
		long folderId, String name,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByF_N_Last(
			folderId, name, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where folderId = &#63; and name = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param folderId the folder ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByF_N_PrevAndNext(
			long fileEntryId, long folderId, String name,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByF_N_PrevAndNext(
			fileEntryId, folderId, name, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where folderId = &#63; and name = &#63; from the database.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 */
	public static void removeByF_N(long folderId, String name) {
		getPersistence().removeByF_N(folderId, name);
	}

	/**
	 * Returns the number of document library file entries where folderId = &#63; and name = &#63;.
	 *
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the number of matching document library file entries
	 */
	public static int countByF_N(long folderId, String name) {
		return getPersistence().countByF_N(folderId, name);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId) {

		return getPersistence().findByG_U_F(groupId, userId, folderId);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId, int start, int end) {

		return getPersistence().findByG_U_F(
			groupId, userId, folderId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByG_U_F(
			groupId, userId, folderId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U_F(
			groupId, userId, folderId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByG_U_F_First(
			long groupId, long userId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_U_F_First(
			groupId, userId, folderId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_U_F_First(
		long groupId, long userId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByG_U_F_First(
			groupId, userId, folderId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByG_U_F_Last(
			long groupId, long userId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_U_F_Last(
			groupId, userId, folderId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_U_F_Last(
		long groupId, long userId, long folderId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByG_U_F_Last(
			groupId, userId, folderId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByG_U_F_PrevAndNext(
			long fileEntryId, long groupId, long userId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_U_F_PrevAndNext(
			fileEntryId, groupId, userId, folderId, orderByComparator);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long folderId) {

		return getPersistence().filterFindByG_U_F(groupId, userId, folderId);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long folderId, int start, int end) {

		return getPersistence().filterFindByG_U_F(
			groupId, userId, folderId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().filterFindByG_U_F(
			groupId, userId, folderId, start, end, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] filterFindByG_U_F_PrevAndNext(
			long fileEntryId, long groupId, long userId, long folderId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().filterFindByG_U_F_PrevAndNext(
			fileEntryId, groupId, userId, folderId, orderByComparator);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long[] folderIds) {

		return getPersistence().filterFindByG_U_F(groupId, userId, folderIds);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end) {

		return getPersistence().filterFindByG_U_F(
			groupId, userId, folderIds, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().filterFindByG_U_F(
			groupId, userId, folderIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds) {

		return getPersistence().findByG_U_F(groupId, userId, folderIds);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end) {

		return getPersistence().findByG_U_F(
			groupId, userId, folderIds, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByG_U_F(
			groupId, userId, folderIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_U_F(
		long groupId, long userId, long[] folderIds, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U_F(
			groupId, userId, folderIds, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 */
	public static void removeByG_U_F(long groupId, long userId, long folderId) {
		getPersistence().removeByG_U_F(groupId, userId, folderId);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries
	 */
	public static int countByG_U_F(long groupId, long userId, long folderId) {
		return getPersistence().countByG_U_F(groupId, userId, folderId);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries
	 */
	public static int countByG_U_F(
		long groupId, long userId, long[] folderIds) {

		return getPersistence().countByG_U_F(groupId, userId, folderIds);
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderId the folder ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public static int filterCountByG_U_F(
		long groupId, long userId, long folderId) {

		return getPersistence().filterCountByG_U_F(groupId, userId, folderId);
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and userId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param folderIds the folder IDs
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public static int filterCountByG_U_F(
		long groupId, long userId, long[] folderIds) {

		return getPersistence().filterCountByG_U_F(groupId, userId, folderIds);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByG_F_N(
			long groupId, long folderId, String name)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_F_N(groupId, folderId, name);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_F_N(
		long groupId, long folderId, String name) {

		return getPersistence().fetchByG_F_N(groupId, folderId, name);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_F_N(
		long groupId, long folderId, String name, boolean useFinderCache) {

		return getPersistence().fetchByG_F_N(
			groupId, folderId, name, useFinderCache);
	}

	/**
	 * Removes the document library file entry where groupId = &#63; and folderId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the document library file entry that was removed
	 */
	public static DLFileEntry removeByG_F_N(
			long groupId, long folderId, String name)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().removeByG_F_N(groupId, folderId, name);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param name the name
	 * @return the number of matching document library file entries
	 */
	public static int countByG_F_N(long groupId, long folderId, String name) {
		return getPersistence().countByG_F_N(groupId, folderId, name);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByG_F_FN(
			long groupId, long folderId, String fileName)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_F_FN(groupId, folderId, fileName);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_F_FN(
		long groupId, long folderId, String fileName) {

		return getPersistence().fetchByG_F_FN(groupId, folderId, fileName);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_F_FN(
		long groupId, long folderId, String fileName, boolean useFinderCache) {

		return getPersistence().fetchByG_F_FN(
			groupId, folderId, fileName, useFinderCache);
	}

	/**
	 * Removes the document library file entry where groupId = &#63; and folderId = &#63; and fileName = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the document library file entry that was removed
	 */
	public static DLFileEntry removeByG_F_FN(
			long groupId, long folderId, String fileName)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().removeByG_F_FN(groupId, folderId, fileName);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and fileName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileName the file name
	 * @return the number of matching document library file entries
	 */
	public static int countByG_F_FN(
		long groupId, long folderId, String fileName) {

		return getPersistence().countByG_F_FN(groupId, folderId, fileName);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByG_F_T(
			long groupId, long folderId, String title)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_F_T(groupId, folderId, title);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_F_T(
		long groupId, long folderId, String title) {

		return getPersistence().fetchByG_F_T(groupId, folderId, title);
	}

	/**
	 * Returns the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_F_T(
		long groupId, long folderId, String title, boolean useFinderCache) {

		return getPersistence().fetchByG_F_T(
			groupId, folderId, title, useFinderCache);
	}

	/**
	 * Removes the document library file entry where groupId = &#63; and folderId = &#63; and title = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the document library file entry that was removed
	 */
	public static DLFileEntry removeByG_F_T(
			long groupId, long folderId, String title)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().removeByG_F_T(groupId, folderId, title);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and title = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param title the title
	 * @return the number of matching document library file entries
	 */
	public static int countByG_F_T(long groupId, long folderId, String title) {
		return getPersistence().countByG_F_T(groupId, folderId, title);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		return getPersistence().findByG_F_F(groupId, folderId, fileEntryTypeId);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end) {

		return getPersistence().findByG_F_F(
			groupId, folderId, fileEntryTypeId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByG_F_F(
			groupId, folderId, fileEntryTypeId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_F_F(
			groupId, folderId, fileEntryTypeId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByG_F_F_First(
			long groupId, long folderId, long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_F_F_First(
			groupId, folderId, fileEntryTypeId, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_F_F_First(
		long groupId, long folderId, long fileEntryTypeId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByG_F_F_First(
			groupId, folderId, fileEntryTypeId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByG_F_F_Last(
			long groupId, long folderId, long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_F_F_Last(
			groupId, folderId, fileEntryTypeId, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByG_F_F_Last(
		long groupId, long folderId, long fileEntryTypeId,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByG_F_F_Last(
			groupId, folderId, fileEntryTypeId, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByG_F_F_PrevAndNext(
			long fileEntryId, long groupId, long folderId, long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByG_F_F_PrevAndNext(
			fileEntryId, groupId, folderId, fileEntryTypeId, orderByComparator);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		return getPersistence().filterFindByG_F_F(
			groupId, folderId, fileEntryTypeId);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end) {

		return getPersistence().filterFindByG_F_F(
			groupId, folderId, fileEntryTypeId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permissions to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_F_F(
		long groupId, long folderId, long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().filterFindByG_F_F(
			groupId, folderId, fileEntryTypeId, start, end, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set of document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] filterFindByG_F_F_PrevAndNext(
			long fileEntryId, long groupId, long folderId, long fileEntryTypeId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().filterFindByG_F_F_PrevAndNext(
			fileEntryId, groupId, folderId, fileEntryTypeId, orderByComparator);
	}

	/**
	 * Returns all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId) {

		return getPersistence().filterFindByG_F_F(
			groupId, folderIds, fileEntryTypeId);
	}

	/**
	 * Returns a range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end) {

		return getPersistence().filterFindByG_F_F(
			groupId, folderIds, fileEntryTypeId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries that the user has permission to view
	 */
	public static List<DLFileEntry> filterFindByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().filterFindByG_F_F(
			groupId, folderIds, fileEntryTypeId, start, end, orderByComparator);
	}

	/**
	 * Returns all the document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId) {

		return getPersistence().findByG_F_F(
			groupId, folderIds, fileEntryTypeId);
	}

	/**
	 * Returns a range of all the document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end) {

		return getPersistence().findByG_F_F(
			groupId, folderIds, fileEntryTypeId, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByG_F_F(
			groupId, folderIds, fileEntryTypeId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId, int start,
		int end, OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_F_F(
			groupId, folderIds, fileEntryTypeId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 */
	public static void removeByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		getPersistence().removeByG_F_F(groupId, folderId, fileEntryTypeId);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries
	 */
	public static int countByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		return getPersistence().countByG_F_F(
			groupId, folderId, fileEntryTypeId);
	}

	/**
	 * Returns the number of document library file entries where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries
	 */
	public static int countByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId) {

		return getPersistence().countByG_F_F(
			groupId, folderIds, fileEntryTypeId);
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public static int filterCountByG_F_F(
		long groupId, long folderId, long fileEntryTypeId) {

		return getPersistence().filterCountByG_F_F(
			groupId, folderId, fileEntryTypeId);
	}

	/**
	 * Returns the number of document library file entries that the user has permission to view where groupId = &#63; and folderId = any &#63; and fileEntryTypeId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param fileEntryTypeId the file entry type ID
	 * @return the number of matching document library file entries that the user has permission to view
	 */
	public static int filterCountByG_F_F(
		long groupId, long[] folderIds, long fileEntryTypeId) {

		return getPersistence().filterCountByG_F_F(
			groupId, folderIds, fileEntryTypeId);
	}

	/**
	 * Returns all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK) {

		return getPersistence().findByC_C_C(companyId, classNameId, classPK);
	}

	/**
	 * Returns a range of all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end) {

		return getPersistence().findByC_C_C(
			companyId, classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByC_C_C(
			companyId, classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C_C(
			companyId, classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByC_C_C_First(
			long companyId, long classNameId, long classPK,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByC_C_C_First(
			companyId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByC_C_C_First(
		long companyId, long classNameId, long classPK,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByC_C_C_First(
			companyId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByC_C_C_Last(
			long companyId, long classNameId, long classPK,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByC_C_C_Last(
			companyId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByC_C_C_Last(
		long companyId, long classNameId, long classPK,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByC_C_C_Last(
			companyId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByC_C_C_PrevAndNext(
			long fileEntryId, long companyId, long classNameId, long classPK,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByC_C_C_PrevAndNext(
			fileEntryId, companyId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByC_C_C(
		long companyId, long classNameId, long classPK) {

		getPersistence().removeByC_C_C(companyId, classNameId, classPK);
	}

	/**
	 * Returns the number of document library file entries where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching document library file entries
	 */
	public static int countByC_C_C(
		long companyId, long classNameId, long classPK) {

		return getPersistence().countByC_C_C(companyId, classNameId, classPK);
	}

	/**
	 * Returns all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @return the matching document library file entries
	 */
	public static List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId) {

		return getPersistence().findByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId);
	}

	/**
	 * Returns a range of all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of matching document library file entries
	 */
	public static List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, int start, int end) {

		return getPersistence().findByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId, start,
			end);
	}

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId, start,
			end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching document library file entries
	 */
	public static List<DLFileEntry> findByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByS_L_C1_C2_First(
			long smallImageId, long largeImageId, long custom1ImageId,
			long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByS_L_C1_C2_First(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId,
			orderByComparator);
	}

	/**
	 * Returns the first document library file entry in the ordered set where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByS_L_C1_C2_First(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByS_L_C1_C2_First(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId,
			orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByS_L_C1_C2_Last(
			long smallImageId, long largeImageId, long custom1ImageId,
			long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByS_L_C1_C2_Last(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId,
			orderByComparator);
	}

	/**
	 * Returns the last document library file entry in the ordered set where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByS_L_C1_C2_Last(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().fetchByS_L_C1_C2_Last(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId,
			orderByComparator);
	}

	/**
	 * Returns the document library file entries before and after the current document library file entry in the ordered set where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param fileEntryId the primary key of the current document library file entry
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry[] findByS_L_C1_C2_PrevAndNext(
			long fileEntryId, long smallImageId, long largeImageId,
			long custom1ImageId, long custom2ImageId,
			OrderByComparator<DLFileEntry> orderByComparator)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByS_L_C1_C2_PrevAndNext(
			fileEntryId, smallImageId, largeImageId, custom1ImageId,
			custom2ImageId, orderByComparator);
	}

	/**
	 * Removes all the document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63; from the database.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 */
	public static void removeByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId) {

		getPersistence().removeByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId);
	}

	/**
	 * Returns the number of document library file entries where smallImageId = &#63; and largeImageId = &#63; and custom1ImageId = &#63; and custom2ImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param largeImageId the large image ID
	 * @param custom1ImageId the custom1 image ID
	 * @param custom2ImageId the custom2 image ID
	 * @return the number of matching document library file entries
	 */
	public static int countByS_L_C1_C2(
		long smallImageId, long largeImageId, long custom1ImageId,
		long custom2ImageId) {

		return getPersistence().countByS_L_C1_C2(
			smallImageId, largeImageId, custom1ImageId, custom2ImageId);
	}

	/**
	 * Returns the document library file entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library file entry
	 * @throws NoSuchFileEntryException if a matching document library file entry could not be found
	 */
	public static DLFileEntry findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library file entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library file entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	public static DLFileEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the document library file entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the document library file entry that was removed
	 */
	public static DLFileEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of document library file entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching document library file entries
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Caches the document library file entry in the entity cache if it is enabled.
	 *
	 * @param dlFileEntry the document library file entry
	 */
	public static void cacheResult(DLFileEntry dlFileEntry) {
		getPersistence().cacheResult(dlFileEntry);
	}

	/**
	 * Caches the document library file entries in the entity cache if it is enabled.
	 *
	 * @param dlFileEntries the document library file entries
	 */
	public static void cacheResult(List<DLFileEntry> dlFileEntries) {
		getPersistence().cacheResult(dlFileEntries);
	}

	/**
	 * Creates a new document library file entry with the primary key. Does not add the document library file entry to the database.
	 *
	 * @param fileEntryId the primary key for the new document library file entry
	 * @return the new document library file entry
	 */
	public static DLFileEntry create(long fileEntryId) {
		return getPersistence().create(fileEntryId);
	}

	/**
	 * Removes the document library file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry that was removed
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry remove(long fileEntryId)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().remove(fileEntryId);
	}

	public static DLFileEntry updateImpl(DLFileEntry dlFileEntry) {
		return getPersistence().updateImpl(dlFileEntry);
	}

	/**
	 * Returns the document library file entry with the primary key or throws a <code>NoSuchFileEntryException</code> if it could not be found.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry
	 * @throws NoSuchFileEntryException if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry findByPrimaryKey(long fileEntryId)
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryException {

		return getPersistence().findByPrimaryKey(fileEntryId);
	}

	/**
	 * Returns the document library file entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry, or <code>null</code> if a document library file entry with the primary key could not be found
	 */
	public static DLFileEntry fetchByPrimaryKey(long fileEntryId) {
		return getPersistence().fetchByPrimaryKey(fileEntryId);
	}

	/**
	 * Returns all the document library file entries.
	 *
	 * @return the document library file entries
	 */
	public static List<DLFileEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the document library file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of document library file entries
	 */
	public static List<DLFileEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the document library file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of document library file entries
	 */
	public static List<DLFileEntry> findAll(
		int start, int end, OrderByComparator<DLFileEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the document library file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of document library file entries
	 */
	public static List<DLFileEntry> findAll(
		int start, int end, OrderByComparator<DLFileEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the document library file entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of document library file entries.
	 *
	 * @return the number of document library file entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static DLFileEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(DLFileEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile DLFileEntryPersistence _persistence;

}