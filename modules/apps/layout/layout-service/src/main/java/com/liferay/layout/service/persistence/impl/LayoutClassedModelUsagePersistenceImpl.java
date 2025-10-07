/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.persistence.impl;

import com.liferay.layout.exception.NoSuchLayoutClassedModelUsageException;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.model.LayoutClassedModelUsageTable;
import com.liferay.layout.model.impl.LayoutClassedModelUsageImpl;
import com.liferay.layout.model.impl.LayoutClassedModelUsageModelImpl;
import com.liferay.layout.service.persistence.LayoutClassedModelUsagePersistence;
import com.liferay.layout.service.persistence.LayoutClassedModelUsageUtil;
import com.liferay.layout.service.persistence.impl.constants.LayoutPersistenceConstants;
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
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
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
 * The persistence implementation for the layout classed model usage service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutClassedModelUsagePersistence.class)
public class LayoutClassedModelUsagePersistenceImpl
	extends BasePersistenceImpl<LayoutClassedModelUsage>
	implements LayoutClassedModelUsagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutClassedModelUsageUtil</code> to access the layout classed model usage persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutClassedModelUsageImpl.class.getName();

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
	 * Returns all the layout classed model usages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout classed model usages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

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

			List<LayoutClassedModelUsage> list = null;

			if (useFinderCache) {
				list = (List<LayoutClassedModelUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutClassedModelUsage layoutClassedModelUsage :
							list) {

						if (!uuid.equals(layoutClassedModelUsage.getUuid())) {
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

				sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

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
					sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
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

					list = (List<LayoutClassedModelUsage>)QueryUtil.list(
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
	 * Returns the first layout classed model usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByUuid_First(
			String uuid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByUuid_First(
			uuid, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		List<LayoutClassedModelUsage> list = findByUuid(
			uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByUuid_Last(
			String uuid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByUuid_Last(
			uuid, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByUuid_Last(
		String uuid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<LayoutClassedModelUsage> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where uuid = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage[] findByUuid_PrevAndNext(
			long layoutClassedModelUsageId, String uuid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		uuid = Objects.toString(uuid, "");

		LayoutClassedModelUsage layoutClassedModelUsage = findByPrimaryKey(
			layoutClassedModelUsageId);

		Session session = null;

		try {
			session = openSession();

			LayoutClassedModelUsage[] array =
				new LayoutClassedModelUsageImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, layoutClassedModelUsage, uuid, orderByComparator,
				true);

			array[1] = layoutClassedModelUsage;

			array[2] = getByUuid_PrevAndNext(
				session, layoutClassedModelUsage, uuid, orderByComparator,
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

	protected LayoutClassedModelUsage getByUuid_PrevAndNext(
		Session session, LayoutClassedModelUsage layoutClassedModelUsage,
		String uuid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

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
			sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
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
						layoutClassedModelUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutClassedModelUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout classed model usages where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (LayoutClassedModelUsage layoutClassedModelUsage :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layoutClassedModelUsage);
		}
	}

	/**
	 * Returns the number of layout classed model usages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE);

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
		"layoutClassedModelUsage.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(layoutClassedModelUsage.uuid IS NULL OR layoutClassedModelUsage.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the layout classed model usage where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutClassedModelUsageException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByUUID_G(String uuid, long groupId)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByUUID_G(
			uuid, groupId);

		if (layoutClassedModelUsage == null) {
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

			throw new NoSuchLayoutClassedModelUsageException(sb.toString());
		}

		return layoutClassedModelUsage;
	}

	/**
	 * Returns the layout classed model usage where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the layout classed model usage where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

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

			if (result instanceof LayoutClassedModelUsage) {
				LayoutClassedModelUsage layoutClassedModelUsage =
					(LayoutClassedModelUsage)result;

				if (!Objects.equals(uuid, layoutClassedModelUsage.getUuid()) ||
					(groupId != layoutClassedModelUsage.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

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

					List<LayoutClassedModelUsage> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						LayoutClassedModelUsage layoutClassedModelUsage =
							list.get(0);

						result = layoutClassedModelUsage;

						cacheResult(layoutClassedModelUsage);
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
				return (LayoutClassedModelUsage)result;
			}
		}
	}

	/**
	 * Removes the layout classed model usage where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout classed model usage that was removed
	 */
	@Override
	public LayoutClassedModelUsage removeByUUID_G(String uuid, long groupId)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = findByUUID_G(
			uuid, groupId);

		return remove(layoutClassedModelUsage);
	}

	/**
	 * Returns the number of layout classed model usages where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		LayoutClassedModelUsage layoutClassedModelUsage = fetchByUUID_G(
			uuid, groupId);

		if (layoutClassedModelUsage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"layoutClassedModelUsage.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(layoutClassedModelUsage.uuid IS NULL OR layoutClassedModelUsage.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"layoutClassedModelUsage.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the layout classed model usages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout classed model usages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

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

			List<LayoutClassedModelUsage> list = null;

			if (useFinderCache) {
				list = (List<LayoutClassedModelUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutClassedModelUsage layoutClassedModelUsage :
							list) {

						if (!uuid.equals(layoutClassedModelUsage.getUuid()) ||
							(companyId !=
								layoutClassedModelUsage.getCompanyId())) {

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

				sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

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
					sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
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

					list = (List<LayoutClassedModelUsage>)QueryUtil.list(
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
	 * Returns the first layout classed model usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		List<LayoutClassedModelUsage> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<LayoutClassedModelUsage> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage[] findByUuid_C_PrevAndNext(
			long layoutClassedModelUsageId, String uuid, long companyId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		uuid = Objects.toString(uuid, "");

		LayoutClassedModelUsage layoutClassedModelUsage = findByPrimaryKey(
			layoutClassedModelUsageId);

		Session session = null;

		try {
			session = openSession();

			LayoutClassedModelUsage[] array =
				new LayoutClassedModelUsageImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, layoutClassedModelUsage, uuid, companyId,
				orderByComparator, true);

			array[1] = layoutClassedModelUsage;

			array[2] = getByUuid_C_PrevAndNext(
				session, layoutClassedModelUsage, uuid, companyId,
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

	protected LayoutClassedModelUsage getByUuid_C_PrevAndNext(
		Session session, LayoutClassedModelUsage layoutClassedModelUsage,
		String uuid, long companyId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

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
			sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
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
						layoutClassedModelUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutClassedModelUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout classed model usages where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (LayoutClassedModelUsage layoutClassedModelUsage :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layoutClassedModelUsage);
		}
	}

	/**
	 * Returns the number of layout classed model usages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE);

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
		"layoutClassedModelUsage.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(layoutClassedModelUsage.uuid IS NULL OR layoutClassedModelUsage.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"layoutClassedModelUsage.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByPlid;
	private FinderPath _finderPathWithoutPaginationFindByPlid;
	private FinderPath _finderPathCountByPlid;

	/**
	 * Returns all the layout classed model usages where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByPlid(long plid) {
		return findByPlid(plid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout classed model usages where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByPlid(
		long plid, int start, int end) {

		return findByPlid(plid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return findByPlid(plid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByPlid;
					finderArgs = new Object[] {plid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByPlid;
				finderArgs = new Object[] {plid, start, end, orderByComparator};
			}

			List<LayoutClassedModelUsage> list = null;

			if (useFinderCache) {
				list = (List<LayoutClassedModelUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutClassedModelUsage layoutClassedModelUsage :
							list) {

						if (plid != layoutClassedModelUsage.getPlid()) {
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

				sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_PLID_PLID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(plid);

					list = (List<LayoutClassedModelUsage>)QueryUtil.list(
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
	 * Returns the first layout classed model usage in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByPlid_First(
			long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByPlid_First(
			plid, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("plid=");
		sb.append(plid);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByPlid_First(
		long plid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		List<LayoutClassedModelUsage> list = findByPlid(
			plid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByPlid_Last(
			long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByPlid_Last(
			plid, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("plid=");
		sb.append(plid);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByPlid_Last(
		long plid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		int count = countByPlid(plid);

		if (count == 0) {
			return null;
		}

		List<LayoutClassedModelUsage> list = findByPlid(
			plid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where plid = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage[] findByPlid_PrevAndNext(
			long layoutClassedModelUsageId, long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = findByPrimaryKey(
			layoutClassedModelUsageId);

		Session session = null;

		try {
			session = openSession();

			LayoutClassedModelUsage[] array =
				new LayoutClassedModelUsageImpl[3];

			array[0] = getByPlid_PrevAndNext(
				session, layoutClassedModelUsage, plid, orderByComparator,
				true);

			array[1] = layoutClassedModelUsage;

			array[2] = getByPlid_PrevAndNext(
				session, layoutClassedModelUsage, plid, orderByComparator,
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

	protected LayoutClassedModelUsage getByPlid_PrevAndNext(
		Session session, LayoutClassedModelUsage layoutClassedModelUsage,
		long plid, OrderByComparator<LayoutClassedModelUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_PLID_PLID_2);

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
			sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(plid);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutClassedModelUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutClassedModelUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout classed model usages where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	@Override
	public void removeByPlid(long plid) {
		for (LayoutClassedModelUsage layoutClassedModelUsage :
				findByPlid(plid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layoutClassedModelUsage);
		}
	}

	/**
	 * Returns the number of layout classed model usages where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByPlid(long plid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			FinderPath finderPath = _finderPathCountByPlid;

			Object[] finderArgs = new Object[] {plid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_PLID_PLID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(plid);

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

	private static final String _FINDER_COLUMN_PLID_PLID_2 =
		"layoutClassedModelUsage.plid = ? AND layoutClassedModelUsage.containerKey IS NOT NULL";

	private FinderPath _finderPathWithPaginationFindByC_CN;
	private FinderPath _finderPathWithoutPaginationFindByC_CN;
	private FinderPath _finderPathCountByC_CN;

	/**
	 * Returns all the layout classed model usages where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CN(
		long companyId, long classNameId) {

		return findByC_CN(
			companyId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout classed model usages where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CN(
		long companyId, long classNameId, int start, int end) {

		return findByC_CN(companyId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return findByC_CN(
			companyId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_CN;
					finderArgs = new Object[] {companyId, classNameId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_CN;
				finderArgs = new Object[] {
					companyId, classNameId, start, end, orderByComparator
				};
			}

			List<LayoutClassedModelUsage> list = null;

			if (useFinderCache) {
				list = (List<LayoutClassedModelUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutClassedModelUsage layoutClassedModelUsage :
							list) {

						if ((companyId !=
								layoutClassedModelUsage.getCompanyId()) ||
							(classNameId !=
								layoutClassedModelUsage.getClassNameId())) {

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

				sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_CN_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_CN_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					list = (List<LayoutClassedModelUsage>)QueryUtil.list(
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
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByC_CN_First(
			companyId, classNameId, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		List<LayoutClassedModelUsage> list = findByC_CN(
			companyId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByC_CN_Last(
			long companyId, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByC_CN_Last(
			companyId, classNameId, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByC_CN_Last(
		long companyId, long classNameId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		int count = countByC_CN(companyId, classNameId);

		if (count == 0) {
			return null;
		}

		List<LayoutClassedModelUsage> list = findByC_CN(
			companyId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage[] findByC_CN_PrevAndNext(
			long layoutClassedModelUsageId, long companyId, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = findByPrimaryKey(
			layoutClassedModelUsageId);

		Session session = null;

		try {
			session = openSession();

			LayoutClassedModelUsage[] array =
				new LayoutClassedModelUsageImpl[3];

			array[0] = getByC_CN_PrevAndNext(
				session, layoutClassedModelUsage, companyId, classNameId,
				orderByComparator, true);

			array[1] = layoutClassedModelUsage;

			array[2] = getByC_CN_PrevAndNext(
				session, layoutClassedModelUsage, companyId, classNameId,
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

	protected LayoutClassedModelUsage getByC_CN_PrevAndNext(
		Session session, LayoutClassedModelUsage layoutClassedModelUsage,
		long companyId, long classNameId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_C_CN_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_CN_CLASSNAMEID_2);

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
			sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
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
						layoutClassedModelUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutClassedModelUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout classed model usages where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_CN(long companyId, long classNameId) {
		for (LayoutClassedModelUsage layoutClassedModelUsage :
				findByC_CN(
					companyId, classNameId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layoutClassedModelUsage);
		}
	}

	/**
	 * Returns the number of layout classed model usages where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByC_CN(long companyId, long classNameId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			FinderPath finderPath = _finderPathCountByC_CN;

			Object[] finderArgs = new Object[] {companyId, classNameId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_CN_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_CN_CLASSNAMEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

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

	private static final String _FINDER_COLUMN_C_CN_COMPANYID_2 =
		"layoutClassedModelUsage.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_CN_CLASSNAMEID_2 =
		"layoutClassedModelUsage.classNameId = ? AND layoutClassedModelUsage.containerKey IS NOT NULL";

	private FinderPath _finderPathWithPaginationFindByCN_CPK;
	private FinderPath _finderPathWithoutPaginationFindByCN_CPK;
	private FinderPath _finderPathCountByCN_CPK;

	/**
	 * Returns all the layout classed model usages where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCN_CPK(
		long classNameId, long classPK) {

		return findByCN_CPK(
			classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout classed model usages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCN_CPK(
		long classNameId, long classPK, int start, int end) {

		return findByCN_CPK(classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return findByCN_CPK(
			classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByCN_CPK;
					finderArgs = new Object[] {classNameId, classPK};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCN_CPK;
				finderArgs = new Object[] {
					classNameId, classPK, start, end, orderByComparator
				};
			}

			List<LayoutClassedModelUsage> list = null;

			if (useFinderCache) {
				list = (List<LayoutClassedModelUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutClassedModelUsage layoutClassedModelUsage :
							list) {

						if ((classNameId !=
								layoutClassedModelUsage.getClassNameId()) ||
							(classPK != layoutClassedModelUsage.getClassPK())) {

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

				sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_CN_CPK_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_CN_CPK_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					list = (List<LayoutClassedModelUsage>)QueryUtil.list(
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
	 * Returns the first layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByCN_CPK_First(
			long classNameId, long classPK,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByCN_CPK_First(
			classNameId, classPK, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByCN_CPK_First(
		long classNameId, long classPK,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		List<LayoutClassedModelUsage> list = findByCN_CPK(
			classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByCN_CPK_Last(
			long classNameId, long classPK,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByCN_CPK_Last(
			classNameId, classPK, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByCN_CPK_Last(
		long classNameId, long classPK,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		int count = countByCN_CPK(classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<LayoutClassedModelUsage> list = findByCN_CPK(
			classNameId, classPK, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage[] findByCN_CPK_PrevAndNext(
			long layoutClassedModelUsageId, long classNameId, long classPK,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = findByPrimaryKey(
			layoutClassedModelUsageId);

		Session session = null;

		try {
			session = openSession();

			LayoutClassedModelUsage[] array =
				new LayoutClassedModelUsageImpl[3];

			array[0] = getByCN_CPK_PrevAndNext(
				session, layoutClassedModelUsage, classNameId, classPK,
				orderByComparator, true);

			array[1] = layoutClassedModelUsage;

			array[2] = getByCN_CPK_PrevAndNext(
				session, layoutClassedModelUsage, classNameId, classPK,
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

	protected LayoutClassedModelUsage getByCN_CPK_PrevAndNext(
		Session session, LayoutClassedModelUsage layoutClassedModelUsage,
		long classNameId, long classPK,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_CN_CPK_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_CN_CPK_CLASSPK_2);

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
			sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutClassedModelUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutClassedModelUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout classed model usages where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByCN_CPK(long classNameId, long classPK) {
		for (LayoutClassedModelUsage layoutClassedModelUsage :
				findByCN_CPK(
					classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layoutClassedModelUsage);
		}
	}

	/**
	 * Returns the number of layout classed model usages where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByCN_CPK(long classNameId, long classPK) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			FinderPath finderPath = _finderPathCountByCN_CPK;

			Object[] finderArgs = new Object[] {classNameId, classPK};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_CN_CPK_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_CN_CPK_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

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

	private static final String _FINDER_COLUMN_CN_CPK_CLASSNAMEID_2 =
		"layoutClassedModelUsage.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_CN_CPK_CLASSPK_2 =
		"layoutClassedModelUsage.classPK = ? AND layoutClassedModelUsage.containerKey IS NOT NULL";

	private FinderPath _finderPathWithPaginationFindByC_CERC_CN;
	private FinderPath _finderPathWithoutPaginationFindByC_CERC_CN;
	private FinderPath _finderPathCountByC_CERC_CN;

	/**
	 * Returns all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId) {

		return findByC_CERC_CN(
			companyId, classExternalReferenceCode, classNameId,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId,
		int start, int end) {

		return findByC_CERC_CN(
			companyId, classExternalReferenceCode, classNameId, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId,
		int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return findByC_CERC_CN(
			companyId, classExternalReferenceCode, classNameId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId,
		int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			classExternalReferenceCode = Objects.toString(
				classExternalReferenceCode, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_CERC_CN;
					finderArgs = new Object[] {
						companyId, classExternalReferenceCode, classNameId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_CERC_CN;
				finderArgs = new Object[] {
					companyId, classExternalReferenceCode, classNameId, start,
					end, orderByComparator
				};
			}

			List<LayoutClassedModelUsage> list = null;

			if (useFinderCache) {
				list = (List<LayoutClassedModelUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutClassedModelUsage layoutClassedModelUsage :
							list) {

						if ((companyId !=
								layoutClassedModelUsage.getCompanyId()) ||
							!classExternalReferenceCode.equals(
								layoutClassedModelUsage.
									getClassExternalReferenceCode()) ||
							(classNameId !=
								layoutClassedModelUsage.getClassNameId())) {

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

				sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_CERC_CN_COMPANYID_2);

				boolean bindClassExternalReferenceCode = false;

				if (classExternalReferenceCode.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_C_CERC_CN_CLASSEXTERNALREFERENCECODE_3);
				}
				else {
					bindClassExternalReferenceCode = true;

					sb.append(
						_FINDER_COLUMN_C_CERC_CN_CLASSEXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_C_CERC_CN_CLASSNAMEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindClassExternalReferenceCode) {
						queryPos.add(classExternalReferenceCode);
					}

					queryPos.add(classNameId);

					list = (List<LayoutClassedModelUsage>)QueryUtil.list(
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
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByC_CERC_CN_First(
			long companyId, String classExternalReferenceCode, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage =
			fetchByC_CERC_CN_First(
				companyId, classExternalReferenceCode, classNameId,
				orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classExternalReferenceCode=");
		sb.append(classExternalReferenceCode);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByC_CERC_CN_First(
		long companyId, String classExternalReferenceCode, long classNameId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		List<LayoutClassedModelUsage> list = findByC_CERC_CN(
			companyId, classExternalReferenceCode, classNameId, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByC_CERC_CN_Last(
			long companyId, String classExternalReferenceCode, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByC_CERC_CN_Last(
			companyId, classExternalReferenceCode, classNameId,
			orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classExternalReferenceCode=");
		sb.append(classExternalReferenceCode);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByC_CERC_CN_Last(
		long companyId, String classExternalReferenceCode, long classNameId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		int count = countByC_CERC_CN(
			companyId, classExternalReferenceCode, classNameId);

		if (count == 0) {
			return null;
		}

		List<LayoutClassedModelUsage> list = findByC_CERC_CN(
			companyId, classExternalReferenceCode, classNameId, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage[] findByC_CERC_CN_PrevAndNext(
			long layoutClassedModelUsageId, long companyId,
			String classExternalReferenceCode, long classNameId,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		classExternalReferenceCode = Objects.toString(
			classExternalReferenceCode, "");

		LayoutClassedModelUsage layoutClassedModelUsage = findByPrimaryKey(
			layoutClassedModelUsageId);

		Session session = null;

		try {
			session = openSession();

			LayoutClassedModelUsage[] array =
				new LayoutClassedModelUsageImpl[3];

			array[0] = getByC_CERC_CN_PrevAndNext(
				session, layoutClassedModelUsage, companyId,
				classExternalReferenceCode, classNameId, orderByComparator,
				true);

			array[1] = layoutClassedModelUsage;

			array[2] = getByC_CERC_CN_PrevAndNext(
				session, layoutClassedModelUsage, companyId,
				classExternalReferenceCode, classNameId, orderByComparator,
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

	protected LayoutClassedModelUsage getByC_CERC_CN_PrevAndNext(
		Session session, LayoutClassedModelUsage layoutClassedModelUsage,
		long companyId, String classExternalReferenceCode, long classNameId,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_C_CERC_CN_COMPANYID_2);

		boolean bindClassExternalReferenceCode = false;

		if (classExternalReferenceCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_CERC_CN_CLASSEXTERNALREFERENCECODE_3);
		}
		else {
			bindClassExternalReferenceCode = true;

			sb.append(_FINDER_COLUMN_C_CERC_CN_CLASSEXTERNALREFERENCECODE_2);
		}

		sb.append(_FINDER_COLUMN_C_CERC_CN_CLASSNAMEID_2);

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
			sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindClassExternalReferenceCode) {
			queryPos.add(classExternalReferenceCode);
		}

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutClassedModelUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutClassedModelUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId) {

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				findByC_CERC_CN(
					companyId, classExternalReferenceCode, classNameId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layoutClassedModelUsage);
		}
	}

	/**
	 * Returns the number of layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByC_CERC_CN(
		long companyId, String classExternalReferenceCode, long classNameId) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			classExternalReferenceCode = Objects.toString(
				classExternalReferenceCode, "");

			FinderPath finderPath = _finderPathCountByC_CERC_CN;

			Object[] finderArgs = new Object[] {
				companyId, classExternalReferenceCode, classNameId
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_CERC_CN_COMPANYID_2);

				boolean bindClassExternalReferenceCode = false;

				if (classExternalReferenceCode.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_C_CERC_CN_CLASSEXTERNALREFERENCECODE_3);
				}
				else {
					bindClassExternalReferenceCode = true;

					sb.append(
						_FINDER_COLUMN_C_CERC_CN_CLASSEXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_C_CERC_CN_CLASSNAMEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindClassExternalReferenceCode) {
						queryPos.add(classExternalReferenceCode);
					}

					queryPos.add(classNameId);

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

	private static final String _FINDER_COLUMN_C_CERC_CN_COMPANYID_2 =
		"layoutClassedModelUsage.companyId = ? AND ";

	private static final String
		_FINDER_COLUMN_C_CERC_CN_CLASSEXTERNALREFERENCECODE_2 =
			"layoutClassedModelUsage.classExternalReferenceCode = ? AND ";

	private static final String
		_FINDER_COLUMN_C_CERC_CN_CLASSEXTERNALREFERENCECODE_3 =
			"(layoutClassedModelUsage.classExternalReferenceCode IS NULL OR layoutClassedModelUsage.classExternalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_C_CERC_CN_CLASSNAMEID_2 =
		"layoutClassedModelUsage.classNameId = ? AND layoutClassedModelUsage.containerKey IS NOT NULL";

	private FinderPath _finderPathWithPaginationFindByC_CN_CT;
	private FinderPath _finderPathWithoutPaginationFindByC_CN_CT;
	private FinderPath _finderPathCountByC_CN_CT;

	/**
	 * Returns all the layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @return the matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CN_CT(
		long companyId, long classNameId, long containerType) {

		return findByC_CN_CT(
			companyId, classNameId, containerType, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CN_CT(
		long companyId, long classNameId, long containerType, int start,
		int end) {

		return findByC_CN_CT(
			companyId, classNameId, containerType, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CN_CT(
		long companyId, long classNameId, long containerType, int start,
		int end, OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return findByC_CN_CT(
			companyId, classNameId, containerType, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CN_CT(
		long companyId, long classNameId, long containerType, int start,
		int end, OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_CN_CT;
					finderArgs = new Object[] {
						companyId, classNameId, containerType
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_CN_CT;
				finderArgs = new Object[] {
					companyId, classNameId, containerType, start, end,
					orderByComparator
				};
			}

			List<LayoutClassedModelUsage> list = null;

			if (useFinderCache) {
				list = (List<LayoutClassedModelUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutClassedModelUsage layoutClassedModelUsage :
							list) {

						if ((companyId !=
								layoutClassedModelUsage.getCompanyId()) ||
							(classNameId !=
								layoutClassedModelUsage.getClassNameId()) ||
							(containerType !=
								layoutClassedModelUsage.getContainerType())) {

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

				sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_CN_CT_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_CN_CT_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_CN_CT_CONTAINERTYPE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					queryPos.add(containerType);

					list = (List<LayoutClassedModelUsage>)QueryUtil.list(
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
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByC_CN_CT_First(
			long companyId, long classNameId, long containerType,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByC_CN_CT_First(
			companyId, classNameId, containerType, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", containerType=");
		sb.append(containerType);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByC_CN_CT_First(
		long companyId, long classNameId, long containerType,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		List<LayoutClassedModelUsage> list = findByC_CN_CT(
			companyId, classNameId, containerType, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByC_CN_CT_Last(
			long companyId, long classNameId, long containerType,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByC_CN_CT_Last(
			companyId, classNameId, containerType, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", containerType=");
		sb.append(containerType);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByC_CN_CT_Last(
		long companyId, long classNameId, long containerType,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		int count = countByC_CN_CT(companyId, classNameId, containerType);

		if (count == 0) {
			return null;
		}

		List<LayoutClassedModelUsage> list = findByC_CN_CT(
			companyId, classNameId, containerType, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage[] findByC_CN_CT_PrevAndNext(
			long layoutClassedModelUsageId, long companyId, long classNameId,
			long containerType,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = findByPrimaryKey(
			layoutClassedModelUsageId);

		Session session = null;

		try {
			session = openSession();

			LayoutClassedModelUsage[] array =
				new LayoutClassedModelUsageImpl[3];

			array[0] = getByC_CN_CT_PrevAndNext(
				session, layoutClassedModelUsage, companyId, classNameId,
				containerType, orderByComparator, true);

			array[1] = layoutClassedModelUsage;

			array[2] = getByC_CN_CT_PrevAndNext(
				session, layoutClassedModelUsage, companyId, classNameId,
				containerType, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected LayoutClassedModelUsage getByC_CN_CT_PrevAndNext(
		Session session, LayoutClassedModelUsage layoutClassedModelUsage,
		long companyId, long classNameId, long containerType,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_C_CN_CT_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_CN_CT_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_CN_CT_CONTAINERTYPE_2);

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
			sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(classNameId);

		queryPos.add(containerType);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutClassedModelUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutClassedModelUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 */
	@Override
	public void removeByC_CN_CT(
		long companyId, long classNameId, long containerType) {

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				findByC_CN_CT(
					companyId, classNameId, containerType, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layoutClassedModelUsage);
		}
	}

	/**
	 * Returns the number of layout classed model usages where companyId = &#63; and classNameId = &#63; and containerType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param containerType the container type
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByC_CN_CT(
		long companyId, long classNameId, long containerType) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			FinderPath finderPath = _finderPathCountByC_CN_CT;

			Object[] finderArgs = new Object[] {
				companyId, classNameId, containerType
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_CN_CT_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_CN_CT_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_CN_CT_CONTAINERTYPE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(classNameId);

					queryPos.add(containerType);

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

	private static final String _FINDER_COLUMN_C_CN_CT_COMPANYID_2 =
		"layoutClassedModelUsage.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_CN_CT_CLASSNAMEID_2 =
		"layoutClassedModelUsage.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_CN_CT_CONTAINERTYPE_2 =
		"layoutClassedModelUsage.containerType = ? AND layoutClassedModelUsage.containerKey IS NOT NULL";

	private FinderPath _finderPathWithPaginationFindByCN_CPK_T;
	private FinderPath _finderPathWithoutPaginationFindByCN_CPK_T;
	private FinderPath _finderPathCountByCN_CPK_T;

	/**
	 * Returns all the layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCN_CPK_T(
		long classNameId, long classPK, int type) {

		return findByCN_CPK_T(
			classNameId, classPK, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCN_CPK_T(
		long classNameId, long classPK, int type, int start, int end) {

		return findByCN_CPK_T(classNameId, classPK, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCN_CPK_T(
		long classNameId, long classPK, int type, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return findByCN_CPK_T(
			classNameId, classPK, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCN_CPK_T(
		long classNameId, long classPK, int type, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByCN_CPK_T;
					finderArgs = new Object[] {classNameId, classPK, type};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCN_CPK_T;
				finderArgs = new Object[] {
					classNameId, classPK, type, start, end, orderByComparator
				};
			}

			List<LayoutClassedModelUsage> list = null;

			if (useFinderCache) {
				list = (List<LayoutClassedModelUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutClassedModelUsage layoutClassedModelUsage :
							list) {

						if ((classNameId !=
								layoutClassedModelUsage.getClassNameId()) ||
							(classPK != layoutClassedModelUsage.getClassPK()) ||
							(type != layoutClassedModelUsage.getType())) {

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

				sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_CN_CPK_T_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_CN_CPK_T_CLASSPK_2);

				sb.append(_FINDER_COLUMN_CN_CPK_T_TYPE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					queryPos.add(type);

					list = (List<LayoutClassedModelUsage>)QueryUtil.list(
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
	 * Returns the first layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByCN_CPK_T_First(
			long classNameId, long classPK, int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByCN_CPK_T_First(
			classNameId, classPK, type, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByCN_CPK_T_First(
		long classNameId, long classPK, int type,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		List<LayoutClassedModelUsage> list = findByCN_CPK_T(
			classNameId, classPK, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByCN_CPK_T_Last(
			long classNameId, long classPK, int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByCN_CPK_T_Last(
			classNameId, classPK, type, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByCN_CPK_T_Last(
		long classNameId, long classPK, int type,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		int count = countByCN_CPK_T(classNameId, classPK, type);

		if (count == 0) {
			return null;
		}

		List<LayoutClassedModelUsage> list = findByCN_CPK_T(
			classNameId, classPK, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage[] findByCN_CPK_T_PrevAndNext(
			long layoutClassedModelUsageId, long classNameId, long classPK,
			int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = findByPrimaryKey(
			layoutClassedModelUsageId);

		Session session = null;

		try {
			session = openSession();

			LayoutClassedModelUsage[] array =
				new LayoutClassedModelUsageImpl[3];

			array[0] = getByCN_CPK_T_PrevAndNext(
				session, layoutClassedModelUsage, classNameId, classPK, type,
				orderByComparator, true);

			array[1] = layoutClassedModelUsage;

			array[2] = getByCN_CPK_T_PrevAndNext(
				session, layoutClassedModelUsage, classNameId, classPK, type,
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

	protected LayoutClassedModelUsage getByCN_CPK_T_PrevAndNext(
		Session session, LayoutClassedModelUsage layoutClassedModelUsage,
		long classNameId, long classPK, int type,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_CN_CPK_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_CN_CPK_T_CLASSPK_2);

		sb.append(_FINDER_COLUMN_CN_CPK_T_TYPE_2);

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
			sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(classNameId);

		queryPos.add(classPK);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutClassedModelUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutClassedModelUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByCN_CPK_T(long classNameId, long classPK, int type) {
		for (LayoutClassedModelUsage layoutClassedModelUsage :
				findByCN_CPK_T(
					classNameId, classPK, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layoutClassedModelUsage);
		}
	}

	/**
	 * Returns the number of layout classed model usages where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByCN_CPK_T(long classNameId, long classPK, int type) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			FinderPath finderPath = _finderPathCountByCN_CPK_T;

			Object[] finderArgs = new Object[] {classNameId, classPK, type};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_CN_CPK_T_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_CN_CPK_T_CLASSPK_2);

				sb.append(_FINDER_COLUMN_CN_CPK_T_TYPE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					queryPos.add(type);

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

	private static final String _FINDER_COLUMN_CN_CPK_T_CLASSNAMEID_2 =
		"layoutClassedModelUsage.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_CN_CPK_T_CLASSPK_2 =
		"layoutClassedModelUsage.classPK = ? AND ";

	private static final String _FINDER_COLUMN_CN_CPK_T_TYPE_2 =
		"layoutClassedModelUsage.type = ? AND layoutClassedModelUsage.containerKey IS NOT NULL";

	private FinderPath _finderPathWithPaginationFindByCK_CT_P;
	private FinderPath _finderPathWithoutPaginationFindByCK_CT_P;
	private FinderPath _finderPathCountByCK_CT_P;

	/**
	 * Returns all the layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCK_CT_P(
		String containerKey, long containerType, long plid) {

		return findByCK_CT_P(
			containerKey, containerType, plid, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCK_CT_P(
		String containerKey, long containerType, long plid, int start,
		int end) {

		return findByCK_CT_P(
			containerKey, containerType, plid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCK_CT_P(
		String containerKey, long containerType, long plid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return findByCK_CT_P(
			containerKey, containerType, plid, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByCK_CT_P(
		String containerKey, long containerType, long plid, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			containerKey = Objects.toString(containerKey, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByCK_CT_P;
					finderArgs = new Object[] {
						containerKey, containerType, plid
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCK_CT_P;
				finderArgs = new Object[] {
					containerKey, containerType, plid, start, end,
					orderByComparator
				};
			}

			List<LayoutClassedModelUsage> list = null;

			if (useFinderCache) {
				list = (List<LayoutClassedModelUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutClassedModelUsage layoutClassedModelUsage :
							list) {

						if (!containerKey.equals(
								layoutClassedModelUsage.getContainerKey()) ||
							(containerType !=
								layoutClassedModelUsage.getContainerType()) ||
							(plid != layoutClassedModelUsage.getPlid())) {

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

				sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				boolean bindContainerKey = false;

				if (containerKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_CK_CT_P_CONTAINERKEY_3);
				}
				else {
					bindContainerKey = true;

					sb.append(_FINDER_COLUMN_CK_CT_P_CONTAINERKEY_2);
				}

				sb.append(_FINDER_COLUMN_CK_CT_P_CONTAINERTYPE_2);

				sb.append(_FINDER_COLUMN_CK_CT_P_PLID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindContainerKey) {
						queryPos.add(containerKey);
					}

					queryPos.add(containerType);

					queryPos.add(plid);

					list = (List<LayoutClassedModelUsage>)QueryUtil.list(
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
	 * Returns the first layout classed model usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByCK_CT_P_First(
			String containerKey, long containerType, long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByCK_CT_P_First(
			containerKey, containerType, plid, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("containerKey=");
		sb.append(containerKey);

		sb.append(", containerType=");
		sb.append(containerType);

		sb.append(", plid=");
		sb.append(plid);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByCK_CT_P_First(
		String containerKey, long containerType, long plid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		List<LayoutClassedModelUsage> list = findByCK_CT_P(
			containerKey, containerType, plid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByCK_CT_P_Last(
			String containerKey, long containerType, long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByCK_CT_P_Last(
			containerKey, containerType, plid, orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("containerKey=");
		sb.append(containerKey);

		sb.append(", containerType=");
		sb.append(containerType);

		sb.append(", plid=");
		sb.append(plid);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByCK_CT_P_Last(
		String containerKey, long containerType, long plid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		int count = countByCK_CT_P(containerKey, containerType, plid);

		if (count == 0) {
			return null;
		}

		List<LayoutClassedModelUsage> list = findByCK_CT_P(
			containerKey, containerType, plid, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage[] findByCK_CT_P_PrevAndNext(
			long layoutClassedModelUsageId, String containerKey,
			long containerType, long plid,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		containerKey = Objects.toString(containerKey, "");

		LayoutClassedModelUsage layoutClassedModelUsage = findByPrimaryKey(
			layoutClassedModelUsageId);

		Session session = null;

		try {
			session = openSession();

			LayoutClassedModelUsage[] array =
				new LayoutClassedModelUsageImpl[3];

			array[0] = getByCK_CT_P_PrevAndNext(
				session, layoutClassedModelUsage, containerKey, containerType,
				plid, orderByComparator, true);

			array[1] = layoutClassedModelUsage;

			array[2] = getByCK_CT_P_PrevAndNext(
				session, layoutClassedModelUsage, containerKey, containerType,
				plid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected LayoutClassedModelUsage getByCK_CT_P_PrevAndNext(
		Session session, LayoutClassedModelUsage layoutClassedModelUsage,
		String containerKey, long containerType, long plid,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

		boolean bindContainerKey = false;

		if (containerKey.isEmpty()) {
			sb.append(_FINDER_COLUMN_CK_CT_P_CONTAINERKEY_3);
		}
		else {
			bindContainerKey = true;

			sb.append(_FINDER_COLUMN_CK_CT_P_CONTAINERKEY_2);
		}

		sb.append(_FINDER_COLUMN_CK_CT_P_CONTAINERTYPE_2);

		sb.append(_FINDER_COLUMN_CK_CT_P_PLID_2);

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
			sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindContainerKey) {
			queryPos.add(containerKey);
		}

		queryPos.add(containerType);

		queryPos.add(plid);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutClassedModelUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutClassedModelUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63; from the database.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 */
	@Override
	public void removeByCK_CT_P(
		String containerKey, long containerType, long plid) {

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				findByCK_CT_P(
					containerKey, containerType, plid, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layoutClassedModelUsage);
		}
	}

	/**
	 * Returns the number of layout classed model usages where containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByCK_CT_P(
		String containerKey, long containerType, long plid) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			containerKey = Objects.toString(containerKey, "");

			FinderPath finderPath = _finderPathCountByCK_CT_P;

			Object[] finderArgs = new Object[] {
				containerKey, containerType, plid
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				boolean bindContainerKey = false;

				if (containerKey.isEmpty()) {
					sb.append(_FINDER_COLUMN_CK_CT_P_CONTAINERKEY_3);
				}
				else {
					bindContainerKey = true;

					sb.append(_FINDER_COLUMN_CK_CT_P_CONTAINERKEY_2);
				}

				sb.append(_FINDER_COLUMN_CK_CT_P_CONTAINERTYPE_2);

				sb.append(_FINDER_COLUMN_CK_CT_P_PLID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindContainerKey) {
						queryPos.add(containerKey);
					}

					queryPos.add(containerType);

					queryPos.add(plid);

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

	private static final String _FINDER_COLUMN_CK_CT_P_CONTAINERKEY_2 =
		"layoutClassedModelUsage.containerKey = ? AND ";

	private static final String _FINDER_COLUMN_CK_CT_P_CONTAINERKEY_3 =
		"(layoutClassedModelUsage.containerKey IS NULL OR layoutClassedModelUsage.containerKey = '') AND ";

	private static final String _FINDER_COLUMN_CK_CT_P_CONTAINERTYPE_2 =
		"layoutClassedModelUsage.containerType = ? AND ";

	private static final String _FINDER_COLUMN_CK_CT_P_PLID_2 =
		"layoutClassedModelUsage.plid = ? AND layoutClassedModelUsage.containerKey IS NOT NULL";

	private FinderPath _finderPathWithPaginationFindByC_CERC_CN_T;
	private FinderPath _finderPathWithoutPaginationFindByC_CERC_CN_T;
	private FinderPath _finderPathCountByC_CERC_CN_T;

	/**
	 * Returns all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @return the matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type) {

		return findByC_CERC_CN_T(
			companyId, classExternalReferenceCode, classNameId, type,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type, int start, int end) {

		return findByC_CERC_CN_T(
			companyId, classExternalReferenceCode, classNameId, type, start,
			end, null);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return findByC_CERC_CN_T(
			companyId, classExternalReferenceCode, classNameId, type, start,
			end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type, int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			classExternalReferenceCode = Objects.toString(
				classExternalReferenceCode, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_CERC_CN_T;
					finderArgs = new Object[] {
						companyId, classExternalReferenceCode, classNameId, type
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_CERC_CN_T;
				finderArgs = new Object[] {
					companyId, classExternalReferenceCode, classNameId, type,
					start, end, orderByComparator
				};
			}

			List<LayoutClassedModelUsage> list = null;

			if (useFinderCache) {
				list = (List<LayoutClassedModelUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (LayoutClassedModelUsage layoutClassedModelUsage :
							list) {

						if ((companyId !=
								layoutClassedModelUsage.getCompanyId()) ||
							!classExternalReferenceCode.equals(
								layoutClassedModelUsage.
									getClassExternalReferenceCode()) ||
							(classNameId !=
								layoutClassedModelUsage.getClassNameId()) ||
							(type != layoutClassedModelUsage.getType())) {

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

				sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_CERC_CN_T_COMPANYID_2);

				boolean bindClassExternalReferenceCode = false;

				if (classExternalReferenceCode.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_C_CERC_CN_T_CLASSEXTERNALREFERENCECODE_3);
				}
				else {
					bindClassExternalReferenceCode = true;

					sb.append(
						_FINDER_COLUMN_C_CERC_CN_T_CLASSEXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_C_CERC_CN_T_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_CERC_CN_T_TYPE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindClassExternalReferenceCode) {
						queryPos.add(classExternalReferenceCode);
					}

					queryPos.add(classNameId);

					queryPos.add(type);

					list = (List<LayoutClassedModelUsage>)QueryUtil.list(
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
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByC_CERC_CN_T_First(
			long companyId, String classExternalReferenceCode, long classNameId,
			int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage =
			fetchByC_CERC_CN_T_First(
				companyId, classExternalReferenceCode, classNameId, type,
				orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classExternalReferenceCode=");
		sb.append(classExternalReferenceCode);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the first layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByC_CERC_CN_T_First(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		List<LayoutClassedModelUsage> list = findByC_CERC_CN_T(
			companyId, classExternalReferenceCode, classNameId, type, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByC_CERC_CN_T_Last(
			long companyId, String classExternalReferenceCode, long classNameId,
			int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage =
			fetchByC_CERC_CN_T_Last(
				companyId, classExternalReferenceCode, classNameId, type,
				orderByComparator);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classExternalReferenceCode=");
		sb.append(classExternalReferenceCode);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutClassedModelUsageException(sb.toString());
	}

	/**
	 * Returns the last layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByC_CERC_CN_T_Last(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		int count = countByC_CERC_CN_T(
			companyId, classExternalReferenceCode, classNameId, type);

		if (count == 0) {
			return null;
		}

		List<LayoutClassedModelUsage> list = findByC_CERC_CN_T(
			companyId, classExternalReferenceCode, classNameId, type, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layout classed model usages before and after the current layout classed model usage in the ordered set where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param layoutClassedModelUsageId the primary key of the current layout classed model usage
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage[] findByC_CERC_CN_T_PrevAndNext(
			long layoutClassedModelUsageId, long companyId,
			String classExternalReferenceCode, long classNameId, int type,
			OrderByComparator<LayoutClassedModelUsage> orderByComparator)
		throws NoSuchLayoutClassedModelUsageException {

		classExternalReferenceCode = Objects.toString(
			classExternalReferenceCode, "");

		LayoutClassedModelUsage layoutClassedModelUsage = findByPrimaryKey(
			layoutClassedModelUsageId);

		Session session = null;

		try {
			session = openSession();

			LayoutClassedModelUsage[] array =
				new LayoutClassedModelUsageImpl[3];

			array[0] = getByC_CERC_CN_T_PrevAndNext(
				session, layoutClassedModelUsage, companyId,
				classExternalReferenceCode, classNameId, type,
				orderByComparator, true);

			array[1] = layoutClassedModelUsage;

			array[2] = getByC_CERC_CN_T_PrevAndNext(
				session, layoutClassedModelUsage, companyId,
				classExternalReferenceCode, classNameId, type,
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

	protected LayoutClassedModelUsage getByC_CERC_CN_T_PrevAndNext(
		Session session, LayoutClassedModelUsage layoutClassedModelUsage,
		long companyId, String classExternalReferenceCode, long classNameId,
		int type, OrderByComparator<LayoutClassedModelUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_C_CERC_CN_T_COMPANYID_2);

		boolean bindClassExternalReferenceCode = false;

		if (classExternalReferenceCode.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_CERC_CN_T_CLASSEXTERNALREFERENCECODE_3);
		}
		else {
			bindClassExternalReferenceCode = true;

			sb.append(_FINDER_COLUMN_C_CERC_CN_T_CLASSEXTERNALREFERENCECODE_2);
		}

		sb.append(_FINDER_COLUMN_C_CERC_CN_T_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_CERC_CN_T_TYPE_2);

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
			sb.append(LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindClassExternalReferenceCode) {
			queryPos.add(classExternalReferenceCode);
		}

		queryPos.add(classNameId);

		queryPos.add(type);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						layoutClassedModelUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<LayoutClassedModelUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 */
	@Override
	public void removeByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type) {

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				findByC_CERC_CN_T(
					companyId, classExternalReferenceCode, classNameId, type,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layoutClassedModelUsage);
		}
	}

	/**
	 * Returns the number of layout classed model usages where companyId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param type the type
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByC_CERC_CN_T(
		long companyId, String classExternalReferenceCode, long classNameId,
		int type) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			classExternalReferenceCode = Objects.toString(
				classExternalReferenceCode, "");

			FinderPath finderPath = _finderPathCountByC_CERC_CN_T;

			Object[] finderArgs = new Object[] {
				companyId, classExternalReferenceCode, classNameId, type
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_CERC_CN_T_COMPANYID_2);

				boolean bindClassExternalReferenceCode = false;

				if (classExternalReferenceCode.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_C_CERC_CN_T_CLASSEXTERNALREFERENCECODE_3);
				}
				else {
					bindClassExternalReferenceCode = true;

					sb.append(
						_FINDER_COLUMN_C_CERC_CN_T_CLASSEXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_C_CERC_CN_T_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_CERC_CN_T_TYPE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindClassExternalReferenceCode) {
						queryPos.add(classExternalReferenceCode);
					}

					queryPos.add(classNameId);

					queryPos.add(type);

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

	private static final String _FINDER_COLUMN_C_CERC_CN_T_COMPANYID_2 =
		"layoutClassedModelUsage.companyId = ? AND ";

	private static final String
		_FINDER_COLUMN_C_CERC_CN_T_CLASSEXTERNALREFERENCECODE_2 =
			"layoutClassedModelUsage.classExternalReferenceCode = ? AND ";

	private static final String
		_FINDER_COLUMN_C_CERC_CN_T_CLASSEXTERNALREFERENCECODE_3 =
			"(layoutClassedModelUsage.classExternalReferenceCode IS NULL OR layoutClassedModelUsage.classExternalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_C_CERC_CN_T_CLASSNAMEID_2 =
		"layoutClassedModelUsage.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_CERC_CN_T_TYPE_2 =
		"layoutClassedModelUsage.type = ? AND layoutClassedModelUsage.containerKey IS NOT NULL";

	private FinderPath _finderPathFetchByG_CERC_CN_CPK_CK_CT_P;

	/**
	 * Returns the layout classed model usage where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63; or throws a <code>NoSuchLayoutClassedModelUsageException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the matching layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByG_CERC_CN_CPK_CK_CT_P(
			long groupId, String classExternalReferenceCode, long classNameId,
			long classPK, String containerKey, long containerType, long plid)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage =
			fetchByG_CERC_CN_CPK_CK_CT_P(
				groupId, classExternalReferenceCode, classNameId, classPK,
				containerKey, containerType, plid);

		if (layoutClassedModelUsage == null) {
			StringBundler sb = new StringBundler(16);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", classExternalReferenceCode=");
			sb.append(classExternalReferenceCode);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append(", classPK=");
			sb.append(classPK);

			sb.append(", containerKey=");
			sb.append(containerKey);

			sb.append(", containerType=");
			sb.append(containerType);

			sb.append(", plid=");
			sb.append(plid);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchLayoutClassedModelUsageException(sb.toString());
		}

		return layoutClassedModelUsage;
	}

	/**
	 * Returns the layout classed model usage where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByG_CERC_CN_CPK_CK_CT_P(
		long groupId, String classExternalReferenceCode, long classNameId,
		long classPK, String containerKey, long containerType, long plid) {

		return fetchByG_CERC_CN_CPK_CK_CT_P(
			groupId, classExternalReferenceCode, classNameId, classPK,
			containerKey, containerType, plid, true);
	}

	/**
	 * Returns the layout classed model usage where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout classed model usage, or <code>null</code> if a matching layout classed model usage could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByG_CERC_CN_CPK_CK_CT_P(
		long groupId, String classExternalReferenceCode, long classNameId,
		long classPK, String containerKey, long containerType, long plid,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			classExternalReferenceCode = Objects.toString(
				classExternalReferenceCode, "");
			containerKey = Objects.toString(containerKey, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, classExternalReferenceCode, classNameId, classPK,
					containerKey, containerType, plid
				};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByG_CERC_CN_CPK_CK_CT_P, finderArgs, this);
			}

			if (result instanceof LayoutClassedModelUsage) {
				LayoutClassedModelUsage layoutClassedModelUsage =
					(LayoutClassedModelUsage)result;

				if ((groupId != layoutClassedModelUsage.getGroupId()) ||
					!Objects.equals(
						classExternalReferenceCode,
						layoutClassedModelUsage.
							getClassExternalReferenceCode()) ||
					(classNameId != layoutClassedModelUsage.getClassNameId()) ||
					(classPK != layoutClassedModelUsage.getClassPK()) ||
					!Objects.equals(
						containerKey,
						layoutClassedModelUsage.getContainerKey()) ||
					(containerType !=
						layoutClassedModelUsage.getContainerType()) ||
					(plid != layoutClassedModelUsage.getPlid())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(9);

				sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_GROUPID_2);

				boolean bindClassExternalReferenceCode = false;

				if (classExternalReferenceCode.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CLASSEXTERNALREFERENCECODE_3);
				}
				else {
					bindClassExternalReferenceCode = true;

					sb.append(
						_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CLASSEXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CLASSPK_2);

				boolean bindContainerKey = false;

				if (containerKey.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CONTAINERKEY_3);
				}
				else {
					bindContainerKey = true;

					sb.append(
						_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CONTAINERKEY_2);
				}

				sb.append(_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CONTAINERTYPE_2);

				sb.append(_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_PLID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindClassExternalReferenceCode) {
						queryPos.add(classExternalReferenceCode);
					}

					queryPos.add(classNameId);

					queryPos.add(classPK);

					if (bindContainerKey) {
						queryPos.add(containerKey);
					}

					queryPos.add(containerType);

					queryPos.add(plid);

					List<LayoutClassedModelUsage> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByG_CERC_CN_CPK_CK_CT_P,
								finderArgs, list);
						}
					}
					else {
						LayoutClassedModelUsage layoutClassedModelUsage =
							list.get(0);

						result = layoutClassedModelUsage;

						cacheResult(layoutClassedModelUsage);
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
				return (LayoutClassedModelUsage)result;
			}
		}
	}

	/**
	 * Removes the layout classed model usage where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the layout classed model usage that was removed
	 */
	@Override
	public LayoutClassedModelUsage removeByG_CERC_CN_CPK_CK_CT_P(
			long groupId, String classExternalReferenceCode, long classNameId,
			long classPK, String containerKey, long containerType, long plid)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage =
			findByG_CERC_CN_CPK_CK_CT_P(
				groupId, classExternalReferenceCode, classNameId, classPK,
				containerKey, containerType, plid);

		return remove(layoutClassedModelUsage);
	}

	/**
	 * Returns the number of layout classed model usages where groupId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; and classPK = &#63; and containerKey = &#63; and containerType = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param containerKey the container key
	 * @param containerType the container type
	 * @param plid the plid
	 * @return the number of matching layout classed model usages
	 */
	@Override
	public int countByG_CERC_CN_CPK_CK_CT_P(
		long groupId, String classExternalReferenceCode, long classNameId,
		long classPK, String containerKey, long containerType, long plid) {

		LayoutClassedModelUsage layoutClassedModelUsage =
			fetchByG_CERC_CN_CPK_CK_CT_P(
				groupId, classExternalReferenceCode, classNameId, classPK,
				containerKey, containerType, plid);

		if (layoutClassedModelUsage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_GROUPID_2 =
		"layoutClassedModelUsage.groupId = ? AND ";

	private static final String
		_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CLASSEXTERNALREFERENCECODE_2 =
			"layoutClassedModelUsage.classExternalReferenceCode = ? AND ";

	private static final String
		_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CLASSEXTERNALREFERENCECODE_3 =
			"(layoutClassedModelUsage.classExternalReferenceCode IS NULL OR layoutClassedModelUsage.classExternalReferenceCode = '') AND ";

	private static final String
		_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CLASSNAMEID_2 =
			"layoutClassedModelUsage.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CLASSPK_2 =
		"layoutClassedModelUsage.classPK = ? AND ";

	private static final String
		_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CONTAINERKEY_2 =
			"layoutClassedModelUsage.containerKey = ? AND ";

	private static final String
		_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CONTAINERKEY_3 =
			"(layoutClassedModelUsage.containerKey IS NULL OR layoutClassedModelUsage.containerKey = '') AND ";

	private static final String
		_FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_CONTAINERTYPE_2 =
			"layoutClassedModelUsage.containerType = ? AND ";

	private static final String _FINDER_COLUMN_G_CERC_CN_CPK_CK_CT_P_PLID_2 =
		"layoutClassedModelUsage.plid = ?";

	public LayoutClassedModelUsagePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutClassedModelUsage.class);

		setModelImplClass(LayoutClassedModelUsageImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutClassedModelUsageTable.INSTANCE);
	}

	/**
	 * Caches the layout classed model usage in the entity cache if it is enabled.
	 *
	 * @param layoutClassedModelUsage the layout classed model usage
	 */
	@Override
	public void cacheResult(LayoutClassedModelUsage layoutClassedModelUsage) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					layoutClassedModelUsage.getCtCollectionId())) {

			entityCache.putResult(
				LayoutClassedModelUsageImpl.class,
				layoutClassedModelUsage.getPrimaryKey(),
				layoutClassedModelUsage);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					layoutClassedModelUsage.getUuid(),
					layoutClassedModelUsage.getGroupId()
				},
				layoutClassedModelUsage);

			finderCache.putResult(
				_finderPathFetchByG_CERC_CN_CPK_CK_CT_P,
				new Object[] {
					layoutClassedModelUsage.getGroupId(),
					layoutClassedModelUsage.getClassExternalReferenceCode(),
					layoutClassedModelUsage.getClassNameId(),
					layoutClassedModelUsage.getClassPK(),
					layoutClassedModelUsage.getContainerKey(),
					layoutClassedModelUsage.getContainerType(),
					layoutClassedModelUsage.getPlid()
				},
				layoutClassedModelUsage);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the layout classed model usages in the entity cache if it is enabled.
	 *
	 * @param layoutClassedModelUsages the layout classed model usages
	 */
	@Override
	public void cacheResult(
		List<LayoutClassedModelUsage> layoutClassedModelUsages) {

		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (layoutClassedModelUsages.size() >
				 _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				layoutClassedModelUsages) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						layoutClassedModelUsage.getCtCollectionId())) {

				if (entityCache.getResult(
						LayoutClassedModelUsageImpl.class,
						layoutClassedModelUsage.getPrimaryKey()) == null) {

					cacheResult(layoutClassedModelUsage);
				}
			}
		}
	}

	/**
	 * Clears the cache for all layout classed model usages.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(LayoutClassedModelUsageImpl.class);

		finderCache.clearCache(LayoutClassedModelUsageImpl.class);
	}

	/**
	 * Clears the cache for the layout classed model usage.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(LayoutClassedModelUsage layoutClassedModelUsage) {
		entityCache.removeResult(
			LayoutClassedModelUsageImpl.class, layoutClassedModelUsage);
	}

	@Override
	public void clearCache(
		List<LayoutClassedModelUsage> layoutClassedModelUsages) {

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				layoutClassedModelUsages) {

			entityCache.removeResult(
				LayoutClassedModelUsageImpl.class, layoutClassedModelUsage);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(LayoutClassedModelUsageImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(
				LayoutClassedModelUsageImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		LayoutClassedModelUsageModelImpl layoutClassedModelUsageModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					layoutClassedModelUsageModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				layoutClassedModelUsageModelImpl.getUuid(),
				layoutClassedModelUsageModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args,
				layoutClassedModelUsageModelImpl);

			args = new Object[] {
				layoutClassedModelUsageModelImpl.getGroupId(),
				layoutClassedModelUsageModelImpl.
					getClassExternalReferenceCode(),
				layoutClassedModelUsageModelImpl.getClassNameId(),
				layoutClassedModelUsageModelImpl.getClassPK(),
				layoutClassedModelUsageModelImpl.getContainerKey(),
				layoutClassedModelUsageModelImpl.getContainerType(),
				layoutClassedModelUsageModelImpl.getPlid()
			};

			finderCache.putResult(
				_finderPathFetchByG_CERC_CN_CPK_CK_CT_P, args,
				layoutClassedModelUsageModelImpl);
		}
	}

	/**
	 * Creates a new layout classed model usage with the primary key. Does not add the layout classed model usage to the database.
	 *
	 * @param layoutClassedModelUsageId the primary key for the new layout classed model usage
	 * @return the new layout classed model usage
	 */
	@Override
	public LayoutClassedModelUsage create(long layoutClassedModelUsageId) {
		LayoutClassedModelUsage layoutClassedModelUsage =
			new LayoutClassedModelUsageImpl();

		layoutClassedModelUsage.setNew(true);
		layoutClassedModelUsage.setPrimaryKey(layoutClassedModelUsageId);

		String uuid = PortalUUIDUtil.generate();

		layoutClassedModelUsage.setUuid(uuid);

		layoutClassedModelUsage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutClassedModelUsage;
	}

	/**
	 * Removes the layout classed model usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutClassedModelUsageId the primary key of the layout classed model usage
	 * @return the layout classed model usage that was removed
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage remove(long layoutClassedModelUsageId)
		throws NoSuchLayoutClassedModelUsageException {

		return remove((Serializable)layoutClassedModelUsageId);
	}

	/**
	 * Removes the layout classed model usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the layout classed model usage
	 * @return the layout classed model usage that was removed
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage remove(Serializable primaryKey)
		throws NoSuchLayoutClassedModelUsageException {

		Session session = null;

		try {
			session = openSession();

			LayoutClassedModelUsage layoutClassedModelUsage =
				(LayoutClassedModelUsage)session.get(
					LayoutClassedModelUsageImpl.class, primaryKey);

			if (layoutClassedModelUsage == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchLayoutClassedModelUsageException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(layoutClassedModelUsage);
		}
		catch (NoSuchLayoutClassedModelUsageException noSuchEntityException) {
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
	protected LayoutClassedModelUsage removeImpl(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutClassedModelUsage)) {
				layoutClassedModelUsage = (LayoutClassedModelUsage)session.get(
					LayoutClassedModelUsageImpl.class,
					layoutClassedModelUsage.getPrimaryKeyObj());
			}

			if ((layoutClassedModelUsage != null) &&
				ctPersistenceHelper.isRemove(layoutClassedModelUsage)) {

				session.delete(layoutClassedModelUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutClassedModelUsage != null) {
			clearCache(layoutClassedModelUsage);
		}

		return layoutClassedModelUsage;
	}

	@Override
	public LayoutClassedModelUsage updateImpl(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		boolean isNew = layoutClassedModelUsage.isNew();

		if (!(layoutClassedModelUsage instanceof
				LayoutClassedModelUsageModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutClassedModelUsage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutClassedModelUsage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutClassedModelUsage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutClassedModelUsage implementation " +
					layoutClassedModelUsage.getClass());
		}

		LayoutClassedModelUsageModelImpl layoutClassedModelUsageModelImpl =
			(LayoutClassedModelUsageModelImpl)layoutClassedModelUsage;

		if (Validator.isNull(layoutClassedModelUsage.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutClassedModelUsage.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutClassedModelUsage.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutClassedModelUsage.setCreateDate(date);
			}
			else {
				layoutClassedModelUsage.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutClassedModelUsageModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutClassedModelUsage.setModifiedDate(date);
			}
			else {
				layoutClassedModelUsage.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutClassedModelUsage)) {
				if (!isNew) {
					session.evict(
						LayoutClassedModelUsageImpl.class,
						layoutClassedModelUsage.getPrimaryKeyObj());
				}

				session.save(layoutClassedModelUsage);
			}
			else {
				layoutClassedModelUsage =
					(LayoutClassedModelUsage)session.merge(
						layoutClassedModelUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			LayoutClassedModelUsageImpl.class, layoutClassedModelUsageModelImpl,
			false, true);

		cacheUniqueFindersCache(layoutClassedModelUsageModelImpl);

		if (isNew) {
			layoutClassedModelUsage.setNew(false);
		}

		layoutClassedModelUsage.resetOriginalValues();

		return layoutClassedModelUsage;
	}

	/**
	 * Returns the layout classed model usage with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the layout classed model usage
	 * @return the layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByPrimaryKey(Serializable primaryKey)
		throws NoSuchLayoutClassedModelUsageException {

		LayoutClassedModelUsage layoutClassedModelUsage = fetchByPrimaryKey(
			primaryKey);

		if (layoutClassedModelUsage == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchLayoutClassedModelUsageException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return layoutClassedModelUsage;
	}

	/**
	 * Returns the layout classed model usage with the primary key or throws a <code>NoSuchLayoutClassedModelUsageException</code> if it could not be found.
	 *
	 * @param layoutClassedModelUsageId the primary key of the layout classed model usage
	 * @return the layout classed model usage
	 * @throws NoSuchLayoutClassedModelUsageException if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage findByPrimaryKey(
			long layoutClassedModelUsageId)
		throws NoSuchLayoutClassedModelUsageException {

		return findByPrimaryKey((Serializable)layoutClassedModelUsageId);
	}

	/**
	 * Returns the layout classed model usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the layout classed model usage
	 * @return the layout classed model usage, or <code>null</code> if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				LayoutClassedModelUsage.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		LayoutClassedModelUsage layoutClassedModelUsage =
			(LayoutClassedModelUsage)entityCache.getResult(
				LayoutClassedModelUsageImpl.class, primaryKey);

		if (layoutClassedModelUsage != null) {
			return layoutClassedModelUsage;
		}

		Session session = null;

		try {
			session = openSession();

			layoutClassedModelUsage = (LayoutClassedModelUsage)session.get(
				LayoutClassedModelUsageImpl.class, primaryKey);

			if (layoutClassedModelUsage != null) {
				cacheResult(layoutClassedModelUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return layoutClassedModelUsage;
	}

	/**
	 * Returns the layout classed model usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutClassedModelUsageId the primary key of the layout classed model usage
	 * @return the layout classed model usage, or <code>null</code> if a layout classed model usage with the primary key could not be found
	 */
	@Override
	public LayoutClassedModelUsage fetchByPrimaryKey(
		long layoutClassedModelUsageId) {

		return fetchByPrimaryKey((Serializable)layoutClassedModelUsageId);
	}

	@Override
	public Map<Serializable, LayoutClassedModelUsage> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(
				LayoutClassedModelUsage.class)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, LayoutClassedModelUsage> map =
			new HashMap<Serializable, LayoutClassedModelUsage>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			LayoutClassedModelUsage layoutClassedModelUsage = fetchByPrimaryKey(
				primaryKey);

			if (layoutClassedModelUsage != null) {
				map.put(primaryKey, layoutClassedModelUsage);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						LayoutClassedModelUsage.class, primaryKey)) {

				LayoutClassedModelUsage layoutClassedModelUsage =
					(LayoutClassedModelUsage)entityCache.getResult(
						LayoutClassedModelUsageImpl.class, primaryKey);

				if (layoutClassedModelUsage == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, layoutClassedModelUsage);
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

			for (LayoutClassedModelUsage layoutClassedModelUsage :
					(List<LayoutClassedModelUsage>)query.list()) {

				map.put(
					layoutClassedModelUsage.getPrimaryKeyObj(),
					layoutClassedModelUsage);

				cacheResult(layoutClassedModelUsage);
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
	 * Returns all the layout classed model usages.
	 *
	 * @return the layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout classed model usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @return the range of layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findAll(
		int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layout classed model usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutClassedModelUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout classed model usages
	 * @param end the upper bound of the range of layout classed model usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of layout classed model usages
	 */
	@Override
	public List<LayoutClassedModelUsage> findAll(
		int start, int end,
		OrderByComparator<LayoutClassedModelUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

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

			List<LayoutClassedModelUsage> list = null;

			if (useFinderCache) {
				list = (List<LayoutClassedModelUsage>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_LAYOUTCLASSEDMODELUSAGE);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_LAYOUTCLASSEDMODELUSAGE;

					sql = sql.concat(
						LayoutClassedModelUsageModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<LayoutClassedModelUsage>)QueryUtil.list(
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
	 * Removes all the layout classed model usages from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (LayoutClassedModelUsage layoutClassedModelUsage : findAll()) {
			remove(layoutClassedModelUsage);
		}
	}

	/**
	 * Returns the number of layout classed model usages.
	 *
	 * @return the number of layout classed model usages
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					LayoutClassedModelUsage.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_LAYOUTCLASSEDMODELUSAGE);

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
		return "layoutClassedModelUsageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTCLASSEDMODELUSAGE;
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
		return LayoutClassedModelUsageModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutClassedModelUsage";
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
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("classExternalReferenceCode");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("containerKey");
		ctMergeColumnNames.add("containerType");
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("layoutClassedModelUsageId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"groupId", "classExternalReferenceCode", "classNameId",
				"classPK", "containerKey", "containerType", "plid"
			});
	}

	/**
	 * Initializes the layout classed model usage persistence.
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

		_finderPathWithPaginationFindByPlid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPlid",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"plid"}, true);

		_finderPathWithoutPaginationFindByPlid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPlid",
			new String[] {Long.class.getName()}, new String[] {"plid"}, true);

		_finderPathCountByPlid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPlid",
			new String[] {Long.class.getName()}, new String[] {"plid"}, false);

		_finderPathWithPaginationFindByC_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByC_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CN",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "classNameId"}, true);

		_finderPathCountByC_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CN",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "classNameId"}, false);

		_finderPathWithPaginationFindByCN_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCN_CPK",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"classNameId", "classPK"}, true);

		_finderPathWithoutPaginationFindByCN_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCN_CPK",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"classNameId", "classPK"}, true);

		_finderPathCountByCN_CPK = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCN_CPK",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"classNameId", "classPK"}, false);

		_finderPathWithPaginationFindByC_CERC_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CERC_CN",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"companyId", "classExternalReferenceCode", "classNameId"
			},
			true);

		_finderPathWithoutPaginationFindByC_CERC_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CERC_CN",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName()
			},
			new String[] {
				"companyId", "classExternalReferenceCode", "classNameId"
			},
			true);

		_finderPathCountByC_CERC_CN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CERC_CN",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName()
			},
			new String[] {
				"companyId", "classExternalReferenceCode", "classNameId"
			},
			false);

		_finderPathWithPaginationFindByC_CN_CT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN_CT",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId", "classNameId", "containerType"}, true);

		_finderPathWithoutPaginationFindByC_CN_CT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CN_CT",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "classNameId", "containerType"}, true);

		_finderPathCountByC_CN_CT = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CN_CT",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "classNameId", "containerType"}, false);

		_finderPathWithPaginationFindByCN_CPK_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCN_CPK_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"classNameId", "classPK", "type_"}, true);

		_finderPathWithoutPaginationFindByCN_CPK_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCN_CPK_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"classNameId", "classPK", "type_"}, true);

		_finderPathCountByCN_CPK_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCN_CPK_T",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"classNameId", "classPK", "type_"}, false);

		_finderPathWithPaginationFindByCK_CT_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCK_CT_P",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"containerKey", "containerType", "plid"}, true);

		_finderPathWithoutPaginationFindByCK_CT_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCK_CT_P",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Long.class.getName()
			},
			new String[] {"containerKey", "containerType", "plid"}, true);

		_finderPathCountByCK_CT_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCK_CT_P",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Long.class.getName()
			},
			new String[] {"containerKey", "containerType", "plid"}, false);

		_finderPathWithPaginationFindByC_CERC_CN_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CERC_CN_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"companyId", "classExternalReferenceCode", "classNameId",
				"type_"
			},
			true);

		_finderPathWithoutPaginationFindByC_CERC_CN_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CERC_CN_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {
				"companyId", "classExternalReferenceCode", "classNameId",
				"type_"
			},
			true);

		_finderPathCountByC_CERC_CN_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CERC_CN_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {
				"companyId", "classExternalReferenceCode", "classNameId",
				"type_"
			},
			false);

		_finderPathFetchByG_CERC_CN_CPK_CK_CT_P = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_CERC_CN_CPK_CK_CT_P",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName(), Long.class.getName(),
				String.class.getName(), Long.class.getName(),
				Long.class.getName()
			},
			new String[] {
				"groupId", "classExternalReferenceCode", "classNameId",
				"classPK", "containerKey", "containerType", "plid"
			},
			true);

		LayoutClassedModelUsageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutClassedModelUsageUtil.setPersistence(null);

		entityCache.removeCache(LayoutClassedModelUsageImpl.class.getName());
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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

	private static final String _SQL_SELECT_LAYOUTCLASSEDMODELUSAGE =
		"SELECT layoutClassedModelUsage FROM LayoutClassedModelUsage layoutClassedModelUsage";

	private static final String _SQL_SELECT_LAYOUTCLASSEDMODELUSAGE_WHERE =
		"SELECT layoutClassedModelUsage FROM LayoutClassedModelUsage layoutClassedModelUsage WHERE ";

	private static final String _SQL_COUNT_LAYOUTCLASSEDMODELUSAGE =
		"SELECT COUNT(layoutClassedModelUsage) FROM LayoutClassedModelUsage layoutClassedModelUsage";

	private static final String _SQL_COUNT_LAYOUTCLASSEDMODELUSAGE_WHERE =
		"SELECT COUNT(layoutClassedModelUsage) FROM LayoutClassedModelUsage layoutClassedModelUsage WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS =
		"layoutClassedModelUsage.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No LayoutClassedModelUsage exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutClassedModelUsage exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutClassedModelUsagePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}