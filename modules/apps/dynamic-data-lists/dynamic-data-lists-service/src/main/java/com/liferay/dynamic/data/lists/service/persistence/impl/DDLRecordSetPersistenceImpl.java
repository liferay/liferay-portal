/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.persistence.impl;

import com.liferay.dynamic.data.lists.exception.NoSuchRecordSetException;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetTable;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordSetImpl;
import com.liferay.dynamic.data.lists.model.impl.DDLRecordSetModelImpl;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordSetPersistence;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordSetUtil;
import com.liferay.dynamic.data.lists.service.persistence.impl.constants.DDLPersistenceConstants;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
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

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the ddl record set service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDLRecordSetPersistence.class)
public class DDLRecordSetPersistenceImpl
	extends BasePersistenceImpl<DDLRecordSet>
	implements DDLRecordSetPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDLRecordSetUtil</code> to access the ddl record set persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDLRecordSetImpl.class.getName();

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
	 * Returns all the ddl record sets where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddl record sets where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @return the range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddl record sets where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddl record sets where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

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

			List<DDLRecordSet> list = null;

			if (useFinderCache) {
				list = (List<DDLRecordSet>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDLRecordSet ddlRecordSet : list) {
						if (!uuid.equals(ddlRecordSet.getUuid())) {
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

				sb.append(_SQL_SELECT_DDLRECORDSET_WHERE);

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
					sb.append(DDLRecordSetModelImpl.ORDER_BY_JPQL);
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

					list = (List<DDLRecordSet>)QueryUtil.list(
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
	}

	/**
	 * Returns the first ddl record set in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set
	 * @throws NoSuchRecordSetException if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet findByUuid_First(
			String uuid, OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = fetchByUuid_First(uuid, orderByComparator);

		if (ddlRecordSet != null) {
			return ddlRecordSet;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchRecordSetException(sb.toString());
	}

	/**
	 * Returns the first ddl record set in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set, or <code>null</code> if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet fetchByUuid_First(
		String uuid, OrderByComparator<DDLRecordSet> orderByComparator) {

		List<DDLRecordSet> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddl record set in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddl record set
	 * @throws NoSuchRecordSetException if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet findByUuid_Last(
			String uuid, OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = fetchByUuid_Last(uuid, orderByComparator);

		if (ddlRecordSet != null) {
			return ddlRecordSet;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchRecordSetException(sb.toString());
	}

	/**
	 * Returns the last ddl record set in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddl record set, or <code>null</code> if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet fetchByUuid_Last(
		String uuid, OrderByComparator<DDLRecordSet> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<DDLRecordSet> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddl record sets before and after the current ddl record set in the ordered set where uuid = &#63;.
	 *
	 * @param recordSetId the primary key of the current ddl record set
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddl record set
	 * @throws NoSuchRecordSetException if a ddl record set with the primary key could not be found
	 */
	@Override
	public DDLRecordSet[] findByUuid_PrevAndNext(
			long recordSetId, String uuid,
			OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		uuid = Objects.toString(uuid, "");

		DDLRecordSet ddlRecordSet = findByPrimaryKey(recordSetId);

		Session session = null;

		try {
			session = openSession();

			DDLRecordSet[] array = new DDLRecordSetImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, ddlRecordSet, uuid, orderByComparator, true);

			array[1] = ddlRecordSet;

			array[2] = getByUuid_PrevAndNext(
				session, ddlRecordSet, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDLRecordSet getByUuid_PrevAndNext(
		Session session, DDLRecordSet ddlRecordSet, String uuid,
		OrderByComparator<DDLRecordSet> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDLRECORDSET_WHERE);

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
			sb.append(DDLRecordSetModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(ddlRecordSet)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDLRecordSet> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddl record sets where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (DDLRecordSet ddlRecordSet :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddlRecordSet);
		}
	}

	/**
	 * Returns the number of ddl record sets where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddl record sets
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDLRECORDSET_WHERE);

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
	}

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"ddlRecordSet.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(ddlRecordSet.uuid IS NULL OR ddlRecordSet.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the ddl record set where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchRecordSetException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddl record set
	 * @throws NoSuchRecordSetException if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet findByUUID_G(String uuid, long groupId)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = fetchByUUID_G(uuid, groupId);

		if (ddlRecordSet == null) {
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

			throw new NoSuchRecordSetException(sb.toString());
		}

		return ddlRecordSet;
	}

	/**
	 * Returns the ddl record set where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddl record set, or <code>null</code> if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the ddl record set where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddl record set, or <code>null</code> if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			uuid = Objects.toString(uuid, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {uuid, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByUUID_G, finderArgs, this);
			}

			if (result instanceof DDLRecordSet) {
				DDLRecordSet ddlRecordSet = (DDLRecordSet)result;

				if (!Objects.equals(uuid, ddlRecordSet.getUuid()) ||
					(groupId != ddlRecordSet.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DDLRECORDSET_WHERE);

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

					List<DDLRecordSet> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						DDLRecordSet ddlRecordSet = list.get(0);

						result = ddlRecordSet;

						cacheResult(ddlRecordSet);
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
				return (DDLRecordSet)result;
			}
		}
	}

	/**
	 * Removes the ddl record set where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the ddl record set that was removed
	 */
	@Override
	public DDLRecordSet removeByUUID_G(String uuid, long groupId)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = findByUUID_G(uuid, groupId);

		return remove(ddlRecordSet);
	}

	/**
	 * Returns the number of ddl record sets where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching ddl record sets
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		DDLRecordSet ddlRecordSet = fetchByUUID_G(uuid, groupId);

		if (ddlRecordSet == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"ddlRecordSet.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(ddlRecordSet.uuid IS NULL OR ddlRecordSet.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"ddlRecordSet.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the ddl record sets where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddl record sets where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @return the range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddl record sets where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddl record sets where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

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

			List<DDLRecordSet> list = null;

			if (useFinderCache) {
				list = (List<DDLRecordSet>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDLRecordSet ddlRecordSet : list) {
						if (!uuid.equals(ddlRecordSet.getUuid()) ||
							(companyId != ddlRecordSet.getCompanyId())) {

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

				sb.append(_SQL_SELECT_DDLRECORDSET_WHERE);

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
					sb.append(DDLRecordSetModelImpl.ORDER_BY_JPQL);
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

					list = (List<DDLRecordSet>)QueryUtil.list(
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
	}

	/**
	 * Returns the first ddl record set in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set
	 * @throws NoSuchRecordSetException if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (ddlRecordSet != null) {
			return ddlRecordSet;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchRecordSetException(sb.toString());
	}

	/**
	 * Returns the first ddl record set in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set, or <code>null</code> if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		List<DDLRecordSet> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddl record set in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddl record set
	 * @throws NoSuchRecordSetException if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (ddlRecordSet != null) {
			return ddlRecordSet;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchRecordSetException(sb.toString());
	}

	/**
	 * Returns the last ddl record set in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddl record set, or <code>null</code> if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<DDLRecordSet> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddl record sets before and after the current ddl record set in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param recordSetId the primary key of the current ddl record set
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddl record set
	 * @throws NoSuchRecordSetException if a ddl record set with the primary key could not be found
	 */
	@Override
	public DDLRecordSet[] findByUuid_C_PrevAndNext(
			long recordSetId, String uuid, long companyId,
			OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		uuid = Objects.toString(uuid, "");

		DDLRecordSet ddlRecordSet = findByPrimaryKey(recordSetId);

		Session session = null;

		try {
			session = openSession();

			DDLRecordSet[] array = new DDLRecordSetImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, ddlRecordSet, uuid, companyId, orderByComparator,
				true);

			array[1] = ddlRecordSet;

			array[2] = getByUuid_C_PrevAndNext(
				session, ddlRecordSet, uuid, companyId, orderByComparator,
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

	protected DDLRecordSet getByUuid_C_PrevAndNext(
		Session session, DDLRecordSet ddlRecordSet, String uuid, long companyId,
		OrderByComparator<DDLRecordSet> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_DDLRECORDSET_WHERE);

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
			sb.append(DDLRecordSetModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(ddlRecordSet)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDLRecordSet> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddl record sets where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (DDLRecordSet ddlRecordSet :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(ddlRecordSet);
		}
	}

	/**
	 * Returns the number of ddl record sets where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddl record sets
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DDLRECORDSET_WHERE);

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
	}

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"ddlRecordSet.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(ddlRecordSet.uuid IS NULL OR ddlRecordSet.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"ddlRecordSet.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;
	private FinderPath _finderPathWithPaginationCountByGroupId;

	/**
	 * Returns all the ddl record sets where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddl record sets where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @return the range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddl record sets where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddl record sets where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByGroupId;
					finderArgs = new Object[] {groupId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByGroupId;
				finderArgs = new Object[] {
					groupId, start, end, orderByComparator
				};
			}

			List<DDLRecordSet> list = null;

			if (useFinderCache) {
				list = (List<DDLRecordSet>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDLRecordSet ddlRecordSet : list) {
						if (groupId != ddlRecordSet.getGroupId()) {
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

				sb.append(_SQL_SELECT_DDLRECORDSET_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDLRecordSetModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<DDLRecordSet>)QueryUtil.list(
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
	}

	/**
	 * Returns the first ddl record set in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set
	 * @throws NoSuchRecordSetException if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet findByGroupId_First(
			long groupId, OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = fetchByGroupId_First(
			groupId, orderByComparator);

		if (ddlRecordSet != null) {
			return ddlRecordSet;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchRecordSetException(sb.toString());
	}

	/**
	 * Returns the first ddl record set in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set, or <code>null</code> if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet fetchByGroupId_First(
		long groupId, OrderByComparator<DDLRecordSet> orderByComparator) {

		List<DDLRecordSet> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddl record set in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddl record set
	 * @throws NoSuchRecordSetException if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet findByGroupId_Last(
			long groupId, OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (ddlRecordSet != null) {
			return ddlRecordSet;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchRecordSetException(sb.toString());
	}

	/**
	 * Returns the last ddl record set in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddl record set, or <code>null</code> if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet fetchByGroupId_Last(
		long groupId, OrderByComparator<DDLRecordSet> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<DDLRecordSet> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddl record sets before and after the current ddl record set in the ordered set where groupId = &#63;.
	 *
	 * @param recordSetId the primary key of the current ddl record set
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddl record set
	 * @throws NoSuchRecordSetException if a ddl record set with the primary key could not be found
	 */
	@Override
	public DDLRecordSet[] findByGroupId_PrevAndNext(
			long recordSetId, long groupId,
			OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = findByPrimaryKey(recordSetId);

		Session session = null;

		try {
			session = openSession();

			DDLRecordSet[] array = new DDLRecordSetImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, ddlRecordSet, groupId, orderByComparator, true);

			array[1] = ddlRecordSet;

			array[2] = getByGroupId_PrevAndNext(
				session, ddlRecordSet, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDLRecordSet getByGroupId_PrevAndNext(
		Session session, DDLRecordSet ddlRecordSet, long groupId,
		OrderByComparator<DDLRecordSet> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDLRECORDSET_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

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
			sb.append(DDLRecordSetModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddlRecordSet)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDLRecordSet> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddl record sets that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching ddl record sets that the user has permission to view
	 */
	@Override
	public List<DDLRecordSet> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddl record sets that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @return the range of matching ddl record sets that the user has permission to view
	 */
	@Override
	public List<DDLRecordSet> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddl record sets that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record sets that the user has permission to view
	 */
	@Override
	public List<DDLRecordSet> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId(groupId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_DDLRECORDSET_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDLRECORDSET_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDLRECORDSET_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDLRecordSetModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDLRecordSetModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDLRecordSet.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DDLRecordSetImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DDLRecordSetImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<DDLRecordSet>)QueryUtil.list(
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
	 * Returns the ddl record sets before and after the current ddl record set in the ordered set of ddl record sets that the user has permission to view where groupId = &#63;.
	 *
	 * @param recordSetId the primary key of the current ddl record set
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddl record set
	 * @throws NoSuchRecordSetException if a ddl record set with the primary key could not be found
	 */
	@Override
	public DDLRecordSet[] filterFindByGroupId_PrevAndNext(
			long recordSetId, long groupId,
			OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				recordSetId, groupId, orderByComparator);
		}

		DDLRecordSet ddlRecordSet = findByPrimaryKey(recordSetId);

		Session session = null;

		try {
			session = openSession();

			DDLRecordSet[] array = new DDLRecordSetImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, ddlRecordSet, groupId, orderByComparator, true);

			array[1] = ddlRecordSet;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, ddlRecordSet, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected DDLRecordSet filterGetByGroupId_PrevAndNext(
		Session session, DDLRecordSet ddlRecordSet, long groupId,
		OrderByComparator<DDLRecordSet> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_DDLRECORDSET_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDLRECORDSET_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDLRECORDSET_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDLRecordSetModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDLRecordSetModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDLRecordSet.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, DDLRecordSetImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, DDLRecordSetImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddlRecordSet)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDLRecordSet> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddl record sets that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching ddl record sets that the user has permission to view
	 */
	@Override
	public List<DDLRecordSet> filterFindByGroupId(long[] groupIds) {
		return filterFindByGroupId(
			groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddl record sets that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @return the range of matching ddl record sets that the user has permission to view
	 */
	@Override
	public List<DDLRecordSet> filterFindByGroupId(
		long[] groupIds, int start, int end) {

		return filterFindByGroupId(groupIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddl record sets that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record sets that the user has permission to view
	 */
	@Override
	public List<DDLRecordSet> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return findByGroupId(groupIds, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByGroupId(
					groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupIds);
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_DDLRECORDSET_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDLRECORDSET_NO_INLINE_DISTINCT_WHERE_1);
		}

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDLRECORDSET_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(DDLRecordSetModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDLRecordSetModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDLRecordSet.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DDLRecordSetImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DDLRecordSetImpl.class);
			}

			return (List<DDLRecordSet>)QueryUtil.list(
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
	 * Returns all the ddl record sets where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @return the matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByGroupId(long[] groupIds) {
		return findByGroupId(
			groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddl record sets where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @return the range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByGroupId(
		long[] groupIds, int start, int end) {

		return findByGroupId(groupIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddl record sets where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		return findByGroupId(groupIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddl record sets where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator,
		boolean useFinderCache) {

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		if (groupIds.length == 1) {
			return findByGroupId(groupIds[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {StringUtil.merge(groupIds)};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(groupIds), start, end, orderByComparator
				};
			}

			List<DDLRecordSet> list = null;

			if (useFinderCache) {
				list = (List<DDLRecordSet>)finderCache.getResult(
					_finderPathWithPaginationFindByGroupId, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDLRecordSet ddlRecordSet : list) {
						if (!ArrayUtil.contains(
								groupIds, ddlRecordSet.getGroupId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_DDLRECORDSET_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDLRecordSetModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DDLRecordSet>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByGroupId, finderArgs,
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
	}

	/**
	 * Removes all the ddl record sets where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (DDLRecordSet ddlRecordSet :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddlRecordSet);
		}
	}

	/**
	 * Returns the number of ddl record sets where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddl record sets
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDLRECORDSET_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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
	}

	/**
	 * Returns the number of ddl record sets where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddl record sets
	 */
	@Override
	public int countByGroupId(long[] groupIds) {
		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			Object[] finderArgs = new Object[] {StringUtil.merge(groupIds)};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByGroupId, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_DDLRECORDSET_WHERE);

				if (groupIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

					sb.append(StringUtil.merge(groupIds));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByGroupId, finderArgs,
						count);
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

	/**
	 * Returns the number of ddl record sets that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddl record sets that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDLRecordSet> ddlRecordSets = findByGroupId(groupId);

			ddlRecordSets = InlineSQLHelperUtil.filter(ddlRecordSets, groupId);

			return ddlRecordSets.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_DDLRECORDSET_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDLRecordSet.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

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
	 * Returns the number of ddl record sets that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddl record sets that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long[] groupIds) {
		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return countByGroupId(groupIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDLRecordSet> ddlRecordSets = InlineSQLHelperUtil.filter(
				findByGroupId(groupIds), groupIds);

			return ddlRecordSets.size();
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_DDLRECORDSET_WHERE);

		if (groupIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_GROUPID_GROUPID_7);

			sb.append(StringUtil.merge(groupIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDLRecordSet.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

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

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"ddlRecordSet.groupId = ?";

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_7 =
		"ddlRecordSet.groupId IN (";

	private FinderPath _finderPathWithPaginationFindByDDMStructureId;
	private FinderPath _finderPathWithoutPaginationFindByDDMStructureId;
	private FinderPath _finderPathCountByDDMStructureId;
	private FinderPath _finderPathWithPaginationCountByDDMStructureId;

	/**
	 * Returns all the ddl record sets where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @return the matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByDDMStructureId(long DDMStructureId) {
		return findByDDMStructureId(
			DDMStructureId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddl record sets where DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @return the range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByDDMStructureId(
		long DDMStructureId, int start, int end) {

		return findByDDMStructureId(DDMStructureId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddl record sets where DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByDDMStructureId(
		long DDMStructureId, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		return findByDDMStructureId(
			DDMStructureId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddl record sets where DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByDDMStructureId(
		long DDMStructureId, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByDDMStructureId;
					finderArgs = new Object[] {DDMStructureId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByDDMStructureId;
				finderArgs = new Object[] {
					DDMStructureId, start, end, orderByComparator
				};
			}

			List<DDLRecordSet> list = null;

			if (useFinderCache) {
				list = (List<DDLRecordSet>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDLRecordSet ddlRecordSet : list) {
						if (DDMStructureId !=
								ddlRecordSet.getDDMStructureId()) {

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

				sb.append(_SQL_SELECT_DDLRECORDSET_WHERE);

				sb.append(_FINDER_COLUMN_DDMSTRUCTUREID_DDMSTRUCTUREID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDLRecordSetModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(DDMStructureId);

					list = (List<DDLRecordSet>)QueryUtil.list(
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
	}

	/**
	 * Returns the first ddl record set in the ordered set where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set
	 * @throws NoSuchRecordSetException if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet findByDDMStructureId_First(
			long DDMStructureId,
			OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = fetchByDDMStructureId_First(
			DDMStructureId, orderByComparator);

		if (ddlRecordSet != null) {
			return ddlRecordSet;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("DDMStructureId=");
		sb.append(DDMStructureId);

		sb.append("}");

		throw new NoSuchRecordSetException(sb.toString());
	}

	/**
	 * Returns the first ddl record set in the ordered set where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddl record set, or <code>null</code> if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet fetchByDDMStructureId_First(
		long DDMStructureId,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		List<DDLRecordSet> list = findByDDMStructureId(
			DDMStructureId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddl record set in the ordered set where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddl record set
	 * @throws NoSuchRecordSetException if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet findByDDMStructureId_Last(
			long DDMStructureId,
			OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = fetchByDDMStructureId_Last(
			DDMStructureId, orderByComparator);

		if (ddlRecordSet != null) {
			return ddlRecordSet;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("DDMStructureId=");
		sb.append(DDMStructureId);

		sb.append("}");

		throw new NoSuchRecordSetException(sb.toString());
	}

	/**
	 * Returns the last ddl record set in the ordered set where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddl record set, or <code>null</code> if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet fetchByDDMStructureId_Last(
		long DDMStructureId,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		int count = countByDDMStructureId(DDMStructureId);

		if (count == 0) {
			return null;
		}

		List<DDLRecordSet> list = findByDDMStructureId(
			DDMStructureId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddl record sets before and after the current ddl record set in the ordered set where DDMStructureId = &#63;.
	 *
	 * @param recordSetId the primary key of the current ddl record set
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddl record set
	 * @throws NoSuchRecordSetException if a ddl record set with the primary key could not be found
	 */
	@Override
	public DDLRecordSet[] findByDDMStructureId_PrevAndNext(
			long recordSetId, long DDMStructureId,
			OrderByComparator<DDLRecordSet> orderByComparator)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = findByPrimaryKey(recordSetId);

		Session session = null;

		try {
			session = openSession();

			DDLRecordSet[] array = new DDLRecordSetImpl[3];

			array[0] = getByDDMStructureId_PrevAndNext(
				session, ddlRecordSet, DDMStructureId, orderByComparator, true);

			array[1] = ddlRecordSet;

			array[2] = getByDDMStructureId_PrevAndNext(
				session, ddlRecordSet, DDMStructureId, orderByComparator,
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

	protected DDLRecordSet getByDDMStructureId_PrevAndNext(
		Session session, DDLRecordSet ddlRecordSet, long DDMStructureId,
		OrderByComparator<DDLRecordSet> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_DDLRECORDSET_WHERE);

		sb.append(_FINDER_COLUMN_DDMSTRUCTUREID_DDMSTRUCTUREID_2);

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
			sb.append(DDLRecordSetModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(DDMStructureId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(ddlRecordSet)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDLRecordSet> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddl record sets where DDMStructureId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param DDMStructureIds the ddm structure IDs
	 * @return the matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByDDMStructureId(long[] DDMStructureIds) {
		return findByDDMStructureId(
			DDMStructureIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddl record sets where DDMStructureId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param DDMStructureIds the ddm structure IDs
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @return the range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByDDMStructureId(
		long[] DDMStructureIds, int start, int end) {

		return findByDDMStructureId(DDMStructureIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddl record sets where DDMStructureId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param DDMStructureIds the ddm structure IDs
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByDDMStructureId(
		long[] DDMStructureIds, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator) {

		return findByDDMStructureId(
			DDMStructureIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddl record sets where DDMStructureId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param DDMStructureIds the ddm structure IDs
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findByDDMStructureId(
		long[] DDMStructureIds, int start, int end,
		OrderByComparator<DDLRecordSet> orderByComparator,
		boolean useFinderCache) {

		if (DDMStructureIds == null) {
			DDMStructureIds = new long[0];
		}
		else if (DDMStructureIds.length > 1) {
			DDMStructureIds = ArrayUtil.sortedUnique(DDMStructureIds);
		}

		if (DDMStructureIds.length == 1) {
			return findByDDMStructureId(
				DDMStructureIds[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						StringUtil.merge(DDMStructureIds)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					StringUtil.merge(DDMStructureIds), start, end,
					orderByComparator
				};
			}

			List<DDLRecordSet> list = null;

			if (useFinderCache) {
				list = (List<DDLRecordSet>)finderCache.getResult(
					_finderPathWithPaginationFindByDDMStructureId, finderArgs,
					this);

				if ((list != null) && !list.isEmpty()) {
					for (DDLRecordSet ddlRecordSet : list) {
						if (!ArrayUtil.contains(
								DDMStructureIds,
								ddlRecordSet.getDDMStructureId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_DDLRECORDSET_WHERE);

				if (DDMStructureIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_DDMSTRUCTUREID_DDMSTRUCTUREID_7);

					sb.append(StringUtil.merge(DDMStructureIds));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDLRecordSetModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DDLRecordSet>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByDDMStructureId,
							finderArgs, list);
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
	 * Removes all the ddl record sets where DDMStructureId = &#63; from the database.
	 *
	 * @param DDMStructureId the ddm structure ID
	 */
	@Override
	public void removeByDDMStructureId(long DDMStructureId) {
		for (DDLRecordSet ddlRecordSet :
				findByDDMStructureId(
					DDMStructureId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(ddlRecordSet);
		}
	}

	/**
	 * Returns the number of ddl record sets where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @return the number of matching ddl record sets
	 */
	@Override
	public int countByDDMStructureId(long DDMStructureId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			FinderPath finderPath = _finderPathCountByDDMStructureId;

			Object[] finderArgs = new Object[] {DDMStructureId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDLRECORDSET_WHERE);

				sb.append(_FINDER_COLUMN_DDMSTRUCTUREID_DDMSTRUCTUREID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(DDMStructureId);

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
	}

	/**
	 * Returns the number of ddl record sets where DDMStructureId = any &#63;.
	 *
	 * @param DDMStructureIds the ddm structure IDs
	 * @return the number of matching ddl record sets
	 */
	@Override
	public int countByDDMStructureId(long[] DDMStructureIds) {
		if (DDMStructureIds == null) {
			DDMStructureIds = new long[0];
		}
		else if (DDMStructureIds.length > 1) {
			DDMStructureIds = ArrayUtil.sortedUnique(DDMStructureIds);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			Object[] finderArgs = new Object[] {
				StringUtil.merge(DDMStructureIds)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByDDMStructureId, finderArgs,
				this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_DDLRECORDSET_WHERE);

				if (DDMStructureIds.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_DDMSTRUCTUREID_DDMSTRUCTUREID_7);

					sb.append(StringUtil.merge(DDMStructureIds));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByDDMStructureId,
						finderArgs, count);
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

	private static final String _FINDER_COLUMN_DDMSTRUCTUREID_DDMSTRUCTUREID_2 =
		"ddlRecordSet.DDMStructureId = ?";

	private static final String _FINDER_COLUMN_DDMSTRUCTUREID_DDMSTRUCTUREID_7 =
		"ddlRecordSet.DDMStructureId IN (";

	private FinderPath _finderPathFetchByG_R;

	/**
	 * Returns the ddl record set where groupId = &#63; and recordSetKey = &#63; or throws a <code>NoSuchRecordSetException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param recordSetKey the record set key
	 * @return the matching ddl record set
	 * @throws NoSuchRecordSetException if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet findByG_R(long groupId, String recordSetKey)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = fetchByG_R(groupId, recordSetKey);

		if (ddlRecordSet == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", recordSetKey=");
			sb.append(recordSetKey);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchRecordSetException(sb.toString());
		}

		return ddlRecordSet;
	}

	/**
	 * Returns the ddl record set where groupId = &#63; and recordSetKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param recordSetKey the record set key
	 * @return the matching ddl record set, or <code>null</code> if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet fetchByG_R(long groupId, String recordSetKey) {
		return fetchByG_R(groupId, recordSetKey, true);
	}

	/**
	 * Returns the ddl record set where groupId = &#63; and recordSetKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param recordSetKey the record set key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddl record set, or <code>null</code> if a matching ddl record set could not be found
	 */
	@Override
	public DDLRecordSet fetchByG_R(
		long groupId, String recordSetKey, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			recordSetKey = Objects.toString(recordSetKey, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, recordSetKey};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_R, finderArgs, this);
			}

			if (result instanceof DDLRecordSet) {
				DDLRecordSet ddlRecordSet = (DDLRecordSet)result;

				if ((groupId != ddlRecordSet.getGroupId()) ||
					!Objects.equals(
						recordSetKey, ddlRecordSet.getRecordSetKey())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DDLRECORDSET_WHERE);

				sb.append(_FINDER_COLUMN_G_R_GROUPID_2);

				boolean bindRecordSetKey = false;

				if (recordSetKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_R_RECORDSETKEY_3);
				}
				else {
					bindRecordSetKey = true;

					sb.append(_FINDER_COLUMN_G_R_RECORDSETKEY_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindRecordSetKey) {
						queryPos.add(recordSetKey);
					}

					List<DDLRecordSet> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_R, finderArgs, list);
						}
					}
					else {
						DDLRecordSet ddlRecordSet = list.get(0);

						result = ddlRecordSet;

						cacheResult(ddlRecordSet);
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
				return (DDLRecordSet)result;
			}
		}
	}

	/**
	 * Removes the ddl record set where groupId = &#63; and recordSetKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param recordSetKey the record set key
	 * @return the ddl record set that was removed
	 */
	@Override
	public DDLRecordSet removeByG_R(long groupId, String recordSetKey)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = findByG_R(groupId, recordSetKey);

		return remove(ddlRecordSet);
	}

	/**
	 * Returns the number of ddl record sets where groupId = &#63; and recordSetKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param recordSetKey the record set key
	 * @return the number of matching ddl record sets
	 */
	@Override
	public int countByG_R(long groupId, String recordSetKey) {
		DDLRecordSet ddlRecordSet = fetchByG_R(groupId, recordSetKey);

		if (ddlRecordSet == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_R_GROUPID_2 =
		"ddlRecordSet.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_R_RECORDSETKEY_2 =
		"ddlRecordSet.recordSetKey = ?";

	private static final String _FINDER_COLUMN_G_R_RECORDSETKEY_3 =
		"(ddlRecordSet.recordSetKey IS NULL OR ddlRecordSet.recordSetKey = '')";

	public DDLRecordSetPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("settings", "settings_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDLRecordSet.class);

		setModelImplClass(DDLRecordSetImpl.class);
		setModelPKClass(long.class);

		setTable(DDLRecordSetTable.INSTANCE);
	}

	/**
	 * Caches the ddl record set in the entity cache if it is enabled.
	 *
	 * @param ddlRecordSet the ddl record set
	 */
	@Override
	public void cacheResult(DDLRecordSet ddlRecordSet) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ddlRecordSet.getCtCollectionId())) {

			entityCache.putResult(
				DDLRecordSetImpl.class, ddlRecordSet.getPrimaryKey(),
				ddlRecordSet);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					ddlRecordSet.getUuid(), ddlRecordSet.getGroupId()
				},
				ddlRecordSet);

			finderCache.putResult(
				_finderPathFetchByG_R,
				new Object[] {
					ddlRecordSet.getGroupId(), ddlRecordSet.getRecordSetKey()
				},
				ddlRecordSet);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the ddl record sets in the entity cache if it is enabled.
	 *
	 * @param ddlRecordSets the ddl record sets
	 */
	@Override
	public void cacheResult(List<DDLRecordSet> ddlRecordSets) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (ddlRecordSets.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (DDLRecordSet ddlRecordSet : ddlRecordSets) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ddlRecordSet.getCtCollectionId())) {

				DDLRecordSet cachedDDLRecordSet =
					(DDLRecordSet)entityCache.getResult(
						DDLRecordSetImpl.class, ddlRecordSet.getPrimaryKey());

				if (cachedDDLRecordSet == null) {
					cacheResult(ddlRecordSet);
				}
				else {
					DDLRecordSetModelImpl ddlRecordSetModelImpl =
						(DDLRecordSetModelImpl)ddlRecordSet;
					DDLRecordSetModelImpl cachedDDLRecordSetModelImpl =
						(DDLRecordSetModelImpl)cachedDDLRecordSet;

					ddlRecordSetModelImpl.setSettingsDDMFormValues(
						cachedDDLRecordSetModelImpl.getSettingsDDMFormValues());
				}
			}
		}
	}

	/**
	 * Clears the cache for all ddl record sets.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(DDLRecordSetImpl.class);

		finderCache.clearCache(DDLRecordSetImpl.class);
	}

	/**
	 * Clears the cache for the ddl record set.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DDLRecordSet ddlRecordSet) {
		entityCache.removeResult(DDLRecordSetImpl.class, ddlRecordSet);
	}

	@Override
	public void clearCache(List<DDLRecordSet> ddlRecordSets) {
		for (DDLRecordSet ddlRecordSet : ddlRecordSets) {
			entityCache.removeResult(DDLRecordSetImpl.class, ddlRecordSet);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(DDLRecordSetImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(DDLRecordSetImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		DDLRecordSetModelImpl ddlRecordSetModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ddlRecordSetModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				ddlRecordSetModelImpl.getUuid(),
				ddlRecordSetModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, ddlRecordSetModelImpl);

			args = new Object[] {
				ddlRecordSetModelImpl.getGroupId(),
				ddlRecordSetModelImpl.getRecordSetKey()
			};

			finderCache.putResult(
				_finderPathFetchByG_R, args, ddlRecordSetModelImpl);
		}
	}

	/**
	 * Creates a new ddl record set with the primary key. Does not add the ddl record set to the database.
	 *
	 * @param recordSetId the primary key for the new ddl record set
	 * @return the new ddl record set
	 */
	@Override
	public DDLRecordSet create(long recordSetId) {
		DDLRecordSet ddlRecordSet = new DDLRecordSetImpl();

		ddlRecordSet.setNew(true);
		ddlRecordSet.setPrimaryKey(recordSetId);

		String uuid = PortalUUIDUtil.generate();

		ddlRecordSet.setUuid(uuid);

		ddlRecordSet.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddlRecordSet;
	}

	/**
	 * Removes the ddl record set with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param recordSetId the primary key of the ddl record set
	 * @return the ddl record set that was removed
	 * @throws NoSuchRecordSetException if a ddl record set with the primary key could not be found
	 */
	@Override
	public DDLRecordSet remove(long recordSetId)
		throws NoSuchRecordSetException {

		return remove((Serializable)recordSetId);
	}

	/**
	 * Removes the ddl record set with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the ddl record set
	 * @return the ddl record set that was removed
	 * @throws NoSuchRecordSetException if a ddl record set with the primary key could not be found
	 */
	@Override
	public DDLRecordSet remove(Serializable primaryKey)
		throws NoSuchRecordSetException {

		Session session = null;

		try {
			session = openSession();

			DDLRecordSet ddlRecordSet = (DDLRecordSet)session.get(
				DDLRecordSetImpl.class, primaryKey);

			if (ddlRecordSet == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchRecordSetException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ddlRecordSet);
		}
		catch (NoSuchRecordSetException noSuchEntityException) {
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
	protected DDLRecordSet removeImpl(DDLRecordSet ddlRecordSet) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddlRecordSet)) {
				ddlRecordSet = (DDLRecordSet)session.get(
					DDLRecordSetImpl.class, ddlRecordSet.getPrimaryKeyObj());
			}

			if ((ddlRecordSet != null) &&
				ctPersistenceHelper.isRemove(ddlRecordSet)) {

				session.delete(ddlRecordSet);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddlRecordSet != null) {
			clearCache(ddlRecordSet);
		}

		return ddlRecordSet;
	}

	@Override
	public DDLRecordSet updateImpl(DDLRecordSet ddlRecordSet) {
		boolean isNew = ddlRecordSet.isNew();

		if (!(ddlRecordSet instanceof DDLRecordSetModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddlRecordSet.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddlRecordSet);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddlRecordSet proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDLRecordSet implementation " +
					ddlRecordSet.getClass());
		}

		DDLRecordSetModelImpl ddlRecordSetModelImpl =
			(DDLRecordSetModelImpl)ddlRecordSet;

		if (Validator.isNull(ddlRecordSet.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ddlRecordSet.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ddlRecordSet.getCreateDate() == null)) {
			if (serviceContext == null) {
				ddlRecordSet.setCreateDate(date);
			}
			else {
				ddlRecordSet.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!ddlRecordSetModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ddlRecordSet.setModifiedDate(date);
			}
			else {
				ddlRecordSet.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddlRecordSet)) {
				if (!isNew) {
					session.evict(
						DDLRecordSetImpl.class,
						ddlRecordSet.getPrimaryKeyObj());
				}

				session.save(ddlRecordSet);
			}
			else {
				ddlRecordSet = (DDLRecordSet)session.merge(ddlRecordSet);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			DDLRecordSetImpl.class, ddlRecordSetModelImpl, false, true);

		cacheUniqueFindersCache(ddlRecordSetModelImpl);

		if (isNew) {
			ddlRecordSet.setNew(false);
		}

		ddlRecordSet.resetOriginalValues();

		return ddlRecordSet;
	}

	/**
	 * Returns the ddl record set with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the ddl record set
	 * @return the ddl record set
	 * @throws NoSuchRecordSetException if a ddl record set with the primary key could not be found
	 */
	@Override
	public DDLRecordSet findByPrimaryKey(Serializable primaryKey)
		throws NoSuchRecordSetException {

		DDLRecordSet ddlRecordSet = fetchByPrimaryKey(primaryKey);

		if (ddlRecordSet == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchRecordSetException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ddlRecordSet;
	}

	/**
	 * Returns the ddl record set with the primary key or throws a <code>NoSuchRecordSetException</code> if it could not be found.
	 *
	 * @param recordSetId the primary key of the ddl record set
	 * @return the ddl record set
	 * @throws NoSuchRecordSetException if a ddl record set with the primary key could not be found
	 */
	@Override
	public DDLRecordSet findByPrimaryKey(long recordSetId)
		throws NoSuchRecordSetException {

		return findByPrimaryKey((Serializable)recordSetId);
	}

	/**
	 * Returns the ddl record set with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the ddl record set
	 * @return the ddl record set, or <code>null</code> if a ddl record set with the primary key could not be found
	 */
	@Override
	public DDLRecordSet fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				DDLRecordSet.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		DDLRecordSet ddlRecordSet = (DDLRecordSet)entityCache.getResult(
			DDLRecordSetImpl.class, primaryKey);

		if (ddlRecordSet != null) {
			return ddlRecordSet;
		}

		Session session = null;

		try {
			session = openSession();

			ddlRecordSet = (DDLRecordSet)session.get(
				DDLRecordSetImpl.class, primaryKey);

			if (ddlRecordSet != null) {
				cacheResult(ddlRecordSet);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return ddlRecordSet;
	}

	/**
	 * Returns the ddl record set with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param recordSetId the primary key of the ddl record set
	 * @return the ddl record set, or <code>null</code> if a ddl record set with the primary key could not be found
	 */
	@Override
	public DDLRecordSet fetchByPrimaryKey(long recordSetId) {
		return fetchByPrimaryKey((Serializable)recordSetId);
	}

	@Override
	public Map<Serializable, DDLRecordSet> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(DDLRecordSet.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, DDLRecordSet> map =
			new HashMap<Serializable, DDLRecordSet>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			DDLRecordSet ddlRecordSet = fetchByPrimaryKey(primaryKey);

			if (ddlRecordSet != null) {
				map.put(primaryKey, ddlRecordSet);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						DDLRecordSet.class, primaryKey)) {

				DDLRecordSet ddlRecordSet = (DDLRecordSet)entityCache.getResult(
					DDLRecordSetImpl.class, primaryKey);

				if (ddlRecordSet == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, ddlRecordSet);
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

			for (DDLRecordSet ddlRecordSet : (List<DDLRecordSet>)query.list()) {
				map.put(ddlRecordSet.getPrimaryKeyObj(), ddlRecordSet);

				cacheResult(ddlRecordSet);
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
	 * Returns all the ddl record sets.
	 *
	 * @return the ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddl record sets.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @return the range of ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddl record sets.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findAll(
		int start, int end, OrderByComparator<DDLRecordSet> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddl record sets.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDLRecordSetModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddl record sets
	 * @param end the upper bound of the range of ddl record sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of ddl record sets
	 */
	@Override
	public List<DDLRecordSet> findAll(
		int start, int end, OrderByComparator<DDLRecordSet> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

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

			List<DDLRecordSet> list = null;

			if (useFinderCache) {
				list = (List<DDLRecordSet>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_DDLRECORDSET);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_DDLRECORDSET;

					sql = sql.concat(DDLRecordSetModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DDLRecordSet>)QueryUtil.list(
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
	}

	/**
	 * Removes all the ddl record sets from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DDLRecordSet ddlRecordSet : findAll()) {
			remove(ddlRecordSet);
		}
	}

	/**
	 * Returns the number of ddl record sets.
	 *
	 * @return the number of ddl record sets
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDLRecordSet.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_DDLRECORDSET);

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
		return "recordSetId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDLRECORDSET;
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
		return DDLRecordSetModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDLRecordSet";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctMergeColumnNames.add("versionUserId");
		ctMergeColumnNames.add("versionUserName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("DDMStructureId");
		ctMergeColumnNames.add("recordSetKey");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("minDisplayRows");
		ctMergeColumnNames.add("scope");
		ctMergeColumnNames.add("settings_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("recordSetId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"groupId", "recordSetKey"});
	}

	/**
	 * Initializes the ddl record set persistence.
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

		_finderPathWithPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId"}, true);

		_finderPathWithoutPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			true);

		_finderPathCountByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		_finderPathWithPaginationCountByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		_finderPathWithPaginationFindByDDMStructureId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByDDMStructureId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"DDMStructureId"}, true);

		_finderPathWithoutPaginationFindByDDMStructureId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByDDMStructureId",
			new String[] {Long.class.getName()},
			new String[] {"DDMStructureId"}, true);

		_finderPathCountByDDMStructureId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByDDMStructureId",
			new String[] {Long.class.getName()},
			new String[] {"DDMStructureId"}, false);

		_finderPathWithPaginationCountByDDMStructureId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByDDMStructureId",
			new String[] {Long.class.getName()},
			new String[] {"DDMStructureId"}, false);

		_finderPathFetchByG_R = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_R",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "recordSetKey"}, true);

		DDLRecordSetUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDLRecordSetUtil.setPersistence(null);

		entityCache.removeCache(DDLRecordSetImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDLPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDLPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_DDLRECORDSET =
		"SELECT ddlRecordSet FROM DDLRecordSet ddlRecordSet";

	private static final String _SQL_SELECT_DDLRECORDSET_WHERE =
		"SELECT ddlRecordSet FROM DDLRecordSet ddlRecordSet WHERE ";

	private static final String _SQL_COUNT_DDLRECORDSET =
		"SELECT COUNT(ddlRecordSet) FROM DDLRecordSet ddlRecordSet";

	private static final String _SQL_COUNT_DDLRECORDSET_WHERE =
		"SELECT COUNT(ddlRecordSet) FROM DDLRecordSet ddlRecordSet WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"ddlRecordSet.recordSetId";

	private static final String _FILTER_SQL_SELECT_DDLRECORDSET_WHERE =
		"SELECT DISTINCT {ddlRecordSet.*} FROM DDLRecordSet ddlRecordSet WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DDLRECORDSET_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {DDLRecordSet.*} FROM (SELECT DISTINCT ddlRecordSet.recordSetId FROM DDLRecordSet ddlRecordSet WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DDLRECORDSET_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN DDLRecordSet ON TEMP_TABLE.recordSetId = DDLRecordSet.recordSetId";

	private static final String _FILTER_SQL_COUNT_DDLRECORDSET_WHERE =
		"SELECT COUNT(DISTINCT ddlRecordSet.recordSetId) AS COUNT_VALUE FROM DDLRecordSet ddlRecordSet WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "ddlRecordSet";

	private static final String _FILTER_ENTITY_TABLE = "DDLRecordSet";

	private static final String _ORDER_BY_ENTITY_ALIAS = "ddlRecordSet.";

	private static final String _ORDER_BY_ENTITY_TABLE = "DDLRecordSet.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No DDLRecordSet exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDLRecordSet exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDLRecordSetPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "settings"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}