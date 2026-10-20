/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the object entry folder service. This utility wraps <code>com.liferay.object.service.persistence.impl.ObjectEntryFolderPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectEntryFolderPersistence
 * @generated
 */
public class ObjectEntryFolderUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<ObjectEntryFolder> objectEntryFolders) {
		getPersistence().cacheResult(objectEntryFolders);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(ObjectEntryFolder objectEntryFolder) {
		getPersistence().cacheResult(objectEntryFolder);
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
	public static void clearCache(ObjectEntryFolder objectEntryFolder) {
		getPersistence().clearCache(objectEntryFolder);
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
	public static Map<Serializable, ObjectEntryFolder> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ObjectEntryFolder> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ObjectEntryFolder> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ObjectEntryFolder> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ObjectEntryFolder update(
		ObjectEntryFolder objectEntryFolder) {

		return getPersistence().update(objectEntryFolder);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ObjectEntryFolder update(
		ObjectEntryFolder objectEntryFolder, ServiceContext serviceContext) {

		return getPersistence().update(objectEntryFolder, serviceContext);
	}

	/**
	 * Returns an ordered range of all the object entry folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder findByUuid_First(
			String uuid, OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryFolderException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first object entry folder in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder fetchByUuid_First(
		String uuid, OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the object entry folders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of object entry folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object entry folders
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the object entry folder where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchObjectEntryFolderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder findByUUID_G(String uuid, long groupId)
		throws com.liferay.object.exception.NoSuchObjectEntryFolderException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the object entry folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the object entry folder where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the object entry folder that was removed
	 */
	public static ObjectEntryFolder removeByUUID_G(String uuid, long groupId)
		throws com.liferay.object.exception.NoSuchObjectEntryFolderException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of object entry folders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching object entry folders
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the object entry folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryFolderException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first object entry folder in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the object entry folders where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of object entry folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object entry folders
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the object entry folder where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; or throws a <code>NoSuchObjectEntryFolderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder findByERC_G_C(
			String externalReferenceCode, long groupId, long companyId)
		throws com.liferay.object.exception.NoSuchObjectEntryFolderException {

		return getPersistence().findByERC_G_C(
			externalReferenceCode, groupId, companyId);
	}

	/**
	 * Returns the object entry folder where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder fetchByERC_G_C(
		String externalReferenceCode, long groupId, long companyId,
		boolean useFinderCache) {

		return getPersistence().fetchByERC_G_C(
			externalReferenceCode, groupId, companyId, useFinderCache);
	}

	/**
	 * Removes the object entry folder where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the object entry folder that was removed
	 */
	public static ObjectEntryFolder removeByERC_G_C(
			String externalReferenceCode, long groupId, long companyId)
		throws com.liferay.object.exception.NoSuchObjectEntryFolderException {

		return getPersistence().removeByERC_G_C(
			externalReferenceCode, groupId, companyId);
	}

	/**
	 * Returns the number of object entry folders where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the number of matching object entry folders
	 */
	public static int countByERC_G_C(
		String externalReferenceCode, long groupId, long companyId) {

		return getPersistence().countByERC_G_C(
			externalReferenceCode, groupId, companyId);
	}

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end, OrderByComparator<ObjectEntryFolder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder findByG_C_P_First(
			long groupId, long companyId, long parentObjectEntryFolderId,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryFolderException {

		return getPersistence().findByG_C_P_First(
			groupId, companyId, parentObjectEntryFolderId, orderByComparator);
	}

	/**
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder fetchByG_C_P_First(
		long groupId, long companyId, long parentObjectEntryFolderId,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return getPersistence().fetchByG_C_P_First(
			groupId, companyId, parentObjectEntryFolderId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object entry folders that the user has permissions to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders that the user has permission to view
	 */
	public static List<ObjectEntryFolder> filterFindByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end, OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return getPersistence().filterFindByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 */
	public static void removeByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		getPersistence().removeByG_C_P(
			groupId, companyId, parentObjectEntryFolderId);
	}

	/**
	 * Returns the number of object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @return the number of matching object entry folders
	 */
	public static int countByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		return getPersistence().countByG_C_P(
			groupId, companyId, parentObjectEntryFolderId);
	}

	/**
	 * Returns the number of object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @return the number of matching object entry folders that the user has permission to view
	 */
	public static int filterCountByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		return getPersistence().filterCountByG_C_P(
			groupId, companyId, parentObjectEntryFolderId);
	}

	/**
	 * Returns all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @return the matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status) {

		return getPersistence().findByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status);
	}

	/**
	 * Returns a range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status, int start, int end) {

		return getPersistence().findByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status, start,
			end);
	}

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return getPersistence().findByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status, start,
			end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder
	 * @throws NoSuchObjectEntryFolderException if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder findByG_C_P_N_NotS_First(
			long groupId, long companyId, long parentObjectEntryFolderId,
			String name, int status,
			OrderByComparator<ObjectEntryFolder> orderByComparator)
		throws com.liferay.object.exception.NoSuchObjectEntryFolderException {

		return getPersistence().findByG_C_P_N_NotS_First(
			groupId, companyId, parentObjectEntryFolderId, name, status,
			orderByComparator);
	}

	/**
	 * Returns the first object entry folder in the ordered set where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder fetchByG_C_P_N_NotS_First(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return getPersistence().fetchByG_C_P_N_NotS_First(
			groupId, companyId, parentObjectEntryFolderId, name, status,
			orderByComparator);
	}

	/**
	 * Returns all the object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @return the matching object entry folders that the user has permission to view
	 */
	public static List<ObjectEntryFolder> filterFindByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status) {

		return getPersistence().filterFindByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status);
	}

	/**
	 * Returns a range of all the object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders that the user has permission to view
	 */
	public static List<ObjectEntryFolder> filterFindByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status, int start, int end) {

		return getPersistence().filterFindByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status, start,
			end);
	}

	/**
	 * Returns an ordered range of all the object entry folders that the user has permissions to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders that the user has permission to view
	 */
	public static List<ObjectEntryFolder> filterFindByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return getPersistence().filterFindByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status, start,
			end, orderByComparator);
	}

	/**
	 * Removes all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 */
	public static void removeByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status) {

		getPersistence().removeByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status);
	}

	/**
	 * Returns the number of object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching object entry folders
	 */
	public static int countByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status) {

		return getPersistence().countByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status);
	}

	/**
	 * Returns the number of object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63; and name = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching object entry folders that the user has permission to view
	 */
	public static int filterCountByG_C_P_N_NotS(
		long groupId, long companyId, long parentObjectEntryFolderId,
		String name, int status) {

		return getPersistence().filterCountByG_C_P_N_NotS(
			groupId, companyId, parentObjectEntryFolderId, name, status);
	}

	/**
	 * Creates a new object entry folder with the primary key. Does not add the object entry folder to the database.
	 *
	 * @param objectEntryFolderId the primary key for the new object entry folder
	 * @return the new object entry folder
	 */
	public static ObjectEntryFolder create(long objectEntryFolderId) {
		return getPersistence().create(objectEntryFolderId);
	}

	/**
	 * Removes the object entry folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder that was removed
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	public static ObjectEntryFolder remove(long objectEntryFolderId)
		throws com.liferay.object.exception.NoSuchObjectEntryFolderException {

		return getPersistence().remove(objectEntryFolderId);
	}

	public static ObjectEntryFolder updateImpl(
		ObjectEntryFolder objectEntryFolder) {

		return getPersistence().updateImpl(objectEntryFolder);
	}

	/**
	 * Returns the object entry folder with the primary key or throws a <code>NoSuchObjectEntryFolderException</code> if it could not be found.
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder
	 * @throws NoSuchObjectEntryFolderException if a object entry folder with the primary key could not be found
	 */
	public static ObjectEntryFolder findByPrimaryKey(long objectEntryFolderId)
		throws com.liferay.object.exception.NoSuchObjectEntryFolderException {

		return getPersistence().findByPrimaryKey(objectEntryFolderId);
	}

	/**
	 * Returns the object entry folder with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder, or <code>null</code> if a object entry folder with the primary key could not be found
	 */
	public static ObjectEntryFolder fetchByPrimaryKey(
		long objectEntryFolderId) {

		return getPersistence().fetchByPrimaryKey(objectEntryFolderId);
	}

	/**
	 * Returns the object entry folder where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the object entry folder where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder fetchByERC_G_C(
		String externalReferenceCode, long groupId, long companyId) {

		return getPersistence().fetchByERC_G_C(
			externalReferenceCode, groupId, companyId);
	}

	/**
	 * Returns all the object entry folders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the object entry folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the object entry folders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the object entry folders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the object entry folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the object entry folders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @return the matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		return getPersistence().findByG_C_P(
			groupId, companyId, parentObjectEntryFolderId);
	}

	/**
	 * Returns a range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end) {

		return getPersistence().findByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, start, end);
	}

	/**
	 * Returns an ordered range of all the object entry folders where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entry folders
	 */
	public static List<ObjectEntryFolder> findByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end, OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return getPersistence().findByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @return the matching object entry folders that the user has permission to view
	 */
	public static List<ObjectEntryFolder> filterFindByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		return getPersistence().filterFindByG_C_P(
			groupId, companyId, parentObjectEntryFolderId);
	}

	/**
	 * Returns a range of all the object entry folders that the user has permission to view where groupId = &#63; and companyId = &#63; and parentObjectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentObjectEntryFolderId the parent object entry folder ID
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of matching object entry folders that the user has permission to view
	 */
	public static List<ObjectEntryFolder> filterFindByG_C_P(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end) {

		return getPersistence().filterFindByG_C_P(
			groupId, companyId, parentObjectEntryFolderId, start, end);
	}

	public static ObjectEntryFolderPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		ObjectEntryFolderPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile ObjectEntryFolderPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-194649572