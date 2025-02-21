/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchPortletItemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.model.PortletItemTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.PortletItemPersistence;
import com.liferay.portal.kernel.service.persistence.PortletItemUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.impl.PortletItemImpl;
import com.liferay.portal.model.impl.PortletItemModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the portlet item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PortletItemPersistenceImpl
	extends BasePersistenceImpl<PortletItem> implements PortletItemPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PortletItemUtil</code> to access the portlet item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PortletItemImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByG_C;
	private FinderPath _finderPathWithoutPaginationFindByG_C;
	private FinderPath _finderPathCountByG_C;

	/**
	 * Returns all the portlet items where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching portlet items
	 */
	@Override
	public List<PortletItem> findByG_C(long groupId, long classNameId) {
		return findByG_C(
			groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the portlet items where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of portlet items
	 * @param end the upper bound of the range of portlet items (not inclusive)
	 * @return the range of matching portlet items
	 */
	@Override
	public List<PortletItem> findByG_C(
		long groupId, long classNameId, int start, int end) {

		return findByG_C(groupId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the portlet items where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of portlet items
	 * @param end the upper bound of the range of portlet items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portlet items
	 */
	@Override
	public List<PortletItem> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<PortletItem> orderByComparator) {

		return findByG_C(
			groupId, classNameId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the portlet items where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of portlet items
	 * @param end the upper bound of the range of portlet items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet items
	 */
	@Override
	public List<PortletItem> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<PortletItem> orderByComparator,
		boolean useFinderCache) {

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_C;
				finderArgs = new Object[] {groupId, classNameId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_C;
			finderArgs = new Object[] {
				groupId, classNameId, start, end, orderByComparator
			};
		}

		List<PortletItem> list = null;

		if (useFinderCache) {
			list = (List<PortletItem>)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PortletItem portletItem : list) {
					if ((groupId != portletItem.getGroupId()) ||
						(classNameId != portletItem.getClassNameId())) {

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

			sb.append(_SQL_SELECT_PORTLETITEM_WHERE);

			sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PortletItemModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				queryPos.add(classNameId);

				list = (List<PortletItem>)QueryUtil.list(
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

	/**
	 * Returns the first portlet item in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet item
	 * @throws NoSuchPortletItemException if a matching portlet item could not be found
	 */
	@Override
	public PortletItem findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<PortletItem> orderByComparator)
		throws NoSuchPortletItemException {

		PortletItem portletItem = fetchByG_C_First(
			groupId, classNameId, orderByComparator);

		if (portletItem != null) {
			return portletItem;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchPortletItemException(sb.toString());
	}

	/**
	 * Returns the first portlet item in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet item, or <code>null</code> if a matching portlet item could not be found
	 */
	@Override
	public PortletItem fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<PortletItem> orderByComparator) {

		List<PortletItem> list = findByG_C(
			groupId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last portlet item in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portlet item
	 * @throws NoSuchPortletItemException if a matching portlet item could not be found
	 */
	@Override
	public PortletItem findByG_C_Last(
			long groupId, long classNameId,
			OrderByComparator<PortletItem> orderByComparator)
		throws NoSuchPortletItemException {

		PortletItem portletItem = fetchByG_C_Last(
			groupId, classNameId, orderByComparator);

		if (portletItem != null) {
			return portletItem;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchPortletItemException(sb.toString());
	}

	/**
	 * Returns the last portlet item in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portlet item, or <code>null</code> if a matching portlet item could not be found
	 */
	@Override
	public PortletItem fetchByG_C_Last(
		long groupId, long classNameId,
		OrderByComparator<PortletItem> orderByComparator) {

		int count = countByG_C(groupId, classNameId);

		if (count == 0) {
			return null;
		}

		List<PortletItem> list = findByG_C(
			groupId, classNameId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the portlet items before and after the current portlet item in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param portletItemId the primary key of the current portlet item
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portlet item
	 * @throws NoSuchPortletItemException if a portlet item with the primary key could not be found
	 */
	@Override
	public PortletItem[] findByG_C_PrevAndNext(
			long portletItemId, long groupId, long classNameId,
			OrderByComparator<PortletItem> orderByComparator)
		throws NoSuchPortletItemException {

		PortletItem portletItem = findByPrimaryKey(portletItemId);

		Session session = null;

		try {
			session = openSession();

			PortletItem[] array = new PortletItemImpl[3];

			array[0] = getByG_C_PrevAndNext(
				session, portletItem, groupId, classNameId, orderByComparator,
				true);

			array[1] = portletItem;

			array[2] = getByG_C_PrevAndNext(
				session, portletItem, groupId, classNameId, orderByComparator,
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

	protected PortletItem getByG_C_PrevAndNext(
		Session session, PortletItem portletItem, long groupId,
		long classNameId, OrderByComparator<PortletItem> orderByComparator,
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

		sb.append(_SQL_SELECT_PORTLETITEM_WHERE);

		sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

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
			sb.append(PortletItemModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(portletItem)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PortletItem> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the portlet items where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C(long groupId, long classNameId) {
		for (PortletItem portletItem :
				findByG_C(
					groupId, classNameId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(portletItem);
		}
	}

	/**
	 * Returns the number of portlet items where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching portlet items
	 */
	@Override
	public int countByG_C(long groupId, long classNameId) {
		FinderPath finderPath = _finderPathCountByG_C;

		Object[] finderArgs = new Object[] {groupId, classNameId};

		Long count = (Long)FinderCacheUtil.getResult(
			finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_COUNT_PORTLETITEM_WHERE);

			sb.append(_FINDER_COLUMN_G_C_GROUPID_2);

			sb.append(_FINDER_COLUMN_G_C_CLASSNAMEID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

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

	private static final String _FINDER_COLUMN_G_C_GROUPID_2 =
		"portletItem.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_C_CLASSNAMEID_2 =
		"portletItem.classNameId = ?";

	private FinderPath _finderPathWithPaginationFindByG_P_C;
	private FinderPath _finderPathWithoutPaginationFindByG_P_C;
	private FinderPath _finderPathCountByG_P_C;

	/**
	 * Returns all the portlet items where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @return the matching portlet items
	 */
	@Override
	public List<PortletItem> findByG_P_C(
		long groupId, String portletId, long classNameId) {

		return findByG_P_C(
			groupId, portletId, classNameId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the portlet items where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of portlet items
	 * @param end the upper bound of the range of portlet items (not inclusive)
	 * @return the range of matching portlet items
	 */
	@Override
	public List<PortletItem> findByG_P_C(
		long groupId, String portletId, long classNameId, int start, int end) {

		return findByG_P_C(groupId, portletId, classNameId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the portlet items where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of portlet items
	 * @param end the upper bound of the range of portlet items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching portlet items
	 */
	@Override
	public List<PortletItem> findByG_P_C(
		long groupId, String portletId, long classNameId, int start, int end,
		OrderByComparator<PortletItem> orderByComparator) {

		return findByG_P_C(
			groupId, portletId, classNameId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the portlet items where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of portlet items
	 * @param end the upper bound of the range of portlet items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portlet items
	 */
	@Override
	public List<PortletItem> findByG_P_C(
		long groupId, String portletId, long classNameId, int start, int end,
		OrderByComparator<PortletItem> orderByComparator,
		boolean useFinderCache) {

		portletId = Objects.toString(portletId, "");

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache) {
				finderPath = _finderPathWithoutPaginationFindByG_P_C;
				finderArgs = new Object[] {groupId, portletId, classNameId};
			}
		}
		else if (useFinderCache) {
			finderPath = _finderPathWithPaginationFindByG_P_C;
			finderArgs = new Object[] {
				groupId, portletId, classNameId, start, end, orderByComparator
			};
		}

		List<PortletItem> list = null;

		if (useFinderCache) {
			list = (List<PortletItem>)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (PortletItem portletItem : list) {
					if ((groupId != portletItem.getGroupId()) ||
						!portletId.equals(portletItem.getPortletId()) ||
						(classNameId != portletItem.getClassNameId())) {

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

			sb.append(_SQL_SELECT_PORTLETITEM_WHERE);

			sb.append(_FINDER_COLUMN_G_P_C_GROUPID_2);

			boolean bindPortletId = false;

			if (portletId.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_P_C_PORTLETID_3);
			}
			else {
				bindPortletId = true;

				sb.append(_FINDER_COLUMN_G_P_C_PORTLETID_2);
			}

			sb.append(_FINDER_COLUMN_G_P_C_CLASSNAMEID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
			}
			else {
				sb.append(PortletItemModelImpl.ORDER_BY_JPQL);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				if (bindPortletId) {
					queryPos.add(portletId);
				}

				queryPos.add(classNameId);

				list = (List<PortletItem>)QueryUtil.list(
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

	/**
	 * Returns the first portlet item in the ordered set where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet item
	 * @throws NoSuchPortletItemException if a matching portlet item could not be found
	 */
	@Override
	public PortletItem findByG_P_C_First(
			long groupId, String portletId, long classNameId,
			OrderByComparator<PortletItem> orderByComparator)
		throws NoSuchPortletItemException {

		PortletItem portletItem = fetchByG_P_C_First(
			groupId, portletId, classNameId, orderByComparator);

		if (portletItem != null) {
			return portletItem;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", portletId=");
		sb.append(portletId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchPortletItemException(sb.toString());
	}

	/**
	 * Returns the first portlet item in the ordered set where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portlet item, or <code>null</code> if a matching portlet item could not be found
	 */
	@Override
	public PortletItem fetchByG_P_C_First(
		long groupId, String portletId, long classNameId,
		OrderByComparator<PortletItem> orderByComparator) {

		List<PortletItem> list = findByG_P_C(
			groupId, portletId, classNameId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last portlet item in the ordered set where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portlet item
	 * @throws NoSuchPortletItemException if a matching portlet item could not be found
	 */
	@Override
	public PortletItem findByG_P_C_Last(
			long groupId, String portletId, long classNameId,
			OrderByComparator<PortletItem> orderByComparator)
		throws NoSuchPortletItemException {

		PortletItem portletItem = fetchByG_P_C_Last(
			groupId, portletId, classNameId, orderByComparator);

		if (portletItem != null) {
			return portletItem;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", portletId=");
		sb.append(portletId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append("}");

		throw new NoSuchPortletItemException(sb.toString());
	}

	/**
	 * Returns the last portlet item in the ordered set where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching portlet item, or <code>null</code> if a matching portlet item could not be found
	 */
	@Override
	public PortletItem fetchByG_P_C_Last(
		long groupId, String portletId, long classNameId,
		OrderByComparator<PortletItem> orderByComparator) {

		int count = countByG_P_C(groupId, portletId, classNameId);

		if (count == 0) {
			return null;
		}

		List<PortletItem> list = findByG_P_C(
			groupId, portletId, classNameId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the portlet items before and after the current portlet item in the ordered set where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * @param portletItemId the primary key of the current portlet item
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next portlet item
	 * @throws NoSuchPortletItemException if a portlet item with the primary key could not be found
	 */
	@Override
	public PortletItem[] findByG_P_C_PrevAndNext(
			long portletItemId, long groupId, String portletId,
			long classNameId, OrderByComparator<PortletItem> orderByComparator)
		throws NoSuchPortletItemException {

		portletId = Objects.toString(portletId, "");

		PortletItem portletItem = findByPrimaryKey(portletItemId);

		Session session = null;

		try {
			session = openSession();

			PortletItem[] array = new PortletItemImpl[3];

			array[0] = getByG_P_C_PrevAndNext(
				session, portletItem, groupId, portletId, classNameId,
				orderByComparator, true);

			array[1] = portletItem;

			array[2] = getByG_P_C_PrevAndNext(
				session, portletItem, groupId, portletId, classNameId,
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

	protected PortletItem getByG_P_C_PrevAndNext(
		Session session, PortletItem portletItem, long groupId,
		String portletId, long classNameId,
		OrderByComparator<PortletItem> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_PORTLETITEM_WHERE);

		sb.append(_FINDER_COLUMN_G_P_C_GROUPID_2);

		boolean bindPortletId = false;

		if (portletId.isEmpty()) {
			sb.append(_FINDER_COLUMN_G_P_C_PORTLETID_3);
		}
		else {
			bindPortletId = true;

			sb.append(_FINDER_COLUMN_G_P_C_PORTLETID_2);
		}

		sb.append(_FINDER_COLUMN_G_P_C_CLASSNAMEID_2);

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
			sb.append(PortletItemModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (bindPortletId) {
			queryPos.add(portletId);
		}

		queryPos.add(classNameId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(portletItem)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PortletItem> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the portlet items where groupId = &#63; and portletId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_P_C(
		long groupId, String portletId, long classNameId) {

		for (PortletItem portletItem :
				findByG_P_C(
					groupId, portletId, classNameId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(portletItem);
		}
	}

	/**
	 * Returns the number of portlet items where groupId = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @return the number of matching portlet items
	 */
	@Override
	public int countByG_P_C(long groupId, String portletId, long classNameId) {
		portletId = Objects.toString(portletId, "");

		FinderPath finderPath = _finderPathCountByG_P_C;

		Object[] finderArgs = new Object[] {groupId, portletId, classNameId};

		Long count = (Long)FinderCacheUtil.getResult(
			finderPath, finderArgs, this);

		if (count == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_SQL_COUNT_PORTLETITEM_WHERE);

			sb.append(_FINDER_COLUMN_G_P_C_GROUPID_2);

			boolean bindPortletId = false;

			if (portletId.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_P_C_PORTLETID_3);
			}
			else {
				bindPortletId = true;

				sb.append(_FINDER_COLUMN_G_P_C_PORTLETID_2);
			}

			sb.append(_FINDER_COLUMN_G_P_C_CLASSNAMEID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				if (bindPortletId) {
					queryPos.add(portletId);
				}

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

	private static final String _FINDER_COLUMN_G_P_C_GROUPID_2 =
		"portletItem.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_C_PORTLETID_2 =
		"portletItem.portletId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_C_PORTLETID_3 =
		"(portletItem.portletId IS NULL OR portletItem.portletId = '') AND ";

	private static final String _FINDER_COLUMN_G_P_C_CLASSNAMEID_2 =
		"portletItem.classNameId = ?";

	private FinderPath _finderPathFetchByG_N_P_C;

	/**
	 * Returns the portlet item where groupId = &#63; and name = &#63; and portletId = &#63; and classNameId = &#63; or throws a <code>NoSuchPortletItemException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @return the matching portlet item
	 * @throws NoSuchPortletItemException if a matching portlet item could not be found
	 */
	@Override
	public PortletItem findByG_N_P_C(
			long groupId, String name, String portletId, long classNameId)
		throws NoSuchPortletItemException {

		PortletItem portletItem = fetchByG_N_P_C(
			groupId, name, portletId, classNameId);

		if (portletItem == null) {
			StringBundler sb = new StringBundler(10);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("groupId=");
			sb.append(groupId);

			sb.append(", name=");
			sb.append(name);

			sb.append(", portletId=");
			sb.append(portletId);

			sb.append(", classNameId=");
			sb.append(classNameId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPortletItemException(sb.toString());
		}

		return portletItem;
	}

	/**
	 * Returns the portlet item where groupId = &#63; and name = &#63; and portletId = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @return the matching portlet item, or <code>null</code> if a matching portlet item could not be found
	 */
	@Override
	public PortletItem fetchByG_N_P_C(
		long groupId, String name, String portletId, long classNameId) {

		return fetchByG_N_P_C(groupId, name, portletId, classNameId, true);
	}

	/**
	 * Returns the portlet item where groupId = &#63; and name = &#63; and portletId = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching portlet item, or <code>null</code> if a matching portlet item could not be found
	 */
	@Override
	public PortletItem fetchByG_N_P_C(
		long groupId, String name, String portletId, long classNameId,
		boolean useFinderCache) {

		name = Objects.toString(name, "");
		portletId = Objects.toString(portletId, "");

		Object[] finderArgs = null;

		if (useFinderCache) {
			finderArgs = new Object[] {groupId, name, portletId, classNameId};
		}

		Object result = null;

		if (useFinderCache) {
			result = FinderCacheUtil.getResult(
				_finderPathFetchByG_N_P_C, finderArgs, this);
		}

		if (result instanceof PortletItem) {
			PortletItem portletItem = (PortletItem)result;

			if ((groupId != portletItem.getGroupId()) ||
				!Objects.equals(name, portletItem.getName()) ||
				!Objects.equals(portletId, portletItem.getPortletId()) ||
				(classNameId != portletItem.getClassNameId())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_SQL_SELECT_PORTLETITEM_WHERE);

			sb.append(_FINDER_COLUMN_G_N_P_C_GROUPID_2);

			boolean bindName = false;

			if (name.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_N_P_C_NAME_3);
			}
			else {
				bindName = true;

				sb.append(_FINDER_COLUMN_G_N_P_C_NAME_2);
			}

			boolean bindPortletId = false;

			if (portletId.isEmpty()) {
				sb.append(_FINDER_COLUMN_G_N_P_C_PORTLETID_3);
			}
			else {
				bindPortletId = true;

				sb.append(_FINDER_COLUMN_G_N_P_C_PORTLETID_2);
			}

			sb.append(_FINDER_COLUMN_G_N_P_C_CLASSNAMEID_2);

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				queryPos.add(groupId);

				if (bindName) {
					queryPos.add(StringUtil.toLowerCase(name));
				}

				if (bindPortletId) {
					queryPos.add(portletId);
				}

				queryPos.add(classNameId);

				List<PortletItem> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathFetchByG_N_P_C, finderArgs, list);
					}
				}
				else {
					PortletItem portletItem = list.get(0);

					result = portletItem;

					cacheResult(portletItem);
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
			return (PortletItem)result;
		}
	}

	/**
	 * Removes the portlet item where groupId = &#63; and name = &#63; and portletId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @return the portlet item that was removed
	 */
	@Override
	public PortletItem removeByG_N_P_C(
			long groupId, String name, String portletId, long classNameId)
		throws NoSuchPortletItemException {

		PortletItem portletItem = findByG_N_P_C(
			groupId, name, portletId, classNameId);

		return remove(portletItem);
	}

	/**
	 * Returns the number of portlet items where groupId = &#63; and name = &#63; and portletId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @return the number of matching portlet items
	 */
	@Override
	public int countByG_N_P_C(
		long groupId, String name, String portletId, long classNameId) {

		PortletItem portletItem = fetchByG_N_P_C(
			groupId, name, portletId, classNameId);

		if (portletItem == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_G_N_P_C_GROUPID_2 =
		"portletItem.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_P_C_NAME_2 =
		"lower(portletItem.name) = ? AND ";

	private static final String _FINDER_COLUMN_G_N_P_C_NAME_3 =
		"(portletItem.name IS NULL OR portletItem.name = '') AND ";

	private static final String _FINDER_COLUMN_G_N_P_C_PORTLETID_2 =
		"portletItem.portletId = ? AND ";

	private static final String _FINDER_COLUMN_G_N_P_C_PORTLETID_3 =
		"(portletItem.portletId IS NULL OR portletItem.portletId = '') AND ";

	private static final String _FINDER_COLUMN_G_N_P_C_CLASSNAMEID_2 =
		"portletItem.classNameId = ?";

	public PortletItemPersistenceImpl() {
		setModelClass(PortletItem.class);

		setModelImplClass(PortletItemImpl.class);
		setModelPKClass(long.class);

		setTable(PortletItemTable.INSTANCE);
	}

	/**
	 * Caches the portlet item in the entity cache if it is enabled.
	 *
	 * @param portletItem the portlet item
	 */
	@Override
	public void cacheResult(PortletItem portletItem) {
		EntityCacheUtil.putResult(
			PortletItemImpl.class, portletItem.getPrimaryKey(), portletItem);

		FinderCacheUtil.putResult(
			_finderPathFetchByG_N_P_C,
			new Object[] {
				portletItem.getGroupId(), portletItem.getName(),
				portletItem.getPortletId(), portletItem.getClassNameId()
			},
			portletItem);
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the portlet items in the entity cache if it is enabled.
	 *
	 * @param portletItems the portlet items
	 */
	@Override
	public void cacheResult(List<PortletItem> portletItems) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (portletItems.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PortletItem portletItem : portletItems) {
			if (EntityCacheUtil.getResult(
					PortletItemImpl.class, portletItem.getPrimaryKey()) ==
						null) {

				cacheResult(portletItem);
			}
		}
	}

	/**
	 * Clears the cache for all portlet items.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(PortletItemImpl.class);

		FinderCacheUtil.clearCache(PortletItemImpl.class);
	}

	/**
	 * Clears the cache for the portlet item.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PortletItem portletItem) {
		EntityCacheUtil.removeResult(PortletItemImpl.class, portletItem);
	}

	@Override
	public void clearCache(List<PortletItem> portletItems) {
		for (PortletItem portletItem : portletItems) {
			EntityCacheUtil.removeResult(PortletItemImpl.class, portletItem);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(PortletItemImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(PortletItemImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		PortletItemModelImpl portletItemModelImpl) {

		Object[] args = new Object[] {
			portletItemModelImpl.getGroupId(), portletItemModelImpl.getName(),
			portletItemModelImpl.getPortletId(),
			portletItemModelImpl.getClassNameId()
		};

		FinderCacheUtil.putResult(
			_finderPathFetchByG_N_P_C, args, portletItemModelImpl);
	}

	/**
	 * Creates a new portlet item with the primary key. Does not add the portlet item to the database.
	 *
	 * @param portletItemId the primary key for the new portlet item
	 * @return the new portlet item
	 */
	@Override
	public PortletItem create(long portletItemId) {
		PortletItem portletItem = new PortletItemImpl();

		portletItem.setNew(true);
		portletItem.setPrimaryKey(portletItemId);

		portletItem.setCompanyId(CompanyThreadLocal.getCompanyId());

		return portletItem;
	}

	/**
	 * Removes the portlet item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param portletItemId the primary key of the portlet item
	 * @return the portlet item that was removed
	 * @throws NoSuchPortletItemException if a portlet item with the primary key could not be found
	 */
	@Override
	public PortletItem remove(long portletItemId)
		throws NoSuchPortletItemException {

		return remove((Serializable)portletItemId);
	}

	/**
	 * Removes the portlet item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the portlet item
	 * @return the portlet item that was removed
	 * @throws NoSuchPortletItemException if a portlet item with the primary key could not be found
	 */
	@Override
	public PortletItem remove(Serializable primaryKey)
		throws NoSuchPortletItemException {

		Session session = null;

		try {
			session = openSession();

			PortletItem portletItem = (PortletItem)session.get(
				PortletItemImpl.class, primaryKey);

			if (portletItem == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPortletItemException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(portletItem);
		}
		catch (NoSuchPortletItemException noSuchEntityException) {
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
	protected PortletItem removeImpl(PortletItem portletItem) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(portletItem)) {
				portletItem = (PortletItem)session.get(
					PortletItemImpl.class, portletItem.getPrimaryKeyObj());
			}

			if (portletItem != null) {
				session.delete(portletItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (portletItem != null) {
			clearCache(portletItem);
		}

		return portletItem;
	}

	@Override
	public PortletItem updateImpl(PortletItem portletItem) {
		boolean isNew = portletItem.isNew();

		if (!(portletItem instanceof PortletItemModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(portletItem.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(portletItem);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in portletItem proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PortletItem implementation " +
					portletItem.getClass());
		}

		PortletItemModelImpl portletItemModelImpl =
			(PortletItemModelImpl)portletItem;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (portletItem.getCreateDate() == null)) {
			if (serviceContext == null) {
				portletItem.setCreateDate(date);
			}
			else {
				portletItem.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!portletItemModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				portletItem.setModifiedDate(date);
			}
			else {
				portletItem.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(portletItem);
			}
			else {
				portletItem = (PortletItem)session.merge(portletItem);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(
			PortletItemImpl.class, portletItemModelImpl, false, true);

		cacheUniqueFindersCache(portletItemModelImpl);

		if (isNew) {
			portletItem.setNew(false);
		}

		portletItem.resetOriginalValues();

		return portletItem;
	}

	/**
	 * Returns the portlet item with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the portlet item
	 * @return the portlet item
	 * @throws NoSuchPortletItemException if a portlet item with the primary key could not be found
	 */
	@Override
	public PortletItem findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPortletItemException {

		PortletItem portletItem = fetchByPrimaryKey(primaryKey);

		if (portletItem == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPortletItemException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return portletItem;
	}

	/**
	 * Returns the portlet item with the primary key or throws a <code>NoSuchPortletItemException</code> if it could not be found.
	 *
	 * @param portletItemId the primary key of the portlet item
	 * @return the portlet item
	 * @throws NoSuchPortletItemException if a portlet item with the primary key could not be found
	 */
	@Override
	public PortletItem findByPrimaryKey(long portletItemId)
		throws NoSuchPortletItemException {

		return findByPrimaryKey((Serializable)portletItemId);
	}

	/**
	 * Returns the portlet item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param portletItemId the primary key of the portlet item
	 * @return the portlet item, or <code>null</code> if a portlet item with the primary key could not be found
	 */
	@Override
	public PortletItem fetchByPrimaryKey(long portletItemId) {
		return fetchByPrimaryKey((Serializable)portletItemId);
	}

	/**
	 * Returns all the portlet items.
	 *
	 * @return the portlet items
	 */
	@Override
	public List<PortletItem> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the portlet items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portlet items
	 * @param end the upper bound of the range of portlet items (not inclusive)
	 * @return the range of portlet items
	 */
	@Override
	public List<PortletItem> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the portlet items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portlet items
	 * @param end the upper bound of the range of portlet items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of portlet items
	 */
	@Override
	public List<PortletItem> findAll(
		int start, int end, OrderByComparator<PortletItem> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the portlet items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortletItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portlet items
	 * @param end the upper bound of the range of portlet items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of portlet items
	 */
	@Override
	public List<PortletItem> findAll(
		int start, int end, OrderByComparator<PortletItem> orderByComparator,
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

		List<PortletItem> list = null;

		if (useFinderCache) {
			list = (List<PortletItem>)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_PORTLETITEM);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_PORTLETITEM;

				sql = sql.concat(PortletItemModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<PortletItem>)QueryUtil.list(
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

	/**
	 * Removes all the portlet items from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PortletItem portletItem : findAll()) {
			remove(portletItem);
		}
	}

	/**
	 * Returns the number of portlet items.
	 *
	 * @return the number of portlet items
	 */
	@Override
	public int countAll() {
		Long count = (Long)FinderCacheUtil.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_PORTLETITEM);

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

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "portletItemId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PORTLETITEM;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PortletItemModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the portlet item persistence.
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

		_finderPathWithPaginationFindByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classNameId"}, true);

		_finderPathCountByG_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"groupId", "classNameId"}, false);

		_finderPathWithPaginationFindByG_P_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_C",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "portletId", "classNameId"}, true);

		_finderPathWithoutPaginationFindByG_P_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_C",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "portletId", "classNameId"}, true);

		_finderPathCountByG_P_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_C",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "portletId", "classNameId"}, false);

		_finderPathFetchByG_N_P_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByG_N_P_C",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName(), Long.class.getName()
			},
			new String[] {"groupId", "name", "portletId", "classNameId"}, true);

		PortletItemUtil.setPersistence(this);
	}

	public void destroy() {
		PortletItemUtil.setPersistence(null);

		EntityCacheUtil.removeCache(PortletItemImpl.class.getName());
	}

	private static final String _SQL_SELECT_PORTLETITEM =
		"SELECT portletItem FROM PortletItem portletItem";

	private static final String _SQL_SELECT_PORTLETITEM_WHERE =
		"SELECT portletItem FROM PortletItem portletItem WHERE ";

	private static final String _SQL_COUNT_PORTLETITEM =
		"SELECT COUNT(portletItem) FROM PortletItem portletItem";

	private static final String _SQL_COUNT_PORTLETITEM_WHERE =
		"SELECT COUNT(portletItem) FROM PortletItem portletItem WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "portletItem.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PortletItem exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PortletItem exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletItemPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}