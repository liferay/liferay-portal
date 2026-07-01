/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.service.persistence.impl;

import com.liferay.batch.planner.exception.NoSuchPlanException;
import com.liferay.batch.planner.model.BatchPlannerPlan;
import com.liferay.batch.planner.model.BatchPlannerPlanTable;
import com.liferay.batch.planner.model.impl.BatchPlannerPlanImpl;
import com.liferay.batch.planner.model.impl.BatchPlannerPlanModelImpl;
import com.liferay.batch.planner.service.persistence.BatchPlannerPlanPersistence;
import com.liferay.batch.planner.service.persistence.BatchPlannerPlanUtil;
import com.liferay.batch.planner.service.persistence.impl.constants.BatchPlannerPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the batch planner plan service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Igor Beslic
 * @generated
 */
@Component(service = BatchPlannerPlanPersistence.class)
public class BatchPlannerPlanPersistenceImpl
	extends BasePersistenceImpl<BatchPlannerPlan, NoSuchPlanException>
	implements BatchPlannerPlanPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BatchPlannerPlanUtil</code> to access the batch planner plan persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BatchPlannerPlanImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<BatchPlannerPlan, NoSuchPlanException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan findByCompanyId_First(
			long companyId,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan fetchByCompanyId_First(
		long companyId, OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of batch planner plans where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching batch planner plans
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of batch planner plans that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching batch planner plans that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<BatchPlannerPlan, NoSuchPlanException>
			_collectionPersistenceFinderByC_U;

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U.find(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan findByC_U_First(
			long companyId, long userId,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		return _collectionPersistenceFinderByC_U.findFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return _collectionPersistenceFinderByC_U.fetchFirst(
			finderCache, new Object[] {companyId, userId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return _collectionPersistenceFinderByC_U.filterFind(
			finderCache, new Object[] {companyId, userId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		_collectionPersistenceFinderByC_U.remove(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the number of batch planner plans where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching batch planner plans
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.count(
			finderCache, new Object[] {companyId, userId});
	}

	/**
	 * Returns the number of batch planner plans that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching batch planner plans that the user has permission to view
	 */
	@Override
	public int filterCountByC_U(long companyId, long userId) {
		return _collectionPersistenceFinderByC_U.filterCount(
			finderCache, new Object[] {companyId, userId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<BatchPlannerPlan, NoSuchPlanException>
			_collectionPersistenceFinderByC_E;

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and export = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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
	@Override
	public List<BatchPlannerPlan> findByC_E(
		long companyId, boolean export, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_E.find(
			finderCache, new Object[] {companyId, export}, start, end,
			orderByComparator, useFinderCache);
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
	@Override
	public BatchPlannerPlan findByC_E_First(
			long companyId, boolean export,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		return _collectionPersistenceFinderByC_E.findFirst(
			finderCache, new Object[] {companyId, export}, orderByComparator);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan fetchByC_E_First(
		long companyId, boolean export,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return _collectionPersistenceFinderByC_E.fetchFirst(
			finderCache, new Object[] {companyId, export}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and export = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_E(
		long companyId, boolean export, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return _collectionPersistenceFinderByC_E.filterFind(
			finderCache, new Object[] {companyId, export}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; and export = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 */
	@Override
	public void removeByC_E(long companyId, boolean export) {
		_collectionPersistenceFinderByC_E.remove(
			finderCache, new Object[] {companyId, export});
	}

	/**
	 * Returns the number of batch planner plans where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @return the number of matching batch planner plans
	 */
	@Override
	public int countByC_E(long companyId, boolean export) {
		return _collectionPersistenceFinderByC_E.count(
			finderCache, new Object[] {companyId, export});
	}

	/**
	 * Returns the number of batch planner plans that the user has permission to view where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @return the number of matching batch planner plans that the user has permission to view
	 */
	@Override
	public int filterCountByC_E(long companyId, boolean export) {
		return _collectionPersistenceFinderByC_E.filterCount(
			finderCache, new Object[] {companyId, export}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<BatchPlannerPlan, NoSuchPlanException>
			_collectionPersistenceFinderByC_N;

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_N(
		long companyId, String name, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_N.find(
			finderCache, new Object[] {companyId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan findByC_N_First(
			long companyId, String name,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		return _collectionPersistenceFinderByC_N.findFirst(
			finderCache, new Object[] {companyId, name}, orderByComparator);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan fetchByC_N_First(
		long companyId, String name,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return _collectionPersistenceFinderByC_N.fetchFirst(
			finderCache, new Object[] {companyId, name}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_N(
		long companyId, String name, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return _collectionPersistenceFinderByC_N.filterFind(
			finderCache, new Object[] {companyId, name}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 */
	@Override
	public void removeByC_N(long companyId, String name) {
		_collectionPersistenceFinderByC_N.remove(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the number of batch planner plans where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching batch planner plans
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _collectionPersistenceFinderByC_N.count(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the number of batch planner plans that the user has permission to view where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching batch planner plans that the user has permission to view
	 */
	@Override
	public int filterCountByC_N(long companyId, String name) {
		return _collectionPersistenceFinderByC_N.filterCount(
			finderCache, new Object[] {companyId, name}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<BatchPlannerPlan, NoSuchPlanException>
			_collectionPersistenceFinderByC_T;

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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
	@Override
	public List<BatchPlannerPlan> findByC_T(
		long companyId, boolean template, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_T.find(
			finderCache, new Object[] {companyId, template}, start, end,
			orderByComparator, useFinderCache);
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
	@Override
	public BatchPlannerPlan findByC_T_First(
			long companyId, boolean template,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		return _collectionPersistenceFinderByC_T.findFirst(
			finderCache, new Object[] {companyId, template}, orderByComparator);
	}

	/**
	 * Returns the first batch planner plan in the ordered set where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan fetchByC_T_First(
		long companyId, boolean template,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return _collectionPersistenceFinderByC_T.fetchFirst(
			finderCache, new Object[] {companyId, template}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_T(
		long companyId, boolean template, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return _collectionPersistenceFinderByC_T.filterFind(
			finderCache, new Object[] {companyId, template}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; and template = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 */
	@Override
	public void removeByC_T(long companyId, boolean template) {
		_collectionPersistenceFinderByC_T.remove(
			finderCache, new Object[] {companyId, template});
	}

	/**
	 * Returns the number of batch planner plans where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @return the number of matching batch planner plans
	 */
	@Override
	public int countByC_T(long companyId, boolean template) {
		return _collectionPersistenceFinderByC_T.count(
			finderCache, new Object[] {companyId, template});
	}

	/**
	 * Returns the number of batch planner plans that the user has permission to view where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @return the number of matching batch planner plans that the user has permission to view
	 */
	@Override
	public int filterCountByC_T(long companyId, boolean template) {
		return _collectionPersistenceFinderByC_T.filterCount(
			finderCache, new Object[] {companyId, template}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<BatchPlannerPlan, NoSuchPlanException>
			_collectionPersistenceFinderByC_E_T;

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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
	@Override
	public List<BatchPlannerPlan> findByC_E_T(
		long companyId, boolean export, boolean template, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_E_T.find(
			finderCache, new Object[] {companyId, export, template}, start, end,
			orderByComparator, useFinderCache);
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
	@Override
	public BatchPlannerPlan findByC_E_T_First(
			long companyId, boolean export, boolean template,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		return _collectionPersistenceFinderByC_E_T.findFirst(
			finderCache, new Object[] {companyId, export, template},
			orderByComparator);
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
	@Override
	public BatchPlannerPlan fetchByC_E_T_First(
		long companyId, boolean export, boolean template,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return _collectionPersistenceFinderByC_E_T.fetchFirst(
			finderCache, new Object[] {companyId, export, template},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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
	@Override
	public List<BatchPlannerPlan> filterFindByC_E_T(
		long companyId, boolean export, boolean template, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return _collectionPersistenceFinderByC_E_T.filterFind(
			finderCache, new Object[] {companyId, export, template}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; and export = &#63; and template = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 */
	@Override
	public void removeByC_E_T(
		long companyId, boolean export, boolean template) {

		_collectionPersistenceFinderByC_E_T.remove(
			finderCache, new Object[] {companyId, export, template});
	}

	/**
	 * Returns the number of batch planner plans where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @return the number of matching batch planner plans
	 */
	@Override
	public int countByC_E_T(long companyId, boolean export, boolean template) {
		return _collectionPersistenceFinderByC_E_T.count(
			finderCache, new Object[] {companyId, export, template});
	}

	/**
	 * Returns the number of batch planner plans that the user has permission to view where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @return the number of matching batch planner plans that the user has permission to view
	 */
	@Override
	public int filterCountByC_E_T(
		long companyId, boolean export, boolean template) {

		return _collectionPersistenceFinderByC_E_T.filterCount(
			finderCache, new Object[] {companyId, export, template}, companyId,
			0);
	}

	public BatchPlannerPlanPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("active", "active_");
		dbColumnNames.put("size", "size_");

		setDBColumnNames(dbColumnNames);

		setModelClass(BatchPlannerPlan.class);

		setModelImplClass(BatchPlannerPlanImpl.class);
		setModelPKClass(long.class);

		setTable(BatchPlannerPlanTable.INSTANCE);
	}

	/**
	 * Creates a new batch planner plan with the primary key. Does not add the batch planner plan to the database.
	 *
	 * @param batchPlannerPlanId the primary key for the new batch planner plan
	 * @return the new batch planner plan
	 */
	@Override
	public BatchPlannerPlan create(long batchPlannerPlanId) {
		BatchPlannerPlan batchPlannerPlan = new BatchPlannerPlanImpl();

		batchPlannerPlan.setNew(true);
		batchPlannerPlan.setPrimaryKey(batchPlannerPlanId);

		batchPlannerPlan.setCompanyId(CompanyThreadLocal.getCompanyId());

		return batchPlannerPlan;
	}

	/**
	 * Removes the batch planner plan with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param batchPlannerPlanId the primary key of the batch planner plan
	 * @return the batch planner plan that was removed
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan remove(long batchPlannerPlanId)
		throws NoSuchPlanException {

		return remove((Serializable)batchPlannerPlanId);
	}

	@Override
	protected BatchPlannerPlan removeImpl(BatchPlannerPlan batchPlannerPlan) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(batchPlannerPlan)) {
				batchPlannerPlan = (BatchPlannerPlan)session.get(
					BatchPlannerPlanImpl.class,
					batchPlannerPlan.getPrimaryKeyObj());
			}

			if (batchPlannerPlan != null) {
				session.delete(batchPlannerPlan);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (batchPlannerPlan != null) {
			clearCache(batchPlannerPlan);
		}

		return batchPlannerPlan;
	}

	@Override
	public BatchPlannerPlan updateImpl(BatchPlannerPlan batchPlannerPlan) {
		boolean isNew = batchPlannerPlan.isNew();

		if (!(batchPlannerPlan instanceof BatchPlannerPlanModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(batchPlannerPlan.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					batchPlannerPlan);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in batchPlannerPlan proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BatchPlannerPlan implementation " +
					batchPlannerPlan.getClass());
		}

		BatchPlannerPlanModelImpl batchPlannerPlanModelImpl =
			(BatchPlannerPlanModelImpl)batchPlannerPlan;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (batchPlannerPlan.getCreateDate() == null)) {
			if (serviceContext == null) {
				batchPlannerPlan.setCreateDate(date);
			}
			else {
				batchPlannerPlan.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!batchPlannerPlanModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				batchPlannerPlan.setModifiedDate(date);
			}
			else {
				batchPlannerPlan.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(batchPlannerPlan);
			}
			else {
				batchPlannerPlan = (BatchPlannerPlan)session.merge(
					batchPlannerPlan);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(batchPlannerPlan, false);

		if (isNew) {
			batchPlannerPlan.setNew(false);
		}

		batchPlannerPlan.resetOriginalValues();

		return batchPlannerPlan;
	}

	/**
	 * Returns the batch planner plan with the primary key or throws a <code>NoSuchPlanException</code> if it could not be found.
	 *
	 * @param batchPlannerPlanId the primary key of the batch planner plan
	 * @return the batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan findByPrimaryKey(long batchPlannerPlanId)
		throws NoSuchPlanException {

		return findByPrimaryKey((Serializable)batchPlannerPlanId);
	}

	/**
	 * Returns the batch planner plan with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param batchPlannerPlanId the primary key of the batch planner plan
	 * @return the batch planner plan, or <code>null</code> if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan fetchByPrimaryKey(long batchPlannerPlanId) {
		return fetchByPrimaryKey((Serializable)batchPlannerPlanId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "batchPlannerPlanId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BATCHPLANNERPLAN;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BatchPlannerPlanModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the batch planner plan persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCompanyId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_BATCHPLANNERPLAN_WHERE,
				_SQL_COUNT_BATCHPLANNERPLAN_WHERE,
				BatchPlannerPlanModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"batchPlannerPlan.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, BatchPlannerPlan::getCompanyId));

		_collectionPersistenceFinderByC_U =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "userId"}, false),
				_SQL_SELECT_BATCHPLANNERPLAN_WHERE,
				_SQL_COUNT_BATCHPLANNERPLAN_WHERE,
				BatchPlannerPlanModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"batchPlannerPlan.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, BatchPlannerPlan::getCompanyId),
				new FinderColumn<>(
					"batchPlannerPlan.", "userId", FinderColumn.Type.LONG, "=",
					true, true, BatchPlannerPlan::getUserId));

		_collectionPersistenceFinderByC_E =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_E",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "export"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_E",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "export"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_E",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "export"}, false),
				_SQL_SELECT_BATCHPLANNERPLAN_WHERE,
				_SQL_COUNT_BATCHPLANNERPLAN_WHERE,
				BatchPlannerPlanModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"batchPlannerPlan.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, BatchPlannerPlan::getCompanyId),
				new FinderColumn<>(
					"batchPlannerPlan.", "export", FinderColumn.Type.BOOLEAN,
					"=", true, true, BatchPlannerPlan::isExport));

		_collectionPersistenceFinderByC_N =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "name"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "name"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "name"}, 0, 2, false, null),
				_SQL_SELECT_BATCHPLANNERPLAN_WHERE,
				_SQL_COUNT_BATCHPLANNERPLAN_WHERE,
				BatchPlannerPlanModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"batchPlannerPlan.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, BatchPlannerPlan::getCompanyId),
				new FinderColumn<>(
					"batchPlannerPlan.", "name", FinderColumn.Type.STRING, "=",
					true, true, BatchPlannerPlan::getName));

		_collectionPersistenceFinderByC_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "template"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "template"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "template"}, false),
				_SQL_SELECT_BATCHPLANNERPLAN_WHERE,
				_SQL_COUNT_BATCHPLANNERPLAN_WHERE,
				BatchPlannerPlanModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"batchPlannerPlan.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, BatchPlannerPlan::getCompanyId),
				new FinderColumn<>(
					"batchPlannerPlan.", "template", FinderColumn.Type.BOOLEAN,
					"=", true, true, BatchPlannerPlan::isTemplate));

		_collectionPersistenceFinderByC_E_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_E_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "export", "template"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_E_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"companyId", "export", "template"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_E_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"companyId", "export", "template"}, false),
				_SQL_SELECT_BATCHPLANNERPLAN_WHERE,
				_SQL_COUNT_BATCHPLANNERPLAN_WHERE,
				BatchPlannerPlanModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"batchPlannerPlan.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, BatchPlannerPlan::getCompanyId),
				new FinderColumn<>(
					"batchPlannerPlan.", "export", FinderColumn.Type.BOOLEAN,
					"=", true, true, BatchPlannerPlan::isExport),
				new FinderColumn<>(
					"batchPlannerPlan.", "template", FinderColumn.Type.BOOLEAN,
					"=", true, true, BatchPlannerPlan::isTemplate));

		BatchPlannerPlanUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BatchPlannerPlanUtil.setPersistence(null);

		entityCache.removeCache(BatchPlannerPlanImpl.class.getName());
	}

	@Override
	@Reference(
		target = BatchPlannerPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = BatchPlannerPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = BatchPlannerPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		BatchPlannerPlanModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_BATCHPLANNERPLAN =
		"SELECT batchPlannerPlan FROM BatchPlannerPlan batchPlannerPlan";

	private static final String _SQL_SELECT_BATCHPLANNERPLAN_WHERE =
		"SELECT batchPlannerPlan FROM BatchPlannerPlan batchPlannerPlan WHERE ";

	private static final String _SQL_COUNT_BATCHPLANNERPLAN_WHERE =
		"SELECT COUNT(batchPlannerPlan) FROM BatchPlannerPlan batchPlannerPlan WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BatchPlannerPlan exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"active", "size"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-134734332