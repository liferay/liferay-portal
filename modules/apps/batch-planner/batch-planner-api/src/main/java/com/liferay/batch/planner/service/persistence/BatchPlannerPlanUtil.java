/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.service.persistence;

import com.liferay.batch.planner.model.BatchPlannerPlan;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the batch planner plan service. This utility wraps <code>com.liferay.batch.planner.service.persistence.impl.BatchPlannerPlanPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Igor Beslic
 * @see BatchPlannerPlanPersistence
 * @generated
 */
public class BatchPlannerPlanUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<BatchPlannerPlan> batchPlannerPlans) {
		getPersistence().cacheResult(batchPlannerPlans);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(BatchPlannerPlan batchPlannerPlan) {
		getPersistence().cacheResult(batchPlannerPlan);
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
	public static void clearCache(BatchPlannerPlan batchPlannerPlan) {
		getPersistence().clearCache(batchPlannerPlan);
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
	public static Map<Serializable, BatchPlannerPlan> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<BatchPlannerPlan> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<BatchPlannerPlan> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<BatchPlannerPlan> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static BatchPlannerPlan update(BatchPlannerPlan batchPlannerPlan) {
		return getPersistence().update(batchPlannerPlan);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static BatchPlannerPlan update(
		BatchPlannerPlan batchPlannerPlan, ServiceContext serviceContext) {

		return getPersistence().update(batchPlannerPlan, serviceContext);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	public static BatchPlannerPlan findByCompanyId_First(
			long companyId,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws com.liferay.batch.planner.exception.NoSuchPlanException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	public static BatchPlannerPlan fetchByCompanyId_First(
		long companyId, OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans that the user has permission to view
	 */
	public static List<BatchPlannerPlan> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().filterFindByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of batch planner plans where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching batch planner plans
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns the number of batch planner plans that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching batch planner plans that the user has permission to view
	 */
	public static int filterCountByCompanyId(long companyId) {
		return getPersistence().filterCountByCompanyId(companyId);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and export = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByC_E(
		long companyId, boolean export, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_E(
			companyId, export, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	public static BatchPlannerPlan findByC_E_First(
			long companyId, boolean export,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws com.liferay.batch.planner.exception.NoSuchPlanException {

		return getPersistence().findByC_E_First(
			companyId, export, orderByComparator);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	public static BatchPlannerPlan fetchByC_E_First(
		long companyId, boolean export,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().fetchByC_E_First(
			companyId, export, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and export = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans that the user has permission to view
	 */
	public static List<BatchPlannerPlan> filterFindByC_E(
		long companyId, boolean export, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().filterFindByC_E(
			companyId, export, start, end, orderByComparator);
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; and export = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 */
	public static void removeByC_E(long companyId, boolean export) {
		getPersistence().removeByC_E(companyId, export);
	}

	/**
	 * Returns the number of batch planner plans where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @return the number of matching batch planner plans
	 */
	public static int countByC_E(long companyId, boolean export) {
		return getPersistence().countByC_E(companyId, export);
	}

	/**
	 * Returns the number of batch planner plans that the user has permission to view where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @return the number of matching batch planner plans that the user has permission to view
	 */
	public static int filterCountByC_E(long companyId, boolean export) {
		return getPersistence().filterCountByC_E(companyId, export);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByC_T(
		long companyId, boolean template, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_T(
			companyId, template, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	public static BatchPlannerPlan findByC_T_First(
			long companyId, boolean template,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws com.liferay.batch.planner.exception.NoSuchPlanException {

		return getPersistence().findByC_T_First(
			companyId, template, orderByComparator);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	public static BatchPlannerPlan fetchByC_T_First(
		long companyId, boolean template,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().fetchByC_T_First(
			companyId, template, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans that the user has permission to view
	 */
	public static List<BatchPlannerPlan> filterFindByC_T(
		long companyId, boolean template, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().filterFindByC_T(
			companyId, template, start, end, orderByComparator);
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; and template = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 */
	public static void removeByC_T(long companyId, boolean template) {
		getPersistence().removeByC_T(companyId, template);
	}

	/**
	 * Returns the number of batch planner plans where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @return the number of matching batch planner plans
	 */
	public static int countByC_T(long companyId, boolean template) {
		return getPersistence().countByC_T(companyId, template);
	}

	/**
	 * Returns the number of batch planner plans that the user has permission to view where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @return the number of matching batch planner plans that the user has permission to view
	 */
	public static int filterCountByC_T(long companyId, boolean template) {
		return getPersistence().filterCountByC_T(companyId, template);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByC_E_T(
		long companyId, boolean export, boolean template, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_E_T(
			companyId, export, template, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	public static BatchPlannerPlan findByC_E_T_First(
			long companyId, boolean export, boolean template,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws com.liferay.batch.planner.exception.NoSuchPlanException {

		return getPersistence().findByC_E_T_First(
			companyId, export, template, orderByComparator);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	public static BatchPlannerPlan fetchByC_E_T_First(
		long companyId, boolean export, boolean template,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().fetchByC_E_T_First(
			companyId, export, template, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans that the user has permission to view
	 */
	public static List<BatchPlannerPlan> filterFindByC_E_T(
		long companyId, boolean export, boolean template, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().filterFindByC_E_T(
			companyId, export, template, start, end, orderByComparator);
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; and export = &#63; and template = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 */
	public static void removeByC_E_T(
		long companyId, boolean export, boolean template) {

		getPersistence().removeByC_E_T(companyId, export, template);
	}

	/**
	 * Returns the number of batch planner plans where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @return the number of matching batch planner plans
	 */
	public static int countByC_E_T(
		long companyId, boolean export, boolean template) {

		return getPersistence().countByC_E_T(companyId, export, template);
	}

	/**
	 * Returns the number of batch planner plans that the user has permission to view where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @return the number of matching batch planner plans that the user has permission to view
	 */
	public static int filterCountByC_E_T(
		long companyId, boolean export, boolean template) {

		return getPersistence().filterCountByC_E_T(companyId, export, template);
	}

	/**
	 * Creates a new batch planner plan with the primary key. Does not add the batch planner plan to the database.
	 *
	 * @param batchPlannerPlanId the primary key for the new batch planner plan
	 * @return the new batch planner plan
	 */
	public static BatchPlannerPlan create(long batchPlannerPlanId) {
		return getPersistence().create(batchPlannerPlanId);
	}

	/**
	 * Removes the batch planner plan with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param batchPlannerPlanId the primary key of the batch planner plan
	 * @return the batch planner plan that was removed
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	public static BatchPlannerPlan remove(long batchPlannerPlanId)
		throws com.liferay.batch.planner.exception.NoSuchPlanException {

		return getPersistence().remove(batchPlannerPlanId);
	}

	public static BatchPlannerPlan updateImpl(
		BatchPlannerPlan batchPlannerPlan) {

		return getPersistence().updateImpl(batchPlannerPlan);
	}

	/**
	 * Returns the batch planner plan with the primary key or throws a <code>NoSuchPlanException</code> if it could not be found.
	 *
	 * @param batchPlannerPlanId the primary key of the batch planner plan
	 * @return the batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	public static BatchPlannerPlan findByPrimaryKey(long batchPlannerPlanId)
		throws com.liferay.batch.planner.exception.NoSuchPlanException {

		return getPersistence().findByPrimaryKey(batchPlannerPlanId);
	}

	/**
	 * Returns the batch planner plan with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param batchPlannerPlanId the primary key of the batch planner plan
	 * @return the batch planner plan, or <code>null</code> if a batch planner plan with the primary key could not be found
	 */
	public static BatchPlannerPlan fetchByPrimaryKey(long batchPlannerPlanId) {
		return getPersistence().fetchByPrimaryKey(batchPlannerPlanId);
	}

	/**
	 * Returns all the batch planner plans where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the batch planner plans where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the batch planner plans that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching batch planner plans that the user has permission to view
	 */
	public static List<BatchPlannerPlan> filterFindByCompanyId(long companyId) {
		return getPersistence().filterFindByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the batch planner plans that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans that the user has permission to view
	 */
	public static List<BatchPlannerPlan> filterFindByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().filterFindByCompanyId(companyId, start, end);
	}

	/**
	 * Returns all the batch planner plans where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @return the matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByC_E(
		long companyId, boolean export) {

		return getPersistence().findByC_E(companyId, export);
	}

	/**
	 * Returns a range of all the batch planner plans where companyId = &#63; and export = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByC_E(
		long companyId, boolean export, int start, int end) {

		return getPersistence().findByC_E(companyId, export, start, end);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and export = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByC_E(
		long companyId, boolean export, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().findByC_E(
			companyId, export, start, end, orderByComparator);
	}

	/**
	 * Returns all the batch planner plans that the user has permission to view where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @return the matching batch planner plans that the user has permission to view
	 */
	public static List<BatchPlannerPlan> filterFindByC_E(
		long companyId, boolean export) {

		return getPersistence().filterFindByC_E(companyId, export);
	}

	/**
	 * Returns a range of all the batch planner plans that the user has permission to view where companyId = &#63; and export = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans that the user has permission to view
	 */
	public static List<BatchPlannerPlan> filterFindByC_E(
		long companyId, boolean export, int start, int end) {

		return getPersistence().filterFindByC_E(companyId, export, start, end);
	}

	/**
	 * Returns all the batch planner plans where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @return the matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByC_T(
		long companyId, boolean template) {

		return getPersistence().findByC_T(companyId, template);
	}

	/**
	 * Returns a range of all the batch planner plans where companyId = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByC_T(
		long companyId, boolean template, int start, int end) {

		return getPersistence().findByC_T(companyId, template, start, end);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByC_T(
		long companyId, boolean template, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().findByC_T(
			companyId, template, start, end, orderByComparator);
	}

	/**
	 * Returns all the batch planner plans that the user has permission to view where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @return the matching batch planner plans that the user has permission to view
	 */
	public static List<BatchPlannerPlan> filterFindByC_T(
		long companyId, boolean template) {

		return getPersistence().filterFindByC_T(companyId, template);
	}

	/**
	 * Returns a range of all the batch planner plans that the user has permission to view where companyId = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans that the user has permission to view
	 */
	public static List<BatchPlannerPlan> filterFindByC_T(
		long companyId, boolean template, int start, int end) {

		return getPersistence().filterFindByC_T(
			companyId, template, start, end);
	}

	/**
	 * Returns all the batch planner plans where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @return the matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByC_E_T(
		long companyId, boolean export, boolean template) {

		return getPersistence().findByC_E_T(companyId, export, template);
	}

	/**
	 * Returns a range of all the batch planner plans where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByC_E_T(
		long companyId, boolean export, boolean template, int start, int end) {

		return getPersistence().findByC_E_T(
			companyId, export, template, start, end);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans
	 */
	public static List<BatchPlannerPlan> findByC_E_T(
		long companyId, boolean export, boolean template, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return getPersistence().findByC_E_T(
			companyId, export, template, start, end, orderByComparator);
	}

	/**
	 * Returns all the batch planner plans that the user has permission to view where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @return the matching batch planner plans that the user has permission to view
	 */
	public static List<BatchPlannerPlan> filterFindByC_E_T(
		long companyId, boolean export, boolean template) {

		return getPersistence().filterFindByC_E_T(companyId, export, template);
	}

	/**
	 * Returns a range of all the batch planner plans that the user has permission to view where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans that the user has permission to view
	 */
	public static List<BatchPlannerPlan> filterFindByC_E_T(
		long companyId, boolean export, boolean template, int start, int end) {

		return getPersistence().filterFindByC_E_T(
			companyId, export, template, start, end);
	}

	public static BatchPlannerPlanPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(BatchPlannerPlanPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile BatchPlannerPlanPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-223400835