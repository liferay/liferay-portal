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
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateLayoutExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.LayoutPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
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
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.model.impl.LayoutModelImpl;

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
 * The persistence implementation for the layout service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutPersistenceImpl
	extends BasePersistenceImpl<Layout> implements LayoutPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutUtil</code> to access the layout persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutImpl.class.getName();

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
	 * Returns all the layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

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

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if (!uuid.equals(layout.getUuid())) {
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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

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
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
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

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByUuid_First(
			String uuid, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByUuid_First(uuid, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByUuid_First(
		String uuid, OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByUuid_Last(
			String uuid, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByUuid_Last(uuid, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByUuid_Last(
		String uuid, OrderByComparator<Layout> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where uuid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByUuid_PrevAndNext(
			long plid, String uuid, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		uuid = Objects.toString(uuid, "");

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, layout, uuid, orderByComparator, true);

			array[1] = layout;

			array[2] = getByUuid_PrevAndNext(
				session, layout, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout getByUuid_PrevAndNext(
		Session session, Layout layout, String uuid,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (Layout layout :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layouts
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

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

	private static final String _FINDER_COLUMN_UUID_UUID_2 = "layout.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(layout.uuid IS NULL OR layout.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G_P;

	/**
	 * Returns the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByUUID_G_P(
			String uuid, long groupId, boolean privateLayout)
		throws NoSuchLayoutException {

		Layout layout = fetchByUUID_G_P(uuid, groupId, privateLayout);

		if (layout == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("uuid=");
			sb.append(uuid);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append(", privateLayout=");
			sb.append(privateLayout);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchLayoutException(sb.toString());
		}

		return layout;
	}

	/**
	 * Returns the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByUUID_G_P(
		String uuid, long groupId, boolean privateLayout) {

		return fetchByUUID_G_P(uuid, groupId, privateLayout, true);
	}

	/**
	 * Returns the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByUUID_G_P(
		String uuid, long groupId, boolean privateLayout,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			uuid = Objects.toString(uuid, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {uuid, groupId, privateLayout};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByUUID_G_P, finderArgs, this);
			}

			if (result instanceof Layout) {
				Layout layout = (Layout)result;

				if (!Objects.equals(uuid, layout.getUuid()) ||
					(groupId != layout.getGroupId()) ||
					(privateLayout != layout.isPrivateLayout())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_G_P_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_G_P_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_UUID_G_P_PRIVATELAYOUT_2);

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

					queryPos.add(privateLayout);

					List<Layout> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByUUID_G_P, finderArgs, list);
						}
					}
					else {
						Layout layout = list.get(0);

						result = layout;

						cacheResult(layout);
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
				return (Layout)result;
			}
		}
	}

	/**
	 * Removes the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the layout that was removed
	 */
	@Override
	public Layout removeByUUID_G_P(
			String uuid, long groupId, boolean privateLayout)
		throws NoSuchLayoutException {

		Layout layout = findByUUID_G_P(uuid, groupId, privateLayout);

		return remove(layout);
	}

	/**
	 * Returns the number of layouts where uuid = &#63; and groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layouts
	 */
	@Override
	public int countByUUID_G_P(
		String uuid, long groupId, boolean privateLayout) {

		Layout layout = fetchByUUID_G_P(uuid, groupId, privateLayout);

		if (layout == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_P_UUID_2 =
		"layout.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_P_UUID_3 =
		"(layout.uuid IS NULL OR layout.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_P_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_P_PRIVATELAYOUT_2 =
		"layout.privateLayout = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

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

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if (!uuid.equals(layout.getUuid()) ||
							(companyId != layout.getCompanyId())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

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
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
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

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByUuid_C_First(uuid, companyId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByUuid_C_Last(uuid, companyId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByUuid_C_PrevAndNext(
			long plid, String uuid, long companyId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		uuid = Objects.toString(uuid, "");

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, layout, uuid, companyId, orderByComparator, true);

			array[1] = layout;

			array[2] = getByUuid_C_PrevAndNext(
				session, layout, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout getByUuid_C_PrevAndNext(
		Session session, Layout layout, String uuid, long companyId,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (Layout layout :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

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
		"layout.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(layout.uuid IS NULL OR layout.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"layout.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the layouts where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

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

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if (groupId != layout.getGroupId()) {
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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByGroupId_First(
			long groupId, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByGroupId_First(groupId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByGroupId_First(
		long groupId, OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByGroupId(groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByGroupId_Last(
			long groupId, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByGroupId_Last(groupId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByGroupId_Last(
		long groupId, OrderByComparator<Layout> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByGroupId_PrevAndNext(
			long plid, long groupId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, layout, groupId, orderByComparator, true);

			array[1] = layout;

			array[2] = getByGroupId_PrevAndNext(
				session, layout, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout getByGroupId_PrevAndNext(
		Session session, Layout layout, long groupId,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByGroupId(long groupId, int start, int end) {
		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<Layout>)QueryUtil.list(
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
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] filterFindByGroupId_PrevAndNext(
			long plid, long groupId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(plid, groupId, orderByComparator);
		}

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, layout, groupId, orderByComparator, true);

			array[1] = layout;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, layout, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout filterGetByGroupId_PrevAndNext(
		Session session, Layout layout, long groupId,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (Layout layout :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

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

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByGroupId(groupId);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
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

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"layout.groupId = ? AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2_SQL =
		"layout.groupId = ? AND layout.system_ = [$FALSE$]";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the layouts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByCompanyId(long companyId, int start, int end) {
		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

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

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if (companyId != layout.getCompanyId()) {
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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByCompanyId_First(
			long companyId, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByCompanyId_First(companyId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByCompanyId_First(
		long companyId, OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByCompanyId(companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByCompanyId_Last(
			long companyId, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByCompanyId_Last(companyId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByCompanyId_Last(
		long companyId, OrderByComparator<Layout> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where companyId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByCompanyId_PrevAndNext(
			long plid, long companyId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, layout, companyId, orderByComparator, true);

			array[1] = layout;

			array[2] = getByCompanyId_PrevAndNext(
				session, layout, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout getByCompanyId_PrevAndNext(
		Session session, Layout layout, long companyId,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (Layout layout :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

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
		"layout.companyId = ? AND layout.system = [$FALSE$]";

	private FinderPath _finderPathWithPaginationFindByParentPlid;
	private FinderPath _finderPathWithoutPaginationFindByParentPlid;
	private FinderPath _finderPathCountByParentPlid;

	/**
	 * Returns all the layouts where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByParentPlid(long parentPlid) {
		return findByParentPlid(
			parentPlid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where parentPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param parentPlid the parent plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByParentPlid(long parentPlid, int start, int end) {
		return findByParentPlid(parentPlid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where parentPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param parentPlid the parent plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByParentPlid(
		long parentPlid, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByParentPlid(
			parentPlid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where parentPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param parentPlid the parent plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByParentPlid(
		long parentPlid, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByParentPlid;
					finderArgs = new Object[] {parentPlid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByParentPlid;
				finderArgs = new Object[] {
					parentPlid, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if (parentPlid != layout.getParentPlid()) {
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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_PARENTPLID_PARENTPLID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentPlid);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByParentPlid_First(
			long parentPlid, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByParentPlid_First(parentPlid, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentPlid=");
		sb.append(parentPlid);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByParentPlid_First(
		long parentPlid, OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByParentPlid(
			parentPlid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByParentPlid_Last(
			long parentPlid, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByParentPlid_Last(parentPlid, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("parentPlid=");
		sb.append(parentPlid);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByParentPlid_Last(
		long parentPlid, OrderByComparator<Layout> orderByComparator) {

		int count = countByParentPlid(parentPlid);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByParentPlid(
			parentPlid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByParentPlid_PrevAndNext(
			long plid, long parentPlid,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByParentPlid_PrevAndNext(
				session, layout, parentPlid, orderByComparator, true);

			array[1] = layout;

			array[2] = getByParentPlid_PrevAndNext(
				session, layout, parentPlid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout getByParentPlid_PrevAndNext(
		Session session, Layout layout, long parentPlid,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_PARENTPLID_PARENTPLID_2);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(parentPlid);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where parentPlid = &#63; from the database.
	 *
	 * @param parentPlid the parent plid
	 */
	@Override
	public void removeByParentPlid(long parentPlid) {
		for (Layout layout :
				findByParentPlid(
					parentPlid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @return the number of matching layouts
	 */
	@Override
	public int countByParentPlid(long parentPlid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByParentPlid;

			Object[] finderArgs = new Object[] {parentPlid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_PARENTPLID_PARENTPLID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(parentPlid);

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

	private static final String _FINDER_COLUMN_PARENTPLID_PARENTPLID_2 =
		"layout.parentPlid = ? AND layout.system = [$FALSE$]";

	private FinderPath _finderPathWithPaginationFindByIconImageId;
	private FinderPath _finderPathWithoutPaginationFindByIconImageId;
	private FinderPath _finderPathCountByIconImageId;

	/**
	 * Returns all the layouts where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByIconImageId(long iconImageId) {
		return findByIconImageId(
			iconImageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByIconImageId(
		long iconImageId, int start, int end) {

		return findByIconImageId(iconImageId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByIconImageId(
		long iconImageId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByIconImageId(
			iconImageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByIconImageId(
		long iconImageId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByIconImageId;
					finderArgs = new Object[] {iconImageId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByIconImageId;
				finderArgs = new Object[] {
					iconImageId, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if (iconImageId != layout.getIconImageId()) {
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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_ICONIMAGEID_ICONIMAGEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(iconImageId);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByIconImageId_First(
			long iconImageId, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByIconImageId_First(
			iconImageId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("iconImageId=");
		sb.append(iconImageId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByIconImageId_First(
		long iconImageId, OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByIconImageId(
			iconImageId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByIconImageId_Last(
			long iconImageId, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByIconImageId_Last(iconImageId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("iconImageId=");
		sb.append(iconImageId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByIconImageId_Last(
		long iconImageId, OrderByComparator<Layout> orderByComparator) {

		int count = countByIconImageId(iconImageId);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByIconImageId(
			iconImageId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByIconImageId_PrevAndNext(
			long plid, long iconImageId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByIconImageId_PrevAndNext(
				session, layout, iconImageId, orderByComparator, true);

			array[1] = layout;

			array[2] = getByIconImageId_PrevAndNext(
				session, layout, iconImageId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout getByIconImageId_PrevAndNext(
		Session session, Layout layout, long iconImageId,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_ICONIMAGEID_ICONIMAGEID_2);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(iconImageId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where iconImageId = &#63; from the database.
	 *
	 * @param iconImageId the icon image ID
	 */
	@Override
	public void removeByIconImageId(long iconImageId) {
		for (Layout layout :
				findByIconImageId(
					iconImageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByIconImageId(long iconImageId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByIconImageId;

			Object[] finderArgs = new Object[] {iconImageId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_ICONIMAGEID_ICONIMAGEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(iconImageId);

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

	private static final String _FINDER_COLUMN_ICONIMAGEID_ICONIMAGEID_2 =
		"layout.iconImageId = ?";

	private FinderPath _finderPathWithPaginationFindByLayoutPrototypeUuid;
	private FinderPath _finderPathWithoutPaginationFindByLayoutPrototypeUuid;
	private FinderPath _finderPathCountByLayoutPrototypeUuid;

	/**
	 * Returns all the layouts where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByLayoutPrototypeUuid(String layoutPrototypeUuid) {
		return findByLayoutPrototypeUuid(
			layoutPrototypeUuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByLayoutPrototypeUuid(
		String layoutPrototypeUuid, int start, int end) {

		return findByLayoutPrototypeUuid(layoutPrototypeUuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByLayoutPrototypeUuid(
		String layoutPrototypeUuid, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByLayoutPrototypeUuid(
			layoutPrototypeUuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByLayoutPrototypeUuid(
		String layoutPrototypeUuid, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			layoutPrototypeUuid = Objects.toString(layoutPrototypeUuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByLayoutPrototypeUuid;
					finderArgs = new Object[] {layoutPrototypeUuid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByLayoutPrototypeUuid;
				finderArgs = new Object[] {
					layoutPrototypeUuid, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if (!layoutPrototypeUuid.equals(
								layout.getLayoutPrototypeUuid())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				boolean bindLayoutPrototypeUuid = false;

				if (layoutPrototypeUuid.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_LAYOUTPROTOTYPEUUID_LAYOUTPROTOTYPEUUID_3);
				}
				else {
					bindLayoutPrototypeUuid = true;

					sb.append(
						_FINDER_COLUMN_LAYOUTPROTOTYPEUUID_LAYOUTPROTOTYPEUUID_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindLayoutPrototypeUuid) {
						queryPos.add(layoutPrototypeUuid);
					}

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByLayoutPrototypeUuid_First(
			String layoutPrototypeUuid,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByLayoutPrototypeUuid_First(
			layoutPrototypeUuid, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("layoutPrototypeUuid=");
		sb.append(layoutPrototypeUuid);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByLayoutPrototypeUuid_First(
		String layoutPrototypeUuid,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByLayoutPrototypeUuid(
			layoutPrototypeUuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByLayoutPrototypeUuid_Last(
			String layoutPrototypeUuid,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByLayoutPrototypeUuid_Last(
			layoutPrototypeUuid, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("layoutPrototypeUuid=");
		sb.append(layoutPrototypeUuid);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByLayoutPrototypeUuid_Last(
		String layoutPrototypeUuid,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByLayoutPrototypeUuid(layoutPrototypeUuid);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByLayoutPrototypeUuid(
			layoutPrototypeUuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByLayoutPrototypeUuid_PrevAndNext(
			long plid, String layoutPrototypeUuid,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		layoutPrototypeUuid = Objects.toString(layoutPrototypeUuid, "");

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByLayoutPrototypeUuid_PrevAndNext(
				session, layout, layoutPrototypeUuid, orderByComparator, true);

			array[1] = layout;

			array[2] = getByLayoutPrototypeUuid_PrevAndNext(
				session, layout, layoutPrototypeUuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout getByLayoutPrototypeUuid_PrevAndNext(
		Session session, Layout layout, String layoutPrototypeUuid,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		boolean bindLayoutPrototypeUuid = false;

		if (layoutPrototypeUuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_LAYOUTPROTOTYPEUUID_LAYOUTPROTOTYPEUUID_3);
		}
		else {
			bindLayoutPrototypeUuid = true;

			sb.append(_FINDER_COLUMN_LAYOUTPROTOTYPEUUID_LAYOUTPROTOTYPEUUID_2);
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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindLayoutPrototypeUuid) {
			queryPos.add(layoutPrototypeUuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where layoutPrototypeUuid = &#63; from the database.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 */
	@Override
	public void removeByLayoutPrototypeUuid(String layoutPrototypeUuid) {
		for (Layout layout :
				findByLayoutPrototypeUuid(
					layoutPrototypeUuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @return the number of matching layouts
	 */
	@Override
	public int countByLayoutPrototypeUuid(String layoutPrototypeUuid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			layoutPrototypeUuid = Objects.toString(layoutPrototypeUuid, "");

			FinderPath finderPath = _finderPathCountByLayoutPrototypeUuid;

			Object[] finderArgs = new Object[] {layoutPrototypeUuid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				boolean bindLayoutPrototypeUuid = false;

				if (layoutPrototypeUuid.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_LAYOUTPROTOTYPEUUID_LAYOUTPROTOTYPEUUID_3);
				}
				else {
					bindLayoutPrototypeUuid = true;

					sb.append(
						_FINDER_COLUMN_LAYOUTPROTOTYPEUUID_LAYOUTPROTOTYPEUUID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindLayoutPrototypeUuid) {
						queryPos.add(layoutPrototypeUuid);
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

	private static final String
		_FINDER_COLUMN_LAYOUTPROTOTYPEUUID_LAYOUTPROTOTYPEUUID_2 =
			"layout.layoutPrototypeUuid = ? AND layout.system = [$FALSE$]";

	private static final String
		_FINDER_COLUMN_LAYOUTPROTOTYPEUUID_LAYOUTPROTOTYPEUUID_3 =
			"(layout.layoutPrototypeUuid IS NULL OR layout.layoutPrototypeUuid = '') AND layout.system = [$FALSE$]";

	private FinderPath
		_finderPathWithPaginationFindByLayoutSetPrototypeLayoutERC;
	private FinderPath
		_finderPathWithoutPaginationFindByLayoutSetPrototypeLayoutERC;
	private FinderPath _finderPathCountByLayoutSetPrototypeLayoutERC;

	/**
	 * Returns all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC) {

		return findByLayoutSetPrototypeLayoutERC(
			layoutSetPrototypeLayoutERC, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC, int start, int end) {

		return findByLayoutSetPrototypeLayoutERC(
			layoutSetPrototypeLayoutERC, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByLayoutSetPrototypeLayoutERC(
			layoutSetPrototypeLayoutERC, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			layoutSetPrototypeLayoutERC = Objects.toString(
				layoutSetPrototypeLayoutERC, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByLayoutSetPrototypeLayoutERC;
					finderArgs = new Object[] {layoutSetPrototypeLayoutERC};
				}
			}
			else if (useFinderCache) {
				finderPath =
					_finderPathWithPaginationFindByLayoutSetPrototypeLayoutERC;
				finderArgs = new Object[] {
					layoutSetPrototypeLayoutERC, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if (!layoutSetPrototypeLayoutERC.equals(
								layout.getLayoutSetPrototypeLayoutERC())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				boolean bindLayoutSetPrototypeLayoutERC = false;

				if (layoutSetPrototypeLayoutERC.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_LAYOUTSETPROTOTYPELAYOUTERC_LAYOUTSETPROTOTYPELAYOUTERC_3);
				}
				else {
					bindLayoutSetPrototypeLayoutERC = true;

					sb.append(
						_FINDER_COLUMN_LAYOUTSETPROTOTYPELAYOUTERC_LAYOUTSETPROTOTYPELAYOUTERC_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindLayoutSetPrototypeLayoutERC) {
						queryPos.add(layoutSetPrototypeLayoutERC);
					}

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByLayoutSetPrototypeLayoutERC_First(
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByLayoutSetPrototypeLayoutERC_First(
			layoutSetPrototypeLayoutERC, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("layoutSetPrototypeLayoutERC=");
		sb.append(layoutSetPrototypeLayoutERC);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByLayoutSetPrototypeLayoutERC_First(
		String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByLayoutSetPrototypeLayoutERC(
			layoutSetPrototypeLayoutERC, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByLayoutSetPrototypeLayoutERC_Last(
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByLayoutSetPrototypeLayoutERC_Last(
			layoutSetPrototypeLayoutERC, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("layoutSetPrototypeLayoutERC=");
		sb.append(layoutSetPrototypeLayoutERC);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByLayoutSetPrototypeLayoutERC_Last(
		String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByLayoutSetPrototypeLayoutERC(
			layoutSetPrototypeLayoutERC);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByLayoutSetPrototypeLayoutERC(
			layoutSetPrototypeLayoutERC, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByLayoutSetPrototypeLayoutERC_PrevAndNext(
			long plid, String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		layoutSetPrototypeLayoutERC = Objects.toString(
			layoutSetPrototypeLayoutERC, "");

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByLayoutSetPrototypeLayoutERC_PrevAndNext(
				session, layout, layoutSetPrototypeLayoutERC, orderByComparator,
				true);

			array[1] = layout;

			array[2] = getByLayoutSetPrototypeLayoutERC_PrevAndNext(
				session, layout, layoutSetPrototypeLayoutERC, orderByComparator,
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

	protected Layout getByLayoutSetPrototypeLayoutERC_PrevAndNext(
		Session session, Layout layout, String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		boolean bindLayoutSetPrototypeLayoutERC = false;

		if (layoutSetPrototypeLayoutERC.isEmpty()) {
			sb.append(
				_FINDER_COLUMN_LAYOUTSETPROTOTYPELAYOUTERC_LAYOUTSETPROTOTYPELAYOUTERC_3);
		}
		else {
			bindLayoutSetPrototypeLayoutERC = true;

			sb.append(
				_FINDER_COLUMN_LAYOUTSETPROTOTYPELAYOUTERC_LAYOUTSETPROTOTYPELAYOUTERC_2);
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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindLayoutSetPrototypeLayoutERC) {
			queryPos.add(layoutSetPrototypeLayoutERC);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where layoutSetPrototypeLayoutERC = &#63; from the database.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 */
	@Override
	public void removeByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC) {

		for (Layout layout :
				findByLayoutSetPrototypeLayoutERC(
					layoutSetPrototypeLayoutERC, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the number of matching layouts
	 */
	@Override
	public int countByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			layoutSetPrototypeLayoutERC = Objects.toString(
				layoutSetPrototypeLayoutERC, "");

			FinderPath finderPath =
				_finderPathCountByLayoutSetPrototypeLayoutERC;

			Object[] finderArgs = new Object[] {layoutSetPrototypeLayoutERC};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				boolean bindLayoutSetPrototypeLayoutERC = false;

				if (layoutSetPrototypeLayoutERC.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_LAYOUTSETPROTOTYPELAYOUTERC_LAYOUTSETPROTOTYPELAYOUTERC_3);
				}
				else {
					bindLayoutSetPrototypeLayoutERC = true;

					sb.append(
						_FINDER_COLUMN_LAYOUTSETPROTOTYPELAYOUTERC_LAYOUTSETPROTOTYPELAYOUTERC_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindLayoutSetPrototypeLayoutERC) {
						queryPos.add(layoutSetPrototypeLayoutERC);
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

	private static final String
		_FINDER_COLUMN_LAYOUTSETPROTOTYPELAYOUTERC_LAYOUTSETPROTOTYPELAYOUTERC_2 =
			"layout.layoutSetPrototypeLayoutERC = ? AND layout.system = [$FALSE$]";

	private static final String
		_FINDER_COLUMN_LAYOUTSETPROTOTYPELAYOUTERC_LAYOUTSETPROTOTYPELAYOUTERC_3 =
			"(layout.layoutSetPrototypeLayoutERC IS NULL OR layout.layoutSetPrototypeLayoutERC = '') AND layout.system = [$FALSE$]";

	private FinderPath _finderPathWithPaginationFindByG_P;
	private FinderPath _finderPathWithoutPaginationFindByG_P;
	private FinderPath _finderPathCountByG_P;

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P(long groupId, boolean privateLayout) {
		return findByG_P(
			groupId, privateLayout, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P(
		long groupId, boolean privateLayout, int start, int end) {

		return findByG_P(groupId, privateLayout, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_P(
			groupId, privateLayout, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P;
					finderArgs = new Object[] {groupId, privateLayout};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P;
				finderArgs = new Object[] {
					groupId, privateLayout, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PRIVATELAYOUT_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_First(
			long groupId, boolean privateLayout,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_First(
			groupId, privateLayout, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_First(
		long groupId, boolean privateLayout,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_P(
			groupId, privateLayout, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_Last(
			long groupId, boolean privateLayout,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_Last(
			groupId, privateLayout, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_Last(
		long groupId, boolean privateLayout,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByG_P(groupId, privateLayout);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByG_P(
			groupId, privateLayout, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByG_P_PrevAndNext(
			long plid, long groupId, boolean privateLayout,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByG_P_PrevAndNext(
				session, layout, groupId, privateLayout, orderByComparator,
				true);

			array[1] = layout;

			array[2] = getByG_P_PrevAndNext(
				session, layout, groupId, privateLayout, orderByComparator,
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

	protected Layout getByG_P_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_PRIVATELAYOUT_2);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P(long groupId, boolean privateLayout) {
		return filterFindByG_P(
			groupId, privateLayout, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P(
		long groupId, boolean privateLayout, int start, int end) {

		return filterFindByG_P(groupId, privateLayout, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P(
				groupId, privateLayout, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P(
					groupId, privateLayout, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_PRIVATELAYOUT_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			return (List<Layout>)QueryUtil.list(
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
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] filterFindByG_P_PrevAndNext(
			long plid, long groupId, boolean privateLayout,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_PrevAndNext(
				plid, groupId, privateLayout, orderByComparator);
		}

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = filterGetByG_P_PrevAndNext(
				session, layout, groupId, privateLayout, orderByComparator,
				true);

			array[1] = layout;

			array[2] = filterGetByG_P_PrevAndNext(
				session, layout, groupId, privateLayout, orderByComparator,
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

	protected Layout filterGetByG_P_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_PRIVATELAYOUT_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 */
	@Override
	public void removeByG_P(long groupId, boolean privateLayout) {
		for (Layout layout :
				findByG_P(
					groupId, privateLayout, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P(long groupId, boolean privateLayout) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByG_P;

			Object[] finderArgs = new Object[] {groupId, privateLayout};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_PRIVATELAYOUT_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

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

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, boolean privateLayout) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P(groupId, privateLayout);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_P(groupId, privateLayout);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_PRIVATELAYOUT_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

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

	private static final String _FINDER_COLUMN_G_P_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_GROUPID_2_SQL =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_PRIVATELAYOUT_2_SQL =
		"layout.privateLayout = ? AND layout.system_ = [$FALSE$]";

	private FinderPath _finderPathWithPaginationFindByG_T;
	private FinderPath _finderPathWithoutPaginationFindByG_T;
	private FinderPath _finderPathCountByG_T;

	/**
	 * Returns all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_T(long groupId, String type) {
		return findByG_T(
			groupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_T(
		long groupId, String type, int start, int end) {

		return findByG_T(groupId, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_T(groupId, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_T;
					finderArgs = new Object[] {groupId, type};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_T;
				finderArgs = new Object[] {
					groupId, type, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							!type.equals(layout.getType())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_T_TYPE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindType) {
						queryPos.add(type);
					}

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_T_First(
			long groupId, String type,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_T_First(groupId, type, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_T_First(
		long groupId, String type,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_T(groupId, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_T_Last(
			long groupId, String type,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_T_Last(groupId, type, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_T_Last(
		long groupId, String type,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByG_T(groupId, type);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByG_T(
			groupId, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByG_T_PrevAndNext(
			long plid, long groupId, String type,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		type = Objects.toString(type, "");

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByG_T_PrevAndNext(
				session, layout, groupId, type, orderByComparator, true);

			array[1] = layout;

			array[2] = getByG_T_PrevAndNext(
				session, layout, groupId, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout getByG_T_PrevAndNext(
		Session session, Layout layout, long groupId, String type,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_T_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_T_TYPE_2);
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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_T(long groupId, String type) {
		return filterFindByG_T(
			groupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_T(
		long groupId, String type, int start, int end) {

		return filterFindByG_T(groupId, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_T(groupId, type, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_T(
					groupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					orderByComparator),
				groupId);
		}

		type = Objects.toString(type, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2_SQL);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindType) {
				queryPos.add(type);
			}

			return (List<Layout>)QueryUtil.list(
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
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] filterFindByG_T_PrevAndNext(
			long plid, long groupId, String type,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_T_PrevAndNext(
				plid, groupId, type, orderByComparator);
		}

		type = Objects.toString(type, "");

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = filterGetByG_T_PrevAndNext(
				session, layout, groupId, type, orderByComparator, true);

			array[1] = layout;

			array[2] = filterGetByG_T_PrevAndNext(
				session, layout, groupId, type, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout filterGetByG_T_PrevAndNext(
		Session session, Layout layout, long groupId, String type,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2_SQL);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	@Override
	public void removeByG_T(long groupId, String type) {
		for (Layout layout :
				findByG_T(
					groupId, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_T(long groupId, String type) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = _finderPathCountByG_T;

			Object[] finderArgs = new Object[] {groupId, type};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_T_GROUPID_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_T_TYPE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					if (bindType) {
						queryPos.add(type);
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

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_T(long groupId, String type) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_T(groupId, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_T(groupId, type);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		type = Objects.toString(type, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_T_GROUPID_2_SQL);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_T_TYPE_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			if (bindType) {
				queryPos.add(type);
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

	private static final String _FINDER_COLUMN_G_T_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_T_GROUPID_2_SQL =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_T_TYPE_2 =
		"layout.type = ? AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_T_TYPE_3 =
		"(layout.type IS NULL OR layout.type = '') AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_T_TYPE_2_SQL =
		"layout.type_ = ? AND layout.system_ = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_T_TYPE_3_SQL =
		"(layout.type_ IS NULL OR layout.type_ = '') AND layout.system_ = [$FALSE$]";

	private FinderPath _finderPathWithPaginationFindByG_MLP;
	private FinderPath _finderPathWithoutPaginationFindByG_MLP;
	private FinderPath _finderPathCountByG_MLP;

	/**
	 * Returns all the layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_MLP(long groupId, long masterLayoutPlid) {
		return findByG_MLP(
			groupId, masterLayoutPlid, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end) {

		return findByG_MLP(groupId, masterLayoutPlid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_MLP(
			groupId, masterLayoutPlid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_MLP;
					finderArgs = new Object[] {groupId, masterLayoutPlid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_MLP;
				finderArgs = new Object[] {
					groupId, masterLayoutPlid, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(masterLayoutPlid !=
								layout.getMasterLayoutPlid())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_MLP_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_MLP_MASTERLAYOUTPLID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(masterLayoutPlid);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_MLP_First(
			long groupId, long masterLayoutPlid,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_MLP_First(
			groupId, masterLayoutPlid, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", masterLayoutPlid=");
		sb.append(masterLayoutPlid);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_MLP_First(
		long groupId, long masterLayoutPlid,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_MLP(
			groupId, masterLayoutPlid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_MLP_Last(
			long groupId, long masterLayoutPlid,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_MLP_Last(
			groupId, masterLayoutPlid, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", masterLayoutPlid=");
		sb.append(masterLayoutPlid);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_MLP_Last(
		long groupId, long masterLayoutPlid,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByG_MLP(groupId, masterLayoutPlid);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByG_MLP(
			groupId, masterLayoutPlid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByG_MLP_PrevAndNext(
			long plid, long groupId, long masterLayoutPlid,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByG_MLP_PrevAndNext(
				session, layout, groupId, masterLayoutPlid, orderByComparator,
				true);

			array[1] = layout;

			array[2] = getByG_MLP_PrevAndNext(
				session, layout, groupId, masterLayoutPlid, orderByComparator,
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

	protected Layout getByG_MLP_PrevAndNext(
		Session session, Layout layout, long groupId, long masterLayoutPlid,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_MLP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_MLP_MASTERLAYOUTPLID_2);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(masterLayoutPlid);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_MLP(long groupId, long masterLayoutPlid) {
		return filterFindByG_MLP(
			groupId, masterLayoutPlid, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end) {

		return filterFindByG_MLP(groupId, masterLayoutPlid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_MLP(
				groupId, masterLayoutPlid, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_MLP(
					groupId, masterLayoutPlid, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_MLP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_MLP_MASTERLAYOUTPLID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(masterLayoutPlid);

			return (List<Layout>)QueryUtil.list(
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
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] filterFindByG_MLP_PrevAndNext(
			long plid, long groupId, long masterLayoutPlid,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_MLP_PrevAndNext(
				plid, groupId, masterLayoutPlid, orderByComparator);
		}

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = filterGetByG_MLP_PrevAndNext(
				session, layout, groupId, masterLayoutPlid, orderByComparator,
				true);

			array[1] = layout;

			array[2] = filterGetByG_MLP_PrevAndNext(
				session, layout, groupId, masterLayoutPlid, orderByComparator,
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

	protected Layout filterGetByG_MLP_PrevAndNext(
		Session session, Layout layout, long groupId, long masterLayoutPlid,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_MLP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_MLP_MASTERLAYOUTPLID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(masterLayoutPlid);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where groupId = &#63; and masterLayoutPlid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 */
	@Override
	public void removeByG_MLP(long groupId, long masterLayoutPlid) {
		for (Layout layout :
				findByG_MLP(
					groupId, masterLayoutPlid, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_MLP(long groupId, long masterLayoutPlid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByG_MLP;

			Object[] finderArgs = new Object[] {groupId, masterLayoutPlid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_MLP_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_MLP_MASTERLAYOUTPLID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(masterLayoutPlid);

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

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_MLP(long groupId, long masterLayoutPlid) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_MLP(groupId, masterLayoutPlid);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_MLP(groupId, masterLayoutPlid);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_MLP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_MLP_MASTERLAYOUTPLID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(masterLayoutPlid);

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

	private static final String _FINDER_COLUMN_G_MLP_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_MLP_MASTERLAYOUTPLID_2 =
		"layout.masterLayoutPlid = ?";

	private FinderPath _finderPathWithPaginationFindByC_L;
	private FinderPath _finderPathWithoutPaginationFindByC_L;
	private FinderPath _finderPathCountByC_L;

	/**
	 * Returns all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByC_L(long companyId, String layoutPrototypeUuid) {
		return findByC_L(
			companyId, layoutPrototypeUuid, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByC_L(
		long companyId, String layoutPrototypeUuid, int start, int end) {

		return findByC_L(companyId, layoutPrototypeUuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByC_L(
		long companyId, String layoutPrototypeUuid, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByC_L(
			companyId, layoutPrototypeUuid, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByC_L(
		long companyId, String layoutPrototypeUuid, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			layoutPrototypeUuid = Objects.toString(layoutPrototypeUuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_L;
					finderArgs = new Object[] {companyId, layoutPrototypeUuid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_L;
				finderArgs = new Object[] {
					companyId, layoutPrototypeUuid, start, end,
					orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((companyId != layout.getCompanyId()) ||
							!layoutPrototypeUuid.equals(
								layout.getLayoutPrototypeUuid())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_C_L_COMPANYID_2);

				boolean bindLayoutPrototypeUuid = false;

				if (layoutPrototypeUuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_L_LAYOUTPROTOTYPEUUID_3);
				}
				else {
					bindLayoutPrototypeUuid = true;

					sb.append(_FINDER_COLUMN_C_L_LAYOUTPROTOTYPEUUID_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindLayoutPrototypeUuid) {
						queryPos.add(layoutPrototypeUuid);
					}

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByC_L_First(
			long companyId, String layoutPrototypeUuid,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByC_L_First(
			companyId, layoutPrototypeUuid, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", layoutPrototypeUuid=");
		sb.append(layoutPrototypeUuid);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByC_L_First(
		long companyId, String layoutPrototypeUuid,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByC_L(
			companyId, layoutPrototypeUuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByC_L_Last(
			long companyId, String layoutPrototypeUuid,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByC_L_Last(
			companyId, layoutPrototypeUuid, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", layoutPrototypeUuid=");
		sb.append(layoutPrototypeUuid);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByC_L_Last(
		long companyId, String layoutPrototypeUuid,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByC_L(companyId, layoutPrototypeUuid);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByC_L(
			companyId, layoutPrototypeUuid, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByC_L_PrevAndNext(
			long plid, long companyId, String layoutPrototypeUuid,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		layoutPrototypeUuid = Objects.toString(layoutPrototypeUuid, "");

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByC_L_PrevAndNext(
				session, layout, companyId, layoutPrototypeUuid,
				orderByComparator, true);

			array[1] = layout;

			array[2] = getByC_L_PrevAndNext(
				session, layout, companyId, layoutPrototypeUuid,
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

	protected Layout getByC_L_PrevAndNext(
		Session session, Layout layout, long companyId,
		String layoutPrototypeUuid, OrderByComparator<Layout> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_C_L_COMPANYID_2);

		boolean bindLayoutPrototypeUuid = false;

		if (layoutPrototypeUuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_L_LAYOUTPROTOTYPEUUID_3);
		}
		else {
			bindLayoutPrototypeUuid = true;

			sb.append(_FINDER_COLUMN_C_L_LAYOUTPROTOTYPEUUID_2);
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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindLayoutPrototypeUuid) {
			queryPos.add(layoutPrototypeUuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 */
	@Override
	public void removeByC_L(long companyId, String layoutPrototypeUuid) {
		for (Layout layout :
				findByC_L(
					companyId, layoutPrototypeUuid, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @return the number of matching layouts
	 */
	@Override
	public int countByC_L(long companyId, String layoutPrototypeUuid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			layoutPrototypeUuid = Objects.toString(layoutPrototypeUuid, "");

			FinderPath finderPath = _finderPathCountByC_L;

			Object[] finderArgs = new Object[] {companyId, layoutPrototypeUuid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_C_L_COMPANYID_2);

				boolean bindLayoutPrototypeUuid = false;

				if (layoutPrototypeUuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_L_LAYOUTPROTOTYPEUUID_3);
				}
				else {
					bindLayoutPrototypeUuid = true;

					sb.append(_FINDER_COLUMN_C_L_LAYOUTPROTOTYPEUUID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindLayoutPrototypeUuid) {
						queryPos.add(layoutPrototypeUuid);
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

	private static final String _FINDER_COLUMN_C_L_COMPANYID_2 =
		"layout.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_L_LAYOUTPROTOTYPEUUID_2 =
		"layout.layoutPrototypeUuid = ? AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_C_L_LAYOUTPROTOTYPEUUID_3 =
		"(layout.layoutPrototypeUuid IS NULL OR layout.layoutPrototypeUuid = '') AND layout.system = [$FALSE$]";

	private FinderPath _finderPathWithPaginationFindByP_I;
	private FinderPath _finderPathWithoutPaginationFindByP_I;
	private FinderPath _finderPathCountByP_I;

	/**
	 * Returns all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByP_I(boolean privateLayout, long iconImageId) {
		return findByP_I(
			privateLayout, iconImageId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByP_I(
		boolean privateLayout, long iconImageId, int start, int end) {

		return findByP_I(privateLayout, iconImageId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByP_I(
		boolean privateLayout, long iconImageId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByP_I(
			privateLayout, iconImageId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByP_I(
		boolean privateLayout, long iconImageId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByP_I;
					finderArgs = new Object[] {privateLayout, iconImageId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByP_I;
				finderArgs = new Object[] {
					privateLayout, iconImageId, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((privateLayout != layout.isPrivateLayout()) ||
							(iconImageId != layout.getIconImageId())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_P_I_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_P_I_ICONIMAGEID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(privateLayout);

					queryPos.add(iconImageId);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByP_I_First(
			boolean privateLayout, long iconImageId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByP_I_First(
			privateLayout, iconImageId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("privateLayout=");
		sb.append(privateLayout);

		sb.append(", iconImageId=");
		sb.append(iconImageId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByP_I_First(
		boolean privateLayout, long iconImageId,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByP_I(
			privateLayout, iconImageId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByP_I_Last(
			boolean privateLayout, long iconImageId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByP_I_Last(
			privateLayout, iconImageId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("privateLayout=");
		sb.append(privateLayout);

		sb.append(", iconImageId=");
		sb.append(iconImageId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByP_I_Last(
		boolean privateLayout, long iconImageId,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByP_I(privateLayout, iconImageId);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByP_I(
			privateLayout, iconImageId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByP_I_PrevAndNext(
			long plid, boolean privateLayout, long iconImageId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByP_I_PrevAndNext(
				session, layout, privateLayout, iconImageId, orderByComparator,
				true);

			array[1] = layout;

			array[2] = getByP_I_PrevAndNext(
				session, layout, privateLayout, iconImageId, orderByComparator,
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

	protected Layout getByP_I_PrevAndNext(
		Session session, Layout layout, boolean privateLayout, long iconImageId,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_P_I_PRIVATELAYOUT_2);

		sb.append(_FINDER_COLUMN_P_I_ICONIMAGEID_2);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(privateLayout);

		queryPos.add(iconImageId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where privateLayout = &#63; and iconImageId = &#63; from the database.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 */
	@Override
	public void removeByP_I(boolean privateLayout, long iconImageId) {
		for (Layout layout :
				findByP_I(
					privateLayout, iconImageId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByP_I(boolean privateLayout, long iconImageId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByP_I;

			Object[] finderArgs = new Object[] {privateLayout, iconImageId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_P_I_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_P_I_ICONIMAGEID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(privateLayout);

					queryPos.add(iconImageId);

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

	private static final String _FINDER_COLUMN_P_I_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_P_I_ICONIMAGEID_2 =
		"layout.iconImageId = ?";

	private FinderPath _finderPathWithPaginationFindByC_C;
	private FinderPath _finderPathWithoutPaginationFindByC_C;
	private FinderPath _finderPathCountByC_C;
	private FinderPath _finderPathWithPaginationCountByC_C;

	/**
	 * Returns all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByC_C(long classNameId, long classPK) {
		return findByC_C(
			classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return findByC_C(classNameId, classPK, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByC_C(
			classNameId, classPK, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_C;
					finderArgs = new Object[] {classNameId, classPK};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_C;
				finderArgs = new Object[] {
					classNameId, classPK, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((classNameId != layout.getClassNameId()) ||
							(classPK != layout.getClassPK())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_CLASSPK_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					queryPos.add(classPK);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByC_C_First(
			classNameId, classPK, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByC_C(
			classNameId, classPK, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByC_C_Last(
			long classNameId, long classPK,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByC_C_Last(
			classNameId, classPK, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByC_C_Last(
		long classNameId, long classPK,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByC_C(classNameId, classPK);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByC_C(
			classNameId, classPK, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByC_C_PrevAndNext(
			long plid, long classNameId, long classPK,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByC_C_PrevAndNext(
				session, layout, classNameId, classPK, orderByComparator, true);

			array[1] = layout;

			array[2] = getByC_C_PrevAndNext(
				session, layout, classNameId, classPK, orderByComparator,
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

	protected Layout getByC_C_PrevAndNext(
		Session session, Layout layout, long classNameId, long classPK,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

		sb.append(_FINDER_COLUMN_C_C_CLASSPK_2);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByC_C(long classNameId, long[] classPKs) {
		return findByC_C(
			classNameId, classPKs, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByC_C(
		long classNameId, long[] classPKs, int start, int end) {

		return findByC_C(classNameId, classPKs, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByC_C(
		long classNameId, long[] classPKs, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByC_C(
			classNameId, classPKs, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByC_C(
		long classNameId, long[] classPKs, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		if (classPKs == null) {
			classPKs = new long[0];
		}
		else if (classPKs.length > 1) {
			classPKs = ArrayUtil.sortedUnique(classPKs);
		}

		if (classPKs.length == 1) {
			return findByC_C(
				classNameId, classPKs[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						classNameId, StringUtil.merge(classPKs)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					classNameId, StringUtil.merge(classPKs), start, end,
					orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByC_C, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((classNameId != layout.getClassNameId()) ||
							!ArrayUtil.contains(
								classPKs, layout.getClassPK())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				if (classPKs.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_C_C_CLASSPK_7);

					sb.append(StringUtil.merge(classPKs));

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
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					list = (List<Layout>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByC_C, finderArgs,
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
	 * Removes all the layouts where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		for (Layout layout :
				findByC_C(
					classNameId, classPK, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching layouts
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByC_C;

			Object[] finderArgs = new Object[] {classNameId, classPK};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				sb.append(_FINDER_COLUMN_C_C_CLASSPK_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

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

	/**
	 * Returns the number of layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the number of matching layouts
	 */
	@Override
	public int countByC_C(long classNameId, long[] classPKs) {
		if (classPKs == null) {
			classPKs = new long[0];
		}
		else if (classPKs.length > 1) {
			classPKs = ArrayUtil.sortedUnique(classPKs);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = new Object[] {
				classNameId, StringUtil.merge(classPKs)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByC_C, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_C_C_CLASSNAMEID_2);

				if (classPKs.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_C_C_CLASSPK_7);

					sb.append(StringUtil.merge(classPKs));

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

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(classNameId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByC_C, finderArgs, count);
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

	private static final String _FINDER_COLUMN_C_C_CLASSNAMEID_2 =
		"layout.classNameId = ? AND ";

	private static final String _FINDER_COLUMN_C_C_CLASSPK_2 =
		"layout.classPK = ?";

	private static final String _FINDER_COLUMN_C_C_CLASSPK_7 =
		"layout.classPK IN (";

	private FinderPath _finderPathFetchByG_P_L;

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_L(
			long groupId, boolean privateLayout, long layoutId)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_L(groupId, privateLayout, layoutId);

		if (layout == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", privateLayout=");
			sb.append(privateLayout);

			sb.append(", layoutId=");
			sb.append(layoutId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchLayoutException(sb.toString());
		}

		return layout;
	}

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_L(
		long groupId, boolean privateLayout, long layoutId) {

		return fetchByG_P_L(groupId, privateLayout, layoutId, true);
	}

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_L(
		long groupId, boolean privateLayout, long layoutId,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, privateLayout, layoutId};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByG_P_L, finderArgs, this);
			}

			if (result instanceof Layout) {
				Layout layout = (Layout)result;

				if ((groupId != layout.getGroupId()) ||
					(privateLayout != layout.isPrivateLayout()) ||
					(layoutId != layout.getLayoutId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_L_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_L_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_L_LAYOUTID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(layoutId);

					List<Layout> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByG_P_L, finderArgs, list);
						}
					}
					else {
						Layout layout = list.get(0);

						result = layout;

						cacheResult(layout);
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
				return (Layout)result;
			}
		}
	}

	/**
	 * Removes the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the layout that was removed
	 */
	@Override
	public Layout removeByG_P_L(
			long groupId, boolean privateLayout, long layoutId)
		throws NoSuchLayoutException {

		Layout layout = findByG_P_L(groupId, privateLayout, layoutId);

		return remove(layout);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and layoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_L(
		long groupId, boolean privateLayout, long layoutId) {

		Layout layout = fetchByG_P_L(groupId, privateLayout, layoutId);

		if (layout == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_P_L_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_L_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_L_LAYOUTID_2 =
		"layout.layoutId = ?";

	private FinderPath _finderPathWithPaginationFindByG_P_P;
	private FinderPath _finderPathWithoutPaginationFindByG_P_P;
	private FinderPath _finderPathCountByG_P_P;
	private FinderPath _finderPathWithPaginationCountByG_P_P;

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return findByG_P_P(
			groupId, privateLayout, parentLayoutId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end) {

		return findByG_P_P(
			groupId, privateLayout, parentLayoutId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		return findByG_P_P(
			groupId, privateLayout, parentLayoutId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_P;
					finderArgs = new Object[] {
						groupId, privateLayout, parentLayoutId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_P;
				finderArgs = new Object[] {
					groupId, privateLayout, parentLayoutId, start, end,
					orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							(parentLayoutId != layout.getParentLayoutId())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_P_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_P_First(
			groupId, privateLayout, parentLayoutId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", parentLayoutId=");
		sb.append(parentLayoutId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_P_First(
		long groupId, boolean privateLayout, long parentLayoutId,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_P_P(
			groupId, privateLayout, parentLayoutId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_P_Last(
			long groupId, boolean privateLayout, long parentLayoutId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_P_Last(
			groupId, privateLayout, parentLayoutId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", parentLayoutId=");
		sb.append(parentLayoutId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_P_Last(
		long groupId, boolean privateLayout, long parentLayoutId,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByG_P_P(groupId, privateLayout, parentLayoutId);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByG_P_P(
			groupId, privateLayout, parentLayoutId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByG_P_P_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByG_P_P_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId,
				orderByComparator, true);

			array[1] = layout;

			array[2] = getByG_P_P_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId,
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

	protected Layout getByG_P_P_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		long parentLayoutId, OrderByComparator<Layout> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2);

		sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		queryPos.add(parentLayoutId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return filterFindByG_P_P(
			groupId, privateLayout, parentLayoutId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end) {

		return filterFindByG_P_P(
			groupId, privateLayout, parentLayoutId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P(
				groupId, privateLayout, parentLayoutId, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P(
					groupId, privateLayout, parentLayoutId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			return (List<Layout>)QueryUtil.list(
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
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] filterFindByG_P_P_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_PrevAndNext(
				plid, groupId, privateLayout, parentLayoutId,
				orderByComparator);
		}

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = filterGetByG_P_P_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId,
				orderByComparator, true);

			array[1] = layout;

			array[2] = filterGetByG_P_P_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId,
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

	protected Layout filterGetByG_P_P_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		long parentLayoutId, OrderByComparator<Layout> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		queryPos.add(parentLayoutId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		return filterFindByG_P_P(
			groupId, privateLayout, parentLayoutIds, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end) {

		return filterFindByG_P_P(
			groupId, privateLayout, parentLayoutIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P(
				groupId, privateLayout, parentLayoutIds, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P(
					groupId, privateLayout, parentLayoutIds, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2_SQL);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_7_SQL);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system_ = [$FALSE$]");

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			return (List<Layout>)QueryUtil.list(
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
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		return findByG_P_P(
			groupId, privateLayout, parentLayoutIds, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end) {

		return findByG_P_P(
			groupId, privateLayout, parentLayoutIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		return findByG_P_P(
			groupId, privateLayout, parentLayoutIds, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		if (parentLayoutIds.length == 1) {
			return findByG_P_P(
				groupId, privateLayout, parentLayoutIds[0], start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, privateLayout,
						StringUtil.merge(parentLayoutIds)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, privateLayout, StringUtil.merge(parentLayoutIds),
					start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_P_P, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							!ArrayUtil.contains(
								parentLayoutIds, layout.getParentLayoutId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				try {
					if ((start == QueryUtil.ALL_POS) &&
						(end == QueryUtil.ALL_POS) &&
						(databaseInMaxParameters > 0) &&
						(parentLayoutIds.length > databaseInMaxParameters)) {

						list = new ArrayList<Layout>();

						long[][] parentLayoutIdsPages =
							(long[][])ArrayUtil.split(
								parentLayoutIds, databaseInMaxParameters);

						for (long[] parentLayoutIdsPage :
								parentLayoutIdsPages) {

							list.addAll(
								_findByG_P_P(
									groupId, privateLayout, parentLayoutIdsPage,
									start, end, orderByComparator));
						}

						Collections.sort(list, orderByComparator);

						list = Collections.unmodifiableList(list);
					}
					else {
						list = _findByG_P_P(
							groupId, privateLayout, parentLayoutIds, start, end,
							orderByComparator);
					}

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_P_P, finderArgs,
							list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return list;
		}
	}

	private List<Layout> _findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system = [$FALSE$]");

		if (orderByComparator != null) {
			appendOrderByComparator(
				sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
		}
		else {
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			list = (List<Layout>)QueryUtil.list(
				query, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return list;
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 */
	@Override
	public void removeByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		for (Layout layout :
				findByG_P_P(
					groupId, privateLayout, parentLayoutId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByG_P_P;

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, parentLayoutId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

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

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, StringUtil.merge(parentLayoutIds)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_P_P, finderArgs, this);

			if (count == null) {
				try {
					if ((databaseInMaxParameters > 0) &&
						(parentLayoutIds.length > databaseInMaxParameters)) {

						count = Long.valueOf(0);

						long[][] parentLayoutIdsPages =
							(long[][])ArrayUtil.split(
								parentLayoutIds, databaseInMaxParameters);

						for (long[] parentLayoutIdsPage :
								parentLayoutIdsPages) {

							count += Long.valueOf(
								_countByG_P_P(
									groupId, privateLayout,
									parentLayoutIdsPage));
						}
					}
					else {
						count = Long.valueOf(
							_countByG_P_P(
								groupId, privateLayout, parentLayoutIds));
					}

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_P_P, finderArgs,
						count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return count.intValue();
		}
	}

	private int _countByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		Long count = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system = [$FALSE$]");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			count = (Long)query.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return count.intValue();
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P(groupId, privateLayout, parentLayoutId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_P_P(
				groupId, privateLayout, parentLayoutId);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

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
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P(groupId, privateLayout, parentLayoutIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = InlineSQLHelperUtil.filter(
				findByG_P_P(groupId, privateLayout, parentLayoutIds), groupId);

			return layouts.size();
		}

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2_SQL);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_7_SQL);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system_ = [$FALSE$]");

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

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

	private static final String _FINDER_COLUMN_G_P_P_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_GROUPID_2_SQL =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2_SQL =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2 =
		"layout.parentLayoutId = ? AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_P_PARENTLAYOUTID_7 =
		"layout.parentLayoutId IN (";

	private static final String _FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2_SQL =
		"layout.parentLayoutId = ? AND layout.system_ = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_P_PARENTLAYOUTID_7_SQL =
		"layout.parentLayoutId IN (";

	private FinderPath _finderPathWithPaginationFindByG_P_T;
	private FinderPath _finderPathWithoutPaginationFindByG_P_T;
	private FinderPath _finderPathCountByG_P_T;
	private FinderPath _finderPathWithPaginationCountByG_P_T;

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type) {

		return findByG_P_T(
			groupId, privateLayout, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end) {

		return findByG_P_T(groupId, privateLayout, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_P_T(
			groupId, privateLayout, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_T;
					finderArgs = new Object[] {groupId, privateLayout, type};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_T;
				finderArgs = new Object[] {
					groupId, privateLayout, type, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							!type.equals(layout.getType())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_T_PRIVATELAYOUT_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_P_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_P_T_TYPE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					if (bindType) {
						queryPos.add(type);
					}

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_T_First(
			long groupId, boolean privateLayout, String type,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_T_First(
			groupId, privateLayout, type, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_T_First(
		long groupId, boolean privateLayout, String type,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_P_T(
			groupId, privateLayout, type, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_T_Last(
			long groupId, boolean privateLayout, String type,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_T_Last(
			groupId, privateLayout, type, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_T_Last(
		long groupId, boolean privateLayout, String type,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByG_P_T(groupId, privateLayout, type);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByG_P_T(
			groupId, privateLayout, type, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByG_P_T_PrevAndNext(
			long plid, long groupId, boolean privateLayout, String type,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		type = Objects.toString(type, "");

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByG_P_T_PrevAndNext(
				session, layout, groupId, privateLayout, type,
				orderByComparator, true);

			array[1] = layout;

			array[2] = getByG_P_T_PrevAndNext(
				session, layout, groupId, privateLayout, type,
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

	protected Layout getByG_P_T_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		String type, OrderByComparator<Layout> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_T_PRIVATELAYOUT_2);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_P_T_TYPE_3);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_P_T_TYPE_2);
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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String type) {

		return filterFindByG_P_T(
			groupId, privateLayout, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end) {

		return filterFindByG_P_T(
			groupId, privateLayout, type, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_T(
				groupId, privateLayout, type, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_T(
					groupId, privateLayout, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		type = Objects.toString(type, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_T_PRIVATELAYOUT_2_SQL);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_P_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_P_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			if (bindType) {
				queryPos.add(type);
			}

			return (List<Layout>)QueryUtil.list(
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
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] filterFindByG_P_T_PrevAndNext(
			long plid, long groupId, boolean privateLayout, String type,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_T_PrevAndNext(
				plid, groupId, privateLayout, type, orderByComparator);
		}

		type = Objects.toString(type, "");

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = filterGetByG_P_T_PrevAndNext(
				session, layout, groupId, privateLayout, type,
				orderByComparator, true);

			array[1] = layout;

			array[2] = filterGetByG_P_T_PrevAndNext(
				session, layout, groupId, privateLayout, type,
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

	protected Layout filterGetByG_P_T_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		String type, OrderByComparator<Layout> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_T_PRIVATELAYOUT_2_SQL);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_P_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_P_T_TYPE_2_SQL);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		if (bindType) {
			queryPos.add(type);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String[] types) {

		return filterFindByG_P_T(
			groupId, privateLayout, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start,
		int end) {

		return filterFindByG_P_T(
			groupId, privateLayout, types, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_T(
				groupId, privateLayout, types, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_T(
					groupId, privateLayout, types, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_T_PRIVATELAYOUT_2_SQL);

		if (types.length > 0) {
			sb.append("(");

			for (int i = 0; i < types.length; i++) {
				String type = types[i];

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_P_T_TYPE_6_SQL);
				}
				else {
					sb.append(_FINDER_COLUMN_G_P_T_TYPE_5_SQL);
				}

				if ((i + 1) < types.length) {
					sb.append(WHERE_OR);
				}
			}

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system_ = [$FALSE$]");

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			for (String type : types) {
				if ((type != null) && !type.isEmpty()) {
					queryPos.add(type);
				}
			}

			return (List<Layout>)QueryUtil.list(
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
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types) {

		return findByG_P_T(
			groupId, privateLayout, types, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start,
		int end) {

		return findByG_P_T(groupId, privateLayout, types, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_P_T(
			groupId, privateLayout, types, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		if (types.length == 1) {
			return findByG_P_T(
				groupId, privateLayout, types[0], start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, privateLayout, StringUtil.merge(types)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, privateLayout, StringUtil.merge(types), start, end,
					orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_P_T, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							!ArrayUtil.contains(types, layout.getType())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_T_PRIVATELAYOUT_2);

				if (types.length > 0) {
					sb.append("(");

					for (int i = 0; i < types.length; i++) {
						String type = types[i];

						if (type.isEmpty()) {
							sb.append(_FINDER_COLUMN_G_P_T_TYPE_6);
						}
						else {
							sb.append(_FINDER_COLUMN_G_P_T_TYPE_5);
						}

						if ((i + 1) < types.length) {
							sb.append(WHERE_OR);
						}
					}

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				sb.append(" AND layout.system = [$FALSE$]");

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					for (String type : types) {
						if ((type != null) && !type.isEmpty()) {
							queryPos.add(type);
						}
					}

					list = (List<Layout>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_P_T, finderArgs,
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
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 */
	@Override
	public void removeByG_P_T(
		long groupId, boolean privateLayout, String type) {

		for (Layout layout :
				findByG_P_T(
					groupId, privateLayout, type, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_T(long groupId, boolean privateLayout, String type) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			type = Objects.toString(type, "");

			FinderPath finderPath = _finderPathCountByG_P_T;

			Object[] finderArgs = new Object[] {groupId, privateLayout, type};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_T_PRIVATELAYOUT_2);

				boolean bindType = false;

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_P_T_TYPE_3);
				}
				else {
					bindType = true;

					sb.append(_FINDER_COLUMN_G_P_T_TYPE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					if (bindType) {
						queryPos.add(type);
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

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_T(
		long groupId, boolean privateLayout, String[] types) {

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, StringUtil.merge(types)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_P_T, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_T_PRIVATELAYOUT_2);

				if (types.length > 0) {
					sb.append("(");

					for (int i = 0; i < types.length; i++) {
						String type = types[i];

						if (type.isEmpty()) {
							sb.append(_FINDER_COLUMN_G_P_T_TYPE_6);
						}
						else {
							sb.append(_FINDER_COLUMN_G_P_T_TYPE_5);
						}

						if ((i + 1) < types.length) {
							sb.append(WHERE_OR);
						}
					}

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				sb.append(" AND layout.system = [$FALSE$]");

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					for (String type : types) {
						if ((type != null) && !type.isEmpty()) {
							queryPos.add(type);
						}
					}

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_P_T, finderArgs,
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
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_T(
		long groupId, boolean privateLayout, String type) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_T(groupId, privateLayout, type);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_P_T(groupId, privateLayout, type);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		type = Objects.toString(type, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_T_PRIVATELAYOUT_2_SQL);

		boolean bindType = false;

		if (type.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_P_T_TYPE_3_SQL);
		}
		else {
			bindType = true;

			sb.append(_FINDER_COLUMN_G_P_T_TYPE_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			if (bindType) {
				queryPos.add(type);
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

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_T(
		long groupId, boolean privateLayout, String[] types) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_T(groupId, privateLayout, types);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = InlineSQLHelperUtil.filter(
				findByG_P_T(groupId, privateLayout, types), groupId);

			return layouts.size();
		}

		if (types == null) {
			types = new String[0];
		}
		else if (types.length > 1) {
			for (int i = 0; i < types.length; i++) {
				types[i] = Objects.toString(types[i], "");
			}

			types = ArrayUtil.sortedUnique(types);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_T_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_T_PRIVATELAYOUT_2_SQL);

		if (types.length > 0) {
			sb.append("(");

			for (int i = 0; i < types.length; i++) {
				String type = types[i];

				if (type.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_P_T_TYPE_6_SQL);
				}
				else {
					sb.append(_FINDER_COLUMN_G_P_T_TYPE_5_SQL);
				}

				if ((i + 1) < types.length) {
					sb.append(WHERE_OR);
				}
			}

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system_ = [$FALSE$]");

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			for (String type : types) {
				if ((type != null) && !type.isEmpty()) {
					queryPos.add(type);
				}
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

	private static final String _FINDER_COLUMN_G_P_T_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_T_GROUPID_2_SQL =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_T_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_T_PRIVATELAYOUT_2_SQL =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_T_TYPE_2 =
		"layout.type = ? AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_T_TYPE_3 =
		"(layout.type IS NULL OR layout.type = '') AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_T_TYPE_5 =
		"(" + removeConjunction(_FINDER_COLUMN_G_P_T_TYPE_2) + ")";

	private static final String _FINDER_COLUMN_G_P_T_TYPE_6 =
		"(" + removeConjunction(_FINDER_COLUMN_G_P_T_TYPE_3) + ")";

	private static final String _FINDER_COLUMN_G_P_T_TYPE_2_SQL =
		"layout.type_ = ? AND layout.system_ = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_T_TYPE_3_SQL =
		"(layout.type_ IS NULL OR layout.type_ = '') AND layout.system_ = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_T_TYPE_5_SQL =
		"(" + removeConjunction(_FINDER_COLUMN_G_P_T_TYPE_2) + ")";

	private static final String _FINDER_COLUMN_G_P_T_TYPE_6_SQL =
		"(" + removeConjunction(_FINDER_COLUMN_G_P_T_TYPE_3) + ")";

	private FinderPath _finderPathFetchByG_P_F;

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_F(
			long groupId, boolean privateLayout, String friendlyURL)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_F(groupId, privateLayout, friendlyURL);

		if (layout == null) {
			StringBundler sb = new StringBundler(8);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", privateLayout=");
			sb.append(privateLayout);

			sb.append(", friendlyURL=");
			sb.append(friendlyURL);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchLayoutException(sb.toString());
		}

		return layout;
	}

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL) {

		return fetchByG_P_F(groupId, privateLayout, friendlyURL, true);
	}

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			friendlyURL = Objects.toString(friendlyURL, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {groupId, privateLayout, friendlyURL};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByG_P_F, finderArgs, this);
			}

			if (result instanceof Layout) {
				Layout layout = (Layout)result;

				if ((groupId != layout.getGroupId()) ||
					(privateLayout != layout.isPrivateLayout()) ||
					!Objects.equals(friendlyURL, layout.getFriendlyURL())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_F_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_F_PRIVATELAYOUT_2);

				boolean bindFriendlyURL = false;

				if (friendlyURL.isEmpty()) {
					sb.append(_FINDER_COLUMN_G_P_F_FRIENDLYURL_3);
				}
				else {
					bindFriendlyURL = true;

					sb.append(_FINDER_COLUMN_G_P_F_FRIENDLYURL_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					if (bindFriendlyURL) {
						queryPos.add(friendlyURL);
					}

					List<Layout> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByG_P_F, finderArgs, list);
						}
					}
					else {
						Layout layout = list.get(0);

						result = layout;

						cacheResult(layout);
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
				return (Layout)result;
			}
		}
	}

	/**
	 * Removes the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the layout that was removed
	 */
	@Override
	public Layout removeByG_P_F(
			long groupId, boolean privateLayout, String friendlyURL)
		throws NoSuchLayoutException {

		Layout layout = findByG_P_F(groupId, privateLayout, friendlyURL);

		return remove(layout);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL) {

		Layout layout = fetchByG_P_F(groupId, privateLayout, friendlyURL);

		if (layout == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_P_F_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_F_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_F_FRIENDLYURL_2 =
		"layout.friendlyURL = ?";

	private static final String _FINDER_COLUMN_G_P_F_FRIENDLYURL_3 =
		"(layout.friendlyURL IS NULL OR layout.friendlyURL = '')";

	private FinderPath _finderPathWithPaginationFindByG_P_LSPLE;
	private FinderPath _finderPathWithoutPaginationFindByG_P_LSPLE;
	private FinderPath _finderPathCountByG_P_LSPLE;

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		return findByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end) {

		return findByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		return findByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			layoutSetPrototypeLayoutERC = Objects.toString(
				layoutSetPrototypeLayoutERC, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_LSPLE;
					finderArgs = new Object[] {
						groupId, privateLayout, layoutSetPrototypeLayoutERC
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_LSPLE;
				finderArgs = new Object[] {
					groupId, privateLayout, layoutSetPrototypeLayoutERC, start,
					end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							!layoutSetPrototypeLayoutERC.equals(
								layout.getLayoutSetPrototypeLayoutERC())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_LSPLE_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_LSPLE_PRIVATELAYOUT_2);

				boolean bindLayoutSetPrototypeLayoutERC = false;

				if (layoutSetPrototypeLayoutERC.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_3);
				}
				else {
					bindLayoutSetPrototypeLayoutERC = true;

					sb.append(
						_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					if (bindLayoutSetPrototypeLayoutERC) {
						queryPos.add(layoutSetPrototypeLayoutERC);
					}

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_LSPLE_First(
			long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_LSPLE_First(
			groupId, privateLayout, layoutSetPrototypeLayoutERC,
			orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", layoutSetPrototypeLayoutERC=");
		sb.append(layoutSetPrototypeLayoutERC);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_LSPLE_First(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_LSPLE_Last(
			long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_LSPLE_Last(
			groupId, privateLayout, layoutSetPrototypeLayoutERC,
			orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", layoutSetPrototypeLayoutERC=");
		sb.append(layoutSetPrototypeLayoutERC);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_LSPLE_Last(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC, count - 1,
			count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByG_P_LSPLE_PrevAndNext(
			long plid, long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		layoutSetPrototypeLayoutERC = Objects.toString(
			layoutSetPrototypeLayoutERC, "");

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByG_P_LSPLE_PrevAndNext(
				session, layout, groupId, privateLayout,
				layoutSetPrototypeLayoutERC, orderByComparator, true);

			array[1] = layout;

			array[2] = getByG_P_LSPLE_PrevAndNext(
				session, layout, groupId, privateLayout,
				layoutSetPrototypeLayoutERC, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout getByG_P_LSPLE_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_LSPLE_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_LSPLE_PRIVATELAYOUT_2);

		boolean bindLayoutSetPrototypeLayoutERC = false;

		if (layoutSetPrototypeLayoutERC.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_3);
		}
		else {
			bindLayoutSetPrototypeLayoutERC = true;

			sb.append(_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_2);
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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		if (bindLayoutSetPrototypeLayoutERC) {
			queryPos.add(layoutSetPrototypeLayoutERC);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		return filterFindByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end) {

		return filterFindByG_P_LSPLE(
			groupId, privateLayout, layoutSetPrototypeLayoutERC, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_LSPLE(
				groupId, privateLayout, layoutSetPrototypeLayoutERC, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_LSPLE(
					groupId, privateLayout, layoutSetPrototypeLayoutERC,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		layoutSetPrototypeLayoutERC = Objects.toString(
			layoutSetPrototypeLayoutERC, "");

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_LSPLE_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_LSPLE_PRIVATELAYOUT_2);

		boolean bindLayoutSetPrototypeLayoutERC = false;

		if (layoutSetPrototypeLayoutERC.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_3);
		}
		else {
			bindLayoutSetPrototypeLayoutERC = true;

			sb.append(_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			if (bindLayoutSetPrototypeLayoutERC) {
				queryPos.add(layoutSetPrototypeLayoutERC);
			}

			return (List<Layout>)QueryUtil.list(
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
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] filterFindByG_P_LSPLE_PrevAndNext(
			long plid, long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_LSPLE_PrevAndNext(
				plid, groupId, privateLayout, layoutSetPrototypeLayoutERC,
				orderByComparator);
		}

		layoutSetPrototypeLayoutERC = Objects.toString(
			layoutSetPrototypeLayoutERC, "");

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = filterGetByG_P_LSPLE_PrevAndNext(
				session, layout, groupId, privateLayout,
				layoutSetPrototypeLayoutERC, orderByComparator, true);

			array[1] = layout;

			array[2] = filterGetByG_P_LSPLE_PrevAndNext(
				session, layout, groupId, privateLayout,
				layoutSetPrototypeLayoutERC, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout filterGetByG_P_LSPLE_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_LSPLE_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_LSPLE_PRIVATELAYOUT_2);

		boolean bindLayoutSetPrototypeLayoutERC = false;

		if (layoutSetPrototypeLayoutERC.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_3);
		}
		else {
			bindLayoutSetPrototypeLayoutERC = true;

			sb.append(_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		if (bindLayoutSetPrototypeLayoutERC) {
			queryPos.add(layoutSetPrototypeLayoutERC);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 */
	@Override
	public void removeByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		for (Layout layout :
				findByG_P_LSPLE(
					groupId, privateLayout, layoutSetPrototypeLayoutERC,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			layoutSetPrototypeLayoutERC = Objects.toString(
				layoutSetPrototypeLayoutERC, "");

			FinderPath finderPath = _finderPathCountByG_P_LSPLE;

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, layoutSetPrototypeLayoutERC
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_LSPLE_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_LSPLE_PRIVATELAYOUT_2);

				boolean bindLayoutSetPrototypeLayoutERC = false;

				if (layoutSetPrototypeLayoutERC.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_3);
				}
				else {
					bindLayoutSetPrototypeLayoutERC = true;

					sb.append(
						_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					if (bindLayoutSetPrototypeLayoutERC) {
						queryPos.add(layoutSetPrototypeLayoutERC);
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

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_LSPLE(
				groupId, privateLayout, layoutSetPrototypeLayoutERC);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_P_LSPLE(
				groupId, privateLayout, layoutSetPrototypeLayoutERC);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		layoutSetPrototypeLayoutERC = Objects.toString(
			layoutSetPrototypeLayoutERC, "");

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_LSPLE_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_LSPLE_PRIVATELAYOUT_2);

		boolean bindLayoutSetPrototypeLayoutERC = false;

		if (layoutSetPrototypeLayoutERC.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_3);
		}
		else {
			bindLayoutSetPrototypeLayoutERC = true;

			sb.append(_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			if (bindLayoutSetPrototypeLayoutERC) {
				queryPos.add(layoutSetPrototypeLayoutERC);
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

	private static final String _FINDER_COLUMN_G_P_LSPLE_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_LSPLE_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String
		_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_2 =
			"layout.layoutSetPrototypeLayoutERC = ?";

	private static final String
		_FINDER_COLUMN_G_P_LSPLE_LAYOUTSETPROTOTYPELAYOUTERC_3 =
			"(layout.layoutSetPrototypeLayoutERC IS NULL OR layout.layoutSetPrototypeLayoutERC = '')";

	private FinderPath _finderPathWithPaginationFindByG_P_ST;
	private FinderPath _finderPathWithoutPaginationFindByG_P_ST;
	private FinderPath _finderPathCountByG_P_ST;
	private FinderPath _finderPathWithPaginationCountByG_P_ST;

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status) {

		return findByG_P_ST(
			groupId, privateLayout, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end) {

		return findByG_P_ST(groupId, privateLayout, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_P_ST(
			groupId, privateLayout, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_ST;
					finderArgs = new Object[] {groupId, privateLayout, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_ST;
				finderArgs = new Object[] {
					groupId, privateLayout, status, start, end,
					orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							(status != layout.getStatus())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_ST_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_ST_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_ST_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(status);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_ST_First(
			long groupId, boolean privateLayout, int status,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_ST_First(
			groupId, privateLayout, status, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_ST_First(
		long groupId, boolean privateLayout, int status,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_P_ST(
			groupId, privateLayout, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_ST_Last(
			long groupId, boolean privateLayout, int status,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_ST_Last(
			groupId, privateLayout, status, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_ST_Last(
		long groupId, boolean privateLayout, int status,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByG_P_ST(groupId, privateLayout, status);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByG_P_ST(
			groupId, privateLayout, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByG_P_ST_PrevAndNext(
			long plid, long groupId, boolean privateLayout, int status,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByG_P_ST_PrevAndNext(
				session, layout, groupId, privateLayout, status,
				orderByComparator, true);

			array[1] = layout;

			array[2] = getByG_P_ST_PrevAndNext(
				session, layout, groupId, privateLayout, status,
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

	protected Layout getByG_P_ST_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		int status, OrderByComparator<Layout> orderByComparator,
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

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_ST_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_ST_PRIVATELAYOUT_2);

		sb.append(_FINDER_COLUMN_G_P_ST_STATUS_2);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int status) {

		return filterFindByG_P_ST(
			groupId, privateLayout, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end) {

		return filterFindByG_P_ST(
			groupId, privateLayout, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_ST(
				groupId, privateLayout, status, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_ST(
					groupId, privateLayout, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_ST_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_ST_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_ST_STATUS_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(status);

			return (List<Layout>)QueryUtil.list(
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
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] filterFindByG_P_ST_PrevAndNext(
			long plid, long groupId, boolean privateLayout, int status,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_ST_PrevAndNext(
				plid, groupId, privateLayout, status, orderByComparator);
		}

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = filterGetByG_P_ST_PrevAndNext(
				session, layout, groupId, privateLayout, status,
				orderByComparator, true);

			array[1] = layout;

			array[2] = filterGetByG_P_ST_PrevAndNext(
				session, layout, groupId, privateLayout, status,
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

	protected Layout filterGetByG_P_ST_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		int status, OrderByComparator<Layout> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_ST_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_ST_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_ST_STATUS_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses) {

		return filterFindByG_P_ST(
			groupId, privateLayout, statuses, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start,
		int end) {

		return filterFindByG_P_ST(
			groupId, privateLayout, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_ST(
				groupId, privateLayout, statuses, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_ST(
					groupId, privateLayout, statuses, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_ST_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_ST_PRIVATELAYOUT_2_SQL);

		if (statuses.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_ST_STATUS_7_SQL);

			sb.append(StringUtil.merge(statuses));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system_ = [$FALSE$]");

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			return (List<Layout>)QueryUtil.list(
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
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses) {

		return findByG_P_ST(
			groupId, privateLayout, statuses, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start,
		int end) {

		return findByG_P_ST(groupId, privateLayout, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_P_ST(
			groupId, privateLayout, statuses, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		if (statuses.length == 1) {
			return findByG_P_ST(
				groupId, privateLayout, statuses[0], start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, privateLayout, StringUtil.merge(statuses)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, privateLayout, StringUtil.merge(statuses), start,
					end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_P_ST, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							!ArrayUtil.contains(statuses, layout.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_ST_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_ST_PRIVATELAYOUT_2);

				if (statuses.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_P_ST_STATUS_7);

					sb.append(StringUtil.merge(statuses));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				sb.append(" AND layout.system = [$FALSE$]");

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					list = (List<Layout>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_P_ST, finderArgs,
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
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 */
	@Override
	public void removeByG_P_ST(
		long groupId, boolean privateLayout, int status) {

		for (Layout layout :
				findByG_P_ST(
					groupId, privateLayout, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_ST(long groupId, boolean privateLayout, int status) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByG_P_ST;

			Object[] finderArgs = new Object[] {groupId, privateLayout, status};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_ST_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_ST_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_ST_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(status);

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

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses) {

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, StringUtil.merge(statuses)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_P_ST, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_ST_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_ST_PRIVATELAYOUT_2);

				if (statuses.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_P_ST_STATUS_7);

					sb.append(StringUtil.merge(statuses));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				sb.append(" AND layout.system = [$FALSE$]");

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_P_ST, finderArgs,
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
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_ST(
		long groupId, boolean privateLayout, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_ST(groupId, privateLayout, status);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_P_ST(groupId, privateLayout, status);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_ST_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_ST_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_ST_STATUS_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(status);

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
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_ST(groupId, privateLayout, statuses);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = InlineSQLHelperUtil.filter(
				findByG_P_ST(groupId, privateLayout, statuses), groupId);

			return layouts.size();
		}

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_ST_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_ST_PRIVATELAYOUT_2_SQL);

		if (statuses.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_ST_STATUS_7_SQL);

			sb.append(StringUtil.merge(statuses));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system_ = [$FALSE$]");

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

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

	private static final String _FINDER_COLUMN_G_P_ST_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_ST_GROUPID_2_SQL =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_ST_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_ST_PRIVATELAYOUT_2_SQL =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_ST_STATUS_2 =
		"layout.status = ? AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_ST_STATUS_7 =
		"layout.status IN (";

	private static final String _FINDER_COLUMN_G_P_ST_STATUS_2_SQL =
		"layout.status = ? AND layout.system_ = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_ST_STATUS_7_SQL =
		"layout.status IN (";

	private FinderPath _finderPathWithPaginationFindByG_P_P_H;
	private FinderPath _finderPathWithoutPaginationFindByG_P_P_H;
	private FinderPath _finderPathCountByG_P_P_H;
	private FinderPath _finderPathWithPaginationCountByG_P_P_H;

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		return findByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end) {

		return findByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_P_H;
					finderArgs = new Object[] {
						groupId, privateLayout, parentLayoutId, hidden
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_P_H;
				finderArgs = new Object[] {
					groupId, privateLayout, parentLayoutId, hidden, start, end,
					orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							(parentLayoutId != layout.getParentLayoutId()) ||
							(hidden != layout.isHidden())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2);

				sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					queryPos.add(hidden);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_P_H_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_P_H_First(
			groupId, privateLayout, parentLayoutId, hidden, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", parentLayoutId=");
		sb.append(parentLayoutId);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_P_H_First(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_P_H_Last(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_P_H_Last(
			groupId, privateLayout, parentLayoutId, hidden, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", parentLayoutId=");
		sb.append(parentLayoutId);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_P_H_Last(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, OrderByComparator<Layout> orderByComparator) {

		int count = countByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByG_P_P_H_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByG_P_P_H_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId, hidden,
				orderByComparator, true);

			array[1] = layout;

			array[2] = getByG_P_P_H_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId, hidden,
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

	protected Layout getByG_P_P_H_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		long parentLayoutId, boolean hidden,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2);

		sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2);

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		queryPos.add(parentLayoutId);

		queryPos.add(hidden);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		return filterFindByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end) {

		return filterFindByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_H(
				groupId, privateLayout, parentLayoutId, hidden, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P_H(
					groupId, privateLayout, parentLayoutId, hidden,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			queryPos.add(hidden);

			return (List<Layout>)QueryUtil.list(
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
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] filterFindByG_P_P_H_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_H_PrevAndNext(
				plid, groupId, privateLayout, parentLayoutId, hidden,
				orderByComparator);
		}

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = filterGetByG_P_P_H_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId, hidden,
				orderByComparator, true);

			array[1] = layout;

			array[2] = filterGetByG_P_P_H_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId, hidden,
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

	protected Layout filterGetByG_P_P_H_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		long parentLayoutId, boolean hidden,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		queryPos.add(parentLayoutId);

		queryPos.add(hidden);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		return filterFindByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end) {

		return filterFindByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_H(
				groupId, privateLayout, parentLayoutIds, hidden, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P_H(
					groupId, privateLayout, parentLayoutIds, hidden,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2_SQL);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_7_SQL);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2_SQL);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system_ = [$FALSE$]");

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(hidden);

			return (List<Layout>)QueryUtil.list(
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
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		return findByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end) {

		return findByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		if (parentLayoutIds.length == 1) {
			return findByG_P_P_H(
				groupId, privateLayout, parentLayoutIds[0], hidden, start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, privateLayout,
						StringUtil.merge(parentLayoutIds), hidden
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, privateLayout, StringUtil.merge(parentLayoutIds),
					hidden, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_P_P_H, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							!ArrayUtil.contains(
								parentLayoutIds, layout.getParentLayoutId()) ||
							(hidden != layout.isHidden())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				try {
					if ((start == QueryUtil.ALL_POS) &&
						(end == QueryUtil.ALL_POS) &&
						(databaseInMaxParameters > 0) &&
						(parentLayoutIds.length > databaseInMaxParameters)) {

						list = new ArrayList<Layout>();

						long[][] parentLayoutIdsPages =
							(long[][])ArrayUtil.split(
								parentLayoutIds, databaseInMaxParameters);

						for (long[] parentLayoutIdsPage :
								parentLayoutIdsPages) {

							list.addAll(
								_findByG_P_P_H(
									groupId, privateLayout, parentLayoutIdsPage,
									hidden, start, end, orderByComparator));
						}

						Collections.sort(list, orderByComparator);

						list = Collections.unmodifiableList(list);
					}
					else {
						list = _findByG_P_P_H(
							groupId, privateLayout, parentLayoutIds, hidden,
							start, end, orderByComparator);
					}

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_P_P_H, finderArgs,
							list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return list;
		}
	}

	private List<Layout> _findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system = [$FALSE$]");

		if (orderByComparator != null) {
			appendOrderByComparator(
				sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
		}
		else {
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(hidden);

			list = (List<Layout>)QueryUtil.list(
				query, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return list;
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 */
	@Override
	public void removeByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		for (Layout layout :
				findByG_P_P_H(
					groupId, privateLayout, parentLayoutId, hidden,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByG_P_P_H;

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, parentLayoutId, hidden
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2);

				sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					queryPos.add(hidden);

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

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, StringUtil.merge(parentLayoutIds),
				hidden
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_P_P_H, finderArgs, this);

			if (count == null) {
				try {
					if ((databaseInMaxParameters > 0) &&
						(parentLayoutIds.length > databaseInMaxParameters)) {

						count = Long.valueOf(0);

						long[][] parentLayoutIdsPages =
							(long[][])ArrayUtil.split(
								parentLayoutIds, databaseInMaxParameters);

						for (long[] parentLayoutIdsPage :
								parentLayoutIdsPages) {

							count += Long.valueOf(
								_countByG_P_P_H(
									groupId, privateLayout, parentLayoutIdsPage,
									hidden));
						}
					}
					else {
						count = Long.valueOf(
							_countByG_P_P_H(
								groupId, privateLayout, parentLayoutIds,
								hidden));
					}

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_P_P_H, finderArgs,
						count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return count.intValue();
		}
	}

	private int _countByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		Long count = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system = [$FALSE$]");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(hidden);

			count = (Long)query.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return count.intValue();
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P_H(
				groupId, privateLayout, parentLayoutId, hidden);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_P_P_H(
				groupId, privateLayout, parentLayoutId, hidden);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			queryPos.add(hidden);

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
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P_H(
				groupId, privateLayout, parentLayoutIds, hidden);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = InlineSQLHelperUtil.filter(
				findByG_P_P_H(groupId, privateLayout, parentLayoutIds, hidden),
				groupId);

			return layouts.size();
		}

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2_SQL);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_7_SQL);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2_SQL);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system_ = [$FALSE$]");

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(hidden);

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

	private static final String _FINDER_COLUMN_G_P_P_H_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_H_GROUPID_2_SQL =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2_SQL =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2 =
		"layout.parentLayoutId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_7 =
		"layout.parentLayoutId IN (";

	private static final String _FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2_SQL =
		"layout.parentLayoutId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_7_SQL =
		"layout.parentLayoutId IN (";

	private static final String _FINDER_COLUMN_G_P_P_H_HIDDEN_2 =
		"layout.hidden = ? AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_P_H_HIDDEN_2_SQL =
		"layout.hidden_ = ? AND layout.system_ = [$FALSE$]";

	private FinderPath _finderPathWithPaginationFindByG_P_P_S;
	private FinderPath _finderPathWithoutPaginationFindByG_P_P_S;
	private FinderPath _finderPathCountByG_P_P_S;
	private FinderPath _finderPathWithPaginationCountByG_P_P_S;

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		return findByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end) {

		return findByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_P_S;
					finderArgs = new Object[] {
						groupId, privateLayout, parentLayoutId, system
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_P_S;
				finderArgs = new Object[] {
					groupId, privateLayout, parentLayoutId, system, start, end,
					orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							(parentLayoutId != layout.getParentLayoutId()) ||
							(system != layout.isSystem())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_2);

				sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					queryPos.add(system);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_P_S_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean system, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_P_S_First(
			groupId, privateLayout, parentLayoutId, system, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", parentLayoutId=");
		sb.append(parentLayoutId);

		sb.append(", system=");
		sb.append(system);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_P_S_First(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_P_S_Last(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean system, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_P_S_Last(
			groupId, privateLayout, parentLayoutId, system, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", parentLayoutId=");
		sb.append(parentLayoutId);

		sb.append(", system=");
		sb.append(system);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_P_S_Last(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, OrderByComparator<Layout> orderByComparator) {

		int count = countByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByG_P_P_S_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			boolean system, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByG_P_P_S_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId, system,
				orderByComparator, true);

			array[1] = layout;

			array[2] = getByG_P_P_S_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId, system,
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

	protected Layout getByG_P_P_S_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		long parentLayoutId, boolean system,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		queryPos.add(parentLayoutId);

		queryPos.add(system);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		return filterFindByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end) {

		return filterFindByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_S(
				groupId, privateLayout, parentLayoutId, system, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P_S(
					groupId, privateLayout, parentLayoutId, system,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			queryPos.add(system);

			return (List<Layout>)QueryUtil.list(
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
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] filterFindByG_P_P_S_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			boolean system, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_S_PrevAndNext(
				plid, groupId, privateLayout, parentLayoutId, system,
				orderByComparator);
		}

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = filterGetByG_P_P_S_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId, system,
				orderByComparator, true);

			array[1] = layout;

			array[2] = filterGetByG_P_P_S_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId, system,
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

	protected Layout filterGetByG_P_P_S_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		long parentLayoutId, boolean system,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		queryPos.add(parentLayoutId);

		queryPos.add(system);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		return filterFindByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end) {

		return filterFindByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_S(
				groupId, privateLayout, parentLayoutIds, system, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P_S(
					groupId, privateLayout, parentLayoutIds, system,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2_SQL);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(system);

			return (List<Layout>)QueryUtil.list(
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
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		return findByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end) {

		return findByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		if (parentLayoutIds.length == 1) {
			return findByG_P_P_S(
				groupId, privateLayout, parentLayoutIds[0], system, start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, privateLayout,
						StringUtil.merge(parentLayoutIds), system
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, privateLayout, StringUtil.merge(parentLayoutIds),
					system, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_P_P_S, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							!ArrayUtil.contains(
								parentLayoutIds, layout.getParentLayoutId()) ||
							(system != layout.isSystem())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				try {
					if ((start == QueryUtil.ALL_POS) &&
						(end == QueryUtil.ALL_POS) &&
						(databaseInMaxParameters > 0) &&
						(parentLayoutIds.length > databaseInMaxParameters)) {

						list = new ArrayList<Layout>();

						long[][] parentLayoutIdsPages =
							(long[][])ArrayUtil.split(
								parentLayoutIds, databaseInMaxParameters);

						for (long[] parentLayoutIdsPage :
								parentLayoutIdsPages) {

							list.addAll(
								_findByG_P_P_S(
									groupId, privateLayout, parentLayoutIdsPage,
									system, start, end, orderByComparator));
						}

						Collections.sort(list, orderByComparator);

						list = Collections.unmodifiableList(list);
					}
					else {
						list = _findByG_P_P_S(
							groupId, privateLayout, parentLayoutIds, system,
							start, end, orderByComparator);
					}

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_P_P_S, finderArgs,
							list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return list;
		}
	}

	private List<Layout> _findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (orderByComparator != null) {
			appendOrderByComparator(
				sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
		}
		else {
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(system);

			list = (List<Layout>)QueryUtil.list(
				query, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return list;
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 */
	@Override
	public void removeByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		for (Layout layout :
				findByG_P_P_S(
					groupId, privateLayout, parentLayoutId, system,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByG_P_P_S;

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, parentLayoutId, system
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_2);

				sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					queryPos.add(system);

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

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, StringUtil.merge(parentLayoutIds),
				system
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_P_P_S, finderArgs, this);

			if (count == null) {
				try {
					if ((databaseInMaxParameters > 0) &&
						(parentLayoutIds.length > databaseInMaxParameters)) {

						count = Long.valueOf(0);

						long[][] parentLayoutIdsPages =
							(long[][])ArrayUtil.split(
								parentLayoutIds, databaseInMaxParameters);

						for (long[] parentLayoutIdsPage :
								parentLayoutIdsPages) {

							count += Long.valueOf(
								_countByG_P_P_S(
									groupId, privateLayout, parentLayoutIdsPage,
									system));
						}
					}
					else {
						count = Long.valueOf(
							_countByG_P_P_S(
								groupId, privateLayout, parentLayoutIds,
								system));
					}

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_P_P_S, finderArgs,
						count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return count.intValue();
		}
	}

	private int _countByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		Long count = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(system);

			count = (Long)query.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return count.intValue();
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P_S(
				groupId, privateLayout, parentLayoutId, system);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_P_P_S(
				groupId, privateLayout, parentLayoutId, system);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			queryPos.add(system);

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
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P_S(
				groupId, privateLayout, parentLayoutIds, system);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = InlineSQLHelperUtil.filter(
				findByG_P_P_S(groupId, privateLayout, parentLayoutIds, system),
				groupId);

			return layouts.size();
		}

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2_SQL);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(system);

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

	private static final String _FINDER_COLUMN_G_P_P_S_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_2 =
		"layout.parentLayoutId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_7 =
		"layout.parentLayoutId IN (";

	private static final String _FINDER_COLUMN_G_P_P_S_SYSTEM_2 =
		"layout.system = ?";

	private static final String _FINDER_COLUMN_G_P_P_S_SYSTEM_2_SQL =
		"layout.system_ = ?";

	private FinderPath _finderPathWithPaginationFindByG_P_P_LteP;
	private FinderPath _finderPathWithPaginationCountByG_P_P_LteP;

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		return findByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end) {

		return findByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		return findByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_P_P_LteP;
			finderArgs = new Object[] {
				groupId, privateLayout, parentLayoutId, priority, start, end,
				orderByComparator
			};

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							(parentLayoutId != layout.getParentLayoutId()) ||
							(priority < layout.getPriority())) {

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

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_LTEP_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_LTEP_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_LTEP_PARENTLAYOUTID_2);

				sb.append(_FINDER_COLUMN_G_P_P_LTEP_PRIORITY_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					queryPos.add(priority);

					list = (List<Layout>)QueryUtil.list(
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
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_P_LteP_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			int priority, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_P_LteP_First(
			groupId, privateLayout, parentLayoutId, priority,
			orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", parentLayoutId=");
		sb.append(parentLayoutId);

		sb.append(", priority<=");
		sb.append(priority);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_P_LteP_First(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_P_LteP_Last(
			long groupId, boolean privateLayout, long parentLayoutId,
			int priority, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_P_LteP_Last(
			groupId, privateLayout, parentLayoutId, priority,
			orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", parentLayoutId=");
		sb.append(parentLayoutId);

		sb.append(", priority<=");
		sb.append(priority);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_P_LteP_Last(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		OrderByComparator<Layout> orderByComparator) {

		int count = countByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority);

		if (count == 0) {
			return null;
		}

		List<Layout> list = findByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] findByG_P_P_LteP_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			int priority, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = getByG_P_P_LteP_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId,
				priority, orderByComparator, true);

			array[1] = layout;

			array[2] = getByG_P_P_LteP_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId,
				priority, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout getByG_P_P_LteP_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		long parentLayoutId, int priority,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_PRIVATELAYOUT_2);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_PARENTLAYOUTID_2);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_PRIORITY_2);

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
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		queryPos.add(parentLayoutId);

		queryPos.add(priority);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		return filterFindByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end) {

		return filterFindByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_LteP(
				groupId, privateLayout, parentLayoutId, priority, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P_LteP(
					groupId, privateLayout, parentLayoutId, priority,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_PARENTLAYOUTID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_PRIORITY_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			queryPos.add(priority);

			return (List<Layout>)QueryUtil.list(
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
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout[] filterFindByG_P_P_LteP_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			int priority, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_LteP_PrevAndNext(
				plid, groupId, privateLayout, parentLayoutId, priority,
				orderByComparator);
		}

		Layout layout = findByPrimaryKey(plid);

		Session session = null;

		try {
			session = openSession();

			Layout[] array = new LayoutImpl[3];

			array[0] = filterGetByG_P_P_LteP_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId,
				priority, orderByComparator, true);

			array[1] = layout;

			array[2] = filterGetByG_P_P_LteP_PrevAndNext(
				session, layout, groupId, privateLayout, parentLayoutId,
				priority, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected Layout filterGetByG_P_P_LteP_PrevAndNext(
		Session session, Layout layout, long groupId, boolean privateLayout,
		long parentLayoutId, int priority,
		OrderByComparator<Layout> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_PARENTLAYOUTID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_PRIORITY_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(privateLayout);

		queryPos.add(parentLayoutId);

		queryPos.add(priority);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(layout)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<Layout> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 */
	@Override
	public void removeByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		for (Layout layout :
				findByG_P_P_LteP(
					groupId, privateLayout, parentLayoutId, priority,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_P_P_LteP;

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, parentLayoutId, priority
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_LTEP_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_LTEP_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_LTEP_PARENTLAYOUTID_2);

				sb.append(_FINDER_COLUMN_G_P_P_LTEP_PRIORITY_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					queryPos.add(priority);

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

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P_LteP(
				groupId, privateLayout, parentLayoutId, priority);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_P_P_LteP(
				groupId, privateLayout, parentLayoutId, priority);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_PARENTLAYOUTID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_LTEP_PRIORITY_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			queryPos.add(priority);

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

	private static final String _FINDER_COLUMN_G_P_P_LTEP_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_LTEP_GROUPID_2_SQL =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_LTEP_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_LTEP_PRIVATELAYOUT_2_SQL =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_LTEP_PARENTLAYOUTID_2 =
		"layout.parentLayoutId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_LTEP_PARENTLAYOUTID_2_SQL =
		"layout.parentLayoutId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_LTEP_PRIORITY_2 =
		"layout.priority <= ? AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_P_LTEP_PRIORITY_2_SQL =
		"layout.priority <= ? AND layout.system_ = [$FALSE$]";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the layout where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchLayoutException {

		Layout layout = fetchByERC_G(externalReferenceCode, groupId);

		if (layout == null) {
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

			throw new NoSuchLayoutException(sb.toString());
		}

		return layout;
	}

	/**
	 * Returns the layout where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByERC_G(String externalReferenceCode, long groupId) {
		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the layout where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

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

			if (result instanceof Layout) {
				Layout layout = (Layout)result;

				if (!Objects.equals(
						externalReferenceCode,
						layout.getExternalReferenceCode()) ||
					(groupId != layout.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

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

					List<Layout> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						Layout layout = list.get(0);

						result = layout;

						cacheResult(layout);
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
				return (Layout)result;
			}
		}
	}

	/**
	 * Removes the layout where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout that was removed
	 */
	@Override
	public Layout removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchLayoutException {

		Layout layout = findByERC_G(externalReferenceCode, groupId);

		return remove(layout);
	}

	/**
	 * Returns the number of layouts where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		Layout layout = fetchByERC_G(externalReferenceCode, groupId);

		if (layout == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"layout.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(layout.externalReferenceCode IS NULL OR layout.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"layout.groupId = ?";

	public LayoutPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");
		dbColumnNames.put("hidden", "hidden_");
		dbColumnNames.put("system", "system_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Layout.class);

		setModelImplClass(LayoutImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutTable.INSTANCE);
	}

	/**
	 * Caches the layout in the entity cache if it is enabled.
	 *
	 * @param layout the layout
	 */
	@Override
	public void cacheResult(Layout layout) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					layout.getCtCollectionId())) {

			EntityCacheUtil.putResult(
				LayoutImpl.class, layout.getPrimaryKey(), layout);

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G_P,
				new Object[] {
					layout.getUuid(), layout.getGroupId(),
					layout.isPrivateLayout()
				},
				layout);

			FinderCacheUtil.putResult(
				_finderPathFetchByG_P_L,
				new Object[] {
					layout.getGroupId(), layout.isPrivateLayout(),
					layout.getLayoutId()
				},
				layout);

			FinderCacheUtil.putResult(
				_finderPathFetchByG_P_F,
				new Object[] {
					layout.getGroupId(), layout.isPrivateLayout(),
					layout.getFriendlyURL()
				},
				layout);

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					layout.getExternalReferenceCode(), layout.getGroupId()
				},
				layout);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the layouts in the entity cache if it is enabled.
	 *
	 * @param layouts the layouts
	 */
	@Override
	public void cacheResult(List<Layout> layouts) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (layouts.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (Layout layout : layouts) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						layout.getCtCollectionId())) {

				if (EntityCacheUtil.getResult(
						LayoutImpl.class, layout.getPrimaryKey()) == null) {

					cacheResult(layout);
				}
			}
		}
	}

	/**
	 * Clears the cache for all layouts.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(LayoutImpl.class);

		FinderCacheUtil.clearCache(LayoutImpl.class);
	}

	/**
	 * Clears the cache for the layout.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(Layout layout) {
		EntityCacheUtil.removeResult(LayoutImpl.class, layout);
	}

	@Override
	public void clearCache(List<Layout> layouts) {
		for (Layout layout : layouts) {
			EntityCacheUtil.removeResult(LayoutImpl.class, layout);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(LayoutImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(LayoutImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(LayoutModelImpl layoutModelImpl) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					layoutModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				layoutModelImpl.getUuid(), layoutModelImpl.getGroupId(),
				layoutModelImpl.isPrivateLayout()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByUUID_G_P, args, layoutModelImpl);

			args = new Object[] {
				layoutModelImpl.getGroupId(), layoutModelImpl.isPrivateLayout(),
				layoutModelImpl.getLayoutId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByG_P_L, args, layoutModelImpl);

			args = new Object[] {
				layoutModelImpl.getGroupId(), layoutModelImpl.isPrivateLayout(),
				layoutModelImpl.getFriendlyURL()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByG_P_F, args, layoutModelImpl);

			args = new Object[] {
				layoutModelImpl.getExternalReferenceCode(),
				layoutModelImpl.getGroupId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_G, args, layoutModelImpl);
		}
	}

	/**
	 * Creates a new layout with the primary key. Does not add the layout to the database.
	 *
	 * @param plid the primary key for the new layout
	 * @return the new layout
	 */
	@Override
	public Layout create(long plid) {
		Layout layout = new LayoutImpl();

		layout.setNew(true);
		layout.setPrimaryKey(plid);

		String uuid = PortalUUIDUtil.generate();

		layout.setUuid(uuid);

		layout.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layout;
	}

	/**
	 * Removes the layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout that was removed
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout remove(long plid) throws NoSuchLayoutException {
		return remove((Serializable)plid);
	}

	/**
	 * Removes the layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the layout
	 * @return the layout that was removed
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout remove(Serializable primaryKey) throws NoSuchLayoutException {
		Session session = null;

		try {
			session = openSession();

			Layout layout = (Layout)session.get(LayoutImpl.class, primaryKey);

			if (layout == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchLayoutException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(layout);
		}
		catch (NoSuchLayoutException noSuchEntityException) {
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
	protected Layout removeImpl(Layout layout) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layout)) {
				layout = (Layout)session.get(
					LayoutImpl.class, layout.getPrimaryKeyObj());
			}

			if ((layout != null) && CTPersistenceHelperUtil.isRemove(layout)) {
				session.delete(layout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layout != null) {
			clearCache(layout);
		}

		return layout;
	}

	@Override
	public Layout updateImpl(Layout layout) {
		boolean isNew = layout.isNew();

		if (!(layout instanceof LayoutModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layout.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(layout);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layout proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Layout implementation " +
					layout.getClass());
		}

		LayoutModelImpl layoutModelImpl = (LayoutModelImpl)layout;

		if (Validator.isNull(layout.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layout.setUuid(uuid);
		}

		if (Validator.isNull(layout.getExternalReferenceCode())) {
			layout.setExternalReferenceCode(layout.getUuid());
		}
		else {
			if (!Objects.equals(
					layoutModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					layout.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = layout.getCompanyId();

					long groupId = layout.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = layout.getPrimaryKey();
					}

					try {
						layout.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								Layout.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								layout.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			Layout ercLayout = fetchByERC_G(
				layout.getExternalReferenceCode(), layout.getGroupId());

			if (isNew) {
				if (ercLayout != null) {
					throw new DuplicateLayoutExternalReferenceCodeException(
						"Duplicate layout with external reference code " +
							layout.getExternalReferenceCode() + " and group " +
								layout.getGroupId());
				}
			}
			else {
				if ((ercLayout != null) &&
					(layout.getPlid() != ercLayout.getPlid())) {

					throw new DuplicateLayoutExternalReferenceCodeException(
						"Duplicate layout with external reference code " +
							layout.getExternalReferenceCode() + " and group " +
								layout.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layout.getCreateDate() == null)) {
			if (serviceContext == null) {
				layout.setCreateDate(date);
			}
			else {
				layout.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!layoutModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layout.setModifiedDate(date);
			}
			else {
				layout.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(layout)) {
				if (!isNew) {
					session.evict(LayoutImpl.class, layout.getPrimaryKeyObj());
				}

				session.save(layout);
			}
			else {
				layout = (Layout)session.merge(layout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(
			LayoutImpl.class, layoutModelImpl, false, true);

		cacheUniqueFindersCache(layoutModelImpl);

		if (isNew) {
			layout.setNew(false);
		}

		layout.resetOriginalValues();

		return layout;
	}

	/**
	 * Returns the layout with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the layout
	 * @return the layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout findByPrimaryKey(Serializable primaryKey)
		throws NoSuchLayoutException {

		Layout layout = fetchByPrimaryKey(primaryKey);

		if (layout == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchLayoutException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return layout;
	}

	/**
	 * Returns the layout with the primary key or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout findByPrimaryKey(long plid) throws NoSuchLayoutException {
		return findByPrimaryKey((Serializable)plid);
	}

	/**
	 * Returns the layout with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the layout
	 * @return the layout, or <code>null</code> if a layout with the primary key could not be found
	 */
	@Override
	public Layout fetchByPrimaryKey(Serializable primaryKey) {
		if (CTPersistenceHelperUtil.isProductionMode(
				Layout.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		Layout layout = (Layout)EntityCacheUtil.getResult(
			LayoutImpl.class, primaryKey);

		if (layout != null) {
			return layout;
		}

		Session session = null;

		try {
			session = openSession();

			layout = (Layout)session.get(LayoutImpl.class, primaryKey);

			if (layout != null) {
				cacheResult(layout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return layout;
	}

	/**
	 * Returns the layout with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout, or <code>null</code> if a layout with the primary key could not be found
	 */
	@Override
	public Layout fetchByPrimaryKey(long plid) {
		return fetchByPrimaryKey((Serializable)plid);
	}

	@Override
	public Map<Serializable, Layout> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (CTPersistenceHelperUtil.isProductionMode(Layout.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, Layout> map = new HashMap<Serializable, Layout>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			Layout layout = fetchByPrimaryKey(primaryKey);

			if (layout != null) {
				map.put(primaryKey, layout);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
						Layout.class, primaryKey)) {

				Layout layout = (Layout)EntityCacheUtil.getResult(
					LayoutImpl.class, primaryKey);

				if (layout == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, layout);
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

			for (Layout layout : (List<Layout>)query.list()) {
				map.put(layout.getPrimaryKeyObj(), layout);

				cacheResult(layout);
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
	 * Returns all the layouts.
	 *
	 * @return the layouts
	 */
	@Override
	public List<Layout> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of layouts
	 */
	@Override
	public List<Layout> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of layouts
	 */
	@Override
	public List<Layout> findAll(
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of layouts
	 */
	@Override
	public List<Layout> findAll(
		int start, int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

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

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_LAYOUT);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_LAYOUT;

					sql = sql.concat(LayoutModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<Layout>)QueryUtil.list(
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
	 * Removes all the layouts from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (Layout layout : findAll()) {
			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts.
	 *
	 * @return the number of layouts
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_LAYOUT);

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
		return "plid";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUT;
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
		return LayoutModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "Layout";
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
		ctMergeColumnNames.add("parentPlid");
		ctMergeColumnNames.add("privateLayout");
		ctMergeColumnNames.add("layoutId");
		ctMergeColumnNames.add("parentLayoutId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("keywords");
		ctMergeColumnNames.add("robots");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("hidden_");
		ctMergeColumnNames.add("system_");
		ctMergeColumnNames.add("friendlyURL");
		ctMergeColumnNames.add("iconImageId");
		ctMergeColumnNames.add("themeId");
		ctMergeColumnNames.add("colorSchemeId");
		ctMergeColumnNames.add("styleBookEntryId");
		ctMergeColumnNames.add("css");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("faviconFileEntryId");
		ctMergeColumnNames.add("masterLayoutPlid");
		ctMergeColumnNames.add("layoutPrototypeUuid");
		ctMergeColumnNames.add("layoutPrototypeLinkEnabled");
		ctMergeColumnNames.add("layoutSetPrototypeLayoutERC");
		ctMergeColumnNames.add("publishDate");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("plid"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"uuid_", "groupId", "privateLayout"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "privateLayout", "layoutId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "privateLayout", "friendlyURL"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the layout persistence.
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

		_finderPathFetchByUUID_G_P = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G_P",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Boolean.class.getName()
			},
			new String[] {"uuid_", "groupId", "privateLayout"}, true);

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

		_finderPathWithPaginationFindByParentPlid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByParentPlid",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"parentPlid"}, true);

		_finderPathWithoutPaginationFindByParentPlid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByParentPlid",
			new String[] {Long.class.getName()}, new String[] {"parentPlid"},
			true);

		_finderPathCountByParentPlid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByParentPlid",
			new String[] {Long.class.getName()}, new String[] {"parentPlid"},
			false);

		_finderPathWithPaginationFindByIconImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByIconImageId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"iconImageId"}, true);

		_finderPathWithoutPaginationFindByIconImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByIconImageId",
			new String[] {Long.class.getName()}, new String[] {"iconImageId"},
			true);

		_finderPathCountByIconImageId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByIconImageId",
			new String[] {Long.class.getName()}, new String[] {"iconImageId"},
			false);

		_finderPathWithPaginationFindByLayoutPrototypeUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLayoutPrototypeUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"layoutPrototypeUuid"}, true);

		_finderPathWithoutPaginationFindByLayoutPrototypeUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByLayoutPrototypeUuid", new String[] {String.class.getName()},
			new String[] {"layoutPrototypeUuid"}, true);

		_finderPathCountByLayoutPrototypeUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByLayoutPrototypeUuid", new String[] {String.class.getName()},
			new String[] {"layoutPrototypeUuid"}, false);

		_finderPathWithPaginationFindByLayoutSetPrototypeLayoutERC =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
				"findByLayoutSetPrototypeLayoutERC",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"layoutSetPrototypeLayoutERC"}, true);

		_finderPathWithoutPaginationFindByLayoutSetPrototypeLayoutERC =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByLayoutSetPrototypeLayoutERC",
				new String[] {String.class.getName()},
				new String[] {"layoutSetPrototypeLayoutERC"}, true);

		_finderPathCountByLayoutSetPrototypeLayoutERC = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByLayoutSetPrototypeLayoutERC",
			new String[] {String.class.getName()},
			new String[] {"layoutSetPrototypeLayoutERC"}, false);

		_finderPathWithPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "privateLayout"}, true);

		_finderPathWithoutPaginationFindByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"groupId", "privateLayout"}, true);

		_finderPathCountByG_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"groupId", "privateLayout"}, false);

		_finderPathWithPaginationFindByG_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "type_"}, true);

		_finderPathWithoutPaginationFindByG_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "type_"}, true);

		_finderPathCountByG_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_T",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"groupId", "type_"}, false);

		_finderPathWithPaginationFindByG_MLP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_MLP",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "masterLayoutPlid"}, true);

		_finderPathWithoutPaginationFindByG_MLP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_MLP",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "masterLayoutPlid"}, true);

		_finderPathCountByG_MLP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_MLP",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "masterLayoutPlid"}, false);

		_finderPathWithPaginationFindByC_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_L",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "layoutPrototypeUuid"}, true);

		_finderPathWithoutPaginationFindByC_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_L",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "layoutPrototypeUuid"}, true);

		_finderPathCountByC_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_L",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "layoutPrototypeUuid"}, false);

		_finderPathWithPaginationFindByP_I = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_I",
			new String[] {
				Boolean.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"privateLayout", "iconImageId"}, true);

		_finderPathWithoutPaginationFindByP_I = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_I",
			new String[] {Boolean.class.getName(), Long.class.getName()},
			new String[] {"privateLayout", "iconImageId"}, true);

		_finderPathCountByP_I = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_I",
			new String[] {Boolean.class.getName(), Long.class.getName()},
			new String[] {"privateLayout", "iconImageId"}, false);

		_finderPathWithPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"classNameId", "classPK"}, true);

		_finderPathWithoutPaginationFindByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"classNameId", "classPK"}, true);

		_finderPathCountByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"classNameId", "classPK"}, false);

		_finderPathWithPaginationCountByC_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"classNameId", "classPK"}, false);

		_finderPathFetchByG_P_L = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_P_L",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "privateLayout", "layoutId"}, true);

		_finderPathWithPaginationFindByG_P_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "privateLayout", "parentLayoutId"}, true);

		_finderPathWithoutPaginationFindByG_P_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "privateLayout", "parentLayoutId"}, true);

		_finderPathCountByG_P_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "privateLayout", "parentLayoutId"}, false);

		_finderPathWithPaginationCountByG_P_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "privateLayout", "parentLayoutId"}, false);

		_finderPathWithPaginationFindByG_P_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "privateLayout", "type_"}, true);

		_finderPathWithoutPaginationFindByG_P_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "privateLayout", "type_"}, true);

		_finderPathCountByG_P_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "privateLayout", "type_"}, false);

		_finderPathWithPaginationCountByG_P_T = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_T",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "privateLayout", "type_"}, false);

		_finderPathFetchByG_P_F = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_P_F",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {"groupId", "privateLayout", "friendlyURL"}, true);

		_finderPathWithPaginationFindByG_P_LSPLE = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_LSPLE",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "layoutSetPrototypeLayoutERC"
			},
			true);

		_finderPathWithoutPaginationFindByG_P_LSPLE = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_LSPLE",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "layoutSetPrototypeLayoutERC"
			},
			true);

		_finderPathCountByG_P_LSPLE = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_LSPLE",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				String.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "layoutSetPrototypeLayoutERC"
			},
			false);

		_finderPathWithPaginationFindByG_P_ST = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_ST",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "privateLayout", "status"}, true);

		_finderPathWithoutPaginationFindByG_P_ST = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_ST",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "privateLayout", "status"}, true);

		_finderPathCountByG_P_ST = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_ST",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "privateLayout", "status"}, false);

		_finderPathWithPaginationCountByG_P_ST = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_ST",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "privateLayout", "status"}, false);

		_finderPathWithPaginationFindByG_P_P_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_P_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "hidden_"
			},
			true);

		_finderPathWithoutPaginationFindByG_P_P_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_P_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "hidden_"
			},
			true);

		_finderPathCountByG_P_P_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_P_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "hidden_"
			},
			false);

		_finderPathWithPaginationCountByG_P_P_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_P_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "hidden_"
			},
			false);

		_finderPathWithPaginationFindByG_P_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_P_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "system_"
			},
			true);

		_finderPathWithoutPaginationFindByG_P_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_P_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "system_"
			},
			true);

		_finderPathCountByG_P_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_P_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "system_"
			},
			false);

		_finderPathWithPaginationCountByG_P_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_P_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "system_"
			},
			false);

		_finderPathWithPaginationFindByG_P_P_LteP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_P_LteP",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "priority"
			},
			true);

		_finderPathWithPaginationCountByG_P_P_LteP = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_P_LteP",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "priority"
			},
			false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		LayoutUtil.setPersistence(this);
	}

	public void destroy() {
		LayoutUtil.setPersistence(null);

		EntityCacheUtil.removeCache(LayoutImpl.class.getName());
	}

	private static final String _SQL_SELECT_LAYOUT =
		"SELECT layout FROM Layout layout";

	private static final String _SQL_SELECT_LAYOUT_WHERE =
		"SELECT layout FROM Layout layout WHERE ";

	private static final String _SQL_COUNT_LAYOUT =
		"SELECT COUNT(layout) FROM Layout layout";

	private static final String _SQL_COUNT_LAYOUT_WHERE =
		"SELECT COUNT(layout) FROM Layout layout WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"layout.plid";

	private static final String _FILTER_SQL_SELECT_LAYOUT_WHERE =
		"SELECT DISTINCT {layout.*} FROM Layout layout WHERE ";

	private static final String
		_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {Layout.*} FROM (SELECT DISTINCT layout.plid FROM Layout layout WHERE ";

	private static final String
		_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN Layout ON TEMP_TABLE.plid = Layout.plid";

	private static final String _FILTER_SQL_COUNT_LAYOUT_WHERE =
		"SELECT COUNT(DISTINCT layout.plid) AS COUNT_VALUE FROM Layout layout WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "layout";

	private static final String _FILTER_ENTITY_TABLE = "Layout";

	private static final String _ORDER_BY_ENTITY_ALIAS = "layout.";

	private static final String _ORDER_BY_ENTITY_TABLE = "Layout.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No Layout exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Layout exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type", "hidden", "system"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}