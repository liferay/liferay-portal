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
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	extends BasePersistenceImpl<BatchPlannerPlan>
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

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the batch planner plans where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch planner plans where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByCompanyId;
				finderArgs = new Object[] {companyId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByCompanyId;
			finderArgs = new Object[] {
				companyId, start, end, orderByComparator
			};
		}

		List<BatchPlannerPlan> list = null;

		if (useFinderCache) {
			list = (List<BatchPlannerPlan>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (BatchPlannerPlan batchPlannerPlan : list) {
					if (companyId != batchPlannerPlan.getCompanyId()) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					3 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(3);
			}

			sb.append(_SQL_SELECT_BATCHPLANNERPLAN_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<BatchPlannerPlan>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
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

		BatchPlannerPlan batchPlannerPlan = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (batchPlannerPlan != null) {
			return batchPlannerPlan;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPlanException(sb.toString());
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

		List<BatchPlannerPlan> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last batch planner plan in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan findByCompanyId_Last(
			long companyId,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		BatchPlannerPlan batchPlannerPlan = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (batchPlannerPlan != null) {
			return batchPlannerPlan;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPlanException(sb.toString());
	}

	/**
	 * Returns the last batch planner plan in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan fetchByCompanyId_Last(
		long companyId, OrderByComparator<BatchPlannerPlan> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<BatchPlannerPlan> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the batch planner plans before and after the current batch planner plan in the ordered set where companyId = &#63;.
	 *
	 * @param batchPlannerPlanId the primary key of the current batch planner plan
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan[] findByCompanyId_PrevAndNext(
			long batchPlannerPlanId, long companyId,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		BatchPlannerPlan batchPlannerPlan = findByPrimaryKey(
			batchPlannerPlanId);

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan[] array = new BatchPlannerPlanImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, batchPlannerPlan, companyId, orderByComparator, true);

			array[1] = batchPlannerPlan;

			array[2] = getByCompanyId_PrevAndNext(
				session, batchPlannerPlan, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchPlannerPlan getByCompanyId_PrevAndNext(
		Session session, BatchPlannerPlan batchPlannerPlan, long companyId,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_BATCHPLANNERPLAN_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchPlannerPlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchPlannerPlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the batch planner plans that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByCompanyId(long companyId) {
		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch planner plans that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId(companyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					BatchPlannerPlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BatchPlannerPlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BatchPlannerPlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<BatchPlannerPlan>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the batch planner plans before and after the current batch planner plan in the ordered set of batch planner plans that the user has permission to view where companyId = &#63;.
	 *
	 * @param batchPlannerPlanId the primary key of the current batch planner plan
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan[] filterFindByCompanyId_PrevAndNext(
			long batchPlannerPlanId, long companyId,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				batchPlannerPlanId, companyId, orderByComparator);
		}

		BatchPlannerPlan batchPlannerPlan = findByPrimaryKey(
			batchPlannerPlanId);

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan[] array = new BatchPlannerPlanImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, batchPlannerPlan, companyId, orderByComparator, true);

			array[1] = batchPlannerPlan;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, batchPlannerPlan, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchPlannerPlan filterGetByCompanyId_PrevAndNext(
		Session session, BatchPlannerPlan batchPlannerPlan, long companyId,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					BatchPlannerPlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, BatchPlannerPlanImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, BatchPlannerPlanImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchPlannerPlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchPlannerPlan> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (BatchPlannerPlan batchPlannerPlan :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(batchPlannerPlan);
		}
	}

	/**
	 * Returns the number of batch planner plans where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching batch planner plans
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_BATCHPLANNERPLAN_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	/**
	 * Returns the number of batch planner plans that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching batch planner plans that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BatchPlannerPlan> batchPlannerPlans = findByCompanyId(
				companyId);

			batchPlannerPlans = InlineSQLHelperUtil.filter(batchPlannerPlans);

			return batchPlannerPlans.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_BATCHPLANNERPLAN_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"batchPlannerPlan.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByC_U;
	private FinderPath _finderPathWithoutPaginationFindByC_U;
	private FinderPath _finderPathCountByC_U;

	/**
	 * Returns all the batch planner plans where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_U(long companyId, long userId) {
		return findByC_U(
			companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch planner plans where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_U(
		long companyId, long userId, int start, int end) {

		return findByC_U(companyId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return findByC_U(
			companyId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_U;
				finderArgs = new Object[] {companyId, userId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_U;
			finderArgs = new Object[] {
				companyId, userId, start, end, orderByComparator
			};
		}

		List<BatchPlannerPlan> list = null;

		if (useFinderCache) {
			list = (List<BatchPlannerPlan>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (BatchPlannerPlan batchPlannerPlan : list) {
					if ((companyId != batchPlannerPlan.getCompanyId()) ||
						(userId != batchPlannerPlan.getUserId())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_BATCHPLANNERPLAN_WHERE);

			sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_U_USERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(userId);

				list = (List<BatchPlannerPlan>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
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

		BatchPlannerPlan batchPlannerPlan = fetchByC_U_First(
			companyId, userId, orderByComparator);

		if (batchPlannerPlan != null) {
			return batchPlannerPlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchPlanException(sb.toString());
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

		List<BatchPlannerPlan> list = findByC_U(
			companyId, userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last batch planner plan in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan findByC_U_Last(
			long companyId, long userId,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		BatchPlannerPlan batchPlannerPlan = fetchByC_U_Last(
			companyId, userId, orderByComparator);

		if (batchPlannerPlan != null) {
			return batchPlannerPlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchPlanException(sb.toString());
	}

	/**
	 * Returns the last batch planner plan in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan fetchByC_U_Last(
		long companyId, long userId,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		int count = countByC_U(companyId, userId);

		if (count == 0) {
			return null;
		}

		List<BatchPlannerPlan> list = findByC_U(
			companyId, userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the batch planner plans before and after the current batch planner plan in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param batchPlannerPlanId the primary key of the current batch planner plan
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan[] findByC_U_PrevAndNext(
			long batchPlannerPlanId, long companyId, long userId,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		BatchPlannerPlan batchPlannerPlan = findByPrimaryKey(
			batchPlannerPlanId);

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan[] array = new BatchPlannerPlanImpl[3];

			array[0] = getByC_U_PrevAndNext(
				session, batchPlannerPlan, companyId, userId, orderByComparator,
				true);

			array[1] = batchPlannerPlan;

			array[2] = getByC_U_PrevAndNext(
				session, batchPlannerPlan, companyId, userId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchPlannerPlan getByC_U_PrevAndNext(
		Session session, BatchPlannerPlan batchPlannerPlan, long companyId,
		long userId, OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BATCHPLANNERPLAN_WHERE);

		sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_U_USERID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchPlannerPlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchPlannerPlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the batch planner plans that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_U(long companyId, long userId) {
		return filterFindByC_U(
			companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch planner plans that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_U(
		long companyId, long userId, int start, int end) {

		return filterFindByC_U(companyId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_U(companyId, userId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_U(
					companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_U_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					BatchPlannerPlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BatchPlannerPlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BatchPlannerPlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(userId);

			return (List<BatchPlannerPlan>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the batch planner plans before and after the current batch planner plan in the ordered set of batch planner plans that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param batchPlannerPlanId the primary key of the current batch planner plan
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan[] filterFindByC_U_PrevAndNext(
			long batchPlannerPlanId, long companyId, long userId,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_U_PrevAndNext(
				batchPlannerPlanId, companyId, userId, orderByComparator);
		}

		BatchPlannerPlan batchPlannerPlan = findByPrimaryKey(
			batchPlannerPlanId);

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan[] array = new BatchPlannerPlanImpl[3];

			array[0] = filterGetByC_U_PrevAndNext(
				session, batchPlannerPlan, companyId, userId, orderByComparator,
				true);

			array[1] = batchPlannerPlan;

			array[2] = filterGetByC_U_PrevAndNext(
				session, batchPlannerPlan, companyId, userId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchPlannerPlan filterGetByC_U_PrevAndNext(
		Session session, BatchPlannerPlan batchPlannerPlan, long companyId,
		long userId, OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_U_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					BatchPlannerPlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, BatchPlannerPlanImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, BatchPlannerPlanImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchPlannerPlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchPlannerPlan> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		for (BatchPlannerPlan batchPlannerPlan :
				findByC_U(
					companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(batchPlannerPlan);
		}
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
		FinderPath finderPath = _finderPathCountByC_U;

		Object[] finderArgs = new Object[] {companyId, userId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_BATCHPLANNERPLAN_WHERE);

			sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_U_USERID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(userId);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
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
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_U(companyId, userId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BatchPlannerPlan> batchPlannerPlans = findByC_U(
				companyId, userId);

			batchPlannerPlans = InlineSQLHelperUtil.filter(batchPlannerPlans);

			return batchPlannerPlans.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_BATCHPLANNERPLAN_WHERE);

		sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_U_USERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(userId);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_C_U_COMPANYID_2 =
		"batchPlannerPlan.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_USERID_2 =
		"batchPlannerPlan.userId = ?";

	private FinderPath _finderPathWithPaginationFindByC_E;
	private FinderPath _finderPathWithoutPaginationFindByC_E;
	private FinderPath _finderPathCountByC_E;

	/**
	 * Returns all the batch planner plans where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @return the matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_E(long companyId, boolean export) {
		return findByC_E(
			companyId, export, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch planner plans where companyId = &#63; and export = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_E(
		long companyId, boolean export, int start, int end) {

		return findByC_E(companyId, export, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and export = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_E(
		long companyId, boolean export, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return findByC_E(
			companyId, export, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and export = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_E;
				finderArgs = new Object[] {companyId, export};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_E;
			finderArgs = new Object[] {
				companyId, export, start, end, orderByComparator
			};
		}

		List<BatchPlannerPlan> list = null;

		if (useFinderCache) {
			list = (List<BatchPlannerPlan>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (BatchPlannerPlan batchPlannerPlan : list) {
					if ((companyId != batchPlannerPlan.getCompanyId()) ||
						(export != batchPlannerPlan.isExport())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_BATCHPLANNERPLAN_WHERE);

			sb.append(_FINDER_COLUMN_C_E_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_E_EXPORT_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(export);

				list = (List<BatchPlannerPlan>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
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

		BatchPlannerPlan batchPlannerPlan = fetchByC_E_First(
			companyId, export, orderByComparator);

		if (batchPlannerPlan != null) {
			return batchPlannerPlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", export=");
		sb.append(export);

		sb.append("}");

		throw new NoSuchPlanException(sb.toString());
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

		List<BatchPlannerPlan> list = findByC_E(
			companyId, export, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last batch planner plan in the ordered set where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan findByC_E_Last(
			long companyId, boolean export,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		BatchPlannerPlan batchPlannerPlan = fetchByC_E_Last(
			companyId, export, orderByComparator);

		if (batchPlannerPlan != null) {
			return batchPlannerPlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", export=");
		sb.append(export);

		sb.append("}");

		throw new NoSuchPlanException(sb.toString());
	}

	/**
	 * Returns the last batch planner plan in the ordered set where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan fetchByC_E_Last(
		long companyId, boolean export,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		int count = countByC_E(companyId, export);

		if (count == 0) {
			return null;
		}

		List<BatchPlannerPlan> list = findByC_E(
			companyId, export, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the batch planner plans before and after the current batch planner plan in the ordered set where companyId = &#63; and export = &#63;.
	 *
	 * @param batchPlannerPlanId the primary key of the current batch planner plan
	 * @param companyId the company ID
	 * @param export the export
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan[] findByC_E_PrevAndNext(
			long batchPlannerPlanId, long companyId, boolean export,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		BatchPlannerPlan batchPlannerPlan = findByPrimaryKey(
			batchPlannerPlanId);

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan[] array = new BatchPlannerPlanImpl[3];

			array[0] = getByC_E_PrevAndNext(
				session, batchPlannerPlan, companyId, export, orderByComparator,
				true);

			array[1] = batchPlannerPlan;

			array[2] = getByC_E_PrevAndNext(
				session, batchPlannerPlan, companyId, export, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchPlannerPlan getByC_E_PrevAndNext(
		Session session, BatchPlannerPlan batchPlannerPlan, long companyId,
		boolean export, OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BATCHPLANNERPLAN_WHERE);

		sb.append(_FINDER_COLUMN_C_E_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_E_EXPORT_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(export);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchPlannerPlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchPlannerPlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the batch planner plans that the user has permission to view where companyId = &#63; and export = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @return the matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_E(
		long companyId, boolean export) {

		return filterFindByC_E(
			companyId, export, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch planner plans that the user has permission to view where companyId = &#63; and export = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_E(
		long companyId, boolean export, int start, int end) {

		return filterFindByC_E(companyId, export, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and export = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_E(companyId, export, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_E(
					companyId, export, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_E_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_E_EXPORT_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					BatchPlannerPlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BatchPlannerPlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BatchPlannerPlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(export);

			return (List<BatchPlannerPlan>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the batch planner plans before and after the current batch planner plan in the ordered set of batch planner plans that the user has permission to view where companyId = &#63; and export = &#63;.
	 *
	 * @param batchPlannerPlanId the primary key of the current batch planner plan
	 * @param companyId the company ID
	 * @param export the export
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan[] filterFindByC_E_PrevAndNext(
			long batchPlannerPlanId, long companyId, boolean export,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_E_PrevAndNext(
				batchPlannerPlanId, companyId, export, orderByComparator);
		}

		BatchPlannerPlan batchPlannerPlan = findByPrimaryKey(
			batchPlannerPlanId);

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan[] array = new BatchPlannerPlanImpl[3];

			array[0] = filterGetByC_E_PrevAndNext(
				session, batchPlannerPlan, companyId, export, orderByComparator,
				true);

			array[1] = batchPlannerPlan;

			array[2] = filterGetByC_E_PrevAndNext(
				session, batchPlannerPlan, companyId, export, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchPlannerPlan filterGetByC_E_PrevAndNext(
		Session session, BatchPlannerPlan batchPlannerPlan, long companyId,
		boolean export, OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_E_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_E_EXPORT_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					BatchPlannerPlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, BatchPlannerPlanImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, BatchPlannerPlanImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(export);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchPlannerPlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchPlannerPlan> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; and export = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 */
	@Override
	public void removeByC_E(long companyId, boolean export) {
		for (BatchPlannerPlan batchPlannerPlan :
				findByC_E(
					companyId, export, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(batchPlannerPlan);
		}
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
		FinderPath finderPath = _finderPathCountByC_E;

		Object[] finderArgs = new Object[] {companyId, export};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_BATCHPLANNERPLAN_WHERE);

			sb.append(_FINDER_COLUMN_C_E_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_E_EXPORT_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(export);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
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
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_E(companyId, export);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BatchPlannerPlan> batchPlannerPlans = findByC_E(
				companyId, export);

			batchPlannerPlans = InlineSQLHelperUtil.filter(batchPlannerPlans);

			return batchPlannerPlans.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_BATCHPLANNERPLAN_WHERE);

		sb.append(_FINDER_COLUMN_C_E_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_E_EXPORT_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(export);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_C_E_COMPANYID_2 =
		"batchPlannerPlan.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_E_EXPORT_2 =
		"batchPlannerPlan.export = ?";

	private FinderPath _finderPathWithPaginationFindByC_N;
	private FinderPath _finderPathWithoutPaginationFindByC_N;
	private FinderPath _finderPathCountByC_N;

	/**
	 * Returns all the batch planner plans where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_N(long companyId, String name) {
		return findByC_N(
			companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch planner plans where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_N(
		long companyId, String name, int start, int end) {

		return findByC_N(companyId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_N(
		long companyId, String name, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return findByC_N(companyId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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

		name = Objects.toString(name, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_N;
				finderArgs = new Object[] {companyId, name};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_N;
			finderArgs = new Object[] {
				companyId, name, start, end, orderByComparator
			};
		}

		List<BatchPlannerPlan> list = null;

		if (useFinderCache) {
			list = (List<BatchPlannerPlan>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (BatchPlannerPlan batchPlannerPlan : list) {
					if ((companyId != batchPlannerPlan.getCompanyId()) ||
						!name.equals(batchPlannerPlan.getName())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_BATCHPLANNERPLAN_WHERE);

			sb.append(_FINDER_COLUMN_C_N_COMPANYID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_C_N_NAME_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindName) {
					queryPos.add(name);
				}

				list = (List<BatchPlannerPlan>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
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

		BatchPlannerPlan batchPlannerPlan = fetchByC_N_First(
			companyId, name, orderByComparator);

		if (batchPlannerPlan != null) {
			return batchPlannerPlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchPlanException(sb.toString());
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

		List<BatchPlannerPlan> list = findByC_N(
			companyId, name, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last batch planner plan in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan findByC_N_Last(
			long companyId, String name,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		BatchPlannerPlan batchPlannerPlan = fetchByC_N_Last(
			companyId, name, orderByComparator);

		if (batchPlannerPlan != null) {
			return batchPlannerPlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", name=");
		sb.append(name);

		sb.append("}");

		throw new NoSuchPlanException(sb.toString());
	}

	/**
	 * Returns the last batch planner plan in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan fetchByC_N_Last(
		long companyId, String name,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		int count = countByC_N(companyId, name);

		if (count == 0) {
			return null;
		}

		List<BatchPlannerPlan> list = findByC_N(
			companyId, name, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the batch planner plans before and after the current batch planner plan in the ordered set where companyId = &#63; and name = &#63;.
	 *
	 * @param batchPlannerPlanId the primary key of the current batch planner plan
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan[] findByC_N_PrevAndNext(
			long batchPlannerPlanId, long companyId, String name,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		name = Objects.toString(name, "");

		BatchPlannerPlan batchPlannerPlan = findByPrimaryKey(
			batchPlannerPlanId);

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan[] array = new BatchPlannerPlanImpl[3];

			array[0] = getByC_N_PrevAndNext(
				session, batchPlannerPlan, companyId, name, orderByComparator,
				true);

			array[1] = batchPlannerPlan;

			array[2] = getByC_N_PrevAndNext(
				session, batchPlannerPlan, companyId, name, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchPlannerPlan getByC_N_PrevAndNext(
		Session session, BatchPlannerPlan batchPlannerPlan, long companyId,
		String name, OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BATCHPLANNERPLAN_WHERE);

		sb.append(_FINDER_COLUMN_C_N_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_NAME_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindName) {
			queryPos.add(name);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchPlannerPlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchPlannerPlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the batch planner plans that the user has permission to view where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_N(long companyId, String name) {
		return filterFindByC_N(
			companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch planner plans that the user has permission to view where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_N(
		long companyId, String name, int start, int end) {

		return filterFindByC_N(companyId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_N(companyId, name, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_N(
					companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		name = Objects.toString(name, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_N_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_NAME_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					BatchPlannerPlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BatchPlannerPlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BatchPlannerPlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindName) {
				queryPos.add(name);
			}

			return (List<BatchPlannerPlan>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the batch planner plans before and after the current batch planner plan in the ordered set of batch planner plans that the user has permission to view where companyId = &#63; and name = &#63;.
	 *
	 * @param batchPlannerPlanId the primary key of the current batch planner plan
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan[] filterFindByC_N_PrevAndNext(
			long batchPlannerPlanId, long companyId, String name,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_N_PrevAndNext(
				batchPlannerPlanId, companyId, name, orderByComparator);
		}

		name = Objects.toString(name, "");

		BatchPlannerPlan batchPlannerPlan = findByPrimaryKey(
			batchPlannerPlanId);

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan[] array = new BatchPlannerPlanImpl[3];

			array[0] = filterGetByC_N_PrevAndNext(
				session, batchPlannerPlan, companyId, name, orderByComparator,
				true);

			array[1] = batchPlannerPlan;

			array[2] = filterGetByC_N_PrevAndNext(
				session, batchPlannerPlan, companyId, name, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchPlannerPlan filterGetByC_N_PrevAndNext(
		Session session, BatchPlannerPlan batchPlannerPlan, long companyId,
		String name, OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_N_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_NAME_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					BatchPlannerPlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, BatchPlannerPlanImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, BatchPlannerPlanImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (bindName) {
			queryPos.add(name);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchPlannerPlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchPlannerPlan> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 */
	@Override
	public void removeByC_N(long companyId, String name) {
		for (BatchPlannerPlan batchPlannerPlan :
				findByC_N(
					companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(batchPlannerPlan);
		}
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
		name = Objects.toString(name, "");

		FinderPath finderPath = _finderPathCountByC_N;

		Object[] finderArgs = new Object[] {companyId, name};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_BATCHPLANNERPLAN_WHERE);

			sb.append(_FINDER_COLUMN_C_N_COMPANYID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_N_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_C_N_NAME_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindName) {
					queryPos.add(name);
				}

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
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
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_N(companyId, name);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BatchPlannerPlan> batchPlannerPlans = findByC_N(
				companyId, name);

			batchPlannerPlans = InlineSQLHelperUtil.filter(batchPlannerPlans);

			return batchPlannerPlans.size();
		}

		name = Objects.toString(name, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_BATCHPLANNERPLAN_WHERE);

		sb.append(_FINDER_COLUMN_C_N_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_N_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_N_NAME_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindName) {
				queryPos.add(name);
			}

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_C_N_COMPANYID_2 =
		"batchPlannerPlan.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_NAME_2 =
		"batchPlannerPlan.name = ?";

	private static final String _FINDER_COLUMN_C_N_NAME_3 =
		"(batchPlannerPlan.name IS NULL OR batchPlannerPlan.name = '')";

	private FinderPath _finderPathWithPaginationFindByC_T;
	private FinderPath _finderPathWithoutPaginationFindByC_T;
	private FinderPath _finderPathCountByC_T;

	/**
	 * Returns all the batch planner plans where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @return the matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_T(long companyId, boolean template) {
		return findByC_T(
			companyId, template, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch planner plans where companyId = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_T(
		long companyId, boolean template, int start, int end) {

		return findByC_T(companyId, template, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_T(
		long companyId, boolean template, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return findByC_T(
			companyId, template, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_T;
				finderArgs = new Object[] {companyId, template};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_T;
			finderArgs = new Object[] {
				companyId, template, start, end, orderByComparator
			};
		}

		List<BatchPlannerPlan> list = null;

		if (useFinderCache) {
			list = (List<BatchPlannerPlan>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (BatchPlannerPlan batchPlannerPlan : list) {
					if ((companyId != batchPlannerPlan.getCompanyId()) ||
						(template != batchPlannerPlan.isTemplate())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					4 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(4);
			}

			sb.append(_SQL_SELECT_BATCHPLANNERPLAN_WHERE);

			sb.append(_FINDER_COLUMN_C_T_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_T_TEMPLATE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(template);

				list = (List<BatchPlannerPlan>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
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

		BatchPlannerPlan batchPlannerPlan = fetchByC_T_First(
			companyId, template, orderByComparator);

		if (batchPlannerPlan != null) {
			return batchPlannerPlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", template=");
		sb.append(template);

		sb.append("}");

		throw new NoSuchPlanException(sb.toString());
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

		List<BatchPlannerPlan> list = findByC_T(
			companyId, template, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last batch planner plan in the ordered set where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan findByC_T_Last(
			long companyId, boolean template,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		BatchPlannerPlan batchPlannerPlan = fetchByC_T_Last(
			companyId, template, orderByComparator);

		if (batchPlannerPlan != null) {
			return batchPlannerPlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", template=");
		sb.append(template);

		sb.append("}");

		throw new NoSuchPlanException(sb.toString());
	}

	/**
	 * Returns the last batch planner plan in the ordered set where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan fetchByC_T_Last(
		long companyId, boolean template,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		int count = countByC_T(companyId, template);

		if (count == 0) {
			return null;
		}

		List<BatchPlannerPlan> list = findByC_T(
			companyId, template, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the batch planner plans before and after the current batch planner plan in the ordered set where companyId = &#63; and template = &#63;.
	 *
	 * @param batchPlannerPlanId the primary key of the current batch planner plan
	 * @param companyId the company ID
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan[] findByC_T_PrevAndNext(
			long batchPlannerPlanId, long companyId, boolean template,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		BatchPlannerPlan batchPlannerPlan = findByPrimaryKey(
			batchPlannerPlanId);

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan[] array = new BatchPlannerPlanImpl[3];

			array[0] = getByC_T_PrevAndNext(
				session, batchPlannerPlan, companyId, template,
				orderByComparator, true);

			array[1] = batchPlannerPlan;

			array[2] = getByC_T_PrevAndNext(
				session, batchPlannerPlan, companyId, template,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchPlannerPlan getByC_T_PrevAndNext(
		Session session, BatchPlannerPlan batchPlannerPlan, long companyId,
		boolean template, OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BATCHPLANNERPLAN_WHERE);

		sb.append(_FINDER_COLUMN_C_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_T_TEMPLATE_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(template);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchPlannerPlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchPlannerPlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the batch planner plans that the user has permission to view where companyId = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @return the matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_T(
		long companyId, boolean template) {

		return filterFindByC_T(
			companyId, template, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch planner plans that the user has permission to view where companyId = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_T(
		long companyId, boolean template, int start, int end) {

		return filterFindByC_T(companyId, template, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_T(
				companyId, template, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_T(
					companyId, template, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_T_TEMPLATE_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					BatchPlannerPlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BatchPlannerPlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BatchPlannerPlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(template);

			return (List<BatchPlannerPlan>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the batch planner plans before and after the current batch planner plan in the ordered set of batch planner plans that the user has permission to view where companyId = &#63; and template = &#63;.
	 *
	 * @param batchPlannerPlanId the primary key of the current batch planner plan
	 * @param companyId the company ID
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan[] filterFindByC_T_PrevAndNext(
			long batchPlannerPlanId, long companyId, boolean template,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_T_PrevAndNext(
				batchPlannerPlanId, companyId, template, orderByComparator);
		}

		BatchPlannerPlan batchPlannerPlan = findByPrimaryKey(
			batchPlannerPlanId);

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan[] array = new BatchPlannerPlanImpl[3];

			array[0] = filterGetByC_T_PrevAndNext(
				session, batchPlannerPlan, companyId, template,
				orderByComparator, true);

			array[1] = batchPlannerPlan;

			array[2] = filterGetByC_T_PrevAndNext(
				session, batchPlannerPlan, companyId, template,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchPlannerPlan filterGetByC_T_PrevAndNext(
		Session session, BatchPlannerPlan batchPlannerPlan, long companyId,
		boolean template, OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_T_TEMPLATE_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					BatchPlannerPlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, BatchPlannerPlanImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, BatchPlannerPlanImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(template);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchPlannerPlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchPlannerPlan> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the batch planner plans where companyId = &#63; and template = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param template the template
	 */
	@Override
	public void removeByC_T(long companyId, boolean template) {
		for (BatchPlannerPlan batchPlannerPlan :
				findByC_T(
					companyId, template, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(batchPlannerPlan);
		}
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
		FinderPath finderPath = _finderPathCountByC_T;

		Object[] finderArgs = new Object[] {companyId, template};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_BATCHPLANNERPLAN_WHERE);

			sb.append(_FINDER_COLUMN_C_T_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_T_TEMPLATE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(template);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
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
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_T(companyId, template);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BatchPlannerPlan> batchPlannerPlans = findByC_T(
				companyId, template);

			batchPlannerPlans = InlineSQLHelperUtil.filter(batchPlannerPlans);

			return batchPlannerPlans.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_BATCHPLANNERPLAN_WHERE);

		sb.append(_FINDER_COLUMN_C_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_T_TEMPLATE_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(template);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_C_T_COMPANYID_2 =
		"batchPlannerPlan.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_T_TEMPLATE_2 =
		"batchPlannerPlan.template = ?";

	private FinderPath _finderPathWithPaginationFindByC_E_T;
	private FinderPath _finderPathWithoutPaginationFindByC_E_T;
	private FinderPath _finderPathCountByC_E_T;

	/**
	 * Returns all the batch planner plans where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @return the matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_E_T(
		long companyId, boolean export, boolean template) {

		return findByC_E_T(
			companyId, export, template, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the batch planner plans where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findByC_E_T(
		long companyId, boolean export, boolean template, int start, int end) {

		return findByC_E_T(companyId, export, template, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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
	@Override
	public List<BatchPlannerPlan> findByC_E_T(
		long companyId, boolean export, boolean template, int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return findByC_E_T(
			companyId, export, template, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the batch planner plans where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_E_T;
				finderArgs = new Object[] {companyId, export, template};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_E_T;
			finderArgs = new Object[] {
				companyId, export, template, start, end, orderByComparator
			};
		}

		List<BatchPlannerPlan> list = null;

		if (useFinderCache) {
			list = (List<BatchPlannerPlan>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (BatchPlannerPlan batchPlannerPlan : list) {
					if ((companyId != batchPlannerPlan.getCompanyId()) ||
						(export != batchPlannerPlan.isExport()) ||
						(template != batchPlannerPlan.isTemplate())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					5 + (orderByComparator.getOrderByFields().length * 2));
			}
			else {
				sb = new StringBundler(5);
			}

			sb.append(_SQL_SELECT_BATCHPLANNERPLAN_WHERE);

			sb.append(_FINDER_COLUMN_C_E_T_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_E_T_EXPORT_2);

			sb.append(_FINDER_COLUMN_C_E_T_TEMPLATE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(export);

				queryPos.add(template);

				list = (List<BatchPlannerPlan>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
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

		BatchPlannerPlan batchPlannerPlan = fetchByC_E_T_First(
			companyId, export, template, orderByComparator);

		if (batchPlannerPlan != null) {
			return batchPlannerPlan;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", export=");
		sb.append(export);

		sb.append(", template=");
		sb.append(template);

		sb.append("}");

		throw new NoSuchPlanException(sb.toString());
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

		List<BatchPlannerPlan> list = findByC_E_T(
			companyId, export, template, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last batch planner plan in the ordered set where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch planner plan
	 * @throws NoSuchPlanException if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan findByC_E_T_Last(
			long companyId, boolean export, boolean template,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		BatchPlannerPlan batchPlannerPlan = fetchByC_E_T_Last(
			companyId, export, template, orderByComparator);

		if (batchPlannerPlan != null) {
			return batchPlannerPlan;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", export=");
		sb.append(export);

		sb.append(", template=");
		sb.append(template);

		sb.append("}");

		throw new NoSuchPlanException(sb.toString());
	}

	/**
	 * Returns the last batch planner plan in the ordered set where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching batch planner plan, or <code>null</code> if a matching batch planner plan could not be found
	 */
	@Override
	public BatchPlannerPlan fetchByC_E_T_Last(
		long companyId, boolean export, boolean template,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		int count = countByC_E_T(companyId, export, template);

		if (count == 0) {
			return null;
		}

		List<BatchPlannerPlan> list = findByC_E_T(
			companyId, export, template, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the batch planner plans before and after the current batch planner plan in the ordered set where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param batchPlannerPlanId the primary key of the current batch planner plan
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan[] findByC_E_T_PrevAndNext(
			long batchPlannerPlanId, long companyId, boolean export,
			boolean template,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		BatchPlannerPlan batchPlannerPlan = findByPrimaryKey(
			batchPlannerPlanId);

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan[] array = new BatchPlannerPlanImpl[3];

			array[0] = getByC_E_T_PrevAndNext(
				session, batchPlannerPlan, companyId, export, template,
				orderByComparator, true);

			array[1] = batchPlannerPlan;

			array[2] = getByC_E_T_PrevAndNext(
				session, batchPlannerPlan, companyId, export, template,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchPlannerPlan getByC_E_T_PrevAndNext(
		Session session, BatchPlannerPlan batchPlannerPlan, long companyId,
		boolean export, boolean template,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_BATCHPLANNERPLAN_WHERE);

		sb.append(_FINDER_COLUMN_C_E_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_E_T_EXPORT_2);

		sb.append(_FINDER_COLUMN_C_E_T_TEMPLATE_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(export);

		queryPos.add(template);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchPlannerPlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchPlannerPlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the batch planner plans that the user has permission to view where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @return the matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_E_T(
		long companyId, boolean export, boolean template) {

		return filterFindByC_E_T(
			companyId, export, template, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the batch planner plans that the user has permission to view where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of matching batch planner plans that the user has permission to view
	 */
	@Override
	public List<BatchPlannerPlan> filterFindByC_E_T(
		long companyId, boolean export, boolean template, int start, int end) {

		return filterFindByC_E_T(companyId, export, template, start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans that the user has permissions to view where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
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

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_E_T(
				companyId, export, template, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_E_T(
					companyId, export, template, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_E_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_E_T_EXPORT_2);

		sb.append(_FINDER_COLUMN_C_E_T_TEMPLATE_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					BatchPlannerPlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BatchPlannerPlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BatchPlannerPlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(export);

			queryPos.add(template);

			return (List<BatchPlannerPlan>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the batch planner plans before and after the current batch planner plan in the ordered set of batch planner plans that the user has permission to view where companyId = &#63; and export = &#63; and template = &#63;.
	 *
	 * @param batchPlannerPlanId the primary key of the current batch planner plan
	 * @param companyId the company ID
	 * @param export the export
	 * @param template the template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan[] filterFindByC_E_T_PrevAndNext(
			long batchPlannerPlanId, long companyId, boolean export,
			boolean template,
			OrderByComparator<BatchPlannerPlan> orderByComparator)
		throws NoSuchPlanException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_E_T_PrevAndNext(
				batchPlannerPlanId, companyId, export, template,
				orderByComparator);
		}

		BatchPlannerPlan batchPlannerPlan = findByPrimaryKey(
			batchPlannerPlanId);

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan[] array = new BatchPlannerPlanImpl[3];

			array[0] = filterGetByC_E_T_PrevAndNext(
				session, batchPlannerPlan, companyId, export, template,
				orderByComparator, true);

			array[1] = batchPlannerPlan;

			array[2] = filterGetByC_E_T_PrevAndNext(
				session, batchPlannerPlan, companyId, export, template,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BatchPlannerPlan filterGetByC_E_T_PrevAndNext(
		Session session, BatchPlannerPlan batchPlannerPlan, long companyId,
		boolean export, boolean template,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_E_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_E_T_EXPORT_2);

		sb.append(_FINDER_COLUMN_C_E_T_TEMPLATE_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(
					BatchPlannerPlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BatchPlannerPlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, BatchPlannerPlanImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, BatchPlannerPlanImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(export);

		queryPos.add(template);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						batchPlannerPlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BatchPlannerPlan> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
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

		for (BatchPlannerPlan batchPlannerPlan :
				findByC_E_T(
					companyId, export, template, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(batchPlannerPlan);
		}
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
		FinderPath finderPath = _finderPathCountByC_E_T;

		Object[] finderArgs = new Object[] {companyId, export, template};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_BATCHPLANNERPLAN_WHERE);

			sb.append(_FINDER_COLUMN_C_E_T_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_E_T_EXPORT_2);

			sb.append(_FINDER_COLUMN_C_E_T_TEMPLATE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(export);

				queryPos.add(template);

				count = (Long)query.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
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

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_E_T(companyId, export, template);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<BatchPlannerPlan> batchPlannerPlans = findByC_E_T(
				companyId, export, template);

			batchPlannerPlans = InlineSQLHelperUtil.filter(batchPlannerPlans);

			return batchPlannerPlans.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BATCHPLANNERPLAN_WHERE);

		sb.append(_FINDER_COLUMN_C_E_T_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_E_T_EXPORT_2);

		sb.append(_FINDER_COLUMN_C_E_T_TEMPLATE_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BatchPlannerPlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(export);

			queryPos.add(template);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_C_E_T_COMPANYID_2 =
		"batchPlannerPlan.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_E_T_EXPORT_2 =
		"batchPlannerPlan.export = ? AND ";

	private static final String _FINDER_COLUMN_C_E_T_TEMPLATE_2 =
		"batchPlannerPlan.template = ?";

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
	 * Caches the batch planner plan in the entity cache if it is enabled.
	 *
	 * @param batchPlannerPlan the batch planner plan
	 */
	@Override
	public void cacheResult(BatchPlannerPlan batchPlannerPlan) {
		entityCache.putResult(
			BatchPlannerPlanImpl.class, batchPlannerPlan.getPrimaryKey(),
			batchPlannerPlan);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the batch planner plans in the entity cache if it is enabled.
	 *
	 * @param batchPlannerPlans the batch planner plans
	 */
	@Override
	public void cacheResult(List<BatchPlannerPlan> batchPlannerPlans) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (batchPlannerPlans.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (BatchPlannerPlan batchPlannerPlan : batchPlannerPlans) {
			if (entityCache.getResult(
					BatchPlannerPlanImpl.class,
					batchPlannerPlan.getPrimaryKey()) == null) {

				cacheResult(batchPlannerPlan);
			}
		}
	}

	/**
	 * Clears the cache for all batch planner plans.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(BatchPlannerPlanImpl.class);

		finderCache.clearCache(BatchPlannerPlanImpl.class);
	}

	/**
	 * Clears the cache for the batch planner plan.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(BatchPlannerPlan batchPlannerPlan) {
		entityCache.removeResult(BatchPlannerPlanImpl.class, batchPlannerPlan);
	}

	@Override
	public void clearCache(List<BatchPlannerPlan> batchPlannerPlans) {
		for (BatchPlannerPlan batchPlannerPlan : batchPlannerPlans) {
			entityCache.removeResult(
				BatchPlannerPlanImpl.class, batchPlannerPlan);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(BatchPlannerPlanImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(BatchPlannerPlanImpl.class, primaryKey);
		}
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

	/**
	 * Removes the batch planner plan with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the batch planner plan
	 * @return the batch planner plan that was removed
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan remove(Serializable primaryKey)
		throws NoSuchPlanException {

		Session session = null;

		try {
			session = openSession();

			BatchPlannerPlan batchPlannerPlan = (BatchPlannerPlan)session.get(
				BatchPlannerPlanImpl.class, primaryKey);

			if (batchPlannerPlan == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPlanException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(batchPlannerPlan);
		}
		catch (NoSuchPlanException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
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

		entityCache.putResult(
			BatchPlannerPlanImpl.class, batchPlannerPlanModelImpl, false, true);

		if (isNew) {
			batchPlannerPlan.setNew(false);
		}

		batchPlannerPlan.resetOriginalValues();

		return batchPlannerPlan;
	}

	/**
	 * Returns the batch planner plan with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the batch planner plan
	 * @return the batch planner plan
	 * @throws NoSuchPlanException if a batch planner plan with the primary key could not be found
	 */
	@Override
	public BatchPlannerPlan findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPlanException {

		BatchPlannerPlan batchPlannerPlan = fetchByPrimaryKey(primaryKey);

		if (batchPlannerPlan == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPlanException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

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

	/**
	 * Returns all the batch planner plans.
	 *
	 * @return the batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the batch planner plans.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @return the range of batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the batch planner plans.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findAll(
		int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the batch planner plans.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BatchPlannerPlanModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of batch planner plans
	 * @param end the upper bound of the range of batch planner plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of batch planner plans
	 */
	@Override
	public List<BatchPlannerPlan> findAll(
		int start, int end,
		OrderByComparator<BatchPlannerPlan> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<BatchPlannerPlan> list = null;

		if (useFinderCache) {
			list = (List<BatchPlannerPlan>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_BATCHPLANNERPLAN);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_BATCHPLANNERPLAN;

				sql = sql.concat(BatchPlannerPlanModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<BatchPlannerPlan>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the batch planner plans from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (BatchPlannerPlan batchPlannerPlan : findAll()) {
			remove(batchPlannerPlan);
		}
	}

	/**
	 * Returns the number of batch planner plans.
	 *
	 * @return the number of batch planner plans
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_BATCHPLANNERPLAN);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
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
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId"}, true);

		_finderPathWithoutPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			true);

		_finderPathCountByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			false);

		_finderPathWithPaginationFindByC_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "userId"}, true);

		_finderPathWithoutPaginationFindByC_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "userId"}, true);

		_finderPathCountByC_U = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "userId"}, false);

		_finderPathWithPaginationFindByC_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_E",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "export"}, true);

		_finderPathWithoutPaginationFindByC_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_E",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "export"}, true);

		_finderPathCountByC_E = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_E",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "export"}, false);

		_finderPathWithPaginationFindByC_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_N",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "name"}, true);

		_finderPathWithoutPaginationFindByC_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "name"}, true);

		_finderPathCountByC_N = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "name"}, false);

		_finderPathWithPaginationFindByC_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "template"}, true);

		_finderPathWithoutPaginationFindByC_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_T",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "template"}, true);

		_finderPathCountByC_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_T",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "template"}, false);

		_finderPathWithPaginationFindByC_E_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_E_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "export", "template"}, true);

		_finderPathWithoutPaginationFindByC_E_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_E_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"companyId", "export", "template"}, true);

		_finderPathCountByC_E_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_E_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"companyId", "export", "template"}, false);

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

	private static final String _SQL_SELECT_BATCHPLANNERPLAN =
		"SELECT batchPlannerPlan FROM BatchPlannerPlan batchPlannerPlan";

	private static final String _SQL_SELECT_BATCHPLANNERPLAN_WHERE =
		"SELECT batchPlannerPlan FROM BatchPlannerPlan batchPlannerPlan WHERE ";

	private static final String _SQL_COUNT_BATCHPLANNERPLAN =
		"SELECT COUNT(batchPlannerPlan) FROM BatchPlannerPlan batchPlannerPlan";

	private static final String _SQL_COUNT_BATCHPLANNERPLAN_WHERE =
		"SELECT COUNT(batchPlannerPlan) FROM BatchPlannerPlan batchPlannerPlan WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"batchPlannerPlan.batchPlannerPlanId";

	private static final String _FILTER_SQL_SELECT_BATCHPLANNERPLAN_WHERE =
		"SELECT DISTINCT {batchPlannerPlan.*} FROM BatchPlannerPlan batchPlannerPlan WHERE ";

	private static final String
		_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {BatchPlannerPlan.*} FROM (SELECT DISTINCT batchPlannerPlan.batchPlannerPlanId FROM BatchPlannerPlan batchPlannerPlan WHERE ";

	private static final String
		_FILTER_SQL_SELECT_BATCHPLANNERPLAN_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN BatchPlannerPlan ON TEMP_TABLE.batchPlannerPlanId = BatchPlannerPlan.batchPlannerPlanId";

	private static final String _FILTER_SQL_COUNT_BATCHPLANNERPLAN_WHERE =
		"SELECT COUNT(DISTINCT batchPlannerPlan.batchPlannerPlanId) AS COUNT_VALUE FROM BatchPlannerPlan batchPlannerPlan WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "batchPlannerPlan";

	private static final String _FILTER_ENTITY_TABLE = "BatchPlannerPlan";

	private static final String _ORDER_BY_ENTITY_ALIAS = "batchPlannerPlan.";

	private static final String _ORDER_BY_ENTITY_TABLE = "BatchPlannerPlan.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No BatchPlannerPlan exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BatchPlannerPlan exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		BatchPlannerPlanPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"active", "size"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}