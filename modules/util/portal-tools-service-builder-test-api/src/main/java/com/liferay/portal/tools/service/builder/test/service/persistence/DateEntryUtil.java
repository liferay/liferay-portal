/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.DateEntry;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the date entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.DateEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DateEntryPersistence
 * @generated
 */
public class DateEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<DateEntry> dateEntries) {
		getPersistence().cacheResult(dateEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(DateEntry dateEntry) {
		getPersistence().cacheResult(dateEntry);
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
	public static void clearCache(DateEntry dateEntry) {
		getPersistence().clearCache(dateEntry);
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
	public static Map<Serializable, DateEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DateEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DateEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DateEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DateEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DateEntry update(DateEntry dateEntry) {
		return getPersistence().update(dateEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DateEntry update(
		DateEntry dateEntry, ServiceContext serviceContext) {

		return getPersistence().update(dateEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the date entries where snapshotDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param snapshotDate the snapshot date
	 * @param start the lower bound of the range of date entries
	 * @param end the upper bound of the range of date entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching date entries
	 */
	public static List<DateEntry> findBySnapshotDate(
		Date snapshotDate, int start, int end,
		OrderByComparator<DateEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findBySnapshotDate(
			snapshotDate, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first date entry in the ordered set where snapshotDate = &#63;.
	 *
	 * @param snapshotDate the snapshot date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching date entry
	 * @throws NoSuchDateEntryException if a matching date entry could not be found
	 */
	public static DateEntry findBySnapshotDate_First(
			Date snapshotDate, OrderByComparator<DateEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchDateEntryException {

		return getPersistence().findBySnapshotDate_First(
			snapshotDate, orderByComparator);
	}

	/**
	 * Returns the first date entry in the ordered set where snapshotDate = &#63;.
	 *
	 * @param snapshotDate the snapshot date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching date entry, or <code>null</code> if a matching date entry could not be found
	 */
	public static DateEntry fetchBySnapshotDate_First(
		Date snapshotDate, OrderByComparator<DateEntry> orderByComparator) {

		return getPersistence().fetchBySnapshotDate_First(
			snapshotDate, orderByComparator);
	}

	/**
	 * Removes all the date entries where snapshotDate = &#63; from the database.
	 *
	 * @param snapshotDate the snapshot date
	 */
	public static void removeBySnapshotDate(Date snapshotDate) {
		getPersistence().removeBySnapshotDate(snapshotDate);
	}

	/**
	 * Returns the number of date entries where snapshotDate = &#63;.
	 *
	 * @param snapshotDate the snapshot date
	 * @return the number of matching date entries
	 */
	public static int countBySnapshotDate(Date snapshotDate) {
		return getPersistence().countBySnapshotDate(snapshotDate);
	}

	/**
	 * Returns the date entry where companyId = &#63; and snapshotDate = &#63; or throws a <code>NoSuchDateEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @return the matching date entry
	 * @throws NoSuchDateEntryException if a matching date entry could not be found
	 */
	public static DateEntry findByC_S(long companyId, Date snapshotDate)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchDateEntryException {

		return getPersistence().findByC_S(companyId, snapshotDate);
	}

	/**
	 * Returns the date entry where companyId = &#63; and snapshotDate = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching date entry, or <code>null</code> if a matching date entry could not be found
	 */
	public static DateEntry fetchByC_S(
		long companyId, Date snapshotDate, boolean useFinderCache) {

		return getPersistence().fetchByC_S(
			companyId, snapshotDate, useFinderCache);
	}

	/**
	 * Removes the date entry where companyId = &#63; and snapshotDate = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @return the date entry that was removed
	 */
	public static DateEntry removeByC_S(long companyId, Date snapshotDate)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchDateEntryException {

		return getPersistence().removeByC_S(companyId, snapshotDate);
	}

	/**
	 * Returns the number of date entries where companyId = &#63; and snapshotDate = &#63;.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @return the number of matching date entries
	 */
	public static int countByC_S(long companyId, Date snapshotDate) {
		return getPersistence().countByC_S(companyId, snapshotDate);
	}

	/**
	 * Creates a new date entry with the primary key. Does not add the date entry to the database.
	 *
	 * @param dateEntryId the primary key for the new date entry
	 * @return the new date entry
	 */
	public static DateEntry create(long dateEntryId) {
		return getPersistence().create(dateEntryId);
	}

	/**
	 * Removes the date entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dateEntryId the primary key of the date entry
	 * @return the date entry that was removed
	 * @throws NoSuchDateEntryException if a date entry with the primary key could not be found
	 */
	public static DateEntry remove(long dateEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchDateEntryException {

		return getPersistence().remove(dateEntryId);
	}

	public static DateEntry updateImpl(DateEntry dateEntry) {
		return getPersistence().updateImpl(dateEntry);
	}

	/**
	 * Returns the date entry with the primary key or throws a <code>NoSuchDateEntryException</code> if it could not be found.
	 *
	 * @param dateEntryId the primary key of the date entry
	 * @return the date entry
	 * @throws NoSuchDateEntryException if a date entry with the primary key could not be found
	 */
	public static DateEntry findByPrimaryKey(long dateEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchDateEntryException {

		return getPersistence().findByPrimaryKey(dateEntryId);
	}

	/**
	 * Returns the date entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dateEntryId the primary key of the date entry
	 * @return the date entry, or <code>null</code> if a date entry with the primary key could not be found
	 */
	public static DateEntry fetchByPrimaryKey(long dateEntryId) {
		return getPersistence().fetchByPrimaryKey(dateEntryId);
	}

	/**
	 * Returns the date entry where companyId = &#63; and snapshotDate = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param snapshotDate the snapshot date
	 * @return the matching date entry, or <code>null</code> if a matching date entry could not be found
	 */
	public static DateEntry fetchByC_S(long companyId, Date snapshotDate) {
		return getPersistence().fetchByC_S(companyId, snapshotDate);
	}

	/**
	 * Returns all the date entries where snapshotDate = &#63;.
	 *
	 * @param snapshotDate the snapshot date
	 * @return the matching date entries
	 */
	public static List<DateEntry> findBySnapshotDate(Date snapshotDate) {
		return getPersistence().findBySnapshotDate(snapshotDate);
	}

	/**
	 * Returns a range of all the date entries where snapshotDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param snapshotDate the snapshot date
	 * @param start the lower bound of the range of date entries
	 * @param end the upper bound of the range of date entries (not inclusive)
	 * @return the range of matching date entries
	 */
	public static List<DateEntry> findBySnapshotDate(
		Date snapshotDate, int start, int end) {

		return getPersistence().findBySnapshotDate(snapshotDate, start, end);
	}

	/**
	 * Returns an ordered range of all the date entries where snapshotDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.DateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param snapshotDate the snapshot date
	 * @param start the lower bound of the range of date entries
	 * @param end the upper bound of the range of date entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching date entries
	 */
	public static List<DateEntry> findBySnapshotDate(
		Date snapshotDate, int start, int end,
		OrderByComparator<DateEntry> orderByComparator) {

		return getPersistence().findBySnapshotDate(
			snapshotDate, start, end, orderByComparator);
	}

	public static DateEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(DateEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile DateEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-496945811