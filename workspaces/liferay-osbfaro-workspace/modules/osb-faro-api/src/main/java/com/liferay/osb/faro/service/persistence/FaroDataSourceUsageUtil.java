/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence;

import com.liferay.osb.faro.model.FaroDataSourceUsage;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the faro data source usage service. This utility wraps <code>com.liferay.osb.faro.service.persistence.impl.FaroDataSourceUsagePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @see FaroDataSourceUsagePersistence
 * @generated
 */
public class FaroDataSourceUsageUtil {

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
	public static void clearCache(FaroDataSourceUsage faroDataSourceUsage) {
		getPersistence().clearCache(faroDataSourceUsage);
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
	public static Map<Serializable, FaroDataSourceUsage> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FaroDataSourceUsage> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FaroDataSourceUsage> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FaroDataSourceUsage> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FaroDataSourceUsage> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FaroDataSourceUsage update(
		FaroDataSourceUsage faroDataSourceUsage) {

		return getPersistence().update(faroDataSourceUsage);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FaroDataSourceUsage update(
		FaroDataSourceUsage faroDataSourceUsage,
		ServiceContext serviceContext) {

		return getPersistence().update(faroDataSourceUsage, serviceContext);
	}

	/**
	 * Returns the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; or throws a <code>NoSuchFaroDataSourceUsageException</code> if it could not be found.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro data source usage
	 * @throws NoSuchFaroDataSourceUsageException if a matching faro data source usage could not be found
	 */
	public static FaroDataSourceUsage findByF_D_U(
			long dataSourceId, long faroProjectId, long usageTime)
		throws com.liferay.osb.faro.exception.
			NoSuchFaroDataSourceUsageException {

		return getPersistence().findByF_D_U(
			dataSourceId, faroProjectId, usageTime);
	}

	/**
	 * Returns the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro data source usage, or <code>null</code> if a matching faro data source usage could not be found
	 */
	public static FaroDataSourceUsage fetchByF_D_U(
		long dataSourceId, long faroProjectId, long usageTime) {

		return getPersistence().fetchByF_D_U(
			dataSourceId, faroProjectId, usageTime);
	}

	/**
	 * Returns the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro data source usage, or <code>null</code> if a matching faro data source usage could not be found
	 */
	public static FaroDataSourceUsage fetchByF_D_U(
		long dataSourceId, long faroProjectId, long usageTime,
		boolean useFinderCache) {

		return getPersistence().fetchByF_D_U(
			dataSourceId, faroProjectId, usageTime, useFinderCache);
	}

	/**
	 * Removes the faro data source usage where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63; from the database.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the faro data source usage that was removed
	 */
	public static FaroDataSourceUsage removeByF_D_U(
			long dataSourceId, long faroProjectId, long usageTime)
		throws com.liferay.osb.faro.exception.
			NoSuchFaroDataSourceUsageException {

		return getPersistence().removeByF_D_U(
			dataSourceId, faroProjectId, usageTime);
	}

	/**
	 * Returns the number of faro data source usages where dataSourceId = &#63; and faroProjectId = &#63; and usageTime = &#63;.
	 *
	 * @param dataSourceId the data source ID
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the number of matching faro data source usages
	 */
	public static int countByF_D_U(
		long dataSourceId, long faroProjectId, long usageTime) {

		return getPersistence().countByF_D_U(
			dataSourceId, faroProjectId, usageTime);
	}

	/**
	 * Caches the faro data source usage in the entity cache if it is enabled.
	 *
	 * @param faroDataSourceUsage the faro data source usage
	 */
	public static void cacheResult(FaroDataSourceUsage faroDataSourceUsage) {
		getPersistence().cacheResult(faroDataSourceUsage);
	}

	/**
	 * Caches the faro data source usages in the entity cache if it is enabled.
	 *
	 * @param faroDataSourceUsages the faro data source usages
	 */
	public static void cacheResult(
		List<FaroDataSourceUsage> faroDataSourceUsages) {

		getPersistence().cacheResult(faroDataSourceUsages);
	}

	/**
	 * Creates a new faro data source usage with the primary key. Does not add the faro data source usage to the database.
	 *
	 * @param faroDataSourceUsageId the primary key for the new faro data source usage
	 * @return the new faro data source usage
	 */
	public static FaroDataSourceUsage create(long faroDataSourceUsageId) {
		return getPersistence().create(faroDataSourceUsageId);
	}

	/**
	 * Removes the faro data source usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroDataSourceUsageId the primary key of the faro data source usage
	 * @return the faro data source usage that was removed
	 * @throws NoSuchFaroDataSourceUsageException if a faro data source usage with the primary key could not be found
	 */
	public static FaroDataSourceUsage remove(long faroDataSourceUsageId)
		throws com.liferay.osb.faro.exception.
			NoSuchFaroDataSourceUsageException {

		return getPersistence().remove(faroDataSourceUsageId);
	}

	public static FaroDataSourceUsage updateImpl(
		FaroDataSourceUsage faroDataSourceUsage) {

		return getPersistence().updateImpl(faroDataSourceUsage);
	}

	/**
	 * Returns the faro data source usage with the primary key or throws a <code>NoSuchFaroDataSourceUsageException</code> if it could not be found.
	 *
	 * @param faroDataSourceUsageId the primary key of the faro data source usage
	 * @return the faro data source usage
	 * @throws NoSuchFaroDataSourceUsageException if a faro data source usage with the primary key could not be found
	 */
	public static FaroDataSourceUsage findByPrimaryKey(
			long faroDataSourceUsageId)
		throws com.liferay.osb.faro.exception.
			NoSuchFaroDataSourceUsageException {

		return getPersistence().findByPrimaryKey(faroDataSourceUsageId);
	}

	/**
	 * Returns the faro data source usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroDataSourceUsageId the primary key of the faro data source usage
	 * @return the faro data source usage, or <code>null</code> if a faro data source usage with the primary key could not be found
	 */
	public static FaroDataSourceUsage fetchByPrimaryKey(
		long faroDataSourceUsageId) {

		return getPersistence().fetchByPrimaryKey(faroDataSourceUsageId);
	}

	/**
	 * Returns all the faro data source usages.
	 *
	 * @return the faro data source usages
	 */
	public static List<FaroDataSourceUsage> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the faro data source usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroDataSourceUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro data source usages
	 * @param end the upper bound of the range of faro data source usages (not inclusive)
	 * @return the range of faro data source usages
	 */
	public static List<FaroDataSourceUsage> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the faro data source usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroDataSourceUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro data source usages
	 * @param end the upper bound of the range of faro data source usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of faro data source usages
	 */
	public static List<FaroDataSourceUsage> findAll(
		int start, int end,
		OrderByComparator<FaroDataSourceUsage> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the faro data source usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroDataSourceUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro data source usages
	 * @param end the upper bound of the range of faro data source usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of faro data source usages
	 */
	public static List<FaroDataSourceUsage> findAll(
		int start, int end,
		OrderByComparator<FaroDataSourceUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the faro data source usages from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of faro data source usages.
	 *
	 * @return the number of faro data source usages
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static FaroDataSourceUsagePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		FaroDataSourceUsagePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile FaroDataSourceUsagePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:738474325