/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchDataProviderInstanceException;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstanceTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMDataProviderInstancePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMDataProviderInstanceUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
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
 * The persistence implementation for the ddm data provider instance service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMDataProviderInstancePersistence.class)
public class DDMDataProviderInstancePersistenceImpl
	extends BasePersistenceImpl<DDMDataProviderInstance>
	implements DDMDataProviderInstancePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMDataProviderInstanceUtil</code> to access the ddm data provider instance persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMDataProviderInstanceImpl.class.getName();

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
	 * Returns all the ddm data provider instances where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm data provider instances where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @return the range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMDataProviderInstance.class)) {

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

			List<DDMDataProviderInstance> list = null;

			if (useFinderCache) {
				list = (List<DDMDataProviderInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMDataProviderInstance ddmDataProviderInstance :
							list) {

						if (!uuid.equals(ddmDataProviderInstance.getUuid())) {
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

				sb.append(_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);

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
					sb.append(DDMDataProviderInstanceModelImpl.ORDER_BY_JPQL);
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

					list = (List<DDMDataProviderInstance>)QueryUtil.list(
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
	 * Returns the first ddm data provider instance in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance findByUuid_First(
			String uuid,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance = fetchByUuid_First(
			uuid, orderByComparator);

		if (ddmDataProviderInstance != null) {
			return ddmDataProviderInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchDataProviderInstanceException(sb.toString());
	}

	/**
	 * Returns the first ddm data provider instance in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm data provider instance, or <code>null</code> if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchByUuid_First(
		String uuid,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		List<DDMDataProviderInstance> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm data provider instance in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance findByUuid_Last(
			String uuid,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance = fetchByUuid_Last(
			uuid, orderByComparator);

		if (ddmDataProviderInstance != null) {
			return ddmDataProviderInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchDataProviderInstanceException(sb.toString());
	}

	/**
	 * Returns the last ddm data provider instance in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm data provider instance, or <code>null</code> if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchByUuid_Last(
		String uuid,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<DDMDataProviderInstance> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm data provider instances before and after the current ddm data provider instance in the ordered set where uuid = &#63;.
	 *
	 * @param dataProviderInstanceId the primary key of the current ddm data provider instance
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a ddm data provider instance with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstance[] findByUuid_PrevAndNext(
			long dataProviderInstanceId, String uuid,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		uuid = Objects.toString(uuid, "");

		DDMDataProviderInstance ddmDataProviderInstance = findByPrimaryKey(
			dataProviderInstanceId);

		Session session = null;

		try {
			session = openSession();

			DDMDataProviderInstance[] array =
				new DDMDataProviderInstanceImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, ddmDataProviderInstance, uuid, orderByComparator,
				true);

			array[1] = ddmDataProviderInstance;

			array[2] = getByUuid_PrevAndNext(
				session, ddmDataProviderInstance, uuid, orderByComparator,
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

	protected DDMDataProviderInstance getByUuid_PrevAndNext(
		Session session, DDMDataProviderInstance ddmDataProviderInstance,
		String uuid,
		OrderByComparator<DDMDataProviderInstance> orderByComparator,
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

		sb.append(_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);

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
			sb.append(DDMDataProviderInstanceModelImpl.ORDER_BY_JPQL);
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
						ddmDataProviderInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMDataProviderInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm data provider instances where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (DDMDataProviderInstance ddmDataProviderInstance :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmDataProviderInstance);
		}
	}

	/**
	 * Returns the number of ddm data provider instances where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddm data provider instances
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMDataProviderInstance.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMDATAPROVIDERINSTANCE_WHERE);

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
		"ddmDataProviderInstance.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(ddmDataProviderInstance.uuid IS NULL OR ddmDataProviderInstance.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the ddm data provider instance where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchDataProviderInstanceException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance findByUUID_G(String uuid, long groupId)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance = fetchByUUID_G(
			uuid, groupId);

		if (ddmDataProviderInstance == null) {
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

			throw new NoSuchDataProviderInstanceException(sb.toString());
		}

		return ddmDataProviderInstance;
	}

	/**
	 * Returns the ddm data provider instance where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm data provider instance, or <code>null</code> if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the ddm data provider instance where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm data provider instance, or <code>null</code> if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMDataProviderInstance.class)) {

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

			if (result instanceof DDMDataProviderInstance) {
				DDMDataProviderInstance ddmDataProviderInstance =
					(DDMDataProviderInstance)result;

				if (!Objects.equals(uuid, ddmDataProviderInstance.getUuid()) ||
					(groupId != ddmDataProviderInstance.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);

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

					List<DDMDataProviderInstance> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						DDMDataProviderInstance ddmDataProviderInstance =
							list.get(0);

						result = ddmDataProviderInstance;

						cacheResult(ddmDataProviderInstance);
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
				return (DDMDataProviderInstance)result;
			}
		}
	}

	/**
	 * Removes the ddm data provider instance where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the ddm data provider instance that was removed
	 */
	@Override
	public DDMDataProviderInstance removeByUUID_G(String uuid, long groupId)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance = findByUUID_G(
			uuid, groupId);

		return remove(ddmDataProviderInstance);
	}

	/**
	 * Returns the number of ddm data provider instances where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching ddm data provider instances
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		DDMDataProviderInstance ddmDataProviderInstance = fetchByUUID_G(
			uuid, groupId);

		if (ddmDataProviderInstance == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"ddmDataProviderInstance.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(ddmDataProviderInstance.uuid IS NULL OR ddmDataProviderInstance.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"ddmDataProviderInstance.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the ddm data provider instances where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm data provider instances where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @return the range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMDataProviderInstance.class)) {

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

			List<DDMDataProviderInstance> list = null;

			if (useFinderCache) {
				list = (List<DDMDataProviderInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMDataProviderInstance ddmDataProviderInstance :
							list) {

						if (!uuid.equals(ddmDataProviderInstance.getUuid()) ||
							(companyId !=
								ddmDataProviderInstance.getCompanyId())) {

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

				sb.append(_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);

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
					sb.append(DDMDataProviderInstanceModelImpl.ORDER_BY_JPQL);
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

					list = (List<DDMDataProviderInstance>)QueryUtil.list(
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
	 * Returns the first ddm data provider instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (ddmDataProviderInstance != null) {
			return ddmDataProviderInstance;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchDataProviderInstanceException(sb.toString());
	}

	/**
	 * Returns the first ddm data provider instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm data provider instance, or <code>null</code> if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		List<DDMDataProviderInstance> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm data provider instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (ddmDataProviderInstance != null) {
			return ddmDataProviderInstance;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchDataProviderInstanceException(sb.toString());
	}

	/**
	 * Returns the last ddm data provider instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm data provider instance, or <code>null</code> if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<DDMDataProviderInstance> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm data provider instances before and after the current ddm data provider instance in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param dataProviderInstanceId the primary key of the current ddm data provider instance
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a ddm data provider instance with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstance[] findByUuid_C_PrevAndNext(
			long dataProviderInstanceId, String uuid, long companyId,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		uuid = Objects.toString(uuid, "");

		DDMDataProviderInstance ddmDataProviderInstance = findByPrimaryKey(
			dataProviderInstanceId);

		Session session = null;

		try {
			session = openSession();

			DDMDataProviderInstance[] array =
				new DDMDataProviderInstanceImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, ddmDataProviderInstance, uuid, companyId,
				orderByComparator, true);

			array[1] = ddmDataProviderInstance;

			array[2] = getByUuid_C_PrevAndNext(
				session, ddmDataProviderInstance, uuid, companyId,
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

	protected DDMDataProviderInstance getByUuid_C_PrevAndNext(
		Session session, DDMDataProviderInstance ddmDataProviderInstance,
		String uuid, long companyId,
		OrderByComparator<DDMDataProviderInstance> orderByComparator,
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

		sb.append(_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);

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
			sb.append(DDMDataProviderInstanceModelImpl.ORDER_BY_JPQL);
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
						ddmDataProviderInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMDataProviderInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm data provider instances where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (DDMDataProviderInstance ddmDataProviderInstance :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(ddmDataProviderInstance);
		}
	}

	/**
	 * Returns the number of ddm data provider instances where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddm data provider instances
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMDataProviderInstance.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_DDMDATAPROVIDERINSTANCE_WHERE);

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
		"ddmDataProviderInstance.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(ddmDataProviderInstance.uuid IS NULL OR ddmDataProviderInstance.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"ddmDataProviderInstance.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;
	private FinderPath _finderPathWithPaginationCountByGroupId;

	/**
	 * Returns all the ddm data provider instances where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm data provider instances where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @return the range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMDataProviderInstance.class)) {

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

			List<DDMDataProviderInstance> list = null;

			if (useFinderCache) {
				list = (List<DDMDataProviderInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMDataProviderInstance ddmDataProviderInstance :
							list) {

						if (groupId != ddmDataProviderInstance.getGroupId()) {
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

				sb.append(_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMDataProviderInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<DDMDataProviderInstance>)QueryUtil.list(
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
	 * Returns the first ddm data provider instance in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance findByGroupId_First(
			long groupId,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance = fetchByGroupId_First(
			groupId, orderByComparator);

		if (ddmDataProviderInstance != null) {
			return ddmDataProviderInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchDataProviderInstanceException(sb.toString());
	}

	/**
	 * Returns the first ddm data provider instance in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm data provider instance, or <code>null</code> if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchByGroupId_First(
		long groupId,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		List<DDMDataProviderInstance> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm data provider instance in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance findByGroupId_Last(
			long groupId,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (ddmDataProviderInstance != null) {
			return ddmDataProviderInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchDataProviderInstanceException(sb.toString());
	}

	/**
	 * Returns the last ddm data provider instance in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm data provider instance, or <code>null</code> if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchByGroupId_Last(
		long groupId,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<DDMDataProviderInstance> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm data provider instances before and after the current ddm data provider instance in the ordered set where groupId = &#63;.
	 *
	 * @param dataProviderInstanceId the primary key of the current ddm data provider instance
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a ddm data provider instance with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstance[] findByGroupId_PrevAndNext(
			long dataProviderInstanceId, long groupId,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance = findByPrimaryKey(
			dataProviderInstanceId);

		Session session = null;

		try {
			session = openSession();

			DDMDataProviderInstance[] array =
				new DDMDataProviderInstanceImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, ddmDataProviderInstance, groupId, orderByComparator,
				true);

			array[1] = ddmDataProviderInstance;

			array[2] = getByGroupId_PrevAndNext(
				session, ddmDataProviderInstance, groupId, orderByComparator,
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

	protected DDMDataProviderInstance getByGroupId_PrevAndNext(
		Session session, DDMDataProviderInstance ddmDataProviderInstance,
		long groupId,
		OrderByComparator<DDMDataProviderInstance> orderByComparator,
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

		sb.append(_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);

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
			sb.append(DDMDataProviderInstanceModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						ddmDataProviderInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMDataProviderInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm data provider instances that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching ddm data provider instances that the user has permission to view
	 */
	@Override
	public List<DDMDataProviderInstance> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm data provider instances that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @return the range of matching ddm data provider instances that the user has permission to view
	 */
	@Override
	public List<DDMDataProviderInstance> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm data provider instances that the user has permission to view
	 */
	@Override
	public List<DDMDataProviderInstance> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMDATAPROVIDERINSTANCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMDATAPROVIDERINSTANCE_NO_INLINE_DISTINCT_WHERE_2);
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
					DDMDataProviderInstanceModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMDataProviderInstanceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMDataProviderInstance.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DDMDataProviderInstanceImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DDMDataProviderInstanceImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<DDMDataProviderInstance>)QueryUtil.list(
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
	 * Returns the ddm data provider instances before and after the current ddm data provider instance in the ordered set of ddm data provider instances that the user has permission to view where groupId = &#63;.
	 *
	 * @param dataProviderInstanceId the primary key of the current ddm data provider instance
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a ddm data provider instance with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstance[] filterFindByGroupId_PrevAndNext(
			long dataProviderInstanceId, long groupId,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				dataProviderInstanceId, groupId, orderByComparator);
		}

		DDMDataProviderInstance ddmDataProviderInstance = findByPrimaryKey(
			dataProviderInstanceId);

		Session session = null;

		try {
			session = openSession();

			DDMDataProviderInstance[] array =
				new DDMDataProviderInstanceImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, ddmDataProviderInstance, groupId, orderByComparator,
				true);

			array[1] = ddmDataProviderInstance;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, ddmDataProviderInstance, groupId, orderByComparator,
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

	protected DDMDataProviderInstance filterGetByGroupId_PrevAndNext(
		Session session, DDMDataProviderInstance ddmDataProviderInstance,
		long groupId,
		OrderByComparator<DDMDataProviderInstance> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMDATAPROVIDERINSTANCE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_DDMDATAPROVIDERINSTANCE_NO_INLINE_DISTINCT_WHERE_2);
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
					DDMDataProviderInstanceModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMDataProviderInstanceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMDataProviderInstance.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(
				_FILTER_ENTITY_ALIAS, DDMDataProviderInstanceImpl.class);
		}
		else {
			sqlQuery.addEntity(
				_FILTER_ENTITY_TABLE, DDMDataProviderInstanceImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						ddmDataProviderInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMDataProviderInstance> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the ddm data provider instances that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching ddm data provider instances that the user has permission to view
	 */
	@Override
	public List<DDMDataProviderInstance> filterFindByGroupId(long[] groupIds) {
		return filterFindByGroupId(
			groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm data provider instances that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @return the range of matching ddm data provider instances that the user has permission to view
	 */
	@Override
	public List<DDMDataProviderInstance> filterFindByGroupId(
		long[] groupIds, int start, int end) {

		return filterFindByGroupId(groupIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm data provider instances that the user has permission to view
	 */
	@Override
	public List<DDMDataProviderInstance> filterFindByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_DDMDATAPROVIDERINSTANCE_NO_INLINE_DISTINCT_WHERE_1);
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
				_FILTER_SQL_SELECT_DDMDATAPROVIDERINSTANCE_NO_INLINE_DISTINCT_WHERE_2);
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
					DDMDataProviderInstanceModelImpl.
						ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(DDMDataProviderInstanceModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMDataProviderInstance.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupIds);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, DDMDataProviderInstanceImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, DDMDataProviderInstanceImpl.class);
			}

			return (List<DDMDataProviderInstance>)QueryUtil.list(
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
	 * Returns all the ddm data provider instances where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @return the matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByGroupId(long[] groupIds) {
		return findByGroupId(
			groupIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm data provider instances where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @return the range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByGroupId(
		long[] groupIds, int start, int end) {

		return findByGroupId(groupIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		return findByGroupId(groupIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator,
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
					DDMDataProviderInstance.class)) {

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

			List<DDMDataProviderInstance> list = null;

			if (useFinderCache) {
				list = (List<DDMDataProviderInstance>)finderCache.getResult(
					_finderPathWithPaginationFindByGroupId, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMDataProviderInstance ddmDataProviderInstance :
							list) {

						if (!ArrayUtil.contains(
								groupIds,
								ddmDataProviderInstance.getGroupId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);

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
					sb.append(DDMDataProviderInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DDMDataProviderInstance>)QueryUtil.list(
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
	 * Removes all the ddm data provider instances where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (DDMDataProviderInstance ddmDataProviderInstance :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmDataProviderInstance);
		}
	}

	/**
	 * Returns the number of ddm data provider instances where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm data provider instances
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMDataProviderInstance.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMDATAPROVIDERINSTANCE_WHERE);

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
	 * Returns the number of ddm data provider instances where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddm data provider instances
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
					DDMDataProviderInstance.class)) {

			Object[] finderArgs = new Object[] {StringUtil.merge(groupIds)};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByGroupId, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_DDMDATAPROVIDERINSTANCE_WHERE);

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
	 * Returns the number of ddm data provider instances that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm data provider instances that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMDataProviderInstance> ddmDataProviderInstances =
				findByGroupId(groupId);

			ddmDataProviderInstances = InlineSQLHelperUtil.filter(
				ddmDataProviderInstances, groupId);

			return ddmDataProviderInstances.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_DDMDATAPROVIDERINSTANCE_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), DDMDataProviderInstance.class.getName(),
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
	 * Returns the number of ddm data provider instances that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddm data provider instances that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long[] groupIds) {
		if (!InlineSQLHelperUtil.isEnabled(groupIds)) {
			return countByGroupId(groupIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<DDMDataProviderInstance> ddmDataProviderInstances =
				InlineSQLHelperUtil.filter(findByGroupId(groupIds), groupIds);

			return ddmDataProviderInstances.size();
		}

		if (groupIds == null) {
			groupIds = new long[0];
		}
		else if (groupIds.length > 1) {
			groupIds = ArrayUtil.sortedUnique(groupIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_DDMDATAPROVIDERINSTANCE_WHERE);

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
			sb.toString(), DDMDataProviderInstance.class.getName(),
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
		"ddmDataProviderInstance.groupId = ?";

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_7 =
		"ddmDataProviderInstance.groupId IN (";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the ddm data provider instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm data provider instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @return the range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMDataProviderInstance.class)) {

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

			List<DDMDataProviderInstance> list = null;

			if (useFinderCache) {
				list = (List<DDMDataProviderInstance>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (DDMDataProviderInstance ddmDataProviderInstance :
							list) {

						if (companyId !=
								ddmDataProviderInstance.getCompanyId()) {

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

				sb.append(_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(DDMDataProviderInstanceModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<DDMDataProviderInstance>)QueryUtil.list(
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
	 * Returns the first ddm data provider instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance findByCompanyId_First(
			long companyId,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance =
			fetchByCompanyId_First(companyId, orderByComparator);

		if (ddmDataProviderInstance != null) {
			return ddmDataProviderInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchDataProviderInstanceException(sb.toString());
	}

	/**
	 * Returns the first ddm data provider instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm data provider instance, or <code>null</code> if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchByCompanyId_First(
		long companyId,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		List<DDMDataProviderInstance> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last ddm data provider instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance findByCompanyId_Last(
			long companyId,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (ddmDataProviderInstance != null) {
			return ddmDataProviderInstance;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchDataProviderInstanceException(sb.toString());
	}

	/**
	 * Returns the last ddm data provider instance in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm data provider instance, or <code>null</code> if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<DDMDataProviderInstance> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the ddm data provider instances before and after the current ddm data provider instance in the ordered set where companyId = &#63;.
	 *
	 * @param dataProviderInstanceId the primary key of the current ddm data provider instance
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a ddm data provider instance with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstance[] findByCompanyId_PrevAndNext(
			long dataProviderInstanceId, long companyId,
			OrderByComparator<DDMDataProviderInstance> orderByComparator)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance = findByPrimaryKey(
			dataProviderInstanceId);

		Session session = null;

		try {
			session = openSession();

			DDMDataProviderInstance[] array =
				new DDMDataProviderInstanceImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, ddmDataProviderInstance, companyId, orderByComparator,
				true);

			array[1] = ddmDataProviderInstance;

			array[2] = getByCompanyId_PrevAndNext(
				session, ddmDataProviderInstance, companyId, orderByComparator,
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

	protected DDMDataProviderInstance getByCompanyId_PrevAndNext(
		Session session, DDMDataProviderInstance ddmDataProviderInstance,
		long companyId,
		OrderByComparator<DDMDataProviderInstance> orderByComparator,
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

		sb.append(_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE);

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
			sb.append(DDMDataProviderInstanceModelImpl.ORDER_BY_JPQL);
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
						ddmDataProviderInstance)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<DDMDataProviderInstance> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the ddm data provider instances where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (DDMDataProviderInstance ddmDataProviderInstance :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(ddmDataProviderInstance);
		}
	}

	/**
	 * Returns the number of ddm data provider instances where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching ddm data provider instances
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMDataProviderInstance.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_DDMDATAPROVIDERINSTANCE_WHERE);

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
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"ddmDataProviderInstance.companyId = ?";

	public DDMDataProviderInstancePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(DDMDataProviderInstance.class);

		setModelImplClass(DDMDataProviderInstanceImpl.class);
		setModelPKClass(long.class);

		setTable(DDMDataProviderInstanceTable.INSTANCE);
	}

	/**
	 * Caches the ddm data provider instance in the entity cache if it is enabled.
	 *
	 * @param ddmDataProviderInstance the ddm data provider instance
	 */
	@Override
	public void cacheResult(DDMDataProviderInstance ddmDataProviderInstance) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ddmDataProviderInstance.getCtCollectionId())) {

			entityCache.putResult(
				DDMDataProviderInstanceImpl.class,
				ddmDataProviderInstance.getPrimaryKey(),
				ddmDataProviderInstance);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					ddmDataProviderInstance.getUuid(),
					ddmDataProviderInstance.getGroupId()
				},
				ddmDataProviderInstance);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the ddm data provider instances in the entity cache if it is enabled.
	 *
	 * @param ddmDataProviderInstances the ddm data provider instances
	 */
	@Override
	public void cacheResult(
		List<DDMDataProviderInstance> ddmDataProviderInstances) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (ddmDataProviderInstances.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (DDMDataProviderInstance ddmDataProviderInstance :
				ddmDataProviderInstances) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ddmDataProviderInstance.getCtCollectionId())) {

				if (entityCache.getResult(
						DDMDataProviderInstanceImpl.class,
						ddmDataProviderInstance.getPrimaryKey()) == null) {

					cacheResult(ddmDataProviderInstance);
				}
			}
		}
	}

	/**
	 * Clears the cache for all ddm data provider instances.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(DDMDataProviderInstanceImpl.class);

		finderCache.clearCache(DDMDataProviderInstanceImpl.class);
	}

	/**
	 * Clears the cache for the ddm data provider instance.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DDMDataProviderInstance ddmDataProviderInstance) {
		entityCache.removeResult(
			DDMDataProviderInstanceImpl.class, ddmDataProviderInstance);
	}

	@Override
	public void clearCache(
		List<DDMDataProviderInstance> ddmDataProviderInstances) {

		for (DDMDataProviderInstance ddmDataProviderInstance :
				ddmDataProviderInstances) {

			entityCache.removeResult(
				DDMDataProviderInstanceImpl.class, ddmDataProviderInstance);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(DDMDataProviderInstanceImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				DDMDataProviderInstanceImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		DDMDataProviderInstanceModelImpl ddmDataProviderInstanceModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ddmDataProviderInstanceModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				ddmDataProviderInstanceModelImpl.getUuid(),
				ddmDataProviderInstanceModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args,
				ddmDataProviderInstanceModelImpl);
		}
	}

	/**
	 * Creates a new ddm data provider instance with the primary key. Does not add the ddm data provider instance to the database.
	 *
	 * @param dataProviderInstanceId the primary key for the new ddm data provider instance
	 * @return the new ddm data provider instance
	 */
	@Override
	public DDMDataProviderInstance create(long dataProviderInstanceId) {
		DDMDataProviderInstance ddmDataProviderInstance =
			new DDMDataProviderInstanceImpl();

		ddmDataProviderInstance.setNew(true);
		ddmDataProviderInstance.setPrimaryKey(dataProviderInstanceId);

		String uuid = PortalUUIDUtil.generate();

		ddmDataProviderInstance.setUuid(uuid);

		ddmDataProviderInstance.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ddmDataProviderInstance;
	}

	/**
	 * Removes the ddm data provider instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param dataProviderInstanceId the primary key of the ddm data provider instance
	 * @return the ddm data provider instance that was removed
	 * @throws NoSuchDataProviderInstanceException if a ddm data provider instance with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstance remove(long dataProviderInstanceId)
		throws NoSuchDataProviderInstanceException {

		return remove((Serializable)dataProviderInstanceId);
	}

	/**
	 * Removes the ddm data provider instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the ddm data provider instance
	 * @return the ddm data provider instance that was removed
	 * @throws NoSuchDataProviderInstanceException if a ddm data provider instance with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstance remove(Serializable primaryKey)
		throws NoSuchDataProviderInstanceException {

		Session session = null;

		try {
			session = openSession();

			DDMDataProviderInstance ddmDataProviderInstance =
				(DDMDataProviderInstance)session.get(
					DDMDataProviderInstanceImpl.class, primaryKey);

			if (ddmDataProviderInstance == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchDataProviderInstanceException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ddmDataProviderInstance);
		}
		catch (NoSuchDataProviderInstanceException noSuchEntityException) {
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
	protected DDMDataProviderInstance removeImpl(
		DDMDataProviderInstance ddmDataProviderInstance) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmDataProviderInstance)) {
				ddmDataProviderInstance = (DDMDataProviderInstance)session.get(
					DDMDataProviderInstanceImpl.class,
					ddmDataProviderInstance.getPrimaryKeyObj());
			}

			if ((ddmDataProviderInstance != null) &&
				ctPersistenceHelper.isRemove(ddmDataProviderInstance)) {

				session.delete(ddmDataProviderInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmDataProviderInstance != null) {
			clearCache(ddmDataProviderInstance);
		}

		return ddmDataProviderInstance;
	}

	@Override
	public DDMDataProviderInstance updateImpl(
		DDMDataProviderInstance ddmDataProviderInstance) {

		boolean isNew = ddmDataProviderInstance.isNew();

		if (!(ddmDataProviderInstance instanceof
				DDMDataProviderInstanceModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmDataProviderInstance.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmDataProviderInstance);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmDataProviderInstance proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMDataProviderInstance implementation " +
					ddmDataProviderInstance.getClass());
		}

		DDMDataProviderInstanceModelImpl ddmDataProviderInstanceModelImpl =
			(DDMDataProviderInstanceModelImpl)ddmDataProviderInstance;

		if (Validator.isNull(ddmDataProviderInstance.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ddmDataProviderInstance.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ddmDataProviderInstance.getCreateDate() == null)) {
			if (serviceContext == null) {
				ddmDataProviderInstance.setCreateDate(date);
			}
			else {
				ddmDataProviderInstance.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!ddmDataProviderInstanceModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ddmDataProviderInstance.setModifiedDate(date);
			}
			else {
				ddmDataProviderInstance.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmDataProviderInstance)) {
				if (!isNew) {
					session.evict(
						DDMDataProviderInstanceImpl.class,
						ddmDataProviderInstance.getPrimaryKeyObj());
				}

				session.save(ddmDataProviderInstance);
			}
			else {
				ddmDataProviderInstance =
					(DDMDataProviderInstance)session.merge(
						ddmDataProviderInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			DDMDataProviderInstanceImpl.class, ddmDataProviderInstanceModelImpl,
			false, true);

		cacheUniqueFindersCache(ddmDataProviderInstanceModelImpl);

		if (isNew) {
			ddmDataProviderInstance.setNew(false);
		}

		ddmDataProviderInstance.resetOriginalValues();

		return ddmDataProviderInstance;
	}

	/**
	 * Returns the ddm data provider instance with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the ddm data provider instance
	 * @return the ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a ddm data provider instance with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstance findByPrimaryKey(Serializable primaryKey)
		throws NoSuchDataProviderInstanceException {

		DDMDataProviderInstance ddmDataProviderInstance = fetchByPrimaryKey(
			primaryKey);

		if (ddmDataProviderInstance == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchDataProviderInstanceException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ddmDataProviderInstance;
	}

	/**
	 * Returns the ddm data provider instance with the primary key or throws a <code>NoSuchDataProviderInstanceException</code> if it could not be found.
	 *
	 * @param dataProviderInstanceId the primary key of the ddm data provider instance
	 * @return the ddm data provider instance
	 * @throws NoSuchDataProviderInstanceException if a ddm data provider instance with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstance findByPrimaryKey(long dataProviderInstanceId)
		throws NoSuchDataProviderInstanceException {

		return findByPrimaryKey((Serializable)dataProviderInstanceId);
	}

	/**
	 * Returns the ddm data provider instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the ddm data provider instance
	 * @return the ddm data provider instance, or <code>null</code> if a ddm data provider instance with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				DDMDataProviderInstance.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		DDMDataProviderInstance ddmDataProviderInstance =
			(DDMDataProviderInstance)entityCache.getResult(
				DDMDataProviderInstanceImpl.class, primaryKey);

		if (ddmDataProviderInstance != null) {
			return ddmDataProviderInstance;
		}

		Session session = null;

		try {
			session = openSession();

			ddmDataProviderInstance = (DDMDataProviderInstance)session.get(
				DDMDataProviderInstanceImpl.class, primaryKey);

			if (ddmDataProviderInstance != null) {
				cacheResult(ddmDataProviderInstance);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return ddmDataProviderInstance;
	}

	/**
	 * Returns the ddm data provider instance with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param dataProviderInstanceId the primary key of the ddm data provider instance
	 * @return the ddm data provider instance, or <code>null</code> if a ddm data provider instance with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchByPrimaryKey(
		long dataProviderInstanceId) {

		return fetchByPrimaryKey((Serializable)dataProviderInstanceId);
	}

	@Override
	public Map<Serializable, DDMDataProviderInstance> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(
				DDMDataProviderInstance.class)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, DDMDataProviderInstance> map =
			new HashMap<Serializable, DDMDataProviderInstance>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			DDMDataProviderInstance ddmDataProviderInstance = fetchByPrimaryKey(
				primaryKey);

			if (ddmDataProviderInstance != null) {
				map.put(primaryKey, ddmDataProviderInstance);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						DDMDataProviderInstance.class, primaryKey)) {

				DDMDataProviderInstance ddmDataProviderInstance =
					(DDMDataProviderInstance)entityCache.getResult(
						DDMDataProviderInstanceImpl.class, primaryKey);

				if (ddmDataProviderInstance == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, ddmDataProviderInstance);
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

			for (DDMDataProviderInstance ddmDataProviderInstance :
					(List<DDMDataProviderInstance>)query.list()) {

				map.put(
					ddmDataProviderInstance.getPrimaryKeyObj(),
					ddmDataProviderInstance);

				cacheResult(ddmDataProviderInstance);
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
	 * Returns all the ddm data provider instances.
	 *
	 * @return the ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the ddm data provider instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @return the range of ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findAll(
		int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the ddm data provider instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of ddm data provider instances
	 */
	@Override
	public List<DDMDataProviderInstance> findAll(
		int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMDataProviderInstance.class)) {

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

			List<DDMDataProviderInstance> list = null;

			if (useFinderCache) {
				list = (List<DDMDataProviderInstance>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_DDMDATAPROVIDERINSTANCE);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_DDMDATAPROVIDERINSTANCE;

					sql = sql.concat(
						DDMDataProviderInstanceModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<DDMDataProviderInstance>)QueryUtil.list(
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
	 * Removes all the ddm data provider instances from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DDMDataProviderInstance ddmDataProviderInstance : findAll()) {
			remove(ddmDataProviderInstance);
		}
	}

	/**
	 * Returns the number of ddm data provider instances.
	 *
	 * @return the number of ddm data provider instances
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					DDMDataProviderInstance.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_DDMDATAPROVIDERINSTANCE);

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
		return "dataProviderInstanceId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMDATAPROVIDERINSTANCE;
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
		return DDMDataProviderInstanceModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMDataProviderInstance";
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
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("definition");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("dataProviderInstanceId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});
	}

	/**
	 * Initializes the ddm data provider instance persistence.
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

		DDMDataProviderInstanceUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMDataProviderInstanceUtil.setPersistence(null);

		entityCache.removeCache(DDMDataProviderInstanceImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_DDMDATAPROVIDERINSTANCE =
		"SELECT ddmDataProviderInstance FROM DDMDataProviderInstance ddmDataProviderInstance";

	private static final String _SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE =
		"SELECT ddmDataProviderInstance FROM DDMDataProviderInstance ddmDataProviderInstance WHERE ";

	private static final String _SQL_COUNT_DDMDATAPROVIDERINSTANCE =
		"SELECT COUNT(ddmDataProviderInstance) FROM DDMDataProviderInstance ddmDataProviderInstance";

	private static final String _SQL_COUNT_DDMDATAPROVIDERINSTANCE_WHERE =
		"SELECT COUNT(ddmDataProviderInstance) FROM DDMDataProviderInstance ddmDataProviderInstance WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"ddmDataProviderInstance.dataProviderInstanceId";

	private static final String
		_FILTER_SQL_SELECT_DDMDATAPROVIDERINSTANCE_WHERE =
			"SELECT DISTINCT {ddmDataProviderInstance.*} FROM DDMDataProviderInstance ddmDataProviderInstance WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DDMDATAPROVIDERINSTANCE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {DDMDataProviderInstance.*} FROM (SELECT DISTINCT ddmDataProviderInstance.dataProviderInstanceId FROM DDMDataProviderInstance ddmDataProviderInstance WHERE ";

	private static final String
		_FILTER_SQL_SELECT_DDMDATAPROVIDERINSTANCE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN DDMDataProviderInstance ON TEMP_TABLE.dataProviderInstanceId = DDMDataProviderInstance.dataProviderInstanceId";

	private static final String
		_FILTER_SQL_COUNT_DDMDATAPROVIDERINSTANCE_WHERE =
			"SELECT COUNT(DISTINCT ddmDataProviderInstance.dataProviderInstanceId) AS COUNT_VALUE FROM DDMDataProviderInstance ddmDataProviderInstance WHERE ";

	private static final String _FILTER_ENTITY_ALIAS =
		"ddmDataProviderInstance";

	private static final String _FILTER_ENTITY_TABLE =
		"DDMDataProviderInstance";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"ddmDataProviderInstance.";

	private static final String _ORDER_BY_ENTITY_TABLE =
		"DDMDataProviderInstance.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No DDMDataProviderInstance exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMDataProviderInstance exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMDataProviderInstancePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}