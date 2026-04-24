/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the production readiness ignore service. This utility wraps <code>com.liferay.production.readiness.ignore.service.persistence.impl.ProductionReadinessIgnorePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ProductionReadinessIgnorePersistence
 * @generated
 */
public class ProductionReadinessIgnoreUtil {

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
		ProductionReadinessIgnore productionReadinessIgnore) {

		getPersistence().clearCache(productionReadinessIgnore);
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
	public static Map<Serializable, ProductionReadinessIgnore>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ProductionReadinessIgnore> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ProductionReadinessIgnore> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ProductionReadinessIgnore> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ProductionReadinessIgnore update(
		ProductionReadinessIgnore productionReadinessIgnore) {

		return getPersistence().update(productionReadinessIgnore);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ProductionReadinessIgnore update(
		ProductionReadinessIgnore productionReadinessIgnore,
		ServiceContext serviceContext) {

		return getPersistence().update(
			productionReadinessIgnore, serviceContext);
	}

	/**
	 * Returns all the production readiness ignores where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching production readiness ignores
	 */
	public static List<ProductionReadinessIgnore> findByCompanyId(
		long companyId) {

		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the production readiness ignores where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @return the range of matching production readiness ignores
	 */
	public static List<ProductionReadinessIgnore> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the production readiness ignores where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching production readiness ignores
	 */
	public static List<ProductionReadinessIgnore> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the production readiness ignores where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching production readiness ignores
	 */
	public static List<ProductionReadinessIgnore> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first production readiness ignore in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a matching production readiness ignore could not be found
	 */
	public static ProductionReadinessIgnore findByCompanyId_First(
			long companyId,
			OrderByComparator<ProductionReadinessIgnore> orderByComparator)
		throws com.liferay.production.readiness.ignore.exception.
			NoSuchProductionReadinessIgnoreException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first production readiness ignore in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching production readiness ignore, or <code>null</code> if a matching production readiness ignore could not be found
	 */
	public static ProductionReadinessIgnore fetchByCompanyId_First(
		long companyId,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the production readiness ignores where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of production readiness ignores where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching production readiness ignores
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns the production readiness ignore where companyId = &#63; and ruleKey = &#63; or throws a <code>NoSuchProductionReadinessIgnoreException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a matching production readiness ignore could not be found
	 */
	public static ProductionReadinessIgnore findByC_R(
			long companyId, String ruleKey)
		throws com.liferay.production.readiness.ignore.exception.
			NoSuchProductionReadinessIgnoreException {

		return getPersistence().findByC_R(companyId, ruleKey);
	}

	/**
	 * Returns the production readiness ignore where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the matching production readiness ignore, or <code>null</code> if a matching production readiness ignore could not be found
	 */
	public static ProductionReadinessIgnore fetchByC_R(
		long companyId, String ruleKey) {

		return getPersistence().fetchByC_R(companyId, ruleKey);
	}

	/**
	 * Returns the production readiness ignore where companyId = &#63; and ruleKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching production readiness ignore, or <code>null</code> if a matching production readiness ignore could not be found
	 */
	public static ProductionReadinessIgnore fetchByC_R(
		long companyId, String ruleKey, boolean useFinderCache) {

		return getPersistence().fetchByC_R(companyId, ruleKey, useFinderCache);
	}

	/**
	 * Removes the production readiness ignore where companyId = &#63; and ruleKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the production readiness ignore that was removed
	 */
	public static ProductionReadinessIgnore removeByC_R(
			long companyId, String ruleKey)
		throws com.liferay.production.readiness.ignore.exception.
			NoSuchProductionReadinessIgnoreException {

		return getPersistence().removeByC_R(companyId, ruleKey);
	}

	/**
	 * Returns the number of production readiness ignores where companyId = &#63; and ruleKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ruleKey the rule key
	 * @return the number of matching production readiness ignores
	 */
	public static int countByC_R(long companyId, String ruleKey) {
		return getPersistence().countByC_R(companyId, ruleKey);
	}

	/**
	 * Caches the production readiness ignore in the entity cache if it is enabled.
	 *
	 * @param productionReadinessIgnore the production readiness ignore
	 */
	public static void cacheResult(
		ProductionReadinessIgnore productionReadinessIgnore) {

		getPersistence().cacheResult(productionReadinessIgnore);
	}

	/**
	 * Caches the production readiness ignores in the entity cache if it is enabled.
	 *
	 * @param productionReadinessIgnores the production readiness ignores
	 */
	public static void cacheResult(
		List<ProductionReadinessIgnore> productionReadinessIgnores) {

		getPersistence().cacheResult(productionReadinessIgnores);
	}

	/**
	 * Creates a new production readiness ignore with the primary key. Does not add the production readiness ignore to the database.
	 *
	 * @param productionReadinessIgnoreId the primary key for the new production readiness ignore
	 * @return the new production readiness ignore
	 */
	public static ProductionReadinessIgnore create(
		long productionReadinessIgnoreId) {

		return getPersistence().create(productionReadinessIgnoreId);
	}

	/**
	 * Removes the production readiness ignore with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore that was removed
	 * @throws NoSuchProductionReadinessIgnoreException if a production readiness ignore with the primary key could not be found
	 */
	public static ProductionReadinessIgnore remove(
			long productionReadinessIgnoreId)
		throws com.liferay.production.readiness.ignore.exception.
			NoSuchProductionReadinessIgnoreException {

		return getPersistence().remove(productionReadinessIgnoreId);
	}

	public static ProductionReadinessIgnore updateImpl(
		ProductionReadinessIgnore productionReadinessIgnore) {

		return getPersistence().updateImpl(productionReadinessIgnore);
	}

	/**
	 * Returns the production readiness ignore with the primary key or throws a <code>NoSuchProductionReadinessIgnoreException</code> if it could not be found.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore
	 * @throws NoSuchProductionReadinessIgnoreException if a production readiness ignore with the primary key could not be found
	 */
	public static ProductionReadinessIgnore findByPrimaryKey(
			long productionReadinessIgnoreId)
		throws com.liferay.production.readiness.ignore.exception.
			NoSuchProductionReadinessIgnoreException {

		return getPersistence().findByPrimaryKey(productionReadinessIgnoreId);
	}

	/**
	 * Returns the production readiness ignore with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param productionReadinessIgnoreId the primary key of the production readiness ignore
	 * @return the production readiness ignore, or <code>null</code> if a production readiness ignore with the primary key could not be found
	 */
	public static ProductionReadinessIgnore fetchByPrimaryKey(
		long productionReadinessIgnoreId) {

		return getPersistence().fetchByPrimaryKey(productionReadinessIgnoreId);
	}

	/**
	 * Returns all the production readiness ignores.
	 *
	 * @return the production readiness ignores
	 */
	public static List<ProductionReadinessIgnore> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the production readiness ignores.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @return the range of production readiness ignores
	 */
	public static List<ProductionReadinessIgnore> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the production readiness ignores.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of production readiness ignores
	 */
	public static List<ProductionReadinessIgnore> findAll(
		int start, int end,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the production readiness ignores.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ProductionReadinessIgnoreModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of production readiness ignores
	 * @param end the upper bound of the range of production readiness ignores (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of production readiness ignores
	 */
	public static List<ProductionReadinessIgnore> findAll(
		int start, int end,
		OrderByComparator<ProductionReadinessIgnore> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the production readiness ignores from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of production readiness ignores.
	 *
	 * @return the number of production readiness ignores
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static ProductionReadinessIgnorePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		ProductionReadinessIgnorePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile ProductionReadinessIgnorePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1426429167