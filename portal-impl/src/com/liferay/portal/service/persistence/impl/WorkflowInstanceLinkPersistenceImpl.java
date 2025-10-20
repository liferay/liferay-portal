/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchWorkflowInstanceLinkException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.model.WorkflowInstanceLinkTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.WorkflowInstanceLinkPersistence;
import com.liferay.portal.kernel.service.persistence.WorkflowInstanceLinkUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.WorkflowInstanceLinkImpl;
import com.liferay.portal.model.impl.WorkflowInstanceLinkModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the workflow instance link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class WorkflowInstanceLinkPersistenceImpl
	extends BasePersistenceImpl<WorkflowInstanceLink>
	implements WorkflowInstanceLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>WorkflowInstanceLinkUtil</code> to access the workflow instance link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		WorkflowInstanceLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByWorkflowInstanceId;

	/**
	 * Returns the workflow instance link where workflowInstanceId = &#63; or throws a <code>NoSuchWorkflowInstanceLinkException</code> if it could not be found.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink findByWorkflowInstanceId(
			long workflowInstanceId)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = fetchByWorkflowInstanceId(
			workflowInstanceId);

		if (workflowInstanceLink == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("workflowInstanceId=");
			sb.append(workflowInstanceId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchWorkflowInstanceLinkException(sb.toString());
		}

		return workflowInstanceLink;
	}

	/**
	 * Returns the workflow instance link where workflowInstanceId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByWorkflowInstanceId(
		long workflowInstanceId) {

		return fetchByWorkflowInstanceId(workflowInstanceId, true);
	}

	/**
	 * Returns the workflow instance link where workflowInstanceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByWorkflowInstanceId(
		long workflowInstanceId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowInstanceLink.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {workflowInstanceId};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByWorkflowInstanceId, finderArgs, this);
			}

			if (result instanceof WorkflowInstanceLink) {
				WorkflowInstanceLink workflowInstanceLink =
					(WorkflowInstanceLink)result;

				if (workflowInstanceId !=
						workflowInstanceLink.getWorkflowInstanceId()) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_SELECT_WORKFLOWINSTANCELINK_WHERE);

				sb.append(
					_FINDER_COLUMN_WORKFLOWINSTANCEID_WORKFLOWINSTANCEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(workflowInstanceId);

					List<WorkflowInstanceLink> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByWorkflowInstanceId,
								finderArgs, list);
						}
					}
					else {
						WorkflowInstanceLink workflowInstanceLink = list.get(0);

						result = workflowInstanceLink;

						cacheResult(workflowInstanceLink);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			if (result instanceof List<?>) {
				return null;
			}
			else {
				return (WorkflowInstanceLink)result;
			}
		}
	}

	/**
	 * Removes the workflow instance link where workflowInstanceId = &#63; from the database.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the workflow instance link that was removed
	 */
	@Override
	public WorkflowInstanceLink removeByWorkflowInstanceId(
			long workflowInstanceId)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = findByWorkflowInstanceId(
			workflowInstanceId);

		return remove(workflowInstanceLink);
	}

	/**
	 * Returns the number of workflow instance links where workflowInstanceId = &#63;.
	 *
	 * @param workflowInstanceId the workflow instance ID
	 * @return the number of matching workflow instance links
	 */
	@Override
	public int countByWorkflowInstanceId(long workflowInstanceId) {
		WorkflowInstanceLink workflowInstanceLink = fetchByWorkflowInstanceId(
			workflowInstanceId);

		if (workflowInstanceLink == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_WORKFLOWINSTANCEID_WORKFLOWINSTANCEID_2 =
			"workflowInstanceLink.workflowInstanceId = ?";

	private FinderPath _finderPathWithPaginationFindByC_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C;
	private FinderPath _finderPathCountByC_C;

	/**
	 * Returns all the workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId) {

		return findByC_C(
			companyId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @return the range of matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId, int start, int end) {

		return findByC_C(companyId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return findByC_C(
			companyId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowInstanceLink.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_C;
					finderArgs = new Object[] {companyId, classNameId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_C;
				finderArgs = new Object[] {
					companyId, classNameId, start, end, orderByComparator
				};
			}

			List<WorkflowInstanceLink> list = null;

			if (useFinderCache) {
				list = (List<WorkflowInstanceLink>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WorkflowInstanceLink workflowInstanceLink : list) {
						if ((companyId !=
								workflowInstanceLink.getCompanyId()) ||
							(classNameId !=
								workflowInstanceLink.getClassNameId())) {

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

				sb.append(_SQL_SELECT_WORKFLOWINSTANCELINK_WHERE);

				sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WorkflowInstanceLinkModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					list = (List<WorkflowInstanceLink>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	}

	/**
	 * Returns the first workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = fetchByC_C_First(
			companyId, classNameId, orderByComparator);

		if (workflowInstanceLink != null) {
			return workflowInstanceLink;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchWorkflowInstanceLinkException(sb.toString());
	}

	/**
	 * Returns the first workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		List<WorkflowInstanceLink> list = findByC_C(
			companyId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink findByC_C_Last(
			long companyId, long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = fetchByC_C_Last(
			companyId, classNameId, orderByComparator);

		if (workflowInstanceLink != null) {
			return workflowInstanceLink;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchWorkflowInstanceLinkException(sb.toString());
	}

	/**
	 * Returns the last workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByC_C_Last(
		long companyId, long classNameId,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		int count = countByC_C(companyId, classNameId);

		if (count == 0) {
			return null;
		}

		List<WorkflowInstanceLink> list = findByC_C(
			companyId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the workflow instance links before and after the current workflow instance link in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param workflowInstanceLinkId the primary key of the current workflow instance link
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink[] findByC_C_PrevAndNext(
			long workflowInstanceLinkId, long companyId, long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = findByPrimaryKey(
			workflowInstanceLinkId);

		Session session = null;

		try {
			session = openSession();

			WorkflowInstanceLink[] array = new WorkflowInstanceLinkImpl[3];

			array[0] = getByC_C_PrevAndNext(
				session, workflowInstanceLink, companyId, classNameId,
				orderByComparator, true);

			array[1] = workflowInstanceLink;

			array[2] = getByC_C_PrevAndNext(
				session, workflowInstanceLink, companyId, classNameId,
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

	protected WorkflowInstanceLink getByC_C_PrevAndNext(
		Session session, WorkflowInstanceLink workflowInstanceLink,
		long companyId, long classNameId,
		OrderByComparator<WorkflowInstanceLink> orderByComparator,
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

		sb.append(_SQL_SELECT_WORKFLOWINSTANCELINK_WHERE);

		sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

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
			sb.append(WorkflowInstanceLinkModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						workflowInstanceLink)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WorkflowInstanceLink> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the workflow instance links where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_C(long companyId, long classNameId) {
		for (WorkflowInstanceLink workflowInstanceLink :
				findByC_C(
					companyId, classNameId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(workflowInstanceLink);
		}
	}

	/**
	 * Returns the number of workflow instance links where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow instance links
	 */
	@Override
	public int countByC_C(long companyId, long classNameId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowInstanceLink.class)) {

			FinderPath finderPath = _finderPathCountByC_C;

			Object[] finderArgs = new Object[] {companyId, classNameId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_WORKFLOWINSTANCELINK_WHERE);

				sb.append(_FINDER_COLUMN_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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
	}

	private static final String _FINDER_COLUMN_C_C_COMPANYID_2 =
		"workflowInstanceLink.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_CLASSNAMEID_2 =
		"workflowInstanceLink.classNameId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_C;
	private FinderPath _finderPathWithoutPaginationFindByG_C_C;
	private FinderPath _finderPathCountByG_C_C;

	/**
	 * Returns all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId) {

		return findByG_C_C(
			groupId, companyId, classNameId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @return the range of matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end) {

		return findByG_C_C(groupId, companyId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return findByG_C_C(
			groupId, companyId, classNameId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowInstanceLink.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_C;
					finderArgs = new Object[] {groupId, companyId, classNameId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_C;
				finderArgs = new Object[] {
					groupId, companyId, classNameId, start, end,
					orderByComparator
				};
			}

			List<WorkflowInstanceLink> list = null;

			if (useFinderCache) {
				list = (List<WorkflowInstanceLink>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WorkflowInstanceLink workflowInstanceLink : list) {
						if ((groupId != workflowInstanceLink.getGroupId()) ||
							(companyId !=
								workflowInstanceLink.getCompanyId()) ||
							(classNameId !=
								workflowInstanceLink.getClassNameId())) {

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

				sb.append(_SQL_SELECT_WORKFLOWINSTANCELINK_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WorkflowInstanceLinkModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					list = (List<WorkflowInstanceLink>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	}

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink findByG_C_C_First(
			long groupId, long companyId, long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = fetchByG_C_C_First(
			groupId, companyId, classNameId, orderByComparator);

		if (workflowInstanceLink != null) {
			return workflowInstanceLink;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchWorkflowInstanceLinkException(sb.toString());
	}

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByG_C_C_First(
		long groupId, long companyId, long classNameId,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		List<WorkflowInstanceLink> list = findByG_C_C(
			groupId, companyId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink findByG_C_C_Last(
			long groupId, long companyId, long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = fetchByG_C_C_Last(
			groupId, companyId, classNameId, orderByComparator);

		if (workflowInstanceLink != null) {
			return workflowInstanceLink;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchWorkflowInstanceLinkException(sb.toString());
	}

	/**
	 * Returns the last workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByG_C_C_Last(
		long groupId, long companyId, long classNameId,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		int count = countByG_C_C(groupId, companyId, classNameId);

		if (count == 0) {
			return null;
		}

		List<WorkflowInstanceLink> list = findByG_C_C(
			groupId, companyId, classNameId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the workflow instance links before and after the current workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param workflowInstanceLinkId the primary key of the current workflow instance link
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink[] findByG_C_C_PrevAndNext(
			long workflowInstanceLinkId, long groupId, long companyId,
			long classNameId,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = findByPrimaryKey(
			workflowInstanceLinkId);

		Session session = null;

		try {
			session = openSession();

			WorkflowInstanceLink[] array = new WorkflowInstanceLinkImpl[3];

			array[0] = getByG_C_C_PrevAndNext(
				session, workflowInstanceLink, groupId, companyId, classNameId,
				orderByComparator, true);

			array[1] = workflowInstanceLink;

			array[2] = getByG_C_C_PrevAndNext(
				session, workflowInstanceLink, groupId, companyId, classNameId,
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

	protected WorkflowInstanceLink getByG_C_C_PrevAndNext(
		Session session, WorkflowInstanceLink workflowInstanceLink,
		long groupId, long companyId, long classNameId,
		OrderByComparator<WorkflowInstanceLink> orderByComparator,
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

		sb.append(_SQL_SELECT_WORKFLOWINSTANCELINK_WHERE);

		sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

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
			sb.append(WorkflowInstanceLinkModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						workflowInstanceLink)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WorkflowInstanceLink> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C_C(long groupId, long companyId, long classNameId) {
		for (WorkflowInstanceLink workflowInstanceLink :
				findByG_C_C(
					groupId, companyId, classNameId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(workflowInstanceLink);
		}
	}

	/**
	 * Returns the number of workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow instance links
	 */
	@Override
	public int countByG_C_C(long groupId, long companyId, long classNameId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowInstanceLink.class)) {

			FinderPath finderPath = _finderPathCountByG_C_C;

			Object[] finderArgs = new Object[] {
				groupId, companyId, classNameId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WORKFLOWINSTANCELINK_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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
	}

	private static final String _FINDER_COLUMN_G_C_C_GROUPID_2 =
		"workflowInstanceLink.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_COMPANYID_2 =
		"workflowInstanceLink.companyId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_CLASSNAMEID_2 =
		"workflowInstanceLink.classNameId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_C_C;
	private FinderPath _finderPathWithoutPaginationFindByG_C_C_C;
	private FinderPath _finderPathCountByG_C_C_C;

	/**
	 * Returns all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		return findByG_C_C_C(
			groupId, companyId, classNameId, classPK, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @return the range of matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end) {

		return findByG_C_C_C(
			groupId, companyId, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end, OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return findByG_C_C_C(
			groupId, companyId, classNameId, classPK, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end, OrderByComparator<WorkflowInstanceLink> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowInstanceLink.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_C_C;
					finderArgs = new Object[] {
						groupId, companyId, classNameId, classPK
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_C_C;
				finderArgs = new Object[] {
					groupId, companyId, classNameId, classPK, start, end,
					orderByComparator
				};
			}

			List<WorkflowInstanceLink> list = null;

			if (useFinderCache) {
				list = (List<WorkflowInstanceLink>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WorkflowInstanceLink workflowInstanceLink : list) {
						if ((groupId != workflowInstanceLink.getGroupId()) ||
							(companyId !=
								workflowInstanceLink.getCompanyId()) ||
							(classNameId !=
								workflowInstanceLink.getClassNameId()) ||
							(classPK != workflowInstanceLink.getClassPK())) {

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
						6 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(6);
				}

				sb.append(_SQL_SELECT_WORKFLOWINSTANCELINK_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WorkflowInstanceLinkModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					list = (List<WorkflowInstanceLink>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	}

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink findByG_C_C_C_First(
			long groupId, long companyId, long classNameId, long classPK,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = fetchByG_C_C_C_First(
			groupId, companyId, classNameId, classPK, orderByComparator);

		if (workflowInstanceLink != null) {
			return workflowInstanceLink;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchWorkflowInstanceLinkException(sb.toString());
	}

	/**
	 * Returns the first workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByG_C_C_C_First(
		long groupId, long companyId, long classNameId, long classPK,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		List<WorkflowInstanceLink> list = findByG_C_C_C(
			groupId, companyId, classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink findByG_C_C_C_Last(
			long groupId, long companyId, long classNameId, long classPK,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = fetchByG_C_C_C_Last(
			groupId, companyId, classNameId, classPK, orderByComparator);

		if (workflowInstanceLink != null) {
			return workflowInstanceLink;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchWorkflowInstanceLinkException(sb.toString());
	}

	/**
	 * Returns the last workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow instance link, or <code>null</code> if a matching workflow instance link could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByG_C_C_C_Last(
		long groupId, long companyId, long classNameId, long classPK,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		int count = countByG_C_C_C(groupId, companyId, classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<WorkflowInstanceLink> list = findByG_C_C_C(
			groupId, companyId, classNameId, classPK, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the workflow instance links before and after the current workflow instance link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param workflowInstanceLinkId the primary key of the current workflow instance link
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink[] findByG_C_C_C_PrevAndNext(
			long workflowInstanceLinkId, long groupId, long companyId,
			long classNameId, long classPK,
			OrderByComparator<WorkflowInstanceLink> orderByComparator)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = findByPrimaryKey(
			workflowInstanceLinkId);

		Session session = null;

		try {
			session = openSession();

			WorkflowInstanceLink[] array = new WorkflowInstanceLinkImpl[3];

			array[0] = getByG_C_C_C_PrevAndNext(
				session, workflowInstanceLink, groupId, companyId, classNameId,
				classPK, orderByComparator, true);

			array[1] = workflowInstanceLink;

			array[2] = getByG_C_C_C_PrevAndNext(
				session, workflowInstanceLink, groupId, companyId, classNameId,
				classPK, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WorkflowInstanceLink getByG_C_C_C_PrevAndNext(
		Session session, WorkflowInstanceLink workflowInstanceLink,
		long groupId, long companyId, long classNameId, long classPK,
		OrderByComparator<WorkflowInstanceLink> orderByComparator,
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

		sb.append(_SQL_SELECT_WORKFLOWINSTANCELINK_WHERE);

		sb.append(_FINDER_COLUMN_G_C_C_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_C_C_COMPANYID_2);

		sb.append(_FINDER_COLUMN_G_C_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_G_C_C_C_CLASSPK_2);

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
			sb.append(WorkflowInstanceLinkModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						workflowInstanceLink)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WorkflowInstanceLink> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		for (WorkflowInstanceLink workflowInstanceLink :
				findByG_C_C_C(
					groupId, companyId, classNameId, classPK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(workflowInstanceLink);
		}
	}

	/**
	 * Returns the number of workflow instance links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching workflow instance links
	 */
	@Override
	public int countByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowInstanceLink.class)) {

			FinderPath finderPath = _finderPathCountByG_C_C_C;

			Object[] finderArgs = new Object[] {
				groupId, companyId, classNameId, classPK
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_WORKFLOWINSTANCELINK_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
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
	}

	private static final String _FINDER_COLUMN_G_C_C_C_GROUPID_2 =
		"workflowInstanceLink.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_C_COMPANYID_2 =
		"workflowInstanceLink.companyId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_C_CLASSNAMEID_2 =
		"workflowInstanceLink.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_C_CLASSPK_2 =
		"workflowInstanceLink.classPK = ?";

	public WorkflowInstanceLinkPersistenceImpl() {
		setModelClass(WorkflowInstanceLink.class);

		setModelImplClass(WorkflowInstanceLinkImpl.class);
		setModelPKClass(long.class);

		setTable(WorkflowInstanceLinkTable.INSTANCE);
	}

	/**
	 * Caches the workflow instance link in the entity cache if it is enabled.
	 *
	 * @param workflowInstanceLink the workflow instance link
	 */
	@Override
	public void cacheResult(WorkflowInstanceLink workflowInstanceLink) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					workflowInstanceLink.getCtCollectionId())) {

			EntityCacheUtil.putResult(
				WorkflowInstanceLinkImpl.class,
				workflowInstanceLink.getPrimaryKey(), workflowInstanceLink);

			FinderCacheUtil.putResult(
				_finderPathFetchByWorkflowInstanceId,
				new Object[] {workflowInstanceLink.getWorkflowInstanceId()},
				workflowInstanceLink);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the workflow instance links in the entity cache if it is enabled.
	 *
	 * @param workflowInstanceLinks the workflow instance links
	 */
	@Override
	public void cacheResult(List<WorkflowInstanceLink> workflowInstanceLinks) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (workflowInstanceLinks.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (WorkflowInstanceLink workflowInstanceLink :
				workflowInstanceLinks) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						workflowInstanceLink.getCtCollectionId())) {

				if (EntityCacheUtil.getResult(
						WorkflowInstanceLinkImpl.class,
						workflowInstanceLink.getPrimaryKey()) == null) {

					cacheResult(workflowInstanceLink);
				}
			}
		}
	}

	/**
	 * Clears the cache for all workflow instance links.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(WorkflowInstanceLinkImpl.class);

		FinderCacheUtil.clearCache(WorkflowInstanceLinkImpl.class);
	}

	/**
	 * Clears the cache for the workflow instance link.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(WorkflowInstanceLink workflowInstanceLink) {
		EntityCacheUtil.removeResult(
			WorkflowInstanceLinkImpl.class, workflowInstanceLink);
	}

	@Override
	public void clearCache(List<WorkflowInstanceLink> workflowInstanceLinks) {
		for (WorkflowInstanceLink workflowInstanceLink :
				workflowInstanceLinks) {

			EntityCacheUtil.removeResult(
				WorkflowInstanceLinkImpl.class, workflowInstanceLink);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(WorkflowInstanceLinkImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(
				WorkflowInstanceLinkImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		WorkflowInstanceLinkModelImpl workflowInstanceLinkModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					workflowInstanceLinkModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				workflowInstanceLinkModelImpl.getWorkflowInstanceId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByWorkflowInstanceId, args,
				workflowInstanceLinkModelImpl);
		}
	}

	/**
	 * Creates a new workflow instance link with the primary key. Does not add the workflow instance link to the database.
	 *
	 * @param workflowInstanceLinkId the primary key for the new workflow instance link
	 * @return the new workflow instance link
	 */
	@Override
	public WorkflowInstanceLink create(long workflowInstanceLinkId) {
		WorkflowInstanceLink workflowInstanceLink =
			new WorkflowInstanceLinkImpl();

		workflowInstanceLink.setNew(true);
		workflowInstanceLink.setPrimaryKey(workflowInstanceLinkId);

		workflowInstanceLink.setCompanyId(CompanyThreadLocal.getCompanyId());

		return workflowInstanceLink;
	}

	/**
	 * Removes the workflow instance link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link that was removed
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink remove(long workflowInstanceLinkId)
		throws NoSuchWorkflowInstanceLinkException {

		return remove((Serializable)workflowInstanceLinkId);
	}

	/**
	 * Removes the workflow instance link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the workflow instance link
	 * @return the workflow instance link that was removed
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink remove(Serializable primaryKey)
		throws NoSuchWorkflowInstanceLinkException {

		Session session = null;

		try {
			session = openSession();

			WorkflowInstanceLink workflowInstanceLink =
				(WorkflowInstanceLink)session.get(
					WorkflowInstanceLinkImpl.class, primaryKey);

			if (workflowInstanceLink == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchWorkflowInstanceLinkException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(workflowInstanceLink);
		}
		catch (NoSuchWorkflowInstanceLinkException noSuchEntityException) {
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
	protected WorkflowInstanceLink removeImpl(
		WorkflowInstanceLink workflowInstanceLink) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(workflowInstanceLink)) {
				workflowInstanceLink = (WorkflowInstanceLink)session.get(
					WorkflowInstanceLinkImpl.class,
					workflowInstanceLink.getPrimaryKeyObj());
			}

			if ((workflowInstanceLink != null) &&
				CTPersistenceHelperUtil.isRemove(workflowInstanceLink)) {

				session.delete(workflowInstanceLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (workflowInstanceLink != null) {
			clearCache(workflowInstanceLink);
		}

		return workflowInstanceLink;
	}

	@Override
	public WorkflowInstanceLink updateImpl(
		WorkflowInstanceLink workflowInstanceLink) {

		boolean isNew = workflowInstanceLink.isNew();

		if (!(workflowInstanceLink instanceof WorkflowInstanceLinkModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(workflowInstanceLink.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					workflowInstanceLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in workflowInstanceLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom WorkflowInstanceLink implementation " +
					workflowInstanceLink.getClass());
		}

		WorkflowInstanceLinkModelImpl workflowInstanceLinkModelImpl =
			(WorkflowInstanceLinkModelImpl)workflowInstanceLink;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (workflowInstanceLink.getCreateDate() == null)) {
			if (serviceContext == null) {
				workflowInstanceLink.setCreateDate(date);
			}
			else {
				workflowInstanceLink.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!workflowInstanceLinkModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				workflowInstanceLink.setModifiedDate(date);
			}
			else {
				workflowInstanceLink.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(workflowInstanceLink)) {
				if (!isNew) {
					session.evict(
						WorkflowInstanceLinkImpl.class,
						workflowInstanceLink.getPrimaryKeyObj());
				}

				session.save(workflowInstanceLink);
			}
			else {
				workflowInstanceLink = (WorkflowInstanceLink)session.merge(
					workflowInstanceLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(
			WorkflowInstanceLinkImpl.class, workflowInstanceLinkModelImpl,
			false, true);

		cacheUniqueFindersCache(workflowInstanceLinkModelImpl);

		if (isNew) {
			workflowInstanceLink.setNew(false);
		}

		workflowInstanceLink.resetOriginalValues();

		return workflowInstanceLink;
	}

	/**
	 * Returns the workflow instance link with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the workflow instance link
	 * @return the workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink findByPrimaryKey(Serializable primaryKey)
		throws NoSuchWorkflowInstanceLinkException {

		WorkflowInstanceLink workflowInstanceLink = fetchByPrimaryKey(
			primaryKey);

		if (workflowInstanceLink == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchWorkflowInstanceLinkException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return workflowInstanceLink;
	}

	/**
	 * Returns the workflow instance link with the primary key or throws a <code>NoSuchWorkflowInstanceLinkException</code> if it could not be found.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link
	 * @throws NoSuchWorkflowInstanceLinkException if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink findByPrimaryKey(long workflowInstanceLinkId)
		throws NoSuchWorkflowInstanceLinkException {

		return findByPrimaryKey((Serializable)workflowInstanceLinkId);
	}

	/**
	 * Returns the workflow instance link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the workflow instance link
	 * @return the workflow instance link, or <code>null</code> if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByPrimaryKey(Serializable primaryKey) {
		if (CTPersistenceHelperUtil.isProductionMode(
				WorkflowInstanceLink.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		WorkflowInstanceLink workflowInstanceLink =
			(WorkflowInstanceLink)EntityCacheUtil.getResult(
				WorkflowInstanceLinkImpl.class, primaryKey);

		if (workflowInstanceLink != null) {
			return workflowInstanceLink;
		}

		Session session = null;

		try {
			session = openSession();

			workflowInstanceLink = (WorkflowInstanceLink)session.get(
				WorkflowInstanceLinkImpl.class, primaryKey);

			if (workflowInstanceLink != null) {
				cacheResult(workflowInstanceLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return workflowInstanceLink;
	}

	/**
	 * Returns the workflow instance link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param workflowInstanceLinkId the primary key of the workflow instance link
	 * @return the workflow instance link, or <code>null</code> if a workflow instance link with the primary key could not be found
	 */
	@Override
	public WorkflowInstanceLink fetchByPrimaryKey(long workflowInstanceLinkId) {
		return fetchByPrimaryKey((Serializable)workflowInstanceLinkId);
	}

	@Override
	public Map<Serializable, WorkflowInstanceLink> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (CTPersistenceHelperUtil.isProductionMode(
				WorkflowInstanceLink.class)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, WorkflowInstanceLink> map =
			new HashMap<Serializable, WorkflowInstanceLink>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			WorkflowInstanceLink workflowInstanceLink = fetchByPrimaryKey(
				primaryKey);

			if (workflowInstanceLink != null) {
				map.put(primaryKey, workflowInstanceLink);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
						WorkflowInstanceLink.class, primaryKey)) {

				WorkflowInstanceLink workflowInstanceLink =
					(WorkflowInstanceLink)EntityCacheUtil.getResult(
						WorkflowInstanceLinkImpl.class, primaryKey);

				if (workflowInstanceLink == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, workflowInstanceLink);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		if ((databaseInMaxParameters > 0) &&
			(primaryKeys.size() > databaseInMaxParameters)) {

			Iterator<Serializable> iterator = primaryKeys.iterator();

			while (iterator.hasNext()) {
				Set<Serializable> page = new HashSet<>();

				for (int i = 0;
					 (i < databaseInMaxParameters) && iterator.hasNext(); i++) {

					page.add(iterator.next());
				}

				map.putAll(fetchByPrimaryKeys(page));
			}

			return map;
		}

		StringBundler sb = new StringBundler((primaryKeys.size() * 2) + 1);

		sb.append(getSelectSQL());
		sb.append(" WHERE ");
		sb.append(getPKDBName());
		sb.append(" IN (");

		for (Serializable primaryKey : primaryKeys) {
			sb.append((long)primaryKey);

			sb.append(",");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(")");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			for (WorkflowInstanceLink workflowInstanceLink :
					(List<WorkflowInstanceLink>)query.list()) {

				map.put(
					workflowInstanceLink.getPrimaryKeyObj(),
					workflowInstanceLink);

				cacheResult(workflowInstanceLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return map;
	}

	/**
	 * Returns all the workflow instance links.
	 *
	 * @return the workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the workflow instance links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @return the range of workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the workflow instance links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findAll(
		int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the workflow instance links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow instance links
	 * @param end the upper bound of the range of workflow instance links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of workflow instance links
	 */
	@Override
	public List<WorkflowInstanceLink> findAll(
		int start, int end,
		OrderByComparator<WorkflowInstanceLink> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowInstanceLink.class)) {

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

			List<WorkflowInstanceLink> list = null;

			if (useFinderCache) {
				list = (List<WorkflowInstanceLink>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_WORKFLOWINSTANCELINK);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_WORKFLOWINSTANCELINK;

					sql = sql.concat(
						WorkflowInstanceLinkModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<WorkflowInstanceLink>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
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
	}

	/**
	 * Removes all the workflow instance links from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (WorkflowInstanceLink workflowInstanceLink : findAll()) {
			remove(workflowInstanceLink);
		}
	}

	/**
	 * Returns the number of workflow instance links.
	 *
	 * @return the number of workflow instance links
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowInstanceLink.class)) {

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_WORKFLOWINSTANCELINK);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
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
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "workflowInstanceLinkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_WORKFLOWINSTANCELINK;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return WorkflowInstanceLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "WorkflowInstanceLink";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("workflowInstanceId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("workflowInstanceLinkId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"workflowInstanceId"});
	}

	/**
	 * Initializes the workflow instance link persistence.
	 */
	public void afterPropertiesSet() {
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

		_finderPathFetchByWorkflowInstanceId = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByWorkflowInstanceId",
			new String[] {Long.class.getName()},
			new String[] {"workflowInstanceId"}, true);

		_finderPathWithPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "classNameId"}, true);

		_finderPathCountByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "classNameId"}, false);

		_finderPathWithPaginationFindByG_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "companyId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByG_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "companyId", "classNameId"}, true);

		_finderPathCountByG_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "companyId", "classNameId"}, false);

		_finderPathWithPaginationFindByG_C_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "companyId", "classNameId", "classPK"},
			true);

		_finderPathWithoutPaginationFindByG_C_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "companyId", "classNameId", "classPK"},
			true);

		_finderPathCountByG_C_C_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "companyId", "classNameId", "classPK"},
			false);

		WorkflowInstanceLinkUtil.setPersistence(this);
	}

	public void destroy() {
		WorkflowInstanceLinkUtil.setPersistence(null);

		EntityCacheUtil.removeCache(WorkflowInstanceLinkImpl.class.getName());
	}

	private static final String _SQL_SELECT_WORKFLOWINSTANCELINK =
		"SELECT workflowInstanceLink FROM WorkflowInstanceLink workflowInstanceLink";

	private static final String _SQL_SELECT_WORKFLOWINSTANCELINK_WHERE =
		"SELECT workflowInstanceLink FROM WorkflowInstanceLink workflowInstanceLink WHERE ";

	private static final String _SQL_COUNT_WORKFLOWINSTANCELINK =
		"SELECT COUNT(workflowInstanceLink) FROM WorkflowInstanceLink workflowInstanceLink";

	private static final String _SQL_COUNT_WORKFLOWINSTANCELINK_WHERE =
		"SELECT COUNT(workflowInstanceLink) FROM WorkflowInstanceLink workflowInstanceLink WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"workflowInstanceLink.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No WorkflowInstanceLink exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No WorkflowInstanceLink exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowInstanceLinkPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}