/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence;

import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce shipping method service. This utility wraps <code>com.liferay.commerce.service.persistence.impl.CommerceShippingMethodPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShippingMethodPersistence
 * @generated
 */
public class CommerceShippingMethodUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CommerceShippingMethod> commerceShippingMethods) {

		getPersistence().cacheResult(commerceShippingMethods);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		CommerceShippingMethod commerceShippingMethod) {

		getPersistence().cacheResult(commerceShippingMethod);
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
	public static void clearCache(
		CommerceShippingMethod commerceShippingMethod) {

		getPersistence().clearCache(commerceShippingMethod);
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
	public static Map<Serializable, CommerceShippingMethod> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommerceShippingMethod> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommerceShippingMethod> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommerceShippingMethod> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommerceShippingMethod update(
		CommerceShippingMethod commerceShippingMethod) {

		return getPersistence().update(commerceShippingMethod);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommerceShippingMethod update(
		CommerceShippingMethod commerceShippingMethod,
		ServiceContext serviceContext) {

		return getPersistence().update(commerceShippingMethod, serviceContext);
	}

	/**
	 * Returns an ordered range of all the commerce shipping methods where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping methods
	 */
	public static List<CommerceShippingMethod> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipping method in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping method
	 * @throws NoSuchShippingMethodException if a matching commerce shipping method could not be found
	 */
	public static CommerceShippingMethod findByGroupId_First(
			long groupId,
			OrderByComparator<CommerceShippingMethod> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShippingMethodException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first commerce shipping method in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	public static CommerceShippingMethod fetchByGroupId_First(
		long groupId,
		OrderByComparator<CommerceShippingMethod> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the commerce shipping methods where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of commerce shipping methods where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce shipping methods
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the commerce shipping methods where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping methods
	 */
	public static List<CommerceShippingMethod> findByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_A(
			groupId, active, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipping method in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping method
	 * @throws NoSuchShippingMethodException if a matching commerce shipping method could not be found
	 */
	public static CommerceShippingMethod findByG_A_First(
			long groupId, boolean active,
			OrderByComparator<CommerceShippingMethod> orderByComparator)
		throws com.liferay.commerce.exception.NoSuchShippingMethodException {

		return getPersistence().findByG_A_First(
			groupId, active, orderByComparator);
	}

	/**
	 * Returns the first commerce shipping method in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	public static CommerceShippingMethod fetchByG_A_First(
		long groupId, boolean active,
		OrderByComparator<CommerceShippingMethod> orderByComparator) {

		return getPersistence().fetchByG_A_First(
			groupId, active, orderByComparator);
	}

	/**
	 * Removes all the commerce shipping methods where groupId = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 */
	public static void removeByG_A(long groupId, boolean active) {
		getPersistence().removeByG_A(groupId, active);
	}

	/**
	 * Returns the number of commerce shipping methods where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching commerce shipping methods
	 */
	public static int countByG_A(long groupId, boolean active) {
		return getPersistence().countByG_A(groupId, active);
	}

	/**
	 * Returns the commerce shipping method where groupId = &#63; and engineKey = &#63; or throws a <code>NoSuchShippingMethodException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the matching commerce shipping method
	 * @throws NoSuchShippingMethodException if a matching commerce shipping method could not be found
	 */
	public static CommerceShippingMethod findByG_E(
			long groupId, String engineKey)
		throws com.liferay.commerce.exception.NoSuchShippingMethodException {

		return getPersistence().findByG_E(groupId, engineKey);
	}

	/**
	 * Returns the commerce shipping method where groupId = &#63; and engineKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	public static CommerceShippingMethod fetchByG_E(
		long groupId, String engineKey, boolean useFinderCache) {

		return getPersistence().fetchByG_E(groupId, engineKey, useFinderCache);
	}

	/**
	 * Removes the commerce shipping method where groupId = &#63; and engineKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the commerce shipping method that was removed
	 */
	public static CommerceShippingMethod removeByG_E(
			long groupId, String engineKey)
		throws com.liferay.commerce.exception.NoSuchShippingMethodException {

		return getPersistence().removeByG_E(groupId, engineKey);
	}

	/**
	 * Returns the number of commerce shipping methods where groupId = &#63; and engineKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the number of matching commerce shipping methods
	 */
	public static int countByG_E(long groupId, String engineKey) {
		return getPersistence().countByG_E(groupId, engineKey);
	}

	/**
	 * Returns the commerce shipping method where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchShippingMethodException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce shipping method
	 * @throws NoSuchShippingMethodException if a matching commerce shipping method could not be found
	 */
	public static CommerceShippingMethod findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.exception.NoSuchShippingMethodException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce shipping method where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	public static CommerceShippingMethod fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the commerce shipping method where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce shipping method that was removed
	 */
	public static CommerceShippingMethod removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.exception.NoSuchShippingMethodException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of commerce shipping methods where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce shipping methods
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new commerce shipping method with the primary key. Does not add the commerce shipping method to the database.
	 *
	 * @param commerceShippingMethodId the primary key for the new commerce shipping method
	 * @return the new commerce shipping method
	 */
	public static CommerceShippingMethod create(long commerceShippingMethodId) {
		return getPersistence().create(commerceShippingMethodId);
	}

	/**
	 * Removes the commerce shipping method with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method that was removed
	 * @throws NoSuchShippingMethodException if a commerce shipping method with the primary key could not be found
	 */
	public static CommerceShippingMethod remove(long commerceShippingMethodId)
		throws com.liferay.commerce.exception.NoSuchShippingMethodException {

		return getPersistence().remove(commerceShippingMethodId);
	}

	public static CommerceShippingMethod updateImpl(
		CommerceShippingMethod commerceShippingMethod) {

		return getPersistence().updateImpl(commerceShippingMethod);
	}

	/**
	 * Returns the commerce shipping method with the primary key or throws a <code>NoSuchShippingMethodException</code> if it could not be found.
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method
	 * @throws NoSuchShippingMethodException if a commerce shipping method with the primary key could not be found
	 */
	public static CommerceShippingMethod findByPrimaryKey(
			long commerceShippingMethodId)
		throws com.liferay.commerce.exception.NoSuchShippingMethodException {

		return getPersistence().findByPrimaryKey(commerceShippingMethodId);
	}

	/**
	 * Returns the commerce shipping method with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method, or <code>null</code> if a commerce shipping method with the primary key could not be found
	 */
	public static CommerceShippingMethod fetchByPrimaryKey(
		long commerceShippingMethodId) {

		return getPersistence().fetchByPrimaryKey(commerceShippingMethodId);
	}

	/**
	 * Returns the commerce shipping method where groupId = &#63; and engineKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	public static CommerceShippingMethod fetchByG_E(
		long groupId, String engineKey) {

		return getPersistence().fetchByG_E(groupId, engineKey);
	}

	/**
	 * Returns the commerce shipping method where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	public static CommerceShippingMethod fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the commerce shipping methods where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce shipping methods
	 */
	public static List<CommerceShippingMethod> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the commerce shipping methods where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @return the range of matching commerce shipping methods
	 */
	public static List<CommerceShippingMethod> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce shipping methods where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipping methods
	 */
	public static List<CommerceShippingMethod> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce shipping methods where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the matching commerce shipping methods
	 */
	public static List<CommerceShippingMethod> findByG_A(
		long groupId, boolean active) {

		return getPersistence().findByG_A(groupId, active);
	}

	/**
	 * Returns a range of all the commerce shipping methods where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @return the range of matching commerce shipping methods
	 */
	public static List<CommerceShippingMethod> findByG_A(
		long groupId, boolean active, int start, int end) {

		return getPersistence().findByG_A(groupId, active, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce shipping methods where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipping methods
	 */
	public static List<CommerceShippingMethod> findByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator) {

		return getPersistence().findByG_A(
			groupId, active, start, end, orderByComparator);
	}

	public static CommerceShippingMethodPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CommerceShippingMethodPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CommerceShippingMethodPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1894038886