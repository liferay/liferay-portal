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
	 * Returns an ordered range of all the compound pk entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of compound pk entries
	 * @param end the upper bound of the range of compound pk entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching compound pk entries
	 */
	public static List<CompoundPKEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<CompoundPKEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_CN(
			companyId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first compound pk entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching compound pk entry
	 * @throws NoSuchCompoundPKEntryException if a matching compound pk entry could not be found
	 */
	public static CompoundPKEntry findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<CompoundPKEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchCompoundPKEntryException {

		return getPersistence().findByC_CN_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first compound pk entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching compound pk entry, or <code>null</code> if a matching compound pk entry could not be found
	 */
	public static CompoundPKEntry fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<CompoundPKEntry> orderByComparator) {

		return getPersistence().fetchByC_CN_First(
			companyId, classNameId, orderByComparator);
	}

	/**
	 * Removes all the compound pk entries where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public static void removeByC_CN(long companyId, long classNameId) {
		getPersistence().removeByC_CN(companyId, classNameId);
	}

	/**
	 * Returns the number of compound pk entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching compound pk entries
	 */
	public static int countByC_CN(long companyId, long classNameId) {
		return getPersistence().countByC_CN(companyId, classNameId);
	}

	/**
	 * Returns the compound pk entry where companyId = &#63; and name = &#63; or throws a <code>NoSuchCompoundPKEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching compound pk entry
	 * @throws NoSuchCompoundPKEntryException if a matching compound pk entry could not be found
	 */
	public static CompoundPKEntry findByC_N(long companyId, String name)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchCompoundPKEntryException {

		return getPersistence().findByC_N(companyId, name);
	}

	/**
	 * Returns the compound pk entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching compound pk entry, or <code>null</code> if a matching compound pk entry could not be found
	 */
	public static CompoundPKEntry fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return getPersistence().fetchByC_N(companyId, name, useFinderCache);
	}

	/**
	 * Removes the compound pk entry where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the compound pk entry that was removed
	 */
	public static CompoundPKEntry removeByC_N(long companyId, String name)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchCompoundPKEntryException {

		return getPersistence().removeByC_N(companyId, name);
	}

	/**
	 * Returns the number of compound pk entries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching compound pk entries
	 */
	public static int countByC_N(long companyId, String name) {
		return getPersistence().countByC_N(companyId, name);
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

	/**
	 * Returns the compound pk entry where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching compound pk entry, or <code>null</code> if a matching compound pk entry could not be found
	 */
	public static CompoundPKEntry fetchByC_N(long companyId, String name) {
		return getPersistence().fetchByC_N(companyId, name);
	}

	/**
	 * Returns all the compound pk entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching compound pk entries
	 */
	public static List<CompoundPKEntry> findByC_CN(
		long companyId, long classNameId) {

		return getPersistence().findByC_CN(companyId, classNameId);
	}

	/**
	 * Returns a range of all the compound pk entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of compound pk entries
	 * @param end the upper bound of the range of compound pk entries (not inclusive)
	 * @return the range of matching compound pk entries
	 */
	public static List<CompoundPKEntry> findByC_CN(
		long companyId, long classNameId, int start, int end) {

		return getPersistence().findByC_CN(companyId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the compound pk entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.CompoundPKEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of compound pk entries
	 * @param end the upper bound of the range of compound pk entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching compound pk entries
	 */
	public static List<CompoundPKEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<CompoundPKEntry> orderByComparator) {

		return getPersistence().findByC_CN(
			companyId, classNameId, start, end, orderByComparator);
	}

	public static CompoundPKEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(CompoundPKEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile CompoundPKEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1075216952