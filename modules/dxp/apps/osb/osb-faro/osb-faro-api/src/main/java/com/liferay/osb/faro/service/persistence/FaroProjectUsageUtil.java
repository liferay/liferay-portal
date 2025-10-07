/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence;

import com.liferay.osb.faro.model.FaroProjectUsage;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the faro project usage service. This utility wraps <code>com.liferay.osb.faro.service.persistence.impl.FaroProjectUsagePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @see FaroProjectUsagePersistence
 * @generated
 */
public class FaroProjectUsageUtil {

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
	public static void clearCache(FaroProjectUsage faroProjectUsage) {
		getPersistence().clearCache(faroProjectUsage);
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
	public static Map<Serializable, FaroProjectUsage> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FaroProjectUsage> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FaroProjectUsage> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FaroProjectUsage> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FaroProjectUsage> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FaroProjectUsage update(FaroProjectUsage faroProjectUsage) {
		return getPersistence().update(faroProjectUsage);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FaroProjectUsage update(
		FaroProjectUsage faroProjectUsage, ServiceContext serviceContext) {

		return getPersistence().update(faroProjectUsage, serviceContext);
	}

	/**
	 * Returns the faro project usage where faroProjectId = &#63; and usageTime = &#63; or throws a <code>NoSuchFaroProjectUsageException</code> if it could not be found.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro project usage
	 * @throws NoSuchFaroProjectUsageException if a matching faro project usage could not be found
	 */
	public static FaroProjectUsage findByF_U(long faroProjectId, long usageTime)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectUsageException {

		return getPersistence().findByF_U(faroProjectId, usageTime);
	}

	/**
	 * Returns the faro project usage where faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the matching faro project usage, or <code>null</code> if a matching faro project usage could not be found
	 */
	public static FaroProjectUsage fetchByF_U(
		long faroProjectId, long usageTime) {

		return getPersistence().fetchByF_U(faroProjectId, usageTime);
	}

	/**
	 * Returns the faro project usage where faroProjectId = &#63; and usageTime = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro project usage, or <code>null</code> if a matching faro project usage could not be found
	 */
	public static FaroProjectUsage fetchByF_U(
		long faroProjectId, long usageTime, boolean useFinderCache) {

		return getPersistence().fetchByF_U(
			faroProjectId, usageTime, useFinderCache);
	}

	/**
	 * Removes the faro project usage where faroProjectId = &#63; and usageTime = &#63; from the database.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the faro project usage that was removed
	 */
	public static FaroProjectUsage removeByF_U(
			long faroProjectId, long usageTime)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectUsageException {

		return getPersistence().removeByF_U(faroProjectId, usageTime);
	}

	/**
	 * Returns the number of faro project usages where faroProjectId = &#63; and usageTime = &#63;.
	 *
	 * @param faroProjectId the faro project ID
	 * @param usageTime the usage time
	 * @return the number of matching faro project usages
	 */
	public static int countByF_U(long faroProjectId, long usageTime) {
		return getPersistence().countByF_U(faroProjectId, usageTime);
	}

	/**
	 * Caches the faro project usage in the entity cache if it is enabled.
	 *
	 * @param faroProjectUsage the faro project usage
	 */
	public static void cacheResult(FaroProjectUsage faroProjectUsage) {
		getPersistence().cacheResult(faroProjectUsage);
	}

	/**
	 * Caches the faro project usages in the entity cache if it is enabled.
	 *
	 * @param faroProjectUsages the faro project usages
	 */
	public static void cacheResult(List<FaroProjectUsage> faroProjectUsages) {
		getPersistence().cacheResult(faroProjectUsages);
	}

	/**
	 * Creates a new faro project usage with the primary key. Does not add the faro project usage to the database.
	 *
	 * @param faroProjectUsageId the primary key for the new faro project usage
	 * @return the new faro project usage
	 */
	public static FaroProjectUsage create(long faroProjectUsageId) {
		return getPersistence().create(faroProjectUsageId);
	}

	/**
	 * Removes the faro project usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroProjectUsageId the primary key of the faro project usage
	 * @return the faro project usage that was removed
	 * @throws NoSuchFaroProjectUsageException if a faro project usage with the primary key could not be found
	 */
	public static FaroProjectUsage remove(long faroProjectUsageId)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectUsageException {

		return getPersistence().remove(faroProjectUsageId);
	}

	public static FaroProjectUsage updateImpl(
		FaroProjectUsage faroProjectUsage) {

		return getPersistence().updateImpl(faroProjectUsage);
	}

	/**
	 * Returns the faro project usage with the primary key or throws a <code>NoSuchFaroProjectUsageException</code> if it could not be found.
	 *
	 * @param faroProjectUsageId the primary key of the faro project usage
	 * @return the faro project usage
	 * @throws NoSuchFaroProjectUsageException if a faro project usage with the primary key could not be found
	 */
	public static FaroProjectUsage findByPrimaryKey(long faroProjectUsageId)
		throws com.liferay.osb.faro.exception.NoSuchFaroProjectUsageException {

		return getPersistence().findByPrimaryKey(faroProjectUsageId);
	}

	/**
	 * Returns the faro project usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroProjectUsageId the primary key of the faro project usage
	 * @return the faro project usage, or <code>null</code> if a faro project usage with the primary key could not be found
	 */
	public static FaroProjectUsage fetchByPrimaryKey(long faroProjectUsageId) {
		return getPersistence().fetchByPrimaryKey(faroProjectUsageId);
	}

	/**
	 * Returns all the faro project usages.
	 *
	 * @return the faro project usages
	 */
	public static List<FaroProjectUsage> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the faro project usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro project usages
	 * @param end the upper bound of the range of faro project usages (not inclusive)
	 * @return the range of faro project usages
	 */
	public static List<FaroProjectUsage> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the faro project usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro project usages
	 * @param end the upper bound of the range of faro project usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of faro project usages
	 */
	public static List<FaroProjectUsage> findAll(
		int start, int end,
		OrderByComparator<FaroProjectUsage> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the faro project usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroProjectUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro project usages
	 * @param end the upper bound of the range of faro project usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of faro project usages
	 */
	public static List<FaroProjectUsage> findAll(
		int start, int end,
		OrderByComparator<FaroProjectUsage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the faro project usages from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of faro project usages.
	 *
	 * @return the number of faro project usages
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static FaroProjectUsagePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(FaroProjectUsagePersistence persistence) {
		_persistence = persistence;
	}

	private static volatile FaroProjectUsagePersistence _persistence;

}