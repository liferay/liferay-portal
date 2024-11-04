/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the ddm structure service. This utility wraps <code>com.liferay.dynamic.data.mapping.service.persistence.impl.DDMStructurePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructurePersistence
 * @generated
 */
public class DDMStructureUtil {

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
	public static void clearCache(DDMStructure ddmStructure) {
		getPersistence().clearCache(ddmStructure);
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
	public static Map<Serializable, DDMStructure> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DDMStructure> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DDMStructure> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DDMStructure> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DDMStructure update(DDMStructure ddmStructure) {
		return getPersistence().update(ddmStructure);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DDMStructure update(
		DDMStructure ddmStructure, ServiceContext serviceContext) {

		return getPersistence().update(ddmStructure, serviceContext);
	}

	/**
	 * Returns all the ddm structures where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the ddm structures where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByUuid_First(
			String uuid, OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByUuid_First(
		String uuid, OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByUuid_Last(
			String uuid, OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByUuid_Last(
		String uuid, OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] findByUuid_PrevAndNext(
			long structureId, String uuid,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByUuid_PrevAndNext(
			structureId, uuid, orderByComparator);
	}

	/**
	 * Removes all the ddm structures where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of ddm structures where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddm structures
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the ddm structure where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByUUID_G(String uuid, long groupId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the ddm structure where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the ddm structure where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the ddm structure where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the ddm structure that was removed
	 */
	public static DDMStructure removeByUUID_G(String uuid, long groupId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of ddm structures where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching ddm structures
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] findByUuid_C_PrevAndNext(
			long structureId, String uuid, long companyId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByUuid_C_PrevAndNext(
			structureId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the ddm structures where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddm structures
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the ddm structures where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByGroupId_First(
			long groupId, OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByGroupId_First(
		long groupId, OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByGroupId_Last(
			long groupId, OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByGroupId_Last(
		long groupId, OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] findByGroupId_PrevAndNext(
			long structureId, long groupId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByGroupId_PrevAndNext(
			structureId, groupId, orderByComparator);
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] filterFindByGroupId_PrevAndNext(
			long structureId, long groupId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().filterFindByGroupId_PrevAndNext(
			structureId, groupId, orderByComparator);
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByGroupId(long[] groupIds) {
		return getPersistence().filterFindByGroupId(groupIds);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByGroupId(
		long[] groupIds, int start, int end) {

		return getPersistence().filterFindByGroupId(groupIds, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm structures where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByGroupId(long[] groupIds) {
		return getPersistence().findByGroupId(groupIds);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByGroupId(
		long[] groupIds, int start, int end) {

		return getPersistence().findByGroupId(groupIds, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByGroupId(
			groupIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupIds, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the ddm structures where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm structures
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of ddm structures where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddm structures
	 */
	public static int countByGroupId(long[] groupIds) {
		return getPersistence().countByGroupId(groupIds);
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public static int filterCountByGroupId(long[] groupIds) {
		return getPersistence().filterCountByGroupId(groupIds);
	}

	/**
	 * Returns all the ddm structures where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByParentStructureId(
		long parentStructureId) {

		return getPersistence().findByParentStructureId(parentStructureId);
	}

	/**
	 * Returns a range of all the ddm structures where parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByParentStructureId(
		long parentStructureId, int start, int end) {

		return getPersistence().findByParentStructureId(
			parentStructureId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByParentStructureId(
		long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByParentStructureId(
			parentStructureId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByParentStructureId(
		long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByParentStructureId(
			parentStructureId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByParentStructureId_First(
			long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByParentStructureId_First(
			parentStructureId, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByParentStructureId_First(
		long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByParentStructureId_First(
			parentStructureId, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByParentStructureId_Last(
			long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByParentStructureId_Last(
			parentStructureId, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByParentStructureId_Last(
		long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByParentStructureId_Last(
			parentStructureId, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] findByParentStructureId_PrevAndNext(
			long structureId, long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByParentStructureId_PrevAndNext(
			structureId, parentStructureId, orderByComparator);
	}

	/**
	 * Removes all the ddm structures where parentStructureId = &#63; from the database.
	 *
	 * @param parentStructureId the parent structure ID
	 */
	public static void removeByParentStructureId(long parentStructureId) {
		getPersistence().removeByParentStructureId(parentStructureId);
	}

	/**
	 * Returns the number of ddm structures where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @return the number of matching ddm structures
	 */
	public static int countByParentStructureId(long parentStructureId) {
		return getPersistence().countByParentStructureId(parentStructureId);
	}

	/**
	 * Returns all the ddm structures where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByStructureKey(String structureKey) {
		return getPersistence().findByStructureKey(structureKey);
	}

	/**
	 * Returns a range of all the ddm structures where structureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param structureKey the structure key
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByStructureKey(
		String structureKey, int start, int end) {

		return getPersistence().findByStructureKey(structureKey, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where structureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param structureKey the structure key
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByStructureKey(
		String structureKey, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByStructureKey(
			structureKey, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where structureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param structureKey the structure key
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByStructureKey(
		String structureKey, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByStructureKey(
			structureKey, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByStructureKey_First(
			String structureKey,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByStructureKey_First(
			structureKey, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByStructureKey_First(
		String structureKey,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByStructureKey_First(
			structureKey, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByStructureKey_Last(
			String structureKey,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByStructureKey_Last(
			structureKey, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByStructureKey_Last(
		String structureKey,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByStructureKey_Last(
			structureKey, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] findByStructureKey_PrevAndNext(
			long structureId, String structureKey,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByStructureKey_PrevAndNext(
			structureId, structureKey, orderByComparator);
	}

	/**
	 * Removes all the ddm structures where structureKey = &#63; from the database.
	 *
	 * @param structureKey the structure key
	 */
	public static void removeByStructureKey(String structureKey) {
		getPersistence().removeByStructureKey(structureKey);
	}

	/**
	 * Returns the number of ddm structures where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @return the number of matching ddm structures
	 */
	public static int countByStructureKey(String structureKey) {
		return getPersistence().countByStructureKey(structureKey);
	}

	/**
	 * Returns all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByG_P(
		long groupId, long parentStructureId) {

		return getPersistence().findByG_P(groupId, parentStructureId);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_P(
		long groupId, long parentStructureId, int start, int end) {

		return getPersistence().findByG_P(
			groupId, parentStructureId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_P(
		long groupId, long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByG_P(
			groupId, parentStructureId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_P(
		long groupId, long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P(
			groupId, parentStructureId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByG_P_First(
			long groupId, long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_P_First(
			groupId, parentStructureId, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByG_P_First(
		long groupId, long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByG_P_First(
			groupId, parentStructureId, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByG_P_Last(
			long groupId, long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_P_Last(
			groupId, parentStructureId, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByG_P_Last(
		long groupId, long parentStructureId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByG_P_Last(
			groupId, parentStructureId, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] findByG_P_PrevAndNext(
			long structureId, long groupId, long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_P_PrevAndNext(
			structureId, groupId, parentStructureId, orderByComparator);
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_P(
		long groupId, long parentStructureId) {

		return getPersistence().filterFindByG_P(groupId, parentStructureId);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_P(
		long groupId, long parentStructureId, int start, int end) {

		return getPersistence().filterFindByG_P(
			groupId, parentStructureId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_P(
		long groupId, long parentStructureId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().filterFindByG_P(
			groupId, parentStructureId, start, end, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] filterFindByG_P_PrevAndNext(
			long structureId, long groupId, long parentStructureId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().filterFindByG_P_PrevAndNext(
			structureId, groupId, parentStructureId, orderByComparator);
	}

	/**
	 * Removes all the ddm structures where groupId = &#63; and parentStructureId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 */
	public static void removeByG_P(long groupId, long parentStructureId) {
		getPersistence().removeByG_P(groupId, parentStructureId);
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the number of matching ddm structures
	 */
	public static int countByG_P(long groupId, long parentStructureId) {
		return getPersistence().countByG_P(groupId, parentStructureId);
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public static int filterCountByG_P(long groupId, long parentStructureId) {
		return getPersistence().filterCountByG_P(groupId, parentStructureId);
	}

	/**
	 * Returns all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByG_C(long groupId, long classNameId) {
		return getPersistence().findByG_C(groupId, classNameId);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_C(
		long groupId, long classNameId, int start, int end) {

		return getPersistence().findByG_C(groupId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByG_C(
			groupId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C(
			groupId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_C_First(
			groupId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByG_C_First(
			groupId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByG_C_Last(
			long groupId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_C_Last(
			groupId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByG_C_Last(
		long groupId, long classNameId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByG_C_Last(
			groupId, classNameId, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] findByG_C_PrevAndNext(
			long structureId, long groupId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_C_PrevAndNext(
			structureId, groupId, classNameId, orderByComparator);
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_C(
		long groupId, long classNameId) {

		return getPersistence().filterFindByG_C(groupId, classNameId);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_C(
		long groupId, long classNameId, int start, int end) {

		return getPersistence().filterFindByG_C(
			groupId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().filterFindByG_C(
			groupId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] filterFindByG_C_PrevAndNext(
			long structureId, long groupId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().filterFindByG_C_PrevAndNext(
			structureId, groupId, classNameId, orderByComparator);
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_C(
		long[] groupIds, long classNameId) {

		return getPersistence().filterFindByG_C(groupIds, classNameId);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_C(
		long[] groupIds, long classNameId, int start, int end) {

		return getPersistence().filterFindByG_C(
			groupIds, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_C(
		long[] groupIds, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().filterFindByG_C(
			groupIds, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns all the ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByG_C(
		long[] groupIds, long classNameId) {

		return getPersistence().findByG_C(groupIds, classNameId);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_C(
		long[] groupIds, long classNameId, int start, int end) {

		return getPersistence().findByG_C(groupIds, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_C(
		long[] groupIds, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByG_C(
			groupIds, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_C(
		long[] groupIds, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C(
			groupIds, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the ddm structures where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	public static void removeByG_C(long groupId, long classNameId) {
		getPersistence().removeByG_C(groupId, classNameId);
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	public static int countByG_C(long groupId, long classNameId) {
		return getPersistence().countByG_C(groupId, classNameId);
	}

	/**
	 * Returns the number of ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	public static int countByG_C(long[] groupIds, long classNameId) {
		return getPersistence().countByG_C(groupIds, classNameId);
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public static int filterCountByG_C(long groupId, long classNameId) {
		return getPersistence().filterCountByG_C(groupId, classNameId);
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public static int filterCountByG_C(long[] groupIds, long classNameId) {
		return getPersistence().filterCountByG_C(groupIds, classNameId);
	}

	/**
	 * Returns all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByC_C(
		long companyId, long classNameId) {

		return getPersistence().findByC_C(companyId, classNameId);
	}

	/**
	 * Returns a range of all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByC_C(
		long companyId, long classNameId, int start, int end) {

		return getPersistence().findByC_C(companyId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByC_C(
			companyId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			companyId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByC_C_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByC_C_Last(
			long companyId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByC_C_Last(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByC_C_Last(
		long companyId, long classNameId,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByC_C_Last(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] findByC_C_PrevAndNext(
			long structureId, long companyId, long classNameId,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByC_C_PrevAndNext(
			structureId, companyId, classNameId, orderByComparator);
	}

	/**
	 * Removes all the ddm structures where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public static void removeByC_C(long companyId, long classNameId) {
		getPersistence().removeByC_C(companyId, classNameId);
	}

	/**
	 * Returns the number of ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	public static int countByC_C(long companyId, long classNameId) {
		return getPersistence().countByC_C(companyId, classNameId);
	}

	/**
	 * Returns the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByERC_G_C(
			String externalReferenceCode, long groupId, long classNameId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByERC_G_C(
			externalReferenceCode, groupId, classNameId);
	}

	/**
	 * Returns the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByERC_G_C(
		String externalReferenceCode, long groupId, long classNameId) {

		return getPersistence().fetchByERC_G_C(
			externalReferenceCode, groupId, classNameId);
	}

	/**
	 * Returns the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByERC_G_C(
		String externalReferenceCode, long groupId, long classNameId,
		boolean useFinderCache) {

		return getPersistence().fetchByERC_G_C(
			externalReferenceCode, groupId, classNameId, useFinderCache);
	}

	/**
	 * Removes the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the ddm structure that was removed
	 */
	public static DDMStructure removeByERC_G_C(
			String externalReferenceCode, long groupId, long classNameId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().removeByERC_G_C(
			externalReferenceCode, groupId, classNameId);
	}

	/**
	 * Returns the number of ddm structures where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	public static int countByERC_G_C(
		String externalReferenceCode, long groupId, long classNameId) {

		return getPersistence().countByERC_G_C(
			externalReferenceCode, groupId, classNameId);
	}

	/**
	 * Returns the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByG_C_S(
			long groupId, long classNameId, String structureKey)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_C_S(groupId, classNameId, structureKey);
	}

	/**
	 * Returns the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByG_C_S(
		long groupId, long classNameId, String structureKey) {

		return getPersistence().fetchByG_C_S(
			groupId, classNameId, structureKey);
	}

	/**
	 * Returns the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByG_C_S(
		long groupId, long classNameId, String structureKey,
		boolean useFinderCache) {

		return getPersistence().fetchByG_C_S(
			groupId, classNameId, structureKey, useFinderCache);
	}

	/**
	 * Removes the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the ddm structure that was removed
	 */
	public static DDMStructure removeByG_C_S(
			long groupId, long classNameId, String structureKey)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().removeByG_C_S(
			groupId, classNameId, structureKey);
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and classNameId = &#63; and structureKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the number of matching ddm structures
	 */
	public static int countByG_C_S(
		long groupId, long classNameId, String structureKey) {

		return getPersistence().countByG_C_S(
			groupId, classNameId, structureKey);
	}

	/**
	 * Returns all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByG_N_D(
		long groupId, String name, String description) {

		return getPersistence().findByG_N_D(groupId, name, description);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_N_D(
		long groupId, String name, String description, int start, int end) {

		return getPersistence().findByG_N_D(
			groupId, name, description, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_N_D(
		long groupId, String name, String description, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByG_N_D(
			groupId, name, description, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_N_D(
		long groupId, String name, String description, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_N_D(
			groupId, name, description, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByG_N_D_First(
			long groupId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_N_D_First(
			groupId, name, description, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByG_N_D_First(
		long groupId, String name, String description,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByG_N_D_First(
			groupId, name, description, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByG_N_D_Last(
			long groupId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_N_D_Last(
			groupId, name, description, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByG_N_D_Last(
		long groupId, String name, String description,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByG_N_D_Last(
			groupId, name, description, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] findByG_N_D_PrevAndNext(
			long structureId, long groupId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_N_D_PrevAndNext(
			structureId, groupId, name, description, orderByComparator);
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_N_D(
		long groupId, String name, String description) {

		return getPersistence().filterFindByG_N_D(groupId, name, description);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_N_D(
		long groupId, String name, String description, int start, int end) {

		return getPersistence().filterFindByG_N_D(
			groupId, name, description, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_N_D(
		long groupId, String name, String description, int start, int end,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().filterFindByG_N_D(
			groupId, name, description, start, end, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] filterFindByG_N_D_PrevAndNext(
			long structureId, long groupId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().filterFindByG_N_D_PrevAndNext(
			structureId, groupId, name, description, orderByComparator);
	}

	/**
	 * Removes all the ddm structures where groupId = &#63; and name = &#63; and description = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 */
	public static void removeByG_N_D(
		long groupId, String name, String description) {

		getPersistence().removeByG_N_D(groupId, name, description);
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures
	 */
	public static int countByG_N_D(
		long groupId, String name, String description) {

		return getPersistence().countByG_N_D(groupId, name, description);
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public static int filterCountByG_N_D(
		long groupId, String name, String description) {

		return getPersistence().filterCountByG_N_D(groupId, name, description);
	}

	/**
	 * Returns all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		return getPersistence().findByG_C_N_D(
			groupId, classNameId, name, description);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end) {

		return getPersistence().findByG_C_N_D(
			groupId, classNameId, name, description, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByG_C_N_D(
			groupId, classNameId, name, description, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_N_D(
			groupId, classNameId, name, description, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByG_C_N_D_First(
			long groupId, long classNameId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_C_N_D_First(
			groupId, classNameId, name, description, orderByComparator);
	}

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByG_C_N_D_First(
		long groupId, long classNameId, String name, String description,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByG_C_N_D_First(
			groupId, classNameId, name, description, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public static DDMStructure findByG_C_N_D_Last(
			long groupId, long classNameId, String name, String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_C_N_D_Last(
			groupId, classNameId, name, description, orderByComparator);
	}

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public static DDMStructure fetchByG_C_N_D_Last(
		long groupId, long classNameId, String name, String description,
		OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().fetchByG_C_N_D_Last(
			groupId, classNameId, name, description, orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] findByG_C_N_D_PrevAndNext(
			long structureId, long groupId, long classNameId, String name,
			String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByG_C_N_D_PrevAndNext(
			structureId, groupId, classNameId, name, description,
			orderByComparator);
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		return getPersistence().filterFindByG_C_N_D(
			groupId, classNameId, name, description);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end) {

		return getPersistence().filterFindByG_C_N_D(
			groupId, classNameId, name, description, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().filterFindByG_C_N_D(
			groupId, classNameId, name, description, start, end,
			orderByComparator);
	}

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure[] filterFindByG_C_N_D_PrevAndNext(
			long structureId, long groupId, long classNameId, String name,
			String description,
			OrderByComparator<DDMStructure> orderByComparator)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().filterFindByG_C_N_D_PrevAndNext(
			structureId, groupId, classNameId, name, description,
			orderByComparator);
	}

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description) {

		return getPersistence().filterFindByG_C_N_D(
			groupIds, classNameId, name, description);
	}

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end) {

		return getPersistence().filterFindByG_C_N_D(
			groupIds, classNameId, name, description, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public static List<DDMStructure> filterFindByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().filterFindByG_C_N_D(
			groupIds, classNameId, name, description, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures
	 */
	public static List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description) {

		return getPersistence().findByG_C_N_D(
			groupIds, classNameId, name, description);
	}

	/**
	 * Returns a range of all the ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end) {

		return getPersistence().findByG_C_N_D(
			groupIds, classNameId, name, description, start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findByG_C_N_D(
			groupIds, classNameId, name, description, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public static List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end, OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_N_D(
			groupIds, classNameId, name, description, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 */
	public static void removeByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		getPersistence().removeByG_C_N_D(
			groupId, classNameId, name, description);
	}

	/**
	 * Returns the number of ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures
	 */
	public static int countByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		return getPersistence().countByG_C_N_D(
			groupId, classNameId, name, description);
	}

	/**
	 * Returns the number of ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures
	 */
	public static int countByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description) {

		return getPersistence().countByG_C_N_D(
			groupIds, classNameId, name, description);
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public static int filterCountByG_C_N_D(
		long groupId, long classNameId, String name, String description) {

		return getPersistence().filterCountByG_C_N_D(
			groupId, classNameId, name, description);
	}

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public static int filterCountByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description) {

		return getPersistence().filterCountByG_C_N_D(
			groupIds, classNameId, name, description);
	}

	/**
	 * Caches the ddm structure in the entity cache if it is enabled.
	 *
	 * @param ddmStructure the ddm structure
	 */
	public static void cacheResult(DDMStructure ddmStructure) {
		getPersistence().cacheResult(ddmStructure);
	}

	/**
	 * Caches the ddm structures in the entity cache if it is enabled.
	 *
	 * @param ddmStructures the ddm structures
	 */
	public static void cacheResult(List<DDMStructure> ddmStructures) {
		getPersistence().cacheResult(ddmStructures);
	}

	/**
	 * Creates a new ddm structure with the primary key. Does not add the ddm structure to the database.
	 *
	 * @param structureId the primary key for the new ddm structure
	 * @return the new ddm structure
	 */
	public static DDMStructure create(long structureId) {
		return getPersistence().create(structureId);
	}

	/**
	 * Removes the ddm structure with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure that was removed
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure remove(long structureId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().remove(structureId);
	}

	public static DDMStructure updateImpl(DDMStructure ddmStructure) {
		return getPersistence().updateImpl(ddmStructure);
	}

	/**
	 * Returns the ddm structure with the primary key or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure findByPrimaryKey(long structureId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchStructureException {

		return getPersistence().findByPrimaryKey(structureId);
	}

	/**
	 * Returns the ddm structure with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure, or <code>null</code> if a ddm structure with the primary key could not be found
	 */
	public static DDMStructure fetchByPrimaryKey(long structureId) {
		return getPersistence().fetchByPrimaryKey(structureId);
	}

	/**
	 * Returns all the ddm structures.
	 *
	 * @return the ddm structures
	 */
	public static List<DDMStructure> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the ddm structures.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of ddm structures
	 */
	public static List<DDMStructure> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the ddm structures.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of ddm structures
	 */
	public static List<DDMStructure> findAll(
		int start, int end, OrderByComparator<DDMStructure> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm structures.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of ddm structures
	 */
	public static List<DDMStructure> findAll(
		int start, int end, OrderByComparator<DDMStructure> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the ddm structures from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of ddm structures.
	 *
	 * @return the number of ddm structures
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static DDMStructurePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(DDMStructurePersistence persistence) {
		_persistence = persistence;
	}

	private static volatile DDMStructurePersistence _persistence;

}