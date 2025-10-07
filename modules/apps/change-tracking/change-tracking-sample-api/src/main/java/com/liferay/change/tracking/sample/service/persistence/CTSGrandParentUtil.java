/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence;

import com.liferay.change.tracking.sample.model.CTSGrandParent;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cts grand parent service. This utility wraps <code>com.liferay.change.tracking.sample.service.persistence.impl.CTSGrandParentPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTSGrandParentPersistence
 * @generated
 */
public class CTSGrandParentUtil {

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
	public static void clearCache(CTSGrandParent ctsGrandParent) {
		getPersistence().clearCache(ctsGrandParent);
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
	public static Map<Serializable, CTSGrandParent> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CTSGrandParent> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CTSGrandParent> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CTSGrandParent> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CTSGrandParent> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CTSGrandParent update(CTSGrandParent ctsGrandParent) {
		return getPersistence().update(ctsGrandParent);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CTSGrandParent update(
		CTSGrandParent ctsGrandParent, ServiceContext serviceContext) {

		return getPersistence().update(ctsGrandParent, serviceContext);
	}

	/**
	 * Returns all the cts grand parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cts grand parents
	 */
	public static List<CTSGrandParent> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the cts grand parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @return the range of matching cts grand parents
	 */
	public static List<CTSGrandParent> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cts grand parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts grand parents
	 */
	public static List<CTSGrandParent> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSGrandParent> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cts grand parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts grand parents
	 */
	public static List<CTSGrandParent> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSGrandParent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts grand parent
	 * @throws NoSuchCTSGrandParentException if a matching cts grand parent could not be found
	 */
	public static CTSGrandParent findByCompanyId_First(
			long companyId, OrderByComparator<CTSGrandParent> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSGrandParentException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts grand parent, or <code>null</code> if a matching cts grand parent could not be found
	 */
	public static CTSGrandParent fetchByCompanyId_First(
		long companyId, OrderByComparator<CTSGrandParent> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts grand parent
	 * @throws NoSuchCTSGrandParentException if a matching cts grand parent could not be found
	 */
	public static CTSGrandParent findByCompanyId_Last(
			long companyId, OrderByComparator<CTSGrandParent> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSGrandParentException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts grand parent, or <code>null</code> if a matching cts grand parent could not be found
	 */
	public static CTSGrandParent fetchByCompanyId_Last(
		long companyId, OrderByComparator<CTSGrandParent> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the cts grand parents before and after the current cts grand parent in the ordered set where companyId = &#63;.
	 *
	 * @param ctsGrandParentId the primary key of the current cts grand parent
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts grand parent
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	public static CTSGrandParent[] findByCompanyId_PrevAndNext(
			long ctsGrandParentId, long companyId,
			OrderByComparator<CTSGrandParent> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSGrandParentException {

		return getPersistence().findByCompanyId_PrevAndNext(
			ctsGrandParentId, companyId, orderByComparator);
	}

	/**
	 * Removes all the cts grand parents where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of cts grand parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cts grand parents
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Caches the cts grand parent in the entity cache if it is enabled.
	 *
	 * @param ctsGrandParent the cts grand parent
	 */
	public static void cacheResult(CTSGrandParent ctsGrandParent) {
		getPersistence().cacheResult(ctsGrandParent);
	}

	/**
	 * Caches the cts grand parents in the entity cache if it is enabled.
	 *
	 * @param ctsGrandParents the cts grand parents
	 */
	public static void cacheResult(List<CTSGrandParent> ctsGrandParents) {
		getPersistence().cacheResult(ctsGrandParents);
	}

	/**
	 * Creates a new cts grand parent with the primary key. Does not add the cts grand parent to the database.
	 *
	 * @param ctsGrandParentId the primary key for the new cts grand parent
	 * @return the new cts grand parent
	 */
	public static CTSGrandParent create(long ctsGrandParentId) {
		return getPersistence().create(ctsGrandParentId);
	}

	/**
	 * Removes the cts grand parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent that was removed
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	public static CTSGrandParent remove(long ctsGrandParentId)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSGrandParentException {

		return getPersistence().remove(ctsGrandParentId);
	}

	public static CTSGrandParent updateImpl(CTSGrandParent ctsGrandParent) {
		return getPersistence().updateImpl(ctsGrandParent);
	}

	/**
	 * Returns the cts grand parent with the primary key or throws a <code>NoSuchCTSGrandParentException</code> if it could not be found.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent
	 * @throws NoSuchCTSGrandParentException if a cts grand parent with the primary key could not be found
	 */
	public static CTSGrandParent findByPrimaryKey(long ctsGrandParentId)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSGrandParentException {

		return getPersistence().findByPrimaryKey(ctsGrandParentId);
	}

	/**
	 * Returns the cts grand parent with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent, or <code>null</code> if a cts grand parent with the primary key could not be found
	 */
	public static CTSGrandParent fetchByPrimaryKey(long ctsGrandParentId) {
		return getPersistence().fetchByPrimaryKey(ctsGrandParentId);
	}

	/**
	 * Returns all the cts grand parents.
	 *
	 * @return the cts grand parents
	 */
	public static List<CTSGrandParent> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the cts grand parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @return the range of cts grand parents
	 */
	public static List<CTSGrandParent> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the cts grand parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cts grand parents
	 */
	public static List<CTSGrandParent> findAll(
		int start, int end,
		OrderByComparator<CTSGrandParent> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cts grand parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cts grand parents
	 */
	public static List<CTSGrandParent> findAll(
		int start, int end, OrderByComparator<CTSGrandParent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the cts grand parents from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of cts grand parents.
	 *
	 * @return the number of cts grand parents
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static CTSGrandParentPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(CTSGrandParentPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile CTSGrandParentPersistence _persistence;

}