/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.persistence;

import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout classed model usage service. This utility wraps <code>com.liferay.layout.service.persistence.impl.LayoutClassedModelUsagePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutClassedModelUsagePersistence
 * @generated
 */
public class LayoutClassedModelUsageUtil {

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
		LayoutClassedModelUsage layoutClassedModelUsage) {

		getPersistence().clearCache(layoutClassedModelUsage);
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
	public static Map<Serializable, LayoutClassedModelUsage> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<LayoutClassedModelUsage> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<LayoutClassedModelUsage> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<LayoutClassedModelUsage> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutClassedModelUsage update(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		return getPersistence().update(layoutClassedModelUsage);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutClassedModelUsage update(
		LayoutClassedModelUsage layoutClassedModelUsage,
		ServiceContext serviceContext) {

		return getPersistence().update(layoutClassedModelUsage, serviceContext);
	}

	/**
	 * Returns all the layout classed model usages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the layout classed model usages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByUuid_First(
			String uuid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByUuid_Last(
			String uuid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByUuid_Last(
		String uuid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where uuid = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage[] findByUuid_PrevAndNext(
			long layoutClassedModelUsageId, String uuid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByUuid_PrevAndNext(
			layoutClassedModelUsageId, uuid, orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of layout classed model usages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout classed model usages
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the layout classed model usage where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutClassedModelUsageException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByUUID_G(
			String uuid, long groupId)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout classed model usage where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout classed model usage where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the layout classed model usage where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout classed model usage that was removed
	 */
	public static LayoutClassedModelUsage removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of layout classed model usages where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout classed model usages
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the layout classed model usages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the layout classed model usages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage[] findByUuid_C_PrevAndNext(
			long layoutClassedModelUsageId, String uuid, long companyId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByUuid_C_PrevAndNext(
			layoutClassedModelUsageId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of layout classed model usages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout classed model usages
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the layout classed model usages where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByPlid(long plid) {
		return getPersistence().findByPlid(plid);
	}

	/**
	 * Returns a range of all the layout classed model usages where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByPlid(
		long plid, int start, int end) {

		return getPersistence().findByPlid(plid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().findByPlid(plid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPlid(
			plid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByPlid_First(
			long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByPlid_First(plid, orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByPlid_First(
		long plid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByPlid_First(plid, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByPlid_Last(
			long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByPlid_Last(plid, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByPlid_Last(
		long plid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByPlid_Last(plid, orderByComparator);
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where plid = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage[] findByPlid_PrevAndNext(
			long layoutClassedModelUsageId, long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByPlid_PrevAndNext(
			layoutClassedModelUsageId, plid, orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	public static void removeByPlid(long plid) {
		getPersistence().removeByPlid(plid);
	}

	/**
	 * Returns the number of layout classed model usages where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout classed model usages
	 */
	public static int countByPlid(long plid) {
		return getPersistence().countByPlid(plid);
	}

	/**
	 * Returns all the layout classed model usages where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CN(
		long companyId, long classNameId) {

		return getPersistence().findByC_CN(companyId, classNameId);
	}

	/**
	 * Returns a range of all the layout classed model usages where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CN(
		long companyId, long classNameId, int start, int end) {

		return getPersistence().findByC_CN(companyId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().findByC_CN(
			companyId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_CN(
			companyId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByC_CN_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByC_CN_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByC_CN_Last(
			long companyId, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByC_CN_Last(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByC_CN_Last(
		long companyId, long classNameId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByC_CN_Last(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage[] findByC_CN_PrevAndNext(
			long layoutClassedModelUsageId, long companyId, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByC_CN_PrevAndNext(
			layoutClassedModelUsageId, companyId, classNameId,
			orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public static void removeByC_CN(long companyId, long classNameId) {
		getPersistence().removeByC_CN(companyId, classNameId);
	}

	/**
	 * Returns the number of layout classed model usages where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching layout classed model usages
	 */
	public static int countByC_CN(long companyId, long classNameId) {
		return getPersistence().countByC_CN(companyId, classNameId);
	}

	/**
	 * Returns all the layout classed model usages where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByCN_CPK(
		long classNameId, long classPK) {

		return getPersistence().findByCN_CPK(classNameId, classPK);
	}

	/**
	 * Returns a range of all the layout classed model usages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByCN_CPK(
		long classNameId, long classPK, int start, int end) {

		return getPersistence().findByCN_CPK(classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().findByCN_CPK(
			classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCN_CPK(
			classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByCN_CPK_First(
			long classNameId, long classPK,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByCN_CPK_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByCN_CPK_First(
		long classNameId, long classPK,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByCN_CPK_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByCN_CPK_Last(
			long classNameId, long classPK,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByCN_CPK_Last(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByCN_CPK_Last(
		long classNameId, long classPK,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByCN_CPK_Last(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage[] findByCN_CPK_PrevAndNext(
			long layoutClassedModelUsageId, long classNameId, long classPK,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByCN_CPK_PrevAndNext(
			layoutClassedModelUsageId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByCN_CPK(long classNameId, long classPK) {
		getPersistence().removeByCN_CPK(classNameId, classPK);
	}

	/**
	 * Returns the number of layout classed model usages where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching layout classed model usages
	 */
	public static int countByCN_CPK(long classNameId, long classPK) {
		return getPersistence().countByCN_CPK(classNameId, classPK);
	}

	/**
	 * Returns all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId) {

		return getPersistence().findByC_CERC_CN(
			companyId, classExternalReferenceCode, classNameId);
	}

	/**
	 * Returns a range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId,
		int start, int end) {

		return getPersistence().findByC_CERC_CN(
			companyId, classExternalReferenceCode, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId,
		int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().findByC_CERC_CN(
			companyId, classExternalReferenceCode, classNameId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId,
		int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_CERC_CN(
			companyId, classExternalReferenceCode, classNameId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByC_CERC_CN_First(
			long companyId, String classExternalReferenceCode, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByC_CERC_CN_First(
			companyId, classExternalReferenceCode, classNameId,
			orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByC_CERC_CN_First(
		long companyId, String classExternalReferenceCode, long classNameId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByC_CERC_CN_First(
			companyId, classExternalReferenceCode, classNameId,
			orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByC_CERC_CN_Last(
			long companyId, String classExternalReferenceCode, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByC_CERC_CN_Last(
			companyId, classExternalReferenceCode, classNameId,
			orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByC_CERC_CN_Last(
		long companyId, String classExternalReferenceCode, long classNameId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByC_CERC_CN_Last(
			companyId, classExternalReferenceCode, classNameId,
			orderByComparator);
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage[] findByC_CERC_CN_PrevAndNext(
			long layoutClassedModelUsageId, long companyId,
			String classExternalReferenceCode, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByC_CERC_CN_PrevAndNext(
			layoutClassedModelUsageId, companyId, classExternalReferenceCode,
			classNameId, orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 */
	public static void removeByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId) {

		getPersistence().removeByC_CERC_CN(
			companyId, classExternalReferenceCode, classNameId);
	}

	/**
	 * Returns the number of layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the number of matching layout classed model usages
	 */
	public static int countByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId) {

		return getPersistence().countByC_CERC_CN(
			companyId, classExternalReferenceCode, classNameId);
	}

	/**
	 * Returns all the layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @return the matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CN_CT(
		long companyId, long classNameId, long containerType) {

		return getPersistence().findByC_CN_CT(
			companyId, classNameId, containerType);
	}

	/**
	 * Returns a range of all the layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CN_CT(
		long companyId, long classNameId, long containerType, int start,
		int end) {

		return getPersistence().findByC_CN_CT(
			companyId, classNameId, containerType, start, end);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CN_CT(
		long companyId, long classNameId, long containerType, int start,
		int end, OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().findByC_CN_CT(
			companyId, classNameId, containerType, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CN_CT(
		long companyId, long classNameId, long containerType, int start,
		int end, OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_CN_CT(
			companyId, classNameId, containerType, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByC_CN_CT_First(
			long companyId, long classNameId, long containerType,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByC_CN_CT_First(
			companyId, classNameId, containerType, orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByC_CN_CT_First(
		long companyId, long classNameId, long containerType,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByC_CN_CT_First(
			companyId, classNameId, containerType, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByC_CN_CT_Last(
			long companyId, long classNameId, long containerType,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByC_CN_CT_Last(
			companyId, classNameId, containerType, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByC_CN_CT_Last(
		long companyId, long classNameId, long containerType,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByC_CN_CT_Last(
			companyId, classNameId, containerType, orderByComparator);
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage[] findByC_CN_CT_PrevAndNext(
			long layoutClassedModelUsageId, long companyId, long classNameId,
			long containerType,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByC_CN_CT_PrevAndNext(
			layoutClassedModelUsageId, companyId, classNameId, containerType,
			orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 */
	public static void removeByC_CN_CT(
		long companyId, long classNameId, long containerType) {

		getPersistence().removeByC_CN_CT(companyId, classNameId, containerType);
	}

	/**
	 * Returns the number of layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @return the number of matching layout classed model usages
	 */
	public static int countByC_CN_CT(
		long companyId, long classNameId, long containerType) {

		return getPersistence().countByC_CN_CT(
			companyId, classNameId, containerType);
	}

	/**
	 * Returns all the layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByCN_CPK_T(
		long classNameId, long classPK, int type) {

		return getPersistence().findByCN_CPK_T(classNameId, classPK, type);
	}

	/**
	 * Returns a range of all the layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByCN_CPK_T(
		long classNameId, long classPK, int type, int start, int end) {

		return getPersistence().findByCN_CPK_T(
			classNameId, classPK, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByCN_CPK_T(
		long classNameId, long classPK, int type, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().findByCN_CPK_T(
			classNameId, classPK, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByCN_CPK_T(
		long classNameId, long classPK, int type, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCN_CPK_T(
			classNameId, classPK, type, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByCN_CPK_T_First(
			long classNameId, long classPK, int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByCN_CPK_T_First(
			classNameId, classPK, type, orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByCN_CPK_T_First(
		long classNameId, long classPK, int type,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByCN_CPK_T_First(
			classNameId, classPK, type, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByCN_CPK_T_Last(
			long classNameId, long classPK, int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByCN_CPK_T_Last(
			classNameId, classPK, type, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByCN_CPK_T_Last(
		long classNameId, long classPK, int type,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByCN_CPK_T_Last(
			classNameId, classPK, type, orderByComparator);
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage[] findByCN_CPK_T_PrevAndNext(
			long layoutClassedModelUsageId, long classNameId, long classPK,
			int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByCN_CPK_T_PrevAndNext(
			layoutClassedModelUsageId, classNameId, classPK, type,
			orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	public static void removeByCN_CPK_T(
		long classNameId, long classPK, int type) {

		getPersistence().removeByCN_CPK_T(classNameId, classPK, type);
	}

	/**
	 * Returns the number of layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching layout classed model usages
	 */
	public static int countByCN_CPK_T(
		long classNameId, long classPK, int type) {

		return getPersistence().countByCN_CPK_T(classNameId, classPK, type);
	}

	/**
	 * Returns all the layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByCK_CT_P(
		String containerKey, long containerType, long plid) {

		return getPersistence().findByCK_CT_P(
			containerKey, containerType, plid);
	}

	/**
	 * Returns a range of all the layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByCK_CT_P(
		String containerKey, long containerType, long plid, int start,
		int end) {

		return getPersistence().findByCK_CT_P(
			containerKey, containerType, plid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByCK_CT_P(
		String containerKey, long containerType, long plid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().findByCK_CT_P(
			containerKey, containerType, plid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByCK_CT_P(
		String containerKey, long containerType, long plid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCK_CT_P(
			containerKey, containerType, plid, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByCK_CT_P_First(
			String containerKey, long containerType, long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByCK_CT_P_First(
			containerKey, containerType, plid, orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByCK_CT_P_First(
		String containerKey, long containerType, long plid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByCK_CT_P_First(
			containerKey, containerType, plid, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByCK_CT_P_Last(
			String containerKey, long containerType, long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByCK_CT_P_Last(
			containerKey, containerType, plid, orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByCK_CT_P_Last(
		String containerKey, long containerType, long plid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByCK_CT_P_Last(
			containerKey, containerType, plid, orderByComparator);
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage[] findByCK_CT_P_PrevAndNext(
			long layoutClassedModelUsageId, String containerKey,
			long containerType, long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByCK_CT_P_PrevAndNext(
			layoutClassedModelUsageId, containerKey, containerType, plid,
			orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63; from the database.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 */
	public static void removeByCK_CT_P(
		String containerKey, long containerType, long plid) {

		getPersistence().removeByCK_CT_P(containerKey, containerType, plid);
	}

	/**
	 * Returns the number of layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the number of matching layout classed model usages
	 */
	public static int countByCK_CT_P(
		String containerKey, long containerType, long plid) {

		return getPersistence().countByCK_CT_P(
			containerKey, containerType, plid);
	}

	/**
	 * Returns all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @return the matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type) {

		return getPersistence().findByC_CERC_CN_T(
			companyId, classExternalReferenceCode, classNameId, type);
	}

	/**
	 * Returns a range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type, int start, int end) {

		return getPersistence().findByC_CERC_CN_T(
			companyId, classExternalReferenceCode, classNameId, type, start,
			end);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().findByC_CERC_CN_T(
			companyId, classExternalReferenceCode, classNameId, type, start,
			end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_CERC_CN_T(
			companyId, classExternalReferenceCode, classNameId, type, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByC_CERC_CN_T_First(
			long companyId, String classExternalReferenceCode, long classNameId,
			int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByC_CERC_CN_T_First(
			companyId, classExternalReferenceCode, classNameId, type,
			orderByComparator);
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByC_CERC_CN_T_First(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByC_CERC_CN_T_First(
			companyId, classExternalReferenceCode, classNameId, type,
			orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByC_CERC_CN_T_Last(
			long companyId, String classExternalReferenceCode, long classNameId,
			int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByC_CERC_CN_T_Last(
			companyId, classExternalReferenceCode, classNameId, type,
			orderByComparator);
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByC_CERC_CN_T_Last(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().fetchByC_CERC_CN_T_Last(
			companyId, classExternalReferenceCode, classNameId, type,
			orderByComparator);
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage[] findByC_CERC_CN_T_PrevAndNext(
			long layoutClassedModelUsageId, long companyId,
			String classExternalReferenceCode, long classNameId, int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByC_CERC_CN_T_PrevAndNext(
			layoutClassedModelUsageId, companyId, classExternalReferenceCode,
			classNameId, type, orderByComparator);
	}

	/**
	 * Removes all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 */
	public static void removeByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type) {

		getPersistence().removeByC_CERC_CN_T(
			companyId, classExternalReferenceCode, classNameId, type);
	}

	/**
	 * Returns the number of layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @return the number of matching layout classed model usages
	 */
	public static int countByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type) {

		return getPersistence().countByC_CERC_CN_T(
			companyId, classExternalReferenceCode, classNameId, type);
	}

	/**
	 * Returns the layout classed model usage where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63; or throws a <code>NoSuchLayoutClassedModelUsageException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage findByG_CERC_CN_CPK_CK_CT_P(
			long groupId, String classExternalReferenceCode, long classNameId,
			long classPK, String containerKey, long containerType, long plid)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByG_CERC_CN_CPK_CK_CT_P(
			groupId, classExternalReferenceCode, classNameId, classPK,
			containerKey, containerType, plid);
	}

	/**
	 * Returns the layout classed model usage where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByG_CERC_CN_CPK_CK_CT_P(
		long groupId, String classExternalReferenceCode, long classNameId,
		long classPK, String containerKey, long containerType, long plid) {

		return getPersistence().fetchByG_CERC_CN_CPK_CK_CT_P(
			groupId, classExternalReferenceCode, classNameId, classPK,
			containerKey, containerType, plid);
	}

	/**
	 * Returns the layout classed model usage where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	public static LayoutClassedModelUsage fetchByG_CERC_CN_CPK_CK_CT_P(
		long groupId, String classExternalReferenceCode, long classNameId,
		long classPK, String containerKey, long containerType, long plid,
		boolean useFinderCache) {

		return getPersistence().fetchByG_CERC_CN_CPK_CK_CT_P(
			groupId, classExternalReferenceCode, classNameId, classPK,
			containerKey, containerType, plid, useFinderCache);
	}

	/**
	 * Removes the layout classed model usage where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the layout classed model usage that was removed
	 */
	public static LayoutClassedModelUsage removeByG_CERC_CN_CPK_CK_CT_P(
			long groupId, String classExternalReferenceCode, long classNameId,
			long classPK, String containerKey, long containerType, long plid)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().removeByG_CERC_CN_CPK_CK_CT_P(
			groupId, classExternalReferenceCode, classNameId, classPK,
			containerKey, containerType, plid);
	}

	/**
	 * Returns the number of layout classed model usages where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the number of matching layout classed model usages
	 */
	public static int countByG_CERC_CN_CPK_CK_CT_P(
		long groupId, String classExternalReferenceCode, long classNameId,
		long classPK, String containerKey, long containerType, long plid) {

		return getPersistence().countByG_CERC_CN_CPK_CK_CT_P(
			groupId, classExternalReferenceCode, classNameId, classPK,
			containerKey, containerType, plid);
	}

	/**
	 * Caches the layout classed model usage in the entity cache if it is enabled.
	 *
	 * @param layoutClassedModelUsage the layout classed model usage
	 */
	public static void cacheResult(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		getPersistence().cacheResult(layoutClassedModelUsage);
	}

	/**
	 * Caches the layout classed model usages in the entity cache if it is enabled.
	 *
	 * @param layoutClassedModelUsages the layout classed model usages
	 */
	public static void cacheResult(
		List<LayoutClassedModelUsage> layoutClassedModelUsages) {

		getPersistence().cacheResult(layoutClassedModelUsages);
	}

	/**
	 * Creates a new layout classed model usage with the primary key. Does not add the layout classed model usage to the database.
	 *
	 * @param layoutClassedModelUsageId the primary key for the new layout classed model usage
	 * @return the new layout classed model usage
	 */
	public static LayoutClassedModelUsage create(
		long layoutClassedModelUsageId) {

		return getPersistence().create(layoutClassedModelUsageId);
	}

	/**
	 * Removes the layout classed model usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutClassedModelUsageId the primary key of the layout classed model usage
	 * @return the layout classed model usage that was removed
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage remove(long layoutClassedModelUsageId)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().remove(layoutClassedModelUsageId);
	}

	public static LayoutClassedModelUsage updateImpl(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		return getPersistence().updateImpl(layoutClassedModelUsage);
	}

	/**
	 * Returns the layout classed model usage with the primary key or throws a <code>NoSuchLayoutClassedModelUsageException</code> if it could not be found.
	 *
	 * @param layoutClassedModelUsageId the primary key of the layout classed model usage
	 * @return the layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage findByPrimaryKey(
			long layoutClassedModelUsageId)
		throws com.liferay.layout.exception.
			NoSuchLayoutClassedModelUsageException {

		return getPersistence().findByPrimaryKey(layoutClassedModelUsageId);
	}

	/**
	 * Returns the layout classed model usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutClassedModelUsageId the primary key of the layout classed model usage
	 * @return the layout classed model usage, or <code>null</code> if a layout classed model usage with the primary key could not be found
	 */
	public static LayoutClassedModelUsage fetchByPrimaryKey(
		long layoutClassedModelUsageId) {

		return getPersistence().fetchByPrimaryKey(layoutClassedModelUsageId);
	}

	/**
	 * Returns all the layout classed model usages.
	 *
	 * @return the layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the layout classed model usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findAll(
		int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of layout classed model usages
	 */
	public static List<LayoutClassedModelUsage> findAll(
		int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout classed model usages from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of layout classed model usages.
	 *
	 * @return the number of layout classed model usages
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static LayoutClassedModelUsagePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		LayoutClassedModelUsagePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile LayoutClassedModelUsagePersistence _persistence;

}