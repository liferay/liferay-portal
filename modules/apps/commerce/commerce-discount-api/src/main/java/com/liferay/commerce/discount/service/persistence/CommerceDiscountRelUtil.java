/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence;

import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce discount rel service. This utility wraps <code>com.liferay.commerce.discount.service.persistence.impl.CommerceDiscountRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CommerceDiscountRelPersistence
 * @generated
 */
public class CommerceDiscountRelUtil {

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
	public static void clearCache(CommerceDiscountRel commerceDiscountRel) {
		getPersistence().clearCache(commerceDiscountRel);
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
	public static Map<Serializable, CommerceDiscountRel> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommerceDiscountRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommerceDiscountRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommerceDiscountRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommerceDiscountRel update(
		CommerceDiscountRel commerceDiscountRel) {

		return getPersistence().update(commerceDiscountRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommerceDiscountRel update(
		CommerceDiscountRel commerceDiscountRel,
		ServiceContext serviceContext) {

		return getPersistence().update(commerceDiscountRel, serviceContext);
	}

	/**
	 * Returns all the commerce discount rels where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCommerceDiscountId(
		long commerceDiscountId) {

		return getPersistence().findByCommerceDiscountId(commerceDiscountId);
	}

	/**
	 * Returns a range of all the commerce discount rels where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end) {

		return getPersistence().findByCommerceDiscountId(
			commerceDiscountId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().findByCommerceDiscountId(
			commerceDiscountId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCommerceDiscountId(
			commerceDiscountId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel findByCommerceDiscountId_First(
			long commerceDiscountId,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByCommerceDiscountId_First(
			commerceDiscountId, orderByComparator);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel fetchByCommerceDiscountId_First(
		long commerceDiscountId,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().fetchByCommerceDiscountId_First(
			commerceDiscountId, orderByComparator);
	}

	/**
	 * Returns the last commerce discount rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel findByCommerceDiscountId_Last(
			long commerceDiscountId,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByCommerceDiscountId_Last(
			commerceDiscountId, orderByComparator);
	}

	/**
	 * Returns the last commerce discount rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel fetchByCommerceDiscountId_Last(
		long commerceDiscountId,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().fetchByCommerceDiscountId_Last(
			commerceDiscountId, orderByComparator);
	}

	/**
	 * Returns the commerce discount rels before and after the current commerce discount rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountRelId the primary key of the current commerce discount rel
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount rel
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	public static CommerceDiscountRel[] findByCommerceDiscountId_PrevAndNext(
			long commerceDiscountRelId, long commerceDiscountId,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByCommerceDiscountId_PrevAndNext(
			commerceDiscountRelId, commerceDiscountId, orderByComparator);
	}

	/**
	 * Removes all the commerce discount rels where commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 */
	public static void removeByCommerceDiscountId(long commerceDiscountId) {
		getPersistence().removeByCommerceDiscountId(commerceDiscountId);
	}

	/**
	 * Returns the number of commerce discount rels where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount rels
	 */
	public static int countByCommerceDiscountId(long commerceDiscountId) {
		return getPersistence().countByCommerceDiscountId(commerceDiscountId);
	}

	/**
	 * Returns all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @return the matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCD_CN(
		long commerceDiscountId, long classNameId) {

		return getPersistence().findByCD_CN(commerceDiscountId, classNameId);
	}

	/**
	 * Returns a range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCD_CN(
		long commerceDiscountId, long classNameId, int start, int end) {

		return getPersistence().findByCD_CN(
			commerceDiscountId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCD_CN(
		long commerceDiscountId, long classNameId, int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().findByCD_CN(
			commerceDiscountId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCD_CN(
		long commerceDiscountId, long classNameId, int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCD_CN(
			commerceDiscountId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel findByCD_CN_First(
			long commerceDiscountId, long classNameId,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByCD_CN_First(
			commerceDiscountId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel fetchByCD_CN_First(
		long commerceDiscountId, long classNameId,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().fetchByCD_CN_First(
			commerceDiscountId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel findByCD_CN_Last(
			long commerceDiscountId, long classNameId,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByCD_CN_Last(
			commerceDiscountId, classNameId, orderByComparator);
	}

	/**
	 * Returns the last commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel fetchByCD_CN_Last(
		long commerceDiscountId, long classNameId,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().fetchByCD_CN_Last(
			commerceDiscountId, classNameId, orderByComparator);
	}

	/**
	 * Returns the commerce discount rels before and after the current commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountRelId the primary key of the current commerce discount rel
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount rel
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	public static CommerceDiscountRel[] findByCD_CN_PrevAndNext(
			long commerceDiscountRelId, long commerceDiscountId,
			long classNameId,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByCD_CN_PrevAndNext(
			commerceDiscountRelId, commerceDiscountId, classNameId,
			orderByComparator);
	}

	/**
	 * Removes all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 */
	public static void removeByCD_CN(
		long commerceDiscountId, long classNameId) {

		getPersistence().removeByCD_CN(commerceDiscountId, classNameId);
	}

	/**
	 * Returns the number of commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @return the number of matching commerce discount rels
	 */
	public static int countByCD_CN(long commerceDiscountId, long classNameId) {
		return getPersistence().countByCD_CN(commerceDiscountId, classNameId);
	}

	/**
	 * Returns all the commerce discount rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCN_CPK(
		long classNameId, long classPK) {

		return getPersistence().findByCN_CPK(classNameId, classPK);
	}

	/**
	 * Returns a range of all the commerce discount rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCN_CPK(
		long classNameId, long classPK, int start, int end) {

		return getPersistence().findByCN_CPK(classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce discount rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().findByCN_CPK(
			classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce discount rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCN_CPK(
			classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel findByCN_CPK_First(
			long classNameId, long classPK,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByCN_CPK_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel fetchByCN_CPK_First(
		long classNameId, long classPK,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().fetchByCN_CPK_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last commerce discount rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel findByCN_CPK_Last(
			long classNameId, long classPK,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByCN_CPK_Last(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last commerce discount rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel fetchByCN_CPK_Last(
		long classNameId, long classPK,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().fetchByCN_CPK_Last(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the commerce discount rels before and after the current commerce discount rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountRelId the primary key of the current commerce discount rel
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount rel
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	public static CommerceDiscountRel[] findByCN_CPK_PrevAndNext(
			long commerceDiscountRelId, long classNameId, long classPK,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByCN_CPK_PrevAndNext(
			commerceDiscountRelId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the commerce discount rels where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByCN_CPK(long classNameId, long classPK) {
		getPersistence().removeByCN_CPK(classNameId, classPK);
	}

	/**
	 * Returns the number of commerce discount rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce discount rels
	 */
	public static int countByCN_CPK(long classNameId, long classPK) {
		return getPersistence().countByCN_CPK(classNameId, classPK);
	}

	/**
	 * Returns all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK) {

		return getPersistence().findByCD_CN_CPK(
			commerceDiscountId, classNameId, classPK);
	}

	/**
	 * Returns a range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK, int start,
		int end) {

		return getPersistence().findByCD_CN_CPK(
			commerceDiscountId, classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK, int start,
		int end, OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().findByCD_CN_CPK(
			commerceDiscountId, classNameId, classPK, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rels
	 */
	public static List<CommerceDiscountRel> findByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK, int start,
		int end, OrderByComparator<CommerceDiscountRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCD_CN_CPK(
			commerceDiscountId, classNameId, classPK, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel findByCD_CN_CPK_First(
			long commerceDiscountId, long classNameId, long classPK,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByCD_CN_CPK_First(
			commerceDiscountId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel fetchByCD_CN_CPK_First(
		long commerceDiscountId, long classNameId, long classPK,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().fetchByCD_CN_CPK_First(
			commerceDiscountId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel findByCD_CN_CPK_Last(
			long commerceDiscountId, long classNameId, long classPK,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByCD_CN_CPK_Last(
			commerceDiscountId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the last commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public static CommerceDiscountRel fetchByCD_CN_CPK_Last(
		long commerceDiscountId, long classNameId, long classPK,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().fetchByCD_CN_CPK_Last(
			commerceDiscountId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the commerce discount rels before and after the current commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountRelId the primary key of the current commerce discount rel
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount rel
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	public static CommerceDiscountRel[] findByCD_CN_CPK_PrevAndNext(
			long commerceDiscountRelId, long commerceDiscountId,
			long classNameId, long classPK,
			OrderByComparator<CommerceDiscountRel> orderByComparator)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByCD_CN_CPK_PrevAndNext(
			commerceDiscountRelId, commerceDiscountId, classNameId, classPK,
			orderByComparator);
	}

	/**
	 * Removes all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK) {

		getPersistence().removeByCD_CN_CPK(
			commerceDiscountId, classNameId, classPK);
	}

	/**
	 * Returns the number of commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce discount rels
	 */
	public static int countByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK) {

		return getPersistence().countByCD_CN_CPK(
			commerceDiscountId, classNameId, classPK);
	}

	/**
	 * Caches the commerce discount rel in the entity cache if it is enabled.
	 *
	 * @param commerceDiscountRel the commerce discount rel
	 */
	public static void cacheResult(CommerceDiscountRel commerceDiscountRel) {
		getPersistence().cacheResult(commerceDiscountRel);
	}

	/**
	 * Caches the commerce discount rels in the entity cache if it is enabled.
	 *
	 * @param commerceDiscountRels the commerce discount rels
	 */
	public static void cacheResult(
		List<CommerceDiscountRel> commerceDiscountRels) {

		getPersistence().cacheResult(commerceDiscountRels);
	}

	/**
	 * Creates a new commerce discount rel with the primary key. Does not add the commerce discount rel to the database.
	 *
	 * @param commerceDiscountRelId the primary key for the new commerce discount rel
	 * @return the new commerce discount rel
	 */
	public static CommerceDiscountRel create(long commerceDiscountRelId) {
		return getPersistence().create(commerceDiscountRelId);
	}

	/**
	 * Removes the commerce discount rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel that was removed
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	public static CommerceDiscountRel remove(long commerceDiscountRelId)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().remove(commerceDiscountRelId);
	}

	public static CommerceDiscountRel updateImpl(
		CommerceDiscountRel commerceDiscountRel) {

		return getPersistence().updateImpl(commerceDiscountRel);
	}

	/**
	 * Returns the commerce discount rel with the primary key or throws a <code>NoSuchDiscountRelException</code> if it could not be found.
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	public static CommerceDiscountRel findByPrimaryKey(
			long commerceDiscountRelId)
		throws com.liferay.commerce.discount.exception.
			NoSuchDiscountRelException {

		return getPersistence().findByPrimaryKey(commerceDiscountRelId);
	}

	/**
	 * Returns the commerce discount rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel, or <code>null</code> if a commerce discount rel with the primary key could not be found
	 */
	public static CommerceDiscountRel fetchByPrimaryKey(
		long commerceDiscountRelId) {

		return getPersistence().fetchByPrimaryKey(commerceDiscountRelId);
	}

	/**
	 * Returns all the commerce discount rels.
	 *
	 * @return the commerce discount rels
	 */
	public static List<CommerceDiscountRel> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the commerce discount rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of commerce discount rels
	 */
	public static List<CommerceDiscountRel> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the commerce discount rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce discount rels
	 */
	public static List<CommerceDiscountRel> findAll(
		int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce discount rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce discount rels
	 */
	public static List<CommerceDiscountRel> findAll(
		int start, int end,
		OrderByComparator<CommerceDiscountRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the commerce discount rels from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of commerce discount rels.
	 *
	 * @return the number of commerce discount rels
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static CommerceDiscountRelPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CommerceDiscountRelPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CommerceDiscountRelPersistence _persistence;

}