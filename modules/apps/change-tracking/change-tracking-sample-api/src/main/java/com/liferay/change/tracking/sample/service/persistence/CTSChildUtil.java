/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence;

import com.liferay.change.tracking.sample.model.CTSChild;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cts child service. This utility wraps <code>com.liferay.change.tracking.sample.service.persistence.impl.CTSChildPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTSChildPersistence
 * @generated
 */
public class CTSChildUtil {

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
	public static void clearCache(CTSChild ctsChild) {
		getPersistence().clearCache(ctsChild);
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
	public static Map<Serializable, CTSChild> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CTSChild> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CTSChild> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CTSChild> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CTSChild> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CTSChild update(CTSChild ctsChild) {
		return getPersistence().update(ctsChild);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CTSChild update(
		CTSChild ctsChild, ServiceContext serviceContext) {

		return getPersistence().update(ctsChild, serviceContext);
	}

	/**
	 * Returns all the cts childs where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cts childs
	 */
	public static List<CTSChild> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the cts childs where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of matching cts childs
	 */
	public static List<CTSChild> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts childs
	 */
	public static List<CTSChild> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts childs
	 */
	public static List<CTSChild> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	public static CTSChild findByCompanyId_First(
			long companyId, OrderByComparator<CTSChild> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSChildException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	public static CTSChild fetchByCompanyId_First(
		long companyId, OrderByComparator<CTSChild> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	public static CTSChild findByCompanyId_Last(
			long companyId, OrderByComparator<CTSChild> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSChildException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	public static CTSChild fetchByCompanyId_Last(
		long companyId, OrderByComparator<CTSChild> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the cts childs before and after the current cts child in the ordered set where companyId = &#63;.
	 *
	 * @param ctsChildId the primary key of the current cts child
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	public static CTSChild[] findByCompanyId_PrevAndNext(
			long ctsChildId, long companyId,
			OrderByComparator<CTSChild> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSChildException {

		return getPersistence().findByCompanyId_PrevAndNext(
			ctsChildId, companyId, orderByComparator);
	}

	/**
	 * Removes all the cts childs where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of cts childs where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cts childs
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the matching cts childs
	 */
	public static List<CTSChild> findByC_C(
		long companyId, long ctsGrandParentId) {

		return getPersistence().findByC_C(companyId, ctsGrandParentId);
	}

	/**
	 * Returns a range of all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of matching cts childs
	 */
	public static List<CTSChild> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end) {

		return getPersistence().findByC_C(
			companyId, ctsGrandParentId, start, end);
	}

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts childs
	 */
	public static List<CTSChild> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator) {

		return getPersistence().findByC_C(
			companyId, ctsGrandParentId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts childs
	 */
	public static List<CTSChild> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_C(
			companyId, ctsGrandParentId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	public static CTSChild findByC_C_First(
			long companyId, long ctsGrandParentId,
			OrderByComparator<CTSChild> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSChildException {

		return getPersistence().findByC_C_First(
			companyId, ctsGrandParentId, orderByComparator);
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	public static CTSChild fetchByC_C_First(
		long companyId, long ctsGrandParentId,
		OrderByComparator<CTSChild> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			companyId, ctsGrandParentId, orderByComparator);
	}

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	public static CTSChild findByC_C_Last(
			long companyId, long ctsGrandParentId,
			OrderByComparator<CTSChild> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSChildException {

		return getPersistence().findByC_C_Last(
			companyId, ctsGrandParentId, orderByComparator);
	}

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	public static CTSChild fetchByC_C_Last(
		long companyId, long ctsGrandParentId,
		OrderByComparator<CTSChild> orderByComparator) {

		return getPersistence().fetchByC_C_Last(
			companyId, ctsGrandParentId, orderByComparator);
	}

	/**
	 * Returns the cts childs before and after the current cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param ctsChildId the primary key of the current cts child
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	public static CTSChild[] findByC_C_PrevAndNext(
			long ctsChildId, long companyId, long ctsGrandParentId,
			OrderByComparator<CTSChild> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSChildException {

		return getPersistence().findByC_C_PrevAndNext(
			ctsChildId, companyId, ctsGrandParentId, orderByComparator);
	}

	/**
	 * Removes all the cts childs where companyId = &#63; and ctsGrandParentId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 */
	public static void removeByC_C(long companyId, long ctsGrandParentId) {
		getPersistence().removeByC_C(companyId, ctsGrandParentId);
	}

	/**
	 * Returns the number of cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the number of matching cts childs
	 */
	public static int countByC_C(long companyId, long ctsGrandParentId) {
		return getPersistence().countByC_C(companyId, ctsGrandParentId);
	}

	/**
	 * Returns all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @return the matching cts childs
	 */
	public static List<CTSChild> findByC_P(
		long companyId, long parentCTSChildId) {

		return getPersistence().findByC_P(companyId, parentCTSChildId);
	}

	/**
	 * Returns a range of all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of matching cts childs
	 */
	public static List<CTSChild> findByC_P(
		long companyId, long parentCTSChildId, int start, int end) {

		return getPersistence().findByC_P(
			companyId, parentCTSChildId, start, end);
	}

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts childs
	 */
	public static List<CTSChild> findByC_P(
		long companyId, long parentCTSChildId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator) {

		return getPersistence().findByC_P(
			companyId, parentCTSChildId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts childs
	 */
	public static List<CTSChild> findByC_P(
		long companyId, long parentCTSChildId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_P(
			companyId, parentCTSChildId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	public static CTSChild findByC_P_First(
			long companyId, long parentCTSChildId,
			OrderByComparator<CTSChild> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSChildException {

		return getPersistence().findByC_P_First(
			companyId, parentCTSChildId, orderByComparator);
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	public static CTSChild fetchByC_P_First(
		long companyId, long parentCTSChildId,
		OrderByComparator<CTSChild> orderByComparator) {

		return getPersistence().fetchByC_P_First(
			companyId, parentCTSChildId, orderByComparator);
	}

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	public static CTSChild findByC_P_Last(
			long companyId, long parentCTSChildId,
			OrderByComparator<CTSChild> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSChildException {

		return getPersistence().findByC_P_Last(
			companyId, parentCTSChildId, orderByComparator);
	}

	/**
	 * Returns the last cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	public static CTSChild fetchByC_P_Last(
		long companyId, long parentCTSChildId,
		OrderByComparator<CTSChild> orderByComparator) {

		return getPersistence().fetchByC_P_Last(
			companyId, parentCTSChildId, orderByComparator);
	}

	/**
	 * Returns the cts childs before and after the current cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param ctsChildId the primary key of the current cts child
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	public static CTSChild[] findByC_P_PrevAndNext(
			long ctsChildId, long companyId, long parentCTSChildId,
			OrderByComparator<CTSChild> orderByComparator)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSChildException {

		return getPersistence().findByC_P_PrevAndNext(
			ctsChildId, companyId, parentCTSChildId, orderByComparator);
	}

	/**
	 * Removes all the cts childs where companyId = &#63; and parentCTSChildId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 */
	public static void removeByC_P(long companyId, long parentCTSChildId) {
		getPersistence().removeByC_P(companyId, parentCTSChildId);
	}

	/**
	 * Returns the number of cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @return the number of matching cts childs
	 */
	public static int countByC_P(long companyId, long parentCTSChildId) {
		return getPersistence().countByC_P(companyId, parentCTSChildId);
	}

	/**
	 * Caches the cts child in the entity cache if it is enabled.
	 *
	 * @param ctsChild the cts child
	 */
	public static void cacheResult(CTSChild ctsChild) {
		getPersistence().cacheResult(ctsChild);
	}

	/**
	 * Caches the cts childs in the entity cache if it is enabled.
	 *
	 * @param ctsChilds the cts childs
	 */
	public static void cacheResult(List<CTSChild> ctsChilds) {
		getPersistence().cacheResult(ctsChilds);
	}

	/**
	 * Creates a new cts child with the primary key. Does not add the cts child to the database.
	 *
	 * @param ctsChildId the primary key for the new cts child
	 * @return the new cts child
	 */
	public static CTSChild create(long ctsChildId) {
		return getPersistence().create(ctsChildId);
	}

	/**
	 * Removes the cts child with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child that was removed
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	public static CTSChild remove(long ctsChildId)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSChildException {

		return getPersistence().remove(ctsChildId);
	}

	public static CTSChild updateImpl(CTSChild ctsChild) {
		return getPersistence().updateImpl(ctsChild);
	}

	/**
	 * Returns the cts child with the primary key or throws a <code>NoSuchCTSChildException</code> if it could not be found.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	public static CTSChild findByPrimaryKey(long ctsChildId)
		throws com.liferay.change.tracking.sample.exception.
			NoSuchCTSChildException {

		return getPersistence().findByPrimaryKey(ctsChildId);
	}

	/**
	 * Returns the cts child with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child, or <code>null</code> if a cts child with the primary key could not be found
	 */
	public static CTSChild fetchByPrimaryKey(long ctsChildId) {
		return getPersistence().fetchByPrimaryKey(ctsChildId);
	}

	/**
	 * Returns all the cts childs.
	 *
	 * @return the cts childs
	 */
	public static List<CTSChild> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the cts childs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of cts childs
	 */
	public static List<CTSChild> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the cts childs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cts childs
	 */
	public static List<CTSChild> findAll(
		int start, int end, OrderByComparator<CTSChild> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cts childs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cts childs
	 */
	public static List<CTSChild> findAll(
		int start, int end, OrderByComparator<CTSChild> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the cts childs from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of cts childs.
	 *
	 * @return the number of cts childs
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static CTSChildPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(CTSChildPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile CTSChildPersistence _persistence;

}