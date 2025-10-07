/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the site navigation menu item service. This utility wraps <code>com.liferay.site.navigation.service.persistence.impl.SiteNavigationMenuItemPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SiteNavigationMenuItemPersistence
 * @generated
 */
public class SiteNavigationMenuItemUtil {

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
		SiteNavigationMenuItem siteNavigationMenuItem) {

		getPersistence().clearCache(siteNavigationMenuItem);
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
	public static Map<Serializable, SiteNavigationMenuItem> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<SiteNavigationMenuItem> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<SiteNavigationMenuItem> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<SiteNavigationMenuItem> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static SiteNavigationMenuItem update(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		return getPersistence().update(siteNavigationMenuItem);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static SiteNavigationMenuItem update(
		SiteNavigationMenuItem siteNavigationMenuItem,
		ServiceContext serviceContext) {

		return getPersistence().update(siteNavigationMenuItem, serviceContext);
	}

	/**
	 * Returns all the site navigation menu items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the site navigation menu items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @return the range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByUuid_First(
			String uuid,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByUuid_First(
		String uuid,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByUuid_Last(
			String uuid,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByUuid_Last(
		String uuid,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the site navigation menu items before and after the current site navigation menu item in the ordered set where uuid = &#63;.
	 *
	 * @param siteNavigationMenuItemId the primary key of the current site navigation menu item
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next site navigation menu item
	 * @throws NoSuchMenuItemException if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem[] findByUuid_PrevAndNext(
			long siteNavigationMenuItemId, String uuid,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByUuid_PrevAndNext(
			siteNavigationMenuItemId, uuid, orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of site navigation menu items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching site navigation menu items
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the site navigation menu item where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchMenuItemException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByUUID_G(String uuid, long groupId)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the site navigation menu item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the site navigation menu item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the site navigation menu item where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the site navigation menu item that was removed
	 */
	public static SiteNavigationMenuItem removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of site navigation menu items where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching site navigation menu items
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the site navigation menu items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the site navigation menu items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @return the range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the site navigation menu items before and after the current site navigation menu item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param siteNavigationMenuItemId the primary key of the current site navigation menu item
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next site navigation menu item
	 * @throws NoSuchMenuItemException if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem[] findByUuid_C_PrevAndNext(
			long siteNavigationMenuItemId, String uuid, long companyId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByUuid_C_PrevAndNext(
			siteNavigationMenuItemId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of site navigation menu items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching site navigation menu items
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the site navigation menu items where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the site navigation menu items where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @return the range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByCompanyId_First(
			long companyId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByCompanyId_First(
		long companyId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByCompanyId_Last(
			long companyId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the site navigation menu items before and after the current site navigation menu item in the ordered set where companyId = &#63;.
	 *
	 * @param siteNavigationMenuItemId the primary key of the current site navigation menu item
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next site navigation menu item
	 * @throws NoSuchMenuItemException if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem[] findByCompanyId_PrevAndNext(
			long siteNavigationMenuItemId, long companyId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByCompanyId_PrevAndNext(
			siteNavigationMenuItemId, companyId, orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of site navigation menu items where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching site navigation menu items
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns all the site navigation menu items where siteNavigationMenuId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @return the matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findBySiteNavigationMenuId(
		long siteNavigationMenuId) {

		return getPersistence().findBySiteNavigationMenuId(
			siteNavigationMenuId);
	}

	/**
	 * Returns a range of all the site navigation menu items where siteNavigationMenuId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @return the range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findBySiteNavigationMenuId(
		long siteNavigationMenuId, int start, int end) {

		return getPersistence().findBySiteNavigationMenuId(
			siteNavigationMenuId, start, end);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where siteNavigationMenuId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findBySiteNavigationMenuId(
		long siteNavigationMenuId, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().findBySiteNavigationMenuId(
			siteNavigationMenuId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where siteNavigationMenuId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findBySiteNavigationMenuId(
		long siteNavigationMenuId, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findBySiteNavigationMenuId(
			siteNavigationMenuId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where siteNavigationMenuId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findBySiteNavigationMenuId_First(
			long siteNavigationMenuId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findBySiteNavigationMenuId_First(
			siteNavigationMenuId, orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where siteNavigationMenuId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchBySiteNavigationMenuId_First(
		long siteNavigationMenuId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchBySiteNavigationMenuId_First(
			siteNavigationMenuId, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where siteNavigationMenuId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findBySiteNavigationMenuId_Last(
			long siteNavigationMenuId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findBySiteNavigationMenuId_Last(
			siteNavigationMenuId, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where siteNavigationMenuId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchBySiteNavigationMenuId_Last(
		long siteNavigationMenuId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchBySiteNavigationMenuId_Last(
			siteNavigationMenuId, orderByComparator);
	}

	/**
	 * Returns the site navigation menu items before and after the current site navigation menu item in the ordered set where siteNavigationMenuId = &#63;.
	 *
	 * @param siteNavigationMenuItemId the primary key of the current site navigation menu item
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next site navigation menu item
	 * @throws NoSuchMenuItemException if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem[]
			findBySiteNavigationMenuId_PrevAndNext(
				long siteNavigationMenuItemId, long siteNavigationMenuId,
				OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findBySiteNavigationMenuId_PrevAndNext(
			siteNavigationMenuItemId, siteNavigationMenuId, orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where siteNavigationMenuId = &#63; from the database.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 */
	public static void removeBySiteNavigationMenuId(long siteNavigationMenuId) {
		getPersistence().removeBySiteNavigationMenuId(siteNavigationMenuId);
	}

	/**
	 * Returns the number of site navigation menu items where siteNavigationMenuId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @return the number of matching site navigation menu items
	 */
	public static int countBySiteNavigationMenuId(long siteNavigationMenuId) {
		return getPersistence().countBySiteNavigationMenuId(
			siteNavigationMenuId);
	}

	/**
	 * Returns all the site navigation menu items where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @return the matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem>
		findByParentSiteNavigationMenuItemId(
			long parentSiteNavigationMenuItemId) {

		return getPersistence().findByParentSiteNavigationMenuItemId(
			parentSiteNavigationMenuItemId);
	}

	/**
	 * Returns a range of all the site navigation menu items where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @return the range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem>
		findByParentSiteNavigationMenuItemId(
			long parentSiteNavigationMenuItemId, int start, int end) {

		return getPersistence().findByParentSiteNavigationMenuItemId(
			parentSiteNavigationMenuItemId, start, end);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem>
		findByParentSiteNavigationMenuItemId(
			long parentSiteNavigationMenuItemId, int start, int end,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().findByParentSiteNavigationMenuItemId(
			parentSiteNavigationMenuItemId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem>
		findByParentSiteNavigationMenuItemId(
			long parentSiteNavigationMenuItemId, int start, int end,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByParentSiteNavigationMenuItemId(
			parentSiteNavigationMenuItemId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem
			findByParentSiteNavigationMenuItemId_First(
				long parentSiteNavigationMenuItemId,
				OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByParentSiteNavigationMenuItemId_First(
			parentSiteNavigationMenuItemId, orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem
		fetchByParentSiteNavigationMenuItemId_First(
			long parentSiteNavigationMenuItemId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByParentSiteNavigationMenuItemId_First(
			parentSiteNavigationMenuItemId, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem
			findByParentSiteNavigationMenuItemId_Last(
				long parentSiteNavigationMenuItemId,
				OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByParentSiteNavigationMenuItemId_Last(
			parentSiteNavigationMenuItemId, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem
		fetchByParentSiteNavigationMenuItemId_Last(
			long parentSiteNavigationMenuItemId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByParentSiteNavigationMenuItemId_Last(
			parentSiteNavigationMenuItemId, orderByComparator);
	}

	/**
	 * Returns the site navigation menu items before and after the current site navigation menu item in the ordered set where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param siteNavigationMenuItemId the primary key of the current site navigation menu item
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next site navigation menu item
	 * @throws NoSuchMenuItemException if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem[]
			findByParentSiteNavigationMenuItemId_PrevAndNext(
				long siteNavigationMenuItemId,
				long parentSiteNavigationMenuItemId,
				OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().
			findByParentSiteNavigationMenuItemId_PrevAndNext(
				siteNavigationMenuItemId, parentSiteNavigationMenuItemId,
				orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where parentSiteNavigationMenuItemId = &#63; from the database.
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 */
	public static void removeByParentSiteNavigationMenuItemId(
		long parentSiteNavigationMenuItemId) {

		getPersistence().removeByParentSiteNavigationMenuItemId(
			parentSiteNavigationMenuItemId);
	}

	/**
	 * Returns the number of site navigation menu items where parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @return the number of matching site navigation menu items
	 */
	public static int countByParentSiteNavigationMenuItemId(
		long parentSiteNavigationMenuItemId) {

		return getPersistence().countByParentSiteNavigationMenuItemId(
			parentSiteNavigationMenuItemId);
	}

	/**
	 * Returns all the site navigation menu items where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByType(String type) {
		return getPersistence().findByType(type);
	}

	/**
	 * Returns a range of all the site navigation menu items where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @return the range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByType(
		String type, int start, int end) {

		return getPersistence().findByType(type, start, end);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByType(
		String type, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().findByType(type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByType(
		String type, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByType(
			type, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByType_First(
			String type,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByType_First(type, orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByType_First(
		String type,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByType_First(type, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByType_Last(
			String type,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByType_Last(type, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByType_Last(
		String type,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByType_Last(type, orderByComparator);
	}

	/**
	 * Returns the site navigation menu items before and after the current site navigation menu item in the ordered set where type = &#63;.
	 *
	 * @param siteNavigationMenuItemId the primary key of the current site navigation menu item
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next site navigation menu item
	 * @throws NoSuchMenuItemException if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem[] findByType_PrevAndNext(
			long siteNavigationMenuItemId, String type,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByType_PrevAndNext(
			siteNavigationMenuItemId, type, orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	public static void removeByType(String type) {
		getPersistence().removeByType(type);
	}

	/**
	 * Returns the number of site navigation menu items where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching site navigation menu items
	 */
	public static int countByType(String type) {
		return getPersistence().countByType(type);
	}

	/**
	 * Returns all the site navigation menu items where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @return the matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByS_P(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId) {

		return getPersistence().findByS_P(
			siteNavigationMenuId, parentSiteNavigationMenuItemId);
	}

	/**
	 * Returns a range of all the site navigation menu items where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @return the range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByS_P(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
		int start, int end) {

		return getPersistence().findByS_P(
			siteNavigationMenuId, parentSiteNavigationMenuItemId, start, end);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByS_P(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
		int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().findByS_P(
			siteNavigationMenuId, parentSiteNavigationMenuItemId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByS_P(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
		int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByS_P(
			siteNavigationMenuId, parentSiteNavigationMenuItemId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByS_P_First(
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByS_P_First(
			siteNavigationMenuId, parentSiteNavigationMenuItemId,
			orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByS_P_First(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByS_P_First(
			siteNavigationMenuId, parentSiteNavigationMenuItemId,
			orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByS_P_Last(
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByS_P_Last(
			siteNavigationMenuId, parentSiteNavigationMenuItemId,
			orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByS_P_Last(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByS_P_Last(
			siteNavigationMenuId, parentSiteNavigationMenuItemId,
			orderByComparator);
	}

	/**
	 * Returns the site navigation menu items before and after the current site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param siteNavigationMenuItemId the primary key of the current site navigation menu item
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next site navigation menu item
	 * @throws NoSuchMenuItemException if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem[] findByS_P_PrevAndNext(
			long siteNavigationMenuItemId, long siteNavigationMenuId,
			long parentSiteNavigationMenuItemId,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByS_P_PrevAndNext(
			siteNavigationMenuItemId, siteNavigationMenuId,
			parentSiteNavigationMenuItemId, orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63; from the database.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 */
	public static void removeByS_P(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId) {

		getPersistence().removeByS_P(
			siteNavigationMenuId, parentSiteNavigationMenuItemId);
	}

	/**
	 * Returns the number of site navigation menu items where siteNavigationMenuId = &#63; and parentSiteNavigationMenuItemId = &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param parentSiteNavigationMenuItemId the parent site navigation menu item ID
	 * @return the number of matching site navigation menu items
	 */
	public static int countByS_P(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId) {

		return getPersistence().countByS_P(
			siteNavigationMenuId, parentSiteNavigationMenuItemId);
	}

	/**
	 * Returns all the site navigation menu items where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @return the matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByS_LikeN(
		long siteNavigationMenuId, String name) {

		return getPersistence().findByS_LikeN(siteNavigationMenuId, name);
	}

	/**
	 * Returns a range of all the site navigation menu items where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @return the range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByS_LikeN(
		long siteNavigationMenuId, String name, int start, int end) {

		return getPersistence().findByS_LikeN(
			siteNavigationMenuId, name, start, end);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByS_LikeN(
		long siteNavigationMenuId, String name, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().findByS_LikeN(
			siteNavigationMenuId, name, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findByS_LikeN(
		long siteNavigationMenuId, String name, int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByS_LikeN(
			siteNavigationMenuId, name, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByS_LikeN_First(
			long siteNavigationMenuId, String name,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByS_LikeN_First(
			siteNavigationMenuId, name, orderByComparator);
	}

	/**
	 * Returns the first site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByS_LikeN_First(
		long siteNavigationMenuId, String name,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByS_LikeN_First(
			siteNavigationMenuId, name, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByS_LikeN_Last(
			long siteNavigationMenuId, String name,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByS_LikeN_Last(
			siteNavigationMenuId, name, orderByComparator);
	}

	/**
	 * Returns the last site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByS_LikeN_Last(
		long siteNavigationMenuId, String name,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().fetchByS_LikeN_Last(
			siteNavigationMenuId, name, orderByComparator);
	}

	/**
	 * Returns the site navigation menu items before and after the current site navigation menu item in the ordered set where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * @param siteNavigationMenuItemId the primary key of the current site navigation menu item
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next site navigation menu item
	 * @throws NoSuchMenuItemException if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem[] findByS_LikeN_PrevAndNext(
			long siteNavigationMenuItemId, long siteNavigationMenuId,
			String name,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByS_LikeN_PrevAndNext(
			siteNavigationMenuItemId, siteNavigationMenuId, name,
			orderByComparator);
	}

	/**
	 * Removes all the site navigation menu items where siteNavigationMenuId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 */
	public static void removeByS_LikeN(long siteNavigationMenuId, String name) {
		getPersistence().removeByS_LikeN(siteNavigationMenuId, name);
	}

	/**
	 * Returns the number of site navigation menu items where siteNavigationMenuId = &#63; and name LIKE &#63;.
	 *
	 * @param siteNavigationMenuId the site navigation menu ID
	 * @param name the name
	 * @return the number of matching site navigation menu items
	 */
	public static int countByS_LikeN(long siteNavigationMenuId, String name) {
		return getPersistence().countByS_LikeN(siteNavigationMenuId, name);
	}

	/**
	 * Returns the site navigation menu item where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchMenuItemException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching site navigation menu item
	 * @throws NoSuchMenuItemException if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the site navigation menu item where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the site navigation menu item where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	public static SiteNavigationMenuItem fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the site navigation menu item where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the site navigation menu item that was removed
	 */
	public static SiteNavigationMenuItem removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of site navigation menu items where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching site navigation menu items
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Caches the site navigation menu item in the entity cache if it is enabled.
	 *
	 * @param siteNavigationMenuItem the site navigation menu item
	 */
	public static void cacheResult(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		getPersistence().cacheResult(siteNavigationMenuItem);
	}

	/**
	 * Caches the site navigation menu items in the entity cache if it is enabled.
	 *
	 * @param siteNavigationMenuItems the site navigation menu items
	 */
	public static void cacheResult(
		List<SiteNavigationMenuItem> siteNavigationMenuItems) {

		getPersistence().cacheResult(siteNavigationMenuItems);
	}

	/**
	 * Creates a new site navigation menu item with the primary key. Does not add the site navigation menu item to the database.
	 *
	 * @param siteNavigationMenuItemId the primary key for the new site navigation menu item
	 * @return the new site navigation menu item
	 */
	public static SiteNavigationMenuItem create(long siteNavigationMenuItemId) {
		return getPersistence().create(siteNavigationMenuItemId);
	}

	/**
	 * Removes the site navigation menu item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param siteNavigationMenuItemId the primary key of the site navigation menu item
	 * @return the site navigation menu item that was removed
	 * @throws NoSuchMenuItemException if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem remove(long siteNavigationMenuItemId)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().remove(siteNavigationMenuItemId);
	}

	public static SiteNavigationMenuItem updateImpl(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		return getPersistence().updateImpl(siteNavigationMenuItem);
	}

	/**
	 * Returns the site navigation menu item with the primary key or throws a <code>NoSuchMenuItemException</code> if it could not be found.
	 *
	 * @param siteNavigationMenuItemId the primary key of the site navigation menu item
	 * @return the site navigation menu item
	 * @throws NoSuchMenuItemException if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem findByPrimaryKey(
			long siteNavigationMenuItemId)
		throws com.liferay.site.navigation.exception.NoSuchMenuItemException {

		return getPersistence().findByPrimaryKey(siteNavigationMenuItemId);
	}

	/**
	 * Returns the site navigation menu item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param siteNavigationMenuItemId the primary key of the site navigation menu item
	 * @return the site navigation menu item, or <code>null</code> if a site navigation menu item with the primary key could not be found
	 */
	public static SiteNavigationMenuItem fetchByPrimaryKey(
		long siteNavigationMenuItemId) {

		return getPersistence().fetchByPrimaryKey(siteNavigationMenuItemId);
	}

	/**
	 * Returns all the site navigation menu items.
	 *
	 * @return the site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the site navigation menu items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @return the range of site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findAll(
		int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the site navigation menu items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of site navigation menu items
	 */
	public static List<SiteNavigationMenuItem> findAll(
		int start, int end,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the site navigation menu items from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of site navigation menu items.
	 *
	 * @return the number of site navigation menu items
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static SiteNavigationMenuItemPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		SiteNavigationMenuItemPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile SiteNavigationMenuItemPersistence _persistence;

}