/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.PermissionCheckFinderEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the permission check finder entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.PermissionCheckFinderEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PermissionCheckFinderEntryPersistence
 * @generated
 */
public class PermissionCheckFinderEntryUtil {

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
	public static void clearCache(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		getPersistence().clearCache(permissionCheckFinderEntry);
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
	public static Map<Serializable, PermissionCheckFinderEntry>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PermissionCheckFinderEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PermissionCheckFinderEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PermissionCheckFinderEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PermissionCheckFinderEntry update(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		return getPersistence().update(permissionCheckFinderEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PermissionCheckFinderEntry update(
		PermissionCheckFinderEntry permissionCheckFinderEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(
			permissionCheckFinderEntry, serviceContext);
	}

	/**
	 * Returns all the permission check finder entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the permission check finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of matching permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a matching permission check finder entry could not be found
	 */
	public static PermissionCheckFinderEntry findByGroupId_First(
			long groupId,
			OrderByComparator<PermissionCheckFinderEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchPermissionCheckFinderEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching permission check finder entry, or <code>null</code> if a matching permission check finder entry could not be found
	 */
	public static PermissionCheckFinderEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a matching permission check finder entry could not be found
	 */
	public static PermissionCheckFinderEntry findByGroupId_Last(
			long groupId,
			OrderByComparator<PermissionCheckFinderEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchPermissionCheckFinderEntryException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching permission check finder entry, or <code>null</code> if a matching permission check finder entry could not be found
	 */
	public static PermissionCheckFinderEntry fetchByGroupId_Last(
		long groupId,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the permission check finder entries before and after the current permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the current permission check finder entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	public static PermissionCheckFinderEntry[] findByGroupId_PrevAndNext(
			long permissionCheckFinderEntryId, long groupId,
			OrderByComparator<PermissionCheckFinderEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchPermissionCheckFinderEntryException {

		return getPersistence().findByGroupId_PrevAndNext(
			permissionCheckFinderEntryId, groupId, orderByComparator);
	}

	/**
	 * Returns all the permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching permission check finder entries that the user has permission to view
	 */
	public static List<PermissionCheckFinderEntry> filterFindByGroupId(
		long groupId) {

		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of matching permission check finder entries that the user has permission to view
	 */
	public static List<PermissionCheckFinderEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching permission check finder entries that the user has permission to view
	 */
	public static List<PermissionCheckFinderEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns the permission check finder entries before and after the current permission check finder entry in the ordered set of permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the current permission check finder entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	public static PermissionCheckFinderEntry[] filterFindByGroupId_PrevAndNext(
			long permissionCheckFinderEntryId, long groupId,
			OrderByComparator<PermissionCheckFinderEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchPermissionCheckFinderEntryException {

		return getPersistence().filterFindByGroupId_PrevAndNext(
			permissionCheckFinderEntryId, groupId, orderByComparator);
	}

	/**
	 * Returns all the permission check finder entries that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching permission check finder entries that the user has permission to view
	 */
	public static List<PermissionCheckFinderEntry> filterFindByGroupId(
		long[] groupIds) {

		return getPersistence().filterFindByGroupId(groupIds);
	}

	/**
	 * Returns a range of all the permission check finder entries that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of matching permission check finder entries that the user has permission to view
	 */
	public static List<PermissionCheckFinderEntry> filterFindByGroupId(
		long[] groupIds, int start, int end) {

		return getPersistence().filterFindByGroupId(groupIds, start, end);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching permission check finder entries that the user has permission to view
	 */
	public static List<PermissionCheckFinderEntry> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the permission check finder entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @return the matching permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry> findByGroupId(
		long[] groupIds) {

		return getPersistence().findByGroupId(groupIds);
	}

	/**
	 * Returns a range of all the permission check finder entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of matching permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry> findByGroupId(
		long[] groupIds, int start, int end) {

		return getPersistence().findByGroupId(groupIds, start, end);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupIds, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the permission check finder entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of permission check finder entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching permission check finder entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of permission check finder entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching permission check finder entries
	 */
	public static int countByGroupId(long[] groupIds) {
		return getPersistence().countByGroupId(groupIds);
	}

	/**
	 * Returns the number of permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching permission check finder entries that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns the number of permission check finder entries that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching permission check finder entries that the user has permission to view
	 */
	public static int filterCountByGroupId(long[] groupIds) {
		return getPersistence().filterCountByGroupId(groupIds);
	}

	/**
	 * Caches the permission check finder entry in the entity cache if it is enabled.
	 *
	 * @param permissionCheckFinderEntry the permission check finder entry
	 */
	public static void cacheResult(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		getPersistence().cacheResult(permissionCheckFinderEntry);
	}

	/**
	 * Caches the permission check finder entries in the entity cache if it is enabled.
	 *
	 * @param permissionCheckFinderEntries the permission check finder entries
	 */
	public static void cacheResult(
		List<PermissionCheckFinderEntry> permissionCheckFinderEntries) {

		getPersistence().cacheResult(permissionCheckFinderEntries);
	}

	/**
	 * Creates a new permission check finder entry with the primary key. Does not add the permission check finder entry to the database.
	 *
	 * @param permissionCheckFinderEntryId the primary key for the new permission check finder entry
	 * @return the new permission check finder entry
	 */
	public static PermissionCheckFinderEntry create(
		long permissionCheckFinderEntryId) {

		return getPersistence().create(permissionCheckFinderEntryId);
	}

	/**
	 * Removes the permission check finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry that was removed
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	public static PermissionCheckFinderEntry remove(
			long permissionCheckFinderEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchPermissionCheckFinderEntryException {

		return getPersistence().remove(permissionCheckFinderEntryId);
	}

	public static PermissionCheckFinderEntry updateImpl(
		PermissionCheckFinderEntry permissionCheckFinderEntry) {

		return getPersistence().updateImpl(permissionCheckFinderEntry);
	}

	/**
	 * Returns the permission check finder entry with the primary key or throws a <code>NoSuchPermissionCheckFinderEntryException</code> if it could not be found.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	public static PermissionCheckFinderEntry findByPrimaryKey(
			long permissionCheckFinderEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchPermissionCheckFinderEntryException {

		return getPersistence().findByPrimaryKey(permissionCheckFinderEntryId);
	}

	/**
	 * Returns the permission check finder entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry, or <code>null</code> if a permission check finder entry with the primary key could not be found
	 */
	public static PermissionCheckFinderEntry fetchByPrimaryKey(
		long permissionCheckFinderEntryId) {

		return getPersistence().fetchByPrimaryKey(permissionCheckFinderEntryId);
	}

	/**
	 * Returns all the permission check finder entries.
	 *
	 * @return the permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the permission check finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry> findAll(
		int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the permission check finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of permission check finder entries
	 */
	public static List<PermissionCheckFinderEntry> findAll(
		int start, int end,
		OrderByComparator<PermissionCheckFinderEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the permission check finder entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of permission check finder entries.
	 *
	 * @return the number of permission check finder entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static PermissionCheckFinderEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		PermissionCheckFinderEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile PermissionCheckFinderEntryPersistence _persistence;

}