/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence;

import com.liferay.change.tracking.sample.model.CTSParent;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cts parent service. This utility wraps <code>com.liferay.change.tracking.sample.service.persistence.impl.CTSParentPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTSParentPersistence
 * @generated
 */
public class CTSParentUtil {

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
	public static void clearCache(CTSParent ctsParent) {
		getPersistence().clearCache(ctsParent);
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
	public static Map<Serializable, CTSParent> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CTSParent> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CTSParent> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CTSParent> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CTSParent> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CTSParent update(CTSParent ctsParent) {
		return getPersistence().update(ctsParent);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CTSParent update(
		CTSParent ctsParent, ServiceContext serviceContext) {

		return getPersistence().update(ctsParent, serviceContext);
	}

	/**
	 * Returns all the cts parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cts parents
	 */
	public static List<CTSParent> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the cts parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @return the range of matching cts parents
	 */
	public static List<CTSParent> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts parents
	 */
	public static List<CTSParent> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSParent> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts parents
	 */
	public static List<CTSParent> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSParent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	public static CTSParent findByCompanyId_First(
			long companyId, OrderByComparator<CTSParent> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSParentException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	public static CTSParent fetchByCompanyId_First(
		long companyId, OrderByComparator<CTSParent> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	public static CTSParent findByCompanyId_Last(
			long companyId, OrderByComparator<CTSParent> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSParentException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	public static CTSParent fetchByCompanyId_Last(
		long companyId, OrderByComparator<CTSParent> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the cts parents before and after the current cts parent in the ordered set where companyId = &#63;.
	 *
	 * @param ctsParentId the primary key of the current cts parent
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts parent
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	public static CTSParent[] findByCompanyId_PrevAndNext(
			long ctsParentId, long companyId,
			OrderByComparator<CTSParent> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSParentException {

		return getPersistence().findByCompanyId_PrevAndNext(
			ctsParentId, companyId, orderByComparator);
	}

	/**
	 * Removes all the cts parents where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of cts parents where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cts parents
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the matching cts parents
	 */
	public static List<CTSParent> findByC_C(
		long companyId, long ctsGrandParentId) {

		return getPersistence().findByC_C(companyId, ctsGrandParentId);
	}

	/**
	 * Returns a range of all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @return the range of matching cts parents
	 */
	public static List<CTSParent> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end) {

		return getPersistence().findByC_C(
			companyId, ctsGrandParentId, start, end);
	}

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts parents
	 */
	public static List<CTSParent> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		OrderByComparator<CTSParent> orderByComparator) {

		return getPersistence().findByC_C(
			companyId, ctsGrandParentId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts parents
	 */
	public static List<CTSParent> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		OrderByComparator<CTSParent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			companyId, ctsGrandParentId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	public static CTSParent findByC_C_First(
			long companyId, long ctsGrandParentId,
			OrderByComparator<CTSParent> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSParentException {

		return getPersistence().findByC_C_First(
			companyId, ctsGrandParentId, orderByComparator);
	}

	/**
	 * Returns the first cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	public static CTSParent fetchByC_C_First(
		long companyId, long ctsGrandParentId,
		OrderByComparator<CTSParent> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			companyId, ctsGrandParentId, orderByComparator);
	}

	/**
	 * Returns the last cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts parent
	 * @throws NoSuchCTSParentException if a matching cts parent could not be found
	 */
	public static CTSParent findByC_C_Last(
			long companyId, long ctsGrandParentId,
			OrderByComparator<CTSParent> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSParentException {

		return getPersistence().findByC_C_Last(
			companyId, ctsGrandParentId, orderByComparator);
	}

	/**
	 * Returns the last cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts parent, or <code>null</code> if a matching cts parent could not be found
	 */
	public static CTSParent fetchByC_C_Last(
		long companyId, long ctsGrandParentId,
		OrderByComparator<CTSParent> orderByComparator) {

		return getPersistence().fetchByC_C_Last(
			companyId, ctsGrandParentId, orderByComparator);
	}

	/**
	 * Returns the cts parents before and after the current cts parent in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param ctsParentId the primary key of the current cts parent
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts parent
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	public static CTSParent[] findByC_C_PrevAndNext(
			long ctsParentId, long companyId, long ctsGrandParentId,
			OrderByComparator<CTSParent> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSParentException {

		return getPersistence().findByC_C_PrevAndNext(
			ctsParentId, companyId, ctsGrandParentId, orderByComparator);
	}

	/**
	 * Removes all the cts parents where companyId = &#63; and ctsGrandParentId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 */
	public static void removeByC_C(long companyId, long ctsGrandParentId) {
		getPersistence().removeByC_C(companyId, ctsGrandParentId);
	}

	/**
	 * Returns the number of cts parents where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the number of matching cts parents
	 */
	public static int countByC_C(long companyId, long ctsGrandParentId) {
		return getPersistence().countByC_C(companyId, ctsGrandParentId);
	}

	/**
	 * Caches the cts parent in the entity cache if it is enabled.
	 *
	 * @param ctsParent the cts parent
	 */
	public static void cacheResult(CTSParent ctsParent) {
		getPersistence().cacheResult(ctsParent);
	}

	/**
	 * Caches the cts parents in the entity cache if it is enabled.
	 *
	 * @param ctsParents the cts parents
	 */
	public static void cacheResult(List<CTSParent> ctsParents) {
		getPersistence().cacheResult(ctsParents);
	}

	/**
	 * Creates a new cts parent with the primary key. Does not add the cts parent to the database.
	 *
	 * @param ctsParentId the primary key for the new cts parent
	 * @return the new cts parent
	 */
	public static CTSParent create(long ctsParentId) {
		return getPersistence().create(ctsParentId);
	}

	/**
	 * Removes the cts parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent that was removed
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	public static CTSParent remove(long ctsParentId)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSParentException {

		return getPersistence().remove(ctsParentId);
	}

	public static CTSParent updateImpl(CTSParent ctsParent) {
		return getPersistence().updateImpl(ctsParent);
	}

	/**
	 * Returns the cts parent with the primary key or throws a <code>NoSuchCTSParentException</code> if it could not be found.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent
	 * @throws NoSuchCTSParentException if a cts parent with the primary key could not be found
	 */
	public static CTSParent findByPrimaryKey(long ctsParentId)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSParentException {

		return getPersistence().findByPrimaryKey(ctsParentId);
	}

	/**
	 * Returns the cts parent with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsParentId the primary key of the cts parent
	 * @return the cts parent, or <code>null</code> if a cts parent with the primary key could not be found
	 */
	public static CTSParent fetchByPrimaryKey(long ctsParentId) {
		return getPersistence().fetchByPrimaryKey(ctsParentId);
	}

	/**
	 * Returns all the cts parents.
	 *
	 * @return the cts parents
	 */
	public static List<CTSParent> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the cts parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @return the range of cts parents
	 */
	public static List<CTSParent> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the cts parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cts parents
	 */
	public static List<CTSParent> findAll(
		int start, int end, OrderByComparator<CTSParent> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cts parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts parents
	 * @param end the upper bound of the range of cts parents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cts parents
	 */
	public static List<CTSParent> findAll(
		int start, int end, OrderByComparator<CTSParent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the cts parents from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of cts parents.
	 *
	 * @return the number of cts parents
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static CTSParentPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(CTSParentPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile CTSParentPersistence _persistence;

}