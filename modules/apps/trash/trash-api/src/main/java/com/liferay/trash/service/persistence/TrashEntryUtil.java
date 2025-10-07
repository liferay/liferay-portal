/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.trash.model.TrashEntry;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the trash entry service. This utility wraps <code>com.liferay.trash.service.persistence.impl.TrashEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see TrashEntryPersistence
 * @generated
 */
public class TrashEntryUtil {

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
	public static void clearCache(TrashEntry trashEntry) {
		getPersistence().clearCache(trashEntry);
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
	public static Map<Serializable, TrashEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<TrashEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<TrashEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<TrashEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static TrashEntry update(TrashEntry trashEntry) {
		return getPersistence().update(trashEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static TrashEntry update(
		TrashEntry trashEntry, ServiceContext serviceContext) {

		return getPersistence().update(trashEntry, serviceContext);
	}

	/**
	 * Returns all the trash entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching trash entries
	 */
	public static List<TrashEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the trash entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	public static List<TrashEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	public static List<TrashEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	public static List<TrashEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public static TrashEntry findByGroupId_First(
			long groupId, OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public static TrashEntry fetchByGroupId_First(
		long groupId, OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public static TrashEntry findByGroupId_Last(
			long groupId, OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public static TrashEntry fetchByGroupId_Last(
		long groupId, OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_Last(groupId, orderByComparator);
	}

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public static TrashEntry[] findByGroupId_PrevAndNext(
			long entryId, long groupId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByGroupId_PrevAndNext(
			entryId, groupId, orderByComparator);
	}

	/**
	 * Removes all the trash entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of trash entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching trash entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns all the trash entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching trash entries
	 */
	public static List<TrashEntry> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the trash entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	public static List<TrashEntry> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	public static List<TrashEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	public static List<TrashEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public static TrashEntry findByCompanyId_First(
			long companyId, OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public static TrashEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public static TrashEntry findByCompanyId_Last(
			long companyId, OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public static TrashEntry fetchByCompanyId_Last(
		long companyId, OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public static TrashEntry[] findByCompanyId_PrevAndNext(
			long entryId, long companyId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByCompanyId_PrevAndNext(
			entryId, companyId, orderByComparator);
	}

	/**
	 * Removes all the trash entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of trash entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching trash entries
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @return the matching trash entries
	 */
	public static List<TrashEntry> findByG_LtCD(long groupId, Date createDate) {
		return getPersistence().findByG_LtCD(groupId, createDate);
	}

	/**
	 * Returns a range of all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	public static List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate, int start, int end) {

		return getPersistence().findByG_LtCD(groupId, createDate, start, end);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	public static List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().findByG_LtCD(
			groupId, createDate, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	public static List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LtCD(
			groupId, createDate, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public static TrashEntry findByG_LtCD_First(
			long groupId, Date createDate,
			OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByG_LtCD_First(
			groupId, createDate, orderByComparator);
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public static TrashEntry fetchByG_LtCD_First(
		long groupId, Date createDate,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().fetchByG_LtCD_First(
			groupId, createDate, orderByComparator);
	}

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public static TrashEntry findByG_LtCD_Last(
			long groupId, Date createDate,
			OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByG_LtCD_Last(
			groupId, createDate, orderByComparator);
	}

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public static TrashEntry fetchByG_LtCD_Last(
		long groupId, Date createDate,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().fetchByG_LtCD_Last(
			groupId, createDate, orderByComparator);
	}

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public static TrashEntry[] findByG_LtCD_PrevAndNext(
			long entryId, long groupId, Date createDate,
			OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByG_LtCD_PrevAndNext(
			entryId, groupId, createDate, orderByComparator);
	}

	/**
	 * Removes all the trash entries where groupId = &#63; and createDate &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 */
	public static void removeByG_LtCD(long groupId, Date createDate) {
		getPersistence().removeByG_LtCD(groupId, createDate);
	}

	/**
	 * Returns the number of trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @return the number of matching trash entries
	 */
	public static int countByG_LtCD(long groupId, Date createDate) {
		return getPersistence().countByG_LtCD(groupId, createDate);
	}

	/**
	 * Returns all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching trash entries
	 */
	public static List<TrashEntry> findByG_CN(long groupId, long classNameId) {
		return getPersistence().findByG_CN(groupId, classNameId);
	}

	/**
	 * Returns a range of all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	public static List<TrashEntry> findByG_CN(
		long groupId, long classNameId, int start, int end) {

		return getPersistence().findByG_CN(groupId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	public static List<TrashEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().findByG_CN(
			groupId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	public static List<TrashEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_CN(
			groupId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public static TrashEntry findByG_CN_First(
			long groupId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByG_CN_First(
			groupId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public static TrashEntry fetchByG_CN_First(
		long groupId, long classNameId,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().fetchByG_CN_First(
			groupId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public static TrashEntry findByG_CN_Last(
			long groupId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByG_CN_Last(
			groupId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public static TrashEntry fetchByG_CN_Last(
		long groupId, long classNameId,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().fetchByG_CN_Last(
			groupId, classNameId, orderByComparator);
	}

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public static TrashEntry[] findByG_CN_PrevAndNext(
			long entryId, long groupId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByG_CN_PrevAndNext(
			entryId, groupId, classNameId, orderByComparator);
	}

	/**
	 * Removes all the trash entries where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	public static void removeByG_CN(long groupId, long classNameId) {
		getPersistence().removeByG_CN(groupId, classNameId);
	}

	/**
	 * Returns the number of trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching trash entries
	 */
	public static int countByG_CN(long groupId, long classNameId) {
		return getPersistence().countByG_CN(groupId, classNameId);
	}

	/**
	 * Returns all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching trash entries
	 */
	public static List<TrashEntry> findByC_CN(
		long companyId, long classNameId) {

		return getPersistence().findByC_CN(companyId, classNameId);
	}

	/**
	 * Returns a range of all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	public static List<TrashEntry> findByC_CN(
		long companyId, long classNameId, int start, int end) {

		return getPersistence().findByC_CN(companyId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	public static List<TrashEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().findByC_CN(
			companyId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	public static List<TrashEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_CN(
			companyId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public static TrashEntry findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByC_CN_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public static TrashEntry fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().fetchByC_CN_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public static TrashEntry findByC_CN_Last(
			long companyId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByC_CN_Last(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public static TrashEntry fetchByC_CN_Last(
		long companyId, long classNameId,
		OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().fetchByC_CN_Last(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the trash entries before and after the current trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param entryId the primary key of the current trash entry
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public static TrashEntry[] findByC_CN_PrevAndNext(
			long entryId, long companyId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByC_CN_PrevAndNext(
			entryId, companyId, classNameId, orderByComparator);
	}

	/**
	 * Removes all the trash entries where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public static void removeByC_CN(long companyId, long classNameId) {
		getPersistence().removeByC_CN(companyId, classNameId);
	}

	/**
	 * Returns the number of trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching trash entries
	 */
	public static int countByC_CN(long companyId, long classNameId) {
		return getPersistence().countByC_CN(companyId, classNameId);
	}

	/**
	 * Returns the trash entry where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	public static TrashEntry findByCN_CPK(long classNameId, long classPK)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByCN_CPK(classNameId, classPK);
	}

	/**
	 * Returns the trash entry where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public static TrashEntry fetchByCN_CPK(long classNameId, long classPK) {
		return getPersistence().fetchByCN_CPK(classNameId, classPK);
	}

	/**
	 * Returns the trash entry where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	public static TrashEntry fetchByCN_CPK(
		long classNameId, long classPK, boolean useFinderCache) {

		return getPersistence().fetchByCN_CPK(
			classNameId, classPK, useFinderCache);
	}

	/**
	 * Removes the trash entry where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the trash entry that was removed
	 */
	public static TrashEntry removeByCN_CPK(long classNameId, long classPK)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().removeByCN_CPK(classNameId, classPK);
	}

	/**
	 * Returns the number of trash entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching trash entries
	 */
	public static int countByCN_CPK(long classNameId, long classPK) {
		return getPersistence().countByCN_CPK(classNameId, classPK);
	}

	/**
	 * Caches the trash entry in the entity cache if it is enabled.
	 *
	 * @param trashEntry the trash entry
	 */
	public static void cacheResult(TrashEntry trashEntry) {
		getPersistence().cacheResult(trashEntry);
	}

	/**
	 * Caches the trash entries in the entity cache if it is enabled.
	 *
	 * @param trashEntries the trash entries
	 */
	public static void cacheResult(List<TrashEntry> trashEntries) {
		getPersistence().cacheResult(trashEntries);
	}

	/**
	 * Creates a new trash entry with the primary key. Does not add the trash entry to the database.
	 *
	 * @param entryId the primary key for the new trash entry
	 * @return the new trash entry
	 */
	public static TrashEntry create(long entryId) {
		return getPersistence().create(entryId);
	}

	/**
	 * Removes the trash entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry that was removed
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public static TrashEntry remove(long entryId)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().remove(entryId);
	}

	public static TrashEntry updateImpl(TrashEntry trashEntry) {
		return getPersistence().updateImpl(trashEntry);
	}

	/**
	 * Returns the trash entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	public static TrashEntry findByPrimaryKey(long entryId)
		throws com.liferay.trash.exception.NoSuchEntryException {

		return getPersistence().findByPrimaryKey(entryId);
	}

	/**
	 * Returns the trash entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry, or <code>null</code> if a trash entry with the primary key could not be found
	 */
	public static TrashEntry fetchByPrimaryKey(long entryId) {
		return getPersistence().fetchByPrimaryKey(entryId);
	}

	/**
	 * Returns all the trash entries.
	 *
	 * @return the trash entries
	 */
	public static List<TrashEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the trash entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of trash entries
	 */
	public static List<TrashEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the trash entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of trash entries
	 */
	public static List<TrashEntry> findAll(
		int start, int end, OrderByComparator<TrashEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the trash entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of trash entries
	 */
	public static List<TrashEntry> findAll(
		int start, int end, OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the trash entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of trash entries.
	 *
	 * @return the number of trash entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static TrashEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(TrashEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile TrashEntryPersistence _persistence;

}