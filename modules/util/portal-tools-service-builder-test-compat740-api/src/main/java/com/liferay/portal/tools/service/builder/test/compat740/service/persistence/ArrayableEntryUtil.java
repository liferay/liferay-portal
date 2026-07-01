/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.compat740.model.ArrayableEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the arrayable entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.ArrayableEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ArrayableEntryPersistence
 * @generated
 */
public class ArrayableEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<ArrayableEntry> arrayableEntries) {
		getPersistence().cacheResult(arrayableEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(ArrayableEntry arrayableEntry) {
		getPersistence().cacheResult(arrayableEntry);
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
	public static void clearCache(ArrayableEntry arrayableEntry) {
		getPersistence().clearCache(arrayableEntry);
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
	public static Map<Serializable, ArrayableEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ArrayableEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ArrayableEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ArrayableEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ArrayableEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ArrayableEntry update(ArrayableEntry arrayableEntry) {
		return getPersistence().update(arrayableEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ArrayableEntry update(
		ArrayableEntry arrayableEntry, ServiceContext serviceContext) {

		return getPersistence().update(arrayableEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the arrayable entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching arrayable entries
	 */
	public static List<ArrayableEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<ArrayableEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first arrayable entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching arrayable entry
	 * @throws NoSuchArrayableEntryException if a matching arrayable entry could not be found
	 */
	public static ArrayableEntry findByGroupId_First(
			long groupId, OrderByComparator<ArrayableEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.compat740.
			exception.NoSuchArrayableEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first arrayable entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching arrayable entry, or <code>null</code> if a matching arrayable entry could not be found
	 */
	public static ArrayableEntry fetchByGroupId_First(
		long groupId, OrderByComparator<ArrayableEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the arrayable entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching arrayable entries
	 */
	public static List<ArrayableEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<ArrayableEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupIds, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the arrayable entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of arrayable entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching arrayable entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of arrayable entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching arrayable entries
	 */
	public static int countByGroupId(long[] groupIds) {
		return getPersistence().countByGroupId(groupIds);
	}

	/**
	 * Creates a new arrayable entry with the primary key. Does not add the arrayable entry to the database.
	 *
	 * @param arrayableEntryId the primary key for the new arrayable entry
	 * @return the new arrayable entry
	 */
	public static ArrayableEntry create(long arrayableEntryId) {
		return getPersistence().create(arrayableEntryId);
	}

	/**
	 * Removes the arrayable entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param arrayableEntryId the primary key of the arrayable entry
	 * @return the arrayable entry that was removed
	 * @throws NoSuchArrayableEntryException if a arrayable entry with the primary key could not be found
	 */
	public static ArrayableEntry remove(long arrayableEntryId)
		throws com.liferay.portal.tools.service.builder.test.compat740.
			exception.NoSuchArrayableEntryException {

		return getPersistence().remove(arrayableEntryId);
	}

	public static ArrayableEntry updateImpl(ArrayableEntry arrayableEntry) {
		return getPersistence().updateImpl(arrayableEntry);
	}

	/**
	 * Returns the arrayable entry with the primary key or throws a <code>NoSuchArrayableEntryException</code> if it could not be found.
	 *
	 * @param arrayableEntryId the primary key of the arrayable entry
	 * @return the arrayable entry
	 * @throws NoSuchArrayableEntryException if a arrayable entry with the primary key could not be found
	 */
	public static ArrayableEntry findByPrimaryKey(long arrayableEntryId)
		throws com.liferay.portal.tools.service.builder.test.compat740.
			exception.NoSuchArrayableEntryException {

		return getPersistence().findByPrimaryKey(arrayableEntryId);
	}

	/**
	 * Returns the arrayable entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param arrayableEntryId the primary key of the arrayable entry
	 * @return the arrayable entry, or <code>null</code> if a arrayable entry with the primary key could not be found
	 */
	public static ArrayableEntry fetchByPrimaryKey(long arrayableEntryId) {
		return getPersistence().fetchByPrimaryKey(arrayableEntryId);
	}

	/**
	 * Returns all the arrayable entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching arrayable entries
	 */
	public static List<ArrayableEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the arrayable entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @return the range of matching arrayable entries
	 */
	public static List<ArrayableEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the arrayable entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching arrayable entries
	 */
	public static List<ArrayableEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<ArrayableEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the arrayable entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching arrayable entries
	 */
	public static List<ArrayableEntry> findByGroupId(long[] groupIds) {
		return getPersistence().findByGroupId(groupIds);
	}

	/**
	 * Returns a range of all the arrayable entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @return the range of matching arrayable entries
	 */
	public static List<ArrayableEntry> findByGroupId(
		long[] groupIds, int start, int end) {

		return getPersistence().findByGroupId(groupIds, start, end);
	}

	/**
	 * Returns an ordered range of all the arrayable entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching arrayable entries
	 */
	public static List<ArrayableEntry> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<ArrayableEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupIds, start, end, orderByComparator);
	}

	public static ArrayableEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(ArrayableEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile ArrayableEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1129923496