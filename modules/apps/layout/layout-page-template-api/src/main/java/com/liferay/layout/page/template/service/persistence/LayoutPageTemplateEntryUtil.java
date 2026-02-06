/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout page template entry service. This utility wraps <code>com.liferay.layout.page.template.service.persistence.impl.LayoutPageTemplateEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateEntryPersistence
 * @generated
 */
public class LayoutPageTemplateEntryUtil {

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
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		getPersistence().clearCache(layoutPageTemplateEntry);
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
	public static Map<Serializable, LayoutPageTemplateEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<LayoutPageTemplateEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<LayoutPageTemplateEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<LayoutPageTemplateEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutPageTemplateEntry update(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return getPersistence().update(layoutPageTemplateEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutPageTemplateEntry update(
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(layoutPageTemplateEntry, serviceContext);
	}

	/**
	 * Returns all the layout page template entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the layout page template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByUuid_First(
			String uuid,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByUuid_Last(
			String uuid,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByUuid_Last(
		String uuid,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where uuid = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByUuid_PrevAndNext(
			long layoutPageTemplateEntryId, String uuid,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByUuid_PrevAndNext(
			layoutPageTemplateEntryId, uuid, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of layout page template entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template entries
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the layout page template entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByUUID_G(
			String uuid, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout page template entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout page template entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the layout page template entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template entry that was removed
	 */
	public static LayoutPageTemplateEntry removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of layout page template entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template entries
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByUuid_C_PrevAndNext(
			long layoutPageTemplateEntryId, String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByUuid_C_PrevAndNext(
			layoutPageTemplateEntryId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template entries
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByGroupId_First(
			long groupId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByGroupId_First(
		long groupId,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByGroupId_Last(
			long groupId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByGroupId_Last(
		long groupId,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByGroupId_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByGroupId_PrevAndNext(
			layoutPageTemplateEntryId, groupId, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByGroupId(
		long groupId) {

		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByGroupId_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByGroupId_PrevAndNext(
			layoutPageTemplateEntryId, groupId, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByLayoutPrototypeId(
		long layoutPrototypeId) {

		return getPersistence().findByLayoutPrototypeId(layoutPrototypeId);
	}

	/**
	 * Returns a range of all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByLayoutPrototypeId(
		long layoutPrototypeId, int start, int end) {

		return getPersistence().findByLayoutPrototypeId(
			layoutPrototypeId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByLayoutPrototypeId(
		long layoutPrototypeId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByLayoutPrototypeId(
			layoutPrototypeId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByLayoutPrototypeId(
		long layoutPrototypeId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLayoutPrototypeId(
			layoutPrototypeId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByLayoutPrototypeId_First(
			long layoutPrototypeId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByLayoutPrototypeId_First(
			layoutPrototypeId, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByLayoutPrototypeId_First(
		long layoutPrototypeId,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByLayoutPrototypeId_First(
			layoutPrototypeId, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByLayoutPrototypeId_Last(
			long layoutPrototypeId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByLayoutPrototypeId_Last(
			layoutPrototypeId, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByLayoutPrototypeId_Last(
		long layoutPrototypeId,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByLayoutPrototypeId_Last(
			layoutPrototypeId, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param layoutPrototypeId the layout prototype ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByLayoutPrototypeId_PrevAndNext(
			long layoutPageTemplateEntryId, long layoutPrototypeId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByLayoutPrototypeId_PrevAndNext(
			layoutPageTemplateEntryId, layoutPrototypeId, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where layoutPrototypeId = &#63; from the database.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 */
	public static void removeByLayoutPrototypeId(long layoutPrototypeId) {
		getPersistence().removeByLayoutPrototypeId(layoutPrototypeId);
	}

	/**
	 * Returns the number of layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @return the number of matching layout page template entries
	 */
	public static int countByLayoutPrototypeId(long layoutPrototypeId) {
		return getPersistence().countByLayoutPrototypeId(layoutPrototypeId);
	}

	/**
	 * Returns the layout page template entry where plid = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param plid the plid
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByPlid(long plid)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByPlid(plid);
	}

	/**
	 * Returns the layout page template entry where plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param plid the plid
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByPlid(long plid) {
		return getPersistence().fetchByPlid(plid);
	}

	/**
	 * Returns the layout page template entry where plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByPlid(
		long plid, boolean useFinderCache) {

		return getPersistence().fetchByPlid(plid, useFinderCache);
	}

	/**
	 * Removes the layout page template entry where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 * @return the layout page template entry that was removed
	 */
	public static LayoutPageTemplateEntry removeByPlid(long plid)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().removeByPlid(plid);
	}

	/**
	 * Returns the number of layout page template entries where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout page template entries
	 */
	public static int countByPlid(long plid) {
		return getPersistence().countByPlid(plid);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L(
		long groupId, long layoutPageTemplateCollectionId) {

		return getPersistence().findByG_L(
			groupId, layoutPageTemplateCollectionId);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end) {

		return getPersistence().findByG_L(
			groupId, layoutPageTemplateCollectionId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_L(
			groupId, layoutPageTemplateCollectionId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_L(
			groupId, layoutPageTemplateCollectionId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_L_First(
			long groupId, long layoutPageTemplateCollectionId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_First(
			groupId, layoutPageTemplateCollectionId, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_L_First(
		long groupId, long layoutPageTemplateCollectionId,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_L_First(
			groupId, layoutPageTemplateCollectionId, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_L_Last(
			long groupId, long layoutPageTemplateCollectionId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_Last(
			groupId, layoutPageTemplateCollectionId, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_L_Last(
		long groupId, long layoutPageTemplateCollectionId,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_L_Last(
			groupId, layoutPageTemplateCollectionId, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_L_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_PrevAndNext(
			layoutPageTemplateEntryId, groupId, layoutPageTemplateCollectionId,
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L(
		long groupId, long layoutPageTemplateCollectionId) {

		return getPersistence().filterFindByG_L(
			groupId, layoutPageTemplateCollectionId);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end) {

		return getPersistence().filterFindByG_L(
			groupId, layoutPageTemplateCollectionId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_L(
			groupId, layoutPageTemplateCollectionId, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_L_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_L_PrevAndNext(
			layoutPageTemplateEntryId, groupId, layoutPageTemplateCollectionId,
			orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 */
	public static void removeByG_L(
		long groupId, long layoutPageTemplateCollectionId) {

		getPersistence().removeByG_L(groupId, layoutPageTemplateCollectionId);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_L(
		long groupId, long layoutPageTemplateCollectionId) {

		return getPersistence().countByG_L(
			groupId, layoutPageTemplateCollectionId);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_L(
		long groupId, long layoutPageTemplateCollectionId) {

		return getPersistence().filterCountByG_L(
			groupId, layoutPageTemplateCollectionId);
	}

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateEntryKey = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_LPTEK(
			long groupId, String layoutPageTemplateEntryKey)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_LPTEK(
			groupId, layoutPageTemplateEntryKey);
	}

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateEntryKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_LPTEK(
		long groupId, String layoutPageTemplateEntryKey) {

		return getPersistence().fetchByG_LPTEK(
			groupId, layoutPageTemplateEntryKey);
	}

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateEntryKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_LPTEK(
		long groupId, String layoutPageTemplateEntryKey,
		boolean useFinderCache) {

		return getPersistence().fetchByG_LPTEK(
			groupId, layoutPageTemplateEntryKey, useFinderCache);
	}

	/**
	 * Removes the layout page template entry where groupId = &#63; and layoutPageTemplateEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @return the layout page template entry that was removed
	 */
	public static LayoutPageTemplateEntry removeByG_LPTEK(
			long groupId, String layoutPageTemplateEntryKey)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().removeByG_LPTEK(
			groupId, layoutPageTemplateEntryKey);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_LPTEK(
		long groupId, String layoutPageTemplateEntryKey) {

		return getPersistence().countByG_LPTEK(
			groupId, layoutPageTemplateEntryKey);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_N(
		long groupId, String name) {

		return getPersistence().findByG_N(groupId, name);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_N(
		long groupId, String name, int start, int end) {

		return getPersistence().findByG_N(groupId, name, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_N(
			groupId, name, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_N(
			groupId, name, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_N_First(
			long groupId, String name,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_N_First(
			groupId, name, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_N_First(
		long groupId, String name,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_N_First(
			groupId, name, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_N_Last(
			long groupId, String name,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_N_Last(
			groupId, name, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_N_Last(
		long groupId, String name,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_N_Last(
			groupId, name, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_N_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_N_PrevAndNext(
			layoutPageTemplateEntryId, groupId, name, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_N(
		long groupId, String name) {

		return getPersistence().filterFindByG_N(groupId, name);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_N(
		long groupId, String name, int start, int end) {

		return getPersistence().filterFindByG_N(groupId, name, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_N(
			groupId, name, start, end, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and name = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_N_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_N_PrevAndNext(
			layoutPageTemplateEntryId, groupId, name, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	public static void removeByG_N(long groupId, String name) {
		getPersistence().removeByG_N(groupId, name);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_N(long groupId, String name) {
		return getPersistence().countByG_N(groupId, name);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_N(long groupId, String name) {
		return getPersistence().filterCountByG_N(groupId, name);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int type) {

		return getPersistence().findByG_T(groupId, type);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int type, int start, int end) {

		return getPersistence().findByG_T(groupId, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_T(
			groupId, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_T(
			groupId, type, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_T_First(
			long groupId, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_First(
			groupId, type, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_T_First(
		long groupId, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_T_First(
			groupId, type, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_T_Last(
			long groupId, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_Last(
			groupId, type, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_T_Last(
		long groupId, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_T_Last(
			groupId, type, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_PrevAndNext(
			layoutPageTemplateEntryId, groupId, type, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int type) {

		return getPersistence().filterFindByG_T(groupId, type);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int type, int start, int end) {

		return getPersistence().filterFindByG_T(groupId, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_T(
			groupId, type, start, end, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_T_PrevAndNext(
			layoutPageTemplateEntryId, groupId, type, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int[] types) {

		return getPersistence().filterFindByG_T(groupId, types);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int[] types, int start, int end) {

		return getPersistence().filterFindByG_T(groupId, types, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_T(
			groupId, types, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int[] types) {

		return getPersistence().findByG_T(groupId, types);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int[] types, int start, int end) {

		return getPersistence().findByG_T(groupId, types, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_T(
			groupId, types, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_T(
			groupId, types, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	public static void removeByG_T(long groupId, int type) {
		getPersistence().removeByG_T(groupId, type);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_T(long groupId, int type) {
		return getPersistence().countByG_T(groupId, type);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_T(long groupId, int[] types) {
		return getPersistence().countByG_T(groupId, types);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_T(long groupId, int type) {
		return getPersistence().filterCountByG_T(groupId, type);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_T(long groupId, int[] types) {
		return getPersistence().filterCountByG_T(groupId, types);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_S(
		long groupId, int status) {

		return getPersistence().findByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().findByG_S(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_S_First(
			long groupId, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_S_Last(
			long groupId, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_S_Last(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_S_Last(
		long groupId, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_S_Last(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, status, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_S(
		long groupId, int status) {

		return getPersistence().filterFindByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().filterFindByG_S(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, status, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public static void removeByG_S(long groupId, int status) {
		getPersistence().removeByG_S(groupId, status);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_S(long groupId, int status) {
		return getPersistence().countByG_S(groupId, status);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_S(long groupId, int status) {
		return getPersistence().filterCountByG_S(groupId, status);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name) {

		return getPersistence().findByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end) {

		return getPersistence().findByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_L_LikeN_First(
			long groupId, long layoutPageTemplateCollectionId, String name,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_LikeN_First(
			groupId, layoutPageTemplateCollectionId, name, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_L_LikeN_First(
		long groupId, long layoutPageTemplateCollectionId, String name,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_L_LikeN_First(
			groupId, layoutPageTemplateCollectionId, name, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_L_LikeN_Last(
			long groupId, long layoutPageTemplateCollectionId, String name,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_LikeN_Last(
			groupId, layoutPageTemplateCollectionId, name, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_L_LikeN_Last(
		long groupId, long layoutPageTemplateCollectionId, String name,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_L_LikeN_Last(
			groupId, layoutPageTemplateCollectionId, name, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_L_LikeN_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, String name,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_LikeN_PrevAndNext(
			layoutPageTemplateEntryId, groupId, layoutPageTemplateCollectionId,
			name, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name) {

		return getPersistence().filterFindByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end) {

		return getPersistence().filterFindByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_L_LikeN_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, String name,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_L_LikeN_PrevAndNext(
			layoutPageTemplateEntryId, groupId, layoutPageTemplateCollectionId,
			name, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 */
	public static void removeByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name) {

		getPersistence().removeByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name) {

		return getPersistence().countByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name) {

		return getPersistence().filterCountByG_L_LikeN(
			groupId, layoutPageTemplateCollectionId, name);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		return getPersistence().findByG_L_T(
			groupId, layoutPageTemplateCollectionId, type);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end) {

		return getPersistence().findByG_L_T(
			groupId, layoutPageTemplateCollectionId, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_L_T(
			groupId, layoutPageTemplateCollectionId, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_L_T(
			groupId, layoutPageTemplateCollectionId, type, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_L_T_First(
			long groupId, long layoutPageTemplateCollectionId, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_T_First(
			groupId, layoutPageTemplateCollectionId, type, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_L_T_First(
		long groupId, long layoutPageTemplateCollectionId, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_L_T_First(
			groupId, layoutPageTemplateCollectionId, type, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_L_T_Last(
			long groupId, long layoutPageTemplateCollectionId, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_T_Last(
			groupId, layoutPageTemplateCollectionId, type, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_L_T_Last(
		long groupId, long layoutPageTemplateCollectionId, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_L_T_Last(
			groupId, layoutPageTemplateCollectionId, type, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_L_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_T_PrevAndNext(
			layoutPageTemplateEntryId, groupId, layoutPageTemplateCollectionId,
			type, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		return getPersistence().filterFindByG_L_T(
			groupId, layoutPageTemplateCollectionId, type);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end) {

		return getPersistence().filterFindByG_L_T(
			groupId, layoutPageTemplateCollectionId, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_L_T(
			groupId, layoutPageTemplateCollectionId, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_L_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_L_T_PrevAndNext(
			layoutPageTemplateEntryId, groupId, layoutPageTemplateCollectionId,
			type, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 */
	public static void removeByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		getPersistence().removeByG_L_T(
			groupId, layoutPageTemplateCollectionId, type);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		return getPersistence().countByG_L_T(
			groupId, layoutPageTemplateCollectionId, type);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		return getPersistence().filterCountByG_L_T(
			groupId, layoutPageTemplateCollectionId, type);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		return getPersistence().findByG_L_S(
			groupId, layoutPageTemplateCollectionId, status);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end) {

		return getPersistence().findByG_L_S(
			groupId, layoutPageTemplateCollectionId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_L_S(
			groupId, layoutPageTemplateCollectionId, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_L_S(
			groupId, layoutPageTemplateCollectionId, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_L_S_First(
			long groupId, long layoutPageTemplateCollectionId, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_S_First(
			groupId, layoutPageTemplateCollectionId, status, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_L_S_First(
		long groupId, long layoutPageTemplateCollectionId, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_L_S_First(
			groupId, layoutPageTemplateCollectionId, status, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_L_S_Last(
			long groupId, long layoutPageTemplateCollectionId, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_S_Last(
			groupId, layoutPageTemplateCollectionId, status, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_L_S_Last(
		long groupId, long layoutPageTemplateCollectionId, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_L_S_Last(
			groupId, layoutPageTemplateCollectionId, status, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_L_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, layoutPageTemplateCollectionId,
			status, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		return getPersistence().filterFindByG_L_S(
			groupId, layoutPageTemplateCollectionId, status);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end) {

		return getPersistence().filterFindByG_L_S(
			groupId, layoutPageTemplateCollectionId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_L_S(
			groupId, layoutPageTemplateCollectionId, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_L_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_L_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, layoutPageTemplateCollectionId,
			status, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 */
	public static void removeByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		getPersistence().removeByG_L_S(
			groupId, layoutPageTemplateCollectionId, status);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		return getPersistence().countByG_L_S(
			groupId, layoutPageTemplateCollectionId, status);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		return getPersistence().filterCountByG_L_S(
			groupId, layoutPageTemplateCollectionId, status);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_N_T(
		long groupId, String name, int type) {

		return getPersistence().findByG_N_T(groupId, name, type);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_N_T(
		long groupId, String name, int type, int start, int end) {

		return getPersistence().findByG_N_T(groupId, name, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_N_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_N_T(
			groupId, name, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_N_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_N_T(
			groupId, name, type, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_N_T_First(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_N_T_First(
			groupId, name, type, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_N_T_First(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_N_T_First(
			groupId, name, type, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_N_T_Last(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_N_T_Last(
			groupId, name, type, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_N_T_Last(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_N_T_Last(
			groupId, name, type, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_N_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_N_T_PrevAndNext(
			layoutPageTemplateEntryId, groupId, name, type, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_N_T(
		long groupId, String name, int type) {

		return getPersistence().filterFindByG_N_T(groupId, name, type);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_N_T(
		long groupId, String name, int type, int start, int end) {

		return getPersistence().filterFindByG_N_T(
			groupId, name, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_N_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_N_T(
			groupId, name, type, start, end, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_N_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_N_T_PrevAndNext(
			layoutPageTemplateEntryId, groupId, name, type, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	public static void removeByG_N_T(long groupId, String name, int type) {
		getPersistence().removeByG_N_T(groupId, name, type);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_N_T(long groupId, String name, int type) {
		return getPersistence().countByG_N_T(groupId, name, type);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_N_T(long groupId, String name, int type) {
		return getPersistence().filterCountByG_N_T(groupId, name, type);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int type) {

		return getPersistence().findByG_T_LikeN(groupId, name, type);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int type, int start, int end) {

		return getPersistence().findByG_T_LikeN(
			groupId, name, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_T_LikeN(
			groupId, name, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_T_LikeN(
			groupId, name, type, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_T_LikeN_First(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_LikeN_First(
			groupId, name, type, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_T_LikeN_First(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_T_LikeN_First(
			groupId, name, type, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_T_LikeN_Last(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_LikeN_Last(
			groupId, name, type, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_T_LikeN_Last(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_T_LikeN_Last(
			groupId, name, type, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_T_LikeN_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_LikeN_PrevAndNext(
			layoutPageTemplateEntryId, groupId, name, type, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int type) {

		return getPersistence().filterFindByG_T_LikeN(groupId, name, type);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int type, int start, int end) {

		return getPersistence().filterFindByG_T_LikeN(
			groupId, name, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_T_LikeN(
			groupId, name, type, start, end, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_T_LikeN_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_T_LikeN_PrevAndNext(
			layoutPageTemplateEntryId, groupId, name, type, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int[] types) {

		return getPersistence().filterFindByG_T_LikeN(groupId, name, types);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end) {

		return getPersistence().filterFindByG_T_LikeN(
			groupId, name, types, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_T_LikeN(
			groupId, name, types, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int[] types) {

		return getPersistence().findByG_T_LikeN(groupId, name, types);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end) {

		return getPersistence().findByG_T_LikeN(
			groupId, name, types, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_T_LikeN(
			groupId, name, types, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_T_LikeN(
			groupId, name, types, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	public static void removeByG_T_LikeN(long groupId, String name, int type) {
		getPersistence().removeByG_T_LikeN(groupId, name, type);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_T_LikeN(long groupId, String name, int type) {
		return getPersistence().countByG_T_LikeN(groupId, name, type);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_T_LikeN(long groupId, String name, int[] types) {
		return getPersistence().countByG_T_LikeN(groupId, name, types);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_T_LikeN(
		long groupId, String name, int type) {

		return getPersistence().filterCountByG_T_LikeN(groupId, name, type);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_T_LikeN(
		long groupId, String name, int[] types) {

		return getPersistence().filterCountByG_T_LikeN(groupId, name, types);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int type, int status) {

		return getPersistence().findByG_T_S(groupId, type, status);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int type, int status, int start, int end) {

		return getPersistence().findByG_T_S(groupId, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_T_S(
			groupId, type, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_T_S(
			groupId, type, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_T_S_First(
			long groupId, int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_S_First(
			groupId, type, status, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_T_S_First(
		long groupId, int type, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_T_S_First(
			groupId, type, status, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_T_S_Last(
			long groupId, int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_S_Last(
			groupId, type, status, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_T_S_Last(
		long groupId, int type, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_T_S_Last(
			groupId, type, status, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_T_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, type, status,
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int type, int status) {

		return getPersistence().filterFindByG_T_S(groupId, type, status);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int type, int status, int start, int end) {

		return getPersistence().filterFindByG_T_S(
			groupId, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_T_S(
			groupId, type, status, start, end, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_T_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_T_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, type, status,
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int[] types, int status) {

		return getPersistence().filterFindByG_T_S(groupId, types, status);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int[] types, int status, int start, int end) {

		return getPersistence().filterFindByG_T_S(
			groupId, types, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_T_S(
			groupId, types, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int[] types, int status) {

		return getPersistence().findByG_T_S(groupId, types, status);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int[] types, int status, int start, int end) {

		return getPersistence().findByG_T_S(groupId, types, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_T_S(
			groupId, types, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_T_S(
			groupId, types, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 */
	public static void removeByG_T_S(long groupId, int type, int status) {
		getPersistence().removeByG_T_S(groupId, type, status);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_T_S(long groupId, int type, int status) {
		return getPersistence().countByG_T_S(groupId, type, status);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_T_S(long groupId, int[] types, int status) {
		return getPersistence().countByG_T_S(groupId, types, status);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_T_S(long groupId, int type, int status) {
		return getPersistence().filterCountByG_T_S(groupId, type, status);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_T_S(
		long groupId, int[] types, int status) {

		return getPersistence().filterCountByG_T_S(groupId, types, status);
	}

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_L_N_T(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int type)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_N_T(
			groupId, layoutPageTemplateCollectionId, name, type);
	}

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_L_N_T(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type) {

		return getPersistence().fetchByG_L_N_T(
			groupId, layoutPageTemplateCollectionId, name, type);
	}

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_L_N_T(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type, boolean useFinderCache) {

		return getPersistence().fetchByG_L_N_T(
			groupId, layoutPageTemplateCollectionId, name, type,
			useFinderCache);
	}

	/**
	 * Removes the layout page template entry where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the layout page template entry that was removed
	 */
	public static LayoutPageTemplateEntry removeByG_L_N_T(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int type)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().removeByG_L_N_T(
			groupId, layoutPageTemplateCollectionId, name, type);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_L_N_T(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type) {

		return getPersistence().countByG_L_N_T(
			groupId, layoutPageTemplateCollectionId, name, type);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status) {

		return getPersistence().findByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end) {

		return getPersistence().findByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_L_LikeN_S_First(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_LikeN_S_First(
			groupId, layoutPageTemplateCollectionId, name, status,
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_L_LikeN_S_First(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_L_LikeN_S_First(
			groupId, layoutPageTemplateCollectionId, name, status,
			orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_L_LikeN_S_Last(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_LikeN_S_Last(
			groupId, layoutPageTemplateCollectionId, name, status,
			orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_L_LikeN_S_Last(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_L_LikeN_S_Last(
			groupId, layoutPageTemplateCollectionId, name, status,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_L_LikeN_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, String name, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_L_LikeN_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, layoutPageTemplateCollectionId,
			name, status, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status) {

		return getPersistence().filterFindByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end) {

		return getPersistence().filterFindByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_L_LikeN_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, String name, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_L_LikeN_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, layoutPageTemplateCollectionId,
			name, status, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 */
	public static void removeByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status) {

		getPersistence().removeByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status) {

		return getPersistence().countByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status) {

		return getPersistence().filterCountByG_L_LikeN_S(
			groupId, layoutPageTemplateCollectionId, name, status);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type) {

		return getPersistence().findByG_C_C_T(
			groupId, classNameId, classTypeKey, type);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end) {

		return getPersistence().findByG_C_C_T(
			groupId, classNameId, classTypeKey, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_C_C_T(
			groupId, classNameId, classTypeKey, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C_T(
			groupId, classNameId, classTypeKey, type, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_C_T_First(
			long groupId, long classNameId, String classTypeKey, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_T_First(
			groupId, classNameId, classTypeKey, type, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_C_T_First(
		long groupId, long classNameId, String classTypeKey, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_C_T_First(
			groupId, classNameId, classTypeKey, type, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_C_T_Last(
			long groupId, long classNameId, String classTypeKey, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_T_Last(
			groupId, classNameId, classTypeKey, type, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_C_T_Last(
		long groupId, long classNameId, String classTypeKey, int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_C_T_Last(
			groupId, classNameId, classTypeKey, type, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_C_C_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_T_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, classTypeKey, type,
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type) {

		return getPersistence().filterFindByG_C_C_T(
			groupId, classNameId, classTypeKey, type);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end) {

		return getPersistence().filterFindByG_C_C_T(
			groupId, classNameId, classTypeKey, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_C_C_T(
			groupId, classNameId, classTypeKey, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_C_C_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_C_C_T_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, classTypeKey, type,
			orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 */
	public static void removeByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type) {

		getPersistence().removeByG_C_C_T(
			groupId, classNameId, classTypeKey, type);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type) {

		return getPersistence().countByG_C_C_T(
			groupId, classNameId, classTypeKey, type);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type) {

		return getPersistence().filterCountByG_C_C_T(
			groupId, classNameId, classTypeKey, type);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate) {

		return getPersistence().findByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end) {

		return getPersistence().findByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_C_D_First(
			long groupId, long classNameId, String classTypeKey,
			boolean defaultTemplate,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_D_First(
			groupId, classNameId, classTypeKey, defaultTemplate,
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_C_D_First(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_C_D_First(
			groupId, classNameId, classTypeKey, defaultTemplate,
			orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_C_D_Last(
			long groupId, long classNameId, String classTypeKey,
			boolean defaultTemplate,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_D_Last(
			groupId, classNameId, classTypeKey, defaultTemplate,
			orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_C_D_Last(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_C_D_Last(
			groupId, classNameId, classTypeKey, defaultTemplate,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_C_C_D_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, boolean defaultTemplate,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_D_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, classTypeKey,
			defaultTemplate, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate) {

		return getPersistence().filterFindByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end) {

		return getPersistence().filterFindByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_C_C_D_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, boolean defaultTemplate,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_C_C_D_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, classTypeKey,
			defaultTemplate, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 */
	public static void removeByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate) {

		getPersistence().removeByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate) {

		return getPersistence().countByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate) {

		return getPersistence().filterCountByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate) {

		return getPersistence().findByG_C_T_D(
			groupId, classNameId, type, defaultTemplate);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end) {

		return getPersistence().findByG_C_T_D(
			groupId, classNameId, type, defaultTemplate, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_C_T_D(
			groupId, classNameId, type, defaultTemplate, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_T_D(
			groupId, classNameId, type, defaultTemplate, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_T_D_First(
			long groupId, long classNameId, int type, boolean defaultTemplate,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_T_D_First(
			groupId, classNameId, type, defaultTemplate, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_T_D_First(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_T_D_First(
			groupId, classNameId, type, defaultTemplate, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_T_D_Last(
			long groupId, long classNameId, int type, boolean defaultTemplate,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_T_D_Last(
			groupId, classNameId, type, defaultTemplate, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_T_D_Last(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_T_D_Last(
			groupId, classNameId, type, defaultTemplate, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_C_T_D_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			int type, boolean defaultTemplate,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_T_D_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, type,
			defaultTemplate, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate) {

		return getPersistence().filterFindByG_C_T_D(
			groupId, classNameId, type, defaultTemplate);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end) {

		return getPersistence().filterFindByG_C_T_D(
			groupId, classNameId, type, defaultTemplate, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_C_T_D(
			groupId, classNameId, type, defaultTemplate, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_C_T_D_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			int type, boolean defaultTemplate,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_C_T_D_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, type,
			defaultTemplate, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 */
	public static void removeByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate) {

		getPersistence().removeByG_C_T_D(
			groupId, classNameId, type, defaultTemplate);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate) {

		return getPersistence().countByG_C_T_D(
			groupId, classNameId, type, defaultTemplate);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate) {

		return getPersistence().filterCountByG_C_T_D(
			groupId, classNameId, type, defaultTemplate);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int type, int status) {

		return getPersistence().findByG_T_LikeN_S(groupId, name, type, status);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end) {

		return getPersistence().findByG_T_LikeN_S(
			groupId, name, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_T_LikeN_S(
			groupId, name, type, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_T_LikeN_S(
			groupId, name, type, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_T_LikeN_S_First(
			long groupId, String name, int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_LikeN_S_First(
			groupId, name, type, status, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_T_LikeN_S_First(
		long groupId, String name, int type, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_T_LikeN_S_First(
			groupId, name, type, status, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_T_LikeN_S_Last(
			long groupId, String name, int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_LikeN_S_Last(
			groupId, name, type, status, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_T_LikeN_S_Last(
		long groupId, String name, int type, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_T_LikeN_S_Last(
			groupId, name, type, status, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_T_LikeN_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name, int type,
			int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_LikeN_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, name, type, status,
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int type, int status) {

		return getPersistence().filterFindByG_T_LikeN_S(
			groupId, name, type, status);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end) {

		return getPersistence().filterFindByG_T_LikeN_S(
			groupId, name, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_T_LikeN_S(
			groupId, name, type, status, start, end, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_T_LikeN_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name, int type,
			int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_T_LikeN_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, name, type, status,
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int[] types, int status) {

		return getPersistence().filterFindByG_T_LikeN_S(
			groupId, name, types, status);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start,
		int end) {

		return getPersistence().filterFindByG_T_LikeN_S(
			groupId, name, types, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_T_LikeN_S(
			groupId, name, types, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int[] types, int status) {

		return getPersistence().findByG_T_LikeN_S(groupId, name, types, status);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start,
		int end) {

		return getPersistence().findByG_T_LikeN_S(
			groupId, name, types, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_T_LikeN_S(
			groupId, name, types, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_T_LikeN_S(
			groupId, name, types, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 */
	public static void removeByG_T_LikeN_S(
		long groupId, String name, int type, int status) {

		getPersistence().removeByG_T_LikeN_S(groupId, name, type, status);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_T_LikeN_S(
		long groupId, String name, int type, int status) {

		return getPersistence().countByG_T_LikeN_S(groupId, name, type, status);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_T_LikeN_S(
		long groupId, String name, int[] types, int status) {

		return getPersistence().countByG_T_LikeN_S(
			groupId, name, types, status);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_T_LikeN_S(
		long groupId, String name, int type, int status) {

		return getPersistence().filterCountByG_T_LikeN_S(
			groupId, name, type, status);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_T_LikeN_S(
		long groupId, String name, int[] types, int status) {

		return getPersistence().filterCountByG_T_LikeN_S(
			groupId, name, types, status);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status) {

		return getPersistence().findByG_T_D_S(
			groupId, type, defaultTemplate, status);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end) {

		return getPersistence().findByG_T_D_S(
			groupId, type, defaultTemplate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_T_D_S(
			groupId, type, defaultTemplate, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_T_D_S(
			groupId, type, defaultTemplate, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_T_D_S_First(
			long groupId, int type, boolean defaultTemplate, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_D_S_First(
			groupId, type, defaultTemplate, status, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_T_D_S_First(
		long groupId, int type, boolean defaultTemplate, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_T_D_S_First(
			groupId, type, defaultTemplate, status, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_T_D_S_Last(
			long groupId, int type, boolean defaultTemplate, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_D_S_Last(
			groupId, type, defaultTemplate, status, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_T_D_S_Last(
		long groupId, int type, boolean defaultTemplate, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_T_D_S_Last(
			groupId, type, defaultTemplate, status, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_T_D_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int type,
			boolean defaultTemplate, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_T_D_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, type, defaultTemplate, status,
			orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status) {

		return getPersistence().filterFindByG_T_D_S(
			groupId, type, defaultTemplate, status);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end) {

		return getPersistence().filterFindByG_T_D_S(
			groupId, type, defaultTemplate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_T_D_S(
			groupId, type, defaultTemplate, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_T_D_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int type,
			boolean defaultTemplate, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_T_D_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, type, defaultTemplate, status,
			orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 */
	public static void removeByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status) {

		getPersistence().removeByG_T_D_S(
			groupId, type, defaultTemplate, status);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status) {

		return getPersistence().countByG_T_D_S(
			groupId, type, defaultTemplate, status);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status) {

		return getPersistence().filterCountByG_T_D_S(
			groupId, type, defaultTemplate, status);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type) {

		return getPersistence().findByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end) {

		return getPersistence().findByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_C_LikeN_T_First(
			long groupId, long classNameId, String classTypeKey, String name,
			int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_LikeN_T_First(
			groupId, classNameId, classTypeKey, name, type, orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_C_LikeN_T_First(
		long groupId, long classNameId, String classTypeKey, String name,
		int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_C_LikeN_T_First(
			groupId, classNameId, classTypeKey, name, type, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_C_LikeN_T_Last(
			long groupId, long classNameId, String classTypeKey, String name,
			int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_LikeN_T_Last(
			groupId, classNameId, classTypeKey, name, type, orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_C_LikeN_T_Last(
		long groupId, long classNameId, String classTypeKey, String name,
		int type,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_C_LikeN_T_Last(
			groupId, classNameId, classTypeKey, name, type, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_C_C_LikeN_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, String name, int type,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_LikeN_T_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, classTypeKey, name,
			type, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type) {

		return getPersistence().filterFindByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end) {

		return getPersistence().filterFindByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[]
			filterFindByG_C_C_LikeN_T_PrevAndNext(
				long layoutPageTemplateEntryId, long groupId, long classNameId,
				String classTypeKey, String name, int type,
				OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_C_C_LikeN_T_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, classTypeKey, name,
			type, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 */
	public static void removeByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type) {

		getPersistence().removeByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type) {

		return getPersistence().countByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type) {

		return getPersistence().filterCountByG_C_C_LikeN_T(
			groupId, classNameId, classTypeKey, name, type);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status) {

		return getPersistence().findByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end) {

		return getPersistence().findByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_C_T_S_First(
			long groupId, long classNameId, String classTypeKey, int type,
			int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_T_S_First(
			groupId, classNameId, classTypeKey, type, status,
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_C_T_S_First(
		long groupId, long classNameId, String classTypeKey, int type,
		int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_C_T_S_First(
			groupId, classNameId, classTypeKey, type, status,
			orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_C_T_S_Last(
			long groupId, long classNameId, String classTypeKey, int type,
			int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_T_S_Last(
			groupId, classNameId, classTypeKey, type, status,
			orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_C_T_S_Last(
		long groupId, long classNameId, String classTypeKey, int type,
		int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_C_T_S_Last(
			groupId, classNameId, classTypeKey, type, status,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_C_C_T_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_T_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, classTypeKey, type,
			status, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status) {

		return getPersistence().filterFindByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end) {

		return getPersistence().filterFindByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_C_C_T_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_C_C_T_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, classTypeKey, type,
			status, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 */
	public static void removeByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status) {

		getPersistence().removeByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status) {

		return getPersistence().countByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status) {

		return getPersistence().filterCountByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status) {

		return getPersistence().findByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end) {

		return getPersistence().findByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status, start,
			end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status, start,
			end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_C_D_S_First(
			long groupId, long classNameId, String classTypeKey,
			boolean defaultTemplate, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_D_S_First(
			groupId, classNameId, classTypeKey, defaultTemplate, status,
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_C_D_S_First(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_C_D_S_First(
			groupId, classNameId, classTypeKey, defaultTemplate, status,
			orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_C_D_S_Last(
			long groupId, long classNameId, String classTypeKey,
			boolean defaultTemplate, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_D_S_Last(
			groupId, classNameId, classTypeKey, defaultTemplate, status,
			orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_C_D_S_Last(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_C_D_S_Last(
			groupId, classNameId, classTypeKey, defaultTemplate, status,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_C_C_D_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, boolean defaultTemplate, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_D_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, classTypeKey,
			defaultTemplate, status, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status) {

		return getPersistence().filterFindByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end) {

		return getPersistence().filterFindByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status, start,
			end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status, start,
			end, orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] filterFindByG_C_C_D_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, boolean defaultTemplate, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_C_C_D_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, classTypeKey,
			defaultTemplate, status, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 */
	public static void removeByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status) {

		getPersistence().removeByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status) {

		return getPersistence().countByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status) {

		return getPersistence().filterCountByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status) {

		return getPersistence().findByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end) {

		return getPersistence().findByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_C_LikeN_T_S_First(
			long groupId, long classNameId, String classTypeKey, String name,
			int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_LikeN_T_S_First(
			groupId, classNameId, classTypeKey, name, type, status,
			orderByComparator);
	}

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_C_LikeN_T_S_First(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_C_LikeN_T_S_First(
			groupId, classNameId, classTypeKey, name, type, status,
			orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByG_C_C_LikeN_T_S_Last(
			long groupId, long classNameId, String classTypeKey, String name,
			int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_LikeN_T_S_Last(
			groupId, classNameId, classTypeKey, name, type, status,
			orderByComparator);
	}

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByG_C_C_LikeN_T_S_Last(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().fetchByG_C_C_LikeN_T_S_Last(
			groupId, classNameId, classTypeKey, name, type, status,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[] findByG_C_C_LikeN_T_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, String name, int type, int status,
			OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByG_C_C_LikeN_T_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, classTypeKey, name,
			type, status, orderByComparator);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status) {

		return getPersistence().filterFindByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end) {

		return getPersistence().filterFindByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public static List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().filterFindByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry[]
			filterFindByG_C_C_LikeN_T_S_PrevAndNext(
				long layoutPageTemplateEntryId, long groupId, long classNameId,
				String classTypeKey, String name, int type, int status,
				OrderByComparator<LayoutPageTemplateEntry> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().filterFindByG_C_C_LikeN_T_S_PrevAndNext(
			layoutPageTemplateEntryId, groupId, classNameId, classTypeKey, name,
			type, status, orderByComparator);
	}

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 */
	public static void removeByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status) {

		getPersistence().removeByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status);
	}

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public static int countByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status) {

		return getPersistence().countByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status);
	}

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public static int filterCountByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status) {

		return getPersistence().filterCountByG_C_C_LikeN_T_S(
			groupId, classNameId, classTypeKey, name, type, status);
	}

	/**
	 * Returns the layout page template entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout page template entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout page template entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public static LayoutPageTemplateEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the layout page template entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template entry that was removed
	 */
	public static LayoutPageTemplateEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of layout page template entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template entries
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Caches the layout page template entry in the entity cache if it is enabled.
	 *
	 * @param layoutPageTemplateEntry the layout page template entry
	 */
	public static void cacheResult(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		getPersistence().cacheResult(layoutPageTemplateEntry);
	}

	/**
	 * Caches the layout page template entries in the entity cache if it is enabled.
	 *
	 * @param layoutPageTemplateEntries the layout page template entries
	 */
	public static void cacheResult(
		List<LayoutPageTemplateEntry> layoutPageTemplateEntries) {

		getPersistence().cacheResult(layoutPageTemplateEntries);
	}

	/**
	 * Creates a new layout page template entry with the primary key. Does not add the layout page template entry to the database.
	 *
	 * @param layoutPageTemplateEntryId the primary key for the new layout page template entry
	 * @return the new layout page template entry
	 */
	public static LayoutPageTemplateEntry create(
		long layoutPageTemplateEntryId) {

		return getPersistence().create(layoutPageTemplateEntryId);
	}

	/**
	 * Removes the layout page template entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the layout page template entry
	 * @return the layout page template entry that was removed
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry remove(long layoutPageTemplateEntryId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().remove(layoutPageTemplateEntryId);
	}

	public static LayoutPageTemplateEntry updateImpl(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return getPersistence().updateImpl(layoutPageTemplateEntry);
	}

	/**
	 * Returns the layout page template entry with the primary key or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the layout page template entry
	 * @return the layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry findByPrimaryKey(
			long layoutPageTemplateEntryId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateEntryException {

		return getPersistence().findByPrimaryKey(layoutPageTemplateEntryId);
	}

	/**
	 * Returns the layout page template entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the layout page template entry
	 * @return the layout page template entry, or <code>null</code> if a layout page template entry with the primary key could not be found
	 */
	public static LayoutPageTemplateEntry fetchByPrimaryKey(
		long layoutPageTemplateEntryId) {

		return getPersistence().fetchByPrimaryKey(layoutPageTemplateEntryId);
	}

	/**
	 * Returns all the layout page template entries.
	 *
	 * @return the layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the layout page template entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findAll(
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of layout page template entries
	 */
	public static List<LayoutPageTemplateEntry> findAll(
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layout page template entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of layout page template entries.
	 *
	 * @return the number of layout page template entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static LayoutPageTemplateEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		LayoutPageTemplateEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile LayoutPageTemplateEntryPersistence _persistence;

}