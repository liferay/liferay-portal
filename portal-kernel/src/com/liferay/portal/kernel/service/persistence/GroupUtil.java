/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the group service. This utility wraps <code>com.liferay.portal.service.persistence.impl.GroupPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see GroupPersistence
 * @generated
 */
public class GroupUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<Group> groups) {
		getPersistence().cacheResult(groups);
	}

	/**
	 * @see BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(Group group) {
		getPersistence().cacheResult(group);
	}

	/**
	 * @see BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(Group group) {
		getPersistence().clearCache(group);
	}

	/**
	 * @see BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, Group> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<Group> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<Group> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<Group> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static Group update(Group group) {
		return getPersistence().update(group);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static Group update(Group group, ServiceContext serviceContext) {
		return getPersistence().update(group, serviceContext);
	}

	/**
	 * Returns an ordered range of all the groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByUuid_First(
			String uuid, OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByUuid_First(
		String uuid, OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the groups where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of groups where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching groups
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the group where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByUUID_G(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the group where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the group where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the group that was removed
	 */
	public static Group removeByUUID_G(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of groups where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching groups
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the groups where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of groups where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching groups
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByCompanyId_First(
			long companyId, OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByCompanyId_First(
		long companyId, OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of groups where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching groups
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns an ordered range of all the groups where liveGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param liveGroupId the live group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByLiveGroupId(
		long liveGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByLiveGroupId(
			liveGroupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByLiveGroupId_First(
			long liveGroupId, OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByLiveGroupId_First(
			liveGroupId, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByLiveGroupId_First(
		long liveGroupId, OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByLiveGroupId_First(
			liveGroupId, orderByComparator);
	}

	/**
	 * Removes all the groups where liveGroupId = &#63; from the database.
	 *
	 * @param liveGroupId the live group ID
	 */
	public static void removeByLiveGroupId(long liveGroupId) {
		getPersistence().removeByLiveGroupId(liveGroupId);
	}

	/**
	 * Returns the number of groups where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @return the number of matching groups
	 */
	public static int countByLiveGroupId(long liveGroupId) {
		return getPersistence().countByLiveGroupId(liveGroupId);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_C(
			companyId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_C_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public static void removeByC_C(long companyId, long classNameId) {
		getPersistence().removeByC_C(companyId, classNameId);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching groups
	 */
	public static int countByC_C(long companyId, long classNameId) {
		return getPersistence().countByC_C(companyId, classNameId);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_P(
		long companyId, long parentGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_P(
			companyId, parentGroupId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_P_First(
			long companyId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_P_First(
			companyId, parentGroupId, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_P_First(
		long companyId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_P_First(
			companyId, parentGroupId, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 */
	public static void removeByC_P(long companyId, long parentGroupId) {
		getPersistence().removeByC_P(companyId, parentGroupId);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	public static int countByC_P(long companyId, long parentGroupId) {
		return getPersistence().countByC_P(companyId, parentGroupId);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and groupKey = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_GK(
		long companyId, String[] groupKeys, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_GK(
			companyId, groupKeys, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the group where companyId = &#63; and groupKey = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_GK(long companyId, String groupKey)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_GK(companyId, groupKey);
	}

	/**
	 * Returns the group where companyId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_GK(
		long companyId, String groupKey, boolean useFinderCache) {

		return getPersistence().fetchByC_GK(
			companyId, groupKey, useFinderCache);
	}

	/**
	 * Removes the group where companyId = &#63; and groupKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the group that was removed
	 */
	public static Group removeByC_GK(long companyId, String groupKey)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().removeByC_GK(companyId, groupKey);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and groupKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the number of matching groups
	 */
	public static int countByC_GK(long companyId, String groupKey) {
		return getPersistence().countByC_GK(companyId, groupKey);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @return the number of matching groups
	 */
	public static int countByC_GK(long companyId, String[] groupKeys) {
		return getPersistence().countByC_GK(companyId, groupKeys);
	}

	/**
	 * Returns the group where companyId = &#63; and friendlyURL = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_F(long companyId, String friendlyURL)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_F(companyId, friendlyURL);
	}

	/**
	 * Returns the group where companyId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_F(
		long companyId, String friendlyURL, boolean useFinderCache) {

		return getPersistence().fetchByC_F(
			companyId, friendlyURL, useFinderCache);
	}

	/**
	 * Removes the group where companyId = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the group that was removed
	 */
	public static Group removeByC_F(long companyId, String friendlyURL)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().removeByC_F(companyId, friendlyURL);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the number of matching groups
	 */
	public static int countByC_F(long companyId, String friendlyURL) {
		return getPersistence().countByC_F(companyId, friendlyURL);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_S(
		long companyId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_S(
			companyId, site, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_S_First(
			long companyId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_S_First(
			companyId, site, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_S_First(
		long companyId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_S_First(
			companyId, site, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 */
	public static void removeByC_S(long companyId, boolean site) {
		getPersistence().removeByC_S(companyId, site);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	public static int countByC_S(long companyId, boolean site) {
		return getPersistence().countByC_S(companyId, site);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_A(
			companyId, active, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_A_First(
			long companyId, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_A_First(
			companyId, active, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_A_First(
		long companyId, boolean active,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_A_First(
			companyId, active, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	public static void removeByC_A(long companyId, boolean active) {
		getPersistence().removeByC_A(companyId, active);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching groups
	 */
	public static int countByC_A(long companyId, boolean active) {
		return getPersistence().countByC_A(companyId, active);
	}

	/**
	 * Returns an ordered range of all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_CPK(
			classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_CPK_First(
			long classNameId, long classPK,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_CPK_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_CPK_First(
		long classNameId, long classPK,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_CPK_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the groups where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByC_CPK(long classNameId, long classPK) {
		getPersistence().removeByC_CPK(classNameId, classPK);
	}

	/**
	 * Returns the number of groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching groups
	 */
	public static int countByC_CPK(long classNameId, long classPK) {
		return getPersistence().countByC_CPK(classNameId, classPK);
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_C_C(
			long companyId, long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_C_C(companyId, classNameId, classPK);
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_C_C(
		long companyId, long classNameId, long classPK,
		boolean useFinderCache) {

		return getPersistence().fetchByC_C_C(
			companyId, classNameId, classPK, useFinderCache);
	}

	/**
	 * Removes the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the group that was removed
	 */
	public static Group removeByC_C_C(
			long companyId, long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().removeByC_C_C(companyId, classNameId, classPK);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching groups
	 */
	public static int countByC_C_C(
		long companyId, long classNameId, long classPK) {

		return getPersistence().countByC_C_C(companyId, classNameId, classPK);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId, int start,
		int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C_P(
			companyId, classNameId, parentGroupId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_C_P_First(
			long companyId, long classNameId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_C_P_First(
			companyId, classNameId, parentGroupId, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_C_P_First(
		long companyId, long classNameId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_C_P_First(
			companyId, classNameId, parentGroupId, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 */
	public static void removeByC_C_P(
		long companyId, long classNameId, long parentGroupId) {

		getPersistence().removeByC_C_P(companyId, classNameId, parentGroupId);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	public static int countByC_C_P(
		long companyId, long classNameId, long parentGroupId) {

		return getPersistence().countByC_C_P(
			companyId, classNameId, parentGroupId);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_C_S(
			companyId, classNameId, site, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_C_S_First(
			long companyId, long classNameId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_C_S_First(
			companyId, classNameId, site, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_C_S_First(
		long companyId, long classNameId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_C_S_First(
			companyId, classNameId, site, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and classNameId = &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 */
	public static void removeByC_C_S(
		long companyId, long classNameId, boolean site) {

		getPersistence().removeByC_C_S(companyId, classNameId, site);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	public static int countByC_C_S(
		long companyId, long classNameId, boolean site) {

		return getPersistence().countByC_C_S(companyId, classNameId, site);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_P_S(
			companyId, parentGroupId, site, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_P_S_First(
			long companyId, long parentGroupId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_P_S_First(
			companyId, parentGroupId, site, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_P_S_First(
		long companyId, long parentGroupId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_P_S_First(
			companyId, parentGroupId, site, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 */
	public static void removeByC_P_S(
		long companyId, long parentGroupId, boolean site) {

		getPersistence().removeByC_P_S(companyId, parentGroupId, site);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	public static int countByC_P_S(
		long companyId, long parentGroupId, boolean site) {

		return getPersistence().countByC_P_S(companyId, parentGroupId, site);
	}

	/**
	 * Returns the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_L_GK(
			long companyId, long liveGroupId, String groupKey)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_L_GK(companyId, liveGroupId, groupKey);
	}

	/**
	 * Returns the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_L_GK(
		long companyId, long liveGroupId, String groupKey,
		boolean useFinderCache) {

		return getPersistence().fetchByC_L_GK(
			companyId, liveGroupId, groupKey, useFinderCache);
	}

	/**
	 * Removes the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the group that was removed
	 */
	public static Group removeByC_L_GK(
			long companyId, long liveGroupId, String groupKey)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().removeByC_L_GK(
			companyId, liveGroupId, groupKey);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the number of matching groups
	 */
	public static int countByC_L_GK(
		long companyId, long liveGroupId, String groupKey) {

		return getPersistence().countByC_L_GK(companyId, liveGroupId, groupKey);
	}

	/**
	 * Returns all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @return the matching groups
	 */
	public static List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site) {

		return getPersistence().findByC_LikeT_S(companyId, treePath, site);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site, int start, int end) {

		return getPersistence().findByC_LikeT_S(
			companyId, treePath, site, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_LikeT_S(
			companyId, treePath, site, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_LikeT_S(
			companyId, treePath, site, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_LikeT_S_First(
			long companyId, String treePath, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_LikeT_S_First(
			companyId, treePath, site, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_LikeT_S_First(
		long companyId, String treePath, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_LikeT_S_First(
			companyId, treePath, site, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 */
	public static void removeByC_LikeT_S(
		long companyId, String treePath, boolean site) {

		getPersistence().removeByC_LikeT_S(companyId, treePath, site);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @return the number of matching groups
	 */
	public static int countByC_LikeT_S(
		long companyId, String treePath, boolean site) {

		return getPersistence().countByC_LikeT_S(companyId, treePath, site);
	}

	/**
	 * Returns all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @return the matching groups
	 */
	public static List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site) {

		return getPersistence().findByC_LikeN_S(companyId, name, site);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site, int start, int end) {

		return getPersistence().findByC_LikeN_S(
			companyId, name, site, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_LikeN_S(
			companyId, name, site, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_LikeN_S(
			companyId, name, site, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_LikeN_S_First(
			long companyId, String name, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_LikeN_S_First(
			companyId, name, site, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_LikeN_S_First(
		long companyId, String name, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_LikeN_S_First(
			companyId, name, site, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and name LIKE &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 */
	public static void removeByC_LikeN_S(
		long companyId, String name, boolean site) {

		getPersistence().removeByC_LikeN_S(companyId, name, site);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @return the number of matching groups
	 */
	public static int countByC_LikeN_S(
		long companyId, String name, boolean site) {

		return getPersistence().countByC_LikeN_S(companyId, name, site);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_S_A(
		long companyId, boolean site, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_S_A(
			companyId, site, active, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_S_A_First(
			long companyId, boolean site, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_S_A_First(
			companyId, site, active, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_S_A_First(
		long companyId, boolean site, boolean active,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_S_A_First(
			companyId, site, active, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and site = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 */
	public static void removeByC_S_A(
		long companyId, boolean site, boolean active) {

		getPersistence().removeByC_S_A(companyId, site, active);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @return the number of matching groups
	 */
	public static int countByC_S_A(
		long companyId, boolean site, boolean active) {

		return getPersistence().countByC_S_A(companyId, site, active);
	}

	/**
	 * Returns all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the matching groups
	 */
	public static List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId) {

		return getPersistence().findByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId);
	}

	/**
	 * Returns a range of all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId,
		int start, int end) {

		return getPersistence().findByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId,
		int start, int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByGtG_C_C_P_First(
			long groupId, long companyId, long classNameId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByGtG_C_C_P_First(
			groupId, companyId, classNameId, parentGroupId, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByGtG_C_C_P_First(
		long groupId, long companyId, long classNameId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByGtG_C_C_P_First(
			groupId, companyId, classNameId, parentGroupId, orderByComparator);
	}

	/**
	 * Removes all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 */
	public static void removeByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId) {

		getPersistence().removeByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId);
	}

	/**
	 * Returns the number of groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	public static int countByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId) {

		return getPersistence().countByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId);
	}

	/**
	 * Returns all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the matching groups
	 */
	public static List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site) {

		return getPersistence().findByGtG_C_P_S(
			groupId, companyId, parentGroupId, site);
	}

	/**
	 * Returns a range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site,
		int start, int end) {

		return getPersistence().findByGtG_C_P_S(
			groupId, companyId, parentGroupId, site, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByGtG_C_P_S(
			groupId, companyId, parentGroupId, site, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site,
		int start, int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGtG_C_P_S(
			groupId, companyId, parentGroupId, site, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByGtG_C_P_S_First(
			long groupId, long companyId, long parentGroupId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByGtG_C_P_S_First(
			groupId, companyId, parentGroupId, site, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByGtG_C_P_S_First(
		long groupId, long companyId, long parentGroupId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByGtG_C_P_S_First(
			groupId, companyId, parentGroupId, site, orderByComparator);
	}

	/**
	 * Removes all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 */
	public static void removeByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site) {

		getPersistence().removeByGtG_C_P_S(
			groupId, companyId, parentGroupId, site);
	}

	/**
	 * Returns the number of groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	public static int countByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site) {

		return getPersistence().countByGtG_C_P_S(
			groupId, companyId, parentGroupId, site);
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_C_L_GK(
			long companyId, long classNameId, long liveGroupId, String groupKey)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_C_L_GK(
			companyId, classNameId, liveGroupId, groupKey);
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_C_L_GK(
		long companyId, long classNameId, long liveGroupId, String groupKey,
		boolean useFinderCache) {

		return getPersistence().fetchByC_C_L_GK(
			companyId, classNameId, liveGroupId, groupKey, useFinderCache);
	}

	/**
	 * Removes the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the group that was removed
	 */
	public static Group removeByC_C_L_GK(
			long companyId, long classNameId, long liveGroupId, String groupKey)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().removeByC_C_L_GK(
			companyId, classNameId, liveGroupId, groupKey);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the number of matching groups
	 */
	public static int countByC_C_L_GK(
		long companyId, long classNameId, long liveGroupId, String groupKey) {

		return getPersistence().countByC_C_L_GK(
			companyId, classNameId, liveGroupId, groupKey);
	}

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @return the matching groups
	 */
	public static List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site) {

		return getPersistence().findByC_P_LikeN_S(
			companyId, parentGroupId, name, site);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end) {

		return getPersistence().findByC_P_LikeN_S(
			companyId, parentGroupId, name, site, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_P_LikeN_S(
			companyId, parentGroupId, name, site, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_P_LikeN_S(
			companyId, parentGroupId, name, site, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_P_LikeN_S_First(
			long companyId, long parentGroupId, String name, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_P_LikeN_S_First(
			companyId, parentGroupId, name, site, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_P_LikeN_S_First(
		long companyId, long parentGroupId, String name, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_P_LikeN_S_First(
			companyId, parentGroupId, name, site, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 */
	public static void removeByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site) {

		getPersistence().removeByC_P_LikeN_S(
			companyId, parentGroupId, name, site);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @return the number of matching groups
	 */
	public static int countByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site) {

		return getPersistence().countByC_P_LikeN_S(
			companyId, parentGroupId, name, site);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByC_P_S_I_First(
			long companyId, long parentGroupId, boolean site,
			boolean inheritContent, OrderByComparator<Group> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByC_P_S_I_First(
			companyId, parentGroupId, site, inheritContent, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_P_S_I_First(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, OrderByComparator<Group> orderByComparator) {

		return getPersistence().fetchByC_P_S_I_First(
			companyId, parentGroupId, site, inheritContent, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 */
	public static void removeByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent) {

		getPersistence().removeByC_P_S_I(
			companyId, parentGroupId, site, inheritContent);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @return the number of matching groups
	 */
	public static int countByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent) {

		return getPersistence().countByC_P_S_I(
			companyId, parentGroupId, site, inheritContent);
	}

	/**
	 * Returns the group where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	public static Group findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the group where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the group where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the group that was removed
	 */
	public static Group removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of groups where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching groups
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new group with the primary key. Does not add the group to the database.
	 *
	 * @param groupId the primary key for the new group
	 * @return the new group
	 */
	public static Group create(long groupId) {
		return getPersistence().create(groupId);
	}

	/**
	 * Removes the group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param groupId the primary key of the group
	 * @return the group that was removed
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	public static Group remove(long groupId)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().remove(groupId);
	}

	public static Group updateImpl(Group group) {
		return getPersistence().updateImpl(group);
	}

	/**
	 * Returns the group with the primary key or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param groupId the primary key of the group
	 * @return the group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	public static Group findByPrimaryKey(long groupId)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getPersistence().findByPrimaryKey(groupId);
	}

	/**
	 * Returns the group with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param groupId the primary key of the group
	 * @return the group, or <code>null</code> if a group with the primary key could not be found
	 */
	public static Group fetchByPrimaryKey(long groupId) {
		return getPersistence().fetchByPrimaryKey(groupId);
	}

	/**
	 * Returns the primaryKeys of organizations associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of organizations associated with the group
	 */
	public static long[] getOrganizationPrimaryKeys(long pk) {
		return getPersistence().getOrganizationPrimaryKeys(pk);
	}

	/**
	 * Returns all the organizations associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the organizations associated with the group
	 */
	public static List<com.liferay.portal.kernel.model.Organization>
		getOrganizations(long pk) {

		return getPersistence().getOrganizations(pk);
	}

	/**
	 * Returns a range of all the organizations associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of organizations associated with the group
	 */
	public static List<com.liferay.portal.kernel.model.Organization>
		getOrganizations(long pk, int start, int end) {

		return getPersistence().getOrganizations(pk, start, end);
	}

	/**
	 * Returns an ordered range of all the organizations associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of organizations associated with the group
	 */
	public static List<com.liferay.portal.kernel.model.Organization>
		getOrganizations(
			long pk, int start, int end,
			OrderByComparator<com.liferay.portal.kernel.model.Organization>
				orderByComparator) {

		return getPersistence().getOrganizations(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of organizations associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of organizations associated with the group
	 */
	public static int getOrganizationsSize(long pk) {
		return getPersistence().getOrganizationsSize(pk);
	}

	/**
	 * Returns <code>true</code> if the organization is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPK the primary key of the organization
	 * @return <code>true</code> if the organization is associated with the group; <code>false</code> otherwise
	 */
	public static boolean containsOrganization(long pk, long organizationPK) {
		return getPersistence().containsOrganization(pk, organizationPK);
	}

	/**
	 * Returns <code>true</code> if the group has any organizations associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with organizations
	 * @return <code>true</code> if the group has any organizations associated with it; <code>false</code> otherwise
	 */
	public static boolean containsOrganizations(long pk) {
		return getPersistence().containsOrganizations(pk);
	}

	/**
	 * Adds an association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPK the primary key of the organization
	 * @return <code>true</code> if an association between the group and the organization was added; <code>false</code> if they were already associated
	 */
	public static boolean addOrganization(long pk, long organizationPK) {
		return getPersistence().addOrganization(pk, organizationPK);
	}

	/**
	 * Adds an association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organization the organization
	 * @return <code>true</code> if an association between the group and the organization was added; <code>false</code> if they were already associated
	 */
	public static boolean addOrganization(
		long pk, com.liferay.portal.kernel.model.Organization organization) {

		return getPersistence().addOrganization(pk, organization);
	}

	/**
	 * Adds an association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPKs the primary keys of the organizations
	 * @return <code>true</code> if at least one association between the group and the organizations was added; <code>false</code> if they were all already associated
	 */
	public static boolean addOrganizations(long pk, long[] organizationPKs) {
		return getPersistence().addOrganizations(pk, organizationPKs);
	}

	/**
	 * Adds an association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizations the organizations
	 * @return <code>true</code> if at least one association between the group and the organizations was added; <code>false</code> if they were all already associated
	 */
	public static boolean addOrganizations(
		long pk,
		List<com.liferay.portal.kernel.model.Organization> organizations) {

		return getPersistence().addOrganizations(pk, organizations);
	}

	/**
	 * Clears all associations between the group and its organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated organizations from
	 */
	public static void clearOrganizations(long pk) {
		getPersistence().clearOrganizations(pk);
	}

	/**
	 * Removes the association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPK the primary key of the organization
	 */
	public static void removeOrganization(long pk, long organizationPK) {
		getPersistence().removeOrganization(pk, organizationPK);
	}

	/**
	 * Removes the association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organization the organization
	 */
	public static void removeOrganization(
		long pk, com.liferay.portal.kernel.model.Organization organization) {

		getPersistence().removeOrganization(pk, organization);
	}

	/**
	 * Removes the association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPKs the primary keys of the organizations
	 */
	public static void removeOrganizations(long pk, long[] organizationPKs) {
		getPersistence().removeOrganizations(pk, organizationPKs);
	}

	/**
	 * Removes the association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizations the organizations
	 */
	public static void removeOrganizations(
		long pk,
		List<com.liferay.portal.kernel.model.Organization> organizations) {

		getPersistence().removeOrganizations(pk, organizations);
	}

	/**
	 * Sets the organizations associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPKs the primary keys of the organizations to be associated with the group
	 */
	public static void setOrganizations(long pk, long[] organizationPKs) {
		getPersistence().setOrganizations(pk, organizationPKs);
	}

	/**
	 * Sets the organizations associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizations the organizations to be associated with the group
	 */
	public static void setOrganizations(
		long pk,
		List<com.liferay.portal.kernel.model.Organization> organizations) {

		getPersistence().setOrganizations(pk, organizations);
	}

	/**
	 * Returns the primaryKeys of roles associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of roles associated with the group
	 */
	public static long[] getRolePrimaryKeys(long pk) {
		return getPersistence().getRolePrimaryKeys(pk);
	}

	/**
	 * Returns all the roles associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the roles associated with the group
	 */
	public static List<com.liferay.portal.kernel.model.Role> getRoles(long pk) {
		return getPersistence().getRoles(pk);
	}

	/**
	 * Returns a range of all the roles associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of roles associated with the group
	 */
	public static List<com.liferay.portal.kernel.model.Role> getRoles(
		long pk, int start, int end) {

		return getPersistence().getRoles(pk, start, end);
	}

	/**
	 * Returns an ordered range of all the roles associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of roles associated with the group
	 */
	public static List<com.liferay.portal.kernel.model.Role> getRoles(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.Role>
			orderByComparator) {

		return getPersistence().getRoles(pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of roles associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of roles associated with the group
	 */
	public static int getRolesSize(long pk) {
		return getPersistence().getRolesSize(pk);
	}

	/**
	 * Returns <code>true</code> if the role is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param rolePK the primary key of the role
	 * @return <code>true</code> if the role is associated with the group; <code>false</code> otherwise
	 */
	public static boolean containsRole(long pk, long rolePK) {
		return getPersistence().containsRole(pk, rolePK);
	}

	/**
	 * Returns <code>true</code> if the group has any roles associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with roles
	 * @return <code>true</code> if the group has any roles associated with it; <code>false</code> otherwise
	 */
	public static boolean containsRoles(long pk) {
		return getPersistence().containsRoles(pk);
	}

	/**
	 * Adds an association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePK the primary key of the role
	 * @return <code>true</code> if an association between the group and the role was added; <code>false</code> if they were already associated
	 */
	public static boolean addRole(long pk, long rolePK) {
		return getPersistence().addRole(pk, rolePK);
	}

	/**
	 * Adds an association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param role the role
	 * @return <code>true</code> if an association between the group and the role was added; <code>false</code> if they were already associated
	 */
	public static boolean addRole(
		long pk, com.liferay.portal.kernel.model.Role role) {

		return getPersistence().addRole(pk, role);
	}

	/**
	 * Adds an association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePKs the primary keys of the roles
	 * @return <code>true</code> if at least one association between the group and the roles was added; <code>false</code> if they were all already associated
	 */
	public static boolean addRoles(long pk, long[] rolePKs) {
		return getPersistence().addRoles(pk, rolePKs);
	}

	/**
	 * Adds an association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param roles the roles
	 * @return <code>true</code> if at least one association between the group and the roles was added; <code>false</code> if they were all already associated
	 */
	public static boolean addRoles(
		long pk, List<com.liferay.portal.kernel.model.Role> roles) {

		return getPersistence().addRoles(pk, roles);
	}

	/**
	 * Clears all associations between the group and its roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated roles from
	 */
	public static void clearRoles(long pk) {
		getPersistence().clearRoles(pk);
	}

	/**
	 * Removes the association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePK the primary key of the role
	 */
	public static void removeRole(long pk, long rolePK) {
		getPersistence().removeRole(pk, rolePK);
	}

	/**
	 * Removes the association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param role the role
	 */
	public static void removeRole(
		long pk, com.liferay.portal.kernel.model.Role role) {

		getPersistence().removeRole(pk, role);
	}

	/**
	 * Removes the association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePKs the primary keys of the roles
	 */
	public static void removeRoles(long pk, long[] rolePKs) {
		getPersistence().removeRoles(pk, rolePKs);
	}

	/**
	 * Removes the association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param roles the roles
	 */
	public static void removeRoles(
		long pk, List<com.liferay.portal.kernel.model.Role> roles) {

		getPersistence().removeRoles(pk, roles);
	}

	/**
	 * Sets the roles associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePKs the primary keys of the roles to be associated with the group
	 */
	public static void setRoles(long pk, long[] rolePKs) {
		getPersistence().setRoles(pk, rolePKs);
	}

	/**
	 * Sets the roles associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param roles the roles to be associated with the group
	 */
	public static void setRoles(
		long pk, List<com.liferay.portal.kernel.model.Role> roles) {

		getPersistence().setRoles(pk, roles);
	}

	/**
	 * Returns the primaryKeys of user groups associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of user groups associated with the group
	 */
	public static long[] getUserGroupPrimaryKeys(long pk) {
		return getPersistence().getUserGroupPrimaryKeys(pk);
	}

	/**
	 * Returns all the user groups associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the user groups associated with the group
	 */
	public static List<com.liferay.portal.kernel.model.UserGroup> getUserGroups(
		long pk) {

		return getPersistence().getUserGroups(pk);
	}

	/**
	 * Returns a range of all the user groups associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of user groups associated with the group
	 */
	public static List<com.liferay.portal.kernel.model.UserGroup> getUserGroups(
		long pk, int start, int end) {

		return getPersistence().getUserGroups(pk, start, end);
	}

	/**
	 * Returns an ordered range of all the user groups associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of user groups associated with the group
	 */
	public static List<com.liferay.portal.kernel.model.UserGroup> getUserGroups(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.UserGroup>
			orderByComparator) {

		return getPersistence().getUserGroups(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of user groups associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of user groups associated with the group
	 */
	public static int getUserGroupsSize(long pk) {
		return getPersistence().getUserGroupsSize(pk);
	}

	/**
	 * Returns <code>true</code> if the user group is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPK the primary key of the user group
	 * @return <code>true</code> if the user group is associated with the group; <code>false</code> otherwise
	 */
	public static boolean containsUserGroup(long pk, long userGroupPK) {
		return getPersistence().containsUserGroup(pk, userGroupPK);
	}

	/**
	 * Returns <code>true</code> if the group has any user groups associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with user groups
	 * @return <code>true</code> if the group has any user groups associated with it; <code>false</code> otherwise
	 */
	public static boolean containsUserGroups(long pk) {
		return getPersistence().containsUserGroups(pk);
	}

	/**
	 * Adds an association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPK the primary key of the user group
	 * @return <code>true</code> if an association between the group and the user group was added; <code>false</code> if they were already associated
	 */
	public static boolean addUserGroup(long pk, long userGroupPK) {
		return getPersistence().addUserGroup(pk, userGroupPK);
	}

	/**
	 * Adds an association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroup the user group
	 * @return <code>true</code> if an association between the group and the user group was added; <code>false</code> if they were already associated
	 */
	public static boolean addUserGroup(
		long pk, com.liferay.portal.kernel.model.UserGroup userGroup) {

		return getPersistence().addUserGroup(pk, userGroup);
	}

	/**
	 * Adds an association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPKs the primary keys of the user groups
	 * @return <code>true</code> if at least one association between the group and the user groups was added; <code>false</code> if they were all already associated
	 */
	public static boolean addUserGroups(long pk, long[] userGroupPKs) {
		return getPersistence().addUserGroups(pk, userGroupPKs);
	}

	/**
	 * Adds an association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroups the user groups
	 * @return <code>true</code> if at least one association between the group and the user groups was added; <code>false</code> if they were all already associated
	 */
	public static boolean addUserGroups(
		long pk, List<com.liferay.portal.kernel.model.UserGroup> userGroups) {

		return getPersistence().addUserGroups(pk, userGroups);
	}

	/**
	 * Clears all associations between the group and its user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated user groups from
	 */
	public static void clearUserGroups(long pk) {
		getPersistence().clearUserGroups(pk);
	}

	/**
	 * Removes the association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPK the primary key of the user group
	 */
	public static void removeUserGroup(long pk, long userGroupPK) {
		getPersistence().removeUserGroup(pk, userGroupPK);
	}

	/**
	 * Removes the association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroup the user group
	 */
	public static void removeUserGroup(
		long pk, com.liferay.portal.kernel.model.UserGroup userGroup) {

		getPersistence().removeUserGroup(pk, userGroup);
	}

	/**
	 * Removes the association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPKs the primary keys of the user groups
	 */
	public static void removeUserGroups(long pk, long[] userGroupPKs) {
		getPersistence().removeUserGroups(pk, userGroupPKs);
	}

	/**
	 * Removes the association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroups the user groups
	 */
	public static void removeUserGroups(
		long pk, List<com.liferay.portal.kernel.model.UserGroup> userGroups) {

		getPersistence().removeUserGroups(pk, userGroups);
	}

	/**
	 * Sets the user groups associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPKs the primary keys of the user groups to be associated with the group
	 */
	public static void setUserGroups(long pk, long[] userGroupPKs) {
		getPersistence().setUserGroups(pk, userGroupPKs);
	}

	/**
	 * Sets the user groups associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroups the user groups to be associated with the group
	 */
	public static void setUserGroups(
		long pk, List<com.liferay.portal.kernel.model.UserGroup> userGroups) {

		getPersistence().setUserGroups(pk, userGroups);
	}

	/**
	 * Returns the primaryKeys of users associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of users associated with the group
	 */
	public static long[] getUserPrimaryKeys(long pk) {
		return getPersistence().getUserPrimaryKeys(pk);
	}

	/**
	 * Returns all the users associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the users associated with the group
	 */
	public static List<com.liferay.portal.kernel.model.User> getUsers(long pk) {
		return getPersistence().getUsers(pk);
	}

	/**
	 * Returns a range of all the users associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of users associated with the group
	 */
	public static List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end) {

		return getPersistence().getUsers(pk, start, end);
	}

	/**
	 * Returns an ordered range of all the users associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of users associated with the group
	 */
	public static List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.User>
			orderByComparator) {

		return getPersistence().getUsers(pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of users associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of users associated with the group
	 */
	public static int getUsersSize(long pk) {
		return getPersistence().getUsersSize(pk);
	}

	/**
	 * Returns <code>true</code> if the user is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if the user is associated with the group; <code>false</code> otherwise
	 */
	public static boolean containsUser(long pk, long userPK) {
		return getPersistence().containsUser(pk, userPK);
	}

	/**
	 * Returns <code>true</code> if the group has any users associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with users
	 * @return <code>true</code> if the group has any users associated with it; <code>false</code> otherwise
	 */
	public static boolean containsUsers(long pk) {
		return getPersistence().containsUsers(pk);
	}

	/**
	 * Adds an association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if an association between the group and the user was added; <code>false</code> if they were already associated
	 */
	public static boolean addUser(long pk, long userPK) {
		return getPersistence().addUser(pk, userPK);
	}

	/**
	 * Adds an association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param user the user
	 * @return <code>true</code> if an association between the group and the user was added; <code>false</code> if they were already associated
	 */
	public static boolean addUser(
		long pk, com.liferay.portal.kernel.model.User user) {

		return getPersistence().addUser(pk, user);
	}

	/**
	 * Adds an association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPKs the primary keys of the users
	 * @return <code>true</code> if at least one association between the group and the users was added; <code>false</code> if they were all already associated
	 */
	public static boolean addUsers(long pk, long[] userPKs) {
		return getPersistence().addUsers(pk, userPKs);
	}

	/**
	 * Adds an association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param users the users
	 * @return <code>true</code> if at least one association between the group and the users was added; <code>false</code> if they were all already associated
	 */
	public static boolean addUsers(
		long pk, List<com.liferay.portal.kernel.model.User> users) {

		return getPersistence().addUsers(pk, users);
	}

	/**
	 * Clears all associations between the group and its users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated users from
	 */
	public static void clearUsers(long pk) {
		getPersistence().clearUsers(pk);
	}

	/**
	 * Removes the association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPK the primary key of the user
	 */
	public static void removeUser(long pk, long userPK) {
		getPersistence().removeUser(pk, userPK);
	}

	/**
	 * Removes the association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param user the user
	 */
	public static void removeUser(
		long pk, com.liferay.portal.kernel.model.User user) {

		getPersistence().removeUser(pk, user);
	}

	/**
	 * Removes the association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPKs the primary keys of the users
	 */
	public static void removeUsers(long pk, long[] userPKs) {
		getPersistence().removeUsers(pk, userPKs);
	}

	/**
	 * Removes the association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param users the users
	 */
	public static void removeUsers(
		long pk, List<com.liferay.portal.kernel.model.User> users) {

		getPersistence().removeUsers(pk, users);
	}

	/**
	 * Sets the users associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPKs the primary keys of the users to be associated with the group
	 */
	public static void setUsers(long pk, long[] userPKs) {
		getPersistence().setUsers(pk, userPKs);
	}

	/**
	 * Sets the users associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param users the users to be associated with the group
	 */
	public static void setUsers(
		long pk, List<com.liferay.portal.kernel.model.User> users) {

		getPersistence().setUsers(pk, users);
	}

	/**
	 * Returns the group where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the group where companyId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_GK(long companyId, String groupKey) {
		return getPersistence().fetchByC_GK(companyId, groupKey);
	}

	/**
	 * Returns the group where companyId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_F(long companyId, String friendlyURL) {
		return getPersistence().fetchByC_F(companyId, friendlyURL);
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_C_C(
		long companyId, long classNameId, long classPK) {

		return getPersistence().fetchByC_C_C(companyId, classNameId, classPK);
	}

	/**
	 * Returns the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_L_GK(
		long companyId, long liveGroupId, String groupKey) {

		return getPersistence().fetchByC_L_GK(companyId, liveGroupId, groupKey);
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByC_C_L_GK(
		long companyId, long classNameId, long liveGroupId, String groupKey) {

		return getPersistence().fetchByC_C_L_GK(
			companyId, classNameId, liveGroupId, groupKey);
	}

	/**
	 * Returns the group where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	public static Group fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the groups where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching groups
	 */
	public static List<Group> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByUuid(String uuid, int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching groups
	 */
	public static List<Group> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching groups
	 */
	public static List<Group> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @return the matching groups
	 */
	public static List<Group> findByLiveGroupId(long liveGroupId) {
		return getPersistence().findByLiveGroupId(liveGroupId);
	}

	/**
	 * Returns a range of all the groups where liveGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param liveGroupId the live group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByLiveGroupId(
		long liveGroupId, int start, int end) {

		return getPersistence().findByLiveGroupId(liveGroupId, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where liveGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param liveGroupId the live group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByLiveGroupId(
		long liveGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByLiveGroupId(
			liveGroupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching groups
	 */
	public static List<Group> findByC_C(long companyId, long classNameId) {
		return getPersistence().findByC_C(companyId, classNameId);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_C(
		long companyId, long classNameId, int start, int end) {

		return getPersistence().findByC_C(companyId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_C(
			companyId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @return the matching groups
	 */
	public static List<Group> findByC_P(long companyId, long parentGroupId) {
		return getPersistence().findByC_P(companyId, parentGroupId);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_P(
		long companyId, long parentGroupId, int start, int end) {

		return getPersistence().findByC_P(companyId, parentGroupId, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_P(
		long companyId, long parentGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_P(
			companyId, parentGroupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @return the matching groups
	 */
	public static List<Group> findByC_GK(long companyId, String[] groupKeys) {
		return getPersistence().findByC_GK(companyId, groupKeys);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_GK(
		long companyId, String[] groupKeys, int start, int end) {

		return getPersistence().findByC_GK(companyId, groupKeys, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_GK(
		long companyId, String[] groupKeys, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_GK(
			companyId, groupKeys, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @return the matching groups
	 */
	public static List<Group> findByC_S(long companyId, boolean site) {
		return getPersistence().findByC_S(companyId, site);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_S(
		long companyId, boolean site, int start, int end) {

		return getPersistence().findByC_S(companyId, site, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_S(
		long companyId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_S(
			companyId, site, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the matching groups
	 */
	public static List<Group> findByC_A(long companyId, boolean active) {
		return getPersistence().findByC_A(companyId, active);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_A(
		long companyId, boolean active, int start, int end) {

		return getPersistence().findByC_A(companyId, active, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_A(
			companyId, active, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching groups
	 */
	public static List<Group> findByC_CPK(long classNameId, long classPK) {
		return getPersistence().findByC_CPK(classNameId, classPK);
	}

	/**
	 * Returns a range of all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_CPK(
		long classNameId, long classPK, int start, int end) {

		return getPersistence().findByC_CPK(classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_CPK(
			classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the matching groups
	 */
	public static List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId) {

		return getPersistence().findByC_C_P(
			companyId, classNameId, parentGroupId);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId, int start,
		int end) {

		return getPersistence().findByC_C_P(
			companyId, classNameId, parentGroupId, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId, int start,
		int end, OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_C_P(
			companyId, classNameId, parentGroupId, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @return the matching groups
	 */
	public static List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site) {

		return getPersistence().findByC_C_S(companyId, classNameId, site);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site, int start, int end) {

		return getPersistence().findByC_C_S(
			companyId, classNameId, site, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_C_S(
			companyId, classNameId, site, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the matching groups
	 */
	public static List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site) {

		return getPersistence().findByC_P_S(companyId, parentGroupId, site);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site, int start, int end) {

		return getPersistence().findByC_P_S(
			companyId, parentGroupId, site, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_P_S(
			companyId, parentGroupId, site, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @return the matching groups
	 */
	public static List<Group> findByC_S_A(
		long companyId, boolean site, boolean active) {

		return getPersistence().findByC_S_A(companyId, site, active);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_S_A(
		long companyId, boolean site, boolean active, int start, int end) {

		return getPersistence().findByC_S_A(
			companyId, site, active, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_S_A(
		long companyId, boolean site, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_S_A(
			companyId, site, active, start, end, orderByComparator);
	}

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @return the matching groups
	 */
	public static List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent) {

		return getPersistence().findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	public static List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, int start, int end) {

		return getPersistence().findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent, start, end);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	public static List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return getPersistence().findByC_P_S_I(
			companyId, parentGroupId, site, inheritContent, start, end,
			orderByComparator);
	}

	public static GroupPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(GroupPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile GroupPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:356312130