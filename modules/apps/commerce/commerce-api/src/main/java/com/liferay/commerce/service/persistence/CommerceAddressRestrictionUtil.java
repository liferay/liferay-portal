/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence;

import com.liferay.commerce.model.CommerceAddressRestriction;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce address restriction service. This utility wraps <code>com.liferay.commerce.service.persistence.impl.CommerceAddressRestrictionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAddressRestrictionPersistence
 * @generated
 */
public class CommerceAddressRestrictionUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CommerceAddressRestriction> commerceAddressRestrictions) {

		getPersistence().cacheResult(commerceAddressRestrictions);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		CommerceAddressRestriction commerceAddressRestriction) {

		getPersistence().cacheResult(commerceAddressRestriction);
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
		CommerceAddressRestriction commerceAddressRestriction) {

		getPersistence().clearCache(commerceAddressRestriction);
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
	public static Map<Serializable, CommerceAddressRestriction>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommerceAddressRestriction> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommerceAddressRestriction> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommerceAddressRestriction> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommerceAddressRestriction> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommerceAddressRestriction update(
		CommerceAddressRestriction commerceAddressRestriction) {

		return getPersistence().update(commerceAddressRestriction);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommerceAddressRestriction update(
		CommerceAddressRestriction commerceAddressRestriction,
		ServiceContext serviceContext) {

		return getPersistence().update(
			commerceAddressRestriction, serviceContext);
	}

	/**
	 * Returns an ordered range of all the commerce address restrictions where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAddressRestrictionModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce address restrictions
	 * @param end the upper bound of the range of commerce address restrictions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce address restrictions
	 */
	public static List<CommerceAddressRestriction> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CommerceAddressRestriction> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce address restriction in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce address restriction
	 * @throws NoSuchAddressRestrictionException if a matching commerce address restriction could not be found
	 */
	public static CommerceAddressRestriction findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<CommerceAddressRestriction> orderByComparator)
		throws com.liferay.commerce.exception.
			NoSuchAddressRestrictionException {

		return getPersistence().findByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first commerce address restriction in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce address restriction, or <code>null</code> if a matching commerce address restriction could not be found
	 */
	public static CommerceAddressRestriction fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<CommerceAddressRestriction> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the commerce address restrictions where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByC_C(long classNameId, long classPK) {
		getPersistence().removeByC_C(classNameId, classPK);
	}

	/**
	 * Returns the number of commerce address restrictions where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce address restrictions
	 */
	public static int countByC_C(long classNameId, long classPK) {
		return getPersistence().countByC_C(classNameId, classPK);
	}

	/**
	 * Returns the commerce address restriction where classNameId = &#63; and classPK = &#63; and countryId = &#63; or throws a <code>NoSuchAddressRestrictionException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param countryId the country ID
	 * @return the matching commerce address restriction
	 * @throws NoSuchAddressRestrictionException if a matching commerce address restriction could not be found
	 */
	public static CommerceAddressRestriction findByC_C_C(
			long classNameId, long classPK, long countryId)
		throws com.liferay.commerce.exception.
			NoSuchAddressRestrictionException {

		return getPersistence().findByC_C_C(classNameId, classPK, countryId);
	}

	/**
	 * Returns the commerce address restriction where classNameId = &#63; and classPK = &#63; and countryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param countryId the country ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce address restriction, or <code>null</code> if a matching commerce address restriction could not be found
	 */
	public static CommerceAddressRestriction fetchByC_C_C(
		long classNameId, long classPK, long countryId,
		boolean useFinderCache) {

		return getPersistence().fetchByC_C_C(
			classNameId, classPK, countryId, useFinderCache);
	}

	/**
	 * Removes the commerce address restriction where classNameId = &#63; and classPK = &#63; and countryId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param countryId the country ID
	 * @return the commerce address restriction that was removed
	 */
	public static CommerceAddressRestriction removeByC_C_C(
			long classNameId, long classPK, long countryId)
		throws com.liferay.commerce.exception.
			NoSuchAddressRestrictionException {

		return getPersistence().removeByC_C_C(classNameId, classPK, countryId);
	}

	/**
	 * Returns the number of commerce address restrictions where classNameId = &#63; and classPK = &#63; and countryId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param countryId the country ID
	 * @return the number of matching commerce address restrictions
	 */
	public static int countByC_C_C(
		long classNameId, long classPK, long countryId) {

		return getPersistence().countByC_C_C(classNameId, classPK, countryId);
	}

	/**
	 * Creates a new commerce address restriction with the primary key. Does not add the commerce address restriction to the database.
	 *
	 * @param commerceAddressRestrictionId the primary key for the new commerce address restriction
	 * @return the new commerce address restriction
	 */
	public static CommerceAddressRestriction create(
		long commerceAddressRestrictionId) {

		return getPersistence().create(commerceAddressRestrictionId);
	}

	/**
	 * Removes the commerce address restriction with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceAddressRestrictionId the primary key of the commerce address restriction
	 * @return the commerce address restriction that was removed
	 * @throws NoSuchAddressRestrictionException if a commerce address restriction with the primary key could not be found
	 */
	public static CommerceAddressRestriction remove(
			long commerceAddressRestrictionId)
		throws com.liferay.commerce.exception.
			NoSuchAddressRestrictionException {

		return getPersistence().remove(commerceAddressRestrictionId);
	}

	public static CommerceAddressRestriction updateImpl(
		CommerceAddressRestriction commerceAddressRestriction) {

		return getPersistence().updateImpl(commerceAddressRestriction);
	}

	/**
	 * Returns the commerce address restriction with the primary key or throws a <code>NoSuchAddressRestrictionException</code> if it could not be found.
	 *
	 * @param commerceAddressRestrictionId the primary key of the commerce address restriction
	 * @return the commerce address restriction
	 * @throws NoSuchAddressRestrictionException if a commerce address restriction with the primary key could not be found
	 */
	public static CommerceAddressRestriction findByPrimaryKey(
			long commerceAddressRestrictionId)
		throws com.liferay.commerce.exception.
			NoSuchAddressRestrictionException {

		return getPersistence().findByPrimaryKey(commerceAddressRestrictionId);
	}

	/**
	 * Returns the commerce address restriction with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceAddressRestrictionId the primary key of the commerce address restriction
	 * @return the commerce address restriction, or <code>null</code> if a commerce address restriction with the primary key could not be found
	 */
	public static CommerceAddressRestriction fetchByPrimaryKey(
		long commerceAddressRestrictionId) {

		return getPersistence().fetchByPrimaryKey(commerceAddressRestrictionId);
	}

	/**
	 * Returns the commerce address restriction where classNameId = &#63; and classPK = &#63; and countryId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param countryId the country ID
	 * @return the matching commerce address restriction, or <code>null</code> if a matching commerce address restriction could not be found
	 */
	public static CommerceAddressRestriction fetchByC_C_C(
		long classNameId, long classPK, long countryId) {

		return getPersistence().fetchByC_C_C(classNameId, classPK, countryId);
	}

	/**
	 * Returns all the commerce address restrictions where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching commerce address restrictions
	 */
	public static List<CommerceAddressRestriction> findByC_C(
		long classNameId, long classPK) {

		return getPersistence().findByC_C(classNameId, classPK);
	}

	/**
	 * Returns a range of all the commerce address restrictions where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAddressRestrictionModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce address restrictions
	 * @param end the upper bound of the range of commerce address restrictions (not inclusive)
	 * @return the range of matching commerce address restrictions
	 */
	public static List<CommerceAddressRestriction> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return getPersistence().findByC_C(classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce address restrictions where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAddressRestrictionModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce address restrictions
	 * @param end the upper bound of the range of commerce address restrictions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce address restrictions
	 */
	public static List<CommerceAddressRestriction> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CommerceAddressRestriction> orderByComparator) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator);
	}

	public static CommerceAddressRestrictionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CommerceAddressRestrictionPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CommerceAddressRestrictionPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-264473341