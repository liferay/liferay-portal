/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.ClobEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the clob entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.ClobEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ClobEntryPersistence
 * @generated
 */
public class ClobEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<ClobEntry> clobEntries) {
		getPersistence().cacheResult(clobEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(ClobEntry clobEntry) {
		getPersistence().cacheResult(clobEntry);
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
	public static void clearCache(ClobEntry clobEntry) {
		getPersistence().clearCache(clobEntry);
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
	public static Map<Serializable, ClobEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ClobEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ClobEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ClobEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ClobEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ClobEntry update(ClobEntry clobEntry) {
		return getPersistence().update(clobEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ClobEntry update(
		ClobEntry clobEntry, ServiceContext serviceContext) {

		return getPersistence().update(clobEntry, serviceContext);
	}

	/**
	 * Creates a new clob entry with the primary key. Does not add the clob entry to the database.
	 *
	 * @param clobEntryId the primary key for the new clob entry
	 * @return the new clob entry
	 */
	public static ClobEntry create(long clobEntryId) {
		return getPersistence().create(clobEntryId);
	}

	/**
	 * Removes the clob entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param clobEntryId the primary key of the clob entry
	 * @return the clob entry that was removed
	 * @throws NoSuchClobEntryException if a clob entry with the primary key could not be found
	 */
	public static ClobEntry remove(long clobEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchClobEntryException {

		return getPersistence().remove(clobEntryId);
	}

	public static ClobEntry updateImpl(ClobEntry clobEntry) {
		return getPersistence().updateImpl(clobEntry);
	}

	/**
	 * Returns the clob entry with the primary key or throws a <code>NoSuchClobEntryException</code> if it could not be found.
	 *
	 * @param clobEntryId the primary key of the clob entry
	 * @return the clob entry
	 * @throws NoSuchClobEntryException if a clob entry with the primary key could not be found
	 */
	public static ClobEntry findByPrimaryKey(long clobEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchClobEntryException {

		return getPersistence().findByPrimaryKey(clobEntryId);
	}

	/**
	 * Returns the clob entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param clobEntryId the primary key of the clob entry
	 * @return the clob entry, or <code>null</code> if a clob entry with the primary key could not be found
	 */
	public static ClobEntry fetchByPrimaryKey(long clobEntryId) {
		return getPersistence().fetchByPrimaryKey(clobEntryId);
	}

	public static ClobEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(ClobEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile ClobEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1581648673