/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.CompoundPKEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the compound pk entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.CompoundPKEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CompoundPKEntryPersistence
 * @generated
 */
public class CompoundPKEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<CompoundPKEntry> compoundPKEntries) {
		getPersistence().cacheResult(compoundPKEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(CompoundPKEntry compoundPKEntry) {
		getPersistence().cacheResult(compoundPKEntry);
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
	public static void clearCache(CompoundPKEntry compoundPKEntry) {
		getPersistence().clearCache(compoundPKEntry);
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
	public static Map<Serializable, CompoundPKEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CompoundPKEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CompoundPKEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CompoundPKEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CompoundPKEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CompoundPKEntry update(CompoundPKEntry compoundPKEntry) {
		return getPersistence().update(compoundPKEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CompoundPKEntry update(
		CompoundPKEntry compoundPKEntry, ServiceContext serviceContext) {

		return getPersistence().update(compoundPKEntry, serviceContext);
	}

	/**
	 * Creates a new compound pk entry with the primary key. Does not add the compound pk entry to the database.
	 *
	 * @param compoundPKEntryPK the primary key for the new compound pk entry
	 * @return the new compound pk entry
	 */
	public static CompoundPKEntry create(CompoundPKEntryPK compoundPKEntryPK) {
		return getPersistence().create(compoundPKEntryPK);
	}

	/**
	 * Removes the compound pk entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param compoundPKEntryPK the primary key of the compound pk entry
	 * @return the compound pk entry that was removed
	 * @throws NoSuchCompoundPKEntryException if a compound pk entry with the primary key could not be found
	 */
	public static CompoundPKEntry remove(CompoundPKEntryPK compoundPKEntryPK)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchCompoundPKEntryException {

		return getPersistence().remove(compoundPKEntryPK);
	}

	public static CompoundPKEntry updateImpl(CompoundPKEntry compoundPKEntry) {
		return getPersistence().updateImpl(compoundPKEntry);
	}

	/**
	 * Returns the compound pk entry with the primary key or throws a <code>NoSuchCompoundPKEntryException</code> if it could not be found.
	 *
	 * @param compoundPKEntryPK the primary key of the compound pk entry
	 * @return the compound pk entry
	 * @throws NoSuchCompoundPKEntryException if a compound pk entry with the primary key could not be found
	 */
	public static CompoundPKEntry findByPrimaryKey(
			CompoundPKEntryPK compoundPKEntryPK)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchCompoundPKEntryException {

		return getPersistence().findByPrimaryKey(compoundPKEntryPK);
	}

	/**
	 * Returns the compound pk entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param compoundPKEntryPK the primary key of the compound pk entry
	 * @return the compound pk entry, or <code>null</code> if a compound pk entry with the primary key could not be found
	 */
	public static CompoundPKEntry fetchByPrimaryKey(
		CompoundPKEntryPK compoundPKEntryPK) {

		return getPersistence().fetchByPrimaryKey(compoundPKEntryPK);
	}

	public static Set<String> getCompoundPKColumnNames() {
		return getPersistence().getCompoundPKColumnNames();
	}

	public static CompoundPKEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(CompoundPKEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile CompoundPKEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1808300466