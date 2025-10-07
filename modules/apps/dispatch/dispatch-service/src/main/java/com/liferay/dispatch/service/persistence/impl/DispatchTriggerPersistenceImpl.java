/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.service.persistence.impl;

import com.liferay.dispatch.exception.DuplicateDispatchTriggerExternalReferenceCodeException;
import com.liferay.dispatch.exception.NoSuchTriggerException;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.model.DispatchTriggerTable;
import com.liferay.dispatch.model.impl.DispatchTriggerImpl;
import com.liferay.dispatch.model.impl.DispatchTriggerModelImpl;
import com.liferay.dispatch.service.persistence.DispatchTriggerPersistence;
import com.liferay.dispatch.service.persistence.DispatchTriggerUtil;
import com.liferay.dispatch.service.persistence.impl.constants.DispatchPersistenceConstants;
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
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
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
 * The persistence implementation for the dispatch trigger service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matija Petanjek
 * @generated
 */
@Component(service = DispatchTriggerPersistence.class)
public class DispatchTriggerPersistenceImpl
	extends BasePersistenceImpl<DispatchTrigger>
	implements DispatchTriggerPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DispatchTriggerUtil</code> to access the dispatch trigger persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DispatchTriggerImpl.class.getName();

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
	 * Returns all the dispatch triggers where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

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

		List<DispatchTrigger> list = null;

		if (useFinderCache) {
			list = (List<DispatchTrigger>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (DispatchTrigger dispatchTrigger : list) {
					if (!uuid.equals(dispatchTrigger.getUuid())) {
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

			sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

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
				sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
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

				list = (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the first dispatch trigger in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByUuid_First(
			String uuid, OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByUuid_First(
			uuid, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByUuid_First(
		String uuid, OrderByComparator<DispatchTrigger> orderByComparator) {

		List<DispatchTrigger> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByUuid_Last(
			String uuid, OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByUuid_Last(
			uuid, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByUuid_Last(
		String uuid, OrderByComparator<DispatchTrigger> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<DispatchTrigger> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set where uuid = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] findByUuid_PrevAndNext(
			long dispatchTriggerId, String uuid,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		uuid = Objects.toString(uuid, "");

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, dispatchTrigger, uuid, orderByComparator, true);

			array[1] = dispatchTrigger;

			array[2] = getByUuid_PrevAndNext(
				session, dispatchTrigger, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DispatchTrigger getByUuid_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, String uuid,
		OrderByComparator<DispatchTrigger> orderByComparator,
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

		sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

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
			sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
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
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the dispatch triggers that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByUuid(String uuid) {
		return filterFindByUuid(
			uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByUuid(
		String uuid, int start, int end) {

		return filterFindByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUuid(uuid, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByUuid(
					uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			return (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set of dispatch triggers that the user has permission to view where uuid = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] filterFindByUuid_PrevAndNext(
			long dispatchTriggerId, String uuid,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUuid_PrevAndNext(
				dispatchTriggerId, uuid, orderByComparator);
		}

		uuid = Objects.toString(uuid, "");

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = filterGetByUuid_PrevAndNext(
				session, dispatchTrigger, uuid, orderByComparator, true);

			array[1] = dispatchTrigger;

			array[2] = filterGetByUuid_PrevAndNext(
				session, dispatchTrigger, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DispatchTrigger filterGetByUuid_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, String uuid,
		OrderByComparator<DispatchTrigger> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the dispatch triggers where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (DispatchTrigger dispatchTrigger :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dispatchTrigger);
		}
	}

	/**
	 * Returns the number of dispatch triggers where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByUuid(String uuid) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid;

		Object[] finderArgs = new Object[] {uuid};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_DISPATCHTRIGGER_WHERE);

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
	 * Returns the number of dispatch triggers that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByUuid(uuid);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DispatchTrigger> dispatchTriggers = findByUuid(uuid);

			dispatchTriggers = InlineSQLHelperUtil.filter(dispatchTriggers);

			return dispatchTriggers.size();
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_DISPATCHTRIGGER_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
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

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"dispatchTrigger.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(dispatchTrigger.uuid IS NULL OR dispatchTrigger.uuid = '')";

	private static final String _FINDER_COLUMN_UUID_UUID_2_SQL =
		"dispatchTrigger.uuid_ = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3_SQL =
		"(dispatchTrigger.uuid_ IS NULL OR dispatchTrigger.uuid_ = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the dispatch triggers where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

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

		List<DispatchTrigger> list = null;

		if (useFinderCache) {
			list = (List<DispatchTrigger>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (DispatchTrigger dispatchTrigger : list) {
					if (!uuid.equals(dispatchTrigger.getUuid()) ||
						(companyId != dispatchTrigger.getCompanyId())) {

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

			sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

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
				sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
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

				list = (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the first dispatch trigger in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		List<DispatchTrigger> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<DispatchTrigger> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] findByUuid_C_PrevAndNext(
			long dispatchTriggerId, String uuid, long companyId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		uuid = Objects.toString(uuid, "");

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, dispatchTrigger, uuid, companyId, orderByComparator,
				true);

			array[1] = dispatchTrigger;

			array[2] = getByUuid_C_PrevAndNext(
				session, dispatchTrigger, uuid, companyId, orderByComparator,
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

	protected DispatchTrigger getByUuid_C_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, String uuid,
		long companyId, OrderByComparator<DispatchTrigger> orderByComparator,
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

		sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

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
			sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
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
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the dispatch triggers that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByUuid_C(
		String uuid, long companyId) {

		return filterFindByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByUuid_C(
		String uuid, long companyId, int start, int end) {

		return filterFindByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByUuid_C(uuid, companyId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator));
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2_SQL);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			queryPos.add(companyId);

			return (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set of dispatch triggers that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] filterFindByUuid_C_PrevAndNext(
			long dispatchTriggerId, String uuid, long companyId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByUuid_C_PrevAndNext(
				dispatchTriggerId, uuid, companyId, orderByComparator);
		}

		uuid = Objects.toString(uuid, "");

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = filterGetByUuid_C_PrevAndNext(
				session, dispatchTrigger, uuid, companyId, orderByComparator,
				true);

			array[1] = dispatchTrigger;

			array[2] = filterGetByUuid_C_PrevAndNext(
				session, dispatchTrigger, uuid, companyId, orderByComparator,
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

	protected DispatchTrigger filterGetByUuid_C_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, String uuid,
		long companyId, OrderByComparator<DispatchTrigger> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2_SQL);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the dispatch triggers where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (DispatchTrigger dispatchTrigger :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dispatchTrigger);
		}
	}

	/**
	 * Returns the number of dispatch triggers where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		uuid = Objects.toString(uuid, "");

		FinderPath finderPath = _finderPathCountByUuid_C;

		Object[] finderArgs = new Object[] {uuid, companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_DISPATCHTRIGGER_WHERE);

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
	 * Returns the number of dispatch triggers that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByUuid_C(uuid, companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DispatchTrigger> dispatchTriggers = findByUuid_C(
				uuid, companyId);

			dispatchTriggers = InlineSQLHelperUtil.filter(dispatchTriggers);

			return dispatchTriggers.size();
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_DISPATCHTRIGGER_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2_SQL);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

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

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"dispatchTrigger.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(dispatchTrigger.uuid IS NULL OR dispatchTrigger.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_2_SQL =
		"dispatchTrigger.uuid_ = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3_SQL =
		"(dispatchTrigger.uuid_ IS NULL OR dispatchTrigger.uuid_ = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"dispatchTrigger.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the dispatch triggers where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
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

		List<DispatchTrigger> list = null;

		if (useFinderCache) {
			list = (List<DispatchTrigger>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (DispatchTrigger dispatchTrigger : list) {
					if (companyId != dispatchTrigger.getCompanyId()) {
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

			sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

			sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				list = (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the first dispatch trigger in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByCompanyId_First(
			long companyId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByCompanyId_First(
		long companyId, OrderByComparator<DispatchTrigger> orderByComparator) {

		List<DispatchTrigger> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByCompanyId_Last(
			long companyId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByCompanyId_Last(
		long companyId, OrderByComparator<DispatchTrigger> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<DispatchTrigger> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set where companyId = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] findByCompanyId_PrevAndNext(
			long dispatchTriggerId, long companyId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, dispatchTrigger, companyId, orderByComparator, true);

			array[1] = dispatchTrigger;

			array[2] = getByCompanyId_PrevAndNext(
				session, dispatchTrigger, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DispatchTrigger getByCompanyId_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, long companyId,
		OrderByComparator<DispatchTrigger> orderByComparator,
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

		sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

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
			sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
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
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the dispatch triggers that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByCompanyId(long companyId) {
		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set of dispatch triggers that the user has permission to view where companyId = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] filterFindByCompanyId_PrevAndNext(
			long dispatchTriggerId, long companyId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				dispatchTriggerId, companyId, orderByComparator);
		}

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, dispatchTrigger, companyId, orderByComparator, true);

			array[1] = dispatchTrigger;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, dispatchTrigger, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DispatchTrigger filterGetByCompanyId_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, long companyId,
		OrderByComparator<DispatchTrigger> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the dispatch triggers where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (DispatchTrigger dispatchTrigger :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dispatchTrigger);
		}
	}

	/**
	 * Returns the number of dispatch triggers where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByCompanyId(long companyId) {
		FinderPath finderPath = _finderPathCountByCompanyId;

		Object[] finderArgs = new Object[] {companyId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_DISPATCHTRIGGER_WHERE);

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
	 * Returns the number of dispatch triggers that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DispatchTrigger> dispatchTriggers = findByCompanyId(companyId);

			dispatchTriggers = InlineSQLHelperUtil.filter(dispatchTriggers);

			return dispatchTriggers.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_DISPATCHTRIGGER_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
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
		"dispatchTrigger.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByActive;
	private FinderPath _finderPathWithoutPaginationFindByActive;
	private FinderPath _finderPathCountByActive;

	/**
	 * Returns all the dispatch triggers where active = &#63;.
	 *
	 * @param active the active
	 * @return the matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByActive(boolean active) {
		return findByActive(active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers where active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByActive(
		boolean active, int start, int end) {

		return findByActive(active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByActive(
		boolean active, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return findByActive(active, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByActive(
		boolean active, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByActive;
				finderArgs = new Object[] {active};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByActive;
			finderArgs = new Object[] {active, start, end, orderByComparator};
		}

		List<DispatchTrigger> list = null;

		if (useFinderCache) {
			list = (List<DispatchTrigger>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (DispatchTrigger dispatchTrigger : list) {
					if (active != dispatchTrigger.isActive()) {
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

			sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

			sb.append(_FINDER_COLUMN_ACTIVE_ACTIVE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(active);

				list = (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the first dispatch trigger in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByActive_First(
			boolean active,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByActive_First(
			active, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByActive_First(
		boolean active, OrderByComparator<DispatchTrigger> orderByComparator) {

		List<DispatchTrigger> list = findByActive(
			active, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByActive_Last(
			boolean active,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByActive_Last(
			active, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByActive_Last(
		boolean active, OrderByComparator<DispatchTrigger> orderByComparator) {

		int count = countByActive(active);

		if (count == 0) {
			return null;
		}

		List<DispatchTrigger> list = findByActive(
			active, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set where active = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] findByActive_PrevAndNext(
			long dispatchTriggerId, boolean active,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = getByActive_PrevAndNext(
				session, dispatchTrigger, active, orderByComparator, true);

			array[1] = dispatchTrigger;

			array[2] = getByActive_PrevAndNext(
				session, dispatchTrigger, active, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DispatchTrigger getByActive_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, boolean active,
		OrderByComparator<DispatchTrigger> orderByComparator,
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

		sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

		sb.append(_FINDER_COLUMN_ACTIVE_ACTIVE_2);

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
			sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the dispatch triggers that the user has permission to view where active = &#63;.
	 *
	 * @param active the active
	 * @return the matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByActive(boolean active) {
		return filterFindByActive(
			active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers that the user has permission to view where active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByActive(
		boolean active, int start, int end) {

		return filterFindByActive(active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByActive(
		boolean active, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByActive(active, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByActive(
					active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_ACTIVE_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(active);

			return (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set of dispatch triggers that the user has permission to view where active = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] filterFindByActive_PrevAndNext(
			long dispatchTriggerId, boolean active,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByActive_PrevAndNext(
				dispatchTriggerId, active, orderByComparator);
		}

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = filterGetByActive_PrevAndNext(
				session, dispatchTrigger, active, orderByComparator, true);

			array[1] = dispatchTrigger;

			array[2] = filterGetByActive_PrevAndNext(
				session, dispatchTrigger, active, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DispatchTrigger filterGetByActive_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, boolean active,
		OrderByComparator<DispatchTrigger> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_ACTIVE_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the dispatch triggers where active = &#63; from the database.
	 *
	 * @param active the active
	 */
	@Override
	public void removeByActive(boolean active) {
		for (DispatchTrigger dispatchTrigger :
				findByActive(
					active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(dispatchTrigger);
		}
	}

	/**
	 * Returns the number of dispatch triggers where active = &#63;.
	 *
	 * @param active the active
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByActive(boolean active) {
		FinderPath finderPath = _finderPathCountByActive;

		Object[] finderArgs = new Object[] {active};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_DISPATCHTRIGGER_WHERE);

			sb.append(_FINDER_COLUMN_ACTIVE_ACTIVE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(active);

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
	 * Returns the number of dispatch triggers that the user has permission to view where active = &#63;.
	 *
	 * @param active the active
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByActive(boolean active) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByActive(active);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DispatchTrigger> dispatchTriggers = findByActive(active);

			dispatchTriggers = InlineSQLHelperUtil.filter(dispatchTriggers);

			return dispatchTriggers.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_DISPATCHTRIGGER_WHERE);

		sb.append(_FINDER_COLUMN_ACTIVE_ACTIVE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(active);

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

	private static final String _FINDER_COLUMN_ACTIVE_ACTIVE_2 =
		"dispatchTrigger.active = ?";

	private static final String _FINDER_COLUMN_ACTIVE_ACTIVE_2_SQL =
		"dispatchTrigger.active_ = ?";

	private FinderPath _finderPathWithPaginationFindByC_U;
	private FinderPath _finderPathWithoutPaginationFindByC_U;
	private FinderPath _finderPathCountByC_U;

	/**
	 * Returns all the dispatch triggers where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByC_U(long companyId, long userId) {
		return findByC_U(
			companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByC_U(
		long companyId, long userId, int start, int end) {

		return findByC_U(companyId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return findByC_U(
			companyId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
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

		List<DispatchTrigger> list = null;

		if (useFinderCache) {
			list = (List<DispatchTrigger>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (DispatchTrigger dispatchTrigger : list) {
					if ((companyId != dispatchTrigger.getCompanyId()) ||
						(userId != dispatchTrigger.getUserId())) {

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

			sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

			sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

			sb.append(_FINDER_COLUMN_C_U_USERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				queryPos.add(userId);

				list = (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the first dispatch trigger in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByC_U_First(
			long companyId, long userId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByC_U_First(
			companyId, userId, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		List<DispatchTrigger> list = findByC_U(
			companyId, userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByC_U_Last(
			long companyId, long userId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByC_U_Last(
			companyId, userId, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByC_U_Last(
		long companyId, long userId,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		int count = countByC_U(companyId, userId);

		if (count == 0) {
			return null;
		}

		List<DispatchTrigger> list = findByC_U(
			companyId, userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] findByC_U_PrevAndNext(
			long dispatchTriggerId, long companyId, long userId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = getByC_U_PrevAndNext(
				session, dispatchTrigger, companyId, userId, orderByComparator,
				true);

			array[1] = dispatchTrigger;

			array[2] = getByC_U_PrevAndNext(
				session, dispatchTrigger, companyId, userId, orderByComparator,
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

	protected DispatchTrigger getByC_U_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, long companyId,
		long userId, OrderByComparator<DispatchTrigger> orderByComparator,
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

		sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

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
			sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
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
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the dispatch triggers that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByC_U(long companyId, long userId) {
		return filterFindByC_U(
			companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByC_U(
		long companyId, long userId, int start, int end) {

		return filterFindByC_U(companyId, userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_U_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(userId);

			return (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set of dispatch triggers that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] filterFindByC_U_PrevAndNext(
			long dispatchTriggerId, long companyId, long userId,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_U_PrevAndNext(
				dispatchTriggerId, companyId, userId, orderByComparator);
		}

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = filterGetByC_U_PrevAndNext(
				session, dispatchTrigger, companyId, userId, orderByComparator,
				true);

			array[1] = dispatchTrigger;

			array[2] = filterGetByC_U_PrevAndNext(
				session, dispatchTrigger, companyId, userId, orderByComparator,
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

	protected DispatchTrigger filterGetByC_U_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, long companyId,
		long userId, OrderByComparator<DispatchTrigger> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_U_USERID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the dispatch triggers where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByC_U(long companyId, long userId) {
		for (DispatchTrigger dispatchTrigger :
				findByC_U(
					companyId, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(dispatchTrigger);
		}
	}

	/**
	 * Returns the number of dispatch triggers where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByC_U(long companyId, long userId) {
		FinderPath finderPath = _finderPathCountByC_U;

		Object[] finderArgs = new Object[] {companyId, userId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_DISPATCHTRIGGER_WHERE);

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
	 * Returns the number of dispatch triggers that the user has permission to view where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByC_U(long companyId, long userId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_U(companyId, userId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DispatchTrigger> dispatchTriggers = findByC_U(
				companyId, userId);

			dispatchTriggers = InlineSQLHelperUtil.filter(dispatchTriggers);

			return dispatchTriggers.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_DISPATCHTRIGGER_WHERE);

		sb.append(_FINDER_COLUMN_C_U_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_U_USERID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
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
		"dispatchTrigger.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_U_USERID_2 =
		"dispatchTrigger.userId = ?";

	private FinderPath _finderPathWithPaginationFindByC_DTET;
	private FinderPath _finderPathWithoutPaginationFindByC_DTET;
	private FinderPath _finderPathCountByC_DTET;

	/**
	 * Returns all the dispatch triggers where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @return the matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByC_DTET(
		long companyId, String dispatchTaskExecutorType) {

		return findByC_DTET(
			companyId, dispatchTaskExecutorType, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByC_DTET(
		long companyId, String dispatchTaskExecutorType, int start, int end) {

		return findByC_DTET(
			companyId, dispatchTaskExecutorType, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByC_DTET(
		long companyId, String dispatchTaskExecutorType, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return findByC_DTET(
			companyId, dispatchTaskExecutorType, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByC_DTET(
		long companyId, String dispatchTaskExecutorType, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

		dispatchTaskExecutorType = Objects.toString(
			dispatchTaskExecutorType, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByC_DTET;
				finderArgs = new Object[] {companyId, dispatchTaskExecutorType};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByC_DTET;
			finderArgs = new Object[] {
				companyId, dispatchTaskExecutorType, start, end,
				orderByComparator
			};
		}

		List<DispatchTrigger> list = null;

		if (useFinderCache) {
			list = (List<DispatchTrigger>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (DispatchTrigger dispatchTrigger : list) {
					if ((companyId != dispatchTrigger.getCompanyId()) ||
						!dispatchTaskExecutorType.equals(
							dispatchTrigger.getDispatchTaskExecutorType())) {

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

			sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

			sb.append(_FINDER_COLUMN_C_DTET_COMPANYID_2);

			boolean bindDispatchTaskExecutorType = false;

			if (dispatchTaskExecutorType.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_3);
			}
			else {
				bindDispatchTaskExecutorType = true;

				sb.append(_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindDispatchTaskExecutorType) {
					queryPos.add(dispatchTaskExecutorType);
				}

				list = (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the first dispatch trigger in the ordered set where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByC_DTET_First(
			long companyId, String dispatchTaskExecutorType,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByC_DTET_First(
			companyId, dispatchTaskExecutorType, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", dispatchTaskExecutorType=");
		sb.append(dispatchTaskExecutorType);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByC_DTET_First(
		long companyId, String dispatchTaskExecutorType,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		List<DispatchTrigger> list = findByC_DTET(
			companyId, dispatchTaskExecutorType, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByC_DTET_Last(
			long companyId, String dispatchTaskExecutorType,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByC_DTET_Last(
			companyId, dispatchTaskExecutorType, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", dispatchTaskExecutorType=");
		sb.append(dispatchTaskExecutorType);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByC_DTET_Last(
		long companyId, String dispatchTaskExecutorType,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		int count = countByC_DTET(companyId, dispatchTaskExecutorType);

		if (count == 0) {
			return null;
		}

		List<DispatchTrigger> list = findByC_DTET(
			companyId, dispatchTaskExecutorType, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] findByC_DTET_PrevAndNext(
			long dispatchTriggerId, long companyId,
			String dispatchTaskExecutorType,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		dispatchTaskExecutorType = Objects.toString(
			dispatchTaskExecutorType, "");

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = getByC_DTET_PrevAndNext(
				session, dispatchTrigger, companyId, dispatchTaskExecutorType,
				orderByComparator, true);

			array[1] = dispatchTrigger;

			array[2] = getByC_DTET_PrevAndNext(
				session, dispatchTrigger, companyId, dispatchTaskExecutorType,
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

	protected DispatchTrigger getByC_DTET_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, long companyId,
		String dispatchTaskExecutorType,
		OrderByComparator<DispatchTrigger> orderByComparator,
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

		sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

		sb.append(_FINDER_COLUMN_C_DTET_COMPANYID_2);

		boolean bindDispatchTaskExecutorType = false;

		if (dispatchTaskExecutorType.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_3);
		}
		else {
			bindDispatchTaskExecutorType = true;

			sb.append(_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_2);
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
			sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindDispatchTaskExecutorType) {
			queryPos.add(dispatchTaskExecutorType);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the dispatch triggers that the user has permission to view where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @return the matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByC_DTET(
		long companyId, String dispatchTaskExecutorType) {

		return filterFindByC_DTET(
			companyId, dispatchTaskExecutorType, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers that the user has permission to view where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByC_DTET(
		long companyId, String dispatchTaskExecutorType, int start, int end) {

		return filterFindByC_DTET(
			companyId, dispatchTaskExecutorType, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByC_DTET(
		long companyId, String dispatchTaskExecutorType, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_DTET(
				companyId, dispatchTaskExecutorType, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_DTET(
					companyId, dispatchTaskExecutorType, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		dispatchTaskExecutorType = Objects.toString(
			dispatchTaskExecutorType, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_DTET_COMPANYID_2);

		boolean bindDispatchTaskExecutorType = false;

		if (dispatchTaskExecutorType.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_3);
		}
		else {
			bindDispatchTaskExecutorType = true;

			sb.append(_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindDispatchTaskExecutorType) {
				queryPos.add(dispatchTaskExecutorType);
			}

			return (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set of dispatch triggers that the user has permission to view where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] filterFindByC_DTET_PrevAndNext(
			long dispatchTriggerId, long companyId,
			String dispatchTaskExecutorType,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_DTET_PrevAndNext(
				dispatchTriggerId, companyId, dispatchTaskExecutorType,
				orderByComparator);
		}

		dispatchTaskExecutorType = Objects.toString(
			dispatchTaskExecutorType, "");

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = filterGetByC_DTET_PrevAndNext(
				session, dispatchTrigger, companyId, dispatchTaskExecutorType,
				orderByComparator, true);

			array[1] = dispatchTrigger;

			array[2] = filterGetByC_DTET_PrevAndNext(
				session, dispatchTrigger, companyId, dispatchTaskExecutorType,
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

	protected DispatchTrigger filterGetByC_DTET_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, long companyId,
		String dispatchTaskExecutorType,
		OrderByComparator<DispatchTrigger> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_DTET_COMPANYID_2);

		boolean bindDispatchTaskExecutorType = false;

		if (dispatchTaskExecutorType.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_3);
		}
		else {
			bindDispatchTaskExecutorType = true;

			sb.append(_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (bindDispatchTaskExecutorType) {
			queryPos.add(dispatchTaskExecutorType);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the dispatch triggers where companyId = &#63; and dispatchTaskExecutorType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 */
	@Override
	public void removeByC_DTET(
		long companyId, String dispatchTaskExecutorType) {

		for (DispatchTrigger dispatchTrigger :
				findByC_DTET(
					companyId, dispatchTaskExecutorType, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dispatchTrigger);
		}
	}

	/**
	 * Returns the number of dispatch triggers where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByC_DTET(long companyId, String dispatchTaskExecutorType) {
		dispatchTaskExecutorType = Objects.toString(
			dispatchTaskExecutorType, "");

		FinderPath finderPath = _finderPathCountByC_DTET;

		Object[] finderArgs = new Object[] {
			companyId, dispatchTaskExecutorType
		};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_DISPATCHTRIGGER_WHERE);

			sb.append(_FINDER_COLUMN_C_DTET_COMPANYID_2);

			boolean bindDispatchTaskExecutorType = false;

			if (dispatchTaskExecutorType.isEmpty()) {
				sb.append(_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_3);
			}
			else {
				bindDispatchTaskExecutorType = true;

				sb.append(_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(companyId);

				if (bindDispatchTaskExecutorType) {
					queryPos.add(dispatchTaskExecutorType);
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
	 * Returns the number of dispatch triggers that the user has permission to view where companyId = &#63; and dispatchTaskExecutorType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param dispatchTaskExecutorType the dispatch task executor type
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByC_DTET(
		long companyId, String dispatchTaskExecutorType) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_DTET(companyId, dispatchTaskExecutorType);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DispatchTrigger> dispatchTriggers = findByC_DTET(
				companyId, dispatchTaskExecutorType);

			dispatchTriggers = InlineSQLHelperUtil.filter(dispatchTriggers);

			return dispatchTriggers.size();
		}

		dispatchTaskExecutorType = Objects.toString(
			dispatchTaskExecutorType, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_DISPATCHTRIGGER_WHERE);

		sb.append(_FINDER_COLUMN_C_DTET_COMPANYID_2);

		boolean bindDispatchTaskExecutorType = false;

		if (dispatchTaskExecutorType.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_3);
		}
		else {
			bindDispatchTaskExecutorType = true;

			sb.append(_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindDispatchTaskExecutorType) {
				queryPos.add(dispatchTaskExecutorType);
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

	private static final String _FINDER_COLUMN_C_DTET_COMPANYID_2 =
		"dispatchTrigger.companyId = ? AND ";

	private static final String
		_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_2 =
			"dispatchTrigger.dispatchTaskExecutorType = ?";

	private static final String
		_FINDER_COLUMN_C_DTET_DISPATCHTASKEXECUTORTYPE_3 =
			"(dispatchTrigger.dispatchTaskExecutorType IS NULL OR dispatchTrigger.dispatchTaskExecutorType = '')";

	private FinderPath _finderPathFetchByC_N;

	/**
	 * Returns the dispatch trigger where companyId = &#63; and name = &#63; or throws a <code>NoSuchTriggerException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByC_N(long companyId, String name)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByC_N(companyId, name);

		if (dispatchTrigger == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("companyId=");
			sb.append(companyId);

			sb.append(", name=");
			sb.append(name);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchTriggerException(sb.toString());
		}

		return dispatchTrigger;
	}

	/**
	 * Returns the dispatch trigger where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByC_N(long companyId, String name) {
		return fetchByC_N(companyId, name, true);
	}

	/**
	 * Returns the dispatch trigger where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		name = Objects.toString(name, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {companyId, name};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByC_N, finderArgs, this);
		}

		if (result instanceof DispatchTrigger) {
			DispatchTrigger dispatchTrigger = (DispatchTrigger)result;

			if ((companyId != dispatchTrigger.getCompanyId()) ||
				!Objects.equals(name, dispatchTrigger.getName())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

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

				List<DispatchTrigger> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByC_N, finderArgs, list);
					}
				}
				else {
					DispatchTrigger dispatchTrigger = list.get(0);

					result = dispatchTrigger;

					cacheResult(dispatchTrigger);
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
			return (DispatchTrigger)result;
		}
	}

	/**
	 * Removes the dispatch trigger where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the dispatch trigger that was removed
	 */
	@Override
	public DispatchTrigger removeByC_N(long companyId, String name)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = findByC_N(companyId, name);

		return remove(dispatchTrigger);
	}

	/**
	 * Returns the number of dispatch triggers where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		DispatchTrigger dispatchTrigger = fetchByC_N(companyId, name);

		if (dispatchTrigger == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_N_COMPANYID_2 =
		"dispatchTrigger.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_NAME_2 =
		"dispatchTrigger.name = ?";

	private static final String _FINDER_COLUMN_C_N_NAME_3 =
		"(dispatchTrigger.name IS NULL OR dispatchTrigger.name = '')";

	private FinderPath _finderPathWithPaginationFindByA_DTCM;
	private FinderPath _finderPathWithoutPaginationFindByA_DTCM;
	private FinderPath _finderPathCountByA_DTCM;
	private FinderPath _finderPathWithPaginationCountByA_DTCM;

	/**
	 * Returns all the dispatch triggers where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @return the matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByA_DTCM(
		boolean active, int dispatchTaskClusterMode) {

		return findByA_DTCM(
			active, dispatchTaskClusterMode, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByA_DTCM(
		boolean active, int dispatchTaskClusterMode, int start, int end) {

		return findByA_DTCM(active, dispatchTaskClusterMode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByA_DTCM(
		boolean active, int dispatchTaskClusterMode, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return findByA_DTCM(
			active, dispatchTaskClusterMode, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByA_DTCM(
		boolean active, int dispatchTaskClusterMode, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByA_DTCM;
				finderArgs = new Object[] {active, dispatchTaskClusterMode};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByA_DTCM;
			finderArgs = new Object[] {
				active, dispatchTaskClusterMode, start, end, orderByComparator
			};
		}

		List<DispatchTrigger> list = null;

		if (useFinderCache) {
			list = (List<DispatchTrigger>)finderCache.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (DispatchTrigger dispatchTrigger : list) {
					if ((active != dispatchTrigger.isActive()) ||
						(dispatchTaskClusterMode !=
							dispatchTrigger.getDispatchTaskClusterMode())) {

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

			sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

			sb.append(_FINDER_COLUMN_A_DTCM_ACTIVE_2);

			sb.append(_FINDER_COLUMN_A_DTCM_DISPATCHTASKCLUSTERMODE_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(active);

				queryPos.add(dispatchTaskClusterMode);

				list = (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the first dispatch trigger in the ordered set where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByA_DTCM_First(
			boolean active, int dispatchTaskClusterMode,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByA_DTCM_First(
			active, dispatchTaskClusterMode, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("active=");
		sb.append(active);

		sb.append(", dispatchTaskClusterMode=");
		sb.append(dispatchTaskClusterMode);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the first dispatch trigger in the ordered set where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByA_DTCM_First(
		boolean active, int dispatchTaskClusterMode,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		List<DispatchTrigger> list = findByA_DTCM(
			active, dispatchTaskClusterMode, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByA_DTCM_Last(
			boolean active, int dispatchTaskClusterMode,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByA_DTCM_Last(
			active, dispatchTaskClusterMode, orderByComparator);

		if (dispatchTrigger != null) {
			return dispatchTrigger;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("active=");
		sb.append(active);

		sb.append(", dispatchTaskClusterMode=");
		sb.append(dispatchTaskClusterMode);

		sb.append("}");

		throw new NoSuchTriggerException(sb.toString());
	}

	/**
	 * Returns the last dispatch trigger in the ordered set where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByA_DTCM_Last(
		boolean active, int dispatchTaskClusterMode,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		int count = countByA_DTCM(active, dispatchTaskClusterMode);

		if (count == 0) {
			return null;
		}

		List<DispatchTrigger> list = findByA_DTCM(
			active, dispatchTaskClusterMode, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] findByA_DTCM_PrevAndNext(
			long dispatchTriggerId, boolean active, int dispatchTaskClusterMode,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = getByA_DTCM_PrevAndNext(
				session, dispatchTrigger, active, dispatchTaskClusterMode,
				orderByComparator, true);

			array[1] = dispatchTrigger;

			array[2] = getByA_DTCM_PrevAndNext(
				session, dispatchTrigger, active, dispatchTaskClusterMode,
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

	protected DispatchTrigger getByA_DTCM_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, boolean active,
		int dispatchTaskClusterMode,
		OrderByComparator<DispatchTrigger> orderByComparator,
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

		sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

		sb.append(_FINDER_COLUMN_A_DTCM_ACTIVE_2);

		sb.append(_FINDER_COLUMN_A_DTCM_DISPATCHTASKCLUSTERMODE_2);

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
			sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(active);

		queryPos.add(dispatchTaskClusterMode);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the dispatch triggers that the user has permission to view where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @return the matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByA_DTCM(
		boolean active, int dispatchTaskClusterMode) {

		return filterFindByA_DTCM(
			active, dispatchTaskClusterMode, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers that the user has permission to view where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByA_DTCM(
		boolean active, int dispatchTaskClusterMode, int start, int end) {

		return filterFindByA_DTCM(
			active, dispatchTaskClusterMode, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permissions to view where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByA_DTCM(
		boolean active, int dispatchTaskClusterMode, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByA_DTCM(
				active, dispatchTaskClusterMode, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByA_DTCM(
					active, dispatchTaskClusterMode, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_A_DTCM_ACTIVE_2_SQL);

		sb.append(_FINDER_COLUMN_A_DTCM_DISPATCHTASKCLUSTERMODE_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(active);

			queryPos.add(dispatchTaskClusterMode);

			return (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns the dispatch triggers before and after the current dispatch trigger in the ordered set of dispatch triggers that the user has permission to view where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param dispatchTriggerId the primary key of the current dispatch trigger
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger[] filterFindByA_DTCM_PrevAndNext(
			long dispatchTriggerId, boolean active, int dispatchTaskClusterMode,
			OrderByComparator<DispatchTrigger> orderByComparator)
		throws NoSuchTriggerException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByA_DTCM_PrevAndNext(
				dispatchTriggerId, active, dispatchTaskClusterMode,
				orderByComparator);
		}

		DispatchTrigger dispatchTrigger = findByPrimaryKey(dispatchTriggerId);

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger[] array = new DispatchTriggerImpl[3];

			array[0] = filterGetByA_DTCM_PrevAndNext(
				session, dispatchTrigger, active, dispatchTaskClusterMode,
				orderByComparator, true);

			array[1] = dispatchTrigger;

			array[2] = filterGetByA_DTCM_PrevAndNext(
				session, dispatchTrigger, active, dispatchTaskClusterMode,
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

	protected DispatchTrigger filterGetByA_DTCM_PrevAndNext(
		Session session, DispatchTrigger dispatchTrigger, boolean active,
		int dispatchTaskClusterMode,
		OrderByComparator<DispatchTrigger> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_A_DTCM_ACTIVE_2_SQL);

		sb.append(_FINDER_COLUMN_A_DTCM_DISPATCHTASKCLUSTERMODE_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(active);

		queryPos.add(dispatchTaskClusterMode);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						dispatchTrigger)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DispatchTrigger> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the dispatch triggers that the user has permission to view where active = &#63; and dispatchTaskClusterMode = any &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @return the matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByA_DTCM(
		boolean active, int[] dispatchTaskClusterModes) {

		return filterFindByA_DTCM(
			active, dispatchTaskClusterModes, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers that the user has permission to view where active = &#63; and dispatchTaskClusterMode = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByA_DTCM(
		boolean active, int[] dispatchTaskClusterModes, int start, int end) {

		return filterFindByA_DTCM(
			active, dispatchTaskClusterModes, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers that the user has permission to view where active = &#63; and dispatchTaskClusterMode = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public List<DispatchTrigger> filterFindByA_DTCM(
		boolean active, int[] dispatchTaskClusterModes, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByA_DTCM(
				active, dispatchTaskClusterModes, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByA_DTCM(
					active, dispatchTaskClusterModes, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator));
		}

		if (dispatchTaskClusterModes == null) {
			dispatchTaskClusterModes = new int[0];
		}
		else if (dispatchTaskClusterModes.length > 1) {
			dispatchTaskClusterModes = ArrayUtil.sortedUnique(
				dispatchTaskClusterModes);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_A_DTCM_ACTIVE_2_SQL);

		if (dispatchTaskClusterModes.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_A_DTCM_DISPATCHTASKCLUSTERMODE_7);

			sb.append(StringUtil.merge(dispatchTaskClusterModes));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2);
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
					DispatchTriggerModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DispatchTriggerImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DispatchTriggerImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(active);

			return (List<DispatchTrigger>)QueryUtil.list(
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
	 * Returns all the dispatch triggers where active = &#63; and dispatchTaskClusterMode = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @return the matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByA_DTCM(
		boolean active, int[] dispatchTaskClusterModes) {

		return findByA_DTCM(
			active, dispatchTaskClusterModes, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers where active = &#63; and dispatchTaskClusterMode = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByA_DTCM(
		boolean active, int[] dispatchTaskClusterModes, int start, int end) {

		return findByA_DTCM(active, dispatchTaskClusterModes, start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where active = &#63; and dispatchTaskClusterMode = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByA_DTCM(
		boolean active, int[] dispatchTaskClusterModes, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return findByA_DTCM(
			active, dispatchTaskClusterModes, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers where active = &#63; and dispatchTaskClusterMode = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findByA_DTCM(
		boolean active, int[] dispatchTaskClusterModes, int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
		boolean useFinderCache) {

		if (dispatchTaskClusterModes == null) {
			dispatchTaskClusterModes = new int[0];
		}
		else if (dispatchTaskClusterModes.length > 1) {
			dispatchTaskClusterModes = ArrayUtil.sortedUnique(
				dispatchTaskClusterModes);
		}

		if (dispatchTaskClusterModes.length == 1) {
			return findByA_DTCM(
				active, dispatchTaskClusterModes[0], start, end,
				orderByComparator);
		}

		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderArgs = new Object[] {
					active, StringUtil.merge(dispatchTaskClusterModes)
				};
			}
		}
		else if (useFinderCache) {
			finderArgs = new Object[] {
				active, StringUtil.merge(dispatchTaskClusterModes), start, end,
				orderByComparator
			};
		}

		List<DispatchTrigger> list = null;

		if (useFinderCache) {
			list = (List<DispatchTrigger>)finderCache.getResult(
				_finderPathWithPaginationFindByA_DTCM, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (DispatchTrigger dispatchTrigger : list) {
					if ((active != dispatchTrigger.isActive()) ||
						!ArrayUtil.contains(
							dispatchTaskClusterModes,
							dispatchTrigger.getDispatchTaskClusterMode())) {

						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

			sb.append(_FINDER_COLUMN_A_DTCM_ACTIVE_2);

			if (dispatchTaskClusterModes.length > 0) {
				sb.append("(");

				sb.append(_FINDER_COLUMN_A_DTCM_DISPATCHTASKCLUSTERMODE_7);

				sb.append(StringUtil.merge(dispatchTaskClusterModes));

				sb.append(")");

				sb.append(")");
			}

			sb.setStringAt(
				removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(DispatchTriggerModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(active);

				list = (List<DispatchTrigger>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache) {
					finderCache.putResult(
						_finderPathWithPaginationFindByA_DTCM, finderArgs,
						list);
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
	 * Removes all the dispatch triggers where active = &#63; and dispatchTaskClusterMode = &#63; from the database.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 */
	@Override
	public void removeByA_DTCM(boolean active, int dispatchTaskClusterMode) {
		for (DispatchTrigger dispatchTrigger :
				findByA_DTCM(
					active, dispatchTaskClusterMode, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(dispatchTrigger);
		}
	}

	/**
	 * Returns the number of dispatch triggers where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByA_DTCM(boolean active, int dispatchTaskClusterMode) {
		FinderPath finderPath = _finderPathCountByA_DTCM;

		Object[] finderArgs = new Object[] {active, dispatchTaskClusterMode};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_DISPATCHTRIGGER_WHERE);

			sb.append(_FINDER_COLUMN_A_DTCM_ACTIVE_2);

			sb.append(_FINDER_COLUMN_A_DTCM_DISPATCHTASKCLUSTERMODE_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(active);

				queryPos.add(dispatchTaskClusterMode);

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
	 * Returns the number of dispatch triggers where active = &#63; and dispatchTaskClusterMode = any &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByA_DTCM(boolean active, int[] dispatchTaskClusterModes) {
		if (dispatchTaskClusterModes == null) {
			dispatchTaskClusterModes = new int[0];
		}
		else if (dispatchTaskClusterModes.length > 1) {
			dispatchTaskClusterModes = ArrayUtil.sortedUnique(
				dispatchTaskClusterModes);
		}

		Object[] finderArgs = new Object[] {
			active, StringUtil.merge(dispatchTaskClusterModes)
		};

		Long count = (Long)finderCache.getResult(
			_finderPathWithPaginationCountByA_DTCM, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler();

			sb.append(_SQL_COUNT_DISPATCHTRIGGER_WHERE);

			sb.append(_FINDER_COLUMN_A_DTCM_ACTIVE_2);

			if (dispatchTaskClusterModes.length > 0) {
				sb.append("(");

				sb.append(_FINDER_COLUMN_A_DTCM_DISPATCHTASKCLUSTERMODE_7);

				sb.append(StringUtil.merge(dispatchTaskClusterModes));

				sb.append(")");

				sb.append(")");
			}

			sb.setStringAt(
				removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(active);

				count = (Long)query.uniqueResult();

				finderCache.putResult(
					_finderPathWithPaginationCountByA_DTCM, finderArgs, count);
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
	 * Returns the number of dispatch triggers that the user has permission to view where active = &#63; and dispatchTaskClusterMode = &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterMode the dispatch task cluster mode
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByA_DTCM(
		boolean active, int dispatchTaskClusterMode) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByA_DTCM(active, dispatchTaskClusterMode);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DispatchTrigger> dispatchTriggers = findByA_DTCM(
				active, dispatchTaskClusterMode);

			dispatchTriggers = InlineSQLHelperUtil.filter(dispatchTriggers);

			return dispatchTriggers.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_DISPATCHTRIGGER_WHERE);

		sb.append(_FINDER_COLUMN_A_DTCM_ACTIVE_2_SQL);

		sb.append(_FINDER_COLUMN_A_DTCM_DISPATCHTASKCLUSTERMODE_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(active);

			queryPos.add(dispatchTaskClusterMode);

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

	/**
	 * Returns the number of dispatch triggers that the user has permission to view where active = &#63; and dispatchTaskClusterMode = any &#63;.
	 *
	 * @param active the active
	 * @param dispatchTaskClusterModes the dispatch task cluster modes
	 * @return the number of matching dispatch triggers that the user has permission to view
	 */
	@Override
	public int filterCountByA_DTCM(
		boolean active, int[] dispatchTaskClusterModes) {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByA_DTCM(active, dispatchTaskClusterModes);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DispatchTrigger> dispatchTriggers = InlineSQLHelperUtil.filter(
				findByA_DTCM(active, dispatchTaskClusterModes));

			return dispatchTriggers.size();
		}

		if (dispatchTaskClusterModes == null) {
			dispatchTaskClusterModes = new int[0];
		}
		else if (dispatchTaskClusterModes.length > 1) {
			dispatchTaskClusterModes = ArrayUtil.sortedUnique(
				dispatchTaskClusterModes);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_DISPATCHTRIGGER_WHERE);

		sb.append(_FINDER_COLUMN_A_DTCM_ACTIVE_2_SQL);

		if (dispatchTaskClusterModes.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_A_DTCM_DISPATCHTASKCLUSTERMODE_7);

			sb.append(StringUtil.merge(dispatchTaskClusterModes));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DispatchTrigger.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(active);

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

	private static final String _FINDER_COLUMN_A_DTCM_ACTIVE_2 =
		"dispatchTrigger.active = ? AND ";

	private static final String _FINDER_COLUMN_A_DTCM_ACTIVE_2_SQL =
		"dispatchTrigger.active_ = ? AND ";

	private static final String
		_FINDER_COLUMN_A_DTCM_DISPATCHTASKCLUSTERMODE_2 =
			"dispatchTrigger.dispatchTaskClusterMode = ?";

	private static final String
		_FINDER_COLUMN_A_DTCM_DISPATCHTASKCLUSTERMODE_7 =
			"dispatchTrigger.dispatchTaskClusterMode IN (";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the dispatch trigger where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchTriggerException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching dispatch trigger
	 * @throws NoSuchTriggerException if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByERC_C(
			externalReferenceCode, companyId);

		if (dispatchTrigger == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", companyId=");
			sb.append(companyId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchTriggerException(sb.toString());
		}

		return dispatchTrigger;
	}

	/**
	 * Returns the dispatch trigger where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the dispatch trigger where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public DispatchTrigger fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		externalReferenceCode = Objects.toString(externalReferenceCode, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {externalReferenceCode, companyId};
		}

		Object result = null;

		if (useFinderCache) {
			result = finderCache.getResult(
				_finderPathFetchByERC_C, finderArgs, this);
		}

		if (result instanceof DispatchTrigger) {
			DispatchTrigger dispatchTrigger = (DispatchTrigger)result;

			if (!Objects.equals(
					externalReferenceCode,
					dispatchTrigger.getExternalReferenceCode()) ||
				(companyId != dispatchTrigger.getCompanyId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_SELECT_DISPATCHTRIGGER_WHERE);

			boolean bindExternalReferenceCode = false;

			if (externalReferenceCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3);
			}
			else {
				bindExternalReferenceCode = true;

				sb.append(_FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2);
			}

			sb.append(_FINDER_COLUMN_ERC_C_COMPANYID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindExternalReferenceCode) {
					queryPos.add(externalReferenceCode);
				}

				queryPos.add(companyId);

				List<DispatchTrigger> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						finderCache.putResult(
							_finderPathFetchByERC_C, finderArgs, list);
					}
				}
				else {
					DispatchTrigger dispatchTrigger = list.get(0);

					result = dispatchTrigger;

					cacheResult(dispatchTrigger);
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
			return (DispatchTrigger)result;
		}
	}

	/**
	 * Removes the dispatch trigger where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the dispatch trigger that was removed
	 */
	@Override
	public DispatchTrigger removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = findByERC_C(
			externalReferenceCode, companyId);

		return remove(dispatchTrigger);
	}

	/**
	 * Returns the number of dispatch triggers where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching dispatch triggers
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		DispatchTrigger dispatchTrigger = fetchByERC_C(
			externalReferenceCode, companyId);

		if (dispatchTrigger == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"dispatchTrigger.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(dispatchTrigger.externalReferenceCode IS NULL OR dispatchTrigger.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"dispatchTrigger.companyId = ?";

	public DispatchTriggerPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DispatchTrigger.class);

		setModelImplClass(DispatchTriggerImpl.class);
		setModelPKClass(long.class);

		setTable(DispatchTriggerTable.INSTANCE);
	}

	/**
	 * Caches the dispatch trigger in the entity cache if it is enabled.
	 *
	 * @param dispatchTrigger the dispatch trigger
	 */
	@Override
	public void cacheResult(DispatchTrigger dispatchTrigger) {
		entityCache.putResult(
			DispatchTriggerImpl.class, dispatchTrigger.getPrimaryKey(),
			dispatchTrigger);

		finderCache.putResult(
			_finderPathFetchByC_N,
			new Object[] {
				dispatchTrigger.getCompanyId(), dispatchTrigger.getName()
			},
			dispatchTrigger);

		finderCache.putResult(
			_finderPathFetchByERC_C,
			new Object[] {
				dispatchTrigger.getExternalReferenceCode(),
				dispatchTrigger.getCompanyId()
			},
			dispatchTrigger);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the dispatch triggers in the entity cache if it is enabled.
	 *
	 * @param dispatchTriggers the dispatch triggers
	 */
	@Override
	public void cacheResult(List<DispatchTrigger> dispatchTriggers) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (dispatchTriggers.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (DispatchTrigger dispatchTrigger : dispatchTriggers) {
			if (entityCache.getResult(
					DispatchTriggerImpl.class,
					dispatchTrigger.getPrimaryKey()) == null) {

				cacheResult(dispatchTrigger);
			}
		}
	}

	/**
	 * Clears the cache for all dispatch triggers.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(DispatchTriggerImpl.class);

		finderCache.clearCache(DispatchTriggerImpl.class);
	}

	/**
	 * Clears the cache for the dispatch trigger.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DispatchTrigger dispatchTrigger) {
		entityCache.removeResult(DispatchTriggerImpl.class, dispatchTrigger);
	}

	@Override
	public void clearCache(List<DispatchTrigger> dispatchTriggers) {
		for (DispatchTrigger dispatchTrigger : dispatchTriggers) {
			entityCache.removeResult(
				DispatchTriggerImpl.class, dispatchTrigger);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(DispatchTriggerImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(DispatchTriggerImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		DispatchTriggerModelImpl dispatchTriggerModelImpl) {

		Object[] args = new Object[] {
			dispatchTriggerModelImpl.getCompanyId(),
			dispatchTriggerModelImpl.getName()
		};

		finderCache.putResult(
			_finderPathFetchByC_N, args, dispatchTriggerModelImpl);

		args = new Object[] {
			dispatchTriggerModelImpl.getExternalReferenceCode(),
			dispatchTriggerModelImpl.getCompanyId()
		};

		finderCache.putResult(
			_finderPathFetchByERC_C, args, dispatchTriggerModelImpl);
	}

	/**
	 * Creates a new dispatch trigger with the primary key. Does not add the dispatch trigger to the database.
	 *
	 * @param dispatchTriggerId the primary key for the new dispatch trigger
	 * @return the new dispatch trigger
	 */
	@Override
	public DispatchTrigger create(long dispatchTriggerId) {
		DispatchTrigger dispatchTrigger = new DispatchTriggerImpl();

		dispatchTrigger.setNew(true);
		dispatchTrigger.setPrimaryKey(dispatchTriggerId);

		String uuid = PortalUUIDUtil.generate();

		dispatchTrigger.setUuid(uuid);

		dispatchTrigger.setCompanyId(CompanyThreadLocal.getCompanyId());

		return dispatchTrigger;
	}

	/**
	 * Removes the dispatch trigger with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dispatchTriggerId the primary key of the dispatch trigger
	 * @return the dispatch trigger that was removed
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger remove(long dispatchTriggerId)
		throws NoSuchTriggerException {

		return remove((Serializable)dispatchTriggerId);
	}

	/**
	 * Removes the dispatch trigger with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the dispatch trigger
	 * @return the dispatch trigger that was removed
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger remove(Serializable primaryKey)
		throws NoSuchTriggerException {

		Session session = null;

		try {
			session = openSession();

			DispatchTrigger dispatchTrigger = (DispatchTrigger)session.get(
				DispatchTriggerImpl.class, primaryKey);

			if (dispatchTrigger == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchTriggerException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(dispatchTrigger);
		}
		catch (NoSuchTriggerException noSuchEntityException) {
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
	protected DispatchTrigger removeImpl(DispatchTrigger dispatchTrigger) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(dispatchTrigger)) {
				dispatchTrigger = (DispatchTrigger)session.get(
					DispatchTriggerImpl.class,
					dispatchTrigger.getPrimaryKeyObj());
			}

			if (dispatchTrigger != null) {
				session.delete(dispatchTrigger);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (dispatchTrigger != null) {
			clearCache(dispatchTrigger);
		}

		return dispatchTrigger;
	}

	@Override
	public DispatchTrigger updateImpl(DispatchTrigger dispatchTrigger) {
		boolean isNew = dispatchTrigger.isNew();

		if (!(dispatchTrigger instanceof DispatchTriggerModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(dispatchTrigger.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					dispatchTrigger);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in dispatchTrigger proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DispatchTrigger implementation " +
					dispatchTrigger.getClass());
		}

		DispatchTriggerModelImpl dispatchTriggerModelImpl =
			(DispatchTriggerModelImpl)dispatchTrigger;

		if (Validator.isNull(dispatchTrigger.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			dispatchTrigger.setUuid(uuid);
		}

		if (Validator.isNull(dispatchTrigger.getExternalReferenceCode())) {
			dispatchTrigger.setExternalReferenceCode(dispatchTrigger.getUuid());
		}
		else {
			if (!Objects.equals(
					dispatchTriggerModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					dispatchTrigger.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = dispatchTrigger.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = dispatchTrigger.getPrimaryKey();
					}

					try {
						dispatchTrigger.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								DispatchTrigger.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								dispatchTrigger.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			DispatchTrigger ercDispatchTrigger = fetchByERC_C(
				dispatchTrigger.getExternalReferenceCode(),
				dispatchTrigger.getCompanyId());

			if (isNew) {
				if (ercDispatchTrigger != null) {
					throw new DuplicateDispatchTriggerExternalReferenceCodeException(
						"Duplicate dispatch trigger with external reference code " +
							dispatchTrigger.getExternalReferenceCode() +
								" and company " +
									dispatchTrigger.getCompanyId());
				}
			}
			else {
				if ((ercDispatchTrigger != null) &&
					(dispatchTrigger.getDispatchTriggerId() !=
						ercDispatchTrigger.getDispatchTriggerId())) {

					throw new DuplicateDispatchTriggerExternalReferenceCodeException(
						"Duplicate dispatch trigger with external reference code " +
							dispatchTrigger.getExternalReferenceCode() +
								" and company " +
									dispatchTrigger.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (dispatchTrigger.getCreateDate() == null)) {
			if (serviceContext == null) {
				dispatchTrigger.setCreateDate(date);
			}
			else {
				dispatchTrigger.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!dispatchTriggerModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				dispatchTrigger.setModifiedDate(date);
			}
			else {
				dispatchTrigger.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(dispatchTrigger);
			}
			else {
				dispatchTrigger = (DispatchTrigger)session.merge(
					dispatchTrigger);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			DispatchTriggerImpl.class, dispatchTriggerModelImpl, false, true);

		cacheUniqueFindersCache(dispatchTriggerModelImpl);

		if (isNew) {
			dispatchTrigger.setNew(false);
		}

		dispatchTrigger.resetOriginalValues();

		return dispatchTrigger;
	}

	/**
	 * Returns the dispatch trigger with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the dispatch trigger
	 * @return the dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger findByPrimaryKey(Serializable primaryKey)
		throws NoSuchTriggerException {

		DispatchTrigger dispatchTrigger = fetchByPrimaryKey(primaryKey);

		if (dispatchTrigger == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchTriggerException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return dispatchTrigger;
	}

	/**
	 * Returns the dispatch trigger with the primary key or throws a <code>NoSuchTriggerException</code> if it could not be found.
	 *
	 * @param dispatchTriggerId the primary key of the dispatch trigger
	 * @return the dispatch trigger
	 * @throws NoSuchTriggerException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger findByPrimaryKey(long dispatchTriggerId)
		throws NoSuchTriggerException {

		return findByPrimaryKey((Serializable)dispatchTriggerId);
	}

	/**
	 * Returns the dispatch trigger with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dispatchTriggerId the primary key of the dispatch trigger
	 * @return the dispatch trigger, or <code>null</code> if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public DispatchTrigger fetchByPrimaryKey(long dispatchTriggerId) {
		return fetchByPrimaryKey((Serializable)dispatchTriggerId);
	}

	/**
	 * Returns all the dispatch triggers.
	 *
	 * @return the dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the dispatch triggers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findAll(
		int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the dispatch triggers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of dispatch triggers
	 */
	@Override
	public List<DispatchTrigger> findAll(
		int start, int end,
		OrderByComparator<DispatchTrigger> orderByComparator,
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

		List<DispatchTrigger> list = null;

		if (useFinderCache) {
			list = (List<DispatchTrigger>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_DISPATCHTRIGGER);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_DISPATCHTRIGGER;

				sql = sql.concat(DispatchTriggerModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<DispatchTrigger>)QueryUtil.list(
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
	 * Removes all the dispatch triggers from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DispatchTrigger dispatchTrigger : findAll()) {
			remove(dispatchTrigger);
		}
	}

	/**
	 * Returns the number of dispatch triggers.
	 *
	 * @return the number of dispatch triggers
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_DISPATCHTRIGGER);

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
		return "dispatchTriggerId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DISPATCHTRIGGER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DispatchTriggerModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the dispatch trigger persistence.
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

		_finderPathWithPaginationFindByActive = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByActive",
			new String[] {
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"active_"}, true);

		_finderPathWithoutPaginationFindByActive = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByActive",
			new String[] {Boolean.class.getName()}, new String[] {"active_"},
			true);

		_finderPathCountByActive = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByActive",
			new String[] {Boolean.class.getName()}, new String[] {"active_"},
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

		_finderPathWithPaginationFindByC_DTET = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_DTET",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "dispatchTaskExecutorType"}, true);

		_finderPathWithoutPaginationFindByC_DTET = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_DTET",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "dispatchTaskExecutorType"}, true);

		_finderPathCountByC_DTET = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_DTET",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "dispatchTaskExecutorType"}, false);

		_finderPathFetchByC_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "name"}, true);

		_finderPathWithPaginationFindByA_DTCM = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByA_DTCM",
			new String[] {
				Boolean.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"active_", "dispatchTaskClusterMode"}, true);

		_finderPathWithoutPaginationFindByA_DTCM = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByA_DTCM",
			new String[] {Boolean.class.getName(), Integer.class.getName()},
			new String[] {"active_", "dispatchTaskClusterMode"}, true);

		_finderPathCountByA_DTCM = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByA_DTCM",
			new String[] {Boolean.class.getName(), Integer.class.getName()},
			new String[] {"active_", "dispatchTaskClusterMode"}, false);

		_finderPathWithPaginationCountByA_DTCM = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByA_DTCM",
			new String[] {Boolean.class.getName(), Integer.class.getName()},
			new String[] {"active_", "dispatchTaskClusterMode"}, false);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		DispatchTriggerUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DispatchTriggerUtil.setPersistence(null);

		entityCache.removeCache(DispatchTriggerImpl.class.getName());
	}

	@Override
	@Reference(
		target = DispatchPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DispatchPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DispatchPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_DISPATCHTRIGGER =
		"SELECT dispatchTrigger FROM DispatchTrigger dispatchTrigger";

	private static final String _SQL_SELECT_DISPATCHTRIGGER_WHERE =
		"SELECT dispatchTrigger FROM DispatchTrigger dispatchTrigger WHERE ";

	private static final String _SQL_COUNT_DISPATCHTRIGGER =
		"SELECT COUNT(dispatchTrigger) FROM DispatchTrigger dispatchTrigger";

	private static final String _SQL_COUNT_DISPATCHTRIGGER_WHERE =
		"SELECT COUNT(dispatchTrigger) FROM DispatchTrigger dispatchTrigger WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"dispatchTrigger.dispatchTriggerId";

	private static final String _FILTER_SQL_SELECT_DISPATCHTRIGGER_WHERE =
		"SELECT DISTINCT {dispatchTrigger.*} FROM DispatchTrigger dispatchTrigger WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {DispatchTrigger.*} FROM (SELECT DISTINCT dispatchTrigger.dispatchTriggerId FROM DispatchTrigger dispatchTrigger WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DISPATCHTRIGGER_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN DispatchTrigger ON TEMP_TABLE.dispatchTriggerId = DispatchTrigger.dispatchTriggerId";

	private static final String _FILTER_SQL_COUNT_DISPATCHTRIGGER_WHERE =
		"SELECT COUNT(DISTINCT dispatchTrigger.dispatchTriggerId) AS COUNT_VALUE FROM DispatchTrigger dispatchTrigger WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "dispatchTrigger";

	private static final String _FILTER_ENTITY_TABLE = "DispatchTrigger";

	private static final String _ORDER_BY_ENTITY_ALIAS = "dispatchTrigger.";

	private static final String _ORDER_BY_ENTITY_TABLE = "DispatchTrigger.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No DispatchTrigger exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DispatchTrigger exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DispatchTriggerPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active", "system"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}