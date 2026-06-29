/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.ColumnNameEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the column name entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.ColumnNameEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ColumnNameEntryPersistence
 * @generated
 */
public class ColumnNameEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<ColumnNameEntry> columnNameEntries) {
		getPersistence().cacheResult(columnNameEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(ColumnNameEntry columnNameEntry) {
		getPersistence().cacheResult(columnNameEntry);
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
	public static void clearCache(ColumnNameEntry columnNameEntry) {
		getPersistence().clearCache(columnNameEntry);
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
	public static Map<Serializable, ColumnNameEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ColumnNameEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ColumnNameEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ColumnNameEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ColumnNameEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ColumnNameEntry update(ColumnNameEntry columnNameEntry) {
		return getPersistence().update(columnNameEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ColumnNameEntry update(
		ColumnNameEntry columnNameEntry, ServiceContext serviceContext) {

		return getPersistence().update(columnNameEntry, serviceContext);
	}

	/**
	 * Creates a new column name entry with the primary key. Does not add the column name entry to the database.
	 *
	 * @param columnNameEntryId the primary key for the new column name entry
	 * @return the new column name entry
	 */
	public static ColumnNameEntry create(long columnNameEntryId) {
		return getPersistence().create(columnNameEntryId);
	}

	/**
	 * Removes the column name entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param columnNameEntryId the primary key of the column name entry
	 * @return the column name entry that was removed
	 * @throws NoSuchColumnNameEntryException if a column name entry with the primary key could not be found
	 */
	public static ColumnNameEntry remove(long columnNameEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchColumnNameEntryException {

		return getPersistence().remove(columnNameEntryId);
	}

	public static ColumnNameEntry updateImpl(ColumnNameEntry columnNameEntry) {
		return getPersistence().updateImpl(columnNameEntry);
	}

	/**
	 * Returns the column name entry with the primary key or throws a <code>NoSuchColumnNameEntryException</code> if it could not be found.
	 *
	 * @param columnNameEntryId the primary key of the column name entry
	 * @return the column name entry
	 * @throws NoSuchColumnNameEntryException if a column name entry with the primary key could not be found
	 */
	public static ColumnNameEntry findByPrimaryKey(long columnNameEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchColumnNameEntryException {

		return getPersistence().findByPrimaryKey(columnNameEntryId);
	}

	/**
	 * Returns the column name entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param columnNameEntryId the primary key of the column name entry
	 * @return the column name entry, or <code>null</code> if a column name entry with the primary key could not be found
	 */
	public static ColumnNameEntry fetchByPrimaryKey(long columnNameEntryId) {
		return getPersistence().fetchByPrimaryKey(columnNameEntryId);
	}

	public static ColumnNameEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(ColumnNameEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile ColumnNameEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1250385432