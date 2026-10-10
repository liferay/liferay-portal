/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the filter primary entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.FilterPrimaryEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FilterPrimaryEntryPersistence
 * @generated
 */
public class FilterPrimaryEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<FilterPrimaryEntry> filterPrimaryEntries) {

		getPersistence().cacheResult(filterPrimaryEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(FilterPrimaryEntry filterPrimaryEntry) {
		getPersistence().cacheResult(filterPrimaryEntry);
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
	public static void clearCache(FilterPrimaryEntry filterPrimaryEntry) {
		getPersistence().clearCache(filterPrimaryEntry);
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
	public static Map<Serializable, FilterPrimaryEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FilterPrimaryEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FilterPrimaryEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FilterPrimaryEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FilterPrimaryEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FilterPrimaryEntry update(
		FilterPrimaryEntry filterPrimaryEntry) {

		return getPersistence().update(filterPrimaryEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FilterPrimaryEntry update(
		FilterPrimaryEntry filterPrimaryEntry, ServiceContext serviceContext) {

		return getPersistence().update(filterPrimaryEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the filter primary entries where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching filter primary entries
	 */
	public static List<FilterPrimaryEntry> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		OrderByComparator<FilterPrimaryEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByResourcePrimKey(
			resourcePrimKey, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first filter primary entry in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter primary entry
	 * @throws NoSuchFilterPrimaryEntryException if a matching filter primary entry could not be found
	 */
	public static FilterPrimaryEntry findByResourcePrimKey_First(
			long resourcePrimKey,
			OrderByComparator<FilterPrimaryEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFilterPrimaryEntryException {

		return getPersistence().findByResourcePrimKey_First(
			resourcePrimKey, orderByComparator);
	}

	/**
	 * Returns the first filter primary entry in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter primary entry, or <code>null</code> if a matching filter primary entry could not be found
	 */
	public static FilterPrimaryEntry fetchByResourcePrimKey_First(
		long resourcePrimKey,
		OrderByComparator<FilterPrimaryEntry> orderByComparator) {

		return getPersistence().fetchByResourcePrimKey_First(
			resourcePrimKey, orderByComparator);
	}

	/**
	 * Removes all the filter primary entries where resourcePrimKey = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 */
	public static void removeByResourcePrimKey(long resourcePrimKey) {
		getPersistence().removeByResourcePrimKey(resourcePrimKey);
	}

	/**
	 * Returns the number of filter primary entries where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the number of matching filter primary entries
	 */
	public static int countByResourcePrimKey(long resourcePrimKey) {
		return getPersistence().countByResourcePrimKey(resourcePrimKey);
	}

	/**
	 * Returns an ordered range of all the filter primary entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching filter primary entries
	 */
	public static List<FilterPrimaryEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FilterPrimaryEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first filter primary entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter primary entry
	 * @throws NoSuchFilterPrimaryEntryException if a matching filter primary entry could not be found
	 */
	public static FilterPrimaryEntry findByGroupId_First(
			long groupId,
			OrderByComparator<FilterPrimaryEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFilterPrimaryEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first filter primary entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching filter primary entry, or <code>null</code> if a matching filter primary entry could not be found
	 */
	public static FilterPrimaryEntry fetchByGroupId_First(
		long groupId, OrderByComparator<FilterPrimaryEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the filter primary entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter primary entries that the user has permission to view
	 */
	public static List<FilterPrimaryEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FilterPrimaryEntry> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Removes all the filter primary entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of filter primary entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching filter primary entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of filter primary entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching filter primary entries that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Creates a new filter primary entry with the primary key. Does not add the filter primary entry to the database.
	 *
	 * @param filterPrimaryEntryId the primary key for the new filter primary entry
	 * @return the new filter primary entry
	 */
	public static FilterPrimaryEntry create(long filterPrimaryEntryId) {
		return getPersistence().create(filterPrimaryEntryId);
	}

	/**
	 * Removes the filter primary entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param filterPrimaryEntryId the primary key of the filter primary entry
	 * @return the filter primary entry that was removed
	 * @throws NoSuchFilterPrimaryEntryException if a filter primary entry with the primary key could not be found
	 */
	public static FilterPrimaryEntry remove(long filterPrimaryEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFilterPrimaryEntryException {

		return getPersistence().remove(filterPrimaryEntryId);
	}

	public static FilterPrimaryEntry updateImpl(
		FilterPrimaryEntry filterPrimaryEntry) {

		return getPersistence().updateImpl(filterPrimaryEntry);
	}

	/**
	 * Returns the filter primary entry with the primary key or throws a <code>NoSuchFilterPrimaryEntryException</code> if it could not be found.
	 *
	 * @param filterPrimaryEntryId the primary key of the filter primary entry
	 * @return the filter primary entry
	 * @throws NoSuchFilterPrimaryEntryException if a filter primary entry with the primary key could not be found
	 */
	public static FilterPrimaryEntry findByPrimaryKey(long filterPrimaryEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFilterPrimaryEntryException {

		return getPersistence().findByPrimaryKey(filterPrimaryEntryId);
	}

	/**
	 * Returns the filter primary entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param filterPrimaryEntryId the primary key of the filter primary entry
	 * @return the filter primary entry, or <code>null</code> if a filter primary entry with the primary key could not be found
	 */
	public static FilterPrimaryEntry fetchByPrimaryKey(
		long filterPrimaryEntryId) {

		return getPersistence().fetchByPrimaryKey(filterPrimaryEntryId);
	}

	/**
	 * Returns all the filter primary entries where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the matching filter primary entries
	 */
	public static List<FilterPrimaryEntry> findByResourcePrimKey(
		long resourcePrimKey) {

		return getPersistence().findByResourcePrimKey(resourcePrimKey);
	}

	/**
	 * Returns a range of all the filter primary entries where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @return the range of matching filter primary entries
	 */
	public static List<FilterPrimaryEntry> findByResourcePrimKey(
		long resourcePrimKey, int start, int end) {

		return getPersistence().findByResourcePrimKey(
			resourcePrimKey, start, end);
	}

	/**
	 * Returns an ordered range of all the filter primary entries where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter primary entries
	 */
	public static List<FilterPrimaryEntry> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		OrderByComparator<FilterPrimaryEntry> orderByComparator) {

		return getPersistence().findByResourcePrimKey(
			resourcePrimKey, start, end, orderByComparator);
	}

	/**
	 * Returns all the filter primary entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching filter primary entries
	 */
	public static List<FilterPrimaryEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the filter primary entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @return the range of matching filter primary entries
	 */
	public static List<FilterPrimaryEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the filter primary entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching filter primary entries
	 */
	public static List<FilterPrimaryEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FilterPrimaryEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the filter primary entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching filter primary entries that the user has permission to view
	 */
	public static List<FilterPrimaryEntry> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the filter primary entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FilterPrimaryEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of filter primary entries
	 * @param end the upper bound of the range of filter primary entries (not inclusive)
	 * @return the range of matching filter primary entries that the user has permission to view
	 */
	public static List<FilterPrimaryEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	public static FilterPrimaryEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		FilterPrimaryEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile FilterPrimaryEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1081552474