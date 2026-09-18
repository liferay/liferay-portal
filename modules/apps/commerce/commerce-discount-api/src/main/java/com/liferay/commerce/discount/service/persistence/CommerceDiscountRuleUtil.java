/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence;

import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce discount rule service. This utility wraps <code>com.liferay.commerce.discount.service.persistence.impl.CommerceDiscountRulePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CommerceDiscountRulePersistence
 * @generated
 */
public class CommerceDiscountRuleUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CommerceDiscountRule> commerceDiscountRules) {

		getPersistence().cacheResult(commerceDiscountRules);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(CommerceDiscountRule commerceDiscountRule) {
		getPersistence().cacheResult(commerceDiscountRule);
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
	public static void clearCache(CommerceDiscountRule commerceDiscountRule) {
		getPersistence().clearCache(commerceDiscountRule);
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
	public static Map<Serializable, CommerceDiscountRule> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommerceDiscountRule> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommerceDiscountRule> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommerceDiscountRule> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommerceDiscountRule> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommerceDiscountRule update(
		CommerceDiscountRule commerceDiscountRule) {

		return getPersistence().update(commerceDiscountRule);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommerceDiscountRule update(
		CommerceDiscountRule commerceDiscountRule,
		ServiceContext serviceContext) {

		return getPersistence().update(commerceDiscountRule, serviceContext);
	}

	/**
	 * Returns an ordered range of all the commerce discount rules where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRuleModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rules
	 * @param end the upper bound of the range of commerce discount rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rules
	 */
	public static List<CommerceDiscountRule> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountRule> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCommerceDiscountId(
			commerceDiscountId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount rule in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rule
	 * @throws NoSuchDiscountRuleException if a matching commerce discount rule could not be found
	 */
	public static CommerceDiscountRule findByCommerceDiscountId_First(
			long commerceDiscountId,
			OrderByComparator<CommerceDiscountRule> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRuleException {

		return getPersistence().findByCommerceDiscountId_First(
			commerceDiscountId, orderByComparator);
	}

	/**
	 * Returns the first commerce discount rule in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rule, or <code>null</code> if a matching commerce discount rule could not be found
	 */
	public static CommerceDiscountRule fetchByCommerceDiscountId_First(
		long commerceDiscountId,
		OrderByComparator<CommerceDiscountRule> orderByComparator) {

		return getPersistence().fetchByCommerceDiscountId_First(
			commerceDiscountId, orderByComparator);
	}

	/**
	 * Removes all the commerce discount rules where commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 */
	public static void removeByCommerceDiscountId(long commerceDiscountId) {
		getPersistence().removeByCommerceDiscountId(commerceDiscountId);
	}

	/**
	 * Returns the number of commerce discount rules where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount rules
	 */
	public static int countByCommerceDiscountId(long commerceDiscountId) {
		return getPersistence().countByCommerceDiscountId(commerceDiscountId);
	}

	/**
	 * Returns the commerce discount rule where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchDiscountRuleException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce discount rule
	 * @throws NoSuchDiscountRuleException if a matching commerce discount rule could not be found
	 */
	public static CommerceDiscountRule findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRuleException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce discount rule where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce discount rule, or <code>null</code> if a matching commerce discount rule could not be found
	 */
	public static CommerceDiscountRule fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the commerce discount rule where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce discount rule that was removed
	 */
	public static CommerceDiscountRule removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRuleException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of commerce discount rules where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce discount rules
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new commerce discount rule with the primary key. Does not add the commerce discount rule to the database.
	 *
	 * @param commerceDiscountRuleId the primary key for the new commerce discount rule
	 * @return the new commerce discount rule
	 */
	public static CommerceDiscountRule create(long commerceDiscountRuleId) {
		return getPersistence().create(commerceDiscountRuleId);
	}

	/**
	 * Removes the commerce discount rule with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountRuleId the primary key of the commerce discount rule
	 * @return the commerce discount rule that was removed
	 * @throws NoSuchDiscountRuleException if a commerce discount rule with the primary key could not be found
	 */
	public static CommerceDiscountRule remove(long commerceDiscountRuleId)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRuleException {

		return getPersistence().remove(commerceDiscountRuleId);
	}

	public static CommerceDiscountRule updateImpl(
		CommerceDiscountRule commerceDiscountRule) {

		return getPersistence().updateImpl(commerceDiscountRule);
	}

	/**
	 * Returns the commerce discount rule with the primary key or throws a <code>NoSuchDiscountRuleException</code> if it could not be found.
	 *
	 * @param commerceDiscountRuleId the primary key of the commerce discount rule
	 * @return the commerce discount rule
	 * @throws NoSuchDiscountRuleException if a commerce discount rule with the primary key could not be found
	 */
	public static CommerceDiscountRule findByPrimaryKey(
			long commerceDiscountRuleId)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRuleException {

		return getPersistence().findByPrimaryKey(commerceDiscountRuleId);
	}

	/**
	 * Returns the commerce discount rule with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountRuleId the primary key of the commerce discount rule
	 * @return the commerce discount rule, or <code>null</code> if a commerce discount rule with the primary key could not be found
	 */
	public static CommerceDiscountRule fetchByPrimaryKey(
		long commerceDiscountRuleId) {

		return getPersistence().fetchByPrimaryKey(commerceDiscountRuleId);
	}

	/**
	 * Returns the commerce discount rule where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce discount rule, or <code>null</code> if a matching commerce discount rule could not be found
	 */
	public static CommerceDiscountRule fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the commerce discount rules where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the matching commerce discount rules
	 */
	public static List<CommerceDiscountRule> findByCommerceDiscountId(
		long commerceDiscountId) {

		return getPersistence().findByCommerceDiscountId(commerceDiscountId);
	}

	/**
	 * Returns a range of all the commerce discount rules where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRuleModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rules
	 * @param end the upper bound of the range of commerce discount rules (not inclusive)
	 * @return the range of matching commerce discount rules
	 */
	public static List<CommerceDiscountRule> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end) {

		return getPersistence().findByCommerceDiscountId(
			commerceDiscountId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce discount rules where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRuleModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rules
	 * @param end the upper bound of the range of commerce discount rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount rules
	 */
	public static List<CommerceDiscountRule> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountRule> orderByComparator) {

		return getPersistence().findByCommerceDiscountId(
			commerceDiscountId, start, end, orderByComparator);
	}

	public static CommerceDiscountRulePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CommerceDiscountRulePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CommerceDiscountRulePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1398705285