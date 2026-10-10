/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.service.persistence;

import com.liferay.adaptive.media.image.model.AMImageEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the am image entry service. This utility wraps <code>com.liferay.adaptive.media.image.service.persistence.impl.AMImageEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AMImageEntryPersistence
 * @generated
 */
public class AMImageEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<AMImageEntry> amImageEntries) {
		getPersistence().cacheResult(amImageEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(AMImageEntry amImageEntry) {
		getPersistence().cacheResult(amImageEntry);
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
	public static void clearCache(AMImageEntry amImageEntry) {
		getPersistence().clearCache(amImageEntry);
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
	public static Map<Serializable, AMImageEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<AMImageEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<AMImageEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<AMImageEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<AMImageEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static AMImageEntry update(AMImageEntry amImageEntry) {
		return getPersistence().update(amImageEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static AMImageEntry update(
		AMImageEntry amImageEntry, ServiceContext serviceContext) {

		return getPersistence().update(amImageEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the am image entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching am image entries
	 */
	public static List<AMImageEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AMImageEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first am image entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry
	 * @throws NoSuchAMImageEntryException if a matching am image entry could not be found
	 */
	public static AMImageEntry findByUuid_First(
			String uuid, OrderByComparator<AMImageEntry> orderByComparator)
		throws com.liferay.adaptive.media.image.exception.
			NoSuchAMImageEntryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first am image entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public static AMImageEntry fetchByUuid_First(
		String uuid, OrderByComparator<AMImageEntry> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the am image entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of am image entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching am image entries
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the am image entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchAMImageEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching am image entry
	 * @throws NoSuchAMImageEntryException if a matching am image entry could not be found
	 */
	public static AMImageEntry findByUUID_G(String uuid, long groupId)
		throws com.liferay.adaptive.media.image.exception.
			NoSuchAMImageEntryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the am image entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public static AMImageEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the am image entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the am image entry that was removed
	 */
	public static AMImageEntry removeByUUID_G(String uuid, long groupId)
		throws com.liferay.adaptive.media.image.exception.
			NoSuchAMImageEntryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of am image entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching am image entries
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the am image entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching am image entries
	 */
	public static List<AMImageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AMImageEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first am image entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry
	 * @throws NoSuchAMImageEntryException if a matching am image entry could not be found
	 */
	public static AMImageEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<AMImageEntry> orderByComparator)
		throws com.liferay.adaptive.media.image.exception.
			NoSuchAMImageEntryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first am image entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public static AMImageEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<AMImageEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the am image entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of am image entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching am image entries
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the am image entries where fileVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileVersionId the file version ID
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching am image entries
	 */
	public static List<AMImageEntry> findByFileVersionId(
		long fileVersionId, int start, int end,
		OrderByComparator<AMImageEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFileVersionId(
			fileVersionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first am image entry in the ordered set where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry
	 * @throws NoSuchAMImageEntryException if a matching am image entry could not be found
	 */
	public static AMImageEntry findByFileVersionId_First(
			long fileVersionId,
			OrderByComparator<AMImageEntry> orderByComparator)
		throws com.liferay.adaptive.media.image.exception.
			NoSuchAMImageEntryException {

		return getPersistence().findByFileVersionId_First(
			fileVersionId, orderByComparator);
	}

	/**
	 * Returns the first am image entry in the ordered set where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public static AMImageEntry fetchByFileVersionId_First(
		long fileVersionId, OrderByComparator<AMImageEntry> orderByComparator) {

		return getPersistence().fetchByFileVersionId_First(
			fileVersionId, orderByComparator);
	}

	/**
	 * Removes all the am image entries where fileVersionId = &#63; from the database.
	 *
	 * @param fileVersionId the file version ID
	 */
	public static void removeByFileVersionId(long fileVersionId) {
		getPersistence().removeByFileVersionId(fileVersionId);
	}

	/**
	 * Returns the number of am image entries where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @return the number of matching am image entries
	 */
	public static int countByFileVersionId(long fileVersionId) {
		return getPersistence().countByFileVersionId(fileVersionId);
	}

	/**
	 * Returns an ordered range of all the am image entries where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching am image entries
	 */
	public static List<AMImageEntry> findByC_C(
		long companyId, String configurationUuid, int start, int end,
		OrderByComparator<AMImageEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			companyId, configurationUuid, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first am image entry in the ordered set where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry
	 * @throws NoSuchAMImageEntryException if a matching am image entry could not be found
	 */
	public static AMImageEntry findByC_C_First(
			long companyId, String configurationUuid,
			OrderByComparator<AMImageEntry> orderByComparator)
		throws com.liferay.adaptive.media.image.exception.
			NoSuchAMImageEntryException {

		return getPersistence().findByC_C_First(
			companyId, configurationUuid, orderByComparator);
	}

	/**
	 * Returns the first am image entry in the ordered set where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public static AMImageEntry fetchByC_C_First(
		long companyId, String configurationUuid,
		OrderByComparator<AMImageEntry> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			companyId, configurationUuid, orderByComparator);
	}

	/**
	 * Removes all the am image entries where companyId = &#63; and configurationUuid = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 */
	public static void removeByC_C(long companyId, String configurationUuid) {
		getPersistence().removeByC_C(companyId, configurationUuid);
	}

	/**
	 * Returns the number of am image entries where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @return the number of matching am image entries
	 */
	public static int countByC_C(long companyId, String configurationUuid) {
		return getPersistence().countByC_C(companyId, configurationUuid);
	}

	/**
	 * Returns the am image entry where configurationUuid = &#63; and fileVersionId = &#63; or throws a <code>NoSuchAMImageEntryException</code> if it could not be found.
	 *
	 * @param configurationUuid the configuration uuid
	 * @param fileVersionId the file version ID
	 * @return the matching am image entry
	 * @throws NoSuchAMImageEntryException if a matching am image entry could not be found
	 */
	public static AMImageEntry findByC_F(
			String configurationUuid, long fileVersionId)
		throws com.liferay.adaptive.media.image.exception.
			NoSuchAMImageEntryException {

		return getPersistence().findByC_F(configurationUuid, fileVersionId);
	}

	/**
	 * Returns the am image entry where configurationUuid = &#63; and fileVersionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param configurationUuid the configuration uuid
	 * @param fileVersionId the file version ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public static AMImageEntry fetchByC_F(
		String configurationUuid, long fileVersionId, boolean useFinderCache) {

		return getPersistence().fetchByC_F(
			configurationUuid, fileVersionId, useFinderCache);
	}

	/**
	 * Removes the am image entry where configurationUuid = &#63; and fileVersionId = &#63; from the database.
	 *
	 * @param configurationUuid the configuration uuid
	 * @param fileVersionId the file version ID
	 * @return the am image entry that was removed
	 */
	public static AMImageEntry removeByC_F(
			String configurationUuid, long fileVersionId)
		throws com.liferay.adaptive.media.image.exception.
			NoSuchAMImageEntryException {

		return getPersistence().removeByC_F(configurationUuid, fileVersionId);
	}

	/**
	 * Returns the number of am image entries where configurationUuid = &#63; and fileVersionId = &#63;.
	 *
	 * @param configurationUuid the configuration uuid
	 * @param fileVersionId the file version ID
	 * @return the number of matching am image entries
	 */
	public static int countByC_F(String configurationUuid, long fileVersionId) {
		return getPersistence().countByC_F(configurationUuid, fileVersionId);
	}

	/**
	 * Creates a new am image entry with the primary key. Does not add the am image entry to the database.
	 *
	 * @param amImageEntryId the primary key for the new am image entry
	 * @return the new am image entry
	 */
	public static AMImageEntry create(long amImageEntryId) {
		return getPersistence().create(amImageEntryId);
	}

	/**
	 * Removes the am image entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param amImageEntryId the primary key of the am image entry
	 * @return the am image entry that was removed
	 * @throws NoSuchAMImageEntryException if a am image entry with the primary key could not be found
	 */
	public static AMImageEntry remove(long amImageEntryId)
		throws com.liferay.adaptive.media.image.exception.
			NoSuchAMImageEntryException {

		return getPersistence().remove(amImageEntryId);
	}

	public static AMImageEntry updateImpl(AMImageEntry amImageEntry) {
		return getPersistence().updateImpl(amImageEntry);
	}

	/**
	 * Returns the am image entry with the primary key or throws a <code>NoSuchAMImageEntryException</code> if it could not be found.
	 *
	 * @param amImageEntryId the primary key of the am image entry
	 * @return the am image entry
	 * @throws NoSuchAMImageEntryException if a am image entry with the primary key could not be found
	 */
	public static AMImageEntry findByPrimaryKey(long amImageEntryId)
		throws com.liferay.adaptive.media.image.exception.
			NoSuchAMImageEntryException {

		return getPersistence().findByPrimaryKey(amImageEntryId);
	}

	/**
	 * Returns the am image entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param amImageEntryId the primary key of the am image entry
	 * @return the am image entry, or <code>null</code> if a am image entry with the primary key could not be found
	 */
	public static AMImageEntry fetchByPrimaryKey(long amImageEntryId) {
		return getPersistence().fetchByPrimaryKey(amImageEntryId);
	}

	/**
	 * Returns the am image entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public static AMImageEntry fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the am image entry where configurationUuid = &#63; and fileVersionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param configurationUuid the configuration uuid
	 * @param fileVersionId the file version ID
	 * @return the matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	public static AMImageEntry fetchByC_F(
		String configurationUuid, long fileVersionId) {

		return getPersistence().fetchByC_F(configurationUuid, fileVersionId);
	}

	/**
	 * Returns all the am image entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching am image entries
	 */
	public static List<AMImageEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the am image entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @return the range of matching am image entries
	 */
	public static List<AMImageEntry> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the am image entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching am image entries
	 */
	public static List<AMImageEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<AMImageEntry> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the am image entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching am image entries
	 */
	public static List<AMImageEntry> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the am image entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @return the range of matching am image entries
	 */
	public static List<AMImageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the am image entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching am image entries
	 */
	public static List<AMImageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AMImageEntry> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the am image entries where fileVersionId = &#63;.
	 *
	 * @param fileVersionId the file version ID
	 * @return the matching am image entries
	 */
	public static List<AMImageEntry> findByFileVersionId(long fileVersionId) {
		return getPersistence().findByFileVersionId(fileVersionId);
	}

	/**
	 * Returns a range of all the am image entries where fileVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileVersionId the file version ID
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @return the range of matching am image entries
	 */
	public static List<AMImageEntry> findByFileVersionId(
		long fileVersionId, int start, int end) {

		return getPersistence().findByFileVersionId(fileVersionId, start, end);
	}

	/**
	 * Returns an ordered range of all the am image entries where fileVersionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fileVersionId the file version ID
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching am image entries
	 */
	public static List<AMImageEntry> findByFileVersionId(
		long fileVersionId, int start, int end,
		OrderByComparator<AMImageEntry> orderByComparator) {

		return getPersistence().findByFileVersionId(
			fileVersionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the am image entries where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @return the matching am image entries
	 */
	public static List<AMImageEntry> findByC_C(
		long companyId, String configurationUuid) {

		return getPersistence().findByC_C(companyId, configurationUuid);
	}

	/**
	 * Returns a range of all the am image entries where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @return the range of matching am image entries
	 */
	public static List<AMImageEntry> findByC_C(
		long companyId, String configurationUuid, int start, int end) {

		return getPersistence().findByC_C(
			companyId, configurationUuid, start, end);
	}

	/**
	 * Returns an ordered range of all the am image entries where companyId = &#63; and configurationUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param configurationUuid the configuration uuid
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching am image entries
	 */
	public static List<AMImageEntry> findByC_C(
		long companyId, String configurationUuid, int start, int end,
		OrderByComparator<AMImageEntry> orderByComparator) {

		return getPersistence().findByC_C(
			companyId, configurationUuid, start, end, orderByComparator);
	}

	public static AMImageEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(AMImageEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile AMImageEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:98152299