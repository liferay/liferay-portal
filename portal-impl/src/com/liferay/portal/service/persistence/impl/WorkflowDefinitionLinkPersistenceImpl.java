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
import com.liferay.portal.kernel.exception.DuplicateWorkflowDefinitionLinkExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchWorkflowDefinitionLinkException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.WorkflowDefinitionLinkTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.WorkflowDefinitionLinkPersistence;
import com.liferay.portal.kernel.service.persistence.WorkflowDefinitionLinkUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.WorkflowDefinitionLinkImpl;
import com.liferay.portal.model.impl.WorkflowDefinitionLinkModelImpl;

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
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the workflow definition link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class WorkflowDefinitionLinkPersistenceImpl
	extends BasePersistenceImpl<WorkflowDefinitionLink>
	implements WorkflowDefinitionLinkPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>WorkflowDefinitionLinkUtil</code> to access the workflow definition link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		WorkflowDefinitionLinkImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByUuid;
	private FinderPath _finderPathWithoutPaginationFindByUuid;
	private FinderPath _finderPathCountByUuid;

	/**
	 * Returns all the workflow definition links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the workflow definition links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUuid;
					finderArgs = new Object[] {uuid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUuid;
				finderArgs = new Object[] {uuid, start, end, orderByComparator};
			}

			List<WorkflowDefinitionLink> list = null;

			if (useFinderCache) {
				list = (List<WorkflowDefinitionLink>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WorkflowDefinitionLink workflowDefinitionLink : list) {
						if (!uuid.equals(workflowDefinitionLink.getUuid())) {
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

				sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_UUID_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					list = (List<WorkflowDefinitionLink>)QueryUtil.list(
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
	 * Returns the first workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByUuid_First(
			String uuid,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByUuid_First(
			uuid, orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByUuid_First(
		String uuid,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		List<WorkflowDefinitionLink> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByUuid_Last(
			String uuid,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByUuid_Last(
			uuid, orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the last workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByUuid_Last(
		String uuid,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<WorkflowDefinitionLink> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where uuid = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink[] findByUuid_PrevAndNext(
			long workflowDefinitionLinkId, String uuid,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		uuid = Objects.toString(uuid, "");

		WorkflowDefinitionLink workflowDefinitionLink = findByPrimaryKey(
			workflowDefinitionLinkId);

		Session session = null;

		try {
			session = openSession();

			WorkflowDefinitionLink[] array = new WorkflowDefinitionLinkImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, workflowDefinitionLink, uuid, orderByComparator, true);

			array[1] = workflowDefinitionLink;

			array[2] = getByUuid_PrevAndNext(
				session, workflowDefinitionLink, uuid, orderByComparator,
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

	protected WorkflowDefinitionLink getByUuid_PrevAndNext(
		Session session, WorkflowDefinitionLink workflowDefinitionLink,
		String uuid,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
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

		sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2);
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
			sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						workflowDefinitionLink)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WorkflowDefinitionLink> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the workflow definition links where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (WorkflowDefinitionLink workflowDefinitionLink :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(workflowDefinitionLink);
		}
	}

	/**
	 * Returns the number of workflow definition links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_UUID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

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

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"workflowDefinitionLink.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(workflowDefinitionLink.uuid IS NULL OR workflowDefinitionLink.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the workflow definition link where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByUUID_G(String uuid, long groupId)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByUUID_G(
			uuid, groupId);

		if (workflowDefinitionLink == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("uuid=");
			sb.append(uuid);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
		}

		return workflowDefinitionLink;
	}

	/**
	 * Returns the workflow definition link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the workflow definition link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			uuid = Objects.toString(uuid, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {uuid, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByUUID_G, finderArgs, this);
			}

			if (result instanceof WorkflowDefinitionLink) {
				WorkflowDefinitionLink workflowDefinitionLink =
					(WorkflowDefinitionLink)result;

				if (!Objects.equals(uuid, workflowDefinitionLink.getUuid()) ||
					(groupId != workflowDefinitionLink.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_G_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_G_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_G_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(groupId);

					List<WorkflowDefinitionLink> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						WorkflowDefinitionLink workflowDefinitionLink =
							list.get(0);

						result = workflowDefinitionLink;

						cacheResult(workflowDefinitionLink);
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
				return (WorkflowDefinitionLink)result;
			}
		}
	}

	/**
	 * Removes the workflow definition link where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the workflow definition link that was removed
	 */
	@Override
	public WorkflowDefinitionLink removeByUUID_G(String uuid, long groupId)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = findByUUID_G(
			uuid, groupId);

		return remove(workflowDefinitionLink);
	}

	/**
	 * Returns the number of workflow definition links where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		WorkflowDefinitionLink workflowDefinitionLink = fetchByUUID_G(
			uuid, groupId);

		if (workflowDefinitionLink == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"workflowDefinitionLink.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(workflowDefinitionLink.uuid IS NULL OR workflowDefinitionLink.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"workflowDefinitionLink.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUuid_C;
					finderArgs = new Object[] {uuid, companyId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUuid_C;
				finderArgs = new Object[] {
					uuid, companyId, start, end, orderByComparator
				};
			}

			List<WorkflowDefinitionLink> list = null;

			if (useFinderCache) {
				list = (List<WorkflowDefinitionLink>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WorkflowDefinitionLink workflowDefinitionLink : list) {
						if (!uuid.equals(workflowDefinitionLink.getUuid()) ||
							(companyId !=
								workflowDefinitionLink.getCompanyId())) {

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

				sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(companyId);

					list = (List<WorkflowDefinitionLink>)QueryUtil.list(
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
	 * Returns the first workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the first workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		List<WorkflowDefinitionLink> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the last workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<WorkflowDefinitionLink> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink[] findByUuid_C_PrevAndNext(
			long workflowDefinitionLinkId, String uuid, long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		uuid = Objects.toString(uuid, "");

		WorkflowDefinitionLink workflowDefinitionLink = findByPrimaryKey(
			workflowDefinitionLinkId);

		Session session = null;

		try {
			session = openSession();

			WorkflowDefinitionLink[] array = new WorkflowDefinitionLinkImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, workflowDefinitionLink, uuid, companyId,
				orderByComparator, true);

			array[1] = workflowDefinitionLink;

			array[2] = getByUuid_C_PrevAndNext(
				session, workflowDefinitionLink, uuid, companyId,
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

	protected WorkflowDefinitionLink getByUuid_C_PrevAndNext(
		Session session, WorkflowDefinitionLink workflowDefinitionLink,
		String uuid, long companyId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
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

		sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

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
			sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						workflowDefinitionLink)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WorkflowDefinitionLink> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the workflow definition links where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (WorkflowDefinitionLink workflowDefinitionLink :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(workflowDefinitionLink);
		}
	}

	/**
	 * Returns the number of workflow definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"workflowDefinitionLink.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(workflowDefinitionLink.uuid IS NULL OR workflowDefinitionLink.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"workflowDefinitionLink.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the workflow definition links where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the workflow definition links where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

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

			List<WorkflowDefinitionLink> list = null;

			if (useFinderCache) {
				list = (List<WorkflowDefinitionLink>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WorkflowDefinitionLink workflowDefinitionLink : list) {
						if (companyId !=
								workflowDefinitionLink.getCompanyId()) {

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

				sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<WorkflowDefinitionLink>)QueryUtil.list(
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
	 * Returns the first workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByCompanyId_First(
			long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByCompanyId_First(
		long companyId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		List<WorkflowDefinitionLink> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByCompanyId_Last(
			long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the last workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<WorkflowDefinitionLink> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where companyId = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink[] findByCompanyId_PrevAndNext(
			long workflowDefinitionLinkId, long companyId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = findByPrimaryKey(
			workflowDefinitionLinkId);

		Session session = null;

		try {
			session = openSession();

			WorkflowDefinitionLink[] array = new WorkflowDefinitionLinkImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, workflowDefinitionLink, companyId, orderByComparator,
				true);

			array[1] = workflowDefinitionLink;

			array[2] = getByCompanyId_PrevAndNext(
				session, workflowDefinitionLink, companyId, orderByComparator,
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

	protected WorkflowDefinitionLink getByCompanyId_PrevAndNext(
		Session session, WorkflowDefinitionLink workflowDefinitionLink,
		long companyId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
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

		sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

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
			sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
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
						workflowDefinitionLink)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WorkflowDefinitionLink> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the workflow definition links where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (WorkflowDefinitionLink workflowDefinitionLink :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(workflowDefinitionLink);
		}
	}

	/**
	 * Returns the number of workflow definition links where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"workflowDefinitionLink.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_C;
	private FinderPath _finderPathWithoutPaginationFindByG_C_C;
	private FinderPath _finderPathCountByG_C_C;

	/**
	 * Returns all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId) {

		return findByG_C_C(
			groupId, companyId, classNameId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end) {

		return findByG_C_C(groupId, companyId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return findByG_C_C(
			groupId, companyId, classNameId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_C(
		long groupId, long companyId, long classNameId, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

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

			List<WorkflowDefinitionLink> list = null;

			if (useFinderCache) {
				list = (List<WorkflowDefinitionLink>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WorkflowDefinitionLink workflowDefinitionLink : list) {
						if ((groupId != workflowDefinitionLink.getGroupId()) ||
							(companyId !=
								workflowDefinitionLink.getCompanyId()) ||
							(classNameId !=
								workflowDefinitionLink.getClassNameId())) {

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

				sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_G_C_C_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
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

					list = (List<WorkflowDefinitionLink>)QueryUtil.list(
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
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByG_C_C_First(
			long groupId, long companyId, long classNameId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByG_C_C_First(
			groupId, companyId, classNameId, orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
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

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_C_First(
		long groupId, long companyId, long classNameId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		List<WorkflowDefinitionLink> list = findByG_C_C(
			groupId, companyId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByG_C_C_Last(
			long groupId, long companyId, long classNameId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByG_C_C_Last(
			groupId, companyId, classNameId, orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
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

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_C_Last(
		long groupId, long companyId, long classNameId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		int count = countByG_C_C(groupId, companyId, classNameId);

		if (count == 0) {
			return null;
		}

		List<WorkflowDefinitionLink> list = findByG_C_C(
			groupId, companyId, classNameId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink[] findByG_C_C_PrevAndNext(
			long workflowDefinitionLinkId, long groupId, long companyId,
			long classNameId,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = findByPrimaryKey(
			workflowDefinitionLinkId);

		Session session = null;

		try {
			session = openSession();

			WorkflowDefinitionLink[] array = new WorkflowDefinitionLinkImpl[3];

			array[0] = getByG_C_C_PrevAndNext(
				session, workflowDefinitionLink, groupId, companyId,
				classNameId, orderByComparator, true);

			array[1] = workflowDefinitionLink;

			array[2] = getByG_C_C_PrevAndNext(
				session, workflowDefinitionLink, groupId, companyId,
				classNameId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WorkflowDefinitionLink getByG_C_C_PrevAndNext(
		Session session, WorkflowDefinitionLink workflowDefinitionLink,
		long groupId, long companyId, long classNameId,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
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

		sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

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
			sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
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
						workflowDefinitionLink)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WorkflowDefinitionLink> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C_C(long groupId, long companyId, long classNameId) {
		for (WorkflowDefinitionLink workflowDefinitionLink :
				findByG_C_C(
					groupId, companyId, classNameId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(workflowDefinitionLink);
		}
	}

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByG_C_C(long groupId, long companyId, long classNameId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			FinderPath finderPath = _finderPathCountByG_C_C;

			Object[] finderArgs = new Object[] {
				groupId, companyId, classNameId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE);

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
		"workflowDefinitionLink.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_COMPANYID_2 =
		"workflowDefinitionLink.companyId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_CLASSNAMEID_2 =
		"workflowDefinitionLink.classNameId = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_CPK;
	private FinderPath _finderPathWithoutPaginationFindByG_C_CPK;
	private FinderPath _finderPathCountByG_C_CPK;

	/**
	 * Returns all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @return the matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK) {

		return findByG_C_CPK(
			groupId, companyId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK, int start, int end) {

		return findByG_C_CPK(groupId, companyId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return findByG_C_CPK(
			groupId, companyId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_CPK(
		long groupId, long companyId, long classPK, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_C_CPK;
					finderArgs = new Object[] {groupId, companyId, classPK};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_C_CPK;
				finderArgs = new Object[] {
					groupId, companyId, classPK, start, end, orderByComparator
				};
			}

			List<WorkflowDefinitionLink> list = null;

			if (useFinderCache) {
				list = (List<WorkflowDefinitionLink>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WorkflowDefinitionLink workflowDefinitionLink : list) {
						if ((groupId != workflowDefinitionLink.getGroupId()) ||
							(companyId !=
								workflowDefinitionLink.getCompanyId()) ||
							(classPK != workflowDefinitionLink.getClassPK())) {

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

				sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

				sb.append(_FINDER_COLUMN_G_C_CPK_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_CPK_COMPANYID_2);

				sb.append(_FINDER_COLUMN_G_C_CPK_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(companyId);

					queryPos.add(classPK);

					list = (List<WorkflowDefinitionLink>)QueryUtil.list(
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
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByG_C_CPK_First(
			long groupId, long companyId, long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByG_C_CPK_First(
			groupId, companyId, classPK, orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_CPK_First(
		long groupId, long companyId, long classPK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		List<WorkflowDefinitionLink> list = findByG_C_CPK(
			groupId, companyId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByG_C_CPK_Last(
			long groupId, long companyId, long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByG_C_CPK_Last(
			groupId, companyId, classPK, orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_CPK_Last(
		long groupId, long companyId, long classPK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		int count = countByG_C_CPK(groupId, companyId, classPK);

		if (count == 0) {
			return null;
		}

		List<WorkflowDefinitionLink> list = findByG_C_CPK(
			groupId, companyId, classPK, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink[] findByG_C_CPK_PrevAndNext(
			long workflowDefinitionLinkId, long groupId, long companyId,
			long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = findByPrimaryKey(
			workflowDefinitionLinkId);

		Session session = null;

		try {
			session = openSession();

			WorkflowDefinitionLink[] array = new WorkflowDefinitionLinkImpl[3];

			array[0] = getByG_C_CPK_PrevAndNext(
				session, workflowDefinitionLink, groupId, companyId, classPK,
				orderByComparator, true);

			array[1] = workflowDefinitionLink;

			array[2] = getByG_C_CPK_PrevAndNext(
				session, workflowDefinitionLink, groupId, companyId, classPK,
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

	protected WorkflowDefinitionLink getByG_C_CPK_PrevAndNext(
		Session session, WorkflowDefinitionLink workflowDefinitionLink,
		long groupId, long companyId, long classPK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
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

		sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

		sb.append(_FINDER_COLUMN_G_C_CPK_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CPK_COMPANYID_2);

		sb.append(_FINDER_COLUMN_G_C_CPK_CLASSPK_2);

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
			sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(companyId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						workflowDefinitionLink)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WorkflowDefinitionLink> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_C_CPK(long groupId, long companyId, long classPK) {
		for (WorkflowDefinitionLink workflowDefinitionLink :
				findByG_C_CPK(
					groupId, companyId, classPK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(workflowDefinitionLink);
		}
	}

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classPK the class pk
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByG_C_CPK(long groupId, long companyId, long classPK) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			FinderPath finderPath = _finderPathCountByG_C_CPK;

			Object[] finderArgs = new Object[] {groupId, companyId, classPK};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE);

				sb.append(_FINDER_COLUMN_G_C_CPK_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_CPK_COMPANYID_2);

				sb.append(_FINDER_COLUMN_G_C_CPK_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(companyId);

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

	private static final String _FINDER_COLUMN_G_C_CPK_GROUPID_2 =
		"workflowDefinitionLink.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_CPK_COMPANYID_2 =
		"workflowDefinitionLink.companyId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_CPK_CLASSPK_2 =
		"workflowDefinitionLink.classPK = ?";

	private FinderPath _finderPathWithPaginationFindByC_W_W;
	private FinderPath _finderPathWithoutPaginationFindByC_W_W;
	private FinderPath _finderPathCountByC_W_W;

	/**
	 * Returns all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @return the matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion) {

		return findByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion, int start, int end) {

		return findByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion, start,
			end, null);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return findByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion, start,
			end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion, int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			workflowDefinitionName = Objects.toString(
				workflowDefinitionName, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_W_W;
					finderArgs = new Object[] {
						companyId, workflowDefinitionName,
						workflowDefinitionVersion
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_W_W;
				finderArgs = new Object[] {
					companyId, workflowDefinitionName,
					workflowDefinitionVersion, start, end, orderByComparator
				};
			}

			List<WorkflowDefinitionLink> list = null;

			if (useFinderCache) {
				list = (List<WorkflowDefinitionLink>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WorkflowDefinitionLink workflowDefinitionLink : list) {
						if ((companyId !=
								workflowDefinitionLink.getCompanyId()) ||
							!workflowDefinitionName.equals(
								workflowDefinitionLink.
									getWorkflowDefinitionName()) ||
							(workflowDefinitionVersion !=
								workflowDefinitionLink.
									getWorkflowDefinitionVersion())) {

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

				sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

				sb.append(_FINDER_COLUMN_C_W_W_COMPANYID_2);

				boolean bindWorkflowDefinitionName = false;

				if (workflowDefinitionName.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_W_W_WORKFLOWDEFINITIONNAME_3);
				}
				else {
					bindWorkflowDefinitionName = true;

					sb.append(_FINDER_COLUMN_C_W_W_WORKFLOWDEFINITIONNAME_2);
				}

				sb.append(_FINDER_COLUMN_C_W_W_WORKFLOWDEFINITIONVERSION_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindWorkflowDefinitionName) {
						queryPos.add(workflowDefinitionName);
					}

					queryPos.add(workflowDefinitionVersion);

					list = (List<WorkflowDefinitionLink>)QueryUtil.list(
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
	 * Returns the first workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByC_W_W_First(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByC_W_W_First(
			companyId, workflowDefinitionName, workflowDefinitionVersion,
			orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", workflowDefinitionName=");
		sb.append(workflowDefinitionName);

		sb.append(", workflowDefinitionVersion=");
		sb.append(workflowDefinitionVersion);

		sb.append("}");

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the first workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByC_W_W_First(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		List<WorkflowDefinitionLink> list = findByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByC_W_W_Last(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByC_W_W_Last(
			companyId, workflowDefinitionName, workflowDefinitionVersion,
			orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", workflowDefinitionName=");
		sb.append(workflowDefinitionName);

		sb.append(", workflowDefinitionVersion=");
		sb.append(workflowDefinitionVersion);

		sb.append("}");

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the last workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByC_W_W_Last(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		int count = countByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion);

		if (count == 0) {
			return null;
		}

		List<WorkflowDefinitionLink> list = findByC_W_W(
			companyId, workflowDefinitionName, workflowDefinitionVersion,
			count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink[] findByC_W_W_PrevAndNext(
			long workflowDefinitionLinkId, long companyId,
			String workflowDefinitionName, int workflowDefinitionVersion,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		workflowDefinitionName = Objects.toString(workflowDefinitionName, "");

		WorkflowDefinitionLink workflowDefinitionLink = findByPrimaryKey(
			workflowDefinitionLinkId);

		Session session = null;

		try {
			session = openSession();

			WorkflowDefinitionLink[] array = new WorkflowDefinitionLinkImpl[3];

			array[0] = getByC_W_W_PrevAndNext(
				session, workflowDefinitionLink, companyId,
				workflowDefinitionName, workflowDefinitionVersion,
				orderByComparator, true);

			array[1] = workflowDefinitionLink;

			array[2] = getByC_W_W_PrevAndNext(
				session, workflowDefinitionLink, companyId,
				workflowDefinitionName, workflowDefinitionVersion,
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

	protected WorkflowDefinitionLink getByC_W_W_PrevAndNext(
		Session session, WorkflowDefinitionLink workflowDefinitionLink,
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
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

		sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

		sb.append(_FINDER_COLUMN_C_W_W_COMPANYID_2);

		boolean bindWorkflowDefinitionName = false;

		if (workflowDefinitionName.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_W_W_WORKFLOWDEFINITIONNAME_3);
		}
		else {
			bindWorkflowDefinitionName = true;

			sb.append(_FINDER_COLUMN_C_W_W_WORKFLOWDEFINITIONNAME_2);
		}

		sb.append(_FINDER_COLUMN_C_W_W_WORKFLOWDEFINITIONVERSION_2);

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
			sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindWorkflowDefinitionName) {
			queryPos.add(workflowDefinitionName);
		}

		queryPos.add(workflowDefinitionVersion);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						workflowDefinitionLink)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WorkflowDefinitionLink> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 */
	@Override
	public void removeByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion) {

		for (WorkflowDefinitionLink workflowDefinitionLink :
				findByC_W_W(
					companyId, workflowDefinitionName,
					workflowDefinitionVersion, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(workflowDefinitionLink);
		}
	}

	/**
	 * Returns the number of workflow definition links where companyId = &#63; and workflowDefinitionName = &#63; and workflowDefinitionVersion = &#63;.
	 *
	 * @param companyId the company ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param workflowDefinitionVersion the workflow definition version
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByC_W_W(
		long companyId, String workflowDefinitionName,
		int workflowDefinitionVersion) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			workflowDefinitionName = Objects.toString(
				workflowDefinitionName, "");

			FinderPath finderPath = _finderPathCountByC_W_W;

			Object[] finderArgs = new Object[] {
				companyId, workflowDefinitionName, workflowDefinitionVersion
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE);

				sb.append(_FINDER_COLUMN_C_W_W_COMPANYID_2);

				boolean bindWorkflowDefinitionName = false;

				if (workflowDefinitionName.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_W_W_WORKFLOWDEFINITIONNAME_3);
				}
				else {
					bindWorkflowDefinitionName = true;

					sb.append(_FINDER_COLUMN_C_W_W_WORKFLOWDEFINITIONNAME_2);
				}

				sb.append(_FINDER_COLUMN_C_W_W_WORKFLOWDEFINITIONVERSION_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindWorkflowDefinitionName) {
						queryPos.add(workflowDefinitionName);
					}

					queryPos.add(workflowDefinitionVersion);

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

	private static final String _FINDER_COLUMN_C_W_W_COMPANYID_2 =
		"workflowDefinitionLink.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_W_W_WORKFLOWDEFINITIONNAME_2 =
		"workflowDefinitionLink.workflowDefinitionName = ? AND ";

	private static final String _FINDER_COLUMN_C_W_W_WORKFLOWDEFINITIONNAME_3 =
		"(workflowDefinitionLink.workflowDefinitionName IS NULL OR workflowDefinitionLink.workflowDefinitionName = '') AND ";

	private static final String
		_FINDER_COLUMN_C_W_W_WORKFLOWDEFINITIONVERSION_2 =
			"workflowDefinitionLink.workflowDefinitionVersion = ?";

	private FinderPath _finderPathWithPaginationFindByG_C_C_C;
	private FinderPath _finderPathWithoutPaginationFindByG_C_C_C;
	private FinderPath _finderPathCountByG_C_C_C;

	/**
	 * Returns all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		return findByG_C_C_C(
			groupId, companyId, classNameId, classPK, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end) {

		return findByG_C_C_C(
			groupId, companyId, classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end, OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return findByG_C_C_C(
			groupId, companyId, classNameId, classPK, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK, int start,
		int end, OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

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

			List<WorkflowDefinitionLink> list = null;

			if (useFinderCache) {
				list = (List<WorkflowDefinitionLink>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (WorkflowDefinitionLink workflowDefinitionLink : list) {
						if ((groupId != workflowDefinitionLink.getGroupId()) ||
							(companyId !=
								workflowDefinitionLink.getCompanyId()) ||
							(classNameId !=
								workflowDefinitionLink.getClassNameId()) ||
							(classPK != workflowDefinitionLink.getClassPK())) {

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

				sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_C_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_COMPANYID_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
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

					list = (List<WorkflowDefinitionLink>)QueryUtil.list(
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
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByG_C_C_C_First(
			long groupId, long companyId, long classNameId, long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByG_C_C_C_First(
			groupId, companyId, classNameId, classPK, orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
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

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the first workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_C_C_First(
		long groupId, long companyId, long classNameId, long classPK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		List<WorkflowDefinitionLink> list = findByG_C_C_C(
			groupId, companyId, classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByG_C_C_C_Last(
			long groupId, long companyId, long classNameId, long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByG_C_C_C_Last(
			groupId, companyId, classNameId, classPK, orderByComparator);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
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

		throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
	}

	/**
	 * Returns the last workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_C_C_Last(
		long groupId, long companyId, long classNameId, long classPK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		int count = countByG_C_C_C(groupId, companyId, classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<WorkflowDefinitionLink> list = findByG_C_C_C(
			groupId, companyId, classNameId, classPK, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the workflow definition links before and after the current workflow definition link in the ordered set where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param workflowDefinitionLinkId the primary key of the current workflow definition link
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink[] findByG_C_C_C_PrevAndNext(
			long workflowDefinitionLinkId, long groupId, long companyId,
			long classNameId, long classPK,
			OrderByComparator<WorkflowDefinitionLink> orderByComparator)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = findByPrimaryKey(
			workflowDefinitionLinkId);

		Session session = null;

		try {
			session = openSession();

			WorkflowDefinitionLink[] array = new WorkflowDefinitionLinkImpl[3];

			array[0] = getByG_C_C_C_PrevAndNext(
				session, workflowDefinitionLink, groupId, companyId,
				classNameId, classPK, orderByComparator, true);

			array[1] = workflowDefinitionLink;

			array[2] = getByG_C_C_C_PrevAndNext(
				session, workflowDefinitionLink, groupId, companyId,
				classNameId, classPK, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected WorkflowDefinitionLink getByG_C_C_C_PrevAndNext(
		Session session, WorkflowDefinitionLink workflowDefinitionLink,
		long groupId, long companyId, long classNameId, long classPK,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
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

		sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

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
			sb.append(WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
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
						workflowDefinitionLink)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<WorkflowDefinitionLink> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		for (WorkflowDefinitionLink workflowDefinitionLink :
				findByG_C_C_C(
					groupId, companyId, classNameId, classPK, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(workflowDefinitionLink);
		}
	}

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByG_C_C_C(
		long groupId, long companyId, long classNameId, long classPK) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			FinderPath finderPath = _finderPathCountByG_C_C_C;

			Object[] finderArgs = new Object[] {
				groupId, companyId, classNameId, classPK
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE);

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
		"workflowDefinitionLink.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_C_COMPANYID_2 =
		"workflowDefinitionLink.companyId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_C_CLASSNAMEID_2 =
		"workflowDefinitionLink.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_C_CLASSPK_2 =
		"workflowDefinitionLink.classPK = ?";

	private FinderPath _finderPathFetchByG_C_C_W;

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63; or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @return the matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByG_C_C_W(
			long groupId, long companyId, long classNameId,
			String workflowDefinitionName)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByG_C_C_W(
			groupId, companyId, classNameId, workflowDefinitionName);

		if (workflowDefinitionLink == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", companyId=");
			sb.append(companyId);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append(", workflowDefinitionName=");
			sb.append(workflowDefinitionName);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
		}

		return workflowDefinitionLink;
	}

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_C_W(
		long groupId, long companyId, long classNameId,
		String workflowDefinitionName) {

		return fetchByG_C_C_W(
			groupId, companyId, classNameId, workflowDefinitionName, true);
	}

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_C_W(
		long groupId, long companyId, long classNameId,
		String workflowDefinitionName, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			workflowDefinitionName = Objects.toString(
				workflowDefinitionName, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, companyId, classNameId, workflowDefinitionName
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByG_C_C_W, finderArgs, this);
			}

			if (result instanceof WorkflowDefinitionLink) {
				WorkflowDefinitionLink workflowDefinitionLink =
					(WorkflowDefinitionLink)result;

				if ((groupId != workflowDefinitionLink.getGroupId()) ||
					(companyId != workflowDefinitionLink.getCompanyId()) ||
					(classNameId != workflowDefinitionLink.getClassNameId()) ||
					!Objects.equals(
						workflowDefinitionName,
						workflowDefinitionLink.getWorkflowDefinitionName())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(6);

				sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_W_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_W_COMPANYID_2);

				sb.append(_FINDER_COLUMN_G_C_C_W_CLASSNAMEID_2);

				boolean bindWorkflowDefinitionName = false;

				if (workflowDefinitionName.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_C_C_W_WORKFLOWDEFINITIONNAME_3);
				}
				else {
					bindWorkflowDefinitionName = true;

					sb.append(_FINDER_COLUMN_G_C_C_W_WORKFLOWDEFINITIONNAME_2);
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

					if (bindWorkflowDefinitionName) {
						queryPos.add(workflowDefinitionName);
					}

					List<WorkflowDefinitionLink> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByG_C_C_W, finderArgs, list);
						}
					}
					else {
						WorkflowDefinitionLink workflowDefinitionLink =
							list.get(0);

						result = workflowDefinitionLink;

						cacheResult(workflowDefinitionLink);
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
				return (WorkflowDefinitionLink)result;
			}
		}
	}

	/**
	 * Removes the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @return the workflow definition link that was removed
	 */
	@Override
	public WorkflowDefinitionLink removeByG_C_C_W(
			long groupId, long companyId, long classNameId,
			String workflowDefinitionName)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = findByG_C_C_W(
			groupId, companyId, classNameId, workflowDefinitionName);

		return remove(workflowDefinitionLink);
	}

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and workflowDefinitionName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param workflowDefinitionName the workflow definition name
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByG_C_C_W(
		long groupId, long companyId, long classNameId,
		String workflowDefinitionName) {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByG_C_C_W(
			groupId, companyId, classNameId, workflowDefinitionName);

		if (workflowDefinitionLink == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_C_C_W_GROUPID_2 =
		"workflowDefinitionLink.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_W_COMPANYID_2 =
		"workflowDefinitionLink.companyId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_W_CLASSNAMEID_2 =
		"workflowDefinitionLink.classNameId = ? AND ";

	private static final String
		_FINDER_COLUMN_G_C_C_W_WORKFLOWDEFINITIONNAME_2 =
			"workflowDefinitionLink.workflowDefinitionName = ?";

	private static final String
		_FINDER_COLUMN_G_C_C_W_WORKFLOWDEFINITIONNAME_3 =
			"(workflowDefinitionLink.workflowDefinitionName IS NULL OR workflowDefinitionLink.workflowDefinitionName = '')";

	private FinderPath _finderPathFetchByG_C_C_C_T;

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63; or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @return the matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByG_C_C_C_T(
			long groupId, long companyId, long classNameId, long classPK,
			long typePK)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByG_C_C_C_T(
			groupId, companyId, classNameId, classPK, typePK);

		if (workflowDefinitionLink == null) {
			StringBundler sb = new StringBundler(12);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", companyId=");
			sb.append(companyId);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append(", classPK=");
			sb.append(classPK);

			sb.append(", typePK=");
			sb.append(typePK);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
		}

		return workflowDefinitionLink;
	}

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_C_C_T(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK) {

		return fetchByG_C_C_C_T(
			groupId, companyId, classNameId, classPK, typePK, true);
	}

	/**
	 * Returns the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByG_C_C_C_T(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, companyId, classNameId, classPK, typePK
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByG_C_C_C_T, finderArgs, this);
			}

			if (result instanceof WorkflowDefinitionLink) {
				WorkflowDefinitionLink workflowDefinitionLink =
					(WorkflowDefinitionLink)result;

				if ((groupId != workflowDefinitionLink.getGroupId()) ||
					(companyId != workflowDefinitionLink.getCompanyId()) ||
					(classNameId != workflowDefinitionLink.getClassNameId()) ||
					(classPK != workflowDefinitionLink.getClassPK()) ||
					(typePK != workflowDefinitionLink.getTypePK())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(7);

				sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

				sb.append(_FINDER_COLUMN_G_C_C_C_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_T_COMPANYID_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_T_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_T_CLASSPK_2);

				sb.append(_FINDER_COLUMN_G_C_C_C_T_TYPEPK_2);

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

					queryPos.add(typePK);

					List<WorkflowDefinitionLink> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByG_C_C_C_T, finderArgs, list);
						}
					}
					else {
						if (list.size() > 1) {
							Collections.sort(list, Collections.reverseOrder());

							if (_log.isWarnEnabled()) {
								if (!useFinderCache) {
									finderArgs = new Object[] {
										groupId, companyId, classNameId,
										classPK, typePK
									};
								}

								_log.warn(
									"WorkflowDefinitionLinkPersistenceImpl.fetchByG_C_C_C_T(long, long, long, long, long, boolean) with parameters (" +
										StringUtil.merge(finderArgs) +
											") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
							}
						}

						WorkflowDefinitionLink workflowDefinitionLink =
							list.get(0);

						result = workflowDefinitionLink;

						cacheResult(workflowDefinitionLink);
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
				return (WorkflowDefinitionLink)result;
			}
		}
	}

	/**
	 * Removes the workflow definition link where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @return the workflow definition link that was removed
	 */
	@Override
	public WorkflowDefinitionLink removeByG_C_C_C_T(
			long groupId, long companyId, long classNameId, long classPK,
			long typePK)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = findByG_C_C_C_T(
			groupId, companyId, classNameId, classPK, typePK);

		return remove(workflowDefinitionLink);
	}

	/**
	 * Returns the number of workflow definition links where groupId = &#63; and companyId = &#63; and classNameId = &#63; and classPK = &#63; and typePK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param typePK the type pk
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByG_C_C_C_T(
		long groupId, long companyId, long classNameId, long classPK,
		long typePK) {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByG_C_C_C_T(
			groupId, companyId, classNameId, classPK, typePK);

		if (workflowDefinitionLink == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_C_C_C_T_GROUPID_2 =
		"workflowDefinitionLink.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_C_T_COMPANYID_2 =
		"workflowDefinitionLink.companyId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_C_T_CLASSNAMEID_2 =
		"workflowDefinitionLink.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_C_T_CLASSPK_2 =
		"workflowDefinitionLink.classPK = ? AND ";

	private static final String _FINDER_COLUMN_G_C_C_C_T_TYPEPK_2 =
		"workflowDefinitionLink.typePK = ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByERC_G(
			externalReferenceCode, groupId);

		if (workflowDefinitionLink == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchWorkflowDefinitionLinkException(sb.toString());
		}

		return workflowDefinitionLink;
	}

	/**
	 * Returns the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching workflow definition link, or <code>null</code> if a matching workflow definition link could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {externalReferenceCode, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByERC_G, finderArgs, this);
			}

			if (result instanceof WorkflowDefinitionLink) {
				WorkflowDefinitionLink workflowDefinitionLink =
					(WorkflowDefinitionLink)result;

				if (!Objects.equals(
						externalReferenceCode,
						workflowDefinitionLink.getExternalReferenceCode()) ||
					(groupId != workflowDefinitionLink.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(_FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(_FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_ERC_G_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindExternalReferenceCode) {
						queryPos.add(externalReferenceCode);
					}

					queryPos.add(groupId);

					List<WorkflowDefinitionLink> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						WorkflowDefinitionLink workflowDefinitionLink =
							list.get(0);

						result = workflowDefinitionLink;

						cacheResult(workflowDefinitionLink);
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
				return (WorkflowDefinitionLink)result;
			}
		}
	}

	/**
	 * Removes the workflow definition link where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the workflow definition link that was removed
	 */
	@Override
	public WorkflowDefinitionLink removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = findByERC_G(
			externalReferenceCode, groupId);

		return remove(workflowDefinitionLink);
	}

	/**
	 * Returns the number of workflow definition links where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching workflow definition links
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		WorkflowDefinitionLink workflowDefinitionLink = fetchByERC_G(
			externalReferenceCode, groupId);

		if (workflowDefinitionLink == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"workflowDefinitionLink.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(workflowDefinitionLink.externalReferenceCode IS NULL OR workflowDefinitionLink.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"workflowDefinitionLink.groupId = ?";

	public WorkflowDefinitionLinkPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(WorkflowDefinitionLink.class);

		setModelImplClass(WorkflowDefinitionLinkImpl.class);
		setModelPKClass(long.class);

		setTable(WorkflowDefinitionLinkTable.INSTANCE);
	}

	/**
	 * Caches the workflow definition link in the entity cache if it is enabled.
	 *
	 * @param workflowDefinitionLink the workflow definition link
	 */
	@Override
	public void cacheResult(WorkflowDefinitionLink workflowDefinitionLink) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					workflowDefinitionLink.getCtCollectionId())) {

			EntityCacheUtil.putResult(
				WorkflowDefinitionLinkImpl.class,
				workflowDefinitionLink.getPrimaryKey(), workflowDefinitionLink);

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					workflowDefinitionLink.getUuid(),
					workflowDefinitionLink.getGroupId()
				},
				workflowDefinitionLink);

			FinderCacheUtil.putResult(
				_finderPathFetchByG_C_C_W,
				new Object[] {
					workflowDefinitionLink.getGroupId(),
					workflowDefinitionLink.getCompanyId(),
					workflowDefinitionLink.getClassNameId(),
					workflowDefinitionLink.getWorkflowDefinitionName()
				},
				workflowDefinitionLink);

			FinderCacheUtil.putResult(
				_finderPathFetchByG_C_C_C_T,
				new Object[] {
					workflowDefinitionLink.getGroupId(),
					workflowDefinitionLink.getCompanyId(),
					workflowDefinitionLink.getClassNameId(),
					workflowDefinitionLink.getClassPK(),
					workflowDefinitionLink.getTypePK()
				},
				workflowDefinitionLink);

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					workflowDefinitionLink.getExternalReferenceCode(),
					workflowDefinitionLink.getGroupId()
				},
				workflowDefinitionLink);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the workflow definition links in the entity cache if it is enabled.
	 *
	 * @param workflowDefinitionLinks the workflow definition links
	 */
	@Override
	public void cacheResult(
		List<WorkflowDefinitionLink> workflowDefinitionLinks) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (workflowDefinitionLinks.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (WorkflowDefinitionLink workflowDefinitionLink :
				workflowDefinitionLinks) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						workflowDefinitionLink.getCtCollectionId())) {

				if (EntityCacheUtil.getResult(
						WorkflowDefinitionLinkImpl.class,
						workflowDefinitionLink.getPrimaryKey()) == null) {

					cacheResult(workflowDefinitionLink);
				}
			}
		}
	}

	/**
	 * Clears the cache for all workflow definition links.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(WorkflowDefinitionLinkImpl.class);

		FinderCacheUtil.clearCache(WorkflowDefinitionLinkImpl.class);
	}

	/**
	 * Clears the cache for the workflow definition link.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(WorkflowDefinitionLink workflowDefinitionLink) {
		EntityCacheUtil.removeResult(
			WorkflowDefinitionLinkImpl.class, workflowDefinitionLink);
	}

	@Override
	public void clearCache(
		List<WorkflowDefinitionLink> workflowDefinitionLinks) {

		for (WorkflowDefinitionLink workflowDefinitionLink :
				workflowDefinitionLinks) {

			EntityCacheUtil.removeResult(
				WorkflowDefinitionLinkImpl.class, workflowDefinitionLink);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(WorkflowDefinitionLinkImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(
				WorkflowDefinitionLinkImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		WorkflowDefinitionLinkModelImpl workflowDefinitionLinkModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					workflowDefinitionLinkModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				workflowDefinitionLinkModelImpl.getUuid(),
				workflowDefinitionLinkModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G, args,
				workflowDefinitionLinkModelImpl);

			args = new Object[] {
				workflowDefinitionLinkModelImpl.getGroupId(),
				workflowDefinitionLinkModelImpl.getCompanyId(),
				workflowDefinitionLinkModelImpl.getClassNameId(),
				workflowDefinitionLinkModelImpl.getWorkflowDefinitionName()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByG_C_C_W, args,
				workflowDefinitionLinkModelImpl);

			args = new Object[] {
				workflowDefinitionLinkModelImpl.getGroupId(),
				workflowDefinitionLinkModelImpl.getCompanyId(),
				workflowDefinitionLinkModelImpl.getClassNameId(),
				workflowDefinitionLinkModelImpl.getClassPK(),
				workflowDefinitionLinkModelImpl.getTypePK()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByG_C_C_C_T, args,
				workflowDefinitionLinkModelImpl);

			args = new Object[] {
				workflowDefinitionLinkModelImpl.getExternalReferenceCode(),
				workflowDefinitionLinkModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G, args, workflowDefinitionLinkModelImpl);
		}
	}

	/**
	 * Creates a new workflow definition link with the primary key. Does not add the workflow definition link to the database.
	 *
	 * @param workflowDefinitionLinkId the primary key for the new workflow definition link
	 * @return the new workflow definition link
	 */
	@Override
	public WorkflowDefinitionLink create(long workflowDefinitionLinkId) {
		WorkflowDefinitionLink workflowDefinitionLink =
			new WorkflowDefinitionLinkImpl();

		workflowDefinitionLink.setNew(true);
		workflowDefinitionLink.setPrimaryKey(workflowDefinitionLinkId);

		String uuid = PortalUUIDUtil.generate();

		workflowDefinitionLink.setUuid(uuid);

		workflowDefinitionLink.setCompanyId(CompanyThreadLocal.getCompanyId());

		return workflowDefinitionLink;
	}

	/**
	 * Removes the workflow definition link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link that was removed
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink remove(long workflowDefinitionLinkId)
		throws NoSuchWorkflowDefinitionLinkException {

		return remove((Serializable)workflowDefinitionLinkId);
	}

	/**
	 * Removes the workflow definition link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the workflow definition link
	 * @return the workflow definition link that was removed
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink remove(Serializable primaryKey)
		throws NoSuchWorkflowDefinitionLinkException {

		Session session = null;

		try {
			session = openSession();

			WorkflowDefinitionLink workflowDefinitionLink =
				(WorkflowDefinitionLink)session.get(
					WorkflowDefinitionLinkImpl.class, primaryKey);

			if (workflowDefinitionLink == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchWorkflowDefinitionLinkException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(workflowDefinitionLink);
		}
		catch (NoSuchWorkflowDefinitionLinkException noSuchEntityException) {
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
	protected WorkflowDefinitionLink removeImpl(
		WorkflowDefinitionLink workflowDefinitionLink) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(workflowDefinitionLink)) {
				workflowDefinitionLink = (WorkflowDefinitionLink)session.get(
					WorkflowDefinitionLinkImpl.class,
					workflowDefinitionLink.getPrimaryKeyObj());
			}

			if ((workflowDefinitionLink != null) &&
				CTPersistenceHelperUtil.isRemove(workflowDefinitionLink)) {

				session.delete(workflowDefinitionLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (workflowDefinitionLink != null) {
			clearCache(workflowDefinitionLink);
		}

		return workflowDefinitionLink;
	}

	@Override
	public WorkflowDefinitionLink updateImpl(
		WorkflowDefinitionLink workflowDefinitionLink) {

		boolean isNew = workflowDefinitionLink.isNew();

		if (!(workflowDefinitionLink instanceof
				WorkflowDefinitionLinkModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(workflowDefinitionLink.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					workflowDefinitionLink);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in workflowDefinitionLink proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom WorkflowDefinitionLink implementation " +
					workflowDefinitionLink.getClass());
		}

		WorkflowDefinitionLinkModelImpl workflowDefinitionLinkModelImpl =
			(WorkflowDefinitionLinkModelImpl)workflowDefinitionLink;

		if (Validator.isNull(workflowDefinitionLink.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			workflowDefinitionLink.setUuid(uuid);
		}

		if (Validator.isNull(
				workflowDefinitionLink.getExternalReferenceCode())) {

			workflowDefinitionLink.setExternalReferenceCode(
				workflowDefinitionLink.getUuid());
		}
		else {
			if (!Objects.equals(
					workflowDefinitionLinkModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					workflowDefinitionLink.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = workflowDefinitionLink.getCompanyId();

					long groupId = workflowDefinitionLink.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = workflowDefinitionLink.getPrimaryKey();
					}

					try {
						workflowDefinitionLink.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								WorkflowDefinitionLink.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								workflowDefinitionLink.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			WorkflowDefinitionLink ercWorkflowDefinitionLink = fetchByERC_G(
				workflowDefinitionLink.getExternalReferenceCode(),
				workflowDefinitionLink.getGroupId());

			if (isNew) {
				if (ercWorkflowDefinitionLink != null) {
					throw new DuplicateWorkflowDefinitionLinkExternalReferenceCodeException(
						"Duplicate workflow definition link with external reference code " +
							workflowDefinitionLink.getExternalReferenceCode() +
								" and group " +
									workflowDefinitionLink.getGroupId());
				}
			}
			else {
				if ((ercWorkflowDefinitionLink != null) &&
					(workflowDefinitionLink.getWorkflowDefinitionLinkId() !=
						ercWorkflowDefinitionLink.
							getWorkflowDefinitionLinkId())) {

					throw new DuplicateWorkflowDefinitionLinkExternalReferenceCodeException(
						"Duplicate workflow definition link with external reference code " +
							workflowDefinitionLink.getExternalReferenceCode() +
								" and group " +
									workflowDefinitionLink.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (workflowDefinitionLink.getCreateDate() == null)) {
			if (serviceContext == null) {
				workflowDefinitionLink.setCreateDate(date);
			}
			else {
				workflowDefinitionLink.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!workflowDefinitionLinkModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				workflowDefinitionLink.setModifiedDate(date);
			}
			else {
				workflowDefinitionLink.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(workflowDefinitionLink)) {
				if (!isNew) {
					session.evict(
						WorkflowDefinitionLinkImpl.class,
						workflowDefinitionLink.getPrimaryKeyObj());
				}

				session.save(workflowDefinitionLink);
			}
			else {
				workflowDefinitionLink = (WorkflowDefinitionLink)session.merge(
					workflowDefinitionLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(
			WorkflowDefinitionLinkImpl.class, workflowDefinitionLinkModelImpl,
			false, true);

		cacheUniqueFindersCache(workflowDefinitionLinkModelImpl);

		if (isNew) {
			workflowDefinitionLink.setNew(false);
		}

		workflowDefinitionLink.resetOriginalValues();

		return workflowDefinitionLink;
	}

	/**
	 * Returns the workflow definition link with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the workflow definition link
	 * @return the workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByPrimaryKey(Serializable primaryKey)
		throws NoSuchWorkflowDefinitionLinkException {

		WorkflowDefinitionLink workflowDefinitionLink = fetchByPrimaryKey(
			primaryKey);

		if (workflowDefinitionLink == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchWorkflowDefinitionLinkException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return workflowDefinitionLink;
	}

	/**
	 * Returns the workflow definition link with the primary key or throws a <code>NoSuchWorkflowDefinitionLinkException</code> if it could not be found.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link
	 * @throws NoSuchWorkflowDefinitionLinkException if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink findByPrimaryKey(
			long workflowDefinitionLinkId)
		throws NoSuchWorkflowDefinitionLinkException {

		return findByPrimaryKey((Serializable)workflowDefinitionLinkId);
	}

	/**
	 * Returns the workflow definition link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the workflow definition link
	 * @return the workflow definition link, or <code>null</code> if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByPrimaryKey(Serializable primaryKey) {
		if (CTPersistenceHelperUtil.isProductionMode(
				WorkflowDefinitionLink.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		WorkflowDefinitionLink workflowDefinitionLink =
			(WorkflowDefinitionLink)EntityCacheUtil.getResult(
				WorkflowDefinitionLinkImpl.class, primaryKey);

		if (workflowDefinitionLink != null) {
			return workflowDefinitionLink;
		}

		Session session = null;

		try {
			session = openSession();

			workflowDefinitionLink = (WorkflowDefinitionLink)session.get(
				WorkflowDefinitionLinkImpl.class, primaryKey);

			if (workflowDefinitionLink != null) {
				cacheResult(workflowDefinitionLink);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return workflowDefinitionLink;
	}

	/**
	 * Returns the workflow definition link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param workflowDefinitionLinkId the primary key of the workflow definition link
	 * @return the workflow definition link, or <code>null</code> if a workflow definition link with the primary key could not be found
	 */
	@Override
	public WorkflowDefinitionLink fetchByPrimaryKey(
		long workflowDefinitionLinkId) {

		return fetchByPrimaryKey((Serializable)workflowDefinitionLinkId);
	}

	@Override
	public Map<Serializable, WorkflowDefinitionLink> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (CTPersistenceHelperUtil.isProductionMode(
				WorkflowDefinitionLink.class)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, WorkflowDefinitionLink> map =
			new HashMap<Serializable, WorkflowDefinitionLink>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			WorkflowDefinitionLink workflowDefinitionLink = fetchByPrimaryKey(
				primaryKey);

			if (workflowDefinitionLink != null) {
				map.put(primaryKey, workflowDefinitionLink);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
						WorkflowDefinitionLink.class, primaryKey)) {

				WorkflowDefinitionLink workflowDefinitionLink =
					(WorkflowDefinitionLink)EntityCacheUtil.getResult(
						WorkflowDefinitionLinkImpl.class, primaryKey);

				if (workflowDefinitionLink == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, workflowDefinitionLink);
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

			for (WorkflowDefinitionLink workflowDefinitionLink :
					(List<WorkflowDefinitionLink>)query.list()) {

				map.put(
					workflowDefinitionLink.getPrimaryKeyObj(),
					workflowDefinitionLink);

				cacheResult(workflowDefinitionLink);
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
	 * Returns all the workflow definition links.
	 *
	 * @return the workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the workflow definition links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @return the range of workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the workflow definition links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findAll(
		int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the workflow definition links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>WorkflowDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of workflow definition links
	 * @param end the upper bound of the range of workflow definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of workflow definition links
	 */
	@Override
	public List<WorkflowDefinitionLink> findAll(
		int start, int end,
		OrderByComparator<WorkflowDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

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

			List<WorkflowDefinitionLink> list = null;

			if (useFinderCache) {
				list = (List<WorkflowDefinitionLink>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_WORKFLOWDEFINITIONLINK);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_WORKFLOWDEFINITIONLINK;

					sql = sql.concat(
						WorkflowDefinitionLinkModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<WorkflowDefinitionLink>)QueryUtil.list(
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
	 * Removes all the workflow definition links from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (WorkflowDefinitionLink workflowDefinitionLink : findAll()) {
			remove(workflowDefinitionLink);
		}
	}

	/**
	 * Returns the number of workflow definition links.
	 *
	 * @return the number of workflow definition links
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					WorkflowDefinitionLink.class)) {

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_WORKFLOWDEFINITIONLINK);

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
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "workflowDefinitionLinkId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_WORKFLOWDEFINITIONLINK;
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
		return WorkflowDefinitionLinkModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "WorkflowDefinitionLink";
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
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("typePK");
		ctMergeColumnNames.add("workflowDefinitionName");
		ctMergeColumnNames.add("workflowDefinitionVersion");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("workflowDefinitionLinkId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"groupId", "companyId", "classNameId", "workflowDefinitionName"
			});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the workflow definition link persistence.
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

		_finderPathWithPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"uuid_"}, true);

		_finderPathWithoutPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			true);

		_finderPathCountByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			false);

		_finderPathFetchByUUID_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "groupId"}, true);

		_finderPathWithPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathWithoutPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathCountByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, false);

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

		_finderPathWithPaginationFindByG_C_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_CPK",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "companyId", "classPK"}, true);

		_finderPathWithoutPaginationFindByG_C_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_CPK",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "companyId", "classPK"}, true);

		_finderPathCountByG_C_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_CPK",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "companyId", "classPK"}, false);

		_finderPathWithPaginationFindByC_W_W = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_W_W",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"companyId", "workflowDefinitionName",
				"workflowDefinitionVersion"
			},
			true);

		_finderPathWithoutPaginationFindByC_W_W = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_W_W",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName()
			},
			new String[] {
				"companyId", "workflowDefinitionName",
				"workflowDefinitionVersion"
			},
			true);

		_finderPathCountByC_W_W = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_W_W",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName()
			},
			new String[] {
				"companyId", "workflowDefinitionName",
				"workflowDefinitionVersion"
			},
			false);

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

		_finderPathFetchByG_C_C_W = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_C_C_W",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), String.class.getName()
			},
			new String[] {
				"groupId", "companyId", "classNameId", "workflowDefinitionName"
			},
			true);

		_finderPathFetchByG_C_C_C_T = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_C_C_C_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {
				"groupId", "companyId", "classNameId", "classPK", "typePK"
			},
			true);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		WorkflowDefinitionLinkUtil.setPersistence(this);
	}

	public void destroy() {
		WorkflowDefinitionLinkUtil.setPersistence(null);

		EntityCacheUtil.removeCache(WorkflowDefinitionLinkImpl.class.getName());
	}

	private static final String _SQL_SELECT_WORKFLOWDEFINITIONLINK =
		"SELECT workflowDefinitionLink FROM WorkflowDefinitionLink workflowDefinitionLink";

	private static final String _SQL_SELECT_WORKFLOWDEFINITIONLINK_WHERE =
		"SELECT workflowDefinitionLink FROM WorkflowDefinitionLink workflowDefinitionLink WHERE ";

	private static final String _SQL_COUNT_WORKFLOWDEFINITIONLINK =
		"SELECT COUNT(workflowDefinitionLink) FROM WorkflowDefinitionLink workflowDefinitionLink";

	private static final String _SQL_COUNT_WORKFLOWDEFINITIONLINK_WHERE =
		"SELECT COUNT(workflowDefinitionLink) FROM WorkflowDefinitionLink workflowDefinitionLink WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"workflowDefinitionLink.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No WorkflowDefinitionLink exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No WorkflowDefinitionLink exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowDefinitionLinkPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}