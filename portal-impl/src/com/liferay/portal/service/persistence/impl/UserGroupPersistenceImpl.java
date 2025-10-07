/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
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
import com.liferay.portal.kernel.exception.DuplicateUserGroupExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchUserGroupException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.TeamPersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupPersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupUtil;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.UserGroupImpl;
import com.liferay.portal.model.impl.UserGroupModelImpl;

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
 * The persistence implementation for the user group service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UserGroupPersistenceImpl
	extends BasePersistenceImpl<UserGroup> implements UserGroupPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UserGroupUtil</code> to access the user group persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UserGroupImpl.class.getName();

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
	 * Returns all the user groups where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching user groups
	 */
	@Override
	public List<UserGroup> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups
	 */
	@Override
	public List<UserGroup> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the user groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

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

			List<UserGroup> list = null;

			if (useFinderCache) {
				list = (List<UserGroup>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (UserGroup userGroup : list) {
						if (!uuid.equals(userGroup.getUuid())) {
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

				sb.append(_SQL_SELECT_USERGROUP_WHERE);

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
					sb.append(UserGroupModelImpl.ORDER_BY_JPQL);
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

					list = (List<UserGroup>)QueryUtil.list(
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
	 * Returns the first user group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByUuid_First(
			String uuid, OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByUuid_First(uuid, orderByComparator);

		if (userGroup != null) {
			return userGroup;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchUserGroupException(sb.toString());
	}

	/**
	 * Returns the first user group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByUuid_First(
		String uuid, OrderByComparator<UserGroup> orderByComparator) {

		List<UserGroup> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last user group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByUuid_Last(
			String uuid, OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByUuid_Last(uuid, orderByComparator);

		if (userGroup != null) {
			return userGroup;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchUserGroupException(sb.toString());
	}

	/**
	 * Returns the last user group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByUuid_Last(
		String uuid, OrderByComparator<UserGroup> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<UserGroup> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the user groups before and after the current user group in the ordered set where uuid = &#63;.
	 *
	 * @param userGroupId the primary key of the current user group
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup[] findByUuid_PrevAndNext(
			long userGroupId, String uuid,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		uuid = Objects.toString(uuid, "");

		UserGroup userGroup = findByPrimaryKey(userGroupId);

		Session session = null;

		try {
			session = openSession();

			UserGroup[] array = new UserGroupImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, userGroup, uuid, orderByComparator, true);

			array[1] = userGroup;

			array[2] = getByUuid_PrevAndNext(
				session, userGroup, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected UserGroup getByUuid_PrevAndNext(
		Session session, UserGroup userGroup, String uuid,
		OrderByComparator<UserGroup> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_USERGROUP_WHERE);

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
			sb.append(UserGroupModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(userGroup)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<UserGroup> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the user groups that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByUuid(String uuid) {
		return filterFindByUuid(
			uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByUuid(String uuid, int start, int end) {
		return filterFindByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_1);
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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(UserGroupModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(UserGroupModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, UserGroupImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, UserGroupImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			return (List<UserGroup>)QueryUtil.list(
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
	 * Returns the user groups before and after the current user group in the ordered set of user groups that the user has permission to view where uuid = &#63;.
	 *
	 * @param userGroupId the primary key of the current user group
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup[] filterFindByUuid_PrevAndNext(
			long userGroupId, String uuid,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		if (!InlineSQLHelperUtil.isEnabled()) {
			return findByUuid_PrevAndNext(userGroupId, uuid, orderByComparator);
		}

		uuid = Objects.toString(uuid, "");

		UserGroup userGroup = findByPrimaryKey(userGroupId);

		Session session = null;

		try {
			session = openSession();

			UserGroup[] array = new UserGroupImpl[3];

			array[0] = filterGetByUuid_PrevAndNext(
				session, userGroup, uuid, orderByComparator, true);

			array[1] = userGroup;

			array[2] = filterGetByUuid_PrevAndNext(
				session, userGroup, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected UserGroup filterGetByUuid_PrevAndNext(
		Session session, UserGroup userGroup, String uuid,
		OrderByComparator<UserGroup> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_1);
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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(UserGroupModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(UserGroupModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, UserGroupImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, UserGroupImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(userGroup)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<UserGroup> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the user groups where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (UserGroup userGroup :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(userGroup);
		}
	}

	/**
	 * Returns the number of user groups where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching user groups
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_USERGROUP_WHERE);

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

	/**
	 * Returns the number of user groups that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching user groups that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		if (!InlineSQLHelperUtil.isEnabled()) {
			return countByUuid(uuid);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<UserGroup> userGroups = findByUuid(uuid);

			userGroups = InlineSQLHelperUtil.filter(userGroups);

			return userGroups.size();
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_USERGROUP_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3_SQL);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2_SQL);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
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
		"userGroup.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(userGroup.uuid IS NULL OR userGroup.uuid = '')";

	private static final String _FINDER_COLUMN_UUID_UUID_2_SQL =
		"userGroup.uuid_ = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3_SQL =
		"(userGroup.uuid_ IS NULL OR userGroup.uuid_ = '')";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the user groups where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching user groups
	 */
	@Override
	public List<UserGroup> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups
	 */
	@Override
	public List<UserGroup> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the user groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

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

			List<UserGroup> list = null;

			if (useFinderCache) {
				list = (List<UserGroup>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (UserGroup userGroup : list) {
						if (!uuid.equals(userGroup.getUuid()) ||
							(companyId != userGroup.getCompanyId())) {

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

				sb.append(_SQL_SELECT_USERGROUP_WHERE);

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
					sb.append(UserGroupModelImpl.ORDER_BY_JPQL);
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

					list = (List<UserGroup>)QueryUtil.list(
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
	 * Returns the first user group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (userGroup != null) {
			return userGroup;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchUserGroupException(sb.toString());
	}

	/**
	 * Returns the first user group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<UserGroup> orderByComparator) {

		List<UserGroup> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last user group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (userGroup != null) {
			return userGroup;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchUserGroupException(sb.toString());
	}

	/**
	 * Returns the last user group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<UserGroup> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<UserGroup> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the user groups before and after the current user group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param userGroupId the primary key of the current user group
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup[] findByUuid_C_PrevAndNext(
			long userGroupId, String uuid, long companyId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		uuid = Objects.toString(uuid, "");

		UserGroup userGroup = findByPrimaryKey(userGroupId);

		Session session = null;

		try {
			session = openSession();

			UserGroup[] array = new UserGroupImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, userGroup, uuid, companyId, orderByComparator, true);

			array[1] = userGroup;

			array[2] = getByUuid_C_PrevAndNext(
				session, userGroup, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected UserGroup getByUuid_C_PrevAndNext(
		Session session, UserGroup userGroup, String uuid, long companyId,
		OrderByComparator<UserGroup> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_USERGROUP_WHERE);

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
			sb.append(UserGroupModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(userGroup)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<UserGroup> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the user groups that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByUuid_C(String uuid, long companyId) {
		return filterFindByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByUuid_C(
		String uuid, long companyId, int start, int end) {

		return filterFindByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_1);
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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(UserGroupModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(UserGroupModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, UserGroupImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, UserGroupImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			if (bindUuid) {
				queryPos.add(uuid);
			}

			queryPos.add(companyId);

			return (List<UserGroup>)QueryUtil.list(
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
	 * Returns the user groups before and after the current user group in the ordered set of user groups that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param userGroupId the primary key of the current user group
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup[] filterFindByUuid_C_PrevAndNext(
			long userGroupId, String uuid, long companyId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByUuid_C_PrevAndNext(
				userGroupId, uuid, companyId, orderByComparator);
		}

		uuid = Objects.toString(uuid, "");

		UserGroup userGroup = findByPrimaryKey(userGroupId);

		Session session = null;

		try {
			session = openSession();

			UserGroup[] array = new UserGroupImpl[3];

			array[0] = filterGetByUuid_C_PrevAndNext(
				session, userGroup, uuid, companyId, orderByComparator, true);

			array[1] = userGroup;

			array[2] = filterGetByUuid_C_PrevAndNext(
				session, userGroup, uuid, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected UserGroup filterGetByUuid_C_PrevAndNext(
		Session session, UserGroup userGroup, String uuid, long companyId,
		OrderByComparator<UserGroup> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_1);
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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(UserGroupModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(UserGroupModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, UserGroupImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, UserGroupImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(userGroup)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<UserGroup> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the user groups where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (UserGroup userGroup :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(userGroup);
		}
	}

	/**
	 * Returns the number of user groups where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching user groups
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_USERGROUP_WHERE);

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

	/**
	 * Returns the number of user groups that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching user groups that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByUuid_C(uuid, companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<UserGroup> userGroups = findByUuid_C(uuid, companyId);

			userGroups = InlineSQLHelperUtil.filter(userGroups);

			return userGroups.size();
		}

		uuid = Objects.toString(uuid, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_USERGROUP_WHERE);

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
			sb.toString(), UserGroup.class.getName(),
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
		"userGroup.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(userGroup.uuid IS NULL OR userGroup.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_2_SQL =
		"userGroup.uuid_ = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3_SQL =
		"(userGroup.uuid_ IS NULL OR userGroup.uuid_ = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"userGroup.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the user groups where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching user groups
	 */
	@Override
	public List<UserGroup> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups
	 */
	@Override
	public List<UserGroup> findByCompanyId(long companyId, int start, int end) {
		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the user groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

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

			List<UserGroup> list = null;

			if (useFinderCache) {
				list = (List<UserGroup>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (UserGroup userGroup : list) {
						if (companyId != userGroup.getCompanyId()) {
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

				sb.append(_SQL_SELECT_USERGROUP_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(UserGroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<UserGroup>)QueryUtil.list(
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
	 * Returns the first user group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByCompanyId_First(
			long companyId, OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (userGroup != null) {
			return userGroup;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchUserGroupException(sb.toString());
	}

	/**
	 * Returns the first user group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByCompanyId_First(
		long companyId, OrderByComparator<UserGroup> orderByComparator) {

		List<UserGroup> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last user group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByCompanyId_Last(
			long companyId, OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (userGroup != null) {
			return userGroup;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchUserGroupException(sb.toString());
	}

	/**
	 * Returns the last user group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByCompanyId_Last(
		long companyId, OrderByComparator<UserGroup> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<UserGroup> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the user groups before and after the current user group in the ordered set where companyId = &#63;.
	 *
	 * @param userGroupId the primary key of the current user group
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup[] findByCompanyId_PrevAndNext(
			long userGroupId, long companyId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = findByPrimaryKey(userGroupId);

		Session session = null;

		try {
			session = openSession();

			UserGroup[] array = new UserGroupImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, userGroup, companyId, orderByComparator, true);

			array[1] = userGroup;

			array[2] = getByCompanyId_PrevAndNext(
				session, userGroup, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected UserGroup getByCompanyId_PrevAndNext(
		Session session, UserGroup userGroup, long companyId,
		OrderByComparator<UserGroup> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_USERGROUP_WHERE);

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
			sb.append(UserGroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(userGroup)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<UserGroup> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the user groups that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByCompanyId(long companyId) {
		return filterFindByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(UserGroupModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(UserGroupModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, UserGroupImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, UserGroupImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			return (List<UserGroup>)QueryUtil.list(
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
	 * Returns the user groups before and after the current user group in the ordered set of user groups that the user has permission to view where companyId = &#63;.
	 *
	 * @param userGroupId the primary key of the current user group
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup[] filterFindByCompanyId_PrevAndNext(
			long userGroupId, long companyId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByCompanyId_PrevAndNext(
				userGroupId, companyId, orderByComparator);
		}

		UserGroup userGroup = findByPrimaryKey(userGroupId);

		Session session = null;

		try {
			session = openSession();

			UserGroup[] array = new UserGroupImpl[3];

			array[0] = filterGetByCompanyId_PrevAndNext(
				session, userGroup, companyId, orderByComparator, true);

			array[1] = userGroup;

			array[2] = filterGetByCompanyId_PrevAndNext(
				session, userGroup, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected UserGroup filterGetByCompanyId_PrevAndNext(
		Session session, UserGroup userGroup, long companyId,
		OrderByComparator<UserGroup> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(UserGroupModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(UserGroupModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, UserGroupImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, UserGroupImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(userGroup)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<UserGroup> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the user groups where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (UserGroup userGroup :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(userGroup);
		}
	}

	/**
	 * Returns the number of user groups where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching user groups
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_USERGROUP_WHERE);

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

	/**
	 * Returns the number of user groups that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching user groups that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByCompanyId(companyId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<UserGroup> userGroups = findByCompanyId(companyId);

			userGroups = InlineSQLHelperUtil.filter(userGroups);

			return userGroups.size();
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_USERGROUP_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
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
		"userGroup.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByC_P;
	private FinderPath _finderPathWithoutPaginationFindByC_P;
	private FinderPath _finderPathCountByC_P;

	/**
	 * Returns all the user groups where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the matching user groups
	 */
	@Override
	public List<UserGroup> findByC_P(long companyId, long parentUserGroupId) {
		return findByC_P(
			companyId, parentUserGroupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the user groups where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups
	 */
	@Override
	public List<UserGroup> findByC_P(
		long companyId, long parentUserGroupId, int start, int end) {

		return findByC_P(companyId, parentUserGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByC_P(
		long companyId, long parentUserGroupId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return findByC_P(
			companyId, parentUserGroupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the user groups where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByC_P(
		long companyId, long parentUserGroupId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_P;
					finderArgs = new Object[] {companyId, parentUserGroupId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_P;
				finderArgs = new Object[] {
					companyId, parentUserGroupId, start, end, orderByComparator
				};
			}

			List<UserGroup> list = null;

			if (useFinderCache) {
				list = (List<UserGroup>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (UserGroup userGroup : list) {
						if ((companyId != userGroup.getCompanyId()) ||
							(parentUserGroupId !=
								userGroup.getParentUserGroupId())) {

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

				sb.append(_SQL_SELECT_USERGROUP_WHERE);

				sb.append(_FINDER_COLUMN_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_P_PARENTUSERGROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(UserGroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(parentUserGroupId);

					list = (List<UserGroup>)QueryUtil.list(
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
	 * Returns the first user group in the ordered set where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByC_P_First(
			long companyId, long parentUserGroupId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByC_P_First(
			companyId, parentUserGroupId, orderByComparator);

		if (userGroup != null) {
			return userGroup;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", parentUserGroupId=");
		sb.append(parentUserGroupId);

		sb.append("}");

		throw new NoSuchUserGroupException(sb.toString());
	}

	/**
	 * Returns the first user group in the ordered set where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByC_P_First(
		long companyId, long parentUserGroupId,
		OrderByComparator<UserGroup> orderByComparator) {

		List<UserGroup> list = findByC_P(
			companyId, parentUserGroupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last user group in the ordered set where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByC_P_Last(
			long companyId, long parentUserGroupId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByC_P_Last(
			companyId, parentUserGroupId, orderByComparator);

		if (userGroup != null) {
			return userGroup;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", parentUserGroupId=");
		sb.append(parentUserGroupId);

		sb.append("}");

		throw new NoSuchUserGroupException(sb.toString());
	}

	/**
	 * Returns the last user group in the ordered set where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByC_P_Last(
		long companyId, long parentUserGroupId,
		OrderByComparator<UserGroup> orderByComparator) {

		int count = countByC_P(companyId, parentUserGroupId);

		if (count == 0) {
			return null;
		}

		List<UserGroup> list = findByC_P(
			companyId, parentUserGroupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the user groups before and after the current user group in the ordered set where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the primary key of the current user group
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup[] findByC_P_PrevAndNext(
			long userGroupId, long companyId, long parentUserGroupId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = findByPrimaryKey(userGroupId);

		Session session = null;

		try {
			session = openSession();

			UserGroup[] array = new UserGroupImpl[3];

			array[0] = getByC_P_PrevAndNext(
				session, userGroup, companyId, parentUserGroupId,
				orderByComparator, true);

			array[1] = userGroup;

			array[2] = getByC_P_PrevAndNext(
				session, userGroup, companyId, parentUserGroupId,
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

	protected UserGroup getByC_P_PrevAndNext(
		Session session, UserGroup userGroup, long companyId,
		long parentUserGroupId, OrderByComparator<UserGroup> orderByComparator,
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

		sb.append(_SQL_SELECT_USERGROUP_WHERE);

		sb.append(_FINDER_COLUMN_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_P_PARENTUSERGROUPID_2);

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
			sb.append(UserGroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(parentUserGroupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(userGroup)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<UserGroup> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the user groups that the user has permission to view where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByC_P(
		long companyId, long parentUserGroupId) {

		return filterFindByC_P(
			companyId, parentUserGroupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the user groups that the user has permission to view where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByC_P(
		long companyId, long parentUserGroupId, int start, int end) {

		return filterFindByC_P(companyId, parentUserGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups that the user has permissions to view where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByC_P(
		long companyId, long parentUserGroupId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_P(
				companyId, parentUserGroupId, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_P(
					companyId, parentUserGroupId, QueryUtil.ALL_POS,
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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_P_PARENTUSERGROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(UserGroupModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(UserGroupModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, UserGroupImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, UserGroupImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(parentUserGroupId);

			return (List<UserGroup>)QueryUtil.list(
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
	 * Returns the user groups before and after the current user group in the ordered set of user groups that the user has permission to view where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the primary key of the current user group
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup[] filterFindByC_P_PrevAndNext(
			long userGroupId, long companyId, long parentUserGroupId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_P_PrevAndNext(
				userGroupId, companyId, parentUserGroupId, orderByComparator);
		}

		UserGroup userGroup = findByPrimaryKey(userGroupId);

		Session session = null;

		try {
			session = openSession();

			UserGroup[] array = new UserGroupImpl[3];

			array[0] = filterGetByC_P_PrevAndNext(
				session, userGroup, companyId, parentUserGroupId,
				orderByComparator, true);

			array[1] = userGroup;

			array[2] = filterGetByC_P_PrevAndNext(
				session, userGroup, companyId, parentUserGroupId,
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

	protected UserGroup filterGetByC_P_PrevAndNext(
		Session session, UserGroup userGroup, long companyId,
		long parentUserGroupId, OrderByComparator<UserGroup> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_P_PARENTUSERGROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(UserGroupModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(UserGroupModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, UserGroupImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, UserGroupImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		queryPos.add(parentUserGroupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(userGroup)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<UserGroup> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the user groups where companyId = &#63; and parentUserGroupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 */
	@Override
	public void removeByC_P(long companyId, long parentUserGroupId) {
		for (UserGroup userGroup :
				findByC_P(
					companyId, parentUserGroupId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(userGroup);
		}
	}

	/**
	 * Returns the number of user groups where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the number of matching user groups
	 */
	@Override
	public int countByC_P(long companyId, long parentUserGroupId) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

			FinderPath finderPath = _finderPathCountByC_P;

			Object[] finderArgs = new Object[] {companyId, parentUserGroupId};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_USERGROUP_WHERE);

				sb.append(_FINDER_COLUMN_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_P_PARENTUSERGROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(parentUserGroupId);

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
	 * Returns the number of user groups that the user has permission to view where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the number of matching user groups that the user has permission to view
	 */
	@Override
	public int filterCountByC_P(long companyId, long parentUserGroupId) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_P(companyId, parentUserGroupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<UserGroup> userGroups = findByC_P(
				companyId, parentUserGroupId);

			userGroups = InlineSQLHelperUtil.filter(userGroups);

			return userGroups.size();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_USERGROUP_WHERE);

		sb.append(_FINDER_COLUMN_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_P_PARENTUSERGROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			queryPos.add(parentUserGroupId);

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

	private static final String _FINDER_COLUMN_C_P_COMPANYID_2 =
		"userGroup.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_P_PARENTUSERGROUPID_2 =
		"userGroup.parentUserGroupId = ?";

	private FinderPath _finderPathFetchByC_N;

	/**
	 * Returns the user group where companyId = &#63; and name = &#63; or throws a <code>NoSuchUserGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByC_N(long companyId, String name)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByC_N(companyId, name);

		if (userGroup == null) {
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

			throw new NoSuchUserGroupException(sb.toString());
		}

		return userGroup;
	}

	/**
	 * Returns the user group where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByC_N(long companyId, String name) {
		return fetchByC_N(companyId, name, true);
	}

	/**
	 * Returns the user group where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

			name = Objects.toString(name, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {companyId, name};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByC_N, finderArgs, this);
			}

			if (result instanceof UserGroup) {
				UserGroup userGroup = (UserGroup)result;

				if ((companyId != userGroup.getCompanyId()) ||
					!Objects.equals(name, userGroup.getName())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_USERGROUP_WHERE);

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
						queryPos.add(StringUtil.toLowerCase(name));
					}

					List<UserGroup> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByC_N, finderArgs, list);
						}
					}
					else {
						UserGroup userGroup = list.get(0);

						result = userGroup;

						cacheResult(userGroup);
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
				return (UserGroup)result;
			}
		}
	}

	/**
	 * Removes the user group where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the user group that was removed
	 */
	@Override
	public UserGroup removeByC_N(long companyId, String name)
		throws NoSuchUserGroupException {

		UserGroup userGroup = findByC_N(companyId, name);

		return remove(userGroup);
	}

	/**
	 * Returns the number of user groups where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching user groups
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		UserGroup userGroup = fetchByC_N(companyId, name);

		if (userGroup == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_C_N_COMPANYID_2 =
		"userGroup.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_N_NAME_2 =
		"lower(userGroup.name) = ?";

	private static final String _FINDER_COLUMN_C_N_NAME_3 =
		"(userGroup.name IS NULL OR userGroup.name = '')";

	private FinderPath _finderPathWithPaginationFindByC_LikeN;
	private FinderPath _finderPathWithPaginationCountByC_LikeN;

	/**
	 * Returns all the user groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching user groups
	 */
	@Override
	public List<UserGroup> findByC_LikeN(long companyId, String name) {
		return findByC_LikeN(
			companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups
	 */
	@Override
	public List<UserGroup> findByC_LikeN(
		long companyId, String name, int start, int end) {

		return findByC_LikeN(companyId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByC_LikeN(
		long companyId, String name, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return findByC_LikeN(
			companyId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the user groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByC_LikeN(
		long companyId, String name, int start, int end,
		OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_LikeN;
			finderArgs = new Object[] {
				companyId, name, start, end, orderByComparator
			};

			List<UserGroup> list = null;

			if (useFinderCache) {
				list = (List<UserGroup>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (UserGroup userGroup : list) {
						if ((companyId != userGroup.getCompanyId()) ||
							!StringUtil.wildcardMatches(
								userGroup.getName(), name, '_', '%', '\\',
								false)) {

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

				sb.append(_SQL_SELECT_USERGROUP_WHERE);

				sb.append(_FINDER_COLUMN_C_LIKEN_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_LIKEN_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_LIKEN_NAME_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(UserGroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindName) {
						queryPos.add(StringUtil.toLowerCase(name));
					}

					list = (List<UserGroup>)QueryUtil.list(
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
	 * Returns the first user group in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByC_LikeN_First(
			long companyId, String name,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByC_LikeN_First(
			companyId, name, orderByComparator);

		if (userGroup != null) {
			return userGroup;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append("}");

		throw new NoSuchUserGroupException(sb.toString());
	}

	/**
	 * Returns the first user group in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByC_LikeN_First(
		long companyId, String name,
		OrderByComparator<UserGroup> orderByComparator) {

		List<UserGroup> list = findByC_LikeN(
			companyId, name, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last user group in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByC_LikeN_Last(
			long companyId, String name,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByC_LikeN_Last(
			companyId, name, orderByComparator);

		if (userGroup != null) {
			return userGroup;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", nameLIKE");
		sb.append(name);

		sb.append("}");

		throw new NoSuchUserGroupException(sb.toString());
	}

	/**
	 * Returns the last user group in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByC_LikeN_Last(
		long companyId, String name,
		OrderByComparator<UserGroup> orderByComparator) {

		int count = countByC_LikeN(companyId, name);

		if (count == 0) {
			return null;
		}

		List<UserGroup> list = findByC_LikeN(
			companyId, name, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the user groups before and after the current user group in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param userGroupId the primary key of the current user group
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup[] findByC_LikeN_PrevAndNext(
			long userGroupId, long companyId, String name,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		name = Objects.toString(name, "");

		UserGroup userGroup = findByPrimaryKey(userGroupId);

		Session session = null;

		try {
			session = openSession();

			UserGroup[] array = new UserGroupImpl[3];

			array[0] = getByC_LikeN_PrevAndNext(
				session, userGroup, companyId, name, orderByComparator, true);

			array[1] = userGroup;

			array[2] = getByC_LikeN_PrevAndNext(
				session, userGroup, companyId, name, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected UserGroup getByC_LikeN_PrevAndNext(
		Session session, UserGroup userGroup, long companyId, String name,
		OrderByComparator<UserGroup> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_USERGROUP_WHERE);

		sb.append(_FINDER_COLUMN_C_LIKEN_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKEN_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_LIKEN_NAME_2);
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
			sb.append(UserGroupModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (bindName) {
			queryPos.add(StringUtil.toLowerCase(name));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(userGroup)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<UserGroup> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the user groups that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByC_LikeN(long companyId, String name) {
		return filterFindByC_LikeN(
			companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByC_LikeN(
		long companyId, String name, int start, int end) {

		return filterFindByC_LikeN(companyId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups that the user has permissions to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByC_LikeN(
		long companyId, String name, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_LikeN(
				companyId, name, start, end, orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByC_LikeN(
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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_LIKEN_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKEN_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_LIKEN_NAME_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(UserGroupModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(UserGroupModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, UserGroupImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, UserGroupImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(companyId);

			if (bindName) {
				queryPos.add(StringUtil.toLowerCase(name));
			}

			return (List<UserGroup>)QueryUtil.list(
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
	 * Returns the user groups before and after the current user group in the ordered set of user groups that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param userGroupId the primary key of the current user group
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup[] filterFindByC_LikeN_PrevAndNext(
			long userGroupId, long companyId, String name,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByC_LikeN_PrevAndNext(
				userGroupId, companyId, name, orderByComparator);
		}

		name = Objects.toString(name, "");

		UserGroup userGroup = findByPrimaryKey(userGroupId);

		Session session = null;

		try {
			session = openSession();

			UserGroup[] array = new UserGroupImpl[3];

			array[0] = filterGetByC_LikeN_PrevAndNext(
				session, userGroup, companyId, name, orderByComparator, true);

			array[1] = userGroup;

			array[2] = filterGetByC_LikeN_PrevAndNext(
				session, userGroup, companyId, name, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected UserGroup filterGetByC_LikeN_PrevAndNext(
		Session session, UserGroup userGroup, long companyId, String name,
		OrderByComparator<UserGroup> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_C_LIKEN_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKEN_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_LIKEN_NAME_2);
		}

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(UserGroupModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(UserGroupModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, UserGroupImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, UserGroupImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(companyId);

		if (bindName) {
			queryPos.add(StringUtil.toLowerCase(name));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(userGroup)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<UserGroup> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the user groups where companyId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 */
	@Override
	public void removeByC_LikeN(long companyId, String name) {
		for (UserGroup userGroup :
				findByC_LikeN(
					companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(userGroup);
		}
	}

	/**
	 * Returns the number of user groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching user groups
	 */
	@Override
	public int countByC_LikeN(long companyId, String name) {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

			name = Objects.toString(name, "");

			FinderPath finderPath = _finderPathWithPaginationCountByC_LikeN;

			Object[] finderArgs = new Object[] {companyId, name};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_USERGROUP_WHERE);

				sb.append(_FINDER_COLUMN_C_LIKEN_COMPANYID_2);

				boolean bindName = false;

				if (name.isEmpty()) {
					sb.append(_FINDER_COLUMN_C_LIKEN_NAME_3);
				}
				else {
					bindName = true;

					sb.append(_FINDER_COLUMN_C_LIKEN_NAME_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					if (bindName) {
						queryPos.add(StringUtil.toLowerCase(name));
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
	 * Returns the number of user groups that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching user groups that the user has permission to view
	 */
	@Override
	public int filterCountByC_LikeN(long companyId, String name) {
		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByC_LikeN(companyId, name);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<UserGroup> userGroups = findByC_LikeN(companyId, name);

			userGroups = InlineSQLHelperUtil.filter(userGroups);

			return userGroups.size();
		}

		name = Objects.toString(name, "");

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_USERGROUP_WHERE);

		sb.append(_FINDER_COLUMN_C_LIKEN_COMPANYID_2);

		boolean bindName = false;

		if (name.isEmpty()) {
			sb.append(_FINDER_COLUMN_C_LIKEN_NAME_3);
		}
		else {
			bindName = true;

			sb.append(_FINDER_COLUMN_C_LIKEN_NAME_2);
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
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
				queryPos.add(StringUtil.toLowerCase(name));
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

	private static final String _FINDER_COLUMN_C_LIKEN_COMPANYID_2 =
		"userGroup.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_LIKEN_NAME_2 =
		"lower(userGroup.name) LIKE ?";

	private static final String _FINDER_COLUMN_C_LIKEN_NAME_3 =
		"(userGroup.name IS NULL OR userGroup.name LIKE '')";

	private FinderPath _finderPathWithPaginationFindByGtU_C_P;
	private FinderPath _finderPathWithPaginationCountByGtU_C_P;

	/**
	 * Returns all the user groups where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the matching user groups
	 */
	@Override
	public List<UserGroup> findByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId) {

		return findByGtU_C_P(
			userGroupId, companyId, parentUserGroupId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups
	 */
	@Override
	public List<UserGroup> findByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId, int start,
		int end) {

		return findByGtU_C_P(
			userGroupId, companyId, parentUserGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId, int start,
		int end, OrderByComparator<UserGroup> orderByComparator) {

		return findByGtU_C_P(
			userGroupId, companyId, parentUserGroupId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the user groups where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId, int start,
		int end, OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByGtU_C_P;
			finderArgs = new Object[] {
				userGroupId, companyId, parentUserGroupId, start, end,
				orderByComparator
			};

			List<UserGroup> list = null;

			if (useFinderCache) {
				list = (List<UserGroup>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (UserGroup userGroup : list) {
						if ((userGroupId >= userGroup.getUserGroupId()) ||
							(companyId != userGroup.getCompanyId()) ||
							(parentUserGroupId !=
								userGroup.getParentUserGroupId())) {

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

				sb.append(_SQL_SELECT_USERGROUP_WHERE);

				sb.append(_FINDER_COLUMN_GTU_C_P_USERGROUPID_2);

				sb.append(_FINDER_COLUMN_GTU_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTU_C_P_PARENTUSERGROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(UserGroupModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userGroupId);

					queryPos.add(companyId);

					queryPos.add(parentUserGroupId);

					list = (List<UserGroup>)QueryUtil.list(
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
	 * Returns the first user group in the ordered set where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByGtU_C_P_First(
			long userGroupId, long companyId, long parentUserGroupId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByGtU_C_P_First(
			userGroupId, companyId, parentUserGroupId, orderByComparator);

		if (userGroup != null) {
			return userGroup;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userGroupId>");
		sb.append(userGroupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentUserGroupId=");
		sb.append(parentUserGroupId);

		sb.append("}");

		throw new NoSuchUserGroupException(sb.toString());
	}

	/**
	 * Returns the first user group in the ordered set where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByGtU_C_P_First(
		long userGroupId, long companyId, long parentUserGroupId,
		OrderByComparator<UserGroup> orderByComparator) {

		List<UserGroup> list = findByGtU_C_P(
			userGroupId, companyId, parentUserGroupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last user group in the ordered set where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByGtU_C_P_Last(
			long userGroupId, long companyId, long parentUserGroupId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByGtU_C_P_Last(
			userGroupId, companyId, parentUserGroupId, orderByComparator);

		if (userGroup != null) {
			return userGroup;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userGroupId>");
		sb.append(userGroupId);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append(", parentUserGroupId=");
		sb.append(parentUserGroupId);

		sb.append("}");

		throw new NoSuchUserGroupException(sb.toString());
	}

	/**
	 * Returns the last user group in the ordered set where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByGtU_C_P_Last(
		long userGroupId, long companyId, long parentUserGroupId,
		OrderByComparator<UserGroup> orderByComparator) {

		int count = countByGtU_C_P(userGroupId, companyId, parentUserGroupId);

		if (count == 0) {
			return null;
		}

		List<UserGroup> list = findByGtU_C_P(
			userGroupId, companyId, parentUserGroupId, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns all the user groups that the user has permission to view where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId) {

		return filterFindByGtU_C_P(
			userGroupId, companyId, parentUserGroupId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups that the user has permission to view where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId, int start,
		int end) {

		return filterFindByGtU_C_P(
			userGroupId, companyId, parentUserGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups that the user has permissions to view where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId, int start,
		int end, OrderByComparator<UserGroup> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return findByGtU_C_P(
				userGroupId, companyId, parentUserGroupId, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByGtU_C_P(
					userGroupId, companyId, parentUserGroupId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator));
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
			sb.append(_FILTER_SQL_SELECT_USERGROUP_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GTU_C_P_USERGROUPID_2);

		sb.append(_FINDER_COLUMN_GTU_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_GTU_C_P_PARENTUSERGROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(UserGroupModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(UserGroupModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, UserGroupImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, UserGroupImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(userGroupId);

			queryPos.add(companyId);

			queryPos.add(parentUserGroupId);

			return (List<UserGroup>)QueryUtil.list(
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
	 * Removes all the user groups where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63; from the database.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 */
	@Override
	public void removeByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId) {

		for (UserGroup userGroup :
				findByGtU_C_P(
					userGroupId, companyId, parentUserGroupId,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(userGroup);
		}
	}

	/**
	 * Returns the number of user groups where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the number of matching user groups
	 */
	@Override
	public int countByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByGtU_C_P;

			Object[] finderArgs = new Object[] {
				userGroupId, companyId, parentUserGroupId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_USERGROUP_WHERE);

				sb.append(_FINDER_COLUMN_GTU_C_P_USERGROUPID_2);

				sb.append(_FINDER_COLUMN_GTU_C_P_COMPANYID_2);

				sb.append(_FINDER_COLUMN_GTU_C_P_PARENTUSERGROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userGroupId);

					queryPos.add(companyId);

					queryPos.add(parentUserGroupId);

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
	 * Returns the number of user groups that the user has permission to view where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the number of matching user groups that the user has permission to view
	 */
	@Override
	public int filterCountByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId) {

		if (!InlineSQLHelperUtil.isEnabled(companyId, 0)) {
			return countByGtU_C_P(userGroupId, companyId, parentUserGroupId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<UserGroup> userGroups = findByGtU_C_P(
				userGroupId, companyId, parentUserGroupId);

			userGroups = InlineSQLHelperUtil.filter(userGroups);

			return userGroups.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_USERGROUP_WHERE);

		sb.append(_FINDER_COLUMN_GTU_C_P_USERGROUPID_2);

		sb.append(_FINDER_COLUMN_GTU_C_P_COMPANYID_2);

		sb.append(_FINDER_COLUMN_GTU_C_P_PARENTUSERGROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), UserGroup.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(userGroupId);

			queryPos.add(companyId);

			queryPos.add(parentUserGroupId);

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

	private static final String _FINDER_COLUMN_GTU_C_P_USERGROUPID_2 =
		"userGroup.userGroupId > ? AND ";

	private static final String _FINDER_COLUMN_GTU_C_P_COMPANYID_2 =
		"userGroup.companyId = ? AND ";

	private static final String _FINDER_COLUMN_GTU_C_P_PARENTUSERGROUPID_2 =
		"userGroup.parentUserGroupId = ?";

	private FinderPath _finderPathFetchByERC_C;

	/**
	 * Returns the user group where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchUserGroupException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByERC_C(externalReferenceCode, companyId);

		if (userGroup == null) {
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

			throw new NoSuchUserGroupException(sb.toString());
		}

		return userGroup;
	}

	/**
	 * Returns the user group where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the user group where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {externalReferenceCode, companyId};
			}

			Object result = null;

			if (useFinderCache) {
				result = FinderCacheUtil.getResult(
					_finderPathFetchByERC_C, finderArgs, this);
			}

			if (result instanceof UserGroup) {
				UserGroup userGroup = (UserGroup)result;

				if (!Objects.equals(
						externalReferenceCode,
						userGroup.getExternalReferenceCode()) ||
					(companyId != userGroup.getCompanyId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_USERGROUP_WHERE);

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

					List<UserGroup> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							FinderCacheUtil.putResult(
								_finderPathFetchByERC_C, finderArgs, list);
						}
					}
					else {
						UserGroup userGroup = list.get(0);

						result = userGroup;

						cacheResult(userGroup);
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
				return (UserGroup)result;
			}
		}
	}

	/**
	 * Removes the user group where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the user group that was removed
	 */
	@Override
	public UserGroup removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchUserGroupException {

		UserGroup userGroup = findByERC_C(externalReferenceCode, companyId);

		return remove(userGroup);
	}

	/**
	 * Returns the number of user groups where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching user groups
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		UserGroup userGroup = fetchByERC_C(externalReferenceCode, companyId);

		if (userGroup == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_2 =
		"userGroup.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_C_EXTERNALREFERENCECODE_3 =
		"(userGroup.externalReferenceCode IS NULL OR userGroup.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_C_COMPANYID_2 =
		"userGroup.companyId = ?";

	public UserGroupPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("groups", "groups_");

		setDBColumnNames(dbColumnNames);

		setModelClass(UserGroup.class);

		setModelImplClass(UserGroupImpl.class);
		setModelPKClass(long.class);

		setTable(UserGroupTable.INSTANCE);
	}

	/**
	 * Caches the user group in the entity cache if it is enabled.
	 *
	 * @param userGroup the user group
	 */
	@Override
	public void cacheResult(UserGroup userGroup) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					userGroup.getCtCollectionId())) {

			EntityCacheUtil.putResult(
				UserGroupImpl.class, userGroup.getPrimaryKey(), userGroup);

			FinderCacheUtil.putResult(
				_finderPathFetchByC_N,
				new Object[] {userGroup.getCompanyId(), userGroup.getName()},
				userGroup);

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_C,
				new Object[] {
					userGroup.getExternalReferenceCode(),
					userGroup.getCompanyId()
				},
				userGroup);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the user groups in the entity cache if it is enabled.
	 *
	 * @param userGroups the user groups
	 */
	@Override
	public void cacheResult(List<UserGroup> userGroups) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (userGroups.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (UserGroup userGroup : userGroups) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						userGroup.getCtCollectionId())) {

				if (EntityCacheUtil.getResult(
						UserGroupImpl.class, userGroup.getPrimaryKey()) ==
							null) {

					cacheResult(userGroup);
				}
			}
		}
	}

	/**
	 * Clears the cache for all user groups.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		EntityCacheUtil.clearCache(UserGroupImpl.class);

		FinderCacheUtil.clearCache(UserGroupImpl.class);
	}

	/**
	 * Clears the cache for the user group.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(UserGroup userGroup) {
		EntityCacheUtil.removeResult(UserGroupImpl.class, userGroup);
	}

	@Override
	public void clearCache(List<UserGroup> userGroups) {
		for (UserGroup userGroup : userGroups) {
			EntityCacheUtil.removeResult(UserGroupImpl.class, userGroup);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		FinderCacheUtil.clearCache(UserGroupImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			EntityCacheUtil.removeResult(UserGroupImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		UserGroupModelImpl userGroupModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					userGroupModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				userGroupModelImpl.getCompanyId(), userGroupModelImpl.getName()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByC_N, args, userGroupModelImpl);

			args = new Object[] {
				userGroupModelImpl.getExternalReferenceCode(),
				userGroupModelImpl.getCompanyId()
			};

			FinderCacheUtil.putResult(
				_finderPathFetchByERC_C, args, userGroupModelImpl);
		}
	}

	/**
	 * Creates a new user group with the primary key. Does not add the user group to the database.
	 *
	 * @param userGroupId the primary key for the new user group
	 * @return the new user group
	 */
	@Override
	public UserGroup create(long userGroupId) {
		UserGroup userGroup = new UserGroupImpl();

		userGroup.setNew(true);
		userGroup.setPrimaryKey(userGroupId);

		String uuid = PortalUUIDUtil.generate();

		userGroup.setUuid(uuid);

		userGroup.setCompanyId(CompanyThreadLocal.getCompanyId());

		return userGroup;
	}

	/**
	 * Removes the user group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group that was removed
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup remove(long userGroupId) throws NoSuchUserGroupException {
		return remove((Serializable)userGroupId);
	}

	/**
	 * Removes the user group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the user group
	 * @return the user group that was removed
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup remove(Serializable primaryKey)
		throws NoSuchUserGroupException {

		Session session = null;

		try {
			session = openSession();

			UserGroup userGroup = (UserGroup)session.get(
				UserGroupImpl.class, primaryKey);

			if (userGroup == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchUserGroupException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(userGroup);
		}
		catch (NoSuchUserGroupException noSuchEntityException) {
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
	protected UserGroup removeImpl(UserGroup userGroup) {
		userGroupToGroupTableMapper.deleteLeftPrimaryKeyTableMappings(
			userGroup.getPrimaryKey());

		userGroupToTeamTableMapper.deleteLeftPrimaryKeyTableMappings(
			userGroup.getPrimaryKey());

		userGroupToUserTableMapper.deleteLeftPrimaryKeyTableMappings(
			userGroup.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(userGroup)) {
				userGroup = (UserGroup)session.get(
					UserGroupImpl.class, userGroup.getPrimaryKeyObj());
			}

			if ((userGroup != null) &&
				CTPersistenceHelperUtil.isRemove(userGroup)) {

				session.delete(userGroup);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (userGroup != null) {
			clearCache(userGroup);
		}

		return userGroup;
	}

	@Override
	public UserGroup updateImpl(UserGroup userGroup) {
		boolean isNew = userGroup.isNew();

		if (!(userGroup instanceof UserGroupModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(userGroup.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(userGroup);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in userGroup proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom UserGroup implementation " +
					userGroup.getClass());
		}

		UserGroupModelImpl userGroupModelImpl = (UserGroupModelImpl)userGroup;

		if (Validator.isNull(userGroup.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			userGroup.setUuid(uuid);
		}

		if (Validator.isNull(userGroup.getExternalReferenceCode())) {
			userGroup.setExternalReferenceCode(userGroup.getUuid());
		}
		else {
			if (!Objects.equals(
					userGroupModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					userGroup.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = userGroup.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = userGroup.getPrimaryKey();
					}

					try {
						userGroup.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								UserGroup.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								userGroup.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			UserGroup ercUserGroup = fetchByERC_C(
				userGroup.getExternalReferenceCode(), userGroup.getCompanyId());

			if (isNew) {
				if (ercUserGroup != null) {
					throw new DuplicateUserGroupExternalReferenceCodeException(
						"Duplicate user group with external reference code " +
							userGroup.getExternalReferenceCode() +
								" and company " + userGroup.getCompanyId());
				}
			}
			else {
				if ((ercUserGroup != null) &&
					(userGroup.getUserGroupId() !=
						ercUserGroup.getUserGroupId())) {

					throw new DuplicateUserGroupExternalReferenceCodeException(
						"Duplicate user group with external reference code " +
							userGroup.getExternalReferenceCode() +
								" and company " + userGroup.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (userGroup.getCreateDate() == null)) {
			if (serviceContext == null) {
				userGroup.setCreateDate(date);
			}
			else {
				userGroup.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!userGroupModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				userGroup.setModifiedDate(date);
			}
			else {
				userGroup.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(userGroup)) {
				if (!isNew) {
					session.evict(
						UserGroupImpl.class, userGroup.getPrimaryKeyObj());
				}

				session.save(userGroup);
			}
			else {
				userGroup = (UserGroup)session.merge(userGroup);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		EntityCacheUtil.putResult(
			UserGroupImpl.class, userGroupModelImpl, false, true);

		cacheUniqueFindersCache(userGroupModelImpl);

		if (isNew) {
			userGroup.setNew(false);
		}

		userGroup.resetOriginalValues();

		return userGroup;
	}

	/**
	 * Returns the user group with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the user group
	 * @return the user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup findByPrimaryKey(Serializable primaryKey)
		throws NoSuchUserGroupException {

		UserGroup userGroup = fetchByPrimaryKey(primaryKey);

		if (userGroup == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchUserGroupException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return userGroup;
	}

	/**
	 * Returns the user group with the primary key or throws a <code>NoSuchUserGroupException</code> if it could not be found.
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup findByPrimaryKey(long userGroupId)
		throws NoSuchUserGroupException {

		return findByPrimaryKey((Serializable)userGroupId);
	}

	/**
	 * Returns the user group with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the user group
	 * @return the user group, or <code>null</code> if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup fetchByPrimaryKey(Serializable primaryKey) {
		if (CTPersistenceHelperUtil.isProductionMode(
				UserGroup.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		UserGroup userGroup = (UserGroup)EntityCacheUtil.getResult(
			UserGroupImpl.class, primaryKey);

		if (userGroup != null) {
			return userGroup;
		}

		Session session = null;

		try {
			session = openSession();

			userGroup = (UserGroup)session.get(UserGroupImpl.class, primaryKey);

			if (userGroup != null) {
				cacheResult(userGroup);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return userGroup;
	}

	/**
	 * Returns the user group with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group, or <code>null</code> if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup fetchByPrimaryKey(long userGroupId) {
		return fetchByPrimaryKey((Serializable)userGroupId);
	}

	@Override
	public Map<Serializable, UserGroup> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (CTPersistenceHelperUtil.isProductionMode(UserGroup.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, UserGroup> map =
			new HashMap<Serializable, UserGroup>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			UserGroup userGroup = fetchByPrimaryKey(primaryKey);

			if (userGroup != null) {
				map.put(primaryKey, userGroup);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
						UserGroup.class, primaryKey)) {

				UserGroup userGroup = (UserGroup)EntityCacheUtil.getResult(
					UserGroupImpl.class, primaryKey);

				if (userGroup == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, userGroup);
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

			for (UserGroup userGroup : (List<UserGroup>)query.list()) {
				map.put(userGroup.getPrimaryKeyObj(), userGroup);

				cacheResult(userGroup);
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
	 * Returns all the user groups.
	 *
	 * @return the user groups
	 */
	@Override
	public List<UserGroup> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of user groups
	 */
	@Override
	public List<UserGroup> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of user groups
	 */
	@Override
	public List<UserGroup> findAll(
		int start, int end, OrderByComparator<UserGroup> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the user groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of user groups
	 */
	@Override
	public List<UserGroup> findAll(
		int start, int end, OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

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

			List<UserGroup> list = null;

			if (useFinderCache) {
				list = (List<UserGroup>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_USERGROUP);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_USERGROUP;

					sql = sql.concat(UserGroupModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<UserGroup>)QueryUtil.list(
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
	 * Removes all the user groups from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (UserGroup userGroup : findAll()) {
			remove(userGroup);
		}
	}

	/**
	 * Returns the number of user groups.
	 *
	 * @return the number of user groups
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					UserGroup.class)) {

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_USERGROUP);

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

	/**
	 * Returns the primaryKeys of groups associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return long[] of the primaryKeys of groups associated with the user group
	 */
	@Override
	public long[] getGroupPrimaryKeys(long pk) {
		long[] pks = userGroupToGroupTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the groups associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return the groups associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Group> getGroups(long pk) {
		return getGroups(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the groups associated with the user group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the user group
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of groups associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Group> getGroups(
		long pk, int start, int end) {

		return getGroups(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups associated with the user group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the user group
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of groups associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Group> getGroups(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.Group>
			orderByComparator) {

		return userGroupToGroupTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of groups associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return the number of groups associated with the user group
	 */
	@Override
	public int getGroupsSize(long pk) {
		long[] pks = userGroupToGroupTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the group is associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @param groupPK the primary key of the group
	 * @return <code>true</code> if the group is associated with the user group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsGroup(long pk, long groupPK) {
		return userGroupToGroupTableMapper.containsTableMapping(pk, groupPK);
	}

	/**
	 * Returns <code>true</code> if the user group has any groups associated with it.
	 *
	 * @param pk the primary key of the user group to check for associations with groups
	 * @return <code>true</code> if the user group has any groups associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsGroups(long pk) {
		if (getGroupsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the user group and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groupPK the primary key of the group
	 * @return <code>true</code> if an association between the user group and the group was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addGroup(long pk, long groupPK) {
		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			return userGroupToGroupTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, groupPK);
		}
		else {
			return userGroupToGroupTableMapper.addTableMapping(
				userGroup.getCompanyId(), pk, groupPK);
		}
	}

	/**
	 * Adds an association between the user group and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param group the group
	 * @return <code>true</code> if an association between the user group and the group was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addGroup(
		long pk, com.liferay.portal.kernel.model.Group group) {

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			return userGroupToGroupTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, group.getPrimaryKey());
		}
		else {
			return userGroupToGroupTableMapper.addTableMapping(
				userGroup.getCompanyId(), pk, group.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the user group and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groupPKs the primary keys of the groups
	 * @return <code>true</code> if at least one association between the user group and the groups was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addGroups(long pk, long[] groupPKs) {
		long companyId = 0;

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = userGroup.getCompanyId();
		}

		long[] addedKeys = userGroupToGroupTableMapper.addTableMappings(
			companyId, pk, groupPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the user group and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groups the groups
	 * @return <code>true</code> if at least one association between the user group and the groups was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addGroups(
		long pk, List<com.liferay.portal.kernel.model.Group> groups) {

		return addGroups(
			pk,
			ListUtil.toLongArray(
				groups,
				com.liferay.portal.kernel.model.Group.GROUP_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the user group and its groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group to clear the associated groups from
	 */
	@Override
	public void clearGroups(long pk) {
		userGroupToGroupTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the user group and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groupPK the primary key of the group
	 */
	@Override
	public void removeGroup(long pk, long groupPK) {
		userGroupToGroupTableMapper.deleteTableMapping(pk, groupPK);
	}

	/**
	 * Removes the association between the user group and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param group the group
	 */
	@Override
	public void removeGroup(
		long pk, com.liferay.portal.kernel.model.Group group) {

		userGroupToGroupTableMapper.deleteTableMapping(
			pk, group.getPrimaryKey());
	}

	/**
	 * Removes the association between the user group and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groupPKs the primary keys of the groups
	 */
	@Override
	public void removeGroups(long pk, long[] groupPKs) {
		userGroupToGroupTableMapper.deleteTableMappings(pk, groupPKs);
	}

	/**
	 * Removes the association between the user group and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groups the groups
	 */
	@Override
	public void removeGroups(
		long pk, List<com.liferay.portal.kernel.model.Group> groups) {

		removeGroups(
			pk,
			ListUtil.toLongArray(
				groups,
				com.liferay.portal.kernel.model.Group.GROUP_ID_ACCESSOR));
	}

	/**
	 * Sets the groups associated with the user group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groupPKs the primary keys of the groups to be associated with the user group
	 */
	@Override
	public void setGroups(long pk, long[] groupPKs) {
		Set<Long> newGroupPKsSet = SetUtil.fromArray(groupPKs);
		Set<Long> oldGroupPKsSet = SetUtil.fromArray(
			userGroupToGroupTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeGroupPKsSet = new HashSet<Long>(oldGroupPKsSet);

		removeGroupPKsSet.removeAll(newGroupPKsSet);

		userGroupToGroupTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeGroupPKsSet));

		newGroupPKsSet.removeAll(oldGroupPKsSet);

		long companyId = 0;

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = userGroup.getCompanyId();
		}

		userGroupToGroupTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newGroupPKsSet));
	}

	/**
	 * Sets the groups associated with the user group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groups the groups to be associated with the user group
	 */
	@Override
	public void setGroups(
		long pk, List<com.liferay.portal.kernel.model.Group> groups) {

		try {
			long[] groupPKs = new long[groups.size()];

			for (int i = 0; i < groups.size(); i++) {
				com.liferay.portal.kernel.model.Group group = groups.get(i);

				groupPKs[i] = group.getPrimaryKey();
			}

			setGroups(pk, groupPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of teams associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return long[] of the primaryKeys of teams associated with the user group
	 */
	@Override
	public long[] getTeamPrimaryKeys(long pk) {
		long[] pks = userGroupToTeamTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the teams associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return the teams associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Team> getTeams(long pk) {
		return getTeams(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the teams associated with the user group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the user group
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of teams associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Team> getTeams(
		long pk, int start, int end) {

		return getTeams(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the teams associated with the user group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the user group
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of teams associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Team> getTeams(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.Team>
			orderByComparator) {

		return userGroupToTeamTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of teams associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return the number of teams associated with the user group
	 */
	@Override
	public int getTeamsSize(long pk) {
		long[] pks = userGroupToTeamTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the team is associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @param teamPK the primary key of the team
	 * @return <code>true</code> if the team is associated with the user group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsTeam(long pk, long teamPK) {
		return userGroupToTeamTableMapper.containsTableMapping(pk, teamPK);
	}

	/**
	 * Returns <code>true</code> if the user group has any teams associated with it.
	 *
	 * @param pk the primary key of the user group to check for associations with teams
	 * @return <code>true</code> if the user group has any teams associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsTeams(long pk) {
		if (getTeamsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the user group and the team. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teamPK the primary key of the team
	 * @return <code>true</code> if an association between the user group and the team was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addTeam(long pk, long teamPK) {
		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			return userGroupToTeamTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, teamPK);
		}
		else {
			return userGroupToTeamTableMapper.addTableMapping(
				userGroup.getCompanyId(), pk, teamPK);
		}
	}

	/**
	 * Adds an association between the user group and the team. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param team the team
	 * @return <code>true</code> if an association between the user group and the team was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addTeam(long pk, com.liferay.portal.kernel.model.Team team) {
		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			return userGroupToTeamTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, team.getPrimaryKey());
		}
		else {
			return userGroupToTeamTableMapper.addTableMapping(
				userGroup.getCompanyId(), pk, team.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the user group and the teams. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teamPKs the primary keys of the teams
	 * @return <code>true</code> if at least one association between the user group and the teams was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addTeams(long pk, long[] teamPKs) {
		long companyId = 0;

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = userGroup.getCompanyId();
		}

		long[] addedKeys = userGroupToTeamTableMapper.addTableMappings(
			companyId, pk, teamPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the user group and the teams. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teams the teams
	 * @return <code>true</code> if at least one association between the user group and the teams was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addTeams(
		long pk, List<com.liferay.portal.kernel.model.Team> teams) {

		return addTeams(
			pk,
			ListUtil.toLongArray(
				teams, com.liferay.portal.kernel.model.Team.TEAM_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the user group and its teams. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group to clear the associated teams from
	 */
	@Override
	public void clearTeams(long pk) {
		userGroupToTeamTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the user group and the team. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teamPK the primary key of the team
	 */
	@Override
	public void removeTeam(long pk, long teamPK) {
		userGroupToTeamTableMapper.deleteTableMapping(pk, teamPK);
	}

	/**
	 * Removes the association between the user group and the team. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param team the team
	 */
	@Override
	public void removeTeam(long pk, com.liferay.portal.kernel.model.Team team) {
		userGroupToTeamTableMapper.deleteTableMapping(pk, team.getPrimaryKey());
	}

	/**
	 * Removes the association between the user group and the teams. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teamPKs the primary keys of the teams
	 */
	@Override
	public void removeTeams(long pk, long[] teamPKs) {
		userGroupToTeamTableMapper.deleteTableMappings(pk, teamPKs);
	}

	/**
	 * Removes the association between the user group and the teams. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teams the teams
	 */
	@Override
	public void removeTeams(
		long pk, List<com.liferay.portal.kernel.model.Team> teams) {

		removeTeams(
			pk,
			ListUtil.toLongArray(
				teams, com.liferay.portal.kernel.model.Team.TEAM_ID_ACCESSOR));
	}

	/**
	 * Sets the teams associated with the user group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teamPKs the primary keys of the teams to be associated with the user group
	 */
	@Override
	public void setTeams(long pk, long[] teamPKs) {
		Set<Long> newTeamPKsSet = SetUtil.fromArray(teamPKs);
		Set<Long> oldTeamPKsSet = SetUtil.fromArray(
			userGroupToTeamTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeTeamPKsSet = new HashSet<Long>(oldTeamPKsSet);

		removeTeamPKsSet.removeAll(newTeamPKsSet);

		userGroupToTeamTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeTeamPKsSet));

		newTeamPKsSet.removeAll(oldTeamPKsSet);

		long companyId = 0;

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = userGroup.getCompanyId();
		}

		userGroupToTeamTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newTeamPKsSet));
	}

	/**
	 * Sets the teams associated with the user group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teams the teams to be associated with the user group
	 */
	@Override
	public void setTeams(
		long pk, List<com.liferay.portal.kernel.model.Team> teams) {

		try {
			long[] teamPKs = new long[teams.size()];

			for (int i = 0; i < teams.size(); i++) {
				com.liferay.portal.kernel.model.Team team = teams.get(i);

				teamPKs[i] = team.getPrimaryKey();
			}

			setTeams(pk, teamPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of users associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return long[] of the primaryKeys of users associated with the user group
	 */
	@Override
	public long[] getUserPrimaryKeys(long pk) {
		long[] pks = userGroupToUserTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the users associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return the users associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(long pk) {
		return getUsers(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the users associated with the user group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the user group
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of users associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end) {

		return getUsers(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the users associated with the user group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the user group
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of users associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.User>
			orderByComparator) {

		return userGroupToUserTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of users associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return the number of users associated with the user group
	 */
	@Override
	public int getUsersSize(long pk) {
		long[] pks = userGroupToUserTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the user is associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if the user is associated with the user group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsUser(long pk, long userPK) {
		return userGroupToUserTableMapper.containsTableMapping(pk, userPK);
	}

	/**
	 * Returns <code>true</code> if the user group has any users associated with it.
	 *
	 * @param pk the primary key of the user group to check for associations with users
	 * @return <code>true</code> if the user group has any users associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsUsers(long pk) {
		if (getUsersSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the user group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if an association between the user group and the user was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUser(long pk, long userPK) {
		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			return userGroupToUserTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, userPK);
		}
		else {
			return userGroupToUserTableMapper.addTableMapping(
				userGroup.getCompanyId(), pk, userPK);
		}
	}

	/**
	 * Adds an association between the user group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param user the user
	 * @return <code>true</code> if an association between the user group and the user was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUser(long pk, com.liferay.portal.kernel.model.User user) {
		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			return userGroupToUserTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, user.getPrimaryKey());
		}
		else {
			return userGroupToUserTableMapper.addTableMapping(
				userGroup.getCompanyId(), pk, user.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the user group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param userPKs the primary keys of the users
	 * @return <code>true</code> if at least one association between the user group and the users was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addUsers(long pk, long[] userPKs) {
		long companyId = 0;

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = userGroup.getCompanyId();
		}

		long[] addedKeys = userGroupToUserTableMapper.addTableMappings(
			companyId, pk, userPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the user group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param users the users
	 * @return <code>true</code> if at least one association between the user group and the users was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addUsers(
		long pk, List<com.liferay.portal.kernel.model.User> users) {

		return addUsers(
			pk,
			ListUtil.toLongArray(
				users, com.liferay.portal.kernel.model.User.USER_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the user group and its users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group to clear the associated users from
	 */
	@Override
	public void clearUsers(long pk) {
		userGroupToUserTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the user group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param userPK the primary key of the user
	 */
	@Override
	public void removeUser(long pk, long userPK) {
		userGroupToUserTableMapper.deleteTableMapping(pk, userPK);
	}

	/**
	 * Removes the association between the user group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param user the user
	 */
	@Override
	public void removeUser(long pk, com.liferay.portal.kernel.model.User user) {
		userGroupToUserTableMapper.deleteTableMapping(pk, user.getPrimaryKey());
	}

	/**
	 * Removes the association between the user group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param userPKs the primary keys of the users
	 */
	@Override
	public void removeUsers(long pk, long[] userPKs) {
		userGroupToUserTableMapper.deleteTableMappings(pk, userPKs);
	}

	/**
	 * Removes the association between the user group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param users the users
	 */
	@Override
	public void removeUsers(
		long pk, List<com.liferay.portal.kernel.model.User> users) {

		removeUsers(
			pk,
			ListUtil.toLongArray(
				users, com.liferay.portal.kernel.model.User.USER_ID_ACCESSOR));
	}

	/**
	 * Sets the users associated with the user group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param userPKs the primary keys of the users to be associated with the user group
	 */
	@Override
	public void setUsers(long pk, long[] userPKs) {
		Set<Long> newUserPKsSet = SetUtil.fromArray(userPKs);
		Set<Long> oldUserPKsSet = SetUtil.fromArray(
			userGroupToUserTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeUserPKsSet = new HashSet<Long>(oldUserPKsSet);

		removeUserPKsSet.removeAll(newUserPKsSet);

		userGroupToUserTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeUserPKsSet));

		newUserPKsSet.removeAll(oldUserPKsSet);

		long companyId = 0;

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = userGroup.getCompanyId();
		}

		userGroupToUserTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newUserPKsSet));
	}

	/**
	 * Sets the users associated with the user group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param users the users to be associated with the user group
	 */
	@Override
	public void setUsers(
		long pk, List<com.liferay.portal.kernel.model.User> users) {

		try {
			long[] userPKs = new long[users.size()];

			for (int i = 0; i < users.size(); i++) {
				com.liferay.portal.kernel.model.User user = users.get(i);

				userPKs[i] = user.getPrimaryKey();
			}

			setUsers(pk, userPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
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
		return "userGroupId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_USERGROUP;
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
		return UserGroupModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "UserGroup";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("parentUserGroupId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("addedByLDAPImport");
		ctMergeColumnNames.add("groups_");
		ctMergeColumnNames.add("teams");
		ctMergeColumnNames.add("users");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("userGroupId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_mappingTableNames.add("Groups_UserGroups");
		_mappingTableNames.add("UserGroups_Teams");
		_mappingTableNames.add("Users_UserGroups");

		_uniqueIndexColumnNames.add(new String[] {"companyId", "name"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the user group persistence.
	 */
	public void afterPropertiesSet() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		userGroupToGroupTableMapper = TableMapperFactory.getTableMapper(
			"Groups_UserGroups", "companyId", "userGroupId", "groupId", this,
			groupPersistence);

		userGroupToTeamTableMapper = TableMapperFactory.getTableMapper(
			"UserGroups_Teams", "companyId", "userGroupId", "teamId", this,
			teamPersistence);

		userGroupToUserTableMapper = TableMapperFactory.getTableMapper(
			"Users_UserGroups", "companyId", "userGroupId", "userId", this,
			userPersistence);

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

		_finderPathWithPaginationFindByC_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "parentUserGroupId"}, true);

		_finderPathWithoutPaginationFindByC_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "parentUserGroupId"}, true);

		_finderPathCountByC_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P",
			new String[] {Long.class.getName(), Long.class.getName()},
			new String[] {"companyId", "parentUserGroupId"}, false);

		_finderPathFetchByC_N = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "name"}, true);

		_finderPathWithPaginationFindByC_LikeN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeN",
			new String[] {
				Long.class.getName(), String.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "name"}, true);

		_finderPathWithPaginationCountByC_LikeN = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeN",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "name"}, false);

		_finderPathWithPaginationFindByGtU_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGtU_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userGroupId", "companyId", "parentUserGroupId"},
			true);

		_finderPathWithPaginationCountByGtU_C_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGtU_C_P",
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"userGroupId", "companyId", "parentUserGroupId"},
			false);

		_finderPathFetchByERC_C = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, true);

		UserGroupUtil.setPersistence(this);
	}

	public void destroy() {
		UserGroupUtil.setPersistence(null);

		EntityCacheUtil.removeCache(UserGroupImpl.class.getName());

		TableMapperFactory.removeTableMapper("Groups_UserGroups");
		TableMapperFactory.removeTableMapper("UserGroups_Teams");
		TableMapperFactory.removeTableMapper("Users_UserGroups");
	}

	@BeanReference(type = GroupPersistence.class)
	protected GroupPersistence groupPersistence;

	protected TableMapper<UserGroup, com.liferay.portal.kernel.model.Group>
		userGroupToGroupTableMapper;

	@BeanReference(type = TeamPersistence.class)
	protected TeamPersistence teamPersistence;

	protected TableMapper<UserGroup, com.liferay.portal.kernel.model.Team>
		userGroupToTeamTableMapper;

	@BeanReference(type = UserPersistence.class)
	protected UserPersistence userPersistence;

	protected TableMapper<UserGroup, com.liferay.portal.kernel.model.User>
		userGroupToUserTableMapper;

	private static final String _SQL_SELECT_USERGROUP =
		"SELECT userGroup FROM UserGroup userGroup";

	private static final String _SQL_SELECT_USERGROUP_WHERE =
		"SELECT userGroup FROM UserGroup userGroup WHERE ";

	private static final String _SQL_COUNT_USERGROUP =
		"SELECT COUNT(userGroup) FROM UserGroup userGroup";

	private static final String _SQL_COUNT_USERGROUP_WHERE =
		"SELECT COUNT(userGroup) FROM UserGroup userGroup WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"userGroup.userGroupId";

	private static final String _FILTER_SQL_SELECT_USERGROUP_WHERE =
		"SELECT DISTINCT {userGroup.*} FROM UserGroup userGroup WHERE ";

	private static final String
		_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {UserGroup.*} FROM (SELECT DISTINCT userGroup.userGroupId FROM UserGroup userGroup WHERE ";

	private static final String
		_FILTER_SQL_SELECT_USERGROUP_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN UserGroup ON TEMP_TABLE.userGroupId = UserGroup.userGroupId";

	private static final String _FILTER_SQL_COUNT_USERGROUP_WHERE =
		"SELECT COUNT(DISTINCT userGroup.userGroupId) AS COUNT_VALUE FROM UserGroup userGroup WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "userGroup";

	private static final String _FILTER_ENTITY_TABLE = "UserGroup";

	private static final String _ORDER_BY_ENTITY_ALIAS = "userGroup.";

	private static final String _ORDER_BY_ENTITY_TABLE = "UserGroup.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No UserGroup exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No UserGroup exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "groups"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}