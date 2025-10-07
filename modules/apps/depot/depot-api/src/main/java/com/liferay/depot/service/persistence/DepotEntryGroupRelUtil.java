/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.persistence;

import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the depot entry group rel service. This utility wraps <code>com.liferay.depot.service.persistence.impl.DepotEntryGroupRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryGroupRelPersistence
 * @generated
 */
public class DepotEntryGroupRelUtil {

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
	public static void clearCache(DepotEntryGroupRel depotEntryGroupRel) {
		getPersistence().clearCache(depotEntryGroupRel);
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
	public static Map<Serializable, DepotEntryGroupRel> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DepotEntryGroupRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DepotEntryGroupRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DepotEntryGroupRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DepotEntryGroupRel update(
		DepotEntryGroupRel depotEntryGroupRel) {

		return getPersistence().update(depotEntryGroupRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DepotEntryGroupRel update(
		DepotEntryGroupRel depotEntryGroupRel, ServiceContext serviceContext) {

		return getPersistence().update(depotEntryGroupRel, serviceContext);
	}

	/**
	 * Returns all the depot entry group rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the depot entry group rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByUuid_First(
			String uuid,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByUuid_First(
		String uuid, OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByUuid_Last(
			String uuid,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByUuid_Last(
		String uuid, OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public static DepotEntryGroupRel[] findByUuid_PrevAndNext(
			long depotEntryGroupRelId, String uuid,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByUuid_PrevAndNext(
			depotEntryGroupRelId, uuid, orderByComparator);
	}

	/**
	 * Removes all the depot entry group rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of depot entry group rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching depot entry group rels
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the depot entry group rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryGroupRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByUUID_G(String uuid, long groupId)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the depot entry group rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the depot entry group rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the depot entry group rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the depot entry group rel that was removed
	 */
	public static DepotEntryGroupRel removeByUUID_G(String uuid, long groupId)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of depot entry group rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching depot entry group rels
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public static DepotEntryGroupRel[] findByUuid_C_PrevAndNext(
			long depotEntryGroupRelId, String uuid, long companyId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByUuid_C_PrevAndNext(
			depotEntryGroupRelId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the depot entry group rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching depot entry group rels
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the depot entry group rels where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @return the matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByDepotEntryId(
		long depotEntryId) {

		return getPersistence().findByDepotEntryId(depotEntryId);
	}

	/**
	 * Returns a range of all the depot entry group rels where depotEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param depotEntryId the depot entry ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByDepotEntryId(
		long depotEntryId, int start, int end) {

		return getPersistence().findByDepotEntryId(depotEntryId, start, end);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where depotEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param depotEntryId the depot entry ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByDepotEntryId(
		long depotEntryId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().findByDepotEntryId(
			depotEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where depotEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param depotEntryId the depot entry ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByDepotEntryId(
		long depotEntryId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByDepotEntryId(
			depotEntryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByDepotEntryId_First(
			long depotEntryId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByDepotEntryId_First(
			depotEntryId, orderByComparator);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByDepotEntryId_First(
		long depotEntryId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByDepotEntryId_First(
			depotEntryId, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByDepotEntryId_Last(
			long depotEntryId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByDepotEntryId_Last(
			depotEntryId, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByDepotEntryId_Last(
		long depotEntryId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByDepotEntryId_Last(
			depotEntryId, orderByComparator);
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public static DepotEntryGroupRel[] findByDepotEntryId_PrevAndNext(
			long depotEntryGroupRelId, long depotEntryId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByDepotEntryId_PrevAndNext(
			depotEntryGroupRelId, depotEntryId, orderByComparator);
	}

	/**
	 * Removes all the depot entry group rels where depotEntryId = &#63; from the database.
	 *
	 * @param depotEntryId the depot entry ID
	 */
	public static void removeByDepotEntryId(long depotEntryId) {
		getPersistence().removeByDepotEntryId(depotEntryId);
	}

	/**
	 * Returns the number of depot entry group rels where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @return the number of matching depot entry group rels
	 */
	public static int countByDepotEntryId(long depotEntryId) {
		return getPersistence().countByDepotEntryId(depotEntryId);
	}

	/**
	 * Returns all the depot entry group rels where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByToGroupId(long toGroupId) {
		return getPersistence().findByToGroupId(toGroupId);
	}

	/**
	 * Returns a range of all the depot entry group rels where toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByToGroupId(
		long toGroupId, int start, int end) {

		return getPersistence().findByToGroupId(toGroupId, start, end);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByToGroupId(
		long toGroupId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().findByToGroupId(
			toGroupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByToGroupId(
		long toGroupId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByToGroupId(
			toGroupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByToGroupId_First(
			long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByToGroupId_First(
			toGroupId, orderByComparator);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByToGroupId_First(
		long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByToGroupId_First(
			toGroupId, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByToGroupId_Last(
			long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByToGroupId_Last(
			toGroupId, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByToGroupId_Last(
		long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByToGroupId_Last(
			toGroupId, orderByComparator);
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public static DepotEntryGroupRel[] findByToGroupId_PrevAndNext(
			long depotEntryGroupRelId, long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByToGroupId_PrevAndNext(
			depotEntryGroupRelId, toGroupId, orderByComparator);
	}

	/**
	 * Removes all the depot entry group rels where toGroupId = &#63; from the database.
	 *
	 * @param toGroupId the to group ID
	 */
	public static void removeByToGroupId(long toGroupId) {
		getPersistence().removeByToGroupId(toGroupId);
	}

	/**
	 * Returns the number of depot entry group rels where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @return the number of matching depot entry group rels
	 */
	public static int countByToGroupId(long toGroupId) {
		return getPersistence().countByToGroupId(toGroupId);
	}

	/**
	 * Returns all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId) {

		return getPersistence().findByDDMSA_TGI(
			ddmStructuresAvailable, toGroupId);
	}

	/**
	 * Returns a range of all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId, int start, int end) {

		return getPersistence().findByDDMSA_TGI(
			ddmStructuresAvailable, toGroupId, start, end);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().findByDDMSA_TGI(
			ddmStructuresAvailable, toGroupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByDDMSA_TGI(
			ddmStructuresAvailable, toGroupId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByDDMSA_TGI_First(
			boolean ddmStructuresAvailable, long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByDDMSA_TGI_First(
			ddmStructuresAvailable, toGroupId, orderByComparator);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByDDMSA_TGI_First(
		boolean ddmStructuresAvailable, long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByDDMSA_TGI_First(
			ddmStructuresAvailable, toGroupId, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByDDMSA_TGI_Last(
			boolean ddmStructuresAvailable, long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByDDMSA_TGI_Last(
			ddmStructuresAvailable, toGroupId, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByDDMSA_TGI_Last(
		boolean ddmStructuresAvailable, long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByDDMSA_TGI_Last(
			ddmStructuresAvailable, toGroupId, orderByComparator);
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public static DepotEntryGroupRel[] findByDDMSA_TGI_PrevAndNext(
			long depotEntryGroupRelId, boolean ddmStructuresAvailable,
			long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByDDMSA_TGI_PrevAndNext(
			depotEntryGroupRelId, ddmStructuresAvailable, toGroupId,
			orderByComparator);
	}

	/**
	 * Removes all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63; from the database.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 */
	public static void removeByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId) {

		getPersistence().removeByDDMSA_TGI(ddmStructuresAvailable, toGroupId);
	}

	/**
	 * Returns the number of depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @return the number of matching depot entry group rels
	 */
	public static int countByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId) {

		return getPersistence().countByDDMSA_TGI(
			ddmStructuresAvailable, toGroupId);
	}

	/**
	 * Returns the depot entry group rel where depotEntryId = &#63; and toGroupId = &#63; or throws a <code>NoSuchEntryGroupRelException</code> if it could not be found.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByD_TGI(
			long depotEntryId, long toGroupId)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByD_TGI(depotEntryId, toGroupId);
	}

	/**
	 * Returns the depot entry group rel where depotEntryId = &#63; and toGroupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByD_TGI(
		long depotEntryId, long toGroupId) {

		return getPersistence().fetchByD_TGI(depotEntryId, toGroupId);
	}

	/**
	 * Returns the depot entry group rel where depotEntryId = &#63; and toGroupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByD_TGI(
		long depotEntryId, long toGroupId, boolean useFinderCache) {

		return getPersistence().fetchByD_TGI(
			depotEntryId, toGroupId, useFinderCache);
	}

	/**
	 * Removes the depot entry group rel where depotEntryId = &#63; and toGroupId = &#63; from the database.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @return the depot entry group rel that was removed
	 */
	public static DepotEntryGroupRel removeByD_TGI(
			long depotEntryId, long toGroupId)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().removeByD_TGI(depotEntryId, toGroupId);
	}

	/**
	 * Returns the number of depot entry group rels where depotEntryId = &#63; and toGroupId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @return the number of matching depot entry group rels
	 */
	public static int countByD_TGI(long depotEntryId, long toGroupId) {
		return getPersistence().countByD_TGI(depotEntryId, toGroupId);
	}

	/**
	 * Returns all the depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByS_TGI(
		boolean searchable, long toGroupId) {

		return getPersistence().findByS_TGI(searchable, toGroupId);
	}

	/**
	 * Returns a range of all the depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByS_TGI(
		boolean searchable, long toGroupId, int start, int end) {

		return getPersistence().findByS_TGI(searchable, toGroupId, start, end);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByS_TGI(
		boolean searchable, long toGroupId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().findByS_TGI(
			searchable, toGroupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByS_TGI(
		boolean searchable, long toGroupId, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByS_TGI(
			searchable, toGroupId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByS_TGI_First(
			boolean searchable, long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByS_TGI_First(
			searchable, toGroupId, orderByComparator);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByS_TGI_First(
		boolean searchable, long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByS_TGI_First(
			searchable, toGroupId, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByS_TGI_Last(
			boolean searchable, long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByS_TGI_Last(
			searchable, toGroupId, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByS_TGI_Last(
		boolean searchable, long toGroupId,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByS_TGI_Last(
			searchable, toGroupId, orderByComparator);
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public static DepotEntryGroupRel[] findByS_TGI_PrevAndNext(
			long depotEntryGroupRelId, boolean searchable, long toGroupId,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByS_TGI_PrevAndNext(
			depotEntryGroupRelId, searchable, toGroupId, orderByComparator);
	}

	/**
	 * Removes all the depot entry group rels where searchable = &#63; and toGroupId = &#63; from the database.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 */
	public static void removeByS_TGI(boolean searchable, long toGroupId) {
		getPersistence().removeByS_TGI(searchable, toGroupId);
	}

	/**
	 * Returns the number of depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @return the number of matching depot entry group rels
	 */
	public static int countByS_TGI(boolean searchable, long toGroupId) {
		return getPersistence().countByS_TGI(searchable, toGroupId);
	}

	/**
	 * Returns all the depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @return the matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByTGI_T(
		long toGroupId, int type) {

		return getPersistence().findByTGI_T(toGroupId, type);
	}

	/**
	 * Returns a range of all the depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByTGI_T(
		long toGroupId, int type, int start, int end) {

		return getPersistence().findByTGI_T(toGroupId, type, start, end);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByTGI_T(
		long toGroupId, int type, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().findByTGI_T(
			toGroupId, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findByTGI_T(
		long toGroupId, int type, int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByTGI_T(
			toGroupId, type, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByTGI_T_First(
			long toGroupId, int type,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByTGI_T_First(
			toGroupId, type, orderByComparator);
	}

	/**
	 * Returns the first depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByTGI_T_First(
		long toGroupId, int type,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByTGI_T_First(
			toGroupId, type, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel findByTGI_T_Last(
			long toGroupId, int type,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByTGI_T_Last(
			toGroupId, type, orderByComparator);
	}

	/**
	 * Returns the last depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchByTGI_T_Last(
		long toGroupId, int type,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().fetchByTGI_T_Last(
			toGroupId, type, orderByComparator);
	}

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public static DepotEntryGroupRel[] findByTGI_T_PrevAndNext(
			long depotEntryGroupRelId, long toGroupId, int type,
			OrderByComparator<DepotEntryGroupRel> orderByComparator)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByTGI_T_PrevAndNext(
			depotEntryGroupRelId, toGroupId, type, orderByComparator);
	}

	/**
	 * Removes all the depot entry group rels where toGroupId = &#63; and type = &#63; from the database.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 */
	public static void removeByTGI_T(long toGroupId, int type) {
		getPersistence().removeByTGI_T(toGroupId, type);
	}

	/**
	 * Returns the number of depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @return the number of matching depot entry group rels
	 */
	public static int countByTGI_T(long toGroupId, int type) {
		return getPersistence().countByTGI_T(toGroupId, type);
	}

	/**
	 * Caches the depot entry group rel in the entity cache if it is enabled.
	 *
	 * @param depotEntryGroupRel the depot entry group rel
	 */
	public static void cacheResult(DepotEntryGroupRel depotEntryGroupRel) {
		getPersistence().cacheResult(depotEntryGroupRel);
	}

	/**
	 * Caches the depot entry group rels in the entity cache if it is enabled.
	 *
	 * @param depotEntryGroupRels the depot entry group rels
	 */
	public static void cacheResult(
		List<DepotEntryGroupRel> depotEntryGroupRels) {

		getPersistence().cacheResult(depotEntryGroupRels);
	}

	/**
	 * Creates a new depot entry group rel with the primary key. Does not add the depot entry group rel to the database.
	 *
	 * @param depotEntryGroupRelId the primary key for the new depot entry group rel
	 * @return the new depot entry group rel
	 */
	public static DepotEntryGroupRel create(long depotEntryGroupRelId) {
		return getPersistence().create(depotEntryGroupRelId);
	}

	/**
	 * Removes the depot entry group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel that was removed
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public static DepotEntryGroupRel remove(long depotEntryGroupRelId)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().remove(depotEntryGroupRelId);
	}

	public static DepotEntryGroupRel updateImpl(
		DepotEntryGroupRel depotEntryGroupRel) {

		return getPersistence().updateImpl(depotEntryGroupRel);
	}

	/**
	 * Returns the depot entry group rel with the primary key or throws a <code>NoSuchEntryGroupRelException</code> if it could not be found.
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public static DepotEntryGroupRel findByPrimaryKey(long depotEntryGroupRelId)
		throws com.liferay.depot.exception.NoSuchEntryGroupRelException {

		return getPersistence().findByPrimaryKey(depotEntryGroupRelId);
	}

	/**
	 * Returns the depot entry group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel, or <code>null</code> if a depot entry group rel with the primary key could not be found
	 */
	public static DepotEntryGroupRel fetchByPrimaryKey(
		long depotEntryGroupRelId) {

		return getPersistence().fetchByPrimaryKey(depotEntryGroupRelId);
	}

	/**
	 * Returns all the depot entry group rels.
	 *
	 * @return the depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the depot entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findAll(
		int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the depot entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of depot entry group rels
	 */
	public static List<DepotEntryGroupRel> findAll(
		int start, int end,
		OrderByComparator<DepotEntryGroupRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the depot entry group rels from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of depot entry group rels.
	 *
	 * @return the number of depot entry group rels
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static DepotEntryGroupRelPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		DepotEntryGroupRelPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile DepotEntryGroupRelPersistence _persistence;

}