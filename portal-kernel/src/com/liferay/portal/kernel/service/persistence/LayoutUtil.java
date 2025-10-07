/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout service. This utility wraps <code>com.liferay.portal.service.persistence.impl.LayoutPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPersistence
 * @generated
 */
public class LayoutUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(Layout layout) {
		getPersistence().clearCache(layout);
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
	public static Map<Serializable, Layout> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<Layout> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<Layout> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<Layout> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static Layout update(Layout layout) {
		return getPersistence().update(layout);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static Layout update(Layout layout, ServiceContext serviceContext) {
		return getPersistence().update(layout, serviceContext);
	}

	/**
	 * Returns all the layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layouts
	 */
	public static List<Layout> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByUuid(String uuid, int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByUuid_First(
			String uuid, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByUuid_First(
		String uuid, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByUuid_Last(
			String uuid, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByUuid_Last(
		String uuid, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where uuid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByUuid_PrevAndNext(
			long plid, String uuid, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByUuid_PrevAndNext(
			plid, uuid, orderByComparator);
	}

	/**
	 * Removes all the layouts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layouts
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByUUID_G_P(
			String uuid, long groupId, boolean privateLayout)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByUUID_G_P(uuid, groupId, privateLayout);
	}

	/**
	 * Returns the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByUUID_G_P(
		String uuid, long groupId, boolean privateLayout) {

		return getPersistence().fetchByUUID_G_P(uuid, groupId, privateLayout);
	}

	/**
	 * Returns the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByUUID_G_P(
		String uuid, long groupId, boolean privateLayout,
		boolean useFinderCache) {

		return getPersistence().fetchByUUID_G_P(
			uuid, groupId, privateLayout, useFinderCache);
	}

	/**
	 * Removes the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the layout that was removed
	 */
	public static Layout removeByUUID_G_P(
			String uuid, long groupId, boolean privateLayout)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().removeByUUID_G_P(uuid, groupId, privateLayout);
	}

	/**
	 * Returns the number of layouts where uuid = &#63; and groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layouts
	 */
	public static int countByUUID_G_P(
		String uuid, long groupId, boolean privateLayout) {

		return getPersistence().countByUUID_G_P(uuid, groupId, privateLayout);
	}

	/**
	 * Returns all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layouts
	 */
	public static List<Layout> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByUuid_C_PrevAndNext(
			long plid, String uuid, long companyId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByUuid_C_PrevAndNext(
			plid, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the layouts where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layouts
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the layouts where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layouts
	 */
	public static List<Layout> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByGroupId(long groupId, int start, int end) {
		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByGroupId_First(
			long groupId, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByGroupId_First(
		long groupId, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByGroupId_Last(
			long groupId, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByGroupId_Last(
		long groupId, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByGroupId_PrevAndNext(
			long plid, long groupId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByGroupId_PrevAndNext(
			plid, groupId, orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] filterFindByGroupId_PrevAndNext(
			long plid, long groupId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().filterFindByGroupId_PrevAndNext(
			plid, groupId, orderByComparator);
	}

	/**
	 * Removes all the layouts where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of layouts where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layouts
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns all the layouts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching layouts
	 */
	public static List<Layout> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the layouts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByCompanyId_First(
			long companyId, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByCompanyId_First(
		long companyId, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByCompanyId_Last(
			long companyId, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByCompanyId_Last(
		long companyId, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where companyId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByCompanyId_PrevAndNext(
			long plid, long companyId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByCompanyId_PrevAndNext(
			plid, companyId, orderByComparator);
	}

	/**
	 * Removes all the layouts where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of layouts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching layouts
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns all the layouts where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @return the matching layouts
	 */
	public static List<Layout> findByParentPlid(long parentPlid) {
		return getPersistence().findByParentPlid(parentPlid);
	}

	/**
	 * Returns a range of all the layouts where parentPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param parentPlid the parent plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByParentPlid(
		long parentPlid, int start, int end) {

		return getPersistence().findByParentPlid(parentPlid, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where parentPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param parentPlid the parent plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByParentPlid(
		long parentPlid, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByParentPlid(
			parentPlid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where parentPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param parentPlid the parent plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByParentPlid(
		long parentPlid, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByParentPlid(
			parentPlid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByParentPlid_First(
			long parentPlid, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByParentPlid_First(
			parentPlid, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByParentPlid_First(
		long parentPlid, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByParentPlid_First(
			parentPlid, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByParentPlid_Last(
			long parentPlid, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByParentPlid_Last(
			parentPlid, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByParentPlid_Last(
		long parentPlid, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByParentPlid_Last(
			parentPlid, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByParentPlid_PrevAndNext(
			long plid, long parentPlid,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByParentPlid_PrevAndNext(
			plid, parentPlid, orderByComparator);
	}

	/**
	 * Removes all the layouts where parentPlid = &#63; from the database.
	 *
	 * @param parentPlid the parent plid
	 */
	public static void removeByParentPlid(long parentPlid) {
		getPersistence().removeByParentPlid(parentPlid);
	}

	/**
	 * Returns the number of layouts where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @return the number of matching layouts
	 */
	public static int countByParentPlid(long parentPlid) {
		return getPersistence().countByParentPlid(parentPlid);
	}

	/**
	 * Returns all the layouts where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @return the matching layouts
	 */
	public static List<Layout> findByIconImageId(long iconImageId) {
		return getPersistence().findByIconImageId(iconImageId);
	}

	/**
	 * Returns a range of all the layouts where iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByIconImageId(
		long iconImageId, int start, int end) {

		return getPersistence().findByIconImageId(iconImageId, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByIconImageId(
		long iconImageId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByIconImageId(
			iconImageId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByIconImageId(
		long iconImageId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByIconImageId(
			iconImageId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByIconImageId_First(
			long iconImageId, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByIconImageId_First(
			iconImageId, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByIconImageId_First(
		long iconImageId, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByIconImageId_First(
			iconImageId, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByIconImageId_Last(
			long iconImageId, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByIconImageId_Last(
			iconImageId, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByIconImageId_Last(
		long iconImageId, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByIconImageId_Last(
			iconImageId, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByIconImageId_PrevAndNext(
			long plid, long iconImageId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByIconImageId_PrevAndNext(
			plid, iconImageId, orderByComparator);
	}

	/**
	 * Removes all the layouts where iconImageId = &#63; from the database.
	 *
	 * @param iconImageId the icon image ID
	 */
	public static void removeByIconImageId(long iconImageId) {
		getPersistence().removeByIconImageId(iconImageId);
	}

	/**
	 * Returns the number of layouts where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @return the number of matching layouts
	 */
	public static int countByIconImageId(long iconImageId) {
		return getPersistence().countByIconImageId(iconImageId);
	}

	/**
	 * Returns all the layouts where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @return the matching layouts
	 */
	public static List<Layout> findByLayoutPrototypeUuid(
		String layoutPrototypeUuid) {

		return getPersistence().findByLayoutPrototypeUuid(layoutPrototypeUuid);
	}

	/**
	 * Returns a range of all the layouts where layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByLayoutPrototypeUuid(
		String layoutPrototypeUuid, int start, int end) {

		return getPersistence().findByLayoutPrototypeUuid(
			layoutPrototypeUuid, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByLayoutPrototypeUuid(
		String layoutPrototypeUuid, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByLayoutPrototypeUuid(
			layoutPrototypeUuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByLayoutPrototypeUuid(
		String layoutPrototypeUuid, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByLayoutPrototypeUuid(
			layoutPrototypeUuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByLayoutPrototypeUuid_First(
			String layoutPrototypeUuid,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByLayoutPrototypeUuid_First(
			layoutPrototypeUuid, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByLayoutPrototypeUuid_First(
		String layoutPrototypeUuid,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByLayoutPrototypeUuid_First(
			layoutPrototypeUuid, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByLayoutPrototypeUuid_Last(
			String layoutPrototypeUuid,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByLayoutPrototypeUuid_Last(
			layoutPrototypeUuid, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByLayoutPrototypeUuid_Last(
		String layoutPrototypeUuid,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByLayoutPrototypeUuid_Last(
			layoutPrototypeUuid, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByLayoutPrototypeUuid_PrevAndNext(
			long plid, String layoutPrototypeUuid,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByLayoutPrototypeUuid_PrevAndNext(
			plid, layoutPrototypeUuid, orderByComparator);
	}

	/**
	 * Removes all the layouts where layoutPrototypeUuid = &#63; from the database.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 */
	public static void removeByLayoutPrototypeUuid(String layoutPrototypeUuid) {
		getPersistence().removeByLayoutPrototypeUuid(layoutPrototypeUuid);
	}

	/**
	 * Returns the number of layouts where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @return the number of matching layouts
	 */
	public static int countByLayoutPrototypeUuid(String layoutPrototypeUuid) {
		return getPersistence().countByLayoutPrototypeUuid(layoutPrototypeUuid);
	}

	/**
	 * Returns all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the matching layouts
	 */
	public static List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC) {

		return getPersistence().findByLayoutSetPrototypeLayoutERC(
			layoutSetPrototypeLayoutERC);
	}

	/**
	 * Returns a range of all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC, int start, int end) {

		return getPersistence().findByLayoutSetPrototypeLayoutERC(
			layoutSetPrototypeLayoutERC, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByLayoutSetPrototypeLayoutERC(
			layoutSetPrototypeLayoutERC, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByLayoutSetPrototypeLayoutERC(
			layoutSetPrototypeLayoutERC, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByLayoutSetPrototypeLayoutERC_First(
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByLayoutSetPrototypeLayoutERC_First(
			layoutSetPrototypeLayoutERC, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByLayoutSetPrototypeLayoutERC_First(
		String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByLayoutSetPrototypeLayoutERC_First(
			layoutSetPrototypeLayoutERC, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByLayoutSetPrototypeLayoutERC_Last(
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByLayoutSetPrototypeLayoutERC_Last(
			layoutSetPrototypeLayoutERC, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByLayoutSetPrototypeLayoutERC_Last(
		String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByLayoutSetPrototypeLayoutERC_Last(
			layoutSetPrototypeLayoutERC, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByLayoutSetPrototypeLayoutERC_PrevAndNext(
			long plid, String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByLayoutSetPrototypeLayoutERC_PrevAndNext(
			plid, layoutSetPrototypeLayoutERC, orderByComparator);
	}

	/**
	 * Removes all the layouts where layoutSetPrototypeLayoutERC = &#63; from the database.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 */
	public static void removeByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC) {

		getPersistence().removeByLayoutSetPrototypeLayoutERC(
			layoutSetPrototypeLayoutERC);
	}

	/**
	 * Returns the number of layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the number of matching layouts
	 */
	public static int countByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC) {

		return getPersistence().countByLayoutSetPrototypeLayoutERC(
			layoutSetPrototypeLayoutERC);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P(long groupId, boolean privateLayout) {
		return getPersistence().findByG_P(groupId, privateLayout);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P(
		long groupId, boolean privateLayout, int start, int end) {

		return getPersistence().findByG_P(groupId, privateLayout, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P(
			groupId, privateLayout, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_P(
			groupId, privateLayout, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_First(
			long groupId, boolean privateLayout,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_First(
			groupId, privateLayout, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_First(
		long groupId, boolean privateLayout,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_First(
			groupId, privateLayout, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_Last(
			long groupId, boolean privateLayout,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_Last(
			groupId, privateLayout, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_Last(
		long groupId, boolean privateLayout,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_Last(
			groupId, privateLayout, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByG_P_PrevAndNext(
			long plid, long groupId, boolean privateLayout,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_PrevAndNext(
			plid, groupId, privateLayout, orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P(
		long groupId, boolean privateLayout) {

		return getPersistence().filterFindByG_P(groupId, privateLayout);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P(
		long groupId, boolean privateLayout, int start, int end) {

		return getPersistence().filterFindByG_P(
			groupId, privateLayout, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P(
			groupId, privateLayout, start, end, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] filterFindByG_P_PrevAndNext(
			long plid, long groupId, boolean privateLayout,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().filterFindByG_P_PrevAndNext(
			plid, groupId, privateLayout, orderByComparator);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 */
	public static void removeByG_P(long groupId, boolean privateLayout) {
		getPersistence().removeByG_P(groupId, privateLayout);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layouts
	 */
	public static int countByG_P(long groupId, boolean privateLayout) {
		return getPersistence().countByG_P(groupId, privateLayout);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P(long groupId, boolean privateLayout) {
		return getPersistence().filterCountByG_P(groupId, privateLayout);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_T(long groupId, String type) {
		return getPersistence().findByG_T(groupId, type);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_T(
		long groupId, String type, int start, int end) {

		return getPersistence().findByG_T(groupId, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_T(
			groupId, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_T(
			groupId, type, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_T_First(
			long groupId, String type,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_T_First(
			groupId, type, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_T_First(
		long groupId, String type,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_T_First(
			groupId, type, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_T_Last(
			long groupId, String type,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_T_Last(
			groupId, type, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_T_Last(
		long groupId, String type,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_T_Last(
			groupId, type, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByG_T_PrevAndNext(
			long plid, long groupId, String type,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_T_PrevAndNext(
			plid, groupId, type, orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_T(long groupId, String type) {
		return getPersistence().filterFindByG_T(groupId, type);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_T(
		long groupId, String type, int start, int end) {

		return getPersistence().filterFindByG_T(groupId, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_T(
			groupId, type, start, end, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] filterFindByG_T_PrevAndNext(
			long plid, long groupId, String type,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().filterFindByG_T_PrevAndNext(
			plid, groupId, type, orderByComparator);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	public static void removeByG_T(long groupId, String type) {
		getPersistence().removeByG_T(groupId, type);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layouts
	 */
	public static int countByG_T(long groupId, String type) {
		return getPersistence().countByG_T(groupId, type);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_T(long groupId, String type) {
		return getPersistence().filterCountByG_T(groupId, type);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_MLP(
		long groupId, long masterLayoutPlid) {

		return getPersistence().findByG_MLP(groupId, masterLayoutPlid);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end) {

		return getPersistence().findByG_MLP(
			groupId, masterLayoutPlid, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_MLP(
			groupId, masterLayoutPlid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_MLP(
			groupId, masterLayoutPlid, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_MLP_First(
			long groupId, long masterLayoutPlid,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_MLP_First(
			groupId, masterLayoutPlid, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_MLP_First(
		long groupId, long masterLayoutPlid,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_MLP_First(
			groupId, masterLayoutPlid, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_MLP_Last(
			long groupId, long masterLayoutPlid,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_MLP_Last(
			groupId, masterLayoutPlid, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_MLP_Last(
		long groupId, long masterLayoutPlid,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_MLP_Last(
			groupId, masterLayoutPlid, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByG_MLP_PrevAndNext(
			long plid, long groupId, long masterLayoutPlid,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_MLP_PrevAndNext(
			plid, groupId, masterLayoutPlid, orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_MLP(
		long groupId, long masterLayoutPlid) {

		return getPersistence().filterFindByG_MLP(groupId, masterLayoutPlid);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end) {

		return getPersistence().filterFindByG_MLP(
			groupId, masterLayoutPlid, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_MLP(
			groupId, masterLayoutPlid, start, end, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] filterFindByG_MLP_PrevAndNext(
			long plid, long groupId, long masterLayoutPlid,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().filterFindByG_MLP_PrevAndNext(
			plid, groupId, masterLayoutPlid, orderByComparator);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and masterLayoutPlid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 */
	public static void removeByG_MLP(long groupId, long masterLayoutPlid) {
		getPersistence().removeByG_MLP(groupId, masterLayoutPlid);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @return the number of matching layouts
	 */
	public static int countByG_MLP(long groupId, long masterLayoutPlid) {
		return getPersistence().countByG_MLP(groupId, masterLayoutPlid);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_MLP(long groupId, long masterLayoutPlid) {
		return getPersistence().filterCountByG_MLP(groupId, masterLayoutPlid);
	}

	/**
	 * Returns all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @return the matching layouts
	 */
	public static List<Layout> findByC_L(
		long companyId, String layoutPrototypeUuid) {

		return getPersistence().findByC_L(companyId, layoutPrototypeUuid);
	}

	/**
	 * Returns a range of all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByC_L(
		long companyId, String layoutPrototypeUuid, int start, int end) {

		return getPersistence().findByC_L(
			companyId, layoutPrototypeUuid, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByC_L(
		long companyId, String layoutPrototypeUuid, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByC_L(
			companyId, layoutPrototypeUuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByC_L(
		long companyId, String layoutPrototypeUuid, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_L(
			companyId, layoutPrototypeUuid, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByC_L_First(
			long companyId, String layoutPrototypeUuid,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByC_L_First(
			companyId, layoutPrototypeUuid, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByC_L_First(
		long companyId, String layoutPrototypeUuid,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByC_L_First(
			companyId, layoutPrototypeUuid, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByC_L_Last(
			long companyId, String layoutPrototypeUuid,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByC_L_Last(
			companyId, layoutPrototypeUuid, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByC_L_Last(
		long companyId, String layoutPrototypeUuid,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByC_L_Last(
			companyId, layoutPrototypeUuid, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByC_L_PrevAndNext(
			long plid, long companyId, String layoutPrototypeUuid,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByC_L_PrevAndNext(
			plid, companyId, layoutPrototypeUuid, orderByComparator);
	}

	/**
	 * Removes all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 */
	public static void removeByC_L(long companyId, String layoutPrototypeUuid) {
		getPersistence().removeByC_L(companyId, layoutPrototypeUuid);
	}

	/**
	 * Returns the number of layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @return the number of matching layouts
	 */
	public static int countByC_L(long companyId, String layoutPrototypeUuid) {
		return getPersistence().countByC_L(companyId, layoutPrototypeUuid);
	}

	/**
	 * Returns all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @return the matching layouts
	 */
	public static List<Layout> findByP_I(
		boolean privateLayout, long iconImageId) {

		return getPersistence().findByP_I(privateLayout, iconImageId);
	}

	/**
	 * Returns a range of all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByP_I(
		boolean privateLayout, long iconImageId, int start, int end) {

		return getPersistence().findByP_I(
			privateLayout, iconImageId, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByP_I(
		boolean privateLayout, long iconImageId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByP_I(
			privateLayout, iconImageId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByP_I(
		boolean privateLayout, long iconImageId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByP_I(
			privateLayout, iconImageId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByP_I_First(
			boolean privateLayout, long iconImageId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByP_I_First(
			privateLayout, iconImageId, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByP_I_First(
		boolean privateLayout, long iconImageId,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByP_I_First(
			privateLayout, iconImageId, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByP_I_Last(
			boolean privateLayout, long iconImageId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByP_I_Last(
			privateLayout, iconImageId, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByP_I_Last(
		boolean privateLayout, long iconImageId,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByP_I_Last(
			privateLayout, iconImageId, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByP_I_PrevAndNext(
			long plid, boolean privateLayout, long iconImageId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByP_I_PrevAndNext(
			plid, privateLayout, iconImageId, orderByComparator);
	}

	/**
	 * Removes all the layouts where privateLayout = &#63; and iconImageId = &#63; from the database.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 */
	public static void removeByP_I(boolean privateLayout, long iconImageId) {
		getPersistence().removeByP_I(privateLayout, iconImageId);
	}

	/**
	 * Returns the number of layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @return the number of matching layouts
	 */
	public static int countByP_I(boolean privateLayout, long iconImageId) {
		return getPersistence().countByP_I(privateLayout, iconImageId);
	}

	/**
	 * Returns all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching layouts
	 */
	public static List<Layout> findByC_C(long classNameId, long classPK) {
		return getPersistence().findByC_C(classNameId, classPK);
	}

	/**
	 * Returns a range of all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return getPersistence().findByC_C(classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByC_C_Last(
			long classNameId, long classPK,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByC_C_Last(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByC_C_Last(
		long classNameId, long classPK,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByC_C_Last(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByC_C_PrevAndNext(
			long plid, long classNameId, long classPK,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByC_C_PrevAndNext(
			plid, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns all the layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the matching layouts
	 */
	public static List<Layout> findByC_C(long classNameId, long[] classPKs) {
		return getPersistence().findByC_C(classNameId, classPKs);
	}

	/**
	 * Returns a range of all the layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByC_C(
		long classNameId, long[] classPKs, int start, int end) {

		return getPersistence().findByC_C(classNameId, classPKs, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByC_C(
		long classNameId, long[] classPKs, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByC_C(
			classNameId, classPKs, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByC_C(
		long classNameId, long[] classPKs, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_C(
			classNameId, classPKs, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the layouts where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByC_C(long classNameId, long classPK) {
		getPersistence().removeByC_C(classNameId, classPK);
	}

	/**
	 * Returns the number of layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching layouts
	 */
	public static int countByC_C(long classNameId, long classPK) {
		return getPersistence().countByC_C(classNameId, classPK);
	}

	/**
	 * Returns the number of layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the number of matching layouts
	 */
	public static int countByC_C(long classNameId, long[] classPKs) {
		return getPersistence().countByC_C(classNameId, classPKs);
	}

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_L(
			long groupId, boolean privateLayout, long layoutId)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_L(groupId, privateLayout, layoutId);
	}

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_L(
		long groupId, boolean privateLayout, long layoutId) {

		return getPersistence().fetchByG_P_L(groupId, privateLayout, layoutId);
	}

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_L(
		long groupId, boolean privateLayout, long layoutId,
		boolean useFinderCache) {

		return getPersistence().fetchByG_P_L(
			groupId, privateLayout, layoutId, useFinderCache);
	}

	/**
	 * Removes the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the layout that was removed
	 */
	public static Layout removeByG_P_L(
			long groupId, boolean privateLayout, long layoutId)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().removeByG_P_L(groupId, privateLayout, layoutId);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and layoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the number of matching layouts
	 */
	public static int countByG_P_L(
		long groupId, boolean privateLayout, long layoutId) {

		return getPersistence().countByG_P_L(groupId, privateLayout, layoutId);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return getPersistence().findByG_P_P(
			groupId, privateLayout, parentLayoutId);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end) {

		return getPersistence().findByG_P_P(
			groupId, privateLayout, parentLayoutId, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P_P(
			groupId, privateLayout, parentLayoutId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_P(
			groupId, privateLayout, parentLayoutId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_P_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_P_First(
			groupId, privateLayout, parentLayoutId, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_P_First(
		long groupId, boolean privateLayout, long parentLayoutId,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_P_First(
			groupId, privateLayout, parentLayoutId, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_P_Last(
			long groupId, boolean privateLayout, long parentLayoutId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_P_Last(
			groupId, privateLayout, parentLayoutId, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_P_Last(
		long groupId, boolean privateLayout, long parentLayoutId,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_P_Last(
			groupId, privateLayout, parentLayoutId, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByG_P_P_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_P_PrevAndNext(
			plid, groupId, privateLayout, parentLayoutId, orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return getPersistence().filterFindByG_P_P(
			groupId, privateLayout, parentLayoutId);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end) {

		return getPersistence().filterFindByG_P_P(
			groupId, privateLayout, parentLayoutId, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P_P(
			groupId, privateLayout, parentLayoutId, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] filterFindByG_P_P_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().filterFindByG_P_P_PrevAndNext(
			plid, groupId, privateLayout, parentLayoutId, orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		return getPersistence().filterFindByG_P_P(
			groupId, privateLayout, parentLayoutIds);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end) {

		return getPersistence().filterFindByG_P_P(
			groupId, privateLayout, parentLayoutIds, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P_P(
			groupId, privateLayout, parentLayoutIds, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		return getPersistence().findByG_P_P(
			groupId, privateLayout, parentLayoutIds);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end) {

		return getPersistence().findByG_P_P(
			groupId, privateLayout, parentLayoutIds, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P_P(
			groupId, privateLayout, parentLayoutIds, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_P(
			groupId, privateLayout, parentLayoutIds, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 */
	public static void removeByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		getPersistence().removeByG_P_P(groupId, privateLayout, parentLayoutId);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the number of matching layouts
	 */
	public static int countByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return getPersistence().countByG_P_P(
			groupId, privateLayout, parentLayoutId);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the number of matching layouts
	 */
	public static int countByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		return getPersistence().countByG_P_P(
			groupId, privateLayout, parentLayoutIds);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return getPersistence().filterCountByG_P_P(
			groupId, privateLayout, parentLayoutId);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		return getPersistence().filterCountByG_P_P(
			groupId, privateLayout, parentLayoutIds);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type) {

		return getPersistence().findByG_P_T(groupId, privateLayout, type);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end) {

		return getPersistence().findByG_P_T(
			groupId, privateLayout, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P_T(
			groupId, privateLayout, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_P_T(
			groupId, privateLayout, type, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_T_First(
			long groupId, boolean privateLayout, String type,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_T_First(
			groupId, privateLayout, type, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_T_First(
		long groupId, boolean privateLayout, String type,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_T_First(
			groupId, privateLayout, type, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_T_Last(
			long groupId, boolean privateLayout, String type,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_T_Last(
			groupId, privateLayout, type, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_T_Last(
		long groupId, boolean privateLayout, String type,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_T_Last(
			groupId, privateLayout, type, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByG_P_T_PrevAndNext(
			long plid, long groupId, boolean privateLayout, String type,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_T_PrevAndNext(
			plid, groupId, privateLayout, type, orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String type) {

		return getPersistence().filterFindByG_P_T(groupId, privateLayout, type);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end) {

		return getPersistence().filterFindByG_P_T(
			groupId, privateLayout, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P_T(
			groupId, privateLayout, type, start, end, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] filterFindByG_P_T_PrevAndNext(
			long plid, long groupId, boolean privateLayout, String type,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().filterFindByG_P_T_PrevAndNext(
			plid, groupId, privateLayout, type, orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String[] types) {

		return getPersistence().filterFindByG_P_T(
			groupId, privateLayout, types);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start,
		int end) {

		return getPersistence().filterFindByG_P_T(
			groupId, privateLayout, types, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P_T(
			groupId, privateLayout, types, start, end, orderByComparator);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types) {

		return getPersistence().findByG_P_T(groupId, privateLayout, types);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start,
		int end) {

		return getPersistence().findByG_P_T(
			groupId, privateLayout, types, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P_T(
			groupId, privateLayout, types, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_P_T(
			groupId, privateLayout, types, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 */
	public static void removeByG_P_T(
		long groupId, boolean privateLayout, String type) {

		getPersistence().removeByG_P_T(groupId, privateLayout, type);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the number of matching layouts
	 */
	public static int countByG_P_T(
		long groupId, boolean privateLayout, String type) {

		return getPersistence().countByG_P_T(groupId, privateLayout, type);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the number of matching layouts
	 */
	public static int countByG_P_T(
		long groupId, boolean privateLayout, String[] types) {

		return getPersistence().countByG_P_T(groupId, privateLayout, types);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P_T(
		long groupId, boolean privateLayout, String type) {

		return getPersistence().filterCountByG_P_T(
			groupId, privateLayout, type);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P_T(
		long groupId, boolean privateLayout, String[] types) {

		return getPersistence().filterCountByG_P_T(
			groupId, privateLayout, types);
	}

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_F(
			long groupId, boolean privateLayout, String friendlyURL)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_F(
			groupId, privateLayout, friendlyURL);
	}

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL) {

		return getPersistence().fetchByG_P_F(
			groupId, privateLayout, friendlyURL);
	}

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL,
		boolean useFinderCache) {

		return getPersistence().fetchByG_P_F(
			groupId, privateLayout, friendlyURL, useFinderCache);
	}

	/**
	 * Removes the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the layout that was removed
	 */
	public static Layout removeByG_P_F(
			long groupId, boolean privateLayout, String friendlyURL)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().removeByG_P_F(
			groupId, privateLayout, friendlyURL);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the number of matching layouts
	 */
	public static int countByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL) {

		return getPersistence().countByG_P_F(
			groupId, privateLayout, friendlyURL);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		return getPersistence().findByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end) {

		return getPersistence().findByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_LSPLE_First(
			long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_LSPLE_First(
			groupId, privateLayout, layoutSetPrototypeLayoutERC,
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_LSPLE_First(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_LSPLE_First(
			groupId, privateLayout, layoutSetPrototypeLayoutERC,
			orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_LSPLE_Last(
			long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_LSPLE_Last(
			groupId, privateLayout, layoutSetPrototypeLayoutERC,
			orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_LSPLE_Last(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_LSPLE_Last(
			groupId, privateLayout, layoutSetPrototypeLayoutERC,
			orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByG_P_LSPLE_PrevAndNext(
			long plid, long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_LSPLE_PrevAndNext(
			plid, groupId, privateLayout, layoutSetPrototypeLayoutERC,
			orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		return getPersistence().filterFindByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end) {

		return getPersistence().filterFindByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] filterFindByG_P_LSPLE_PrevAndNext(
			long plid, long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().filterFindByG_P_LSPLE_PrevAndNext(
			plid, groupId, privateLayout, layoutSetPrototypeLayoutERC,
			orderByComparator);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 */
	public static void removeByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		getPersistence().removeByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the number of matching layouts
	 */
	public static int countByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		return getPersistence().countByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		return getPersistence().filterCountByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status) {

		return getPersistence().findByG_P_ST(groupId, privateLayout, status);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end) {

		return getPersistence().findByG_P_ST(
			groupId, privateLayout, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P_ST(
			groupId, privateLayout, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_P_ST(
			groupId, privateLayout, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_ST_First(
			long groupId, boolean privateLayout, int status,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_ST_First(
			groupId, privateLayout, status, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_ST_First(
		long groupId, boolean privateLayout, int status,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_ST_First(
			groupId, privateLayout, status, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_ST_Last(
			long groupId, boolean privateLayout, int status,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_ST_Last(
			groupId, privateLayout, status, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_ST_Last(
		long groupId, boolean privateLayout, int status,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_ST_Last(
			groupId, privateLayout, status, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByG_P_ST_PrevAndNext(
			long plid, long groupId, boolean privateLayout, int status,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_ST_PrevAndNext(
			plid, groupId, privateLayout, status, orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int status) {

		return getPersistence().filterFindByG_P_ST(
			groupId, privateLayout, status);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end) {

		return getPersistence().filterFindByG_P_ST(
			groupId, privateLayout, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P_ST(
			groupId, privateLayout, status, start, end, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] filterFindByG_P_ST_PrevAndNext(
			long plid, long groupId, boolean privateLayout, int status,
			OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().filterFindByG_P_ST_PrevAndNext(
			plid, groupId, privateLayout, status, orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses) {

		return getPersistence().filterFindByG_P_ST(
			groupId, privateLayout, statuses);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start,
		int end) {

		return getPersistence().filterFindByG_P_ST(
			groupId, privateLayout, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P_ST(
			groupId, privateLayout, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses) {

		return getPersistence().findByG_P_ST(groupId, privateLayout, statuses);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start,
		int end) {

		return getPersistence().findByG_P_ST(
			groupId, privateLayout, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P_ST(
			groupId, privateLayout, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_P_ST(
			groupId, privateLayout, statuses, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 */
	public static void removeByG_P_ST(
		long groupId, boolean privateLayout, int status) {

		getPersistence().removeByG_P_ST(groupId, privateLayout, status);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the number of matching layouts
	 */
	public static int countByG_P_ST(
		long groupId, boolean privateLayout, int status) {

		return getPersistence().countByG_P_ST(groupId, privateLayout, status);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the number of matching layouts
	 */
	public static int countByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses) {

		return getPersistence().countByG_P_ST(groupId, privateLayout, statuses);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P_ST(
		long groupId, boolean privateLayout, int status) {

		return getPersistence().filterCountByG_P_ST(
			groupId, privateLayout, status);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses) {

		return getPersistence().filterCountByG_P_ST(
			groupId, privateLayout, statuses);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		return getPersistence().findByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end) {

		return getPersistence().findByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_P_H_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_P_H_First(
			groupId, privateLayout, parentLayoutId, hidden, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_P_H_First(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_P_H_First(
			groupId, privateLayout, parentLayoutId, hidden, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_P_H_Last(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_P_H_Last(
			groupId, privateLayout, parentLayoutId, hidden, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_P_H_Last(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_P_H_Last(
			groupId, privateLayout, parentLayoutId, hidden, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByG_P_P_H_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_P_H_PrevAndNext(
			plid, groupId, privateLayout, parentLayoutId, hidden,
			orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		return getPersistence().filterFindByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end) {

		return getPersistence().filterFindByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] filterFindByG_P_P_H_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().filterFindByG_P_P_H_PrevAndNext(
			plid, groupId, privateLayout, parentLayoutId, hidden,
			orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		return getPersistence().filterFindByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end) {

		return getPersistence().filterFindByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		return getPersistence().findByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end) {

		return getPersistence().findByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 */
	public static void removeByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		getPersistence().removeByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the number of matching layouts
	 */
	public static int countByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		return getPersistence().countByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the number of matching layouts
	 */
	public static int countByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		return getPersistence().countByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		return getPersistence().filterCountByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		return getPersistence().filterCountByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		return getPersistence().findByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end) {

		return getPersistence().findByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_P_S_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean system, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_P_S_First(
			groupId, privateLayout, parentLayoutId, system, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_P_S_First(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_P_S_First(
			groupId, privateLayout, parentLayoutId, system, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_P_S_Last(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean system, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_P_S_Last(
			groupId, privateLayout, parentLayoutId, system, orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_P_S_Last(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_P_S_Last(
			groupId, privateLayout, parentLayoutId, system, orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByG_P_P_S_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			boolean system, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_P_S_PrevAndNext(
			plid, groupId, privateLayout, parentLayoutId, system,
			orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		return getPersistence().filterFindByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end) {

		return getPersistence().filterFindByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] filterFindByG_P_P_S_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			boolean system, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().filterFindByG_P_P_S_PrevAndNext(
			plid, groupId, privateLayout, parentLayoutId, system,
			orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		return getPersistence().filterFindByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end) {

		return getPersistence().filterFindByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		return getPersistence().findByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end) {

		return getPersistence().findByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 */
	public static void removeByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		getPersistence().removeByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the number of matching layouts
	 */
	public static int countByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		return getPersistence().countByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the number of matching layouts
	 */
	public static int countByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		return getPersistence().countByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		return getPersistence().filterCountByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		return getPersistence().filterCountByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system);
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the matching layouts
	 */
	public static List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		return getPersistence().findByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public static List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end) {

		return getPersistence().findByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public static List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_P_LteP_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			int priority, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_P_LteP_First(
			groupId, privateLayout, parentLayoutId, priority,
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_P_LteP_First(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_P_LteP_First(
			groupId, privateLayout, parentLayoutId, priority,
			orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByG_P_P_LteP_Last(
			long groupId, boolean privateLayout, long parentLayoutId,
			int priority, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_P_LteP_Last(
			groupId, privateLayout, parentLayoutId, priority,
			orderByComparator);
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByG_P_P_LteP_Last(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		OrderByComparator<Layout> orderByComparator) {

		return getPersistence().fetchByG_P_P_LteP_Last(
			groupId, privateLayout, parentLayoutId, priority,
			orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] findByG_P_P_LteP_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			int priority, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByG_P_P_LteP_PrevAndNext(
			plid, groupId, privateLayout, parentLayoutId, priority,
			orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		return getPersistence().filterFindByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end) {

		return getPersistence().filterFindByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, start, end);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public static List<Layout> filterFindByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().filterFindByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout[] filterFindByG_P_P_LteP_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			int priority, OrderByComparator<Layout> orderByComparator)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().filterFindByG_P_P_LteP_PrevAndNext(
			plid, groupId, privateLayout, parentLayoutId, priority,
			orderByComparator);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 */
	public static void removeByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		getPersistence().removeByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the number of matching layouts
	 */
	public static int countByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		return getPersistence().countByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the number of matching layouts that the user has permission to view
	 */
	public static int filterCountByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		return getPersistence().filterCountByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority);
	}

	/**
	 * Returns the layout where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public static Layout findByERC_G(String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the layout where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout that was removed
	 */
	public static Layout removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of layouts where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layouts
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Caches the layout in the entity cache if it is enabled.
	 *
	 * @param layout the layout
	 */
	public static void cacheResult(Layout layout) {
		getPersistence().cacheResult(layout);
	}

	/**
	 * Caches the layouts in the entity cache if it is enabled.
	 *
	 * @param layouts the layouts
	 */
	public static void cacheResult(List<Layout> layouts) {
		getPersistence().cacheResult(layouts);
	}

	/**
	 * Creates a new layout with the primary key. Does not add the layout to the database.
	 *
	 * @param plid the primary key for the new layout
	 * @return the new layout
	 */
	public static Layout create(long plid) {
		return getPersistence().create(plid);
	}

	/**
	 * Removes the layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout that was removed
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout remove(long plid)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().remove(plid);
	}

	public static Layout updateImpl(Layout layout) {
		return getPersistence().updateImpl(layout);
	}

	/**
	 * Returns the layout with the primary key or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public static Layout findByPrimaryKey(long plid)
		throws com.liferay.portal.kernel.exception.NoSuchLayoutException {

		return getPersistence().findByPrimaryKey(plid);
	}

	/**
	 * Returns the layout with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout, or <code>null</code> if a layout with the primary key could not be found
	 */
	public static Layout fetchByPrimaryKey(long plid) {
		return getPersistence().fetchByPrimaryKey(plid);
	}

	/**
	 * Returns all the layouts.
	 *
	 * @return the layouts
	 */
	public static List<Layout> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of layouts
	 */
	public static List<Layout> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of layouts
	 */
	public static List<Layout> findAll(
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of layouts
	 */
	public static List<Layout> findAll(
		int start, int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layouts from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of layouts.
	 *
	 * @return the number of layouts
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static LayoutPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(LayoutPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile LayoutPersistence _persistence;

}